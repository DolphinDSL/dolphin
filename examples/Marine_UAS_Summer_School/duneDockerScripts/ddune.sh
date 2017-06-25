TAG=glued
VERSION=2016.05.0
VOLUME=$(pwd)/$(dirname $0)

docker run --name glued-dune-$1 --rm -w $VOLUME -i -a stdin -a stdout -p 8080:8080 -p 30100:30100/udp -p 6002:6002/udp -v $VOLUME:$VOLUME -t $TAG:$VERSION $VOLUME/build-d/dune -c $1 -p Simulation
