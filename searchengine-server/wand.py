import math
import bisect
import spacy
import unicodedata

nlp = spacy.load('fr_core_news_md')
wikipedia_base_url = "https://fr.wikipedia.org/wiki/"

i = 0
word_idf = {}
# mutiply scores by IDF
with open("idf2.txt", "r", buffering=8192, encoding='utf-8') as file:
    for line in file:
        lineCleaned = line.strip()
        word = lineCleaned.split(":")[0]
        word = word.lower()
        idf_score = float(lineCleaned.split(":")[1])
        word_idf[word] = idf_score
        i += 1
        print(i)
i = 0
word_maxs = {}
with open("word-maxs.txt", "r", buffering=8192, encoding='utf-8') as file:
    for line in file:
        lineCleaned = line.strip()
        word = lineCleaned.split(":")[0]
        word = word.lower()
        maxs = lineCleaned.split(":")[1].split(",")
        maxs.pop()
        float_list = [float(x) for x in maxs]
        word_maxs[word] = float_list
        i += 1
        print(i)

i = 0
word_pages_relation = {}
with open("tf.txt", "r", buffering=8192, encoding='utf-8') as file:
    for line in file:
        lineCleaned = line.strip()
        word = lineCleaned.split(":")[0]
        word = word.lower()
        pages = lineCleaned.split(":")[1].split(";")
        pages.pop()
        pages_score = []
        for p in pages:
            page_id = p.split(",")[0]
            tf_score_normalized = p.split(",")[1]
            pages_score.append((int(page_id), float(
                tf_score_normalized)*word_idf[word]))

        word_pages_relation[word] = pages_score

        i += 1
        print(i)
i = 0
pagerank = {}
with open("page_rank.txt", "r", buffering=8192, encoding='utf-8') as file:
    for line in file:
        lineCleaned = line.strip()
        page_id = int(lineCleaned.split(":")[0])
        rank = float(lineCleaned.split(":")[1])
        pagerank[page_id] = rank
        i += 1
        print(i)
i = 0
page_id_to_title = {}
with open("pageid_title2.txt", "r", buffering=8192, encoding='utf-8') as file:
    for line in file:
        lineCleaned = line.strip()
        page_id = int(lineCleaned.split(":")[0])
        title = lineCleaned.split(":")[1]
        page_id_to_title[page_id] = title
        i += 1
        print(i)


