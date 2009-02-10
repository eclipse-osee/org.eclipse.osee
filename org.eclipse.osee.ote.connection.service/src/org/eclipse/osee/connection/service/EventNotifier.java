/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.connection.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

public class EventNotifier {

    private final ExecutorService executorService = Executors
    .newCachedThreadPool();

    private final CopyOnWriteArrayList<IConnectorListener> connectorListener = new CopyOnWriteArrayList<IConnectorListener>();

    void notifyConnectorsAdded(final Collection<IServiceConnector> connectors) {
	executorService.submit(new Runnable() {
	    // copy the collection of new connectors
	    final ArrayList<IServiceConnector> newConnectors = new ArrayList<IServiceConnector>(
		    connectors);
	    @Override
	    public void run() {
		for (IConnectorListener listener : connectorListener) {
		    try {
			listener.onConnectorsAdded(newConnectors);
		    } catch (Exception e) {
			Activator
				.log(
					Level.SEVERE,
					"Error processing listeners for connector added event",
					e);
		    }
		}
	    }
	});

    }

    void notifyConnectorRemoved(final IServiceConnector connector) {
	executorService.submit(new Runnable() {

	    @Override
	    public void run() {
		for (IConnectorListener listener : connectorListener) {
		    try {
			listener.onConnectorRemoved(connector);
		    } catch (Exception e) {
			Activator
			.log(
				Level.SEVERE,
				"Error processing listeners for connector remove event",
				e);
		    }
		}
	    }

	});

    }

    void notifyServiceStopped() {
	for (IConnectorListener listener : connectorListener) {
	    listener.onConnectionServiceStopped();
	}
    }

    void addListener(IConnectorListener listener) {
	connectorListener.add(listener);
    }

    void removeListener(IConnectorListener listener) {
	connectorListener.remove(listener);
    }
}
