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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.swt.SWT;
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
public class XComboViewer extends XWidget {

   private ComboViewer comboViewer;
   private Composite parent;
   private Composite composite;
   private boolean grabHorizontal = false;

   protected SelectionListener listListener = new SelectionListener() {

      public void widgetSelected(SelectionEvent e) {
         handleSelection();
      }

      public void widgetDefaultSelected(SelectionEvent e) {
         widgetSelected(e);
      }
   };
   private Collection<Object> input;
   private IContentProvider contentProvider;
   private ILabelProvider labelProvider;
   private ViewerSorter sorter;
   private int widthHint;
   private int heightHint;

   public XComboViewer(String displayLabel) {
      this(displayLabel, "list", "");
   }

   public XComboViewer() {
      this("List", "list", "");
   }

   public XComboViewer(String displayLabel, String xmlRoot, String xmlSubRoot) {
      super(displayLabel, xmlRoot, xmlSubRoot);
      setReportType(XWidget.RPT_SINGLE_LINE);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#getControl()
    */
   @Override
   public Control getControl() {
      return comboViewer.getControl();
   }

   public void setContentProvider(IContentProvider contentProvider) {
      this.contentProvider = contentProvider;
   }

   public void setLabelProvider(ILabelProvider labelProvider) {
      this.labelProvider = labelProvider;
   }

   public void setSorter(ViewerSorter sorter) {
      this.sorter = sorter;
   }

   public void setInput(Collection<Object> input) {
      this.input = input;
      if (comboViewer != null) comboViewer.setInput(input);
   }

   public void addSelectionChangedListener(ISelectionChangedListener listener) {
      comboViewer.addSelectionChangedListener(listener);
   }

   public void setHints(int widthHint, int heightHint) {
      this.widthHint = widthHint;
      this.heightHint = heightHint;
   }

   /**
    * Create List Widgets. Widgets Created: List: horizonatalSpan takes up 2 columns; horizontalSpan must be >=2
    */
   public void createWidgets(Composite parent, int horizontalSpan) {

      this.parent = parent;
      composite = null;

      if (!verticalLabel && (horizontalSpan < 2)) {
         horizontalSpan = 2;
      } else if (verticalLabel) {
         horizontalSpan = 1;
      }

      if (displayLabel && verticalLabel) {
         composite = new Composite(parent, SWT.NONE);
         int numColumns = 1;
         GridLayout gridLayout = new GridLayout();
         gridLayout.numColumns = numColumns;
         composite.setLayout(gridLayout);
         GridData gd = new GridData(GridData.FILL_BOTH);
         gd.horizontalSpan = horizontalSpan;
         composite.setLayoutData(gd);
      } else {
         composite = parent;
      }

      // Create List Widgets
      if (displayLabel) {
         labelWidget = new Label(composite, SWT.NONE);
         labelWidget.setText(label + ":");
         if (toolTip != null) {
            labelWidget.setToolTipText(toolTip);
         }
      }

      comboViewer = new ComboViewer(composite, SWT.NONE);
      comboViewer.setContentProvider(contentProvider);
      comboViewer.setLabelProvider(labelProvider);
      if (sorter != null) comboViewer.setSorter(sorter);
      comboViewer.setInput(input);
      comboViewer.getCombo().addSelectionListener(new SelectionListener() {
         public void widgetDefaultSelected(SelectionEvent e) {
         }

         public void widgetSelected(SelectionEvent e) {
            handleSelection();
         }
      });
      GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
      if (grabHorizontal) gd.grabExcessHorizontalSpace = true;
      if (widthHint > 0) gd.widthHint = widthHint;
      if (heightHint > 0) gd.heightHint = heightHint;
      comboViewer.getCombo().setLayoutData(gd);
      comboViewer.getCombo().addSelectionListener(listListener);
      updateListWidget();
   }

   @Override
   public void dispose() {
      labelWidget.dispose();
      comboViewer.getCombo().dispose();
      if (composite != null && !composite.isDisposed()) composite.dispose();
      if (parent != null && !parent.isDisposed()) parent.layout();
   }

   public void setFocus() {
   }

   /**
    * Don't need this since overriding toReport and toXml
    */
   public String getXmlData() {
      return "";
   }

   /**
    * Don't need this since overriding setFromXml
    */
   public void setXmlData(String str) {
      return;
   }

   private void handleSelection() {
      setLabelError();
      notifyXModifiedListeners();
   }

   public void refresh() {
      updateListWidget();
   }

   public void addSelectionListener(SelectionListener selectionListener) {
      comboViewer.getCombo().addSelectionListener(selectionListener);
   }

   @SuppressWarnings("unchecked")
   public Object getSelected() {
      IStructuredSelection selection = (IStructuredSelection) comboViewer.getSelection();
      Iterator iter = selection.iterator();
      if (iter.hasNext()) return iter.next();
      return null;
   }

   public void setFromXml(String xml) {
   }

   public Combo getCombo() {
      return comboViewer.getCombo();
   }

   protected void updateListWidget() {
      comboViewer.refresh();
      setLabelError();
   }

   public void add(Object obj) {
      input.add(obj);
   }

   public void add(Object[] names) {
      for (Object name : names) {
         add(name);
      }
   }

   public void add(Collection<String> names) {
      input.addAll(names);
   }

   public void setSelected(ArrayList<Object> selected) {
      comboViewer.setSelection(new StructuredSelection(selected.toArray(new Object[selected.size()])));
      updateListWidget();
   }

   public Result isValid() {
      if (!requiredEntry) return Result.TrueResult;
      Object selected = getSelected();
      if (selected == null && isRequiredEntry()) return new Result("Must select " + getLabel());
      return Result.TrueResult;
   }

   public String getReportData() {
      String s = "\n";
      Object obj = getSelected();
      s = s + "       - " + obj + "\n";
      s = s.replaceAll("\n+$", "");
      return s;
   }

   public String toXml() {
      return toXml(xmlRoot, xmlSubRoot);
   }

   public String toXml(String xmlRoot, String xmlSubRoot) {
      return "";
   }

   public String toHTML(String labelFont) {
      String s = "<dl><dt>" + AHTML.getLabelStr(labelFont, label + ": ") + "<dt><ul type=\"disc\">";
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

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#getData()
    */
   @Override
   public Object getData() {
      return getSelected();
   }

}