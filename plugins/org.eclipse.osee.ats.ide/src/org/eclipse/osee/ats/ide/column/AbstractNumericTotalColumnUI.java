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

package org.eclipse.osee.ats.ide.column;

import org.eclipse.nebula.widgets.xviewer.IAltLeftClickProvider;
import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractNumericTotalColumnUI extends XViewerAtsColumn implements IXViewerValueColumn, IAltLeftClickProvider {

   private final String calulationStr;
   private final AttributeTypeToken pointsAttrType;

   public AbstractNumericTotalColumnUI(String id, String name, String description, String calulationStr, AttributeTypeToken pointsAttrType) {
      super(id, name, 40, XViewerAlign.Center, false, SortDataType.Float, false, description);
      this.calulationStr = calulationStr;
      this.pointsAttrType = pointsAttrType;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      if (element instanceof AbstractWorkflowArtifact) {
         return AtsUtil.doubleToI18nString(getRemainingPoints(element));
      } else if (element instanceof IAtsWorkItem) {
         return getColumnText(((IAtsWorkItem) element).getStoreObject(), column, columnIndex);
      }
      return "";
   }

   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      try {
         AbstractWorkflowArtifact aba = null;
         if (treeItem.getData() instanceof AbstractWorkflowArtifact) {
            aba = (AbstractWorkflowArtifact) treeItem.getData();
         } else if (Artifacts.isOfType(treeItem.getData(),
            AtsArtifactTypes.Action) && AtsApiService.get().getWorkItemService().getTeams(
               treeItem.getData()).size() == 1) {
            aba = (AbstractWorkflowArtifact) AtsApiService.get().getWorkItemService().getFirstTeam(
               treeItem.getData()).getStoreObject();
         }
         if (aba != null) {
            AWorkbench.popup("Calculated Field", getDescription() + "\n\n" + calulationStr);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   private double getRemainingPoints(Object object) {
      if (object instanceof AbstractWorkflowArtifact) {
         return getRemainPointsFromArtifact((IAtsWorkItem) object);
      } else if (Artifacts.isOfType(object, AtsArtifactTypes.Action)) {
         double hours = 0;
         // Add up points for all children
         for (IAtsTeamWorkflow team : AtsApiService.get().getWorkItemService().getTeams(object)) {
            hours += getRemainingPoints(team);
         }
         return hours;
      }
      return 0;
   }

   private double getRemainPointsFromArtifact(IAtsWorkItem workItem) {
      if (workItem.getCurrentStateType().isCompletedOrCancelled()) {
         return 0;
      }
      double est = getTotalPoints(workItem);
      if (est > 0) {
         int percentComplete = getPercentComplete(workItem);
         est = est - est * percentComplete / 100.0;
      }
      return est;
   }

   private double getTotalPoints(IAtsWorkItem workItem) {
      IAttributeResolver attributeResolver = AtsApiService.get().getAttributeResolver();
      double est;
      if (pointsAttrType.isDouble()) {
         est = attributeResolver.getSoleAttributeValue(workItem, pointsAttrType, 0.0);
      } else {
         est = Double.valueOf(attributeResolver.getSoleAttributeValue(workItem, pointsAttrType, "0"));
      }
      return est;
   }

   abstract protected int getPercentComplete(IAtsWorkItem workItem);

}
