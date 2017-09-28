/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.links;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.account.rest.model.AccountWebPreferences;
import org.eclipse.osee.account.rest.model.Link;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.swt.program.Program;

/**
 * @author Donald G. Dunne
 */
public class EditLinksNavigateItem extends XNavigateItem implements FileChangedListener {

   private static String LINKS_FILENAME = "OSSE_%s_Links.txt";
   private static final List<FileChangedListener> listeners = new LinkedList<>();
   private final boolean global;
   private boolean addedListener = false;

   public EditLinksNavigateItem(XNavigateItem parent, boolean global) {
      super(parent, "Edit " + (global ? "Global" : "Personal") + " Links", FrameworkImage.EDIT);
      this.global = global;
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      AccountWebPreferences webPrefs =
         LinkUtil.getAccountsPreferencesData(LinkUtil.getStoreArtifact(global).getArtId());

      StringBuilder sb = new StringBuilder(
         "OSEE " + (global ? "Global" : "Personal") + " Links " + UserManager.getUser().toStringWithId() + "\n\n");
      sb.append("// Move items up and down to change order.\n// Change names, urls and tags without changing id.\n// " //
         + "Delete line to remove.\n// Copy existing link and clear id for new link.\n// Save to Update\n" //
         + "// Example: {\"name\":\"Google\",\"url\":\"http://www.google.com\",\"id\":\"AOd9poc8Kz02K3K7xfwA\",\"team\":\"Joe Smith\",\"tags\":[]}\n\n");
      for (Link link : webPrefs.getLinks().values()) {
         sb.append(JsonUtil.getMapper().writeValueAsString(link) + "\n");
      }
      File outFile = getLinksFile();
      Lib.writeStringToFile(sb.toString(), outFile);
      Program.launch(outFile.getAbsolutePath());

      if (!addedListener) {
         listeners.add(this);
         addedListener = true;
      }
      startFileWatcher();
   }

   private File getLinksFile() {
      File outFile = new File(System.getProperty("user.home") + "/" + getFilename(global));
      return outFile;
   }

   private String getFilename(boolean global) {
      return String.format(LINKS_FILENAME, (global ? "Global" : "Personal"));
   }

   private static final AtomicBoolean startedWatcher = new AtomicBoolean(false);

   private static void startFileWatcher() {
      if (startedWatcher.get()) {
         return;
      }
      startedWatcher.set(true);

      Job job = new Job("Links File Watcher") {

         @Override
         protected IStatus run(IProgressMonitor monitor) {
            try {
               final Path dir = new File(System.getProperty("user.home")).toPath();
               final WatchService watcher = FileSystems.getDefault().newWatchService();

               WatchKey key = dir.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
               for (;;) {

                  // pend for change to the home directory
                  try {
                     key = watcher.take();
                  } catch (InterruptedException ex) {
                     return new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1, ex.getLocalizedMessage(), ex);
                  }

                  for (WatchEvent<?> event : key.pollEvents()) {

                     @SuppressWarnings("unchecked")
                     WatchEvent<Path> ev = (WatchEvent<Path>) event;
                     Path filename = ev.context();

                     for (FileChangedListener listener : listeners) {
                        listener.fileChanged(filename.toString());
                     }
                  }

                  // Reset the key
                  boolean valid = key.reset();
                  if (!valid) {
                     break;
                  }
               }
               return Status.OK_STATUS;
            } catch (IOException ex) {
               OseeLog.log(EditLinksNavigateItem.class, Level.SEVERE, ex);
               return new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1, ex.getLocalizedMessage(), ex);
            }
         }
      };
      Jobs.startJob(job);
   }

   @Override
   public void fileChanged(String filename) {
      if (filename.endsWith(getFilename(global))) {
         System.err.println("file changed: " + getFilename(global));
         storeChangeAndContinue();
      }
   }

   private void storeChangeAndContinue() {
      try {
         File outFile = getLinksFile();
         String fileToString = Lib.fileToString(outFile);
         AccountWebPreferences newWebPrefs = new AccountWebPreferences();
         for (String line : fileToString.split("\n")) {
            if (line.startsWith("{")) {
               Link link = JsonUtil.readValue(line, Link.class);
               newWebPrefs.getLinks().put(link.getId(), link);
            }
         }
         Artifact useArtifact = LinkUtil.getStoreArtifact(global);
         Conditions.checkNotNull(useArtifact,
            "Could not find " + (global ? "Global" : "Personal") + " store artifact.");
         LinkUtil.saveWebPreferences(newWebPrefs, global, useArtifact);
      } catch (Exception ex) {
         OseeLog.log(EditLinksNavigateItem.class, Level.SEVERE, ex);
      }
   }

}
