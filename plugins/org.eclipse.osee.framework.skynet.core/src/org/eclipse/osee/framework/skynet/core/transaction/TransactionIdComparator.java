/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.framework.skynet.core.transaction;

import java.io.Serializable;
import java.util.Comparator;
import org.eclipse.osee.framework.core.data.TransactionId;

/**
 * @author Donald G. Dunne
 */
public class TransactionIdComparator implements Comparator<TransactionId>, Serializable {

   private static final long serialVersionUID = 1L;

   public TransactionIdComparator() {
      super();
   }

   @Override
   public int compare(TransactionId trans1, TransactionId trans2) {
      return trans1.getId().compareTo(trans2.getId());
   }
}