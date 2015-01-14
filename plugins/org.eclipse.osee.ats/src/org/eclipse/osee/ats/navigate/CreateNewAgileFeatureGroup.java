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
package org.eclipse.osee.ats.navigate;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.api.agile.AgileTeamEndpointApi;
import org.eclipse.osee.ats.api.agile.NewAgileFeatureGroup;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsJaxRsService;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
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
public class CreateNewAgileFeatureGroup extends XNavigateItemAction {

   public CreateNewAgileFeatureGroup(XNavigateItem parent) {
      super(parent, "Create new Agile Feature Group", AtsImage.AGILE_TEAM);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws OseeCoreException {

      List<Artifact> activeTeams = new LinkedList<Artifact>();
      for (Artifact agTeam : ArtifactQuery.getArtifactListFromType(AtsArtifactTypes.AgileTeam,
         AtsUtilCore.getAtsBranch())) {
         if (agTeam.getSoleAttributeValue(AtsAttributeTypes.Active, true)) {
            activeTeams.add(agTeam);
         }
      }
      FilteredTreeArtifactDialog dialog =
         new FilteredTreeArtifactDialog(getName(), "Select Agile Team", activeTeams, new ArtifactTreeContentProvider(),
            new ArtifactLabelProvider());
      if (dialog.open() == 0) {

         EntryDialog ed = new EntryDialog(getName(), "Enter new Agile Feature Group name");
         if (ed.open() == 0) {
            if (Strings.isValid(ed.getEntry())) {
               try {
                  AgileTeamEndpointApi teamApi = AtsJaxRsService.get().getAgileTeam();
                  NewAgileFeatureGroup newGroup = new NewAgileFeatureGroup();
                  newGroup.setName(ed.getEntry());
                  newGroup.setTeamUuid(((Artifact) dialog.getSelectedFirst()).getArtId());
                  NewAgileFeatureGroup team =
                     teamApi.createFeatureGroup(((Artifact) dialog.getSelectedFirst()).getArtId(), newGroup);
                  AtsUtil.openArtifact(team.getGuid(), OseeCmEditor.CmPcrEditor);
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         }
      }
   }
}
