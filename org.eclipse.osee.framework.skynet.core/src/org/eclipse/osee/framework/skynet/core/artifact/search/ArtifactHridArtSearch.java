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
public class ArtifactHridArtSearch {

   private final Collection<String> hrids;
   private final Branch branch;

   /**
    * Search for given artifactType with active attribute set as specified
    * 
    * @param branch TODO
    */
   public ArtifactHridArtSearch(Collection<String> hrids, Branch branch) {
      super();
      this.hrids = hrids;
      this.branch = branch;
   }

   @SuppressWarnings("unchecked")
   public <A extends Artifact> Set<A> getArtifacts(Class<A> clazz) {
      Set<A> results = new HashSet<A>();
      List<ISearchPrimitive> activeCriteria = new LinkedList<ISearchPrimitive>();
      for (String hrid : hrids)
         activeCriteria.add(new org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactHridSearch(hrid));
      try {
         Collection<Artifact> arts =
               ArtifactPersistenceManager.getInstance().getArtifacts(activeCriteria, false, branch);
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
         throw new IllegalArgumentException("Can't find requested artifact \"" + hrids.iterator().next() + "\"");
      else if (arts.size() > 1) throw new IllegalArgumentException(
            "Expected 1 \"" + hrids.iterator().next() + "\" artifact, retrieved " + arts.size());
      return arts.iterator().next();
   }

}