def wand(request_words, word_pages_relation, word_idf):
    a = 10**(-3)
    b = 1-10**(-3)
    top_k = 1000
    pile = [(0, 0) for _ in range(top_k)]
    gamma = 0
    words_requests_ratio = 2/3

    request_words = clean_request_words(request_words)

    stop = 0
    for word in request_words:
        if word not in word_idf:
            stop += 1

    if stop == len(request_words):
        return None

    pointers = {}
    for word in request_words:
        if word in word_idf:
            pointers[word] = 0

    nr = 0
    for word in request_words:
        if word in word_idf:
            nr += word_idf[word] ** 2

    nr = math.sqrt(nr)

    # # initialize pile
    # pointers = dict(sorted(pointers.items(),
    #                 key=lambda item: word_pages_relation[item[0]][item[1]][0]))

    # words_ordrered_in_pointer = list(pointers.keys())

    # for i in range(top_k):
    #     stop = 0
    #     first_pointer = pointers[words_ordrered_in_pointer[0]]
    #     to_be_added_to_tas = word_pages_relation[words_ordrered_in_pointer[0]
    #                                              ][first_pointer][0]
    #     score = 0
    #     for j in range(len(words_ordrered_in_pointer)):
    #         if ((
    #             to_be_added_to_tas ==
    #                 word_pages_relation[words_ordrered_in_pointer[j]][pointers[words_ordrered_in_pointer[j]]][0])):
    #             score += (
    #                 word_pages_relation[words_ordrered_in_pointer[j]][pointers[words_ordrered_in_pointer[j]]][1])/nr

    #     # pile.append((to_be_added_to_tas, score))
    #     bisect.insort_right(
    #         pile, (to_be_added_to_tas, score), key=lambda x: -x[1])

    #     # if pointers[words_ordrered_in_pointer[0]] < len(word_pages_relation[words_ordrered_in_pointer[0]]) - 1:
    #     #     pointers[words_ordrered_in_pointer[0]] += 1

    #     for j in range(len(words_ordrered_in_pointer)):
    #         if ((
    #             to_be_added_to_tas == word_pages_relation[words_ordrered_in_pointer[j]][pointers[words_ordrered_in_pointer[j]]][0])
    #                 and pointers[words_ordrered_in_pointer[j]] < (len(word_pages_relation[words_ordrered_in_pointer[j]]) - 1)):
    #             pointers[words_ordrered_in_pointer[j]] += 1

    #     for i in range(len(pointers)):
    #         if (pointers[words_ordrered_in_pointer[i]]) == (len(word_pages_relation[words_ordrered_in_pointer[i]]) - 1):
    #             stop += 1

    #     if stop == len(pointers):
    #         break

    #     pointers = dict(sorted(pointers.items(),
    #                            key=lambda item: word_pages_relation[item[0]][item[1]][0]))

    #     words_ordrered_in_pointer = list(pointers.keys())

    # # pile = sorted(pile, key=lambda x: x[1], reverse=True)

    # print("////////////////")
    # print(pile)
    # print("////////////////")

    # print(pointers)

    gamma = pile[-1][1]

    pointers = dict(sorted(pointers.items(),
                    key=lambda item: pagerank[word_pages_relation[item[0]][item[1]][0]], reverse=True))

    words_ordrered_in_pointer = list(pointers.keys())

    potential_maxs = 0
    pivot_index = 0

    while (True):
        potential_maxs = 0
        first_word = words_ordrered_in_pointer[0]
        max_pagerank = pagerank[word_pages_relation[first_word]
                                [pointers[first_word]][0]]
        stop = 0
        for i in range(len(pointers)):
            potential_maxs += word_maxs[words_ordrered_in_pointer[i]
                                        ][pointers[words_ordrered_in_pointer[i]]]

            not_pivot_again = (pointers[words_ordrered_in_pointer[i]]) != (
                len(word_pages_relation[words_ordrered_in_pointer[i]]) - 1)

            if potential_maxs + max_pagerank >= gamma and not_pivot_again:
                pivot_index = i

                pivot = word_pages_relation[words_ordrered_in_pointer[pivot_index]
                                            ][pointers[words_ordrered_in_pointer[pivot_index]]][0]
                break

        for i in range(len(pointers)):
            if (pointers[words_ordrered_in_pointer[i]]) == (len(word_pages_relation[words_ordrered_in_pointer[i]]) - 1):
                stop += 1

        pivot = word_pages_relation[words_ordrered_in_pointer[pivot_index]
                                    ][pointers[words_ordrered_in_pointer[pivot_index]]][0]

        # max_pagerank = pagerank[pivot]
        # for j in range(pivot_index):
        #     w = words_ordrered_in_pointer[j]
        #     pagerank_current = pagerank[word_pages_relation[w][pointers[w]][0]]
        #     if (max_pagerank < pagerank_current):
        #         max_pagerank = pagerank_current

        pivot_pagerank = pagerank[pivot]
        for j in range(pivot_index-1):
            w = words_ordrered_in_pointer[j]

            word_pages_relation[w]

            # binary search
            lo, hi = 0, len(word_pages_relation[w]) - 1
            while lo <= hi:
                mid = (lo + hi) // 2
                if pagerank[word_pages_relation[w][mid][0]] == pivot_pagerank:
                    lo = mid
                    break
                elif pagerank[word_pages_relation[w][mid][0]] < pivot_pagerank:
                    hi = mid - 1
                else:
                    lo = mid + 1

            pointers[w] = lo

        # this k is the number of request words that contains one page
        k = 0
        score_pivot_page = 0
        for i in range(len(pointers)):
            page_current = word_pages_relation[words_ordrered_in_pointer[i]
                                               ][pointers[words_ordrered_in_pointer[i]]][0]
            if (pivot == page_current):
                k += 1
                score_current_page = word_pages_relation[words_ordrered_in_pointer[i]
                                                         ][pointers[words_ordrered_in_pointer[i]]][1]
                score_pivot_page += score_current_page

        score_pivot_page = a*(score_pivot_page / nr)
        score_pivot_page += b*(max_pagerank)

        if ((k/len(pointers) >= words_requests_ratio) and (score_pivot_page >= gamma)):
            pile.pop()
            bisect.insort_right(
                pile, (pivot, score_pivot_page), key=lambda x: -x[1])
            gamma = pile[-1][1]

        if potential_maxs < gamma or stop == len(pointers):
            break

        for i in range(len(pointers)):
            page_current = word_pages_relation[words_ordrered_in_pointer[i]
                                               ][pointers[words_ordrered_in_pointer[i]]][0]
            if (pivot == page_current):
                if (pointers[words_ordrered_in_pointer[i]] < len(word_pages_relation[words_ordrered_in_pointer[i]])-1):
                    pointers[words_ordrered_in_pointer[i]] += 1

        pointers = dict(sorted(pointers.items(),
                               key=lambda item: pagerank[word_pages_relation[item[0]][item[1]][0]], reverse=True))

        words_ordrered_in_pointer = list(pointers.keys())

    return pile


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


def getURLs(pages):
    pages_list_urls = [page_id_to_title[x[0]]
                       for x in pages if x[0] != 0 and x[1] != 0]
    return pages_list_urls


