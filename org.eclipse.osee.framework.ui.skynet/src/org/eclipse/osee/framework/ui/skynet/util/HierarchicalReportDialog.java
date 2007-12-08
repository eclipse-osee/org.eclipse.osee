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
package org.eclipse.osee.framework.ui.skynet.util;

import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.SelectionDialog;

/**
 * @author Donald G. Dunne
 */
public class HierarchicalReportDialog extends SelectionDialog {

   XCheckBox showAttributesCheck = new XCheckBox("Include Attributes");
   boolean showAttributes = true;
   XCheckBox recurseChildrenCheck = new XCheckBox("Recurse Children");
   boolean recurseChildren = true;

   public HierarchicalReportDialog(Shell parent) {
      super(parent);
      setTitle("Heirarchical Report");
      setMessage(String.format("Select Reporting Options"));
   }

   @Override
   protected Control createDialogArea(Composite container) {

      Composite comp = new Composite(container, SWT.NONE);
      comp.setLayout(new GridLayout(2, false));
      comp.setLayoutData(new GridData(GridData.FILL_BOTH));

      showAttributesCheck.createWidgets(comp, 2);
      showAttributesCheck.set(showAttributes);
      showAttributesCheck.addSelectionListener(new SelectionListener() {
         public void widgetDefaultSelected(SelectionEvent e) {
         }

         public void widgetSelected(SelectionEvent e) {
            showAttributes = showAttributesCheck.isSelected();
         };
      });
      recurseChildrenCheck.createWidgets(comp, 2);
      recurseChildrenCheck.set(recurseChildren);
      recurseChildrenCheck.addSelectionListener(new SelectionListener() {
         public void widgetDefaultSelected(SelectionEvent e) {
         }

         public void widgetSelected(SelectionEvent e) {
            recurseChildren = recurseChildrenCheck.isSelected();
         };
      });

      return container;
   }

   public boolean isRecurseChildren() {
      return recurseChildren;
   }

   public boolean isShowAttributes() {
      return showAttributes;
   }

}
