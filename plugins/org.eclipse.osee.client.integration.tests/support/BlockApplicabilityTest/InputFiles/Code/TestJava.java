/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.orcs.core.internal.applicability;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.eclipse.osee.framework.core.applicability.FeatureDefinition;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.FileTypeApplicabilityData;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.grammar.ApplicabilityBlock;
import org.eclipse.osee.framework.core.grammar.ApplicabilityBlock.ApplicabilityType;
import org.eclipse.osee.framework.core.grammar.ApplicabilityGrammarLexer;
import org.eclipse.osee.framework.core.grammar.ApplicabilityGrammarParser;
import org.eclipse.osee.framework.core.util.WordCoreUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Ryan D. Brooks
 */
public class BlockApplicabilityOps {

// Feature[ROBOT_SPEAKER=SPKR_A]
   public static final int beginFeatureCommentMatcherGroup = 1;
// End Feature
   public static final int beginFeatureTagMatcherGroup = 2;
   public static final int endFeatureCommentMatcherGroup = 6;
   public static final int endFeatureTagMatcherGroup = 7;

   private final OrcsApi orcsApi;
   private final Log logger;
   private final ScriptEngine se;
   private final BranchId branch;
   private final ArtifactToken view;
   private final List<FeatureDefinition> featureDefinition;
   private final Map<String, List<String>> viewApplicabilitiesMap;

   private boolean commentNonApplicableBlocks = false;

// This is the constructor of the class
   public BlockApplicabilityOps(OrcsApi orcsApi, Log logger, BranchId branch, ArtifactToken view) {
      this.orcsApi = orcsApi;
      this.logger = logger;
      this.branch = branch;
      this.view = view;
      this.featureDefinition = orcsApi.getApplicabilityOps().getFeatureDefinitionData(branch);
      ScriptEngineManager sem = new ScriptEngineManager();
      se = sem.getEngineByName(SCRIPT_ENGINE_NAME);
      viewApplicabilitiesMap =
         orcsApi.getQueryFactory().applicabilityQuery().getNamedViewApplicabilityMap(this.branch, view);
   }

// Feature[JHU_CONTROLLER]
   public ApplicabilityBlock createApplicabilityBlock(ApplicabilityType applicType, String beginExpression) {
      ApplicabilityBlock beginApplic = new ApplicabilityBlock(applicType);
      beginExpression = beginExpression.replace(" [", "[");
      beginApplic.setApplicabilityExpression(beginExpression);
      return beginApplic;
   }
// End Feature
}