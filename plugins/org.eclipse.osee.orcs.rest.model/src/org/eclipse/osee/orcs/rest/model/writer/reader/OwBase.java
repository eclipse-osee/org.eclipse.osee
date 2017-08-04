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

import org.codehaus.jackson.map.JsonSerializer.None;
import org.codehaus.jackson.map.annotate.JsonSerialize;
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

   public void setId(Long id) {
      this.id = id;
   }
}
