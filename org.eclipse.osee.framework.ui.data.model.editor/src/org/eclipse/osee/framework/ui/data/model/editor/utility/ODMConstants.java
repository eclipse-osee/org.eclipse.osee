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
import org.eclipse.osee.framework.ui.data.model.editor.model.DataType;
import org.eclipse.swt.graphics.Font;

/**
 * @author Roberto E. Escobar
 */
public class ODMConstants {

   public static final int TOTAL_STEPS = Integer.MAX_VALUE;
   public static final int SHORT_TASK_STEPS = TOTAL_STEPS / 50;
   public static final int VERY_LONG_TASK = TOTAL_STEPS / 2;
   public static final int TASK_STEPS = (TOTAL_STEPS - SHORT_TASK_STEPS * 3 - VERY_LONG_TASK) / 2;

   public static final Font HEADER_FONT = JFaceResources.getTextFont();

   public static String getDataTypeText(DataType dataType) {
      return dataType.getName();
   }
}
