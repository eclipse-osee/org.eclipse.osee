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
package org.eclipse.osee.display.api.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Shawn F. Cook
 */
public class Artifact {
   private final String guid;
   private final String artifactName;
   private final String artifactType;
   private final Map<RelationType, Collection<Artifact>> relations = new HashMap<RelationType, Collection<Artifact>>();

   public Artifact(String guid, String artifactName, String artifactType, Map<RelationType, Collection<Artifact>> relations) {
      this.guid = guid;
      this.artifactName = artifactName;
      this.artifactType = artifactType;
      if (relations != null) {
         this.relations.putAll(relations);
      }
   }

   public String getArtifactName() {
      return artifactName;
   }

   public String getArtifactType() {
      return artifactType;
   }

   public Map<RelationType, Collection<Artifact>> getRelations() {
      return relations;
   }

   public Collection<RelationType> getRelationTypes() {
      return relations.keySet();
   }

   public Collection<Artifact> getRelationsWithRelationType(RelationType relationType) {
      return relations.get(relationType);
   }

   public String getGuid() {
      return guid;
   }

   public Artifact getParent() {
      Collection<Artifact> listOfParents = relations.get(RelationType.PARENT);
      if (listOfParents == null || listOfParents.size() <= 0) {
         return null;
      }
      return listOfParents.iterator().next();
   }

   /*
    * Returns list of ancestor Artifacts or empty list if there are no ancestors (i.e.: parent is null).
    */
   public Collection<Artifact> getAncestry() {
      Collection<Artifact> ancestry = new ArrayList<Artifact>();
      Artifact parent = this.getParent();
      if (parent != null) {
         ancestry.addAll(parent.getAncestry());
         ancestry.add(parent);
      }
      return ancestry;
   }

   @Override
   public String toString() {
      return artifactName;
   }
}
