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
import org.eclipse.osee.define.api.publishing.templatemanager.PublishingTemplateKeyGroup;
import org.eclipse.osee.define.api.publishing.templatemanager.PublishingTemplateKeyType;
import org.eclipse.osee.define.api.publishing.templatemanager.PublishingTemplateScalarKey;
import org.eclipse.osee.define.api.publishing.templatemanager.PublishingTemplateVectorKey;
import org.eclipse.osee.framework.core.publishing.PublishingTemplate;
import org.eclipse.osee.framework.core.publishing.RendererOptions;
import org.eclipse.osee.framework.core.publishing.TemplateContent;
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

   PublishingTemplateScalarKey getIdentifier();

   /**
    * Gets a scalar key supplier as an {@link Iterator} for the Publishing Template's identifier.
    *
    * @return an {@link Iterator} that returns the Publishing Template's identifier.
    */

   default Iterable<PublishingTemplateScalarKey> getIdentifierKeyExtractor() {
      return this.makeScalarKeyIterable(this::getIdentifier);
   }

   /**
    * Gets the publishing template's name. Name's are not guaranteed to be unique.
    *
    * @return the name of the publishing template.
    */

   PublishingTemplateScalarKey getName();

   /**
    * Gets a scalar key supplier as an {@link Iterator} for the Publishing Template's name.
    *
    * @return an {@link Iterator} that returns the Publishing Template's name.
    */

   Iterable<PublishingTemplateScalarKey> getKeyIterable(PublishingTemplateKeyType keyType);

   /**
    * Gets a scalar key supplier as an {@link Iterator} for the Publishing Template's name.
    *
    * @return an {@link Iterator} that returns the Publishing Template's name.
    */

   default Iterable<PublishingTemplateScalarKey> getNameKeyExtractor() {
      return this.makeScalarKeyIterable(this::getName);
   }

   /**
    * Gets a new {@link PublishingTemplateKeyGroup} with all the cache keys for the publishing template.
    *
    * @return a {@link PublishingTemplateKeyGroup} will all of the publishing template cache keys.
    */

   default PublishingTemplateKeyGroup getPublishingTemplateKeyGroup() {
      return new PublishingTemplateKeyGroup(this.getIdentifier(), this.getMatchCriteria(), this.getName(),
         this.getSafeName());
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

   PublishingTemplateScalarKey getSafeName();

   /**
    * Gets a scalar key supplier as an {@link Iterator} for the Publishing Template's safe name.
    *
    * @return an {@link Iterator} that returns the Publishing Template's safe name.
    */

   default Iterable<PublishingTemplateScalarKey> getSafeNameKeyExtractor() {
      return this.makeScalarKeyIterable(this::getSafeName);
   }

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

   PublishingTemplateVectorKey getMatchCriteria();

   /**
    * Gets a vector key supplier as an {@link Iterator} for the Publishing Template's match criteria.
    *
    * @return an {@link Iterator} that returns the Publishing Template's match criteria.
    */

   default Iterable<PublishingTemplateScalarKey> getMatchCriteriaKeyExtractor() {
      return this.makeVectorKeyIterable(this::getMatchCriteria);
   }

   /**
    * Makes a new {@link Iterator} for scalar keys.
    *
    * @param scalarKeySupplier the scalar key supplier.
    * @return an {@link Iterator} that returns only one value, the value from the <code>keySupplier</code>.
    */

   default Iterable<PublishingTemplateScalarKey> makeScalarKeyIterable(Supplier<PublishingTemplateScalarKey> scalarKeySupplier) {
      //@formatter:off
      return
         new Iterable<PublishingTemplateScalarKey> () {

         @Override
         public Iterator<PublishingTemplateScalarKey> iterator() {

            return
               new Iterator<PublishingTemplateScalarKey>() {

                  boolean first = true;
                  Supplier<PublishingTemplateScalarKey> iteratorKeySupplier = scalarKeySupplier;

                  @Override
                  public boolean hasNext() {
                     return this.first;
                  }

                  @Override
                  public PublishingTemplateScalarKey next() {
                     this.first = false;
                     return this.iteratorKeySupplier.get();
                  }
               };
         }
      };
      //@formatter:on
   }

   /**
    * Makes a new {@link Iterator} for vector keys.
    *
    * @param keyListSupplier the vector key supplier.
    * @return an {@link Iterator} that returns keys on the key vector provided by the <code>keyListSupplier</code>.
    */

   default Iterable<PublishingTemplateScalarKey> makeVectorKeyIterable(Supplier<PublishingTemplateVectorKey> vectorKeySupplier) {
      //@formatter:off
      return
         new Iterable<PublishingTemplateScalarKey> () {

         @Override
         public Iterator<PublishingTemplateScalarKey> iterator() {
            return vectorKeySupplier.get().getKey().iterator();
         }
      };
   }

}

/* EOF */
