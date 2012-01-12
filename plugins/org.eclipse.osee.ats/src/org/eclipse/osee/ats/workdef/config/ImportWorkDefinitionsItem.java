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
import java.util.List;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.core.config.AtsArtifactToken;
import org.eclipse.osee.ats.core.workdef.WorkDefinitionSheet;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.workdef.AtsWorkDefinitionSheetProviders;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;

public class ImportWorkDefinitionsItem extends XNavigateItemAction {

   public ImportWorkDefinitionsItem(XNavigateItem parent) {
      super(parent, "Import Work Definitions to DB", AtsImage.WORK_DEFINITION);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      List<WorkDefinitionSheet> importSheets = new ArrayList<WorkDefinitionSheet>();
      for (WorkDefinitionSheet sheet : AtsWorkDefinitionSheetProviders.getWorkDefinitionSheets()) {
         if (!sheet.getName().endsWith("AIs_And_Teams")) {
            importSheets.add(sheet);
         }
      }
      WorkDefinitionCheckTreeDialog dialog = new WorkDefinitionCheckTreeDialog(importSheets);
      dialog.setTitle(getName());
      dialog.setMessage("Select Work Definition Sheet(s) to import");
      if (dialog.open() == 0) {
         XResultData resultData = new XResultData(false);
         SkynetTransaction transaction = TransactionManager.createTransaction(AtsUtil.getAtsBranch(), getName());
         Artifact folder =
            OseeSystemArtifacts.getOrCreateArtifact(AtsArtifactToken.WorkDefinitionsFolder, AtsUtil.getAtsBranch());
         AtsWorkDefinitionSheetProviders.importWorkDefinitionSheets(resultData, true, transaction, folder,
            dialog.getSelection());

         if (!resultData.isErrors()) {
            transaction.execute();
         }
         XResultDataUI.report(resultData, getName());
      }
   }
}
