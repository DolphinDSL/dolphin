package pt.lsts.dolphin.dsl.mavlink

import pt.lsts.dolphin.dsl.Builder
import pt.lsts.dolphin.dsl.DSLClass
import pt.lsts.dolphin.util.wgs84.NED

@DSLClass
class MoveBuilder extends Builder<NED> {

    double north = 0, east = 0, down = 0;

    void north(double north) {
        this.north = north;
    }

    void east (double east) {
        this.east = east;
    }

    void down(double down) {
        this.down = down;
    }

    @Override
    NED build() {
        return new NED(north, east, down);
    }
}
