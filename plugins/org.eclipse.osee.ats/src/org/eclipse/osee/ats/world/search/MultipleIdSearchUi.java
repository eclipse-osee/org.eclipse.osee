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

import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.jdk.core.type.MutableBoolean;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.branch.BranchSelectionDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryCheckDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class MultipleIdSearchUi {

   private final MultipleIdSearchData data;
   private boolean multiLine;

   public MultipleIdSearchUi(MultipleIdSearchData data) {
      this.data = data;
   }

   public boolean getInput() {
      MutableBoolean result = new MutableBoolean(false);
      Displays.pendInDisplayThread(new EntryJob(result));
      return result.getValue();
   }

   private final class EntryJob implements Runnable {
      private final MutableBoolean result;

      public EntryJob(MutableBoolean result) {
         this.result = result;
      }

      @Override
      public void run() {
         EntryDialog ed = null;
         if (AtsClientService.get().getUserService().isAtsAdmin()) {
            ed = new EntryCheckDialog(data.getName(), "Enter Legacy ID, ID (comma separated)", "Include ArtIds");
         } else {
            ed = new EntryDialog(Displays.getActiveShell(), data.getName(), null,
               "Enter Legacy ID or ID (comma separated)", MessageDialog.QUESTION, new String[] {"OK", "Cancel"}, 0);
         }
         if (multiLine) {
            ed.setFillVertically(true);
         }
         int response = ed.open();
         if (response == 0) {
            String entry = processEntry(ed.getEntry());
            data.setEnteredIds(entry);
            if (ed instanceof EntryCheckDialog) {
               data.setIncludeArtIds(((EntryCheckDialog) ed).isChecked());
               if (data.isIncludeArtIds()) {
                  data.setBranch(BranchSelectionDialog.getBranchFromUser());
               }
               result.setValue(true);
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
                  result.setValue(true);
               }
            }
         }
      }

   }

   public void setMultiLine(boolean multiLine) {
      this.multiLine = multiLine;
   }

   public String processEntry(String entry) {
      String result = entry;
      if (multiLine) {
         Set<String> entries = new HashSet<>(50);
         for (String line : entry.split(System.getProperty("line.separator"))) {
            line = line.replaceFirst("^ +", "");
            line = line.replaceFirst(" +$", "");
            entries.add(line);
         }
         result = org.eclipse.osee.framework.jdk.core.util.Collections.toString(",", entries);
      }
      return result;
   }
}
