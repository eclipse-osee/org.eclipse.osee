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
package org.eclipse.osee.ats.config.demo.config;

import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.config.demo.internal.OseeAtsConfigDemoActivator;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.support.test.util.DemoUsers;

/**
 * @author Donald G. Dunne
 */
public class DemoDbTasks {

   public static void createTasks() throws Exception {
      OseeLog.log(OseeAtsConfigDemoActivator.class, Level.INFO, "Create tasks off code workflows");
      boolean firstTaskWorkflow = true;
      for (TeamWorkFlowArtifact codeArt : DemoDbUtil.getSampleCodeWorkflows()) {
         for (String title : getTaskTitles(firstTaskWorkflow)) {
            TaskArtifact taskArt =
                  codeArt.getSmaMgr().getTaskMgr().createNewTask(
                        (firstTaskWorkflow ? Arrays.asList(DemoDbUtil.getDemoUser(DemoUsers.Joe_Smith),
                              DemoDbUtil.getDemoUser(DemoUsers.Kay_Jones)) : Arrays.asList(DemoDbUtil.getDemoUser(DemoUsers.Joe_Smith))),
                        title);
            taskArt.persist();
         }
         firstTaskWorkflow = false;
      }
   }

   /**
    * Return different set of task titles for first and second workflow that make request
    */
   public static Collection<String> getTaskTitles(boolean firstTaskWorkflow) {
      if (firstTaskWorkflow) {
         firstTaskWorkflow = false;
         return Arrays.asList("Look into Graph View.", "Redesign how view shows values.",
               "Discuss new design with Senior Engineer", "Develop prototype", "Show prototype to management",
               "Create development plan", "Create test plan", "Make changes");
      } else
         return Arrays.asList("Document how Graph View works", "Update help contents", "Review new documentation",
               "Publish documentation to website", "Remove old viewer", "Deploy release");
   }

   public static int getNumTasks() {
      return getTaskTitles(false).size() + getTaskTitles(true).size();
   }

}
