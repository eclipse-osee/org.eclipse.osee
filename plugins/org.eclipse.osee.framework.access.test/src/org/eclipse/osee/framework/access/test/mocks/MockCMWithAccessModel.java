/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.access.test.mocks;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.IAccessContextId;
import org.eclipse.osee.framework.core.model.access.AccessModel;
import org.eclipse.osee.framework.core.model.access.HasAccessModel;

/**
 * @author Roberto E. Escobar
 */
public final class MockCMWithAccessModel extends MockConfigurationManagement implements HasAccessModel {

   private final AccessModel accessModel;
   private boolean wasGetAccessModelCalled;

   public MockCMWithAccessModel(AccessModel accessModel, ArtifactToken expectedUser, Object expectedObject, boolean isApplicable, Collection<IAccessContextId> contextIds) {
      super(expectedUser, expectedObject, isApplicable, contextIds);
      this.accessModel = accessModel;
   }

   @Override
   public AccessModel getAccessModel() {
      this.wasGetAccessModelCalled = true;
      return accessModel;
   }

   public boolean wasAccessModelCalled() {
      return wasGetAccessModelCalled;
   }
}