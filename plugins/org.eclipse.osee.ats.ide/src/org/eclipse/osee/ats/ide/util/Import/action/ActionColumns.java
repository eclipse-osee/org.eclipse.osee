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
public class ActionColumns extends OseeEnum {

   private static final Long ENUM_ID = 232843291L;

   private static List<String> colNames = new ArrayList<>();
   public static ActionColumns None = new ActionColumns(10L, "None");
   public static ActionColumns Title = new ActionColumns(11L, "Title");
   public static ActionColumns Description = new ActionColumns(22L, "Description");
   public static ActionColumns ActionableItems = new ActionColumns(33L, "ActionableItems");
   public static ActionColumns Assignees = new ActionColumns(44L, "Assignees");
   public static ActionColumns Originator = new ActionColumns(55L, "Originator");
   public static ActionColumns ChangeType = new ActionColumns(66L, "ChangeType");
   public static ActionColumns Priority = new ActionColumns(77L, "Priority");
   public static ActionColumns Version = new ActionColumns(88L, "Version");
   public static ActionColumns EstimatedHours = new ActionColumns(99L, "EstimatedHours");
   public static ActionColumns AgilePoints = new ActionColumns(111L, "AgilePoints");
   public static ActionColumns AgileSprintName = new ActionColumns(222L, "AgileSprintName");
   public static ActionColumns AgileTeamName = new ActionColumns(333L, "AgileTeamName");

   public ActionColumns(long id, String name) {
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
