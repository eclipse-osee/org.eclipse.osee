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
package org.eclipse.osee.framework.ui.skynet.render;

public enum PresentationType {
   GENERALIZED_EDIT, // open using general editor (i.e. artifact editor)
   SPECIALIZED_EDIT, // open using application specific editor
   DIFF,
   PREVIEW, // open read-only using application specific editor
   MERGE,
   MERGE_EDIT,
   DEFAULT_OPEN, // up to the renderer to determine what is used for default
   PRINT;

   public boolean matches(PresentationType... presentationTypes) {
      for (PresentationType presentationType : presentationTypes) {
         if (this == presentationType) {
            return true;
         }
      }
      return false;
   }
}