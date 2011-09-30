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
import org.eclipse.osee.display.api.data.WebId;

/*
 * @author John Misinco
 */
public interface SearchPresenter {

   void initSearchHome(SearchHeaderComponent searchHeaderComp);

   void initSearchResults(String url, SearchHeaderComponent searchHeaderComp, SearchResultsListComponent searchResultsComp);

   void selectArtifact(WebId id);

   void initArtifactPage(String url, SearchHeaderComponent searchHeaderComp, ArtifactHeaderComponent artHeaderComp, RelationComponent relComp, AttributeComponent attrComp);

   void selectRelationType(WebId id);

}