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
package org.eclipse.osee.ats.ide.util.widgets.dialog;

import java.util.Collection;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Donald G. Dunne
 */
public class StateListAndTitleDialog extends StateListDialog {

   String reviewTitle;
   XText titleText;

   public StateListAndTitleDialog(String title, String message, Collection<String> values) {
      super(title, message, values);
   }

   @Override
   protected Control createDialogArea(Composite container) {
      Composite comp = new Composite(container, SWT.NONE);
      comp.setLayout(new GridLayout());
      comp.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, true, false));
      titleText = new XText("Review Title");
      titleText.createWidgets(comp, 1);
      if (reviewTitle != null) {
         titleText.set(reviewTitle);
      }
      titleText.getStyledText().setLayoutData(new GridData(SWT.FILL, SWT.LEFT, true, false));
      titleText.addXModifiedListener(new XModifiedListener() {
         @Override
         public void widgetModified(XWidget widget) {
            reviewTitle = titleText.get();
         }
      });

      Control control = super.createDialogArea(container);
      getTableViewer().setComparator(new ViewerComparator());

      return control;
   }

   /**
    * @return the reviewTitle
    */
   public String getReviewTitle() {
      return reviewTitle;
   }

   /**
    * @param reviewTitle the reviewTitle to set
    */
   public void setReviewTitle(String reviewTitle) {
      this.reviewTitle = reviewTitle;
   }

}
