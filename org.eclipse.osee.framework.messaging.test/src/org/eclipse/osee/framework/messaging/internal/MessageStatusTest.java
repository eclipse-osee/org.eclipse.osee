/*
 * Created on Oct 19, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.internal;

import org.eclipse.osee.framework.messaging.OseeMessagingStatusCallback;

/**
 * @author b1528444
 * 
 */
public class MessageStatusTest implements OseeMessagingStatusCallback {

	private volatile boolean isDone = false;
	private volatile boolean timedOut = false;
	private boolean shouldPass;

	public MessageStatusTest(boolean shouldPass){
		this.shouldPass = shouldPass;
	}
	
	@Override
	public void fail(Throwable th) {
		if(timedOut){
			return;
		}
		if(shouldPass){
			org.junit.Assert.fail(th.getMessage());	
		} else {
			org.junit.Assert.assertTrue(true);
		}
		isDone = true;
	}

	@Override
	public void success() {
		if(timedOut){
			return;
		}
		if(shouldPass){
			org.junit.Assert.assertTrue(true);
		} else {
			org.junit.Assert.fail("We had a status of 'success'");
		}
		isDone = true;
	}

	public void waitForStatus(int time) {
		long timeout = System.currentTimeMillis() + time;
		while (!isDone && System.currentTimeMillis() < timeout) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException ex) {
			}
		}
		if (!isDone) {
			timedOut = true;
			if(shouldPass){
				org.junit.Assert.fail("We timed out waiting for status.");
			} else {
				org.junit.Assert.assertTrue(true);
			}
		}
	}
}
