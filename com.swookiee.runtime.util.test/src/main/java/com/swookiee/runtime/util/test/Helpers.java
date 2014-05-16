package com.swookiee.runtime.util.test;

import java.util.HashMap;

/* in here because creating them on the groovy side creates additional fields that throw off the algorithm */
public class Helpers {

	public static class ConfigWrapper {
		public PojoWithNonPrimitiveField offender = new PojoWithNonPrimitiveField();
	}
	
    public static class PojoWithNonPrimitiveField implements com.swookiee.runtime.util.configuration.Configuration {
		@Override
		public String getPid() {
			return "whatever";
		}
		
        public HashMap<String, String> aMap = new HashMap<String, String>();
    }
}
