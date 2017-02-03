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

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Donald G. Dunne
 */
@XmlRootElement
public class ColorTeam extends NamedIdBase {

   private List<Long> goalUuids;

   public ColorTeam() {
      this(-1L, "");
   }

   public ColorTeam(Long id, String name) {
      super(id, name);
   }

   public List<Long> getGoalUuids() {
      return goalUuids;
   }

   public void setGoalUuids(List<Long> goalUuids) {
      this.goalUuids = goalUuids;
   }

   @Override
   public String toString() {
      return "ColorTeam [id=" + id + ", name=" + getName() + ", id=" + getId() + " goalUuids=" + goalUuids + "]";
   }

}
