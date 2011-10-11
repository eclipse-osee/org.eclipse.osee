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
package org.eclipse.osee.ats.api.components;

import org.eclipse.osee.display.api.components.SearchHeaderComponent;
import org.eclipse.osee.display.api.data.WebId;

/**
 * @author John Misinco
 */
public interface AtsSearchHeaderComponentInterface extends SearchHeaderComponent {

   void addProgram(WebId program);

   void clearBuilds();

   void addBuild(WebId build);

   void setSearchCriteria(WebId program, WebId build, boolean nameOnly, String searchPhrase);

   void setProgram(WebId program);

   void setBuild(WebId build);

}
