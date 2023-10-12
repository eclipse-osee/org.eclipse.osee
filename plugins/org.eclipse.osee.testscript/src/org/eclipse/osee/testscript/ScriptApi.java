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

/**
 * @author Stephen J. Molaro
 */
public interface ScriptApi {
   OrcsApi getOrcsApi();

   ScriptProgramApi getScriptProgramApi();

   ScriptDefApi getScriptDefApi();

   ScriptResultApi getScriptResultApi();

   TestCaseApi getTestCaseApi();

   TestPointApi getTestPointApi();

   TmoImportApi getTmoImportApi();

}