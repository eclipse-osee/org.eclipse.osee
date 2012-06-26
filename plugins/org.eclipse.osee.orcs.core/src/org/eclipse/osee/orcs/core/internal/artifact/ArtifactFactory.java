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
package org.eclipse.osee.orcs.core.internal.artifact;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.cache.ArtifactTypeCache;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.ArtifactDataFactory;
import org.eclipse.osee.orcs.core.ds.ArtifactTransactionData;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.data.ArtifactTxDataImpl;
import org.eclipse.osee.orcs.core.internal.AbstractProxy;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeFactory;
import org.eclipse.osee.orcs.core.internal.relation.RelationFactory;
import org.eclipse.osee.orcs.core.internal.transaction.WriteableProxy;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.ArtifactWriteable;
import org.eclipse.osee.orcs.data.AttributeReadable;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactFactory {

   private final AttributeFactory attributeFactory;
   private final RelationFactory relationFactory;
   private final ArtifactTypeCache artifactTypeCache;
   private final BranchCache branchCache;
   private final ArtifactDataFactory factory;

   public ArtifactFactory(ArtifactDataFactory factory, AttributeFactory attributeFactory, RelationFactory relationFactory, ArtifactTypeCache artifactTypeCache, BranchCache branchCache) {
      super();
      this.factory = factory;
      this.attributeFactory = attributeFactory;
      this.relationFactory = relationFactory;
      this.artifactTypeCache = artifactTypeCache;
      this.branchCache = branchCache;
   }

   public ArtifactWriteable createWriteableArtifact(ArtifactData artifactData) throws OseeCoreException {
      //TODO implement an artifact class resolver for specific artifact types
      ArtifactImpl artifact = createArtifact(artifactData);
      artifact.setLoaded(true);
      WritableArtifactProxy proxy = new WritableArtifactProxy(this, artifact);
      return proxy;
   }

   public ArtifactWriteable createWriteableArtifact(IOseeBranch branch, IArtifactType artifactType, String guid) throws OseeCoreException {
      ArtifactImpl artifact = createArtifact(branch, artifactType, guid);
      artifact.setLoaded(true);
      WritableArtifactProxy proxy = new WritableArtifactProxy(this, artifact);
      return proxy;
   }

   public ArtifactReadable createReadableArtifact(ArtifactData artifactData) throws OseeCoreException {
      ArtifactImpl artifact = createArtifact(artifactData);
      ReadableArtifactProxy proxy = new ReadableArtifactProxy(artifact);
      return proxy;
   }

   public ArtifactWriteable copyArtifact(ArtifactReadable source, Collection<? extends IAttributeType> types, IOseeBranch ontoBranch) throws OseeCoreException {
      ArtifactImpl artifact = copyArtifactHelperTypeChecked(asArtifactImpl(source), ontoBranch, types);
      artifact.setLoaded(true);
      WritableArtifactProxy proxy = new WritableArtifactProxy(this, artifact);
      return proxy;
   }

   public ArtifactWriteable introduceArtifact(ArtifactReadable source, IOseeBranch ontoBranch) throws OseeCoreException {
      ArtifactImpl artifact = introduceArtifactHelper(asArtifactImpl(source), ontoBranch);
      artifact.setLoaded(true);
      WritableArtifactProxy proxy = new WritableArtifactProxy(this, artifact);
      return proxy;
   }

   public ArtifactImpl clone(ArtifactImpl source) throws OseeCoreException {
      ArtifactImpl copy = copyAllHelper(source);
      copy.setLoaded(true);
      return copy;
   }

   //////////////////////////////////////////////////////////////
   //////////////////////////////////////////////////////////////

   private ArtifactImpl createArtifact(ArtifactData artifactData) throws OseeCoreException {
      //TODO implement an artifact class resolver for specific artifact types
      RelationContainer relationContainer = relationFactory.createRelationContainer(artifactData.getLocalId());

      ArtifactType type = artifactTypeCache.getByGuid(artifactData.getTypeUuid());
      Branch branch = branchCache.getById(artifactData.getVersion().getBranchId());

      return new ArtifactImpl(type, branch, artifactData, attributeFactory, relationContainer);
   }

   private ArtifactImpl createArtifact(IOseeBranch branch, IArtifactType artifactType, String guid) throws OseeCoreException {
      ArtifactData artifactData = factory.create(branch, artifactType, guid);
      ArtifactImpl artifact = createArtifact(artifactData);
      return artifact;
   }

   private ArtifactImpl copyAllHelper(ArtifactImpl source) throws OseeCoreException {
      ArtifactData artifactData = factory.copy(source.getBranch(), source.getOrcsData());
      ArtifactImpl artifact = createArtifact(artifactData);
      for (IAttributeType attributeType : source.getExistingAttributeTypes()) {
         for (AttributeReadable<?> attributeSource : source.getAttributes(attributeType)) {
            attributeFactory.copyAttribute(attributeSource, source.getBranch(), artifact);
         }
      }
      return artifact;
   }

   private ArtifactImpl copyArtifactHelperTypeChecked(ArtifactImpl source, IOseeBranch ontoBranch, Collection<? extends IAttributeType> types) throws OseeCoreException {
      ArtifactData artifactData = factory.copy(ontoBranch, source.getOrcsData());
      ArtifactImpl artifact = createArtifact(artifactData);
      Collection<? extends IAttributeType> typeToCopy = getAllowedTypes(artifact, types);
      for (IAttributeType attributeType : typeToCopy) {
         for (AttributeReadable<?> attributeSource : source.getAttributes(attributeType)) {
            attributeFactory.copyAttribute(attributeSource, ontoBranch, artifact);
         }
      }
      return artifact;
   }

   private ArtifactImpl introduceArtifactHelper(ArtifactImpl source, IOseeBranch ontoBranch) throws OseeCoreException {
      Conditions.checkExpressionFailOnTrue(ontoBranch.equals(source.getBranch()),
         "Source artifact is on the same branch as transaction [%s]", ontoBranch);

      ArtifactData artifactData = factory.introduce(ontoBranch, source.getOrcsData());
      ArtifactImpl artifact = createArtifact(artifactData);
      Collection<? extends IAttributeType> typeToCopy = getAllowedTypes(artifact, source.getExistingAttributeTypes());
      for (IAttributeType attributeType : typeToCopy) {
         for (AttributeReadable<?> attributeSource : source.getAttributes(attributeType)) {
            attributeFactory.introduceAttribute(attributeSource, ontoBranch, artifact);
         }
      }
      return artifact;
   }

   private Collection<? extends IAttributeType> getAllowedTypes(ArtifactReadable destination, Collection<? extends IAttributeType> types) throws OseeCoreException {
      Set<IAttributeType> toReturn = new HashSet<IAttributeType>();
      for (IAttributeType type : types) {
         if (!CoreAttributeTypes.RelationOrder.equals(type)) {
            if (destination.isAttributeTypeValid(type)) {
               toReturn.add(type);
            }
         }
      }
      return toReturn;
   }

   //////////////////////////////////////////////////////////////
   //////////////////////////////////////////////////////////////

   public ArtifactWriteable asWriteableArtifact(ArtifactReadable readable) throws OseeCoreException {
      ArtifactWriteable toReturn = null;
      if (readable instanceof WritableArtifactProxy) {
         toReturn = (WritableArtifactProxy) readable;
      } else if (readable instanceof ReadableArtifactProxy) {
         ArtifactImpl artifact = asArtifactImpl(readable);
         toReturn = new WritableArtifactProxy(this, artifact);
      } else if (readable instanceof ArtifactImpl) {
         toReturn = new WritableArtifactProxy(this, (ArtifactImpl) readable);
      } else {
         throw new OseeStateException("Unable to convert from [%s] to ArtifactWriteable ",
            readable != null ? readable.getClass().getName() : "null");
      }
      return toReturn;
   }

   @SuppressWarnings("unchecked")
   public ArtifactImpl asArtifactImpl(ArtifactReadable readable) {
      ArtifactImpl toReturn = null;
      if (readable instanceof AbstractProxy) {
         AbstractProxy<? extends ArtifactImpl> proxy = (AbstractProxy<? extends ArtifactImpl>) readable;
         toReturn = proxy.getProxiedObject();
      } else if (readable instanceof ArtifactImpl) {
         toReturn = (ArtifactImpl) readable;
      }
      return toReturn;
   }

   /////////////////////////////////////////////
   public void setBackingData(ArtifactWriteable writeable, ArtifactTransactionData data) throws OseeCoreException {
      // TX_TODO In Case of exception restore all to original ?
      if (writeable instanceof WritableArtifactProxy) {
         WritableArtifactProxy proxy = (WritableArtifactProxy) writeable;
         ArtifactImpl systemArtifact = proxy.getOriginal();
         ArtifactImpl writeableArtifact = proxy.getProxiedObject();
         setBackingData(data, writeableArtifact);
         setBackingData(data, systemArtifact);
      } else {
         throw new OseeArgumentException("Invalid object [%s]", writeable);
      }
   }

   private void setBackingData(ArtifactTransactionData data, ArtifactImpl destination) throws OseeCoreException {
      synchronized (destination) {
         ArtifactData newData = data.getArtifactData();
         destination.setOrcsData(newData);
         List<AttributeData> attributes = data.getAttributeData();
         destination.setBackingData(attributes);
      }
   }

   public void setEditState(ArtifactWriteable writeable, boolean value) {
      if (writeable instanceof WriteableProxy) {
         WriteableProxy proxy = (WriteableProxy) writeable;
         proxy.setWriteState(value);
      }
   }

   public ArtifactTransactionData getChangeData(ArtifactWriteable artifact) throws OseeCoreException {
      ArtifactTxDataImpl toReturn = null;
      ArtifactImpl impl = asArtifactImpl(artifact);
      if (artifact.isDirty()) {
         ArtifactData artData = impl.getOrcsData();
         ArtifactData artifactData = factory.clone(artData);
         List<AttributeData> attributes = attributeFactory.getChangeData(impl);
         toReturn = new ArtifactTxDataImpl(artifactData, attributes);
      }
      return toReturn;
   }

}