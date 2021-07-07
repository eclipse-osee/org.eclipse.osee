/*********************************************************************
 * Copyright (c) 2021 Boeing
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
package org.eclipse.osee.orcs.core.internal.applicability;

import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BlockApplicabilityStageRequest;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * This class is meant for the BlockApplicabilityTool Staging area to watch for changes within the source directory and
 * apply them to any registered staging directory. <br/>
 * KeyMap - Stores each WatchKey and associated Path. <br/>
 * ViewMap - Stores each view token and maps it to a staging path and BlockApplicabilityOps, each ops class has view
 * relevant data and cannot be shared within views. <br/>
 *
 * @author Branden W. Phillips
 */
public class StagedFileWatcher {

   private WatchService watchService;
   private final Map<WatchKey, Path> keyMap = new HashMap<>();
   private final Map<ArtifactToken, Pair<String, BlockApplicabilityOps>> viewMap = new HashMap<>();

   public StagedFileWatcher() {
      // Do nothing
   }

   public void runWatcher(BlockApplicabilityStageRequest data, String directory) {
      try {
         watchService = FileSystems.getDefault().newWatchService();

         Path dir = Paths.get(directory);

         Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
               String fileName = dir.getFileName().toString();
               if (!fileName.equals("Staging") && !(fileName.startsWith(".") && dir.toFile().isDirectory())) {
                  WatchKey key = dir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                     StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
                  keyMap.put(key, dir);
                  return FileVisitResult.CONTINUE;
               }
               return FileVisitResult.SKIP_SUBTREE;
            }
         });

         /**
          * Each time a new WatchKey is gathered upon file change, first all files in that key are added to a list and
          * then for each registered view the files are processed with the refresh method.
          */
         WatchKey key;
         while ((key = watchService.take()) != null) {
            List<String> files = new ArrayList<>();
            for (WatchEvent<?> event : key.pollEvents()) {
               Path path = keyMap.get(key);
               String filePath = path.resolve((Path) event.context()).toString();
               filePath = filePath.replace(directory, "");
               files.add(filePath);
            }
            for (Map.Entry<ArtifactToken, Pair<String, BlockApplicabilityOps>> entry : viewMap.entrySet()) {
               String stagePath = entry.getValue().getFirst();
               BlockApplicabilityOps ops = entry.getValue().getSecond();
               System.out.println(
                  String.format("File Watcher has started processing files for %s", entry.getKey().getName()));
               XResultData results = ops.refreshStagedFiles(directory, stagePath, files);
               if (results.isErrors()) {
                  System.out.println(results.getResults());
               }
               System.out.println(
                  String.format("File Watcher has completed file processing for %s", entry.getKey().getName()));
            }
            key.reset();
         }

         watchService.close();
      } catch (ClosedWatchServiceException ex) {
         return;
      } catch (IOException | InterruptedException ex) {
         System.out.println(ex.getMessage());
      }
   }

   public void stopWatcher() {
      try {
         watchService.close();
         keyMap.clear();
         viewMap.clear();
      } catch (IOException ex) {
         System.out.println(ex.getMessage());
      }
   }

   public void addView(ArtifactToken view, String stagePath, BlockApplicabilityOps ops) {
      viewMap.put(view, new Pair<String, BlockApplicabilityOps>(stagePath, ops));
   }

}
