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
package org.eclipse.osee.framework.ui.branch.graph.model;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.UserId;

/**
 * @author Roberto E. Escobar
 */
public class StubBranchModel extends BranchModel {
   private static final long serialVersionUID = -6424441243526185426L;

   public static final BranchId STUB_BRANCH = null;

   private final Map<Long, TxModel> stubs;

   public StubBranchModel() {
      super(STUB_BRANCH);
      this.stubs = new HashMap<>();
   }

   public TxModel addTx(Long value) {
      TxModel toReturn = stubs.get(value);
      if (toReturn == null) {
         toReturn = new TxModel(new TxData(STUB_BRANCH, UserId.SENTINEL, new Timestamp(new Date().getTime()),
            String.format("Transaction: [%s] not found", value), 0, 0, value));
         addTx(toReturn);
      }
      return toReturn;
   }
}
