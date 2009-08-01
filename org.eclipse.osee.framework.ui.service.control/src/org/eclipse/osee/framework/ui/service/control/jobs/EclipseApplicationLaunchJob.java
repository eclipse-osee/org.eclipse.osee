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
package org.eclipse.osee.framework.ui.service.control.jobs;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.service.control.ControlPlugin;
import org.eclipse.osee.framework.ui.service.control.wizards.launcher.ServiceLaunchDataPersist;
import org.eclipse.osee.framework.ui.service.control.wizards.launcher.ServiceLaunchingInformation;
import org.eclipse.osee.framework.ui.service.control.wizards.launcher.data.ServiceItem;
import org.eclipse.swt.SWT;

/**
 * @author Roberto E. Escobar
 */
public class EclipseApplicationLaunchJob extends Job {
   private TextDisplayHelper display;
   private ServiceLaunchingInformation serviceInfo;
   private String javaCompiler;
   private File latestPlugin;

   public EclipseApplicationLaunchJob(String name, String javaCompiler, File latestPlugin, ServiceLaunchingInformation serviceInfo, TextDisplayHelper display) {
      super(name);
      this.javaCompiler = javaCompiler;
      this.serviceInfo = serviceInfo;
      this.display = display;
      this.latestPlugin = latestPlugin;
   }

   @Override
   protected IStatus run(IProgressMonitor monitor) {
      IStatus toReturn = Status.OK_STATUS;
      try {
         ServiceLaunchDataPersist data = ServiceLaunchDataPersist.getInstance();
         data.saveLastServiceLaunched(serviceInfo.getServiceItem().getName());

         if (latestPlugin != null) {
            String compilerExec = javaCompiler.trim();
            if (compilerExec.endsWith(".exe")) {
               compilerExec = compilerExec.substring(0, compilerExec.lastIndexOf(".exe"));
            }

            String exec = serviceInfo.getServiceItem().getLocalExecution();
            if (Lib.isWindows()) {
               compilerExec = "\"" + compilerExec + "\"";
               exec = exec.replaceAll("-?nohup" + ServiceItem.EXEC_SEPARATOR + "?", "");
            }
            exec = exec.replace("java", compilerExec);

            exec = exec.replaceAll(ServiceItem.EXEC_SEPARATOR + ServiceItem.EXEC_SEPARATOR, ServiceItem.EXEC_SEPARATOR);

            OseeLog.log(ControlPlugin.class, Level.INFO, "Local Launch: " + exec);

            exec += ServiceItem.EXEC_SEPARATOR + "-debug";

            display.addText("\n\t\t ----------- Execute -------------\n\n", SWT.BOLD, SWT.COLOR_BLACK, false);
            String temp = exec.replaceAll(ServiceItem.EXEC_SEPARATOR, " ");
            display.addText("\tExecuting:", SWT.BOLD, SWT.COLOR_DARK_BLUE, true);
            String tag = "-application";
            display.addText("\t" + temp.substring(temp.lastIndexOf(tag) + tag.length(), temp.length()) + "\n\n",
                  SWT.BOLD, SWT.COLOR_BLACK, false);

            display.addText("\tRaw Command:", SWT.BOLD, SWT.COLOR_DARK_BLUE, true);
            display.addText("\t" + exec.replaceAll(ServiceItem.EXEC_SEPARATOR, " ") + "\n\n", SWT.BOLD,
                  SWT.COLOR_BLACK, false);

            File workingDir = new File(System.getProperty("user.home") + File.separator + "oseeservices");
            workingDir.mkdirs();

            ProcessBuilder runner = new ProcessBuilder();
            runner.directory(workingDir);
            runner.command(exec.split(ServiceItem.EXEC_SEPARATOR));
            Process process = runner.start();

            display.startProcessHandling(process);
         } else {
            display.addText("\n" + serviceInfo.getServiceItem().getPlugin() + " could not be found." + "\n\n",
                  SWT.NORMAL, SWT.COLOR_RED, false);
            toReturn = Status.CANCEL_STATUS;
         }
      } catch (IOException ex) {
         display.addText("\n" + ControlPlugin.getStackMessages(ex) + "\n\n", SWT.NORMAL, SWT.COLOR_RED, false);
         toReturn = Status.CANCEL_STATUS;
      }
      return toReturn;
   }
}
