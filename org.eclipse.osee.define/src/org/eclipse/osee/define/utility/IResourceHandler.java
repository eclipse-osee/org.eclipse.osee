/*
 * Created on Mar 26, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.define.utility;

import java.net.URI;
import java.nio.CharBuffer;

/**
 * @author Roberto E. Escobar
 */
public interface IResourceHandler {

   void onResourceFound(URI uriPath, String name, CharBuffer fileBuffer);
}
