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
package org.eclipse.osee.framework.skynet.core.sql;

import org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase;

public class SkynetRelational {

   private static SkynetRelational instance = null;
   private static SkynetRevisionControl skynetRevisionControlSql;

   private SkynetRelational() {
      skynetRevisionControlSql = SkynetRevisionControl.getInstance();
   }

   static SkynetRelational getInstance() {
      if (instance == null) {
         instance = new SkynetRelational();
      }
      return instance;
   }

   public String getValidTableView(String tableName, int branchId, int revision) {
      String rcGammasAlias = "revGammasView";
      return "\n\nSELECT " + tableName + ".*" + " FROM " + tableName + " INNER JOIN " + "(" + skynetRevisionControlSql.getValidGammaIds(
            branchId, revision) + ") " + rcGammasAlias + " ON " + "(" + tableName + ".gamma_id = " + rcGammasAlias + ".valid_gammas )\n\n";
   }

   public static void main(String[] args) {
      System.out.println(SkynetRelational.getInstance().getValidTableView(
            SkynetDatabase.ATTRIBUTE_TYPE_TABLE.toString(), 1, 4));
      System.exit(1);
   }
}
