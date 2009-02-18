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
package org.eclipse.osee.framework.core.server.internal;

import java.io.File;
import java.net.URI;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.server.CoreServerActivator;
import org.eclipse.osee.framework.core.server.OseeServerProperties;
import org.eclipse.osee.framework.db.connection.DatabaseInfoManager;
import org.eclipse.osee.framework.db.connection.IDatabaseInfo;
import org.eclipse.osee.framework.db.connection.IDbConnectionInformationContributor;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class UriDbConnectionInfo implements IDbConnectionInformationContributor {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.db.connection.IDbConnectionInformationContributer#getDbInformation()
    */
   @Override
   public IDatabaseInfo[] getDbInformation() throws Exception {
      String uri = OseeServerProperties.getOseeConnectionInfoUri();
      if (Strings.isValid(uri)) {
         OseeLog.log(CoreServerActivator.class, Level.INFO, String.format("Loading connection info from: [%s]", uri));
         //         URL url = new URL(uri);
         //         uri = uri.replaceAll("\\\\", "/");
         URI connectionFile = null;
         if (!uri.contains("://")) {
            connectionFile = new File(uri).toURI();
            //            uri = "file://" + uri;
         } else {
            connectionFile = new URI(uri);
         }
         return DatabaseInfoManager.readFromXml(connectionFile.toURL().openStream());
      }
      return new IDatabaseInfo[0];
   }
}
