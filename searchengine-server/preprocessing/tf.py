import io
import math
from utils.progress import progress_bar
from pathlib import Path

BASE_DIR = Path(__file__).resolve().parent.parent.parent

word_page_relation = {}
pages_freq = {}
normes = {}
pages_list = []
total_lines = sum(1 for _ in open(
    BASE_DIR / "resources" / "python-processed" / "final-word-page-relation.txt",
    "r",
    encoding="utf-8"
))

with open(BASE_DIR / "resources" / "python-processed" / "final-word-page-relation.txt", "r", buffering=8192, encoding='utf-8') as file:
    for i, line in enumerate(file, start=1):
        lineCleaned = line.strip()
        word = lineCleaned.split(":")[0]
        word = word.lower()
        pages = lineCleaned.split(":")[1].split(";")
        pages.pop()
        pages_freq = {}
        for p in pages:
            page_id = p.split(",")[0]
            word_count = p.split(",")[1]
            pages_freq[int(page_id)] = 1 + math.log10(int(word_count))
            squared_freq = pages_freq[int(page_id)] ** 2
            if int(page_id) not in normes:
                normes[int(page_id)] = 0
            normes[int(page_id)] += squared_freq

        word_page_relation[word] = pages_freq

        progress_bar(i, total_lines, "loading tf")

pagerank = {}
with open(BASE_DIR / "resources" / "java-processed" / "pagerank-scores.txt", "r", buffering=8192, encoding='utf-8') as file:
    for line in file:
        lineCleaned = line.strip()
        page_id = int(lineCleaned.split(":")[0])
        rank = float(lineCleaned.split(":")[1])
        pagerank[page_id] = rank

total_words = len(word_page_relation)
# normalize the frequences
for i, word in enumerate(word_page_relation, start=1):
    pages = word_page_relation[word]
    for page_id in pages:
        pages[page_id] = pages[page_id] / math.sqrt(normes[page_id])

    # sort by pagerank in descending order
    sorted_pages_freq = sorted(
        pages.items(), key=lambda x: (pagerank[x[0]], -x[0]), reverse=True)
    converted_pages_freq_to_dict = dict(sorted_pages_freq)

    word_page_relation[word] = converted_pages_freq_to_dict

    progress_bar(i, total_words, "normalizing tf")


# this is slow
# i = 0
# with open('tf.txt', 'wb') as f:
#     writer = io.BufferedWriter(f)

#     for word in word_page_relation:
#         line = word+":"
#         pages = word_page_relation[word]
#         for page in pages:
#             line = line + str(page) + "," + str(pages[page]) + ";"

#         line += "\n"

#         line_byte = line.encode("utf-8")
#         writer.write(line_byte)
#         i += 1
#         print(i)

#     writer.flush()
#     writer.close()



with open(BASE_DIR / "resources" / "python-processed" / "tf.txt", "wb") as f:
    writer = io.BufferedWriter(f, buffer_size=1024 * 1024)
    total_words_write = len(word_page_relation)

    for i, (word, pages) in enumerate(word_page_relation.items(), start=1):
        pieces = [word, ':']
        for page, count in pages.items():
            scientific_notation = '{:.7E}'.format(count)
            pieces.append(f'{page},{scientific_notation};')
        line = ''.join(pieces) + '\n'
        writer.write(line.encode("utf-8"))
        progress_bar(i, total_words_write, "writing tf")

    writer.flush()
    writer.close()

print("\nFinished tf.py")