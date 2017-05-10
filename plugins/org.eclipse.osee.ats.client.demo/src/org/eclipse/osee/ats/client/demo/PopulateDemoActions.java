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
package org.eclipse.osee.ats.client.demo;

import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_2;
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_3;
import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.client.demo.config.DemoDbActionData;
import org.eclipse.osee.ats.client.demo.config.DemoDbActionData.CreateReview;
import org.eclipse.osee.ats.client.demo.config.DemoDbGroups;
import org.eclipse.osee.ats.client.demo.config.DemoDbReviews;
import org.eclipse.osee.ats.client.demo.config.DemoDbTasks;
import org.eclipse.osee.ats.client.demo.config.DemoDbUtil;
import org.eclipse.osee.ats.client.demo.internal.Activator;
import org.eclipse.osee.ats.client.demo.internal.AtsClientService;
import org.eclipse.osee.ats.core.client.action.ActionArtifact;
import org.eclipse.osee.ats.core.client.action.ActionArtifactRollup;
import org.eclipse.osee.ats.core.client.config.AtsBulkLoad;
import org.eclipse.osee.ats.core.client.review.AbstractReviewArtifact;
import org.eclipse.osee.ats.core.client.review.ReviewManager;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.util.AtsUtilClient;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.core.workflow.transition.TeamWorkFlowManager;
import org.eclipse.osee.ats.demo.api.DemoArtifactToken;
import org.eclipse.osee.ats.demo.api.DemoArtifactTypes;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.FavoritesManager;
import org.eclipse.osee.ats.util.SubscribeManagerUI;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.importing.parsers.IArtifactExtractor;
import org.eclipse.osee.framework.skynet.core.importing.parsers.WordOutlineExtractor;
import org.eclipse.osee.framework.skynet.core.importing.parsers.WordOutlineExtractorDelegate;
import org.eclipse.osee.framework.skynet.core.importing.resolvers.IArtifactImportResolver;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;
import org.eclipse.osee.framework.skynet.core.utility.DbUtil;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.Import.ArtifactImportOperationFactory;
import org.eclipse.osee.framework.ui.skynet.Import.ArtifactImportOperationParameter;
import org.eclipse.osee.framework.ui.skynet.Import.ArtifactResolverFactory;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.support.test.util.TestUtil;

/**
 * Run from the ATS Navigator after the DB is configured for "OSEE Demo Database", this class will populate the database
 * with sample actions written against XYZ configured teams
 *
 * @author Donald G. Dunne
 */
public class PopulateDemoActions extends XNavigateItemAction {

   private static boolean DEBUG = false;
   private final String[] TITLE_PREFIX = {
      "Problem with the",
      "Can't see the",
      "Button A doesn't work on",
      "Add to the",
      "Make new Button for ",
      "User can't load "};
   private final ChangeType[] CHANGE_TYPE = {
      ChangeType.Problem,
      ChangeType.Problem,
      ChangeType.Problem,
      ChangeType.Improvement,
      ChangeType.Improvement,
      ChangeType.Support,
      ChangeType.Improvement,
      ChangeType.Support};

   private static final String UPDATE_BRANCH_TYPE = "update osee_branch set branch_type = ? where branch_id = ?";

