/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.dbHealth;

/**
 * @author Theron Virgin
 */
public class LocalTxData {
   public int dataId;
   public int branchUuid;
   public int number;

   public LocalTxData(int dataId, int branchUuid, int number) {
      super();
      this.branchUuid = branchUuid;
      this.dataId = dataId;
      this.number = number;
   }
}
