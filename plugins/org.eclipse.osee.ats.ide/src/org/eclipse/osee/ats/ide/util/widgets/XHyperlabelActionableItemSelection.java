/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.util.widgets;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.dialog.ActionableItemTreeContentProvider;
import org.eclipse.osee.ats.ide.util.widgets.dialog.ActionableItemTreeWithChildrenDialog;
import org.eclipse.osee.ats.ide.world.WorldEditor;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabelCmdValueSelection;
import org.eclipse.osee.framework.ui.skynet.widgets.checkbox.CheckBoxStateFilteredTreeDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.checkbox.CheckBoxStateTreeLabelProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.checkbox.ICheckBoxStateTreeViewer;

/**
 * @author Donald G. Dunne
 */
public class XHyperlabelActionableItemSelection extends XHyperlinkLabelCmdValueSelection {

   Collection<IAtsActionableItem> selectedAis = new HashSet<>();
   Collection<IAtsActionableItem> ais;
   ActionableItemTreeWithChildrenDialog dialog = null;

   public XHyperlabelActionableItemSelection() {
      this("Actionable Item(s)");
   }

   public XHyperlabelActionableItemSelection(String label) {
      super(label, true, WorldEditor.TITLE_MAX_LENGTH);
   }

   public Collection<IAtsActionableItem> getSelectedActionableItems() {
      return selectedAis;
   }

   @Override
   public Object getData() {
      List<Artifact> arts = org.eclipse.osee.framework.jdk.core.util.Collections.castAll(getSelectedActionableItems());
      return arts;
   }

   @Override
   public String getCurrentValue() {
      return Collections.toString(", ", selectedAis);
   }

   public void setSelectedAIs(Collection<IAtsActionableItem> selectedAIs) {
      this.selectedAis = selectedAIs;
      refresh();
      notifyXModifiedListeners();
   }

   @Override
   public boolean handleClear() {
      selectedAis.clear();
      notifyXModifiedListeners();
      return true;
   }

   @Override
   public boolean handleSelection() {
      try {
         AiLabelProvider labelProvider = new AiLabelProvider(null);
         CheckBoxStateFilteredTreeDialog<IAtsActionableItem> dialog =
            new CheckBoxStateFilteredTreeDialog<IAtsActionableItem>("Select Actionable Item(s)",
               "Select Actionable Item(s)", new ActionableItemTreeContentProvider(Active.Both), labelProvider);
         labelProvider.setTreeViewer(dialog.getTreeViewer());
         if (ais == null) {
            dialog.setInput(AtsApiService.get().getActionableItemService().getTopLevelActionableItems(Active.Active));
         } else {
            dialog.setInput(ais);
         }
         int result = dialog.open();
         if (result == 0) {
            selectedAis.clear();
            for (IAtsActionableItem ai : dialog.getChecked()) {
               if (ai.isActive() && ai.isActionable()) {
                  selectedAis.add(ai);
               }
            }
            notifyXModifiedListeners();
         }
         return true;
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   @Override
   public boolean isEmpty() {
      return selectedAis.isEmpty();
   }

   public static class AiLabelProvider extends CheckBoxStateTreeLabelProvider {
      public AiLabelProvider(ICheckBoxStateTreeViewer treeViewer) {
         super(treeViewer);
      }

      @Override
      protected boolean isEnabled(Object element) {
         return ((IAtsActionableItem) element).isActive() && ((IAtsActionableItem) element).isActionable();
      }

   }

   @Override
   public IStatus isValid() {
      IStatus status = super.isValid();
      if (isRequiredEntry() && selectedAis.isEmpty()) {
         status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Must Select Actionable Item(s)");
      }
      return status;
   }

   public Collection<IAtsActionableItem> getAis() {
      return ais;
   }

   public void setAis(Collection<IAtsActionableItem> ais) {
      this.ais = ais;
   }

}
