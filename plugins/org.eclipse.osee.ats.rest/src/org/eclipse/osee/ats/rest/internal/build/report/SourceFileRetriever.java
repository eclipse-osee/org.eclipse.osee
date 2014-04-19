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
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.eclipse.osee.framework.jdk.core.type.ClassBasedResourceToken;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.ResourceToken;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.template.engine.OseeTemplateTokens;
import org.eclipse.osee.template.engine.PageFactory;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

/**
 * @author John Misinco
 */
public class SourceFileRetriever {
   private static final ResourceToken SOURCE = new ClassBasedResourceToken("sourceFileTemplate.html",
      SourceFileRetriever.class);
   private static final ResourceToken SOURCE_OFFLINE = new ClassBasedResourceToken("offlineSourceFileTemplate.html",
      SourceFileRetriever.class);

   public void getSourceFile(OutputStream output, IResourceRegistry registry, String urlToSource, boolean offline) {
      ResourceToken template = offline ? SOURCE_OFFLINE : SOURCE;
      Writer writer = null;
      try {
         writer = new OutputStreamWriter(output);
         Client client = Client.create();
         WebResource service = client.resource(urlToSource);

         PageFactory.realizePage(registry, template, writer, "fileContents", service.get(String.class));
      } finally {
         Lib.close(writer);
      }
   }

   public void getSupportFiles(OutputStream output) throws IOException {
      ZipOutputStream zout = null;
      try {
         zout = new ZipOutputStream(output);

         ResourceToken tokens[] = {OseeTemplateTokens.BuiltEditorCss, OseeTemplateTokens.BuiltEditorJs};

         for (ResourceToken token : tokens) {
            zout.putNextEntry(new ZipEntry(token.getName()));
            Lib.inputStreamToOutputStream(token.getInputStream(), zout);
            zout.closeEntry();
         }
      } finally {
         Lib.close(zout);
      }
   }
}