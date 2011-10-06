/*
 * Created on Oct 4, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs.core.internal.attribute;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.eclipse.osee.framework.core.data.NamedIdentity;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.core.ds.ResourceNameResolver;

public class AttributeResourceNameResolver implements ResourceNameResolver {
   private final static int MAX_NAME_SIZE = 60;

   private final Attribute<?> attribute;

   public AttributeResourceNameResolver(Attribute<?> attribute) {
      this.attribute = attribute;
   }

   @Override
   public String getStorageName() throws OseeCoreException {
      NamedIdentity<String> identity = attribute.getContainer().getParent();
      String guid = identity.getGuid();
      Conditions.checkExpressionFailOnTrue(!GUID.isValid(guid), "Artifact has an invalid guid [%s]", guid);
      return guid;
   }

   @Override
   public String getInternalFileName() throws OseeCoreException {
      NamedIdentity<String> identity = attribute.getContainer().getParent();

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

      String fileTypeExtension = getExtension(attribute);
      if (Strings.isValid(fileTypeExtension)) {
         builder.append(".");
         builder.append(fileTypeExtension);
      }
      return builder.toString();
   }

   private String getExtension(Attribute<?> attribute) throws OseeCoreException {
      AttributeType attributeType = attribute.getAttributeType();
      String fileTypeExtension = null;
      if (attribute.isOfType(CoreAttributeTypes.NativeContent)) {
         fileTypeExtension = (String) attribute.getValue();
      }
      if (!Strings.isValid(fileTypeExtension)) {
         fileTypeExtension = attributeType.getFileTypeExtension();
      }
      return fileTypeExtension;
   }
}
