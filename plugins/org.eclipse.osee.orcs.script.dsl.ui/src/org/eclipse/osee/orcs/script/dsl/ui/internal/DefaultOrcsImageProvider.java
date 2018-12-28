/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.script.dsl.ui.internal;

import java.util.List;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.orcs.script.dsl.ui.IOrcsImageProvider;
import org.eclipse.swt.graphics.Image;

/**
 * @author Roberto E. Escobar
 */
public class DefaultOrcsImageProvider implements IOrcsImageProvider {

   private static final String EXTENSION_POINT_ID = "org.eclipse.osee.orcs.script.dsl.ui.OrcsImageProvider";
   private static final String EXTENSION_ELEMENT = "OrcsImageProvider";
   private static final String CLASSNAME = "classname";

   private final ExtensionDefinedObjects<IOrcsImageProvider> extensions =
      new ExtensionDefinedObjects<>(EXTENSION_POINT_ID, EXTENSION_ELEMENT, CLASSNAME, true);

   private List<IOrcsImageProvider> getProviders() {
      return extensions.getObjects();
   }

   @Override
   public Image getBranchImage() {
      Image image = null;
      for (IOrcsImageProvider provider : getProviders()) {
         image = provider.getBranchImage();
         if (image != null) {
            break;
         }
      }
      return image;
   }

   @Override
   public Image getTxImage() {
      Image image = null;
      for (IOrcsImageProvider provider : getProviders()) {
         image = provider.getTxImage();
         if (image != null) {
            break;
         }
      }
      return image;
   }

   @Override
   public Image getArtifactImage() {
      Image image = null;
      for (IOrcsImageProvider provider : getProviders()) {
         image = provider.getArtifactImage();
         if (image != null) {
            break;
         }
      }
      return image;
   }

   @Override
   public Image getAttributeImage() {
      Image image = null;
      for (IOrcsImageProvider provider : getProviders()) {
         image = provider.getAttributeImage();
         if (image != null) {
            break;
         }
      }
      return image;
   }

   @Override
   public Image getRelationImage() {
      Image image = null;
      for (IOrcsImageProvider provider : getProviders()) {
         image = provider.getRelationImage();
         if (image != null) {
            break;
         }
      }
      return image;
   }

   @Override
   public Image getArtifactTypeImage(Id type) {
      Image image = null;
      for (IOrcsImageProvider provider : getProviders()) {
         image = provider.getArtifactTypeImage(type);
         if (image != null) {
            break;
         }
      }
      return image;
   }

   @Override
   public Image getAttributeTypeImage(Id type) {
      Image image = null;
      for (IOrcsImageProvider provider : getProviders()) {
         image = provider.getAttributeTypeImage(type);
         if (image != null) {
            break;
         }
      }
      return image;
   }

   @Override
   public Image getRelationTypeImage(Id type) {
      Image image = null;
      for (IOrcsImageProvider provider : getProviders()) {
         image = provider.getRelationTypeImage(type);
         if (image != null) {
            break;
         }
      }
      return image;
   }

}