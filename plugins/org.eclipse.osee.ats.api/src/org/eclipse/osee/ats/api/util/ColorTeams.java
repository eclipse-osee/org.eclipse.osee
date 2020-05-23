/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.ats.api.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public class ColorTeams {

   List<ColorTeam> teams = new ArrayList<>();

   public ColorTeams() {
   }

   public List<ColorTeam> getTeams() {
      return teams;
   }

   public void setTeams(List<ColorTeam> teams) {
      this.teams = teams;
   }

   @Override
   public String toString() {
      return "ColorTeams [teams=" + teams + "]";
   }

}
