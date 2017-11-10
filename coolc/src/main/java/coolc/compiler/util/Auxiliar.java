package coolc.compiler.util;

import java.util.ArrayList;

public class Auxiliar {
	protected String nombre;
	protected ArrayList<CClass> clases;
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public ArrayList<CClass> getLista() {
		return clases;
	}
	public void setLista(ArrayList<CClass> lista) {
		this.clases = lista;
	}
	public Auxiliar(String nombre, ArrayList<CClass> clases) {
		super();
		this.nombre = nombre;
		this.clases = clases;
	}
	
	public CClass getCertainClass(String clase){
		CClass aux = null;
		for(CClass p : clases){
			if(p.getNombre().equals(clase)){
				return p;
			}
		}
		return aux;
		
	}
	
}
