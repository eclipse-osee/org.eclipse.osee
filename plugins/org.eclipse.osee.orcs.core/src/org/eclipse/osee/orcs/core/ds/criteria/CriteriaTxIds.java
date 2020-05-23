/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.orcs.core.ds.criteria;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.TransactionId;

/**
 * @author Roberto E. Escobar
 */
public final class CriteriaTxIds extends CriteriaMainTableField implements TxCriteria {
   public CriteriaTxIds(Collection<TransactionId> ids) {
      super(ids);
   }

   public CriteriaTxIds(TransactionId id) {
      super(id);
   }
}