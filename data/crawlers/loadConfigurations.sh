#!/bin/bash

mongoimport --db voxxrin-crawlers --drop --collection configurations --type json --jsonArray --file configurations.json