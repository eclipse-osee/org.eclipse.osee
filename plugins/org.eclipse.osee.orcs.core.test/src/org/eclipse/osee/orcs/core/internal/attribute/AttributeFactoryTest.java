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
package org.eclipse.osee.orcs.core.internal.attribute;

import org.eclipse.osee.framework.core.data.NamedIdentity;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.cache.AttributeTypeCache;
import org.eclipse.osee.framework.core.model.mocks.MockOseeDataAccessor;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.model.type.AttributeTypeFactory;
import org.eclipse.osee.framework.core.model.type.OseeEnumType;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.AttributeContainer;
import org.eclipse.osee.orcs.core.ds.AttributeRow;
import org.eclipse.osee.orcs.core.internal.attribute.primitives.PrimitiveAttributeClassProvider;
import org.eclipse.osee.orcs.core.mocks.MockLog;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test Case for {@link AttributeFactory}
 * 
 * @author Roberto E. Escobar
 */
public class AttributeFactoryTest {

   @Test
   @Ignore
   public void test() throws OseeCoreException {
      Log logger = new MockLog();
      AttributeTypeCache cache = new AttributeTypeCache(new MockOseeDataAccessor<Long, AttributeType>());
      AttributeClassResolver resolver = new AttributeClassResolver();
      resolver.addProvider(new PrimitiveAttributeClassProvider());
      AttributeFactory factory = new AttributeFactory(logger, resolver, cache);
      AttributeTypeFactory typeFactory = new AttributeTypeFactory();
      typeFactory.createOrUpdate(cache, CoreAttributeTypes.Name.getGuid(), CoreAttributeTypes.Name.getName(),
         "StringAttribute", "name", "name", "name", new OseeEnumType(CoreAttributeTypes.Name.getGuid(),
            CoreAttributeTypes.Name.getName()), 1, 1, "", "");
      //      typeFactory.create(guid, name, baseAttributeTypeId, attributeProviderNameId, fileTypeExtension, defaultValue, minOccurrences, maxOccurrences, tipText, taggerId)
      //      AttributeType type = new AttributeType(guid, typeName, baseAttributeTypeId, attributeProviderNameId, fileTypeExtension, defaultValue, minOccurrences, maxOccurrences, description, taggerId).createAttributeType();
      //      cache.cache(type);
      NamedIdentity<String> namedIdentity = new NamedIdentity<String>("something", "name");
      AttributeContainer container = new AttributeContainerImpl(namedIdentity);
      AttributeRow row = new AttributeRow();
      row.setAttrTypeUuid(CoreAttributeTypes.Name.getGuid());
      row.getArtifactId();
      //      factory.loadAttribute(container, row);
      System.out.println("check the container");
   }
}
