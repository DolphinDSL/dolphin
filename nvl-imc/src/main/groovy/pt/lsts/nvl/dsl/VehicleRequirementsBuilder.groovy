package pt.lsts.nvl.dsl;


import pt.lsts.nvl.runtime.PayloadComponent
import pt.lsts.nvl.runtime.Position
import pt.lsts.nvl.runtime.VehicleRequirements

@DSLClass
final class VehicleRequirementsBuilder 
  extends Instruction<VehicleRequirements> {
    
  VehicleRequirements req = new VehicleRequirements()
  
  VehicleRequirementsBuilder() {
  }
  
  void type(String t) {
    req.setRequiredType t
  }
  
  void payload(String... payloads) {
    List<PayloadComponent> list = []
    payloads.each  {  
      list.add new PayloadComponent() {
        @Override
        String getName() {
          return it;
        }
		@Override
		Map<String,String> getParameters(){
			return Collections.emptyMap();
		}
		@Override
		void setParameter(String key,String value){
			//TODO
		}
        

      }
    }
    req.setRequiredPayload list
  }
  
  void near(Position location, double radius) {
    req.setAreaCenter(location)
    req.setAreaRadius(radius)
  }

  @Override
  public VehicleRequirements execute() {
    req
  }
}