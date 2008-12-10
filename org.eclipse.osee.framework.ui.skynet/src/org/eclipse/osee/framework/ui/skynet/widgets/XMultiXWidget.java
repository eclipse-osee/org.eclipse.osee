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
import java.util.List;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

/**
 * @author Donald G. Dunne
 */
public class XMultiXWidget extends XWidget {

   protected List<XWidget> xWidgets = new ArrayList<XWidget>();
   protected XMultiXWidgetFactory xMultiXWidgetFactory;
   protected Group group;
   protected int horizontalSpan;

   /**
    * @param label
    */
   public XMultiXWidget(String label, XMultiXWidgetFactory xMultiXWidgetFactory) {
      super(label);
      this.xMultiXWidgetFactory = xMultiXWidgetFactory;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#createWidgets(org.eclipse.swt.widgets.Composite, int)
    */
   @Override
   public void createWidgets(Composite parent, int horizontalSpan) {
      this.horizontalSpan = horizontalSpan;
      //      parent.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
      group = new Group(parent, SWT.NONE);
      if (toolkit != null) toolkit.adapt(group);
      //      group.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW));
      group.setLayout(new GridLayout(2, false));
      GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
      gridData.horizontalSpan = horizontalSpan;
      group.setLayoutData(gridData);
      if (isDisplayLabel()) group.setText(getLabel() + "(s): ");

      // Create add label / icon
      Label addLabel = new Label(group, SWT.NONE);
      if (toolkit != null) toolkit.adapt(addLabel, true, true);
      Image image = SkynetGuiPlugin.getInstance() != null ? SkynetGuiPlugin.getInstance().getImage("add.gif") : null;
      if (image != null)
         addLabel.setImage(image);
      else
         addLabel.setText("add");
      addLabel.setToolTipText("Add New \"" + getLabel() + "\"");
      addLabel.addListener(SWT.MouseUp, new Listener() {
         public void handleEvent(org.eclipse.swt.widgets.Event event) {
            handleAddXWidget();
         };
      });

      // Create xWidget lines
      for (final XWidget xWidget : xWidgets) {
         createWidgetControlComposite(xWidget, group, 2);
      }
   }

   protected void handleAddXWidget() {
      System.out.println("Add widget ");
      XWidget xWidget = xMultiXWidgetFactory.addXWidget();
      createWidgetControlComposite(xWidget, group, 2);
      if (!xWidgets.contains(xWidget)) xWidgets.add(xWidget);
      xWidget.addXModifiedListener(xModifiedListener);
      group.layout();
      group.getParent().layout();
      notifyXModifiedListeners();
   }

   XModifiedListener xModifiedListener = new XModifiedListener() {
      public void widgetModified(XWidget widget) {
         notifyXModifiedListeners();
      };
   };

   private void createWidgetControlComposite(final XWidget xWidget, final Composite parent, int horizontalSpan) {
      final Composite controlComp = new Composite(parent, SWT.NONE);
      controlComp.setLayout(ALayout.getZeroMarginLayout(4, false));
      GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
      gridData.horizontalSpan = 2;
      controlComp.setLayoutData(gridData);
      if (toolkit != null) toolkit.adapt(controlComp);

      // Add delete label / icon
      Label deleteLabel = new Label(controlComp, SWT.NONE);
      if (toolkit != null) toolkit.adapt(deleteLabel, true, true);
      Image image = SkynetGuiPlugin.getInstance() != null ? SkynetGuiPlugin.getInstance().getImage("delete.gif") : null;
      if (image != null)
         deleteLabel.setImage(image);
      else
         deleteLabel.setText("delete");
      deleteLabel.setToolTipText("Delete \"" + getLabel() + "\"");
      deleteLabel.addListener(SWT.MouseUp, new Listener() {
         public void handleEvent(org.eclipse.swt.widgets.Event event) {
            System.out.println("Delete widget " + xWidget);
            xWidgets.remove(xWidget);
            xWidget.dispose();
            controlComp.dispose();
            parent.layout();
            group.layout();
            group.getParent().layout();
            notifyXModifiedListeners();
         };
      });

      // Add Widget
      xWidget.setFillHorizontally(true);
      xWidget.createWidgets(controlComp, 1);

   }

   public void addXWidget(XWidget xWidget) {
      xWidgets.add(xWidget);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#dispose()
    */
   @Override
   public void dispose() {
      for (XWidget xWidget : xWidgets) {
         xWidget.dispose();
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#getControl()
    */
   @Override
   public Control getControl() {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#getData()
    */
   @Override
   public Object getData() {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#getReportData()
    */
   @Override
   public String getReportData() {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#getXmlData()
    */
   @Override
   public String getXmlData() {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#isValid()
    */
   @Override
   public Result isValid() {
      for (XWidget xWidget : xWidgets) {
         if (xWidget.isValid().isFalse()) return xWidget.isValid();
      }
      return Result.TrueResult;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#refresh()
    */
   @Override
   public void refresh() {
      for (XWidget xWidget : xWidgets) {
         xWidget.refresh();
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#setFocus()
    */
   @Override
   public void setFocus() {
      if (xWidgets.size() > 0) xWidgets.iterator().next().setFocus();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#setXmlData(java.lang.String)
    */
   @Override
   public void setXmlData(String str) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#toHTML(java.lang.String)
    */
   @Override
   public String toHTML(String labelFont) {
      return null;
   }

   /**
    * @return the xWidgets
    */
   public List<XWidget> getXWidgets() throws Exception {
      return xWidgets;
   }

   /**
    * @param widgets the xWidgets to set
    */
   public void setXWidgets(List<XWidget> widgets) {
      xWidgets = widgets;
   }

   /**
    * @param multiXWidgetFactory the xMultiXWidgetFactory to set
    */
   public void setXMultiXWidgetFactory(XMultiXWidgetFactory multiXWidgetFactory) {
      xMultiXWidgetFactory = multiXWidgetFactory;
   }

}
