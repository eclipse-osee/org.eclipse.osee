/*********************************************************************
 * Copyright (c) 2019 Boeing
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

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.util.IAtsDatabaseConversion;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * See description below
 *
 * @author Donald G Dunne
 */
public class ConvertWorkDefinitionsToJava implements IAtsDatabaseConversion {

   @Override
   public void run(XResultData data, boolean reportOnly, AtsApi atsApi) {
      if (reportOnly) {
         data.log("No REPORT-ONLY available for this conversion.\n");
      } else {
         ConvertWorkDefinitionsToJavaOperation update = new ConvertWorkDefinitionsToJavaOperation(atsApi);
         update.convert(data);
         data.log("Complete");
      }
   }

   @Override
   public String getDescription() {
      StringBuffer data = new StringBuffer();
      data.append("TBD");
      return data.toString();
   }

   @Override
   public String getName() {
      return "Convert Work Definitions to Java API";
   }
}