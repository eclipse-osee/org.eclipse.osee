/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.transaction;

import static java.util.Collections.singleton;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.ResultSet;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.ArtifactTransactionData;
import org.eclipse.osee.orcs.core.ds.OrcsVisitor;
import org.eclipse.osee.orcs.core.ds.TransactionData;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactFactory;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactVisitable;
import org.eclipse.osee.orcs.core.internal.proxy.ExternalArtifactManager;
import org.eclipse.osee.orcs.core.internal.transaction.TxData.TxState;
import org.eclipse.osee.orcs.data.ArtifactId;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Roberto E. Escobar
 * @author Megumi Telles
 */
public class TxDataManager {

   public interface TxDataLoader {

      ResultSet<Artifact> loadArtifacts(OrcsSession session, IOseeBranch branch, Collection<ArtifactId> artifactIds) throws OseeCoreException;

   }

   private final ExternalArtifactManager proxyManager;
   private final ArtifactFactory artifactFactory;
   private final TxDataLoader loader;

   public TxDataManager(ExternalArtifactManager proxyManager, ArtifactFactory artifactFactory, TxDataLoader loader) {
      this.proxyManager = proxyManager;
      this.artifactFactory = artifactFactory;
      this.loader = loader;
   }

   public TxData createTxData(OrcsSession session, IOseeBranch branch) {
      return new TxData(session, branch);
   }

   public void txCommitSuccess(TxData txData) {
      for (Artifact writeable : txData.getAllWriteables()) {
         writeable.setNotDirty();
      }
      txData.setTxState(TxState.COMMITTED);
   }

   public void rollbackTx(TxData txData) {
      txData.setTxState(TxState.COMMIT_FAILED);
   }

   public void startTx(TxData txData) throws OseeCoreException {
      Conditions.checkExpressionFailOnTrue(txData.isCommitInProgress(), "Commit is already in progress");
      txData.setCommitInProgress(true);
      txData.setTxState(TxState.COMMIT_STARTED);
   }

   public void endTx(TxData txData) {
      txData.setCommitInProgress(false);
   }

   public Iterable<Artifact> getForWrite(TxData txData, Iterable<? extends ArtifactId> ids) throws OseeCoreException {
      checkChangesAllowed(txData);
      Set<ArtifactId> toLoad = new LinkedHashSet<ArtifactId>();

      LinkedHashMap<String, Artifact> items = new LinkedHashMap<String, Artifact>();
      for (ArtifactId artifactId : ids) {
         Artifact node = findArtifactLocallyForWrite(txData, artifactId);
         if (node == null) {
            toLoad.add(artifactId);
         } else {
            checkAndAdd(txData, node);
         }
         items.put(artifactId.getGuid(), node);
      }
      if (!toLoad.isEmpty()) {
         Iterable<Artifact> result = loader.loadArtifacts(txData.getSession(), txData.getBranch(), toLoad);
         for (Artifact node : result) {
            items.put(node.getGuid(), node);
            checkAndAdd(txData, node);
         }
      }
      return items.values();
   }

   public Artifact getForWrite(TxData txData, ArtifactId artifactId) throws OseeCoreException {
      checkChangesAllowed(txData);
      Artifact node = findArtifactLocallyForWrite(txData, artifactId);
      if (node == null) {
         ResultSet<Artifact> result =
            loader.loadArtifacts(txData.getSession(), txData.getBranch(), singleton(artifactId));
         node = result.getExactlyOne();
      }
      checkAndAdd(txData, node);
      return node;
   }

   private Artifact findArtifactLocallyForWrite(TxData txData, ArtifactId artifactId) throws OseeCoreException {
      Artifact node = txData.getWriteable(artifactId);
      if (node == null) {
         if (artifactId instanceof Artifact) {
            Artifact source = (Artifact) artifactId;
            node = copyArtifactForWrite(txData, source);
         } else if (artifactId instanceof ArtifactReadable) {
            ArtifactReadable external = (ArtifactReadable) artifactId;
            Artifact source = proxyManager.asInternalArtifact(external);
            node = copyArtifactForWrite(txData, source);
         }
      }
      return node;
   }

   private Artifact copyArtifactForWrite(TxData txData, Artifact source) throws OseeCoreException {
      return artifactFactory.clone(source);
   }

   private Artifact getSourceArtifact(TxData txData, IOseeBranch fromBranch, ArtifactId artifactId) throws OseeCoreException {
      Artifact source = null;
      if (txData.getBranch().equals(fromBranch)) {
         source = txData.getWriteable(artifactId);
      }

      if (source == null) {
         if (artifactId instanceof Artifact) {
            Artifact artifact = (Artifact) artifactId;
            if (fromBranch.equals(artifact.getBranch())) {
               source = artifact;
            }
         } else if (artifactId instanceof ArtifactReadable) {
            ArtifactReadable external = (ArtifactReadable) artifactId;
            if (fromBranch.equals(external.getBranch())) {
               source = proxyManager.asInternalArtifact(external);
            }
         }
      }
      if (source == null) {
         ResultSet<Artifact> loadArtifacts =
            loader.loadArtifacts(txData.getSession(), fromBranch, singleton(artifactId));
         source = loadArtifacts.getExactlyOne();
      }
      return source;
   }

