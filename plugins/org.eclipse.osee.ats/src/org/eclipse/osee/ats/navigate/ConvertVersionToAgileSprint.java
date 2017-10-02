/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.navigate;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.ws.rs.core.Response;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.api.agile.AgileEndpointApi;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.agile.JaxAgileSprint;
import org.eclipse.osee.ats.api.agile.JaxNewAgileSprint;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.core.client.IAtsClient;
import org.eclipse.osee.ats.core.client.artifact.SprintArtifact;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.widgets.dialog.AtsConfigCheckTreeDialog;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;

/**
 * @author Donald G. Dunne
 */
public class ConvertVersionToAgileSprint extends XNavigateItemAction {

   public ConvertVersionToAgileSprint(XNavigateItem parent) {
      super(parent, "Convert Version(s) to Agile Sprint", AtsImage.AGILE_SPRINT);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {

      List<IAgileTeam> activeAgileTeams = new LinkedList<>();
      IAtsClient client = AtsClientService.get();
      activeAgileTeams.addAll(
         client.getQueryService().createQuery(AtsArtifactTypes.AgileTeam).andActive(true).getConfigObjects());

      AtsConfigCheckTreeDialog<IAgileTeam> teamDialog =
         new AtsConfigCheckTreeDialog<IAgileTeam>(getName(), "Select Agile Team", activeAgileTeams, true);
      IAgileTeam agileTeam = null;
      if (teamDialog.open() != 0) {
         return;
      }
      agileTeam = teamDialog.getChecked().iterator().next();

      Collection<IAtsTeamDefinition> teamDefs = client.getTeamDefinitionService().getTeamDefinitions(agileTeam);
      if (teamDefs.isEmpty()) {
         AWorkbench.popup("No Team Definitions configured for Agile Team " + agileTeam);
         return;
      }
      IAtsTeamDefinition teamDefHoldingVersions =
         client.getTeamDefinitionService().getTeamDefHoldingVersions(teamDefs.iterator().next());
      Collection<IAtsVersion> versions = client.getVersionService().getVersions(teamDefHoldingVersions);

      AtsConfigCheckTreeDialog<IAtsVersion> versionDialog =
         new AtsConfigCheckTreeDialog<IAtsVersion>(getName(), "Select Version(s)", versions, true);
      Collection<IAtsVersion> selectedVersions = null;
      if (versionDialog.open() != 0) {
         return;
      }
      selectedVersions = versionDialog.getChecked();

      String newTitle = null;
      boolean useVersionTitle = true;
      if (selectedVersions.size() == 1) {
         EntryDialog ed = new EntryDialog(getName(), "Enter new Agile Sprint name(s) (comma delimited)");
         ed.setEntry(selectedVersions.iterator().next().getName());
         if (ed.open() == 0) {
            if (Strings.isValid(ed.getEntry())) {
               newTitle = ed.getEntry();
               useVersionTitle = false;
            }
         }
      }
      try {
         AgileEndpointApi ageilEp = AtsClientService.getAgileEndpoint();
         JaxNewAgileSprint newSprint = new JaxNewAgileSprint();
         long teamUuid = agileTeam.getId();

         List<IAgileSprint> sprints = new LinkedList<>();
         for (IAtsVersion version : selectedVersions) {
            newSprint.setName(useVersionTitle ? version.getName() : newTitle);
            newSprint.setTeamUuid(teamUuid);
            Response response = ageilEp.createSprint(new Long(teamUuid), newSprint);
            JaxAgileSprint jaxSprint = null;
            if (response != null) {
               jaxSprint = response.readEntity(JaxAgileSprint.class);
            }
            if (jaxSprint != null) {
               long uuid = jaxSprint.getUuid();
               IAgileSprint sprint = (SprintArtifact) client.getArtifact(uuid);
               ((Artifact) sprint.getStoreObject()).getParent().reloadAttributesAndRelations();

               IAtsChangeSet changes =
                  client.getStoreService().createAtsChangeSet(getName(), client.getUserService().getCurrentUser());
               Collection<ArtifactToken> teamWfs = client.getRelationResolver().getRelated(version,
                  AtsRelationTypes.TeamWorkflowTargetedForVersion_Workflow);
               for (ArtifactId teamWf : teamWfs) {
                  changes.relate(sprint, AtsRelationTypes.AgileSprintToItem_AtsItem, teamWf);
               }
               changes.executeIfNeeded();

            } else {
               AWorkbench.popup("Error creating Agile Team [%s]", response != null ? response.toString() : "");
               return;
            }
         }
         AtsUtil.openInAtsWorldEditor(sprints, getName());
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }
}
