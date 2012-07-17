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
import org.eclipse.osee.framework.core.data.IDatabaseInfo;
import org.eclipse.osee.framework.database.AbstractDatabaseInfoContributor;

/**
 * @author Andrew M. Finkbeiner
 */
public class DemoDbConnectionInfo extends AbstractDatabaseInfoContributor {

   @Override
   public IDatabaseInfo[] getDbInformation() throws Exception {
      URL url = getClass().getResource("osee.demo.db.connection.xml");
      return readFromXml(url.openStream());
   }

}
