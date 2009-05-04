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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.XWidgetParser;
import org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget;
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
import org.eclipse.ui.forms.IManagedForm;
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

   private Composite createComposite(Composite parent, FormToolkit toolkit) {
      return toolkit != null ? toolkit.createComposite(parent, SWT.WRAP) : new Composite(parent, SWT.NONE);
   }

   public void createBody(IManagedForm managedForm, Composite parent, Artifact artifact, XModifiedListener xModListener, boolean isEditable) throws OseeCoreException {
      Composite attrComp = null;

      final FormToolkit toolkit = managedForm != null ? managedForm.getToolkit() : null;

      attrComp = createComposite(parent, toolkit);

      GridLayout layout = new GridLayout(1, false);
      layout.marginWidth = 2;
      layout.marginHeight = 2;
      attrComp.setLayout(layout);
      attrComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      if (toolkit != null) {
         toolkit.adapt(attrComp);
      }

      boolean inChildComposite = false;
      Composite childComp = null;
      // Create Attributes
      for (DynamicXWidgetLayoutData xWidgetLayoutData : getLayoutDatas()) {
         Composite useComp = attrComp;

         if (xWidgetLayoutData.getBeginComposite() > 0) {
            childComp = createComposite(attrComp, toolkit);
            childComp.setLayout(ALayout.getZeroMarginLayout(xWidgetLayoutData.getBeginComposite(), false));
            childComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            if (toolkit != null) {
               toolkit.adapt(childComp);
            }
            inChildComposite = true;
         }
         if (inChildComposite) {
            useComp = childComp;
            if (xWidgetLayoutData.isEndComposite()) {
               inChildComposite = false;
            }
         } else if (xWidgetLayoutData.getXOptionHandler().contains(XOption.HORIZONTAL_LABEL)) {
            useComp = createComposite(attrComp, toolkit);
            useComp.setLayout(ALayout.getZeroMarginLayout(2, false));
            useComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            if (toolkit != null) {
               toolkit.adapt(useComp);
            }
         }

         XWidget xWidget = xWidgetLayoutData.getXWidget();
         xWidgets.add(xWidget);

         if (Strings.isValid(xWidgetLayoutData.getName())) {
            xWidget.setLabel(xWidgetLayoutData.getName().replaceFirst("^.*?\\.", ""));
         }

         if (Strings.isValid(xWidgetLayoutData.getToolTip())) {
            xWidget.setToolTip(xWidgetLayoutData.getToolTip());
         }

         xWidget.setRequiredEntry(xWidgetLayoutData.isRequired());
         xWidget.setEditable(xWidgetLayoutData.getXOptionHandler().contains(XOption.EDITABLE) && isEditable);

         if (dynamicWidgetLayoutListener != null) {
            dynamicWidgetLayoutListener.widgetCreating(xWidget, toolkit, artifact, this, xModListener, isEditable);
         }

         if (artifact != null && (xWidget instanceof IArtifactWidget)) {
            try {
               ((IArtifactWidget) xWidget).setArtifact(artifact, xWidgetLayoutData.getStorageName());
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }

         if (xWidget instanceof XText) {
            XText xText = (XText) xWidget;
            if (xWidgetLayoutData.getXOptionHandler().contains(XOption.FILL_HORIZONTALLY)) {
               xText.setFillHorizontally(true);
            }
            if (xWidgetLayoutData.getXOptionHandler().contains(XOption.FILL_VERTICALLY)) {
               xText.setFillVertically(true);
            }
         }

         xWidget.createWidgets(managedForm, useComp, 2);

         if (xWidget instanceof XText) {
            XText xText = (XText) xWidget;
            if (xWidgetLayoutData.getXOptionHandler().contains(XOption.FILL_HORIZONTALLY) && xWidgetLayoutData.getXOptionHandler().contains(
                  XOption.FILL_VERTICALLY)) {
               GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
               gd.minimumWidth = 60;
               gd.minimumHeight = 60;
               useComp.setLayoutData(gd);

               gd = new GridData(SWT.FILL, SWT.FILL, true, true);
               gd.minimumWidth = 60;
               gd.minimumHeight = 60;
               xText.getStyledText().setLayoutData(gd);
            } else if (xWidgetLayoutData.getXOptionHandler().contains(XOption.FILL_HORIZONTALLY)) {
               GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
               gd.minimumWidth = 60;
               useComp.setLayoutData(gd);

               gd = new GridData(SWT.FILL, SWT.FILL, true, false);
               gd.minimumWidth = 60;
               xText.getStyledText().setLayoutData(gd);
            } else if (xWidgetLayoutData.getXOptionHandler().contains(XOption.FILL_VERTICALLY)) {
               GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
               gd.minimumHeight = 60;
               useComp.setLayoutData(gd);

               gd = new GridData(SWT.FILL, SWT.FILL, true, true);
               gd.minimumHeight = 60;
               xText.getStyledText().setLayoutData(gd);
            }

            if (xWidgetLayoutData.isHeightSet()) {
               xText.setHeight(xWidgetLayoutData.getHeight());
            }
         }
         useComp.layout();

         if (xModListener != null) {
            xWidget.addXModifiedListener(xModListener);
         }

         xWidget.addXModifiedListener(refreshRequiredModListener);

         if (dynamicWidgetLayoutListener != null) {
            dynamicWidgetLayoutListener.widgetCreated(xWidget, toolkit, artifact, this, xModListener, isEditable);
            dynamicWidgetLayoutListener.createXWidgetLayoutData(xWidgetLayoutData, xWidget, toolkit, artifact,
                  xModListener, isEditable);
         }
      }

      Display.getDefault().asyncExec(new Runnable() {

         public void run() {
            for (DynamicXWidgetLayoutData xWidgetLayoutData : getLayoutDatas()) {
               xWidgetLayoutData.getXWidget().validate();
            }
            refreshOrAndXOrRequiredFlags();
         }
      });
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

   public IStatus isPageComplete() {
      for (DynamicXWidgetLayoutData data : datas) {
         IStatus valid = data.getXWidget().isValid();
         if (!valid.isOK()) {
            // Check to see if widget is part of a completed OR or XOR group
            if (!isOrGroupFromAttrNameComplete(data.getStorageName()) && !isXOrGroupFromAttrNameComplete(data.getStorageName())) return valid;
         }
      }
      return Status.OK_STATUS;
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
         if (layoutData.getXWidget() != null && layoutData.getXWidget().isValid().isOK()) return true;
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
         if (layoutData.getXWidget() != null && layoutData.getXWidget().isValid().isOK())
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
