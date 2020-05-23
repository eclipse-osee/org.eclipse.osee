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

import org.eclipse.osee.ats.api.agile.JaxAgileTeamObject;

/**
 * @author David W. Miller
 */
public class JaxInsertionActivity extends JaxAgileTeamObject {

   private long insertionId;

   public long getInsertionId() {
      return insertionId;
   }

   public void setInsertionId(long insertionId) {
      this.insertionId = insertionId;
   }
}
