/**
 * 
 */
package org.eclipse.osee.ote.define;

import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.providers.MappedAttributeDataProvider;

/**
 * @author Ryan D. Brooks
 */
public class MappedAttributeDataProviderOld extends MappedAttributeDataProvider {
   public MappedAttributeDataProviderOld(Attribute<?> attribute) {
      super(attribute);
   }
}
