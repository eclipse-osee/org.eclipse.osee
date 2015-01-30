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
package org.eclipse.osee.ats.api.agile;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Donald G. Dunne
 */
@XmlRootElement
public class JaxAgileSprint extends AbstractAgileObject {

   private long teamUuid;
   private List<String> names;

   public long getTeamUuid() {
      return teamUuid;
   }

   public void setTeamUuid(long teamUuid) {
      this.teamUuid = teamUuid;
   }

   public List<String> getNames() {
      if (names == null) {
         names = new ArrayList<String>();
      }
      return names;
   }

   public void setNames(List<String> names) {
      this.names = names;
   }

}
