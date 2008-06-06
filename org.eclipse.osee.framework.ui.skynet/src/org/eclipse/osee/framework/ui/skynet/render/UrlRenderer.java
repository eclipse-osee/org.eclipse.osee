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
import org.eclipse.core.resources.IFile;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.WorkspaceURL;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.exception.MultipleAttributesExist;

/**
 * @author Ryan D. Brooks
 */
public class UrlRenderer extends Renderer {
   private Collection<ArtifactType> descriptors;

   /**
    * @param applicableArtifactTypes
    * @throws SQLException
    */
   public UrlRenderer() throws SQLException {
      descriptors =
            ConfigurationPersistenceManager.getArtifactTypesFromAttributeType(AttributeTypeManager.getType("Content URL"));
   }

   @Override
   public String getArtifactUrl(Artifact artifact) throws MultipleAttributesExist, SQLException {
      String url = artifact.getSoleAttributeValue("Content URL", "");
      if (url.startsWith("ws:")) {
         IFile iFile = WorkspaceURL.getIFile(url);
         url = iFile.getLocation().toString();
      }
      return url;
   }

   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact) {
      for (ArtifactType descriptor : descriptors) {
         if (descriptor.canProduceArtifact(artifact)) {
            return SUBTYPE_TYPE_MATCH;
         }
      }
      return NO_MATCH;
   }
}
