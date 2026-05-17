import subprocess
import sys

scripts = [
    "lemmatize_dico.py",
    "lemmatize_word_page_relation.py",
    "tf.py",
    "idf.py",
    "word_maxs.py"
]

for script in scripts:
    print(f"\nRunning {script}...\n")
    result = subprocess.run(
        [sys.executable, f"searchengine-server/preprocessing/{script}"],
        check=True
    )

print("\nAll preprocessing completed.")