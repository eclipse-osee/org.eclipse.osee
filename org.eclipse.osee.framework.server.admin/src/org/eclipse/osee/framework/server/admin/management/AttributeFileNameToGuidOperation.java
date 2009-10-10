/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.server.admin.management;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.ConnectionHandlerStatement;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.IResourceLocatorManager;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.framework.resource.management.Options;
import org.eclipse.osee.framework.resource.management.StandardOptions;
import org.eclipse.osee.framework.resource.management.exception.MalformedLocatorException;
import org.eclipse.osee.framework.server.admin.Activator;
import org.eclipse.osee.framework.server.admin.BaseServerCommand;

/**
 * @author Ryan Schmitt
 */
public final class AttributeFileNameToGuidOperation extends BaseServerCommand {

   public AttributeFileNameToGuidOperation() {
      super("Change Attribute URI to Guid");
   }

   @Override
   protected void doCommandWork(IProgressMonitor monitor) throws Exception {
      List<AttrData> data = getAttributeData();
      IResourceManager manager = Activator.getInstance().getResourceManager();
      Options options = new Options();
      options.put(StandardOptions.CompressOnSave.name(), true);
      options.put(StandardOptions.DecompressOnAquire.name(), true);
      String REPLACEMENT_QUERY = "update osee_attribute set uri = ? where gamma_id = ?";
      List<Object[]> renameData = new ArrayList<Object[]>();
      int count = 0;
      for (AttrData attr : data) {
         IResource attrResource = manager.acquire(attr.getSourceLocator(), options);
         if (attrResource != null) {
            boolean do_not_erase = false;
            IResource modifiedResource = new ResourceHelper(attrResource, attr.getHrid(), attr.getGuid());
            try {
               manager.save(attr.getDestLocator(), modifiedResource, options);
            } catch (IOException e) // overwriting
            {
               do_not_erase = true;
            }
            if (!do_not_erase) {
               manager.delete(attr.getDeleteLocator());
               Object[] ins = {attr.getDestLocator().getLocation().toASCIIString() + ".zip", attr.getGammaId()};
               println(String.format("(%s, %s) from %s", ins[0], ins[1], attr.getUri()));
               renameData.add(ins);
               count++;
            }
         } else {
            println(String.format("HRID %s not found; skipping", attr.getHrid()));
         }
      }

      ConnectionHandler.runBatchUpdate(REPLACEMENT_QUERY, renameData);
      println(String.format("Transferred %s items", count));
   }

   public List<AttrData> getAttributeData() throws OseeDataStoreException, MalformedLocatorException {
      List<AttrData> data = new ArrayList<AttrData>();
      String GET_PERSISTED_ARTIFACT_QUERY =
            "SELECT oart.guid,  oart.art_id,  oattr.gamma_id,  oattr.uri, oart.human_readable_id FROM osee_artifact oart,  osee_attribute oattr WHERE oart.art_id = oattr.art_id AND oattr.uri IS NOT NULL";
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(GET_PERSISTED_ARTIFACT_QUERY);
         while (chStmt.next()) {
            String guid = chStmt.getString("GUID");
            int gammaId = chStmt.getInt("GAMMA_ID");
            String uri = chStmt.getString("URI");
            String hrid = chStmt.getString("HUMAN_READABLE_ID");
            if (Strings.isValid(uri)) {
               data.add(new AttrData(guid, gammaId, uri, hrid));
            }
         }
      } finally {
         chStmt.close();
      }
      return data;
   }

   private final class ResourceHelper implements IResource {
      private final IResource source;
      private URI newLocation;

      public ResourceHelper(IResource source, String hrid, String guid) {
         this.source = source;
         try {
            String temp = source.getLocation().getSchemeSpecificPart();
            temp = temp.replaceAll(hrid, guid);
            this.newLocation = new URI("file", temp, null);
         } catch (URISyntaxException ex) {
         }
      }

      @Override
      public InputStream getContent() throws IOException {
         return source.getContent();
      }

      @Override
      public URI getLocation() {
         return newLocation;
      }

      @Override
      public String getName() {
         String value = newLocation.toASCIIString();
         return value.substring(value.lastIndexOf("/") + 1, value.length());
      }

      @Override
      public boolean isCompressed() {
         return source.isCompressed();
      }

   }
   private final class AttrData {
      private final String guid;
      private final int gammaId;
      private final String uri;
      private final String hrid;
      IResourceLocatorManager locatorManager;
      IResourceLocator sourceLocator;
      IResourceLocator deleteLocator;
      IResourceLocator destLocator;
      IResource resource;

      public AttrData(String _guid, int _gammaId, String _uri, String _hrid) throws MalformedLocatorException {
         guid = _guid;
         gammaId = _gammaId;
         uri = _uri;
         hrid = _hrid;
         locatorManager = Activator.getInstance().getResourceLocatorManager();
         sourceLocator = locatorManager.getResourceLocator(uri.substring(0, uri.length() - 4));
         deleteLocator = locatorManager.getResourceLocator(uri);
         destLocator = locatorManager.generateResourceLocator("attr://", String.valueOf(getGammaId()), guid);
      }

      public String getGuid() {
         return guid;
      }

      public int getGammaId() {
         return gammaId;
      }

      public String getUri() {
         return uri;
      }

      public String getHrid() {
         return hrid;
      }

      public IResourceLocator getSourceLocator() {
         return sourceLocator;
      }

      public IResourceLocator getDestLocator() {
         return destLocator;
      }

      public IResourceLocator getDeleteLocator() {
         return deleteLocator;
      }
   }
}