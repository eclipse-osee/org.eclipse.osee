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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public abstract class XHyperlinkLabelSelection extends XWidget {

   Label valueLabel;
   HyperLinkLabel selectHyperLinkLabel, clearHyperLinkLabel;
   private final boolean supportClear;

   /**
    * @param label
    */
   public XHyperlinkLabelSelection(String label) {
      this(label, false);
   }

   public XHyperlinkLabelSelection(String label, boolean supportClear) {
      super(label);
      this.supportClear = supportClear;
   }

   public String getCurrentValue() {
      return "";
   }

   public String getHyperlinkLabelString() {
      return " (select)";
   }

   public String getClearHyperlinkLabelString() {
      return "(clear) ";
   }

   public boolean handleSelection() {
      return false;
   }

   public boolean handleClear() {
      return false;
   }

   /**
    * @return the supportClear
    */
   public boolean isSupportClear() {
      return supportClear;
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.skynet.gui.widgets.XWidget#createControls(org.eclipse.swt.widgets.Composite, int)
    */
   @Override
   protected void createControls(Composite parent, int horizontalSpan) {

      Composite comp = new Composite(parent, SWT.NONE);
      comp.setLayout(ALayout.getZeroMarginLayout(5, false));
      comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      // Create Text Widgets
      if (isDisplayLabel() && !getLabel().equals("")) {
         labelWidget = new Label(comp, SWT.NONE);
         labelWidget.setText(getLabel() + ":");
         if (getToolTip() != null) {
            labelWidget.setToolTipText(getToolTip());
         }
      }

      selectHyperLinkLabel = new HyperLinkLabel(comp, SWT.NONE);
      selectHyperLinkLabel.setToolTipText("Select to Modify");
      selectHyperLinkLabel.addListener(SWT.MouseUp, new Listener() {
         /*
          * (non-Javadoc)
          * 
          * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
          */
         public void handleEvent(Event event) {
            if (handleSelection()) {
               refresh();
               notifyXModifiedListeners();
            }
         }
      });
      if (supportClear) {
         clearHyperLinkLabel = new HyperLinkLabel(comp, SWT.NONE);
         clearHyperLinkLabel.setToolTipText("Select to Clear");
         clearHyperLinkLabel.addListener(SWT.MouseUp, new Listener() {
            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
             */
            public void handleEvent(Event event) {
               if (handleClear()) {
                  refresh();
                  notifyXModifiedListeners();
               }
            }
         });
      }
      valueLabel = new Label(comp, SWT.NONE);
      valueLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      valueLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));

      refresh();
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.skynet.gui.widgets.XWidget#refresh()
    */
   @Override
   public void refresh() {
      selectHyperLinkLabel.refresh();
      selectHyperLinkLabel.setText(getHyperlinkLabelString());
      if (supportClear) {
         clearHyperLinkLabel.refresh();
         clearHyperLinkLabel.setText(getClearHyperlinkLabelString());
      }
      valueLabel.setText(getCurrentValue());
      valueLabel.getParent().layout();
      validate();

   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.skynet.gui.widgets.XWidget#getControl()
    */
   @Override
   public Control getControl() {
      return valueLabel;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#adaptControls(org.eclipse.ui.forms.widgets.FormToolkit)
    */
   @Override
   public void adaptControls(FormToolkit toolkit) {
      super.adaptControls(toolkit);
      toolkit.adapt(selectHyperLinkLabel, true, true);
      selectHyperLinkLabel.refresh();
      if (supportClear) {
         toolkit.adapt(clearHyperLinkLabel, true, true);
         clearHyperLinkLabel.refresh();
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.skynet.gui.widgets.XWidget#toHTML(java.lang.String)
    */
   @Override
   public String toHTML(String labelFont) {
      return AHTML.getLabelValueStr(AHTML.LABEL_FONT, getHyperlinkLabelString(), getCurrentValue());
   }

   /* (non-Javadoc)
    * @see osee.skynet.gui.widgets.XWidget#dispose()
    */
   @Override
   public void dispose() {
   }

   /* (non-Javadoc)
    * @see osee.skynet.gui.widgets.XWidget#getData()
    */
   @Override
   public Object getData() {
      return null;
   }

   /* (non-Javadoc)
    * @see osee.skynet.gui.widgets.XWidget#getReportData()
    */
   @Override
   public String getReportData() {
      return null;
   }

   /* (non-Javadoc)
    * @see osee.skynet.gui.widgets.XWidget#getXmlData()
    */
   @Override
   public String getXmlData() {
      return null;
   }

   /* (non-Javadoc)
    * @see osee.skynet.gui.widgets.XWidget#isValid()
    */
   @Override
   public IStatus isValid() {
      return Status.OK_STATUS;
   }

   /* (non-Javadoc)
    * @see osee.skynet.gui.widgets.XWidget#setFocus()
    */
   @Override
   public void setFocus() {
   }

   /* (non-Javadoc)
    * @see osee.skynet.gui.widgets.XWidget#setXmlData(java.lang.String)
    */
   @Override
   public void setXmlData(String str) {
   }

}
