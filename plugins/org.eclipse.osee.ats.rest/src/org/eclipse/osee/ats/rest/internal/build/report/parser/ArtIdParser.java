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
package org.eclipse.osee.ats.rest.internal.build.report.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import org.eclipse.osee.ats.rest.internal.build.report.util.InputFilesUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;

/**
 * @author John Misinco
 */
public class ArtIdParser {

   private ArtIdParser() {
      // utility class
   }

   public static Collection<Integer> getArtIds(String rpcr) throws OseeCoreException {
      Set<Integer> artIds = new TreeSet<Integer>();
      File input = InputFilesUtil.getChangeReportIds(rpcr);
      if (input.exists()) {
         try {
            Scanner s = new Scanner(input);
            s.useDelimiter(",");
            while (s.hasNext()) {
               artIds.add(s.nextInt());
            }
         } catch (FileNotFoundException ex) {
            OseeExceptions.wrapAndThrow(ex);
         }
      }
      return artIds;
   }
}
