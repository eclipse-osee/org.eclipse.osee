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
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;

/**
 * @author Ryan D. Brooks
 */
public class PolymorphicArtifactFactory extends ArtifactFactory<BasicArtifact> {
   private static PolymorphicArtifactFactory factory = null;

   private PolymorphicArtifactFactory(int factoryId) {
      super(factoryId);
   }

   /**
    * is called by the ArtifactFactoryCache with the factory id that was read from the database along with all the other
    * factory ids with a single query
    * 
    * @param factoryId
    */
   public static PolymorphicArtifactFactory getInstance(int factoryId) {
      if (factory == null) {
         factory = new PolymorphicArtifactFactory(factoryId);
      }
      return factory;
   }

   public static PolymorphicArtifactFactory getInstance() {
      return factory;
   }

   public @Override
   BasicArtifact getNewArtifact(String guid, String humandReadableId, String factoryKey, Branch branch) throws SQLException {
      return new BasicArtifact(this, guid, humandReadableId, branch);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.artifact.factory.ArtifactFactory#compatibleWith(org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor)
    */
   @Override
   protected boolean compatibleWith(ArtifactSubtypeDescriptor descriptor) {
      return true;
   }
}