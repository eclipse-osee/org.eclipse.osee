/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.widgets.util;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.core.widget.XWidgetData;
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
 * @author Donald G. Dunne
 */
public class XWidgetPage implements IDynamicWidgetLayoutListener {

   protected SwtXWidgetRenderer swtXWidgetRenderer;

   private XWidgetPage(IDynamicWidgetLayoutListener dynamicWidgetLayoutListener) {
      if (dynamicWidgetLayoutListener == null) {
         swtXWidgetRenderer = new SwtXWidgetRenderer(this);
      } else {
         swtXWidgetRenderer = new SwtXWidgetRenderer(dynamicWidgetLayoutListener);
      }
   }

   /**
    * @param instructionLines input lines of WorkAttribute declarations
    */
   public XWidgetPage(String xWidgetsXml, IDynamicWidgetLayoutListener dynamicWidgetLayoutListener) {
      this(dynamicWidgetLayoutListener);
      try {
         if (xWidgetsXml != null) {
            processXmlLayoutDatas(xWidgetsXml);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, "Error processing attributes", ex);
      }
   }

   public XWidgetPage(List<XWidgetData> widDatas, IDynamicWidgetLayoutListener dynamicWidgetLayoutListener) {
      this(dynamicWidgetLayoutListener);
      swtXWidgetRenderer.setWidgetDatas(widDatas);
   }

   public XWidgetPage(List<XWidgetData> widDatas) {
      this(widDatas, null);
   }

   public XWidgetPage(String xWidgetsXml) {
      this(xWidgetsXml, null);
   }

   public XWidgetPage() {
      this((String) null, null);
   }

   public void widgetCreating(XWidget xWidget, FormToolkit toolkit, Artifact art, XWidgetPage page,
      XModifiedListener xModListener, boolean isEditable) {
      // provided for subclass implementation
   }

   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, XWidgetPage page,
      XModifiedListener xModListener, boolean isEditable) {
      // provided for subclass implementation
   }

   @Override
   public void createXWidgetLayoutData(XWidgetData workAttr, XWidget xWidget, FormToolkit toolkit, Artifact art,
      XModifiedListener xModListener, boolean isEditable) {
      // provided for subclass implementation
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer swtXWidgetRenderer,
      XModifiedListener xModListener, boolean isEditable) {
      widgetCreated(xWidget, toolkit, art, this, xModListener, isEditable);
   }

   @Override
   public void widgetCreating(XWidget xWidget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer swtXWidgetRenderer,
      XModifiedListener xModListener, boolean isEditable) {
      widgetCreating(xWidget, toolkit, art, this, xModListener, isEditable);
   }

   public void dispose() {
      try {
         for (XWidgetData widData : getXWidgetDatas()) {
            swtXWidgetRenderer.getXWidget(widData).dispose();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public SwtXWidgetRenderer createBody(IManagedForm managedForm, Composite parent, Artifact artifact,
      XModifiedListener xModListener, boolean isEditable) {
      swtXWidgetRenderer.createBody(managedForm, parent, artifact, xModListener, isEditable);
      return swtXWidgetRenderer;
   }

   public Result isPageComplete() {
      try {
         for (XWidgetData widData : swtXWidgetRenderer.getXWidgetDatas()) {
            XWidget widget = swtXWidgetRenderer.getXWidget(widData);
            return new Result(widget.isValid().getMessage());
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return Result.TrueResult;
   }

   public Set<XWidgetData> getXWidgetDatas() {
      return swtXWidgetRenderer.getXWidgetDatas();
   }

   public void addXWidgetDatas(List<XWidgetData> widDatas) {
      swtXWidgetRenderer.addXWidgetDatas(widDatas);
   }

   public void addXWidgetData(XWidgetData widData) {
      swtXWidgetRenderer.addXWidgetData(widData);
   }

   public XWidgetData getXWidgetData(String labelName) {
      return swtXWidgetRenderer.getXWidgetData(labelName);
   }

   public XWidget getXWidget(String labelName) {
      return swtXWidgetRenderer.getXWidget(labelName);
   }

   public void processInstructions(Document doc) {
      processXWidgetDatas(doc.getDocumentElement());
   }

   protected void processXmlLayoutDatas(String xWidgetXml) {
      swtXWidgetRenderer.processXWidgetDatas(xWidgetXml);
   }

   protected void processXWidgetDatas(Element element) {
      swtXWidgetRenderer.processXWidgetDatas(element);
   }

   public SwtXWidgetRenderer getSwtXWidgetRenderer() {
      return swtXWidgetRenderer;
   }

   @Override
   public int hashCode() {
      return super.hashCode();
   }

   public XWidget getXWidget(XWidgetData widData) {
      return swtXWidgetRenderer.getXWidget(widData);
   }

}
