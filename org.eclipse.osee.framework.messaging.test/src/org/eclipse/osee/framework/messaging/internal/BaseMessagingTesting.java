/*
 * Created on Jul 30, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.internal;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;

import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.Component;
import org.eclipse.osee.framework.messaging.MessagingTracker;
import org.eclipse.osee.framework.messaging.OseeMessaging;
import org.eclipse.osee.framework.messaging.OseeMessagingListener;
import org.eclipse.osee.framework.messaging.SystemTopic;

/**
 * @author b1528444
 * 
 */
public class BaseMessagingTesting {
	private MessagingTracker tracker;

	protected BaseMessagingTesting() {
		tracker = new MessagingTracker();
		tracker.open();
	}

	protected void startBroker() {
		try{
			String really = System.getProperty("eclipse.home.location");
			URL url = new URL(really);
			String exe = null;
			if(Lib.isWindows()){
				exe = "eclipse.exe";
			} else {
				exe = "eclipse";
			}
			ProcessBuilder builder = new ProcessBuilder(url.getPath() + exe, "-console", "-application", "jms.activemq.launch.RunActiveMq" );
			builder.directory(new File(url.getPath()));
			builder.redirectErrorStream(true);
			Process process = builder.start();
			Lib.handleProcessNoWait(process, new PrintWriter(System.out));
			Thread.sleep(10000);
		} catch (MalformedURLException ex) {
			OseeLog.log(BaseMessagingTesting.class, Level.SEVERE, ex);
			fail(ex.getMessage());
		} catch (IOException ex) {
			OseeLog.log(BaseMessagingTesting.class, Level.SEVERE, ex);
			fail(ex.getMessage());
		} catch (InterruptedException ex) {
			OseeLog.log(BaseMessagingTesting.class, Level.SEVERE, ex);
			fail(ex.getMessage());
		}
	}

	protected void stopBroker() {
		try {
			getMessaging().sendMessage(Component.JMS, SystemTopic.KILL_TEST_JMS_BROKER,
					"kill", new MessageStatusTest(true));
			Thread.sleep(10000);
		} catch (InterruptedException ex) {
			OseeLog.log(BaseMessagingTesting.class, Level.SEVERE, ex);
			fail(ex.getMessage());
		}
	}

	protected final OseeMessaging getMessaging() {
		OseeMessaging messaging = null;
		try {
			messaging = (OseeMessaging) tracker.waitForService(15000);
		} catch (InterruptedException ex) {
			fail("Failed to get messaging service. " + ex.getMessage());
		}
		assertTrue(messaging != null);
		return messaging;
	}

	protected void testJMSSendShouldFail(OseeMessaging messaging) {
		MessageStatusTest status = new MessageStatusTest(false);
			messaging.sendMessage(Component.JMS, "test", "test", status);
		status.waitForStatus(5000);
	}

	protected void testJMSSendShouldPass(OseeMessaging messaging) {
		MessageStatusTest status = new MessageStatusTest(true);
		messaging.sendMessage(Component.JMS, "test", "test", status);
		status.waitForStatus(5000);
	}

	protected void testJMSSubscribeShouldFail(OseeMessaging messaging) {
		MessageStatusTest status = new MessageStatusTest(false);
			messaging.addListener(Component.JMS, "test2",
					new OseeMessagingListener() {
						@Override
						public void process(Properties message) {
						}
					}, status);
		status.waitForStatus(5000);
	}

	protected void testJMSSubscribeShouldPass(OseeMessaging messaging) {
		MessageStatusTest status = new MessageStatusTest(true);
			messaging.addListener(Component.JMS, "test2",
					new OseeMessagingListener() {
						@Override
						public void process(Properties message) {
						}
					}, status);
			status.waitForStatus(5000);
	}
}
