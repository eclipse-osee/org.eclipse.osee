/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.search;

import org.eclipse.jface.resource.ImageDescriptor;

public class ArtifactSearchResult extends AbstractArtifactSearchResult {
   private final AbstractArtifactSearchQuery aQuery;

   public ArtifactSearchResult(AbstractArtifactSearchQuery job) {
      aQuery = job;
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return null;
   }

   @Override
   public String getLabel() {
      return aQuery.getResultLabel();
   }

   @Override
   public String getTooltip() {
      return getLabel();
   }

   @Override
   public AbstractArtifactSearchQuery getQuery() {
      return aQuery;
   }
}
