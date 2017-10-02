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

package org.eclipse.osee.ats.util.widgets.dialog;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.core.config.ActionableItems;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.AtsObjectLabelProvider;
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
public class ActionableItemTreeWithChildrenDialog extends FilteredCheckboxTreeDialog {

   XCheckBox recurseChildrenCheck = new XCheckBox("Include all children Actionable Item Actions");
   boolean recurseChildren = false;
   protected Composite dialogComp;

   public ActionableItemTreeWithChildrenDialog(Active active)  {
      this(active, ActionableItems.getTopLevelActionableItems(active, AtsClientService.get()));
   }

   public ActionableItemTreeWithChildrenDialog(Active active, Collection<IAtsActionableItem> actionableItems) {
      super("Select Actionable Item", "Select Actionable Item", new ActionableItemTreeContentProvider(active),
         new AtsObjectLabelProvider(), new ArtifactNameSorter());
      setInput(actionableItems);
   }

   /**
    * @return selected AIs and children if recurseChildren was checked
    */
   public Collection<IAtsActionableItem> getResultAndRecursedAIs()  {
      Set<IAtsActionableItem> aias = new HashSet<>(10);
      for (Object obj : getResult()) {
         IAtsActionableItem ai = (IAtsActionableItem) obj;
         aias.add(ai);
         if (recurseChildren) {
            aias.addAll(ActionableItems.getChildren(ai, true));
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

      recurseChildrenCheck.createWidgets(dialogComp, 2);
      recurseChildrenCheck.set(recurseChildren);
      recurseChildrenCheck.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            recurseChildren = recurseChildrenCheck.isSelected();
         };
      });

      return container;
   }

}
