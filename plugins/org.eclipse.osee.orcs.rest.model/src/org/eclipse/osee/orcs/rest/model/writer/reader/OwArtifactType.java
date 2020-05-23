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