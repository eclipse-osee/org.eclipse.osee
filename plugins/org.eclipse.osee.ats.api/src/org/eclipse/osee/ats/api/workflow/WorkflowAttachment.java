/*********************************************************************
 * Copyright (c) 2025 Boeing
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
package org.eclipse.osee.ats.api.workflow;

public class WorkflowAttachment {
   private String id;
   private String name;
   private String extension;
   private int sizeInBytes;
   private byte[] attachmentBytes;

   // Getters and setters
   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getExtension() {
      return extension;
   }

   public void setExtension(String extension) {
      this.extension = extension;
   }

   public int getSizeInBytes() {
      return sizeInBytes;
   }

   public void setSizeInBytes(int sizeInBytes) {
      this.sizeInBytes = sizeInBytes;
   }

   public byte[] getAttachmentBytes() {
      return attachmentBytes;
   }

   public void setAttachmentBytes(byte[] attachmentBytes) {
      this.attachmentBytes = attachmentBytes;
   }
}
