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

/**
 * @author Donald G. Dunne
 */
public class AtsObjectContextId extends AtsAccessContextId {

   public static final AtsObjectContextId DEFAULT_ATSOBJECT_CONTEXT = new AtsObjectContextId("ABcgW411ZTrr4oapsZQA",
      "atsobject.default.context");

   public static final AtsObjectContextId WORK_ITEM_CONFIG_CONTEXT = new AtsObjectContextId("AFRkIhhTTDQX5chTSoQA",
      "atsobject.admin.config.workitem");
   public static final AtsObjectContextId TEAM_CONFIG_CONTEXT = new AtsObjectContextId("ABcgW5ESyyaUQT8dx5gA",
      "atsobject.admin.config.team");
   public static final AtsObjectContextId TEAM_AND_ACCESS_CONFIG_CONTEXT = new AtsObjectContextId(
      "ABcgW5GgqFU8Kk96sZwA", "atsobject.admin.config.teamAccess");
   public static final AtsObjectContextId WORKFLOW_ADMIN_CONTEXT = new AtsObjectContextId("AFRkIhiFf3rvbeTD87AA",
      "atsobject.admin.workflow");

   public static final AtsObjectContextId DENY_CONTEXT = new AtsObjectContextId("ABcgW4v_FzudjHwY95gA",
      "atsobject.deny");

   protected AtsObjectContextId(String guid, String name) {
      super(guid, name);
   }

   public static AtsObjectContextId get(String guid) {
      if (!guidToIds.containsKey(guid)) {
         guidToIds.put(guid, new AtsObjectContextId(guid, "name unknown"));
      }
      return (AtsObjectContextId) guidToIds.get(guid);
   }

}
