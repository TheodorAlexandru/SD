#!/usr/bin/env python3
"""reducer.py"""

import sys
from collections import Counter

"""
current_page = None
internal_links = []

daor pt mapare url, url_interne 
"""

current_page = None
internal_links = []
count_cuvinte = Counter()

# input comes from STDIN
for line in sys.stdin:
    # remove leading and trailing whitespace
    line = line.strip()

    try:
        key, value = line.split('\t', 1)
    except ValueError:
        continue

    """
        daor pt mapare url, url_interne 
        for line in sys.stdin:
            # remove leading and trailing whitespace
            line = line.strip()
            try:
                url, full_internal_url = line.split('\t', 1)
            except ValueError:
                continue
            # this IF-switch only works because Hadoop sorts map output
            # by key (here: word) before it is passed to the reducer
            if current_page != url:
                if current_page:
                    # write result to STDOUT
                    print('%s\t%s' % (current_page, internal_links))
                internal_links = []
                current_page = url
            internal_links.append(full_internal_url)
        
        if current_page:
            print('%s\t%s' % (current_page, internal_links)) 
    """

    # this IF-switch only works because Hadoop sorts map output
    # by key (here: word) before it is passed to the reducer
    if current_page != key:
        if current_page:
            if current_page.startswith('URL:'):
                # write result to STDOUT
                clean_url = current_page.replace('URL:','')
                print('SITE_MAP: %s\t%s' % (clean_url, internal_links))
            elif current_page.startswith('CUVANT:'):
                clean_url = current_page.replace('CUVANT:', '')
                top_5_words = count_cuvinte.most_common(5)
                print('TOP_5_CUVINTE: %s\t%s' % (clean_url, top_5_words))
        internal_links = []
        count_cuvinte = Counter()
        current_page = key

    if key.startswith('URL:'):
        if value not in internal_links:
            internal_links.append(value)
    elif key.startswith('CUVANT:'):
        count_cuvinte[value] += 1

if current_page:
    if current_page.startswith('URL:'):
        # write result to STDOUT
        clean_url = current_page.replace('URL:', '')
        print('SITE_MAP: %s\t%s' % (clean_url, internal_links))
    elif current_page.startswith('CUVANT:'):
        clean_url = current_page.replace('CUVANT:', '')
        top_5_words = count_cuvinte.most_common(5)
        print('TOP_5_CUVINTE: %s\t%s' % (clean_url, top_5_words))
