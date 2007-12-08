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
package org.eclipse.osee.framework.skynet.core.artifact.factory;

import java.sql.SQLException;
import org.eclipse.osee.framework.skynet.core.artifact.BasicArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;

/**
 * @author Ryan D. Brooks
 */
public class BasicArtifactFactory extends ArtifactFactory<BasicArtifact> {
   private static BasicArtifactFactory factory = null;

   private BasicArtifactFactory(int factoryId) {
      super(factoryId);
   }

   public static BasicArtifactFactory getInstance(int factoryId) {
      if (factory == null) {
         factory = new BasicArtifactFactory(factoryId);
      }
      return factory;
   }

   public static BasicArtifactFactory getInstance() {
      return factory;
   }

   public @Override
   BasicArtifact getNewArtifact(String guid, String humandReadableId, String factoryKey, Branch branch) throws SQLException {
      return new BasicArtifact(this, guid, humandReadableId, branch);
   }
}