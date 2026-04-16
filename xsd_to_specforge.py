#!/usr/bin/env python3
"""
XSD-to-SpecForge Markdown Converter
====================================
Parses all .xsd files in a directory, resolves cross-references (xs:include/xs:import),
and generates a single consolidated SpecForge-format .md specification file with:
  - Schema documentation
  - Sample XML payloads
  - AI directives

Usage:
    python xsd_to_specforge.py /path/to/xsd/directory /path/to/output.md

Requirements:
    Python 3.8+ (stdlib only, no pip installs needed)
"""

import os
import sys
import xml.etree.ElementTree as ET
from collections import OrderedDict
from datetime import datetime
from pathlib import Path
from typing import Dict, List, Optional, Tuple, Set

# ─── XSD Namespace ───────────────────────────────────────────────────────────
XS = "http://www.w3.org/2001/XMLSchema"
NS = {"xs": XS}


# ─── Data Classes ────────────────────────────────────────────────────────────
class XSDAttribute:
    def __init__(self, name: str, type_str: str, use: str = "optional"):
        self.name = name
        self.type_str = type_str
        self.use = use

    def type_display(self) -> str:
        return self.type_str.split(":")[-1] if ":" in self.type_str else self.type_str

    def sample_value(self, enums: Dict[str, List[str]]) -> str:
        """Generate a plausible sample value."""
        t = self.type_display()
        if t in enums and enums[t]:
            return enums[t][0]
        simple_map = {
            "string": f"sample-{self.name}",
            "boolean": "true",
            "int": "1",
            "integer": "1",
            "long": "100",
            "decimal": "100.00",
            "double": "100.00",
            "float": "1.0",
            "date": "2025-01-15",
            "dateTime": "2025-01-15T10:30:00+05:30",
            "time": "10:30:00",
        }
        if t in simple_map:
            return simple_map[t]
        name_lower = self.name.lower()
        if "ts" == name_lower:
            return "2025-01-15T10:30:00+05:30"
        if "id" in name_lower:
            return f"REF{self.name.upper()}001"
        if "amount" in name_lower or "amt" in name_lower:
            return "1500.00"
        if "date" in name_lower:
            return "2025-01-15"
        return f"sample-{self.name}"


class XSDElement:
    def __init__(self, name: str, type_str: str, min_occurs: str = "1", max_occurs: str = "1",
                 doc: str = "", inline_complex: Optional['XSDComplexType'] = None):
        self.name = name
        self.type_str = type_str
        self.min_occurs = min_occurs
        self.max_occurs = max_occurs
        self.doc = doc
        self.inline_complex = inline_complex

    def is_required(self) -> bool:
        return self.min_occurs not in ("0",)

    def type_display(self) -> str:
        return self.type_str.split(":")[-1] if ":" in self.type_str else self.type_str


class XSDComplexType:
    def __init__(self, name: str):
        self.name = name
        self.attributes: List[XSDAttribute] = []
        self.elements: List[XSDElement] = []
        self.has_simple_content = False
        self.simple_content_base = ""
        self.doc = ""


class XSDSimpleType:
    def __init__(self, name: str, base: str, enumerations: List[str]):
        self.name = name
        self.base = base
        self.enumerations = enumerations


class XSDFile:
    def __init__(self, filename: str, target_ns: str):
        self.filename = filename
        self.target_ns = target_ns
        self.includes: List[str] = []
        self.imports: List[Tuple[str, str]] = []
        self.root_elements: List[XSDElement] = []
        self.complex_types: OrderedDict[str, XSDComplexType] = OrderedDict()
        self.simple_types: OrderedDict[str, XSDSimpleType] = OrderedDict()
        self.doc = ""


