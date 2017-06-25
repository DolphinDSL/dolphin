TAG=glued
VERSION=2016.05.0
VOLUME=$(pwd)/$(dirname $0)
CONF=uuv-docker-$1
CONFIG_FILE=$VOLUME/etc/$CONF.ini
UDP_PORT="700$1"
$VOLUME/dtpl.sh $1 $VOLUME/etc/uuv-docker.ini.template > $CONFIG_FILE
docker run --name uuv-$1 --rm -w $VOLUME -i -a stdin -a stdout -p $UDP_PORT:$UDP_PORT/udp -v $VOLUME:$VOLUME -t $TAG:$VERSION $VOLUME/build-d/dune -c $CONF -p Simulation
