/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.workdef.editor;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.renderer.handlers.AbstractEditorHandler;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;

/**
 * @author Mark Joy
 */
public class AtsRuleDefinitionDslHandler extends AbstractEditorHandler {

   @Override
   public Object executeWithException(ExecutionEvent event, IStructuredSelection selection) throws OseeCoreException {
      if (!artifacts.isEmpty()) {
         AtsRuleDefinitionDslRenderer renderer = new AtsRuleDefinitionDslRenderer();
         renderer.open(artifacts, PresentationType.SPECIALIZED_EDIT);
      }
      return null;
   }
}
