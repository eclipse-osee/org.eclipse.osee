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
package org.eclipse.osee.ote.core.environment.interfaces;

import java.util.Set;
import org.eclipse.osee.ote.core.TestScript;
import org.eclipse.osee.ote.core.environment.EnvironmentTask;

/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public interface ITestEnvironmentAccessor {
   void abortTestScript();

   void abortTestScript(Throwable t);

   boolean addTask(EnvironmentTask task);

   void associateObject(Class<?> c, Object obj);

   Object getAssociatedObject(Class<?> c);

   Set<Class<?>> getAssociatedObjects();

   TestScript getTestScript();

   long getEnvTime();

   IExecutionUnitManagement getExecutionUnitManagement();

   ITestLogger getLogger();

   IScriptControl getScriptCtrl();

   ITestStation getTestStation();

   ITimerControl getTimerCtrl();

   void onScriptComplete() throws InterruptedException;

   void onScriptSetup();

   ICancelTimer setTimerFor(ITimeout listener, int time);
}
