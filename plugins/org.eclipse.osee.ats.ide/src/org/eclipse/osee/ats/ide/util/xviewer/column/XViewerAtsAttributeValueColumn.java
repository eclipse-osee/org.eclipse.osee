/*********************************************************************
 * Copyright (c) 2010 Boeing
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
import org.eclipse.nebula.widgets.xviewer.IAltLeftClickProvider;
import org.eclipse.nebula.widgets.xviewer.IMultiColumnEditProvider;
import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.config.AtsAttrVaCol;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsEditors;
import org.eclipse.osee.ats.ide.util.PromptChangeUtil;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * Given ATS attribute, will return ; delimited values for AbstractWorkflowArtifact and unique rolled up values for
 * ActionArtifacts. This column will also provide default alt-left-click operation for any single value (max=1)
 * attribute.
 *
 * @author Donald G. Dunne
 */
public class XViewerAtsAttributeValueColumn extends XViewerAtsAttributeColumn implements IAltLeftClickProvider, IMultiColumnEditProvider, IXViewerValueColumn {

   String booleanOnTrueShow = null;
   String booleanOnFalseShow = null;
   String booleanNotSetShow = null;

   public XViewerAtsAttributeValueColumn(AttributeTypeToken attributeType, int width, XViewerAlign align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(attributeType, attributeType.getName(), attributeType.getUnqualifiedName(), width, align, show,
         sortDataType, multiColumnEditable, description);
   }

   public XViewerAtsAttributeValueColumn(AttributeTypeToken attributeType, String id, String name, int width, XViewerAlign align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(attributeType, id, name, width, align, show, sortDataType, multiColumnEditable, description);
   }

   public XViewerAtsAttributeValueColumn(AtsAttrVaCol column) {
      super(AttributeTypeManager.getAttributeType(column.getAttrTypeId()), column.getId(), column.getName(),
         column.getWidth(), AtsEditors.getXViewerAlign(column.getAlign()), column.isVisible(), getSortDataType(column),
         column.isColumnMultiEdit(), column.getDescription());
      setInheritParent(column.isInheritParent());
      setActionRollup(column.isActionRollup());
   }

   private static SortDataType getSortDataType(AtsAttrVaCol column) {
      SortDataType result = SortDataType.String;
      try {
         result = SortDataType.valueOf(column.getSortDataType());
      } catch (Exception ex) {
         // do nothing
      }
      return result;
   }

   protected XViewerAtsAttributeValueColumn() {
      super();
   }

