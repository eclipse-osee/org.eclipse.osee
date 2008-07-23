package org.eclipse.osee.connection.service;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventNotifier {

	private final ExecutorService executorService = Executors.newCachedThreadPool();

	private final CopyOnWriteArrayList<IConnectorListener> connectorListener = new CopyOnWriteArrayList<IConnectorListener>();

	void notifyConnectorsAdded(Collection<IServiceConnector> connectors) {
		for (IConnectorListener listener : connectorListener) {
			try {
				listener.onConnectorsAdded(connectors);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	void notifyConnectorRemoved(IServiceConnector connector) {
		for (IConnectorListener listener : connectorListener) {
			try {
				listener.onConnectorRemoved(connector);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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
