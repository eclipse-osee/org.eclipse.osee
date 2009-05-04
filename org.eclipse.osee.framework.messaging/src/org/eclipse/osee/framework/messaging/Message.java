/*
 * Created on Apr 10, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging;

import org.eclipse.osee.framework.messaging.id.MessageId;

/**
 * @author b1528444
 *
 */
public interface Message {
   Source getSource();
   MessageId getId();
}
