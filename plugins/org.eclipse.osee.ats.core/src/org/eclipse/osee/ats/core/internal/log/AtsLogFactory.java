/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.internal.log;

import java.util.Date;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.workflow.log.IAtsLog;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogFactory;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogItem;
import org.eclipse.osee.ats.api.workflow.log.ILogStorageProvider;
import org.eclipse.osee.ats.api.workflow.log.LogType;
import org.eclipse.osee.ats.core.AtsCore;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class AtsLogFactory implements IAtsLogFactory {

   @Override
   public IAtsLog getLog(ILogStorageProvider storageProvider, IAtsUserService userService) {
      return new AtsLog(storageProvider, userService);
   }

   @Override
   public IAtsLogItem newLogItem(LogType type, Date date, IAtsUser user, String state, String msg, String hrid) throws OseeCoreException {
      return new LogItem(type, date, user, state, msg, hrid, AtsCore.getUserService());
   }

   @Override
   public IAtsLogItem newLogItem(LogType type, String date, String userId, String state, String msg, String hrid) throws OseeCoreException {
      return new LogItem(type, date, userId, state, msg, hrid, AtsCore.getUserService());
   }

   @Override
   public IAtsLogItem newLogItem(String type, String date, String userId, String state, String msg, String hrid) throws OseeCoreException {
      return new LogItem(type, date, userId, state, msg, hrid, AtsCore.getUserService());
   }

}
