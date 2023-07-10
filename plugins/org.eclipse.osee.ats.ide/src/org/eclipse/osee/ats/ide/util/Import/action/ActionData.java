/*********************************************************************
 * Copyright (c) 2023 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.ats.ide.util.Import.action;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.task.JaxAttribute;
import org.eclipse.osee.ats.api.user.AtsUser;

/**
 * @author Donald G. Dunne
 */
public final class ActionData {

   protected String title = "";
   protected String desc = "";
   protected String priorityStr = "";
   protected String changeType = "";
   protected Collection<String> assigneeStrs = new HashSet<>();
   protected List<AtsUser> assignees = new LinkedList<>();
   protected AtsUser originator = null;
   protected Collection<String> actionableItems = new HashSet<>();
   protected String version = "";
   protected Double estimatedHours = null;
   protected List<JaxAttribute> attributes = new LinkedList<>();
   protected String agilePoints = "";
   protected String agileTeamName = "";
   protected String agileSprintName = "";

}
