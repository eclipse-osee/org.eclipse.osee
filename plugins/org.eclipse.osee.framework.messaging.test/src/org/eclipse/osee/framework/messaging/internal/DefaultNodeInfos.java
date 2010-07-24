/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.messaging.internal;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.NodeInfo;

/**
 * @author Andrew M. Finkbeiner
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