   private static final String ApplicabilityBasicTags =
      "<w:p wsp:rsidR=\"009511DC\" wsp:rsidRDefault=\"009511DC\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr><w:r><w:t>Feature[A=Included]</w:t></w:r></w:p><w:p wsp:rsidR=\"009511DC\" wsp:rsidRDefault=\"009511DC\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr><w:r><w:t>Test that a is included</w:t></w:r></w:p><w:p wsp:rsidR=\"009511DC\" wsp:rsidRDefault=\"009511DC\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr><w:r><w:t>End Feature[A=Included]</w:t></w:r></w:p><w:p wsp:rsidR=\"009511DC\" wsp:rsidRDefault=\"009511DC\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr></w:p><w:p wsp:rsidR=\"009511DC\" wsp:rsidRDefault=\"009511DC\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr><w:r><w:t>feature[c]</w:t></w:r></w:p><w:p wsp:rsidR=\"009511DC\" wsp:rsidRDefault=\"009511DC\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr><w:r><w:t>Test case insensitive &amp; default value</w:t></w:r></w:p><w:p wsp:rsidR=\"009511DC\" wsp:rsidRDefault=\"009511DC\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr><w:r><w:t>End feature</w:t></w:r></w:p><w:p wsp:rsidR=\"009511DC\" wsp:rsidRDefault=\"009511DC\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr></w:p><w:p wsp:rsidR=\"009511DC\" wsp:rsidRDefault=\"009511DC\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr><w:r><w:t>Feature[B=(Choice1| Choice2) | A=Included]</w:t></w:r></w:p><w:p wsp:rsidR=\"009511DC\" wsp:rsidRDefault=\"009511DC\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr><w:r><w:t>Test OR in values and features</w:t></w:r></w:p><w:p wsp:rsidR=\"009511DC\" wsp:rsidRDefault=\"009511DC\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr><w:r><w:t>End Feature</w:t></w:r></w:p><w:p wsp:rsidR=\"009511DC\" wsp:rsidRDefault=\"009511DC\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr></w:p><w:p wsp:rsidR=\"009511DC\" wsp:rsidRDefault=\"009511DC\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr><w:r><w:t>Feature[B=Choice1 &amp; A=Included]</w:t></w:r></w:p><w:p wsp:rsidR=\"009511DC\" wsp:rsidRDefault=\"009511DC\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr><w:r><w:t>Test AND in features</w:t></w:r></w:p><w:p wsp:rsidR=\"009511DC\" wsp:rsidRDefault=\"009511DC\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr><w:r><w:t>End Feature</w:t></w:r></w:p><w:p wsp:rsidR=\"009511DC\" wsp:rsidRDefault=\"009511DC\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr></w:p><w:p wsp:rsidR=\"009511DC\" wsp:rsidRDefault=\"009511DC\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr><w:r><w:t>Feature[A=Excluded]</w:t></w:r></w:p><w:p wsp:rsidR=\"009511DC\" wsp:rsidRDefault=\"009511DC\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr><w:r><w:t>Test before else feature text</w:t></w:r></w:p><w:p wsp:rsidR=\"009511DC\" wsp:rsidRDefault=\"009511DC\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr><w:r><w:t>Feature Else</w:t></w:r></w:p><w:p wsp:rsidR=\"009511DC\" wsp:rsidRDefault=\"009511DC\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr><w:r><w:t>Test after else feature text</w:t></w:r></w:p><w:p wsp:rsidR=\"009511DC\" wsp:rsidRDefault=\"009511DC\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr><w:r><w:t>End Feature</w:t></w:r></w:p><w:p wsp:rsidR=\"009511DC\" wsp:rsidRDefault=\"009511DC\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr></w:p><w:p wsp:rsidR=\"009511DC\" wsp:rsidRDefault=\"009511DC\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr><w:r><w:t>Configuration [Config1]</w:t></w:r></w:p><w:p wsp:rsidR=\"009511DC\" wsp:rsidRDefault=\"009511DC\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr><w:r><w:t>Test configuration</w:t></w:r></w:p><w:p wsp:rsidR=\"009511DC\" wsp:rsidRDefault=\"009511DC\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr><w:r><w:t>End Configuration</w:t></w:r></w:p><w:p wsp:rsidR=\"009511DC\" wsp:rsidRDefault=\"009511DC\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr></w:p><w:p wsp:rsidR=\"009511DC\" wsp:rsidRDefault=\"009511DC\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr><w:r><w:t>Configuration[Config1]</w:t></w:r></w:p><w:p wsp:rsidR=\"009511DC\" wsp:rsidRDefault=\"009511DC\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr><w:r><w:t>Test before else</w:t></w:r></w:p><w:p wsp:rsidR=\"009511DC\" wsp:rsidRDefault=\"009511DC\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr><w:r><w:t>Configuration Else</w:t></w:r></w:p><w:p wsp:rsidR=\"009511DC\" wsp:rsidRDefault=\"009511DC\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr><w:r><w:t>Test after else</w:t></w:r></w:p><w:p wsp:rsidR=\"009511DC\" wsp:rsidRDefault=\"009511DC\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr><w:r><w:t>End Configuration</w:t></w:r></w:p><w:p wsp:rsidR=\"009511DC\" wsp:rsidRDefault=\"009511DC\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr></w:p><w:p wsp:rsidR=\"009511DC\" wsp:rsidRDefault=\"009511DC\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr><w:r><w:t>Configuration[Config1=Excluded]</w:t></w:r></w:p><w:p wsp:rsidR=\"009511DC\" wsp:rsidRDefault=\"009511DC\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr><w:r><w:t>Test excluding config1</w:t></w:r></w:p><w:p wsp:rsidR=\"009511DC\" wsp:rsidRDefault=\"009511DC\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr><w:r><w:t>End Configuration</w:t></w:r></w:p><w:p wsp:rsidR=\"009511DC\" wsp:rsidRDefault=\"009511DC\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr></w:p><w:p wsp:rsidR=\"009511DC\" wsp:rsidRDefault=\"009511DC\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr><w:r><w:t>Configuration[Config1 | Config2]</w:t></w:r></w:p><w:p wsp:rsidR=\"009511DC\" wsp:rsidRDefault=\"009511DC\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr><w:r><w:t>Test OR configurations</w:t></w:r></w:p><w:p wsp:rsidR=\"009511DC\" wsp:rsidRDefault=\"009511DC\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr><w:r><w:t>End Configuration</w:t></w:r></w:p><w:p wsp:rsidR=\"009511DC\" wsp:rsidRDefault=\"009511DC\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr></w:p><w:p wsp:rsidR=\"009511DC\" wsp:rsidRDefault=\"009511DC\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr></w:p><w:p wsp:rsidR=\"009511DC\" wsp:rsidRDefault=\"009511DC\"><w:pPr><w:spacing w:after=\"0\"></w:spacing><w:sectPr wsp:rsidR=\"009511DC\"><w:ftr w:type=\"odd\"><w:p wsp:rsidR=\"00DF6E46\" wsp:rsidRDefault=\"00DF6E46\"><w:pPr><w:pStyle w:val=\"para8pt\"></w:pStyle><w:jc w:val=\"center\"></w:jc></w:pPr><w:r><w:rPr><w:rStyle w:val=\"PageNumber\"></w:rStyle></w:rPr><w:fldChar w:fldCharType=\"begin\"></w:fldChar></w:r><w:r wsp:rsidR=\"00A35FD3\"><w:rPr><w:rStyle w:val=\"PageNumber\"></w:rStyle></w:rPr><w:instrText> PAGE </w:instrText></w:r><w:r><w:rPr><w:rStyle w:val=\"PageNumber\"></w:rStyle></w:rPr><w:fldChar w:fldCharType=\"separate\"></w:fldChar></w:r><w:r wsp:rsidR=\"009511DC\"><w:rPr><w:rStyle w:val=\"PageNumber\"></w:rStyle><w:noProof></w:noProof></w:rPr><w:t>1</w:t></w:r><w:r><w:rPr><w:rStyle w:val=\"PageNumber\"></w:rStyle></w:rPr><w:fldChar w:fldCharType=\"end\"></w:fldChar></w:r></w:p><w:p wsp:rsidR=\"00DF6E46\" wsp:rsidRDefault=\"00A35FD3\"><w:pPr><w:spacing w:after=\"0\" w:before=\"0\" w:line=\"240\" w:line-rule=\"auto\"></w:spacing><w:jc w:val=\"both\"></w:jc><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr></w:pPr><w:r><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr><w:t>UNSPECIFIED - PLEASE TAG WITH CORRECT DATA RIGHTS ATTRIBUTE!!!</w:t></w:r></w:p><w:p wsp:rsidR=\"00DF6E46\" wsp:rsidRDefault=\"00A35FD3\"><w:pPr><w:spacing w:after=\"0\" w:before=\"0\" w:line=\"240\" w:line-rule=\"auto\"></w:spacing><w:jc w:val=\"both\"></w:jc><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr></w:pPr><w:r><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr><w:t>Contract No.: W58RGZ-14-D-0045/T.O. 0016</w:t></w:r></w:p><w:p wsp:rsidR=\"00DF6E46\" wsp:rsidRDefault=\"00A35FD3\"><w:pPr><w:spacing w:after=\"0\" w:before=\"0\" w:line=\"240\" w:line-rule=\"auto\"></w:spacing><w:jc w:val=\"both\"></w:jc><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr></w:pPr><w:r><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr><w:t>Contractor Name: The Boeing Company</w:t></w:r></w:p><w:p wsp:rsidR=\"00DF6E46\" wsp:rsidRDefault=\"00A35FD3\"><w:pPr><w:spacing w:after=\"0\" w:before=\"0\" w:line=\"240\" w:line-rule=\"auto\"></w:spacing><w:jc w:val=\"both\"></w:jc><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr></w:pPr><w:r><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr><w:t>Contractor Address: 5000 E. McDowell Road; Mesa, AZ 85215-9797 </w:t></w:r></w:p><w:p wsp:rsidR=\"00DF6E46\" wsp:rsidRDefault=\"00DF6E46\"><w:pPr><w:spacing w:after=\"0\" w:before=\"0\" w:line=\"240\" w:line-rule=\"auto\"></w:spacing><w:jc w:val=\"both\"></w:jc><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr></w:pPr></w:p><w:p wsp:rsidR=\"00DF6E46\" wsp:rsidRDefault=\"00DF6E46\"><w:pPr><w:spacing w:after=\"0\" w:before=\"0\" w:line=\"240\" w:line-rule=\"auto\"></w:spacing><w:jc w:val=\"both\"></w:jc><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr></w:pPr></w:p><w:p wsp:rsidR=\"00DF6E46\" wsp:rsidRDefault=\"00A35FD3\"><w:pPr><w:spacing w:after=\"0\" w:before=\"0\" w:line=\"240\" w:line-rule=\"auto\"></w:spacing><w:jc w:val=\"both\"></w:jc><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr></w:pPr><w:r><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr><w:t>The Government's rights to use, modify, reproduce,</w:t></w:r><w:r><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr><w:t> release, perform, display, or disclose this software are restricted by paragraph (b)(3) of the Rights in Noncommercial Computer Software and Noncommercial Computer Software Documentation clause contained in the above identified contract.  Any reproduction</w:t></w:r><w:r><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr><w:t> of computer software or portions thereof marked with this legend must also reproduce the markings.  Any person, other than the Government, who has been provided access to such software must promptly notify the above named Contractor. </w:t></w:r></w:p><w:p wsp:rsidR=\"00DF6E46\" wsp:rsidRDefault=\"00DF6E46\"><w:pPr><w:spacing w:after=\"0\" w:before=\"0\" w:line=\"240\" w:line-rule=\"auto\"></w:spacing><w:jc w:val=\"both\"></w:jc><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr></w:pPr></w:p><w:p wsp:rsidR=\"00DF6E46\" wsp:rsidRDefault=\"00A35FD3\"><w:pPr><w:spacing w:after=\"0\" w:before=\"0\" w:line=\"240\" w:line-rule=\"auto\"></w:spacing><w:jc w:val=\"both\"></w:jc><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr></w:pPr><w:r><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr><w:t>Copyright (c) 2017 </w:t></w:r><w:r><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr><w:t>– The Boeing Company</w:t></w:r></w:p></w:ftr><w:pgSz w:h=\"15840\" w:w=\"12240\"></w:pgSz><w:pgMar w:bottom=\"1440\" w:footer=\"432\" w:gutter=\"0\" w:header=\"432\" w:left=\"1440\" w:right=\"1440\" w:top=\"1440\"></w:pgMar><w:cols w:space=\"720\"></w:cols></w:sectPr></w:pPr></w:p>";
   private static final String ApplicabilityEmbeddedTagsCase =
      "<w:p wsp:rsidR=\"009511DC\" wsp:rsidRDefault=\"00E32D10\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr><w:r><w:t>Feature[A=Included]</w:t></w:r></w:p><w:p wsp:rsidR=\"009511DC\" wsp:rsidRDefault=\"00E9626A\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr><w:r><w:t>Test text before embedded feature</w:t></w:r></w:p><w:p wsp:rsidP=\"00E9626A\" wsp:rsidR=\"00E9626A\" wsp:rsidRDefault=\"00E9626A\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr><w:r><w:t>feature[c]</w:t></w:r></w:p><w:p wsp:rsidP=\"00E9626A\" wsp:rsidR=\"00E9626A\" wsp:rsidRDefault=\"00E9626A\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr><w:r><w:t>Test embedded features</w:t></w:r></w:p><w:p wsp:rsidP=\"00E9626A\" wsp:rsidR=\"00E9626A\" wsp:rsidRDefault=\"00E9626A\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr><w:r><w:t>End feature</w:t></w:r><w:r><w:t>[c]</w:t></w:r></w:p><w:p wsp:rsidR=\"00E9626A\" wsp:rsidRDefault=\"00E9626A\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr><w:r><w:t>Test text after embedded feature</w:t></w:r></w:p><w:p wsp:rsidR=\"009511DC\" wsp:rsidRDefault=\"00E9626A\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr><w:r><w:t>End Feature</w:t></w:r></w:p><w:p wsp:rsidR=\"00E9626A\" wsp:rsidRDefault=\"00E9626A\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr></w:p><w:p wsp:rsidR=\"009511DC\" wsp:rsidRDefault=\"00E32D10\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr><w:r><w:t>Configuration [Config1]</w:t></w:r></w:p><w:p wsp:rsidR=\"009511DC\" wsp:rsidRDefault=\"00E32D10\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr><w:r><w:t>Test configuration</w:t></w:r></w:p><w:p wsp:rsidR=\"00E9626A\" wsp:rsidRDefault=\"00E9626A\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr><w:r><w:t>Configuration[Config1]</w:t></w:r></w:p><w:p wsp:rsidR=\"00E9626A\" wsp:rsidRDefault=\"00E9626A\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr><w:r><w:t>Test embedded configuration</w:t></w:r></w:p><w:p wsp:rsidR=\"00E9626A\" wsp:rsidRDefault=\"00E9626A\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr><w:r><w:t>End Configuration</w:t></w:r></w:p><w:p wsp:rsidR=\"009511DC\" wsp:rsidRDefault=\"00E32D10\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr><w:r><w:t>End Configuration</w:t></w:r><w:r wsp:rsidR=\"00E9626A\"><w:t>[config1]</w:t></w:r></w:p><w:p wsp:rsidR=\"00AC3EB1\" wsp:rsidRDefault=\"00AC3EB1\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr></w:p><w:p wsp:rsidR=\"00AC3EB1\" wsp:rsidRDefault=\"00AC3EB1\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr></w:p><w:p wsp:rsidP=\"00AC3EB1\" wsp:rsidR=\"00AC3EB1\" wsp:rsidRDefault=\"00AC3EB1\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr><w:r><w:t>Feature[A=Included]</w:t></w:r></w:p><w:p wsp:rsidP=\"00AC3EB1\" wsp:rsidR=\"00AC3EB1\" wsp:rsidRDefault=\"00AC3EB1\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr><w:r><w:t>Test text before embedded feature</w:t></w:r></w:p><w:p wsp:rsidP=\"00AC3EB1\" wsp:rsidR=\"00AC3EB1\" wsp:rsidRDefault=\"00AC3EB1\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr><w:r><w:t>feature[c]</w:t></w:r></w:p><w:p wsp:rsidP=\"00AC3EB1\" wsp:rsidR=\"00AC3EB1\" wsp:rsidRDefault=\"00AC3EB1\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr><w:r><w:t>Test embedded features</w:t></w:r></w:p><w:p wsp:rsidP=\"00AC3EB1\" wsp:rsidR=\"00AC3EB1\" wsp:rsidRDefault=\"00AC3EB1\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr><w:r><w:t>End feature[c]</w:t></w:r></w:p><w:p wsp:rsidP=\"00AC3EB1\" wsp:rsidR=\"00AC3EB1\" wsp:rsidRDefault=\"00AC3EB1\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr><w:r><w:t>Test text after embedded feature</w:t></w:r></w:p><w:p wsp:rsidP=\"00AC3EB1\" wsp:rsidR=\"00AC3EB1\" wsp:rsidRDefault=\"00AC3EB1\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr><w:r><w:t>Feature Else</w:t></w:r></w:p><w:p wsp:rsidP=\"00AC3EB1\" wsp:rsidR=\"00AC3EB1\" wsp:rsidRDefault=\"00AC3EB1\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr><w:r><w:t>Test text </w:t></w:r><w:r><w:t>inside else statement</w:t></w:r><w:r wsp:rsidR=\"00DD4E5E\"><w:t> with embedded feature</w:t></w:r></w:p><w:p wsp:rsidP=\"00AC3EB1\" wsp:rsidR=\"00AC3EB1\" wsp:rsidRDefault=\"00AC3EB1\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr><w:r><w:t>End Feature</w:t></w:r></w:p><w:p wsp:rsidR=\"00AC3EB1\" wsp:rsidRDefault=\"00AC3EB1\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr></w:p><w:p wsp:rsidR=\"009511DC\" wsp:rsidRDefault=\"00E32D10\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr></w:p><w:p wsp:rsidR=\"00146F38\" wsp:rsidRDefault=\"00146F38\"><w:pPr><w:spacing w:after=\"0\"></w:spacing><w:sectPr wsp:rsidR=\"00146F38\"><w:ftr w:type=\"odd\"><w:p wsp:rsidR=\"00146F38\" wsp:rsidRDefault=\"00146F38\"><w:pPr><w:pStyle w:val=\"para8pt\"></w:pStyle><w:jc w:val=\"center\"></w:jc></w:pPr><w:r><w:rPr><w:rStyle w:val=\"PageNumber\"></w:rStyle></w:rPr><w:fldChar w:fldCharType=\"begin\"></w:fldChar></w:r><w:r wsp:rsidR=\"00E32D10\"><w:rPr><w:rStyle w:val=\"PageNumber\"></w:rStyle></w:rPr><w:instrText> PAGE </w:instrText></w:r><w:r><w:rPr><w:rStyle w:val=\"PageNumber\"></w:rStyle></w:rPr><w:fldChar w:fldCharType=\"separate\"></w:fldChar></w:r><w:r wsp:rsidR=\"00DD4E5E\"><w:rPr><w:rStyle w:val=\"PageNumber\"></w:rStyle><w:noProof></w:noProof></w:rPr><w:t>2</w:t></w:r><w:r><w:rPr><w:rStyle w:val=\"PageNumber\"></w:rStyle></w:rPr><w:fldChar w:fldCharType=\"end\"></w:fldChar></w:r></w:p><w:p wsp:rsidR=\"00146F38\" wsp:rsidRDefault=\"00E32D10\"><w:pPr><w:spacing w:after=\"0\" w:before=\"0\" w:line=\"240\" w:line-rule=\"auto\"></w:spacing><w:jc w:val=\"both\"></w:jc><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr></w:pPr><w:r><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr><w:t>UNSPECIFIED - PLEASE TAG WITH CORRECT DATA RIGHTS ATTRIBUTE!!!</w:t></w:r></w:p><w:p wsp:rsidR=\"00146F38\" wsp:rsidRDefault=\"00E32D10\"><w:pPr><w:spacing w:after=\"0\" w:before=\"0\" w:line=\"240\" w:line-rule=\"auto\"></w:spacing><w:jc w:val=\"both\"></w:jc><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr></w:pPr><w:r><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr><w:t>Contract No.: W58RGZ-14-D-0045/T.O. 0016</w:t></w:r></w:p><w:p wsp:rsidR=\"00146F38\" wsp:rsidRDefault=\"00E32D10\"><w:pPr><w:spacing w:after=\"0\" w:before=\"0\" w:line=\"240\" w:line-rule=\"auto\"></w:spacing><w:jc w:val=\"both\"></w:jc><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr></w:pPr><w:r><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr><w:t>Contractor Name: The Boeing Company</w:t></w:r></w:p><w:p wsp:rsidR=\"00146F38\" wsp:rsidRDefault=\"00E32D10\"><w:pPr><w:spacing w:after=\"0\" w:before=\"0\" w:line=\"240\" w:line-rule=\"auto\"></w:spacing><w:jc w:val=\"both\"></w:jc><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr></w:pPr><w:r><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr><w:t>Contractor Address: 5000 E. M</w:t></w:r><w:r><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr><w:t>cDowell Road; Mesa, AZ 85215-9797 </w:t></w:r></w:p><w:p wsp:rsidR=\"00146F38\" wsp:rsidRDefault=\"00146F38\"><w:pPr><w:spacing w:after=\"0\" w:before=\"0\" w:line=\"240\" w:line-rule=\"auto\"></w:spacing><w:jc w:val=\"both\"></w:jc><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr></w:pPr></w:p><w:p wsp:rsidR=\"00146F38\" wsp:rsidRDefault=\"00146F38\"><w:pPr><w:spacing w:after=\"0\" w:before=\"0\" w:line=\"240\" w:line-rule=\"auto\"></w:spacing><w:jc w:val=\"both\"></w:jc><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr></w:pPr></w:p><w:p wsp:rsidR=\"00146F38\" wsp:rsidRDefault=\"00E32D10\"><w:pPr><w:spacing w:after=\"0\" w:before=\"0\" w:line=\"240\" w:line-rule=\"auto\"></w:spacing><w:jc w:val=\"both\"></w:jc><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr></w:pPr><w:r><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr><w:t>The Government's rights to use, modify, reproduce, release, perform, display, or disclose this software are restricted by paragraph (b)(3) of the Rights in Noncommercial Computer Software and Noncommercial Computer Soft</w:t></w:r><w:r><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr><w:t>ware Documentation clause contained in the above identified contract.  Any reproduction of computer software or portions thereof marked with this legend must also reproduce the markings.  Any person, other than the Government, who has been provided access </w:t></w:r><w:r><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr><w:t>to such software must promptly notify the above named Contractor. </w:t></w:r></w:p><w:p wsp:rsidR=\"00146F38\" wsp:rsidRDefault=\"00146F38\"><w:pPr><w:spacing w:after=\"0\" w:before=\"0\" w:line=\"240\" w:line-rule=\"auto\"></w:spacing><w:jc w:val=\"both\"></w:jc><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr></w:pPr></w:p><w:p wsp:rsidR=\"00146F38\" wsp:rsidRDefault=\"00E32D10\"><w:pPr><w:spacing w:after=\"0\" w:before=\"0\" w:line=\"240\" w:line-rule=\"auto\"></w:spacing><w:jc w:val=\"both\"></w:jc><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr></w:pPr><w:r><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr><w:t>Copyright (c) 2017 – The Boeing Company</w:t></w:r></w:p></w:ftr><w:pgSz w:h=\"15840\" w:w=\"12240\"></w:pgSz><w:pgMar w:bottom=\"1440\" w:footer=\"432\" w:gutter=\"0\" w:header=\"432\" w:left=\"1440\" w:right=\"1440\" w:top=\"1440\"></w:pgMar><w:cols w:space=\"720\"></w:cols></w:sectPr></w:pPr></w:p>";
   private static final String ApplicabilityTable = "<w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"></w:p><w:tbl><w:tblPr><w:tblW w:type=\"auto\" w:w=\"0\"></w:tblW><w:tblBorders><w:top w:color=\"auto\" w:space=\"0\" w:sz=\"4\" w:val=\"single\" wx:bdrwidth=\"10\"></w:top><w:left w:color=\"auto\" w:space=\"0\" w:sz=\"4\" w:val=\"single\" wx:bdrwidth=\"10\"></w:left><w:bottom w:color=\"auto\" w:space=\"0\" w:sz=\"4\" w:val=\"single\" wx:bdrwidth=\"10\"></w:bottom><w:right w:color=\"auto\" w:space=\"0\" w:sz=\"4\" w:val=\"single\" wx:bdrwidth=\"10\"></w:right><w:insideH w:color=\"auto\" w:space=\"0\" w:sz=\"4\" w:val=\"single\" wx:bdrwidth=\"10\"></w:insideH><w:insideV w:color=\"auto\" w:space=\"0\" w:sz=\"4\" w:val=\"single\" wx:bdrwidth=\"10\"></w:insideV></w:tblBorders><w:tblLook w:val=\"04A0\"></w:tblLook></w:tblPr><w:tblGrid><w:gridCol w:w=\"2461\"></w:gridCol><w:gridCol w:w=\"822\"></w:gridCol><w:gridCol w:w=\"822\"></w:gridCol><w:gridCol w:w=\"823\"></w:gridCol><w:gridCol w:w=\"823\"></w:gridCol><w:gridCol w:w=\"823\"></w:gridCol><w:gridCol w:w=\"823\"></w:gridCol><w:gridCol w:w=\"2179\"></w:gridCol></w:tblGrid><w:tr wsp:rsidR=\"00183C52\" wsp:rsidTr=\"00183C52\"><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>Feature[a] a1</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>A2</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>A3</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>A4</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>A5</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>A6</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>A7</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>A8 End Feature[a]</w:t></w:r></w:p></w:tc></w:tr><w:tr wsp:rsidR=\"00183C52\" wsp:rsidTr=\"00183C52\"><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>B1</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>B2</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>B3</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>B4</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>B5</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>B6</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>B7</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>B8</w:t></w:r></w:p></w:tc></w:tr><w:tr wsp:rsidR=\"00183C52\" wsp:rsidTr=\"00183C52\"><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>Feature[B=Choice1]C1</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>C2</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>C3</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>C4</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>C5</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>C6</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>C7</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>C8 </w:t></w:r></w:p></w:tc></w:tr><w:tr wsp:rsidR=\"00183C52\" wsp:rsidTr=\"00183C52\"><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>Configuration[Config1] D1</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>D2</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>D3</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>D4</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>D5</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>D6</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>D7</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>D8 End Configuration</w:t></w:r></w:p></w:tc></w:tr><w:tr wsp:rsidR=\"00183C52\" wsp:rsidTr=\"00183C52\"><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>E8</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>E2</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>E3</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>E4</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>E5</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>E6</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>E7</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>E8</w:t></w:r></w:p></w:tc></w:tr><w:tr wsp:rsidR=\"00183C52\" wsp:rsidTr=\"00183C52\"><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>F1</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>F2</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>F3</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>F4</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>F5</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>F6</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>F7</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>F8 End Feature[B=Choice1]</w:t></w:r></w:p></w:tc></w:tr><w:tr wsp:rsidR=\"00183C52\" wsp:rsidTr=\"00183C52\"><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>G1</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>G2</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>G3</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>G4</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>G5</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>G6</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>G7</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00C620A4\"><w:r><w:t>G8</w:t></w:r></w:p></w:tc></w:tr></w:tbl><w:p wsp:rsidR=\"00AC3EB1\" wsp:rsidRDefault=\"00183C52\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr></w:p><w:p wsp:rsidR=\"009511DC\" wsp:rsidRDefault=\"00183C52\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr></w:p><w:p wsp:rsidR=\"007B2CA7\" wsp:rsidRDefault=\"007B2CA7\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr></w:p><w:tbl><w:tblPr><w:tblW w:type=\"auto\" w:w=\"0\"></w:tblW><w:tblBorders><w:top w:color=\"auto\" w:space=\"0\" w:sz=\"4\" w:val=\"single\" wx:bdrwidth=\"10\"></w:top><w:left w:color=\"auto\" w:space=\"0\" w:sz=\"4\" w:val=\"single\" wx:bdrwidth=\"10\"></w:left><w:bottom w:color=\"auto\" w:space=\"0\" w:sz=\"4\" w:val=\"single\" wx:bdrwidth=\"10\"></w:bottom><w:right w:color=\"auto\" w:space=\"0\" w:sz=\"4\" w:val=\"single\" wx:bdrwidth=\"10\"></w:right><w:insideH w:color=\"auto\" w:space=\"0\" w:sz=\"4\" w:val=\"single\" wx:bdrwidth=\"10\"></w:insideH><w:insideV w:color=\"auto\" w:space=\"0\" w:sz=\"4\" w:val=\"single\" wx:bdrwidth=\"10\"></w:insideV></w:tblBorders><w:tblLook w:val=\"04A0\"></w:tblLook></w:tblPr><w:tblGrid><w:gridCol w:w=\"2461\"></w:gridCol><w:gridCol w:w=\"822\"></w:gridCol><w:gridCol w:w=\"822\"></w:gridCol><w:gridCol w:w=\"823\"></w:gridCol><w:gridCol w:w=\"823\"></w:gridCol><w:gridCol w:w=\"823\"></w:gridCol><w:gridCol w:w=\"823\"></w:gridCol><w:gridCol w:w=\"2179\"></w:gridCol></w:tblGrid><w:tr wsp:rsidR=\"00183C52\" wsp:rsidTr=\"00183C52\"><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>Feature[a] a1</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>A2</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>A3</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>A4</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>A5</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>A6</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>A7</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>A8 End Feature[a]</w:t></w:r></w:p></w:tc></w:tr><w:tr wsp:rsidR=\"00183C52\" wsp:rsidTr=\"00183C52\"><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>B1</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>B2</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>B3</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>B4</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>B5</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>B6</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>B7</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>B8</w:t></w:r></w:p></w:tc></w:tr><w:tr wsp:rsidR=\"00183C52\" wsp:rsidTr=\"00183C52\"><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>Feature[B=Choice1]C1</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>C2</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>C3</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>C4</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>C5</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>C6</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>C7</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>C8 </w:t></w:r></w:p></w:tc></w:tr><w:tr wsp:rsidR=\"00183C52\" wsp:rsidTr=\"00183C52\"><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>D1</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>D2</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>D3</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>D4</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>D5</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>D6</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>D7</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"006F3C1E\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>D8 </w:t></w:r></w:p></w:tc></w:tr><w:tr wsp:rsidR=\"00183C52\" wsp:rsidTr=\"00183C52\"><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>Feature Else </w:t></w:r><w:r><w:t>E8</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>E2</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>E3</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>E4</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>E5</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>E6</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>E7</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>E8</w:t></w:r></w:p></w:tc></w:tr><w:tr wsp:rsidR=\"00183C52\" wsp:rsidTr=\"00183C52\"><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>F1</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>F2</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>F3</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>F4</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>F5</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>F6</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>F7</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>F8 End Feature[B=Choice1]</w:t></w:r></w:p></w:tc></w:tr><w:tr wsp:rsidR=\"00183C52\" wsp:rsidTr=\"00183C52\"><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>G1</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>G2</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>G3</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>G4</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>G5</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>G6</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>G7</w:t></w:r></w:p></w:tc><w:tc><w:tcPr><w:tcW w:type=\"dxa\" w:w=\"1197\"></w:tcW><w:shd w:color=\"auto\" w:fill=\"auto\" w:val=\"clear\"></w:shd></w:tcPr><w:p wsp:rsidP=\"009D4255\" wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:r><w:t>G8</w:t></w:r></w:p></w:tc></w:tr></w:tbl><w:p wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr></w:p><w:p wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"006F3C1E\"><w:pPr><w:spacing w:after=\"0\"></w:spacing><w:sectPr wsp:rsidR=\"006F3C1E\"><w:ftr w:type=\"odd\"><w:p wsp:rsidR=\"007B2CA7\" wsp:rsidRDefault=\"007B2CA7\"><w:pPr><w:pStyle w:val=\"para8pt\"></w:pStyle><w:jc w:val=\"center\"></w:jc></w:pPr><w:r><w:rPr><w:rStyle w:val=\"PageNumber\"></w:rStyle></w:rPr><w:fldChar w:fldCharType=\"begin\"></w:fldChar></w:r><w:r wsp:rsidR=\"00183C52\"><w:rPr><w:rStyle w:val=\"PageNumber\"></w:rStyle></w:rPr><w:instrText> PAGE </w:instrText></w:r><w:r><w:rPr><w:rStyle w:val=\"PageNumber\"></w:rStyle></w:rPr><w:fldChar w:fldCharType=\"separate\"></w:fldChar></w:r><w:r wsp:rsidR=\"006F3C1E\"><w:rPr><w:rStyle w:val=\"PageNumber\"></w:rStyle><w:noProof></w:noProof></w:rPr><w:t>1</w:t></w:r><w:r><w:rPr><w:rStyle w:val=\"PageNumber\"></w:rStyle></w:rPr><w:fldChar w:fldCharType=\"end\"></w:fldChar></w:r></w:p><w:p wsp:rsidR=\"007B2CA7\" wsp:rsidRDefault=\"00183C52\"><w:pPr><w:spacing w:after=\"0\" w:before=\"0\" w:line=\"240\" w:line-rule=\"auto\"></w:spacing><w:jc w:val=\"both\"></w:jc><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr></w:pPr><w:r><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr><w:t>UNSPECIFIED - PLEASE TAG WITH CORRECT DATA RIGHTS ATTRIBUTE!!!</w:t></w:r></w:p><w:p wsp:rsidR=\"007B2CA7\" wsp:rsidRDefault=\"00183C52\"><w:pPr><w:spacing w:after=\"0\" w:before=\"0\" w:line=\"240\" w:line-rule=\"auto\"></w:spacing><w:jc w:val=\"both\"></w:jc><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr></w:pPr><w:r><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr><w:t>Contract No.: W58RGZ-14-D-0045/T.O. 0016</w:t></w:r></w:p><w:p wsp:rsidR=\"007B2CA7\" wsp:rsidRDefault=\"00183C52\"><w:pPr><w:spacing w:after=\"0\" w:before=\"0\" w:line=\"240\" w:line-rule=\"auto\"></w:spacing><w:jc w:val=\"both\"></w:jc><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr></w:pPr><w:r><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr><w:t>Contractor Name: The Boeing Company</w:t></w:r></w:p><w:p wsp:rsidR=\"007B2CA7\" wsp:rsidRDefault=\"00183C52\"><w:pPr><w:spacing w:after=\"0\" w:before=\"0\" w:line=\"240\" w:line-rule=\"auto\"></w:spacing><w:jc w:val=\"both\"></w:jc><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr></w:pPr><w:r><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr><w:t>Contractor Address: 5000 E. McDowell Road; Mesa, AZ 85215-9797 </w:t></w:r></w:p><w:p wsp:rsidR=\"007B2CA7\" wsp:rsidRDefault=\"007B2CA7\"><w:pPr><w:spacing w:after=\"0\" w:before=\"0\" w:line=\"240\" w:line-rule=\"auto\"></w:spacing><w:jc w:val=\"both\"></w:jc><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr></w:pPr></w:p><w:p wsp:rsidR=\"007B2CA7\" wsp:rsidRDefault=\"007B2CA7\"><w:pPr><w:spacing w:after=\"0\" w:before=\"0\" w:line=\"240\" w:line-rule=\"auto\"></w:spacing><w:jc w:val=\"both\"></w:jc><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr></w:pPr></w:p><w:p wsp:rsidR=\"007B2CA7\" wsp:rsidRDefault=\"00183C52\"><w:pPr><w:spacing w:after=\"0\" w:before=\"0\" w:line=\"240\" w:line-rule=\"auto\"></w:spacing><w:jc w:val=\"both\"></w:jc><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr></w:pPr><w:r><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr><w:t>The Government's rights to use, modify, reproduce, release, perform, display, or disclose this software are restricted by paragraph (b)(3) of the Rights in Noncommercial Computer Software an</w:t></w:r><w:r><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr><w:t>d Noncommercial Computer Software Documentation clause contained in the above identified contract.  Any reproduction of computer software or portions thereof marked with this legend must also reproduce the markings.  Any person, other than the Government, </w:t></w:r><w:r><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr><w:t>who has been provided access to such software must promptly notify the above named Contractor. </w:t></w:r></w:p><w:p wsp:rsidR=\"007B2CA7\" wsp:rsidRDefault=\"007B2CA7\"><w:pPr><w:spacing w:after=\"0\" w:before=\"0\" w:line=\"240\" w:line-rule=\"auto\"></w:spacing><w:jc w:val=\"both\"></w:jc><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr></w:pPr></w:p><w:p wsp:rsidR=\"007B2CA7\" wsp:rsidRDefault=\"00183C52\"><w:pPr><w:spacing w:after=\"0\" w:before=\"0\" w:line=\"240\" w:line-rule=\"auto\"></w:spacing><w:jc w:val=\"both\"></w:jc><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr></w:pPr><w:r><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr><w:t>Copyright (c) 2017 – The Boeing Company</w:t></w:r></w:p></w:ftr><w:pgSz w:h=\"15840\" w:w=\"12240\"></w:pgSz><w:pgMar w:bottom=\"1440\" w:footer=\"432\" w:gutter=\"0\" w:header=\"432\" w:left=\"1440\" w:right=\"1440\" w:top=\"1440\"></w:pgMar><w:cols w:space=\"720\"></w:cols></w:sectPr></w:pPr></w:p>";
   private static final String ApplicabilityLists = "<w:p wsp:rsidP=\"001A3BB8\" wsp:rsidR=\"00C620A4\" wsp:rsidRDefault=\"00983D8A\"></w:p><w:p wsp:rsidP=\"009B239B\" wsp:rsidR=\"00AC3EB1\" wsp:rsidRDefault=\"009B239B\"><w:pPr><w:pStyle w:val=\"bulletlvl1\"></w:pStyle><w:listPr><wx:t wx:val=\"·\"></wx:t><wx:font wx:val=\"Symbol\"></wx:font></w:listPr></w:pPr><w:r><w:t>Feature[a] Test 1 End Feature[A]</w:t></w:r></w:p><w:p wsp:rsidP=\"009B239B\" wsp:rsidR=\"009B239B\" wsp:rsidRDefault=\"009B239B\"><w:pPr><w:pStyle w:val=\"bulletlvl1\"></w:pStyle><w:listPr><wx:t wx:val=\"·\"></wx:t><wx:font wx:val=\"Symbol\"></wx:font></w:listPr></w:pPr><w:r><w:t>Feature[B=Choice1]Test 2</w:t></w:r></w:p><w:p wsp:rsidP=\"009B239B\" wsp:rsidR=\"009B239B\" wsp:rsidRDefault=\"009B239B\"><w:pPr><w:pStyle w:val=\"bulletlvl1\"></w:pStyle><w:listPr><wx:t wx:val=\"·\"></wx:t><wx:font wx:val=\"Symbol\"></wx:font></w:listPr></w:pPr><w:r><w:t>Test 3 Feature Else</w:t></w:r></w:p><w:p wsp:rsidP=\"009B239B\" wsp:rsidR=\"009B239B\" wsp:rsidRDefault=\"009B239B\"><w:pPr><w:pStyle w:val=\"bulletlvl1\"></w:pStyle><w:listPr><wx:t wx:val=\"·\"></wx:t><wx:font wx:val=\"Symbol\"></wx:font></w:listPr></w:pPr><w:r><w:t>Test 4 End Feature</w:t></w:r></w:p><w:p wsp:rsidP=\"009B239B\" wsp:rsidR=\"009B239B\" wsp:rsidRDefault=\"009B239B\"><w:pPr><w:pStyle w:val=\"bulletlvl1\"></w:pStyle><w:listPr><w:ilvl w:val=\"0\"></w:ilvl><w:ilfo w:val=\"0\"></w:ilfo></w:listPr><w:ind w:left=\"720\"></w:ind></w:pPr></w:p><w:p wsp:rsidP=\"009B239B\" wsp:rsidR=\"009B239B\" wsp:rsidRDefault=\"009B239B\"><w:pPr><w:pStyle w:val=\"bulletlvl1\"></w:pStyle><w:listPr><w:ilvl w:val=\"0\"></w:ilvl><w:ilfo w:val=\"0\"></w:ilfo></w:listPr><w:ind w:left=\"720\"></w:ind></w:pPr></w:p><w:p wsp:rsidP=\"009B239B\" wsp:rsidR=\"009B239B\" wsp:rsidRDefault=\"009B239B\"><w:pPr><w:pStyle w:val=\"bulletlvl1\"></w:pStyle><w:listPr><wx:t wx:val=\"·\"></wx:t><wx:font wx:val=\"Symbol\"></wx:font></w:listPr></w:pPr><w:r><w:t>Feature[C=Included] test embedded lists</w:t></w:r></w:p><w:p wsp:rsidP=\"009B239B\" wsp:rsidR=\"009B239B\" wsp:rsidRDefault=\"009B239B\"><w:pPr><w:pStyle w:val=\"bulletlvl1\"></w:pStyle><w:listPr><wx:t wx:val=\"·\"></wx:t><wx:font wx:val=\"Symbol\"></wx:font></w:listPr></w:pPr><w:r><w:t>Feature[A=Excluded] test inside embedded End Feature</w:t></w:r></w:p><w:p wsp:rsidP=\"009B239B\" wsp:rsidR=\"009B239B\" wsp:rsidRDefault=\"009B239B\"><w:pPr><w:pStyle w:val=\"bulletlvl1\"></w:pStyle><w:listPr><wx:t wx:val=\"·\"></wx:t><wx:font wx:val=\"Symbol\"></wx:font></w:listPr></w:pPr><w:r><w:t>Test last bullet End Feature[C=Included]</w:t></w:r></w:p><w:p wsp:rsidR=\"009511DC\" wsp:rsidRDefault=\"00983D8A\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr></w:p><w:p wsp:rsidR=\"006F3C1E\" wsp:rsidRDefault=\"00983D8A\"><w:pPr><w:spacing w:after=\"0\"></w:spacing></w:pPr></w:p><w:p wsp:rsidR=\"00664D5C\" wsp:rsidRDefault=\"00664D5C\"><w:pPr><w:spacing w:after=\"0\"></w:spacing><w:sectPr wsp:rsidR=\"00664D5C\"><w:ftr w:type=\"odd\"><w:p wsp:rsidR=\"00664D5C\" wsp:rsidRDefault=\"00664D5C\"><w:pPr><w:pStyle w:val=\"para8pt\"></w:pStyle><w:jc w:val=\"center\"></w:jc></w:pPr><w:r><w:rPr><w:rStyle w:val=\"PageNumber\"></w:rStyle></w:rPr><w:fldChar w:fldCharType=\"begin\"></w:fldChar></w:r><w:r wsp:rsidR=\"00983D8A\"><w:rPr><w:rStyle w:val=\"PageNumber\"></w:rStyle></w:rPr><w:instrText> PAGE </w:instrText></w:r><w:r><w:rPr><w:rStyle w:val=\"PageNumber\"></w:rStyle></w:rPr><w:fldChar w:fldCharType=\"separate\"></w:fldChar></w:r><w:r wsp:rsidR=\"009B239B\"><w:rPr><w:rStyle w:val=\"PageNumber\"></w:rStyle><w:noProof></w:noProof></w:rPr><w:t>1</w:t></w:r><w:r><w:rPr><w:rStyle w:val=\"PageNumber\"></w:rStyle></w:rPr><w:fldChar w:fldCharType=\"end\"></w:fldChar></w:r></w:p><w:p wsp:rsidR=\"00664D5C\" wsp:rsidRDefault=\"00983D8A\"><w:pPr><w:spacing w:after=\"0\" w:before=\"0\" w:line=\"240\" w:line-rule=\"auto\"></w:spacing><w:jc w:val=\"both\"></w:jc><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr></w:pPr><w:r><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr><w:t>UNSPECIFIED - PLEASE TAG WITH CORRECT DATA RIGHTS ATTRIBUTE!!!</w:t></w:r></w:p><w:p wsp:rsidR=\"00664D5C\" wsp:rsidRDefault=\"00983D8A\"><w:pPr><w:spacing w:after=\"0\" w:before=\"0\" w:line=\"240\" w:line-rule=\"auto\"></w:spacing><w:jc w:val=\"both\"></w:jc><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr></w:pPr><w:r><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr><w:t>Contract No.: W58RGZ-14-D-0045/T.O. 0016</w:t></w:r></w:p><w:p wsp:rsidR=\"00664D5C\" wsp:rsidRDefault=\"00983D8A\"><w:pPr><w:spacing w:after=\"0\" w:before=\"0\" w:line=\"240\" w:line-rule=\"auto\"></w:spacing><w:jc w:val=\"both\"></w:jc><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr></w:pPr><w:r><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr><w:t>Contractor Name: The Boeing Company</w:t></w:r></w:p><w:p wsp:rsidR=\"00664D5C\" wsp:rsidRDefault=\"00983D8A\"><w:pPr><w:spacing w:after=\"0\" w:before=\"0\" w:line=\"240\" w:line-rule=\"auto\"></w:spacing><w:jc w:val=\"both\"></w:jc><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr></w:pPr><w:r><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr><w:t>Contractor Address: 5000 E. McDowell Road; Mesa, AZ 85215-9797 </w:t></w:r></w:p><w:p wsp:rsidR=\"00664D5C\" wsp:rsidRDefault=\"00664D5C\"><w:pPr><w:spacing w:after=\"0\" w:before=\"0\" w:line=\"240\" w:line-rule=\"auto\"></w:spacing><w:jc w:val=\"both\"></w:jc><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr></w:pPr></w:p><w:p wsp:rsidR=\"00664D5C\" wsp:rsidRDefault=\"00664D5C\"><w:pPr><w:spacing w:after=\"0\" w:before=\"0\" w:line=\"240\" w:line-rule=\"auto\"></w:spacing><w:jc w:val=\"both\"></w:jc><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr></w:pPr></w:p><w:p wsp:rsidR=\"00664D5C\" wsp:rsidRDefault=\"00983D8A\"><w:pPr><w:spacing w:after=\"0\" w:before=\"0\" w:line=\"240\" w:line-rule=\"auto\"></w:spacing><w:jc w:val=\"both\"></w:jc><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr></w:pPr><w:r><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr><w:t>The Government's rights to use, modify, reproduce, release, perform, display, or disclose this software are restricted by paragraph (b)(3) of the Rights i</w:t></w:r><w:r><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr><w:t>n Noncommercial Computer Software and Noncommercial Computer Software Documentation clause contained in the above identified contract.  Any reproduction of computer software or portions thereof marked with this legend must also reproduce the markings.  Any</w:t></w:r><w:r><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr><w:t> person, other than the Government, who has been provided access to such software must promptly notify the above named Contractor. </w:t></w:r></w:p><w:p wsp:rsidR=\"00664D5C\" wsp:rsidRDefault=\"00664D5C\"><w:pPr><w:spacing w:after=\"0\" w:before=\"0\" w:line=\"240\" w:line-rule=\"auto\"></w:spacing><w:jc w:val=\"both\"></w:jc><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr></w:pPr></w:p><w:p wsp:rsidR=\"00664D5C\" wsp:rsidRDefault=\"00983D8A\"><w:pPr><w:spacing w:after=\"0\" w:before=\"0\" w:line=\"240\" w:line-rule=\"auto\"></w:spacing><w:jc w:val=\"both\"></w:jc><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr></w:pPr><w:r><w:rPr><w:rFonts w:cs=\"Arial\"></w:rFonts><w:sz w:val=\"16\"></w:sz></w:rPr><w:t>Copyright (c) 2017 – The Boeing Company</w:t></w:r></w:p></w:ftr><w:pgSz w:h=\"15840\" w:w=\"12240\"></w:pgSz><w:pgMar w:bottom=\"1440\" w:footer=\"432\" w:gutter=\"0\" w:header=\"432\" w:left=\"1440\" w:right=\"1440\" w:top=\"1440\"></w:pgMar><w:cols w:space=\"720\"></w:cols></w:sectPr></w:pPr></w:p>";

