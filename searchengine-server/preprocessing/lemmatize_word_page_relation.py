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

word_page_relation = {}
pages_freq = {}
total_lines = sum(1 for _ in open(
    BASE_DIR / "resources" / "java-processed" / "word-page-relation.txt",
    "r",
    encoding="utf-8"
))

total_corpus_page_number = 0

with open(BASE_DIR / "resources" / "java-processed" / "corpus.xml", "r", encoding="utf-8") as file:
    for line in file:
        if "<page>" in line:
            total_corpus_page_number += 1

with open(BASE_DIR / "resources" / "java-processed" / "word-page-relation.txt", "r", buffering=8192, encoding='utf-8') as file:
    for i, line in enumerate(file, start=1):
        lineCleaned = line.strip()
        word = lineCleaned.split(":")[0]
        pages = lineCleaned.split(":")[1].split(";")
        pages.pop()
        pages_freq = {}
        for p in pages:
            page_id = p.split(",")[0]
            word_count = p.split(",")[1]
            pages_freq[page_id] = int(word_count)

            word_page_relation[word] = pages_freq
        i += 1
        progress_bar(i, total_lines, "loading word page relation")

# print(word_page_relation)
# print(json.dumps(word_page_relation, indent=4, ensure_ascii=False))

new_word_page = {}
total_words = len(word_page_relation)

for i, word in enumerate(word_page_relation, start=1):
    p_freq = word_page_relation[word]

    doc = nlp(word)

    lemmas = [token.lemma_ for token in doc if not token.is_stop]

    if (len(lemmas) > 0):
        lemmatizedWord = lemmas[0]

        normalized_word = unicodedata.normalize(
            "NFD", lemmatizedWord).encode("ascii", "ignore").decode("utf-8")

        # this function normalize for a word monsieur it will modify it to Monsieur thats why we lower
        normalized_word = normalized_word.lower()

        if normalized_word in new_word_page:
            new_p_freq = new_word_page[normalized_word]

            for page_id in new_p_freq:
                if page_id in p_freq:
                    p_freq[page_id] = p_freq[page_id] + new_p_freq[page_id]
                else:
                    p_freq[page_id] = new_p_freq[page_id]
            new_word_page[normalized_word] = p_freq
        else:
            new_word_page[normalized_word] = p_freq

    progress_bar(i, total_words, "lemmatizing words")


# print(json.dumps(new_word_page, indent=4))

# this is so slow
# i = 0
# with open('final-word-page-relation.txt', 'wb') as f:
#     writer = io.BufferedWriter(f)

#     for word in new_word_page:
#         line = word+":"
#         pages = new_word_page[word]

#         for page in pages:
#             line = line + page + "," + str(pages[page]) + ";"

#         line += "\n"

#         line_byte = line.encode("utf-8")
#         writer.write(line_byte)
#         i += 1
#         print(i)

#     writer.flush()
#     writer.close()




with open(BASE_DIR / "resources" / "python-processed" / "final-word-page-relation.txt", "wb") as f:
    writer = io.BufferedWriter(f, buffer_size=1024 * 1024)
    total_new_words = len(new_word_page)

    for i, (word, pages) in enumerate(new_word_page.items(), start=1):
        pieces = [word, ':']
        for page, count in pages.items():
            pieces.append(f'{page},{count};')
        line = ''.join(pieces) + '\n'
        writer.write(line.encode("utf-8"))
        progress_bar(i, total_new_words, "writing final file")

    writer.flush()
    writer.close()

print("\nFinished lemmatize_word_page_relation.py")
