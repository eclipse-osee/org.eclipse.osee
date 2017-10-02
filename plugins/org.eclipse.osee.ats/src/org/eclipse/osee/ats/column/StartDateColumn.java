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
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;

/**
 * @author Donald G. Dunne
 */
public class StartDateColumn extends XViewerAtsAttributeValueColumn {

   public static StartDateColumn instance = new StartDateColumn();

   public static StartDateColumn getInstance() {
      return instance;
   }

   private StartDateColumn() {
      super(AtsAttributeTypes.StartDate, WorldXViewerFactory.COLUMN_NAMESPACE + ".startDate",
         AtsAttributeTypes.StartDate.getUnqualifiedName(), 80, XViewerAlign.Left, false, SortDataType.Date, true, "");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public StartDateColumn copy() {
      StartDateColumn newXCol = new StartDateColumn();
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

   public static Date getDate(Object object)  {
      if (Artifacts.isOfType(object, AtsArtifactTypes.Action)) {
         return getDate(AtsClientService.get().getWorkItemService().getFirstTeam(object));
      } else if (object instanceof AbstractWorkflowArtifact) {
         return ((AbstractWorkflowArtifact) object).getSoleAttributeValue(AtsAttributeTypes.StartDate, null);
      }
      return null;
   }

   public static String getDateStr(Object object)  {
      Set<String> strs = new HashSet<>();
      if (Artifacts.isOfType(object, AtsArtifactTypes.Action)) {
         for (IAtsTeamWorkflow team : AtsClientService.get().getWorkItemService().getTeams(object)) {
            Date date = getDate(team);
            if (date == null) {
               strs.add("");
            } else {
               strs.add(DateUtil.getMMDDYY(getDate(team)));
            }
         }
         return Collections.toString(";", strs);

      }
      return DateUtil.getMMDDYY(getDate(object));
   }

   @Override
   public Object getBackingData(Object element, XViewerColumn xCol, int columnIndex) throws Exception {
      return getDate(element);
   }

}
