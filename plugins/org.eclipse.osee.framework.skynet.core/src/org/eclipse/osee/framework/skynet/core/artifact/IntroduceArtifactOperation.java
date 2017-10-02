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
package org.eclipse.osee.framework.skynet.core.artifact;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;

/**
 * @author Ryan D. Brooks
 * @author David W. Miller
 */
public class IntroduceArtifactOperation {
   private final Artifact fosterParent;
   private final BranchId destinationBranch;
   private Collection<Artifact> sourceArtifacts;
   private List<Artifact> destinationArtifacts;

   public IntroduceArtifactOperation(BranchId destinationBranch)  {
      this(OseeSystemArtifacts.getDefaultHierarchyRootArtifact(destinationBranch));
   }

   public IntroduceArtifactOperation(Artifact fosterParent) {
      this.fosterParent = fosterParent;
      this.destinationBranch = fosterParent.getBranch();
   }

   /**
    * @return the introduced artifact on the destination branch
    * 
    */
   public Artifact introduce(Artifact sourceArtifact)  {
      introduce(Arrays.asList(sourceArtifact));
      return destinationArtifacts.get(0);
   }

   public List<Artifact> introduce(Collection<Artifact> sourceArtifacts)  {
      this.sourceArtifacts = sourceArtifacts;
      destinationArtifacts = new ArrayList<>(sourceArtifacts.size());

      for (Artifact sourceArtifact : sourceArtifacts) {
         introduceArtifact(sourceArtifact);
      }
      return destinationArtifacts;
   }

   private void introduceArtifact(Artifact sourceArtifact)  {
      Artifact destinationArtifact =
         ArtifactQuery.getArtifactOrNull(sourceArtifact, destinationBranch, DeletionFlag.INCLUDE_DELETED);

      if (destinationArtifact == null) {
         destinationArtifact = sourceArtifact.introduceShallowArtifact(destinationBranch);
         processArtifact(sourceArtifact, destinationArtifact);
      } else {
         destinationArtifact.introduce(sourceArtifact);
         processArtifact(sourceArtifact, destinationArtifact);
      }
      destinationArtifact.meetMinimumAttributeCounts(true);
      destinationArtifacts.add(destinationArtifact);
   }

   private void processArtifact(Artifact sourceArtifact, Artifact destinationArtifact)  {
      introduceAttributes(sourceArtifact, destinationArtifact);

      if (!sourceArtifact.isHistorical()) {
         introduceRelations(sourceArtifact, destinationArtifact);
         try {
            if (sourceArtifact.hasParent() && !destinationArtifact.hasParent() && !sourceArtifacts.contains(
               sourceArtifact.getParent())) {
               fosterParent.addChild(destinationArtifact);
            }
         } catch (MultipleArtifactsExist ex) {
            fosterParent.addChild(destinationArtifact);
         }
      } else {
         OseeLog.logf(Activator.class, Level.INFO,
            "Historical relations are only supported on the server. Artifact [%s] is historical", sourceArtifact);
      }
   }

   private void introduceAttributes(Artifact sourceArtifact, Artifact destinationArtifact) throws OseeDataStoreException {
      List<Attribute<?>> sourceAttributes = sourceArtifact.getAttributes(true);

      removeNewAttributesFromDestination(sourceArtifact, destinationArtifact);

      // introduce the existing attributes
      for (Attribute<?> sourceAttribute : sourceAttributes) {
         // must be valid for the destination branch
         if (destinationArtifact.isAttributeTypeValid(sourceAttribute.getAttributeType())) {
            introduceAttribute(sourceAttribute, destinationArtifact);
         }
      }
   }

