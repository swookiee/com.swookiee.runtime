/*******************************************************************************
 * Copyright (c) 2014 Thorsten Krüger and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Thorsten Krüger - provide better error message on invalid configuration field types
 *******************************************************************************/

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
