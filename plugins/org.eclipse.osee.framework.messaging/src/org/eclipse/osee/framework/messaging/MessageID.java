/*
 * Created on Jan 25, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging;

import org.eclipse.osee.framework.core.data.Named;

/**
 * @author Andrew M. Finkbeiner
 *
 */
public interface MessageID extends Named {
	Class<?> getSerializationClass();
	boolean isReplyRequired();
	boolean isTopic();
	String getId();
}
