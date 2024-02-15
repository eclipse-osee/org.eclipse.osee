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

import java.util.Objects;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * Enumeration used to indicate which artifact types are allowed as document headings.
 *
 * @author Loren K. Ashley
 */

public enum AllowedOutlineTypes {

   /**
    * Any artifact type can be used for outlining.
    */

   ANYTHING {

      /**
       * {@inheritDoc}
       * <p>
       * All artifact types are allowed.
       *
       * @throws NullPointerException when <code>publishingArtifact</code> is <code>null</code>.
       */

      @Override
      public boolean isAllowed(@NonNull PublishingArtifact artifact,
         @Nullable ArtifactTypeToken allowedHeadingArtifactTypeToken) {
         Conditions.requireNonNull(artifact, "artifact");
         return true;
      }
   },

   /**
    * Only artifact types of or derived from {@link CoreArtifactTypes#Folder} can be used for outlining.
    */

   FOLDERS_ONLY {

      /**
       * {@inheritDoc}
       * <p>
       * All artifact types of or derived from {@link CoreArtifactTypes.Folder} are allowed.
       *
       * @throws NullPointerException when <code>publishingArtifact</code> is <code>null</code>.
       */

      @Override
      public boolean isAllowed(@NonNull PublishingArtifact artifact,
         @Nullable ArtifactTypeToken allowedHeadingArtifactTypeToken) {
         final var safeArtifact = Conditions.requireNonNull(artifact, "artifact");
         return safeArtifact.isOfType(CoreArtifactTypes.Folder);
      }
   },

   /**
    * Only artifact types of or derived from {@link CoreArtifactTypes#Folder} and
    * {@link CoreArtifactTypes#AbstractHeading} can be used for outlining.
    */

   HEADERS_AND_FOLDERS_ONLY {

      /**
       * {@inheritDoc}
       * <p>
       * All artifact types of or derived from {@link CoreArtifactTypes.Folder} or
       * {@link CoreArtifactTypes.AbstractHeading} are allowed.
       *
       * @throws NullPointerException when <code>publishingArtifact</code> is <code>null</code>.
       */

      @Override
      public boolean isAllowed(@NonNull PublishingArtifact artifact,
         @Nullable ArtifactTypeToken allowedHeadingArtifactTypeToken) {
         final var safeArtifact = Conditions.requireNonNull(artifact, "artifact");
         return safeArtifact.isOfType(CoreArtifactTypes.AbstractHeading, CoreArtifactTypes.Folder);
      }
   },

   /**
    * Only artifact types of or derived from {@link CoreArtifactTypes#AbstractHeading} can be used for outlining.
    */

   HEADERS_ONLY {

      /**
       * {@inheritDoc}
       * <p>
       * All artifact types of or derived from {@link CoreArtifactTypes.AbstractHeading} are allowed.
       *
       * @throws NullPointerException when <code>publishingArtifact</code> is <code>null</code>.
       */

      @Override
      public boolean isAllowed(@NonNull PublishingArtifact artifact,
         @Nullable ArtifactTypeToken allowedHeadingArtifactTypeToken) {
         final var safeArtifact = Conditions.requireNonNull(artifact, "artifact");
         return safeArtifact.isOfType(CoreArtifactTypes.AbstractHeading);
      }

   },

   /**
    * Only artifact types of or derived from the type specified by the outlining options "HeadingArtifactType" can be
    * used for outlining. When the outlining option "HeadingArtifactType" is not specified, any artifact type can be
    * used for outlining.
    */

   RESTRICTED {

      /**
       * {@inheritDoc}
       * <p>
       * Only artifact types of the type specified by <code>allowedHeadingArtifactTypeToken</code> are allowed when
       * <code>allowedHeadingArtifactTypeToken</code> is non-<code>null</code>; otherwise, all artifact types are
       * allowed.
       *
       * @throws NullPointerException when <code>publishingArtifact</code> is <code>null</code>.
       */

      @Override
      public boolean isAllowed(@NonNull PublishingArtifact artifact,
         @Nullable ArtifactTypeToken allowedHeadingArtifactTypeToken) {
         final var safeArtifact = Conditions.requireNonNull(artifact, "artifact");
   //@formatter:off
         return Objects.nonNull(allowedHeadingArtifactTypeToken)
            ? safeArtifact.isOfType( allowedHeadingArtifactTypeToken )
            : true;
         //@formatter:on
      }
   };

   /**
    * Predicate to determine if a {@link PublishingArtifact} is allowed to be used as a document heading.
    *
    * @param artifact the {@link PublishingArtifact} to test.
    * @param allowedHeadingArtifactTypeToken specifies the allowed artifact type when the enumeration member is
    * {@link #RESTRICTED}; otherwise, ignored.
    * @return <code>true</code> when the <code>artifact</code>'s type is allowed as a document heading; otherwise,
    * <code>false</code>.
    */

   public abstract boolean isAllowed(@NonNull PublishingArtifact artifact,
      @Nullable ArtifactTypeToken allowedHeadingArtifactTypeToken);

}

/* EOF */
