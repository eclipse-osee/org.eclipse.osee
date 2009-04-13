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
package org.eclipse.osee.framework.ui.skynet.widgets;

import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Donald G. Dunne
 */
public class XButton extends XWidget {

   protected Button button;
   private Composite parent;

   public XButton(String displayLabel, String xmlRoot) {
      super(displayLabel, xmlRoot);
   }

   public XButton(String displayLabel) {
      this(displayLabel, "");
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#getControl()
    */
   @Override
   public Control getControl() {
      return button;
   }

   /**
    * Create Check Widgets. Widgets Created: Label: "text entry" horizonatalSpan takes up 2 columns; horizontalSpan must
    * be >=2
    */
   @Override
   public void createWidgets(Composite parent, int horizontalSpan) {
      if (horizontalSpan < 2) {
         horizontalSpan = 2;
      }
      this.parent = parent;

      button = new Button(parent, SWT.PUSH);
      button.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent event) {
            setLabelError();
            notifyXModifiedListeners();
         }
      });
      if (toolTip != null) {
         button.setToolTipText(toolTip);
      }
      updateCheckWidget();
      button.setEnabled(isEditable());
      button.setText(getLabel());
   }

   @Override
   public void dispose() {
      button.dispose();
      if (parent != null && !parent.isDisposed()) parent.layout();
   }

   @Override
   public void setFocus() {
      return;
   }

   @Override
   public String getXmlData() {
      return "";
   }

   @Override
   public String getReportData() {
      return getXmlData();
   }

   @Override
   public void setXmlData(String set) {
   }

   public void addSelectionListener(SelectionListener selectionListener) {
      button.addSelectionListener(selectionListener);
   }

   private void updateCheckWidget() {
      setLabelError();
   }

   @Override
   public void refresh() {
      updateCheckWidget();
   }

   @Override
   public Result isValid() {
      return Result.TrueResult;
   }

   @Override
   public String toHTML(String labelFont) {
      return AHTML.getLabelStr(labelFont, label + ": ");
   }

   public Button getButton() {
      return button;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#getData()
    */
   @Override
   public Object getData() {
      return "";
   }
}