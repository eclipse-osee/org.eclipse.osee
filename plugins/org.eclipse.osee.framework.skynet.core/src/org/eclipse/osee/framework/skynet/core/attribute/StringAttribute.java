/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.skynet.core.attribute;

import java.io.InputStream;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.io.xml.XmlTextInputStream;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;

/**
 * @author Ryan D. Brooks
 */
public class StringAttribute extends CharacterBackedAttribute<String> {

   @Override
   public String getValue() {
      return getAttributeDataProvider().getValueAsString();
   }

   @Override
   public String convertStringToValue(String value) {
      return value;
   }

   @Override
   public String getDisplayableString() {
      if (this.isOfType(CoreAttributeTypes.WordTemplateContent)) {
         try (InputStream inputStream = new XmlTextInputStream(getValue())) {
            return Lib.inputStreamToString(new XmlTextInputStream(getValue()));
         } catch (Exception ex) {
            OseeCoreException.wrapAndThrow(ex);
         }
      }
      return getValue();
   }

   @Override
   protected String modifyWordValue(String value) {
      value = WordUtil.checkForTrackedChanges(value, getArtifact());
      value = WordUtil.removeWordMarkupSmartTags(value);
      return value;
   }

}