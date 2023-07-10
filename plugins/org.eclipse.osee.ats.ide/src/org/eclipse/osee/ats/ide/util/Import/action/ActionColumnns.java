/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.ats.ide.util.Import.action;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.enums.OseeEnum;

/**
 * @author Donald G. Dunne
 */
public class ActionColumnns extends OseeEnum {

   private static final Long ENUM_ID = 232843291L;

   private static List<String> colNames = new ArrayList<>();
   public static ActionColumnns None = new ActionColumnns(10L, "None");
   public static ActionColumnns Title = new ActionColumnns(11L, "Title");
   public static ActionColumnns Description = new ActionColumnns(22L, "Description");
   public static ActionColumnns ActionableItems = new ActionColumnns(33L, "ActionableItems");
   public static ActionColumnns Assignees = new ActionColumnns(44L, "Assignees");
   public static ActionColumnns Originator = new ActionColumnns(55L, "Originator");
   public static ActionColumnns ChangeType = new ActionColumnns(66L, "ChangeType");
   public static ActionColumnns Priority = new ActionColumnns(77L, "Priority");
   public static ActionColumnns Version = new ActionColumnns(88L, "Version");
   public static ActionColumnns EstimatedHours = new ActionColumnns(99L, "EstimatedHours");
   public static ActionColumnns AgilePoints = new ActionColumnns(111L, "AgilePoints");
   public static ActionColumnns AgileSprintName = new ActionColumnns(222L, "AgileSprintName");
   public static ActionColumnns AgileTeamName = new ActionColumnns(333L, "AgileTeamName");

   public ActionColumnns(long id, String name) {
      super(ENUM_ID, id, name);
      colNames.add(name);
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

   public static List<String> getColNames() {
      return colNames;
   }

}
