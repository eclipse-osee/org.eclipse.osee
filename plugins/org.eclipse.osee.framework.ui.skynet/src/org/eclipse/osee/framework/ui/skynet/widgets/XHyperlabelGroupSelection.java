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
package org.eclipse.osee.framework.ui.skynet.widgets;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.UniversalGroup;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxTreeArtifactDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxTreeDialog;

/**
 * @author Donald G. Dunne
 */
public class XHyperlabelGroupSelection extends XHyperlinkLabelCmdValueSelection {

   public static final String WIDGET_ID = XHyperlabelGroupSelection.class.getSimpleName();
   Set<Artifact> selectedGroups = new HashSet<>();

   public XHyperlabelGroupSelection(String label) {
      super(label, true, 80);
   }

   public Set<Artifact> getSelectedGroups() {
      return selectedGroups;
   }

   @Override
   public String getCurrentValue() {
      return Collections.toString(",", selectedGroups);
   }

   public void setSelectedGroups(Set<Artifact> selectedUsers) {
      this.selectedGroups = selectedUsers;
      refresh();
   }

   @Override
   public boolean handleClear() {
      selectedGroups.clear();
      notifyXModifiedListeners();
      return true;
   }

   @Override
   public boolean handleSelection() {
      try {
         FilteredCheckboxTreeDialog dialog =
            new FilteredCheckboxTreeArtifactDialog("Select Groups", "Select Groups", getGroups());
         int result = dialog.open();
         if (result == 0) {
            selectedGroups.clear();
            for (Object obj : dialog.getChecked()) {
               selectedGroups.add((Artifact) obj);
            }
            notifyXModifiedListeners();
         }
         return true;
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   private Collection<Artifact> getGroups() {
      List<Artifact> groups = new ArrayList<>();
      for (Artifact art : UniversalGroup.getGroups(COMMON)) {
         if (!art.isOfType(CoreArtifactTypes.RootArtifact)) {
            groups.add(art);
         }
      }
      return groups;
   }

   @Override
   public boolean isEmpty() {
      return selectedGroups.isEmpty();
   }
}
