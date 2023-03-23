import spacy
import unicodedata
import io
import json

nlp = spacy.load('fr_core_news_md')

total_corpus_page_number = 94374
word_page_relation = {}
pages_freq = {}
i = 0
with open("server/ressources/word-page-relation.txt", "r", buffering=8192, encoding='utf-8') as file:
    for line in file:
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
        print(i)

# print(word_page_relation)
# print(json.dumps(word_page_relation, indent=4, ensure_ascii=False))

new_word_page = {}
i = 0
for word in word_page_relation:
    p_freq = word_page_relation[word]

    doc = nlp(word, disable=["tagger", "parser", "attribute_ruler"])

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

    i += 1
    print(i)


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


i = 0
with open('server/ressources/final-word-page-relation.txt', 'wb') as f:
    writer = io.BufferedWriter(f, buffer_size=1024 * 1024)

    for word, pages in new_word_page.items():
        pieces = [word, ':']
        for page, count in pages.items():
            pieces.append(f'{page},{count};')
        line = ''.join(pieces) + '\n'
        writer.write(line.encode("utf-8"))
        i += 1
        print(i)

    writer.flush()
    writer.close()
