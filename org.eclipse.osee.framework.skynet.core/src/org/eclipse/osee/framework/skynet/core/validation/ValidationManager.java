/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;

/**
 * @author Roberto E. Escobar
 */
public class ValidationManager {
   private static final String PLUGIN_ID = "";
   private static final String EXTENSION_ID = "";
   private static final String CLASS_NAME = "classname";

   private ExtensionDefinedObjects<IValidator> extensions;

   private ValidationManager() {
      extensions = new ExtensionDefinedObjects<IValidator>(PLUGIN_ID + "." + EXTENSION_ID, EXTENSION_ID, CLASS_NAME);
   }

   public IValidator getValidator(String id) {
      return extensions.getObjectById(id);
   }

   public List<IValidator> createValidateChain(String xml) {
      List<IValidator> validators = new ArrayList<IValidator>();
      return validators;
   }

   public boolean validate(String xml, Object toValidate, IValidationListener... listeners) {
      boolean result = true;
      for (IValidator validator : createValidateChain(xml)) {
         result &= validator.isValid();
         if (!result) {
            notifyListenersOnMessage(validator.getMessage(), validator.getLevel(), listeners);
         }
      }
      return result;
   }

   private void notifyListenersOnMessage(String message, Level level, IValidationListener... listeners) {
      if (listeners != null && listeners.length > 0) {
         for (IValidationListener listener : listeners) {
            listener.onValidateMessage(message, level);
         }
      }
   }
}
