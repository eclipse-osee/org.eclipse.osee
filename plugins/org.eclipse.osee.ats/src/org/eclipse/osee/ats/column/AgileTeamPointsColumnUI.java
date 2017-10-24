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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.nebula.widgets.xviewer.IAltLeftClickProvider;
import org.eclipse.nebula.widgets.xviewer.IMultiColumnEditProvider;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.core.column.AtsColumnToken;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.PromptChangeUtil;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumnIdColumn;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class AgileTeamPointsColumnUI extends XViewerAtsColumnIdColumn implements IAltLeftClickProvider, IMultiColumnEditProvider {

   public static AgileTeamPointsColumnUI instance = new AgileTeamPointsColumnUI();

   public static AgileTeamPointsColumnUI getInstance() {
      return instance;
   }

   private AgileTeamPointsColumnUI() {
      super(AtsColumnToken.AgileTeamPointsColumn);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public XViewerAtsColumnIdColumn copy() {
      XViewerAtsColumnIdColumn newXCol = new AgileTeamPointsColumnUI();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      try {
         if (treeItem.getData() instanceof IAtsWorkItem) {
            IAtsWorkItem workItem = (IAtsWorkItem) treeItem.getData();
            boolean modified = promptChange(Collections.singleton(workItem));
            if (modified) {
               XViewer xViewer = (XViewer) ((XViewerColumn) treeColumn.getData()).getXViewer();
               xViewer.update(workItem, null);
               return true;
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   @Override
   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      List<IAtsWorkItem> workItems = new ArrayList<>();
      for (TreeItem treeItem : treeItems) {
         IAtsWorkItem workItem = (IAtsWorkItem) treeItem.getData();
         workItems.add(workItem);
      }
      boolean modified = promptChange(workItems);
      if (modified) {
         XViewer xViewer = (XViewer) ((XViewerColumn) treeColumn.getData()).getXViewer();
         xViewer.update(workItems.toArray(new Object[workItems.size()]), null);
      }
   }

   private boolean promptChange(Collection<IAtsWorkItem> workItems) {
      boolean modified = false;
      IAtsWorkItem firstWf = workItems.iterator().next();
      IAgileTeam agileTeam = AtsClientService.get().getAgileService().getAgileTeam(firstWf);
      if (agileTeam == null) {
         AWorkbench.popup("Can not determine Agile Team for %s", firstWf.toStringWithId());
         return false;
      }
      AttributeTypeId attributeType =
         AtsClientService.get().getAgileService().getAgileTeamPointsAttributeType(agileTeam);
      if (attributeType == null) {
         AWorkbench.popup("Can not determine Agile Team points attribute type for team %s", agileTeam.toStringWithId());
         return false;
      }

      if (AtsAttributeTypes.Points.equals(attributeType)) {
         if (PromptChangeUtil.promptChangeAttributeWI(workItems, AtsAttributeTypes.Points, true, false)) {
            modified = true;
         }
      } else if (AtsAttributeTypes.PointsNumeric.equals(attributeType)) {
         if (PromptChangeUtil.promptChangeAttributeWI(workItems, AtsAttributeTypes.PointsNumeric, true, false)) {
            modified = true;
         }
      }
      return modified;
   }

}
