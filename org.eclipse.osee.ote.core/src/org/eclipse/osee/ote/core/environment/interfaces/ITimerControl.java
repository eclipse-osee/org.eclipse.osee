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

import org.eclipse.osee.ote.core.environment.EnvironmentTask;
import org.eclipse.osee.ote.core.environment.TestEnvironment;

public interface ITimerControl {
   void addTask(EnvironmentTask task, TestEnvironment environment);
   void removeTask( EnvironmentTask task);
   void cancelTimers();
   long getEnvTime();
   ICancelTimer setTimerFor(ITimeout objToNotify, int milliseconds);
   void envWait(ITimeout obj, int milliseconds) throws InterruptedException;
   void envWait(int milliseconds) throws InterruptedException;
   int getCycleCount();
   void incrementCycleCount();
   void setCycleCount(int cycle);
   void dispose();
   public void cancelAllTasks();
   public void step();
}
