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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.core.data.FileTypeApplicabilityData;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * Applies product line engineering block applicability to file of the configured file extensions. Formerly modeled
 * under the Rule Interface, BatStagingCreator works with BatFileProcessor to process these files in place in a
 * thread-safe way. BatStagingCreator focuses on handling of the directory portion of the BlockApplicabilityTool.
 *
 * @author Ryan D. Brooks
 * @author Branden W. Phillips
 */
public class BatStagingCreator {

   private final BlockApplicabilityOps orcsApplicability;
   private final Map<String, FileTypeApplicabilityData> fileTypeApplicabilityDataMap;
   private final Pattern validFileExtensions;
   private final boolean commentNonApplicableBlocks;

   public BatStagingCreator(BlockApplicabilityOps orcsApplicability, Map<String, FileTypeApplicabilityData> fileTypeApplicabilityData, Pattern validFileExtensions, boolean commentNonApplicableBlocks) {
      this.orcsApplicability = orcsApplicability;
      this.fileTypeApplicabilityDataMap = fileTypeApplicabilityData;
      this.validFileExtensions = validFileExtensions;
      this.commentNonApplicableBlocks = commentNonApplicableBlocks;
   }

   /**
    * This method is for processing the given directory meant for staging with applicability. There are two main paths,
    * whether the given file is a file for processing or a directory. <br/>
    * <br/>
    * When the file is a directory, the method handles special cases for the config file, along with situations where
    * the staging directory already exists and is being refreshed instead of created from scratch. Any file still
    * existing in the staging children, means that the file was once staged but now is not applicable for various
    * reasons and is deleted. Any sibling or descendant can be excluded by a config file.<br/>
    * <br/>
    * If a file is not a directory, to be processed it must have an accepted file extension via the GlobalPreferences
    * artifact. If a former stage file existed, it is only overwritten if it is different from the newly staged file. If
    * the file has no applied applicability, a link is created. If it was changed, that new file is set as
    * ReadOnly.<br/>
    * <br/>
    *
    * @param inFile - File to process
    * @param stageFileParent - Parent stage file to the given inFile
    * @param filesToExclude - A list of files that will not be included in the staging area
    * @return File - The staged file, or null if the file was not included
    */
   public File processDirectory(XResultData results, File inFile, File stageFileParent, Set<String> filesToExclude) {
      OseeLog.log(getClass(), Level.INFO, "BatStagingCreator::processDirectory => Started file: " + inFile.getPath());
      if (results.isErrors()) {
         return null;
      }
      Set<String> excludedFiles = new HashSet<>();
      excludedFiles.addAll(filesToExclude); // A new set is created to ensure thread safety
      String inFileName = inFile.getName();
      if (!isExcluded(inFileName, excludedFiles)) {
         boolean isDirectory = inFile.isDirectory();
         if (!(isDirectory && inFileName.startsWith("."))) {
            File stageFile = new File(stageFileParent, inFileName);
            if (isDirectory) {
               if (!stageFile.exists() && !stageFile.mkdir()) {
                  results.warningf("Unable to create stage directory for %s\n", inFile.getPath());
                  return null;
               }
               // Get the children for the inFile
               List<File> children = new ArrayList<>();
               children.addAll(Arrays.asList(inFile.listFiles()));

               Set<File> stagedChildren = new ConcurrentSkipListSet<>();
               stagedChildren.addAll(Arrays.asList(stageFile.listFiles()));

               Set<String> filesInConfig = processConfig(results, children, stageFile);
               excludedFiles.addAll(filesInConfig);

               /**
                * Because each child file/directory is independent of their siblings', this process is able to be
                * threaded as it follows the directory tree. It was found/tested that limiting it to 2 per executor was
                * optimal. Too many threads ends up CPU throttling and slowing the process down. It is important to note
                * that this does not mean only 2 threads total for the processing, but 2 threads per individual
                * directory in the tree. Thread count goes up exponentially the deeper it goes into the directory tree.
                */
               ExecutorService executor = Executors.newFixedThreadPool(2);
               for (File child : children) {
                  Runnable task = () -> {
                     File stagedFile = processDirectory(results, child, stageFile, excludedFiles);
                     // Since the file was processed, it no longer needs to be tracked in the list for removal later
                     if (stagedFile != null) {
                        stagedChildren.remove(stagedFile);
                     }
                  };
                  executor.execute(task);
               }
               executor.shutdown();
               try {
                  // At the moment, this process should never come close to taking an hour
                  executor.awaitTermination(3600, TimeUnit.SECONDS);
               } catch (InterruptedException ex1) {
                  results.warningf("A thread was interrupted while processing a subdirectory of %s\n",
                     inFile.getPath());
               }

               // Any staged child that was not processed, is now deleted.
               for (File fileToRemove : stagedChildren) {
                  try {
                     /**
                      * This line will go through a directory's tree and delete all the files. In order to delete a
                      * directory, its' children must be deleted. If the file is not a directory, this will delete the
                      * file anyway.
                      */
                     Files.walk(fileToRemove.toPath()).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(
                        File::delete);
                  } catch (IOException ex) {
                     results.warningf("An exception occured while cleaning up unstaged files under %s\n",
                        inFile.getPath());
                  }
               }

               // The excluded files from this scope are removed from the list
               if (!excludedFiles.isEmpty()) {
                  orcsApplicability.addFileApplicabilityEntry(stageFile.getPath(), excludedFiles);
               }
            } else {
               try {
                  boolean tagProcessed = false;
                  // .tmp is used to signify this as a temporarily created file
                  File outFile = new File(stageFile.toPath() + ".tmp");
                  if (validFileExtensions.matcher(inFileName).matches()) {
                     FileTypeApplicabilityData fileTypeApplicabilityData =
                        fileTypeApplicabilityDataMap.get(Lib.getExtension(inFile.getName()).toLowerCase());
                     try {
                        tagProcessed = new BatFileProcessor(orcsApplicability, fileTypeApplicabilityData, false,
                           commentNonApplicableBlocks).processFile(inFile, outFile);
                     } catch (TagNotPlacedCorrectlyException ex) {
                        OseeLog.log(getClass(), Level.INFO,
                           "BatStagingCreator::processDirectory => TagNotPlacedCorrectlyException thrown in file: " + inFile.getPath());
                        OseeLog.log(getClass(), Level.INFO,
                           "BatStagingCreator::processDirectory => TagNotPlacedCorrectlyException details: " + ex.toString());
                        results.warning(
                           "Tag was not placed correctly in file: " + inFile.getPath() + ". This file will not be processed.");
                        return stageFile;
                     } catch (Exception ex) {
                        OseeLog.log(getClass(), Level.INFO,
                           "BatStagingCreator::processDirectory => Exception thrown in file: " + inFile.getPath());
                        OseeLog.log(getClass(), Level.INFO,
                           "BatStagingCreator::processDirectory => Exception details: " + ex.toString());
                        results.error(ex.toString());
                        return stageFile;
                     }
                  }

                  /**
                   * If the processed outFile is not different than what was previously in the staging, no changes are
                   * made.
                   */
                  boolean isNew = isStageFileNew(stageFile, outFile);
                  if (isNew) {
                     if (!tagProcessed) {
                        OseeLog.log(getClass(), Level.INFO,
                           "BatStagingCreator::processDirectory: Creating link " + stageFile.toPath() + " with " + inFile.toPath());
                        Files.createLink(stageFile.toPath(), inFile.toPath());
                     } else {
                        stageFile.delete();
                        /**
                         * Only want to set new processed files to read only, otherwise the original will also be read
                         * only
                         */
                        outFile.setReadOnly();
                        com.google.common.io.Files.move(outFile, stageFile); // Another Files import is already taken for this class
                     }
                  }
                  if (outFile.exists()) {
                     outFile.delete();
                  }
               } catch (Exception ex) {
                  OseeLog.log(getClass(), Level.INFO,
                     "BatStagingCreator::processDirectory: Exception in " + inFile.getPath());
                  OseeLog.log(getClass(), Level.INFO,
                     "BatStagingCreator::processDirectory: Exception details" + ex.toString());
                  results.warningf("Exception %s in file %s\n", ex.toString(), inFile.getPath());
               }
            }
            return stageFile;
         }
      }

      /**
       * By returning null, we signal to the parent call that this file was not processed and if a staged version
       * exists, it should be deleted.
       */
      return null;
   }

