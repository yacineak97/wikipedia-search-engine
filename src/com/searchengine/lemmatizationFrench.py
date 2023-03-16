# -*- coding: utf-8 -*-
import sys

sys.path.append("c:\\python310\\lib\\site-packages")
print(sys.version)
import spacy
from spacy.tokens import Doc
# from spacy.lang.fr.stop_words import STOP_WORDS as fr_stop
nlp = spacy.load('fr_core_news_md')

def lemmatizer(text):
    doc = nlp(text)

    lemmas = " ".join(
        [token.lemma_ for token in doc if not token.is_stop and not token.is_punct])

    return lemmas


# x=lemmatizer(
#     "voudrais non animaux % yeux sa dors @ écoutent couvre hommes j'ai ? ! ,,?!.")

# print(x)
