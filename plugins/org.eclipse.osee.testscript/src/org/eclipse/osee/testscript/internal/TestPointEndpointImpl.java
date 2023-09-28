/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.testscript.internal;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.testscript.TestPointApi;
import org.eclipse.osee.testscript.TestPointEndpoint;

/**
 * @author Stephen J. Molaro
 */
public class TestPointEndpointImpl implements TestPointEndpoint {

   private final TestPointApi testPointTypeApi;
   private final BranchId branch;
   public TestPointEndpointImpl(BranchId branch, TestPointApi testPointTypeApi) {
      this.testPointTypeApi = testPointTypeApi;
      this.branch = branch;
   }

   @Override
   public Collection<TestPointToken> getAllTestPointTypes(String filter, ArtifactId viewId, long pageNum, long pageSize,
      AttributeTypeToken orderByAttributeType) {
      viewId = viewId == null ? ArtifactId.SENTINEL : viewId;
      if (Strings.isValid(filter)) {
         return testPointTypeApi.getAllByFilter(branch, viewId, filter, pageNum, pageSize, orderByAttributeType);
      }
      return testPointTypeApi.getAll(branch, viewId, pageNum, pageSize, orderByAttributeType);
   }

   @Override
   public TestPointToken getTestPointType(ArtifactId testPointTypeId) {
      return testPointTypeApi.get(branch, testPointTypeId);
   }

   @Override
   public int getCount(String filter, ArtifactId viewId) {
      return testPointTypeApi.getCountWithFilter(branch, viewId, filter);
   }

}
