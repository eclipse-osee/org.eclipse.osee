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
import java.util.Set;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;

/**
 * @author Donald G. Dunne
 */
public class ArtifactStaticIdSearch {

   public static String STATIC_ID_ATTRIBUTE = "Static Id";
   private final String artifactTypeName;
   private final String artifactName;
   private final Branch branch;

   /**
    * Search for given artifactType with active attribute set as specified
    * 
    * @param artifactTypeName type name
    * @param staticId string portion of name to search
    * @param branch branch to search on
    */
   public ArtifactStaticIdSearch(String artifactTypeName, String staticId, Branch branch) {
      this.artifactTypeName = artifactTypeName;
      this.artifactName = staticId;
      this.branch = branch;
   }

   @SuppressWarnings("unchecked")
   public <A extends Artifact> Set<A> getArtifacts(Class<A> clazz) throws SQLException {
      Set<A> results = new HashSet<A>();
      Collection<Artifact> arts =
            ArtifactQuery.getArtifactsFromTypeAndAttribute(artifactTypeName, STATIC_ID_ATTRIBUTE, artifactName, branch);
      for (Artifact art : arts) {
         results.add((A) art);
      }
      return results;
   }

   public static <A extends Artifact> A getSingletonArtifactOrException(String artifactType, String staticId, Branch branch, Class<A> clazz) throws SQLException {
      return (new ArtifactStaticIdSearch(artifactType, staticId, branch)).getSingletonArtifactOrException(clazz);
   }

   public static <A extends Artifact> A getSingletonArtifact(String artifactType, String staticId, Branch branch, Class<A> clazz) throws SQLException {
      return (new ArtifactStaticIdSearch(artifactType, staticId, branch)).getSingletonArtifact(clazz);
   }

   public <A extends Artifact> A getSingletonArtifactOrException(Class<A> clazz) throws SQLException {
      Collection<A> arts = getArtifacts(clazz);
      if (arts.size() == 0)
         throw new IllegalArgumentException("Can't find requested artifact \"" + artifactName + "\"");
      else if (arts.size() > 1) throw new IllegalArgumentException(
            "Expected 1 \"" + artifactName + "\" artifact, retrieved " + arts.size());
      return arts.iterator().next();
   }

   public <A extends Artifact> A getSingletonArtifact(Class<A> clazz) throws SQLException {
      Collection<A> arts = getArtifacts(clazz);
      if (arts.size() == 1) return arts.iterator().next();
      return null;
   }
}