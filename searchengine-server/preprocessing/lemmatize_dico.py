import spacy
import subprocess
import unicodedata
import io
import sys
from utils.progress import progress_bar
from pathlib import Path

BASE_DIR = Path(__file__).resolve().parent.parent.parent

MODEL = "fr_core_news_md"

# Auto-install model if missing
try:
    nlp = spacy.load(
        MODEL,
        disable=["tagger", "parser", "attribute_ruler"]
    )

except OSError:
    print(f"Installing spaCy model: {MODEL}")

    subprocess.check_call([
        sys.executable,
        "-m",
        "spacy",
        "download",
        MODEL
    ])

    nlp = spacy.load(MODEL)

dico_words = {}

with open(BASE_DIR / "resources" / "java-processed" / "top_words.txt", "r", buffering=8192, encoding='utf-8') as file:
    for line in file:
        lineCleaned = line.strip().split(":")
        word = lineCleaned[0]
        word = word.lower()
        if word not in dico_words:
            dico_words[word] = int(lineCleaned[1])

print(f"Loaded {len(dico_words)} words.")

new_dico_words = {}
total_words = len(dico_words)

for i, word in enumerate(dico_words, start=1):
    word_count = dico_words[word]

    doc = nlp(word)

    lemmas = [token.lemma_ for token in doc if not token.is_stop]

    if (len(lemmas) > 0):
        lemmatizedWord = lemmas[0]

        normalized_word = unicodedata.normalize(
            "NFD", lemmatizedWord).encode("ascii", "ignore").decode("utf-8")

        # this function normalize for a word monsieur it will modify it to Monsieur thats why we lower
        normalized_word = normalized_word.lower()

        if normalized_word in new_dico_words:
            new_dico_words[normalized_word] = new_dico_words[normalized_word] + word_count
        else:
            new_dico_words[normalized_word] = word_count

    progress_bar(i, total_words, "lemmatizing dictionary")


with open(BASE_DIR / "resources" / "python-processed" / "final-dico.txt", "wb") as f:
    writer = io.BufferedWriter(f)
    total_new_words = len(new_dico_words)

    for i, word in enumerate(new_dico_words, start=1):
        line = word + ":" + str(new_dico_words[word]) + "\n"
        line_byte = line.encode("utf-8")
        writer.write(line_byte)

        progress_bar(i, total_new_words, "writing_final_dico")

    writer.flush()
    writer.close()

print("\nFinished lemmatize_dico.py")