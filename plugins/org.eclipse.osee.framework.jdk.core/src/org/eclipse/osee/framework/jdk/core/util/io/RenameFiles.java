/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.text.Rule;
import org.eclipse.osee.framework.jdk.core.text.change.ChangeSet;
import org.eclipse.osee.framework.jdk.core.text.rules.ReplaceAll;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Ryan D. Brooks
 */
public class RenameFiles {

   public static void main(String[] args) throws IOException {
      if (args.length < 3) {
         System.out.println("Usage: java library.RenameFiles <directory> <full path pattern> <replacement>\n");
         return;
      }

      Rule rule = new ReplaceAll(Pattern.compile(args[1]), args[2]);
      List<File> files = Lib.recursivelyListFiles(new File(args[0]).getCanonicalFile());
      int size = files.size();
      int renamedFileCount = 0;

      for (int i = 0; i < size; i++) {
         File file = files.get(i);
         ChangeSet newName = rule.computeChanges(file.getPath());
         if (rule.ruleWasApplicable()) {
            File newFile = new File(newName.toString());
            if (file.renameTo(newFile)) {
               System.out.println(file.getPath() + " became " + newFile.getPath());
               renamedFileCount++;
            } else {
               System.out.println(file.getPath() + " failed to become " + newFile.getPath());
            }
         }
      }
      System.out.println("Changed " + renamedFileCount + " files");
   }
}
