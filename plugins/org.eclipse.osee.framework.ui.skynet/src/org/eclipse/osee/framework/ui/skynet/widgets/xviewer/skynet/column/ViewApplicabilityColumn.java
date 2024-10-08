/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.nebula.widgets.xviewer.IAltLeftClickProvider;
import org.eclipse.nebula.widgets.xviewer.IMultiColumnEditProvider;
import org.eclipse.nebula.widgets.xviewer.IXViewerPreComputedColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.branch.ViewApplicabilityUtil;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.orcs.rest.model.ApplicabilityEndpoint;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class ViewApplicabilityColumn extends XViewerColumn implements IXViewerPreComputedColumn, IAltLeftClickProvider, IMultiColumnEditProvider {

   public static final String APPLICABILITY = "framework.view.applicability";
   public static final String columnlabel = "View Applicability";

   public ViewApplicabilityColumn(boolean show) {
      super(APPLICABILITY, columnlabel, 50, XViewerAlign.Left, show, SortDataType.String, true,
         "Retrieves the view applicability for a give Artifact.");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public ViewApplicabilityColumn copy() {
      ViewApplicabilityColumn newXCol = new ViewApplicabilityColumn(isShow());
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public Long getKey(Object obj) {
      Artifact artifact = getArtifact(obj);
      if (artifact != null) {
         return artifact.getId();
      }
      return null;
   }

   @Override
   public void populateCachedValues(Collection<?> objects, Map<Long, String> preComputedValueMap) {
      List<ArtifactToken> artifacts = new ArrayList<>(objects.size());
      for (Object obj : objects) {
         Artifact artifact = getArtifact(obj);
         if (!artifacts.contains(artifact)) {
            artifacts.add(artifact);
         }
      }

      if (!artifacts.isEmpty()) {
         BranchId branch = artifacts.iterator().next().getBranch();
         ApplicabilityEndpoint applEndpoint = ServiceUtil.getOseeClient().getApplicabilityEndpoint(branch);
         List<Pair<ArtifactId, ApplicabilityToken>> applicabilities = applEndpoint.getApplicabilityTokens(artifacts);
         for (Pair<ArtifactId, ApplicabilityToken> pair : applicabilities) {
            preComputedValueMap.put(pair.getFirst().getId(), pair.getSecond().getName());
         }
      }
   }

   @Override
   public String getText(Object obj, Long key, String cachedValue) {
      return cachedValue == null ? "" : cachedValue;
   }

   private Artifact getArtifact(Object obj) {
      try {
         if (obj instanceof Artifact) {
            return (Artifact) obj;
         } else if (obj instanceof Change) {
            return ((Change) obj).getChangeArtifact();
         }
      } catch (OseeCoreException ex) {
         // do nothing
      }
      return null;
   }

   @Override
   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      List<Artifact> artifacts = new LinkedList<>();
      for (TreeItem treeItem : treeItems) {
         if (treeItem.getData() instanceof Artifact) {
            artifacts.add((Artifact) treeItem.getData());
         }
      }
      if (artifacts.isEmpty()) {
         AWorkbench.popup("No Artifacts Selected");
         return;
      }
      if (!ViewApplicabilityUtil.isChangeApplicabilityValid(artifacts)) {
         AWorkbench.popup(ViewApplicabilityUtil.CHANGE_APPLICABILITY_INVAILD);
         return;
      }
      updateViewApplicability(artifacts);
   }

   private boolean updateViewApplicability(List<Artifact> artifacts) {
      Pair<Boolean, String> result = ViewApplicabilityUtil.changeApplicability(artifacts);
      boolean success = result.getFirst();
      String value = result.getSecond();
      if (success) {
         for (Artifact art : artifacts) {
            getPreComputedValueMap().put(art.getId(), value);
         }
      }
      return success;
   }

   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      Object obj = treeItem.getData();
      if (obj instanceof Artifact) {
         Artifact artifact = (Artifact) obj;
         if (!ViewApplicabilityUtil.isChangeApplicabilityValid(Collections.singleton(artifact))) {
            AWorkbench.popup(ViewApplicabilityUtil.CHANGE_APPLICABILITY_INVAILD);
            return false;
         }
         return updateViewApplicability(Collections.singletonList(artifact));
      } else {
         AWorkbench.popup("No Artifact Selected");
         return false;
      }
   }

}
