/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.window.Window;
import org.eclipse.nebula.widgets.xviewer.IAltLeftClickProvider;
import org.eclipse.nebula.widgets.xviewer.IMultiColumnEditProvider;
import org.eclipse.nebula.widgets.xviewer.IXViewerPreComputedColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.ViewApplicabilityFilterTreeDialog;
import org.eclipse.osee.orcs.rest.model.Applicabilities;
import org.eclipse.osee.orcs.rest.model.Applicability;
import org.eclipse.osee.orcs.rest.model.ApplicabilityEndpoint;
import org.eclipse.osee.orcs.rest.model.ApplicabilityId;
import org.eclipse.osee.orcs.rest.model.ArtifactIds;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class ViewApplicabilityColumn extends XViewerColumn implements IXViewerPreComputedColumn, IAltLeftClickProvider, IMultiColumnEditProvider {

   public static final String APPLICABILITY = "framework.view.applicability";

   public ViewApplicabilityColumn(boolean show) {
      super(APPLICABILITY, "View Applicability", 50, XViewerAlign.Left, show, SortDataType.String, true,
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
         return artifact.getUuid();
      }
      return null;
   }

   @Override
   public void populateCachedValues(Collection<?> objects, Map<Long, String> preComputedValueMap) {
      ArtifactIds artIds = new ArtifactIds();
      for (Object obj : objects) {
         Artifact artifact = getArtifact(obj);
         artIds.getArtifactIds().add(artifact.getUuid());
      }
      ApplicabilityEndpoint applEndpoint = ServiceUtil.getOseeClient().getApplicabilityEndpoint();
      Applicabilities applicabilities = applEndpoint.getApplicabilities(artIds);
      for (Applicability appl : applicabilities.getApplicabilities()) {
         preComputedValueMap.put(appl.getArtId(), appl.getApplicability().getName());
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
      ApplicabilityEndpoint applEndpoint = ServiceUtil.getOseeClient().getApplicabilityEndpoint();
      List<ApplicabilityId> applicabilityIds = applEndpoint.getApplicabilityIds().getApplicabilityIds();
      ViewApplicabilityFilterTreeDialog dialog = new ViewApplicabilityFilterTreeDialog("Select View Applicability",
         "Select View Applicability", applicabilityIds);
      dialog.setInput();
      dialog.setMultiSelect(false);
      int result = dialog.open();
      if (result == Window.OK) {
         Applicabilities applicabilities = new Applicabilities();
         List<Artifact> artifacts = new LinkedList<>();
         for (TreeItem treeItem : treeItems) {
            if (treeItem.getData() instanceof Artifact) {
               Artifact artifact = (Artifact) treeItem.getData();
               artifacts.add(artifact);
               Applicability appl = new Applicability();
               appl.setArtId(artifact.getUuid());
               if (dialog.isRemoveViewApplicability()) {
                  appl.setApplicability(null);
               } else {
                  appl.setApplicability(dialog.getSelection());
               }
            }
            if (applicabilities.getApplicabilities().isEmpty()) {
               AWorkbench.popup("No Artifacts Selected");
               return;
            }
            applEndpoint.setApplicabilities(applicabilities);
         }
         ArtifactQuery.reloadArtifacts(artifacts);
      }
   }

   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      Object obj = treeItem.getData();
      if (obj instanceof Artifact) {
         Artifact artifact = (Artifact) obj;
         ApplicabilityEndpoint applEndpoint = ServiceUtil.getOseeClient().getApplicabilityEndpoint();
         ViewApplicabilityFilterTreeDialog dialog = new ViewApplicabilityFilterTreeDialog("Select View Applicability",
            "Select View Applicability", applEndpoint.getApplicabilityIds().getApplicabilityIds());
         dialog.setMultiSelect(false);
         dialog.setInput();
         ArtifactIds artifactIds = new ArtifactIds();
         artifactIds.getArtifactIds().add(artifact.getUuid());
         Applicabilities applicabilities = applEndpoint.getApplicabilities(artifactIds);
         if (!applicabilities.getApplicabilities().isEmpty() && applicabilities.getApplicabilities().iterator().next().getApplicability() != null) {
            dialog.setInitialSelections(
               Arrays.asList(applicabilities.getApplicabilities().iterator().next().getApplicability()));
         }
         int result = dialog.open();
         if (result == Window.OK) {
            Applicability appl = new Applicability();
            appl.setArtId(artifact.getUuid());
            if (dialog.isRemoveViewApplicability()) {
               appl.setApplicability(null);
            } else {
               appl.setApplicability(dialog.getSelection());
            }
            applEndpoint.setApplicability(appl);
            ArtifactQuery.reloadArtifacts(Collections.singleton(artifact));
            return true;
         }
      }
      return false;
   }
}
