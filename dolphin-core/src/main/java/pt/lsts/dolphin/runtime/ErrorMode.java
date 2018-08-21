package pt.lsts.dolphin.runtime;

import groovy.lang.Closure;

public class ErrorMode {
	private Closure cl;
	private Type mode;
	
	public static enum Type {
		IGNORE,
		PROPAGATE
	}

	
	public ErrorMode(Closure c,Type t) {
		cl = c;
		mode = t;
	}
	public ErrorMode(Type t) {
		cl = null;
		mode = t;
	}
	
	/**
	 * @param the closure to be executed when an runtime error occurs
	 */
	public void setClosure(Closure code) {
		this.cl = code;
	}
	/**
	 * @return true if it is possible to call the code and false otherwise
	 */
	public String runClosure(String info) {
		if(this.cl != null) {
			cl.call(info);
			return info;
		}
		return null;
	}

	/**
	 * @return the mode
	 */
	public Type getMode() {
		return mode;
	}

	/**
	 * @param mode the mode to set
	 */
	public void setMode(Type mode) {
		this.mode = mode;
	}

}
