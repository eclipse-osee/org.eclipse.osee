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

import java.sql.SQLException;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;

/**
 * @author Robert A. Fisher
 */
public class ArtifactNameDescriptorResolver implements IArtifactNameDescriptorResolver {
   private static final ArtifactPersistenceManager artifactManager = ArtifactPersistenceManager.getInstance();
   private Branch branch;

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
      } catch (SQLException ex) {
         SkynetActivator.getLogger().log(Level.SEVERE, ex.toString(), ex);
      }
      return new Pair<String, ArtifactType>(artifact.getDescriptiveName(), artifact.getArtifactType());
   }
}
