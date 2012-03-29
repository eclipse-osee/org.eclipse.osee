package org.eclipse.osee.ote.ui.message.internal;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.osee.ote.message.MessageDefinitionProvider;
import org.eclipse.osee.ote.ui.message.watch.WatchView;

public class MessageProviderComponent {

	private static MessageProviderComponent instance; 
	
	private List<MessageDefinitionProvider> providers;
	private WatchView watchView;
	
	public static MessageProviderComponent getInstance(){
		return instance;
	}
	
	public void start(){
		instance = this;
	}
	
	public void stop(){
		instance = null;
	}
	
	public MessageProviderComponent() {
		providers = new CopyOnWriteArrayList<MessageDefinitionProvider>();
	}
	
	public void addMessageDefinitionProvider(MessageDefinitionProvider provider){
		providers.add(provider);
		if(watchView != null){
			this.watchView.addMessageDefinitionProvider(provider);
		}
	}
	
	public void removeMessageDefinitionProvider(MessageDefinitionProvider provider){
		providers.remove(provider);
		if(watchView != null){
			this.watchView.removeMessageDefinitionProvider(provider);
		}
	}
	
	public void set(WatchView watchView){
		this.watchView = watchView;
		for(MessageDefinitionProvider provider:providers){
			this.watchView.addMessageDefinitionProvider(provider);
		}
	}
	
	public void unset(WatchView watchView){
		for(MessageDefinitionProvider provider:providers){
			this.watchView.removeMessageDefinitionProvider(provider);
		}
		this.watchView = null;
	}
	
}