   /**
    * If there is no previous stage file, then it is new. <br/>
    * If there is no outFile, but there is a stageFile, means the file is not a valid one for processing and should be
    * left alone <br/>
    * If the files are not equal lengths, they must be different. <br/>
    * Read each file line by line until a different is found, if none are found, must be the same
    */
   private boolean isStageFileNew(File stageFile, File outFile) throws IOException {
      if (!stageFile.exists()) {
         return true;
      }

      if (!outFile.exists()) {
         return false;
      }

      if (stageFile.length() != outFile.length()) {
         return true;
      }

      boolean isFileNew = false;

      BufferedReader stageReader = Files.newBufferedReader(stageFile.toPath());
      BufferedReader outReader = Files.newBufferedReader(outFile.toPath());
      String stageLine, outLine;
      while (((stageLine = stageReader.readLine()) != null) && ((outLine = outReader.readLine()) != null)) {
         if (!stageLine.equals(outLine)) {
            isFileNew = true;
         }
      }

      stageReader.close();
      outReader.close();
      return isFileNew;
   }

   /**
    * This method checks the children of a directory for a BlockApplicabilityConfig text file. If found, it will process
    * that file to find files that have applicability applied to them. The processing of this file relies on the only
    * text being left are applicable file names. If commenting is turned on, the isConfig boolean is used to fix that
    * and make sure no commenting is enabled during that file processing.
    */
   private Set<String> processConfig(XResultData results, List<File> children, File stageFile) {
      Set<String> filesToExclude = new HashSet<>();
      BufferedReader reader;
      String readLine;
      File configFile = null;

      for (File child : children) {
         String fileName = child.getName();
         if (fileName.equals(".fileApplicability")) {
            configFile = child;
            FileTypeApplicabilityData fileTypeApplicabilityData =
               fileTypeApplicabilityDataMap.get(Lib.getExtension(configFile.getName()).toLowerCase());
            Pattern tagPattern = fileTypeApplicabilityData.getCommentedTagPattern();

            try {
               // Reading the original file for all names
               reader = new BufferedReader(new FileReader(configFile));
               while ((readLine = reader.readLine()) != null) {
                  if (!tagPattern.matcher(readLine).matches() && !readLine.isEmpty()) {
                     filesToExclude.add(readLine);
                  }
               }
               reader.close();

               // Using existing BlockApplicability logic to process the file
               File stagedConfig = new File(stageFile, configFile.getName());
               new BatFileProcessor(orcsApplicability, fileTypeApplicabilityData, true,
                  commentNonApplicableBlocks).processFile(configFile, stagedConfig);

               /**
                * Reading the new staged config file, any names that are left are included in the publish and removed
                * from the list
                */
               reader = new BufferedReader(new FileReader(stagedConfig));
               while ((readLine = reader.readLine()) != null) {
                  if (!tagPattern.matcher(readLine).matches() && !readLine.isEmpty()) {
                     filesToExclude.remove(readLine);
                  }
               }
               reader.close();
               stagedConfig.delete();
            } catch (Exception ex) {
               results.warningf("Exception %s with file %s\n", ex.toString(), configFile.getPath());
               return new HashSet<>();
            }
            break;
         }
      }

      // Remove the config file from the children list so it is not processed again
      children.remove(configFile);

      return filesToExclude;
   }

   /**
    * This method checks if the file is excluded, first checking to see if there are any direct matches and then
    * checking if there are any matches where the .applicabilityFile input included a wildcard.
    */
   private boolean isExcluded(String inFileName, Set<String> excludedFiles) {
      if (excludedFiles.contains(inFileName)) {
         return true;
      } else {
         Iterator<String> excludedIterator = excludedFiles.iterator();
         while (excludedIterator.hasNext()) {
            String excludedName = excludedIterator.next();
            if (excludedName.contains("*")) {
               excludedName = excludedName.replaceAll("\\.", "\\\\.").replaceAll("\\*", ".*").replaceAll("\\!", "");
               Pattern excludePattern = Pattern.compile("^(" + excludedName + ")$");
               if (excludePattern.matcher(inFileName).matches()) {
                  return true;
               }
            }
         }
      }
      return false;
   }

}
