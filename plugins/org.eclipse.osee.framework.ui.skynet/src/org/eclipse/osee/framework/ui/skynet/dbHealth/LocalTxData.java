/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
