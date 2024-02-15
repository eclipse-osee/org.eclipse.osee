/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import java.util.Set;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.publishing.artifactacceptor.ArtifactAcceptor;

/**
 * An {@link ArtifactAcceptor} implementation that determines if an artifact is view applicable for publishing.
 *
 * @author Loren K. Ashley
 */

public class WordRenderApplicabilityChecker implements ArtifactAcceptor {

   /**
    * A Functional Interface for a method that loads the identifiers of all the non-applicable artifacts on a branch for
    * the specified view.
    */

   @FunctionalInterface
   public interface NonApplicableArtifactIdLoader {

      /**
       * Loads the identifiers of all artifacts on a branch that are not applicable to a view.
       *
       * @param branchId the branch to load artifact identifiers from.
       * @param viewId the view used to determine which artifacts are non-applicable.
       * @return a {@link Set} of the {@link ArtifactId}s for all the artifacts on the branch that are not applicable to
       * the view.
       */

      Set<ArtifactId> load(BranchId branchId, ArtifactId viewId);
   }

   /**
    * Save an implementation of the {@link NonApplicableArtifactIdLoader} which is used to load the identifiers of all
    * artifacts on a branch that are not applicable to a view.
    */

   private final NonApplicableArtifactIdLoader nonApplicableArtifactIdLoader;

   /**
    * Save the {@link ArtifactId}s of all artifacts on the branch that are not applicable to the view. This member will
    * be null if no view was specified to the {@link WordRenderApplicabilityChecker#load} method.
    */

   private Set<ArtifactId> nonApplicableArtifactIds;

   /**
    * Saves the implementation of the {@link NonApplicableArtifactIdLoader} to be used to load the identifiers of
    * artifacts on a branch that are not applicable to a view. The implementation of this interface will be different
    * for client side publishing than for server side publishing.
    *
    * @param nonApplicableArtifactIdLoader the non-applicable artifact identifier loader.
    * @throws NullPointerException when the parameter <code>nonApplicableArtifactIdLoader</code> is <code>null</code>.
    */

   public WordRenderApplicabilityChecker(NonApplicableArtifactIdLoader nonApplicableArtifactIdLoader) {
      this.nonApplicableArtifactIdLoader = Objects.requireNonNull(nonApplicableArtifactIdLoader,
         "WordRenderApplicabilityChecker::new, parameter \"nonApplicableArtifactIdLoader\" cannot be null.");
      this.nonApplicableArtifactIds = null;
   }

   /**
    * Loads the artifact identifiers of all the artifact on the branch that are not applicable to the view.
    *
    * @param branchId the branch to load non-applicable artifact identifiers from.
    * @param viewId the applicability view.
    * @throws NullPointerException when either parameter <code>branchId</code> or <code>viewId</code> is
    * <code>null</code>.
    */

   public void load(BranchId branchId, ArtifactId viewId) {
      Objects.requireNonNull(branchId, "WordRenderApplicabilityChecker::load, parameter \"branchId\" cannot be null.");
      Objects.requireNonNull(viewId, "WordRenderApplicabilityChecker::load, parameter \"viewId\" cannot be null.");

      //@formatter:off
      var state =
           ( ArtifactId.SENTINEL.equals( branchId.getViewId() ) ? 0 : 1 )
         + ( ArtifactId.SENTINEL.equals( viewId               ) ? 0 : 2 );
      //@formatter:on

      switch (state) {
         case 0: //branchIdViewId and viewId are SENTINEL
            this.nonApplicableArtifactIds = null;
            break;

         case 1: //branchIdViewId is valid viewId is SENTINEL
            this.nonApplicableArtifactIds = this.nonApplicableArtifactIdLoader.load(branchId, branchId.getViewId());
            break;

         case 2: //branchIdViewId is SENTINEL viewId is valid
            this.nonApplicableArtifactIds = this.nonApplicableArtifactIdLoader.load(branchId, viewId);
            break;

         case 3: //branchIdViewId and viewId are valid
            if (!branchId.getViewId().equals(viewId)) {
               throw new IllegalArgumentException();
            }

            this.nonApplicableArtifactIds = this.nonApplicableArtifactIdLoader.load(branchId, viewId);
            break;
      }
   }

   /**
    * Predicate to determine if an artifact is applicable to a view. The artifact identifier presented for testing must
    * be from the same branch that non-applicable artifact identifiers were loaded from with the last call to the method
    * {@link #load}.
    *
    * @param artifactReadable the artifact to test for applicability.
    * @return <code>true</code>, when the submitted {@link ArtifactId} is not in the set of non-applicable artifact
    * identifiers {@link ArtifactId}s; otherwise, <code>false</code>.
    */

   @Override
   public boolean isOk(ArtifactReadable artifactReadable) {
      //@formatter:off
      return
         Objects.nonNull( this.nonApplicableArtifactIds )
            ? !this.nonApplicableArtifactIds.contains( artifactReadable )
            : true;
      //@formatter:on
   }

}

/* EOF */
