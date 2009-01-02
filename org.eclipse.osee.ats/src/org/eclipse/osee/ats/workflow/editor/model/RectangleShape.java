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

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.swt.graphics.Image;

/**
 * A rectangular shape.
 * 
 * @author Donald G. Dunne
 */
public class RectangleShape extends Shape {
   /** A 16x16 pictogram of a rectangular shape. */
   private static final Image RECTANGLE_ICON = createImage("rectangle16.gif");

   public RectangleShape() {
      setSize(new Dimension(100, 50));
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.workflow.editor.model.ModelElement#validForSave()
    */
   @Override
   public Result validForSave() throws OseeCoreException {
      return Result.TrueResult;
   }

   @Override
   public Image getIcon() {
      return RECTANGLE_ICON;
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
