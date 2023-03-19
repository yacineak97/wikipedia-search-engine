from flask import Flask, render_template, request
import math
import bisect
import spacy
import unicodedata

nlp = spacy.load('fr_core_news_md')


def clean_request_words(request_text):
    request_text = request_text.split()
    request_text = ' '.join(request_text)
    doc = nlp(request_text, disable=[
        "tagger", "parser", "attribute_ruler"])
    lemmas = [token.lemma_ for token in doc if not token.is_stop]

    cleaned_words = []
    for word in lemmas:
        normalized_word = unicodedata.normalize(
            "NFD", word).encode("ascii", "ignore").decode("utf-8")
        normalized_word = normalized_word.lower()

        cleaned_words.append(normalized_word)

    return cleaned_words


x = clean_request_words("algebre")
print(x)
