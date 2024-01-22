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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.column.AtsColumnUtil;
import org.eclipse.osee.ats.api.column.AtsCoreAttrTokColumnToken;
import org.eclipse.osee.ats.api.config.AtsDisplayHint;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.DisplayHint;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.IAttributeColumn;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * Given ATS attribute, will return ; delimited values for AbstractWorkflowArtifact and unique rolled up values for
 * ActionArtifacts. This column will also provide default alt-left-click operation for any single value (max=1)
 * attribute.
 *
 * @author Donald G. Dunne
 */
public class XViewerAtsAttrTokenXColumn extends XViewerAtsColumn implements IXViewerValueColumn, IAttributeColumn {

   String booleanOnTrueShow = null;
   String booleanOnFalseShow = null;
   String booleanNotSetShow = null;
   private AtsCoreAttrTokColumnToken columnToken;
   protected AttributeTypeToken attributeType;

   protected XViewerAtsAttrTokenXColumn() {
      super();
   }

   public XViewerAtsAttrTokenXColumn(AttributeTypeToken attributeType, int width, XViewerAlign align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(attributeType.getName(), attributeType.getUnqualifiedName(), width, align, show, sortDataType,
         multiColumnEditable, description);
      this.attributeType = attributeType;
   }

   public XViewerAtsAttrTokenXColumn(AttributeTypeToken attributeType, String id, String name, int width, XViewerAlign align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(id, name, width, align, show, sortDataType, multiColumnEditable, description);
      this.attributeType = attributeType;
   }

   public XViewerAtsAttrTokenXColumn(AtsCoreAttrTokColumnToken columnToken) {
      super(columnToken.getId(), columnToken.getName(), columnToken.getWidth(),
         AtsColumnUtil.getXViewerAlign(columnToken.getAlign()), columnToken.isVisible(),
         AtsColumnUtil.getSortDataType(columnToken), columnToken.isColumnMultiEdit(), columnToken.getDescription());
      this.attributeType = AtsApiService.get().tokenService().getAttributeTypeOrSentinel(columnToken.getAttrTypeId());
      this.columnToken = columnToken;
      setInheritParent(columnToken.isInheritParent());
      setActionRollup(columnToken.isActionRollup());
   }

   public XViewerAtsAttrTokenXColumn(AttributeTypeToken attrType) {
      this(new AtsCoreAttrTokColumnToken(attrType));
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof Artifact && AtsApiService.get().getQueryServiceIde().getArtifact(element).isDeleted()) {
            return "<deleted>";
         }
         if (isBooleanShow() && column.getSortDataType() == SortDataType.Boolean) {
            if (element instanceof AbstractWorkflowArtifact) {

               if (attributeType.getDisplayHints().contains(DisplayHint.YesNoBoolean)) {
                  if (AtsApiService.get().getAttributeResolver().isAttributeTypeValid((IAtsWorkItem) element,
                     attributeType)) {
                     Boolean set = AtsApiService.get().getAttributeResolver().getSoleAttributeValue((Artifact) element,
                        attributeType, null);
                     if (set == null) {
                        return "";
                     } else if (set) {
                        return "Yes";
                     } else {
                        return "No";
                     }
                  }
               }
               Boolean value = ((AbstractWorkflowArtifact) element).getSoleAttributeValue(attributeType, null);
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
            if (attributeType.hasDisplayHint(AtsDisplayHint.UserArtId)) {
               List<String> names = new ArrayList<>();
               for (String userArtId : awa.getAttributesToStringList(attributeType)) {
                  if (Strings.isNumeric(userArtId)) {
                     AtsUser userById = AtsApiService.get().getUserService().getUserById(ArtifactId.valueOf(userArtId));
                     if (userById != null) {
                        names.add(userById.getName());
                     }
                  }
               }
               return Collections.toString(";", names);
            } else {
               String result = awa.getAttributesToStringUnique(attributeType, ";");
               if (Strings.isValid(result)) {
                  return result;
               }
               if (isInheritParentWithDefault() && !awa.isTeamWorkflow() && awa.getParentTeamWorkflow() != null) {
                  result = Collections.toString("; ",
                     ((Artifact) awa.getParentTeamWorkflow()).getAttributesToStringList(attributeType));
                  if (Strings.isValid(result)) {
                     return result;
                  }
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
      return booleanOnFalseShow != null || //
         booleanOnTrueShow != null || //
         booleanNotSetShow != null || (attributeType != null && attributeType.getDisplayHints().contains(
            DisplayHint.YesNoBoolean));
   }

   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      if (treeColumn != null && !treeColumn.isDisposed() && treeItem != null && !treeItem.isDisposed()) {
         return AtsColumnUtilIde.handleAltLeftClick(treeColumn.getData(), treeItem.getData(), true);
      }
      return false;
   }

   @Override
   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      if (treeColumn != null && !treeColumn.isDisposed()) {
         AtsColumnUtilIde.handleColumnMultiEdit(treeItems, attributeType, (XViewer) getXViewer());
      }
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn need to extend this constructor to copy extra stored fields
    */
   @Override
   public XViewerAtsAttrTokenXColumn copy() {
      XViewerAtsAttrTokenXColumn newXCol = new XViewerAtsAttrTokenXColumn();
      copy(this, newXCol);
      newXCol.setBooleanNotSetShow(getBooleanNotSetShow());
      newXCol.setBooleanOnFalseShow(getBooleanOnFalseShow());
      newXCol.setBooleanOnTrueShow(getBooleanOnTrueShow());
      newXCol.setInheritParent(isInheritParent());
      newXCol.setActionRollup(isActionRollup());
      newXCol.setColumnToken(getColumnToken());
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

   public AtsCoreAttrTokColumnToken getColumnToken() {
      return columnToken;
   }

   @Override
   public AttributeTypeToken getAttributeType() {
      return attributeType;
   }

   @Override
   public void setAttributeType(AttributeTypeToken attributeType) {
      this.attributeType = attributeType;
   }

   public void setColumnToken(AtsCoreAttrTokColumnToken columnToken) {
      this.columnToken = columnToken;
   }

}
