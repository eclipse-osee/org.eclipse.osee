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
package org.eclipse.osee.framework.core.dsl.integration;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.IAccessContextId;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl;
import org.eclipse.osee.framework.core.model.access.AccessData;
import org.eclipse.osee.framework.core.model.access.AccessDetail;
import org.eclipse.osee.framework.core.model.access.AccessDetailCollector;
import org.eclipse.osee.framework.core.model.access.AccessModel;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Roberto E. Escobar
 */
public class OseeDslAccessModel implements AccessModel {
   private final AccessModelInterpreter interpreter;
   private final OseeDslProvider dslProvider;

   public OseeDslAccessModel(AccessModelInterpreter interpreter, OseeDslProvider dslProvider) {
      this.interpreter = interpreter;
      this.dslProvider = dslProvider;
   }

   @Override
   public void computeAccess(IAccessContextId contextId, Collection<Object> objectsToCheck, AccessData accessData) {
      Conditions.checkNotNull(contextId, "contextId");
      Conditions.checkNotNull(objectsToCheck, "objectsToCheck");
      Conditions.checkNotNull(accessData, "accessData");

      OseeDsl oseeDsl = dslProvider.getDsl();
      Conditions.checkNotNull(oseeDsl, "oseeDsl", "dsl provider returned null");

      Collection<AccessContext> contexts = oseeDsl.getAccessDeclarations();
      AccessContext context = interpreter.getContext(contexts, contextId);
      Conditions.checkNotNull(context, "interpreted accessContext",
         "No matching access context was found in access dsl for [%s]", contextId);

      for (Object objectToCheck : objectsToCheck) {
         List<AccessDetail<?>> accessDetail = new LinkedList<>();
         AccessDetailCollector collector = new AccessDataCollector(accessDetail);
         interpreter.computeAccessDetails(collector, context, objectToCheck);
         accessData.addAll(objectToCheck, accessDetail);
      }
   }

   private static final class AccessDataCollector implements AccessDetailCollector {
      private final Collection<AccessDetail<?>> accessDetails;

      public AccessDataCollector(Collection<AccessDetail<?>> accessDetails) {
         this.accessDetails = accessDetails;
      }

      @Override
      public void collect(AccessDetail<?> accessDetail) {
         if (accessDetail != null) {
            accessDetails.add(accessDetail);
         }
      }
   }
}