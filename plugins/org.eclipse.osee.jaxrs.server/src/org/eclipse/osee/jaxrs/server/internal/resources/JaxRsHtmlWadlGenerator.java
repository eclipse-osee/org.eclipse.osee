/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.jaxrs.server.internal.resources;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.cxf.jaxrs.model.ClassResourceInfo;
import org.apache.cxf.jaxrs.model.wadl.WadlGenerator;
import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.apache.cxf.message.Message;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jaxrs.server.internal.JaxRsUtils;
import org.eclipse.osee.logger.Log;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * @author Roberto E. Escobar
 */
@Provider
public class JaxRsHtmlWadlGenerator extends WadlGenerator {

   private static final String WADL_TRANSFORMED_FLAG = "was.wadl.transformed";

   private static final String DEFAULT_TRANSFORM = "OSEE-INF/transforms/wadl.xsl";

   private Log logger;
   private BundleContext bundleContext;
   private String wadlTemplatePath;

   //@formatter:off
   private @Context HttpHeaders headers;
   //@formatter:on

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void start(BundleContext bundleContext, Map<String, Object> props) {
      this.bundleContext = bundleContext;
      update(props);
   }

   public void stop() {
      this.bundleContext = null;
      this.wadlTemplatePath = null;
   }

   public void update(Map<String, Object> props) {
      this.wadlTemplatePath = DEFAULT_TRANSFORM;
   }

   @Override
   protected void doFilter(ContainerRequestContext context, Message m) {
      m.getExchange().put(WADL_TRANSFORMED_FLAG, Boolean.FALSE);
      super.doFilter(context, m);
      Boolean wasTransformed = (Boolean) m.getExchange().get(WADL_TRANSFORMED_FLAG);
      if (wasTransformed) {
         Response response = m.getExchange().get(javax.ws.rs.core.Response.class);
         if (response != null) {
            MediaType type = MediaType.TEXT_HTML_TYPE;
            response = Response.fromResponse(response).type(type).build();
            m.getExchange().put(javax.ws.rs.core.Response.class, JAXRSUtils.copyResponseIfNeeded(response));
         }
      }
   }

   @Override
   public StringBuilder generateWADL(String baseURI, List<ClassResourceInfo> cris, boolean isJson, Message m, UriInfo ui) {
      StringBuilder wadl = super.generateWADL(baseURI, cris, isJson, m, ui);

      StringBuilder toReturn = wadl;
      List<MediaType> acceptableMediaTypes = headers.getAcceptableMediaTypes();
      if (JaxRsUtils.isHtmlSupported(acceptableMediaTypes)) {
         URL templateUrl = getTemplateURL(wadlTemplatePath);
         if (templateUrl != null) {
            InputStream wadlStream = null;
            InputStream templateStream = null;
            try {
               templateStream = templateUrl.openStream();

               StringWriter writer = new StringWriter();
               StreamSource wadlSource = new StreamSource(new StringReader(wadl.toString()));
               StreamSource templateSource = new StreamSource(templateStream);
               StreamResult output = new StreamResult(writer);
               transformWadl(wadlSource, templateSource, output);

               toReturn = new StringBuilder(writer.toString());
               m.getExchange().put(WADL_TRANSFORMED_FLAG, Boolean.TRUE);
            } catch (Exception ex) {
               logger.warn(ex, "Error applying wadl transform");
            } finally {
               Lib.close(wadlStream);
               Lib.close(templateStream);
            }
         } else {
            logger.warn("WADL to HTML template url was null - templatePath[%s]", wadlTemplatePath);
         }
      }
      return toReturn;
   }

   private URL getTemplateURL(String template) {
      URL toReturn = null;
      if (Strings.isValid(template)) {
         if (template.contains("://")) {
            try {
               URI uri = URI.create(template);
               toReturn = uri.toURL();
            } catch (Exception ex) {
               // do nothing
            }
         } else if (bundleContext != null) {
            Bundle bundle = bundleContext.getBundle();
            toReturn = bundle.getResource(template);
         }
      }
      return toReturn;
   }

   private void transformWadl(StreamSource wadlSource, StreamSource templateSource, StreamResult output) throws Exception {
      TransformerFactory factory = TransformerFactory.newInstance();
      Templates template = factory.newTemplates(templateSource);
      Transformer xformer = template.newTransformer();
      xformer.transform(wadlSource, output);
   }

}