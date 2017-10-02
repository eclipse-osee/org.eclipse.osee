/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.script.dsl.tests;

import static org.junit.Assert.assertEquals;
import com.google.inject.Inject;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import org.eclipse.osee.orcs.script.dsl.OrcsScriptDslInjectorProvider;
import org.eclipse.osee.orcs.script.dsl.OrcsScriptUtil;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScript;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactQueryStatement;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchQueryStatement;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsFollowRelationType;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxQueryStatement;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTimestampCriteria;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsVariableDeclaration;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.ScriptStatement;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.ScriptVersion;
import org.eclipse.xtext.IGrammarAccess;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.junit4.util.ParseHelper;
import org.eclipse.xtext.parser.IParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test Case for {@link OrcsScriptDslSyntaxErrorMessageProvider} and general grammar syntax
 * 
 * @author Roberto E. Escobar
 */
@InjectWith(OrcsScriptDslInjectorProvider.class)
@RunWith(XtextRunner.class)
public class OrcsScriptDslParserTest {

   //@formatter:off
   @Inject private ParseHelper<OrcsScript> parserHelper;
   @Inject private IGrammarAccess grammar;
   @Inject private IParser parser;
   //@formatter:on

   private ParserVerification verify;

   @Before
   public void setup() {
      verify = new ParserVerification(parser, grammar);
   }

   @Test
   public void testScriptVersion() {
      verify.rule(ScriptVersion.class, "script-version 0.12.0;");
      verify.ruleError(ScriptVersion.class, "script version 0.12.0;");
   }

   @Test
   public void testScriptStatement() {
      verify.rule(ScriptStatement.class, "start from tx *;");
      verify.ruleError(ScriptStatement.class, "start from tx");

      verify.rule(ScriptStatement.class, "start from branch *;");
      verify.ruleError(ScriptStatement.class, "start from branch");

      verify.ruleError(ScriptStatement.class, "start from tx * branch *");
      verify.ruleError(ScriptStatement.class, "start from artifacts *");

      verify.rule(ScriptStatement.class, "start from branch * find artifacts *;");
      verify.rule(ScriptStatement.class, "start from tx * find artifacts *;");

      verify.rule(ScriptStatement.class, "start from branch * find artifacts * follow relation type = 435 to side-A;");
      verify.rule(ScriptStatement.class,
         "start from branch 570 find artifacts where art-id = 23 follow relation type = 435 to side-B;");
   }

