/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.ats.ide.navigate;

import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryCheckDialog;

/**
 * @author Donald G. Dunne
 */
public class DuplicateArtifactReport extends XNavigateItemAction {

   public DuplicateArtifactReport() {
      super("Duplicate Artifact Report", FrameworkImage.GEAR, AtsNavigateViewItems.ATS_UTIL);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {

      String newArtId = "NEW_ART_ID";
      EntryCheckDialog diag = new EntryCheckDialog(getName(), "Artifact Id",
         "Get Next Art Id (Only check this on last run when your ready\nto fix database as it will burn a real artifact id)");
      if (diag.open() == Window.OK) {

         if (diag.isChecked()) {
            newArtId = String.valueOf(ConnectionHandler.getNextSequence(OseeData.ART_ID_SEQ, true));
         }
         String entry = diag.getEntry();
         if (Strings.isNotNumeric(entry)) {
            AWorkbench.popup("Inavlid artifact id [%s]", entry);
            return;
         }
         ArtifactId id = ArtifactId.valueOf(entry);
         XResultData rd = AtsApiService.get().getServerEndpoints().getHealthEndpoint().dupArtReport(id, newArtId);
         String html = XResultDataUI.getReport(rd, getName()).getManipulatedHtml();
         html = html.replaceAll("OSEE_COMMENT=(.*?),", "OSEE_COMMENT=<b style=\"color:blue;\">$1</b>,");
         html = html.replaceAll("TIME=(.*?),", "TIME=<b style=\"color:blue;\">$1</b>,");
         html = html.replaceAll("(ACTION:.*?);", "<b style=\"color:green;\">$1</b>");
         ResultsEditor.open("Report", getName(), html);
      }
   }

   @Override
   public String getDescription() {
      return "Generate a report on showing/resolving duplicate artifacts with same id.  \n" //
         + "Without checking box, this will just show analysis and what querys would be.";
   }
}
