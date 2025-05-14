/*********************************************************************
 * Copyright (c) 2025 Boeing
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
package org.eclipse.osee.ats.ide.integration.tests.publishing.markdown;

public class ApplicabilityTagTestCase {
   public long productId;
   public boolean expectsLight;
   public String expectedSpeakerText;
   public String expectedSpeakerGroupText;

   public ApplicabilityTagTestCase(long productId, boolean expectsLight, String expectedSpeakerText, String expectedSpeakerGroupText) {
      this.productId = productId;
      this.expectsLight = expectsLight;
      this.expectedSpeakerText = expectedSpeakerText;
      this.expectedSpeakerGroupText = expectedSpeakerGroupText;
   }
}
