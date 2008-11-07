/*
 * Created on Apr 24, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.demo.db.connection;

import java.net.URL;
import org.eclipse.osee.framework.db.connection.DatabaseInfoManager;
import org.eclipse.osee.framework.db.connection.IDatabaseInfo;
import org.eclipse.osee.framework.db.connection.IDbConnectionInformationContributor;

/**
 * @author Andrew M Finkbeiner
 */
public class DbConnectionInfo implements IDbConnectionInformationContributor {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.db.connection.IDbConnectionInformationContributer#getDbInformation()
    */
   @Override
   public IDatabaseInfo[] getDbInformation() throws Exception {
      URL url = Activator.getInstance().getBundleContext().getBundle().getEntry("/support/osee.demo.db.connection.xml");
      return DatabaseInfoManager.readFromXml(url.openStream());
   }
}
