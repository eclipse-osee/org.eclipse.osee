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
package org.eclipse.osee.framework.skynet.core.importing.parsers;

import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.skynet.core.importing.operations.RoughArtifactCollector;

/**
 * @author Robert A. Fisher
 */
public interface IArtifactExtractorDelegate {

   /**
    * Determines whether this delegate can operate with the selected parser
    * 
    * @return whether this delegate is compatible with the parser
    */
   public boolean isApplicable(IArtifactExtractor parser);

   /**
    * Opportunity to set state data prior to a parser being processed. This method may be called many times on the same
    * object.
    * 
    * @param parser The extractor where RoughArtifacts that are created can be added to
    */
   public void initialize();

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
    */
   public void processContent(OperationLogger logger, RoughArtifactCollector collector, boolean forceBody, boolean forcePrimaryType, String headerNumber, String listIdentifier, String paragraphStyle, String content, boolean isParagraph) ;

   public void finish() ;

}
