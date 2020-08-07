##!/bin/bash
cd ~/gest-Sicurtea/server-Side-Scripts/RSPP
git reset --hard
git pull
chmod a+x RSPPweekly.sh
echo
python3 emailRSPP.py
