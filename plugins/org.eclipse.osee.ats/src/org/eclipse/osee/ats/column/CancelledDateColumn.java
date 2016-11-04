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
import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.core.client.action.ActionManager;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;

/**
 * @author Donald G. Dunne
 */
public class CancelledDateColumn extends XViewerAtsColumn implements IXViewerValueColumn {

   public static CancelledDateColumn instance = new CancelledDateColumn();

   public static CancelledDateColumn getInstance() {
      return instance;
   }

   private CancelledDateColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".cancelledDate", "Cancelled Date", 80, XViewerAlign.Center, false,
         SortDataType.Date, false, null);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public CancelledDateColumn copy() {
      CancelledDateColumn newXCol = new CancelledDateColumn();
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
         getDate(ActionManager.getFirstTeam(object));
      } else if (object instanceof AbstractWorkflowArtifact) {
         AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) object;
         if (((AbstractWorkflowArtifact) object).isCancelled()) {
            Date date = ((AbstractWorkflowArtifact) object).internalGetCancelledDate();
            if (date == null) {
               OseeLog.log(Activator.class, Level.SEVERE, "Cancelled with no date => " + awa.getAtsId());
            }
            return date;
         }
      }
      return null;
   }

   public static String getDateStr(Object object) throws OseeCoreException {
      if (Artifacts.isOfType(object, AtsArtifactTypes.Action)) {
         Set<String> strs = new HashSet<>();
         for (TeamWorkFlowArtifact team : ActionManager.getTeams(object)) {
            String str = getDateStr(team);
            if (Strings.isValid(str)) {
               strs.add(str);
            }
         }
         return Collections.toString(";", strs);
      }
      return DateUtil.getMMDDYYHHMM(getDate(object));
   }

   @Override
   public Object getBackingData(Object element, XViewerColumn xCol, int columnIndex) throws Exception {
      if (!Artifacts.isOfType(element, AtsArtifactTypes.Action)) {
         return getDate(element);
      }
      return null;
   }
}
