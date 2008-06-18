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

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.exception.OseeDataStoreException;

/**
 * @author Donald G. Dunne
 */
public class StaticIdQuery {

   public static String STATIC_ID_ATTRIBUTE = "Static Id";

   @SuppressWarnings("unchecked")
   public static Set<Artifact> getArtifacts(String artifactTypeName, String staticId, Branch branch) throws OseeCoreException {
      try {
         Set<Artifact> artifacts = new HashSet<Artifact>();
         // Retrieve cached artifacts first
         for (Artifact artifact : ArtifactCache.getArtifactsByStaticId(staticId, branch)) {
            if (artifact.getArtifactTypeName().equals(artifactTypeName)) artifacts.add(artifact);
         }
         if (artifacts.size() > 0) {
            OseeLog.log(SkynetActivator.class, Level.FINE,
                  "StaticId Load: [" + staticId + "][" + artifactTypeName + "]");
         }

         // Retrieve database artifacts if cache has none
         artifacts.addAll(ArtifactQuery.getArtifactsFromTypeAndAttribute(artifactTypeName, STATIC_ID_ATTRIBUTE,
               staticId, branch));

         // Store results in cache
         for (Artifact artifact : artifacts) {
            ArtifactCache.cache(artifact);
         }
         return artifacts;
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   public static Artifact getSingletonArtifactOrException(String artifactType, String staticId, Branch branch) throws OseeCoreException {
      Set<Artifact> artifacts = getArtifacts(artifactType, staticId, branch);
      // Exception on problems
      if (artifacts.size() == 0)
         throw new IllegalArgumentException("Can't find requested artifact \"" + staticId + "\"");
      else if (artifacts.size() > 1) throw new IllegalArgumentException(
            "Expected 1 \"" + staticId + "\" artifact, retrieved " + artifacts.size());
      return artifacts.iterator().next();
   }

   public static Artifact getSingletonArtifact(String artifactType, String staticId, Branch branch) throws OseeCoreException {
      Set<Artifact> artifacts = getArtifacts(artifactType, staticId, branch);
      if (artifacts.size() == 1) return artifacts.iterator().next();
      return null;
   }
}