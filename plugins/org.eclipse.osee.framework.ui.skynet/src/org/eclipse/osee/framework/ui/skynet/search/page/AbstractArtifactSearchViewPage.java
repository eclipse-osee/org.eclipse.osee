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

package org.eclipse.osee.framework.ui.skynet.search.page;

import org.eclipse.osee.framework.ui.skynet.search.AbstractArtifactSearchResult;
import org.eclipse.search.ui.text.AbstractTextSearchViewPage;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractArtifactSearchViewPage extends AbstractTextSearchViewPage {

   @Override
   public AbstractArtifactSearchResult getInput() {
      return (AbstractArtifactSearchResult) super.getInput();
   }

}
