/*
 * Created on May 15, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.config.demo.config;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.config.demo.OseeAtsConfigDemoPlugin;
import org.eclipse.osee.ats.config.demo.artifact.DemoCodeTeamWorkflowArtifact;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.UniversalGroup;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;

/**
 * @author Donald G. Dunne
 */
public class DemoDbGroups {
   public static String TEST_GROUP_NAME = "Test Group";

   public static List<TeamWorkFlowArtifact> createGroups() throws Exception {

      // Create group of all resulting objects
      List<TeamWorkFlowArtifact> codeWorkflows = new ArrayList<TeamWorkFlowArtifact>();
      OseeLog.log(OseeAtsConfigDemoPlugin.class, Level.INFO,  "Create Groups and add objects");
      Artifact groupArt = UniversalGroup.addGroup(TEST_GROUP_NAME, AtsPlugin.getAtsBranch());
      for (DemoCodeTeamWorkflowArtifact codeArt : DemoDbUtil.getSampleCodeWorkflows()) {

         // Add Action to Universal Group
         groupArt.addRelation(CoreRelationEnumeration.UNIVERSAL_GROUPING__MEMBERS, codeArt.getParentActionArtifact());

         // Add All Team Workflows to Universal Group
         for (Artifact teamWorkflow : codeArt.getParentActionArtifact().getTeamWorkFlowArtifacts()) {
            groupArt.addRelation(CoreRelationEnumeration.UNIVERSAL_GROUPING__MEMBERS, teamWorkflow);
         }

         codeArt.persistAttributesAndRelations();
      }

      // Add all Tasks to Group
      for (Artifact task : ArtifactQuery.getArtifactsFromType(TaskArtifact.ARTIFACT_NAME, AtsPlugin.getAtsBranch())) {
         groupArt.addRelation(CoreRelationEnumeration.UNIVERSAL_GROUPING__MEMBERS, task);
      }
      groupArt.persistRelations();

      return codeWorkflows;
   }
}
