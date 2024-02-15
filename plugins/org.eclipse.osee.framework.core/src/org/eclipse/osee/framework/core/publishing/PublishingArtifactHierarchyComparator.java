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

import java.util.Comparator;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * A {@link Comparator} implementation for hierarchically sorting a {@link List} of {@link PublishingArtifacts}.
 */

public class PublishingArtifactHierarchyComparator implements Comparator<PublishingArtifact> {

   /**
    * Creates a new {@link HierarchyComparator} instance.
    *
    * @param publishingErrorLog
    */

   public PublishingArtifactHierarchyComparator() {
   }

   /**
    * Compares the hierarchical position of two artifacts.
    *
    * @param lhsArtifact the left hand side artifact to compare.
    * @param rhsArtifact the right hand side artifact to compare.
    * @return -1 when the RHS artifact comes before the LHS artifact, 0 when the RHS artifact has the same position as
    * the LHS artifact, and 1 when the RHS artifact comes after the LHS artifact.
    * @throws NullPointerException when either <code>lhsArtifact</code> or <code>rhsArtifact</code> are
    * <code>null</code>.
    */

   @Override
   public int compare(@NonNull PublishingArtifact lhsArtifact, @NonNull PublishingArtifact rhsArtifact) {

      var lhsHierarchyPosition = Conditions.requireNonNull(lhsArtifact, "lhsArtifact").getHierarchyPosition();
      var lhsSize = lhsHierarchyPosition.size();

      var rhsHierarchyPosition = Conditions.requireNonNull(rhsArtifact, "rhsArtifact").getHierarchyPosition();
      var rhsSize = rhsHierarchyPosition.size();

      for (var i = 0; i < lhsSize; i++) {

         if (i >= rhsSize) {
            return 1;
         }

         var lhsPosition = lhsHierarchyPosition.get(i);
         var rhsPosition = rhsHierarchyPosition.get(i);

         if (rhsPosition < lhsPosition) {
            return 1;
         }

         if (rhsPosition > lhsPosition) {
            return -1;
         }
      }

      if (rhsSize > lhsSize) {
         return -1;
      }

      return 0;
   }

}

/* EOF */
