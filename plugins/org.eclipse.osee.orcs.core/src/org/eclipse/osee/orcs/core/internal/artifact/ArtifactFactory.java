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

package org.eclipse.osee.orcs.core.internal.artifact;

import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.RelationOrder;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.exception.AttributeDoesNotExist;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.ArtifactDataFactory;
import org.eclipse.osee.orcs.core.ds.Attribute;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeFactory;
import org.eclipse.osee.orcs.data.AttributeReadable;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactFactory {

   private final ArtifactDataFactory factory;
   private final AttributeFactory attributeFactory;

   public ArtifactFactory(ArtifactDataFactory factory, AttributeFactory attributeFactory) {
      this.factory = factory;
      this.attributeFactory = attributeFactory;
   }

   public Artifact createArtifact(final OrcsSession session, ArtifactData artifactData) {
      return new ArtifactImpl(artifactData, attributeFactory);
   }

   public Artifact createArtifact(OrcsSession session, BranchId branch, ArtifactTypeToken artifactType, String guid) {
      ArtifactData artifactData = factory.create(branch, artifactType, guid);
      Artifact artifact = createArtifact(session, artifactData);
      artifact.setLoaded(true);
      return artifact;
   }

   public Artifact createArtifact(OrcsSession session, BranchId branch, ArtifactTypeToken artifactType, String guid, ApplicabilityId appId) {
      ArtifactData artifactData = factory.create(branch, artifactType, guid, appId);
      Artifact artifact = createArtifact(session, artifactData);
      artifact.setLoaded(true);
      return artifact;
   }

   public Artifact createArtifact(OrcsSession session, BranchId branch, ArtifactTypeToken artifactType, Long artifactId) {
      ArtifactData artifactData = factory.create(branch, artifactType, artifactId);
      Artifact artifact = createArtifact(session, artifactData);
      artifact.setLoaded(true);
      return artifact;
   }

   public Artifact createArtifact(OrcsSession session, BranchId branch, ArtifactTypeToken artifactType, Long artifactId, ApplicabilityId appId) {
      ArtifactData artifactData = factory.create(branch, artifactType, artifactId, appId);
      Artifact artifact = createArtifact(session, artifactData);
      artifact.setLoaded(true);
      return artifact;
   }

   public Artifact copyArtifact(OrcsSession session, Artifact source, Collection<AttributeTypeToken> types, BranchId ontoBranch) {
      ArtifactData artifactData = factory.copy(ontoBranch, source.getOrcsData());
      Artifact copy = createArtifact(session, artifactData);
      Collection<AttributeTypeToken> typesToCopy = getAllowedTypes(copy, types);
      for (AttributeTypeToken attributeType : typesToCopy) {
         for (AttributeReadable<?> attributeSource : source.getAttributes(attributeType)) {
            AttributeData data = getAttributeData(attributeSource);
            attributeFactory.copyAttribute(data, ontoBranch, copy);
         }
      }
      copy.setLoaded(true);
      return copy;
   }

   public Artifact introduceArtifact(OrcsSession session, Artifact source, Artifact destination, BranchId ontoBranch) {
      destination = processIntroduceArtifact(session, source, destination, ontoBranch);
      processIntroduceAttributes(source, destination, ontoBranch);
      destination.setLoaded(true);
      return destination;
   }

   private void processIntroduceAttributes(Artifact source, Artifact destination, BranchId ontoBranch) {
      Collection<Attribute<Object>> introduceAttributes = source.getAttributes(DeletionFlag.INCLUDE_DELETED);
      removeAttributes(source, destination);
      //introduce the existing attributes
      for (Attribute<Object> attr : introduceAttributes) {
         if (destination.isAttributeTypeValid(attr.getAttributeType())) {
            attributeFactory.introduceAttribute(attr.getOrcsData(), ontoBranch, destination);
         }
      }
   }

   private Artifact processIntroduceArtifact(OrcsSession session, Artifact source, Artifact destination, BranchId ontoBranch) {
      ArtifactData artifactData = factory.introduce(ontoBranch, source.getOrcsData());
      destination.setOrcsData(artifactData);
      return destination;
   }

   private void removeAttributes(Artifact introduce, Artifact destination) {
      for (Attribute<Object> destAttribute : destination.getAttributes(DeletionFlag.INCLUDE_DELETED)) {
         try {
            introduce.getAttributeById(destAttribute);
         } catch (AttributeDoesNotExist ex) {
            // remove new attributes
            destAttribute.delete();
         }
      }
   }

   public Artifact clone(OrcsSession session, Artifact source) {
      ArtifactData artifactData = factory.clone(source.getOrcsData());
      Artifact copy = createArtifact(session, artifactData);
      for (AttributeTypeToken attributeType : source.getExistingAttributeTypes()) {
         for (AttributeReadable<?> attributeSource : source.getAttributes(attributeType)) {
            AttributeData data = getAttributeData(attributeSource);
            attributeFactory.cloneAttribute(data, copy);
         }
      }
      copy.setLoaded(true);
      return copy;
   }

   private AttributeData getAttributeData(AttributeReadable<?> source) {
      return ((Attribute<?>) source).getOrcsData();
   }

   private Collection<AttributeTypeToken> getAllowedTypes(Artifact destination, Collection<AttributeTypeToken> types) {
      Set<AttributeTypeToken> toReturn = new HashSet<>();
      for (AttributeTypeToken type : types) {
         if (type.notEqual(RelationOrder)) {
            if (destination.isAttributeTypeValid(type)) {
               toReturn.add(type);
            }
         }
      }
      return toReturn;
   }
}