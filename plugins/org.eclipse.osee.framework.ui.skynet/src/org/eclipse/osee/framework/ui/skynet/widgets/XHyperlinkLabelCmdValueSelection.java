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
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Donald G. Dunne
 */
public abstract class XHyperlinkLabelCmdValueSelection extends GenericXWidget {

   Label valueLabel;
   Hyperlink selectHyperLinkLabel, clearHyperLinkLabel;
   private final boolean supportClear;
   private Integer truncateValueLength = null;

   public XHyperlinkLabelCmdValueSelection(String label) {
      this(label, false);
   }

   public XHyperlinkLabelCmdValueSelection(String label, boolean supportClear, Integer truncateValueLength) {
      super(label);
      this.supportClear = supportClear;
      this.truncateValueLength = truncateValueLength;
   }

   public XHyperlinkLabelCmdValueSelection(String label, boolean supportClear) {
      this(label, supportClear, null);
   }

   public String getCurrentValue() {
      return "";
   }

   public void setErrorState(boolean error) {
      valueLabel.setForeground(error ? Displays.getSystemColor(SWT.COLOR_RED) : null);
   }

   public String getHyperlinkLabelString() {
      return " <select>";
   }

   public String getClearHyperlinkLabelString() {
      return "<clear> ";
   }

   public boolean handleSelection() {
      return false;
   }

   public boolean handleClear() {
      return false;
   }

   public boolean isSupportClear() {
      return supportClear;
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {

      Composite comp = new Composite(parent, SWT.NONE);
      comp.setLayout(ALayout.getZeroMarginLayout(5, false));
      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      gd.horizontalSpan = horizontalSpan;
      comp.setLayoutData(gd);

      // Create Text Widgets
      if (isDisplayLabel() && !getLabel().equals("")) {
         labelWidget = new Label(comp, SWT.NONE);
         labelWidget.setText(getLabel() + ":");
         if (getToolTip() != null) {
            labelWidget.setToolTipText(getToolTip());
         }
         if (toolkit != null) {
            toolkit.adapt(labelWidget, true, true);
         }
      }
      if (toolkit == null) {
         selectHyperLinkLabel = new Hyperlink(comp, SWT.NONE);
      } else {
         selectHyperLinkLabel = toolkit.createHyperlink(comp, "<select>", SWT.NONE);
      }
      selectHyperLinkLabel.setToolTipText(Strings.isValid(getToolTip()) ? getToolTip() : "Select to Modify");
      selectHyperLinkLabel.addListener(SWT.MouseUp, new Listener() {
         @Override
         public void handleEvent(Event event) {
            if (handleSelection()) {
               refresh();
               notifyXModifiedListeners();
            }
         }
      });
      if (supportClear) {
         if (toolkit == null) {
            clearHyperLinkLabel = new Hyperlink(comp, SWT.NONE);
         } else {
            clearHyperLinkLabel = toolkit.createHyperlink(comp, "<clear>", SWT.NONE);
         }
         clearHyperLinkLabel.setToolTipText("Select to Clear");
         clearHyperLinkLabel.addListener(SWT.MouseUp, new Listener() {
            @Override
            public void handleEvent(Event event) {
               clear();
            }
         });
      }
      valueLabel = new Label(comp, SWT.NONE);
      valueLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      valueLabel.setForeground(Displays.getSystemColor(SWT.COLOR_BLACK));

      refresh();
   }

   public void clear() {
      if (handleClear()) {
         refresh();
         notifyXModifiedListeners();
      }
   }

   @Override
   public void refresh() {
      if (getControl() == null || getControl().isDisposed()) {
         return;
      }
      boolean updated = false;
      if (!getHyperlinkLabelString().equals(selectHyperLinkLabel.getText())) {
         selectHyperLinkLabel.setText(getHyperlinkLabelString());
         updated = true;
      }
      if (supportClear && !getClearHyperlinkLabelString().equals(clearHyperLinkLabel.getText())) {
         clearHyperLinkLabel.setText(getClearHyperlinkLabelString());
         updated = true;
      }
      if (!getCurrentValue().equals(valueLabel.getText())) {
         valueLabel.setText(truncateValueLength == null ? getCurrentValue() : Strings.truncate(getCurrentValue(),
            truncateValueLength, true));
         valueLabel.setToolTipText(getCurrentValue());
         updated = true;
      }
      if (updated) {
         valueLabel.getParent().layout();
      }
      validate();
   }

   @Override
   public Control getControl() {
      return valueLabel;
   }

   @Override
   public void adaptControls(FormToolkit toolkit) {
      super.adaptControls(toolkit);
      toolkit.adapt(selectHyperLinkLabel, true, true);
      if (supportClear) {
         toolkit.adapt(clearHyperLinkLabel, true, true);
      }
   }

   @Override
   public String toHTML(String labelFont) {
      return AHTML.getLabelValueStr(AHTML.LABEL_FONT, getHyperlinkLabelString(), getCurrentValue());
   }

   public Integer getTruncateValueLength() {
      return truncateValueLength;
   }

   public void setTruncateValueLength(Integer truncateValueLength) {
      this.truncateValueLength = truncateValueLength;
   }

   public void setDisableHyperLink() {
      if (selectHyperLinkLabel != null && !selectHyperLinkLabel.isDisposed()) {
         selectHyperLinkLabel.setEnabled(false);
      }
      if (clearHyperLinkLabel != null && !clearHyperLinkLabel.isDisposed()) {
         clearHyperLinkLabel.setEnabled(false);
      }
   }

   public void setEnableHyperLink() {
      if (selectHyperLinkLabel != null && !selectHyperLinkLabel.isDisposed()) {
         selectHyperLinkLabel.setEnabled(true);
      }
      if (clearHyperLinkLabel != null && !clearHyperLinkLabel.isDisposed()) {
         clearHyperLinkLabel.setEnabled(true);
      }
   }
}
