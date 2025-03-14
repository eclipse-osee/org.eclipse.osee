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

package org.eclipse.osee.testscript;

import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.testscript.ats.AtsScriptApi;

/**
 * @author Stephen J. Molaro
 */
public interface ScriptApi {
   OrcsApi getOrcsApi();

   AtsScriptApi getAtsScriptApi();

   ScriptSetApi getScriptProgramApi();

   ScriptBatchApi getScriptBatchApi();

   ScriptDefApi getScriptDefApi();

   ScriptResultApi getScriptResultApi();

   TestCaseApi getTestCaseApi();

   TestPointApi getTestPointApi();

   TmoImportApi getTmoImportApi();

   DashboardApi getDashboardApi();

   ScriptConfigApi getScriptConfigApi();

   ScriptPurgeApi getScriptPurgeApi();

   TmoFileApi getTmoFileApi();

}