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

import javax.ws.rs.core.Response;
import org.eclipse.osee.ats.AtsArtifactImageProvider;
import org.eclipse.osee.ats.api.agile.AgileEndpointApi;
import org.eclipse.osee.ats.api.agile.JaxAgileTeam;
import org.eclipse.osee.ats.api.agile.JaxNewAgileTeam;
import org.eclipse.osee.ats.api.data.AtsArtifactImages;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.AtsEditors;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.cm.OseeCmEditor;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;

/**
 * @author Donald G. Dunne
 */
public class CreateNewAgileTeam extends XNavigateItemAction {

   public CreateNewAgileTeam(XNavigateItem parent) {
      super(parent, "Create new Agile Team", AtsArtifactImageProvider.getKeyedImage(AtsArtifactImages.AGILE_TEAM));
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      EntryDialog ed = new EntryDialog(getName(), "Enter new Agile Team name");
      if (ed.open() == 0) {
         if (Strings.isValid(ed.getEntry())) {
            try {
               AgileEndpointApi agileEp = AtsClientService.getAgileEndpoint();
               JaxNewAgileTeam newTeam = new JaxNewAgileTeam();
               newTeam.setName(ed.getEntry());
               Response response = agileEp.createTeam(newTeam);
               Object entity = null;
               if (response != null) {
                  entity = response.readEntity(JaxAgileTeam.class);
               }
               if (entity != null) {
                  JaxAgileTeam team = (JaxAgileTeam) entity;
                  Artifact teamArt =
                     ArtifactQuery.getArtifactFromId(team.getId(), AtsClientService.get().getAtsBranch());
                  teamArt.getParent().reloadAttributesAndRelations();
                  AtsEditors.openArtifact(teamArt, OseeCmEditor.CmPcrEditor);
               } else {
                  AWorkbench.popup("Error Creating Team", response != null ? response.toString() : "");
               }
            } catch (Exception ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      }
   }
}
