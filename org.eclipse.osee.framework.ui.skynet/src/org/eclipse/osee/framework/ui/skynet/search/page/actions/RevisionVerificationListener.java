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
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class RevisionVerificationListener implements Listener {

   private ArtifactSearchComposite parentWindow;

   public RevisionVerificationListener(ArtifactSearchComposite parentWindow) {
      this.parentWindow = parentWindow;
      this.parentWindow.getRevisionWidget().getRevisionField().addListener(SWT.Verify, this);
   }

   private void verifyRevisionValue(String currentValue, Event e) {
      String currentBranch = this.parentWindow.getRevisionWidget().getCurrentBranchSelection();
      int revision = (currentValue.equals("") ? 0 : Integer.parseInt(currentValue));
      int min = this.parentWindow.getRevisionWidget().getValidMin(currentBranch);
      int max = this.parentWindow.getRevisionWidget().getValidMax(currentBranch);

      if (currentValue.equals("")) {
         e.doit = true;
      } else if (min <= revision && revision <= max) {
         e.doit = true;
      } else {
         revision = 0;
         e.doit = false;
      }
      updateListAndTree(currentBranch, revision);
   }

   public void handleEvent(Event e) {
      Text textField = (Text) e.widget;
      String currentText = textField.getText();

      if (!Character.isDigit(e.character) && e.character != SWT.DEL && e.character != SWT.BS) {
         e.doit = false;
         return;
      }

      final String newString;
      if (e.start >= currentText.length()) {
         newString = currentText + e.text;
      } else {
         String first = currentText.substring(0, e.start);
         String last = currentText.substring(e.end);
         newString = first + e.text + last;
      }

      if (newString.matches("\\d*")) {
         verifyRevisionValue(newString, e);
      } else {
         e.doit = false;
      }
   }

   private void updateListAndTree(String currentBranch, int revision) {
      this.parentWindow.getRevisionDataManager().setBranchAndRevision(currentBranch, revision);
      this.parentWindow.getRevisionWidget().setToolTipText(currentBranch);
   }
}