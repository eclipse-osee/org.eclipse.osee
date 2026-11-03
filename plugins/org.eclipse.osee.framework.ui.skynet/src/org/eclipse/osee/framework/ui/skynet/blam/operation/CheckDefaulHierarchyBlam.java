/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.blam.operation;

import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.core.widget.XWidgetLabels;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.osgi.service.component.annotations.Component;

/**
 * @author Ryan D. Brooks
 */
@Component(service = AbstractBlam.class, immediate = true)
public class CheckDefaulHierarchyBlam extends AbstractBlam {

   private static final String BLAM_NAME = "Check Default Hierarchy";

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public CheckDefaulHierarchyBlam() {
      super(BLAM_NAME, CoreImage.GEAR);
   }

   //////////////////////////////////////////
   // Widgets
   //////////////////////////////////////////

   @Override
   public List<XWidgetData> getXWidgetItems() {
      createWidgetBuilder();
      wb.andBranchSelWidget();
      wb.andArtifactTypeWidget();
      return wb.getXWidgetDatas();
   }

   //////////////////////////////////////////
   // Operation
   //////////////////////////////////////////

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      BranchId branch = variableMap.getBranch(XWidgetLabels.BRANCH);
      ArtifactTypeToken artifactType = variableMap.getArtifactType(XWidgetLabels.ARTIFACT_TYPE);
      List<Artifact> artifacts = ArtifactQuery.getArtifactListFromType(artifactType, branch, EXCLUDE_DELETED);
      for (Artifact artifact : artifacts) {
         try {
            if (!artifact.hasParent()) {
               logf("\n" + artifact.getGuid() + " has no parent");
            }
         } catch (MultipleArtifactsExist ex) {
            logf("\n" + ex.getLocalizedMessage());
         }
      }
   }

   //////////////////////////////////////////
   // Categories & Description
   //////////////////////////////////////////

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(XNavigateItem.DEFINE_HEALTH);
   }

   @Override
   public String getDescriptionUsage() {
      return "Checks all artifacts of the selected type on the given branch for missing or invalid default hierarchy parents. Reports artifacts with no parent or multiple parents.";
   }

}
