/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.widgets;

import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
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
public abstract class XAbstractHyperlinkLabelCmdValueSelWidget extends XWidget {

   Label valueLabel;
   Hyperlink selectHyperLinkLabel, clearHyperLinkLabel;
   protected boolean supportClear;
   protected Integer truncateValueLength = null;
   private Composite comp;

   public XAbstractHyperlinkLabelCmdValueSelWidget(WidgetId widgetId, String label) {
      this(widgetId, label, false);
   }

   public XAbstractHyperlinkLabelCmdValueSelWidget(WidgetId widgetId, String label, boolean supportClear, Integer truncateValueLength) {
      super(widgetId, label);
      this.supportClear = supportClear;
      this.truncateValueLength = truncateValueLength;
   }

   public XAbstractHyperlinkLabelCmdValueSelWidget(WidgetId widgetId, String label, boolean supportClear) {
      this(widgetId, label, supportClear, null);
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

   public boolean isSupportClear() {
      return supportClear;
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {

      comp = new Composite(parent, SWT.NONE);
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
         if (getToolkit() != null) {
            getToolkit().adapt(labelWidget, true, true);
         }
      }
      if (getToolkit() == null) {
         selectHyperLinkLabel = new Hyperlink(comp, SWT.NONE);
         selectHyperLinkLabel.setForeground(Displays.getSystemColor(SWT.COLOR_BLUE));
      } else {
         selectHyperLinkLabel = getToolkit().createHyperlink(comp, "<select>", SWT.NONE);
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
         if (getToolkit() == null) {
            clearHyperLinkLabel = new Hyperlink(comp, SWT.NONE);
            clearHyperLinkLabel.setForeground(Displays.getSystemColor(SWT.COLOR_BLUE));
         } else {
            clearHyperLinkLabel = getToolkit().createHyperlink(comp, "<clear>", SWT.NONE);
         }
         clearHyperLinkLabel.setToolTipText("Select to Clear");
         clearHyperLinkLabel.addListener(SWT.MouseUp, new Listener() {
            @Override
            public void handleEvent(Event event) {
               handleClear();
            }
         });
      }
      valueLabel = new Label(comp, SWT.NONE);
      valueLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      valueLabel.setForeground(Displays.getSystemColor(SWT.COLOR_BLACK));

      refresh();
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
         valueLabel.redraw();
         valueLabel.requestLayout();
         valueLabel.getParent().layout();
      }
      validate();
   }

   @Override
   public Control getControl() {
      return comp;
   }

   @Override
   public void adaptControls(FormToolkit toolkit) {
      super.adaptControls(getToolkit());
      if (Widgets.isAccessible(selectHyperLinkLabel)) {
         getToolkit().adapt(selectHyperLinkLabel, true, true);
      }
      if (Widgets.isAccessible(clearHyperLinkLabel)) {
         getToolkit().adapt(clearHyperLinkLabel, true, true);
      }
      if (Widgets.isAccessible(valueLabel)) {
         getToolkit().adapt(valueLabel, true, true);
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
