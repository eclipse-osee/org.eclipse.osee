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

package org.eclipse.osee.framework.ui.skynet.render;

import java.sql.SQLException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.WorkspaceURL;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;

/**
 * @author Ryan D. Brooks
 */
public class UrlRenderer extends Renderer {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(UrlRenderer.class);
   private static final ConfigurationPersistenceManager configurationPersistenceManager =
         ConfigurationPersistenceManager.getInstance();
   private Collection<ArtifactSubtypeDescriptor> descriptors;

   /**
    * @param applicableArtifactTypes
    */
   public UrlRenderer() {
      try {
         descriptors =
               configurationPersistenceManager.getArtifactSubtypeDescriptorsForAttribute(configurationPersistenceManager.getDynamicAttributeType(
                     "Content URL", BranchPersistenceManager.getInstance().getCommonBranch()));
      } catch (SQLException ex) {
         logger.log(Level.SEVERE, "", ex);
      }
   }

   @Override
   public String getArtifactUrl(Artifact artifact) {
      String url = artifact.getSoleAttributeValue("Content URL");
      if (url.startsWith("ws:")) {
         IFile iFile = WorkspaceURL.getIFile(url);
         url = iFile.getLocation().toString();
      }
      return url;
   }

   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact) {
      for (ArtifactSubtypeDescriptor descriptor : descriptors) {
         if (descriptor.canProduceArtifact(artifact)) {
            return SUBTYPE_TYPE_MATCH;
         }
      }
      return NO_MATCH;
   }
}
