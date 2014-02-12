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
package org.eclipse.osee.ats.rest.internal.build.report;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.eclipse.osee.ats.rest.internal.AtsRestTemplateTokens;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.ResourceToken;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.template.engine.OseeTemplateTokens;
import org.eclipse.osee.template.engine.PageCreator;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

/**
 * @author John Misinco
 */
public class SourceFileRetriever {

   public void getSourceFile(OutputStream output, OrcsApi orcsApi, String urlToSource, boolean offline) {
      ResourceToken pageToken =
         offline ? AtsRestTemplateTokens.OfflineSourceFileTemplateHtml : AtsRestTemplateTokens.SourceFileTemplateHtml;
      Writer writer = null;
      try {
         writer = new OutputStreamWriter(output);
         Client client = Client.create();
         URI.create(urlToSource);
         WebResource service = client.resource(urlToSource);

         PageCreator page = new PageCreator(orcsApi.getResourceRegistry());

         page.addKeyValuePair("fileContents", service.get(String.class));
         page.realizePage(pageToken, writer);
      } finally {
         Lib.close(writer);
      }
   }

   public void getSupportFiles(OutputStream output, OrcsApi orcsApi) throws IOException {
      ZipOutputStream zout = null;
      try {
         zout = new ZipOutputStream(output);

         IResourceRegistry registry = orcsApi.getResourceRegistry();
         ResourceToken tokens[] = {OseeTemplateTokens.BuiltEditorCss, OseeTemplateTokens.BuiltEditorJs};

         for (ResourceToken token : tokens) {
            zout.putNextEntry(new ZipEntry(token.getName()));
            Lib.inputStreamToOutputStream(registry.getResource(token.getGuid()), zout);
            zout.closeEntry();
         }
      } finally {
         Lib.close(zout);
      }
   }
}
