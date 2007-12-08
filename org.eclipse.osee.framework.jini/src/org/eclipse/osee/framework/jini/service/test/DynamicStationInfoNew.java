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
package org.eclipse.osee.framework.jini.service.test;

import org.eclipse.osee.framework.jini.OSEEPerson1_4;
import org.eclipse.osee.framework.jini.service.core.FormmatedEntry;

/**
 * @author Andrew M. Finkbeiner
 */
public class DynamicStationInfoNew extends FormmatedEntry {

   private static final long serialVersionUID = 883459295979280651L;
   public Integer queueTasks;
   public Integer userCount;
   public OSEEPerson1_4[] users;

   public DynamicStationInfoNew() {
      this(0, 0, new OSEEPerson1_4[0]);
   }

   /**
    *  
    */
   public DynamicStationInfoNew(int queueTasks, int userCount, OSEEPerson1_4[] users) {
      super();
      this.queueTasks = new Integer(queueTasks);
      this.userCount = new Integer(userCount);
      this.users = users;
   }

   /**
    * @return Returns the userCount.
    */
   public Integer getUserCount() {
      return userCount;
   }

   /**
    * @return Returns the users.
    */
   public OSEEPerson1_4[] getUsers() {
      return users;
   }

   public String getFormmatedString() {
      return "User Count : " + userCount + "\n";
   }
}