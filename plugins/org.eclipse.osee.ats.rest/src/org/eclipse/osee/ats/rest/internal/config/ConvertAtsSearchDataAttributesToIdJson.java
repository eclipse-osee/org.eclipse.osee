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

package org.eclipse.osee.ats.rest.internal.config;

import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IAtsDatabaseConversion;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.IAttribute;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * See getDescription() below
 *
 * @author Donald G Dunne
 */
public class ConvertAtsSearchDataAttributesToIdJson implements IAtsDatabaseConversion {

   private final String TITLE = "Convert User artifact ATS Search Data from uuid key to id";

   @Override
   public void run(XResultData rd, boolean reportOnly, AtsApi atsApi) {

      Collection<ArtifactToken> artifacts =
         atsApi.getQueryService().getArtifactsAttrTypeExists(CoreAttributeTypes.AtsActionSearch);

      IAtsChangeSet changes = null;
      if (!reportOnly) {
         changes = atsApi.createChangeSet("AtsSearchData uuid to id - Convert", AtsCoreUsers.SYSTEM_USER);
      }
      for (ArtifactToken art : artifacts) {
         for (IAttribute<?> attr : atsApi.getAttributeResolver().getAttributes(art)) {
            String val = (String) attr.getValue();
            if (val.contains("\"uuid\"")) {
               String newVal = val.replaceAll("\"uuid\"", "\"id\"");
               rd.logf("For %s\nConvert [%s]\nTo    [%s]\n\n", art.toStringWithId(), val, newVal);
            }
         }
      }
      if (!reportOnly && changes != null && !changes.isEmpty()) {
         TransactionToken tx = changes.executeIfNeeded();
         System.err.println("Transaction: " + tx.getIdString());
         rd.logf("Transaction %s\n", tx.getIdString());
      }
   }

   @Override
   public String getName() {
      return TITLE;
   }

   @Override
   public String getDescription() {
      return "Converts all User art stored CoreAttrTypes.ATS Action Search json from uuid to id key";
   }
}