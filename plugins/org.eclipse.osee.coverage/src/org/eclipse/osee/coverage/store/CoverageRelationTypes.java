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
package org.eclipse.osee.coverage.store;

import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.RelationSide;

/**
 * @author Donald G. Dunne
 */
public final class CoverageRelationTypes {

   public static final IRelationTypeSide TeamWorkflowTargetedForVersion_Version = TokenFactory.createRelationTypeSide(
      RelationSide.SIDE_B, "AAMFE99pzm4zSibDT9gA", "TeamWorkflowTargetedForVersion");
   public static final IRelationTypeSide TeamWorkflowTargetedForVersion_Workflow = TokenFactory.createRelationTypeSide(
      RelationSide.SIDE_A, "AAMFE99pzm4zSibDT9gA", "TeamWorkflowTargetedForVersion");
   public static final IRelationTypeSide ActionToWorkflow_Action = TokenFactory.createRelationTypeSide(
      RelationSide.SIDE_A, "AAMFE953ixQThusHUPwA", "ActionToWorkflow");
   public static final IRelationTypeSide ActionToWorkflow_WorkFlow = TokenFactory.createRelationTypeSide(
      RelationSide.SIDE_B, "AAMFE953ixQThusHUPwA", "ActionToWorkflow");

   private CoverageRelationTypes() {
      // Constants
   }
}