   @Test
   public void testTxQueryStatement() {
      verify.rule(OsTxQueryStatement.class, "tx *");
      verify.ruleError(OsTxQueryStatement.class, "tx * and");

      verify.rule(OsTxQueryStatement.class, "tx 570");
      verify.ruleError(OsTxQueryStatement.class, "tx 570 and");

      verify.rule(OsTxQueryStatement.class, "tx where tx-id = 570");
      verify.rule(OsTxQueryStatement.class, "tx where tx-id = [570, 645]");
      verify.ruleError(OsTxQueryStatement.class, "tx where tx-id != [570, 645]");

      verify.rule(OsTxQueryStatement.class, "tx where tx-id != 570");
      verify.rule(OsTxQueryStatement.class, "tx where tx-id <= 570");
      verify.rule(OsTxQueryStatement.class, "tx where tx-id >= 570");
      verify.rule(OsTxQueryStatement.class, "tx where tx-id > 570");
      verify.rule(OsTxQueryStatement.class, "tx where tx-id < 570");
      verify.rule(OsTxQueryStatement.class, "tx where tx-id in (570 .. 45)");

      verify.rule(OsTxQueryStatement.class, "tx where type = baseline");
      verify.rule(OsTxQueryStatement.class, "tx where type = non-baseline");
      verify.rule(OsTxQueryStatement.class, "tx where type = [baseline, non-baseline]");

      verify.rule(OsTxQueryStatement.class, "tx where comment = \"the comment\"");
      verify.rule(OsTxQueryStatement.class, "tx where comment matches \"th.*?comment\"");

      verify.rule(OsTxQueryStatement.class, "tx where branch-id = 1231");
      verify.rule(OsTxQueryStatement.class, "tx where branch-id = [1231, 123]");
      verify.rule(OsTxQueryStatement.class, "tx where is-head of branch-id 1231");

      verify.rule(OsTxQueryStatement.class, "tx where author-id = 1231");
      verify.rule(OsTxQueryStatement.class, "tx where author-id = [1231, 123]");

      verify.rule(OsTxQueryStatement.class, "tx where commit-id = 1231");
      verify.rule(OsTxQueryStatement.class, "tx where commit-id = [1231, 123]");
      verify.rule(OsTxQueryStatement.class, "tx where commit-id is null");

      verify.rule(OsTxQueryStatement.class, "tx where date = '09/10/2014 11:00:12 AM'");
      verify.rule(OsTxQueryStatement.class, "tx where date != '09/10/2014 11:00:12 AM'");
      verify.rule(OsTxQueryStatement.class, "tx where date >= '09/10/2014 11:00:12 AM'");
      verify.rule(OsTxQueryStatement.class, "tx where date <= '09/10/2014 11:00:12 AM'");
      verify.rule(OsTxQueryStatement.class, "tx where date > '09/10/2014 11:00:12 AM'");
      verify.rule(OsTxQueryStatement.class, "tx where date < '09/10/2014 11:00:12 AM'");
      verify.rule(OsTxQueryStatement.class, "tx where date in ('09/10/2014 11:00:12 AM' .. '09/10/2014 12:00:12 AM')");
      //      verify.ruleError(OsTxQueryStatement.class, "tx where date = '09/10/2014 11:00'");

      String allPredicates = "tx where tx-id = 570" + //
         " and type = [baseline, non-baseline]" + //
         " and comment matches \"122.*?L\"" + //
         " and tx-id != 21123" + //
         " and commit-id is null" + //
         " and author-id = [1231, 123]" + //
         " and date = '09/10/2014 11:00:12 AM'";

      verify.rule(OsTxQueryStatement.class, allPredicates);
   }

   @Test
   public void testBranchQueryStatement() {
      verify.rule(OsBranchQueryStatement.class, "branch *");
      verify.ruleError(OsBranchQueryStatement.class, "branch * and");

      verify.rule(OsBranchQueryStatement.class, "branch 570");
      verify.ruleError(OsBranchQueryStatement.class, "branch 570 and");

      verify.rule(OsBranchQueryStatement.class, "branch where branch-id = 570");
      verify.rule(OsBranchQueryStatement.class, "branch where branch-id = [570, 645]");
      verify.ruleError(OsBranchQueryStatement.class, "branch where branch-id != [570, 645]");

      verify.rule(OsBranchQueryStatement.class, "branch where name = \"my-name\"");
      verify.rule(OsBranchQueryStatement.class, "branch where name matches \"m.*?e\"");

      verify.rule(OsBranchQueryStatement.class, "branch where type = baseline");
      verify.rule(OsBranchQueryStatement.class, "branch where type = [working, baseline, merge, system-root, port]");

      verify.rule(OsBranchQueryStatement.class, "branch where state = created");
      verify.rule(OsBranchQueryStatement.class,
         "branch where state = [" + //
            "created, modified, committed, rebaselined, deleted, " + //
            "rebaseline_in_progress, commit_in_progress, creation_in_progress, " + //
            "delete_in_progress, purge_in_progress, purged" + //
            "]");

      verify.rule(OsBranchQueryStatement.class, "branch where archived is excluded");
      verify.rule(OsBranchQueryStatement.class, "branch where archived is included");

      verify.rule(OsBranchQueryStatement.class, "branch where branch is child-of 567");
      verify.rule(OsBranchQueryStatement.class, "branch where branch is parent-of 567");

      String allPredicates = "branch where branch-id = [570, 645]" + //
         " and name matches \"m.*?e\"" + //
         " and type = [working, merge]" + //
         " and state = [created, committed]" + //
         " and archived is excluded" + //
         " and branch is child-of 567";
      verify.rule(OsBranchQueryStatement.class, allPredicates);
   }