# ─── Parser ──────────────────────────────────────────────────────────────────
class XSDParser:
    def __init__(self, xsd_dir: str):
        self.xsd_dir = Path(xsd_dir)
        self.files: OrderedDict[str, XSDFile] = OrderedDict()
        self.all_complex_types: Dict[str, XSDComplexType] = {}
        self.all_simple_types: Dict[str, XSDSimpleType] = {}
        self.enums: Dict[str, List[str]] = {}

    def parse_all(self):
        """Parse all XSD files in the directory."""
        xsd_paths = sorted(self.xsd_dir.glob("*.xsd"))
        if not xsd_paths:
            print(f"No .xsd files found in {self.xsd_dir}")
            sys.exit(1)

        # Parse common/shared files first so their types are available
        priority = ["BBPS-Common.xsd", "UPMSCommon.xsd"]
        ordered_paths = []
        for p in priority:
            full = self.xsd_dir / p
            if full.exists():
                ordered_paths.append(full)
        for p in xsd_paths:
            if p.name not in priority:
                ordered_paths.append(p)

        for path in ordered_paths:
            self._parse_file(path)

        # Build global lookups
        for f in self.files.values():
            for name, ct in f.complex_types.items():
                self.all_complex_types[name] = ct
            for name, st in f.simple_types.items():
                self.all_simple_types[name] = st
                if st.enumerations:
                    self.enums[name] = st.enumerations

    def _parse_file(self, path: Path):
        try:
            tree = ET.parse(path)
        except ET.ParseError as e:
            print(f"  [WARN] Could not parse {path.name}: {e}")
            return

        root = tree.getroot()
        target_ns = root.get("targetNamespace", "")
        xsd_file = XSDFile(path.name, target_ns)

        # Includes / Imports
        for inc in root.findall("xs:include", NS):
            loc = inc.get("schemaLocation", "")
            if loc:
                xsd_file.includes.append(loc)
        for imp in root.findall("xs:import", NS):
            ns_val = imp.get("namespace", "")
            loc = imp.get("schemaLocation", "")
            xsd_file.imports.append((ns_val, loc))

        # Top-level elements
        for elem in root.findall("xs:element", NS):
            xsd_file.root_elements.append(self._parse_element(elem))

        # ComplexTypes
        for ct_elem in root.findall("xs:complexType", NS):
            ct = self._parse_complex_type(ct_elem)
            if ct and ct.name:
                xsd_file.complex_types[ct.name] = ct

        # SimpleTypes
        for st_elem in root.findall("xs:simpleType", NS):
            st = self._parse_simple_type(st_elem)
            if st and st.name:
                xsd_file.simple_types[st.name] = st

        self.files[path.name] = xsd_file

    def _parse_element(self, elem) -> XSDElement:
        name = elem.get("name", "")
        type_str = elem.get("type", "xs:string")
        min_occ = elem.get("minOccurs", "1")
        max_occ = elem.get("maxOccurs", "1")
        doc = ""
        doc_elem = elem.find(".//xs:documentation", NS)
        if doc_elem is not None and doc_elem.text:
            doc = doc_elem.text.strip()

        inline_ct = None
        inner_ct = elem.find("xs:complexType", NS)
        if inner_ct is not None:
            inline_ct = self._parse_complex_type(inner_ct, anonymous_name=f"{name}_inline")

        return XSDElement(name, type_str, min_occ, max_occ, doc, inline_ct)

    def _parse_complex_type(self, ct_elem, anonymous_name: str = "") -> Optional[XSDComplexType]:
        name = ct_elem.get("name", anonymous_name)
        if not name:
            return None
        ct = XSDComplexType(name)

        doc_elem = ct_elem.find(".//xs:documentation", NS)
        if doc_elem is not None and doc_elem.text:
            ct.doc = doc_elem.text.strip()

        # Direct attributes
        for attr in ct_elem.findall("xs:attribute", NS):
            ct.attributes.append(self._parse_attribute(attr))

        # Sequence elements
        seq = ct_elem.find("xs:sequence", NS)
        if seq is not None:
            self._collect_elements(seq, ct)

        # SimpleContent
        sc = ct_elem.find("xs:simpleContent", NS)
        if sc is not None:
            ct.has_simple_content = True
            ext = sc.find("xs:extension", NS)
            if ext is not None:
                ct.simple_content_base = ext.get("base", "xs:string")
                for attr in ext.findall("xs:attribute", NS):
                    ct.attributes.append(self._parse_attribute(attr))

        return ct

    def _collect_elements(self, parent, ct: XSDComplexType):
        for child in parent:
            tag = child.tag.replace(f"{{{XS}}}", "xs:")
            if tag == "xs:element":
                ct.elements.append(self._parse_element(child))
            elif tag == "xs:sequence":
                self._collect_elements(child, ct)

    def _parse_attribute(self, attr_elem) -> XSDAttribute:
        name = attr_elem.get("name", "")
        type_str = attr_elem.get("type", "xs:string")
        use = attr_elem.get("use", "optional")
        return XSDAttribute(name, type_str, use)

    def _parse_simple_type(self, st_elem) -> Optional[XSDSimpleType]:
        name = st_elem.get("name", "")
        if not name:
            return None
        restriction = st_elem.find("xs:restriction", NS)
        base = ""
        enums = []
        if restriction is not None:
            base = restriction.get("base", "xs:string")
            for enum in restriction.findall("xs:enumeration", NS):
                val = enum.get("value", "")
                if val:
                    enums.append(val)
        return XSDSimpleType(name, base, enums)


