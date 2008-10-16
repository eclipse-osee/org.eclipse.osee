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
package org.eclipse.osee.framework.skynet.core.revision;

import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Robert A. Fisher
 */
public class ArtifactNameDescriptorResolver implements IArtifactNameDescriptorResolver {
   private final Branch branch;

   /**
    * @param branch
    */
   public ArtifactNameDescriptorResolver(Branch branch) {
      super();
      this.branch = branch;
   }

   public Pair<String, ArtifactType> get(Integer artId) {
      Artifact artifact = null;
      try {
         artifact = ArtifactQuery.getArtifactFromId(artId, branch);
      } catch (OseeCoreException ex) {
         return new Pair<String, ArtifactType>("", null);
      }
      return new Pair<String, ArtifactType>(artifact.getDescriptiveName(), artifact.getArtifactType());
   }
}
