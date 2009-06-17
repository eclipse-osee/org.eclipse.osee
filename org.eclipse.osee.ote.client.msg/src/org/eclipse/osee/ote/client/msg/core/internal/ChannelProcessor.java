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
package org.eclipse.osee.ote.client.msg.core.internal;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.client.msg.core.db.AbstractMessageDataBase;
import org.eclipse.osee.ote.client.msg.core.db.MessageInstance;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.enums.MemType;

/**
 * Handles processing of updates from a channel. The channel processor maintains an internal queue whose max size
 * dictates the maximum number of concurrent updates. All updates are submitted to the given thread pool for execution.
 * 
 * @author Ken J. Aguilar
 */
final public class ChannelProcessor {
	private final ArrayBlockingQueue<Task> queue;
	private final ExecutorService threadPool;
	private final AbstractMessageDataBase msgDb;
	private final MemType memType;

	/**
	 * A task allows each channel to have multiple updates processed
	 * concurrently. Each task has its own buffers.
	 * 
	 * @author Ken J. Aguilar
	 */
	private final class Task implements Runnable {
		private final ByteBuffer buffer;
		private final byte[] nameBuffer = new byte[128];

		public Task(ByteBuffer buffer) {
			this.buffer = buffer;
		}

		public void prepTask(ReadableByteChannel channel) throws IOException {
			buffer.clear();
			// read the data from the channel into the buffer
			channel.read(buffer);
			buffer.flip();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			try {
				final int id = buffer.getInt();
				final long time = buffer.getLong();
				final MessageInstance instance = msgDb.findById(id);
				if (instance != null) {
					onUpdate(instance, buffer, time);
				}
				// return to the queue
				queue.put(this);
			} catch (InterruptedException e) {
				// do nothing
			} catch (Exception ex) {
				OseeLog.log(Activator.class, Level.SEVERE, "failed to process message update", ex);
			}
		}

	}

	public ChannelProcessor(int depth, int bufferSize, ExecutorService threadPool, AbstractMessageDataBase msgDb, MemType memType) {
		this.queue = new ArrayBlockingQueue<Task>(depth);
		try {
			// fill the queue with pre-allocated tasks
			for (int i = 0; i < depth; i++) {
				queue.put(new Task(ByteBuffer.allocateDirect(bufferSize)));
			}
		} catch (InterruptedException ex) {
			throw new Error("should never happen", ex);
		}
		this.threadPool = threadPool;
		this.msgDb = msgDb;
		this.memType = memType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.osee.ote.ui.message.internal.core.IChannelHandler#process
	 * (java.nio.channels.SelectionKey)
	 */
	public final void process(final ReadableByteChannel channel) throws InterruptedException, IOException {
		// get a free task
		final Task task = queue.take();

		// prep the task
		task.prepTask(channel);

		// the task is now ready for execution, submit it to the thread pool
		threadPool.submit(task);
	}

	/**
	 * called when there is data to be processed from a channel. Can be called
	 * by one or more threads for the same data concurrently to so implementors
	 * need to be thread safe
	 * 
	 * @param data
	 * @param buffer
	 * @param time
	 */
	protected void onUpdate(MessageInstance instance, ByteBuffer buffer, long time) {
		MessageData msgData = instance.getMessage().getActiveDataSource(memType);
		if (msgData != null) {
			byte[] data = msgData.getMem().getData();
			int remaining = buffer.remaining();
			if (data.length < remaining) {
				OseeLog.log(Activator.class, Level.WARNING, String.format("Message [%s] changed it's backing data size from [%d] to [%d].", instance
						.getMessage().getName(),
						data.length, remaining));
				data = new byte[remaining];
				buffer.get(data, 0, remaining);
				msgData.setNewBackingBuffer(data);
				return;
			}  
			
			if (remaining < data.length) {
				Arrays.fill(data, remaining, data.length, (byte) 0);
				// msg.getActiveDataSource().setCurrentLength(remaining);
			}
			buffer.get(data, 0, remaining);
			msgData.setCurrentLength(remaining);
			msgData.incrementActivityCount();
			msgData.notifyListeners();
		}
	}

}
