package org.eclipse.osee.ote.ui.message.watch;

import org.eclipse.osee.ote.message.MessageDefinitionProvider;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

class WatchViewMessageDefinitionProviderTracker extends ServiceTracker<MessageDefinitionProvider, MessageDefinitionProvider> {

	private final WatchView view;
	
	public WatchViewMessageDefinitionProviderTracker(BundleContext context, WatchView view){
		super(context, MessageDefinitionProvider.class.getName(), null);
		this.view = view;
	}

	@Override
	public MessageDefinitionProvider addingService(ServiceReference<MessageDefinitionProvider> reference) {
		MessageDefinitionProvider provider = super.addingService(reference);
		view.addMessageDefinitionProvider(provider);
		return provider;
	}

	@Override
	public void removedService(ServiceReference<MessageDefinitionProvider> reference, MessageDefinitionProvider service) {
		view.removeMessageDefinitionProvider(service);
		super.removedService(reference, service);
	}
	
}