# ─── Sample XML Generator ───────────────────────────────────────────────────
class SampleXMLGenerator:
    def __init__(self, parser: XSDParser):
        self.parser = parser
        self.indent_str = "  "
        self._depth = 0
        self._visited: Set[str] = set()

    def generate_for_root(self, root_elem: XSDElement, target_ns: str) -> str:
        self._depth = 0
        self._visited = set()
        lines = ['<?xml version="1.0" encoding="UTF-8"?>']
        type_name = root_elem.type_display()
        ns_prefix = "bbps"
        if "upms" in target_ns.lower():
            ns_prefix = "upms"

        # Get attributes for the root element's type
        ct = self.parser.all_complex_types.get(type_name)
        root_attrs = ""
        if ct:
            root_attrs = self._attr_string(ct.attributes)

        lines.append(f'<{ns_prefix}:{root_elem.name} xmlns:{ns_prefix}="{target_ns}"{root_attrs}>')
        self._depth = 1
        if ct:
            lines.extend(self._render_children(ct))
        lines.append(f'</{ns_prefix}:{root_elem.name}>')
        return "\n".join(lines)

    def _render_children(self, ct: XSDComplexType) -> List[str]:
        lines = []
        for elem in ct.elements:
            lines.extend(self._render_element(elem))
        return lines

    def _render_element(self, elem: XSDElement) -> List[str]:
        lines = []
        indent = self.indent_str * self._depth
        type_name = elem.type_display()

        # Inline anonymous complexType
        if elem.inline_complex:
            attr_str = self._attr_string(elem.inline_complex.attributes)
            if elem.inline_complex.elements:
                lines.append(f"{indent}<{elem.name}{attr_str}>")
                self._depth += 1
                for child in elem.inline_complex.elements:
                    lines.extend(self._render_element(child))
                self._depth -= 1
                lines.append(f"{indent}</{elem.name}>")
            else:
                lines.append(f"{indent}<{elem.name}{attr_str}/>")
            return lines

        # Known complex type
        ct = self.parser.all_complex_types.get(type_name)
        if ct and type_name not in self._visited:
            self._visited.add(type_name)
            attr_str = self._attr_string(ct.attributes)
            if ct.has_simple_content:
                lines.append(f"{indent}<{elem.name}{attr_str}>sample-text</{elem.name}>")
            elif ct.elements:
                lines.append(f"{indent}<{elem.name}{attr_str}>")
                self._depth += 1
                for child in ct.elements:
                    lines.extend(self._render_element(child))
                self._depth -= 1
                lines.append(f"{indent}</{elem.name}>")
            else:
                lines.append(f"{indent}<{elem.name}{attr_str}/>")
            self._visited.discard(type_name)
        elif ct and type_name in self._visited:
            lines.append(f"{indent}<!-- {elem.name} (recursive ref to {type_name}, omitted) -->")
        else:
            # Primitive type
            sample = self._sample_simple_value(type_name, elem.name)
            lines.append(f"{indent}<{elem.name}>{sample}</{elem.name}>")

        return lines

    def _attr_string(self, attributes: List[XSDAttribute]) -> str:
        if not attributes:
            return ""
        parts = []
        for attr in attributes:
            val = attr.sample_value(self.parser.enums)
            parts.append(f'{attr.name}="{val}"')
        return " " + " ".join(parts)

    def _sample_simple_value(self, type_name: str, elem_name: str) -> str:
        if type_name in self.parser.enums and self.parser.enums[type_name]:
            return self.parser.enums[type_name][0]
        name_l = elem_name.lower()
        if "mobile" in name_l:
            return "9876543210"
        if "email" in name_l:
            return "customer@example.com"
        if "amount" in name_l or "amt" in name_l:
            return "1500.00"
        if "date" in name_l or name_l == "ts":
            return "2025-01-15T10:30:00+05:30"
        if "id" in name_l:
            return "SAMPLE-ID-001"
        if "name" in name_l:
            return "Sample Name"
        if "code" in name_l or "cd" in name_l:
            return "000"
        return "sample-value"


