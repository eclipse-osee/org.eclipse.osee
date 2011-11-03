/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.display.mvp.mocks;

/**
 * @author Roberto E. Escobar
 */
public class EventMessage {

   private final Long id;
   private final String name;

   public EventMessage(Long id, String name) {
      this.id = id;
      this.name = name;
   }

   public Long getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   @Override
   public String toString() {
      return "EventMessage [id=" + id + ", name=" + name + "]";
   }

}
