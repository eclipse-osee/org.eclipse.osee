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
package org.eclipse.osee.framework.messaging.services.internal;

import java.util.List;
import java.util.Map;

import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.messaging.OseeMessagingListener;
import org.eclipse.osee.framework.messaging.ReplyConnection;
import org.eclipse.osee.framework.messaging.services.messages.ServiceHealthRequest;

/**
 * @author Andrew M. Finkbeiner
 * 
 */
public class HealthRequestListener extends OseeMessagingListener {
	private CompositeKeyHashMap<String, String, List<UpdateStatus>> mapForReplys;

	public HealthRequestListener(
			CompositeKeyHashMap<String, String, List<UpdateStatus>> mapForReplys) {
		super(ServiceHealthRequest.class);
		this.mapForReplys = mapForReplys;
	}

	@Override
	public void process(Object message, Map<String, Object> headers,
			ReplyConnection replyConnection) {
		if (replyConnection.isReplyRequested()) {
			ServiceHealthRequest request = (ServiceHealthRequest)message;
			List<UpdateStatus> updates = mapForReplys.get(request.getServiceName(), request.getServiceVersion());
			if(updates != null){
				for(UpdateStatus update:updates){
					if(update != null){
						update.run();
					}
				}
			}
		}
	}

}
