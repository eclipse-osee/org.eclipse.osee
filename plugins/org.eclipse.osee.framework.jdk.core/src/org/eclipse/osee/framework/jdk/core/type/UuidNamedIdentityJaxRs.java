/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.jdk.core.type;

/**
 * UuidNamedIdentity with zero parameter constructor needed by Jax-Rs serialization
 * 
 * @author Donald G. Dunne
 */
public class UuidNamedIdentityJaxRs extends UuidBaseIdentityJaxRs implements UuidIdentifiable {
   private String name;

   public UuidNamedIdentityJaxRs() {
      this(null, null);
   }

   public UuidNamedIdentityJaxRs(Long uid, String name) {
      super(uid);
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
      return getName();
   }
}