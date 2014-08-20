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
package org.eclipse.osee.template.engine.internal;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.ResourceToken;
import org.eclipse.osee.framework.jdk.core.util.ChecksumUtil;
import org.eclipse.osee.framework.jdk.core.util.HexUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.template.engine.OseeTemplateTokens;
import org.eclipse.osgi.util.ManifestElement;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;

/**
 * @author Roberto E. Escobar
 */
public class TemplateRegistryImpl implements TemplateRegistry, IResourceRegistry, BundleListener {

   private static final String OSEE_TEMPLATE_HDR = "Osee-Template";
   private static final String OSEE_TEMPLATE_HDR__UUID_ATTRIBUTE = "uuid";

   private ConcurrentHashMap<String, TemplateResources> templates;
   private ConcurrentHashMap<Long, ResourceToken> tokenByUuid;
   private ConcurrentHashMap<String, ResourceToken> tokenByName;

   private Log logger;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void start(BundleContext context) {
      templates = new ConcurrentHashMap<String, TemplateResources>();
      tokenByName = new ConcurrentHashMap<String, ResourceToken>();
      tokenByUuid = new ConcurrentHashMap<Long, ResourceToken>();

      OseeTemplateTokens.register(this);

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

      templates.clear();
      tokenByName.clear();
      tokenByUuid.clear();
   }

   private void cache(ResourceToken token) {
      tokenByUuid.put(token.getGuid(), token);
      ResourceToken oldToken = tokenByName.put(token.getName(), token);
      if (oldToken != null) {
         logger.error("Template conflict detected between - [%s] and [%s]", oldToken, token);
      }
   }

   private void decache(ResourceToken token) {
      tokenByUuid.remove(token.getGuid());
      tokenByName.remove(token.getName());
   }

   private String getTemplateHeader(Bundle bundle) {
      return bundle.getHeaders().get(OSEE_TEMPLATE_HDR);
   }

   private boolean hasTemplateHeader(Bundle bundle) {
      String headerValue = getTemplateHeader(bundle);
      return Strings.isValid(headerValue);
   }

   @Override
   public ResourceToken resolveTemplate(String viewId, MediaType mediaType) {
      ResourceToken resourceToken = null;
      if (Strings.isNumeric(viewId)) {
         Long uuid = Long.parseLong(viewId);
         resourceToken = getResourceToken(uuid);
      } else {
         String name = viewId;
         int index = name.lastIndexOf("/");
         if (index > -1) {
            name = name.substring(index + 1);
         }
         resourceToken = tokenByName.get(name);
      }
      return resourceToken;
   }

   @Override
   public IResourceRegistry getResourceRegistry() {
      return this;
   }

   @Override
   public ResourceToken registerResource(Long universalId, ResourceToken token) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void registerAll(Iterable<ResourceToken> tokens) {
      for (ResourceToken token : tokens) {
         cache(token);
      }
   }

   @Override
   public ResourceToken getResourceToken(Long uuid) {
      return tokenByUuid.get(uuid);
   }

   @Override
   public InputStream getResource(Long uuid) {
      ResourceToken token = getResourceToken(uuid);
      InputStream toReturn = null;
      if (token != null) {
         toReturn = token.getInputStream();
      } else {
         logger.error("Unable to find template-resource [%s]", uuid);
      }
      return toReturn;
   }

   @Override
   public void bundleChanged(BundleEvent event) {
      Bundle bundle = event.getBundle();
      processBundle(bundle, event.getType());
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

      if (isActive && hasTemplateHeader(bundle)) {
         addBundle(bundle);
      } else if (isStopping && hasTemplateHeader(bundle)) {
         removeBundle(bundle);
      }
   }

   private void removeBundle(Bundle bundle) {
      String bundleName = bundle.getSymbolicName();
      TemplateResources removed = templates.remove(bundleName);
      if (removed != null) {
         Iterable<ResourceToken> tokens = removed.getTokens();
         for (ResourceToken token : tokens) {
            decache(token);
         }
      }
   }

