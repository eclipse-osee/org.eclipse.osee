/*
 * Created on Jan 26, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.services.internal;

import java.net.URI;

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
	private String serviceName;
	private String serviceVersion;
	private String serviceUniqueId;
	private URI broker;
	private ServiceInfoPopulator infoPopulator;
	private int refreshRateInSeconds;
	
	UpdateStatus(ConnectionNode connectionNode, String serviceName, String serviceVersion, String serviceUniqueId, URI broker, int refreshRateInSeconds, ServiceInfoPopulator infoPopulator){
		this.connectionNode = connectionNode;
		this.serviceName = serviceName;
		this.serviceUniqueId = serviceUniqueId;
		this.serviceVersion = serviceVersion;
		this.broker = broker;
		this.refreshRateInSeconds = refreshRateInSeconds;
		this.infoPopulator = infoPopulator;
	}
	
	@Override
	public void run() {
		ServiceHealth health = new ServiceHealth();
		health.setBrokerURI(broker.toASCIIString());
		health.setServiceName(serviceName);
		health.setServiceVersion(serviceVersion);
		health.setServiceUniqueId(serviceUniqueId);
		health.setRefreshRateInSeconds(refreshRateInSeconds);
		infoPopulator.updateServiceInfo(health.getServiceDescription());
		connectionNode.send(BaseMessages.ServiceHealth, health, new OseeMessagingStatusImpl(
				String.format("Failed to send %s to %s v[%s][%s]", BaseMessages.ServiceHealth.getName(), serviceName, serviceVersion, serviceUniqueId),
				UpdateStatus.class));
	}

}
