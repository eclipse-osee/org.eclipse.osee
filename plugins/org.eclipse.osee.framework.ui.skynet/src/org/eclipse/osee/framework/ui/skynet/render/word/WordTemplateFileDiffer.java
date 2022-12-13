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

package org.eclipse.osee.framework.ui.skynet.render.word;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.core.util.RendererOption;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.ChangeDataLoader;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.render.DefaultArtifactRenderer;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;

/**
 * @author Jeff C. Phillips
 * @author Mark Joy
 */
public final class WordTemplateFileDiffer {
   private final DefaultArtifactRenderer renderer;

   public WordTemplateFileDiffer(DefaultArtifactRenderer renderer) {
      this.renderer = renderer;
   }

   public void generateFileDifferences(List<Artifact> endArtifacts, String diffPrefix, String nextParagraphNumber, String outlineType, boolean recurseChildren) {

      this.renderer.updateOption(RendererOption.PARAGRAPH_NUMBER, nextParagraphNumber);
      this.renderer.updateOption(RendererOption.OUTLINE_TYPE, outlineType);
      this.renderer.updateOption(RendererOption.ALL_ATTRIBUTES, true);
      this.renderer.updateOption(RendererOption.USE_ARTIFACT_NAMES, true);
      this.renderer.updateOption(RendererOption.IN_PUBLISH_MODE, true);
      // need to keep original value as well as reseting to false
      this.renderer.updateOption(RendererOption.ORIG_PUBLISH_AS_DIFF,
         renderer.getRendererOptionValue(RendererOption.PUBLISH_DIFF));
      this.renderer.updateOption(RendererOption.PUBLISH_DIFF, false);

      this.renderer.updateOption(RendererOption.RECURSE, recurseChildren);
      // can use this as "diff branch?"
      BranchId endBranch = (BranchId) renderer.getRendererOptionValue(RendererOption.BRANCH);
      this.renderer.updateOption(RendererOption.WAS_BRANCH, endBranch);

      BranchId compareBranch = (BranchId) renderer.getRendererOptionValue(RendererOption.COMPARE_BRANCH);

      TransactionToken startTransaction;

      if (BranchId.SENTINEL.equals(compareBranch)) {
         startTransaction = BranchManager.getBaseTransaction(endBranch);
         compareBranch = endBranch;
      } else {
         startTransaction = TransactionManager.getHeadTransaction(compareBranch);
      }

      TransactionToken endTransaction = TransactionManager.getHeadTransaction(endBranch);
      TransactionDelta txDelta;

      boolean maintainOrder = (boolean) renderer.getRendererOptionValue(RendererOption.MAINTAIN_ORDER);
      if (startTransaction.getId() < endTransaction.getId() || maintainOrder) {
         if (compareBranch.equals(endBranch)) {
            txDelta = new TransactionDelta(startTransaction, endTransaction);
         } else {
            txDelta = new TransactionDelta(endTransaction, startTransaction);
         }
      } else {
         txDelta = new TransactionDelta(startTransaction, endTransaction);
      }

      boolean recurseOnLoad = (boolean) renderer.getRendererOptionValue(RendererOption.RECURSE_ON_LOAD);
      Collection<Artifact> toProcess = recurseChildren || recurseOnLoad ? getAllArtifacts(endArtifacts) : endArtifacts;
      List<Change> changes = new LinkedList<>();
      ChangeDataLoader changeLoader = new ChangeDataLoader(changes, txDelta);
      IProgressMonitor monitor = (IProgressMonitor) renderer.getRendererOptionValue(RendererOption.PROGRESS_MONITOR);
      if (monitor == null) {
         monitor = new NullProgressMonitor();
      }
      changeLoader.determineChanges(monitor);

      try {
         monitor.setTaskName("Compare differences");
         diff(changes, toProcess, diffPrefix, txDelta);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private Collection<Artifact> getAllArtifacts(List<Artifact> endArtifacts) {
      Set<Artifact> toReturn = new LinkedHashSet<>();
      for (Artifact art : endArtifacts) {
         toReturn.add(art);
         toReturn.addAll(art.getDescendants());
      }
      return toReturn;
   }

   private void diff(List<Change> changes, Collection<Artifact> endArtifacts, String diffPrefix, TransactionDelta txDelta) {

      Collection<ArtifactDelta> artifactDeltas = new ArrayList<>();
      Set<ArtifactId> addedIds = new HashSet<>();
      Set<ArtifactId> changeIds = new HashSet<>(changes.size());
      for (Change change : changes) {
         changeIds.add(change.getArtId());
      }
      BranchId endBranch = txDelta.getEndTx().getBranch();
      // loop through all artifacts that are on the IS branch
      for (Artifact art : endArtifacts) {
         if (changeIds.contains(art)) {
            // If there is a change on the IS branch
            Change newChange = findChange(art, changes);
            if (newChange != null && !newChange.getChangeItem().getChangeType().isRelationChange() && !addedIds.contains(
               art)) {
               artifactDeltas.add(newChange.getDelta());
               addedIds.add(art);
            }
            // If artifact on the old branch didn't exist then return the entire artifact as a diff
         } else if (ArtifactQuery.checkArtifactFromId(art, endBranch) == null) {
            // Return the current artifact as being new
            artifactDeltas.add(new ArtifactDelta(txDelta, null, art));
            addedIds.add(art);
         } else {
            // No change to this artifact, so show the WAS version as is.
            Artifact wasArt = ArtifactQuery.getArtifactFromId(art, endBranch);
            artifactDeltas.add(new ArtifactDelta(txDelta, wasArt, wasArt, wasArt));
            addedIds.add(art);
         }
      }

      if (!artifactDeltas.isEmpty()) {
         RendererManager.diffWithRenderer(artifactDeltas, diffPrefix, renderer, renderer.getRendererOptions());
      }
   }

   private Change findChange(ArtifactId artId, List<Change> changes) {
      Change toReturn = null;
      for (Change change : changes) {
         if (change.getArtId().equals(artId)) {
            toReturn = change;
            break;
         }
      }
      return toReturn;
   }
}