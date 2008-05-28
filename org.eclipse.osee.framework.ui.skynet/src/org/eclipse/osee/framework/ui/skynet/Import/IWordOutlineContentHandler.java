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
package org.eclipse.osee.framework.ui.skynet.Import;

import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;

/**
 * @author Robert A. Fisher
 */
public interface IWordOutlineContentHandler extends IExecutableExtension {

   /**
    * Opportunity to setup state data prior to an outline being processed. This method may be called many times on the
    * same object.
    * 
    * @param extractor The extractor where RoughArtifacts that are created can be added to
    * @param headingDescriptor The descriptor to use for headings
    * @param mainDescriptor
    */
   public void init(WordOutlineExtractor extractor, ArtifactType headingDescriptor, ArtifactType mainDescriptor);

   /**
    * Opportunity to release resources. This method may be called many times on the same object.
    */
   public void dispose();

   /**
    * Provides a name that can be displayed in the UI for the user to make a decision on which extension point should be
    * used.
    */
   public String getName();

   /**
    * Called as content is parsed out of a WordML source. A typical action to take would be to create a RoughArtifact
    * and initialize it with data from the content, or to append the content to the last, or a prior created
    * RoughArtifact.
    * 
    * @param forceBody
    * @param forcePrimaryType
    * @param headerNumber
    * @param listIdentifier
    * @param paragraphStyle
    * @param content
    * @param isParagraph
    */
   public void processContent(boolean forceBody, boolean forcePrimaryType, String headerNumber, String listIdentifier, String paragraphStyle, String content, boolean isParagraph);
}
