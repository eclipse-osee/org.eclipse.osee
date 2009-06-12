/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet;

import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.swt.graphics.Image;

/**
 * @author Ryan D. Brooks
 */
public abstract class ArtifactImageProvider {
   public abstract Image getImage(Artifact artifact) throws OseeCoreException;

   public Image getImage(ArtifactType artifactType) throws OseeCoreException {
      return ImageManager.getImage(BaseImage.getBaseImageEnum(artifactType));
   }

   public abstract void init() throws OseeCoreException;
}