#!/usr/bin/env python3
"""reducer.py"""

import sys
from collections import Counter

current_host = None
current_count = 0
top_sites = Counter()

for line in sys.stdin:
    line = line.strip()

    try:
        host, count = line.split('\t', 1)
        count = int(count)
    except ValueError:
        continue

    if current_host != host:
        if current_host:
            top_sites[current_host] += current_count

        current_host = host
        current_count = count
    else:
        current_count += count

if current_host:
    top_sites[current_host] += current_count

print("CELE MAI VIZITATE 5 SITE-URI:")
for site, nr_cautari in top_sites.most_common(5):
    print('%s\t%s' % (site, nr_cautari))