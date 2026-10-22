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
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.core.widget.validate.XWidgetStatus;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.listen.XModifiedListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public class XWidgetPage implements XWidgetSwtRendererListener {

   protected XWidgetSwtRenderer xWidgetSwtRenderer;

   private XWidgetPage(XWidgetSwtRendererListener xWidgetSwtRendererListener) {
      if (xWidgetSwtRendererListener == null) {
         xWidgetSwtRenderer = new XWidgetSwtRenderer(this);
      } else {
         xWidgetSwtRenderer = new XWidgetSwtRenderer(xWidgetSwtRendererListener);
      }
   }

   public XWidgetPage(List<XWidgetData> widDatas, XWidgetSwtRendererListener xWidgetSwtRendererListener) {
      this(xWidgetSwtRendererListener);
      xWidgetSwtRenderer.setWidgetDatas(widDatas);
   }

   public XWidgetPage(List<XWidgetData> widDatas) {
      this(widDatas, null);
   }

   public XWidgetSwtRenderer createBody(IManagedForm managedForm, Composite parent, Artifact artifact,
      XModifiedListener xModListener, boolean isEditable) {
      xWidgetSwtRenderer.createBody(managedForm, parent, artifact, xModListener, isEditable);
      return xWidgetSwtRenderer;
   }

   public void dispose() {
      try {
         for (XWidget widget : getSwtXWidgetRenderer().getXWidgets()) {
            widget.dispose();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public XResultData isPageComplete() {
      try {
         for (XWidget widget : getSwtXWidgetRenderer().getXWidgets()) {
            XResultData valid = widget.isValid();
            if (valid.isErrors()) {
               return valid;
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return XWidgetStatus.OK;
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
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, XWidgetSwtRenderer xWidgetSwtRenderer,
      XModifiedListener xModListener, boolean isEditable) {
      widgetCreated(xWidget, toolkit, art, this, xModListener, isEditable);
   }

   @Override
   public void widgetCreating(XWidget xWidget, FormToolkit toolkit, Artifact art, XWidgetSwtRenderer xWidgetSwtRenderer,
      XModifiedListener xModListener, boolean isEditable) {
      widgetCreating(xWidget, toolkit, art, this, xModListener, isEditable);
   }

   public Set<XWidgetData> getXWidgetDatas() {
      return xWidgetSwtRenderer.getXWidgetDatas();
   }

   public void addXWidgetDatas(List<XWidgetData> widDatas) {
      xWidgetSwtRenderer.addXWidgetDatas(widDatas);
   }

   public void addXWidgetData(XWidgetData widData) {
      xWidgetSwtRenderer.addXWidgetData(widData);
   }

   public XWidgetData getXWidgetData(String labelName) {
      return xWidgetSwtRenderer.getXWidgetData(labelName);
   }

   public XWidget getXWidget(String labelName) {
      return xWidgetSwtRenderer.getXWidget(labelName);
   }

   public XWidgetSwtRenderer getSwtXWidgetRenderer() {
      return xWidgetSwtRenderer;
   }

   @Override
   public int hashCode() {
      return super.hashCode();
   }

   public XWidget getXWidget(WidgetId widgetId) {
      return xWidgetSwtRenderer.getXWidget(widgetId);
   }

   public void createIfMissing() {
      xWidgetSwtRenderer.createIfMissing();
   }

   public XWidget getXWidget(long id) {
      return xWidgetSwtRenderer.getWidget(id);
   }

}
