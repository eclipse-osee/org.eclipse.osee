/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.core.framework.event;

import org.eclipse.osee.framework.jdk.core.type.IPropertyStore;
import org.eclipse.osee.ote.core.TestCase;
import org.eclipse.osee.ote.core.TestScript;

public class BaseEvent implements IEventData {

	private final IPropertyStore propertyStore;
	private final TestScript test;
	private final TestCase testCase;

	public BaseEvent(IPropertyStore propertyStore, TestScript test) {
		this.propertyStore = propertyStore;
		this.test = test;
		this.testCase = null;
	}

	public BaseEvent(IPropertyStore propertyStore, TestScript test, TestCase testCase) {
		this.propertyStore = propertyStore;
		this.test = test;
		this.testCase = testCase;
	}
	
	public BaseEvent(TestScript test, TestCase testCase) {
	      this.propertyStore = null;
	      this.test = test;
	      this.testCase = testCase;
	}

	public BaseEvent(TestScript test) {
		this.test = test;
		this.testCase = null;
		this.propertyStore = null;
	}

	public TestScript getTest() {
		return test;
	}

	public TestCase getTestCase() {
		return testCase;
	}

	public IPropertyStore getProperties() {
		return propertyStore;
	}

	public String getScriptClass() {
		return propertyStore.get("classname");
	}
}
