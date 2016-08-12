/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.util;

import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcStatement;

/**
 * @author Megumi Telles
 */
public class AttributeDataUtil {

   private static final String NAME_FROM_GUID =
      "select attr.value from osee_artifact art, osee_attribute attr where art.guid=? and  art.art_id = attr.art_id and attr.attr_type_id = 1152921504606847088";
   private final static Pattern guidPattern = Pattern.compile("(\\{)([0-9A-Za-z\\+_=]{20,22})(\\})");

   private AttributeDataUtil() {
      // Utility class
   }

   public static String getNameByGuid(String value, JdbcClient jdbcClient) {
      if (value != null) {
         StringBuffer sb = new StringBuffer();
         Matcher matcher = guidPattern.matcher(value);
         while (matcher.find()) {
            String guid = matcher.group(2);
            sb.append(getName(guid, jdbcClient));
         }
         return sb.toString();
      }
      return null;
   }

   private static String getName(String guid, JdbcClient jdbcClient) {
      final StringBuffer sb = new StringBuffer();
      Consumer<JdbcStatement> consumer = stmt -> {
         sb.append(stmt.getString("value"));
      };
      jdbcClient.runQuery(consumer, NAME_FROM_GUID, guid);
      return sb.toString();
   }

}
