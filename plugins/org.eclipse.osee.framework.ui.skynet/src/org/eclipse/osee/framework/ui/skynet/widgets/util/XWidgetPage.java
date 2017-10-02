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
package org.eclipse.osee.framework.ui.skynet.widgets.util;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Instantiation of a workpagedefinition for a given workflow. This contains UI components that are specific to the
 * instantiation.
 *
 * @author Donald G. Dunne
 */
public class XWidgetPage implements IDynamicWidgetLayoutListener {

   protected SwtXWidgetRenderer dynamicXWidgetLayout;

   private XWidgetPage(IXWidgetOptionResolver optionResolver, IDynamicWidgetLayoutListener dynamicWidgetLayoutListener) {
      if (dynamicWidgetLayoutListener == null) {
         dynamicXWidgetLayout = new SwtXWidgetRenderer(this, optionResolver);
      } else {
         dynamicXWidgetLayout = new SwtXWidgetRenderer(dynamicWidgetLayoutListener, optionResolver);
      }
   }

   /**
    * @param instructionLines input lines of WorkAttribute declarations
    */
   public XWidgetPage(String xWidgetsXml, IXWidgetOptionResolver optionResolver, IDynamicWidgetLayoutListener dynamicWidgetLayoutListener) {
      this(optionResolver, dynamicWidgetLayoutListener);
      try {
         if (xWidgetsXml != null) {
            processXmlLayoutDatas(xWidgetsXml);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, "Error processing attributes", ex);
      }
   }

   public XWidgetPage(List<XWidgetRendererItem> datas, IXWidgetOptionResolver optionResolver, IDynamicWidgetLayoutListener dynamicWidgetLayoutListener) {
      this(optionResolver, dynamicWidgetLayoutListener);
      dynamicXWidgetLayout.setLayoutDatas(datas);
   }

   public XWidgetPage(List<XWidgetRendererItem> datas, IXWidgetOptionResolver optionResolver) {
      this(datas, optionResolver, null);
   }

   public XWidgetPage(String xWidgetsXml, IXWidgetOptionResolver optionResolver) {
      this(xWidgetsXml, optionResolver, null);
   }

   public XWidgetPage(IXWidgetOptionResolver optionResolver) {
      this((String) null, optionResolver, null);
   }

   public void widgetCreating(XWidget xWidget, FormToolkit toolkit, Artifact art, XWidgetPage page, XModifiedListener xModListener, boolean isEditable)  {
      // provided for subclass implementation
   }

   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, XWidgetPage page, XModifiedListener xModListener, boolean isEditable)  {
      // provided for subclass implementation
   }

   @Override
   public void createXWidgetLayoutData(XWidgetRendererItem workAttr, XWidget xWidget, FormToolkit toolkit, Artifact art, XModifiedListener xModListener, boolean isEditable)  {
      // provided for subclass implementation
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener xModListener, boolean isEditable)  {
      widgetCreated(xWidget, toolkit, art, this, xModListener, isEditable);
   }

   @Override
   public void widgetCreating(XWidget xWidget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener xModListener, boolean isEditable)  {
      widgetCreating(xWidget, toolkit, art, this, xModListener, isEditable);
   }

   public void dispose() {
      try {
         for (XWidgetRendererItem layoutData : getlayoutDatas()) {
            layoutData.getXWidget().dispose();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public SwtXWidgetRenderer createBody(IManagedForm managedForm, Composite parent, Artifact artifact, XModifiedListener xModListener, boolean isEditable)  {
      dynamicXWidgetLayout.createBody(managedForm, parent, artifact, xModListener, isEditable);
      return dynamicXWidgetLayout;
   }

   public Result isPageComplete() {
      try {
         for (XWidgetRendererItem layoutData : dynamicXWidgetLayout.getLayoutDatas()) {
            if (!layoutData.getXWidget().isValid().isOK()) {
               // Check to see if widget is part of a completed OR or XOR group
               if (!dynamicXWidgetLayout.isOrGroupFromAttrNameComplete(
                  layoutData.getStoreName()) && !dynamicXWidgetLayout.isXOrGroupFromAttrNameComplete(
                     layoutData.getStoreName())) {
                  return new Result(layoutData.getXWidget().isValid().getMessage());
               }
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return Result.TrueResult;
   }

   public Set<XWidgetRendererItem> getlayoutDatas() {
      return dynamicXWidgetLayout.getLayoutDatas();
   }

   public void addLayoutDatas(List<XWidgetRendererItem> datas) {
      dynamicXWidgetLayout.addWorkLayoutDatas(datas);
   }

   public void addLayoutData(XWidgetRendererItem data) {
      dynamicXWidgetLayout.addWorkLayoutData(data);
   }

   public XWidgetRendererItem getLayoutData(String layoutName) {
      return dynamicXWidgetLayout.getLayoutData(layoutName);
   }

   public void processInstructions(Document doc) {
      processLayoutDatas(doc.getDocumentElement());
   }

   protected void processXmlLayoutDatas(String xWidgetXml)  {
      dynamicXWidgetLayout.processlayoutDatas(xWidgetXml);
   }

   protected void processLayoutDatas(Element element) {
      dynamicXWidgetLayout.processLayoutDatas(element);
   }

   public SwtXWidgetRenderer getDynamicXWidgetLayout() {
      return dynamicXWidgetLayout;
   }

   @Override
   public int hashCode() {
      return super.hashCode();
   }

}
