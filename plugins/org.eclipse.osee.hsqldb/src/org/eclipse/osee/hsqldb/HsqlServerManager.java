/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.hsqldb;

import java.util.Properties;
import org.eclipse.osee.framework.core.data.IDatabaseInfo;

/**
 * @author Roberto E. Escobar
 */
public interface HsqlServerManager {

   String asConnectionUrl(String url, Properties props);

   String startServer(String host, int port, int webPort, IDatabaseInfo dbInfo) throws Exception;

   boolean stopServerWithWait(String dbId);

   Iterable<String> getIds();

   boolean isRunning(String dbId);

   void printInfo(String dbId);

}