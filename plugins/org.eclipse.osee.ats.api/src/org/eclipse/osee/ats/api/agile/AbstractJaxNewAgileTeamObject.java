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

import javax.xml.bind.annotation.XmlRootElement;
import org.eclipse.osee.ats.api.config.JaxAtsObject;

/**
 * @author Donald G. Dunne
 */
@XmlRootElement
public class AbstractJaxNewAgileTeamObject extends JaxAtsObject {

   private long teamId;

   public long getTeamId() {
      return teamId;
   }

   public void setTeamId(long teamId) {
      this.teamId = teamId;
   }

}
