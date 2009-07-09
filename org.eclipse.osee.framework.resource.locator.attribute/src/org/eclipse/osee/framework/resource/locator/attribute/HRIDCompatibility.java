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
package org.eclipse.osee.framework.resource.locator.attribute;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Ryan Schmitt
 */
public class HRIDCompatibility {
   private static final String classOne = "[0123456789ABCDEFGHJKLMNPQRSTUVWXYZ]";
   private static final String classTwo = "[0123456789BCDFGHJKLMNPQRSTVWXYZ]";
   private static final String patternString = "^" + classOne + classTwo + "{3}" + classOne + "$";
   private static final String FIND_GUID_FROM_HRID =
         "SELECT oart.guid FROM osee_artifact oart WHERE oart.human_readable_id = ?";
   public final Matcher hridMatcher;

   public HRIDCompatibility() {
      hridMatcher = Pattern.compile(patternString).matcher("");
   }

   public boolean isHRID(String name) {
      hridMatcher.reset(name);
      return hridMatcher.find();
   }

   public String convertToGUID(String name) throws OseeDataStoreException {
      String hrid = Lib.removeExtension(name);

      String guid = null;
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(FIND_GUID_FROM_HRID, hrid);
         int count = 0;
         while (chStmt.next()) {
            if (count > 0) {
               throw new OseeDataStoreException(String.format(
                     "Unable to find guid for hrid [%s] - more than one guid exists", hrid));
            }
            guid = chStmt.getString("guid");
            count++;
         }
      } finally {
         chStmt.close();
      }
      if (!Strings.isValid(guid)) {
         throw new OseeDataStoreException(String.format("Unable to find guid for hrid [%s]", hrid));
      }
      String extension = Lib.getExtension(name);
      if (Strings.isValid(extension)) {
         guid = guid + "." + extension;
      }
      return guid;
   }
}
