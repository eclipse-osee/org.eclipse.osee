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
package org.eclipse.osee.ats.util.xviewer.column;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.IAltLeftClickProvider;
import org.eclipse.nebula.widgets.xviewer.IMultiColumnEditProvider;
import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.config.AtsAttributeValueColumn;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.column.AtsColumnId;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.AtsEditors;
import org.eclipse.osee.ats.util.PromptChangeUtil;
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
   List<String> multiLineColumns = Arrays.asList(AtsColumnId.Notes.getId());

   public XViewerAtsAttributeValueColumn(AttributeTypeToken attributeType, int width, XViewerAlign align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(attributeType, attributeType.getName(), attributeType.getUnqualifiedName(), width, align, show,
         sortDataType, multiColumnEditable, description);
   }

   public XViewerAtsAttributeValueColumn(AttributeTypeToken attributeType, String id, String name, int width, XViewerAlign align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(attributeType, id, name, width, align, show, sortDataType, multiColumnEditable, description);
   }

   public XViewerAtsAttributeValueColumn(AtsAttributeValueColumn column) {
      super(AttributeTypeManager.getTypeByGuid(column.getAttrTypeId()), column.getId(), column.getName(),
         column.getWidth(), AtsEditors.getXViewerAlign(column.getAlign()), column.isVisible(), getSortDataType(column),
         column.isColumnMultiEdit(), column.getDescription());
      setInheritParent(column.isInheritParent());
      setActionRollup(column.isActionRollup());
   }

   private static SortDataType getSortDataType(AtsAttributeValueColumn column) {
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
            useArt = ((AbstractWorkflowArtifact) element).getParentTeamWorkflow();
         } else {
            useArt = (Artifact) element;
         }
      }
      return useArt;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof Artifact && ((Artifact) element).isDeleted()) {
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
               result =
                  Collections.toString("; ", awa.getParentTeamWorkflow().getAttributesToStringList(getAttributeType()));
               if (Strings.isValid(result)) {
                  return result;
               }
            }
         }
         if (Artifacts.isOfType(element, AtsArtifactTypes.Action) && (isActionRollupWithDefault())) {
            Set<String> strs = new LinkedHashSet<>();
            for (IAtsTeamWorkflow team : AtsClientService.get().getWorkItemService().getTeams(element)) {
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
      if (treeColumn != null && !treeColumn.isDisposed() && treeItem != null && !treeItem.isDisposed()) {
         return AtsAttributeColumnUtility.handleAltLeftClick(treeColumn.getData(), treeItem.getData(),
            isMultiLineStringAttribute(), isPersistAltLeftClick());
      }
      return false;
   }

   @Override
   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      Set<AbstractWorkflowArtifact> awas = new LinkedHashSet<>();
      for (TreeItem item : treeItems) {
         Artifact art = (Artifact) item.getData();
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
         AtsAttributeColumnUtility.isPersistViewer((XViewer) getXViewer()), isMultiLineStringAttribute());
      ((XViewer) getXViewer()).update(awas.toArray(), null);
   }

   /**
    * Set if promptChange should display a multi-lined dialog. Only valid for Textual attributes.
    */
   public boolean isMultiLineStringAttribute() {
      return multiLineColumns.contains(getId());
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
