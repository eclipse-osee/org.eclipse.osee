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

package org.eclipse.osee.ats.ide.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionUtility;

/**
 * @author Donald G. Dunne
 */
public class AtsBulkLoad {

   private static AtomicBoolean atsTypeDataLoadedStarted = new AtomicBoolean(false);

   public static List<IOperation> getConfigLoadingOperations() {
      List<IOperation> ops = new ArrayList<>();
      if (atsTypeDataLoadedStarted.compareAndSet(false, true) && DbConnectionUtility.isVersionSupported()) {
         IOperation op = new AbstractOperation("Re-load ATS Config", Activator.PLUGIN_ID) {
            @Override
            protected void doWork(IProgressMonitor monitor) throws Exception {
               AtsApiService.get().clearCaches();
               AtsApiService.get().getUserService().getCurrentUser();
               AtsApiService.get().getConfigService().getConfigurations();
            }
         };
         ops.add(op);
      } else {
         ops.add(Operations.createNoOpOperation("ATS Bulk Loading"));
      }
      return ops;
   }

   public static Set<Artifact> bulkLoadArtifacts(Collection<? extends Artifact> artifacts) {
      List<Artifact> actions = new ArrayList<>();
      List<Artifact> teams = new ArrayList<>();
      List<Artifact> tasks = new ArrayList<>();
      for (Artifact art : artifacts) {
         if (art.isOfType(AtsArtifactTypes.Action)) {
            actions.add(art);
         } else if (art.isOfType(AtsArtifactTypes.TeamWorkflow)) {
            teams.add(art);
         } else if (art.isOfType(AtsArtifactTypes.Task)) {
            tasks.add(art);
         }
      }
      Set<Artifact> arts = new HashSet<>();
      if (!actions.isEmpty()) {
         arts.addAll(RelationManager.getRelatedArtifacts(actions, 3, AtsRelationTypes.TeamWfToTask_Task,
            AtsRelationTypes.TeamWorkflowToReview_Review, AtsRelationTypes.ActionToWorkflow_Action));
      }
      if (!teams.isEmpty()) {
         arts.addAll(RelationManager.getRelatedArtifacts(teams, 4, AtsRelationTypes.TeamWfToTask_Task,
            AtsRelationTypes.ActionToWorkflow_TeamWorkflow, AtsRelationTypes.TeamWorkflowToReview_Review,
            AtsRelationTypes.AgileFeatureToItem_AgileFeatureGroup,
            AtsRelationTypes.TeamWorkflowTargetedForVersion_Version));
      }
      if (!tasks.isEmpty()) {
         arts.addAll(RelationManager.getRelatedArtifacts(tasks, 2, AtsRelationTypes.TeamWfToTask_TeamWorkflow,
            AtsRelationTypes.ActionToWorkflow_TeamWorkflow));
      }
      arts.addAll(artifacts);
      return arts;
   }

   public static void bulkLoadSiblings(Collection<? extends Artifact> artifacts) {
      List<Artifact> teams = new ArrayList<>();
      for (Artifact art : artifacts) {
         if (art.isOfType(AtsArtifactTypes.TeamWorkflow)) {
            teams.add(art);
         }
      }

      int siblingQueryCount = 400;
      String siblingQueryCountStr = AtsApiService.get().getConfigValue("siblingQueryCount");
      if (Strings.isNumeric(siblingQueryCountStr)) {
         siblingQueryCount = Integer.valueOf(siblingQueryCountStr);
      }

      List<Collection<Artifact>> artSets = Collections.subDivide(teams, siblingQueryCount);

      for (Collection<Artifact> arts : artSets) {
         // Bulk load related artifacts
         HashCollection<ArtifactId, ArtifactToken> artifactTokenListFromRelated =
            ArtifactQuery.getArtifactTokenListFromRelated(CoreBranches.COMMON, Collections.castAll(arts),
               AtsArtifactTypes.Action, AtsRelationTypes.ActionToWorkflow_Action);
         List<Artifact> actionArts =
            ArtifactQuery.getArtifactListFrom(artifactTokenListFromRelated.getValues(), CoreBranches.COMMON);

         // Buld load team wfs off loaded actions
         ArtifactQuery.getArtifactTokenListFromRelated(CoreBranches.COMMON, Collections.castAll(actionArts),
            AtsArtifactTypes.TeamWorkflow, AtsRelationTypes.ActionToWorkflow_TeamWorkflow);
      }

   }

}
