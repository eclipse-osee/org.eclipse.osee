/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.framework.ui.skynet.render;

import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.ContentUrl;
import static org.eclipse.osee.framework.ui.skynet.render.PresentationType.GENERALIZED_EDIT;
import static org.eclipse.osee.framework.ui.skynet.render.PresentationType.GENERAL_REQUESTED;
import static org.eclipse.osee.framework.ui.skynet.render.PresentationType.PRODUCE_ATTRIBUTE;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Ryan D. Brooks
 */
public class UrlRenderer extends DefaultArtifactRenderer {

   @Override
   public UrlRenderer newInstance() {
      return new UrlRenderer();
   }

   @Override
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact) throws OseeCoreException {
      if (!presentationType.matches(GENERALIZED_EDIT, GENERAL_REQUESTED, PRODUCE_ATTRIBUTE) && artifact.isAttributeTypeValid(ContentUrl)) {
         return SUBTYPE_TYPE_MATCH;
      }
      return NO_MATCH;
   }
}