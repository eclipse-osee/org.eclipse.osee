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
package org.eclipse.osee.jaxrs.server.internal;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;
import org.eclipse.osgi.util.ManifestElement;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;

/**
 * Class responsible for managing static web resources contributed by bundles.
 *
 * <pre>
 * Contributions are specified in the bundle's MANIFEST.MF file using the following header and syntax:
 *
 * <b>Osee-JaxRs-Resource:</b> <i>path_to_resource<i><b>;path=</b><i>web_address</i><b>,</b> ...
 *
 * Example:
 *    <b>Osee-JaxRs-Resource:</b> <i>/web/js/*<i><b>;path=</b><i>/lib</i>
 *
 *    Make all files in <b>/web/js/</b> available through <b>/lib/</b>.
 *    Therefore, if we have a file - /web/js/script.js it will be available at /lib/script.js
 * </pre>
 *
 * @author Roberto E. Escobar
 */
public final class JaxRsResourceManager implements BundleListener {

   public interface JaxRsResourceVisitor {
      void onResource(Bundle bundle, Collection<String> resources);
   }

   public interface Resource {

      URL getUrl();

      boolean isSecure();
   }

   private static final String OSEE_WEB_RESOURCE_HDR = "Osee-JaxRs-Resource";
   private static final String OSEE_WEB_RESOURCE_HDR__PATH_ATTRIBUTE = "path";
   private static final String OSEE_WEB_RESOURCE_HDR__SECURE_ATTRIBUTE = "secure";

   private Map<String, Resource> pathToResource;
   private Map<Bundle, Set<String>> bundleToKey;

   private Log logger;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void start(BundleContext context) {
      pathToResource = new HashMap<>();
      bundleToKey = new HashMap<>();

      context.addBundleListener(this);

      // Add bundles that have already started
      Bundle[] bundles = context.getBundles();
      if (bundles != null) {
         for (Bundle bundle : bundles) {
            int state = bundle.getState();
            processBundle(bundle, state);
         }
      }
   }

   public void stop(BundleContext context) {
      context.removeBundleListener(this);

      if (pathToResource != null) {
         pathToResource.clear();
         pathToResource = null;
      }

      if (bundleToKey != null) {
         bundleToKey.clear();
         bundleToKey = null;
      }
   }

   public Resource getResource(String path) {
      String toMatch = JaxRsUtils.normalize(path);
      return pathToResource.get(toMatch);
   }

   public Resource findResource(ContainerRequestContext requestContext) {
      UriInfo uriInfo = requestContext.getUriInfo();
      String path = Lib.getURIAbsolutePath(uriInfo);
      Resource resource = getResource(path);
      if (resource == null) {
         if (!hasExtension(path)) {
            List<MediaType> mediaTypes = getMediaTypesToSearch(requestContext);
            for (MediaType mediaType : mediaTypes) {
               String resourcePath = addExtension(path, mediaType);
               if (Strings.isValid(resourcePath)) {
                  resource = getResource(resourcePath);
                  if (resource != null) {
                     break;
                  }
               }
            }
         }
      }
      return resource;
   }

   private boolean hasExtension(String path) {
      String extension = null;
      if (Strings.isValid(path)) {
         int index = path.lastIndexOf("/");
         String toProcess = path;
         if (index > 0 && index + 1 < path.length()) {
            toProcess = path.substring(index + 1);
         }
         extension = Lib.getExtension(toProcess);
      }
      return Strings.isValid(extension);
   }

   private String addExtension(String path, MediaType mediaType) {
      String extension = mediaType.getSubtype();
      if ("plain".equals(extension)) {
         extension = "txt";
      } else if (extension.contains("+")) {
         int index = extension.lastIndexOf("+");
         if (index > 0 && index + 1 < extension.length()) {
            extension = extension.substring(index + 1);
         }
      } else if (extension.contains(".")) {
         extension = Lib.getExtension(extension);
      }
      String toReturn = null;
      if (Strings.isValid(extension)) {
         StringBuilder builder = new StringBuilder(path);
         builder.append(".");
         builder.append(extension);
         toReturn = builder.toString();
      }
      return toReturn;
   }

   private List<MediaType> getMediaTypesToSearch(ContainerRequestContext requestContext) {
      List<MediaType> acceptableMediaTypes = new ArrayList<>();
      MediaType mediaType = requestContext.getMediaType();
      if (mediaType != null) {
         acceptableMediaTypes.add(mediaType);
      }
      acceptableMediaTypes.addAll(requestContext.getAcceptableMediaTypes());
      return acceptableMediaTypes;
   }

   public void accept(JaxRsResourceVisitor visitor) {
      for (Entry<Bundle, Set<String>> entry : bundleToKey.entrySet()) {
         visitor.onResource(entry.getKey(), Collections.unmodifiableCollection(entry.getValue()));
      }
   }

   @Override
   public void bundleChanged(BundleEvent event) {
      Bundle bundle = event.getBundle();
      processBundle(bundle, event.getType());
   }

   private String getWebResourceHeader(Bundle bundle) {
      return bundle.getHeaders().get(OSEE_WEB_RESOURCE_HDR);
   }

   private boolean hasWebResourceHeader(Bundle bundle) {
      String headerValue = getWebResourceHeader(bundle);
      return Strings.isValid(headerValue);
   }

   private void processBundle(Bundle bundle, int state) {
      boolean isActive = false;
      boolean isStopping = false;
      if (state == Bundle.ACTIVE //
         || state == Bundle.STARTING //
         || state == Bundle.INSTALLED //
         || state == Bundle.RESOLVED) {
         isActive = true;
      } else if (state == Bundle.STOPPING) {
         isStopping = true;
      }

      if (isActive && hasWebResourceHeader(bundle)) {
         addBundle(bundle);
      } else if (isStopping && hasWebResourceHeader(bundle)) {
         removeBundle(bundle);
      }
   }

