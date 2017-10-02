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
package org.eclipse.osee.framework.skynet.core.importing.operations;

import static org.eclipse.osee.framework.core.enums.RelationSorter.USER_DEFINED;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;
import org.eclipse.osee.framework.skynet.core.importing.RoughRelation;
import org.eclipse.osee.framework.skynet.core.importing.parsers.IArtifactExtractor;
import org.eclipse.osee.framework.skynet.core.importing.resolvers.IArtifactImportResolver;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

/**
 * @author Roberto E. Escobar
 */
public class RoughToRealArtifactOperation extends AbstractOperation {
   private final SkynetTransaction transaction;
   private final RoughArtifactCollector rawData;
   private final IArtifactImportResolver artifactResolver;
   private final Map<RoughArtifact, Artifact> roughToRealArtifact;
   private final Collection<Artifact> createdArtifacts;
   private final Artifact destinationArtifact;
   private final RelationSorter importArtifactOrder;
   private final boolean deleteUnmatchedArtifacts;
   private Collection<Artifact> unmatchedArtifacts;
   private final IArtifactExtractor extractor;
   private boolean addRelation = true;

   public RoughToRealArtifactOperation(SkynetTransaction transaction, Artifact destinationArtifact, RoughArtifactCollector rawData, IArtifactImportResolver artifactResolver, boolean deleteUnmatchedArtifacts, IArtifactExtractor extractor) {
      super("Materialize Artifacts", Activator.PLUGIN_ID);
      this.rawData = rawData;
      this.transaction = transaction;
      this.artifactResolver = artifactResolver;
      this.destinationArtifact = destinationArtifact;
      this.importArtifactOrder = USER_DEFINED;
      this.roughToRealArtifact = new HashMap<>();
      this.createdArtifacts = new LinkedList<>();
      this.deleteUnmatchedArtifacts = deleteUnmatchedArtifacts;
      this.extractor = extractor;
      roughToRealArtifact.put(rawData.getParentRoughArtifact(), this.destinationArtifact);
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      if (monitor != null) {
         monitor.setTaskName("Creating Artifacts");
      }
      this.unmatchedArtifacts = destinationArtifact.getDescendants();
      int totalItems = rawData.getRoughArtifacts().size() + rawData.getRoughRelations().size();
      int unitOfWork = calculateWork(1.0 / totalItems);

      for (RoughArtifact roughArtifact : rawData.getParentRoughArtifact().getChildren()) {
         Artifact child = createArtifact(monitor, roughArtifact, destinationArtifact);
         createdArtifacts.add(child);
         if (addRelation && child != null && !child.hasParent()) {
            destinationArtifact.addChild(importArtifactOrder, child);
         }
         if (monitor != null) {
            monitor.worked(unitOfWork);
         }

         if (monitor != null) {
            monitor.setTaskName("Creating Relations");
         }
         for (RoughRelation roughRelation : rawData.getRoughRelations()) {
            createRelation(monitor, roughRelation);
            if (monitor != null) {
               monitor.worked(unitOfWork);
            }
         }
      }

      if (deleteUnmatchedArtifacts) {
         for (Artifact toDelete : unmatchedArtifacts) {
            toDelete.deleteAndPersist(transaction);
         }
      }
   }

   private Artifact createArtifact(IProgressMonitor monitor, RoughArtifact roughArtifact, Artifact realParent) {
      Artifact realArtifact = roughToRealArtifact.get(roughArtifact);
      if (realArtifact != null) {
         return realArtifact;
      }

      realArtifact = artifactResolver.resolve(roughArtifact, transaction.getBranch(), realParent, destinationArtifact);
      unmatchedArtifacts.remove(realArtifact);

      for (RoughArtifact childRoughArtifact : roughArtifact.getDescendants()) {
         Artifact childArtifact = createArtifact(monitor, childRoughArtifact, realArtifact);
         if (areValid(realArtifact, childArtifact)) {
            removeOtherParent(childArtifact, realArtifact);
            if (!childArtifact.hasParent()) {
               realArtifact.addChild(importArtifactOrder, childArtifact);
            }
         }
         if (extractor != null && extractor.artifactCreated(childArtifact, childRoughArtifact)) {
            childArtifact.persist(transaction);
         }
      }

      if (realArtifact != null) {
         realArtifact.persist(transaction);
      }
      return realArtifact;
   }

   private void removeOtherParent(Artifact child, Artifact parent) {
      if (hasDifferentParent(child, parent)) {
         child.deleteRelations(CoreRelationTypes.Default_Hierarchical__Parent);
         child.persist(transaction);
      }
   }

   private boolean hasDifferentParent(Artifact art, Artifact parent) {
      return art.hasParent() && !art.getParent().equals(parent);
   }

   // TODO move these two functions into jdk.core or find existing library functions
   private boolean isValid(Artifact art) {
      return art != null && !art.isDeleted();
   }

   private boolean areValid(Artifact... artifacts) {
      boolean returnValue = true;
      for (Artifact art : artifacts) {
         returnValue &= isValid(art);
      }
      return returnValue;
   }

   private void createRelation(IProgressMonitor monitor, RoughRelation roughRelation) {
      RelationType relationType = RelationTypeManager.getType(roughRelation.getRelationTypeName());
      Artifact aArt = ArtifactQuery.getArtifactFromId(roughRelation.getAartifactGuid(), transaction.getBranch());
      Artifact bArt = ArtifactQuery.getArtifactFromId(roughRelation.getBartifactGuid(), transaction.getBranch());

      if (aArt == null || bArt == null) {
         OseeLog.log(Activator.class, Level.WARNING,
            "The relation of type " + roughRelation.getRelationTypeName() + " could not be created.");
         if (aArt == null) {
            OseeLog.log(Activator.class, Level.WARNING,
               "The artifact with guid: " + roughRelation.getAartifactGuid() + " does not exist.");
         }
         if (bArt == null) {
            OseeLog.log(Activator.class, Level.WARNING,
               "The artifact with guid: " + roughRelation.getBartifactGuid() + " does not exist.");
         }
      } else {
         try {
            if (monitor != null) {
               monitor.subTask(aArt.getName() + " <--> " + bArt.getName());
               monitor.worked(1);
            }
            RelationManager.addRelation(importArtifactOrder, relationType, aArt, bArt, roughRelation.getRationale());
            aArt.persist(transaction);
         } catch (IllegalArgumentException ex) {
            OseeLog.log(Activator.class, Level.WARNING, ex);
         }
      }
   }

   public boolean isAddRelation() {
      return addRelation;
   }

   public void setAddRelation(boolean addRelation) {
      this.addRelation = addRelation;
   }

   public Collection<Artifact> getCreatedArtifacts() {
      return createdArtifacts;
   }
}
