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
package org.eclipse.osee.ats.api.search;

import org.eclipse.osee.ats.api.components.AtsSearchHeaderComponent;
import org.eclipse.osee.ats.api.data.AtsSearchParameters;
import org.eclipse.osee.display.api.data.ViewId;
import org.eclipse.osee.display.api.search.SearchPresenter;

/*
 * @author John Misinco
 */
public interface AtsSearchPresenter<T extends AtsSearchHeaderComponent, K extends AtsSearchParameters> extends SearchPresenter<T, K> {

   void selectProgram(ViewId program, T headerComponent);

}
