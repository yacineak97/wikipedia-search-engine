import io
import math

word_page_relation = {}
pages_freq = {}
normes = {}
pages_list = []
i = 0
with open("final-word-page-relation.txt", "r", buffering=8192, encoding='utf-8') as file:
    for line in file:
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

        i += 1
        print(i)

i = 0
# normalize the frequences
for word in word_page_relation:
    pages = word_page_relation[word]
    for page_id in pages:
        pages[page_id] = pages[page_id] / math.sqrt(normes[page_id])

    sorted_pages_freq = sorted(pages.items(), key=lambda x: x[0])
    converted_pages_freq_to_dict = dict(sorted_pages_freq)

    word_page_relation[word] = converted_pages_freq_to_dict

    i += 1
    print(i)

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

i = 0
with open('tf.txt', 'wb') as f:
    writer = io.BufferedWriter(f, buffer_size=1024 * 1024)

    for word, pages in word_page_relation.items():
        pieces = [word, ':']
        for page, count in pages.items():
            pieces.append(f'{page},{count};')
        line = ''.join(pieces) + '\n'
        writer.write(line.encode("utf-8"))
        i += 1
        print(i)

    writer.flush()
    writer.close()
