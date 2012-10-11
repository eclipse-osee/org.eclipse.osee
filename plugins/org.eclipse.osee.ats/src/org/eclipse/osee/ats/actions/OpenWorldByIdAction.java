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
package org.eclipse.osee.ats.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.util.AtsEditor;
import org.eclipse.osee.ats.world.search.MultipleHridSearchData;
import org.eclipse.osee.ats.world.search.MultipleHridSearchOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class OpenWorldByIdAction extends Action {

   public String overrideId = null;
   public boolean pend = false;

   public OpenWorldByIdAction() {
      this("Open World Editor by ID(s)");
   }

   public OpenWorldByIdAction(String name) {
      super(name);
      setToolTipText(getText());
   }

   @Override
   public void run() {
      MultipleHridSearchData data = new MultipleHridSearchData(getText(), AtsEditor.WorldEditor);
      if (Strings.isValid(overrideId)) {
         data.setEnteredIds(overrideId);
      }
      MultipleHridSearchOperation operation = new MultipleHridSearchOperation(data);
      Operations.executeAsJob(operation, true);
      if (pend) {
         operation.run(null);
      } else {
         Operations.executeAsJob(operation, true);
      }

   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.OPEN_BY_ID);
   }

   public void setOverrideIdString(String enteredIdString) {
      this.overrideId = enteredIdString;
   }

   public void setPend(boolean pend) {
      this.pend = pend;
   }

}
