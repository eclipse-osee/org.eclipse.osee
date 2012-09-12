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
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.change.RelationChangeItem;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.ChangeDataLoader;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.render.DefaultArtifactRenderer;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;

/**
 * @author Jeff C. Phillips
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
      renderer.setOption("Publish As Diff", false);
      renderer.setOption("RecurseChildren", recurseChildren);

      Branch endBranch = renderer.getBranchOption("Branch");
      renderer.setOption("Diff Branch", endBranch);

      Branch compareBranch = renderer.getBranchOption("compareBranch");

      TransactionRecord startTransaction;

      if (compareBranch == null) {
         startTransaction = endBranch.getBaseTransaction();
         compareBranch = endBranch;
      } else {
         startTransaction = TransactionManager.getHeadTransaction(compareBranch);
      }

      TransactionRecord endTransaction = TransactionManager.getHeadTransaction(endBranch);
      TransactionDelta txDelta;
      if (startTransaction.getId() < endTransaction.getId()) {
         txDelta = new TransactionDelta(startTransaction, endTransaction);
      } else {
         txDelta = new TransactionDelta(endTransaction, startTransaction);
      }

      Collection<Artifact> toProcess = recurseChildren ? getAllArtifacts(endArtifacts) : endArtifacts;
      List<Change> changes = new LinkedList<Change>();
      ChangeDataLoader changeLoader = new ChangeDataLoader(changes, txDelta);

      Operations.executeWorkAndCheckStatus(changeLoader);

      try {
         diff(changes, toProcess, diffPrefix);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private Collection<Artifact> getAllArtifacts(List<Artifact> endArtifacts) throws OseeCoreException {
      Set<Artifact> toReturn = new LinkedHashSet<Artifact>();
      for (Artifact art : endArtifacts) {
         toReturn.addAll(art.getDescendants());
         toReturn.add(art);
      }
      return toReturn;
   }

   private void diff(List<Change> changes, Collection<Artifact> endArtifacts, String diffPrefix) {

      Collection<ArtifactDelta> artifactDeltas = new ArrayList<ArtifactDelta>();
      Collection<Integer> endIds = Artifacts.toIds(endArtifacts);
      Set<Integer> addedIds = new HashSet<Integer>();

      for (Change change : changes) {
         Integer artId = change.getArtId();
         if (!(change.getChangeItem() instanceof RelationChangeItem) && !addedIds.contains(artId)) {
            if (endIds.contains(artId)) {
               artifactDeltas.add(change.getDelta());
               addedIds.add(artId);
            }
         }
      }

      if (!artifactDeltas.isEmpty()) {
         RendererManager.diff(artifactDeltas, diffPrefix, renderer.getValues());
      }
   }
}
