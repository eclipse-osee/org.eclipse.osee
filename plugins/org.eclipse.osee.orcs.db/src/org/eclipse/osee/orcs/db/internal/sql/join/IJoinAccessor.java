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
package org.eclipse.osee.orcs.db.internal.sql.join;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.enums.JoinItem;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.jdbc.JdbcConnection;

/**
 * @author Roberto E. Escobar
 */
public interface IJoinAccessor {

   void store(JdbcConnection connection, JoinItem joinItem, int queryId, List<Object[]> dataList, Long issuedAt, Long expiresIn) throws OseeCoreException;

   int delete(JdbcConnection connection, JoinItem joinItem, int queryId) throws OseeCoreException;

   Collection<Integer> getAllQueryIds(JdbcConnection connection, JoinItem joinItem) throws OseeCoreException;
}
