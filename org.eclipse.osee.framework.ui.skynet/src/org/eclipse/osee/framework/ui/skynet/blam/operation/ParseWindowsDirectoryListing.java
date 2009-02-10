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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

/**
 * @author Ryan D. Brooks
 */
public class ParseWindowsDirectoryListing extends AbstractBlam {
   private static final String DIRECTORY_PREFIX = " Directory of Y:\\";

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.VariableMap, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      String listingFile = variableMap.getString("Directory Listing File");
      Matcher matcher = Pattern.compile("(\\d+/\\d+/\\d+).*<DIR>.*?SW\\\\(\\S+)\\s+(.*)").matcher("");
      BufferedWriter writer = new BufferedWriter(new FileWriter(Lib.removeExtension(listingFile) + ".csv"));

      String path = null;
      for (String line : Lib.readListFromFile(listingFile)) {
         if (line.startsWith(DIRECTORY_PREFIX)) {
            path = line.substring(DIRECTORY_PREFIX.length());
         } else {
            matcher.reset(line);
            if (matcher.find()) {
               String summary = matcher.group(1) + "|" + matcher.group(2) + "|" + path + "\\" + matcher.group(3);
               if (!summary.endsWith(".")) {
                  writer.write(summary);
                  writer.write(Lib.lineSeparator);
               }
            }
         }
      }
      writer.close();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getXWidgetXml()
    */
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XText\" displayName=\"Directory Listing File\" defaultValue=\"c:\\UserData\\cte.txt\" /></xWidgets>";
   }
}
