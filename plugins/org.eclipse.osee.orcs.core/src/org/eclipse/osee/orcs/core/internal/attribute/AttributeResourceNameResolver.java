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

package org.eclipse.osee.orcs.core.internal.attribute;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.core.ds.Attribute;
import org.eclipse.osee.orcs.core.ds.ResourceNameResolver;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactImpl;

/**
 * @author Roberto E. Escobar
 */
public class AttributeResourceNameResolver implements ResourceNameResolver {
   private final static int MAX_NAME_SIZE = 60;

   private final Attribute<?> attribute;

   public AttributeResourceNameResolver(Attribute<?> attribute) {
      this.attribute = attribute;
   }

   @Override
   public String getStorageName() {
      ArtifactToken identity = attribute.getContainer();
      String guid = identity.getGuid();
      Conditions.checkExpressionFailOnTrue(!GUID.isValid(guid), "Artifact has an invalid guid [%s]", guid);
      return guid;
   }

   @Override
   public String getInternalFileName() {
      Named identity = attribute.getContainer();

      StringBuilder builder = new StringBuilder();
      try {
         String name = identity.getName();
         if (name.length() > MAX_NAME_SIZE) {
            name = name.substring(0, MAX_NAME_SIZE);
         }
         builder.append(URLEncoder.encode(name, "UTF-8"));
         builder.append(".");
      } catch (UnsupportedEncodingException ex) {
         // Do Nothing - this is not important
      }

      builder.append(getStorageName());

      String fileTypeExtension = null;
      if (identity instanceof ArtifactImpl) {
         fileTypeExtension = ((ArtifactImpl) identity).getSoleAttributeValue(CoreAttributeTypes.Extension, null);
      }
      if (!Strings.isValid(fileTypeExtension)) {
         fileTypeExtension = attribute.getAttributeType().getFileExtension();
      }
      if (Strings.isValid(fileTypeExtension)) {
         builder.append(".");
         builder.append(fileTypeExtension);
      }
      return builder.toString();
   }
}
