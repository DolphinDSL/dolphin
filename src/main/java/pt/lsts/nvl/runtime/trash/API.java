package pt.lsts.nvl.runtime.trash;

import pt.lsts.nvl.runtime.NVLRuntime;
import pt.lsts.nvl.runtime.VehicleRequirements;

// Utility class to group methods directly called by Groovy DSL / other code
public final class API {
	

	static NVLRuntime runtime() {
		return null;
	}

	static VehicleRequirements require() {
		return new VehicleRequirements();
	}

	
	private API() { }
}
