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

import org.eclipse.osee.display.api.data.WebArtifact;
import org.eclipse.osee.display.api.data.WebId;

/**
 * @author Shawn F. Cook
 */
public interface RelationComponent extends DisplaysErrorComponent {

   void setArtifact(WebArtifact artifact);

   void clearAll();

   void addRelationType(WebId id);

   void clearRelations();

   void setLeftName(String name);

   void setRightName(String name);

   void addLeftRelated(WebArtifact id);

   void addRightRelated(WebArtifact id);

}