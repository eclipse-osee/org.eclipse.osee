/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.workflow.transition;

import static org.eclipse.osee.ats.api.workdef.StateType.Cancelled;
import static org.eclipse.osee.ats.api.workdef.StateType.Completed;
import static org.eclipse.osee.ats.api.workdef.StateType.Working;
import static org.eclipse.osee.framework.jdk.core.util.Collections.isEqual;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.transition.TransitionData;
import org.eclipse.osee.ats.core.workflow.transition.TransitionAssigneesOperation;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for TransitionAssigneesOperation. Set debug==true and run to see truth table. Also stored in
 * TransitionAssigneesOperationTruthTable.html in code.
 *
 * @author Donald G. Dunne
 */
public class TransitionAssigneesOperationTest {

   private AtsUser joeSmith;
   private AtsUser kayJones;
   private AtsUser unAssigned;
   private TransitionData tData;
   private List<AtsUser> revRoleAssignees;
   private AtsUser jasonMichael;
   private List<AtsUser> currAssignees;
   private List<AtsUser> expectAssignees;
   private static XResultData truthTableRd;
   // Set to true to create and open an html truth table
   private static boolean debug = false;

   public static synchronized XResultData getTruthTableRd() {
      if (debug && truthTableRd == null) {
         truthTableRd = new XResultData();
         truthTableRd.addRaw(AHTML.beginMultiColumnTable(98, 2));
         truthTableRd.addRaw(
            AHTML.addHeaderRowMultiColumnTable(Arrays.asList("From State", "To State", "Transition User",
               "Curr Assignees", "To-Assignees", "Is System User", "Review Assignees", "Result Assignees", "Notes")));
      }
      return truthTableRd;
   }

   @AfterClass
   public static void report() {
      if (truthTableRd != null) {
         truthTableRd.addRaw(AHTML.endMultiColumnTable());
         XResultDataUI.reportAndOpen(truthTableRd, TransitionAssigneesOperationTest.class.getSimpleName(),
            TransitionAssigneesOperationTest.class.getSimpleName() + ".html");
      }
   }

   @Before
   public void setup() {
      joeSmith = AtsApiService.get().getUserService().getUserById(DemoUsers.Joe_Smith);
      kayJones = AtsApiService.get().getUserService().getUserById(DemoUsers.Kay_Jones);
      jasonMichael = AtsApiService.get().getUserService().getUserById(DemoUsers.Jason_Michael);
      unAssigned = AtsCoreUsers.UNASSIGNED_USER;

      tData = new TransitionData();
      tData.setTransitionUser(joeSmith);
      revRoleAssignees = new ArrayList<>();
   }

   @Test
   public void testCompletedCancelled() {
      tData.setToAssignees(Arrays.asList(kayJones));
      revRoleAssignees.add(jasonMichael);
      currAssignees = Arrays.asList(joeSmith);
      expectAssignees = java.util.Collections.emptyList();

      // Completed always come back as no-assignee
      isEqual(expectAssignees, getToAssignees(currAssignees, Working, Completed, tData, revRoleAssignees, ""));

      // Cancelled always come back as no-assignee
      isEqual(expectAssignees, getToAssignees(currAssignees, Working, Cancelled, tData, revRoleAssignees, ""));
   }

   @Test
   public void testToAssignees() {
      tData.setToAssignees(Arrays.asList(kayJones, jasonMichael));
      currAssignees = Arrays.asList(joeSmith);
      expectAssignees = Arrays.asList(kayJones, jasonMichael);

      // if toAssigneees specified in TransitionData, this overrides all
      isEqual(expectAssignees,
         getToAssignees(currAssignees, Working, Working, tData, revRoleAssignees, "To-Assignees Rules All"));
      isEqual(expectAssignees,
         getToAssignees(currAssignees, Completed, Working, tData, revRoleAssignees, "To-Assignees Rules All"));
      isEqual(expectAssignees,
         getToAssignees(currAssignees, Cancelled, Working, tData, revRoleAssignees, "To-Assignees Rules All"));
   }

