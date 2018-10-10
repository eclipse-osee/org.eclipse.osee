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

import org.eclipse.nebula.widgets.xviewer.IXViewerFactory;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.workflow.goal.GoalArtifact;
import org.eclipse.osee.ats.workflow.goal.GoalManager;
import org.eclipse.osee.ats.workflow.goal.GoalXViewerFactory;
import org.eclipse.osee.ats.workflow.goal.MembersManager;
import org.eclipse.osee.ats.world.WorldXViewer;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class GoalOrderColumn extends AbstractMembersOrderColumn {

   public static final String COLUMN_ID = WorldXViewerFactory.COLUMN_NAMESPACE + ".goalOrder";
   private boolean backlog = false;
   private GoalManager goalManager;
   static GoalOrderColumn instance = new GoalOrderColumn();

   public static GoalOrderColumn getInstance() {
      return instance;
   }

   public GoalOrderColumn() {
      this(false, COLUMN_ID, "Goal Order");
   }

   protected GoalOrderColumn(boolean backlog, String id, String name) {
      super(id, name, DEFAULT_WIDTH, XViewerAlign.Left, false, SortDataType.Integer, true,
         "Order of item within displayed " + (backlog ? "Backlog" : "Goal") + ".  Editing this field changes order.");
      this.backlog = backlog;
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public GoalOrderColumn copy() {
      GoalOrderColumn newXCol = new GoalOrderColumn(backlog, getId(), getName());
      super.copy(this, newXCol);
      newXCol.setBacklog(backlog);
      return newXCol;
   }

   private void setBacklog(boolean backlog) {
      this.backlog = backlog;
   }

   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      try {
         XViewer xViewer = (XViewer) getXViewer();
         IXViewerFactory xViewerFactory = xViewer.getXViewerFactory();
         GoalArtifact parentGoalArtifact = null;
         if (xViewerFactory instanceof GoalXViewerFactory) {
            parentGoalArtifact = ((GoalXViewerFactory) xViewerFactory).getSoleGoalArtifact();
         }
         if (parentGoalArtifact == null) {
            parentGoalArtifact = getParentGoalArtifact(treeItem);
         }
         GoalArtifact changedGoal = null;
         if (treeItem.getData() instanceof Artifact) {
            if (parentGoalArtifact != null) {
               changedGoal = new GoalManager().promptChangeMemberOrder(parentGoalArtifact,
                  AtsClientService.get().getQueryServiceClient().getArtifact(treeItem));
            } else {
               changedGoal = new GoalManager().promptChangeGoalOrder(
                  AtsClientService.get().getQueryServiceClient().getArtifact(treeItem));
            }
            if (changedGoal != null) {
               xViewer.refresh(changedGoal);
               xViewer.update(treeItem.getData(), null);
            }
            return true;
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public IArtifactType getArtifactType() {
      return AtsArtifactTypes.Goal;
   }

   public GoalArtifact getParentGoalArtifact(TreeItem treeItem) {
      return getParentGoalArtifact(treeItem, getArtifactType());
   }

   public static GoalArtifact getParentGoalArtifact(TreeItem treeItem, IArtifactType artifactType) {
      if (Widgets.isAccessible(treeItem) && Widgets.isAccessible(treeItem.getParentItem()) && Artifacts.isOfType(
         treeItem.getParentItem().getData(), artifactType)) {
         return (GoalArtifact) treeItem.getParentItem().getData();
      }
      return null;
   }

   @Override
   public Artifact getParentMembersArtifact(WorldXViewer worldXViewer) {
      return worldXViewer.getParentGoalArtifact();
   }

   @Override
   public MembersManager<?> getMembersManager() {
      if (goalManager == null) {
         goalManager = new GoalManager();
      }
      return goalManager;
   }

}
