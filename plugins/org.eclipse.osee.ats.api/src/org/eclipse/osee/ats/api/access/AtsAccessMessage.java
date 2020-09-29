/*********************************************************************
 * Copyright (c) 2020 Boeing
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
package org.eclipse.osee.ats.api.access;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.osee.framework.core.enums.OseeEnum;

/**
 * @author Donald G. Dunne
 */
public class AtsAccessMessage extends OseeEnum {

   private static final Long ENUM_ID = 438427448383L;

   public static final AtsAccessMessage AnonymousAccessDenied = new AtsAccessMessage("Anonymous Access Denied", "");
   public static final AtsAccessMessage AllowDefaultEdit = new AtsAccessMessage("Allow Default Edit", "");
   public static final AtsAccessMessage None = new AtsAccessMessage("");

   private String description;

   public AtsAccessMessage() {
      super(ENUM_ID, "");
   }

   private AtsAccessMessage(String name) {
      this(name, "");
   }

   private AtsAccessMessage(String name, String description) {
      super(ENUM_ID, name);
      this.description = description;
   }

   public String getDescription() {
      return description;
   }

   @JsonIgnore
   @Override
   public Long getTypeId() {
      return ENUM_ID;
   }

   @JsonIgnore
   @Override
   public OseeEnum getDefault() {
      return None;
   }

   public void setDescription(String description) {
      this.description = description;
   }
}
