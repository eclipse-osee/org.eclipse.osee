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
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition.TransitionType;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Instantiation of a workpagedefinition for a given workflow. This contains UI components that are specific to the
 * instantiation.
 * 
 * @author Donald G. Dunne
 */
public class WorkPage implements IDynamicWidgetLayoutListener {

   protected DynamicXWidgetLayout dynamicXWidgetLayout;
   protected final WorkPageDefinition workPageDefinition;
   protected final WorkFlowDefinition workFlowDefinition;

   private WorkPage(WorkFlowDefinition workFlowDefinition, WorkPageDefinition workPageDefinition, IXWidgetOptionResolver optionResolver, IDynamicWidgetLayoutListener dynamicWidgetLayoutListener) {
      this.workFlowDefinition = workFlowDefinition;
      this.workPageDefinition = workPageDefinition;
      if (dynamicWidgetLayoutListener == null) {
         dynamicXWidgetLayout = new DynamicXWidgetLayout(this, optionResolver);
      } else {
         dynamicXWidgetLayout = new DynamicXWidgetLayout(dynamicWidgetLayoutListener, optionResolver);
      }
   }

   /**
    * @param instructionLines input lines of WorkAttribute declarations
    */
   public WorkPage(WorkFlowDefinition workFlowDefinition, WorkPageDefinition workPageDefinition, String xWidgetsXml, IXWidgetOptionResolver optionResolver, IDynamicWidgetLayoutListener dynamicWidgetLayoutListener) {
      this(workFlowDefinition, workPageDefinition, optionResolver, dynamicWidgetLayoutListener);
      try {
         if (xWidgetsXml != null) {
            processXmlLayoutDatas(xWidgetsXml);
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, "Error processing attributes", ex);
      }
   }

   public WorkPage(WorkFlowDefinition workFlowDefinition, WorkPageDefinition workPageDefinition, List<DynamicXWidgetLayoutData> datas, IXWidgetOptionResolver optionResolver, IDynamicWidgetLayoutListener dynamicWidgetLayoutListener) {
      this(workFlowDefinition, workPageDefinition, optionResolver, dynamicWidgetLayoutListener);
      dynamicXWidgetLayout.setLayoutDatas(datas);
   }

   public WorkPage(List<DynamicXWidgetLayoutData> datas, IXWidgetOptionResolver optionResolver, IDynamicWidgetLayoutListener dynamicWidgetLayoutListener) {
      this(null, null, datas, optionResolver, dynamicWidgetLayoutListener);
   }

   public WorkPage(List<DynamicXWidgetLayoutData> datas, IXWidgetOptionResolver optionResolver) {
      this(null, null, datas, optionResolver, null);
   }

   public WorkPage(String xWidgetsXml, IXWidgetOptionResolver optionResolver) {
      this(null, null, xWidgetsXml, optionResolver, null);
   }

   public WorkPage(IXWidgetOptionResolver optionResolver) {
      this(null, null, (String) null, optionResolver, null);
   }

