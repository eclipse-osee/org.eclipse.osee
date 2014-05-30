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
package org.eclipse.osee.ats.core.workflow.log;

import java.util.logging.Level;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workflow.log.ILogStorageProvider;
import org.eclipse.osee.ats.core.internal.AtsCoreService;
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
         OseeLog.log(AtsCoreService.class, Level.SEVERE, ex);
         return "getLogXml exception " + ex.getLocalizedMessage();
      }
   }

   @Override
   public IStatus saveLogXml(String xml, IAtsChangeSet changes) {
      try {
         attrResolver.setSoleAttributeValue(workItem, AtsAttributeTypes.Log, xml, changes);
         return Status.OK_STATUS;
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsCoreService.class, Level.SEVERE, ex);
         return new Status(IStatus.ERROR, AtsCoreService.PLUGIN_ID, "saveLogXml exception " + ex.getLocalizedMessage());
      }
   }

   @Override
   public String getLogTitle() {
      try {
         return "History for \"" + workItem.getWorkData().getArtifactTypeName() + "\" - " + getLogId() + " - titled \"" + workItem.getName() + "\"";
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsCoreService.class, Level.SEVERE, ex);
         return "getLogTitle exception " + ex.getLocalizedMessage();
      }
   }

   @Override
   public String getLogId() {
      return workItem.getAtsId();
   }

}
