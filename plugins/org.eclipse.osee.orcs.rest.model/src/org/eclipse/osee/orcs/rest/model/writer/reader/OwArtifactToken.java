/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.model.writer.reader;

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;

/**
 * Data Transfer object for Orcs Writer
 *
 * @author Donald G. Dunne
 */
public class OwArtifactToken extends OwBase implements ArtifactToken {

   public OwArtifactToken() {
      // for jax-rs instantiation
      super(ArtifactId.SENTINEL.getId(), "");
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

}
