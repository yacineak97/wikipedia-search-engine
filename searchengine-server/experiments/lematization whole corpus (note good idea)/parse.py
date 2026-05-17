import xml.etree.ElementTree as ET
import io
import spacy
import nltk

nlp = spacy.load('fr_core_news_sm')

# open the XML file and get the root element
context = ET.iterparse(io.BufferedReader(
    open('corpus.xml', 'rb')), events=('start', 'end'))

id = ""
title = ""


# def lemmatizer(text):
#     doc = nlp(text, disable=["tagger", "parser", "attribute_ruler"])
#     # doc = nlp.pipe([text])

#     lemmas = " ".join(
#         [token.lemma_ for token in doc if token.is_alpha and not token.is_stop and not token.is_punct])

#     return lemmas


# def lemmatizer(text):
#     for doc in nlp.pipe(text, batch_size=8000, disable=["tagger", "parser", "attribute_ruler"]):
#         # Do something with the doc here
#         lemmas = " ".join(
#             [token.lemma_ for token in doc if token.is_alpha and not token.is_stop and not token.is_punct])

#     return lemmas


def lemmatizer(texts):
    lemmas = []
    for doc in nlp.pipe(texts, disable=["tagger", "parser", "attribute_ruler"]):
        # Do something with the doc here
        text_lemmas = " ".join(
            [token.lemma_ for token in doc if token.is_alpha and not token.is_stop and not token.is_punct])

        lemmas.append(text_lemmas)

    return " ".join(lemmas)


if __name__ == '__main__':
    with open('corpus-cleaned.xml', 'wb') as f:
        # create a buffered writer object
        writer = io.BufferedWriter(f)

        # write the XML declaration and root element
        writer.write(b'<mediawiki xmlns="http://www.mediawiki.org/xml/export-0.10/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.mediawiki.org/xml/export-0.10/ http://www.mediawiki.org/xml/export-0.10.xsd" version="0.10" xml:lang="fr">\n')

        i = 0
    # iterate over the elements using the iterative parser
        for event, element in context:
            if event == 'end' and element.tag == '{http://www.mediawiki.org/xml/export-0.10/}id':
                id = element.text

            if event == 'end' and element.tag == '{http://www.mediawiki.org/xml/export-0.10/}title':
                title = element.text

            if event == 'end' and element.tag == '{http://www.mediawiki.org/xml/export-0.10/}text':
                page_element = ET.Element('page')

                id_element = ET.Element('id')
                id_element.text = id

                title_element = ET.Element('title')
                title_element.text = title

                page_element.append(id_element)
                page_element.append(title_element)

                sentences = nltk.sent_tokenize(element.text, language='french')

                element.text = lemmatizer(sentences)
                page_element.append(element)

                ET.ElementTree(page_element).write(writer, encoding='utf-8')

                # remove the element from memory to conserve memory
                page_element.clear()
                i += 1
                print(i)

            # write the closing root element
        writer.write(b'</mediawiki>\n')

        # flush any remaining data to the file
        writer.flush()
