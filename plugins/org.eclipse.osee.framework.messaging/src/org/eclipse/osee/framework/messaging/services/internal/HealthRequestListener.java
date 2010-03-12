/*
 * Created on Jan 26, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.services.internal;

import java.util.List;
import java.util.Map;

import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.messaging.OseeMessagingListener;
import org.eclipse.osee.framework.messaging.ReplyConnection;
import org.eclipse.osee.framework.messaging.services.messages.ServiceHealthRequest;

/**
 * @author b1528444
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
