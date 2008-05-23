/*
 * Created on May 22, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
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
   private XMultiXWidgetFactory xMultiXWidgetFactory;

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
      final Group group = new Group(parent, SWT.BORDER);
      group.setLayout(new GridLayout(1, true));
      GridData gridData = new GridData();
      gridData.horizontalSpan = horizontalSpan;
      group.setLayoutData(gridData);
      if (isDisplayLabel()) group.setText(getLabel());
      Label addLabel = new Label(group, SWT.NONE);
      Image image = SkynetGuiPlugin.getInstance() != null ? SkynetGuiPlugin.getInstance().getImage("add.gif") : null;
      if (image != null)
         addLabel.setImage(image);
      else
         addLabel.setText("add");
      addLabel.addListener(SWT.MouseUp, new Listener() {
         public void handleEvent(org.eclipse.swt.widgets.Event event) {
            System.out.println("Add widget ");
            XWidget xWidget = xMultiXWidgetFactory.addXWidget();
            createWidgetControlComposite(xWidget, group);
            xWidgets.add(xWidget);
            group.layout();
            group.getParent().layout();
         };
      });

      for (final XWidget xWidget : xWidgets) {
         createWidgetControlComposite(xWidget, group);
      }
   }

   public void createWidgetControlComposite(final XWidget xWidget, final Composite parent) {
      final Composite controlComp = new Composite(parent, SWT.NONE);
      controlComp.setLayout(new GridLayout(3, false));
      controlComp.setLayoutData(new GridData());

      xWidget.createWidgets(controlComp, 1);

      Label deleteLabel = new Label(controlComp, SWT.NONE);
      Image image = SkynetGuiPlugin.getInstance() != null ? SkynetGuiPlugin.getInstance().getImage("delete.gif") : null;
      if (image != null)
         deleteLabel.setImage(image);
      else
         deleteLabel.setText("delete");
      deleteLabel.addListener(SWT.MouseUp, new Listener() {
         public void handleEvent(org.eclipse.swt.widgets.Event event) {
            System.out.println("Delete widget " + xWidget);
            xWidget.dispose();
            xWidgets.remove(xWidget);
            controlComp.dispose();
            parent.layout();
         };
      });
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
   public List<XWidget> getXWidgets() {
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
