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
import java.util.Map;
import java.util.Set;
import org.eclipse.nebula.widgets.xviewer.IAltLeftClickProvider;
import org.eclipse.nebula.widgets.xviewer.IMultiColumnEditProvider;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.agile.AgileEndpointApi;
import org.eclipse.osee.ats.api.agile.IAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.JaxAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.JaxAgileItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.column.IAtsXViewerPreComputedColumn;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.plugin.util.StringLabelProvider;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;
import org.eclipse.osee.framework.ui.skynet.util.StringNameComparator;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxTreeDialog;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class AgileFeatureGroupColumn extends XViewerAtsColumn implements IAtsXViewerPreComputedColumn, IAltLeftClickProvider, IMultiColumnEditProvider {

   public static AgileFeatureGroupColumn instance = new AgileFeatureGroupColumn();

   public static AgileFeatureGroupColumn getInstance() {
      return instance;
   }

   private AgileFeatureGroupColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".agileFeatureGroup", "Feature Group", 40, XViewerAlign.Left, false,
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
            XViewer xViewer = (XViewer) ((XViewerColumn) treeColumn.getData()).getXViewer();
            if (modified && isPersistViewer(xViewer)) {
               awa.persist("persist goals via alt-left-click");
            }
            if (modified) {
               populateCachedValues(java.util.Collections.singleton(awa), preComputedValueMap);
               xViewer.update(awa, null);
               return true;
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

      return false;
   }

   public static boolean promptChangeFeatureGroup(final Collection<? extends AbstractWorkflowArtifact> awas) {
      SprintItems items = new SprintItems(awas);

      if (items.isNoBacklogDetected()) {
         AWorkbench.popup("Workflow(s) must belong to a Backlog to set their Feature Group.");
         return false;
      }
      if (items.isMultipleBacklogsDetected()) {
         AWorkbench.popup("All workflows must belong to same Backlog.");
         return false;
      }

      AgileEndpointApi agileEp = AtsClientService.getAgileEndpoint();
      List<JaxAgileFeatureGroup> activeFeatureGroups = new ArrayList<>();
      long teamId = items.getCommonBacklog().getTeamId();
      try {
         for (JaxAgileFeatureGroup feature : agileEp.getFeatureGroups(items.getCommonBacklog().getTeamId())) {
            if (feature.isActive()) {
               activeFeatureGroups.add(feature);
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         return false;
      }

      FilteredCheckboxTreeDialog dialog =
         new FilteredCheckboxTreeDialog("Select Feature Group(s)", "Select Feature Group(s)",
            new ArrayTreeContentProvider(), new StringLabelProvider(), new StringNameComparator());
      dialog.setInput(activeFeatureGroups);
      Collection<JaxAgileFeatureGroup> selectedFeatureGroups = getSelectedFeatureGroups(awas);
      if (!selectedFeatureGroups.isEmpty()) {
         dialog.setInitialSelections(selectedFeatureGroups);
      }
      dialog.setShowSelectButtons(true);

      int result = dialog.open();
      if (result != 0) {
         return false;
      }

      JaxAgileItem updateItem = new JaxAgileItem();
      if (dialog.getResult().length == 0) {
         updateItem.setRemoveFeatures(true);
      } else {
         updateItem.setSetFeatures(true);
         for (Object obj : dialog.getResult()) {
            updateItem.getFeatures().add(((JaxAgileFeatureGroup) obj).getId());
         }
      }
      for (AbstractWorkflowArtifact awa : awas) {
         updateItem.getIds().add(Long.valueOf(awa.getArtId()));
      }

      try {
         agileEp.updateAgileItem(teamId, updateItem);
         ArtifactQuery.reloadArtifacts(awas);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

      return true;
   }

   private static Collection<JaxAgileFeatureGroup> getSelectedFeatureGroups(Collection<? extends AbstractWorkflowArtifact> awas) {
      List<JaxAgileFeatureGroup> selected = new LinkedList<>();
      if (awas.size() == 1) {
         for (Artifact featureArt : awas.iterator().next().getRelatedArtifacts(
            AtsRelationTypes.AgileFeatureToItem_FeatureGroup)) {
            IAgileFeatureGroup featureGroup = AtsClientService.get().getAgileService().getAgileFeatureGroup(featureArt);
            if (featureGroup.isActive()) {
               selected.add(createJaxAgileFeatureGroupFromAgileFeatureGroup(featureGroup));
            }
         }
      }
      return selected;
   }

   private static JaxAgileFeatureGroup createJaxAgileFeatureGroupFromAgileFeatureGroup(IAgileFeatureGroup group) {
      JaxAgileFeatureGroup newGroup = new JaxAgileFeatureGroup();
      newGroup.setName(group.getName());
      newGroup.setId(group.getId());
      newGroup.setActive(group.isActive());
      newGroup.setTeamId(group.getTeamId());
      return newGroup;
   }

   @Override
   public Long getKey(Object obj) {
      Long result = 0L;
      if (obj instanceof IAtsObject) {
         result = ((IAtsObject) obj).getId();
      } else if (obj instanceof ArtifactId) {
         result = ((ArtifactId) obj).getId();
      }
      return result;
   }

   @Override
   public void populateCachedValues(Collection<?> objects, Map<Long, String> preComputedValueMap) {
      Collection<ArtifactId> workItemArts = AtsObjects.getTeamWfArtifacts(objects, AtsClientService.get());
      // Change NamedId to ArtifactToken when merge to 25.0
      for (ArtifactId workItemId : workItemArts) {
         try {
            Artifact workItem = AtsClientService.get().getQueryServiceClient().getArtifact(workItemId);
            List<Artifact> featureArts = workItem.getRelatedArtifacts(AtsRelationTypes.AgileFeatureToItem_FeatureGroup);
            if (Artifacts.isOfType(workItem, AtsArtifactTypes.Action)) {
               Set<String> strs = new HashSet<>();
               for (IAtsTeamWorkflow teamWf : AtsClientService.get().getWorkItemService().getTeams(workItem)) {
                  for (ArtifactToken featureArt : AtsClientService.get().getQueryServiceClient().getArtifact(
                     teamWf).getRelatedArtifacts(AtsRelationTypes.AgileFeatureToItem_FeatureGroup)) {
                     strs.add(featureArt.getName());
                  }
               }
               preComputedValueMap.put(getKey(workItem), Collections.toString(", ", strs));
            } else {
               Set<String> strs = new HashSet<>();
               for (ArtifactToken featureArt : featureArts) {
                  strs.add(featureArt.getName());
               }
               preComputedValueMap.put(getKey(workItem), Collections.toString(", ", strs));
            }
         } catch (OseeCoreException ex) {
            preComputedValueMap.put(getKey(workItemId), LogUtil.getCellExceptionString(ex));
         }
      }
   }

   @Override
   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      try {
         Set<AbstractWorkflowArtifact> awas = new HashSet<>();
         List<Artifact> arts = new ArrayList<>();
         for (TreeItem item : treeItems) {
            Artifact art = AtsClientService.get().getQueryServiceClient().getArtifact(item);
            if (art != null && art.isOfType(AtsArtifactTypes.AbstractWorkflowArtifact)) {
               awas.add((AbstractWorkflowArtifact) art);
               arts.add(art);
            }
         }

         promptChangeFeatureGroup(awas);
         populateCachedValues(awas, preComputedValueMap);

         ((XViewer) getXViewer()).update(awas.toArray(), null);
         return;
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

}
