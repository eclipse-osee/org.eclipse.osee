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
package org.eclipse.osee.framework.ui.skynet.search.page.actions;

import org.eclipse.osee.framework.ui.skynet.search.page.ArtifactSearchComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class BranchSelectionListener extends SelectionAdapter {
   private ArtifactSearchComposite parentWindow;

   public BranchSelectionListener(ArtifactSearchComposite parentWindow) {
      this.parentWindow = parentWindow;
      this.parentWindow.getRevisionWidget().getBranchCombo().addSelectionListener(this);
   }

   public void widgetSelected(SelectionEvent e) {
      String currentSelection = this.parentWindow.getRevisionWidget().getCurrentBranchSelection();
      if (currentSelection != null && !currentSelection.equals("") && !currentSelection.equals(this.parentWindow.getRevisionWidget().getDefaultBranchValue())) {

         this.parentWindow.getRevisionWidget().getRevisionField().setEnabled(true);
         this.parentWindow.getRevisionWidget().getRevisionField().setEditable(true);
         this.parentWindow.getRevisionWidget().setTextFieldToolTip(currentSelection);
      } else {
         this.parentWindow.getRevisionWidget().getRevisionField().setEnabled(false);
         this.parentWindow.getRevisionWidget().setTextFieldToolTip(currentSelection);
      }
   }
}
