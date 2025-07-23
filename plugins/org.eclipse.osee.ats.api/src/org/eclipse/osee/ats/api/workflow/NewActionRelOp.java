/*******************************************************************************
 * Copyright (c) 2025 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.workflow;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.osee.framework.core.enums.OseeEnum;

/**
 * @author Donald G. Dunne
 */
public class NewActionRelOp extends OseeEnum {

   private static final Long ENUM_ID = 21352345234L;

   public static NewActionRelOp Add = new NewActionRelOp(111L, "Add");
   public static NewActionRelOp Remove = new NewActionRelOp(222L, "Remove");

   public NewActionRelOp() {
      super(ENUM_ID, -1L, "unset");
      // for jax-rs
   }

   public NewActionRelOp(long id, String name) {
      super(ENUM_ID, id, name);
   }

   @Override
   public Long getTypeId() {
      return ENUM_ID;
   }

   @JsonIgnore
   @Override
   public OseeEnum getDefault() {
      return Add;
   }

}
