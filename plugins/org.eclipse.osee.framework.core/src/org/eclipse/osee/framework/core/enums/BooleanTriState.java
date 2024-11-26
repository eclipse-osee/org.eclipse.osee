/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.framework.core.enums;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public class BooleanTriState extends OseeEnum {

   private static final Long ENUM_ID = 328502323324L;
   private static final List<BooleanTriState> values = new ArrayList<>();

   public static final BooleanTriState NotSet = new BooleanTriState("Not Set");
   public static final BooleanTriState True = new BooleanTriState("True");
   public static final BooleanTriState False = new BooleanTriState("False");

   public BooleanTriState() {
      super(ENUM_ID, "");
   }

   protected BooleanTriState(String name) {
      super(ENUM_ID, name);
      values.add(this);
   }

   @JsonIgnore
   @Override
   public Long getTypeId() {
      return ENUM_ID;
   }

   @JsonIgnore
   @Override
   public OseeEnum getDefault() {
      return NotSet;
   }

   public static List<BooleanTriState> getValues() {
      return values;
   }

   public boolean isTrue() {
      return this.equals(True);
   }

   public boolean isFalse() {
      return this.equals(False);
   }

   public boolean isNotSet() {
      return this.equals(NotSet);
   }
}