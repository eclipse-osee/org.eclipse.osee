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
package org.eclipse.osee.display.api.search;

import java.util.List;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.search.Match;

/**
 * @author John R. Misinco
 */
public interface AsyncSearchListener {

   public void onSearchComplete(List<Match<ArtifactReadable, AttributeReadable<?>>> results);

   public void onSearchCancelled();

   public void onSearchFailed(Throwable throwable);

}