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
package org.eclipse.osee.display.api.components;

import org.eclipse.osee.display.api.data.DisplayOptions;
import org.eclipse.osee.display.api.data.SearchResultMatch;
import org.eclipse.osee.display.api.data.ViewArtifact;

/**
 * @author Shawn F. Cook
 */
public interface SearchResultComponent extends DisplaysErrorComponent {

   void setArtifact(ViewArtifact artifact);

   void addSearchResultMatch(SearchResultMatch match);

   void setDisplayOptions(DisplayOptions options);
}
