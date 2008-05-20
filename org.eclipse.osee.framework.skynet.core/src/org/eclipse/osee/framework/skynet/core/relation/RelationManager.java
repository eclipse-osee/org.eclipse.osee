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
package org.eclipse.osee.framework.skynet.core.relation;

import java.util.List;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;

/**
 * @author Ryan D. Brooks
 */
public class RelationManager {
   // the branch is accounted for because Artifact.equals 
   private static final CompositeKeyHashMap<Artifact, RelationType, List<RelationLink>> relations =
         new CompositeKeyHashMap<Artifact, RelationType, List<RelationLink>>(1024);

   private static RelationLink getLoadedRelation(Artifact artifact, int aArtifactId, int bArtifactId, RelationType relationType) {
      List<RelationLink> selectedRelations = relations.get(artifact, relationType);
      if (selectedRelations != null) {
         for (RelationLink relation : selectedRelations) {
            if (relation.artAId == aArtifactId && relation.artBId == bArtifactId) {
               return relation;
            }
         }
      }
      return null;
   }

   public static RelationLink loadRelation(RelationType relationType, int aArtifactId, int bArtifactId, Branch branch) {
      Artifact artifactA = ArtifactCache.get(aArtifactId, branch);
      Artifact artifactB = ArtifactCache.get(bArtifactId, branch);

      RelationLink relation = null;
      if (artifactA != null) {
         relation = getLoadedRelation(artifactA, aArtifactId, bArtifactId, relationType);
      }
      if (artifactB != null && relation == null) {
         relation = getLoadedRelation(artifactB, aArtifactId, bArtifactId, relationType);
      }
      return relation;
   }
}
