package org.eclipse.osee.ote.ui.message.view;

import org.eclipse.osee.ote.message.MessageDefinitionProvider;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

class MessageDefinitionProviderTracker extends ServiceTracker<MessageDefinitionProvider, MessageDefinitionProvider> {

	private final MessageView view;
	
	public MessageDefinitionProviderTracker(BundleContext context, MessageView view){
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
