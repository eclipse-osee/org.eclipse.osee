/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactNameComparator;
import org.eclipse.osee.framework.skynet.core.relation.IRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;

/**
 * @author Ryan D. Brooks
 */
public class ComputeLegacyOrder {
   private final List<Artifact> legacyOrder = new ArrayList<Artifact>();
   private final ArtifactNameComparator nameComparator = new ArtifactNameComparator();
   private List<RelationLink> relations;
   private final List<Artifact> detachedArtifacts = new ArrayList<Artifact>();
   private final List<Artifact> candidates = new ArrayList<Artifact>();

   public List<Artifact> getOrginalOrder(Artifact artifact, IRelationEnumeration relationEnum) throws OseeCoreException {
      legacyOrder.clear();
      detachedArtifacts.clear();

      relations = artifact.getRelations(relationEnum);
      assertRelationState(relations);

      if (relations.isEmpty()) {
         throw new OseeStateException("is empty");
         //return legacyOrder;
      }

      gatherDetachedArtifacts();
      determineLegacyOrder();

      return legacyOrder;
   }

   private void determineLegacyOrder() throws OseeCoreException {
      int artifactIdToFind = -1;

      while (true) {
         candidates.clear();
         for (RelationLink relation : relations) {
            if (relation.getBOrder() == artifactIdToFind) {
               candidates.add(relation.getArtifactB());
            }
         }

         Artifact selectedArtifact;
         if (candidates.size() == 0) {
            if (detachedArtifacts.isEmpty()) {
               return;
            } else {
               selectedArtifact = detachedArtifacts.remove(0);
            }
         } else {
            if (candidates.size() > 1) {
               Collections.sort(candidates, nameComparator);
               detachedArtifacts.addAll(candidates);
            }
            selectedArtifact = candidates.get(0);
            detachedArtifacts.remove(selectedArtifact);
         }

         legacyOrder.add(selectedArtifact);
         artifactIdToFind = selectedArtifact.getArtId();
      }
   }

   private void gatherDetachedArtifacts() throws OseeCoreException {
      for (RelationLink relation : relations) {
         if (relation.getBOrder() != -1 && !previousArtifactExists(relation.getBOrder())) {
            detachedArtifacts.add(relation.getArtifactB());
         }
      }
      if (detachedArtifacts.size() > 1) {
         Collections.sort(detachedArtifacts, nameComparator);
      }
   }

   private boolean previousArtifactExists(int artifactIdToFind) {
      for (RelationLink relation : relations) {
         if (relation.getBArtifactId() == artifactIdToFind) {
            return true;
         }
      }
      return false;
   }

   private void assertRelationState(List<RelationLink> relations) throws OseeCoreException {
      for (RelationLink relation : relations) {
         if (relation.isDeleted() || relation.getArtifactA().isDeleted() || relation.getArtifactB().isDeleted()) {
            throw new OseeStateException("is deleted");
         }
      }

   }
}