/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.osee.ats.ide.workflow.task.widgets.estimates;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.OseeTreeReportAdapter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class TaskEstXViewer extends XViewer {

   private final XTaskEstManager xTaskEstManager;
   private final AtsApi atsApi;
   private final IAtsTeamWorkflow teamWf;
   private AttributeTypeToken pointsAttrType;

   public TaskEstXViewer(Composite parent, int style, XTaskEstManager xTaskEstManager, IAtsTeamWorkflow teamWf, AtsApi atsApi) {
      super(parent, style, new TaskEstFactory(new OseeTreeReportAdapter("Table Report - " + XTaskEstManager.NAME)));
      this.xTaskEstManager = xTaskEstManager;
      this.teamWf = teamWf;
      this.atsApi = atsApi;
   }

   @Override
   public void dispose() {
      getLabelProvider().dispose();
   }

   public List<TaskEstDefinition> getSelected() {
      List<TaskEstDefinition> teds = new ArrayList<>();
      TreeItem items[] = getTree().getSelection();
      if (items.length > 0) {
         for (TreeItem item : items) {
            teds.add((TaskEstDefinition) item.getData());
         }
      }
      return teds;
   }

   public XTaskEstManager getXCommitViewer() {
      return xTaskEstManager;
   }

   @Override
   public void handleDoubleClick() {
      try {
         TaskEstDefinition ted = getSelected().iterator().next();
         if (ted.hasTask()) {
            WorkflowEditor.edit(ted.getTask());
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public boolean handleLeftClickInIconArea(TreeColumn treeColumn, TreeItem treeItem) {
      TaskEstDefinition ted = getSelected().iterator().next();
      if (ted.hasTask()) {
         return false;
      }
      ted.setChecked(!ted.isChecked());
      refresh(ted);
      return true;
   }

   public AttributeTypeToken getPointsAttrType() {
      if (pointsAttrType == null) {
         IAgileTeam agileTeam = atsApi.getAgileService().getAgileTeam(teamWf);
         if (agileTeam != null) {
            pointsAttrType = atsApi.getAgileService().getAgileTeamPointsAttributeType(agileTeam);
         }
         if (pointsAttrType == null) {
            pointsAttrType = AtsAttributeTypes.PointsNumeric;
         }
      }
      return pointsAttrType;
   }

}