   private void addBundle(Bundle bundle) {
      String bundleName = bundle.getSymbolicName();
      String headerValue = getTemplateHeader(bundle);

      ManifestElement[] elements = null;
      try {
         elements = ManifestElement.parseHeader(OSEE_TEMPLATE_HDR, headerValue);
      } catch (BundleException ex) {
         logger.error(ex, "Error parsing manifest header [%s] for bundle [%s]", OSEE_TEMPLATE_HDR, bundleName);
      }

      if (elements != null && elements.length > 0) {
         TemplateResources tokens = new TemplateResources();
         for (ManifestElement element : elements) {
            String resource = element.getValue();
            String uuidAttribute = element.getAttribute(OSEE_TEMPLATE_HDR__UUID_ATTRIBUTE);
            List<URL> resourceUrls = findUrls(bundle, headerValue, false, resource);

            if (resourceUrls != null && !resourceUrls.isEmpty()) {
               if (Strings.isValid(uuidAttribute)) {
                  if (isValidUuid(uuidAttribute)) {
                     URL url = resourceUrls.iterator().next();
                     Long uuid = HexUtil.toLong(uuidAttribute);
                     addEntry(bundleName, headerValue, tokens, url, uuid);
                  } else {
                     logger.error("Invalid uuidAttribute [%s] for manifest element [%s] in bundle [%s]", uuidAttribute,
                        element, bundleName);
                  }
               } else {
                  for (URL url : resourceUrls) {
                     addEntry(bundleName, headerValue, tokens, url, null);
                  }
               }
            }
         }
         if (!tokens.isEmpty()) {
            templates.put(bundleName, tokens);
         }
      }
   }

   private boolean isValidUuid(String value) {
      return HexUtil.isHexString(value);
   }

   private void addEntry(String bundleName, String headerValue, TemplateResources tokens, URL url, Long uuidSpecified) {
      try {
         String name = urlAsName(url);
         Long uuid = uuidSpecified != null ? uuidSpecified : nameToUuid(name);
         ResourceToken token = newToken(bundleName, uuid, name, url);
         boolean wasAdded = tokens.addToken(token);
         if (!wasAdded) {
            logger.error("Invalid template uuid conflicts with previous definition - bundle [%s] header [%s]",
               bundleName, headerValue);
         }
         cache(token);
      } catch (Exception ex) {
         logger.error(ex, "Invalid uuidAttribute for bundle [%s] with header [%s]", bundleName, headerValue);
      }
   }

   private Long nameToUuid(String name) throws Exception {
      ByteArrayInputStream inputStream = new ByteArrayInputStream(name.getBytes("UTF-8"));
      byte[] checksum = ChecksumUtil.createChecksum(inputStream, ChecksumUtil.SHA);
      long value = 0;
      for (int i = 0; i < 8; i++) {
         value += (checksum[i] & 0xffL) << (8 * i);
      }
      return value;
   }

   private String urlAsName(URL url) {
      String name = url.toString();
      int index = name.lastIndexOf('/');
      if (index > -1) {
         name = name.substring(index + 1);
      }
      return name;
   }

   private List<URL> findUrls(Bundle bundle, String headerValue, boolean recurse, String... resources) {
      List<URL> resourceUrls = new ArrayList<URL>();
      for (String resource : resources) {
         int index = resource.lastIndexOf('/');
         String path = index != -1 ? resource.substring(0, index) : "/";
         String resourceName = index != -1 ? resource.substring(index + 1) : resource;
         Enumeration<URL> urls = bundle.findEntries(path, resourceName, recurse);
         if (urls != null && urls.hasMoreElements()) {
            while (urls.hasMoreElements()) {
               URL url = urls.nextElement();
               resourceUrls.add(url);
            }
         } else {
            logger.error("Unable to find template-resource[%s] for bundle [%s]. The component header value is [%s]",
               resource, bundle.getSymbolicName(), headerValue);
         }
      }
      return resourceUrls;
   }

   private static ResourceToken newToken(String bundleName, Long uuid, String name, URL url) {
      return new TemplateToken(bundleName, uuid, name, url);
   }

   private static class TemplateResources {
      private final Set<ResourceToken> tokens = new HashSet<ResourceToken>();

      public boolean addToken(ResourceToken token) {
         return tokens.add(token);
      }

      public Iterable<ResourceToken> getTokens() {
         return tokens;
      }

      public boolean isEmpty() {
         return tokens.isEmpty();
      }
   }

   private static class TemplateToken extends ResourceToken {
      private final String bundleName;
      private final Long uuid;
      private final String name;
      private final URL url;

      public TemplateToken(String bundleName, Long uuid, String name, URL url) {
         super(uuid, name);
         this.bundleName = bundleName;
         this.uuid = uuid;
         this.name = name;
         this.url = url;
      }

      @Override
      public URL getUrl() {
         return url;
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = super.hashCode();
         result = prime * result + ((url == null) ? 0 : url.hashCode());
         return result;
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         }
         if (!super.equals(obj)) {
            return false;
         }
         if (getClass() != obj.getClass()) {
            return false;
         }
         ResourceToken other = (ResourceToken) obj;
         if (getUrl() == null) {
            if (other.getUrl() != null) {
               return false;
            }
         } else if (!getUrl().equals(other.getUrl())) {
            return false;
         }
         return true;
      }

      @Override
      public String toString() {
         return String.format("%s::%s::%s - [%s]", bundleName, uuid, name, url);
      }
   }

}