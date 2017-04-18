package pt.lsts.nvl.runtime.trash;

import static pt.lsts.nvl.runtime.trash.API.*;

import java.util.List;

import pt.lsts.nvl.runtime.Availability;
import pt.lsts.nvl.runtime.NVLRuntime;
import pt.lsts.nvl.runtime.NVLVehicle;
import pt.lsts.nvl.runtime.NVLVehicleType;
import pt.lsts.nvl.runtime.PayloadComponent;

public class TemporaryExamples {


	
    List<NVLVehicle> getResourcesExample(NVLRuntime runtime) {
		List<NVLVehicle> lista = runtime.getVehicles( v -> v.getId().startsWith("abc") );
		return lista;
	}
    
    List<NVLVehicle> getAllAUVs(NVLRuntime runtime) {
		List<NVLVehicle> lista = runtime.getVehicles( v -> v.getType() == NVLVehicleType.AUV );

		return lista;
	}
    
    List<NVLVehicle> getAllAvailableAUVs(NVLRuntime runtime) {
    	List<NVLVehicle> lista = runtime.getVehicles( v -> v.getType() == NVLVehicleType.AUV && v.getAvailability() == Availability.AVAILABLE);

		return lista;
	}
    
   
 
    List<NVLVehicle> getAllAUVs_version2(NVLRuntime runtime) {
       return runtime.getVehicles( require().type(NVLVehicleType.AUV) );
	}
    
    List<NVLVehicle> getAllAvailableAUVs_version2(NVLRuntime runtime) {
        return runtime.getVehicles( require().type(NVLVehicleType.AUV).available() );
	}
    
    List<NVLVehicle> getAllAvailableAUVs_version2(NVLRuntime runtime, List<PayloadComponent> payloadComp) {
        return runtime.getVehicles( require().type(NVLVehicleType.AUV)
        		                             .available()
        		                             .payload(payloadComp) );
	}
}
