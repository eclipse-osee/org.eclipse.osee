/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.server;

import java.util.concurrent.TimeUnit;
import org.eclipse.core.runtime.IStatus;

/**
 * @author Roberto E. Escobar
 */
public interface ServerTaskInfo {

   public static enum TaskState {
      SCHEDULED,
      RUNNING,
      CANCELLED,
      WAITING,
      DONE;
   }

   String getName();

   SchedulingScheme getSchedulingScheme();

   long getInitialDelay();

   long getPeriod();

   TimeUnit getTimeUnit();

   IStatus getLastStatus();

   long getTimeUntilNextRun();

   TaskState getTaskState();
}
