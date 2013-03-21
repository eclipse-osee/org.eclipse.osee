/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
