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
package org.eclipse.osee.demo.db.connection;

import java.net.URL;
import org.eclipse.osee.demo.db.connection.internal.Activator;
import org.eclipse.osee.framework.core.data.IDatabaseInfo;
import org.eclipse.osee.framework.database.core.DatabaseInfoManager;
import org.eclipse.osee.framework.database.core.IDbConnectionInformationContributor;

/**
 * @author Andrew M Finkbeiner
 */
public class DemoDbConnectionInfo implements IDbConnectionInformationContributor {

   @Override
   public IDatabaseInfo[] getDbInformation() throws Exception {
      URL url = Activator.getEntry("/support/osee.demo.db.connection.xml");
      return DatabaseInfoManager.readFromXml(url.openStream());
   }
}
