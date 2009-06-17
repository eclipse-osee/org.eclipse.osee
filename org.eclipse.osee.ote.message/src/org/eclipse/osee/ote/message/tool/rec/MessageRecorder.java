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
package org.eclipse.osee.ote.message.tool.rec;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.environment.TestEnvironmentInterface;
import org.eclipse.osee.ote.message.MessageSystemTestEnvironment;
import org.eclipse.osee.ote.message.internal.Activator;
import org.eclipse.osee.ote.message.tool.rec.entry.IMessageEntry;

public class MessageRecorder {
   private static final int NUM_BUFFERS = 3;
	private WritableByteChannel channel;

	private final ArrayList<IMessageEntry> msgsToRecord = new ArrayList<IMessageEntry>(64);
	private final Lock recLock = new ReentrantLock();
	private volatile boolean isRecording = false;
	private final IMessageEntryFactory factory;
	
	private final ExecutorService taskHandler = Executors.newFixedThreadPool(2);
	private final WeakReference<TestEnvironmentInterface> env;
	private final ArrayBlockingQueue<ByteBuffer> bufferQueue = new ArrayBlockingQueue<ByteBuffer>(NUM_BUFFERS);
	
	public MessageRecorder(IMessageEntryFactory factory) {
		this.factory = factory;
		this.env = new WeakReference<TestEnvironmentInterface>(Activator.getTestEnvironment());
		try {
         for (int i = 0; i < NUM_BUFFERS; i++) {
            bufferQueue.put(ByteBuffer.allocateDirect(256000));
	}
      } catch (InterruptedException e) {
         // this should be absolutely impossible
         throw new Error("What on Earth has happened here!", e);
      }
	}

	public void startRecording(Collection<MessageRecordConfig> list, WritableByteChannel channel) throws InterruptedException{
		if (list == null) {
			throw new IllegalArgumentException("list cannot be null");
		}
		if (channel == null) {
			throw new IllegalArgumentException("channel cannot be null");
		}
		recLock.tryLock(2, TimeUnit.SECONDS);
        this.channel = channel;
		try {			
			for (MessageRecordConfig config : list) {
				IMessageEntry handler = factory.createMessageEntry(config, this);
				msgsToRecord.add(handler);
				handler.enable(true);
			}
			 OseeLog.log(MessageSystemTestEnvironment.class,
	     			 Level.INFO,  
	     			"recording " + list.size() + "messages");
			isRecording = true;
		} finally {
			recLock.unlock();
		}
	}
	
	public ByteBuffer acquireOutputBuffer() throws InterruptedException {
      return bufferQueue.take();
   }

   public void releaseOutputBuffer(ByteBuffer buffer) throws InterruptedException {
      bufferQueue.put(buffer);
   }
	
	public WritableByteChannel getChannel() {
		return channel;
	}

	public Future<?> submitTask(Runnable task) {
		return taskHandler.submit(task);
	}
	
	public boolean isRecording() {
		return isRecording;
	}

	public long getTimeStamp() {
		return env.get().getEnvTime();
	}
	
	public void stopRecording(boolean closeOutputChannel) throws IOException, InterruptedException {
		 OseeLog.log(MessageSystemTestEnvironment.class,
     			 Level.INFO,  
     			"stopping message recorder...");
		isRecording = false;
		recLock.tryLock(2, TimeUnit.SECONDS);
		try {
			for (IMessageEntry handler : msgsToRecord) {
				handler.enable(false);
			}
			msgsToRecord.clear();
			if (closeOutputChannel) {
				channel.close();
			}
		} finally {
			recLock.unlock();
			 OseeLog.log(MessageSystemTestEnvironment.class,
	     			 Level.INFO,  
	     			"message recorder stopped");
		}
	}
}
