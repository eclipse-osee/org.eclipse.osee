/*********************************************************************
 * Copyright (c) 2022 Boeing
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
   private final ArtifactId artId;
   private final Long relationId;
   private final boolean added;

   public MimRelationChange(ArtifactId artId, Long relationId, boolean added) {
      this.artId = artId;
      this.relationId = relationId;
      this.added = added;
   }

   public ArtifactId getArtId() {
      return artId;
   }

   public Long getRelationid() {
      return relationId;
   }

   public boolean isAdded() {
      return added;
   }
}