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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.XWidgetParser;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.IDamWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XLabelDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Jeff C. Phillips
 */
public class DynamicXWidgetLayout {

   private Set<DynamicXWidgetLayoutData> datas = new LinkedHashSet<DynamicXWidgetLayoutData>();
   private Map<String, DynamicXWidgetLayoutData> nameToLayoutData = new HashMap<String, DynamicXWidgetLayoutData>();
   private ArrayList<ArrayList<String>> orRequired = new ArrayList<ArrayList<String>>();
   private ArrayList<ArrayList<String>> xorRequired = new ArrayList<ArrayList<String>>();
   public static String OR_REQUIRED = "OrRequired";
   public static String XOR_REQUIRED = "XOrRequired";
   public static String XWIDGET = "XWidget";
   public static String XWIDGETS_LIST = "xWidgets";
   private final IDynamicWidgetLayoutListener dynamicWidgetLayoutListener;
   private final IXWidgetOptionResolver optionResolver;

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

   public void createBody(FormToolkit toolkit, Composite parent, Artifact artifact, XModifiedListener xModListener, boolean isEditable) {
      Composite attrComp = null;
      if (toolkit != null)
         attrComp = toolkit.createComposite(parent);
      else
         attrComp = new Composite(parent, SWT.NONE);
      GridLayout layout = new GridLayout(1, false);
      layout.marginWidth = layout.marginHeight = 2;
      attrComp.setLayout(layout);
      attrComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      if (toolkit != null) toolkit.paintBordersFor(attrComp);

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
         } else if (xWidgetLayoutData.isHorizontalLabel()) {
            useComp = new Composite(attrComp, SWT.NONE);
            useComp.setLayout(ALayout.getZeroMarginLayout(2, false));
            useComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            if (toolkit != null) toolkit.adapt(useComp);
         }

