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
package org.eclipse.osee.framework.skynet.core.transaction.data;

import java.util.List;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;

/**
 * @author Jeff C. Phillips
 */
public interface ITransactionData {

   public String getTransactionChangeSql();

   public String setPreviousTxNotCurrentSql();

   public List<Object> getPreviousTxNotCurrentData();

   public List<Object> getTransactionChangeData();

   public int getGammaId();

   public int getTransactionId();

   public ModificationType getModificationType();

   public void setModificationType(ModificationType modificationType);
}
