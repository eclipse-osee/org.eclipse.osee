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
package org.eclipse.osee.ats.ui.api.view;

import org.eclipse.osee.ats.ui.api.data.AtsSearchParameters;
import org.eclipse.osee.display.api.components.SearchHeaderComponent;
import org.eclipse.osee.display.api.data.ViewId;

/**
 * @author John Misinco
 */
public interface AtsSearchHeaderComponent extends SearchHeaderComponent {

   void addProgram(ViewId program);

   void clearBuilds();

   void addBuild(ViewId build);

   void setSearchCriteria(AtsSearchParameters params);

}
