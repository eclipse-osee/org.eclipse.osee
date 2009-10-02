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
package org.eclipse.osee.framework.skynet.core.test.types;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.types.BranchCache;
import org.eclipse.osee.framework.skynet.core.types.ShallowArtifact;
import org.junit.Test;

/**
 * @author Roberto E. Escobar
 */
public class ShallowArtifactTest {

   private final BranchCache cache;

   public ShallowArtifactTest() {
      cache = null;
   }

   @Test
   public void testGetGuid() {
      // TODO add test
      //      IArtifact artifact = new ShallowArtifact(cache, 45);
      //      artifact.getArtId();
      //      artifact.getArtifactType();
      //      artifact.getBranch();
      //      artifact.getGuid();
      //      artifact.getName();
   }

   // This class is used to avoid needing a full database to run this test.
   // It purposely avoids using the getFullArtifact method from the base class
   private final class MockShallowArtifact extends ShallowArtifact {

      public MockShallowArtifact(BranchCache cache, int artifactId) {
         super(cache, artifactId);
      }

      @Override
      public Artifact getFullArtifact() throws OseeCoreException {
         Artifact associatedArtifact = null;
         //         if (getArtId() > 0) {
         //            associatedArtifact = ArtifactQuery.getArtifactFromId(getArtId(), getBranch());
         //         } else {
         //            associatedArtifact = UserManager.getUser(SystemUser.OseeSystem);
         //            artifactId = associatedArtifact.getArtId();
         //         }
         return associatedArtifact;
      }

   }
}