   @Test
   public void testArtifactQueryStatement() {
      verify.rule(OsArtifactQueryStatement.class, "artifacts *");
      verify.ruleError(OsArtifactQueryStatement.class, "artifacts * and");

      verify.rule(OsArtifactQueryStatement.class, "artifacts where art-id = 570");
      verify.rule(OsArtifactQueryStatement.class, "artifacts where art-id = [570, 645]");
      verify.ruleError(OsArtifactQueryStatement.class, "artifacts where art-id != [570, 645]");

      verify.rule(OsArtifactQueryStatement.class, "artifacts where art-guid = 'AJWGM8WWN3fn2JkS5+AA'");
      verify.rule(OsArtifactQueryStatement.class,
         "artifacts where art-guid = ['AJWGM8WWN3fn2JkS5+AA', 'AJWGM8WWN3fn2JkS5+AB']");
      verify.ruleError(OsArtifactQueryStatement.class,
         "artifacts where art-guid != ['AJWGM8WWN3fn2JkS5+AA', 'AJWGM8WWN3fn2JkS5+AB']");

      verify.rule(OsArtifactQueryStatement.class, "artifacts where art-type = 570");
      verify.rule(OsArtifactQueryStatement.class, "artifacts where art-type = [123,2412]");
      verify.ruleError(OsArtifactQueryStatement.class, "artifacts where art-type != 570");

      verify.rule(OsArtifactQueryStatement.class, "artifacts where art-type instance-of 570");

      verify.rule(OsArtifactQueryStatement.class, "artifacts where attribute type = 570 exists");
      verify.rule(OsArtifactQueryStatement.class, "artifacts where attribute type = [570 , 345] exists");
      verify.ruleError(OsArtifactQueryStatement.class, "artifacts where attribute type = 570 not-exists");

      verify.rule(OsArtifactQueryStatement.class, "artifacts where attribute type = 570 contains 'Hello'");
      verify.rule(OsArtifactQueryStatement.class,
         "artifacts where attribute type = [570,324] contains ['hello','bye']");
      verify.rule(OsArtifactQueryStatement.class,
         "artifacts where attribute type = 570 [match-case, any-order] ['two','one']");

      String options = "contains, match-case, ignore-case, not-exists, exists, match-token-count, " + //
         "ignore-token-count, exact-delim, whitespace-delim, any-delim, any-order, match-order";
      verify.rule(OsArtifactQueryStatement.class, "artifacts where attribute type = 570 [" + options + "] 'Hello'");
      verify.rule(OsArtifactQueryStatement.class,
         "artifacts where attribute type = 570 [" + options + "] ['two','one']");

      verify.rule(OsArtifactQueryStatement.class, "artifacts where relation type = 570 exists");
      verify.rule(OsArtifactQueryStatement.class, "artifacts where relation type = 570 not-exists");
      verify.ruleError(OsArtifactQueryStatement.class, "artifacts where relation type = [570, 345] exists");
      verify.ruleError(OsArtifactQueryStatement.class, "artifacts where relation type = [570, 345] not-exists");

      verify.rule(OsArtifactQueryStatement.class, "artifacts where relation type = 570 exists on side-A");
      verify.rule(OsArtifactQueryStatement.class, "artifacts where relation type = 570 not-exists on side-B");

      verify.rule(OsArtifactQueryStatement.class, "artifacts where relation type = 570 on side-A id = 321");
      verify.rule(OsArtifactQueryStatement.class,
         "artifacts where relation type = 345 on side-B id = [321, 2314, 123]");
   }

   @Test
   public void testFollowRelationType() throws Exception {
      verify.rule(OsFollowRelationType.class, "relation type = 570 to side-A");
      verify.rule(OsFollowRelationType.class, "relation type = 570 to side-B");

      verify.ruleError(OsFollowRelationType.class, "relation type = 570");
      verify.ruleError(OsFollowRelationType.class, "relation type = [570, 123] to side-B");
      verify.ruleError(OsFollowRelationType.class, "relation type != 570");
   }

