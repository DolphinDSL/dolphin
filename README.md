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

## Build JAR to support Neptus plugin

From root dir:

	./sync_with_neptus.sh <neptus_dir>

