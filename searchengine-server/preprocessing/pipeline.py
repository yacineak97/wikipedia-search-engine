from pathlib import Path
import subprocess
import sys

scripts = [
    "lemmatize_dico.py",
    "lemmatize_word_page_relation.py",
    "tf.py",
    "idf.py",
    "word_maxs.py"
]

BASE_DIR = Path(__file__).resolve().parent.parent.parent
output_dir = BASE_DIR / "resources" / "python-processed"
output_dir.mkdir(parents=True, exist_ok=True)

SCRIPT_DIR = Path(__file__).resolve().parent
for script in scripts:
    print(f"\nRunning {script}...\n")
    script_path = SCRIPT_DIR / script

    result = subprocess.run(
        [sys.executable, str(script_path)],
        check=True
    )

print("\nAll preprocessing completed.")