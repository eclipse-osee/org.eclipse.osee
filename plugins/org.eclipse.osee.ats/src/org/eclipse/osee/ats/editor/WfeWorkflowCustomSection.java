/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.editor;

import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.workdef.StateXWidgetPage;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public class WfeWorkflowCustomSection extends WfeWorkflowSection {

   public WfeWorkflowCustomSection(Composite parent, int style, StateXWidgetPage page, AbstractWorkflowArtifact sma, WorkflowEditor editor) {
      super(parent, style, page, sma, editor);
   }

}
