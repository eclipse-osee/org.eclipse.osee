/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.account.admin.internal;

import java.util.concurrent.Callable;
import org.eclipse.osee.account.admin.AccountAccess;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;

/**
 * @author Roberto E. Escobar
 */
public interface AccountAccessStorage {

   Callable<ResultSet<AccountAccess>> getAccountAccessByAccountId(long accountId);

   Callable<ResultSet<AccountAccess>> getAccountAccessByAccessToken(String accessToken);

   Callable<Integer> createAccountAccess(Iterable<AccountAccess> datas);

   Callable<Integer> updateAccountAccess(Iterable<AccountAccess> datas);

   Callable<Integer> deleteAccountAccessByAccessToken(String accessToken);

}