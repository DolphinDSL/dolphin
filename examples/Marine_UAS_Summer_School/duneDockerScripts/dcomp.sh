TAG=glued
VERSION=2016.05.0
VOLUME=$(pwd)/$(dirname $0)
docker run --name glued-dune-compilation --rm -w $VOLUME -i -a stdin -a stdout -v $VOLUME:$VOLUME -t $TAG:$VERSION sh -c "cd build-d; make $*"
