/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

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
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
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

   public void generateFileDifferences(List<Artifact> endArtifacts, String diffPrefix, String nextParagraphNumber, String outlineType, boolean recurseChildren) throws OseeArgumentException, OseeCoreException {
      renderer.setOption("artifacts", endArtifacts);
      renderer.setOption("paragraphNumber", nextParagraphNumber);
      renderer.setOption("outlineType", outlineType);
      renderer.setOption("Publish With Attributes", true);
      renderer.setOption("Use Artifact Names", true);
      renderer.setOption("inPublishMode", true);
      // need to keep original value as well as reseting to false
      renderer.setOption("Orig Publish As Diff", renderer.getBooleanOption(WordTemplateProcessor.PUBLISH_AS_DIFF));
      renderer.setOption(WordTemplateProcessor.PUBLISH_AS_DIFF, false);
      renderer.setOption("RecurseChildren", recurseChildren);

      BranchId endBranch = renderer.getBranchOption("Branch");
      renderer.setOption("Diff Branch", endBranch);

      BranchId compareBranch = renderer.getBranchOption("compareBranch");

      TransactionToken startTransaction;

      if (compareBranch == null) {
         startTransaction = BranchManager.getBaseTransaction(endBranch);
         compareBranch = endBranch;
      } else {
         startTransaction = TransactionManager.getHeadTransaction(compareBranch);
      }

      TransactionToken endTransaction = TransactionManager.getHeadTransaction(endBranch);
      TransactionDelta txDelta;

      boolean maintainOrder = renderer.getBooleanOption("Maintain Order");
      if (startTransaction.getId() < endTransaction.getId() || maintainOrder) {
         if (compareBranch.equals(endBranch)) {
            txDelta = new TransactionDelta(startTransaction, endTransaction);
         } else {
            txDelta = new TransactionDelta(endTransaction, startTransaction);
         }
      } else {
         txDelta = new TransactionDelta(startTransaction, endTransaction);
      }

      boolean recurseOnLoad = renderer.getBooleanOption(WordTemplateProcessor.RECURSE_ON_LOAD);
      Collection<Artifact> toProcess = recurseChildren || recurseOnLoad ? getAllArtifacts(endArtifacts) : endArtifacts;
      List<Change> changes = new LinkedList<>();
      ChangeDataLoader changeLoader = new ChangeDataLoader(changes, txDelta);
      IProgressMonitor monitor = (IProgressMonitor) renderer.getOption("Progress Monitor");
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

   private Collection<Artifact> getAllArtifacts(List<Artifact> endArtifacts) throws OseeCoreException {
      Set<Artifact> toReturn = new LinkedHashSet<>();
      for (Artifact art : endArtifacts) {
         toReturn.add(art);
         toReturn.addAll(art.getDescendants());
      }
      return toReturn;
   }

   private void diff(List<Change> changes, Collection<Artifact> endArtifacts, String diffPrefix, TransactionDelta txDelta) throws OseeCoreException {

      Collection<ArtifactDelta> artifactDeltas = new ArrayList<>();
      Set<Integer> addedIds = new HashSet<>();
      Set<Integer> changeIds = new HashSet<>(changes.size());
      for (Change change : changes) {
         changeIds.add(change.getArtId().getId().intValue());
      }
      BranchId endBranch = txDelta.getEndTx().getBranch();
      // loop through all artifacts that are on the IS branch
      for (Artifact art : endArtifacts) {
         Integer artId = art.getArtId();
         if (changeIds.contains(artId)) {
            // If there is a change on the IS branch
            Change newChange = findChange(artId, changes);
            if (newChange != null && !newChange.getChangeItem().getChangeType().isRelationChange() && !addedIds.contains(
               artId)) {
               artifactDeltas.add(newChange.getDelta());
               addedIds.add(artId);
            }
            // If artifact on the old branch didn't exist then return the entire artifact as a diff
         } else if (ArtifactQuery.checkArtifactFromId(art, endBranch) == null) {
            // Return the current artifact as being new
            artifactDeltas.add(new ArtifactDelta(txDelta, null, art));
            addedIds.add(artId);
         } else {
            // No change to this artifact, so show the WAS version as is.
            Artifact wasArt = ArtifactQuery.getArtifactFromId(art, endBranch);
            artifactDeltas.add(new ArtifactDelta(txDelta, wasArt, wasArt, wasArt));
            addedIds.add(artId);

         }
      }

      if (!artifactDeltas.isEmpty()) {
         RendererManager.diffWithRenderer(artifactDeltas, diffPrefix, renderer, renderer.getValues());
      }
   }

   private Change findChange(Integer artId, List<Change> changes) {
      Change toReturn = null;
      for (Change change : changes) {
         if (change.getArtId().getId().intValue() == artId.intValue()) {
            toReturn = change;
            break;
         }
      }
      return toReturn;
   }
}
