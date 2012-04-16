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
package org.eclipse.osee.framework.access.provider.internal;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.IAccessContextId;
import org.eclipse.osee.framework.core.dsl.integration.AccessModelInterpreter;
import org.eclipse.osee.framework.core.dsl.integration.OseeDslAccessModel;
import org.eclipse.osee.framework.core.dsl.integration.OseeDslProvider;
import org.eclipse.osee.framework.core.dsl.integration.RoleContextProvider;
import org.eclipse.osee.framework.core.dsl.ui.integration.operations.OseeDslRoleContextProvider;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.core.model.access.AccessModel;
import org.eclipse.osee.framework.core.model.access.HasAccessModel;
import org.eclipse.osee.framework.core.services.CmAccessControl;
import org.eclipse.osee.framework.core.util.Conditions;

/**
 * @author John Misinco
 */
public class FrameworkAccessControlProxy implements CmAccessControl, HasAccessModel {

   private AccessModelInterpreter interpreter;
   private CmAccessControl frameworkAccessControl;
   private AccessModel accessModel;

   public void setAccessModelInterpreter(AccessModelInterpreter interpreter) {
      this.interpreter = interpreter;
   }

   public void start() {
      OseeDslProvider frameworkDslProvider = new FrameworkDslProvider("osee:/xtext/framework.access.osee");
      RoleContextProvider roleProvider = new OseeDslRoleContextProvider(frameworkDslProvider);

      accessModel = new OseeDslAccessModel(interpreter, frameworkDslProvider);
      frameworkAccessControl = new FrameworkAccessControl(roleProvider);
   }

   public void stop() {
      frameworkAccessControl = null;
      accessModel = null;
   }

   private void checkInitialized() throws OseeCoreException {
      Conditions.checkNotNull(frameworkAccessControl, "frameworkAccess",
         "FrameworkAccessControlService not properly initialized");
   }

   @Override
   public AccessModel getAccessModel() {
      return accessModel;
   }

   @Override
   public boolean isApplicable(IBasicArtifact<?> user, Object object) {
      return frameworkAccessControl.isApplicable(user, object);
   }

   @Override
   public Collection<? extends IAccessContextId> getContextId(IBasicArtifact<?> user, Object object) throws OseeCoreException {
      checkInitialized();
      return frameworkAccessControl.getContextId(user, object);
   }

}
