package pt.lsts.dolphin.dsl.mavlink

import pt.lsts.dolphin.dsl.Builder
import pt.lsts.dolphin.dsl.DSLClass
import pt.lsts.dolphin.runtime.Position

@DSLClass
class PositionBuilder extends Builder<Position> {

    double lat, lon, hae = 0;

    void lat(double lat) {
        this.lat = lat;
    }

    void lon(double lon) {
        this.lon = lon;
    }

    void hae(double hae) {
        this.hae = hae;
    }

    void height(double hae) {
        this.hae = hae;
    }

    @Override
    Position build() {
        return Position.fromDegrees(lat, lon, hae);
    }
}
