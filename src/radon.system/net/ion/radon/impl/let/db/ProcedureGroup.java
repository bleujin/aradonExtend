package net.ion.radon.impl.let.db;


public class ProcedureGroup {
	
	private Procedures[] procedures = new Procedures[0];
	
	public Procedures[] getProcedures() {
		return procedures;
	}

	public void setProcedures(Procedures[] procedures) {
		this.procedures = procedures;
	}

//	public List<Map<String, Object>> getProcedureList(){
//		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//		for(Procedures pc : procedures){
//			list.add(pc.toMap());
//		}
//		return list;
//	}

}
