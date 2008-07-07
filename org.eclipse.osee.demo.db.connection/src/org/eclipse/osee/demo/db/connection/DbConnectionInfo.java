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
import org.eclipse.osee.framework.db.connection.IDbConnectionInformationContributer;
import org.eclipse.osee.framework.db.connection.info.DbInformation;
import org.eclipse.osee.framework.db.connection.info.ServerConfigUtil;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Andrew M Finkbeiner
 */
public class DbConnectionInfo implements IDbConnectionInformationContributer {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.db.connection.IDbConnectionInformationContributer#getDbInformation()
    */
   @Override
   public DbInformation[] getDbInformation() throws Exception {
      URL url = Activator.getInstance().getBundleContext().getBundle().getEntry("support/osee.demo.db.connection.xml");
      Document document = Jaxp.readXmlDocument(url.openStream());
      Element rootElement = document.getDocumentElement();
      ServerConfigUtil util = ServerConfigUtil.getNewInstance();
      util.parseDatabaseConfigFile(rootElement);
      return util.getAllDbServices();
   }
}
