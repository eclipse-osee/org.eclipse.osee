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

package org.eclipse.osee.framework.skynet.core.artifact.search;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;

/**
 * @author Donald G. Dunne
 */
public class ArtifactStaticIdSearch {

   public static String STATIC_ID_ATTRIBUTE = "Static Id";
   private final String artifactType;
   private final String artifactName;
   private final Branch branch;
   private final SearchOperator operator;
   public static enum SearchOperator {
      LIKE, EQUAL
   };

   /**
    * Search for given artifactType with active attribute set as specified
    * 
    * @param artifactType type name or null for all types
    * @param staticId string portion of name to search
    * @param branch branch to search on
    */
   public ArtifactStaticIdSearch(String artifactType, String staticId, Branch branch) {
      this(artifactType, staticId, branch, SearchOperator.EQUAL);
   }

   /**
    * Search for given artifactType with active attribute set as specified
    * 
    * @param artifactType type name or null for all types
    * @param staticId string portion of name to search
    * @param branch branch to search on
    * @param operator LIKE for portion of string or EQUAL for exact match
    */
   public ArtifactStaticIdSearch(String artifactType, String staticId, Branch branch, SearchOperator operator) {
      super();
      this.artifactType = artifactType;
      this.artifactName = staticId;
      this.branch = branch;
      this.operator = operator;
   }

   public static <A extends Artifact> Set<A> getArtifacts(String artifactType, String staticId, Branch branch, SearchOperator operator, Class<A> clazz) {
      return (new ArtifactStaticIdSearch(artifactType, staticId, branch, operator)).getArtifacts(clazz);
   }

   @SuppressWarnings("unchecked")
   public <A extends Artifact> Set<A> getArtifacts(Class<A> clazz) {
      Set<A> results = new HashSet<A>();
      List<ISearchPrimitive> activeCriteria = new LinkedList<ISearchPrimitive>();
      if (artifactType != null) activeCriteria.add(new ArtifactTypeSearch(artifactType, Operator.EQUAL));
      if (operator == SearchOperator.EQUAL)
         activeCriteria.add(new AttributeValueSearch(STATIC_ID_ATTRIBUTE, artifactName, Operator.EQUAL));
      else
         activeCriteria.add(new AttributeValueSearch(STATIC_ID_ATTRIBUTE, "%" + artifactName + "%", Operator.LIKE));
      try {
         Collection<Artifact> arts =
               ArtifactPersistenceManager.getInstance().getArtifacts(activeCriteria, true, branch);
         for (Artifact art : arts)
            results.add((A) art);
      } catch (SQLException ex) {
         SkynetActivator.getLogger().log(Level.SEVERE, ex.toString(), ex);
      }
      return results;
   }

   public static <A extends Artifact> A getSingletonArtifactOrException(String artifactType, String staticId, Branch branch, SearchOperator operator, Class<A> clazz) {
      return (new ArtifactStaticIdSearch(artifactType, staticId, branch, operator)).getSingletonArtifactOrException(clazz);
   }

   public static <A extends Artifact> A getSingletonArtifact(String artifactType, String staticId, Branch branch, SearchOperator operator, Class<A> clazz) {
      return (new ArtifactStaticIdSearch(artifactType, staticId, branch, operator)).getSingletonArtifact(clazz);
   }

   public <A extends Artifact> A getSingletonArtifactOrException(Class<A> clazz) {
      Collection<A> arts = getArtifacts(clazz);
      if (arts.size() == 0)
         throw new IllegalArgumentException("Can't find requested artifact \"" + artifactName + "\"");
      else if (arts.size() > 1) throw new IllegalArgumentException(
            "Expected 1 \"" + artifactName + "\" artifact, retrieved " + arts.size());
      return arts.iterator().next();
   }

   public <A extends Artifact> A getSingletonArtifact(Class<A> clazz) {
      Collection<A> arts = getArtifacts(clazz);
      if (arts.size() == 1) return arts.iterator().next();
      return null;
   }

}
