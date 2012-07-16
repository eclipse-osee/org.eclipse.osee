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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.config.demo.internal.Activator;
import org.eclipse.osee.ats.core.client.task.TaskArtifact;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.util.AtsUsersClient;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.support.test.util.DemoUsers;

/**
 * @author Donald G. Dunne
 */
public class DemoDbTasks {

   public static void createTasks(boolean DEBUG) throws Exception {
      if (DEBUG) {
         OseeLog.log(Activator.class, Level.INFO, "Create tasks off code workflows");
      }
      Date createdDate = new Date();
      IAtsUser createdBy = AtsUsersClient.getUser();
      SkynetTransaction transaction =
         TransactionManager.createTransaction(AtsUtil.getAtsBranch(), "Populate Demo DB - Create Tasks");
      boolean firstTaskWorkflow = true;
      for (TeamWorkFlowArtifact codeArt : DemoDbUtil.getSampleCodeWorkflows()) {
         List<IAtsUser> demoUsers = new ArrayList<IAtsUser>();
         if (firstTaskWorkflow) {
            demoUsers.add(AtsUsersClient.getUserFromOseeUser(DemoDbUtil.getDemoUser(DemoUsers.Joe_Smith)));
            demoUsers.add(AtsUsersClient.getUserFromOseeUser(DemoDbUtil.getDemoUser(DemoUsers.Kay_Jones)));
         } else {
            demoUsers.add(AtsUsersClient.getUserFromOseeUser(DemoDbUtil.getDemoUser(DemoUsers.Joe_Smith)));
         }
         for (String title : getTaskTitles(firstTaskWorkflow)) {
            TaskArtifact taskArt = codeArt.createNewTask(demoUsers, title, createdDate, createdBy);
            taskArt.persist(transaction);
         }
         firstTaskWorkflow = false;
      }
      transaction.execute();
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
      } else {
         return Arrays.asList("Document how Graph View works", "Update help contents", "Review new documentation",
            "Publish documentation to website", "Remove old viewer", "Deploy release");
      }
   }

   public static int getNumTasks() {
      return getTaskTitles(false).size() + getTaskTitles(true).size();
   }

}
