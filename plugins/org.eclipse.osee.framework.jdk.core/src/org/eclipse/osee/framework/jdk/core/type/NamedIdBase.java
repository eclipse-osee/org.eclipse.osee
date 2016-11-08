/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.framework.jdk.core.type;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * @author Ryan D. Brooks
 */
@JsonSerialize(using = NamedIdSerializer.class)
public class NamedIdBase extends BaseId implements NamedId {
   private String name;

   public NamedIdBase(Long id, String name) {
      super(id);
      this.name = name;
   }

   @Override
   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   @Override
   public String toString() {
      return name == null ? super.toString() : name;
   }

}