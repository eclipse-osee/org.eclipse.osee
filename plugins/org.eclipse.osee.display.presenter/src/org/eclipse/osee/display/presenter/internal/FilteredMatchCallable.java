/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.display.presenter.internal;

import java.util.Collection;
import org.eclipse.osee.display.presenter.ArtifactFilter;
import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Filter;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.search.Match;

/**
 * @author Roberto E. Escobar
 */
public class FilteredMatchCallable extends CancellableCallable<Collection<Match<ArtifactReadable, AttributeReadable<?>>>> implements Filter<Match<ArtifactReadable, AttributeReadable<?>>> {

   private final Collection<Match<ArtifactReadable, AttributeReadable<?>>> toSanitize;
   private final ArtifactFilter sanitizer;

   public FilteredMatchCallable(ArtifactFilter sanitizer, Collection<Match<ArtifactReadable, AttributeReadable<?>>> toSanitize) {
      this.sanitizer = sanitizer;
      this.toSanitize = toSanitize;
   }

   @Override
   public Collection<Match<ArtifactReadable, AttributeReadable<?>>> call() throws Exception {
      Collections.filter(toSanitize, this);
      return toSanitize;
   }

   @Override
   public boolean accept(Match<ArtifactReadable, AttributeReadable<?>> item) throws Exception {
      checkForCancelled();
      return sanitizer.accept(item.getItem());
   }
}