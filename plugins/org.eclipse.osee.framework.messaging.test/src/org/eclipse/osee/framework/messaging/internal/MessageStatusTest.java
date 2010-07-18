/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.messaging.internal;

import org.eclipse.osee.framework.messaging.OseeMessagingStatusCallback;

/**
 * @author Andrew M. Finkbeiner
 * 
 */
public class MessageStatusTest implements OseeMessagingStatusCallback {

	private volatile boolean isDone = false;
	private volatile boolean timedOut = false;
	private volatile boolean waitedOnStatus = false;
 	private boolean shouldPass;

	public MessageStatusTest(boolean shouldPass){
		this.shouldPass = shouldPass;
	}
	
	@Override
	public void fail(Throwable th) {
	   if(waitedOnStatus){
         return;
      }
      
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
	   if(waitedOnStatus){
	      return;
	   }
	   
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
		waitedOnStatus = true;
	}
}
