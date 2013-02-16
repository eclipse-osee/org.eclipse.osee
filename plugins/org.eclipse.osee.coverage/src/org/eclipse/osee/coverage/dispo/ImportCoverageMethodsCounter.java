/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
