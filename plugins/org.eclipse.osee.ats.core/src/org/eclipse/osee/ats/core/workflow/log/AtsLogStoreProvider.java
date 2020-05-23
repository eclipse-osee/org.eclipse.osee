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

package org.eclipse.osee.ats.core.workflow.log;

import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workflow.log.ILogStorageProvider;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class AtsLogStoreProvider implements ILogStorageProvider {

   private final IAttributeResolver attrResolver;
   private final IAtsWorkItem workItem;

   public AtsLogStoreProvider(IAtsWorkItem workItem, IAttributeResolver attrResolver) {
      this.workItem = workItem;
      this.attrResolver = attrResolver;
   }

   @Override
   public String getLogXml() {
      try {
         return attrResolver.getSoleAttributeValue(workItem, AtsAttributeTypes.Log, "");
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsLogStoreProvider.class, Level.SEVERE, ex);
         return "getLogXml exception " + ex.getLocalizedMessage();
      }
   }

   @Override
   public Result saveLogXml(String xml, IAtsChangeSet changes) {
      try {
         attrResolver.setSoleAttributeValue(workItem, AtsAttributeTypes.Log, xml, changes);
         return Result.TrueResult;
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsLogStoreProvider.class, Level.SEVERE, ex);
         return new Result("saveLogXml exception " + ex.getLocalizedMessage());
      }
   }

   @Override
   public String getLogTitle() {
      try {
         return "History for \"" + workItem.getArtifactTypeName() + "\" - " + getLogId() + " - titled \"" + workItem.getName() + "\"";
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsLogStoreProvider.class, Level.SEVERE, ex);
         return "getLogTitle exception " + ex.getLocalizedMessage();
      }
   }

   @Override
   public String getLogId() {
      return workItem.getAtsId();
   }

}
