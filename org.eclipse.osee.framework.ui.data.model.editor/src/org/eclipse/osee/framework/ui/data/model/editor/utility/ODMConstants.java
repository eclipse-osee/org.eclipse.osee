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

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.data.model.editor.model.DataType;
import org.eclipse.swt.graphics.Font;

/**
 * @author Roberto E. Escobar
 */
public class ODMConstants {

   public static final String DEFAULT_NAMESPACE = "default";
   public static final Font HEADER_FONT = JFaceResources.getTextFont();

   public static String getNamespace(DataType dataType) {
      String namespace = dataType.getNamespace();
      if (!Strings.isValid(namespace) || namespace.equals("null") || namespace.equals(ODMConstants.DEFAULT_NAMESPACE)) {
         namespace = "<<" + ODMConstants.DEFAULT_NAMESPACE + ">>";
      }
      return namespace;
   }

   public static String getDataTypeText(DataType dataType) {
      return String.format("%s:%s", getNamespace(dataType), dataType.getName());
   }
}
