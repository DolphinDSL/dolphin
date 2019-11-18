package pt.lsts.dolphin.runtime.mavlink.mission.missionpoints;

import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.common.msg_mission_item;
import com.MAVLink.enums.MAV_CMD;
import com.MAVLink.enums.MAV_FRAME;
import pt.lsts.dolphin.runtime.Position;
import pt.lsts.dolphin.runtime.mavlink.MAVLinkNode;
import pt.lsts.dolphin.runtime.mavlink.mission.MissionPoint;
import pt.lsts.dolphin.runtime.mavlink.mission.PointPayload;

public class LoiterPoint extends pt.lsts.dolphin.runtime.mavlink.mission.MissionPoint {

    private static final float DEFAULT_RADIUS = 20f;

    private LoiterType type;

    private float radius;

    private int arg;

    private LoiterPoint(Position position, PointPayload payload, LoiterType type, float radius, int arg) {
        super(position, payload);

        this.type = type;
        this.radius = radius;
        this.arg = arg;
    }

    @Override
    public MAVLinkMessage toMavLinkMessage(MAVLinkNode dest, int current) {

        msg_mission_item m_item = new msg_mission_item();

        m_item.command = type.getCmd();
        m_item.target_system = (short) dest.getMAVLinkId();
        m_item.target_component = 0;

        m_item.current = 0;
        m_item.autocontinue = 1;
        m_item.frame = MAV_FRAME.MAV_FRAME_GLOBAL;
        m_item.seq = current;

        m_item.param1 = arg;
        m_item.param2 = 0;
        m_item.param3 = radius;

        m_item.x = (float) (getPositionLocation().lat * Position.R2D);
        m_item.y = (float) (getPositionLocation().lon * Position.R2D);
        m_item.z = (float) getPositionLocation().hae;

        return m_item;
    }

    public enum LoiterType {
        UNLIM(MAV_CMD.MAV_CMD_NAV_LOITER_UNLIM),
        TURNS(MAV_CMD.MAV_CMD_NAV_LOITER_TURNS),
        TIME(MAV_CMD.MAV_CMD_NAV_LOITER_TIME);

        int cmd;

        LoiterType(int cmd) {
            this.cmd = cmd;
        }

        int getCmd() {
            return cmd;
        }
    }

    public static MissionPoint initLoiterPoint(double lat, double lon, double hae) {
        return initLoiterPoint(lat, lon, hae, DEFAULT_RADIUS);
    }

    public static MissionPoint initLoiterPoint(Position pos) {
        return initLoiterPoint(pos, DEFAULT_RADIUS);
    }

    public static MissionPoint initLoiterPoint(double lat, double lon, double hae, float radius) {
        return initLoiterPoint(Position.fromDegrees(lat, lon, hae), radius);
    }

    public static MissionPoint initLoiterPoint(Position pos, float radius) {
        return new LoiterPoint(pos, null, LoiterType.UNLIM, radius, 0);
    }

    public static MissionPoint initLoiterPoint(Position pos, LoiterType type, float radius, int arg) {
        return new LoiterPoint(pos, null, type, radius, arg);
    }
}
