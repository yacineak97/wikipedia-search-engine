import spacy
import unicodedata
import io

nlp = spacy.load('fr_core_news_md')

dico_words = {}

with open("server/ressources/top_words.txt", "r", buffering=8192, encoding='utf-8') as file:
    for line in file:
        lineCleaned = line.strip().split(":")
        word = lineCleaned[0]
        word = word.lower()
        if word not in dico_words:
            dico_words[word] = int(lineCleaned[1])

print(len(dico_words))

new_dico_words = {}
i = 0
for word in dico_words:
    word_count = dico_words[word]

    doc = nlp(word, disable=["tagger", "parser", "attribute_ruler"])

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

    i += 1
    print(i)


with open('server/ressources/final-dico.txt', 'wb') as f:
    writer = io.BufferedWriter(f)

    for word in new_dico_words:
        line = word + ":" + str(new_dico_words[word]) + "\n"
        line_byte = line.encode("utf-8")
        writer.write(line_byte)

    writer.flush()
