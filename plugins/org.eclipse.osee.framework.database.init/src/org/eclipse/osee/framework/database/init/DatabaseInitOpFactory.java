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
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public class DatabaseInitOpFactory {

   private DatabaseInitOpFactory() {
      // static factory
   }

   public static void executeWithoutPrompting(IDbInitChoiceEnum choice) throws OseeCoreException {
      executeOperation(choice.name(), false);
   }

   public static void executeWithoutPrompting(String choice) throws OseeCoreException {
      executeOperation(choice, false);
   }

   public static void executeWithPromptsAndChoice(String choice) throws OseeCoreException {
      executeOperation(choice, true);
   }

   public static void executeWithPrompts() throws OseeCoreException {
      executeOperation(null, true);
   }

   public static void executeConfigureFromJvmProperties() throws OseeCoreException {
      boolean arePromptsAllowed = OseeClientProperties.promptOnDbInit();
      String predefinedChoice = OseeClientProperties.getChoiceOnDbInit();
      executeOperation(predefinedChoice, arePromptsAllowed);
   }

   private static void executeOperation(String predefinedChoice, boolean isPromptEnabled) throws OseeCoreException {
      IOperation operation = new DatabaseInitializationOperation(predefinedChoice, isPromptEnabled);
      Operations.executeWorkAndCheckStatus(operation);
   }
}
