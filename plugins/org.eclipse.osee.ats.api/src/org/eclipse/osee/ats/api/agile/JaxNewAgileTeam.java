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

import org.eclipse.osee.ats.api.config.JaxAtsObject;

/**
 * @author Donald G. Dunne
 */
public class JaxNewAgileTeam extends JaxAtsObject {

   public String programId;

   public String getProgramId() {
      return programId;
   }

   public void setProgramId(String programId) {
      this.programId = programId;
   }

}
