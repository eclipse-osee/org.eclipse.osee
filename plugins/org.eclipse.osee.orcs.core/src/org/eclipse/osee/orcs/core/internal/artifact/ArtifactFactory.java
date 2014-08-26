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
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.ArtifactDataFactory;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactImpl.BranchProvider;
import org.eclipse.osee.orcs.core.internal.attribute.Attribute;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeFactory;
import org.eclipse.osee.orcs.data.ArtifactTypes;
import org.eclipse.osee.orcs.data.AttributeReadable;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactFactory {

   private final ArtifactDataFactory factory;
   private final AttributeFactory attributeFactory;
   private final ArtifactTypes artifactTypeCache;
   private final BranchProviderFactory branchProviderFactory;

   public static interface BranchProviderFactory {
      BranchProvider createBranchProvider(OrcsSession session);
   }

   public ArtifactFactory(ArtifactDataFactory factory, AttributeFactory attributeFactory, ArtifactTypes artifactTypeCache, BranchProviderFactory branchProviderFactory) {
      this.factory = factory;
      this.attributeFactory = attributeFactory;
      this.artifactTypeCache = artifactTypeCache;
      this.branchProviderFactory = branchProviderFactory;
   }

   public Artifact createArtifact(final OrcsSession session, ArtifactData artifactData) throws OseeCoreException {
      BranchProvider branchProvider = branchProviderFactory.createBranchProvider(session);
      return new ArtifactImpl(artifactTypeCache, artifactData, attributeFactory, branchProvider);
   }

   public Artifact createArtifact(OrcsSession session, IOseeBranch branch, IArtifactType artifactType, String guid) throws OseeCoreException {
      ArtifactData artifactData = factory.create(branch, artifactType, guid);
      Artifact artifact = createArtifact(session, artifactData);
      artifact.setLoaded(true);
      return artifact;
   }

   public Artifact copyArtifact(OrcsSession session, Artifact source, Collection<? extends IAttributeType> types, IOseeBranch ontoBranch) throws OseeCoreException {
      ArtifactData artifactData = factory.copy(ontoBranch, source.getOrcsData());
      Artifact copy = createArtifact(session, artifactData);
      Collection<? extends IAttributeType> typeToCopy = getAllowedTypes(copy, types);
      for (IAttributeType attributeType : typeToCopy) {
         for (AttributeReadable<?> attributeSource : source.getAttributes(attributeType)) {
            AttributeData data = getAttributeData(attributeSource);
            attributeFactory.copyAttribute(data, ontoBranch, copy);
         }
      }
      copy.setLoaded(true);
      return copy;
   }

   public Artifact introduceArtifact(OrcsSession session, Artifact source, IOseeBranch ontoBranch) throws OseeCoreException {
      Conditions.checkExpressionFailOnTrue(ontoBranch.equals(source.getBranch()),
         "Source artifact is on the same branch as [%s]", ontoBranch);

      ArtifactData artifactData = factory.introduce(ontoBranch, source.getOrcsData());
      Artifact introducedArt = createArtifact(session, artifactData);
      Collection<? extends IAttributeType> typeToCopy =
         getAllowedTypes(introducedArt, source.getExistingAttributeTypes());
      for (IAttributeType attributeType : typeToCopy) {
         for (AttributeReadable<?> attributeSource : source.getAttributes(attributeType)) {
            AttributeData data = getAttributeData(attributeSource);
            attributeFactory.introduceAttribute(data, ontoBranch, introducedArt);
         }
      }
      introducedArt.setLoaded(true);
      return introducedArt;
   }

   public Artifact clone(OrcsSession session, Artifact source) throws OseeCoreException {
      ArtifactData artifactData = factory.clone(source.getOrcsData());
      Artifact copy = createArtifact(session, artifactData);
      for (IAttributeType attributeType : source.getExistingAttributeTypes()) {
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

   private Collection<? extends IAttributeType> getAllowedTypes(Artifact destination, Collection<? extends IAttributeType> types) throws OseeCoreException {
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

}