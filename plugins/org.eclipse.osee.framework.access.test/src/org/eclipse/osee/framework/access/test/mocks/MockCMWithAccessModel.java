/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.framework.access.test.mocks;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AccessContextToken;
import org.eclipse.osee.framework.core.model.access.AccessModel;
import org.eclipse.osee.framework.core.model.access.HasAccessModel;

/**
 * @author Roberto E. Escobar
 */
public final class MockCMWithAccessModel extends MockConfigurationManagement implements HasAccessModel {

   private final AccessModel accessModel;
   private boolean wasGetAccessModelCalled;

   public MockCMWithAccessModel(AccessModel accessModel, ArtifactToken expectedUser, Object expectedObject, boolean isApplicable, Collection<AccessContextToken> contextIds) {
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