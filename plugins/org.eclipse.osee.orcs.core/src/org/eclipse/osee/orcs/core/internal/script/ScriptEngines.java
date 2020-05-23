/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.orcs.core.internal.script;

import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

/**
 * @author Roberto E. Escobar
 */
public final class ScriptEngines {

   private ScriptEngines() {
      // Utility
   }

   public static final String ORCS_SCRIPT_ENGINE_ID = "orcs";

   public static ScriptEngineManager newScriptEngineManager(OrcsScriptCompiler compiler) {
      ScriptEngineFactory factory = new OrcsScriptEngineFactory(compiler);
      ScriptEngineManager manager = new ScriptEngineManager();
      registerEngine(manager, factory);
      return manager;
   }

   private static void registerEngine(ScriptEngineManager manager, ScriptEngineFactory factory) {
      for (String extension : factory.getExtensions()) {
         manager.registerEngineExtension(extension, factory);
      }
      for (String type : factory.getMimeTypes()) {
         manager.registerEngineMimeType(type, factory);
      }
      for (String name : factory.getNames()) {
         manager.registerEngineName(name, factory);
      }
   }
}
