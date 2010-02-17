/*
 * Created on Jan 15, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.future;

import java.util.Collection;

import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author b1122182
 */
public interface MessageService {

   ConnectionNode getDefault() throws OseeCoreException;	
	
   ConnectionNode get(NodeInfo nodeInfo) throws OseeCoreException;

   Collection<NodeInfo> getAvailableConnections();
   
   int size();

   boolean isEmpty();

}
