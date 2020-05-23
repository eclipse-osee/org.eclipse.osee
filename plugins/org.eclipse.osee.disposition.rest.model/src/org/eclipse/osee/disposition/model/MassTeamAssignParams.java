/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.disposition.model;

import java.util.List;

/**
 * @author Angel Avila
 */
public class MassTeamAssignParams {

   private String setId;
   private String team;
   private String userName;
   private List<String> namesList;

   public String getSetId() {
      return setId;
   }

   public String getTeam() {
      return team;
   }

   public String getUserName() {
      return userName;
   }

   public List<String> getNamesList() {
      return namesList;
   }

   public void setSetId(String setId) {
      this.setId = setId;
   }

   public void setTeam(String team) {
      this.team = team;
   }

   public void setUserName(String userName) {
      this.userName = userName;
   }

   public void setNamesList(List<String> namesList) {
      this.namesList = namesList;
   }

}
