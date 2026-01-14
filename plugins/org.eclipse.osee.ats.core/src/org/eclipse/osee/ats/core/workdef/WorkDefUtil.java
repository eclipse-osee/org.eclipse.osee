/*********************************************************************
 * Copyright (c) 2025 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.ats.core.workdef;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workdef.model.WorkDefOption;

/**
 * @author Donald G. Dunne
 */
public class WorkDefUtil {

   private WorkDefUtil() {
      // Utility class
   }

   public static boolean isShowTargetedVersion(IAtsWorkItem workItem, AtsApi atsApi) {
      if (!workItem.isTeamWorkflow()) {
         return false;
      }
      if (workItem.getWorkDefinition().hasOption(WorkDefOption.NoTargetedVersion)) {
         return false;
      }
      return atsApi.getVersionService().isTeamUsesVersions(workItem.getParentTeamWorkflow().getTeamDefinition());
   }

   public static boolean isEditable(IAtsWorkItem workItem, AtsApi atsApi) {
      return !atsApi.getAccessControlService().isReadOnly(workItem.getStoreObject());
   }

}