         XWidget xWidget = xWidgetLayoutData.getXWidget();
         if (!xWidgetLayoutData.getName().equals("")) xWidget.setLabel(xWidgetLayoutData.getName().replaceFirst(
               "^.*?\\.", ""));
         if (xWidgetLayoutData.getToolTip() != null && !xWidgetLayoutData.getToolTip().equals("")) xWidget.setToolTip(xWidgetLayoutData.getToolTip());
         xWidget.setRequiredEntry(xWidgetLayoutData.isRequired());
         if (xWidgetLayoutData.getFill() != DynamicXWidgetLayoutData.Fill.None) {
            if (xWidget instanceof XText) {
               if (xWidgetLayoutData.getFill() == DynamicXWidgetLayoutData.Fill.Horizontally) ((XText) xWidget).setFillHorizontally(true);
               if (xWidgetLayoutData.getFill() == DynamicXWidgetLayoutData.Fill.Vertically) {
                  GridData gd = new GridData(GridData.FILL_BOTH);
                  useComp.setLayoutData(gd);
                  ((XText) xWidget).setFillVertically(true);
               }
               if (xWidgetLayoutData.isHeightSet()) ((XText) xWidget).setHeight(xWidgetLayoutData.getHeight());
            }
         }
         xWidget.setEditable(isEditable);
         if (dynamicWidgetLayoutListener != null) dynamicWidgetLayoutListener.widgetCreating(xWidget, toolkit,
               artifact, this, xModListener, isEditable);
         if (toolkit != null)
            xWidget.createWidgets(toolkit, useComp, 2);
         else
            xWidget.createWidgets(useComp, 2);
         if (xWidgetLayoutData.getFill() == DynamicXWidgetLayoutData.Fill.Vertically) {
            GridData gd = new GridData(GridData.FILL_BOTH);
            gd.minimumHeight = 60;
            ((XText) xWidget).getStyledText().setLayoutData(gd);
         }
         if (artifact != null && (xWidget instanceof IDamWidget)) {
            try {
               ((IDamWidget) xWidget).setArtifact(artifact, xWidgetLayoutData.getLayoutName());
            } catch (IllegalStateException ex) {
               OSEELog.logException(SkynetGuiPlugin.class, ex, true);
            } catch (SQLException ex) {
               OSEELog.logException(SkynetGuiPlugin.class, ex, true);
            }
         }
         if (artifact != null && (xWidget instanceof XLabelDam)) ((XLabelDam) xWidget).setArtifact(artifact,
               xWidgetLayoutData.getLayoutName());
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
   private XModifiedListener refreshRequiredModListener = new XModifiedListener() {
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
            label.setForeground(isComplete ? null : Display.getCurrent().getSystemColor(SWT.COLOR_RED));
         }
      }
      // Handle xorRequired
      for (ArrayList<String> xorReq : xorRequired) {
         // If group is complete, change all to black, else all red
         boolean isComplete = isXOrGroupFromAttrNameComplete(xorReq.iterator().next());
         for (String aName : xorReq) {
            DynamicXWidgetLayoutData layoutData = getLayoutData(aName);
            Label label = layoutData.getXWidget().getLabelWidget();
            label.setForeground(isComplete ? null : Display.getCurrent().getSystemColor(SWT.COLOR_RED));
         }
      }
   }

   public Result isPageComplete() {
      for (DynamicXWidgetLayoutData data : datas) {
         if (!data.getXWidget().isValid()) {
            // Check to see if widget is part of a completed OR or XOR group
            if (!isOrGroupFromAttrNameComplete(data.getLayoutName()) && !isXOrGroupFromAttrNameComplete(data.getLayoutName())) return new Result(
                  "Must Enter \"" + data.getName() + "\"");
         }
      }
      return Result.TrueResult;
   }

   public Set<DynamicXWidgetLayoutData> getLayoutDatas() {
      return datas;
   }

   public void addWorkLayoutDatas(List<DynamicXWidgetLayoutData> datas) {
      // remove old datas before adding new ones.
      this.datas.clear();
      this.datas.addAll(datas);
   }

   public DynamicXWidgetLayoutData getLayoutData(String attrName) {
      for (DynamicXWidgetLayoutData layoutData : datas)
         if (layoutData.getLayoutName().equals(attrName)) return layoutData;
      return null;
   }

   public void processInstructions(Document doc) throws IOException, ParserConfigurationException, SAXException {

      for (String reqStrTag : new String[] {XOR_REQUIRED, OR_REQUIRED}) {
         NodeList nodes = doc.getElementsByTagName(reqStrTag);
         if (nodes.getLength() > 0) {
            for (int y = 0; y < nodes.getLength(); y++) {
               Element element = (Element) nodes.item(y);
               for (int x = 0; x < element.getAttributes().getLength(); x++) {
                  Node node = element.getAttributes().item(x);
                  String nodeName = node.getNodeName();
                  if (nodeName.equals(XWIDGETS_LIST))
                     if (reqStrTag.equals(XOR_REQUIRED)) {
                        processXOrRequired(node.getNodeValue());
                     } else {
                        processOrRequired(node.getNodeValue());
                     }
                  else {
                     SkynetGuiPlugin.getLogger().log(Level.SEVERE,
                           "Unhandled " + reqStrTag + " attribute \"" + nodeName + "\" for " + this);
                     throw new IllegalArgumentException("Unhandled " + reqStrTag + " attribute (see error log)");
                  }
               }
            }
         }
      }
      processLayoutDatas(doc.getDocumentElement());
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
         if (layoutData.getXWidget() != null && layoutData.getXWidget().isValid()) return true;
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
         if (layoutData.getXWidget() != null && layoutData.getXWidget().isValid())
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

      XWidgetParser parser = new XWidgetParser();
      List<DynamicXWidgetLayoutData> attrs = parser.extractlayoutDatas(this, rootElement);
      for (DynamicXWidgetLayoutData attr : attrs) {
         nameToLayoutData.put(attr.getName(), attr);
         datas.add(attr);
      }
   }

   protected void processLayoutDatas(Element element) throws IOException, ParserConfigurationException, SAXException {
      XWidgetParser parser = new XWidgetParser();
      List<DynamicXWidgetLayoutData> layoutDatas = parser.extractlayoutDatas(this, element);
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
}
