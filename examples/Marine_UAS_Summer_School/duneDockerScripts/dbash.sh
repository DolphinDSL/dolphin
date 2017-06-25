TAG=glued
VERSION=2016.05.0
VOLUME=$(pwd)/$(dirname $0)
docker run --name glued-bash --rm -w $VOLUME -i -a stdin -a stdout -v $VOLUME:$VOLUME -t $TAG:$VERSION
