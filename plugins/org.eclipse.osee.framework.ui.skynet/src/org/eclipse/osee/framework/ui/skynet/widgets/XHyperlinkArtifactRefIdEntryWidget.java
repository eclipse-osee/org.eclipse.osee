/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.framework.ui.skynet.widgets;

import java.text.NumberFormat;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;

/**
 * @author Donald G. Dunne
 */
public class XHyperlinkArtifactRefIdEntryWidget extends XHyperlinkLabelCmdValueSelection {

   ArtifactToken selected = ArtifactToken.SENTINEL;
   BranchToken branch = BranchToken.SENTINEL;

   public XHyperlinkArtifactRefIdEntryWidget() {
      super("");
   }

   public XHyperlinkArtifactRefIdEntryWidget(String displayLabel) {
      super(displayLabel, true, 50);
   }

   @Override
   public String getCurrentValue() {
      if (branch.isInvalid()) {
         return "Branch Not Selected";
      }
      return (selected == null || selected.isInvalid()) ? "No Set" : selected.toStringWithId();
   }

   @Override
   public boolean handleSelection() {
      if (branch.isInvalid()) {
         AWorkbench.popup("No Branch Selected");
         return false;
      }
      EntryDialog dialog = new EntryDialog("Enter " + label, "Enter " + label);
      dialog.setNumberFormat(NumberFormat.getIntegerInstance());
      int result = dialog.open();
      if (result != Window.OK) {
         return false;
      }
      String artIdStr = dialog.getEntry();
      if (Strings.isNumeric(artIdStr)) {
         Artifact art =
            ArtifactQuery.getArtifactOrNull(ArtifactId.valueOf(artIdStr), branch, DeletionFlag.EXCLUDE_DELETED);
         if (art != null) {
            selected = art;
            return true;
         } else {
            AWorkbench.popup(String.format("Invalid Artifact Id [%s]\nfor\nBranch [%s]", art.toStringWithId(),
               branch.toStringWithId()));
         }
      }
      return false;
   }

   @Override
   public boolean handleClear() {
      selected = null;
      return true;
   }

   public ArtifactToken getSelected() {
      return selected;
   }

   public BranchToken getBranch() {
      return branch;
   }

   public void setBranch(BranchToken branch) {
      this.branch = branch;
      if (selected.isValid()) {
         refresh();
      }
   }

   public void setSelected(ArtifactToken selected) {
      this.selected = selected;
   }

}
