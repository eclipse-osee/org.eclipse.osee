/*******************************************************************************
 * Copyright (c) 2004, 2005 Donald G. Dunne and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Donald G. Dunne - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.workflow.editor.model;

import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.swt.graphics.Image;

/**
 * An elliptical shape.
 * 
 * @author Donald G. Dunne
 */
public class EllipticalShape extends Shape {

   /** A 16x16 pictogram of an elliptical shape. */
   private static final Image ELLIPSE_ICON = createImage("ellipse16.gif");

   @Override
   public Image getIcon() {
      return ELLIPSE_ICON;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.workflow.editor.model.ModelElement#validForSave()
    */
   @Override
   public Result validForSave() throws OseeCoreException {
      return Result.TrueResult;
   }

   @Override
   public String toString() {
      return "Ellipse " + hashCode();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.config.editor.model.Shape#getName()
    */
   @Override
   protected String getName() {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.config.editor.model.Shape#getToolTip()
    */
   @Override
   protected String getToolTip() {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.workflow.editor.model.ModelElement#doSave(org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction)
    */
   @Override
   public Result doSave(SkynetTransaction transaction) throws OseeCoreException {
      return Result.TrueResult;
   }

}
