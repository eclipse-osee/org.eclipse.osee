/*
 * Created on Jan 15, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging;

import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public interface MessageServiceProvider {

   MessageService getMessageService() throws OseeCoreException;

}
