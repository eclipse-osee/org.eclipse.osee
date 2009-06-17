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

public interface IRunManager {

   public boolean addListener(ITestLifecycleListener listener);

   public void clearAllListeners();

   public boolean removeListener(ITestLifecycleListener listener);

   public IMethodResult run(TestEnvironment env, IPropertyStore propertyStore);

   public boolean abort();

   public boolean abort(Throwable th, boolean wait);

   public boolean isAborted();
   
   public TestScript getCurrentScript();
}
