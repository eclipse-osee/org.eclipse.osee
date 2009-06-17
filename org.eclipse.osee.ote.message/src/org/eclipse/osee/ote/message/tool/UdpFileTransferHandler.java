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
package org.eclipse.osee.ote.message.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.message.MessageSystemTestEnvironment;

public class UdpFileTransferHandler {

   private final Thread thread;
   private final Selector selector;
   private volatile boolean done = false;

   private final HashMap<SelectionKey, Handle> handles = new HashMap<SelectionKey, Handle>();
   private final Object gate = new Object();

   private final class Handle implements IFileTransferHandle {
      private final CopyOnWriteArrayList<IUdpTransferListener> listeners =
            new CopyOnWriteArrayList<IUdpTransferListener>();
      private final SelectionKey key;
      private final TransferConfig config;
      private final FileLock lock;

      /**
       * @param key
       * @param config
       * @param lock
       */
      public Handle(SelectionKey key, TransferConfig config, FileLock lock) {
         if (key == null) {
            throw new IllegalArgumentException("The selection key cannot be null");
         }
         if (config == null) {
            throw new IllegalArgumentException("The transfer configuration cannot be null");
         }
         if (lock == null) {
            throw new IllegalArgumentException("The file lock cannot be null");
         }
         this.key = key;
         this.config = config;
         this.lock = lock;
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.ote.message.tool.IFileTransferHandle#addListener(org.eclipse.osee.ote.message.tool.IUdpTransferListener)
       */
      public void addListener(IUdpTransferListener listener) {
         if (!listeners.contains(listener)) {
            listeners.add(listener);
         }
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.ote.message.tool.IFileTransferHandle#stop()
       */
      public void stop() throws IOException {
         synchronized (gate) {
            handles.remove(key);
            key.cancel();
            //selector.wakeup();
            OseeLog.log(MessageSystemTestEnvironment.class,
                  Level.INFO, "stopping transfer for " + config.getFileName());
            key.channel().close();
            lock.release();
            lock.channel().close();
            for (IUdpTransferListener listener : listeners) {
               listener.onTransferComplete(config);
            }
            listeners.clear();
         }
      }

   }

   public UdpFileTransferHandler() throws IOException {
      selector = Selector.open();
      thread = new Thread(new Runnable() {

         /*
          * (non-Javadoc)
          * 
          * @see java.lang.Runnable#run()
          */
         public void run() {
            try {
               while (!done) {
                  selector.select();
                  processReadySet(selector.selectedKeys());
                  synchronized (gate) {
                     // we do this to prevent entry to the selector.select() when
                     // registering new channels from a different thread. Otherwise
                     // we would deadlock
                  }
               }
            } catch (ClosedByInterruptException ex) {
               if (!done) {
                  // interrupted but we were not stopped
                  OseeLog.log(MessageSystemTestEnvironment.class,
                        Level.SEVERE, ex.getMessage(), ex);
               }
            } catch (IOException ex) {
               OseeLog.log(MessageSystemTestEnvironment.class,
                     Level.SEVERE, ex.getMessage(), ex);
            }
         }

      });
      thread.setName("UDP File Transfer Handler");
   }

   /**
    * @param config
    * @return true if successful or false if a lock on the file was not obtained
    * @throws IOException
    * @throws FileNotFoundException
    */
   public IFileTransferHandle registerTransfer(TransferConfig config) throws IOException, FileNotFoundException {
      synchronized (gate) {
         selector.wakeup();
         return addTransfer(config);
      }
   }

   private synchronized IFileTransferHandle addTransfer(final TransferConfig config) throws IOException, FileNotFoundException {
      File file = new File(config.getFileName());
      final FileChannel fileChannel;
      if (config.getDirection() == TransferConfig.Direction.SOCKET_TO_FILE) {
         FileOutputStream fos = new FileOutputStream(file);
         fileChannel = fos.getChannel();
      } else {
         FileInputStream fis = new FileInputStream(file);
         fileChannel = fis.getChannel();
      }
      final FileLock lock = fileChannel.tryLock();
      if (lock == null) {
         return null;
      }
      fileChannel.position(0);
      final DatagramChannel sourceChannel = DatagramChannel.open();
      sourceChannel.configureBlocking(false);
      final DatagramSocket socket = sourceChannel.socket();
      socket.bind(config.getDestinationAddress());
      sourceChannel.connect(config.getSourceAddress());
      System.out.println("file side bind address=" + socket.getLocalAddress() + ":" + socket.getLocalPort());
      System.out.println("connected socket address=" + config.getSourceAddress());

      if (config.getDirection() == TransferConfig.Direction.SOCKET_TO_FILE && socket.getReceiveBufferSize() < config.getBlockCount()) {
         socket.setReceiveBufferSize(config.getBlockCount());
         System.out.println("internal UDP receive buffer size =" + socket.getReceiveBufferSize());
      }
      final SelectionKey key = sourceChannel.register(selector, config.getDirection().getSelectionAccessOperation());
      Handle h = new Handle(key, config, lock);
      handles.put(key, h);
      return h;
   }

   public void start() throws IOException {
      thread.start();
   }

   public void stop(int time) throws InterruptedException, IOException {
      done = true;
      thread.interrupt();
      thread.join(time);
      stopAllTransfers();
      handles.clear();
      selector.close();
   }

   private void processReadySet(final Set<SelectionKey> readySet) throws ClosedByInterruptException, IOException {
      /* iterate through the set of readable channels */
      final Iterator<SelectionKey> keys = readySet.iterator();
      while (keys.hasNext()) {
         final SelectionKey key = keys.next();
         if (key.isValid()) {
            final Handle handle = handles.get(key);
            final FileChannel fileChannel = handle.lock.channel();
            final DatagramChannel channel = (DatagramChannel) key.channel();
            final long pos = fileChannel.position();
            try {
               channel.socket().getReceiveBufferSize();
               if ((key.interestOps() & SelectionKey.OP_READ) > 0) {
                  final long count = fileChannel.transferFrom(channel, pos, handle.config.getBlockCount());
                  fileChannel.position(pos + count);
                  if (count == 0) {
                     System.out.println("warning! read zero bytes");
                  }
               } else if ((key.interestOps() & SelectionKey.OP_WRITE) > 0) {
                  final long count = fileChannel.transferTo(pos, handle.config.getBlockCount(), channel);
                  if (count == 0) {
                     System.out.println("warning! wrote zero bytes");
                  }
                  fileChannel.position(pos + count);
                  if (fileChannel.position() >= fileChannel.size()) {
                     synchronized (this) {
                        System.out.println("done transfering file " + handle.config.getFileName());
                        handle.stop();
                     }
                  }
               }
            } catch (ClosedChannelException ex) {
               handle.stop();
            } catch (Throwable t) {
               try {
                  handle.key.cancel();
                  handle.key.channel().close();
                  handle.lock.release();
                  handle.lock.channel().close();
               } finally {
                  for (IUdpTransferListener listener : handle.listeners) {
                     listener.onTransferException(handle.config, t);
                  }
                  handle.listeners.clear();
               }
            }
         }
         keys.remove();
      }
   }

   public static void main(String[] args) {
      try {
         final UdpFileTransferHandler rec = new UdpFileTransferHandler();
         String file = args[0];
         InetAddress ipAddr = InetAddress.getByName(args[1]);
         int remotePort = Integer.parseInt(args[2]);
         int localPort = Integer.parseInt(args[3]);
         TransferConfig.Direction direction = TransferConfig.Direction.values()[Integer.parseInt(args[4])];
         InetSocketAddress address = new InetSocketAddress(ipAddr, remotePort);
         if (direction == TransferConfig.Direction.FILE_TO_SOCKET) {
            System.out.printf("Transfering %s to %s via local port %d\n", file, address.toString(), localPort);
         } else {
            System.out.printf("Writing to %s data recieved from %s via local port %d\n", file, address.toString(),
                  localPort);
         }
         TransferConfig config = new TransferConfig(file, address, new InetSocketAddress(InetAddress.getLocalHost(), localPort), direction, 7 * 188);
         rec.registerTransfer(config);
         System.in.read();
         rec.stop(2500);
      } catch (UnknownHostException ex) {
         OseeLog.log(MessageSystemTestEnvironment.class, Level.SEVERE, ex.getMessage(), ex);
      } catch (FileNotFoundException ex) {
         OseeLog.log(MessageSystemTestEnvironment.class, Level.SEVERE, ex.getMessage(), ex);
      } catch (IOException ex) {
         OseeLog.log(MessageSystemTestEnvironment.class, Level.SEVERE, ex.getMessage(), ex);
      } catch (InterruptedException ex) {
         OseeLog.log(MessageSystemTestEnvironment.class, Level.SEVERE, ex.getMessage(), ex);
      }
   }

   /**
    * stops all currently running file transfers but does not shutdown the file transfer service
    * 
    * @throws IOException
    */
   public synchronized void stopAllTransfers() throws IOException {
      OseeLog.log(MessageSystemTestEnvironment.class,Level.FINE,
            "stopping all transfers");
      for (Handle handle : handles.values()) {
         handle.stop();
      }
      handles.clear();
   }

   public synchronized boolean hasActiveTransfers() {
      return !handles.isEmpty();
   }

   public boolean isRunning() {
      final Thread.State state = thread.getState();
      return state != Thread.State.NEW && state != Thread.State.TERMINATED && !done;
   }
}
