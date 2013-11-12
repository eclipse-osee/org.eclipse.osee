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
package org.eclipse.osee.ats.client.demo.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.client.demo.DemoUsers;
import org.eclipse.osee.ats.client.demo.internal.Activator;
import org.eclipse.osee.ats.client.demo.internal.AtsClientService;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.util.AtsChangeSet;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class DemoDbTasks {

   public static void createTasks(boolean DEBUG) throws Exception {
      if (DEBUG) {
         OseeLog.log(Activator.class, Level.INFO, "Create tasks off code workflows");
      }
      Date createdDate = new Date();
      IAtsUser createdBy = AtsClientService.get().getUserAdmin().getCurrentUser();
      AtsChangeSet changes = new AtsChangeSet("Populate Demo DB - Create Tasks");
      boolean firstTaskWorkflow = true;
      for (TeamWorkFlowArtifact codeArt : DemoDbUtil.getSampleCodeWorkflows()) {
         List<IAtsUser> demoUsers = new ArrayList<IAtsUser>();
         if (firstTaskWorkflow) {
            demoUsers.add(AtsClientService.get().getUserAdmin().getUserFromOseeUser(
               DemoDbUtil.getDemoUser(DemoUsers.Joe_Smith)));
            demoUsers.add(AtsClientService.get().getUserAdmin().getUserFromOseeUser(
               DemoDbUtil.getDemoUser(DemoUsers.Kay_Jones)));
         } else {
            demoUsers.add(AtsClientService.get().getUserAdmin().getUserFromOseeUser(
               DemoDbUtil.getDemoUser(DemoUsers.Joe_Smith)));
         }
         for (String title : getTaskTitles(firstTaskWorkflow)) {
            codeArt.createNewTask(demoUsers, title, createdDate, createdBy, codeArt.getCurrentStateName(), changes);
         }
         firstTaskWorkflow = false;
         changes.add(codeArt);
      }
      changes.execute();
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
