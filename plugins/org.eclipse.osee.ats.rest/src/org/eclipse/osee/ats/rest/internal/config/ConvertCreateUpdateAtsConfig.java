/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.config;

import org.eclipse.osee.ats.api.util.IAtsDatabaseConversion;
import org.eclipse.osee.ats.impl.IAtsServer;
import org.eclipse.osee.framework.core.util.XResultData;

/**
 * See description below
 * 
 * @author Donald G Dunne
 */
public class ConvertCreateUpdateAtsConfig implements IAtsDatabaseConversion {

   private final IAtsServer atsServer;

   public ConvertCreateUpdateAtsConfig(IAtsServer atsServer) {
      this.atsServer = atsServer;
   }

   @Override
   public void run(XResultData data, boolean reportOnly) {
      if (reportOnly) {
         data.log("No REPORT-ONLY available for this conversion.\n");
      } else {
         UpdateAtsConfiguration update = new UpdateAtsConfiguration(atsServer);
         update.createUpdateConfig(data);
         data.log("Complete");
      }
   }

   @Override
   public String getDescription() {
      StringBuffer data = new StringBuffer();
      data.append("Updates AtsConfig artifact (optional but recommended conversion)\n\n");
      data.append("This will add any new ATS columns that have been configured.\n\nCan be run multiple times without corruption.\nShould be run after each release.");
      return data.toString();
   }

   @Override
   public String getName() {
      return "Create or Update AtsConfig";
   }

}