   private void introduceAttribute(Attribute<?> sourceAttribute, Artifact destinationArtifact) throws OseeDataStoreException {
      if (sourceAttribute.isDirty()) {
         throw new OseeArgumentException("The un-persisted attribute [%s] can not be introduced until it is persisted.",
            sourceAttribute);

      } else if (sourceAttribute.isInDb()) {
         Attribute<?> destinationAttribute = destinationArtifact.getAttributeById(sourceAttribute.getId(), true);

         if (destinationAttribute == null) {
            destinationArtifact.internalInitializeAttribute(sourceAttribute.getAttributeType(), sourceAttribute,
               sourceAttribute.getGammaId(), sourceAttribute.getModificationType(),
               sourceAttribute.getApplicabilityId(), true,
               sourceAttribute.getAttributeDataProvider().getData()).internalSetModType(
                  sourceAttribute.getModificationType(), true, true);
         } else {
            destinationAttribute.introduce(sourceAttribute);
         }
      }
   }

   private void introduceRelations(Artifact sourceArtifact, Artifact destinationArtifact)  {
      List<RelationLink> sourceRelations = sourceArtifact.getRelationsAll(DeletionFlag.INCLUDE_DELETED);

      for (RelationLink sourceRelation : sourceRelations) {
         // must be valid for the destination branch
         if (destinationArtifact.isRelationTypeValid(sourceRelation.getRelationType())) {
            introduceRelation(sourceRelation, destinationArtifact);
         }
      }
   }

   private void introduceRelation(RelationLink sourceRelation, Artifact destinationArtifact) throws OseeDataStoreException {
      if (sourceRelation.isDirty()) {
         throw new OseeArgumentException("The un-persisted relation [%s] can not be introduced until it is persisted.",
            sourceRelation);
      } else if (sourceRelation.isInDb()) {
         ArtifactToken srcArtA = sourceRelation.getArtifactIdA();
         ArtifactToken srcArtB = sourceRelation.getArtifactIdB();
         RelationLink destinationRelation =
            RelationManager.getLoadedRelationById(sourceRelation.getId(), srcArtA, srcArtB, destinationBranch);

         if (destinationRelation == null) {
            if (doesRelatedArtifactExist(destinationArtifact, srcArtA, srcArtB)) {
               ModificationType modType = sourceRelation.getModificationType();
               ArtifactToken destArtA = ArtifactToken.valueOf(srcArtA, destinationBranch);
               ArtifactToken destArtB = ArtifactToken.valueOf(srcArtB, destinationBranch);
               destinationRelation = RelationManager.getOrCreate(destArtA, destArtB, sourceRelation.getRelationType(),
                  sourceRelation.getId(), sourceRelation.getGammaId(), sourceRelation.getRationale(), modType,
                  sourceRelation.getApplicabilityId());
               destinationRelation.internalSetModType(modType, true, true);
            }
         } else {
            destinationRelation.introduce(sourceRelation.getGammaId(), sourceRelation.getModificationType());
         }
      }
   }

   private void removeNewAttributesFromDestination(Artifact sourceArtifact, Artifact destinationArtifact)  {
      List<Attribute<?>> destAttributes = destinationArtifact.getAttributes(true);

      // since introduce is 'replacing' the destination artifact with the source artifact,
      // any new attributes from the destination artifact should be removed/deleted.
      for (Attribute<?> destAttribute : destAttributes) {
         Attribute<?> attribute = sourceArtifact.getAttributeById(destAttribute.getId(), true);
         if (attribute == null) {
            destAttribute.delete();
         }
      }
   }

   private boolean doesRelatedArtifactExist(Artifact destinationArtifact, ArtifactId aArtifactId, ArtifactId bArtifactId) {
      ArtifactId otherId = destinationArtifact.equals(aArtifactId.getId()) ? bArtifactId : aArtifactId;
      Artifact otherArtifact = ArtifactQuery.checkArtifactFromId(otherId, destinationBranch);

      boolean found = otherArtifact != null;
      if (!found) {
         for (Artifact sourceArtifact : sourceArtifacts) {
            if (sourceArtifact.equals(otherId)) {
               return true;
            }
         }
      }
      return found;
   }
}