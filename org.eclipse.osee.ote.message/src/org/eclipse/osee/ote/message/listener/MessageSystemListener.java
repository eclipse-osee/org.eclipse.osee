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
package org.eclipse.osee.ote.message.listener;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.eclipse.osee.framework.jdk.core.util.benchmark.Benchmark;
import org.eclipse.osee.ote.core.environment.interfaces.ICancelTimer;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.core.environment.interfaces.ITimeout;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.MessageSystemException;
import org.eclipse.osee.ote.message.condition.ICondition;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.elements.MsgWaitResult;
import org.eclipse.osee.ote.message.enums.MemType;
import org.eclipse.osee.ote.message.interfaces.IOSEEMessageReaderListener;
import org.eclipse.osee.ote.message.interfaces.IOSEEMessageWriterListener;

/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public class MessageSystemListener implements IOSEEMessageReaderListener,
IOSEEMessageWriterListener, ITimeout {
	private volatile boolean isTimedOut = false;
	private int masterMessageCount = 0;
//	private final Message message;
	private WeakReference<Message<?,?,?>> message;
	private static final Benchmark tbm = new Benchmark("Total Message System Listener", 2500);

	private int messageCount = 0;

//	public static enum SPEED {
////	FAST, SLOW
//	};

	/**
	 * A thread pool for handling slow listeners. We start the pool with 5
	 * threads, which should in most cases be more than enough threads to handle
	 * the listeners. Because the queue is static, it will be shared by all
	 * "slow listeners". Because of the expense of thread creation, we want to
	 * avoid creating threads when possible. To accomplish this, we start with
	 * more threads than we think we'll need, and we keep any newly created
	 * threads around for a long period of time. We assume that if we need a lot
	 * of threads now, we may continue to need a lot of threads for an extended
	 * period of time.
	 * <p>
	 * We use a SynchronousQueue in order to avoid queueing. We prefer to create
	 * a new thread to handle requests if necessary to help ensure that
	 * listeners are notified as quickly as possible and without delays caused
	 * by other listeners.
	 */
	private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(5,
			Integer.MAX_VALUE, 60 * 30, TimeUnit.SECONDS,
			new SynchronousQueue<Runnable>());

	private final CopyOnWriteArrayList<IOSEEMessageListener> fastListeners = new CopyOnWriteArrayList<IOSEEMessageListener>();
	private final CopyOnWriteArrayList<IOSEEMessageListener> slowListeners = new CopyOnWriteArrayList<IOSEEMessageListener>();
	/**
	 * This class takes in a message in the constructor so that it can tell the
	 * message to update when it recieves new data.
	 */
	public MessageSystemListener(Message<?,?,?> msg) {
		super();
		this.message = new WeakReference<Message<?,?,?>>(msg);  
	}

	/**
	 * returns the number of received messages since the last call to waitForData
	 * 
	 * @return int   
	 */
	public synchronized int getLocalMessageCount() {
		return messageCount;
	}

	public synchronized int getMasterMessageCount() {
		return masterMessageCount;
	}

	/**
	 * return whether new data has been received since the last call to waitForData
	 * 
	 * @return boolean
	 */
	public boolean isTimedOut() {
		return this.isTimedOut;
	}

	public void setTimeout(boolean timeout) {
		this.isTimedOut = timeout;
	}

	public synchronized boolean waitForData() throws InterruptedException {
		messageCount = 0;
		if(this.isTimedOut)
			return true;
		while (messageCount == 0 && !isTimedOut) {
			wait(); // the test environment will notify us after a specified time out
		}
		return isTimedOut;
	}

	public synchronized boolean waitForMessageNumber(int count) throws InterruptedException
	{
		while(masterMessageCount < count)
		{
			messageCount = 0;
			wait();// onDataAvailable
			if(isTimedOut()){//we timed out
				return false;
			}
		}
		return true;
	}

   public MsgWaitResult waitForCondition(ITestEnvironmentAccessor accessor, ICondition condition, boolean maintain, int milliseconds) throws InterruptedException {
      long time = accessor.getEnvTime();
      boolean pass = condition.check();
      if (milliseconds > 0) {
         boolean done = pass ^ maintain;
         final ICancelTimer cancelTimer = accessor.setTimerFor(this, milliseconds);
         while (!done) {
            if (waitForData()) {
               // we timed out
               break;
            } else {
               pass = condition.checkAndIncrement();
               done = pass ^ maintain;
            }
         }
         cancelTimer.cancelTimer();
      }
      time = accessor.getEnvTime() - time;

      return new MsgWaitResult(time, condition.getCheckCount(), pass);
   }
   
	/**
	 * Registers a listener for the message. If the listener will not respond
	 * quickly (for example, if the listener is going to make RMI calls, or
	 * other network activites which it will wait for the remote side to
	 * respond), then it should identify itself as a slow listener by passing
	 * "false" for isFastListener. "Slow" listeners will be notified by a
	 * separate thread, thereby not forcing other listener notifications to be
	 * delayed, and subsequent messages from being processed.
	 * 
	 * @param listener - The listener to be added
	 * @param listenerSpeed - 
	 * @return Returns boolean success indication.
	 */
	public boolean addListener(IOSEEMessageListener listener, SPEED listenerSpeed) {
		Collection<IOSEEMessageListener> c = (listenerSpeed == SPEED.FAST) ? fastListeners : slowListeners;
		if (!c.contains(listener)) {
			c.add(listener);
		}
		return true;
	}

	/**
	 * Adds the listener as a "fast" listener.
	 * 
	 * @see MessageSystemListener#addListener(IOSEEMessageListener, SPEED)
	 */
	public boolean addListener(IOSEEMessageListener listener) {
		return addListener(listener, SPEED.FAST);
	}

	/**
	 * Checks to see if the specified listener is registered
	 * @param listener
	 * @param listenerSpeed
	 * @return true if the listener is register false otherwise
	 */
	public boolean containsListener(final IOSEEMessageListener listener, final SPEED listenerSpeed) {
		return (listenerSpeed.equals(SPEED.FAST)) ? fastListeners.contains(listener) : slowListeners.contains(listener);
	}

	/**
	 * Convience method. 
	 * @param listener
	 * @return Returns presence boolean indication.
	 * @see #containsListener(IOSEEMessageListener, SPEED)
	 */
	public boolean containsListener(final IOSEEMessageListener listener) {
		return containsListener(listener, SPEED.FAST);

	}

	public boolean removeListener(IOSEEMessageListener listener,
			SPEED listenerSpeed) {

		return (listenerSpeed == SPEED.FAST) ? fastListeners.remove(listener) : slowListeners.remove(listener);
	}

	public boolean removeListener(IOSEEMessageListener listener) {
		return (removeListener(listener, SPEED.FAST) || removeListener(
				listener, SPEED.SLOW));
	}

	public synchronized void onDataAvailable(final MessageData data, MemType type) throws MessageSystemException{

		tbm.startSample();
		if (message.get().getMemType() == type) {
			messageCount++;
			masterMessageCount++; 
			notifyAll();  
		}

		for (IOSEEMessageListener listener : fastListeners){
				listener.onDataAvailable(data, type);
		}
		for (IOSEEMessageListener listener :  slowListeners){
			threadPool.execute(new SlowListenerNotifier(listener,
					data, type, false));
		}
		tbm.endSample();
	}

	public synchronized void onInitListener() throws MessageSystemException{

		for (IOSEEMessageListener listener : fastListeners){
			listener.onInitListener();
		}
		for (IOSEEMessageListener listener :  slowListeners){
			threadPool.execute(new SlowListenerNotifier(listener, null,
					null, true));
		}
	}

	/**
	 * Manages the notification of a slow IOSEEMessageListener. The
	 * implementation prevents multiple calls into the listener at the same
	 * time.
	 * 
	 * @author David Diepenbrock
	 */
	private static final class SlowListenerNotifier implements Runnable {

		/**
		 * Indicates if we are performing the onInitListener() call or
		 * onDataAvailable() call
		 */
		private final boolean isOnInit;

		private final IOSEEMessageListener listener;

		private final MessageData data;

		private final MemType type;

		public SlowListenerNotifier(IOSEEMessageListener listener, MessageData data,
				MemType type, boolean isOnInit) {
			this.listener = listener;
			this.data = data;
			this.type = type;
			this.isOnInit = isOnInit;
		}

		public void run() {
			synchronized (listener) {
				if (isOnInit)
					listener.onInitListener();
				else
					listener.onDataAvailable(data, type);
			}
		}
	}

	public Collection<IOSEEMessageListener> getRegisteredFastListeners() {
		return fastListeners;
	}

	public Collection<IOSEEMessageListener> getRegisteredSlowListeners() {
		return slowListeners;
	}

	public void dispose(){
		this.clearListeners();
	}

	public void clearListeners()
	{
		this.fastListeners.clear();
		this.slowListeners.clear();
	}
}
