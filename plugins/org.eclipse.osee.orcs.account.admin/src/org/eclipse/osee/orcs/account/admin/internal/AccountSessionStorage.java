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
import org.eclipse.osee.account.admin.AccountSession;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;

/**
 * @author Roberto E. Escobar
 */
public interface AccountSessionStorage {

   Callable<ResultSet<AccountSession>> getAccountSessionByAccountId(long accountId);

   Callable<ResultSet<AccountSession>> getAccountSessionBySessionToken(String sessionToken);

   Callable<Integer> createAccountSession(Iterable<AccountSession> datas);

   Callable<Integer> updateAccountSession(Iterable<AccountSession> datas);

   Callable<Integer> deleteAccountSessionBySessionToken(String sessionToken);

}