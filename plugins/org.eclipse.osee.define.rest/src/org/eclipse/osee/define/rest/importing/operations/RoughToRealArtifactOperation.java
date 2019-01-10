/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.rest.importing.operations;

import static org.eclipse.osee.framework.core.enums.RelationSorter.USER_DEFINED;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import org.eclipse.define.api.importing.IArtifactExtractor;
import org.eclipse.define.api.importing.RoughArtifact;
import org.eclipse.define.api.importing.RoughArtifactCollector;
import org.eclipse.define.api.importing.RoughRelation;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.define.rest.importing.resolvers.IArtifactImportResolver;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.CoreActivityTypes;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Roberto E. Escobar
 * @author David W. Miller
 */
public class RoughToRealArtifactOperation {
   private final OrcsApi orcsApi;
   private final ActivityLog activityLog;
   private final RoughArtifactCollector rawData;
   private final IArtifactImportResolver artifactResolver;
   private final Map<RoughArtifact, ArtifactReadable> roughToRealArtifacts;
   private final Collection<ArtifactId> createdArtifacts;
   private final ArtifactReadable destinationArtifact;
   private final RelationSorter importArtifactOrder;
   private final boolean deleteUnmatchedArtifacts;
   private Collection<ArtifactReadable> unmatchedArtifacts;
   private final IArtifactExtractor extractor;
   private final TransactionBuilder transaction;
   private boolean addRelation = true;

   public RoughToRealArtifactOperation(OrcsApi orcsApi, ActivityLog activityLog, TransactionBuilder transaction, ArtifactReadable destinationArtifact, RoughArtifactCollector rawData, IArtifactImportResolver artifactResolver, boolean deleteUnmatchedArtifacts, IArtifactExtractor extractor) {
      this.activityLog = activityLog;
      this.orcsApi = orcsApi;
      this.rawData = rawData;
      this.transaction = transaction;
      this.artifactResolver = artifactResolver;
      this.destinationArtifact = destinationArtifact;
      this.importArtifactOrder = USER_DEFINED;
      this.roughToRealArtifacts = new HashMap<>();
      this.createdArtifacts = new LinkedList<>();
      this.deleteUnmatchedArtifacts = deleteUnmatchedArtifacts;
      this.extractor = extractor;
      roughToRealArtifacts.put(rawData.getParentRoughArtifact(), this.destinationArtifact);
   }

   public void doWork() {

      this.unmatchedArtifacts = destinationArtifact.getDescendants();

      for (RoughArtifact roughArtifact : rawData.getParentRoughArtifact().getChildren()) {
         ArtifactId child = createArtifact(roughArtifact, destinationArtifact);
         createdArtifacts.add(child);
         if (addRelation && child != null && noParent(child)) {
            transaction.relate(destinationArtifact, CoreRelationTypes.Default_Hierarchical__Child, child,
               importArtifactOrder);
         }

         for (RoughRelation roughRelation : rawData.getRoughRelations()) {
            createRelation(roughRelation);
         }
      }

      if (deleteUnmatchedArtifacts) {
         for (ArtifactReadable toDelete : unmatchedArtifacts) {
            transaction.deleteArtifact(toDelete);
         }
      }
   }

   private boolean noParent(ArtifactId artifact) {
      if (artifact instanceof ArtifactReadable) {
         ArtifactReadable parent = ((ArtifactReadable) artifact).getParent();
         if (parent == null) {
            return true;
         }
         if (!parent.isValid()) {
            return true;
         }
         return false;
      }
      throw new OseeCoreException("Incorrect artifact instance in noParent in rough to real operation");
   }

   private ArtifactId createArtifact(RoughArtifact roughArtifact, ArtifactId realParent) {
      ArtifactReadable realArtifact = roughToRealArtifacts.get(roughArtifact);
      if (realArtifact != null) {
         return realArtifact;
      }
      ArtifactId realArtifactId =
         artifactResolver.resolve(roughArtifact, transaction.getBranch(), realParent, destinationArtifact);
      unmatchedArtifacts.remove(realArtifactId);

      for (RoughArtifact childRoughArtifact : roughArtifact.getDescendants()) {
         ArtifactId childArtifact = createArtifact(childRoughArtifact, realArtifactId);
         if (areValid(realArtifactId, childArtifact)) {
            replaceParent(childArtifact, realArtifactId);
         }
         extractor.artifactCreated(transaction, childArtifact, childRoughArtifact);
      }
      return realArtifactId;
   }

   private void replaceParent(ArtifactId child, ArtifactId parent) {

      if (hasDifferentParent(child, parent)) {
         if (child instanceof ArtifactReadable) {
            transaction.unrelate(((ArtifactReadable) child).getParent(), CoreRelationTypes.Default_Hierarchical__Child,
               child);
         }
      }
      transaction.relate(parent, CoreRelationTypes.Default_Hierarchical__Child, child, importArtifactOrder);
   }

   private boolean hasDifferentParent(ArtifactId art, ArtifactId parent) {
      ArtifactReadable knownParent;
      if (parent instanceof ArtifactReadable) {
         knownParent = ((ArtifactReadable) art).getParent();
      } else {
         throw new OseeCoreException("Failed artifact creation on rough to real operation");
      }
      return knownParent != null && knownParent.notEqual(parent);

   }

   private boolean isValid(ArtifactId art) {
      return art != null;
   }

   private boolean areValid(ArtifactId... artifacts) {
      boolean returnValue = true;
      for (ArtifactId art : artifacts) {
         returnValue &= isValid(art);
      }
      return returnValue;
   }

   private void createRelation(RoughRelation roughRelation) {
      RelationTypeToken relationType = getRelationType(roughRelation.getRelationTypeName());

      ArtifactReadable aArt = orcsApi.getQueryFactory().fromBranch(transaction.getBranch()).andGuid(
         roughRelation.getAartifactGuid()).getArtifact();
      ArtifactReadable bArt = orcsApi.getQueryFactory().fromBranch(transaction.getBranch()).andGuid(
         roughRelation.getBartifactGuid()).getArtifact();

      if (aArt == null || bArt == null) {
         activityLog.getDebugLogger().warn("The relation of type %s could not be created.",
            roughRelation.getRelationTypeName());

         if (aArt == null) {
            activityLog.getDebugLogger().warn("The artifact with guid: %s does not exist.",
               roughRelation.getAartifactGuid());
         }
         if (bArt == null) {
            activityLog.getDebugLogger().warn("The artifact with guid: %s does not exist.",
               roughRelation.getBartifactGuid());
         }
      } else {
         try {
            transaction.relate(aArt, relationType, bArt, roughRelation.getRationale(), importArtifactOrder);
         } catch (IllegalArgumentException ex) {
            activityLog.createThrowableEntry(CoreActivityTypes.OSEE_ERROR, ex);
         }
      }
   }

   private RelationTypeToken getRelationType(String relationName) {
      for (RelationTypeToken type : orcsApi.getOrcsTypes().getRelationTypes().getAll()) {
         if (type.getName().equals(relationName)) {
            return type;
         }
      }
      return null;
   }

   public boolean isAddRelation() {
      return addRelation;
   }

   public void setAddRelation(boolean addRelation) {
      this.addRelation = addRelation;
   }

   public Collection<ArtifactId> getCreatedArtifacts() {
      return createdArtifacts;
   }
}
