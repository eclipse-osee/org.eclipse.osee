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
import java.util.Set;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.cache.ArtifactTypeCache;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.ArtifactDataFactory;
import org.eclipse.osee.orcs.core.internal.AbstractProxy;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeFactory;
import org.eclipse.osee.orcs.core.internal.relation.RelationFactory;
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

   public ArtifactWriteable createWriteableArtifact(IOseeBranch branch, IArtifactType artifactType, String guid) throws OseeCoreException {
      ArtifactData artifactData = factory.create(branch, artifactType, guid);
      ArtifactWriteable artifact = createWriteableArtifact(artifactData);
      return artifact;
   }

   public ArtifactWriteable createWriteableArtifact(ArtifactData artifactData) throws OseeCoreException {
      //TODO implement an artifact class resolver for specific artifact types
      ArtifactImpl artifact = createArtifactImpl(artifactData);
      WritableArtifactProxy proxy = new WritableArtifactProxy(artifact);
      return proxy;
   }

   public ArtifactReadable createReadableArtifact(ArtifactData artifactData) throws OseeCoreException {
      //TODO implement an artifact class resolver for specific artifact types
      ArtifactImpl artifact = createArtifactImpl(artifactData);
      ReadableArtifactProxy proxy = new ReadableArtifactProxy(artifact);
      return proxy;
   }

   public ArtifactWriteable asWriteableArtifact(ArtifactReadable readable) throws OseeCoreException {
      ArtifactWriteable toReturn = null;
      if (readable instanceof WritableArtifactProxy) {
         toReturn = (WritableArtifactProxy) readable;
      } else if (readable instanceof ReadableArtifactProxy) {
         ArtifactImpl artifact = asArtifactImpl(readable);
         toReturn = new WritableArtifactProxy(artifact);
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
      }
      return toReturn;
   }

   public ArtifactWriteable copyArtifact(ArtifactReadable source, Collection<? extends IAttributeType> types, IOseeBranch ontoBranch) throws OseeCoreException {
      ArtifactData artifactData = factory.copy(ontoBranch, getOrcsData(source));
      ArtifactWriteable duplicate = createWriteableArtifact(artifactData);

      AttributeContainer destination = getAttributeContainer(duplicate);
      Collection<? extends IAttributeType> typeToCopy = getAllowedTypes(duplicate, types);
      for (IAttributeType attributeType : typeToCopy) {
         for (AttributeReadable<?> attributeSource : source.getAttributes(attributeType)) {
            attributeFactory.copyAttribute(attributeSource, ontoBranch, destination);
         }
      }
      return duplicate;
   }

   public ArtifactWriteable introduceArtifact(ArtifactReadable source, IOseeBranch ontoBranch) throws OseeCoreException {
      Conditions.checkExpressionFailOnTrue(ontoBranch.equals(source.getBranch()),
         "Source artifact is on the same branch as transaction [%s]", ontoBranch);

      ArtifactData artifactData = factory.introduce(ontoBranch, getOrcsData(source));

      ArtifactWriteable duplicate = createWriteableArtifact(artifactData);
      AttributeContainer destination = getAttributeContainer(duplicate);

      Collection<? extends IAttributeType> typeToCopy = getAllowedTypes(duplicate, source.getExistingAttributeTypes());
      for (IAttributeType attributeType : typeToCopy) {
         for (AttributeReadable<?> attributeSource : source.getAttributes(attributeType)) {
            attributeFactory.introduceAttribute(attributeSource, ontoBranch, destination);
         }
      }
      return duplicate;
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

   private AttributeContainer getAttributeContainer(ArtifactWriteable item) {
      return asArtifactImpl(item).getAttributeContainer();
   }

   private ArtifactData getOrcsData(ArtifactReadable item) {
      return asArtifactImpl(item).getOrcsData();
   }

   private ArtifactImpl createArtifactImpl(ArtifactData artifactData) throws OseeCoreException {
      //      RelationContainer relationContainer = relationFactory.createRelationContainer(artifactData.getLocalId());

      //      ArtifactType type = artifactTypeCache.getByGuid(artifactData.getTypeUuid());
      //      Branch branch = branchCache.getById(artifactData.getBranchId());
      ArtifactImpl artifact = null;
      //         new ArtifactImpl(type, branch, relationContainer, artifactData);
      return artifact;
   }
}