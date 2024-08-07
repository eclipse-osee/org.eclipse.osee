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

import java.util.List;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Donald G. Dunne
 */
public class ColorTeam extends NamedIdBase {

   private List<Long> goalIds;

   public ColorTeam() {
      this(-1L, "");
   }

   public ColorTeam(Long id, String name) {
      super(id, name);
   }

   public List<Long> getGoalIds() {
      return goalIds;
   }

   public void setGoalIds(List<Long> goalIds) {
      this.goalIds = goalIds;
   }

   @Override
   public String toString() {
      return "ColorTeam [id=" + id + ", name=" + getName() + ", id=" + getIdString() + " goalIds=" + goalIds + "]";
   }

}
