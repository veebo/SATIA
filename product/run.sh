#!/usr/bin/env bash
sudo ./bin/standalone.sh --debug -Djboss.bind.address=0.0.0.0 -Djboss.bind.address.management=0.0.0.0 --server-config=standalone.xml
