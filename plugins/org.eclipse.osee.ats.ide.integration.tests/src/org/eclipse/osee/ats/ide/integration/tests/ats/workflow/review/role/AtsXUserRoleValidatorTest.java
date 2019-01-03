/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.integration.tests.ats.workflow.review.role;

import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.review.IAtsPeerReviewRoleManager;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.review.Role;
import org.eclipse.osee.ats.api.review.UserRole;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.WidgetOption;
import org.eclipse.osee.ats.api.workdef.WidgetOptionHandler;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workdef.WidgetStatus;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.review.defect.ValidatorTestUtil;
import org.eclipse.osee.ats.ide.workflow.review.role.AtsXUserRoleValidator;
import org.eclipse.osee.ats.ide.workflow.review.role.UserRoleError;
import org.junit.Assert;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author Donald G. Dunne
 */
public class AtsXUserRoleValidatorTest {

   // @formatter:off
   @Mock IAtsPeerToPeerReview workItem;
   @Mock IAtsWidgetDefinition widgetDef;
   @Mock AtsApi atsApi;
   @Mock IAtsStateDefinition fromStateDef;
   @Mock IAtsStateDefinition toStateDef;
   @Mock IAttributeResolver attrResolver;
   @Mock IAtsUserService userService;
   @Mock IAtsPeerReviewRoleManager roleMgr;
   // @formatter:on

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);

      when(widgetDef.getName()).thenReturn("test");
      when(fromStateDef.getStateType()).thenReturn(StateType.Working);
      when(toStateDef.getStateType()).thenReturn(StateType.Working);

      when(atsApi.getAttributeResolver()).thenReturn(attrResolver);
      when(atsApi.getUserService()).thenReturn(userService);

      when(workItem.getRoleManager()).thenReturn(roleMgr);
   }

   @org.junit.Test
   public void testValidateTransition() {
      AtsXUserRoleValidator validator = new AtsXUserRoleValidator();

      when(widgetDef.getXWidgetName()).thenReturn("xList");

      // Valid for anything not XIntegerDam
      WidgetResult result = validator.validateTransition(workItem, ValidatorTestUtil.emptyValueProvider, widgetDef,
         fromStateDef, toStateDef, atsApi);
      ValidatorTestUtil.assertValidResult(result);

      when(widgetDef.getXWidgetName()).thenReturn("XUserRoleViewer");

      // Not valid to have no roles
      result = validator.validateTransition(workItem, ValidatorTestUtil.emptyValueProvider, widgetDef, fromStateDef,
         toStateDef, atsApi);
      Assert.assertEquals(WidgetStatus.Invalid_Incompleted, result.getStatus());
      Assert.assertEquals(UserRoleError.OneRoleEntryRequired.getError(), result.getDetails());

      when(widgetDef.getOptions()).thenReturn(new WidgetOptionHandler(WidgetOption.NOT_REQUIRED_FOR_TRANSITION));

      // Not valid to have no roles
      result = validator.validateTransition(workItem, ValidatorTestUtil.emptyValueProvider, widgetDef, fromStateDef,
         toStateDef, atsApi);
      Assert.assertEquals(WidgetStatus.Invalid_Incompleted, result.getStatus());
      Assert.assertEquals(UserRoleError.OneRoleEntryRequired.getError(), result.getDetails());
   }

   @org.junit.Test
   public void testValidateTransition_Roles() {
      AtsXUserRoleValidator validator = new AtsXUserRoleValidator();

      when(widgetDef.getXWidgetName()).thenReturn("XUserRoleViewer");

      UserRole author = new UserRole(Role.Author, "2134", 0.0, false);
      UserRole reviewer = new UserRole(Role.Reviewer, "123");
      List<UserRole> roles = Arrays.asList(author, reviewer);

      when(roleMgr.getUserRoles()).thenReturn(roles);
      when(roleMgr.getUserRoles(Role.Author)).thenReturn(Arrays.asList(author));
      when(roleMgr.getUserRoles(Role.Reviewer)).thenReturn(Arrays.asList(reviewer));

      when(fromStateDef.getName()).thenReturn("from");
      when(fromStateDef.getName()).thenReturn("to");

      // Valid Roles
      WidgetResult result = validator.validateTransition(workItem, null, widgetDef, fromStateDef, toStateDef, atsApi);
      ValidatorTestUtil.assertValidResult(result);

      // Not valid to have no author
      when(roleMgr.getUserRoles(Role.Author)).thenReturn(Arrays.asList());
      result = validator.validateTransition(workItem, null, widgetDef, fromStateDef, toStateDef, atsApi);
      Assert.assertEquals(WidgetStatus.Invalid_Incompleted, result.getStatus());
      Assert.assertEquals(UserRoleError.MustHaveAtLeastOneAuthor.getError(), result.getDetails());

      // Not valid to have no reviewer
      when(roleMgr.getUserRoles(Role.Author)).thenReturn(Arrays.asList(author));
      when(roleMgr.getUserRoles(Role.Reviewer)).thenReturn(Arrays.asList());
      result = validator.validateTransition(workItem, null, widgetDef, fromStateDef, toStateDef, atsApi);
      Assert.assertEquals(WidgetStatus.Invalid_Incompleted, result.getStatus());
      Assert.assertEquals(UserRoleError.MustHaveAtLeastOneReviewer.getError(), result.getDetails());

   }

}
