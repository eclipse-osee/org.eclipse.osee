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
package org.eclipse.osee.ats.agile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.nebula.widgets.xviewer.IAltLeftClickProvider;
import org.eclipse.nebula.widgets.xviewer.IMultiColumnEditProvider;
import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.AgileEndpointApi;
import org.eclipse.osee.ats.api.agile.IAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.JaxAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.JaxAgileItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.core.client.action.ActionManager;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.util.AtsUtilClient;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.internal.AtsJaxRsService;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.plugin.util.StringLabelProvider;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxTreeDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class AgileFeatureGroupColumn extends XViewerAtsColumn implements IXViewerValueColumn, IAltLeftClickProvider, IMultiColumnEditProvider {

   public static AgileFeatureGroupColumn instance = new AgileFeatureGroupColumn();

   public static AgileFeatureGroupColumn getInstance() {
      return instance;
   }

   private AgileFeatureGroupColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".agileFeatureGroup", "Feature Group", 40, SWT.LEFT, true,
         SortDataType.String, true, "Agile Feature Group for this Item.");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public AgileFeatureGroupColumn copy() {
      AgileFeatureGroupColumn newXCol = new AgileFeatureGroupColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      try {
         if (treeItem.getData() instanceof AbstractWorkflowArtifact) {
            AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) treeItem.getData();
            boolean modified = promptChangeFeatureGroup(Arrays.asList(awa));
            XViewer xViewer = ((XViewerColumn) treeColumn.getData()).getTreeViewer();
            if (modified && isPersistViewer(xViewer)) {
               awa.persist("persist goals via alt-left-click");
            }
            if (modified) {
               xViewer.update(awa, null);
               return true;
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

      return false;
   }

   public static boolean promptChangeFeatureGroup(AbstractWorkflowArtifact sma) throws OseeCoreException {
      if (AtsUtilClient.isAtsAdmin() && !sma.isTeamWorkflow()) {
         AWorkbench.popup("ERROR ", "Cannot set Feature Group for: \n\n" + sma.getName());
         return false;
      }
      return promptChangeFeatureGroup(Arrays.asList((TeamWorkFlowArtifact) sma));
   }

   public static boolean promptChangeFeatureGroup(final Collection<? extends AbstractWorkflowArtifact> awas) throws OseeCoreException {
      SprintItems items = new SprintItems(awas);

      if (items.isNoBacklogDetected()) {
         AWorkbench.popup("Workflow(s) must belong to a Backlog to set their Sprint.");
         return false;
      }
      if (items.isMultipleBacklogsDetected()) {
         AWorkbench.popup("All workflows must belong to same Backlog.");
         return false;
      }

      AgileEndpointApi agileApi = AtsJaxRsService.get().getAgile();
      List<JaxAgileFeatureGroup> featureGroups;
      long teamUuid = items.getCommonBacklog().getTeamUuid();
      try {
         featureGroups = agileApi.getFeatureGroups(teamUuid);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         return false;
      }

      FilteredCheckboxTreeDialog dialog =
         new FilteredCheckboxTreeDialog("Select Feature Group(s)", "Select Version", new ArrayTreeContentProvider(),
            new StringLabelProvider());
      dialog.setInput(featureGroups);
      Collection<IAgileFeatureGroup> selectedFeatureGroups = getSelectedFeatureGroups(awas);
      if (!selectedFeatureGroups.isEmpty()) {
         dialog.setInitialSelections(selectedFeatureGroups);
      }
      dialog.setShowSelectButtons(true);

      int result = dialog.open();
      if (result != 0) {
         return false;
      }

      JaxAgileItem updateItem = new JaxAgileItem();
      updateItem.setSetFeatures(true);
      for (Object obj : dialog.getResult()) {
         updateItem.getFeatures().add(((JaxAgileFeatureGroup) obj).getUuid());
      }
      for (AbstractWorkflowArtifact awa : awas) {
         updateItem.getUuids().add(Long.valueOf(awa.getArtId()));
      }

      try {
         agileApi.updateItem(teamUuid, updateItem);
         ArtifactQuery.reloadArtifacts(awas);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

      return true;
   }

   private static Collection<IAgileFeatureGroup> getSelectedFeatureGroups(Collection<? extends AbstractWorkflowArtifact> awas) {
      List<IAgileFeatureGroup> selected = new LinkedList<IAgileFeatureGroup>();
      if (awas.size() == 1) {
         for (Artifact featureArt : awas.iterator().next().getRelatedArtifacts(
            AtsRelationTypes.AgileFeatureToItem_FeatureGroup)) {
            IAgileFeatureGroup featureGroup =
               AtsClientService.get().getConfigItemFactory().getAgileFeatureGroup(featureArt);
            if (featureGroup.isActive()) {
               selected.add(featureGroup);
            }
         }
      }
      return selected;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (Artifacts.isOfType(element, AtsArtifactTypes.Action)) {
            Set<String> strs = new HashSet<String>();
            for (TeamWorkFlowArtifact team : ActionManager.getTeams(element)) {
               String str = getColumnText(team, column, columnIndex);
               if (Strings.isValid(str)) {
                  strs.add(str);
               }
            }
            return Collections.toString(", ", strs);

         } else {
            return Collections.toString(
               ", ",
               ((Artifact) ((IAtsWorkItem) element)).getRelatedArtifacts(AtsRelationTypes.AgileFeatureToItem_FeatureGroup));
         }
      } catch (OseeCoreException ex) {
         return LogUtil.getCellExceptionString(ex);
      }
   }

   @Override
   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      try {
         Set<TeamWorkFlowArtifact> awas = new HashSet<TeamWorkFlowArtifact>();
         List<Artifact> arts = new ArrayList<Artifact>();
         for (TreeItem item : treeItems) {
            Artifact art = (Artifact) item.getData();
            if (art.isOfType(AtsArtifactTypes.TeamWorkflow)) {
               awas.add((TeamWorkFlowArtifact) art);
               arts.add(art);
            }
         }

         promptChangeFeatureGroup(awas);
         getXViewer().update(awas.toArray(), null);
         return;
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

}
