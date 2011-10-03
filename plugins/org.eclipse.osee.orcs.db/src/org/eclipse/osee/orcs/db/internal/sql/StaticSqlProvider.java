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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.SystemPreferences;
import org.eclipse.osee.orcs.db.internal.SqlProvider;

/**
 * @author Roberto E. Escobar
 */
public class StaticSqlProvider implements SqlProvider {

   private final Map<String, String> sqlMap = new HashMap<String, String>();
   private final Log logger;
   private final SystemPreferences preferences;
   private boolean wasPopulated;

   public StaticSqlProvider(Log logger, SystemPreferences preferences) {
      super();
      this.logger = logger;
      this.preferences = preferences;
      this.wasPopulated = false;
   }

   @Override
   public String getSql(String key) throws OseeCoreException {
      Conditions.checkNotNull(key, "Sql Key");
      ensurePopulated();
      String query = sqlMap.get(key);
      if (!Strings.isValid(query)) {
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
      return preferences.isBoolean("hintsSupported");
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
                     hints = getHintsOrderedFirstRows();
                  }
               }
               if (hints == null) {
                  hints = "";
               }
               sql = String.format(sql, hints);

               sqlMap.put(oseeSql.toString(), sql);
            }
         }
         wasPopulated = true;
      }
   }

   private String getHintsOrderedFirstRows() {
      // better for performance when using branch_id indexes
      String toReturn = "/*+ ordered */";
      try {
         // necessary performance when using gamma_id indexes
         if (preferences.isBoolean("useOrderedFirstRows")) {
            toReturn = "/*+ ordered FIRST_ROWS */";
         }
      } catch (OseeCoreException ex) {
         logger.error(ex, "Error getHintEnabled setting");
      }
      return toReturn;
   }
}
