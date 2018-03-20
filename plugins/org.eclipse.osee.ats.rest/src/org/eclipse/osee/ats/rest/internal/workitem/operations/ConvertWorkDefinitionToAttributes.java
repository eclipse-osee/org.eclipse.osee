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
package org.eclipse.osee.ats.rest.internal.workitem.operations;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.util.IAtsDatabaseConversion;
import org.eclipse.osee.ats.rest.internal.AtsApplication;
import org.eclipse.osee.framework.core.util.result.XResultData;

/**
 * See description below
 *
 * @author Donald G Dunne
 */
public class ConvertWorkDefinitionToAttributes implements IAtsDatabaseConversion {

   @Override
   public void run(XResultData data, boolean reportOnly, AtsApi atsApi) {
      if (reportOnly) {
         data.log("No REPORT-ONLY available for this conversion.\n");
      } else {
         ConvertWorkDefinitionOperations ops = new ConvertWorkDefinitionOperations(atsApi, AtsApplication.getOrcsApi());
         ops.convert(data);
         data.log("Complete");
      }
   }

   @Override
   public String getDescription() {
      StringBuffer data = new StringBuffer();
      data.append("Convert ATS Work Definitions from computed to Artifact Reference Attribute (required)\n\n");
      data.append("Necessary for upgrade from 0.26.0 to 0.27.0.\n\n");
      data.append("This will:\n" //
         + "   ---- Create a Work Definition Artifact related Attribute for each Work Item\n" //
         + "   ---- Create a Work Definition Artifact related Attribute for each Team Definition that has one of attrs below\n" //
         + "Can be run multiple times without corruption.\n" //
         + "Should be run periodically on 0.26.0 and once more after 0.27.0 release.\n" //
         + "After final run, the following can be removed from db:\n" //
         + "   --- Work Definition\n" //
         + "   --- Related Task Work Definition\n" //
         + "   --- Related Peer Workflow Definition");
      return data.toString();
   }

   @Override
   public String getName() {
      return "Create Work Definition Reference Attributes";
   }
}