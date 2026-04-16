import os
from datetime import datetime

PROJECT_BASE_DIR = "bbps-biller-integrator"
OUTPUT_FILE = "bbps_dao_impl.md"

FILES_TO_EXTRACT = [
    ("1. Bill Fetch DAO Implementation", "BillFetchDaoImpl.java", 
     "src/main/java/bharat/connect/biller/dao/impl/BillFetchDaoImpl.java", "java"),
    ("2. Bill Payment DAO Implementation", "BillPaymentDaoImpl.java", 
     "src/main/java/bharat/connect/biller/dao/impl/BillPaymentDaoImpl.java", "java"),
]

def read_file(path):
    full = os.path.join(PROJECT_BASE_DIR, path)
    if not os.path.exists(full):
        return f"// [File not found]: {path}"
    with open(full, "r", encoding="utf-8", errors="replace") as f:
        return f.read().strip()

def generate():
    lines = []
    lines.append("## 12. Bill Fetch DAO Implementation\n")
    lines.append(f"*Generated: {datetime.now().strftime('%B %d, %Y')}*\n")
    lines.append("*Note for AI: Contains the actual SQL queries used to find unpaid bills by customer params.*\n")
    lines.append("---\n")

    for section, label, path, lang in FILES_TO_EXTRACT:
        lines.append(f"### {section}\n")
        lines.append(f"**File:** `{path}`\n")
        content = read_file(path)
        lines.append(f"```{lang}\n{content}\n```\n")

    with open(OUTPUT_FILE, "w", encoding="utf-8") as f:
        f.write("\n".join(lines))

    print(f"Done → {OUTPUT_FILE}")

if __name__ == "__main__":
    generate()
