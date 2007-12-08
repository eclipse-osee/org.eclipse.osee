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

import java.sql.SQLException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.factory.ArtifactFactory;

/**
 * @author Ryan D. Brooks
 */
public class BlamFactory extends ArtifactFactory<Artifact> {
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
   Artifact getNewArtifact(String guid, String humandReadableId, String factoryKey, Branch branch) throws SQLException {
      if (factoryKey.equals(BlamWorkflow.ARTIFACT_NAME)) {
         return new BlamWorkflow(this, guid, humandReadableId, branch);
      }
      throw new IllegalArgumentException("did not recognize the factory key: " + factoryKey);
   }
}