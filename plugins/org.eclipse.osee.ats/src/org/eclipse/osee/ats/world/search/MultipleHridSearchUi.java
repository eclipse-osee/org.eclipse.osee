/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.world.search;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.branch.BranchSelectionDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryCheckDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class MultipleHridSearchUi {

   private final MultipleHridSearchData data;

   public MultipleHridSearchUi(MultipleHridSearchData data) {
      this.data = data;
   }

   public boolean getInput() {
      EntryJob job = new EntryJob();
      Displays.ensureInDisplayThread(job, true);
      extractIds();
      return job.isValid();
   }

   public class EntryJob implements Runnable {
      boolean valid = false;

      @Override
      public void run() {
         EntryDialog ed = null;
         if (AtsUtil.isAtsAdmin()) {
            ed =
                  new EntryCheckDialog(data.getName(), "Enter Legacy ID, Guid or HRID (comma separated)",
                        "Include ArtIds");
         } else {
            ed =
                  new EntryDialog(Display.getCurrent().getActiveShell(), data.getName(), null,
                        "Enter Legacy ID, Guid or HRID (comma separated)", MessageDialog.QUESTION, new String[] {"OK",
                              "Cancel"}, 0);
         }
         int response = ed.open();
         if (response == 0) {
            data.setEnteredIds(ed.getEntry());
            if (ed instanceof EntryCheckDialog) {
               data.setIncludeArtIds(((EntryCheckDialog) ed).isChecked());
               if (data.isIncludeArtIds()) {
                  data.setBranch(BranchSelectionDialog.getBranchFromUser());
               }
               valid = true;
            }
            if (!Strings.isValid(data.getEnteredIds())) {
               AWorkbench.popup("Must Enter Valid Id");
            } else {
               if (data.getEnteredIds().equals("oseerocks") || data.getEnteredIds().equals("osee rocks")) {
                  AWorkbench.popup("Confirmation", "Confirmed!  Osee Rocks!");
               } else if (data.getEnteredIds().equals("purple icons")) {
                  AWorkbench.popup("Confirmation", "Yeehaw, Purple Icons Rule!!");
                  ArtifactImageManager.setOverrideImageEnum(FrameworkImage.PURPLE);
               } else {
                  valid = true;
               }
            }
         }
      }

      public boolean isValid() {
         return valid;
      }
   }

   private void extractIds() {
      for (String str : data.getEnteredIds().split(",")) {
         str = str.replaceAll("^\\s+", "");
         str = str.replaceAll("\\s+$", "");
         if (!str.equals("")) {
            data.getIds().add(str);
         }
         // allow for lower case hrids
         if (str.length() == 5) {
            if (!data.getIds().contains(str.toUpperCase())) {
               data.getIds().add(str.toUpperCase());
            }
         }
      }
   }

}