# ─── Markdown Generator ─────────────────────────────────────────────────────
class SpecForgeMarkdownGenerator:
    def __init__(self, parser: XSDParser):
        self.parser = parser
        self.xml_gen = SampleXMLGenerator(parser)

    def generate(self) -> str:
        sections = []
        sections.append(self._header())
        sections.append(self._overview())
        sections.append(self._shared_types_section())
        sections.append(self._enumerations_section())
        sections.append(self._api_sections())
        sections.append(self._ai_footer())
        return "\n\n".join(sections)

    def _header(self) -> str:
        file_count = len(self.parser.files)
        ct_count = len(self.parser.all_complex_types)
        st_count = len(self.parser.all_simple_types)
        today = datetime.now().strftime("%B %d, %Y")
        return f"""# BBPS Biller Integrator – Complete XSD Schema Specification

**Version:** Auto-generated
**Date:** {today}
**Description:** Complete schema specification for the BBPS Biller Integrator system, auto-generated from {file_count} XSD files. Covers bill fetch, bill payment, transaction status, UPMS presentment, notifications, diagnostics, biller management, and all supporting message types.
**Audience:** COU/BOU integration teams, backend developers, QA engineers, MCP AI agents.
**Protocol:** XML over HTTPS. All messages conform to the `http://bbps.org/schema` or `http://upms.org/schema` namespaces.
**Source Files:** {file_count} XSD files ({ct_count} complex types, {st_count} simple/enumeration types)

*Note for AI: This document is the single authoritative schema reference for the BBPS Biller Integrator. Use it to validate XML payloads, generate request/response templates, and answer integration questions.*

---"""

    def _overview(self) -> str:
        lines = ["## 1. System Overview", ""]
        lines.append("The BBPS (Bharat Bill Payment System) Biller Integrator defines XML message schemas for communication between Customer Operating Units (COUs), the BBPS Central Unit (CU), and Biller Operating Units (BOUs). The schemas are organized as follows:")
        lines.append("")

        categories = {
            "Core Transaction": [],
            "Bill Fetch & Payment": [],
            "UPMS / Presentment": [],
            "Biller Management": [],
            "Notifications & Alerts": [],
            "Diagnostics & Status": [],
            "Bulk Operations": [],
            "Shared / Common": [],
            "Other": [],
        }

        for fname in self.parser.files:
            fl = fname.lower()
            if "common" in fl:
                categories["Shared / Common"].append(fname)
            elif "bulk" in fl:
                categories["Bulk Operations"].append(fname)
            elif "billfetch" in fl or "billpayment" in fl or "billvalidation" in fl:
                categories["Bill Fetch & Payment"].append(fname)
            elif "notification" in fl or "alert" in fl:
                categories["Notifications & Alerts"].append(fname)
            elif "biller" in fl and ("fetch" in fl or "status" in fl or "activation" in fl):
                categories["Biller Management"].append(fname)
            elif "diagnostic" in fl or "switchover" in fl:
                categories["Diagnostics & Status"].append(fname)
            elif "upms" in fl or "presentment" in fl:
                categories["UPMS / Presentment"].append(fname)
            elif "402" in fl or "txnstatus" in fl or "ack" in fl or "ticket" in fl:
                categories["Core Transaction"].append(fname)
            elif "plan" in fl or "mdm" in fl:
                categories["Biller Management"].append(fname)
            elif "efrm" in fl or "billref" in fl or "generate" in fl:
                categories["Other"].append(fname)
            elif "agent" in fl:
                categories["Biller Management"].append(fname)
            else:
                categories["Other"].append(fname)

        for cat, files in categories.items():
            if files:
                lines.append(f"**{cat}:** {', '.join(f'`{f}`' for f in sorted(files))}")
                lines.append("")

        return "\n".join(lines)

    def _shared_types_section(self) -> str:
        lines = ["## 2. Shared Complex Types (BBPS-Common & UPMSCommon)", ""]
        lines.append("*Note for AI: These shared types are referenced by almost every message in the system. Understand these first before working with any specific API.*")
        lines.append("")

        common_files = ["BBPS-Common.xsd", "UPMSCommon.xsd"]
        sub_num = 1
        for cf in common_files:
            if cf not in self.parser.files:
                continue
            xsd = self.parser.files[cf]
            lines.append(f"### 2.{sub_num}. Types from `{cf}`")
            lines.append(f"**Namespace:** `{xsd.target_ns}`")
            lines.append("")

            for ct_name, ct in xsd.complex_types.items():
                lines.extend(self._render_complex_type_doc(ct_name, ct))

            sub_num += 1

        return "\n".join(lines)

    def _enumerations_section(self) -> str:
        lines = ["## 3. Enumeration Types", ""]
        lines.append("*Note for AI: These enumerations define the allowed values for key fields. Validate all incoming/outgoing values against these lists.*")
        lines.append("")

        all_enums = []
        for f in self.parser.files.values():
            for st_name, st in f.simple_types.items():
                if st.enumerations:
                    all_enums.append((st_name, st))

        if not all_enums:
            lines.append("No enumeration types found.")
            return "\n".join(lines)

        for st_name, st in sorted(all_enums, key=lambda x: x[0]):
            lines.append(f"#### `{st_name}`")
            lines.append(f"**Base type:** `{st.base}`")
            lines.append("")
            lines.append("| Allowed Value |")
            lines.append("|---|")
            for val in st.enumerations:
                lines.append(f"| `{val}` |")
            lines.append("")

        return "\n".join(lines)

    def _api_sections(self) -> str:
        lines = []
        section_num = 4
        common_files = {"BBPS-Common.xsd", "UPMSCommon.xsd"}

        # Collect common type names for deduplication
        common_type_names: Set[str] = set()
        for cf in common_files:
            if cf in self.parser.files:
                common_type_names.update(self.parser.files[cf].complex_types.keys())

        for fname, xsd in self.parser.files.items():
            if fname in common_files:
                continue
            if not xsd.root_elements and not xsd.complex_types:
                continue

            lines.append(f"## {section_num}. `{fname}`")
            lines.append(f"**Namespace:** `{xsd.target_ns}`")
            if xsd.includes:
                lines.append(f"**Includes:** {', '.join(f'`{i}`' for i in xsd.includes)}")
            if xsd.imports:
                imp_strs = [f'`{loc}` (ns: `{ns}`)' for ns, loc in xsd.imports if loc]
                if imp_strs:
                    lines.append(f"**Imports:** {', '.join(imp_strs)}")
            lines.append("")

            # Root elements
            if xsd.root_elements:
                lines.append(f"### {section_num}.1. Root Elements")
                lines.append("")
                for root_elem in xsd.root_elements:
                    doc_str = f" — {root_elem.doc}" if root_elem.doc else ""
                    lines.append(f"**`<{root_elem.name}>`** (type: `{root_elem.type_display()}`){doc_str}")
                    lines.append("")

                    # Element structure table
                    type_name = root_elem.type_display()
                    ct = self.parser.all_complex_types.get(type_name)
                    if ct:
                        lines.extend(self._render_complex_type_doc(type_name, ct, heading_level="####"))

                    # Sample XML
                    lines.append(f"**Sample XML Payload:**")
                    lines.append("")
                    lines.append("*Note for AI: Use this as a template when generating or validating XML for this message type.*")
                    lines.append("")
                    sample = self.xml_gen.generate_for_root(root_elem, xsd.target_ns)
                    lines.append("```xml")
                    lines.append(sample)
                    lines.append("```")
                    lines.append("")

            # File-local complex types not already in common
            local_types = {
                name: ct for name, ct in xsd.complex_types.items()
                if name not in common_type_names
            }
            if local_types:
                sub = 2 if xsd.root_elements else 1
                lines.append(f"### {section_num}.{sub}. Local Complex Types")
                lines.append("")
                for ct_name, ct in local_types.items():
                    lines.extend(self._render_complex_type_doc(ct_name, ct, heading_level="####"))

            section_num += 1

        return "\n".join(lines)

    def _render_complex_type_doc(self, name: str, ct: XSDComplexType, heading_level: str = "####") -> List[str]:
        lines = []
        lines.append(f"{heading_level} `{name}`")
        if ct.doc:
            lines.append(f"_{ct.doc}_")
        if ct.has_simple_content:
            lines.append(f"**Content model:** simpleContent extending `{ct.simple_content_base}`")
        lines.append("")

        # Attributes table
        if ct.attributes:
            lines.append("**Attributes:**")
            lines.append("")
            lines.append("| Name | Type | Required |")
            lines.append("|---|---|---|")
            for attr in ct.attributes:
                req = "Yes" if attr.use == "required" else "No"
                lines.append(f"| `{attr.name}` | `{attr.type_display()}` | {req} |")
            lines.append("")

        # Child elements table
        if ct.elements:
            lines.append("**Child Elements:**")
            lines.append("")
            lines.append("| Element | Type | Min | Max |")
            lines.append("|---|---|---|---|")
            for elem in ct.elements:
                max_d = "∞" if elem.max_occurs == "unbounded" else elem.max_occurs
                type_d = elem.type_display()
                if elem.inline_complex:
                    type_d = "(inline complex)"
                lines.append(f"| `<{elem.name}>` | `{type_d}` | {elem.min_occurs} | {max_d} |")
            lines.append("")

        return lines

    def _ai_footer(self) -> str:
        root_count = sum(len(f.root_elements) for f in self.parser.files.values())
        return f"""---

## Appendix: AI Integration Notes

*Note for AI: This specification covers {len(self.parser.files)} XSD schema files defining {len(self.parser.all_complex_types)} complex types, {len(self.parser.all_simple_types)} simple/enumeration types, and {root_count} root message elements for the BBPS Biller Integrator system.*

*Key integration points:*
- *All messages use the `http://bbps.org/schema` namespace (BBPS core) or `http://upms.org/schema` namespace (UPMS presentment).*
- *The `Head` element (type `headType`) is present in every request/response and carries version, timestamp, originating institution, and reference IDs.*
- *The `Txn` element (type `txnType`) carries transaction metadata including message ID, reference ID, risk scores, and direct bill channel info.*
- *Enumeration types define strict allowed values — always validate against the enumeration lists in Section 3.*
- *Sample XML payloads in each section are illustrative; actual values must conform to BBPS operational guidelines.*
- *Cross-file references (xs:include) mean types from `BBPS-Common.xsd` and `UPMSCommon.xsd` are available in all schemas that include them.*

*Use this document to: generate valid XML requests, validate responses, build integration test cases, and answer developer questions about the BBPS schema.*
"""


