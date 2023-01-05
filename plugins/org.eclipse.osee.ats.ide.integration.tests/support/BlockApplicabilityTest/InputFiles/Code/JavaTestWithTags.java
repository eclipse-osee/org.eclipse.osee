/*********************************************************************
 * Copyright (c) 2023 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
package saw.productline.package

import robot.api;

public class JavaTestWithTags {

	private String test;
	
	public JavaTestWithTags(String test){
		this.test = test;
	}
	
	public static void main(){
		String str;
		/* Configuration[Product A] */
		str = testMethodName();
		/* Configuration Else */
		str = "Name: Light Robot";
		/* End Configuration */
		/* Configuration[Product C] */
		str = testMethodType();
		/* End Configuration */
		System.out.println(str);
	}
	
	/* Configuration[Product A] */
	private String testMethodName(){
		return test + robot.getName();
	}
	/* End Configuration */
	
	/* Configuration[Product C] */
	private String testMethodType(){
		return test + robot.getType()
	}
	/* End Configuration */
}
