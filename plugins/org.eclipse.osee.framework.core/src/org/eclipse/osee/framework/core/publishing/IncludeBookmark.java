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

package org.eclipse.osee.framework.core.publishing;

import org.eclipse.osee.framework.core.publishing.artifactacceptor.ArtifactAcceptor;

/**
 * Enumeration used to indicate if the publisher should include a book mark unless otherwise indicated not to.
 *
 * @author Loren K. Ashley
 */

public enum IncludeBookmark {

   /**
    * Do not add a book mark.
    */

   NO {

      /**
       * {@inheritDoc}
       *
       * @return {@link ArtifactAcceptor#ko}.
       */

      @Override
      public ArtifactAcceptor getArtifactAcceptor() {
         return ArtifactAcceptor.ko();
      }
   },

   /**
    * Add a book mark unless otherwise indicated not to.
    */

   YES {

      /**
       * {@inheritDoc}
       *
       * @return {@link ArtifactAcceptor#ok}.
       */

      @Override
      public ArtifactAcceptor getArtifactAcceptor() {
         return ArtifactAcceptor.ok();
      }
   };

   /**
    * Predicate to determine if a book mark should not be added.
    *
    * @return <code>true</code> when the enumeration member is {@link IncludeBookmark#NO}; otherwise,
    * <code>false</code>.
    */

   public boolean isNo() {
      return this == NO;
   }

   /**
    * Predicate to determine if a book mark should be added unless otherwise indicated not to.
    *
    * @return <code>true</code> when the enumeration member is {@link IncludeBookmark#NO}; otherwise,
    * <code>false</code>.
    */

   public boolean isYes() {
      return this == YES;
   }

   /**
    * Returns an {@link ArtifactAcceptor} that indicates when a book mark should be included.
    *
    * @return the {@link ArtifactAcceptor} for the enumeration memeber.
    */

   public abstract ArtifactAcceptor getArtifactAcceptor();

}

/* EOF */
