/*
 * Created on May 15, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.config.demo.config;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.config.demo.OseeAtsConfigDemoPlugin;
import org.eclipse.osee.ats.config.demo.util.DemoUsers;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Donald G. Dunne
 */
public class DemoDbTasks {

   public static void createTasks() throws Exception {
      OSEELog.logInfo(OseeAtsConfigDemoPlugin.class, "Create tasks off code workflows", false);
      boolean firstTaskWorkflow = true;
      for (TeamWorkFlowArtifact codeArt : DemoDbUtil.getSampleCodeWorkflows()) {
         for (String title : getTaskTitles(firstTaskWorkflow)) {
            codeArt.getSmaMgr().getTaskMgr().createNewTask(
                  (firstTaskWorkflow ? Arrays.asList(DemoUsers.getDemoUser(DemoUsers.Joe_Smith),
                        DemoUsers.getDemoUser(DemoUsers.Kay_Jones)) : Arrays.asList(DemoUsers.getDemoUser(DemoUsers.Joe_Smith))),
                  title, true);
         }
         firstTaskWorkflow = false;
      }
   }

   /**
    * Return different set of task titles for first and second workflow that make request
    * 
    * @return
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
