#!/usr/bin/env python3
"""mapper.py"""

import sys
import requests
from bs4 import BeautifulSoup
from urllib.parse import urljoin
import re

# input comes from STDIN (standard input)
for line in sys.stdin:
    # remove leading and trailing whitespace
    url = line.strip()
    if not url:
        continue

    try:
        headers = {'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)'}
        response = requests.get(url, headers=headers, timeout=5)
        soup = BeautifulSoup(response.text, "html.parser")

        text_site = soup.get_text(separator=' ')
        cuvinte = re.findall(r'\b[a-zA-Z]+\b', text_site.lower())

        for link in soup.find_all("a"):
            href = link.get("href")
            if href:
                full_internal_url = urljoin(url, href)

                print('URL:%s\t%s' % (url, full_internal_url))

        for cuvant in cuvinte:
            print('CUVANT:%s\t%s' % (url, cuvant))


    except Exception as e:
        continue
