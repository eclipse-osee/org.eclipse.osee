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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.core.client.action.ActionManager;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class ActionableItemsColumn extends XViewerAtsAttributeValueColumn {

   public static ActionableItemsColumn instance = new ActionableItemsColumn();

   public static ActionableItemsColumn getInstance() {
      return instance;
   }

   private ActionableItemsColumn() {
      super(AtsAttributeTypes.ActionableItem, WorldXViewerFactory.COLUMN_NAMESPACE + ".actionableItems",
         AtsAttributeTypes.ActionableItem.getUnqualifiedName(), 80, SWT.LEFT, true, SortDataType.String, false, "");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public ActionableItemsColumn copy() {
      ActionableItemsColumn newXCol = new ActionableItemsColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         return getActionableItemsStr(element);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return "";
   }

   public static String getActionableItemsStr(Object element) throws OseeCoreException {
      return Artifacts.commaArts(getActionableItems(element));
   }

   public static Collection<IAtsActionableItem> getActionableItems(Object element) throws OseeCoreException {
      if (Artifacts.isOfType(element, AtsArtifactTypes.Action)) {
         Set<IAtsActionableItem> aias = new HashSet<IAtsActionableItem>();
         // Roll up if same for all children
         for (TeamWorkFlowArtifact team : ActionManager.getTeams(element)) {
            aias.addAll(team.getActionableItemsDam().getActionableItems());
         }
         return aias;
      } else if (Artifacts.isOfType(element, AtsArtifactTypes.TeamWorkflow)) {
         return ((TeamWorkFlowArtifact) element).getActionableItemsDam().getActionableItems();
      } else if (element instanceof AbstractWorkflowArtifact) {
         TeamWorkFlowArtifact teamArt = ((AbstractWorkflowArtifact) element).getParentTeamWorkflow();
         return getActionableItems(teamArt);
      }
      return Collections.emptyList();
   }
}