   private void checkChangesAllowed(TxData txData) throws OseeCoreException {
      String errorMessage = "";
      if (txData.isCommitInProgress() || TxState.COMMIT_STARTED == txData.getTxState()) {
         errorMessage = "Changes are not allowed - [COMMIT_IN_PROGRESS]";
      }
      if (Strings.isValid(errorMessage)) {
         throw new OseeStateException(errorMessage);
      }
   }

   public void setComment(TxData txData, String comment) throws OseeCoreException {
      checkChangesAllowed(txData);
      txData.setComment(comment);
   }

   public void setAuthor(TxData txData, ArtifactReadable author) throws OseeCoreException {
      checkChangesAllowed(txData);
      txData.setAuthor(author);
   }

   public ArtifactReadable createArtifact(TxData txData, IArtifactType artifactType, String name, String guid) throws OseeCoreException {
      checkChangesAllowed(txData);
      Artifact artifact = artifactFactory.createArtifact(txData.getBranch(), artifactType, guid);
      artifact.setName(name);
      return asExternalArtifact(txData, artifact);
   }

   public ArtifactReadable copyArtifact(TxData txData, IOseeBranch fromBranch, ArtifactId artifactId) throws OseeCoreException {
      checkChangesAllowed(txData);
      Artifact source = getSourceArtifact(txData, fromBranch, artifactId);
      return copyArtifactHelper(txData, source, source.getExistingAttributeTypes());
   }

   public ArtifactReadable copyArtifact(TxData txData, IOseeBranch fromBranch, ArtifactId artifactId, Collection<? extends IAttributeType> attributesToDuplicate) throws OseeCoreException {
      checkChangesAllowed(txData);
      Artifact source = getSourceArtifact(txData, fromBranch, artifactId);
      return copyArtifactHelper(txData, source, attributesToDuplicate);
   }

   private ArtifactReadable copyArtifactHelper(TxData txData, Artifact source, Collection<? extends IAttributeType> attributesToDuplicate) throws OseeCoreException {
      Artifact copy = artifactFactory.copyArtifact(source, attributesToDuplicate, txData.getBranch());
      return asExternalArtifact(txData, copy);
   }

   public ArtifactReadable introduceArtifact(TxData txData, IOseeBranch fromBranch, ArtifactId artifactId) throws OseeCoreException {
      checkChangesAllowed(txData);
      checkAreOnDifferentBranches(txData, fromBranch);
      Artifact source = getSourceArtifact(txData, fromBranch, artifactId);
      Artifact artifact = artifactFactory.introduceArtifact(source, txData.getBranch());
      return asExternalArtifact(txData, artifact);
   }

   private ArtifactReadable asExternalArtifact(TxData txData, Artifact artifact) throws OseeCoreException {
      checkAndAdd(txData, artifact);
      ArtifactReadable readable = txData.getReadable(artifact);
      if (readable == null) {
         readable = proxyManager.asExternalArtifact(txData.getSession(), artifact);
         txData.add(readable);
      }
      return readable;
   }

   private void checkAndAdd(TxData txData, Artifact artifact) throws OseeCoreException {
      checkChangesAllowed(txData);
      Artifact oldArtifact = txData.add(artifact);
      boolean isDifferent = oldArtifact != null && oldArtifact != artifact;
      Conditions.checkExpressionFailOnTrue(isDifferent,
         "Another instance of writeable detected - writeable tracking would be inconsistent");
   }

   private void checkAreOnDifferentBranches(TxData txData, IOseeBranch sourceBranch) throws OseeCoreException {
      boolean isOnSameBranch = txData.getBranch().equals(sourceBranch);
      Conditions.checkExpressionFailOnTrue(isOnSameBranch, "Source branch is same branch as transaction branch[%s]",
         txData.getBranch());
   }

   public void deleteArtifact(TxData txData, ArtifactId sourceArtifact) throws OseeCoreException {
      Artifact asArtifact = getForWrite(txData, sourceArtifact);
      asArtifact.delete();
   }

   public TransactionData createChangeData(TxData txData) throws OseeCoreException {
      List<ArtifactTransactionData> changes = new ArrayList<ArtifactTransactionData>();
      CollectDirtyData visitor = new CollectDirtyData(changes);
      for (Artifact artifact : txData.getAllWriteables()) {
         if (artifact.isDirty()) {
            ArtifactVisitable visitable = artifact;
            visitable.accept(visitor);
         }
      }
      return new TransactionDataImpl(txData.getBranch(), txData.getAuthor(), txData.getComment(), changes);
   }

   private static final class TransactionDataImpl implements TransactionData {

      private final IOseeBranch branch;
      private final ArtifactReadable author;
      private final String comment;
      private final List<ArtifactTransactionData> data;

      public TransactionDataImpl(IOseeBranch branch, ArtifactReadable author, String comment, List<ArtifactTransactionData> data) {
         super();
         this.branch = branch;
         this.author = author;
         this.comment = comment;
         this.data = data;
      }

      @Override
      public IOseeBranch getBranch() {
         return branch;
      }

      @Override
      public ArtifactReadable getAuthor() {
         return author;
      }

      @Override
      public String getComment() {
         return comment;
      }

      @Override
      public List<ArtifactTransactionData> getTxData() {
         return data;
      }

      @Override
      public void accept(OrcsVisitor visitor) throws OseeCoreException {
         for (ArtifactTransactionData data : getTxData()) {
            data.accept(visitor);
         }
      }

      @Override
      public String toString() {
         return "TransactionDataImpl [branch=" + branch + ", readable=" + author + ", comment=" + comment + ", data=" + data + "]";
      }
   }

}
