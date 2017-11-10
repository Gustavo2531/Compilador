package coolc.compiler.util;


import java.util.HashMap;


import coolc.compiler.autogen.node.AMethodFeature;

public class Method {
	
	protected String nombre;
	protected HashMap<String, AMethodFeature> features;
	
	public Method(String nombre, HashMap<String, AMethodFeature> features) {
		super();
		this.nombre = nombre;
		this.features = features;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public HashMap<String, AMethodFeature> getFeatures() {
		return features;
	}
	public void setFeatures(HashMap<String, AMethodFeature> features) {
		this.features = features;
	}

	

}