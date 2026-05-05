/*********************************************************************
 * Copyright (c) 2026 Boeing
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
package org.eclipse.osee.orcs.search.ds;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.osee.framework.core.enums.OseeEnum;

/**
 * @author Donald G. Dunne
 */
public class FollowAllCriteria extends OseeEnum {

   private static final Long ENUM_ID = 2323809834L;

   public static FollowAllCriteria OneLevel = new FollowAllCriteria(111L, "OneLevel");
   public static FollowAllCriteria TwoLevels = new FollowAllCriteria(222L, "TwoLevels");

   public FollowAllCriteria(long id, String name) {
      super(ENUM_ID, id, name);
   }

   @Override
   public Long getTypeId() {
      return ENUM_ID;
   }

   @JsonIgnore
   @Override
   public OseeEnum getDefault() {
      return OneLevel;
   }

}
