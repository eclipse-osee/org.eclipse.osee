/*********************************************************************
 * Copyright (c) 2021 Boeing
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
package org.eclipse.osee.ats.ide.util.widgets.dialog;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.ide.actions.wizard.NewActionUtil;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.util.IsEnabled;
import org.eclipse.osee.framework.ui.skynet.widgets.checkbox.CheckBoxStateFilteredTreeViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.checkbox.ICheckBoxStateTreeListener;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * @author Donald G. Dunne
 */
public class ActionableItemCheckboxStateDialog extends EntryDialog implements IsEnabled {

   private final Collection<IAtsActionableItem> selectedAis;
   private final Collection<IAtsActionableItem> ais;
   private CheckBoxStateFilteredTreeViewer<IAtsActionableItem> treeViewer;

   public ActionableItemCheckboxStateDialog(String dialogTitle, String dialogMessage, Collection<IAtsActionableItem> ais) {
      super(dialogTitle, dialogMessage);
      this.ais = ais;
      selectedAis = new HashSet<>();
   }

   @Override
   protected void createExtendedArea(Composite parent) {

      text.dispose();

      Composite comboComp = new Composite(parent, SWT.NONE);
      comboComp.setLayout(new GridLayout(6, false));
      GridData gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
      gd.widthHint = 750;
      gd.horizontalSpan = 2;
      comboComp.setLayoutData(gd);
      createAisSelection(parent);

   }

   private void createAisSelection(Composite parent) {
      Pair<CheckBoxStateFilteredTreeViewer<IAtsActionableItem>, Text> results =
         NewActionUtil.createActionableItemTreeViewer(parent, null);
      treeViewer = results.getFirst();
      treeViewer.setEnabledChecker(this);

      treeViewer.addCheckListener(new ICheckBoxStateTreeListener() {
         @Override
         public void checkStateNodesChanged() {
            selectedAis.clear();
            for (Object obj : treeViewer.getChecked()) {
               selectedAis.add((IAtsActionableItem) obj);
            }
            handleModified();
         }
      });
      treeViewer.getViewer().setInput(ais);
   }

   public Set<IAtsActionableItem> getSelectedActionableItems() {
      Set<IAtsActionableItem> selected = new HashSet<>();
      for (Object obj : treeViewer.getChecked()) {
         selected.add((IAtsActionableItem) obj);
      }
      return selected;
   }

   @Override
   public boolean isEntryValid() {
      if (!super.isEntryValid()) {
         return false;
      }
      if (treeViewer != null) {
         if (selectedAis.isEmpty()) {
            setErrorString("Must select Actionable Item");
            return false;
         } else if (selectedAis.size() > 1) {
            setErrorString("Only select 1 Actionable Item");
            return false;
         }
         IAtsActionableItem ai = selectedAis.iterator().next();
         IAtsTeamDefinition teamDef = ai.getAtsApi().getActionableItemService().getTeamDefinitionInherited(ai);
         if (teamDef == null) {
            AWorkbench.popup("No related Team Definition for selected Actionable Item.  Choose another");
            treeViewer.setChecked(ai, false);
         }
      }
      return true;
   }

   @Override
   protected Control createButtonBar(Composite parent) {
      Control control = super.createButtonBar(parent);
      handleModified();
      return control;
   }

   @Override
   public boolean isEnabled(Object obj) {
      if (obj instanceof IAtsActionableItem) {
         return ((IAtsActionableItem) obj).isActive() && ((IAtsActionableItem) obj).isActionable();
      }
      return false;
   }

}
