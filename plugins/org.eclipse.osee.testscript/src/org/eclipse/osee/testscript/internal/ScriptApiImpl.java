/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.testscript.internal;

import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.testscript.DashboardApi;
import org.eclipse.osee.testscript.ScriptApi;
import org.eclipse.osee.testscript.ScriptBatchApi;
import org.eclipse.osee.testscript.ScriptDefApi;
import org.eclipse.osee.testscript.ScriptResultApi;
import org.eclipse.osee.testscript.ScriptSetApi;
import org.eclipse.osee.testscript.TestCaseApi;
import org.eclipse.osee.testscript.TestPointApi;
import org.eclipse.osee.testscript.TmoImportApi;

/**
 * @author Stephen J. Molaro
 */
public class ScriptApiImpl implements ScriptApi {

   private OrcsApi orcsApi;
   private ScriptSetApi scriptProgramApi;
   private ScriptBatchApi scriptBatchApi;
   private ScriptDefApi scriptDefApi;
   private ScriptResultApi scriptResultApi;
   private TestCaseApi testCaseApi;
   private TestPointApi testPointApi;
   private TmoImportApi tmoImportApi;
   private DashboardApi dashboardApi;

   public void bindOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public void start() {
      this.scriptProgramApi = new ScriptSetApiImpl(orcsApi);
      this.scriptBatchApi = new ScriptBatchApiImpl(orcsApi);
      this.scriptDefApi = new ScriptDefApiImpl(orcsApi);
      this.scriptResultApi = new ScriptResultApiImpl(orcsApi);
      this.testCaseApi = new TestCaseApiImpl(orcsApi);
      this.testPointApi = new TestPointApiImpl(orcsApi);
      this.tmoImportApi = new TmoImportApiImpl(orcsApi, scriptDefApi);
      this.dashboardApi = new DashboardApiImpl(scriptDefApi);
   }

   @Override
   public OrcsApi getOrcsApi() {
      return this.orcsApi;
   }

   @Override
   public ScriptSetApi getScriptProgramApi() {
      return this.scriptProgramApi;
   }

   @Override
   public ScriptBatchApi getScriptBatchApi() {
      return this.scriptBatchApi;
   }

   @Override
   public ScriptDefApi getScriptDefApi() {
      return this.scriptDefApi;
   }

   @Override
   public ScriptResultApi getScriptResultApi() {
      return this.scriptResultApi;
   }

   @Override
   public TestCaseApi getTestCaseApi() {
      return this.testCaseApi;
   }

   @Override
   public TestPointApi getTestPointApi() {
      return this.testPointApi;
   }

   @Override
   public TmoImportApi getTmoImportApi() {
      return this.tmoImportApi;
   }

   @Override
   public DashboardApi getDashboardApi() {
      return this.dashboardApi;
   }

}