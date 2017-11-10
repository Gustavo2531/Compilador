package coolc.compiler.util;

import java.util.ArrayList;
import java.util.HashMap;

import coolc.compiler.autogen.node.AMethodFeature;


public class TableSymbol {
	private static TableSymbol instance;
	private ArrayList<Auxiliar> variables;
	private ArrayList<Method> metodos;

	private TableSymbol(){
		variables = new ArrayList<>();
		metodos = new ArrayList<>();
	}

	public ArrayList<Auxiliar> getVariables() {
		return variables;
	}

	public void setVariables(ArrayList<Auxiliar> variables) {
		this.variables = variables;
	}

	public ArrayList<Method> getMetodos() {
		
		return metodos;
	}

	public void setMetodos(ArrayList<Method> metodos) {
		this.metodos = metodos;
	}
	
	public static TableSymbol getInstance(){
		if(instance == null){
			instance = new TableSymbol();
		}
		return instance;
	}
	
	public void reset(){
		variables = new ArrayList<>();
		metodos = new ArrayList<>();
	}
	
	public Auxiliar getAuxiliar(String a){
		
		for(Auxiliar p : variables){
			if(p.getNombre().equals(a)){
				return p;
			}
		}
		return new Auxiliar(a, new ArrayList<CClass>());

		
		
	}
	public Method getMethod(String method){
		
		for(Method p : metodos){
			if(p.getNombre().equals(method)){
				return p;
			}
		}
		return new Method(method, new HashMap<String, AMethodFeature>());
		
	}
}