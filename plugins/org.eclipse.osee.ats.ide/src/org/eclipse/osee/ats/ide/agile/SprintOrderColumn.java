/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.agile;

import org.eclipse.nebula.widgets.xviewer.IXViewerFactory;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.ide.column.AbstractMembersOrderColumn;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.workflow.goal.MembersManager;
import org.eclipse.osee.ats.ide.workflow.sprint.SprintArtifact;
import org.eclipse.osee.ats.ide.world.WorldXViewer;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;
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
public class SprintOrderColumn extends AbstractMembersOrderColumn {

   public static final String COLUMN_ID = WorldXViewerFactory.COLUMN_NAMESPACE + ".sprintOrder";
   public static SprintOrderColumn instance = new SprintOrderColumn();

   public static SprintOrderColumn getInstance() {
      return instance;
   }
   private SprintManager sprintManager;

   private SprintOrderColumn() {
      super(COLUMN_ID, "Sprint Order", 45, XViewerAlign.Left, false, SortDataType.Integer, true,
         "Order of item within displayed sprint.  Editing this field changes order.");
   }

   @Override
   public SprintOrderColumn copy() {
      SprintOrderColumn newXCol = new SprintOrderColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      try {
         XViewer xViewer = (XViewer) getXViewer();
         IXViewerFactory xViewerFactory = xViewer.getXViewerFactory();
         SprintArtifact parentSprintArtifact = null;
         if (xViewerFactory instanceof SprintXViewerFactory) {
            parentSprintArtifact = ((SprintXViewerFactory) xViewerFactory).getSoleSprintArtifact();
         }
         if (parentSprintArtifact == null) {
            parentSprintArtifact = getParentSprintArtifact(treeItem);
         }
         SprintArtifact changedSprint = null;
         if (treeItem.getData() instanceof Artifact) {
            if (parentSprintArtifact != null) {
               changedSprint = new SprintManager().promptChangeMemberOrder(parentSprintArtifact,
                  AtsClientService.get().getQueryServiceClient().getArtifact(treeItem));
            } else {
               changedSprint = new SprintManager().promptChangeSprintOrder(
                  AtsClientService.get().getQueryServiceClient().getArtifact(treeItem));
            }

            if (changedSprint != null) {
               xViewer.refresh(changedSprint);
               xViewer.update(treeItem.getData(), null);
            }
         }
         return true;
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         return false;
      }
   }

   public static SprintArtifact getParentSprintArtifact(TreeItem treeItem) {
      if (Widgets.isAccessible(treeItem) && Widgets.isAccessible(treeItem.getParentItem()) && Artifacts.isOfType(
         treeItem.getParentItem().getData(), AtsArtifactTypes.AgileSprint)) {
         return (SprintArtifact) treeItem.getParentItem().getData();
      }
      return null;
   }

   @Override
   public Artifact getParentMembersArtifact(WorldXViewer worldXViewer) {
      return worldXViewer.getParentSprintArtifact();
   }

   @Override
   public MembersManager<?> getMembersManager() {
      if (sprintManager == null) {
         sprintManager = new SprintManager();
      }
      return sprintManager;
   }

}
