/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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
public class ChangeTypes extends OseeEnum {

   public static final String CHANGE_TYPE_PARAM_KEY = "ChangeType";

   private static final Long ENUM_ID = 23412356755L;
   private static final List<ChangeTypes> values = new ArrayList<>();

   public static final ChangeTypes None = new ChangeTypes("None");
   public static final ChangeTypes Support = new ChangeTypes("Support", "");
   public static final ChangeTypes Problem = new ChangeTypes("Problem", "");
   public static final ChangeTypes Improvement = new ChangeTypes("Improvement", "");
   public static final ChangeTypes Refinement = new ChangeTypes("Refinement", "Small change");
   public static final ChangeTypes InitialDev = new ChangeTypes("Initial Dev", "");
   public static final ChangeTypes Fix = new ChangeTypes("Fix", "");
   public static final List<ChangeTypes> DEFAULT_CHANGE_TYPES =
      Arrays.asList(ChangeTypes.Improvement, ChangeTypes.Problem, ChangeTypes.Refinement, ChangeTypes.Support);

   private String description;

   public ChangeTypes() {
      super(ENUM_ID, "");
   }

   private ChangeTypes(String name) {
      this(name, "");
   }

   protected ChangeTypes(String name, String description) {
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

   @JsonIgnore
   public boolean isNotNone() {
      return !this.equals(ChangeTypes.None);
   }

   public static ChangeTypes valueOf(String changeTypeStr) {
      return valueOfOrNone(changeTypeStr);
   }

   public static ChangeTypes valueOfOrNone(String changeTypeStr) {
      ChangeTypes changeType = (ChangeTypes) None.get(changeTypeStr);
      if (changeType == null) {
         changeType = ChangeTypes.None;
      }
      return changeType;
   }

   public static ChangeTypes getChangeType(Object changeType) {
      String changeTypeName = "";
      if (changeType instanceof ChangeTypes) {
         changeTypeName = ((ChangeTypes) changeType).getName();
      } else if (changeType instanceof String) {
         changeTypeName = (String) changeType;
      }
      for (ChangeTypes type : values) {
         if (type.name().equals(changeTypeName)) {
            return type;
         }
      }
      return None;
   }

   public static List<ChangeTypes> getValues() {
      return values;
   }

   public static List<String> getDefaultValuesStrs() {
      List<String> vals = new ArrayList<>();
      for (ChangeTypes type : DEFAULT_CHANGE_TYPES) {
         vals.add(type.name());
      }
      return vals;
   }

   public static List<String> getValuesStrs() {
      List<String> vals = new ArrayList<>();
      for (ChangeTypes type : values) {
         vals.add(type.name());
      }
      return vals;
   }
}