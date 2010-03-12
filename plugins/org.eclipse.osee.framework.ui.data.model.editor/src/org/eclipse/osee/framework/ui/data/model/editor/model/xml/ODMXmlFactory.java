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

import java.util.HashMap;
import org.eclipse.osee.framework.ui.data.model.editor.model.ArtifactDataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.AttributeDataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.DataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.RelationDataType;

/**
 * @author Roberto E. Escobar
 */
public class ODMXmlFactory {
   private final HashMap<String, BaseXmlDataType<?>> writerMap;

   public ODMXmlFactory() {
      writerMap = new HashMap<String, BaseXmlDataType<?>>();
      writerMap.put(ArtifactDataType.class.getSimpleName(), new ArtifactDataTypeXml());
      writerMap.put(AttributeDataType.class.getSimpleName(), new AttributeDataTypeXml());
      writerMap.put(RelationDataType.class.getSimpleName(), new RelationDataTypeXml());
      writerMap.put(DataType.class.getSimpleName(), new ImportDataTypeXml());
   }

   @SuppressWarnings("unchecked")
   public BaseXmlDataType<DataType> getXmlDataType(Class clazzToGet) {
      return (BaseXmlDataType<DataType>) writerMap.get(clazzToGet.getSimpleName());
   }

   @SuppressWarnings("unchecked")
   public BaseXmlDataType<DataType> getXmlDataType(String clazzToGet) {
      return (BaseXmlDataType<DataType>) writerMap.get(clazzToGet);
   }
}
