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
package org.eclipse.osee.ats.column;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.XViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.workflow.task.TaskArtifact;
import org.eclipse.osee.ats.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;

/**
 * @author Donald G. Dunne
 */
public class OperationalImpactWorkaroundColumn extends XViewerValueColumn {

   public static OperationalImpactWorkaroundColumn instance = new OperationalImpactWorkaroundColumn();

   public static OperationalImpactWorkaroundColumn getInstance() {
      return instance;
   }

   private OperationalImpactWorkaroundColumn() {
      super("ats.Operational Impact Workaround", "Operational Impact Workaround", 80, XViewerAlign.Left, false,
         SortDataType.String, true, "Does operational impact to the product have a workaround?");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public OperationalImpactWorkaroundColumn copy() {
      OperationalImpactWorkaroundColumn newXCol = new OperationalImpactWorkaroundColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      if (AtsObjects.isAtsWorkItemOrAction(element)) {
         try {
            return getOperationalImpact(AtsClientService.get().getQueryServiceClient().getArtifact(element));
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
            return LogUtil.getCellExceptionString(ex);
         }
      }
      return "";
   }

   private String getOperationalImpact(Artifact art) {
      if (art.isOfType(AtsArtifactTypes.TeamWorkflow)) {
         return ((TeamWorkFlowArtifact) art).getSoleAttributeValue(AtsAttributeTypes.OperationalImpactWorkaround, "");
      }
      if (art.isOfType(AtsArtifactTypes.Action)) {
         Set<String> strs = new HashSet<>();
         for (IAtsTeamWorkflow team : AtsClientService.get().getWorkItemService().getTeams(art)) {
            strs.add(getOperationalImpact(AtsClientService.get().getQueryServiceClient().getArtifact(team)));
         }
         return Collections.toString(", ", strs);
      }
      if (art.isOfType(AtsArtifactTypes.Task)) {
         return getOperationalImpact(((TaskArtifact) art).getParentTeamWorkflow());
      }
      return "";
   }

}
