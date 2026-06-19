#!/usr/bin/env python3
"""mapper.py"""

import sys
from urllib.parse import urlparse

for line in sys.stdin:
    line = line.strip()
    if not line:
        continue

    try:
        # Folosim rsplit('|', 1) pentru a despărți linia de la dreapta la stânga pt cazul in care URL-ul contine un | in el.
        url, count = line.rsplit('|', 1)

        host = urlparse(url).netloc

        if host:
            host = host.replace('www.', '')

            print('%s\t%s' % (host, count))

    except ValueError:
        continue