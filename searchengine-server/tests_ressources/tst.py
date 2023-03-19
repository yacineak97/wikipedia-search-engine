import spacy
import unicodedata

nlp = spacy.load('fr_core_news_md')


text = "fermé    écoute    écoutez     ".split()
text = ' '.join(text)
doc = nlp(text, disable=[
          "tagger", "parser", "attribute_ruler"])
lemmas = [token.lemma_ for token in doc if not token.is_stop]

cleaned_request_words = []
for word in lemmas:
    normalized_word = unicodedata.normalize(
        "NFD", word).encode("ascii", "ignore").decode("utf-8")
    normalized_word = normalized_word.lower()

    cleaned_request_words.append(normalized_word)

print(cleaned_request_words)
