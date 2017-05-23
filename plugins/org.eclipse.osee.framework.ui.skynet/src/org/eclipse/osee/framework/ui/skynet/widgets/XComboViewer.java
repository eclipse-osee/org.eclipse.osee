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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.ui.skynet.ArtifactLabelProvider;
import org.eclipse.osee.framework.ui.skynet.ToStringViewerSorter;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * @author Donald G. Dunne
 */
public class XComboViewer extends GenericXWidget {
   private final int comboWidgetSWTStyle;
   private ComboViewer comboViewer;
   private Composite parent;
   private Composite composite;
   private boolean grabHorizontal = false;
   private Object selected;

   protected SelectionListener listListener = new SelectionListener() {

      @Override
      public void widgetSelected(SelectionEvent e) {
         handleSelection();
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
         widgetSelected(e);
      }
   };
   private Collection<Object> input;
   private IContentProvider contentProvider;
   private ViewerComparator sorter;
   private int widthHint;
   private int heightHint;

   public XComboViewer(String displayLabel, int comboWidgetSWTStyle) {
      super(displayLabel);
      contentProvider = new ArrayContentProvider();
      this.comboWidgetSWTStyle = comboWidgetSWTStyle;
   }

   @Override
   public Control getControl() {
      return comboViewer.getControl();
   }

   public void setContentProvider(IContentProvider contentProvider) {
      this.contentProvider = contentProvider;
   }

   public void setComparator(ViewerComparator sorter) {
      this.sorter = sorter;
      if (comboViewer != null) {
         comboViewer.setComparator(sorter);
      }
   }

   public void setInput(Collection<Object> input) {
      this.input = input;
      if (comboViewer != null) {
         comboViewer.setInput(input);
      }
   }

   public void addSelectionChangedListener(ISelectionChangedListener listener) {
      comboViewer.addSelectionChangedListener(listener);
   }

   public void setHints(int widthHint, int heightHint) {
      this.widthHint = widthHint;
      this.heightHint = heightHint;
   }

   /**
    * Create List Widgets. <br>
    * <br>
    * Widgets Created:
    * <li>List: horizonatalSpan takes up 2 columns; <br>
    *
    * @param horizontalSpan horizontalSpan must be >=2
    * @param comboWidgetSWTStyle style of the widget providing the combo, usually {@code SWT.READ_ONLY} or
    * {@code SWT.NONE}
    */
   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      this.parent = parent;
      composite = null;

      if (!verticalLabel && horizontalSpan < 2) {
         horizontalSpan = 2;
      } else if (verticalLabel) {
         horizontalSpan = 1;
      }

      if (isDisplayLabel() && verticalLabel) {
         composite = new Composite(parent, SWT.NONE);
         GridLayout gridLayout = new GridLayout();
         gridLayout.numColumns = 1;
         composite.setLayout(gridLayout);
         GridData gd = new GridData(GridData.FILL_BOTH);
         gd.horizontalSpan = horizontalSpan;
         composite.setLayoutData(gd);
      } else {
         composite = parent;
      }

      // Create List Widgets
      if (isDisplayLabel()) {
         labelWidget = new Label(composite, SWT.NONE);
         labelWidget.setText(getLabel() + ":");
         if (getToolTip() != null) {
            labelWidget.setToolTipText(getToolTip());
         }
      }

      comboViewer = new ComboViewer(composite, comboWidgetSWTStyle);
      comboViewer.setContentProvider(contentProvider);
      comboViewer.setLabelProvider(getLabelProvider());
      if (sorter != null) {
         comboViewer.setComparator(sorter);
      } else if (isUseToStringSorter()) {
         comboViewer.setComparator(new ToStringViewerSorter());
      }
      comboViewer.setInput(input);
      comboViewer.getCombo().addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            handleSelection();
         }
      });
      GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
      if (grabHorizontal) {
         gd.grabExcessHorizontalSpace = true;
      }
      if (widthHint > 0) {
         gd.widthHint = widthHint;
      }
      if (heightHint > 0) {
         gd.heightHint = heightHint;
      }
      comboViewer.getCombo().setLayoutData(gd);
      comboViewer.getCombo().addSelectionListener(listListener);
      updateListWidget();
   }

   @Override
   public void dispose() {
      labelWidget.dispose();
      comboViewer.getCombo().dispose();
      if (composite != null && !composite.isDisposed()) {
         composite.dispose();
      }
      if (parent != null && !parent.isDisposed()) {
         parent.layout();
      }
   }

   private void handleSelection() {
      IStructuredSelection selection = (IStructuredSelection) comboViewer.getSelection();
      Iterator<?> iter = selection.iterator();
      if (iter.hasNext()) {
         selected = iter.next();
      } else {
         selected = null;
      }
      validate();
      notifyXModifiedListeners();
   }

   @Override
   public void refresh() {
      updateListWidget();
   }

   public void addSelectionListener(SelectionListener selectionListener) {
      comboViewer.getCombo().addSelectionListener(selectionListener);
   }

   public Object getSelected() {
      return selected;
   }

   public Combo getCombo() {
      return comboViewer.getCombo();
   }

   public ComboViewer getComboViewer() {
      return comboViewer;
   }

   protected void updateListWidget() {
      comboViewer.refresh();
      validate();
   }

   public void add(Object obj) {
      input.add(obj);
   }

   public void add(Object[] names) {
      for (Object name : names) {
         add(name);
      }
   }

   public void setSelected(List<Object> selected) {
      comboViewer.setSelection(new StructuredSelection(selected.toArray(new Object[selected.size()])));
      updateListWidget();
   }

   @Override
   public IStatus isValid() {
      if (!isRequiredEntry()) {
         return Status.OK_STATUS;
      }
      Object selected = getSelected();
      if (selected == null && isRequiredEntry()) {
         return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Must select " + getLabel());
      }
      return Status.OK_STATUS;
   }

   @Override
   public boolean isEmpty() {
      return getSelected() != null;
   }

   @Override
   public String getReportData() {
      String s = "\n";
      Object obj = getSelected();
      s = s + "       - " + obj + "\n";
      s = s.replaceAll("\n+$", "");
      return s;
   }

   @Override
   public String toHTML(String labelFont) {
      String s = "<dl><dt>" + AHTML.getLabelStr(labelFont, getLabel() + ": ") + "<dt><ul type=\"disc\">";
      Object xItem = getSelected();
      s += "<li>" + xItem;
      return s + "</ul></dl>";
   }

   /**
    * @param grabHorizontal The grabHorizontal to set.
    */
   public void setGrabHorizontal(boolean grabHorizontal) {
      this.grabHorizontal = grabHorizontal;
   }

   @Override
   public Object getData() {
      return getSelected();
   }

   @Override
   public ILabelProvider getLabelProvider() {
      if (super.getLabelProvider() != null) {
         return super.getLabelProvider();
      }
      return new ArtifactLabelProvider();
   }

}
