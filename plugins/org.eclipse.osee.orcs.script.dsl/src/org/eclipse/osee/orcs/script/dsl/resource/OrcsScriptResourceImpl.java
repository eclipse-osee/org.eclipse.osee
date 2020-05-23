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

package org.eclipse.osee.orcs.script.dsl.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.script.dsl.OrcsScriptDslResource;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScript;

/**
 * @author Roberto E. Escobar
 */
public class OrcsScriptResourceImpl implements OrcsScriptDslResource {

   private final Resource resource;
   private List<String> additionalErrors;

   public OrcsScriptResourceImpl(Resource resource) {
      this.resource = resource;
   }

   public Resource getResource() {
      return resource;
   }

   public void error(Throwable th, String message, Object... args) {
      StringBuilder builder = new StringBuilder();
      if (args != null && args.length > 0) {
         try {
            builder.append(String.format(message, args));
         } catch (Exception ex) {
            builder.append(Lib.exceptionToString(ex));
         }
      } else {
         builder.append(message);
      }
      if (th != null) {
         builder.append(" ");
         builder.append(Lib.exceptionToString(th));
      }
      String error = builder.toString();
      if (Strings.isValid(error)) {
         if (additionalErrors == null) {
            additionalErrors = new ArrayList<>();
         }
         additionalErrors.add(error);
      }
   }

   @Override
   public Collection<String> getErrors() {
      List<String> errors = null;
      if (additionalErrors != null && !additionalErrors.isEmpty()) {
         errors = new LinkedList<>(additionalErrors);
      }
      if (resource != null) {
         if (errors == null) {
            errors = new LinkedList<>();
         }
         for (org.eclipse.emf.ecore.resource.Resource.Diagnostic diagnostic : resource.getErrors()) {
            errors.add(diagnostic.toString());
         }
      } else if (errors == null) {
         errors = Collections.emptyList();
      }
      return errors;
   }

   @Override
   public OrcsScript getModel() {
      OrcsScript script = null;
      if (resource != null) {
         EList<EObject> contents = resource.getContents();
         if (contents != null && !contents.isEmpty()) {
            script = (OrcsScript) contents.get(0);
         }
      }
      return script;
   }

   @Override
   public boolean hasErrors() {
      boolean result = additionalErrors != null && !additionalErrors.isEmpty();
      if (!result) {
         EList<org.eclipse.emf.ecore.resource.Resource.Diagnostic> errors = resource.getErrors();
         result = errors != null && !errors.isEmpty();
      }
      return result;
   }
}