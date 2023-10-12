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
import org.eclipse.osee.testscript.ScriptApi;
import org.eclipse.osee.testscript.ScriptDefApi;
import org.eclipse.osee.testscript.ScriptProgramApi;
import org.eclipse.osee.testscript.ScriptResultApi;
import org.eclipse.osee.testscript.TestCaseApi;
import org.eclipse.osee.testscript.TestPointApi;
import org.eclipse.osee.testscript.TmoImportApi;

/**
 * @author Stephen J. Molaro
 */
public class ScriptApiImpl implements ScriptApi {

   private OrcsApi orcsApi;
   private ScriptProgramApi scriptProgramApi;
   private ScriptDefApi scriptDefApi;
   private ScriptResultApi scriptResultApi;
   private TestCaseApi testCaseApi;
   private TestPointApi testPointApi;
   private TmoImportApi tmoImportApi;

   public void bindOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public void start() {
      this.scriptProgramApi = new ScriptProgramApiImpl(orcsApi);
      this.scriptDefApi = new ScriptDefApiImpl(orcsApi);
      this.scriptResultApi = new ScriptResultApiImpl(orcsApi);
      this.testCaseApi = new TestCaseApiImpl(orcsApi);
      this.testPointApi = new TestPointApiImpl(orcsApi);
      this.tmoImportApi = new TmoImportApiImpl(scriptDefApi);
   }

   @Override
   public OrcsApi getOrcsApi() {
      return this.orcsApi;
   }

   @Override
   public ScriptProgramApi getScriptProgramApi() {
      return this.scriptProgramApi;
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
}