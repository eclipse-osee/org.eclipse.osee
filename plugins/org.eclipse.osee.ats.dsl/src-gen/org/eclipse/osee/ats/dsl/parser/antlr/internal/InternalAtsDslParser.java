package org.eclipse.osee.ats.dsl.parser.antlr.internal;

import org.antlr.runtime.BaseRecognizer;
import org.antlr.runtime.BitSet;
import org.antlr.runtime.DFA;
import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.osee.ats.dsl.services.AtsDslGrammarAccess;
import org.eclipse.xtext.parser.antlr.AbstractInternalAntlrParser;
import org.eclipse.xtext.parser.antlr.AntlrDatatypeRuleToken;

@SuppressWarnings("all")
public class InternalAtsDslParser extends AbstractInternalAntlrParser {
   public static final String[] tokenNames = new String[] {
      "<invalid>",
      "<EOR>",
      "<DOWN>",
      "<UP>",
      "RULE_STRING",
      "RULE_INT",
      "RULE_ID",
      "RULE_ML_COMMENT",
      "RULE_SL_COMMENT",
      "RULE_WS",
      "RULE_ANY_OTHER",
      "'userDefinition'",
      "'teamDefinition'",
      "'actionableItem'",
      "'workDefinition'",
      "'program'",
      "'rule'",
      "'{'",
      "'active'",
      "'userId'",
      "'email'",
      "'isAdmin'",
      "'}'",
      "'value'",
      "'uuid'",
      "'guid'",
      "'artifactType'",
      "'namespace'",
      "'attribute'",
      "'staticId'",
      "'lead'",
      "'member'",
      "'privileged'",
      "'relatedTaskWorkDefinition'",
      "'teamWorkflowArtifactType'",
      "'accessContextId'",
      "'version'",
      "'children'",
      "'actionable'",
      "'owner'",
      "'team'",
      "'next'",
      "'released'",
      "'allowCreateBranch'",
      "'allowCommitBranch'",
      "'baselineBranchUuid'",
      "'parallelVersion'",
      "'id'",
      "'startState'",
      "'widgetDefinition'",
      "'attributeName'",
      "'description'",
      "'xWidgetName'",
      "'defaultValue'",
      "'height'",
      "'option'",
      "'minConstraint'",
      "'maxConstraint'",
      "'widget'",
      "'attributeWidget'",
      "'with'",
      "'state'",
      "'type'",
      "'ordinal'",
      "'percentWeight'",
      "'recommendedPercentComplete'",
      "'color'",
      "'decisionReview'",
      "'decisionReviewDefinition'",
      "'title'",
      "'relatedToState'",
      "'blockingType'",
      "'onEvent'",
      "'assignee'",
      "'autoTransitionToDecision'",
      "'peerReview'",
      "'peerReviewDefinition'",
      "'location'",
      "'followup by'",
      "'named'",
      "'to'",
      "'layout'",
      "'layoutCopyFrom'",
      "'composite'",
      "'numColumns'",
      "'GetOrCreate'",
      "'None'",
      "'AsDefault'",
      "'OverrideAttributeValidation'",
      "'name'",
      "'ruleLocation'",
      "'assignees'",
      "'relatedState'",
      "'taskWorkDef'",
      "'RequireStateHourSpentPrompt'",
      "'AddDecisionValidateBlockingReview'",
      "'AddDecisionValidateNonBlockingReview'",
      "'AllowTransitionWithWorkingBranch'",
      "'ForceAssigneesToTeamLeads'",
      "'RequireTargetedVersion'",
      "'AllowPrivilegedEditToTeamMember'",
      "'AllowPrivilegedEditToTeamMemberAndOriginator'",
      "'AllowPrivilegedEditToAll'",
      "'AllowEditToAll'",
      "'AllowAssigneeToAll'",
      "'AllowTransitionWithoutTaskCompletion'",
      "'REQUIRED_FOR_TRANSITION'",
      "'NOT_REQUIRED_FOR_TRANSITION'",
      "'REQUIRED_FOR_COMPLETION'",
      "'NOT_REQUIRED_FOR_COMPLETION'",
      "'ENABLED'",
      "'NOT_ENABLED'",
      "'EDITABLE'",
      "'NOT_EDITABLE'",
      "'FUTURE_DATE_REQUIRED'",
      "'NOT_FUTURE_DATE_REQUIRED'",
      "'MULTI_SELECT'",
      "'HORIZONTAL_LABEL'",
      "'VERTICAL_LABEL'",
      "'LABEL_AFTER'",
      "'LABEL_BEFORE'",
      "'NO_LABEL'",
      "'SORTED'",
      "'ADD_DEFAULT_VALUE'",
      "'NO_DEFAULT_VALUE'",
      "'BEGIN_COMPOSITE_4'",
      "'BEGIN_COMPOSITE_6'",
      "'BEGIN_COMPOSITE_8'",
      "'BEGIN_COMPOSITE_10'",
      "'END_COMPOSITE'",
      "'FILL_NONE'",
      "'FILL_HORIZONTALLY'",
      "'FILL_VERTICALLY'",
      "'ALIGN_LEFT'",
      "'ALIGN_RIGHT'",
      "'ALIGN_CENTER'",
      "'Working'",
      "'Completed'",
      "'Cancelled'",
      "'BLACK'",
      "'WHITE'",
      "'RED'",
      "'DARK_RED'",
      "'GREEN'",
      "'DARK_GREEN'",
      "'YELLOW'",
      "'DARK_YELLOW'",
      "'BLUE'",
      "'DARK_BLUE'",
      "'MAGENTA'",
      "'DARK_MAGENTA'",
      "'CYAN'",
      "'DARK_CYAN'",
      "'GRAY'",
      "'DARK_GRAY'",
      "'CreateBranch'",
      "'CommitBranch'",
      "'CreateWorkflow'",
      "'TransitionTo'",
      "'Manual'",
      "'True'",
      "'False'",
      "'Transition'",
      "'Commit'",
      "'StateDefinition'",
      "'TeamDefinition'",
      "'ActionableItem'"};
   public static final int T__144 = 144;
   public static final int T__143 = 143;
   public static final int T__146 = 146;
   public static final int T__50 = 50;
   public static final int T__145 = 145;
   public static final int T__140 = 140;
   public static final int T__142 = 142;
   public static final int T__141 = 141;
   public static final int T__59 = 59;
   public static final int T__55 = 55;
   public static final int T__56 = 56;
   public static final int T__57 = 57;
   public static final int T__58 = 58;
   public static final int T__51 = 51;
   public static final int T__137 = 137;
   public static final int T__52 = 52;
   public static final int T__136 = 136;
   public static final int T__53 = 53;
   public static final int T__139 = 139;
   public static final int T__54 = 54;
   public static final int T__138 = 138;
   public static final int T__133 = 133;
   public static final int T__132 = 132;
   public static final int T__60 = 60;
   public static final int T__135 = 135;
   public static final int T__61 = 61;
   public static final int T__134 = 134;
   public static final int RULE_ID = 6;
   public static final int T__131 = 131;
   public static final int T__130 = 130;
   public static final int RULE_INT = 5;
   public static final int T__66 = 66;
   public static final int RULE_ML_COMMENT = 7;
   public static final int T__67 = 67;
   public static final int T__129 = 129;
   public static final int T__68 = 68;
   public static final int T__69 = 69;
   public static final int T__62 = 62;
   public static final int T__126 = 126;
   public static final int T__63 = 63;
   public static final int T__125 = 125;
   public static final int T__64 = 64;
   public static final int T__128 = 128;
   public static final int T__65 = 65;
   public static final int T__127 = 127;
   public static final int T__166 = 166;
   public static final int T__165 = 165;
   public static final int T__162 = 162;
   public static final int T__161 = 161;
   public static final int T__164 = 164;
   public static final int T__163 = 163;
   public static final int T__160 = 160;
   public static final int T__37 = 37;
   public static final int T__38 = 38;
   public static final int T__39 = 39;
   public static final int T__33 = 33;
   public static final int T__34 = 34;
   public static final int T__35 = 35;
   public static final int T__36 = 36;
   public static final int T__159 = 159;
   public static final int T__30 = 30;
   public static final int T__158 = 158;
   public static final int T__31 = 31;
   public static final int T__32 = 32;
   public static final int T__155 = 155;
   public static final int T__154 = 154;
   public static final int T__157 = 157;
   public static final int T__156 = 156;
   public static final int T__151 = 151;
   public static final int T__150 = 150;
   public static final int T__153 = 153;
   public static final int T__152 = 152;
   public static final int T__48 = 48;
   public static final int T__49 = 49;
   public static final int T__44 = 44;
   public static final int T__45 = 45;
   public static final int T__46 = 46;
   public static final int T__47 = 47;
   public static final int T__40 = 40;
   public static final int T__148 = 148;
   public static final int T__41 = 41;
   public static final int T__147 = 147;
   public static final int T__42 = 42;
   public static final int T__43 = 43;
   public static final int T__149 = 149;
   public static final int T__91 = 91;
   public static final int T__100 = 100;
   public static final int T__92 = 92;
   public static final int T__93 = 93;
   public static final int T__102 = 102;
   public static final int T__94 = 94;
   public static final int T__101 = 101;
   public static final int T__90 = 90;
   public static final int T__19 = 19;
   public static final int T__15 = 15;
   public static final int T__16 = 16;
   public static final int T__17 = 17;
   public static final int T__18 = 18;
   public static final int T__11 = 11;
   public static final int T__99 = 99;
   public static final int T__12 = 12;
   public static final int T__13 = 13;
   public static final int T__14 = 14;
   public static final int T__95 = 95;
   public static final int T__96 = 96;
   public static final int T__97 = 97;
   public static final int T__98 = 98;
   public static final int T__26 = 26;
   public static final int T__27 = 27;
   public static final int T__28 = 28;
   public static final int T__29 = 29;
   public static final int T__22 = 22;
   public static final int T__23 = 23;
   public static final int T__24 = 24;
   public static final int T__25 = 25;
   public static final int T__20 = 20;
   public static final int T__21 = 21;
   public static final int T__122 = 122;
   public static final int T__70 = 70;
   public static final int T__121 = 121;
   public static final int T__71 = 71;
   public static final int T__124 = 124;
   public static final int T__72 = 72;
   public static final int T__123 = 123;
   public static final int T__120 = 120;
   public static final int RULE_STRING = 4;
   public static final int RULE_SL_COMMENT = 8;
   public static final int T__77 = 77;
   public static final int T__119 = 119;
   public static final int T__78 = 78;
   public static final int T__118 = 118;
   public static final int T__79 = 79;
   public static final int T__73 = 73;
   public static final int T__115 = 115;
   public static final int EOF = -1;
   public static final int T__74 = 74;
   public static final int T__114 = 114;
   public static final int T__75 = 75;
   public static final int T__117 = 117;
   public static final int T__76 = 76;
   public static final int T__116 = 116;
   public static final int T__80 = 80;
   public static final int T__111 = 111;
   public static final int T__81 = 81;
   public static final int T__110 = 110;
   public static final int T__82 = 82;
   public static final int T__113 = 113;
   public static final int T__83 = 83;
   public static final int T__112 = 112;
   public static final int RULE_WS = 9;
   public static final int RULE_ANY_OTHER = 10;
   public static final int T__88 = 88;
   public static final int T__108 = 108;
   public static final int T__89 = 89;
   public static final int T__107 = 107;
   public static final int T__109 = 109;
   public static final int T__84 = 84;
   public static final int T__104 = 104;
   public static final int T__85 = 85;
   public static final int T__103 = 103;
   public static final int T__86 = 86;
   public static final int T__106 = 106;
   public static final int T__87 = 87;
   public static final int T__105 = 105;

   // delegates
   // delegators

   public InternalAtsDslParser(TokenStream input) {
      this(input, new RecognizerSharedState());
   }

   public InternalAtsDslParser(TokenStream input, RecognizerSharedState state) {
      super(input, state);

   }

   @Override
   public String[] getTokenNames() {
      return InternalAtsDslParser.tokenNames;
   }

   @Override
   public String getGrammarFileName() {
      return "../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g";
   }

   private AtsDslGrammarAccess grammarAccess;

   public InternalAtsDslParser(TokenStream input, AtsDslGrammarAccess grammarAccess) {
      this(input);
      this.grammarAccess = grammarAccess;
      registerRules(grammarAccess.getGrammar());
   }

   @Override
   protected String getFirstRuleName() {
      return "AtsDsl";
   }

   @Override
   protected AtsDslGrammarAccess getGrammarAccess() {
      return grammarAccess;
   }

   // $ANTLR start "entryRuleAtsDsl"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:68:1: entryRuleAtsDsl returns [EObject current=null] : iv_ruleAtsDsl= ruleAtsDsl EOF ;
   public final EObject entryRuleAtsDsl() throws RecognitionException {
      EObject current = null;

      EObject iv_ruleAtsDsl = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:69:2: (iv_ruleAtsDsl= ruleAtsDsl EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:70:2: iv_ruleAtsDsl= ruleAtsDsl EOF
         {
            newCompositeNode(grammarAccess.getAtsDslRule());
            pushFollow(FOLLOW_ruleAtsDsl_in_entryRuleAtsDsl75);
            iv_ruleAtsDsl = ruleAtsDsl();

            state._fsp--;

            current = iv_ruleAtsDsl;
            match(input, EOF, FOLLOW_EOF_in_entryRuleAtsDsl85);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleAtsDsl"

   // $ANTLR start "ruleAtsDsl"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:77:1: ruleAtsDsl returns [EObject current=null] : ( ( (otherlv_0= 'userDefinition' ( (lv_userDef_1_0= ruleUserDef ) ) )* (otherlv_2= 'teamDefinition' ( (lv_teamDef_3_0= ruleTeamDef ) ) )* (otherlv_4= 'actionableItem' ( (lv_actionableItemDef_5_0= ruleActionableItemDef ) ) )* (otherlv_6= 'workDefinition' ( (lv_workDef_7_0= ruleWorkDef ) ) )* (otherlv_8= 'program' ( (lv_program_9_0= ruleProgramDef ) ) )* ) | (otherlv_10= 'rule' ( (lv_rule_11_0= ruleRule ) ) )* ) ;
   public final EObject ruleAtsDsl() throws RecognitionException {
      EObject current = null;

      Token otherlv_0 = null;
      Token otherlv_2 = null;
      Token otherlv_4 = null;
      Token otherlv_6 = null;
      Token otherlv_8 = null;
      Token otherlv_10 = null;
      EObject lv_userDef_1_0 = null;

      EObject lv_teamDef_3_0 = null;

      EObject lv_actionableItemDef_5_0 = null;

      EObject lv_workDef_7_0 = null;

      EObject lv_program_9_0 = null;

      EObject lv_rule_11_0 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:80:28: ( ( ( (otherlv_0= 'userDefinition' ( (lv_userDef_1_0= ruleUserDef ) ) )* (otherlv_2= 'teamDefinition' ( (lv_teamDef_3_0= ruleTeamDef ) ) )* (otherlv_4= 'actionableItem' ( (lv_actionableItemDef_5_0= ruleActionableItemDef ) ) )* (otherlv_6= 'workDefinition' ( (lv_workDef_7_0= ruleWorkDef ) ) )* (otherlv_8= 'program' ( (lv_program_9_0= ruleProgramDef ) ) )* ) | (otherlv_10= 'rule' ( (lv_rule_11_0= ruleRule ) ) )* ) )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:81:1: ( ( (otherlv_0= 'userDefinition' ( (lv_userDef_1_0= ruleUserDef ) ) )* (otherlv_2= 'teamDefinition' ( (lv_teamDef_3_0= ruleTeamDef ) ) )* (otherlv_4= 'actionableItem' ( (lv_actionableItemDef_5_0= ruleActionableItemDef ) ) )* (otherlv_6= 'workDefinition' ( (lv_workDef_7_0= ruleWorkDef ) ) )* (otherlv_8= 'program' ( (lv_program_9_0= ruleProgramDef ) ) )* ) | (otherlv_10= 'rule' ( (lv_rule_11_0= ruleRule ) ) )* )
         {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:81:1: ( ( (otherlv_0= 'userDefinition' ( (lv_userDef_1_0= ruleUserDef ) ) )* (otherlv_2= 'teamDefinition' ( (lv_teamDef_3_0= ruleTeamDef ) ) )* (otherlv_4= 'actionableItem' ( (lv_actionableItemDef_5_0= ruleActionableItemDef ) ) )* (otherlv_6= 'workDefinition' ( (lv_workDef_7_0= ruleWorkDef ) ) )* (otherlv_8= 'program' ( (lv_program_9_0= ruleProgramDef ) ) )* ) | (otherlv_10= 'rule' ( (lv_rule_11_0= ruleRule ) ) )* )
            int alt7 = 2;
            switch (input.LA(1)) {
               case 11:
               case 12:
               case 13:
               case 14:
               case 15: {
                  alt7 = 1;
               }
                  break;
               case EOF: {
                  alt7 = 1;
               }
                  break;
               case 16: {
                  alt7 = 2;
               }
                  break;
               default:
                  NoViableAltException nvae = new NoViableAltException("", 7, 0, input);

                  throw nvae;
            }

            switch (alt7) {
               case 1:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:81:2: ( (otherlv_0= 'userDefinition' ( (lv_userDef_1_0= ruleUserDef ) ) )* (otherlv_2= 'teamDefinition' ( (lv_teamDef_3_0= ruleTeamDef ) ) )* (otherlv_4= 'actionableItem' ( (lv_actionableItemDef_5_0= ruleActionableItemDef ) ) )* (otherlv_6= 'workDefinition' ( (lv_workDef_7_0= ruleWorkDef ) ) )* (otherlv_8= 'program' ( (lv_program_9_0= ruleProgramDef ) ) )* )
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:81:2: ( (otherlv_0= 'userDefinition' ( (lv_userDef_1_0= ruleUserDef ) ) )* (otherlv_2= 'teamDefinition' ( (lv_teamDef_3_0= ruleTeamDef ) ) )* (otherlv_4= 'actionableItem' ( (lv_actionableItemDef_5_0= ruleActionableItemDef ) ) )* (otherlv_6= 'workDefinition' ( (lv_workDef_7_0= ruleWorkDef ) ) )* (otherlv_8= 'program' ( (lv_program_9_0= ruleProgramDef ) ) )* )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:81:3: (otherlv_0= 'userDefinition' ( (lv_userDef_1_0= ruleUserDef ) ) )* (otherlv_2= 'teamDefinition' ( (lv_teamDef_3_0= ruleTeamDef ) ) )* (otherlv_4= 'actionableItem' ( (lv_actionableItemDef_5_0= ruleActionableItemDef ) ) )* (otherlv_6= 'workDefinition' ( (lv_workDef_7_0= ruleWorkDef ) ) )* (otherlv_8= 'program' ( (lv_program_9_0= ruleProgramDef ) ) )*
                  {
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:81:3: (otherlv_0= 'userDefinition' ( (lv_userDef_1_0= ruleUserDef ) ) )*
                     loop1: do {
                        int alt1 = 2;
                        int LA1_0 = input.LA(1);

                        if (LA1_0 == 11) {
                           alt1 = 1;
                        }

                        switch (alt1) {
                           case 1:
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:81:5: otherlv_0= 'userDefinition' ( (lv_userDef_1_0= ruleUserDef ) )
                           {
                              otherlv_0 = (Token) match(input, 11, FOLLOW_11_in_ruleAtsDsl124);

                              newLeafNode(otherlv_0, grammarAccess.getAtsDslAccess().getUserDefinitionKeyword_0_0_0());

                              // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:85:1: ( (lv_userDef_1_0= ruleUserDef ) )
                              // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:86:1: (lv_userDef_1_0= ruleUserDef )
                              {
                                 // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:86:1: (lv_userDef_1_0= ruleUserDef )
                                 // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:87:3: lv_userDef_1_0= ruleUserDef
                                 {

                                    newCompositeNode(
                                       grammarAccess.getAtsDslAccess().getUserDefUserDefParserRuleCall_0_0_1_0());

                                    pushFollow(FOLLOW_ruleUserDef_in_ruleAtsDsl145);
                                    lv_userDef_1_0 = ruleUserDef();

                                    state._fsp--;

                                    if (current == null) {
                                       current = createModelElementForParent(grammarAccess.getAtsDslRule());
                                    }
                                    add(current, "userDef", lv_userDef_1_0, "UserDef");
                                    afterParserOrEnumRuleCall();

                                 }

                              }

                           }
                              break;

                           default:
                              break loop1;
                        }
                     } while (true);

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:103:4: (otherlv_2= 'teamDefinition' ( (lv_teamDef_3_0= ruleTeamDef ) ) )*
                     loop2: do {
                        int alt2 = 2;
                        int LA2_0 = input.LA(1);

                        if (LA2_0 == 12) {
                           alt2 = 1;
                        }

                        switch (alt2) {
                           case 1:
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:103:6: otherlv_2= 'teamDefinition' ( (lv_teamDef_3_0= ruleTeamDef ) )
                           {
                              otherlv_2 = (Token) match(input, 12, FOLLOW_12_in_ruleAtsDsl160);

                              newLeafNode(otherlv_2, grammarAccess.getAtsDslAccess().getTeamDefinitionKeyword_0_1_0());

                              // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:107:1: ( (lv_teamDef_3_0= ruleTeamDef ) )
                              // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:108:1: (lv_teamDef_3_0= ruleTeamDef )
                              {
                                 // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:108:1: (lv_teamDef_3_0= ruleTeamDef )
                                 // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:109:3: lv_teamDef_3_0= ruleTeamDef
                                 {

                                    newCompositeNode(
                                       grammarAccess.getAtsDslAccess().getTeamDefTeamDefParserRuleCall_0_1_1_0());

                                    pushFollow(FOLLOW_ruleTeamDef_in_ruleAtsDsl181);
                                    lv_teamDef_3_0 = ruleTeamDef();

                                    state._fsp--;

                                    if (current == null) {
                                       current = createModelElementForParent(grammarAccess.getAtsDslRule());
                                    }
                                    add(current, "teamDef", lv_teamDef_3_0, "TeamDef");
                                    afterParserOrEnumRuleCall();

                                 }

                              }

                           }
                              break;

                           default:
                              break loop2;
                        }
                     } while (true);

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:125:4: (otherlv_4= 'actionableItem' ( (lv_actionableItemDef_5_0= ruleActionableItemDef ) ) )*
                     loop3: do {
                        int alt3 = 2;
                        int LA3_0 = input.LA(1);

                        if (LA3_0 == 13) {
                           alt3 = 1;
                        }

                        switch (alt3) {
                           case 1:
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:125:6: otherlv_4= 'actionableItem' ( (lv_actionableItemDef_5_0= ruleActionableItemDef ) )
                           {
                              otherlv_4 = (Token) match(input, 13, FOLLOW_13_in_ruleAtsDsl196);

                              newLeafNode(otherlv_4, grammarAccess.getAtsDslAccess().getActionableItemKeyword_0_2_0());

                              // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:129:1: ( (lv_actionableItemDef_5_0= ruleActionableItemDef ) )
                              // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:130:1: (lv_actionableItemDef_5_0= ruleActionableItemDef )
                              {
                                 // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:130:1: (lv_actionableItemDef_5_0= ruleActionableItemDef )
                                 // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:131:3: lv_actionableItemDef_5_0= ruleActionableItemDef
                                 {

                                    newCompositeNode(
                                       grammarAccess.getAtsDslAccess().getActionableItemDefActionableItemDefParserRuleCall_0_2_1_0());

                                    pushFollow(FOLLOW_ruleActionableItemDef_in_ruleAtsDsl217);
                                    lv_actionableItemDef_5_0 = ruleActionableItemDef();

                                    state._fsp--;

                                    if (current == null) {
                                       current = createModelElementForParent(grammarAccess.getAtsDslRule());
                                    }
                                    add(current, "actionableItemDef", lv_actionableItemDef_5_0, "ActionableItemDef");
                                    afterParserOrEnumRuleCall();

                                 }

                              }

                           }
                              break;

                           default:
                              break loop3;
                        }
                     } while (true);

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:147:4: (otherlv_6= 'workDefinition' ( (lv_workDef_7_0= ruleWorkDef ) ) )*
                     loop4: do {
                        int alt4 = 2;
                        int LA4_0 = input.LA(1);

                        if (LA4_0 == 14) {
                           alt4 = 1;
                        }

                        switch (alt4) {
                           case 1:
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:147:6: otherlv_6= 'workDefinition' ( (lv_workDef_7_0= ruleWorkDef ) )
                           {
                              otherlv_6 = (Token) match(input, 14, FOLLOW_14_in_ruleAtsDsl232);

                              newLeafNode(otherlv_6, grammarAccess.getAtsDslAccess().getWorkDefinitionKeyword_0_3_0());

                              // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:151:1: ( (lv_workDef_7_0= ruleWorkDef ) )
                              // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:152:1: (lv_workDef_7_0= ruleWorkDef )
                              {
                                 // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:152:1: (lv_workDef_7_0= ruleWorkDef )
                                 // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:153:3: lv_workDef_7_0= ruleWorkDef
                                 {

                                    newCompositeNode(
                                       grammarAccess.getAtsDslAccess().getWorkDefWorkDefParserRuleCall_0_3_1_0());

                                    pushFollow(FOLLOW_ruleWorkDef_in_ruleAtsDsl253);
                                    lv_workDef_7_0 = ruleWorkDef();

                                    state._fsp--;

                                    if (current == null) {
                                       current = createModelElementForParent(grammarAccess.getAtsDslRule());
                                    }
                                    add(current, "workDef", lv_workDef_7_0, "WorkDef");
                                    afterParserOrEnumRuleCall();

                                 }

                              }

                           }
                              break;

                           default:
                              break loop4;
                        }
                     } while (true);

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:169:4: (otherlv_8= 'program' ( (lv_program_9_0= ruleProgramDef ) ) )*
                     loop5: do {
                        int alt5 = 2;
                        int LA5_0 = input.LA(1);

                        if (LA5_0 == 15) {
                           alt5 = 1;
                        }

                        switch (alt5) {
                           case 1:
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:169:6: otherlv_8= 'program' ( (lv_program_9_0= ruleProgramDef ) )
                           {
                              otherlv_8 = (Token) match(input, 15, FOLLOW_15_in_ruleAtsDsl268);

                              newLeafNode(otherlv_8, grammarAccess.getAtsDslAccess().getProgramKeyword_0_4_0());

                              // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:173:1: ( (lv_program_9_0= ruleProgramDef ) )
                              // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:174:1: (lv_program_9_0= ruleProgramDef )
                              {
                                 // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:174:1: (lv_program_9_0= ruleProgramDef )
                                 // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:175:3: lv_program_9_0= ruleProgramDef
                                 {

                                    newCompositeNode(
                                       grammarAccess.getAtsDslAccess().getProgramProgramDefParserRuleCall_0_4_1_0());

                                    pushFollow(FOLLOW_ruleProgramDef_in_ruleAtsDsl289);
                                    lv_program_9_0 = ruleProgramDef();

                                    state._fsp--;

                                    if (current == null) {
                                       current = createModelElementForParent(grammarAccess.getAtsDslRule());
                                    }
                                    add(current, "program", lv_program_9_0, "ProgramDef");
                                    afterParserOrEnumRuleCall();

                                 }

                              }

                           }
                              break;

                           default:
                              break loop5;
                        }
                     } while (true);

                  }

               }
                  break;
               case 2:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:192:6: (otherlv_10= 'rule' ( (lv_rule_11_0= ruleRule ) ) )*
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:192:6: (otherlv_10= 'rule' ( (lv_rule_11_0= ruleRule ) ) )*
                  loop6: do {
                     int alt6 = 2;
                     int LA6_0 = input.LA(1);

                     if (LA6_0 == 16) {
                        alt6 = 1;
                     }

                     switch (alt6) {
                        case 1:
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:192:8: otherlv_10= 'rule' ( (lv_rule_11_0= ruleRule ) )
                        {
                           otherlv_10 = (Token) match(input, 16, FOLLOW_16_in_ruleAtsDsl311);

                           newLeafNode(otherlv_10, grammarAccess.getAtsDslAccess().getRuleKeyword_1_0());

                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:196:1: ( (lv_rule_11_0= ruleRule ) )
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:197:1: (lv_rule_11_0= ruleRule )
                           {
                              // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:197:1: (lv_rule_11_0= ruleRule )
                              // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:198:3: lv_rule_11_0= ruleRule
                              {

                                 newCompositeNode(grammarAccess.getAtsDslAccess().getRuleRuleParserRuleCall_1_1_0());

                                 pushFollow(FOLLOW_ruleRule_in_ruleAtsDsl332);
                                 lv_rule_11_0 = ruleRule();

                                 state._fsp--;

                                 if (current == null) {
                                    current = createModelElementForParent(grammarAccess.getAtsDslRule());
                                 }
                                 add(current, "rule", lv_rule_11_0, "Rule");
                                 afterParserOrEnumRuleCall();

                              }

                           }

                        }
                           break;

                        default:
                           break loop6;
                     }
                  } while (true);

               }
                  break;

            }

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleAtsDsl"

   // $ANTLR start "entryRuleUSER_DEF_REFERENCE"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:222:1: entryRuleUSER_DEF_REFERENCE returns [String current=null] : iv_ruleUSER_DEF_REFERENCE= ruleUSER_DEF_REFERENCE EOF ;
   public final String entryRuleUSER_DEF_REFERENCE() throws RecognitionException {
      String current = null;

      AntlrDatatypeRuleToken iv_ruleUSER_DEF_REFERENCE = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:223:2: (iv_ruleUSER_DEF_REFERENCE= ruleUSER_DEF_REFERENCE EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:224:2: iv_ruleUSER_DEF_REFERENCE= ruleUSER_DEF_REFERENCE EOF
         {
            newCompositeNode(grammarAccess.getUSER_DEF_REFERENCERule());
            pushFollow(FOLLOW_ruleUSER_DEF_REFERENCE_in_entryRuleUSER_DEF_REFERENCE371);
            iv_ruleUSER_DEF_REFERENCE = ruleUSER_DEF_REFERENCE();

            state._fsp--;

            current = iv_ruleUSER_DEF_REFERENCE.getText();
            match(input, EOF, FOLLOW_EOF_in_entryRuleUSER_DEF_REFERENCE382);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleUSER_DEF_REFERENCE"

   // $ANTLR start "ruleUSER_DEF_REFERENCE"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:231:1: ruleUSER_DEF_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
   public final AntlrDatatypeRuleToken ruleUSER_DEF_REFERENCE() throws RecognitionException {
      AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

      Token this_STRING_0 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:234:28: (this_STRING_0= RULE_STRING )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:235:5: this_STRING_0= RULE_STRING
         {
            this_STRING_0 = (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleUSER_DEF_REFERENCE421);

            current.merge(this_STRING_0);

            newLeafNode(this_STRING_0, grammarAccess.getUSER_DEF_REFERENCEAccess().getSTRINGTerminalRuleCall());

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleUSER_DEF_REFERENCE"

   // $ANTLR start "entryRuleUserDef"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:250:1: entryRuleUserDef returns [EObject current=null] : iv_ruleUserDef= ruleUserDef EOF ;
   public final EObject entryRuleUserDef() throws RecognitionException {
      EObject current = null;

      EObject iv_ruleUserDef = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:251:2: (iv_ruleUserDef= ruleUserDef EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:252:2: iv_ruleUserDef= ruleUserDef EOF
         {
            newCompositeNode(grammarAccess.getUserDefRule());
            pushFollow(FOLLOW_ruleUserDef_in_entryRuleUserDef465);
            iv_ruleUserDef = ruleUserDef();

            state._fsp--;

            current = iv_ruleUserDef;
            match(input, EOF, FOLLOW_EOF_in_entryRuleUserDef475);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleUserDef"

   // $ANTLR start "ruleUserDef"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:259:1: ruleUserDef returns [EObject current=null] : ( ( (lv_name_0_0= ruleUSER_DEF_REFERENCE ) ) ( (lv_userDefOption_1_0= ruleUserDefOption ) )* (otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'userId' ( (lv_userId_6_0= RULE_STRING ) ) )? (otherlv_7= 'email' ( (lv_email_8_0= RULE_STRING ) ) )? (otherlv_9= 'isAdmin' ( (lv_admin_10_0= ruleBooleanDef ) ) )? otherlv_11= '}' )? ) ;
   public final EObject ruleUserDef() throws RecognitionException {
      EObject current = null;

      Token otherlv_2 = null;
      Token otherlv_3 = null;
      Token otherlv_5 = null;
      Token lv_userId_6_0 = null;
      Token otherlv_7 = null;
      Token lv_email_8_0 = null;
      Token otherlv_9 = null;
      Token otherlv_11 = null;
      AntlrDatatypeRuleToken lv_name_0_0 = null;

      AntlrDatatypeRuleToken lv_userDefOption_1_0 = null;

      Enumerator lv_active_4_0 = null;

      Enumerator lv_admin_10_0 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:262:28: ( ( ( (lv_name_0_0= ruleUSER_DEF_REFERENCE ) ) ( (lv_userDefOption_1_0= ruleUserDefOption ) )* (otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'userId' ( (lv_userId_6_0= RULE_STRING ) ) )? (otherlv_7= 'email' ( (lv_email_8_0= RULE_STRING ) ) )? (otherlv_9= 'isAdmin' ( (lv_admin_10_0= ruleBooleanDef ) ) )? otherlv_11= '}' )? ) )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:263:1: ( ( (lv_name_0_0= ruleUSER_DEF_REFERENCE ) ) ( (lv_userDefOption_1_0= ruleUserDefOption ) )* (otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'userId' ( (lv_userId_6_0= RULE_STRING ) ) )? (otherlv_7= 'email' ( (lv_email_8_0= RULE_STRING ) ) )? (otherlv_9= 'isAdmin' ( (lv_admin_10_0= ruleBooleanDef ) ) )? otherlv_11= '}' )? )
         {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:263:1: ( ( (lv_name_0_0= ruleUSER_DEF_REFERENCE ) ) ( (lv_userDefOption_1_0= ruleUserDefOption ) )* (otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'userId' ( (lv_userId_6_0= RULE_STRING ) ) )? (otherlv_7= 'email' ( (lv_email_8_0= RULE_STRING ) ) )? (otherlv_9= 'isAdmin' ( (lv_admin_10_0= ruleBooleanDef ) ) )? otherlv_11= '}' )? )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:263:2: ( (lv_name_0_0= ruleUSER_DEF_REFERENCE ) ) ( (lv_userDefOption_1_0= ruleUserDefOption ) )* (otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'userId' ( (lv_userId_6_0= RULE_STRING ) ) )? (otherlv_7= 'email' ( (lv_email_8_0= RULE_STRING ) ) )? (otherlv_9= 'isAdmin' ( (lv_admin_10_0= ruleBooleanDef ) ) )? otherlv_11= '}' )?
            {
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:263:2: ( (lv_name_0_0= ruleUSER_DEF_REFERENCE ) )
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:264:1: (lv_name_0_0= ruleUSER_DEF_REFERENCE )
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:264:1: (lv_name_0_0= ruleUSER_DEF_REFERENCE )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:265:3: lv_name_0_0= ruleUSER_DEF_REFERENCE
                  {

                     newCompositeNode(grammarAccess.getUserDefAccess().getNameUSER_DEF_REFERENCEParserRuleCall_0_0());

                     pushFollow(FOLLOW_ruleUSER_DEF_REFERENCE_in_ruleUserDef521);
                     lv_name_0_0 = ruleUSER_DEF_REFERENCE();

                     state._fsp--;

                     if (current == null) {
                        current = createModelElementForParent(grammarAccess.getUserDefRule());
                     }
                     set(current, "name", lv_name_0_0, "USER_DEF_REFERENCE");
                     afterParserOrEnumRuleCall();

                  }

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:281:2: ( (lv_userDefOption_1_0= ruleUserDefOption ) )*
               loop8: do {
                  int alt8 = 2;
                  int LA8_0 = input.LA(1);

                  if (LA8_0 == RULE_STRING || LA8_0 == 85) {
                     alt8 = 1;
                  }

                  switch (alt8) {
                     case 1:
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:282:1: (lv_userDefOption_1_0= ruleUserDefOption )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:282:1: (lv_userDefOption_1_0= ruleUserDefOption )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:283:3: lv_userDefOption_1_0= ruleUserDefOption
                        {

                           newCompositeNode(
                              grammarAccess.getUserDefAccess().getUserDefOptionUserDefOptionParserRuleCall_1_0());

                           pushFollow(FOLLOW_ruleUserDefOption_in_ruleUserDef542);
                           lv_userDefOption_1_0 = ruleUserDefOption();

                           state._fsp--;

                           if (current == null) {
                              current = createModelElementForParent(grammarAccess.getUserDefRule());
                           }
                           add(current, "userDefOption", lv_userDefOption_1_0, "UserDefOption");
                           afterParserOrEnumRuleCall();

                        }

                     }
                        break;

                     default:
                        break loop8;
                  }
               } while (true);

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:299:3: (otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'userId' ( (lv_userId_6_0= RULE_STRING ) ) )? (otherlv_7= 'email' ( (lv_email_8_0= RULE_STRING ) ) )? (otherlv_9= 'isAdmin' ( (lv_admin_10_0= ruleBooleanDef ) ) )? otherlv_11= '}' )?
               int alt13 = 2;
               int LA13_0 = input.LA(1);

               if (LA13_0 == 17) {
                  alt13 = 1;
               }
               switch (alt13) {
                  case 1:
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:299:5: otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'userId' ( (lv_userId_6_0= RULE_STRING ) ) )? (otherlv_7= 'email' ( (lv_email_8_0= RULE_STRING ) ) )? (otherlv_9= 'isAdmin' ( (lv_admin_10_0= ruleBooleanDef ) ) )? otherlv_11= '}'
                  {
                     otherlv_2 = (Token) match(input, 17, FOLLOW_17_in_ruleUserDef556);

                     newLeafNode(otherlv_2, grammarAccess.getUserDefAccess().getLeftCurlyBracketKeyword_2_0());

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:303:1: (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )?
                     int alt9 = 2;
                     int LA9_0 = input.LA(1);

                     if (LA9_0 == 18) {
                        alt9 = 1;
                     }
                     switch (alt9) {
                        case 1:
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:303:3: otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) )
                        {
                           otherlv_3 = (Token) match(input, 18, FOLLOW_18_in_ruleUserDef569);

                           newLeafNode(otherlv_3, grammarAccess.getUserDefAccess().getActiveKeyword_2_1_0());

                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:307:1: ( (lv_active_4_0= ruleBooleanDef ) )
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:308:1: (lv_active_4_0= ruleBooleanDef )
                           {
                              // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:308:1: (lv_active_4_0= ruleBooleanDef )
                              // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:309:3: lv_active_4_0= ruleBooleanDef
                              {

                                 newCompositeNode(
                                    grammarAccess.getUserDefAccess().getActiveBooleanDefEnumRuleCall_2_1_1_0());

                                 pushFollow(FOLLOW_ruleBooleanDef_in_ruleUserDef590);
                                 lv_active_4_0 = ruleBooleanDef();

                                 state._fsp--;

                                 if (current == null) {
                                    current = createModelElementForParent(grammarAccess.getUserDefRule());
                                 }
                                 set(current, "active", lv_active_4_0, "BooleanDef");
                                 afterParserOrEnumRuleCall();

                              }

                           }

                        }
                           break;

                     }

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:325:4: (otherlv_5= 'userId' ( (lv_userId_6_0= RULE_STRING ) ) )?
                     int alt10 = 2;
                     int LA10_0 = input.LA(1);

                     if (LA10_0 == 19) {
                        alt10 = 1;
                     }
                     switch (alt10) {
                        case 1:
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:325:6: otherlv_5= 'userId' ( (lv_userId_6_0= RULE_STRING ) )
                        {
                           otherlv_5 = (Token) match(input, 19, FOLLOW_19_in_ruleUserDef605);

                           newLeafNode(otherlv_5, grammarAccess.getUserDefAccess().getUserIdKeyword_2_2_0());

                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:329:1: ( (lv_userId_6_0= RULE_STRING ) )
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:330:1: (lv_userId_6_0= RULE_STRING )
                           {
                              // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:330:1: (lv_userId_6_0= RULE_STRING )
                              // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:331:3: lv_userId_6_0= RULE_STRING
                              {
                                 lv_userId_6_0 =
                                    (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleUserDef622);

                                 newLeafNode(lv_userId_6_0,
                                    grammarAccess.getUserDefAccess().getUserIdSTRINGTerminalRuleCall_2_2_1_0());

                                 if (current == null) {
                                    current = createModelElement(grammarAccess.getUserDefRule());
                                 }
                                 setWithLastConsumed(current, "userId", lv_userId_6_0, "STRING");

                              }

                           }

                        }
                           break;

                     }

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:347:4: (otherlv_7= 'email' ( (lv_email_8_0= RULE_STRING ) ) )?
                     int alt11 = 2;
                     int LA11_0 = input.LA(1);

                     if (LA11_0 == 20) {
                        alt11 = 1;
                     }
                     switch (alt11) {
                        case 1:
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:347:6: otherlv_7= 'email' ( (lv_email_8_0= RULE_STRING ) )
                        {
                           otherlv_7 = (Token) match(input, 20, FOLLOW_20_in_ruleUserDef642);

                           newLeafNode(otherlv_7, grammarAccess.getUserDefAccess().getEmailKeyword_2_3_0());

                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:351:1: ( (lv_email_8_0= RULE_STRING ) )
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:352:1: (lv_email_8_0= RULE_STRING )
                           {
                              // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:352:1: (lv_email_8_0= RULE_STRING )
                              // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:353:3: lv_email_8_0= RULE_STRING
                              {
                                 lv_email_8_0 = (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleUserDef659);

                                 newLeafNode(lv_email_8_0,
                                    grammarAccess.getUserDefAccess().getEmailSTRINGTerminalRuleCall_2_3_1_0());

                                 if (current == null) {
                                    current = createModelElement(grammarAccess.getUserDefRule());
                                 }
                                 setWithLastConsumed(current, "email", lv_email_8_0, "STRING");

                              }

                           }

                        }
                           break;

                     }

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:369:4: (otherlv_9= 'isAdmin' ( (lv_admin_10_0= ruleBooleanDef ) ) )?
                     int alt12 = 2;
                     int LA12_0 = input.LA(1);

                     if (LA12_0 == 21) {
                        alt12 = 1;
                     }
                     switch (alt12) {
                        case 1:
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:369:6: otherlv_9= 'isAdmin' ( (lv_admin_10_0= ruleBooleanDef ) )
                        {
                           otherlv_9 = (Token) match(input, 21, FOLLOW_21_in_ruleUserDef679);

                           newLeafNode(otherlv_9, grammarAccess.getUserDefAccess().getIsAdminKeyword_2_4_0());

                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:373:1: ( (lv_admin_10_0= ruleBooleanDef ) )
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:374:1: (lv_admin_10_0= ruleBooleanDef )
                           {
                              // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:374:1: (lv_admin_10_0= ruleBooleanDef )
                              // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:375:3: lv_admin_10_0= ruleBooleanDef
                              {

                                 newCompositeNode(
                                    grammarAccess.getUserDefAccess().getAdminBooleanDefEnumRuleCall_2_4_1_0());

                                 pushFollow(FOLLOW_ruleBooleanDef_in_ruleUserDef700);
                                 lv_admin_10_0 = ruleBooleanDef();

                                 state._fsp--;

                                 if (current == null) {
                                    current = createModelElementForParent(grammarAccess.getUserDefRule());
                                 }
                                 set(current, "admin", lv_admin_10_0, "BooleanDef");
                                 afterParserOrEnumRuleCall();

                              }

                           }

                        }
                           break;

                     }

                     otherlv_11 = (Token) match(input, 22, FOLLOW_22_in_ruleUserDef714);

                     newLeafNode(otherlv_11, grammarAccess.getUserDefAccess().getRightCurlyBracketKeyword_2_5());

                  }
                     break;

               }

            }

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleUserDef"

   // $ANTLR start "entryRuleATTR_DEF_REFERENCE"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:403:1: entryRuleATTR_DEF_REFERENCE returns [String current=null] : iv_ruleATTR_DEF_REFERENCE= ruleATTR_DEF_REFERENCE EOF ;
   public final String entryRuleATTR_DEF_REFERENCE() throws RecognitionException {
      String current = null;

      AntlrDatatypeRuleToken iv_ruleATTR_DEF_REFERENCE = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:404:2: (iv_ruleATTR_DEF_REFERENCE= ruleATTR_DEF_REFERENCE EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:405:2: iv_ruleATTR_DEF_REFERENCE= ruleATTR_DEF_REFERENCE EOF
         {
            newCompositeNode(grammarAccess.getATTR_DEF_REFERENCERule());
            pushFollow(FOLLOW_ruleATTR_DEF_REFERENCE_in_entryRuleATTR_DEF_REFERENCE753);
            iv_ruleATTR_DEF_REFERENCE = ruleATTR_DEF_REFERENCE();

            state._fsp--;

            current = iv_ruleATTR_DEF_REFERENCE.getText();
            match(input, EOF, FOLLOW_EOF_in_entryRuleATTR_DEF_REFERENCE764);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleATTR_DEF_REFERENCE"

   // $ANTLR start "ruleATTR_DEF_REFERENCE"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:412:1: ruleATTR_DEF_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
   public final AntlrDatatypeRuleToken ruleATTR_DEF_REFERENCE() throws RecognitionException {
      AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

      Token this_STRING_0 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:415:28: (this_STRING_0= RULE_STRING )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:416:5: this_STRING_0= RULE_STRING
         {
            this_STRING_0 = (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleATTR_DEF_REFERENCE803);

            current.merge(this_STRING_0);

            newLeafNode(this_STRING_0, grammarAccess.getATTR_DEF_REFERENCEAccess().getSTRINGTerminalRuleCall());

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleATTR_DEF_REFERENCE"

   // $ANTLR start "entryRuleAttrDef"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:431:1: entryRuleAttrDef returns [EObject current=null] : iv_ruleAttrDef= ruleAttrDef EOF ;
   public final EObject entryRuleAttrDef() throws RecognitionException {
      EObject current = null;

      EObject iv_ruleAttrDef = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:432:2: (iv_ruleAttrDef= ruleAttrDef EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:433:2: iv_ruleAttrDef= ruleAttrDef EOF
         {
            newCompositeNode(grammarAccess.getAttrDefRule());
            pushFollow(FOLLOW_ruleAttrDef_in_entryRuleAttrDef847);
            iv_ruleAttrDef = ruleAttrDef();

            state._fsp--;

            current = iv_ruleAttrDef;
            match(input, EOF, FOLLOW_EOF_in_entryRuleAttrDef857);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleAttrDef"

   // $ANTLR start "ruleAttrDef"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:440:1: ruleAttrDef returns [EObject current=null] : ( ( (lv_name_0_0= ruleATTR_DEF_REFERENCE ) ) ( (lv_option_1_0= ruleAttrDefOptions ) ) ) ;
   public final EObject ruleAttrDef() throws RecognitionException {
      EObject current = null;

      AntlrDatatypeRuleToken lv_name_0_0 = null;

      EObject lv_option_1_0 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:443:28: ( ( ( (lv_name_0_0= ruleATTR_DEF_REFERENCE ) ) ( (lv_option_1_0= ruleAttrDefOptions ) ) ) )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:444:1: ( ( (lv_name_0_0= ruleATTR_DEF_REFERENCE ) ) ( (lv_option_1_0= ruleAttrDefOptions ) ) )
         {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:444:1: ( ( (lv_name_0_0= ruleATTR_DEF_REFERENCE ) ) ( (lv_option_1_0= ruleAttrDefOptions ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:444:2: ( (lv_name_0_0= ruleATTR_DEF_REFERENCE ) ) ( (lv_option_1_0= ruleAttrDefOptions ) )
            {
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:444:2: ( (lv_name_0_0= ruleATTR_DEF_REFERENCE ) )
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:445:1: (lv_name_0_0= ruleATTR_DEF_REFERENCE )
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:445:1: (lv_name_0_0= ruleATTR_DEF_REFERENCE )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:446:3: lv_name_0_0= ruleATTR_DEF_REFERENCE
                  {

                     newCompositeNode(grammarAccess.getAttrDefAccess().getNameATTR_DEF_REFERENCEParserRuleCall_0_0());

                     pushFollow(FOLLOW_ruleATTR_DEF_REFERENCE_in_ruleAttrDef903);
                     lv_name_0_0 = ruleATTR_DEF_REFERENCE();

                     state._fsp--;

                     if (current == null) {
                        current = createModelElementForParent(grammarAccess.getAttrDefRule());
                     }
                     set(current, "name", lv_name_0_0, "ATTR_DEF_REFERENCE");
                     afterParserOrEnumRuleCall();

                  }

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:462:2: ( (lv_option_1_0= ruleAttrDefOptions ) )
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:463:1: (lv_option_1_0= ruleAttrDefOptions )
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:463:1: (lv_option_1_0= ruleAttrDefOptions )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:464:3: lv_option_1_0= ruleAttrDefOptions
                  {

                     newCompositeNode(grammarAccess.getAttrDefAccess().getOptionAttrDefOptionsParserRuleCall_1_0());

                     pushFollow(FOLLOW_ruleAttrDefOptions_in_ruleAttrDef924);
                     lv_option_1_0 = ruleAttrDefOptions();

                     state._fsp--;

                     if (current == null) {
                        current = createModelElementForParent(grammarAccess.getAttrDefRule());
                     }
                     set(current, "option", lv_option_1_0, "AttrDefOptions");
                     afterParserOrEnumRuleCall();

                  }

               }

            }

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleAttrDef"

   // $ANTLR start "entryRuleAttrDefOptions"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:488:1: entryRuleAttrDefOptions returns [EObject current=null] : iv_ruleAttrDefOptions= ruleAttrDefOptions EOF ;
   public final EObject entryRuleAttrDefOptions() throws RecognitionException {
      EObject current = null;

      EObject iv_ruleAttrDefOptions = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:489:2: (iv_ruleAttrDefOptions= ruleAttrDefOptions EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:490:2: iv_ruleAttrDefOptions= ruleAttrDefOptions EOF
         {
            newCompositeNode(grammarAccess.getAttrDefOptionsRule());
            pushFollow(FOLLOW_ruleAttrDefOptions_in_entryRuleAttrDefOptions960);
            iv_ruleAttrDefOptions = ruleAttrDefOptions();

            state._fsp--;

            current = iv_ruleAttrDefOptions;
            match(input, EOF, FOLLOW_EOF_in_entryRuleAttrDefOptions970);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleAttrDefOptions"

   // $ANTLR start "ruleAttrDefOptions"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:497:1: ruleAttrDefOptions returns [EObject current=null] : (this_AttrValueDef_0= ruleAttrValueDef | this_AttrFullDef_1= ruleAttrFullDef ) ;
   public final EObject ruleAttrDefOptions() throws RecognitionException {
      EObject current = null;

      EObject this_AttrValueDef_0 = null;

      EObject this_AttrFullDef_1 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:500:28: ( (this_AttrValueDef_0= ruleAttrValueDef | this_AttrFullDef_1= ruleAttrFullDef ) )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:501:1: (this_AttrValueDef_0= ruleAttrValueDef | this_AttrFullDef_1= ruleAttrFullDef )
         {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:501:1: (this_AttrValueDef_0= ruleAttrValueDef | this_AttrFullDef_1= ruleAttrFullDef )
            int alt14 = 2;
            int LA14_0 = input.LA(1);

            if (LA14_0 == 23) {
               alt14 = 1;
            } else if (LA14_0 == 17) {
               alt14 = 2;
            } else {
               NoViableAltException nvae = new NoViableAltException("", 14, 0, input);

               throw nvae;
            }
            switch (alt14) {
               case 1:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:502:5: this_AttrValueDef_0= ruleAttrValueDef
               {

                  newCompositeNode(grammarAccess.getAttrDefOptionsAccess().getAttrValueDefParserRuleCall_0());

                  pushFollow(FOLLOW_ruleAttrValueDef_in_ruleAttrDefOptions1017);
                  this_AttrValueDef_0 = ruleAttrValueDef();

                  state._fsp--;

                  current = this_AttrValueDef_0;
                  afterParserOrEnumRuleCall();

               }
                  break;
               case 2:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:512:5: this_AttrFullDef_1= ruleAttrFullDef
               {

                  newCompositeNode(grammarAccess.getAttrDefOptionsAccess().getAttrFullDefParserRuleCall_1());

                  pushFollow(FOLLOW_ruleAttrFullDef_in_ruleAttrDefOptions1044);
                  this_AttrFullDef_1 = ruleAttrFullDef();

                  state._fsp--;

                  current = this_AttrFullDef_1;
                  afterParserOrEnumRuleCall();

               }
                  break;

            }

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleAttrDefOptions"

   // $ANTLR start "entryRuleAttrValueDef"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:528:1: entryRuleAttrValueDef returns [EObject current=null] : iv_ruleAttrValueDef= ruleAttrValueDef EOF ;
   public final EObject entryRuleAttrValueDef() throws RecognitionException {
      EObject current = null;

      EObject iv_ruleAttrValueDef = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:529:2: (iv_ruleAttrValueDef= ruleAttrValueDef EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:530:2: iv_ruleAttrValueDef= ruleAttrValueDef EOF
         {
            newCompositeNode(grammarAccess.getAttrValueDefRule());
            pushFollow(FOLLOW_ruleAttrValueDef_in_entryRuleAttrValueDef1079);
            iv_ruleAttrValueDef = ruleAttrValueDef();

            state._fsp--;

            current = iv_ruleAttrValueDef;
            match(input, EOF, FOLLOW_EOF_in_entryRuleAttrValueDef1089);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleAttrValueDef"

   // $ANTLR start "ruleAttrValueDef"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:537:1: ruleAttrValueDef returns [EObject current=null] : (otherlv_0= 'value' ( (lv_value_1_0= RULE_STRING ) ) ) ;
   public final EObject ruleAttrValueDef() throws RecognitionException {
      EObject current = null;

      Token otherlv_0 = null;
      Token lv_value_1_0 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:540:28: ( (otherlv_0= 'value' ( (lv_value_1_0= RULE_STRING ) ) ) )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:541:1: (otherlv_0= 'value' ( (lv_value_1_0= RULE_STRING ) ) )
         {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:541:1: (otherlv_0= 'value' ( (lv_value_1_0= RULE_STRING ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:541:3: otherlv_0= 'value' ( (lv_value_1_0= RULE_STRING ) )
            {
               otherlv_0 = (Token) match(input, 23, FOLLOW_23_in_ruleAttrValueDef1126);

               newLeafNode(otherlv_0, grammarAccess.getAttrValueDefAccess().getValueKeyword_0());

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:545:1: ( (lv_value_1_0= RULE_STRING ) )
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:546:1: (lv_value_1_0= RULE_STRING )
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:546:1: (lv_value_1_0= RULE_STRING )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:547:3: lv_value_1_0= RULE_STRING
                  {
                     lv_value_1_0 = (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleAttrValueDef1143);

                     newLeafNode(lv_value_1_0,
                        grammarAccess.getAttrValueDefAccess().getValueSTRINGTerminalRuleCall_1_0());

                     if (current == null) {
                        current = createModelElement(grammarAccess.getAttrValueDefRule());
                     }
                     setWithLastConsumed(current, "value", lv_value_1_0, "STRING");

                  }

               }

            }

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleAttrValueDef"

   // $ANTLR start "entryRuleAttrFullDef"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:571:1: entryRuleAttrFullDef returns [EObject current=null] : iv_ruleAttrFullDef= ruleAttrFullDef EOF ;
   public final EObject entryRuleAttrFullDef() throws RecognitionException {
      EObject current = null;

      EObject iv_ruleAttrFullDef = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:572:2: (iv_ruleAttrFullDef= ruleAttrFullDef EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:573:2: iv_ruleAttrFullDef= ruleAttrFullDef EOF
         {
            newCompositeNode(grammarAccess.getAttrFullDefRule());
            pushFollow(FOLLOW_ruleAttrFullDef_in_entryRuleAttrFullDef1184);
            iv_ruleAttrFullDef = ruleAttrFullDef();

            state._fsp--;

            current = iv_ruleAttrFullDef;
            match(input, EOF, FOLLOW_EOF_in_entryRuleAttrFullDef1194);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleAttrFullDef"

   // $ANTLR start "ruleAttrFullDef"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:580:1: ruleAttrFullDef returns [EObject current=null] : (otherlv_0= '{' (otherlv_1= 'uuid' ( (lv_uuid_2_0= RULE_STRING ) ) )? (otherlv_3= 'value' ( (lv_values_4_0= RULE_STRING ) ) )+ otherlv_5= '}' ) ;
   public final EObject ruleAttrFullDef() throws RecognitionException {
      EObject current = null;

      Token otherlv_0 = null;
      Token otherlv_1 = null;
      Token lv_uuid_2_0 = null;
      Token otherlv_3 = null;
      Token lv_values_4_0 = null;
      Token otherlv_5 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:583:28: ( (otherlv_0= '{' (otherlv_1= 'uuid' ( (lv_uuid_2_0= RULE_STRING ) ) )? (otherlv_3= 'value' ( (lv_values_4_0= RULE_STRING ) ) )+ otherlv_5= '}' ) )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:584:1: (otherlv_0= '{' (otherlv_1= 'uuid' ( (lv_uuid_2_0= RULE_STRING ) ) )? (otherlv_3= 'value' ( (lv_values_4_0= RULE_STRING ) ) )+ otherlv_5= '}' )
         {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:584:1: (otherlv_0= '{' (otherlv_1= 'uuid' ( (lv_uuid_2_0= RULE_STRING ) ) )? (otherlv_3= 'value' ( (lv_values_4_0= RULE_STRING ) ) )+ otherlv_5= '}' )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:584:3: otherlv_0= '{' (otherlv_1= 'uuid' ( (lv_uuid_2_0= RULE_STRING ) ) )? (otherlv_3= 'value' ( (lv_values_4_0= RULE_STRING ) ) )+ otherlv_5= '}'
            {
               otherlv_0 = (Token) match(input, 17, FOLLOW_17_in_ruleAttrFullDef1231);

               newLeafNode(otherlv_0, grammarAccess.getAttrFullDefAccess().getLeftCurlyBracketKeyword_0());

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:588:1: (otherlv_1= 'uuid' ( (lv_uuid_2_0= RULE_STRING ) ) )?
               int alt15 = 2;
               int LA15_0 = input.LA(1);

               if (LA15_0 == 24) {
                  alt15 = 1;
               }
               switch (alt15) {
                  case 1:
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:588:3: otherlv_1= 'uuid' ( (lv_uuid_2_0= RULE_STRING ) )
                  {
                     otherlv_1 = (Token) match(input, 24, FOLLOW_24_in_ruleAttrFullDef1244);

                     newLeafNode(otherlv_1, grammarAccess.getAttrFullDefAccess().getUuidKeyword_1_0());

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:592:1: ( (lv_uuid_2_0= RULE_STRING ) )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:593:1: (lv_uuid_2_0= RULE_STRING )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:593:1: (lv_uuid_2_0= RULE_STRING )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:594:3: lv_uuid_2_0= RULE_STRING
                        {
                           lv_uuid_2_0 = (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleAttrFullDef1261);

                           newLeafNode(lv_uuid_2_0,
                              grammarAccess.getAttrFullDefAccess().getUuidSTRINGTerminalRuleCall_1_1_0());

                           if (current == null) {
                              current = createModelElement(grammarAccess.getAttrFullDefRule());
                           }
                           setWithLastConsumed(current, "uuid", lv_uuid_2_0, "STRING");

                        }

                     }

                  }
                     break;

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:610:4: (otherlv_3= 'value' ( (lv_values_4_0= RULE_STRING ) ) )+
               int cnt16 = 0;
               loop16: do {
                  int alt16 = 2;
                  int LA16_0 = input.LA(1);

                  if (LA16_0 == 23) {
                     alt16 = 1;
                  }

                  switch (alt16) {
                     case 1:
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:610:6: otherlv_3= 'value' ( (lv_values_4_0= RULE_STRING ) )
                     {
                        otherlv_3 = (Token) match(input, 23, FOLLOW_23_in_ruleAttrFullDef1281);

                        newLeafNode(otherlv_3, grammarAccess.getAttrFullDefAccess().getValueKeyword_2_0());

                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:614:1: ( (lv_values_4_0= RULE_STRING ) )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:615:1: (lv_values_4_0= RULE_STRING )
                        {
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:615:1: (lv_values_4_0= RULE_STRING )
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:616:3: lv_values_4_0= RULE_STRING
                           {
                              lv_values_4_0 =
                                 (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleAttrFullDef1298);

                              newLeafNode(lv_values_4_0,
                                 grammarAccess.getAttrFullDefAccess().getValuesSTRINGTerminalRuleCall_2_1_0());

                              if (current == null) {
                                 current = createModelElement(grammarAccess.getAttrFullDefRule());
                              }
                              addWithLastConsumed(current, "values", lv_values_4_0, "STRING");

                           }

                        }

                     }
                        break;

                     default:
                        if (cnt16 >= 1) {
                           break loop16;
                        }
                        EarlyExitException eee = new EarlyExitException(16, input);
                        throw eee;
                  }
                  cnt16++;
               } while (true);

               otherlv_5 = (Token) match(input, 22, FOLLOW_22_in_ruleAttrFullDef1317);

               newLeafNode(otherlv_5, grammarAccess.getAttrFullDefAccess().getRightCurlyBracketKeyword_3());

            }

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleAttrFullDef"

   // $ANTLR start "entryRulePROGRAM_REFERENCE"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:644:1: entryRulePROGRAM_REFERENCE returns [String current=null] : iv_rulePROGRAM_REFERENCE= rulePROGRAM_REFERENCE EOF ;
   public final String entryRulePROGRAM_REFERENCE() throws RecognitionException {
      String current = null;

      AntlrDatatypeRuleToken iv_rulePROGRAM_REFERENCE = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:645:2: (iv_rulePROGRAM_REFERENCE= rulePROGRAM_REFERENCE EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:646:2: iv_rulePROGRAM_REFERENCE= rulePROGRAM_REFERENCE EOF
         {
            newCompositeNode(grammarAccess.getPROGRAM_REFERENCERule());
            pushFollow(FOLLOW_rulePROGRAM_REFERENCE_in_entryRulePROGRAM_REFERENCE1354);
            iv_rulePROGRAM_REFERENCE = rulePROGRAM_REFERENCE();

            state._fsp--;

            current = iv_rulePROGRAM_REFERENCE.getText();
            match(input, EOF, FOLLOW_EOF_in_entryRulePROGRAM_REFERENCE1365);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRulePROGRAM_REFERENCE"

   // $ANTLR start "rulePROGRAM_REFERENCE"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:653:1: rulePROGRAM_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
   public final AntlrDatatypeRuleToken rulePROGRAM_REFERENCE() throws RecognitionException {
      AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

      Token this_STRING_0 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:656:28: (this_STRING_0= RULE_STRING )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:657:5: this_STRING_0= RULE_STRING
         {
            this_STRING_0 = (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_rulePROGRAM_REFERENCE1404);

            current.merge(this_STRING_0);

            newLeafNode(this_STRING_0, grammarAccess.getPROGRAM_REFERENCEAccess().getSTRINGTerminalRuleCall());

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "rulePROGRAM_REFERENCE"

   // $ANTLR start "entryRuleProgramDef"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:672:1: entryRuleProgramDef returns [EObject current=null] : iv_ruleProgramDef= ruleProgramDef EOF ;
   public final EObject entryRuleProgramDef() throws RecognitionException {
      EObject current = null;

      EObject iv_ruleProgramDef = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:673:2: (iv_ruleProgramDef= ruleProgramDef EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:674:2: iv_ruleProgramDef= ruleProgramDef EOF
         {
            newCompositeNode(grammarAccess.getProgramDefRule());
            pushFollow(FOLLOW_ruleProgramDef_in_entryRuleProgramDef1448);
            iv_ruleProgramDef = ruleProgramDef();

            state._fsp--;

            current = iv_ruleProgramDef;
            match(input, EOF, FOLLOW_EOF_in_entryRuleProgramDef1458);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleProgramDef"

   // $ANTLR start "ruleProgramDef"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:681:1: ruleProgramDef returns [EObject current=null] : ( ( (lv_name_0_0= rulePROGRAM_REFERENCE ) ) ( (lv_programDefOption_1_0= ruleProgramDefOption ) )* otherlv_2= '{' (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )? (otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_INT ) ) )? (otherlv_7= 'artifactType' ( (lv_artifactTypeName_8_0= RULE_STRING ) ) )? (otherlv_9= 'active' ( (lv_active_10_0= ruleBooleanDef ) ) )? (otherlv_11= 'namespace' ( (lv_namespace_12_0= RULE_STRING ) ) )* (otherlv_13= 'teamDefinition' ( (lv_teamDefinition_14_0= ruleTEAM_DEF_REFERENCE ) ) )* (otherlv_15= 'attribute' ( (lv_attributes_16_0= ruleAttrDef ) ) )* otherlv_17= '}' ) ;
   public final EObject ruleProgramDef() throws RecognitionException {
      EObject current = null;

      Token otherlv_2 = null;
      Token otherlv_3 = null;
      Token lv_guid_4_0 = null;
      Token otherlv_5 = null;
      Token lv_uuid_6_0 = null;
      Token otherlv_7 = null;
      Token lv_artifactTypeName_8_0 = null;
      Token otherlv_9 = null;
      Token otherlv_11 = null;
      Token lv_namespace_12_0 = null;
      Token otherlv_13 = null;
      Token otherlv_15 = null;
      Token otherlv_17 = null;
      AntlrDatatypeRuleToken lv_name_0_0 = null;

      AntlrDatatypeRuleToken lv_programDefOption_1_0 = null;

      Enumerator lv_active_10_0 = null;

      AntlrDatatypeRuleToken lv_teamDefinition_14_0 = null;

      EObject lv_attributes_16_0 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:684:28: ( ( ( (lv_name_0_0= rulePROGRAM_REFERENCE ) ) ( (lv_programDefOption_1_0= ruleProgramDefOption ) )* otherlv_2= '{' (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )? (otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_INT ) ) )? (otherlv_7= 'artifactType' ( (lv_artifactTypeName_8_0= RULE_STRING ) ) )? (otherlv_9= 'active' ( (lv_active_10_0= ruleBooleanDef ) ) )? (otherlv_11= 'namespace' ( (lv_namespace_12_0= RULE_STRING ) ) )* (otherlv_13= 'teamDefinition' ( (lv_teamDefinition_14_0= ruleTEAM_DEF_REFERENCE ) ) )* (otherlv_15= 'attribute' ( (lv_attributes_16_0= ruleAttrDef ) ) )* otherlv_17= '}' ) )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:685:1: ( ( (lv_name_0_0= rulePROGRAM_REFERENCE ) ) ( (lv_programDefOption_1_0= ruleProgramDefOption ) )* otherlv_2= '{' (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )? (otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_INT ) ) )? (otherlv_7= 'artifactType' ( (lv_artifactTypeName_8_0= RULE_STRING ) ) )? (otherlv_9= 'active' ( (lv_active_10_0= ruleBooleanDef ) ) )? (otherlv_11= 'namespace' ( (lv_namespace_12_0= RULE_STRING ) ) )* (otherlv_13= 'teamDefinition' ( (lv_teamDefinition_14_0= ruleTEAM_DEF_REFERENCE ) ) )* (otherlv_15= 'attribute' ( (lv_attributes_16_0= ruleAttrDef ) ) )* otherlv_17= '}' )
         {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:685:1: ( ( (lv_name_0_0= rulePROGRAM_REFERENCE ) ) ( (lv_programDefOption_1_0= ruleProgramDefOption ) )* otherlv_2= '{' (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )? (otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_INT ) ) )? (otherlv_7= 'artifactType' ( (lv_artifactTypeName_8_0= RULE_STRING ) ) )? (otherlv_9= 'active' ( (lv_active_10_0= ruleBooleanDef ) ) )? (otherlv_11= 'namespace' ( (lv_namespace_12_0= RULE_STRING ) ) )* (otherlv_13= 'teamDefinition' ( (lv_teamDefinition_14_0= ruleTEAM_DEF_REFERENCE ) ) )* (otherlv_15= 'attribute' ( (lv_attributes_16_0= ruleAttrDef ) ) )* otherlv_17= '}' )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:685:2: ( (lv_name_0_0= rulePROGRAM_REFERENCE ) ) ( (lv_programDefOption_1_0= ruleProgramDefOption ) )* otherlv_2= '{' (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )? (otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_INT ) ) )? (otherlv_7= 'artifactType' ( (lv_artifactTypeName_8_0= RULE_STRING ) ) )? (otherlv_9= 'active' ( (lv_active_10_0= ruleBooleanDef ) ) )? (otherlv_11= 'namespace' ( (lv_namespace_12_0= RULE_STRING ) ) )* (otherlv_13= 'teamDefinition' ( (lv_teamDefinition_14_0= ruleTEAM_DEF_REFERENCE ) ) )* (otherlv_15= 'attribute' ( (lv_attributes_16_0= ruleAttrDef ) ) )* otherlv_17= '}'
            {
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:685:2: ( (lv_name_0_0= rulePROGRAM_REFERENCE ) )
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:686:1: (lv_name_0_0= rulePROGRAM_REFERENCE )
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:686:1: (lv_name_0_0= rulePROGRAM_REFERENCE )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:687:3: lv_name_0_0= rulePROGRAM_REFERENCE
                  {

                     newCompositeNode(grammarAccess.getProgramDefAccess().getNamePROGRAM_REFERENCEParserRuleCall_0_0());

                     pushFollow(FOLLOW_rulePROGRAM_REFERENCE_in_ruleProgramDef1504);
                     lv_name_0_0 = rulePROGRAM_REFERENCE();

                     state._fsp--;

                     if (current == null) {
                        current = createModelElementForParent(grammarAccess.getProgramDefRule());
                     }
                     set(current, "name", lv_name_0_0, "PROGRAM_REFERENCE");
                     afterParserOrEnumRuleCall();

                  }

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:703:2: ( (lv_programDefOption_1_0= ruleProgramDefOption ) )*
               loop17: do {
                  int alt17 = 2;
                  int LA17_0 = input.LA(1);

                  if (LA17_0 == RULE_STRING || LA17_0 == 85) {
                     alt17 = 1;
                  }

                  switch (alt17) {
                     case 1:
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:704:1: (lv_programDefOption_1_0= ruleProgramDefOption )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:704:1: (lv_programDefOption_1_0= ruleProgramDefOption )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:705:3: lv_programDefOption_1_0= ruleProgramDefOption
                        {

                           newCompositeNode(
                              grammarAccess.getProgramDefAccess().getProgramDefOptionProgramDefOptionParserRuleCall_1_0());

                           pushFollow(FOLLOW_ruleProgramDefOption_in_ruleProgramDef1525);
                           lv_programDefOption_1_0 = ruleProgramDefOption();

                           state._fsp--;

                           if (current == null) {
                              current = createModelElementForParent(grammarAccess.getProgramDefRule());
                           }
                           add(current, "programDefOption", lv_programDefOption_1_0, "ProgramDefOption");
                           afterParserOrEnumRuleCall();

                        }

                     }
                        break;

                     default:
                        break loop17;
                  }
               } while (true);

               otherlv_2 = (Token) match(input, 17, FOLLOW_17_in_ruleProgramDef1538);

               newLeafNode(otherlv_2, grammarAccess.getProgramDefAccess().getLeftCurlyBracketKeyword_2());

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:725:1: (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )?
               int alt18 = 2;
               int LA18_0 = input.LA(1);

               if (LA18_0 == 25) {
                  alt18 = 1;
               }
               switch (alt18) {
                  case 1:
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:725:3: otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) )
                  {
                     otherlv_3 = (Token) match(input, 25, FOLLOW_25_in_ruleProgramDef1551);

                     newLeafNode(otherlv_3, grammarAccess.getProgramDefAccess().getGuidKeyword_3_0());

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:729:1: ( (lv_guid_4_0= RULE_STRING ) )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:730:1: (lv_guid_4_0= RULE_STRING )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:730:1: (lv_guid_4_0= RULE_STRING )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:731:3: lv_guid_4_0= RULE_STRING
                        {
                           lv_guid_4_0 = (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleProgramDef1568);

                           newLeafNode(lv_guid_4_0,
                              grammarAccess.getProgramDefAccess().getGuidSTRINGTerminalRuleCall_3_1_0());

                           if (current == null) {
                              current = createModelElement(grammarAccess.getProgramDefRule());
                           }
                           setWithLastConsumed(current, "guid", lv_guid_4_0, "STRING");

                        }

                     }

                  }
                     break;

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:747:4: (otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_INT ) ) )?
               int alt19 = 2;
               int LA19_0 = input.LA(1);

               if (LA19_0 == 24) {
                  alt19 = 1;
               }
               switch (alt19) {
                  case 1:
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:747:6: otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_INT ) )
                  {
                     otherlv_5 = (Token) match(input, 24, FOLLOW_24_in_ruleProgramDef1588);

                     newLeafNode(otherlv_5, grammarAccess.getProgramDefAccess().getUuidKeyword_4_0());

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:751:1: ( (lv_uuid_6_0= RULE_INT ) )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:752:1: (lv_uuid_6_0= RULE_INT )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:752:1: (lv_uuid_6_0= RULE_INT )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:753:3: lv_uuid_6_0= RULE_INT
                        {
                           lv_uuid_6_0 = (Token) match(input, RULE_INT, FOLLOW_RULE_INT_in_ruleProgramDef1605);

                           newLeafNode(lv_uuid_6_0,
                              grammarAccess.getProgramDefAccess().getUuidINTTerminalRuleCall_4_1_0());

                           if (current == null) {
                              current = createModelElement(grammarAccess.getProgramDefRule());
                           }
                           setWithLastConsumed(current, "uuid", lv_uuid_6_0, "INT");

                        }

                     }

                  }
                     break;

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:769:4: (otherlv_7= 'artifactType' ( (lv_artifactTypeName_8_0= RULE_STRING ) ) )?
               int alt20 = 2;
               int LA20_0 = input.LA(1);

               if (LA20_0 == 26) {
                  alt20 = 1;
               }
               switch (alt20) {
                  case 1:
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:769:6: otherlv_7= 'artifactType' ( (lv_artifactTypeName_8_0= RULE_STRING ) )
                  {
                     otherlv_7 = (Token) match(input, 26, FOLLOW_26_in_ruleProgramDef1625);

                     newLeafNode(otherlv_7, grammarAccess.getProgramDefAccess().getArtifactTypeKeyword_5_0());

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:773:1: ( (lv_artifactTypeName_8_0= RULE_STRING ) )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:774:1: (lv_artifactTypeName_8_0= RULE_STRING )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:774:1: (lv_artifactTypeName_8_0= RULE_STRING )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:775:3: lv_artifactTypeName_8_0= RULE_STRING
                        {
                           lv_artifactTypeName_8_0 =
                              (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleProgramDef1642);

                           newLeafNode(lv_artifactTypeName_8_0,
                              grammarAccess.getProgramDefAccess().getArtifactTypeNameSTRINGTerminalRuleCall_5_1_0());

                           if (current == null) {
                              current = createModelElement(grammarAccess.getProgramDefRule());
                           }
                           setWithLastConsumed(current, "artifactTypeName", lv_artifactTypeName_8_0, "STRING");

                        }

                     }

                  }
                     break;

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:791:4: (otherlv_9= 'active' ( (lv_active_10_0= ruleBooleanDef ) ) )?
               int alt21 = 2;
               int LA21_0 = input.LA(1);

               if (LA21_0 == 18) {
                  alt21 = 1;
               }
               switch (alt21) {
                  case 1:
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:791:6: otherlv_9= 'active' ( (lv_active_10_0= ruleBooleanDef ) )
                  {
                     otherlv_9 = (Token) match(input, 18, FOLLOW_18_in_ruleProgramDef1662);

                     newLeafNode(otherlv_9, grammarAccess.getProgramDefAccess().getActiveKeyword_6_0());

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:795:1: ( (lv_active_10_0= ruleBooleanDef ) )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:796:1: (lv_active_10_0= ruleBooleanDef )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:796:1: (lv_active_10_0= ruleBooleanDef )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:797:3: lv_active_10_0= ruleBooleanDef
                        {

                           newCompositeNode(
                              grammarAccess.getProgramDefAccess().getActiveBooleanDefEnumRuleCall_6_1_0());

                           pushFollow(FOLLOW_ruleBooleanDef_in_ruleProgramDef1683);
                           lv_active_10_0 = ruleBooleanDef();

                           state._fsp--;

                           if (current == null) {
                              current = createModelElementForParent(grammarAccess.getProgramDefRule());
                           }
                           set(current, "active", lv_active_10_0, "BooleanDef");
                           afterParserOrEnumRuleCall();

                        }

                     }

                  }
                     break;

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:813:4: (otherlv_11= 'namespace' ( (lv_namespace_12_0= RULE_STRING ) ) )*
               loop22: do {
                  int alt22 = 2;
                  int LA22_0 = input.LA(1);

                  if (LA22_0 == 27) {
                     alt22 = 1;
                  }

                  switch (alt22) {
                     case 1:
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:813:6: otherlv_11= 'namespace' ( (lv_namespace_12_0= RULE_STRING ) )
                     {
                        otherlv_11 = (Token) match(input, 27, FOLLOW_27_in_ruleProgramDef1698);

                        newLeafNode(otherlv_11, grammarAccess.getProgramDefAccess().getNamespaceKeyword_7_0());

                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:817:1: ( (lv_namespace_12_0= RULE_STRING ) )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:818:1: (lv_namespace_12_0= RULE_STRING )
                        {
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:818:1: (lv_namespace_12_0= RULE_STRING )
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:819:3: lv_namespace_12_0= RULE_STRING
                           {
                              lv_namespace_12_0 =
                                 (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleProgramDef1715);

                              newLeafNode(lv_namespace_12_0,
                                 grammarAccess.getProgramDefAccess().getNamespaceSTRINGTerminalRuleCall_7_1_0());

                              if (current == null) {
                                 current = createModelElement(grammarAccess.getProgramDefRule());
                              }
                              setWithLastConsumed(current, "namespace", lv_namespace_12_0, "STRING");

                           }

                        }

                     }
                        break;

                     default:
                        break loop22;
                  }
               } while (true);

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:835:4: (otherlv_13= 'teamDefinition' ( (lv_teamDefinition_14_0= ruleTEAM_DEF_REFERENCE ) ) )*
               loop23: do {
                  int alt23 = 2;
                  int LA23_0 = input.LA(1);

                  if (LA23_0 == 12) {
                     alt23 = 1;
                  }

                  switch (alt23) {
                     case 1:
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:835:6: otherlv_13= 'teamDefinition' ( (lv_teamDefinition_14_0= ruleTEAM_DEF_REFERENCE ) )
                     {
                        otherlv_13 = (Token) match(input, 12, FOLLOW_12_in_ruleProgramDef1735);

                        newLeafNode(otherlv_13, grammarAccess.getProgramDefAccess().getTeamDefinitionKeyword_8_0());

                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:839:1: ( (lv_teamDefinition_14_0= ruleTEAM_DEF_REFERENCE ) )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:840:1: (lv_teamDefinition_14_0= ruleTEAM_DEF_REFERENCE )
                        {
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:840:1: (lv_teamDefinition_14_0= ruleTEAM_DEF_REFERENCE )
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:841:3: lv_teamDefinition_14_0= ruleTEAM_DEF_REFERENCE
                           {

                              newCompositeNode(
                                 grammarAccess.getProgramDefAccess().getTeamDefinitionTEAM_DEF_REFERENCEParserRuleCall_8_1_0());

                              pushFollow(FOLLOW_ruleTEAM_DEF_REFERENCE_in_ruleProgramDef1756);
                              lv_teamDefinition_14_0 = ruleTEAM_DEF_REFERENCE();

                              state._fsp--;

                              if (current == null) {
                                 current = createModelElementForParent(grammarAccess.getProgramDefRule());
                              }
                              set(current, "teamDefinition", lv_teamDefinition_14_0, "TEAM_DEF_REFERENCE");
                              afterParserOrEnumRuleCall();

                           }

                        }

                     }
                        break;

                     default:
                        break loop23;
                  }
               } while (true);

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:857:4: (otherlv_15= 'attribute' ( (lv_attributes_16_0= ruleAttrDef ) ) )*
               loop24: do {
                  int alt24 = 2;
                  int LA24_0 = input.LA(1);

                  if (LA24_0 == 28) {
                     alt24 = 1;
                  }

                  switch (alt24) {
                     case 1:
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:857:6: otherlv_15= 'attribute' ( (lv_attributes_16_0= ruleAttrDef ) )
                     {
                        otherlv_15 = (Token) match(input, 28, FOLLOW_28_in_ruleProgramDef1771);

                        newLeafNode(otherlv_15, grammarAccess.getProgramDefAccess().getAttributeKeyword_9_0());

                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:861:1: ( (lv_attributes_16_0= ruleAttrDef ) )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:862:1: (lv_attributes_16_0= ruleAttrDef )
                        {
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:862:1: (lv_attributes_16_0= ruleAttrDef )
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:863:3: lv_attributes_16_0= ruleAttrDef
                           {

                              newCompositeNode(
                                 grammarAccess.getProgramDefAccess().getAttributesAttrDefParserRuleCall_9_1_0());

                              pushFollow(FOLLOW_ruleAttrDef_in_ruleProgramDef1792);
                              lv_attributes_16_0 = ruleAttrDef();

                              state._fsp--;

                              if (current == null) {
                                 current = createModelElementForParent(grammarAccess.getProgramDefRule());
                              }
                              add(current, "attributes", lv_attributes_16_0, "AttrDef");
                              afterParserOrEnumRuleCall();

                           }

                        }

                     }
                        break;

                     default:
                        break loop24;
                  }
               } while (true);

               otherlv_17 = (Token) match(input, 22, FOLLOW_22_in_ruleProgramDef1806);

               newLeafNode(otherlv_17, grammarAccess.getProgramDefAccess().getRightCurlyBracketKeyword_10());

            }

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleProgramDef"

   // $ANTLR start "entryRuleTEAM_DEF_REFERENCE"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:891:1: entryRuleTEAM_DEF_REFERENCE returns [String current=null] : iv_ruleTEAM_DEF_REFERENCE= ruleTEAM_DEF_REFERENCE EOF ;
   public final String entryRuleTEAM_DEF_REFERENCE() throws RecognitionException {
      String current = null;

      AntlrDatatypeRuleToken iv_ruleTEAM_DEF_REFERENCE = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:892:2: (iv_ruleTEAM_DEF_REFERENCE= ruleTEAM_DEF_REFERENCE EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:893:2: iv_ruleTEAM_DEF_REFERENCE= ruleTEAM_DEF_REFERENCE EOF
         {
            newCompositeNode(grammarAccess.getTEAM_DEF_REFERENCERule());
            pushFollow(FOLLOW_ruleTEAM_DEF_REFERENCE_in_entryRuleTEAM_DEF_REFERENCE1843);
            iv_ruleTEAM_DEF_REFERENCE = ruleTEAM_DEF_REFERENCE();

            state._fsp--;

            current = iv_ruleTEAM_DEF_REFERENCE.getText();
            match(input, EOF, FOLLOW_EOF_in_entryRuleTEAM_DEF_REFERENCE1854);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleTEAM_DEF_REFERENCE"

   // $ANTLR start "ruleTEAM_DEF_REFERENCE"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:900:1: ruleTEAM_DEF_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
   public final AntlrDatatypeRuleToken ruleTEAM_DEF_REFERENCE() throws RecognitionException {
      AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

      Token this_STRING_0 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:903:28: (this_STRING_0= RULE_STRING )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:904:5: this_STRING_0= RULE_STRING
         {
            this_STRING_0 = (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleTEAM_DEF_REFERENCE1893);

            current.merge(this_STRING_0);

            newLeafNode(this_STRING_0, grammarAccess.getTEAM_DEF_REFERENCEAccess().getSTRINGTerminalRuleCall());

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleTEAM_DEF_REFERENCE"

   // $ANTLR start "entryRuleTeamDef"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:919:1: entryRuleTeamDef returns [EObject current=null] : iv_ruleTeamDef= ruleTeamDef EOF ;
   public final EObject entryRuleTeamDef() throws RecognitionException {
      EObject current = null;

      EObject iv_ruleTeamDef = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:920:2: (iv_ruleTeamDef= ruleTeamDef EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:921:2: iv_ruleTeamDef= ruleTeamDef EOF
         {
            newCompositeNode(grammarAccess.getTeamDefRule());
            pushFollow(FOLLOW_ruleTeamDef_in_entryRuleTeamDef1937);
            iv_ruleTeamDef = ruleTeamDef();

            state._fsp--;

            current = iv_ruleTeamDef;
            match(input, EOF, FOLLOW_EOF_in_entryRuleTeamDef1947);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleTeamDef"

   // $ANTLR start "ruleTeamDef"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:928:1: ruleTeamDef returns [EObject current=null] : ( ( (lv_name_0_0= ruleTEAM_DEF_REFERENCE ) ) ( (lv_teamDefOption_1_0= ruleTeamDefOption ) )* otherlv_2= '{' (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )? (otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_INT ) ) )? (otherlv_7= 'active' ( (lv_active_8_0= ruleBooleanDef ) ) )? (otherlv_9= 'staticId' ( (lv_staticId_10_0= RULE_STRING ) ) )* (otherlv_11= 'lead' ( (lv_lead_12_0= ruleUserRef ) ) )* (otherlv_13= 'member' ( (lv_member_14_0= ruleUserRef ) ) )* (otherlv_15= 'privileged' ( (lv_privileged_16_0= ruleUserRef ) ) )* (otherlv_17= 'workDefinition' ( (lv_workDefinition_18_0= RULE_STRING ) ) )? (otherlv_19= 'relatedTaskWorkDefinition' ( (lv_relatedTaskWorkDefinition_20_0= RULE_STRING ) ) )? (otherlv_21= 'teamWorkflowArtifactType' ( (lv_teamWorkflowArtifactType_22_0= RULE_STRING ) ) )? (otherlv_23= 'accessContextId' ( (lv_accessContextId_24_0= RULE_STRING ) ) )* (otherlv_25= 'version' ( (lv_version_26_0= ruleVersionDef ) ) )* (otherlv_27= 'rule' ( (lv_rules_28_0= ruleRuleRef ) ) )* (otherlv_29= 'children' otherlv_30= '{' (otherlv_31= 'teamDefinition' ( (lv_children_32_0= ruleTeamDef ) ) )+ otherlv_33= '}' )? otherlv_34= '}' ) ;
   public final EObject ruleTeamDef() throws RecognitionException {
      EObject current = null;

      Token otherlv_2 = null;
      Token otherlv_3 = null;
      Token lv_guid_4_0 = null;
      Token otherlv_5 = null;
      Token lv_uuid_6_0 = null;
      Token otherlv_7 = null;
      Token otherlv_9 = null;
      Token lv_staticId_10_0 = null;
      Token otherlv_11 = null;
      Token otherlv_13 = null;
      Token otherlv_15 = null;
      Token otherlv_17 = null;
      Token lv_workDefinition_18_0 = null;
      Token otherlv_19 = null;
      Token lv_relatedTaskWorkDefinition_20_0 = null;
      Token otherlv_21 = null;
      Token lv_teamWorkflowArtifactType_22_0 = null;
      Token otherlv_23 = null;
      Token lv_accessContextId_24_0 = null;
      Token otherlv_25 = null;
      Token otherlv_27 = null;
      Token otherlv_29 = null;
      Token otherlv_30 = null;
      Token otherlv_31 = null;
      Token otherlv_33 = null;
      Token otherlv_34 = null;
      AntlrDatatypeRuleToken lv_name_0_0 = null;

      AntlrDatatypeRuleToken lv_teamDefOption_1_0 = null;

      Enumerator lv_active_8_0 = null;

      EObject lv_lead_12_0 = null;

      EObject lv_member_14_0 = null;

      EObject lv_privileged_16_0 = null;

      EObject lv_version_26_0 = null;

      AntlrDatatypeRuleToken lv_rules_28_0 = null;

      EObject lv_children_32_0 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:931:28: ( ( ( (lv_name_0_0= ruleTEAM_DEF_REFERENCE ) ) ( (lv_teamDefOption_1_0= ruleTeamDefOption ) )* otherlv_2= '{' (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )? (otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_INT ) ) )? (otherlv_7= 'active' ( (lv_active_8_0= ruleBooleanDef ) ) )? (otherlv_9= 'staticId' ( (lv_staticId_10_0= RULE_STRING ) ) )* (otherlv_11= 'lead' ( (lv_lead_12_0= ruleUserRef ) ) )* (otherlv_13= 'member' ( (lv_member_14_0= ruleUserRef ) ) )* (otherlv_15= 'privileged' ( (lv_privileged_16_0= ruleUserRef ) ) )* (otherlv_17= 'workDefinition' ( (lv_workDefinition_18_0= RULE_STRING ) ) )? (otherlv_19= 'relatedTaskWorkDefinition' ( (lv_relatedTaskWorkDefinition_20_0= RULE_STRING ) ) )? (otherlv_21= 'teamWorkflowArtifactType' ( (lv_teamWorkflowArtifactType_22_0= RULE_STRING ) ) )? (otherlv_23= 'accessContextId' ( (lv_accessContextId_24_0= RULE_STRING ) ) )* (otherlv_25= 'version' ( (lv_version_26_0= ruleVersionDef ) ) )* (otherlv_27= 'rule' ( (lv_rules_28_0= ruleRuleRef ) ) )* (otherlv_29= 'children' otherlv_30= '{' (otherlv_31= 'teamDefinition' ( (lv_children_32_0= ruleTeamDef ) ) )+ otherlv_33= '}' )? otherlv_34= '}' ) )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:932:1: ( ( (lv_name_0_0= ruleTEAM_DEF_REFERENCE ) ) ( (lv_teamDefOption_1_0= ruleTeamDefOption ) )* otherlv_2= '{' (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )? (otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_INT ) ) )? (otherlv_7= 'active' ( (lv_active_8_0= ruleBooleanDef ) ) )? (otherlv_9= 'staticId' ( (lv_staticId_10_0= RULE_STRING ) ) )* (otherlv_11= 'lead' ( (lv_lead_12_0= ruleUserRef ) ) )* (otherlv_13= 'member' ( (lv_member_14_0= ruleUserRef ) ) )* (otherlv_15= 'privileged' ( (lv_privileged_16_0= ruleUserRef ) ) )* (otherlv_17= 'workDefinition' ( (lv_workDefinition_18_0= RULE_STRING ) ) )? (otherlv_19= 'relatedTaskWorkDefinition' ( (lv_relatedTaskWorkDefinition_20_0= RULE_STRING ) ) )? (otherlv_21= 'teamWorkflowArtifactType' ( (lv_teamWorkflowArtifactType_22_0= RULE_STRING ) ) )? (otherlv_23= 'accessContextId' ( (lv_accessContextId_24_0= RULE_STRING ) ) )* (otherlv_25= 'version' ( (lv_version_26_0= ruleVersionDef ) ) )* (otherlv_27= 'rule' ( (lv_rules_28_0= ruleRuleRef ) ) )* (otherlv_29= 'children' otherlv_30= '{' (otherlv_31= 'teamDefinition' ( (lv_children_32_0= ruleTeamDef ) ) )+ otherlv_33= '}' )? otherlv_34= '}' )
         {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:932:1: ( ( (lv_name_0_0= ruleTEAM_DEF_REFERENCE ) ) ( (lv_teamDefOption_1_0= ruleTeamDefOption ) )* otherlv_2= '{' (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )? (otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_INT ) ) )? (otherlv_7= 'active' ( (lv_active_8_0= ruleBooleanDef ) ) )? (otherlv_9= 'staticId' ( (lv_staticId_10_0= RULE_STRING ) ) )* (otherlv_11= 'lead' ( (lv_lead_12_0= ruleUserRef ) ) )* (otherlv_13= 'member' ( (lv_member_14_0= ruleUserRef ) ) )* (otherlv_15= 'privileged' ( (lv_privileged_16_0= ruleUserRef ) ) )* (otherlv_17= 'workDefinition' ( (lv_workDefinition_18_0= RULE_STRING ) ) )? (otherlv_19= 'relatedTaskWorkDefinition' ( (lv_relatedTaskWorkDefinition_20_0= RULE_STRING ) ) )? (otherlv_21= 'teamWorkflowArtifactType' ( (lv_teamWorkflowArtifactType_22_0= RULE_STRING ) ) )? (otherlv_23= 'accessContextId' ( (lv_accessContextId_24_0= RULE_STRING ) ) )* (otherlv_25= 'version' ( (lv_version_26_0= ruleVersionDef ) ) )* (otherlv_27= 'rule' ( (lv_rules_28_0= ruleRuleRef ) ) )* (otherlv_29= 'children' otherlv_30= '{' (otherlv_31= 'teamDefinition' ( (lv_children_32_0= ruleTeamDef ) ) )+ otherlv_33= '}' )? otherlv_34= '}' )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:932:2: ( (lv_name_0_0= ruleTEAM_DEF_REFERENCE ) ) ( (lv_teamDefOption_1_0= ruleTeamDefOption ) )* otherlv_2= '{' (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )? (otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_INT ) ) )? (otherlv_7= 'active' ( (lv_active_8_0= ruleBooleanDef ) ) )? (otherlv_9= 'staticId' ( (lv_staticId_10_0= RULE_STRING ) ) )* (otherlv_11= 'lead' ( (lv_lead_12_0= ruleUserRef ) ) )* (otherlv_13= 'member' ( (lv_member_14_0= ruleUserRef ) ) )* (otherlv_15= 'privileged' ( (lv_privileged_16_0= ruleUserRef ) ) )* (otherlv_17= 'workDefinition' ( (lv_workDefinition_18_0= RULE_STRING ) ) )? (otherlv_19= 'relatedTaskWorkDefinition' ( (lv_relatedTaskWorkDefinition_20_0= RULE_STRING ) ) )? (otherlv_21= 'teamWorkflowArtifactType' ( (lv_teamWorkflowArtifactType_22_0= RULE_STRING ) ) )? (otherlv_23= 'accessContextId' ( (lv_accessContextId_24_0= RULE_STRING ) ) )* (otherlv_25= 'version' ( (lv_version_26_0= ruleVersionDef ) ) )* (otherlv_27= 'rule' ( (lv_rules_28_0= ruleRuleRef ) ) )* (otherlv_29= 'children' otherlv_30= '{' (otherlv_31= 'teamDefinition' ( (lv_children_32_0= ruleTeamDef ) ) )+ otherlv_33= '}' )? otherlv_34= '}'
            {
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:932:2: ( (lv_name_0_0= ruleTEAM_DEF_REFERENCE ) )
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:933:1: (lv_name_0_0= ruleTEAM_DEF_REFERENCE )
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:933:1: (lv_name_0_0= ruleTEAM_DEF_REFERENCE )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:934:3: lv_name_0_0= ruleTEAM_DEF_REFERENCE
                  {

                     newCompositeNode(grammarAccess.getTeamDefAccess().getNameTEAM_DEF_REFERENCEParserRuleCall_0_0());

                     pushFollow(FOLLOW_ruleTEAM_DEF_REFERENCE_in_ruleTeamDef1993);
                     lv_name_0_0 = ruleTEAM_DEF_REFERENCE();

                     state._fsp--;

                     if (current == null) {
                        current = createModelElementForParent(grammarAccess.getTeamDefRule());
                     }
                     set(current, "name", lv_name_0_0, "TEAM_DEF_REFERENCE");
                     afterParserOrEnumRuleCall();

                  }

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:950:2: ( (lv_teamDefOption_1_0= ruleTeamDefOption ) )*
               loop25: do {
                  int alt25 = 2;
                  int LA25_0 = input.LA(1);

                  if (LA25_0 == RULE_STRING || LA25_0 == 85) {
                     alt25 = 1;
                  }

                  switch (alt25) {
                     case 1:
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:951:1: (lv_teamDefOption_1_0= ruleTeamDefOption )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:951:1: (lv_teamDefOption_1_0= ruleTeamDefOption )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:952:3: lv_teamDefOption_1_0= ruleTeamDefOption
                        {

                           newCompositeNode(
                              grammarAccess.getTeamDefAccess().getTeamDefOptionTeamDefOptionParserRuleCall_1_0());

                           pushFollow(FOLLOW_ruleTeamDefOption_in_ruleTeamDef2014);
                           lv_teamDefOption_1_0 = ruleTeamDefOption();

                           state._fsp--;

                           if (current == null) {
                              current = createModelElementForParent(grammarAccess.getTeamDefRule());
                           }
                           add(current, "teamDefOption", lv_teamDefOption_1_0, "TeamDefOption");
                           afterParserOrEnumRuleCall();

                        }

                     }
                        break;

                     default:
                        break loop25;
                  }
               } while (true);

               otherlv_2 = (Token) match(input, 17, FOLLOW_17_in_ruleTeamDef2027);

               newLeafNode(otherlv_2, grammarAccess.getTeamDefAccess().getLeftCurlyBracketKeyword_2());

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:972:1: (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )?
               int alt26 = 2;
               int LA26_0 = input.LA(1);

               if (LA26_0 == 25) {
                  alt26 = 1;
               }
               switch (alt26) {
                  case 1:
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:972:3: otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) )
                  {
                     otherlv_3 = (Token) match(input, 25, FOLLOW_25_in_ruleTeamDef2040);

                     newLeafNode(otherlv_3, grammarAccess.getTeamDefAccess().getGuidKeyword_3_0());

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:976:1: ( (lv_guid_4_0= RULE_STRING ) )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:977:1: (lv_guid_4_0= RULE_STRING )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:977:1: (lv_guid_4_0= RULE_STRING )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:978:3: lv_guid_4_0= RULE_STRING
                        {
                           lv_guid_4_0 = (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleTeamDef2057);

                           newLeafNode(lv_guid_4_0,
                              grammarAccess.getTeamDefAccess().getGuidSTRINGTerminalRuleCall_3_1_0());

                           if (current == null) {
                              current = createModelElement(grammarAccess.getTeamDefRule());
                           }
                           setWithLastConsumed(current, "guid", lv_guid_4_0, "STRING");

                        }

                     }

                  }
                     break;

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:994:4: (otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_INT ) ) )?
               int alt27 = 2;
               int LA27_0 = input.LA(1);

               if (LA27_0 == 24) {
                  alt27 = 1;
               }
               switch (alt27) {
                  case 1:
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:994:6: otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_INT ) )
                  {
                     otherlv_5 = (Token) match(input, 24, FOLLOW_24_in_ruleTeamDef2077);

                     newLeafNode(otherlv_5, grammarAccess.getTeamDefAccess().getUuidKeyword_4_0());

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:998:1: ( (lv_uuid_6_0= RULE_INT ) )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:999:1: (lv_uuid_6_0= RULE_INT )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:999:1: (lv_uuid_6_0= RULE_INT )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1000:3: lv_uuid_6_0= RULE_INT
                        {
                           lv_uuid_6_0 = (Token) match(input, RULE_INT, FOLLOW_RULE_INT_in_ruleTeamDef2094);

                           newLeafNode(lv_uuid_6_0,
                              grammarAccess.getTeamDefAccess().getUuidINTTerminalRuleCall_4_1_0());

                           if (current == null) {
                              current = createModelElement(grammarAccess.getTeamDefRule());
                           }
                           setWithLastConsumed(current, "uuid", lv_uuid_6_0, "INT");

                        }

                     }

                  }
                     break;

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1016:4: (otherlv_7= 'active' ( (lv_active_8_0= ruleBooleanDef ) ) )?
               int alt28 = 2;
               int LA28_0 = input.LA(1);

               if (LA28_0 == 18) {
                  alt28 = 1;
               }
               switch (alt28) {
                  case 1:
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1016:6: otherlv_7= 'active' ( (lv_active_8_0= ruleBooleanDef ) )
                  {
                     otherlv_7 = (Token) match(input, 18, FOLLOW_18_in_ruleTeamDef2114);

                     newLeafNode(otherlv_7, grammarAccess.getTeamDefAccess().getActiveKeyword_5_0());

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1020:1: ( (lv_active_8_0= ruleBooleanDef ) )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1021:1: (lv_active_8_0= ruleBooleanDef )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1021:1: (lv_active_8_0= ruleBooleanDef )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1022:3: lv_active_8_0= ruleBooleanDef
                        {

                           newCompositeNode(grammarAccess.getTeamDefAccess().getActiveBooleanDefEnumRuleCall_5_1_0());

                           pushFollow(FOLLOW_ruleBooleanDef_in_ruleTeamDef2135);
                           lv_active_8_0 = ruleBooleanDef();

                           state._fsp--;

                           if (current == null) {
                              current = createModelElementForParent(grammarAccess.getTeamDefRule());
                           }
                           set(current, "active", lv_active_8_0, "BooleanDef");
                           afterParserOrEnumRuleCall();

                        }

                     }

                  }
                     break;

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1038:4: (otherlv_9= 'staticId' ( (lv_staticId_10_0= RULE_STRING ) ) )*
               loop29: do {
                  int alt29 = 2;
                  int LA29_0 = input.LA(1);

                  if (LA29_0 == 29) {
                     alt29 = 1;
                  }

                  switch (alt29) {
                     case 1:
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1038:6: otherlv_9= 'staticId' ( (lv_staticId_10_0= RULE_STRING ) )
                     {
                        otherlv_9 = (Token) match(input, 29, FOLLOW_29_in_ruleTeamDef2150);

                        newLeafNode(otherlv_9, grammarAccess.getTeamDefAccess().getStaticIdKeyword_6_0());

                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1042:1: ( (lv_staticId_10_0= RULE_STRING ) )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1043:1: (lv_staticId_10_0= RULE_STRING )
                        {
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1043:1: (lv_staticId_10_0= RULE_STRING )
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1044:3: lv_staticId_10_0= RULE_STRING
                           {
                              lv_staticId_10_0 =
                                 (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleTeamDef2167);

                              newLeafNode(lv_staticId_10_0,
                                 grammarAccess.getTeamDefAccess().getStaticIdSTRINGTerminalRuleCall_6_1_0());

                              if (current == null) {
                                 current = createModelElement(grammarAccess.getTeamDefRule());
                              }
                              addWithLastConsumed(current, "staticId", lv_staticId_10_0, "STRING");

                           }

                        }

                     }
                        break;

                     default:
                        break loop29;
                  }
               } while (true);

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1060:4: (otherlv_11= 'lead' ( (lv_lead_12_0= ruleUserRef ) ) )*
               loop30: do {
                  int alt30 = 2;
                  int LA30_0 = input.LA(1);

                  if (LA30_0 == 30) {
                     alt30 = 1;
                  }

                  switch (alt30) {
                     case 1:
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1060:6: otherlv_11= 'lead' ( (lv_lead_12_0= ruleUserRef ) )
                     {
                        otherlv_11 = (Token) match(input, 30, FOLLOW_30_in_ruleTeamDef2187);

                        newLeafNode(otherlv_11, grammarAccess.getTeamDefAccess().getLeadKeyword_7_0());

                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1064:1: ( (lv_lead_12_0= ruleUserRef ) )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1065:1: (lv_lead_12_0= ruleUserRef )
                        {
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1065:1: (lv_lead_12_0= ruleUserRef )
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1066:3: lv_lead_12_0= ruleUserRef
                           {

                              newCompositeNode(grammarAccess.getTeamDefAccess().getLeadUserRefParserRuleCall_7_1_0());

                              pushFollow(FOLLOW_ruleUserRef_in_ruleTeamDef2208);
                              lv_lead_12_0 = ruleUserRef();

                              state._fsp--;

                              if (current == null) {
                                 current = createModelElementForParent(grammarAccess.getTeamDefRule());
                              }
                              add(current, "lead", lv_lead_12_0, "UserRef");
                              afterParserOrEnumRuleCall();

                           }

                        }

                     }
                        break;

                     default:
                        break loop30;
                  }
               } while (true);

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1082:4: (otherlv_13= 'member' ( (lv_member_14_0= ruleUserRef ) ) )*
               loop31: do {
                  int alt31 = 2;
                  int LA31_0 = input.LA(1);

                  if (LA31_0 == 31) {
                     alt31 = 1;
                  }

                  switch (alt31) {
                     case 1:
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1082:6: otherlv_13= 'member' ( (lv_member_14_0= ruleUserRef ) )
                     {
                        otherlv_13 = (Token) match(input, 31, FOLLOW_31_in_ruleTeamDef2223);

                        newLeafNode(otherlv_13, grammarAccess.getTeamDefAccess().getMemberKeyword_8_0());

                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1086:1: ( (lv_member_14_0= ruleUserRef ) )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1087:1: (lv_member_14_0= ruleUserRef )
                        {
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1087:1: (lv_member_14_0= ruleUserRef )
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1088:3: lv_member_14_0= ruleUserRef
                           {

                              newCompositeNode(grammarAccess.getTeamDefAccess().getMemberUserRefParserRuleCall_8_1_0());

                              pushFollow(FOLLOW_ruleUserRef_in_ruleTeamDef2244);
                              lv_member_14_0 = ruleUserRef();

                              state._fsp--;

                              if (current == null) {
                                 current = createModelElementForParent(grammarAccess.getTeamDefRule());
                              }
                              add(current, "member", lv_member_14_0, "UserRef");
                              afterParserOrEnumRuleCall();

                           }

                        }

                     }
                        break;

                     default:
                        break loop31;
                  }
               } while (true);

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1104:4: (otherlv_15= 'privileged' ( (lv_privileged_16_0= ruleUserRef ) ) )*
               loop32: do {
                  int alt32 = 2;
                  int LA32_0 = input.LA(1);

                  if (LA32_0 == 32) {
                     alt32 = 1;
                  }

                  switch (alt32) {
                     case 1:
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1104:6: otherlv_15= 'privileged' ( (lv_privileged_16_0= ruleUserRef ) )
                     {
                        otherlv_15 = (Token) match(input, 32, FOLLOW_32_in_ruleTeamDef2259);

                        newLeafNode(otherlv_15, grammarAccess.getTeamDefAccess().getPrivilegedKeyword_9_0());

                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1108:1: ( (lv_privileged_16_0= ruleUserRef ) )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1109:1: (lv_privileged_16_0= ruleUserRef )
                        {
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1109:1: (lv_privileged_16_0= ruleUserRef )
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1110:3: lv_privileged_16_0= ruleUserRef
                           {

                              newCompositeNode(
                                 grammarAccess.getTeamDefAccess().getPrivilegedUserRefParserRuleCall_9_1_0());

                              pushFollow(FOLLOW_ruleUserRef_in_ruleTeamDef2280);
                              lv_privileged_16_0 = ruleUserRef();

                              state._fsp--;

                              if (current == null) {
                                 current = createModelElementForParent(grammarAccess.getTeamDefRule());
                              }
                              add(current, "privileged", lv_privileged_16_0, "UserRef");
                              afterParserOrEnumRuleCall();

                           }

                        }

                     }
                        break;

                     default:
                        break loop32;
                  }
               } while (true);

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1126:4: (otherlv_17= 'workDefinition' ( (lv_workDefinition_18_0= RULE_STRING ) ) )?
               int alt33 = 2;
               int LA33_0 = input.LA(1);

               if (LA33_0 == 14) {
                  alt33 = 1;
               }
               switch (alt33) {
                  case 1:
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1126:6: otherlv_17= 'workDefinition' ( (lv_workDefinition_18_0= RULE_STRING ) )
                  {
                     otherlv_17 = (Token) match(input, 14, FOLLOW_14_in_ruleTeamDef2295);

                     newLeafNode(otherlv_17, grammarAccess.getTeamDefAccess().getWorkDefinitionKeyword_10_0());

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1130:1: ( (lv_workDefinition_18_0= RULE_STRING ) )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1131:1: (lv_workDefinition_18_0= RULE_STRING )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1131:1: (lv_workDefinition_18_0= RULE_STRING )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1132:3: lv_workDefinition_18_0= RULE_STRING
                        {
                           lv_workDefinition_18_0 =
                              (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleTeamDef2312);

                           newLeafNode(lv_workDefinition_18_0,
                              grammarAccess.getTeamDefAccess().getWorkDefinitionSTRINGTerminalRuleCall_10_1_0());

                           if (current == null) {
                              current = createModelElement(grammarAccess.getTeamDefRule());
                           }
                           setWithLastConsumed(current, "workDefinition", lv_workDefinition_18_0, "STRING");

                        }

                     }

                  }
                     break;

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1148:4: (otherlv_19= 'relatedTaskWorkDefinition' ( (lv_relatedTaskWorkDefinition_20_0= RULE_STRING ) ) )?
               int alt34 = 2;
               int LA34_0 = input.LA(1);

               if (LA34_0 == 33) {
                  alt34 = 1;
               }
               switch (alt34) {
                  case 1:
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1148:6: otherlv_19= 'relatedTaskWorkDefinition' ( (lv_relatedTaskWorkDefinition_20_0= RULE_STRING ) )
                  {
                     otherlv_19 = (Token) match(input, 33, FOLLOW_33_in_ruleTeamDef2332);

                     newLeafNode(otherlv_19,
                        grammarAccess.getTeamDefAccess().getRelatedTaskWorkDefinitionKeyword_11_0());

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1152:1: ( (lv_relatedTaskWorkDefinition_20_0= RULE_STRING ) )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1153:1: (lv_relatedTaskWorkDefinition_20_0= RULE_STRING )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1153:1: (lv_relatedTaskWorkDefinition_20_0= RULE_STRING )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1154:3: lv_relatedTaskWorkDefinition_20_0= RULE_STRING
                        {
                           lv_relatedTaskWorkDefinition_20_0 =
                              (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleTeamDef2349);

                           newLeafNode(lv_relatedTaskWorkDefinition_20_0,
                              grammarAccess.getTeamDefAccess().getRelatedTaskWorkDefinitionSTRINGTerminalRuleCall_11_1_0());

                           if (current == null) {
                              current = createModelElement(grammarAccess.getTeamDefRule());
                           }
                           setWithLastConsumed(current, "relatedTaskWorkDefinition", lv_relatedTaskWorkDefinition_20_0,
                              "STRING");

                        }

                     }

                  }
                     break;

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1170:4: (otherlv_21= 'teamWorkflowArtifactType' ( (lv_teamWorkflowArtifactType_22_0= RULE_STRING ) ) )?
               int alt35 = 2;
               int LA35_0 = input.LA(1);

               if (LA35_0 == 34) {
                  alt35 = 1;
               }
               switch (alt35) {
                  case 1:
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1170:6: otherlv_21= 'teamWorkflowArtifactType' ( (lv_teamWorkflowArtifactType_22_0= RULE_STRING ) )
                  {
                     otherlv_21 = (Token) match(input, 34, FOLLOW_34_in_ruleTeamDef2369);

                     newLeafNode(otherlv_21,
                        grammarAccess.getTeamDefAccess().getTeamWorkflowArtifactTypeKeyword_12_0());

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1174:1: ( (lv_teamWorkflowArtifactType_22_0= RULE_STRING ) )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1175:1: (lv_teamWorkflowArtifactType_22_0= RULE_STRING )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1175:1: (lv_teamWorkflowArtifactType_22_0= RULE_STRING )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1176:3: lv_teamWorkflowArtifactType_22_0= RULE_STRING
                        {
                           lv_teamWorkflowArtifactType_22_0 =
                              (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleTeamDef2386);

                           newLeafNode(lv_teamWorkflowArtifactType_22_0,
                              grammarAccess.getTeamDefAccess().getTeamWorkflowArtifactTypeSTRINGTerminalRuleCall_12_1_0());

                           if (current == null) {
                              current = createModelElement(grammarAccess.getTeamDefRule());
                           }
                           setWithLastConsumed(current, "teamWorkflowArtifactType", lv_teamWorkflowArtifactType_22_0,
                              "STRING");

                        }

                     }

                  }
                     break;

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1192:4: (otherlv_23= 'accessContextId' ( (lv_accessContextId_24_0= RULE_STRING ) ) )*
               loop36: do {
                  int alt36 = 2;
                  int LA36_0 = input.LA(1);

                  if (LA36_0 == 35) {
                     alt36 = 1;
                  }

                  switch (alt36) {
                     case 1:
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1192:6: otherlv_23= 'accessContextId' ( (lv_accessContextId_24_0= RULE_STRING ) )
                     {
                        otherlv_23 = (Token) match(input, 35, FOLLOW_35_in_ruleTeamDef2406);

                        newLeafNode(otherlv_23, grammarAccess.getTeamDefAccess().getAccessContextIdKeyword_13_0());

                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1196:1: ( (lv_accessContextId_24_0= RULE_STRING ) )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1197:1: (lv_accessContextId_24_0= RULE_STRING )
                        {
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1197:1: (lv_accessContextId_24_0= RULE_STRING )
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1198:3: lv_accessContextId_24_0= RULE_STRING
                           {
                              lv_accessContextId_24_0 =
                                 (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleTeamDef2423);

                              newLeafNode(lv_accessContextId_24_0,
                                 grammarAccess.getTeamDefAccess().getAccessContextIdSTRINGTerminalRuleCall_13_1_0());

                              if (current == null) {
                                 current = createModelElement(grammarAccess.getTeamDefRule());
                              }
                              addWithLastConsumed(current, "accessContextId", lv_accessContextId_24_0, "STRING");

                           }

                        }

                     }
                        break;

                     default:
                        break loop36;
                  }
               } while (true);

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1214:4: (otherlv_25= 'version' ( (lv_version_26_0= ruleVersionDef ) ) )*
               loop37: do {
                  int alt37 = 2;
                  int LA37_0 = input.LA(1);

                  if (LA37_0 == 36) {
                     alt37 = 1;
                  }

                  switch (alt37) {
                     case 1:
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1214:6: otherlv_25= 'version' ( (lv_version_26_0= ruleVersionDef ) )
                     {
                        otherlv_25 = (Token) match(input, 36, FOLLOW_36_in_ruleTeamDef2443);

                        newLeafNode(otherlv_25, grammarAccess.getTeamDefAccess().getVersionKeyword_14_0());

                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1218:1: ( (lv_version_26_0= ruleVersionDef ) )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1219:1: (lv_version_26_0= ruleVersionDef )
                        {
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1219:1: (lv_version_26_0= ruleVersionDef )
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1220:3: lv_version_26_0= ruleVersionDef
                           {

                              newCompositeNode(
                                 grammarAccess.getTeamDefAccess().getVersionVersionDefParserRuleCall_14_1_0());

                              pushFollow(FOLLOW_ruleVersionDef_in_ruleTeamDef2464);
                              lv_version_26_0 = ruleVersionDef();

                              state._fsp--;

                              if (current == null) {
                                 current = createModelElementForParent(grammarAccess.getTeamDefRule());
                              }
                              add(current, "version", lv_version_26_0, "VersionDef");
                              afterParserOrEnumRuleCall();

                           }

                        }

                     }
                        break;

                     default:
                        break loop37;
                  }
               } while (true);

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1236:4: (otherlv_27= 'rule' ( (lv_rules_28_0= ruleRuleRef ) ) )*
               loop38: do {
                  int alt38 = 2;
                  int LA38_0 = input.LA(1);

                  if (LA38_0 == 16) {
                     alt38 = 1;
                  }

                  switch (alt38) {
                     case 1:
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1236:6: otherlv_27= 'rule' ( (lv_rules_28_0= ruleRuleRef ) )
                     {
                        otherlv_27 = (Token) match(input, 16, FOLLOW_16_in_ruleTeamDef2479);

                        newLeafNode(otherlv_27, grammarAccess.getTeamDefAccess().getRuleKeyword_15_0());

                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1240:1: ( (lv_rules_28_0= ruleRuleRef ) )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1241:1: (lv_rules_28_0= ruleRuleRef )
                        {
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1241:1: (lv_rules_28_0= ruleRuleRef )
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1242:3: lv_rules_28_0= ruleRuleRef
                           {

                              newCompositeNode(grammarAccess.getTeamDefAccess().getRulesRuleRefParserRuleCall_15_1_0());

                              pushFollow(FOLLOW_ruleRuleRef_in_ruleTeamDef2500);
                              lv_rules_28_0 = ruleRuleRef();

                              state._fsp--;

                              if (current == null) {
                                 current = createModelElementForParent(grammarAccess.getTeamDefRule());
                              }
                              add(current, "rules", lv_rules_28_0, "RuleRef");
                              afterParserOrEnumRuleCall();

                           }

                        }

                     }
                        break;

                     default:
                        break loop38;
                  }
               } while (true);

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1258:4: (otherlv_29= 'children' otherlv_30= '{' (otherlv_31= 'teamDefinition' ( (lv_children_32_0= ruleTeamDef ) ) )+ otherlv_33= '}' )?
               int alt40 = 2;
               int LA40_0 = input.LA(1);

               if (LA40_0 == 37) {
                  alt40 = 1;
               }
               switch (alt40) {
                  case 1:
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1258:6: otherlv_29= 'children' otherlv_30= '{' (otherlv_31= 'teamDefinition' ( (lv_children_32_0= ruleTeamDef ) ) )+ otherlv_33= '}'
                  {
                     otherlv_29 = (Token) match(input, 37, FOLLOW_37_in_ruleTeamDef2515);

                     newLeafNode(otherlv_29, grammarAccess.getTeamDefAccess().getChildrenKeyword_16_0());

                     otherlv_30 = (Token) match(input, 17, FOLLOW_17_in_ruleTeamDef2527);

                     newLeafNode(otherlv_30, grammarAccess.getTeamDefAccess().getLeftCurlyBracketKeyword_16_1());

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1266:1: (otherlv_31= 'teamDefinition' ( (lv_children_32_0= ruleTeamDef ) ) )+
                     int cnt39 = 0;
                     loop39: do {
                        int alt39 = 2;
                        int LA39_0 = input.LA(1);

                        if (LA39_0 == 12) {
                           alt39 = 1;
                        }

                        switch (alt39) {
                           case 1:
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1266:3: otherlv_31= 'teamDefinition' ( (lv_children_32_0= ruleTeamDef ) )
                           {
                              otherlv_31 = (Token) match(input, 12, FOLLOW_12_in_ruleTeamDef2540);

                              newLeafNode(otherlv_31,
                                 grammarAccess.getTeamDefAccess().getTeamDefinitionKeyword_16_2_0());

                              // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1270:1: ( (lv_children_32_0= ruleTeamDef ) )
                              // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1271:1: (lv_children_32_0= ruleTeamDef )
                              {
                                 // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1271:1: (lv_children_32_0= ruleTeamDef )
                                 // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1272:3: lv_children_32_0= ruleTeamDef
                                 {

                                    newCompositeNode(
                                       grammarAccess.getTeamDefAccess().getChildrenTeamDefParserRuleCall_16_2_1_0());

                                    pushFollow(FOLLOW_ruleTeamDef_in_ruleTeamDef2561);
                                    lv_children_32_0 = ruleTeamDef();

                                    state._fsp--;

                                    if (current == null) {
                                       current = createModelElementForParent(grammarAccess.getTeamDefRule());
                                    }
                                    add(current, "children", lv_children_32_0, "TeamDef");
                                    afterParserOrEnumRuleCall();

                                 }

                              }

                           }
                              break;

                           default:
                              if (cnt39 >= 1) {
                                 break loop39;
                              }
                              EarlyExitException eee = new EarlyExitException(39, input);
                              throw eee;
                        }
                        cnt39++;
                     } while (true);

                     otherlv_33 = (Token) match(input, 22, FOLLOW_22_in_ruleTeamDef2575);

                     newLeafNode(otherlv_33, grammarAccess.getTeamDefAccess().getRightCurlyBracketKeyword_16_3());

                  }
                     break;

               }

               otherlv_34 = (Token) match(input, 22, FOLLOW_22_in_ruleTeamDef2589);

               newLeafNode(otherlv_34, grammarAccess.getTeamDefAccess().getRightCurlyBracketKeyword_17());

            }

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleTeamDef"

   // $ANTLR start "entryRuleAI_DEF_REFERENCE"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1304:1: entryRuleAI_DEF_REFERENCE returns [String current=null] : iv_ruleAI_DEF_REFERENCE= ruleAI_DEF_REFERENCE EOF ;
   public final String entryRuleAI_DEF_REFERENCE() throws RecognitionException {
      String current = null;

      AntlrDatatypeRuleToken iv_ruleAI_DEF_REFERENCE = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1305:2: (iv_ruleAI_DEF_REFERENCE= ruleAI_DEF_REFERENCE EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1306:2: iv_ruleAI_DEF_REFERENCE= ruleAI_DEF_REFERENCE EOF
         {
            newCompositeNode(grammarAccess.getAI_DEF_REFERENCERule());
            pushFollow(FOLLOW_ruleAI_DEF_REFERENCE_in_entryRuleAI_DEF_REFERENCE2626);
            iv_ruleAI_DEF_REFERENCE = ruleAI_DEF_REFERENCE();

            state._fsp--;

            current = iv_ruleAI_DEF_REFERENCE.getText();
            match(input, EOF, FOLLOW_EOF_in_entryRuleAI_DEF_REFERENCE2637);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleAI_DEF_REFERENCE"

   // $ANTLR start "ruleAI_DEF_REFERENCE"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1313:1: ruleAI_DEF_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
   public final AntlrDatatypeRuleToken ruleAI_DEF_REFERENCE() throws RecognitionException {
      AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

      Token this_STRING_0 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1316:28: (this_STRING_0= RULE_STRING )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1317:5: this_STRING_0= RULE_STRING
         {
            this_STRING_0 = (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleAI_DEF_REFERENCE2676);

            current.merge(this_STRING_0);

            newLeafNode(this_STRING_0, grammarAccess.getAI_DEF_REFERENCEAccess().getSTRINGTerminalRuleCall());

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleAI_DEF_REFERENCE"

   // $ANTLR start "entryRuleActionableItemDef"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1332:1: entryRuleActionableItemDef returns [EObject current=null] : iv_ruleActionableItemDef= ruleActionableItemDef EOF ;
   public final EObject entryRuleActionableItemDef() throws RecognitionException {
      EObject current = null;

      EObject iv_ruleActionableItemDef = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1333:2: (iv_ruleActionableItemDef= ruleActionableItemDef EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1334:2: iv_ruleActionableItemDef= ruleActionableItemDef EOF
         {
            newCompositeNode(grammarAccess.getActionableItemDefRule());
            pushFollow(FOLLOW_ruleActionableItemDef_in_entryRuleActionableItemDef2720);
            iv_ruleActionableItemDef = ruleActionableItemDef();

            state._fsp--;

            current = iv_ruleActionableItemDef;
            match(input, EOF, FOLLOW_EOF_in_entryRuleActionableItemDef2730);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleActionableItemDef"

   // $ANTLR start "ruleActionableItemDef"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1341:1: ruleActionableItemDef returns [EObject current=null] : ( ( (lv_name_0_0= ruleAI_DEF_REFERENCE ) ) ( (lv_aiDefOption_1_0= ruleActionableItemOption ) )* (otherlv_2= '{' (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )? (otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_INT ) ) )? (otherlv_7= 'active' ( (lv_active_8_0= ruleBooleanDef ) ) )? (otherlv_9= 'actionable' ( (lv_actionable_10_0= ruleBooleanDef ) ) )? (otherlv_11= 'lead' ( (lv_lead_12_0= ruleUserRef ) ) )* (otherlv_13= 'owner' ( (lv_owner_14_0= ruleUserRef ) ) )* (otherlv_15= 'staticId' ( (lv_staticId_16_0= RULE_STRING ) ) )* (otherlv_17= 'team' ( (lv_teamDef_18_0= RULE_STRING ) ) )? (otherlv_19= 'accessContextId' ( (lv_accessContextId_20_0= RULE_STRING ) ) )* (otherlv_21= 'rule' ( (lv_rules_22_0= ruleRuleRef ) ) )* (otherlv_23= 'children' otherlv_24= '{' (otherlv_25= 'actionableItem' ( (lv_children_26_0= ruleActionableItemDef ) ) )+ otherlv_27= '}' )? otherlv_28= '}' )? ) ;
   public final EObject ruleActionableItemDef() throws RecognitionException {
      EObject current = null;

      Token otherlv_2 = null;
      Token otherlv_3 = null;
      Token lv_guid_4_0 = null;
      Token otherlv_5 = null;
      Token lv_uuid_6_0 = null;
      Token otherlv_7 = null;
      Token otherlv_9 = null;
      Token otherlv_11 = null;
      Token otherlv_13 = null;
      Token otherlv_15 = null;
      Token lv_staticId_16_0 = null;
      Token otherlv_17 = null;
      Token lv_teamDef_18_0 = null;
      Token otherlv_19 = null;
      Token lv_accessContextId_20_0 = null;
      Token otherlv_21 = null;
      Token otherlv_23 = null;
      Token otherlv_24 = null;
      Token otherlv_25 = null;
      Token otherlv_27 = null;
      Token otherlv_28 = null;
      AntlrDatatypeRuleToken lv_name_0_0 = null;

      AntlrDatatypeRuleToken lv_aiDefOption_1_0 = null;

      Enumerator lv_active_8_0 = null;

      Enumerator lv_actionable_10_0 = null;

      EObject lv_lead_12_0 = null;

      EObject lv_owner_14_0 = null;

      AntlrDatatypeRuleToken lv_rules_22_0 = null;

      EObject lv_children_26_0 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1344:28: ( ( ( (lv_name_0_0= ruleAI_DEF_REFERENCE ) ) ( (lv_aiDefOption_1_0= ruleActionableItemOption ) )* (otherlv_2= '{' (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )? (otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_INT ) ) )? (otherlv_7= 'active' ( (lv_active_8_0= ruleBooleanDef ) ) )? (otherlv_9= 'actionable' ( (lv_actionable_10_0= ruleBooleanDef ) ) )? (otherlv_11= 'lead' ( (lv_lead_12_0= ruleUserRef ) ) )* (otherlv_13= 'owner' ( (lv_owner_14_0= ruleUserRef ) ) )* (otherlv_15= 'staticId' ( (lv_staticId_16_0= RULE_STRING ) ) )* (otherlv_17= 'team' ( (lv_teamDef_18_0= RULE_STRING ) ) )? (otherlv_19= 'accessContextId' ( (lv_accessContextId_20_0= RULE_STRING ) ) )* (otherlv_21= 'rule' ( (lv_rules_22_0= ruleRuleRef ) ) )* (otherlv_23= 'children' otherlv_24= '{' (otherlv_25= 'actionableItem' ( (lv_children_26_0= ruleActionableItemDef ) ) )+ otherlv_27= '}' )? otherlv_28= '}' )? ) )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1345:1: ( ( (lv_name_0_0= ruleAI_DEF_REFERENCE ) ) ( (lv_aiDefOption_1_0= ruleActionableItemOption ) )* (otherlv_2= '{' (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )? (otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_INT ) ) )? (otherlv_7= 'active' ( (lv_active_8_0= ruleBooleanDef ) ) )? (otherlv_9= 'actionable' ( (lv_actionable_10_0= ruleBooleanDef ) ) )? (otherlv_11= 'lead' ( (lv_lead_12_0= ruleUserRef ) ) )* (otherlv_13= 'owner' ( (lv_owner_14_0= ruleUserRef ) ) )* (otherlv_15= 'staticId' ( (lv_staticId_16_0= RULE_STRING ) ) )* (otherlv_17= 'team' ( (lv_teamDef_18_0= RULE_STRING ) ) )? (otherlv_19= 'accessContextId' ( (lv_accessContextId_20_0= RULE_STRING ) ) )* (otherlv_21= 'rule' ( (lv_rules_22_0= ruleRuleRef ) ) )* (otherlv_23= 'children' otherlv_24= '{' (otherlv_25= 'actionableItem' ( (lv_children_26_0= ruleActionableItemDef ) ) )+ otherlv_27= '}' )? otherlv_28= '}' )? )
         {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1345:1: ( ( (lv_name_0_0= ruleAI_DEF_REFERENCE ) ) ( (lv_aiDefOption_1_0= ruleActionableItemOption ) )* (otherlv_2= '{' (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )? (otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_INT ) ) )? (otherlv_7= 'active' ( (lv_active_8_0= ruleBooleanDef ) ) )? (otherlv_9= 'actionable' ( (lv_actionable_10_0= ruleBooleanDef ) ) )? (otherlv_11= 'lead' ( (lv_lead_12_0= ruleUserRef ) ) )* (otherlv_13= 'owner' ( (lv_owner_14_0= ruleUserRef ) ) )* (otherlv_15= 'staticId' ( (lv_staticId_16_0= RULE_STRING ) ) )* (otherlv_17= 'team' ( (lv_teamDef_18_0= RULE_STRING ) ) )? (otherlv_19= 'accessContextId' ( (lv_accessContextId_20_0= RULE_STRING ) ) )* (otherlv_21= 'rule' ( (lv_rules_22_0= ruleRuleRef ) ) )* (otherlv_23= 'children' otherlv_24= '{' (otherlv_25= 'actionableItem' ( (lv_children_26_0= ruleActionableItemDef ) ) )+ otherlv_27= '}' )? otherlv_28= '}' )? )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1345:2: ( (lv_name_0_0= ruleAI_DEF_REFERENCE ) ) ( (lv_aiDefOption_1_0= ruleActionableItemOption ) )* (otherlv_2= '{' (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )? (otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_INT ) ) )? (otherlv_7= 'active' ( (lv_active_8_0= ruleBooleanDef ) ) )? (otherlv_9= 'actionable' ( (lv_actionable_10_0= ruleBooleanDef ) ) )? (otherlv_11= 'lead' ( (lv_lead_12_0= ruleUserRef ) ) )* (otherlv_13= 'owner' ( (lv_owner_14_0= ruleUserRef ) ) )* (otherlv_15= 'staticId' ( (lv_staticId_16_0= RULE_STRING ) ) )* (otherlv_17= 'team' ( (lv_teamDef_18_0= RULE_STRING ) ) )? (otherlv_19= 'accessContextId' ( (lv_accessContextId_20_0= RULE_STRING ) ) )* (otherlv_21= 'rule' ( (lv_rules_22_0= ruleRuleRef ) ) )* (otherlv_23= 'children' otherlv_24= '{' (otherlv_25= 'actionableItem' ( (lv_children_26_0= ruleActionableItemDef ) ) )+ otherlv_27= '}' )? otherlv_28= '}' )?
            {
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1345:2: ( (lv_name_0_0= ruleAI_DEF_REFERENCE ) )
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1346:1: (lv_name_0_0= ruleAI_DEF_REFERENCE )
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1346:1: (lv_name_0_0= ruleAI_DEF_REFERENCE )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1347:3: lv_name_0_0= ruleAI_DEF_REFERENCE
                  {

                     newCompositeNode(
                        grammarAccess.getActionableItemDefAccess().getNameAI_DEF_REFERENCEParserRuleCall_0_0());

                     pushFollow(FOLLOW_ruleAI_DEF_REFERENCE_in_ruleActionableItemDef2776);
                     lv_name_0_0 = ruleAI_DEF_REFERENCE();

                     state._fsp--;

                     if (current == null) {
                        current = createModelElementForParent(grammarAccess.getActionableItemDefRule());
                     }
                     set(current, "name", lv_name_0_0, "AI_DEF_REFERENCE");
                     afterParserOrEnumRuleCall();

                  }

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1363:2: ( (lv_aiDefOption_1_0= ruleActionableItemOption ) )*
               loop41: do {
                  int alt41 = 2;
                  int LA41_0 = input.LA(1);

                  if (LA41_0 == RULE_STRING || LA41_0 == 85) {
                     alt41 = 1;
                  }

                  switch (alt41) {
                     case 1:
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1364:1: (lv_aiDefOption_1_0= ruleActionableItemOption )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1364:1: (lv_aiDefOption_1_0= ruleActionableItemOption )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1365:3: lv_aiDefOption_1_0= ruleActionableItemOption
                        {

                           newCompositeNode(
                              grammarAccess.getActionableItemDefAccess().getAiDefOptionActionableItemOptionParserRuleCall_1_0());

                           pushFollow(FOLLOW_ruleActionableItemOption_in_ruleActionableItemDef2797);
                           lv_aiDefOption_1_0 = ruleActionableItemOption();

                           state._fsp--;

                           if (current == null) {
                              current = createModelElementForParent(grammarAccess.getActionableItemDefRule());
                           }
                           add(current, "aiDefOption", lv_aiDefOption_1_0, "ActionableItemOption");
                           afterParserOrEnumRuleCall();

                        }

                     }
                        break;

                     default:
                        break loop41;
                  }
               } while (true);

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1381:3: (otherlv_2= '{' (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )? (otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_INT ) ) )? (otherlv_7= 'active' ( (lv_active_8_0= ruleBooleanDef ) ) )? (otherlv_9= 'actionable' ( (lv_actionable_10_0= ruleBooleanDef ) ) )? (otherlv_11= 'lead' ( (lv_lead_12_0= ruleUserRef ) ) )* (otherlv_13= 'owner' ( (lv_owner_14_0= ruleUserRef ) ) )* (otherlv_15= 'staticId' ( (lv_staticId_16_0= RULE_STRING ) ) )* (otherlv_17= 'team' ( (lv_teamDef_18_0= RULE_STRING ) ) )? (otherlv_19= 'accessContextId' ( (lv_accessContextId_20_0= RULE_STRING ) ) )* (otherlv_21= 'rule' ( (lv_rules_22_0= ruleRuleRef ) ) )* (otherlv_23= 'children' otherlv_24= '{' (otherlv_25= 'actionableItem' ( (lv_children_26_0= ruleActionableItemDef ) ) )+ otherlv_27= '}' )? otherlv_28= '}' )?
               int alt54 = 2;
               int LA54_0 = input.LA(1);

               if (LA54_0 == 17) {
                  alt54 = 1;
               }
               switch (alt54) {
                  case 1:
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1381:5: otherlv_2= '{' (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )? (otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_INT ) ) )? (otherlv_7= 'active' ( (lv_active_8_0= ruleBooleanDef ) ) )? (otherlv_9= 'actionable' ( (lv_actionable_10_0= ruleBooleanDef ) ) )? (otherlv_11= 'lead' ( (lv_lead_12_0= ruleUserRef ) ) )* (otherlv_13= 'owner' ( (lv_owner_14_0= ruleUserRef ) ) )* (otherlv_15= 'staticId' ( (lv_staticId_16_0= RULE_STRING ) ) )* (otherlv_17= 'team' ( (lv_teamDef_18_0= RULE_STRING ) ) )? (otherlv_19= 'accessContextId' ( (lv_accessContextId_20_0= RULE_STRING ) ) )* (otherlv_21= 'rule' ( (lv_rules_22_0= ruleRuleRef ) ) )* (otherlv_23= 'children' otherlv_24= '{' (otherlv_25= 'actionableItem' ( (lv_children_26_0= ruleActionableItemDef ) ) )+ otherlv_27= '}' )? otherlv_28= '}'
                  {
                     otherlv_2 = (Token) match(input, 17, FOLLOW_17_in_ruleActionableItemDef2811);

                     newLeafNode(otherlv_2,
                        grammarAccess.getActionableItemDefAccess().getLeftCurlyBracketKeyword_2_0());

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1385:1: (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )?
                     int alt42 = 2;
                     int LA42_0 = input.LA(1);

                     if (LA42_0 == 25) {
                        alt42 = 1;
                     }
                     switch (alt42) {
                        case 1:
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1385:3: otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) )
                        {
                           otherlv_3 = (Token) match(input, 25, FOLLOW_25_in_ruleActionableItemDef2824);

                           newLeafNode(otherlv_3, grammarAccess.getActionableItemDefAccess().getGuidKeyword_2_1_0());

                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1389:1: ( (lv_guid_4_0= RULE_STRING ) )
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1390:1: (lv_guid_4_0= RULE_STRING )
                           {
                              // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1390:1: (lv_guid_4_0= RULE_STRING )
                              // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1391:3: lv_guid_4_0= RULE_STRING
                              {
                                 lv_guid_4_0 =
                                    (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleActionableItemDef2841);

                                 newLeafNode(lv_guid_4_0,
                                    grammarAccess.getActionableItemDefAccess().getGuidSTRINGTerminalRuleCall_2_1_1_0());

                                 if (current == null) {
                                    current = createModelElement(grammarAccess.getActionableItemDefRule());
                                 }
                                 setWithLastConsumed(current, "guid", lv_guid_4_0, "STRING");

                              }

                           }

                        }
                           break;

                     }

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1407:4: (otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_INT ) ) )?
                     int alt43 = 2;
                     int LA43_0 = input.LA(1);

                     if (LA43_0 == 24) {
                        alt43 = 1;
                     }
                     switch (alt43) {
                        case 1:
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1407:6: otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_INT ) )
                        {
                           otherlv_5 = (Token) match(input, 24, FOLLOW_24_in_ruleActionableItemDef2861);

                           newLeafNode(otherlv_5, grammarAccess.getActionableItemDefAccess().getUuidKeyword_2_2_0());

                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1411:1: ( (lv_uuid_6_0= RULE_INT ) )
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1412:1: (lv_uuid_6_0= RULE_INT )
                           {
                              // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1412:1: (lv_uuid_6_0= RULE_INT )
                              // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1413:3: lv_uuid_6_0= RULE_INT
                              {
                                 lv_uuid_6_0 =
                                    (Token) match(input, RULE_INT, FOLLOW_RULE_INT_in_ruleActionableItemDef2878);

                                 newLeafNode(lv_uuid_6_0,
                                    grammarAccess.getActionableItemDefAccess().getUuidINTTerminalRuleCall_2_2_1_0());

                                 if (current == null) {
                                    current = createModelElement(grammarAccess.getActionableItemDefRule());
                                 }
                                 setWithLastConsumed(current, "uuid", lv_uuid_6_0, "INT");

                              }

                           }

                        }
                           break;

                     }

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1429:4: (otherlv_7= 'active' ( (lv_active_8_0= ruleBooleanDef ) ) )?
                     int alt44 = 2;
                     int LA44_0 = input.LA(1);

                     if (LA44_0 == 18) {
                        alt44 = 1;
                     }
                     switch (alt44) {
                        case 1:
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1429:6: otherlv_7= 'active' ( (lv_active_8_0= ruleBooleanDef ) )
                        {
                           otherlv_7 = (Token) match(input, 18, FOLLOW_18_in_ruleActionableItemDef2898);

                           newLeafNode(otherlv_7, grammarAccess.getActionableItemDefAccess().getActiveKeyword_2_3_0());

                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1433:1: ( (lv_active_8_0= ruleBooleanDef ) )
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1434:1: (lv_active_8_0= ruleBooleanDef )
                           {
                              // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1434:1: (lv_active_8_0= ruleBooleanDef )
                              // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1435:3: lv_active_8_0= ruleBooleanDef
                              {

                                 newCompositeNode(
                                    grammarAccess.getActionableItemDefAccess().getActiveBooleanDefEnumRuleCall_2_3_1_0());

                                 pushFollow(FOLLOW_ruleBooleanDef_in_ruleActionableItemDef2919);
                                 lv_active_8_0 = ruleBooleanDef();

                                 state._fsp--;

                                 if (current == null) {
                                    current = createModelElementForParent(grammarAccess.getActionableItemDefRule());
                                 }
                                 set(current, "active", lv_active_8_0, "BooleanDef");
                                 afterParserOrEnumRuleCall();

                              }

                           }

                        }
                           break;

                     }

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1451:4: (otherlv_9= 'actionable' ( (lv_actionable_10_0= ruleBooleanDef ) ) )?
                     int alt45 = 2;
                     int LA45_0 = input.LA(1);

                     if (LA45_0 == 38) {
                        alt45 = 1;
                     }
                     switch (alt45) {
                        case 1:
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1451:6: otherlv_9= 'actionable' ( (lv_actionable_10_0= ruleBooleanDef ) )
                        {
                           otherlv_9 = (Token) match(input, 38, FOLLOW_38_in_ruleActionableItemDef2934);

                           newLeafNode(otherlv_9,
                              grammarAccess.getActionableItemDefAccess().getActionableKeyword_2_4_0());

                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1455:1: ( (lv_actionable_10_0= ruleBooleanDef ) )
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1456:1: (lv_actionable_10_0= ruleBooleanDef )
                           {
                              // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1456:1: (lv_actionable_10_0= ruleBooleanDef )
                              // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1457:3: lv_actionable_10_0= ruleBooleanDef
                              {

                                 newCompositeNode(
                                    grammarAccess.getActionableItemDefAccess().getActionableBooleanDefEnumRuleCall_2_4_1_0());

                                 pushFollow(FOLLOW_ruleBooleanDef_in_ruleActionableItemDef2955);
                                 lv_actionable_10_0 = ruleBooleanDef();

                                 state._fsp--;

                                 if (current == null) {
                                    current = createModelElementForParent(grammarAccess.getActionableItemDefRule());
                                 }
                                 set(current, "actionable", lv_actionable_10_0, "BooleanDef");
                                 afterParserOrEnumRuleCall();

                              }

                           }

                        }
                           break;

                     }

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1473:4: (otherlv_11= 'lead' ( (lv_lead_12_0= ruleUserRef ) ) )*
                     loop46: do {
                        int alt46 = 2;
                        int LA46_0 = input.LA(1);

                        if (LA46_0 == 30) {
                           alt46 = 1;
                        }

                        switch (alt46) {
                           case 1:
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1473:6: otherlv_11= 'lead' ( (lv_lead_12_0= ruleUserRef ) )
                           {
                              otherlv_11 = (Token) match(input, 30, FOLLOW_30_in_ruleActionableItemDef2970);

                              newLeafNode(otherlv_11,
                                 grammarAccess.getActionableItemDefAccess().getLeadKeyword_2_5_0());

                              // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1477:1: ( (lv_lead_12_0= ruleUserRef ) )
                              // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1478:1: (lv_lead_12_0= ruleUserRef )
                              {
                                 // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1478:1: (lv_lead_12_0= ruleUserRef )
                                 // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1479:3: lv_lead_12_0= ruleUserRef
                                 {

                                    newCompositeNode(
                                       grammarAccess.getActionableItemDefAccess().getLeadUserRefParserRuleCall_2_5_1_0());

                                    pushFollow(FOLLOW_ruleUserRef_in_ruleActionableItemDef2991);
                                    lv_lead_12_0 = ruleUserRef();

                                    state._fsp--;

                                    if (current == null) {
                                       current = createModelElementForParent(grammarAccess.getActionableItemDefRule());
                                    }
                                    add(current, "lead", lv_lead_12_0, "UserRef");
                                    afterParserOrEnumRuleCall();

                                 }

                              }

                           }
                              break;

                           default:
                              break loop46;
                        }
                     } while (true);

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1495:4: (otherlv_13= 'owner' ( (lv_owner_14_0= ruleUserRef ) ) )*
                     loop47: do {
                        int alt47 = 2;
                        int LA47_0 = input.LA(1);

                        if (LA47_0 == 39) {
                           alt47 = 1;
                        }

                        switch (alt47) {
                           case 1:
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1495:6: otherlv_13= 'owner' ( (lv_owner_14_0= ruleUserRef ) )
                           {
                              otherlv_13 = (Token) match(input, 39, FOLLOW_39_in_ruleActionableItemDef3006);

                              newLeafNode(otherlv_13,
                                 grammarAccess.getActionableItemDefAccess().getOwnerKeyword_2_6_0());

                              // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1499:1: ( (lv_owner_14_0= ruleUserRef ) )
                              // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1500:1: (lv_owner_14_0= ruleUserRef )
                              {
                                 // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1500:1: (lv_owner_14_0= ruleUserRef )
                                 // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1501:3: lv_owner_14_0= ruleUserRef
                                 {

                                    newCompositeNode(
                                       grammarAccess.getActionableItemDefAccess().getOwnerUserRefParserRuleCall_2_6_1_0());

                                    pushFollow(FOLLOW_ruleUserRef_in_ruleActionableItemDef3027);
                                    lv_owner_14_0 = ruleUserRef();

                                    state._fsp--;

                                    if (current == null) {
                                       current = createModelElementForParent(grammarAccess.getActionableItemDefRule());
                                    }
                                    add(current, "owner", lv_owner_14_0, "UserRef");
                                    afterParserOrEnumRuleCall();

                                 }

                              }

                           }
                              break;

                           default:
                              break loop47;
                        }
                     } while (true);

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1517:4: (otherlv_15= 'staticId' ( (lv_staticId_16_0= RULE_STRING ) ) )*
                     loop48: do {
                        int alt48 = 2;
                        int LA48_0 = input.LA(1);

                        if (LA48_0 == 29) {
                           alt48 = 1;
                        }

                        switch (alt48) {
                           case 1:
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1517:6: otherlv_15= 'staticId' ( (lv_staticId_16_0= RULE_STRING ) )
                           {
                              otherlv_15 = (Token) match(input, 29, FOLLOW_29_in_ruleActionableItemDef3042);

                              newLeafNode(otherlv_15,
                                 grammarAccess.getActionableItemDefAccess().getStaticIdKeyword_2_7_0());

                              // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1521:1: ( (lv_staticId_16_0= RULE_STRING ) )
                              // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1522:1: (lv_staticId_16_0= RULE_STRING )
                              {
                                 // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1522:1: (lv_staticId_16_0= RULE_STRING )
                                 // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1523:3: lv_staticId_16_0= RULE_STRING
                                 {
                                    lv_staticId_16_0 = (Token) match(input, RULE_STRING,
                                       FOLLOW_RULE_STRING_in_ruleActionableItemDef3059);

                                    newLeafNode(lv_staticId_16_0,
                                       grammarAccess.getActionableItemDefAccess().getStaticIdSTRINGTerminalRuleCall_2_7_1_0());

                                    if (current == null) {
                                       current = createModelElement(grammarAccess.getActionableItemDefRule());
                                    }
                                    addWithLastConsumed(current, "staticId", lv_staticId_16_0, "STRING");

                                 }

                              }

                           }
                              break;

                           default:
                              break loop48;
                        }
                     } while (true);

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1539:4: (otherlv_17= 'team' ( (lv_teamDef_18_0= RULE_STRING ) ) )?
                     int alt49 = 2;
                     int LA49_0 = input.LA(1);

                     if (LA49_0 == 40) {
                        alt49 = 1;
                     }
                     switch (alt49) {
                        case 1:
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1539:6: otherlv_17= 'team' ( (lv_teamDef_18_0= RULE_STRING ) )
                        {
                           otherlv_17 = (Token) match(input, 40, FOLLOW_40_in_ruleActionableItemDef3079);

                           newLeafNode(otherlv_17, grammarAccess.getActionableItemDefAccess().getTeamKeyword_2_8_0());

                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1543:1: ( (lv_teamDef_18_0= RULE_STRING ) )
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1544:1: (lv_teamDef_18_0= RULE_STRING )
                           {
                              // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1544:1: (lv_teamDef_18_0= RULE_STRING )
                              // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1545:3: lv_teamDef_18_0= RULE_STRING
                              {
                                 lv_teamDef_18_0 =
                                    (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleActionableItemDef3096);

                                 newLeafNode(lv_teamDef_18_0,
                                    grammarAccess.getActionableItemDefAccess().getTeamDefSTRINGTerminalRuleCall_2_8_1_0());

                                 if (current == null) {
                                    current = createModelElement(grammarAccess.getActionableItemDefRule());
                                 }
                                 setWithLastConsumed(current, "teamDef", lv_teamDef_18_0, "STRING");

                              }

                           }

                        }
                           break;

                     }

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1561:4: (otherlv_19= 'accessContextId' ( (lv_accessContextId_20_0= RULE_STRING ) ) )*
                     loop50: do {
                        int alt50 = 2;
                        int LA50_0 = input.LA(1);

                        if (LA50_0 == 35) {
                           alt50 = 1;
                        }

                        switch (alt50) {
                           case 1:
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1561:6: otherlv_19= 'accessContextId' ( (lv_accessContextId_20_0= RULE_STRING ) )
                           {
                              otherlv_19 = (Token) match(input, 35, FOLLOW_35_in_ruleActionableItemDef3116);

                              newLeafNode(otherlv_19,
                                 grammarAccess.getActionableItemDefAccess().getAccessContextIdKeyword_2_9_0());

                              // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1565:1: ( (lv_accessContextId_20_0= RULE_STRING ) )
                              // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1566:1: (lv_accessContextId_20_0= RULE_STRING )
                              {
                                 // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1566:1: (lv_accessContextId_20_0= RULE_STRING )
                                 // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1567:3: lv_accessContextId_20_0= RULE_STRING
                                 {
                                    lv_accessContextId_20_0 = (Token) match(input, RULE_STRING,
                                       FOLLOW_RULE_STRING_in_ruleActionableItemDef3133);

                                    newLeafNode(lv_accessContextId_20_0,
                                       grammarAccess.getActionableItemDefAccess().getAccessContextIdSTRINGTerminalRuleCall_2_9_1_0());

                                    if (current == null) {
                                       current = createModelElement(grammarAccess.getActionableItemDefRule());
                                    }
                                    addWithLastConsumed(current, "accessContextId", lv_accessContextId_20_0, "STRING");

                                 }

                              }

                           }
                              break;

                           default:
                              break loop50;
                        }
                     } while (true);

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1583:4: (otherlv_21= 'rule' ( (lv_rules_22_0= ruleRuleRef ) ) )*
                     loop51: do {
                        int alt51 = 2;
                        int LA51_0 = input.LA(1);

                        if (LA51_0 == 16) {
                           alt51 = 1;
                        }

                        switch (alt51) {
                           case 1:
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1583:6: otherlv_21= 'rule' ( (lv_rules_22_0= ruleRuleRef ) )
                           {
                              otherlv_21 = (Token) match(input, 16, FOLLOW_16_in_ruleActionableItemDef3153);

                              newLeafNode(otherlv_21,
                                 grammarAccess.getActionableItemDefAccess().getRuleKeyword_2_10_0());

                              // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1587:1: ( (lv_rules_22_0= ruleRuleRef ) )
                              // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1588:1: (lv_rules_22_0= ruleRuleRef )
                              {
                                 // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1588:1: (lv_rules_22_0= ruleRuleRef )
                                 // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1589:3: lv_rules_22_0= ruleRuleRef
                                 {

                                    newCompositeNode(
                                       grammarAccess.getActionableItemDefAccess().getRulesRuleRefParserRuleCall_2_10_1_0());

                                    pushFollow(FOLLOW_ruleRuleRef_in_ruleActionableItemDef3174);
                                    lv_rules_22_0 = ruleRuleRef();

                                    state._fsp--;

                                    if (current == null) {
                                       current = createModelElementForParent(grammarAccess.getActionableItemDefRule());
                                    }
                                    add(current, "rules", lv_rules_22_0, "RuleRef");
                                    afterParserOrEnumRuleCall();

                                 }

                              }

                           }
                              break;

                           default:
                              break loop51;
                        }
                     } while (true);

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1605:4: (otherlv_23= 'children' otherlv_24= '{' (otherlv_25= 'actionableItem' ( (lv_children_26_0= ruleActionableItemDef ) ) )+ otherlv_27= '}' )?
                     int alt53 = 2;
                     int LA53_0 = input.LA(1);

                     if (LA53_0 == 37) {
                        alt53 = 1;
                     }
                     switch (alt53) {
                        case 1:
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1605:6: otherlv_23= 'children' otherlv_24= '{' (otherlv_25= 'actionableItem' ( (lv_children_26_0= ruleActionableItemDef ) ) )+ otherlv_27= '}'
                        {
                           otherlv_23 = (Token) match(input, 37, FOLLOW_37_in_ruleActionableItemDef3189);

                           newLeafNode(otherlv_23,
                              grammarAccess.getActionableItemDefAccess().getChildrenKeyword_2_11_0());

                           otherlv_24 = (Token) match(input, 17, FOLLOW_17_in_ruleActionableItemDef3201);

                           newLeafNode(otherlv_24,
                              grammarAccess.getActionableItemDefAccess().getLeftCurlyBracketKeyword_2_11_1());

                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1613:1: (otherlv_25= 'actionableItem' ( (lv_children_26_0= ruleActionableItemDef ) ) )+
                           int cnt52 = 0;
                           loop52: do {
                              int alt52 = 2;
                              int LA52_0 = input.LA(1);

                              if (LA52_0 == 13) {
                                 alt52 = 1;
                              }

                              switch (alt52) {
                                 case 1:
                                 // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1613:3: otherlv_25= 'actionableItem' ( (lv_children_26_0= ruleActionableItemDef ) )
                                 {
                                    otherlv_25 = (Token) match(input, 13, FOLLOW_13_in_ruleActionableItemDef3214);

                                    newLeafNode(otherlv_25,
                                       grammarAccess.getActionableItemDefAccess().getActionableItemKeyword_2_11_2_0());

                                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1617:1: ( (lv_children_26_0= ruleActionableItemDef ) )
                                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1618:1: (lv_children_26_0= ruleActionableItemDef )
                                    {
                                       // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1618:1: (lv_children_26_0= ruleActionableItemDef )
                                       // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1619:3: lv_children_26_0= ruleActionableItemDef
                                       {

                                          newCompositeNode(
                                             grammarAccess.getActionableItemDefAccess().getChildrenActionableItemDefParserRuleCall_2_11_2_1_0());

                                          pushFollow(FOLLOW_ruleActionableItemDef_in_ruleActionableItemDef3235);
                                          lv_children_26_0 = ruleActionableItemDef();

                                          state._fsp--;

                                          if (current == null) {
                                             current =
                                                createModelElementForParent(grammarAccess.getActionableItemDefRule());
                                          }
                                          add(current, "children", lv_children_26_0, "ActionableItemDef");
                                          afterParserOrEnumRuleCall();

                                       }

                                    }

                                 }
                                    break;

                                 default:
                                    if (cnt52 >= 1) {
                                       break loop52;
                                    }
                                    EarlyExitException eee = new EarlyExitException(52, input);
                                    throw eee;
                              }
                              cnt52++;
                           } while (true);

                           otherlv_27 = (Token) match(input, 22, FOLLOW_22_in_ruleActionableItemDef3249);

                           newLeafNode(otherlv_27,
                              grammarAccess.getActionableItemDefAccess().getRightCurlyBracketKeyword_2_11_3());

                        }
                           break;

                     }

                     otherlv_28 = (Token) match(input, 22, FOLLOW_22_in_ruleActionableItemDef3263);

                     newLeafNode(otherlv_28,
                        grammarAccess.getActionableItemDefAccess().getRightCurlyBracketKeyword_2_12());

                  }
                     break;

               }

            }

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleActionableItemDef"

   // $ANTLR start "entryRuleVERSION_DEF_REFERENCE"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1651:1: entryRuleVERSION_DEF_REFERENCE returns [String current=null] : iv_ruleVERSION_DEF_REFERENCE= ruleVERSION_DEF_REFERENCE EOF ;
   public final String entryRuleVERSION_DEF_REFERENCE() throws RecognitionException {
      String current = null;

      AntlrDatatypeRuleToken iv_ruleVERSION_DEF_REFERENCE = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1652:2: (iv_ruleVERSION_DEF_REFERENCE= ruleVERSION_DEF_REFERENCE EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1653:2: iv_ruleVERSION_DEF_REFERENCE= ruleVERSION_DEF_REFERENCE EOF
         {
            newCompositeNode(grammarAccess.getVERSION_DEF_REFERENCERule());
            pushFollow(FOLLOW_ruleVERSION_DEF_REFERENCE_in_entryRuleVERSION_DEF_REFERENCE3302);
            iv_ruleVERSION_DEF_REFERENCE = ruleVERSION_DEF_REFERENCE();

            state._fsp--;

            current = iv_ruleVERSION_DEF_REFERENCE.getText();
            match(input, EOF, FOLLOW_EOF_in_entryRuleVERSION_DEF_REFERENCE3313);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleVERSION_DEF_REFERENCE"

   // $ANTLR start "ruleVERSION_DEF_REFERENCE"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1660:1: ruleVERSION_DEF_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
   public final AntlrDatatypeRuleToken ruleVERSION_DEF_REFERENCE() throws RecognitionException {
      AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

      Token this_STRING_0 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1663:28: (this_STRING_0= RULE_STRING )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1664:5: this_STRING_0= RULE_STRING
         {
            this_STRING_0 = (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleVERSION_DEF_REFERENCE3352);

            current.merge(this_STRING_0);

            newLeafNode(this_STRING_0, grammarAccess.getVERSION_DEF_REFERENCEAccess().getSTRINGTerminalRuleCall());

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleVERSION_DEF_REFERENCE"

   // $ANTLR start "entryRuleVersionDef"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1679:1: entryRuleVersionDef returns [EObject current=null] : iv_ruleVersionDef= ruleVersionDef EOF ;
   public final EObject entryRuleVersionDef() throws RecognitionException {
      EObject current = null;

      EObject iv_ruleVersionDef = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1680:2: (iv_ruleVersionDef= ruleVersionDef EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1681:2: iv_ruleVersionDef= ruleVersionDef EOF
         {
            newCompositeNode(grammarAccess.getVersionDefRule());
            pushFollow(FOLLOW_ruleVersionDef_in_entryRuleVersionDef3396);
            iv_ruleVersionDef = ruleVersionDef();

            state._fsp--;

            current = iv_ruleVersionDef;
            match(input, EOF, FOLLOW_EOF_in_entryRuleVersionDef3406);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleVersionDef"

   // $ANTLR start "ruleVersionDef"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1688:1: ruleVersionDef returns [EObject current=null] : ( ( (lv_name_0_0= ruleVERSION_DEF_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'guid' ( (lv_guid_3_0= RULE_STRING ) ) )? (otherlv_4= 'uuid' ( (lv_uuid_5_0= RULE_INT ) ) )? (otherlv_6= 'staticId' ( (lv_staticId_7_0= RULE_STRING ) ) )* (otherlv_8= 'next' ( (lv_next_9_0= ruleBooleanDef ) ) )? (otherlv_10= 'released' ( (lv_released_11_0= ruleBooleanDef ) ) )? (otherlv_12= 'allowCreateBranch' ( (lv_allowCreateBranch_13_0= ruleBooleanDef ) ) )? (otherlv_14= 'allowCommitBranch' ( (lv_allowCommitBranch_15_0= ruleBooleanDef ) ) )? (otherlv_16= 'baselineBranchUuid' ( (lv_baselineBranchUuid_17_0= RULE_STRING ) ) )? (otherlv_18= 'parallelVersion' ( (lv_parallelVersion_19_0= RULE_STRING ) ) )* otherlv_20= '}' ) ;
   public final EObject ruleVersionDef() throws RecognitionException {
      EObject current = null;

      Token otherlv_1 = null;
      Token otherlv_2 = null;
      Token lv_guid_3_0 = null;
      Token otherlv_4 = null;
      Token lv_uuid_5_0 = null;
      Token otherlv_6 = null;
      Token lv_staticId_7_0 = null;
      Token otherlv_8 = null;
      Token otherlv_10 = null;
      Token otherlv_12 = null;
      Token otherlv_14 = null;
      Token otherlv_16 = null;
      Token lv_baselineBranchUuid_17_0 = null;
      Token otherlv_18 = null;
      Token lv_parallelVersion_19_0 = null;
      Token otherlv_20 = null;
      AntlrDatatypeRuleToken lv_name_0_0 = null;

      Enumerator lv_next_9_0 = null;

      Enumerator lv_released_11_0 = null;

      Enumerator lv_allowCreateBranch_13_0 = null;

      Enumerator lv_allowCommitBranch_15_0 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1691:28: ( ( ( (lv_name_0_0= ruleVERSION_DEF_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'guid' ( (lv_guid_3_0= RULE_STRING ) ) )? (otherlv_4= 'uuid' ( (lv_uuid_5_0= RULE_INT ) ) )? (otherlv_6= 'staticId' ( (lv_staticId_7_0= RULE_STRING ) ) )* (otherlv_8= 'next' ( (lv_next_9_0= ruleBooleanDef ) ) )? (otherlv_10= 'released' ( (lv_released_11_0= ruleBooleanDef ) ) )? (otherlv_12= 'allowCreateBranch' ( (lv_allowCreateBranch_13_0= ruleBooleanDef ) ) )? (otherlv_14= 'allowCommitBranch' ( (lv_allowCommitBranch_15_0= ruleBooleanDef ) ) )? (otherlv_16= 'baselineBranchUuid' ( (lv_baselineBranchUuid_17_0= RULE_STRING ) ) )? (otherlv_18= 'parallelVersion' ( (lv_parallelVersion_19_0= RULE_STRING ) ) )* otherlv_20= '}' ) )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1692:1: ( ( (lv_name_0_0= ruleVERSION_DEF_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'guid' ( (lv_guid_3_0= RULE_STRING ) ) )? (otherlv_4= 'uuid' ( (lv_uuid_5_0= RULE_INT ) ) )? (otherlv_6= 'staticId' ( (lv_staticId_7_0= RULE_STRING ) ) )* (otherlv_8= 'next' ( (lv_next_9_0= ruleBooleanDef ) ) )? (otherlv_10= 'released' ( (lv_released_11_0= ruleBooleanDef ) ) )? (otherlv_12= 'allowCreateBranch' ( (lv_allowCreateBranch_13_0= ruleBooleanDef ) ) )? (otherlv_14= 'allowCommitBranch' ( (lv_allowCommitBranch_15_0= ruleBooleanDef ) ) )? (otherlv_16= 'baselineBranchUuid' ( (lv_baselineBranchUuid_17_0= RULE_STRING ) ) )? (otherlv_18= 'parallelVersion' ( (lv_parallelVersion_19_0= RULE_STRING ) ) )* otherlv_20= '}' )
         {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1692:1: ( ( (lv_name_0_0= ruleVERSION_DEF_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'guid' ( (lv_guid_3_0= RULE_STRING ) ) )? (otherlv_4= 'uuid' ( (lv_uuid_5_0= RULE_INT ) ) )? (otherlv_6= 'staticId' ( (lv_staticId_7_0= RULE_STRING ) ) )* (otherlv_8= 'next' ( (lv_next_9_0= ruleBooleanDef ) ) )? (otherlv_10= 'released' ( (lv_released_11_0= ruleBooleanDef ) ) )? (otherlv_12= 'allowCreateBranch' ( (lv_allowCreateBranch_13_0= ruleBooleanDef ) ) )? (otherlv_14= 'allowCommitBranch' ( (lv_allowCommitBranch_15_0= ruleBooleanDef ) ) )? (otherlv_16= 'baselineBranchUuid' ( (lv_baselineBranchUuid_17_0= RULE_STRING ) ) )? (otherlv_18= 'parallelVersion' ( (lv_parallelVersion_19_0= RULE_STRING ) ) )* otherlv_20= '}' )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1692:2: ( (lv_name_0_0= ruleVERSION_DEF_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'guid' ( (lv_guid_3_0= RULE_STRING ) ) )? (otherlv_4= 'uuid' ( (lv_uuid_5_0= RULE_INT ) ) )? (otherlv_6= 'staticId' ( (lv_staticId_7_0= RULE_STRING ) ) )* (otherlv_8= 'next' ( (lv_next_9_0= ruleBooleanDef ) ) )? (otherlv_10= 'released' ( (lv_released_11_0= ruleBooleanDef ) ) )? (otherlv_12= 'allowCreateBranch' ( (lv_allowCreateBranch_13_0= ruleBooleanDef ) ) )? (otherlv_14= 'allowCommitBranch' ( (lv_allowCommitBranch_15_0= ruleBooleanDef ) ) )? (otherlv_16= 'baselineBranchUuid' ( (lv_baselineBranchUuid_17_0= RULE_STRING ) ) )? (otherlv_18= 'parallelVersion' ( (lv_parallelVersion_19_0= RULE_STRING ) ) )* otherlv_20= '}'
            {
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1692:2: ( (lv_name_0_0= ruleVERSION_DEF_REFERENCE ) )
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1693:1: (lv_name_0_0= ruleVERSION_DEF_REFERENCE )
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1693:1: (lv_name_0_0= ruleVERSION_DEF_REFERENCE )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1694:3: lv_name_0_0= ruleVERSION_DEF_REFERENCE
                  {

                     newCompositeNode(
                        grammarAccess.getVersionDefAccess().getNameVERSION_DEF_REFERENCEParserRuleCall_0_0());

                     pushFollow(FOLLOW_ruleVERSION_DEF_REFERENCE_in_ruleVersionDef3452);
                     lv_name_0_0 = ruleVERSION_DEF_REFERENCE();

                     state._fsp--;

                     if (current == null) {
                        current = createModelElementForParent(grammarAccess.getVersionDefRule());
                     }
                     set(current, "name", lv_name_0_0, "VERSION_DEF_REFERENCE");
                     afterParserOrEnumRuleCall();

                  }

               }

               otherlv_1 = (Token) match(input, 17, FOLLOW_17_in_ruleVersionDef3464);

               newLeafNode(otherlv_1, grammarAccess.getVersionDefAccess().getLeftCurlyBracketKeyword_1());

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1714:1: (otherlv_2= 'guid' ( (lv_guid_3_0= RULE_STRING ) ) )?
               int alt55 = 2;
               int LA55_0 = input.LA(1);

               if (LA55_0 == 25) {
                  alt55 = 1;
               }
               switch (alt55) {
                  case 1:
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1714:3: otherlv_2= 'guid' ( (lv_guid_3_0= RULE_STRING ) )
                  {
                     otherlv_2 = (Token) match(input, 25, FOLLOW_25_in_ruleVersionDef3477);

                     newLeafNode(otherlv_2, grammarAccess.getVersionDefAccess().getGuidKeyword_2_0());

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1718:1: ( (lv_guid_3_0= RULE_STRING ) )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1719:1: (lv_guid_3_0= RULE_STRING )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1719:1: (lv_guid_3_0= RULE_STRING )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1720:3: lv_guid_3_0= RULE_STRING
                        {
                           lv_guid_3_0 = (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleVersionDef3494);

                           newLeafNode(lv_guid_3_0,
                              grammarAccess.getVersionDefAccess().getGuidSTRINGTerminalRuleCall_2_1_0());

                           if (current == null) {
                              current = createModelElement(grammarAccess.getVersionDefRule());
                           }
                           setWithLastConsumed(current, "guid", lv_guid_3_0, "STRING");

                        }

                     }

                  }
                     break;

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1736:4: (otherlv_4= 'uuid' ( (lv_uuid_5_0= RULE_INT ) ) )?
               int alt56 = 2;
               int LA56_0 = input.LA(1);

               if (LA56_0 == 24) {
                  alt56 = 1;
               }
               switch (alt56) {
                  case 1:
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1736:6: otherlv_4= 'uuid' ( (lv_uuid_5_0= RULE_INT ) )
                  {
                     otherlv_4 = (Token) match(input, 24, FOLLOW_24_in_ruleVersionDef3514);

                     newLeafNode(otherlv_4, grammarAccess.getVersionDefAccess().getUuidKeyword_3_0());

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1740:1: ( (lv_uuid_5_0= RULE_INT ) )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1741:1: (lv_uuid_5_0= RULE_INT )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1741:1: (lv_uuid_5_0= RULE_INT )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1742:3: lv_uuid_5_0= RULE_INT
                        {
                           lv_uuid_5_0 = (Token) match(input, RULE_INT, FOLLOW_RULE_INT_in_ruleVersionDef3531);

                           newLeafNode(lv_uuid_5_0,
                              grammarAccess.getVersionDefAccess().getUuidINTTerminalRuleCall_3_1_0());

                           if (current == null) {
                              current = createModelElement(grammarAccess.getVersionDefRule());
                           }
                           setWithLastConsumed(current, "uuid", lv_uuid_5_0, "INT");

                        }

                     }

                  }
                     break;

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1758:4: (otherlv_6= 'staticId' ( (lv_staticId_7_0= RULE_STRING ) ) )*
               loop57: do {
                  int alt57 = 2;
                  int LA57_0 = input.LA(1);

                  if (LA57_0 == 29) {
                     alt57 = 1;
                  }

                  switch (alt57) {
                     case 1:
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1758:6: otherlv_6= 'staticId' ( (lv_staticId_7_0= RULE_STRING ) )
                     {
                        otherlv_6 = (Token) match(input, 29, FOLLOW_29_in_ruleVersionDef3551);

                        newLeafNode(otherlv_6, grammarAccess.getVersionDefAccess().getStaticIdKeyword_4_0());

                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1762:1: ( (lv_staticId_7_0= RULE_STRING ) )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1763:1: (lv_staticId_7_0= RULE_STRING )
                        {
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1763:1: (lv_staticId_7_0= RULE_STRING )
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1764:3: lv_staticId_7_0= RULE_STRING
                           {
                              lv_staticId_7_0 =
                                 (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleVersionDef3568);

                              newLeafNode(lv_staticId_7_0,
                                 grammarAccess.getVersionDefAccess().getStaticIdSTRINGTerminalRuleCall_4_1_0());

                              if (current == null) {
                                 current = createModelElement(grammarAccess.getVersionDefRule());
                              }
                              addWithLastConsumed(current, "staticId", lv_staticId_7_0, "STRING");

                           }

                        }

                     }
                        break;

                     default:
                        break loop57;
                  }
               } while (true);

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1780:4: (otherlv_8= 'next' ( (lv_next_9_0= ruleBooleanDef ) ) )?
               int alt58 = 2;
               int LA58_0 = input.LA(1);

               if (LA58_0 == 41) {
                  alt58 = 1;
               }
               switch (alt58) {
                  case 1:
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1780:6: otherlv_8= 'next' ( (lv_next_9_0= ruleBooleanDef ) )
                  {
                     otherlv_8 = (Token) match(input, 41, FOLLOW_41_in_ruleVersionDef3588);

                     newLeafNode(otherlv_8, grammarAccess.getVersionDefAccess().getNextKeyword_5_0());

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1784:1: ( (lv_next_9_0= ruleBooleanDef ) )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1785:1: (lv_next_9_0= ruleBooleanDef )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1785:1: (lv_next_9_0= ruleBooleanDef )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1786:3: lv_next_9_0= ruleBooleanDef
                        {

                           newCompositeNode(grammarAccess.getVersionDefAccess().getNextBooleanDefEnumRuleCall_5_1_0());

                           pushFollow(FOLLOW_ruleBooleanDef_in_ruleVersionDef3609);
                           lv_next_9_0 = ruleBooleanDef();

                           state._fsp--;

                           if (current == null) {
                              current = createModelElementForParent(grammarAccess.getVersionDefRule());
                           }
                           set(current, "next", lv_next_9_0, "BooleanDef");
                           afterParserOrEnumRuleCall();

                        }

                     }

                  }
                     break;

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1802:4: (otherlv_10= 'released' ( (lv_released_11_0= ruleBooleanDef ) ) )?
               int alt59 = 2;
               int LA59_0 = input.LA(1);

               if (LA59_0 == 42) {
                  alt59 = 1;
               }
               switch (alt59) {
                  case 1:
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1802:6: otherlv_10= 'released' ( (lv_released_11_0= ruleBooleanDef ) )
                  {
                     otherlv_10 = (Token) match(input, 42, FOLLOW_42_in_ruleVersionDef3624);

                     newLeafNode(otherlv_10, grammarAccess.getVersionDefAccess().getReleasedKeyword_6_0());

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1806:1: ( (lv_released_11_0= ruleBooleanDef ) )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1807:1: (lv_released_11_0= ruleBooleanDef )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1807:1: (lv_released_11_0= ruleBooleanDef )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1808:3: lv_released_11_0= ruleBooleanDef
                        {

                           newCompositeNode(
                              grammarAccess.getVersionDefAccess().getReleasedBooleanDefEnumRuleCall_6_1_0());

                           pushFollow(FOLLOW_ruleBooleanDef_in_ruleVersionDef3645);
                           lv_released_11_0 = ruleBooleanDef();

                           state._fsp--;

                           if (current == null) {
                              current = createModelElementForParent(grammarAccess.getVersionDefRule());
                           }
                           set(current, "released", lv_released_11_0, "BooleanDef");
                           afterParserOrEnumRuleCall();

                        }

                     }

                  }
                     break;

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1824:4: (otherlv_12= 'allowCreateBranch' ( (lv_allowCreateBranch_13_0= ruleBooleanDef ) ) )?
               int alt60 = 2;
               int LA60_0 = input.LA(1);

               if (LA60_0 == 43) {
                  alt60 = 1;
               }
               switch (alt60) {
                  case 1:
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1824:6: otherlv_12= 'allowCreateBranch' ( (lv_allowCreateBranch_13_0= ruleBooleanDef ) )
                  {
                     otherlv_12 = (Token) match(input, 43, FOLLOW_43_in_ruleVersionDef3660);

                     newLeafNode(otherlv_12, grammarAccess.getVersionDefAccess().getAllowCreateBranchKeyword_7_0());

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1828:1: ( (lv_allowCreateBranch_13_0= ruleBooleanDef ) )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1829:1: (lv_allowCreateBranch_13_0= ruleBooleanDef )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1829:1: (lv_allowCreateBranch_13_0= ruleBooleanDef )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1830:3: lv_allowCreateBranch_13_0= ruleBooleanDef
                        {

                           newCompositeNode(
                              grammarAccess.getVersionDefAccess().getAllowCreateBranchBooleanDefEnumRuleCall_7_1_0());

                           pushFollow(FOLLOW_ruleBooleanDef_in_ruleVersionDef3681);
                           lv_allowCreateBranch_13_0 = ruleBooleanDef();

                           state._fsp--;

                           if (current == null) {
                              current = createModelElementForParent(grammarAccess.getVersionDefRule());
                           }
                           set(current, "allowCreateBranch", lv_allowCreateBranch_13_0, "BooleanDef");
                           afterParserOrEnumRuleCall();

                        }

                     }

                  }
                     break;

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1846:4: (otherlv_14= 'allowCommitBranch' ( (lv_allowCommitBranch_15_0= ruleBooleanDef ) ) )?
               int alt61 = 2;
               int LA61_0 = input.LA(1);

               if (LA61_0 == 44) {
                  alt61 = 1;
               }
               switch (alt61) {
                  case 1:
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1846:6: otherlv_14= 'allowCommitBranch' ( (lv_allowCommitBranch_15_0= ruleBooleanDef ) )
                  {
                     otherlv_14 = (Token) match(input, 44, FOLLOW_44_in_ruleVersionDef3696);

                     newLeafNode(otherlv_14, grammarAccess.getVersionDefAccess().getAllowCommitBranchKeyword_8_0());

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1850:1: ( (lv_allowCommitBranch_15_0= ruleBooleanDef ) )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1851:1: (lv_allowCommitBranch_15_0= ruleBooleanDef )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1851:1: (lv_allowCommitBranch_15_0= ruleBooleanDef )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1852:3: lv_allowCommitBranch_15_0= ruleBooleanDef
                        {

                           newCompositeNode(
                              grammarAccess.getVersionDefAccess().getAllowCommitBranchBooleanDefEnumRuleCall_8_1_0());

                           pushFollow(FOLLOW_ruleBooleanDef_in_ruleVersionDef3717);
                           lv_allowCommitBranch_15_0 = ruleBooleanDef();

                           state._fsp--;

                           if (current == null) {
                              current = createModelElementForParent(grammarAccess.getVersionDefRule());
                           }
                           set(current, "allowCommitBranch", lv_allowCommitBranch_15_0, "BooleanDef");
                           afterParserOrEnumRuleCall();

                        }

                     }

                  }
                     break;

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1868:4: (otherlv_16= 'baselineBranchUuid' ( (lv_baselineBranchUuid_17_0= RULE_STRING ) ) )?
               int alt62 = 2;
               int LA62_0 = input.LA(1);

               if (LA62_0 == 45) {
                  alt62 = 1;
               }
               switch (alt62) {
                  case 1:
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1868:6: otherlv_16= 'baselineBranchUuid' ( (lv_baselineBranchUuid_17_0= RULE_STRING ) )
                  {
                     otherlv_16 = (Token) match(input, 45, FOLLOW_45_in_ruleVersionDef3732);

                     newLeafNode(otherlv_16, grammarAccess.getVersionDefAccess().getBaselineBranchUuidKeyword_9_0());

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1872:1: ( (lv_baselineBranchUuid_17_0= RULE_STRING ) )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1873:1: (lv_baselineBranchUuid_17_0= RULE_STRING )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1873:1: (lv_baselineBranchUuid_17_0= RULE_STRING )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1874:3: lv_baselineBranchUuid_17_0= RULE_STRING
                        {
                           lv_baselineBranchUuid_17_0 =
                              (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleVersionDef3749);

                           newLeafNode(lv_baselineBranchUuid_17_0,
                              grammarAccess.getVersionDefAccess().getBaselineBranchUuidSTRINGTerminalRuleCall_9_1_0());

                           if (current == null) {
                              current = createModelElement(grammarAccess.getVersionDefRule());
                           }
                           setWithLastConsumed(current, "baselineBranchUuid", lv_baselineBranchUuid_17_0, "STRING");

                        }

                     }

                  }
                     break;

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1890:4: (otherlv_18= 'parallelVersion' ( (lv_parallelVersion_19_0= RULE_STRING ) ) )*
               loop63: do {
                  int alt63 = 2;
                  int LA63_0 = input.LA(1);

                  if (LA63_0 == 46) {
                     alt63 = 1;
                  }

                  switch (alt63) {
                     case 1:
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1890:6: otherlv_18= 'parallelVersion' ( (lv_parallelVersion_19_0= RULE_STRING ) )
                     {
                        otherlv_18 = (Token) match(input, 46, FOLLOW_46_in_ruleVersionDef3769);

                        newLeafNode(otherlv_18, grammarAccess.getVersionDefAccess().getParallelVersionKeyword_10_0());

                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1894:1: ( (lv_parallelVersion_19_0= RULE_STRING ) )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1895:1: (lv_parallelVersion_19_0= RULE_STRING )
                        {
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1895:1: (lv_parallelVersion_19_0= RULE_STRING )
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1896:3: lv_parallelVersion_19_0= RULE_STRING
                           {
                              lv_parallelVersion_19_0 =
                                 (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleVersionDef3786);

                              newLeafNode(lv_parallelVersion_19_0,
                                 grammarAccess.getVersionDefAccess().getParallelVersionSTRINGTerminalRuleCall_10_1_0());

                              if (current == null) {
                                 current = createModelElement(grammarAccess.getVersionDefRule());
                              }
                              addWithLastConsumed(current, "parallelVersion", lv_parallelVersion_19_0, "STRING");

                           }

                        }

                     }
                        break;

                     default:
                        break loop63;
                  }
               } while (true);

               otherlv_20 = (Token) match(input, 22, FOLLOW_22_in_ruleVersionDef3805);

               newLeafNode(otherlv_20, grammarAccess.getVersionDefAccess().getRightCurlyBracketKeyword_11());

            }

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleVersionDef"

   // $ANTLR start "entryRuleWorkDef"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1924:1: entryRuleWorkDef returns [EObject current=null] : iv_ruleWorkDef= ruleWorkDef EOF ;
   public final EObject entryRuleWorkDef() throws RecognitionException {
      EObject current = null;

      EObject iv_ruleWorkDef = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1925:2: (iv_ruleWorkDef= ruleWorkDef EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1926:2: iv_ruleWorkDef= ruleWorkDef EOF
         {
            newCompositeNode(grammarAccess.getWorkDefRule());
            pushFollow(FOLLOW_ruleWorkDef_in_entryRuleWorkDef3841);
            iv_ruleWorkDef = ruleWorkDef();

            state._fsp--;

            current = iv_ruleWorkDef;
            match(input, EOF, FOLLOW_EOF_in_entryRuleWorkDef3851);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleWorkDef"

   // $ANTLR start "ruleWorkDef"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1933:1: ruleWorkDef returns [EObject current=null] : ( ( (lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'id' ( (lv_id_3_0= RULE_STRING ) ) )+ (otherlv_4= 'startState' ( ( ruleSTATE_NAME_REFERENCE ) ) ) ( (lv_widgetDefs_6_0= ruleWidgetDef ) )* ( (lv_decisionReviewDefs_7_0= ruleDecisionReviewDef ) )* ( (lv_peerReviewDefs_8_0= rulePeerReviewDef ) )* ( (lv_states_9_0= ruleStateDef ) )+ otherlv_10= '}' ) ;
   public final EObject ruleWorkDef() throws RecognitionException {
      EObject current = null;

      Token otherlv_1 = null;
      Token otherlv_2 = null;
      Token lv_id_3_0 = null;
      Token otherlv_4 = null;
      Token otherlv_10 = null;
      AntlrDatatypeRuleToken lv_name_0_0 = null;

      EObject lv_widgetDefs_6_0 = null;

      EObject lv_decisionReviewDefs_7_0 = null;

      EObject lv_peerReviewDefs_8_0 = null;

      EObject lv_states_9_0 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1936:28: ( ( ( (lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'id' ( (lv_id_3_0= RULE_STRING ) ) )+ (otherlv_4= 'startState' ( ( ruleSTATE_NAME_REFERENCE ) ) ) ( (lv_widgetDefs_6_0= ruleWidgetDef ) )* ( (lv_decisionReviewDefs_7_0= ruleDecisionReviewDef ) )* ( (lv_peerReviewDefs_8_0= rulePeerReviewDef ) )* ( (lv_states_9_0= ruleStateDef ) )+ otherlv_10= '}' ) )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1937:1: ( ( (lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'id' ( (lv_id_3_0= RULE_STRING ) ) )+ (otherlv_4= 'startState' ( ( ruleSTATE_NAME_REFERENCE ) ) ) ( (lv_widgetDefs_6_0= ruleWidgetDef ) )* ( (lv_decisionReviewDefs_7_0= ruleDecisionReviewDef ) )* ( (lv_peerReviewDefs_8_0= rulePeerReviewDef ) )* ( (lv_states_9_0= ruleStateDef ) )+ otherlv_10= '}' )
         {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1937:1: ( ( (lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'id' ( (lv_id_3_0= RULE_STRING ) ) )+ (otherlv_4= 'startState' ( ( ruleSTATE_NAME_REFERENCE ) ) ) ( (lv_widgetDefs_6_0= ruleWidgetDef ) )* ( (lv_decisionReviewDefs_7_0= ruleDecisionReviewDef ) )* ( (lv_peerReviewDefs_8_0= rulePeerReviewDef ) )* ( (lv_states_9_0= ruleStateDef ) )+ otherlv_10= '}' )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1937:2: ( (lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'id' ( (lv_id_3_0= RULE_STRING ) ) )+ (otherlv_4= 'startState' ( ( ruleSTATE_NAME_REFERENCE ) ) ) ( (lv_widgetDefs_6_0= ruleWidgetDef ) )* ( (lv_decisionReviewDefs_7_0= ruleDecisionReviewDef ) )* ( (lv_peerReviewDefs_8_0= rulePeerReviewDef ) )* ( (lv_states_9_0= ruleStateDef ) )+ otherlv_10= '}'
            {
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1937:2: ( (lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE ) )
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1938:1: (lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE )
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1938:1: (lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1939:3: lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE
                  {

                     newCompositeNode(
                        grammarAccess.getWorkDefAccess().getNameWORK_DEFINITION_NAME_REFERENCEParserRuleCall_0_0());

                     pushFollow(FOLLOW_ruleWORK_DEFINITION_NAME_REFERENCE_in_ruleWorkDef3897);
                     lv_name_0_0 = ruleWORK_DEFINITION_NAME_REFERENCE();

                     state._fsp--;

                     if (current == null) {
                        current = createModelElementForParent(grammarAccess.getWorkDefRule());
                     }
                     set(current, "name", lv_name_0_0, "WORK_DEFINITION_NAME_REFERENCE");
                     afterParserOrEnumRuleCall();

                  }

               }

               otherlv_1 = (Token) match(input, 17, FOLLOW_17_in_ruleWorkDef3909);

               newLeafNode(otherlv_1, grammarAccess.getWorkDefAccess().getLeftCurlyBracketKeyword_1());

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1959:1: (otherlv_2= 'id' ( (lv_id_3_0= RULE_STRING ) ) )+
               int cnt64 = 0;
               loop64: do {
                  int alt64 = 2;
                  int LA64_0 = input.LA(1);

                  if (LA64_0 == 47) {
                     alt64 = 1;
                  }

                  switch (alt64) {
                     case 1:
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1959:3: otherlv_2= 'id' ( (lv_id_3_0= RULE_STRING ) )
                     {
                        otherlv_2 = (Token) match(input, 47, FOLLOW_47_in_ruleWorkDef3922);

                        newLeafNode(otherlv_2, grammarAccess.getWorkDefAccess().getIdKeyword_2_0());

                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1963:1: ( (lv_id_3_0= RULE_STRING ) )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1964:1: (lv_id_3_0= RULE_STRING )
                        {
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1964:1: (lv_id_3_0= RULE_STRING )
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1965:3: lv_id_3_0= RULE_STRING
                           {
                              lv_id_3_0 = (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleWorkDef3939);

                              newLeafNode(lv_id_3_0,
                                 grammarAccess.getWorkDefAccess().getIdSTRINGTerminalRuleCall_2_1_0());

                              if (current == null) {
                                 current = createModelElement(grammarAccess.getWorkDefRule());
                              }
                              addWithLastConsumed(current, "id", lv_id_3_0, "STRING");

                           }

                        }

                     }
                        break;

                     default:
                        if (cnt64 >= 1) {
                           break loop64;
                        }
                        EarlyExitException eee = new EarlyExitException(64, input);
                        throw eee;
                  }
                  cnt64++;
               } while (true);

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1981:4: (otherlv_4= 'startState' ( ( ruleSTATE_NAME_REFERENCE ) ) )
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1981:6: otherlv_4= 'startState' ( ( ruleSTATE_NAME_REFERENCE ) )
               {
                  otherlv_4 = (Token) match(input, 48, FOLLOW_48_in_ruleWorkDef3959);

                  newLeafNode(otherlv_4, grammarAccess.getWorkDefAccess().getStartStateKeyword_3_0());

                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1985:1: ( ( ruleSTATE_NAME_REFERENCE ) )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1986:1: ( ruleSTATE_NAME_REFERENCE )
                  {
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1986:1: ( ruleSTATE_NAME_REFERENCE )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1987:3: ruleSTATE_NAME_REFERENCE
                     {

                        if (current == null) {
                           current = createModelElement(grammarAccess.getWorkDefRule());
                        }

                        newCompositeNode(grammarAccess.getWorkDefAccess().getStartStateStateDefCrossReference_3_1_0());

                        pushFollow(FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleWorkDef3982);
                        ruleSTATE_NAME_REFERENCE();

                        state._fsp--;

                        afterParserOrEnumRuleCall();

                     }

                  }

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2000:3: ( (lv_widgetDefs_6_0= ruleWidgetDef ) )*
               loop65: do {
                  int alt65 = 2;
                  int LA65_0 = input.LA(1);

                  if (LA65_0 == 49) {
                     alt65 = 1;
                  }

                  switch (alt65) {
                     case 1:
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2001:1: (lv_widgetDefs_6_0= ruleWidgetDef )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2001:1: (lv_widgetDefs_6_0= ruleWidgetDef )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2002:3: lv_widgetDefs_6_0= ruleWidgetDef
                        {

                           newCompositeNode(
                              grammarAccess.getWorkDefAccess().getWidgetDefsWidgetDefParserRuleCall_4_0());

                           pushFollow(FOLLOW_ruleWidgetDef_in_ruleWorkDef4004);
                           lv_widgetDefs_6_0 = ruleWidgetDef();

                           state._fsp--;

                           if (current == null) {
                              current = createModelElementForParent(grammarAccess.getWorkDefRule());
                           }
                           add(current, "widgetDefs", lv_widgetDefs_6_0, "WidgetDef");
                           afterParserOrEnumRuleCall();

                        }

                     }
                        break;

                     default:
                        break loop65;
                  }
               } while (true);

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2018:3: ( (lv_decisionReviewDefs_7_0= ruleDecisionReviewDef ) )*
               loop66: do {
                  int alt66 = 2;
                  int LA66_0 = input.LA(1);

                  if (LA66_0 == 68) {
                     alt66 = 1;
                  }

                  switch (alt66) {
                     case 1:
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2019:1: (lv_decisionReviewDefs_7_0= ruleDecisionReviewDef )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2019:1: (lv_decisionReviewDefs_7_0= ruleDecisionReviewDef )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2020:3: lv_decisionReviewDefs_7_0= ruleDecisionReviewDef
                        {

                           newCompositeNode(
                              grammarAccess.getWorkDefAccess().getDecisionReviewDefsDecisionReviewDefParserRuleCall_5_0());

                           pushFollow(FOLLOW_ruleDecisionReviewDef_in_ruleWorkDef4026);
                           lv_decisionReviewDefs_7_0 = ruleDecisionReviewDef();

                           state._fsp--;

                           if (current == null) {
                              current = createModelElementForParent(grammarAccess.getWorkDefRule());
                           }
                           add(current, "decisionReviewDefs", lv_decisionReviewDefs_7_0, "DecisionReviewDef");
                           afterParserOrEnumRuleCall();

                        }

                     }
                        break;

                     default:
                        break loop66;
                  }
               } while (true);

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2036:3: ( (lv_peerReviewDefs_8_0= rulePeerReviewDef ) )*
               loop67: do {
                  int alt67 = 2;
                  int LA67_0 = input.LA(1);

                  if (LA67_0 == 76) {
                     alt67 = 1;
                  }

                  switch (alt67) {
                     case 1:
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2037:1: (lv_peerReviewDefs_8_0= rulePeerReviewDef )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2037:1: (lv_peerReviewDefs_8_0= rulePeerReviewDef )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2038:3: lv_peerReviewDefs_8_0= rulePeerReviewDef
                        {

                           newCompositeNode(
                              grammarAccess.getWorkDefAccess().getPeerReviewDefsPeerReviewDefParserRuleCall_6_0());

                           pushFollow(FOLLOW_rulePeerReviewDef_in_ruleWorkDef4048);
                           lv_peerReviewDefs_8_0 = rulePeerReviewDef();

                           state._fsp--;

                           if (current == null) {
                              current = createModelElementForParent(grammarAccess.getWorkDefRule());
                           }
                           add(current, "peerReviewDefs", lv_peerReviewDefs_8_0, "PeerReviewDef");
                           afterParserOrEnumRuleCall();

                        }

                     }
                        break;

                     default:
                        break loop67;
                  }
               } while (true);

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2054:3: ( (lv_states_9_0= ruleStateDef ) )+
               int cnt68 = 0;
               loop68: do {
                  int alt68 = 2;
                  int LA68_0 = input.LA(1);

                  if (LA68_0 == 61) {
                     alt68 = 1;
                  }

                  switch (alt68) {
                     case 1:
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2055:1: (lv_states_9_0= ruleStateDef )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2055:1: (lv_states_9_0= ruleStateDef )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2056:3: lv_states_9_0= ruleStateDef
                        {

                           newCompositeNode(grammarAccess.getWorkDefAccess().getStatesStateDefParserRuleCall_7_0());

                           pushFollow(FOLLOW_ruleStateDef_in_ruleWorkDef4070);
                           lv_states_9_0 = ruleStateDef();

                           state._fsp--;

                           if (current == null) {
                              current = createModelElementForParent(grammarAccess.getWorkDefRule());
                           }
                           add(current, "states", lv_states_9_0, "StateDef");
                           afterParserOrEnumRuleCall();

                        }

                     }
                        break;

                     default:
                        if (cnt68 >= 1) {
                           break loop68;
                        }
                        EarlyExitException eee = new EarlyExitException(68, input);
                        throw eee;
                  }
                  cnt68++;
               } while (true);

               otherlv_10 = (Token) match(input, 22, FOLLOW_22_in_ruleWorkDef4083);

               newLeafNode(otherlv_10, grammarAccess.getWorkDefAccess().getRightCurlyBracketKeyword_8());

            }

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleWorkDef"

   // $ANTLR start "entryRuleWidgetDef"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2084:1: entryRuleWidgetDef returns [EObject current=null] : iv_ruleWidgetDef= ruleWidgetDef EOF ;
   public final EObject entryRuleWidgetDef() throws RecognitionException {
      EObject current = null;

      EObject iv_ruleWidgetDef = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2085:2: (iv_ruleWidgetDef= ruleWidgetDef EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2086:2: iv_ruleWidgetDef= ruleWidgetDef EOF
         {
            newCompositeNode(grammarAccess.getWidgetDefRule());
            pushFollow(FOLLOW_ruleWidgetDef_in_entryRuleWidgetDef4119);
            iv_ruleWidgetDef = ruleWidgetDef();

            state._fsp--;

            current = iv_ruleWidgetDef;
            match(input, EOF, FOLLOW_EOF_in_entryRuleWidgetDef4129);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleWidgetDef"

   // $ANTLR start "ruleWidgetDef"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2093:1: ruleWidgetDef returns [EObject current=null] : (otherlv_0= 'widgetDefinition' ( (lv_name_1_0= ruleWIDGET_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'attributeName' ( (lv_attributeName_4_0= RULE_STRING ) ) )? (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'xWidgetName' ( (lv_xWidgetName_8_0= RULE_STRING ) ) )? (otherlv_9= 'defaultValue' ( (lv_defaultValue_10_0= RULE_STRING ) ) )? (otherlv_11= 'height' ( (lv_height_12_0= RULE_INT ) ) )? (otherlv_13= 'option' ( (lv_option_14_0= ruleWidgetOption ) ) )* (otherlv_15= 'minConstraint' ( (lv_minConstraint_16_0= RULE_STRING ) ) )? (otherlv_17= 'maxConstraint' ( (lv_maxConstraint_18_0= RULE_STRING ) ) )? otherlv_19= '}' ) ;
   public final EObject ruleWidgetDef() throws RecognitionException {
      EObject current = null;

      Token otherlv_0 = null;
      Token otherlv_2 = null;
      Token otherlv_3 = null;
      Token lv_attributeName_4_0 = null;
      Token otherlv_5 = null;
      Token lv_description_6_0 = null;
      Token otherlv_7 = null;
      Token lv_xWidgetName_8_0 = null;
      Token otherlv_9 = null;
      Token lv_defaultValue_10_0 = null;
      Token otherlv_11 = null;
      Token lv_height_12_0 = null;
      Token otherlv_13 = null;
      Token otherlv_15 = null;
      Token lv_minConstraint_16_0 = null;
      Token otherlv_17 = null;
      Token lv_maxConstraint_18_0 = null;
      Token otherlv_19 = null;
      AntlrDatatypeRuleToken lv_name_1_0 = null;

      AntlrDatatypeRuleToken lv_option_14_0 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2096:28: ( (otherlv_0= 'widgetDefinition' ( (lv_name_1_0= ruleWIDGET_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'attributeName' ( (lv_attributeName_4_0= RULE_STRING ) ) )? (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'xWidgetName' ( (lv_xWidgetName_8_0= RULE_STRING ) ) )? (otherlv_9= 'defaultValue' ( (lv_defaultValue_10_0= RULE_STRING ) ) )? (otherlv_11= 'height' ( (lv_height_12_0= RULE_INT ) ) )? (otherlv_13= 'option' ( (lv_option_14_0= ruleWidgetOption ) ) )* (otherlv_15= 'minConstraint' ( (lv_minConstraint_16_0= RULE_STRING ) ) )? (otherlv_17= 'maxConstraint' ( (lv_maxConstraint_18_0= RULE_STRING ) ) )? otherlv_19= '}' ) )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2097:1: (otherlv_0= 'widgetDefinition' ( (lv_name_1_0= ruleWIDGET_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'attributeName' ( (lv_attributeName_4_0= RULE_STRING ) ) )? (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'xWidgetName' ( (lv_xWidgetName_8_0= RULE_STRING ) ) )? (otherlv_9= 'defaultValue' ( (lv_defaultValue_10_0= RULE_STRING ) ) )? (otherlv_11= 'height' ( (lv_height_12_0= RULE_INT ) ) )? (otherlv_13= 'option' ( (lv_option_14_0= ruleWidgetOption ) ) )* (otherlv_15= 'minConstraint' ( (lv_minConstraint_16_0= RULE_STRING ) ) )? (otherlv_17= 'maxConstraint' ( (lv_maxConstraint_18_0= RULE_STRING ) ) )? otherlv_19= '}' )
         {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2097:1: (otherlv_0= 'widgetDefinition' ( (lv_name_1_0= ruleWIDGET_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'attributeName' ( (lv_attributeName_4_0= RULE_STRING ) ) )? (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'xWidgetName' ( (lv_xWidgetName_8_0= RULE_STRING ) ) )? (otherlv_9= 'defaultValue' ( (lv_defaultValue_10_0= RULE_STRING ) ) )? (otherlv_11= 'height' ( (lv_height_12_0= RULE_INT ) ) )? (otherlv_13= 'option' ( (lv_option_14_0= ruleWidgetOption ) ) )* (otherlv_15= 'minConstraint' ( (lv_minConstraint_16_0= RULE_STRING ) ) )? (otherlv_17= 'maxConstraint' ( (lv_maxConstraint_18_0= RULE_STRING ) ) )? otherlv_19= '}' )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2097:3: otherlv_0= 'widgetDefinition' ( (lv_name_1_0= ruleWIDGET_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'attributeName' ( (lv_attributeName_4_0= RULE_STRING ) ) )? (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'xWidgetName' ( (lv_xWidgetName_8_0= RULE_STRING ) ) )? (otherlv_9= 'defaultValue' ( (lv_defaultValue_10_0= RULE_STRING ) ) )? (otherlv_11= 'height' ( (lv_height_12_0= RULE_INT ) ) )? (otherlv_13= 'option' ( (lv_option_14_0= ruleWidgetOption ) ) )* (otherlv_15= 'minConstraint' ( (lv_minConstraint_16_0= RULE_STRING ) ) )? (otherlv_17= 'maxConstraint' ( (lv_maxConstraint_18_0= RULE_STRING ) ) )? otherlv_19= '}'
            {
               otherlv_0 = (Token) match(input, 49, FOLLOW_49_in_ruleWidgetDef4166);

               newLeafNode(otherlv_0, grammarAccess.getWidgetDefAccess().getWidgetDefinitionKeyword_0());

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2101:1: ( (lv_name_1_0= ruleWIDGET_NAME_REFERENCE ) )
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2102:1: (lv_name_1_0= ruleWIDGET_NAME_REFERENCE )
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2102:1: (lv_name_1_0= ruleWIDGET_NAME_REFERENCE )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2103:3: lv_name_1_0= ruleWIDGET_NAME_REFERENCE
                  {

                     newCompositeNode(
                        grammarAccess.getWidgetDefAccess().getNameWIDGET_NAME_REFERENCEParserRuleCall_1_0());

                     pushFollow(FOLLOW_ruleWIDGET_NAME_REFERENCE_in_ruleWidgetDef4187);
                     lv_name_1_0 = ruleWIDGET_NAME_REFERENCE();

                     state._fsp--;

                     if (current == null) {
                        current = createModelElementForParent(grammarAccess.getWidgetDefRule());
                     }
                     set(current, "name", lv_name_1_0, "WIDGET_NAME_REFERENCE");
                     afterParserOrEnumRuleCall();

                  }

               }

               otherlv_2 = (Token) match(input, 17, FOLLOW_17_in_ruleWidgetDef4199);

               newLeafNode(otherlv_2, grammarAccess.getWidgetDefAccess().getLeftCurlyBracketKeyword_2());

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2123:1: (otherlv_3= 'attributeName' ( (lv_attributeName_4_0= RULE_STRING ) ) )?
               int alt69 = 2;
               int LA69_0 = input.LA(1);

               if (LA69_0 == 50) {
                  alt69 = 1;
               }
               switch (alt69) {
                  case 1:
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2123:3: otherlv_3= 'attributeName' ( (lv_attributeName_4_0= RULE_STRING ) )
                  {
                     otherlv_3 = (Token) match(input, 50, FOLLOW_50_in_ruleWidgetDef4212);

                     newLeafNode(otherlv_3, grammarAccess.getWidgetDefAccess().getAttributeNameKeyword_3_0());

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2127:1: ( (lv_attributeName_4_0= RULE_STRING ) )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2128:1: (lv_attributeName_4_0= RULE_STRING )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2128:1: (lv_attributeName_4_0= RULE_STRING )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2129:3: lv_attributeName_4_0= RULE_STRING
                        {
                           lv_attributeName_4_0 =
                              (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleWidgetDef4229);

                           newLeafNode(lv_attributeName_4_0,
                              grammarAccess.getWidgetDefAccess().getAttributeNameSTRINGTerminalRuleCall_3_1_0());

                           if (current == null) {
                              current = createModelElement(grammarAccess.getWidgetDefRule());
                           }
                           setWithLastConsumed(current, "attributeName", lv_attributeName_4_0, "STRING");

                        }

                     }

                  }
                     break;

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2145:4: (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )?
               int alt70 = 2;
               int LA70_0 = input.LA(1);

               if (LA70_0 == 51) {
                  alt70 = 1;
               }
               switch (alt70) {
                  case 1:
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2145:6: otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) )
                  {
                     otherlv_5 = (Token) match(input, 51, FOLLOW_51_in_ruleWidgetDef4249);

                     newLeafNode(otherlv_5, grammarAccess.getWidgetDefAccess().getDescriptionKeyword_4_0());

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2149:1: ( (lv_description_6_0= RULE_STRING ) )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2150:1: (lv_description_6_0= RULE_STRING )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2150:1: (lv_description_6_0= RULE_STRING )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2151:3: lv_description_6_0= RULE_STRING
                        {
                           lv_description_6_0 =
                              (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleWidgetDef4266);

                           newLeafNode(lv_description_6_0,
                              grammarAccess.getWidgetDefAccess().getDescriptionSTRINGTerminalRuleCall_4_1_0());

                           if (current == null) {
                              current = createModelElement(grammarAccess.getWidgetDefRule());
                           }
                           setWithLastConsumed(current, "description", lv_description_6_0, "STRING");

                        }

                     }

                  }
                     break;

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2167:4: (otherlv_7= 'xWidgetName' ( (lv_xWidgetName_8_0= RULE_STRING ) ) )?
               int alt71 = 2;
               int LA71_0 = input.LA(1);

               if (LA71_0 == 52) {
                  alt71 = 1;
               }
               switch (alt71) {
                  case 1:
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2167:6: otherlv_7= 'xWidgetName' ( (lv_xWidgetName_8_0= RULE_STRING ) )
                  {
                     otherlv_7 = (Token) match(input, 52, FOLLOW_52_in_ruleWidgetDef4286);

                     newLeafNode(otherlv_7, grammarAccess.getWidgetDefAccess().getXWidgetNameKeyword_5_0());

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2171:1: ( (lv_xWidgetName_8_0= RULE_STRING ) )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2172:1: (lv_xWidgetName_8_0= RULE_STRING )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2172:1: (lv_xWidgetName_8_0= RULE_STRING )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2173:3: lv_xWidgetName_8_0= RULE_STRING
                        {
                           lv_xWidgetName_8_0 =
                              (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleWidgetDef4303);

                           newLeafNode(lv_xWidgetName_8_0,
                              grammarAccess.getWidgetDefAccess().getXWidgetNameSTRINGTerminalRuleCall_5_1_0());

                           if (current == null) {
                              current = createModelElement(grammarAccess.getWidgetDefRule());
                           }
                           setWithLastConsumed(current, "xWidgetName", lv_xWidgetName_8_0, "STRING");

                        }

                     }

                  }
                     break;

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2189:4: (otherlv_9= 'defaultValue' ( (lv_defaultValue_10_0= RULE_STRING ) ) )?
               int alt72 = 2;
               int LA72_0 = input.LA(1);

               if (LA72_0 == 53) {
                  alt72 = 1;
               }
               switch (alt72) {
                  case 1:
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2189:6: otherlv_9= 'defaultValue' ( (lv_defaultValue_10_0= RULE_STRING ) )
                  {
                     otherlv_9 = (Token) match(input, 53, FOLLOW_53_in_ruleWidgetDef4323);

                     newLeafNode(otherlv_9, grammarAccess.getWidgetDefAccess().getDefaultValueKeyword_6_0());

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2193:1: ( (lv_defaultValue_10_0= RULE_STRING ) )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2194:1: (lv_defaultValue_10_0= RULE_STRING )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2194:1: (lv_defaultValue_10_0= RULE_STRING )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2195:3: lv_defaultValue_10_0= RULE_STRING
                        {
                           lv_defaultValue_10_0 =
                              (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleWidgetDef4340);

                           newLeafNode(lv_defaultValue_10_0,
                              grammarAccess.getWidgetDefAccess().getDefaultValueSTRINGTerminalRuleCall_6_1_0());

                           if (current == null) {
                              current = createModelElement(grammarAccess.getWidgetDefRule());
                           }
                           setWithLastConsumed(current, "defaultValue", lv_defaultValue_10_0, "STRING");

                        }

                     }

                  }
                     break;

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2211:4: (otherlv_11= 'height' ( (lv_height_12_0= RULE_INT ) ) )?
               int alt73 = 2;
               int LA73_0 = input.LA(1);

               if (LA73_0 == 54) {
                  alt73 = 1;
               }
               switch (alt73) {
                  case 1:
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2211:6: otherlv_11= 'height' ( (lv_height_12_0= RULE_INT ) )
                  {
                     otherlv_11 = (Token) match(input, 54, FOLLOW_54_in_ruleWidgetDef4360);

                     newLeafNode(otherlv_11, grammarAccess.getWidgetDefAccess().getHeightKeyword_7_0());

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2215:1: ( (lv_height_12_0= RULE_INT ) )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2216:1: (lv_height_12_0= RULE_INT )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2216:1: (lv_height_12_0= RULE_INT )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2217:3: lv_height_12_0= RULE_INT
                        {
                           lv_height_12_0 = (Token) match(input, RULE_INT, FOLLOW_RULE_INT_in_ruleWidgetDef4377);

                           newLeafNode(lv_height_12_0,
                              grammarAccess.getWidgetDefAccess().getHeightINTTerminalRuleCall_7_1_0());

                           if (current == null) {
                              current = createModelElement(grammarAccess.getWidgetDefRule());
                           }
                           setWithLastConsumed(current, "height", lv_height_12_0, "INT");

                        }

                     }

                  }
                     break;

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2233:4: (otherlv_13= 'option' ( (lv_option_14_0= ruleWidgetOption ) ) )*
               loop74: do {
                  int alt74 = 2;
                  int LA74_0 = input.LA(1);

                  if (LA74_0 == 55) {
                     alt74 = 1;
                  }

                  switch (alt74) {
                     case 1:
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2233:6: otherlv_13= 'option' ( (lv_option_14_0= ruleWidgetOption ) )
                     {
                        otherlv_13 = (Token) match(input, 55, FOLLOW_55_in_ruleWidgetDef4397);

                        newLeafNode(otherlv_13, grammarAccess.getWidgetDefAccess().getOptionKeyword_8_0());

                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2237:1: ( (lv_option_14_0= ruleWidgetOption ) )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2238:1: (lv_option_14_0= ruleWidgetOption )
                        {
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2238:1: (lv_option_14_0= ruleWidgetOption )
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2239:3: lv_option_14_0= ruleWidgetOption
                           {

                              newCompositeNode(
                                 grammarAccess.getWidgetDefAccess().getOptionWidgetOptionParserRuleCall_8_1_0());

                              pushFollow(FOLLOW_ruleWidgetOption_in_ruleWidgetDef4418);
                              lv_option_14_0 = ruleWidgetOption();

                              state._fsp--;

                              if (current == null) {
                                 current = createModelElementForParent(grammarAccess.getWidgetDefRule());
                              }
                              add(current, "option", lv_option_14_0, "WidgetOption");
                              afterParserOrEnumRuleCall();

                           }

                        }

                     }
                        break;

                     default:
                        break loop74;
                  }
               } while (true);

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2255:4: (otherlv_15= 'minConstraint' ( (lv_minConstraint_16_0= RULE_STRING ) ) )?
               int alt75 = 2;
               int LA75_0 = input.LA(1);

               if (LA75_0 == 56) {
                  alt75 = 1;
               }
               switch (alt75) {
                  case 1:
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2255:6: otherlv_15= 'minConstraint' ( (lv_minConstraint_16_0= RULE_STRING ) )
                  {
                     otherlv_15 = (Token) match(input, 56, FOLLOW_56_in_ruleWidgetDef4433);

                     newLeafNode(otherlv_15, grammarAccess.getWidgetDefAccess().getMinConstraintKeyword_9_0());

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2259:1: ( (lv_minConstraint_16_0= RULE_STRING ) )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2260:1: (lv_minConstraint_16_0= RULE_STRING )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2260:1: (lv_minConstraint_16_0= RULE_STRING )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2261:3: lv_minConstraint_16_0= RULE_STRING
                        {
                           lv_minConstraint_16_0 =
                              (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleWidgetDef4450);

                           newLeafNode(lv_minConstraint_16_0,
                              grammarAccess.getWidgetDefAccess().getMinConstraintSTRINGTerminalRuleCall_9_1_0());

                           if (current == null) {
                              current = createModelElement(grammarAccess.getWidgetDefRule());
                           }
                           setWithLastConsumed(current, "minConstraint", lv_minConstraint_16_0, "STRING");

                        }

                     }

                  }
                     break;

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2277:4: (otherlv_17= 'maxConstraint' ( (lv_maxConstraint_18_0= RULE_STRING ) ) )?
               int alt76 = 2;
               int LA76_0 = input.LA(1);

               if (LA76_0 == 57) {
                  alt76 = 1;
               }
               switch (alt76) {
                  case 1:
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2277:6: otherlv_17= 'maxConstraint' ( (lv_maxConstraint_18_0= RULE_STRING ) )
                  {
                     otherlv_17 = (Token) match(input, 57, FOLLOW_57_in_ruleWidgetDef4470);

                     newLeafNode(otherlv_17, grammarAccess.getWidgetDefAccess().getMaxConstraintKeyword_10_0());

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2281:1: ( (lv_maxConstraint_18_0= RULE_STRING ) )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2282:1: (lv_maxConstraint_18_0= RULE_STRING )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2282:1: (lv_maxConstraint_18_0= RULE_STRING )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2283:3: lv_maxConstraint_18_0= RULE_STRING
                        {
                           lv_maxConstraint_18_0 =
                              (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleWidgetDef4487);

                           newLeafNode(lv_maxConstraint_18_0,
                              grammarAccess.getWidgetDefAccess().getMaxConstraintSTRINGTerminalRuleCall_10_1_0());

                           if (current == null) {
                              current = createModelElement(grammarAccess.getWidgetDefRule());
                           }
                           setWithLastConsumed(current, "maxConstraint", lv_maxConstraint_18_0, "STRING");

                        }

                     }

                  }
                     break;

               }

               otherlv_19 = (Token) match(input, 22, FOLLOW_22_in_ruleWidgetDef4506);

               newLeafNode(otherlv_19, grammarAccess.getWidgetDefAccess().getRightCurlyBracketKeyword_11());

            }

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleWidgetDef"

   // $ANTLR start "entryRuleWidgetRef"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2311:1: entryRuleWidgetRef returns [EObject current=null] : iv_ruleWidgetRef= ruleWidgetRef EOF ;
   public final EObject entryRuleWidgetRef() throws RecognitionException {
      EObject current = null;

      EObject iv_ruleWidgetRef = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2312:2: (iv_ruleWidgetRef= ruleWidgetRef EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2313:2: iv_ruleWidgetRef= ruleWidgetRef EOF
         {
            newCompositeNode(grammarAccess.getWidgetRefRule());
            pushFollow(FOLLOW_ruleWidgetRef_in_entryRuleWidgetRef4542);
            iv_ruleWidgetRef = ruleWidgetRef();

            state._fsp--;

            current = iv_ruleWidgetRef;
            match(input, EOF, FOLLOW_EOF_in_entryRuleWidgetRef4552);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleWidgetRef"

   // $ANTLR start "ruleWidgetRef"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2320:1: ruleWidgetRef returns [EObject current=null] : (otherlv_0= 'widget' ( ( ruleWIDGET_NAME_REFERENCE ) ) ) ;
   public final EObject ruleWidgetRef() throws RecognitionException {
      EObject current = null;

      Token otherlv_0 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2323:28: ( (otherlv_0= 'widget' ( ( ruleWIDGET_NAME_REFERENCE ) ) ) )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2324:1: (otherlv_0= 'widget' ( ( ruleWIDGET_NAME_REFERENCE ) ) )
         {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2324:1: (otherlv_0= 'widget' ( ( ruleWIDGET_NAME_REFERENCE ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2324:3: otherlv_0= 'widget' ( ( ruleWIDGET_NAME_REFERENCE ) )
            {
               otherlv_0 = (Token) match(input, 58, FOLLOW_58_in_ruleWidgetRef4589);

               newLeafNode(otherlv_0, grammarAccess.getWidgetRefAccess().getWidgetKeyword_0());

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2328:1: ( ( ruleWIDGET_NAME_REFERENCE ) )
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2329:1: ( ruleWIDGET_NAME_REFERENCE )
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2329:1: ( ruleWIDGET_NAME_REFERENCE )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2330:3: ruleWIDGET_NAME_REFERENCE
                  {

                     if (current == null) {
                        current = createModelElement(grammarAccess.getWidgetRefRule());
                     }

                     newCompositeNode(grammarAccess.getWidgetRefAccess().getWidgetWidgetDefCrossReference_1_0());

                     pushFollow(FOLLOW_ruleWIDGET_NAME_REFERENCE_in_ruleWidgetRef4612);
                     ruleWIDGET_NAME_REFERENCE();

                     state._fsp--;

                     afterParserOrEnumRuleCall();

                  }

               }

            }

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleWidgetRef"

   // $ANTLR start "entryRuleAttrWidget"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2351:1: entryRuleAttrWidget returns [EObject current=null] : iv_ruleAttrWidget= ruleAttrWidget EOF ;
   public final EObject entryRuleAttrWidget() throws RecognitionException {
      EObject current = null;

      EObject iv_ruleAttrWidget = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2352:2: (iv_ruleAttrWidget= ruleAttrWidget EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2353:2: iv_ruleAttrWidget= ruleAttrWidget EOF
         {
            newCompositeNode(grammarAccess.getAttrWidgetRule());
            pushFollow(FOLLOW_ruleAttrWidget_in_entryRuleAttrWidget4648);
            iv_ruleAttrWidget = ruleAttrWidget();

            state._fsp--;

            current = iv_ruleAttrWidget;
            match(input, EOF, FOLLOW_EOF_in_entryRuleAttrWidget4658);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleAttrWidget"

   // $ANTLR start "ruleAttrWidget"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2360:1: ruleAttrWidget returns [EObject current=null] : (otherlv_0= 'attributeWidget' ( (lv_attributeName_1_0= RULE_STRING ) ) (otherlv_2= 'with' ( (lv_option_3_0= ruleWidgetOption ) ) )* ) ;
   public final EObject ruleAttrWidget() throws RecognitionException {
      EObject current = null;

      Token otherlv_0 = null;
      Token lv_attributeName_1_0 = null;
      Token otherlv_2 = null;
      AntlrDatatypeRuleToken lv_option_3_0 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2363:28: ( (otherlv_0= 'attributeWidget' ( (lv_attributeName_1_0= RULE_STRING ) ) (otherlv_2= 'with' ( (lv_option_3_0= ruleWidgetOption ) ) )* ) )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2364:1: (otherlv_0= 'attributeWidget' ( (lv_attributeName_1_0= RULE_STRING ) ) (otherlv_2= 'with' ( (lv_option_3_0= ruleWidgetOption ) ) )* )
         {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2364:1: (otherlv_0= 'attributeWidget' ( (lv_attributeName_1_0= RULE_STRING ) ) (otherlv_2= 'with' ( (lv_option_3_0= ruleWidgetOption ) ) )* )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2364:3: otherlv_0= 'attributeWidget' ( (lv_attributeName_1_0= RULE_STRING ) ) (otherlv_2= 'with' ( (lv_option_3_0= ruleWidgetOption ) ) )*
            {
               otherlv_0 = (Token) match(input, 59, FOLLOW_59_in_ruleAttrWidget4695);

               newLeafNode(otherlv_0, grammarAccess.getAttrWidgetAccess().getAttributeWidgetKeyword_0());

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2368:1: ( (lv_attributeName_1_0= RULE_STRING ) )
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2369:1: (lv_attributeName_1_0= RULE_STRING )
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2369:1: (lv_attributeName_1_0= RULE_STRING )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2370:3: lv_attributeName_1_0= RULE_STRING
                  {
                     lv_attributeName_1_0 = (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleAttrWidget4712);

                     newLeafNode(lv_attributeName_1_0,
                        grammarAccess.getAttrWidgetAccess().getAttributeNameSTRINGTerminalRuleCall_1_0());

                     if (current == null) {
                        current = createModelElement(grammarAccess.getAttrWidgetRule());
                     }
                     setWithLastConsumed(current, "attributeName", lv_attributeName_1_0, "STRING");

                  }

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2386:2: (otherlv_2= 'with' ( (lv_option_3_0= ruleWidgetOption ) ) )*
               loop77: do {
                  int alt77 = 2;
                  int LA77_0 = input.LA(1);

                  if (LA77_0 == 60) {
                     alt77 = 1;
                  }

                  switch (alt77) {
                     case 1:
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2386:4: otherlv_2= 'with' ( (lv_option_3_0= ruleWidgetOption ) )
                     {
                        otherlv_2 = (Token) match(input, 60, FOLLOW_60_in_ruleAttrWidget4730);

                        newLeafNode(otherlv_2, grammarAccess.getAttrWidgetAccess().getWithKeyword_2_0());

                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2390:1: ( (lv_option_3_0= ruleWidgetOption ) )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2391:1: (lv_option_3_0= ruleWidgetOption )
                        {
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2391:1: (lv_option_3_0= ruleWidgetOption )
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2392:3: lv_option_3_0= ruleWidgetOption
                           {

                              newCompositeNode(
                                 grammarAccess.getAttrWidgetAccess().getOptionWidgetOptionParserRuleCall_2_1_0());

                              pushFollow(FOLLOW_ruleWidgetOption_in_ruleAttrWidget4751);
                              lv_option_3_0 = ruleWidgetOption();

                              state._fsp--;

                              if (current == null) {
                                 current = createModelElementForParent(grammarAccess.getAttrWidgetRule());
                              }
                              add(current, "option", lv_option_3_0, "WidgetOption");
                              afterParserOrEnumRuleCall();

                           }

                        }

                     }
                        break;

                     default:
                        break loop77;
                  }
               } while (true);

            }

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleAttrWidget"

   // $ANTLR start "entryRuleStateDef"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2416:1: entryRuleStateDef returns [EObject current=null] : iv_ruleStateDef= ruleStateDef EOF ;
   public final EObject entryRuleStateDef() throws RecognitionException {
      EObject current = null;

      EObject iv_ruleStateDef = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2417:2: (iv_ruleStateDef= ruleStateDef EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2418:2: iv_ruleStateDef= ruleStateDef EOF
         {
            newCompositeNode(grammarAccess.getStateDefRule());
            pushFollow(FOLLOW_ruleStateDef_in_entryRuleStateDef4789);
            iv_ruleStateDef = ruleStateDef();

            state._fsp--;

            current = iv_ruleStateDef;
            match(input, EOF, FOLLOW_EOF_in_entryRuleStateDef4799);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleStateDef"

   // $ANTLR start "ruleStateDef"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2425:1: ruleStateDef returns [EObject current=null] : (otherlv_0= 'state' ( (lv_name_1_0= ruleSTATE_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'description' ( (lv_description_4_0= RULE_STRING ) ) )? otherlv_5= 'type' ( (lv_pageType_6_0= rulePageType ) ) otherlv_7= 'ordinal' ( (lv_ordinal_8_0= RULE_INT ) ) ( (lv_transitionStates_9_0= ruleToState ) )* (otherlv_10= 'rule' ( (lv_rules_11_0= ruleRuleRef ) ) )* ( (lv_decisionReviews_12_0= ruleDecisionReviewRef ) )* ( (lv_peerReviews_13_0= rulePeerReviewRef ) )* (otherlv_14= 'percentWeight' ( (lv_percentWeight_15_0= RULE_INT ) ) )? (otherlv_16= 'recommendedPercentComplete' ( (lv_recommendedPercentComplete_17_0= RULE_INT ) ) )? (otherlv_18= 'color' ( (lv_color_19_0= ruleStateColor ) ) )? ( (lv_layout_20_0= ruleLayoutType ) )? otherlv_21= '}' ) ;
   public final EObject ruleStateDef() throws RecognitionException {
      EObject current = null;

      Token otherlv_0 = null;
      Token otherlv_2 = null;
      Token otherlv_3 = null;
      Token lv_description_4_0 = null;
      Token otherlv_5 = null;
      Token otherlv_7 = null;
      Token lv_ordinal_8_0 = null;
      Token otherlv_10 = null;
      Token otherlv_14 = null;
      Token lv_percentWeight_15_0 = null;
      Token otherlv_16 = null;
      Token lv_recommendedPercentComplete_17_0 = null;
      Token otherlv_18 = null;
      Token otherlv_21 = null;
      AntlrDatatypeRuleToken lv_name_1_0 = null;

      AntlrDatatypeRuleToken lv_pageType_6_0 = null;

      EObject lv_transitionStates_9_0 = null;

      AntlrDatatypeRuleToken lv_rules_11_0 = null;

      EObject lv_decisionReviews_12_0 = null;

      EObject lv_peerReviews_13_0 = null;

      AntlrDatatypeRuleToken lv_color_19_0 = null;

      EObject lv_layout_20_0 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2428:28: ( (otherlv_0= 'state' ( (lv_name_1_0= ruleSTATE_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'description' ( (lv_description_4_0= RULE_STRING ) ) )? otherlv_5= 'type' ( (lv_pageType_6_0= rulePageType ) ) otherlv_7= 'ordinal' ( (lv_ordinal_8_0= RULE_INT ) ) ( (lv_transitionStates_9_0= ruleToState ) )* (otherlv_10= 'rule' ( (lv_rules_11_0= ruleRuleRef ) ) )* ( (lv_decisionReviews_12_0= ruleDecisionReviewRef ) )* ( (lv_peerReviews_13_0= rulePeerReviewRef ) )* (otherlv_14= 'percentWeight' ( (lv_percentWeight_15_0= RULE_INT ) ) )? (otherlv_16= 'recommendedPercentComplete' ( (lv_recommendedPercentComplete_17_0= RULE_INT ) ) )? (otherlv_18= 'color' ( (lv_color_19_0= ruleStateColor ) ) )? ( (lv_layout_20_0= ruleLayoutType ) )? otherlv_21= '}' ) )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2429:1: (otherlv_0= 'state' ( (lv_name_1_0= ruleSTATE_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'description' ( (lv_description_4_0= RULE_STRING ) ) )? otherlv_5= 'type' ( (lv_pageType_6_0= rulePageType ) ) otherlv_7= 'ordinal' ( (lv_ordinal_8_0= RULE_INT ) ) ( (lv_transitionStates_9_0= ruleToState ) )* (otherlv_10= 'rule' ( (lv_rules_11_0= ruleRuleRef ) ) )* ( (lv_decisionReviews_12_0= ruleDecisionReviewRef ) )* ( (lv_peerReviews_13_0= rulePeerReviewRef ) )* (otherlv_14= 'percentWeight' ( (lv_percentWeight_15_0= RULE_INT ) ) )? (otherlv_16= 'recommendedPercentComplete' ( (lv_recommendedPercentComplete_17_0= RULE_INT ) ) )? (otherlv_18= 'color' ( (lv_color_19_0= ruleStateColor ) ) )? ( (lv_layout_20_0= ruleLayoutType ) )? otherlv_21= '}' )
         {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2429:1: (otherlv_0= 'state' ( (lv_name_1_0= ruleSTATE_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'description' ( (lv_description_4_0= RULE_STRING ) ) )? otherlv_5= 'type' ( (lv_pageType_6_0= rulePageType ) ) otherlv_7= 'ordinal' ( (lv_ordinal_8_0= RULE_INT ) ) ( (lv_transitionStates_9_0= ruleToState ) )* (otherlv_10= 'rule' ( (lv_rules_11_0= ruleRuleRef ) ) )* ( (lv_decisionReviews_12_0= ruleDecisionReviewRef ) )* ( (lv_peerReviews_13_0= rulePeerReviewRef ) )* (otherlv_14= 'percentWeight' ( (lv_percentWeight_15_0= RULE_INT ) ) )? (otherlv_16= 'recommendedPercentComplete' ( (lv_recommendedPercentComplete_17_0= RULE_INT ) ) )? (otherlv_18= 'color' ( (lv_color_19_0= ruleStateColor ) ) )? ( (lv_layout_20_0= ruleLayoutType ) )? otherlv_21= '}' )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2429:3: otherlv_0= 'state' ( (lv_name_1_0= ruleSTATE_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'description' ( (lv_description_4_0= RULE_STRING ) ) )? otherlv_5= 'type' ( (lv_pageType_6_0= rulePageType ) ) otherlv_7= 'ordinal' ( (lv_ordinal_8_0= RULE_INT ) ) ( (lv_transitionStates_9_0= ruleToState ) )* (otherlv_10= 'rule' ( (lv_rules_11_0= ruleRuleRef ) ) )* ( (lv_decisionReviews_12_0= ruleDecisionReviewRef ) )* ( (lv_peerReviews_13_0= rulePeerReviewRef ) )* (otherlv_14= 'percentWeight' ( (lv_percentWeight_15_0= RULE_INT ) ) )? (otherlv_16= 'recommendedPercentComplete' ( (lv_recommendedPercentComplete_17_0= RULE_INT ) ) )? (otherlv_18= 'color' ( (lv_color_19_0= ruleStateColor ) ) )? ( (lv_layout_20_0= ruleLayoutType ) )? otherlv_21= '}'
            {
               otherlv_0 = (Token) match(input, 61, FOLLOW_61_in_ruleStateDef4836);

               newLeafNode(otherlv_0, grammarAccess.getStateDefAccess().getStateKeyword_0());

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2433:1: ( (lv_name_1_0= ruleSTATE_NAME_REFERENCE ) )
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2434:1: (lv_name_1_0= ruleSTATE_NAME_REFERENCE )
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2434:1: (lv_name_1_0= ruleSTATE_NAME_REFERENCE )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2435:3: lv_name_1_0= ruleSTATE_NAME_REFERENCE
                  {

                     newCompositeNode(
                        grammarAccess.getStateDefAccess().getNameSTATE_NAME_REFERENCEParserRuleCall_1_0());

                     pushFollow(FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleStateDef4857);
                     lv_name_1_0 = ruleSTATE_NAME_REFERENCE();

                     state._fsp--;

                     if (current == null) {
                        current = createModelElementForParent(grammarAccess.getStateDefRule());
                     }
                     set(current, "name", lv_name_1_0, "STATE_NAME_REFERENCE");
                     afterParserOrEnumRuleCall();

                  }

               }

               otherlv_2 = (Token) match(input, 17, FOLLOW_17_in_ruleStateDef4869);

               newLeafNode(otherlv_2, grammarAccess.getStateDefAccess().getLeftCurlyBracketKeyword_2());

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2455:1: (otherlv_3= 'description' ( (lv_description_4_0= RULE_STRING ) ) )?
               int alt78 = 2;
               int LA78_0 = input.LA(1);

               if (LA78_0 == 51) {
                  alt78 = 1;
               }
               switch (alt78) {
                  case 1:
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2455:3: otherlv_3= 'description' ( (lv_description_4_0= RULE_STRING ) )
                  {
                     otherlv_3 = (Token) match(input, 51, FOLLOW_51_in_ruleStateDef4882);

                     newLeafNode(otherlv_3, grammarAccess.getStateDefAccess().getDescriptionKeyword_3_0());

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2459:1: ( (lv_description_4_0= RULE_STRING ) )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2460:1: (lv_description_4_0= RULE_STRING )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2460:1: (lv_description_4_0= RULE_STRING )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2461:3: lv_description_4_0= RULE_STRING
                        {
                           lv_description_4_0 =
                              (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleStateDef4899);

                           newLeafNode(lv_description_4_0,
                              grammarAccess.getStateDefAccess().getDescriptionSTRINGTerminalRuleCall_3_1_0());

                           if (current == null) {
                              current = createModelElement(grammarAccess.getStateDefRule());
                           }
                           setWithLastConsumed(current, "description", lv_description_4_0, "STRING");

                        }

                     }

                  }
                     break;

               }

               otherlv_5 = (Token) match(input, 62, FOLLOW_62_in_ruleStateDef4918);

               newLeafNode(otherlv_5, grammarAccess.getStateDefAccess().getTypeKeyword_4());

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2481:1: ( (lv_pageType_6_0= rulePageType ) )
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2482:1: (lv_pageType_6_0= rulePageType )
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2482:1: (lv_pageType_6_0= rulePageType )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2483:3: lv_pageType_6_0= rulePageType
                  {

                     newCompositeNode(grammarAccess.getStateDefAccess().getPageTypePageTypeParserRuleCall_5_0());

                     pushFollow(FOLLOW_rulePageType_in_ruleStateDef4939);
                     lv_pageType_6_0 = rulePageType();

                     state._fsp--;

                     if (current == null) {
                        current = createModelElementForParent(grammarAccess.getStateDefRule());
                     }
                     set(current, "pageType", lv_pageType_6_0, "PageType");
                     afterParserOrEnumRuleCall();

                  }

               }

               otherlv_7 = (Token) match(input, 63, FOLLOW_63_in_ruleStateDef4951);

               newLeafNode(otherlv_7, grammarAccess.getStateDefAccess().getOrdinalKeyword_6());

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2503:1: ( (lv_ordinal_8_0= RULE_INT ) )
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2504:1: (lv_ordinal_8_0= RULE_INT )
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2504:1: (lv_ordinal_8_0= RULE_INT )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2505:3: lv_ordinal_8_0= RULE_INT
                  {
                     lv_ordinal_8_0 = (Token) match(input, RULE_INT, FOLLOW_RULE_INT_in_ruleStateDef4968);

                     newLeafNode(lv_ordinal_8_0, grammarAccess.getStateDefAccess().getOrdinalINTTerminalRuleCall_7_0());

                     if (current == null) {
                        current = createModelElement(grammarAccess.getStateDefRule());
                     }
                     setWithLastConsumed(current, "ordinal", lv_ordinal_8_0, "INT");

                  }

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2521:2: ( (lv_transitionStates_9_0= ruleToState ) )*
               loop79: do {
                  int alt79 = 2;
                  int LA79_0 = input.LA(1);

                  if (LA79_0 == 80) {
                     alt79 = 1;
                  }

                  switch (alt79) {
                     case 1:
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2522:1: (lv_transitionStates_9_0= ruleToState )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2522:1: (lv_transitionStates_9_0= ruleToState )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2523:3: lv_transitionStates_9_0= ruleToState
                        {

                           newCompositeNode(
                              grammarAccess.getStateDefAccess().getTransitionStatesToStateParserRuleCall_8_0());

                           pushFollow(FOLLOW_ruleToState_in_ruleStateDef4994);
                           lv_transitionStates_9_0 = ruleToState();

                           state._fsp--;

                           if (current == null) {
                              current = createModelElementForParent(grammarAccess.getStateDefRule());
                           }
                           add(current, "transitionStates", lv_transitionStates_9_0, "ToState");
                           afterParserOrEnumRuleCall();

                        }

                     }
                        break;

                     default:
                        break loop79;
                  }
               } while (true);

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2539:3: (otherlv_10= 'rule' ( (lv_rules_11_0= ruleRuleRef ) ) )*
               loop80: do {
                  int alt80 = 2;
                  int LA80_0 = input.LA(1);

                  if (LA80_0 == 16) {
                     alt80 = 1;
                  }

                  switch (alt80) {
                     case 1:
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2539:5: otherlv_10= 'rule' ( (lv_rules_11_0= ruleRuleRef ) )
                     {
                        otherlv_10 = (Token) match(input, 16, FOLLOW_16_in_ruleStateDef5008);

                        newLeafNode(otherlv_10, grammarAccess.getStateDefAccess().getRuleKeyword_9_0());

                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2543:1: ( (lv_rules_11_0= ruleRuleRef ) )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2544:1: (lv_rules_11_0= ruleRuleRef )
                        {
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2544:1: (lv_rules_11_0= ruleRuleRef )
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2545:3: lv_rules_11_0= ruleRuleRef
                           {

                              newCompositeNode(grammarAccess.getStateDefAccess().getRulesRuleRefParserRuleCall_9_1_0());

                              pushFollow(FOLLOW_ruleRuleRef_in_ruleStateDef5029);
                              lv_rules_11_0 = ruleRuleRef();

                              state._fsp--;

                              if (current == null) {
                                 current = createModelElementForParent(grammarAccess.getStateDefRule());
                              }
                              add(current, "rules", lv_rules_11_0, "RuleRef");
                              afterParserOrEnumRuleCall();

                           }

                        }

                     }
                        break;

                     default:
                        break loop80;
                  }
               } while (true);

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2561:4: ( (lv_decisionReviews_12_0= ruleDecisionReviewRef ) )*
               loop81: do {
                  int alt81 = 2;
                  int LA81_0 = input.LA(1);

                  if (LA81_0 == 67) {
                     alt81 = 1;
                  }

                  switch (alt81) {
                     case 1:
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2562:1: (lv_decisionReviews_12_0= ruleDecisionReviewRef )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2562:1: (lv_decisionReviews_12_0= ruleDecisionReviewRef )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2563:3: lv_decisionReviews_12_0= ruleDecisionReviewRef
                        {

                           newCompositeNode(
                              grammarAccess.getStateDefAccess().getDecisionReviewsDecisionReviewRefParserRuleCall_10_0());

                           pushFollow(FOLLOW_ruleDecisionReviewRef_in_ruleStateDef5052);
                           lv_decisionReviews_12_0 = ruleDecisionReviewRef();

                           state._fsp--;

                           if (current == null) {
                              current = createModelElementForParent(grammarAccess.getStateDefRule());
                           }
                           add(current, "decisionReviews", lv_decisionReviews_12_0, "DecisionReviewRef");
                           afterParserOrEnumRuleCall();

                        }

                     }
                        break;

                     default:
                        break loop81;
                  }
               } while (true);

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2579:3: ( (lv_peerReviews_13_0= rulePeerReviewRef ) )*
               loop82: do {
                  int alt82 = 2;
                  int LA82_0 = input.LA(1);

                  if (LA82_0 == 75) {
                     alt82 = 1;
                  }

                  switch (alt82) {
                     case 1:
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2580:1: (lv_peerReviews_13_0= rulePeerReviewRef )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2580:1: (lv_peerReviews_13_0= rulePeerReviewRef )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2581:3: lv_peerReviews_13_0= rulePeerReviewRef
                        {

                           newCompositeNode(
                              grammarAccess.getStateDefAccess().getPeerReviewsPeerReviewRefParserRuleCall_11_0());

                           pushFollow(FOLLOW_rulePeerReviewRef_in_ruleStateDef5074);
                           lv_peerReviews_13_0 = rulePeerReviewRef();

                           state._fsp--;

                           if (current == null) {
                              current = createModelElementForParent(grammarAccess.getStateDefRule());
                           }
                           add(current, "peerReviews", lv_peerReviews_13_0, "PeerReviewRef");
                           afterParserOrEnumRuleCall();

                        }

                     }
                        break;

                     default:
                        break loop82;
                  }
               } while (true);

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2597:3: (otherlv_14= 'percentWeight' ( (lv_percentWeight_15_0= RULE_INT ) ) )?
               int alt83 = 2;
               int LA83_0 = input.LA(1);

               if (LA83_0 == 64) {
                  alt83 = 1;
               }
               switch (alt83) {
                  case 1:
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2597:5: otherlv_14= 'percentWeight' ( (lv_percentWeight_15_0= RULE_INT ) )
                  {
                     otherlv_14 = (Token) match(input, 64, FOLLOW_64_in_ruleStateDef5088);

                     newLeafNode(otherlv_14, grammarAccess.getStateDefAccess().getPercentWeightKeyword_12_0());

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2601:1: ( (lv_percentWeight_15_0= RULE_INT ) )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2602:1: (lv_percentWeight_15_0= RULE_INT )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2602:1: (lv_percentWeight_15_0= RULE_INT )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2603:3: lv_percentWeight_15_0= RULE_INT
                        {
                           lv_percentWeight_15_0 = (Token) match(input, RULE_INT, FOLLOW_RULE_INT_in_ruleStateDef5105);

                           newLeafNode(lv_percentWeight_15_0,
                              grammarAccess.getStateDefAccess().getPercentWeightINTTerminalRuleCall_12_1_0());

                           if (current == null) {
                              current = createModelElement(grammarAccess.getStateDefRule());
                           }
                           setWithLastConsumed(current, "percentWeight", lv_percentWeight_15_0, "INT");

                        }

                     }

                  }
                     break;

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2619:4: (otherlv_16= 'recommendedPercentComplete' ( (lv_recommendedPercentComplete_17_0= RULE_INT ) ) )?
               int alt84 = 2;
               int LA84_0 = input.LA(1);

               if (LA84_0 == 65) {
                  alt84 = 1;
               }
               switch (alt84) {
                  case 1:
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2619:6: otherlv_16= 'recommendedPercentComplete' ( (lv_recommendedPercentComplete_17_0= RULE_INT ) )
                  {
                     otherlv_16 = (Token) match(input, 65, FOLLOW_65_in_ruleStateDef5125);

                     newLeafNode(otherlv_16,
                        grammarAccess.getStateDefAccess().getRecommendedPercentCompleteKeyword_13_0());

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2623:1: ( (lv_recommendedPercentComplete_17_0= RULE_INT ) )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2624:1: (lv_recommendedPercentComplete_17_0= RULE_INT )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2624:1: (lv_recommendedPercentComplete_17_0= RULE_INT )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2625:3: lv_recommendedPercentComplete_17_0= RULE_INT
                        {
                           lv_recommendedPercentComplete_17_0 =
                              (Token) match(input, RULE_INT, FOLLOW_RULE_INT_in_ruleStateDef5142);

                           newLeafNode(lv_recommendedPercentComplete_17_0,
                              grammarAccess.getStateDefAccess().getRecommendedPercentCompleteINTTerminalRuleCall_13_1_0());

                           if (current == null) {
                              current = createModelElement(grammarAccess.getStateDefRule());
                           }
                           setWithLastConsumed(current, "recommendedPercentComplete",
                              lv_recommendedPercentComplete_17_0, "INT");

                        }

                     }

                  }
                     break;

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2641:4: (otherlv_18= 'color' ( (lv_color_19_0= ruleStateColor ) ) )?
               int alt85 = 2;
               int LA85_0 = input.LA(1);

               if (LA85_0 == 66) {
                  alt85 = 1;
               }
               switch (alt85) {
                  case 1:
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2641:6: otherlv_18= 'color' ( (lv_color_19_0= ruleStateColor ) )
                  {
                     otherlv_18 = (Token) match(input, 66, FOLLOW_66_in_ruleStateDef5162);

                     newLeafNode(otherlv_18, grammarAccess.getStateDefAccess().getColorKeyword_14_0());

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2645:1: ( (lv_color_19_0= ruleStateColor ) )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2646:1: (lv_color_19_0= ruleStateColor )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2646:1: (lv_color_19_0= ruleStateColor )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2647:3: lv_color_19_0= ruleStateColor
                        {

                           newCompositeNode(
                              grammarAccess.getStateDefAccess().getColorStateColorParserRuleCall_14_1_0());

                           pushFollow(FOLLOW_ruleStateColor_in_ruleStateDef5183);
                           lv_color_19_0 = ruleStateColor();

                           state._fsp--;

                           if (current == null) {
                              current = createModelElementForParent(grammarAccess.getStateDefRule());
                           }
                           set(current, "color", lv_color_19_0, "StateColor");
                           afterParserOrEnumRuleCall();

                        }

                     }

                  }
                     break;

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2663:4: ( (lv_layout_20_0= ruleLayoutType ) )?
               int alt86 = 2;
               int LA86_0 = input.LA(1);

               if (LA86_0 >= 81 && LA86_0 <= 82) {
                  alt86 = 1;
               }
               switch (alt86) {
                  case 1:
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2664:1: (lv_layout_20_0= ruleLayoutType )
                  {
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2664:1: (lv_layout_20_0= ruleLayoutType )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2665:3: lv_layout_20_0= ruleLayoutType
                     {

                        newCompositeNode(grammarAccess.getStateDefAccess().getLayoutLayoutTypeParserRuleCall_15_0());

                        pushFollow(FOLLOW_ruleLayoutType_in_ruleStateDef5206);
                        lv_layout_20_0 = ruleLayoutType();

                        state._fsp--;

                        if (current == null) {
                           current = createModelElementForParent(grammarAccess.getStateDefRule());
                        }
                        set(current, "layout", lv_layout_20_0, "LayoutType");
                        afterParserOrEnumRuleCall();

                     }

                  }
                     break;

               }

               otherlv_21 = (Token) match(input, 22, FOLLOW_22_in_ruleStateDef5219);

               newLeafNode(otherlv_21, grammarAccess.getStateDefAccess().getRightCurlyBracketKeyword_16());

            }

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleStateDef"

   // $ANTLR start "entryRuleDecisionReviewRef"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2693:1: entryRuleDecisionReviewRef returns [EObject current=null] : iv_ruleDecisionReviewRef= ruleDecisionReviewRef EOF ;
   public final EObject entryRuleDecisionReviewRef() throws RecognitionException {
      EObject current = null;

      EObject iv_ruleDecisionReviewRef = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2694:2: (iv_ruleDecisionReviewRef= ruleDecisionReviewRef EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2695:2: iv_ruleDecisionReviewRef= ruleDecisionReviewRef EOF
         {
            newCompositeNode(grammarAccess.getDecisionReviewRefRule());
            pushFollow(FOLLOW_ruleDecisionReviewRef_in_entryRuleDecisionReviewRef5255);
            iv_ruleDecisionReviewRef = ruleDecisionReviewRef();

            state._fsp--;

            current = iv_ruleDecisionReviewRef;
            match(input, EOF, FOLLOW_EOF_in_entryRuleDecisionReviewRef5265);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleDecisionReviewRef"

   // $ANTLR start "ruleDecisionReviewRef"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2702:1: ruleDecisionReviewRef returns [EObject current=null] : (otherlv_0= 'decisionReview' ( ( ruleDECISION_REVIEW_NAME_REFERENCE ) ) ) ;
   public final EObject ruleDecisionReviewRef() throws RecognitionException {
      EObject current = null;

      Token otherlv_0 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2705:28: ( (otherlv_0= 'decisionReview' ( ( ruleDECISION_REVIEW_NAME_REFERENCE ) ) ) )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2706:1: (otherlv_0= 'decisionReview' ( ( ruleDECISION_REVIEW_NAME_REFERENCE ) ) )
         {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2706:1: (otherlv_0= 'decisionReview' ( ( ruleDECISION_REVIEW_NAME_REFERENCE ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2706:3: otherlv_0= 'decisionReview' ( ( ruleDECISION_REVIEW_NAME_REFERENCE ) )
            {
               otherlv_0 = (Token) match(input, 67, FOLLOW_67_in_ruleDecisionReviewRef5302);

               newLeafNode(otherlv_0, grammarAccess.getDecisionReviewRefAccess().getDecisionReviewKeyword_0());

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2710:1: ( ( ruleDECISION_REVIEW_NAME_REFERENCE ) )
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2711:1: ( ruleDECISION_REVIEW_NAME_REFERENCE )
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2711:1: ( ruleDECISION_REVIEW_NAME_REFERENCE )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2712:3: ruleDECISION_REVIEW_NAME_REFERENCE
                  {

                     if (current == null) {
                        current = createModelElement(grammarAccess.getDecisionReviewRefRule());
                     }

                     newCompositeNode(
                        grammarAccess.getDecisionReviewRefAccess().getDecisionReviewDecisionReviewDefCrossReference_1_0());

                     pushFollow(FOLLOW_ruleDECISION_REVIEW_NAME_REFERENCE_in_ruleDecisionReviewRef5325);
                     ruleDECISION_REVIEW_NAME_REFERENCE();

                     state._fsp--;

                     afterParserOrEnumRuleCall();

                  }

               }

            }

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleDecisionReviewRef"

   // $ANTLR start "entryRuleDecisionReviewDef"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2733:1: entryRuleDecisionReviewDef returns [EObject current=null] : iv_ruleDecisionReviewDef= ruleDecisionReviewDef EOF ;
   public final EObject entryRuleDecisionReviewDef() throws RecognitionException {
      EObject current = null;

      EObject iv_ruleDecisionReviewDef = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2734:2: (iv_ruleDecisionReviewDef= ruleDecisionReviewDef EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2735:2: iv_ruleDecisionReviewDef= ruleDecisionReviewDef EOF
         {
            newCompositeNode(grammarAccess.getDecisionReviewDefRule());
            pushFollow(FOLLOW_ruleDecisionReviewDef_in_entryRuleDecisionReviewDef5361);
            iv_ruleDecisionReviewDef = ruleDecisionReviewDef();

            state._fsp--;

            current = iv_ruleDecisionReviewDef;
            match(input, EOF, FOLLOW_EOF_in_entryRuleDecisionReviewDef5371);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleDecisionReviewDef"

   // $ANTLR start "ruleDecisionReviewDef"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2742:1: ruleDecisionReviewDef returns [EObject current=null] : (otherlv_0= 'decisionReviewDefinition' ( (lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_9= 'blockingType' ( (lv_blockingType_10_0= ruleReviewBlockingType ) ) otherlv_11= 'onEvent' ( (lv_stateEvent_12_0= ruleWorkflowEventType ) ) (otherlv_13= 'assignee' ( (lv_assigneeRefs_14_0= ruleUserRef ) ) )* (otherlv_15= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_16_0= ruleBooleanDef ) ) )? ( (lv_options_17_0= ruleDecisionReviewOpt ) )+ otherlv_18= '}' ) ;
   public final EObject ruleDecisionReviewDef() throws RecognitionException {
      EObject current = null;

      Token otherlv_0 = null;
      Token otherlv_2 = null;
      Token otherlv_3 = null;
      Token lv_title_4_0 = null;
      Token otherlv_5 = null;
      Token lv_description_6_0 = null;
      Token otherlv_7 = null;
      Token otherlv_9 = null;
      Token otherlv_11 = null;
      Token otherlv_13 = null;
      Token otherlv_15 = null;
      Token otherlv_18 = null;
      AntlrDatatypeRuleToken lv_name_1_0 = null;

      Enumerator lv_blockingType_10_0 = null;

      Enumerator lv_stateEvent_12_0 = null;

      EObject lv_assigneeRefs_14_0 = null;

      Enumerator lv_autoTransitionToDecision_16_0 = null;

      EObject lv_options_17_0 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2745:28: ( (otherlv_0= 'decisionReviewDefinition' ( (lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_9= 'blockingType' ( (lv_blockingType_10_0= ruleReviewBlockingType ) ) otherlv_11= 'onEvent' ( (lv_stateEvent_12_0= ruleWorkflowEventType ) ) (otherlv_13= 'assignee' ( (lv_assigneeRefs_14_0= ruleUserRef ) ) )* (otherlv_15= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_16_0= ruleBooleanDef ) ) )? ( (lv_options_17_0= ruleDecisionReviewOpt ) )+ otherlv_18= '}' ) )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2746:1: (otherlv_0= 'decisionReviewDefinition' ( (lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_9= 'blockingType' ( (lv_blockingType_10_0= ruleReviewBlockingType ) ) otherlv_11= 'onEvent' ( (lv_stateEvent_12_0= ruleWorkflowEventType ) ) (otherlv_13= 'assignee' ( (lv_assigneeRefs_14_0= ruleUserRef ) ) )* (otherlv_15= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_16_0= ruleBooleanDef ) ) )? ( (lv_options_17_0= ruleDecisionReviewOpt ) )+ otherlv_18= '}' )
         {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2746:1: (otherlv_0= 'decisionReviewDefinition' ( (lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_9= 'blockingType' ( (lv_blockingType_10_0= ruleReviewBlockingType ) ) otherlv_11= 'onEvent' ( (lv_stateEvent_12_0= ruleWorkflowEventType ) ) (otherlv_13= 'assignee' ( (lv_assigneeRefs_14_0= ruleUserRef ) ) )* (otherlv_15= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_16_0= ruleBooleanDef ) ) )? ( (lv_options_17_0= ruleDecisionReviewOpt ) )+ otherlv_18= '}' )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2746:3: otherlv_0= 'decisionReviewDefinition' ( (lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_9= 'blockingType' ( (lv_blockingType_10_0= ruleReviewBlockingType ) ) otherlv_11= 'onEvent' ( (lv_stateEvent_12_0= ruleWorkflowEventType ) ) (otherlv_13= 'assignee' ( (lv_assigneeRefs_14_0= ruleUserRef ) ) )* (otherlv_15= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_16_0= ruleBooleanDef ) ) )? ( (lv_options_17_0= ruleDecisionReviewOpt ) )+ otherlv_18= '}'
            {
               otherlv_0 = (Token) match(input, 68, FOLLOW_68_in_ruleDecisionReviewDef5408);

               newLeafNode(otherlv_0,
                  grammarAccess.getDecisionReviewDefAccess().getDecisionReviewDefinitionKeyword_0());

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2750:1: ( (lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE ) )
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2751:1: (lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE )
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2751:1: (lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2752:3: lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE
                  {

                     newCompositeNode(
                        grammarAccess.getDecisionReviewDefAccess().getNameDECISION_REVIEW_NAME_REFERENCEParserRuleCall_1_0());

                     pushFollow(FOLLOW_ruleDECISION_REVIEW_NAME_REFERENCE_in_ruleDecisionReviewDef5429);
                     lv_name_1_0 = ruleDECISION_REVIEW_NAME_REFERENCE();

                     state._fsp--;

                     if (current == null) {
                        current = createModelElementForParent(grammarAccess.getDecisionReviewDefRule());
                     }
                     set(current, "name", lv_name_1_0, "DECISION_REVIEW_NAME_REFERENCE");
                     afterParserOrEnumRuleCall();

                  }

               }

               otherlv_2 = (Token) match(input, 17, FOLLOW_17_in_ruleDecisionReviewDef5441);

               newLeafNode(otherlv_2, grammarAccess.getDecisionReviewDefAccess().getLeftCurlyBracketKeyword_2());

               otherlv_3 = (Token) match(input, 69, FOLLOW_69_in_ruleDecisionReviewDef5453);

               newLeafNode(otherlv_3, grammarAccess.getDecisionReviewDefAccess().getTitleKeyword_3());

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2776:1: ( (lv_title_4_0= RULE_STRING ) )
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2777:1: (lv_title_4_0= RULE_STRING )
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2777:1: (lv_title_4_0= RULE_STRING )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2778:3: lv_title_4_0= RULE_STRING
                  {
                     lv_title_4_0 = (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleDecisionReviewDef5470);

                     newLeafNode(lv_title_4_0,
                        grammarAccess.getDecisionReviewDefAccess().getTitleSTRINGTerminalRuleCall_4_0());

                     if (current == null) {
                        current = createModelElement(grammarAccess.getDecisionReviewDefRule());
                     }
                     setWithLastConsumed(current, "title", lv_title_4_0, "STRING");

                  }

               }

               otherlv_5 = (Token) match(input, 51, FOLLOW_51_in_ruleDecisionReviewDef5487);

               newLeafNode(otherlv_5, grammarAccess.getDecisionReviewDefAccess().getDescriptionKeyword_5());

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2798:1: ( (lv_description_6_0= RULE_STRING ) )
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2799:1: (lv_description_6_0= RULE_STRING )
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2799:1: (lv_description_6_0= RULE_STRING )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2800:3: lv_description_6_0= RULE_STRING
                  {
                     lv_description_6_0 =
                        (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleDecisionReviewDef5504);

                     newLeafNode(lv_description_6_0,
                        grammarAccess.getDecisionReviewDefAccess().getDescriptionSTRINGTerminalRuleCall_6_0());

                     if (current == null) {
                        current = createModelElement(grammarAccess.getDecisionReviewDefRule());
                     }
                     setWithLastConsumed(current, "description", lv_description_6_0, "STRING");

                  }

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2816:2: (otherlv_7= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )?
               int alt87 = 2;
               int LA87_0 = input.LA(1);

               if (LA87_0 == 70) {
                  alt87 = 1;
               }
               switch (alt87) {
                  case 1:
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2816:4: otherlv_7= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) )
                  {
                     otherlv_7 = (Token) match(input, 70, FOLLOW_70_in_ruleDecisionReviewDef5522);

                     newLeafNode(otherlv_7, grammarAccess.getDecisionReviewDefAccess().getRelatedToStateKeyword_7_0());

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2820:1: ( ( ruleSTATE_NAME_REFERENCE ) )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2821:1: ( ruleSTATE_NAME_REFERENCE )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2821:1: ( ruleSTATE_NAME_REFERENCE )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2822:3: ruleSTATE_NAME_REFERENCE
                        {

                           if (current == null) {
                              current = createModelElement(grammarAccess.getDecisionReviewDefRule());
                           }

                           newCompositeNode(
                              grammarAccess.getDecisionReviewDefAccess().getRelatedToStateStateDefCrossReference_7_1_0());

                           pushFollow(FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleDecisionReviewDef5545);
                           ruleSTATE_NAME_REFERENCE();

                           state._fsp--;

                           afterParserOrEnumRuleCall();

                        }

                     }

                  }
                     break;

               }

               otherlv_9 = (Token) match(input, 71, FOLLOW_71_in_ruleDecisionReviewDef5559);

               newLeafNode(otherlv_9, grammarAccess.getDecisionReviewDefAccess().getBlockingTypeKeyword_8());

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2839:1: ( (lv_blockingType_10_0= ruleReviewBlockingType ) )
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2840:1: (lv_blockingType_10_0= ruleReviewBlockingType )
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2840:1: (lv_blockingType_10_0= ruleReviewBlockingType )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2841:3: lv_blockingType_10_0= ruleReviewBlockingType
                  {

                     newCompositeNode(
                        grammarAccess.getDecisionReviewDefAccess().getBlockingTypeReviewBlockingTypeEnumRuleCall_9_0());

                     pushFollow(FOLLOW_ruleReviewBlockingType_in_ruleDecisionReviewDef5580);
                     lv_blockingType_10_0 = ruleReviewBlockingType();

                     state._fsp--;

                     if (current == null) {
                        current = createModelElementForParent(grammarAccess.getDecisionReviewDefRule());
                     }
                     set(current, "blockingType", lv_blockingType_10_0, "ReviewBlockingType");
                     afterParserOrEnumRuleCall();

                  }

               }

               otherlv_11 = (Token) match(input, 72, FOLLOW_72_in_ruleDecisionReviewDef5592);

               newLeafNode(otherlv_11, grammarAccess.getDecisionReviewDefAccess().getOnEventKeyword_10());

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2861:1: ( (lv_stateEvent_12_0= ruleWorkflowEventType ) )
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2862:1: (lv_stateEvent_12_0= ruleWorkflowEventType )
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2862:1: (lv_stateEvent_12_0= ruleWorkflowEventType )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2863:3: lv_stateEvent_12_0= ruleWorkflowEventType
                  {

                     newCompositeNode(
                        grammarAccess.getDecisionReviewDefAccess().getStateEventWorkflowEventTypeEnumRuleCall_11_0());

                     pushFollow(FOLLOW_ruleWorkflowEventType_in_ruleDecisionReviewDef5613);
                     lv_stateEvent_12_0 = ruleWorkflowEventType();

                     state._fsp--;

                     if (current == null) {
                        current = createModelElementForParent(grammarAccess.getDecisionReviewDefRule());
                     }
                     set(current, "stateEvent", lv_stateEvent_12_0, "WorkflowEventType");
                     afterParserOrEnumRuleCall();

                  }

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2879:2: (otherlv_13= 'assignee' ( (lv_assigneeRefs_14_0= ruleUserRef ) ) )*
               loop88: do {
                  int alt88 = 2;
                  int LA88_0 = input.LA(1);

                  if (LA88_0 == 73) {
                     alt88 = 1;
                  }

                  switch (alt88) {
                     case 1:
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2879:4: otherlv_13= 'assignee' ( (lv_assigneeRefs_14_0= ruleUserRef ) )
                     {
                        otherlv_13 = (Token) match(input, 73, FOLLOW_73_in_ruleDecisionReviewDef5626);

                        newLeafNode(otherlv_13, grammarAccess.getDecisionReviewDefAccess().getAssigneeKeyword_12_0());

                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2883:1: ( (lv_assigneeRefs_14_0= ruleUserRef ) )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2884:1: (lv_assigneeRefs_14_0= ruleUserRef )
                        {
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2884:1: (lv_assigneeRefs_14_0= ruleUserRef )
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2885:3: lv_assigneeRefs_14_0= ruleUserRef
                           {

                              newCompositeNode(
                                 grammarAccess.getDecisionReviewDefAccess().getAssigneeRefsUserRefParserRuleCall_12_1_0());

                              pushFollow(FOLLOW_ruleUserRef_in_ruleDecisionReviewDef5647);
                              lv_assigneeRefs_14_0 = ruleUserRef();

                              state._fsp--;

                              if (current == null) {
                                 current = createModelElementForParent(grammarAccess.getDecisionReviewDefRule());
                              }
                              add(current, "assigneeRefs", lv_assigneeRefs_14_0, "UserRef");
                              afterParserOrEnumRuleCall();

                           }

                        }

                     }
                        break;

                     default:
                        break loop88;
                  }
               } while (true);

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2901:4: (otherlv_15= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_16_0= ruleBooleanDef ) ) )?
               int alt89 = 2;
               int LA89_0 = input.LA(1);

               if (LA89_0 == 74) {
                  alt89 = 1;
               }
               switch (alt89) {
                  case 1:
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2901:6: otherlv_15= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_16_0= ruleBooleanDef ) )
                  {
                     otherlv_15 = (Token) match(input, 74, FOLLOW_74_in_ruleDecisionReviewDef5662);

                     newLeafNode(otherlv_15,
                        grammarAccess.getDecisionReviewDefAccess().getAutoTransitionToDecisionKeyword_13_0());

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2905:1: ( (lv_autoTransitionToDecision_16_0= ruleBooleanDef ) )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2906:1: (lv_autoTransitionToDecision_16_0= ruleBooleanDef )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2906:1: (lv_autoTransitionToDecision_16_0= ruleBooleanDef )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2907:3: lv_autoTransitionToDecision_16_0= ruleBooleanDef
                        {

                           newCompositeNode(
                              grammarAccess.getDecisionReviewDefAccess().getAutoTransitionToDecisionBooleanDefEnumRuleCall_13_1_0());

                           pushFollow(FOLLOW_ruleBooleanDef_in_ruleDecisionReviewDef5683);
                           lv_autoTransitionToDecision_16_0 = ruleBooleanDef();

                           state._fsp--;

                           if (current == null) {
                              current = createModelElementForParent(grammarAccess.getDecisionReviewDefRule());
                           }
                           set(current, "autoTransitionToDecision", lv_autoTransitionToDecision_16_0, "BooleanDef");
                           afterParserOrEnumRuleCall();

                        }

                     }

                  }
                     break;

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2923:4: ( (lv_options_17_0= ruleDecisionReviewOpt ) )+
               int cnt90 = 0;
               loop90: do {
                  int alt90 = 2;
                  int LA90_0 = input.LA(1);

                  if (LA90_0 == 55) {
                     alt90 = 1;
                  }

                  switch (alt90) {
                     case 1:
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2924:1: (lv_options_17_0= ruleDecisionReviewOpt )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2924:1: (lv_options_17_0= ruleDecisionReviewOpt )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2925:3: lv_options_17_0= ruleDecisionReviewOpt
                        {

                           newCompositeNode(
                              grammarAccess.getDecisionReviewDefAccess().getOptionsDecisionReviewOptParserRuleCall_14_0());

                           pushFollow(FOLLOW_ruleDecisionReviewOpt_in_ruleDecisionReviewDef5706);
                           lv_options_17_0 = ruleDecisionReviewOpt();

                           state._fsp--;

                           if (current == null) {
                              current = createModelElementForParent(grammarAccess.getDecisionReviewDefRule());
                           }
                           add(current, "options", lv_options_17_0, "DecisionReviewOpt");
                           afterParserOrEnumRuleCall();

                        }

                     }
                        break;

                     default:
                        if (cnt90 >= 1) {
                           break loop90;
                        }
                        EarlyExitException eee = new EarlyExitException(90, input);
                        throw eee;
                  }
                  cnt90++;
               } while (true);

               otherlv_18 = (Token) match(input, 22, FOLLOW_22_in_ruleDecisionReviewDef5719);

               newLeafNode(otherlv_18, grammarAccess.getDecisionReviewDefAccess().getRightCurlyBracketKeyword_15());

            }

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleDecisionReviewDef"

   // $ANTLR start "entryRuleDECISION_REVIEW_OPT_REF"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2953:1: entryRuleDECISION_REVIEW_OPT_REF returns [String current=null] : iv_ruleDECISION_REVIEW_OPT_REF= ruleDECISION_REVIEW_OPT_REF EOF ;
   public final String entryRuleDECISION_REVIEW_OPT_REF() throws RecognitionException {
      String current = null;

      AntlrDatatypeRuleToken iv_ruleDECISION_REVIEW_OPT_REF = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2954:2: (iv_ruleDECISION_REVIEW_OPT_REF= ruleDECISION_REVIEW_OPT_REF EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2955:2: iv_ruleDECISION_REVIEW_OPT_REF= ruleDECISION_REVIEW_OPT_REF EOF
         {
            newCompositeNode(grammarAccess.getDECISION_REVIEW_OPT_REFRule());
            pushFollow(FOLLOW_ruleDECISION_REVIEW_OPT_REF_in_entryRuleDECISION_REVIEW_OPT_REF5756);
            iv_ruleDECISION_REVIEW_OPT_REF = ruleDECISION_REVIEW_OPT_REF();

            state._fsp--;

            current = iv_ruleDECISION_REVIEW_OPT_REF.getText();
            match(input, EOF, FOLLOW_EOF_in_entryRuleDECISION_REVIEW_OPT_REF5767);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleDECISION_REVIEW_OPT_REF"

   // $ANTLR start "ruleDECISION_REVIEW_OPT_REF"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2962:1: ruleDECISION_REVIEW_OPT_REF returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
   public final AntlrDatatypeRuleToken ruleDECISION_REVIEW_OPT_REF() throws RecognitionException {
      AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

      Token this_STRING_0 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2965:28: (this_STRING_0= RULE_STRING )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2966:5: this_STRING_0= RULE_STRING
         {
            this_STRING_0 = (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleDECISION_REVIEW_OPT_REF5806);

            current.merge(this_STRING_0);

            newLeafNode(this_STRING_0, grammarAccess.getDECISION_REVIEW_OPT_REFAccess().getSTRINGTerminalRuleCall());

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleDECISION_REVIEW_OPT_REF"

   // $ANTLR start "entryRuleDecisionReviewOpt"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2981:1: entryRuleDecisionReviewOpt returns [EObject current=null] : iv_ruleDecisionReviewOpt= ruleDecisionReviewOpt EOF ;
   public final EObject entryRuleDecisionReviewOpt() throws RecognitionException {
      EObject current = null;

      EObject iv_ruleDecisionReviewOpt = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2982:2: (iv_ruleDecisionReviewOpt= ruleDecisionReviewOpt EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2983:2: iv_ruleDecisionReviewOpt= ruleDecisionReviewOpt EOF
         {
            newCompositeNode(grammarAccess.getDecisionReviewOptRule());
            pushFollow(FOLLOW_ruleDecisionReviewOpt_in_entryRuleDecisionReviewOpt5850);
            iv_ruleDecisionReviewOpt = ruleDecisionReviewOpt();

            state._fsp--;

            current = iv_ruleDecisionReviewOpt;
            match(input, EOF, FOLLOW_EOF_in_entryRuleDecisionReviewOpt5860);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleDecisionReviewOpt"

   // $ANTLR start "ruleDecisionReviewOpt"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2990:1: ruleDecisionReviewOpt returns [EObject current=null] : (otherlv_0= 'option' ( (lv_name_1_0= ruleDECISION_REVIEW_OPT_REF ) ) ( (lv_followup_2_0= ruleFollowupRef ) )? ) ;
   public final EObject ruleDecisionReviewOpt() throws RecognitionException {
      EObject current = null;

      Token otherlv_0 = null;
      AntlrDatatypeRuleToken lv_name_1_0 = null;

      EObject lv_followup_2_0 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2993:28: ( (otherlv_0= 'option' ( (lv_name_1_0= ruleDECISION_REVIEW_OPT_REF ) ) ( (lv_followup_2_0= ruleFollowupRef ) )? ) )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2994:1: (otherlv_0= 'option' ( (lv_name_1_0= ruleDECISION_REVIEW_OPT_REF ) ) ( (lv_followup_2_0= ruleFollowupRef ) )? )
         {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2994:1: (otherlv_0= 'option' ( (lv_name_1_0= ruleDECISION_REVIEW_OPT_REF ) ) ( (lv_followup_2_0= ruleFollowupRef ) )? )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2994:3: otherlv_0= 'option' ( (lv_name_1_0= ruleDECISION_REVIEW_OPT_REF ) ) ( (lv_followup_2_0= ruleFollowupRef ) )?
            {
               otherlv_0 = (Token) match(input, 55, FOLLOW_55_in_ruleDecisionReviewOpt5897);

               newLeafNode(otherlv_0, grammarAccess.getDecisionReviewOptAccess().getOptionKeyword_0());

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2998:1: ( (lv_name_1_0= ruleDECISION_REVIEW_OPT_REF ) )
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2999:1: (lv_name_1_0= ruleDECISION_REVIEW_OPT_REF )
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2999:1: (lv_name_1_0= ruleDECISION_REVIEW_OPT_REF )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3000:3: lv_name_1_0= ruleDECISION_REVIEW_OPT_REF
                  {

                     newCompositeNode(
                        grammarAccess.getDecisionReviewOptAccess().getNameDECISION_REVIEW_OPT_REFParserRuleCall_1_0());

                     pushFollow(FOLLOW_ruleDECISION_REVIEW_OPT_REF_in_ruleDecisionReviewOpt5918);
                     lv_name_1_0 = ruleDECISION_REVIEW_OPT_REF();

                     state._fsp--;

                     if (current == null) {
                        current = createModelElementForParent(grammarAccess.getDecisionReviewOptRule());
                     }
                     set(current, "name", lv_name_1_0, "DECISION_REVIEW_OPT_REF");
                     afterParserOrEnumRuleCall();

                  }

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3016:2: ( (lv_followup_2_0= ruleFollowupRef ) )?
               int alt91 = 2;
               int LA91_0 = input.LA(1);

               if (LA91_0 == 78) {
                  alt91 = 1;
               }
               switch (alt91) {
                  case 1:
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3017:1: (lv_followup_2_0= ruleFollowupRef )
                  {
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3017:1: (lv_followup_2_0= ruleFollowupRef )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3018:3: lv_followup_2_0= ruleFollowupRef
                     {

                        newCompositeNode(
                           grammarAccess.getDecisionReviewOptAccess().getFollowupFollowupRefParserRuleCall_2_0());

                        pushFollow(FOLLOW_ruleFollowupRef_in_ruleDecisionReviewOpt5939);
                        lv_followup_2_0 = ruleFollowupRef();

                        state._fsp--;

                        if (current == null) {
                           current = createModelElementForParent(grammarAccess.getDecisionReviewOptRule());
                        }
                        set(current, "followup", lv_followup_2_0, "FollowupRef");
                        afterParserOrEnumRuleCall();

                     }

                  }
                     break;

               }

            }

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleDecisionReviewOpt"

   // $ANTLR start "entryRulePeerReviewRef"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3042:1: entryRulePeerReviewRef returns [EObject current=null] : iv_rulePeerReviewRef= rulePeerReviewRef EOF ;
   public final EObject entryRulePeerReviewRef() throws RecognitionException {
      EObject current = null;

      EObject iv_rulePeerReviewRef = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3043:2: (iv_rulePeerReviewRef= rulePeerReviewRef EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3044:2: iv_rulePeerReviewRef= rulePeerReviewRef EOF
         {
            newCompositeNode(grammarAccess.getPeerReviewRefRule());
            pushFollow(FOLLOW_rulePeerReviewRef_in_entryRulePeerReviewRef5976);
            iv_rulePeerReviewRef = rulePeerReviewRef();

            state._fsp--;

            current = iv_rulePeerReviewRef;
            match(input, EOF, FOLLOW_EOF_in_entryRulePeerReviewRef5986);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRulePeerReviewRef"

   // $ANTLR start "rulePeerReviewRef"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3051:1: rulePeerReviewRef returns [EObject current=null] : (otherlv_0= 'peerReview' ( ( rulePEER_REVIEW_NAME_REFERENCE ) ) ) ;
   public final EObject rulePeerReviewRef() throws RecognitionException {
      EObject current = null;

      Token otherlv_0 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3054:28: ( (otherlv_0= 'peerReview' ( ( rulePEER_REVIEW_NAME_REFERENCE ) ) ) )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3055:1: (otherlv_0= 'peerReview' ( ( rulePEER_REVIEW_NAME_REFERENCE ) ) )
         {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3055:1: (otherlv_0= 'peerReview' ( ( rulePEER_REVIEW_NAME_REFERENCE ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3055:3: otherlv_0= 'peerReview' ( ( rulePEER_REVIEW_NAME_REFERENCE ) )
            {
               otherlv_0 = (Token) match(input, 75, FOLLOW_75_in_rulePeerReviewRef6023);

               newLeafNode(otherlv_0, grammarAccess.getPeerReviewRefAccess().getPeerReviewKeyword_0());

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3059:1: ( ( rulePEER_REVIEW_NAME_REFERENCE ) )
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3060:1: ( rulePEER_REVIEW_NAME_REFERENCE )
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3060:1: ( rulePEER_REVIEW_NAME_REFERENCE )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3061:3: rulePEER_REVIEW_NAME_REFERENCE
                  {

                     if (current == null) {
                        current = createModelElement(grammarAccess.getPeerReviewRefRule());
                     }

                     newCompositeNode(
                        grammarAccess.getPeerReviewRefAccess().getPeerReviewPeerReviewDefCrossReference_1_0());

                     pushFollow(FOLLOW_rulePEER_REVIEW_NAME_REFERENCE_in_rulePeerReviewRef6046);
                     rulePEER_REVIEW_NAME_REFERENCE();

                     state._fsp--;

                     afterParserOrEnumRuleCall();

                  }

               }

            }

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "rulePeerReviewRef"

   // $ANTLR start "entryRulePeerReviewDef"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3082:1: entryRulePeerReviewDef returns [EObject current=null] : iv_rulePeerReviewDef= rulePeerReviewDef EOF ;
   public final EObject entryRulePeerReviewDef() throws RecognitionException {
      EObject current = null;

      EObject iv_rulePeerReviewDef = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3083:2: (iv_rulePeerReviewDef= rulePeerReviewDef EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3084:2: iv_rulePeerReviewDef= rulePeerReviewDef EOF
         {
            newCompositeNode(grammarAccess.getPeerReviewDefRule());
            pushFollow(FOLLOW_rulePeerReviewDef_in_entryRulePeerReviewDef6082);
            iv_rulePeerReviewDef = rulePeerReviewDef();

            state._fsp--;

            current = iv_rulePeerReviewDef;
            match(input, EOF, FOLLOW_EOF_in_entryRulePeerReviewDef6092);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRulePeerReviewDef"

   // $ANTLR start "rulePeerReviewDef"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3091:1: rulePeerReviewDef returns [EObject current=null] : (otherlv_0= 'peerReviewDefinition' ( (lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) )? otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'location' ( (lv_location_8_0= RULE_STRING ) ) )? (otherlv_9= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_11= 'blockingType' ( (lv_blockingType_12_0= ruleReviewBlockingType ) ) otherlv_13= 'onEvent' ( (lv_stateEvent_14_0= ruleWorkflowEventType ) ) (otherlv_15= 'assignee' ( (lv_assigneeRefs_16_0= ruleUserRef ) ) )* otherlv_17= '}' ) ;
   public final EObject rulePeerReviewDef() throws RecognitionException {
      EObject current = null;

      Token otherlv_0 = null;
      Token otherlv_2 = null;
      Token otherlv_3 = null;
      Token lv_title_4_0 = null;
      Token otherlv_5 = null;
      Token lv_description_6_0 = null;
      Token otherlv_7 = null;
      Token lv_location_8_0 = null;
      Token otherlv_9 = null;
      Token otherlv_11 = null;
      Token otherlv_13 = null;
      Token otherlv_15 = null;
      Token otherlv_17 = null;
      AntlrDatatypeRuleToken lv_name_1_0 = null;

      Enumerator lv_blockingType_12_0 = null;

      Enumerator lv_stateEvent_14_0 = null;

      EObject lv_assigneeRefs_16_0 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3094:28: ( (otherlv_0= 'peerReviewDefinition' ( (lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) )? otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'location' ( (lv_location_8_0= RULE_STRING ) ) )? (otherlv_9= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_11= 'blockingType' ( (lv_blockingType_12_0= ruleReviewBlockingType ) ) otherlv_13= 'onEvent' ( (lv_stateEvent_14_0= ruleWorkflowEventType ) ) (otherlv_15= 'assignee' ( (lv_assigneeRefs_16_0= ruleUserRef ) ) )* otherlv_17= '}' ) )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3095:1: (otherlv_0= 'peerReviewDefinition' ( (lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) )? otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'location' ( (lv_location_8_0= RULE_STRING ) ) )? (otherlv_9= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_11= 'blockingType' ( (lv_blockingType_12_0= ruleReviewBlockingType ) ) otherlv_13= 'onEvent' ( (lv_stateEvent_14_0= ruleWorkflowEventType ) ) (otherlv_15= 'assignee' ( (lv_assigneeRefs_16_0= ruleUserRef ) ) )* otherlv_17= '}' )
         {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3095:1: (otherlv_0= 'peerReviewDefinition' ( (lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) )? otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'location' ( (lv_location_8_0= RULE_STRING ) ) )? (otherlv_9= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_11= 'blockingType' ( (lv_blockingType_12_0= ruleReviewBlockingType ) ) otherlv_13= 'onEvent' ( (lv_stateEvent_14_0= ruleWorkflowEventType ) ) (otherlv_15= 'assignee' ( (lv_assigneeRefs_16_0= ruleUserRef ) ) )* otherlv_17= '}' )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3095:3: otherlv_0= 'peerReviewDefinition' ( (lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) )? otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'location' ( (lv_location_8_0= RULE_STRING ) ) )? (otherlv_9= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_11= 'blockingType' ( (lv_blockingType_12_0= ruleReviewBlockingType ) ) otherlv_13= 'onEvent' ( (lv_stateEvent_14_0= ruleWorkflowEventType ) ) (otherlv_15= 'assignee' ( (lv_assigneeRefs_16_0= ruleUserRef ) ) )* otherlv_17= '}'
            {
               otherlv_0 = (Token) match(input, 76, FOLLOW_76_in_rulePeerReviewDef6129);

               newLeafNode(otherlv_0, grammarAccess.getPeerReviewDefAccess().getPeerReviewDefinitionKeyword_0());

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3099:1: ( (lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE ) )
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3100:1: (lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE )
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3100:1: (lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3101:3: lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE
                  {

                     newCompositeNode(
                        grammarAccess.getPeerReviewDefAccess().getNamePEER_REVIEW_NAME_REFERENCEParserRuleCall_1_0());

                     pushFollow(FOLLOW_rulePEER_REVIEW_NAME_REFERENCE_in_rulePeerReviewDef6150);
                     lv_name_1_0 = rulePEER_REVIEW_NAME_REFERENCE();

                     state._fsp--;

                     if (current == null) {
                        current = createModelElementForParent(grammarAccess.getPeerReviewDefRule());
                     }
                     set(current, "name", lv_name_1_0, "PEER_REVIEW_NAME_REFERENCE");
                     afterParserOrEnumRuleCall();

                  }

               }

               otherlv_2 = (Token) match(input, 17, FOLLOW_17_in_rulePeerReviewDef6162);

               newLeafNode(otherlv_2, grammarAccess.getPeerReviewDefAccess().getLeftCurlyBracketKeyword_2());

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3121:1: (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) )?
               int alt92 = 2;
               int LA92_0 = input.LA(1);

               if (LA92_0 == 69) {
                  alt92 = 1;
               }
               switch (alt92) {
                  case 1:
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3121:3: otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) )
                  {
                     otherlv_3 = (Token) match(input, 69, FOLLOW_69_in_rulePeerReviewDef6175);

                     newLeafNode(otherlv_3, grammarAccess.getPeerReviewDefAccess().getTitleKeyword_3_0());

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3125:1: ( (lv_title_4_0= RULE_STRING ) )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3126:1: (lv_title_4_0= RULE_STRING )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3126:1: (lv_title_4_0= RULE_STRING )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3127:3: lv_title_4_0= RULE_STRING
                        {
                           lv_title_4_0 =
                              (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_rulePeerReviewDef6192);

                           newLeafNode(lv_title_4_0,
                              grammarAccess.getPeerReviewDefAccess().getTitleSTRINGTerminalRuleCall_3_1_0());

                           if (current == null) {
                              current = createModelElement(grammarAccess.getPeerReviewDefRule());
                           }
                           setWithLastConsumed(current, "title", lv_title_4_0, "STRING");

                        }

                     }

                  }
                     break;

               }

               otherlv_5 = (Token) match(input, 51, FOLLOW_51_in_rulePeerReviewDef6211);

               newLeafNode(otherlv_5, grammarAccess.getPeerReviewDefAccess().getDescriptionKeyword_4());

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3147:1: ( (lv_description_6_0= RULE_STRING ) )
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3148:1: (lv_description_6_0= RULE_STRING )
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3148:1: (lv_description_6_0= RULE_STRING )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3149:3: lv_description_6_0= RULE_STRING
                  {
                     lv_description_6_0 =
                        (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_rulePeerReviewDef6228);

                     newLeafNode(lv_description_6_0,
                        grammarAccess.getPeerReviewDefAccess().getDescriptionSTRINGTerminalRuleCall_5_0());

                     if (current == null) {
                        current = createModelElement(grammarAccess.getPeerReviewDefRule());
                     }
                     setWithLastConsumed(current, "description", lv_description_6_0, "STRING");

                  }

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3165:2: (otherlv_7= 'location' ( (lv_location_8_0= RULE_STRING ) ) )?
               int alt93 = 2;
               int LA93_0 = input.LA(1);

               if (LA93_0 == 77) {
                  alt93 = 1;
               }
               switch (alt93) {
                  case 1:
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3165:4: otherlv_7= 'location' ( (lv_location_8_0= RULE_STRING ) )
                  {
                     otherlv_7 = (Token) match(input, 77, FOLLOW_77_in_rulePeerReviewDef6246);

                     newLeafNode(otherlv_7, grammarAccess.getPeerReviewDefAccess().getLocationKeyword_6_0());

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3169:1: ( (lv_location_8_0= RULE_STRING ) )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3170:1: (lv_location_8_0= RULE_STRING )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3170:1: (lv_location_8_0= RULE_STRING )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3171:3: lv_location_8_0= RULE_STRING
                        {
                           lv_location_8_0 =
                              (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_rulePeerReviewDef6263);

                           newLeafNode(lv_location_8_0,
                              grammarAccess.getPeerReviewDefAccess().getLocationSTRINGTerminalRuleCall_6_1_0());

                           if (current == null) {
                              current = createModelElement(grammarAccess.getPeerReviewDefRule());
                           }
                           setWithLastConsumed(current, "location", lv_location_8_0, "STRING");

                        }

                     }

                  }
                     break;

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3187:4: (otherlv_9= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )?
               int alt94 = 2;
               int LA94_0 = input.LA(1);

               if (LA94_0 == 70) {
                  alt94 = 1;
               }
               switch (alt94) {
                  case 1:
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3187:6: otherlv_9= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) )
                  {
                     otherlv_9 = (Token) match(input, 70, FOLLOW_70_in_rulePeerReviewDef6283);

                     newLeafNode(otherlv_9, grammarAccess.getPeerReviewDefAccess().getRelatedToStateKeyword_7_0());

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3191:1: ( ( ruleSTATE_NAME_REFERENCE ) )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3192:1: ( ruleSTATE_NAME_REFERENCE )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3192:1: ( ruleSTATE_NAME_REFERENCE )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3193:3: ruleSTATE_NAME_REFERENCE
                        {

                           if (current == null) {
                              current = createModelElement(grammarAccess.getPeerReviewDefRule());
                           }

                           newCompositeNode(
                              grammarAccess.getPeerReviewDefAccess().getRelatedToStateStateDefCrossReference_7_1_0());

                           pushFollow(FOLLOW_ruleSTATE_NAME_REFERENCE_in_rulePeerReviewDef6306);
                           ruleSTATE_NAME_REFERENCE();

                           state._fsp--;

                           afterParserOrEnumRuleCall();

                        }

                     }

                  }
                     break;

               }

               otherlv_11 = (Token) match(input, 71, FOLLOW_71_in_rulePeerReviewDef6320);

               newLeafNode(otherlv_11, grammarAccess.getPeerReviewDefAccess().getBlockingTypeKeyword_8());

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3210:1: ( (lv_blockingType_12_0= ruleReviewBlockingType ) )
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3211:1: (lv_blockingType_12_0= ruleReviewBlockingType )
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3211:1: (lv_blockingType_12_0= ruleReviewBlockingType )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3212:3: lv_blockingType_12_0= ruleReviewBlockingType
                  {

                     newCompositeNode(
                        grammarAccess.getPeerReviewDefAccess().getBlockingTypeReviewBlockingTypeEnumRuleCall_9_0());

                     pushFollow(FOLLOW_ruleReviewBlockingType_in_rulePeerReviewDef6341);
                     lv_blockingType_12_0 = ruleReviewBlockingType();

                     state._fsp--;

                     if (current == null) {
                        current = createModelElementForParent(grammarAccess.getPeerReviewDefRule());
                     }
                     set(current, "blockingType", lv_blockingType_12_0, "ReviewBlockingType");
                     afterParserOrEnumRuleCall();

                  }

               }

               otherlv_13 = (Token) match(input, 72, FOLLOW_72_in_rulePeerReviewDef6353);

               newLeafNode(otherlv_13, grammarAccess.getPeerReviewDefAccess().getOnEventKeyword_10());

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3232:1: ( (lv_stateEvent_14_0= ruleWorkflowEventType ) )
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3233:1: (lv_stateEvent_14_0= ruleWorkflowEventType )
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3233:1: (lv_stateEvent_14_0= ruleWorkflowEventType )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3234:3: lv_stateEvent_14_0= ruleWorkflowEventType
                  {

                     newCompositeNode(
                        grammarAccess.getPeerReviewDefAccess().getStateEventWorkflowEventTypeEnumRuleCall_11_0());

                     pushFollow(FOLLOW_ruleWorkflowEventType_in_rulePeerReviewDef6374);
                     lv_stateEvent_14_0 = ruleWorkflowEventType();

                     state._fsp--;

                     if (current == null) {
                        current = createModelElementForParent(grammarAccess.getPeerReviewDefRule());
                     }
                     set(current, "stateEvent", lv_stateEvent_14_0, "WorkflowEventType");
                     afterParserOrEnumRuleCall();

                  }

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3250:2: (otherlv_15= 'assignee' ( (lv_assigneeRefs_16_0= ruleUserRef ) ) )*
               loop95: do {
                  int alt95 = 2;
                  int LA95_0 = input.LA(1);

                  if (LA95_0 == 73) {
                     alt95 = 1;
                  }

                  switch (alt95) {
                     case 1:
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3250:4: otherlv_15= 'assignee' ( (lv_assigneeRefs_16_0= ruleUserRef ) )
                     {
                        otherlv_15 = (Token) match(input, 73, FOLLOW_73_in_rulePeerReviewDef6387);

                        newLeafNode(otherlv_15, grammarAccess.getPeerReviewDefAccess().getAssigneeKeyword_12_0());

                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3254:1: ( (lv_assigneeRefs_16_0= ruleUserRef ) )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3255:1: (lv_assigneeRefs_16_0= ruleUserRef )
                        {
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3255:1: (lv_assigneeRefs_16_0= ruleUserRef )
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3256:3: lv_assigneeRefs_16_0= ruleUserRef
                           {

                              newCompositeNode(
                                 grammarAccess.getPeerReviewDefAccess().getAssigneeRefsUserRefParserRuleCall_12_1_0());

                              pushFollow(FOLLOW_ruleUserRef_in_rulePeerReviewDef6408);
                              lv_assigneeRefs_16_0 = ruleUserRef();

                              state._fsp--;

                              if (current == null) {
                                 current = createModelElementForParent(grammarAccess.getPeerReviewDefRule());
                              }
                              add(current, "assigneeRefs", lv_assigneeRefs_16_0, "UserRef");
                              afterParserOrEnumRuleCall();

                           }

                        }

                     }
                        break;

                     default:
                        break loop95;
                  }
               } while (true);

               otherlv_17 = (Token) match(input, 22, FOLLOW_22_in_rulePeerReviewDef6422);

               newLeafNode(otherlv_17, grammarAccess.getPeerReviewDefAccess().getRightCurlyBracketKeyword_13());

            }

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "rulePeerReviewDef"

   // $ANTLR start "entryRuleFollowupRef"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3284:1: entryRuleFollowupRef returns [EObject current=null] : iv_ruleFollowupRef= ruleFollowupRef EOF ;
   public final EObject entryRuleFollowupRef() throws RecognitionException {
      EObject current = null;

      EObject iv_ruleFollowupRef = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3285:2: (iv_ruleFollowupRef= ruleFollowupRef EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3286:2: iv_ruleFollowupRef= ruleFollowupRef EOF
         {
            newCompositeNode(grammarAccess.getFollowupRefRule());
            pushFollow(FOLLOW_ruleFollowupRef_in_entryRuleFollowupRef6458);
            iv_ruleFollowupRef = ruleFollowupRef();

            state._fsp--;

            current = iv_ruleFollowupRef;
            match(input, EOF, FOLLOW_EOF_in_entryRuleFollowupRef6468);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleFollowupRef"

   // $ANTLR start "ruleFollowupRef"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3293:1: ruleFollowupRef returns [EObject current=null] : (otherlv_0= 'followup by' (otherlv_1= 'assignee' ( (lv_assigneeRefs_2_0= ruleUserRef ) ) )+ ) ;
   public final EObject ruleFollowupRef() throws RecognitionException {
      EObject current = null;

      Token otherlv_0 = null;
      Token otherlv_1 = null;
      EObject lv_assigneeRefs_2_0 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3296:28: ( (otherlv_0= 'followup by' (otherlv_1= 'assignee' ( (lv_assigneeRefs_2_0= ruleUserRef ) ) )+ ) )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3297:1: (otherlv_0= 'followup by' (otherlv_1= 'assignee' ( (lv_assigneeRefs_2_0= ruleUserRef ) ) )+ )
         {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3297:1: (otherlv_0= 'followup by' (otherlv_1= 'assignee' ( (lv_assigneeRefs_2_0= ruleUserRef ) ) )+ )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3297:3: otherlv_0= 'followup by' (otherlv_1= 'assignee' ( (lv_assigneeRefs_2_0= ruleUserRef ) ) )+
            {
               otherlv_0 = (Token) match(input, 78, FOLLOW_78_in_ruleFollowupRef6505);

               newLeafNode(otherlv_0, grammarAccess.getFollowupRefAccess().getFollowupByKeyword_0());

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3301:1: (otherlv_1= 'assignee' ( (lv_assigneeRefs_2_0= ruleUserRef ) ) )+
               int cnt96 = 0;
               loop96: do {
                  int alt96 = 2;
                  int LA96_0 = input.LA(1);

                  if (LA96_0 == 73) {
                     alt96 = 1;
                  }

                  switch (alt96) {
                     case 1:
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3301:3: otherlv_1= 'assignee' ( (lv_assigneeRefs_2_0= ruleUserRef ) )
                     {
                        otherlv_1 = (Token) match(input, 73, FOLLOW_73_in_ruleFollowupRef6518);

                        newLeafNode(otherlv_1, grammarAccess.getFollowupRefAccess().getAssigneeKeyword_1_0());

                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3305:1: ( (lv_assigneeRefs_2_0= ruleUserRef ) )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3306:1: (lv_assigneeRefs_2_0= ruleUserRef )
                        {
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3306:1: (lv_assigneeRefs_2_0= ruleUserRef )
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3307:3: lv_assigneeRefs_2_0= ruleUserRef
                           {

                              newCompositeNode(
                                 grammarAccess.getFollowupRefAccess().getAssigneeRefsUserRefParserRuleCall_1_1_0());

                              pushFollow(FOLLOW_ruleUserRef_in_ruleFollowupRef6539);
                              lv_assigneeRefs_2_0 = ruleUserRef();

                              state._fsp--;

                              if (current == null) {
                                 current = createModelElementForParent(grammarAccess.getFollowupRefRule());
                              }
                              add(current, "assigneeRefs", lv_assigneeRefs_2_0, "UserRef");
                              afterParserOrEnumRuleCall();

                           }

                        }

                     }
                        break;

                     default:
                        if (cnt96 >= 1) {
                           break loop96;
                        }
                        EarlyExitException eee = new EarlyExitException(96, input);
                        throw eee;
                  }
                  cnt96++;
               } while (true);

            }

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleFollowupRef"

   // $ANTLR start "entryRuleUserRef"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3331:1: entryRuleUserRef returns [EObject current=null] : iv_ruleUserRef= ruleUserRef EOF ;
   public final EObject entryRuleUserRef() throws RecognitionException {
      EObject current = null;

      EObject iv_ruleUserRef = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3332:2: (iv_ruleUserRef= ruleUserRef EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3333:2: iv_ruleUserRef= ruleUserRef EOF
         {
            newCompositeNode(grammarAccess.getUserRefRule());
            pushFollow(FOLLOW_ruleUserRef_in_entryRuleUserRef6577);
            iv_ruleUserRef = ruleUserRef();

            state._fsp--;

            current = iv_ruleUserRef;
            match(input, EOF, FOLLOW_EOF_in_entryRuleUserRef6587);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleUserRef"

   // $ANTLR start "ruleUserRef"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3340:1: ruleUserRef returns [EObject current=null] : (this_UserByUserId_0= ruleUserByUserId | this_UserByName_1= ruleUserByName ) ;
   public final EObject ruleUserRef() throws RecognitionException {
      EObject current = null;

      EObject this_UserByUserId_0 = null;

      EObject this_UserByName_1 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3343:28: ( (this_UserByUserId_0= ruleUserByUserId | this_UserByName_1= ruleUserByName ) )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3344:1: (this_UserByUserId_0= ruleUserByUserId | this_UserByName_1= ruleUserByName )
         {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3344:1: (this_UserByUserId_0= ruleUserByUserId | this_UserByName_1= ruleUserByName )
            int alt97 = 2;
            int LA97_0 = input.LA(1);

            if (LA97_0 == 19) {
               alt97 = 1;
            } else if (LA97_0 == 79) {
               alt97 = 2;
            } else {
               NoViableAltException nvae = new NoViableAltException("", 97, 0, input);

               throw nvae;
            }
            switch (alt97) {
               case 1:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3345:5: this_UserByUserId_0= ruleUserByUserId
               {

                  newCompositeNode(grammarAccess.getUserRefAccess().getUserByUserIdParserRuleCall_0());

                  pushFollow(FOLLOW_ruleUserByUserId_in_ruleUserRef6634);
                  this_UserByUserId_0 = ruleUserByUserId();

                  state._fsp--;

                  current = this_UserByUserId_0;
                  afterParserOrEnumRuleCall();

               }
                  break;
               case 2:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3355:5: this_UserByName_1= ruleUserByName
               {

                  newCompositeNode(grammarAccess.getUserRefAccess().getUserByNameParserRuleCall_1());

                  pushFollow(FOLLOW_ruleUserByName_in_ruleUserRef6661);
                  this_UserByName_1 = ruleUserByName();

                  state._fsp--;

                  current = this_UserByName_1;
                  afterParserOrEnumRuleCall();

               }
                  break;

            }

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleUserRef"

   // $ANTLR start "entryRuleUserByUserId"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3371:1: entryRuleUserByUserId returns [EObject current=null] : iv_ruleUserByUserId= ruleUserByUserId EOF ;
   public final EObject entryRuleUserByUserId() throws RecognitionException {
      EObject current = null;

      EObject iv_ruleUserByUserId = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3372:2: (iv_ruleUserByUserId= ruleUserByUserId EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3373:2: iv_ruleUserByUserId= ruleUserByUserId EOF
         {
            newCompositeNode(grammarAccess.getUserByUserIdRule());
            pushFollow(FOLLOW_ruleUserByUserId_in_entryRuleUserByUserId6696);
            iv_ruleUserByUserId = ruleUserByUserId();

            state._fsp--;

            current = iv_ruleUserByUserId;
            match(input, EOF, FOLLOW_EOF_in_entryRuleUserByUserId6706);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleUserByUserId"

   // $ANTLR start "ruleUserByUserId"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3380:1: ruleUserByUserId returns [EObject current=null] : (otherlv_0= 'userId' ( (lv_userId_1_0= RULE_STRING ) ) ) ;
   public final EObject ruleUserByUserId() throws RecognitionException {
      EObject current = null;

      Token otherlv_0 = null;
      Token lv_userId_1_0 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3383:28: ( (otherlv_0= 'userId' ( (lv_userId_1_0= RULE_STRING ) ) ) )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3384:1: (otherlv_0= 'userId' ( (lv_userId_1_0= RULE_STRING ) ) )
         {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3384:1: (otherlv_0= 'userId' ( (lv_userId_1_0= RULE_STRING ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3384:3: otherlv_0= 'userId' ( (lv_userId_1_0= RULE_STRING ) )
            {
               otherlv_0 = (Token) match(input, 19, FOLLOW_19_in_ruleUserByUserId6743);

               newLeafNode(otherlv_0, grammarAccess.getUserByUserIdAccess().getUserIdKeyword_0());

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3388:1: ( (lv_userId_1_0= RULE_STRING ) )
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3389:1: (lv_userId_1_0= RULE_STRING )
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3389:1: (lv_userId_1_0= RULE_STRING )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3390:3: lv_userId_1_0= RULE_STRING
                  {
                     lv_userId_1_0 = (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleUserByUserId6760);

                     newLeafNode(lv_userId_1_0,
                        grammarAccess.getUserByUserIdAccess().getUserIdSTRINGTerminalRuleCall_1_0());

                     if (current == null) {
                        current = createModelElement(grammarAccess.getUserByUserIdRule());
                     }
                     setWithLastConsumed(current, "userId", lv_userId_1_0, "STRING");

                  }

               }

            }

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleUserByUserId"

   // $ANTLR start "entryRuleUserByName"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3414:1: entryRuleUserByName returns [EObject current=null] : iv_ruleUserByName= ruleUserByName EOF ;
   public final EObject entryRuleUserByName() throws RecognitionException {
      EObject current = null;

      EObject iv_ruleUserByName = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3415:2: (iv_ruleUserByName= ruleUserByName EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3416:2: iv_ruleUserByName= ruleUserByName EOF
         {
            newCompositeNode(grammarAccess.getUserByNameRule());
            pushFollow(FOLLOW_ruleUserByName_in_entryRuleUserByName6801);
            iv_ruleUserByName = ruleUserByName();

            state._fsp--;

            current = iv_ruleUserByName;
            match(input, EOF, FOLLOW_EOF_in_entryRuleUserByName6811);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleUserByName"

   // $ANTLR start "ruleUserByName"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3423:1: ruleUserByName returns [EObject current=null] : (otherlv_0= 'named' ( (lv_userName_1_0= RULE_STRING ) ) ) ;
   public final EObject ruleUserByName() throws RecognitionException {
      EObject current = null;

      Token otherlv_0 = null;
      Token lv_userName_1_0 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3426:28: ( (otherlv_0= 'named' ( (lv_userName_1_0= RULE_STRING ) ) ) )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3427:1: (otherlv_0= 'named' ( (lv_userName_1_0= RULE_STRING ) ) )
         {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3427:1: (otherlv_0= 'named' ( (lv_userName_1_0= RULE_STRING ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3427:3: otherlv_0= 'named' ( (lv_userName_1_0= RULE_STRING ) )
            {
               otherlv_0 = (Token) match(input, 79, FOLLOW_79_in_ruleUserByName6848);

               newLeafNode(otherlv_0, grammarAccess.getUserByNameAccess().getNamedKeyword_0());

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3431:1: ( (lv_userName_1_0= RULE_STRING ) )
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3432:1: (lv_userName_1_0= RULE_STRING )
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3432:1: (lv_userName_1_0= RULE_STRING )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3433:3: lv_userName_1_0= RULE_STRING
                  {
                     lv_userName_1_0 = (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleUserByName6865);

                     newLeafNode(lv_userName_1_0,
                        grammarAccess.getUserByNameAccess().getUserNameSTRINGTerminalRuleCall_1_0());

                     if (current == null) {
                        current = createModelElement(grammarAccess.getUserByNameRule());
                     }
                     setWithLastConsumed(current, "userName", lv_userName_1_0, "STRING");

                  }

               }

            }

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleUserByName"

   // $ANTLR start "entryRuleDECISION_REVIEW_NAME_REFERENCE"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3457:1: entryRuleDECISION_REVIEW_NAME_REFERENCE returns [String current=null] : iv_ruleDECISION_REVIEW_NAME_REFERENCE= ruleDECISION_REVIEW_NAME_REFERENCE EOF ;
   public final String entryRuleDECISION_REVIEW_NAME_REFERENCE() throws RecognitionException {
      String current = null;

      AntlrDatatypeRuleToken iv_ruleDECISION_REVIEW_NAME_REFERENCE = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3458:2: (iv_ruleDECISION_REVIEW_NAME_REFERENCE= ruleDECISION_REVIEW_NAME_REFERENCE EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3459:2: iv_ruleDECISION_REVIEW_NAME_REFERENCE= ruleDECISION_REVIEW_NAME_REFERENCE EOF
         {
            newCompositeNode(grammarAccess.getDECISION_REVIEW_NAME_REFERENCERule());
            pushFollow(FOLLOW_ruleDECISION_REVIEW_NAME_REFERENCE_in_entryRuleDECISION_REVIEW_NAME_REFERENCE6907);
            iv_ruleDECISION_REVIEW_NAME_REFERENCE = ruleDECISION_REVIEW_NAME_REFERENCE();

            state._fsp--;

            current = iv_ruleDECISION_REVIEW_NAME_REFERENCE.getText();
            match(input, EOF, FOLLOW_EOF_in_entryRuleDECISION_REVIEW_NAME_REFERENCE6918);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleDECISION_REVIEW_NAME_REFERENCE"

   // $ANTLR start "ruleDECISION_REVIEW_NAME_REFERENCE"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3466:1: ruleDECISION_REVIEW_NAME_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
   public final AntlrDatatypeRuleToken ruleDECISION_REVIEW_NAME_REFERENCE() throws RecognitionException {
      AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

      Token this_STRING_0 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3469:28: (this_STRING_0= RULE_STRING )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3470:5: this_STRING_0= RULE_STRING
         {
            this_STRING_0 =
               (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleDECISION_REVIEW_NAME_REFERENCE6957);

            current.merge(this_STRING_0);

            newLeafNode(this_STRING_0,
               grammarAccess.getDECISION_REVIEW_NAME_REFERENCEAccess().getSTRINGTerminalRuleCall());

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleDECISION_REVIEW_NAME_REFERENCE"

   // $ANTLR start "entryRulePEER_REVIEW_NAME_REFERENCE"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3485:1: entryRulePEER_REVIEW_NAME_REFERENCE returns [String current=null] : iv_rulePEER_REVIEW_NAME_REFERENCE= rulePEER_REVIEW_NAME_REFERENCE EOF ;
   public final String entryRulePEER_REVIEW_NAME_REFERENCE() throws RecognitionException {
      String current = null;

      AntlrDatatypeRuleToken iv_rulePEER_REVIEW_NAME_REFERENCE = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3486:2: (iv_rulePEER_REVIEW_NAME_REFERENCE= rulePEER_REVIEW_NAME_REFERENCE EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3487:2: iv_rulePEER_REVIEW_NAME_REFERENCE= rulePEER_REVIEW_NAME_REFERENCE EOF
         {
            newCompositeNode(grammarAccess.getPEER_REVIEW_NAME_REFERENCERule());
            pushFollow(FOLLOW_rulePEER_REVIEW_NAME_REFERENCE_in_entryRulePEER_REVIEW_NAME_REFERENCE7002);
            iv_rulePEER_REVIEW_NAME_REFERENCE = rulePEER_REVIEW_NAME_REFERENCE();

            state._fsp--;

            current = iv_rulePEER_REVIEW_NAME_REFERENCE.getText();
            match(input, EOF, FOLLOW_EOF_in_entryRulePEER_REVIEW_NAME_REFERENCE7013);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRulePEER_REVIEW_NAME_REFERENCE"

   // $ANTLR start "rulePEER_REVIEW_NAME_REFERENCE"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3494:1: rulePEER_REVIEW_NAME_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
   public final AntlrDatatypeRuleToken rulePEER_REVIEW_NAME_REFERENCE() throws RecognitionException {
      AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

      Token this_STRING_0 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3497:28: (this_STRING_0= RULE_STRING )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3498:5: this_STRING_0= RULE_STRING
         {
            this_STRING_0 = (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_rulePEER_REVIEW_NAME_REFERENCE7052);

            current.merge(this_STRING_0);

            newLeafNode(this_STRING_0, grammarAccess.getPEER_REVIEW_NAME_REFERENCEAccess().getSTRINGTerminalRuleCall());

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "rulePEER_REVIEW_NAME_REFERENCE"

   // $ANTLR start "entryRuleSTATE_NAME_REFERENCE"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3513:1: entryRuleSTATE_NAME_REFERENCE returns [String current=null] : iv_ruleSTATE_NAME_REFERENCE= ruleSTATE_NAME_REFERENCE EOF ;
   public final String entryRuleSTATE_NAME_REFERENCE() throws RecognitionException {
      String current = null;

      AntlrDatatypeRuleToken iv_ruleSTATE_NAME_REFERENCE = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3514:2: (iv_ruleSTATE_NAME_REFERENCE= ruleSTATE_NAME_REFERENCE EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3515:2: iv_ruleSTATE_NAME_REFERENCE= ruleSTATE_NAME_REFERENCE EOF
         {
            newCompositeNode(grammarAccess.getSTATE_NAME_REFERENCERule());
            pushFollow(FOLLOW_ruleSTATE_NAME_REFERENCE_in_entryRuleSTATE_NAME_REFERENCE7097);
            iv_ruleSTATE_NAME_REFERENCE = ruleSTATE_NAME_REFERENCE();

            state._fsp--;

            current = iv_ruleSTATE_NAME_REFERENCE.getText();
            match(input, EOF, FOLLOW_EOF_in_entryRuleSTATE_NAME_REFERENCE7108);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleSTATE_NAME_REFERENCE"

   // $ANTLR start "ruleSTATE_NAME_REFERENCE"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3522:1: ruleSTATE_NAME_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
   public final AntlrDatatypeRuleToken ruleSTATE_NAME_REFERENCE() throws RecognitionException {
      AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

      Token this_STRING_0 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3525:28: (this_STRING_0= RULE_STRING )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3526:5: this_STRING_0= RULE_STRING
         {
            this_STRING_0 = (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleSTATE_NAME_REFERENCE7147);

            current.merge(this_STRING_0);

            newLeafNode(this_STRING_0, grammarAccess.getSTATE_NAME_REFERENCEAccess().getSTRINGTerminalRuleCall());

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleSTATE_NAME_REFERENCE"

   // $ANTLR start "entryRuleWIDGET_NAME_REFERENCE"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3541:1: entryRuleWIDGET_NAME_REFERENCE returns [String current=null] : iv_ruleWIDGET_NAME_REFERENCE= ruleWIDGET_NAME_REFERENCE EOF ;
   public final String entryRuleWIDGET_NAME_REFERENCE() throws RecognitionException {
      String current = null;

      AntlrDatatypeRuleToken iv_ruleWIDGET_NAME_REFERENCE = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3542:2: (iv_ruleWIDGET_NAME_REFERENCE= ruleWIDGET_NAME_REFERENCE EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3543:2: iv_ruleWIDGET_NAME_REFERENCE= ruleWIDGET_NAME_REFERENCE EOF
         {
            newCompositeNode(grammarAccess.getWIDGET_NAME_REFERENCERule());
            pushFollow(FOLLOW_ruleWIDGET_NAME_REFERENCE_in_entryRuleWIDGET_NAME_REFERENCE7192);
            iv_ruleWIDGET_NAME_REFERENCE = ruleWIDGET_NAME_REFERENCE();

            state._fsp--;

            current = iv_ruleWIDGET_NAME_REFERENCE.getText();
            match(input, EOF, FOLLOW_EOF_in_entryRuleWIDGET_NAME_REFERENCE7203);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleWIDGET_NAME_REFERENCE"

   // $ANTLR start "ruleWIDGET_NAME_REFERENCE"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3550:1: ruleWIDGET_NAME_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
   public final AntlrDatatypeRuleToken ruleWIDGET_NAME_REFERENCE() throws RecognitionException {
      AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

      Token this_STRING_0 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3553:28: (this_STRING_0= RULE_STRING )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3554:5: this_STRING_0= RULE_STRING
         {
            this_STRING_0 = (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleWIDGET_NAME_REFERENCE7242);

            current.merge(this_STRING_0);

            newLeafNode(this_STRING_0, grammarAccess.getWIDGET_NAME_REFERENCEAccess().getSTRINGTerminalRuleCall());

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleWIDGET_NAME_REFERENCE"

   // $ANTLR start "entryRuleWORK_DEFINITION_NAME_REFERENCE"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3569:1: entryRuleWORK_DEFINITION_NAME_REFERENCE returns [String current=null] : iv_ruleWORK_DEFINITION_NAME_REFERENCE= ruleWORK_DEFINITION_NAME_REFERENCE EOF ;
   public final String entryRuleWORK_DEFINITION_NAME_REFERENCE() throws RecognitionException {
      String current = null;

      AntlrDatatypeRuleToken iv_ruleWORK_DEFINITION_NAME_REFERENCE = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3570:2: (iv_ruleWORK_DEFINITION_NAME_REFERENCE= ruleWORK_DEFINITION_NAME_REFERENCE EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3571:2: iv_ruleWORK_DEFINITION_NAME_REFERENCE= ruleWORK_DEFINITION_NAME_REFERENCE EOF
         {
            newCompositeNode(grammarAccess.getWORK_DEFINITION_NAME_REFERENCERule());
            pushFollow(FOLLOW_ruleWORK_DEFINITION_NAME_REFERENCE_in_entryRuleWORK_DEFINITION_NAME_REFERENCE7287);
            iv_ruleWORK_DEFINITION_NAME_REFERENCE = ruleWORK_DEFINITION_NAME_REFERENCE();

            state._fsp--;

            current = iv_ruleWORK_DEFINITION_NAME_REFERENCE.getText();
            match(input, EOF, FOLLOW_EOF_in_entryRuleWORK_DEFINITION_NAME_REFERENCE7298);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleWORK_DEFINITION_NAME_REFERENCE"

   // $ANTLR start "ruleWORK_DEFINITION_NAME_REFERENCE"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3578:1: ruleWORK_DEFINITION_NAME_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
   public final AntlrDatatypeRuleToken ruleWORK_DEFINITION_NAME_REFERENCE() throws RecognitionException {
      AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

      Token this_STRING_0 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3581:28: (this_STRING_0= RULE_STRING )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3582:5: this_STRING_0= RULE_STRING
         {
            this_STRING_0 =
               (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleWORK_DEFINITION_NAME_REFERENCE7337);

            current.merge(this_STRING_0);

            newLeafNode(this_STRING_0,
               grammarAccess.getWORK_DEFINITION_NAME_REFERENCEAccess().getSTRINGTerminalRuleCall());

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleWORK_DEFINITION_NAME_REFERENCE"

   // $ANTLR start "entryRuleToState"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3597:1: entryRuleToState returns [EObject current=null] : iv_ruleToState= ruleToState EOF ;
   public final EObject entryRuleToState() throws RecognitionException {
      EObject current = null;

      EObject iv_ruleToState = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3598:2: (iv_ruleToState= ruleToState EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3599:2: iv_ruleToState= ruleToState EOF
         {
            newCompositeNode(grammarAccess.getToStateRule());
            pushFollow(FOLLOW_ruleToState_in_entryRuleToState7381);
            iv_ruleToState = ruleToState();

            state._fsp--;

            current = iv_ruleToState;
            match(input, EOF, FOLLOW_EOF_in_entryRuleToState7391);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleToState"

   // $ANTLR start "ruleToState"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3606:1: ruleToState returns [EObject current=null] : (otherlv_0= 'to' ( ( ruleSTATE_NAME_REFERENCE ) ) ( (lv_options_2_0= ruleTransitionOption ) )* ) ;
   public final EObject ruleToState() throws RecognitionException {
      EObject current = null;

      Token otherlv_0 = null;
      AntlrDatatypeRuleToken lv_options_2_0 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3609:28: ( (otherlv_0= 'to' ( ( ruleSTATE_NAME_REFERENCE ) ) ( (lv_options_2_0= ruleTransitionOption ) )* ) )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3610:1: (otherlv_0= 'to' ( ( ruleSTATE_NAME_REFERENCE ) ) ( (lv_options_2_0= ruleTransitionOption ) )* )
         {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3610:1: (otherlv_0= 'to' ( ( ruleSTATE_NAME_REFERENCE ) ) ( (lv_options_2_0= ruleTransitionOption ) )* )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3610:3: otherlv_0= 'to' ( ( ruleSTATE_NAME_REFERENCE ) ) ( (lv_options_2_0= ruleTransitionOption ) )*
            {
               otherlv_0 = (Token) match(input, 80, FOLLOW_80_in_ruleToState7428);

               newLeafNode(otherlv_0, grammarAccess.getToStateAccess().getToKeyword_0());

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3614:1: ( ( ruleSTATE_NAME_REFERENCE ) )
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3615:1: ( ruleSTATE_NAME_REFERENCE )
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3615:1: ( ruleSTATE_NAME_REFERENCE )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3616:3: ruleSTATE_NAME_REFERENCE
                  {

                     if (current == null) {
                        current = createModelElement(grammarAccess.getToStateRule());
                     }

                     newCompositeNode(grammarAccess.getToStateAccess().getStateStateDefCrossReference_1_0());

                     pushFollow(FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleToState7451);
                     ruleSTATE_NAME_REFERENCE();

                     state._fsp--;

                     afterParserOrEnumRuleCall();

                  }

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3629:2: ( (lv_options_2_0= ruleTransitionOption ) )*
               loop98: do {
                  int alt98 = 2;
                  int LA98_0 = input.LA(1);

                  if (LA98_0 == RULE_STRING || LA98_0 >= 87 && LA98_0 <= 88) {
                     alt98 = 1;
                  }

                  switch (alt98) {
                     case 1:
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3630:1: (lv_options_2_0= ruleTransitionOption )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3630:1: (lv_options_2_0= ruleTransitionOption )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3631:3: lv_options_2_0= ruleTransitionOption
                        {

                           newCompositeNode(
                              grammarAccess.getToStateAccess().getOptionsTransitionOptionParserRuleCall_2_0());

                           pushFollow(FOLLOW_ruleTransitionOption_in_ruleToState7472);
                           lv_options_2_0 = ruleTransitionOption();

                           state._fsp--;

                           if (current == null) {
                              current = createModelElementForParent(grammarAccess.getToStateRule());
                           }
                           add(current, "options", lv_options_2_0, "TransitionOption");
                           afterParserOrEnumRuleCall();

                        }

                     }
                        break;

                     default:
                        break loop98;
                  }
               } while (true);

            }

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleToState"

   // $ANTLR start "entryRuleLayoutType"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3655:1: entryRuleLayoutType returns [EObject current=null] : iv_ruleLayoutType= ruleLayoutType EOF ;
   public final EObject entryRuleLayoutType() throws RecognitionException {
      EObject current = null;

      EObject iv_ruleLayoutType = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3656:2: (iv_ruleLayoutType= ruleLayoutType EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3657:2: iv_ruleLayoutType= ruleLayoutType EOF
         {
            newCompositeNode(grammarAccess.getLayoutTypeRule());
            pushFollow(FOLLOW_ruleLayoutType_in_entryRuleLayoutType7509);
            iv_ruleLayoutType = ruleLayoutType();

            state._fsp--;

            current = iv_ruleLayoutType;
            match(input, EOF, FOLLOW_EOF_in_entryRuleLayoutType7519);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleLayoutType"

   // $ANTLR start "ruleLayoutType"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3664:1: ruleLayoutType returns [EObject current=null] : (this_LayoutDef_0= ruleLayoutDef | this_LayoutCopy_1= ruleLayoutCopy ) ;
   public final EObject ruleLayoutType() throws RecognitionException {
      EObject current = null;

      EObject this_LayoutDef_0 = null;

      EObject this_LayoutCopy_1 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3667:28: ( (this_LayoutDef_0= ruleLayoutDef | this_LayoutCopy_1= ruleLayoutCopy ) )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3668:1: (this_LayoutDef_0= ruleLayoutDef | this_LayoutCopy_1= ruleLayoutCopy )
         {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3668:1: (this_LayoutDef_0= ruleLayoutDef | this_LayoutCopy_1= ruleLayoutCopy )
            int alt99 = 2;
            int LA99_0 = input.LA(1);

            if (LA99_0 == 81) {
               alt99 = 1;
            } else if (LA99_0 == 82) {
               alt99 = 2;
            } else {
               NoViableAltException nvae = new NoViableAltException("", 99, 0, input);

               throw nvae;
            }
            switch (alt99) {
               case 1:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3669:5: this_LayoutDef_0= ruleLayoutDef
               {

                  newCompositeNode(grammarAccess.getLayoutTypeAccess().getLayoutDefParserRuleCall_0());

                  pushFollow(FOLLOW_ruleLayoutDef_in_ruleLayoutType7566);
                  this_LayoutDef_0 = ruleLayoutDef();

                  state._fsp--;

                  current = this_LayoutDef_0;
                  afterParserOrEnumRuleCall();

               }
                  break;
               case 2:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3679:5: this_LayoutCopy_1= ruleLayoutCopy
               {

                  newCompositeNode(grammarAccess.getLayoutTypeAccess().getLayoutCopyParserRuleCall_1());

                  pushFollow(FOLLOW_ruleLayoutCopy_in_ruleLayoutType7593);
                  this_LayoutCopy_1 = ruleLayoutCopy();

                  state._fsp--;

                  current = this_LayoutCopy_1;
                  afterParserOrEnumRuleCall();

               }
                  break;

            }

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleLayoutType"

   // $ANTLR start "entryRuleLayoutDef"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3695:1: entryRuleLayoutDef returns [EObject current=null] : iv_ruleLayoutDef= ruleLayoutDef EOF ;
   public final EObject entryRuleLayoutDef() throws RecognitionException {
      EObject current = null;

      EObject iv_ruleLayoutDef = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3696:2: (iv_ruleLayoutDef= ruleLayoutDef EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3697:2: iv_ruleLayoutDef= ruleLayoutDef EOF
         {
            newCompositeNode(grammarAccess.getLayoutDefRule());
            pushFollow(FOLLOW_ruleLayoutDef_in_entryRuleLayoutDef7628);
            iv_ruleLayoutDef = ruleLayoutDef();

            state._fsp--;

            current = iv_ruleLayoutDef;
            match(input, EOF, FOLLOW_EOF_in_entryRuleLayoutDef7638);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleLayoutDef"

   // $ANTLR start "ruleLayoutDef"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3704:1: ruleLayoutDef returns [EObject current=null] : (otherlv_0= 'layout' otherlv_1= '{' ( (lv_layoutItems_2_0= ruleLayoutItem ) )+ otherlv_3= '}' ) ;
   public final EObject ruleLayoutDef() throws RecognitionException {
      EObject current = null;

      Token otherlv_0 = null;
      Token otherlv_1 = null;
      Token otherlv_3 = null;
      EObject lv_layoutItems_2_0 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3707:28: ( (otherlv_0= 'layout' otherlv_1= '{' ( (lv_layoutItems_2_0= ruleLayoutItem ) )+ otherlv_3= '}' ) )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3708:1: (otherlv_0= 'layout' otherlv_1= '{' ( (lv_layoutItems_2_0= ruleLayoutItem ) )+ otherlv_3= '}' )
         {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3708:1: (otherlv_0= 'layout' otherlv_1= '{' ( (lv_layoutItems_2_0= ruleLayoutItem ) )+ otherlv_3= '}' )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3708:3: otherlv_0= 'layout' otherlv_1= '{' ( (lv_layoutItems_2_0= ruleLayoutItem ) )+ otherlv_3= '}'
            {
               otherlv_0 = (Token) match(input, 81, FOLLOW_81_in_ruleLayoutDef7675);

               newLeafNode(otherlv_0, grammarAccess.getLayoutDefAccess().getLayoutKeyword_0());

               otherlv_1 = (Token) match(input, 17, FOLLOW_17_in_ruleLayoutDef7687);

               newLeafNode(otherlv_1, grammarAccess.getLayoutDefAccess().getLeftCurlyBracketKeyword_1());

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3716:1: ( (lv_layoutItems_2_0= ruleLayoutItem ) )+
               int cnt100 = 0;
               loop100: do {
                  int alt100 = 2;
                  int LA100_0 = input.LA(1);

                  if (LA100_0 >= 58 && LA100_0 <= 59 || LA100_0 == 83) {
                     alt100 = 1;
                  }

                  switch (alt100) {
                     case 1:
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3717:1: (lv_layoutItems_2_0= ruleLayoutItem )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3717:1: (lv_layoutItems_2_0= ruleLayoutItem )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3718:3: lv_layoutItems_2_0= ruleLayoutItem
                        {

                           newCompositeNode(
                              grammarAccess.getLayoutDefAccess().getLayoutItemsLayoutItemParserRuleCall_2_0());

                           pushFollow(FOLLOW_ruleLayoutItem_in_ruleLayoutDef7708);
                           lv_layoutItems_2_0 = ruleLayoutItem();

                           state._fsp--;

                           if (current == null) {
                              current = createModelElementForParent(grammarAccess.getLayoutDefRule());
                           }
                           add(current, "layoutItems", lv_layoutItems_2_0, "LayoutItem");
                           afterParserOrEnumRuleCall();

                        }

                     }
                        break;

                     default:
                        if (cnt100 >= 1) {
                           break loop100;
                        }
                        EarlyExitException eee = new EarlyExitException(100, input);
                        throw eee;
                  }
                  cnt100++;
               } while (true);

               otherlv_3 = (Token) match(input, 22, FOLLOW_22_in_ruleLayoutDef7721);

               newLeafNode(otherlv_3, grammarAccess.getLayoutDefAccess().getRightCurlyBracketKeyword_3());

            }

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleLayoutDef"

   // $ANTLR start "entryRuleLayoutCopy"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3746:1: entryRuleLayoutCopy returns [EObject current=null] : iv_ruleLayoutCopy= ruleLayoutCopy EOF ;
   public final EObject entryRuleLayoutCopy() throws RecognitionException {
      EObject current = null;

      EObject iv_ruleLayoutCopy = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3747:2: (iv_ruleLayoutCopy= ruleLayoutCopy EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3748:2: iv_ruleLayoutCopy= ruleLayoutCopy EOF
         {
            newCompositeNode(grammarAccess.getLayoutCopyRule());
            pushFollow(FOLLOW_ruleLayoutCopy_in_entryRuleLayoutCopy7757);
            iv_ruleLayoutCopy = ruleLayoutCopy();

            state._fsp--;

            current = iv_ruleLayoutCopy;
            match(input, EOF, FOLLOW_EOF_in_entryRuleLayoutCopy7767);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleLayoutCopy"

   // $ANTLR start "ruleLayoutCopy"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3755:1: ruleLayoutCopy returns [EObject current=null] : (otherlv_0= 'layoutCopyFrom' ( ( ruleSTATE_NAME_REFERENCE ) ) ) ;
   public final EObject ruleLayoutCopy() throws RecognitionException {
      EObject current = null;

      Token otherlv_0 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3758:28: ( (otherlv_0= 'layoutCopyFrom' ( ( ruleSTATE_NAME_REFERENCE ) ) ) )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3759:1: (otherlv_0= 'layoutCopyFrom' ( ( ruleSTATE_NAME_REFERENCE ) ) )
         {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3759:1: (otherlv_0= 'layoutCopyFrom' ( ( ruleSTATE_NAME_REFERENCE ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3759:3: otherlv_0= 'layoutCopyFrom' ( ( ruleSTATE_NAME_REFERENCE ) )
            {
               otherlv_0 = (Token) match(input, 82, FOLLOW_82_in_ruleLayoutCopy7804);

               newLeafNode(otherlv_0, grammarAccess.getLayoutCopyAccess().getLayoutCopyFromKeyword_0());

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3763:1: ( ( ruleSTATE_NAME_REFERENCE ) )
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3764:1: ( ruleSTATE_NAME_REFERENCE )
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3764:1: ( ruleSTATE_NAME_REFERENCE )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3765:3: ruleSTATE_NAME_REFERENCE
                  {

                     if (current == null) {
                        current = createModelElement(grammarAccess.getLayoutCopyRule());
                     }

                     newCompositeNode(grammarAccess.getLayoutCopyAccess().getStateStateDefCrossReference_1_0());

                     pushFollow(FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleLayoutCopy7827);
                     ruleSTATE_NAME_REFERENCE();

                     state._fsp--;

                     afterParserOrEnumRuleCall();

                  }

               }

            }

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleLayoutCopy"

   // $ANTLR start "entryRuleLayoutItem"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3786:1: entryRuleLayoutItem returns [EObject current=null] : iv_ruleLayoutItem= ruleLayoutItem EOF ;
   public final EObject entryRuleLayoutItem() throws RecognitionException {
      EObject current = null;

      EObject iv_ruleLayoutItem = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3787:2: (iv_ruleLayoutItem= ruleLayoutItem EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3788:2: iv_ruleLayoutItem= ruleLayoutItem EOF
         {
            newCompositeNode(grammarAccess.getLayoutItemRule());
            pushFollow(FOLLOW_ruleLayoutItem_in_entryRuleLayoutItem7863);
            iv_ruleLayoutItem = ruleLayoutItem();

            state._fsp--;

            current = iv_ruleLayoutItem;
            match(input, EOF, FOLLOW_EOF_in_entryRuleLayoutItem7873);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleLayoutItem"

   // $ANTLR start "ruleLayoutItem"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3795:1: ruleLayoutItem returns [EObject current=null] : (this_WidgetRef_0= ruleWidgetRef | this_AttrWidget_1= ruleAttrWidget | this_Composite_2= ruleComposite ) ;
   public final EObject ruleLayoutItem() throws RecognitionException {
      EObject current = null;

      EObject this_WidgetRef_0 = null;

      EObject this_AttrWidget_1 = null;

      EObject this_Composite_2 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3798:28: ( (this_WidgetRef_0= ruleWidgetRef | this_AttrWidget_1= ruleAttrWidget | this_Composite_2= ruleComposite ) )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3799:1: (this_WidgetRef_0= ruleWidgetRef | this_AttrWidget_1= ruleAttrWidget | this_Composite_2= ruleComposite )
         {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3799:1: (this_WidgetRef_0= ruleWidgetRef | this_AttrWidget_1= ruleAttrWidget | this_Composite_2= ruleComposite )
            int alt101 = 3;
            switch (input.LA(1)) {
               case 58: {
                  alt101 = 1;
               }
                  break;
               case 59: {
                  alt101 = 2;
               }
                  break;
               case 83: {
                  alt101 = 3;
               }
                  break;
               default:
                  NoViableAltException nvae = new NoViableAltException("", 101, 0, input);

                  throw nvae;
            }

            switch (alt101) {
               case 1:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3800:5: this_WidgetRef_0= ruleWidgetRef
               {

                  newCompositeNode(grammarAccess.getLayoutItemAccess().getWidgetRefParserRuleCall_0());

                  pushFollow(FOLLOW_ruleWidgetRef_in_ruleLayoutItem7920);
                  this_WidgetRef_0 = ruleWidgetRef();

                  state._fsp--;

                  current = this_WidgetRef_0;
                  afterParserOrEnumRuleCall();

               }
                  break;
               case 2:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3810:5: this_AttrWidget_1= ruleAttrWidget
               {

                  newCompositeNode(grammarAccess.getLayoutItemAccess().getAttrWidgetParserRuleCall_1());

                  pushFollow(FOLLOW_ruleAttrWidget_in_ruleLayoutItem7947);
                  this_AttrWidget_1 = ruleAttrWidget();

                  state._fsp--;

                  current = this_AttrWidget_1;
                  afterParserOrEnumRuleCall();

               }
                  break;
               case 3:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3820:5: this_Composite_2= ruleComposite
               {

                  newCompositeNode(grammarAccess.getLayoutItemAccess().getCompositeParserRuleCall_2());

                  pushFollow(FOLLOW_ruleComposite_in_ruleLayoutItem7974);
                  this_Composite_2 = ruleComposite();

                  state._fsp--;

                  current = this_Composite_2;
                  afterParserOrEnumRuleCall();

               }
                  break;

            }

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleLayoutItem"

   // $ANTLR start "entryRuleComposite"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3836:1: entryRuleComposite returns [EObject current=null] : iv_ruleComposite= ruleComposite EOF ;
   public final EObject entryRuleComposite() throws RecognitionException {
      EObject current = null;

      EObject iv_ruleComposite = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3837:2: (iv_ruleComposite= ruleComposite EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3838:2: iv_ruleComposite= ruleComposite EOF
         {
            newCompositeNode(grammarAccess.getCompositeRule());
            pushFollow(FOLLOW_ruleComposite_in_entryRuleComposite8009);
            iv_ruleComposite = ruleComposite();

            state._fsp--;

            current = iv_ruleComposite;
            match(input, EOF, FOLLOW_EOF_in_entryRuleComposite8019);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleComposite"

   // $ANTLR start "ruleComposite"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3845:1: ruleComposite returns [EObject current=null] : (otherlv_0= 'composite' otherlv_1= '{' otherlv_2= 'numColumns' ( (lv_numColumns_3_0= RULE_INT ) ) ( (lv_layoutItems_4_0= ruleLayoutItem ) )+ (otherlv_5= 'option' ( (lv_options_6_0= ruleCompositeOption ) ) )* otherlv_7= '}' ) ;
   public final EObject ruleComposite() throws RecognitionException {
      EObject current = null;

      Token otherlv_0 = null;
      Token otherlv_1 = null;
      Token otherlv_2 = null;
      Token lv_numColumns_3_0 = null;
      Token otherlv_5 = null;
      Token otherlv_7 = null;
      EObject lv_layoutItems_4_0 = null;

      AntlrDatatypeRuleToken lv_options_6_0 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3848:28: ( (otherlv_0= 'composite' otherlv_1= '{' otherlv_2= 'numColumns' ( (lv_numColumns_3_0= RULE_INT ) ) ( (lv_layoutItems_4_0= ruleLayoutItem ) )+ (otherlv_5= 'option' ( (lv_options_6_0= ruleCompositeOption ) ) )* otherlv_7= '}' ) )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3849:1: (otherlv_0= 'composite' otherlv_1= '{' otherlv_2= 'numColumns' ( (lv_numColumns_3_0= RULE_INT ) ) ( (lv_layoutItems_4_0= ruleLayoutItem ) )+ (otherlv_5= 'option' ( (lv_options_6_0= ruleCompositeOption ) ) )* otherlv_7= '}' )
         {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3849:1: (otherlv_0= 'composite' otherlv_1= '{' otherlv_2= 'numColumns' ( (lv_numColumns_3_0= RULE_INT ) ) ( (lv_layoutItems_4_0= ruleLayoutItem ) )+ (otherlv_5= 'option' ( (lv_options_6_0= ruleCompositeOption ) ) )* otherlv_7= '}' )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3849:3: otherlv_0= 'composite' otherlv_1= '{' otherlv_2= 'numColumns' ( (lv_numColumns_3_0= RULE_INT ) ) ( (lv_layoutItems_4_0= ruleLayoutItem ) )+ (otherlv_5= 'option' ( (lv_options_6_0= ruleCompositeOption ) ) )* otherlv_7= '}'
            {
               otherlv_0 = (Token) match(input, 83, FOLLOW_83_in_ruleComposite8056);

               newLeafNode(otherlv_0, grammarAccess.getCompositeAccess().getCompositeKeyword_0());

               otherlv_1 = (Token) match(input, 17, FOLLOW_17_in_ruleComposite8068);

               newLeafNode(otherlv_1, grammarAccess.getCompositeAccess().getLeftCurlyBracketKeyword_1());

               otherlv_2 = (Token) match(input, 84, FOLLOW_84_in_ruleComposite8080);

               newLeafNode(otherlv_2, grammarAccess.getCompositeAccess().getNumColumnsKeyword_2());

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3861:1: ( (lv_numColumns_3_0= RULE_INT ) )
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3862:1: (lv_numColumns_3_0= RULE_INT )
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3862:1: (lv_numColumns_3_0= RULE_INT )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3863:3: lv_numColumns_3_0= RULE_INT
                  {
                     lv_numColumns_3_0 = (Token) match(input, RULE_INT, FOLLOW_RULE_INT_in_ruleComposite8097);

                     newLeafNode(lv_numColumns_3_0,
                        grammarAccess.getCompositeAccess().getNumColumnsINTTerminalRuleCall_3_0());

                     if (current == null) {
                        current = createModelElement(grammarAccess.getCompositeRule());
                     }
                     setWithLastConsumed(current, "numColumns", lv_numColumns_3_0, "INT");

                  }

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3879:2: ( (lv_layoutItems_4_0= ruleLayoutItem ) )+
               int cnt102 = 0;
               loop102: do {
                  int alt102 = 2;
                  int LA102_0 = input.LA(1);

                  if (LA102_0 >= 58 && LA102_0 <= 59 || LA102_0 == 83) {
                     alt102 = 1;
                  }

                  switch (alt102) {
                     case 1:
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3880:1: (lv_layoutItems_4_0= ruleLayoutItem )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3880:1: (lv_layoutItems_4_0= ruleLayoutItem )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3881:3: lv_layoutItems_4_0= ruleLayoutItem
                        {

                           newCompositeNode(
                              grammarAccess.getCompositeAccess().getLayoutItemsLayoutItemParserRuleCall_4_0());

                           pushFollow(FOLLOW_ruleLayoutItem_in_ruleComposite8123);
                           lv_layoutItems_4_0 = ruleLayoutItem();

                           state._fsp--;

                           if (current == null) {
                              current = createModelElementForParent(grammarAccess.getCompositeRule());
                           }
                           add(current, "layoutItems", lv_layoutItems_4_0, "LayoutItem");
                           afterParserOrEnumRuleCall();

                        }

                     }
                        break;

                     default:
                        if (cnt102 >= 1) {
                           break loop102;
                        }
                        EarlyExitException eee = new EarlyExitException(102, input);
                        throw eee;
                  }
                  cnt102++;
               } while (true);

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3897:3: (otherlv_5= 'option' ( (lv_options_6_0= ruleCompositeOption ) ) )*
               loop103: do {
                  int alt103 = 2;
                  int LA103_0 = input.LA(1);

                  if (LA103_0 == 55) {
                     alt103 = 1;
                  }

                  switch (alt103) {
                     case 1:
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3897:5: otherlv_5= 'option' ( (lv_options_6_0= ruleCompositeOption ) )
                     {
                        otherlv_5 = (Token) match(input, 55, FOLLOW_55_in_ruleComposite8137);

                        newLeafNode(otherlv_5, grammarAccess.getCompositeAccess().getOptionKeyword_5_0());

                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3901:1: ( (lv_options_6_0= ruleCompositeOption ) )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3902:1: (lv_options_6_0= ruleCompositeOption )
                        {
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3902:1: (lv_options_6_0= ruleCompositeOption )
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3903:3: lv_options_6_0= ruleCompositeOption
                           {

                              newCompositeNode(
                                 grammarAccess.getCompositeAccess().getOptionsCompositeOptionParserRuleCall_5_1_0());

                              pushFollow(FOLLOW_ruleCompositeOption_in_ruleComposite8158);
                              lv_options_6_0 = ruleCompositeOption();

                              state._fsp--;

                              if (current == null) {
                                 current = createModelElementForParent(grammarAccess.getCompositeRule());
                              }
                              add(current, "options", lv_options_6_0, "CompositeOption");
                              afterParserOrEnumRuleCall();

                           }

                        }

                     }
                        break;

                     default:
                        break loop103;
                  }
               } while (true);

               otherlv_7 = (Token) match(input, 22, FOLLOW_22_in_ruleComposite8172);

               newLeafNode(otherlv_7, grammarAccess.getCompositeAccess().getRightCurlyBracketKeyword_6());

            }

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleComposite"

   // $ANTLR start "entryRuleUSER_DEF_OPTION_NAME"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3931:1: entryRuleUSER_DEF_OPTION_NAME returns [String current=null] : iv_ruleUSER_DEF_OPTION_NAME= ruleUSER_DEF_OPTION_NAME EOF ;
   public final String entryRuleUSER_DEF_OPTION_NAME() throws RecognitionException {
      String current = null;

      AntlrDatatypeRuleToken iv_ruleUSER_DEF_OPTION_NAME = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3932:2: (iv_ruleUSER_DEF_OPTION_NAME= ruleUSER_DEF_OPTION_NAME EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3933:2: iv_ruleUSER_DEF_OPTION_NAME= ruleUSER_DEF_OPTION_NAME EOF
         {
            newCompositeNode(grammarAccess.getUSER_DEF_OPTION_NAMERule());
            pushFollow(FOLLOW_ruleUSER_DEF_OPTION_NAME_in_entryRuleUSER_DEF_OPTION_NAME8209);
            iv_ruleUSER_DEF_OPTION_NAME = ruleUSER_DEF_OPTION_NAME();

            state._fsp--;

            current = iv_ruleUSER_DEF_OPTION_NAME.getText();
            match(input, EOF, FOLLOW_EOF_in_entryRuleUSER_DEF_OPTION_NAME8220);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleUSER_DEF_OPTION_NAME"

   // $ANTLR start "ruleUSER_DEF_OPTION_NAME"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3940:1: ruleUSER_DEF_OPTION_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
   public final AntlrDatatypeRuleToken ruleUSER_DEF_OPTION_NAME() throws RecognitionException {
      AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

      Token this_STRING_0 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3943:28: (this_STRING_0= RULE_STRING )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3944:5: this_STRING_0= RULE_STRING
         {
            this_STRING_0 = (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleUSER_DEF_OPTION_NAME8259);

            current.merge(this_STRING_0);

            newLeafNode(this_STRING_0, grammarAccess.getUSER_DEF_OPTION_NAMEAccess().getSTRINGTerminalRuleCall());

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleUSER_DEF_OPTION_NAME"

   // $ANTLR start "entryRuleUserDefOption"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3959:1: entryRuleUserDefOption returns [String current=null] : iv_ruleUserDefOption= ruleUserDefOption EOF ;
   public final String entryRuleUserDefOption() throws RecognitionException {
      String current = null;

      AntlrDatatypeRuleToken iv_ruleUserDefOption = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3960:2: (iv_ruleUserDefOption= ruleUserDefOption EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3961:2: iv_ruleUserDefOption= ruleUserDefOption EOF
         {
            newCompositeNode(grammarAccess.getUserDefOptionRule());
            pushFollow(FOLLOW_ruleUserDefOption_in_entryRuleUserDefOption8304);
            iv_ruleUserDefOption = ruleUserDefOption();

            state._fsp--;

            current = iv_ruleUserDefOption.getText();
            match(input, EOF, FOLLOW_EOF_in_entryRuleUserDefOption8315);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleUserDefOption"

   // $ANTLR start "ruleUserDefOption"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3968:1: ruleUserDefOption returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'GetOrCreate' | this_USER_DEF_OPTION_NAME_1= ruleUSER_DEF_OPTION_NAME ) ;
   public final AntlrDatatypeRuleToken ruleUserDefOption() throws RecognitionException {
      AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

      Token kw = null;
      AntlrDatatypeRuleToken this_USER_DEF_OPTION_NAME_1 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3971:28: ( (kw= 'GetOrCreate' | this_USER_DEF_OPTION_NAME_1= ruleUSER_DEF_OPTION_NAME ) )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3972:1: (kw= 'GetOrCreate' | this_USER_DEF_OPTION_NAME_1= ruleUSER_DEF_OPTION_NAME )
         {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3972:1: (kw= 'GetOrCreate' | this_USER_DEF_OPTION_NAME_1= ruleUSER_DEF_OPTION_NAME )
            int alt104 = 2;
            int LA104_0 = input.LA(1);

            if (LA104_0 == 85) {
               alt104 = 1;
            } else if (LA104_0 == RULE_STRING) {
               alt104 = 2;
            } else {
               NoViableAltException nvae = new NoViableAltException("", 104, 0, input);

               throw nvae;
            }
            switch (alt104) {
               case 1:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3973:2: kw= 'GetOrCreate'
               {
                  kw = (Token) match(input, 85, FOLLOW_85_in_ruleUserDefOption8353);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getUserDefOptionAccess().getGetOrCreateKeyword_0());

               }
                  break;
               case 2:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3980:5: this_USER_DEF_OPTION_NAME_1= ruleUSER_DEF_OPTION_NAME
               {

                  newCompositeNode(grammarAccess.getUserDefOptionAccess().getUSER_DEF_OPTION_NAMEParserRuleCall_1());

                  pushFollow(FOLLOW_ruleUSER_DEF_OPTION_NAME_in_ruleUserDefOption8381);
                  this_USER_DEF_OPTION_NAME_1 = ruleUSER_DEF_OPTION_NAME();

                  state._fsp--;

                  current.merge(this_USER_DEF_OPTION_NAME_1);

                  afterParserOrEnumRuleCall();

               }
                  break;

            }

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleUserDefOption"

   // $ANTLR start "entryRulePROGRAM_DEF_OPTION_NAME"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3998:1: entryRulePROGRAM_DEF_OPTION_NAME returns [String current=null] : iv_rulePROGRAM_DEF_OPTION_NAME= rulePROGRAM_DEF_OPTION_NAME EOF ;
   public final String entryRulePROGRAM_DEF_OPTION_NAME() throws RecognitionException {
      String current = null;

      AntlrDatatypeRuleToken iv_rulePROGRAM_DEF_OPTION_NAME = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3999:2: (iv_rulePROGRAM_DEF_OPTION_NAME= rulePROGRAM_DEF_OPTION_NAME EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4000:2: iv_rulePROGRAM_DEF_OPTION_NAME= rulePROGRAM_DEF_OPTION_NAME EOF
         {
            newCompositeNode(grammarAccess.getPROGRAM_DEF_OPTION_NAMERule());
            pushFollow(FOLLOW_rulePROGRAM_DEF_OPTION_NAME_in_entryRulePROGRAM_DEF_OPTION_NAME8427);
            iv_rulePROGRAM_DEF_OPTION_NAME = rulePROGRAM_DEF_OPTION_NAME();

            state._fsp--;

            current = iv_rulePROGRAM_DEF_OPTION_NAME.getText();
            match(input, EOF, FOLLOW_EOF_in_entryRulePROGRAM_DEF_OPTION_NAME8438);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRulePROGRAM_DEF_OPTION_NAME"

   // $ANTLR start "rulePROGRAM_DEF_OPTION_NAME"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4007:1: rulePROGRAM_DEF_OPTION_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
   public final AntlrDatatypeRuleToken rulePROGRAM_DEF_OPTION_NAME() throws RecognitionException {
      AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

      Token this_STRING_0 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4010:28: (this_STRING_0= RULE_STRING )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4011:5: this_STRING_0= RULE_STRING
         {
            this_STRING_0 = (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_rulePROGRAM_DEF_OPTION_NAME8477);

            current.merge(this_STRING_0);

            newLeafNode(this_STRING_0, grammarAccess.getPROGRAM_DEF_OPTION_NAMEAccess().getSTRINGTerminalRuleCall());

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "rulePROGRAM_DEF_OPTION_NAME"

   // $ANTLR start "entryRuleProgramDefOption"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4026:1: entryRuleProgramDefOption returns [String current=null] : iv_ruleProgramDefOption= ruleProgramDefOption EOF ;
   public final String entryRuleProgramDefOption() throws RecognitionException {
      String current = null;

      AntlrDatatypeRuleToken iv_ruleProgramDefOption = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4027:2: (iv_ruleProgramDefOption= ruleProgramDefOption EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4028:2: iv_ruleProgramDefOption= ruleProgramDefOption EOF
         {
            newCompositeNode(grammarAccess.getProgramDefOptionRule());
            pushFollow(FOLLOW_ruleProgramDefOption_in_entryRuleProgramDefOption8522);
            iv_ruleProgramDefOption = ruleProgramDefOption();

            state._fsp--;

            current = iv_ruleProgramDefOption.getText();
            match(input, EOF, FOLLOW_EOF_in_entryRuleProgramDefOption8533);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleProgramDefOption"

   // $ANTLR start "ruleProgramDefOption"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4035:1: ruleProgramDefOption returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'GetOrCreate' | this_PROGRAM_DEF_OPTION_NAME_1= rulePROGRAM_DEF_OPTION_NAME ) ;
   public final AntlrDatatypeRuleToken ruleProgramDefOption() throws RecognitionException {
      AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

      Token kw = null;
      AntlrDatatypeRuleToken this_PROGRAM_DEF_OPTION_NAME_1 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4038:28: ( (kw= 'GetOrCreate' | this_PROGRAM_DEF_OPTION_NAME_1= rulePROGRAM_DEF_OPTION_NAME ) )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4039:1: (kw= 'GetOrCreate' | this_PROGRAM_DEF_OPTION_NAME_1= rulePROGRAM_DEF_OPTION_NAME )
         {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4039:1: (kw= 'GetOrCreate' | this_PROGRAM_DEF_OPTION_NAME_1= rulePROGRAM_DEF_OPTION_NAME )
            int alt105 = 2;
            int LA105_0 = input.LA(1);

            if (LA105_0 == 85) {
               alt105 = 1;
            } else if (LA105_0 == RULE_STRING) {
               alt105 = 2;
            } else {
               NoViableAltException nvae = new NoViableAltException("", 105, 0, input);

               throw nvae;
            }
            switch (alt105) {
               case 1:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4040:2: kw= 'GetOrCreate'
               {
                  kw = (Token) match(input, 85, FOLLOW_85_in_ruleProgramDefOption8571);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getProgramDefOptionAccess().getGetOrCreateKeyword_0());

               }
                  break;
               case 2:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4047:5: this_PROGRAM_DEF_OPTION_NAME_1= rulePROGRAM_DEF_OPTION_NAME
               {

                  newCompositeNode(
                     grammarAccess.getProgramDefOptionAccess().getPROGRAM_DEF_OPTION_NAMEParserRuleCall_1());

                  pushFollow(FOLLOW_rulePROGRAM_DEF_OPTION_NAME_in_ruleProgramDefOption8599);
                  this_PROGRAM_DEF_OPTION_NAME_1 = rulePROGRAM_DEF_OPTION_NAME();

                  state._fsp--;

                  current.merge(this_PROGRAM_DEF_OPTION_NAME_1);

                  afterParserOrEnumRuleCall();

               }
                  break;

            }

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleProgramDefOption"

   // $ANTLR start "entryRuleTEAM_DEF_OPTION_NAME"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4065:1: entryRuleTEAM_DEF_OPTION_NAME returns [String current=null] : iv_ruleTEAM_DEF_OPTION_NAME= ruleTEAM_DEF_OPTION_NAME EOF ;
   public final String entryRuleTEAM_DEF_OPTION_NAME() throws RecognitionException {
      String current = null;

      AntlrDatatypeRuleToken iv_ruleTEAM_DEF_OPTION_NAME = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4066:2: (iv_ruleTEAM_DEF_OPTION_NAME= ruleTEAM_DEF_OPTION_NAME EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4067:2: iv_ruleTEAM_DEF_OPTION_NAME= ruleTEAM_DEF_OPTION_NAME EOF
         {
            newCompositeNode(grammarAccess.getTEAM_DEF_OPTION_NAMERule());
            pushFollow(FOLLOW_ruleTEAM_DEF_OPTION_NAME_in_entryRuleTEAM_DEF_OPTION_NAME8645);
            iv_ruleTEAM_DEF_OPTION_NAME = ruleTEAM_DEF_OPTION_NAME();

            state._fsp--;

            current = iv_ruleTEAM_DEF_OPTION_NAME.getText();
            match(input, EOF, FOLLOW_EOF_in_entryRuleTEAM_DEF_OPTION_NAME8656);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleTEAM_DEF_OPTION_NAME"

   // $ANTLR start "ruleTEAM_DEF_OPTION_NAME"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4074:1: ruleTEAM_DEF_OPTION_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
   public final AntlrDatatypeRuleToken ruleTEAM_DEF_OPTION_NAME() throws RecognitionException {
      AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

      Token this_STRING_0 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4077:28: (this_STRING_0= RULE_STRING )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4078:5: this_STRING_0= RULE_STRING
         {
            this_STRING_0 = (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleTEAM_DEF_OPTION_NAME8695);

            current.merge(this_STRING_0);

            newLeafNode(this_STRING_0, grammarAccess.getTEAM_DEF_OPTION_NAMEAccess().getSTRINGTerminalRuleCall());

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleTEAM_DEF_OPTION_NAME"

   // $ANTLR start "entryRuleTeamDefOption"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4093:1: entryRuleTeamDefOption returns [String current=null] : iv_ruleTeamDefOption= ruleTeamDefOption EOF ;
   public final String entryRuleTeamDefOption() throws RecognitionException {
      String current = null;

      AntlrDatatypeRuleToken iv_ruleTeamDefOption = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4094:2: (iv_ruleTeamDefOption= ruleTeamDefOption EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4095:2: iv_ruleTeamDefOption= ruleTeamDefOption EOF
         {
            newCompositeNode(grammarAccess.getTeamDefOptionRule());
            pushFollow(FOLLOW_ruleTeamDefOption_in_entryRuleTeamDefOption8740);
            iv_ruleTeamDefOption = ruleTeamDefOption();

            state._fsp--;

            current = iv_ruleTeamDefOption.getText();
            match(input, EOF, FOLLOW_EOF_in_entryRuleTeamDefOption8751);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleTeamDefOption"

   // $ANTLR start "ruleTeamDefOption"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4102:1: ruleTeamDefOption returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'GetOrCreate' | this_TEAM_DEF_OPTION_NAME_1= ruleTEAM_DEF_OPTION_NAME ) ;
   public final AntlrDatatypeRuleToken ruleTeamDefOption() throws RecognitionException {
      AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

      Token kw = null;
      AntlrDatatypeRuleToken this_TEAM_DEF_OPTION_NAME_1 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4105:28: ( (kw= 'GetOrCreate' | this_TEAM_DEF_OPTION_NAME_1= ruleTEAM_DEF_OPTION_NAME ) )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4106:1: (kw= 'GetOrCreate' | this_TEAM_DEF_OPTION_NAME_1= ruleTEAM_DEF_OPTION_NAME )
         {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4106:1: (kw= 'GetOrCreate' | this_TEAM_DEF_OPTION_NAME_1= ruleTEAM_DEF_OPTION_NAME )
            int alt106 = 2;
            int LA106_0 = input.LA(1);

            if (LA106_0 == 85) {
               alt106 = 1;
            } else if (LA106_0 == RULE_STRING) {
               alt106 = 2;
            } else {
               NoViableAltException nvae = new NoViableAltException("", 106, 0, input);

               throw nvae;
            }
            switch (alt106) {
               case 1:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4107:2: kw= 'GetOrCreate'
               {
                  kw = (Token) match(input, 85, FOLLOW_85_in_ruleTeamDefOption8789);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getTeamDefOptionAccess().getGetOrCreateKeyword_0());

               }
                  break;
               case 2:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4114:5: this_TEAM_DEF_OPTION_NAME_1= ruleTEAM_DEF_OPTION_NAME
               {

                  newCompositeNode(grammarAccess.getTeamDefOptionAccess().getTEAM_DEF_OPTION_NAMEParserRuleCall_1());

                  pushFollow(FOLLOW_ruleTEAM_DEF_OPTION_NAME_in_ruleTeamDefOption8817);
                  this_TEAM_DEF_OPTION_NAME_1 = ruleTEAM_DEF_OPTION_NAME();

                  state._fsp--;

                  current.merge(this_TEAM_DEF_OPTION_NAME_1);

                  afterParserOrEnumRuleCall();

               }
                  break;

            }

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleTeamDefOption"

   // $ANTLR start "entryRuleAI_DEF_OPTION_NAME"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4132:1: entryRuleAI_DEF_OPTION_NAME returns [String current=null] : iv_ruleAI_DEF_OPTION_NAME= ruleAI_DEF_OPTION_NAME EOF ;
   public final String entryRuleAI_DEF_OPTION_NAME() throws RecognitionException {
      String current = null;

      AntlrDatatypeRuleToken iv_ruleAI_DEF_OPTION_NAME = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4133:2: (iv_ruleAI_DEF_OPTION_NAME= ruleAI_DEF_OPTION_NAME EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4134:2: iv_ruleAI_DEF_OPTION_NAME= ruleAI_DEF_OPTION_NAME EOF
         {
            newCompositeNode(grammarAccess.getAI_DEF_OPTION_NAMERule());
            pushFollow(FOLLOW_ruleAI_DEF_OPTION_NAME_in_entryRuleAI_DEF_OPTION_NAME8863);
            iv_ruleAI_DEF_OPTION_NAME = ruleAI_DEF_OPTION_NAME();

            state._fsp--;

            current = iv_ruleAI_DEF_OPTION_NAME.getText();
            match(input, EOF, FOLLOW_EOF_in_entryRuleAI_DEF_OPTION_NAME8874);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleAI_DEF_OPTION_NAME"

   // $ANTLR start "ruleAI_DEF_OPTION_NAME"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4141:1: ruleAI_DEF_OPTION_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
   public final AntlrDatatypeRuleToken ruleAI_DEF_OPTION_NAME() throws RecognitionException {
      AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

      Token this_STRING_0 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4144:28: (this_STRING_0= RULE_STRING )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4145:5: this_STRING_0= RULE_STRING
         {
            this_STRING_0 = (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleAI_DEF_OPTION_NAME8913);

            current.merge(this_STRING_0);

            newLeafNode(this_STRING_0, grammarAccess.getAI_DEF_OPTION_NAMEAccess().getSTRINGTerminalRuleCall());

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleAI_DEF_OPTION_NAME"

   // $ANTLR start "entryRuleActionableItemOption"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4160:1: entryRuleActionableItemOption returns [String current=null] : iv_ruleActionableItemOption= ruleActionableItemOption EOF ;
   public final String entryRuleActionableItemOption() throws RecognitionException {
      String current = null;

      AntlrDatatypeRuleToken iv_ruleActionableItemOption = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4161:2: (iv_ruleActionableItemOption= ruleActionableItemOption EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4162:2: iv_ruleActionableItemOption= ruleActionableItemOption EOF
         {
            newCompositeNode(grammarAccess.getActionableItemOptionRule());
            pushFollow(FOLLOW_ruleActionableItemOption_in_entryRuleActionableItemOption8958);
            iv_ruleActionableItemOption = ruleActionableItemOption();

            state._fsp--;

            current = iv_ruleActionableItemOption.getText();
            match(input, EOF, FOLLOW_EOF_in_entryRuleActionableItemOption8969);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleActionableItemOption"

   // $ANTLR start "ruleActionableItemOption"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4169:1: ruleActionableItemOption returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'GetOrCreate' | this_AI_DEF_OPTION_NAME_1= ruleAI_DEF_OPTION_NAME ) ;
   public final AntlrDatatypeRuleToken ruleActionableItemOption() throws RecognitionException {
      AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

      Token kw = null;
      AntlrDatatypeRuleToken this_AI_DEF_OPTION_NAME_1 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4172:28: ( (kw= 'GetOrCreate' | this_AI_DEF_OPTION_NAME_1= ruleAI_DEF_OPTION_NAME ) )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4173:1: (kw= 'GetOrCreate' | this_AI_DEF_OPTION_NAME_1= ruleAI_DEF_OPTION_NAME )
         {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4173:1: (kw= 'GetOrCreate' | this_AI_DEF_OPTION_NAME_1= ruleAI_DEF_OPTION_NAME )
            int alt107 = 2;
            int LA107_0 = input.LA(1);

            if (LA107_0 == 85) {
               alt107 = 1;
            } else if (LA107_0 == RULE_STRING) {
               alt107 = 2;
            } else {
               NoViableAltException nvae = new NoViableAltException("", 107, 0, input);

               throw nvae;
            }
            switch (alt107) {
               case 1:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4174:2: kw= 'GetOrCreate'
               {
                  kw = (Token) match(input, 85, FOLLOW_85_in_ruleActionableItemOption9007);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getActionableItemOptionAccess().getGetOrCreateKeyword_0());

               }
                  break;
               case 2:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4181:5: this_AI_DEF_OPTION_NAME_1= ruleAI_DEF_OPTION_NAME
               {

                  newCompositeNode(
                     grammarAccess.getActionableItemOptionAccess().getAI_DEF_OPTION_NAMEParserRuleCall_1());

                  pushFollow(FOLLOW_ruleAI_DEF_OPTION_NAME_in_ruleActionableItemOption9035);
                  this_AI_DEF_OPTION_NAME_1 = ruleAI_DEF_OPTION_NAME();

                  state._fsp--;

                  current.merge(this_AI_DEF_OPTION_NAME_1);

                  afterParserOrEnumRuleCall();

               }
                  break;

            }

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleActionableItemOption"

   // $ANTLR start "entryRuleCOMPOSITE_OPTION_NAME"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4199:1: entryRuleCOMPOSITE_OPTION_NAME returns [String current=null] : iv_ruleCOMPOSITE_OPTION_NAME= ruleCOMPOSITE_OPTION_NAME EOF ;
   public final String entryRuleCOMPOSITE_OPTION_NAME() throws RecognitionException {
      String current = null;

      AntlrDatatypeRuleToken iv_ruleCOMPOSITE_OPTION_NAME = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4200:2: (iv_ruleCOMPOSITE_OPTION_NAME= ruleCOMPOSITE_OPTION_NAME EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4201:2: iv_ruleCOMPOSITE_OPTION_NAME= ruleCOMPOSITE_OPTION_NAME EOF
         {
            newCompositeNode(grammarAccess.getCOMPOSITE_OPTION_NAMERule());
            pushFollow(FOLLOW_ruleCOMPOSITE_OPTION_NAME_in_entryRuleCOMPOSITE_OPTION_NAME9081);
            iv_ruleCOMPOSITE_OPTION_NAME = ruleCOMPOSITE_OPTION_NAME();

            state._fsp--;

            current = iv_ruleCOMPOSITE_OPTION_NAME.getText();
            match(input, EOF, FOLLOW_EOF_in_entryRuleCOMPOSITE_OPTION_NAME9092);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleCOMPOSITE_OPTION_NAME"

   // $ANTLR start "ruleCOMPOSITE_OPTION_NAME"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4208:1: ruleCOMPOSITE_OPTION_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
   public final AntlrDatatypeRuleToken ruleCOMPOSITE_OPTION_NAME() throws RecognitionException {
      AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

      Token this_STRING_0 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4211:28: (this_STRING_0= RULE_STRING )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4212:5: this_STRING_0= RULE_STRING
         {
            this_STRING_0 = (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleCOMPOSITE_OPTION_NAME9131);

            current.merge(this_STRING_0);

            newLeafNode(this_STRING_0, grammarAccess.getCOMPOSITE_OPTION_NAMEAccess().getSTRINGTerminalRuleCall());

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleCOMPOSITE_OPTION_NAME"

   // $ANTLR start "entryRuleCompositeOption"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4227:1: entryRuleCompositeOption returns [String current=null] : iv_ruleCompositeOption= ruleCompositeOption EOF ;
   public final String entryRuleCompositeOption() throws RecognitionException {
      String current = null;

      AntlrDatatypeRuleToken iv_ruleCompositeOption = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4228:2: (iv_ruleCompositeOption= ruleCompositeOption EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4229:2: iv_ruleCompositeOption= ruleCompositeOption EOF
         {
            newCompositeNode(grammarAccess.getCompositeOptionRule());
            pushFollow(FOLLOW_ruleCompositeOption_in_entryRuleCompositeOption9176);
            iv_ruleCompositeOption = ruleCompositeOption();

            state._fsp--;

            current = iv_ruleCompositeOption.getText();
            match(input, EOF, FOLLOW_EOF_in_entryRuleCompositeOption9187);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleCompositeOption"

   // $ANTLR start "ruleCompositeOption"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4236:1: ruleCompositeOption returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'None' | this_COMPOSITE_OPTION_NAME_1= ruleCOMPOSITE_OPTION_NAME ) ;
   public final AntlrDatatypeRuleToken ruleCompositeOption() throws RecognitionException {
      AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

      Token kw = null;
      AntlrDatatypeRuleToken this_COMPOSITE_OPTION_NAME_1 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4239:28: ( (kw= 'None' | this_COMPOSITE_OPTION_NAME_1= ruleCOMPOSITE_OPTION_NAME ) )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4240:1: (kw= 'None' | this_COMPOSITE_OPTION_NAME_1= ruleCOMPOSITE_OPTION_NAME )
         {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4240:1: (kw= 'None' | this_COMPOSITE_OPTION_NAME_1= ruleCOMPOSITE_OPTION_NAME )
            int alt108 = 2;
            int LA108_0 = input.LA(1);

            if (LA108_0 == 86) {
               alt108 = 1;
            } else if (LA108_0 == RULE_STRING) {
               alt108 = 2;
            } else {
               NoViableAltException nvae = new NoViableAltException("", 108, 0, input);

               throw nvae;
            }
            switch (alt108) {
               case 1:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4241:2: kw= 'None'
               {
                  kw = (Token) match(input, 86, FOLLOW_86_in_ruleCompositeOption9225);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getCompositeOptionAccess().getNoneKeyword_0());

               }
                  break;
               case 2:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4248:5: this_COMPOSITE_OPTION_NAME_1= ruleCOMPOSITE_OPTION_NAME
               {

                  newCompositeNode(grammarAccess.getCompositeOptionAccess().getCOMPOSITE_OPTION_NAMEParserRuleCall_1());

                  pushFollow(FOLLOW_ruleCOMPOSITE_OPTION_NAME_in_ruleCompositeOption9253);
                  this_COMPOSITE_OPTION_NAME_1 = ruleCOMPOSITE_OPTION_NAME();

                  state._fsp--;

                  current.merge(this_COMPOSITE_OPTION_NAME_1);

                  afterParserOrEnumRuleCall();

               }
                  break;

            }

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleCompositeOption"

   // $ANTLR start "entryRuleTRANSITION_OPTION_NAME"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4266:1: entryRuleTRANSITION_OPTION_NAME returns [String current=null] : iv_ruleTRANSITION_OPTION_NAME= ruleTRANSITION_OPTION_NAME EOF ;
   public final String entryRuleTRANSITION_OPTION_NAME() throws RecognitionException {
      String current = null;

      AntlrDatatypeRuleToken iv_ruleTRANSITION_OPTION_NAME = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4267:2: (iv_ruleTRANSITION_OPTION_NAME= ruleTRANSITION_OPTION_NAME EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4268:2: iv_ruleTRANSITION_OPTION_NAME= ruleTRANSITION_OPTION_NAME EOF
         {
            newCompositeNode(grammarAccess.getTRANSITION_OPTION_NAMERule());
            pushFollow(FOLLOW_ruleTRANSITION_OPTION_NAME_in_entryRuleTRANSITION_OPTION_NAME9299);
            iv_ruleTRANSITION_OPTION_NAME = ruleTRANSITION_OPTION_NAME();

            state._fsp--;

            current = iv_ruleTRANSITION_OPTION_NAME.getText();
            match(input, EOF, FOLLOW_EOF_in_entryRuleTRANSITION_OPTION_NAME9310);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleTRANSITION_OPTION_NAME"

   // $ANTLR start "ruleTRANSITION_OPTION_NAME"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4275:1: ruleTRANSITION_OPTION_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
   public final AntlrDatatypeRuleToken ruleTRANSITION_OPTION_NAME() throws RecognitionException {
      AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

      Token this_STRING_0 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4278:28: (this_STRING_0= RULE_STRING )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4279:5: this_STRING_0= RULE_STRING
         {
            this_STRING_0 = (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleTRANSITION_OPTION_NAME9349);

            current.merge(this_STRING_0);

            newLeafNode(this_STRING_0, grammarAccess.getTRANSITION_OPTION_NAMEAccess().getSTRINGTerminalRuleCall());

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleTRANSITION_OPTION_NAME"

   // $ANTLR start "entryRuleTransitionOption"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4294:1: entryRuleTransitionOption returns [String current=null] : iv_ruleTransitionOption= ruleTransitionOption EOF ;
   public final String entryRuleTransitionOption() throws RecognitionException {
      String current = null;

      AntlrDatatypeRuleToken iv_ruleTransitionOption = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4295:2: (iv_ruleTransitionOption= ruleTransitionOption EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4296:2: iv_ruleTransitionOption= ruleTransitionOption EOF
         {
            newCompositeNode(grammarAccess.getTransitionOptionRule());
            pushFollow(FOLLOW_ruleTransitionOption_in_entryRuleTransitionOption9394);
            iv_ruleTransitionOption = ruleTransitionOption();

            state._fsp--;

            current = iv_ruleTransitionOption.getText();
            match(input, EOF, FOLLOW_EOF_in_entryRuleTransitionOption9405);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleTransitionOption"

   // $ANTLR start "ruleTransitionOption"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4303:1: ruleTransitionOption returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'AsDefault' | kw= 'OverrideAttributeValidation' | this_TRANSITION_OPTION_NAME_2= ruleTRANSITION_OPTION_NAME ) ;
   public final AntlrDatatypeRuleToken ruleTransitionOption() throws RecognitionException {
      AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

      Token kw = null;
      AntlrDatatypeRuleToken this_TRANSITION_OPTION_NAME_2 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4306:28: ( (kw= 'AsDefault' | kw= 'OverrideAttributeValidation' | this_TRANSITION_OPTION_NAME_2= ruleTRANSITION_OPTION_NAME ) )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4307:1: (kw= 'AsDefault' | kw= 'OverrideAttributeValidation' | this_TRANSITION_OPTION_NAME_2= ruleTRANSITION_OPTION_NAME )
         {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4307:1: (kw= 'AsDefault' | kw= 'OverrideAttributeValidation' | this_TRANSITION_OPTION_NAME_2= ruleTRANSITION_OPTION_NAME )
            int alt109 = 3;
            switch (input.LA(1)) {
               case 87: {
                  alt109 = 1;
               }
                  break;
               case 88: {
                  alt109 = 2;
               }
                  break;
               case RULE_STRING: {
                  alt109 = 3;
               }
                  break;
               default:
                  NoViableAltException nvae = new NoViableAltException("", 109, 0, input);

                  throw nvae;
            }

            switch (alt109) {
               case 1:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4308:2: kw= 'AsDefault'
               {
                  kw = (Token) match(input, 87, FOLLOW_87_in_ruleTransitionOption9443);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getTransitionOptionAccess().getAsDefaultKeyword_0());

               }
                  break;
               case 2:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4315:2: kw= 'OverrideAttributeValidation'
               {
                  kw = (Token) match(input, 88, FOLLOW_88_in_ruleTransitionOption9462);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getTransitionOptionAccess().getOverrideAttributeValidationKeyword_1());

               }
                  break;
               case 3:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4322:5: this_TRANSITION_OPTION_NAME_2= ruleTRANSITION_OPTION_NAME
               {

                  newCompositeNode(
                     grammarAccess.getTransitionOptionAccess().getTRANSITION_OPTION_NAMEParserRuleCall_2());

                  pushFollow(FOLLOW_ruleTRANSITION_OPTION_NAME_in_ruleTransitionOption9490);
                  this_TRANSITION_OPTION_NAME_2 = ruleTRANSITION_OPTION_NAME();

                  state._fsp--;

                  current.merge(this_TRANSITION_OPTION_NAME_2);

                  afterParserOrEnumRuleCall();

               }
                  break;

            }

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleTransitionOption"

   // $ANTLR start "entryRuleRULE_NAME_REFERENCE"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4340:1: entryRuleRULE_NAME_REFERENCE returns [String current=null] : iv_ruleRULE_NAME_REFERENCE= ruleRULE_NAME_REFERENCE EOF ;
   public final String entryRuleRULE_NAME_REFERENCE() throws RecognitionException {
      String current = null;

      AntlrDatatypeRuleToken iv_ruleRULE_NAME_REFERENCE = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4341:2: (iv_ruleRULE_NAME_REFERENCE= ruleRULE_NAME_REFERENCE EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4342:2: iv_ruleRULE_NAME_REFERENCE= ruleRULE_NAME_REFERENCE EOF
         {
            newCompositeNode(grammarAccess.getRULE_NAME_REFERENCERule());
            pushFollow(FOLLOW_ruleRULE_NAME_REFERENCE_in_entryRuleRULE_NAME_REFERENCE9536);
            iv_ruleRULE_NAME_REFERENCE = ruleRULE_NAME_REFERENCE();

            state._fsp--;

            current = iv_ruleRULE_NAME_REFERENCE.getText();
            match(input, EOF, FOLLOW_EOF_in_entryRuleRULE_NAME_REFERENCE9547);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleRULE_NAME_REFERENCE"

   // $ANTLR start "ruleRULE_NAME_REFERENCE"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4349:1: ruleRULE_NAME_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
   public final AntlrDatatypeRuleToken ruleRULE_NAME_REFERENCE() throws RecognitionException {
      AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

      Token this_STRING_0 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4352:28: (this_STRING_0= RULE_STRING )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4353:5: this_STRING_0= RULE_STRING
         {
            this_STRING_0 = (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleRULE_NAME_REFERENCE9586);

            current.merge(this_STRING_0);

            newLeafNode(this_STRING_0, grammarAccess.getRULE_NAME_REFERENCEAccess().getSTRINGTerminalRuleCall());

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleRULE_NAME_REFERENCE"

   // $ANTLR start "entryRuleRuleDef"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4368:1: entryRuleRuleDef returns [EObject current=null] : iv_ruleRuleDef= ruleRuleDef EOF ;
   public final EObject entryRuleRuleDef() throws RecognitionException {
      EObject current = null;

      EObject iv_ruleRuleDef = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4369:2: (iv_ruleRuleDef= ruleRuleDef EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4370:2: iv_ruleRuleDef= ruleRuleDef EOF
         {
            newCompositeNode(grammarAccess.getRuleDefRule());
            pushFollow(FOLLOW_ruleRuleDef_in_entryRuleRuleDef9630);
            iv_ruleRuleDef = ruleRuleDef();

            state._fsp--;

            current = iv_ruleRuleDef;
            match(input, EOF, FOLLOW_EOF_in_entryRuleRuleDef9640);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleRuleDef"

   // $ANTLR start "ruleRuleDef"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4377:1: ruleRuleDef returns [EObject current=null] : ( (otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) ) (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) ) )+ otherlv_9= '}' ) ;
   public final EObject ruleRuleDef() throws RecognitionException {
      EObject current = null;

      Token otherlv_0 = null;
      Token otherlv_2 = null;
      Token otherlv_3 = null;
      Token lv_title_4_0 = null;
      Token otherlv_5 = null;
      Token lv_description_6_0 = null;
      Token otherlv_7 = null;
      Token otherlv_9 = null;
      AntlrDatatypeRuleToken lv_name_1_0 = null;

      Enumerator lv_ruleLocation_8_0 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4380:28: ( ( (otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) ) (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) ) )+ otherlv_9= '}' ) )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4381:1: ( (otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) ) (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) ) )+ otherlv_9= '}' )
         {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4381:1: ( (otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) ) (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) ) )+ otherlv_9= '}' )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4381:2: (otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) ) (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) ) )+ otherlv_9= '}'
            {
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4381:2: (otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) ) )
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4381:4: otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) )
               {
                  otherlv_0 = (Token) match(input, 89, FOLLOW_89_in_ruleRuleDef9678);

                  newLeafNode(otherlv_0, grammarAccess.getRuleDefAccess().getNameKeyword_0_0());

                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4385:1: ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4386:1: (lv_name_1_0= ruleRULE_NAME_REFERENCE )
                  {
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4386:1: (lv_name_1_0= ruleRULE_NAME_REFERENCE )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4387:3: lv_name_1_0= ruleRULE_NAME_REFERENCE
                     {

                        newCompositeNode(
                           grammarAccess.getRuleDefAccess().getNameRULE_NAME_REFERENCEParserRuleCall_0_1_0());

                        pushFollow(FOLLOW_ruleRULE_NAME_REFERENCE_in_ruleRuleDef9699);
                        lv_name_1_0 = ruleRULE_NAME_REFERENCE();

                        state._fsp--;

                        if (current == null) {
                           current = createModelElementForParent(grammarAccess.getRuleDefRule());
                        }
                        set(current, "name", lv_name_1_0, "RULE_NAME_REFERENCE");
                        afterParserOrEnumRuleCall();

                     }

                  }

               }

               otherlv_2 = (Token) match(input, 17, FOLLOW_17_in_ruleRuleDef9712);

               newLeafNode(otherlv_2, grammarAccess.getRuleDefAccess().getLeftCurlyBracketKeyword_1());

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4407:1: (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) )
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4407:3: otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) )
               {
                  otherlv_3 = (Token) match(input, 69, FOLLOW_69_in_ruleRuleDef9725);

                  newLeafNode(otherlv_3, grammarAccess.getRuleDefAccess().getTitleKeyword_2_0());

                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4411:1: ( (lv_title_4_0= RULE_STRING ) )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4412:1: (lv_title_4_0= RULE_STRING )
                  {
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4412:1: (lv_title_4_0= RULE_STRING )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4413:3: lv_title_4_0= RULE_STRING
                     {
                        lv_title_4_0 = (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleRuleDef9742);

                        newLeafNode(lv_title_4_0,
                           grammarAccess.getRuleDefAccess().getTitleSTRINGTerminalRuleCall_2_1_0());

                        if (current == null) {
                           current = createModelElement(grammarAccess.getRuleDefRule());
                        }
                        setWithLastConsumed(current, "title", lv_title_4_0, "STRING");

                     }

                  }

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4429:3: (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )?
               int alt110 = 2;
               int LA110_0 = input.LA(1);

               if (LA110_0 == 51) {
                  alt110 = 1;
               }
               switch (alt110) {
                  case 1:
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4429:5: otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) )
                  {
                     otherlv_5 = (Token) match(input, 51, FOLLOW_51_in_ruleRuleDef9761);

                     newLeafNode(otherlv_5, grammarAccess.getRuleDefAccess().getDescriptionKeyword_3_0());

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4433:1: ( (lv_description_6_0= RULE_STRING ) )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4434:1: (lv_description_6_0= RULE_STRING )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4434:1: (lv_description_6_0= RULE_STRING )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4435:3: lv_description_6_0= RULE_STRING
                        {
                           lv_description_6_0 =
                              (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleRuleDef9778);

                           newLeafNode(lv_description_6_0,
                              grammarAccess.getRuleDefAccess().getDescriptionSTRINGTerminalRuleCall_3_1_0());

                           if (current == null) {
                              current = createModelElement(grammarAccess.getRuleDefRule());
                           }
                           setWithLastConsumed(current, "description", lv_description_6_0, "STRING");

                        }

                     }

                  }
                     break;

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4451:4: (otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) ) )+
               int cnt111 = 0;
               loop111: do {
                  int alt111 = 2;
                  int LA111_0 = input.LA(1);

                  if (LA111_0 == 90) {
                     alt111 = 1;
                  }

                  switch (alt111) {
                     case 1:
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4451:6: otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) )
                     {
                        otherlv_7 = (Token) match(input, 90, FOLLOW_90_in_ruleRuleDef9798);

                        newLeafNode(otherlv_7, grammarAccess.getRuleDefAccess().getRuleLocationKeyword_4_0());

                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4455:1: ( (lv_ruleLocation_8_0= ruleRuleLocation ) )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4456:1: (lv_ruleLocation_8_0= ruleRuleLocation )
                        {
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4456:1: (lv_ruleLocation_8_0= ruleRuleLocation )
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4457:3: lv_ruleLocation_8_0= ruleRuleLocation
                           {

                              newCompositeNode(
                                 grammarAccess.getRuleDefAccess().getRuleLocationRuleLocationEnumRuleCall_4_1_0());

                              pushFollow(FOLLOW_ruleRuleLocation_in_ruleRuleDef9819);
                              lv_ruleLocation_8_0 = ruleRuleLocation();

                              state._fsp--;

                              if (current == null) {
                                 current = createModelElementForParent(grammarAccess.getRuleDefRule());
                              }
                              add(current, "ruleLocation", lv_ruleLocation_8_0, "RuleLocation");
                              afterParserOrEnumRuleCall();

                           }

                        }

                     }
                        break;

                     default:
                        if (cnt111 >= 1) {
                           break loop111;
                        }
                        EarlyExitException eee = new EarlyExitException(111, input);
                        throw eee;
                  }
                  cnt111++;
               } while (true);

               otherlv_9 = (Token) match(input, 22, FOLLOW_22_in_ruleRuleDef9833);

               newLeafNode(otherlv_9, grammarAccess.getRuleDefAccess().getRightCurlyBracketKeyword_5());

            }

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleRuleDef"

   // $ANTLR start "entryRuleCreateTaskRuleDef"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4485:1: entryRuleCreateTaskRuleDef returns [EObject current=null] : iv_ruleCreateTaskRuleDef= ruleCreateTaskRuleDef EOF ;
   public final EObject entryRuleCreateTaskRuleDef() throws RecognitionException {
      EObject current = null;

      EObject iv_ruleCreateTaskRuleDef = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4486:2: (iv_ruleCreateTaskRuleDef= ruleCreateTaskRuleDef EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4487:2: iv_ruleCreateTaskRuleDef= ruleCreateTaskRuleDef EOF
         {
            newCompositeNode(grammarAccess.getCreateTaskRuleDefRule());
            pushFollow(FOLLOW_ruleCreateTaskRuleDef_in_entryRuleCreateTaskRuleDef9869);
            iv_ruleCreateTaskRuleDef = ruleCreateTaskRuleDef();

            state._fsp--;

            current = iv_ruleCreateTaskRuleDef;
            match(input, EOF, FOLLOW_EOF_in_entryRuleCreateTaskRuleDef9879);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleCreateTaskRuleDef"

   // $ANTLR start "ruleCreateTaskRuleDef"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4494:1: ruleCreateTaskRuleDef returns [EObject current=null] : ( (otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) ) (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) ) )+ (otherlv_9= 'assignees' ( (lv_assignees_10_0= ruleUserDef ) ) )* (otherlv_11= 'relatedState' ( (lv_relatedState_12_0= ruleSTATE_NAME_REFERENCE ) ) )? (otherlv_13= 'taskWorkDef' ( (lv_taskWorkDef_14_0= RULE_STRING ) ) )? (otherlv_15= 'onEvent' ( (lv_onEvent_16_0= ruleOnEventType ) ) )* (otherlv_17= 'attribute' ( (lv_attributes_18_0= ruleAttrDef ) ) )* otherlv_19= '}' ) ;
   public final EObject ruleCreateTaskRuleDef() throws RecognitionException {
      EObject current = null;

      Token otherlv_0 = null;
      Token otherlv_2 = null;
      Token otherlv_3 = null;
      Token lv_title_4_0 = null;
      Token otherlv_5 = null;
      Token lv_description_6_0 = null;
      Token otherlv_7 = null;
      Token otherlv_9 = null;
      Token otherlv_11 = null;
      Token otherlv_13 = null;
      Token lv_taskWorkDef_14_0 = null;
      Token otherlv_15 = null;
      Token otherlv_17 = null;
      Token otherlv_19 = null;
      AntlrDatatypeRuleToken lv_name_1_0 = null;

      Enumerator lv_ruleLocation_8_0 = null;

      EObject lv_assignees_10_0 = null;

      AntlrDatatypeRuleToken lv_relatedState_12_0 = null;

      Enumerator lv_onEvent_16_0 = null;

      EObject lv_attributes_18_0 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4497:28: ( ( (otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) ) (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) ) )+ (otherlv_9= 'assignees' ( (lv_assignees_10_0= ruleUserDef ) ) )* (otherlv_11= 'relatedState' ( (lv_relatedState_12_0= ruleSTATE_NAME_REFERENCE ) ) )? (otherlv_13= 'taskWorkDef' ( (lv_taskWorkDef_14_0= RULE_STRING ) ) )? (otherlv_15= 'onEvent' ( (lv_onEvent_16_0= ruleOnEventType ) ) )* (otherlv_17= 'attribute' ( (lv_attributes_18_0= ruleAttrDef ) ) )* otherlv_19= '}' ) )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4498:1: ( (otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) ) (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) ) )+ (otherlv_9= 'assignees' ( (lv_assignees_10_0= ruleUserDef ) ) )* (otherlv_11= 'relatedState' ( (lv_relatedState_12_0= ruleSTATE_NAME_REFERENCE ) ) )? (otherlv_13= 'taskWorkDef' ( (lv_taskWorkDef_14_0= RULE_STRING ) ) )? (otherlv_15= 'onEvent' ( (lv_onEvent_16_0= ruleOnEventType ) ) )* (otherlv_17= 'attribute' ( (lv_attributes_18_0= ruleAttrDef ) ) )* otherlv_19= '}' )
         {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4498:1: ( (otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) ) (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) ) )+ (otherlv_9= 'assignees' ( (lv_assignees_10_0= ruleUserDef ) ) )* (otherlv_11= 'relatedState' ( (lv_relatedState_12_0= ruleSTATE_NAME_REFERENCE ) ) )? (otherlv_13= 'taskWorkDef' ( (lv_taskWorkDef_14_0= RULE_STRING ) ) )? (otherlv_15= 'onEvent' ( (lv_onEvent_16_0= ruleOnEventType ) ) )* (otherlv_17= 'attribute' ( (lv_attributes_18_0= ruleAttrDef ) ) )* otherlv_19= '}' )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4498:2: (otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) ) (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) ) )+ (otherlv_9= 'assignees' ( (lv_assignees_10_0= ruleUserDef ) ) )* (otherlv_11= 'relatedState' ( (lv_relatedState_12_0= ruleSTATE_NAME_REFERENCE ) ) )? (otherlv_13= 'taskWorkDef' ( (lv_taskWorkDef_14_0= RULE_STRING ) ) )? (otherlv_15= 'onEvent' ( (lv_onEvent_16_0= ruleOnEventType ) ) )* (otherlv_17= 'attribute' ( (lv_attributes_18_0= ruleAttrDef ) ) )* otherlv_19= '}'
            {
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4498:2: (otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) ) )
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4498:4: otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) )
               {
                  otherlv_0 = (Token) match(input, 89, FOLLOW_89_in_ruleCreateTaskRuleDef9917);

                  newLeafNode(otherlv_0, grammarAccess.getCreateTaskRuleDefAccess().getNameKeyword_0_0());

                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4502:1: ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4503:1: (lv_name_1_0= ruleRULE_NAME_REFERENCE )
                  {
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4503:1: (lv_name_1_0= ruleRULE_NAME_REFERENCE )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4504:3: lv_name_1_0= ruleRULE_NAME_REFERENCE
                     {

                        newCompositeNode(
                           grammarAccess.getCreateTaskRuleDefAccess().getNameRULE_NAME_REFERENCEParserRuleCall_0_1_0());

                        pushFollow(FOLLOW_ruleRULE_NAME_REFERENCE_in_ruleCreateTaskRuleDef9938);
                        lv_name_1_0 = ruleRULE_NAME_REFERENCE();

                        state._fsp--;

                        if (current == null) {
                           current = createModelElementForParent(grammarAccess.getCreateTaskRuleDefRule());
                        }
                        set(current, "name", lv_name_1_0, "RULE_NAME_REFERENCE");
                        afterParserOrEnumRuleCall();

                     }

                  }

               }

               otherlv_2 = (Token) match(input, 17, FOLLOW_17_in_ruleCreateTaskRuleDef9951);

               newLeafNode(otherlv_2, grammarAccess.getCreateTaskRuleDefAccess().getLeftCurlyBracketKeyword_1());

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4524:1: (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) )
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4524:3: otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) )
               {
                  otherlv_3 = (Token) match(input, 69, FOLLOW_69_in_ruleCreateTaskRuleDef9964);

                  newLeafNode(otherlv_3, grammarAccess.getCreateTaskRuleDefAccess().getTitleKeyword_2_0());

                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4528:1: ( (lv_title_4_0= RULE_STRING ) )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4529:1: (lv_title_4_0= RULE_STRING )
                  {
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4529:1: (lv_title_4_0= RULE_STRING )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4530:3: lv_title_4_0= RULE_STRING
                     {
                        lv_title_4_0 =
                           (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleCreateTaskRuleDef9981);

                        newLeafNode(lv_title_4_0,
                           grammarAccess.getCreateTaskRuleDefAccess().getTitleSTRINGTerminalRuleCall_2_1_0());

                        if (current == null) {
                           current = createModelElement(grammarAccess.getCreateTaskRuleDefRule());
                        }
                        setWithLastConsumed(current, "title", lv_title_4_0, "STRING");

                     }

                  }

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4546:3: (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )?
               int alt112 = 2;
               int LA112_0 = input.LA(1);

               if (LA112_0 == 51) {
                  alt112 = 1;
               }
               switch (alt112) {
                  case 1:
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4546:5: otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) )
                  {
                     otherlv_5 = (Token) match(input, 51, FOLLOW_51_in_ruleCreateTaskRuleDef10000);

                     newLeafNode(otherlv_5, grammarAccess.getCreateTaskRuleDefAccess().getDescriptionKeyword_3_0());

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4550:1: ( (lv_description_6_0= RULE_STRING ) )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4551:1: (lv_description_6_0= RULE_STRING )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4551:1: (lv_description_6_0= RULE_STRING )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4552:3: lv_description_6_0= RULE_STRING
                        {
                           lv_description_6_0 =
                              (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleCreateTaskRuleDef10017);

                           newLeafNode(lv_description_6_0,
                              grammarAccess.getCreateTaskRuleDefAccess().getDescriptionSTRINGTerminalRuleCall_3_1_0());

                           if (current == null) {
                              current = createModelElement(grammarAccess.getCreateTaskRuleDefRule());
                           }
                           setWithLastConsumed(current, "description", lv_description_6_0, "STRING");

                        }

                     }

                  }
                     break;

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4568:4: (otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) ) )+
               int cnt113 = 0;
               loop113: do {
                  int alt113 = 2;
                  int LA113_0 = input.LA(1);

                  if (LA113_0 == 90) {
                     alt113 = 1;
                  }

                  switch (alt113) {
                     case 1:
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4568:6: otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) )
                     {
                        otherlv_7 = (Token) match(input, 90, FOLLOW_90_in_ruleCreateTaskRuleDef10037);

                        newLeafNode(otherlv_7, grammarAccess.getCreateTaskRuleDefAccess().getRuleLocationKeyword_4_0());

                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4572:1: ( (lv_ruleLocation_8_0= ruleRuleLocation ) )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4573:1: (lv_ruleLocation_8_0= ruleRuleLocation )
                        {
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4573:1: (lv_ruleLocation_8_0= ruleRuleLocation )
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4574:3: lv_ruleLocation_8_0= ruleRuleLocation
                           {

                              newCompositeNode(
                                 grammarAccess.getCreateTaskRuleDefAccess().getRuleLocationRuleLocationEnumRuleCall_4_1_0());

                              pushFollow(FOLLOW_ruleRuleLocation_in_ruleCreateTaskRuleDef10058);
                              lv_ruleLocation_8_0 = ruleRuleLocation();

                              state._fsp--;

                              if (current == null) {
                                 current = createModelElementForParent(grammarAccess.getCreateTaskRuleDefRule());
                              }
                              add(current, "ruleLocation", lv_ruleLocation_8_0, "RuleLocation");
                              afterParserOrEnumRuleCall();

                           }

                        }

                     }
                        break;

                     default:
                        if (cnt113 >= 1) {
                           break loop113;
                        }
                        EarlyExitException eee = new EarlyExitException(113, input);
                        throw eee;
                  }
                  cnt113++;
               } while (true);

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4590:4: (otherlv_9= 'assignees' ( (lv_assignees_10_0= ruleUserDef ) ) )*
               loop114: do {
                  int alt114 = 2;
                  int LA114_0 = input.LA(1);

                  if (LA114_0 == 91) {
                     alt114 = 1;
                  }

                  switch (alt114) {
                     case 1:
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4590:6: otherlv_9= 'assignees' ( (lv_assignees_10_0= ruleUserDef ) )
                     {
                        otherlv_9 = (Token) match(input, 91, FOLLOW_91_in_ruleCreateTaskRuleDef10073);

                        newLeafNode(otherlv_9, grammarAccess.getCreateTaskRuleDefAccess().getAssigneesKeyword_5_0());

                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4594:1: ( (lv_assignees_10_0= ruleUserDef ) )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4595:1: (lv_assignees_10_0= ruleUserDef )
                        {
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4595:1: (lv_assignees_10_0= ruleUserDef )
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4596:3: lv_assignees_10_0= ruleUserDef
                           {

                              newCompositeNode(
                                 grammarAccess.getCreateTaskRuleDefAccess().getAssigneesUserDefParserRuleCall_5_1_0());

                              pushFollow(FOLLOW_ruleUserDef_in_ruleCreateTaskRuleDef10094);
                              lv_assignees_10_0 = ruleUserDef();

                              state._fsp--;

                              if (current == null) {
                                 current = createModelElementForParent(grammarAccess.getCreateTaskRuleDefRule());
                              }
                              add(current, "assignees", lv_assignees_10_0, "UserDef");
                              afterParserOrEnumRuleCall();

                           }

                        }

                     }
                        break;

                     default:
                        break loop114;
                  }
               } while (true);

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4612:4: (otherlv_11= 'relatedState' ( (lv_relatedState_12_0= ruleSTATE_NAME_REFERENCE ) ) )?
               int alt115 = 2;
               int LA115_0 = input.LA(1);

               if (LA115_0 == 92) {
                  alt115 = 1;
               }
               switch (alt115) {
                  case 1:
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4612:6: otherlv_11= 'relatedState' ( (lv_relatedState_12_0= ruleSTATE_NAME_REFERENCE ) )
                  {
                     otherlv_11 = (Token) match(input, 92, FOLLOW_92_in_ruleCreateTaskRuleDef10109);

                     newLeafNode(otherlv_11, grammarAccess.getCreateTaskRuleDefAccess().getRelatedStateKeyword_6_0());

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4616:1: ( (lv_relatedState_12_0= ruleSTATE_NAME_REFERENCE ) )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4617:1: (lv_relatedState_12_0= ruleSTATE_NAME_REFERENCE )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4617:1: (lv_relatedState_12_0= ruleSTATE_NAME_REFERENCE )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4618:3: lv_relatedState_12_0= ruleSTATE_NAME_REFERENCE
                        {

                           newCompositeNode(
                              grammarAccess.getCreateTaskRuleDefAccess().getRelatedStateSTATE_NAME_REFERENCEParserRuleCall_6_1_0());

                           pushFollow(FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleCreateTaskRuleDef10130);
                           lv_relatedState_12_0 = ruleSTATE_NAME_REFERENCE();

                           state._fsp--;

                           if (current == null) {
                              current = createModelElementForParent(grammarAccess.getCreateTaskRuleDefRule());
                           }
                           set(current, "relatedState", lv_relatedState_12_0, "STATE_NAME_REFERENCE");
                           afterParserOrEnumRuleCall();

                        }

                     }

                  }
                     break;

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4634:4: (otherlv_13= 'taskWorkDef' ( (lv_taskWorkDef_14_0= RULE_STRING ) ) )?
               int alt116 = 2;
               int LA116_0 = input.LA(1);

               if (LA116_0 == 93) {
                  alt116 = 1;
               }
               switch (alt116) {
                  case 1:
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4634:6: otherlv_13= 'taskWorkDef' ( (lv_taskWorkDef_14_0= RULE_STRING ) )
                  {
                     otherlv_13 = (Token) match(input, 93, FOLLOW_93_in_ruleCreateTaskRuleDef10145);

                     newLeafNode(otherlv_13, grammarAccess.getCreateTaskRuleDefAccess().getTaskWorkDefKeyword_7_0());

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4638:1: ( (lv_taskWorkDef_14_0= RULE_STRING ) )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4639:1: (lv_taskWorkDef_14_0= RULE_STRING )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4639:1: (lv_taskWorkDef_14_0= RULE_STRING )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4640:3: lv_taskWorkDef_14_0= RULE_STRING
                        {
                           lv_taskWorkDef_14_0 =
                              (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleCreateTaskRuleDef10162);

                           newLeafNode(lv_taskWorkDef_14_0,
                              grammarAccess.getCreateTaskRuleDefAccess().getTaskWorkDefSTRINGTerminalRuleCall_7_1_0());

                           if (current == null) {
                              current = createModelElement(grammarAccess.getCreateTaskRuleDefRule());
                           }
                           setWithLastConsumed(current, "taskWorkDef", lv_taskWorkDef_14_0, "STRING");

                        }

                     }

                  }
                     break;

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4656:4: (otherlv_15= 'onEvent' ( (lv_onEvent_16_0= ruleOnEventType ) ) )*
               loop117: do {
                  int alt117 = 2;
                  int LA117_0 = input.LA(1);

                  if (LA117_0 == 72) {
                     alt117 = 1;
                  }

                  switch (alt117) {
                     case 1:
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4656:6: otherlv_15= 'onEvent' ( (lv_onEvent_16_0= ruleOnEventType ) )
                     {
                        otherlv_15 = (Token) match(input, 72, FOLLOW_72_in_ruleCreateTaskRuleDef10182);

                        newLeafNode(otherlv_15, grammarAccess.getCreateTaskRuleDefAccess().getOnEventKeyword_8_0());

                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4660:1: ( (lv_onEvent_16_0= ruleOnEventType ) )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4661:1: (lv_onEvent_16_0= ruleOnEventType )
                        {
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4661:1: (lv_onEvent_16_0= ruleOnEventType )
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4662:3: lv_onEvent_16_0= ruleOnEventType
                           {

                              newCompositeNode(
                                 grammarAccess.getCreateTaskRuleDefAccess().getOnEventOnEventTypeEnumRuleCall_8_1_0());

                              pushFollow(FOLLOW_ruleOnEventType_in_ruleCreateTaskRuleDef10203);
                              lv_onEvent_16_0 = ruleOnEventType();

                              state._fsp--;

                              if (current == null) {
                                 current = createModelElementForParent(grammarAccess.getCreateTaskRuleDefRule());
                              }
                              add(current, "onEvent", lv_onEvent_16_0, "OnEventType");
                              afterParserOrEnumRuleCall();

                           }

                        }

                     }
                        break;

                     default:
                        break loop117;
                  }
               } while (true);

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4678:4: (otherlv_17= 'attribute' ( (lv_attributes_18_0= ruleAttrDef ) ) )*
               loop118: do {
                  int alt118 = 2;
                  int LA118_0 = input.LA(1);

                  if (LA118_0 == 28) {
                     alt118 = 1;
                  }

                  switch (alt118) {
                     case 1:
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4678:6: otherlv_17= 'attribute' ( (lv_attributes_18_0= ruleAttrDef ) )
                     {
                        otherlv_17 = (Token) match(input, 28, FOLLOW_28_in_ruleCreateTaskRuleDef10218);

                        newLeafNode(otherlv_17, grammarAccess.getCreateTaskRuleDefAccess().getAttributeKeyword_9_0());

                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4682:1: ( (lv_attributes_18_0= ruleAttrDef ) )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4683:1: (lv_attributes_18_0= ruleAttrDef )
                        {
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4683:1: (lv_attributes_18_0= ruleAttrDef )
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4684:3: lv_attributes_18_0= ruleAttrDef
                           {

                              newCompositeNode(
                                 grammarAccess.getCreateTaskRuleDefAccess().getAttributesAttrDefParserRuleCall_9_1_0());

                              pushFollow(FOLLOW_ruleAttrDef_in_ruleCreateTaskRuleDef10239);
                              lv_attributes_18_0 = ruleAttrDef();

                              state._fsp--;

                              if (current == null) {
                                 current = createModelElementForParent(grammarAccess.getCreateTaskRuleDefRule());
                              }
                              add(current, "attributes", lv_attributes_18_0, "AttrDef");
                              afterParserOrEnumRuleCall();

                           }

                        }

                     }
                        break;

                     default:
                        break loop118;
                  }
               } while (true);

               otherlv_19 = (Token) match(input, 22, FOLLOW_22_in_ruleCreateTaskRuleDef10253);

               newLeafNode(otherlv_19, grammarAccess.getCreateTaskRuleDefAccess().getRightCurlyBracketKeyword_10());

            }

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleCreateTaskRuleDef"

   // $ANTLR start "entryRuleCreateDecisionReviewRuleDef"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4712:1: entryRuleCreateDecisionReviewRuleDef returns [EObject current=null] : iv_ruleCreateDecisionReviewRuleDef= ruleCreateDecisionReviewRuleDef EOF ;
   public final EObject entryRuleCreateDecisionReviewRuleDef() throws RecognitionException {
      EObject current = null;

      EObject iv_ruleCreateDecisionReviewRuleDef = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4713:2: (iv_ruleCreateDecisionReviewRuleDef= ruleCreateDecisionReviewRuleDef EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4714:2: iv_ruleCreateDecisionReviewRuleDef= ruleCreateDecisionReviewRuleDef EOF
         {
            newCompositeNode(grammarAccess.getCreateDecisionReviewRuleDefRule());
            pushFollow(FOLLOW_ruleCreateDecisionReviewRuleDef_in_entryRuleCreateDecisionReviewRuleDef10289);
            iv_ruleCreateDecisionReviewRuleDef = ruleCreateDecisionReviewRuleDef();

            state._fsp--;

            current = iv_ruleCreateDecisionReviewRuleDef;
            match(input, EOF, FOLLOW_EOF_in_entryRuleCreateDecisionReviewRuleDef10299);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleCreateDecisionReviewRuleDef"

   // $ANTLR start "ruleCreateDecisionReviewRuleDef"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4721:1: ruleCreateDecisionReviewRuleDef returns [EObject current=null] : ( (otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) ) (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) ) )+ (otherlv_9= 'assignees' ( (lv_assignees_10_0= ruleUserDef ) ) )* (otherlv_11= 'relatedToState' ( (lv_relatedToState_12_0= ruleSTATE_NAME_REFERENCE ) ) )? otherlv_13= 'blockingType' ( (lv_blockingType_14_0= ruleReviewBlockingType ) ) otherlv_15= 'onEvent' ( (lv_stateEvent_16_0= ruleWorkflowEventType ) ) (otherlv_17= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_18_0= ruleBooleanDef ) ) ) ( (lv_options_19_0= ruleDecisionReviewOpt ) )+ (otherlv_20= 'attribute' ( (lv_attributes_21_0= ruleAttrDef ) ) )* otherlv_22= '}' ) ;
   public final EObject ruleCreateDecisionReviewRuleDef() throws RecognitionException {
      EObject current = null;

      Token otherlv_0 = null;
      Token otherlv_2 = null;
      Token otherlv_3 = null;
      Token lv_title_4_0 = null;
      Token otherlv_5 = null;
      Token lv_description_6_0 = null;
      Token otherlv_7 = null;
      Token otherlv_9 = null;
      Token otherlv_11 = null;
      Token otherlv_13 = null;
      Token otherlv_15 = null;
      Token otherlv_17 = null;
      Token otherlv_20 = null;
      Token otherlv_22 = null;
      AntlrDatatypeRuleToken lv_name_1_0 = null;

      Enumerator lv_ruleLocation_8_0 = null;

      EObject lv_assignees_10_0 = null;

      AntlrDatatypeRuleToken lv_relatedToState_12_0 = null;

      Enumerator lv_blockingType_14_0 = null;

      Enumerator lv_stateEvent_16_0 = null;

      Enumerator lv_autoTransitionToDecision_18_0 = null;

      EObject lv_options_19_0 = null;

      EObject lv_attributes_21_0 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4724:28: ( ( (otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) ) (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) ) )+ (otherlv_9= 'assignees' ( (lv_assignees_10_0= ruleUserDef ) ) )* (otherlv_11= 'relatedToState' ( (lv_relatedToState_12_0= ruleSTATE_NAME_REFERENCE ) ) )? otherlv_13= 'blockingType' ( (lv_blockingType_14_0= ruleReviewBlockingType ) ) otherlv_15= 'onEvent' ( (lv_stateEvent_16_0= ruleWorkflowEventType ) ) (otherlv_17= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_18_0= ruleBooleanDef ) ) ) ( (lv_options_19_0= ruleDecisionReviewOpt ) )+ (otherlv_20= 'attribute' ( (lv_attributes_21_0= ruleAttrDef ) ) )* otherlv_22= '}' ) )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4725:1: ( (otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) ) (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) ) )+ (otherlv_9= 'assignees' ( (lv_assignees_10_0= ruleUserDef ) ) )* (otherlv_11= 'relatedToState' ( (lv_relatedToState_12_0= ruleSTATE_NAME_REFERENCE ) ) )? otherlv_13= 'blockingType' ( (lv_blockingType_14_0= ruleReviewBlockingType ) ) otherlv_15= 'onEvent' ( (lv_stateEvent_16_0= ruleWorkflowEventType ) ) (otherlv_17= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_18_0= ruleBooleanDef ) ) ) ( (lv_options_19_0= ruleDecisionReviewOpt ) )+ (otherlv_20= 'attribute' ( (lv_attributes_21_0= ruleAttrDef ) ) )* otherlv_22= '}' )
         {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4725:1: ( (otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) ) (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) ) )+ (otherlv_9= 'assignees' ( (lv_assignees_10_0= ruleUserDef ) ) )* (otherlv_11= 'relatedToState' ( (lv_relatedToState_12_0= ruleSTATE_NAME_REFERENCE ) ) )? otherlv_13= 'blockingType' ( (lv_blockingType_14_0= ruleReviewBlockingType ) ) otherlv_15= 'onEvent' ( (lv_stateEvent_16_0= ruleWorkflowEventType ) ) (otherlv_17= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_18_0= ruleBooleanDef ) ) ) ( (lv_options_19_0= ruleDecisionReviewOpt ) )+ (otherlv_20= 'attribute' ( (lv_attributes_21_0= ruleAttrDef ) ) )* otherlv_22= '}' )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4725:2: (otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) ) (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) ) )+ (otherlv_9= 'assignees' ( (lv_assignees_10_0= ruleUserDef ) ) )* (otherlv_11= 'relatedToState' ( (lv_relatedToState_12_0= ruleSTATE_NAME_REFERENCE ) ) )? otherlv_13= 'blockingType' ( (lv_blockingType_14_0= ruleReviewBlockingType ) ) otherlv_15= 'onEvent' ( (lv_stateEvent_16_0= ruleWorkflowEventType ) ) (otherlv_17= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_18_0= ruleBooleanDef ) ) ) ( (lv_options_19_0= ruleDecisionReviewOpt ) )+ (otherlv_20= 'attribute' ( (lv_attributes_21_0= ruleAttrDef ) ) )* otherlv_22= '}'
            {
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4725:2: (otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) ) )
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4725:4: otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) )
               {
                  otherlv_0 = (Token) match(input, 89, FOLLOW_89_in_ruleCreateDecisionReviewRuleDef10337);

                  newLeafNode(otherlv_0, grammarAccess.getCreateDecisionReviewRuleDefAccess().getNameKeyword_0_0());

                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4729:1: ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4730:1: (lv_name_1_0= ruleRULE_NAME_REFERENCE )
                  {
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4730:1: (lv_name_1_0= ruleRULE_NAME_REFERENCE )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4731:3: lv_name_1_0= ruleRULE_NAME_REFERENCE
                     {

                        newCompositeNode(
                           grammarAccess.getCreateDecisionReviewRuleDefAccess().getNameRULE_NAME_REFERENCEParserRuleCall_0_1_0());

                        pushFollow(FOLLOW_ruleRULE_NAME_REFERENCE_in_ruleCreateDecisionReviewRuleDef10358);
                        lv_name_1_0 = ruleRULE_NAME_REFERENCE();

                        state._fsp--;

                        if (current == null) {
                           current = createModelElementForParent(grammarAccess.getCreateDecisionReviewRuleDefRule());
                        }
                        set(current, "name", lv_name_1_0, "RULE_NAME_REFERENCE");
                        afterParserOrEnumRuleCall();

                     }

                  }

               }

               otherlv_2 = (Token) match(input, 17, FOLLOW_17_in_ruleCreateDecisionReviewRuleDef10371);

               newLeafNode(otherlv_2,
                  grammarAccess.getCreateDecisionReviewRuleDefAccess().getLeftCurlyBracketKeyword_1());

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4751:1: (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) )
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4751:3: otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) )
               {
                  otherlv_3 = (Token) match(input, 69, FOLLOW_69_in_ruleCreateDecisionReviewRuleDef10384);

                  newLeafNode(otherlv_3, grammarAccess.getCreateDecisionReviewRuleDefAccess().getTitleKeyword_2_0());

                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4755:1: ( (lv_title_4_0= RULE_STRING ) )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4756:1: (lv_title_4_0= RULE_STRING )
                  {
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4756:1: (lv_title_4_0= RULE_STRING )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4757:3: lv_title_4_0= RULE_STRING
                     {
                        lv_title_4_0 = (Token) match(input, RULE_STRING,
                           FOLLOW_RULE_STRING_in_ruleCreateDecisionReviewRuleDef10401);

                        newLeafNode(lv_title_4_0,
                           grammarAccess.getCreateDecisionReviewRuleDefAccess().getTitleSTRINGTerminalRuleCall_2_1_0());

                        if (current == null) {
                           current = createModelElement(grammarAccess.getCreateDecisionReviewRuleDefRule());
                        }
                        setWithLastConsumed(current, "title", lv_title_4_0, "STRING");

                     }

                  }

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4773:3: (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )?
               int alt119 = 2;
               int LA119_0 = input.LA(1);

               if (LA119_0 == 51) {
                  alt119 = 1;
               }
               switch (alt119) {
                  case 1:
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4773:5: otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) )
                  {
                     otherlv_5 = (Token) match(input, 51, FOLLOW_51_in_ruleCreateDecisionReviewRuleDef10420);

                     newLeafNode(otherlv_5,
                        grammarAccess.getCreateDecisionReviewRuleDefAccess().getDescriptionKeyword_3_0());

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4777:1: ( (lv_description_6_0= RULE_STRING ) )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4778:1: (lv_description_6_0= RULE_STRING )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4778:1: (lv_description_6_0= RULE_STRING )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4779:3: lv_description_6_0= RULE_STRING
                        {
                           lv_description_6_0 = (Token) match(input, RULE_STRING,
                              FOLLOW_RULE_STRING_in_ruleCreateDecisionReviewRuleDef10437);

                           newLeafNode(lv_description_6_0,
                              grammarAccess.getCreateDecisionReviewRuleDefAccess().getDescriptionSTRINGTerminalRuleCall_3_1_0());

                           if (current == null) {
                              current = createModelElement(grammarAccess.getCreateDecisionReviewRuleDefRule());
                           }
                           setWithLastConsumed(current, "description", lv_description_6_0, "STRING");

                        }

                     }

                  }
                     break;

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4795:4: (otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) ) )+
               int cnt120 = 0;
               loop120: do {
                  int alt120 = 2;
                  int LA120_0 = input.LA(1);

                  if (LA120_0 == 90) {
                     alt120 = 1;
                  }

                  switch (alt120) {
                     case 1:
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4795:6: otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) )
                     {
                        otherlv_7 = (Token) match(input, 90, FOLLOW_90_in_ruleCreateDecisionReviewRuleDef10457);

                        newLeafNode(otherlv_7,
                           grammarAccess.getCreateDecisionReviewRuleDefAccess().getRuleLocationKeyword_4_0());

                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4799:1: ( (lv_ruleLocation_8_0= ruleRuleLocation ) )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4800:1: (lv_ruleLocation_8_0= ruleRuleLocation )
                        {
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4800:1: (lv_ruleLocation_8_0= ruleRuleLocation )
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4801:3: lv_ruleLocation_8_0= ruleRuleLocation
                           {

                              newCompositeNode(
                                 grammarAccess.getCreateDecisionReviewRuleDefAccess().getRuleLocationRuleLocationEnumRuleCall_4_1_0());

                              pushFollow(FOLLOW_ruleRuleLocation_in_ruleCreateDecisionReviewRuleDef10478);
                              lv_ruleLocation_8_0 = ruleRuleLocation();

                              state._fsp--;

                              if (current == null) {
                                 current =
                                    createModelElementForParent(grammarAccess.getCreateDecisionReviewRuleDefRule());
                              }
                              add(current, "ruleLocation", lv_ruleLocation_8_0, "RuleLocation");
                              afterParserOrEnumRuleCall();

                           }

                        }

                     }
                        break;

                     default:
                        if (cnt120 >= 1) {
                           break loop120;
                        }
                        EarlyExitException eee = new EarlyExitException(120, input);
                        throw eee;
                  }
                  cnt120++;
               } while (true);

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4817:4: (otherlv_9= 'assignees' ( (lv_assignees_10_0= ruleUserDef ) ) )*
               loop121: do {
                  int alt121 = 2;
                  int LA121_0 = input.LA(1);

                  if (LA121_0 == 91) {
                     alt121 = 1;
                  }

                  switch (alt121) {
                     case 1:
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4817:6: otherlv_9= 'assignees' ( (lv_assignees_10_0= ruleUserDef ) )
                     {
                        otherlv_9 = (Token) match(input, 91, FOLLOW_91_in_ruleCreateDecisionReviewRuleDef10493);

                        newLeafNode(otherlv_9,
                           grammarAccess.getCreateDecisionReviewRuleDefAccess().getAssigneesKeyword_5_0());

                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4821:1: ( (lv_assignees_10_0= ruleUserDef ) )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4822:1: (lv_assignees_10_0= ruleUserDef )
                        {
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4822:1: (lv_assignees_10_0= ruleUserDef )
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4823:3: lv_assignees_10_0= ruleUserDef
                           {

                              newCompositeNode(
                                 grammarAccess.getCreateDecisionReviewRuleDefAccess().getAssigneesUserDefParserRuleCall_5_1_0());

                              pushFollow(FOLLOW_ruleUserDef_in_ruleCreateDecisionReviewRuleDef10514);
                              lv_assignees_10_0 = ruleUserDef();

                              state._fsp--;

                              if (current == null) {
                                 current =
                                    createModelElementForParent(grammarAccess.getCreateDecisionReviewRuleDefRule());
                              }
                              add(current, "assignees", lv_assignees_10_0, "UserDef");
                              afterParserOrEnumRuleCall();

                           }

                        }

                     }
                        break;

                     default:
                        break loop121;
                  }
               } while (true);

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4839:4: (otherlv_11= 'relatedToState' ( (lv_relatedToState_12_0= ruleSTATE_NAME_REFERENCE ) ) )?
               int alt122 = 2;
               int LA122_0 = input.LA(1);

               if (LA122_0 == 70) {
                  alt122 = 1;
               }
               switch (alt122) {
                  case 1:
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4839:6: otherlv_11= 'relatedToState' ( (lv_relatedToState_12_0= ruleSTATE_NAME_REFERENCE ) )
                  {
                     otherlv_11 = (Token) match(input, 70, FOLLOW_70_in_ruleCreateDecisionReviewRuleDef10529);

                     newLeafNode(otherlv_11,
                        grammarAccess.getCreateDecisionReviewRuleDefAccess().getRelatedToStateKeyword_6_0());

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4843:1: ( (lv_relatedToState_12_0= ruleSTATE_NAME_REFERENCE ) )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4844:1: (lv_relatedToState_12_0= ruleSTATE_NAME_REFERENCE )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4844:1: (lv_relatedToState_12_0= ruleSTATE_NAME_REFERENCE )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4845:3: lv_relatedToState_12_0= ruleSTATE_NAME_REFERENCE
                        {

                           newCompositeNode(
                              grammarAccess.getCreateDecisionReviewRuleDefAccess().getRelatedToStateSTATE_NAME_REFERENCEParserRuleCall_6_1_0());

                           pushFollow(FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleCreateDecisionReviewRuleDef10550);
                           lv_relatedToState_12_0 = ruleSTATE_NAME_REFERENCE();

                           state._fsp--;

                           if (current == null) {
                              current = createModelElementForParent(grammarAccess.getCreateDecisionReviewRuleDefRule());
                           }
                           set(current, "relatedToState", lv_relatedToState_12_0, "STATE_NAME_REFERENCE");
                           afterParserOrEnumRuleCall();

                        }

                     }

                  }
                     break;

               }

               otherlv_13 = (Token) match(input, 71, FOLLOW_71_in_ruleCreateDecisionReviewRuleDef10564);

               newLeafNode(otherlv_13, grammarAccess.getCreateDecisionReviewRuleDefAccess().getBlockingTypeKeyword_7());

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4865:1: ( (lv_blockingType_14_0= ruleReviewBlockingType ) )
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4866:1: (lv_blockingType_14_0= ruleReviewBlockingType )
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4866:1: (lv_blockingType_14_0= ruleReviewBlockingType )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4867:3: lv_blockingType_14_0= ruleReviewBlockingType
                  {

                     newCompositeNode(
                        grammarAccess.getCreateDecisionReviewRuleDefAccess().getBlockingTypeReviewBlockingTypeEnumRuleCall_8_0());

                     pushFollow(FOLLOW_ruleReviewBlockingType_in_ruleCreateDecisionReviewRuleDef10585);
                     lv_blockingType_14_0 = ruleReviewBlockingType();

                     state._fsp--;

                     if (current == null) {
                        current = createModelElementForParent(grammarAccess.getCreateDecisionReviewRuleDefRule());
                     }
                     set(current, "blockingType", lv_blockingType_14_0, "ReviewBlockingType");
                     afterParserOrEnumRuleCall();

                  }

               }

               otherlv_15 = (Token) match(input, 72, FOLLOW_72_in_ruleCreateDecisionReviewRuleDef10597);

               newLeafNode(otherlv_15, grammarAccess.getCreateDecisionReviewRuleDefAccess().getOnEventKeyword_9());

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4887:1: ( (lv_stateEvent_16_0= ruleWorkflowEventType ) )
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4888:1: (lv_stateEvent_16_0= ruleWorkflowEventType )
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4888:1: (lv_stateEvent_16_0= ruleWorkflowEventType )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4889:3: lv_stateEvent_16_0= ruleWorkflowEventType
                  {

                     newCompositeNode(
                        grammarAccess.getCreateDecisionReviewRuleDefAccess().getStateEventWorkflowEventTypeEnumRuleCall_10_0());

                     pushFollow(FOLLOW_ruleWorkflowEventType_in_ruleCreateDecisionReviewRuleDef10618);
                     lv_stateEvent_16_0 = ruleWorkflowEventType();

                     state._fsp--;

                     if (current == null) {
                        current = createModelElementForParent(grammarAccess.getCreateDecisionReviewRuleDefRule());
                     }
                     set(current, "stateEvent", lv_stateEvent_16_0, "WorkflowEventType");
                     afterParserOrEnumRuleCall();

                  }

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4905:2: (otherlv_17= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_18_0= ruleBooleanDef ) ) )
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4905:4: otherlv_17= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_18_0= ruleBooleanDef ) )
               {
                  otherlv_17 = (Token) match(input, 74, FOLLOW_74_in_ruleCreateDecisionReviewRuleDef10631);

                  newLeafNode(otherlv_17,
                     grammarAccess.getCreateDecisionReviewRuleDefAccess().getAutoTransitionToDecisionKeyword_11_0());

                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4909:1: ( (lv_autoTransitionToDecision_18_0= ruleBooleanDef ) )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4910:1: (lv_autoTransitionToDecision_18_0= ruleBooleanDef )
                  {
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4910:1: (lv_autoTransitionToDecision_18_0= ruleBooleanDef )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4911:3: lv_autoTransitionToDecision_18_0= ruleBooleanDef
                     {

                        newCompositeNode(
                           grammarAccess.getCreateDecisionReviewRuleDefAccess().getAutoTransitionToDecisionBooleanDefEnumRuleCall_11_1_0());

                        pushFollow(FOLLOW_ruleBooleanDef_in_ruleCreateDecisionReviewRuleDef10652);
                        lv_autoTransitionToDecision_18_0 = ruleBooleanDef();

                        state._fsp--;

                        if (current == null) {
                           current = createModelElementForParent(grammarAccess.getCreateDecisionReviewRuleDefRule());
                        }
                        set(current, "autoTransitionToDecision", lv_autoTransitionToDecision_18_0, "BooleanDef");
                        afterParserOrEnumRuleCall();

                     }

                  }

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4927:3: ( (lv_options_19_0= ruleDecisionReviewOpt ) )+
               int cnt123 = 0;
               loop123: do {
                  int alt123 = 2;
                  int LA123_0 = input.LA(1);

                  if (LA123_0 == 55) {
                     alt123 = 1;
                  }

                  switch (alt123) {
                     case 1:
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4928:1: (lv_options_19_0= ruleDecisionReviewOpt )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4928:1: (lv_options_19_0= ruleDecisionReviewOpt )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4929:3: lv_options_19_0= ruleDecisionReviewOpt
                        {

                           newCompositeNode(
                              grammarAccess.getCreateDecisionReviewRuleDefAccess().getOptionsDecisionReviewOptParserRuleCall_12_0());

                           pushFollow(FOLLOW_ruleDecisionReviewOpt_in_ruleCreateDecisionReviewRuleDef10674);
                           lv_options_19_0 = ruleDecisionReviewOpt();

                           state._fsp--;

                           if (current == null) {
                              current = createModelElementForParent(grammarAccess.getCreateDecisionReviewRuleDefRule());
                           }
                           add(current, "options", lv_options_19_0, "DecisionReviewOpt");
                           afterParserOrEnumRuleCall();

                        }

                     }
                        break;

                     default:
                        if (cnt123 >= 1) {
                           break loop123;
                        }
                        EarlyExitException eee = new EarlyExitException(123, input);
                        throw eee;
                  }
                  cnt123++;
               } while (true);

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4945:3: (otherlv_20= 'attribute' ( (lv_attributes_21_0= ruleAttrDef ) ) )*
               loop124: do {
                  int alt124 = 2;
                  int LA124_0 = input.LA(1);

                  if (LA124_0 == 28) {
                     alt124 = 1;
                  }

                  switch (alt124) {
                     case 1:
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4945:5: otherlv_20= 'attribute' ( (lv_attributes_21_0= ruleAttrDef ) )
                     {
                        otherlv_20 = (Token) match(input, 28, FOLLOW_28_in_ruleCreateDecisionReviewRuleDef10688);

                        newLeafNode(otherlv_20,
                           grammarAccess.getCreateDecisionReviewRuleDefAccess().getAttributeKeyword_13_0());

                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4949:1: ( (lv_attributes_21_0= ruleAttrDef ) )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4950:1: (lv_attributes_21_0= ruleAttrDef )
                        {
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4950:1: (lv_attributes_21_0= ruleAttrDef )
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4951:3: lv_attributes_21_0= ruleAttrDef
                           {

                              newCompositeNode(
                                 grammarAccess.getCreateDecisionReviewRuleDefAccess().getAttributesAttrDefParserRuleCall_13_1_0());

                              pushFollow(FOLLOW_ruleAttrDef_in_ruleCreateDecisionReviewRuleDef10709);
                              lv_attributes_21_0 = ruleAttrDef();

                              state._fsp--;

                              if (current == null) {
                                 current =
                                    createModelElementForParent(grammarAccess.getCreateDecisionReviewRuleDefRule());
                              }
                              add(current, "attributes", lv_attributes_21_0, "AttrDef");
                              afterParserOrEnumRuleCall();

                           }

                        }

                     }
                        break;

                     default:
                        break loop124;
                  }
               } while (true);

               otherlv_22 = (Token) match(input, 22, FOLLOW_22_in_ruleCreateDecisionReviewRuleDef10723);

               newLeafNode(otherlv_22,
                  grammarAccess.getCreateDecisionReviewRuleDefAccess().getRightCurlyBracketKeyword_14());

            }

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleCreateDecisionReviewRuleDef"

   // $ANTLR start "entryRuleCreatePeerReviewRuleDef"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4979:1: entryRuleCreatePeerReviewRuleDef returns [EObject current=null] : iv_ruleCreatePeerReviewRuleDef= ruleCreatePeerReviewRuleDef EOF ;
   public final EObject entryRuleCreatePeerReviewRuleDef() throws RecognitionException {
      EObject current = null;

      EObject iv_ruleCreatePeerReviewRuleDef = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4980:2: (iv_ruleCreatePeerReviewRuleDef= ruleCreatePeerReviewRuleDef EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4981:2: iv_ruleCreatePeerReviewRuleDef= ruleCreatePeerReviewRuleDef EOF
         {
            newCompositeNode(grammarAccess.getCreatePeerReviewRuleDefRule());
            pushFollow(FOLLOW_ruleCreatePeerReviewRuleDef_in_entryRuleCreatePeerReviewRuleDef10759);
            iv_ruleCreatePeerReviewRuleDef = ruleCreatePeerReviewRuleDef();

            state._fsp--;

            current = iv_ruleCreatePeerReviewRuleDef;
            match(input, EOF, FOLLOW_EOF_in_entryRuleCreatePeerReviewRuleDef10769);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleCreatePeerReviewRuleDef"

   // $ANTLR start "ruleCreatePeerReviewRuleDef"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4988:1: ruleCreatePeerReviewRuleDef returns [EObject current=null] : ( (otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) ) (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) ) )+ (otherlv_9= 'assignees' ( (lv_assignees_10_0= ruleUserDef ) ) )* (otherlv_11= 'relatedToState' ( (lv_relatedToState_12_0= ruleSTATE_NAME_REFERENCE ) ) )? (otherlv_13= 'location' ( (lv_location_14_0= RULE_STRING ) ) )? otherlv_15= 'blockingType' ( (lv_blockingType_16_0= ruleReviewBlockingType ) ) otherlv_17= 'onEvent' ( (lv_stateEvent_18_0= ruleWorkflowEventType ) ) (otherlv_19= 'attribute' ( (lv_attributes_20_0= ruleAttrDef ) ) )* otherlv_21= '}' ) ;
   public final EObject ruleCreatePeerReviewRuleDef() throws RecognitionException {
      EObject current = null;

      Token otherlv_0 = null;
      Token otherlv_2 = null;
      Token otherlv_3 = null;
      Token lv_title_4_0 = null;
      Token otherlv_5 = null;
      Token lv_description_6_0 = null;
      Token otherlv_7 = null;
      Token otherlv_9 = null;
      Token otherlv_11 = null;
      Token otherlv_13 = null;
      Token lv_location_14_0 = null;
      Token otherlv_15 = null;
      Token otherlv_17 = null;
      Token otherlv_19 = null;
      Token otherlv_21 = null;
      AntlrDatatypeRuleToken lv_name_1_0 = null;

      Enumerator lv_ruleLocation_8_0 = null;

      EObject lv_assignees_10_0 = null;

      AntlrDatatypeRuleToken lv_relatedToState_12_0 = null;

      Enumerator lv_blockingType_16_0 = null;

      Enumerator lv_stateEvent_18_0 = null;

      EObject lv_attributes_20_0 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4991:28: ( ( (otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) ) (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) ) )+ (otherlv_9= 'assignees' ( (lv_assignees_10_0= ruleUserDef ) ) )* (otherlv_11= 'relatedToState' ( (lv_relatedToState_12_0= ruleSTATE_NAME_REFERENCE ) ) )? (otherlv_13= 'location' ( (lv_location_14_0= RULE_STRING ) ) )? otherlv_15= 'blockingType' ( (lv_blockingType_16_0= ruleReviewBlockingType ) ) otherlv_17= 'onEvent' ( (lv_stateEvent_18_0= ruleWorkflowEventType ) ) (otherlv_19= 'attribute' ( (lv_attributes_20_0= ruleAttrDef ) ) )* otherlv_21= '}' ) )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4992:1: ( (otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) ) (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) ) )+ (otherlv_9= 'assignees' ( (lv_assignees_10_0= ruleUserDef ) ) )* (otherlv_11= 'relatedToState' ( (lv_relatedToState_12_0= ruleSTATE_NAME_REFERENCE ) ) )? (otherlv_13= 'location' ( (lv_location_14_0= RULE_STRING ) ) )? otherlv_15= 'blockingType' ( (lv_blockingType_16_0= ruleReviewBlockingType ) ) otherlv_17= 'onEvent' ( (lv_stateEvent_18_0= ruleWorkflowEventType ) ) (otherlv_19= 'attribute' ( (lv_attributes_20_0= ruleAttrDef ) ) )* otherlv_21= '}' )
         {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4992:1: ( (otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) ) (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) ) )+ (otherlv_9= 'assignees' ( (lv_assignees_10_0= ruleUserDef ) ) )* (otherlv_11= 'relatedToState' ( (lv_relatedToState_12_0= ruleSTATE_NAME_REFERENCE ) ) )? (otherlv_13= 'location' ( (lv_location_14_0= RULE_STRING ) ) )? otherlv_15= 'blockingType' ( (lv_blockingType_16_0= ruleReviewBlockingType ) ) otherlv_17= 'onEvent' ( (lv_stateEvent_18_0= ruleWorkflowEventType ) ) (otherlv_19= 'attribute' ( (lv_attributes_20_0= ruleAttrDef ) ) )* otherlv_21= '}' )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4992:2: (otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) ) (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) ) )+ (otherlv_9= 'assignees' ( (lv_assignees_10_0= ruleUserDef ) ) )* (otherlv_11= 'relatedToState' ( (lv_relatedToState_12_0= ruleSTATE_NAME_REFERENCE ) ) )? (otherlv_13= 'location' ( (lv_location_14_0= RULE_STRING ) ) )? otherlv_15= 'blockingType' ( (lv_blockingType_16_0= ruleReviewBlockingType ) ) otherlv_17= 'onEvent' ( (lv_stateEvent_18_0= ruleWorkflowEventType ) ) (otherlv_19= 'attribute' ( (lv_attributes_20_0= ruleAttrDef ) ) )* otherlv_21= '}'
            {
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4992:2: (otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) ) )
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4992:4: otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) )
               {
                  otherlv_0 = (Token) match(input, 89, FOLLOW_89_in_ruleCreatePeerReviewRuleDef10807);

                  newLeafNode(otherlv_0, grammarAccess.getCreatePeerReviewRuleDefAccess().getNameKeyword_0_0());

                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4996:1: ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4997:1: (lv_name_1_0= ruleRULE_NAME_REFERENCE )
                  {
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4997:1: (lv_name_1_0= ruleRULE_NAME_REFERENCE )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4998:3: lv_name_1_0= ruleRULE_NAME_REFERENCE
                     {

                        newCompositeNode(
                           grammarAccess.getCreatePeerReviewRuleDefAccess().getNameRULE_NAME_REFERENCEParserRuleCall_0_1_0());

                        pushFollow(FOLLOW_ruleRULE_NAME_REFERENCE_in_ruleCreatePeerReviewRuleDef10828);
                        lv_name_1_0 = ruleRULE_NAME_REFERENCE();

                        state._fsp--;

                        if (current == null) {
                           current = createModelElementForParent(grammarAccess.getCreatePeerReviewRuleDefRule());
                        }
                        set(current, "name", lv_name_1_0, "RULE_NAME_REFERENCE");
                        afterParserOrEnumRuleCall();

                     }

                  }

               }

               otherlv_2 = (Token) match(input, 17, FOLLOW_17_in_ruleCreatePeerReviewRuleDef10841);

               newLeafNode(otherlv_2, grammarAccess.getCreatePeerReviewRuleDefAccess().getLeftCurlyBracketKeyword_1());

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5018:1: (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) )
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5018:3: otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) )
               {
                  otherlv_3 = (Token) match(input, 69, FOLLOW_69_in_ruleCreatePeerReviewRuleDef10854);

                  newLeafNode(otherlv_3, grammarAccess.getCreatePeerReviewRuleDefAccess().getTitleKeyword_2_0());

                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5022:1: ( (lv_title_4_0= RULE_STRING ) )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5023:1: (lv_title_4_0= RULE_STRING )
                  {
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5023:1: (lv_title_4_0= RULE_STRING )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5024:3: lv_title_4_0= RULE_STRING
                     {
                        lv_title_4_0 =
                           (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleCreatePeerReviewRuleDef10871);

                        newLeafNode(lv_title_4_0,
                           grammarAccess.getCreatePeerReviewRuleDefAccess().getTitleSTRINGTerminalRuleCall_2_1_0());

                        if (current == null) {
                           current = createModelElement(grammarAccess.getCreatePeerReviewRuleDefRule());
                        }
                        setWithLastConsumed(current, "title", lv_title_4_0, "STRING");

                     }

                  }

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5040:3: (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )?
               int alt125 = 2;
               int LA125_0 = input.LA(1);

               if (LA125_0 == 51) {
                  alt125 = 1;
               }
               switch (alt125) {
                  case 1:
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5040:5: otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) )
                  {
                     otherlv_5 = (Token) match(input, 51, FOLLOW_51_in_ruleCreatePeerReviewRuleDef10890);

                     newLeafNode(otherlv_5,
                        grammarAccess.getCreatePeerReviewRuleDefAccess().getDescriptionKeyword_3_0());

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5044:1: ( (lv_description_6_0= RULE_STRING ) )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5045:1: (lv_description_6_0= RULE_STRING )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5045:1: (lv_description_6_0= RULE_STRING )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5046:3: lv_description_6_0= RULE_STRING
                        {
                           lv_description_6_0 =
                              (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleCreatePeerReviewRuleDef10907);

                           newLeafNode(lv_description_6_0,
                              grammarAccess.getCreatePeerReviewRuleDefAccess().getDescriptionSTRINGTerminalRuleCall_3_1_0());

                           if (current == null) {
                              current = createModelElement(grammarAccess.getCreatePeerReviewRuleDefRule());
                           }
                           setWithLastConsumed(current, "description", lv_description_6_0, "STRING");

                        }

                     }

                  }
                     break;

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5062:4: (otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) ) )+
               int cnt126 = 0;
               loop126: do {
                  int alt126 = 2;
                  int LA126_0 = input.LA(1);

                  if (LA126_0 == 90) {
                     alt126 = 1;
                  }

                  switch (alt126) {
                     case 1:
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5062:6: otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) )
                     {
                        otherlv_7 = (Token) match(input, 90, FOLLOW_90_in_ruleCreatePeerReviewRuleDef10927);

                        newLeafNode(otherlv_7,
                           grammarAccess.getCreatePeerReviewRuleDefAccess().getRuleLocationKeyword_4_0());

                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5066:1: ( (lv_ruleLocation_8_0= ruleRuleLocation ) )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5067:1: (lv_ruleLocation_8_0= ruleRuleLocation )
                        {
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5067:1: (lv_ruleLocation_8_0= ruleRuleLocation )
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5068:3: lv_ruleLocation_8_0= ruleRuleLocation
                           {

                              newCompositeNode(
                                 grammarAccess.getCreatePeerReviewRuleDefAccess().getRuleLocationRuleLocationEnumRuleCall_4_1_0());

                              pushFollow(FOLLOW_ruleRuleLocation_in_ruleCreatePeerReviewRuleDef10948);
                              lv_ruleLocation_8_0 = ruleRuleLocation();

                              state._fsp--;

                              if (current == null) {
                                 current = createModelElementForParent(grammarAccess.getCreatePeerReviewRuleDefRule());
                              }
                              add(current, "ruleLocation", lv_ruleLocation_8_0, "RuleLocation");
                              afterParserOrEnumRuleCall();

                           }

                        }

                     }
                        break;

                     default:
                        if (cnt126 >= 1) {
                           break loop126;
                        }
                        EarlyExitException eee = new EarlyExitException(126, input);
                        throw eee;
                  }
                  cnt126++;
               } while (true);

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5084:4: (otherlv_9= 'assignees' ( (lv_assignees_10_0= ruleUserDef ) ) )*
               loop127: do {
                  int alt127 = 2;
                  int LA127_0 = input.LA(1);

                  if (LA127_0 == 91) {
                     alt127 = 1;
                  }

                  switch (alt127) {
                     case 1:
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5084:6: otherlv_9= 'assignees' ( (lv_assignees_10_0= ruleUserDef ) )
                     {
                        otherlv_9 = (Token) match(input, 91, FOLLOW_91_in_ruleCreatePeerReviewRuleDef10963);

                        newLeafNode(otherlv_9,
                           grammarAccess.getCreatePeerReviewRuleDefAccess().getAssigneesKeyword_5_0());

                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5088:1: ( (lv_assignees_10_0= ruleUserDef ) )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5089:1: (lv_assignees_10_0= ruleUserDef )
                        {
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5089:1: (lv_assignees_10_0= ruleUserDef )
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5090:3: lv_assignees_10_0= ruleUserDef
                           {

                              newCompositeNode(
                                 grammarAccess.getCreatePeerReviewRuleDefAccess().getAssigneesUserDefParserRuleCall_5_1_0());

                              pushFollow(FOLLOW_ruleUserDef_in_ruleCreatePeerReviewRuleDef10984);
                              lv_assignees_10_0 = ruleUserDef();

                              state._fsp--;

                              if (current == null) {
                                 current = createModelElementForParent(grammarAccess.getCreatePeerReviewRuleDefRule());
                              }
                              add(current, "assignees", lv_assignees_10_0, "UserDef");
                              afterParserOrEnumRuleCall();

                           }

                        }

                     }
                        break;

                     default:
                        break loop127;
                  }
               } while (true);

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5106:4: (otherlv_11= 'relatedToState' ( (lv_relatedToState_12_0= ruleSTATE_NAME_REFERENCE ) ) )?
               int alt128 = 2;
               int LA128_0 = input.LA(1);

               if (LA128_0 == 70) {
                  alt128 = 1;
               }
               switch (alt128) {
                  case 1:
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5106:6: otherlv_11= 'relatedToState' ( (lv_relatedToState_12_0= ruleSTATE_NAME_REFERENCE ) )
                  {
                     otherlv_11 = (Token) match(input, 70, FOLLOW_70_in_ruleCreatePeerReviewRuleDef10999);

                     newLeafNode(otherlv_11,
                        grammarAccess.getCreatePeerReviewRuleDefAccess().getRelatedToStateKeyword_6_0());

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5110:1: ( (lv_relatedToState_12_0= ruleSTATE_NAME_REFERENCE ) )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5111:1: (lv_relatedToState_12_0= ruleSTATE_NAME_REFERENCE )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5111:1: (lv_relatedToState_12_0= ruleSTATE_NAME_REFERENCE )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5112:3: lv_relatedToState_12_0= ruleSTATE_NAME_REFERENCE
                        {

                           newCompositeNode(
                              grammarAccess.getCreatePeerReviewRuleDefAccess().getRelatedToStateSTATE_NAME_REFERENCEParserRuleCall_6_1_0());

                           pushFollow(FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleCreatePeerReviewRuleDef11020);
                           lv_relatedToState_12_0 = ruleSTATE_NAME_REFERENCE();

                           state._fsp--;

                           if (current == null) {
                              current = createModelElementForParent(grammarAccess.getCreatePeerReviewRuleDefRule());
                           }
                           set(current, "relatedToState", lv_relatedToState_12_0, "STATE_NAME_REFERENCE");
                           afterParserOrEnumRuleCall();

                        }

                     }

                  }
                     break;

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5128:4: (otherlv_13= 'location' ( (lv_location_14_0= RULE_STRING ) ) )?
               int alt129 = 2;
               int LA129_0 = input.LA(1);

               if (LA129_0 == 77) {
                  alt129 = 1;
               }
               switch (alt129) {
                  case 1:
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5128:6: otherlv_13= 'location' ( (lv_location_14_0= RULE_STRING ) )
                  {
                     otherlv_13 = (Token) match(input, 77, FOLLOW_77_in_ruleCreatePeerReviewRuleDef11035);

                     newLeafNode(otherlv_13, grammarAccess.getCreatePeerReviewRuleDefAccess().getLocationKeyword_7_0());

                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5132:1: ( (lv_location_14_0= RULE_STRING ) )
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5133:1: (lv_location_14_0= RULE_STRING )
                     {
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5133:1: (lv_location_14_0= RULE_STRING )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5134:3: lv_location_14_0= RULE_STRING
                        {
                           lv_location_14_0 =
                              (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleCreatePeerReviewRuleDef11052);

                           newLeafNode(lv_location_14_0,
                              grammarAccess.getCreatePeerReviewRuleDefAccess().getLocationSTRINGTerminalRuleCall_7_1_0());

                           if (current == null) {
                              current = createModelElement(grammarAccess.getCreatePeerReviewRuleDefRule());
                           }
                           setWithLastConsumed(current, "location", lv_location_14_0, "STRING");

                        }

                     }

                  }
                     break;

               }

               otherlv_15 = (Token) match(input, 71, FOLLOW_71_in_ruleCreatePeerReviewRuleDef11071);

               newLeafNode(otherlv_15, grammarAccess.getCreatePeerReviewRuleDefAccess().getBlockingTypeKeyword_8());

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5154:1: ( (lv_blockingType_16_0= ruleReviewBlockingType ) )
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5155:1: (lv_blockingType_16_0= ruleReviewBlockingType )
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5155:1: (lv_blockingType_16_0= ruleReviewBlockingType )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5156:3: lv_blockingType_16_0= ruleReviewBlockingType
                  {

                     newCompositeNode(
                        grammarAccess.getCreatePeerReviewRuleDefAccess().getBlockingTypeReviewBlockingTypeEnumRuleCall_9_0());

                     pushFollow(FOLLOW_ruleReviewBlockingType_in_ruleCreatePeerReviewRuleDef11092);
                     lv_blockingType_16_0 = ruleReviewBlockingType();

                     state._fsp--;

                     if (current == null) {
                        current = createModelElementForParent(grammarAccess.getCreatePeerReviewRuleDefRule());
                     }
                     set(current, "blockingType", lv_blockingType_16_0, "ReviewBlockingType");
                     afterParserOrEnumRuleCall();

                  }

               }

               otherlv_17 = (Token) match(input, 72, FOLLOW_72_in_ruleCreatePeerReviewRuleDef11104);

               newLeafNode(otherlv_17, grammarAccess.getCreatePeerReviewRuleDefAccess().getOnEventKeyword_10());

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5176:1: ( (lv_stateEvent_18_0= ruleWorkflowEventType ) )
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5177:1: (lv_stateEvent_18_0= ruleWorkflowEventType )
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5177:1: (lv_stateEvent_18_0= ruleWorkflowEventType )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5178:3: lv_stateEvent_18_0= ruleWorkflowEventType
                  {

                     newCompositeNode(
                        grammarAccess.getCreatePeerReviewRuleDefAccess().getStateEventWorkflowEventTypeEnumRuleCall_11_0());

                     pushFollow(FOLLOW_ruleWorkflowEventType_in_ruleCreatePeerReviewRuleDef11125);
                     lv_stateEvent_18_0 = ruleWorkflowEventType();

                     state._fsp--;

                     if (current == null) {
                        current = createModelElementForParent(grammarAccess.getCreatePeerReviewRuleDefRule());
                     }
                     set(current, "stateEvent", lv_stateEvent_18_0, "WorkflowEventType");
                     afterParserOrEnumRuleCall();

                  }

               }

               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5194:2: (otherlv_19= 'attribute' ( (lv_attributes_20_0= ruleAttrDef ) ) )*
               loop130: do {
                  int alt130 = 2;
                  int LA130_0 = input.LA(1);

                  if (LA130_0 == 28) {
                     alt130 = 1;
                  }

                  switch (alt130) {
                     case 1:
                     // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5194:4: otherlv_19= 'attribute' ( (lv_attributes_20_0= ruleAttrDef ) )
                     {
                        otherlv_19 = (Token) match(input, 28, FOLLOW_28_in_ruleCreatePeerReviewRuleDef11138);

                        newLeafNode(otherlv_19,
                           grammarAccess.getCreatePeerReviewRuleDefAccess().getAttributeKeyword_12_0());

                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5198:1: ( (lv_attributes_20_0= ruleAttrDef ) )
                        // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5199:1: (lv_attributes_20_0= ruleAttrDef )
                        {
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5199:1: (lv_attributes_20_0= ruleAttrDef )
                           // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5200:3: lv_attributes_20_0= ruleAttrDef
                           {

                              newCompositeNode(
                                 grammarAccess.getCreatePeerReviewRuleDefAccess().getAttributesAttrDefParserRuleCall_12_1_0());

                              pushFollow(FOLLOW_ruleAttrDef_in_ruleCreatePeerReviewRuleDef11159);
                              lv_attributes_20_0 = ruleAttrDef();

                              state._fsp--;

                              if (current == null) {
                                 current = createModelElementForParent(grammarAccess.getCreatePeerReviewRuleDefRule());
                              }
                              add(current, "attributes", lv_attributes_20_0, "AttrDef");
                              afterParserOrEnumRuleCall();

                           }

                        }

                     }
                        break;

                     default:
                        break loop130;
                  }
               } while (true);

               otherlv_21 = (Token) match(input, 22, FOLLOW_22_in_ruleCreatePeerReviewRuleDef11173);

               newLeafNode(otherlv_21,
                  grammarAccess.getCreatePeerReviewRuleDefAccess().getRightCurlyBracketKeyword_13());

            }

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleCreatePeerReviewRuleDef"

   // $ANTLR start "entryRuleRuleRef"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5228:1: entryRuleRuleRef returns [String current=null] : iv_ruleRuleRef= ruleRuleRef EOF ;
   public final String entryRuleRuleRef() throws RecognitionException {
      String current = null;

      AntlrDatatypeRuleToken iv_ruleRuleRef = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5229:2: (iv_ruleRuleRef= ruleRuleRef EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5230:2: iv_ruleRuleRef= ruleRuleRef EOF
         {
            newCompositeNode(grammarAccess.getRuleRefRule());
            pushFollow(FOLLOW_ruleRuleRef_in_entryRuleRuleRef11210);
            iv_ruleRuleRef = ruleRuleRef();

            state._fsp--;

            current = iv_ruleRuleRef.getText();
            match(input, EOF, FOLLOW_EOF_in_entryRuleRuleRef11221);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleRuleRef"

   // $ANTLR start "ruleRuleRef"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5237:1: ruleRuleRef returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'RequireStateHourSpentPrompt' | kw= 'AddDecisionValidateBlockingReview' | kw= 'AddDecisionValidateNonBlockingReview' | kw= 'AllowTransitionWithWorkingBranch' | kw= 'ForceAssigneesToTeamLeads' | kw= 'RequireTargetedVersion' | kw= 'AllowPrivilegedEditToTeamMember' | kw= 'AllowPrivilegedEditToTeamMemberAndOriginator' | kw= 'AllowPrivilegedEditToAll' | kw= 'AllowEditToAll' | kw= 'AllowAssigneeToAll' | kw= 'AllowTransitionWithoutTaskCompletion' | this_RULE_NAME_REFERENCE_12= ruleRULE_NAME_REFERENCE ) ;
   public final AntlrDatatypeRuleToken ruleRuleRef() throws RecognitionException {
      AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

      Token kw = null;
      AntlrDatatypeRuleToken this_RULE_NAME_REFERENCE_12 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5240:28: ( (kw= 'RequireStateHourSpentPrompt' | kw= 'AddDecisionValidateBlockingReview' | kw= 'AddDecisionValidateNonBlockingReview' | kw= 'AllowTransitionWithWorkingBranch' | kw= 'ForceAssigneesToTeamLeads' | kw= 'RequireTargetedVersion' | kw= 'AllowPrivilegedEditToTeamMember' | kw= 'AllowPrivilegedEditToTeamMemberAndOriginator' | kw= 'AllowPrivilegedEditToAll' | kw= 'AllowEditToAll' | kw= 'AllowAssigneeToAll' | kw= 'AllowTransitionWithoutTaskCompletion' | this_RULE_NAME_REFERENCE_12= ruleRULE_NAME_REFERENCE ) )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5241:1: (kw= 'RequireStateHourSpentPrompt' | kw= 'AddDecisionValidateBlockingReview' | kw= 'AddDecisionValidateNonBlockingReview' | kw= 'AllowTransitionWithWorkingBranch' | kw= 'ForceAssigneesToTeamLeads' | kw= 'RequireTargetedVersion' | kw= 'AllowPrivilegedEditToTeamMember' | kw= 'AllowPrivilegedEditToTeamMemberAndOriginator' | kw= 'AllowPrivilegedEditToAll' | kw= 'AllowEditToAll' | kw= 'AllowAssigneeToAll' | kw= 'AllowTransitionWithoutTaskCompletion' | this_RULE_NAME_REFERENCE_12= ruleRULE_NAME_REFERENCE )
         {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5241:1: (kw= 'RequireStateHourSpentPrompt' | kw= 'AddDecisionValidateBlockingReview' | kw= 'AddDecisionValidateNonBlockingReview' | kw= 'AllowTransitionWithWorkingBranch' | kw= 'ForceAssigneesToTeamLeads' | kw= 'RequireTargetedVersion' | kw= 'AllowPrivilegedEditToTeamMember' | kw= 'AllowPrivilegedEditToTeamMemberAndOriginator' | kw= 'AllowPrivilegedEditToAll' | kw= 'AllowEditToAll' | kw= 'AllowAssigneeToAll' | kw= 'AllowTransitionWithoutTaskCompletion' | this_RULE_NAME_REFERENCE_12= ruleRULE_NAME_REFERENCE )
            int alt131 = 13;
            switch (input.LA(1)) {
               case 94: {
                  alt131 = 1;
               }
                  break;
               case 95: {
                  alt131 = 2;
               }
                  break;
               case 96: {
                  alt131 = 3;
               }
                  break;
               case 97: {
                  alt131 = 4;
               }
                  break;
               case 98: {
                  alt131 = 5;
               }
                  break;
               case 99: {
                  alt131 = 6;
               }
                  break;
               case 100: {
                  alt131 = 7;
               }
                  break;
               case 101: {
                  alt131 = 8;
               }
                  break;
               case 102: {
                  alt131 = 9;
               }
                  break;
               case 103: {
                  alt131 = 10;
               }
                  break;
               case 104: {
                  alt131 = 11;
               }
                  break;
               case 105: {
                  alt131 = 12;
               }
                  break;
               case RULE_STRING: {
                  alt131 = 13;
               }
                  break;
               default:
                  NoViableAltException nvae = new NoViableAltException("", 131, 0, input);

                  throw nvae;
            }

            switch (alt131) {
               case 1:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5242:2: kw= 'RequireStateHourSpentPrompt'
               {
                  kw = (Token) match(input, 94, FOLLOW_94_in_ruleRuleRef11259);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getRuleRefAccess().getRequireStateHourSpentPromptKeyword_0());

               }
                  break;
               case 2:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5249:2: kw= 'AddDecisionValidateBlockingReview'
               {
                  kw = (Token) match(input, 95, FOLLOW_95_in_ruleRuleRef11278);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getRuleRefAccess().getAddDecisionValidateBlockingReviewKeyword_1());

               }
                  break;
               case 3:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5256:2: kw= 'AddDecisionValidateNonBlockingReview'
               {
                  kw = (Token) match(input, 96, FOLLOW_96_in_ruleRuleRef11297);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getRuleRefAccess().getAddDecisionValidateNonBlockingReviewKeyword_2());

               }
                  break;
               case 4:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5263:2: kw= 'AllowTransitionWithWorkingBranch'
               {
                  kw = (Token) match(input, 97, FOLLOW_97_in_ruleRuleRef11316);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getRuleRefAccess().getAllowTransitionWithWorkingBranchKeyword_3());

               }
                  break;
               case 5:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5270:2: kw= 'ForceAssigneesToTeamLeads'
               {
                  kw = (Token) match(input, 98, FOLLOW_98_in_ruleRuleRef11335);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getRuleRefAccess().getForceAssigneesToTeamLeadsKeyword_4());

               }
                  break;
               case 6:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5277:2: kw= 'RequireTargetedVersion'
               {
                  kw = (Token) match(input, 99, FOLLOW_99_in_ruleRuleRef11354);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getRuleRefAccess().getRequireTargetedVersionKeyword_5());

               }
                  break;
               case 7:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5284:2: kw= 'AllowPrivilegedEditToTeamMember'
               {
                  kw = (Token) match(input, 100, FOLLOW_100_in_ruleRuleRef11373);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getRuleRefAccess().getAllowPrivilegedEditToTeamMemberKeyword_6());

               }
                  break;
               case 8:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5291:2: kw= 'AllowPrivilegedEditToTeamMemberAndOriginator'
               {
                  kw = (Token) match(input, 101, FOLLOW_101_in_ruleRuleRef11392);

                  current.merge(kw);
                  newLeafNode(kw,
                     grammarAccess.getRuleRefAccess().getAllowPrivilegedEditToTeamMemberAndOriginatorKeyword_7());

               }
                  break;
               case 9:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5298:2: kw= 'AllowPrivilegedEditToAll'
               {
                  kw = (Token) match(input, 102, FOLLOW_102_in_ruleRuleRef11411);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getRuleRefAccess().getAllowPrivilegedEditToAllKeyword_8());

               }
                  break;
               case 10:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5305:2: kw= 'AllowEditToAll'
               {
                  kw = (Token) match(input, 103, FOLLOW_103_in_ruleRuleRef11430);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getRuleRefAccess().getAllowEditToAllKeyword_9());

               }
                  break;
               case 11:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5312:2: kw= 'AllowAssigneeToAll'
               {
                  kw = (Token) match(input, 104, FOLLOW_104_in_ruleRuleRef11449);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getRuleRefAccess().getAllowAssigneeToAllKeyword_10());

               }
                  break;
               case 12:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5319:2: kw= 'AllowTransitionWithoutTaskCompletion'
               {
                  kw = (Token) match(input, 105, FOLLOW_105_in_ruleRuleRef11468);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getRuleRefAccess().getAllowTransitionWithoutTaskCompletionKeyword_11());

               }
                  break;
               case 13:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5326:5: this_RULE_NAME_REFERENCE_12= ruleRULE_NAME_REFERENCE
               {

                  newCompositeNode(grammarAccess.getRuleRefAccess().getRULE_NAME_REFERENCEParserRuleCall_12());

                  pushFollow(FOLLOW_ruleRULE_NAME_REFERENCE_in_ruleRuleRef11496);
                  this_RULE_NAME_REFERENCE_12 = ruleRULE_NAME_REFERENCE();

                  state._fsp--;

                  current.merge(this_RULE_NAME_REFERENCE_12);

                  afterParserOrEnumRuleCall();

               }
                  break;

            }

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleRuleRef"

   // $ANTLR start "entryRuleReviewRule"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5344:1: entryRuleReviewRule returns [EObject current=null] : iv_ruleReviewRule= ruleReviewRule EOF ;
   public final EObject entryRuleReviewRule() throws RecognitionException {
      EObject current = null;

      EObject iv_ruleReviewRule = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5345:2: (iv_ruleReviewRule= ruleReviewRule EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5346:2: iv_ruleReviewRule= ruleReviewRule EOF
         {
            newCompositeNode(grammarAccess.getReviewRuleRule());
            pushFollow(FOLLOW_ruleReviewRule_in_entryRuleReviewRule11541);
            iv_ruleReviewRule = ruleReviewRule();

            state._fsp--;

            current = iv_ruleReviewRule;
            match(input, EOF, FOLLOW_EOF_in_entryRuleReviewRule11551);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleReviewRule"

   // $ANTLR start "ruleReviewRule"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5353:1: ruleReviewRule returns [EObject current=null] : (this_CreateDecisionReviewRuleDef_0= ruleCreateDecisionReviewRuleDef | this_CreatePeerReviewRuleDef_1= ruleCreatePeerReviewRuleDef ) ;
   public final EObject ruleReviewRule() throws RecognitionException {
      EObject current = null;

      EObject this_CreateDecisionReviewRuleDef_0 = null;

      EObject this_CreatePeerReviewRuleDef_1 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5356:28: ( (this_CreateDecisionReviewRuleDef_0= ruleCreateDecisionReviewRuleDef | this_CreatePeerReviewRuleDef_1= ruleCreatePeerReviewRuleDef ) )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5357:1: (this_CreateDecisionReviewRuleDef_0= ruleCreateDecisionReviewRuleDef | this_CreatePeerReviewRuleDef_1= ruleCreatePeerReviewRuleDef )
         {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5357:1: (this_CreateDecisionReviewRuleDef_0= ruleCreateDecisionReviewRuleDef | this_CreatePeerReviewRuleDef_1= ruleCreatePeerReviewRuleDef )
            int alt132 = 2;
            alt132 = dfa132.predict(input);
            switch (alt132) {
               case 1:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5358:5: this_CreateDecisionReviewRuleDef_0= ruleCreateDecisionReviewRuleDef
               {

                  newCompositeNode(
                     grammarAccess.getReviewRuleAccess().getCreateDecisionReviewRuleDefParserRuleCall_0());

                  pushFollow(FOLLOW_ruleCreateDecisionReviewRuleDef_in_ruleReviewRule11598);
                  this_CreateDecisionReviewRuleDef_0 = ruleCreateDecisionReviewRuleDef();

                  state._fsp--;

                  current = this_CreateDecisionReviewRuleDef_0;
                  afterParserOrEnumRuleCall();

               }
                  break;
               case 2:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5368:5: this_CreatePeerReviewRuleDef_1= ruleCreatePeerReviewRuleDef
               {

                  newCompositeNode(grammarAccess.getReviewRuleAccess().getCreatePeerReviewRuleDefParserRuleCall_1());

                  pushFollow(FOLLOW_ruleCreatePeerReviewRuleDef_in_ruleReviewRule11625);
                  this_CreatePeerReviewRuleDef_1 = ruleCreatePeerReviewRuleDef();

                  state._fsp--;

                  current = this_CreatePeerReviewRuleDef_1;
                  afterParserOrEnumRuleCall();

               }
                  break;

            }

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleReviewRule"

   // $ANTLR start "entryRuleRule"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5384:1: entryRuleRule returns [EObject current=null] : iv_ruleRule= ruleRule EOF ;
   public final EObject entryRuleRule() throws RecognitionException {
      EObject current = null;

      EObject iv_ruleRule = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5385:2: (iv_ruleRule= ruleRule EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5386:2: iv_ruleRule= ruleRule EOF
         {
            newCompositeNode(grammarAccess.getRuleRule());
            pushFollow(FOLLOW_ruleRule_in_entryRuleRule11660);
            iv_ruleRule = ruleRule();

            state._fsp--;

            current = iv_ruleRule;
            match(input, EOF, FOLLOW_EOF_in_entryRuleRule11670);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleRule"

   // $ANTLR start "ruleRule"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5393:1: ruleRule returns [EObject current=null] : (this_RuleDef_0= ruleRuleDef | this_CreateTaskRuleDef_1= ruleCreateTaskRuleDef | this_ReviewRule_2= ruleReviewRule ) ;
   public final EObject ruleRule() throws RecognitionException {
      EObject current = null;

      EObject this_RuleDef_0 = null;

      EObject this_CreateTaskRuleDef_1 = null;

      EObject this_ReviewRule_2 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5396:28: ( (this_RuleDef_0= ruleRuleDef | this_CreateTaskRuleDef_1= ruleCreateTaskRuleDef | this_ReviewRule_2= ruleReviewRule ) )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5397:1: (this_RuleDef_0= ruleRuleDef | this_CreateTaskRuleDef_1= ruleCreateTaskRuleDef | this_ReviewRule_2= ruleReviewRule )
         {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5397:1: (this_RuleDef_0= ruleRuleDef | this_CreateTaskRuleDef_1= ruleCreateTaskRuleDef | this_ReviewRule_2= ruleReviewRule )
            int alt133 = 3;
            alt133 = dfa133.predict(input);
            switch (alt133) {
               case 1:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5398:5: this_RuleDef_0= ruleRuleDef
               {

                  newCompositeNode(grammarAccess.getRuleAccess().getRuleDefParserRuleCall_0());

                  pushFollow(FOLLOW_ruleRuleDef_in_ruleRule11717);
                  this_RuleDef_0 = ruleRuleDef();

                  state._fsp--;

                  current = this_RuleDef_0;
                  afterParserOrEnumRuleCall();

               }
                  break;
               case 2:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5408:5: this_CreateTaskRuleDef_1= ruleCreateTaskRuleDef
               {

                  newCompositeNode(grammarAccess.getRuleAccess().getCreateTaskRuleDefParserRuleCall_1());

                  pushFollow(FOLLOW_ruleCreateTaskRuleDef_in_ruleRule11744);
                  this_CreateTaskRuleDef_1 = ruleCreateTaskRuleDef();

                  state._fsp--;

                  current = this_CreateTaskRuleDef_1;
                  afterParserOrEnumRuleCall();

               }
                  break;
               case 3:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5418:5: this_ReviewRule_2= ruleReviewRule
               {

                  newCompositeNode(grammarAccess.getRuleAccess().getReviewRuleParserRuleCall_2());

                  pushFollow(FOLLOW_ruleReviewRule_in_ruleRule11771);
                  this_ReviewRule_2 = ruleReviewRule();

                  state._fsp--;

                  current = this_ReviewRule_2;
                  afterParserOrEnumRuleCall();

               }
                  break;

            }

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleRule"

   // $ANTLR start "entryRuleWIDGET_OPTION_NAME"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5434:1: entryRuleWIDGET_OPTION_NAME returns [String current=null] : iv_ruleWIDGET_OPTION_NAME= ruleWIDGET_OPTION_NAME EOF ;
   public final String entryRuleWIDGET_OPTION_NAME() throws RecognitionException {
      String current = null;

      AntlrDatatypeRuleToken iv_ruleWIDGET_OPTION_NAME = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5435:2: (iv_ruleWIDGET_OPTION_NAME= ruleWIDGET_OPTION_NAME EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5436:2: iv_ruleWIDGET_OPTION_NAME= ruleWIDGET_OPTION_NAME EOF
         {
            newCompositeNode(grammarAccess.getWIDGET_OPTION_NAMERule());
            pushFollow(FOLLOW_ruleWIDGET_OPTION_NAME_in_entryRuleWIDGET_OPTION_NAME11807);
            iv_ruleWIDGET_OPTION_NAME = ruleWIDGET_OPTION_NAME();

            state._fsp--;

            current = iv_ruleWIDGET_OPTION_NAME.getText();
            match(input, EOF, FOLLOW_EOF_in_entryRuleWIDGET_OPTION_NAME11818);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleWIDGET_OPTION_NAME"

   // $ANTLR start "ruleWIDGET_OPTION_NAME"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5443:1: ruleWIDGET_OPTION_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
   public final AntlrDatatypeRuleToken ruleWIDGET_OPTION_NAME() throws RecognitionException {
      AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

      Token this_STRING_0 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5446:28: (this_STRING_0= RULE_STRING )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5447:5: this_STRING_0= RULE_STRING
         {
            this_STRING_0 = (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleWIDGET_OPTION_NAME11857);

            current.merge(this_STRING_0);

            newLeafNode(this_STRING_0, grammarAccess.getWIDGET_OPTION_NAMEAccess().getSTRINGTerminalRuleCall());

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleWIDGET_OPTION_NAME"

   // $ANTLR start "entryRuleWidgetOption"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5462:1: entryRuleWidgetOption returns [String current=null] : iv_ruleWidgetOption= ruleWidgetOption EOF ;
   public final String entryRuleWidgetOption() throws RecognitionException {
      String current = null;

      AntlrDatatypeRuleToken iv_ruleWidgetOption = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5463:2: (iv_ruleWidgetOption= ruleWidgetOption EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5464:2: iv_ruleWidgetOption= ruleWidgetOption EOF
         {
            newCompositeNode(grammarAccess.getWidgetOptionRule());
            pushFollow(FOLLOW_ruleWidgetOption_in_entryRuleWidgetOption11902);
            iv_ruleWidgetOption = ruleWidgetOption();

            state._fsp--;

            current = iv_ruleWidgetOption.getText();
            match(input, EOF, FOLLOW_EOF_in_entryRuleWidgetOption11913);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleWidgetOption"

   // $ANTLR start "ruleWidgetOption"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5471:1: ruleWidgetOption returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'REQUIRED_FOR_TRANSITION' | kw= 'NOT_REQUIRED_FOR_TRANSITION' | kw= 'REQUIRED_FOR_COMPLETION' | kw= 'NOT_REQUIRED_FOR_COMPLETION' | kw= 'ENABLED' | kw= 'NOT_ENABLED' | kw= 'EDITABLE' | kw= 'NOT_EDITABLE' | kw= 'FUTURE_DATE_REQUIRED' | kw= 'NOT_FUTURE_DATE_REQUIRED' | kw= 'MULTI_SELECT' | kw= 'HORIZONTAL_LABEL' | kw= 'VERTICAL_LABEL' | kw= 'LABEL_AFTER' | kw= 'LABEL_BEFORE' | kw= 'NO_LABEL' | kw= 'SORTED' | kw= 'ADD_DEFAULT_VALUE' | kw= 'NO_DEFAULT_VALUE' | kw= 'BEGIN_COMPOSITE_4' | kw= 'BEGIN_COMPOSITE_6' | kw= 'BEGIN_COMPOSITE_8' | kw= 'BEGIN_COMPOSITE_10' | kw= 'END_COMPOSITE' | kw= 'FILL_NONE' | kw= 'FILL_HORIZONTALLY' | kw= 'FILL_VERTICALLY' | kw= 'ALIGN_LEFT' | kw= 'ALIGN_RIGHT' | kw= 'ALIGN_CENTER' | this_WIDGET_OPTION_NAME_30= ruleWIDGET_OPTION_NAME ) ;
   public final AntlrDatatypeRuleToken ruleWidgetOption() throws RecognitionException {
      AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

      Token kw = null;
      AntlrDatatypeRuleToken this_WIDGET_OPTION_NAME_30 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5474:28: ( (kw= 'REQUIRED_FOR_TRANSITION' | kw= 'NOT_REQUIRED_FOR_TRANSITION' | kw= 'REQUIRED_FOR_COMPLETION' | kw= 'NOT_REQUIRED_FOR_COMPLETION' | kw= 'ENABLED' | kw= 'NOT_ENABLED' | kw= 'EDITABLE' | kw= 'NOT_EDITABLE' | kw= 'FUTURE_DATE_REQUIRED' | kw= 'NOT_FUTURE_DATE_REQUIRED' | kw= 'MULTI_SELECT' | kw= 'HORIZONTAL_LABEL' | kw= 'VERTICAL_LABEL' | kw= 'LABEL_AFTER' | kw= 'LABEL_BEFORE' | kw= 'NO_LABEL' | kw= 'SORTED' | kw= 'ADD_DEFAULT_VALUE' | kw= 'NO_DEFAULT_VALUE' | kw= 'BEGIN_COMPOSITE_4' | kw= 'BEGIN_COMPOSITE_6' | kw= 'BEGIN_COMPOSITE_8' | kw= 'BEGIN_COMPOSITE_10' | kw= 'END_COMPOSITE' | kw= 'FILL_NONE' | kw= 'FILL_HORIZONTALLY' | kw= 'FILL_VERTICALLY' | kw= 'ALIGN_LEFT' | kw= 'ALIGN_RIGHT' | kw= 'ALIGN_CENTER' | this_WIDGET_OPTION_NAME_30= ruleWIDGET_OPTION_NAME ) )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5475:1: (kw= 'REQUIRED_FOR_TRANSITION' | kw= 'NOT_REQUIRED_FOR_TRANSITION' | kw= 'REQUIRED_FOR_COMPLETION' | kw= 'NOT_REQUIRED_FOR_COMPLETION' | kw= 'ENABLED' | kw= 'NOT_ENABLED' | kw= 'EDITABLE' | kw= 'NOT_EDITABLE' | kw= 'FUTURE_DATE_REQUIRED' | kw= 'NOT_FUTURE_DATE_REQUIRED' | kw= 'MULTI_SELECT' | kw= 'HORIZONTAL_LABEL' | kw= 'VERTICAL_LABEL' | kw= 'LABEL_AFTER' | kw= 'LABEL_BEFORE' | kw= 'NO_LABEL' | kw= 'SORTED' | kw= 'ADD_DEFAULT_VALUE' | kw= 'NO_DEFAULT_VALUE' | kw= 'BEGIN_COMPOSITE_4' | kw= 'BEGIN_COMPOSITE_6' | kw= 'BEGIN_COMPOSITE_8' | kw= 'BEGIN_COMPOSITE_10' | kw= 'END_COMPOSITE' | kw= 'FILL_NONE' | kw= 'FILL_HORIZONTALLY' | kw= 'FILL_VERTICALLY' | kw= 'ALIGN_LEFT' | kw= 'ALIGN_RIGHT' | kw= 'ALIGN_CENTER' | this_WIDGET_OPTION_NAME_30= ruleWIDGET_OPTION_NAME )
         {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5475:1: (kw= 'REQUIRED_FOR_TRANSITION' | kw= 'NOT_REQUIRED_FOR_TRANSITION' | kw= 'REQUIRED_FOR_COMPLETION' | kw= 'NOT_REQUIRED_FOR_COMPLETION' | kw= 'ENABLED' | kw= 'NOT_ENABLED' | kw= 'EDITABLE' | kw= 'NOT_EDITABLE' | kw= 'FUTURE_DATE_REQUIRED' | kw= 'NOT_FUTURE_DATE_REQUIRED' | kw= 'MULTI_SELECT' | kw= 'HORIZONTAL_LABEL' | kw= 'VERTICAL_LABEL' | kw= 'LABEL_AFTER' | kw= 'LABEL_BEFORE' | kw= 'NO_LABEL' | kw= 'SORTED' | kw= 'ADD_DEFAULT_VALUE' | kw= 'NO_DEFAULT_VALUE' | kw= 'BEGIN_COMPOSITE_4' | kw= 'BEGIN_COMPOSITE_6' | kw= 'BEGIN_COMPOSITE_8' | kw= 'BEGIN_COMPOSITE_10' | kw= 'END_COMPOSITE' | kw= 'FILL_NONE' | kw= 'FILL_HORIZONTALLY' | kw= 'FILL_VERTICALLY' | kw= 'ALIGN_LEFT' | kw= 'ALIGN_RIGHT' | kw= 'ALIGN_CENTER' | this_WIDGET_OPTION_NAME_30= ruleWIDGET_OPTION_NAME )
            int alt134 = 31;
            switch (input.LA(1)) {
               case 106: {
                  alt134 = 1;
               }
                  break;
               case 107: {
                  alt134 = 2;
               }
                  break;
               case 108: {
                  alt134 = 3;
               }
                  break;
               case 109: {
                  alt134 = 4;
               }
                  break;
               case 110: {
                  alt134 = 5;
               }
                  break;
               case 111: {
                  alt134 = 6;
               }
                  break;
               case 112: {
                  alt134 = 7;
               }
                  break;
               case 113: {
                  alt134 = 8;
               }
                  break;
               case 114: {
                  alt134 = 9;
               }
                  break;
               case 115: {
                  alt134 = 10;
               }
                  break;
               case 116: {
                  alt134 = 11;
               }
                  break;
               case 117: {
                  alt134 = 12;
               }
                  break;
               case 118: {
                  alt134 = 13;
               }
                  break;
               case 119: {
                  alt134 = 14;
               }
                  break;
               case 120: {
                  alt134 = 15;
               }
                  break;
               case 121: {
                  alt134 = 16;
               }
                  break;
               case 122: {
                  alt134 = 17;
               }
                  break;
               case 123: {
                  alt134 = 18;
               }
                  break;
               case 124: {
                  alt134 = 19;
               }
                  break;
               case 125: {
                  alt134 = 20;
               }
                  break;
               case 126: {
                  alt134 = 21;
               }
                  break;
               case 127: {
                  alt134 = 22;
               }
                  break;
               case 128: {
                  alt134 = 23;
               }
                  break;
               case 129: {
                  alt134 = 24;
               }
                  break;
               case 130: {
                  alt134 = 25;
               }
                  break;
               case 131: {
                  alt134 = 26;
               }
                  break;
               case 132: {
                  alt134 = 27;
               }
                  break;
               case 133: {
                  alt134 = 28;
               }
                  break;
               case 134: {
                  alt134 = 29;
               }
                  break;
               case 135: {
                  alt134 = 30;
               }
                  break;
               case RULE_STRING: {
                  alt134 = 31;
               }
                  break;
               default:
                  NoViableAltException nvae = new NoViableAltException("", 134, 0, input);

                  throw nvae;
            }

            switch (alt134) {
               case 1:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5476:2: kw= 'REQUIRED_FOR_TRANSITION'
               {
                  kw = (Token) match(input, 106, FOLLOW_106_in_ruleWidgetOption11951);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getREQUIRED_FOR_TRANSITIONKeyword_0());

               }
                  break;
               case 2:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5483:2: kw= 'NOT_REQUIRED_FOR_TRANSITION'
               {
                  kw = (Token) match(input, 107, FOLLOW_107_in_ruleWidgetOption11970);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getNOT_REQUIRED_FOR_TRANSITIONKeyword_1());

               }
                  break;
               case 3:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5490:2: kw= 'REQUIRED_FOR_COMPLETION'
               {
                  kw = (Token) match(input, 108, FOLLOW_108_in_ruleWidgetOption11989);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getREQUIRED_FOR_COMPLETIONKeyword_2());

               }
                  break;
               case 4:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5497:2: kw= 'NOT_REQUIRED_FOR_COMPLETION'
               {
                  kw = (Token) match(input, 109, FOLLOW_109_in_ruleWidgetOption12008);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getNOT_REQUIRED_FOR_COMPLETIONKeyword_3());

               }
                  break;
               case 5:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5504:2: kw= 'ENABLED'
               {
                  kw = (Token) match(input, 110, FOLLOW_110_in_ruleWidgetOption12027);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getENABLEDKeyword_4());

               }
                  break;
               case 6:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5511:2: kw= 'NOT_ENABLED'
               {
                  kw = (Token) match(input, 111, FOLLOW_111_in_ruleWidgetOption12046);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getNOT_ENABLEDKeyword_5());

               }
                  break;
               case 7:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5518:2: kw= 'EDITABLE'
               {
                  kw = (Token) match(input, 112, FOLLOW_112_in_ruleWidgetOption12065);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getEDITABLEKeyword_6());

               }
                  break;
               case 8:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5525:2: kw= 'NOT_EDITABLE'
               {
                  kw = (Token) match(input, 113, FOLLOW_113_in_ruleWidgetOption12084);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getNOT_EDITABLEKeyword_7());

               }
                  break;
               case 9:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5532:2: kw= 'FUTURE_DATE_REQUIRED'
               {
                  kw = (Token) match(input, 114, FOLLOW_114_in_ruleWidgetOption12103);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getFUTURE_DATE_REQUIREDKeyword_8());

               }
                  break;
               case 10:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5539:2: kw= 'NOT_FUTURE_DATE_REQUIRED'
               {
                  kw = (Token) match(input, 115, FOLLOW_115_in_ruleWidgetOption12122);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getNOT_FUTURE_DATE_REQUIREDKeyword_9());

               }
                  break;
               case 11:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5546:2: kw= 'MULTI_SELECT'
               {
                  kw = (Token) match(input, 116, FOLLOW_116_in_ruleWidgetOption12141);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getMULTI_SELECTKeyword_10());

               }
                  break;
               case 12:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5553:2: kw= 'HORIZONTAL_LABEL'
               {
                  kw = (Token) match(input, 117, FOLLOW_117_in_ruleWidgetOption12160);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getHORIZONTAL_LABELKeyword_11());

               }
                  break;
               case 13:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5560:2: kw= 'VERTICAL_LABEL'
               {
                  kw = (Token) match(input, 118, FOLLOW_118_in_ruleWidgetOption12179);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getVERTICAL_LABELKeyword_12());

               }
                  break;
               case 14:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5567:2: kw= 'LABEL_AFTER'
               {
                  kw = (Token) match(input, 119, FOLLOW_119_in_ruleWidgetOption12198);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getLABEL_AFTERKeyword_13());

               }
                  break;
               case 15:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5574:2: kw= 'LABEL_BEFORE'
               {
                  kw = (Token) match(input, 120, FOLLOW_120_in_ruleWidgetOption12217);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getLABEL_BEFOREKeyword_14());

               }
                  break;
               case 16:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5581:2: kw= 'NO_LABEL'
               {
                  kw = (Token) match(input, 121, FOLLOW_121_in_ruleWidgetOption12236);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getNO_LABELKeyword_15());

               }
                  break;
               case 17:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5588:2: kw= 'SORTED'
               {
                  kw = (Token) match(input, 122, FOLLOW_122_in_ruleWidgetOption12255);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getSORTEDKeyword_16());

               }
                  break;
               case 18:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5595:2: kw= 'ADD_DEFAULT_VALUE'
               {
                  kw = (Token) match(input, 123, FOLLOW_123_in_ruleWidgetOption12274);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getADD_DEFAULT_VALUEKeyword_17());

               }
                  break;
               case 19:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5602:2: kw= 'NO_DEFAULT_VALUE'
               {
                  kw = (Token) match(input, 124, FOLLOW_124_in_ruleWidgetOption12293);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getNO_DEFAULT_VALUEKeyword_18());

               }
                  break;
               case 20:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5609:2: kw= 'BEGIN_COMPOSITE_4'
               {
                  kw = (Token) match(input, 125, FOLLOW_125_in_ruleWidgetOption12312);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getBEGIN_COMPOSITE_4Keyword_19());

               }
                  break;
               case 21:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5616:2: kw= 'BEGIN_COMPOSITE_6'
               {
                  kw = (Token) match(input, 126, FOLLOW_126_in_ruleWidgetOption12331);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getBEGIN_COMPOSITE_6Keyword_20());

               }
                  break;
               case 22:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5623:2: kw= 'BEGIN_COMPOSITE_8'
               {
                  kw = (Token) match(input, 127, FOLLOW_127_in_ruleWidgetOption12350);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getBEGIN_COMPOSITE_8Keyword_21());

               }
                  break;
               case 23:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5630:2: kw= 'BEGIN_COMPOSITE_10'
               {
                  kw = (Token) match(input, 128, FOLLOW_128_in_ruleWidgetOption12369);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getBEGIN_COMPOSITE_10Keyword_22());

               }
                  break;
               case 24:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5637:2: kw= 'END_COMPOSITE'
               {
                  kw = (Token) match(input, 129, FOLLOW_129_in_ruleWidgetOption12388);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getEND_COMPOSITEKeyword_23());

               }
                  break;
               case 25:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5644:2: kw= 'FILL_NONE'
               {
                  kw = (Token) match(input, 130, FOLLOW_130_in_ruleWidgetOption12407);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getFILL_NONEKeyword_24());

               }
                  break;
               case 26:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5651:2: kw= 'FILL_HORIZONTALLY'
               {
                  kw = (Token) match(input, 131, FOLLOW_131_in_ruleWidgetOption12426);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getFILL_HORIZONTALLYKeyword_25());

               }
                  break;
               case 27:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5658:2: kw= 'FILL_VERTICALLY'
               {
                  kw = (Token) match(input, 132, FOLLOW_132_in_ruleWidgetOption12445);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getFILL_VERTICALLYKeyword_26());

               }
                  break;
               case 28:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5665:2: kw= 'ALIGN_LEFT'
               {
                  kw = (Token) match(input, 133, FOLLOW_133_in_ruleWidgetOption12464);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getALIGN_LEFTKeyword_27());

               }
                  break;
               case 29:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5672:2: kw= 'ALIGN_RIGHT'
               {
                  kw = (Token) match(input, 134, FOLLOW_134_in_ruleWidgetOption12483);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getALIGN_RIGHTKeyword_28());

               }
                  break;
               case 30:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5679:2: kw= 'ALIGN_CENTER'
               {
                  kw = (Token) match(input, 135, FOLLOW_135_in_ruleWidgetOption12502);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getALIGN_CENTERKeyword_29());

               }
                  break;
               case 31:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5686:5: this_WIDGET_OPTION_NAME_30= ruleWIDGET_OPTION_NAME
               {

                  newCompositeNode(grammarAccess.getWidgetOptionAccess().getWIDGET_OPTION_NAMEParserRuleCall_30());

                  pushFollow(FOLLOW_ruleWIDGET_OPTION_NAME_in_ruleWidgetOption12530);
                  this_WIDGET_OPTION_NAME_30 = ruleWIDGET_OPTION_NAME();

                  state._fsp--;

                  current.merge(this_WIDGET_OPTION_NAME_30);

                  afterParserOrEnumRuleCall();

               }
                  break;

            }

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleWidgetOption"

   // $ANTLR start "entryRulePAGE_TYPE_NAME"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5704:1: entryRulePAGE_TYPE_NAME returns [String current=null] : iv_rulePAGE_TYPE_NAME= rulePAGE_TYPE_NAME EOF ;
   public final String entryRulePAGE_TYPE_NAME() throws RecognitionException {
      String current = null;

      AntlrDatatypeRuleToken iv_rulePAGE_TYPE_NAME = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5705:2: (iv_rulePAGE_TYPE_NAME= rulePAGE_TYPE_NAME EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5706:2: iv_rulePAGE_TYPE_NAME= rulePAGE_TYPE_NAME EOF
         {
            newCompositeNode(grammarAccess.getPAGE_TYPE_NAMERule());
            pushFollow(FOLLOW_rulePAGE_TYPE_NAME_in_entryRulePAGE_TYPE_NAME12576);
            iv_rulePAGE_TYPE_NAME = rulePAGE_TYPE_NAME();

            state._fsp--;

            current = iv_rulePAGE_TYPE_NAME.getText();
            match(input, EOF, FOLLOW_EOF_in_entryRulePAGE_TYPE_NAME12587);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRulePAGE_TYPE_NAME"

   // $ANTLR start "rulePAGE_TYPE_NAME"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5713:1: rulePAGE_TYPE_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
   public final AntlrDatatypeRuleToken rulePAGE_TYPE_NAME() throws RecognitionException {
      AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

      Token this_STRING_0 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5716:28: (this_STRING_0= RULE_STRING )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5717:5: this_STRING_0= RULE_STRING
         {
            this_STRING_0 = (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_rulePAGE_TYPE_NAME12626);

            current.merge(this_STRING_0);

            newLeafNode(this_STRING_0, grammarAccess.getPAGE_TYPE_NAMEAccess().getSTRINGTerminalRuleCall());

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "rulePAGE_TYPE_NAME"

   // $ANTLR start "entryRulePageType"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5732:1: entryRulePageType returns [String current=null] : iv_rulePageType= rulePageType EOF ;
   public final String entryRulePageType() throws RecognitionException {
      String current = null;

      AntlrDatatypeRuleToken iv_rulePageType = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5733:2: (iv_rulePageType= rulePageType EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5734:2: iv_rulePageType= rulePageType EOF
         {
            newCompositeNode(grammarAccess.getPageTypeRule());
            pushFollow(FOLLOW_rulePageType_in_entryRulePageType12671);
            iv_rulePageType = rulePageType();

            state._fsp--;

            current = iv_rulePageType.getText();
            match(input, EOF, FOLLOW_EOF_in_entryRulePageType12682);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRulePageType"

   // $ANTLR start "rulePageType"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5741:1: rulePageType returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'Working' | kw= 'Completed' | kw= 'Cancelled' | this_PAGE_TYPE_NAME_3= rulePAGE_TYPE_NAME ) ;
   public final AntlrDatatypeRuleToken rulePageType() throws RecognitionException {
      AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

      Token kw = null;
      AntlrDatatypeRuleToken this_PAGE_TYPE_NAME_3 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5744:28: ( (kw= 'Working' | kw= 'Completed' | kw= 'Cancelled' | this_PAGE_TYPE_NAME_3= rulePAGE_TYPE_NAME ) )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5745:1: (kw= 'Working' | kw= 'Completed' | kw= 'Cancelled' | this_PAGE_TYPE_NAME_3= rulePAGE_TYPE_NAME )
         {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5745:1: (kw= 'Working' | kw= 'Completed' | kw= 'Cancelled' | this_PAGE_TYPE_NAME_3= rulePAGE_TYPE_NAME )
            int alt135 = 4;
            switch (input.LA(1)) {
               case 136: {
                  alt135 = 1;
               }
                  break;
               case 137: {
                  alt135 = 2;
               }
                  break;
               case 138: {
                  alt135 = 3;
               }
                  break;
               case RULE_STRING: {
                  alt135 = 4;
               }
                  break;
               default:
                  NoViableAltException nvae = new NoViableAltException("", 135, 0, input);

                  throw nvae;
            }

            switch (alt135) {
               case 1:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5746:2: kw= 'Working'
               {
                  kw = (Token) match(input, 136, FOLLOW_136_in_rulePageType12720);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getPageTypeAccess().getWorkingKeyword_0());

               }
                  break;
               case 2:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5753:2: kw= 'Completed'
               {
                  kw = (Token) match(input, 137, FOLLOW_137_in_rulePageType12739);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getPageTypeAccess().getCompletedKeyword_1());

               }
                  break;
               case 3:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5760:2: kw= 'Cancelled'
               {
                  kw = (Token) match(input, 138, FOLLOW_138_in_rulePageType12758);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getPageTypeAccess().getCancelledKeyword_2());

               }
                  break;
               case 4:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5767:5: this_PAGE_TYPE_NAME_3= rulePAGE_TYPE_NAME
               {

                  newCompositeNode(grammarAccess.getPageTypeAccess().getPAGE_TYPE_NAMEParserRuleCall_3());

                  pushFollow(FOLLOW_rulePAGE_TYPE_NAME_in_rulePageType12786);
                  this_PAGE_TYPE_NAME_3 = rulePAGE_TYPE_NAME();

                  state._fsp--;

                  current.merge(this_PAGE_TYPE_NAME_3);

                  afterParserOrEnumRuleCall();

               }
                  break;

            }

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "rulePageType"

   // $ANTLR start "entryRuleCOLOR_NAME"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5785:1: entryRuleCOLOR_NAME returns [String current=null] : iv_ruleCOLOR_NAME= ruleCOLOR_NAME EOF ;
   public final String entryRuleCOLOR_NAME() throws RecognitionException {
      String current = null;

      AntlrDatatypeRuleToken iv_ruleCOLOR_NAME = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5786:2: (iv_ruleCOLOR_NAME= ruleCOLOR_NAME EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5787:2: iv_ruleCOLOR_NAME= ruleCOLOR_NAME EOF
         {
            newCompositeNode(grammarAccess.getCOLOR_NAMERule());
            pushFollow(FOLLOW_ruleCOLOR_NAME_in_entryRuleCOLOR_NAME12832);
            iv_ruleCOLOR_NAME = ruleCOLOR_NAME();

            state._fsp--;

            current = iv_ruleCOLOR_NAME.getText();
            match(input, EOF, FOLLOW_EOF_in_entryRuleCOLOR_NAME12843);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleCOLOR_NAME"

   // $ANTLR start "ruleCOLOR_NAME"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5794:1: ruleCOLOR_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
   public final AntlrDatatypeRuleToken ruleCOLOR_NAME() throws RecognitionException {
      AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

      Token this_STRING_0 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5797:28: (this_STRING_0= RULE_STRING )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5798:5: this_STRING_0= RULE_STRING
         {
            this_STRING_0 = (Token) match(input, RULE_STRING, FOLLOW_RULE_STRING_in_ruleCOLOR_NAME12882);

            current.merge(this_STRING_0);

            newLeafNode(this_STRING_0, grammarAccess.getCOLOR_NAMEAccess().getSTRINGTerminalRuleCall());

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleCOLOR_NAME"

   // $ANTLR start "entryRuleStateColor"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5813:1: entryRuleStateColor returns [String current=null] : iv_ruleStateColor= ruleStateColor EOF ;
   public final String entryRuleStateColor() throws RecognitionException {
      String current = null;

      AntlrDatatypeRuleToken iv_ruleStateColor = null;

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5814:2: (iv_ruleStateColor= ruleStateColor EOF )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5815:2: iv_ruleStateColor= ruleStateColor EOF
         {
            newCompositeNode(grammarAccess.getStateColorRule());
            pushFollow(FOLLOW_ruleStateColor_in_entryRuleStateColor12927);
            iv_ruleStateColor = ruleStateColor();

            state._fsp--;

            current = iv_ruleStateColor.getText();
            match(input, EOF, FOLLOW_EOF_in_entryRuleStateColor12938);

         }

      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "entryRuleStateColor"

   // $ANTLR start "ruleStateColor"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5822:1: ruleStateColor returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'BLACK' | kw= 'WHITE' | kw= 'RED' | kw= 'DARK_RED' | kw= 'GREEN' | kw= 'DARK_GREEN' | kw= 'YELLOW' | kw= 'DARK_YELLOW' | kw= 'BLUE' | kw= 'DARK_BLUE' | kw= 'MAGENTA' | kw= 'DARK_MAGENTA' | kw= 'CYAN' | kw= 'DARK_CYAN' | kw= 'GRAY' | kw= 'DARK_GRAY' | this_COLOR_NAME_16= ruleCOLOR_NAME ) ;
   public final AntlrDatatypeRuleToken ruleStateColor() throws RecognitionException {
      AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

      Token kw = null;
      AntlrDatatypeRuleToken this_COLOR_NAME_16 = null;

      enterRule();

      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5825:28: ( (kw= 'BLACK' | kw= 'WHITE' | kw= 'RED' | kw= 'DARK_RED' | kw= 'GREEN' | kw= 'DARK_GREEN' | kw= 'YELLOW' | kw= 'DARK_YELLOW' | kw= 'BLUE' | kw= 'DARK_BLUE' | kw= 'MAGENTA' | kw= 'DARK_MAGENTA' | kw= 'CYAN' | kw= 'DARK_CYAN' | kw= 'GRAY' | kw= 'DARK_GRAY' | this_COLOR_NAME_16= ruleCOLOR_NAME ) )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5826:1: (kw= 'BLACK' | kw= 'WHITE' | kw= 'RED' | kw= 'DARK_RED' | kw= 'GREEN' | kw= 'DARK_GREEN' | kw= 'YELLOW' | kw= 'DARK_YELLOW' | kw= 'BLUE' | kw= 'DARK_BLUE' | kw= 'MAGENTA' | kw= 'DARK_MAGENTA' | kw= 'CYAN' | kw= 'DARK_CYAN' | kw= 'GRAY' | kw= 'DARK_GRAY' | this_COLOR_NAME_16= ruleCOLOR_NAME )
         {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5826:1: (kw= 'BLACK' | kw= 'WHITE' | kw= 'RED' | kw= 'DARK_RED' | kw= 'GREEN' | kw= 'DARK_GREEN' | kw= 'YELLOW' | kw= 'DARK_YELLOW' | kw= 'BLUE' | kw= 'DARK_BLUE' | kw= 'MAGENTA' | kw= 'DARK_MAGENTA' | kw= 'CYAN' | kw= 'DARK_CYAN' | kw= 'GRAY' | kw= 'DARK_GRAY' | this_COLOR_NAME_16= ruleCOLOR_NAME )
            int alt136 = 17;
            switch (input.LA(1)) {
               case 139: {
                  alt136 = 1;
               }
                  break;
               case 140: {
                  alt136 = 2;
               }
                  break;
               case 141: {
                  alt136 = 3;
               }
                  break;
               case 142: {
                  alt136 = 4;
               }
                  break;
               case 143: {
                  alt136 = 5;
               }
                  break;
               case 144: {
                  alt136 = 6;
               }
                  break;
               case 145: {
                  alt136 = 7;
               }
                  break;
               case 146: {
                  alt136 = 8;
               }
                  break;
               case 147: {
                  alt136 = 9;
               }
                  break;
               case 148: {
                  alt136 = 10;
               }
                  break;
               case 149: {
                  alt136 = 11;
               }
                  break;
               case 150: {
                  alt136 = 12;
               }
                  break;
               case 151: {
                  alt136 = 13;
               }
                  break;
               case 152: {
                  alt136 = 14;
               }
                  break;
               case 153: {
                  alt136 = 15;
               }
                  break;
               case 154: {
                  alt136 = 16;
               }
                  break;
               case RULE_STRING: {
                  alt136 = 17;
               }
                  break;
               default:
                  NoViableAltException nvae = new NoViableAltException("", 136, 0, input);

                  throw nvae;
            }

            switch (alt136) {
               case 1:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5827:2: kw= 'BLACK'
               {
                  kw = (Token) match(input, 139, FOLLOW_139_in_ruleStateColor12976);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getStateColorAccess().getBLACKKeyword_0());

               }
                  break;
               case 2:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5834:2: kw= 'WHITE'
               {
                  kw = (Token) match(input, 140, FOLLOW_140_in_ruleStateColor12995);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getStateColorAccess().getWHITEKeyword_1());

               }
                  break;
               case 3:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5841:2: kw= 'RED'
               {
                  kw = (Token) match(input, 141, FOLLOW_141_in_ruleStateColor13014);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getStateColorAccess().getREDKeyword_2());

               }
                  break;
               case 4:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5848:2: kw= 'DARK_RED'
               {
                  kw = (Token) match(input, 142, FOLLOW_142_in_ruleStateColor13033);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getStateColorAccess().getDARK_REDKeyword_3());

               }
                  break;
               case 5:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5855:2: kw= 'GREEN'
               {
                  kw = (Token) match(input, 143, FOLLOW_143_in_ruleStateColor13052);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getStateColorAccess().getGREENKeyword_4());

               }
                  break;
               case 6:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5862:2: kw= 'DARK_GREEN'
               {
                  kw = (Token) match(input, 144, FOLLOW_144_in_ruleStateColor13071);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getStateColorAccess().getDARK_GREENKeyword_5());

               }
                  break;
               case 7:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5869:2: kw= 'YELLOW'
               {
                  kw = (Token) match(input, 145, FOLLOW_145_in_ruleStateColor13090);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getStateColorAccess().getYELLOWKeyword_6());

               }
                  break;
               case 8:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5876:2: kw= 'DARK_YELLOW'
               {
                  kw = (Token) match(input, 146, FOLLOW_146_in_ruleStateColor13109);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getStateColorAccess().getDARK_YELLOWKeyword_7());

               }
                  break;
               case 9:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5883:2: kw= 'BLUE'
               {
                  kw = (Token) match(input, 147, FOLLOW_147_in_ruleStateColor13128);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getStateColorAccess().getBLUEKeyword_8());

               }
                  break;
               case 10:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5890:2: kw= 'DARK_BLUE'
               {
                  kw = (Token) match(input, 148, FOLLOW_148_in_ruleStateColor13147);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getStateColorAccess().getDARK_BLUEKeyword_9());

               }
                  break;
               case 11:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5897:2: kw= 'MAGENTA'
               {
                  kw = (Token) match(input, 149, FOLLOW_149_in_ruleStateColor13166);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getStateColorAccess().getMAGENTAKeyword_10());

               }
                  break;
               case 12:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5904:2: kw= 'DARK_MAGENTA'
               {
                  kw = (Token) match(input, 150, FOLLOW_150_in_ruleStateColor13185);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getStateColorAccess().getDARK_MAGENTAKeyword_11());

               }
                  break;
               case 13:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5911:2: kw= 'CYAN'
               {
                  kw = (Token) match(input, 151, FOLLOW_151_in_ruleStateColor13204);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getStateColorAccess().getCYANKeyword_12());

               }
                  break;
               case 14:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5918:2: kw= 'DARK_CYAN'
               {
                  kw = (Token) match(input, 152, FOLLOW_152_in_ruleStateColor13223);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getStateColorAccess().getDARK_CYANKeyword_13());

               }
                  break;
               case 15:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5925:2: kw= 'GRAY'
               {
                  kw = (Token) match(input, 153, FOLLOW_153_in_ruleStateColor13242);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getStateColorAccess().getGRAYKeyword_14());

               }
                  break;
               case 16:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5932:2: kw= 'DARK_GRAY'
               {
                  kw = (Token) match(input, 154, FOLLOW_154_in_ruleStateColor13261);

                  current.merge(kw);
                  newLeafNode(kw, grammarAccess.getStateColorAccess().getDARK_GRAYKeyword_15());

               }
                  break;
               case 17:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5939:5: this_COLOR_NAME_16= ruleCOLOR_NAME
               {

                  newCompositeNode(grammarAccess.getStateColorAccess().getCOLOR_NAMEParserRuleCall_16());

                  pushFollow(FOLLOW_ruleCOLOR_NAME_in_ruleStateColor13289);
                  this_COLOR_NAME_16 = ruleCOLOR_NAME();

                  state._fsp--;

                  current.merge(this_COLOR_NAME_16);

                  afterParserOrEnumRuleCall();

               }
                  break;

            }

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleStateColor"

   // $ANTLR start "ruleOnEventType"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5957:1: ruleOnEventType returns [Enumerator current=null] : ( (enumLiteral_0= 'CreateBranch' ) | (enumLiteral_1= 'CommitBranch' ) | (enumLiteral_2= 'CreateWorkflow' ) | (enumLiteral_3= 'TransitionTo' ) | (enumLiteral_4= 'Manual' ) ) ;
   public final Enumerator ruleOnEventType() throws RecognitionException {
      Enumerator current = null;

      Token enumLiteral_0 = null;
      Token enumLiteral_1 = null;
      Token enumLiteral_2 = null;
      Token enumLiteral_3 = null;
      Token enumLiteral_4 = null;

      enterRule();
      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5959:28: ( ( (enumLiteral_0= 'CreateBranch' ) | (enumLiteral_1= 'CommitBranch' ) | (enumLiteral_2= 'CreateWorkflow' ) | (enumLiteral_3= 'TransitionTo' ) | (enumLiteral_4= 'Manual' ) ) )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5960:1: ( (enumLiteral_0= 'CreateBranch' ) | (enumLiteral_1= 'CommitBranch' ) | (enumLiteral_2= 'CreateWorkflow' ) | (enumLiteral_3= 'TransitionTo' ) | (enumLiteral_4= 'Manual' ) )
         {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5960:1: ( (enumLiteral_0= 'CreateBranch' ) | (enumLiteral_1= 'CommitBranch' ) | (enumLiteral_2= 'CreateWorkflow' ) | (enumLiteral_3= 'TransitionTo' ) | (enumLiteral_4= 'Manual' ) )
            int alt137 = 5;
            switch (input.LA(1)) {
               case 155: {
                  alt137 = 1;
               }
                  break;
               case 156: {
                  alt137 = 2;
               }
                  break;
               case 157: {
                  alt137 = 3;
               }
                  break;
               case 158: {
                  alt137 = 4;
               }
                  break;
               case 159: {
                  alt137 = 5;
               }
                  break;
               default:
                  NoViableAltException nvae = new NoViableAltException("", 137, 0, input);

                  throw nvae;
            }

            switch (alt137) {
               case 1:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5960:2: (enumLiteral_0= 'CreateBranch' )
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5960:2: (enumLiteral_0= 'CreateBranch' )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5960:4: enumLiteral_0= 'CreateBranch'
                  {
                     enumLiteral_0 = (Token) match(input, 155, FOLLOW_155_in_ruleOnEventType13348);

                     current =
                        grammarAccess.getOnEventTypeAccess().getCreateBranchEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                     newLeafNode(enumLiteral_0,
                        grammarAccess.getOnEventTypeAccess().getCreateBranchEnumLiteralDeclaration_0());

                  }

               }
                  break;
               case 2:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5966:6: (enumLiteral_1= 'CommitBranch' )
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5966:6: (enumLiteral_1= 'CommitBranch' )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5966:8: enumLiteral_1= 'CommitBranch'
                  {
                     enumLiteral_1 = (Token) match(input, 156, FOLLOW_156_in_ruleOnEventType13365);

                     current =
                        grammarAccess.getOnEventTypeAccess().getCommitBranchEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                     newLeafNode(enumLiteral_1,
                        grammarAccess.getOnEventTypeAccess().getCommitBranchEnumLiteralDeclaration_1());

                  }

               }
                  break;
               case 3:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5972:6: (enumLiteral_2= 'CreateWorkflow' )
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5972:6: (enumLiteral_2= 'CreateWorkflow' )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5972:8: enumLiteral_2= 'CreateWorkflow'
                  {
                     enumLiteral_2 = (Token) match(input, 157, FOLLOW_157_in_ruleOnEventType13382);

                     current =
                        grammarAccess.getOnEventTypeAccess().getCreateWorkflowEnumLiteralDeclaration_2().getEnumLiteral().getInstance();
                     newLeafNode(enumLiteral_2,
                        grammarAccess.getOnEventTypeAccess().getCreateWorkflowEnumLiteralDeclaration_2());

                  }

               }
                  break;
               case 4:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5978:6: (enumLiteral_3= 'TransitionTo' )
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5978:6: (enumLiteral_3= 'TransitionTo' )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5978:8: enumLiteral_3= 'TransitionTo'
                  {
                     enumLiteral_3 = (Token) match(input, 158, FOLLOW_158_in_ruleOnEventType13399);

                     current =
                        grammarAccess.getOnEventTypeAccess().getTransitionToEnumLiteralDeclaration_3().getEnumLiteral().getInstance();
                     newLeafNode(enumLiteral_3,
                        grammarAccess.getOnEventTypeAccess().getTransitionToEnumLiteralDeclaration_3());

                  }

               }
                  break;
               case 5:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5984:6: (enumLiteral_4= 'Manual' )
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5984:6: (enumLiteral_4= 'Manual' )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5984:8: enumLiteral_4= 'Manual'
                  {
                     enumLiteral_4 = (Token) match(input, 159, FOLLOW_159_in_ruleOnEventType13416);

                     current =
                        grammarAccess.getOnEventTypeAccess().getManualEnumLiteralDeclaration_4().getEnumLiteral().getInstance();
                     newLeafNode(enumLiteral_4,
                        grammarAccess.getOnEventTypeAccess().getManualEnumLiteralDeclaration_4());

                  }

               }
                  break;

            }

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleOnEventType"

   // $ANTLR start "ruleBooleanDef"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5994:1: ruleBooleanDef returns [Enumerator current=null] : ( (enumLiteral_0= 'None' ) | (enumLiteral_1= 'True' ) | (enumLiteral_2= 'False' ) ) ;
   public final Enumerator ruleBooleanDef() throws RecognitionException {
      Enumerator current = null;

      Token enumLiteral_0 = null;
      Token enumLiteral_1 = null;
      Token enumLiteral_2 = null;

      enterRule();
      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5996:28: ( ( (enumLiteral_0= 'None' ) | (enumLiteral_1= 'True' ) | (enumLiteral_2= 'False' ) ) )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5997:1: ( (enumLiteral_0= 'None' ) | (enumLiteral_1= 'True' ) | (enumLiteral_2= 'False' ) )
         {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5997:1: ( (enumLiteral_0= 'None' ) | (enumLiteral_1= 'True' ) | (enumLiteral_2= 'False' ) )
            int alt138 = 3;
            switch (input.LA(1)) {
               case 86: {
                  alt138 = 1;
               }
                  break;
               case 160: {
                  alt138 = 2;
               }
                  break;
               case 161: {
                  alt138 = 3;
               }
                  break;
               default:
                  NoViableAltException nvae = new NoViableAltException("", 138, 0, input);

                  throw nvae;
            }

            switch (alt138) {
               case 1:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5997:2: (enumLiteral_0= 'None' )
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5997:2: (enumLiteral_0= 'None' )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:5997:4: enumLiteral_0= 'None'
                  {
                     enumLiteral_0 = (Token) match(input, 86, FOLLOW_86_in_ruleBooleanDef13461);

                     current =
                        grammarAccess.getBooleanDefAccess().getNoneEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                     newLeafNode(enumLiteral_0, grammarAccess.getBooleanDefAccess().getNoneEnumLiteralDeclaration_0());

                  }

               }
                  break;
               case 2:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:6003:6: (enumLiteral_1= 'True' )
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:6003:6: (enumLiteral_1= 'True' )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:6003:8: enumLiteral_1= 'True'
                  {
                     enumLiteral_1 = (Token) match(input, 160, FOLLOW_160_in_ruleBooleanDef13478);

                     current =
                        grammarAccess.getBooleanDefAccess().getTrueEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                     newLeafNode(enumLiteral_1, grammarAccess.getBooleanDefAccess().getTrueEnumLiteralDeclaration_1());

                  }

               }
                  break;
               case 3:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:6009:6: (enumLiteral_2= 'False' )
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:6009:6: (enumLiteral_2= 'False' )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:6009:8: enumLiteral_2= 'False'
                  {
                     enumLiteral_2 = (Token) match(input, 161, FOLLOW_161_in_ruleBooleanDef13495);

                     current =
                        grammarAccess.getBooleanDefAccess().getFalseEnumLiteralDeclaration_2().getEnumLiteral().getInstance();
                     newLeafNode(enumLiteral_2, grammarAccess.getBooleanDefAccess().getFalseEnumLiteralDeclaration_2());

                  }

               }
                  break;

            }

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleBooleanDef"

   // $ANTLR start "ruleWorkflowEventType"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:6019:1: ruleWorkflowEventType returns [Enumerator current=null] : ( (enumLiteral_0= 'TransitionTo' ) | (enumLiteral_1= 'CreateBranch' ) | (enumLiteral_2= 'CommitBranch' ) ) ;
   public final Enumerator ruleWorkflowEventType() throws RecognitionException {
      Enumerator current = null;

      Token enumLiteral_0 = null;
      Token enumLiteral_1 = null;
      Token enumLiteral_2 = null;

      enterRule();
      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:6021:28: ( ( (enumLiteral_0= 'TransitionTo' ) | (enumLiteral_1= 'CreateBranch' ) | (enumLiteral_2= 'CommitBranch' ) ) )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:6022:1: ( (enumLiteral_0= 'TransitionTo' ) | (enumLiteral_1= 'CreateBranch' ) | (enumLiteral_2= 'CommitBranch' ) )
         {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:6022:1: ( (enumLiteral_0= 'TransitionTo' ) | (enumLiteral_1= 'CreateBranch' ) | (enumLiteral_2= 'CommitBranch' ) )
            int alt139 = 3;
            switch (input.LA(1)) {
               case 158: {
                  alt139 = 1;
               }
                  break;
               case 155: {
                  alt139 = 2;
               }
                  break;
               case 156: {
                  alt139 = 3;
               }
                  break;
               default:
                  NoViableAltException nvae = new NoViableAltException("", 139, 0, input);

                  throw nvae;
            }

            switch (alt139) {
               case 1:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:6022:2: (enumLiteral_0= 'TransitionTo' )
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:6022:2: (enumLiteral_0= 'TransitionTo' )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:6022:4: enumLiteral_0= 'TransitionTo'
                  {
                     enumLiteral_0 = (Token) match(input, 158, FOLLOW_158_in_ruleWorkflowEventType13540);

                     current =
                        grammarAccess.getWorkflowEventTypeAccess().getTransitionToEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                     newLeafNode(enumLiteral_0,
                        grammarAccess.getWorkflowEventTypeAccess().getTransitionToEnumLiteralDeclaration_0());

                  }

               }
                  break;
               case 2:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:6028:6: (enumLiteral_1= 'CreateBranch' )
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:6028:6: (enumLiteral_1= 'CreateBranch' )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:6028:8: enumLiteral_1= 'CreateBranch'
                  {
                     enumLiteral_1 = (Token) match(input, 155, FOLLOW_155_in_ruleWorkflowEventType13557);

                     current =
                        grammarAccess.getWorkflowEventTypeAccess().getCreateBranchEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                     newLeafNode(enumLiteral_1,
                        grammarAccess.getWorkflowEventTypeAccess().getCreateBranchEnumLiteralDeclaration_1());

                  }

               }
                  break;
               case 3:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:6034:6: (enumLiteral_2= 'CommitBranch' )
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:6034:6: (enumLiteral_2= 'CommitBranch' )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:6034:8: enumLiteral_2= 'CommitBranch'
                  {
                     enumLiteral_2 = (Token) match(input, 156, FOLLOW_156_in_ruleWorkflowEventType13574);

                     current =
                        grammarAccess.getWorkflowEventTypeAccess().getCommitBranchEnumLiteralDeclaration_2().getEnumLiteral().getInstance();
                     newLeafNode(enumLiteral_2,
                        grammarAccess.getWorkflowEventTypeAccess().getCommitBranchEnumLiteralDeclaration_2());

                  }

               }
                  break;

            }

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleWorkflowEventType"

   // $ANTLR start "ruleReviewBlockingType"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:6044:1: ruleReviewBlockingType returns [Enumerator current=null] : ( (enumLiteral_0= 'Transition' ) | (enumLiteral_1= 'Commit' ) ) ;
   public final Enumerator ruleReviewBlockingType() throws RecognitionException {
      Enumerator current = null;

      Token enumLiteral_0 = null;
      Token enumLiteral_1 = null;

      enterRule();
      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:6046:28: ( ( (enumLiteral_0= 'Transition' ) | (enumLiteral_1= 'Commit' ) ) )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:6047:1: ( (enumLiteral_0= 'Transition' ) | (enumLiteral_1= 'Commit' ) )
         {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:6047:1: ( (enumLiteral_0= 'Transition' ) | (enumLiteral_1= 'Commit' ) )
            int alt140 = 2;
            int LA140_0 = input.LA(1);

            if (LA140_0 == 162) {
               alt140 = 1;
            } else if (LA140_0 == 163) {
               alt140 = 2;
            } else {
               NoViableAltException nvae = new NoViableAltException("", 140, 0, input);

               throw nvae;
            }
            switch (alt140) {
               case 1:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:6047:2: (enumLiteral_0= 'Transition' )
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:6047:2: (enumLiteral_0= 'Transition' )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:6047:4: enumLiteral_0= 'Transition'
                  {
                     enumLiteral_0 = (Token) match(input, 162, FOLLOW_162_in_ruleReviewBlockingType13619);

                     current =
                        grammarAccess.getReviewBlockingTypeAccess().getTransitionEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                     newLeafNode(enumLiteral_0,
                        grammarAccess.getReviewBlockingTypeAccess().getTransitionEnumLiteralDeclaration_0());

                  }

               }
                  break;
               case 2:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:6053:6: (enumLiteral_1= 'Commit' )
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:6053:6: (enumLiteral_1= 'Commit' )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:6053:8: enumLiteral_1= 'Commit'
                  {
                     enumLiteral_1 = (Token) match(input, 163, FOLLOW_163_in_ruleReviewBlockingType13636);

                     current =
                        grammarAccess.getReviewBlockingTypeAccess().getCommitEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                     newLeafNode(enumLiteral_1,
                        grammarAccess.getReviewBlockingTypeAccess().getCommitEnumLiteralDeclaration_1());

                  }

               }
                  break;

            }

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleReviewBlockingType"

   // $ANTLR start "ruleRuleLocation"
   // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:6063:1: ruleRuleLocation returns [Enumerator current=null] : ( (enumLiteral_0= 'StateDefinition' ) | (enumLiteral_1= 'TeamDefinition' ) | (enumLiteral_2= 'ActionableItem' ) ) ;
   public final Enumerator ruleRuleLocation() throws RecognitionException {
      Enumerator current = null;

      Token enumLiteral_0 = null;
      Token enumLiteral_1 = null;
      Token enumLiteral_2 = null;

      enterRule();
      try {
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:6065:28: ( ( (enumLiteral_0= 'StateDefinition' ) | (enumLiteral_1= 'TeamDefinition' ) | (enumLiteral_2= 'ActionableItem' ) ) )
         // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:6066:1: ( (enumLiteral_0= 'StateDefinition' ) | (enumLiteral_1= 'TeamDefinition' ) | (enumLiteral_2= 'ActionableItem' ) )
         {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:6066:1: ( (enumLiteral_0= 'StateDefinition' ) | (enumLiteral_1= 'TeamDefinition' ) | (enumLiteral_2= 'ActionableItem' ) )
            int alt141 = 3;
            switch (input.LA(1)) {
               case 164: {
                  alt141 = 1;
               }
                  break;
               case 165: {
                  alt141 = 2;
               }
                  break;
               case 166: {
                  alt141 = 3;
               }
                  break;
               default:
                  NoViableAltException nvae = new NoViableAltException("", 141, 0, input);

                  throw nvae;
            }

            switch (alt141) {
               case 1:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:6066:2: (enumLiteral_0= 'StateDefinition' )
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:6066:2: (enumLiteral_0= 'StateDefinition' )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:6066:4: enumLiteral_0= 'StateDefinition'
                  {
                     enumLiteral_0 = (Token) match(input, 164, FOLLOW_164_in_ruleRuleLocation13681);

                     current =
                        grammarAccess.getRuleLocationAccess().getStateDefinitionEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                     newLeafNode(enumLiteral_0,
                        grammarAccess.getRuleLocationAccess().getStateDefinitionEnumLiteralDeclaration_0());

                  }

               }
                  break;
               case 2:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:6072:6: (enumLiteral_1= 'TeamDefinition' )
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:6072:6: (enumLiteral_1= 'TeamDefinition' )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:6072:8: enumLiteral_1= 'TeamDefinition'
                  {
                     enumLiteral_1 = (Token) match(input, 165, FOLLOW_165_in_ruleRuleLocation13698);

                     current =
                        grammarAccess.getRuleLocationAccess().getTeamDefinitionEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                     newLeafNode(enumLiteral_1,
                        grammarAccess.getRuleLocationAccess().getTeamDefinitionEnumLiteralDeclaration_1());

                  }

               }
                  break;
               case 3:
               // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:6078:6: (enumLiteral_2= 'ActionableItem' )
               {
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:6078:6: (enumLiteral_2= 'ActionableItem' )
                  // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:6078:8: enumLiteral_2= 'ActionableItem'
                  {
                     enumLiteral_2 = (Token) match(input, 166, FOLLOW_166_in_ruleRuleLocation13715);

                     current =
                        grammarAccess.getRuleLocationAccess().getActionableItemEnumLiteralDeclaration_2().getEnumLiteral().getInstance();
                     newLeafNode(enumLiteral_2,
                        grammarAccess.getRuleLocationAccess().getActionableItemEnumLiteralDeclaration_2());

                  }

               }
                  break;

            }

         }

         leaveRule();
      }

      catch (RecognitionException re) {
         recover(input, re);
         appendSkippedTokens();
      } finally {
      }
      return current;
   }
   // $ANTLR end "ruleRuleLocation"

   // Delegated rules

   protected DFA132 dfa132 = new DFA132(this);
   protected DFA133 dfa133 = new DFA133(this);
   static final String DFA132_eotS = "\51\uffff";
   static final String DFA132_eofS = "\51\uffff";
   static final String DFA132_minS =
      "\1\131\1\4\1\21\1\105\1\4\1\63\1\4\1\u00a4\1\132\3\106\2\4\1\u00a2" + "\1\uffff\1\4\1\107\2\110\2\4\1\22\1\u009b\1\126\2\4\1\126\1\106" + "\3\26\3\23\1\24\1\25\3\26\1\uffff";
   static final String DFA132_maxS =
      "\1\131\1\4\1\21\1\105\1\4\1\132\1\4\1\u00a6\1\132\3\133\2\4\1\u00a3" + "\1\uffff\1\133\1\115\2\110\2\133\1\26\1\u009e\1\u00a1\2\4\1\u00a1" + "\1\133\3\112\10\26\1\uffff";
   static final String DFA132_acceptS = "\17\uffff\1\2\30\uffff\1\1";
   static final String DFA132_specialS = "\51\uffff}>";
   static final String[] DFA132_transitionS = {
      "\1\1",
      "\1\2",
      "\1\3",
      "\1\4",
      "\1\5",
      "\1\6\46\uffff\1\7",
      "\1\10",
      "\1\11\1\12\1\13",
      "\1\7",
      "\1\15\1\16\5\uffff\1\17\14\uffff\1\7\1\14",
      "\1\15\1\16\5\uffff\1\17\14\uffff\1\7\1\14",
      "\1\15\1\16\5\uffff\1\17\14\uffff\1\7\1\14",
      "\1\20",
      "\1\21",
      "\1\22\1\23",
      "",
      "\1\25\14\uffff\1\26\64\uffff\1\15\1\16\5\uffff\1\17\7\uffff" + "\1\24\5\uffff\1\14",
      "\1\16\5\uffff\1\17",
      "\1\27",
      "\1\27",
      "\1\25\14\uffff\1\26\64\uffff\1\15\1\16\5\uffff\1\17\7\uffff" + "\1\24\5\uffff\1\14",
      "\1\25\14\uffff\1\26\64\uffff\1\15\1\16\5\uffff\1\17\7\uffff" + "\1\24\5\uffff\1\14",
      "\1\30\1\31\1\32\1\33\1\34",
      "\1\36\1\37\1\uffff\1\35",
      "\1\40\111\uffff\1\41\1\42",
      "\1\43",
      "\1\44",
      "\1\45\111\uffff\1\46\1\47",
      "\1\15\1\16\5\uffff\1\17\15\uffff\1\14",
      "\1\17\5\uffff\1\17\55\uffff\1\50",
      "\1\17\5\uffff\1\17\55\uffff\1\50",
      "\1\17\5\uffff\1\17\55\uffff\1\50",
      "\1\31\1\32\1\33\1\34",
      "\1\31\1\32\1\33\1\34",
      "\1\31\1\32\1\33\1\34",
      "\1\32\1\33\1\34",
      "\1\33\1\34",
      "\1\34",
      "\1\34",
      "\1\34",
      ""};

   static final short[] DFA132_eot = DFA.unpackEncodedString(DFA132_eotS);
   static final short[] DFA132_eof = DFA.unpackEncodedString(DFA132_eofS);
   static final char[] DFA132_min = DFA.unpackEncodedStringToUnsignedChars(DFA132_minS);
   static final char[] DFA132_max = DFA.unpackEncodedStringToUnsignedChars(DFA132_maxS);
   static final short[] DFA132_accept = DFA.unpackEncodedString(DFA132_acceptS);
   static final short[] DFA132_special = DFA.unpackEncodedString(DFA132_specialS);
   static final short[][] DFA132_transition;

   static {
      int numStates = DFA132_transitionS.length;
      DFA132_transition = new short[numStates][];
      for (int i = 0; i < numStates; i++) {
         DFA132_transition[i] = DFA.unpackEncodedString(DFA132_transitionS[i]);
      }
   }

   class DFA132 extends DFA {

      public DFA132(BaseRecognizer recognizer) {
         this.recognizer = recognizer;
         this.decisionNumber = 132;
         this.eot = DFA132_eot;
         this.eof = DFA132_eof;
         this.min = DFA132_min;
         this.max = DFA132_max;
         this.accept = DFA132_accept;
         this.special = DFA132_special;
         this.transition = DFA132_transition;
      }

      @Override
      public String getDescription() {
         return "5357:1: (this_CreateDecisionReviewRuleDef_0= ruleCreateDecisionReviewRuleDef | this_CreatePeerReviewRuleDef_1= ruleCreatePeerReviewRuleDef )";
      }
   }
   static final String DFA133_eotS = "\41\uffff";
   static final String DFA133_eofS = "\41\uffff";
   static final String DFA133_minS =
      "\1\131\1\4\1\21\1\105\1\4\1\63\1\4\1\u00a4\1\132\3\26\1\uffff\1" + "\4\2\uffff\3\4\1\22\1\126\2\4\1\126\1\26\3\23\1\24\1\25\3\26";
   static final String DFA133_maxS =
      "\1\131\1\4\1\21\1\105\1\4\1\132\1\4\1\u00a6\1\132\3\135\1\uffff" + "\1\4\2\uffff\3\135\1\26\1\u00a1\2\4\1\u00a1\1\135\10\26";
   static final String DFA133_acceptS = "\14\uffff\1\1\1\uffff\1\2\1\3\21\uffff";
   static final String DFA133_specialS = "\41\uffff}>";
   static final String[] DFA133_transitionS = {
      "\1\1",
      "\1\2",
      "\1\3",
      "\1\4",
      "\1\5",
      "\1\6\46\uffff\1\7",
      "\1\10",
      "\1\11\1\12\1\13",
      "\1\7",
      "\1\14\5\uffff\1\16\51\uffff\2\17\1\16\4\uffff\1\17\14\uffff" + "\1\7\1\15\2\16",
      "\1\14\5\uffff\1\16\51\uffff\2\17\1\16\4\uffff\1\17\14\uffff" + "\1\7\1\15\2\16",
      "\1\14\5\uffff\1\16\51\uffff\2\17\1\16\4\uffff\1\17\14\uffff" + "\1\7\1\15\2\16",
      "",
      "\1\20",
      "",
      "",
      "\1\22\14\uffff\1\23\4\uffff\1\16\5\uffff\1\16\51\uffff\2\17" + "\1\16\4\uffff\1\17\7\uffff\1\21\5\uffff\1\15\2\16",
      "\1\22\14\uffff\1\23\4\uffff\1\16\5\uffff\1\16\51\uffff\2\17" + "\1\16\4\uffff\1\17\7\uffff\1\21\5\uffff\1\15\2\16",
      "\1\22\14\uffff\1\23\4\uffff\1\16\5\uffff\1\16\51\uffff\2\17" + "\1\16\4\uffff\1\17\7\uffff\1\21\5\uffff\1\15\2\16",
      "\1\24\1\25\1\26\1\27\1\30",
      "\1\31\111\uffff\1\32\1\33",
      "\1\34",
      "\1\35",
      "\1\36\111\uffff\1\37\1\40",
      "\1\16\5\uffff\1\16\51\uffff\2\17\1\16\4\uffff\1\17\15\uffff" + "\1\15\2\16",
      "\1\25\1\26\1\27\1\30",
      "\1\25\1\26\1\27\1\30",
      "\1\25\1\26\1\27\1\30",
      "\1\26\1\27\1\30",
      "\1\27\1\30",
      "\1\30",
      "\1\30",
      "\1\30"};

   static final short[] DFA133_eot = DFA.unpackEncodedString(DFA133_eotS);
   static final short[] DFA133_eof = DFA.unpackEncodedString(DFA133_eofS);
   static final char[] DFA133_min = DFA.unpackEncodedStringToUnsignedChars(DFA133_minS);
   static final char[] DFA133_max = DFA.unpackEncodedStringToUnsignedChars(DFA133_maxS);
   static final short[] DFA133_accept = DFA.unpackEncodedString(DFA133_acceptS);
   static final short[] DFA133_special = DFA.unpackEncodedString(DFA133_specialS);
   static final short[][] DFA133_transition;

   static {
      int numStates = DFA133_transitionS.length;
      DFA133_transition = new short[numStates][];
      for (int i = 0; i < numStates; i++) {
         DFA133_transition[i] = DFA.unpackEncodedString(DFA133_transitionS[i]);
      }
   }

   class DFA133 extends DFA {

      public DFA133(BaseRecognizer recognizer) {
         this.recognizer = recognizer;
         this.decisionNumber = 133;
         this.eot = DFA133_eot;
         this.eof = DFA133_eof;
         this.min = DFA133_min;
         this.max = DFA133_max;
         this.accept = DFA133_accept;
         this.special = DFA133_special;
         this.transition = DFA133_transition;
      }

      @Override
      public String getDescription() {
         return "5397:1: (this_RuleDef_0= ruleRuleDef | this_CreateTaskRuleDef_1= ruleCreateTaskRuleDef | this_ReviewRule_2= ruleReviewRule )";
      }
   }

   public static final BitSet FOLLOW_ruleAtsDsl_in_entryRuleAtsDsl75 = new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleAtsDsl85 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_11_in_ruleAtsDsl124 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_ruleUserDef_in_ruleAtsDsl145 = new BitSet(new long[] {0x000000000000F802L});
   public static final BitSet FOLLOW_12_in_ruleAtsDsl160 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_ruleTeamDef_in_ruleAtsDsl181 = new BitSet(new long[] {0x000000000000F002L});
   public static final BitSet FOLLOW_13_in_ruleAtsDsl196 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_ruleActionableItemDef_in_ruleAtsDsl217 =
      new BitSet(new long[] {0x000000000000E002L});
   public static final BitSet FOLLOW_14_in_ruleAtsDsl232 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_ruleWorkDef_in_ruleAtsDsl253 = new BitSet(new long[] {0x000000000000C002L});
   public static final BitSet FOLLOW_15_in_ruleAtsDsl268 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_ruleProgramDef_in_ruleAtsDsl289 = new BitSet(new long[] {0x0000000000008002L});
   public static final BitSet FOLLOW_16_in_ruleAtsDsl311 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000002000000L});
   public static final BitSet FOLLOW_ruleRule_in_ruleAtsDsl332 = new BitSet(new long[] {0x0000000000010002L});
   public static final BitSet FOLLOW_ruleUSER_DEF_REFERENCE_in_entryRuleUSER_DEF_REFERENCE371 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleUSER_DEF_REFERENCE382 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleUSER_DEF_REFERENCE421 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleUserDef_in_entryRuleUserDef465 = new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleUserDef475 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleUSER_DEF_REFERENCE_in_ruleUserDef521 =
      new BitSet(new long[] {0x0000000000020012L, 0x0000000000200000L});
   public static final BitSet FOLLOW_ruleUserDefOption_in_ruleUserDef542 =
      new BitSet(new long[] {0x0000000000020012L, 0x0000000000200000L});
   public static final BitSet FOLLOW_17_in_ruleUserDef556 = new BitSet(new long[] {0x00000000007C0000L});
   public static final BitSet FOLLOW_18_in_ruleUserDef569 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000400000L, 0x0000000300000000L});
   public static final BitSet FOLLOW_ruleBooleanDef_in_ruleUserDef590 = new BitSet(new long[] {0x0000000000780000L});
   public static final BitSet FOLLOW_19_in_ruleUserDef605 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleUserDef622 = new BitSet(new long[] {0x0000000000700000L});
   public static final BitSet FOLLOW_20_in_ruleUserDef642 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleUserDef659 = new BitSet(new long[] {0x0000000000600000L});
   public static final BitSet FOLLOW_21_in_ruleUserDef679 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000400000L, 0x0000000300000000L});
   public static final BitSet FOLLOW_ruleBooleanDef_in_ruleUserDef700 = new BitSet(new long[] {0x0000000000400000L});
   public static final BitSet FOLLOW_22_in_ruleUserDef714 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleATTR_DEF_REFERENCE_in_entryRuleATTR_DEF_REFERENCE753 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleATTR_DEF_REFERENCE764 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleATTR_DEF_REFERENCE803 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleAttrDef_in_entryRuleAttrDef847 = new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleAttrDef857 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleATTR_DEF_REFERENCE_in_ruleAttrDef903 =
      new BitSet(new long[] {0x0000000000820000L});
   public static final BitSet FOLLOW_ruleAttrDefOptions_in_ruleAttrDef924 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleAttrDefOptions_in_entryRuleAttrDefOptions960 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleAttrDefOptions970 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleAttrValueDef_in_ruleAttrDefOptions1017 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleAttrFullDef_in_ruleAttrDefOptions1044 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleAttrValueDef_in_entryRuleAttrValueDef1079 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleAttrValueDef1089 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_23_in_ruleAttrValueDef1126 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleAttrValueDef1143 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleAttrFullDef_in_entryRuleAttrFullDef1184 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleAttrFullDef1194 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_17_in_ruleAttrFullDef1231 = new BitSet(new long[] {0x0000000001800000L});
   public static final BitSet FOLLOW_24_in_ruleAttrFullDef1244 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleAttrFullDef1261 = new BitSet(new long[] {0x0000000000800000L});
   public static final BitSet FOLLOW_23_in_ruleAttrFullDef1281 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleAttrFullDef1298 = new BitSet(new long[] {0x0000000000C00000L});
   public static final BitSet FOLLOW_22_in_ruleAttrFullDef1317 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_rulePROGRAM_REFERENCE_in_entryRulePROGRAM_REFERENCE1354 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRulePROGRAM_REFERENCE1365 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_RULE_STRING_in_rulePROGRAM_REFERENCE1404 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleProgramDef_in_entryRuleProgramDef1448 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleProgramDef1458 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_rulePROGRAM_REFERENCE_in_ruleProgramDef1504 =
      new BitSet(new long[] {0x0000000000020010L, 0x0000000000200000L});
   public static final BitSet FOLLOW_ruleProgramDefOption_in_ruleProgramDef1525 =
      new BitSet(new long[] {0x0000000000020010L, 0x0000000000200000L});
   public static final BitSet FOLLOW_17_in_ruleProgramDef1538 = new BitSet(new long[] {0x000000001F441000L});
   public static final BitSet FOLLOW_25_in_ruleProgramDef1551 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleProgramDef1568 = new BitSet(new long[] {0x000000001D441000L});
   public static final BitSet FOLLOW_24_in_ruleProgramDef1588 = new BitSet(new long[] {0x0000000000000020L});
   public static final BitSet FOLLOW_RULE_INT_in_ruleProgramDef1605 = new BitSet(new long[] {0x000000001C441000L});
   public static final BitSet FOLLOW_26_in_ruleProgramDef1625 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleProgramDef1642 = new BitSet(new long[] {0x0000000018441000L});
   public static final BitSet FOLLOW_18_in_ruleProgramDef1662 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000400000L, 0x0000000300000000L});
   public static final BitSet FOLLOW_ruleBooleanDef_in_ruleProgramDef1683 =
      new BitSet(new long[] {0x0000000018401000L});
   public static final BitSet FOLLOW_27_in_ruleProgramDef1698 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleProgramDef1715 = new BitSet(new long[] {0x0000000018401000L});
   public static final BitSet FOLLOW_12_in_ruleProgramDef1735 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_ruleTEAM_DEF_REFERENCE_in_ruleProgramDef1756 =
      new BitSet(new long[] {0x0000000010401000L});
   public static final BitSet FOLLOW_28_in_ruleProgramDef1771 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_ruleAttrDef_in_ruleProgramDef1792 = new BitSet(new long[] {0x0000000010400000L});
   public static final BitSet FOLLOW_22_in_ruleProgramDef1806 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleTEAM_DEF_REFERENCE_in_entryRuleTEAM_DEF_REFERENCE1843 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleTEAM_DEF_REFERENCE1854 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleTEAM_DEF_REFERENCE1893 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleTeamDef_in_entryRuleTeamDef1937 = new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleTeamDef1947 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleTEAM_DEF_REFERENCE_in_ruleTeamDef1993 =
      new BitSet(new long[] {0x0000000000020010L, 0x0000000000200000L});
   public static final BitSet FOLLOW_ruleTeamDefOption_in_ruleTeamDef2014 =
      new BitSet(new long[] {0x0000000000020010L, 0x0000000000200000L});
   public static final BitSet FOLLOW_17_in_ruleTeamDef2027 = new BitSet(new long[] {0x0000003FE3454000L});
   public static final BitSet FOLLOW_25_in_ruleTeamDef2040 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleTeamDef2057 = new BitSet(new long[] {0x0000003FE1454000L});
   public static final BitSet FOLLOW_24_in_ruleTeamDef2077 = new BitSet(new long[] {0x0000000000000020L});
   public static final BitSet FOLLOW_RULE_INT_in_ruleTeamDef2094 = new BitSet(new long[] {0x0000003FE0454000L});
   public static final BitSet FOLLOW_18_in_ruleTeamDef2114 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000400000L, 0x0000000300000000L});
   public static final BitSet FOLLOW_ruleBooleanDef_in_ruleTeamDef2135 = new BitSet(new long[] {0x0000003FE0414000L});
   public static final BitSet FOLLOW_29_in_ruleTeamDef2150 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleTeamDef2167 = new BitSet(new long[] {0x0000003FE0414000L});
   public static final BitSet FOLLOW_30_in_ruleTeamDef2187 =
      new BitSet(new long[] {0x0000000000080000L, 0x0000000000008000L});
   public static final BitSet FOLLOW_ruleUserRef_in_ruleTeamDef2208 = new BitSet(new long[] {0x0000003FC0414000L});
   public static final BitSet FOLLOW_31_in_ruleTeamDef2223 =
      new BitSet(new long[] {0x0000000000080000L, 0x0000000000008000L});
   public static final BitSet FOLLOW_ruleUserRef_in_ruleTeamDef2244 = new BitSet(new long[] {0x0000003F80414000L});
   public static final BitSet FOLLOW_32_in_ruleTeamDef2259 =
      new BitSet(new long[] {0x0000000000080000L, 0x0000000000008000L});
   public static final BitSet FOLLOW_ruleUserRef_in_ruleTeamDef2280 = new BitSet(new long[] {0x0000003F00414000L});
   public static final BitSet FOLLOW_14_in_ruleTeamDef2295 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleTeamDef2312 = new BitSet(new long[] {0x0000003E00410000L});
   public static final BitSet FOLLOW_33_in_ruleTeamDef2332 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleTeamDef2349 = new BitSet(new long[] {0x0000003C00410000L});
   public static final BitSet FOLLOW_34_in_ruleTeamDef2369 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleTeamDef2386 = new BitSet(new long[] {0x0000003800410000L});
   public static final BitSet FOLLOW_35_in_ruleTeamDef2406 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleTeamDef2423 = new BitSet(new long[] {0x0000003800410000L});
   public static final BitSet FOLLOW_36_in_ruleTeamDef2443 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_ruleVersionDef_in_ruleTeamDef2464 = new BitSet(new long[] {0x0000003000410000L});
   public static final BitSet FOLLOW_16_in_ruleTeamDef2479 =
      new BitSet(new long[] {0x0000000000000010L, 0x000003FFC0000000L});
   public static final BitSet FOLLOW_ruleRuleRef_in_ruleTeamDef2500 = new BitSet(new long[] {0x0000002000410000L});
   public static final BitSet FOLLOW_37_in_ruleTeamDef2515 = new BitSet(new long[] {0x0000000000020000L});
   public static final BitSet FOLLOW_17_in_ruleTeamDef2527 = new BitSet(new long[] {0x0000000000001000L});
   public static final BitSet FOLLOW_12_in_ruleTeamDef2540 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_ruleTeamDef_in_ruleTeamDef2561 = new BitSet(new long[] {0x0000000000401000L});
   public static final BitSet FOLLOW_22_in_ruleTeamDef2575 = new BitSet(new long[] {0x0000000000400000L});
   public static final BitSet FOLLOW_22_in_ruleTeamDef2589 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleAI_DEF_REFERENCE_in_entryRuleAI_DEF_REFERENCE2626 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleAI_DEF_REFERENCE2637 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleAI_DEF_REFERENCE2676 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleActionableItemDef_in_entryRuleActionableItemDef2720 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleActionableItemDef2730 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleAI_DEF_REFERENCE_in_ruleActionableItemDef2776 =
      new BitSet(new long[] {0x0000000000020012L, 0x0000000000200000L});
   public static final BitSet FOLLOW_ruleActionableItemOption_in_ruleActionableItemDef2797 =
      new BitSet(new long[] {0x0000000000020012L, 0x0000000000200000L});
   public static final BitSet FOLLOW_17_in_ruleActionableItemDef2811 = new BitSet(new long[] {0x000001E863450000L});
   public static final BitSet FOLLOW_25_in_ruleActionableItemDef2824 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleActionableItemDef2841 =
      new BitSet(new long[] {0x000001E861450000L});
   public static final BitSet FOLLOW_24_in_ruleActionableItemDef2861 = new BitSet(new long[] {0x0000000000000020L});
   public static final BitSet FOLLOW_RULE_INT_in_ruleActionableItemDef2878 =
      new BitSet(new long[] {0x000001E860450000L});
   public static final BitSet FOLLOW_18_in_ruleActionableItemDef2898 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000400000L, 0x0000000300000000L});
   public static final BitSet FOLLOW_ruleBooleanDef_in_ruleActionableItemDef2919 =
      new BitSet(new long[] {0x000001E860410000L});
   public static final BitSet FOLLOW_38_in_ruleActionableItemDef2934 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000400000L, 0x0000000300000000L});
   public static final BitSet FOLLOW_ruleBooleanDef_in_ruleActionableItemDef2955 =
      new BitSet(new long[] {0x000001A860410000L});
   public static final BitSet FOLLOW_30_in_ruleActionableItemDef2970 =
      new BitSet(new long[] {0x0000000000080000L, 0x0000000000008000L});
   public static final BitSet FOLLOW_ruleUserRef_in_ruleActionableItemDef2991 =
      new BitSet(new long[] {0x000001A860410000L});
   public static final BitSet FOLLOW_39_in_ruleActionableItemDef3006 =
      new BitSet(new long[] {0x0000000000080000L, 0x0000000000008000L});
   public static final BitSet FOLLOW_ruleUserRef_in_ruleActionableItemDef3027 =
      new BitSet(new long[] {0x000001A820410000L});
   public static final BitSet FOLLOW_29_in_ruleActionableItemDef3042 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleActionableItemDef3059 =
      new BitSet(new long[] {0x0000012820410000L});
   public static final BitSet FOLLOW_40_in_ruleActionableItemDef3079 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleActionableItemDef3096 =
      new BitSet(new long[] {0x0000002800410000L});
   public static final BitSet FOLLOW_35_in_ruleActionableItemDef3116 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleActionableItemDef3133 =
      new BitSet(new long[] {0x0000002800410000L});
   public static final BitSet FOLLOW_16_in_ruleActionableItemDef3153 =
      new BitSet(new long[] {0x0000000000000010L, 0x000003FFC0000000L});
   public static final BitSet FOLLOW_ruleRuleRef_in_ruleActionableItemDef3174 =
      new BitSet(new long[] {0x0000002000410000L});
   public static final BitSet FOLLOW_37_in_ruleActionableItemDef3189 = new BitSet(new long[] {0x0000000000020000L});
   public static final BitSet FOLLOW_17_in_ruleActionableItemDef3201 = new BitSet(new long[] {0x0000000000002000L});
   public static final BitSet FOLLOW_13_in_ruleActionableItemDef3214 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_ruleActionableItemDef_in_ruleActionableItemDef3235 =
      new BitSet(new long[] {0x0000000000402000L});
   public static final BitSet FOLLOW_22_in_ruleActionableItemDef3249 = new BitSet(new long[] {0x0000000000400000L});
   public static final BitSet FOLLOW_22_in_ruleActionableItemDef3263 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleVERSION_DEF_REFERENCE_in_entryRuleVERSION_DEF_REFERENCE3302 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleVERSION_DEF_REFERENCE3313 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleVERSION_DEF_REFERENCE3352 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleVersionDef_in_entryRuleVersionDef3396 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleVersionDef3406 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleVERSION_DEF_REFERENCE_in_ruleVersionDef3452 =
      new BitSet(new long[] {0x0000000000020000L});
   public static final BitSet FOLLOW_17_in_ruleVersionDef3464 = new BitSet(new long[] {0x00007E0023400000L});
   public static final BitSet FOLLOW_25_in_ruleVersionDef3477 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleVersionDef3494 = new BitSet(new long[] {0x00007E0021400000L});
   public static final BitSet FOLLOW_24_in_ruleVersionDef3514 = new BitSet(new long[] {0x0000000000000020L});
   public static final BitSet FOLLOW_RULE_INT_in_ruleVersionDef3531 = new BitSet(new long[] {0x00007E0020400000L});
   public static final BitSet FOLLOW_29_in_ruleVersionDef3551 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleVersionDef3568 = new BitSet(new long[] {0x00007E0020400000L});
   public static final BitSet FOLLOW_41_in_ruleVersionDef3588 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000400000L, 0x0000000300000000L});
   public static final BitSet FOLLOW_ruleBooleanDef_in_ruleVersionDef3609 =
      new BitSet(new long[] {0x00007C0000400000L});
   public static final BitSet FOLLOW_42_in_ruleVersionDef3624 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000400000L, 0x0000000300000000L});
   public static final BitSet FOLLOW_ruleBooleanDef_in_ruleVersionDef3645 =
      new BitSet(new long[] {0x0000780000400000L});
   public static final BitSet FOLLOW_43_in_ruleVersionDef3660 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000400000L, 0x0000000300000000L});
   public static final BitSet FOLLOW_ruleBooleanDef_in_ruleVersionDef3681 =
      new BitSet(new long[] {0x0000700000400000L});
   public static final BitSet FOLLOW_44_in_ruleVersionDef3696 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000400000L, 0x0000000300000000L});
   public static final BitSet FOLLOW_ruleBooleanDef_in_ruleVersionDef3717 =
      new BitSet(new long[] {0x0000600000400000L});
   public static final BitSet FOLLOW_45_in_ruleVersionDef3732 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleVersionDef3749 = new BitSet(new long[] {0x0000400000400000L});
   public static final BitSet FOLLOW_46_in_ruleVersionDef3769 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleVersionDef3786 = new BitSet(new long[] {0x0000400000400000L});
   public static final BitSet FOLLOW_22_in_ruleVersionDef3805 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleWorkDef_in_entryRuleWorkDef3841 = new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleWorkDef3851 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleWORK_DEFINITION_NAME_REFERENCE_in_ruleWorkDef3897 =
      new BitSet(new long[] {0x0000000000020000L});
   public static final BitSet FOLLOW_17_in_ruleWorkDef3909 = new BitSet(new long[] {0x0000800000000000L});
   public static final BitSet FOLLOW_47_in_ruleWorkDef3922 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleWorkDef3939 = new BitSet(new long[] {0x0001800000000000L});
   public static final BitSet FOLLOW_48_in_ruleWorkDef3959 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleWorkDef3982 =
      new BitSet(new long[] {0x2002000000000000L, 0x0000000000001010L});
   public static final BitSet FOLLOW_ruleWidgetDef_in_ruleWorkDef4004 =
      new BitSet(new long[] {0x2002000000000000L, 0x0000000000001010L});
   public static final BitSet FOLLOW_ruleDecisionReviewDef_in_ruleWorkDef4026 =
      new BitSet(new long[] {0x2002000000000000L, 0x0000000000001010L});
   public static final BitSet FOLLOW_rulePeerReviewDef_in_ruleWorkDef4048 =
      new BitSet(new long[] {0x2002000000000000L, 0x0000000000001010L});
   public static final BitSet FOLLOW_ruleStateDef_in_ruleWorkDef4070 =
      new BitSet(new long[] {0x2002000000400000L, 0x0000000000001010L});
   public static final BitSet FOLLOW_22_in_ruleWorkDef4083 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleWidgetDef_in_entryRuleWidgetDef4119 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleWidgetDef4129 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_49_in_ruleWidgetDef4166 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_ruleWIDGET_NAME_REFERENCE_in_ruleWidgetDef4187 =
      new BitSet(new long[] {0x0000000000020000L});
   public static final BitSet FOLLOW_17_in_ruleWidgetDef4199 = new BitSet(new long[] {0x03FC000000400000L});
   public static final BitSet FOLLOW_50_in_ruleWidgetDef4212 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleWidgetDef4229 = new BitSet(new long[] {0x03F8000000400000L});
   public static final BitSet FOLLOW_51_in_ruleWidgetDef4249 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleWidgetDef4266 = new BitSet(new long[] {0x03F0000000400000L});
   public static final BitSet FOLLOW_52_in_ruleWidgetDef4286 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleWidgetDef4303 = new BitSet(new long[] {0x03E0000000400000L});
   public static final BitSet FOLLOW_53_in_ruleWidgetDef4323 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleWidgetDef4340 = new BitSet(new long[] {0x03C0000000400000L});
   public static final BitSet FOLLOW_54_in_ruleWidgetDef4360 = new BitSet(new long[] {0x0000000000000020L});
   public static final BitSet FOLLOW_RULE_INT_in_ruleWidgetDef4377 = new BitSet(new long[] {0x0380000000400000L});
   public static final BitSet FOLLOW_55_in_ruleWidgetDef4397 =
      new BitSet(new long[] {0x0000000000000010L, 0xFFFFFC0000000000L, 0x00000000000000FFL});
   public static final BitSet FOLLOW_ruleWidgetOption_in_ruleWidgetDef4418 =
      new BitSet(new long[] {0x0380000000400000L});
   public static final BitSet FOLLOW_56_in_ruleWidgetDef4433 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleWidgetDef4450 = new BitSet(new long[] {0x0200000000400000L});
   public static final BitSet FOLLOW_57_in_ruleWidgetDef4470 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleWidgetDef4487 = new BitSet(new long[] {0x0000000000400000L});
   public static final BitSet FOLLOW_22_in_ruleWidgetDef4506 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleWidgetRef_in_entryRuleWidgetRef4542 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleWidgetRef4552 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_58_in_ruleWidgetRef4589 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_ruleWIDGET_NAME_REFERENCE_in_ruleWidgetRef4612 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleAttrWidget_in_entryRuleAttrWidget4648 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleAttrWidget4658 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_59_in_ruleAttrWidget4695 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleAttrWidget4712 = new BitSet(new long[] {0x1000000000000002L});
   public static final BitSet FOLLOW_60_in_ruleAttrWidget4730 =
      new BitSet(new long[] {0x0000000000000010L, 0xFFFFFC0000000000L, 0x00000000000000FFL});
   public static final BitSet FOLLOW_ruleWidgetOption_in_ruleAttrWidget4751 =
      new BitSet(new long[] {0x1000000000000002L});
   public static final BitSet FOLLOW_ruleStateDef_in_entryRuleStateDef4789 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleStateDef4799 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_61_in_ruleStateDef4836 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleStateDef4857 =
      new BitSet(new long[] {0x0000000000020000L});
   public static final BitSet FOLLOW_17_in_ruleStateDef4869 = new BitSet(new long[] {0x4008000000000000L});
   public static final BitSet FOLLOW_51_in_ruleStateDef4882 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleStateDef4899 = new BitSet(new long[] {0x4000000000000000L});
   public static final BitSet FOLLOW_62_in_ruleStateDef4918 =
      new BitSet(new long[] {0x0000000000000010L, 0x0000000000000000L, 0x0000000000000700L});
   public static final BitSet FOLLOW_rulePageType_in_ruleStateDef4939 = new BitSet(new long[] {0x8000000000000000L});
   public static final BitSet FOLLOW_63_in_ruleStateDef4951 = new BitSet(new long[] {0x0000000000000020L});
   public static final BitSet FOLLOW_RULE_INT_in_ruleStateDef4968 =
      new BitSet(new long[] {0x0000000000410000L, 0x000000000007080FL});
   public static final BitSet FOLLOW_ruleToState_in_ruleStateDef4994 =
      new BitSet(new long[] {0x0000000000410000L, 0x000000000007080FL});
   public static final BitSet FOLLOW_16_in_ruleStateDef5008 =
      new BitSet(new long[] {0x0000000000000010L, 0x000003FFC0000000L});
   public static final BitSet FOLLOW_ruleRuleRef_in_ruleStateDef5029 =
      new BitSet(new long[] {0x0000000000410000L, 0x000000000006080FL});
   public static final BitSet FOLLOW_ruleDecisionReviewRef_in_ruleStateDef5052 =
      new BitSet(new long[] {0x0000000000400000L, 0x000000000006080FL});
   public static final BitSet FOLLOW_rulePeerReviewRef_in_ruleStateDef5074 =
      new BitSet(new long[] {0x0000000000400000L, 0x0000000000060807L});
   public static final BitSet FOLLOW_64_in_ruleStateDef5088 = new BitSet(new long[] {0x0000000000000020L});
   public static final BitSet FOLLOW_RULE_INT_in_ruleStateDef5105 =
      new BitSet(new long[] {0x0000000000400000L, 0x0000000000060006L});
   public static final BitSet FOLLOW_65_in_ruleStateDef5125 = new BitSet(new long[] {0x0000000000000020L});
   public static final BitSet FOLLOW_RULE_INT_in_ruleStateDef5142 =
      new BitSet(new long[] {0x0000000000400000L, 0x0000000000060004L});
   public static final BitSet FOLLOW_66_in_ruleStateDef5162 =
      new BitSet(new long[] {0x0000000000000010L, 0x0000000000000000L, 0x0000000007FFF800L});
   public static final BitSet FOLLOW_ruleStateColor_in_ruleStateDef5183 =
      new BitSet(new long[] {0x0000000000400000L, 0x0000000000060000L});
   public static final BitSet FOLLOW_ruleLayoutType_in_ruleStateDef5206 = new BitSet(new long[] {0x0000000000400000L});
   public static final BitSet FOLLOW_22_in_ruleStateDef5219 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleDecisionReviewRef_in_entryRuleDecisionReviewRef5255 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleDecisionReviewRef5265 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_67_in_ruleDecisionReviewRef5302 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_ruleDECISION_REVIEW_NAME_REFERENCE_in_ruleDecisionReviewRef5325 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleDecisionReviewDef_in_entryRuleDecisionReviewDef5361 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleDecisionReviewDef5371 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_68_in_ruleDecisionReviewDef5408 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_ruleDECISION_REVIEW_NAME_REFERENCE_in_ruleDecisionReviewDef5429 =
      new BitSet(new long[] {0x0000000000020000L});
   public static final BitSet FOLLOW_17_in_ruleDecisionReviewDef5441 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000000020L});
   public static final BitSet FOLLOW_69_in_ruleDecisionReviewDef5453 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleDecisionReviewDef5470 =
      new BitSet(new long[] {0x0008000000000000L});
   public static final BitSet FOLLOW_51_in_ruleDecisionReviewDef5487 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleDecisionReviewDef5504 =
      new BitSet(new long[] {0x0000000000000000L, 0x00000000000000C0L});
   public static final BitSet FOLLOW_70_in_ruleDecisionReviewDef5522 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleDecisionReviewDef5545 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000000080L});
   public static final BitSet FOLLOW_71_in_ruleDecisionReviewDef5559 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0000000C00000000L});
   public static final BitSet FOLLOW_ruleReviewBlockingType_in_ruleDecisionReviewDef5580 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000000100L});
   public static final BitSet FOLLOW_72_in_ruleDecisionReviewDef5592 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0000000058000000L});
   public static final BitSet FOLLOW_ruleWorkflowEventType_in_ruleDecisionReviewDef5613 =
      new BitSet(new long[] {0x0080000000000000L, 0x0000000000000600L});
   public static final BitSet FOLLOW_73_in_ruleDecisionReviewDef5626 =
      new BitSet(new long[] {0x0000000000080000L, 0x0000000000008000L});
   public static final BitSet FOLLOW_ruleUserRef_in_ruleDecisionReviewDef5647 =
      new BitSet(new long[] {0x0080000000000000L, 0x0000000000000600L});
   public static final BitSet FOLLOW_74_in_ruleDecisionReviewDef5662 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000400000L, 0x0000000300000000L});
   public static final BitSet FOLLOW_ruleBooleanDef_in_ruleDecisionReviewDef5683 =
      new BitSet(new long[] {0x0080000000000000L, 0x0000000000000600L});
   public static final BitSet FOLLOW_ruleDecisionReviewOpt_in_ruleDecisionReviewDef5706 =
      new BitSet(new long[] {0x0080000000400000L, 0x0000000000000600L});
   public static final BitSet FOLLOW_22_in_ruleDecisionReviewDef5719 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleDECISION_REVIEW_OPT_REF_in_entryRuleDECISION_REVIEW_OPT_REF5756 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleDECISION_REVIEW_OPT_REF5767 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleDECISION_REVIEW_OPT_REF5806 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleDecisionReviewOpt_in_entryRuleDecisionReviewOpt5850 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleDecisionReviewOpt5860 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_55_in_ruleDecisionReviewOpt5897 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_ruleDECISION_REVIEW_OPT_REF_in_ruleDecisionReviewOpt5918 =
      new BitSet(new long[] {0x0000000000000002L, 0x0000000000004000L});
   public static final BitSet FOLLOW_ruleFollowupRef_in_ruleDecisionReviewOpt5939 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_rulePeerReviewRef_in_entryRulePeerReviewRef5976 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRulePeerReviewRef5986 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_75_in_rulePeerReviewRef6023 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_rulePEER_REVIEW_NAME_REFERENCE_in_rulePeerReviewRef6046 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_rulePeerReviewDef_in_entryRulePeerReviewDef6082 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRulePeerReviewDef6092 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_76_in_rulePeerReviewDef6129 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_rulePEER_REVIEW_NAME_REFERENCE_in_rulePeerReviewDef6150 =
      new BitSet(new long[] {0x0000000000020000L});
   public static final BitSet FOLLOW_17_in_rulePeerReviewDef6162 =
      new BitSet(new long[] {0x0008000000000000L, 0x0000000000000020L});
   public static final BitSet FOLLOW_69_in_rulePeerReviewDef6175 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_RULE_STRING_in_rulePeerReviewDef6192 =
      new BitSet(new long[] {0x0008000000000000L});
   public static final BitSet FOLLOW_51_in_rulePeerReviewDef6211 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_RULE_STRING_in_rulePeerReviewDef6228 =
      new BitSet(new long[] {0x0000000000000000L, 0x00000000000020C0L});
   public static final BitSet FOLLOW_77_in_rulePeerReviewDef6246 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_RULE_STRING_in_rulePeerReviewDef6263 =
      new BitSet(new long[] {0x0000000000000000L, 0x00000000000000C0L});
   public static final BitSet FOLLOW_70_in_rulePeerReviewDef6283 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_ruleSTATE_NAME_REFERENCE_in_rulePeerReviewDef6306 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000000080L});
   public static final BitSet FOLLOW_71_in_rulePeerReviewDef6320 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0000000C00000000L});
   public static final BitSet FOLLOW_ruleReviewBlockingType_in_rulePeerReviewDef6341 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000000100L});
   public static final BitSet FOLLOW_72_in_rulePeerReviewDef6353 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0000000058000000L});
   public static final BitSet FOLLOW_ruleWorkflowEventType_in_rulePeerReviewDef6374 =
      new BitSet(new long[] {0x0000000000400000L, 0x0000000000000200L});
   public static final BitSet FOLLOW_73_in_rulePeerReviewDef6387 =
      new BitSet(new long[] {0x0000000000080000L, 0x0000000000008000L});
   public static final BitSet FOLLOW_ruleUserRef_in_rulePeerReviewDef6408 =
      new BitSet(new long[] {0x0000000000400000L, 0x0000000000000200L});
   public static final BitSet FOLLOW_22_in_rulePeerReviewDef6422 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleFollowupRef_in_entryRuleFollowupRef6458 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleFollowupRef6468 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_78_in_ruleFollowupRef6505 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000000200L});
   public static final BitSet FOLLOW_73_in_ruleFollowupRef6518 =
      new BitSet(new long[] {0x0000000000080000L, 0x0000000000008000L});
   public static final BitSet FOLLOW_ruleUserRef_in_ruleFollowupRef6539 =
      new BitSet(new long[] {0x0000000000000002L, 0x0000000000000200L});
   public static final BitSet FOLLOW_ruleUserRef_in_entryRuleUserRef6577 = new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleUserRef6587 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleUserByUserId_in_ruleUserRef6634 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleUserByName_in_ruleUserRef6661 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleUserByUserId_in_entryRuleUserByUserId6696 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleUserByUserId6706 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_19_in_ruleUserByUserId6743 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleUserByUserId6760 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleUserByName_in_entryRuleUserByName6801 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleUserByName6811 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_79_in_ruleUserByName6848 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleUserByName6865 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleDECISION_REVIEW_NAME_REFERENCE_in_entryRuleDECISION_REVIEW_NAME_REFERENCE6907 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleDECISION_REVIEW_NAME_REFERENCE6918 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleDECISION_REVIEW_NAME_REFERENCE6957 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_rulePEER_REVIEW_NAME_REFERENCE_in_entryRulePEER_REVIEW_NAME_REFERENCE7002 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRulePEER_REVIEW_NAME_REFERENCE7013 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_RULE_STRING_in_rulePEER_REVIEW_NAME_REFERENCE7052 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleSTATE_NAME_REFERENCE_in_entryRuleSTATE_NAME_REFERENCE7097 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleSTATE_NAME_REFERENCE7108 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleSTATE_NAME_REFERENCE7147 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleWIDGET_NAME_REFERENCE_in_entryRuleWIDGET_NAME_REFERENCE7192 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleWIDGET_NAME_REFERENCE7203 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleWIDGET_NAME_REFERENCE7242 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleWORK_DEFINITION_NAME_REFERENCE_in_entryRuleWORK_DEFINITION_NAME_REFERENCE7287 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleWORK_DEFINITION_NAME_REFERENCE7298 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleWORK_DEFINITION_NAME_REFERENCE7337 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleToState_in_entryRuleToState7381 = new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleToState7391 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_80_in_ruleToState7428 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleToState7451 =
      new BitSet(new long[] {0x0000000000000012L, 0x0000000001800000L});
   public static final BitSet FOLLOW_ruleTransitionOption_in_ruleToState7472 =
      new BitSet(new long[] {0x0000000000000012L, 0x0000000001800000L});
   public static final BitSet FOLLOW_ruleLayoutType_in_entryRuleLayoutType7509 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleLayoutType7519 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleLayoutDef_in_ruleLayoutType7566 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleLayoutCopy_in_ruleLayoutType7593 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleLayoutDef_in_entryRuleLayoutDef7628 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleLayoutDef7638 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_81_in_ruleLayoutDef7675 = new BitSet(new long[] {0x0000000000020000L});
   public static final BitSet FOLLOW_17_in_ruleLayoutDef7687 =
      new BitSet(new long[] {0x0C00000000000000L, 0x0000000000080000L});
   public static final BitSet FOLLOW_ruleLayoutItem_in_ruleLayoutDef7708 =
      new BitSet(new long[] {0x0C00000000400000L, 0x0000000000080000L});
   public static final BitSet FOLLOW_22_in_ruleLayoutDef7721 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleLayoutCopy_in_entryRuleLayoutCopy7757 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleLayoutCopy7767 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_82_in_ruleLayoutCopy7804 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleLayoutCopy7827 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleLayoutItem_in_entryRuleLayoutItem7863 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleLayoutItem7873 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleWidgetRef_in_ruleLayoutItem7920 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleAttrWidget_in_ruleLayoutItem7947 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleComposite_in_ruleLayoutItem7974 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleComposite_in_entryRuleComposite8009 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleComposite8019 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_83_in_ruleComposite8056 = new BitSet(new long[] {0x0000000000020000L});
   public static final BitSet FOLLOW_17_in_ruleComposite8068 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000100000L});
   public static final BitSet FOLLOW_84_in_ruleComposite8080 = new BitSet(new long[] {0x0000000000000020L});
   public static final BitSet FOLLOW_RULE_INT_in_ruleComposite8097 =
      new BitSet(new long[] {0x0C00000000000000L, 0x0000000000080000L});
   public static final BitSet FOLLOW_ruleLayoutItem_in_ruleComposite8123 =
      new BitSet(new long[] {0x0C80000000400000L, 0x0000000000080000L});
   public static final BitSet FOLLOW_55_in_ruleComposite8137 =
      new BitSet(new long[] {0x0000000000000010L, 0x0000000000400000L});
   public static final BitSet FOLLOW_ruleCompositeOption_in_ruleComposite8158 =
      new BitSet(new long[] {0x0080000000400000L});
   public static final BitSet FOLLOW_22_in_ruleComposite8172 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleUSER_DEF_OPTION_NAME_in_entryRuleUSER_DEF_OPTION_NAME8209 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleUSER_DEF_OPTION_NAME8220 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleUSER_DEF_OPTION_NAME8259 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleUserDefOption_in_entryRuleUserDefOption8304 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleUserDefOption8315 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_85_in_ruleUserDefOption8353 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleUSER_DEF_OPTION_NAME_in_ruleUserDefOption8381 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_rulePROGRAM_DEF_OPTION_NAME_in_entryRulePROGRAM_DEF_OPTION_NAME8427 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRulePROGRAM_DEF_OPTION_NAME8438 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_RULE_STRING_in_rulePROGRAM_DEF_OPTION_NAME8477 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleProgramDefOption_in_entryRuleProgramDefOption8522 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleProgramDefOption8533 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_85_in_ruleProgramDefOption8571 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_rulePROGRAM_DEF_OPTION_NAME_in_ruleProgramDefOption8599 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleTEAM_DEF_OPTION_NAME_in_entryRuleTEAM_DEF_OPTION_NAME8645 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleTEAM_DEF_OPTION_NAME8656 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleTEAM_DEF_OPTION_NAME8695 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleTeamDefOption_in_entryRuleTeamDefOption8740 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleTeamDefOption8751 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_85_in_ruleTeamDefOption8789 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleTEAM_DEF_OPTION_NAME_in_ruleTeamDefOption8817 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleAI_DEF_OPTION_NAME_in_entryRuleAI_DEF_OPTION_NAME8863 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleAI_DEF_OPTION_NAME8874 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleAI_DEF_OPTION_NAME8913 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleActionableItemOption_in_entryRuleActionableItemOption8958 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleActionableItemOption8969 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_85_in_ruleActionableItemOption9007 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleAI_DEF_OPTION_NAME_in_ruleActionableItemOption9035 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleCOMPOSITE_OPTION_NAME_in_entryRuleCOMPOSITE_OPTION_NAME9081 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleCOMPOSITE_OPTION_NAME9092 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleCOMPOSITE_OPTION_NAME9131 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleCompositeOption_in_entryRuleCompositeOption9176 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleCompositeOption9187 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_86_in_ruleCompositeOption9225 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleCOMPOSITE_OPTION_NAME_in_ruleCompositeOption9253 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleTRANSITION_OPTION_NAME_in_entryRuleTRANSITION_OPTION_NAME9299 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleTRANSITION_OPTION_NAME9310 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleTRANSITION_OPTION_NAME9349 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleTransitionOption_in_entryRuleTransitionOption9394 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleTransitionOption9405 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_87_in_ruleTransitionOption9443 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_88_in_ruleTransitionOption9462 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleTRANSITION_OPTION_NAME_in_ruleTransitionOption9490 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleRULE_NAME_REFERENCE_in_entryRuleRULE_NAME_REFERENCE9536 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleRULE_NAME_REFERENCE9547 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleRULE_NAME_REFERENCE9586 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleRuleDef_in_entryRuleRuleDef9630 = new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleRuleDef9640 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_89_in_ruleRuleDef9678 =
      new BitSet(new long[] {0x0000000000000010L, 0x000003FFC0000000L});
   public static final BitSet FOLLOW_ruleRULE_NAME_REFERENCE_in_ruleRuleDef9699 =
      new BitSet(new long[] {0x0000000000020000L});
   public static final BitSet FOLLOW_17_in_ruleRuleDef9712 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000000020L});
   public static final BitSet FOLLOW_69_in_ruleRuleDef9725 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleRuleDef9742 =
      new BitSet(new long[] {0x0008000000000000L, 0x0000000004000000L});
   public static final BitSet FOLLOW_51_in_ruleRuleDef9761 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleRuleDef9778 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000004000000L});
   public static final BitSet FOLLOW_90_in_ruleRuleDef9798 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0000007000000000L});
   public static final BitSet FOLLOW_ruleRuleLocation_in_ruleRuleDef9819 =
      new BitSet(new long[] {0x0000000000400000L, 0x0000000004000000L});
   public static final BitSet FOLLOW_22_in_ruleRuleDef9833 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleCreateTaskRuleDef_in_entryRuleCreateTaskRuleDef9869 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleCreateTaskRuleDef9879 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_89_in_ruleCreateTaskRuleDef9917 =
      new BitSet(new long[] {0x0000000000000010L, 0x000003FFC0000000L});
   public static final BitSet FOLLOW_ruleRULE_NAME_REFERENCE_in_ruleCreateTaskRuleDef9938 =
      new BitSet(new long[] {0x0000000000020000L});
   public static final BitSet FOLLOW_17_in_ruleCreateTaskRuleDef9951 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000000020L});
   public static final BitSet FOLLOW_69_in_ruleCreateTaskRuleDef9964 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleCreateTaskRuleDef9981 =
      new BitSet(new long[] {0x0008000000000000L, 0x0000000004000000L});
   public static final BitSet FOLLOW_51_in_ruleCreateTaskRuleDef10000 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleCreateTaskRuleDef10017 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000004000000L});
   public static final BitSet FOLLOW_90_in_ruleCreateTaskRuleDef10037 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0000007000000000L});
   public static final BitSet FOLLOW_ruleRuleLocation_in_ruleCreateTaskRuleDef10058 =
      new BitSet(new long[] {0x0000000010400000L, 0x000000003C000100L});
   public static final BitSet FOLLOW_91_in_ruleCreateTaskRuleDef10073 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_ruleUserDef_in_ruleCreateTaskRuleDef10094 =
      new BitSet(new long[] {0x0000000010400000L, 0x0000000038000100L});
   public static final BitSet FOLLOW_92_in_ruleCreateTaskRuleDef10109 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleCreateTaskRuleDef10130 =
      new BitSet(new long[] {0x0000000010400000L, 0x0000000020000100L});
   public static final BitSet FOLLOW_93_in_ruleCreateTaskRuleDef10145 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleCreateTaskRuleDef10162 =
      new BitSet(new long[] {0x0000000010400000L, 0x0000000000000100L});
   public static final BitSet FOLLOW_72_in_ruleCreateTaskRuleDef10182 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000000000L, 0x00000000F8000000L});
   public static final BitSet FOLLOW_ruleOnEventType_in_ruleCreateTaskRuleDef10203 =
      new BitSet(new long[] {0x0000000010400000L, 0x0000000000000100L});
   public static final BitSet FOLLOW_28_in_ruleCreateTaskRuleDef10218 = new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_ruleAttrDef_in_ruleCreateTaskRuleDef10239 =
      new BitSet(new long[] {0x0000000010400000L});
   public static final BitSet FOLLOW_22_in_ruleCreateTaskRuleDef10253 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleCreateDecisionReviewRuleDef_in_entryRuleCreateDecisionReviewRuleDef10289 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleCreateDecisionReviewRuleDef10299 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_89_in_ruleCreateDecisionReviewRuleDef10337 =
      new BitSet(new long[] {0x0000000000000010L, 0x000003FFC0000000L});
   public static final BitSet FOLLOW_ruleRULE_NAME_REFERENCE_in_ruleCreateDecisionReviewRuleDef10358 =
      new BitSet(new long[] {0x0000000000020000L});
   public static final BitSet FOLLOW_17_in_ruleCreateDecisionReviewRuleDef10371 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000000020L});
   public static final BitSet FOLLOW_69_in_ruleCreateDecisionReviewRuleDef10384 =
      new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleCreateDecisionReviewRuleDef10401 =
      new BitSet(new long[] {0x0008000000000000L, 0x0000000004000000L});
   public static final BitSet FOLLOW_51_in_ruleCreateDecisionReviewRuleDef10420 =
      new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleCreateDecisionReviewRuleDef10437 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000004000000L});
   public static final BitSet FOLLOW_90_in_ruleCreateDecisionReviewRuleDef10457 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0000007000000000L});
   public static final BitSet FOLLOW_ruleRuleLocation_in_ruleCreateDecisionReviewRuleDef10478 =
      new BitSet(new long[] {0x0000000000000000L, 0x000000000C0000C0L});
   public static final BitSet FOLLOW_91_in_ruleCreateDecisionReviewRuleDef10493 =
      new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_ruleUserDef_in_ruleCreateDecisionReviewRuleDef10514 =
      new BitSet(new long[] {0x0000000000000000L, 0x00000000080000C0L});
   public static final BitSet FOLLOW_70_in_ruleCreateDecisionReviewRuleDef10529 =
      new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleCreateDecisionReviewRuleDef10550 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000000080L});
   public static final BitSet FOLLOW_71_in_ruleCreateDecisionReviewRuleDef10564 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0000000C00000000L});
   public static final BitSet FOLLOW_ruleReviewBlockingType_in_ruleCreateDecisionReviewRuleDef10585 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000000100L});
   public static final BitSet FOLLOW_72_in_ruleCreateDecisionReviewRuleDef10597 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0000000058000000L});
   public static final BitSet FOLLOW_ruleWorkflowEventType_in_ruleCreateDecisionReviewRuleDef10618 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000000400L});
   public static final BitSet FOLLOW_74_in_ruleCreateDecisionReviewRuleDef10631 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000400000L, 0x0000000300000000L});
   public static final BitSet FOLLOW_ruleBooleanDef_in_ruleCreateDecisionReviewRuleDef10652 =
      new BitSet(new long[] {0x0080000000000000L, 0x0000000000000600L});
   public static final BitSet FOLLOW_ruleDecisionReviewOpt_in_ruleCreateDecisionReviewRuleDef10674 =
      new BitSet(new long[] {0x0080000010400000L, 0x0000000000000600L});
   public static final BitSet FOLLOW_28_in_ruleCreateDecisionReviewRuleDef10688 =
      new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_ruleAttrDef_in_ruleCreateDecisionReviewRuleDef10709 =
      new BitSet(new long[] {0x0000000010400000L});
   public static final BitSet FOLLOW_22_in_ruleCreateDecisionReviewRuleDef10723 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleCreatePeerReviewRuleDef_in_entryRuleCreatePeerReviewRuleDef10759 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleCreatePeerReviewRuleDef10769 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_89_in_ruleCreatePeerReviewRuleDef10807 =
      new BitSet(new long[] {0x0000000000000010L, 0x000003FFC0000000L});
   public static final BitSet FOLLOW_ruleRULE_NAME_REFERENCE_in_ruleCreatePeerReviewRuleDef10828 =
      new BitSet(new long[] {0x0000000000020000L});
   public static final BitSet FOLLOW_17_in_ruleCreatePeerReviewRuleDef10841 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000000020L});
   public static final BitSet FOLLOW_69_in_ruleCreatePeerReviewRuleDef10854 =
      new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleCreatePeerReviewRuleDef10871 =
      new BitSet(new long[] {0x0008000000000000L, 0x0000000004000000L});
   public static final BitSet FOLLOW_51_in_ruleCreatePeerReviewRuleDef10890 =
      new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleCreatePeerReviewRuleDef10907 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000004000000L});
   public static final BitSet FOLLOW_90_in_ruleCreatePeerReviewRuleDef10927 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0000007000000000L});
   public static final BitSet FOLLOW_ruleRuleLocation_in_ruleCreatePeerReviewRuleDef10948 =
      new BitSet(new long[] {0x0000000000000000L, 0x000000000C0020C0L});
   public static final BitSet FOLLOW_91_in_ruleCreatePeerReviewRuleDef10963 =
      new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_ruleUserDef_in_ruleCreatePeerReviewRuleDef10984 =
      new BitSet(new long[] {0x0000000000000000L, 0x00000000080020C0L});
   public static final BitSet FOLLOW_70_in_ruleCreatePeerReviewRuleDef10999 =
      new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleCreatePeerReviewRuleDef11020 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000002080L});
   public static final BitSet FOLLOW_77_in_ruleCreatePeerReviewRuleDef11035 =
      new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleCreatePeerReviewRuleDef11052 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000000080L});
   public static final BitSet FOLLOW_71_in_ruleCreatePeerReviewRuleDef11071 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0000000C00000000L});
   public static final BitSet FOLLOW_ruleReviewBlockingType_in_ruleCreatePeerReviewRuleDef11092 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000000100L});
   public static final BitSet FOLLOW_72_in_ruleCreatePeerReviewRuleDef11104 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0000000058000000L});
   public static final BitSet FOLLOW_ruleWorkflowEventType_in_ruleCreatePeerReviewRuleDef11125 =
      new BitSet(new long[] {0x0000000010400000L});
   public static final BitSet FOLLOW_28_in_ruleCreatePeerReviewRuleDef11138 =
      new BitSet(new long[] {0x0000000000000010L});
   public static final BitSet FOLLOW_ruleAttrDef_in_ruleCreatePeerReviewRuleDef11159 =
      new BitSet(new long[] {0x0000000010400000L});
   public static final BitSet FOLLOW_22_in_ruleCreatePeerReviewRuleDef11173 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleRuleRef_in_entryRuleRuleRef11210 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleRuleRef11221 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_94_in_ruleRuleRef11259 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_95_in_ruleRuleRef11278 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_96_in_ruleRuleRef11297 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_97_in_ruleRuleRef11316 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_98_in_ruleRuleRef11335 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_99_in_ruleRuleRef11354 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_100_in_ruleRuleRef11373 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_101_in_ruleRuleRef11392 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_102_in_ruleRuleRef11411 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_103_in_ruleRuleRef11430 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_104_in_ruleRuleRef11449 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_105_in_ruleRuleRef11468 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleRULE_NAME_REFERENCE_in_ruleRuleRef11496 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleReviewRule_in_entryRuleReviewRule11541 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleReviewRule11551 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleCreateDecisionReviewRuleDef_in_ruleReviewRule11598 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleCreatePeerReviewRuleDef_in_ruleReviewRule11625 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleRule_in_entryRuleRule11660 = new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleRule11670 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleRuleDef_in_ruleRule11717 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleCreateTaskRuleDef_in_ruleRule11744 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleReviewRule_in_ruleRule11771 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleWIDGET_OPTION_NAME_in_entryRuleWIDGET_OPTION_NAME11807 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleWIDGET_OPTION_NAME11818 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleWIDGET_OPTION_NAME11857 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleWidgetOption_in_entryRuleWidgetOption11902 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleWidgetOption11913 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_106_in_ruleWidgetOption11951 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_107_in_ruleWidgetOption11970 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_108_in_ruleWidgetOption11989 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_109_in_ruleWidgetOption12008 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_110_in_ruleWidgetOption12027 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_111_in_ruleWidgetOption12046 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_112_in_ruleWidgetOption12065 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_113_in_ruleWidgetOption12084 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_114_in_ruleWidgetOption12103 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_115_in_ruleWidgetOption12122 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_116_in_ruleWidgetOption12141 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_117_in_ruleWidgetOption12160 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_118_in_ruleWidgetOption12179 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_119_in_ruleWidgetOption12198 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_120_in_ruleWidgetOption12217 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_121_in_ruleWidgetOption12236 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_122_in_ruleWidgetOption12255 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_123_in_ruleWidgetOption12274 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_124_in_ruleWidgetOption12293 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_125_in_ruleWidgetOption12312 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_126_in_ruleWidgetOption12331 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_127_in_ruleWidgetOption12350 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_128_in_ruleWidgetOption12369 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_129_in_ruleWidgetOption12388 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_130_in_ruleWidgetOption12407 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_131_in_ruleWidgetOption12426 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_132_in_ruleWidgetOption12445 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_133_in_ruleWidgetOption12464 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_134_in_ruleWidgetOption12483 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_135_in_ruleWidgetOption12502 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleWIDGET_OPTION_NAME_in_ruleWidgetOption12530 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_rulePAGE_TYPE_NAME_in_entryRulePAGE_TYPE_NAME12576 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRulePAGE_TYPE_NAME12587 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_RULE_STRING_in_rulePAGE_TYPE_NAME12626 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_rulePageType_in_entryRulePageType12671 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRulePageType12682 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_136_in_rulePageType12720 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_137_in_rulePageType12739 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_138_in_rulePageType12758 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_rulePAGE_TYPE_NAME_in_rulePageType12786 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleCOLOR_NAME_in_entryRuleCOLOR_NAME12832 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleCOLOR_NAME12843 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_RULE_STRING_in_ruleCOLOR_NAME12882 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleStateColor_in_entryRuleStateColor12927 =
      new BitSet(new long[] {0x0000000000000000L});
   public static final BitSet FOLLOW_EOF_in_entryRuleStateColor12938 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_139_in_ruleStateColor12976 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_140_in_ruleStateColor12995 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_141_in_ruleStateColor13014 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_142_in_ruleStateColor13033 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_143_in_ruleStateColor13052 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_144_in_ruleStateColor13071 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_145_in_ruleStateColor13090 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_146_in_ruleStateColor13109 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_147_in_ruleStateColor13128 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_148_in_ruleStateColor13147 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_149_in_ruleStateColor13166 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_150_in_ruleStateColor13185 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_151_in_ruleStateColor13204 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_152_in_ruleStateColor13223 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_153_in_ruleStateColor13242 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_154_in_ruleStateColor13261 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_ruleCOLOR_NAME_in_ruleStateColor13289 =
      new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_155_in_ruleOnEventType13348 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_156_in_ruleOnEventType13365 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_157_in_ruleOnEventType13382 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_158_in_ruleOnEventType13399 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_159_in_ruleOnEventType13416 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_86_in_ruleBooleanDef13461 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_160_in_ruleBooleanDef13478 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_161_in_ruleBooleanDef13495 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_158_in_ruleWorkflowEventType13540 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_155_in_ruleWorkflowEventType13557 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_156_in_ruleWorkflowEventType13574 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_162_in_ruleReviewBlockingType13619 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_163_in_ruleReviewBlockingType13636 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_164_in_ruleRuleLocation13681 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_165_in_ruleRuleLocation13698 = new BitSet(new long[] {0x0000000000000002L});
   public static final BitSet FOLLOW_166_in_ruleRuleLocation13715 = new BitSet(new long[] {0x0000000000000002L});

}