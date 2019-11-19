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
package org.eclipse.osee.framework.skynet.core.artifact;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.AccessPolicy;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

/**
 * @author Ryan D. Brooks
 * @author Robert A. Fisher
 */
public class ArtifactPersistenceManager {
   /**
    * @param transaction if the transaction is null then persist is not called
    * @param overrideDeleteCheck if <b>true</b> deletes without checking preconditions
    * @param artifacts The artifacts to delete.
    */
   public static void deleteArtifact(SkynetTransaction transaction, boolean overrideDeleteCheck, final Artifact... artifacts) {
      deleteArtifactCollection(transaction, overrideDeleteCheck, Arrays.asList(artifacts));
   }

   public static void deleteArtifactCollection(SkynetTransaction transaction, boolean overrideDeleteCheck, final Collection<Artifact> artifacts) {
      if (artifacts.isEmpty()) {
         return;
      }

      bulkLoadRelatives(artifacts);

      if (!overrideDeleteCheck) {
         performDeleteChecks(artifacts);
      }

      boolean reorderRelations = true;
      for (Artifact artifact : artifacts) {
         deleteTrace(artifact, transaction, reorderRelations);
      }
   }

   // Confirm artifacts are fit to delete
   private static void performDeleteChecks(Collection<Artifact> artifacts) {
      AccessPolicy policy = ServiceUtil.getAccessPolicy();
      XResultData results = policy.isDeleteable(Collections.castAll(artifacts), new XResultData());
      if (results.isErrors()) {
         throw new OseeStateException(results.toString());
      }
   }

   // Confirm relations are fit to delete
   public static void performDeleteRelationChecks(Artifact artifact, IRelationType relationType) {
      AccessPolicy policy = ServiceUtil.getAccessPolicy();
      XResultData results = policy.isDeleteableRelation(artifact, relationType, new XResultData());
      if (results.isErrors()) {
         throw new OseeStateException(results.toString());
      }
   }

   private static void bulkLoadRelatives(Collection<Artifact> artifacts) {
      Collection<ArtifactId> relatives = new HashSet<>();
      for (Artifact artifact : artifacts) {
         for (RelationLink link : artifact.getRelationsAll(DeletionFlag.EXCLUDE_DELETED)) {
            relatives.add(link.getArtifactIdA());
            relatives.add(link.getArtifactIdB());
         }
      }
      BranchId branch = artifacts.iterator().next().getBranch();
      ArtifactQuery.getArtifactListFrom(relatives, branch);
   }

   private static void deleteTrace(Artifact artifact, SkynetTransaction transaction, boolean reorderRelations) {
      if (!artifact.isDeleted()) {
         // This must be done first since the the actual deletion of an
         // artifact clears out the link manager
         for (Artifact childArtifact : artifact.getChildren()) {
            deleteTrace(childArtifact, transaction, false);
         }
         try {
            // calling deCache here creates a race condition when the handleRelationModifiedEvent listeners fire - RS
            //          ArtifactCache.deCache(artifact);
            artifact.internalSetDeleted();
            RelationManager.deleteRelationsAll(artifact, reorderRelations, transaction);
            if (transaction != null) {
               artifact.persist(transaction);
            }
         } catch (OseeCoreException ex) {
            artifact.resetToPreviousModType();
            throw ex;
         }
      }
   }
}