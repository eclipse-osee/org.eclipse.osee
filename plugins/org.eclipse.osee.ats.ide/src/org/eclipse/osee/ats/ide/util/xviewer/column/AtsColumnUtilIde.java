/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.ide.util.xviewer.column;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.config.AtsDisplayHint;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.PromptChangeUtil;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.IAttributeColumn;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class AtsColumnUtilIde {

   public static final String INVALID_SELECTION = "Invalid Selection";

   public static final String INVALID_ATTR_TYPE = "Invalid Attribute [%s]";
   public static final String COLUMN_NOT_EDITABLE = "Column [%s] is not editable";
   public static final String ATTRIBUTE_NOT_EDITABLE = "Attribute [%s] is not editable";
   public static final String INVALID_COLUMN_FOR_SELECTED = "Column [%s] is not valid for selected";
   public static final String INAVLID_ATTR_FOR_SELECTED = "Attribute [%s] is not valid for selected";

   public static boolean handleAltLeftClick(Object columnData, Object item, boolean persist) {
      try {
         if (columnData instanceof IAttributeColumn) {
            IAttributeColumn attrColumn = (IAttributeColumn) columnData;
            AttributeTypeToken attrType = attrColumn.getAttributeType();
            if (attrType.isInvalid()) {
               AWorkbench.popup(INVALID_SELECTION, COLUMN_NOT_EDITABLE, attrColumn.getName());
               return false;
            }
            if (!attrType.getDisplayHints().contains(AtsDisplayHint.Edit)) {
               AWorkbench.popup(INVALID_SELECTION, ATTRIBUTE_NOT_EDITABLE, attrType.getUnqualifiedName());
               return false;
            }
            if (item instanceof Artifact) {
               Artifact useArt = AtsApiService.get().getQueryServiceIde().getArtifact(item);
               if (!useArt.isAttributeTypeValid(attrType)) {
                  AWorkbench.popup(INVALID_SELECTION, INAVLID_ATTR_FOR_SELECTED, attrType.getUnqualifiedName());
                  return false;
               }
               if (useArt.getArtifactType().getMax(attrColumn.getAttributeType()) != 1) {
                  if (useArt.getAttributeCount(attrColumn.getAttributeType()) > 1) {
                     return false;
                  }
               }
               boolean modified = PromptChangeUtil.promptChangeAttribute((AbstractWorkflowArtifact) useArt,
                  attrColumn.getAttributeType(), true);
               if (modified && persist) {
                  useArt.persist("persist attribute via alt-left-click");
               }
               if (modified) {
                  ((XViewer) ((XViewerColumn) columnData).getXViewer()).update(useArt, null);
                  return true;
               }
            }
         }
         if (columnData instanceof XViewerColumn) {
            XViewerColumn column = (XViewerColumn) columnData;
            AWorkbench.popup(INVALID_SELECTION, COLUMN_NOT_EDITABLE, column.getName());
         }
         return false;

      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

      return false;
   }

   public static void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems, XViewer xViewer) {
      AWorkbench.popup(INVALID_SELECTION, COLUMN_NOT_EDITABLE, treeColumn.getText());
      return;
   }

   public static void handleColumnMultiEdit(Collection<TreeItem> treeItems, AttributeTypeToken attrType,
      XViewer xViewer) {
      if (attrType.isInvalid()) {
         AWorkbench.popup(INVALID_SELECTION, INVALID_ATTR_TYPE, attrType.getUnqualifiedName());
         return;
      }
      if (!attrType.getDisplayHints().contains(AtsDisplayHint.Edit)) {
         AWorkbench.popup(INVALID_SELECTION, ATTRIBUTE_NOT_EDITABLE, attrType.getUnqualifiedName());
         return;
      }
      Set<AbstractWorkflowArtifact> awas = new LinkedHashSet<>();
      for (TreeItem item : treeItems) {
         Artifact art = AtsApiService.get().getQueryServiceIde().getArtifact(item);
         try {
            if (art instanceof AbstractWorkflowArtifact && art.isAttributeTypeValid(attrType)) {
               awas.add((AbstractWorkflowArtifact) art);
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      if (awas.isEmpty()) {
         AWorkbench.popup(INVALID_SELECTION, INAVLID_ATTR_FOR_SELECTED, attrType.getUnqualifiedName());
         return;
      }
      if (PromptChangeUtil.promptChangeAttribute(awas, attrType, true)) {
         xViewer.update(awas.toArray(), null);
      }
   }

}
