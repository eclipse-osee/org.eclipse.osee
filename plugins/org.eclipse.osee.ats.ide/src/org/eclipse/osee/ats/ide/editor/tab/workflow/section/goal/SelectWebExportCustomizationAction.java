/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.ats.ide.editor.tab.workflow.section.goal;

import java.util.Collection;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.window.Window;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.goal.GoalArtifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredListDialog;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class SelectWebExportCustomizationAction extends AbstractWebExportAction {

   public SelectWebExportCustomizationAction(GoalArtifact goalArt, WorkflowEditor editor) {
      super("Select Web Export Customization", goalArt, editor, AtsImage.CUSTOMIZE);
   }

   @Override
   public void runWithException() {

      Collection<CustomizeData> customizations = AtsApiService.get().getStoreService().getCustomizations("GoalXViewer");
      FilteredListDialog<CustomizeData> dialog = new FilteredListDialog<CustomizeData>("Select Customization",
         "Select Customization", new CustomizeLabelProvider());
      dialog.setInput(customizations);
      if (dialog.open() == Window.OK) {
         if (dialog.getSelected() != null) {
            CustomizeData custData = dialog.getSelected();
            IAtsChangeSet changes = AtsApiService.get().createChangeSet(getText());
            changes.setSoleAttributeValue((IAtsObject) goalArt, AtsAttributeTypes.WorldResultsCustId,
               custData.getGuid());
            changes.executeIfNeeded();

            AWorkbench.popup(getText(), "Customization Set to [%s]", custData.getName());
         }
      }
   }

   private class CustomizeLabelProvider implements ILabelProvider {

      @Override
      public void addListener(ILabelProviderListener listener) {
         // do nothing
      }

      @Override
      public void dispose() {
         // do nothing
      }

      @Override
      public boolean isLabelProperty(Object element, String property) {
         return false;
      }

      @Override
      public void removeListener(ILabelProviderListener listener) {
         // do nothing
      }

      @Override
      public Image getImage(Object element) {
         return ImageManager.getImage(AtsImage.CUSTOMIZE);
      }

      @Override
      public String getText(Object element) {
         return ((CustomizeData) element).getName();
      }

   }

}
