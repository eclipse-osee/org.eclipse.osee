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

package org.eclipse.osee.ats.api.team;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.framework.core.enums.OseeEnum;

/**
 * @author Donald G. Dunne
 */
public class Priorities extends OseeEnum {

   public static final String PRIORITY_PARAM_KEY = "Priority";

   private static final Long ENUM_ID = 3452690439L;
   private static final List<Priorities> values = new ArrayList<>();

   public static final Priorities One = new Priorities("1");
   public static final Priorities Two = new Priorities("2");
   public static final Priorities Three = new Priorities("3");
   public static final Priorities Four = new Priorities("4");
   public static final Priorities Five = new Priorities("5");
   public static final Priorities Six = new Priorities("6");
   public static final Priorities None = new Priorities("None", "");

   public static final List<Priorities> DEFAULT_PRIORITIES =
      Arrays.asList(Priorities.One, Priorities.Two, Priorities.Three, Priorities.Four, Priorities.Five);

   private String description;

   public Priorities() {
      super(ENUM_ID, "");
   }

   private Priorities(String name) {
      this(name, "");
   }

   protected Priorities(String name, String description) {
      super(ENUM_ID, name);
      this.description = description;
      values.add(this);
   }

   public String getDescription() {
      return description;
   }

   @JsonIgnore
   @Override
   public Long getTypeId() {
      return ENUM_ID;
   }

   @JsonIgnore
   @Override
   public OseeEnum getDefault() {
      return None;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public static Priorities valueOf(String priorityStr) {
      return valueOfOrNone(priorityStr);
   }

   public static Priorities valueOfOrNone(String priorityTypes) {
      Priorities priority = (Priorities) None.get(priorityTypes);
      if (priority == null) {
         priority = Priorities.None;
      }
      return priority;
   }

   public static Priorities getPriority(Object priority) {
      String priorityName = "";
      if (priority instanceof Priorities) {
         priorityName = ((Priorities) priority).getName();
      } else if (priority instanceof String) {
         priorityName = (String) priority;
      }
      for (Priorities type : values) {
         if (type.name().equals(priorityName)) {
            return type;
         }
      }
      return None;
   }

   public static List<Priorities> getValues() {
      return values;
   }

   public static List<String> getDefaultValuesStrs() {
      List<String> vals = new ArrayList<>();
      for (Priorities type : DEFAULT_PRIORITIES) {
         vals.add(type.name());
      }
      return vals;
   }

   public static List<String> getValuesStrs() {
      List<String> vals = new ArrayList<>();
      for (Priorities type : values) {
         vals.add(type.name());
      }
      return vals;
   }
}