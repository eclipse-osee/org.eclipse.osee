/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.transaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.ITransaction;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.BranchDataStore;
import org.eclipse.osee.orcs.core.ds.TransactionData;
import org.eclipse.osee.orcs.core.internal.SessionContext;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactFactory;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.data.WritableArtifact;
import org.eclipse.osee.orcs.transaction.OrcsTransaction;

/**
 * @author Roberto E. Escobar
 */
public class OrcsTransactionImpl implements OrcsTransaction, TransactionData {

   private final Log logger;
   private final IOseeBranch branch;
   private final BranchDataStore dataStore;
   private final ArtifactFactory artifactFactory;

   private String comment;
   private ReadableArtifact authorArtifact;
   private final Map<String, WritableArtifact> writeableArtifacts = new ConcurrentHashMap<String, WritableArtifact>();

   public OrcsTransactionImpl(Log logger, SessionContext sessionContext, BranchDataStore dataStore, ArtifactFactory artifactFactory, IOseeBranch branch) {
      super();
      this.logger = logger;
      this.dataStore = dataStore;
      this.artifactFactory = artifactFactory;
      this.branch = branch;
   }

   @Override
   public void setComment(String comment) {
      this.comment = comment;
   }

   public void setAuthor(ReadableArtifact authorArtifact) {
      this.authorArtifact = authorArtifact;
   }

   @Override
   public ReadableArtifact getAuthor() {
      return authorArtifact;
   }

   @Override
   public String getComment() {
      return comment;
   }

   @Override
   public Collection<WritableArtifact> getWriteables() {
      return writeableArtifacts.values();
   }

   @Override
   public void rollback() {
      // TODO
      // ? 
   }

   private void startCommit() {

   }

   private void closeCommit() {

   }

   @Override
   public ITransaction commit() throws OseeCoreException {
      ITransaction transaction = null;
      try {
         startCommit();
         Callable<ITransaction> callable = dataStore.commitTransaction(this);
         transaction = callable.call();
      } catch (Exception ex) {
         rollback();
         OseeExceptions.wrapAndThrow(ex);
      } finally {
         closeCommit();
      }
      return transaction;
   }

   @Override
   public IOseeBranch getBranch() {
      return branch;
   }

   @Override
   public synchronized WritableArtifact asWritable(ReadableArtifact readable) throws OseeCoreException {
      String guid = readable.getGuid();
      WritableArtifact toReturn = writeableArtifacts.get(guid);
      if (toReturn == null) {
         toReturn = artifactFactory.createWriteableArtifact(readable);
         writeableArtifacts.put(guid, toReturn);
      }
      return toReturn;
   }

   @Override
   public List<WritableArtifact> asWritable(Collection<? extends ReadableArtifact> artifacts) throws OseeCoreException {
      List<WritableArtifact> toReturn = new ArrayList<WritableArtifact>();
      for (ReadableArtifact readable : artifacts) {
         toReturn.add(asWritable(readable));
      }
      return toReturn;
   }

   @Override
   public WritableArtifact createArtifact(IArtifactType artifactType, String name) throws OseeCoreException {
      return null;
   }

   @Override
   public WritableArtifact createArtifact(IArtifactType artifactType, String name, GUID guid) throws OseeCoreException {
      return null;
   }

   @Override
   public WritableArtifact createArtifact(IArtifactToken artifactToken) throws OseeCoreException {
      return null;
   }

   @Override
   public WritableArtifact duplicateArtifact(ReadableArtifact sourceArtifact) throws OseeCoreException {
      return null;
   }

   @Override
   public WritableArtifact duplicateArtifact(ReadableArtifact sourceArtifact, Collection<? extends IAttributeType> attributesToDuplicate) throws OseeCoreException {
      return null;
   }

   @Override
   public WritableArtifact reflectArtifact(ReadableArtifact sourceArtifact) throws OseeCoreException {
      return null;
   }

   @Override
   public void deleteArtifact(WritableArtifact artifact) throws OseeCoreException {
   }

}
