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
package org.eclipse.osee.framework.skynet.core.change;

import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Jeff C. Phillips
 * @author Wilik Karol
 */
public class AttributeChangeWorker implements IChangeWorker {

   private final Change change;
   private final Artifact artifact;

   public AttributeChangeWorker(Change change, Artifact artifact) {
      this.change = change;
      this.artifact = artifact;
   }

   @Override
   public void revert() {
      if (change.isBaseline()) {
         artifact.getAttributeById(change.getItemId().getId(), true).replaceWithVersion(
            change.getBaselineGamma().getId().intValue());
      } else {
         artifact.getAttributeById(change.getItemId().getId(), true).delete();
      }
   }
}
