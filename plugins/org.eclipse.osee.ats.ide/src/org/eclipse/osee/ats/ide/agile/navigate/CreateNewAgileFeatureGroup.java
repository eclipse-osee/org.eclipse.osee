/*********************************************************************
 * Copyright (c) 2015 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.agile.navigate;

import java.util.LinkedList;
import java.util.List;
import javax.ws.rs.core.Response;
import org.eclipse.osee.ats.api.agile.AgileEndpointApi;
import org.eclipse.osee.ats.api.agile.JaxAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.JaxNewAgileFeatureGroup;
import org.eclipse.osee.ats.api.data.AtsArtifactImages;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.ide.AtsArtifactImageProvider;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsEditors;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.ArtifactLabelProvider;
import org.eclipse.osee.framework.ui.skynet.cm.OseeCmEditor;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.ArtifactTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTreeArtifactDialog;

/**
 * @author Donald G. Dunne
 */
public class CreateNewAgileFeatureGroup extends XNavigateItemAction {

   public CreateNewAgileFeatureGroup() {
      super("Create new Agile Feature Group",
         AtsArtifactImageProvider.getKeyedImage(AtsArtifactImages.AGILE_FEATURE_GROUP),
         AgileNavigateItemProvider.AGILE_CONFIG);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {

      List<Artifact> activeTeams = new LinkedList<>();
      for (Artifact agTeam : ArtifactQuery.getArtifactListFromType(AtsArtifactTypes.AgileTeam,
         AtsApiService.get().getAtsBranch())) {
         if (agTeam.getSoleAttributeValue(AtsAttributeTypes.Active, true)) {
            activeTeams.add(agTeam);
         }
      }
      FilteredTreeArtifactDialog dialog = new FilteredTreeArtifactDialog(getName(), "Select Agile Team", activeTeams,
         new ArtifactTreeContentProvider(), new ArtifactLabelProvider());
      if (dialog.open() == 0) {

         EntryDialog ed = new EntryDialog(getName(), "Enter new Agile Feature Group name(s) (comma delimited)");
         if (ed.open() == 0) {
            if (Strings.isValid(ed.getEntry())) {
               try {
                  AgileEndpointApi teamApi = AtsApiService.get().getServerEndpoints().getAgileEndpoint();
                  JaxNewAgileFeatureGroup newGroup = new JaxNewAgileFeatureGroup();
                  for (String name : ed.getEntry().split(",")) {
                     newGroup.setName(name);
                     Artifact firstArtifact = dialog.getSelectedFirst();
                     if (firstArtifact == null) {
                        throw new OseeCoreException("Must make a selection");
                     }
                     int teamId = firstArtifact.getArtId();

                     newGroup.setTeamId(teamId);
                     Response response = teamApi.createFeatureGroup(new Long(teamId), newGroup);
                     Object entity = null;
                     if (response != null) {
                        entity = response.readEntity(JaxAgileFeatureGroup.class);
                     }
                     if (entity != null) {
                        JaxAgileFeatureGroup group = (JaxAgileFeatureGroup) entity;
                        Artifact groupArt =
                           ArtifactQuery.getArtifactFromId(group.getId(), AtsApiService.get().getAtsBranch());
                        groupArt.getParent().reloadAttributesAndRelations();
                        AtsEditors.openArtifactById(ArtifactId.valueOf(group.getId()), OseeCmEditor.CmPcrEditor);
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
