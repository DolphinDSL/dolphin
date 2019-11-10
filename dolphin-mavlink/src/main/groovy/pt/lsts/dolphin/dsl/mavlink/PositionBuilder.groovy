package pt.lsts.dolphin.dsl.mavlink

import pt.lsts.dolphin.dsl.Builder
import pt.lsts.dolphin.dsl.DSLClass
import pt.lsts.dolphin.runtime.Position
import pt.lsts.dolphin.util.wgs84.NED
import pt.lsts.dolphin.util.wgs84.WGS84

@DSLClass
class PositionBuilder extends Builder<Position> {

    double lat, lon, hae;

    void lat(double lat) {
        this.lat = lat;
    }

    void lon(double lon) {
        this.lon = lon;
    }

    void hae(double hae) {
        this.hae = hae;
    }

    @Override
    Position build() {
        return Position.fromDegrees(lat, lon, hae);
    }
}
