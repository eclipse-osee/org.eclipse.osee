/*********************************************************************
 * Copyright (c) 2010 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.jdk.core.util.io;

import java.io.File;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import org.eclipse.osee.framework.jdk.core.mock.MockFileWatcherListener;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Roberto E. Escobar
 */
public class FileWatcherTest {

   @Test
   public void testWatcher() throws InterruptedException {
      MockFileWatcherListener listener = new MockFileWatcherListener();

      FileWatcher watcher = new FileWatcher(320, TimeUnit.MILLISECONDS);

      MockFile file = new MockFile("abcde", true);

      Assert.assertTrue(file.exists());

      watcher.addListener(listener);
      watcher.addFile(file);
      watcher.start();

      file.setLastModified(10000);
      synchronized (listener) {
         listener.wait(1000);
      }
      checkEvent(listener, file, FileChangeType.CREATED);

      listener.clear();
      file.setLastModified(15000);
      synchronized (listener) {
         listener.wait(1000);
      }
      checkEvent(listener, file, FileChangeType.MODIFIED);

      listener.clear();
      file.setExists(false);
      file.setLastModified(0);
      synchronized (listener) {
         listener.wait(1000);
      }
      checkEvent(listener, file, FileChangeType.DELETED);

      watcher.stop();
      watcher.removeFile(file);
      watcher.removeListener(listener);
   }

   private static void checkEvent(MockFileWatcherListener listener, File expectedFile, FileChangeType expectedType) {
      Assert.assertEquals("File modified event was not received", 1, listener.getFileModifiedCallCount());
      Collection<FileChangeEvent> items = listener.getFileChangeEvents();
      if (items == null) {
         throw new RuntimeException("items is null");
      }
      Assert.assertEquals(1, items.size());
      FileChangeEvent event = items.iterator().next();
      Assert.assertNotNull(event);
      Assert.assertEquals(expectedType, event.getChangeType());
      Assert.assertEquals(expectedFile, event.getFile());
   }

   private final class MockFile extends File {

      private static final long serialVersionUID = 8677714040873925615L;
      private long lastModified;
      private boolean exists;

      public MockFile(String pathname, boolean exists) {
         super(pathname);
         this.exists = exists;
      }

      @Override
      public long lastModified() {
         return lastModified;
      }

      @Override
      public boolean setLastModified(long time) {
         boolean change = lastModified() != time;
         if (change) {
            this.lastModified = time;
         }
         return change;
      }

      @Override
      public boolean exists() {
         return exists;
      }

      public void setExists(boolean exists) {
         this.exists = exists;
      }

   }
}