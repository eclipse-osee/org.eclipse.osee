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

import org.eclipse.osee.framework.skynet.core.artifact.search.Active;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Donald G. Dunne
 */
public class TeamDefinitionTreeDialog extends TeamDefinitionTreeWithChildrenDialog {

   XCheckBox showFinishedCheck = new XCheckBox("Show Completed and Cancelled Workflows");
   boolean showFinished = false;
   XCheckBox showActionCheck = new XCheckBox("Show Action instead of Workflows");
   boolean showAction = false;

   public TeamDefinitionTreeDialog(Active active) {
      super(active);
   }

   @Override
   protected Control createDialogArea(Composite container) {

      Control control = super.createDialogArea(container);

      showFinishedCheck.createWidgets(dialogComp, 2);
      showFinishedCheck.addSelectionListener(new SelectionListener() {
         public void widgetDefaultSelected(SelectionEvent e) {
         }

         public void widgetSelected(SelectionEvent e) {
            showFinished = showFinishedCheck.isSelected();
         };
      });
      showActionCheck.createWidgets(dialogComp, 2);
      showAction = true;
      showActionCheck.set(true);
      showActionCheck.addSelectionListener(new SelectionListener() {
         public void widgetDefaultSelected(SelectionEvent e) {
         }

         public void widgetSelected(SelectionEvent e) {
            showAction = showActionCheck.isSelected();
         };
      });

      return control;
   }

   public boolean isShowFinished() {
      return showFinished;
   }

   public boolean isShowAction() {
      return showAction;
   }

   /**
    * @param showFinished the showFinished to set
    */
   public void setShowFinished(boolean showFinished) {
      this.showFinished = showFinished;
   }

   /**
    * @param showAction the showAction to set
    */
   public void setShowAction(boolean showAction) {
      this.showAction = showAction;
   }

}
