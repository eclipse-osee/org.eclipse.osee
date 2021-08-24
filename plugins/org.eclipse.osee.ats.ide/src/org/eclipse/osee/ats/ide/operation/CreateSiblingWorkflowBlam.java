/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.operation;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.util.IsEnabled;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public class CreateSiblingWorkflowBlam extends ModifyActionableItemsBlam implements IsEnabled {

   @Override
   protected void createTreeViewers(Composite parent) {
      super.createTreeViewers(parent);
      newTree.setEnabledChecker(this);
   }

   @Override
   protected int getLayoutColumns() {
      return 1;
   }

   @Override
   protected boolean displayWfTree() {
      return false;
   }

   @Override
   protected boolean displayDuplicateButton() {
      return false;
   }

   @Override
   protected boolean displayOtherTree() {
      return false;
   }

   @Override
   public String getDescriptionUsage() {
      return "Create sibling workflows in this Action.  Select AIs and select Create button.";
   }

   @Override
   public String getRunText() {
      return "Create Sibling Workflows";
   }

   @Override
   public String getOutputMessage() {
      return "Not yet run.";
   }

   @Override
   public String getTabTitle() {
      return "Create Sibling Workflows";
   }

   @Override
   public String getTitle() {
      return "Create Sibling Workflows";
   }

   @Override
   public Image getImage() {
      return ImageManager.getImage(AtsImage.CHANGE_REQUEST);
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.CHANGE_REQUEST);
   }

   @Override
   public String getDropLabelStr() {
      return "Team Workflow";
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) {
      super.widgetCreated(xWidget, toolkit, art, dynamicXWidgetLayout, modListener, isEditable);
      if (xWidget.getLabel().equals(getDropLabelStr())) {
         dropViewer.getTableViewer().getTable().setEnabled(false);
      }
   }

   @Override
   public boolean isEnabled(Object obj) {
      if (obj instanceof IAtsActionableItem) {
         return ((IAtsActionableItem) obj).isActive() && ((IAtsActionableItem) obj).isActionable();
      }
      return false;
   }

}
