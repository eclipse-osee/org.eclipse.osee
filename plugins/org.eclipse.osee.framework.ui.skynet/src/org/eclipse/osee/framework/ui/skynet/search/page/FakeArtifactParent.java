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

package org.eclipse.osee.framework.ui.skynet.search.page;

/**
 * @author Derric L. Tubbs
 */
public class FakeArtifactParent {
   private final String name;

   public FakeArtifactParent(String name) {
      this.name = name;
   }

   public String getName() {
      return name;
   }
}
