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

package org.eclipse.osee.ats.api.team;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.enums.OseeEnum;

/**
 * @author Donald G. Dunne
 */
public class Points extends OseeEnum {

   private static final Long ENUM_ID = 1287059213L;
   private static final List<Points> values = new ArrayList<>();

   public static final Points SENTINEL = new Points(-1, "Sentinel");
   public static final Points Points_1 = new Points("1");
   public static final Points Points_2 = new Points("2");
   public static final Points Points_3 = new Points("3");
   public static final Points Points_4 = new Points("4");
   public static final Points Points_5 = new Points("5");
   public static final Points Points_8 = new Points("8");
   public static final Points Points_13 = new Points("13");
   public static final Points Points_20 = new Points("20");
   public static final Points Points_40 = new Points("40");
   public static final Points Points_80 = new Points("80");
   public static final Points Points_150 = new Points("150");
   public static final Points Points_Epic = new Points("Epic");

   private static long ordinal = 0;

   public Points() {
      super(ENUM_ID, "");
   }

   private Points(String name) {
      this(++ordinal, name);
   }

   private Points(long ordinal, String name) {
      super(ENUM_ID, ++ordinal, name);
      if (ordinal > 0) {
         values.add(this);
      }
   }

   @JsonIgnore
   @Override
   public Long getTypeId() {
      return ENUM_ID;
   }

   @JsonIgnore
   @Override
   public OseeEnum getDefault() {
      return SENTINEL;
   }

   @JsonIgnore
   public boolean isNone() {
      return this.equals(SENTINEL);
   }

   @JsonIgnore
   public boolean isNotNone() {
      return !isNone();
   }

   public static List<Points> getValues() {
      return values;
   }

   public static Collection<String> getValuesStr() {
      return Points.getValues().stream().map(p -> p.name()).collect(Collectors.toList());
   }

}