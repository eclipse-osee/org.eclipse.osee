/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.ats.api.task.create;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.osee.framework.core.enums.OseeEnum;

/**
 * @author Donald G. Dunne
 */
public class AutoTaskGenType extends OseeEnum {

   private static final Long ENUM_ID = 436296943332L;

   public static AutoTaskGenType ChgRptAddMod = new AutoTaskGenType(111L, "ChgRptAddMod");
   public static AutoTaskGenType ChgRptDelete = new AutoTaskGenType(222L, "ChgRptDelete");
   public static AutoTaskGenType Static = new AutoTaskGenType(333L, "Static");
   public static AutoTaskGenType None = new AutoTaskGenType(444L, "None");

   public AutoTaskGenType(long id, String name) {
      super(ENUM_ID, id, name);
   }

   @Override
   public Long getTypeId() {
      return ENUM_ID;
   }

   @JsonIgnore
   @Override
   public OseeEnum getDefault() {
      return None;
   }

}
