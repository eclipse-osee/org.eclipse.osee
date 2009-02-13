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
package org.eclipse.osee.framework.ui.data.model.editor.model;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactDataType extends DataType {

   private static final long serialVersionUID = 377718505364720109L;
   private String factoryKey;
   private String factoryName;
   private Image image;

   public ArtifactDataType() {
      this(EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, null);
   }

   public ArtifactDataType(String namespace, String name, String factoryKey, String factoryName, Image imageName) {
      this(EMPTY_STRING, namespace, name, factoryKey, factoryName, imageName);
   }

   public ArtifactDataType(String typeId, String namespace, String name, String factoryKey, String factoryName, Image image) {
      super(typeId, namespace, name);
      this.factoryKey = factoryKey;
      this.factoryName = factoryName;
      this.image = image;
   }

   /**
    * @return the imageName
    */
   public Image getImage() {
      return image != null ? image : ImageDescriptor.getMissingImageDescriptor().createImage();
   }

   /**
    * @param imageName the imageName to set
    */
   public void setImageName(Image image) {
      this.image = image;
   }

   /**
    * @return the factoryKey
    */
   public String getFactoryKey() {
      return factoryKey;
   }

   /**
    * @param factoryKey the factoryKey to set
    */
   public void setFactoryKey(String factoryKey) {
      this.factoryKey = factoryKey;
   }

   /**
    * @return the factoryName
    */
   public String getFactoryName() {
      return factoryName;
   }

   /**
    * @param factoryName the factoryName to set
    */
   public void setFactoryName(String factoryName) {
      this.factoryName = factoryName;
   }

}
