/*
 * Created on Jan 20, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.internal;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.future.NodeInfo;

/**
 * @author b1528444
 *
 */
public class DefaultNodeInfos {
	
	public static String OSEE_JMS_DEFAULT_PORT = "61616";
	public static NodeInfo OSEE_JMS_DEFAULT;
	public static NodeInfo OSEE_VM;
	
	static{
		try {
			OSEE_JMS_DEFAULT = new NodeInfo("osee-jms", new URI("tcp://localhost:" + OSEE_JMS_DEFAULT_PORT));
			OSEE_VM = new NodeInfo("osee-vm", new URI("vm://localhost?broker.persistent=false"));
		} catch (URISyntaxException ex) {
			OseeLog.log(DefaultNodeInfos.class, Level.SEVERE, ex);
		}
	}
	
}
