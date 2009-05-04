/*
 * Created on Apr 10, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.id;


/**
 * @author Andrew M. Finkbeiner
 *
 */
public interface MessageId {
   Namespace getNamespace();
   Name getName();
}
