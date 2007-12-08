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
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * @author Donald G. Dunne
 */
public class WorkPage implements IDynamicWidgetLayoutListener {

   private String id = GUID.generateGuidStr();
   private String name;
   private ArrayList<WorkPage> fromPages = new ArrayList<WorkPage>();
   private ArrayList<WorkPage> toPages = new ArrayList<WorkPage>();
   private ArrayList<WorkPage> returnPages = new ArrayList<WorkPage>();
   private WorkPage defaultToPage;
   protected DynamicXWidgetLayout dynamicXWidgetLayout;

   /**
    * @param instructionLines input lines of WorkAttribute declarations
    */
   public WorkPage(String name, String id, String xWidgetsXml, IXWidgetOptionResolver optionResolver) {
      super();
      this.name = name;
      if (id != null && !id.equals("")) this.id = id;
      dynamicXWidgetLayout = new DynamicXWidgetLayout(this, optionResolver);
      try {
         if (xWidgetsXml != null) processLayoutDatas(xWidgetsXml);
      } catch (Exception ex) {
         SkynetGuiPlugin.getLogger().log(Level.SEVERE, "Error processing attributes", ex);
      }
   }

   public WorkPage(String xWidgetsXml, IXWidgetOptionResolver optionResolver) {
      this("", "", xWidgetsXml, optionResolver);
   }

   public WorkPage(IXWidgetOptionResolver optionResolver) {
      this("", "", null, optionResolver);
   }

   public void widgetCreating(XWidget xWidget, FormToolkit toolkit, Artifact art, WorkPage page, XModifiedListener xModListener, boolean isEditable) {
   }

   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, WorkPage page, XModifiedListener xModListener, boolean isEditable) {
   }

   public void createXWidgetLayoutData(DynamicXWidgetLayoutData workAttr, XWidget xWidget, FormToolkit toolkit, Artifact art, XModifiedListener xModListener, boolean isEditable) {
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.widgets.workflow.IDynamicWidgetLayoutListener#widgetCreated(org.eclipse.osee.framework.ui.skynet.widgets.XWidget,
    *      org.eclipse.ui.forms.widgets.FormToolkit,
    *      org.eclipse.osee.framework.skynet.core.artifact.Artifact,
    *      org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayout,
    *      org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener, boolean)
    */
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, DynamicXWidgetLayout dynamicXWidgetLayout, XModifiedListener xModListener, boolean isEditable) {
      widgetCreated(xWidget, toolkit, art, this, xModListener, isEditable);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.widgets.workflow.IDynamicWidgetLayoutListener#widgetCreating(org.eclipse.osee.framework.ui.skynet.widgets.XWidget,
    *      org.eclipse.ui.forms.widgets.FormToolkit,
    *      org.eclipse.osee.framework.skynet.core.artifact.Artifact,
    *      org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayout,
    *      org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener, boolean)
    */
   public void widgetCreating(XWidget xWidget, FormToolkit toolkit, Artifact art, DynamicXWidgetLayout dynamicXWidgetLayout, XModifiedListener xModListener, boolean isEditable) {
      widgetCreating(xWidget, toolkit, art, this, xModListener, isEditable);
   }

   public void dispose() {
      for (DynamicXWidgetLayoutData layoutData : getlayoutDatas()) {
         layoutData.getXWidget().dispose();
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) {
      if (obj instanceof WorkPage) return getId().equals(((WorkPage) obj).getId());
      return false;
   }

   public void createBody(FormToolkit toolkit, Composite parent, Artifact artifact, XModifiedListener xModListener, boolean isEditable) {
      dynamicXWidgetLayout.createBody(toolkit, parent, artifact, xModListener, isEditable);
   }

   public Result isPageComplete() {
      for (DynamicXWidgetLayoutData layoutData : dynamicXWidgetLayout.getLayoutDatas()) {
         if (!layoutData.getXWidget().isValid()) {
            // Check to see if widget is part of a completed OR or XOR group
            if (!dynamicXWidgetLayout.isOrGroupFromAttrNameComplete(layoutData.getLayoutName()) && !dynamicXWidgetLayout.isXOrGroupFromAttrNameComplete(layoutData.getLayoutName())) return new Result(
                  "Must Enter \"" + layoutData.getName() + "\"");
         }
      }
      return Result.TrueResult;
   }

   public String getHtml(String backgroundColor) {
      StringBuffer sb = new StringBuffer();
      sb.append(AHTML.startBorderTable(100, backgroundColor, getName()));
      for (DynamicXWidgetLayoutData layoutData : dynamicXWidgetLayout.getLayoutDatas()) {
         sb.append(layoutData.getXWidget().toHTML(AHTML.LABEL_FONT) + AHTML.newline());
      }
      sb.append(AHTML.endBorderTable());
      return sb.toString();
   }

   public String toString() {
      StringBuffer sb = new StringBuffer(name + (id != null ? " (" + id + ") " : "") + "\n");
      for (WorkPage page : toPages) {
         sb.append("-> " + page.name + (returnPages.contains(toPages) ? " (return)" : "") + "\n");
      }
      return sb.toString();
   }

   public void addFromPage(WorkPage page) {
      fromPages.add(page);
   }

   public void addToPage(WorkPage page, boolean returnPage) {
      toPages.add(page);
      if (returnPage) returnPages.add(page);
   }

   public Set<DynamicXWidgetLayoutData> getlayoutDatas() {
      return dynamicXWidgetLayout.getLayoutDatas();
   }

   public void addLayoutDatas(List<DynamicXWidgetLayoutData> datas) {
      dynamicXWidgetLayout.addWorkLayoutDatas(datas);
   }

   public DynamicXWidgetLayoutData getLayoutData(String layoutName) {
      return dynamicXWidgetLayout.getLayoutData(layoutName);
   }

   public void processInstructions(Document doc) throws IOException, ParserConfigurationException, SAXException {
      processLayoutDatas(doc.getDocumentElement());
   }

   protected void processLayoutDatas(String xWidgetXml) throws IOException, ParserConfigurationException, SAXException {
      dynamicXWidgetLayout.processlayoutDatas(xWidgetXml);
   }

   protected void processLayoutDatas(Element element) throws IOException, ParserConfigurationException, SAXException {
      dynamicXWidgetLayout.processLayoutDatas(element);
   }

   public String getName() {
      return name;
   }

   public String getId() {
      return id;
   }

   /**
    * @return Returns the fromPages.
    */
   public ArrayList<WorkPage> getFromPages() {
      return fromPages;
   }

   /**
    * @return Returns the toPages.
    */
   public ArrayList<WorkPage> getToPages() {
      return toPages;
   }

   public boolean isReturnPage(WorkPage page) {
      return returnPages.contains(page);
   }

   /**
    * @return Returns the defaultToPage.
    */
   public WorkPage getDefaultToPage() {
      return defaultToPage;
   }

   /**
    * @param defaultToPage The defaultToPage to set.
    */
   public void setDefaultToPage(WorkPage defaultToPage) {
      this.defaultToPage = defaultToPage;
   }

   /**
    * @param id The id to set.
    */
   public void setId(String id) {
      this.id = id;
   }

   /**
    * @param name The name to set.
    */
   public void setName(String name) {
      this.name = name;
   }

   /**
    * @return the returnPages
    */
   public ArrayList<WorkPage> getReturnPages() {
      return returnPages;
   }

}
