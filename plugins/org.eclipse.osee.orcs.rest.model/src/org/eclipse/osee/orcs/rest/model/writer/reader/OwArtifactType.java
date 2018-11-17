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

import org.eclipse.osee.framework.jdk.core.type.Id;

/**
 * Data Transfer object for Orcs Writer
 *
 * @author Donald G. Dunne
 */
public class OwArtifactType extends OwBase {

   public OwArtifactType() {
      // for jax-rs instantiation
      super(Id.SENTINEL, "");
   }

   public OwArtifactType(Long id, String name) {
      super(id, name);
   }

   @Override
   public String toString() {
      return "OwArtifactType [id=" + getId() + ", data=" + data + "]";
   }
}