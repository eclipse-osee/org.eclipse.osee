/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
