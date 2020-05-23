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

package org.eclipse.osee.ats.api.insertion;

import org.eclipse.osee.ats.api.config.JaxAtsObject;

/**
 * @author David W. Miller
 */
public class JaxInsertion extends JaxAtsObject {

   private long programId;

   public long getProgramId() {
      return programId;
   }

   public void setProgramId(long programId) {
      this.programId = programId;
   }

}
