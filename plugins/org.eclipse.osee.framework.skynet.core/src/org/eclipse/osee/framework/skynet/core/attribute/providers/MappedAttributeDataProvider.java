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
package org.eclipse.osee.framework.skynet.core.attribute.providers;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.eclipse.core.resources.IFile;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.utils.BinaryContentUtils;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.jaxrs.client.JaxRsExceptions;
import org.eclipse.osee.orcs.rest.client.OseeClient;
import org.eclipse.osee.orcs.rest.model.ResourcesEndpoint;

/**
 * @author Roberto E. Escobar
 */
public class MappedAttributeDataProvider extends AbstractAttributeDataProvider implements ICharacterAttributeDataProvider {

   private String localUri;
   private String remoteUri;
   private IFile backingFile;

   public MappedAttributeDataProvider(Attribute<?> attribute) {
      super(attribute);
      this.remoteUri = null;
      this.localUri = null;
      this.backingFile = null;
   }

   @Override
   public Object[] getData() {
      return new Object[] {"", remoteUri};
   }

   @Override
   public String getDisplayableString() {
      return isFromLocalWorkspace() ? this.localUri : "Remote Content";
   }

   @Override
   public void loadData(Object... objects) {
      if (objects != null && objects.length > 1) {
         remoteUri = (String) objects[1];
      }
   }

   private ResourcesEndpoint getResourcesEndpoint() {
      OseeClient client = ServiceUtil.getOseeClient();
      return client.getResourcesEndpoint();
   }

   private String getOutfileName() {
      StringBuilder builder = new StringBuilder();
      //TestRunOperator operator = new TestRunOperator(getAttribute().getArtifact());
      builder.append(getAttribute().getArtifact().getName());
      builder.append(".");
      //builder.append(operator.getChecksum());
      //builder.append(".");
      String extension = null; //operator.getOutfileExtension();
      if (!Strings.isValid(extension)) {
         extension = getAttribute().getAttributeType().getFileTypeExtension();
      }
      builder.append(extension);
      return builder.toString();
   }

   @Override
   public void persist(GammaId storageId) {
      try {
         if (isFromLocalWorkspace()) {
            InputStream inputStream = null;
            try {
               ResourcesEndpoint endpoint = getResourcesEndpoint();

               URI sourceUri = new URI(localUri);
               inputStream = sourceUri.toURL().openStream();
               byte[] compressed = Lib.compressStream(inputStream, getOutfileName());

               String resourceId = storageId.getIdString();
               boolean overwriteAllowed = false;
               boolean compressOnSave = false;

               String resourceName = String.format("%s.zip", getAttribute().getArtifact().getGuid());
               try {
                  Response response = endpoint.saveResource(new ByteArrayInputStream(compressed),
                     BinaryContentUtils.ATTRIBUTE_RESOURCE_PROTOCOL, resourceId, resourceName, overwriteAllowed,
                     compressOnSave);
                  String location = BinaryContentUtils.getAttributeLocation(response);
                  if (location != null) {
                     this.remoteUri = location;
                     this.localUri = null;
                  }
               } catch (Exception ex) {
                  throw JaxRsExceptions.asOseeException(ex);
               }
            } finally {
               Lib.close(inputStream);
            }
         }
      } catch (OseeCoreException ex) {
         throw ex; // keep exceptions of type OseeCoreException from being unnecessarily wrapped
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
   }

   @Override
   public void purge() {
      try {
         if (isRemoteUriValid()) {
            String path = BinaryContentUtils.asResourcePath(remoteUri);

            ResourcesEndpoint endpoint = getResourcesEndpoint();
            try {
               Response response = endpoint.deleteResource(path);
               if (Status.OK.getStatusCode() == response.getStatus()) {
                  remoteUri = null;
                  if (isBackingFileValid()) {
                     backingFile.delete(true, null);
                  }
               }
            } catch (Exception ex) {
               throw JaxRsExceptions.asOseeException(ex);
            }
         }
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
   }

   @Override
   public void setDisplayableString(String toDisplay) {
      // Do Nothing
   }

   @Override
   public String getValueAsString() {
      if (isRemoteUriValid() && isBackingFileValid() != true) {
         try {
            this.backingFile = requestRemoteFile();
            this.localUri = null;
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      String toReturn = this.localUri;
      if (isBackingFileValid()) {
         toReturn = backingFile.getLocationURI().toASCIIString();
      }
      return toReturn;
   }

   private boolean isBackingFileValid() {
      return backingFile != null && backingFile.isAccessible();
   }

   @Override
   public boolean setValue(Object value) {
      // DO NOTHING
      return false;
   }

   private IFile requestRemoteFile() {
      IFile file = null;

      String path = BinaryContentUtils.asResourcePath(remoteUri);

      ResourcesEndpoint endpoint = getResourcesEndpoint();
      InputStream inputStream = null;
      try {
         Response resource = endpoint.getResource(path, false, false);
         InputStream entity = resource.readEntity(InputStream.class);

         inputStream = new BufferedInputStream(entity);
         ZipInputStream zipInputStream = new ZipInputStream(inputStream);
         ZipEntry entry = zipInputStream.getNextEntry();
         file = OseeData.getIFile(entry.getName(), zipInputStream, true);
      } catch (Exception ex) {
         throw JaxRsExceptions.asOseeException(ex);
      } finally {
         Lib.close(inputStream);
      }
      return file;
   }

   public void setLocalUri(String localUri) {
      this.localUri = localUri;
   }

   private boolean isRemoteUriValid() {
      return remoteUri != null && remoteUri.length() > 0;
   }

   private boolean isFromLocalWorkspace() {
      boolean toReturn = isRemoteUriValid() != true;
      try {
         //toReturn |= new TestRunOperator(getAttribute().getArtifact()).isFromLocalWorkspace();
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return toReturn;
   }

   @Override
   public Object getValue() {
      return getValueAsString();
   }
}
