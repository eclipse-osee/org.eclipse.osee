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
package org.eclipse.osee.framework.database.internal;

import java.io.File;
import java.net.URI;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.IDatabaseInfo;
import org.eclipse.osee.framework.database.core.DatabaseInfoManager;
import org.eclipse.osee.framework.database.core.IDbConnectionInformationContributor;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class UriDbConnectionInfo implements IDbConnectionInformationContributor {

   @Override
   public IDatabaseInfo[] getDbInformation() throws Exception {
      String uri = OseeProperties.getOseeConnectionInfoUri();
      if (Strings.isValid(uri)) {
         OseeLog.log(InternalActivator.class, Level.INFO, String.format("Loading connection info from: [%s]", uri));
         URI connectionFile = null;
         if (!uri.contains("://")) {
            connectionFile = new File(uri).toURI();
         } else {
            connectionFile = new URI(uri);
         }
         return DatabaseInfoManager.readFromXml(connectionFile.toURL().openStream());
      }
      return new IDatabaseInfo[0];
   }
}
