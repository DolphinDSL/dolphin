package pt.lsts.dolphin.runtime.mavlink.mission.missionpoints;

import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.common.msg_mission_item;
import com.MAVLink.enums.MAV_CMD;
import com.MAVLink.enums.MAV_FRAME;
import pt.lsts.dolphin.runtime.mavlink.MAVLinkNode;
import pt.lsts.dolphin.runtime.mavlink.mission.MissionPoint;

import java.util.concurrent.TimeUnit;

public class DelayCommand extends MissionPoint {

    private long time;

    private DelayCommand(long time) {
        super(null);

        this.time = time;
    }

    @Override
    public MAVLinkMessage toMavLinkMessage(MAVLinkNode dest, int current) {

        msg_mission_item msg = new msg_mission_item();


        msg.target_component = 0;
        msg.target_system = (short) dest.getMAVLinkId();
        msg.seq = current;
        msg.autocontinue = 1;
        msg.frame = MAV_FRAME.MAV_FRAME_GLOBAL;
        msg.command = MAV_CMD.MAV_CMD_NAV_DELAY;

        long tempTime = time;

        long hours = TimeUnit.SECONDS.toHours(tempTime);
        tempTime -= TimeUnit.HOURS.toSeconds(hours);

        long minutes = TimeUnit.SECONDS.toMinutes(tempTime);
        tempTime -= TimeUnit.MINUTES.toSeconds(minutes);

        long seconds = tempTime;

        msg.param2 = hours == 0 ? -1 : hours;
        msg.param3 = minutes == 0 ? -1 : hours;
        msg.param4 = seconds;

        return msg;
    }

    public static MissionPoint initDelayPoint(long time) {
        return new DelayCommand(time);
    }

}