   @Test
   public void testTimeStampParsingAndErrorMessages() throws ParseException {
      // Supports both quotes and single quotes
      verify.rule(OsTxTimestampCriteria.class, "date = '09/10/2014 11:00:12 AM'");
      verify.rule(OsTxTimestampCriteria.class, "date = \"09/10/2014 11:00:12 AM\"");

      verify.rule(OsTxTimestampCriteria.class, "date in ('09/10/2014 11:00:12 AM' .. '09/10/2014 12:00:12 AM')");
      verify.rule(OsTxTimestampCriteria.class, "date in (\"09/10/2014 11:00:12 AM\" .. \"09/10/2014 12:00:12 AM\")");

      //      String syntaxtError = "Invalid timestamp format - format should be [MM/dd/yyyy hh:mm:ss a] or [";
      //      verify.checkRuleErrors(OsTxTimestampCriteria.class, "date = '09/10/2014  AM'", syntaxtError);

      // Use locale specific timestamp format - system should convert correctly
      // For example: MMM d, yyyy h:mm:ss a
      Date date = OrcsScriptUtil.parseDate("09/10/2014 11:00:12 AM");
      String localeFormattedDate = DateFormat.getDateTimeInstance().format(date);
      verify.rule(OsTxTimestampCriteria.class, "date = '" + localeFormattedDate + "'");
   }

   @Test
   public void testCollectClause() {
      verify.rule(OsCollectClause.class, "collect artifacts { * }");

      verify.rule(OsCollectClause.class, "collect artifacts as \"alias-1\" { * }");
      verify.rule(OsCollectClause.class, "collect artifacts as 'alias-2' { * }");

      verify.rule(OsCollectClause.class, "collect artifacts as 'alias-2' { id as 'value'}");
      verify.rule(OsCollectClause.class, "collect artifacts as 'alias-2' { id as 'value', type as 'type'}");

      verify.rule(OsCollectClause.class, "collect artifacts { * } limit 34");

      verify.rule(OsCollectClause.class, "collect artifacts as 'alias-2' { * } limit 34");

      String allFollowOptions = //
         "collect artifacts as 'alias-2' { id as 'value1', type as 'type2', attributes { * } } limit 34";
      verify.rule(OsCollectClause.class, allFollowOptions);

      // Ensure keyword collisions are not a problem
      String allFields =
         "art-id, name, branch-id, archived, state, tx-id, comment, date, author-id, commit-id, id, type, gamma-id, art-id, art-type";
      verify.rule(OsCollectClause.class, "collect artifacts {" + allFields + "} limit 1000");
   }

   @Test
   public void testVariableAssignments() {
      verify.rule(OsVariableDeclaration.class, "var number = 02;");
      verify.rule(OsVariableDeclaration.class, "var number = 0x1230000CF;");
      verify.rule(OsVariableDeclaration.class, "var number = 0x1230000CFL;");
      verify.rule(OsVariableDeclaration.class, "var number = -123;");
      verify.rule(OsVariableDeclaration.class, "var number = -0.1231;");
      verify.rule(OsVariableDeclaration.class, "var number = -1e-10;");
      verify.rule(OsVariableDeclaration.class, "var number = 26.67;");

      verify.rule(OsVariableDeclaration.class, "var template = {{hello.value.xx}};");
      verify.rule(OsVariableDeclaration.class, "var arrayVar = [\"black\", \"blue\", \"black\"];");
      verify.rule(OsVariableDeclaration.class, "var query = start from tx * collect txs { * };");
      verify.rule(OsVariableDeclaration.class, "var var4 = -2, var5 = 1e-10, var6 = 26.67;");
   }

   @Test
   public void testVariableReferences() {
      verify.rule(OsVariableDeclaration.class, "var a = 1231, b = a;");
      verify.rule(OsVariableDeclaration.class, "var number = 1231, arrayVar = [number, \"blue\", \"black\"];");

      verify.rule(OrcsScript.class, "var a = 1231; var b = a;");

      verify.rule(OrcsScript.class,
         "var a = 1; start from tx {{param.tx_id}} find artifacts where art-type = [a, \"type name\", {{param.art_type}}];");
      verify.rule(OrcsScript.class,
         "var a = 1; var var8 = start from tx {{param.value}} find artifacts where art-type = [a, \"type name\", 0x123ABC];");
   }

   @Test
   public void testScriptModel() throws Exception {
      OrcsScript model = parserHelper.parse(
         "script-version 0.12.0; start from tx * find artifacts * start from branch * find artifacts *;");

      String version = model.getVersion().getVersion();
      assertEquals("0.12.0", version);

   }

}
