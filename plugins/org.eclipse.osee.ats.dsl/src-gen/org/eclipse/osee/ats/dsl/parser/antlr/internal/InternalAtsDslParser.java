package org.eclipse.osee.ats.dsl.parser.antlr.internal; 

import org.eclipse.xtext.*;
import org.eclipse.xtext.parser.*;
import org.eclipse.xtext.parser.impl.*;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.xtext.parser.antlr.AbstractInternalAntlrParser;
import org.eclipse.xtext.parser.antlr.XtextTokenStream;
import org.eclipse.xtext.parser.antlr.XtextTokenStream.HiddenTokens;
import org.eclipse.xtext.parser.antlr.AntlrDatatypeRuleToken;
import org.eclipse.osee.ats.dsl.services.AtsDslGrammarAccess;



import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class InternalAtsDslParser extends AbstractInternalAntlrParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE_STRING", "RULE_INT", "RULE_ID", "RULE_ML_COMMENT", "RULE_SL_COMMENT", "RULE_WS", "RULE_ANY_OTHER", "'userDefinition'", "'teamDefinition'", "'actionableItem'", "'workDefinition'", "'program'", "'{'", "'active'", "'userId'", "'email'", "'isAdmin'", "'}'", "'value'", "'uuid'", "'guid'", "'artifactType'", "'namespace'", "'attribute'", "'staticId'", "'lead'", "'member'", "'privileged'", "'relatedTaskWorkDefinition'", "'teamWorkflowArtifactType'", "'accessContextId'", "'version'", "'children'", "'actionable'", "'owner'", "'team'", "'next'", "'released'", "'allowCreateBranch'", "'allowCommitBranch'", "'baselineBranchUuid'", "'parallelVersion'", "'id'", "'startState'", "'widgetDefinition'", "'attributeName'", "'description'", "'xWidgetName'", "'defaultValue'", "'height'", "'option'", "'minConstraint'", "'maxConstraint'", "'widget'", "'attributeWidget'", "'with'", "'state'", "'type'", "'ordinal'", "'rule'", "'percentWeight'", "'recommendedPercentComplete'", "'color'", "'decisionReview'", "'decisionReviewDefinition'", "'title'", "'relatedToState'", "'blockingType'", "'onEvent'", "'assignee'", "'autoTransitionToDecision'", "'peerReview'", "'peerReviewDefinition'", "'location'", "'followup by'", "'named'", "'to'", "'layout'", "'layoutCopyFrom'", "'composite'", "'numColumns'", "'GetOrCreate'", "'None'", "'AsDefault'", "'OverrideAttributeValidation'", "'RequireStateHourSpentPrompt'", "'AddDecisionValidateBlockingReview'", "'AddDecisionValidateNonBlockingReview'", "'AllowTransitionWithWorkingBranch'", "'ForceAssigneesToTeamLeads'", "'RequireTargetedVersion'", "'AllowPrivilegedEditToTeamMember'", "'AllowPrivilegedEditToTeamMemberAndOriginator'", "'AllowPrivilegedEditToAll'", "'AllowEditToAll'", "'AllowAssigneeToAll'", "'AllowTransitionWithoutTaskCompletion'", "'REQUIRED_FOR_TRANSITION'", "'NOT_REQUIRED_FOR_TRANSITION'", "'REQUIRED_FOR_COMPLETION'", "'NOT_REQUIRED_FOR_COMPLETION'", "'ENABLED'", "'NOT_ENABLED'", "'EDITABLE'", "'NOT_EDITABLE'", "'FUTURE_DATE_REQUIRED'", "'NOT_FUTURE_DATE_REQUIRED'", "'MULTI_SELECT'", "'HORIZONTAL_LABEL'", "'VERTICAL_LABEL'", "'LABEL_AFTER'", "'LABEL_BEFORE'", "'NO_LABEL'", "'SORTED'", "'ADD_DEFAULT_VALUE'", "'NO_DEFAULT_VALUE'", "'BEGIN_COMPOSITE_4'", "'BEGIN_COMPOSITE_6'", "'BEGIN_COMPOSITE_8'", "'BEGIN_COMPOSITE_10'", "'END_COMPOSITE'", "'FILL_NONE'", "'FILL_HORIZONTALLY'", "'FILL_VERTICALLY'", "'ALIGN_LEFT'", "'ALIGN_RIGHT'", "'ALIGN_CENTER'", "'Working'", "'Completed'", "'Cancelled'", "'BLACK'", "'WHITE'", "'RED'", "'DARK_RED'", "'GREEN'", "'DARK_GREEN'", "'YELLOW'", "'DARK_YELLOW'", "'BLUE'", "'DARK_BLUE'", "'MAGENTA'", "'DARK_MAGENTA'", "'CYAN'", "'DARK_CYAN'", "'GRAY'", "'DARK_GRAY'", "'True'", "'False'", "'TransitionTo'", "'CreateBranch'", "'CommitBranch'", "'Transition'", "'Commit'"
    };
    public static final int RULE_ID=6;
    public static final int T__29=29;
    public static final int T__28=28;
    public static final int T__27=27;
    public static final int T__26=26;
    public static final int T__25=25;
    public static final int T__24=24;
    public static final int T__23=23;
    public static final int T__22=22;
    public static final int RULE_ANY_OTHER=10;
    public static final int T__21=21;
    public static final int T__20=20;
    public static final int EOF=-1;
    public static final int T__93=93;
    public static final int T__19=19;
    public static final int T__94=94;
    public static final int T__91=91;
    public static final int T__92=92;
    public static final int T__148=148;
    public static final int T__16=16;
    public static final int T__147=147;
    public static final int T__15=15;
    public static final int T__90=90;
    public static final int T__18=18;
    public static final int T__149=149;
    public static final int T__17=17;
    public static final int T__12=12;
    public static final int T__11=11;
    public static final int T__14=14;
    public static final int T__13=13;
    public static final int T__154=154;
    public static final int T__155=155;
    public static final int T__156=156;
    public static final int T__99=99;
    public static final int T__150=150;
    public static final int T__98=98;
    public static final int T__151=151;
    public static final int T__97=97;
    public static final int T__152=152;
    public static final int T__96=96;
    public static final int T__153=153;
    public static final int T__95=95;
    public static final int T__139=139;
    public static final int T__138=138;
    public static final int T__137=137;
    public static final int T__136=136;
    public static final int T__80=80;
    public static final int T__81=81;
    public static final int T__82=82;
    public static final int T__83=83;
    public static final int T__141=141;
    public static final int T__85=85;
    public static final int T__142=142;
    public static final int T__84=84;
    public static final int T__87=87;
    public static final int T__140=140;
    public static final int T__86=86;
    public static final int T__145=145;
    public static final int T__89=89;
    public static final int T__146=146;
    public static final int T__88=88;
    public static final int RULE_ML_COMMENT=7;
    public static final int T__143=143;
    public static final int T__144=144;
    public static final int T__126=126;
    public static final int T__125=125;
    public static final int T__128=128;
    public static final int RULE_STRING=4;
    public static final int T__127=127;
    public static final int T__71=71;
    public static final int T__129=129;
    public static final int T__72=72;
    public static final int T__70=70;
    public static final int T__76=76;
    public static final int T__75=75;
    public static final int T__130=130;
    public static final int T__74=74;
    public static final int T__131=131;
    public static final int T__73=73;
    public static final int T__132=132;
    public static final int T__133=133;
    public static final int T__79=79;
    public static final int T__134=134;
    public static final int T__78=78;
    public static final int T__135=135;
    public static final int T__77=77;
    public static final int T__68=68;
    public static final int T__69=69;
    public static final int T__66=66;
    public static final int T__67=67;
    public static final int T__64=64;
    public static final int T__65=65;
    public static final int T__62=62;
    public static final int T__63=63;
    public static final int T__118=118;
    public static final int T__119=119;
    public static final int T__116=116;
    public static final int T__117=117;
    public static final int T__114=114;
    public static final int T__115=115;
    public static final int T__124=124;
    public static final int T__123=123;
    public static final int T__122=122;
    public static final int T__121=121;
    public static final int T__120=120;
    public static final int T__61=61;
    public static final int T__60=60;
    public static final int T__55=55;
    public static final int T__56=56;
    public static final int T__57=57;
    public static final int T__58=58;
    public static final int T__51=51;
    public static final int T__52=52;
    public static final int T__53=53;
    public static final int T__54=54;
    public static final int T__107=107;
    public static final int T__108=108;
    public static final int T__109=109;
    public static final int T__103=103;
    public static final int T__59=59;
    public static final int T__104=104;
    public static final int T__105=105;
    public static final int T__106=106;
    public static final int T__111=111;
    public static final int T__110=110;
    public static final int RULE_INT=5;
    public static final int T__113=113;
    public static final int T__112=112;
    public static final int T__50=50;
    public static final int T__42=42;
    public static final int T__43=43;
    public static final int T__40=40;
    public static final int T__41=41;
    public static final int T__46=46;
    public static final int T__47=47;
    public static final int T__44=44;
    public static final int T__45=45;
    public static final int T__48=48;
    public static final int T__49=49;
    public static final int T__102=102;
    public static final int T__101=101;
    public static final int T__100=100;
    public static final int RULE_SL_COMMENT=8;
    public static final int T__30=30;
    public static final int T__31=31;
    public static final int T__32=32;
    public static final int T__33=33;
    public static final int T__34=34;
    public static final int T__35=35;
    public static final int T__36=36;
    public static final int T__37=37;
    public static final int T__38=38;
    public static final int T__39=39;
    public static final int RULE_WS=9;

    // delegates
    // delegators


        public InternalAtsDslParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public InternalAtsDslParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return InternalAtsDslParser.tokenNames; }
    public String getGrammarFileName() { return "../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g"; }



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
            iv_ruleAtsDsl=ruleAtsDsl();

            state._fsp--;

             current =iv_ruleAtsDsl; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAtsDsl85); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleAtsDsl"


    // $ANTLR start "ruleAtsDsl"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:77:1: ruleAtsDsl returns [EObject current=null] : ( (otherlv_0= 'userDefinition' ( (lv_userDef_1_0= ruleUserDef ) ) )* (otherlv_2= 'teamDefinition' ( (lv_teamDef_3_0= ruleTeamDef ) ) )* (otherlv_4= 'actionableItem' ( (lv_actionableItemDef_5_0= ruleActionableItemDef ) ) )* (otherlv_6= 'workDefinition' ( (lv_workDef_7_0= ruleWorkDef ) ) )* (otherlv_8= 'program' ( (lv_program_9_0= ruleProgramDef ) ) )* ) ;
    public final EObject ruleAtsDsl() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_2=null;
        Token otherlv_4=null;
        Token otherlv_6=null;
        Token otherlv_8=null;
        EObject lv_userDef_1_0 = null;

        EObject lv_teamDef_3_0 = null;

        EObject lv_actionableItemDef_5_0 = null;

        EObject lv_workDef_7_0 = null;

        EObject lv_program_9_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:80:28: ( ( (otherlv_0= 'userDefinition' ( (lv_userDef_1_0= ruleUserDef ) ) )* (otherlv_2= 'teamDefinition' ( (lv_teamDef_3_0= ruleTeamDef ) ) )* (otherlv_4= 'actionableItem' ( (lv_actionableItemDef_5_0= ruleActionableItemDef ) ) )* (otherlv_6= 'workDefinition' ( (lv_workDef_7_0= ruleWorkDef ) ) )* (otherlv_8= 'program' ( (lv_program_9_0= ruleProgramDef ) ) )* ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:81:1: ( (otherlv_0= 'userDefinition' ( (lv_userDef_1_0= ruleUserDef ) ) )* (otherlv_2= 'teamDefinition' ( (lv_teamDef_3_0= ruleTeamDef ) ) )* (otherlv_4= 'actionableItem' ( (lv_actionableItemDef_5_0= ruleActionableItemDef ) ) )* (otherlv_6= 'workDefinition' ( (lv_workDef_7_0= ruleWorkDef ) ) )* (otherlv_8= 'program' ( (lv_program_9_0= ruleProgramDef ) ) )* )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:81:1: ( (otherlv_0= 'userDefinition' ( (lv_userDef_1_0= ruleUserDef ) ) )* (otherlv_2= 'teamDefinition' ( (lv_teamDef_3_0= ruleTeamDef ) ) )* (otherlv_4= 'actionableItem' ( (lv_actionableItemDef_5_0= ruleActionableItemDef ) ) )* (otherlv_6= 'workDefinition' ( (lv_workDef_7_0= ruleWorkDef ) ) )* (otherlv_8= 'program' ( (lv_program_9_0= ruleProgramDef ) ) )* )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:81:2: (otherlv_0= 'userDefinition' ( (lv_userDef_1_0= ruleUserDef ) ) )* (otherlv_2= 'teamDefinition' ( (lv_teamDef_3_0= ruleTeamDef ) ) )* (otherlv_4= 'actionableItem' ( (lv_actionableItemDef_5_0= ruleActionableItemDef ) ) )* (otherlv_6= 'workDefinition' ( (lv_workDef_7_0= ruleWorkDef ) ) )* (otherlv_8= 'program' ( (lv_program_9_0= ruleProgramDef ) ) )*
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:81:2: (otherlv_0= 'userDefinition' ( (lv_userDef_1_0= ruleUserDef ) ) )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==11) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:81:4: otherlv_0= 'userDefinition' ( (lv_userDef_1_0= ruleUserDef ) )
            	    {
            	    otherlv_0=(Token)match(input,11,FOLLOW_11_in_ruleAtsDsl123); 

            	        	newLeafNode(otherlv_0, grammarAccess.getAtsDslAccess().getUserDefinitionKeyword_0_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:85:1: ( (lv_userDef_1_0= ruleUserDef ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:86:1: (lv_userDef_1_0= ruleUserDef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:86:1: (lv_userDef_1_0= ruleUserDef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:87:3: lv_userDef_1_0= ruleUserDef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getAtsDslAccess().getUserDefUserDefParserRuleCall_0_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleUserDef_in_ruleAtsDsl144);
            	    lv_userDef_1_0=ruleUserDef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getAtsDslRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"userDef",
            	            		lv_userDef_1_0, 
            	            		"UserDef");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:103:4: (otherlv_2= 'teamDefinition' ( (lv_teamDef_3_0= ruleTeamDef ) ) )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==12) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:103:6: otherlv_2= 'teamDefinition' ( (lv_teamDef_3_0= ruleTeamDef ) )
            	    {
            	    otherlv_2=(Token)match(input,12,FOLLOW_12_in_ruleAtsDsl159); 

            	        	newLeafNode(otherlv_2, grammarAccess.getAtsDslAccess().getTeamDefinitionKeyword_1_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:107:1: ( (lv_teamDef_3_0= ruleTeamDef ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:108:1: (lv_teamDef_3_0= ruleTeamDef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:108:1: (lv_teamDef_3_0= ruleTeamDef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:109:3: lv_teamDef_3_0= ruleTeamDef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getAtsDslAccess().getTeamDefTeamDefParserRuleCall_1_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleTeamDef_in_ruleAtsDsl180);
            	    lv_teamDef_3_0=ruleTeamDef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getAtsDslRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"teamDef",
            	            		lv_teamDef_3_0, 
            	            		"TeamDef");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:125:4: (otherlv_4= 'actionableItem' ( (lv_actionableItemDef_5_0= ruleActionableItemDef ) ) )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==13) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:125:6: otherlv_4= 'actionableItem' ( (lv_actionableItemDef_5_0= ruleActionableItemDef ) )
            	    {
            	    otherlv_4=(Token)match(input,13,FOLLOW_13_in_ruleAtsDsl195); 

            	        	newLeafNode(otherlv_4, grammarAccess.getAtsDslAccess().getActionableItemKeyword_2_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:129:1: ( (lv_actionableItemDef_5_0= ruleActionableItemDef ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:130:1: (lv_actionableItemDef_5_0= ruleActionableItemDef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:130:1: (lv_actionableItemDef_5_0= ruleActionableItemDef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:131:3: lv_actionableItemDef_5_0= ruleActionableItemDef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getAtsDslAccess().getActionableItemDefActionableItemDefParserRuleCall_2_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleActionableItemDef_in_ruleAtsDsl216);
            	    lv_actionableItemDef_5_0=ruleActionableItemDef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getAtsDslRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"actionableItemDef",
            	            		lv_actionableItemDef_5_0, 
            	            		"ActionableItemDef");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:147:4: (otherlv_6= 'workDefinition' ( (lv_workDef_7_0= ruleWorkDef ) ) )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==14) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:147:6: otherlv_6= 'workDefinition' ( (lv_workDef_7_0= ruleWorkDef ) )
            	    {
            	    otherlv_6=(Token)match(input,14,FOLLOW_14_in_ruleAtsDsl231); 

            	        	newLeafNode(otherlv_6, grammarAccess.getAtsDslAccess().getWorkDefinitionKeyword_3_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:151:1: ( (lv_workDef_7_0= ruleWorkDef ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:152:1: (lv_workDef_7_0= ruleWorkDef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:152:1: (lv_workDef_7_0= ruleWorkDef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:153:3: lv_workDef_7_0= ruleWorkDef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getAtsDslAccess().getWorkDefWorkDefParserRuleCall_3_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleWorkDef_in_ruleAtsDsl252);
            	    lv_workDef_7_0=ruleWorkDef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getAtsDslRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"workDef",
            	            		lv_workDef_7_0, 
            	            		"WorkDef");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:169:4: (otherlv_8= 'program' ( (lv_program_9_0= ruleProgramDef ) ) )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==15) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:169:6: otherlv_8= 'program' ( (lv_program_9_0= ruleProgramDef ) )
            	    {
            	    otherlv_8=(Token)match(input,15,FOLLOW_15_in_ruleAtsDsl267); 

            	        	newLeafNode(otherlv_8, grammarAccess.getAtsDslAccess().getProgramKeyword_4_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:173:1: ( (lv_program_9_0= ruleProgramDef ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:174:1: (lv_program_9_0= ruleProgramDef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:174:1: (lv_program_9_0= ruleProgramDef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:175:3: lv_program_9_0= ruleProgramDef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getAtsDslAccess().getProgramProgramDefParserRuleCall_4_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleProgramDef_in_ruleAtsDsl288);
            	    lv_program_9_0=ruleProgramDef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getAtsDslRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"program",
            	            		lv_program_9_0, 
            	            		"ProgramDef");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);


            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleAtsDsl"


    // $ANTLR start "entryRuleUSER_DEF_REFERENCE"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:199:1: entryRuleUSER_DEF_REFERENCE returns [String current=null] : iv_ruleUSER_DEF_REFERENCE= ruleUSER_DEF_REFERENCE EOF ;
    public final String entryRuleUSER_DEF_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleUSER_DEF_REFERENCE = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:200:2: (iv_ruleUSER_DEF_REFERENCE= ruleUSER_DEF_REFERENCE EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:201:2: iv_ruleUSER_DEF_REFERENCE= ruleUSER_DEF_REFERENCE EOF
            {
             newCompositeNode(grammarAccess.getUSER_DEF_REFERENCERule()); 
            pushFollow(FOLLOW_ruleUSER_DEF_REFERENCE_in_entryRuleUSER_DEF_REFERENCE327);
            iv_ruleUSER_DEF_REFERENCE=ruleUSER_DEF_REFERENCE();

            state._fsp--;

             current =iv_ruleUSER_DEF_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleUSER_DEF_REFERENCE338); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleUSER_DEF_REFERENCE"


    // $ANTLR start "ruleUSER_DEF_REFERENCE"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:208:1: ruleUSER_DEF_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleUSER_DEF_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:211:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:212:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleUSER_DEF_REFERENCE377); 

            		current.merge(this_STRING_0);
                
             
                newLeafNode(this_STRING_0, grammarAccess.getUSER_DEF_REFERENCEAccess().getSTRINGTerminalRuleCall()); 
                

            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleUSER_DEF_REFERENCE"


    // $ANTLR start "entryRuleUserDef"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:227:1: entryRuleUserDef returns [EObject current=null] : iv_ruleUserDef= ruleUserDef EOF ;
    public final EObject entryRuleUserDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleUserDef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:228:2: (iv_ruleUserDef= ruleUserDef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:229:2: iv_ruleUserDef= ruleUserDef EOF
            {
             newCompositeNode(grammarAccess.getUserDefRule()); 
            pushFollow(FOLLOW_ruleUserDef_in_entryRuleUserDef421);
            iv_ruleUserDef=ruleUserDef();

            state._fsp--;

             current =iv_ruleUserDef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleUserDef431); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleUserDef"


    // $ANTLR start "ruleUserDef"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:236:1: ruleUserDef returns [EObject current=null] : ( ( (lv_name_0_0= ruleUSER_DEF_REFERENCE ) ) ( (lv_userDefOption_1_0= ruleUserDefOption ) )* (otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'userId' ( (lv_userId_6_0= RULE_STRING ) ) )? (otherlv_7= 'email' ( (lv_email_8_0= RULE_STRING ) ) )? (otherlv_9= 'isAdmin' ( (lv_admin_10_0= ruleBooleanDef ) ) )? otherlv_11= '}' )? ) ;
    public final EObject ruleUserDef() throws RecognitionException {
        EObject current = null;

        Token otherlv_2=null;
        Token otherlv_3=null;
        Token otherlv_5=null;
        Token lv_userId_6_0=null;
        Token otherlv_7=null;
        Token lv_email_8_0=null;
        Token otherlv_9=null;
        Token otherlv_11=null;
        AntlrDatatypeRuleToken lv_name_0_0 = null;

        AntlrDatatypeRuleToken lv_userDefOption_1_0 = null;

        Enumerator lv_active_4_0 = null;

        Enumerator lv_admin_10_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:239:28: ( ( ( (lv_name_0_0= ruleUSER_DEF_REFERENCE ) ) ( (lv_userDefOption_1_0= ruleUserDefOption ) )* (otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'userId' ( (lv_userId_6_0= RULE_STRING ) ) )? (otherlv_7= 'email' ( (lv_email_8_0= RULE_STRING ) ) )? (otherlv_9= 'isAdmin' ( (lv_admin_10_0= ruleBooleanDef ) ) )? otherlv_11= '}' )? ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:240:1: ( ( (lv_name_0_0= ruleUSER_DEF_REFERENCE ) ) ( (lv_userDefOption_1_0= ruleUserDefOption ) )* (otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'userId' ( (lv_userId_6_0= RULE_STRING ) ) )? (otherlv_7= 'email' ( (lv_email_8_0= RULE_STRING ) ) )? (otherlv_9= 'isAdmin' ( (lv_admin_10_0= ruleBooleanDef ) ) )? otherlv_11= '}' )? )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:240:1: ( ( (lv_name_0_0= ruleUSER_DEF_REFERENCE ) ) ( (lv_userDefOption_1_0= ruleUserDefOption ) )* (otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'userId' ( (lv_userId_6_0= RULE_STRING ) ) )? (otherlv_7= 'email' ( (lv_email_8_0= RULE_STRING ) ) )? (otherlv_9= 'isAdmin' ( (lv_admin_10_0= ruleBooleanDef ) ) )? otherlv_11= '}' )? )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:240:2: ( (lv_name_0_0= ruleUSER_DEF_REFERENCE ) ) ( (lv_userDefOption_1_0= ruleUserDefOption ) )* (otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'userId' ( (lv_userId_6_0= RULE_STRING ) ) )? (otherlv_7= 'email' ( (lv_email_8_0= RULE_STRING ) ) )? (otherlv_9= 'isAdmin' ( (lv_admin_10_0= ruleBooleanDef ) ) )? otherlv_11= '}' )?
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:240:2: ( (lv_name_0_0= ruleUSER_DEF_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:241:1: (lv_name_0_0= ruleUSER_DEF_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:241:1: (lv_name_0_0= ruleUSER_DEF_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:242:3: lv_name_0_0= ruleUSER_DEF_REFERENCE
            {
             
            	        newCompositeNode(grammarAccess.getUserDefAccess().getNameUSER_DEF_REFERENCEParserRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_ruleUSER_DEF_REFERENCE_in_ruleUserDef477);
            lv_name_0_0=ruleUSER_DEF_REFERENCE();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getUserDefRule());
            	        }
                   		set(
                   			current, 
                   			"name",
                    		lv_name_0_0, 
                    		"USER_DEF_REFERENCE");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:258:2: ( (lv_userDefOption_1_0= ruleUserDefOption ) )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( (LA6_0==RULE_STRING||LA6_0==85) ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:259:1: (lv_userDefOption_1_0= ruleUserDefOption )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:259:1: (lv_userDefOption_1_0= ruleUserDefOption )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:260:3: lv_userDefOption_1_0= ruleUserDefOption
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getUserDefAccess().getUserDefOptionUserDefOptionParserRuleCall_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleUserDefOption_in_ruleUserDef498);
            	    lv_userDefOption_1_0=ruleUserDefOption();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getUserDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"userDefOption",
            	            		lv_userDefOption_1_0, 
            	            		"UserDefOption");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    break loop6;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:276:3: (otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'userId' ( (lv_userId_6_0= RULE_STRING ) ) )? (otherlv_7= 'email' ( (lv_email_8_0= RULE_STRING ) ) )? (otherlv_9= 'isAdmin' ( (lv_admin_10_0= ruleBooleanDef ) ) )? otherlv_11= '}' )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==16) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:276:5: otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'userId' ( (lv_userId_6_0= RULE_STRING ) ) )? (otherlv_7= 'email' ( (lv_email_8_0= RULE_STRING ) ) )? (otherlv_9= 'isAdmin' ( (lv_admin_10_0= ruleBooleanDef ) ) )? otherlv_11= '}'
                    {
                    otherlv_2=(Token)match(input,16,FOLLOW_16_in_ruleUserDef512); 

                        	newLeafNode(otherlv_2, grammarAccess.getUserDefAccess().getLeftCurlyBracketKeyword_2_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:280:1: (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )?
                    int alt7=2;
                    int LA7_0 = input.LA(1);

                    if ( (LA7_0==17) ) {
                        alt7=1;
                    }
                    switch (alt7) {
                        case 1 :
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:280:3: otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) )
                            {
                            otherlv_3=(Token)match(input,17,FOLLOW_17_in_ruleUserDef525); 

                                	newLeafNode(otherlv_3, grammarAccess.getUserDefAccess().getActiveKeyword_2_1_0());
                                
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:284:1: ( (lv_active_4_0= ruleBooleanDef ) )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:285:1: (lv_active_4_0= ruleBooleanDef )
                            {
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:285:1: (lv_active_4_0= ruleBooleanDef )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:286:3: lv_active_4_0= ruleBooleanDef
                            {
                             
                            	        newCompositeNode(grammarAccess.getUserDefAccess().getActiveBooleanDefEnumRuleCall_2_1_1_0()); 
                            	    
                            pushFollow(FOLLOW_ruleBooleanDef_in_ruleUserDef546);
                            lv_active_4_0=ruleBooleanDef();

                            state._fsp--;


                            	        if (current==null) {
                            	            current = createModelElementForParent(grammarAccess.getUserDefRule());
                            	        }
                                   		set(
                                   			current, 
                                   			"active",
                                    		lv_active_4_0, 
                                    		"BooleanDef");
                            	        afterParserOrEnumRuleCall();
                            	    

                            }


                            }


                            }
                            break;

                    }

                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:302:4: (otherlv_5= 'userId' ( (lv_userId_6_0= RULE_STRING ) ) )?
                    int alt8=2;
                    int LA8_0 = input.LA(1);

                    if ( (LA8_0==18) ) {
                        alt8=1;
                    }
                    switch (alt8) {
                        case 1 :
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:302:6: otherlv_5= 'userId' ( (lv_userId_6_0= RULE_STRING ) )
                            {
                            otherlv_5=(Token)match(input,18,FOLLOW_18_in_ruleUserDef561); 

                                	newLeafNode(otherlv_5, grammarAccess.getUserDefAccess().getUserIdKeyword_2_2_0());
                                
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:306:1: ( (lv_userId_6_0= RULE_STRING ) )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:307:1: (lv_userId_6_0= RULE_STRING )
                            {
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:307:1: (lv_userId_6_0= RULE_STRING )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:308:3: lv_userId_6_0= RULE_STRING
                            {
                            lv_userId_6_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleUserDef578); 

                            			newLeafNode(lv_userId_6_0, grammarAccess.getUserDefAccess().getUserIdSTRINGTerminalRuleCall_2_2_1_0()); 
                            		

                            	        if (current==null) {
                            	            current = createModelElement(grammarAccess.getUserDefRule());
                            	        }
                                   		setWithLastConsumed(
                                   			current, 
                                   			"userId",
                                    		lv_userId_6_0, 
                                    		"STRING");
                            	    

                            }


                            }


                            }
                            break;

                    }

                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:324:4: (otherlv_7= 'email' ( (lv_email_8_0= RULE_STRING ) ) )?
                    int alt9=2;
                    int LA9_0 = input.LA(1);

                    if ( (LA9_0==19) ) {
                        alt9=1;
                    }
                    switch (alt9) {
                        case 1 :
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:324:6: otherlv_7= 'email' ( (lv_email_8_0= RULE_STRING ) )
                            {
                            otherlv_7=(Token)match(input,19,FOLLOW_19_in_ruleUserDef598); 

                                	newLeafNode(otherlv_7, grammarAccess.getUserDefAccess().getEmailKeyword_2_3_0());
                                
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:328:1: ( (lv_email_8_0= RULE_STRING ) )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:329:1: (lv_email_8_0= RULE_STRING )
                            {
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:329:1: (lv_email_8_0= RULE_STRING )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:330:3: lv_email_8_0= RULE_STRING
                            {
                            lv_email_8_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleUserDef615); 

                            			newLeafNode(lv_email_8_0, grammarAccess.getUserDefAccess().getEmailSTRINGTerminalRuleCall_2_3_1_0()); 
                            		

                            	        if (current==null) {
                            	            current = createModelElement(grammarAccess.getUserDefRule());
                            	        }
                                   		setWithLastConsumed(
                                   			current, 
                                   			"email",
                                    		lv_email_8_0, 
                                    		"STRING");
                            	    

                            }


                            }


                            }
                            break;

                    }

                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:346:4: (otherlv_9= 'isAdmin' ( (lv_admin_10_0= ruleBooleanDef ) ) )?
                    int alt10=2;
                    int LA10_0 = input.LA(1);

                    if ( (LA10_0==20) ) {
                        alt10=1;
                    }
                    switch (alt10) {
                        case 1 :
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:346:6: otherlv_9= 'isAdmin' ( (lv_admin_10_0= ruleBooleanDef ) )
                            {
                            otherlv_9=(Token)match(input,20,FOLLOW_20_in_ruleUserDef635); 

                                	newLeafNode(otherlv_9, grammarAccess.getUserDefAccess().getIsAdminKeyword_2_4_0());
                                
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:350:1: ( (lv_admin_10_0= ruleBooleanDef ) )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:351:1: (lv_admin_10_0= ruleBooleanDef )
                            {
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:351:1: (lv_admin_10_0= ruleBooleanDef )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:352:3: lv_admin_10_0= ruleBooleanDef
                            {
                             
                            	        newCompositeNode(grammarAccess.getUserDefAccess().getAdminBooleanDefEnumRuleCall_2_4_1_0()); 
                            	    
                            pushFollow(FOLLOW_ruleBooleanDef_in_ruleUserDef656);
                            lv_admin_10_0=ruleBooleanDef();

                            state._fsp--;


                            	        if (current==null) {
                            	            current = createModelElementForParent(grammarAccess.getUserDefRule());
                            	        }
                                   		set(
                                   			current, 
                                   			"admin",
                                    		lv_admin_10_0, 
                                    		"BooleanDef");
                            	        afterParserOrEnumRuleCall();
                            	    

                            }


                            }


                            }
                            break;

                    }

                    otherlv_11=(Token)match(input,21,FOLLOW_21_in_ruleUserDef670); 

                        	newLeafNode(otherlv_11, grammarAccess.getUserDefAccess().getRightCurlyBracketKeyword_2_5());
                        

                    }
                    break;

            }


            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleUserDef"


    // $ANTLR start "entryRuleATTR_DEF_REFERENCE"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:380:1: entryRuleATTR_DEF_REFERENCE returns [String current=null] : iv_ruleATTR_DEF_REFERENCE= ruleATTR_DEF_REFERENCE EOF ;
    public final String entryRuleATTR_DEF_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleATTR_DEF_REFERENCE = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:381:2: (iv_ruleATTR_DEF_REFERENCE= ruleATTR_DEF_REFERENCE EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:382:2: iv_ruleATTR_DEF_REFERENCE= ruleATTR_DEF_REFERENCE EOF
            {
             newCompositeNode(grammarAccess.getATTR_DEF_REFERENCERule()); 
            pushFollow(FOLLOW_ruleATTR_DEF_REFERENCE_in_entryRuleATTR_DEF_REFERENCE709);
            iv_ruleATTR_DEF_REFERENCE=ruleATTR_DEF_REFERENCE();

            state._fsp--;

             current =iv_ruleATTR_DEF_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleATTR_DEF_REFERENCE720); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleATTR_DEF_REFERENCE"


    // $ANTLR start "ruleATTR_DEF_REFERENCE"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:389:1: ruleATTR_DEF_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleATTR_DEF_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:392:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:393:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleATTR_DEF_REFERENCE759); 

            		current.merge(this_STRING_0);
                
             
                newLeafNode(this_STRING_0, grammarAccess.getATTR_DEF_REFERENCEAccess().getSTRINGTerminalRuleCall()); 
                

            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleATTR_DEF_REFERENCE"


    // $ANTLR start "entryRuleAttrDef"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:408:1: entryRuleAttrDef returns [EObject current=null] : iv_ruleAttrDef= ruleAttrDef EOF ;
    public final EObject entryRuleAttrDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttrDef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:409:2: (iv_ruleAttrDef= ruleAttrDef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:410:2: iv_ruleAttrDef= ruleAttrDef EOF
            {
             newCompositeNode(grammarAccess.getAttrDefRule()); 
            pushFollow(FOLLOW_ruleAttrDef_in_entryRuleAttrDef803);
            iv_ruleAttrDef=ruleAttrDef();

            state._fsp--;

             current =iv_ruleAttrDef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttrDef813); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleAttrDef"


    // $ANTLR start "ruleAttrDef"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:417:1: ruleAttrDef returns [EObject current=null] : ( ( (lv_name_0_0= ruleATTR_DEF_REFERENCE ) ) ( (lv_option_1_0= ruleAttrDefOptions ) ) ) ;
    public final EObject ruleAttrDef() throws RecognitionException {
        EObject current = null;

        AntlrDatatypeRuleToken lv_name_0_0 = null;

        EObject lv_option_1_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:420:28: ( ( ( (lv_name_0_0= ruleATTR_DEF_REFERENCE ) ) ( (lv_option_1_0= ruleAttrDefOptions ) ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:421:1: ( ( (lv_name_0_0= ruleATTR_DEF_REFERENCE ) ) ( (lv_option_1_0= ruleAttrDefOptions ) ) )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:421:1: ( ( (lv_name_0_0= ruleATTR_DEF_REFERENCE ) ) ( (lv_option_1_0= ruleAttrDefOptions ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:421:2: ( (lv_name_0_0= ruleATTR_DEF_REFERENCE ) ) ( (lv_option_1_0= ruleAttrDefOptions ) )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:421:2: ( (lv_name_0_0= ruleATTR_DEF_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:422:1: (lv_name_0_0= ruleATTR_DEF_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:422:1: (lv_name_0_0= ruleATTR_DEF_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:423:3: lv_name_0_0= ruleATTR_DEF_REFERENCE
            {
             
            	        newCompositeNode(grammarAccess.getAttrDefAccess().getNameATTR_DEF_REFERENCEParserRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_ruleATTR_DEF_REFERENCE_in_ruleAttrDef859);
            lv_name_0_0=ruleATTR_DEF_REFERENCE();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getAttrDefRule());
            	        }
                   		set(
                   			current, 
                   			"name",
                    		lv_name_0_0, 
                    		"ATTR_DEF_REFERENCE");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:439:2: ( (lv_option_1_0= ruleAttrDefOptions ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:440:1: (lv_option_1_0= ruleAttrDefOptions )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:440:1: (lv_option_1_0= ruleAttrDefOptions )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:441:3: lv_option_1_0= ruleAttrDefOptions
            {
             
            	        newCompositeNode(grammarAccess.getAttrDefAccess().getOptionAttrDefOptionsParserRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_ruleAttrDefOptions_in_ruleAttrDef880);
            lv_option_1_0=ruleAttrDefOptions();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getAttrDefRule());
            	        }
                   		set(
                   			current, 
                   			"option",
                    		lv_option_1_0, 
                    		"AttrDefOptions");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }


            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleAttrDef"


    // $ANTLR start "entryRuleAttrDefOptions"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:465:1: entryRuleAttrDefOptions returns [EObject current=null] : iv_ruleAttrDefOptions= ruleAttrDefOptions EOF ;
    public final EObject entryRuleAttrDefOptions() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttrDefOptions = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:466:2: (iv_ruleAttrDefOptions= ruleAttrDefOptions EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:467:2: iv_ruleAttrDefOptions= ruleAttrDefOptions EOF
            {
             newCompositeNode(grammarAccess.getAttrDefOptionsRule()); 
            pushFollow(FOLLOW_ruleAttrDefOptions_in_entryRuleAttrDefOptions916);
            iv_ruleAttrDefOptions=ruleAttrDefOptions();

            state._fsp--;

             current =iv_ruleAttrDefOptions; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttrDefOptions926); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleAttrDefOptions"


    // $ANTLR start "ruleAttrDefOptions"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:474:1: ruleAttrDefOptions returns [EObject current=null] : (this_AttrValueDef_0= ruleAttrValueDef | this_AttrFullDef_1= ruleAttrFullDef ) ;
    public final EObject ruleAttrDefOptions() throws RecognitionException {
        EObject current = null;

        EObject this_AttrValueDef_0 = null;

        EObject this_AttrFullDef_1 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:477:28: ( (this_AttrValueDef_0= ruleAttrValueDef | this_AttrFullDef_1= ruleAttrFullDef ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:478:1: (this_AttrValueDef_0= ruleAttrValueDef | this_AttrFullDef_1= ruleAttrFullDef )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:478:1: (this_AttrValueDef_0= ruleAttrValueDef | this_AttrFullDef_1= ruleAttrFullDef )
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==22) ) {
                alt12=1;
            }
            else if ( (LA12_0==16) ) {
                alt12=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:479:5: this_AttrValueDef_0= ruleAttrValueDef
                    {
                     
                            newCompositeNode(grammarAccess.getAttrDefOptionsAccess().getAttrValueDefParserRuleCall_0()); 
                        
                    pushFollow(FOLLOW_ruleAttrValueDef_in_ruleAttrDefOptions973);
                    this_AttrValueDef_0=ruleAttrValueDef();

                    state._fsp--;

                     
                            current = this_AttrValueDef_0; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:489:5: this_AttrFullDef_1= ruleAttrFullDef
                    {
                     
                            newCompositeNode(grammarAccess.getAttrDefOptionsAccess().getAttrFullDefParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_ruleAttrFullDef_in_ruleAttrDefOptions1000);
                    this_AttrFullDef_1=ruleAttrFullDef();

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
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleAttrDefOptions"


    // $ANTLR start "entryRuleAttrValueDef"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:505:1: entryRuleAttrValueDef returns [EObject current=null] : iv_ruleAttrValueDef= ruleAttrValueDef EOF ;
    public final EObject entryRuleAttrValueDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttrValueDef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:506:2: (iv_ruleAttrValueDef= ruleAttrValueDef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:507:2: iv_ruleAttrValueDef= ruleAttrValueDef EOF
            {
             newCompositeNode(grammarAccess.getAttrValueDefRule()); 
            pushFollow(FOLLOW_ruleAttrValueDef_in_entryRuleAttrValueDef1035);
            iv_ruleAttrValueDef=ruleAttrValueDef();

            state._fsp--;

             current =iv_ruleAttrValueDef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttrValueDef1045); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleAttrValueDef"


    // $ANTLR start "ruleAttrValueDef"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:514:1: ruleAttrValueDef returns [EObject current=null] : (otherlv_0= 'value' ( (lv_value_1_0= RULE_STRING ) ) ) ;
    public final EObject ruleAttrValueDef() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_value_1_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:517:28: ( (otherlv_0= 'value' ( (lv_value_1_0= RULE_STRING ) ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:518:1: (otherlv_0= 'value' ( (lv_value_1_0= RULE_STRING ) ) )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:518:1: (otherlv_0= 'value' ( (lv_value_1_0= RULE_STRING ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:518:3: otherlv_0= 'value' ( (lv_value_1_0= RULE_STRING ) )
            {
            otherlv_0=(Token)match(input,22,FOLLOW_22_in_ruleAttrValueDef1082); 

                	newLeafNode(otherlv_0, grammarAccess.getAttrValueDefAccess().getValueKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:522:1: ( (lv_value_1_0= RULE_STRING ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:523:1: (lv_value_1_0= RULE_STRING )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:523:1: (lv_value_1_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:524:3: lv_value_1_0= RULE_STRING
            {
            lv_value_1_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAttrValueDef1099); 

            			newLeafNode(lv_value_1_0, grammarAccess.getAttrValueDefAccess().getValueSTRINGTerminalRuleCall_1_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getAttrValueDefRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"value",
                    		lv_value_1_0, 
                    		"STRING");
            	    

            }


            }


            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleAttrValueDef"


    // $ANTLR start "entryRuleAttrFullDef"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:548:1: entryRuleAttrFullDef returns [EObject current=null] : iv_ruleAttrFullDef= ruleAttrFullDef EOF ;
    public final EObject entryRuleAttrFullDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttrFullDef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:549:2: (iv_ruleAttrFullDef= ruleAttrFullDef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:550:2: iv_ruleAttrFullDef= ruleAttrFullDef EOF
            {
             newCompositeNode(grammarAccess.getAttrFullDefRule()); 
            pushFollow(FOLLOW_ruleAttrFullDef_in_entryRuleAttrFullDef1140);
            iv_ruleAttrFullDef=ruleAttrFullDef();

            state._fsp--;

             current =iv_ruleAttrFullDef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttrFullDef1150); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleAttrFullDef"


    // $ANTLR start "ruleAttrFullDef"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:557:1: ruleAttrFullDef returns [EObject current=null] : (otherlv_0= '{' (otherlv_1= 'uuid' ( (lv_uuid_2_0= RULE_STRING ) ) )? (otherlv_3= 'value' ( (lv_values_4_0= RULE_STRING ) ) )+ otherlv_5= '}' ) ;
    public final EObject ruleAttrFullDef() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;
        Token lv_uuid_2_0=null;
        Token otherlv_3=null;
        Token lv_values_4_0=null;
        Token otherlv_5=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:560:28: ( (otherlv_0= '{' (otherlv_1= 'uuid' ( (lv_uuid_2_0= RULE_STRING ) ) )? (otherlv_3= 'value' ( (lv_values_4_0= RULE_STRING ) ) )+ otherlv_5= '}' ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:561:1: (otherlv_0= '{' (otherlv_1= 'uuid' ( (lv_uuid_2_0= RULE_STRING ) ) )? (otherlv_3= 'value' ( (lv_values_4_0= RULE_STRING ) ) )+ otherlv_5= '}' )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:561:1: (otherlv_0= '{' (otherlv_1= 'uuid' ( (lv_uuid_2_0= RULE_STRING ) ) )? (otherlv_3= 'value' ( (lv_values_4_0= RULE_STRING ) ) )+ otherlv_5= '}' )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:561:3: otherlv_0= '{' (otherlv_1= 'uuid' ( (lv_uuid_2_0= RULE_STRING ) ) )? (otherlv_3= 'value' ( (lv_values_4_0= RULE_STRING ) ) )+ otherlv_5= '}'
            {
            otherlv_0=(Token)match(input,16,FOLLOW_16_in_ruleAttrFullDef1187); 

                	newLeafNode(otherlv_0, grammarAccess.getAttrFullDefAccess().getLeftCurlyBracketKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:565:1: (otherlv_1= 'uuid' ( (lv_uuid_2_0= RULE_STRING ) ) )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==23) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:565:3: otherlv_1= 'uuid' ( (lv_uuid_2_0= RULE_STRING ) )
                    {
                    otherlv_1=(Token)match(input,23,FOLLOW_23_in_ruleAttrFullDef1200); 

                        	newLeafNode(otherlv_1, grammarAccess.getAttrFullDefAccess().getUuidKeyword_1_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:569:1: ( (lv_uuid_2_0= RULE_STRING ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:570:1: (lv_uuid_2_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:570:1: (lv_uuid_2_0= RULE_STRING )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:571:3: lv_uuid_2_0= RULE_STRING
                    {
                    lv_uuid_2_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAttrFullDef1217); 

                    			newLeafNode(lv_uuid_2_0, grammarAccess.getAttrFullDefAccess().getUuidSTRINGTerminalRuleCall_1_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getAttrFullDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"uuid",
                            		lv_uuid_2_0, 
                            		"STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:587:4: (otherlv_3= 'value' ( (lv_values_4_0= RULE_STRING ) ) )+
            int cnt14=0;
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);

                if ( (LA14_0==22) ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:587:6: otherlv_3= 'value' ( (lv_values_4_0= RULE_STRING ) )
            	    {
            	    otherlv_3=(Token)match(input,22,FOLLOW_22_in_ruleAttrFullDef1237); 

            	        	newLeafNode(otherlv_3, grammarAccess.getAttrFullDefAccess().getValueKeyword_2_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:591:1: ( (lv_values_4_0= RULE_STRING ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:592:1: (lv_values_4_0= RULE_STRING )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:592:1: (lv_values_4_0= RULE_STRING )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:593:3: lv_values_4_0= RULE_STRING
            	    {
            	    lv_values_4_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAttrFullDef1254); 

            	    			newLeafNode(lv_values_4_0, grammarAccess.getAttrFullDefAccess().getValuesSTRINGTerminalRuleCall_2_1_0()); 
            	    		

            	    	        if (current==null) {
            	    	            current = createModelElement(grammarAccess.getAttrFullDefRule());
            	    	        }
            	           		addWithLastConsumed(
            	           			current, 
            	           			"values",
            	            		lv_values_4_0, 
            	            		"STRING");
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt14 >= 1 ) break loop14;
                        EarlyExitException eee =
                            new EarlyExitException(14, input);
                        throw eee;
                }
                cnt14++;
            } while (true);

            otherlv_5=(Token)match(input,21,FOLLOW_21_in_ruleAttrFullDef1273); 

                	newLeafNode(otherlv_5, grammarAccess.getAttrFullDefAccess().getRightCurlyBracketKeyword_3());
                

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleAttrFullDef"


    // $ANTLR start "entryRulePROGRAM_REFERENCE"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:621:1: entryRulePROGRAM_REFERENCE returns [String current=null] : iv_rulePROGRAM_REFERENCE= rulePROGRAM_REFERENCE EOF ;
    public final String entryRulePROGRAM_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_rulePROGRAM_REFERENCE = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:622:2: (iv_rulePROGRAM_REFERENCE= rulePROGRAM_REFERENCE EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:623:2: iv_rulePROGRAM_REFERENCE= rulePROGRAM_REFERENCE EOF
            {
             newCompositeNode(grammarAccess.getPROGRAM_REFERENCERule()); 
            pushFollow(FOLLOW_rulePROGRAM_REFERENCE_in_entryRulePROGRAM_REFERENCE1310);
            iv_rulePROGRAM_REFERENCE=rulePROGRAM_REFERENCE();

            state._fsp--;

             current =iv_rulePROGRAM_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRulePROGRAM_REFERENCE1321); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRulePROGRAM_REFERENCE"


    // $ANTLR start "rulePROGRAM_REFERENCE"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:630:1: rulePROGRAM_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken rulePROGRAM_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:633:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:634:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rulePROGRAM_REFERENCE1360); 

            		current.merge(this_STRING_0);
                
             
                newLeafNode(this_STRING_0, grammarAccess.getPROGRAM_REFERENCEAccess().getSTRINGTerminalRuleCall()); 
                

            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "rulePROGRAM_REFERENCE"


    // $ANTLR start "entryRuleProgramDef"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:649:1: entryRuleProgramDef returns [EObject current=null] : iv_ruleProgramDef= ruleProgramDef EOF ;
    public final EObject entryRuleProgramDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleProgramDef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:650:2: (iv_ruleProgramDef= ruleProgramDef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:651:2: iv_ruleProgramDef= ruleProgramDef EOF
            {
             newCompositeNode(grammarAccess.getProgramDefRule()); 
            pushFollow(FOLLOW_ruleProgramDef_in_entryRuleProgramDef1404);
            iv_ruleProgramDef=ruleProgramDef();

            state._fsp--;

             current =iv_ruleProgramDef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleProgramDef1414); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleProgramDef"


    // $ANTLR start "ruleProgramDef"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:658:1: ruleProgramDef returns [EObject current=null] : ( ( (lv_name_0_0= rulePROGRAM_REFERENCE ) ) ( (lv_programDefOption_1_0= ruleProgramDefOption ) )* otherlv_2= '{' (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )? (otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_INT ) ) )? (otherlv_7= 'artifactType' ( (lv_artifactTypeName_8_0= RULE_STRING ) ) )? (otherlv_9= 'active' ( (lv_active_10_0= ruleBooleanDef ) ) )? (otherlv_11= 'namespace' ( (lv_namespace_12_0= RULE_STRING ) ) )* (otherlv_13= 'teamDefinition' ( (lv_teamDefinition_14_0= ruleTEAM_DEF_REFERENCE ) ) )* (otherlv_15= 'attribute' ( (lv_attributes_16_0= ruleAttrDef ) ) )* otherlv_17= '}' ) ;
    public final EObject ruleProgramDef() throws RecognitionException {
        EObject current = null;

        Token otherlv_2=null;
        Token otherlv_3=null;
        Token lv_guid_4_0=null;
        Token otherlv_5=null;
        Token lv_uuid_6_0=null;
        Token otherlv_7=null;
        Token lv_artifactTypeName_8_0=null;
        Token otherlv_9=null;
        Token otherlv_11=null;
        Token lv_namespace_12_0=null;
        Token otherlv_13=null;
        Token otherlv_15=null;
        Token otherlv_17=null;
        AntlrDatatypeRuleToken lv_name_0_0 = null;

        AntlrDatatypeRuleToken lv_programDefOption_1_0 = null;

        Enumerator lv_active_10_0 = null;

        AntlrDatatypeRuleToken lv_teamDefinition_14_0 = null;

        EObject lv_attributes_16_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:661:28: ( ( ( (lv_name_0_0= rulePROGRAM_REFERENCE ) ) ( (lv_programDefOption_1_0= ruleProgramDefOption ) )* otherlv_2= '{' (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )? (otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_INT ) ) )? (otherlv_7= 'artifactType' ( (lv_artifactTypeName_8_0= RULE_STRING ) ) )? (otherlv_9= 'active' ( (lv_active_10_0= ruleBooleanDef ) ) )? (otherlv_11= 'namespace' ( (lv_namespace_12_0= RULE_STRING ) ) )* (otherlv_13= 'teamDefinition' ( (lv_teamDefinition_14_0= ruleTEAM_DEF_REFERENCE ) ) )* (otherlv_15= 'attribute' ( (lv_attributes_16_0= ruleAttrDef ) ) )* otherlv_17= '}' ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:662:1: ( ( (lv_name_0_0= rulePROGRAM_REFERENCE ) ) ( (lv_programDefOption_1_0= ruleProgramDefOption ) )* otherlv_2= '{' (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )? (otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_INT ) ) )? (otherlv_7= 'artifactType' ( (lv_artifactTypeName_8_0= RULE_STRING ) ) )? (otherlv_9= 'active' ( (lv_active_10_0= ruleBooleanDef ) ) )? (otherlv_11= 'namespace' ( (lv_namespace_12_0= RULE_STRING ) ) )* (otherlv_13= 'teamDefinition' ( (lv_teamDefinition_14_0= ruleTEAM_DEF_REFERENCE ) ) )* (otherlv_15= 'attribute' ( (lv_attributes_16_0= ruleAttrDef ) ) )* otherlv_17= '}' )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:662:1: ( ( (lv_name_0_0= rulePROGRAM_REFERENCE ) ) ( (lv_programDefOption_1_0= ruleProgramDefOption ) )* otherlv_2= '{' (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )? (otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_INT ) ) )? (otherlv_7= 'artifactType' ( (lv_artifactTypeName_8_0= RULE_STRING ) ) )? (otherlv_9= 'active' ( (lv_active_10_0= ruleBooleanDef ) ) )? (otherlv_11= 'namespace' ( (lv_namespace_12_0= RULE_STRING ) ) )* (otherlv_13= 'teamDefinition' ( (lv_teamDefinition_14_0= ruleTEAM_DEF_REFERENCE ) ) )* (otherlv_15= 'attribute' ( (lv_attributes_16_0= ruleAttrDef ) ) )* otherlv_17= '}' )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:662:2: ( (lv_name_0_0= rulePROGRAM_REFERENCE ) ) ( (lv_programDefOption_1_0= ruleProgramDefOption ) )* otherlv_2= '{' (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )? (otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_INT ) ) )? (otherlv_7= 'artifactType' ( (lv_artifactTypeName_8_0= RULE_STRING ) ) )? (otherlv_9= 'active' ( (lv_active_10_0= ruleBooleanDef ) ) )? (otherlv_11= 'namespace' ( (lv_namespace_12_0= RULE_STRING ) ) )* (otherlv_13= 'teamDefinition' ( (lv_teamDefinition_14_0= ruleTEAM_DEF_REFERENCE ) ) )* (otherlv_15= 'attribute' ( (lv_attributes_16_0= ruleAttrDef ) ) )* otherlv_17= '}'
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:662:2: ( (lv_name_0_0= rulePROGRAM_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:663:1: (lv_name_0_0= rulePROGRAM_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:663:1: (lv_name_0_0= rulePROGRAM_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:664:3: lv_name_0_0= rulePROGRAM_REFERENCE
            {
             
            	        newCompositeNode(grammarAccess.getProgramDefAccess().getNamePROGRAM_REFERENCEParserRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_rulePROGRAM_REFERENCE_in_ruleProgramDef1460);
            lv_name_0_0=rulePROGRAM_REFERENCE();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getProgramDefRule());
            	        }
                   		set(
                   			current, 
                   			"name",
                    		lv_name_0_0, 
                    		"PROGRAM_REFERENCE");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:680:2: ( (lv_programDefOption_1_0= ruleProgramDefOption ) )*
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( (LA15_0==RULE_STRING||LA15_0==85) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:681:1: (lv_programDefOption_1_0= ruleProgramDefOption )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:681:1: (lv_programDefOption_1_0= ruleProgramDefOption )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:682:3: lv_programDefOption_1_0= ruleProgramDefOption
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getProgramDefAccess().getProgramDefOptionProgramDefOptionParserRuleCall_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleProgramDefOption_in_ruleProgramDef1481);
            	    lv_programDefOption_1_0=ruleProgramDefOption();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getProgramDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"programDefOption",
            	            		lv_programDefOption_1_0, 
            	            		"ProgramDefOption");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    break loop15;
                }
            } while (true);

            otherlv_2=(Token)match(input,16,FOLLOW_16_in_ruleProgramDef1494); 

                	newLeafNode(otherlv_2, grammarAccess.getProgramDefAccess().getLeftCurlyBracketKeyword_2());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:702:1: (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==24) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:702:3: otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) )
                    {
                    otherlv_3=(Token)match(input,24,FOLLOW_24_in_ruleProgramDef1507); 

                        	newLeafNode(otherlv_3, grammarAccess.getProgramDefAccess().getGuidKeyword_3_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:706:1: ( (lv_guid_4_0= RULE_STRING ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:707:1: (lv_guid_4_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:707:1: (lv_guid_4_0= RULE_STRING )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:708:3: lv_guid_4_0= RULE_STRING
                    {
                    lv_guid_4_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleProgramDef1524); 

                    			newLeafNode(lv_guid_4_0, grammarAccess.getProgramDefAccess().getGuidSTRINGTerminalRuleCall_3_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getProgramDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"guid",
                            		lv_guid_4_0, 
                            		"STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:724:4: (otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_INT ) ) )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==23) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:724:6: otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_INT ) )
                    {
                    otherlv_5=(Token)match(input,23,FOLLOW_23_in_ruleProgramDef1544); 

                        	newLeafNode(otherlv_5, grammarAccess.getProgramDefAccess().getUuidKeyword_4_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:728:1: ( (lv_uuid_6_0= RULE_INT ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:729:1: (lv_uuid_6_0= RULE_INT )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:729:1: (lv_uuid_6_0= RULE_INT )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:730:3: lv_uuid_6_0= RULE_INT
                    {
                    lv_uuid_6_0=(Token)match(input,RULE_INT,FOLLOW_RULE_INT_in_ruleProgramDef1561); 

                    			newLeafNode(lv_uuid_6_0, grammarAccess.getProgramDefAccess().getUuidINTTerminalRuleCall_4_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getProgramDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"uuid",
                            		lv_uuid_6_0, 
                            		"INT");
                    	    

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:746:4: (otherlv_7= 'artifactType' ( (lv_artifactTypeName_8_0= RULE_STRING ) ) )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==25) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:746:6: otherlv_7= 'artifactType' ( (lv_artifactTypeName_8_0= RULE_STRING ) )
                    {
                    otherlv_7=(Token)match(input,25,FOLLOW_25_in_ruleProgramDef1581); 

                        	newLeafNode(otherlv_7, grammarAccess.getProgramDefAccess().getArtifactTypeKeyword_5_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:750:1: ( (lv_artifactTypeName_8_0= RULE_STRING ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:751:1: (lv_artifactTypeName_8_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:751:1: (lv_artifactTypeName_8_0= RULE_STRING )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:752:3: lv_artifactTypeName_8_0= RULE_STRING
                    {
                    lv_artifactTypeName_8_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleProgramDef1598); 

                    			newLeafNode(lv_artifactTypeName_8_0, grammarAccess.getProgramDefAccess().getArtifactTypeNameSTRINGTerminalRuleCall_5_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getProgramDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"artifactTypeName",
                            		lv_artifactTypeName_8_0, 
                            		"STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:768:4: (otherlv_9= 'active' ( (lv_active_10_0= ruleBooleanDef ) ) )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==17) ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:768:6: otherlv_9= 'active' ( (lv_active_10_0= ruleBooleanDef ) )
                    {
                    otherlv_9=(Token)match(input,17,FOLLOW_17_in_ruleProgramDef1618); 

                        	newLeafNode(otherlv_9, grammarAccess.getProgramDefAccess().getActiveKeyword_6_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:772:1: ( (lv_active_10_0= ruleBooleanDef ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:773:1: (lv_active_10_0= ruleBooleanDef )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:773:1: (lv_active_10_0= ruleBooleanDef )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:774:3: lv_active_10_0= ruleBooleanDef
                    {
                     
                    	        newCompositeNode(grammarAccess.getProgramDefAccess().getActiveBooleanDefEnumRuleCall_6_1_0()); 
                    	    
                    pushFollow(FOLLOW_ruleBooleanDef_in_ruleProgramDef1639);
                    lv_active_10_0=ruleBooleanDef();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getProgramDefRule());
                    	        }
                           		set(
                           			current, 
                           			"active",
                            		lv_active_10_0, 
                            		"BooleanDef");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:790:4: (otherlv_11= 'namespace' ( (lv_namespace_12_0= RULE_STRING ) ) )*
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( (LA20_0==26) ) {
                    alt20=1;
                }


                switch (alt20) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:790:6: otherlv_11= 'namespace' ( (lv_namespace_12_0= RULE_STRING ) )
            	    {
            	    otherlv_11=(Token)match(input,26,FOLLOW_26_in_ruleProgramDef1654); 

            	        	newLeafNode(otherlv_11, grammarAccess.getProgramDefAccess().getNamespaceKeyword_7_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:794:1: ( (lv_namespace_12_0= RULE_STRING ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:795:1: (lv_namespace_12_0= RULE_STRING )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:795:1: (lv_namespace_12_0= RULE_STRING )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:796:3: lv_namespace_12_0= RULE_STRING
            	    {
            	    lv_namespace_12_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleProgramDef1671); 

            	    			newLeafNode(lv_namespace_12_0, grammarAccess.getProgramDefAccess().getNamespaceSTRINGTerminalRuleCall_7_1_0()); 
            	    		

            	    	        if (current==null) {
            	    	            current = createModelElement(grammarAccess.getProgramDefRule());
            	    	        }
            	           		setWithLastConsumed(
            	           			current, 
            	           			"namespace",
            	            		lv_namespace_12_0, 
            	            		"STRING");
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop20;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:812:4: (otherlv_13= 'teamDefinition' ( (lv_teamDefinition_14_0= ruleTEAM_DEF_REFERENCE ) ) )*
            loop21:
            do {
                int alt21=2;
                int LA21_0 = input.LA(1);

                if ( (LA21_0==12) ) {
                    alt21=1;
                }


                switch (alt21) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:812:6: otherlv_13= 'teamDefinition' ( (lv_teamDefinition_14_0= ruleTEAM_DEF_REFERENCE ) )
            	    {
            	    otherlv_13=(Token)match(input,12,FOLLOW_12_in_ruleProgramDef1691); 

            	        	newLeafNode(otherlv_13, grammarAccess.getProgramDefAccess().getTeamDefinitionKeyword_8_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:816:1: ( (lv_teamDefinition_14_0= ruleTEAM_DEF_REFERENCE ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:817:1: (lv_teamDefinition_14_0= ruleTEAM_DEF_REFERENCE )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:817:1: (lv_teamDefinition_14_0= ruleTEAM_DEF_REFERENCE )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:818:3: lv_teamDefinition_14_0= ruleTEAM_DEF_REFERENCE
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getProgramDefAccess().getTeamDefinitionTEAM_DEF_REFERENCEParserRuleCall_8_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleTEAM_DEF_REFERENCE_in_ruleProgramDef1712);
            	    lv_teamDefinition_14_0=ruleTEAM_DEF_REFERENCE();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getProgramDefRule());
            	    	        }
            	           		set(
            	           			current, 
            	           			"teamDefinition",
            	            		lv_teamDefinition_14_0, 
            	            		"TEAM_DEF_REFERENCE");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop21;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:834:4: (otherlv_15= 'attribute' ( (lv_attributes_16_0= ruleAttrDef ) ) )*
            loop22:
            do {
                int alt22=2;
                int LA22_0 = input.LA(1);

                if ( (LA22_0==27) ) {
                    alt22=1;
                }


                switch (alt22) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:834:6: otherlv_15= 'attribute' ( (lv_attributes_16_0= ruleAttrDef ) )
            	    {
            	    otherlv_15=(Token)match(input,27,FOLLOW_27_in_ruleProgramDef1727); 

            	        	newLeafNode(otherlv_15, grammarAccess.getProgramDefAccess().getAttributeKeyword_9_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:838:1: ( (lv_attributes_16_0= ruleAttrDef ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:839:1: (lv_attributes_16_0= ruleAttrDef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:839:1: (lv_attributes_16_0= ruleAttrDef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:840:3: lv_attributes_16_0= ruleAttrDef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getProgramDefAccess().getAttributesAttrDefParserRuleCall_9_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleAttrDef_in_ruleProgramDef1748);
            	    lv_attributes_16_0=ruleAttrDef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getProgramDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"attributes",
            	            		lv_attributes_16_0, 
            	            		"AttrDef");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop22;
                }
            } while (true);

            otherlv_17=(Token)match(input,21,FOLLOW_21_in_ruleProgramDef1762); 

                	newLeafNode(otherlv_17, grammarAccess.getProgramDefAccess().getRightCurlyBracketKeyword_10());
                

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleProgramDef"


    // $ANTLR start "entryRuleTEAM_DEF_REFERENCE"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:868:1: entryRuleTEAM_DEF_REFERENCE returns [String current=null] : iv_ruleTEAM_DEF_REFERENCE= ruleTEAM_DEF_REFERENCE EOF ;
    public final String entryRuleTEAM_DEF_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleTEAM_DEF_REFERENCE = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:869:2: (iv_ruleTEAM_DEF_REFERENCE= ruleTEAM_DEF_REFERENCE EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:870:2: iv_ruleTEAM_DEF_REFERENCE= ruleTEAM_DEF_REFERENCE EOF
            {
             newCompositeNode(grammarAccess.getTEAM_DEF_REFERENCERule()); 
            pushFollow(FOLLOW_ruleTEAM_DEF_REFERENCE_in_entryRuleTEAM_DEF_REFERENCE1799);
            iv_ruleTEAM_DEF_REFERENCE=ruleTEAM_DEF_REFERENCE();

            state._fsp--;

             current =iv_ruleTEAM_DEF_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleTEAM_DEF_REFERENCE1810); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleTEAM_DEF_REFERENCE"


    // $ANTLR start "ruleTEAM_DEF_REFERENCE"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:877:1: ruleTEAM_DEF_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleTEAM_DEF_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:880:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:881:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleTEAM_DEF_REFERENCE1849); 

            		current.merge(this_STRING_0);
                
             
                newLeafNode(this_STRING_0, grammarAccess.getTEAM_DEF_REFERENCEAccess().getSTRINGTerminalRuleCall()); 
                

            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleTEAM_DEF_REFERENCE"


    // $ANTLR start "entryRuleTeamDef"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:896:1: entryRuleTeamDef returns [EObject current=null] : iv_ruleTeamDef= ruleTeamDef EOF ;
    public final EObject entryRuleTeamDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleTeamDef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:897:2: (iv_ruleTeamDef= ruleTeamDef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:898:2: iv_ruleTeamDef= ruleTeamDef EOF
            {
             newCompositeNode(grammarAccess.getTeamDefRule()); 
            pushFollow(FOLLOW_ruleTeamDef_in_entryRuleTeamDef1893);
            iv_ruleTeamDef=ruleTeamDef();

            state._fsp--;

             current =iv_ruleTeamDef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleTeamDef1903); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleTeamDef"


    // $ANTLR start "ruleTeamDef"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:905:1: ruleTeamDef returns [EObject current=null] : ( ( (lv_name_0_0= ruleTEAM_DEF_REFERENCE ) ) ( (lv_teamDefOption_1_0= ruleTeamDefOption ) )* otherlv_2= '{' (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )? (otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_INT ) ) )? (otherlv_7= 'active' ( (lv_active_8_0= ruleBooleanDef ) ) )? (otherlv_9= 'staticId' ( (lv_staticId_10_0= RULE_STRING ) ) )* (otherlv_11= 'lead' ( (lv_lead_12_0= ruleUserRef ) ) )* (otherlv_13= 'member' ( (lv_member_14_0= ruleUserRef ) ) )* (otherlv_15= 'privileged' ( (lv_privileged_16_0= ruleUserRef ) ) )* (otherlv_17= 'workDefinition' ( (lv_workDefinition_18_0= RULE_STRING ) ) )? (otherlv_19= 'relatedTaskWorkDefinition' ( (lv_relatedTaskWorkDefinition_20_0= RULE_STRING ) ) )? (otherlv_21= 'teamWorkflowArtifactType' ( (lv_teamWorkflowArtifactType_22_0= RULE_STRING ) ) )? (otherlv_23= 'accessContextId' ( (lv_accessContextId_24_0= RULE_STRING ) ) )* (otherlv_25= 'version' ( (lv_version_26_0= ruleVersionDef ) ) )* (otherlv_27= 'children' otherlv_28= '{' (otherlv_29= 'teamDefinition' ( (lv_children_30_0= ruleTeamDef ) ) )+ otherlv_31= '}' )? otherlv_32= '}' ) ;
    public final EObject ruleTeamDef() throws RecognitionException {
        EObject current = null;

        Token otherlv_2=null;
        Token otherlv_3=null;
        Token lv_guid_4_0=null;
        Token otherlv_5=null;
        Token lv_uuid_6_0=null;
        Token otherlv_7=null;
        Token otherlv_9=null;
        Token lv_staticId_10_0=null;
        Token otherlv_11=null;
        Token otherlv_13=null;
        Token otherlv_15=null;
        Token otherlv_17=null;
        Token lv_workDefinition_18_0=null;
        Token otherlv_19=null;
        Token lv_relatedTaskWorkDefinition_20_0=null;
        Token otherlv_21=null;
        Token lv_teamWorkflowArtifactType_22_0=null;
        Token otherlv_23=null;
        Token lv_accessContextId_24_0=null;
        Token otherlv_25=null;
        Token otherlv_27=null;
        Token otherlv_28=null;
        Token otherlv_29=null;
        Token otherlv_31=null;
        Token otherlv_32=null;
        AntlrDatatypeRuleToken lv_name_0_0 = null;

        AntlrDatatypeRuleToken lv_teamDefOption_1_0 = null;

        Enumerator lv_active_8_0 = null;

        EObject lv_lead_12_0 = null;

        EObject lv_member_14_0 = null;

        EObject lv_privileged_16_0 = null;

        EObject lv_version_26_0 = null;

        EObject lv_children_30_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:908:28: ( ( ( (lv_name_0_0= ruleTEAM_DEF_REFERENCE ) ) ( (lv_teamDefOption_1_0= ruleTeamDefOption ) )* otherlv_2= '{' (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )? (otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_INT ) ) )? (otherlv_7= 'active' ( (lv_active_8_0= ruleBooleanDef ) ) )? (otherlv_9= 'staticId' ( (lv_staticId_10_0= RULE_STRING ) ) )* (otherlv_11= 'lead' ( (lv_lead_12_0= ruleUserRef ) ) )* (otherlv_13= 'member' ( (lv_member_14_0= ruleUserRef ) ) )* (otherlv_15= 'privileged' ( (lv_privileged_16_0= ruleUserRef ) ) )* (otherlv_17= 'workDefinition' ( (lv_workDefinition_18_0= RULE_STRING ) ) )? (otherlv_19= 'relatedTaskWorkDefinition' ( (lv_relatedTaskWorkDefinition_20_0= RULE_STRING ) ) )? (otherlv_21= 'teamWorkflowArtifactType' ( (lv_teamWorkflowArtifactType_22_0= RULE_STRING ) ) )? (otherlv_23= 'accessContextId' ( (lv_accessContextId_24_0= RULE_STRING ) ) )* (otherlv_25= 'version' ( (lv_version_26_0= ruleVersionDef ) ) )* (otherlv_27= 'children' otherlv_28= '{' (otherlv_29= 'teamDefinition' ( (lv_children_30_0= ruleTeamDef ) ) )+ otherlv_31= '}' )? otherlv_32= '}' ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:909:1: ( ( (lv_name_0_0= ruleTEAM_DEF_REFERENCE ) ) ( (lv_teamDefOption_1_0= ruleTeamDefOption ) )* otherlv_2= '{' (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )? (otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_INT ) ) )? (otherlv_7= 'active' ( (lv_active_8_0= ruleBooleanDef ) ) )? (otherlv_9= 'staticId' ( (lv_staticId_10_0= RULE_STRING ) ) )* (otherlv_11= 'lead' ( (lv_lead_12_0= ruleUserRef ) ) )* (otherlv_13= 'member' ( (lv_member_14_0= ruleUserRef ) ) )* (otherlv_15= 'privileged' ( (lv_privileged_16_0= ruleUserRef ) ) )* (otherlv_17= 'workDefinition' ( (lv_workDefinition_18_0= RULE_STRING ) ) )? (otherlv_19= 'relatedTaskWorkDefinition' ( (lv_relatedTaskWorkDefinition_20_0= RULE_STRING ) ) )? (otherlv_21= 'teamWorkflowArtifactType' ( (lv_teamWorkflowArtifactType_22_0= RULE_STRING ) ) )? (otherlv_23= 'accessContextId' ( (lv_accessContextId_24_0= RULE_STRING ) ) )* (otherlv_25= 'version' ( (lv_version_26_0= ruleVersionDef ) ) )* (otherlv_27= 'children' otherlv_28= '{' (otherlv_29= 'teamDefinition' ( (lv_children_30_0= ruleTeamDef ) ) )+ otherlv_31= '}' )? otherlv_32= '}' )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:909:1: ( ( (lv_name_0_0= ruleTEAM_DEF_REFERENCE ) ) ( (lv_teamDefOption_1_0= ruleTeamDefOption ) )* otherlv_2= '{' (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )? (otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_INT ) ) )? (otherlv_7= 'active' ( (lv_active_8_0= ruleBooleanDef ) ) )? (otherlv_9= 'staticId' ( (lv_staticId_10_0= RULE_STRING ) ) )* (otherlv_11= 'lead' ( (lv_lead_12_0= ruleUserRef ) ) )* (otherlv_13= 'member' ( (lv_member_14_0= ruleUserRef ) ) )* (otherlv_15= 'privileged' ( (lv_privileged_16_0= ruleUserRef ) ) )* (otherlv_17= 'workDefinition' ( (lv_workDefinition_18_0= RULE_STRING ) ) )? (otherlv_19= 'relatedTaskWorkDefinition' ( (lv_relatedTaskWorkDefinition_20_0= RULE_STRING ) ) )? (otherlv_21= 'teamWorkflowArtifactType' ( (lv_teamWorkflowArtifactType_22_0= RULE_STRING ) ) )? (otherlv_23= 'accessContextId' ( (lv_accessContextId_24_0= RULE_STRING ) ) )* (otherlv_25= 'version' ( (lv_version_26_0= ruleVersionDef ) ) )* (otherlv_27= 'children' otherlv_28= '{' (otherlv_29= 'teamDefinition' ( (lv_children_30_0= ruleTeamDef ) ) )+ otherlv_31= '}' )? otherlv_32= '}' )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:909:2: ( (lv_name_0_0= ruleTEAM_DEF_REFERENCE ) ) ( (lv_teamDefOption_1_0= ruleTeamDefOption ) )* otherlv_2= '{' (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )? (otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_INT ) ) )? (otherlv_7= 'active' ( (lv_active_8_0= ruleBooleanDef ) ) )? (otherlv_9= 'staticId' ( (lv_staticId_10_0= RULE_STRING ) ) )* (otherlv_11= 'lead' ( (lv_lead_12_0= ruleUserRef ) ) )* (otherlv_13= 'member' ( (lv_member_14_0= ruleUserRef ) ) )* (otherlv_15= 'privileged' ( (lv_privileged_16_0= ruleUserRef ) ) )* (otherlv_17= 'workDefinition' ( (lv_workDefinition_18_0= RULE_STRING ) ) )? (otherlv_19= 'relatedTaskWorkDefinition' ( (lv_relatedTaskWorkDefinition_20_0= RULE_STRING ) ) )? (otherlv_21= 'teamWorkflowArtifactType' ( (lv_teamWorkflowArtifactType_22_0= RULE_STRING ) ) )? (otherlv_23= 'accessContextId' ( (lv_accessContextId_24_0= RULE_STRING ) ) )* (otherlv_25= 'version' ( (lv_version_26_0= ruleVersionDef ) ) )* (otherlv_27= 'children' otherlv_28= '{' (otherlv_29= 'teamDefinition' ( (lv_children_30_0= ruleTeamDef ) ) )+ otherlv_31= '}' )? otherlv_32= '}'
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:909:2: ( (lv_name_0_0= ruleTEAM_DEF_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:910:1: (lv_name_0_0= ruleTEAM_DEF_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:910:1: (lv_name_0_0= ruleTEAM_DEF_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:911:3: lv_name_0_0= ruleTEAM_DEF_REFERENCE
            {
             
            	        newCompositeNode(grammarAccess.getTeamDefAccess().getNameTEAM_DEF_REFERENCEParserRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_ruleTEAM_DEF_REFERENCE_in_ruleTeamDef1949);
            lv_name_0_0=ruleTEAM_DEF_REFERENCE();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getTeamDefRule());
            	        }
                   		set(
                   			current, 
                   			"name",
                    		lv_name_0_0, 
                    		"TEAM_DEF_REFERENCE");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:927:2: ( (lv_teamDefOption_1_0= ruleTeamDefOption ) )*
            loop23:
            do {
                int alt23=2;
                int LA23_0 = input.LA(1);

                if ( (LA23_0==RULE_STRING||LA23_0==85) ) {
                    alt23=1;
                }


                switch (alt23) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:928:1: (lv_teamDefOption_1_0= ruleTeamDefOption )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:928:1: (lv_teamDefOption_1_0= ruleTeamDefOption )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:929:3: lv_teamDefOption_1_0= ruleTeamDefOption
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getTeamDefAccess().getTeamDefOptionTeamDefOptionParserRuleCall_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleTeamDefOption_in_ruleTeamDef1970);
            	    lv_teamDefOption_1_0=ruleTeamDefOption();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getTeamDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"teamDefOption",
            	            		lv_teamDefOption_1_0, 
            	            		"TeamDefOption");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    break loop23;
                }
            } while (true);

            otherlv_2=(Token)match(input,16,FOLLOW_16_in_ruleTeamDef1983); 

                	newLeafNode(otherlv_2, grammarAccess.getTeamDefAccess().getLeftCurlyBracketKeyword_2());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:949:1: (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )?
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==24) ) {
                alt24=1;
            }
            switch (alt24) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:949:3: otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) )
                    {
                    otherlv_3=(Token)match(input,24,FOLLOW_24_in_ruleTeamDef1996); 

                        	newLeafNode(otherlv_3, grammarAccess.getTeamDefAccess().getGuidKeyword_3_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:953:1: ( (lv_guid_4_0= RULE_STRING ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:954:1: (lv_guid_4_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:954:1: (lv_guid_4_0= RULE_STRING )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:955:3: lv_guid_4_0= RULE_STRING
                    {
                    lv_guid_4_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleTeamDef2013); 

                    			newLeafNode(lv_guid_4_0, grammarAccess.getTeamDefAccess().getGuidSTRINGTerminalRuleCall_3_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getTeamDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"guid",
                            		lv_guid_4_0, 
                            		"STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:971:4: (otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_INT ) ) )?
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==23) ) {
                alt25=1;
            }
            switch (alt25) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:971:6: otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_INT ) )
                    {
                    otherlv_5=(Token)match(input,23,FOLLOW_23_in_ruleTeamDef2033); 

                        	newLeafNode(otherlv_5, grammarAccess.getTeamDefAccess().getUuidKeyword_4_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:975:1: ( (lv_uuid_6_0= RULE_INT ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:976:1: (lv_uuid_6_0= RULE_INT )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:976:1: (lv_uuid_6_0= RULE_INT )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:977:3: lv_uuid_6_0= RULE_INT
                    {
                    lv_uuid_6_0=(Token)match(input,RULE_INT,FOLLOW_RULE_INT_in_ruleTeamDef2050); 

                    			newLeafNode(lv_uuid_6_0, grammarAccess.getTeamDefAccess().getUuidINTTerminalRuleCall_4_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getTeamDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"uuid",
                            		lv_uuid_6_0, 
                            		"INT");
                    	    

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:993:4: (otherlv_7= 'active' ( (lv_active_8_0= ruleBooleanDef ) ) )?
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==17) ) {
                alt26=1;
            }
            switch (alt26) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:993:6: otherlv_7= 'active' ( (lv_active_8_0= ruleBooleanDef ) )
                    {
                    otherlv_7=(Token)match(input,17,FOLLOW_17_in_ruleTeamDef2070); 

                        	newLeafNode(otherlv_7, grammarAccess.getTeamDefAccess().getActiveKeyword_5_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:997:1: ( (lv_active_8_0= ruleBooleanDef ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:998:1: (lv_active_8_0= ruleBooleanDef )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:998:1: (lv_active_8_0= ruleBooleanDef )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:999:3: lv_active_8_0= ruleBooleanDef
                    {
                     
                    	        newCompositeNode(grammarAccess.getTeamDefAccess().getActiveBooleanDefEnumRuleCall_5_1_0()); 
                    	    
                    pushFollow(FOLLOW_ruleBooleanDef_in_ruleTeamDef2091);
                    lv_active_8_0=ruleBooleanDef();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getTeamDefRule());
                    	        }
                           		set(
                           			current, 
                           			"active",
                            		lv_active_8_0, 
                            		"BooleanDef");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1015:4: (otherlv_9= 'staticId' ( (lv_staticId_10_0= RULE_STRING ) ) )*
            loop27:
            do {
                int alt27=2;
                int LA27_0 = input.LA(1);

                if ( (LA27_0==28) ) {
                    alt27=1;
                }


                switch (alt27) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1015:6: otherlv_9= 'staticId' ( (lv_staticId_10_0= RULE_STRING ) )
            	    {
            	    otherlv_9=(Token)match(input,28,FOLLOW_28_in_ruleTeamDef2106); 

            	        	newLeafNode(otherlv_9, grammarAccess.getTeamDefAccess().getStaticIdKeyword_6_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1019:1: ( (lv_staticId_10_0= RULE_STRING ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1020:1: (lv_staticId_10_0= RULE_STRING )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1020:1: (lv_staticId_10_0= RULE_STRING )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1021:3: lv_staticId_10_0= RULE_STRING
            	    {
            	    lv_staticId_10_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleTeamDef2123); 

            	    			newLeafNode(lv_staticId_10_0, grammarAccess.getTeamDefAccess().getStaticIdSTRINGTerminalRuleCall_6_1_0()); 
            	    		

            	    	        if (current==null) {
            	    	            current = createModelElement(grammarAccess.getTeamDefRule());
            	    	        }
            	           		addWithLastConsumed(
            	           			current, 
            	           			"staticId",
            	            		lv_staticId_10_0, 
            	            		"STRING");
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop27;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1037:4: (otherlv_11= 'lead' ( (lv_lead_12_0= ruleUserRef ) ) )*
            loop28:
            do {
                int alt28=2;
                int LA28_0 = input.LA(1);

                if ( (LA28_0==29) ) {
                    alt28=1;
                }


                switch (alt28) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1037:6: otherlv_11= 'lead' ( (lv_lead_12_0= ruleUserRef ) )
            	    {
            	    otherlv_11=(Token)match(input,29,FOLLOW_29_in_ruleTeamDef2143); 

            	        	newLeafNode(otherlv_11, grammarAccess.getTeamDefAccess().getLeadKeyword_7_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1041:1: ( (lv_lead_12_0= ruleUserRef ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1042:1: (lv_lead_12_0= ruleUserRef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1042:1: (lv_lead_12_0= ruleUserRef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1043:3: lv_lead_12_0= ruleUserRef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getTeamDefAccess().getLeadUserRefParserRuleCall_7_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleUserRef_in_ruleTeamDef2164);
            	    lv_lead_12_0=ruleUserRef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getTeamDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"lead",
            	            		lv_lead_12_0, 
            	            		"UserRef");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop28;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1059:4: (otherlv_13= 'member' ( (lv_member_14_0= ruleUserRef ) ) )*
            loop29:
            do {
                int alt29=2;
                int LA29_0 = input.LA(1);

                if ( (LA29_0==30) ) {
                    alt29=1;
                }


                switch (alt29) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1059:6: otherlv_13= 'member' ( (lv_member_14_0= ruleUserRef ) )
            	    {
            	    otherlv_13=(Token)match(input,30,FOLLOW_30_in_ruleTeamDef2179); 

            	        	newLeafNode(otherlv_13, grammarAccess.getTeamDefAccess().getMemberKeyword_8_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1063:1: ( (lv_member_14_0= ruleUserRef ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1064:1: (lv_member_14_0= ruleUserRef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1064:1: (lv_member_14_0= ruleUserRef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1065:3: lv_member_14_0= ruleUserRef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getTeamDefAccess().getMemberUserRefParserRuleCall_8_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleUserRef_in_ruleTeamDef2200);
            	    lv_member_14_0=ruleUserRef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getTeamDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"member",
            	            		lv_member_14_0, 
            	            		"UserRef");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop29;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1081:4: (otherlv_15= 'privileged' ( (lv_privileged_16_0= ruleUserRef ) ) )*
            loop30:
            do {
                int alt30=2;
                int LA30_0 = input.LA(1);

                if ( (LA30_0==31) ) {
                    alt30=1;
                }


                switch (alt30) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1081:6: otherlv_15= 'privileged' ( (lv_privileged_16_0= ruleUserRef ) )
            	    {
            	    otherlv_15=(Token)match(input,31,FOLLOW_31_in_ruleTeamDef2215); 

            	        	newLeafNode(otherlv_15, grammarAccess.getTeamDefAccess().getPrivilegedKeyword_9_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1085:1: ( (lv_privileged_16_0= ruleUserRef ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1086:1: (lv_privileged_16_0= ruleUserRef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1086:1: (lv_privileged_16_0= ruleUserRef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1087:3: lv_privileged_16_0= ruleUserRef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getTeamDefAccess().getPrivilegedUserRefParserRuleCall_9_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleUserRef_in_ruleTeamDef2236);
            	    lv_privileged_16_0=ruleUserRef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getTeamDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"privileged",
            	            		lv_privileged_16_0, 
            	            		"UserRef");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop30;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1103:4: (otherlv_17= 'workDefinition' ( (lv_workDefinition_18_0= RULE_STRING ) ) )?
            int alt31=2;
            int LA31_0 = input.LA(1);

            if ( (LA31_0==14) ) {
                alt31=1;
            }
            switch (alt31) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1103:6: otherlv_17= 'workDefinition' ( (lv_workDefinition_18_0= RULE_STRING ) )
                    {
                    otherlv_17=(Token)match(input,14,FOLLOW_14_in_ruleTeamDef2251); 

                        	newLeafNode(otherlv_17, grammarAccess.getTeamDefAccess().getWorkDefinitionKeyword_10_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1107:1: ( (lv_workDefinition_18_0= RULE_STRING ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1108:1: (lv_workDefinition_18_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1108:1: (lv_workDefinition_18_0= RULE_STRING )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1109:3: lv_workDefinition_18_0= RULE_STRING
                    {
                    lv_workDefinition_18_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleTeamDef2268); 

                    			newLeafNode(lv_workDefinition_18_0, grammarAccess.getTeamDefAccess().getWorkDefinitionSTRINGTerminalRuleCall_10_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getTeamDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"workDefinition",
                            		lv_workDefinition_18_0, 
                            		"STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1125:4: (otherlv_19= 'relatedTaskWorkDefinition' ( (lv_relatedTaskWorkDefinition_20_0= RULE_STRING ) ) )?
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==32) ) {
                alt32=1;
            }
            switch (alt32) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1125:6: otherlv_19= 'relatedTaskWorkDefinition' ( (lv_relatedTaskWorkDefinition_20_0= RULE_STRING ) )
                    {
                    otherlv_19=(Token)match(input,32,FOLLOW_32_in_ruleTeamDef2288); 

                        	newLeafNode(otherlv_19, grammarAccess.getTeamDefAccess().getRelatedTaskWorkDefinitionKeyword_11_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1129:1: ( (lv_relatedTaskWorkDefinition_20_0= RULE_STRING ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1130:1: (lv_relatedTaskWorkDefinition_20_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1130:1: (lv_relatedTaskWorkDefinition_20_0= RULE_STRING )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1131:3: lv_relatedTaskWorkDefinition_20_0= RULE_STRING
                    {
                    lv_relatedTaskWorkDefinition_20_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleTeamDef2305); 

                    			newLeafNode(lv_relatedTaskWorkDefinition_20_0, grammarAccess.getTeamDefAccess().getRelatedTaskWorkDefinitionSTRINGTerminalRuleCall_11_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getTeamDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"relatedTaskWorkDefinition",
                            		lv_relatedTaskWorkDefinition_20_0, 
                            		"STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1147:4: (otherlv_21= 'teamWorkflowArtifactType' ( (lv_teamWorkflowArtifactType_22_0= RULE_STRING ) ) )?
            int alt33=2;
            int LA33_0 = input.LA(1);

            if ( (LA33_0==33) ) {
                alt33=1;
            }
            switch (alt33) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1147:6: otherlv_21= 'teamWorkflowArtifactType' ( (lv_teamWorkflowArtifactType_22_0= RULE_STRING ) )
                    {
                    otherlv_21=(Token)match(input,33,FOLLOW_33_in_ruleTeamDef2325); 

                        	newLeafNode(otherlv_21, grammarAccess.getTeamDefAccess().getTeamWorkflowArtifactTypeKeyword_12_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1151:1: ( (lv_teamWorkflowArtifactType_22_0= RULE_STRING ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1152:1: (lv_teamWorkflowArtifactType_22_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1152:1: (lv_teamWorkflowArtifactType_22_0= RULE_STRING )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1153:3: lv_teamWorkflowArtifactType_22_0= RULE_STRING
                    {
                    lv_teamWorkflowArtifactType_22_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleTeamDef2342); 

                    			newLeafNode(lv_teamWorkflowArtifactType_22_0, grammarAccess.getTeamDefAccess().getTeamWorkflowArtifactTypeSTRINGTerminalRuleCall_12_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getTeamDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"teamWorkflowArtifactType",
                            		lv_teamWorkflowArtifactType_22_0, 
                            		"STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1169:4: (otherlv_23= 'accessContextId' ( (lv_accessContextId_24_0= RULE_STRING ) ) )*
            loop34:
            do {
                int alt34=2;
                int LA34_0 = input.LA(1);

                if ( (LA34_0==34) ) {
                    alt34=1;
                }


                switch (alt34) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1169:6: otherlv_23= 'accessContextId' ( (lv_accessContextId_24_0= RULE_STRING ) )
            	    {
            	    otherlv_23=(Token)match(input,34,FOLLOW_34_in_ruleTeamDef2362); 

            	        	newLeafNode(otherlv_23, grammarAccess.getTeamDefAccess().getAccessContextIdKeyword_13_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1173:1: ( (lv_accessContextId_24_0= RULE_STRING ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1174:1: (lv_accessContextId_24_0= RULE_STRING )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1174:1: (lv_accessContextId_24_0= RULE_STRING )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1175:3: lv_accessContextId_24_0= RULE_STRING
            	    {
            	    lv_accessContextId_24_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleTeamDef2379); 

            	    			newLeafNode(lv_accessContextId_24_0, grammarAccess.getTeamDefAccess().getAccessContextIdSTRINGTerminalRuleCall_13_1_0()); 
            	    		

            	    	        if (current==null) {
            	    	            current = createModelElement(grammarAccess.getTeamDefRule());
            	    	        }
            	           		addWithLastConsumed(
            	           			current, 
            	           			"accessContextId",
            	            		lv_accessContextId_24_0, 
            	            		"STRING");
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop34;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1191:4: (otherlv_25= 'version' ( (lv_version_26_0= ruleVersionDef ) ) )*
            loop35:
            do {
                int alt35=2;
                int LA35_0 = input.LA(1);

                if ( (LA35_0==35) ) {
                    alt35=1;
                }


                switch (alt35) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1191:6: otherlv_25= 'version' ( (lv_version_26_0= ruleVersionDef ) )
            	    {
            	    otherlv_25=(Token)match(input,35,FOLLOW_35_in_ruleTeamDef2399); 

            	        	newLeafNode(otherlv_25, grammarAccess.getTeamDefAccess().getVersionKeyword_14_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1195:1: ( (lv_version_26_0= ruleVersionDef ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1196:1: (lv_version_26_0= ruleVersionDef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1196:1: (lv_version_26_0= ruleVersionDef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1197:3: lv_version_26_0= ruleVersionDef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getTeamDefAccess().getVersionVersionDefParserRuleCall_14_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleVersionDef_in_ruleTeamDef2420);
            	    lv_version_26_0=ruleVersionDef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getTeamDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"version",
            	            		lv_version_26_0, 
            	            		"VersionDef");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop35;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1213:4: (otherlv_27= 'children' otherlv_28= '{' (otherlv_29= 'teamDefinition' ( (lv_children_30_0= ruleTeamDef ) ) )+ otherlv_31= '}' )?
            int alt37=2;
            int LA37_0 = input.LA(1);

            if ( (LA37_0==36) ) {
                alt37=1;
            }
            switch (alt37) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1213:6: otherlv_27= 'children' otherlv_28= '{' (otherlv_29= 'teamDefinition' ( (lv_children_30_0= ruleTeamDef ) ) )+ otherlv_31= '}'
                    {
                    otherlv_27=(Token)match(input,36,FOLLOW_36_in_ruleTeamDef2435); 

                        	newLeafNode(otherlv_27, grammarAccess.getTeamDefAccess().getChildrenKeyword_15_0());
                        
                    otherlv_28=(Token)match(input,16,FOLLOW_16_in_ruleTeamDef2447); 

                        	newLeafNode(otherlv_28, grammarAccess.getTeamDefAccess().getLeftCurlyBracketKeyword_15_1());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1221:1: (otherlv_29= 'teamDefinition' ( (lv_children_30_0= ruleTeamDef ) ) )+
                    int cnt36=0;
                    loop36:
                    do {
                        int alt36=2;
                        int LA36_0 = input.LA(1);

                        if ( (LA36_0==12) ) {
                            alt36=1;
                        }


                        switch (alt36) {
                    	case 1 :
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1221:3: otherlv_29= 'teamDefinition' ( (lv_children_30_0= ruleTeamDef ) )
                    	    {
                    	    otherlv_29=(Token)match(input,12,FOLLOW_12_in_ruleTeamDef2460); 

                    	        	newLeafNode(otherlv_29, grammarAccess.getTeamDefAccess().getTeamDefinitionKeyword_15_2_0());
                    	        
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1225:1: ( (lv_children_30_0= ruleTeamDef ) )
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1226:1: (lv_children_30_0= ruleTeamDef )
                    	    {
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1226:1: (lv_children_30_0= ruleTeamDef )
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1227:3: lv_children_30_0= ruleTeamDef
                    	    {
                    	     
                    	    	        newCompositeNode(grammarAccess.getTeamDefAccess().getChildrenTeamDefParserRuleCall_15_2_1_0()); 
                    	    	    
                    	    pushFollow(FOLLOW_ruleTeamDef_in_ruleTeamDef2481);
                    	    lv_children_30_0=ruleTeamDef();

                    	    state._fsp--;


                    	    	        if (current==null) {
                    	    	            current = createModelElementForParent(grammarAccess.getTeamDefRule());
                    	    	        }
                    	           		add(
                    	           			current, 
                    	           			"children",
                    	            		lv_children_30_0, 
                    	            		"TeamDef");
                    	    	        afterParserOrEnumRuleCall();
                    	    	    

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt36 >= 1 ) break loop36;
                                EarlyExitException eee =
                                    new EarlyExitException(36, input);
                                throw eee;
                        }
                        cnt36++;
                    } while (true);

                    otherlv_31=(Token)match(input,21,FOLLOW_21_in_ruleTeamDef2495); 

                        	newLeafNode(otherlv_31, grammarAccess.getTeamDefAccess().getRightCurlyBracketKeyword_15_3());
                        

                    }
                    break;

            }

            otherlv_32=(Token)match(input,21,FOLLOW_21_in_ruleTeamDef2509); 

                	newLeafNode(otherlv_32, grammarAccess.getTeamDefAccess().getRightCurlyBracketKeyword_16());
                

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleTeamDef"


    // $ANTLR start "entryRuleAI_DEF_REFERENCE"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1259:1: entryRuleAI_DEF_REFERENCE returns [String current=null] : iv_ruleAI_DEF_REFERENCE= ruleAI_DEF_REFERENCE EOF ;
    public final String entryRuleAI_DEF_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleAI_DEF_REFERENCE = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1260:2: (iv_ruleAI_DEF_REFERENCE= ruleAI_DEF_REFERENCE EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1261:2: iv_ruleAI_DEF_REFERENCE= ruleAI_DEF_REFERENCE EOF
            {
             newCompositeNode(grammarAccess.getAI_DEF_REFERENCERule()); 
            pushFollow(FOLLOW_ruleAI_DEF_REFERENCE_in_entryRuleAI_DEF_REFERENCE2546);
            iv_ruleAI_DEF_REFERENCE=ruleAI_DEF_REFERENCE();

            state._fsp--;

             current =iv_ruleAI_DEF_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAI_DEF_REFERENCE2557); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleAI_DEF_REFERENCE"


    // $ANTLR start "ruleAI_DEF_REFERENCE"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1268:1: ruleAI_DEF_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleAI_DEF_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1271:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1272:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAI_DEF_REFERENCE2596); 

            		current.merge(this_STRING_0);
                
             
                newLeafNode(this_STRING_0, grammarAccess.getAI_DEF_REFERENCEAccess().getSTRINGTerminalRuleCall()); 
                

            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleAI_DEF_REFERENCE"


    // $ANTLR start "entryRuleActionableItemDef"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1287:1: entryRuleActionableItemDef returns [EObject current=null] : iv_ruleActionableItemDef= ruleActionableItemDef EOF ;
    public final EObject entryRuleActionableItemDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleActionableItemDef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1288:2: (iv_ruleActionableItemDef= ruleActionableItemDef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1289:2: iv_ruleActionableItemDef= ruleActionableItemDef EOF
            {
             newCompositeNode(grammarAccess.getActionableItemDefRule()); 
            pushFollow(FOLLOW_ruleActionableItemDef_in_entryRuleActionableItemDef2640);
            iv_ruleActionableItemDef=ruleActionableItemDef();

            state._fsp--;

             current =iv_ruleActionableItemDef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleActionableItemDef2650); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleActionableItemDef"


    // $ANTLR start "ruleActionableItemDef"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1296:1: ruleActionableItemDef returns [EObject current=null] : ( ( (lv_name_0_0= ruleAI_DEF_REFERENCE ) ) ( (lv_aiDefOption_1_0= ruleActionableItemOption ) )* (otherlv_2= '{' (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )? (otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_INT ) ) )? (otherlv_7= 'active' ( (lv_active_8_0= ruleBooleanDef ) ) )? (otherlv_9= 'actionable' ( (lv_actionable_10_0= ruleBooleanDef ) ) )? (otherlv_11= 'lead' ( (lv_lead_12_0= ruleUserRef ) ) )* (otherlv_13= 'owner' ( (lv_owner_14_0= ruleUserRef ) ) )* (otherlv_15= 'staticId' ( (lv_staticId_16_0= RULE_STRING ) ) )* (otherlv_17= 'team' ( (lv_teamDef_18_0= RULE_STRING ) ) )? (otherlv_19= 'accessContextId' ( (lv_accessContextId_20_0= RULE_STRING ) ) )* (otherlv_21= 'children' otherlv_22= '{' (otherlv_23= 'actionableItem' ( (lv_children_24_0= ruleActionableItemDef ) ) )+ otherlv_25= '}' )? otherlv_26= '}' )? ) ;
    public final EObject ruleActionableItemDef() throws RecognitionException {
        EObject current = null;

        Token otherlv_2=null;
        Token otherlv_3=null;
        Token lv_guid_4_0=null;
        Token otherlv_5=null;
        Token lv_uuid_6_0=null;
        Token otherlv_7=null;
        Token otherlv_9=null;
        Token otherlv_11=null;
        Token otherlv_13=null;
        Token otherlv_15=null;
        Token lv_staticId_16_0=null;
        Token otherlv_17=null;
        Token lv_teamDef_18_0=null;
        Token otherlv_19=null;
        Token lv_accessContextId_20_0=null;
        Token otherlv_21=null;
        Token otherlv_22=null;
        Token otherlv_23=null;
        Token otherlv_25=null;
        Token otherlv_26=null;
        AntlrDatatypeRuleToken lv_name_0_0 = null;

        AntlrDatatypeRuleToken lv_aiDefOption_1_0 = null;

        Enumerator lv_active_8_0 = null;

        Enumerator lv_actionable_10_0 = null;

        EObject lv_lead_12_0 = null;

        EObject lv_owner_14_0 = null;

        EObject lv_children_24_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1299:28: ( ( ( (lv_name_0_0= ruleAI_DEF_REFERENCE ) ) ( (lv_aiDefOption_1_0= ruleActionableItemOption ) )* (otherlv_2= '{' (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )? (otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_INT ) ) )? (otherlv_7= 'active' ( (lv_active_8_0= ruleBooleanDef ) ) )? (otherlv_9= 'actionable' ( (lv_actionable_10_0= ruleBooleanDef ) ) )? (otherlv_11= 'lead' ( (lv_lead_12_0= ruleUserRef ) ) )* (otherlv_13= 'owner' ( (lv_owner_14_0= ruleUserRef ) ) )* (otherlv_15= 'staticId' ( (lv_staticId_16_0= RULE_STRING ) ) )* (otherlv_17= 'team' ( (lv_teamDef_18_0= RULE_STRING ) ) )? (otherlv_19= 'accessContextId' ( (lv_accessContextId_20_0= RULE_STRING ) ) )* (otherlv_21= 'children' otherlv_22= '{' (otherlv_23= 'actionableItem' ( (lv_children_24_0= ruleActionableItemDef ) ) )+ otherlv_25= '}' )? otherlv_26= '}' )? ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1300:1: ( ( (lv_name_0_0= ruleAI_DEF_REFERENCE ) ) ( (lv_aiDefOption_1_0= ruleActionableItemOption ) )* (otherlv_2= '{' (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )? (otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_INT ) ) )? (otherlv_7= 'active' ( (lv_active_8_0= ruleBooleanDef ) ) )? (otherlv_9= 'actionable' ( (lv_actionable_10_0= ruleBooleanDef ) ) )? (otherlv_11= 'lead' ( (lv_lead_12_0= ruleUserRef ) ) )* (otherlv_13= 'owner' ( (lv_owner_14_0= ruleUserRef ) ) )* (otherlv_15= 'staticId' ( (lv_staticId_16_0= RULE_STRING ) ) )* (otherlv_17= 'team' ( (lv_teamDef_18_0= RULE_STRING ) ) )? (otherlv_19= 'accessContextId' ( (lv_accessContextId_20_0= RULE_STRING ) ) )* (otherlv_21= 'children' otherlv_22= '{' (otherlv_23= 'actionableItem' ( (lv_children_24_0= ruleActionableItemDef ) ) )+ otherlv_25= '}' )? otherlv_26= '}' )? )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1300:1: ( ( (lv_name_0_0= ruleAI_DEF_REFERENCE ) ) ( (lv_aiDefOption_1_0= ruleActionableItemOption ) )* (otherlv_2= '{' (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )? (otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_INT ) ) )? (otherlv_7= 'active' ( (lv_active_8_0= ruleBooleanDef ) ) )? (otherlv_9= 'actionable' ( (lv_actionable_10_0= ruleBooleanDef ) ) )? (otherlv_11= 'lead' ( (lv_lead_12_0= ruleUserRef ) ) )* (otherlv_13= 'owner' ( (lv_owner_14_0= ruleUserRef ) ) )* (otherlv_15= 'staticId' ( (lv_staticId_16_0= RULE_STRING ) ) )* (otherlv_17= 'team' ( (lv_teamDef_18_0= RULE_STRING ) ) )? (otherlv_19= 'accessContextId' ( (lv_accessContextId_20_0= RULE_STRING ) ) )* (otherlv_21= 'children' otherlv_22= '{' (otherlv_23= 'actionableItem' ( (lv_children_24_0= ruleActionableItemDef ) ) )+ otherlv_25= '}' )? otherlv_26= '}' )? )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1300:2: ( (lv_name_0_0= ruleAI_DEF_REFERENCE ) ) ( (lv_aiDefOption_1_0= ruleActionableItemOption ) )* (otherlv_2= '{' (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )? (otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_INT ) ) )? (otherlv_7= 'active' ( (lv_active_8_0= ruleBooleanDef ) ) )? (otherlv_9= 'actionable' ( (lv_actionable_10_0= ruleBooleanDef ) ) )? (otherlv_11= 'lead' ( (lv_lead_12_0= ruleUserRef ) ) )* (otherlv_13= 'owner' ( (lv_owner_14_0= ruleUserRef ) ) )* (otherlv_15= 'staticId' ( (lv_staticId_16_0= RULE_STRING ) ) )* (otherlv_17= 'team' ( (lv_teamDef_18_0= RULE_STRING ) ) )? (otherlv_19= 'accessContextId' ( (lv_accessContextId_20_0= RULE_STRING ) ) )* (otherlv_21= 'children' otherlv_22= '{' (otherlv_23= 'actionableItem' ( (lv_children_24_0= ruleActionableItemDef ) ) )+ otherlv_25= '}' )? otherlv_26= '}' )?
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1300:2: ( (lv_name_0_0= ruleAI_DEF_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1301:1: (lv_name_0_0= ruleAI_DEF_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1301:1: (lv_name_0_0= ruleAI_DEF_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1302:3: lv_name_0_0= ruleAI_DEF_REFERENCE
            {
             
            	        newCompositeNode(grammarAccess.getActionableItemDefAccess().getNameAI_DEF_REFERENCEParserRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_ruleAI_DEF_REFERENCE_in_ruleActionableItemDef2696);
            lv_name_0_0=ruleAI_DEF_REFERENCE();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getActionableItemDefRule());
            	        }
                   		set(
                   			current, 
                   			"name",
                    		lv_name_0_0, 
                    		"AI_DEF_REFERENCE");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1318:2: ( (lv_aiDefOption_1_0= ruleActionableItemOption ) )*
            loop38:
            do {
                int alt38=2;
                int LA38_0 = input.LA(1);

                if ( (LA38_0==RULE_STRING||LA38_0==85) ) {
                    alt38=1;
                }


                switch (alt38) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1319:1: (lv_aiDefOption_1_0= ruleActionableItemOption )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1319:1: (lv_aiDefOption_1_0= ruleActionableItemOption )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1320:3: lv_aiDefOption_1_0= ruleActionableItemOption
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getActionableItemDefAccess().getAiDefOptionActionableItemOptionParserRuleCall_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleActionableItemOption_in_ruleActionableItemDef2717);
            	    lv_aiDefOption_1_0=ruleActionableItemOption();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getActionableItemDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"aiDefOption",
            	            		lv_aiDefOption_1_0, 
            	            		"ActionableItemOption");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    break loop38;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1336:3: (otherlv_2= '{' (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )? (otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_INT ) ) )? (otherlv_7= 'active' ( (lv_active_8_0= ruleBooleanDef ) ) )? (otherlv_9= 'actionable' ( (lv_actionable_10_0= ruleBooleanDef ) ) )? (otherlv_11= 'lead' ( (lv_lead_12_0= ruleUserRef ) ) )* (otherlv_13= 'owner' ( (lv_owner_14_0= ruleUserRef ) ) )* (otherlv_15= 'staticId' ( (lv_staticId_16_0= RULE_STRING ) ) )* (otherlv_17= 'team' ( (lv_teamDef_18_0= RULE_STRING ) ) )? (otherlv_19= 'accessContextId' ( (lv_accessContextId_20_0= RULE_STRING ) ) )* (otherlv_21= 'children' otherlv_22= '{' (otherlv_23= 'actionableItem' ( (lv_children_24_0= ruleActionableItemDef ) ) )+ otherlv_25= '}' )? otherlv_26= '}' )?
            int alt50=2;
            int LA50_0 = input.LA(1);

            if ( (LA50_0==16) ) {
                alt50=1;
            }
            switch (alt50) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1336:5: otherlv_2= '{' (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )? (otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_INT ) ) )? (otherlv_7= 'active' ( (lv_active_8_0= ruleBooleanDef ) ) )? (otherlv_9= 'actionable' ( (lv_actionable_10_0= ruleBooleanDef ) ) )? (otherlv_11= 'lead' ( (lv_lead_12_0= ruleUserRef ) ) )* (otherlv_13= 'owner' ( (lv_owner_14_0= ruleUserRef ) ) )* (otherlv_15= 'staticId' ( (lv_staticId_16_0= RULE_STRING ) ) )* (otherlv_17= 'team' ( (lv_teamDef_18_0= RULE_STRING ) ) )? (otherlv_19= 'accessContextId' ( (lv_accessContextId_20_0= RULE_STRING ) ) )* (otherlv_21= 'children' otherlv_22= '{' (otherlv_23= 'actionableItem' ( (lv_children_24_0= ruleActionableItemDef ) ) )+ otherlv_25= '}' )? otherlv_26= '}'
                    {
                    otherlv_2=(Token)match(input,16,FOLLOW_16_in_ruleActionableItemDef2731); 

                        	newLeafNode(otherlv_2, grammarAccess.getActionableItemDefAccess().getLeftCurlyBracketKeyword_2_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1340:1: (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )?
                    int alt39=2;
                    int LA39_0 = input.LA(1);

                    if ( (LA39_0==24) ) {
                        alt39=1;
                    }
                    switch (alt39) {
                        case 1 :
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1340:3: otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) )
                            {
                            otherlv_3=(Token)match(input,24,FOLLOW_24_in_ruleActionableItemDef2744); 

                                	newLeafNode(otherlv_3, grammarAccess.getActionableItemDefAccess().getGuidKeyword_2_1_0());
                                
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1344:1: ( (lv_guid_4_0= RULE_STRING ) )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1345:1: (lv_guid_4_0= RULE_STRING )
                            {
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1345:1: (lv_guid_4_0= RULE_STRING )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1346:3: lv_guid_4_0= RULE_STRING
                            {
                            lv_guid_4_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleActionableItemDef2761); 

                            			newLeafNode(lv_guid_4_0, grammarAccess.getActionableItemDefAccess().getGuidSTRINGTerminalRuleCall_2_1_1_0()); 
                            		

                            	        if (current==null) {
                            	            current = createModelElement(grammarAccess.getActionableItemDefRule());
                            	        }
                                   		setWithLastConsumed(
                                   			current, 
                                   			"guid",
                                    		lv_guid_4_0, 
                                    		"STRING");
                            	    

                            }


                            }


                            }
                            break;

                    }

                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1362:4: (otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_INT ) ) )?
                    int alt40=2;
                    int LA40_0 = input.LA(1);

                    if ( (LA40_0==23) ) {
                        alt40=1;
                    }
                    switch (alt40) {
                        case 1 :
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1362:6: otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_INT ) )
                            {
                            otherlv_5=(Token)match(input,23,FOLLOW_23_in_ruleActionableItemDef2781); 

                                	newLeafNode(otherlv_5, grammarAccess.getActionableItemDefAccess().getUuidKeyword_2_2_0());
                                
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1366:1: ( (lv_uuid_6_0= RULE_INT ) )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1367:1: (lv_uuid_6_0= RULE_INT )
                            {
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1367:1: (lv_uuid_6_0= RULE_INT )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1368:3: lv_uuid_6_0= RULE_INT
                            {
                            lv_uuid_6_0=(Token)match(input,RULE_INT,FOLLOW_RULE_INT_in_ruleActionableItemDef2798); 

                            			newLeafNode(lv_uuid_6_0, grammarAccess.getActionableItemDefAccess().getUuidINTTerminalRuleCall_2_2_1_0()); 
                            		

                            	        if (current==null) {
                            	            current = createModelElement(grammarAccess.getActionableItemDefRule());
                            	        }
                                   		setWithLastConsumed(
                                   			current, 
                                   			"uuid",
                                    		lv_uuid_6_0, 
                                    		"INT");
                            	    

                            }


                            }


                            }
                            break;

                    }

                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1384:4: (otherlv_7= 'active' ( (lv_active_8_0= ruleBooleanDef ) ) )?
                    int alt41=2;
                    int LA41_0 = input.LA(1);

                    if ( (LA41_0==17) ) {
                        alt41=1;
                    }
                    switch (alt41) {
                        case 1 :
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1384:6: otherlv_7= 'active' ( (lv_active_8_0= ruleBooleanDef ) )
                            {
                            otherlv_7=(Token)match(input,17,FOLLOW_17_in_ruleActionableItemDef2818); 

                                	newLeafNode(otherlv_7, grammarAccess.getActionableItemDefAccess().getActiveKeyword_2_3_0());
                                
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1388:1: ( (lv_active_8_0= ruleBooleanDef ) )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1389:1: (lv_active_8_0= ruleBooleanDef )
                            {
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1389:1: (lv_active_8_0= ruleBooleanDef )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1390:3: lv_active_8_0= ruleBooleanDef
                            {
                             
                            	        newCompositeNode(grammarAccess.getActionableItemDefAccess().getActiveBooleanDefEnumRuleCall_2_3_1_0()); 
                            	    
                            pushFollow(FOLLOW_ruleBooleanDef_in_ruleActionableItemDef2839);
                            lv_active_8_0=ruleBooleanDef();

                            state._fsp--;


                            	        if (current==null) {
                            	            current = createModelElementForParent(grammarAccess.getActionableItemDefRule());
                            	        }
                                   		set(
                                   			current, 
                                   			"active",
                                    		lv_active_8_0, 
                                    		"BooleanDef");
                            	        afterParserOrEnumRuleCall();
                            	    

                            }


                            }


                            }
                            break;

                    }

                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1406:4: (otherlv_9= 'actionable' ( (lv_actionable_10_0= ruleBooleanDef ) ) )?
                    int alt42=2;
                    int LA42_0 = input.LA(1);

                    if ( (LA42_0==37) ) {
                        alt42=1;
                    }
                    switch (alt42) {
                        case 1 :
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1406:6: otherlv_9= 'actionable' ( (lv_actionable_10_0= ruleBooleanDef ) )
                            {
                            otherlv_9=(Token)match(input,37,FOLLOW_37_in_ruleActionableItemDef2854); 

                                	newLeafNode(otherlv_9, grammarAccess.getActionableItemDefAccess().getActionableKeyword_2_4_0());
                                
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1410:1: ( (lv_actionable_10_0= ruleBooleanDef ) )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1411:1: (lv_actionable_10_0= ruleBooleanDef )
                            {
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1411:1: (lv_actionable_10_0= ruleBooleanDef )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1412:3: lv_actionable_10_0= ruleBooleanDef
                            {
                             
                            	        newCompositeNode(grammarAccess.getActionableItemDefAccess().getActionableBooleanDefEnumRuleCall_2_4_1_0()); 
                            	    
                            pushFollow(FOLLOW_ruleBooleanDef_in_ruleActionableItemDef2875);
                            lv_actionable_10_0=ruleBooleanDef();

                            state._fsp--;


                            	        if (current==null) {
                            	            current = createModelElementForParent(grammarAccess.getActionableItemDefRule());
                            	        }
                                   		set(
                                   			current, 
                                   			"actionable",
                                    		lv_actionable_10_0, 
                                    		"BooleanDef");
                            	        afterParserOrEnumRuleCall();
                            	    

                            }


                            }


                            }
                            break;

                    }

                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1428:4: (otherlv_11= 'lead' ( (lv_lead_12_0= ruleUserRef ) ) )*
                    loop43:
                    do {
                        int alt43=2;
                        int LA43_0 = input.LA(1);

                        if ( (LA43_0==29) ) {
                            alt43=1;
                        }


                        switch (alt43) {
                    	case 1 :
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1428:6: otherlv_11= 'lead' ( (lv_lead_12_0= ruleUserRef ) )
                    	    {
                    	    otherlv_11=(Token)match(input,29,FOLLOW_29_in_ruleActionableItemDef2890); 

                    	        	newLeafNode(otherlv_11, grammarAccess.getActionableItemDefAccess().getLeadKeyword_2_5_0());
                    	        
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1432:1: ( (lv_lead_12_0= ruleUserRef ) )
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1433:1: (lv_lead_12_0= ruleUserRef )
                    	    {
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1433:1: (lv_lead_12_0= ruleUserRef )
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1434:3: lv_lead_12_0= ruleUserRef
                    	    {
                    	     
                    	    	        newCompositeNode(grammarAccess.getActionableItemDefAccess().getLeadUserRefParserRuleCall_2_5_1_0()); 
                    	    	    
                    	    pushFollow(FOLLOW_ruleUserRef_in_ruleActionableItemDef2911);
                    	    lv_lead_12_0=ruleUserRef();

                    	    state._fsp--;


                    	    	        if (current==null) {
                    	    	            current = createModelElementForParent(grammarAccess.getActionableItemDefRule());
                    	    	        }
                    	           		add(
                    	           			current, 
                    	           			"lead",
                    	            		lv_lead_12_0, 
                    	            		"UserRef");
                    	    	        afterParserOrEnumRuleCall();
                    	    	    

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop43;
                        }
                    } while (true);

                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1450:4: (otherlv_13= 'owner' ( (lv_owner_14_0= ruleUserRef ) ) )*
                    loop44:
                    do {
                        int alt44=2;
                        int LA44_0 = input.LA(1);

                        if ( (LA44_0==38) ) {
                            alt44=1;
                        }


                        switch (alt44) {
                    	case 1 :
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1450:6: otherlv_13= 'owner' ( (lv_owner_14_0= ruleUserRef ) )
                    	    {
                    	    otherlv_13=(Token)match(input,38,FOLLOW_38_in_ruleActionableItemDef2926); 

                    	        	newLeafNode(otherlv_13, grammarAccess.getActionableItemDefAccess().getOwnerKeyword_2_6_0());
                    	        
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1454:1: ( (lv_owner_14_0= ruleUserRef ) )
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1455:1: (lv_owner_14_0= ruleUserRef )
                    	    {
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1455:1: (lv_owner_14_0= ruleUserRef )
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1456:3: lv_owner_14_0= ruleUserRef
                    	    {
                    	     
                    	    	        newCompositeNode(grammarAccess.getActionableItemDefAccess().getOwnerUserRefParserRuleCall_2_6_1_0()); 
                    	    	    
                    	    pushFollow(FOLLOW_ruleUserRef_in_ruleActionableItemDef2947);
                    	    lv_owner_14_0=ruleUserRef();

                    	    state._fsp--;


                    	    	        if (current==null) {
                    	    	            current = createModelElementForParent(grammarAccess.getActionableItemDefRule());
                    	    	        }
                    	           		add(
                    	           			current, 
                    	           			"owner",
                    	            		lv_owner_14_0, 
                    	            		"UserRef");
                    	    	        afterParserOrEnumRuleCall();
                    	    	    

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop44;
                        }
                    } while (true);

                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1472:4: (otherlv_15= 'staticId' ( (lv_staticId_16_0= RULE_STRING ) ) )*
                    loop45:
                    do {
                        int alt45=2;
                        int LA45_0 = input.LA(1);

                        if ( (LA45_0==28) ) {
                            alt45=1;
                        }


                        switch (alt45) {
                    	case 1 :
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1472:6: otherlv_15= 'staticId' ( (lv_staticId_16_0= RULE_STRING ) )
                    	    {
                    	    otherlv_15=(Token)match(input,28,FOLLOW_28_in_ruleActionableItemDef2962); 

                    	        	newLeafNode(otherlv_15, grammarAccess.getActionableItemDefAccess().getStaticIdKeyword_2_7_0());
                    	        
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1476:1: ( (lv_staticId_16_0= RULE_STRING ) )
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1477:1: (lv_staticId_16_0= RULE_STRING )
                    	    {
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1477:1: (lv_staticId_16_0= RULE_STRING )
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1478:3: lv_staticId_16_0= RULE_STRING
                    	    {
                    	    lv_staticId_16_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleActionableItemDef2979); 

                    	    			newLeafNode(lv_staticId_16_0, grammarAccess.getActionableItemDefAccess().getStaticIdSTRINGTerminalRuleCall_2_7_1_0()); 
                    	    		

                    	    	        if (current==null) {
                    	    	            current = createModelElement(grammarAccess.getActionableItemDefRule());
                    	    	        }
                    	           		addWithLastConsumed(
                    	           			current, 
                    	           			"staticId",
                    	            		lv_staticId_16_0, 
                    	            		"STRING");
                    	    	    

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop45;
                        }
                    } while (true);

                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1494:4: (otherlv_17= 'team' ( (lv_teamDef_18_0= RULE_STRING ) ) )?
                    int alt46=2;
                    int LA46_0 = input.LA(1);

                    if ( (LA46_0==39) ) {
                        alt46=1;
                    }
                    switch (alt46) {
                        case 1 :
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1494:6: otherlv_17= 'team' ( (lv_teamDef_18_0= RULE_STRING ) )
                            {
                            otherlv_17=(Token)match(input,39,FOLLOW_39_in_ruleActionableItemDef2999); 

                                	newLeafNode(otherlv_17, grammarAccess.getActionableItemDefAccess().getTeamKeyword_2_8_0());
                                
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1498:1: ( (lv_teamDef_18_0= RULE_STRING ) )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1499:1: (lv_teamDef_18_0= RULE_STRING )
                            {
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1499:1: (lv_teamDef_18_0= RULE_STRING )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1500:3: lv_teamDef_18_0= RULE_STRING
                            {
                            lv_teamDef_18_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleActionableItemDef3016); 

                            			newLeafNode(lv_teamDef_18_0, grammarAccess.getActionableItemDefAccess().getTeamDefSTRINGTerminalRuleCall_2_8_1_0()); 
                            		

                            	        if (current==null) {
                            	            current = createModelElement(grammarAccess.getActionableItemDefRule());
                            	        }
                                   		setWithLastConsumed(
                                   			current, 
                                   			"teamDef",
                                    		lv_teamDef_18_0, 
                                    		"STRING");
                            	    

                            }


                            }


                            }
                            break;

                    }

                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1516:4: (otherlv_19= 'accessContextId' ( (lv_accessContextId_20_0= RULE_STRING ) ) )*
                    loop47:
                    do {
                        int alt47=2;
                        int LA47_0 = input.LA(1);

                        if ( (LA47_0==34) ) {
                            alt47=1;
                        }


                        switch (alt47) {
                    	case 1 :
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1516:6: otherlv_19= 'accessContextId' ( (lv_accessContextId_20_0= RULE_STRING ) )
                    	    {
                    	    otherlv_19=(Token)match(input,34,FOLLOW_34_in_ruleActionableItemDef3036); 

                    	        	newLeafNode(otherlv_19, grammarAccess.getActionableItemDefAccess().getAccessContextIdKeyword_2_9_0());
                    	        
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1520:1: ( (lv_accessContextId_20_0= RULE_STRING ) )
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1521:1: (lv_accessContextId_20_0= RULE_STRING )
                    	    {
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1521:1: (lv_accessContextId_20_0= RULE_STRING )
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1522:3: lv_accessContextId_20_0= RULE_STRING
                    	    {
                    	    lv_accessContextId_20_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleActionableItemDef3053); 

                    	    			newLeafNode(lv_accessContextId_20_0, grammarAccess.getActionableItemDefAccess().getAccessContextIdSTRINGTerminalRuleCall_2_9_1_0()); 
                    	    		

                    	    	        if (current==null) {
                    	    	            current = createModelElement(grammarAccess.getActionableItemDefRule());
                    	    	        }
                    	           		addWithLastConsumed(
                    	           			current, 
                    	           			"accessContextId",
                    	            		lv_accessContextId_20_0, 
                    	            		"STRING");
                    	    	    

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop47;
                        }
                    } while (true);

                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1538:4: (otherlv_21= 'children' otherlv_22= '{' (otherlv_23= 'actionableItem' ( (lv_children_24_0= ruleActionableItemDef ) ) )+ otherlv_25= '}' )?
                    int alt49=2;
                    int LA49_0 = input.LA(1);

                    if ( (LA49_0==36) ) {
                        alt49=1;
                    }
                    switch (alt49) {
                        case 1 :
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1538:6: otherlv_21= 'children' otherlv_22= '{' (otherlv_23= 'actionableItem' ( (lv_children_24_0= ruleActionableItemDef ) ) )+ otherlv_25= '}'
                            {
                            otherlv_21=(Token)match(input,36,FOLLOW_36_in_ruleActionableItemDef3073); 

                                	newLeafNode(otherlv_21, grammarAccess.getActionableItemDefAccess().getChildrenKeyword_2_10_0());
                                
                            otherlv_22=(Token)match(input,16,FOLLOW_16_in_ruleActionableItemDef3085); 

                                	newLeafNode(otherlv_22, grammarAccess.getActionableItemDefAccess().getLeftCurlyBracketKeyword_2_10_1());
                                
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1546:1: (otherlv_23= 'actionableItem' ( (lv_children_24_0= ruleActionableItemDef ) ) )+
                            int cnt48=0;
                            loop48:
                            do {
                                int alt48=2;
                                int LA48_0 = input.LA(1);

                                if ( (LA48_0==13) ) {
                                    alt48=1;
                                }


                                switch (alt48) {
                            	case 1 :
                            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1546:3: otherlv_23= 'actionableItem' ( (lv_children_24_0= ruleActionableItemDef ) )
                            	    {
                            	    otherlv_23=(Token)match(input,13,FOLLOW_13_in_ruleActionableItemDef3098); 

                            	        	newLeafNode(otherlv_23, grammarAccess.getActionableItemDefAccess().getActionableItemKeyword_2_10_2_0());
                            	        
                            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1550:1: ( (lv_children_24_0= ruleActionableItemDef ) )
                            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1551:1: (lv_children_24_0= ruleActionableItemDef )
                            	    {
                            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1551:1: (lv_children_24_0= ruleActionableItemDef )
                            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1552:3: lv_children_24_0= ruleActionableItemDef
                            	    {
                            	     
                            	    	        newCompositeNode(grammarAccess.getActionableItemDefAccess().getChildrenActionableItemDefParserRuleCall_2_10_2_1_0()); 
                            	    	    
                            	    pushFollow(FOLLOW_ruleActionableItemDef_in_ruleActionableItemDef3119);
                            	    lv_children_24_0=ruleActionableItemDef();

                            	    state._fsp--;


                            	    	        if (current==null) {
                            	    	            current = createModelElementForParent(grammarAccess.getActionableItemDefRule());
                            	    	        }
                            	           		add(
                            	           			current, 
                            	           			"children",
                            	            		lv_children_24_0, 
                            	            		"ActionableItemDef");
                            	    	        afterParserOrEnumRuleCall();
                            	    	    

                            	    }


                            	    }


                            	    }
                            	    break;

                            	default :
                            	    if ( cnt48 >= 1 ) break loop48;
                                        EarlyExitException eee =
                                            new EarlyExitException(48, input);
                                        throw eee;
                                }
                                cnt48++;
                            } while (true);

                            otherlv_25=(Token)match(input,21,FOLLOW_21_in_ruleActionableItemDef3133); 

                                	newLeafNode(otherlv_25, grammarAccess.getActionableItemDefAccess().getRightCurlyBracketKeyword_2_10_3());
                                

                            }
                            break;

                    }

                    otherlv_26=(Token)match(input,21,FOLLOW_21_in_ruleActionableItemDef3147); 

                        	newLeafNode(otherlv_26, grammarAccess.getActionableItemDefAccess().getRightCurlyBracketKeyword_2_11());
                        

                    }
                    break;

            }


            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleActionableItemDef"


    // $ANTLR start "entryRuleVERSION_DEF_REFERENCE"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1584:1: entryRuleVERSION_DEF_REFERENCE returns [String current=null] : iv_ruleVERSION_DEF_REFERENCE= ruleVERSION_DEF_REFERENCE EOF ;
    public final String entryRuleVERSION_DEF_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleVERSION_DEF_REFERENCE = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1585:2: (iv_ruleVERSION_DEF_REFERENCE= ruleVERSION_DEF_REFERENCE EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1586:2: iv_ruleVERSION_DEF_REFERENCE= ruleVERSION_DEF_REFERENCE EOF
            {
             newCompositeNode(grammarAccess.getVERSION_DEF_REFERENCERule()); 
            pushFollow(FOLLOW_ruleVERSION_DEF_REFERENCE_in_entryRuleVERSION_DEF_REFERENCE3186);
            iv_ruleVERSION_DEF_REFERENCE=ruleVERSION_DEF_REFERENCE();

            state._fsp--;

             current =iv_ruleVERSION_DEF_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleVERSION_DEF_REFERENCE3197); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleVERSION_DEF_REFERENCE"


    // $ANTLR start "ruleVERSION_DEF_REFERENCE"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1593:1: ruleVERSION_DEF_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleVERSION_DEF_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1596:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1597:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleVERSION_DEF_REFERENCE3236); 

            		current.merge(this_STRING_0);
                
             
                newLeafNode(this_STRING_0, grammarAccess.getVERSION_DEF_REFERENCEAccess().getSTRINGTerminalRuleCall()); 
                

            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleVERSION_DEF_REFERENCE"


    // $ANTLR start "entryRuleVersionDef"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1612:1: entryRuleVersionDef returns [EObject current=null] : iv_ruleVersionDef= ruleVersionDef EOF ;
    public final EObject entryRuleVersionDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleVersionDef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1613:2: (iv_ruleVersionDef= ruleVersionDef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1614:2: iv_ruleVersionDef= ruleVersionDef EOF
            {
             newCompositeNode(grammarAccess.getVersionDefRule()); 
            pushFollow(FOLLOW_ruleVersionDef_in_entryRuleVersionDef3280);
            iv_ruleVersionDef=ruleVersionDef();

            state._fsp--;

             current =iv_ruleVersionDef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleVersionDef3290); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleVersionDef"


    // $ANTLR start "ruleVersionDef"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1621:1: ruleVersionDef returns [EObject current=null] : ( ( (lv_name_0_0= ruleVERSION_DEF_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'guid' ( (lv_guid_3_0= RULE_STRING ) ) )? (otherlv_4= 'uuid' ( (lv_uuid_5_0= RULE_INT ) ) )? (otherlv_6= 'staticId' ( (lv_staticId_7_0= RULE_STRING ) ) )* (otherlv_8= 'next' ( (lv_next_9_0= ruleBooleanDef ) ) )? (otherlv_10= 'released' ( (lv_released_11_0= ruleBooleanDef ) ) )? (otherlv_12= 'allowCreateBranch' ( (lv_allowCreateBranch_13_0= ruleBooleanDef ) ) )? (otherlv_14= 'allowCommitBranch' ( (lv_allowCommitBranch_15_0= ruleBooleanDef ) ) )? (otherlv_16= 'baselineBranchUuid' ( (lv_baselineBranchUuid_17_0= RULE_STRING ) ) )? (otherlv_18= 'parallelVersion' ( (lv_parallelVersion_19_0= RULE_STRING ) ) )* otherlv_20= '}' ) ;
    public final EObject ruleVersionDef() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_2=null;
        Token lv_guid_3_0=null;
        Token otherlv_4=null;
        Token lv_uuid_5_0=null;
        Token otherlv_6=null;
        Token lv_staticId_7_0=null;
        Token otherlv_8=null;
        Token otherlv_10=null;
        Token otherlv_12=null;
        Token otherlv_14=null;
        Token otherlv_16=null;
        Token lv_baselineBranchUuid_17_0=null;
        Token otherlv_18=null;
        Token lv_parallelVersion_19_0=null;
        Token otherlv_20=null;
        AntlrDatatypeRuleToken lv_name_0_0 = null;

        Enumerator lv_next_9_0 = null;

        Enumerator lv_released_11_0 = null;

        Enumerator lv_allowCreateBranch_13_0 = null;

        Enumerator lv_allowCommitBranch_15_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1624:28: ( ( ( (lv_name_0_0= ruleVERSION_DEF_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'guid' ( (lv_guid_3_0= RULE_STRING ) ) )? (otherlv_4= 'uuid' ( (lv_uuid_5_0= RULE_INT ) ) )? (otherlv_6= 'staticId' ( (lv_staticId_7_0= RULE_STRING ) ) )* (otherlv_8= 'next' ( (lv_next_9_0= ruleBooleanDef ) ) )? (otherlv_10= 'released' ( (lv_released_11_0= ruleBooleanDef ) ) )? (otherlv_12= 'allowCreateBranch' ( (lv_allowCreateBranch_13_0= ruleBooleanDef ) ) )? (otherlv_14= 'allowCommitBranch' ( (lv_allowCommitBranch_15_0= ruleBooleanDef ) ) )? (otherlv_16= 'baselineBranchUuid' ( (lv_baselineBranchUuid_17_0= RULE_STRING ) ) )? (otherlv_18= 'parallelVersion' ( (lv_parallelVersion_19_0= RULE_STRING ) ) )* otherlv_20= '}' ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1625:1: ( ( (lv_name_0_0= ruleVERSION_DEF_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'guid' ( (lv_guid_3_0= RULE_STRING ) ) )? (otherlv_4= 'uuid' ( (lv_uuid_5_0= RULE_INT ) ) )? (otherlv_6= 'staticId' ( (lv_staticId_7_0= RULE_STRING ) ) )* (otherlv_8= 'next' ( (lv_next_9_0= ruleBooleanDef ) ) )? (otherlv_10= 'released' ( (lv_released_11_0= ruleBooleanDef ) ) )? (otherlv_12= 'allowCreateBranch' ( (lv_allowCreateBranch_13_0= ruleBooleanDef ) ) )? (otherlv_14= 'allowCommitBranch' ( (lv_allowCommitBranch_15_0= ruleBooleanDef ) ) )? (otherlv_16= 'baselineBranchUuid' ( (lv_baselineBranchUuid_17_0= RULE_STRING ) ) )? (otherlv_18= 'parallelVersion' ( (lv_parallelVersion_19_0= RULE_STRING ) ) )* otherlv_20= '}' )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1625:1: ( ( (lv_name_0_0= ruleVERSION_DEF_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'guid' ( (lv_guid_3_0= RULE_STRING ) ) )? (otherlv_4= 'uuid' ( (lv_uuid_5_0= RULE_INT ) ) )? (otherlv_6= 'staticId' ( (lv_staticId_7_0= RULE_STRING ) ) )* (otherlv_8= 'next' ( (lv_next_9_0= ruleBooleanDef ) ) )? (otherlv_10= 'released' ( (lv_released_11_0= ruleBooleanDef ) ) )? (otherlv_12= 'allowCreateBranch' ( (lv_allowCreateBranch_13_0= ruleBooleanDef ) ) )? (otherlv_14= 'allowCommitBranch' ( (lv_allowCommitBranch_15_0= ruleBooleanDef ) ) )? (otherlv_16= 'baselineBranchUuid' ( (lv_baselineBranchUuid_17_0= RULE_STRING ) ) )? (otherlv_18= 'parallelVersion' ( (lv_parallelVersion_19_0= RULE_STRING ) ) )* otherlv_20= '}' )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1625:2: ( (lv_name_0_0= ruleVERSION_DEF_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'guid' ( (lv_guid_3_0= RULE_STRING ) ) )? (otherlv_4= 'uuid' ( (lv_uuid_5_0= RULE_INT ) ) )? (otherlv_6= 'staticId' ( (lv_staticId_7_0= RULE_STRING ) ) )* (otherlv_8= 'next' ( (lv_next_9_0= ruleBooleanDef ) ) )? (otherlv_10= 'released' ( (lv_released_11_0= ruleBooleanDef ) ) )? (otherlv_12= 'allowCreateBranch' ( (lv_allowCreateBranch_13_0= ruleBooleanDef ) ) )? (otherlv_14= 'allowCommitBranch' ( (lv_allowCommitBranch_15_0= ruleBooleanDef ) ) )? (otherlv_16= 'baselineBranchUuid' ( (lv_baselineBranchUuid_17_0= RULE_STRING ) ) )? (otherlv_18= 'parallelVersion' ( (lv_parallelVersion_19_0= RULE_STRING ) ) )* otherlv_20= '}'
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1625:2: ( (lv_name_0_0= ruleVERSION_DEF_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1626:1: (lv_name_0_0= ruleVERSION_DEF_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1626:1: (lv_name_0_0= ruleVERSION_DEF_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1627:3: lv_name_0_0= ruleVERSION_DEF_REFERENCE
            {
             
            	        newCompositeNode(grammarAccess.getVersionDefAccess().getNameVERSION_DEF_REFERENCEParserRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_ruleVERSION_DEF_REFERENCE_in_ruleVersionDef3336);
            lv_name_0_0=ruleVERSION_DEF_REFERENCE();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getVersionDefRule());
            	        }
                   		set(
                   			current, 
                   			"name",
                    		lv_name_0_0, 
                    		"VERSION_DEF_REFERENCE");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            otherlv_1=(Token)match(input,16,FOLLOW_16_in_ruleVersionDef3348); 

                	newLeafNode(otherlv_1, grammarAccess.getVersionDefAccess().getLeftCurlyBracketKeyword_1());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1647:1: (otherlv_2= 'guid' ( (lv_guid_3_0= RULE_STRING ) ) )?
            int alt51=2;
            int LA51_0 = input.LA(1);

            if ( (LA51_0==24) ) {
                alt51=1;
            }
            switch (alt51) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1647:3: otherlv_2= 'guid' ( (lv_guid_3_0= RULE_STRING ) )
                    {
                    otherlv_2=(Token)match(input,24,FOLLOW_24_in_ruleVersionDef3361); 

                        	newLeafNode(otherlv_2, grammarAccess.getVersionDefAccess().getGuidKeyword_2_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1651:1: ( (lv_guid_3_0= RULE_STRING ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1652:1: (lv_guid_3_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1652:1: (lv_guid_3_0= RULE_STRING )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1653:3: lv_guid_3_0= RULE_STRING
                    {
                    lv_guid_3_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleVersionDef3378); 

                    			newLeafNode(lv_guid_3_0, grammarAccess.getVersionDefAccess().getGuidSTRINGTerminalRuleCall_2_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getVersionDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"guid",
                            		lv_guid_3_0, 
                            		"STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1669:4: (otherlv_4= 'uuid' ( (lv_uuid_5_0= RULE_INT ) ) )?
            int alt52=2;
            int LA52_0 = input.LA(1);

            if ( (LA52_0==23) ) {
                alt52=1;
            }
            switch (alt52) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1669:6: otherlv_4= 'uuid' ( (lv_uuid_5_0= RULE_INT ) )
                    {
                    otherlv_4=(Token)match(input,23,FOLLOW_23_in_ruleVersionDef3398); 

                        	newLeafNode(otherlv_4, grammarAccess.getVersionDefAccess().getUuidKeyword_3_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1673:1: ( (lv_uuid_5_0= RULE_INT ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1674:1: (lv_uuid_5_0= RULE_INT )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1674:1: (lv_uuid_5_0= RULE_INT )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1675:3: lv_uuid_5_0= RULE_INT
                    {
                    lv_uuid_5_0=(Token)match(input,RULE_INT,FOLLOW_RULE_INT_in_ruleVersionDef3415); 

                    			newLeafNode(lv_uuid_5_0, grammarAccess.getVersionDefAccess().getUuidINTTerminalRuleCall_3_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getVersionDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"uuid",
                            		lv_uuid_5_0, 
                            		"INT");
                    	    

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1691:4: (otherlv_6= 'staticId' ( (lv_staticId_7_0= RULE_STRING ) ) )*
            loop53:
            do {
                int alt53=2;
                int LA53_0 = input.LA(1);

                if ( (LA53_0==28) ) {
                    alt53=1;
                }


                switch (alt53) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1691:6: otherlv_6= 'staticId' ( (lv_staticId_7_0= RULE_STRING ) )
            	    {
            	    otherlv_6=(Token)match(input,28,FOLLOW_28_in_ruleVersionDef3435); 

            	        	newLeafNode(otherlv_6, grammarAccess.getVersionDefAccess().getStaticIdKeyword_4_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1695:1: ( (lv_staticId_7_0= RULE_STRING ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1696:1: (lv_staticId_7_0= RULE_STRING )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1696:1: (lv_staticId_7_0= RULE_STRING )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1697:3: lv_staticId_7_0= RULE_STRING
            	    {
            	    lv_staticId_7_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleVersionDef3452); 

            	    			newLeafNode(lv_staticId_7_0, grammarAccess.getVersionDefAccess().getStaticIdSTRINGTerminalRuleCall_4_1_0()); 
            	    		

            	    	        if (current==null) {
            	    	            current = createModelElement(grammarAccess.getVersionDefRule());
            	    	        }
            	           		addWithLastConsumed(
            	           			current, 
            	           			"staticId",
            	            		lv_staticId_7_0, 
            	            		"STRING");
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop53;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1713:4: (otherlv_8= 'next' ( (lv_next_9_0= ruleBooleanDef ) ) )?
            int alt54=2;
            int LA54_0 = input.LA(1);

            if ( (LA54_0==40) ) {
                alt54=1;
            }
            switch (alt54) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1713:6: otherlv_8= 'next' ( (lv_next_9_0= ruleBooleanDef ) )
                    {
                    otherlv_8=(Token)match(input,40,FOLLOW_40_in_ruleVersionDef3472); 

                        	newLeafNode(otherlv_8, grammarAccess.getVersionDefAccess().getNextKeyword_5_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1717:1: ( (lv_next_9_0= ruleBooleanDef ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1718:1: (lv_next_9_0= ruleBooleanDef )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1718:1: (lv_next_9_0= ruleBooleanDef )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1719:3: lv_next_9_0= ruleBooleanDef
                    {
                     
                    	        newCompositeNode(grammarAccess.getVersionDefAccess().getNextBooleanDefEnumRuleCall_5_1_0()); 
                    	    
                    pushFollow(FOLLOW_ruleBooleanDef_in_ruleVersionDef3493);
                    lv_next_9_0=ruleBooleanDef();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getVersionDefRule());
                    	        }
                           		set(
                           			current, 
                           			"next",
                            		lv_next_9_0, 
                            		"BooleanDef");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1735:4: (otherlv_10= 'released' ( (lv_released_11_0= ruleBooleanDef ) ) )?
            int alt55=2;
            int LA55_0 = input.LA(1);

            if ( (LA55_0==41) ) {
                alt55=1;
            }
            switch (alt55) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1735:6: otherlv_10= 'released' ( (lv_released_11_0= ruleBooleanDef ) )
                    {
                    otherlv_10=(Token)match(input,41,FOLLOW_41_in_ruleVersionDef3508); 

                        	newLeafNode(otherlv_10, grammarAccess.getVersionDefAccess().getReleasedKeyword_6_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1739:1: ( (lv_released_11_0= ruleBooleanDef ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1740:1: (lv_released_11_0= ruleBooleanDef )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1740:1: (lv_released_11_0= ruleBooleanDef )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1741:3: lv_released_11_0= ruleBooleanDef
                    {
                     
                    	        newCompositeNode(grammarAccess.getVersionDefAccess().getReleasedBooleanDefEnumRuleCall_6_1_0()); 
                    	    
                    pushFollow(FOLLOW_ruleBooleanDef_in_ruleVersionDef3529);
                    lv_released_11_0=ruleBooleanDef();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getVersionDefRule());
                    	        }
                           		set(
                           			current, 
                           			"released",
                            		lv_released_11_0, 
                            		"BooleanDef");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1757:4: (otherlv_12= 'allowCreateBranch' ( (lv_allowCreateBranch_13_0= ruleBooleanDef ) ) )?
            int alt56=2;
            int LA56_0 = input.LA(1);

            if ( (LA56_0==42) ) {
                alt56=1;
            }
            switch (alt56) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1757:6: otherlv_12= 'allowCreateBranch' ( (lv_allowCreateBranch_13_0= ruleBooleanDef ) )
                    {
                    otherlv_12=(Token)match(input,42,FOLLOW_42_in_ruleVersionDef3544); 

                        	newLeafNode(otherlv_12, grammarAccess.getVersionDefAccess().getAllowCreateBranchKeyword_7_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1761:1: ( (lv_allowCreateBranch_13_0= ruleBooleanDef ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1762:1: (lv_allowCreateBranch_13_0= ruleBooleanDef )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1762:1: (lv_allowCreateBranch_13_0= ruleBooleanDef )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1763:3: lv_allowCreateBranch_13_0= ruleBooleanDef
                    {
                     
                    	        newCompositeNode(grammarAccess.getVersionDefAccess().getAllowCreateBranchBooleanDefEnumRuleCall_7_1_0()); 
                    	    
                    pushFollow(FOLLOW_ruleBooleanDef_in_ruleVersionDef3565);
                    lv_allowCreateBranch_13_0=ruleBooleanDef();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getVersionDefRule());
                    	        }
                           		set(
                           			current, 
                           			"allowCreateBranch",
                            		lv_allowCreateBranch_13_0, 
                            		"BooleanDef");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1779:4: (otherlv_14= 'allowCommitBranch' ( (lv_allowCommitBranch_15_0= ruleBooleanDef ) ) )?
            int alt57=2;
            int LA57_0 = input.LA(1);

            if ( (LA57_0==43) ) {
                alt57=1;
            }
            switch (alt57) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1779:6: otherlv_14= 'allowCommitBranch' ( (lv_allowCommitBranch_15_0= ruleBooleanDef ) )
                    {
                    otherlv_14=(Token)match(input,43,FOLLOW_43_in_ruleVersionDef3580); 

                        	newLeafNode(otherlv_14, grammarAccess.getVersionDefAccess().getAllowCommitBranchKeyword_8_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1783:1: ( (lv_allowCommitBranch_15_0= ruleBooleanDef ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1784:1: (lv_allowCommitBranch_15_0= ruleBooleanDef )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1784:1: (lv_allowCommitBranch_15_0= ruleBooleanDef )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1785:3: lv_allowCommitBranch_15_0= ruleBooleanDef
                    {
                     
                    	        newCompositeNode(grammarAccess.getVersionDefAccess().getAllowCommitBranchBooleanDefEnumRuleCall_8_1_0()); 
                    	    
                    pushFollow(FOLLOW_ruleBooleanDef_in_ruleVersionDef3601);
                    lv_allowCommitBranch_15_0=ruleBooleanDef();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getVersionDefRule());
                    	        }
                           		set(
                           			current, 
                           			"allowCommitBranch",
                            		lv_allowCommitBranch_15_0, 
                            		"BooleanDef");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1801:4: (otherlv_16= 'baselineBranchUuid' ( (lv_baselineBranchUuid_17_0= RULE_STRING ) ) )?
            int alt58=2;
            int LA58_0 = input.LA(1);

            if ( (LA58_0==44) ) {
                alt58=1;
            }
            switch (alt58) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1801:6: otherlv_16= 'baselineBranchUuid' ( (lv_baselineBranchUuid_17_0= RULE_STRING ) )
                    {
                    otherlv_16=(Token)match(input,44,FOLLOW_44_in_ruleVersionDef3616); 

                        	newLeafNode(otherlv_16, grammarAccess.getVersionDefAccess().getBaselineBranchUuidKeyword_9_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1805:1: ( (lv_baselineBranchUuid_17_0= RULE_STRING ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1806:1: (lv_baselineBranchUuid_17_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1806:1: (lv_baselineBranchUuid_17_0= RULE_STRING )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1807:3: lv_baselineBranchUuid_17_0= RULE_STRING
                    {
                    lv_baselineBranchUuid_17_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleVersionDef3633); 

                    			newLeafNode(lv_baselineBranchUuid_17_0, grammarAccess.getVersionDefAccess().getBaselineBranchUuidSTRINGTerminalRuleCall_9_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getVersionDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"baselineBranchUuid",
                            		lv_baselineBranchUuid_17_0, 
                            		"STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1823:4: (otherlv_18= 'parallelVersion' ( (lv_parallelVersion_19_0= RULE_STRING ) ) )*
            loop59:
            do {
                int alt59=2;
                int LA59_0 = input.LA(1);

                if ( (LA59_0==45) ) {
                    alt59=1;
                }


                switch (alt59) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1823:6: otherlv_18= 'parallelVersion' ( (lv_parallelVersion_19_0= RULE_STRING ) )
            	    {
            	    otherlv_18=(Token)match(input,45,FOLLOW_45_in_ruleVersionDef3653); 

            	        	newLeafNode(otherlv_18, grammarAccess.getVersionDefAccess().getParallelVersionKeyword_10_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1827:1: ( (lv_parallelVersion_19_0= RULE_STRING ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1828:1: (lv_parallelVersion_19_0= RULE_STRING )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1828:1: (lv_parallelVersion_19_0= RULE_STRING )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1829:3: lv_parallelVersion_19_0= RULE_STRING
            	    {
            	    lv_parallelVersion_19_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleVersionDef3670); 

            	    			newLeafNode(lv_parallelVersion_19_0, grammarAccess.getVersionDefAccess().getParallelVersionSTRINGTerminalRuleCall_10_1_0()); 
            	    		

            	    	        if (current==null) {
            	    	            current = createModelElement(grammarAccess.getVersionDefRule());
            	    	        }
            	           		addWithLastConsumed(
            	           			current, 
            	           			"parallelVersion",
            	            		lv_parallelVersion_19_0, 
            	            		"STRING");
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop59;
                }
            } while (true);

            otherlv_20=(Token)match(input,21,FOLLOW_21_in_ruleVersionDef3689); 

                	newLeafNode(otherlv_20, grammarAccess.getVersionDefAccess().getRightCurlyBracketKeyword_11());
                

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleVersionDef"


    // $ANTLR start "entryRuleWorkDef"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1857:1: entryRuleWorkDef returns [EObject current=null] : iv_ruleWorkDef= ruleWorkDef EOF ;
    public final EObject entryRuleWorkDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleWorkDef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1858:2: (iv_ruleWorkDef= ruleWorkDef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1859:2: iv_ruleWorkDef= ruleWorkDef EOF
            {
             newCompositeNode(grammarAccess.getWorkDefRule()); 
            pushFollow(FOLLOW_ruleWorkDef_in_entryRuleWorkDef3725);
            iv_ruleWorkDef=ruleWorkDef();

            state._fsp--;

             current =iv_ruleWorkDef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleWorkDef3735); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleWorkDef"


    // $ANTLR start "ruleWorkDef"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1866:1: ruleWorkDef returns [EObject current=null] : ( ( (lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'id' ( (lv_id_3_0= RULE_STRING ) ) )+ (otherlv_4= 'startState' ( ( ruleSTATE_NAME_REFERENCE ) ) ) ( (lv_widgetDefs_6_0= ruleWidgetDef ) )* ( (lv_decisionReviewDefs_7_0= ruleDecisionReviewDef ) )* ( (lv_peerReviewDefs_8_0= rulePeerReviewDef ) )* ( (lv_states_9_0= ruleStateDef ) )+ otherlv_10= '}' ) ;
    public final EObject ruleWorkDef() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_2=null;
        Token lv_id_3_0=null;
        Token otherlv_4=null;
        Token otherlv_10=null;
        AntlrDatatypeRuleToken lv_name_0_0 = null;

        EObject lv_widgetDefs_6_0 = null;

        EObject lv_decisionReviewDefs_7_0 = null;

        EObject lv_peerReviewDefs_8_0 = null;

        EObject lv_states_9_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1869:28: ( ( ( (lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'id' ( (lv_id_3_0= RULE_STRING ) ) )+ (otherlv_4= 'startState' ( ( ruleSTATE_NAME_REFERENCE ) ) ) ( (lv_widgetDefs_6_0= ruleWidgetDef ) )* ( (lv_decisionReviewDefs_7_0= ruleDecisionReviewDef ) )* ( (lv_peerReviewDefs_8_0= rulePeerReviewDef ) )* ( (lv_states_9_0= ruleStateDef ) )+ otherlv_10= '}' ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1870:1: ( ( (lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'id' ( (lv_id_3_0= RULE_STRING ) ) )+ (otherlv_4= 'startState' ( ( ruleSTATE_NAME_REFERENCE ) ) ) ( (lv_widgetDefs_6_0= ruleWidgetDef ) )* ( (lv_decisionReviewDefs_7_0= ruleDecisionReviewDef ) )* ( (lv_peerReviewDefs_8_0= rulePeerReviewDef ) )* ( (lv_states_9_0= ruleStateDef ) )+ otherlv_10= '}' )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1870:1: ( ( (lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'id' ( (lv_id_3_0= RULE_STRING ) ) )+ (otherlv_4= 'startState' ( ( ruleSTATE_NAME_REFERENCE ) ) ) ( (lv_widgetDefs_6_0= ruleWidgetDef ) )* ( (lv_decisionReviewDefs_7_0= ruleDecisionReviewDef ) )* ( (lv_peerReviewDefs_8_0= rulePeerReviewDef ) )* ( (lv_states_9_0= ruleStateDef ) )+ otherlv_10= '}' )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1870:2: ( (lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'id' ( (lv_id_3_0= RULE_STRING ) ) )+ (otherlv_4= 'startState' ( ( ruleSTATE_NAME_REFERENCE ) ) ) ( (lv_widgetDefs_6_0= ruleWidgetDef ) )* ( (lv_decisionReviewDefs_7_0= ruleDecisionReviewDef ) )* ( (lv_peerReviewDefs_8_0= rulePeerReviewDef ) )* ( (lv_states_9_0= ruleStateDef ) )+ otherlv_10= '}'
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1870:2: ( (lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1871:1: (lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1871:1: (lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1872:3: lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE
            {
             
            	        newCompositeNode(grammarAccess.getWorkDefAccess().getNameWORK_DEFINITION_NAME_REFERENCEParserRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_ruleWORK_DEFINITION_NAME_REFERENCE_in_ruleWorkDef3781);
            lv_name_0_0=ruleWORK_DEFINITION_NAME_REFERENCE();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getWorkDefRule());
            	        }
                   		set(
                   			current, 
                   			"name",
                    		lv_name_0_0, 
                    		"WORK_DEFINITION_NAME_REFERENCE");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            otherlv_1=(Token)match(input,16,FOLLOW_16_in_ruleWorkDef3793); 

                	newLeafNode(otherlv_1, grammarAccess.getWorkDefAccess().getLeftCurlyBracketKeyword_1());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1892:1: (otherlv_2= 'id' ( (lv_id_3_0= RULE_STRING ) ) )+
            int cnt60=0;
            loop60:
            do {
                int alt60=2;
                int LA60_0 = input.LA(1);

                if ( (LA60_0==46) ) {
                    alt60=1;
                }


                switch (alt60) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1892:3: otherlv_2= 'id' ( (lv_id_3_0= RULE_STRING ) )
            	    {
            	    otherlv_2=(Token)match(input,46,FOLLOW_46_in_ruleWorkDef3806); 

            	        	newLeafNode(otherlv_2, grammarAccess.getWorkDefAccess().getIdKeyword_2_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1896:1: ( (lv_id_3_0= RULE_STRING ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1897:1: (lv_id_3_0= RULE_STRING )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1897:1: (lv_id_3_0= RULE_STRING )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1898:3: lv_id_3_0= RULE_STRING
            	    {
            	    lv_id_3_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleWorkDef3823); 

            	    			newLeafNode(lv_id_3_0, grammarAccess.getWorkDefAccess().getIdSTRINGTerminalRuleCall_2_1_0()); 
            	    		

            	    	        if (current==null) {
            	    	            current = createModelElement(grammarAccess.getWorkDefRule());
            	    	        }
            	           		addWithLastConsumed(
            	           			current, 
            	           			"id",
            	            		lv_id_3_0, 
            	            		"STRING");
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt60 >= 1 ) break loop60;
                        EarlyExitException eee =
                            new EarlyExitException(60, input);
                        throw eee;
                }
                cnt60++;
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1914:4: (otherlv_4= 'startState' ( ( ruleSTATE_NAME_REFERENCE ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1914:6: otherlv_4= 'startState' ( ( ruleSTATE_NAME_REFERENCE ) )
            {
            otherlv_4=(Token)match(input,47,FOLLOW_47_in_ruleWorkDef3843); 

                	newLeafNode(otherlv_4, grammarAccess.getWorkDefAccess().getStartStateKeyword_3_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1918:1: ( ( ruleSTATE_NAME_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1919:1: ( ruleSTATE_NAME_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1919:1: ( ruleSTATE_NAME_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1920:3: ruleSTATE_NAME_REFERENCE
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getWorkDefRule());
            	        }
                    
             
            	        newCompositeNode(grammarAccess.getWorkDefAccess().getStartStateStateDefCrossReference_3_1_0()); 
            	    
            pushFollow(FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleWorkDef3866);
            ruleSTATE_NAME_REFERENCE();

            state._fsp--;

             
            	        afterParserOrEnumRuleCall();
            	    

            }


            }


            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1933:3: ( (lv_widgetDefs_6_0= ruleWidgetDef ) )*
            loop61:
            do {
                int alt61=2;
                int LA61_0 = input.LA(1);

                if ( (LA61_0==48) ) {
                    alt61=1;
                }


                switch (alt61) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1934:1: (lv_widgetDefs_6_0= ruleWidgetDef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1934:1: (lv_widgetDefs_6_0= ruleWidgetDef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1935:3: lv_widgetDefs_6_0= ruleWidgetDef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getWorkDefAccess().getWidgetDefsWidgetDefParserRuleCall_4_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleWidgetDef_in_ruleWorkDef3888);
            	    lv_widgetDefs_6_0=ruleWidgetDef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getWorkDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"widgetDefs",
            	            		lv_widgetDefs_6_0, 
            	            		"WidgetDef");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    break loop61;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1951:3: ( (lv_decisionReviewDefs_7_0= ruleDecisionReviewDef ) )*
            loop62:
            do {
                int alt62=2;
                int LA62_0 = input.LA(1);

                if ( (LA62_0==68) ) {
                    alt62=1;
                }


                switch (alt62) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1952:1: (lv_decisionReviewDefs_7_0= ruleDecisionReviewDef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1952:1: (lv_decisionReviewDefs_7_0= ruleDecisionReviewDef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1953:3: lv_decisionReviewDefs_7_0= ruleDecisionReviewDef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getWorkDefAccess().getDecisionReviewDefsDecisionReviewDefParserRuleCall_5_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleDecisionReviewDef_in_ruleWorkDef3910);
            	    lv_decisionReviewDefs_7_0=ruleDecisionReviewDef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getWorkDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"decisionReviewDefs",
            	            		lv_decisionReviewDefs_7_0, 
            	            		"DecisionReviewDef");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    break loop62;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1969:3: ( (lv_peerReviewDefs_8_0= rulePeerReviewDef ) )*
            loop63:
            do {
                int alt63=2;
                int LA63_0 = input.LA(1);

                if ( (LA63_0==76) ) {
                    alt63=1;
                }


                switch (alt63) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1970:1: (lv_peerReviewDefs_8_0= rulePeerReviewDef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1970:1: (lv_peerReviewDefs_8_0= rulePeerReviewDef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1971:3: lv_peerReviewDefs_8_0= rulePeerReviewDef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getWorkDefAccess().getPeerReviewDefsPeerReviewDefParserRuleCall_6_0()); 
            	    	    
            	    pushFollow(FOLLOW_rulePeerReviewDef_in_ruleWorkDef3932);
            	    lv_peerReviewDefs_8_0=rulePeerReviewDef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getWorkDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"peerReviewDefs",
            	            		lv_peerReviewDefs_8_0, 
            	            		"PeerReviewDef");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    break loop63;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1987:3: ( (lv_states_9_0= ruleStateDef ) )+
            int cnt64=0;
            loop64:
            do {
                int alt64=2;
                int LA64_0 = input.LA(1);

                if ( (LA64_0==60) ) {
                    alt64=1;
                }


                switch (alt64) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1988:1: (lv_states_9_0= ruleStateDef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1988:1: (lv_states_9_0= ruleStateDef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1989:3: lv_states_9_0= ruleStateDef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getWorkDefAccess().getStatesStateDefParserRuleCall_7_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleStateDef_in_ruleWorkDef3954);
            	    lv_states_9_0=ruleStateDef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getWorkDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"states",
            	            		lv_states_9_0, 
            	            		"StateDef");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt64 >= 1 ) break loop64;
                        EarlyExitException eee =
                            new EarlyExitException(64, input);
                        throw eee;
                }
                cnt64++;
            } while (true);

            otherlv_10=(Token)match(input,21,FOLLOW_21_in_ruleWorkDef3967); 

                	newLeafNode(otherlv_10, grammarAccess.getWorkDefAccess().getRightCurlyBracketKeyword_8());
                

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleWorkDef"


    // $ANTLR start "entryRuleWidgetDef"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2017:1: entryRuleWidgetDef returns [EObject current=null] : iv_ruleWidgetDef= ruleWidgetDef EOF ;
    public final EObject entryRuleWidgetDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleWidgetDef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2018:2: (iv_ruleWidgetDef= ruleWidgetDef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2019:2: iv_ruleWidgetDef= ruleWidgetDef EOF
            {
             newCompositeNode(grammarAccess.getWidgetDefRule()); 
            pushFollow(FOLLOW_ruleWidgetDef_in_entryRuleWidgetDef4003);
            iv_ruleWidgetDef=ruleWidgetDef();

            state._fsp--;

             current =iv_ruleWidgetDef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleWidgetDef4013); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleWidgetDef"


    // $ANTLR start "ruleWidgetDef"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2026:1: ruleWidgetDef returns [EObject current=null] : (otherlv_0= 'widgetDefinition' ( (lv_name_1_0= ruleWIDGET_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'attributeName' ( (lv_attributeName_4_0= RULE_STRING ) ) )? (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'xWidgetName' ( (lv_xWidgetName_8_0= RULE_STRING ) ) )? (otherlv_9= 'defaultValue' ( (lv_defaultValue_10_0= RULE_STRING ) ) )? (otherlv_11= 'height' ( (lv_height_12_0= RULE_INT ) ) )? (otherlv_13= 'option' ( (lv_option_14_0= ruleWidgetOption ) ) )* (otherlv_15= 'minConstraint' ( (lv_minConstraint_16_0= RULE_STRING ) ) )? (otherlv_17= 'maxConstraint' ( (lv_maxConstraint_18_0= RULE_STRING ) ) )? otherlv_19= '}' ) ;
    public final EObject ruleWidgetDef() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_2=null;
        Token otherlv_3=null;
        Token lv_attributeName_4_0=null;
        Token otherlv_5=null;
        Token lv_description_6_0=null;
        Token otherlv_7=null;
        Token lv_xWidgetName_8_0=null;
        Token otherlv_9=null;
        Token lv_defaultValue_10_0=null;
        Token otherlv_11=null;
        Token lv_height_12_0=null;
        Token otherlv_13=null;
        Token otherlv_15=null;
        Token lv_minConstraint_16_0=null;
        Token otherlv_17=null;
        Token lv_maxConstraint_18_0=null;
        Token otherlv_19=null;
        AntlrDatatypeRuleToken lv_name_1_0 = null;

        AntlrDatatypeRuleToken lv_option_14_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2029:28: ( (otherlv_0= 'widgetDefinition' ( (lv_name_1_0= ruleWIDGET_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'attributeName' ( (lv_attributeName_4_0= RULE_STRING ) ) )? (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'xWidgetName' ( (lv_xWidgetName_8_0= RULE_STRING ) ) )? (otherlv_9= 'defaultValue' ( (lv_defaultValue_10_0= RULE_STRING ) ) )? (otherlv_11= 'height' ( (lv_height_12_0= RULE_INT ) ) )? (otherlv_13= 'option' ( (lv_option_14_0= ruleWidgetOption ) ) )* (otherlv_15= 'minConstraint' ( (lv_minConstraint_16_0= RULE_STRING ) ) )? (otherlv_17= 'maxConstraint' ( (lv_maxConstraint_18_0= RULE_STRING ) ) )? otherlv_19= '}' ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2030:1: (otherlv_0= 'widgetDefinition' ( (lv_name_1_0= ruleWIDGET_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'attributeName' ( (lv_attributeName_4_0= RULE_STRING ) ) )? (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'xWidgetName' ( (lv_xWidgetName_8_0= RULE_STRING ) ) )? (otherlv_9= 'defaultValue' ( (lv_defaultValue_10_0= RULE_STRING ) ) )? (otherlv_11= 'height' ( (lv_height_12_0= RULE_INT ) ) )? (otherlv_13= 'option' ( (lv_option_14_0= ruleWidgetOption ) ) )* (otherlv_15= 'minConstraint' ( (lv_minConstraint_16_0= RULE_STRING ) ) )? (otherlv_17= 'maxConstraint' ( (lv_maxConstraint_18_0= RULE_STRING ) ) )? otherlv_19= '}' )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2030:1: (otherlv_0= 'widgetDefinition' ( (lv_name_1_0= ruleWIDGET_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'attributeName' ( (lv_attributeName_4_0= RULE_STRING ) ) )? (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'xWidgetName' ( (lv_xWidgetName_8_0= RULE_STRING ) ) )? (otherlv_9= 'defaultValue' ( (lv_defaultValue_10_0= RULE_STRING ) ) )? (otherlv_11= 'height' ( (lv_height_12_0= RULE_INT ) ) )? (otherlv_13= 'option' ( (lv_option_14_0= ruleWidgetOption ) ) )* (otherlv_15= 'minConstraint' ( (lv_minConstraint_16_0= RULE_STRING ) ) )? (otherlv_17= 'maxConstraint' ( (lv_maxConstraint_18_0= RULE_STRING ) ) )? otherlv_19= '}' )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2030:3: otherlv_0= 'widgetDefinition' ( (lv_name_1_0= ruleWIDGET_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'attributeName' ( (lv_attributeName_4_0= RULE_STRING ) ) )? (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'xWidgetName' ( (lv_xWidgetName_8_0= RULE_STRING ) ) )? (otherlv_9= 'defaultValue' ( (lv_defaultValue_10_0= RULE_STRING ) ) )? (otherlv_11= 'height' ( (lv_height_12_0= RULE_INT ) ) )? (otherlv_13= 'option' ( (lv_option_14_0= ruleWidgetOption ) ) )* (otherlv_15= 'minConstraint' ( (lv_minConstraint_16_0= RULE_STRING ) ) )? (otherlv_17= 'maxConstraint' ( (lv_maxConstraint_18_0= RULE_STRING ) ) )? otherlv_19= '}'
            {
            otherlv_0=(Token)match(input,48,FOLLOW_48_in_ruleWidgetDef4050); 

                	newLeafNode(otherlv_0, grammarAccess.getWidgetDefAccess().getWidgetDefinitionKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2034:1: ( (lv_name_1_0= ruleWIDGET_NAME_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2035:1: (lv_name_1_0= ruleWIDGET_NAME_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2035:1: (lv_name_1_0= ruleWIDGET_NAME_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2036:3: lv_name_1_0= ruleWIDGET_NAME_REFERENCE
            {
             
            	        newCompositeNode(grammarAccess.getWidgetDefAccess().getNameWIDGET_NAME_REFERENCEParserRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_ruleWIDGET_NAME_REFERENCE_in_ruleWidgetDef4071);
            lv_name_1_0=ruleWIDGET_NAME_REFERENCE();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getWidgetDefRule());
            	        }
                   		set(
                   			current, 
                   			"name",
                    		lv_name_1_0, 
                    		"WIDGET_NAME_REFERENCE");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            otherlv_2=(Token)match(input,16,FOLLOW_16_in_ruleWidgetDef4083); 

                	newLeafNode(otherlv_2, grammarAccess.getWidgetDefAccess().getLeftCurlyBracketKeyword_2());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2056:1: (otherlv_3= 'attributeName' ( (lv_attributeName_4_0= RULE_STRING ) ) )?
            int alt65=2;
            int LA65_0 = input.LA(1);

            if ( (LA65_0==49) ) {
                alt65=1;
            }
            switch (alt65) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2056:3: otherlv_3= 'attributeName' ( (lv_attributeName_4_0= RULE_STRING ) )
                    {
                    otherlv_3=(Token)match(input,49,FOLLOW_49_in_ruleWidgetDef4096); 

                        	newLeafNode(otherlv_3, grammarAccess.getWidgetDefAccess().getAttributeNameKeyword_3_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2060:1: ( (lv_attributeName_4_0= RULE_STRING ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2061:1: (lv_attributeName_4_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2061:1: (lv_attributeName_4_0= RULE_STRING )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2062:3: lv_attributeName_4_0= RULE_STRING
                    {
                    lv_attributeName_4_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleWidgetDef4113); 

                    			newLeafNode(lv_attributeName_4_0, grammarAccess.getWidgetDefAccess().getAttributeNameSTRINGTerminalRuleCall_3_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getWidgetDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"attributeName",
                            		lv_attributeName_4_0, 
                            		"STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2078:4: (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )?
            int alt66=2;
            int LA66_0 = input.LA(1);

            if ( (LA66_0==50) ) {
                alt66=1;
            }
            switch (alt66) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2078:6: otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) )
                    {
                    otherlv_5=(Token)match(input,50,FOLLOW_50_in_ruleWidgetDef4133); 

                        	newLeafNode(otherlv_5, grammarAccess.getWidgetDefAccess().getDescriptionKeyword_4_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2082:1: ( (lv_description_6_0= RULE_STRING ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2083:1: (lv_description_6_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2083:1: (lv_description_6_0= RULE_STRING )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2084:3: lv_description_6_0= RULE_STRING
                    {
                    lv_description_6_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleWidgetDef4150); 

                    			newLeafNode(lv_description_6_0, grammarAccess.getWidgetDefAccess().getDescriptionSTRINGTerminalRuleCall_4_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getWidgetDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"description",
                            		lv_description_6_0, 
                            		"STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2100:4: (otherlv_7= 'xWidgetName' ( (lv_xWidgetName_8_0= RULE_STRING ) ) )?
            int alt67=2;
            int LA67_0 = input.LA(1);

            if ( (LA67_0==51) ) {
                alt67=1;
            }
            switch (alt67) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2100:6: otherlv_7= 'xWidgetName' ( (lv_xWidgetName_8_0= RULE_STRING ) )
                    {
                    otherlv_7=(Token)match(input,51,FOLLOW_51_in_ruleWidgetDef4170); 

                        	newLeafNode(otherlv_7, grammarAccess.getWidgetDefAccess().getXWidgetNameKeyword_5_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2104:1: ( (lv_xWidgetName_8_0= RULE_STRING ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2105:1: (lv_xWidgetName_8_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2105:1: (lv_xWidgetName_8_0= RULE_STRING )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2106:3: lv_xWidgetName_8_0= RULE_STRING
                    {
                    lv_xWidgetName_8_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleWidgetDef4187); 

                    			newLeafNode(lv_xWidgetName_8_0, grammarAccess.getWidgetDefAccess().getXWidgetNameSTRINGTerminalRuleCall_5_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getWidgetDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"xWidgetName",
                            		lv_xWidgetName_8_0, 
                            		"STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2122:4: (otherlv_9= 'defaultValue' ( (lv_defaultValue_10_0= RULE_STRING ) ) )?
            int alt68=2;
            int LA68_0 = input.LA(1);

            if ( (LA68_0==52) ) {
                alt68=1;
            }
            switch (alt68) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2122:6: otherlv_9= 'defaultValue' ( (lv_defaultValue_10_0= RULE_STRING ) )
                    {
                    otherlv_9=(Token)match(input,52,FOLLOW_52_in_ruleWidgetDef4207); 

                        	newLeafNode(otherlv_9, grammarAccess.getWidgetDefAccess().getDefaultValueKeyword_6_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2126:1: ( (lv_defaultValue_10_0= RULE_STRING ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2127:1: (lv_defaultValue_10_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2127:1: (lv_defaultValue_10_0= RULE_STRING )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2128:3: lv_defaultValue_10_0= RULE_STRING
                    {
                    lv_defaultValue_10_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleWidgetDef4224); 

                    			newLeafNode(lv_defaultValue_10_0, grammarAccess.getWidgetDefAccess().getDefaultValueSTRINGTerminalRuleCall_6_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getWidgetDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"defaultValue",
                            		lv_defaultValue_10_0, 
                            		"STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2144:4: (otherlv_11= 'height' ( (lv_height_12_0= RULE_INT ) ) )?
            int alt69=2;
            int LA69_0 = input.LA(1);

            if ( (LA69_0==53) ) {
                alt69=1;
            }
            switch (alt69) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2144:6: otherlv_11= 'height' ( (lv_height_12_0= RULE_INT ) )
                    {
                    otherlv_11=(Token)match(input,53,FOLLOW_53_in_ruleWidgetDef4244); 

                        	newLeafNode(otherlv_11, grammarAccess.getWidgetDefAccess().getHeightKeyword_7_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2148:1: ( (lv_height_12_0= RULE_INT ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2149:1: (lv_height_12_0= RULE_INT )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2149:1: (lv_height_12_0= RULE_INT )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2150:3: lv_height_12_0= RULE_INT
                    {
                    lv_height_12_0=(Token)match(input,RULE_INT,FOLLOW_RULE_INT_in_ruleWidgetDef4261); 

                    			newLeafNode(lv_height_12_0, grammarAccess.getWidgetDefAccess().getHeightINTTerminalRuleCall_7_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getWidgetDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"height",
                            		lv_height_12_0, 
                            		"INT");
                    	    

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2166:4: (otherlv_13= 'option' ( (lv_option_14_0= ruleWidgetOption ) ) )*
            loop70:
            do {
                int alt70=2;
                int LA70_0 = input.LA(1);

                if ( (LA70_0==54) ) {
                    alt70=1;
                }


                switch (alt70) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2166:6: otherlv_13= 'option' ( (lv_option_14_0= ruleWidgetOption ) )
            	    {
            	    otherlv_13=(Token)match(input,54,FOLLOW_54_in_ruleWidgetDef4281); 

            	        	newLeafNode(otherlv_13, grammarAccess.getWidgetDefAccess().getOptionKeyword_8_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2170:1: ( (lv_option_14_0= ruleWidgetOption ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2171:1: (lv_option_14_0= ruleWidgetOption )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2171:1: (lv_option_14_0= ruleWidgetOption )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2172:3: lv_option_14_0= ruleWidgetOption
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getWidgetDefAccess().getOptionWidgetOptionParserRuleCall_8_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleWidgetOption_in_ruleWidgetDef4302);
            	    lv_option_14_0=ruleWidgetOption();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getWidgetDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"option",
            	            		lv_option_14_0, 
            	            		"WidgetOption");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop70;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2188:4: (otherlv_15= 'minConstraint' ( (lv_minConstraint_16_0= RULE_STRING ) ) )?
            int alt71=2;
            int LA71_0 = input.LA(1);

            if ( (LA71_0==55) ) {
                alt71=1;
            }
            switch (alt71) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2188:6: otherlv_15= 'minConstraint' ( (lv_minConstraint_16_0= RULE_STRING ) )
                    {
                    otherlv_15=(Token)match(input,55,FOLLOW_55_in_ruleWidgetDef4317); 

                        	newLeafNode(otherlv_15, grammarAccess.getWidgetDefAccess().getMinConstraintKeyword_9_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2192:1: ( (lv_minConstraint_16_0= RULE_STRING ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2193:1: (lv_minConstraint_16_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2193:1: (lv_minConstraint_16_0= RULE_STRING )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2194:3: lv_minConstraint_16_0= RULE_STRING
                    {
                    lv_minConstraint_16_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleWidgetDef4334); 

                    			newLeafNode(lv_minConstraint_16_0, grammarAccess.getWidgetDefAccess().getMinConstraintSTRINGTerminalRuleCall_9_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getWidgetDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"minConstraint",
                            		lv_minConstraint_16_0, 
                            		"STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2210:4: (otherlv_17= 'maxConstraint' ( (lv_maxConstraint_18_0= RULE_STRING ) ) )?
            int alt72=2;
            int LA72_0 = input.LA(1);

            if ( (LA72_0==56) ) {
                alt72=1;
            }
            switch (alt72) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2210:6: otherlv_17= 'maxConstraint' ( (lv_maxConstraint_18_0= RULE_STRING ) )
                    {
                    otherlv_17=(Token)match(input,56,FOLLOW_56_in_ruleWidgetDef4354); 

                        	newLeafNode(otherlv_17, grammarAccess.getWidgetDefAccess().getMaxConstraintKeyword_10_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2214:1: ( (lv_maxConstraint_18_0= RULE_STRING ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2215:1: (lv_maxConstraint_18_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2215:1: (lv_maxConstraint_18_0= RULE_STRING )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2216:3: lv_maxConstraint_18_0= RULE_STRING
                    {
                    lv_maxConstraint_18_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleWidgetDef4371); 

                    			newLeafNode(lv_maxConstraint_18_0, grammarAccess.getWidgetDefAccess().getMaxConstraintSTRINGTerminalRuleCall_10_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getWidgetDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"maxConstraint",
                            		lv_maxConstraint_18_0, 
                            		"STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            otherlv_19=(Token)match(input,21,FOLLOW_21_in_ruleWidgetDef4390); 

                	newLeafNode(otherlv_19, grammarAccess.getWidgetDefAccess().getRightCurlyBracketKeyword_11());
                

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleWidgetDef"


    // $ANTLR start "entryRuleWidgetRef"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2244:1: entryRuleWidgetRef returns [EObject current=null] : iv_ruleWidgetRef= ruleWidgetRef EOF ;
    public final EObject entryRuleWidgetRef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleWidgetRef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2245:2: (iv_ruleWidgetRef= ruleWidgetRef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2246:2: iv_ruleWidgetRef= ruleWidgetRef EOF
            {
             newCompositeNode(grammarAccess.getWidgetRefRule()); 
            pushFollow(FOLLOW_ruleWidgetRef_in_entryRuleWidgetRef4426);
            iv_ruleWidgetRef=ruleWidgetRef();

            state._fsp--;

             current =iv_ruleWidgetRef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleWidgetRef4436); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleWidgetRef"


    // $ANTLR start "ruleWidgetRef"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2253:1: ruleWidgetRef returns [EObject current=null] : (otherlv_0= 'widget' ( ( ruleWIDGET_NAME_REFERENCE ) ) ) ;
    public final EObject ruleWidgetRef() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2256:28: ( (otherlv_0= 'widget' ( ( ruleWIDGET_NAME_REFERENCE ) ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2257:1: (otherlv_0= 'widget' ( ( ruleWIDGET_NAME_REFERENCE ) ) )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2257:1: (otherlv_0= 'widget' ( ( ruleWIDGET_NAME_REFERENCE ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2257:3: otherlv_0= 'widget' ( ( ruleWIDGET_NAME_REFERENCE ) )
            {
            otherlv_0=(Token)match(input,57,FOLLOW_57_in_ruleWidgetRef4473); 

                	newLeafNode(otherlv_0, grammarAccess.getWidgetRefAccess().getWidgetKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2261:1: ( ( ruleWIDGET_NAME_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2262:1: ( ruleWIDGET_NAME_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2262:1: ( ruleWIDGET_NAME_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2263:3: ruleWIDGET_NAME_REFERENCE
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getWidgetRefRule());
            	        }
                    
             
            	        newCompositeNode(grammarAccess.getWidgetRefAccess().getWidgetWidgetDefCrossReference_1_0()); 
            	    
            pushFollow(FOLLOW_ruleWIDGET_NAME_REFERENCE_in_ruleWidgetRef4496);
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
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleWidgetRef"


    // $ANTLR start "entryRuleAttrWidget"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2284:1: entryRuleAttrWidget returns [EObject current=null] : iv_ruleAttrWidget= ruleAttrWidget EOF ;
    public final EObject entryRuleAttrWidget() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttrWidget = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2285:2: (iv_ruleAttrWidget= ruleAttrWidget EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2286:2: iv_ruleAttrWidget= ruleAttrWidget EOF
            {
             newCompositeNode(grammarAccess.getAttrWidgetRule()); 
            pushFollow(FOLLOW_ruleAttrWidget_in_entryRuleAttrWidget4532);
            iv_ruleAttrWidget=ruleAttrWidget();

            state._fsp--;

             current =iv_ruleAttrWidget; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttrWidget4542); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleAttrWidget"


    // $ANTLR start "ruleAttrWidget"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2293:1: ruleAttrWidget returns [EObject current=null] : (otherlv_0= 'attributeWidget' ( (lv_attributeName_1_0= RULE_STRING ) ) (otherlv_2= 'with' ( (lv_option_3_0= ruleWidgetOption ) ) )* ) ;
    public final EObject ruleAttrWidget() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_attributeName_1_0=null;
        Token otherlv_2=null;
        AntlrDatatypeRuleToken lv_option_3_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2296:28: ( (otherlv_0= 'attributeWidget' ( (lv_attributeName_1_0= RULE_STRING ) ) (otherlv_2= 'with' ( (lv_option_3_0= ruleWidgetOption ) ) )* ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2297:1: (otherlv_0= 'attributeWidget' ( (lv_attributeName_1_0= RULE_STRING ) ) (otherlv_2= 'with' ( (lv_option_3_0= ruleWidgetOption ) ) )* )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2297:1: (otherlv_0= 'attributeWidget' ( (lv_attributeName_1_0= RULE_STRING ) ) (otherlv_2= 'with' ( (lv_option_3_0= ruleWidgetOption ) ) )* )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2297:3: otherlv_0= 'attributeWidget' ( (lv_attributeName_1_0= RULE_STRING ) ) (otherlv_2= 'with' ( (lv_option_3_0= ruleWidgetOption ) ) )*
            {
            otherlv_0=(Token)match(input,58,FOLLOW_58_in_ruleAttrWidget4579); 

                	newLeafNode(otherlv_0, grammarAccess.getAttrWidgetAccess().getAttributeWidgetKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2301:1: ( (lv_attributeName_1_0= RULE_STRING ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2302:1: (lv_attributeName_1_0= RULE_STRING )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2302:1: (lv_attributeName_1_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2303:3: lv_attributeName_1_0= RULE_STRING
            {
            lv_attributeName_1_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAttrWidget4596); 

            			newLeafNode(lv_attributeName_1_0, grammarAccess.getAttrWidgetAccess().getAttributeNameSTRINGTerminalRuleCall_1_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getAttrWidgetRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"attributeName",
                    		lv_attributeName_1_0, 
                    		"STRING");
            	    

            }


            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2319:2: (otherlv_2= 'with' ( (lv_option_3_0= ruleWidgetOption ) ) )*
            loop73:
            do {
                int alt73=2;
                int LA73_0 = input.LA(1);

                if ( (LA73_0==59) ) {
                    alt73=1;
                }


                switch (alt73) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2319:4: otherlv_2= 'with' ( (lv_option_3_0= ruleWidgetOption ) )
            	    {
            	    otherlv_2=(Token)match(input,59,FOLLOW_59_in_ruleAttrWidget4614); 

            	        	newLeafNode(otherlv_2, grammarAccess.getAttrWidgetAccess().getWithKeyword_2_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2323:1: ( (lv_option_3_0= ruleWidgetOption ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2324:1: (lv_option_3_0= ruleWidgetOption )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2324:1: (lv_option_3_0= ruleWidgetOption )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2325:3: lv_option_3_0= ruleWidgetOption
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getAttrWidgetAccess().getOptionWidgetOptionParserRuleCall_2_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleWidgetOption_in_ruleAttrWidget4635);
            	    lv_option_3_0=ruleWidgetOption();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getAttrWidgetRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"option",
            	            		lv_option_3_0, 
            	            		"WidgetOption");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop73;
                }
            } while (true);


            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleAttrWidget"


    // $ANTLR start "entryRuleStateDef"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2349:1: entryRuleStateDef returns [EObject current=null] : iv_ruleStateDef= ruleStateDef EOF ;
    public final EObject entryRuleStateDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleStateDef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2350:2: (iv_ruleStateDef= ruleStateDef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2351:2: iv_ruleStateDef= ruleStateDef EOF
            {
             newCompositeNode(grammarAccess.getStateDefRule()); 
            pushFollow(FOLLOW_ruleStateDef_in_entryRuleStateDef4673);
            iv_ruleStateDef=ruleStateDef();

            state._fsp--;

             current =iv_ruleStateDef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleStateDef4683); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleStateDef"


    // $ANTLR start "ruleStateDef"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2358:1: ruleStateDef returns [EObject current=null] : (otherlv_0= 'state' ( (lv_name_1_0= ruleSTATE_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'description' ( (lv_description_4_0= RULE_STRING ) ) )? otherlv_5= 'type' ( (lv_pageType_6_0= rulePageType ) ) otherlv_7= 'ordinal' ( (lv_ordinal_8_0= RULE_INT ) ) ( (lv_transitionStates_9_0= ruleToState ) )* (otherlv_10= 'rule' ( (lv_rules_11_0= ruleRule ) ) )* ( (lv_decisionReviews_12_0= ruleDecisionReviewRef ) )* ( (lv_peerReviews_13_0= rulePeerReviewRef ) )* (otherlv_14= 'percentWeight' ( (lv_percentWeight_15_0= RULE_INT ) ) )? (otherlv_16= 'recommendedPercentComplete' ( (lv_recommendedPercentComplete_17_0= RULE_INT ) ) )? (otherlv_18= 'color' ( (lv_color_19_0= ruleStateColor ) ) )? ( (lv_layout_20_0= ruleLayoutType ) )? otherlv_21= '}' ) ;
    public final EObject ruleStateDef() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_2=null;
        Token otherlv_3=null;
        Token lv_description_4_0=null;
        Token otherlv_5=null;
        Token otherlv_7=null;
        Token lv_ordinal_8_0=null;
        Token otherlv_10=null;
        Token otherlv_14=null;
        Token lv_percentWeight_15_0=null;
        Token otherlv_16=null;
        Token lv_recommendedPercentComplete_17_0=null;
        Token otherlv_18=null;
        Token otherlv_21=null;
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
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2361:28: ( (otherlv_0= 'state' ( (lv_name_1_0= ruleSTATE_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'description' ( (lv_description_4_0= RULE_STRING ) ) )? otherlv_5= 'type' ( (lv_pageType_6_0= rulePageType ) ) otherlv_7= 'ordinal' ( (lv_ordinal_8_0= RULE_INT ) ) ( (lv_transitionStates_9_0= ruleToState ) )* (otherlv_10= 'rule' ( (lv_rules_11_0= ruleRule ) ) )* ( (lv_decisionReviews_12_0= ruleDecisionReviewRef ) )* ( (lv_peerReviews_13_0= rulePeerReviewRef ) )* (otherlv_14= 'percentWeight' ( (lv_percentWeight_15_0= RULE_INT ) ) )? (otherlv_16= 'recommendedPercentComplete' ( (lv_recommendedPercentComplete_17_0= RULE_INT ) ) )? (otherlv_18= 'color' ( (lv_color_19_0= ruleStateColor ) ) )? ( (lv_layout_20_0= ruleLayoutType ) )? otherlv_21= '}' ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2362:1: (otherlv_0= 'state' ( (lv_name_1_0= ruleSTATE_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'description' ( (lv_description_4_0= RULE_STRING ) ) )? otherlv_5= 'type' ( (lv_pageType_6_0= rulePageType ) ) otherlv_7= 'ordinal' ( (lv_ordinal_8_0= RULE_INT ) ) ( (lv_transitionStates_9_0= ruleToState ) )* (otherlv_10= 'rule' ( (lv_rules_11_0= ruleRule ) ) )* ( (lv_decisionReviews_12_0= ruleDecisionReviewRef ) )* ( (lv_peerReviews_13_0= rulePeerReviewRef ) )* (otherlv_14= 'percentWeight' ( (lv_percentWeight_15_0= RULE_INT ) ) )? (otherlv_16= 'recommendedPercentComplete' ( (lv_recommendedPercentComplete_17_0= RULE_INT ) ) )? (otherlv_18= 'color' ( (lv_color_19_0= ruleStateColor ) ) )? ( (lv_layout_20_0= ruleLayoutType ) )? otherlv_21= '}' )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2362:1: (otherlv_0= 'state' ( (lv_name_1_0= ruleSTATE_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'description' ( (lv_description_4_0= RULE_STRING ) ) )? otherlv_5= 'type' ( (lv_pageType_6_0= rulePageType ) ) otherlv_7= 'ordinal' ( (lv_ordinal_8_0= RULE_INT ) ) ( (lv_transitionStates_9_0= ruleToState ) )* (otherlv_10= 'rule' ( (lv_rules_11_0= ruleRule ) ) )* ( (lv_decisionReviews_12_0= ruleDecisionReviewRef ) )* ( (lv_peerReviews_13_0= rulePeerReviewRef ) )* (otherlv_14= 'percentWeight' ( (lv_percentWeight_15_0= RULE_INT ) ) )? (otherlv_16= 'recommendedPercentComplete' ( (lv_recommendedPercentComplete_17_0= RULE_INT ) ) )? (otherlv_18= 'color' ( (lv_color_19_0= ruleStateColor ) ) )? ( (lv_layout_20_0= ruleLayoutType ) )? otherlv_21= '}' )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2362:3: otherlv_0= 'state' ( (lv_name_1_0= ruleSTATE_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'description' ( (lv_description_4_0= RULE_STRING ) ) )? otherlv_5= 'type' ( (lv_pageType_6_0= rulePageType ) ) otherlv_7= 'ordinal' ( (lv_ordinal_8_0= RULE_INT ) ) ( (lv_transitionStates_9_0= ruleToState ) )* (otherlv_10= 'rule' ( (lv_rules_11_0= ruleRule ) ) )* ( (lv_decisionReviews_12_0= ruleDecisionReviewRef ) )* ( (lv_peerReviews_13_0= rulePeerReviewRef ) )* (otherlv_14= 'percentWeight' ( (lv_percentWeight_15_0= RULE_INT ) ) )? (otherlv_16= 'recommendedPercentComplete' ( (lv_recommendedPercentComplete_17_0= RULE_INT ) ) )? (otherlv_18= 'color' ( (lv_color_19_0= ruleStateColor ) ) )? ( (lv_layout_20_0= ruleLayoutType ) )? otherlv_21= '}'
            {
            otherlv_0=(Token)match(input,60,FOLLOW_60_in_ruleStateDef4720); 

                	newLeafNode(otherlv_0, grammarAccess.getStateDefAccess().getStateKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2366:1: ( (lv_name_1_0= ruleSTATE_NAME_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2367:1: (lv_name_1_0= ruleSTATE_NAME_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2367:1: (lv_name_1_0= ruleSTATE_NAME_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2368:3: lv_name_1_0= ruleSTATE_NAME_REFERENCE
            {
             
            	        newCompositeNode(grammarAccess.getStateDefAccess().getNameSTATE_NAME_REFERENCEParserRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleStateDef4741);
            lv_name_1_0=ruleSTATE_NAME_REFERENCE();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getStateDefRule());
            	        }
                   		set(
                   			current, 
                   			"name",
                    		lv_name_1_0, 
                    		"STATE_NAME_REFERENCE");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            otherlv_2=(Token)match(input,16,FOLLOW_16_in_ruleStateDef4753); 

                	newLeafNode(otherlv_2, grammarAccess.getStateDefAccess().getLeftCurlyBracketKeyword_2());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2388:1: (otherlv_3= 'description' ( (lv_description_4_0= RULE_STRING ) ) )?
            int alt74=2;
            int LA74_0 = input.LA(1);

            if ( (LA74_0==50) ) {
                alt74=1;
            }
            switch (alt74) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2388:3: otherlv_3= 'description' ( (lv_description_4_0= RULE_STRING ) )
                    {
                    otherlv_3=(Token)match(input,50,FOLLOW_50_in_ruleStateDef4766); 

                        	newLeafNode(otherlv_3, grammarAccess.getStateDefAccess().getDescriptionKeyword_3_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2392:1: ( (lv_description_4_0= RULE_STRING ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2393:1: (lv_description_4_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2393:1: (lv_description_4_0= RULE_STRING )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2394:3: lv_description_4_0= RULE_STRING
                    {
                    lv_description_4_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleStateDef4783); 

                    			newLeafNode(lv_description_4_0, grammarAccess.getStateDefAccess().getDescriptionSTRINGTerminalRuleCall_3_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getStateDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"description",
                            		lv_description_4_0, 
                            		"STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            otherlv_5=(Token)match(input,61,FOLLOW_61_in_ruleStateDef4802); 

                	newLeafNode(otherlv_5, grammarAccess.getStateDefAccess().getTypeKeyword_4());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2414:1: ( (lv_pageType_6_0= rulePageType ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2415:1: (lv_pageType_6_0= rulePageType )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2415:1: (lv_pageType_6_0= rulePageType )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2416:3: lv_pageType_6_0= rulePageType
            {
             
            	        newCompositeNode(grammarAccess.getStateDefAccess().getPageTypePageTypeParserRuleCall_5_0()); 
            	    
            pushFollow(FOLLOW_rulePageType_in_ruleStateDef4823);
            lv_pageType_6_0=rulePageType();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getStateDefRule());
            	        }
                   		set(
                   			current, 
                   			"pageType",
                    		lv_pageType_6_0, 
                    		"PageType");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            otherlv_7=(Token)match(input,62,FOLLOW_62_in_ruleStateDef4835); 

                	newLeafNode(otherlv_7, grammarAccess.getStateDefAccess().getOrdinalKeyword_6());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2436:1: ( (lv_ordinal_8_0= RULE_INT ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2437:1: (lv_ordinal_8_0= RULE_INT )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2437:1: (lv_ordinal_8_0= RULE_INT )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2438:3: lv_ordinal_8_0= RULE_INT
            {
            lv_ordinal_8_0=(Token)match(input,RULE_INT,FOLLOW_RULE_INT_in_ruleStateDef4852); 

            			newLeafNode(lv_ordinal_8_0, grammarAccess.getStateDefAccess().getOrdinalINTTerminalRuleCall_7_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getStateDefRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"ordinal",
                    		lv_ordinal_8_0, 
                    		"INT");
            	    

            }


            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2454:2: ( (lv_transitionStates_9_0= ruleToState ) )*
            loop75:
            do {
                int alt75=2;
                int LA75_0 = input.LA(1);

                if ( (LA75_0==80) ) {
                    alt75=1;
                }


                switch (alt75) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2455:1: (lv_transitionStates_9_0= ruleToState )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2455:1: (lv_transitionStates_9_0= ruleToState )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2456:3: lv_transitionStates_9_0= ruleToState
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getStateDefAccess().getTransitionStatesToStateParserRuleCall_8_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleToState_in_ruleStateDef4878);
            	    lv_transitionStates_9_0=ruleToState();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getStateDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"transitionStates",
            	            		lv_transitionStates_9_0, 
            	            		"ToState");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    break loop75;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2472:3: (otherlv_10= 'rule' ( (lv_rules_11_0= ruleRule ) ) )*
            loop76:
            do {
                int alt76=2;
                int LA76_0 = input.LA(1);

                if ( (LA76_0==63) ) {
                    alt76=1;
                }


                switch (alt76) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2472:5: otherlv_10= 'rule' ( (lv_rules_11_0= ruleRule ) )
            	    {
            	    otherlv_10=(Token)match(input,63,FOLLOW_63_in_ruleStateDef4892); 

            	        	newLeafNode(otherlv_10, grammarAccess.getStateDefAccess().getRuleKeyword_9_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2476:1: ( (lv_rules_11_0= ruleRule ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2477:1: (lv_rules_11_0= ruleRule )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2477:1: (lv_rules_11_0= ruleRule )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2478:3: lv_rules_11_0= ruleRule
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getStateDefAccess().getRulesRuleParserRuleCall_9_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleRule_in_ruleStateDef4913);
            	    lv_rules_11_0=ruleRule();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getStateDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"rules",
            	            		lv_rules_11_0, 
            	            		"Rule");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop76;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2494:4: ( (lv_decisionReviews_12_0= ruleDecisionReviewRef ) )*
            loop77:
            do {
                int alt77=2;
                int LA77_0 = input.LA(1);

                if ( (LA77_0==67) ) {
                    alt77=1;
                }


                switch (alt77) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2495:1: (lv_decisionReviews_12_0= ruleDecisionReviewRef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2495:1: (lv_decisionReviews_12_0= ruleDecisionReviewRef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2496:3: lv_decisionReviews_12_0= ruleDecisionReviewRef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getStateDefAccess().getDecisionReviewsDecisionReviewRefParserRuleCall_10_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleDecisionReviewRef_in_ruleStateDef4936);
            	    lv_decisionReviews_12_0=ruleDecisionReviewRef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getStateDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"decisionReviews",
            	            		lv_decisionReviews_12_0, 
            	            		"DecisionReviewRef");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    break loop77;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2512:3: ( (lv_peerReviews_13_0= rulePeerReviewRef ) )*
            loop78:
            do {
                int alt78=2;
                int LA78_0 = input.LA(1);

                if ( (LA78_0==75) ) {
                    alt78=1;
                }


                switch (alt78) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2513:1: (lv_peerReviews_13_0= rulePeerReviewRef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2513:1: (lv_peerReviews_13_0= rulePeerReviewRef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2514:3: lv_peerReviews_13_0= rulePeerReviewRef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getStateDefAccess().getPeerReviewsPeerReviewRefParserRuleCall_11_0()); 
            	    	    
            	    pushFollow(FOLLOW_rulePeerReviewRef_in_ruleStateDef4958);
            	    lv_peerReviews_13_0=rulePeerReviewRef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getStateDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"peerReviews",
            	            		lv_peerReviews_13_0, 
            	            		"PeerReviewRef");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    break loop78;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2530:3: (otherlv_14= 'percentWeight' ( (lv_percentWeight_15_0= RULE_INT ) ) )?
            int alt79=2;
            int LA79_0 = input.LA(1);

            if ( (LA79_0==64) ) {
                alt79=1;
            }
            switch (alt79) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2530:5: otherlv_14= 'percentWeight' ( (lv_percentWeight_15_0= RULE_INT ) )
                    {
                    otherlv_14=(Token)match(input,64,FOLLOW_64_in_ruleStateDef4972); 

                        	newLeafNode(otherlv_14, grammarAccess.getStateDefAccess().getPercentWeightKeyword_12_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2534:1: ( (lv_percentWeight_15_0= RULE_INT ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2535:1: (lv_percentWeight_15_0= RULE_INT )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2535:1: (lv_percentWeight_15_0= RULE_INT )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2536:3: lv_percentWeight_15_0= RULE_INT
                    {
                    lv_percentWeight_15_0=(Token)match(input,RULE_INT,FOLLOW_RULE_INT_in_ruleStateDef4989); 

                    			newLeafNode(lv_percentWeight_15_0, grammarAccess.getStateDefAccess().getPercentWeightINTTerminalRuleCall_12_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getStateDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"percentWeight",
                            		lv_percentWeight_15_0, 
                            		"INT");
                    	    

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2552:4: (otherlv_16= 'recommendedPercentComplete' ( (lv_recommendedPercentComplete_17_0= RULE_INT ) ) )?
            int alt80=2;
            int LA80_0 = input.LA(1);

            if ( (LA80_0==65) ) {
                alt80=1;
            }
            switch (alt80) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2552:6: otherlv_16= 'recommendedPercentComplete' ( (lv_recommendedPercentComplete_17_0= RULE_INT ) )
                    {
                    otherlv_16=(Token)match(input,65,FOLLOW_65_in_ruleStateDef5009); 

                        	newLeafNode(otherlv_16, grammarAccess.getStateDefAccess().getRecommendedPercentCompleteKeyword_13_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2556:1: ( (lv_recommendedPercentComplete_17_0= RULE_INT ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2557:1: (lv_recommendedPercentComplete_17_0= RULE_INT )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2557:1: (lv_recommendedPercentComplete_17_0= RULE_INT )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2558:3: lv_recommendedPercentComplete_17_0= RULE_INT
                    {
                    lv_recommendedPercentComplete_17_0=(Token)match(input,RULE_INT,FOLLOW_RULE_INT_in_ruleStateDef5026); 

                    			newLeafNode(lv_recommendedPercentComplete_17_0, grammarAccess.getStateDefAccess().getRecommendedPercentCompleteINTTerminalRuleCall_13_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getStateDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"recommendedPercentComplete",
                            		lv_recommendedPercentComplete_17_0, 
                            		"INT");
                    	    

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2574:4: (otherlv_18= 'color' ( (lv_color_19_0= ruleStateColor ) ) )?
            int alt81=2;
            int LA81_0 = input.LA(1);

            if ( (LA81_0==66) ) {
                alt81=1;
            }
            switch (alt81) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2574:6: otherlv_18= 'color' ( (lv_color_19_0= ruleStateColor ) )
                    {
                    otherlv_18=(Token)match(input,66,FOLLOW_66_in_ruleStateDef5046); 

                        	newLeafNode(otherlv_18, grammarAccess.getStateDefAccess().getColorKeyword_14_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2578:1: ( (lv_color_19_0= ruleStateColor ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2579:1: (lv_color_19_0= ruleStateColor )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2579:1: (lv_color_19_0= ruleStateColor )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2580:3: lv_color_19_0= ruleStateColor
                    {
                     
                    	        newCompositeNode(grammarAccess.getStateDefAccess().getColorStateColorParserRuleCall_14_1_0()); 
                    	    
                    pushFollow(FOLLOW_ruleStateColor_in_ruleStateDef5067);
                    lv_color_19_0=ruleStateColor();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getStateDefRule());
                    	        }
                           		set(
                           			current, 
                           			"color",
                            		lv_color_19_0, 
                            		"StateColor");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2596:4: ( (lv_layout_20_0= ruleLayoutType ) )?
            int alt82=2;
            int LA82_0 = input.LA(1);

            if ( ((LA82_0>=81 && LA82_0<=82)) ) {
                alt82=1;
            }
            switch (alt82) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2597:1: (lv_layout_20_0= ruleLayoutType )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2597:1: (lv_layout_20_0= ruleLayoutType )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2598:3: lv_layout_20_0= ruleLayoutType
                    {
                     
                    	        newCompositeNode(grammarAccess.getStateDefAccess().getLayoutLayoutTypeParserRuleCall_15_0()); 
                    	    
                    pushFollow(FOLLOW_ruleLayoutType_in_ruleStateDef5090);
                    lv_layout_20_0=ruleLayoutType();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getStateDefRule());
                    	        }
                           		set(
                           			current, 
                           			"layout",
                            		lv_layout_20_0, 
                            		"LayoutType");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }
                    break;

            }

            otherlv_21=(Token)match(input,21,FOLLOW_21_in_ruleStateDef5103); 

                	newLeafNode(otherlv_21, grammarAccess.getStateDefAccess().getRightCurlyBracketKeyword_16());
                

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleStateDef"


    // $ANTLR start "entryRuleDecisionReviewRef"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2626:1: entryRuleDecisionReviewRef returns [EObject current=null] : iv_ruleDecisionReviewRef= ruleDecisionReviewRef EOF ;
    public final EObject entryRuleDecisionReviewRef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDecisionReviewRef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2627:2: (iv_ruleDecisionReviewRef= ruleDecisionReviewRef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2628:2: iv_ruleDecisionReviewRef= ruleDecisionReviewRef EOF
            {
             newCompositeNode(grammarAccess.getDecisionReviewRefRule()); 
            pushFollow(FOLLOW_ruleDecisionReviewRef_in_entryRuleDecisionReviewRef5139);
            iv_ruleDecisionReviewRef=ruleDecisionReviewRef();

            state._fsp--;

             current =iv_ruleDecisionReviewRef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleDecisionReviewRef5149); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleDecisionReviewRef"


    // $ANTLR start "ruleDecisionReviewRef"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2635:1: ruleDecisionReviewRef returns [EObject current=null] : (otherlv_0= 'decisionReview' ( ( ruleDECISION_REVIEW_NAME_REFERENCE ) ) ) ;
    public final EObject ruleDecisionReviewRef() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2638:28: ( (otherlv_0= 'decisionReview' ( ( ruleDECISION_REVIEW_NAME_REFERENCE ) ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2639:1: (otherlv_0= 'decisionReview' ( ( ruleDECISION_REVIEW_NAME_REFERENCE ) ) )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2639:1: (otherlv_0= 'decisionReview' ( ( ruleDECISION_REVIEW_NAME_REFERENCE ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2639:3: otherlv_0= 'decisionReview' ( ( ruleDECISION_REVIEW_NAME_REFERENCE ) )
            {
            otherlv_0=(Token)match(input,67,FOLLOW_67_in_ruleDecisionReviewRef5186); 

                	newLeafNode(otherlv_0, grammarAccess.getDecisionReviewRefAccess().getDecisionReviewKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2643:1: ( ( ruleDECISION_REVIEW_NAME_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2644:1: ( ruleDECISION_REVIEW_NAME_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2644:1: ( ruleDECISION_REVIEW_NAME_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2645:3: ruleDECISION_REVIEW_NAME_REFERENCE
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getDecisionReviewRefRule());
            	        }
                    
             
            	        newCompositeNode(grammarAccess.getDecisionReviewRefAccess().getDecisionReviewDecisionReviewDefCrossReference_1_0()); 
            	    
            pushFollow(FOLLOW_ruleDECISION_REVIEW_NAME_REFERENCE_in_ruleDecisionReviewRef5209);
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
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleDecisionReviewRef"


    // $ANTLR start "entryRuleDecisionReviewDef"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2666:1: entryRuleDecisionReviewDef returns [EObject current=null] : iv_ruleDecisionReviewDef= ruleDecisionReviewDef EOF ;
    public final EObject entryRuleDecisionReviewDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDecisionReviewDef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2667:2: (iv_ruleDecisionReviewDef= ruleDecisionReviewDef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2668:2: iv_ruleDecisionReviewDef= ruleDecisionReviewDef EOF
            {
             newCompositeNode(grammarAccess.getDecisionReviewDefRule()); 
            pushFollow(FOLLOW_ruleDecisionReviewDef_in_entryRuleDecisionReviewDef5245);
            iv_ruleDecisionReviewDef=ruleDecisionReviewDef();

            state._fsp--;

             current =iv_ruleDecisionReviewDef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleDecisionReviewDef5255); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleDecisionReviewDef"


    // $ANTLR start "ruleDecisionReviewDef"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2675:1: ruleDecisionReviewDef returns [EObject current=null] : (otherlv_0= 'decisionReviewDefinition' ( (lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_9= 'blockingType' ( (lv_blockingType_10_0= ruleReviewBlockingType ) ) otherlv_11= 'onEvent' ( (lv_stateEvent_12_0= ruleWorkflowEventType ) ) (otherlv_13= 'assignee' ( (lv_assigneeRefs_14_0= ruleUserRef ) ) )* (otherlv_15= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_16_0= ruleBooleanDef ) ) )? ( (lv_options_17_0= ruleDecisionReviewOpt ) )+ otherlv_18= '}' ) ;
    public final EObject ruleDecisionReviewDef() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_2=null;
        Token otherlv_3=null;
        Token lv_title_4_0=null;
        Token otherlv_5=null;
        Token lv_description_6_0=null;
        Token otherlv_7=null;
        Token otherlv_9=null;
        Token otherlv_11=null;
        Token otherlv_13=null;
        Token otherlv_15=null;
        Token otherlv_18=null;
        AntlrDatatypeRuleToken lv_name_1_0 = null;

        Enumerator lv_blockingType_10_0 = null;

        Enumerator lv_stateEvent_12_0 = null;

        EObject lv_assigneeRefs_14_0 = null;

        Enumerator lv_autoTransitionToDecision_16_0 = null;

        EObject lv_options_17_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2678:28: ( (otherlv_0= 'decisionReviewDefinition' ( (lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_9= 'blockingType' ( (lv_blockingType_10_0= ruleReviewBlockingType ) ) otherlv_11= 'onEvent' ( (lv_stateEvent_12_0= ruleWorkflowEventType ) ) (otherlv_13= 'assignee' ( (lv_assigneeRefs_14_0= ruleUserRef ) ) )* (otherlv_15= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_16_0= ruleBooleanDef ) ) )? ( (lv_options_17_0= ruleDecisionReviewOpt ) )+ otherlv_18= '}' ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2679:1: (otherlv_0= 'decisionReviewDefinition' ( (lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_9= 'blockingType' ( (lv_blockingType_10_0= ruleReviewBlockingType ) ) otherlv_11= 'onEvent' ( (lv_stateEvent_12_0= ruleWorkflowEventType ) ) (otherlv_13= 'assignee' ( (lv_assigneeRefs_14_0= ruleUserRef ) ) )* (otherlv_15= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_16_0= ruleBooleanDef ) ) )? ( (lv_options_17_0= ruleDecisionReviewOpt ) )+ otherlv_18= '}' )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2679:1: (otherlv_0= 'decisionReviewDefinition' ( (lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_9= 'blockingType' ( (lv_blockingType_10_0= ruleReviewBlockingType ) ) otherlv_11= 'onEvent' ( (lv_stateEvent_12_0= ruleWorkflowEventType ) ) (otherlv_13= 'assignee' ( (lv_assigneeRefs_14_0= ruleUserRef ) ) )* (otherlv_15= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_16_0= ruleBooleanDef ) ) )? ( (lv_options_17_0= ruleDecisionReviewOpt ) )+ otherlv_18= '}' )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2679:3: otherlv_0= 'decisionReviewDefinition' ( (lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_9= 'blockingType' ( (lv_blockingType_10_0= ruleReviewBlockingType ) ) otherlv_11= 'onEvent' ( (lv_stateEvent_12_0= ruleWorkflowEventType ) ) (otherlv_13= 'assignee' ( (lv_assigneeRefs_14_0= ruleUserRef ) ) )* (otherlv_15= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_16_0= ruleBooleanDef ) ) )? ( (lv_options_17_0= ruleDecisionReviewOpt ) )+ otherlv_18= '}'
            {
            otherlv_0=(Token)match(input,68,FOLLOW_68_in_ruleDecisionReviewDef5292); 

                	newLeafNode(otherlv_0, grammarAccess.getDecisionReviewDefAccess().getDecisionReviewDefinitionKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2683:1: ( (lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2684:1: (lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2684:1: (lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2685:3: lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE
            {
             
            	        newCompositeNode(grammarAccess.getDecisionReviewDefAccess().getNameDECISION_REVIEW_NAME_REFERENCEParserRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_ruleDECISION_REVIEW_NAME_REFERENCE_in_ruleDecisionReviewDef5313);
            lv_name_1_0=ruleDECISION_REVIEW_NAME_REFERENCE();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getDecisionReviewDefRule());
            	        }
                   		set(
                   			current, 
                   			"name",
                    		lv_name_1_0, 
                    		"DECISION_REVIEW_NAME_REFERENCE");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            otherlv_2=(Token)match(input,16,FOLLOW_16_in_ruleDecisionReviewDef5325); 

                	newLeafNode(otherlv_2, grammarAccess.getDecisionReviewDefAccess().getLeftCurlyBracketKeyword_2());
                
            otherlv_3=(Token)match(input,69,FOLLOW_69_in_ruleDecisionReviewDef5337); 

                	newLeafNode(otherlv_3, grammarAccess.getDecisionReviewDefAccess().getTitleKeyword_3());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2709:1: ( (lv_title_4_0= RULE_STRING ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2710:1: (lv_title_4_0= RULE_STRING )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2710:1: (lv_title_4_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2711:3: lv_title_4_0= RULE_STRING
            {
            lv_title_4_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleDecisionReviewDef5354); 

            			newLeafNode(lv_title_4_0, grammarAccess.getDecisionReviewDefAccess().getTitleSTRINGTerminalRuleCall_4_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getDecisionReviewDefRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"title",
                    		lv_title_4_0, 
                    		"STRING");
            	    

            }


            }

            otherlv_5=(Token)match(input,50,FOLLOW_50_in_ruleDecisionReviewDef5371); 

                	newLeafNode(otherlv_5, grammarAccess.getDecisionReviewDefAccess().getDescriptionKeyword_5());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2731:1: ( (lv_description_6_0= RULE_STRING ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2732:1: (lv_description_6_0= RULE_STRING )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2732:1: (lv_description_6_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2733:3: lv_description_6_0= RULE_STRING
            {
            lv_description_6_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleDecisionReviewDef5388); 

            			newLeafNode(lv_description_6_0, grammarAccess.getDecisionReviewDefAccess().getDescriptionSTRINGTerminalRuleCall_6_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getDecisionReviewDefRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"description",
                    		lv_description_6_0, 
                    		"STRING");
            	    

            }


            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2749:2: (otherlv_7= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )?
            int alt83=2;
            int LA83_0 = input.LA(1);

            if ( (LA83_0==70) ) {
                alt83=1;
            }
            switch (alt83) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2749:4: otherlv_7= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) )
                    {
                    otherlv_7=(Token)match(input,70,FOLLOW_70_in_ruleDecisionReviewDef5406); 

                        	newLeafNode(otherlv_7, grammarAccess.getDecisionReviewDefAccess().getRelatedToStateKeyword_7_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2753:1: ( ( ruleSTATE_NAME_REFERENCE ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2754:1: ( ruleSTATE_NAME_REFERENCE )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2754:1: ( ruleSTATE_NAME_REFERENCE )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2755:3: ruleSTATE_NAME_REFERENCE
                    {

                    			if (current==null) {
                    	            current = createModelElement(grammarAccess.getDecisionReviewDefRule());
                    	        }
                            
                     
                    	        newCompositeNode(grammarAccess.getDecisionReviewDefAccess().getRelatedToStateStateDefCrossReference_7_1_0()); 
                    	    
                    pushFollow(FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleDecisionReviewDef5429);
                    ruleSTATE_NAME_REFERENCE();

                    state._fsp--;

                     
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }


                    }
                    break;

            }

            otherlv_9=(Token)match(input,71,FOLLOW_71_in_ruleDecisionReviewDef5443); 

                	newLeafNode(otherlv_9, grammarAccess.getDecisionReviewDefAccess().getBlockingTypeKeyword_8());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2772:1: ( (lv_blockingType_10_0= ruleReviewBlockingType ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2773:1: (lv_blockingType_10_0= ruleReviewBlockingType )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2773:1: (lv_blockingType_10_0= ruleReviewBlockingType )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2774:3: lv_blockingType_10_0= ruleReviewBlockingType
            {
             
            	        newCompositeNode(grammarAccess.getDecisionReviewDefAccess().getBlockingTypeReviewBlockingTypeEnumRuleCall_9_0()); 
            	    
            pushFollow(FOLLOW_ruleReviewBlockingType_in_ruleDecisionReviewDef5464);
            lv_blockingType_10_0=ruleReviewBlockingType();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getDecisionReviewDefRule());
            	        }
                   		set(
                   			current, 
                   			"blockingType",
                    		lv_blockingType_10_0, 
                    		"ReviewBlockingType");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            otherlv_11=(Token)match(input,72,FOLLOW_72_in_ruleDecisionReviewDef5476); 

                	newLeafNode(otherlv_11, grammarAccess.getDecisionReviewDefAccess().getOnEventKeyword_10());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2794:1: ( (lv_stateEvent_12_0= ruleWorkflowEventType ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2795:1: (lv_stateEvent_12_0= ruleWorkflowEventType )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2795:1: (lv_stateEvent_12_0= ruleWorkflowEventType )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2796:3: lv_stateEvent_12_0= ruleWorkflowEventType
            {
             
            	        newCompositeNode(grammarAccess.getDecisionReviewDefAccess().getStateEventWorkflowEventTypeEnumRuleCall_11_0()); 
            	    
            pushFollow(FOLLOW_ruleWorkflowEventType_in_ruleDecisionReviewDef5497);
            lv_stateEvent_12_0=ruleWorkflowEventType();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getDecisionReviewDefRule());
            	        }
                   		set(
                   			current, 
                   			"stateEvent",
                    		lv_stateEvent_12_0, 
                    		"WorkflowEventType");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2812:2: (otherlv_13= 'assignee' ( (lv_assigneeRefs_14_0= ruleUserRef ) ) )*
            loop84:
            do {
                int alt84=2;
                int LA84_0 = input.LA(1);

                if ( (LA84_0==73) ) {
                    alt84=1;
                }


                switch (alt84) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2812:4: otherlv_13= 'assignee' ( (lv_assigneeRefs_14_0= ruleUserRef ) )
            	    {
            	    otherlv_13=(Token)match(input,73,FOLLOW_73_in_ruleDecisionReviewDef5510); 

            	        	newLeafNode(otherlv_13, grammarAccess.getDecisionReviewDefAccess().getAssigneeKeyword_12_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2816:1: ( (lv_assigneeRefs_14_0= ruleUserRef ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2817:1: (lv_assigneeRefs_14_0= ruleUserRef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2817:1: (lv_assigneeRefs_14_0= ruleUserRef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2818:3: lv_assigneeRefs_14_0= ruleUserRef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getDecisionReviewDefAccess().getAssigneeRefsUserRefParserRuleCall_12_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleUserRef_in_ruleDecisionReviewDef5531);
            	    lv_assigneeRefs_14_0=ruleUserRef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getDecisionReviewDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"assigneeRefs",
            	            		lv_assigneeRefs_14_0, 
            	            		"UserRef");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop84;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2834:4: (otherlv_15= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_16_0= ruleBooleanDef ) ) )?
            int alt85=2;
            int LA85_0 = input.LA(1);

            if ( (LA85_0==74) ) {
                alt85=1;
            }
            switch (alt85) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2834:6: otherlv_15= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_16_0= ruleBooleanDef ) )
                    {
                    otherlv_15=(Token)match(input,74,FOLLOW_74_in_ruleDecisionReviewDef5546); 

                        	newLeafNode(otherlv_15, grammarAccess.getDecisionReviewDefAccess().getAutoTransitionToDecisionKeyword_13_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2838:1: ( (lv_autoTransitionToDecision_16_0= ruleBooleanDef ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2839:1: (lv_autoTransitionToDecision_16_0= ruleBooleanDef )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2839:1: (lv_autoTransitionToDecision_16_0= ruleBooleanDef )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2840:3: lv_autoTransitionToDecision_16_0= ruleBooleanDef
                    {
                     
                    	        newCompositeNode(grammarAccess.getDecisionReviewDefAccess().getAutoTransitionToDecisionBooleanDefEnumRuleCall_13_1_0()); 
                    	    
                    pushFollow(FOLLOW_ruleBooleanDef_in_ruleDecisionReviewDef5567);
                    lv_autoTransitionToDecision_16_0=ruleBooleanDef();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getDecisionReviewDefRule());
                    	        }
                           		set(
                           			current, 
                           			"autoTransitionToDecision",
                            		lv_autoTransitionToDecision_16_0, 
                            		"BooleanDef");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2856:4: ( (lv_options_17_0= ruleDecisionReviewOpt ) )+
            int cnt86=0;
            loop86:
            do {
                int alt86=2;
                int LA86_0 = input.LA(1);

                if ( (LA86_0==54) ) {
                    alt86=1;
                }


                switch (alt86) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2857:1: (lv_options_17_0= ruleDecisionReviewOpt )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2857:1: (lv_options_17_0= ruleDecisionReviewOpt )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2858:3: lv_options_17_0= ruleDecisionReviewOpt
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getDecisionReviewDefAccess().getOptionsDecisionReviewOptParserRuleCall_14_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleDecisionReviewOpt_in_ruleDecisionReviewDef5590);
            	    lv_options_17_0=ruleDecisionReviewOpt();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getDecisionReviewDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"options",
            	            		lv_options_17_0, 
            	            		"DecisionReviewOpt");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt86 >= 1 ) break loop86;
                        EarlyExitException eee =
                            new EarlyExitException(86, input);
                        throw eee;
                }
                cnt86++;
            } while (true);

            otherlv_18=(Token)match(input,21,FOLLOW_21_in_ruleDecisionReviewDef5603); 

                	newLeafNode(otherlv_18, grammarAccess.getDecisionReviewDefAccess().getRightCurlyBracketKeyword_15());
                

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleDecisionReviewDef"


    // $ANTLR start "entryRuleDECISION_REVIEW_OPT_REF"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2886:1: entryRuleDECISION_REVIEW_OPT_REF returns [String current=null] : iv_ruleDECISION_REVIEW_OPT_REF= ruleDECISION_REVIEW_OPT_REF EOF ;
    public final String entryRuleDECISION_REVIEW_OPT_REF() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleDECISION_REVIEW_OPT_REF = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2887:2: (iv_ruleDECISION_REVIEW_OPT_REF= ruleDECISION_REVIEW_OPT_REF EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2888:2: iv_ruleDECISION_REVIEW_OPT_REF= ruleDECISION_REVIEW_OPT_REF EOF
            {
             newCompositeNode(grammarAccess.getDECISION_REVIEW_OPT_REFRule()); 
            pushFollow(FOLLOW_ruleDECISION_REVIEW_OPT_REF_in_entryRuleDECISION_REVIEW_OPT_REF5640);
            iv_ruleDECISION_REVIEW_OPT_REF=ruleDECISION_REVIEW_OPT_REF();

            state._fsp--;

             current =iv_ruleDECISION_REVIEW_OPT_REF.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleDECISION_REVIEW_OPT_REF5651); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleDECISION_REVIEW_OPT_REF"


    // $ANTLR start "ruleDECISION_REVIEW_OPT_REF"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2895:1: ruleDECISION_REVIEW_OPT_REF returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleDECISION_REVIEW_OPT_REF() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2898:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2899:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleDECISION_REVIEW_OPT_REF5690); 

            		current.merge(this_STRING_0);
                
             
                newLeafNode(this_STRING_0, grammarAccess.getDECISION_REVIEW_OPT_REFAccess().getSTRINGTerminalRuleCall()); 
                

            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleDECISION_REVIEW_OPT_REF"


    // $ANTLR start "entryRuleDecisionReviewOpt"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2914:1: entryRuleDecisionReviewOpt returns [EObject current=null] : iv_ruleDecisionReviewOpt= ruleDecisionReviewOpt EOF ;
    public final EObject entryRuleDecisionReviewOpt() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDecisionReviewOpt = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2915:2: (iv_ruleDecisionReviewOpt= ruleDecisionReviewOpt EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2916:2: iv_ruleDecisionReviewOpt= ruleDecisionReviewOpt EOF
            {
             newCompositeNode(grammarAccess.getDecisionReviewOptRule()); 
            pushFollow(FOLLOW_ruleDecisionReviewOpt_in_entryRuleDecisionReviewOpt5734);
            iv_ruleDecisionReviewOpt=ruleDecisionReviewOpt();

            state._fsp--;

             current =iv_ruleDecisionReviewOpt; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleDecisionReviewOpt5744); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleDecisionReviewOpt"


    // $ANTLR start "ruleDecisionReviewOpt"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2923:1: ruleDecisionReviewOpt returns [EObject current=null] : (otherlv_0= 'option' ( (lv_name_1_0= ruleDECISION_REVIEW_OPT_REF ) ) ( (lv_followup_2_0= ruleFollowupRef ) )? ) ;
    public final EObject ruleDecisionReviewOpt() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        AntlrDatatypeRuleToken lv_name_1_0 = null;

        EObject lv_followup_2_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2926:28: ( (otherlv_0= 'option' ( (lv_name_1_0= ruleDECISION_REVIEW_OPT_REF ) ) ( (lv_followup_2_0= ruleFollowupRef ) )? ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2927:1: (otherlv_0= 'option' ( (lv_name_1_0= ruleDECISION_REVIEW_OPT_REF ) ) ( (lv_followup_2_0= ruleFollowupRef ) )? )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2927:1: (otherlv_0= 'option' ( (lv_name_1_0= ruleDECISION_REVIEW_OPT_REF ) ) ( (lv_followup_2_0= ruleFollowupRef ) )? )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2927:3: otherlv_0= 'option' ( (lv_name_1_0= ruleDECISION_REVIEW_OPT_REF ) ) ( (lv_followup_2_0= ruleFollowupRef ) )?
            {
            otherlv_0=(Token)match(input,54,FOLLOW_54_in_ruleDecisionReviewOpt5781); 

                	newLeafNode(otherlv_0, grammarAccess.getDecisionReviewOptAccess().getOptionKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2931:1: ( (lv_name_1_0= ruleDECISION_REVIEW_OPT_REF ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2932:1: (lv_name_1_0= ruleDECISION_REVIEW_OPT_REF )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2932:1: (lv_name_1_0= ruleDECISION_REVIEW_OPT_REF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2933:3: lv_name_1_0= ruleDECISION_REVIEW_OPT_REF
            {
             
            	        newCompositeNode(grammarAccess.getDecisionReviewOptAccess().getNameDECISION_REVIEW_OPT_REFParserRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_ruleDECISION_REVIEW_OPT_REF_in_ruleDecisionReviewOpt5802);
            lv_name_1_0=ruleDECISION_REVIEW_OPT_REF();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getDecisionReviewOptRule());
            	        }
                   		set(
                   			current, 
                   			"name",
                    		lv_name_1_0, 
                    		"DECISION_REVIEW_OPT_REF");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2949:2: ( (lv_followup_2_0= ruleFollowupRef ) )?
            int alt87=2;
            int LA87_0 = input.LA(1);

            if ( (LA87_0==78) ) {
                alt87=1;
            }
            switch (alt87) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2950:1: (lv_followup_2_0= ruleFollowupRef )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2950:1: (lv_followup_2_0= ruleFollowupRef )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2951:3: lv_followup_2_0= ruleFollowupRef
                    {
                     
                    	        newCompositeNode(grammarAccess.getDecisionReviewOptAccess().getFollowupFollowupRefParserRuleCall_2_0()); 
                    	    
                    pushFollow(FOLLOW_ruleFollowupRef_in_ruleDecisionReviewOpt5823);
                    lv_followup_2_0=ruleFollowupRef();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getDecisionReviewOptRule());
                    	        }
                           		set(
                           			current, 
                           			"followup",
                            		lv_followup_2_0, 
                            		"FollowupRef");
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
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleDecisionReviewOpt"


    // $ANTLR start "entryRulePeerReviewRef"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2975:1: entryRulePeerReviewRef returns [EObject current=null] : iv_rulePeerReviewRef= rulePeerReviewRef EOF ;
    public final EObject entryRulePeerReviewRef() throws RecognitionException {
        EObject current = null;

        EObject iv_rulePeerReviewRef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2976:2: (iv_rulePeerReviewRef= rulePeerReviewRef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2977:2: iv_rulePeerReviewRef= rulePeerReviewRef EOF
            {
             newCompositeNode(grammarAccess.getPeerReviewRefRule()); 
            pushFollow(FOLLOW_rulePeerReviewRef_in_entryRulePeerReviewRef5860);
            iv_rulePeerReviewRef=rulePeerReviewRef();

            state._fsp--;

             current =iv_rulePeerReviewRef; 
            match(input,EOF,FOLLOW_EOF_in_entryRulePeerReviewRef5870); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRulePeerReviewRef"


    // $ANTLR start "rulePeerReviewRef"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2984:1: rulePeerReviewRef returns [EObject current=null] : (otherlv_0= 'peerReview' ( ( rulePEER_REVIEW_NAME_REFERENCE ) ) ) ;
    public final EObject rulePeerReviewRef() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2987:28: ( (otherlv_0= 'peerReview' ( ( rulePEER_REVIEW_NAME_REFERENCE ) ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2988:1: (otherlv_0= 'peerReview' ( ( rulePEER_REVIEW_NAME_REFERENCE ) ) )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2988:1: (otherlv_0= 'peerReview' ( ( rulePEER_REVIEW_NAME_REFERENCE ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2988:3: otherlv_0= 'peerReview' ( ( rulePEER_REVIEW_NAME_REFERENCE ) )
            {
            otherlv_0=(Token)match(input,75,FOLLOW_75_in_rulePeerReviewRef5907); 

                	newLeafNode(otherlv_0, grammarAccess.getPeerReviewRefAccess().getPeerReviewKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2992:1: ( ( rulePEER_REVIEW_NAME_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2993:1: ( rulePEER_REVIEW_NAME_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2993:1: ( rulePEER_REVIEW_NAME_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2994:3: rulePEER_REVIEW_NAME_REFERENCE
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getPeerReviewRefRule());
            	        }
                    
             
            	        newCompositeNode(grammarAccess.getPeerReviewRefAccess().getPeerReviewPeerReviewDefCrossReference_1_0()); 
            	    
            pushFollow(FOLLOW_rulePEER_REVIEW_NAME_REFERENCE_in_rulePeerReviewRef5930);
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
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "rulePeerReviewRef"


    // $ANTLR start "entryRulePeerReviewDef"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3015:1: entryRulePeerReviewDef returns [EObject current=null] : iv_rulePeerReviewDef= rulePeerReviewDef EOF ;
    public final EObject entryRulePeerReviewDef() throws RecognitionException {
        EObject current = null;

        EObject iv_rulePeerReviewDef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3016:2: (iv_rulePeerReviewDef= rulePeerReviewDef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3017:2: iv_rulePeerReviewDef= rulePeerReviewDef EOF
            {
             newCompositeNode(grammarAccess.getPeerReviewDefRule()); 
            pushFollow(FOLLOW_rulePeerReviewDef_in_entryRulePeerReviewDef5966);
            iv_rulePeerReviewDef=rulePeerReviewDef();

            state._fsp--;

             current =iv_rulePeerReviewDef; 
            match(input,EOF,FOLLOW_EOF_in_entryRulePeerReviewDef5976); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRulePeerReviewDef"


    // $ANTLR start "rulePeerReviewDef"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3024:1: rulePeerReviewDef returns [EObject current=null] : (otherlv_0= 'peerReviewDefinition' ( (lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) )? otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'location' ( (lv_location_8_0= RULE_STRING ) ) )? (otherlv_9= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_11= 'blockingType' ( (lv_blockingType_12_0= ruleReviewBlockingType ) ) otherlv_13= 'onEvent' ( (lv_stateEvent_14_0= ruleWorkflowEventType ) ) (otherlv_15= 'assignee' ( (lv_assigneeRefs_16_0= ruleUserRef ) ) )* otherlv_17= '}' ) ;
    public final EObject rulePeerReviewDef() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_2=null;
        Token otherlv_3=null;
        Token lv_title_4_0=null;
        Token otherlv_5=null;
        Token lv_description_6_0=null;
        Token otherlv_7=null;
        Token lv_location_8_0=null;
        Token otherlv_9=null;
        Token otherlv_11=null;
        Token otherlv_13=null;
        Token otherlv_15=null;
        Token otherlv_17=null;
        AntlrDatatypeRuleToken lv_name_1_0 = null;

        Enumerator lv_blockingType_12_0 = null;

        Enumerator lv_stateEvent_14_0 = null;

        EObject lv_assigneeRefs_16_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3027:28: ( (otherlv_0= 'peerReviewDefinition' ( (lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) )? otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'location' ( (lv_location_8_0= RULE_STRING ) ) )? (otherlv_9= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_11= 'blockingType' ( (lv_blockingType_12_0= ruleReviewBlockingType ) ) otherlv_13= 'onEvent' ( (lv_stateEvent_14_0= ruleWorkflowEventType ) ) (otherlv_15= 'assignee' ( (lv_assigneeRefs_16_0= ruleUserRef ) ) )* otherlv_17= '}' ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3028:1: (otherlv_0= 'peerReviewDefinition' ( (lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) )? otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'location' ( (lv_location_8_0= RULE_STRING ) ) )? (otherlv_9= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_11= 'blockingType' ( (lv_blockingType_12_0= ruleReviewBlockingType ) ) otherlv_13= 'onEvent' ( (lv_stateEvent_14_0= ruleWorkflowEventType ) ) (otherlv_15= 'assignee' ( (lv_assigneeRefs_16_0= ruleUserRef ) ) )* otherlv_17= '}' )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3028:1: (otherlv_0= 'peerReviewDefinition' ( (lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) )? otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'location' ( (lv_location_8_0= RULE_STRING ) ) )? (otherlv_9= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_11= 'blockingType' ( (lv_blockingType_12_0= ruleReviewBlockingType ) ) otherlv_13= 'onEvent' ( (lv_stateEvent_14_0= ruleWorkflowEventType ) ) (otherlv_15= 'assignee' ( (lv_assigneeRefs_16_0= ruleUserRef ) ) )* otherlv_17= '}' )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3028:3: otherlv_0= 'peerReviewDefinition' ( (lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) )? otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'location' ( (lv_location_8_0= RULE_STRING ) ) )? (otherlv_9= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_11= 'blockingType' ( (lv_blockingType_12_0= ruleReviewBlockingType ) ) otherlv_13= 'onEvent' ( (lv_stateEvent_14_0= ruleWorkflowEventType ) ) (otherlv_15= 'assignee' ( (lv_assigneeRefs_16_0= ruleUserRef ) ) )* otherlv_17= '}'
            {
            otherlv_0=(Token)match(input,76,FOLLOW_76_in_rulePeerReviewDef6013); 

                	newLeafNode(otherlv_0, grammarAccess.getPeerReviewDefAccess().getPeerReviewDefinitionKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3032:1: ( (lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3033:1: (lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3033:1: (lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3034:3: lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE
            {
             
            	        newCompositeNode(grammarAccess.getPeerReviewDefAccess().getNamePEER_REVIEW_NAME_REFERENCEParserRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_rulePEER_REVIEW_NAME_REFERENCE_in_rulePeerReviewDef6034);
            lv_name_1_0=rulePEER_REVIEW_NAME_REFERENCE();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getPeerReviewDefRule());
            	        }
                   		set(
                   			current, 
                   			"name",
                    		lv_name_1_0, 
                    		"PEER_REVIEW_NAME_REFERENCE");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            otherlv_2=(Token)match(input,16,FOLLOW_16_in_rulePeerReviewDef6046); 

                	newLeafNode(otherlv_2, grammarAccess.getPeerReviewDefAccess().getLeftCurlyBracketKeyword_2());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3054:1: (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) )?
            int alt88=2;
            int LA88_0 = input.LA(1);

            if ( (LA88_0==69) ) {
                alt88=1;
            }
            switch (alt88) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3054:3: otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) )
                    {
                    otherlv_3=(Token)match(input,69,FOLLOW_69_in_rulePeerReviewDef6059); 

                        	newLeafNode(otherlv_3, grammarAccess.getPeerReviewDefAccess().getTitleKeyword_3_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3058:1: ( (lv_title_4_0= RULE_STRING ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3059:1: (lv_title_4_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3059:1: (lv_title_4_0= RULE_STRING )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3060:3: lv_title_4_0= RULE_STRING
                    {
                    lv_title_4_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rulePeerReviewDef6076); 

                    			newLeafNode(lv_title_4_0, grammarAccess.getPeerReviewDefAccess().getTitleSTRINGTerminalRuleCall_3_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getPeerReviewDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"title",
                            		lv_title_4_0, 
                            		"STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            otherlv_5=(Token)match(input,50,FOLLOW_50_in_rulePeerReviewDef6095); 

                	newLeafNode(otherlv_5, grammarAccess.getPeerReviewDefAccess().getDescriptionKeyword_4());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3080:1: ( (lv_description_6_0= RULE_STRING ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3081:1: (lv_description_6_0= RULE_STRING )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3081:1: (lv_description_6_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3082:3: lv_description_6_0= RULE_STRING
            {
            lv_description_6_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rulePeerReviewDef6112); 

            			newLeafNode(lv_description_6_0, grammarAccess.getPeerReviewDefAccess().getDescriptionSTRINGTerminalRuleCall_5_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getPeerReviewDefRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"description",
                    		lv_description_6_0, 
                    		"STRING");
            	    

            }


            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3098:2: (otherlv_7= 'location' ( (lv_location_8_0= RULE_STRING ) ) )?
            int alt89=2;
            int LA89_0 = input.LA(1);

            if ( (LA89_0==77) ) {
                alt89=1;
            }
            switch (alt89) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3098:4: otherlv_7= 'location' ( (lv_location_8_0= RULE_STRING ) )
                    {
                    otherlv_7=(Token)match(input,77,FOLLOW_77_in_rulePeerReviewDef6130); 

                        	newLeafNode(otherlv_7, grammarAccess.getPeerReviewDefAccess().getLocationKeyword_6_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3102:1: ( (lv_location_8_0= RULE_STRING ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3103:1: (lv_location_8_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3103:1: (lv_location_8_0= RULE_STRING )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3104:3: lv_location_8_0= RULE_STRING
                    {
                    lv_location_8_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rulePeerReviewDef6147); 

                    			newLeafNode(lv_location_8_0, grammarAccess.getPeerReviewDefAccess().getLocationSTRINGTerminalRuleCall_6_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getPeerReviewDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"location",
                            		lv_location_8_0, 
                            		"STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3120:4: (otherlv_9= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )?
            int alt90=2;
            int LA90_0 = input.LA(1);

            if ( (LA90_0==70) ) {
                alt90=1;
            }
            switch (alt90) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3120:6: otherlv_9= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) )
                    {
                    otherlv_9=(Token)match(input,70,FOLLOW_70_in_rulePeerReviewDef6167); 

                        	newLeafNode(otherlv_9, grammarAccess.getPeerReviewDefAccess().getRelatedToStateKeyword_7_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3124:1: ( ( ruleSTATE_NAME_REFERENCE ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3125:1: ( ruleSTATE_NAME_REFERENCE )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3125:1: ( ruleSTATE_NAME_REFERENCE )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3126:3: ruleSTATE_NAME_REFERENCE
                    {

                    			if (current==null) {
                    	            current = createModelElement(grammarAccess.getPeerReviewDefRule());
                    	        }
                            
                     
                    	        newCompositeNode(grammarAccess.getPeerReviewDefAccess().getRelatedToStateStateDefCrossReference_7_1_0()); 
                    	    
                    pushFollow(FOLLOW_ruleSTATE_NAME_REFERENCE_in_rulePeerReviewDef6190);
                    ruleSTATE_NAME_REFERENCE();

                    state._fsp--;

                     
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }


                    }
                    break;

            }

            otherlv_11=(Token)match(input,71,FOLLOW_71_in_rulePeerReviewDef6204); 

                	newLeafNode(otherlv_11, grammarAccess.getPeerReviewDefAccess().getBlockingTypeKeyword_8());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3143:1: ( (lv_blockingType_12_0= ruleReviewBlockingType ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3144:1: (lv_blockingType_12_0= ruleReviewBlockingType )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3144:1: (lv_blockingType_12_0= ruleReviewBlockingType )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3145:3: lv_blockingType_12_0= ruleReviewBlockingType
            {
             
            	        newCompositeNode(grammarAccess.getPeerReviewDefAccess().getBlockingTypeReviewBlockingTypeEnumRuleCall_9_0()); 
            	    
            pushFollow(FOLLOW_ruleReviewBlockingType_in_rulePeerReviewDef6225);
            lv_blockingType_12_0=ruleReviewBlockingType();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getPeerReviewDefRule());
            	        }
                   		set(
                   			current, 
                   			"blockingType",
                    		lv_blockingType_12_0, 
                    		"ReviewBlockingType");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            otherlv_13=(Token)match(input,72,FOLLOW_72_in_rulePeerReviewDef6237); 

                	newLeafNode(otherlv_13, grammarAccess.getPeerReviewDefAccess().getOnEventKeyword_10());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3165:1: ( (lv_stateEvent_14_0= ruleWorkflowEventType ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3166:1: (lv_stateEvent_14_0= ruleWorkflowEventType )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3166:1: (lv_stateEvent_14_0= ruleWorkflowEventType )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3167:3: lv_stateEvent_14_0= ruleWorkflowEventType
            {
             
            	        newCompositeNode(grammarAccess.getPeerReviewDefAccess().getStateEventWorkflowEventTypeEnumRuleCall_11_0()); 
            	    
            pushFollow(FOLLOW_ruleWorkflowEventType_in_rulePeerReviewDef6258);
            lv_stateEvent_14_0=ruleWorkflowEventType();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getPeerReviewDefRule());
            	        }
                   		set(
                   			current, 
                   			"stateEvent",
                    		lv_stateEvent_14_0, 
                    		"WorkflowEventType");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3183:2: (otherlv_15= 'assignee' ( (lv_assigneeRefs_16_0= ruleUserRef ) ) )*
            loop91:
            do {
                int alt91=2;
                int LA91_0 = input.LA(1);

                if ( (LA91_0==73) ) {
                    alt91=1;
                }


                switch (alt91) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3183:4: otherlv_15= 'assignee' ( (lv_assigneeRefs_16_0= ruleUserRef ) )
            	    {
            	    otherlv_15=(Token)match(input,73,FOLLOW_73_in_rulePeerReviewDef6271); 

            	        	newLeafNode(otherlv_15, grammarAccess.getPeerReviewDefAccess().getAssigneeKeyword_12_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3187:1: ( (lv_assigneeRefs_16_0= ruleUserRef ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3188:1: (lv_assigneeRefs_16_0= ruleUserRef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3188:1: (lv_assigneeRefs_16_0= ruleUserRef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3189:3: lv_assigneeRefs_16_0= ruleUserRef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getPeerReviewDefAccess().getAssigneeRefsUserRefParserRuleCall_12_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleUserRef_in_rulePeerReviewDef6292);
            	    lv_assigneeRefs_16_0=ruleUserRef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getPeerReviewDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"assigneeRefs",
            	            		lv_assigneeRefs_16_0, 
            	            		"UserRef");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop91;
                }
            } while (true);

            otherlv_17=(Token)match(input,21,FOLLOW_21_in_rulePeerReviewDef6306); 

                	newLeafNode(otherlv_17, grammarAccess.getPeerReviewDefAccess().getRightCurlyBracketKeyword_13());
                

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "rulePeerReviewDef"


    // $ANTLR start "entryRuleFollowupRef"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3217:1: entryRuleFollowupRef returns [EObject current=null] : iv_ruleFollowupRef= ruleFollowupRef EOF ;
    public final EObject entryRuleFollowupRef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleFollowupRef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3218:2: (iv_ruleFollowupRef= ruleFollowupRef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3219:2: iv_ruleFollowupRef= ruleFollowupRef EOF
            {
             newCompositeNode(grammarAccess.getFollowupRefRule()); 
            pushFollow(FOLLOW_ruleFollowupRef_in_entryRuleFollowupRef6342);
            iv_ruleFollowupRef=ruleFollowupRef();

            state._fsp--;

             current =iv_ruleFollowupRef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleFollowupRef6352); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleFollowupRef"


    // $ANTLR start "ruleFollowupRef"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3226:1: ruleFollowupRef returns [EObject current=null] : (otherlv_0= 'followup by' (otherlv_1= 'assignee' ( (lv_assigneeRefs_2_0= ruleUserRef ) ) )+ ) ;
    public final EObject ruleFollowupRef() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;
        EObject lv_assigneeRefs_2_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3229:28: ( (otherlv_0= 'followup by' (otherlv_1= 'assignee' ( (lv_assigneeRefs_2_0= ruleUserRef ) ) )+ ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3230:1: (otherlv_0= 'followup by' (otherlv_1= 'assignee' ( (lv_assigneeRefs_2_0= ruleUserRef ) ) )+ )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3230:1: (otherlv_0= 'followup by' (otherlv_1= 'assignee' ( (lv_assigneeRefs_2_0= ruleUserRef ) ) )+ )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3230:3: otherlv_0= 'followup by' (otherlv_1= 'assignee' ( (lv_assigneeRefs_2_0= ruleUserRef ) ) )+
            {
            otherlv_0=(Token)match(input,78,FOLLOW_78_in_ruleFollowupRef6389); 

                	newLeafNode(otherlv_0, grammarAccess.getFollowupRefAccess().getFollowupByKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3234:1: (otherlv_1= 'assignee' ( (lv_assigneeRefs_2_0= ruleUserRef ) ) )+
            int cnt92=0;
            loop92:
            do {
                int alt92=2;
                int LA92_0 = input.LA(1);

                if ( (LA92_0==73) ) {
                    alt92=1;
                }


                switch (alt92) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3234:3: otherlv_1= 'assignee' ( (lv_assigneeRefs_2_0= ruleUserRef ) )
            	    {
            	    otherlv_1=(Token)match(input,73,FOLLOW_73_in_ruleFollowupRef6402); 

            	        	newLeafNode(otherlv_1, grammarAccess.getFollowupRefAccess().getAssigneeKeyword_1_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3238:1: ( (lv_assigneeRefs_2_0= ruleUserRef ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3239:1: (lv_assigneeRefs_2_0= ruleUserRef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3239:1: (lv_assigneeRefs_2_0= ruleUserRef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3240:3: lv_assigneeRefs_2_0= ruleUserRef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getFollowupRefAccess().getAssigneeRefsUserRefParserRuleCall_1_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleUserRef_in_ruleFollowupRef6423);
            	    lv_assigneeRefs_2_0=ruleUserRef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getFollowupRefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"assigneeRefs",
            	            		lv_assigneeRefs_2_0, 
            	            		"UserRef");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt92 >= 1 ) break loop92;
                        EarlyExitException eee =
                            new EarlyExitException(92, input);
                        throw eee;
                }
                cnt92++;
            } while (true);


            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleFollowupRef"


    // $ANTLR start "entryRuleUserRef"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3264:1: entryRuleUserRef returns [EObject current=null] : iv_ruleUserRef= ruleUserRef EOF ;
    public final EObject entryRuleUserRef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleUserRef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3265:2: (iv_ruleUserRef= ruleUserRef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3266:2: iv_ruleUserRef= ruleUserRef EOF
            {
             newCompositeNode(grammarAccess.getUserRefRule()); 
            pushFollow(FOLLOW_ruleUserRef_in_entryRuleUserRef6461);
            iv_ruleUserRef=ruleUserRef();

            state._fsp--;

             current =iv_ruleUserRef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleUserRef6471); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleUserRef"


    // $ANTLR start "ruleUserRef"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3273:1: ruleUserRef returns [EObject current=null] : (this_UserByUserId_0= ruleUserByUserId | this_UserByName_1= ruleUserByName ) ;
    public final EObject ruleUserRef() throws RecognitionException {
        EObject current = null;

        EObject this_UserByUserId_0 = null;

        EObject this_UserByName_1 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3276:28: ( (this_UserByUserId_0= ruleUserByUserId | this_UserByName_1= ruleUserByName ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3277:1: (this_UserByUserId_0= ruleUserByUserId | this_UserByName_1= ruleUserByName )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3277:1: (this_UserByUserId_0= ruleUserByUserId | this_UserByName_1= ruleUserByName )
            int alt93=2;
            int LA93_0 = input.LA(1);

            if ( (LA93_0==18) ) {
                alt93=1;
            }
            else if ( (LA93_0==79) ) {
                alt93=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 93, 0, input);

                throw nvae;
            }
            switch (alt93) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3278:5: this_UserByUserId_0= ruleUserByUserId
                    {
                     
                            newCompositeNode(grammarAccess.getUserRefAccess().getUserByUserIdParserRuleCall_0()); 
                        
                    pushFollow(FOLLOW_ruleUserByUserId_in_ruleUserRef6518);
                    this_UserByUserId_0=ruleUserByUserId();

                    state._fsp--;

                     
                            current = this_UserByUserId_0; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3288:5: this_UserByName_1= ruleUserByName
                    {
                     
                            newCompositeNode(grammarAccess.getUserRefAccess().getUserByNameParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_ruleUserByName_in_ruleUserRef6545);
                    this_UserByName_1=ruleUserByName();

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
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleUserRef"


    // $ANTLR start "entryRuleUserByUserId"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3304:1: entryRuleUserByUserId returns [EObject current=null] : iv_ruleUserByUserId= ruleUserByUserId EOF ;
    public final EObject entryRuleUserByUserId() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleUserByUserId = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3305:2: (iv_ruleUserByUserId= ruleUserByUserId EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3306:2: iv_ruleUserByUserId= ruleUserByUserId EOF
            {
             newCompositeNode(grammarAccess.getUserByUserIdRule()); 
            pushFollow(FOLLOW_ruleUserByUserId_in_entryRuleUserByUserId6580);
            iv_ruleUserByUserId=ruleUserByUserId();

            state._fsp--;

             current =iv_ruleUserByUserId; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleUserByUserId6590); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleUserByUserId"


    // $ANTLR start "ruleUserByUserId"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3313:1: ruleUserByUserId returns [EObject current=null] : (otherlv_0= 'userId' ( (lv_userId_1_0= RULE_STRING ) ) ) ;
    public final EObject ruleUserByUserId() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_userId_1_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3316:28: ( (otherlv_0= 'userId' ( (lv_userId_1_0= RULE_STRING ) ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3317:1: (otherlv_0= 'userId' ( (lv_userId_1_0= RULE_STRING ) ) )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3317:1: (otherlv_0= 'userId' ( (lv_userId_1_0= RULE_STRING ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3317:3: otherlv_0= 'userId' ( (lv_userId_1_0= RULE_STRING ) )
            {
            otherlv_0=(Token)match(input,18,FOLLOW_18_in_ruleUserByUserId6627); 

                	newLeafNode(otherlv_0, grammarAccess.getUserByUserIdAccess().getUserIdKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3321:1: ( (lv_userId_1_0= RULE_STRING ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3322:1: (lv_userId_1_0= RULE_STRING )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3322:1: (lv_userId_1_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3323:3: lv_userId_1_0= RULE_STRING
            {
            lv_userId_1_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleUserByUserId6644); 

            			newLeafNode(lv_userId_1_0, grammarAccess.getUserByUserIdAccess().getUserIdSTRINGTerminalRuleCall_1_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getUserByUserIdRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"userId",
                    		lv_userId_1_0, 
                    		"STRING");
            	    

            }


            }


            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleUserByUserId"


    // $ANTLR start "entryRuleUserByName"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3347:1: entryRuleUserByName returns [EObject current=null] : iv_ruleUserByName= ruleUserByName EOF ;
    public final EObject entryRuleUserByName() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleUserByName = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3348:2: (iv_ruleUserByName= ruleUserByName EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3349:2: iv_ruleUserByName= ruleUserByName EOF
            {
             newCompositeNode(grammarAccess.getUserByNameRule()); 
            pushFollow(FOLLOW_ruleUserByName_in_entryRuleUserByName6685);
            iv_ruleUserByName=ruleUserByName();

            state._fsp--;

             current =iv_ruleUserByName; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleUserByName6695); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleUserByName"


    // $ANTLR start "ruleUserByName"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3356:1: ruleUserByName returns [EObject current=null] : (otherlv_0= 'named' ( (lv_userName_1_0= RULE_STRING ) ) ) ;
    public final EObject ruleUserByName() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_userName_1_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3359:28: ( (otherlv_0= 'named' ( (lv_userName_1_0= RULE_STRING ) ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3360:1: (otherlv_0= 'named' ( (lv_userName_1_0= RULE_STRING ) ) )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3360:1: (otherlv_0= 'named' ( (lv_userName_1_0= RULE_STRING ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3360:3: otherlv_0= 'named' ( (lv_userName_1_0= RULE_STRING ) )
            {
            otherlv_0=(Token)match(input,79,FOLLOW_79_in_ruleUserByName6732); 

                	newLeafNode(otherlv_0, grammarAccess.getUserByNameAccess().getNamedKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3364:1: ( (lv_userName_1_0= RULE_STRING ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3365:1: (lv_userName_1_0= RULE_STRING )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3365:1: (lv_userName_1_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3366:3: lv_userName_1_0= RULE_STRING
            {
            lv_userName_1_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleUserByName6749); 

            			newLeafNode(lv_userName_1_0, grammarAccess.getUserByNameAccess().getUserNameSTRINGTerminalRuleCall_1_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getUserByNameRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"userName",
                    		lv_userName_1_0, 
                    		"STRING");
            	    

            }


            }


            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleUserByName"


    // $ANTLR start "entryRuleDECISION_REVIEW_NAME_REFERENCE"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3390:1: entryRuleDECISION_REVIEW_NAME_REFERENCE returns [String current=null] : iv_ruleDECISION_REVIEW_NAME_REFERENCE= ruleDECISION_REVIEW_NAME_REFERENCE EOF ;
    public final String entryRuleDECISION_REVIEW_NAME_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleDECISION_REVIEW_NAME_REFERENCE = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3391:2: (iv_ruleDECISION_REVIEW_NAME_REFERENCE= ruleDECISION_REVIEW_NAME_REFERENCE EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3392:2: iv_ruleDECISION_REVIEW_NAME_REFERENCE= ruleDECISION_REVIEW_NAME_REFERENCE EOF
            {
             newCompositeNode(grammarAccess.getDECISION_REVIEW_NAME_REFERENCERule()); 
            pushFollow(FOLLOW_ruleDECISION_REVIEW_NAME_REFERENCE_in_entryRuleDECISION_REVIEW_NAME_REFERENCE6791);
            iv_ruleDECISION_REVIEW_NAME_REFERENCE=ruleDECISION_REVIEW_NAME_REFERENCE();

            state._fsp--;

             current =iv_ruleDECISION_REVIEW_NAME_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleDECISION_REVIEW_NAME_REFERENCE6802); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleDECISION_REVIEW_NAME_REFERENCE"


    // $ANTLR start "ruleDECISION_REVIEW_NAME_REFERENCE"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3399:1: ruleDECISION_REVIEW_NAME_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleDECISION_REVIEW_NAME_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3402:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3403:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleDECISION_REVIEW_NAME_REFERENCE6841); 

            		current.merge(this_STRING_0);
                
             
                newLeafNode(this_STRING_0, grammarAccess.getDECISION_REVIEW_NAME_REFERENCEAccess().getSTRINGTerminalRuleCall()); 
                

            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleDECISION_REVIEW_NAME_REFERENCE"


    // $ANTLR start "entryRulePEER_REVIEW_NAME_REFERENCE"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3418:1: entryRulePEER_REVIEW_NAME_REFERENCE returns [String current=null] : iv_rulePEER_REVIEW_NAME_REFERENCE= rulePEER_REVIEW_NAME_REFERENCE EOF ;
    public final String entryRulePEER_REVIEW_NAME_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_rulePEER_REVIEW_NAME_REFERENCE = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3419:2: (iv_rulePEER_REVIEW_NAME_REFERENCE= rulePEER_REVIEW_NAME_REFERENCE EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3420:2: iv_rulePEER_REVIEW_NAME_REFERENCE= rulePEER_REVIEW_NAME_REFERENCE EOF
            {
             newCompositeNode(grammarAccess.getPEER_REVIEW_NAME_REFERENCERule()); 
            pushFollow(FOLLOW_rulePEER_REVIEW_NAME_REFERENCE_in_entryRulePEER_REVIEW_NAME_REFERENCE6886);
            iv_rulePEER_REVIEW_NAME_REFERENCE=rulePEER_REVIEW_NAME_REFERENCE();

            state._fsp--;

             current =iv_rulePEER_REVIEW_NAME_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRulePEER_REVIEW_NAME_REFERENCE6897); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRulePEER_REVIEW_NAME_REFERENCE"


    // $ANTLR start "rulePEER_REVIEW_NAME_REFERENCE"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3427:1: rulePEER_REVIEW_NAME_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken rulePEER_REVIEW_NAME_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3430:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3431:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rulePEER_REVIEW_NAME_REFERENCE6936); 

            		current.merge(this_STRING_0);
                
             
                newLeafNode(this_STRING_0, grammarAccess.getPEER_REVIEW_NAME_REFERENCEAccess().getSTRINGTerminalRuleCall()); 
                

            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "rulePEER_REVIEW_NAME_REFERENCE"


    // $ANTLR start "entryRuleSTATE_NAME_REFERENCE"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3446:1: entryRuleSTATE_NAME_REFERENCE returns [String current=null] : iv_ruleSTATE_NAME_REFERENCE= ruleSTATE_NAME_REFERENCE EOF ;
    public final String entryRuleSTATE_NAME_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleSTATE_NAME_REFERENCE = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3447:2: (iv_ruleSTATE_NAME_REFERENCE= ruleSTATE_NAME_REFERENCE EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3448:2: iv_ruleSTATE_NAME_REFERENCE= ruleSTATE_NAME_REFERENCE EOF
            {
             newCompositeNode(grammarAccess.getSTATE_NAME_REFERENCERule()); 
            pushFollow(FOLLOW_ruleSTATE_NAME_REFERENCE_in_entryRuleSTATE_NAME_REFERENCE6981);
            iv_ruleSTATE_NAME_REFERENCE=ruleSTATE_NAME_REFERENCE();

            state._fsp--;

             current =iv_ruleSTATE_NAME_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleSTATE_NAME_REFERENCE6992); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleSTATE_NAME_REFERENCE"


    // $ANTLR start "ruleSTATE_NAME_REFERENCE"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3455:1: ruleSTATE_NAME_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleSTATE_NAME_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3458:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3459:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleSTATE_NAME_REFERENCE7031); 

            		current.merge(this_STRING_0);
                
             
                newLeafNode(this_STRING_0, grammarAccess.getSTATE_NAME_REFERENCEAccess().getSTRINGTerminalRuleCall()); 
                

            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleSTATE_NAME_REFERENCE"


    // $ANTLR start "entryRuleWIDGET_NAME_REFERENCE"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3474:1: entryRuleWIDGET_NAME_REFERENCE returns [String current=null] : iv_ruleWIDGET_NAME_REFERENCE= ruleWIDGET_NAME_REFERENCE EOF ;
    public final String entryRuleWIDGET_NAME_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleWIDGET_NAME_REFERENCE = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3475:2: (iv_ruleWIDGET_NAME_REFERENCE= ruleWIDGET_NAME_REFERENCE EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3476:2: iv_ruleWIDGET_NAME_REFERENCE= ruleWIDGET_NAME_REFERENCE EOF
            {
             newCompositeNode(grammarAccess.getWIDGET_NAME_REFERENCERule()); 
            pushFollow(FOLLOW_ruleWIDGET_NAME_REFERENCE_in_entryRuleWIDGET_NAME_REFERENCE7076);
            iv_ruleWIDGET_NAME_REFERENCE=ruleWIDGET_NAME_REFERENCE();

            state._fsp--;

             current =iv_ruleWIDGET_NAME_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleWIDGET_NAME_REFERENCE7087); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleWIDGET_NAME_REFERENCE"


    // $ANTLR start "ruleWIDGET_NAME_REFERENCE"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3483:1: ruleWIDGET_NAME_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleWIDGET_NAME_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3486:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3487:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleWIDGET_NAME_REFERENCE7126); 

            		current.merge(this_STRING_0);
                
             
                newLeafNode(this_STRING_0, grammarAccess.getWIDGET_NAME_REFERENCEAccess().getSTRINGTerminalRuleCall()); 
                

            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleWIDGET_NAME_REFERENCE"


    // $ANTLR start "entryRuleWORK_DEFINITION_NAME_REFERENCE"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3502:1: entryRuleWORK_DEFINITION_NAME_REFERENCE returns [String current=null] : iv_ruleWORK_DEFINITION_NAME_REFERENCE= ruleWORK_DEFINITION_NAME_REFERENCE EOF ;
    public final String entryRuleWORK_DEFINITION_NAME_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleWORK_DEFINITION_NAME_REFERENCE = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3503:2: (iv_ruleWORK_DEFINITION_NAME_REFERENCE= ruleWORK_DEFINITION_NAME_REFERENCE EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3504:2: iv_ruleWORK_DEFINITION_NAME_REFERENCE= ruleWORK_DEFINITION_NAME_REFERENCE EOF
            {
             newCompositeNode(grammarAccess.getWORK_DEFINITION_NAME_REFERENCERule()); 
            pushFollow(FOLLOW_ruleWORK_DEFINITION_NAME_REFERENCE_in_entryRuleWORK_DEFINITION_NAME_REFERENCE7171);
            iv_ruleWORK_DEFINITION_NAME_REFERENCE=ruleWORK_DEFINITION_NAME_REFERENCE();

            state._fsp--;

             current =iv_ruleWORK_DEFINITION_NAME_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleWORK_DEFINITION_NAME_REFERENCE7182); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleWORK_DEFINITION_NAME_REFERENCE"


    // $ANTLR start "ruleWORK_DEFINITION_NAME_REFERENCE"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3511:1: ruleWORK_DEFINITION_NAME_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleWORK_DEFINITION_NAME_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3514:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3515:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleWORK_DEFINITION_NAME_REFERENCE7221); 

            		current.merge(this_STRING_0);
                
             
                newLeafNode(this_STRING_0, grammarAccess.getWORK_DEFINITION_NAME_REFERENCEAccess().getSTRINGTerminalRuleCall()); 
                

            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleWORK_DEFINITION_NAME_REFERENCE"


    // $ANTLR start "entryRuleToState"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3530:1: entryRuleToState returns [EObject current=null] : iv_ruleToState= ruleToState EOF ;
    public final EObject entryRuleToState() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleToState = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3531:2: (iv_ruleToState= ruleToState EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3532:2: iv_ruleToState= ruleToState EOF
            {
             newCompositeNode(grammarAccess.getToStateRule()); 
            pushFollow(FOLLOW_ruleToState_in_entryRuleToState7265);
            iv_ruleToState=ruleToState();

            state._fsp--;

             current =iv_ruleToState; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleToState7275); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleToState"


    // $ANTLR start "ruleToState"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3539:1: ruleToState returns [EObject current=null] : (otherlv_0= 'to' ( ( ruleSTATE_NAME_REFERENCE ) ) ( (lv_options_2_0= ruleTransitionOption ) )* ) ;
    public final EObject ruleToState() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        AntlrDatatypeRuleToken lv_options_2_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3542:28: ( (otherlv_0= 'to' ( ( ruleSTATE_NAME_REFERENCE ) ) ( (lv_options_2_0= ruleTransitionOption ) )* ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3543:1: (otherlv_0= 'to' ( ( ruleSTATE_NAME_REFERENCE ) ) ( (lv_options_2_0= ruleTransitionOption ) )* )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3543:1: (otherlv_0= 'to' ( ( ruleSTATE_NAME_REFERENCE ) ) ( (lv_options_2_0= ruleTransitionOption ) )* )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3543:3: otherlv_0= 'to' ( ( ruleSTATE_NAME_REFERENCE ) ) ( (lv_options_2_0= ruleTransitionOption ) )*
            {
            otherlv_0=(Token)match(input,80,FOLLOW_80_in_ruleToState7312); 

                	newLeafNode(otherlv_0, grammarAccess.getToStateAccess().getToKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3547:1: ( ( ruleSTATE_NAME_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3548:1: ( ruleSTATE_NAME_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3548:1: ( ruleSTATE_NAME_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3549:3: ruleSTATE_NAME_REFERENCE
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getToStateRule());
            	        }
                    
             
            	        newCompositeNode(grammarAccess.getToStateAccess().getStateStateDefCrossReference_1_0()); 
            	    
            pushFollow(FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleToState7335);
            ruleSTATE_NAME_REFERENCE();

            state._fsp--;

             
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3562:2: ( (lv_options_2_0= ruleTransitionOption ) )*
            loop94:
            do {
                int alt94=2;
                int LA94_0 = input.LA(1);

                if ( (LA94_0==RULE_STRING||(LA94_0>=87 && LA94_0<=88)) ) {
                    alt94=1;
                }


                switch (alt94) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3563:1: (lv_options_2_0= ruleTransitionOption )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3563:1: (lv_options_2_0= ruleTransitionOption )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3564:3: lv_options_2_0= ruleTransitionOption
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getToStateAccess().getOptionsTransitionOptionParserRuleCall_2_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleTransitionOption_in_ruleToState7356);
            	    lv_options_2_0=ruleTransitionOption();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getToStateRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"options",
            	            		lv_options_2_0, 
            	            		"TransitionOption");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    break loop94;
                }
            } while (true);


            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleToState"


    // $ANTLR start "entryRuleLayoutType"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3588:1: entryRuleLayoutType returns [EObject current=null] : iv_ruleLayoutType= ruleLayoutType EOF ;
    public final EObject entryRuleLayoutType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleLayoutType = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3589:2: (iv_ruleLayoutType= ruleLayoutType EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3590:2: iv_ruleLayoutType= ruleLayoutType EOF
            {
             newCompositeNode(grammarAccess.getLayoutTypeRule()); 
            pushFollow(FOLLOW_ruleLayoutType_in_entryRuleLayoutType7393);
            iv_ruleLayoutType=ruleLayoutType();

            state._fsp--;

             current =iv_ruleLayoutType; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleLayoutType7403); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleLayoutType"


    // $ANTLR start "ruleLayoutType"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3597:1: ruleLayoutType returns [EObject current=null] : (this_LayoutDef_0= ruleLayoutDef | this_LayoutCopy_1= ruleLayoutCopy ) ;
    public final EObject ruleLayoutType() throws RecognitionException {
        EObject current = null;

        EObject this_LayoutDef_0 = null;

        EObject this_LayoutCopy_1 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3600:28: ( (this_LayoutDef_0= ruleLayoutDef | this_LayoutCopy_1= ruleLayoutCopy ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3601:1: (this_LayoutDef_0= ruleLayoutDef | this_LayoutCopy_1= ruleLayoutCopy )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3601:1: (this_LayoutDef_0= ruleLayoutDef | this_LayoutCopy_1= ruleLayoutCopy )
            int alt95=2;
            int LA95_0 = input.LA(1);

            if ( (LA95_0==81) ) {
                alt95=1;
            }
            else if ( (LA95_0==82) ) {
                alt95=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 95, 0, input);

                throw nvae;
            }
            switch (alt95) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3602:5: this_LayoutDef_0= ruleLayoutDef
                    {
                     
                            newCompositeNode(grammarAccess.getLayoutTypeAccess().getLayoutDefParserRuleCall_0()); 
                        
                    pushFollow(FOLLOW_ruleLayoutDef_in_ruleLayoutType7450);
                    this_LayoutDef_0=ruleLayoutDef();

                    state._fsp--;

                     
                            current = this_LayoutDef_0; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3612:5: this_LayoutCopy_1= ruleLayoutCopy
                    {
                     
                            newCompositeNode(grammarAccess.getLayoutTypeAccess().getLayoutCopyParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_ruleLayoutCopy_in_ruleLayoutType7477);
                    this_LayoutCopy_1=ruleLayoutCopy();

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
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleLayoutType"


    // $ANTLR start "entryRuleLayoutDef"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3628:1: entryRuleLayoutDef returns [EObject current=null] : iv_ruleLayoutDef= ruleLayoutDef EOF ;
    public final EObject entryRuleLayoutDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleLayoutDef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3629:2: (iv_ruleLayoutDef= ruleLayoutDef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3630:2: iv_ruleLayoutDef= ruleLayoutDef EOF
            {
             newCompositeNode(grammarAccess.getLayoutDefRule()); 
            pushFollow(FOLLOW_ruleLayoutDef_in_entryRuleLayoutDef7512);
            iv_ruleLayoutDef=ruleLayoutDef();

            state._fsp--;

             current =iv_ruleLayoutDef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleLayoutDef7522); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleLayoutDef"


    // $ANTLR start "ruleLayoutDef"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3637:1: ruleLayoutDef returns [EObject current=null] : (otherlv_0= 'layout' otherlv_1= '{' ( (lv_layoutItems_2_0= ruleLayoutItem ) )+ otherlv_3= '}' ) ;
    public final EObject ruleLayoutDef() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;
        Token otherlv_3=null;
        EObject lv_layoutItems_2_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3640:28: ( (otherlv_0= 'layout' otherlv_1= '{' ( (lv_layoutItems_2_0= ruleLayoutItem ) )+ otherlv_3= '}' ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3641:1: (otherlv_0= 'layout' otherlv_1= '{' ( (lv_layoutItems_2_0= ruleLayoutItem ) )+ otherlv_3= '}' )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3641:1: (otherlv_0= 'layout' otherlv_1= '{' ( (lv_layoutItems_2_0= ruleLayoutItem ) )+ otherlv_3= '}' )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3641:3: otherlv_0= 'layout' otherlv_1= '{' ( (lv_layoutItems_2_0= ruleLayoutItem ) )+ otherlv_3= '}'
            {
            otherlv_0=(Token)match(input,81,FOLLOW_81_in_ruleLayoutDef7559); 

                	newLeafNode(otherlv_0, grammarAccess.getLayoutDefAccess().getLayoutKeyword_0());
                
            otherlv_1=(Token)match(input,16,FOLLOW_16_in_ruleLayoutDef7571); 

                	newLeafNode(otherlv_1, grammarAccess.getLayoutDefAccess().getLeftCurlyBracketKeyword_1());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3649:1: ( (lv_layoutItems_2_0= ruleLayoutItem ) )+
            int cnt96=0;
            loop96:
            do {
                int alt96=2;
                int LA96_0 = input.LA(1);

                if ( ((LA96_0>=57 && LA96_0<=58)||LA96_0==83) ) {
                    alt96=1;
                }


                switch (alt96) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3650:1: (lv_layoutItems_2_0= ruleLayoutItem )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3650:1: (lv_layoutItems_2_0= ruleLayoutItem )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3651:3: lv_layoutItems_2_0= ruleLayoutItem
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getLayoutDefAccess().getLayoutItemsLayoutItemParserRuleCall_2_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleLayoutItem_in_ruleLayoutDef7592);
            	    lv_layoutItems_2_0=ruleLayoutItem();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getLayoutDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"layoutItems",
            	            		lv_layoutItems_2_0, 
            	            		"LayoutItem");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt96 >= 1 ) break loop96;
                        EarlyExitException eee =
                            new EarlyExitException(96, input);
                        throw eee;
                }
                cnt96++;
            } while (true);

            otherlv_3=(Token)match(input,21,FOLLOW_21_in_ruleLayoutDef7605); 

                	newLeafNode(otherlv_3, grammarAccess.getLayoutDefAccess().getRightCurlyBracketKeyword_3());
                

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleLayoutDef"


    // $ANTLR start "entryRuleLayoutCopy"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3679:1: entryRuleLayoutCopy returns [EObject current=null] : iv_ruleLayoutCopy= ruleLayoutCopy EOF ;
    public final EObject entryRuleLayoutCopy() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleLayoutCopy = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3680:2: (iv_ruleLayoutCopy= ruleLayoutCopy EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3681:2: iv_ruleLayoutCopy= ruleLayoutCopy EOF
            {
             newCompositeNode(grammarAccess.getLayoutCopyRule()); 
            pushFollow(FOLLOW_ruleLayoutCopy_in_entryRuleLayoutCopy7641);
            iv_ruleLayoutCopy=ruleLayoutCopy();

            state._fsp--;

             current =iv_ruleLayoutCopy; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleLayoutCopy7651); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleLayoutCopy"


    // $ANTLR start "ruleLayoutCopy"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3688:1: ruleLayoutCopy returns [EObject current=null] : (otherlv_0= 'layoutCopyFrom' ( ( ruleSTATE_NAME_REFERENCE ) ) ) ;
    public final EObject ruleLayoutCopy() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3691:28: ( (otherlv_0= 'layoutCopyFrom' ( ( ruleSTATE_NAME_REFERENCE ) ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3692:1: (otherlv_0= 'layoutCopyFrom' ( ( ruleSTATE_NAME_REFERENCE ) ) )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3692:1: (otherlv_0= 'layoutCopyFrom' ( ( ruleSTATE_NAME_REFERENCE ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3692:3: otherlv_0= 'layoutCopyFrom' ( ( ruleSTATE_NAME_REFERENCE ) )
            {
            otherlv_0=(Token)match(input,82,FOLLOW_82_in_ruleLayoutCopy7688); 

                	newLeafNode(otherlv_0, grammarAccess.getLayoutCopyAccess().getLayoutCopyFromKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3696:1: ( ( ruleSTATE_NAME_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3697:1: ( ruleSTATE_NAME_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3697:1: ( ruleSTATE_NAME_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3698:3: ruleSTATE_NAME_REFERENCE
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getLayoutCopyRule());
            	        }
                    
             
            	        newCompositeNode(grammarAccess.getLayoutCopyAccess().getStateStateDefCrossReference_1_0()); 
            	    
            pushFollow(FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleLayoutCopy7711);
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
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleLayoutCopy"


    // $ANTLR start "entryRuleLayoutItem"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3719:1: entryRuleLayoutItem returns [EObject current=null] : iv_ruleLayoutItem= ruleLayoutItem EOF ;
    public final EObject entryRuleLayoutItem() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleLayoutItem = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3720:2: (iv_ruleLayoutItem= ruleLayoutItem EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3721:2: iv_ruleLayoutItem= ruleLayoutItem EOF
            {
             newCompositeNode(grammarAccess.getLayoutItemRule()); 
            pushFollow(FOLLOW_ruleLayoutItem_in_entryRuleLayoutItem7747);
            iv_ruleLayoutItem=ruleLayoutItem();

            state._fsp--;

             current =iv_ruleLayoutItem; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleLayoutItem7757); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleLayoutItem"


    // $ANTLR start "ruleLayoutItem"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3728:1: ruleLayoutItem returns [EObject current=null] : (this_WidgetRef_0= ruleWidgetRef | this_AttrWidget_1= ruleAttrWidget | this_Composite_2= ruleComposite ) ;
    public final EObject ruleLayoutItem() throws RecognitionException {
        EObject current = null;

        EObject this_WidgetRef_0 = null;

        EObject this_AttrWidget_1 = null;

        EObject this_Composite_2 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3731:28: ( (this_WidgetRef_0= ruleWidgetRef | this_AttrWidget_1= ruleAttrWidget | this_Composite_2= ruleComposite ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3732:1: (this_WidgetRef_0= ruleWidgetRef | this_AttrWidget_1= ruleAttrWidget | this_Composite_2= ruleComposite )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3732:1: (this_WidgetRef_0= ruleWidgetRef | this_AttrWidget_1= ruleAttrWidget | this_Composite_2= ruleComposite )
            int alt97=3;
            switch ( input.LA(1) ) {
            case 57:
                {
                alt97=1;
                }
                break;
            case 58:
                {
                alt97=2;
                }
                break;
            case 83:
                {
                alt97=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 97, 0, input);

                throw nvae;
            }

            switch (alt97) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3733:5: this_WidgetRef_0= ruleWidgetRef
                    {
                     
                            newCompositeNode(grammarAccess.getLayoutItemAccess().getWidgetRefParserRuleCall_0()); 
                        
                    pushFollow(FOLLOW_ruleWidgetRef_in_ruleLayoutItem7804);
                    this_WidgetRef_0=ruleWidgetRef();

                    state._fsp--;

                     
                            current = this_WidgetRef_0; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3743:5: this_AttrWidget_1= ruleAttrWidget
                    {
                     
                            newCompositeNode(grammarAccess.getLayoutItemAccess().getAttrWidgetParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_ruleAttrWidget_in_ruleLayoutItem7831);
                    this_AttrWidget_1=ruleAttrWidget();

                    state._fsp--;

                     
                            current = this_AttrWidget_1; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3753:5: this_Composite_2= ruleComposite
                    {
                     
                            newCompositeNode(grammarAccess.getLayoutItemAccess().getCompositeParserRuleCall_2()); 
                        
                    pushFollow(FOLLOW_ruleComposite_in_ruleLayoutItem7858);
                    this_Composite_2=ruleComposite();

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
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleLayoutItem"


    // $ANTLR start "entryRuleComposite"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3769:1: entryRuleComposite returns [EObject current=null] : iv_ruleComposite= ruleComposite EOF ;
    public final EObject entryRuleComposite() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleComposite = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3770:2: (iv_ruleComposite= ruleComposite EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3771:2: iv_ruleComposite= ruleComposite EOF
            {
             newCompositeNode(grammarAccess.getCompositeRule()); 
            pushFollow(FOLLOW_ruleComposite_in_entryRuleComposite7893);
            iv_ruleComposite=ruleComposite();

            state._fsp--;

             current =iv_ruleComposite; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleComposite7903); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleComposite"


    // $ANTLR start "ruleComposite"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3778:1: ruleComposite returns [EObject current=null] : (otherlv_0= 'composite' otherlv_1= '{' otherlv_2= 'numColumns' ( (lv_numColumns_3_0= RULE_INT ) ) ( (lv_layoutItems_4_0= ruleLayoutItem ) )+ (otherlv_5= 'option' ( (lv_options_6_0= ruleCompositeOption ) ) )* otherlv_7= '}' ) ;
    public final EObject ruleComposite() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;
        Token otherlv_2=null;
        Token lv_numColumns_3_0=null;
        Token otherlv_5=null;
        Token otherlv_7=null;
        EObject lv_layoutItems_4_0 = null;

        AntlrDatatypeRuleToken lv_options_6_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3781:28: ( (otherlv_0= 'composite' otherlv_1= '{' otherlv_2= 'numColumns' ( (lv_numColumns_3_0= RULE_INT ) ) ( (lv_layoutItems_4_0= ruleLayoutItem ) )+ (otherlv_5= 'option' ( (lv_options_6_0= ruleCompositeOption ) ) )* otherlv_7= '}' ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3782:1: (otherlv_0= 'composite' otherlv_1= '{' otherlv_2= 'numColumns' ( (lv_numColumns_3_0= RULE_INT ) ) ( (lv_layoutItems_4_0= ruleLayoutItem ) )+ (otherlv_5= 'option' ( (lv_options_6_0= ruleCompositeOption ) ) )* otherlv_7= '}' )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3782:1: (otherlv_0= 'composite' otherlv_1= '{' otherlv_2= 'numColumns' ( (lv_numColumns_3_0= RULE_INT ) ) ( (lv_layoutItems_4_0= ruleLayoutItem ) )+ (otherlv_5= 'option' ( (lv_options_6_0= ruleCompositeOption ) ) )* otherlv_7= '}' )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3782:3: otherlv_0= 'composite' otherlv_1= '{' otherlv_2= 'numColumns' ( (lv_numColumns_3_0= RULE_INT ) ) ( (lv_layoutItems_4_0= ruleLayoutItem ) )+ (otherlv_5= 'option' ( (lv_options_6_0= ruleCompositeOption ) ) )* otherlv_7= '}'
            {
            otherlv_0=(Token)match(input,83,FOLLOW_83_in_ruleComposite7940); 

                	newLeafNode(otherlv_0, grammarAccess.getCompositeAccess().getCompositeKeyword_0());
                
            otherlv_1=(Token)match(input,16,FOLLOW_16_in_ruleComposite7952); 

                	newLeafNode(otherlv_1, grammarAccess.getCompositeAccess().getLeftCurlyBracketKeyword_1());
                
            otherlv_2=(Token)match(input,84,FOLLOW_84_in_ruleComposite7964); 

                	newLeafNode(otherlv_2, grammarAccess.getCompositeAccess().getNumColumnsKeyword_2());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3794:1: ( (lv_numColumns_3_0= RULE_INT ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3795:1: (lv_numColumns_3_0= RULE_INT )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3795:1: (lv_numColumns_3_0= RULE_INT )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3796:3: lv_numColumns_3_0= RULE_INT
            {
            lv_numColumns_3_0=(Token)match(input,RULE_INT,FOLLOW_RULE_INT_in_ruleComposite7981); 

            			newLeafNode(lv_numColumns_3_0, grammarAccess.getCompositeAccess().getNumColumnsINTTerminalRuleCall_3_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getCompositeRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"numColumns",
                    		lv_numColumns_3_0, 
                    		"INT");
            	    

            }


            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3812:2: ( (lv_layoutItems_4_0= ruleLayoutItem ) )+
            int cnt98=0;
            loop98:
            do {
                int alt98=2;
                int LA98_0 = input.LA(1);

                if ( ((LA98_0>=57 && LA98_0<=58)||LA98_0==83) ) {
                    alt98=1;
                }


                switch (alt98) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3813:1: (lv_layoutItems_4_0= ruleLayoutItem )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3813:1: (lv_layoutItems_4_0= ruleLayoutItem )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3814:3: lv_layoutItems_4_0= ruleLayoutItem
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getCompositeAccess().getLayoutItemsLayoutItemParserRuleCall_4_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleLayoutItem_in_ruleComposite8007);
            	    lv_layoutItems_4_0=ruleLayoutItem();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getCompositeRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"layoutItems",
            	            		lv_layoutItems_4_0, 
            	            		"LayoutItem");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt98 >= 1 ) break loop98;
                        EarlyExitException eee =
                            new EarlyExitException(98, input);
                        throw eee;
                }
                cnt98++;
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3830:3: (otherlv_5= 'option' ( (lv_options_6_0= ruleCompositeOption ) ) )*
            loop99:
            do {
                int alt99=2;
                int LA99_0 = input.LA(1);

                if ( (LA99_0==54) ) {
                    alt99=1;
                }


                switch (alt99) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3830:5: otherlv_5= 'option' ( (lv_options_6_0= ruleCompositeOption ) )
            	    {
            	    otherlv_5=(Token)match(input,54,FOLLOW_54_in_ruleComposite8021); 

            	        	newLeafNode(otherlv_5, grammarAccess.getCompositeAccess().getOptionKeyword_5_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3834:1: ( (lv_options_6_0= ruleCompositeOption ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3835:1: (lv_options_6_0= ruleCompositeOption )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3835:1: (lv_options_6_0= ruleCompositeOption )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3836:3: lv_options_6_0= ruleCompositeOption
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getCompositeAccess().getOptionsCompositeOptionParserRuleCall_5_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleCompositeOption_in_ruleComposite8042);
            	    lv_options_6_0=ruleCompositeOption();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getCompositeRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"options",
            	            		lv_options_6_0, 
            	            		"CompositeOption");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop99;
                }
            } while (true);

            otherlv_7=(Token)match(input,21,FOLLOW_21_in_ruleComposite8056); 

                	newLeafNode(otherlv_7, grammarAccess.getCompositeAccess().getRightCurlyBracketKeyword_6());
                

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleComposite"


    // $ANTLR start "entryRuleUSER_DEF_OPTION_NAME"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3864:1: entryRuleUSER_DEF_OPTION_NAME returns [String current=null] : iv_ruleUSER_DEF_OPTION_NAME= ruleUSER_DEF_OPTION_NAME EOF ;
    public final String entryRuleUSER_DEF_OPTION_NAME() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleUSER_DEF_OPTION_NAME = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3865:2: (iv_ruleUSER_DEF_OPTION_NAME= ruleUSER_DEF_OPTION_NAME EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3866:2: iv_ruleUSER_DEF_OPTION_NAME= ruleUSER_DEF_OPTION_NAME EOF
            {
             newCompositeNode(grammarAccess.getUSER_DEF_OPTION_NAMERule()); 
            pushFollow(FOLLOW_ruleUSER_DEF_OPTION_NAME_in_entryRuleUSER_DEF_OPTION_NAME8093);
            iv_ruleUSER_DEF_OPTION_NAME=ruleUSER_DEF_OPTION_NAME();

            state._fsp--;

             current =iv_ruleUSER_DEF_OPTION_NAME.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleUSER_DEF_OPTION_NAME8104); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleUSER_DEF_OPTION_NAME"


    // $ANTLR start "ruleUSER_DEF_OPTION_NAME"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3873:1: ruleUSER_DEF_OPTION_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleUSER_DEF_OPTION_NAME() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3876:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3877:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleUSER_DEF_OPTION_NAME8143); 

            		current.merge(this_STRING_0);
                
             
                newLeafNode(this_STRING_0, grammarAccess.getUSER_DEF_OPTION_NAMEAccess().getSTRINGTerminalRuleCall()); 
                

            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleUSER_DEF_OPTION_NAME"


    // $ANTLR start "entryRuleUserDefOption"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3892:1: entryRuleUserDefOption returns [String current=null] : iv_ruleUserDefOption= ruleUserDefOption EOF ;
    public final String entryRuleUserDefOption() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleUserDefOption = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3893:2: (iv_ruleUserDefOption= ruleUserDefOption EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3894:2: iv_ruleUserDefOption= ruleUserDefOption EOF
            {
             newCompositeNode(grammarAccess.getUserDefOptionRule()); 
            pushFollow(FOLLOW_ruleUserDefOption_in_entryRuleUserDefOption8188);
            iv_ruleUserDefOption=ruleUserDefOption();

            state._fsp--;

             current =iv_ruleUserDefOption.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleUserDefOption8199); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleUserDefOption"


    // $ANTLR start "ruleUserDefOption"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3901:1: ruleUserDefOption returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'GetOrCreate' | this_USER_DEF_OPTION_NAME_1= ruleUSER_DEF_OPTION_NAME ) ;
    public final AntlrDatatypeRuleToken ruleUserDefOption() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        AntlrDatatypeRuleToken this_USER_DEF_OPTION_NAME_1 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3904:28: ( (kw= 'GetOrCreate' | this_USER_DEF_OPTION_NAME_1= ruleUSER_DEF_OPTION_NAME ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3905:1: (kw= 'GetOrCreate' | this_USER_DEF_OPTION_NAME_1= ruleUSER_DEF_OPTION_NAME )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3905:1: (kw= 'GetOrCreate' | this_USER_DEF_OPTION_NAME_1= ruleUSER_DEF_OPTION_NAME )
            int alt100=2;
            int LA100_0 = input.LA(1);

            if ( (LA100_0==85) ) {
                alt100=1;
            }
            else if ( (LA100_0==RULE_STRING) ) {
                alt100=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 100, 0, input);

                throw nvae;
            }
            switch (alt100) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3906:2: kw= 'GetOrCreate'
                    {
                    kw=(Token)match(input,85,FOLLOW_85_in_ruleUserDefOption8237); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getUserDefOptionAccess().getGetOrCreateKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3913:5: this_USER_DEF_OPTION_NAME_1= ruleUSER_DEF_OPTION_NAME
                    {
                     
                            newCompositeNode(grammarAccess.getUserDefOptionAccess().getUSER_DEF_OPTION_NAMEParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_ruleUSER_DEF_OPTION_NAME_in_ruleUserDefOption8265);
                    this_USER_DEF_OPTION_NAME_1=ruleUSER_DEF_OPTION_NAME();

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
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleUserDefOption"


    // $ANTLR start "entryRulePROGRAM_DEF_OPTION_NAME"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3931:1: entryRulePROGRAM_DEF_OPTION_NAME returns [String current=null] : iv_rulePROGRAM_DEF_OPTION_NAME= rulePROGRAM_DEF_OPTION_NAME EOF ;
    public final String entryRulePROGRAM_DEF_OPTION_NAME() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_rulePROGRAM_DEF_OPTION_NAME = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3932:2: (iv_rulePROGRAM_DEF_OPTION_NAME= rulePROGRAM_DEF_OPTION_NAME EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3933:2: iv_rulePROGRAM_DEF_OPTION_NAME= rulePROGRAM_DEF_OPTION_NAME EOF
            {
             newCompositeNode(grammarAccess.getPROGRAM_DEF_OPTION_NAMERule()); 
            pushFollow(FOLLOW_rulePROGRAM_DEF_OPTION_NAME_in_entryRulePROGRAM_DEF_OPTION_NAME8311);
            iv_rulePROGRAM_DEF_OPTION_NAME=rulePROGRAM_DEF_OPTION_NAME();

            state._fsp--;

             current =iv_rulePROGRAM_DEF_OPTION_NAME.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRulePROGRAM_DEF_OPTION_NAME8322); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRulePROGRAM_DEF_OPTION_NAME"


    // $ANTLR start "rulePROGRAM_DEF_OPTION_NAME"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3940:1: rulePROGRAM_DEF_OPTION_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken rulePROGRAM_DEF_OPTION_NAME() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3943:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3944:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rulePROGRAM_DEF_OPTION_NAME8361); 

            		current.merge(this_STRING_0);
                
             
                newLeafNode(this_STRING_0, grammarAccess.getPROGRAM_DEF_OPTION_NAMEAccess().getSTRINGTerminalRuleCall()); 
                

            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "rulePROGRAM_DEF_OPTION_NAME"


    // $ANTLR start "entryRuleProgramDefOption"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3959:1: entryRuleProgramDefOption returns [String current=null] : iv_ruleProgramDefOption= ruleProgramDefOption EOF ;
    public final String entryRuleProgramDefOption() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleProgramDefOption = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3960:2: (iv_ruleProgramDefOption= ruleProgramDefOption EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3961:2: iv_ruleProgramDefOption= ruleProgramDefOption EOF
            {
             newCompositeNode(grammarAccess.getProgramDefOptionRule()); 
            pushFollow(FOLLOW_ruleProgramDefOption_in_entryRuleProgramDefOption8406);
            iv_ruleProgramDefOption=ruleProgramDefOption();

            state._fsp--;

             current =iv_ruleProgramDefOption.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleProgramDefOption8417); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleProgramDefOption"


    // $ANTLR start "ruleProgramDefOption"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3968:1: ruleProgramDefOption returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'GetOrCreate' | this_PROGRAM_DEF_OPTION_NAME_1= rulePROGRAM_DEF_OPTION_NAME ) ;
    public final AntlrDatatypeRuleToken ruleProgramDefOption() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        AntlrDatatypeRuleToken this_PROGRAM_DEF_OPTION_NAME_1 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3971:28: ( (kw= 'GetOrCreate' | this_PROGRAM_DEF_OPTION_NAME_1= rulePROGRAM_DEF_OPTION_NAME ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3972:1: (kw= 'GetOrCreate' | this_PROGRAM_DEF_OPTION_NAME_1= rulePROGRAM_DEF_OPTION_NAME )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3972:1: (kw= 'GetOrCreate' | this_PROGRAM_DEF_OPTION_NAME_1= rulePROGRAM_DEF_OPTION_NAME )
            int alt101=2;
            int LA101_0 = input.LA(1);

            if ( (LA101_0==85) ) {
                alt101=1;
            }
            else if ( (LA101_0==RULE_STRING) ) {
                alt101=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 101, 0, input);

                throw nvae;
            }
            switch (alt101) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3973:2: kw= 'GetOrCreate'
                    {
                    kw=(Token)match(input,85,FOLLOW_85_in_ruleProgramDefOption8455); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getProgramDefOptionAccess().getGetOrCreateKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3980:5: this_PROGRAM_DEF_OPTION_NAME_1= rulePROGRAM_DEF_OPTION_NAME
                    {
                     
                            newCompositeNode(grammarAccess.getProgramDefOptionAccess().getPROGRAM_DEF_OPTION_NAMEParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_rulePROGRAM_DEF_OPTION_NAME_in_ruleProgramDefOption8483);
                    this_PROGRAM_DEF_OPTION_NAME_1=rulePROGRAM_DEF_OPTION_NAME();

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
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleProgramDefOption"


    // $ANTLR start "entryRuleTEAM_DEF_OPTION_NAME"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3998:1: entryRuleTEAM_DEF_OPTION_NAME returns [String current=null] : iv_ruleTEAM_DEF_OPTION_NAME= ruleTEAM_DEF_OPTION_NAME EOF ;
    public final String entryRuleTEAM_DEF_OPTION_NAME() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleTEAM_DEF_OPTION_NAME = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3999:2: (iv_ruleTEAM_DEF_OPTION_NAME= ruleTEAM_DEF_OPTION_NAME EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4000:2: iv_ruleTEAM_DEF_OPTION_NAME= ruleTEAM_DEF_OPTION_NAME EOF
            {
             newCompositeNode(grammarAccess.getTEAM_DEF_OPTION_NAMERule()); 
            pushFollow(FOLLOW_ruleTEAM_DEF_OPTION_NAME_in_entryRuleTEAM_DEF_OPTION_NAME8529);
            iv_ruleTEAM_DEF_OPTION_NAME=ruleTEAM_DEF_OPTION_NAME();

            state._fsp--;

             current =iv_ruleTEAM_DEF_OPTION_NAME.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleTEAM_DEF_OPTION_NAME8540); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleTEAM_DEF_OPTION_NAME"


    // $ANTLR start "ruleTEAM_DEF_OPTION_NAME"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4007:1: ruleTEAM_DEF_OPTION_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleTEAM_DEF_OPTION_NAME() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4010:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4011:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleTEAM_DEF_OPTION_NAME8579); 

            		current.merge(this_STRING_0);
                
             
                newLeafNode(this_STRING_0, grammarAccess.getTEAM_DEF_OPTION_NAMEAccess().getSTRINGTerminalRuleCall()); 
                

            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleTEAM_DEF_OPTION_NAME"


    // $ANTLR start "entryRuleTeamDefOption"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4026:1: entryRuleTeamDefOption returns [String current=null] : iv_ruleTeamDefOption= ruleTeamDefOption EOF ;
    public final String entryRuleTeamDefOption() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleTeamDefOption = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4027:2: (iv_ruleTeamDefOption= ruleTeamDefOption EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4028:2: iv_ruleTeamDefOption= ruleTeamDefOption EOF
            {
             newCompositeNode(grammarAccess.getTeamDefOptionRule()); 
            pushFollow(FOLLOW_ruleTeamDefOption_in_entryRuleTeamDefOption8624);
            iv_ruleTeamDefOption=ruleTeamDefOption();

            state._fsp--;

             current =iv_ruleTeamDefOption.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleTeamDefOption8635); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleTeamDefOption"


    // $ANTLR start "ruleTeamDefOption"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4035:1: ruleTeamDefOption returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'GetOrCreate' | this_TEAM_DEF_OPTION_NAME_1= ruleTEAM_DEF_OPTION_NAME ) ;
    public final AntlrDatatypeRuleToken ruleTeamDefOption() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        AntlrDatatypeRuleToken this_TEAM_DEF_OPTION_NAME_1 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4038:28: ( (kw= 'GetOrCreate' | this_TEAM_DEF_OPTION_NAME_1= ruleTEAM_DEF_OPTION_NAME ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4039:1: (kw= 'GetOrCreate' | this_TEAM_DEF_OPTION_NAME_1= ruleTEAM_DEF_OPTION_NAME )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4039:1: (kw= 'GetOrCreate' | this_TEAM_DEF_OPTION_NAME_1= ruleTEAM_DEF_OPTION_NAME )
            int alt102=2;
            int LA102_0 = input.LA(1);

            if ( (LA102_0==85) ) {
                alt102=1;
            }
            else if ( (LA102_0==RULE_STRING) ) {
                alt102=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 102, 0, input);

                throw nvae;
            }
            switch (alt102) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4040:2: kw= 'GetOrCreate'
                    {
                    kw=(Token)match(input,85,FOLLOW_85_in_ruleTeamDefOption8673); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getTeamDefOptionAccess().getGetOrCreateKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4047:5: this_TEAM_DEF_OPTION_NAME_1= ruleTEAM_DEF_OPTION_NAME
                    {
                     
                            newCompositeNode(grammarAccess.getTeamDefOptionAccess().getTEAM_DEF_OPTION_NAMEParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_ruleTEAM_DEF_OPTION_NAME_in_ruleTeamDefOption8701);
                    this_TEAM_DEF_OPTION_NAME_1=ruleTEAM_DEF_OPTION_NAME();

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
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleTeamDefOption"


    // $ANTLR start "entryRuleAI_DEF_OPTION_NAME"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4065:1: entryRuleAI_DEF_OPTION_NAME returns [String current=null] : iv_ruleAI_DEF_OPTION_NAME= ruleAI_DEF_OPTION_NAME EOF ;
    public final String entryRuleAI_DEF_OPTION_NAME() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleAI_DEF_OPTION_NAME = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4066:2: (iv_ruleAI_DEF_OPTION_NAME= ruleAI_DEF_OPTION_NAME EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4067:2: iv_ruleAI_DEF_OPTION_NAME= ruleAI_DEF_OPTION_NAME EOF
            {
             newCompositeNode(grammarAccess.getAI_DEF_OPTION_NAMERule()); 
            pushFollow(FOLLOW_ruleAI_DEF_OPTION_NAME_in_entryRuleAI_DEF_OPTION_NAME8747);
            iv_ruleAI_DEF_OPTION_NAME=ruleAI_DEF_OPTION_NAME();

            state._fsp--;

             current =iv_ruleAI_DEF_OPTION_NAME.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAI_DEF_OPTION_NAME8758); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleAI_DEF_OPTION_NAME"


    // $ANTLR start "ruleAI_DEF_OPTION_NAME"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4074:1: ruleAI_DEF_OPTION_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleAI_DEF_OPTION_NAME() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4077:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4078:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAI_DEF_OPTION_NAME8797); 

            		current.merge(this_STRING_0);
                
             
                newLeafNode(this_STRING_0, grammarAccess.getAI_DEF_OPTION_NAMEAccess().getSTRINGTerminalRuleCall()); 
                

            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleAI_DEF_OPTION_NAME"


    // $ANTLR start "entryRuleActionableItemOption"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4093:1: entryRuleActionableItemOption returns [String current=null] : iv_ruleActionableItemOption= ruleActionableItemOption EOF ;
    public final String entryRuleActionableItemOption() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleActionableItemOption = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4094:2: (iv_ruleActionableItemOption= ruleActionableItemOption EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4095:2: iv_ruleActionableItemOption= ruleActionableItemOption EOF
            {
             newCompositeNode(grammarAccess.getActionableItemOptionRule()); 
            pushFollow(FOLLOW_ruleActionableItemOption_in_entryRuleActionableItemOption8842);
            iv_ruleActionableItemOption=ruleActionableItemOption();

            state._fsp--;

             current =iv_ruleActionableItemOption.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleActionableItemOption8853); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleActionableItemOption"


    // $ANTLR start "ruleActionableItemOption"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4102:1: ruleActionableItemOption returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'GetOrCreate' | this_AI_DEF_OPTION_NAME_1= ruleAI_DEF_OPTION_NAME ) ;
    public final AntlrDatatypeRuleToken ruleActionableItemOption() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        AntlrDatatypeRuleToken this_AI_DEF_OPTION_NAME_1 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4105:28: ( (kw= 'GetOrCreate' | this_AI_DEF_OPTION_NAME_1= ruleAI_DEF_OPTION_NAME ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4106:1: (kw= 'GetOrCreate' | this_AI_DEF_OPTION_NAME_1= ruleAI_DEF_OPTION_NAME )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4106:1: (kw= 'GetOrCreate' | this_AI_DEF_OPTION_NAME_1= ruleAI_DEF_OPTION_NAME )
            int alt103=2;
            int LA103_0 = input.LA(1);

            if ( (LA103_0==85) ) {
                alt103=1;
            }
            else if ( (LA103_0==RULE_STRING) ) {
                alt103=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 103, 0, input);

                throw nvae;
            }
            switch (alt103) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4107:2: kw= 'GetOrCreate'
                    {
                    kw=(Token)match(input,85,FOLLOW_85_in_ruleActionableItemOption8891); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getActionableItemOptionAccess().getGetOrCreateKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4114:5: this_AI_DEF_OPTION_NAME_1= ruleAI_DEF_OPTION_NAME
                    {
                     
                            newCompositeNode(grammarAccess.getActionableItemOptionAccess().getAI_DEF_OPTION_NAMEParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_ruleAI_DEF_OPTION_NAME_in_ruleActionableItemOption8919);
                    this_AI_DEF_OPTION_NAME_1=ruleAI_DEF_OPTION_NAME();

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
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleActionableItemOption"


    // $ANTLR start "entryRuleCOMPOSITE_OPTION_NAME"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4132:1: entryRuleCOMPOSITE_OPTION_NAME returns [String current=null] : iv_ruleCOMPOSITE_OPTION_NAME= ruleCOMPOSITE_OPTION_NAME EOF ;
    public final String entryRuleCOMPOSITE_OPTION_NAME() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleCOMPOSITE_OPTION_NAME = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4133:2: (iv_ruleCOMPOSITE_OPTION_NAME= ruleCOMPOSITE_OPTION_NAME EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4134:2: iv_ruleCOMPOSITE_OPTION_NAME= ruleCOMPOSITE_OPTION_NAME EOF
            {
             newCompositeNode(grammarAccess.getCOMPOSITE_OPTION_NAMERule()); 
            pushFollow(FOLLOW_ruleCOMPOSITE_OPTION_NAME_in_entryRuleCOMPOSITE_OPTION_NAME8965);
            iv_ruleCOMPOSITE_OPTION_NAME=ruleCOMPOSITE_OPTION_NAME();

            state._fsp--;

             current =iv_ruleCOMPOSITE_OPTION_NAME.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleCOMPOSITE_OPTION_NAME8976); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleCOMPOSITE_OPTION_NAME"


    // $ANTLR start "ruleCOMPOSITE_OPTION_NAME"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4141:1: ruleCOMPOSITE_OPTION_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleCOMPOSITE_OPTION_NAME() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4144:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4145:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleCOMPOSITE_OPTION_NAME9015); 

            		current.merge(this_STRING_0);
                
             
                newLeafNode(this_STRING_0, grammarAccess.getCOMPOSITE_OPTION_NAMEAccess().getSTRINGTerminalRuleCall()); 
                

            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleCOMPOSITE_OPTION_NAME"


    // $ANTLR start "entryRuleCompositeOption"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4160:1: entryRuleCompositeOption returns [String current=null] : iv_ruleCompositeOption= ruleCompositeOption EOF ;
    public final String entryRuleCompositeOption() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleCompositeOption = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4161:2: (iv_ruleCompositeOption= ruleCompositeOption EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4162:2: iv_ruleCompositeOption= ruleCompositeOption EOF
            {
             newCompositeNode(grammarAccess.getCompositeOptionRule()); 
            pushFollow(FOLLOW_ruleCompositeOption_in_entryRuleCompositeOption9060);
            iv_ruleCompositeOption=ruleCompositeOption();

            state._fsp--;

             current =iv_ruleCompositeOption.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleCompositeOption9071); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleCompositeOption"


    // $ANTLR start "ruleCompositeOption"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4169:1: ruleCompositeOption returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'None' | this_COMPOSITE_OPTION_NAME_1= ruleCOMPOSITE_OPTION_NAME ) ;
    public final AntlrDatatypeRuleToken ruleCompositeOption() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        AntlrDatatypeRuleToken this_COMPOSITE_OPTION_NAME_1 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4172:28: ( (kw= 'None' | this_COMPOSITE_OPTION_NAME_1= ruleCOMPOSITE_OPTION_NAME ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4173:1: (kw= 'None' | this_COMPOSITE_OPTION_NAME_1= ruleCOMPOSITE_OPTION_NAME )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4173:1: (kw= 'None' | this_COMPOSITE_OPTION_NAME_1= ruleCOMPOSITE_OPTION_NAME )
            int alt104=2;
            int LA104_0 = input.LA(1);

            if ( (LA104_0==86) ) {
                alt104=1;
            }
            else if ( (LA104_0==RULE_STRING) ) {
                alt104=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 104, 0, input);

                throw nvae;
            }
            switch (alt104) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4174:2: kw= 'None'
                    {
                    kw=(Token)match(input,86,FOLLOW_86_in_ruleCompositeOption9109); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getCompositeOptionAccess().getNoneKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4181:5: this_COMPOSITE_OPTION_NAME_1= ruleCOMPOSITE_OPTION_NAME
                    {
                     
                            newCompositeNode(grammarAccess.getCompositeOptionAccess().getCOMPOSITE_OPTION_NAMEParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_ruleCOMPOSITE_OPTION_NAME_in_ruleCompositeOption9137);
                    this_COMPOSITE_OPTION_NAME_1=ruleCOMPOSITE_OPTION_NAME();

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
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleCompositeOption"


    // $ANTLR start "entryRuleTRANSITION_OPTION_NAME"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4199:1: entryRuleTRANSITION_OPTION_NAME returns [String current=null] : iv_ruleTRANSITION_OPTION_NAME= ruleTRANSITION_OPTION_NAME EOF ;
    public final String entryRuleTRANSITION_OPTION_NAME() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleTRANSITION_OPTION_NAME = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4200:2: (iv_ruleTRANSITION_OPTION_NAME= ruleTRANSITION_OPTION_NAME EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4201:2: iv_ruleTRANSITION_OPTION_NAME= ruleTRANSITION_OPTION_NAME EOF
            {
             newCompositeNode(grammarAccess.getTRANSITION_OPTION_NAMERule()); 
            pushFollow(FOLLOW_ruleTRANSITION_OPTION_NAME_in_entryRuleTRANSITION_OPTION_NAME9183);
            iv_ruleTRANSITION_OPTION_NAME=ruleTRANSITION_OPTION_NAME();

            state._fsp--;

             current =iv_ruleTRANSITION_OPTION_NAME.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleTRANSITION_OPTION_NAME9194); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleTRANSITION_OPTION_NAME"


    // $ANTLR start "ruleTRANSITION_OPTION_NAME"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4208:1: ruleTRANSITION_OPTION_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleTRANSITION_OPTION_NAME() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4211:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4212:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleTRANSITION_OPTION_NAME9233); 

            		current.merge(this_STRING_0);
                
             
                newLeafNode(this_STRING_0, grammarAccess.getTRANSITION_OPTION_NAMEAccess().getSTRINGTerminalRuleCall()); 
                

            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleTRANSITION_OPTION_NAME"


    // $ANTLR start "entryRuleTransitionOption"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4227:1: entryRuleTransitionOption returns [String current=null] : iv_ruleTransitionOption= ruleTransitionOption EOF ;
    public final String entryRuleTransitionOption() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleTransitionOption = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4228:2: (iv_ruleTransitionOption= ruleTransitionOption EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4229:2: iv_ruleTransitionOption= ruleTransitionOption EOF
            {
             newCompositeNode(grammarAccess.getTransitionOptionRule()); 
            pushFollow(FOLLOW_ruleTransitionOption_in_entryRuleTransitionOption9278);
            iv_ruleTransitionOption=ruleTransitionOption();

            state._fsp--;

             current =iv_ruleTransitionOption.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleTransitionOption9289); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleTransitionOption"


    // $ANTLR start "ruleTransitionOption"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4236:1: ruleTransitionOption returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'AsDefault' | kw= 'OverrideAttributeValidation' | this_TRANSITION_OPTION_NAME_2= ruleTRANSITION_OPTION_NAME ) ;
    public final AntlrDatatypeRuleToken ruleTransitionOption() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        AntlrDatatypeRuleToken this_TRANSITION_OPTION_NAME_2 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4239:28: ( (kw= 'AsDefault' | kw= 'OverrideAttributeValidation' | this_TRANSITION_OPTION_NAME_2= ruleTRANSITION_OPTION_NAME ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4240:1: (kw= 'AsDefault' | kw= 'OverrideAttributeValidation' | this_TRANSITION_OPTION_NAME_2= ruleTRANSITION_OPTION_NAME )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4240:1: (kw= 'AsDefault' | kw= 'OverrideAttributeValidation' | this_TRANSITION_OPTION_NAME_2= ruleTRANSITION_OPTION_NAME )
            int alt105=3;
            switch ( input.LA(1) ) {
            case 87:
                {
                alt105=1;
                }
                break;
            case 88:
                {
                alt105=2;
                }
                break;
            case RULE_STRING:
                {
                alt105=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 105, 0, input);

                throw nvae;
            }

            switch (alt105) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4241:2: kw= 'AsDefault'
                    {
                    kw=(Token)match(input,87,FOLLOW_87_in_ruleTransitionOption9327); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getTransitionOptionAccess().getAsDefaultKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4248:2: kw= 'OverrideAttributeValidation'
                    {
                    kw=(Token)match(input,88,FOLLOW_88_in_ruleTransitionOption9346); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getTransitionOptionAccess().getOverrideAttributeValidationKeyword_1()); 
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4255:5: this_TRANSITION_OPTION_NAME_2= ruleTRANSITION_OPTION_NAME
                    {
                     
                            newCompositeNode(grammarAccess.getTransitionOptionAccess().getTRANSITION_OPTION_NAMEParserRuleCall_2()); 
                        
                    pushFollow(FOLLOW_ruleTRANSITION_OPTION_NAME_in_ruleTransitionOption9374);
                    this_TRANSITION_OPTION_NAME_2=ruleTRANSITION_OPTION_NAME();

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
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleTransitionOption"


    // $ANTLR start "entryRuleRULE_NAME"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4273:1: entryRuleRULE_NAME returns [String current=null] : iv_ruleRULE_NAME= ruleRULE_NAME EOF ;
    public final String entryRuleRULE_NAME() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleRULE_NAME = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4274:2: (iv_ruleRULE_NAME= ruleRULE_NAME EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4275:2: iv_ruleRULE_NAME= ruleRULE_NAME EOF
            {
             newCompositeNode(grammarAccess.getRULE_NAMERule()); 
            pushFollow(FOLLOW_ruleRULE_NAME_in_entryRuleRULE_NAME9420);
            iv_ruleRULE_NAME=ruleRULE_NAME();

            state._fsp--;

             current =iv_ruleRULE_NAME.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleRULE_NAME9431); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleRULE_NAME"


    // $ANTLR start "ruleRULE_NAME"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4282:1: ruleRULE_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleRULE_NAME() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4285:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4286:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleRULE_NAME9470); 

            		current.merge(this_STRING_0);
                
             
                newLeafNode(this_STRING_0, grammarAccess.getRULE_NAMEAccess().getSTRINGTerminalRuleCall()); 
                

            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleRULE_NAME"


    // $ANTLR start "entryRuleRule"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4301:1: entryRuleRule returns [String current=null] : iv_ruleRule= ruleRule EOF ;
    public final String entryRuleRule() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleRule = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4302:2: (iv_ruleRule= ruleRule EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4303:2: iv_ruleRule= ruleRule EOF
            {
             newCompositeNode(grammarAccess.getRuleRule()); 
            pushFollow(FOLLOW_ruleRule_in_entryRuleRule9515);
            iv_ruleRule=ruleRule();

            state._fsp--;

             current =iv_ruleRule.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleRule9526); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleRule"


    // $ANTLR start "ruleRule"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4310:1: ruleRule returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'RequireStateHourSpentPrompt' | kw= 'AddDecisionValidateBlockingReview' | kw= 'AddDecisionValidateNonBlockingReview' | kw= 'AllowTransitionWithWorkingBranch' | kw= 'ForceAssigneesToTeamLeads' | kw= 'RequireTargetedVersion' | kw= 'AllowPrivilegedEditToTeamMember' | kw= 'AllowPrivilegedEditToTeamMemberAndOriginator' | kw= 'AllowPrivilegedEditToAll' | kw= 'AllowEditToAll' | kw= 'AllowAssigneeToAll' | kw= 'AllowTransitionWithoutTaskCompletion' | this_RULE_NAME_12= ruleRULE_NAME ) ;
    public final AntlrDatatypeRuleToken ruleRule() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        AntlrDatatypeRuleToken this_RULE_NAME_12 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4313:28: ( (kw= 'RequireStateHourSpentPrompt' | kw= 'AddDecisionValidateBlockingReview' | kw= 'AddDecisionValidateNonBlockingReview' | kw= 'AllowTransitionWithWorkingBranch' | kw= 'ForceAssigneesToTeamLeads' | kw= 'RequireTargetedVersion' | kw= 'AllowPrivilegedEditToTeamMember' | kw= 'AllowPrivilegedEditToTeamMemberAndOriginator' | kw= 'AllowPrivilegedEditToAll' | kw= 'AllowEditToAll' | kw= 'AllowAssigneeToAll' | kw= 'AllowTransitionWithoutTaskCompletion' | this_RULE_NAME_12= ruleRULE_NAME ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4314:1: (kw= 'RequireStateHourSpentPrompt' | kw= 'AddDecisionValidateBlockingReview' | kw= 'AddDecisionValidateNonBlockingReview' | kw= 'AllowTransitionWithWorkingBranch' | kw= 'ForceAssigneesToTeamLeads' | kw= 'RequireTargetedVersion' | kw= 'AllowPrivilegedEditToTeamMember' | kw= 'AllowPrivilegedEditToTeamMemberAndOriginator' | kw= 'AllowPrivilegedEditToAll' | kw= 'AllowEditToAll' | kw= 'AllowAssigneeToAll' | kw= 'AllowTransitionWithoutTaskCompletion' | this_RULE_NAME_12= ruleRULE_NAME )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4314:1: (kw= 'RequireStateHourSpentPrompt' | kw= 'AddDecisionValidateBlockingReview' | kw= 'AddDecisionValidateNonBlockingReview' | kw= 'AllowTransitionWithWorkingBranch' | kw= 'ForceAssigneesToTeamLeads' | kw= 'RequireTargetedVersion' | kw= 'AllowPrivilegedEditToTeamMember' | kw= 'AllowPrivilegedEditToTeamMemberAndOriginator' | kw= 'AllowPrivilegedEditToAll' | kw= 'AllowEditToAll' | kw= 'AllowAssigneeToAll' | kw= 'AllowTransitionWithoutTaskCompletion' | this_RULE_NAME_12= ruleRULE_NAME )
            int alt106=13;
            switch ( input.LA(1) ) {
            case 89:
                {
                alt106=1;
                }
                break;
            case 90:
                {
                alt106=2;
                }
                break;
            case 91:
                {
                alt106=3;
                }
                break;
            case 92:
                {
                alt106=4;
                }
                break;
            case 93:
                {
                alt106=5;
                }
                break;
            case 94:
                {
                alt106=6;
                }
                break;
            case 95:
                {
                alt106=7;
                }
                break;
            case 96:
                {
                alt106=8;
                }
                break;
            case 97:
                {
                alt106=9;
                }
                break;
            case 98:
                {
                alt106=10;
                }
                break;
            case 99:
                {
                alt106=11;
                }
                break;
            case 100:
                {
                alt106=12;
                }
                break;
            case RULE_STRING:
                {
                alt106=13;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 106, 0, input);

                throw nvae;
            }

            switch (alt106) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4315:2: kw= 'RequireStateHourSpentPrompt'
                    {
                    kw=(Token)match(input,89,FOLLOW_89_in_ruleRule9564); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleAccess().getRequireStateHourSpentPromptKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4322:2: kw= 'AddDecisionValidateBlockingReview'
                    {
                    kw=(Token)match(input,90,FOLLOW_90_in_ruleRule9583); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleAccess().getAddDecisionValidateBlockingReviewKeyword_1()); 
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4329:2: kw= 'AddDecisionValidateNonBlockingReview'
                    {
                    kw=(Token)match(input,91,FOLLOW_91_in_ruleRule9602); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleAccess().getAddDecisionValidateNonBlockingReviewKeyword_2()); 
                        

                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4336:2: kw= 'AllowTransitionWithWorkingBranch'
                    {
                    kw=(Token)match(input,92,FOLLOW_92_in_ruleRule9621); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleAccess().getAllowTransitionWithWorkingBranchKeyword_3()); 
                        

                    }
                    break;
                case 5 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4343:2: kw= 'ForceAssigneesToTeamLeads'
                    {
                    kw=(Token)match(input,93,FOLLOW_93_in_ruleRule9640); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleAccess().getForceAssigneesToTeamLeadsKeyword_4()); 
                        

                    }
                    break;
                case 6 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4350:2: kw= 'RequireTargetedVersion'
                    {
                    kw=(Token)match(input,94,FOLLOW_94_in_ruleRule9659); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleAccess().getRequireTargetedVersionKeyword_5()); 
                        

                    }
                    break;
                case 7 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4357:2: kw= 'AllowPrivilegedEditToTeamMember'
                    {
                    kw=(Token)match(input,95,FOLLOW_95_in_ruleRule9678); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleAccess().getAllowPrivilegedEditToTeamMemberKeyword_6()); 
                        

                    }
                    break;
                case 8 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4364:2: kw= 'AllowPrivilegedEditToTeamMemberAndOriginator'
                    {
                    kw=(Token)match(input,96,FOLLOW_96_in_ruleRule9697); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleAccess().getAllowPrivilegedEditToTeamMemberAndOriginatorKeyword_7()); 
                        

                    }
                    break;
                case 9 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4371:2: kw= 'AllowPrivilegedEditToAll'
                    {
                    kw=(Token)match(input,97,FOLLOW_97_in_ruleRule9716); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleAccess().getAllowPrivilegedEditToAllKeyword_8()); 
                        

                    }
                    break;
                case 10 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4378:2: kw= 'AllowEditToAll'
                    {
                    kw=(Token)match(input,98,FOLLOW_98_in_ruleRule9735); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleAccess().getAllowEditToAllKeyword_9()); 
                        

                    }
                    break;
                case 11 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4385:2: kw= 'AllowAssigneeToAll'
                    {
                    kw=(Token)match(input,99,FOLLOW_99_in_ruleRule9754); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleAccess().getAllowAssigneeToAllKeyword_10()); 
                        

                    }
                    break;
                case 12 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4392:2: kw= 'AllowTransitionWithoutTaskCompletion'
                    {
                    kw=(Token)match(input,100,FOLLOW_100_in_ruleRule9773); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleAccess().getAllowTransitionWithoutTaskCompletionKeyword_11()); 
                        

                    }
                    break;
                case 13 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4399:5: this_RULE_NAME_12= ruleRULE_NAME
                    {
                     
                            newCompositeNode(grammarAccess.getRuleAccess().getRULE_NAMEParserRuleCall_12()); 
                        
                    pushFollow(FOLLOW_ruleRULE_NAME_in_ruleRule9801);
                    this_RULE_NAME_12=ruleRULE_NAME();

                    state._fsp--;


                    		current.merge(this_RULE_NAME_12);
                        
                     
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleRule"


    // $ANTLR start "entryRuleWIDGET_OPTION_NAME"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4417:1: entryRuleWIDGET_OPTION_NAME returns [String current=null] : iv_ruleWIDGET_OPTION_NAME= ruleWIDGET_OPTION_NAME EOF ;
    public final String entryRuleWIDGET_OPTION_NAME() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleWIDGET_OPTION_NAME = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4418:2: (iv_ruleWIDGET_OPTION_NAME= ruleWIDGET_OPTION_NAME EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4419:2: iv_ruleWIDGET_OPTION_NAME= ruleWIDGET_OPTION_NAME EOF
            {
             newCompositeNode(grammarAccess.getWIDGET_OPTION_NAMERule()); 
            pushFollow(FOLLOW_ruleWIDGET_OPTION_NAME_in_entryRuleWIDGET_OPTION_NAME9847);
            iv_ruleWIDGET_OPTION_NAME=ruleWIDGET_OPTION_NAME();

            state._fsp--;

             current =iv_ruleWIDGET_OPTION_NAME.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleWIDGET_OPTION_NAME9858); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleWIDGET_OPTION_NAME"


    // $ANTLR start "ruleWIDGET_OPTION_NAME"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4426:1: ruleWIDGET_OPTION_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleWIDGET_OPTION_NAME() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4429:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4430:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleWIDGET_OPTION_NAME9897); 

            		current.merge(this_STRING_0);
                
             
                newLeafNode(this_STRING_0, grammarAccess.getWIDGET_OPTION_NAMEAccess().getSTRINGTerminalRuleCall()); 
                

            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleWIDGET_OPTION_NAME"


    // $ANTLR start "entryRuleWidgetOption"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4445:1: entryRuleWidgetOption returns [String current=null] : iv_ruleWidgetOption= ruleWidgetOption EOF ;
    public final String entryRuleWidgetOption() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleWidgetOption = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4446:2: (iv_ruleWidgetOption= ruleWidgetOption EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4447:2: iv_ruleWidgetOption= ruleWidgetOption EOF
            {
             newCompositeNode(grammarAccess.getWidgetOptionRule()); 
            pushFollow(FOLLOW_ruleWidgetOption_in_entryRuleWidgetOption9942);
            iv_ruleWidgetOption=ruleWidgetOption();

            state._fsp--;

             current =iv_ruleWidgetOption.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleWidgetOption9953); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleWidgetOption"


    // $ANTLR start "ruleWidgetOption"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4454:1: ruleWidgetOption returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'REQUIRED_FOR_TRANSITION' | kw= 'NOT_REQUIRED_FOR_TRANSITION' | kw= 'REQUIRED_FOR_COMPLETION' | kw= 'NOT_REQUIRED_FOR_COMPLETION' | kw= 'ENABLED' | kw= 'NOT_ENABLED' | kw= 'EDITABLE' | kw= 'NOT_EDITABLE' | kw= 'FUTURE_DATE_REQUIRED' | kw= 'NOT_FUTURE_DATE_REQUIRED' | kw= 'MULTI_SELECT' | kw= 'HORIZONTAL_LABEL' | kw= 'VERTICAL_LABEL' | kw= 'LABEL_AFTER' | kw= 'LABEL_BEFORE' | kw= 'NO_LABEL' | kw= 'SORTED' | kw= 'ADD_DEFAULT_VALUE' | kw= 'NO_DEFAULT_VALUE' | kw= 'BEGIN_COMPOSITE_4' | kw= 'BEGIN_COMPOSITE_6' | kw= 'BEGIN_COMPOSITE_8' | kw= 'BEGIN_COMPOSITE_10' | kw= 'END_COMPOSITE' | kw= 'FILL_NONE' | kw= 'FILL_HORIZONTALLY' | kw= 'FILL_VERTICALLY' | kw= 'ALIGN_LEFT' | kw= 'ALIGN_RIGHT' | kw= 'ALIGN_CENTER' | this_WIDGET_OPTION_NAME_30= ruleWIDGET_OPTION_NAME ) ;
    public final AntlrDatatypeRuleToken ruleWidgetOption() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        AntlrDatatypeRuleToken this_WIDGET_OPTION_NAME_30 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4457:28: ( (kw= 'REQUIRED_FOR_TRANSITION' | kw= 'NOT_REQUIRED_FOR_TRANSITION' | kw= 'REQUIRED_FOR_COMPLETION' | kw= 'NOT_REQUIRED_FOR_COMPLETION' | kw= 'ENABLED' | kw= 'NOT_ENABLED' | kw= 'EDITABLE' | kw= 'NOT_EDITABLE' | kw= 'FUTURE_DATE_REQUIRED' | kw= 'NOT_FUTURE_DATE_REQUIRED' | kw= 'MULTI_SELECT' | kw= 'HORIZONTAL_LABEL' | kw= 'VERTICAL_LABEL' | kw= 'LABEL_AFTER' | kw= 'LABEL_BEFORE' | kw= 'NO_LABEL' | kw= 'SORTED' | kw= 'ADD_DEFAULT_VALUE' | kw= 'NO_DEFAULT_VALUE' | kw= 'BEGIN_COMPOSITE_4' | kw= 'BEGIN_COMPOSITE_6' | kw= 'BEGIN_COMPOSITE_8' | kw= 'BEGIN_COMPOSITE_10' | kw= 'END_COMPOSITE' | kw= 'FILL_NONE' | kw= 'FILL_HORIZONTALLY' | kw= 'FILL_VERTICALLY' | kw= 'ALIGN_LEFT' | kw= 'ALIGN_RIGHT' | kw= 'ALIGN_CENTER' | this_WIDGET_OPTION_NAME_30= ruleWIDGET_OPTION_NAME ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4458:1: (kw= 'REQUIRED_FOR_TRANSITION' | kw= 'NOT_REQUIRED_FOR_TRANSITION' | kw= 'REQUIRED_FOR_COMPLETION' | kw= 'NOT_REQUIRED_FOR_COMPLETION' | kw= 'ENABLED' | kw= 'NOT_ENABLED' | kw= 'EDITABLE' | kw= 'NOT_EDITABLE' | kw= 'FUTURE_DATE_REQUIRED' | kw= 'NOT_FUTURE_DATE_REQUIRED' | kw= 'MULTI_SELECT' | kw= 'HORIZONTAL_LABEL' | kw= 'VERTICAL_LABEL' | kw= 'LABEL_AFTER' | kw= 'LABEL_BEFORE' | kw= 'NO_LABEL' | kw= 'SORTED' | kw= 'ADD_DEFAULT_VALUE' | kw= 'NO_DEFAULT_VALUE' | kw= 'BEGIN_COMPOSITE_4' | kw= 'BEGIN_COMPOSITE_6' | kw= 'BEGIN_COMPOSITE_8' | kw= 'BEGIN_COMPOSITE_10' | kw= 'END_COMPOSITE' | kw= 'FILL_NONE' | kw= 'FILL_HORIZONTALLY' | kw= 'FILL_VERTICALLY' | kw= 'ALIGN_LEFT' | kw= 'ALIGN_RIGHT' | kw= 'ALIGN_CENTER' | this_WIDGET_OPTION_NAME_30= ruleWIDGET_OPTION_NAME )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4458:1: (kw= 'REQUIRED_FOR_TRANSITION' | kw= 'NOT_REQUIRED_FOR_TRANSITION' | kw= 'REQUIRED_FOR_COMPLETION' | kw= 'NOT_REQUIRED_FOR_COMPLETION' | kw= 'ENABLED' | kw= 'NOT_ENABLED' | kw= 'EDITABLE' | kw= 'NOT_EDITABLE' | kw= 'FUTURE_DATE_REQUIRED' | kw= 'NOT_FUTURE_DATE_REQUIRED' | kw= 'MULTI_SELECT' | kw= 'HORIZONTAL_LABEL' | kw= 'VERTICAL_LABEL' | kw= 'LABEL_AFTER' | kw= 'LABEL_BEFORE' | kw= 'NO_LABEL' | kw= 'SORTED' | kw= 'ADD_DEFAULT_VALUE' | kw= 'NO_DEFAULT_VALUE' | kw= 'BEGIN_COMPOSITE_4' | kw= 'BEGIN_COMPOSITE_6' | kw= 'BEGIN_COMPOSITE_8' | kw= 'BEGIN_COMPOSITE_10' | kw= 'END_COMPOSITE' | kw= 'FILL_NONE' | kw= 'FILL_HORIZONTALLY' | kw= 'FILL_VERTICALLY' | kw= 'ALIGN_LEFT' | kw= 'ALIGN_RIGHT' | kw= 'ALIGN_CENTER' | this_WIDGET_OPTION_NAME_30= ruleWIDGET_OPTION_NAME )
            int alt107=31;
            switch ( input.LA(1) ) {
            case 101:
                {
                alt107=1;
                }
                break;
            case 102:
                {
                alt107=2;
                }
                break;
            case 103:
                {
                alt107=3;
                }
                break;
            case 104:
                {
                alt107=4;
                }
                break;
            case 105:
                {
                alt107=5;
                }
                break;
            case 106:
                {
                alt107=6;
                }
                break;
            case 107:
                {
                alt107=7;
                }
                break;
            case 108:
                {
                alt107=8;
                }
                break;
            case 109:
                {
                alt107=9;
                }
                break;
            case 110:
                {
                alt107=10;
                }
                break;
            case 111:
                {
                alt107=11;
                }
                break;
            case 112:
                {
                alt107=12;
                }
                break;
            case 113:
                {
                alt107=13;
                }
                break;
            case 114:
                {
                alt107=14;
                }
                break;
            case 115:
                {
                alt107=15;
                }
                break;
            case 116:
                {
                alt107=16;
                }
                break;
            case 117:
                {
                alt107=17;
                }
                break;
            case 118:
                {
                alt107=18;
                }
                break;
            case 119:
                {
                alt107=19;
                }
                break;
            case 120:
                {
                alt107=20;
                }
                break;
            case 121:
                {
                alt107=21;
                }
                break;
            case 122:
                {
                alt107=22;
                }
                break;
            case 123:
                {
                alt107=23;
                }
                break;
            case 124:
                {
                alt107=24;
                }
                break;
            case 125:
                {
                alt107=25;
                }
                break;
            case 126:
                {
                alt107=26;
                }
                break;
            case 127:
                {
                alt107=27;
                }
                break;
            case 128:
                {
                alt107=28;
                }
                break;
            case 129:
                {
                alt107=29;
                }
                break;
            case 130:
                {
                alt107=30;
                }
                break;
            case RULE_STRING:
                {
                alt107=31;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 107, 0, input);

                throw nvae;
            }

            switch (alt107) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4459:2: kw= 'REQUIRED_FOR_TRANSITION'
                    {
                    kw=(Token)match(input,101,FOLLOW_101_in_ruleWidgetOption9991); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getREQUIRED_FOR_TRANSITIONKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4466:2: kw= 'NOT_REQUIRED_FOR_TRANSITION'
                    {
                    kw=(Token)match(input,102,FOLLOW_102_in_ruleWidgetOption10010); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getNOT_REQUIRED_FOR_TRANSITIONKeyword_1()); 
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4473:2: kw= 'REQUIRED_FOR_COMPLETION'
                    {
                    kw=(Token)match(input,103,FOLLOW_103_in_ruleWidgetOption10029); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getREQUIRED_FOR_COMPLETIONKeyword_2()); 
                        

                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4480:2: kw= 'NOT_REQUIRED_FOR_COMPLETION'
                    {
                    kw=(Token)match(input,104,FOLLOW_104_in_ruleWidgetOption10048); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getNOT_REQUIRED_FOR_COMPLETIONKeyword_3()); 
                        

                    }
                    break;
                case 5 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4487:2: kw= 'ENABLED'
                    {
                    kw=(Token)match(input,105,FOLLOW_105_in_ruleWidgetOption10067); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getENABLEDKeyword_4()); 
                        

                    }
                    break;
                case 6 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4494:2: kw= 'NOT_ENABLED'
                    {
                    kw=(Token)match(input,106,FOLLOW_106_in_ruleWidgetOption10086); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getNOT_ENABLEDKeyword_5()); 
                        

                    }
                    break;
                case 7 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4501:2: kw= 'EDITABLE'
                    {
                    kw=(Token)match(input,107,FOLLOW_107_in_ruleWidgetOption10105); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getEDITABLEKeyword_6()); 
                        

                    }
                    break;
                case 8 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4508:2: kw= 'NOT_EDITABLE'
                    {
                    kw=(Token)match(input,108,FOLLOW_108_in_ruleWidgetOption10124); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getNOT_EDITABLEKeyword_7()); 
                        

                    }
                    break;
                case 9 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4515:2: kw= 'FUTURE_DATE_REQUIRED'
                    {
                    kw=(Token)match(input,109,FOLLOW_109_in_ruleWidgetOption10143); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getFUTURE_DATE_REQUIREDKeyword_8()); 
                        

                    }
                    break;
                case 10 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4522:2: kw= 'NOT_FUTURE_DATE_REQUIRED'
                    {
                    kw=(Token)match(input,110,FOLLOW_110_in_ruleWidgetOption10162); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getNOT_FUTURE_DATE_REQUIREDKeyword_9()); 
                        

                    }
                    break;
                case 11 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4529:2: kw= 'MULTI_SELECT'
                    {
                    kw=(Token)match(input,111,FOLLOW_111_in_ruleWidgetOption10181); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getMULTI_SELECTKeyword_10()); 
                        

                    }
                    break;
                case 12 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4536:2: kw= 'HORIZONTAL_LABEL'
                    {
                    kw=(Token)match(input,112,FOLLOW_112_in_ruleWidgetOption10200); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getHORIZONTAL_LABELKeyword_11()); 
                        

                    }
                    break;
                case 13 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4543:2: kw= 'VERTICAL_LABEL'
                    {
                    kw=(Token)match(input,113,FOLLOW_113_in_ruleWidgetOption10219); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getVERTICAL_LABELKeyword_12()); 
                        

                    }
                    break;
                case 14 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4550:2: kw= 'LABEL_AFTER'
                    {
                    kw=(Token)match(input,114,FOLLOW_114_in_ruleWidgetOption10238); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getLABEL_AFTERKeyword_13()); 
                        

                    }
                    break;
                case 15 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4557:2: kw= 'LABEL_BEFORE'
                    {
                    kw=(Token)match(input,115,FOLLOW_115_in_ruleWidgetOption10257); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getLABEL_BEFOREKeyword_14()); 
                        

                    }
                    break;
                case 16 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4564:2: kw= 'NO_LABEL'
                    {
                    kw=(Token)match(input,116,FOLLOW_116_in_ruleWidgetOption10276); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getNO_LABELKeyword_15()); 
                        

                    }
                    break;
                case 17 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4571:2: kw= 'SORTED'
                    {
                    kw=(Token)match(input,117,FOLLOW_117_in_ruleWidgetOption10295); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getSORTEDKeyword_16()); 
                        

                    }
                    break;
                case 18 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4578:2: kw= 'ADD_DEFAULT_VALUE'
                    {
                    kw=(Token)match(input,118,FOLLOW_118_in_ruleWidgetOption10314); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getADD_DEFAULT_VALUEKeyword_17()); 
                        

                    }
                    break;
                case 19 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4585:2: kw= 'NO_DEFAULT_VALUE'
                    {
                    kw=(Token)match(input,119,FOLLOW_119_in_ruleWidgetOption10333); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getNO_DEFAULT_VALUEKeyword_18()); 
                        

                    }
                    break;
                case 20 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4592:2: kw= 'BEGIN_COMPOSITE_4'
                    {
                    kw=(Token)match(input,120,FOLLOW_120_in_ruleWidgetOption10352); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getBEGIN_COMPOSITE_4Keyword_19()); 
                        

                    }
                    break;
                case 21 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4599:2: kw= 'BEGIN_COMPOSITE_6'
                    {
                    kw=(Token)match(input,121,FOLLOW_121_in_ruleWidgetOption10371); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getBEGIN_COMPOSITE_6Keyword_20()); 
                        

                    }
                    break;
                case 22 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4606:2: kw= 'BEGIN_COMPOSITE_8'
                    {
                    kw=(Token)match(input,122,FOLLOW_122_in_ruleWidgetOption10390); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getBEGIN_COMPOSITE_8Keyword_21()); 
                        

                    }
                    break;
                case 23 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4613:2: kw= 'BEGIN_COMPOSITE_10'
                    {
                    kw=(Token)match(input,123,FOLLOW_123_in_ruleWidgetOption10409); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getBEGIN_COMPOSITE_10Keyword_22()); 
                        

                    }
                    break;
                case 24 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4620:2: kw= 'END_COMPOSITE'
                    {
                    kw=(Token)match(input,124,FOLLOW_124_in_ruleWidgetOption10428); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getEND_COMPOSITEKeyword_23()); 
                        

                    }
                    break;
                case 25 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4627:2: kw= 'FILL_NONE'
                    {
                    kw=(Token)match(input,125,FOLLOW_125_in_ruleWidgetOption10447); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getFILL_NONEKeyword_24()); 
                        

                    }
                    break;
                case 26 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4634:2: kw= 'FILL_HORIZONTALLY'
                    {
                    kw=(Token)match(input,126,FOLLOW_126_in_ruleWidgetOption10466); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getFILL_HORIZONTALLYKeyword_25()); 
                        

                    }
                    break;
                case 27 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4641:2: kw= 'FILL_VERTICALLY'
                    {
                    kw=(Token)match(input,127,FOLLOW_127_in_ruleWidgetOption10485); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getFILL_VERTICALLYKeyword_26()); 
                        

                    }
                    break;
                case 28 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4648:2: kw= 'ALIGN_LEFT'
                    {
                    kw=(Token)match(input,128,FOLLOW_128_in_ruleWidgetOption10504); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getALIGN_LEFTKeyword_27()); 
                        

                    }
                    break;
                case 29 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4655:2: kw= 'ALIGN_RIGHT'
                    {
                    kw=(Token)match(input,129,FOLLOW_129_in_ruleWidgetOption10523); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getALIGN_RIGHTKeyword_28()); 
                        

                    }
                    break;
                case 30 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4662:2: kw= 'ALIGN_CENTER'
                    {
                    kw=(Token)match(input,130,FOLLOW_130_in_ruleWidgetOption10542); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getALIGN_CENTERKeyword_29()); 
                        

                    }
                    break;
                case 31 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4669:5: this_WIDGET_OPTION_NAME_30= ruleWIDGET_OPTION_NAME
                    {
                     
                            newCompositeNode(grammarAccess.getWidgetOptionAccess().getWIDGET_OPTION_NAMEParserRuleCall_30()); 
                        
                    pushFollow(FOLLOW_ruleWIDGET_OPTION_NAME_in_ruleWidgetOption10570);
                    this_WIDGET_OPTION_NAME_30=ruleWIDGET_OPTION_NAME();

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
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleWidgetOption"


    // $ANTLR start "entryRulePAGE_TYPE_NAME"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4687:1: entryRulePAGE_TYPE_NAME returns [String current=null] : iv_rulePAGE_TYPE_NAME= rulePAGE_TYPE_NAME EOF ;
    public final String entryRulePAGE_TYPE_NAME() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_rulePAGE_TYPE_NAME = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4688:2: (iv_rulePAGE_TYPE_NAME= rulePAGE_TYPE_NAME EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4689:2: iv_rulePAGE_TYPE_NAME= rulePAGE_TYPE_NAME EOF
            {
             newCompositeNode(grammarAccess.getPAGE_TYPE_NAMERule()); 
            pushFollow(FOLLOW_rulePAGE_TYPE_NAME_in_entryRulePAGE_TYPE_NAME10616);
            iv_rulePAGE_TYPE_NAME=rulePAGE_TYPE_NAME();

            state._fsp--;

             current =iv_rulePAGE_TYPE_NAME.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRulePAGE_TYPE_NAME10627); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRulePAGE_TYPE_NAME"


    // $ANTLR start "rulePAGE_TYPE_NAME"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4696:1: rulePAGE_TYPE_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken rulePAGE_TYPE_NAME() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4699:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4700:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rulePAGE_TYPE_NAME10666); 

            		current.merge(this_STRING_0);
                
             
                newLeafNode(this_STRING_0, grammarAccess.getPAGE_TYPE_NAMEAccess().getSTRINGTerminalRuleCall()); 
                

            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "rulePAGE_TYPE_NAME"


    // $ANTLR start "entryRulePageType"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4715:1: entryRulePageType returns [String current=null] : iv_rulePageType= rulePageType EOF ;
    public final String entryRulePageType() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_rulePageType = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4716:2: (iv_rulePageType= rulePageType EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4717:2: iv_rulePageType= rulePageType EOF
            {
             newCompositeNode(grammarAccess.getPageTypeRule()); 
            pushFollow(FOLLOW_rulePageType_in_entryRulePageType10711);
            iv_rulePageType=rulePageType();

            state._fsp--;

             current =iv_rulePageType.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRulePageType10722); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRulePageType"


    // $ANTLR start "rulePageType"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4724:1: rulePageType returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'Working' | kw= 'Completed' | kw= 'Cancelled' | this_PAGE_TYPE_NAME_3= rulePAGE_TYPE_NAME ) ;
    public final AntlrDatatypeRuleToken rulePageType() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        AntlrDatatypeRuleToken this_PAGE_TYPE_NAME_3 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4727:28: ( (kw= 'Working' | kw= 'Completed' | kw= 'Cancelled' | this_PAGE_TYPE_NAME_3= rulePAGE_TYPE_NAME ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4728:1: (kw= 'Working' | kw= 'Completed' | kw= 'Cancelled' | this_PAGE_TYPE_NAME_3= rulePAGE_TYPE_NAME )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4728:1: (kw= 'Working' | kw= 'Completed' | kw= 'Cancelled' | this_PAGE_TYPE_NAME_3= rulePAGE_TYPE_NAME )
            int alt108=4;
            switch ( input.LA(1) ) {
            case 131:
                {
                alt108=1;
                }
                break;
            case 132:
                {
                alt108=2;
                }
                break;
            case 133:
                {
                alt108=3;
                }
                break;
            case RULE_STRING:
                {
                alt108=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 108, 0, input);

                throw nvae;
            }

            switch (alt108) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4729:2: kw= 'Working'
                    {
                    kw=(Token)match(input,131,FOLLOW_131_in_rulePageType10760); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getPageTypeAccess().getWorkingKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4736:2: kw= 'Completed'
                    {
                    kw=(Token)match(input,132,FOLLOW_132_in_rulePageType10779); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getPageTypeAccess().getCompletedKeyword_1()); 
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4743:2: kw= 'Cancelled'
                    {
                    kw=(Token)match(input,133,FOLLOW_133_in_rulePageType10798); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getPageTypeAccess().getCancelledKeyword_2()); 
                        

                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4750:5: this_PAGE_TYPE_NAME_3= rulePAGE_TYPE_NAME
                    {
                     
                            newCompositeNode(grammarAccess.getPageTypeAccess().getPAGE_TYPE_NAMEParserRuleCall_3()); 
                        
                    pushFollow(FOLLOW_rulePAGE_TYPE_NAME_in_rulePageType10826);
                    this_PAGE_TYPE_NAME_3=rulePAGE_TYPE_NAME();

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
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "rulePageType"


    // $ANTLR start "entryRuleCOLOR_NAME"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4768:1: entryRuleCOLOR_NAME returns [String current=null] : iv_ruleCOLOR_NAME= ruleCOLOR_NAME EOF ;
    public final String entryRuleCOLOR_NAME() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleCOLOR_NAME = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4769:2: (iv_ruleCOLOR_NAME= ruleCOLOR_NAME EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4770:2: iv_ruleCOLOR_NAME= ruleCOLOR_NAME EOF
            {
             newCompositeNode(grammarAccess.getCOLOR_NAMERule()); 
            pushFollow(FOLLOW_ruleCOLOR_NAME_in_entryRuleCOLOR_NAME10872);
            iv_ruleCOLOR_NAME=ruleCOLOR_NAME();

            state._fsp--;

             current =iv_ruleCOLOR_NAME.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleCOLOR_NAME10883); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleCOLOR_NAME"


    // $ANTLR start "ruleCOLOR_NAME"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4777:1: ruleCOLOR_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleCOLOR_NAME() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4780:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4781:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleCOLOR_NAME10922); 

            		current.merge(this_STRING_0);
                
             
                newLeafNode(this_STRING_0, grammarAccess.getCOLOR_NAMEAccess().getSTRINGTerminalRuleCall()); 
                

            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleCOLOR_NAME"


    // $ANTLR start "entryRuleStateColor"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4796:1: entryRuleStateColor returns [String current=null] : iv_ruleStateColor= ruleStateColor EOF ;
    public final String entryRuleStateColor() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleStateColor = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4797:2: (iv_ruleStateColor= ruleStateColor EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4798:2: iv_ruleStateColor= ruleStateColor EOF
            {
             newCompositeNode(grammarAccess.getStateColorRule()); 
            pushFollow(FOLLOW_ruleStateColor_in_entryRuleStateColor10967);
            iv_ruleStateColor=ruleStateColor();

            state._fsp--;

             current =iv_ruleStateColor.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleStateColor10978); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleStateColor"


    // $ANTLR start "ruleStateColor"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4805:1: ruleStateColor returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'BLACK' | kw= 'WHITE' | kw= 'RED' | kw= 'DARK_RED' | kw= 'GREEN' | kw= 'DARK_GREEN' | kw= 'YELLOW' | kw= 'DARK_YELLOW' | kw= 'BLUE' | kw= 'DARK_BLUE' | kw= 'MAGENTA' | kw= 'DARK_MAGENTA' | kw= 'CYAN' | kw= 'DARK_CYAN' | kw= 'GRAY' | kw= 'DARK_GRAY' | this_COLOR_NAME_16= ruleCOLOR_NAME ) ;
    public final AntlrDatatypeRuleToken ruleStateColor() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        AntlrDatatypeRuleToken this_COLOR_NAME_16 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4808:28: ( (kw= 'BLACK' | kw= 'WHITE' | kw= 'RED' | kw= 'DARK_RED' | kw= 'GREEN' | kw= 'DARK_GREEN' | kw= 'YELLOW' | kw= 'DARK_YELLOW' | kw= 'BLUE' | kw= 'DARK_BLUE' | kw= 'MAGENTA' | kw= 'DARK_MAGENTA' | kw= 'CYAN' | kw= 'DARK_CYAN' | kw= 'GRAY' | kw= 'DARK_GRAY' | this_COLOR_NAME_16= ruleCOLOR_NAME ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4809:1: (kw= 'BLACK' | kw= 'WHITE' | kw= 'RED' | kw= 'DARK_RED' | kw= 'GREEN' | kw= 'DARK_GREEN' | kw= 'YELLOW' | kw= 'DARK_YELLOW' | kw= 'BLUE' | kw= 'DARK_BLUE' | kw= 'MAGENTA' | kw= 'DARK_MAGENTA' | kw= 'CYAN' | kw= 'DARK_CYAN' | kw= 'GRAY' | kw= 'DARK_GRAY' | this_COLOR_NAME_16= ruleCOLOR_NAME )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4809:1: (kw= 'BLACK' | kw= 'WHITE' | kw= 'RED' | kw= 'DARK_RED' | kw= 'GREEN' | kw= 'DARK_GREEN' | kw= 'YELLOW' | kw= 'DARK_YELLOW' | kw= 'BLUE' | kw= 'DARK_BLUE' | kw= 'MAGENTA' | kw= 'DARK_MAGENTA' | kw= 'CYAN' | kw= 'DARK_CYAN' | kw= 'GRAY' | kw= 'DARK_GRAY' | this_COLOR_NAME_16= ruleCOLOR_NAME )
            int alt109=17;
            switch ( input.LA(1) ) {
            case 134:
                {
                alt109=1;
                }
                break;
            case 135:
                {
                alt109=2;
                }
                break;
            case 136:
                {
                alt109=3;
                }
                break;
            case 137:
                {
                alt109=4;
                }
                break;
            case 138:
                {
                alt109=5;
                }
                break;
            case 139:
                {
                alt109=6;
                }
                break;
            case 140:
                {
                alt109=7;
                }
                break;
            case 141:
                {
                alt109=8;
                }
                break;
            case 142:
                {
                alt109=9;
                }
                break;
            case 143:
                {
                alt109=10;
                }
                break;
            case 144:
                {
                alt109=11;
                }
                break;
            case 145:
                {
                alt109=12;
                }
                break;
            case 146:
                {
                alt109=13;
                }
                break;
            case 147:
                {
                alt109=14;
                }
                break;
            case 148:
                {
                alt109=15;
                }
                break;
            case 149:
                {
                alt109=16;
                }
                break;
            case RULE_STRING:
                {
                alt109=17;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 109, 0, input);

                throw nvae;
            }

            switch (alt109) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4810:2: kw= 'BLACK'
                    {
                    kw=(Token)match(input,134,FOLLOW_134_in_ruleStateColor11016); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getStateColorAccess().getBLACKKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4817:2: kw= 'WHITE'
                    {
                    kw=(Token)match(input,135,FOLLOW_135_in_ruleStateColor11035); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getStateColorAccess().getWHITEKeyword_1()); 
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4824:2: kw= 'RED'
                    {
                    kw=(Token)match(input,136,FOLLOW_136_in_ruleStateColor11054); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getStateColorAccess().getREDKeyword_2()); 
                        

                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4831:2: kw= 'DARK_RED'
                    {
                    kw=(Token)match(input,137,FOLLOW_137_in_ruleStateColor11073); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getStateColorAccess().getDARK_REDKeyword_3()); 
                        

                    }
                    break;
                case 5 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4838:2: kw= 'GREEN'
                    {
                    kw=(Token)match(input,138,FOLLOW_138_in_ruleStateColor11092); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getStateColorAccess().getGREENKeyword_4()); 
                        

                    }
                    break;
                case 6 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4845:2: kw= 'DARK_GREEN'
                    {
                    kw=(Token)match(input,139,FOLLOW_139_in_ruleStateColor11111); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getStateColorAccess().getDARK_GREENKeyword_5()); 
                        

                    }
                    break;
                case 7 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4852:2: kw= 'YELLOW'
                    {
                    kw=(Token)match(input,140,FOLLOW_140_in_ruleStateColor11130); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getStateColorAccess().getYELLOWKeyword_6()); 
                        

                    }
                    break;
                case 8 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4859:2: kw= 'DARK_YELLOW'
                    {
                    kw=(Token)match(input,141,FOLLOW_141_in_ruleStateColor11149); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getStateColorAccess().getDARK_YELLOWKeyword_7()); 
                        

                    }
                    break;
                case 9 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4866:2: kw= 'BLUE'
                    {
                    kw=(Token)match(input,142,FOLLOW_142_in_ruleStateColor11168); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getStateColorAccess().getBLUEKeyword_8()); 
                        

                    }
                    break;
                case 10 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4873:2: kw= 'DARK_BLUE'
                    {
                    kw=(Token)match(input,143,FOLLOW_143_in_ruleStateColor11187); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getStateColorAccess().getDARK_BLUEKeyword_9()); 
                        

                    }
                    break;
                case 11 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4880:2: kw= 'MAGENTA'
                    {
                    kw=(Token)match(input,144,FOLLOW_144_in_ruleStateColor11206); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getStateColorAccess().getMAGENTAKeyword_10()); 
                        

                    }
                    break;
                case 12 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4887:2: kw= 'DARK_MAGENTA'
                    {
                    kw=(Token)match(input,145,FOLLOW_145_in_ruleStateColor11225); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getStateColorAccess().getDARK_MAGENTAKeyword_11()); 
                        

                    }
                    break;
                case 13 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4894:2: kw= 'CYAN'
                    {
                    kw=(Token)match(input,146,FOLLOW_146_in_ruleStateColor11244); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getStateColorAccess().getCYANKeyword_12()); 
                        

                    }
                    break;
                case 14 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4901:2: kw= 'DARK_CYAN'
                    {
                    kw=(Token)match(input,147,FOLLOW_147_in_ruleStateColor11263); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getStateColorAccess().getDARK_CYANKeyword_13()); 
                        

                    }
                    break;
                case 15 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4908:2: kw= 'GRAY'
                    {
                    kw=(Token)match(input,148,FOLLOW_148_in_ruleStateColor11282); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getStateColorAccess().getGRAYKeyword_14()); 
                        

                    }
                    break;
                case 16 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4915:2: kw= 'DARK_GRAY'
                    {
                    kw=(Token)match(input,149,FOLLOW_149_in_ruleStateColor11301); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getStateColorAccess().getDARK_GRAYKeyword_15()); 
                        

                    }
                    break;
                case 17 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4922:5: this_COLOR_NAME_16= ruleCOLOR_NAME
                    {
                     
                            newCompositeNode(grammarAccess.getStateColorAccess().getCOLOR_NAMEParserRuleCall_16()); 
                        
                    pushFollow(FOLLOW_ruleCOLOR_NAME_in_ruleStateColor11329);
                    this_COLOR_NAME_16=ruleCOLOR_NAME();

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
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleStateColor"


    // $ANTLR start "ruleBooleanDef"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4940:1: ruleBooleanDef returns [Enumerator current=null] : ( (enumLiteral_0= 'None' ) | (enumLiteral_1= 'True' ) | (enumLiteral_2= 'False' ) ) ;
    public final Enumerator ruleBooleanDef() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;
        Token enumLiteral_2=null;

         enterRule(); 
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4942:28: ( ( (enumLiteral_0= 'None' ) | (enumLiteral_1= 'True' ) | (enumLiteral_2= 'False' ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4943:1: ( (enumLiteral_0= 'None' ) | (enumLiteral_1= 'True' ) | (enumLiteral_2= 'False' ) )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4943:1: ( (enumLiteral_0= 'None' ) | (enumLiteral_1= 'True' ) | (enumLiteral_2= 'False' ) )
            int alt110=3;
            switch ( input.LA(1) ) {
            case 86:
                {
                alt110=1;
                }
                break;
            case 150:
                {
                alt110=2;
                }
                break;
            case 151:
                {
                alt110=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 110, 0, input);

                throw nvae;
            }

            switch (alt110) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4943:2: (enumLiteral_0= 'None' )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4943:2: (enumLiteral_0= 'None' )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4943:4: enumLiteral_0= 'None'
                    {
                    enumLiteral_0=(Token)match(input,86,FOLLOW_86_in_ruleBooleanDef11388); 

                            current = grammarAccess.getBooleanDefAccess().getNoneEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_0, grammarAccess.getBooleanDefAccess().getNoneEnumLiteralDeclaration_0()); 
                        

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4949:6: (enumLiteral_1= 'True' )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4949:6: (enumLiteral_1= 'True' )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4949:8: enumLiteral_1= 'True'
                    {
                    enumLiteral_1=(Token)match(input,150,FOLLOW_150_in_ruleBooleanDef11405); 

                            current = grammarAccess.getBooleanDefAccess().getTrueEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_1, grammarAccess.getBooleanDefAccess().getTrueEnumLiteralDeclaration_1()); 
                        

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4955:6: (enumLiteral_2= 'False' )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4955:6: (enumLiteral_2= 'False' )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4955:8: enumLiteral_2= 'False'
                    {
                    enumLiteral_2=(Token)match(input,151,FOLLOW_151_in_ruleBooleanDef11422); 

                            current = grammarAccess.getBooleanDefAccess().getFalseEnumLiteralDeclaration_2().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_2, grammarAccess.getBooleanDefAccess().getFalseEnumLiteralDeclaration_2()); 
                        

                    }


                    }
                    break;

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleBooleanDef"


    // $ANTLR start "ruleWorkflowEventType"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4965:1: ruleWorkflowEventType returns [Enumerator current=null] : ( (enumLiteral_0= 'TransitionTo' ) | (enumLiteral_1= 'CreateBranch' ) | (enumLiteral_2= 'CommitBranch' ) ) ;
    public final Enumerator ruleWorkflowEventType() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;
        Token enumLiteral_2=null;

         enterRule(); 
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4967:28: ( ( (enumLiteral_0= 'TransitionTo' ) | (enumLiteral_1= 'CreateBranch' ) | (enumLiteral_2= 'CommitBranch' ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4968:1: ( (enumLiteral_0= 'TransitionTo' ) | (enumLiteral_1= 'CreateBranch' ) | (enumLiteral_2= 'CommitBranch' ) )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4968:1: ( (enumLiteral_0= 'TransitionTo' ) | (enumLiteral_1= 'CreateBranch' ) | (enumLiteral_2= 'CommitBranch' ) )
            int alt111=3;
            switch ( input.LA(1) ) {
            case 152:
                {
                alt111=1;
                }
                break;
            case 153:
                {
                alt111=2;
                }
                break;
            case 154:
                {
                alt111=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 111, 0, input);

                throw nvae;
            }

            switch (alt111) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4968:2: (enumLiteral_0= 'TransitionTo' )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4968:2: (enumLiteral_0= 'TransitionTo' )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4968:4: enumLiteral_0= 'TransitionTo'
                    {
                    enumLiteral_0=(Token)match(input,152,FOLLOW_152_in_ruleWorkflowEventType11467); 

                            current = grammarAccess.getWorkflowEventTypeAccess().getTransitionToEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_0, grammarAccess.getWorkflowEventTypeAccess().getTransitionToEnumLiteralDeclaration_0()); 
                        

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4974:6: (enumLiteral_1= 'CreateBranch' )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4974:6: (enumLiteral_1= 'CreateBranch' )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4974:8: enumLiteral_1= 'CreateBranch'
                    {
                    enumLiteral_1=(Token)match(input,153,FOLLOW_153_in_ruleWorkflowEventType11484); 

                            current = grammarAccess.getWorkflowEventTypeAccess().getCreateBranchEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_1, grammarAccess.getWorkflowEventTypeAccess().getCreateBranchEnumLiteralDeclaration_1()); 
                        

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4980:6: (enumLiteral_2= 'CommitBranch' )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4980:6: (enumLiteral_2= 'CommitBranch' )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4980:8: enumLiteral_2= 'CommitBranch'
                    {
                    enumLiteral_2=(Token)match(input,154,FOLLOW_154_in_ruleWorkflowEventType11501); 

                            current = grammarAccess.getWorkflowEventTypeAccess().getCommitBranchEnumLiteralDeclaration_2().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_2, grammarAccess.getWorkflowEventTypeAccess().getCommitBranchEnumLiteralDeclaration_2()); 
                        

                    }


                    }
                    break;

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleWorkflowEventType"


    // $ANTLR start "ruleReviewBlockingType"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4990:1: ruleReviewBlockingType returns [Enumerator current=null] : ( (enumLiteral_0= 'Transition' ) | (enumLiteral_1= 'Commit' ) ) ;
    public final Enumerator ruleReviewBlockingType() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;

         enterRule(); 
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4992:28: ( ( (enumLiteral_0= 'Transition' ) | (enumLiteral_1= 'Commit' ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4993:1: ( (enumLiteral_0= 'Transition' ) | (enumLiteral_1= 'Commit' ) )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4993:1: ( (enumLiteral_0= 'Transition' ) | (enumLiteral_1= 'Commit' ) )
            int alt112=2;
            int LA112_0 = input.LA(1);

            if ( (LA112_0==155) ) {
                alt112=1;
            }
            else if ( (LA112_0==156) ) {
                alt112=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 112, 0, input);

                throw nvae;
            }
            switch (alt112) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4993:2: (enumLiteral_0= 'Transition' )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4993:2: (enumLiteral_0= 'Transition' )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4993:4: enumLiteral_0= 'Transition'
                    {
                    enumLiteral_0=(Token)match(input,155,FOLLOW_155_in_ruleReviewBlockingType11546); 

                            current = grammarAccess.getReviewBlockingTypeAccess().getTransitionEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_0, grammarAccess.getReviewBlockingTypeAccess().getTransitionEnumLiteralDeclaration_0()); 
                        

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4999:6: (enumLiteral_1= 'Commit' )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4999:6: (enumLiteral_1= 'Commit' )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4999:8: enumLiteral_1= 'Commit'
                    {
                    enumLiteral_1=(Token)match(input,156,FOLLOW_156_in_ruleReviewBlockingType11563); 

                            current = grammarAccess.getReviewBlockingTypeAccess().getCommitEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_1, grammarAccess.getReviewBlockingTypeAccess().getCommitEnumLiteralDeclaration_1()); 
                        

                    }


                    }
                    break;

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleReviewBlockingType"

    // Delegated rules


 

    public static final BitSet FOLLOW_ruleAtsDsl_in_entryRuleAtsDsl75 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAtsDsl85 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_11_in_ruleAtsDsl123 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleUserDef_in_ruleAtsDsl144 = new BitSet(new long[]{0x000000000000F802L});
    public static final BitSet FOLLOW_12_in_ruleAtsDsl159 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleTeamDef_in_ruleAtsDsl180 = new BitSet(new long[]{0x000000000000F002L});
    public static final BitSet FOLLOW_13_in_ruleAtsDsl195 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleActionableItemDef_in_ruleAtsDsl216 = new BitSet(new long[]{0x000000000000E002L});
    public static final BitSet FOLLOW_14_in_ruleAtsDsl231 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleWorkDef_in_ruleAtsDsl252 = new BitSet(new long[]{0x000000000000C002L});
    public static final BitSet FOLLOW_15_in_ruleAtsDsl267 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleProgramDef_in_ruleAtsDsl288 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_ruleUSER_DEF_REFERENCE_in_entryRuleUSER_DEF_REFERENCE327 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleUSER_DEF_REFERENCE338 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleUSER_DEF_REFERENCE377 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleUserDef_in_entryRuleUserDef421 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleUserDef431 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleUSER_DEF_REFERENCE_in_ruleUserDef477 = new BitSet(new long[]{0x0000000000010012L,0x0000000000200000L});
    public static final BitSet FOLLOW_ruleUserDefOption_in_ruleUserDef498 = new BitSet(new long[]{0x0000000000010012L,0x0000000000200000L});
    public static final BitSet FOLLOW_16_in_ruleUserDef512 = new BitSet(new long[]{0x00000000003E0000L});
    public static final BitSet FOLLOW_17_in_ruleUserDef525 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L,0x0000000000C00000L});
    public static final BitSet FOLLOW_ruleBooleanDef_in_ruleUserDef546 = new BitSet(new long[]{0x00000000003C0000L});
    public static final BitSet FOLLOW_18_in_ruleUserDef561 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleUserDef578 = new BitSet(new long[]{0x0000000000380000L});
    public static final BitSet FOLLOW_19_in_ruleUserDef598 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleUserDef615 = new BitSet(new long[]{0x0000000000300000L});
    public static final BitSet FOLLOW_20_in_ruleUserDef635 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L,0x0000000000C00000L});
    public static final BitSet FOLLOW_ruleBooleanDef_in_ruleUserDef656 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_21_in_ruleUserDef670 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleATTR_DEF_REFERENCE_in_entryRuleATTR_DEF_REFERENCE709 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleATTR_DEF_REFERENCE720 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleATTR_DEF_REFERENCE759 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttrDef_in_entryRuleAttrDef803 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttrDef813 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleATTR_DEF_REFERENCE_in_ruleAttrDef859 = new BitSet(new long[]{0x0000000000410000L});
    public static final BitSet FOLLOW_ruleAttrDefOptions_in_ruleAttrDef880 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttrDefOptions_in_entryRuleAttrDefOptions916 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttrDefOptions926 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttrValueDef_in_ruleAttrDefOptions973 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttrFullDef_in_ruleAttrDefOptions1000 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttrValueDef_in_entryRuleAttrValueDef1035 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttrValueDef1045 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_22_in_ruleAttrValueDef1082 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAttrValueDef1099 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttrFullDef_in_entryRuleAttrFullDef1140 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttrFullDef1150 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_16_in_ruleAttrFullDef1187 = new BitSet(new long[]{0x0000000000C00000L});
    public static final BitSet FOLLOW_23_in_ruleAttrFullDef1200 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAttrFullDef1217 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_22_in_ruleAttrFullDef1237 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAttrFullDef1254 = new BitSet(new long[]{0x0000000000600000L});
    public static final BitSet FOLLOW_21_in_ruleAttrFullDef1273 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rulePROGRAM_REFERENCE_in_entryRulePROGRAM_REFERENCE1310 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRulePROGRAM_REFERENCE1321 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rulePROGRAM_REFERENCE1360 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleProgramDef_in_entryRuleProgramDef1404 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleProgramDef1414 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rulePROGRAM_REFERENCE_in_ruleProgramDef1460 = new BitSet(new long[]{0x0000000000010010L,0x0000000000200000L});
    public static final BitSet FOLLOW_ruleProgramDefOption_in_ruleProgramDef1481 = new BitSet(new long[]{0x0000000000010010L,0x0000000000200000L});
    public static final BitSet FOLLOW_16_in_ruleProgramDef1494 = new BitSet(new long[]{0x000000000FA21000L});
    public static final BitSet FOLLOW_24_in_ruleProgramDef1507 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleProgramDef1524 = new BitSet(new long[]{0x000000000EA21000L});
    public static final BitSet FOLLOW_23_in_ruleProgramDef1544 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_RULE_INT_in_ruleProgramDef1561 = new BitSet(new long[]{0x000000000E221000L});
    public static final BitSet FOLLOW_25_in_ruleProgramDef1581 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleProgramDef1598 = new BitSet(new long[]{0x000000000C221000L});
    public static final BitSet FOLLOW_17_in_ruleProgramDef1618 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L,0x0000000000C00000L});
    public static final BitSet FOLLOW_ruleBooleanDef_in_ruleProgramDef1639 = new BitSet(new long[]{0x000000000C201000L});
    public static final BitSet FOLLOW_26_in_ruleProgramDef1654 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleProgramDef1671 = new BitSet(new long[]{0x000000000C201000L});
    public static final BitSet FOLLOW_12_in_ruleProgramDef1691 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleTEAM_DEF_REFERENCE_in_ruleProgramDef1712 = new BitSet(new long[]{0x0000000008201000L});
    public static final BitSet FOLLOW_27_in_ruleProgramDef1727 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleAttrDef_in_ruleProgramDef1748 = new BitSet(new long[]{0x0000000008200000L});
    public static final BitSet FOLLOW_21_in_ruleProgramDef1762 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTEAM_DEF_REFERENCE_in_entryRuleTEAM_DEF_REFERENCE1799 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleTEAM_DEF_REFERENCE1810 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleTEAM_DEF_REFERENCE1849 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTeamDef_in_entryRuleTeamDef1893 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleTeamDef1903 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTEAM_DEF_REFERENCE_in_ruleTeamDef1949 = new BitSet(new long[]{0x0000000000010010L,0x0000000000200000L});
    public static final BitSet FOLLOW_ruleTeamDefOption_in_ruleTeamDef1970 = new BitSet(new long[]{0x0000000000010010L,0x0000000000200000L});
    public static final BitSet FOLLOW_16_in_ruleTeamDef1983 = new BitSet(new long[]{0x0000001FF1A24000L});
    public static final BitSet FOLLOW_24_in_ruleTeamDef1996 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleTeamDef2013 = new BitSet(new long[]{0x0000001FF0A24000L});
    public static final BitSet FOLLOW_23_in_ruleTeamDef2033 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_RULE_INT_in_ruleTeamDef2050 = new BitSet(new long[]{0x0000001FF0224000L});
    public static final BitSet FOLLOW_17_in_ruleTeamDef2070 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L,0x0000000000C00000L});
    public static final BitSet FOLLOW_ruleBooleanDef_in_ruleTeamDef2091 = new BitSet(new long[]{0x0000001FF0204000L});
    public static final BitSet FOLLOW_28_in_ruleTeamDef2106 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleTeamDef2123 = new BitSet(new long[]{0x0000001FF0204000L});
    public static final BitSet FOLLOW_29_in_ruleTeamDef2143 = new BitSet(new long[]{0x0000000000040000L,0x0000000000008000L});
    public static final BitSet FOLLOW_ruleUserRef_in_ruleTeamDef2164 = new BitSet(new long[]{0x0000001FE0204000L});
    public static final BitSet FOLLOW_30_in_ruleTeamDef2179 = new BitSet(new long[]{0x0000000000040000L,0x0000000000008000L});
    public static final BitSet FOLLOW_ruleUserRef_in_ruleTeamDef2200 = new BitSet(new long[]{0x0000001FC0204000L});
    public static final BitSet FOLLOW_31_in_ruleTeamDef2215 = new BitSet(new long[]{0x0000000000040000L,0x0000000000008000L});
    public static final BitSet FOLLOW_ruleUserRef_in_ruleTeamDef2236 = new BitSet(new long[]{0x0000001F80204000L});
    public static final BitSet FOLLOW_14_in_ruleTeamDef2251 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleTeamDef2268 = new BitSet(new long[]{0x0000001F00200000L});
    public static final BitSet FOLLOW_32_in_ruleTeamDef2288 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleTeamDef2305 = new BitSet(new long[]{0x0000001E00200000L});
    public static final BitSet FOLLOW_33_in_ruleTeamDef2325 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleTeamDef2342 = new BitSet(new long[]{0x0000001C00200000L});
    public static final BitSet FOLLOW_34_in_ruleTeamDef2362 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleTeamDef2379 = new BitSet(new long[]{0x0000001C00200000L});
    public static final BitSet FOLLOW_35_in_ruleTeamDef2399 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleVersionDef_in_ruleTeamDef2420 = new BitSet(new long[]{0x0000001800200000L});
    public static final BitSet FOLLOW_36_in_ruleTeamDef2435 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_16_in_ruleTeamDef2447 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_12_in_ruleTeamDef2460 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleTeamDef_in_ruleTeamDef2481 = new BitSet(new long[]{0x0000000000201000L});
    public static final BitSet FOLLOW_21_in_ruleTeamDef2495 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_21_in_ruleTeamDef2509 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAI_DEF_REFERENCE_in_entryRuleAI_DEF_REFERENCE2546 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAI_DEF_REFERENCE2557 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAI_DEF_REFERENCE2596 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleActionableItemDef_in_entryRuleActionableItemDef2640 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleActionableItemDef2650 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAI_DEF_REFERENCE_in_ruleActionableItemDef2696 = new BitSet(new long[]{0x0000000000010012L,0x0000000000200000L});
    public static final BitSet FOLLOW_ruleActionableItemOption_in_ruleActionableItemDef2717 = new BitSet(new long[]{0x0000000000010012L,0x0000000000200000L});
    public static final BitSet FOLLOW_16_in_ruleActionableItemDef2731 = new BitSet(new long[]{0x000000F431A20000L});
    public static final BitSet FOLLOW_24_in_ruleActionableItemDef2744 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleActionableItemDef2761 = new BitSet(new long[]{0x000000F430A20000L});
    public static final BitSet FOLLOW_23_in_ruleActionableItemDef2781 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_RULE_INT_in_ruleActionableItemDef2798 = new BitSet(new long[]{0x000000F430220000L});
    public static final BitSet FOLLOW_17_in_ruleActionableItemDef2818 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L,0x0000000000C00000L});
    public static final BitSet FOLLOW_ruleBooleanDef_in_ruleActionableItemDef2839 = new BitSet(new long[]{0x000000F430200000L});
    public static final BitSet FOLLOW_37_in_ruleActionableItemDef2854 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L,0x0000000000C00000L});
    public static final BitSet FOLLOW_ruleBooleanDef_in_ruleActionableItemDef2875 = new BitSet(new long[]{0x000000D430200000L});
    public static final BitSet FOLLOW_29_in_ruleActionableItemDef2890 = new BitSet(new long[]{0x0000000000040000L,0x0000000000008000L});
    public static final BitSet FOLLOW_ruleUserRef_in_ruleActionableItemDef2911 = new BitSet(new long[]{0x000000D430200000L});
    public static final BitSet FOLLOW_38_in_ruleActionableItemDef2926 = new BitSet(new long[]{0x0000000000040000L,0x0000000000008000L});
    public static final BitSet FOLLOW_ruleUserRef_in_ruleActionableItemDef2947 = new BitSet(new long[]{0x000000D410200000L});
    public static final BitSet FOLLOW_28_in_ruleActionableItemDef2962 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleActionableItemDef2979 = new BitSet(new long[]{0x0000009410200000L});
    public static final BitSet FOLLOW_39_in_ruleActionableItemDef2999 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleActionableItemDef3016 = new BitSet(new long[]{0x0000001400200000L});
    public static final BitSet FOLLOW_34_in_ruleActionableItemDef3036 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleActionableItemDef3053 = new BitSet(new long[]{0x0000001400200000L});
    public static final BitSet FOLLOW_36_in_ruleActionableItemDef3073 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_16_in_ruleActionableItemDef3085 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_13_in_ruleActionableItemDef3098 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleActionableItemDef_in_ruleActionableItemDef3119 = new BitSet(new long[]{0x0000000000202000L});
    public static final BitSet FOLLOW_21_in_ruleActionableItemDef3133 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_21_in_ruleActionableItemDef3147 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleVERSION_DEF_REFERENCE_in_entryRuleVERSION_DEF_REFERENCE3186 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleVERSION_DEF_REFERENCE3197 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleVERSION_DEF_REFERENCE3236 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleVersionDef_in_entryRuleVersionDef3280 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleVersionDef3290 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleVERSION_DEF_REFERENCE_in_ruleVersionDef3336 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_16_in_ruleVersionDef3348 = new BitSet(new long[]{0x00003F0011A00000L});
    public static final BitSet FOLLOW_24_in_ruleVersionDef3361 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleVersionDef3378 = new BitSet(new long[]{0x00003F0010A00000L});
    public static final BitSet FOLLOW_23_in_ruleVersionDef3398 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_RULE_INT_in_ruleVersionDef3415 = new BitSet(new long[]{0x00003F0010200000L});
    public static final BitSet FOLLOW_28_in_ruleVersionDef3435 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleVersionDef3452 = new BitSet(new long[]{0x00003F0010200000L});
    public static final BitSet FOLLOW_40_in_ruleVersionDef3472 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L,0x0000000000C00000L});
    public static final BitSet FOLLOW_ruleBooleanDef_in_ruleVersionDef3493 = new BitSet(new long[]{0x00003E0000200000L});
    public static final BitSet FOLLOW_41_in_ruleVersionDef3508 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L,0x0000000000C00000L});
    public static final BitSet FOLLOW_ruleBooleanDef_in_ruleVersionDef3529 = new BitSet(new long[]{0x00003C0000200000L});
    public static final BitSet FOLLOW_42_in_ruleVersionDef3544 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L,0x0000000000C00000L});
    public static final BitSet FOLLOW_ruleBooleanDef_in_ruleVersionDef3565 = new BitSet(new long[]{0x0000380000200000L});
    public static final BitSet FOLLOW_43_in_ruleVersionDef3580 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L,0x0000000000C00000L});
    public static final BitSet FOLLOW_ruleBooleanDef_in_ruleVersionDef3601 = new BitSet(new long[]{0x0000300000200000L});
    public static final BitSet FOLLOW_44_in_ruleVersionDef3616 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleVersionDef3633 = new BitSet(new long[]{0x0000200000200000L});
    public static final BitSet FOLLOW_45_in_ruleVersionDef3653 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleVersionDef3670 = new BitSet(new long[]{0x0000200000200000L});
    public static final BitSet FOLLOW_21_in_ruleVersionDef3689 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleWorkDef_in_entryRuleWorkDef3725 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleWorkDef3735 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleWORK_DEFINITION_NAME_REFERENCE_in_ruleWorkDef3781 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_16_in_ruleWorkDef3793 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_46_in_ruleWorkDef3806 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleWorkDef3823 = new BitSet(new long[]{0x0000C00000000000L});
    public static final BitSet FOLLOW_47_in_ruleWorkDef3843 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleWorkDef3866 = new BitSet(new long[]{0x1001000000000000L,0x0000000000001010L});
    public static final BitSet FOLLOW_ruleWidgetDef_in_ruleWorkDef3888 = new BitSet(new long[]{0x1001000000000000L,0x0000000000001010L});
    public static final BitSet FOLLOW_ruleDecisionReviewDef_in_ruleWorkDef3910 = new BitSet(new long[]{0x1001000000000000L,0x0000000000001010L});
    public static final BitSet FOLLOW_rulePeerReviewDef_in_ruleWorkDef3932 = new BitSet(new long[]{0x1001000000000000L,0x0000000000001010L});
    public static final BitSet FOLLOW_ruleStateDef_in_ruleWorkDef3954 = new BitSet(new long[]{0x1001000000200000L,0x0000000000001010L});
    public static final BitSet FOLLOW_21_in_ruleWorkDef3967 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleWidgetDef_in_entryRuleWidgetDef4003 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleWidgetDef4013 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_ruleWidgetDef4050 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleWIDGET_NAME_REFERENCE_in_ruleWidgetDef4071 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_16_in_ruleWidgetDef4083 = new BitSet(new long[]{0x01FE000000200000L});
    public static final BitSet FOLLOW_49_in_ruleWidgetDef4096 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleWidgetDef4113 = new BitSet(new long[]{0x01FC000000200000L});
    public static final BitSet FOLLOW_50_in_ruleWidgetDef4133 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleWidgetDef4150 = new BitSet(new long[]{0x01F8000000200000L});
    public static final BitSet FOLLOW_51_in_ruleWidgetDef4170 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleWidgetDef4187 = new BitSet(new long[]{0x01F0000000200000L});
    public static final BitSet FOLLOW_52_in_ruleWidgetDef4207 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleWidgetDef4224 = new BitSet(new long[]{0x01E0000000200000L});
    public static final BitSet FOLLOW_53_in_ruleWidgetDef4244 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_RULE_INT_in_ruleWidgetDef4261 = new BitSet(new long[]{0x01C0000000200000L});
    public static final BitSet FOLLOW_54_in_ruleWidgetDef4281 = new BitSet(new long[]{0x0000000000000010L,0xFFFFFFE000000000L,0x0000000000000007L});
    public static final BitSet FOLLOW_ruleWidgetOption_in_ruleWidgetDef4302 = new BitSet(new long[]{0x01C0000000200000L});
    public static final BitSet FOLLOW_55_in_ruleWidgetDef4317 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleWidgetDef4334 = new BitSet(new long[]{0x0100000000200000L});
    public static final BitSet FOLLOW_56_in_ruleWidgetDef4354 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleWidgetDef4371 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_21_in_ruleWidgetDef4390 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleWidgetRef_in_entryRuleWidgetRef4426 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleWidgetRef4436 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_57_in_ruleWidgetRef4473 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleWIDGET_NAME_REFERENCE_in_ruleWidgetRef4496 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttrWidget_in_entryRuleAttrWidget4532 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttrWidget4542 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_58_in_ruleAttrWidget4579 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAttrWidget4596 = new BitSet(new long[]{0x0800000000000002L});
    public static final BitSet FOLLOW_59_in_ruleAttrWidget4614 = new BitSet(new long[]{0x0000000000000010L,0xFFFFFFE000000000L,0x0000000000000007L});
    public static final BitSet FOLLOW_ruleWidgetOption_in_ruleAttrWidget4635 = new BitSet(new long[]{0x0800000000000002L});
    public static final BitSet FOLLOW_ruleStateDef_in_entryRuleStateDef4673 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleStateDef4683 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_60_in_ruleStateDef4720 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleStateDef4741 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_16_in_ruleStateDef4753 = new BitSet(new long[]{0x2004000000000000L});
    public static final BitSet FOLLOW_50_in_ruleStateDef4766 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleStateDef4783 = new BitSet(new long[]{0x2000000000000000L});
    public static final BitSet FOLLOW_61_in_ruleStateDef4802 = new BitSet(new long[]{0x0000000000000010L,0x0000000000000000L,0x0000000000000038L});
    public static final BitSet FOLLOW_rulePageType_in_ruleStateDef4823 = new BitSet(new long[]{0x4000000000000000L});
    public static final BitSet FOLLOW_62_in_ruleStateDef4835 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_RULE_INT_in_ruleStateDef4852 = new BitSet(new long[]{0x8000000000200000L,0x000000000007080FL});
    public static final BitSet FOLLOW_ruleToState_in_ruleStateDef4878 = new BitSet(new long[]{0x8000000000200000L,0x000000000007080FL});
    public static final BitSet FOLLOW_63_in_ruleStateDef4892 = new BitSet(new long[]{0x0000000000000010L,0x0000001FFE000000L});
    public static final BitSet FOLLOW_ruleRule_in_ruleStateDef4913 = new BitSet(new long[]{0x8000000000200000L,0x000000000006080FL});
    public static final BitSet FOLLOW_ruleDecisionReviewRef_in_ruleStateDef4936 = new BitSet(new long[]{0x0000000000200000L,0x000000000006080FL});
    public static final BitSet FOLLOW_rulePeerReviewRef_in_ruleStateDef4958 = new BitSet(new long[]{0x0000000000200000L,0x0000000000060807L});
    public static final BitSet FOLLOW_64_in_ruleStateDef4972 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_RULE_INT_in_ruleStateDef4989 = new BitSet(new long[]{0x0000000000200000L,0x0000000000060006L});
    public static final BitSet FOLLOW_65_in_ruleStateDef5009 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_RULE_INT_in_ruleStateDef5026 = new BitSet(new long[]{0x0000000000200000L,0x0000000000060004L});
    public static final BitSet FOLLOW_66_in_ruleStateDef5046 = new BitSet(new long[]{0x0000000000000010L,0x0000000000000000L,0x00000000003FFFC0L});
    public static final BitSet FOLLOW_ruleStateColor_in_ruleStateDef5067 = new BitSet(new long[]{0x0000000000200000L,0x0000000000060000L});
    public static final BitSet FOLLOW_ruleLayoutType_in_ruleStateDef5090 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_21_in_ruleStateDef5103 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDecisionReviewRef_in_entryRuleDecisionReviewRef5139 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleDecisionReviewRef5149 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_67_in_ruleDecisionReviewRef5186 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleDECISION_REVIEW_NAME_REFERENCE_in_ruleDecisionReviewRef5209 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDecisionReviewDef_in_entryRuleDecisionReviewDef5245 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleDecisionReviewDef5255 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_68_in_ruleDecisionReviewDef5292 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleDECISION_REVIEW_NAME_REFERENCE_in_ruleDecisionReviewDef5313 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_16_in_ruleDecisionReviewDef5325 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_69_in_ruleDecisionReviewDef5337 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleDecisionReviewDef5354 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_50_in_ruleDecisionReviewDef5371 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleDecisionReviewDef5388 = new BitSet(new long[]{0x0000000000000000L,0x00000000000000C0L});
    public static final BitSet FOLLOW_70_in_ruleDecisionReviewDef5406 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleDecisionReviewDef5429 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_71_in_ruleDecisionReviewDef5443 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000018000000L});
    public static final BitSet FOLLOW_ruleReviewBlockingType_in_ruleDecisionReviewDef5464 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_72_in_ruleDecisionReviewDef5476 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000007000000L});
    public static final BitSet FOLLOW_ruleWorkflowEventType_in_ruleDecisionReviewDef5497 = new BitSet(new long[]{0x0040000000000000L,0x0000000000000600L});
    public static final BitSet FOLLOW_73_in_ruleDecisionReviewDef5510 = new BitSet(new long[]{0x0000000000040000L,0x0000000000008000L});
    public static final BitSet FOLLOW_ruleUserRef_in_ruleDecisionReviewDef5531 = new BitSet(new long[]{0x0040000000000000L,0x0000000000000600L});
    public static final BitSet FOLLOW_74_in_ruleDecisionReviewDef5546 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L,0x0000000000C00000L});
    public static final BitSet FOLLOW_ruleBooleanDef_in_ruleDecisionReviewDef5567 = new BitSet(new long[]{0x0040000000000000L,0x0000000000000600L});
    public static final BitSet FOLLOW_ruleDecisionReviewOpt_in_ruleDecisionReviewDef5590 = new BitSet(new long[]{0x0040000000200000L,0x0000000000000600L});
    public static final BitSet FOLLOW_21_in_ruleDecisionReviewDef5603 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDECISION_REVIEW_OPT_REF_in_entryRuleDECISION_REVIEW_OPT_REF5640 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleDECISION_REVIEW_OPT_REF5651 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleDECISION_REVIEW_OPT_REF5690 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDecisionReviewOpt_in_entryRuleDecisionReviewOpt5734 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleDecisionReviewOpt5744 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_54_in_ruleDecisionReviewOpt5781 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleDECISION_REVIEW_OPT_REF_in_ruleDecisionReviewOpt5802 = new BitSet(new long[]{0x0000000000000002L,0x0000000000004000L});
    public static final BitSet FOLLOW_ruleFollowupRef_in_ruleDecisionReviewOpt5823 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rulePeerReviewRef_in_entryRulePeerReviewRef5860 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRulePeerReviewRef5870 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_75_in_rulePeerReviewRef5907 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rulePEER_REVIEW_NAME_REFERENCE_in_rulePeerReviewRef5930 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rulePeerReviewDef_in_entryRulePeerReviewDef5966 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRulePeerReviewDef5976 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_76_in_rulePeerReviewDef6013 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rulePEER_REVIEW_NAME_REFERENCE_in_rulePeerReviewDef6034 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_16_in_rulePeerReviewDef6046 = new BitSet(new long[]{0x0004000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_69_in_rulePeerReviewDef6059 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_rulePeerReviewDef6076 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_50_in_rulePeerReviewDef6095 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_rulePeerReviewDef6112 = new BitSet(new long[]{0x0000000000000000L,0x00000000000020C0L});
    public static final BitSet FOLLOW_77_in_rulePeerReviewDef6130 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_rulePeerReviewDef6147 = new BitSet(new long[]{0x0000000000000000L,0x00000000000000C0L});
    public static final BitSet FOLLOW_70_in_rulePeerReviewDef6167 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleSTATE_NAME_REFERENCE_in_rulePeerReviewDef6190 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_71_in_rulePeerReviewDef6204 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000018000000L});
    public static final BitSet FOLLOW_ruleReviewBlockingType_in_rulePeerReviewDef6225 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_72_in_rulePeerReviewDef6237 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000007000000L});
    public static final BitSet FOLLOW_ruleWorkflowEventType_in_rulePeerReviewDef6258 = new BitSet(new long[]{0x0000000000200000L,0x0000000000000200L});
    public static final BitSet FOLLOW_73_in_rulePeerReviewDef6271 = new BitSet(new long[]{0x0000000000040000L,0x0000000000008000L});
    public static final BitSet FOLLOW_ruleUserRef_in_rulePeerReviewDef6292 = new BitSet(new long[]{0x0000000000200000L,0x0000000000000200L});
    public static final BitSet FOLLOW_21_in_rulePeerReviewDef6306 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleFollowupRef_in_entryRuleFollowupRef6342 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleFollowupRef6352 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_78_in_ruleFollowupRef6389 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_73_in_ruleFollowupRef6402 = new BitSet(new long[]{0x0000000000040000L,0x0000000000008000L});
    public static final BitSet FOLLOW_ruleUserRef_in_ruleFollowupRef6423 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000200L});
    public static final BitSet FOLLOW_ruleUserRef_in_entryRuleUserRef6461 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleUserRef6471 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleUserByUserId_in_ruleUserRef6518 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleUserByName_in_ruleUserRef6545 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleUserByUserId_in_entryRuleUserByUserId6580 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleUserByUserId6590 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_18_in_ruleUserByUserId6627 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleUserByUserId6644 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleUserByName_in_entryRuleUserByName6685 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleUserByName6695 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_79_in_ruleUserByName6732 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleUserByName6749 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDECISION_REVIEW_NAME_REFERENCE_in_entryRuleDECISION_REVIEW_NAME_REFERENCE6791 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleDECISION_REVIEW_NAME_REFERENCE6802 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleDECISION_REVIEW_NAME_REFERENCE6841 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rulePEER_REVIEW_NAME_REFERENCE_in_entryRulePEER_REVIEW_NAME_REFERENCE6886 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRulePEER_REVIEW_NAME_REFERENCE6897 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rulePEER_REVIEW_NAME_REFERENCE6936 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleSTATE_NAME_REFERENCE_in_entryRuleSTATE_NAME_REFERENCE6981 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleSTATE_NAME_REFERENCE6992 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleSTATE_NAME_REFERENCE7031 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleWIDGET_NAME_REFERENCE_in_entryRuleWIDGET_NAME_REFERENCE7076 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleWIDGET_NAME_REFERENCE7087 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleWIDGET_NAME_REFERENCE7126 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleWORK_DEFINITION_NAME_REFERENCE_in_entryRuleWORK_DEFINITION_NAME_REFERENCE7171 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleWORK_DEFINITION_NAME_REFERENCE7182 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleWORK_DEFINITION_NAME_REFERENCE7221 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleToState_in_entryRuleToState7265 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleToState7275 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_80_in_ruleToState7312 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleToState7335 = new BitSet(new long[]{0x0000000000000012L,0x0000000001800000L});
    public static final BitSet FOLLOW_ruleTransitionOption_in_ruleToState7356 = new BitSet(new long[]{0x0000000000000012L,0x0000000001800000L});
    public static final BitSet FOLLOW_ruleLayoutType_in_entryRuleLayoutType7393 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleLayoutType7403 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleLayoutDef_in_ruleLayoutType7450 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleLayoutCopy_in_ruleLayoutType7477 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleLayoutDef_in_entryRuleLayoutDef7512 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleLayoutDef7522 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_81_in_ruleLayoutDef7559 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_16_in_ruleLayoutDef7571 = new BitSet(new long[]{0x0600000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_ruleLayoutItem_in_ruleLayoutDef7592 = new BitSet(new long[]{0x0600000000200000L,0x0000000000080000L});
    public static final BitSet FOLLOW_21_in_ruleLayoutDef7605 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleLayoutCopy_in_entryRuleLayoutCopy7641 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleLayoutCopy7651 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_82_in_ruleLayoutCopy7688 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleLayoutCopy7711 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleLayoutItem_in_entryRuleLayoutItem7747 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleLayoutItem7757 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleWidgetRef_in_ruleLayoutItem7804 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttrWidget_in_ruleLayoutItem7831 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleComposite_in_ruleLayoutItem7858 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleComposite_in_entryRuleComposite7893 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleComposite7903 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_83_in_ruleComposite7940 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_16_in_ruleComposite7952 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_84_in_ruleComposite7964 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_RULE_INT_in_ruleComposite7981 = new BitSet(new long[]{0x0600000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_ruleLayoutItem_in_ruleComposite8007 = new BitSet(new long[]{0x0640000000200000L,0x0000000000080000L});
    public static final BitSet FOLLOW_54_in_ruleComposite8021 = new BitSet(new long[]{0x0000000000000010L,0x0000000000400000L});
    public static final BitSet FOLLOW_ruleCompositeOption_in_ruleComposite8042 = new BitSet(new long[]{0x0040000000200000L});
    public static final BitSet FOLLOW_21_in_ruleComposite8056 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleUSER_DEF_OPTION_NAME_in_entryRuleUSER_DEF_OPTION_NAME8093 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleUSER_DEF_OPTION_NAME8104 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleUSER_DEF_OPTION_NAME8143 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleUserDefOption_in_entryRuleUserDefOption8188 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleUserDefOption8199 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_85_in_ruleUserDefOption8237 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleUSER_DEF_OPTION_NAME_in_ruleUserDefOption8265 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rulePROGRAM_DEF_OPTION_NAME_in_entryRulePROGRAM_DEF_OPTION_NAME8311 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRulePROGRAM_DEF_OPTION_NAME8322 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rulePROGRAM_DEF_OPTION_NAME8361 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleProgramDefOption_in_entryRuleProgramDefOption8406 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleProgramDefOption8417 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_85_in_ruleProgramDefOption8455 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rulePROGRAM_DEF_OPTION_NAME_in_ruleProgramDefOption8483 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTEAM_DEF_OPTION_NAME_in_entryRuleTEAM_DEF_OPTION_NAME8529 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleTEAM_DEF_OPTION_NAME8540 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleTEAM_DEF_OPTION_NAME8579 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTeamDefOption_in_entryRuleTeamDefOption8624 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleTeamDefOption8635 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_85_in_ruleTeamDefOption8673 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTEAM_DEF_OPTION_NAME_in_ruleTeamDefOption8701 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAI_DEF_OPTION_NAME_in_entryRuleAI_DEF_OPTION_NAME8747 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAI_DEF_OPTION_NAME8758 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAI_DEF_OPTION_NAME8797 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleActionableItemOption_in_entryRuleActionableItemOption8842 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleActionableItemOption8853 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_85_in_ruleActionableItemOption8891 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAI_DEF_OPTION_NAME_in_ruleActionableItemOption8919 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleCOMPOSITE_OPTION_NAME_in_entryRuleCOMPOSITE_OPTION_NAME8965 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleCOMPOSITE_OPTION_NAME8976 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleCOMPOSITE_OPTION_NAME9015 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleCompositeOption_in_entryRuleCompositeOption9060 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleCompositeOption9071 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_86_in_ruleCompositeOption9109 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleCOMPOSITE_OPTION_NAME_in_ruleCompositeOption9137 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTRANSITION_OPTION_NAME_in_entryRuleTRANSITION_OPTION_NAME9183 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleTRANSITION_OPTION_NAME9194 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleTRANSITION_OPTION_NAME9233 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTransitionOption_in_entryRuleTransitionOption9278 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleTransitionOption9289 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_87_in_ruleTransitionOption9327 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_88_in_ruleTransitionOption9346 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTRANSITION_OPTION_NAME_in_ruleTransitionOption9374 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRULE_NAME_in_entryRuleRULE_NAME9420 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRULE_NAME9431 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleRULE_NAME9470 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRule_in_entryRuleRule9515 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRule9526 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_89_in_ruleRule9564 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_90_in_ruleRule9583 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_91_in_ruleRule9602 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_92_in_ruleRule9621 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_93_in_ruleRule9640 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_94_in_ruleRule9659 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_95_in_ruleRule9678 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_96_in_ruleRule9697 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_97_in_ruleRule9716 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_98_in_ruleRule9735 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_99_in_ruleRule9754 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_100_in_ruleRule9773 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRULE_NAME_in_ruleRule9801 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleWIDGET_OPTION_NAME_in_entryRuleWIDGET_OPTION_NAME9847 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleWIDGET_OPTION_NAME9858 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleWIDGET_OPTION_NAME9897 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleWidgetOption_in_entryRuleWidgetOption9942 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleWidgetOption9953 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_101_in_ruleWidgetOption9991 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_102_in_ruleWidgetOption10010 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_103_in_ruleWidgetOption10029 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_104_in_ruleWidgetOption10048 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_105_in_ruleWidgetOption10067 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_106_in_ruleWidgetOption10086 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_107_in_ruleWidgetOption10105 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_108_in_ruleWidgetOption10124 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_109_in_ruleWidgetOption10143 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_110_in_ruleWidgetOption10162 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_111_in_ruleWidgetOption10181 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_112_in_ruleWidgetOption10200 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_113_in_ruleWidgetOption10219 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_114_in_ruleWidgetOption10238 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_115_in_ruleWidgetOption10257 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_116_in_ruleWidgetOption10276 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_117_in_ruleWidgetOption10295 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_118_in_ruleWidgetOption10314 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_119_in_ruleWidgetOption10333 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_120_in_ruleWidgetOption10352 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_121_in_ruleWidgetOption10371 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_122_in_ruleWidgetOption10390 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_123_in_ruleWidgetOption10409 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_124_in_ruleWidgetOption10428 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_125_in_ruleWidgetOption10447 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_126_in_ruleWidgetOption10466 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_127_in_ruleWidgetOption10485 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_128_in_ruleWidgetOption10504 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_129_in_ruleWidgetOption10523 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_130_in_ruleWidgetOption10542 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleWIDGET_OPTION_NAME_in_ruleWidgetOption10570 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rulePAGE_TYPE_NAME_in_entryRulePAGE_TYPE_NAME10616 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRulePAGE_TYPE_NAME10627 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rulePAGE_TYPE_NAME10666 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rulePageType_in_entryRulePageType10711 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRulePageType10722 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_131_in_rulePageType10760 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_132_in_rulePageType10779 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_133_in_rulePageType10798 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rulePAGE_TYPE_NAME_in_rulePageType10826 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleCOLOR_NAME_in_entryRuleCOLOR_NAME10872 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleCOLOR_NAME10883 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleCOLOR_NAME10922 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleStateColor_in_entryRuleStateColor10967 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleStateColor10978 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_134_in_ruleStateColor11016 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_135_in_ruleStateColor11035 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_136_in_ruleStateColor11054 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_137_in_ruleStateColor11073 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_138_in_ruleStateColor11092 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_139_in_ruleStateColor11111 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_140_in_ruleStateColor11130 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_141_in_ruleStateColor11149 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_142_in_ruleStateColor11168 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_143_in_ruleStateColor11187 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_144_in_ruleStateColor11206 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_145_in_ruleStateColor11225 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_146_in_ruleStateColor11244 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_147_in_ruleStateColor11263 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_148_in_ruleStateColor11282 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_149_in_ruleStateColor11301 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleCOLOR_NAME_in_ruleStateColor11329 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_86_in_ruleBooleanDef11388 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_150_in_ruleBooleanDef11405 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_151_in_ruleBooleanDef11422 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_152_in_ruleWorkflowEventType11467 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_153_in_ruleWorkflowEventType11484 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_154_in_ruleWorkflowEventType11501 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_155_in_ruleReviewBlockingType11546 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_156_in_ruleReviewBlockingType11563 = new BitSet(new long[]{0x0000000000000002L});

}