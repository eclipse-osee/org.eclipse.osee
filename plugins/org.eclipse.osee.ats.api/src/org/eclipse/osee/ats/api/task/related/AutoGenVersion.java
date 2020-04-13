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
package org.eclipse.osee.ats.api.task.related;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.osee.framework.core.enums.OseeEnum;

/**
 * @author Donald G. Dunne
 */
public class AutoGenVersion extends OseeEnum {

   private static final Long ENUM_ID = 2124432342L;

   // Task attributes or parent contain data
   public static AutoGenVersion Ver2 = new AutoGenVersion(908129430, "Ver2");
   // For other versions that aren't supported by this code base
   public static AutoGenVersion Other = new AutoGenVersion(1084784801, "Other");

   public AutoGenVersion() {
      super(ENUM_ID, -1L, "");
   }

   public AutoGenVersion(long id, String name) {
      super(ENUM_ID, id, name);
   }

   @Override
   public Long getTypeId() {
      return ENUM_ID;
   }

   @JsonIgnore
   @Override
   public AutoGenVersion getDefault() {
      return Other;
   }

   public static AutoGenVersion valueOf(String autoGenVerStr) {
      return (AutoGenVersion) Other.get(autoGenVerStr);
   }

   public static AutoGenVersion valueOf(Long autoGenVerId) {
      return (AutoGenVersion) Other.get(autoGenVerId);
   }

}
