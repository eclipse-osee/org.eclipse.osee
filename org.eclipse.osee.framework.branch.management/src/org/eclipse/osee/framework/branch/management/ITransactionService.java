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
package org.eclipse.osee.framework.branch.management;

import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.TransactionRecord;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public interface ITransactionService {

   public static final int HEAD = -1;
   public static final int BASE = -2;
   public static final int CURRENT = -3;

   TransactionRecord getTransaction(Branch branch, int revision) throws OseeCoreException;
}