   private void removeBundle(Bundle bundle) {
      Set<String> paths = bundleToKey.get(bundle);
      if (paths != null && !paths.isEmpty()) {
         for (String path : paths) {
            pathToResource.remove(path);
         }
      }
   }

   private boolean isValidEntry(Bundle bundle, String headerValue, String resource, String pathAttribute) {
      boolean result = false;
      if (!Strings.isValid(pathAttribute)) {
         logger.warn("Invalid path attribute [%s] for resource [%s] in bundle [%s] with header[%s]", pathAttribute,
            resource, bundle.getSymbolicName(), headerValue);
      } else if (!Strings.isValid(resource)) {
         logger.warn("Invalid resource [%s] for bundle[%s] with header[%s].", resource, bundle.getSymbolicName(),
            headerValue);
      } else {
         result = true;
      }
      return result;
   }

   private void addBundle(Bundle bundle) {
      String bundleName = bundle.getSymbolicName();
      String headerValue = getWebResourceHeader(bundle);

      ManifestElement[] elements = null;
      try {
         elements = ManifestElement.parseHeader(OSEE_WEB_RESOURCE_HDR, headerValue);
      } catch (BundleException ex) {
         logger.error(ex, "Error parsing manifest header [%s] for bundle [%s]", OSEE_WEB_RESOURCE_HDR, bundleName);
      }

      if (elements != null && elements.length > 0) {
         for (ManifestElement element : elements) {
            String resource = element.getValue();
            String aliasAttribute = element.getAttribute(OSEE_WEB_RESOURCE_HDR__PATH_ATTRIBUTE);
            String secureAttribute = element.getAttribute(OSEE_WEB_RESOURCE_HDR__SECURE_ATTRIBUTE);
            boolean secure = false;
            if (Strings.isValid(secureAttribute)) {
               secure = Boolean.parseBoolean(secureAttribute);
            }
            if (isValidEntry(bundle, headerValue, resource, aliasAttribute)) {
               List<URL> resourceUrls = findUrls(bundle, headerValue, true, resource);
               if (resourceUrls != null && !resourceUrls.isEmpty()) {
                  Set<String> paths = bundleToKey.get(bundle);
                  if (paths == null) {
                     paths = new HashSet<>();
                     bundleToKey.put(bundle, paths);
                  }
                  for (URL url : resourceUrls) {
                     addResource(paths, bundle, resource, aliasAttribute, url, secure);
                  }
               } else {
                  logger.warn("No resource urls found for resource path [%s] in bundle[%s] with header[%s].", resource,
                     bundleName, headerValue);
               }
            }
         }
      }
   }

   private List<URL> findUrls(Bundle bundle, String headerValue, boolean recurse, String... resources) {
      List<URL> resourceUrls = new ArrayList<>();
      for (String resource : resources) {
         int index = resource.lastIndexOf('/');
         String path = index != -1 ? resource.substring(0, index) : "/";
         String resourceName = index != -1 ? resource.substring(index + 1) : resource;
         Enumeration<URL> urls = bundle.findEntries(path, resourceName, recurse);
         if (urls != null && urls.hasMoreElements()) {
            while (urls.hasMoreElements()) {
               URL url = urls.nextElement();
               if (!url.toString().endsWith("/")) {
                  resourceUrls.add(url);
               }
            }
         } else {
            logger.error("Unable to find resource[%s] for bundle [%s]. The component header value is [%s]", resource,
               bundle.getSymbolicName(), headerValue);
         }
      }
      return resourceUrls;
   }

   private void addResource(Set<String> paths, Bundle bundle, String resourceBase, String alias, URL url, boolean secure) {
      int index = resourceBase.lastIndexOf('/');
      String path = index != -1 ? resourceBase.substring(0, index) : "/";
      String resourceName = index != -1 ? resourceBase.substring(index + 1) : resourceBase;

      String toStrip = path;
      if (!resourceName.contains("*")) {
         toStrip += "/" + resourceName;
      }

      String urlPath = url.getPath();
      index = urlPath.indexOf(toStrip);
      if (index >= 0) {
         urlPath = urlPath.substring(index + toStrip.length());
      }
      urlPath = JaxRsUtils.normalize(urlPath);

      String pathToMatch = String.format("%s%s", alias, urlPath);
      pathToMatch = pathToMatch.replaceAll("//", "/");
      paths.add(pathToMatch);

      Resource resource = new ResourceImpl(bundle.getSymbolicName(), url, secure);
      Resource oldResource = pathToResource.put(pathToMatch, resource);
      if (oldResource != null && !oldResource.getUrl().equals(url)) {
         logger.error("Resource collision detected for path[%s] between 1:[%s] and 2:[%s]. resource-2 will be used.",
            pathToMatch, oldResource, resource);
      }
   }

   private static final class ResourceImpl implements Resource {
      private final String bundleName;
      private final URL url;
      private final boolean secure;

      public ResourceImpl(String bundleName, URL url, boolean secure) {
         super();
         this.bundleName = bundleName;
         this.url = url;
         this.secure = secure;
      }

      @Override
      public URL getUrl() {
         return url;
      }

      @Override
      public boolean isSecure() {
         return secure;
      }

      @Override
      public String toString() {
         return "resource [bundleName=" + bundleName + ", url=" + url + "]";
      }

   }

}
