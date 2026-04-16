import os
from datetime import datetime

PROJECT_BASE_DIR = "bbps-biller-integrator"
OUTPUT_FILE = "bbps_biller_integrator.md"

FILES_TO_EXTRACT = [
    # Priority 1
    ("1. Database Schema", "schema.sql", "src/main/resources/schema.sql", "sql"),
    ("2. API Endpoints", "API.java", "src/main/java/bharat/connect/biller/common/API.java", "java"),
    ("3. System Constants", "CommonConstants.java", "src/main/java/bharat/connect/biller/common/CommonConstants.java", "java"),
    ("4. Application Properties", "application.properties", "src/main/resources/application.properties", "properties"),

    # Priority 2
    ("5. Request Controller", "BbpsRequestController.java", "src/main/java/bharat/connect/biller/controller/BbpsRequestController.java", "java"),
    ("6. Biller Controller", "BillerController.java", "src/main/java/bharat/connect/biller/controller/BillerController.java", "java"),
    ("7. Bill Fetch Service", "BillFetchService.java", "src/main/java/bharat/connect/biller/service/BillFetchService.java", "java"),
    ("8. Bill Payment Service", "BillPaymentService.java", "src/main/java/bharat/connect/biller/service/BillPaymentService.java", "java"),
    ("9. Bill Fetch DAO", "BillFetchDao.java", "src/main/java/bharat/connect/biller/dao/BillFetchDao.java", "java"),
    ("10. Bill Payment DAO", "BillPaymentDao.java", "src/main/java/bharat/connect/biller/dao/BillPaymentDao.java", "java"),
    ("11. Signature Util", "BbpsSignatureUtil.java", "src/main/java/bharat/connect/biller/rest/BbpsSignatureUtil.java", "java"),
]

def read_file(path):
    full = os.path.join(PROJECT_BASE_DIR, path)
    if not os.path.exists(full):
        return f"// [File not found]: {path}"
    with open(full, "r", encoding="utf-8", errors="replace") as f:
        return f.read().strip()

def generate():
    lines = []
    lines.append("# BBPS Biller Integrator — SpecForge Reference\n")
    lines.append(f"**Generated:** {datetime.now().strftime('%B %d, %Y')}\n")
    lines.append("**Stack:** Java / Spring Boot / PostgreSQL\n")
    lines.append("---\n")

    for section, label, path, lang in FILES_TO_EXTRACT:
        lines.append(f"## {section}\n")
        lines.append(f"**File:** `{path}`\n")
        content = read_file(path)
        lines.append(f"```{lang}\n{content}\n```\n")

    with open(OUTPUT_FILE, "w", encoding="utf-8") as f:
        f.write("\n".join(lines))

    print(f"Done → {OUTPUT_FILE}")

if __name__ == "__main__":
    generate()
