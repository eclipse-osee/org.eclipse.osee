/*******************************************************************************
 * Copyright (c) 2024 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.osee.framework.core.enums.OseeEnum;

/**
 * @author Donald G. Dunne
 */
public class AtsConstants extends OseeEnum {

   private static final Long ENUM_ID = 3285721011L;

   public static AtsConstants SENTINEL = new AtsConstants(-1L, "Sentinel");
   public static AtsConstants CreateTasksFromActions = new AtsConstants(222L, "Create Task(s) from Action(s)");
   public static AtsConstants RestoreTasksFromActions = new AtsConstants(333L, "Restore Action(s) from Task(s)");

   public AtsConstants(long id, String name) {
      super(ENUM_ID, id, name);
   }

   @Override
   public Long getTypeId() {
      return ENUM_ID;
   }

   @JsonIgnore
   @Override
   public OseeEnum getDefault() {
      return SENTINEL;
   }

}
