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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
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

      Branch startBranch = renderer.getBranchOption("compareBranch");

      TransactionRecord startTransaction;
      boolean isDiffFromBaseline = false;

      if (isDiffFromBaseline) {
         startTransaction = endBranch.getBaseTransaction();
         startBranch = endBranch;
      } else {
         startTransaction = TransactionManager.getHeadTransaction(startBranch);
      }

      TransactionRecord endTransaction = TransactionManager.getHeadTransaction(endBranch);
      TransactionDelta txDelta = new TransactionDelta(startTransaction, endTransaction);

      for (Artifact artifact : endArtifacts) {
         try {
            diff(isDiffFromBaseline, txDelta, startBranch, artifact, diffPrefix);
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   }

   private void diff(boolean isDiffFromBaseline, TransactionDelta txDelta, Branch startBranch, Artifact endArtifact, String diffPrefix) throws OseeCoreException {
      List<Artifact> endArtifacts = Arrays.asList(endArtifact);
      List<Artifact> startArtifacts = getStartArtifacts(endArtifacts, startBranch);

      Collection<ArtifactDelta> artifactDeltas = new ArrayList<ArtifactDelta>();
      for (int index = 0; index < startArtifacts.size() && index < endArtifacts.size(); index++) {
         Artifact start = startArtifacts.get(index);
         Artifact end = endArtifacts.get(index);
         if (isDeleted(start)) {
            start = null;
         }
         if (isDeleted(end)) {
            end = null;
         }
         if (start != null || end != null) {
            artifactDeltas.add(new ArtifactDelta(txDelta, start, end));
         }
      }

      RendererManager.diff(artifactDeltas, diffPrefix, renderer.getValues());
   }

   private List<Artifact> getStartArtifacts(List<Artifact> artifacts, Branch startBranch) throws OseeCoreException {
      return ArtifactQuery.getArtifactListFromIds(Artifacts.toGuids(artifacts), startBranch,
         DeletionFlag.INCLUDE_DELETED);
   }

   private boolean isDeleted(Artifact artifact) {
      return artifact != null && artifact.isDeleted();
   }
}
