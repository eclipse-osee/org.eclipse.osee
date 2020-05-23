/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.ats.api.workdef;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.osee.framework.core.enums.OseeEnum;

/**
 * @author Donald G. Dunne
 */
public class WidgetStatus extends OseeEnum {

   private static final Long ENUM_ID = 8837298548L;

   // @formatter:off
   public static WidgetStatus None = new WidgetStatus("None");
   public static WidgetStatus Success = new WidgetStatus("Success");
   public static WidgetStatus Empty = new WidgetStatus("Empty");
   // string entered when sho2uld be int
   public static WidgetStatus Invalid_Type = new WidgetStatus("Invalid_Type");
   // entered 8 when should b2e >2 and <5 or entered 5 characters when min =8
   public static WidgetStatus Invalid_Range = new WidgetStatus("Invalid_Range");
   // entered 2 integers when2 should be 3; have to enter hours for defect
   public static WidgetStatus Invalid_Incompleted = new WidgetStatus("Invalid_Incompleted");
   public static WidgetStatus Exception = new WidgetStatus("Exception");
   // @formatter:on

   public static final WidgetStatus instance = None;

   public WidgetStatus() {
      super(ENUM_ID, -1L, "");
   }

   public WidgetStatus(String name) {
      super(ENUM_ID, name);
   }

   public WidgetStatus(long id, String name) {
      super(ENUM_ID, id, name);
   }

   @Override
   public Long getTypeId() {
      return ENUM_ID;
   }

   @JsonIgnore
   @Override
   public WidgetStatus getDefault() {
      return None;
   }

   @JsonIgnore
   public boolean isEmpty() {
      return getId().equals(Empty.id);
   }

   @JsonIgnore
   public boolean isSuccess() {
      return getId().equals(Success.id);
   }

}