   public PopulateDemoActions(XNavigateItem parent) {
      super(parent, "Populate Demo Actions", PluginUiImage.ADMIN);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      run(true);
   }

   private static void validateArtifactCache() throws OseeStateException {
      final Collection<Artifact> list = ArtifactCache.getDirtyArtifacts();
      if (!list.isEmpty()) {
         for (Artifact artifact : list) {
            System.err.println(String.format("Artifact [%s] is dirty [%s]", artifact.toStringWithId(),
               Artifacts.getDirtyReport(artifact)));
         }
         throw new OseeStateException("[%d] Dirty Artifacts found after populate (see console for details)",
            list.size());
      }

   }

   public void run(boolean prompt) throws Exception {
      AtsUtilClient.setEmailEnabled(false);
      if (AtsUtil.isProductionDb()) {
         throw new IllegalStateException("PopulateDemoActions should not be run on production DB");
      }
      if (DbUtil.isDbInit() || !prompt || prompt && MessageDialog.openConfirm(Displays.getActiveShell(), getName(),
         getName())) {

         validateArtifactCache();

         OseeLog.log(Activator.class, Level.INFO, "Populate Demo Database");

         AtsBulkLoad.reloadConfig(true);

         SevereLoggingMonitor monitorLog = TestUtil.severeLoggingStart();

         // Import all requirements on SAW_Bld_1 Branch
         demoDbImportReqsTx();

         //DemoDbUtil.sleep(5000);

         // Create traceability between System, Subsystem and Software requirements
         SkynetTransaction demoDbTraceability =
            TransactionManager.createTransaction(SAW_Bld_1, "Populate Demo DB - Create Traceability");
         demoDbTraceabilityTx(demoDbTraceability, SAW_Bld_1);
         demoDbTraceability.execute();

         //DemoDbUtil.sleep(5000);

         // Create SAW_Bld_2 Child Main Working Branch off SAW_Bld_1
         createMainWorkingBranchTx();

         // Create SWB_Bld_2 Actions and Reviews
         PopulateSawBuild2Actions.run();

         // Create actions against non-requirement AIs and Teams
         createNonReqChangeDemoActions();
         createGenericDemoActions();

         // Mark all CIS Code "Team Workflows" as Favorites for "Joe Smith"
         if (DEBUG) {
            OseeLog.log(Activator.class, Level.INFO, "Add Favorites");
         }
         for (Artifact art : ArtifactQuery.getArtifactListFromTypeAndName(DemoArtifactTypes.DemoCodeTeamWorkflow,
            "Diagram View", AtsClientService.get().getAtsBranch(), QueryOption.CONTAINS_MATCH_OPTIONS)) {
            new FavoritesManager((AbstractWorkflowArtifact) art).toggleFavorite(false);
         }

         // Mark all Tools Team "Team Workflows" as Subscribed for "Joe Smith"
         if (DEBUG) {
            OseeLog.log(Activator.class, Level.INFO, "Add Subscribed");
         }
         for (Artifact art : ArtifactQuery.getArtifactListFromTypeAndName(DemoArtifactTypes.DemoCodeTeamWorkflow,
            "Even", AtsClientService.get().getAtsBranch(), QueryOption.CONTAINS_MATCH_OPTIONS)) {
            new SubscribeManagerUI((AbstractWorkflowArtifact) art).toggleSubscribe(false);
         }

         // Create some tasks off sample workflows
         DemoDbTasks.createTasks(DEBUG);

         // Create group of sample artifacts
         DemoDbGroups.createGroups(DEBUG);

         // Create and transition reviews off sample workflows
         DemoDbReviews.createReviews(DEBUG);

         // Set Default Work Packages
         setDefaultWorkPackages();

         validateArtifactCache();
         TestUtil.severeLoggingEnd(monitorLog);
         OseeLog.log(Activator.class, Level.INFO, "Populate Complete");
      }
   }

