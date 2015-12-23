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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.core.client.action.ActionManager;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class TitleColumn extends XViewerAtsAttributeValueColumn {

   public static TitleColumn instance = new TitleColumn();

   public static TitleColumn getInstance() {
      return instance;
   }

   private TitleColumn() {
      super(CoreAttributeTypes.Name, "framework.artifact.name.Title", "Title", 150, SWT.LEFT, true, SortDataType.String,
         true, "");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public TitleColumn copy() {
      TitleColumn newXCol = new TitleColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof Artifact && ((Artifact) element).isDeleted()) {
            return "<deleted>";
         }
         if (element instanceof AbstractWorkflowArtifact) {
            return ((AbstractWorkflowArtifact) element).getAttributesToStringUnique(getAttributeType(), ";");
         }
         if (Artifacts.isOfType(element, AtsArtifactTypes.Action)) {
            Collection<TeamWorkFlowArtifact> teams = ActionManager.getTeams(element);
            Set<String> strs = new HashSet<>();
            strs.add(((Artifact) element).getName());
            for (TeamWorkFlowArtifact team : teams) {
               String str = getColumnText(team, column, columnIndex);
               if (Strings.isValid(str)) {
                  strs.add(str);
               }
            }
            if (strs.size() > 5) {
               return ((Artifact) element).getName();
            }
            return Collections.toString("; ", strs);
         }
      } catch (Exception ex) {
         return LogUtil.getCellExceptionString(ex);
      }
      return null;
   }

}
