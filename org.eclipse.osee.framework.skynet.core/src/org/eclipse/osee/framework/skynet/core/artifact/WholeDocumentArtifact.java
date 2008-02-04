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
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.factory.IArtifactFactory;
import org.eclipse.swt.graphics.Image;

/**
 * @author Paul K Waldfogel Artifact type used to indicate that this artifact is to be rendered by Microsoft Word as a
 *         complete document with 1..n sections..
 */
public class WholeDocumentArtifact extends WordArtifact {
   public static final String ARTIFACT_NAME = "Whole Document Artifact";

   private static final SkynetActivator plugin = SkynetActivator.getInstance();

   /**
    * @param parentFactory
    * @param guid
    * @param branch
    * @throws SQLException
    */
   public WholeDocumentArtifact(IArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch) throws SQLException {
      super(parentFactory, guid, humanReadableId, branch);
   }

   @Override
   public Image getImage() {
      Image image = plugin.getImageForProgram(getExtension());
      if (image == null) {
         image = plugin.getImage("laser_16_16.gif");
      }

      return image;
   }

   public String getFileName() {
      return getDescriptiveName() + "." + getExtension();
   }

   public String getExtension() {
      return getSoleAttributeValue("Extension");
   }

}