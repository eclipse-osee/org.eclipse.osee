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
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.jdk.core.text.Rule;
import org.eclipse.osee.framework.jdk.core.text.change.ChangeSet;
import org.eclipse.osee.framework.jdk.core.text.rules.ReplaceAll;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

/**
 * @author Ryan D. Brooks
 */
public class RenameFiles extends AbstractBlam {

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      File parentFolder = new File(variableMap.getString("Parent Folder"));
      String pathPattern = variableMap.getString("Full Path Pattern");
      String replacement = variableMap.getString("Replacement");

      Rule rule = new ReplaceAll(Pattern.compile(pathPattern), replacement);
      List<File> files = Lib.recursivelyListFiles(parentFolder.getCanonicalFile(), null);
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
               println(file.getPath() + " became " + newFile.getPath());
               renamedFileCount++;
            } else {
               println(file.getPath() + " failed to become " + newFile.getPath());
            }
         }
      }
      println("Changed " + renamedFileCount + " files");
   }

   @Override
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XText\" displayName=\"Parent Folder\" /><XWidget xwidgetType=\"XText\" displayName=\"Full Path Pattern\" /><XWidget xwidgetType=\"XText\" displayName=\"Replacement\" /></xWidgets>";
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Define");
   }
}