/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.access;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.osee.framework.core.enums.OseeEnum;

/**
 * @author Donald G. Dunne
 */
public class AccessTypeMatch extends OseeEnum {

   private static final Long ENUM_ID = 992305828L;

   public static AccessTypeMatch NotComputed = new AccessTypeMatch(1, "NotComp");
   public static AccessTypeMatch Deny = new AccessTypeMatch(2, "Deny");
   public static AccessTypeMatch Allow = new AccessTypeMatch(3, "Allow");
   public static AccessTypeMatch NoMatch = new AccessTypeMatch(4, "NoMatch");

   public AccessTypeMatch(long id, String name) {
      super(ENUM_ID, id, name);
   }

   @Override
   public Long getTypeId() {
      return ENUM_ID;
   }

   @JsonIgnore
   @Override
   public OseeEnum getDefault() {
      return NotComputed;
   }

   public boolean isAllow() {
      return this.equals(Allow);
   }

   public boolean isDeny() {
      return this.equals(Deny);
   }

}
