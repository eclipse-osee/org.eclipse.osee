/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.search;

import java.util.logging.Level;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

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
      try {
         return aQuery.getResultLabel();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return "";
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
