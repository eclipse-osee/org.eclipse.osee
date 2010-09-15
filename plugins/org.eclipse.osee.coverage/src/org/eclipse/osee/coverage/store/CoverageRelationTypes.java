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

import org.eclipse.osee.framework.core.data.NamedIdentity;
import org.eclipse.osee.framework.core.enums.IRelationEnumeration;
import org.eclipse.osee.framework.core.enums.RelationSide;

/**
 * @author Donald G. Dunne
 */
public class CoverageRelationTypes extends NamedIdentity implements IRelationEnumeration {
   private final RelationSide relationSide;

   public static final CoverageRelationTypes TeamWorkflowTargetedForVersion_Version = new CoverageRelationTypes(
      RelationSide.SIDE_B, "AAMFE99pzm4zSibDT9gA", "TeamWorkflowTargetedForVersion");
   public static final CoverageRelationTypes TeamWorkflowTargetedForVersion_Workflow = new CoverageRelationTypes(
      RelationSide.SIDE_A, "AAMFE99pzm4zSibDT9gA", "TeamWorkflowTargetedForVersion");

   private CoverageRelationTypes(RelationSide relationSide, String guid, String name) {
      super(guid, name);
      this.relationSide = relationSide;
   }

   @Override
   public RelationSide getSide() {
      return relationSide;
   }
}