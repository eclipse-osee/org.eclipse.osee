package org.eclipse.osee.coverage.dispo;

import org.eclipse.osee.framework.jdk.core.type.CountingMap;

public class ImportCoverageMethodsCounter {
   public int numItems = 0;
   public int numDispo = 0;
   public int numMatch = 0;
   public int numNoMatch = 0;
   public int numImported = 0;
   public CountingMap<String> fileToErrorCount = new CountingMap<String>();

   @Override
   public String toString() {
      return String.format(
         "Num Items %s; Num Dispo %s; Num Match %s; Num NoMatch Items %s; Num NoMatch Files %s; Num Imported %s",
         numItems, numDispo, numMatch, numNoMatch, fileToErrorCount.getCounts().size(), numImported);
   }
}
