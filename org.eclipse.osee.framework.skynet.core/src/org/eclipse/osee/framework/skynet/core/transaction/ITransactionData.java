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
package org.eclipse.osee.framework.skynet.core.transaction;

import java.sql.SQLException;
import java.sql.Timestamp;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;

/**
 * @author Jeff C. Phillips
 */
public interface ITransactionData {

   public void insertTransactionChange() throws SQLException;

   public void setPreviousTxNotCurrent(Timestamp insertTime, int queryId) throws SQLException;

   public int getGammaId();

   public TransactionId getTransactionId();

   public ModificationType getModificationType();

   public void setModificationType(ModificationType modificationType);
}
