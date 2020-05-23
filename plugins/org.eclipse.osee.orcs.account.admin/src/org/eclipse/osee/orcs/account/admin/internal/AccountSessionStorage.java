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

package org.eclipse.osee.orcs.account.admin.internal;

import java.util.concurrent.Callable;
import org.eclipse.osee.account.admin.AccountSession;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;

/**
 * @author Roberto E. Escobar
 */
public interface AccountSessionStorage {

   Callable<ResultSet<AccountSession>> getAccountSessionByAccountId(ArtifactId accountId);

   Callable<ResultSet<AccountSession>> getAccountSessionBySessionToken(String sessionToken);

   Callable<Integer> createAccountSession(Iterable<AccountSession> datas);

   Callable<Integer> updateAccountSession(Iterable<AccountSession> datas);

   Callable<Integer> deleteAccountSessionBySessionToken(String sessionToken);

}