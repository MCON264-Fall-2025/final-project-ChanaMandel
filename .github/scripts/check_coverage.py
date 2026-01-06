import xml.etree.ElementTree as ET
import sys
import os

def check_coverage():
    report_path = 'target/site/jacoco/jacoco.xml'

    if not os.path.exists(report_path):
        print("❌ JaCoCo report not found. Ensure tests are running with coverage.")
        sys.exit(1)

    tree = ET.parse(report_path)
    root = tree.getroot()

    # Extract coverage metrics
    counters = root.findall('.//counter[@type="INSTRUCTION"]')

    total_covered = 0
    total_missed = 0

    for counter in counters:
        total_covered += int(counter.get('covered', 0))
        total_missed += int(counter.get('missed', 0))

    total = total_covered + total_missed
    coverage_percentage = (total_covered / total * 100) if total > 0 else 0

    print(f"Code Coverage: {coverage_percentage:.2f}%")

    if coverage_percentage >= 80 :
        print("✅ Coverage and test requirements met")
        sys.exit(0)
    else:
        print("❌ Insufficient coverage. Should be more then 80%")
        sys.exit(1)

if __name__ == '__main__':
    check_coverage()
