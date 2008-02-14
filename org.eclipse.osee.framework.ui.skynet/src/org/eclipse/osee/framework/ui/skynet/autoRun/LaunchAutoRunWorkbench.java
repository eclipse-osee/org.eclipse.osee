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

import java.io.File;
import org.eclipse.core.resources.IFile;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.action.Action;
import org.eclipse.osee.framework.ui.plugin.util.AWorkspace;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.autoRun.IAutoRunTask.RunDb;

/**
 * This Action (kicked off from Artifact Explorer pulldown toolbar menu) will kickoff an event task that will in turn
 * kickoff instances of OSEE to run certain tests. Each individual instance of OSEE will run the AutoRunStartup.java
 * class which will in turn, through extension points, run whatever test was requested to run.
 * 
 * @author Donald G. Dunne
 */
public class LaunchAutoRunWorkbench extends Action {

   public static Result launch(IAutoRunTask autoRunTask, String defaultDbConnection) throws Exception {
      if (autoRunTask.getRunDb() != RunDb.Production_Db && defaultDbConnection.equals("oracle7")) {
         throw new IllegalArgumentException("Can't run non-production task on production.");
      }
      return launch(autoRunTask.getAutoRunUniqueId(), defaultDbConnection);
   }

   public static Result launch(String autoRunExtensionUniqueId, String defaultDbConnection) throws Exception {
      ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
      String launchFile = "org.eclipse.osee.framework.ui.skynet\\AutoRun.launch";
      File file = AWorkspace.getWorkspaceFile(launchFile);
      if (!file.exists()) {
         throw new IllegalArgumentException("Can't locate file \"" + launchFile + "\"");
      }
      IFile iFile = AWorkspace.fileToIFile(file);
      if (iFile == null || !iFile.exists()) {
         throw new IllegalArgumentException("Can't locate file \"" + launchFile + "\"");
      }
      ILaunchConfiguration config = manager.getLaunchConfiguration(iFile);
      //         System.out.println("Pre Config " + config.getAttributes());
      // Get a copy of the config to work with
      ILaunchConfigurationWorkingCopy copy = config.getWorkingCopy();
      // Add the AutoRun property to the VM_ARGUEMENTS
      copy.setAttribute("org.eclipse.jdt.launching.VM_ARGUMENTS", copy.getAttribute(
            "org.eclipse.jdt.launching.VM_ARGUMENTS", "").replaceFirst("PUT_AUTORUN_ID_HERE", autoRunExtensionUniqueId));
      copy.setAttribute("org.eclipse.jdt.launching.VM_ARGUMENTS", copy.getAttribute(
            "org.eclipse.jdt.launching.VM_ARGUMENTS", "").replaceFirst("PUT_DB_CONNECTION_HERE", defaultDbConnection));
      copy.setAttribute("location", copy.getAttribute("location", "").replaceFirst("PUT_ID_HERE",
            autoRunExtensionUniqueId.replaceAll("\\.", "")));
      System.out.println("Launching: " + copy.getAttributes());
      // Launch with the updated config
      System.err.println("Change back to RUN_MODE");
      copy.launch(ILaunchManager.DEBUG_MODE, null);
      return Result.TrueResult;
   }
}
