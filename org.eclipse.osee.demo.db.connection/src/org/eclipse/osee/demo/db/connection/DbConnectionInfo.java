/*
 * Created on Apr 24, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.demo.db.connection;

import java.net.URL;
import org.eclipse.osee.framework.db.connection.IDbConnectionInformationContributer;
import org.eclipse.osee.framework.db.connection.info.DbConfigParser;
import org.eclipse.osee.framework.db.connection.info.DbInformation;
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
      return DbConfigParser.parse(rootElement);
   }
}
