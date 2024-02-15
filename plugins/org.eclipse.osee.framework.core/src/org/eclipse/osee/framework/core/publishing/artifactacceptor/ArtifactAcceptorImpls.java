/*********************************************************************
 * Copyright (c) 2024 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.core.publishing.artifactacceptor;

import org.eclipse.osee.framework.core.data.ArtifactReadable;

/**
 * A class of package private static {@link ArtifactAcceptor} implementations.
 *
 * @author Loren K. Ashley
 */

final class ArtifactAcceptorImpls {

   /**
    * An {@link ArtifactAcceptor} implementation that always returns <code>true</code>.
    */

   static final ArtifactAcceptor ok = new ArtifactAcceptor() {

      /**
       * {@inheritDoc}
       *
       * @return <code>true</code>.
       */

      @Override
      public boolean isOk(ArtifactReadable artifactReadable) {
         return true;
      }

      /**
       * {@inheritDoc}
       */

      @Override
      public String toString() {
         return "ArtifactAcceptor::ok";
      }
   };

   /**
    * An {@link ArtifactAcceptor} implementation that always returns <code>false</code>.
    */

   static final ArtifactAcceptor ko = new ArtifactAcceptor() {

      /**
       * {@inheritDoc}
       *
       * @return <code>false</code>.
       */

      @Override
      public boolean isOk(ArtifactReadable artifactReadable) {
         return false;
      }

      /**
       * {@inheritDoc}
       */

      @Override
      public String toString() {
         return "ArtifactAcceptor::ko";
      }
   };

}

/* EOF */