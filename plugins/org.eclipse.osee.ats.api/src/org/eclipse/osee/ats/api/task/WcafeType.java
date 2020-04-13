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
package org.eclipse.osee.ats.api.task;

import org.eclipse.osee.framework.core.enums.OseeEnum;

/**
 * @author Donald G. Dunne
 */
public class WcafeType extends OseeEnum {

   private static final Long ENUM_ID = 323847500023L;

   // @formatter:off
   public static final WcafeType Warning = new WcafeType("Warning", 111L);
   public static final WcafeType Caution = new WcafeType("Caution", 222L);
   public static final WcafeType Advisory = new WcafeType("Advisory", 333L);
   public static final WcafeType Exceedence = new WcafeType("Exceedence", 444L);
   public static final WcafeType Fault = new WcafeType("Fault", 555L);
   public static final WcafeType Other = new WcafeType("Other", 999L);
   // @formatter:on

   public WcafeType(String name, Long id) {
      super(ENUM_ID, id, name);
   }

   @Override
   public Long getTypeId() {
      return ENUM_ID;
   }

   @Override
   public OseeEnum getDefault() {
      return Other;
   }

   public static WcafeType valueOf(String name) {
      for (OseeEnum oEnum : Other.values()) {
         if (oEnum.name().equals(name)) {
            return (WcafeType) oEnum;
         }
      }
      return (WcafeType) Other.getDefault();
   }

   public static WcafeType valueOf(Long id) {
      for (OseeEnum oEnum : Other.values()) {
         if (oEnum.getId().equals(id)) {
            return (WcafeType) oEnum;
         }
      }
      return (WcafeType) Other.getDefault();
   }
}
