import io
import math

total_corpus_page_number = 94374

dico_words = {}

with open("final-dico.txt", "r", buffering=8192, encoding='utf-8') as file:
    for line in file:
        lineCleaned = line.strip().split(":")
        word = lineCleaned[0]
        word = word.lower()
        if word not in dico_words:
            dico_words[word] = math.log10(
                total_corpus_page_number / int(lineCleaned[1]))

print(len(dico_words))

with open('idf.txt', 'wb') as f:
    writer = io.BufferedWriter(f)
    for word in dico_words:
        line = word + ":" + str(dico_words[word]) + "\n"
        line_byte = line.encode("utf-8")
        writer.write(line_byte)

    writer.flush()
    writer.close()


print(math.log10(5))
