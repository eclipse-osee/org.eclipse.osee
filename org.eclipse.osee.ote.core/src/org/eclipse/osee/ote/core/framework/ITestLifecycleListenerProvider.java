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
package org.eclipse.osee.ote.core.framework;

import org.eclipse.osee.framework.jdk.core.type.IPropertyStore;
import org.eclipse.osee.ote.core.TestScript;
import org.eclipse.osee.ote.core.environment.TestEnvironment;

public interface ITestLifecycleListenerProvider {
	void clear();
	boolean addListener(ITestLifecycleListener listener);
	boolean removeListener(ITestLifecycleListener listener);
	IMethodResult notifyPostDispose(IPropertyStore propertyStore, TestEnvironment env);
	IMethodResult notifyPostInstantiation(IPropertyStore propertyStore, TestScript test, TestEnvironment env);
	IMethodResult notifyPreDispose(IPropertyStore propertyStore, TestScript test, TestEnvironment env) ;
	IMethodResult notifyPreInstantiation(IPropertyStore propertyStore, TestEnvironment env) ;
}
