# The NVL 2 language repository 

(to be renamed Dolphin though not yet)

## Organization

Maven multi-module project; Eclipse (eclipse2me) `.project` file available.

## Compilation
From root dir:

	mvn clean install

## Execute IMC standalone runtime
From root dir:
	
	./scripts/nvl.sh script_file.nvl

## JAR file for Neptus plugin

	./sync_with_neptus.sh <neptus_dir>

Actual `Platform` definition for Neptus, along with UI interface etc, can be found in kmolima's feature/nvl branch.
