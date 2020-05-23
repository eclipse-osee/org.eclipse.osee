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

package org.eclipse.osee.jdbc.internal;

import java.util.Map;
import org.eclipse.osee.jdbc.JdbcException;

/**
 * @author Roberto E. Escobar
 */
public interface JdbcConnectionProvider {

   Map<String, String> getStatistics() throws JdbcException;

   JdbcConnectionImpl getConnection(JdbcConnectionInfo dbInfo) throws JdbcException;

   void dispose() throws JdbcException;

}