   /**
    * Returns parent team workflow, if AbstractWorkflowArtifact or Artifact, if artifact
    */
   protected Artifact getParentTeamWorkflowOrArtifact(Object element) {
      Artifact useArt = null;
      if (element instanceof Artifact) {
         if (element instanceof AbstractWorkflowArtifact) {
            useArt = (Artifact) ((AbstractWorkflowArtifact) element).getParentTeamWorkflow();
         } else {
            useArt = AtsApiService.get().getQueryServiceIde().getArtifact(element);
         }
      }
      return useArt;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof Artifact && AtsApiService.get().getQueryServiceIde().getArtifact(element).isDeleted()) {
            return "<deleted>";
         }
         if (isBooleanShow() && column.getSortDataType() == SortDataType.Boolean) {
            if (element instanceof AbstractWorkflowArtifact) {
               Boolean value = ((AbstractWorkflowArtifact) element).getSoleAttributeValue(getAttributeType(), null);
               if (value == null && booleanNotSetShow != null) {
                  return booleanNotSetShow;
               } else if (value != null) {
                  if (value && booleanOnTrueShow != null) {
                     return booleanOnTrueShow;
                  } else if (!value && booleanOnFalseShow != null) {
                     return booleanOnFalseShow;
                  }
               }
            }
         }
         if (element instanceof AbstractWorkflowArtifact) {
            AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) element;
            String result = awa.getAttributesToStringUnique(getAttributeType(), ";");
            if (Strings.isValid(result)) {
               return result;
            }
            if (isInheritParentWithDefault() && !awa.isTeamWorkflow() && awa.getParentTeamWorkflow() != null) {
               result = Collections.toString("; ",
                  ((Artifact) awa.getParentTeamWorkflow()).getAttributesToStringList(getAttributeType()));
               if (Strings.isValid(result)) {
                  return result;
               }
            }
         }
         if (Artifacts.isOfType(element, AtsArtifactTypes.Action) && isActionRollupWithDefault()) {
            Set<String> strs = new LinkedHashSet<>();
            for (IAtsTeamWorkflow team : AtsApiService.get().getWorkItemService().getTeams(element)) {
               String str = getColumnText(team, column, columnIndex);
               if (Strings.isValid(str)) {
                  strs.add(str);
               }
            }
            return Collections.toString("; ", strs);
         }
      } catch (Exception ex) {
         return LogUtil.getCellExceptionString(ex);
      }
      return "";
   }

   private boolean isActionRollupWithDefault() {
      if (isActionRollup() == null) {
         return false;
      }
      return isActionRollup();
   }

   private boolean isInheritParentWithDefault() {
      if (isInheritParent() == null) {
         return false;
      }
      return isInheritParent();
   }

   private boolean isBooleanShow() {
      return booleanOnFalseShow != null || booleanOnTrueShow != null || booleanNotSetShow != null;
   }

   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      if (treeColumn != null && !treeColumn.isDisposed() && treeItem != null && !treeItem.isDisposed() && isMultiColumnEditable()) {
         return AtsAttributeColumnUtility.handleAltLeftClick(treeColumn.getData(), treeItem.getData(),
            isPersistAltLeftClick());
      }
      return false;
   }

   @Override
   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      Set<AbstractWorkflowArtifact> awas = new LinkedHashSet<>();
      for (TreeItem item : treeItems) {
         Artifact art = AtsApiService.get().getQueryServiceIde().getArtifact(item);
         try {
            if (art instanceof AbstractWorkflowArtifact && art.isAttributeTypeValid(getAttributeType())) {
               awas.add((AbstractWorkflowArtifact) art);
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      if (awas.isEmpty()) {
         AWorkbench.popup("Invalid Selection",
            String.format("No selected items valid for attribute [%s] editing", getAttributeType()));
         return;
      }
      PromptChangeUtil.promptChangeAttribute(awas, getAttributeType(),
         AtsAttributeColumnUtility.isPersistViewer((XViewer) getXViewer()));
      ((XViewer) getXViewer()).update(awas.toArray(), null);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn need to extend this constructor to copy extra stored fields
    */
   @Override
   public XViewerAtsAttributeValueColumn copy() {
      XViewerAtsAttributeValueColumn newXCol = new XViewerAtsAttributeValueColumn();
      copy(this, newXCol);
      newXCol.setBooleanNotSetShow(getBooleanNotSetShow());
      newXCol.setBooleanOnFalseShow(getBooleanOnFalseShow());
      newXCol.setBooleanOnTrueShow(getBooleanOnTrueShow());
      newXCol.setInheritParent(isInheritParent());
      newXCol.setActionRollup(isActionRollup());
      return newXCol;
   }

   public String getBooleanOnTrueShow() {
      return booleanOnTrueShow;
   }

   public void setBooleanOnTrueShow(String booleanOnTrueShow) {
      this.booleanOnTrueShow = booleanOnTrueShow;
   }

   public String getBooleanOnFalseShow() {
      return booleanOnFalseShow;
   }

   public void setBooleanOnFalseShow(String booleanOnFalseShow) {
      this.booleanOnFalseShow = booleanOnFalseShow;
   }

   public String getBooleanNotSetShow() {
      return booleanNotSetShow;
   }

   public void setBooleanNotSetShow(String booleanNotSetShow) {
      this.booleanNotSetShow = booleanNotSetShow;
   }

}
