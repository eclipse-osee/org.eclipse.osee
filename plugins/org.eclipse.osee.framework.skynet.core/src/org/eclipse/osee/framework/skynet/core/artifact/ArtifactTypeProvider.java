/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.artifact;

import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;

/**
 * @author Donald G. Dunne
 */
public class ArtifactTypeProvider implements IArtifactTypeProvider {

   private final ArtifactTypeProvider provider;

   public ArtifactTypeProvider() {
      provider = this;
   }

   @Override
   public ArtifactTypeToken getTypeByGuid(Long artTypeGuid) {
      return ArtifactTypeManager.getType(artTypeGuid);
   }

   @Override
   public boolean inheritsFrom(ArtifactTypeId artifactType, ArtifactTypeId... parentTypes) {
      return ArtifactTypeManager.inheritsFrom(artifactType, parentTypes);
   }

   public ArtifactTypeProvider getProvider() {
      return provider;
   }

}
