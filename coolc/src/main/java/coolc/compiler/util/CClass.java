package coolc.compiler.util;

import java.util.HashMap;

import coolc.compiler.autogen.node.TTypeId;

public class CClass {
	
	protected String nombre;
	protected HashMap<String, TTypeId> types;
	
	public CClass(String nombre, HashMap<String, TTypeId> types) {
		super();
		this.nombre = nombre;
		this.types = types;
	}
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public HashMap<String, TTypeId> getTypes() {
		return types;
	}
	public void setTypes(HashMap<String, TTypeId> types) {
		this.types = types;
	}

}