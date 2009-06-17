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
package org.eclipse.osee.ote.core.framework.testrun;

import org.eclipse.osee.framework.jdk.core.type.IPropertyStore;
import org.eclipse.osee.ote.core.TestScript;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.framework.IMethodResult;

public interface ITestRunManager {

   IMethodResult initialize(TestEnvironment env, IPropertyStore propertyStore);

   IMethodResult run(IPropertyStore propertyStore, TestEnvironment environment)  ;

   IMethodResult dispose()  ;

   TestScript getTest();

   boolean abort();

   boolean abort(Throwable th, boolean wait);

   boolean isAborted();
}
