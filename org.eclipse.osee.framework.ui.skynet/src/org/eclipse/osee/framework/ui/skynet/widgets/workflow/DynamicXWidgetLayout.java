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
package org.eclipse.osee.framework.ui.skynet.widgets.workflow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.XWidgetParser;
import org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XLabelDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XOption;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * @author Jeff C. Phillips
 */
public class DynamicXWidgetLayout {

   private final Set<DynamicXWidgetLayoutData> datas = new LinkedHashSet<DynamicXWidgetLayoutData>();
   private final Map<String, DynamicXWidgetLayoutData> nameToLayoutData =
         new HashMap<String, DynamicXWidgetLayoutData>();
   private final ArrayList<ArrayList<String>> orRequired = new ArrayList<ArrayList<String>>();
   private final ArrayList<ArrayList<String>> xorRequired = new ArrayList<ArrayList<String>>();
   public static String OR_REQUIRED = "OrRequired";
   public static String XOR_REQUIRED = "XOrRequired";
   public static String XWIDGET = "XWidget";
   public static String XWIDGETS_LIST = "xWidgets";
   private final IDynamicWidgetLayoutListener dynamicWidgetLayoutListener;
   private final IXWidgetOptionResolver optionResolver;
   private final List<XWidget> xWidgets = new ArrayList<XWidget>();

   public DynamicXWidgetLayout() {
      this(null, new DefaultXWidgetOptionResolver());
   }

   public DynamicXWidgetLayout(IDynamicWidgetLayoutListener dynamicWidgetLayoutListener, IXWidgetOptionResolver optionResolver) {
      this.dynamicWidgetLayoutListener = dynamicWidgetLayoutListener;
      this.optionResolver = optionResolver;
   }

   public void dispose() {
      for (DynamicXWidgetLayoutData layoutData : getLayoutDatas()) {
         layoutData.getXWidget().dispose();
      }
   }

