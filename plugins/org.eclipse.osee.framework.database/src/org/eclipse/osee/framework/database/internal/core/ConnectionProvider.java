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
package org.eclipse.osee.framework.database.internal.core;

import java.util.Map;
import org.eclipse.osee.framework.core.data.IDatabaseInfo;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public interface ConnectionProvider {

   Map<String, String> getStatistics() throws OseeCoreException;

   IDatabaseInfo getDefaultDatabaseInfo() throws OseeCoreException;

   BaseOseeConnection getConnection() throws OseeCoreException;

   BaseOseeConnection getConnection(IDatabaseInfo databaseInfo) throws OseeCoreException;

   void dispose() throws OseeCoreException;

}
