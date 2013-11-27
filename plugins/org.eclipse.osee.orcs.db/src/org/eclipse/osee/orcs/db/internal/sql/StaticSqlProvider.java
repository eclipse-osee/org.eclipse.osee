/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.sql;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.SystemPreferences;
import org.eclipse.osee.orcs.db.internal.SqlProvider;

/**
 * @author Roberto E. Escobar
 */
public class StaticSqlProvider implements SqlProvider {

   private final Map<String, String> sqlMap = new HashMap<String, String>();

   private Log logger;
   private SystemPreferences preferences;
   private boolean wasPopulated;

   public StaticSqlProvider() {
      super();
      this.wasPopulated = false;
   }

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setPreferences(SystemPreferences preferences) {
      this.preferences = preferences;
   }

   @Override
   public String getSql(String key) throws OseeCoreException {
      Conditions.checkNotNull(key, "Sql Key");
      ensurePopulated();
      String query = null;
      if (sqlMap.containsKey(key)) {
         query = sqlMap.get(key);
      } else {
         logger.error("Unable to find - SqlKey [%s]", key);
      }
      return query;
   }

   @Override
   public String getSql(OseeSql key) throws OseeCoreException {
      Conditions.checkNotNull(key, "Sql Key");
      ensurePopulated();
      return getSql(key.toString());
   }

   public boolean areHintsSupported() throws OseeCoreException {
      return preferences.isBoolean(SQL_DATABASE_HINTS_SUPPORTED_KEY);
   }

   private synchronized void ensurePopulated() throws OseeCoreException {
      if (!wasPopulated) {
         synchronized (sqlMap) {
            boolean areHintsSupported = areHintsSupported();
            for (OseeSql oseeSql : OseeSql.values()) {
               String sql = oseeSql.getSql();
               String hints = oseeSql.getHints();
               if (areHintsSupported) {
                  if (oseeSql.getIsDynamicHint()) {
                     hints = "/*+ ordered */";
                  }
               }
               if (hints == null) {
                  hints = "";
               }
               sql = String.format(sql, hints);

               sqlMap.put(oseeSql.toString(), sql);
            }
            sqlMap.put(SQL_RECURSIVE_WITH_KEY, preferences.getValue(SQL_RECURSIVE_WITH_KEY));
            sqlMap.put(SQL_REG_EXP_PATTERN_KEY, preferences.getValue(SQL_REG_EXP_PATTERN_KEY));
         }
         wasPopulated = true;
      }
   }

}
