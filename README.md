# The Dolphin language repository 

## Organization

Maven multi-module project; Eclipse (eclipse2me) `.project` file available.

## Compilation
From root dir:

	mvn clean install

## Execute IMC standalone runtime
From root dir:
	
	./scripts/dolphin-imc.sh myScript.dolphin

## JAR file for Neptus plugin

	./scripts/sync_neptus_plugin.sh <dolphi-neptus-plugin-dir>

See `dolphin-neptus-plugin` repository.
Actual `Platform` definition for Neptus, along with UI interface etc, can be found in the dolphin-neptus-plugin 
