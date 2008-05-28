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
package org.eclipse.osee.framework.ui.skynet.blam;

import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;

/**
 * @author Ryan D. Brooks
 */
public class BlamFactory extends ArtifactFactory {
   private static BlamFactory factory = null;

   private BlamFactory(int factoryId) {
      super(factoryId);
   }

   public static BlamFactory getInstance(int factoryId) {
      if (factory == null) {
         factory = new BlamFactory(factoryId);
      }
      return factory;
   }

   public static BlamFactory getInstance() {
      return factory;
   }

   public @Override
   Artifact getArtifactInstance(String guid, String humandReadableId, String factoryKey, Branch branch, ArtifactType artifactType) {
      if (factoryKey.equals(BlamWorkflow.ARTIFACT_NAME)) {
         return new BlamWorkflow(this, guid, humandReadableId, branch, artifactType);
      }
      throw new IllegalArgumentException("did not recognize the factory key: " + factoryKey);
   }
}