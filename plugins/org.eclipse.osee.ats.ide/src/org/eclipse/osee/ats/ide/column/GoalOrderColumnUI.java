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

import org.eclipse.nebula.widgets.xviewer.IXViewerFactory;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault;
import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault.CoreCodeColumnTokenDefault;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.goal.GoalArtifact;
import org.eclipse.osee.ats.ide.workflow.goal.GoalManager;
import org.eclipse.osee.ats.ide.workflow.goal.GoalXViewerFactory;
import org.eclipse.osee.ats.ide.workflow.goal.MembersManager;
import org.eclipse.osee.ats.ide.world.WorldXViewer;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
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
public class GoalOrderColumnUI extends AbstractMembersOrderColumnUI {

   public static final String COLUMN_ID = WorldXViewerFactory.COLUMN_NAMESPACE + ".goalOrder";
   private boolean backlog = false;
   private GoalManager goalManager;
   static GoalOrderColumnUI instance = new GoalOrderColumnUI();

   public static GoalOrderColumnUI getInstance() {
      return instance;
   }

   public GoalOrderColumnUI() {
      this(false, AtsColumnTokensDefault.GoalOrderColumn);
   }

   protected GoalOrderColumnUI(boolean backlog, CoreCodeColumnTokenDefault columnToken) {
      super(columnToken);
      this.backlog = backlog;
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public GoalOrderColumnUI copy() {
      GoalOrderColumnUI newXCol = new GoalOrderColumnUI(backlog, AtsColumnTokensDefault.GoalOrderColumn);
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
                  AtsApiService.get().getQueryServiceIde().getArtifact(treeItem));
            } else {
               changedGoal = new GoalManager().promptChangeGoalOrder(
                  AtsApiService.get().getQueryServiceIde().getArtifact(treeItem));
            }
            if (changedGoal != null) {
               xViewer.refresh(changedGoal);
               xViewer.update(treeItem.getData(), null);
               return true;
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public ArtifactTypeToken getArtifactType() {
      return AtsArtifactTypes.Goal;
   }

   public GoalArtifact getParentGoalArtifact(TreeItem treeItem) {
      return getParentGoalArtifact(treeItem, getArtifactType());
   }

   public static GoalArtifact getParentGoalArtifact(TreeItem treeItem, ArtifactTypeToken artifactType) {
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
