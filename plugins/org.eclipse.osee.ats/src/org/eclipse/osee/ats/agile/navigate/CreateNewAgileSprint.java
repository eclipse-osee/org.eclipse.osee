/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.agile.navigate;

import java.util.LinkedList;
import java.util.List;
import javax.ws.rs.core.Response;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.api.agile.AgileEndpointApi;
import org.eclipse.osee.ats.api.agile.JaxAgileSprint;
import org.eclipse.osee.ats.api.agile.JaxNewAgileSprint;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.ArtifactLabelProvider;
import org.eclipse.osee.framework.ui.skynet.cm.OseeCmEditor;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.ArtifactTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTreeArtifactDialog;

/**
 * @author Donald G. Dunne
 */
public class CreateNewAgileSprint extends XNavigateItemAction {

   public CreateNewAgileSprint(XNavigateItem parent) {
      super(parent, "Create new Agile Sprint", AtsImage.AGILE_SPRINT);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {

      List<Artifact> activeTeams = new LinkedList<>();
      for (Artifact agTeam : ArtifactQuery.getArtifactListFromType(AtsArtifactTypes.AgileTeam,
         AtsClientService.get().getAtsBranch())) {
         if (agTeam.getSoleAttributeValue(AtsAttributeTypes.Active, true)) {
            activeTeams.add(agTeam);
         }
      }
      FilteredTreeArtifactDialog dialog = new FilteredTreeArtifactDialog(getName(), "Select Agile Team", activeTeams,
         new ArtifactTreeContentProvider(), new ArtifactLabelProvider());
      if (dialog.open() == 0) {

         EntryDialog ed = new EntryDialog(getName(), "Enter new Agile Sprint name(s) (comma delimited)");
         if (ed.open() == 0) {
            if (Strings.isValid(ed.getEntry())) {
               try {
                  AgileEndpointApi ageilEp = AtsClientService.getAgileEndpoint();
                  JaxNewAgileSprint newSprint = new JaxNewAgileSprint();
                  Artifact firstArtifact = (Artifact) dialog.getSelectedFirst();

                  if (firstArtifact == null) {
                     throw new OseeCoreException("Must make a selection");
                  }
                  int teamId = (firstArtifact).getArtId();

                  for (String name : ed.getEntry().split(",")) {
                     newSprint.setName(name);
                     newSprint.setTeamId(teamId);
                     Response response = ageilEp.createSprint(new Long(teamId), newSprint);
                     JaxAgileSprint sprint = null;
                     if (response != null) {
                        sprint = response.readEntity(JaxAgileSprint.class);
                     }
                     if (sprint != null) {
                        long id = sprint.getId();
                        Artifact sprintArt = ArtifactQuery.getArtifactFromId(id, AtsClientService.get().getAtsBranch());
                        sprintArt.getParent().reloadAttributesAndRelations();
                        AtsUtil.openArtifact(sprintArt.getGuid(), OseeCmEditor.CmPcrEditor);
                     } else {
                        AWorkbench.popup("Error creating Agile Team [%s]", response != null ? response.toString() : "");
                        return;
                     }
                  }
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         }
      }
   }
}
