/*********************************************************************
 * Copyright (c) 2026 Boeing
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
package org.eclipse.osee.ats.ide.program;

import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.ide.config.copy.ConfigData;

/**
 * @author Donald G. Dunne
 */
public class AtsConfigData extends ConfigData {

   IAtsProgram fromProgram;
   IAtsProgram newProgram;

   public IAtsProgram getFromProgram() {
      return fromProgram;
   }

   public void setFromProgram(IAtsProgram fromProgram) {
      this.fromProgram = fromProgram;
   }

   public IAtsProgram getNewProgram() {
      return newProgram;
   }

   public void setNewProgram(IAtsProgram newProgram) {
      this.newProgram = newProgram;
      setNewProgramId(newProgram.getId());
   }

}
