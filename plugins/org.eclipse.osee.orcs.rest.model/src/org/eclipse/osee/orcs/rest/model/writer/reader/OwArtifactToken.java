/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.orcs.rest.model.writer.reader;

import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.Id;

/**
 * Data Transfer object for Orcs Writer
 *
 * @author Donald G. Dunne
 */
public class OwArtifactToken extends OwBase implements ArtifactToken {

   public OwArtifactToken() {
      // for jax-rs instantiation
      super(Id.SENTINEL, "");
   }

   public OwArtifactToken(Long id, String name) {
      super(id, name);
   }

   @Override
   public String toString() {
      return "OwArtifactToken [id=" + getId() + ", data=" + data + "]";
   }

   @Override
   public BranchId getBranch() {
      return null;
   }

   @Override
   public ArtifactTypeToken getArtifactType() {
      return ArtifactTypeToken.SENTINEL;
   }
}