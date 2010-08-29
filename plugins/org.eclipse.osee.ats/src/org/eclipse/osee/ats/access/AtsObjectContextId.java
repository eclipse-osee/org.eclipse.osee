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
package org.eclipse.osee.ats.access;

import org.eclipse.osee.framework.core.data.AccessContextId;

/**
 * @author Donald G. Dunne
 */
public final class AtsObjectContextId {

   // @formatter:off
   public static final AccessContextId DEFAULT_ATSOBJECT_CONTEXT = AtsAccessContextIdFactory.createContextId("ABcgW411ZTrr4oapsZQA", "atsobject.default.context");
   public static final AccessContextId WORK_ITEM_CONFIG_CONTEXT = AtsAccessContextIdFactory.createContextId("AFRkIhhTTDQX5chTSoQA", "atsobject.admin.config.workitem");
   public static final AccessContextId TEAM_CONFIG_CONTEXT = AtsAccessContextIdFactory.createContextId("ABcgW5ESyyaUQT8dx5gA", "atsobject.admin.config.team");
   public static final AccessContextId TEAM_AND_ACCESS_CONFIG_CONTEXT = AtsAccessContextIdFactory.createContextId("ABcgW5GgqFU8Kk96sZwA", "atsobject.admin.config.teamAccess");
   public static final AccessContextId WORKFLOW_ADMIN_CONTEXT = AtsAccessContextIdFactory.createContextId("AFRkIhiFf3rvbeTD87AA", "atsobject.admin.workflow");
   public static final AccessContextId DENY_CONTEXT = AtsAccessContextIdFactory.createContextId("ABcgW4v_FzudjHwY95gA", "atsobject.deny");
   // @formatter:on

   private AtsObjectContextId() {
      //
   }

}
