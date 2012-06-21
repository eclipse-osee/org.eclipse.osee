/*
 * Created on Jun 21, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workdef.api;

import java.util.List;
import org.eclipse.osee.framework.core.util.XResultData;

public interface IAtsWorkDefinitionService {

   IAtsWorkDefinition getWorkDef(String id, XResultData resultData) throws Exception;

   IAtsWorkDefinition copyWorkDefinition(String newName, IAtsWorkDefinition workDef, XResultData resultData, IAttributeResolver resolver, IUserResolver iUserResolver);

   IAtsWorkDefinition createWorkDefinition(String name);

   IAtsDecisionReviewOption createDecisionReviewOption(String pageName, boolean isFollowupRequired, List<String> userIds);

   IAtsCompositeLayoutItem createCompositeLayoutItem();

   IAtsLayoutItem createLayoutItem(String string);

   IAtsStateDefinition createStateDefinition(String string);

   IAtsWidgetDefinition createWidgetDefinition(String string);

   IAtsPeerReviewDefinition createPeerReviewDefinition(String string);

   IAtsDecisionReviewDefinition createDecisionReviewDefinition(String string);

   IAtsDecisionReviewOption createDecisionReviewOption(String string);

   IAtsCompositeLayoutItem createCompositeLayoutItem(int numColumns);

   IAtsWidgetDefinitionFloatMinMaxConstraint createWidgetDefinitionFloatMinMaxConstraint(String minConstraint, String minConstraint2);

   IAtsWidgetDefinitionIntMinMaxConstraint createWidgetDefinitionIntMinMaxConstraint(String minConstraint, String minConstraint2);

   IAtsWidgetDefinitionListMinMaxSelectedConstraint createWidgetDefinitionListMinMaxSelectedConstraint(String minConstraint, String minConstraint2);

   IAtsWidgetDefinitionIntMinMaxConstraint createWidgetDefinitionIntMinMaxConstraint(int i, int j);

   IAtsWidgetDefinitionListMinMaxSelectedConstraint createWidgetDefinitionListMinMaxSelectedConstraint(int i, int j);

   IAtsWidgetDefinitionFloatMinMaxConstraint createWidgetDefinitionFloatMinMaxConstraint(double d, double e);

   String getStorageString(IAtsWorkDefinition workDef, XResultData resultData) throws Exception;

}
