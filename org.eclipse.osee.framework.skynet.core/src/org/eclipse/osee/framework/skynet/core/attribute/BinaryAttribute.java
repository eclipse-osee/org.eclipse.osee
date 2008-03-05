/*
 * Created on Mar 3, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.attribute;

/**
 * @author Ryan D. Brooks
 */
public abstract class BinaryAttribute<T> extends Attribute<T> {

   /**
    * @param attributeType
    */
   protected BinaryAttribute(DynamicAttributeDescriptor attributeType) {
      super(attributeType);
   }
}
