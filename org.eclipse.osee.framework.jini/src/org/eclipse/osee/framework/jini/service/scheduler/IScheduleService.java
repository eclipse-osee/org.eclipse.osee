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
package org.eclipse.osee.framework.jini.service.scheduler;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Calendar;
import net.jini.core.lookup.ServiceItem;

public interface IScheduleService extends Remote {

   /**
    * @param startTime
    * @param interval
    * @param calendar
    * @param type
    * @param path
    * @param dateStr
    * @param bems
    * @throws RemoteException
    */
   public void addTask(int startTime, int interval, Calendar calendar, int type, String path, String dateStr, String bems, ServiceItem serviceItem) throws RemoteException;

   /**
    * @param taskId
    * @throws RemoteException
    */
   public void removeTask(String taskId) throws RemoteException;

}
