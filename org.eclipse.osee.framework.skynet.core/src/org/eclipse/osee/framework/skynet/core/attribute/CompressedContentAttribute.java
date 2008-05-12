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
package org.eclipse.osee.framework.skynet.core.attribute;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.providers.IAttributeDataProvider;

public final class CompressedContentAttribute extends BinaryAttribute<InputStream> implements IStreamSetableAttribute {

   public CompressedContentAttribute(AttributeType attributeType, Artifact artifact) {
      super(attributeType, artifact);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#getDisplayableString()
    */
   @Override
   public String getDisplayableString() {
      return getAttributeDataProvider().getDisplayableString();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#getValue()
    */
   @Override
   public InputStream getValue() {
      return new ByteArrayInputStream(getAttributeDataProvider().getValueAsBytes());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#setValue(java.lang.Object)
    */
   @Override
   public void setValue(InputStream value) {
      try {
         setValueFromInputStream(value);
      } catch (IOException ex) {
         SkynetActivator.getLogger().log(Level.SEVERE, ex.toString(), ex);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.IStreamableAttribute#setValueFromInputStream(java.io.InputStream)
    */
   @Override
   public void setValueFromInputStream(InputStream value) throws IOException {
      byte[] data = Lib.inputStreamToBytes(value);
      getAttributeDataProvider().setValue(data);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#setAttributeDataProvider(org.eclipse.osee.framework.skynet.core.attribute.providers.IAttributeDataProvider)
    */
   @Override
   public void setAttributeDataProvider(IAttributeDataProvider attributeDataProvider) {
      super.setAttributeDataProvider(attributeDataProvider);
      attributeDataProvider.setDisplayableString(getAttributeType().getName());
   }
}