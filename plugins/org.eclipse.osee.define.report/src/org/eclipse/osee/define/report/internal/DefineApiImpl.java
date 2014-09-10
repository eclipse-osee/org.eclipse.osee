/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.report.internal;

import org.eclipse.osee.define.report.api.DataRightInput;
import org.eclipse.osee.define.report.api.DataRightResult;
import org.eclipse.osee.define.report.api.DefineApi;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Angel Avila
 */
public class DefineApiImpl implements DefineApi {

   private OrcsApi orcsApi;
   private Log logger;
   private DataRightBuilder dataRightsManager;

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void start() {
      dataRightsManager = new DataRightBuilder(orcsApi);
   }

   public void stop() {
      dataRightsManager = null;
   }

   @Override
   public DataRightResult getDataRights(DataRightInput request) {
      return dataRightsManager.getDataRights(request);
   }
}
