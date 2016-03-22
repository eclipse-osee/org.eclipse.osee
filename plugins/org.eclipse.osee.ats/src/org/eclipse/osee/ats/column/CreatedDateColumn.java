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

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.core.client.action.ActionManager;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;

/**
 * @author Donald G. Dunne
 */
public class CreatedDateColumn extends XViewerAtsColumn implements IXViewerValueColumn {

   public static CreatedDateColumn instance = new CreatedDateColumn();

   public static CreatedDateColumn getInstance() {
      return instance;
   }

   private CreatedDateColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".createdDate", "Created Date", 80, XViewerAlign.Left, true,
         SortDataType.Date, false, "Date this workflow was created.");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public CreatedDateColumn copy() {
      CreatedDateColumn newXCol = new CreatedDateColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         return getDateStr(element);
      } catch (OseeCoreException ex) {
         LogUtil.getCellExceptionString(ex);
      }
      return "";
   }

   public static Date getDate(Object object) throws OseeCoreException {
      if (Artifacts.isOfType(object, AtsArtifactTypes.Action)) {
         return getDate(ActionManager.getFirstTeam(object));
      } else if (object instanceof AbstractWorkflowArtifact) {
         return ((AbstractWorkflowArtifact) object).getCreatedDate();
      }
      return null;
   }

   public static String getDateStr(Object object) throws OseeCoreException {
      Set<String> strs = new HashSet<>();
      if (Artifacts.isOfType(object, AtsArtifactTypes.Action)) {
         for (TeamWorkFlowArtifact team : ActionManager.getTeams(object)) {
            Date date = getDate(team);
            if (date == null) {
               strs.add("");
            } else {
               strs.add(DateUtil.getMMDDYYHHMM(getDate(team)));
            }
         }
         return Collections.toString(";", strs);

      }
      return DateUtil.getMMDDYYHHMM(getDate(object));
   }

   @Override
   public Object getBackingData(Object element, XViewerColumn xCol, int columnIndex) throws Exception {
      return getDate(element);
   }

}
