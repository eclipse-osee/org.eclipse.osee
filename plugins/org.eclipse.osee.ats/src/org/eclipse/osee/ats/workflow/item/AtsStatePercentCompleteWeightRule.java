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
package org.eclipse.osee.ats.workflow.item;

import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkRuleDefinition;

/**
 * @author Donald G. Dunne
 */
public class AtsStatePercentCompleteWeightRule extends WorkRuleDefinition {

   public final static String ID = "atsStatePercentCompleteWeight";

   public AtsStatePercentCompleteWeightRule() {
      this(ID, ID);
   }

   public AtsStatePercentCompleteWeightRule(String name, String id) {
      super(name, id);
      setDescription("Work Flow Option: <state>=<percent> Work Data attributes specify weighting given to each state in percent complete calculations.  <state> is either state name (not id) and <percent> is number from 0..1");
   }

}