   private void setDefaultWorkPackages() {
      SkynetTransaction transaction =
         TransactionManager.createTransaction(AtsClientService.get().getAtsBranch(), "Set Work Packages");

      // set work packages
      TeamWorkFlowArtifact commWf = DemoUtil.getSawCodeCommittedWf();
      commWf.setSoleAttributeValue(AtsAttributeTypes.WorkPackageGuid,
         DemoArtifactToken.SAW_Code_Team_WorkPackage_01.getGuid());
      commWf.persist(transaction);

      TeamWorkFlowArtifact unCommWf = DemoUtil.getSawCodeUnCommittedWf();
      unCommWf.setSoleAttributeValue(AtsAttributeTypes.WorkPackageGuid,
         DemoArtifactToken.SAW_Code_Team_WorkPackage_01.getGuid());
      unCommWf.persist(transaction);

      TeamWorkFlowArtifact noBranchWf = DemoUtil.getSawCodeNoBranchWf();
      noBranchWf.setSoleAttributeValue(AtsAttributeTypes.WorkPackageGuid,
         DemoArtifactToken.SAW_Code_Team_WorkPackage_03.getGuid());
      noBranchWf.persist(transaction);

      transaction.execute();
   }

   private void createMainWorkingBranchTx() {
      try {
         if (DEBUG) {
            OseeLog.log(Activator.class, Level.INFO, "Creating SAW_Bld_2 branch off SAW_Bld_1");
         }
         // Create SAW_Bld_2 branch off SAW_Bld_1
         BranchId childBranch = BranchManager.createBaselineBranch(SAW_Bld_1, SAW_Bld_2);

         AccessControlManager.setPermission(UserManager.getUser(DemoUsers.Joe_Smith), SAW_Bld_2,
            PermissionEnum.FULLACCESS);

         DemoDbUtil.sleep(5000);
         // need to update the branch type;
         ConnectionHandler.runPreparedUpdate(UPDATE_BRANCH_TYPE, BranchType.BASELINE.getValue(), childBranch);
         BranchManager.refreshBranches();
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private void createNonReqChangeDemoActions() throws Exception {
      IAtsChangeSet changes = AtsClientService.get().createChangeSet("Populate Demo DB - Create Actions");
      if (DEBUG) {
         OseeLog.log(Activator.class, Level.INFO, "createNonReqChangeDemoActions - SAW_Bld_3");
      }
      Set<ActionArtifact> actions =
         createActions(DemoDbActionData.getNonReqSawActionData(), DemoArtifactToken.SAW_Bld_3, null, changes);
      appendBuildNameToTitles(actions, SAW_Bld_3.getName(), changes);

      if (DEBUG) {
         OseeLog.log(Activator.class, Level.INFO, "createNonReqChangeDemoActions - SAW_Bld_2");
      }
      actions = createActions(DemoDbActionData.getNonReqSawActionData(), DemoArtifactToken.SAW_Bld_2, null, changes);
      appendBuildNameToTitles(actions, SAW_Bld_2.getName(), changes);

      if (DEBUG) {
         OseeLog.log(Activator.class, Level.INFO, "createNonReqChangeDemoActions - SAW_Bld_1");
      }

      actions = createActions(DemoDbActionData.getNonReqSawActionData(), DemoArtifactToken.SAW_Bld_1,
         TeamState.Completed, changes);
      appendBuildNameToTitles(actions, SAW_Bld_1.toString(), changes);

      changes.execute();
   }

   private void createGenericDemoActions() throws Exception {
      if (DEBUG) {
         OseeLog.log(Activator.class, Level.INFO, "createNonReqChangeDemoActions - getGenericActionData");
      }
      IAtsChangeSet changes = AtsClientService.get().createChangeSet("Populate Demo DB - Create Generic Actions");
      createActions(DemoDbActionData.getGenericActionData(), null, null, changes);
      changes.execute();
   }

   private void appendBuildNameToTitles(Set<ActionArtifact> actions, String buildName, IAtsChangeSet changes) throws OseeCoreException {
      for (ActionArtifact action : actions) {
         for (TeamWorkFlowArtifact team : action.getTeams()) {
            team.setName(team.getName() + " for " + buildName);
            changes.add(team);
         }
         ActionArtifactRollup rollup = new ActionArtifactRollup(action);
         rollup.resetAttributesOffChildren();
         changes.add(action);
      }
   }

   private Set<ActionArtifact> createActions(List<DemoDbActionData> actionDatas, ArtifactToken versionToken, TeamState toStateOverride, IAtsChangeSet changes) throws Exception {
      Set<ActionArtifact> actionArts = new HashSet<>();
      int currNum = 1;
      for (DemoDbActionData aData : actionDatas) {
         if (DEBUG) {
            OseeLog.log(Activator.class, Level.INFO, "Creating " + currNum++ + "/" + actionDatas.size());
         }
         int x = 0;
         Date createdDate = new Date();
         IAtsUser createdBy = AtsClientService.get().getUserService().getCurrentUser();

         for (String prefixTitle : aData.prefixTitles) {
            ActionResult actionResult = AtsClientService.get().getActionFactory().createAction(null,
               prefixTitle + " " + aData.postFixTitle, TITLE_PREFIX[x] + " " + aData.postFixTitle, CHANGE_TYPE[x],
               aData.priority, false, null, aData.getActionableItems(), createdDate, createdBy, null, changes);
            actionArts.add((ActionArtifact) actionResult.getActionArt());
            for (IAtsTeamWorkflow teamWf : AtsClientService.get().getWorkItemService().getTeams(actionResult)) {
               TeamWorkFlowManager dtwm = new TeamWorkFlowManager(teamWf, AtsClientService.get().getServices(),
                  TransitionOption.OverrideAssigneeCheck, TransitionOption.OverrideTransitionValidityCheck);
               // Add validation required flag if Decision review is required
               if (aData.getCreateReviews().length > 0) {
                  for (CreateReview createReview : aData.getCreateReviews()) {
                     if (createReview == CreateReview.Decision) {
                        ((TeamWorkFlowArtifact) teamWf.getStoreObject()).setSoleAttributeValue(
                           AtsAttributeTypes.ValidationRequired, true);
                     }
                  }
               }
               boolean isSwDesign = teamWf.getTeamDefinition().getName().contains("SW Design");
               if (isSwDesign) {
                  // set reviews to non-blocking so can transition to Completed
                  for (AbstractReviewArtifact reviewArt : ReviewManager.getReviews(teamWf)) {
                     reviewArt.setSoleAttributeValue(AtsAttributeTypes.ReviewBlocks, ReviewBlockType.None.name());
                  }
               }

               // Transition to desired state
               Result result = dtwm.transitionTo(toStateOverride != null ? toStateOverride : aData.toState,
                  teamWf.getAssignees().iterator().next(), false, changes);
               if (result.isFalse()) {
                  throw new OseeCoreException("Error transitioning [%s] to state [%s]: [%s]", teamWf.toStringWithId(),
                     aData.toState.getName(), result.getText());
               }
               if (!teamWf.isCompletedOrCancelled()) {
                  // Reset assignees that may have been overwritten during transition
                  teamWf.getStateMgr().setAssignees(teamWf.getTeamDefinition().getLeads());
               }
               if (versionToken != null) {
                  IAtsVersion version = AtsClientService.get().getVersionService().getById(versionToken);
                  AtsClientService.get().getVersionService().setTargetedVersion(teamWf, version, changes);
               }
            }
         }
      }
      return actionArts;
   }

   private void demoDbImportReqsTx() {
      try {
         //@formatter:off
         importRequirements(SAW_Bld_1, CoreArtifactTypes.SoftwareRequirement, "Software Requirements", "support/SAW-SoftwareRequirements.xml");
         importRequirements(SAW_Bld_1, CoreArtifactTypes.SystemRequirementMSWord, "System Requirements", "support/SAW-SystemRequirements.xml");
         importRequirements(SAW_Bld_1, CoreArtifactTypes.SubsystemRequirementMSWord, "Subsystem Requirements", "support/SAW-SubsystemRequirements.xml");
         //@formatter:on
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private void importRequirements(BranchId branch, IArtifactType requirementType, String folderName, String filename) throws Exception {
      if (DEBUG) {
         OseeLog.logf(Activator.class, Level.INFO, "Importing \"%s\" requirements on branch \"%s\"", folderName,
            branch);
      }
      Artifact systemReq = ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.Folder, folderName, branch);

      File file = Activator.getInstance().getPluginFile(filename);
      IArtifactImportResolver artifactResolver = ArtifactResolverFactory.createAlwaysNewArtifacts(requirementType);
      IArtifactExtractor extractor = new WordOutlineExtractor();
      extractor.setDelegate(new WordOutlineExtractorDelegate());

      ArtifactImportOperationParameter importOptions = new ArtifactImportOperationParameter();
      importOptions.setSourceFile(file);
      importOptions.setDestinationArtifact(systemReq);
      importOptions.setExtractor(extractor);
      importOptions.setResolver(artifactResolver);

      IOperation operation = ArtifactImportOperationFactory.completeOperation(importOptions);
      Operations.executeWorkAndCheckStatus(operation);

      // Validate that something was imported
      if (systemReq.getChildren().isEmpty()) {
         throw new IllegalStateException("Artifacts were not imported");
      }
   }

   private void relate(RelationTypeSide relationSide, Artifact artifact, Collection<Artifact> artifacts) throws OseeCoreException {
      for (Artifact otherArtifact : artifacts) {
         artifact.addRelation(relationSide, otherArtifact);
      }
   }

   private void demoDbTraceabilityTx(SkynetTransaction transaction, BranchId branch) {
      try {
         Collection<Artifact> systemArts =
            DemoDbUtil.getArtTypeRequirements(DEBUG, CoreArtifactTypes.SystemRequirementMSWord, "Robot", branch);

         Collection<Artifact> component =
            DemoDbUtil.getArtTypeRequirements(DEBUG, CoreArtifactTypes.Component, "API", branch);
         component.addAll(DemoDbUtil.getArtTypeRequirements(DEBUG, CoreArtifactTypes.Component, "Hardware", branch));
         component.addAll(DemoDbUtil.getArtTypeRequirements(DEBUG, CoreArtifactTypes.Component, "Sensor", branch));

         Collection<Artifact> subSystemArts =
            DemoDbUtil.getArtTypeRequirements(DEBUG, CoreArtifactTypes.SubsystemRequirementMSWord, "Robot", branch);
         subSystemArts.addAll(
            DemoDbUtil.getArtTypeRequirements(DEBUG, CoreArtifactTypes.SubsystemRequirementMSWord, "Video", branch));
         subSystemArts.addAll(DemoDbUtil.getArtTypeRequirements(DEBUG, CoreArtifactTypes.SubsystemRequirementMSWord,
            "Interface", branch));

         Collection<Artifact> softArts =
            DemoDbUtil.getArtTypeRequirements(DEBUG, CoreArtifactTypes.SoftwareRequirement, "Robot", branch);
         softArts.addAll(
            DemoDbUtil.getArtTypeRequirements(DEBUG, CoreArtifactTypes.SoftwareRequirement, "Interface", branch));

         // Relate System to SubSystem to Software Requirements
         for (Artifact systemArt : systemArts) {
            relate(CoreRelationTypes.Requirement_Trace__Lower_Level, systemArt, subSystemArts);
            systemArt.persist(transaction);

            for (Artifact subSystemArt : subSystemArts) {
               relate(CoreRelationTypes.Requirement_Trace__Lower_Level, subSystemArt, softArts);
               subSystemArt.persist(transaction);
            }
         }

         // Relate System, SubSystem and Software Requirements to Componets
         for (Artifact art : systemArts) {
            relate(CoreRelationTypes.Allocation__Component, art, component);
            art.persist(transaction);
         }
         for (Artifact art : subSystemArts) {
            relate(CoreRelationTypes.Allocation__Component, art, component);
            art.persist(transaction);
         }
         for (Artifact art : softArts) {
            relate(CoreRelationTypes.Allocation__Component, art, component);
         }

         // Create Test Script Artifacts
         Set<Artifact> verificationTests = new HashSet<>();
         Artifact verificationHeader =
            ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.Folder, "Verification Tests", branch);
         if (verificationHeader == null) {
            throw new IllegalStateException("Could not find Verification Tests header");
         }
         for (String str : new String[] {"A", "B", "C"}) {
            Artifact newArt = ArtifactTypeManager.addArtifact(CoreArtifactTypes.TestCase,
               verificationHeader.getBranch(), "Verification Test " + str);
            verificationTests.add(newArt);
            verificationHeader.addRelation(CoreRelationTypes.Default_Hierarchical__Child, newArt);
            newArt.persist(transaction);
         }
         Artifact verificationTestsArray[] = verificationTests.toArray(new Artifact[verificationTests.size()]);

         // Create Validation Test Procedure Artifacts
         Set<Artifact> validationTests = new HashSet<>();
         Artifact validationHeader =
            ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.Folder, "Validation Tests", branch);
         if (validationHeader == null) {
            throw new IllegalStateException("Could not find Validation Tests header");
         }
         for (String str : new String[] {"1", "2", "3"}) {
            Artifact newArt = ArtifactTypeManager.addArtifact(CoreArtifactTypes.TestProcedure,
               validationHeader.getBranch(), "Validation Test " + str);
            validationTests.add(newArt);
            validationHeader.addRelation(CoreRelationTypes.Default_Hierarchical__Child, newArt);
            newArt.persist(transaction);
         }
         Artifact validationTestsArray[] = validationTests.toArray(new Artifact[validationTests.size()]);

         // Create Integration Test Procedure Artifacts
         Set<Artifact> integrationTests = new HashSet<>();
         Artifact integrationHeader =
            ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.Folder, "Integration Tests", branch);
         if (integrationHeader == null) {
            throw new IllegalStateException("Could not find integration Tests header");
         }
         for (String str : new String[] {"X", "Y", "Z"}) {
            Artifact newArt = ArtifactTypeManager.addArtifact(CoreArtifactTypes.TestProcedure,
               integrationHeader.getBranch(), "integration Test " + str);
            integrationTests.add(newArt);
            integrationHeader.addRelation(CoreRelationTypes.Default_Hierarchical__Child, newArt);
            newArt.persist(transaction);
         }
         Artifact integrationTestsArray[] = integrationTests.toArray(new Artifact[integrationTests.size()]);

         // Relate Software Artifacts to Tests
         Artifact softReqsArray[] = softArts.toArray(new Artifact[softArts.size()]);
         softReqsArray[0].addRelation(CoreRelationTypes.Validation__Validator, verificationTestsArray[0]);
         softReqsArray[0].addRelation(CoreRelationTypes.Validation__Validator, verificationTestsArray[1]);
         softReqsArray[1].addRelation(CoreRelationTypes.Validation__Validator, verificationTestsArray[0]);
         softReqsArray[1].addRelation(CoreRelationTypes.Validation__Validator, validationTestsArray[1]);
         softReqsArray[2].addRelation(CoreRelationTypes.Validation__Validator, validationTestsArray[0]);
         softReqsArray[2].addRelation(CoreRelationTypes.Validation__Validator, integrationTestsArray[1]);
         softReqsArray[3].addRelation(CoreRelationTypes.Validation__Validator, integrationTestsArray[0]);
         softReqsArray[4].addRelation(CoreRelationTypes.Validation__Validator, integrationTestsArray[2]);
         softReqsArray[5].addRelation(CoreRelationTypes.Validation__Validator, validationTestsArray[2]);

         for (Artifact artifact : softArts) {
            artifact.persist(transaction);
         }

         createApplicabilityArtifacts(transaction, branch);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private void createApplicabilityArtifacts(SkynetTransaction transaction, BranchId branch) {
      Artifact applicabilityFolder =
         ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.Folder, "Applicability Tests", branch);
      if (applicabilityFolder == null) {
         throw new IllegalStateException("Could not find Applicability Tests Folder");
      }

      //create a list of strings for all these tests      
      String[] wordMlValue = new String[] {
         ApplicabilityBasicTags,
         ApplicabilityEmbeddedTagsCase,
         ApplicabilityTable,
         ApplicabilityLists};

      int i = 0;
      for (String str : new String[] {
         "ApplicabilityBasicTags", // OR/AND in features, Multi features/values, matching start/end tags, valid features in tags, default value works, exclude config, else tags
         "ApplicabilityEmbeddedTagsCase",
         "ApplicabilityTable",
         "ApplicabilityLists"}) {
         Artifact newArt = ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirement, branch, str);
         newArt.addAttribute(CoreAttributeTypes.WordTemplateContent, wordMlValue[i]);
         applicabilityFolder.addRelation(CoreRelationTypes.Default_Hierarchical__Child, newArt);
         newArt.persist(transaction);
         i++;
      }
   }

}
