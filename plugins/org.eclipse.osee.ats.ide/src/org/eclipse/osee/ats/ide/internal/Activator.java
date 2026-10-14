/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.internal;

import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.util.AtsTopicEvent;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.core.access.AtsBranchAccessManager;
import org.eclipse.osee.ats.ide.workdef.ValidateWorkDefXWidgetOperation;
import org.eclipse.osee.ats.ide.workflow.AtsActionRemoteTopicEventHandler;
import org.eclipse.osee.ats.ide.workflow.AtsWorkItemRemoteTopicEventHandler;
import org.eclipse.osee.framework.core.client.AccessTopicEvent;
import org.eclipse.osee.framework.jdk.core.result.XConsoleLogger;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.OseeActivator;
import org.eclipse.ui.IStartup;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

/**
 * @author Donald G. Dunne
 */
public class Activator extends OseeActivator implements IStartup {
   public static final String PLUGIN_ID = "org.eclipse.osee.ats.ide";
   public static boolean workbenchListenerStarted = false;

   @Override
   public void start(final BundleContext context) {

      String logbackConfig = System.getProperty("logback.configurationFile");
      XConsoleLogger.out("Logback Config File: [%s]\n",
         (Strings.isValid(logbackConfig) ? logbackConfig : "<not set - using logback classpath default>"));

      context.registerService(EventHandler.class.getName(), new AtsWorkItemRemoteTopicEventHandler(),
         AtsUtil.hashTable(EventConstants.EVENT_TOPIC, AtsTopicEvent.WORK_ITEM_MODIFIED.getTopic()));
      context.registerService(EventHandler.class.getName(), new AtsActionRemoteTopicEventHandler(),
         AtsUtil.hashTable(EventConstants.EVENT_TOPIC, AtsTopicEvent.ACTION_MODIFIED.getTopic()));
      context.registerService(EventHandler.class.getName(), new AtsBranchAccessManager(),
         AtsUtil.hashTable(EventConstants.EVENT_TOPIC, AccessTopicEvent.ACCESS_BRANCH_MODIFIED.getTopic()));

   }

   @Override
   public void earlyStartup() {
      XConsoleLogger.out("\nOSEE Workbench Started\n");
      validateWorkDefXWidgetsInBackground();
   }

   /**
    * Runs the client-side work definition XWidget validation once the workbench is up, off the UI thread. Any error
    * is logged SEVERE to the Error Log so a developer sees it immediately during development and fixes it (fail
    * fast). Validation problems must not disrupt startup, so failures here are only logged, never thrown.
    */
   private void validateWorkDefXWidgetsInBackground() {
      Job job = new Job("Validate ATS Work Definition XWidgets") {
         @Override
         protected IStatus run(IProgressMonitor monitor) {
            try {
               AtsApi atsApi = AtsApiService.get();
               XResultData rd = new ValidateWorkDefXWidgetOperation(atsApi).run();
               if (rd.isErrors()) {
                  OseeLog.log(Activator.class, Level.SEVERE,
                     "ATS Work Definition XWidget validation found errors that must be fixed:\n" + rd.toString());
               }
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
            return Status.OK_STATUS;
         }
      };
      job.setUser(false);
      job.setSystem(true);
      job.schedule();
   }

}
