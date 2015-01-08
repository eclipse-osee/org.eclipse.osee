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

import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.api.agile.AgileTeamEndpointApi;
import org.eclipse.osee.ats.api.agile.NewAgileTeam;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsJaxRsService;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
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
      super(parent, "Create new Agile Team", AtsImage.AGILE_TEAM);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws OseeCoreException {
      EntryDialog ed = new EntryDialog("Create New Agile Team", "Enter new Agile Team name");
      if (ed.open() == 0) {
         if (Strings.isValid(ed.getEntry())) {
            try {
               AgileTeamEndpointApi teamApi = AtsJaxRsService.get().getAgileTeam();
               NewAgileTeam newTeam = new NewAgileTeam();
               newTeam.setName(ed.getEntry());
               NewAgileTeam team = teamApi.createTeam(newTeam);
               AtsUtil.openArtifact(team.getGuid(), OseeCmEditor.CmPcrEditor);
            } catch (Exception ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      }
   }
}
