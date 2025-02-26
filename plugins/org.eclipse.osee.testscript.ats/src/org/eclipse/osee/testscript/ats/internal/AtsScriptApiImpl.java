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
package org.eclipse.osee.testscript.ats.internal;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.testscript.ats.AtsScriptApi;
import org.eclipse.osee.testscript.ats.AtsScriptTaskTrackingApi;

/**
 * @author Stephen J. Molaro
 */
public class AtsScriptApiImpl implements AtsScriptApi {

   private OrcsApi orcsApi;
   private AtsApi atsApi;
   private AtsScriptTaskTrackingApi scriptTaskTrackingApi;

   public void bindOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public void bindAtsApi(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   public void start() {
      this.scriptTaskTrackingApi = new AtsScriptTaskTrackingApiImpl(orcsApi, atsApi);
   }

   @Override
   public OrcsApi getOrcsApi() {
      return this.orcsApi;
   }

   @Override
   public AtsApi getAtsApi() {
      return this.atsApi;
   }

   @Override
   public AtsScriptTaskTrackingApi getScriptTaskTrackingApi() {
      return this.scriptTaskTrackingApi;
   }

}