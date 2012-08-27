/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.config;

import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.client.workdef.WorkDefinitionFactory;
import org.eclipse.osee.ats.core.workdef.AtsWorkDefinitionService;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class AtsLoadWorkDefinitionsOperation extends AbstractOperation {
   private static boolean loaded = false;
   private static String NAME = "Loading ATS Work Definitions";

   public AtsLoadWorkDefinitionsOperation() {
      this(false);
   }

   public AtsLoadWorkDefinitionsOperation(boolean reload) {
      super(NAME, Activator.PLUGIN_ID);
      if (reload) {
         loaded = false;
      }
   }

   public void forceReload() {
      loaded = false;
      ensureLoaded();
   }

   public synchronized void ensureLoaded() {
      if (!loaded) {
         loaded = true;
         OseeLog.log(Activator.class, Level.INFO, NAME);
         AtsWorkDefinitionService.getService().clearCaches();
         loadWorkDefinitions();
         loaded = true;
      }
   }

   private void loadWorkDefinitions() {
      // Load in background cause not critical to ATS operation
      Job job = new Job("Loading ATS Work Definitions") {

         @Override
         protected IStatus run(IProgressMonitor monitor) {
            //            ElapsedTime time = new ElapsedTime(NAME);
            try {
               XResultData resultData = new XResultData(false);
               AtsWorkDefinitionService.getService().getWorkDef(WorkDefinitionFactory.TaskWorkflowDefinitionId,
                  resultData);
               if (!resultData.isEmpty()) {
                  OseeLog.log(Activator.class, Level.SEVERE, "Error " + NAME + resultData.toString());
               }
            } catch (Exception ex) {
               return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Error " + NAME, ex);
            }
            //            time.end();
            return Status.OK_STATUS;
         }
      };
      job.schedule();
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      ensureLoaded();
   }

   public static boolean isLoaded() {
      return loaded;
   }

}
