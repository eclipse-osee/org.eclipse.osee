/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.workdef;

import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workdef.IAtsLayoutItem;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.IDynamicWidgetLayoutListener;
import org.eclipse.osee.framework.ui.skynet.widgets.util.IXWidgetOptionResolver;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetRendererItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * Instantiation of a XWidgetPage to provide for automatic creation and management of the XWidgets
 *
 * @author Donald G. Dunne
 */
public class XWidgetPage implements IDynamicWidgetLayoutListener {

   protected SwtXWidgetRenderer dynamicXWidgetLayout;
   protected final IAtsWorkDefinition workDefinition;
   private final AbstractWorkflowArtifact awa;
   private final Collection<IAtsLayoutItem> layoutItems;

   public XWidgetPage(IAtsWorkItem workItem, IAtsWorkDefinition workDefinition, IXWidgetOptionResolver optionResolver, Collection<IAtsLayoutItem> layoutItems) {
      this.workDefinition = workDefinition;
      this.layoutItems = layoutItems;
      this.awa = (AbstractWorkflowArtifact) workItem;
      dynamicXWidgetLayout = new SwtXWidgetRenderer(this, optionResolver);
   }

   public SwtXWidgetRenderer createBody(IManagedForm managedForm, Composite parent, Artifact artifact, XModifiedListener xModListener, boolean isEditable) {
      dynamicXWidgetLayout.createBody(managedForm, parent, artifact, xModListener, isEditable);
      return dynamicXWidgetLayout;
   }

   @Override
   public void createXWidgetLayoutData(XWidgetRendererItem layoutData, XWidget xWidget, FormToolkit toolkit, Artifact art, XModifiedListener xModListener, boolean isEditable) {
      WidgetPageUtil.createXWidgetLayoutData(layoutData, xWidget, toolkit, art, xModListener, isEditable);
   }

   public void generateLayoutDatas() {
      WidgetPageUtil.generateLayoutDatas(awa, layoutItems, dynamicXWidgetLayout);
   }

   public String getHtml(String backgroundColor, String preHtml, String postHtml) {
      return WidgetPageUtil.getHtml(backgroundColor, preHtml, postHtml, dynamicXWidgetLayout.getLayoutDatas(), "");
   }

   public void dispose() {
      WidgetPageUtil.dispose(dynamicXWidgetLayout);
   }

}
