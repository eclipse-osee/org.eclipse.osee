/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.ide.column.ev;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.nebula.widgets.xviewer.IAltLeftClickProvider;
import org.eclipse.nebula.widgets.xviewer.IMultiColumnEditProvider;
import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.column.AtsColumnToken;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsEditors;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.artifact.massEditor.MassArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.cm.OseeCmEditor;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * Abstract class provided to get information from related Work Package
 *
 * @author Donald G. Dunne
 */
public abstract class AbstractWorkPackageRelatedColumnUI extends XViewerAtsColumn implements IMultiColumnEditProvider, IXViewerValueColumn, IAltLeftClickProvider {

   public AbstractWorkPackageRelatedColumnUI(AtsColumnToken column, String id, String name, int width, XViewerAlign align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(id, name, width, align, show, sortDataType, multiColumnEditable, description);
   }

   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      boolean modified = false;
      try {
         if (treeItem.getData() instanceof Artifact) {
            Artifact selectedArt = AtsApiService.get().getQueryServiceIde().getArtifact(treeItem);
            AbstractWorkflowArtifact useAwa = null;
            if (selectedArt instanceof IAtsAction && AtsApiService.get().getWorkItemService().getTeams(
               selectedArt).size() == 1) {
               useAwa = (AbstractWorkflowArtifact) AtsApiService.get().getWorkItemService().getFirstTeam(
                  selectedArt).getStoreObject();
            } else if (selectedArt instanceof AbstractWorkflowArtifact) {
               useAwa = (AbstractWorkflowArtifact) selectedArt;
            }
            if (useAwa != null) {
               openSelectedWorkPackages(Arrays.asList(useAwa));
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return modified;
   }

   @Override
   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      try {
         Set<AbstractWorkflowArtifact> awas = new HashSet<>();
         for (TreeItem item : treeItems) {
            if (item.getData() instanceof AbstractWorkflowArtifact) {
               AbstractWorkflowArtifact art = (AbstractWorkflowArtifact) item.getData();
               awas.add(art);
            }
         }
         if (awas.isEmpty()) {
            AWorkbench.popup("No Work Items Selected");
         } else {
            openSelectedWorkPackages(awas);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   private void openSelectedWorkPackages(Collection<AbstractWorkflowArtifact> awas) {
      List<ArtifactId> ids = new ArrayList<>();
      for (AbstractWorkflowArtifact awa : awas) {
         IAtsWorkPackage workPkg = AtsApiService.get().getEarnedValueService().getWorkPackage((IAtsWorkItem) awa);
         if (workPkg != null) {
            if (!ids.contains(workPkg.getStoreObject())) {
               ids.add(workPkg.getArtifactId());
            }
         }
      }
      if (ids.isEmpty()) {
         AWorkbench.popup("No Work Packages set for selected Work Items");
      } else {
         if (ids.size() == 1) {
            AtsEditors.openArtifact(
               ArtifactQuery.getArtifactFromId(ids.iterator().next(), AtsApiService.get().getAtsBranch()),
               OseeCmEditor.ArtifactEditor);
         } else {
            List<Artifact> artifacts = ArtifactQuery.getArtifactListFrom(ids, AtsApiService.get().getAtsBranch());
            ArtifactQuery.getArtifactListFrom(ids, AtsApiService.get().getAtsBranch());
            MassArtifactEditor.editArtifacts("Edit Work Packages", artifacts);
         }
      }
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      String result = "";
      if (element instanceof IAtsObject) {
         if (AtsApiService.get().getColumnService().getColumn(column.getId()) != null) {
            result =
               AtsApiService.get().getColumnService().getColumn(column.getId()).getColumnText((IAtsObject) element);
         }
      }
      return result;
   }

}
