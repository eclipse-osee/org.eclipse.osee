/*
 * Created on Jan 26, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.services.internal;

import java.net.URI;
import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.future.ConnectionNode;
import org.eclipse.osee.framework.messaging.services.BaseMessages;
import org.eclipse.osee.framework.messaging.services.ServiceInfoPopulator;
import org.eclipse.osee.framework.messaging.services.messages.ServiceHealth;

/**
 * @author b1528444
 *
 */
public class UpdateStatus implements Runnable {

	private ConnectionNode connectionNode;
	private ServiceInfoPopulator infoPopulator;
	private ServiceHealth health;
	private String errorMsg;
	
	UpdateStatus(ConnectionNode connectionNode, String serviceName, String serviceVersion, String serviceUniqueId, URI broker, int refreshRateInSeconds, ServiceInfoPopulator infoPopulator){
		this.connectionNode = connectionNode;
		health = new ServiceHealth();
		health.setBrokerURI(broker.toASCIIString());
		health.setServiceName(serviceName);
		health.setServiceVersion(serviceVersion);
		health.setServiceUniqueId(serviceUniqueId);
		health.setRefreshRateInSeconds(refreshRateInSeconds);
		errorMsg = String.format("Failed to send %s to %s v[%s][%s]", BaseMessages.ServiceHealth.getName(), health.getServiceName(),
				health.getServiceVersion(), health.getServiceUniqueId());
		this.infoPopulator = infoPopulator;
	}
	
	@Override
	public void run() {
		try {
			health.getServiceDescription().clear();
			infoPopulator.updateServiceInfo(health.getServiceDescription());
			connectionNode.send(BaseMessages.ServiceHealth, health, new OseeMessagingStatusImpl(errorMsg, UpdateStatus.class));
		} catch (Exception ex) {
			OseeLog.log(UpdateStatus.class, Level.SEVERE, ex);
		}
	}

}
