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
package org.eclipse.osee.framework.plugin.core.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author Andrew M. Finkbeiner
 */
public interface IConnection {
   Connection getConnection(Properties properties, String connectionURL) throws ClassNotFoundException, SQLException;
}
