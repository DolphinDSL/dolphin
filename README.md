# The Dolphin language repository 

## Organization

Maven multi-module project; Eclipse (eclipse2me) `.project` file available.

## Compilation
From root dir:

	mvn clean install

## Execute IMC standalone runtime
From root dir:
	
	./scripts/dolphin.sh myScript.dolphin

## JAR file for Neptus plugin

	./sync_with_neptus.sh <neptus_dir>

Actual `Platform` definition for Neptus, along with UI interface etc, can be found in kmolima's feature/dolphin branch.
