/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.config;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.util.IAtsDatabaseConversion;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * See description below
 *
 * @author Donald G Dunne
 */
public class ConvertAtsConfigGuidAttributes implements IAtsDatabaseConversion {

   @Override
   public void run(XResultData data, boolean reportOnly, AtsApi atsApi) {
      if (reportOnly) {
         data.log("No REPORT-ONLY available for this conversion.\n");
      } else {
         ConvertAtsConfigGuidAttributesOperation update = new ConvertAtsConfigGuidAttributesOperation(atsApi);
         update.createUpdateConfig(data);
         data.log("Complete");
      }
   }

   @Override
   public String getDescription() {
      StringBuffer data = new StringBuffer();
      data.append("Updates ATS config GUID attributes to long ids (required)\n\n");
      data.append("Necessary for upgrade from 0.26.0 to 0.27.0.\n\n");
      data.append("This will:\n" //
         + "   ---- Create Team Definition Artifact related Attribute\n" //
         + "   ---- Create Actionable Item Artifact related Attribute\n" //
         + "   - For Team Workflows\n" //
         + "   ---- Convert Team Definition to new id attribute\n" //
         + "   ---- Convert Actionable Item to new id attribute\n" //
         + "   - For Programs\n" //
         + "   ---- Convert Team Definition new new id attribute\n" //
         + "   - For Action\n" //
         + "   ---- Convert Actionable Item new new id attribute\n" //
         + "   - For Reviews\n" //
         + "   ---- Convert Actionable Item new new id attribute\n" //
         + "Can be run multiple times without corruption.\n" //
         + "Should be run periodically on 0.26.0 and once more after 0.27.0 release.\n" //
         + "After final run, Team Definition and Actionable Item attributes can be removed from DB.");
      return data.toString();
   }

   @Override
   public String getName() {
      return "Convert ATS Config GUID Attributes";
   }
}