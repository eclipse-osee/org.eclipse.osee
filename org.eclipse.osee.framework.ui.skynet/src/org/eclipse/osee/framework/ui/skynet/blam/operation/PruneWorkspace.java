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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

/**
 * @author Ryan D. Brooks
 */
public class PruneWorkspace extends AbstractBlam {
   /**
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.VariableMap,
    *      org.eclipse.core.runtime.IProgressMonitor)
    */
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      File keeperFile = new File(variableMap.getString("Preserve List File"));
      File workspacePath = new File(variableMap.getString("Workspace Path"));
      String filePathPattern = variableMap.getString("File Path Pattern");

      ArrayList<String> preserveList = Lib.readListFromFile(keeperFile, true);
      HashSet<String> preserveSet = new HashSet<String>(preserveList);

      List<File> files = Lib.recursivelyListFiles(workspacePath, Pattern.compile(filePathPattern));
      for (File file : files) {
         if (monitor.isCanceled()) {
            return;
         }
         if (!preserveSet.contains(file.getName())) {
            file.delete();
         }
      }
   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getXWidgetXml()
    */
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XText\" displayName=\"Preserve List File\" /><XWidget xwidgetType=\"XText\" displayName=\"Workspace Path\" /><XWidget xwidgetType=\"XText\" displayName=\"File Path Pattern\" /></xWidgets>";
   }
}