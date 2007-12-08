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
import java.util.logging.Logger;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;

/**
 * @author Robert A. Fisher
 */
public class ArtifactNameDescriptorResolver implements IArtifactNameDescriptorResolver {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(ArtifactNameDescriptorResolver.class);
   private static final ArtifactPersistenceManager artifactManager = ArtifactPersistenceManager.getInstance();
   private Branch branch;

   /**
    * @param branch
    */
   public ArtifactNameDescriptorResolver(Branch branch) {
      super();
      this.branch = branch;
   }

   public Pair<String, ArtifactSubtypeDescriptor> get(Integer artId) {
      Artifact artifact = null;
      try {
         artifact = artifactManager.getArtifactFromId(artId, branch);
      } catch (IllegalArgumentException ex) {
         return new Pair<String, ArtifactSubtypeDescriptor>("", null);
      } catch (SQLException ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
      }
      return new Pair<String, ArtifactSubtypeDescriptor>(artifact.getDescriptiveName(), artifact.getDescriptor());
   }
}
