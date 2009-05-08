/*
 * Created on Apr 10, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging;

import java.io.Serializable;
import org.eclipse.osee.framework.messaging.id.MessageId;

/**
 * @author Andrew M. Finkbeiner
 *
 */
public interface Message extends Serializable {
   Source getSource();
   MessageId getId();
}
