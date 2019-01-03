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
package org.eclipse.osee.ats.ide.workdef.viewer;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.framework.core.data.Adaptable;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkDefConfigEditorInput implements IEditorInput, Adaptable {
   protected IAtsWorkDefinition workflow;

   public AtsWorkDefConfigEditorInput(IAtsWorkDefinition workflow) {
      this.workflow = workflow;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof AtsWorkDefConfigEditorInput) {
         AtsWorkDefConfigEditorInput otherEdInput = (AtsWorkDefConfigEditorInput) obj;
         return workflow.getName().equals(otherEdInput.workflow.getName());
      }
      return false;
   }

   @Override
   public int hashCode() {
      return workflow.getName().hashCode();
   }

   @Override
   public boolean exists() {
      return true;
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return null;
   }

   @Override
   public String getName() {
      if (workflow == null) {
         return "No Artifact Input Provided";
      }
      return workflow.getName();
   }

   @Override
   public IPersistableElement getPersistable() {
      return null;
   }

   @Override
   public String getToolTipText() {
      return getName();
   }
}