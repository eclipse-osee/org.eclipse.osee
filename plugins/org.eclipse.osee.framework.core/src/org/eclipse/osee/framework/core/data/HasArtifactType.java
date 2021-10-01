/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.osee.framework.core.data;

/**
 * @author Ryan D. Brooks
 */
public interface HasArtifactType {

   ArtifactTypeToken getArtifactType();

   /**
    * Determines if this artifact's type equals, or is a sub-type of, at least one of the given artifact types. This is
    * a more expensive operation than isTypeEqual(), so only use this method when inheritance is needed
    */
   default boolean isOfType(ArtifactTypeId... otherTypes) {
      for (ArtifactTypeId otherType : otherTypes) {
         if (getArtifactType().inheritsFrom(otherType)) {
            return true;
         }
      }
      return false;
   }

   default boolean isTypeEqual(ArtifactTypeId... artifactTypes) {
      ArtifactTypeId artifactType = getArtifactType();
      for (ArtifactTypeId artType : artifactTypes) {
         if (artifactType.equals(artType)) {
            return true;
         }
      }
      return false;
   }

   default boolean isTypeEqual(ArtifactTypeId artifactType) {
      return artifactType.equals(getArtifactType());
   }
}