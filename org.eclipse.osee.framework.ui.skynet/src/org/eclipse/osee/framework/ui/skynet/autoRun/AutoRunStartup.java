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
package org.eclipse.osee.framework.ui.skynet.autoRun;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.database.DatabaseActivator;
import org.eclipse.osee.framework.jdk.core.util.AEmail;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.plugin.core.util.ExtensionPoints;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.autoRun.IAutoRunTask.RunDb;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultData;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultPage;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultPage.Manipulations;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;

/**
 * This class will be kicked off during any normal run of OSEE. It will check for any -Dosee.autoRun=taskId options and
 * inquire, through extension points, which plugins need to perform tasks for the taskId specified. After tests are
 * completed, this class will shutdown the workbench.
 * 
 * @author Donald G. Dunne
 */
public class AutoRunStartup implements IStartup {

   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(AutoRunStartup.class);
   public static String EXTENSION_POINT = "org.eclipse.osee.framework.skynet.core.AutoRunTask";

   /* (non-Javadoc)
    * @see org.eclipse.ui.IStartup#earlyStartup()
    */
   public void earlyStartup() {
      final String autoRunTaskId = OseeProperties.getInstance().getAutoRun();
      final XResultData resultData = new XResultData(SkynetGuiPlugin.getLogger());
      IAutoRunTask autoRunTask = null;
      try {
         if (autoRunTaskId == null) {
            logger.log(Level.INFO, "Checked AutoRunStartup...Nothing to run.");
            return;
         }

         // Run the tasks that match the taskId
         logger.log(Level.INFO, "Running AutoRunStartup; Id=\"" + autoRunTaskId + "\"");
         autoRunTask = getAutoRunTask(autoRunTaskId);
         if (autoRunTask == null) {
            // Send email of completion
            AEmail email =
                  new AEmail(new String[] {"donald.g.dunne@boeing.com"}, "donald.g.dunne@boeing.com",
                        "donald.g.dunne@boeing.com", "Can't find AutoRunTask; Id=\"" + autoRunTaskId + "\" ", " ");
            email.send();
         } else {
            Result result = validateAutoRunExecution(autoRunTask);
            if (result.isFalse()) {
               resultData.log("Auto Run Task invalid to run: " + result.getText());
            } else {
               resultData.log("Starting AutoRunTaskId=\"" + autoRunTaskId + "\" - " + XDate.getDateNow(XDate.MMDDYYHHMM) + "\n\n");
               autoRunTask.startTasks(resultData);
               resultData.log("\n\nCompleted AutoRunTaskId=\"" + autoRunTaskId + "\" - " + XDate.getDateNow(XDate.MMDDYYHHMM) + "\n");
            }

            // Email successful run
            String subject = "Completed AutoRunTaskId=\"" + autoRunTaskId + "\"";
            XResultPage page =
                  resultData.getReport("AutoRunTaskId=\"" + autoRunTaskId + "\"", Manipulations.ALL,
                        Manipulations.ERROR_WARNING_HEADER);
            String htmlBody = page.getManipulatedHtml();
            AEmail emailMessage =
                  new AEmail(autoRunTask.getNotificationEmailAddresses(),
                        SkynetAuthentication.getInstance().getAuthenticatedUser().getEmail(),
                        SkynetAuthentication.getInstance().getAuthenticatedUser().getEmail(), subject);
            emailMessage.setSubject(subject);
            emailMessage.addHTMLBody(htmlBody);
            emailMessage.send();
         }
      } catch (Exception ex) {
         String[] emails = new String[] {"donald.g.dunne@boeing.com"};
         if (autoRunTask != null) emails = autoRunTask.getNotificationEmailAddresses();
         // Email exception
         String subject = "Exception running AutoRunTaskId=\"" + autoRunTaskId + "\" Exceptioned";
         XResultPage page =
               resultData.getReport("AutoRunTaskId=\"" + autoRunTaskId + "\"", Manipulations.ALL,
                     Manipulations.ERROR_WARNING_HEADER);
         String htmlBody = "Output:\n\n" + page.getManipulatedHtml() + "\n\nException:\n\n" + Lib.exceptionToString(ex);
         AEmail emailMessage =
               new AEmail(autoRunTask.getNotificationEmailAddresses(),
                     SkynetAuthentication.getInstance().getAuthenticatedUser().getEmail(),
                     SkynetAuthentication.getInstance().getAuthenticatedUser().getEmail(), subject);
         try {
            emailMessage.setSubject(subject);
            emailMessage.addHTMLBody(htmlBody);
            emailMessage.send();
         } catch (Exception ex2) {
            OSEELog.logException(SkynetGuiPlugin.class, ex2, false);
         }
      } finally {
         if (autoRunTaskId != null) {
            logger.log(Level.INFO, "Sleeping...");
            try {
               Thread.sleep(2000);
            } catch (Exception ex) {
               // do nothing
            }
            logger.log(Level.INFO, "Exiting AutoRunStartup; Id=\"" + autoRunTaskId + "\"");
            Displays.ensureInDisplayThread(new Runnable() {
               /* (non-Javadoc)
                * @see java.lang.Runnable#run()
                */
               public void run() {
                  PlatformUI.getWorkbench().close();
               }
            });
         }
      }
   }

   public static Result validateAutoRunExecution(IAutoRunTask autoRunTask) {
      if (DatabaseActivator.getInstance().isProductionDb()) {
         if (autoRunTask.getRunDb() == RunDb.Test_Db) return new Result("Task should only be run on Test DB");
      } else {
         if (autoRunTask.getRunDb() == RunDb.Production_Db) return new Result(
               "Task should only be run on Production DB");
      }
      return Result.TrueResult;
   }

   /**
    * Execute the autoRun.startTasks with the given unique extension id
    * 
    * @param autoRunTaskId unique AutoRunTask extension id
    * @throws Exception
    */
   private IAutoRunTask getAutoRunTask(String autoRunTaskId) throws Exception {
      List<IExtension> iExtensions =
            ExtensionPoints.getExtensionsByUniqueId(EXTENSION_POINT, Arrays.asList(new String[] {autoRunTaskId}));
      for (IExtension iExtension : iExtensions) {
         for (IConfigurationElement element : iExtension.getConfigurationElements()) {
            String className = element.getAttribute("classname");
            String bundleName = element.getContributor().getName();
            if (className != null && bundleName != null) {
               Bundle bundle = Platform.getBundle(bundleName);
               Class<?> interfaceClass = bundle.loadClass(className);
               return (IAutoRunTask) interfaceClass.getConstructor().newInstance();
            }
         }
      }
      return null;
   }

   public static List<IAutoRunTask> getAutoRunTasks() throws Exception {
      List<IAutoRunTask> tasks = new ArrayList<IAutoRunTask>();
      List<IConfigurationElement> iExtensions = ExtensionPoints.getExtensionElements(EXTENSION_POINT, "AutoRunTask");
      for (IConfigurationElement element : iExtensions) {
         String className = element.getAttribute("classname");
         String bundleName = element.getContributor().getName();
         try {
            if (className != null && bundleName != null) {
               Bundle bundle = Platform.getBundle(bundleName);
               Class<?> interfaceClass = bundle.loadClass(className);
               IAutoRunTask autoRunTask = (IAutoRunTask) interfaceClass.getConstructor().newInstance();
               autoRunTask.setAutoRunUniqueId(element.getDeclaringExtension().getUniqueIdentifier());
               tasks.add(autoRunTask);
            }
         } catch (Exception ex) {
            OSEELog.logException(SkynetGuiPlugin.class, ex, false);
         }
      }
      return tasks;
   }

}