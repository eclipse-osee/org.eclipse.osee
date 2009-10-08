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
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;

/**
 * @author Ryan D. Brooks
 */
public class ComputeLegacyOrder {
   private final List<Artifact> legacyOrder = new ArrayList<Artifact>();
   private final ArtifactNameComparator nameComparator = new ArtifactNameComparator();
   private List<RelationLink> relations;
   private final List<Artifact> remainingArtifacts = new ArrayList<Artifact>();
   private final List<Artifact> candidates = new ArrayList<Artifact>();

   public List<Artifact> getOrginalOrder(List<RelationLink> relations) throws OseeCoreException {
      legacyOrder.clear();
      remainingArtifacts.clear();
      this.relations = relations;
      assertRelationState(relations);

      for (RelationLink relation : relations) {
         remainingArtifacts.add(relation.getArtifactB());
      }
      Collections.sort(remainingArtifacts, nameComparator);

      determineLegacyOrder();

      return legacyOrder;
   }

   private void determineLegacyOrder() throws OseeCoreException {
      int artifactIdToFind = -1;

      while (true) {
         candidates.clear();
         for (RelationLink relation : relations) {
            if (relation.getBOrder() == artifactIdToFind) {
               if (remainingArtifacts.contains(relation.getArtifactB())) {
                  candidates.add(relation.getArtifactB());
               }
            }
         }

         Artifact selectedArtifact;
         if (candidates.size() == 0) {
            if (remainingArtifacts.isEmpty()) {
               return;
            } else {
               selectedArtifact = remainingArtifacts.remove(0);
            }
         } else {
            if (candidates.size() > 1) {
               Collections.sort(candidates, nameComparator);
            }
            selectedArtifact = candidates.get(0);
            remainingArtifacts.remove(selectedArtifact);
         }

         legacyOrder.add(selectedArtifact);
         artifactIdToFind = selectedArtifact.getArtId();
      }
   }

   private void assertRelationState(List<RelationLink> relations) throws OseeCoreException {
      if (relations.isEmpty()) {
         throw new OseeStateException("is empty");
      }
      for (RelationLink relation : relations) {
         if (relation.isDeleted() || relation.getArtifactA().isDeleted() || relation.getArtifactB().isDeleted()) {
            throw new OseeStateException("is deleted");
         }
      }
   }
}