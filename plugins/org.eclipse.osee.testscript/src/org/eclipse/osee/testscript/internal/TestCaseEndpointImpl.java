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
import org.eclipse.osee.testscript.TestCaseApi;
import org.eclipse.osee.testscript.TestCaseEndpoint;

/**
 * @author Stephen J. Molaro
 */
public class TestCaseEndpointImpl implements TestCaseEndpoint {

   private final TestCaseApi testCaseTypeApi;
   private final BranchId branch;
   public TestCaseEndpointImpl(BranchId branch, TestCaseApi testCaseTypeApi) {
      this.testCaseTypeApi = testCaseTypeApi;
      this.branch = branch;
   }

   @Override
   public Collection<TestCaseToken> getAllTestCaseTypes(String filter, ArtifactId viewId, long pageNum, long pageSize,
      AttributeTypeToken orderByAttributeType) {
      viewId = viewId == null ? ArtifactId.SENTINEL : viewId;
      if (Strings.isValid(filter)) {
         return testCaseTypeApi.getAllByFilter(branch, viewId, filter, pageNum, pageSize, orderByAttributeType);
      }
      return testCaseTypeApi.getAll(branch, viewId, pageNum, pageSize, orderByAttributeType);
   }

   @Override
   public TestCaseToken getTestCaseType(ArtifactId testCaseTypeId) {
      return testCaseTypeApi.get(branch, testCaseTypeId);
   }

   @Override
   public int getCount(String filter, ArtifactId viewId) {
      return testCaseTypeApi.getCountWithFilter(branch, viewId, filter);
   }

}
