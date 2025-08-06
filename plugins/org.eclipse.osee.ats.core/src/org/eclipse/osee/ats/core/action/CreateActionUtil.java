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
package org.eclipse.osee.ats.core.action;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.workflow.NewActionData;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class CreateActionUtil {

   private CreateActionUtil() {
      // Utility Class
   }

   public static Date getNeedByDate(NewActionData data, AtsApi atsApi) {
      Date needByDate = null;
      if (Strings.isNumeric(data.getNeedByDateLong())) {
         needByDate = new Date(Long.valueOf(data.getNeedByDateLong()));
      } else if (Strings.isValid(data.getNeedByDate())) {
         try {
            needByDate = DateUtil.getDate("yyyy-MM-dd", data.getNeedByDate());
         } catch (Exception ex) {
            data.getRd().errorf("Error parsing date.  Must be mm/dd/yyyy.", ex);
         }
      }
      return needByDate;
   }

   public static List<IAtsActionableItem> getActionableItems(NewActionData data, AtsApi atsApi) {
      List<IAtsActionableItem> ais = new LinkedList<>();
      if (data.getAiToArtToken() != null && !data.getAiToArtToken().isEmpty()) {
         for (ArtifactId aiId : data.getAiToArtToken().keySet()) {
            IAtsActionableItem ai = atsApi.getConfigService().getConfigurations().getIdToAi().get(aiId.getId());
            if (ai == null) {
               ai = atsApi.getQueryService().getConfigItem(aiId);
            }
            if (ai == null) {
               data.getRd().errorf("Invalid Actionable Item Id [%s] ", aiId);
            }
            ais.add(ai);
         }
      }
      for (String aiIdStr : data.getAiIds()) {
         Long aiId = Long.valueOf(aiIdStr);
         IAtsActionableItem ai = atsApi.getConfigService().getConfigurations().getIdToAi().get(aiId);
         if (ai == null) {
            ai = atsApi.getQueryService().getConfigItem(aiId);
         }
         if (ai == null) {
            data.getRd().errorf("Invalid Actionable Item Id [%s]", aiIdStr);
         }
         ais.add(ai);
      }
      return ais;
   }

}
