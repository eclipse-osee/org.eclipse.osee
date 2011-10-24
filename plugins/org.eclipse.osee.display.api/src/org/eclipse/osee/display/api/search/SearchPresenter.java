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

import org.eclipse.osee.display.api.components.ArtifactHeaderComponent;
import org.eclipse.osee.display.api.components.AttributeComponent;
import org.eclipse.osee.display.api.components.DisplayOptionsComponent;
import org.eclipse.osee.display.api.components.RelationComponent;
import org.eclipse.osee.display.api.components.SearchHeaderComponent;
import org.eclipse.osee.display.api.components.SearchResultsListComponent;
import org.eclipse.osee.display.api.data.DisplayOptions;
import org.eclipse.osee.display.api.data.ViewArtifact;
import org.eclipse.osee.display.api.data.ViewId;
import org.eclipse.osee.display.api.data.ViewSearchParameters;

/*
 * @author John Misinco
 */
public interface SearchPresenter<T extends SearchHeaderComponent, K extends ViewSearchParameters> {

   void initSearchResults(String url, T searchHeaderComp, SearchResultsListComponent searchResultsComp, DisplayOptionsComponent options);

   void selectArtifact(String url, ViewArtifact artifact, SearchNavigator oseeNavigator);

   void initArtifactPage(String url, T searchHeaderComp, ArtifactHeaderComponent artHeaderComp, RelationComponent relComp, AttributeComponent attrComp, DisplayOptionsComponent options);

   void selectRelationType(ViewArtifact artifact, ViewId relation, RelationComponent relationComponent);

   void selectSearch(String url, K params, SearchNavigator navigator);

   void selectDisplayOptions(String url, DisplayOptions options, SearchNavigator navigator);

}