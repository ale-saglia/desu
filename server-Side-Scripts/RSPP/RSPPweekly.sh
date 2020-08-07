##!/bin/bash
cd ~/gest-Sicurtea/server-Side-Scripts/RSPP
git reset HEAD RSPPweekly.sh
git pull
echo
python3 emailRSPP.py
