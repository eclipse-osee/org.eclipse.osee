/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.resource;

import static org.eclipse.osee.framework.core.enums.CoreBranches.SYSTEM_ROOT;
import java.net.URL;
import java.util.Map;
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.jdbc.AbstractJdbcMigrationResource;

public class OseeDatastoreMigrationResource extends AbstractJdbcMigrationResource {

   private static final String FILE_PATH = "migration/";
   private static final String TX_SEQ_PLACEHOLDER = "osee.tx_seq";
   private static final String SYS_ROOT_TYPE_PLACEHOLDER = "osee.sys_root_type";
   private static final String SYS_ROOT_STATE_PLACEHOLDER = "osee.sys_root_state";
   private static final String SYS_ROOT_NAME_PLACEHOLDER = "osee.sys_root_name";
   private static final String SYS_ROOT_ID_PLACEHOLDER = "osee.sys_root_id";

   @Override
   public URL getLocation() {
      return getClass().getResource(FILE_PATH);
   }

   @Override
   public void addPlaceholders(Map<String, String> placeholders) {
      placeholders.put(TX_SEQ_PLACEHOLDER, OseeData.TRANSACTION_ID_SEQ);
      placeholders.put(SYS_ROOT_TYPE_PLACEHOLDER, String.valueOf(BranchType.SYSTEM_ROOT.getValue()));
      placeholders.put(SYS_ROOT_STATE_PLACEHOLDER, BranchState.MODIFIED.getIdString());
      placeholders.put(SYS_ROOT_NAME_PLACEHOLDER, CoreBranches.SYSTEM_ROOT.getName());
      placeholders.put(SYS_ROOT_ID_PLACEHOLDER, SYSTEM_ROOT.getIdString());
   }
}
