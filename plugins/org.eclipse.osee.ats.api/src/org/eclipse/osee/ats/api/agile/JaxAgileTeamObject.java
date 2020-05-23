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

package org.eclipse.osee.ats.api.agile;

/**
 * @author Donald G. Dunne
 */
public class JaxAgileTeamObject extends JaxAgileObject {

   private long teamId;

   public long getTeamId() {
      return teamId;
   }

   public void setTeamId(long teamId) {
      this.teamId = teamId;
   }

}