   public void widgetCreating(XWidget xWidget, FormToolkit toolkit, Artifact art, WorkPage page, XModifiedListener xModListener, boolean isEditable) throws OseeCoreException {
   }

   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, WorkPage page, XModifiedListener xModListener, boolean isEditable) throws OseeCoreException {
   }

   public void createXWidgetLayoutData(DynamicXWidgetLayoutData workAttr, XWidget xWidget, FormToolkit toolkit, Artifact art, XModifiedListener xModListener, boolean isEditable) throws OseeCoreException {
   }

   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, DynamicXWidgetLayout dynamicXWidgetLayout, XModifiedListener xModListener, boolean isEditable) throws OseeCoreException {
      widgetCreated(xWidget, toolkit, art, this, xModListener, isEditable);
   }

   public void widgetCreating(XWidget xWidget, FormToolkit toolkit, Artifact art, DynamicXWidgetLayout dynamicXWidgetLayout, XModifiedListener xModListener, boolean isEditable) throws OseeCoreException {
      widgetCreating(xWidget, toolkit, art, this, xModListener, isEditable);
   }

   public void dispose() {
      try {
         for (DynamicXWidgetLayoutData layoutData : getlayoutDatas()) {
            layoutData.getXWidget().dispose();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof WorkPage) {
         return getId().equals(((WorkPage) obj).getId());
      }
      return false;
   }

   public DynamicXWidgetLayout createBody(IManagedForm managedForm, Composite parent, Artifact artifact, XModifiedListener xModListener, boolean isEditable) throws OseeCoreException {
      dynamicXWidgetLayout.createBody(managedForm, parent, artifact, xModListener, isEditable);
      return dynamicXWidgetLayout;
   }

   public Result isPageComplete() {
      try {
         for (DynamicXWidgetLayoutData layoutData : dynamicXWidgetLayout.getLayoutDatas()) {
            if (!layoutData.getXWidget().isValid().isOK()) {
               // Check to see if widget is part of a completed OR or XOR group
               if (!dynamicXWidgetLayout.isOrGroupFromAttrNameComplete(layoutData.getStorageName()) && !dynamicXWidgetLayout.isXOrGroupFromAttrNameComplete(layoutData.getStorageName())) {
                  return new Result(layoutData.getXWidget().isValid().getMessage());
               }
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return Result.TrueResult;
   }

   public String getHtml(String backgroundColor) throws OseeArgumentException {
      return getHtml(backgroundColor, "", "");
   }

   public String getHtml(String backgroundColor, String preHtml, String postHtml) throws OseeArgumentException {
      StringBuffer sb = new StringBuffer();
      sb.append(AHTML.startBorderTable(100, backgroundColor, getName()));
      if (preHtml != null) {
         sb.append(preHtml);
      }
      for (DynamicXWidgetLayoutData layoutData : dynamicXWidgetLayout.getLayoutDatas()) {
         sb.append(layoutData.getXWidget().toHTML(AHTML.LABEL_FONT) + AHTML.newline());
      }
      if (postHtml != null) {
         sb.append(postHtml);
      }
      sb.append(AHTML.endBorderTable());
      return sb.toString();
   }

   @Override
   public String toString() {
      StringBuffer sb =
            new StringBuffer(
                  workPageDefinition.getPageName() + (workPageDefinition.getId() != null ? " (" + workPageDefinition.getId() + ") " : "") + "\n");
      try {
         for (WorkPageDefinition page : workFlowDefinition.getPageDefinitions(workPageDefinition.getId(),
               TransitionType.ToPage)) {
            sb.append("-> " + page.getPageName() + (workFlowDefinition.getPageDefinitions(workPageDefinition.getId(),
                  TransitionType.ToPageAsReturn).contains(
                  workFlowDefinition.getPageDefinitions(workPageDefinition.getId(), TransitionType.ToPage)) ? " (return)" : "") + "\n");
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
      return sb.toString();
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

   protected void processXmlLayoutDatas(String xWidgetXml) throws IOException, ParserConfigurationException, SAXException {
      dynamicXWidgetLayout.processlayoutDatas(xWidgetXml);
   }

   protected void processLayoutDatas(Element element) throws IOException, ParserConfigurationException, SAXException {
      dynamicXWidgetLayout.processLayoutDatas(element);
   }

   public String getName() {
      return workPageDefinition.getPageName();
   }

   public String getId() {
      return workPageDefinition.id;
   }

   /**
    * @return Returns the toPages.
    */
   public List<WorkPageDefinition> getToPages() throws OseeCoreException {
      return workFlowDefinition.getToPages(workPageDefinition);
   }

   /**
    * @return Returns the toPages.
    */
   public List<WorkPageDefinition> getReturnPages() throws OseeCoreException {
      return workFlowDefinition.getReturnPages(workPageDefinition);
   }

   public boolean isReturnPage(WorkPageDefinition page) throws OseeCoreException {
      return getReturnPages().contains(page);
   }

   /**
    * @return Returns the defaultToPage.
    */
   public WorkPageDefinition getDefaultToPage() throws OseeCoreException {
      return workFlowDefinition.getDefaultToPage(workPageDefinition);
   }

   /**
    * @return the workPageDefinition
    */
   public WorkPageDefinition getWorkPageDefinition() {
      return workPageDefinition;
   }

   /**
    * @return the workFlowDefinition
    */
   public WorkFlowDefinition getWorkFlowDefinition() {
      return workFlowDefinition;
   }

   /**
    * @return the dynamicXWidgetLayout
    */
   public DynamicXWidgetLayout getDynamicXWidgetLayout() {
      return dynamicXWidgetLayout;
   }

}
