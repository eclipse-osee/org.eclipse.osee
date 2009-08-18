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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Donald G. Dunne
 */
public class ActiveArtifactTypeSearch {

   /**
    * Search for given artifactType with active attribute set as specified
    * 
    * @param artifactTypeName
    * @param active
    * @param branch
    */
   @SuppressWarnings("unchecked")
   public static <A extends Artifact> Set<A> getArtifacts(String artifactTypeName, Active active, Branch branch, Class<A> clazz) {
      Set<A> results = new HashSet<A>();

      try {
         Collection<Artifact> arts = null;
         if (active == Active.Both) {
            // Since both, just do a type search
            arts = ArtifactQuery.getArtifactListFromType(artifactTypeName, branch);
         } else {
            arts =
                  ArtifactQuery.getArtifactListFromTypeAndAttribute(artifactTypeName, "ats.Active",
                        active == Active.Active ? "yes" : "no", branch);
         }
         for (Artifact art : arts) {
            results.add((A) art);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return results;
   }
}