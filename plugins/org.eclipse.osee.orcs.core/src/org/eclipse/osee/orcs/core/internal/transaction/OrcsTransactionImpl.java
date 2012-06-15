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
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.BranchDataStore;
import org.eclipse.osee.orcs.core.ds.DataLoader;
import org.eclipse.osee.orcs.core.ds.TransactionData;
import org.eclipse.osee.orcs.core.internal.SessionContext;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactFactory;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactImpl;
import org.eclipse.osee.orcs.core.internal.artifact.WritableArtifactProxy;
import org.eclipse.osee.orcs.core.internal.attribute.Attribute;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeFactory;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.ArtifactWriteable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.data.GraphReadable;
import org.eclipse.osee.orcs.data.GraphWriteable;
import org.eclipse.osee.orcs.transaction.OrcsTransaction;

/**
 * @author Roberto E. Escobar
 */
public class OrcsTransactionImpl implements OrcsTransaction, TransactionData {

   private final Log logger;
   private final IOseeBranch branch;
   private final BranchDataStore dataStore;
   private final ArtifactFactory artifactFactory;
   private final AttributeFactory attributeFactory;

   private String comment;
   private ArtifactReadable authorArtifact;
   private final Map<String, ArtifactWriteable> writeableArtifacts = new ConcurrentHashMap<String, ArtifactWriteable>();
   private final DataLoader dataLoader;

   public OrcsTransactionImpl(Log logger, SessionContext sessionContext, BranchDataStore dataStore, ArtifactFactory artifactFactory, AttributeFactory attributeFactory, DataLoader dataLoader, IOseeBranch branch) {
      super();
      this.logger = logger;
      this.dataStore = dataStore;
      this.artifactFactory = artifactFactory;
      this.attributeFactory = attributeFactory;
      this.dataLoader = dataLoader;
      this.branch = branch;
   }

   @Override
   public void setComment(String comment) {
      this.comment = comment;
   }

   public void setAuthor(ArtifactReadable authorArtifact) {
      this.authorArtifact = authorArtifact;
   }

   @Override
   public ArtifactReadable getAuthor() {
      return authorArtifact;
   }

   @Override
   public String getComment() {
      return comment;
   }

   @Override
   public Collection<ArtifactWriteable> getWriteables() {
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
   public synchronized ArtifactWriteable asWritable(ArtifactReadable readable) throws OseeCoreException {
      String guid = readable.getGuid();
      ArtifactWriteable toReturn = writeableArtifacts.get(guid);
      if (toReturn == null) {
         toReturn = artifactFactory.createWriteableArtifact(readable);
         writeableArtifacts.put(guid, toReturn);
      }
      return toReturn;
   }

   @Override
   public List<ArtifactWriteable> asWritable(Collection<? extends ArtifactReadable> artifacts) throws OseeCoreException {
      List<ArtifactWriteable> toReturn = new ArrayList<ArtifactWriteable>();
      for (ArtifactReadable readable : artifacts) {
         toReturn.add(asWritable(readable));
      }
      return toReturn;
   }

   @Override
   public GraphWriteable asWriteableGraph(GraphReadable readableGraph) throws OseeCoreException {
      return null;
   }

   @Override
   public ArtifactWriteable createArtifact(IArtifactType artifactType, String name) throws OseeCoreException {
      return createArtifact(artifactType, name, GUID.create());
   }

   @Override
   public ArtifactWriteable createArtifact(IArtifactType artifactType, String name, String guid) throws OseeCoreException {
      ArtifactData newArtifact = dataLoader.createNewArtifactData();
      newArtifact.setArtTypeUuid(artifactType.getGuid());
      newArtifact.setGuid(guid);
      ArtifactWriteable artifact = artifactFactory.createWriteableArtifact(newArtifact);
      artifact.setName(name);
      return artifact;
   }

   @Override
   public ArtifactWriteable createArtifact(IArtifactToken artifactToken) throws OseeCoreException {
      return createArtifact(artifactToken.getArtifactType(), artifactToken.getName(), artifactToken.getGuid());
   }

   @Override
   public ArtifactWriteable duplicateArtifact(ArtifactReadable sourceArtifact) throws OseeCoreException {
      return duplicateArtifact(sourceArtifact, sourceArtifact.getAttributeTypes());
   }

   @Override
   public ArtifactWriteable duplicateArtifact(ArtifactReadable sourceArtifact, Collection<? extends IAttributeType> attributesToDuplicate) throws OseeCoreException {
      ArtifactImpl duplicate =
         ((WritableArtifactProxy) createArtifact(sourceArtifact.getArtifactType(), sourceArtifact.getName())).getOriginal();
      for (AttributeReadable<?> attribute : duplicate.getAttributes()) {
         AttributeData attrData = ((Attribute<?>) attribute).getAttributeData();
         AttributeData attr = dataLoader.duplicateAttributeData(attrData);
         attributeFactory.createAttribute(duplicate.getAttributeContainer(), attr);
      }
      return duplicate;
   }

   @Override
   public ArtifactWriteable introduceArtifact(ArtifactReadable sourceArtifact) throws OseeCoreException {
      if (sourceArtifact.getBranch().equals(branch)) {
         throw new OseeArgumentException("Source artifact is on same branch as transaction");
      }
      return null;
   }

   @Override
   public void deleteArtifact(ArtifactWriteable artifact) throws OseeCoreException {
   }

}
