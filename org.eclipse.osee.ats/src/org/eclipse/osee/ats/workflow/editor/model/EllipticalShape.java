/*******************************************************************************
 * Copyright (c) 2004, 2005 Donald G. Dunne and others.
�* All rights reserved. This program and the accompanying materials
�* are made available under the terms of the Eclipse Public License v1.0
�* which accompanies this distribution, and is available at
�* http://www.eclipse.org/legal/epl-v10.html
�*
�* Contributors:
�*����Donald G. Dunne - initial API and implementation
�*******************************************************************************/
package org.eclipse.osee.ats.workflow.editor.model;

import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.swt.graphics.Image;

/**
 * An elliptical shape.
 * 
 * @author Donald G. Dunne
 */
public class EllipticalShape extends Shape {

   /** A 16x16 pictogram of an elliptical shape. */

   @Override
   public Image getIcon() {
      return ImageManager.getImage(AtsImage.ELLIPSE_ICON);
   }

   @Override
   public Result validForSave() throws OseeCoreException {
      return Result.TrueResult;
   }

   @Override
   public String toString() {
      return "Ellipse " + hashCode();
   }

   @Override
   protected String getName() {
      return null;
   }

   @Override
   protected String getToolTip() {
      return null;
   }

   @Override
   public Result doSave(SkynetTransaction transaction) throws OseeCoreException {
      return Result.TrueResult;
   }

}
