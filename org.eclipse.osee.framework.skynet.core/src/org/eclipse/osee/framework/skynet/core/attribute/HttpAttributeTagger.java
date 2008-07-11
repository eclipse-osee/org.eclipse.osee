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
package org.eclipse.osee.framework.skynet.core.attribute;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.IAttributeSaveListener;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.linking.HttpProcessor;
import org.eclipse.osee.framework.skynet.core.linking.HttpUrlBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

/**
 * @author Roberto E. Escobar
 */
public class HttpAttributeTagger implements IAttributeSaveListener {
   private static final HttpAttributeTagger instance = new HttpAttributeTagger();

   private CopyOnWriteArraySet<AttributeData> taggingInfo;
   private ExecutorService executor;

   private HttpAttributeTagger() {
      this.taggingInfo = new CopyOnWriteArraySet<AttributeData>();
      this.executor = Executors.newSingleThreadExecutor();
   }

   public static HttpAttributeTagger getInstance() {
      return instance;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.artifact.IAttributeSaveListener#notifyOnAttributeSave(org.eclipse.osee.framework.skynet.core.artifact.Artifact)
    */
   @Override
   public void notifyOnAttributeSave(Artifact artifact) throws Exception {
      List<Attribute<?>> attributes = artifact.getAttributes();
      for (Attribute<?> attribute : attributes) {
         if (attribute.isDirty() && attribute.getAttributeType().isTaggable()) {
            this.taggingInfo.add(new AttributeData(attribute.getAttrId(), attribute.getGammaId(),
                  attribute.getAttributeType().getTaggerId()));
         }
      }
      if (this.taggingInfo.isEmpty() != true) {
         this.executor.submit(new TagService());
      }
   }

   private final class AttributeData {
      private int attrId;
      private long gammaId;
      private String taggerId;

      public AttributeData(int attrId, long gammaId, String taggerId) {
         super();
         this.attrId = attrId;
         this.gammaId = gammaId;
         this.taggerId = taggerId;
      }

      public String getAttrId() {
         return Integer.toString(attrId);
      }

      public String getGammaId() {
         return Long.toString(gammaId);
      }

      public String getTaggerId() {
         return taggerId;
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         }
         if (!(obj instanceof AttributeData)) {
            return false;
         }
         AttributeData other = (AttributeData) obj;
         return other.attrId == this.attrId && other.gammaId == this.gammaId;
      }
   }

   private final class TagService implements Runnable {

      public void run() {
         try {
            Document document = Jaxp.newDocument();
            Element rootElement = document.createElement("tag");
            document.appendChild(rootElement);

            List<AttributeData> toDelete = new ArrayList<AttributeData>();
            for (AttributeData attributeData : taggingInfo) {
               Element element = document.createElement("attribute");
               element.setAttribute("attrId", attributeData.getAttrId());
               element.setAttribute("gammaId", attributeData.getGammaId());
               element.setAttribute("taggerId", attributeData.getTaggerId());
               rootElement.appendChild(element);

               toDelete.add(attributeData);
            }
            taggingInfo.removeAll(toDelete);
            toDelete.clear();
            toDelete = null;

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            OutputFormat format = new OutputFormat(document);
            format.setIndenting(false);
            format.setIndent(0);
            XMLSerializer serializer = new XMLSerializer(outputStream, format);
            serializer.serialize(document);

            System.out.println(outputStream.toString());
            sendToTagger(new ByteArrayInputStream(outputStream.toByteArray()));

         } catch (OseeCoreException ex) {
            ex.printStackTrace();
         } catch (IOException ex) {
            ex.printStackTrace();
         } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
         }
      }

      private void sendToTagger(InputStream inputStream) throws OseeCoreException {
         Map<String, String> parameters = new HashMap<String, String>();
         String response = null;
         try {
            String url = HttpUrlBuilder.getInstance().getOsgiServletServiceUrl("search", parameters);
            response = HttpProcessor.put(new URL(url), inputStream, "application/xml", "UTF-8");
            if (Strings.isValid(response)) {
               System.out.println("Tagger Response: " + response);
            } else {
               System.out.println(response);
            }
         } catch (Exception ex) {
            throw new OseeCoreException(ex);
         }
      }
   }
}
