/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.util.widgets;

import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.version.Version;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.AttributeWidget;
import org.eclipse.osee.framework.ui.swt.Widgets;

/**
 * Select configuration from PL Config. Uses TeamWorkflowToFoundInVersion_Version to get build to retrieve config, so
 * that must be related to workflow first.
 *
 * @author Donald G. Dunne
 */
public class XHyperlinkWfdForConfigurationDam extends XHyperlinkWfdForConfiguration implements AttributeWidget {

   private Artifact artifact;
   private AttributeTypeToken attributeTypeToken;

   @Override
   public void handleSelectionPersist(ArtifactToken selected) {
      SkynetTransaction tx = TransactionManager.createTransaction(CoreBranches.COMMON, "Set Config");
      artifact.setSoleAttributeValue(attributeTypeToken, selected);
      tx.execute();
   }

   @Override
   public Artifact getArtifact() {
      return artifact;
   }

   @Override
   public void saveToArtifact() {
      // do nothing
   }

   @Override
   public void revert() {
      // do nothing
   }

   @Override
   public Result isDirty() {
      return Result.FalseResult;
   }

   @Override
   public void setAttributeType(Artifact artifact, AttributeTypeToken attributeTypeToken) {
      this.artifact = artifact;
      this.attributeTypeToken = attributeTypeToken;
      if (artifact instanceof IAtsTeamWorkflow) {
         IAtsTeamWorkflow teamWf = (IAtsTeamWorkflow) artifact;
         ArtifactToken versionId = AtsApiService.get().getRelationResolver().getRelatedOrSentinel(teamWf,
            AtsRelationTypes.TeamWorkflowToFoundInVersion_Version);
         if (versionId.isValid()) {
            Version ver = AtsApiService.get().getVersionService().getVersionById(versionId);
            if (ver != null) {
               branch = ver.getBaselineBranch();
            }
         }
      }
   }

   @Override
   public AttributeTypeToken getAttributeType() {
      return attributeTypeToken;
   }

   @Override
   public String getCurrentValue() {
      if (artifact == null) {
         return Widgets.NOT_SET;
      }
      ArtifactId build = artifact.getRelatedArtifactOrNull(AtsRelationTypes.TeamWorkflowToFoundInVersion_Version);
      if (build == null || build.isInvalid()) {
         return "Select Build";
      }
      ArtifactId config = artifact.getSoleAttributeValue(attributeTypeToken, ArtifactId.SENTINEL);
      if (config.isInvalid()) {
         return Widgets.NOT_SET;
      }
      AtsApi atsApi = AtsApiService.get();
      IAtsVersion version = atsApi.getVersionService().getVersionById(build);
      BranchId branch = atsApi.getVersionService().getBaselineBranchIdInherited(version);
      if (branch == null || branch.isInvalid()) {
         return "Invalid Build Id: " + build.getIdString();
      }
      List<ArtifactToken> views = ServiceUtil.getOseeClient().getApplicabilityEndpoint(branch).getViews();
      for (ArtifactToken view : views) {
         if (view.equals(config)) {
            selected = view;
            return view.getName();
         }
      }
      return "Invalid Config Id: " + config.getIdString();
   }

}
