/*
 * Created on Jul 27, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.future;

import java.util.Properties;

/**
 * @author b1528444
 *
 */
public interface OseeMessagingListener {
   public void process(Properties message);
}