   public void createBody(FormToolkit toolkit, Composite parent, Artifact artifact, XModifiedListener xModListener, boolean isEditable) throws OseeCoreException {
      Composite attrComp = null;
      if (toolkit != null)
         attrComp = toolkit.createComposite(parent);
      else
         attrComp = new Composite(parent, SWT.NONE);
      GridLayout layout = new GridLayout(1, false);
      layout.marginWidth = layout.marginHeight = 2;
      attrComp.setLayout(layout);
      attrComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      if (toolkit != null) toolkit.adapt(attrComp);

      boolean inChildComposite = false;
      Composite childComp = null;
      // Create Attributes
      for (DynamicXWidgetLayoutData xWidgetLayoutData : getLayoutDatas()) {
         Composite useComp = attrComp;

         if (xWidgetLayoutData.getBeginComposite() > 0) {
            childComp = new Composite(attrComp, SWT.NONE);
            childComp.setLayout(ALayout.getZeroMarginLayout(xWidgetLayoutData.getBeginComposite(), false));
            childComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            if (toolkit != null) toolkit.adapt(childComp);
            inChildComposite = true;
         }
         if (inChildComposite) {
            useComp = childComp;
            if (xWidgetLayoutData.isEndComposite()) inChildComposite = false;
         } else if (xWidgetLayoutData.getXOptionHandler().contains(XOption.HORIZONTAL_LABEL)) {
            useComp = new Composite(attrComp, SWT.NONE);
            useComp.setLayout(ALayout.getZeroMarginLayout(2, false));
            useComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            if (toolkit != null) toolkit.adapt(useComp);
         }

         XWidget xWidget = xWidgetLayoutData.getXWidget();
         xWidgets.add(xWidget);
         if (!xWidgetLayoutData.getName().equals("")) xWidget.setLabel(xWidgetLayoutData.getName().replaceFirst(
               "^.*?\\.", ""));
         if (xWidgetLayoutData.getToolTip() != null && !xWidgetLayoutData.getToolTip().equals("")) xWidget.setToolTip(xWidgetLayoutData.getToolTip());
         xWidget.setRequiredEntry(xWidgetLayoutData.isRequired());
         if (xWidgetLayoutData.getXOptionHandler().contains(XOption.FILL_HORIZONTALLY) || xWidgetLayoutData.getXOptionHandler().contains(
               XOption.FILL_VERTICALLY)) {
            if (xWidget instanceof XText) {
               if (xWidgetLayoutData.getXOptionHandler().contains(XOption.FILL_HORIZONTALLY)) ((XText) xWidget).setFillHorizontally(true);
               if (xWidgetLayoutData.getXOptionHandler().contains(XOption.FILL_VERTICALLY)) {
                  GridData gd = new GridData(GridData.FILL_BOTH);
                  useComp.setLayoutData(gd);
                  ((XText) xWidget).setFillVertically(true);
               }
               if (xWidgetLayoutData.isHeightSet()) ((XText) xWidget).setHeight(xWidgetLayoutData.getHeight());
            }
         }
         xWidget.setEditable(xWidgetLayoutData.getXOptionHandler().contains(XOption.EDITABLE) && isEditable);
         if (dynamicWidgetLayoutListener != null) dynamicWidgetLayoutListener.widgetCreating(xWidget, toolkit,
               artifact, this, xModListener, isEditable);
         if (artifact != null && (xWidget instanceof IArtifactWidget)) {
            try {
               ((IArtifactWidget) xWidget).setArtifact(artifact, xWidgetLayoutData.getStorageName());
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
         if (toolkit != null)
            xWidget.createWidgets(toolkit, useComp, 2);
         else
            xWidget.createWidgets(useComp, 2);
         if (xWidgetLayoutData.getXOptionHandler().contains(XOption.FILL_VERTICALLY) && (xWidget instanceof XText)) {
            GridData gd = new GridData(GridData.FILL_BOTH);
            gd.minimumHeight = 60;
            ((XText) xWidget).getStyledText().setLayoutData(gd);
         }
         if (artifact != null && (xWidget instanceof XLabelDam)) ((XLabelDam) xWidget).setArtifact(artifact,
               xWidgetLayoutData.getStorageName());
         if (xModListener != null) xWidget.addXModifiedListener(xModListener);
         xWidget.addXModifiedListener(refreshRequiredModListener);

         if (dynamicWidgetLayoutListener != null) {
            dynamicWidgetLayoutListener.widgetCreated(xWidget, toolkit, artifact, this, xModListener, isEditable);
            dynamicWidgetLayoutListener.createXWidgetLayoutData(xWidgetLayoutData, xWidget, toolkit, artifact,
                  xModListener, isEditable);
         }
         xWidget.setLabelError();
      }
      refreshOrAndXOrRequiredFlags();
   }
   private final XModifiedListener refreshRequiredModListener = new XModifiedListener() {
      public void widgetModified(XWidget widget) {
         refreshOrAndXOrRequiredFlags();
      }
   };

   /**
    * Required flags are set per XWidget and the labels change from Red to Black when the widget has been edited
    * successfully. When a page is made up of two or more widgets that need to work together, these required flags need
    * to be set/unset whenever a widget from the group gets modified.
    */
   private void refreshOrAndXOrRequiredFlags() {
      // Handle orRequired
      for (ArrayList<String> orReq : orRequired) {
         // If group is complete, change all to black, else all red
         boolean isComplete = isOrGroupFromAttrNameComplete(orReq.iterator().next());
         for (String aName : orReq) {
            DynamicXWidgetLayoutData layoutData = getLayoutData(aName);
            Label label = layoutData.getXWidget().getLabelWidget();
            if (label != null && !label.isDisposed()) label.setForeground(isComplete ? null : Display.getCurrent().getSystemColor(
                  SWT.COLOR_RED));
         }
      }
      // Handle xorRequired
      for (ArrayList<String> xorReq : xorRequired) {
         // If group is complete, change all to black, else all red
         boolean isComplete = isXOrGroupFromAttrNameComplete(xorReq.iterator().next());
         for (String aName : xorReq) {
            DynamicXWidgetLayoutData layoutData = getLayoutData(aName);
            Label label = layoutData.getXWidget().getLabelWidget();
            if (label != null && !label.isDisposed()) label.setForeground(isComplete ? null : Display.getCurrent().getSystemColor(
                  SWT.COLOR_RED));
         }
      }
   }

   public Result isPageComplete() {
      for (DynamicXWidgetLayoutData data : datas) {
         Result valid = data.getXWidget().isValid();
         if (valid.isFalse()) {
            // Check to see if widget is part of a completed OR or XOR group
            if (!isOrGroupFromAttrNameComplete(data.getStorageName()) && !isXOrGroupFromAttrNameComplete(data.getStorageName())) return valid;
         }
      }
      return Result.TrueResult;
   }

   public Set<DynamicXWidgetLayoutData> getLayoutDatas() {
      return datas;
   }

   public void setLayoutDatas(List<DynamicXWidgetLayoutData> datas) {
      this.datas.clear();
      for (DynamicXWidgetLayoutData data : datas) {
         data.setDynamicXWidgetLayout(this);
         this.datas.add(data);
      }
   }

   public void addWorkLayoutDatas(List<DynamicXWidgetLayoutData> datas) {
      this.datas.addAll(datas);
   }

   public DynamicXWidgetLayoutData getLayoutData(String attrName) {
      for (DynamicXWidgetLayoutData layoutData : datas)
         if (layoutData.getStorageName().equals(attrName)) return layoutData;
      return null;
   }

   public boolean isOrRequired(String attrName) {
      return (getOrRequiredGroup(attrName)).size() > 0;
   }

   public boolean isXOrRequired(String attrName) {
      return (getXOrRequiredGroup(attrName)).size() > 0;
   }

   public ArrayList<String> getOrRequiredGroup(String attrName) {
      return getRequiredGroup(orRequired, attrName);
   }

   public ArrayList<String> getXOrRequiredGroup(String attrName) {
      return getRequiredGroup(xorRequired, attrName);
   }

   private ArrayList<String> getRequiredGroup(ArrayList<ArrayList<String>> requiredList, String attrName) {
      for (ArrayList<String> list : requiredList)
         for (String aName : list)
            if (aName.equals(attrName)) return list;
      return new ArrayList<String>();
   }

   /**
    * @param name
    * @return true if ANY item in group is entered
    */
   public boolean isOrGroupFromAttrNameComplete(String name) {
      for (String aName : getOrRequiredGroup(name)) {
         DynamicXWidgetLayoutData layoutData = getLayoutData(aName);
         if (layoutData.getXWidget() != null && layoutData.getXWidget().isValid().isTrue()) return true;
      }
      return false;
   }

   /**
    * @param attrName
    * @return true if only ONE item in group is entered
    */
   public boolean isXOrGroupFromAttrNameComplete(String attrName) {
      boolean oneFound = false;
      for (String aName : getXOrRequiredGroup(attrName)) {
         DynamicXWidgetLayoutData layoutData = getLayoutData(aName);
         if (layoutData.getXWidget() != null && layoutData.getXWidget().isValid().isTrue())
         // If already found one, return false
         if (oneFound)
            return false;
         else
            oneFound = true;
      }
      return oneFound;
   }

   protected void processOrRequired(String instr) {
      ArrayList<String> names = new ArrayList<String>();
      for (String attr : instr.split(";"))
         if (!attr.contains("[ \\s]*")) names.add(attr);
      orRequired.add(names);
   }

   protected void processXOrRequired(String instr) {
      ArrayList<String> names = new ArrayList<String>();
      for (String attr : instr.split(";"))
         if (!attr.contains("[ \\s]*")) names.add(attr);
      xorRequired.add(names);
   }

   protected void processlayoutDatas(String xWidgetXml) throws IOException, ParserConfigurationException, SAXException {
      Document document = Jaxp.readXmlDocument(xWidgetXml);
      Element rootElement = document.getDocumentElement();

      List<DynamicXWidgetLayoutData> attrs = XWidgetParser.extractlayoutDatas(this, rootElement);
      for (DynamicXWidgetLayoutData attr : attrs) {
         nameToLayoutData.put(attr.getName(), attr);
         datas.add(attr);
      }
   }

   protected void processLayoutDatas(Element element) throws IOException, ParserConfigurationException, SAXException {
      List<DynamicXWidgetLayoutData> layoutDatas = XWidgetParser.extractlayoutDatas(this, element);
      for (DynamicXWidgetLayoutData layoutData : layoutDatas) {
         nameToLayoutData.put(layoutData.getName(), layoutData);
         datas.add(layoutData);
      }
   }

   /**
    * @return the optionResolver
    */
   public IXWidgetOptionResolver getOptionResolver() {
      return optionResolver;
   }

   /**
    * @return the xWidgets
    */
   public List<XWidget> getXWidgets() {
      return xWidgets;
   }
}
