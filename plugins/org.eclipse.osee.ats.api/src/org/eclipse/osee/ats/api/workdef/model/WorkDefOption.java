/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.workdef.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.osee.framework.core.enums.OseeEnum;

/**
 * @author Donald G. Dunne
 */
public class WorkDefOption extends OseeEnum {

   private static final Long ENUM_ID = 3185790353298798L;

   public static WorkDefOption RequireTargetedVersion = new WorkDefOption(111L, "RequireTargetedVersion");
   public static WorkDefOption RequireAssignees = new WorkDefOption(222L, "RequireAssignees");
   public static WorkDefOption None = new WorkDefOption(333L, "None");

   public WorkDefOption(long id, String name) {
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
