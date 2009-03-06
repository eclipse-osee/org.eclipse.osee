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

import java.io.OutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.ui.data.model.editor.model.ArtifactDataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.AttributeDataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.DataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.RelationDataType;
import org.eclipse.osee.framework.ui.data.model.editor.utility.ODMConstants;

/**
 * @author Roberto E. Escobar
 */
public class ODMXmlWriter {

   private final XMLStreamWriter writer;
   private final ODMXmlFactory xmlDataTypeFactory;

   public ODMXmlWriter(OutputStream outputStream) throws OseeCoreException {
      try {
         XMLOutputFactory factory = XMLOutputFactory.newInstance();
         writer = factory.createXMLStreamWriter(outputStream, "UTF-8");
         writer.writeStartDocument("UTF-8", "1.0");
         writer.writeStartElement("oseeTypes");
         xmlDataTypeFactory = new ODMXmlFactory();
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      }
   }

   public void write(IProgressMonitor monitor, ArtifactDataType... artifacts) throws OseeCoreException {
      try {
         if (artifacts != null && artifacts.length > 0) {
            Set<ArtifactDataType> superTypes = new HashSet<ArtifactDataType>();
            Set<ArtifactDataType> artifactCache = new HashSet<ArtifactDataType>();
            Set<AttributeDataType> attributeCache = new HashSet<AttributeDataType>();
            Set<RelationDataType> relationCache = new HashSet<RelationDataType>();

            for (ArtifactDataType artifact : artifacts) {
               artifactCache.add(artifact);
               if (artifact.getSuperType() != null) {
                  superTypes.add(artifact.getSuperType());
               }
               attributeCache.addAll(artifact.getLocalAttributes());
               relationCache.addAll(artifact.getLocalRelations());
            }

            List<ArtifactDataType> toImport = Collections.setComplement(superTypes, artifactCache);
            if (!toImport.isEmpty()) {
               BaseXmlDataType<DataType> toReturn = xmlDataTypeFactory.getXmlDataType(DataType.class);
               writer.writeStartElement("imports");
               for (ArtifactDataType artifact : toImport) {
                  toReturn.write(writer, artifact);
               }
               writer.writeEndElement();
            }

            writeCollection(monitor, attributeCache);
            writeCollection(monitor, relationCache);
            writeCollection(monitor, artifactCache);
         }
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      }
   }

   private void writeCollection(IProgressMonitor monitor, Collection<? extends DataType> collection) throws XMLStreamException {
      if (!collection.isEmpty()) {
         Class<?> clazz = collection.iterator().next().getClass();
         BaseXmlDataType<DataType> toReturn = xmlDataTypeFactory.getXmlDataType(clazz);
         String typeName = clazz.getSimpleName();
         writer.writeStartElement(typeName);
         int index = 0;
         int total = collection.size();
         for (DataType dataType : collection) {
            monitor.subTask(String.format("Writing %s [%s of %s]", typeName, ++index, total));
            toReturn.write(writer, dataType);
         }
         monitor.worked(ODMConstants.SHORT_TASK_STEPS);
         writer.writeEndElement();
      }
   }

   public void close() throws XMLStreamException {
      try {
         writer.writeEndElement();
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
