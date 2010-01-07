/*
 * Created on Oct 19, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package jms.activemq.launch;

import java.util.List;
import java.util.logging.Level;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.camel.CamelConnectionFactory;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RouteDefinition;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.osee.framework.logging.OseeLog;

public class RunActiveMq implements IApplication {
	private static String BROKER_URI = "tcp://localhost:61616";
	private BrokerService broker;
	private CamelConnectionFactory activeMqRemoteConnectionFactory;
	
	private boolean isKillable = true;
	
	@Override
	public Object start(IApplicationContext appContext) throws Exception {
		broker = new BrokerService();
		broker.setBrokerName("osee");
		broker.setUseShutdownHook(true);
		broker.addConnector(BROKER_URI);
		broker.start();
		
		if(isKillable){
			
			 DefaultCamelContext context = new DefaultCamelContext();
			 activeMqRemoteConnectionFactory = new CamelConnectionFactory();
		     activeMqRemoteConnectionFactory.setBrokerURL(BROKER_URI);
	
		     activeMqRemoteConnectionFactory.setAlwaysSessionAsync(true);
		     activeMqRemoteConnectionFactory.setAlwaysSyncSend(false);
		     /*
		      * if we use failover: sends pend not sure about route adding, probably pend also.  Need to thread those operations so we don't block applications
		      * use transport listener for lifecycle events
		      */
		     activeMqRemoteConnectionFactory.setCamelContext(context);
		     activeMqRemoteConnectionFactory.getCamelContext().addComponent("osee-jms",
		           JmsComponent.jmsComponent(activeMqRemoteConnectionFactory));
		     context.start();
	
		     activeMqRemoteConnectionFactory.getCamelContext().addRoutes(new RouteBuilder() {
		        @Override
		        public void configure() {
		           from("osee-jms:" + "topic:jms.kill.broker").process(new Processor() {
	
		              @Override
		              public void process(Exchange exchange) throws Exception {
		                 stopBrokerInNewThread();
		              }
	
		           });
		        }
		     });
			
			while(broker.isStarted()){
				Thread.sleep(1000);
			}
		} else {
			while(broker.isStarted()){
				Thread.sleep(60000);
			}
		}
		return null;
	}

	@Override
	public void stop() {
		try {
			broker.stop();
		} catch (Exception ex) {
			OseeLog.log(RunActiveMq.class, Level.SEVERE, ex);
		}
	}

  void stopBrokerInNewThread() throws Exception {
     new Thread(new StopIt()).start();
  }

  private class StopIt implements Runnable {
     @Override
     public void run() {
        try {
           broker.stop();
           List<RouteDefinition> defs = activeMqRemoteConnectionFactory.getCamelContext().getRouteDefinitions();
           for (RouteDefinition def : defs) {
              def.stop();
           }
           activeMqRemoteConnectionFactory.getCamelContext().removeRouteDefinitions(defs);
           activeMqRemoteConnectionFactory.getCamelContext().stop();
        } catch (Throwable th) {
           th.printStackTrace();
        }
     }
  }
	
}
