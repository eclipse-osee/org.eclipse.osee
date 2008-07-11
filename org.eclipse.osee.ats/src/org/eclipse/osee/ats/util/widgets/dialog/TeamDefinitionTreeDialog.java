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
   Boolean showFinished = false;
   XCheckBox showActionCheck = new XCheckBox("Show Action instead of Workflows");
   Boolean showAction = false;

   public TeamDefinitionTreeDialog(Active active) {
      super(active);
   }

   @Override
   protected Control createDialogArea(Composite container) {

      Control control = super.createDialogArea(container);

      if (showFinished != null) {
         showFinishedCheck.createWidgets(dialogComp, 2);
         showFinishedCheck.set(showFinished);
         showFinishedCheck.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
            }

            public void widgetSelected(SelectionEvent e) {
               showFinished = showFinishedCheck.isSelected();
            };
         });
      }

      if (showAction != null) {
         showActionCheck.createWidgets(dialogComp, 2);
         showActionCheck.set(showAction);
         showActionCheck.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
            }

            public void widgetSelected(SelectionEvent e) {
               showAction = showActionCheck.isSelected();
            };
         });
      }

      return control;
   }

   public Boolean isShowFinished() {
      return showFinished;
   }

   public Boolean isShowAction() {
      return showAction;
   }

   /**
    * @param showFinished true/false default for showAction checkbox; null to not display showAction checkbox
    */
   public void setShowFinished(Boolean showFinished) {
      this.showFinished = showFinished;
   }

   /**
    * @param showAction true/false default for showAction checkbox; null to not display showAction checkbox
    */
   public void setShowAction(Boolean showAction) {
      this.showAction = showAction;
   }

}
