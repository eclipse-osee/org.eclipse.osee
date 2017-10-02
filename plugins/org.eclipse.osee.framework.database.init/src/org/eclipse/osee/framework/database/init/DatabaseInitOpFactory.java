/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.database.init;

import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.database.init.internal.DatabaseInitializationOperation;

/**
 * @author Roberto E. Escobar
 */
public class DatabaseInitOpFactory {

   private DatabaseInitOpFactory() {
      // static factory
   }

   public static void executeWithoutPrompting(IDbInitChoiceEnum choice) {
      executeOperation(choice.name(), false);
   }

   public static void executeWithoutPrompting(String choice) {
      executeOperation(choice, false);
   }

   public static void executeWithPromptsAndChoice(String choice) {
      executeOperation(choice, true);
   }

   public static void executeWithPrompts() {
      executeOperation(null, true);
   }

   public static void executeConfigureFromJvmProperties() {
      boolean arePromptsAllowed = OseeClientProperties.promptOnDbInit();
      String predefinedChoice = OseeClientProperties.getChoiceOnDbInit();
      executeOperation(predefinedChoice, arePromptsAllowed);
   }

   private static void executeOperation(String predefinedChoice, boolean isPromptEnabled) {
      IOperation operation = new DatabaseInitializationOperation(predefinedChoice, isPromptEnabled);
      Operations.executeWorkAndCheckStatus(operation);
   }
}
