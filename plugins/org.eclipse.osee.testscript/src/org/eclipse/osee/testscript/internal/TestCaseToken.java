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

import org.eclipse.osee.accessor.types.ArtifactAccessorResult;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;

/**
 * @author Stephen J. Molaro
 */
public class TestCaseToken extends ArtifactAccessorResult {

   public static final TestCaseToken SENTINEL = new TestCaseToken();

   private Double testNumber;

   public TestCaseToken(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public TestCaseToken(ArtifactReadable art) {
      super(art);
      this.setId(art.getId());
      this.setName(art.getName());
      this.setTestNumber(art.getSoleAttributeValue(CoreAttributeTypes.TestNumber, 0.0));
   }

   public TestCaseToken(Long id, String name) {
      super(id, name);
      this.setTestNumber(0.0);
   }

   public TestCaseToken() {
      super();
   }

   /**
    * @return the testNumber
    */
   public Double getTestNumber() {
      return testNumber;
   }

   /**
    * @param testNumber the testNumber to set
    */
   public void setTestNumber(Double testNumber) {
      this.testNumber = testNumber;
   }
}