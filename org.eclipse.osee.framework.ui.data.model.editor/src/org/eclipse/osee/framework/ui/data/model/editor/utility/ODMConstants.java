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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.osee.framework.ui.data.model.editor.ODMEditorActivator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * @author Roberto E. Escobar
 */
public class ODMConstants {

   public static final String DEFAULT_NAMESPACE = "default";

   public static final Color FONT_COLOR = new Color(null, 1, 70, 122);
   public static final Color CONNECTION_COLOR = new Color(null, 172, 182, 198);
   public static final Color CLASS_BG_COLOR = new Color(null, 242, 240, 255);
   public static final Font BOLD = new Font(null, "", 10, SWT.BOLD);
   public static final Font HEADER_FONT = JFaceResources.getTextFont();

   public static Image getImage(String imageName) {
      return ODMEditorActivator.getInstance().getImage(imageName);
   }

   public static ImageDescriptor getImageDescriptor(String imageName) {
      return ODMEditorActivator.getInstance().getImageDescriptor(imageName);
   }
}
