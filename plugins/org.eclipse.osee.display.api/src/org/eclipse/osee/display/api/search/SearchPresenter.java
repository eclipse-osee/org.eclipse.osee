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
import org.eclipse.osee.display.api.components.RelationComponent;
import org.eclipse.osee.display.api.components.SearchHeaderComponent;
import org.eclipse.osee.display.api.components.SearchResultsListComponent;
import org.eclipse.osee.display.api.data.WebArtifact;
import org.eclipse.osee.display.api.data.WebId;

/*
 * @author John Misinco
 */
public interface SearchPresenter<T extends SearchHeaderComponent> {

   void initSearchResults(String url, T searchHeaderComp, SearchResultsListComponent searchResultsComp);

   void selectArtifact(WebArtifact artifact, SearchNavigator oseeNavigator);

   void initArtifactPage(String url, T searchHeaderComp, ArtifactHeaderComponent artHeaderComp, RelationComponent relComp, AttributeComponent attrComp);

   void selectRelationType(WebArtifact artifact, WebId relation, RelationComponent relationComponent);

}