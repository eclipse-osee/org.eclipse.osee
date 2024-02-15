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

import java.util.LinkedList;
import org.eclipse.osee.framework.core.data.ArtifactReadable;

/**
 * A functional interface for methods that test if an artifact is OK to include in the publish.
 */

@FunctionalInterface
public interface ArtifactAcceptor {

   /**
    * Returns an {@link ArtifactAcceptor} that is the logical AND of the provided <code>artifactAcceptors</code>. The
    * evaluation of the artifact acceptors is done in a fail fast manner. When <code>artifactAcceptors</code> is
    * <code>null</code> or an empty array, an {@link ArtifactAcceptor} that always returns <code>true</code> is
    * returned. When an entry in the <code>artifactAcceptors</code> array is <code>null</code> it is treated as an
    * {@link ArtifactAcceptor} that always returns <code>true</code>.
    *
    * @param artifactAcceptors the {@link ArtifactAcceptor}s whose results are to be logically ANDed.
    * @return <code>true</code> when all of the {@link ArtifactAcceptor}s return <code>true</code>; otherwise,
    * <code>false</code>.
    */

   static ArtifactAcceptor and(ArtifactAcceptor... artifactAcceptors) {

      if ((artifactAcceptors == null) || (artifactAcceptors.length == 0)) {
         return ArtifactAcceptorImpls.ok;
      }

      final var validArtifactAcceptorList = new LinkedList<ArtifactAcceptor>();

      for (final var artifactAcceptor : artifactAcceptors) {

         if (artifactAcceptor == null) {
            continue;
         }

         if (artifactAcceptor == ArtifactAcceptorImpls.ok) {
            continue;
         }

         if (artifactAcceptor == ArtifactAcceptorImpls.ko) {
            return ArtifactAcceptorImpls.ko;
         }

         validArtifactAcceptorList.add(artifactAcceptor);

      }

      if (validArtifactAcceptorList.isEmpty()) {
         return ArtifactAcceptorImpls.ok;
      }

      return new ArtifactAcceptor() {

         @Override
         public boolean isOk(ArtifactReadable artifactReadable) {
            for (final var artifactAcceptor : validArtifactAcceptorList) {
               if (!artifactAcceptor.isOk(artifactReadable)) {
                  return false;
               }
            }
            return true;
         }

      };

   }

   /**
    * Returns an {@link ArtifactAcceptor} that always returns <code>false</code>. Using this method to obtain a KO
    * {@link ArtifactAcceptor} instead of creating one, allows the {@link ArtifactAcceptor} logical combination methods
    * to perform optimizations with the knowledge that the {@link ArtifactAcceptor} always returns <code>false</code>.
    *
    * @return an {@link ArtifactAcceptor} that always returns <code>false</code>.
    */

   static ArtifactAcceptor ko() {
      return ArtifactAcceptorImpls.ko;
   }

   /**
    * Returns an {@link ArtifactAcceptor} that is the logical NOT of the <code>baseArtifactAcceptor</code>.
    *
    * @param baseArtifactAcceptor the {@link ArtifactAcceptor} to create an inverse of.
    * @return an {@link ArtifactAcceptor} that is the logical NOT of the <code>baseArtifactAcceptor</code> when
    * non-<code>null</code>; otherwise, an {@link ArtifactAcceptor} that always returns <code>false</code>.
    */

   static ArtifactAcceptor not(ArtifactAcceptor baseArtifactAcceptor) {

      if (baseArtifactAcceptor == null) {
         return ArtifactAcceptorImpls.ko;
      }

      if (baseArtifactAcceptor == ArtifactAcceptorImpls.ok) {
         return ArtifactAcceptorImpls.ko;
      }

      if (baseArtifactAcceptor == ArtifactAcceptorImpls.ko) {
         return ArtifactAcceptorImpls.ok;
      }

      return (t) -> !baseArtifactAcceptor.isOk(t);
   }

   /**
    * Returns an {@link ArtifactAcceptor} that always returns <code>true</code>. Using this method to obtain a OK
    * {@link ArtifactAcceptor} instead of creating one, allows the {@link ArtifactAcceptor} logical combination methods
    * to perform optimizations with the knowledge that the {@link ArtifactAcceptor} always returns <code>true</code>.
    *
    * @return an {@link ArtifactAcceptor} that always returns <code>true</code>.
    */

   static ArtifactAcceptor ok() {
      return ArtifactAcceptorImpls.ok;
   }

   /**
    * Predicate to determine if the artifact can be included in the publish.
    *
    * @param artifactReadable the {@link ArtifactReadable} to be tested.
    * @return <code>true</code>, when the artifact can be included; otherwise <code>false</code>.
    */

   boolean isOk(ArtifactReadable artifactReadable);
}

/* EOF */
