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
	private String serviceId;
	private String serviceVersion;
	private URI broker;
	private ServiceInfoPopulator infoPopulator;
	
	UpdateStatus(ConnectionNode connectionNode, String serviceId, String serviceVersion, URI broker, ServiceInfoPopulator infoPopulator){
		this.connectionNode = connectionNode;
		this.serviceId = serviceId;
		this.serviceVersion = serviceVersion;
		this.broker = broker;
		this.infoPopulator = infoPopulator;
	}
	
	@Override
	public void run() {
		ServiceHealth health = new ServiceHealth();
		health.setBrokerURI(broker.toASCIIString());
		health.setServiceId(serviceId);
		health.setServiceVersion(serviceVersion);
		infoPopulator.updateServiceInfo(health.getServiceDescription());
		connectionNode.send(BaseMessages.ServiceHealth, health, new OseeMessagingStatusImpl(
				String.format("Failed to send %s to %s [%s]", BaseMessages.ServiceHealth.getName(), serviceId, serviceVersion),
				UpdateStatus.class));
	}

}
