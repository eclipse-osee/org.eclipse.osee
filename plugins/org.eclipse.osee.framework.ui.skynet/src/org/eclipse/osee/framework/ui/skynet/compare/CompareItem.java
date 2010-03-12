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
package org.eclipse.osee.framework.ui.skynet.compare;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import org.eclipse.compare.IEditableContent;
import org.eclipse.compare.IModificationDate;
import org.eclipse.compare.IStreamContentAccessor;
import org.eclipse.compare.ITypedElement;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Image;

/**
 * @author Jeff C. Phillips
 */
public class CompareItem implements IStreamContentAccessor, ITypedElement, IModificationDate, IEditableContent {
   private String contents;
   private final String name;
   private final boolean isEditable;
   private final long time;
   private final Image image;

   public CompareItem(String name, String contents, long time) {
      this(name, contents, time, false, null);
   }

   CompareItem(String name, String contents, long time, boolean isEditable, Image image) {
      this.name = name;
      this.contents = contents;
      this.time = time;
      this.isEditable = isEditable;
      this.image = image;
   }

   public InputStream getContents() throws CoreException {
      try {
         return new ByteArrayInputStream(contents.getBytes("UTF-8"));
      } catch (UnsupportedEncodingException ex) {
         throw new RuntimeException(ex);
      }
   }

   public Image getImage() {
      return image;
   }

   public boolean isEditable() {
      return isEditable;
   }

   public long getModificationDate() {
      return time;
   }

   public String getName() {
      return name;
   }

   public String getStringContent() {
      return contents;
   }

   public String getType() {
      return ITypedElement.TEXT_TYPE;
   }

   @Override
   public ITypedElement replace(ITypedElement dest, ITypedElement src) {
      return null;
   }

   @Override
   public void setContent(byte[] newContent) {
      try {
         this.contents = new String(newContent, "UTF-8");
      } catch (UnsupportedEncodingException ex) {
         throw new RuntimeException(ex);
      }
   }

   /**
    * This method must be overridden by a subclass if they want to be notified when the compare editor has been saved
    */
   public void persistContent() {
   }
}
