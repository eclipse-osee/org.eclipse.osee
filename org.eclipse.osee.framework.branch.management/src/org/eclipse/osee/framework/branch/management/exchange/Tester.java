/*
 * Created on Aug 20, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.branch.management.exchange;

import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author Roberto E. Escobar
 */
public class Tester {

   public static void main(String[] args) throws Exception {
      File importFile = new File("C:\\Documents and Settings\\b1122182\\allBranches.zip");
      //      BranchImport importer = new BranchImport();
      //      importer.importBranch(importFile, new Options());
      Map<String, ZipEntry> binaryDataEntries = new HashMap<String, ZipEntry>();
      ZipFile zipFile = null;
      ZipEntry entry = null;

      try {
         zipFile = new ZipFile(importFile);
         if (binaryDataEntries.isEmpty()) {
            Enumeration e = zipFile.entries();
            while (e.hasMoreElements()) {
               entry = (ZipEntry) e.nextElement();
               String entryName = entry.toString();
               if (entryName.startsWith("resources\\")) {
                  int index = entryName.lastIndexOf('\\');
                  entryName = entryName.substring(index + 1, entryName.length());
                  binaryDataEntries.put(entryName, entry);
               }
            }
         }
         System.out.println(binaryDataEntries);
      } finally {
         if (zipFile != null) {
            zipFile.close();
         }
      }
   }
}