# ─── Main ────────────────────────────────────────────────────────────────────
def main():
    if len(sys.argv) < 2:
        print("Usage: python xsd_to_specforge.py <xsd_directory> [output.md]")
        print()
        print("Example:")
        print("  python xsd_to_specforge.py ./src/main/resources/xsd bbps_schema_spec.md")
        sys.exit(1)

    xsd_dir = sys.argv[1]
    output_path = sys.argv[2] if len(sys.argv) > 2 else "bbps_schema_specification.md"

    if not os.path.isdir(xsd_dir):
        print(f"Error: '{xsd_dir}' is not a valid directory.")
        sys.exit(1)

    print(f"Parsing XSD files from: {xsd_dir}")
    parser = XSDParser(xsd_dir)
    parser.parse_all()
    print(f"  Found {len(parser.files)} XSD files")
    print(f"  Resolved {len(parser.all_complex_types)} complex types, {len(parser.all_simple_types)} simple types")

    print(f"Generating SpecForge markdown...")
    gen = SpecForgeMarkdownGenerator(parser)
    markdown = gen.generate()

    with open(output_path, "w", encoding="utf-8") as f:
        f.write(markdown)

    size_kb = os.path.getsize(output_path) / 1024
    print(f"Done! Output written to: {output_path} ({size_kb:.1f} KB)")


if __name__ == "__main__":
    main()
