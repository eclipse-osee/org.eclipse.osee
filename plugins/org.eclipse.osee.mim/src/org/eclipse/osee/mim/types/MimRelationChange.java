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
package org.eclipse.osee.mim.types;

import org.eclipse.osee.framework.core.data.ArtifactId;

/**
 * @author Ryan T. Baldwin
 */
public class MimRelationChange {
   private final Long relationTypeId;
   private final ArtifactId artIdA;
   private final ArtifactId artIdB;
   private final String artBName;
   private final boolean added;

   public MimRelationChange(Long relationTypeId, ArtifactId artIdA, ArtifactId artIdB, String artBName, boolean added) {
      this.relationTypeId = relationTypeId;
      this.artIdA = artIdA;
      this.artIdB = artIdB;
      this.artBName = artBName;
      this.added = added;
   }

   public Long getRelationTypeId() {
      return relationTypeId;
   }

   public ArtifactId getArtIdA() {
      return artIdA;
   }

   public ArtifactId getArtIdB() {
      return artIdB;
   }

   public String getArtBName() {
      return artBName;
   }

   public boolean isAdded() {
      return added;
   }
}