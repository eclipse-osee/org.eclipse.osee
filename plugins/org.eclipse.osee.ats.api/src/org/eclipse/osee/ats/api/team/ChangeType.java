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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.data.enums.token.ChangeTypeAttributeType.ChangeTypeEnum;

/**
 * @author Donald G. Dunne
 */
public enum ChangeType {

   None,
   Support,
   Problem,
   Improvement,
   Refinement;

   public static String[] getChangeTypes() {
      ArrayList<String> types = new ArrayList<>();
      for (ChangeType type : values()) {
         if (type != None) {
            types.add(type.name());
         }
      }
      return types.toArray(new String[types.size()]);
   }

   public static ChangeType getChangeType(Object changeType) {
      String changeTypeName = "";
      if (changeType instanceof ChangeTypeEnum) {
         changeTypeName = ((ChangeTypeEnum) changeType).getName();
      } else if (changeType instanceof String) {
         changeTypeName = (String) changeType;
      }
      for (ChangeType type : values()) {
         if (type.name().equals(changeTypeName)) {
            return type;
         }
      }
      return None;
   }

   public static String[] valueArray() {
      List<String> values = new ArrayList<>();
      for (String type : getChangeTypes()) {
         values.add(type);
      }
      return values.toArray(new String[values.size()]);
   }
}