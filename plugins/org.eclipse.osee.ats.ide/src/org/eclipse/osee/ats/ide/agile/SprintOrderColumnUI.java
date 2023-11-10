/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.ats.ide.agile;

import org.eclipse.nebula.widgets.xviewer.IXViewerFactory;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.ide.column.AbstractMembersOrderColumnUI;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
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
public class SprintOrderColumnUI extends AbstractMembersOrderColumnUI {

   public static final String COLUMN_ID = WorldXViewerFactory.COLUMN_NAMESPACE + ".sprintOrder";
   public static SprintOrderColumnUI instance = new SprintOrderColumnUI();
   private SprintManager sprintManager;

   public static SprintOrderColumnUI getInstance() {
      return instance;
   }

   private SprintOrderColumnUI() {
      super(AtsColumnTokensDefault.SprintOrderColumn);
   }

   @Override
   public SprintOrderColumnUI copy() {
      SprintOrderColumnUI newXCol = new SprintOrderColumnUI();
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
                  AtsApiService.get().getQueryServiceIde().getArtifact(treeItem));
            } else {
               changedSprint = new SprintManager().promptChangeSprintOrder(
                  AtsApiService.get().getQueryServiceIde().getArtifact(treeItem));
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
