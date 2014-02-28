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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE_STRING", "RULE_INT", "RULE_ID", "RULE_ML_COMMENT", "RULE_SL_COMMENT", "RULE_WS", "RULE_ANY_OTHER", "'workDefinition'", "'userDefinition'", "'teamDefinition'", "'actionableItem'", "'{'", "'active'", "'userId'", "'email'", "'isAdmin'", "'}'", "'guid'", "'staticId'", "'lead'", "'member'", "'privileged'", "'relatedTaskWorkDefinition'", "'accessContextId'", "'version'", "'children'", "'actionable'", "'owner'", "'team'", "'next'", "'released'", "'allowCreateBranch'", "'allowCommitBranch'", "'baselineBranchGuid'", "'parallelVersion'", "'id'", "'startState'", "'widgetDefinition'", "'attributeName'", "'description'", "'xWidgetName'", "'defaultValue'", "'height'", "'option'", "'minConstraint'", "'maxConstraint'", "'widget'", "'attributeWidget'", "'with'", "'state'", "'type'", "'ordinal'", "'rule'", "'percentWeight'", "'recommendedPercentComplete'", "'color'", "'decisionReview'", "'decisionReviewDefinition'", "'title'", "'relatedToState'", "'blockingType'", "'onEvent'", "'assignee'", "'autoTransitionToDecision'", "'peerReview'", "'peerReviewDefinition'", "'location'", "'followup by'", "'named'", "'to'", "'layout'", "'layoutCopyFrom'", "'composite'", "'numColumns'", "'GetOrCreate'", "'None'", "'AsDefault'", "'OverrideAttributeValidation'", "'RequireStateHourSpentPrompt'", "'AddDecisionValidateBlockingReview'", "'AddDecisionValidateNonBlockingReview'", "'AllowTransitionWithWorkingBranch'", "'ForceAssigneesToTeamLeads'", "'RequireTargetedVersion'", "'AllowPrivilegedEditToTeamMember'", "'AllowPrivilegedEditToTeamMemberAndOriginator'", "'AllowPrivilegedEditToAll'", "'AllowEditToAll'", "'AllowAssigneeToAll'", "'AllowTransitionWithoutTaskCompletion'", "'REQUIRED_FOR_TRANSITION'", "'NOT_REQUIRED_FOR_TRANSITION'", "'REQUIRED_FOR_COMPLETION'", "'NOT_REQUIRED_FOR_COMPLETION'", "'ENABLED'", "'NOT_ENABLED'", "'EDITABLE'", "'NOT_EDITABLE'", "'FUTURE_DATE_REQUIRED'", "'NOT_FUTURE_DATE_REQUIRED'", "'MULTI_SELECT'", "'HORIZONTAL_LABEL'", "'VERTICAL_LABEL'", "'LABEL_AFTER'", "'LABEL_BEFORE'", "'NO_LABEL'", "'SORTED'", "'ADD_DEFAULT_VALUE'", "'NO_DEFAULT_VALUE'", "'BEGIN_COMPOSITE_4'", "'BEGIN_COMPOSITE_6'", "'BEGIN_COMPOSITE_8'", "'BEGIN_COMPOSITE_10'", "'END_COMPOSITE'", "'FILL_NONE'", "'FILL_HORIZONTALLY'", "'FILL_VERTICALLY'", "'ALIGN_LEFT'", "'ALIGN_RIGHT'", "'ALIGN_CENTER'", "'Working'", "'Completed'", "'Cancelled'", "'BLACK'", "'WHITE'", "'RED'", "'DARK_RED'", "'GREEN'", "'DARK_GREEN'", "'YELLOW'", "'DARK_YELLOW'", "'BLUE'", "'DARK_BLUE'", "'MAGENTA'", "'DARK_MAGENTA'", "'CYAN'", "'DARK_CYAN'", "'GRAY'", "'DARK_GRAY'", "'True'", "'False'", "'TransitionTo'", "'CreateBranch'", "'CommitBranch'", "'Transition'", "'Commit'"
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
    public static final int T__99=99;
    public static final int T__98=98;
    public static final int T__97=97;
    public static final int T__96=96;
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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:77:1: ruleAtsDsl returns [EObject current=null] : ( (otherlv_0= 'workDefinition' ( (lv_workDef_1_0= ruleWorkDef ) ) )? (otherlv_2= 'userDefinition' ( (lv_userDef_3_0= ruleUserDef ) ) )* (otherlv_4= 'teamDefinition' ( (lv_teamDef_5_0= ruleTeamDef ) ) )* (otherlv_6= 'actionableItem' ( (lv_actionableItemDef_7_0= ruleActionableItemDef ) ) )* ) ;
    public final EObject ruleAtsDsl() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_2=null;
        Token otherlv_4=null;
        Token otherlv_6=null;
        EObject lv_workDef_1_0 = null;

        EObject lv_userDef_3_0 = null;

        EObject lv_teamDef_5_0 = null;

        EObject lv_actionableItemDef_7_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:80:28: ( ( (otherlv_0= 'workDefinition' ( (lv_workDef_1_0= ruleWorkDef ) ) )? (otherlv_2= 'userDefinition' ( (lv_userDef_3_0= ruleUserDef ) ) )* (otherlv_4= 'teamDefinition' ( (lv_teamDef_5_0= ruleTeamDef ) ) )* (otherlv_6= 'actionableItem' ( (lv_actionableItemDef_7_0= ruleActionableItemDef ) ) )* ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:81:1: ( (otherlv_0= 'workDefinition' ( (lv_workDef_1_0= ruleWorkDef ) ) )? (otherlv_2= 'userDefinition' ( (lv_userDef_3_0= ruleUserDef ) ) )* (otherlv_4= 'teamDefinition' ( (lv_teamDef_5_0= ruleTeamDef ) ) )* (otherlv_6= 'actionableItem' ( (lv_actionableItemDef_7_0= ruleActionableItemDef ) ) )* )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:81:1: ( (otherlv_0= 'workDefinition' ( (lv_workDef_1_0= ruleWorkDef ) ) )? (otherlv_2= 'userDefinition' ( (lv_userDef_3_0= ruleUserDef ) ) )* (otherlv_4= 'teamDefinition' ( (lv_teamDef_5_0= ruleTeamDef ) ) )* (otherlv_6= 'actionableItem' ( (lv_actionableItemDef_7_0= ruleActionableItemDef ) ) )* )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:81:2: (otherlv_0= 'workDefinition' ( (lv_workDef_1_0= ruleWorkDef ) ) )? (otherlv_2= 'userDefinition' ( (lv_userDef_3_0= ruleUserDef ) ) )* (otherlv_4= 'teamDefinition' ( (lv_teamDef_5_0= ruleTeamDef ) ) )* (otherlv_6= 'actionableItem' ( (lv_actionableItemDef_7_0= ruleActionableItemDef ) ) )*
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:81:2: (otherlv_0= 'workDefinition' ( (lv_workDef_1_0= ruleWorkDef ) ) )?
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==11) ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:81:4: otherlv_0= 'workDefinition' ( (lv_workDef_1_0= ruleWorkDef ) )
                    {
                    otherlv_0=(Token)match(input,11,FOLLOW_11_in_ruleAtsDsl123); 

                        	newLeafNode(otherlv_0, grammarAccess.getAtsDslAccess().getWorkDefinitionKeyword_0_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:85:1: ( (lv_workDef_1_0= ruleWorkDef ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:86:1: (lv_workDef_1_0= ruleWorkDef )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:86:1: (lv_workDef_1_0= ruleWorkDef )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:87:3: lv_workDef_1_0= ruleWorkDef
                    {
                     
                    	        newCompositeNode(grammarAccess.getAtsDslAccess().getWorkDefWorkDefParserRuleCall_0_1_0()); 
                    	    
                    pushFollow(FOLLOW_ruleWorkDef_in_ruleAtsDsl144);
                    lv_workDef_1_0=ruleWorkDef();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getAtsDslRule());
                    	        }
                           		set(
                           			current, 
                           			"workDef",
                            		lv_workDef_1_0, 
                            		"WorkDef");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:103:4: (otherlv_2= 'userDefinition' ( (lv_userDef_3_0= ruleUserDef ) ) )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==12) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:103:6: otherlv_2= 'userDefinition' ( (lv_userDef_3_0= ruleUserDef ) )
            	    {
            	    otherlv_2=(Token)match(input,12,FOLLOW_12_in_ruleAtsDsl159); 

            	        	newLeafNode(otherlv_2, grammarAccess.getAtsDslAccess().getUserDefinitionKeyword_1_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:107:1: ( (lv_userDef_3_0= ruleUserDef ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:108:1: (lv_userDef_3_0= ruleUserDef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:108:1: (lv_userDef_3_0= ruleUserDef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:109:3: lv_userDef_3_0= ruleUserDef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getAtsDslAccess().getUserDefUserDefParserRuleCall_1_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleUserDef_in_ruleAtsDsl180);
            	    lv_userDef_3_0=ruleUserDef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getAtsDslRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"userDef",
            	            		lv_userDef_3_0, 
            	            		"UserDef");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:125:4: (otherlv_4= 'teamDefinition' ( (lv_teamDef_5_0= ruleTeamDef ) ) )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==13) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:125:6: otherlv_4= 'teamDefinition' ( (lv_teamDef_5_0= ruleTeamDef ) )
            	    {
            	    otherlv_4=(Token)match(input,13,FOLLOW_13_in_ruleAtsDsl195); 

            	        	newLeafNode(otherlv_4, grammarAccess.getAtsDslAccess().getTeamDefinitionKeyword_2_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:129:1: ( (lv_teamDef_5_0= ruleTeamDef ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:130:1: (lv_teamDef_5_0= ruleTeamDef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:130:1: (lv_teamDef_5_0= ruleTeamDef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:131:3: lv_teamDef_5_0= ruleTeamDef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getAtsDslAccess().getTeamDefTeamDefParserRuleCall_2_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleTeamDef_in_ruleAtsDsl216);
            	    lv_teamDef_5_0=ruleTeamDef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getAtsDslRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"teamDef",
            	            		lv_teamDef_5_0, 
            	            		"TeamDef");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:147:4: (otherlv_6= 'actionableItem' ( (lv_actionableItemDef_7_0= ruleActionableItemDef ) ) )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==14) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:147:6: otherlv_6= 'actionableItem' ( (lv_actionableItemDef_7_0= ruleActionableItemDef ) )
            	    {
            	    otherlv_6=(Token)match(input,14,FOLLOW_14_in_ruleAtsDsl231); 

            	        	newLeafNode(otherlv_6, grammarAccess.getAtsDslAccess().getActionableItemKeyword_3_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:151:1: ( (lv_actionableItemDef_7_0= ruleActionableItemDef ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:152:1: (lv_actionableItemDef_7_0= ruleActionableItemDef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:152:1: (lv_actionableItemDef_7_0= ruleActionableItemDef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:153:3: lv_actionableItemDef_7_0= ruleActionableItemDef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getAtsDslAccess().getActionableItemDefActionableItemDefParserRuleCall_3_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleActionableItemDef_in_ruleAtsDsl252);
            	    lv_actionableItemDef_7_0=ruleActionableItemDef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getAtsDslRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"actionableItemDef",
            	            		lv_actionableItemDef_7_0, 
            	            		"ActionableItemDef");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop4;
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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:177:1: entryRuleUSER_DEF_REFERENCE returns [String current=null] : iv_ruleUSER_DEF_REFERENCE= ruleUSER_DEF_REFERENCE EOF ;
    public final String entryRuleUSER_DEF_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleUSER_DEF_REFERENCE = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:178:2: (iv_ruleUSER_DEF_REFERENCE= ruleUSER_DEF_REFERENCE EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:179:2: iv_ruleUSER_DEF_REFERENCE= ruleUSER_DEF_REFERENCE EOF
            {
             newCompositeNode(grammarAccess.getUSER_DEF_REFERENCERule()); 
            pushFollow(FOLLOW_ruleUSER_DEF_REFERENCE_in_entryRuleUSER_DEF_REFERENCE291);
            iv_ruleUSER_DEF_REFERENCE=ruleUSER_DEF_REFERENCE();

            state._fsp--;

             current =iv_ruleUSER_DEF_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleUSER_DEF_REFERENCE302); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:186:1: ruleUSER_DEF_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleUSER_DEF_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:189:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:190:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleUSER_DEF_REFERENCE341); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:205:1: entryRuleUserDef returns [EObject current=null] : iv_ruleUserDef= ruleUserDef EOF ;
    public final EObject entryRuleUserDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleUserDef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:206:2: (iv_ruleUserDef= ruleUserDef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:207:2: iv_ruleUserDef= ruleUserDef EOF
            {
             newCompositeNode(grammarAccess.getUserDefRule()); 
            pushFollow(FOLLOW_ruleUserDef_in_entryRuleUserDef385);
            iv_ruleUserDef=ruleUserDef();

            state._fsp--;

             current =iv_ruleUserDef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleUserDef395); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:214:1: ruleUserDef returns [EObject current=null] : ( ( (lv_name_0_0= ruleUSER_DEF_REFERENCE ) ) ( (lv_userDefOption_1_0= ruleUserDefOption ) )* (otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'userId' ( (lv_userId_6_0= RULE_STRING ) ) )? (otherlv_7= 'email' ( (lv_email_8_0= RULE_STRING ) ) )? (otherlv_9= 'isAdmin' ( (lv_admin_10_0= ruleBooleanDef ) ) )? otherlv_11= '}' )? ) ;
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
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:217:28: ( ( ( (lv_name_0_0= ruleUSER_DEF_REFERENCE ) ) ( (lv_userDefOption_1_0= ruleUserDefOption ) )* (otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'userId' ( (lv_userId_6_0= RULE_STRING ) ) )? (otherlv_7= 'email' ( (lv_email_8_0= RULE_STRING ) ) )? (otherlv_9= 'isAdmin' ( (lv_admin_10_0= ruleBooleanDef ) ) )? otherlv_11= '}' )? ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:218:1: ( ( (lv_name_0_0= ruleUSER_DEF_REFERENCE ) ) ( (lv_userDefOption_1_0= ruleUserDefOption ) )* (otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'userId' ( (lv_userId_6_0= RULE_STRING ) ) )? (otherlv_7= 'email' ( (lv_email_8_0= RULE_STRING ) ) )? (otherlv_9= 'isAdmin' ( (lv_admin_10_0= ruleBooleanDef ) ) )? otherlv_11= '}' )? )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:218:1: ( ( (lv_name_0_0= ruleUSER_DEF_REFERENCE ) ) ( (lv_userDefOption_1_0= ruleUserDefOption ) )* (otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'userId' ( (lv_userId_6_0= RULE_STRING ) ) )? (otherlv_7= 'email' ( (lv_email_8_0= RULE_STRING ) ) )? (otherlv_9= 'isAdmin' ( (lv_admin_10_0= ruleBooleanDef ) ) )? otherlv_11= '}' )? )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:218:2: ( (lv_name_0_0= ruleUSER_DEF_REFERENCE ) ) ( (lv_userDefOption_1_0= ruleUserDefOption ) )* (otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'userId' ( (lv_userId_6_0= RULE_STRING ) ) )? (otherlv_7= 'email' ( (lv_email_8_0= RULE_STRING ) ) )? (otherlv_9= 'isAdmin' ( (lv_admin_10_0= ruleBooleanDef ) ) )? otherlv_11= '}' )?
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:218:2: ( (lv_name_0_0= ruleUSER_DEF_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:219:1: (lv_name_0_0= ruleUSER_DEF_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:219:1: (lv_name_0_0= ruleUSER_DEF_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:220:3: lv_name_0_0= ruleUSER_DEF_REFERENCE
            {
             
            	        newCompositeNode(grammarAccess.getUserDefAccess().getNameUSER_DEF_REFERENCEParserRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_ruleUSER_DEF_REFERENCE_in_ruleUserDef441);
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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:236:2: ( (lv_userDefOption_1_0= ruleUserDefOption ) )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==RULE_STRING||LA5_0==78) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:237:1: (lv_userDefOption_1_0= ruleUserDefOption )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:237:1: (lv_userDefOption_1_0= ruleUserDefOption )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:238:3: lv_userDefOption_1_0= ruleUserDefOption
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getUserDefAccess().getUserDefOptionUserDefOptionParserRuleCall_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleUserDefOption_in_ruleUserDef462);
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
            	    break loop5;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:254:3: (otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'userId' ( (lv_userId_6_0= RULE_STRING ) ) )? (otherlv_7= 'email' ( (lv_email_8_0= RULE_STRING ) ) )? (otherlv_9= 'isAdmin' ( (lv_admin_10_0= ruleBooleanDef ) ) )? otherlv_11= '}' )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==15) ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:254:5: otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'userId' ( (lv_userId_6_0= RULE_STRING ) ) )? (otherlv_7= 'email' ( (lv_email_8_0= RULE_STRING ) ) )? (otherlv_9= 'isAdmin' ( (lv_admin_10_0= ruleBooleanDef ) ) )? otherlv_11= '}'
                    {
                    otherlv_2=(Token)match(input,15,FOLLOW_15_in_ruleUserDef476); 

                        	newLeafNode(otherlv_2, grammarAccess.getUserDefAccess().getLeftCurlyBracketKeyword_2_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:258:1: (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )?
                    int alt6=2;
                    int LA6_0 = input.LA(1);

                    if ( (LA6_0==16) ) {
                        alt6=1;
                    }
                    switch (alt6) {
                        case 1 :
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:258:3: otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) )
                            {
                            otherlv_3=(Token)match(input,16,FOLLOW_16_in_ruleUserDef489); 

                                	newLeafNode(otherlv_3, grammarAccess.getUserDefAccess().getActiveKeyword_2_1_0());
                                
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:262:1: ( (lv_active_4_0= ruleBooleanDef ) )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:263:1: (lv_active_4_0= ruleBooleanDef )
                            {
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:263:1: (lv_active_4_0= ruleBooleanDef )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:264:3: lv_active_4_0= ruleBooleanDef
                            {
                             
                            	        newCompositeNode(grammarAccess.getUserDefAccess().getActiveBooleanDefEnumRuleCall_2_1_1_0()); 
                            	    
                            pushFollow(FOLLOW_ruleBooleanDef_in_ruleUserDef510);
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

                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:280:4: (otherlv_5= 'userId' ( (lv_userId_6_0= RULE_STRING ) ) )?
                    int alt7=2;
                    int LA7_0 = input.LA(1);

                    if ( (LA7_0==17) ) {
                        alt7=1;
                    }
                    switch (alt7) {
                        case 1 :
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:280:6: otherlv_5= 'userId' ( (lv_userId_6_0= RULE_STRING ) )
                            {
                            otherlv_5=(Token)match(input,17,FOLLOW_17_in_ruleUserDef525); 

                                	newLeafNode(otherlv_5, grammarAccess.getUserDefAccess().getUserIdKeyword_2_2_0());
                                
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:284:1: ( (lv_userId_6_0= RULE_STRING ) )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:285:1: (lv_userId_6_0= RULE_STRING )
                            {
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:285:1: (lv_userId_6_0= RULE_STRING )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:286:3: lv_userId_6_0= RULE_STRING
                            {
                            lv_userId_6_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleUserDef542); 

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

                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:302:4: (otherlv_7= 'email' ( (lv_email_8_0= RULE_STRING ) ) )?
                    int alt8=2;
                    int LA8_0 = input.LA(1);

                    if ( (LA8_0==18) ) {
                        alt8=1;
                    }
                    switch (alt8) {
                        case 1 :
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:302:6: otherlv_7= 'email' ( (lv_email_8_0= RULE_STRING ) )
                            {
                            otherlv_7=(Token)match(input,18,FOLLOW_18_in_ruleUserDef562); 

                                	newLeafNode(otherlv_7, grammarAccess.getUserDefAccess().getEmailKeyword_2_3_0());
                                
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:306:1: ( (lv_email_8_0= RULE_STRING ) )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:307:1: (lv_email_8_0= RULE_STRING )
                            {
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:307:1: (lv_email_8_0= RULE_STRING )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:308:3: lv_email_8_0= RULE_STRING
                            {
                            lv_email_8_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleUserDef579); 

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

                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:324:4: (otherlv_9= 'isAdmin' ( (lv_admin_10_0= ruleBooleanDef ) ) )?
                    int alt9=2;
                    int LA9_0 = input.LA(1);

                    if ( (LA9_0==19) ) {
                        alt9=1;
                    }
                    switch (alt9) {
                        case 1 :
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:324:6: otherlv_9= 'isAdmin' ( (lv_admin_10_0= ruleBooleanDef ) )
                            {
                            otherlv_9=(Token)match(input,19,FOLLOW_19_in_ruleUserDef599); 

                                	newLeafNode(otherlv_9, grammarAccess.getUserDefAccess().getIsAdminKeyword_2_4_0());
                                
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:328:1: ( (lv_admin_10_0= ruleBooleanDef ) )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:329:1: (lv_admin_10_0= ruleBooleanDef )
                            {
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:329:1: (lv_admin_10_0= ruleBooleanDef )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:330:3: lv_admin_10_0= ruleBooleanDef
                            {
                             
                            	        newCompositeNode(grammarAccess.getUserDefAccess().getAdminBooleanDefEnumRuleCall_2_4_1_0()); 
                            	    
                            pushFollow(FOLLOW_ruleBooleanDef_in_ruleUserDef620);
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

                    otherlv_11=(Token)match(input,20,FOLLOW_20_in_ruleUserDef634); 

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


    // $ANTLR start "entryRuleTEAM_DEF_REFERENCE"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:358:1: entryRuleTEAM_DEF_REFERENCE returns [String current=null] : iv_ruleTEAM_DEF_REFERENCE= ruleTEAM_DEF_REFERENCE EOF ;
    public final String entryRuleTEAM_DEF_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleTEAM_DEF_REFERENCE = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:359:2: (iv_ruleTEAM_DEF_REFERENCE= ruleTEAM_DEF_REFERENCE EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:360:2: iv_ruleTEAM_DEF_REFERENCE= ruleTEAM_DEF_REFERENCE EOF
            {
             newCompositeNode(grammarAccess.getTEAM_DEF_REFERENCERule()); 
            pushFollow(FOLLOW_ruleTEAM_DEF_REFERENCE_in_entryRuleTEAM_DEF_REFERENCE673);
            iv_ruleTEAM_DEF_REFERENCE=ruleTEAM_DEF_REFERENCE();

            state._fsp--;

             current =iv_ruleTEAM_DEF_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleTEAM_DEF_REFERENCE684); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:367:1: ruleTEAM_DEF_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleTEAM_DEF_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:370:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:371:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleTEAM_DEF_REFERENCE723); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:386:1: entryRuleTeamDef returns [EObject current=null] : iv_ruleTeamDef= ruleTeamDef EOF ;
    public final EObject entryRuleTeamDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleTeamDef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:387:2: (iv_ruleTeamDef= ruleTeamDef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:388:2: iv_ruleTeamDef= ruleTeamDef EOF
            {
             newCompositeNode(grammarAccess.getTeamDefRule()); 
            pushFollow(FOLLOW_ruleTeamDef_in_entryRuleTeamDef767);
            iv_ruleTeamDef=ruleTeamDef();

            state._fsp--;

             current =iv_ruleTeamDef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleTeamDef777); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:395:1: ruleTeamDef returns [EObject current=null] : ( ( (lv_name_0_0= ruleTEAM_DEF_REFERENCE ) ) ( (lv_teamDefOption_1_0= ruleTeamDefOption ) )* otherlv_2= '{' (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )? (otherlv_5= 'active' ( (lv_active_6_0= ruleBooleanDef ) ) )? (otherlv_7= 'staticId' ( (lv_staticId_8_0= RULE_STRING ) ) )* (otherlv_9= 'lead' ( (lv_lead_10_0= ruleUserRef ) ) )* (otherlv_11= 'member' ( (lv_member_12_0= ruleUserRef ) ) )* (otherlv_13= 'privileged' ( (lv_privileged_14_0= ruleUserRef ) ) )* (otherlv_15= 'workDefinition' ( (lv_workDefinition_16_0= RULE_STRING ) ) )? (otherlv_17= 'relatedTaskWorkDefinition' ( (lv_relatedTaskWorkDefinition_18_0= RULE_STRING ) ) )? (otherlv_19= 'accessContextId' ( (lv_accessContextId_20_0= RULE_STRING ) ) )* (otherlv_21= 'version' ( (lv_version_22_0= ruleVersionDef ) ) )* (otherlv_23= 'children' otherlv_24= '{' (otherlv_25= 'teamDefinition' ( (lv_children_26_0= ruleTeamDef ) ) )+ otherlv_27= '}' )? otherlv_28= '}' ) ;
    public final EObject ruleTeamDef() throws RecognitionException {
        EObject current = null;

        Token otherlv_2=null;
        Token otherlv_3=null;
        Token lv_guid_4_0=null;
        Token otherlv_5=null;
        Token otherlv_7=null;
        Token lv_staticId_8_0=null;
        Token otherlv_9=null;
        Token otherlv_11=null;
        Token otherlv_13=null;
        Token otherlv_15=null;
        Token lv_workDefinition_16_0=null;
        Token otherlv_17=null;
        Token lv_relatedTaskWorkDefinition_18_0=null;
        Token otherlv_19=null;
        Token lv_accessContextId_20_0=null;
        Token otherlv_21=null;
        Token otherlv_23=null;
        Token otherlv_24=null;
        Token otherlv_25=null;
        Token otherlv_27=null;
        Token otherlv_28=null;
        AntlrDatatypeRuleToken lv_name_0_0 = null;

        AntlrDatatypeRuleToken lv_teamDefOption_1_0 = null;

        Enumerator lv_active_6_0 = null;

        EObject lv_lead_10_0 = null;

        EObject lv_member_12_0 = null;

        EObject lv_privileged_14_0 = null;

        EObject lv_version_22_0 = null;

        EObject lv_children_26_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:398:28: ( ( ( (lv_name_0_0= ruleTEAM_DEF_REFERENCE ) ) ( (lv_teamDefOption_1_0= ruleTeamDefOption ) )* otherlv_2= '{' (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )? (otherlv_5= 'active' ( (lv_active_6_0= ruleBooleanDef ) ) )? (otherlv_7= 'staticId' ( (lv_staticId_8_0= RULE_STRING ) ) )* (otherlv_9= 'lead' ( (lv_lead_10_0= ruleUserRef ) ) )* (otherlv_11= 'member' ( (lv_member_12_0= ruleUserRef ) ) )* (otherlv_13= 'privileged' ( (lv_privileged_14_0= ruleUserRef ) ) )* (otherlv_15= 'workDefinition' ( (lv_workDefinition_16_0= RULE_STRING ) ) )? (otherlv_17= 'relatedTaskWorkDefinition' ( (lv_relatedTaskWorkDefinition_18_0= RULE_STRING ) ) )? (otherlv_19= 'accessContextId' ( (lv_accessContextId_20_0= RULE_STRING ) ) )* (otherlv_21= 'version' ( (lv_version_22_0= ruleVersionDef ) ) )* (otherlv_23= 'children' otherlv_24= '{' (otherlv_25= 'teamDefinition' ( (lv_children_26_0= ruleTeamDef ) ) )+ otherlv_27= '}' )? otherlv_28= '}' ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:399:1: ( ( (lv_name_0_0= ruleTEAM_DEF_REFERENCE ) ) ( (lv_teamDefOption_1_0= ruleTeamDefOption ) )* otherlv_2= '{' (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )? (otherlv_5= 'active' ( (lv_active_6_0= ruleBooleanDef ) ) )? (otherlv_7= 'staticId' ( (lv_staticId_8_0= RULE_STRING ) ) )* (otherlv_9= 'lead' ( (lv_lead_10_0= ruleUserRef ) ) )* (otherlv_11= 'member' ( (lv_member_12_0= ruleUserRef ) ) )* (otherlv_13= 'privileged' ( (lv_privileged_14_0= ruleUserRef ) ) )* (otherlv_15= 'workDefinition' ( (lv_workDefinition_16_0= RULE_STRING ) ) )? (otherlv_17= 'relatedTaskWorkDefinition' ( (lv_relatedTaskWorkDefinition_18_0= RULE_STRING ) ) )? (otherlv_19= 'accessContextId' ( (lv_accessContextId_20_0= RULE_STRING ) ) )* (otherlv_21= 'version' ( (lv_version_22_0= ruleVersionDef ) ) )* (otherlv_23= 'children' otherlv_24= '{' (otherlv_25= 'teamDefinition' ( (lv_children_26_0= ruleTeamDef ) ) )+ otherlv_27= '}' )? otherlv_28= '}' )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:399:1: ( ( (lv_name_0_0= ruleTEAM_DEF_REFERENCE ) ) ( (lv_teamDefOption_1_0= ruleTeamDefOption ) )* otherlv_2= '{' (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )? (otherlv_5= 'active' ( (lv_active_6_0= ruleBooleanDef ) ) )? (otherlv_7= 'staticId' ( (lv_staticId_8_0= RULE_STRING ) ) )* (otherlv_9= 'lead' ( (lv_lead_10_0= ruleUserRef ) ) )* (otherlv_11= 'member' ( (lv_member_12_0= ruleUserRef ) ) )* (otherlv_13= 'privileged' ( (lv_privileged_14_0= ruleUserRef ) ) )* (otherlv_15= 'workDefinition' ( (lv_workDefinition_16_0= RULE_STRING ) ) )? (otherlv_17= 'relatedTaskWorkDefinition' ( (lv_relatedTaskWorkDefinition_18_0= RULE_STRING ) ) )? (otherlv_19= 'accessContextId' ( (lv_accessContextId_20_0= RULE_STRING ) ) )* (otherlv_21= 'version' ( (lv_version_22_0= ruleVersionDef ) ) )* (otherlv_23= 'children' otherlv_24= '{' (otherlv_25= 'teamDefinition' ( (lv_children_26_0= ruleTeamDef ) ) )+ otherlv_27= '}' )? otherlv_28= '}' )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:399:2: ( (lv_name_0_0= ruleTEAM_DEF_REFERENCE ) ) ( (lv_teamDefOption_1_0= ruleTeamDefOption ) )* otherlv_2= '{' (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )? (otherlv_5= 'active' ( (lv_active_6_0= ruleBooleanDef ) ) )? (otherlv_7= 'staticId' ( (lv_staticId_8_0= RULE_STRING ) ) )* (otherlv_9= 'lead' ( (lv_lead_10_0= ruleUserRef ) ) )* (otherlv_11= 'member' ( (lv_member_12_0= ruleUserRef ) ) )* (otherlv_13= 'privileged' ( (lv_privileged_14_0= ruleUserRef ) ) )* (otherlv_15= 'workDefinition' ( (lv_workDefinition_16_0= RULE_STRING ) ) )? (otherlv_17= 'relatedTaskWorkDefinition' ( (lv_relatedTaskWorkDefinition_18_0= RULE_STRING ) ) )? (otherlv_19= 'accessContextId' ( (lv_accessContextId_20_0= RULE_STRING ) ) )* (otherlv_21= 'version' ( (lv_version_22_0= ruleVersionDef ) ) )* (otherlv_23= 'children' otherlv_24= '{' (otherlv_25= 'teamDefinition' ( (lv_children_26_0= ruleTeamDef ) ) )+ otherlv_27= '}' )? otherlv_28= '}'
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:399:2: ( (lv_name_0_0= ruleTEAM_DEF_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:400:1: (lv_name_0_0= ruleTEAM_DEF_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:400:1: (lv_name_0_0= ruleTEAM_DEF_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:401:3: lv_name_0_0= ruleTEAM_DEF_REFERENCE
            {
             
            	        newCompositeNode(grammarAccess.getTeamDefAccess().getNameTEAM_DEF_REFERENCEParserRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_ruleTEAM_DEF_REFERENCE_in_ruleTeamDef823);
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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:417:2: ( (lv_teamDefOption_1_0= ruleTeamDefOption ) )*
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);

                if ( (LA11_0==RULE_STRING||LA11_0==78) ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:418:1: (lv_teamDefOption_1_0= ruleTeamDefOption )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:418:1: (lv_teamDefOption_1_0= ruleTeamDefOption )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:419:3: lv_teamDefOption_1_0= ruleTeamDefOption
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getTeamDefAccess().getTeamDefOptionTeamDefOptionParserRuleCall_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleTeamDefOption_in_ruleTeamDef844);
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
            	    break loop11;
                }
            } while (true);

            otherlv_2=(Token)match(input,15,FOLLOW_15_in_ruleTeamDef857); 

                	newLeafNode(otherlv_2, grammarAccess.getTeamDefAccess().getLeftCurlyBracketKeyword_2());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:439:1: (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==21) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:439:3: otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) )
                    {
                    otherlv_3=(Token)match(input,21,FOLLOW_21_in_ruleTeamDef870); 

                        	newLeafNode(otherlv_3, grammarAccess.getTeamDefAccess().getGuidKeyword_3_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:443:1: ( (lv_guid_4_0= RULE_STRING ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:444:1: (lv_guid_4_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:444:1: (lv_guid_4_0= RULE_STRING )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:445:3: lv_guid_4_0= RULE_STRING
                    {
                    lv_guid_4_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleTeamDef887); 

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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:461:4: (otherlv_5= 'active' ( (lv_active_6_0= ruleBooleanDef ) ) )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==16) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:461:6: otherlv_5= 'active' ( (lv_active_6_0= ruleBooleanDef ) )
                    {
                    otherlv_5=(Token)match(input,16,FOLLOW_16_in_ruleTeamDef907); 

                        	newLeafNode(otherlv_5, grammarAccess.getTeamDefAccess().getActiveKeyword_4_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:465:1: ( (lv_active_6_0= ruleBooleanDef ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:466:1: (lv_active_6_0= ruleBooleanDef )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:466:1: (lv_active_6_0= ruleBooleanDef )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:467:3: lv_active_6_0= ruleBooleanDef
                    {
                     
                    	        newCompositeNode(grammarAccess.getTeamDefAccess().getActiveBooleanDefEnumRuleCall_4_1_0()); 
                    	    
                    pushFollow(FOLLOW_ruleBooleanDef_in_ruleTeamDef928);
                    lv_active_6_0=ruleBooleanDef();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getTeamDefRule());
                    	        }
                           		set(
                           			current, 
                           			"active",
                            		lv_active_6_0, 
                            		"BooleanDef");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:483:4: (otherlv_7= 'staticId' ( (lv_staticId_8_0= RULE_STRING ) ) )*
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);

                if ( (LA14_0==22) ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:483:6: otherlv_7= 'staticId' ( (lv_staticId_8_0= RULE_STRING ) )
            	    {
            	    otherlv_7=(Token)match(input,22,FOLLOW_22_in_ruleTeamDef943); 

            	        	newLeafNode(otherlv_7, grammarAccess.getTeamDefAccess().getStaticIdKeyword_5_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:487:1: ( (lv_staticId_8_0= RULE_STRING ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:488:1: (lv_staticId_8_0= RULE_STRING )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:488:1: (lv_staticId_8_0= RULE_STRING )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:489:3: lv_staticId_8_0= RULE_STRING
            	    {
            	    lv_staticId_8_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleTeamDef960); 

            	    			newLeafNode(lv_staticId_8_0, grammarAccess.getTeamDefAccess().getStaticIdSTRINGTerminalRuleCall_5_1_0()); 
            	    		

            	    	        if (current==null) {
            	    	            current = createModelElement(grammarAccess.getTeamDefRule());
            	    	        }
            	           		addWithLastConsumed(
            	           			current, 
            	           			"staticId",
            	            		lv_staticId_8_0, 
            	            		"STRING");
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop14;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:505:4: (otherlv_9= 'lead' ( (lv_lead_10_0= ruleUserRef ) ) )*
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( (LA15_0==23) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:505:6: otherlv_9= 'lead' ( (lv_lead_10_0= ruleUserRef ) )
            	    {
            	    otherlv_9=(Token)match(input,23,FOLLOW_23_in_ruleTeamDef980); 

            	        	newLeafNode(otherlv_9, grammarAccess.getTeamDefAccess().getLeadKeyword_6_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:509:1: ( (lv_lead_10_0= ruleUserRef ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:510:1: (lv_lead_10_0= ruleUserRef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:510:1: (lv_lead_10_0= ruleUserRef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:511:3: lv_lead_10_0= ruleUserRef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getTeamDefAccess().getLeadUserRefParserRuleCall_6_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleUserRef_in_ruleTeamDef1001);
            	    lv_lead_10_0=ruleUserRef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getTeamDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"lead",
            	            		lv_lead_10_0, 
            	            		"UserRef");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop15;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:527:4: (otherlv_11= 'member' ( (lv_member_12_0= ruleUserRef ) ) )*
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( (LA16_0==24) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:527:6: otherlv_11= 'member' ( (lv_member_12_0= ruleUserRef ) )
            	    {
            	    otherlv_11=(Token)match(input,24,FOLLOW_24_in_ruleTeamDef1016); 

            	        	newLeafNode(otherlv_11, grammarAccess.getTeamDefAccess().getMemberKeyword_7_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:531:1: ( (lv_member_12_0= ruleUserRef ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:532:1: (lv_member_12_0= ruleUserRef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:532:1: (lv_member_12_0= ruleUserRef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:533:3: lv_member_12_0= ruleUserRef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getTeamDefAccess().getMemberUserRefParserRuleCall_7_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleUserRef_in_ruleTeamDef1037);
            	    lv_member_12_0=ruleUserRef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getTeamDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"member",
            	            		lv_member_12_0, 
            	            		"UserRef");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop16;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:549:4: (otherlv_13= 'privileged' ( (lv_privileged_14_0= ruleUserRef ) ) )*
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);

                if ( (LA17_0==25) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:549:6: otherlv_13= 'privileged' ( (lv_privileged_14_0= ruleUserRef ) )
            	    {
            	    otherlv_13=(Token)match(input,25,FOLLOW_25_in_ruleTeamDef1052); 

            	        	newLeafNode(otherlv_13, grammarAccess.getTeamDefAccess().getPrivilegedKeyword_8_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:553:1: ( (lv_privileged_14_0= ruleUserRef ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:554:1: (lv_privileged_14_0= ruleUserRef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:554:1: (lv_privileged_14_0= ruleUserRef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:555:3: lv_privileged_14_0= ruleUserRef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getTeamDefAccess().getPrivilegedUserRefParserRuleCall_8_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleUserRef_in_ruleTeamDef1073);
            	    lv_privileged_14_0=ruleUserRef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getTeamDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"privileged",
            	            		lv_privileged_14_0, 
            	            		"UserRef");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop17;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:571:4: (otherlv_15= 'workDefinition' ( (lv_workDefinition_16_0= RULE_STRING ) ) )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==11) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:571:6: otherlv_15= 'workDefinition' ( (lv_workDefinition_16_0= RULE_STRING ) )
                    {
                    otherlv_15=(Token)match(input,11,FOLLOW_11_in_ruleTeamDef1088); 

                        	newLeafNode(otherlv_15, grammarAccess.getTeamDefAccess().getWorkDefinitionKeyword_9_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:575:1: ( (lv_workDefinition_16_0= RULE_STRING ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:576:1: (lv_workDefinition_16_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:576:1: (lv_workDefinition_16_0= RULE_STRING )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:577:3: lv_workDefinition_16_0= RULE_STRING
                    {
                    lv_workDefinition_16_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleTeamDef1105); 

                    			newLeafNode(lv_workDefinition_16_0, grammarAccess.getTeamDefAccess().getWorkDefinitionSTRINGTerminalRuleCall_9_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getTeamDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"workDefinition",
                            		lv_workDefinition_16_0, 
                            		"STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:593:4: (otherlv_17= 'relatedTaskWorkDefinition' ( (lv_relatedTaskWorkDefinition_18_0= RULE_STRING ) ) )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==26) ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:593:6: otherlv_17= 'relatedTaskWorkDefinition' ( (lv_relatedTaskWorkDefinition_18_0= RULE_STRING ) )
                    {
                    otherlv_17=(Token)match(input,26,FOLLOW_26_in_ruleTeamDef1125); 

                        	newLeafNode(otherlv_17, grammarAccess.getTeamDefAccess().getRelatedTaskWorkDefinitionKeyword_10_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:597:1: ( (lv_relatedTaskWorkDefinition_18_0= RULE_STRING ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:598:1: (lv_relatedTaskWorkDefinition_18_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:598:1: (lv_relatedTaskWorkDefinition_18_0= RULE_STRING )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:599:3: lv_relatedTaskWorkDefinition_18_0= RULE_STRING
                    {
                    lv_relatedTaskWorkDefinition_18_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleTeamDef1142); 

                    			newLeafNode(lv_relatedTaskWorkDefinition_18_0, grammarAccess.getTeamDefAccess().getRelatedTaskWorkDefinitionSTRINGTerminalRuleCall_10_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getTeamDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"relatedTaskWorkDefinition",
                            		lv_relatedTaskWorkDefinition_18_0, 
                            		"STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:615:4: (otherlv_19= 'accessContextId' ( (lv_accessContextId_20_0= RULE_STRING ) ) )*
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( (LA20_0==27) ) {
                    alt20=1;
                }


                switch (alt20) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:615:6: otherlv_19= 'accessContextId' ( (lv_accessContextId_20_0= RULE_STRING ) )
            	    {
            	    otherlv_19=(Token)match(input,27,FOLLOW_27_in_ruleTeamDef1162); 

            	        	newLeafNode(otherlv_19, grammarAccess.getTeamDefAccess().getAccessContextIdKeyword_11_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:619:1: ( (lv_accessContextId_20_0= RULE_STRING ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:620:1: (lv_accessContextId_20_0= RULE_STRING )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:620:1: (lv_accessContextId_20_0= RULE_STRING )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:621:3: lv_accessContextId_20_0= RULE_STRING
            	    {
            	    lv_accessContextId_20_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleTeamDef1179); 

            	    			newLeafNode(lv_accessContextId_20_0, grammarAccess.getTeamDefAccess().getAccessContextIdSTRINGTerminalRuleCall_11_1_0()); 
            	    		

            	    	        if (current==null) {
            	    	            current = createModelElement(grammarAccess.getTeamDefRule());
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
            	    break loop20;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:637:4: (otherlv_21= 'version' ( (lv_version_22_0= ruleVersionDef ) ) )*
            loop21:
            do {
                int alt21=2;
                int LA21_0 = input.LA(1);

                if ( (LA21_0==28) ) {
                    alt21=1;
                }


                switch (alt21) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:637:6: otherlv_21= 'version' ( (lv_version_22_0= ruleVersionDef ) )
            	    {
            	    otherlv_21=(Token)match(input,28,FOLLOW_28_in_ruleTeamDef1199); 

            	        	newLeafNode(otherlv_21, grammarAccess.getTeamDefAccess().getVersionKeyword_12_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:641:1: ( (lv_version_22_0= ruleVersionDef ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:642:1: (lv_version_22_0= ruleVersionDef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:642:1: (lv_version_22_0= ruleVersionDef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:643:3: lv_version_22_0= ruleVersionDef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getTeamDefAccess().getVersionVersionDefParserRuleCall_12_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleVersionDef_in_ruleTeamDef1220);
            	    lv_version_22_0=ruleVersionDef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getTeamDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"version",
            	            		lv_version_22_0, 
            	            		"VersionDef");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop21;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:659:4: (otherlv_23= 'children' otherlv_24= '{' (otherlv_25= 'teamDefinition' ( (lv_children_26_0= ruleTeamDef ) ) )+ otherlv_27= '}' )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==29) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:659:6: otherlv_23= 'children' otherlv_24= '{' (otherlv_25= 'teamDefinition' ( (lv_children_26_0= ruleTeamDef ) ) )+ otherlv_27= '}'
                    {
                    otherlv_23=(Token)match(input,29,FOLLOW_29_in_ruleTeamDef1235); 

                        	newLeafNode(otherlv_23, grammarAccess.getTeamDefAccess().getChildrenKeyword_13_0());
                        
                    otherlv_24=(Token)match(input,15,FOLLOW_15_in_ruleTeamDef1247); 

                        	newLeafNode(otherlv_24, grammarAccess.getTeamDefAccess().getLeftCurlyBracketKeyword_13_1());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:667:1: (otherlv_25= 'teamDefinition' ( (lv_children_26_0= ruleTeamDef ) ) )+
                    int cnt22=0;
                    loop22:
                    do {
                        int alt22=2;
                        int LA22_0 = input.LA(1);

                        if ( (LA22_0==13) ) {
                            alt22=1;
                        }


                        switch (alt22) {
                    	case 1 :
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:667:3: otherlv_25= 'teamDefinition' ( (lv_children_26_0= ruleTeamDef ) )
                    	    {
                    	    otherlv_25=(Token)match(input,13,FOLLOW_13_in_ruleTeamDef1260); 

                    	        	newLeafNode(otherlv_25, grammarAccess.getTeamDefAccess().getTeamDefinitionKeyword_13_2_0());
                    	        
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:671:1: ( (lv_children_26_0= ruleTeamDef ) )
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:672:1: (lv_children_26_0= ruleTeamDef )
                    	    {
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:672:1: (lv_children_26_0= ruleTeamDef )
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:673:3: lv_children_26_0= ruleTeamDef
                    	    {
                    	     
                    	    	        newCompositeNode(grammarAccess.getTeamDefAccess().getChildrenTeamDefParserRuleCall_13_2_1_0()); 
                    	    	    
                    	    pushFollow(FOLLOW_ruleTeamDef_in_ruleTeamDef1281);
                    	    lv_children_26_0=ruleTeamDef();

                    	    state._fsp--;


                    	    	        if (current==null) {
                    	    	            current = createModelElementForParent(grammarAccess.getTeamDefRule());
                    	    	        }
                    	           		add(
                    	           			current, 
                    	           			"children",
                    	            		lv_children_26_0, 
                    	            		"TeamDef");
                    	    	        afterParserOrEnumRuleCall();
                    	    	    

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt22 >= 1 ) break loop22;
                                EarlyExitException eee =
                                    new EarlyExitException(22, input);
                                throw eee;
                        }
                        cnt22++;
                    } while (true);

                    otherlv_27=(Token)match(input,20,FOLLOW_20_in_ruleTeamDef1295); 

                        	newLeafNode(otherlv_27, grammarAccess.getTeamDefAccess().getRightCurlyBracketKeyword_13_3());
                        

                    }
                    break;

            }

            otherlv_28=(Token)match(input,20,FOLLOW_20_in_ruleTeamDef1309); 

                	newLeafNode(otherlv_28, grammarAccess.getTeamDefAccess().getRightCurlyBracketKeyword_14());
                

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:705:1: entryRuleAI_DEF_REFERENCE returns [String current=null] : iv_ruleAI_DEF_REFERENCE= ruleAI_DEF_REFERENCE EOF ;
    public final String entryRuleAI_DEF_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleAI_DEF_REFERENCE = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:706:2: (iv_ruleAI_DEF_REFERENCE= ruleAI_DEF_REFERENCE EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:707:2: iv_ruleAI_DEF_REFERENCE= ruleAI_DEF_REFERENCE EOF
            {
             newCompositeNode(grammarAccess.getAI_DEF_REFERENCERule()); 
            pushFollow(FOLLOW_ruleAI_DEF_REFERENCE_in_entryRuleAI_DEF_REFERENCE1346);
            iv_ruleAI_DEF_REFERENCE=ruleAI_DEF_REFERENCE();

            state._fsp--;

             current =iv_ruleAI_DEF_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAI_DEF_REFERENCE1357); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:714:1: ruleAI_DEF_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleAI_DEF_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:717:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:718:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAI_DEF_REFERENCE1396); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:733:1: entryRuleActionableItemDef returns [EObject current=null] : iv_ruleActionableItemDef= ruleActionableItemDef EOF ;
    public final EObject entryRuleActionableItemDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleActionableItemDef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:734:2: (iv_ruleActionableItemDef= ruleActionableItemDef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:735:2: iv_ruleActionableItemDef= ruleActionableItemDef EOF
            {
             newCompositeNode(grammarAccess.getActionableItemDefRule()); 
            pushFollow(FOLLOW_ruleActionableItemDef_in_entryRuleActionableItemDef1440);
            iv_ruleActionableItemDef=ruleActionableItemDef();

            state._fsp--;

             current =iv_ruleActionableItemDef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleActionableItemDef1450); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:742:1: ruleActionableItemDef returns [EObject current=null] : ( ( (lv_name_0_0= ruleAI_DEF_REFERENCE ) ) ( (lv_aiDefOption_1_0= ruleActionableItemOption ) )* (otherlv_2= '{' (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )? (otherlv_5= 'active' ( (lv_active_6_0= ruleBooleanDef ) ) )? (otherlv_7= 'actionable' ( (lv_actionable_8_0= ruleBooleanDef ) ) )? (otherlv_9= 'lead' ( (lv_lead_10_0= ruleUserRef ) ) )* (otherlv_11= 'owner' ( (lv_owner_12_0= ruleUserRef ) ) )* (otherlv_13= 'staticId' ( (lv_staticId_14_0= RULE_STRING ) ) )* (otherlv_15= 'team' ( (lv_teamDef_16_0= RULE_STRING ) ) )? (otherlv_17= 'accessContextId' ( (lv_accessContextId_18_0= RULE_STRING ) ) )* (otherlv_19= 'children' otherlv_20= '{' (otherlv_21= 'actionableItem' ( (lv_children_22_0= ruleActionableItemDef ) ) )+ otherlv_23= '}' )? otherlv_24= '}' )? ) ;
    public final EObject ruleActionableItemDef() throws RecognitionException {
        EObject current = null;

        Token otherlv_2=null;
        Token otherlv_3=null;
        Token lv_guid_4_0=null;
        Token otherlv_5=null;
        Token otherlv_7=null;
        Token otherlv_9=null;
        Token otherlv_11=null;
        Token otherlv_13=null;
        Token lv_staticId_14_0=null;
        Token otherlv_15=null;
        Token lv_teamDef_16_0=null;
        Token otherlv_17=null;
        Token lv_accessContextId_18_0=null;
        Token otherlv_19=null;
        Token otherlv_20=null;
        Token otherlv_21=null;
        Token otherlv_23=null;
        Token otherlv_24=null;
        AntlrDatatypeRuleToken lv_name_0_0 = null;

        AntlrDatatypeRuleToken lv_aiDefOption_1_0 = null;

        Enumerator lv_active_6_0 = null;

        Enumerator lv_actionable_8_0 = null;

        EObject lv_lead_10_0 = null;

        EObject lv_owner_12_0 = null;

        EObject lv_children_22_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:745:28: ( ( ( (lv_name_0_0= ruleAI_DEF_REFERENCE ) ) ( (lv_aiDefOption_1_0= ruleActionableItemOption ) )* (otherlv_2= '{' (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )? (otherlv_5= 'active' ( (lv_active_6_0= ruleBooleanDef ) ) )? (otherlv_7= 'actionable' ( (lv_actionable_8_0= ruleBooleanDef ) ) )? (otherlv_9= 'lead' ( (lv_lead_10_0= ruleUserRef ) ) )* (otherlv_11= 'owner' ( (lv_owner_12_0= ruleUserRef ) ) )* (otherlv_13= 'staticId' ( (lv_staticId_14_0= RULE_STRING ) ) )* (otherlv_15= 'team' ( (lv_teamDef_16_0= RULE_STRING ) ) )? (otherlv_17= 'accessContextId' ( (lv_accessContextId_18_0= RULE_STRING ) ) )* (otherlv_19= 'children' otherlv_20= '{' (otherlv_21= 'actionableItem' ( (lv_children_22_0= ruleActionableItemDef ) ) )+ otherlv_23= '}' )? otherlv_24= '}' )? ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:746:1: ( ( (lv_name_0_0= ruleAI_DEF_REFERENCE ) ) ( (lv_aiDefOption_1_0= ruleActionableItemOption ) )* (otherlv_2= '{' (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )? (otherlv_5= 'active' ( (lv_active_6_0= ruleBooleanDef ) ) )? (otherlv_7= 'actionable' ( (lv_actionable_8_0= ruleBooleanDef ) ) )? (otherlv_9= 'lead' ( (lv_lead_10_0= ruleUserRef ) ) )* (otherlv_11= 'owner' ( (lv_owner_12_0= ruleUserRef ) ) )* (otherlv_13= 'staticId' ( (lv_staticId_14_0= RULE_STRING ) ) )* (otherlv_15= 'team' ( (lv_teamDef_16_0= RULE_STRING ) ) )? (otherlv_17= 'accessContextId' ( (lv_accessContextId_18_0= RULE_STRING ) ) )* (otherlv_19= 'children' otherlv_20= '{' (otherlv_21= 'actionableItem' ( (lv_children_22_0= ruleActionableItemDef ) ) )+ otherlv_23= '}' )? otherlv_24= '}' )? )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:746:1: ( ( (lv_name_0_0= ruleAI_DEF_REFERENCE ) ) ( (lv_aiDefOption_1_0= ruleActionableItemOption ) )* (otherlv_2= '{' (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )? (otherlv_5= 'active' ( (lv_active_6_0= ruleBooleanDef ) ) )? (otherlv_7= 'actionable' ( (lv_actionable_8_0= ruleBooleanDef ) ) )? (otherlv_9= 'lead' ( (lv_lead_10_0= ruleUserRef ) ) )* (otherlv_11= 'owner' ( (lv_owner_12_0= ruleUserRef ) ) )* (otherlv_13= 'staticId' ( (lv_staticId_14_0= RULE_STRING ) ) )* (otherlv_15= 'team' ( (lv_teamDef_16_0= RULE_STRING ) ) )? (otherlv_17= 'accessContextId' ( (lv_accessContextId_18_0= RULE_STRING ) ) )* (otherlv_19= 'children' otherlv_20= '{' (otherlv_21= 'actionableItem' ( (lv_children_22_0= ruleActionableItemDef ) ) )+ otherlv_23= '}' )? otherlv_24= '}' )? )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:746:2: ( (lv_name_0_0= ruleAI_DEF_REFERENCE ) ) ( (lv_aiDefOption_1_0= ruleActionableItemOption ) )* (otherlv_2= '{' (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )? (otherlv_5= 'active' ( (lv_active_6_0= ruleBooleanDef ) ) )? (otherlv_7= 'actionable' ( (lv_actionable_8_0= ruleBooleanDef ) ) )? (otherlv_9= 'lead' ( (lv_lead_10_0= ruleUserRef ) ) )* (otherlv_11= 'owner' ( (lv_owner_12_0= ruleUserRef ) ) )* (otherlv_13= 'staticId' ( (lv_staticId_14_0= RULE_STRING ) ) )* (otherlv_15= 'team' ( (lv_teamDef_16_0= RULE_STRING ) ) )? (otherlv_17= 'accessContextId' ( (lv_accessContextId_18_0= RULE_STRING ) ) )* (otherlv_19= 'children' otherlv_20= '{' (otherlv_21= 'actionableItem' ( (lv_children_22_0= ruleActionableItemDef ) ) )+ otherlv_23= '}' )? otherlv_24= '}' )?
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:746:2: ( (lv_name_0_0= ruleAI_DEF_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:747:1: (lv_name_0_0= ruleAI_DEF_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:747:1: (lv_name_0_0= ruleAI_DEF_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:748:3: lv_name_0_0= ruleAI_DEF_REFERENCE
            {
             
            	        newCompositeNode(grammarAccess.getActionableItemDefAccess().getNameAI_DEF_REFERENCEParserRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_ruleAI_DEF_REFERENCE_in_ruleActionableItemDef1496);
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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:764:2: ( (lv_aiDefOption_1_0= ruleActionableItemOption ) )*
            loop24:
            do {
                int alt24=2;
                int LA24_0 = input.LA(1);

                if ( (LA24_0==RULE_STRING||LA24_0==78) ) {
                    alt24=1;
                }


                switch (alt24) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:765:1: (lv_aiDefOption_1_0= ruleActionableItemOption )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:765:1: (lv_aiDefOption_1_0= ruleActionableItemOption )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:766:3: lv_aiDefOption_1_0= ruleActionableItemOption
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getActionableItemDefAccess().getAiDefOptionActionableItemOptionParserRuleCall_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleActionableItemOption_in_ruleActionableItemDef1517);
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
            	    break loop24;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:782:3: (otherlv_2= '{' (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )? (otherlv_5= 'active' ( (lv_active_6_0= ruleBooleanDef ) ) )? (otherlv_7= 'actionable' ( (lv_actionable_8_0= ruleBooleanDef ) ) )? (otherlv_9= 'lead' ( (lv_lead_10_0= ruleUserRef ) ) )* (otherlv_11= 'owner' ( (lv_owner_12_0= ruleUserRef ) ) )* (otherlv_13= 'staticId' ( (lv_staticId_14_0= RULE_STRING ) ) )* (otherlv_15= 'team' ( (lv_teamDef_16_0= RULE_STRING ) ) )? (otherlv_17= 'accessContextId' ( (lv_accessContextId_18_0= RULE_STRING ) ) )* (otherlv_19= 'children' otherlv_20= '{' (otherlv_21= 'actionableItem' ( (lv_children_22_0= ruleActionableItemDef ) ) )+ otherlv_23= '}' )? otherlv_24= '}' )?
            int alt35=2;
            int LA35_0 = input.LA(1);

            if ( (LA35_0==15) ) {
                alt35=1;
            }
            switch (alt35) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:782:5: otherlv_2= '{' (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )? (otherlv_5= 'active' ( (lv_active_6_0= ruleBooleanDef ) ) )? (otherlv_7= 'actionable' ( (lv_actionable_8_0= ruleBooleanDef ) ) )? (otherlv_9= 'lead' ( (lv_lead_10_0= ruleUserRef ) ) )* (otherlv_11= 'owner' ( (lv_owner_12_0= ruleUserRef ) ) )* (otherlv_13= 'staticId' ( (lv_staticId_14_0= RULE_STRING ) ) )* (otherlv_15= 'team' ( (lv_teamDef_16_0= RULE_STRING ) ) )? (otherlv_17= 'accessContextId' ( (lv_accessContextId_18_0= RULE_STRING ) ) )* (otherlv_19= 'children' otherlv_20= '{' (otherlv_21= 'actionableItem' ( (lv_children_22_0= ruleActionableItemDef ) ) )+ otherlv_23= '}' )? otherlv_24= '}'
                    {
                    otherlv_2=(Token)match(input,15,FOLLOW_15_in_ruleActionableItemDef1531); 

                        	newLeafNode(otherlv_2, grammarAccess.getActionableItemDefAccess().getLeftCurlyBracketKeyword_2_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:786:1: (otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) ) )?
                    int alt25=2;
                    int LA25_0 = input.LA(1);

                    if ( (LA25_0==21) ) {
                        alt25=1;
                    }
                    switch (alt25) {
                        case 1 :
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:786:3: otherlv_3= 'guid' ( (lv_guid_4_0= RULE_STRING ) )
                            {
                            otherlv_3=(Token)match(input,21,FOLLOW_21_in_ruleActionableItemDef1544); 

                                	newLeafNode(otherlv_3, grammarAccess.getActionableItemDefAccess().getGuidKeyword_2_1_0());
                                
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:790:1: ( (lv_guid_4_0= RULE_STRING ) )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:791:1: (lv_guid_4_0= RULE_STRING )
                            {
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:791:1: (lv_guid_4_0= RULE_STRING )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:792:3: lv_guid_4_0= RULE_STRING
                            {
                            lv_guid_4_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleActionableItemDef1561); 

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

                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:808:4: (otherlv_5= 'active' ( (lv_active_6_0= ruleBooleanDef ) ) )?
                    int alt26=2;
                    int LA26_0 = input.LA(1);

                    if ( (LA26_0==16) ) {
                        alt26=1;
                    }
                    switch (alt26) {
                        case 1 :
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:808:6: otherlv_5= 'active' ( (lv_active_6_0= ruleBooleanDef ) )
                            {
                            otherlv_5=(Token)match(input,16,FOLLOW_16_in_ruleActionableItemDef1581); 

                                	newLeafNode(otherlv_5, grammarAccess.getActionableItemDefAccess().getActiveKeyword_2_2_0());
                                
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:812:1: ( (lv_active_6_0= ruleBooleanDef ) )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:813:1: (lv_active_6_0= ruleBooleanDef )
                            {
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:813:1: (lv_active_6_0= ruleBooleanDef )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:814:3: lv_active_6_0= ruleBooleanDef
                            {
                             
                            	        newCompositeNode(grammarAccess.getActionableItemDefAccess().getActiveBooleanDefEnumRuleCall_2_2_1_0()); 
                            	    
                            pushFollow(FOLLOW_ruleBooleanDef_in_ruleActionableItemDef1602);
                            lv_active_6_0=ruleBooleanDef();

                            state._fsp--;


                            	        if (current==null) {
                            	            current = createModelElementForParent(grammarAccess.getActionableItemDefRule());
                            	        }
                                   		set(
                                   			current, 
                                   			"active",
                                    		lv_active_6_0, 
                                    		"BooleanDef");
                            	        afterParserOrEnumRuleCall();
                            	    

                            }


                            }


                            }
                            break;

                    }

                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:830:4: (otherlv_7= 'actionable' ( (lv_actionable_8_0= ruleBooleanDef ) ) )?
                    int alt27=2;
                    int LA27_0 = input.LA(1);

                    if ( (LA27_0==30) ) {
                        alt27=1;
                    }
                    switch (alt27) {
                        case 1 :
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:830:6: otherlv_7= 'actionable' ( (lv_actionable_8_0= ruleBooleanDef ) )
                            {
                            otherlv_7=(Token)match(input,30,FOLLOW_30_in_ruleActionableItemDef1617); 

                                	newLeafNode(otherlv_7, grammarAccess.getActionableItemDefAccess().getActionableKeyword_2_3_0());
                                
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:834:1: ( (lv_actionable_8_0= ruleBooleanDef ) )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:835:1: (lv_actionable_8_0= ruleBooleanDef )
                            {
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:835:1: (lv_actionable_8_0= ruleBooleanDef )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:836:3: lv_actionable_8_0= ruleBooleanDef
                            {
                             
                            	        newCompositeNode(grammarAccess.getActionableItemDefAccess().getActionableBooleanDefEnumRuleCall_2_3_1_0()); 
                            	    
                            pushFollow(FOLLOW_ruleBooleanDef_in_ruleActionableItemDef1638);
                            lv_actionable_8_0=ruleBooleanDef();

                            state._fsp--;


                            	        if (current==null) {
                            	            current = createModelElementForParent(grammarAccess.getActionableItemDefRule());
                            	        }
                                   		set(
                                   			current, 
                                   			"actionable",
                                    		lv_actionable_8_0, 
                                    		"BooleanDef");
                            	        afterParserOrEnumRuleCall();
                            	    

                            }


                            }


                            }
                            break;

                    }

                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:852:4: (otherlv_9= 'lead' ( (lv_lead_10_0= ruleUserRef ) ) )*
                    loop28:
                    do {
                        int alt28=2;
                        int LA28_0 = input.LA(1);

                        if ( (LA28_0==23) ) {
                            alt28=1;
                        }


                        switch (alt28) {
                    	case 1 :
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:852:6: otherlv_9= 'lead' ( (lv_lead_10_0= ruleUserRef ) )
                    	    {
                    	    otherlv_9=(Token)match(input,23,FOLLOW_23_in_ruleActionableItemDef1653); 

                    	        	newLeafNode(otherlv_9, grammarAccess.getActionableItemDefAccess().getLeadKeyword_2_4_0());
                    	        
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:856:1: ( (lv_lead_10_0= ruleUserRef ) )
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:857:1: (lv_lead_10_0= ruleUserRef )
                    	    {
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:857:1: (lv_lead_10_0= ruleUserRef )
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:858:3: lv_lead_10_0= ruleUserRef
                    	    {
                    	     
                    	    	        newCompositeNode(grammarAccess.getActionableItemDefAccess().getLeadUserRefParserRuleCall_2_4_1_0()); 
                    	    	    
                    	    pushFollow(FOLLOW_ruleUserRef_in_ruleActionableItemDef1674);
                    	    lv_lead_10_0=ruleUserRef();

                    	    state._fsp--;


                    	    	        if (current==null) {
                    	    	            current = createModelElementForParent(grammarAccess.getActionableItemDefRule());
                    	    	        }
                    	           		add(
                    	           			current, 
                    	           			"lead",
                    	            		lv_lead_10_0, 
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

                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:874:4: (otherlv_11= 'owner' ( (lv_owner_12_0= ruleUserRef ) ) )*
                    loop29:
                    do {
                        int alt29=2;
                        int LA29_0 = input.LA(1);

                        if ( (LA29_0==31) ) {
                            alt29=1;
                        }


                        switch (alt29) {
                    	case 1 :
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:874:6: otherlv_11= 'owner' ( (lv_owner_12_0= ruleUserRef ) )
                    	    {
                    	    otherlv_11=(Token)match(input,31,FOLLOW_31_in_ruleActionableItemDef1689); 

                    	        	newLeafNode(otherlv_11, grammarAccess.getActionableItemDefAccess().getOwnerKeyword_2_5_0());
                    	        
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:878:1: ( (lv_owner_12_0= ruleUserRef ) )
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:879:1: (lv_owner_12_0= ruleUserRef )
                    	    {
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:879:1: (lv_owner_12_0= ruleUserRef )
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:880:3: lv_owner_12_0= ruleUserRef
                    	    {
                    	     
                    	    	        newCompositeNode(grammarAccess.getActionableItemDefAccess().getOwnerUserRefParserRuleCall_2_5_1_0()); 
                    	    	    
                    	    pushFollow(FOLLOW_ruleUserRef_in_ruleActionableItemDef1710);
                    	    lv_owner_12_0=ruleUserRef();

                    	    state._fsp--;


                    	    	        if (current==null) {
                    	    	            current = createModelElementForParent(grammarAccess.getActionableItemDefRule());
                    	    	        }
                    	           		add(
                    	           			current, 
                    	           			"owner",
                    	            		lv_owner_12_0, 
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

                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:896:4: (otherlv_13= 'staticId' ( (lv_staticId_14_0= RULE_STRING ) ) )*
                    loop30:
                    do {
                        int alt30=2;
                        int LA30_0 = input.LA(1);

                        if ( (LA30_0==22) ) {
                            alt30=1;
                        }


                        switch (alt30) {
                    	case 1 :
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:896:6: otherlv_13= 'staticId' ( (lv_staticId_14_0= RULE_STRING ) )
                    	    {
                    	    otherlv_13=(Token)match(input,22,FOLLOW_22_in_ruleActionableItemDef1725); 

                    	        	newLeafNode(otherlv_13, grammarAccess.getActionableItemDefAccess().getStaticIdKeyword_2_6_0());
                    	        
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:900:1: ( (lv_staticId_14_0= RULE_STRING ) )
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:901:1: (lv_staticId_14_0= RULE_STRING )
                    	    {
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:901:1: (lv_staticId_14_0= RULE_STRING )
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:902:3: lv_staticId_14_0= RULE_STRING
                    	    {
                    	    lv_staticId_14_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleActionableItemDef1742); 

                    	    			newLeafNode(lv_staticId_14_0, grammarAccess.getActionableItemDefAccess().getStaticIdSTRINGTerminalRuleCall_2_6_1_0()); 
                    	    		

                    	    	        if (current==null) {
                    	    	            current = createModelElement(grammarAccess.getActionableItemDefRule());
                    	    	        }
                    	           		addWithLastConsumed(
                    	           			current, 
                    	           			"staticId",
                    	            		lv_staticId_14_0, 
                    	            		"STRING");
                    	    	    

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop30;
                        }
                    } while (true);

                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:918:4: (otherlv_15= 'team' ( (lv_teamDef_16_0= RULE_STRING ) ) )?
                    int alt31=2;
                    int LA31_0 = input.LA(1);

                    if ( (LA31_0==32) ) {
                        alt31=1;
                    }
                    switch (alt31) {
                        case 1 :
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:918:6: otherlv_15= 'team' ( (lv_teamDef_16_0= RULE_STRING ) )
                            {
                            otherlv_15=(Token)match(input,32,FOLLOW_32_in_ruleActionableItemDef1762); 

                                	newLeafNode(otherlv_15, grammarAccess.getActionableItemDefAccess().getTeamKeyword_2_7_0());
                                
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:922:1: ( (lv_teamDef_16_0= RULE_STRING ) )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:923:1: (lv_teamDef_16_0= RULE_STRING )
                            {
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:923:1: (lv_teamDef_16_0= RULE_STRING )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:924:3: lv_teamDef_16_0= RULE_STRING
                            {
                            lv_teamDef_16_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleActionableItemDef1779); 

                            			newLeafNode(lv_teamDef_16_0, grammarAccess.getActionableItemDefAccess().getTeamDefSTRINGTerminalRuleCall_2_7_1_0()); 
                            		

                            	        if (current==null) {
                            	            current = createModelElement(grammarAccess.getActionableItemDefRule());
                            	        }
                                   		setWithLastConsumed(
                                   			current, 
                                   			"teamDef",
                                    		lv_teamDef_16_0, 
                                    		"STRING");
                            	    

                            }


                            }


                            }
                            break;

                    }

                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:940:4: (otherlv_17= 'accessContextId' ( (lv_accessContextId_18_0= RULE_STRING ) ) )*
                    loop32:
                    do {
                        int alt32=2;
                        int LA32_0 = input.LA(1);

                        if ( (LA32_0==27) ) {
                            alt32=1;
                        }


                        switch (alt32) {
                    	case 1 :
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:940:6: otherlv_17= 'accessContextId' ( (lv_accessContextId_18_0= RULE_STRING ) )
                    	    {
                    	    otherlv_17=(Token)match(input,27,FOLLOW_27_in_ruleActionableItemDef1799); 

                    	        	newLeafNode(otherlv_17, grammarAccess.getActionableItemDefAccess().getAccessContextIdKeyword_2_8_0());
                    	        
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:944:1: ( (lv_accessContextId_18_0= RULE_STRING ) )
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:945:1: (lv_accessContextId_18_0= RULE_STRING )
                    	    {
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:945:1: (lv_accessContextId_18_0= RULE_STRING )
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:946:3: lv_accessContextId_18_0= RULE_STRING
                    	    {
                    	    lv_accessContextId_18_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleActionableItemDef1816); 

                    	    			newLeafNode(lv_accessContextId_18_0, grammarAccess.getActionableItemDefAccess().getAccessContextIdSTRINGTerminalRuleCall_2_8_1_0()); 
                    	    		

                    	    	        if (current==null) {
                    	    	            current = createModelElement(grammarAccess.getActionableItemDefRule());
                    	    	        }
                    	           		addWithLastConsumed(
                    	           			current, 
                    	           			"accessContextId",
                    	            		lv_accessContextId_18_0, 
                    	            		"STRING");
                    	    	    

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop32;
                        }
                    } while (true);

                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:962:4: (otherlv_19= 'children' otherlv_20= '{' (otherlv_21= 'actionableItem' ( (lv_children_22_0= ruleActionableItemDef ) ) )+ otherlv_23= '}' )?
                    int alt34=2;
                    int LA34_0 = input.LA(1);

                    if ( (LA34_0==29) ) {
                        alt34=1;
                    }
                    switch (alt34) {
                        case 1 :
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:962:6: otherlv_19= 'children' otherlv_20= '{' (otherlv_21= 'actionableItem' ( (lv_children_22_0= ruleActionableItemDef ) ) )+ otherlv_23= '}'
                            {
                            otherlv_19=(Token)match(input,29,FOLLOW_29_in_ruleActionableItemDef1836); 

                                	newLeafNode(otherlv_19, grammarAccess.getActionableItemDefAccess().getChildrenKeyword_2_9_0());
                                
                            otherlv_20=(Token)match(input,15,FOLLOW_15_in_ruleActionableItemDef1848); 

                                	newLeafNode(otherlv_20, grammarAccess.getActionableItemDefAccess().getLeftCurlyBracketKeyword_2_9_1());
                                
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:970:1: (otherlv_21= 'actionableItem' ( (lv_children_22_0= ruleActionableItemDef ) ) )+
                            int cnt33=0;
                            loop33:
                            do {
                                int alt33=2;
                                int LA33_0 = input.LA(1);

                                if ( (LA33_0==14) ) {
                                    alt33=1;
                                }


                                switch (alt33) {
                            	case 1 :
                            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:970:3: otherlv_21= 'actionableItem' ( (lv_children_22_0= ruleActionableItemDef ) )
                            	    {
                            	    otherlv_21=(Token)match(input,14,FOLLOW_14_in_ruleActionableItemDef1861); 

                            	        	newLeafNode(otherlv_21, grammarAccess.getActionableItemDefAccess().getActionableItemKeyword_2_9_2_0());
                            	        
                            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:974:1: ( (lv_children_22_0= ruleActionableItemDef ) )
                            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:975:1: (lv_children_22_0= ruleActionableItemDef )
                            	    {
                            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:975:1: (lv_children_22_0= ruleActionableItemDef )
                            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:976:3: lv_children_22_0= ruleActionableItemDef
                            	    {
                            	     
                            	    	        newCompositeNode(grammarAccess.getActionableItemDefAccess().getChildrenActionableItemDefParserRuleCall_2_9_2_1_0()); 
                            	    	    
                            	    pushFollow(FOLLOW_ruleActionableItemDef_in_ruleActionableItemDef1882);
                            	    lv_children_22_0=ruleActionableItemDef();

                            	    state._fsp--;


                            	    	        if (current==null) {
                            	    	            current = createModelElementForParent(grammarAccess.getActionableItemDefRule());
                            	    	        }
                            	           		add(
                            	           			current, 
                            	           			"children",
                            	            		lv_children_22_0, 
                            	            		"ActionableItemDef");
                            	    	        afterParserOrEnumRuleCall();
                            	    	    

                            	    }


                            	    }


                            	    }
                            	    break;

                            	default :
                            	    if ( cnt33 >= 1 ) break loop33;
                                        EarlyExitException eee =
                                            new EarlyExitException(33, input);
                                        throw eee;
                                }
                                cnt33++;
                            } while (true);

                            otherlv_23=(Token)match(input,20,FOLLOW_20_in_ruleActionableItemDef1896); 

                                	newLeafNode(otherlv_23, grammarAccess.getActionableItemDefAccess().getRightCurlyBracketKeyword_2_9_3());
                                

                            }
                            break;

                    }

                    otherlv_24=(Token)match(input,20,FOLLOW_20_in_ruleActionableItemDef1910); 

                        	newLeafNode(otherlv_24, grammarAccess.getActionableItemDefAccess().getRightCurlyBracketKeyword_2_10());
                        

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1008:1: entryRuleVERSION_DEF_REFERENCE returns [String current=null] : iv_ruleVERSION_DEF_REFERENCE= ruleVERSION_DEF_REFERENCE EOF ;
    public final String entryRuleVERSION_DEF_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleVERSION_DEF_REFERENCE = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1009:2: (iv_ruleVERSION_DEF_REFERENCE= ruleVERSION_DEF_REFERENCE EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1010:2: iv_ruleVERSION_DEF_REFERENCE= ruleVERSION_DEF_REFERENCE EOF
            {
             newCompositeNode(grammarAccess.getVERSION_DEF_REFERENCERule()); 
            pushFollow(FOLLOW_ruleVERSION_DEF_REFERENCE_in_entryRuleVERSION_DEF_REFERENCE1949);
            iv_ruleVERSION_DEF_REFERENCE=ruleVERSION_DEF_REFERENCE();

            state._fsp--;

             current =iv_ruleVERSION_DEF_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleVERSION_DEF_REFERENCE1960); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1017:1: ruleVERSION_DEF_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleVERSION_DEF_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1020:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1021:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleVERSION_DEF_REFERENCE1999); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1036:1: entryRuleVersionDef returns [EObject current=null] : iv_ruleVersionDef= ruleVersionDef EOF ;
    public final EObject entryRuleVersionDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleVersionDef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1037:2: (iv_ruleVersionDef= ruleVersionDef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1038:2: iv_ruleVersionDef= ruleVersionDef EOF
            {
             newCompositeNode(grammarAccess.getVersionDefRule()); 
            pushFollow(FOLLOW_ruleVersionDef_in_entryRuleVersionDef2043);
            iv_ruleVersionDef=ruleVersionDef();

            state._fsp--;

             current =iv_ruleVersionDef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleVersionDef2053); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1045:1: ruleVersionDef returns [EObject current=null] : ( ( (lv_name_0_0= ruleVERSION_DEF_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'guid' ( (lv_guid_3_0= RULE_STRING ) ) )? (otherlv_4= 'staticId' ( (lv_staticId_5_0= RULE_STRING ) ) )* (otherlv_6= 'next' ( (lv_next_7_0= ruleBooleanDef ) ) )? (otherlv_8= 'released' ( (lv_released_9_0= ruleBooleanDef ) ) )? (otherlv_10= 'allowCreateBranch' ( (lv_allowCreateBranch_11_0= ruleBooleanDef ) ) )? (otherlv_12= 'allowCommitBranch' ( (lv_allowCommitBranch_13_0= ruleBooleanDef ) ) )? (otherlv_14= 'baselineBranchGuid' ( (lv_baselineBranchGuid_15_0= RULE_STRING ) ) )? (otherlv_16= 'parallelVersion' ( (lv_parallelVersion_17_0= RULE_STRING ) ) )* otherlv_18= '}' ) ;
    public final EObject ruleVersionDef() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_2=null;
        Token lv_guid_3_0=null;
        Token otherlv_4=null;
        Token lv_staticId_5_0=null;
        Token otherlv_6=null;
        Token otherlv_8=null;
        Token otherlv_10=null;
        Token otherlv_12=null;
        Token otherlv_14=null;
        Token lv_baselineBranchGuid_15_0=null;
        Token otherlv_16=null;
        Token lv_parallelVersion_17_0=null;
        Token otherlv_18=null;
        AntlrDatatypeRuleToken lv_name_0_0 = null;

        Enumerator lv_next_7_0 = null;

        Enumerator lv_released_9_0 = null;

        Enumerator lv_allowCreateBranch_11_0 = null;

        Enumerator lv_allowCommitBranch_13_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1048:28: ( ( ( (lv_name_0_0= ruleVERSION_DEF_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'guid' ( (lv_guid_3_0= RULE_STRING ) ) )? (otherlv_4= 'staticId' ( (lv_staticId_5_0= RULE_STRING ) ) )* (otherlv_6= 'next' ( (lv_next_7_0= ruleBooleanDef ) ) )? (otherlv_8= 'released' ( (lv_released_9_0= ruleBooleanDef ) ) )? (otherlv_10= 'allowCreateBranch' ( (lv_allowCreateBranch_11_0= ruleBooleanDef ) ) )? (otherlv_12= 'allowCommitBranch' ( (lv_allowCommitBranch_13_0= ruleBooleanDef ) ) )? (otherlv_14= 'baselineBranchGuid' ( (lv_baselineBranchGuid_15_0= RULE_STRING ) ) )? (otherlv_16= 'parallelVersion' ( (lv_parallelVersion_17_0= RULE_STRING ) ) )* otherlv_18= '}' ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1049:1: ( ( (lv_name_0_0= ruleVERSION_DEF_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'guid' ( (lv_guid_3_0= RULE_STRING ) ) )? (otherlv_4= 'staticId' ( (lv_staticId_5_0= RULE_STRING ) ) )* (otherlv_6= 'next' ( (lv_next_7_0= ruleBooleanDef ) ) )? (otherlv_8= 'released' ( (lv_released_9_0= ruleBooleanDef ) ) )? (otherlv_10= 'allowCreateBranch' ( (lv_allowCreateBranch_11_0= ruleBooleanDef ) ) )? (otherlv_12= 'allowCommitBranch' ( (lv_allowCommitBranch_13_0= ruleBooleanDef ) ) )? (otherlv_14= 'baselineBranchGuid' ( (lv_baselineBranchGuid_15_0= RULE_STRING ) ) )? (otherlv_16= 'parallelVersion' ( (lv_parallelVersion_17_0= RULE_STRING ) ) )* otherlv_18= '}' )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1049:1: ( ( (lv_name_0_0= ruleVERSION_DEF_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'guid' ( (lv_guid_3_0= RULE_STRING ) ) )? (otherlv_4= 'staticId' ( (lv_staticId_5_0= RULE_STRING ) ) )* (otherlv_6= 'next' ( (lv_next_7_0= ruleBooleanDef ) ) )? (otherlv_8= 'released' ( (lv_released_9_0= ruleBooleanDef ) ) )? (otherlv_10= 'allowCreateBranch' ( (lv_allowCreateBranch_11_0= ruleBooleanDef ) ) )? (otherlv_12= 'allowCommitBranch' ( (lv_allowCommitBranch_13_0= ruleBooleanDef ) ) )? (otherlv_14= 'baselineBranchGuid' ( (lv_baselineBranchGuid_15_0= RULE_STRING ) ) )? (otherlv_16= 'parallelVersion' ( (lv_parallelVersion_17_0= RULE_STRING ) ) )* otherlv_18= '}' )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1049:2: ( (lv_name_0_0= ruleVERSION_DEF_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'guid' ( (lv_guid_3_0= RULE_STRING ) ) )? (otherlv_4= 'staticId' ( (lv_staticId_5_0= RULE_STRING ) ) )* (otherlv_6= 'next' ( (lv_next_7_0= ruleBooleanDef ) ) )? (otherlv_8= 'released' ( (lv_released_9_0= ruleBooleanDef ) ) )? (otherlv_10= 'allowCreateBranch' ( (lv_allowCreateBranch_11_0= ruleBooleanDef ) ) )? (otherlv_12= 'allowCommitBranch' ( (lv_allowCommitBranch_13_0= ruleBooleanDef ) ) )? (otherlv_14= 'baselineBranchGuid' ( (lv_baselineBranchGuid_15_0= RULE_STRING ) ) )? (otherlv_16= 'parallelVersion' ( (lv_parallelVersion_17_0= RULE_STRING ) ) )* otherlv_18= '}'
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1049:2: ( (lv_name_0_0= ruleVERSION_DEF_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1050:1: (lv_name_0_0= ruleVERSION_DEF_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1050:1: (lv_name_0_0= ruleVERSION_DEF_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1051:3: lv_name_0_0= ruleVERSION_DEF_REFERENCE
            {
             
            	        newCompositeNode(grammarAccess.getVersionDefAccess().getNameVERSION_DEF_REFERENCEParserRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_ruleVERSION_DEF_REFERENCE_in_ruleVersionDef2099);
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

            otherlv_1=(Token)match(input,15,FOLLOW_15_in_ruleVersionDef2111); 

                	newLeafNode(otherlv_1, grammarAccess.getVersionDefAccess().getLeftCurlyBracketKeyword_1());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1071:1: (otherlv_2= 'guid' ( (lv_guid_3_0= RULE_STRING ) ) )?
            int alt36=2;
            int LA36_0 = input.LA(1);

            if ( (LA36_0==21) ) {
                alt36=1;
            }
            switch (alt36) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1071:3: otherlv_2= 'guid' ( (lv_guid_3_0= RULE_STRING ) )
                    {
                    otherlv_2=(Token)match(input,21,FOLLOW_21_in_ruleVersionDef2124); 

                        	newLeafNode(otherlv_2, grammarAccess.getVersionDefAccess().getGuidKeyword_2_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1075:1: ( (lv_guid_3_0= RULE_STRING ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1076:1: (lv_guid_3_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1076:1: (lv_guid_3_0= RULE_STRING )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1077:3: lv_guid_3_0= RULE_STRING
                    {
                    lv_guid_3_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleVersionDef2141); 

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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1093:4: (otherlv_4= 'staticId' ( (lv_staticId_5_0= RULE_STRING ) ) )*
            loop37:
            do {
                int alt37=2;
                int LA37_0 = input.LA(1);

                if ( (LA37_0==22) ) {
                    alt37=1;
                }


                switch (alt37) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1093:6: otherlv_4= 'staticId' ( (lv_staticId_5_0= RULE_STRING ) )
            	    {
            	    otherlv_4=(Token)match(input,22,FOLLOW_22_in_ruleVersionDef2161); 

            	        	newLeafNode(otherlv_4, grammarAccess.getVersionDefAccess().getStaticIdKeyword_3_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1097:1: ( (lv_staticId_5_0= RULE_STRING ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1098:1: (lv_staticId_5_0= RULE_STRING )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1098:1: (lv_staticId_5_0= RULE_STRING )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1099:3: lv_staticId_5_0= RULE_STRING
            	    {
            	    lv_staticId_5_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleVersionDef2178); 

            	    			newLeafNode(lv_staticId_5_0, grammarAccess.getVersionDefAccess().getStaticIdSTRINGTerminalRuleCall_3_1_0()); 
            	    		

            	    	        if (current==null) {
            	    	            current = createModelElement(grammarAccess.getVersionDefRule());
            	    	        }
            	           		addWithLastConsumed(
            	           			current, 
            	           			"staticId",
            	            		lv_staticId_5_0, 
            	            		"STRING");
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop37;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1115:4: (otherlv_6= 'next' ( (lv_next_7_0= ruleBooleanDef ) ) )?
            int alt38=2;
            int LA38_0 = input.LA(1);

            if ( (LA38_0==33) ) {
                alt38=1;
            }
            switch (alt38) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1115:6: otherlv_6= 'next' ( (lv_next_7_0= ruleBooleanDef ) )
                    {
                    otherlv_6=(Token)match(input,33,FOLLOW_33_in_ruleVersionDef2198); 

                        	newLeafNode(otherlv_6, grammarAccess.getVersionDefAccess().getNextKeyword_4_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1119:1: ( (lv_next_7_0= ruleBooleanDef ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1120:1: (lv_next_7_0= ruleBooleanDef )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1120:1: (lv_next_7_0= ruleBooleanDef )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1121:3: lv_next_7_0= ruleBooleanDef
                    {
                     
                    	        newCompositeNode(grammarAccess.getVersionDefAccess().getNextBooleanDefEnumRuleCall_4_1_0()); 
                    	    
                    pushFollow(FOLLOW_ruleBooleanDef_in_ruleVersionDef2219);
                    lv_next_7_0=ruleBooleanDef();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getVersionDefRule());
                    	        }
                           		set(
                           			current, 
                           			"next",
                            		lv_next_7_0, 
                            		"BooleanDef");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1137:4: (otherlv_8= 'released' ( (lv_released_9_0= ruleBooleanDef ) ) )?
            int alt39=2;
            int LA39_0 = input.LA(1);

            if ( (LA39_0==34) ) {
                alt39=1;
            }
            switch (alt39) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1137:6: otherlv_8= 'released' ( (lv_released_9_0= ruleBooleanDef ) )
                    {
                    otherlv_8=(Token)match(input,34,FOLLOW_34_in_ruleVersionDef2234); 

                        	newLeafNode(otherlv_8, grammarAccess.getVersionDefAccess().getReleasedKeyword_5_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1141:1: ( (lv_released_9_0= ruleBooleanDef ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1142:1: (lv_released_9_0= ruleBooleanDef )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1142:1: (lv_released_9_0= ruleBooleanDef )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1143:3: lv_released_9_0= ruleBooleanDef
                    {
                     
                    	        newCompositeNode(grammarAccess.getVersionDefAccess().getReleasedBooleanDefEnumRuleCall_5_1_0()); 
                    	    
                    pushFollow(FOLLOW_ruleBooleanDef_in_ruleVersionDef2255);
                    lv_released_9_0=ruleBooleanDef();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getVersionDefRule());
                    	        }
                           		set(
                           			current, 
                           			"released",
                            		lv_released_9_0, 
                            		"BooleanDef");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1159:4: (otherlv_10= 'allowCreateBranch' ( (lv_allowCreateBranch_11_0= ruleBooleanDef ) ) )?
            int alt40=2;
            int LA40_0 = input.LA(1);

            if ( (LA40_0==35) ) {
                alt40=1;
            }
            switch (alt40) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1159:6: otherlv_10= 'allowCreateBranch' ( (lv_allowCreateBranch_11_0= ruleBooleanDef ) )
                    {
                    otherlv_10=(Token)match(input,35,FOLLOW_35_in_ruleVersionDef2270); 

                        	newLeafNode(otherlv_10, grammarAccess.getVersionDefAccess().getAllowCreateBranchKeyword_6_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1163:1: ( (lv_allowCreateBranch_11_0= ruleBooleanDef ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1164:1: (lv_allowCreateBranch_11_0= ruleBooleanDef )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1164:1: (lv_allowCreateBranch_11_0= ruleBooleanDef )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1165:3: lv_allowCreateBranch_11_0= ruleBooleanDef
                    {
                     
                    	        newCompositeNode(grammarAccess.getVersionDefAccess().getAllowCreateBranchBooleanDefEnumRuleCall_6_1_0()); 
                    	    
                    pushFollow(FOLLOW_ruleBooleanDef_in_ruleVersionDef2291);
                    lv_allowCreateBranch_11_0=ruleBooleanDef();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getVersionDefRule());
                    	        }
                           		set(
                           			current, 
                           			"allowCreateBranch",
                            		lv_allowCreateBranch_11_0, 
                            		"BooleanDef");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1181:4: (otherlv_12= 'allowCommitBranch' ( (lv_allowCommitBranch_13_0= ruleBooleanDef ) ) )?
            int alt41=2;
            int LA41_0 = input.LA(1);

            if ( (LA41_0==36) ) {
                alt41=1;
            }
            switch (alt41) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1181:6: otherlv_12= 'allowCommitBranch' ( (lv_allowCommitBranch_13_0= ruleBooleanDef ) )
                    {
                    otherlv_12=(Token)match(input,36,FOLLOW_36_in_ruleVersionDef2306); 

                        	newLeafNode(otherlv_12, grammarAccess.getVersionDefAccess().getAllowCommitBranchKeyword_7_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1185:1: ( (lv_allowCommitBranch_13_0= ruleBooleanDef ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1186:1: (lv_allowCommitBranch_13_0= ruleBooleanDef )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1186:1: (lv_allowCommitBranch_13_0= ruleBooleanDef )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1187:3: lv_allowCommitBranch_13_0= ruleBooleanDef
                    {
                     
                    	        newCompositeNode(grammarAccess.getVersionDefAccess().getAllowCommitBranchBooleanDefEnumRuleCall_7_1_0()); 
                    	    
                    pushFollow(FOLLOW_ruleBooleanDef_in_ruleVersionDef2327);
                    lv_allowCommitBranch_13_0=ruleBooleanDef();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getVersionDefRule());
                    	        }
                           		set(
                           			current, 
                           			"allowCommitBranch",
                            		lv_allowCommitBranch_13_0, 
                            		"BooleanDef");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1203:4: (otherlv_14= 'baselineBranchGuid' ( (lv_baselineBranchGuid_15_0= RULE_STRING ) ) )?
            int alt42=2;
            int LA42_0 = input.LA(1);

            if ( (LA42_0==37) ) {
                alt42=1;
            }
            switch (alt42) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1203:6: otherlv_14= 'baselineBranchGuid' ( (lv_baselineBranchGuid_15_0= RULE_STRING ) )
                    {
                    otherlv_14=(Token)match(input,37,FOLLOW_37_in_ruleVersionDef2342); 

                        	newLeafNode(otherlv_14, grammarAccess.getVersionDefAccess().getBaselineBranchGuidKeyword_8_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1207:1: ( (lv_baselineBranchGuid_15_0= RULE_STRING ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1208:1: (lv_baselineBranchGuid_15_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1208:1: (lv_baselineBranchGuid_15_0= RULE_STRING )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1209:3: lv_baselineBranchGuid_15_0= RULE_STRING
                    {
                    lv_baselineBranchGuid_15_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleVersionDef2359); 

                    			newLeafNode(lv_baselineBranchGuid_15_0, grammarAccess.getVersionDefAccess().getBaselineBranchGuidSTRINGTerminalRuleCall_8_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getVersionDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"baselineBranchGuid",
                            		lv_baselineBranchGuid_15_0, 
                            		"STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1225:4: (otherlv_16= 'parallelVersion' ( (lv_parallelVersion_17_0= RULE_STRING ) ) )*
            loop43:
            do {
                int alt43=2;
                int LA43_0 = input.LA(1);

                if ( (LA43_0==38) ) {
                    alt43=1;
                }


                switch (alt43) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1225:6: otherlv_16= 'parallelVersion' ( (lv_parallelVersion_17_0= RULE_STRING ) )
            	    {
            	    otherlv_16=(Token)match(input,38,FOLLOW_38_in_ruleVersionDef2379); 

            	        	newLeafNode(otherlv_16, grammarAccess.getVersionDefAccess().getParallelVersionKeyword_9_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1229:1: ( (lv_parallelVersion_17_0= RULE_STRING ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1230:1: (lv_parallelVersion_17_0= RULE_STRING )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1230:1: (lv_parallelVersion_17_0= RULE_STRING )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1231:3: lv_parallelVersion_17_0= RULE_STRING
            	    {
            	    lv_parallelVersion_17_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleVersionDef2396); 

            	    			newLeafNode(lv_parallelVersion_17_0, grammarAccess.getVersionDefAccess().getParallelVersionSTRINGTerminalRuleCall_9_1_0()); 
            	    		

            	    	        if (current==null) {
            	    	            current = createModelElement(grammarAccess.getVersionDefRule());
            	    	        }
            	           		addWithLastConsumed(
            	           			current, 
            	           			"parallelVersion",
            	            		lv_parallelVersion_17_0, 
            	            		"STRING");
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop43;
                }
            } while (true);

            otherlv_18=(Token)match(input,20,FOLLOW_20_in_ruleVersionDef2415); 

                	newLeafNode(otherlv_18, grammarAccess.getVersionDefAccess().getRightCurlyBracketKeyword_10());
                

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1259:1: entryRuleWorkDef returns [EObject current=null] : iv_ruleWorkDef= ruleWorkDef EOF ;
    public final EObject entryRuleWorkDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleWorkDef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1260:2: (iv_ruleWorkDef= ruleWorkDef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1261:2: iv_ruleWorkDef= ruleWorkDef EOF
            {
             newCompositeNode(grammarAccess.getWorkDefRule()); 
            pushFollow(FOLLOW_ruleWorkDef_in_entryRuleWorkDef2451);
            iv_ruleWorkDef=ruleWorkDef();

            state._fsp--;

             current =iv_ruleWorkDef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleWorkDef2461); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1268:1: ruleWorkDef returns [EObject current=null] : ( ( (lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'id' ( (lv_id_3_0= RULE_STRING ) ) )+ (otherlv_4= 'startState' ( ( ruleSTATE_NAME_REFERENCE ) ) ) ( (lv_widgetDefs_6_0= ruleWidgetDef ) )* ( (lv_decisionReviewDefs_7_0= ruleDecisionReviewDef ) )* ( (lv_peerReviewDefs_8_0= rulePeerReviewDef ) )* ( (lv_states_9_0= ruleStateDef ) )+ otherlv_10= '}' ) ;
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
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1271:28: ( ( ( (lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'id' ( (lv_id_3_0= RULE_STRING ) ) )+ (otherlv_4= 'startState' ( ( ruleSTATE_NAME_REFERENCE ) ) ) ( (lv_widgetDefs_6_0= ruleWidgetDef ) )* ( (lv_decisionReviewDefs_7_0= ruleDecisionReviewDef ) )* ( (lv_peerReviewDefs_8_0= rulePeerReviewDef ) )* ( (lv_states_9_0= ruleStateDef ) )+ otherlv_10= '}' ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1272:1: ( ( (lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'id' ( (lv_id_3_0= RULE_STRING ) ) )+ (otherlv_4= 'startState' ( ( ruleSTATE_NAME_REFERENCE ) ) ) ( (lv_widgetDefs_6_0= ruleWidgetDef ) )* ( (lv_decisionReviewDefs_7_0= ruleDecisionReviewDef ) )* ( (lv_peerReviewDefs_8_0= rulePeerReviewDef ) )* ( (lv_states_9_0= ruleStateDef ) )+ otherlv_10= '}' )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1272:1: ( ( (lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'id' ( (lv_id_3_0= RULE_STRING ) ) )+ (otherlv_4= 'startState' ( ( ruleSTATE_NAME_REFERENCE ) ) ) ( (lv_widgetDefs_6_0= ruleWidgetDef ) )* ( (lv_decisionReviewDefs_7_0= ruleDecisionReviewDef ) )* ( (lv_peerReviewDefs_8_0= rulePeerReviewDef ) )* ( (lv_states_9_0= ruleStateDef ) )+ otherlv_10= '}' )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1272:2: ( (lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'id' ( (lv_id_3_0= RULE_STRING ) ) )+ (otherlv_4= 'startState' ( ( ruleSTATE_NAME_REFERENCE ) ) ) ( (lv_widgetDefs_6_0= ruleWidgetDef ) )* ( (lv_decisionReviewDefs_7_0= ruleDecisionReviewDef ) )* ( (lv_peerReviewDefs_8_0= rulePeerReviewDef ) )* ( (lv_states_9_0= ruleStateDef ) )+ otherlv_10= '}'
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1272:2: ( (lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1273:1: (lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1273:1: (lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1274:3: lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE
            {
             
            	        newCompositeNode(grammarAccess.getWorkDefAccess().getNameWORK_DEFINITION_NAME_REFERENCEParserRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_ruleWORK_DEFINITION_NAME_REFERENCE_in_ruleWorkDef2507);
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

            otherlv_1=(Token)match(input,15,FOLLOW_15_in_ruleWorkDef2519); 

                	newLeafNode(otherlv_1, grammarAccess.getWorkDefAccess().getLeftCurlyBracketKeyword_1());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1294:1: (otherlv_2= 'id' ( (lv_id_3_0= RULE_STRING ) ) )+
            int cnt44=0;
            loop44:
            do {
                int alt44=2;
                int LA44_0 = input.LA(1);

                if ( (LA44_0==39) ) {
                    alt44=1;
                }


                switch (alt44) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1294:3: otherlv_2= 'id' ( (lv_id_3_0= RULE_STRING ) )
            	    {
            	    otherlv_2=(Token)match(input,39,FOLLOW_39_in_ruleWorkDef2532); 

            	        	newLeafNode(otherlv_2, grammarAccess.getWorkDefAccess().getIdKeyword_2_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1298:1: ( (lv_id_3_0= RULE_STRING ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1299:1: (lv_id_3_0= RULE_STRING )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1299:1: (lv_id_3_0= RULE_STRING )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1300:3: lv_id_3_0= RULE_STRING
            	    {
            	    lv_id_3_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleWorkDef2549); 

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
            	    if ( cnt44 >= 1 ) break loop44;
                        EarlyExitException eee =
                            new EarlyExitException(44, input);
                        throw eee;
                }
                cnt44++;
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1316:4: (otherlv_4= 'startState' ( ( ruleSTATE_NAME_REFERENCE ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1316:6: otherlv_4= 'startState' ( ( ruleSTATE_NAME_REFERENCE ) )
            {
            otherlv_4=(Token)match(input,40,FOLLOW_40_in_ruleWorkDef2569); 

                	newLeafNode(otherlv_4, grammarAccess.getWorkDefAccess().getStartStateKeyword_3_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1320:1: ( ( ruleSTATE_NAME_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1321:1: ( ruleSTATE_NAME_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1321:1: ( ruleSTATE_NAME_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1322:3: ruleSTATE_NAME_REFERENCE
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getWorkDefRule());
            	        }
                    
             
            	        newCompositeNode(grammarAccess.getWorkDefAccess().getStartStateStateDefCrossReference_3_1_0()); 
            	    
            pushFollow(FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleWorkDef2592);
            ruleSTATE_NAME_REFERENCE();

            state._fsp--;

             
            	        afterParserOrEnumRuleCall();
            	    

            }


            }


            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1335:3: ( (lv_widgetDefs_6_0= ruleWidgetDef ) )*
            loop45:
            do {
                int alt45=2;
                int LA45_0 = input.LA(1);

                if ( (LA45_0==41) ) {
                    alt45=1;
                }


                switch (alt45) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1336:1: (lv_widgetDefs_6_0= ruleWidgetDef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1336:1: (lv_widgetDefs_6_0= ruleWidgetDef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1337:3: lv_widgetDefs_6_0= ruleWidgetDef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getWorkDefAccess().getWidgetDefsWidgetDefParserRuleCall_4_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleWidgetDef_in_ruleWorkDef2614);
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
            	    break loop45;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1353:3: ( (lv_decisionReviewDefs_7_0= ruleDecisionReviewDef ) )*
            loop46:
            do {
                int alt46=2;
                int LA46_0 = input.LA(1);

                if ( (LA46_0==61) ) {
                    alt46=1;
                }


                switch (alt46) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1354:1: (lv_decisionReviewDefs_7_0= ruleDecisionReviewDef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1354:1: (lv_decisionReviewDefs_7_0= ruleDecisionReviewDef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1355:3: lv_decisionReviewDefs_7_0= ruleDecisionReviewDef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getWorkDefAccess().getDecisionReviewDefsDecisionReviewDefParserRuleCall_5_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleDecisionReviewDef_in_ruleWorkDef2636);
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
            	    break loop46;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1371:3: ( (lv_peerReviewDefs_8_0= rulePeerReviewDef ) )*
            loop47:
            do {
                int alt47=2;
                int LA47_0 = input.LA(1);

                if ( (LA47_0==69) ) {
                    alt47=1;
                }


                switch (alt47) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1372:1: (lv_peerReviewDefs_8_0= rulePeerReviewDef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1372:1: (lv_peerReviewDefs_8_0= rulePeerReviewDef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1373:3: lv_peerReviewDefs_8_0= rulePeerReviewDef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getWorkDefAccess().getPeerReviewDefsPeerReviewDefParserRuleCall_6_0()); 
            	    	    
            	    pushFollow(FOLLOW_rulePeerReviewDef_in_ruleWorkDef2658);
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
            	    break loop47;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1389:3: ( (lv_states_9_0= ruleStateDef ) )+
            int cnt48=0;
            loop48:
            do {
                int alt48=2;
                int LA48_0 = input.LA(1);

                if ( (LA48_0==53) ) {
                    alt48=1;
                }


                switch (alt48) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1390:1: (lv_states_9_0= ruleStateDef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1390:1: (lv_states_9_0= ruleStateDef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1391:3: lv_states_9_0= ruleStateDef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getWorkDefAccess().getStatesStateDefParserRuleCall_7_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleStateDef_in_ruleWorkDef2680);
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
            	    if ( cnt48 >= 1 ) break loop48;
                        EarlyExitException eee =
                            new EarlyExitException(48, input);
                        throw eee;
                }
                cnt48++;
            } while (true);

            otherlv_10=(Token)match(input,20,FOLLOW_20_in_ruleWorkDef2693); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1419:1: entryRuleWidgetDef returns [EObject current=null] : iv_ruleWidgetDef= ruleWidgetDef EOF ;
    public final EObject entryRuleWidgetDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleWidgetDef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1420:2: (iv_ruleWidgetDef= ruleWidgetDef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1421:2: iv_ruleWidgetDef= ruleWidgetDef EOF
            {
             newCompositeNode(grammarAccess.getWidgetDefRule()); 
            pushFollow(FOLLOW_ruleWidgetDef_in_entryRuleWidgetDef2729);
            iv_ruleWidgetDef=ruleWidgetDef();

            state._fsp--;

             current =iv_ruleWidgetDef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleWidgetDef2739); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1428:1: ruleWidgetDef returns [EObject current=null] : (otherlv_0= 'widgetDefinition' ( (lv_name_1_0= ruleWIDGET_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'attributeName' ( (lv_attributeName_4_0= RULE_STRING ) ) )? (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'xWidgetName' ( (lv_xWidgetName_8_0= RULE_STRING ) ) )? (otherlv_9= 'defaultValue' ( (lv_defaultValue_10_0= RULE_STRING ) ) )? (otherlv_11= 'height' ( (lv_height_12_0= RULE_INT ) ) )? (otherlv_13= 'option' ( (lv_option_14_0= ruleWidgetOption ) ) )* (otherlv_15= 'minConstraint' ( (lv_minConstraint_16_0= RULE_STRING ) ) )? (otherlv_17= 'maxConstraint' ( (lv_maxConstraint_18_0= RULE_STRING ) ) )? otherlv_19= '}' ) ;
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
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1431:28: ( (otherlv_0= 'widgetDefinition' ( (lv_name_1_0= ruleWIDGET_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'attributeName' ( (lv_attributeName_4_0= RULE_STRING ) ) )? (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'xWidgetName' ( (lv_xWidgetName_8_0= RULE_STRING ) ) )? (otherlv_9= 'defaultValue' ( (lv_defaultValue_10_0= RULE_STRING ) ) )? (otherlv_11= 'height' ( (lv_height_12_0= RULE_INT ) ) )? (otherlv_13= 'option' ( (lv_option_14_0= ruleWidgetOption ) ) )* (otherlv_15= 'minConstraint' ( (lv_minConstraint_16_0= RULE_STRING ) ) )? (otherlv_17= 'maxConstraint' ( (lv_maxConstraint_18_0= RULE_STRING ) ) )? otherlv_19= '}' ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1432:1: (otherlv_0= 'widgetDefinition' ( (lv_name_1_0= ruleWIDGET_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'attributeName' ( (lv_attributeName_4_0= RULE_STRING ) ) )? (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'xWidgetName' ( (lv_xWidgetName_8_0= RULE_STRING ) ) )? (otherlv_9= 'defaultValue' ( (lv_defaultValue_10_0= RULE_STRING ) ) )? (otherlv_11= 'height' ( (lv_height_12_0= RULE_INT ) ) )? (otherlv_13= 'option' ( (lv_option_14_0= ruleWidgetOption ) ) )* (otherlv_15= 'minConstraint' ( (lv_minConstraint_16_0= RULE_STRING ) ) )? (otherlv_17= 'maxConstraint' ( (lv_maxConstraint_18_0= RULE_STRING ) ) )? otherlv_19= '}' )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1432:1: (otherlv_0= 'widgetDefinition' ( (lv_name_1_0= ruleWIDGET_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'attributeName' ( (lv_attributeName_4_0= RULE_STRING ) ) )? (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'xWidgetName' ( (lv_xWidgetName_8_0= RULE_STRING ) ) )? (otherlv_9= 'defaultValue' ( (lv_defaultValue_10_0= RULE_STRING ) ) )? (otherlv_11= 'height' ( (lv_height_12_0= RULE_INT ) ) )? (otherlv_13= 'option' ( (lv_option_14_0= ruleWidgetOption ) ) )* (otherlv_15= 'minConstraint' ( (lv_minConstraint_16_0= RULE_STRING ) ) )? (otherlv_17= 'maxConstraint' ( (lv_maxConstraint_18_0= RULE_STRING ) ) )? otherlv_19= '}' )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1432:3: otherlv_0= 'widgetDefinition' ( (lv_name_1_0= ruleWIDGET_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'attributeName' ( (lv_attributeName_4_0= RULE_STRING ) ) )? (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'xWidgetName' ( (lv_xWidgetName_8_0= RULE_STRING ) ) )? (otherlv_9= 'defaultValue' ( (lv_defaultValue_10_0= RULE_STRING ) ) )? (otherlv_11= 'height' ( (lv_height_12_0= RULE_INT ) ) )? (otherlv_13= 'option' ( (lv_option_14_0= ruleWidgetOption ) ) )* (otherlv_15= 'minConstraint' ( (lv_minConstraint_16_0= RULE_STRING ) ) )? (otherlv_17= 'maxConstraint' ( (lv_maxConstraint_18_0= RULE_STRING ) ) )? otherlv_19= '}'
            {
            otherlv_0=(Token)match(input,41,FOLLOW_41_in_ruleWidgetDef2776); 

                	newLeafNode(otherlv_0, grammarAccess.getWidgetDefAccess().getWidgetDefinitionKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1436:1: ( (lv_name_1_0= ruleWIDGET_NAME_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1437:1: (lv_name_1_0= ruleWIDGET_NAME_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1437:1: (lv_name_1_0= ruleWIDGET_NAME_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1438:3: lv_name_1_0= ruleWIDGET_NAME_REFERENCE
            {
             
            	        newCompositeNode(grammarAccess.getWidgetDefAccess().getNameWIDGET_NAME_REFERENCEParserRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_ruleWIDGET_NAME_REFERENCE_in_ruleWidgetDef2797);
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

            otherlv_2=(Token)match(input,15,FOLLOW_15_in_ruleWidgetDef2809); 

                	newLeafNode(otherlv_2, grammarAccess.getWidgetDefAccess().getLeftCurlyBracketKeyword_2());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1458:1: (otherlv_3= 'attributeName' ( (lv_attributeName_4_0= RULE_STRING ) ) )?
            int alt49=2;
            int LA49_0 = input.LA(1);

            if ( (LA49_0==42) ) {
                alt49=1;
            }
            switch (alt49) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1458:3: otherlv_3= 'attributeName' ( (lv_attributeName_4_0= RULE_STRING ) )
                    {
                    otherlv_3=(Token)match(input,42,FOLLOW_42_in_ruleWidgetDef2822); 

                        	newLeafNode(otherlv_3, grammarAccess.getWidgetDefAccess().getAttributeNameKeyword_3_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1462:1: ( (lv_attributeName_4_0= RULE_STRING ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1463:1: (lv_attributeName_4_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1463:1: (lv_attributeName_4_0= RULE_STRING )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1464:3: lv_attributeName_4_0= RULE_STRING
                    {
                    lv_attributeName_4_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleWidgetDef2839); 

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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1480:4: (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )?
            int alt50=2;
            int LA50_0 = input.LA(1);

            if ( (LA50_0==43) ) {
                alt50=1;
            }
            switch (alt50) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1480:6: otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) )
                    {
                    otherlv_5=(Token)match(input,43,FOLLOW_43_in_ruleWidgetDef2859); 

                        	newLeafNode(otherlv_5, grammarAccess.getWidgetDefAccess().getDescriptionKeyword_4_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1484:1: ( (lv_description_6_0= RULE_STRING ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1485:1: (lv_description_6_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1485:1: (lv_description_6_0= RULE_STRING )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1486:3: lv_description_6_0= RULE_STRING
                    {
                    lv_description_6_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleWidgetDef2876); 

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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1502:4: (otherlv_7= 'xWidgetName' ( (lv_xWidgetName_8_0= RULE_STRING ) ) )?
            int alt51=2;
            int LA51_0 = input.LA(1);

            if ( (LA51_0==44) ) {
                alt51=1;
            }
            switch (alt51) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1502:6: otherlv_7= 'xWidgetName' ( (lv_xWidgetName_8_0= RULE_STRING ) )
                    {
                    otherlv_7=(Token)match(input,44,FOLLOW_44_in_ruleWidgetDef2896); 

                        	newLeafNode(otherlv_7, grammarAccess.getWidgetDefAccess().getXWidgetNameKeyword_5_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1506:1: ( (lv_xWidgetName_8_0= RULE_STRING ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1507:1: (lv_xWidgetName_8_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1507:1: (lv_xWidgetName_8_0= RULE_STRING )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1508:3: lv_xWidgetName_8_0= RULE_STRING
                    {
                    lv_xWidgetName_8_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleWidgetDef2913); 

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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1524:4: (otherlv_9= 'defaultValue' ( (lv_defaultValue_10_0= RULE_STRING ) ) )?
            int alt52=2;
            int LA52_0 = input.LA(1);

            if ( (LA52_0==45) ) {
                alt52=1;
            }
            switch (alt52) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1524:6: otherlv_9= 'defaultValue' ( (lv_defaultValue_10_0= RULE_STRING ) )
                    {
                    otherlv_9=(Token)match(input,45,FOLLOW_45_in_ruleWidgetDef2933); 

                        	newLeafNode(otherlv_9, grammarAccess.getWidgetDefAccess().getDefaultValueKeyword_6_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1528:1: ( (lv_defaultValue_10_0= RULE_STRING ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1529:1: (lv_defaultValue_10_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1529:1: (lv_defaultValue_10_0= RULE_STRING )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1530:3: lv_defaultValue_10_0= RULE_STRING
                    {
                    lv_defaultValue_10_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleWidgetDef2950); 

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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1546:4: (otherlv_11= 'height' ( (lv_height_12_0= RULE_INT ) ) )?
            int alt53=2;
            int LA53_0 = input.LA(1);

            if ( (LA53_0==46) ) {
                alt53=1;
            }
            switch (alt53) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1546:6: otherlv_11= 'height' ( (lv_height_12_0= RULE_INT ) )
                    {
                    otherlv_11=(Token)match(input,46,FOLLOW_46_in_ruleWidgetDef2970); 

                        	newLeafNode(otherlv_11, grammarAccess.getWidgetDefAccess().getHeightKeyword_7_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1550:1: ( (lv_height_12_0= RULE_INT ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1551:1: (lv_height_12_0= RULE_INT )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1551:1: (lv_height_12_0= RULE_INT )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1552:3: lv_height_12_0= RULE_INT
                    {
                    lv_height_12_0=(Token)match(input,RULE_INT,FOLLOW_RULE_INT_in_ruleWidgetDef2987); 

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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1568:4: (otherlv_13= 'option' ( (lv_option_14_0= ruleWidgetOption ) ) )*
            loop54:
            do {
                int alt54=2;
                int LA54_0 = input.LA(1);

                if ( (LA54_0==47) ) {
                    alt54=1;
                }


                switch (alt54) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1568:6: otherlv_13= 'option' ( (lv_option_14_0= ruleWidgetOption ) )
            	    {
            	    otherlv_13=(Token)match(input,47,FOLLOW_47_in_ruleWidgetDef3007); 

            	        	newLeafNode(otherlv_13, grammarAccess.getWidgetDefAccess().getOptionKeyword_8_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1572:1: ( (lv_option_14_0= ruleWidgetOption ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1573:1: (lv_option_14_0= ruleWidgetOption )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1573:1: (lv_option_14_0= ruleWidgetOption )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1574:3: lv_option_14_0= ruleWidgetOption
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getWidgetDefAccess().getOptionWidgetOptionParserRuleCall_8_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleWidgetOption_in_ruleWidgetDef3028);
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
            	    break loop54;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1590:4: (otherlv_15= 'minConstraint' ( (lv_minConstraint_16_0= RULE_STRING ) ) )?
            int alt55=2;
            int LA55_0 = input.LA(1);

            if ( (LA55_0==48) ) {
                alt55=1;
            }
            switch (alt55) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1590:6: otherlv_15= 'minConstraint' ( (lv_minConstraint_16_0= RULE_STRING ) )
                    {
                    otherlv_15=(Token)match(input,48,FOLLOW_48_in_ruleWidgetDef3043); 

                        	newLeafNode(otherlv_15, grammarAccess.getWidgetDefAccess().getMinConstraintKeyword_9_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1594:1: ( (lv_minConstraint_16_0= RULE_STRING ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1595:1: (lv_minConstraint_16_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1595:1: (lv_minConstraint_16_0= RULE_STRING )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1596:3: lv_minConstraint_16_0= RULE_STRING
                    {
                    lv_minConstraint_16_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleWidgetDef3060); 

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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1612:4: (otherlv_17= 'maxConstraint' ( (lv_maxConstraint_18_0= RULE_STRING ) ) )?
            int alt56=2;
            int LA56_0 = input.LA(1);

            if ( (LA56_0==49) ) {
                alt56=1;
            }
            switch (alt56) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1612:6: otherlv_17= 'maxConstraint' ( (lv_maxConstraint_18_0= RULE_STRING ) )
                    {
                    otherlv_17=(Token)match(input,49,FOLLOW_49_in_ruleWidgetDef3080); 

                        	newLeafNode(otherlv_17, grammarAccess.getWidgetDefAccess().getMaxConstraintKeyword_10_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1616:1: ( (lv_maxConstraint_18_0= RULE_STRING ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1617:1: (lv_maxConstraint_18_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1617:1: (lv_maxConstraint_18_0= RULE_STRING )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1618:3: lv_maxConstraint_18_0= RULE_STRING
                    {
                    lv_maxConstraint_18_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleWidgetDef3097); 

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

            otherlv_19=(Token)match(input,20,FOLLOW_20_in_ruleWidgetDef3116); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1646:1: entryRuleWidgetRef returns [EObject current=null] : iv_ruleWidgetRef= ruleWidgetRef EOF ;
    public final EObject entryRuleWidgetRef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleWidgetRef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1647:2: (iv_ruleWidgetRef= ruleWidgetRef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1648:2: iv_ruleWidgetRef= ruleWidgetRef EOF
            {
             newCompositeNode(grammarAccess.getWidgetRefRule()); 
            pushFollow(FOLLOW_ruleWidgetRef_in_entryRuleWidgetRef3152);
            iv_ruleWidgetRef=ruleWidgetRef();

            state._fsp--;

             current =iv_ruleWidgetRef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleWidgetRef3162); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1655:1: ruleWidgetRef returns [EObject current=null] : (otherlv_0= 'widget' ( ( ruleWIDGET_NAME_REFERENCE ) ) ) ;
    public final EObject ruleWidgetRef() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1658:28: ( (otherlv_0= 'widget' ( ( ruleWIDGET_NAME_REFERENCE ) ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1659:1: (otherlv_0= 'widget' ( ( ruleWIDGET_NAME_REFERENCE ) ) )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1659:1: (otherlv_0= 'widget' ( ( ruleWIDGET_NAME_REFERENCE ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1659:3: otherlv_0= 'widget' ( ( ruleWIDGET_NAME_REFERENCE ) )
            {
            otherlv_0=(Token)match(input,50,FOLLOW_50_in_ruleWidgetRef3199); 

                	newLeafNode(otherlv_0, grammarAccess.getWidgetRefAccess().getWidgetKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1663:1: ( ( ruleWIDGET_NAME_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1664:1: ( ruleWIDGET_NAME_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1664:1: ( ruleWIDGET_NAME_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1665:3: ruleWIDGET_NAME_REFERENCE
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getWidgetRefRule());
            	        }
                    
             
            	        newCompositeNode(grammarAccess.getWidgetRefAccess().getWidgetWidgetDefCrossReference_1_0()); 
            	    
            pushFollow(FOLLOW_ruleWIDGET_NAME_REFERENCE_in_ruleWidgetRef3222);
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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1686:1: entryRuleAttrWidget returns [EObject current=null] : iv_ruleAttrWidget= ruleAttrWidget EOF ;
    public final EObject entryRuleAttrWidget() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttrWidget = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1687:2: (iv_ruleAttrWidget= ruleAttrWidget EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1688:2: iv_ruleAttrWidget= ruleAttrWidget EOF
            {
             newCompositeNode(grammarAccess.getAttrWidgetRule()); 
            pushFollow(FOLLOW_ruleAttrWidget_in_entryRuleAttrWidget3258);
            iv_ruleAttrWidget=ruleAttrWidget();

            state._fsp--;

             current =iv_ruleAttrWidget; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttrWidget3268); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1695:1: ruleAttrWidget returns [EObject current=null] : (otherlv_0= 'attributeWidget' ( (lv_attributeName_1_0= RULE_STRING ) ) (otherlv_2= 'with' ( (lv_option_3_0= ruleWidgetOption ) ) )* ) ;
    public final EObject ruleAttrWidget() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_attributeName_1_0=null;
        Token otherlv_2=null;
        AntlrDatatypeRuleToken lv_option_3_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1698:28: ( (otherlv_0= 'attributeWidget' ( (lv_attributeName_1_0= RULE_STRING ) ) (otherlv_2= 'with' ( (lv_option_3_0= ruleWidgetOption ) ) )* ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1699:1: (otherlv_0= 'attributeWidget' ( (lv_attributeName_1_0= RULE_STRING ) ) (otherlv_2= 'with' ( (lv_option_3_0= ruleWidgetOption ) ) )* )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1699:1: (otherlv_0= 'attributeWidget' ( (lv_attributeName_1_0= RULE_STRING ) ) (otherlv_2= 'with' ( (lv_option_3_0= ruleWidgetOption ) ) )* )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1699:3: otherlv_0= 'attributeWidget' ( (lv_attributeName_1_0= RULE_STRING ) ) (otherlv_2= 'with' ( (lv_option_3_0= ruleWidgetOption ) ) )*
            {
            otherlv_0=(Token)match(input,51,FOLLOW_51_in_ruleAttrWidget3305); 

                	newLeafNode(otherlv_0, grammarAccess.getAttrWidgetAccess().getAttributeWidgetKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1703:1: ( (lv_attributeName_1_0= RULE_STRING ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1704:1: (lv_attributeName_1_0= RULE_STRING )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1704:1: (lv_attributeName_1_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1705:3: lv_attributeName_1_0= RULE_STRING
            {
            lv_attributeName_1_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAttrWidget3322); 

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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1721:2: (otherlv_2= 'with' ( (lv_option_3_0= ruleWidgetOption ) ) )*
            loop57:
            do {
                int alt57=2;
                int LA57_0 = input.LA(1);

                if ( (LA57_0==52) ) {
                    alt57=1;
                }


                switch (alt57) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1721:4: otherlv_2= 'with' ( (lv_option_3_0= ruleWidgetOption ) )
            	    {
            	    otherlv_2=(Token)match(input,52,FOLLOW_52_in_ruleAttrWidget3340); 

            	        	newLeafNode(otherlv_2, grammarAccess.getAttrWidgetAccess().getWithKeyword_2_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1725:1: ( (lv_option_3_0= ruleWidgetOption ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1726:1: (lv_option_3_0= ruleWidgetOption )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1726:1: (lv_option_3_0= ruleWidgetOption )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1727:3: lv_option_3_0= ruleWidgetOption
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getAttrWidgetAccess().getOptionWidgetOptionParserRuleCall_2_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleWidgetOption_in_ruleAttrWidget3361);
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
            	    break loop57;
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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1751:1: entryRuleStateDef returns [EObject current=null] : iv_ruleStateDef= ruleStateDef EOF ;
    public final EObject entryRuleStateDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleStateDef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1752:2: (iv_ruleStateDef= ruleStateDef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1753:2: iv_ruleStateDef= ruleStateDef EOF
            {
             newCompositeNode(grammarAccess.getStateDefRule()); 
            pushFollow(FOLLOW_ruleStateDef_in_entryRuleStateDef3399);
            iv_ruleStateDef=ruleStateDef();

            state._fsp--;

             current =iv_ruleStateDef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleStateDef3409); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1760:1: ruleStateDef returns [EObject current=null] : (otherlv_0= 'state' ( (lv_name_1_0= ruleSTATE_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'description' ( (lv_description_4_0= RULE_STRING ) ) )? otherlv_5= 'type' ( (lv_pageType_6_0= rulePageType ) ) otherlv_7= 'ordinal' ( (lv_ordinal_8_0= RULE_INT ) ) ( (lv_transitionStates_9_0= ruleToState ) )* (otherlv_10= 'rule' ( (lv_rules_11_0= ruleRule ) ) )* ( (lv_decisionReviews_12_0= ruleDecisionReviewRef ) )* ( (lv_peerReviews_13_0= rulePeerReviewRef ) )* (otherlv_14= 'percentWeight' ( (lv_percentWeight_15_0= RULE_INT ) ) )? (otherlv_16= 'recommendedPercentComplete' ( (lv_recommendedPercentComplete_17_0= RULE_INT ) ) )? (otherlv_18= 'color' ( (lv_color_19_0= ruleStateColor ) ) )? ( (lv_layout_20_0= ruleLayoutType ) )? otherlv_21= '}' ) ;
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
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1763:28: ( (otherlv_0= 'state' ( (lv_name_1_0= ruleSTATE_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'description' ( (lv_description_4_0= RULE_STRING ) ) )? otherlv_5= 'type' ( (lv_pageType_6_0= rulePageType ) ) otherlv_7= 'ordinal' ( (lv_ordinal_8_0= RULE_INT ) ) ( (lv_transitionStates_9_0= ruleToState ) )* (otherlv_10= 'rule' ( (lv_rules_11_0= ruleRule ) ) )* ( (lv_decisionReviews_12_0= ruleDecisionReviewRef ) )* ( (lv_peerReviews_13_0= rulePeerReviewRef ) )* (otherlv_14= 'percentWeight' ( (lv_percentWeight_15_0= RULE_INT ) ) )? (otherlv_16= 'recommendedPercentComplete' ( (lv_recommendedPercentComplete_17_0= RULE_INT ) ) )? (otherlv_18= 'color' ( (lv_color_19_0= ruleStateColor ) ) )? ( (lv_layout_20_0= ruleLayoutType ) )? otherlv_21= '}' ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1764:1: (otherlv_0= 'state' ( (lv_name_1_0= ruleSTATE_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'description' ( (lv_description_4_0= RULE_STRING ) ) )? otherlv_5= 'type' ( (lv_pageType_6_0= rulePageType ) ) otherlv_7= 'ordinal' ( (lv_ordinal_8_0= RULE_INT ) ) ( (lv_transitionStates_9_0= ruleToState ) )* (otherlv_10= 'rule' ( (lv_rules_11_0= ruleRule ) ) )* ( (lv_decisionReviews_12_0= ruleDecisionReviewRef ) )* ( (lv_peerReviews_13_0= rulePeerReviewRef ) )* (otherlv_14= 'percentWeight' ( (lv_percentWeight_15_0= RULE_INT ) ) )? (otherlv_16= 'recommendedPercentComplete' ( (lv_recommendedPercentComplete_17_0= RULE_INT ) ) )? (otherlv_18= 'color' ( (lv_color_19_0= ruleStateColor ) ) )? ( (lv_layout_20_0= ruleLayoutType ) )? otherlv_21= '}' )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1764:1: (otherlv_0= 'state' ( (lv_name_1_0= ruleSTATE_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'description' ( (lv_description_4_0= RULE_STRING ) ) )? otherlv_5= 'type' ( (lv_pageType_6_0= rulePageType ) ) otherlv_7= 'ordinal' ( (lv_ordinal_8_0= RULE_INT ) ) ( (lv_transitionStates_9_0= ruleToState ) )* (otherlv_10= 'rule' ( (lv_rules_11_0= ruleRule ) ) )* ( (lv_decisionReviews_12_0= ruleDecisionReviewRef ) )* ( (lv_peerReviews_13_0= rulePeerReviewRef ) )* (otherlv_14= 'percentWeight' ( (lv_percentWeight_15_0= RULE_INT ) ) )? (otherlv_16= 'recommendedPercentComplete' ( (lv_recommendedPercentComplete_17_0= RULE_INT ) ) )? (otherlv_18= 'color' ( (lv_color_19_0= ruleStateColor ) ) )? ( (lv_layout_20_0= ruleLayoutType ) )? otherlv_21= '}' )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1764:3: otherlv_0= 'state' ( (lv_name_1_0= ruleSTATE_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'description' ( (lv_description_4_0= RULE_STRING ) ) )? otherlv_5= 'type' ( (lv_pageType_6_0= rulePageType ) ) otherlv_7= 'ordinal' ( (lv_ordinal_8_0= RULE_INT ) ) ( (lv_transitionStates_9_0= ruleToState ) )* (otherlv_10= 'rule' ( (lv_rules_11_0= ruleRule ) ) )* ( (lv_decisionReviews_12_0= ruleDecisionReviewRef ) )* ( (lv_peerReviews_13_0= rulePeerReviewRef ) )* (otherlv_14= 'percentWeight' ( (lv_percentWeight_15_0= RULE_INT ) ) )? (otherlv_16= 'recommendedPercentComplete' ( (lv_recommendedPercentComplete_17_0= RULE_INT ) ) )? (otherlv_18= 'color' ( (lv_color_19_0= ruleStateColor ) ) )? ( (lv_layout_20_0= ruleLayoutType ) )? otherlv_21= '}'
            {
            otherlv_0=(Token)match(input,53,FOLLOW_53_in_ruleStateDef3446); 

                	newLeafNode(otherlv_0, grammarAccess.getStateDefAccess().getStateKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1768:1: ( (lv_name_1_0= ruleSTATE_NAME_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1769:1: (lv_name_1_0= ruleSTATE_NAME_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1769:1: (lv_name_1_0= ruleSTATE_NAME_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1770:3: lv_name_1_0= ruleSTATE_NAME_REFERENCE
            {
             
            	        newCompositeNode(grammarAccess.getStateDefAccess().getNameSTATE_NAME_REFERENCEParserRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleStateDef3467);
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

            otherlv_2=(Token)match(input,15,FOLLOW_15_in_ruleStateDef3479); 

                	newLeafNode(otherlv_2, grammarAccess.getStateDefAccess().getLeftCurlyBracketKeyword_2());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1790:1: (otherlv_3= 'description' ( (lv_description_4_0= RULE_STRING ) ) )?
            int alt58=2;
            int LA58_0 = input.LA(1);

            if ( (LA58_0==43) ) {
                alt58=1;
            }
            switch (alt58) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1790:3: otherlv_3= 'description' ( (lv_description_4_0= RULE_STRING ) )
                    {
                    otherlv_3=(Token)match(input,43,FOLLOW_43_in_ruleStateDef3492); 

                        	newLeafNode(otherlv_3, grammarAccess.getStateDefAccess().getDescriptionKeyword_3_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1794:1: ( (lv_description_4_0= RULE_STRING ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1795:1: (lv_description_4_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1795:1: (lv_description_4_0= RULE_STRING )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1796:3: lv_description_4_0= RULE_STRING
                    {
                    lv_description_4_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleStateDef3509); 

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

            otherlv_5=(Token)match(input,54,FOLLOW_54_in_ruleStateDef3528); 

                	newLeafNode(otherlv_5, grammarAccess.getStateDefAccess().getTypeKeyword_4());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1816:1: ( (lv_pageType_6_0= rulePageType ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1817:1: (lv_pageType_6_0= rulePageType )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1817:1: (lv_pageType_6_0= rulePageType )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1818:3: lv_pageType_6_0= rulePageType
            {
             
            	        newCompositeNode(grammarAccess.getStateDefAccess().getPageTypePageTypeParserRuleCall_5_0()); 
            	    
            pushFollow(FOLLOW_rulePageType_in_ruleStateDef3549);
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

            otherlv_7=(Token)match(input,55,FOLLOW_55_in_ruleStateDef3561); 

                	newLeafNode(otherlv_7, grammarAccess.getStateDefAccess().getOrdinalKeyword_6());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1838:1: ( (lv_ordinal_8_0= RULE_INT ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1839:1: (lv_ordinal_8_0= RULE_INT )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1839:1: (lv_ordinal_8_0= RULE_INT )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1840:3: lv_ordinal_8_0= RULE_INT
            {
            lv_ordinal_8_0=(Token)match(input,RULE_INT,FOLLOW_RULE_INT_in_ruleStateDef3578); 

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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1856:2: ( (lv_transitionStates_9_0= ruleToState ) )*
            loop59:
            do {
                int alt59=2;
                int LA59_0 = input.LA(1);

                if ( (LA59_0==73) ) {
                    alt59=1;
                }


                switch (alt59) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1857:1: (lv_transitionStates_9_0= ruleToState )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1857:1: (lv_transitionStates_9_0= ruleToState )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1858:3: lv_transitionStates_9_0= ruleToState
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getStateDefAccess().getTransitionStatesToStateParserRuleCall_8_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleToState_in_ruleStateDef3604);
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
            	    break loop59;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1874:3: (otherlv_10= 'rule' ( (lv_rules_11_0= ruleRule ) ) )*
            loop60:
            do {
                int alt60=2;
                int LA60_0 = input.LA(1);

                if ( (LA60_0==56) ) {
                    alt60=1;
                }


                switch (alt60) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1874:5: otherlv_10= 'rule' ( (lv_rules_11_0= ruleRule ) )
            	    {
            	    otherlv_10=(Token)match(input,56,FOLLOW_56_in_ruleStateDef3618); 

            	        	newLeafNode(otherlv_10, grammarAccess.getStateDefAccess().getRuleKeyword_9_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1878:1: ( (lv_rules_11_0= ruleRule ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1879:1: (lv_rules_11_0= ruleRule )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1879:1: (lv_rules_11_0= ruleRule )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1880:3: lv_rules_11_0= ruleRule
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getStateDefAccess().getRulesRuleParserRuleCall_9_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleRule_in_ruleStateDef3639);
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
            	    break loop60;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1896:4: ( (lv_decisionReviews_12_0= ruleDecisionReviewRef ) )*
            loop61:
            do {
                int alt61=2;
                int LA61_0 = input.LA(1);

                if ( (LA61_0==60) ) {
                    alt61=1;
                }


                switch (alt61) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1897:1: (lv_decisionReviews_12_0= ruleDecisionReviewRef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1897:1: (lv_decisionReviews_12_0= ruleDecisionReviewRef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1898:3: lv_decisionReviews_12_0= ruleDecisionReviewRef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getStateDefAccess().getDecisionReviewsDecisionReviewRefParserRuleCall_10_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleDecisionReviewRef_in_ruleStateDef3662);
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
            	    break loop61;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1914:3: ( (lv_peerReviews_13_0= rulePeerReviewRef ) )*
            loop62:
            do {
                int alt62=2;
                int LA62_0 = input.LA(1);

                if ( (LA62_0==68) ) {
                    alt62=1;
                }


                switch (alt62) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1915:1: (lv_peerReviews_13_0= rulePeerReviewRef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1915:1: (lv_peerReviews_13_0= rulePeerReviewRef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1916:3: lv_peerReviews_13_0= rulePeerReviewRef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getStateDefAccess().getPeerReviewsPeerReviewRefParserRuleCall_11_0()); 
            	    	    
            	    pushFollow(FOLLOW_rulePeerReviewRef_in_ruleStateDef3684);
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
            	    break loop62;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1932:3: (otherlv_14= 'percentWeight' ( (lv_percentWeight_15_0= RULE_INT ) ) )?
            int alt63=2;
            int LA63_0 = input.LA(1);

            if ( (LA63_0==57) ) {
                alt63=1;
            }
            switch (alt63) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1932:5: otherlv_14= 'percentWeight' ( (lv_percentWeight_15_0= RULE_INT ) )
                    {
                    otherlv_14=(Token)match(input,57,FOLLOW_57_in_ruleStateDef3698); 

                        	newLeafNode(otherlv_14, grammarAccess.getStateDefAccess().getPercentWeightKeyword_12_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1936:1: ( (lv_percentWeight_15_0= RULE_INT ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1937:1: (lv_percentWeight_15_0= RULE_INT )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1937:1: (lv_percentWeight_15_0= RULE_INT )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1938:3: lv_percentWeight_15_0= RULE_INT
                    {
                    lv_percentWeight_15_0=(Token)match(input,RULE_INT,FOLLOW_RULE_INT_in_ruleStateDef3715); 

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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1954:4: (otherlv_16= 'recommendedPercentComplete' ( (lv_recommendedPercentComplete_17_0= RULE_INT ) ) )?
            int alt64=2;
            int LA64_0 = input.LA(1);

            if ( (LA64_0==58) ) {
                alt64=1;
            }
            switch (alt64) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1954:6: otherlv_16= 'recommendedPercentComplete' ( (lv_recommendedPercentComplete_17_0= RULE_INT ) )
                    {
                    otherlv_16=(Token)match(input,58,FOLLOW_58_in_ruleStateDef3735); 

                        	newLeafNode(otherlv_16, grammarAccess.getStateDefAccess().getRecommendedPercentCompleteKeyword_13_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1958:1: ( (lv_recommendedPercentComplete_17_0= RULE_INT ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1959:1: (lv_recommendedPercentComplete_17_0= RULE_INT )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1959:1: (lv_recommendedPercentComplete_17_0= RULE_INT )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1960:3: lv_recommendedPercentComplete_17_0= RULE_INT
                    {
                    lv_recommendedPercentComplete_17_0=(Token)match(input,RULE_INT,FOLLOW_RULE_INT_in_ruleStateDef3752); 

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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1976:4: (otherlv_18= 'color' ( (lv_color_19_0= ruleStateColor ) ) )?
            int alt65=2;
            int LA65_0 = input.LA(1);

            if ( (LA65_0==59) ) {
                alt65=1;
            }
            switch (alt65) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1976:6: otherlv_18= 'color' ( (lv_color_19_0= ruleStateColor ) )
                    {
                    otherlv_18=(Token)match(input,59,FOLLOW_59_in_ruleStateDef3772); 

                        	newLeafNode(otherlv_18, grammarAccess.getStateDefAccess().getColorKeyword_14_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1980:1: ( (lv_color_19_0= ruleStateColor ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1981:1: (lv_color_19_0= ruleStateColor )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1981:1: (lv_color_19_0= ruleStateColor )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1982:3: lv_color_19_0= ruleStateColor
                    {
                     
                    	        newCompositeNode(grammarAccess.getStateDefAccess().getColorStateColorParserRuleCall_14_1_0()); 
                    	    
                    pushFollow(FOLLOW_ruleStateColor_in_ruleStateDef3793);
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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1998:4: ( (lv_layout_20_0= ruleLayoutType ) )?
            int alt66=2;
            int LA66_0 = input.LA(1);

            if ( ((LA66_0>=74 && LA66_0<=75)) ) {
                alt66=1;
            }
            switch (alt66) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1999:1: (lv_layout_20_0= ruleLayoutType )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1999:1: (lv_layout_20_0= ruleLayoutType )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2000:3: lv_layout_20_0= ruleLayoutType
                    {
                     
                    	        newCompositeNode(grammarAccess.getStateDefAccess().getLayoutLayoutTypeParserRuleCall_15_0()); 
                    	    
                    pushFollow(FOLLOW_ruleLayoutType_in_ruleStateDef3816);
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

            otherlv_21=(Token)match(input,20,FOLLOW_20_in_ruleStateDef3829); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2028:1: entryRuleDecisionReviewRef returns [EObject current=null] : iv_ruleDecisionReviewRef= ruleDecisionReviewRef EOF ;
    public final EObject entryRuleDecisionReviewRef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDecisionReviewRef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2029:2: (iv_ruleDecisionReviewRef= ruleDecisionReviewRef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2030:2: iv_ruleDecisionReviewRef= ruleDecisionReviewRef EOF
            {
             newCompositeNode(grammarAccess.getDecisionReviewRefRule()); 
            pushFollow(FOLLOW_ruleDecisionReviewRef_in_entryRuleDecisionReviewRef3865);
            iv_ruleDecisionReviewRef=ruleDecisionReviewRef();

            state._fsp--;

             current =iv_ruleDecisionReviewRef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleDecisionReviewRef3875); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2037:1: ruleDecisionReviewRef returns [EObject current=null] : (otherlv_0= 'decisionReview' ( ( ruleDECISION_REVIEW_NAME_REFERENCE ) ) ) ;
    public final EObject ruleDecisionReviewRef() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2040:28: ( (otherlv_0= 'decisionReview' ( ( ruleDECISION_REVIEW_NAME_REFERENCE ) ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2041:1: (otherlv_0= 'decisionReview' ( ( ruleDECISION_REVIEW_NAME_REFERENCE ) ) )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2041:1: (otherlv_0= 'decisionReview' ( ( ruleDECISION_REVIEW_NAME_REFERENCE ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2041:3: otherlv_0= 'decisionReview' ( ( ruleDECISION_REVIEW_NAME_REFERENCE ) )
            {
            otherlv_0=(Token)match(input,60,FOLLOW_60_in_ruleDecisionReviewRef3912); 

                	newLeafNode(otherlv_0, grammarAccess.getDecisionReviewRefAccess().getDecisionReviewKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2045:1: ( ( ruleDECISION_REVIEW_NAME_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2046:1: ( ruleDECISION_REVIEW_NAME_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2046:1: ( ruleDECISION_REVIEW_NAME_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2047:3: ruleDECISION_REVIEW_NAME_REFERENCE
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getDecisionReviewRefRule());
            	        }
                    
             
            	        newCompositeNode(grammarAccess.getDecisionReviewRefAccess().getDecisionReviewDecisionReviewDefCrossReference_1_0()); 
            	    
            pushFollow(FOLLOW_ruleDECISION_REVIEW_NAME_REFERENCE_in_ruleDecisionReviewRef3935);
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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2068:1: entryRuleDecisionReviewDef returns [EObject current=null] : iv_ruleDecisionReviewDef= ruleDecisionReviewDef EOF ;
    public final EObject entryRuleDecisionReviewDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDecisionReviewDef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2069:2: (iv_ruleDecisionReviewDef= ruleDecisionReviewDef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2070:2: iv_ruleDecisionReviewDef= ruleDecisionReviewDef EOF
            {
             newCompositeNode(grammarAccess.getDecisionReviewDefRule()); 
            pushFollow(FOLLOW_ruleDecisionReviewDef_in_entryRuleDecisionReviewDef3971);
            iv_ruleDecisionReviewDef=ruleDecisionReviewDef();

            state._fsp--;

             current =iv_ruleDecisionReviewDef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleDecisionReviewDef3981); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2077:1: ruleDecisionReviewDef returns [EObject current=null] : (otherlv_0= 'decisionReviewDefinition' ( (lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_9= 'blockingType' ( (lv_blockingType_10_0= ruleReviewBlockingType ) ) otherlv_11= 'onEvent' ( (lv_stateEvent_12_0= ruleWorkflowEventType ) ) (otherlv_13= 'assignee' ( (lv_assigneeRefs_14_0= ruleUserRef ) ) )* (otherlv_15= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_16_0= ruleBooleanDef ) ) )? ( (lv_options_17_0= ruleDecisionReviewOpt ) )+ otherlv_18= '}' ) ;
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
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2080:28: ( (otherlv_0= 'decisionReviewDefinition' ( (lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_9= 'blockingType' ( (lv_blockingType_10_0= ruleReviewBlockingType ) ) otherlv_11= 'onEvent' ( (lv_stateEvent_12_0= ruleWorkflowEventType ) ) (otherlv_13= 'assignee' ( (lv_assigneeRefs_14_0= ruleUserRef ) ) )* (otherlv_15= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_16_0= ruleBooleanDef ) ) )? ( (lv_options_17_0= ruleDecisionReviewOpt ) )+ otherlv_18= '}' ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2081:1: (otherlv_0= 'decisionReviewDefinition' ( (lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_9= 'blockingType' ( (lv_blockingType_10_0= ruleReviewBlockingType ) ) otherlv_11= 'onEvent' ( (lv_stateEvent_12_0= ruleWorkflowEventType ) ) (otherlv_13= 'assignee' ( (lv_assigneeRefs_14_0= ruleUserRef ) ) )* (otherlv_15= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_16_0= ruleBooleanDef ) ) )? ( (lv_options_17_0= ruleDecisionReviewOpt ) )+ otherlv_18= '}' )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2081:1: (otherlv_0= 'decisionReviewDefinition' ( (lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_9= 'blockingType' ( (lv_blockingType_10_0= ruleReviewBlockingType ) ) otherlv_11= 'onEvent' ( (lv_stateEvent_12_0= ruleWorkflowEventType ) ) (otherlv_13= 'assignee' ( (lv_assigneeRefs_14_0= ruleUserRef ) ) )* (otherlv_15= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_16_0= ruleBooleanDef ) ) )? ( (lv_options_17_0= ruleDecisionReviewOpt ) )+ otherlv_18= '}' )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2081:3: otherlv_0= 'decisionReviewDefinition' ( (lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_9= 'blockingType' ( (lv_blockingType_10_0= ruleReviewBlockingType ) ) otherlv_11= 'onEvent' ( (lv_stateEvent_12_0= ruleWorkflowEventType ) ) (otherlv_13= 'assignee' ( (lv_assigneeRefs_14_0= ruleUserRef ) ) )* (otherlv_15= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_16_0= ruleBooleanDef ) ) )? ( (lv_options_17_0= ruleDecisionReviewOpt ) )+ otherlv_18= '}'
            {
            otherlv_0=(Token)match(input,61,FOLLOW_61_in_ruleDecisionReviewDef4018); 

                	newLeafNode(otherlv_0, grammarAccess.getDecisionReviewDefAccess().getDecisionReviewDefinitionKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2085:1: ( (lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2086:1: (lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2086:1: (lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2087:3: lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE
            {
             
            	        newCompositeNode(grammarAccess.getDecisionReviewDefAccess().getNameDECISION_REVIEW_NAME_REFERENCEParserRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_ruleDECISION_REVIEW_NAME_REFERENCE_in_ruleDecisionReviewDef4039);
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

            otherlv_2=(Token)match(input,15,FOLLOW_15_in_ruleDecisionReviewDef4051); 

                	newLeafNode(otherlv_2, grammarAccess.getDecisionReviewDefAccess().getLeftCurlyBracketKeyword_2());
                
            otherlv_3=(Token)match(input,62,FOLLOW_62_in_ruleDecisionReviewDef4063); 

                	newLeafNode(otherlv_3, grammarAccess.getDecisionReviewDefAccess().getTitleKeyword_3());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2111:1: ( (lv_title_4_0= RULE_STRING ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2112:1: (lv_title_4_0= RULE_STRING )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2112:1: (lv_title_4_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2113:3: lv_title_4_0= RULE_STRING
            {
            lv_title_4_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleDecisionReviewDef4080); 

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

            otherlv_5=(Token)match(input,43,FOLLOW_43_in_ruleDecisionReviewDef4097); 

                	newLeafNode(otherlv_5, grammarAccess.getDecisionReviewDefAccess().getDescriptionKeyword_5());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2133:1: ( (lv_description_6_0= RULE_STRING ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2134:1: (lv_description_6_0= RULE_STRING )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2134:1: (lv_description_6_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2135:3: lv_description_6_0= RULE_STRING
            {
            lv_description_6_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleDecisionReviewDef4114); 

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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2151:2: (otherlv_7= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )?
            int alt67=2;
            int LA67_0 = input.LA(1);

            if ( (LA67_0==63) ) {
                alt67=1;
            }
            switch (alt67) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2151:4: otherlv_7= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) )
                    {
                    otherlv_7=(Token)match(input,63,FOLLOW_63_in_ruleDecisionReviewDef4132); 

                        	newLeafNode(otherlv_7, grammarAccess.getDecisionReviewDefAccess().getRelatedToStateKeyword_7_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2155:1: ( ( ruleSTATE_NAME_REFERENCE ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2156:1: ( ruleSTATE_NAME_REFERENCE )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2156:1: ( ruleSTATE_NAME_REFERENCE )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2157:3: ruleSTATE_NAME_REFERENCE
                    {

                    			if (current==null) {
                    	            current = createModelElement(grammarAccess.getDecisionReviewDefRule());
                    	        }
                            
                     
                    	        newCompositeNode(grammarAccess.getDecisionReviewDefAccess().getRelatedToStateStateDefCrossReference_7_1_0()); 
                    	    
                    pushFollow(FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleDecisionReviewDef4155);
                    ruleSTATE_NAME_REFERENCE();

                    state._fsp--;

                     
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }


                    }
                    break;

            }

            otherlv_9=(Token)match(input,64,FOLLOW_64_in_ruleDecisionReviewDef4169); 

                	newLeafNode(otherlv_9, grammarAccess.getDecisionReviewDefAccess().getBlockingTypeKeyword_8());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2174:1: ( (lv_blockingType_10_0= ruleReviewBlockingType ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2175:1: (lv_blockingType_10_0= ruleReviewBlockingType )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2175:1: (lv_blockingType_10_0= ruleReviewBlockingType )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2176:3: lv_blockingType_10_0= ruleReviewBlockingType
            {
             
            	        newCompositeNode(grammarAccess.getDecisionReviewDefAccess().getBlockingTypeReviewBlockingTypeEnumRuleCall_9_0()); 
            	    
            pushFollow(FOLLOW_ruleReviewBlockingType_in_ruleDecisionReviewDef4190);
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

            otherlv_11=(Token)match(input,65,FOLLOW_65_in_ruleDecisionReviewDef4202); 

                	newLeafNode(otherlv_11, grammarAccess.getDecisionReviewDefAccess().getOnEventKeyword_10());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2196:1: ( (lv_stateEvent_12_0= ruleWorkflowEventType ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2197:1: (lv_stateEvent_12_0= ruleWorkflowEventType )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2197:1: (lv_stateEvent_12_0= ruleWorkflowEventType )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2198:3: lv_stateEvent_12_0= ruleWorkflowEventType
            {
             
            	        newCompositeNode(grammarAccess.getDecisionReviewDefAccess().getStateEventWorkflowEventTypeEnumRuleCall_11_0()); 
            	    
            pushFollow(FOLLOW_ruleWorkflowEventType_in_ruleDecisionReviewDef4223);
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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2214:2: (otherlv_13= 'assignee' ( (lv_assigneeRefs_14_0= ruleUserRef ) ) )*
            loop68:
            do {
                int alt68=2;
                int LA68_0 = input.LA(1);

                if ( (LA68_0==66) ) {
                    alt68=1;
                }


                switch (alt68) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2214:4: otherlv_13= 'assignee' ( (lv_assigneeRefs_14_0= ruleUserRef ) )
            	    {
            	    otherlv_13=(Token)match(input,66,FOLLOW_66_in_ruleDecisionReviewDef4236); 

            	        	newLeafNode(otherlv_13, grammarAccess.getDecisionReviewDefAccess().getAssigneeKeyword_12_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2218:1: ( (lv_assigneeRefs_14_0= ruleUserRef ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2219:1: (lv_assigneeRefs_14_0= ruleUserRef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2219:1: (lv_assigneeRefs_14_0= ruleUserRef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2220:3: lv_assigneeRefs_14_0= ruleUserRef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getDecisionReviewDefAccess().getAssigneeRefsUserRefParserRuleCall_12_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleUserRef_in_ruleDecisionReviewDef4257);
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
            	    break loop68;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2236:4: (otherlv_15= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_16_0= ruleBooleanDef ) ) )?
            int alt69=2;
            int LA69_0 = input.LA(1);

            if ( (LA69_0==67) ) {
                alt69=1;
            }
            switch (alt69) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2236:6: otherlv_15= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_16_0= ruleBooleanDef ) )
                    {
                    otherlv_15=(Token)match(input,67,FOLLOW_67_in_ruleDecisionReviewDef4272); 

                        	newLeafNode(otherlv_15, grammarAccess.getDecisionReviewDefAccess().getAutoTransitionToDecisionKeyword_13_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2240:1: ( (lv_autoTransitionToDecision_16_0= ruleBooleanDef ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2241:1: (lv_autoTransitionToDecision_16_0= ruleBooleanDef )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2241:1: (lv_autoTransitionToDecision_16_0= ruleBooleanDef )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2242:3: lv_autoTransitionToDecision_16_0= ruleBooleanDef
                    {
                     
                    	        newCompositeNode(grammarAccess.getDecisionReviewDefAccess().getAutoTransitionToDecisionBooleanDefEnumRuleCall_13_1_0()); 
                    	    
                    pushFollow(FOLLOW_ruleBooleanDef_in_ruleDecisionReviewDef4293);
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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2258:4: ( (lv_options_17_0= ruleDecisionReviewOpt ) )+
            int cnt70=0;
            loop70:
            do {
                int alt70=2;
                int LA70_0 = input.LA(1);

                if ( (LA70_0==47) ) {
                    alt70=1;
                }


                switch (alt70) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2259:1: (lv_options_17_0= ruleDecisionReviewOpt )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2259:1: (lv_options_17_0= ruleDecisionReviewOpt )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2260:3: lv_options_17_0= ruleDecisionReviewOpt
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getDecisionReviewDefAccess().getOptionsDecisionReviewOptParserRuleCall_14_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleDecisionReviewOpt_in_ruleDecisionReviewDef4316);
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
            	    if ( cnt70 >= 1 ) break loop70;
                        EarlyExitException eee =
                            new EarlyExitException(70, input);
                        throw eee;
                }
                cnt70++;
            } while (true);

            otherlv_18=(Token)match(input,20,FOLLOW_20_in_ruleDecisionReviewDef4329); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2288:1: entryRuleDECISION_REVIEW_OPT_REF returns [String current=null] : iv_ruleDECISION_REVIEW_OPT_REF= ruleDECISION_REVIEW_OPT_REF EOF ;
    public final String entryRuleDECISION_REVIEW_OPT_REF() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleDECISION_REVIEW_OPT_REF = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2289:2: (iv_ruleDECISION_REVIEW_OPT_REF= ruleDECISION_REVIEW_OPT_REF EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2290:2: iv_ruleDECISION_REVIEW_OPT_REF= ruleDECISION_REVIEW_OPT_REF EOF
            {
             newCompositeNode(grammarAccess.getDECISION_REVIEW_OPT_REFRule()); 
            pushFollow(FOLLOW_ruleDECISION_REVIEW_OPT_REF_in_entryRuleDECISION_REVIEW_OPT_REF4366);
            iv_ruleDECISION_REVIEW_OPT_REF=ruleDECISION_REVIEW_OPT_REF();

            state._fsp--;

             current =iv_ruleDECISION_REVIEW_OPT_REF.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleDECISION_REVIEW_OPT_REF4377); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2297:1: ruleDECISION_REVIEW_OPT_REF returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleDECISION_REVIEW_OPT_REF() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2300:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2301:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleDECISION_REVIEW_OPT_REF4416); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2316:1: entryRuleDecisionReviewOpt returns [EObject current=null] : iv_ruleDecisionReviewOpt= ruleDecisionReviewOpt EOF ;
    public final EObject entryRuleDecisionReviewOpt() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDecisionReviewOpt = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2317:2: (iv_ruleDecisionReviewOpt= ruleDecisionReviewOpt EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2318:2: iv_ruleDecisionReviewOpt= ruleDecisionReviewOpt EOF
            {
             newCompositeNode(grammarAccess.getDecisionReviewOptRule()); 
            pushFollow(FOLLOW_ruleDecisionReviewOpt_in_entryRuleDecisionReviewOpt4460);
            iv_ruleDecisionReviewOpt=ruleDecisionReviewOpt();

            state._fsp--;

             current =iv_ruleDecisionReviewOpt; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleDecisionReviewOpt4470); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2325:1: ruleDecisionReviewOpt returns [EObject current=null] : (otherlv_0= 'option' ( (lv_name_1_0= ruleDECISION_REVIEW_OPT_REF ) ) ( (lv_followup_2_0= ruleFollowupRef ) )? ) ;
    public final EObject ruleDecisionReviewOpt() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        AntlrDatatypeRuleToken lv_name_1_0 = null;

        EObject lv_followup_2_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2328:28: ( (otherlv_0= 'option' ( (lv_name_1_0= ruleDECISION_REVIEW_OPT_REF ) ) ( (lv_followup_2_0= ruleFollowupRef ) )? ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2329:1: (otherlv_0= 'option' ( (lv_name_1_0= ruleDECISION_REVIEW_OPT_REF ) ) ( (lv_followup_2_0= ruleFollowupRef ) )? )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2329:1: (otherlv_0= 'option' ( (lv_name_1_0= ruleDECISION_REVIEW_OPT_REF ) ) ( (lv_followup_2_0= ruleFollowupRef ) )? )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2329:3: otherlv_0= 'option' ( (lv_name_1_0= ruleDECISION_REVIEW_OPT_REF ) ) ( (lv_followup_2_0= ruleFollowupRef ) )?
            {
            otherlv_0=(Token)match(input,47,FOLLOW_47_in_ruleDecisionReviewOpt4507); 

                	newLeafNode(otherlv_0, grammarAccess.getDecisionReviewOptAccess().getOptionKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2333:1: ( (lv_name_1_0= ruleDECISION_REVIEW_OPT_REF ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2334:1: (lv_name_1_0= ruleDECISION_REVIEW_OPT_REF )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2334:1: (lv_name_1_0= ruleDECISION_REVIEW_OPT_REF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2335:3: lv_name_1_0= ruleDECISION_REVIEW_OPT_REF
            {
             
            	        newCompositeNode(grammarAccess.getDecisionReviewOptAccess().getNameDECISION_REVIEW_OPT_REFParserRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_ruleDECISION_REVIEW_OPT_REF_in_ruleDecisionReviewOpt4528);
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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2351:2: ( (lv_followup_2_0= ruleFollowupRef ) )?
            int alt71=2;
            int LA71_0 = input.LA(1);

            if ( (LA71_0==71) ) {
                alt71=1;
            }
            switch (alt71) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2352:1: (lv_followup_2_0= ruleFollowupRef )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2352:1: (lv_followup_2_0= ruleFollowupRef )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2353:3: lv_followup_2_0= ruleFollowupRef
                    {
                     
                    	        newCompositeNode(grammarAccess.getDecisionReviewOptAccess().getFollowupFollowupRefParserRuleCall_2_0()); 
                    	    
                    pushFollow(FOLLOW_ruleFollowupRef_in_ruleDecisionReviewOpt4549);
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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2377:1: entryRulePeerReviewRef returns [EObject current=null] : iv_rulePeerReviewRef= rulePeerReviewRef EOF ;
    public final EObject entryRulePeerReviewRef() throws RecognitionException {
        EObject current = null;

        EObject iv_rulePeerReviewRef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2378:2: (iv_rulePeerReviewRef= rulePeerReviewRef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2379:2: iv_rulePeerReviewRef= rulePeerReviewRef EOF
            {
             newCompositeNode(grammarAccess.getPeerReviewRefRule()); 
            pushFollow(FOLLOW_rulePeerReviewRef_in_entryRulePeerReviewRef4586);
            iv_rulePeerReviewRef=rulePeerReviewRef();

            state._fsp--;

             current =iv_rulePeerReviewRef; 
            match(input,EOF,FOLLOW_EOF_in_entryRulePeerReviewRef4596); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2386:1: rulePeerReviewRef returns [EObject current=null] : (otherlv_0= 'peerReview' ( ( rulePEER_REVIEW_NAME_REFERENCE ) ) ) ;
    public final EObject rulePeerReviewRef() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2389:28: ( (otherlv_0= 'peerReview' ( ( rulePEER_REVIEW_NAME_REFERENCE ) ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2390:1: (otherlv_0= 'peerReview' ( ( rulePEER_REVIEW_NAME_REFERENCE ) ) )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2390:1: (otherlv_0= 'peerReview' ( ( rulePEER_REVIEW_NAME_REFERENCE ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2390:3: otherlv_0= 'peerReview' ( ( rulePEER_REVIEW_NAME_REFERENCE ) )
            {
            otherlv_0=(Token)match(input,68,FOLLOW_68_in_rulePeerReviewRef4633); 

                	newLeafNode(otherlv_0, grammarAccess.getPeerReviewRefAccess().getPeerReviewKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2394:1: ( ( rulePEER_REVIEW_NAME_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2395:1: ( rulePEER_REVIEW_NAME_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2395:1: ( rulePEER_REVIEW_NAME_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2396:3: rulePEER_REVIEW_NAME_REFERENCE
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getPeerReviewRefRule());
            	        }
                    
             
            	        newCompositeNode(grammarAccess.getPeerReviewRefAccess().getPeerReviewPeerReviewDefCrossReference_1_0()); 
            	    
            pushFollow(FOLLOW_rulePEER_REVIEW_NAME_REFERENCE_in_rulePeerReviewRef4656);
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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2417:1: entryRulePeerReviewDef returns [EObject current=null] : iv_rulePeerReviewDef= rulePeerReviewDef EOF ;
    public final EObject entryRulePeerReviewDef() throws RecognitionException {
        EObject current = null;

        EObject iv_rulePeerReviewDef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2418:2: (iv_rulePeerReviewDef= rulePeerReviewDef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2419:2: iv_rulePeerReviewDef= rulePeerReviewDef EOF
            {
             newCompositeNode(grammarAccess.getPeerReviewDefRule()); 
            pushFollow(FOLLOW_rulePeerReviewDef_in_entryRulePeerReviewDef4692);
            iv_rulePeerReviewDef=rulePeerReviewDef();

            state._fsp--;

             current =iv_rulePeerReviewDef; 
            match(input,EOF,FOLLOW_EOF_in_entryRulePeerReviewDef4702); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2426:1: rulePeerReviewDef returns [EObject current=null] : (otherlv_0= 'peerReviewDefinition' ( (lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) )? otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'location' ( (lv_location_8_0= RULE_STRING ) ) )? (otherlv_9= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_11= 'blockingType' ( (lv_blockingType_12_0= ruleReviewBlockingType ) ) otherlv_13= 'onEvent' ( (lv_stateEvent_14_0= ruleWorkflowEventType ) ) (otherlv_15= 'assignee' ( (lv_assigneeRefs_16_0= ruleUserRef ) ) )* otherlv_17= '}' ) ;
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
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2429:28: ( (otherlv_0= 'peerReviewDefinition' ( (lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) )? otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'location' ( (lv_location_8_0= RULE_STRING ) ) )? (otherlv_9= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_11= 'blockingType' ( (lv_blockingType_12_0= ruleReviewBlockingType ) ) otherlv_13= 'onEvent' ( (lv_stateEvent_14_0= ruleWorkflowEventType ) ) (otherlv_15= 'assignee' ( (lv_assigneeRefs_16_0= ruleUserRef ) ) )* otherlv_17= '}' ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2430:1: (otherlv_0= 'peerReviewDefinition' ( (lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) )? otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'location' ( (lv_location_8_0= RULE_STRING ) ) )? (otherlv_9= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_11= 'blockingType' ( (lv_blockingType_12_0= ruleReviewBlockingType ) ) otherlv_13= 'onEvent' ( (lv_stateEvent_14_0= ruleWorkflowEventType ) ) (otherlv_15= 'assignee' ( (lv_assigneeRefs_16_0= ruleUserRef ) ) )* otherlv_17= '}' )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2430:1: (otherlv_0= 'peerReviewDefinition' ( (lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) )? otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'location' ( (lv_location_8_0= RULE_STRING ) ) )? (otherlv_9= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_11= 'blockingType' ( (lv_blockingType_12_0= ruleReviewBlockingType ) ) otherlv_13= 'onEvent' ( (lv_stateEvent_14_0= ruleWorkflowEventType ) ) (otherlv_15= 'assignee' ( (lv_assigneeRefs_16_0= ruleUserRef ) ) )* otherlv_17= '}' )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2430:3: otherlv_0= 'peerReviewDefinition' ( (lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) )? otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'location' ( (lv_location_8_0= RULE_STRING ) ) )? (otherlv_9= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_11= 'blockingType' ( (lv_blockingType_12_0= ruleReviewBlockingType ) ) otherlv_13= 'onEvent' ( (lv_stateEvent_14_0= ruleWorkflowEventType ) ) (otherlv_15= 'assignee' ( (lv_assigneeRefs_16_0= ruleUserRef ) ) )* otherlv_17= '}'
            {
            otherlv_0=(Token)match(input,69,FOLLOW_69_in_rulePeerReviewDef4739); 

                	newLeafNode(otherlv_0, grammarAccess.getPeerReviewDefAccess().getPeerReviewDefinitionKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2434:1: ( (lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2435:1: (lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2435:1: (lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2436:3: lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE
            {
             
            	        newCompositeNode(grammarAccess.getPeerReviewDefAccess().getNamePEER_REVIEW_NAME_REFERENCEParserRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_rulePEER_REVIEW_NAME_REFERENCE_in_rulePeerReviewDef4760);
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

            otherlv_2=(Token)match(input,15,FOLLOW_15_in_rulePeerReviewDef4772); 

                	newLeafNode(otherlv_2, grammarAccess.getPeerReviewDefAccess().getLeftCurlyBracketKeyword_2());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2456:1: (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) )?
            int alt72=2;
            int LA72_0 = input.LA(1);

            if ( (LA72_0==62) ) {
                alt72=1;
            }
            switch (alt72) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2456:3: otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) )
                    {
                    otherlv_3=(Token)match(input,62,FOLLOW_62_in_rulePeerReviewDef4785); 

                        	newLeafNode(otherlv_3, grammarAccess.getPeerReviewDefAccess().getTitleKeyword_3_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2460:1: ( (lv_title_4_0= RULE_STRING ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2461:1: (lv_title_4_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2461:1: (lv_title_4_0= RULE_STRING )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2462:3: lv_title_4_0= RULE_STRING
                    {
                    lv_title_4_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rulePeerReviewDef4802); 

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

            otherlv_5=(Token)match(input,43,FOLLOW_43_in_rulePeerReviewDef4821); 

                	newLeafNode(otherlv_5, grammarAccess.getPeerReviewDefAccess().getDescriptionKeyword_4());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2482:1: ( (lv_description_6_0= RULE_STRING ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2483:1: (lv_description_6_0= RULE_STRING )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2483:1: (lv_description_6_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2484:3: lv_description_6_0= RULE_STRING
            {
            lv_description_6_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rulePeerReviewDef4838); 

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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2500:2: (otherlv_7= 'location' ( (lv_location_8_0= RULE_STRING ) ) )?
            int alt73=2;
            int LA73_0 = input.LA(1);

            if ( (LA73_0==70) ) {
                alt73=1;
            }
            switch (alt73) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2500:4: otherlv_7= 'location' ( (lv_location_8_0= RULE_STRING ) )
                    {
                    otherlv_7=(Token)match(input,70,FOLLOW_70_in_rulePeerReviewDef4856); 

                        	newLeafNode(otherlv_7, grammarAccess.getPeerReviewDefAccess().getLocationKeyword_6_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2504:1: ( (lv_location_8_0= RULE_STRING ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2505:1: (lv_location_8_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2505:1: (lv_location_8_0= RULE_STRING )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2506:3: lv_location_8_0= RULE_STRING
                    {
                    lv_location_8_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rulePeerReviewDef4873); 

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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2522:4: (otherlv_9= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )?
            int alt74=2;
            int LA74_0 = input.LA(1);

            if ( (LA74_0==63) ) {
                alt74=1;
            }
            switch (alt74) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2522:6: otherlv_9= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) )
                    {
                    otherlv_9=(Token)match(input,63,FOLLOW_63_in_rulePeerReviewDef4893); 

                        	newLeafNode(otherlv_9, grammarAccess.getPeerReviewDefAccess().getRelatedToStateKeyword_7_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2526:1: ( ( ruleSTATE_NAME_REFERENCE ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2527:1: ( ruleSTATE_NAME_REFERENCE )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2527:1: ( ruleSTATE_NAME_REFERENCE )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2528:3: ruleSTATE_NAME_REFERENCE
                    {

                    			if (current==null) {
                    	            current = createModelElement(grammarAccess.getPeerReviewDefRule());
                    	        }
                            
                     
                    	        newCompositeNode(grammarAccess.getPeerReviewDefAccess().getRelatedToStateStateDefCrossReference_7_1_0()); 
                    	    
                    pushFollow(FOLLOW_ruleSTATE_NAME_REFERENCE_in_rulePeerReviewDef4916);
                    ruleSTATE_NAME_REFERENCE();

                    state._fsp--;

                     
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }


                    }
                    break;

            }

            otherlv_11=(Token)match(input,64,FOLLOW_64_in_rulePeerReviewDef4930); 

                	newLeafNode(otherlv_11, grammarAccess.getPeerReviewDefAccess().getBlockingTypeKeyword_8());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2545:1: ( (lv_blockingType_12_0= ruleReviewBlockingType ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2546:1: (lv_blockingType_12_0= ruleReviewBlockingType )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2546:1: (lv_blockingType_12_0= ruleReviewBlockingType )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2547:3: lv_blockingType_12_0= ruleReviewBlockingType
            {
             
            	        newCompositeNode(grammarAccess.getPeerReviewDefAccess().getBlockingTypeReviewBlockingTypeEnumRuleCall_9_0()); 
            	    
            pushFollow(FOLLOW_ruleReviewBlockingType_in_rulePeerReviewDef4951);
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

            otherlv_13=(Token)match(input,65,FOLLOW_65_in_rulePeerReviewDef4963); 

                	newLeafNode(otherlv_13, grammarAccess.getPeerReviewDefAccess().getOnEventKeyword_10());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2567:1: ( (lv_stateEvent_14_0= ruleWorkflowEventType ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2568:1: (lv_stateEvent_14_0= ruleWorkflowEventType )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2568:1: (lv_stateEvent_14_0= ruleWorkflowEventType )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2569:3: lv_stateEvent_14_0= ruleWorkflowEventType
            {
             
            	        newCompositeNode(grammarAccess.getPeerReviewDefAccess().getStateEventWorkflowEventTypeEnumRuleCall_11_0()); 
            	    
            pushFollow(FOLLOW_ruleWorkflowEventType_in_rulePeerReviewDef4984);
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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2585:2: (otherlv_15= 'assignee' ( (lv_assigneeRefs_16_0= ruleUserRef ) ) )*
            loop75:
            do {
                int alt75=2;
                int LA75_0 = input.LA(1);

                if ( (LA75_0==66) ) {
                    alt75=1;
                }


                switch (alt75) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2585:4: otherlv_15= 'assignee' ( (lv_assigneeRefs_16_0= ruleUserRef ) )
            	    {
            	    otherlv_15=(Token)match(input,66,FOLLOW_66_in_rulePeerReviewDef4997); 

            	        	newLeafNode(otherlv_15, grammarAccess.getPeerReviewDefAccess().getAssigneeKeyword_12_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2589:1: ( (lv_assigneeRefs_16_0= ruleUserRef ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2590:1: (lv_assigneeRefs_16_0= ruleUserRef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2590:1: (lv_assigneeRefs_16_0= ruleUserRef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2591:3: lv_assigneeRefs_16_0= ruleUserRef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getPeerReviewDefAccess().getAssigneeRefsUserRefParserRuleCall_12_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleUserRef_in_rulePeerReviewDef5018);
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
            	    break loop75;
                }
            } while (true);

            otherlv_17=(Token)match(input,20,FOLLOW_20_in_rulePeerReviewDef5032); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2619:1: entryRuleFollowupRef returns [EObject current=null] : iv_ruleFollowupRef= ruleFollowupRef EOF ;
    public final EObject entryRuleFollowupRef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleFollowupRef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2620:2: (iv_ruleFollowupRef= ruleFollowupRef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2621:2: iv_ruleFollowupRef= ruleFollowupRef EOF
            {
             newCompositeNode(grammarAccess.getFollowupRefRule()); 
            pushFollow(FOLLOW_ruleFollowupRef_in_entryRuleFollowupRef5068);
            iv_ruleFollowupRef=ruleFollowupRef();

            state._fsp--;

             current =iv_ruleFollowupRef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleFollowupRef5078); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2628:1: ruleFollowupRef returns [EObject current=null] : (otherlv_0= 'followup by' (otherlv_1= 'assignee' ( (lv_assigneeRefs_2_0= ruleUserRef ) ) )+ ) ;
    public final EObject ruleFollowupRef() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;
        EObject lv_assigneeRefs_2_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2631:28: ( (otherlv_0= 'followup by' (otherlv_1= 'assignee' ( (lv_assigneeRefs_2_0= ruleUserRef ) ) )+ ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2632:1: (otherlv_0= 'followup by' (otherlv_1= 'assignee' ( (lv_assigneeRefs_2_0= ruleUserRef ) ) )+ )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2632:1: (otherlv_0= 'followup by' (otherlv_1= 'assignee' ( (lv_assigneeRefs_2_0= ruleUserRef ) ) )+ )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2632:3: otherlv_0= 'followup by' (otherlv_1= 'assignee' ( (lv_assigneeRefs_2_0= ruleUserRef ) ) )+
            {
            otherlv_0=(Token)match(input,71,FOLLOW_71_in_ruleFollowupRef5115); 

                	newLeafNode(otherlv_0, grammarAccess.getFollowupRefAccess().getFollowupByKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2636:1: (otherlv_1= 'assignee' ( (lv_assigneeRefs_2_0= ruleUserRef ) ) )+
            int cnt76=0;
            loop76:
            do {
                int alt76=2;
                int LA76_0 = input.LA(1);

                if ( (LA76_0==66) ) {
                    alt76=1;
                }


                switch (alt76) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2636:3: otherlv_1= 'assignee' ( (lv_assigneeRefs_2_0= ruleUserRef ) )
            	    {
            	    otherlv_1=(Token)match(input,66,FOLLOW_66_in_ruleFollowupRef5128); 

            	        	newLeafNode(otherlv_1, grammarAccess.getFollowupRefAccess().getAssigneeKeyword_1_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2640:1: ( (lv_assigneeRefs_2_0= ruleUserRef ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2641:1: (lv_assigneeRefs_2_0= ruleUserRef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2641:1: (lv_assigneeRefs_2_0= ruleUserRef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2642:3: lv_assigneeRefs_2_0= ruleUserRef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getFollowupRefAccess().getAssigneeRefsUserRefParserRuleCall_1_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleUserRef_in_ruleFollowupRef5149);
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
            	    if ( cnt76 >= 1 ) break loop76;
                        EarlyExitException eee =
                            new EarlyExitException(76, input);
                        throw eee;
                }
                cnt76++;
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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2666:1: entryRuleUserRef returns [EObject current=null] : iv_ruleUserRef= ruleUserRef EOF ;
    public final EObject entryRuleUserRef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleUserRef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2667:2: (iv_ruleUserRef= ruleUserRef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2668:2: iv_ruleUserRef= ruleUserRef EOF
            {
             newCompositeNode(grammarAccess.getUserRefRule()); 
            pushFollow(FOLLOW_ruleUserRef_in_entryRuleUserRef5187);
            iv_ruleUserRef=ruleUserRef();

            state._fsp--;

             current =iv_ruleUserRef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleUserRef5197); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2675:1: ruleUserRef returns [EObject current=null] : (this_UserByUserId_0= ruleUserByUserId | this_UserByName_1= ruleUserByName ) ;
    public final EObject ruleUserRef() throws RecognitionException {
        EObject current = null;

        EObject this_UserByUserId_0 = null;

        EObject this_UserByName_1 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2678:28: ( (this_UserByUserId_0= ruleUserByUserId | this_UserByName_1= ruleUserByName ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2679:1: (this_UserByUserId_0= ruleUserByUserId | this_UserByName_1= ruleUserByName )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2679:1: (this_UserByUserId_0= ruleUserByUserId | this_UserByName_1= ruleUserByName )
            int alt77=2;
            int LA77_0 = input.LA(1);

            if ( (LA77_0==17) ) {
                alt77=1;
            }
            else if ( (LA77_0==72) ) {
                alt77=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 77, 0, input);

                throw nvae;
            }
            switch (alt77) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2680:5: this_UserByUserId_0= ruleUserByUserId
                    {
                     
                            newCompositeNode(grammarAccess.getUserRefAccess().getUserByUserIdParserRuleCall_0()); 
                        
                    pushFollow(FOLLOW_ruleUserByUserId_in_ruleUserRef5244);
                    this_UserByUserId_0=ruleUserByUserId();

                    state._fsp--;

                     
                            current = this_UserByUserId_0; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2690:5: this_UserByName_1= ruleUserByName
                    {
                     
                            newCompositeNode(grammarAccess.getUserRefAccess().getUserByNameParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_ruleUserByName_in_ruleUserRef5271);
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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2706:1: entryRuleUserByUserId returns [EObject current=null] : iv_ruleUserByUserId= ruleUserByUserId EOF ;
    public final EObject entryRuleUserByUserId() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleUserByUserId = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2707:2: (iv_ruleUserByUserId= ruleUserByUserId EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2708:2: iv_ruleUserByUserId= ruleUserByUserId EOF
            {
             newCompositeNode(grammarAccess.getUserByUserIdRule()); 
            pushFollow(FOLLOW_ruleUserByUserId_in_entryRuleUserByUserId5306);
            iv_ruleUserByUserId=ruleUserByUserId();

            state._fsp--;

             current =iv_ruleUserByUserId; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleUserByUserId5316); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2715:1: ruleUserByUserId returns [EObject current=null] : (otherlv_0= 'userId' ( (lv_userId_1_0= RULE_STRING ) ) ) ;
    public final EObject ruleUserByUserId() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_userId_1_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2718:28: ( (otherlv_0= 'userId' ( (lv_userId_1_0= RULE_STRING ) ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2719:1: (otherlv_0= 'userId' ( (lv_userId_1_0= RULE_STRING ) ) )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2719:1: (otherlv_0= 'userId' ( (lv_userId_1_0= RULE_STRING ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2719:3: otherlv_0= 'userId' ( (lv_userId_1_0= RULE_STRING ) )
            {
            otherlv_0=(Token)match(input,17,FOLLOW_17_in_ruleUserByUserId5353); 

                	newLeafNode(otherlv_0, grammarAccess.getUserByUserIdAccess().getUserIdKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2723:1: ( (lv_userId_1_0= RULE_STRING ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2724:1: (lv_userId_1_0= RULE_STRING )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2724:1: (lv_userId_1_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2725:3: lv_userId_1_0= RULE_STRING
            {
            lv_userId_1_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleUserByUserId5370); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2749:1: entryRuleUserByName returns [EObject current=null] : iv_ruleUserByName= ruleUserByName EOF ;
    public final EObject entryRuleUserByName() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleUserByName = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2750:2: (iv_ruleUserByName= ruleUserByName EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2751:2: iv_ruleUserByName= ruleUserByName EOF
            {
             newCompositeNode(grammarAccess.getUserByNameRule()); 
            pushFollow(FOLLOW_ruleUserByName_in_entryRuleUserByName5411);
            iv_ruleUserByName=ruleUserByName();

            state._fsp--;

             current =iv_ruleUserByName; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleUserByName5421); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2758:1: ruleUserByName returns [EObject current=null] : (otherlv_0= 'named' ( (lv_userName_1_0= RULE_STRING ) ) ) ;
    public final EObject ruleUserByName() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_userName_1_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2761:28: ( (otherlv_0= 'named' ( (lv_userName_1_0= RULE_STRING ) ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2762:1: (otherlv_0= 'named' ( (lv_userName_1_0= RULE_STRING ) ) )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2762:1: (otherlv_0= 'named' ( (lv_userName_1_0= RULE_STRING ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2762:3: otherlv_0= 'named' ( (lv_userName_1_0= RULE_STRING ) )
            {
            otherlv_0=(Token)match(input,72,FOLLOW_72_in_ruleUserByName5458); 

                	newLeafNode(otherlv_0, grammarAccess.getUserByNameAccess().getNamedKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2766:1: ( (lv_userName_1_0= RULE_STRING ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2767:1: (lv_userName_1_0= RULE_STRING )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2767:1: (lv_userName_1_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2768:3: lv_userName_1_0= RULE_STRING
            {
            lv_userName_1_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleUserByName5475); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2792:1: entryRuleDECISION_REVIEW_NAME_REFERENCE returns [String current=null] : iv_ruleDECISION_REVIEW_NAME_REFERENCE= ruleDECISION_REVIEW_NAME_REFERENCE EOF ;
    public final String entryRuleDECISION_REVIEW_NAME_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleDECISION_REVIEW_NAME_REFERENCE = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2793:2: (iv_ruleDECISION_REVIEW_NAME_REFERENCE= ruleDECISION_REVIEW_NAME_REFERENCE EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2794:2: iv_ruleDECISION_REVIEW_NAME_REFERENCE= ruleDECISION_REVIEW_NAME_REFERENCE EOF
            {
             newCompositeNode(grammarAccess.getDECISION_REVIEW_NAME_REFERENCERule()); 
            pushFollow(FOLLOW_ruleDECISION_REVIEW_NAME_REFERENCE_in_entryRuleDECISION_REVIEW_NAME_REFERENCE5517);
            iv_ruleDECISION_REVIEW_NAME_REFERENCE=ruleDECISION_REVIEW_NAME_REFERENCE();

            state._fsp--;

             current =iv_ruleDECISION_REVIEW_NAME_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleDECISION_REVIEW_NAME_REFERENCE5528); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2801:1: ruleDECISION_REVIEW_NAME_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleDECISION_REVIEW_NAME_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2804:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2805:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleDECISION_REVIEW_NAME_REFERENCE5567); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2820:1: entryRulePEER_REVIEW_NAME_REFERENCE returns [String current=null] : iv_rulePEER_REVIEW_NAME_REFERENCE= rulePEER_REVIEW_NAME_REFERENCE EOF ;
    public final String entryRulePEER_REVIEW_NAME_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_rulePEER_REVIEW_NAME_REFERENCE = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2821:2: (iv_rulePEER_REVIEW_NAME_REFERENCE= rulePEER_REVIEW_NAME_REFERENCE EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2822:2: iv_rulePEER_REVIEW_NAME_REFERENCE= rulePEER_REVIEW_NAME_REFERENCE EOF
            {
             newCompositeNode(grammarAccess.getPEER_REVIEW_NAME_REFERENCERule()); 
            pushFollow(FOLLOW_rulePEER_REVIEW_NAME_REFERENCE_in_entryRulePEER_REVIEW_NAME_REFERENCE5612);
            iv_rulePEER_REVIEW_NAME_REFERENCE=rulePEER_REVIEW_NAME_REFERENCE();

            state._fsp--;

             current =iv_rulePEER_REVIEW_NAME_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRulePEER_REVIEW_NAME_REFERENCE5623); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2829:1: rulePEER_REVIEW_NAME_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken rulePEER_REVIEW_NAME_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2832:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2833:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rulePEER_REVIEW_NAME_REFERENCE5662); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2848:1: entryRuleSTATE_NAME_REFERENCE returns [String current=null] : iv_ruleSTATE_NAME_REFERENCE= ruleSTATE_NAME_REFERENCE EOF ;
    public final String entryRuleSTATE_NAME_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleSTATE_NAME_REFERENCE = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2849:2: (iv_ruleSTATE_NAME_REFERENCE= ruleSTATE_NAME_REFERENCE EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2850:2: iv_ruleSTATE_NAME_REFERENCE= ruleSTATE_NAME_REFERENCE EOF
            {
             newCompositeNode(grammarAccess.getSTATE_NAME_REFERENCERule()); 
            pushFollow(FOLLOW_ruleSTATE_NAME_REFERENCE_in_entryRuleSTATE_NAME_REFERENCE5707);
            iv_ruleSTATE_NAME_REFERENCE=ruleSTATE_NAME_REFERENCE();

            state._fsp--;

             current =iv_ruleSTATE_NAME_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleSTATE_NAME_REFERENCE5718); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2857:1: ruleSTATE_NAME_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleSTATE_NAME_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2860:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2861:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleSTATE_NAME_REFERENCE5757); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2876:1: entryRuleWIDGET_NAME_REFERENCE returns [String current=null] : iv_ruleWIDGET_NAME_REFERENCE= ruleWIDGET_NAME_REFERENCE EOF ;
    public final String entryRuleWIDGET_NAME_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleWIDGET_NAME_REFERENCE = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2877:2: (iv_ruleWIDGET_NAME_REFERENCE= ruleWIDGET_NAME_REFERENCE EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2878:2: iv_ruleWIDGET_NAME_REFERENCE= ruleWIDGET_NAME_REFERENCE EOF
            {
             newCompositeNode(grammarAccess.getWIDGET_NAME_REFERENCERule()); 
            pushFollow(FOLLOW_ruleWIDGET_NAME_REFERENCE_in_entryRuleWIDGET_NAME_REFERENCE5802);
            iv_ruleWIDGET_NAME_REFERENCE=ruleWIDGET_NAME_REFERENCE();

            state._fsp--;

             current =iv_ruleWIDGET_NAME_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleWIDGET_NAME_REFERENCE5813); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2885:1: ruleWIDGET_NAME_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleWIDGET_NAME_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2888:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2889:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleWIDGET_NAME_REFERENCE5852); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2904:1: entryRuleWORK_DEFINITION_NAME_REFERENCE returns [String current=null] : iv_ruleWORK_DEFINITION_NAME_REFERENCE= ruleWORK_DEFINITION_NAME_REFERENCE EOF ;
    public final String entryRuleWORK_DEFINITION_NAME_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleWORK_DEFINITION_NAME_REFERENCE = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2905:2: (iv_ruleWORK_DEFINITION_NAME_REFERENCE= ruleWORK_DEFINITION_NAME_REFERENCE EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2906:2: iv_ruleWORK_DEFINITION_NAME_REFERENCE= ruleWORK_DEFINITION_NAME_REFERENCE EOF
            {
             newCompositeNode(grammarAccess.getWORK_DEFINITION_NAME_REFERENCERule()); 
            pushFollow(FOLLOW_ruleWORK_DEFINITION_NAME_REFERENCE_in_entryRuleWORK_DEFINITION_NAME_REFERENCE5897);
            iv_ruleWORK_DEFINITION_NAME_REFERENCE=ruleWORK_DEFINITION_NAME_REFERENCE();

            state._fsp--;

             current =iv_ruleWORK_DEFINITION_NAME_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleWORK_DEFINITION_NAME_REFERENCE5908); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2913:1: ruleWORK_DEFINITION_NAME_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleWORK_DEFINITION_NAME_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2916:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2917:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleWORK_DEFINITION_NAME_REFERENCE5947); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2932:1: entryRuleToState returns [EObject current=null] : iv_ruleToState= ruleToState EOF ;
    public final EObject entryRuleToState() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleToState = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2933:2: (iv_ruleToState= ruleToState EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2934:2: iv_ruleToState= ruleToState EOF
            {
             newCompositeNode(grammarAccess.getToStateRule()); 
            pushFollow(FOLLOW_ruleToState_in_entryRuleToState5991);
            iv_ruleToState=ruleToState();

            state._fsp--;

             current =iv_ruleToState; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleToState6001); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2941:1: ruleToState returns [EObject current=null] : (otherlv_0= 'to' ( ( ruleSTATE_NAME_REFERENCE ) ) ( (lv_options_2_0= ruleTransitionOption ) )* ) ;
    public final EObject ruleToState() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        AntlrDatatypeRuleToken lv_options_2_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2944:28: ( (otherlv_0= 'to' ( ( ruleSTATE_NAME_REFERENCE ) ) ( (lv_options_2_0= ruleTransitionOption ) )* ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2945:1: (otherlv_0= 'to' ( ( ruleSTATE_NAME_REFERENCE ) ) ( (lv_options_2_0= ruleTransitionOption ) )* )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2945:1: (otherlv_0= 'to' ( ( ruleSTATE_NAME_REFERENCE ) ) ( (lv_options_2_0= ruleTransitionOption ) )* )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2945:3: otherlv_0= 'to' ( ( ruleSTATE_NAME_REFERENCE ) ) ( (lv_options_2_0= ruleTransitionOption ) )*
            {
            otherlv_0=(Token)match(input,73,FOLLOW_73_in_ruleToState6038); 

                	newLeafNode(otherlv_0, grammarAccess.getToStateAccess().getToKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2949:1: ( ( ruleSTATE_NAME_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2950:1: ( ruleSTATE_NAME_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2950:1: ( ruleSTATE_NAME_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2951:3: ruleSTATE_NAME_REFERENCE
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getToStateRule());
            	        }
                    
             
            	        newCompositeNode(grammarAccess.getToStateAccess().getStateStateDefCrossReference_1_0()); 
            	    
            pushFollow(FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleToState6061);
            ruleSTATE_NAME_REFERENCE();

            state._fsp--;

             
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2964:2: ( (lv_options_2_0= ruleTransitionOption ) )*
            loop78:
            do {
                int alt78=2;
                int LA78_0 = input.LA(1);

                if ( (LA78_0==RULE_STRING||(LA78_0>=80 && LA78_0<=81)) ) {
                    alt78=1;
                }


                switch (alt78) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2965:1: (lv_options_2_0= ruleTransitionOption )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2965:1: (lv_options_2_0= ruleTransitionOption )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2966:3: lv_options_2_0= ruleTransitionOption
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getToStateAccess().getOptionsTransitionOptionParserRuleCall_2_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleTransitionOption_in_ruleToState6082);
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
            	    break loop78;
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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2990:1: entryRuleLayoutType returns [EObject current=null] : iv_ruleLayoutType= ruleLayoutType EOF ;
    public final EObject entryRuleLayoutType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleLayoutType = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2991:2: (iv_ruleLayoutType= ruleLayoutType EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2992:2: iv_ruleLayoutType= ruleLayoutType EOF
            {
             newCompositeNode(grammarAccess.getLayoutTypeRule()); 
            pushFollow(FOLLOW_ruleLayoutType_in_entryRuleLayoutType6119);
            iv_ruleLayoutType=ruleLayoutType();

            state._fsp--;

             current =iv_ruleLayoutType; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleLayoutType6129); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2999:1: ruleLayoutType returns [EObject current=null] : (this_LayoutDef_0= ruleLayoutDef | this_LayoutCopy_1= ruleLayoutCopy ) ;
    public final EObject ruleLayoutType() throws RecognitionException {
        EObject current = null;

        EObject this_LayoutDef_0 = null;

        EObject this_LayoutCopy_1 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3002:28: ( (this_LayoutDef_0= ruleLayoutDef | this_LayoutCopy_1= ruleLayoutCopy ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3003:1: (this_LayoutDef_0= ruleLayoutDef | this_LayoutCopy_1= ruleLayoutCopy )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3003:1: (this_LayoutDef_0= ruleLayoutDef | this_LayoutCopy_1= ruleLayoutCopy )
            int alt79=2;
            int LA79_0 = input.LA(1);

            if ( (LA79_0==74) ) {
                alt79=1;
            }
            else if ( (LA79_0==75) ) {
                alt79=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 79, 0, input);

                throw nvae;
            }
            switch (alt79) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3004:5: this_LayoutDef_0= ruleLayoutDef
                    {
                     
                            newCompositeNode(grammarAccess.getLayoutTypeAccess().getLayoutDefParserRuleCall_0()); 
                        
                    pushFollow(FOLLOW_ruleLayoutDef_in_ruleLayoutType6176);
                    this_LayoutDef_0=ruleLayoutDef();

                    state._fsp--;

                     
                            current = this_LayoutDef_0; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3014:5: this_LayoutCopy_1= ruleLayoutCopy
                    {
                     
                            newCompositeNode(grammarAccess.getLayoutTypeAccess().getLayoutCopyParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_ruleLayoutCopy_in_ruleLayoutType6203);
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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3030:1: entryRuleLayoutDef returns [EObject current=null] : iv_ruleLayoutDef= ruleLayoutDef EOF ;
    public final EObject entryRuleLayoutDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleLayoutDef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3031:2: (iv_ruleLayoutDef= ruleLayoutDef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3032:2: iv_ruleLayoutDef= ruleLayoutDef EOF
            {
             newCompositeNode(grammarAccess.getLayoutDefRule()); 
            pushFollow(FOLLOW_ruleLayoutDef_in_entryRuleLayoutDef6238);
            iv_ruleLayoutDef=ruleLayoutDef();

            state._fsp--;

             current =iv_ruleLayoutDef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleLayoutDef6248); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3039:1: ruleLayoutDef returns [EObject current=null] : (otherlv_0= 'layout' otherlv_1= '{' ( (lv_layoutItems_2_0= ruleLayoutItem ) )+ otherlv_3= '}' ) ;
    public final EObject ruleLayoutDef() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;
        Token otherlv_3=null;
        EObject lv_layoutItems_2_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3042:28: ( (otherlv_0= 'layout' otherlv_1= '{' ( (lv_layoutItems_2_0= ruleLayoutItem ) )+ otherlv_3= '}' ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3043:1: (otherlv_0= 'layout' otherlv_1= '{' ( (lv_layoutItems_2_0= ruleLayoutItem ) )+ otherlv_3= '}' )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3043:1: (otherlv_0= 'layout' otherlv_1= '{' ( (lv_layoutItems_2_0= ruleLayoutItem ) )+ otherlv_3= '}' )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3043:3: otherlv_0= 'layout' otherlv_1= '{' ( (lv_layoutItems_2_0= ruleLayoutItem ) )+ otherlv_3= '}'
            {
            otherlv_0=(Token)match(input,74,FOLLOW_74_in_ruleLayoutDef6285); 

                	newLeafNode(otherlv_0, grammarAccess.getLayoutDefAccess().getLayoutKeyword_0());
                
            otherlv_1=(Token)match(input,15,FOLLOW_15_in_ruleLayoutDef6297); 

                	newLeafNode(otherlv_1, grammarAccess.getLayoutDefAccess().getLeftCurlyBracketKeyword_1());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3051:1: ( (lv_layoutItems_2_0= ruleLayoutItem ) )+
            int cnt80=0;
            loop80:
            do {
                int alt80=2;
                int LA80_0 = input.LA(1);

                if ( ((LA80_0>=50 && LA80_0<=51)||LA80_0==76) ) {
                    alt80=1;
                }


                switch (alt80) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3052:1: (lv_layoutItems_2_0= ruleLayoutItem )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3052:1: (lv_layoutItems_2_0= ruleLayoutItem )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3053:3: lv_layoutItems_2_0= ruleLayoutItem
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getLayoutDefAccess().getLayoutItemsLayoutItemParserRuleCall_2_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleLayoutItem_in_ruleLayoutDef6318);
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
            	    if ( cnt80 >= 1 ) break loop80;
                        EarlyExitException eee =
                            new EarlyExitException(80, input);
                        throw eee;
                }
                cnt80++;
            } while (true);

            otherlv_3=(Token)match(input,20,FOLLOW_20_in_ruleLayoutDef6331); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3081:1: entryRuleLayoutCopy returns [EObject current=null] : iv_ruleLayoutCopy= ruleLayoutCopy EOF ;
    public final EObject entryRuleLayoutCopy() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleLayoutCopy = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3082:2: (iv_ruleLayoutCopy= ruleLayoutCopy EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3083:2: iv_ruleLayoutCopy= ruleLayoutCopy EOF
            {
             newCompositeNode(grammarAccess.getLayoutCopyRule()); 
            pushFollow(FOLLOW_ruleLayoutCopy_in_entryRuleLayoutCopy6367);
            iv_ruleLayoutCopy=ruleLayoutCopy();

            state._fsp--;

             current =iv_ruleLayoutCopy; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleLayoutCopy6377); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3090:1: ruleLayoutCopy returns [EObject current=null] : (otherlv_0= 'layoutCopyFrom' ( ( ruleSTATE_NAME_REFERENCE ) ) ) ;
    public final EObject ruleLayoutCopy() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3093:28: ( (otherlv_0= 'layoutCopyFrom' ( ( ruleSTATE_NAME_REFERENCE ) ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3094:1: (otherlv_0= 'layoutCopyFrom' ( ( ruleSTATE_NAME_REFERENCE ) ) )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3094:1: (otherlv_0= 'layoutCopyFrom' ( ( ruleSTATE_NAME_REFERENCE ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3094:3: otherlv_0= 'layoutCopyFrom' ( ( ruleSTATE_NAME_REFERENCE ) )
            {
            otherlv_0=(Token)match(input,75,FOLLOW_75_in_ruleLayoutCopy6414); 

                	newLeafNode(otherlv_0, grammarAccess.getLayoutCopyAccess().getLayoutCopyFromKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3098:1: ( ( ruleSTATE_NAME_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3099:1: ( ruleSTATE_NAME_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3099:1: ( ruleSTATE_NAME_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3100:3: ruleSTATE_NAME_REFERENCE
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getLayoutCopyRule());
            	        }
                    
             
            	        newCompositeNode(grammarAccess.getLayoutCopyAccess().getStateStateDefCrossReference_1_0()); 
            	    
            pushFollow(FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleLayoutCopy6437);
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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3121:1: entryRuleLayoutItem returns [EObject current=null] : iv_ruleLayoutItem= ruleLayoutItem EOF ;
    public final EObject entryRuleLayoutItem() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleLayoutItem = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3122:2: (iv_ruleLayoutItem= ruleLayoutItem EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3123:2: iv_ruleLayoutItem= ruleLayoutItem EOF
            {
             newCompositeNode(grammarAccess.getLayoutItemRule()); 
            pushFollow(FOLLOW_ruleLayoutItem_in_entryRuleLayoutItem6473);
            iv_ruleLayoutItem=ruleLayoutItem();

            state._fsp--;

             current =iv_ruleLayoutItem; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleLayoutItem6483); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3130:1: ruleLayoutItem returns [EObject current=null] : (this_WidgetRef_0= ruleWidgetRef | this_AttrWidget_1= ruleAttrWidget | this_Composite_2= ruleComposite ) ;
    public final EObject ruleLayoutItem() throws RecognitionException {
        EObject current = null;

        EObject this_WidgetRef_0 = null;

        EObject this_AttrWidget_1 = null;

        EObject this_Composite_2 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3133:28: ( (this_WidgetRef_0= ruleWidgetRef | this_AttrWidget_1= ruleAttrWidget | this_Composite_2= ruleComposite ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3134:1: (this_WidgetRef_0= ruleWidgetRef | this_AttrWidget_1= ruleAttrWidget | this_Composite_2= ruleComposite )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3134:1: (this_WidgetRef_0= ruleWidgetRef | this_AttrWidget_1= ruleAttrWidget | this_Composite_2= ruleComposite )
            int alt81=3;
            switch ( input.LA(1) ) {
            case 50:
                {
                alt81=1;
                }
                break;
            case 51:
                {
                alt81=2;
                }
                break;
            case 76:
                {
                alt81=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 81, 0, input);

                throw nvae;
            }

            switch (alt81) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3135:5: this_WidgetRef_0= ruleWidgetRef
                    {
                     
                            newCompositeNode(grammarAccess.getLayoutItemAccess().getWidgetRefParserRuleCall_0()); 
                        
                    pushFollow(FOLLOW_ruleWidgetRef_in_ruleLayoutItem6530);
                    this_WidgetRef_0=ruleWidgetRef();

                    state._fsp--;

                     
                            current = this_WidgetRef_0; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3145:5: this_AttrWidget_1= ruleAttrWidget
                    {
                     
                            newCompositeNode(grammarAccess.getLayoutItemAccess().getAttrWidgetParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_ruleAttrWidget_in_ruleLayoutItem6557);
                    this_AttrWidget_1=ruleAttrWidget();

                    state._fsp--;

                     
                            current = this_AttrWidget_1; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3155:5: this_Composite_2= ruleComposite
                    {
                     
                            newCompositeNode(grammarAccess.getLayoutItemAccess().getCompositeParserRuleCall_2()); 
                        
                    pushFollow(FOLLOW_ruleComposite_in_ruleLayoutItem6584);
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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3171:1: entryRuleComposite returns [EObject current=null] : iv_ruleComposite= ruleComposite EOF ;
    public final EObject entryRuleComposite() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleComposite = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3172:2: (iv_ruleComposite= ruleComposite EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3173:2: iv_ruleComposite= ruleComposite EOF
            {
             newCompositeNode(grammarAccess.getCompositeRule()); 
            pushFollow(FOLLOW_ruleComposite_in_entryRuleComposite6619);
            iv_ruleComposite=ruleComposite();

            state._fsp--;

             current =iv_ruleComposite; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleComposite6629); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3180:1: ruleComposite returns [EObject current=null] : (otherlv_0= 'composite' otherlv_1= '{' otherlv_2= 'numColumns' ( (lv_numColumns_3_0= RULE_INT ) ) ( (lv_layoutItems_4_0= ruleLayoutItem ) )+ (otherlv_5= 'option' ( (lv_options_6_0= ruleCompositeOption ) ) )* otherlv_7= '}' ) ;
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
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3183:28: ( (otherlv_0= 'composite' otherlv_1= '{' otherlv_2= 'numColumns' ( (lv_numColumns_3_0= RULE_INT ) ) ( (lv_layoutItems_4_0= ruleLayoutItem ) )+ (otherlv_5= 'option' ( (lv_options_6_0= ruleCompositeOption ) ) )* otherlv_7= '}' ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3184:1: (otherlv_0= 'composite' otherlv_1= '{' otherlv_2= 'numColumns' ( (lv_numColumns_3_0= RULE_INT ) ) ( (lv_layoutItems_4_0= ruleLayoutItem ) )+ (otherlv_5= 'option' ( (lv_options_6_0= ruleCompositeOption ) ) )* otherlv_7= '}' )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3184:1: (otherlv_0= 'composite' otherlv_1= '{' otherlv_2= 'numColumns' ( (lv_numColumns_3_0= RULE_INT ) ) ( (lv_layoutItems_4_0= ruleLayoutItem ) )+ (otherlv_5= 'option' ( (lv_options_6_0= ruleCompositeOption ) ) )* otherlv_7= '}' )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3184:3: otherlv_0= 'composite' otherlv_1= '{' otherlv_2= 'numColumns' ( (lv_numColumns_3_0= RULE_INT ) ) ( (lv_layoutItems_4_0= ruleLayoutItem ) )+ (otherlv_5= 'option' ( (lv_options_6_0= ruleCompositeOption ) ) )* otherlv_7= '}'
            {
            otherlv_0=(Token)match(input,76,FOLLOW_76_in_ruleComposite6666); 

                	newLeafNode(otherlv_0, grammarAccess.getCompositeAccess().getCompositeKeyword_0());
                
            otherlv_1=(Token)match(input,15,FOLLOW_15_in_ruleComposite6678); 

                	newLeafNode(otherlv_1, grammarAccess.getCompositeAccess().getLeftCurlyBracketKeyword_1());
                
            otherlv_2=(Token)match(input,77,FOLLOW_77_in_ruleComposite6690); 

                	newLeafNode(otherlv_2, grammarAccess.getCompositeAccess().getNumColumnsKeyword_2());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3196:1: ( (lv_numColumns_3_0= RULE_INT ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3197:1: (lv_numColumns_3_0= RULE_INT )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3197:1: (lv_numColumns_3_0= RULE_INT )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3198:3: lv_numColumns_3_0= RULE_INT
            {
            lv_numColumns_3_0=(Token)match(input,RULE_INT,FOLLOW_RULE_INT_in_ruleComposite6707); 

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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3214:2: ( (lv_layoutItems_4_0= ruleLayoutItem ) )+
            int cnt82=0;
            loop82:
            do {
                int alt82=2;
                int LA82_0 = input.LA(1);

                if ( ((LA82_0>=50 && LA82_0<=51)||LA82_0==76) ) {
                    alt82=1;
                }


                switch (alt82) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3215:1: (lv_layoutItems_4_0= ruleLayoutItem )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3215:1: (lv_layoutItems_4_0= ruleLayoutItem )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3216:3: lv_layoutItems_4_0= ruleLayoutItem
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getCompositeAccess().getLayoutItemsLayoutItemParserRuleCall_4_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleLayoutItem_in_ruleComposite6733);
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
            	    if ( cnt82 >= 1 ) break loop82;
                        EarlyExitException eee =
                            new EarlyExitException(82, input);
                        throw eee;
                }
                cnt82++;
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3232:3: (otherlv_5= 'option' ( (lv_options_6_0= ruleCompositeOption ) ) )*
            loop83:
            do {
                int alt83=2;
                int LA83_0 = input.LA(1);

                if ( (LA83_0==47) ) {
                    alt83=1;
                }


                switch (alt83) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3232:5: otherlv_5= 'option' ( (lv_options_6_0= ruleCompositeOption ) )
            	    {
            	    otherlv_5=(Token)match(input,47,FOLLOW_47_in_ruleComposite6747); 

            	        	newLeafNode(otherlv_5, grammarAccess.getCompositeAccess().getOptionKeyword_5_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3236:1: ( (lv_options_6_0= ruleCompositeOption ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3237:1: (lv_options_6_0= ruleCompositeOption )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3237:1: (lv_options_6_0= ruleCompositeOption )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3238:3: lv_options_6_0= ruleCompositeOption
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getCompositeAccess().getOptionsCompositeOptionParserRuleCall_5_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleCompositeOption_in_ruleComposite6768);
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
            	    break loop83;
                }
            } while (true);

            otherlv_7=(Token)match(input,20,FOLLOW_20_in_ruleComposite6782); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3266:1: entryRuleUSER_DEF_OPTION_NAME returns [String current=null] : iv_ruleUSER_DEF_OPTION_NAME= ruleUSER_DEF_OPTION_NAME EOF ;
    public final String entryRuleUSER_DEF_OPTION_NAME() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleUSER_DEF_OPTION_NAME = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3267:2: (iv_ruleUSER_DEF_OPTION_NAME= ruleUSER_DEF_OPTION_NAME EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3268:2: iv_ruleUSER_DEF_OPTION_NAME= ruleUSER_DEF_OPTION_NAME EOF
            {
             newCompositeNode(grammarAccess.getUSER_DEF_OPTION_NAMERule()); 
            pushFollow(FOLLOW_ruleUSER_DEF_OPTION_NAME_in_entryRuleUSER_DEF_OPTION_NAME6819);
            iv_ruleUSER_DEF_OPTION_NAME=ruleUSER_DEF_OPTION_NAME();

            state._fsp--;

             current =iv_ruleUSER_DEF_OPTION_NAME.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleUSER_DEF_OPTION_NAME6830); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3275:1: ruleUSER_DEF_OPTION_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleUSER_DEF_OPTION_NAME() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3278:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3279:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleUSER_DEF_OPTION_NAME6869); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3294:1: entryRuleUserDefOption returns [String current=null] : iv_ruleUserDefOption= ruleUserDefOption EOF ;
    public final String entryRuleUserDefOption() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleUserDefOption = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3295:2: (iv_ruleUserDefOption= ruleUserDefOption EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3296:2: iv_ruleUserDefOption= ruleUserDefOption EOF
            {
             newCompositeNode(grammarAccess.getUserDefOptionRule()); 
            pushFollow(FOLLOW_ruleUserDefOption_in_entryRuleUserDefOption6914);
            iv_ruleUserDefOption=ruleUserDefOption();

            state._fsp--;

             current =iv_ruleUserDefOption.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleUserDefOption6925); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3303:1: ruleUserDefOption returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'GetOrCreate' | this_USER_DEF_OPTION_NAME_1= ruleUSER_DEF_OPTION_NAME ) ;
    public final AntlrDatatypeRuleToken ruleUserDefOption() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        AntlrDatatypeRuleToken this_USER_DEF_OPTION_NAME_1 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3306:28: ( (kw= 'GetOrCreate' | this_USER_DEF_OPTION_NAME_1= ruleUSER_DEF_OPTION_NAME ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3307:1: (kw= 'GetOrCreate' | this_USER_DEF_OPTION_NAME_1= ruleUSER_DEF_OPTION_NAME )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3307:1: (kw= 'GetOrCreate' | this_USER_DEF_OPTION_NAME_1= ruleUSER_DEF_OPTION_NAME )
            int alt84=2;
            int LA84_0 = input.LA(1);

            if ( (LA84_0==78) ) {
                alt84=1;
            }
            else if ( (LA84_0==RULE_STRING) ) {
                alt84=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 84, 0, input);

                throw nvae;
            }
            switch (alt84) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3308:2: kw= 'GetOrCreate'
                    {
                    kw=(Token)match(input,78,FOLLOW_78_in_ruleUserDefOption6963); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getUserDefOptionAccess().getGetOrCreateKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3315:5: this_USER_DEF_OPTION_NAME_1= ruleUSER_DEF_OPTION_NAME
                    {
                     
                            newCompositeNode(grammarAccess.getUserDefOptionAccess().getUSER_DEF_OPTION_NAMEParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_ruleUSER_DEF_OPTION_NAME_in_ruleUserDefOption6991);
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


    // $ANTLR start "entryRuleTEAM_DEF_OPTION_NAME"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3333:1: entryRuleTEAM_DEF_OPTION_NAME returns [String current=null] : iv_ruleTEAM_DEF_OPTION_NAME= ruleTEAM_DEF_OPTION_NAME EOF ;
    public final String entryRuleTEAM_DEF_OPTION_NAME() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleTEAM_DEF_OPTION_NAME = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3334:2: (iv_ruleTEAM_DEF_OPTION_NAME= ruleTEAM_DEF_OPTION_NAME EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3335:2: iv_ruleTEAM_DEF_OPTION_NAME= ruleTEAM_DEF_OPTION_NAME EOF
            {
             newCompositeNode(grammarAccess.getTEAM_DEF_OPTION_NAMERule()); 
            pushFollow(FOLLOW_ruleTEAM_DEF_OPTION_NAME_in_entryRuleTEAM_DEF_OPTION_NAME7037);
            iv_ruleTEAM_DEF_OPTION_NAME=ruleTEAM_DEF_OPTION_NAME();

            state._fsp--;

             current =iv_ruleTEAM_DEF_OPTION_NAME.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleTEAM_DEF_OPTION_NAME7048); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3342:1: ruleTEAM_DEF_OPTION_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleTEAM_DEF_OPTION_NAME() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3345:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3346:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleTEAM_DEF_OPTION_NAME7087); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3361:1: entryRuleTeamDefOption returns [String current=null] : iv_ruleTeamDefOption= ruleTeamDefOption EOF ;
    public final String entryRuleTeamDefOption() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleTeamDefOption = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3362:2: (iv_ruleTeamDefOption= ruleTeamDefOption EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3363:2: iv_ruleTeamDefOption= ruleTeamDefOption EOF
            {
             newCompositeNode(grammarAccess.getTeamDefOptionRule()); 
            pushFollow(FOLLOW_ruleTeamDefOption_in_entryRuleTeamDefOption7132);
            iv_ruleTeamDefOption=ruleTeamDefOption();

            state._fsp--;

             current =iv_ruleTeamDefOption.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleTeamDefOption7143); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3370:1: ruleTeamDefOption returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'GetOrCreate' | this_TEAM_DEF_OPTION_NAME_1= ruleTEAM_DEF_OPTION_NAME ) ;
    public final AntlrDatatypeRuleToken ruleTeamDefOption() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        AntlrDatatypeRuleToken this_TEAM_DEF_OPTION_NAME_1 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3373:28: ( (kw= 'GetOrCreate' | this_TEAM_DEF_OPTION_NAME_1= ruleTEAM_DEF_OPTION_NAME ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3374:1: (kw= 'GetOrCreate' | this_TEAM_DEF_OPTION_NAME_1= ruleTEAM_DEF_OPTION_NAME )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3374:1: (kw= 'GetOrCreate' | this_TEAM_DEF_OPTION_NAME_1= ruleTEAM_DEF_OPTION_NAME )
            int alt85=2;
            int LA85_0 = input.LA(1);

            if ( (LA85_0==78) ) {
                alt85=1;
            }
            else if ( (LA85_0==RULE_STRING) ) {
                alt85=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 85, 0, input);

                throw nvae;
            }
            switch (alt85) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3375:2: kw= 'GetOrCreate'
                    {
                    kw=(Token)match(input,78,FOLLOW_78_in_ruleTeamDefOption7181); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getTeamDefOptionAccess().getGetOrCreateKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3382:5: this_TEAM_DEF_OPTION_NAME_1= ruleTEAM_DEF_OPTION_NAME
                    {
                     
                            newCompositeNode(grammarAccess.getTeamDefOptionAccess().getTEAM_DEF_OPTION_NAMEParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_ruleTEAM_DEF_OPTION_NAME_in_ruleTeamDefOption7209);
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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3400:1: entryRuleAI_DEF_OPTION_NAME returns [String current=null] : iv_ruleAI_DEF_OPTION_NAME= ruleAI_DEF_OPTION_NAME EOF ;
    public final String entryRuleAI_DEF_OPTION_NAME() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleAI_DEF_OPTION_NAME = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3401:2: (iv_ruleAI_DEF_OPTION_NAME= ruleAI_DEF_OPTION_NAME EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3402:2: iv_ruleAI_DEF_OPTION_NAME= ruleAI_DEF_OPTION_NAME EOF
            {
             newCompositeNode(grammarAccess.getAI_DEF_OPTION_NAMERule()); 
            pushFollow(FOLLOW_ruleAI_DEF_OPTION_NAME_in_entryRuleAI_DEF_OPTION_NAME7255);
            iv_ruleAI_DEF_OPTION_NAME=ruleAI_DEF_OPTION_NAME();

            state._fsp--;

             current =iv_ruleAI_DEF_OPTION_NAME.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAI_DEF_OPTION_NAME7266); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3409:1: ruleAI_DEF_OPTION_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleAI_DEF_OPTION_NAME() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3412:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3413:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAI_DEF_OPTION_NAME7305); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3428:1: entryRuleActionableItemOption returns [String current=null] : iv_ruleActionableItemOption= ruleActionableItemOption EOF ;
    public final String entryRuleActionableItemOption() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleActionableItemOption = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3429:2: (iv_ruleActionableItemOption= ruleActionableItemOption EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3430:2: iv_ruleActionableItemOption= ruleActionableItemOption EOF
            {
             newCompositeNode(grammarAccess.getActionableItemOptionRule()); 
            pushFollow(FOLLOW_ruleActionableItemOption_in_entryRuleActionableItemOption7350);
            iv_ruleActionableItemOption=ruleActionableItemOption();

            state._fsp--;

             current =iv_ruleActionableItemOption.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleActionableItemOption7361); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3437:1: ruleActionableItemOption returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'GetOrCreate' | this_AI_DEF_OPTION_NAME_1= ruleAI_DEF_OPTION_NAME ) ;
    public final AntlrDatatypeRuleToken ruleActionableItemOption() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        AntlrDatatypeRuleToken this_AI_DEF_OPTION_NAME_1 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3440:28: ( (kw= 'GetOrCreate' | this_AI_DEF_OPTION_NAME_1= ruleAI_DEF_OPTION_NAME ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3441:1: (kw= 'GetOrCreate' | this_AI_DEF_OPTION_NAME_1= ruleAI_DEF_OPTION_NAME )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3441:1: (kw= 'GetOrCreate' | this_AI_DEF_OPTION_NAME_1= ruleAI_DEF_OPTION_NAME )
            int alt86=2;
            int LA86_0 = input.LA(1);

            if ( (LA86_0==78) ) {
                alt86=1;
            }
            else if ( (LA86_0==RULE_STRING) ) {
                alt86=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 86, 0, input);

                throw nvae;
            }
            switch (alt86) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3442:2: kw= 'GetOrCreate'
                    {
                    kw=(Token)match(input,78,FOLLOW_78_in_ruleActionableItemOption7399); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getActionableItemOptionAccess().getGetOrCreateKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3449:5: this_AI_DEF_OPTION_NAME_1= ruleAI_DEF_OPTION_NAME
                    {
                     
                            newCompositeNode(grammarAccess.getActionableItemOptionAccess().getAI_DEF_OPTION_NAMEParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_ruleAI_DEF_OPTION_NAME_in_ruleActionableItemOption7427);
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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3467:1: entryRuleCOMPOSITE_OPTION_NAME returns [String current=null] : iv_ruleCOMPOSITE_OPTION_NAME= ruleCOMPOSITE_OPTION_NAME EOF ;
    public final String entryRuleCOMPOSITE_OPTION_NAME() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleCOMPOSITE_OPTION_NAME = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3468:2: (iv_ruleCOMPOSITE_OPTION_NAME= ruleCOMPOSITE_OPTION_NAME EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3469:2: iv_ruleCOMPOSITE_OPTION_NAME= ruleCOMPOSITE_OPTION_NAME EOF
            {
             newCompositeNode(grammarAccess.getCOMPOSITE_OPTION_NAMERule()); 
            pushFollow(FOLLOW_ruleCOMPOSITE_OPTION_NAME_in_entryRuleCOMPOSITE_OPTION_NAME7473);
            iv_ruleCOMPOSITE_OPTION_NAME=ruleCOMPOSITE_OPTION_NAME();

            state._fsp--;

             current =iv_ruleCOMPOSITE_OPTION_NAME.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleCOMPOSITE_OPTION_NAME7484); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3476:1: ruleCOMPOSITE_OPTION_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleCOMPOSITE_OPTION_NAME() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3479:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3480:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleCOMPOSITE_OPTION_NAME7523); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3495:1: entryRuleCompositeOption returns [String current=null] : iv_ruleCompositeOption= ruleCompositeOption EOF ;
    public final String entryRuleCompositeOption() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleCompositeOption = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3496:2: (iv_ruleCompositeOption= ruleCompositeOption EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3497:2: iv_ruleCompositeOption= ruleCompositeOption EOF
            {
             newCompositeNode(grammarAccess.getCompositeOptionRule()); 
            pushFollow(FOLLOW_ruleCompositeOption_in_entryRuleCompositeOption7568);
            iv_ruleCompositeOption=ruleCompositeOption();

            state._fsp--;

             current =iv_ruleCompositeOption.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleCompositeOption7579); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3504:1: ruleCompositeOption returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'None' | this_COMPOSITE_OPTION_NAME_1= ruleCOMPOSITE_OPTION_NAME ) ;
    public final AntlrDatatypeRuleToken ruleCompositeOption() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        AntlrDatatypeRuleToken this_COMPOSITE_OPTION_NAME_1 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3507:28: ( (kw= 'None' | this_COMPOSITE_OPTION_NAME_1= ruleCOMPOSITE_OPTION_NAME ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3508:1: (kw= 'None' | this_COMPOSITE_OPTION_NAME_1= ruleCOMPOSITE_OPTION_NAME )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3508:1: (kw= 'None' | this_COMPOSITE_OPTION_NAME_1= ruleCOMPOSITE_OPTION_NAME )
            int alt87=2;
            int LA87_0 = input.LA(1);

            if ( (LA87_0==79) ) {
                alt87=1;
            }
            else if ( (LA87_0==RULE_STRING) ) {
                alt87=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 87, 0, input);

                throw nvae;
            }
            switch (alt87) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3509:2: kw= 'None'
                    {
                    kw=(Token)match(input,79,FOLLOW_79_in_ruleCompositeOption7617); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getCompositeOptionAccess().getNoneKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3516:5: this_COMPOSITE_OPTION_NAME_1= ruleCOMPOSITE_OPTION_NAME
                    {
                     
                            newCompositeNode(grammarAccess.getCompositeOptionAccess().getCOMPOSITE_OPTION_NAMEParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_ruleCOMPOSITE_OPTION_NAME_in_ruleCompositeOption7645);
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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3534:1: entryRuleTRANSITION_OPTION_NAME returns [String current=null] : iv_ruleTRANSITION_OPTION_NAME= ruleTRANSITION_OPTION_NAME EOF ;
    public final String entryRuleTRANSITION_OPTION_NAME() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleTRANSITION_OPTION_NAME = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3535:2: (iv_ruleTRANSITION_OPTION_NAME= ruleTRANSITION_OPTION_NAME EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3536:2: iv_ruleTRANSITION_OPTION_NAME= ruleTRANSITION_OPTION_NAME EOF
            {
             newCompositeNode(grammarAccess.getTRANSITION_OPTION_NAMERule()); 
            pushFollow(FOLLOW_ruleTRANSITION_OPTION_NAME_in_entryRuleTRANSITION_OPTION_NAME7691);
            iv_ruleTRANSITION_OPTION_NAME=ruleTRANSITION_OPTION_NAME();

            state._fsp--;

             current =iv_ruleTRANSITION_OPTION_NAME.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleTRANSITION_OPTION_NAME7702); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3543:1: ruleTRANSITION_OPTION_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleTRANSITION_OPTION_NAME() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3546:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3547:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleTRANSITION_OPTION_NAME7741); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3562:1: entryRuleTransitionOption returns [String current=null] : iv_ruleTransitionOption= ruleTransitionOption EOF ;
    public final String entryRuleTransitionOption() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleTransitionOption = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3563:2: (iv_ruleTransitionOption= ruleTransitionOption EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3564:2: iv_ruleTransitionOption= ruleTransitionOption EOF
            {
             newCompositeNode(grammarAccess.getTransitionOptionRule()); 
            pushFollow(FOLLOW_ruleTransitionOption_in_entryRuleTransitionOption7786);
            iv_ruleTransitionOption=ruleTransitionOption();

            state._fsp--;

             current =iv_ruleTransitionOption.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleTransitionOption7797); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3571:1: ruleTransitionOption returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'AsDefault' | kw= 'OverrideAttributeValidation' | this_TRANSITION_OPTION_NAME_2= ruleTRANSITION_OPTION_NAME ) ;
    public final AntlrDatatypeRuleToken ruleTransitionOption() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        AntlrDatatypeRuleToken this_TRANSITION_OPTION_NAME_2 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3574:28: ( (kw= 'AsDefault' | kw= 'OverrideAttributeValidation' | this_TRANSITION_OPTION_NAME_2= ruleTRANSITION_OPTION_NAME ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3575:1: (kw= 'AsDefault' | kw= 'OverrideAttributeValidation' | this_TRANSITION_OPTION_NAME_2= ruleTRANSITION_OPTION_NAME )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3575:1: (kw= 'AsDefault' | kw= 'OverrideAttributeValidation' | this_TRANSITION_OPTION_NAME_2= ruleTRANSITION_OPTION_NAME )
            int alt88=3;
            switch ( input.LA(1) ) {
            case 80:
                {
                alt88=1;
                }
                break;
            case 81:
                {
                alt88=2;
                }
                break;
            case RULE_STRING:
                {
                alt88=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 88, 0, input);

                throw nvae;
            }

            switch (alt88) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3576:2: kw= 'AsDefault'
                    {
                    kw=(Token)match(input,80,FOLLOW_80_in_ruleTransitionOption7835); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getTransitionOptionAccess().getAsDefaultKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3583:2: kw= 'OverrideAttributeValidation'
                    {
                    kw=(Token)match(input,81,FOLLOW_81_in_ruleTransitionOption7854); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getTransitionOptionAccess().getOverrideAttributeValidationKeyword_1()); 
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3590:5: this_TRANSITION_OPTION_NAME_2= ruleTRANSITION_OPTION_NAME
                    {
                     
                            newCompositeNode(grammarAccess.getTransitionOptionAccess().getTRANSITION_OPTION_NAMEParserRuleCall_2()); 
                        
                    pushFollow(FOLLOW_ruleTRANSITION_OPTION_NAME_in_ruleTransitionOption7882);
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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3608:1: entryRuleRULE_NAME returns [String current=null] : iv_ruleRULE_NAME= ruleRULE_NAME EOF ;
    public final String entryRuleRULE_NAME() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleRULE_NAME = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3609:2: (iv_ruleRULE_NAME= ruleRULE_NAME EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3610:2: iv_ruleRULE_NAME= ruleRULE_NAME EOF
            {
             newCompositeNode(grammarAccess.getRULE_NAMERule()); 
            pushFollow(FOLLOW_ruleRULE_NAME_in_entryRuleRULE_NAME7928);
            iv_ruleRULE_NAME=ruleRULE_NAME();

            state._fsp--;

             current =iv_ruleRULE_NAME.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleRULE_NAME7939); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3617:1: ruleRULE_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleRULE_NAME() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3620:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3621:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleRULE_NAME7978); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3636:1: entryRuleRule returns [String current=null] : iv_ruleRule= ruleRule EOF ;
    public final String entryRuleRule() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleRule = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3637:2: (iv_ruleRule= ruleRule EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3638:2: iv_ruleRule= ruleRule EOF
            {
             newCompositeNode(grammarAccess.getRuleRule()); 
            pushFollow(FOLLOW_ruleRule_in_entryRuleRule8023);
            iv_ruleRule=ruleRule();

            state._fsp--;

             current =iv_ruleRule.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleRule8034); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3645:1: ruleRule returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'RequireStateHourSpentPrompt' | kw= 'AddDecisionValidateBlockingReview' | kw= 'AddDecisionValidateNonBlockingReview' | kw= 'AllowTransitionWithWorkingBranch' | kw= 'ForceAssigneesToTeamLeads' | kw= 'RequireTargetedVersion' | kw= 'AllowPrivilegedEditToTeamMember' | kw= 'AllowPrivilegedEditToTeamMemberAndOriginator' | kw= 'AllowPrivilegedEditToAll' | kw= 'AllowEditToAll' | kw= 'AllowAssigneeToAll' | kw= 'AllowTransitionWithoutTaskCompletion' | this_RULE_NAME_12= ruleRULE_NAME ) ;
    public final AntlrDatatypeRuleToken ruleRule() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        AntlrDatatypeRuleToken this_RULE_NAME_12 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3648:28: ( (kw= 'RequireStateHourSpentPrompt' | kw= 'AddDecisionValidateBlockingReview' | kw= 'AddDecisionValidateNonBlockingReview' | kw= 'AllowTransitionWithWorkingBranch' | kw= 'ForceAssigneesToTeamLeads' | kw= 'RequireTargetedVersion' | kw= 'AllowPrivilegedEditToTeamMember' | kw= 'AllowPrivilegedEditToTeamMemberAndOriginator' | kw= 'AllowPrivilegedEditToAll' | kw= 'AllowEditToAll' | kw= 'AllowAssigneeToAll' | kw= 'AllowTransitionWithoutTaskCompletion' | this_RULE_NAME_12= ruleRULE_NAME ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3649:1: (kw= 'RequireStateHourSpentPrompt' | kw= 'AddDecisionValidateBlockingReview' | kw= 'AddDecisionValidateNonBlockingReview' | kw= 'AllowTransitionWithWorkingBranch' | kw= 'ForceAssigneesToTeamLeads' | kw= 'RequireTargetedVersion' | kw= 'AllowPrivilegedEditToTeamMember' | kw= 'AllowPrivilegedEditToTeamMemberAndOriginator' | kw= 'AllowPrivilegedEditToAll' | kw= 'AllowEditToAll' | kw= 'AllowAssigneeToAll' | kw= 'AllowTransitionWithoutTaskCompletion' | this_RULE_NAME_12= ruleRULE_NAME )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3649:1: (kw= 'RequireStateHourSpentPrompt' | kw= 'AddDecisionValidateBlockingReview' | kw= 'AddDecisionValidateNonBlockingReview' | kw= 'AllowTransitionWithWorkingBranch' | kw= 'ForceAssigneesToTeamLeads' | kw= 'RequireTargetedVersion' | kw= 'AllowPrivilegedEditToTeamMember' | kw= 'AllowPrivilegedEditToTeamMemberAndOriginator' | kw= 'AllowPrivilegedEditToAll' | kw= 'AllowEditToAll' | kw= 'AllowAssigneeToAll' | kw= 'AllowTransitionWithoutTaskCompletion' | this_RULE_NAME_12= ruleRULE_NAME )
            int alt89=13;
            switch ( input.LA(1) ) {
            case 82:
                {
                alt89=1;
                }
                break;
            case 83:
                {
                alt89=2;
                }
                break;
            case 84:
                {
                alt89=3;
                }
                break;
            case 85:
                {
                alt89=4;
                }
                break;
            case 86:
                {
                alt89=5;
                }
                break;
            case 87:
                {
                alt89=6;
                }
                break;
            case 88:
                {
                alt89=7;
                }
                break;
            case 89:
                {
                alt89=8;
                }
                break;
            case 90:
                {
                alt89=9;
                }
                break;
            case 91:
                {
                alt89=10;
                }
                break;
            case 92:
                {
                alt89=11;
                }
                break;
            case 93:
                {
                alt89=12;
                }
                break;
            case RULE_STRING:
                {
                alt89=13;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 89, 0, input);

                throw nvae;
            }

            switch (alt89) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3650:2: kw= 'RequireStateHourSpentPrompt'
                    {
                    kw=(Token)match(input,82,FOLLOW_82_in_ruleRule8072); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleAccess().getRequireStateHourSpentPromptKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3657:2: kw= 'AddDecisionValidateBlockingReview'
                    {
                    kw=(Token)match(input,83,FOLLOW_83_in_ruleRule8091); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleAccess().getAddDecisionValidateBlockingReviewKeyword_1()); 
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3664:2: kw= 'AddDecisionValidateNonBlockingReview'
                    {
                    kw=(Token)match(input,84,FOLLOW_84_in_ruleRule8110); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleAccess().getAddDecisionValidateNonBlockingReviewKeyword_2()); 
                        

                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3671:2: kw= 'AllowTransitionWithWorkingBranch'
                    {
                    kw=(Token)match(input,85,FOLLOW_85_in_ruleRule8129); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleAccess().getAllowTransitionWithWorkingBranchKeyword_3()); 
                        

                    }
                    break;
                case 5 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3678:2: kw= 'ForceAssigneesToTeamLeads'
                    {
                    kw=(Token)match(input,86,FOLLOW_86_in_ruleRule8148); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleAccess().getForceAssigneesToTeamLeadsKeyword_4()); 
                        

                    }
                    break;
                case 6 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3685:2: kw= 'RequireTargetedVersion'
                    {
                    kw=(Token)match(input,87,FOLLOW_87_in_ruleRule8167); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleAccess().getRequireTargetedVersionKeyword_5()); 
                        

                    }
                    break;
                case 7 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3692:2: kw= 'AllowPrivilegedEditToTeamMember'
                    {
                    kw=(Token)match(input,88,FOLLOW_88_in_ruleRule8186); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleAccess().getAllowPrivilegedEditToTeamMemberKeyword_6()); 
                        

                    }
                    break;
                case 8 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3699:2: kw= 'AllowPrivilegedEditToTeamMemberAndOriginator'
                    {
                    kw=(Token)match(input,89,FOLLOW_89_in_ruleRule8205); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleAccess().getAllowPrivilegedEditToTeamMemberAndOriginatorKeyword_7()); 
                        

                    }
                    break;
                case 9 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3706:2: kw= 'AllowPrivilegedEditToAll'
                    {
                    kw=(Token)match(input,90,FOLLOW_90_in_ruleRule8224); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleAccess().getAllowPrivilegedEditToAllKeyword_8()); 
                        

                    }
                    break;
                case 10 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3713:2: kw= 'AllowEditToAll'
                    {
                    kw=(Token)match(input,91,FOLLOW_91_in_ruleRule8243); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleAccess().getAllowEditToAllKeyword_9()); 
                        

                    }
                    break;
                case 11 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3720:2: kw= 'AllowAssigneeToAll'
                    {
                    kw=(Token)match(input,92,FOLLOW_92_in_ruleRule8262); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleAccess().getAllowAssigneeToAllKeyword_10()); 
                        

                    }
                    break;
                case 12 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3727:2: kw= 'AllowTransitionWithoutTaskCompletion'
                    {
                    kw=(Token)match(input,93,FOLLOW_93_in_ruleRule8281); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleAccess().getAllowTransitionWithoutTaskCompletionKeyword_11()); 
                        

                    }
                    break;
                case 13 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3734:5: this_RULE_NAME_12= ruleRULE_NAME
                    {
                     
                            newCompositeNode(grammarAccess.getRuleAccess().getRULE_NAMEParserRuleCall_12()); 
                        
                    pushFollow(FOLLOW_ruleRULE_NAME_in_ruleRule8309);
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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3752:1: entryRuleWIDGET_OPTION_NAME returns [String current=null] : iv_ruleWIDGET_OPTION_NAME= ruleWIDGET_OPTION_NAME EOF ;
    public final String entryRuleWIDGET_OPTION_NAME() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleWIDGET_OPTION_NAME = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3753:2: (iv_ruleWIDGET_OPTION_NAME= ruleWIDGET_OPTION_NAME EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3754:2: iv_ruleWIDGET_OPTION_NAME= ruleWIDGET_OPTION_NAME EOF
            {
             newCompositeNode(grammarAccess.getWIDGET_OPTION_NAMERule()); 
            pushFollow(FOLLOW_ruleWIDGET_OPTION_NAME_in_entryRuleWIDGET_OPTION_NAME8355);
            iv_ruleWIDGET_OPTION_NAME=ruleWIDGET_OPTION_NAME();

            state._fsp--;

             current =iv_ruleWIDGET_OPTION_NAME.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleWIDGET_OPTION_NAME8366); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3761:1: ruleWIDGET_OPTION_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleWIDGET_OPTION_NAME() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3764:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3765:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleWIDGET_OPTION_NAME8405); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3780:1: entryRuleWidgetOption returns [String current=null] : iv_ruleWidgetOption= ruleWidgetOption EOF ;
    public final String entryRuleWidgetOption() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleWidgetOption = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3781:2: (iv_ruleWidgetOption= ruleWidgetOption EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3782:2: iv_ruleWidgetOption= ruleWidgetOption EOF
            {
             newCompositeNode(grammarAccess.getWidgetOptionRule()); 
            pushFollow(FOLLOW_ruleWidgetOption_in_entryRuleWidgetOption8450);
            iv_ruleWidgetOption=ruleWidgetOption();

            state._fsp--;

             current =iv_ruleWidgetOption.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleWidgetOption8461); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3789:1: ruleWidgetOption returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'REQUIRED_FOR_TRANSITION' | kw= 'NOT_REQUIRED_FOR_TRANSITION' | kw= 'REQUIRED_FOR_COMPLETION' | kw= 'NOT_REQUIRED_FOR_COMPLETION' | kw= 'ENABLED' | kw= 'NOT_ENABLED' | kw= 'EDITABLE' | kw= 'NOT_EDITABLE' | kw= 'FUTURE_DATE_REQUIRED' | kw= 'NOT_FUTURE_DATE_REQUIRED' | kw= 'MULTI_SELECT' | kw= 'HORIZONTAL_LABEL' | kw= 'VERTICAL_LABEL' | kw= 'LABEL_AFTER' | kw= 'LABEL_BEFORE' | kw= 'NO_LABEL' | kw= 'SORTED' | kw= 'ADD_DEFAULT_VALUE' | kw= 'NO_DEFAULT_VALUE' | kw= 'BEGIN_COMPOSITE_4' | kw= 'BEGIN_COMPOSITE_6' | kw= 'BEGIN_COMPOSITE_8' | kw= 'BEGIN_COMPOSITE_10' | kw= 'END_COMPOSITE' | kw= 'FILL_NONE' | kw= 'FILL_HORIZONTALLY' | kw= 'FILL_VERTICALLY' | kw= 'ALIGN_LEFT' | kw= 'ALIGN_RIGHT' | kw= 'ALIGN_CENTER' | this_WIDGET_OPTION_NAME_30= ruleWIDGET_OPTION_NAME ) ;
    public final AntlrDatatypeRuleToken ruleWidgetOption() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        AntlrDatatypeRuleToken this_WIDGET_OPTION_NAME_30 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3792:28: ( (kw= 'REQUIRED_FOR_TRANSITION' | kw= 'NOT_REQUIRED_FOR_TRANSITION' | kw= 'REQUIRED_FOR_COMPLETION' | kw= 'NOT_REQUIRED_FOR_COMPLETION' | kw= 'ENABLED' | kw= 'NOT_ENABLED' | kw= 'EDITABLE' | kw= 'NOT_EDITABLE' | kw= 'FUTURE_DATE_REQUIRED' | kw= 'NOT_FUTURE_DATE_REQUIRED' | kw= 'MULTI_SELECT' | kw= 'HORIZONTAL_LABEL' | kw= 'VERTICAL_LABEL' | kw= 'LABEL_AFTER' | kw= 'LABEL_BEFORE' | kw= 'NO_LABEL' | kw= 'SORTED' | kw= 'ADD_DEFAULT_VALUE' | kw= 'NO_DEFAULT_VALUE' | kw= 'BEGIN_COMPOSITE_4' | kw= 'BEGIN_COMPOSITE_6' | kw= 'BEGIN_COMPOSITE_8' | kw= 'BEGIN_COMPOSITE_10' | kw= 'END_COMPOSITE' | kw= 'FILL_NONE' | kw= 'FILL_HORIZONTALLY' | kw= 'FILL_VERTICALLY' | kw= 'ALIGN_LEFT' | kw= 'ALIGN_RIGHT' | kw= 'ALIGN_CENTER' | this_WIDGET_OPTION_NAME_30= ruleWIDGET_OPTION_NAME ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3793:1: (kw= 'REQUIRED_FOR_TRANSITION' | kw= 'NOT_REQUIRED_FOR_TRANSITION' | kw= 'REQUIRED_FOR_COMPLETION' | kw= 'NOT_REQUIRED_FOR_COMPLETION' | kw= 'ENABLED' | kw= 'NOT_ENABLED' | kw= 'EDITABLE' | kw= 'NOT_EDITABLE' | kw= 'FUTURE_DATE_REQUIRED' | kw= 'NOT_FUTURE_DATE_REQUIRED' | kw= 'MULTI_SELECT' | kw= 'HORIZONTAL_LABEL' | kw= 'VERTICAL_LABEL' | kw= 'LABEL_AFTER' | kw= 'LABEL_BEFORE' | kw= 'NO_LABEL' | kw= 'SORTED' | kw= 'ADD_DEFAULT_VALUE' | kw= 'NO_DEFAULT_VALUE' | kw= 'BEGIN_COMPOSITE_4' | kw= 'BEGIN_COMPOSITE_6' | kw= 'BEGIN_COMPOSITE_8' | kw= 'BEGIN_COMPOSITE_10' | kw= 'END_COMPOSITE' | kw= 'FILL_NONE' | kw= 'FILL_HORIZONTALLY' | kw= 'FILL_VERTICALLY' | kw= 'ALIGN_LEFT' | kw= 'ALIGN_RIGHT' | kw= 'ALIGN_CENTER' | this_WIDGET_OPTION_NAME_30= ruleWIDGET_OPTION_NAME )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3793:1: (kw= 'REQUIRED_FOR_TRANSITION' | kw= 'NOT_REQUIRED_FOR_TRANSITION' | kw= 'REQUIRED_FOR_COMPLETION' | kw= 'NOT_REQUIRED_FOR_COMPLETION' | kw= 'ENABLED' | kw= 'NOT_ENABLED' | kw= 'EDITABLE' | kw= 'NOT_EDITABLE' | kw= 'FUTURE_DATE_REQUIRED' | kw= 'NOT_FUTURE_DATE_REQUIRED' | kw= 'MULTI_SELECT' | kw= 'HORIZONTAL_LABEL' | kw= 'VERTICAL_LABEL' | kw= 'LABEL_AFTER' | kw= 'LABEL_BEFORE' | kw= 'NO_LABEL' | kw= 'SORTED' | kw= 'ADD_DEFAULT_VALUE' | kw= 'NO_DEFAULT_VALUE' | kw= 'BEGIN_COMPOSITE_4' | kw= 'BEGIN_COMPOSITE_6' | kw= 'BEGIN_COMPOSITE_8' | kw= 'BEGIN_COMPOSITE_10' | kw= 'END_COMPOSITE' | kw= 'FILL_NONE' | kw= 'FILL_HORIZONTALLY' | kw= 'FILL_VERTICALLY' | kw= 'ALIGN_LEFT' | kw= 'ALIGN_RIGHT' | kw= 'ALIGN_CENTER' | this_WIDGET_OPTION_NAME_30= ruleWIDGET_OPTION_NAME )
            int alt90=31;
            switch ( input.LA(1) ) {
            case 94:
                {
                alt90=1;
                }
                break;
            case 95:
                {
                alt90=2;
                }
                break;
            case 96:
                {
                alt90=3;
                }
                break;
            case 97:
                {
                alt90=4;
                }
                break;
            case 98:
                {
                alt90=5;
                }
                break;
            case 99:
                {
                alt90=6;
                }
                break;
            case 100:
                {
                alt90=7;
                }
                break;
            case 101:
                {
                alt90=8;
                }
                break;
            case 102:
                {
                alt90=9;
                }
                break;
            case 103:
                {
                alt90=10;
                }
                break;
            case 104:
                {
                alt90=11;
                }
                break;
            case 105:
                {
                alt90=12;
                }
                break;
            case 106:
                {
                alt90=13;
                }
                break;
            case 107:
                {
                alt90=14;
                }
                break;
            case 108:
                {
                alt90=15;
                }
                break;
            case 109:
                {
                alt90=16;
                }
                break;
            case 110:
                {
                alt90=17;
                }
                break;
            case 111:
                {
                alt90=18;
                }
                break;
            case 112:
                {
                alt90=19;
                }
                break;
            case 113:
                {
                alt90=20;
                }
                break;
            case 114:
                {
                alt90=21;
                }
                break;
            case 115:
                {
                alt90=22;
                }
                break;
            case 116:
                {
                alt90=23;
                }
                break;
            case 117:
                {
                alt90=24;
                }
                break;
            case 118:
                {
                alt90=25;
                }
                break;
            case 119:
                {
                alt90=26;
                }
                break;
            case 120:
                {
                alt90=27;
                }
                break;
            case 121:
                {
                alt90=28;
                }
                break;
            case 122:
                {
                alt90=29;
                }
                break;
            case 123:
                {
                alt90=30;
                }
                break;
            case RULE_STRING:
                {
                alt90=31;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 90, 0, input);

                throw nvae;
            }

            switch (alt90) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3794:2: kw= 'REQUIRED_FOR_TRANSITION'
                    {
                    kw=(Token)match(input,94,FOLLOW_94_in_ruleWidgetOption8499); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getREQUIRED_FOR_TRANSITIONKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3801:2: kw= 'NOT_REQUIRED_FOR_TRANSITION'
                    {
                    kw=(Token)match(input,95,FOLLOW_95_in_ruleWidgetOption8518); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getNOT_REQUIRED_FOR_TRANSITIONKeyword_1()); 
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3808:2: kw= 'REQUIRED_FOR_COMPLETION'
                    {
                    kw=(Token)match(input,96,FOLLOW_96_in_ruleWidgetOption8537); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getREQUIRED_FOR_COMPLETIONKeyword_2()); 
                        

                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3815:2: kw= 'NOT_REQUIRED_FOR_COMPLETION'
                    {
                    kw=(Token)match(input,97,FOLLOW_97_in_ruleWidgetOption8556); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getNOT_REQUIRED_FOR_COMPLETIONKeyword_3()); 
                        

                    }
                    break;
                case 5 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3822:2: kw= 'ENABLED'
                    {
                    kw=(Token)match(input,98,FOLLOW_98_in_ruleWidgetOption8575); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getENABLEDKeyword_4()); 
                        

                    }
                    break;
                case 6 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3829:2: kw= 'NOT_ENABLED'
                    {
                    kw=(Token)match(input,99,FOLLOW_99_in_ruleWidgetOption8594); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getNOT_ENABLEDKeyword_5()); 
                        

                    }
                    break;
                case 7 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3836:2: kw= 'EDITABLE'
                    {
                    kw=(Token)match(input,100,FOLLOW_100_in_ruleWidgetOption8613); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getEDITABLEKeyword_6()); 
                        

                    }
                    break;
                case 8 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3843:2: kw= 'NOT_EDITABLE'
                    {
                    kw=(Token)match(input,101,FOLLOW_101_in_ruleWidgetOption8632); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getNOT_EDITABLEKeyword_7()); 
                        

                    }
                    break;
                case 9 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3850:2: kw= 'FUTURE_DATE_REQUIRED'
                    {
                    kw=(Token)match(input,102,FOLLOW_102_in_ruleWidgetOption8651); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getFUTURE_DATE_REQUIREDKeyword_8()); 
                        

                    }
                    break;
                case 10 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3857:2: kw= 'NOT_FUTURE_DATE_REQUIRED'
                    {
                    kw=(Token)match(input,103,FOLLOW_103_in_ruleWidgetOption8670); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getNOT_FUTURE_DATE_REQUIREDKeyword_9()); 
                        

                    }
                    break;
                case 11 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3864:2: kw= 'MULTI_SELECT'
                    {
                    kw=(Token)match(input,104,FOLLOW_104_in_ruleWidgetOption8689); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getMULTI_SELECTKeyword_10()); 
                        

                    }
                    break;
                case 12 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3871:2: kw= 'HORIZONTAL_LABEL'
                    {
                    kw=(Token)match(input,105,FOLLOW_105_in_ruleWidgetOption8708); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getHORIZONTAL_LABELKeyword_11()); 
                        

                    }
                    break;
                case 13 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3878:2: kw= 'VERTICAL_LABEL'
                    {
                    kw=(Token)match(input,106,FOLLOW_106_in_ruleWidgetOption8727); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getVERTICAL_LABELKeyword_12()); 
                        

                    }
                    break;
                case 14 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3885:2: kw= 'LABEL_AFTER'
                    {
                    kw=(Token)match(input,107,FOLLOW_107_in_ruleWidgetOption8746); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getLABEL_AFTERKeyword_13()); 
                        

                    }
                    break;
                case 15 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3892:2: kw= 'LABEL_BEFORE'
                    {
                    kw=(Token)match(input,108,FOLLOW_108_in_ruleWidgetOption8765); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getLABEL_BEFOREKeyword_14()); 
                        

                    }
                    break;
                case 16 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3899:2: kw= 'NO_LABEL'
                    {
                    kw=(Token)match(input,109,FOLLOW_109_in_ruleWidgetOption8784); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getNO_LABELKeyword_15()); 
                        

                    }
                    break;
                case 17 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3906:2: kw= 'SORTED'
                    {
                    kw=(Token)match(input,110,FOLLOW_110_in_ruleWidgetOption8803); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getSORTEDKeyword_16()); 
                        

                    }
                    break;
                case 18 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3913:2: kw= 'ADD_DEFAULT_VALUE'
                    {
                    kw=(Token)match(input,111,FOLLOW_111_in_ruleWidgetOption8822); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getADD_DEFAULT_VALUEKeyword_17()); 
                        

                    }
                    break;
                case 19 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3920:2: kw= 'NO_DEFAULT_VALUE'
                    {
                    kw=(Token)match(input,112,FOLLOW_112_in_ruleWidgetOption8841); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getNO_DEFAULT_VALUEKeyword_18()); 
                        

                    }
                    break;
                case 20 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3927:2: kw= 'BEGIN_COMPOSITE_4'
                    {
                    kw=(Token)match(input,113,FOLLOW_113_in_ruleWidgetOption8860); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getBEGIN_COMPOSITE_4Keyword_19()); 
                        

                    }
                    break;
                case 21 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3934:2: kw= 'BEGIN_COMPOSITE_6'
                    {
                    kw=(Token)match(input,114,FOLLOW_114_in_ruleWidgetOption8879); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getBEGIN_COMPOSITE_6Keyword_20()); 
                        

                    }
                    break;
                case 22 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3941:2: kw= 'BEGIN_COMPOSITE_8'
                    {
                    kw=(Token)match(input,115,FOLLOW_115_in_ruleWidgetOption8898); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getBEGIN_COMPOSITE_8Keyword_21()); 
                        

                    }
                    break;
                case 23 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3948:2: kw= 'BEGIN_COMPOSITE_10'
                    {
                    kw=(Token)match(input,116,FOLLOW_116_in_ruleWidgetOption8917); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getBEGIN_COMPOSITE_10Keyword_22()); 
                        

                    }
                    break;
                case 24 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3955:2: kw= 'END_COMPOSITE'
                    {
                    kw=(Token)match(input,117,FOLLOW_117_in_ruleWidgetOption8936); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getEND_COMPOSITEKeyword_23()); 
                        

                    }
                    break;
                case 25 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3962:2: kw= 'FILL_NONE'
                    {
                    kw=(Token)match(input,118,FOLLOW_118_in_ruleWidgetOption8955); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getFILL_NONEKeyword_24()); 
                        

                    }
                    break;
                case 26 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3969:2: kw= 'FILL_HORIZONTALLY'
                    {
                    kw=(Token)match(input,119,FOLLOW_119_in_ruleWidgetOption8974); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getFILL_HORIZONTALLYKeyword_25()); 
                        

                    }
                    break;
                case 27 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3976:2: kw= 'FILL_VERTICALLY'
                    {
                    kw=(Token)match(input,120,FOLLOW_120_in_ruleWidgetOption8993); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getFILL_VERTICALLYKeyword_26()); 
                        

                    }
                    break;
                case 28 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3983:2: kw= 'ALIGN_LEFT'
                    {
                    kw=(Token)match(input,121,FOLLOW_121_in_ruleWidgetOption9012); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getALIGN_LEFTKeyword_27()); 
                        

                    }
                    break;
                case 29 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3990:2: kw= 'ALIGN_RIGHT'
                    {
                    kw=(Token)match(input,122,FOLLOW_122_in_ruleWidgetOption9031); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getALIGN_RIGHTKeyword_28()); 
                        

                    }
                    break;
                case 30 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3997:2: kw= 'ALIGN_CENTER'
                    {
                    kw=(Token)match(input,123,FOLLOW_123_in_ruleWidgetOption9050); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getALIGN_CENTERKeyword_29()); 
                        

                    }
                    break;
                case 31 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4004:5: this_WIDGET_OPTION_NAME_30= ruleWIDGET_OPTION_NAME
                    {
                     
                            newCompositeNode(grammarAccess.getWidgetOptionAccess().getWIDGET_OPTION_NAMEParserRuleCall_30()); 
                        
                    pushFollow(FOLLOW_ruleWIDGET_OPTION_NAME_in_ruleWidgetOption9078);
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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4022:1: entryRulePAGE_TYPE_NAME returns [String current=null] : iv_rulePAGE_TYPE_NAME= rulePAGE_TYPE_NAME EOF ;
    public final String entryRulePAGE_TYPE_NAME() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_rulePAGE_TYPE_NAME = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4023:2: (iv_rulePAGE_TYPE_NAME= rulePAGE_TYPE_NAME EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4024:2: iv_rulePAGE_TYPE_NAME= rulePAGE_TYPE_NAME EOF
            {
             newCompositeNode(grammarAccess.getPAGE_TYPE_NAMERule()); 
            pushFollow(FOLLOW_rulePAGE_TYPE_NAME_in_entryRulePAGE_TYPE_NAME9124);
            iv_rulePAGE_TYPE_NAME=rulePAGE_TYPE_NAME();

            state._fsp--;

             current =iv_rulePAGE_TYPE_NAME.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRulePAGE_TYPE_NAME9135); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4031:1: rulePAGE_TYPE_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken rulePAGE_TYPE_NAME() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4034:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4035:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rulePAGE_TYPE_NAME9174); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4050:1: entryRulePageType returns [String current=null] : iv_rulePageType= rulePageType EOF ;
    public final String entryRulePageType() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_rulePageType = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4051:2: (iv_rulePageType= rulePageType EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4052:2: iv_rulePageType= rulePageType EOF
            {
             newCompositeNode(grammarAccess.getPageTypeRule()); 
            pushFollow(FOLLOW_rulePageType_in_entryRulePageType9219);
            iv_rulePageType=rulePageType();

            state._fsp--;

             current =iv_rulePageType.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRulePageType9230); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4059:1: rulePageType returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'Working' | kw= 'Completed' | kw= 'Cancelled' | this_PAGE_TYPE_NAME_3= rulePAGE_TYPE_NAME ) ;
    public final AntlrDatatypeRuleToken rulePageType() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        AntlrDatatypeRuleToken this_PAGE_TYPE_NAME_3 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4062:28: ( (kw= 'Working' | kw= 'Completed' | kw= 'Cancelled' | this_PAGE_TYPE_NAME_3= rulePAGE_TYPE_NAME ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4063:1: (kw= 'Working' | kw= 'Completed' | kw= 'Cancelled' | this_PAGE_TYPE_NAME_3= rulePAGE_TYPE_NAME )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4063:1: (kw= 'Working' | kw= 'Completed' | kw= 'Cancelled' | this_PAGE_TYPE_NAME_3= rulePAGE_TYPE_NAME )
            int alt91=4;
            switch ( input.LA(1) ) {
            case 124:
                {
                alt91=1;
                }
                break;
            case 125:
                {
                alt91=2;
                }
                break;
            case 126:
                {
                alt91=3;
                }
                break;
            case RULE_STRING:
                {
                alt91=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 91, 0, input);

                throw nvae;
            }

            switch (alt91) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4064:2: kw= 'Working'
                    {
                    kw=(Token)match(input,124,FOLLOW_124_in_rulePageType9268); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getPageTypeAccess().getWorkingKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4071:2: kw= 'Completed'
                    {
                    kw=(Token)match(input,125,FOLLOW_125_in_rulePageType9287); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getPageTypeAccess().getCompletedKeyword_1()); 
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4078:2: kw= 'Cancelled'
                    {
                    kw=(Token)match(input,126,FOLLOW_126_in_rulePageType9306); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getPageTypeAccess().getCancelledKeyword_2()); 
                        

                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4085:5: this_PAGE_TYPE_NAME_3= rulePAGE_TYPE_NAME
                    {
                     
                            newCompositeNode(grammarAccess.getPageTypeAccess().getPAGE_TYPE_NAMEParserRuleCall_3()); 
                        
                    pushFollow(FOLLOW_rulePAGE_TYPE_NAME_in_rulePageType9334);
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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4103:1: entryRuleCOLOR_NAME returns [String current=null] : iv_ruleCOLOR_NAME= ruleCOLOR_NAME EOF ;
    public final String entryRuleCOLOR_NAME() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleCOLOR_NAME = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4104:2: (iv_ruleCOLOR_NAME= ruleCOLOR_NAME EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4105:2: iv_ruleCOLOR_NAME= ruleCOLOR_NAME EOF
            {
             newCompositeNode(grammarAccess.getCOLOR_NAMERule()); 
            pushFollow(FOLLOW_ruleCOLOR_NAME_in_entryRuleCOLOR_NAME9380);
            iv_ruleCOLOR_NAME=ruleCOLOR_NAME();

            state._fsp--;

             current =iv_ruleCOLOR_NAME.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleCOLOR_NAME9391); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4112:1: ruleCOLOR_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleCOLOR_NAME() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4115:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4116:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleCOLOR_NAME9430); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4131:1: entryRuleStateColor returns [String current=null] : iv_ruleStateColor= ruleStateColor EOF ;
    public final String entryRuleStateColor() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleStateColor = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4132:2: (iv_ruleStateColor= ruleStateColor EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4133:2: iv_ruleStateColor= ruleStateColor EOF
            {
             newCompositeNode(grammarAccess.getStateColorRule()); 
            pushFollow(FOLLOW_ruleStateColor_in_entryRuleStateColor9475);
            iv_ruleStateColor=ruleStateColor();

            state._fsp--;

             current =iv_ruleStateColor.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleStateColor9486); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4140:1: ruleStateColor returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'BLACK' | kw= 'WHITE' | kw= 'RED' | kw= 'DARK_RED' | kw= 'GREEN' | kw= 'DARK_GREEN' | kw= 'YELLOW' | kw= 'DARK_YELLOW' | kw= 'BLUE' | kw= 'DARK_BLUE' | kw= 'MAGENTA' | kw= 'DARK_MAGENTA' | kw= 'CYAN' | kw= 'DARK_CYAN' | kw= 'GRAY' | kw= 'DARK_GRAY' | this_COLOR_NAME_16= ruleCOLOR_NAME ) ;
    public final AntlrDatatypeRuleToken ruleStateColor() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        AntlrDatatypeRuleToken this_COLOR_NAME_16 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4143:28: ( (kw= 'BLACK' | kw= 'WHITE' | kw= 'RED' | kw= 'DARK_RED' | kw= 'GREEN' | kw= 'DARK_GREEN' | kw= 'YELLOW' | kw= 'DARK_YELLOW' | kw= 'BLUE' | kw= 'DARK_BLUE' | kw= 'MAGENTA' | kw= 'DARK_MAGENTA' | kw= 'CYAN' | kw= 'DARK_CYAN' | kw= 'GRAY' | kw= 'DARK_GRAY' | this_COLOR_NAME_16= ruleCOLOR_NAME ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4144:1: (kw= 'BLACK' | kw= 'WHITE' | kw= 'RED' | kw= 'DARK_RED' | kw= 'GREEN' | kw= 'DARK_GREEN' | kw= 'YELLOW' | kw= 'DARK_YELLOW' | kw= 'BLUE' | kw= 'DARK_BLUE' | kw= 'MAGENTA' | kw= 'DARK_MAGENTA' | kw= 'CYAN' | kw= 'DARK_CYAN' | kw= 'GRAY' | kw= 'DARK_GRAY' | this_COLOR_NAME_16= ruleCOLOR_NAME )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4144:1: (kw= 'BLACK' | kw= 'WHITE' | kw= 'RED' | kw= 'DARK_RED' | kw= 'GREEN' | kw= 'DARK_GREEN' | kw= 'YELLOW' | kw= 'DARK_YELLOW' | kw= 'BLUE' | kw= 'DARK_BLUE' | kw= 'MAGENTA' | kw= 'DARK_MAGENTA' | kw= 'CYAN' | kw= 'DARK_CYAN' | kw= 'GRAY' | kw= 'DARK_GRAY' | this_COLOR_NAME_16= ruleCOLOR_NAME )
            int alt92=17;
            switch ( input.LA(1) ) {
            case 127:
                {
                alt92=1;
                }
                break;
            case 128:
                {
                alt92=2;
                }
                break;
            case 129:
                {
                alt92=3;
                }
                break;
            case 130:
                {
                alt92=4;
                }
                break;
            case 131:
                {
                alt92=5;
                }
                break;
            case 132:
                {
                alt92=6;
                }
                break;
            case 133:
                {
                alt92=7;
                }
                break;
            case 134:
                {
                alt92=8;
                }
                break;
            case 135:
                {
                alt92=9;
                }
                break;
            case 136:
                {
                alt92=10;
                }
                break;
            case 137:
                {
                alt92=11;
                }
                break;
            case 138:
                {
                alt92=12;
                }
                break;
            case 139:
                {
                alt92=13;
                }
                break;
            case 140:
                {
                alt92=14;
                }
                break;
            case 141:
                {
                alt92=15;
                }
                break;
            case 142:
                {
                alt92=16;
                }
                break;
            case RULE_STRING:
                {
                alt92=17;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 92, 0, input);

                throw nvae;
            }

            switch (alt92) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4145:2: kw= 'BLACK'
                    {
                    kw=(Token)match(input,127,FOLLOW_127_in_ruleStateColor9524); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getStateColorAccess().getBLACKKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4152:2: kw= 'WHITE'
                    {
                    kw=(Token)match(input,128,FOLLOW_128_in_ruleStateColor9543); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getStateColorAccess().getWHITEKeyword_1()); 
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4159:2: kw= 'RED'
                    {
                    kw=(Token)match(input,129,FOLLOW_129_in_ruleStateColor9562); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getStateColorAccess().getREDKeyword_2()); 
                        

                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4166:2: kw= 'DARK_RED'
                    {
                    kw=(Token)match(input,130,FOLLOW_130_in_ruleStateColor9581); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getStateColorAccess().getDARK_REDKeyword_3()); 
                        

                    }
                    break;
                case 5 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4173:2: kw= 'GREEN'
                    {
                    kw=(Token)match(input,131,FOLLOW_131_in_ruleStateColor9600); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getStateColorAccess().getGREENKeyword_4()); 
                        

                    }
                    break;
                case 6 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4180:2: kw= 'DARK_GREEN'
                    {
                    kw=(Token)match(input,132,FOLLOW_132_in_ruleStateColor9619); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getStateColorAccess().getDARK_GREENKeyword_5()); 
                        

                    }
                    break;
                case 7 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4187:2: kw= 'YELLOW'
                    {
                    kw=(Token)match(input,133,FOLLOW_133_in_ruleStateColor9638); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getStateColorAccess().getYELLOWKeyword_6()); 
                        

                    }
                    break;
                case 8 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4194:2: kw= 'DARK_YELLOW'
                    {
                    kw=(Token)match(input,134,FOLLOW_134_in_ruleStateColor9657); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getStateColorAccess().getDARK_YELLOWKeyword_7()); 
                        

                    }
                    break;
                case 9 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4201:2: kw= 'BLUE'
                    {
                    kw=(Token)match(input,135,FOLLOW_135_in_ruleStateColor9676); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getStateColorAccess().getBLUEKeyword_8()); 
                        

                    }
                    break;
                case 10 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4208:2: kw= 'DARK_BLUE'
                    {
                    kw=(Token)match(input,136,FOLLOW_136_in_ruleStateColor9695); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getStateColorAccess().getDARK_BLUEKeyword_9()); 
                        

                    }
                    break;
                case 11 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4215:2: kw= 'MAGENTA'
                    {
                    kw=(Token)match(input,137,FOLLOW_137_in_ruleStateColor9714); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getStateColorAccess().getMAGENTAKeyword_10()); 
                        

                    }
                    break;
                case 12 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4222:2: kw= 'DARK_MAGENTA'
                    {
                    kw=(Token)match(input,138,FOLLOW_138_in_ruleStateColor9733); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getStateColorAccess().getDARK_MAGENTAKeyword_11()); 
                        

                    }
                    break;
                case 13 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4229:2: kw= 'CYAN'
                    {
                    kw=(Token)match(input,139,FOLLOW_139_in_ruleStateColor9752); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getStateColorAccess().getCYANKeyword_12()); 
                        

                    }
                    break;
                case 14 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4236:2: kw= 'DARK_CYAN'
                    {
                    kw=(Token)match(input,140,FOLLOW_140_in_ruleStateColor9771); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getStateColorAccess().getDARK_CYANKeyword_13()); 
                        

                    }
                    break;
                case 15 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4243:2: kw= 'GRAY'
                    {
                    kw=(Token)match(input,141,FOLLOW_141_in_ruleStateColor9790); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getStateColorAccess().getGRAYKeyword_14()); 
                        

                    }
                    break;
                case 16 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4250:2: kw= 'DARK_GRAY'
                    {
                    kw=(Token)match(input,142,FOLLOW_142_in_ruleStateColor9809); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getStateColorAccess().getDARK_GRAYKeyword_15()); 
                        

                    }
                    break;
                case 17 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4257:5: this_COLOR_NAME_16= ruleCOLOR_NAME
                    {
                     
                            newCompositeNode(grammarAccess.getStateColorAccess().getCOLOR_NAMEParserRuleCall_16()); 
                        
                    pushFollow(FOLLOW_ruleCOLOR_NAME_in_ruleStateColor9837);
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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4275:1: ruleBooleanDef returns [Enumerator current=null] : ( (enumLiteral_0= 'None' ) | (enumLiteral_1= 'True' ) | (enumLiteral_2= 'False' ) ) ;
    public final Enumerator ruleBooleanDef() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;
        Token enumLiteral_2=null;

         enterRule(); 
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4277:28: ( ( (enumLiteral_0= 'None' ) | (enumLiteral_1= 'True' ) | (enumLiteral_2= 'False' ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4278:1: ( (enumLiteral_0= 'None' ) | (enumLiteral_1= 'True' ) | (enumLiteral_2= 'False' ) )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4278:1: ( (enumLiteral_0= 'None' ) | (enumLiteral_1= 'True' ) | (enumLiteral_2= 'False' ) )
            int alt93=3;
            switch ( input.LA(1) ) {
            case 79:
                {
                alt93=1;
                }
                break;
            case 143:
                {
                alt93=2;
                }
                break;
            case 144:
                {
                alt93=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 93, 0, input);

                throw nvae;
            }

            switch (alt93) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4278:2: (enumLiteral_0= 'None' )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4278:2: (enumLiteral_0= 'None' )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4278:4: enumLiteral_0= 'None'
                    {
                    enumLiteral_0=(Token)match(input,79,FOLLOW_79_in_ruleBooleanDef9896); 

                            current = grammarAccess.getBooleanDefAccess().getNoneEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_0, grammarAccess.getBooleanDefAccess().getNoneEnumLiteralDeclaration_0()); 
                        

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4284:6: (enumLiteral_1= 'True' )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4284:6: (enumLiteral_1= 'True' )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4284:8: enumLiteral_1= 'True'
                    {
                    enumLiteral_1=(Token)match(input,143,FOLLOW_143_in_ruleBooleanDef9913); 

                            current = grammarAccess.getBooleanDefAccess().getTrueEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_1, grammarAccess.getBooleanDefAccess().getTrueEnumLiteralDeclaration_1()); 
                        

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4290:6: (enumLiteral_2= 'False' )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4290:6: (enumLiteral_2= 'False' )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4290:8: enumLiteral_2= 'False'
                    {
                    enumLiteral_2=(Token)match(input,144,FOLLOW_144_in_ruleBooleanDef9930); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4300:1: ruleWorkflowEventType returns [Enumerator current=null] : ( (enumLiteral_0= 'TransitionTo' ) | (enumLiteral_1= 'CreateBranch' ) | (enumLiteral_2= 'CommitBranch' ) ) ;
    public final Enumerator ruleWorkflowEventType() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;
        Token enumLiteral_2=null;

         enterRule(); 
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4302:28: ( ( (enumLiteral_0= 'TransitionTo' ) | (enumLiteral_1= 'CreateBranch' ) | (enumLiteral_2= 'CommitBranch' ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4303:1: ( (enumLiteral_0= 'TransitionTo' ) | (enumLiteral_1= 'CreateBranch' ) | (enumLiteral_2= 'CommitBranch' ) )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4303:1: ( (enumLiteral_0= 'TransitionTo' ) | (enumLiteral_1= 'CreateBranch' ) | (enumLiteral_2= 'CommitBranch' ) )
            int alt94=3;
            switch ( input.LA(1) ) {
            case 145:
                {
                alt94=1;
                }
                break;
            case 146:
                {
                alt94=2;
                }
                break;
            case 147:
                {
                alt94=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 94, 0, input);

                throw nvae;
            }

            switch (alt94) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4303:2: (enumLiteral_0= 'TransitionTo' )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4303:2: (enumLiteral_0= 'TransitionTo' )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4303:4: enumLiteral_0= 'TransitionTo'
                    {
                    enumLiteral_0=(Token)match(input,145,FOLLOW_145_in_ruleWorkflowEventType9975); 

                            current = grammarAccess.getWorkflowEventTypeAccess().getTransitionToEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_0, grammarAccess.getWorkflowEventTypeAccess().getTransitionToEnumLiteralDeclaration_0()); 
                        

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4309:6: (enumLiteral_1= 'CreateBranch' )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4309:6: (enumLiteral_1= 'CreateBranch' )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4309:8: enumLiteral_1= 'CreateBranch'
                    {
                    enumLiteral_1=(Token)match(input,146,FOLLOW_146_in_ruleWorkflowEventType9992); 

                            current = grammarAccess.getWorkflowEventTypeAccess().getCreateBranchEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_1, grammarAccess.getWorkflowEventTypeAccess().getCreateBranchEnumLiteralDeclaration_1()); 
                        

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4315:6: (enumLiteral_2= 'CommitBranch' )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4315:6: (enumLiteral_2= 'CommitBranch' )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4315:8: enumLiteral_2= 'CommitBranch'
                    {
                    enumLiteral_2=(Token)match(input,147,FOLLOW_147_in_ruleWorkflowEventType10009); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4325:1: ruleReviewBlockingType returns [Enumerator current=null] : ( (enumLiteral_0= 'Transition' ) | (enumLiteral_1= 'Commit' ) ) ;
    public final Enumerator ruleReviewBlockingType() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;

         enterRule(); 
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4327:28: ( ( (enumLiteral_0= 'Transition' ) | (enumLiteral_1= 'Commit' ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4328:1: ( (enumLiteral_0= 'Transition' ) | (enumLiteral_1= 'Commit' ) )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4328:1: ( (enumLiteral_0= 'Transition' ) | (enumLiteral_1= 'Commit' ) )
            int alt95=2;
            int LA95_0 = input.LA(1);

            if ( (LA95_0==148) ) {
                alt95=1;
            }
            else if ( (LA95_0==149) ) {
                alt95=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 95, 0, input);

                throw nvae;
            }
            switch (alt95) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4328:2: (enumLiteral_0= 'Transition' )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4328:2: (enumLiteral_0= 'Transition' )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4328:4: enumLiteral_0= 'Transition'
                    {
                    enumLiteral_0=(Token)match(input,148,FOLLOW_148_in_ruleReviewBlockingType10054); 

                            current = grammarAccess.getReviewBlockingTypeAccess().getTransitionEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_0, grammarAccess.getReviewBlockingTypeAccess().getTransitionEnumLiteralDeclaration_0()); 
                        

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4334:6: (enumLiteral_1= 'Commit' )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4334:6: (enumLiteral_1= 'Commit' )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:4334:8: enumLiteral_1= 'Commit'
                    {
                    enumLiteral_1=(Token)match(input,149,FOLLOW_149_in_ruleReviewBlockingType10071); 

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
    public static final BitSet FOLLOW_ruleWorkDef_in_ruleAtsDsl144 = new BitSet(new long[]{0x0000000000007002L});
    public static final BitSet FOLLOW_12_in_ruleAtsDsl159 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleUserDef_in_ruleAtsDsl180 = new BitSet(new long[]{0x0000000000007002L});
    public static final BitSet FOLLOW_13_in_ruleAtsDsl195 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleTeamDef_in_ruleAtsDsl216 = new BitSet(new long[]{0x0000000000006002L});
    public static final BitSet FOLLOW_14_in_ruleAtsDsl231 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleActionableItemDef_in_ruleAtsDsl252 = new BitSet(new long[]{0x0000000000004002L});
    public static final BitSet FOLLOW_ruleUSER_DEF_REFERENCE_in_entryRuleUSER_DEF_REFERENCE291 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleUSER_DEF_REFERENCE302 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleUSER_DEF_REFERENCE341 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleUserDef_in_entryRuleUserDef385 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleUserDef395 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleUSER_DEF_REFERENCE_in_ruleUserDef441 = new BitSet(new long[]{0x0000000000008012L,0x0000000000004000L});
    public static final BitSet FOLLOW_ruleUserDefOption_in_ruleUserDef462 = new BitSet(new long[]{0x0000000000008012L,0x0000000000004000L});
    public static final BitSet FOLLOW_15_in_ruleUserDef476 = new BitSet(new long[]{0x00000000001F0000L});
    public static final BitSet FOLLOW_16_in_ruleUserDef489 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L,0x0000000000018000L});
    public static final BitSet FOLLOW_ruleBooleanDef_in_ruleUserDef510 = new BitSet(new long[]{0x00000000001E0000L});
    public static final BitSet FOLLOW_17_in_ruleUserDef525 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleUserDef542 = new BitSet(new long[]{0x00000000001C0000L});
    public static final BitSet FOLLOW_18_in_ruleUserDef562 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleUserDef579 = new BitSet(new long[]{0x0000000000180000L});
    public static final BitSet FOLLOW_19_in_ruleUserDef599 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L,0x0000000000018000L});
    public static final BitSet FOLLOW_ruleBooleanDef_in_ruleUserDef620 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_ruleUserDef634 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTEAM_DEF_REFERENCE_in_entryRuleTEAM_DEF_REFERENCE673 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleTEAM_DEF_REFERENCE684 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleTEAM_DEF_REFERENCE723 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTeamDef_in_entryRuleTeamDef767 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleTeamDef777 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTEAM_DEF_REFERENCE_in_ruleTeamDef823 = new BitSet(new long[]{0x0000000000008010L,0x0000000000004000L});
    public static final BitSet FOLLOW_ruleTeamDefOption_in_ruleTeamDef844 = new BitSet(new long[]{0x0000000000008010L,0x0000000000004000L});
    public static final BitSet FOLLOW_15_in_ruleTeamDef857 = new BitSet(new long[]{0x000000003FF10800L});
    public static final BitSet FOLLOW_21_in_ruleTeamDef870 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleTeamDef887 = new BitSet(new long[]{0x000000003FD10800L});
    public static final BitSet FOLLOW_16_in_ruleTeamDef907 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L,0x0000000000018000L});
    public static final BitSet FOLLOW_ruleBooleanDef_in_ruleTeamDef928 = new BitSet(new long[]{0x000000003FD00800L});
    public static final BitSet FOLLOW_22_in_ruleTeamDef943 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleTeamDef960 = new BitSet(new long[]{0x000000003FD00800L});
    public static final BitSet FOLLOW_23_in_ruleTeamDef980 = new BitSet(new long[]{0x0000000000020000L,0x0000000000000100L});
    public static final BitSet FOLLOW_ruleUserRef_in_ruleTeamDef1001 = new BitSet(new long[]{0x000000003F900800L});
    public static final BitSet FOLLOW_24_in_ruleTeamDef1016 = new BitSet(new long[]{0x0000000000020000L,0x0000000000000100L});
    public static final BitSet FOLLOW_ruleUserRef_in_ruleTeamDef1037 = new BitSet(new long[]{0x000000003F100800L});
    public static final BitSet FOLLOW_25_in_ruleTeamDef1052 = new BitSet(new long[]{0x0000000000020000L,0x0000000000000100L});
    public static final BitSet FOLLOW_ruleUserRef_in_ruleTeamDef1073 = new BitSet(new long[]{0x000000003E100800L});
    public static final BitSet FOLLOW_11_in_ruleTeamDef1088 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleTeamDef1105 = new BitSet(new long[]{0x000000003C100000L});
    public static final BitSet FOLLOW_26_in_ruleTeamDef1125 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleTeamDef1142 = new BitSet(new long[]{0x0000000038100000L});
    public static final BitSet FOLLOW_27_in_ruleTeamDef1162 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleTeamDef1179 = new BitSet(new long[]{0x0000000038100000L});
    public static final BitSet FOLLOW_28_in_ruleTeamDef1199 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleVersionDef_in_ruleTeamDef1220 = new BitSet(new long[]{0x0000000030100000L});
    public static final BitSet FOLLOW_29_in_ruleTeamDef1235 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_ruleTeamDef1247 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_13_in_ruleTeamDef1260 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleTeamDef_in_ruleTeamDef1281 = new BitSet(new long[]{0x0000000000102000L});
    public static final BitSet FOLLOW_20_in_ruleTeamDef1295 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_ruleTeamDef1309 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAI_DEF_REFERENCE_in_entryRuleAI_DEF_REFERENCE1346 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAI_DEF_REFERENCE1357 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAI_DEF_REFERENCE1396 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleActionableItemDef_in_entryRuleActionableItemDef1440 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleActionableItemDef1450 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAI_DEF_REFERENCE_in_ruleActionableItemDef1496 = new BitSet(new long[]{0x0000000000008012L,0x0000000000004000L});
    public static final BitSet FOLLOW_ruleActionableItemOption_in_ruleActionableItemDef1517 = new BitSet(new long[]{0x0000000000008012L,0x0000000000004000L});
    public static final BitSet FOLLOW_15_in_ruleActionableItemDef1531 = new BitSet(new long[]{0x00000001E8F10000L});
    public static final BitSet FOLLOW_21_in_ruleActionableItemDef1544 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleActionableItemDef1561 = new BitSet(new long[]{0x00000001E8D10000L});
    public static final BitSet FOLLOW_16_in_ruleActionableItemDef1581 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L,0x0000000000018000L});
    public static final BitSet FOLLOW_ruleBooleanDef_in_ruleActionableItemDef1602 = new BitSet(new long[]{0x00000001E8D00000L});
    public static final BitSet FOLLOW_30_in_ruleActionableItemDef1617 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L,0x0000000000018000L});
    public static final BitSet FOLLOW_ruleBooleanDef_in_ruleActionableItemDef1638 = new BitSet(new long[]{0x00000001A8D00000L});
    public static final BitSet FOLLOW_23_in_ruleActionableItemDef1653 = new BitSet(new long[]{0x0000000000020000L,0x0000000000000100L});
    public static final BitSet FOLLOW_ruleUserRef_in_ruleActionableItemDef1674 = new BitSet(new long[]{0x00000001A8D00000L});
    public static final BitSet FOLLOW_31_in_ruleActionableItemDef1689 = new BitSet(new long[]{0x0000000000020000L,0x0000000000000100L});
    public static final BitSet FOLLOW_ruleUserRef_in_ruleActionableItemDef1710 = new BitSet(new long[]{0x00000001A8500000L});
    public static final BitSet FOLLOW_22_in_ruleActionableItemDef1725 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleActionableItemDef1742 = new BitSet(new long[]{0x0000000128500000L});
    public static final BitSet FOLLOW_32_in_ruleActionableItemDef1762 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleActionableItemDef1779 = new BitSet(new long[]{0x0000000028100000L});
    public static final BitSet FOLLOW_27_in_ruleActionableItemDef1799 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleActionableItemDef1816 = new BitSet(new long[]{0x0000000028100000L});
    public static final BitSet FOLLOW_29_in_ruleActionableItemDef1836 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_ruleActionableItemDef1848 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_14_in_ruleActionableItemDef1861 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleActionableItemDef_in_ruleActionableItemDef1882 = new BitSet(new long[]{0x0000000000104000L});
    public static final BitSet FOLLOW_20_in_ruleActionableItemDef1896 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_ruleActionableItemDef1910 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleVERSION_DEF_REFERENCE_in_entryRuleVERSION_DEF_REFERENCE1949 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleVERSION_DEF_REFERENCE1960 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleVERSION_DEF_REFERENCE1999 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleVersionDef_in_entryRuleVersionDef2043 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleVersionDef2053 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleVERSION_DEF_REFERENCE_in_ruleVersionDef2099 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_ruleVersionDef2111 = new BitSet(new long[]{0x0000007E00700000L});
    public static final BitSet FOLLOW_21_in_ruleVersionDef2124 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleVersionDef2141 = new BitSet(new long[]{0x0000007E00500000L});
    public static final BitSet FOLLOW_22_in_ruleVersionDef2161 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleVersionDef2178 = new BitSet(new long[]{0x0000007E00500000L});
    public static final BitSet FOLLOW_33_in_ruleVersionDef2198 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L,0x0000000000018000L});
    public static final BitSet FOLLOW_ruleBooleanDef_in_ruleVersionDef2219 = new BitSet(new long[]{0x0000007C00100000L});
    public static final BitSet FOLLOW_34_in_ruleVersionDef2234 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L,0x0000000000018000L});
    public static final BitSet FOLLOW_ruleBooleanDef_in_ruleVersionDef2255 = new BitSet(new long[]{0x0000007800100000L});
    public static final BitSet FOLLOW_35_in_ruleVersionDef2270 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L,0x0000000000018000L});
    public static final BitSet FOLLOW_ruleBooleanDef_in_ruleVersionDef2291 = new BitSet(new long[]{0x0000007000100000L});
    public static final BitSet FOLLOW_36_in_ruleVersionDef2306 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L,0x0000000000018000L});
    public static final BitSet FOLLOW_ruleBooleanDef_in_ruleVersionDef2327 = new BitSet(new long[]{0x0000006000100000L});
    public static final BitSet FOLLOW_37_in_ruleVersionDef2342 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleVersionDef2359 = new BitSet(new long[]{0x0000004000100000L});
    public static final BitSet FOLLOW_38_in_ruleVersionDef2379 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleVersionDef2396 = new BitSet(new long[]{0x0000004000100000L});
    public static final BitSet FOLLOW_20_in_ruleVersionDef2415 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleWorkDef_in_entryRuleWorkDef2451 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleWorkDef2461 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleWORK_DEFINITION_NAME_REFERENCE_in_ruleWorkDef2507 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_ruleWorkDef2519 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_39_in_ruleWorkDef2532 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleWorkDef2549 = new BitSet(new long[]{0x0000018000000000L});
    public static final BitSet FOLLOW_40_in_ruleWorkDef2569 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleWorkDef2592 = new BitSet(new long[]{0x2020020000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_ruleWidgetDef_in_ruleWorkDef2614 = new BitSet(new long[]{0x2020020000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_ruleDecisionReviewDef_in_ruleWorkDef2636 = new BitSet(new long[]{0x2020020000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_rulePeerReviewDef_in_ruleWorkDef2658 = new BitSet(new long[]{0x2020020000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_ruleStateDef_in_ruleWorkDef2680 = new BitSet(new long[]{0x2020020000100000L,0x0000000000000020L});
    public static final BitSet FOLLOW_20_in_ruleWorkDef2693 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleWidgetDef_in_entryRuleWidgetDef2729 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleWidgetDef2739 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_ruleWidgetDef2776 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleWIDGET_NAME_REFERENCE_in_ruleWidgetDef2797 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_ruleWidgetDef2809 = new BitSet(new long[]{0x0003FC0000100000L});
    public static final BitSet FOLLOW_42_in_ruleWidgetDef2822 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleWidgetDef2839 = new BitSet(new long[]{0x0003F80000100000L});
    public static final BitSet FOLLOW_43_in_ruleWidgetDef2859 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleWidgetDef2876 = new BitSet(new long[]{0x0003F00000100000L});
    public static final BitSet FOLLOW_44_in_ruleWidgetDef2896 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleWidgetDef2913 = new BitSet(new long[]{0x0003E00000100000L});
    public static final BitSet FOLLOW_45_in_ruleWidgetDef2933 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleWidgetDef2950 = new BitSet(new long[]{0x0003C00000100000L});
    public static final BitSet FOLLOW_46_in_ruleWidgetDef2970 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_RULE_INT_in_ruleWidgetDef2987 = new BitSet(new long[]{0x0003800000100000L});
    public static final BitSet FOLLOW_47_in_ruleWidgetDef3007 = new BitSet(new long[]{0x0000000000000010L,0x0FFFFFFFC0000000L});
    public static final BitSet FOLLOW_ruleWidgetOption_in_ruleWidgetDef3028 = new BitSet(new long[]{0x0003800000100000L});
    public static final BitSet FOLLOW_48_in_ruleWidgetDef3043 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleWidgetDef3060 = new BitSet(new long[]{0x0002000000100000L});
    public static final BitSet FOLLOW_49_in_ruleWidgetDef3080 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleWidgetDef3097 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_ruleWidgetDef3116 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleWidgetRef_in_entryRuleWidgetRef3152 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleWidgetRef3162 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_50_in_ruleWidgetRef3199 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleWIDGET_NAME_REFERENCE_in_ruleWidgetRef3222 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttrWidget_in_entryRuleAttrWidget3258 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttrWidget3268 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_51_in_ruleAttrWidget3305 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAttrWidget3322 = new BitSet(new long[]{0x0010000000000002L});
    public static final BitSet FOLLOW_52_in_ruleAttrWidget3340 = new BitSet(new long[]{0x0000000000000010L,0x0FFFFFFFC0000000L});
    public static final BitSet FOLLOW_ruleWidgetOption_in_ruleAttrWidget3361 = new BitSet(new long[]{0x0010000000000002L});
    public static final BitSet FOLLOW_ruleStateDef_in_entryRuleStateDef3399 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleStateDef3409 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_53_in_ruleStateDef3446 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleStateDef3467 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_ruleStateDef3479 = new BitSet(new long[]{0x0040080000000000L});
    public static final BitSet FOLLOW_43_in_ruleStateDef3492 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleStateDef3509 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_54_in_ruleStateDef3528 = new BitSet(new long[]{0x0000000000000010L,0x7000000000000000L});
    public static final BitSet FOLLOW_rulePageType_in_ruleStateDef3549 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_55_in_ruleStateDef3561 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_RULE_INT_in_ruleStateDef3578 = new BitSet(new long[]{0x1F00000000100000L,0x0000000000000E10L});
    public static final BitSet FOLLOW_ruleToState_in_ruleStateDef3604 = new BitSet(new long[]{0x1F00000000100000L,0x0000000000000E10L});
    public static final BitSet FOLLOW_56_in_ruleStateDef3618 = new BitSet(new long[]{0x0000000000000010L,0x000000003FFC0000L});
    public static final BitSet FOLLOW_ruleRule_in_ruleStateDef3639 = new BitSet(new long[]{0x1F00000000100000L,0x0000000000000C10L});
    public static final BitSet FOLLOW_ruleDecisionReviewRef_in_ruleStateDef3662 = new BitSet(new long[]{0x1E00000000100000L,0x0000000000000C10L});
    public static final BitSet FOLLOW_rulePeerReviewRef_in_ruleStateDef3684 = new BitSet(new long[]{0x0E00000000100000L,0x0000000000000C10L});
    public static final BitSet FOLLOW_57_in_ruleStateDef3698 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_RULE_INT_in_ruleStateDef3715 = new BitSet(new long[]{0x0C00000000100000L,0x0000000000000C00L});
    public static final BitSet FOLLOW_58_in_ruleStateDef3735 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_RULE_INT_in_ruleStateDef3752 = new BitSet(new long[]{0x0800000000100000L,0x0000000000000C00L});
    public static final BitSet FOLLOW_59_in_ruleStateDef3772 = new BitSet(new long[]{0x0000000000000010L,0x8000000000000000L,0x0000000000007FFFL});
    public static final BitSet FOLLOW_ruleStateColor_in_ruleStateDef3793 = new BitSet(new long[]{0x0000000000100000L,0x0000000000000C00L});
    public static final BitSet FOLLOW_ruleLayoutType_in_ruleStateDef3816 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_ruleStateDef3829 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDecisionReviewRef_in_entryRuleDecisionReviewRef3865 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleDecisionReviewRef3875 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_60_in_ruleDecisionReviewRef3912 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleDECISION_REVIEW_NAME_REFERENCE_in_ruleDecisionReviewRef3935 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDecisionReviewDef_in_entryRuleDecisionReviewDef3971 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleDecisionReviewDef3981 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_61_in_ruleDecisionReviewDef4018 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleDECISION_REVIEW_NAME_REFERENCE_in_ruleDecisionReviewDef4039 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_ruleDecisionReviewDef4051 = new BitSet(new long[]{0x4000000000000000L});
    public static final BitSet FOLLOW_62_in_ruleDecisionReviewDef4063 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleDecisionReviewDef4080 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_43_in_ruleDecisionReviewDef4097 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleDecisionReviewDef4114 = new BitSet(new long[]{0x8000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_63_in_ruleDecisionReviewDef4132 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleDecisionReviewDef4155 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_64_in_ruleDecisionReviewDef4169 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000300000L});
    public static final BitSet FOLLOW_ruleReviewBlockingType_in_ruleDecisionReviewDef4190 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_ruleDecisionReviewDef4202 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x00000000000E0000L});
    public static final BitSet FOLLOW_ruleWorkflowEventType_in_ruleDecisionReviewDef4223 = new BitSet(new long[]{0x0000800000000000L,0x000000000000000CL});
    public static final BitSet FOLLOW_66_in_ruleDecisionReviewDef4236 = new BitSet(new long[]{0x0000000000020000L,0x0000000000000100L});
    public static final BitSet FOLLOW_ruleUserRef_in_ruleDecisionReviewDef4257 = new BitSet(new long[]{0x0000800000000000L,0x000000000000000CL});
    public static final BitSet FOLLOW_67_in_ruleDecisionReviewDef4272 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L,0x0000000000018000L});
    public static final BitSet FOLLOW_ruleBooleanDef_in_ruleDecisionReviewDef4293 = new BitSet(new long[]{0x0000800000000000L,0x000000000000000CL});
    public static final BitSet FOLLOW_ruleDecisionReviewOpt_in_ruleDecisionReviewDef4316 = new BitSet(new long[]{0x0000800000100000L,0x000000000000000CL});
    public static final BitSet FOLLOW_20_in_ruleDecisionReviewDef4329 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDECISION_REVIEW_OPT_REF_in_entryRuleDECISION_REVIEW_OPT_REF4366 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleDECISION_REVIEW_OPT_REF4377 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleDECISION_REVIEW_OPT_REF4416 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDecisionReviewOpt_in_entryRuleDecisionReviewOpt4460 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleDecisionReviewOpt4470 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_ruleDecisionReviewOpt4507 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleDECISION_REVIEW_OPT_REF_in_ruleDecisionReviewOpt4528 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_ruleFollowupRef_in_ruleDecisionReviewOpt4549 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rulePeerReviewRef_in_entryRulePeerReviewRef4586 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRulePeerReviewRef4596 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_68_in_rulePeerReviewRef4633 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rulePEER_REVIEW_NAME_REFERENCE_in_rulePeerReviewRef4656 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rulePeerReviewDef_in_entryRulePeerReviewDef4692 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRulePeerReviewDef4702 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_69_in_rulePeerReviewDef4739 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rulePEER_REVIEW_NAME_REFERENCE_in_rulePeerReviewDef4760 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_rulePeerReviewDef4772 = new BitSet(new long[]{0x4000080000000000L});
    public static final BitSet FOLLOW_62_in_rulePeerReviewDef4785 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_rulePeerReviewDef4802 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_43_in_rulePeerReviewDef4821 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_rulePeerReviewDef4838 = new BitSet(new long[]{0x8000000000000000L,0x0000000000000041L});
    public static final BitSet FOLLOW_70_in_rulePeerReviewDef4856 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_rulePeerReviewDef4873 = new BitSet(new long[]{0x8000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_63_in_rulePeerReviewDef4893 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleSTATE_NAME_REFERENCE_in_rulePeerReviewDef4916 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_64_in_rulePeerReviewDef4930 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000300000L});
    public static final BitSet FOLLOW_ruleReviewBlockingType_in_rulePeerReviewDef4951 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_rulePeerReviewDef4963 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x00000000000E0000L});
    public static final BitSet FOLLOW_ruleWorkflowEventType_in_rulePeerReviewDef4984 = new BitSet(new long[]{0x0000000000100000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_rulePeerReviewDef4997 = new BitSet(new long[]{0x0000000000020000L,0x0000000000000100L});
    public static final BitSet FOLLOW_ruleUserRef_in_rulePeerReviewDef5018 = new BitSet(new long[]{0x0000000000100000L,0x0000000000000004L});
    public static final BitSet FOLLOW_20_in_rulePeerReviewDef5032 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleFollowupRef_in_entryRuleFollowupRef5068 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleFollowupRef5078 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_71_in_ruleFollowupRef5115 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_ruleFollowupRef5128 = new BitSet(new long[]{0x0000000000020000L,0x0000000000000100L});
    public static final BitSet FOLLOW_ruleUserRef_in_ruleFollowupRef5149 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_ruleUserRef_in_entryRuleUserRef5187 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleUserRef5197 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleUserByUserId_in_ruleUserRef5244 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleUserByName_in_ruleUserRef5271 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleUserByUserId_in_entryRuleUserByUserId5306 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleUserByUserId5316 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_17_in_ruleUserByUserId5353 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleUserByUserId5370 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleUserByName_in_entryRuleUserByName5411 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleUserByName5421 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_72_in_ruleUserByName5458 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleUserByName5475 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDECISION_REVIEW_NAME_REFERENCE_in_entryRuleDECISION_REVIEW_NAME_REFERENCE5517 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleDECISION_REVIEW_NAME_REFERENCE5528 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleDECISION_REVIEW_NAME_REFERENCE5567 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rulePEER_REVIEW_NAME_REFERENCE_in_entryRulePEER_REVIEW_NAME_REFERENCE5612 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRulePEER_REVIEW_NAME_REFERENCE5623 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rulePEER_REVIEW_NAME_REFERENCE5662 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleSTATE_NAME_REFERENCE_in_entryRuleSTATE_NAME_REFERENCE5707 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleSTATE_NAME_REFERENCE5718 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleSTATE_NAME_REFERENCE5757 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleWIDGET_NAME_REFERENCE_in_entryRuleWIDGET_NAME_REFERENCE5802 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleWIDGET_NAME_REFERENCE5813 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleWIDGET_NAME_REFERENCE5852 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleWORK_DEFINITION_NAME_REFERENCE_in_entryRuleWORK_DEFINITION_NAME_REFERENCE5897 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleWORK_DEFINITION_NAME_REFERENCE5908 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleWORK_DEFINITION_NAME_REFERENCE5947 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleToState_in_entryRuleToState5991 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleToState6001 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_73_in_ruleToState6038 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleToState6061 = new BitSet(new long[]{0x0000000000000012L,0x0000000000030000L});
    public static final BitSet FOLLOW_ruleTransitionOption_in_ruleToState6082 = new BitSet(new long[]{0x0000000000000012L,0x0000000000030000L});
    public static final BitSet FOLLOW_ruleLayoutType_in_entryRuleLayoutType6119 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleLayoutType6129 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleLayoutDef_in_ruleLayoutType6176 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleLayoutCopy_in_ruleLayoutType6203 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleLayoutDef_in_entryRuleLayoutDef6238 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleLayoutDef6248 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_74_in_ruleLayoutDef6285 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_ruleLayoutDef6297 = new BitSet(new long[]{0x000C000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_ruleLayoutItem_in_ruleLayoutDef6318 = new BitSet(new long[]{0x000C000000100000L,0x0000000000001000L});
    public static final BitSet FOLLOW_20_in_ruleLayoutDef6331 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleLayoutCopy_in_entryRuleLayoutCopy6367 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleLayoutCopy6377 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_75_in_ruleLayoutCopy6414 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleLayoutCopy6437 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleLayoutItem_in_entryRuleLayoutItem6473 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleLayoutItem6483 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleWidgetRef_in_ruleLayoutItem6530 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttrWidget_in_ruleLayoutItem6557 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleComposite_in_ruleLayoutItem6584 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleComposite_in_entryRuleComposite6619 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleComposite6629 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_76_in_ruleComposite6666 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_ruleComposite6678 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_77_in_ruleComposite6690 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_RULE_INT_in_ruleComposite6707 = new BitSet(new long[]{0x000C000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_ruleLayoutItem_in_ruleComposite6733 = new BitSet(new long[]{0x000C800000100000L,0x0000000000001000L});
    public static final BitSet FOLLOW_47_in_ruleComposite6747 = new BitSet(new long[]{0x0000000000000010L,0x0000000000008000L});
    public static final BitSet FOLLOW_ruleCompositeOption_in_ruleComposite6768 = new BitSet(new long[]{0x0000800000100000L});
    public static final BitSet FOLLOW_20_in_ruleComposite6782 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleUSER_DEF_OPTION_NAME_in_entryRuleUSER_DEF_OPTION_NAME6819 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleUSER_DEF_OPTION_NAME6830 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleUSER_DEF_OPTION_NAME6869 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleUserDefOption_in_entryRuleUserDefOption6914 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleUserDefOption6925 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_78_in_ruleUserDefOption6963 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleUSER_DEF_OPTION_NAME_in_ruleUserDefOption6991 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTEAM_DEF_OPTION_NAME_in_entryRuleTEAM_DEF_OPTION_NAME7037 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleTEAM_DEF_OPTION_NAME7048 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleTEAM_DEF_OPTION_NAME7087 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTeamDefOption_in_entryRuleTeamDefOption7132 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleTeamDefOption7143 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_78_in_ruleTeamDefOption7181 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTEAM_DEF_OPTION_NAME_in_ruleTeamDefOption7209 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAI_DEF_OPTION_NAME_in_entryRuleAI_DEF_OPTION_NAME7255 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAI_DEF_OPTION_NAME7266 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAI_DEF_OPTION_NAME7305 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleActionableItemOption_in_entryRuleActionableItemOption7350 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleActionableItemOption7361 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_78_in_ruleActionableItemOption7399 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAI_DEF_OPTION_NAME_in_ruleActionableItemOption7427 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleCOMPOSITE_OPTION_NAME_in_entryRuleCOMPOSITE_OPTION_NAME7473 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleCOMPOSITE_OPTION_NAME7484 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleCOMPOSITE_OPTION_NAME7523 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleCompositeOption_in_entryRuleCompositeOption7568 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleCompositeOption7579 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_79_in_ruleCompositeOption7617 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleCOMPOSITE_OPTION_NAME_in_ruleCompositeOption7645 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTRANSITION_OPTION_NAME_in_entryRuleTRANSITION_OPTION_NAME7691 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleTRANSITION_OPTION_NAME7702 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleTRANSITION_OPTION_NAME7741 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTransitionOption_in_entryRuleTransitionOption7786 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleTransitionOption7797 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_80_in_ruleTransitionOption7835 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_81_in_ruleTransitionOption7854 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTRANSITION_OPTION_NAME_in_ruleTransitionOption7882 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRULE_NAME_in_entryRuleRULE_NAME7928 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRULE_NAME7939 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleRULE_NAME7978 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRule_in_entryRuleRule8023 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRule8034 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_82_in_ruleRule8072 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_83_in_ruleRule8091 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_84_in_ruleRule8110 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_85_in_ruleRule8129 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_86_in_ruleRule8148 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_87_in_ruleRule8167 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_88_in_ruleRule8186 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_89_in_ruleRule8205 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_90_in_ruleRule8224 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_91_in_ruleRule8243 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_92_in_ruleRule8262 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_93_in_ruleRule8281 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRULE_NAME_in_ruleRule8309 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleWIDGET_OPTION_NAME_in_entryRuleWIDGET_OPTION_NAME8355 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleWIDGET_OPTION_NAME8366 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleWIDGET_OPTION_NAME8405 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleWidgetOption_in_entryRuleWidgetOption8450 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleWidgetOption8461 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_94_in_ruleWidgetOption8499 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_95_in_ruleWidgetOption8518 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_96_in_ruleWidgetOption8537 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_97_in_ruleWidgetOption8556 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_98_in_ruleWidgetOption8575 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_99_in_ruleWidgetOption8594 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_100_in_ruleWidgetOption8613 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_101_in_ruleWidgetOption8632 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_102_in_ruleWidgetOption8651 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_103_in_ruleWidgetOption8670 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_104_in_ruleWidgetOption8689 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_105_in_ruleWidgetOption8708 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_106_in_ruleWidgetOption8727 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_107_in_ruleWidgetOption8746 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_108_in_ruleWidgetOption8765 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_109_in_ruleWidgetOption8784 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_110_in_ruleWidgetOption8803 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_111_in_ruleWidgetOption8822 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_112_in_ruleWidgetOption8841 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_113_in_ruleWidgetOption8860 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_114_in_ruleWidgetOption8879 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_115_in_ruleWidgetOption8898 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_116_in_ruleWidgetOption8917 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_117_in_ruleWidgetOption8936 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_118_in_ruleWidgetOption8955 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_119_in_ruleWidgetOption8974 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_120_in_ruleWidgetOption8993 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_121_in_ruleWidgetOption9012 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_122_in_ruleWidgetOption9031 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_123_in_ruleWidgetOption9050 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleWIDGET_OPTION_NAME_in_ruleWidgetOption9078 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rulePAGE_TYPE_NAME_in_entryRulePAGE_TYPE_NAME9124 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRulePAGE_TYPE_NAME9135 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rulePAGE_TYPE_NAME9174 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rulePageType_in_entryRulePageType9219 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRulePageType9230 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_124_in_rulePageType9268 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_125_in_rulePageType9287 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_126_in_rulePageType9306 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rulePAGE_TYPE_NAME_in_rulePageType9334 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleCOLOR_NAME_in_entryRuleCOLOR_NAME9380 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleCOLOR_NAME9391 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleCOLOR_NAME9430 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleStateColor_in_entryRuleStateColor9475 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleStateColor9486 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_127_in_ruleStateColor9524 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_128_in_ruleStateColor9543 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_129_in_ruleStateColor9562 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_130_in_ruleStateColor9581 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_131_in_ruleStateColor9600 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_132_in_ruleStateColor9619 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_133_in_ruleStateColor9638 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_134_in_ruleStateColor9657 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_135_in_ruleStateColor9676 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_136_in_ruleStateColor9695 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_137_in_ruleStateColor9714 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_138_in_ruleStateColor9733 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_139_in_ruleStateColor9752 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_140_in_ruleStateColor9771 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_141_in_ruleStateColor9790 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_142_in_ruleStateColor9809 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleCOLOR_NAME_in_ruleStateColor9837 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_79_in_ruleBooleanDef9896 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_143_in_ruleBooleanDef9913 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_144_in_ruleBooleanDef9930 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_145_in_ruleWorkflowEventType9975 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_146_in_ruleWorkflowEventType9992 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_147_in_ruleWorkflowEventType10009 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_148_in_ruleReviewBlockingType10054 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_149_in_ruleReviewBlockingType10071 = new BitSet(new long[]{0x0000000000000002L});

}