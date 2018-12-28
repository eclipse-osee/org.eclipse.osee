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

import java.util.Collection;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.core.workdef.WorkDefinitionSheet;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.workdef.AtsDslUtil;
import org.eclipse.osee.ats.workdef.AtsWorkDefinitionSheetProviders;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.compare.CompareHandler;
import org.eclipse.osee.framework.ui.skynet.compare.CompareItem;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class ValidateWorkspaceToDatabaseWorkDefinitions extends XNavigateItemAction {

   public ValidateWorkspaceToDatabaseWorkDefinitions(XNavigateItem parent) {
      super(parent, "Validate Work Definitions from workspace and DB", AtsImage.WORK_DEFINITION);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      if (!MessageDialog.openConfirm(Displays.getActiveShell(), getName(), getName())) {
         return;
      }
      EntryDialog dialog = new EntryDialog(getName(), "Enter DB Type");
      if (dialog.open() == 0) {
         Collection<WorkDefinitionSheet> sheets =
            AtsWorkDefinitionSheetProviders.getWorkDefinitionSheets(dialog.getEntry());
         XResultData resultData = new XResultData();
         resultData.log(getName());
         for (WorkDefinitionSheet sheet : sheets) {
            resultData.addRaw("Sheet: " + sheet.getName() + "  ");
            if (sheet.getName().endsWith("AIs_And_Teams")) {
               resultData.log(" OK - AIs_And_Teams - No Artifact Needed");
               continue;
            }
            Artifact workDefArt = null;
            try {
               if (sheet.getArtifact() == null) {
                  workDefArt = ArtifactQuery.getArtifactFromTypeAndName(AtsArtifactTypes.WorkDefinition,
                     sheet.getName(), AtsClientService.get().getAtsBranch());
               } else {
                  workDefArt = AtsClientService.get().getQueryServiceClient().getArtifact(sheet.getArtifact(),
                     AtsClientService.get().getAtsBranch());
               }
            } catch (ArtifactDoesNotExist ex) {
               // do nothing;
            }
            if (workDefArt == null) {
               resultData.error(" No Artifact Found with name [" + sheet.getName() + "]");
               continue;
            }
            String sheetText = AtsDslUtil.getString(sheet);
            sheetText = sheetText.replaceAll("\r\n", "\n");
            String artText = workDefArt.getSoleAttributeValueAsString(AtsAttributeTypes.DslSheet, "");
            artText = artText.replaceAll("\r\n", "\n");
            if (!sheetText.equals(artText)) {
               resultData.error(" Different (see opened diff editor)");
               CompareHandler compareHandler = new CompareHandler(
                  "Compare [" + sheet.getName() + "] Work Definition file/artifact",
                  new CompareItem("File contents", sheetText, System.currentTimeMillis(), true, "file_contents"),
                  new CompareItem("Artifact contents", artText, System.currentTimeMillis(), true, "artifact_contents"),
                  null);
               compareHandler.compare();
            } else {
               resultData.log(" - OK");
            }
         }
         XResultDataUI.report(resultData, getName());
      }
   }
}
