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

import com.fasterxml.jackson.databind.JsonSerializer.None;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * Data Transfer object for Orcs Writer
 *
 * @author Donald G. Dunne
 */
@JsonSerialize(using = None.class)
public class OwBase extends NamedIdBase {

   public OwBase() {
      // for jax-rs instantiation
      super(Id.SENTINEL, "");
   }

   public OwBase(Long id, String name) {
      super(id, name);
   }

   String data = null;

   public String getData() {
      return data;
   }

   public void setData(String data) {
      this.data = data;
   }

   @Override
   public void setId(Long id) {
      this.id = id;
   }
}
