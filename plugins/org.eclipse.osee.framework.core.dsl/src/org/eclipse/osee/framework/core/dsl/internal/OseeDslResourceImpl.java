/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.framework.core.dsl.internal;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.osee.framework.core.dsl.OseeDslResource;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl;

/**
 * @author Roberto E. Escobar
 */
public class OseeDslResourceImpl implements OseeDslResource {

   private final Resource resource;

   public OseeDslResourceImpl(Resource resource) {
      this.resource = resource;
   }

   public Resource getResource() {
      return resource;
   }

   @Override
   public Collection<String> getErrors() {
      List<String> errors = new LinkedList<>();
      for (org.eclipse.emf.ecore.resource.Resource.Diagnostic diagnostic : resource.getErrors()) {
         errors.add(diagnostic.toString());
      }
      return errors;
   }

   @Override
   public OseeDsl getModel() {
      EList<EObject> contents = resource.getContents();
      return !contents.isEmpty() ? (OseeDsl) contents.get(0) : null;
   }
}
