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

package org.eclipse.osee.framework.skynet.core.artifact;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.types.AbstractOseeCache;

/**
 * @author Roberto E. Escobar
 */
public class BranchUtility {

   private BranchUtility() {
   }

   public static String toFileName(Branch branch) throws OseeCoreException {
      if (branch == null) {
         throw new OseeArgumentException("branch cannot be null");
      }
      String branchGuid = branch.getGuid();
      if (!GUID.isValid(branchGuid)) {
         throw new OseeStateException(String.format("GUID for branch [%s] is invalid", branch.getId()));
      }
      return encode(branchGuid);
   }

   private static String encode(String name) throws OseeCoreException {
      String toReturn = "";
      try {
         toReturn = URLEncoder.encode(name, "UTF-8");
      } catch (Exception ex) {
         if (ex instanceof OseeCoreException) {
            throw (OseeCoreException) ex;
         } else {
            throw new OseeWrappedException(ex);
         }
      }
      return toReturn;
   }

   public static Branch fromFileName(AbstractOseeCache<Branch> cache, String fileName) throws OseeCoreException {
      if (cache == null) {
         throw new OseeArgumentException("cache cannot be null");
      }
      if (!Strings.isValid(fileName)) {
         throw new OseeArgumentException("file name cannot be null or empty");
      }
      Branch toReturn = null;
      String branchGuid = decode(fileName);
      if (GUID.isValid(branchGuid)) {
         toReturn = cache.getByGuid(branchGuid);
         if (toReturn == null) {
            throw new OseeArgumentException(String.format("Unable to find branch matching guid [%s]", branchGuid));
         }
      } else {
         int branchId = Integer.parseInt(Lib.getExtension(fileName));
         toReturn = cache.getById(branchId);
         if (toReturn == null) {
            throw new OseeArgumentException(String.format("Unable to find branch matching id [%s]", branchId));
         }
      }
      return toReturn;
   }

   private static String decode(String name) {
      String toReturn = name;
      try {
         toReturn = URLDecoder.decode(name, "UTF-8");
      } catch (UnsupportedEncodingException ex) {
         // Do Nothing
      }
      return toReturn;
   }
}
