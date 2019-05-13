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
package org.eclipse.osee.ats.ide.world;

import java.util.logging.Level;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.core.column.AtsColumnId;
import org.eclipse.osee.ats.core.column.AtsColumnToken;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.ide.workdef.StateColorToSwtColor;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class WorldLabelProvider extends XViewerLabelProvider {

   public WorldLabelProvider(WorldXViewer worldXViewer) {
      super(worldXViewer);
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn xCol, int columnIndex) {
      try {
         for (IAtsWorldEditorItem item : AtsWorldEditorItems.getItems()) {
            if (item.isXColumnProvider(xCol)) {
               Image image = item.getColumnImage(element, xCol, columnIndex);
               if (image != null) {
                  return image;
               }
            }
         }
         if (xCol.getId().equals(AtsColumnToken.TypeColumn.getId())) {
            return ArtifactImageManager.getImage(AtsClientService.get().getQueryServiceClient().getArtifact(element));
         }
         if (xCol.getId().equals(AtsColumnToken.StateColumn.getId())) {
            if (element instanceof IAtsWorkItem) {
               IAtsWorkItem workItem = (IAtsWorkItem) element;
               String isBlocked = AtsClientService.get().getAttributeResolver().getSoleAttributeValue(workItem,
                  AtsAttributeTypes.BlockedReason, "");
               if (Strings.isValid(isBlocked)) {
                  return ImageManager.getImage(FrameworkImage.X_RED);
               }
            }
         }
      } catch (Exception ex) {
         // do nothing
      }
      return null;
   }

   @Override
   public Color getForeground(Object element, XViewerColumn xCol, int columnIndex) {
      try {
         for (IAtsWorldEditorItem item : AtsWorldEditorItems.getItems()) {
            if (item.isXColumnProvider(xCol)) {
               Color color = item.getForeground(element, xCol, columnIndex);
               if (color != null) {
                  return color;
               }
            }
         }
         if (element instanceof IAtsWorkItem) {
            if (xCol.getId().equals(AtsColumnId.State.getId())) {
               IAtsStateDefinition state = ((AbstractWorkflowArtifact) element).getStateDefinition();
               if (state == null) {
                  OseeLog.logf(Activator.class, Level.SEVERE, "State null for %s",
                     ((IAtsWorkItem) element).toStringWithId());
               } else {
                  return Displays.getSystemColor(StateColorToSwtColor.convert(state.getColor()));
               }
            }
         }

         if (xCol instanceof XViewerAtsColumn) {
            return ((XViewerAtsColumn) xCol).getForeground(element, xCol, columnIndex);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return null;
   }

   @Override
   public Color getBackground(Object element, XViewerColumn xCol, int columnIndex) {
      try {
         if (xCol instanceof XViewerAtsColumn) {
            return ((XViewerAtsColumn) xCol).getBackground(element, xCol, columnIndex);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return null;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn xCol, int columnIndex) {
      try {
         // NOTE: ID, Type, Title are handled by XViewerValueColumn values
         if (!AtsObjects.isAtsWorkItemOrAction(element)) {
            return "";
         }
         for (IAtsWorldEditorItem item : AtsWorldEditorItems.getItems()) {
            if (item.isXColumnProvider(xCol)) {
               String text = item.getColumnText(element, xCol, columnIndex);
               if (text != null) {
                  return text;
               }
            }
         }
         return "Unhandled Column";
      } catch (Exception ex) {
         return LogUtil.getCellExceptionString(ex);
      }
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
   public void addListener(ILabelProviderListener listener) {
      // do nothing
   }

   @Override
   public void removeListener(ILabelProviderListener listener) {
      // do nothing
   }

}