def binary_search_desc(arr, target):
    lo, hi = 0, len(arr) - 1
    while lo <= hi:
        mid = (lo + hi) // 2
        if arr[mid] == target:
            return mid
        elif arr[mid] < target:
            hi = mid - 1
        else:
            lo = mid + 1
    return lo if lo < len(arr) else -1


best_pages = wand("fermé    écoute    écoutez     ",
                  word_pages_relation, word_idf)

page_results = getURLs(best_pages)
print(page_results)


# top_k = 3
# pile = []
# gamma = 0
# words_requests_ratio = 2/3

# request = "fermé    écoute    écoutez     "
# request_words = request.split()

# pointers = {}
# for word in request_words:
#     pointers[word] = 0

# nr = 0
# for word in request_words:
#     nr += word_idf[word] ** 2

# nr = math.sqrt(nr)

# # initialize pile
# pointers = dict(sorted(pointers.items(),
#                 key=lambda item: word_pages_relation[item[0]][item[1]][0]))

# words_ordrered_in_pointer = list(pointers.keys())

# for i in range(top_k):
#     first_pointer = pointers[words_ordrered_in_pointer[0]]
#     to_be_added_to_tas = word_pages_relation[words_ordrered_in_pointer[0]
#                                              ][first_pointer][0]
#     print(to_be_added_to_tas)
#     count = 0
#     for j in range(len(words_ordrered_in_pointer)):
#         if (to_be_added_to_tas == word_pages_relation[words_ordrered_in_pointer[0]][j][0]):
#             count += 1
#     score = (
#         word_pages_relation[words_ordrered_in_pointer[0]][j][1] * count)/nr

#     # pile.append((to_be_added_to_tas, score))
#     bisect.insort_right(pile, (to_be_added_to_tas, score), key=lambda x: -x[1])

#     pointers[words_ordrered_in_pointer[0]] += 1
#     pointers = dict(sorted(pointers.items(),
#                            key=lambda item: word_pages_relation[item[0]][item[1]][0]))

#     words_ordrered_in_pointer = list(pointers.keys())

# # pile = sorted(pile, key=lambda x: x[1], reverse=True)

# gamma = pile[-1][1]

# pointers = dict(sorted(pointers.items(),
#                 key=lambda item: word_pages_relation[item[0]][item[1]][0]))

# words_ordrered_in_pointer = list(pointers.keys())

# print(pointers)

# potential_maxs = 0
# pivot_index = 0
# while (True):
#     potential_maxs = 0
#     stop = 0

#     for i in range(len(pointers)):
#         potential_maxs += word_maxs[words_ordrered_in_pointer[i]
#                                     ][pointers[words_ordrered_in_pointer[i]]]

#         if potential_maxs >= gamma:
#             pivot_index = i
#             break

#     for i in range(len(pointers)):
#         if (pointers[words_ordrered_in_pointer[i]]) == (len(word_pages_relation[words_ordrered_in_pointer[i]]) - 1):
#             stop += 1

#     if potential_maxs < gamma or stop == len(pointers):
#         break

#     pivot = word_pages_relation[words_ordrered_in_pointer[pivot_index]
#                                 ][pointers[words_ordrered_in_pointer[pivot_index]]][0]
#     new_position = 0
#     for j in range(pivot_index):
#         w = words_ordrered_in_pointer[j]
#         for i in range(pointers[w], len(word_pages_relation[w])):
#             if (word_pages_relation[w][i][0] >= pivot):
#                 pointers[w] = i
#                 break

#     # this k is the number of request words that contains one page
#     k = 0

#     score_pivot_page = 0
#     for i in range(len(pointers)):
#         page_current = word_pages_relation[words_ordrered_in_pointer[i]
#                                            ][pointers[words_ordrered_in_pointer[i]]][0]
#         if (pivot == page_current):
#             k += 1
#             score_current_page = word_pages_relation[words_ordrered_in_pointer[i]
#                                                      ][pointers[words_ordrered_in_pointer[i]]][1]
#             score_pivot_page += score_current_page

#     score_pivot_page = score_pivot_page / nr
#     if ((k/len(pointers) >= words_requests_ratio) and (score_pivot_page >= gamma)):
#         pile.pop()
#         bisect.insort_right(
#             pile, (pivot, score_pivot_page), key=lambda x: -x[1])
#         gamma = pile[-1][1]

#     for i in range(len(pointers)):
#         page_current = word_pages_relation[words_ordrered_in_pointer[i]
#                                            ][pointers[words_ordrered_in_pointer[i]]][0]
#         if (pivot == page_current):
#             if (pointers[words_ordrered_in_pointer[i]] < len(word_pages_relation[words_ordrered_in_pointer[i]])-1):
#                 pointers[words_ordrered_in_pointer[i]] += 1

#     pointers = dict(sorted(pointers.items(),
#                            key=lambda item: word_pages_relation[item[0]][item[1]][0]))

#     words_ordrered_in_pointer = list(pointers.keys())

# print(pile)
