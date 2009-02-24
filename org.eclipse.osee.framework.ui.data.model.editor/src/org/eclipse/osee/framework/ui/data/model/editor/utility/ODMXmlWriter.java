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
package org.eclipse.osee.framework.ui.data.model.editor.utility;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.eclipse.osee.framework.ui.data.model.editor.model.ArtifactDataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.AttributeDataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.DataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.RelationDataType;
import org.eclipse.swt.graphics.Image;

/**
 * @author Roberto E. Escobar
 */
public class ODMXmlWriter {

   private XMLStreamWriter writer;
   private Map<String, byte[]> imageCache;
   private Set<AttributeDataType> attributeCache;
   private Set<RelationDataType> relationCache;

   public ODMXmlWriter() {
   }

   private void initialize(OutputStream outputStream) throws XMLStreamException {
      this.imageCache = new HashMap<String, byte[]>();
      this.attributeCache = new HashSet<AttributeDataType>();
      this.relationCache = new HashSet<RelationDataType>();

      XMLOutputFactory factory = XMLOutputFactory.newInstance();
      writer = factory.createXMLStreamWriter(outputStream);
      writer.writeStartDocument("UTF-8", "1.0");
   }

   public void write(ArtifactDataType... artifacts) throws XMLStreamException, UnsupportedEncodingException {
      if (artifacts != null) {
         for (ArtifactDataType artifact : artifacts) {
            // Find missing items (imports)
            // 
         }

         writeImport("", "", "");
         for (AttributeDataType attribute : attributeCache) {
            writeAttribute(attribute);
         }

         for (RelationDataType relation : relationCache) {
            writeRelation(relation);
         }

         for (ArtifactDataType artifact : artifacts) {
            writeArtifact(artifact);
         }

         writeImages();
      }
   }

   private void writeImport(String type, String namespace, String name) throws XMLStreamException {
      writer.writeStartElement("import");
      writer.writeAttribute("type", type);
      writer.writeAttribute("namespace", namespace);
      writer.writeAttribute("name", name);
      writer.writeEndElement();
   }

   private void writeDataType(DataType dataType) throws XMLStreamException {
      writer.writeAttribute("namespace", dataType.getNamespace());
      writer.writeAttribute("name", dataType.getName());
   }

   private String getImageId(Image image) {
      return "0";
   }

   private void writeArtifact(ArtifactDataType artifact) throws XMLStreamException {
      writer.writeStartElement("artifact");
      writeDataType(artifact);

      if (artifact.getImage() != null) {
         writer.writeAttribute("imageId", getImageId(artifact.getImage()));
      }

      if (artifact.getAncestorType() != null) {
         writer.writeStartElement("parentArtifact");
         writeDataType(artifact);
         writer.writeEndElement();
      }

      for (AttributeDataType attribute : artifact.getLocalAttributes()) {
         attributeCache.add(attribute);
         writer.writeStartElement("attribute");
         writeDataType(attribute);
         writer.writeEndElement();
      }
      relationCache.addAll(artifact.getLocalRelations());
      writer.writeEndElement();
   }

   private void writeImages() throws XMLStreamException, UnsupportedEncodingException {
      for (String key : imageCache.keySet()) {
         byte[] image = imageCache.get(key);
         writer.writeStartElement("imageData");
         writer.writeAttribute("id", key);
         writer.writeCData(new String(image, "UTF-8"));
         writer.writeEndElement();
      }
   }

   private void writeAttribute(AttributeDataType attribute) throws XMLStreamException {
      writer.writeStartElement("attribute");
      writeDataType(attribute);
      writer.writeAttribute("minOccurrence", String.valueOf(attribute));
      writer.writeAttribute("maxOccurrence", String.valueOf(attribute));
      writer.writeAttribute("extension", attribute.getFileTypeExtension());
      writer.writeStartElement("tagger");
      writer.writeCharacters(attribute.getTaggerId());
      writer.writeEndElement();

      writer.writeStartElement("attributeBase");
      writer.writeCharacters(attribute.getBaseAttributeClass());
      writer.writeEndElement();

      writer.writeStartElement("attributeProvider");
      writer.writeCharacters(attribute.getProviderAttributeClass());
      writer.writeEndElement();

      writer.writeStartElement("defaultValue");
      writer.writeCData(attribute.getDefaultValue());
      writer.writeEndElement();

      writer.writeStartElement("validityXml");
      writer.writeCData(attribute.getValidityXml());
      writer.writeEndElement();

      writer.writeStartElement("toolTip");
      writer.writeCharacters(attribute.getToolTipText());
      writer.writeEndElement();

      writer.writeEndElement();
   }

   private void writeRelation(RelationDataType relation) throws XMLStreamException {
      writer.writeStartElement("relation");
      writeDataType(relation);
      writer.writeEndElement();
   }

   public void close() throws XMLStreamException {
      try {
         writer.writeEndDocument();
      } finally {
         try {
            writer.flush();
         } finally {
            if (writer != null) {
               writer.close();
            }
         }
      }
   }
}
