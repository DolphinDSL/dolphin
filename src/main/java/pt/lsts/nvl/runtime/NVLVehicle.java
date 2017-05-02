package pt.lsts.nvl.runtime;

import java.util.List;

public interface NVLVehicle {
   String getId();
   String getType();
   Position getPosition();
   List<PayloadComponent> getPayload();
}
