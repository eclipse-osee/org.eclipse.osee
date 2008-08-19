/*
 * Created on May 16, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ote.connection.jini;

import java.io.IOException;
import java.net.MalformedURLException;

import net.jini.core.discovery.LookupLocator;

/**
 * @author b1529404
 */
public interface IJiniConnectorRegistrar {
   void addLocators(String... hosts) throws MalformedURLException, ClassNotFoundException, IOException;

   LookupLocator[] getLocators();

   void addGroup(String... groups) throws IOException;

   String[] getGroups();
}
