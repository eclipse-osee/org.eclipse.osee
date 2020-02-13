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
package org.eclipse.osee.ats.ide.agile.navigate;

import java.util.LinkedList;
import java.util.List;
import javax.ws.rs.core.Response;
import org.eclipse.osee.ats.api.agile.AgileEndpointApi;
import org.eclipse.osee.ats.api.agile.JaxAgileBacklog;
import org.eclipse.osee.ats.api.agile.JaxNewAgileBacklog;
import org.eclipse.osee.ats.api.data.AtsArtifactImages;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.ide.AtsArtifactImageProvider;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
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
public class CreateNewAgileBacklog extends XNavigateItemAction {

   public CreateNewAgileBacklog(XNavigateItem parent) {
      super(parent, "Create new Agile Backlog",
         AtsArtifactImageProvider.getKeyedImage(AtsArtifactImages.AGILE_BACKLOG));
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

         EntryDialog ed = new EntryDialog(getName(), "Enter new Agile Backlog name");
         if (ed.open() == 0) {
            if (Strings.isValid(ed.getEntry())) {
               try {
                  AgileEndpointApi agileEp = AtsClientService.get().getServerEndpoints().getAgileEndpoint();
                  JaxNewAgileBacklog newBacklog = new JaxNewAgileBacklog();
                  newBacklog.setName(ed.getEntry());
                  Artifact firstArtifact = dialog.getSelectedFirst();
                  if (firstArtifact == null) {
                     throw new OseeCoreException("Must make a selection");
                  }
                  int teamId = firstArtifact.getArtId();

                  newBacklog.setTeamId(teamId);
                  Response response = agileEp.createBacklog(new Long(teamId), newBacklog);
                  Object entity = null;
                  if (response != null) {
                     entity = response.readEntity(JaxAgileBacklog.class);
                  }
                  if (entity != null) {
                     JaxAgileBacklog backlog = (JaxAgileBacklog) entity;
                     Artifact backlogart =
                        ArtifactQuery.getArtifactFromId(backlog.getId(), AtsClientService.get().getAtsBranch());
                     backlogart.getParent().reloadAttributesAndRelations();
                     AtsEditors.openArtifactById(ArtifactId.valueOf(backlog.getId()), OseeCmEditor.CmPcrEditor);
                  } else {
                     AWorkbench.popup("Error creating Agile Backlog [%s]", response != null ? response.toString() : "");
                  }
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         }
      }
   }
}
