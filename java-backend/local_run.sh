#!/bin/sh
dir="${1:-/home/rui/Documents/investment/company_data/tiliote/extracted}"
mvn compile org.codehaus.mojo:exec-maven-plugin:3.5.0:java \
  -Dexec.mainClass="io.github.rxue.investment.cli.Main" \
  -Dexec.args="$dir" \
  -Dexec.classpathScope=runtime