   @Test
   public void testToAssigneesAsSystem() {
      AtsUser systemUser = AtsApiService.get().getUserService().getUserById(AtsCoreUsers.SYSTEM_USER);
      tData.setTransitionUser(systemUser);
      currAssignees = Collections.emptyList();
      expectAssignees = Arrays.asList(unAssigned);

      // if toAssigneees specified in TransitionData, this overrides all
      isEqual(expectAssignees,
         getToAssignees(currAssignees, Working, Working, tData, revRoleAssignees, "If System User then UnAssigned"));
   }

   @Test
   public void testCurrentAssignees() {
      currAssignees = Arrays.asList(kayJones, jasonMichael);
      expectAssignees = Arrays.asList(kayJones, jasonMichael);

      // current assignees will be assigned if not otherwise specified
      isEqual(expectAssignees,
         getToAssignees(currAssignees, Working, Working, tData, revRoleAssignees, "Curr Assignees Stay"));
   }

   @Test
   public void testUnCompleteOrCancel() {
      currAssignees = Collections.emptyList();
      expectAssignees = Arrays.asList(unAssigned);

      // un-complete or un-cancel, user should be un-assigned
      isEqual(expectAssignees,
         getToAssignees(currAssignees, Completed, Working, tData, revRoleAssignees, "Un-Complete gets UnAssigned"));
      isEqual(expectAssignees,
         getToAssignees(currAssignees, Cancelled, Working, tData, revRoleAssignees, "Un-Cancel gets UnAssigned"));
   }

   @Test
   public void testCurrentAssigneesWithReviewRoles() {
      currAssignees = Arrays.asList(kayJones);
      revRoleAssignees.add(jasonMichael);
      expectAssignees = Arrays.asList(kayJones, jasonMichael);

      // review roles will be added to current assignees if specified
      isEqual(expectAssignees,
         getToAssignees(currAssignees, Working, Working, tData, revRoleAssignees, "Curr Asssignees + Review Roles"));
   }

   @Test
   public void testCurrentAssigneesUnAssigned() {
      tData.setTransitionUser(joeSmith);
      currAssignees = Collections.emptyList();
      expectAssignees = Arrays.asList(joeSmith);

      // if nothing else specified, transitionUser will be assigned
      isEqual(expectAssignees,
         getToAssignees(currAssignees, Working, Working, tData, revRoleAssignees, "Transition User Assigned"));

      currAssignees = Arrays.asList(unAssigned);

      // if current assignees is unAssigned, transitionUser will be assigned
      isEqual(expectAssignees,
         getToAssignees(currAssignees, Working, Working, tData, revRoleAssignees, "Transition User, Not UnAssigned"));

      currAssignees = Arrays.asList(kayJones, unAssigned);
      revRoleAssignees.add(jasonMichael);
      revRoleAssignees.add(unAssigned);
      expectAssignees = Arrays.asList(kayJones, jasonMichael);

      // UnAssigned will always be removed; currentAssignees and reviewRoles will be returned
      isEqual(expectAssignees, getToAssignees(currAssignees, Working, Working, tData, revRoleAssignees,
         "Always Remove UnAssigned if Assignee(s)"));
   }

   private Set<AtsUser> getToAssignees(List<AtsUser> currAssignees, StateType fromStateType, StateType toStateType,
      TransitionData tData, Collection<AtsUser> reviewRollAssignees, String notes) {

      Set<AtsUser> toAssignees = TransitionAssigneesOperation.getToAssignees(currAssignees, fromStateType, toStateType,
         tData, reviewRollAssignees);

      if (debug) {
         getTruthTableRd().addRaw(
            AHTML.addRowMultiColumnTable(fromStateType.name(), toStateType.name(), tData.getTransitionUser().getName(),
               currAssignees.toString(), tData.getToAssignees().toString(), (tData.isSystemUser() ? "System User" : ""),
               reviewRollAssignees.toString(), toAssignees.toString(), notes));
      }

      return toAssignees;
   }

}
