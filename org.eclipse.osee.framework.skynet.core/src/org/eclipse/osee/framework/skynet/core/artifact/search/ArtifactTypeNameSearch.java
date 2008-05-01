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
import java.util.logging.Level;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;

/**
 * @author Donald G. Dunne
 */
public class ArtifactTypeNameSearch {

   private final String artifactTypeName;
   private final String artifactName;
   private final Branch branch;

   /**
    * Search for given artifactType with active attribute set as specified
    * 
    * @param artifactType type name or null for all types
    * @param artifactName string portion of name to search
    * @param branch branch to search on
    */
   public ArtifactTypeNameSearch(String artifactType, String artifactName, Branch branch) {
      this.artifactTypeName = artifactType;
      this.artifactName = artifactName;
      this.branch = branch;
   }

   @SuppressWarnings("unchecked")
   public <A extends Artifact> Set<A> getArtifacts(Class<A> clazz) {
      Set<A> results = new HashSet<A>();

      try {
         Collection<Artifact> arts = ArtifactQuery.getArtifactsFromTypeAndName(artifactTypeName, artifactName, branch);
         for (Artifact art : arts)
            results.add((A) art);
      } catch (SQLException ex) {
         SkynetActivator.getLogger().log(Level.SEVERE, ex.toString(), ex);
      }
      return results;
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
