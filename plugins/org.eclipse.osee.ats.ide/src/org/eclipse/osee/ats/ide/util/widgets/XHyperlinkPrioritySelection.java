/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.ats.ide.util.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.team.Priorities;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.workflow.priority.PriorityDialog;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.widgets.GenericXWidget;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Donald G. Dunne
 */
public class XHyperlinkPrioritySelection extends GenericXWidget {

   public static final String WIDGET_ID = XHyperlinkPrioritySelection.class.getSimpleName();

   protected Hyperlink labelHyperlink;
   protected Label labelWidget;
   protected Label valueLabel;
   protected Composite comp;
   protected boolean includeColon = true;
   protected Priorities selected = Priorities.None;
   protected final List<Priorities> priorities = new ArrayList<>();;

   public XHyperlinkPrioritySelection() {
      this("");
   }

   public XHyperlinkPrioritySelection(String label) {
      super(label);
   }

   public XHyperlinkPrioritySelection(String label, Priorities... priorities) {
      super(label);
      for (Priorities type : priorities) {
         this.priorities.add(type);
      }
   }

   /**
    * Override this method to provide changing value
    */
   public String getCurrentValue() {
      if (selected == null || selected.equals(Priorities.None)) {
         return Widgets.NOT_SET;
      }
      return selected.name();
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {

      comp = new Composite(parent, SWT.NONE);
      GridLayout layout = new GridLayout(2, false);
      layout.marginHeight = 1;
      comp.setLayout(layout);
      if (isFillHorizontally()) {
         comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
      } else {
         comp.setLayoutData(new GridData());
      }
      if (toolkit != null) {
         toolkit.adapt(comp);
      }

      if (isEditable()) {
         if (toolkit == null) {
            labelHyperlink = new Hyperlink(comp, SWT.NONE);
            labelHyperlink.setText(getLabel());
         } else {
            labelHyperlink = toolkit.createHyperlink(comp, getLabel() + (isIncludeColon() ? ":" : ""), SWT.NONE);
         }
         labelHyperlink.setToolTipText(Strings.isValid(getToolTip()) ? getToolTip() : "Select to Modify");
         labelHyperlink.setLayoutData(new GridData());
         if (getToolTip() != null) {
            labelHyperlink.setToolTipText(getToolTip());
         }
         labelHyperlink.addListener(SWT.MouseUp, new Listener() {
            @Override
            public void handleEvent(Event event) {
               if (event.button == 1) {
                  PriorityDialog dialog = new PriorityDialog(null, getPriorities());
                  if (dialog.open() == Window.OK) {
                     selected = dialog.getSelected();
                     handleSelected(selected);
                  }
               }
            }

         });
      } else {
         if (toolkit == null) {
            labelWidget = new Label(comp, SWT.NONE);
         } else {
            labelWidget = toolkit.createLabel(comp, getLabel() + ":", SWT.NONE);
         }
         labelWidget.setLayoutData(new GridData());
      }

      GridData gd = new GridData();
      if (isFillHorizontally()) {
         gd.grabExcessHorizontalSpace = true;
         gd.horizontalAlignment = SWT.FILL;
      }

      valueLabel = new Label(comp, SWT.NONE);
      valueLabel.setText(getLabel() + ":");
      if (toolkit != null) {
         toolkit.adapt(valueLabel, false, false);
      }
      valueLabel.setLayoutData(gd);
      if (getToolTip() != null) {
         valueLabel.setToolTipText(getToolTip());
      }

      refresh();
   }

   protected List<Priorities> getPriorities() {
      return priorities;
   }

   protected void handleSelected(Priorities selected) {
      refresh();
      notifyXModifiedListeners();
   }

   @Override
   public void refresh() {
      if (!Widgets.isAccessible(comp)) {
         return;
      }
      if (Widgets.isAccessible(valueLabel)) {
         if (getCurrentValue().equals(valueLabel.getText())) {
            return;
         }
         valueLabel.setText(getCurrentValue());
         valueLabel.update();
         valueLabel.getParent().update();
         valueLabel.getParent().getParent().layout();
      }
      validate();
   }

   @Override
   public String toHTML(String labelFont) {
      return AHTML.getLabelValueStr(AHTML.LABEL_FONT, getLabel(), getCurrentValue());
   }

   @Override
   public Control getControl() {
      if (labelWidget != null) {
         return labelWidget;
      } else if (labelHyperlink != null) {
         return labelHyperlink;
      }
      return comp;
   }

   public boolean isIncludeColon() {
      return includeColon;
   }

   public void setIncludeColon(boolean includeColon) {
      this.includeColon = includeColon;
   }

   public void addLabelWidgetListener(MouseListener listener) {
      if (Widgets.isAccessible(labelHyperlink)) {
         labelHyperlink.addMouseListener(listener);
      }
   }

   public void addLabelMouseListener(MouseListener listener) {
      if (Widgets.isAccessible(labelHyperlink)) {
         labelHyperlink.addMouseListener(listener);
      }
   }

   @Override
   public Label getLabelWidget() {
      return null;
   }

   public Hyperlink getLabelHyperlink() {
      return labelHyperlink;
   }

   public void setSelected(String name) {
      for (Priorities pri : priorities) {
         if (name.equals(pri.name())) {
            selected = pri;
            break;
         }
      }
      refresh();
   }

   public void setSelectable(Collection<Priorities> priorities) {
      this.priorities.clear();
      this.priorities.addAll(priorities);
   }

   @Override
   public IStatus isValid() {
      IStatus status = super.isValid();
      if (isRequiredEntry() && (selected == null || selected == Priorities.None)) {
         status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Must Select Priority");
      }
      return status;
   }

   public Priorities getSelected() {
      return selected;
   }

   public void setSelected(Priorities selected) {
      this.selected = selected;
      refresh();
   }

   public List<Priorities> getSelectable() {
      return priorities;
   }

   @Override
   public Object getData() {
      return selected;
   }

}
