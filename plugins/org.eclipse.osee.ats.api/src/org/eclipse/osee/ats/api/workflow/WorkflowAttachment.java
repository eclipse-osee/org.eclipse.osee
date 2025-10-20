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

import java.io.IOException;
import java.io.InputStream;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.IAttribute;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.util.Lib;

public class WorkflowAttachment {
   private String id;
   private String name;
   private String nameAtId;
   private String nameGamma;
   private String extension;
   private String extensionAtId;
   private String extensionGamma;
   private int sizeInBytes;
   private byte[] attachmentBytes;
   private String nativeContentAtId;
   private String nativeContentGamma;

   public WorkflowAttachment() {

   }

   public WorkflowAttachment(ArtifactReadable attachmentArtifact) {

      InputStream inputStream = attachmentArtifact.getSoleAttributeValue(CoreAttributeTypes.NativeContent);
      try {
         IAttribute<Object> nameAttribute = attachmentArtifact.getSoleAttribute(CoreAttributeTypes.Name);
         IAttribute<Object> extensionAttribute = attachmentArtifact.getSoleAttribute(CoreAttributeTypes.Extension);
         IAttribute<Object> nativeContentAttribute =
            attachmentArtifact.getSoleAttribute(CoreAttributeTypes.NativeContent);

         this.id = attachmentArtifact.getId().toString();
         this.name = attachmentArtifact.getName();
         this.nameAtId = nameAttribute.getIdString();
         this.nameGamma = nameAttribute.getGammaId().toString();

         this.extension = extensionAttribute.getValue().toString();
         this.extensionAtId = extensionAttribute.getIdString();
         this.extensionGamma = extensionAttribute.getGammaId().toString();

         this.attachmentBytes = Lib.inputStreamToBytes(inputStream);
         this.sizeInBytes = this.attachmentBytes.length;
         this.nativeContentAtId = nativeContentAttribute.getIdString();
         this.nativeContentGamma = nativeContentAttribute.getGammaId().toString();
      } catch (IOException ex) {
         throw new RuntimeException(ex);
      }
   }

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

   public String getNameAtId() {
      return nameAtId;
   }

   public void setNameAtId(String nameAtId) {
      this.nameAtId = nameAtId;
   }

   public String getNameGamma() {
      return nameGamma;
   }

   public void setNameGamma(String nameGamma) {
      this.nameGamma = nameGamma;
   }

   public String getExtension() {
      return extension;
   }

   public void setExtension(String extension) {
      this.extension = extension;
   }

   public String getExtensionAtId() {
      return extensionAtId;
   }

   public void setExtensionAtId(String extensionAtId) {
      this.extensionAtId = extensionAtId;
   }

   public String getExtensionGamma() {
      return extensionGamma;
   }

   public void setExtensionGamma(String extensionGamma) {
      this.extensionGamma = extensionGamma;
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

   public String getNativeContentAtId() {
      return nativeContentAtId;
   }

   public void setNativeContentAtId(String nativeContentAtId) {
      this.nativeContentAtId = nativeContentAtId;
   }

   public String getNativeContentGamma() {
      return nativeContentGamma;
   }

   public void setNativeContentGamma(String nativeContentGamma) {
      this.nativeContentGamma = nativeContentGamma;
   }

}
