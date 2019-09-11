/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import java.io.InputStream;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.io.xml.XmlTextInputStream;

/**
 * @author Ryan D. Brooks
 */
public class AttributeTypeString extends AbstractAttributeType<String> {

   public AttributeTypeString(Long id, NamespaceToken namespace, String name, String mediaType, String description, TaggerTypeToken taggerType) {
      super(id, namespace, name, mediaType, description, taggerType);
   }

   @Override
   public String valueFromStorageString(String storedValue) {
      return storedValue;
   }

   @Override
   public String getDisplayableString(String value) {
      if (equals(CoreAttributeTypes.WordTemplateContent)) {
         try (InputStream inputStream = new XmlTextInputStream(value)) {
            return Lib.inputStreamToString(new XmlTextInputStream(value));
         } catch (Exception ex) {
            OseeCoreException.wrapAndThrow(ex);
         }
      }
      return value;
   }
}