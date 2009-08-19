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


/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractArtifactSourceParserDelegate implements IArtifactSourceParserDelegate {

   private IArtifactSourceParser extractor;

   public AbstractArtifactSourceParserDelegate() {
      super();
      this.extractor = null;
   }

   /**
    * Reset delegate state. Subclasses should extend this method if any other resources should be released.
    */
   @Override
   public void dispose() {
      extractor = null;
   }

   /**
    * Subclasses should extend this method if any other resources need to be setup.
    */
   @Override
   public void setExtractor(IArtifactSourceParser extractor) {
      this.extractor = extractor;
   }

   @Override
   public IArtifactSourceParser getExtractor() {
      return extractor;
   }
}
