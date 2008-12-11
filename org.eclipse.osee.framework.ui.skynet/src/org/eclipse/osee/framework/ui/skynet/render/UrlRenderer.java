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

import org.eclipse.core.resources.IFile;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.WorkspaceURL;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;

/**
 * @author Ryan D. Brooks
 */
public class UrlRenderer extends DefaultArtifactRenderer {
   private final AttributeType contentUrlType;

   /**
    * @param applicableArtifactTypes
    * @throws OseeTypeDoesNotExist
    * @throws OseeDataStoreException
    */
   public UrlRenderer(String rendererId) throws OseeDataStoreException, OseeTypeDoesNotExist {
      super(rendererId);
      contentUrlType = AttributeTypeManager.getType("Content URL");
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#newInstance()
    */
   @Override
   public UrlRenderer newInstance() throws OseeCoreException {
      return new UrlRenderer(getId());
   }

   @Override
   public String getArtifactUrl(Artifact artifact) throws OseeCoreException {
      String url = artifact.getSoleAttributeValue("Content URL", "");
      if (url.startsWith("ws:")) {
         IFile iFile = WorkspaceURL.getIFile(url);
         url = iFile.getLocation().toString();
      }
      return url;
   }

   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact) throws OseeCoreException {
      for (AttributeType attributeType : artifact.getAttributeTypes()) {
         if (attributeType.equals(contentUrlType)) {
            return SUBTYPE_TYPE_MATCH;
         }
      }
      return NO_MATCH;
   }
}
