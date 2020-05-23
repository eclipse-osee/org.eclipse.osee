/*********************************************************************
 * Copyright (c) 2013 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.core.internal.log;

import java.util.Date;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workflow.log.IAtsLog;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogFactory;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogItem;
import org.eclipse.osee.ats.api.workflow.log.ILogStorageProvider;
import org.eclipse.osee.ats.api.workflow.log.LogType;
import org.eclipse.osee.ats.core.workflow.log.AtsLogStoreProvider;

/**
 * @author Donald G. Dunne
 */
public class AtsLogFactory implements IAtsLogFactory {

   @Override
   public IAtsLog getLog() {
      return new AtsLog();
   }

   @Override
   public IAtsLog getLogLoaded(IAtsWorkItem workItem, IAttributeResolver attrResolver) {
      IAtsLog log = getLog();
      AtsLogReader reader = new AtsLogReader(log, getLogProvider(workItem, attrResolver));
      reader.load();
      return log;
   }

   @Override
   public IAtsLogItem newLogItem(LogType type, Date date, AtsUser user, String state, String msg) {
      return new LogItem(type, date, user.getUserId(), state, msg);
   }

   @Override
   public void writeToStore(IAtsWorkItem workItem, IAttributeResolver attrResolver, IAtsChangeSet changes) {
      AtsLogWriter writer = new AtsLogWriter(workItem.getLog(), getLogProvider(workItem, attrResolver));
      writer.save(changes);
      getLogLoaded(workItem, attrResolver);
   }

   @Override
   public ILogStorageProvider getLogProvider(IAtsWorkItem workItem, IAttributeResolver attrResolver) {
      return new AtsLogStoreProvider(workItem, attrResolver);
   }

}
