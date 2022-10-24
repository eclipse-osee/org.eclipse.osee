/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.define.operations.publishing.templatemanager;

import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import org.eclipse.osee.define.api.publishing.templatemanager.PublishingTemplate;
import org.eclipse.osee.define.api.publishing.templatemanager.RendererOptions;
import org.eclipse.osee.define.api.publishing.templatemanager.TemplateContent;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * A common internal interface for Publishing Template implementations used by the various
 * {@link PublishingTemplateProvider} implementations.
 *
 * @author Loren K. Ashley
 */

interface PublishingTemplateInternal extends ToMessage {

   /**
    * Creates a {@link PublishingTemplate} object populated with the standard publishing template data.
    *
    * @return a populated {@link PublishingTemplate} object.
    */

   PublishingTemplate getBean();

   /**
    * Gets the publishing template's unique identifier.
    *
    * @return publishing template identifier.
    */

   String getIdentifier();

   /**
    * Gets a scalar key supplier as an {@link Iterator} for the Publishing Template's identifier.
    *
    * @return an {@link Iterator} that returns the Publishing Template's identifier.
    */

   default Iterator<String> getIdentifierKeyExtractor() {
      return this.makeScalarKeyIterator(this::getIdentifier);
   }

   /**
    * Gets the publishing template's name. Name's are not guaranteed to be unique.
    *
    * @return the name of the publishing template.
    */

   String getName();

   /**
    * Gets a scalar key supplier as an {@link Iterator} for the Publishing Template's name.
    *
    * @return an {@link Iterator} that returns the Publishing Template's name.
    */

   default Iterator<String> getNameKeyExtractor() {
      return this.makeScalarKeyIterator(this::getName);
   }

   /**
    * Gets the {@link RendererOptions} specified in the publishing template.
    *
    * @return the renderer options.
    */

   RendererOptions getRendererOptions();

   /**
    * Gets the publishing template's name. Name's are not guaranteed to be unique.
    *
    * @return the name of the publishing template.
    */

   String getSafeName();

   /**
    * Gets a scalar key supplier as an {@link Iterator} for the Publishing Template's safe name.
    *
    * @return an {@link Iterator} that returns the Publishing Template's safe name.
    */

   default Iterator<String> getSafeNameKeyExtractor() {
      return this.makeScalarKeyIterator(this::getSafeName);
   }

   /**
    * Gets the publishing template's style string.
    *
    * @return the publishing style string.
    */

   String getStyle();

   /**
    * Gets the publishing template's WordML content as a {@link String}.
    *
    * @return the WordML content.
    */

   TemplateContent getTemplateContent();

   /**
    * Gets an unmodifiable {@link List} of the Publishing Template's match criteria {@link String}s.
    *
    * @return a {@link List} of the Publishing Template's match criteria {@link String}s.
    */

   List<String> getTemplateMatchCriteria();

   /**
    * Gets a vector key supplier as an {@link Iterator} for the Publishing Template's match criteria.
    *
    * @return an {@link Iterator} that returns the Publishing Template's match criteria.
    */

   default Iterator<String> getTemplateMatchCriteriaKeyExtractor() {
      return this.makeVectorKeyIterator(this::getTemplateMatchCriteria);
   }

   /**
    * Makes a new {@link Iterator} for scalar keys.
    *
    * @param keySupplier the scalar key supplier.
    * @return an {@link Iterator} that returns only one value, the value from the <code>keySupplier</code>.
    */

   default Iterator<String> makeScalarKeyIterator(Supplier<String> keySupplier) {
      return new Iterator<String>() {

         boolean first = true;

         @Override
         public boolean hasNext() {
            return this.first;
         }

         @Override
         public String next() {
            this.first = false;
            return keySupplier.get();
         }
      };
   }

   /**
    * Makes a new {@link Iterator} for vector keys.
    *
    * @param keyListSupplier the vector key supplier.
    * @return an {@link Iterator} that returns keys on the key vector provided by the <code>keyListSupplier</code>.
    */

   default Iterator<String> makeVectorKeyIterator(Supplier<List<String>> keyListSupplier) {
      return keyListSupplier.get().iterator();
   }

}

/* EOF */
