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
package org.eclipse.osee.ats.workdef.config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.core.workdef.WorkDefinitionSheet;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.workdef.AtsWorkDefinitionSheetProviders;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;

/**
 * @author Donald G. Dunne
 */
public class ImportWorkDefinitionsItem extends XNavigateItemAction {

   public ImportWorkDefinitionsItem(XNavigateItem parent) {
      super(parent, "Import Work Definitions to DB", AtsImage.WORK_DEFINITION);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      List<WorkDefinitionSheet> importSheets = new ArrayList<>();
      EntryDialog dbDialog = new EntryDialog(getName(), "Enter DB Type");
      if (dbDialog.open() == 0) {
         for (WorkDefinitionSheet sheet : AtsWorkDefinitionSheetProviders.getWorkDefinitionSheets(
            dbDialog.getEntry())) {
            if (!sheet.getName().endsWith("AIs_And_Teams")) {
               importSheets.add(sheet);
            }
         }
         WorkDefinitionCheckTreeDialog dialog =
            new WorkDefinitionCheckTreeDialog(getName(), "Select Work Definition Sheet(s) to import", importSheets);
         if (dialog.open() == 0) {
            XResultData resultData = new XResultData(false);
            IAtsChangeSet changes = AtsClientService.get().createChangeSet(getName());
            Artifact folder = OseeSystemArtifacts.getOrCreateArtifact(AtsArtifactToken.WorkDefinitionsFolder,
               AtsClientService.get().getAtsBranch());
            Set<String> stateNames = new HashSet<>();
            AtsWorkDefinitionSheetProviders.importWorkDefinitionSheets(resultData, changes, folder,
               dialog.getSelection(), stateNames);
            if (!resultData.isErrors()) {
               changes.execute();
            }
            XResultDataUI.report(resultData, getName());
         }
      }
   }
}
