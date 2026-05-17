import io
from utils.progress import progress_bar
from pathlib import Path

BASE_DIR = Path(__file__).resolve().parent.parent.parent

word_scores = {}
total_tf_lines = sum(1 for _ in open(
    BASE_DIR / "resources" / "python-processed" / "tf.txt",
    "r",
    encoding="utf-8"
))

with open(BASE_DIR / "resources" / "python-processed" / "tf.txt", "r", buffering=8192, encoding='utf-8') as file:
    for i, line in enumerate(file, start=1):
        lineCleaned = line.strip()
        word = lineCleaned.split(":")[0]
        word = word.lower()
        pages = lineCleaned.split(":")[1].split(";")
        pages.pop()
        scores = []
        for p in pages:
            tf_score_normalized = float(p.split(",")[1])
            scores.append(tf_score_normalized)

        word_scores[word] = scores

        progress_bar(i, total_tf_lines, "loading tf scores")

total_idf_lines = sum(1 for _ in open(
    BASE_DIR / "resources" / "python-processed" / "idf.txt",
    "r",
    encoding="utf-8"
))

word_idf = {}

# mutiply scores by IDF
with open(BASE_DIR / "resources" / "python-processed" / "idf.txt", "r", buffering=8192, encoding='utf-8') as file:
    for i, line in enumerate(file, start=1):
        lineCleaned = line.strip()
        word = lineCleaned.split(":")[0]
        word = word.lower()
        idf_score = float(lineCleaned.split(":")[1])
        word_idf[word] = idf_score
        progress_bar(i, total_idf_lines, "loading idf scores")

    total_words = len(word_scores)
    for i, word in enumerate(word_scores, start=1):
        for j in range(len(word_scores[word])):
            word_scores[word][j] = word_scores[word][j]*word_idf[word]
        progress_bar(i, total_words, "multiplying tf idf")

# calculate mean
# k = 0
# sum_value = 0
# for word in word_scores:
#     for i in range(len(word_scores[word])):
#         sum_value += word_scores[word][i]
#         k += 1

# print(sum_value/k)

# this is so slow
# j = 0
# word_maxs = {}
# for word in word_scores:
#     maxs = []
#     for i in range(len(word_scores[word])):
#         sub_list = word_scores[word][i:len(word_scores[word])]
#         max_score = max(sub_list)
#         maxs.append(max_score)

#     word_maxs[word] = maxs
#     j += 1
#     print(j)

total_word_scores = len(word_scores)
word_maxs = {}

for j, word in enumerate(word_scores, start=1):
    maxs = []
    current_max = word_scores[word][-1]
    for i in range(len(word_scores[word])-1, -1, -1):
        if word_scores[word][i] > current_max:
            current_max = word_scores[word][i]
        maxs.append(current_max)
    maxs.reverse()
    word_maxs[word] = maxs
    progress_bar(j, total_word_scores, "building word maxs")


# this is so slow
# i = 0
# with open('word-maxs.txt', 'wb') as f:
#     writer = io.BufferedWriter(f)

#     for word in word_maxs:
#         line = word+":"
#         scores = word_maxs[word]
#         for i in range(len(scores)):
#             line = line + str(scores[i]) + ","

#         line += "\n"

#         line_byte = line.encode("utf-8")
#         writer.write(line_byte)
#         i += 1
#         print(i)

#     writer.flush()
#     writer.close()

with open(BASE_DIR / "resources" / "python-processed" / "word-maxs.txt", "wb") as f:
    writer = io.BufferedWriter(f)
    total_word_maxs = len(word_maxs)

    for i, word in enumerate(word_maxs, start=1):
        line = word+":"
        scores = word_maxs[word]
        pieces = [word, ':']

        for score in scores:
            scientific_notation = '{:.7E}'.format(score)
            pieces.append(f'{scientific_notation},')

        line = ''.join(pieces) + '\n'

        writer.write(line.encode("utf-8"))
        progress_bar(i, total_word_maxs, "writing word maxs")

    writer.flush()
    writer.close()

print("\nFinished word_maxs.py")