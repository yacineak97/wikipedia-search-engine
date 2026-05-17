import spacy
import nltk
# if you haven't downloaded the punkt tokenizer for French already
# nltk.download('punkt')
# from spacy.lang.fr.stop_words import STOP_WORDS as fr_stop
nlp = spacy.load('fr_core_news_md')


# def lemmatizer(texts):
#     lemmas = []
#     for doc in nlp.pipe(texts, disable=["tagger", "parser", "attribute_ruler"]):
#         # Do something with the doc here
#         text_lemmas = " ".join(
#             [token.lemma_ for token in doc if not token.is_stop and not token.is_punct])

#         lemmas.append(text_lemmas)

#     return " ".join(lemmas)

def lemmatizer(text):
    for doc in nlp.pipe(text, disable=["tagger", "parser", "attribute_ruler"]):
        # Do something with the doc here
        lemmas = " ".join(
            [token.lemma_ for token in doc if token.is_alpha and not token.is_stop and not token.is_punct])

    return lemmas


def extract_words(text):
    doc = nlp(text)
    words = [
        token.text for token in doc if not token.is_stop and not token.is_punct and token.is_alpha]
    return words


if __name__ == '__main__':
    # x = lemmatizer(
    #     "voudrais non animaux % yeux sa dors @ écoutent couvre hommes j'ai ? ! ,,?!.")

    text = ["Bonjour à tous.|-  Comment allez-vous allez-vous dff-gfd fbddb aujourd'hui [[ ]] ? J'espère que vous allez bien."]
    # sentences = nltk.sent_tokenize(text, language='french')

    x = lemmatizer(text)

    # x = extract_words(text)

    print(x)
