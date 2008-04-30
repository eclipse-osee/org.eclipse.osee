/*
 * Created on Apr 11, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.resource.management;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * @author Roberto E. Escobar
 */
public interface IResource {

   public InputStream getContent() throws IOException;

   public URI getLocation();

   public String getName();

   public boolean isCompressed();

}
