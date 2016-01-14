/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.ops;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.internal.Activator;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.jdk.core.text.Rule;
import org.eclipse.osee.framework.jdk.core.text.change.ChangeSet;
import org.eclipse.osee.framework.jdk.core.text.rules.ReplaceAll;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * Operation that renames files matching a regex.
 *
 * @author Ryan D. Brooks
 */
public class RenameFilesOperation extends AbstractOperation {
   private final CharSequence parentFolderPath;
   private final CharSequence pathPattern;
   private final CharSequence replacement;
   private final boolean recurseFiles;

   /**
    * All parameters of this operation are not used until the operation is run. This enables the operation to be
    * constructed prior to its execution with parameters whose values are not yet set. This is needed for constructing
    * composite operations where results of prior sub-operations are used in subsequent sub-operations
    *
    * @param logger passed along the the super class and used by this class by calling one of AbstractOperation's log
    * methods
    * @param parentFolderPath parent folder that will be searched using the pathPattern
    * @param pathPattern java regex that must match the entire path of any files to be renamed
    * @param replacement the replacement value used in renaming (may be the empty string)
    */
   public RenameFilesOperation(OperationLogger logger, CharSequence parentFolderPath, CharSequence pathPattern, CharSequence replacement) {
      this(logger, parentFolderPath, pathPattern, replacement, true);
   }

   public RenameFilesOperation(OperationLogger logger, CharSequence parentFolderPath, CharSequence pathPattern, CharSequence replacement, boolean recurseFiles) {
      super("Rename Files", Activator.PLUGIN_ID, logger);
      this.parentFolderPath = parentFolderPath;
      this.pathPattern = pathPattern;
      this.replacement = replacement;
      this.recurseFiles = recurseFiles;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      logf("Starting %s", getClass().getSimpleName());
      Rule rule = new ReplaceAll(Pattern.compile(pathPattern.toString()), replacement.toString());
      File parentFolder = new File(parentFolderPath.toString()).getCanonicalFile();
      List<File> files =
         recurseFiles ? Lib.recursivelyListFiles(parentFolder) : Arrays.asList(parentFolder.listFiles());
      int size = files.size();
      int renamedFileCount = 0;

      for (int i = 0; i < size; i++) {
         if (monitor.isCanceled()) {
            return;
         }
         File file = files.get(i);
         rule.setRuleWasApplicable(false);
         ChangeSet newName = rule.computeChanges(file.getPath());
         if (rule.ruleWasApplicable()) {
            File newFile = new File(newName.toString());
            if (file.renameTo(newFile)) {
               logf("%s became %s", file.getPath(), newFile.getPath());
               renamedFileCount++;
            } else {
               logf("%s failed to become %s", file.getPath(), newFile.getPath());
            }
         }
      }
      logf("Renamed %d files.", renamedFileCount);
   }
}