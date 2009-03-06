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
package org.eclipse.osee.framework.ui.data.model.editor.model.xml;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.data.model.editor.ODMEditorActivator;
import org.eclipse.osee.framework.ui.data.model.editor.model.ArtifactDataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.AttributeDataType;
import org.eclipse.osee.framework.ui.data.model.editor.utility.ImageUtility;
import org.eclipse.swt.graphics.Image;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactDataTypeXml extends BaseXmlDataType<ArtifactDataType> {
   private final String ARTIFACT_TAG = "artifact";

   private final String IMAGE_DATA = "imageData";

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.data.model.editor.utility.ODMXmlWriter.DataTypeWriter#getElementName()
    */
   @Override
   public String getElementName() {
      return ARTIFACT_TAG;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.data.model.editor.utility.ODMXmlWriter.DataTypeWriter#writeBody(org.eclipse.osee.framework.ui.data.model.editor.model.DataType)
    */
   @Override
   protected void writeBody(XMLStreamWriter writer, ArtifactDataType dataType) throws XMLStreamException {
      if (dataType.getSuperType() != null) {
         writer.writeStartElement("superType");
         writeIdAttributes(writer, dataType.getSuperType());
         writer.writeEndElement();
      }

      for (AttributeDataType attribute : dataType.getLocalAttributes()) {
         writer.writeStartElement("attribute");
         writeIdAttributes(writer, attribute);
         writer.writeEndElement();
      }

      if (dataType.getImage() != null) {
         try {
            writeCDataElement(writer, IMAGE_DATA, new String(ImageUtility.imageToBase64(dataType.getImage()), "UTF-8"));
         } catch (UnsupportedEncodingException ex) {
            throw new XMLStreamException(ex);
         }
      }
   }

   private Image stringToImage(String data) {
      try {
         return ImageUtility.base64ToImage(data.getBytes("UTF-8"));
      } catch (Exception ex) {
         OseeLog.log(ODMEditorActivator.class, Level.SEVERE, ex);
      }
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.data.model.editor.model.xml.BaseXmlDataType#populateFromCDataElement(org.eclipse.osee.framework.ui.data.model.editor.model.DataType, java.lang.String, java.lang.String)
    */
   @Override
   protected void populateFromCDataElement(ArtifactDataType dataType, String tag, String text) throws XMLStreamException {
      super.populateFromCDataElement(dataType, tag, text);
      if (IMAGE_DATA.equals(tag)) dataType.setImage(stringToImage(text));
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.data.model.editor.model.xml.BaseXmlDataType#newDataTypeInstance()
    */
   @Override
   protected ArtifactDataType newDataTypeInstance() {
      return new ArtifactDataType();
   }
}