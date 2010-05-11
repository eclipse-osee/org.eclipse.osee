/*
 * Created on Jan 25, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.services;

import java.util.List;

import org.eclipse.osee.framework.messaging.services.messages.ServiceDescriptionPair;

/**
 * @author Andrew M. Finkbeiner
 *
 */
public interface ServiceInfoPopulator {
	void updateServiceInfo(List<ServiceDescriptionPair> serviceDescription);
}
