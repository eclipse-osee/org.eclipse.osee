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

package org.eclipse.osee.ats.ide.util.widgets.dialog;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsObjectLabelProvider;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactNameSorter;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxTreeDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Donald G. Dunne
 */
public class ActionableItemTreeWithChildrenDialog extends FilteredCheckboxTreeDialog<IAtsActionableItem> {

   XCheckBox recurseChildrenCheck = new XCheckBox("Include all children Actionable Item Actions");
   boolean recurseChildren = false;
   protected Composite dialogComp;
   Collection<IAtsActionableItem> actionableItems;
   boolean addIncludeAllCheckbox = true;

   public ActionableItemTreeWithChildrenDialog(Active active) {
      this(active, AtsApiService.get().getActionableItemService().getTopLevelActionableItems(active));
   }

   public ActionableItemTreeWithChildrenDialog(Active active, Collection<IAtsActionableItem> actionableItems) {
      super("Select Actionable Item", "Select Actionable Item", new ActionableItemTreeContentProvider(active),
         new AtsObjectLabelProvider(), new ArtifactNameSorter());
      this.actionableItems = actionableItems;
      setInput(actionableItems);
   }

   /**
    * @return selected AIs and children if recurseChildren was checked
    */
   public Collection<IAtsActionableItem> getResultAndRecursedAIs() {
      Set<IAtsActionableItem> aias = new HashSet<>(10);
      for (Object obj : getResult()) {
         IAtsActionableItem ai = (IAtsActionableItem) obj;
         aias.add(ai);
         if (recurseChildren) {
            aias.addAll(AtsApiService.get().getActionableItemService().getChildren(ai, true));
         }
      }
      return aias;
   }

   @Override
   protected Control createDialogArea(Composite container) {

      Control control = super.createDialogArea(container);
      dialogComp = new Composite(control.getParent(), SWT.NONE);
      dialogComp.setLayout(new GridLayout(2, false));
      dialogComp.setLayoutData(new GridData(GridData.FILL_BOTH));

      if (addIncludeAllCheckbox) {
         recurseChildrenCheck.createWidgets(dialogComp, 2);
         recurseChildrenCheck.set(recurseChildren);
         recurseChildrenCheck.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
               recurseChildren = recurseChildrenCheck.isSelected();
            };
         });
      }
      if (actionableItems.size() == 1) {
         getTreeViewer().getCheckboxTreeViewer().expandToLevel(actionableItems.iterator().next(), 1);
      }

      return container;
   }

   public void setAddIncludeAllCheckbox(boolean addIncludeAllCheckbox) {
      this.addIncludeAllCheckbox = addIncludeAllCheckbox;
   }

}
