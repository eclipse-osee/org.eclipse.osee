/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.framework.core.data;

import java.io.InputStream;
import java.util.Set;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.io.xml.XmlTextInputStream;

/**
 * @author Ryan D. Brooks
 */
public final class AttributeTypeString extends AttributeTypeGeneric<String> {

   public AttributeTypeString(Long id, NamespaceToken namespace, String name, String mediaType, String description, TaggerTypeToken taggerType, String fileExtension, Set<OperationTypeToken> operationTypes, DisplayHint... displayHints) {
      super(id, namespace, name, mediaType, description, taggerType, fileExtension, "", operationTypes, displayHints);
      for (DisplayHint hint : displayHints) {
         addDisplayHint(hint);
      }
   }

   @Override
   public boolean isString() {
      return true;
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