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
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Ryan Schmitt
 */
public class HRIDCompatibility {
   private static final String classOne = "[0123456789ABCDEFGHJKLMNPQRSTUVWXYZ]";
   private static final String classTwo = "[0123456789BCDFGHJKLMNPQRSTVWXYZ]";
   private static final String patternString = "^" + classOne + classTwo + "{3}" + classOne + "$";
   private static final String FIND_GUID_FROM_ATTR_GAMMAID =
         "SELECT oart.guid FROM osee_artifact oart, osee_attribute oattr WHERE oart.art_id = oattr.art_id AND oattr.gamma_id = ?";
   private final Matcher hridMatcher;

   public HRIDCompatibility() {
      hridMatcher = Pattern.compile(patternString).matcher("");
   }

   public boolean isHRID(String name) {
      hridMatcher.reset(name);
      return hridMatcher.find();
   }

   public String convertToGUID(String gammaId) throws OseeDataStoreException {
      String guid =
            ConnectionHandler.runPreparedQueryFetchString(null, FIND_GUID_FROM_ATTR_GAMMAID, Integer.parseInt(gammaId));
      if (!Strings.isValid(guid)) {
         throw new OseeDataStoreException(String.format("Unable to find guid for gammaId [%s]", gammaId));
      }
      return guid;
   }
}
