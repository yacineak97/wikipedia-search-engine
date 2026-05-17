import io
import math
from utils.progress import progress_bar
from pathlib import Path

BASE_DIR = Path(__file__).resolve().parent.parent.parent

dico_words = {}

total_lines = sum(1 for _ in open(
    BASE_DIR / "resources" / "python-processed" / "final-dico.txt",
    "r",
    encoding="utf-8"
))

total_corpus_page_number = 0

with open(BASE_DIR / "resources" / "java-processed" / "corpus.xml", "r", encoding="utf-8") as file:
    for line in file:
        if "<page>" in line:
            total_corpus_page_number += 1

print(f"Total corpus page number: {total_corpus_page_number}")  

with open(BASE_DIR / "resources" / "python-processed" / "final-dico.txt", "r", buffering=8192, encoding='utf-8') as file:
    for i, line in enumerate(file, start=1):
        lineCleaned = line.strip().split(":")
        word = lineCleaned[0]
        word = word.lower()
        if word not in dico_words:
            dico_words[word] = math.log10(
                total_corpus_page_number / int(lineCleaned[1]))
        progress_bar(i, total_lines, "loading idf")

with open(BASE_DIR / "resources" / "python-processed" / "idf.txt", "wb") as f:
    writer = io.BufferedWriter(f)
    total_words = len(dico_words)

    for i, word in enumerate(dico_words, start=1):
        line = word + ":" + str(dico_words[word]) + "\n"
        line_byte = line.encode("utf-8")
        writer.write(line_byte)
        progress_bar(i, total_words, "writing idf")

    writer.flush()
    writer.close()

print("\nFinished idf.py")
# print(math.log10(5))
