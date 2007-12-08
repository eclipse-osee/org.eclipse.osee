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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;

/**
 * @author Robert A. Fisher
 */
public abstract class WordOutlineContentHandler implements IWordOutlineContentHandler {
   protected WordOutlineExtractor extractor;
   protected ArtifactSubtypeDescriptor headingDescriptor;
   protected ArtifactSubtypeDescriptor mainDescriptor;
   private String name;

   public WordOutlineContentHandler() {
      this.extractor = null;
      this.headingDescriptor = null;
      this.mainDescriptor = null;
      this.name = null;
   }

   /**
    * Returns the name that was in the extension point. Clients may re-implement this method.
    */
   public String getName() {
      if (name == null) {
         throw new IllegalStateException("Not yet initialized");
      }
      return name;
   }

   /**
    * Setup the name from the extension point.
    */
   public final void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
      name = config.getAttribute("name");
      if (name == null || name.equals("")) {
         name = "<no name>";
      }
   }

   /**
    * Get rid of references setup in init. Subclasses should extend this method if any other resources should be
    * released.
    */
   public void dispose() {
      extractor = null;
      headingDescriptor = null;
      mainDescriptor = null;
   }

   /**
    * Save off references. Subclasses should extend this method if anyother resources need to be setup.
    */
   public void init(WordOutlineExtractor extractor, ArtifactSubtypeDescriptor headingDescriptor, ArtifactSubtypeDescriptor mainDescriptor) {
      this.extractor = extractor;
      this.headingDescriptor = headingDescriptor;
      this.mainDescriptor = mainDescriptor;
   }
}
