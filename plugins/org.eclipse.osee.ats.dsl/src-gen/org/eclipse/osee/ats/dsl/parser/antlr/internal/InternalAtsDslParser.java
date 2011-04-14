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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE_STRING", "RULE_INT", "RULE_ID", "RULE_ML_COMMENT", "RULE_SL_COMMENT", "RULE_WS", "RULE_ANY_OTHER", "'workDefinition'", "'userDefinition'", "'teamDefinition'", "'actionableItem'", "'{'", "'active'", "'userId'", "'email'", "'isAdmin'", "'}'", "'usesVersions'", "'staticId'", "'lead'", "'member'", "'priviledged'", "'accessContextId'", "'version'", "'children'", "'actionable'", "'team'", "'next'", "'released'", "'allowCreateBranch'", "'allowCommitBranch'", "'baslineBranchGuid'", "'parallelVersion'", "'id'", "'startState'", "'widgetDefinition'", "'attributeName'", "'description'", "'xWidgetName'", "'defaultValue'", "'height'", "'option'", "'widget'", "'attributeWidget'", "'with'", "'state'", "'type'", "'ordinal'", "'rule'", "'percentWeight'", "'decisionReview'", "'decisionReviewDefinition'", "'title'", "'relatedToState'", "'blockingType'", "'onEvent'", "'assignee'", "'autoTransitionToDecision'", "'peerReview'", "'peerReviewDefinition'", "'location'", "'followup by'", "'named'", "'to'", "'layout'", "'layoutCopyFrom'", "'composite'", "'numColumns'", "'GetOrCreate'", "'None'", "'AsDefault'", "'OverrideAttributeValidation'", "'RequireStateHourSpentPrompt'", "'AddDecisionValidateBlockingReview'", "'AddDecisionValidateNonBlockingReview'", "'AllowTransitionWithWorkingBranch'", "'ForceAssigneesToTeamLeads'", "'RequireTargetedVersion'", "'AllowPriviledgedEditToTeamMember'", "'AllowPriviledgedEditToTeamMemberAndOriginator'", "'AllowPriviledgedEditToAll'", "'AllowEditToAll'", "'AllowAssigneeToAll'", "'REQUIRED_FOR_TRANSITION'", "'NOT_REQUIRED_FOR_TRANSITION'", "'REQUIRED_FOR_COMPLETION'", "'NOT_REQUIRED_FOR_COMPLETION'", "'ENABLED'", "'NOT_ENABLED'", "'EDITABLE'", "'NOT_EDITABLE'", "'MULTI_SELECT'", "'HORIZONTAL_LABEL'", "'VERTICAL_LABEL'", "'LABEL_AFTER'", "'LABEL_BEFORE'", "'NO_LABEL'", "'SORTED'", "'ADD_DEFAULT_VALUE'", "'NO_DEFAULT_VALUE'", "'BEGIN_COMPOSITE_4'", "'BEGIN_COMPOSITE_6'", "'BEGIN_COMPOSITE_8'", "'BEGIN_COMPOSITE_10'", "'END_COMPOSITE'", "'FILL_NONE'", "'FILL_HORIZONTALLY'", "'FILL_VERTICALLY'", "'ALIGN_LEFT'", "'ALIGN_RIGHT'", "'ALIGN_CENTER'", "'Working'", "'Completed'", "'Cancelled'", "'True'", "'False'", "'TransitionTo'", "'CreateBranch'", "'CommitBranch'", "'Transition'", "'Commit'"
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
    public static final int T__16=16;
    public static final int T__15=15;
    public static final int T__90=90;
    public static final int T__18=18;
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
    public static final int T__80=80;
    public static final int T__81=81;
    public static final int T__82=82;
    public static final int T__83=83;
    public static final int T__85=85;
    public static final int T__84=84;
    public static final int T__87=87;
    public static final int T__86=86;
    public static final int T__89=89;
    public static final int T__88=88;
    public static final int RULE_ML_COMMENT=7;
    public static final int RULE_STRING=4;
    public static final int T__71=71;
    public static final int T__72=72;
    public static final int T__70=70;
    public static final int T__76=76;
    public static final int T__75=75;
    public static final int T__74=74;
    public static final int T__73=73;
    public static final int T__79=79;
    public static final int T__78=78;
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

                if ( (LA5_0==RULE_STRING||LA5_0==72) ) {
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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:395:1: ruleTeamDef returns [EObject current=null] : ( ( (lv_name_0_0= ruleTEAM_DEF_REFERENCE ) ) ( (lv_teamDefOption_1_0= ruleTeamDefOption ) )* otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'usesVersions' ( (lv_usesVersions_6_0= ruleBooleanDef ) ) )? (otherlv_7= 'staticId' ( (lv_staticId_8_0= RULE_STRING ) ) )* (otherlv_9= 'lead' ( (lv_lead_10_0= ruleUserRef ) ) )* (otherlv_11= 'member' ( (lv_member_12_0= ruleUserRef ) ) )* (otherlv_13= 'priviledged' ( (lv_priviledged_14_0= ruleUserRef ) ) )* (otherlv_15= 'workDefinition' ( (lv_workDefinition_16_0= RULE_STRING ) ) )? (otherlv_17= 'accessContextId' ( (lv_accessContextId_18_0= RULE_STRING ) ) )* (otherlv_19= 'version' ( (lv_version_20_0= ruleVersionDef ) ) )* (otherlv_21= 'children' otherlv_22= '{' (otherlv_23= 'teamDefinition' ( (lv_children_24_0= ruleTeamDef ) ) )+ otherlv_25= '}' )? otherlv_26= '}' ) ;
    public final EObject ruleTeamDef() throws RecognitionException {
        EObject current = null;

        Token otherlv_2=null;
        Token otherlv_3=null;
        Token otherlv_5=null;
        Token otherlv_7=null;
        Token lv_staticId_8_0=null;
        Token otherlv_9=null;
        Token otherlv_11=null;
        Token otherlv_13=null;
        Token otherlv_15=null;
        Token lv_workDefinition_16_0=null;
        Token otherlv_17=null;
        Token lv_accessContextId_18_0=null;
        Token otherlv_19=null;
        Token otherlv_21=null;
        Token otherlv_22=null;
        Token otherlv_23=null;
        Token otherlv_25=null;
        Token otherlv_26=null;
        AntlrDatatypeRuleToken lv_name_0_0 = null;

        AntlrDatatypeRuleToken lv_teamDefOption_1_0 = null;

        Enumerator lv_active_4_0 = null;

        Enumerator lv_usesVersions_6_0 = null;

        EObject lv_lead_10_0 = null;

        EObject lv_member_12_0 = null;

        EObject lv_priviledged_14_0 = null;

        EObject lv_version_20_0 = null;

        EObject lv_children_24_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:398:28: ( ( ( (lv_name_0_0= ruleTEAM_DEF_REFERENCE ) ) ( (lv_teamDefOption_1_0= ruleTeamDefOption ) )* otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'usesVersions' ( (lv_usesVersions_6_0= ruleBooleanDef ) ) )? (otherlv_7= 'staticId' ( (lv_staticId_8_0= RULE_STRING ) ) )* (otherlv_9= 'lead' ( (lv_lead_10_0= ruleUserRef ) ) )* (otherlv_11= 'member' ( (lv_member_12_0= ruleUserRef ) ) )* (otherlv_13= 'priviledged' ( (lv_priviledged_14_0= ruleUserRef ) ) )* (otherlv_15= 'workDefinition' ( (lv_workDefinition_16_0= RULE_STRING ) ) )? (otherlv_17= 'accessContextId' ( (lv_accessContextId_18_0= RULE_STRING ) ) )* (otherlv_19= 'version' ( (lv_version_20_0= ruleVersionDef ) ) )* (otherlv_21= 'children' otherlv_22= '{' (otherlv_23= 'teamDefinition' ( (lv_children_24_0= ruleTeamDef ) ) )+ otherlv_25= '}' )? otherlv_26= '}' ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:399:1: ( ( (lv_name_0_0= ruleTEAM_DEF_REFERENCE ) ) ( (lv_teamDefOption_1_0= ruleTeamDefOption ) )* otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'usesVersions' ( (lv_usesVersions_6_0= ruleBooleanDef ) ) )? (otherlv_7= 'staticId' ( (lv_staticId_8_0= RULE_STRING ) ) )* (otherlv_9= 'lead' ( (lv_lead_10_0= ruleUserRef ) ) )* (otherlv_11= 'member' ( (lv_member_12_0= ruleUserRef ) ) )* (otherlv_13= 'priviledged' ( (lv_priviledged_14_0= ruleUserRef ) ) )* (otherlv_15= 'workDefinition' ( (lv_workDefinition_16_0= RULE_STRING ) ) )? (otherlv_17= 'accessContextId' ( (lv_accessContextId_18_0= RULE_STRING ) ) )* (otherlv_19= 'version' ( (lv_version_20_0= ruleVersionDef ) ) )* (otherlv_21= 'children' otherlv_22= '{' (otherlv_23= 'teamDefinition' ( (lv_children_24_0= ruleTeamDef ) ) )+ otherlv_25= '}' )? otherlv_26= '}' )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:399:1: ( ( (lv_name_0_0= ruleTEAM_DEF_REFERENCE ) ) ( (lv_teamDefOption_1_0= ruleTeamDefOption ) )* otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'usesVersions' ( (lv_usesVersions_6_0= ruleBooleanDef ) ) )? (otherlv_7= 'staticId' ( (lv_staticId_8_0= RULE_STRING ) ) )* (otherlv_9= 'lead' ( (lv_lead_10_0= ruleUserRef ) ) )* (otherlv_11= 'member' ( (lv_member_12_0= ruleUserRef ) ) )* (otherlv_13= 'priviledged' ( (lv_priviledged_14_0= ruleUserRef ) ) )* (otherlv_15= 'workDefinition' ( (lv_workDefinition_16_0= RULE_STRING ) ) )? (otherlv_17= 'accessContextId' ( (lv_accessContextId_18_0= RULE_STRING ) ) )* (otherlv_19= 'version' ( (lv_version_20_0= ruleVersionDef ) ) )* (otherlv_21= 'children' otherlv_22= '{' (otherlv_23= 'teamDefinition' ( (lv_children_24_0= ruleTeamDef ) ) )+ otherlv_25= '}' )? otherlv_26= '}' )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:399:2: ( (lv_name_0_0= ruleTEAM_DEF_REFERENCE ) ) ( (lv_teamDefOption_1_0= ruleTeamDefOption ) )* otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'usesVersions' ( (lv_usesVersions_6_0= ruleBooleanDef ) ) )? (otherlv_7= 'staticId' ( (lv_staticId_8_0= RULE_STRING ) ) )* (otherlv_9= 'lead' ( (lv_lead_10_0= ruleUserRef ) ) )* (otherlv_11= 'member' ( (lv_member_12_0= ruleUserRef ) ) )* (otherlv_13= 'priviledged' ( (lv_priviledged_14_0= ruleUserRef ) ) )* (otherlv_15= 'workDefinition' ( (lv_workDefinition_16_0= RULE_STRING ) ) )? (otherlv_17= 'accessContextId' ( (lv_accessContextId_18_0= RULE_STRING ) ) )* (otherlv_19= 'version' ( (lv_version_20_0= ruleVersionDef ) ) )* (otherlv_21= 'children' otherlv_22= '{' (otherlv_23= 'teamDefinition' ( (lv_children_24_0= ruleTeamDef ) ) )+ otherlv_25= '}' )? otherlv_26= '}'
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

                if ( (LA11_0==RULE_STRING||LA11_0==72) ) {
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
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:439:1: (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==16) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:439:3: otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) )
                    {
                    otherlv_3=(Token)match(input,16,FOLLOW_16_in_ruleTeamDef870); 

                        	newLeafNode(otherlv_3, grammarAccess.getTeamDefAccess().getActiveKeyword_3_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:443:1: ( (lv_active_4_0= ruleBooleanDef ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:444:1: (lv_active_4_0= ruleBooleanDef )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:444:1: (lv_active_4_0= ruleBooleanDef )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:445:3: lv_active_4_0= ruleBooleanDef
                    {
                     
                    	        newCompositeNode(grammarAccess.getTeamDefAccess().getActiveBooleanDefEnumRuleCall_3_1_0()); 
                    	    
                    pushFollow(FOLLOW_ruleBooleanDef_in_ruleTeamDef891);
                    lv_active_4_0=ruleBooleanDef();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getTeamDefRule());
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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:461:4: (otherlv_5= 'usesVersions' ( (lv_usesVersions_6_0= ruleBooleanDef ) ) )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==21) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:461:6: otherlv_5= 'usesVersions' ( (lv_usesVersions_6_0= ruleBooleanDef ) )
                    {
                    otherlv_5=(Token)match(input,21,FOLLOW_21_in_ruleTeamDef906); 

                        	newLeafNode(otherlv_5, grammarAccess.getTeamDefAccess().getUsesVersionsKeyword_4_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:465:1: ( (lv_usesVersions_6_0= ruleBooleanDef ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:466:1: (lv_usesVersions_6_0= ruleBooleanDef )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:466:1: (lv_usesVersions_6_0= ruleBooleanDef )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:467:3: lv_usesVersions_6_0= ruleBooleanDef
                    {
                     
                    	        newCompositeNode(grammarAccess.getTeamDefAccess().getUsesVersionsBooleanDefEnumRuleCall_4_1_0()); 
                    	    
                    pushFollow(FOLLOW_ruleBooleanDef_in_ruleTeamDef927);
                    lv_usesVersions_6_0=ruleBooleanDef();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getTeamDefRule());
                    	        }
                           		set(
                           			current, 
                           			"usesVersions",
                            		lv_usesVersions_6_0, 
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
            	    otherlv_7=(Token)match(input,22,FOLLOW_22_in_ruleTeamDef942); 

            	        	newLeafNode(otherlv_7, grammarAccess.getTeamDefAccess().getStaticIdKeyword_5_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:487:1: ( (lv_staticId_8_0= RULE_STRING ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:488:1: (lv_staticId_8_0= RULE_STRING )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:488:1: (lv_staticId_8_0= RULE_STRING )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:489:3: lv_staticId_8_0= RULE_STRING
            	    {
            	    lv_staticId_8_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleTeamDef959); 

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
            	    otherlv_9=(Token)match(input,23,FOLLOW_23_in_ruleTeamDef979); 

            	        	newLeafNode(otherlv_9, grammarAccess.getTeamDefAccess().getLeadKeyword_6_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:509:1: ( (lv_lead_10_0= ruleUserRef ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:510:1: (lv_lead_10_0= ruleUserRef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:510:1: (lv_lead_10_0= ruleUserRef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:511:3: lv_lead_10_0= ruleUserRef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getTeamDefAccess().getLeadUserRefParserRuleCall_6_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleUserRef_in_ruleTeamDef1000);
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
            	    otherlv_11=(Token)match(input,24,FOLLOW_24_in_ruleTeamDef1015); 

            	        	newLeafNode(otherlv_11, grammarAccess.getTeamDefAccess().getMemberKeyword_7_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:531:1: ( (lv_member_12_0= ruleUserRef ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:532:1: (lv_member_12_0= ruleUserRef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:532:1: (lv_member_12_0= ruleUserRef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:533:3: lv_member_12_0= ruleUserRef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getTeamDefAccess().getMemberUserRefParserRuleCall_7_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleUserRef_in_ruleTeamDef1036);
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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:549:4: (otherlv_13= 'priviledged' ( (lv_priviledged_14_0= ruleUserRef ) ) )*
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);

                if ( (LA17_0==25) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:549:6: otherlv_13= 'priviledged' ( (lv_priviledged_14_0= ruleUserRef ) )
            	    {
            	    otherlv_13=(Token)match(input,25,FOLLOW_25_in_ruleTeamDef1051); 

            	        	newLeafNode(otherlv_13, grammarAccess.getTeamDefAccess().getPriviledgedKeyword_8_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:553:1: ( (lv_priviledged_14_0= ruleUserRef ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:554:1: (lv_priviledged_14_0= ruleUserRef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:554:1: (lv_priviledged_14_0= ruleUserRef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:555:3: lv_priviledged_14_0= ruleUserRef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getTeamDefAccess().getPriviledgedUserRefParserRuleCall_8_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleUserRef_in_ruleTeamDef1072);
            	    lv_priviledged_14_0=ruleUserRef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getTeamDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"priviledged",
            	            		lv_priviledged_14_0, 
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
                    otherlv_15=(Token)match(input,11,FOLLOW_11_in_ruleTeamDef1087); 

                        	newLeafNode(otherlv_15, grammarAccess.getTeamDefAccess().getWorkDefinitionKeyword_9_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:575:1: ( (lv_workDefinition_16_0= RULE_STRING ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:576:1: (lv_workDefinition_16_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:576:1: (lv_workDefinition_16_0= RULE_STRING )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:577:3: lv_workDefinition_16_0= RULE_STRING
                    {
                    lv_workDefinition_16_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleTeamDef1104); 

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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:593:4: (otherlv_17= 'accessContextId' ( (lv_accessContextId_18_0= RULE_STRING ) ) )*
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);

                if ( (LA19_0==26) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:593:6: otherlv_17= 'accessContextId' ( (lv_accessContextId_18_0= RULE_STRING ) )
            	    {
            	    otherlv_17=(Token)match(input,26,FOLLOW_26_in_ruleTeamDef1124); 

            	        	newLeafNode(otherlv_17, grammarAccess.getTeamDefAccess().getAccessContextIdKeyword_10_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:597:1: ( (lv_accessContextId_18_0= RULE_STRING ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:598:1: (lv_accessContextId_18_0= RULE_STRING )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:598:1: (lv_accessContextId_18_0= RULE_STRING )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:599:3: lv_accessContextId_18_0= RULE_STRING
            	    {
            	    lv_accessContextId_18_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleTeamDef1141); 

            	    			newLeafNode(lv_accessContextId_18_0, grammarAccess.getTeamDefAccess().getAccessContextIdSTRINGTerminalRuleCall_10_1_0()); 
            	    		

            	    	        if (current==null) {
            	    	            current = createModelElement(grammarAccess.getTeamDefRule());
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
            	    break loop19;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:615:4: (otherlv_19= 'version' ( (lv_version_20_0= ruleVersionDef ) ) )*
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( (LA20_0==27) ) {
                    alt20=1;
                }


                switch (alt20) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:615:6: otherlv_19= 'version' ( (lv_version_20_0= ruleVersionDef ) )
            	    {
            	    otherlv_19=(Token)match(input,27,FOLLOW_27_in_ruleTeamDef1161); 

            	        	newLeafNode(otherlv_19, grammarAccess.getTeamDefAccess().getVersionKeyword_11_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:619:1: ( (lv_version_20_0= ruleVersionDef ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:620:1: (lv_version_20_0= ruleVersionDef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:620:1: (lv_version_20_0= ruleVersionDef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:621:3: lv_version_20_0= ruleVersionDef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getTeamDefAccess().getVersionVersionDefParserRuleCall_11_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleVersionDef_in_ruleTeamDef1182);
            	    lv_version_20_0=ruleVersionDef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getTeamDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"version",
            	            		lv_version_20_0, 
            	            		"VersionDef");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop20;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:637:4: (otherlv_21= 'children' otherlv_22= '{' (otherlv_23= 'teamDefinition' ( (lv_children_24_0= ruleTeamDef ) ) )+ otherlv_25= '}' )?
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0==28) ) {
                alt22=1;
            }
            switch (alt22) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:637:6: otherlv_21= 'children' otherlv_22= '{' (otherlv_23= 'teamDefinition' ( (lv_children_24_0= ruleTeamDef ) ) )+ otherlv_25= '}'
                    {
                    otherlv_21=(Token)match(input,28,FOLLOW_28_in_ruleTeamDef1197); 

                        	newLeafNode(otherlv_21, grammarAccess.getTeamDefAccess().getChildrenKeyword_12_0());
                        
                    otherlv_22=(Token)match(input,15,FOLLOW_15_in_ruleTeamDef1209); 

                        	newLeafNode(otherlv_22, grammarAccess.getTeamDefAccess().getLeftCurlyBracketKeyword_12_1());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:645:1: (otherlv_23= 'teamDefinition' ( (lv_children_24_0= ruleTeamDef ) ) )+
                    int cnt21=0;
                    loop21:
                    do {
                        int alt21=2;
                        int LA21_0 = input.LA(1);

                        if ( (LA21_0==13) ) {
                            alt21=1;
                        }


                        switch (alt21) {
                    	case 1 :
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:645:3: otherlv_23= 'teamDefinition' ( (lv_children_24_0= ruleTeamDef ) )
                    	    {
                    	    otherlv_23=(Token)match(input,13,FOLLOW_13_in_ruleTeamDef1222); 

                    	        	newLeafNode(otherlv_23, grammarAccess.getTeamDefAccess().getTeamDefinitionKeyword_12_2_0());
                    	        
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:649:1: ( (lv_children_24_0= ruleTeamDef ) )
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:650:1: (lv_children_24_0= ruleTeamDef )
                    	    {
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:650:1: (lv_children_24_0= ruleTeamDef )
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:651:3: lv_children_24_0= ruleTeamDef
                    	    {
                    	     
                    	    	        newCompositeNode(grammarAccess.getTeamDefAccess().getChildrenTeamDefParserRuleCall_12_2_1_0()); 
                    	    	    
                    	    pushFollow(FOLLOW_ruleTeamDef_in_ruleTeamDef1243);
                    	    lv_children_24_0=ruleTeamDef();

                    	    state._fsp--;


                    	    	        if (current==null) {
                    	    	            current = createModelElementForParent(grammarAccess.getTeamDefRule());
                    	    	        }
                    	           		add(
                    	           			current, 
                    	           			"children",
                    	            		lv_children_24_0, 
                    	            		"TeamDef");
                    	    	        afterParserOrEnumRuleCall();
                    	    	    

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt21 >= 1 ) break loop21;
                                EarlyExitException eee =
                                    new EarlyExitException(21, input);
                                throw eee;
                        }
                        cnt21++;
                    } while (true);

                    otherlv_25=(Token)match(input,20,FOLLOW_20_in_ruleTeamDef1257); 

                        	newLeafNode(otherlv_25, grammarAccess.getTeamDefAccess().getRightCurlyBracketKeyword_12_3());
                        

                    }
                    break;

            }

            otherlv_26=(Token)match(input,20,FOLLOW_20_in_ruleTeamDef1271); 

                	newLeafNode(otherlv_26, grammarAccess.getTeamDefAccess().getRightCurlyBracketKeyword_13());
                

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:683:1: entryRuleAI_DEF_REFERENCE returns [String current=null] : iv_ruleAI_DEF_REFERENCE= ruleAI_DEF_REFERENCE EOF ;
    public final String entryRuleAI_DEF_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleAI_DEF_REFERENCE = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:684:2: (iv_ruleAI_DEF_REFERENCE= ruleAI_DEF_REFERENCE EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:685:2: iv_ruleAI_DEF_REFERENCE= ruleAI_DEF_REFERENCE EOF
            {
             newCompositeNode(grammarAccess.getAI_DEF_REFERENCERule()); 
            pushFollow(FOLLOW_ruleAI_DEF_REFERENCE_in_entryRuleAI_DEF_REFERENCE1308);
            iv_ruleAI_DEF_REFERENCE=ruleAI_DEF_REFERENCE();

            state._fsp--;

             current =iv_ruleAI_DEF_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAI_DEF_REFERENCE1319); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:692:1: ruleAI_DEF_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleAI_DEF_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:695:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:696:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAI_DEF_REFERENCE1358); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:711:1: entryRuleActionableItemDef returns [EObject current=null] : iv_ruleActionableItemDef= ruleActionableItemDef EOF ;
    public final EObject entryRuleActionableItemDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleActionableItemDef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:712:2: (iv_ruleActionableItemDef= ruleActionableItemDef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:713:2: iv_ruleActionableItemDef= ruleActionableItemDef EOF
            {
             newCompositeNode(grammarAccess.getActionableItemDefRule()); 
            pushFollow(FOLLOW_ruleActionableItemDef_in_entryRuleActionableItemDef1402);
            iv_ruleActionableItemDef=ruleActionableItemDef();

            state._fsp--;

             current =iv_ruleActionableItemDef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleActionableItemDef1412); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:720:1: ruleActionableItemDef returns [EObject current=null] : ( ( (lv_name_0_0= ruleAI_DEF_REFERENCE ) ) ( (lv_aiDefOption_1_0= ruleActionableItemOption ) )* (otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'actionable' ( (lv_actionable_6_0= ruleBooleanDef ) ) )? (otherlv_7= 'lead' ( (lv_lead_8_0= ruleUserRef ) ) )* (otherlv_9= 'staticId' ( (lv_staticId_10_0= RULE_STRING ) ) )* (otherlv_11= 'team' ( (lv_teamDef_12_0= RULE_STRING ) ) )? (otherlv_13= 'accessContextId' ( (lv_accessContextId_14_0= RULE_STRING ) ) )* (otherlv_15= 'children' otherlv_16= '{' (otherlv_17= 'actionableItem' ( (lv_children_18_0= ruleActionableItemDef ) ) )+ otherlv_19= '}' )? otherlv_20= '}' )? ) ;
    public final EObject ruleActionableItemDef() throws RecognitionException {
        EObject current = null;

        Token otherlv_2=null;
        Token otherlv_3=null;
        Token otherlv_5=null;
        Token otherlv_7=null;
        Token otherlv_9=null;
        Token lv_staticId_10_0=null;
        Token otherlv_11=null;
        Token lv_teamDef_12_0=null;
        Token otherlv_13=null;
        Token lv_accessContextId_14_0=null;
        Token otherlv_15=null;
        Token otherlv_16=null;
        Token otherlv_17=null;
        Token otherlv_19=null;
        Token otherlv_20=null;
        AntlrDatatypeRuleToken lv_name_0_0 = null;

        AntlrDatatypeRuleToken lv_aiDefOption_1_0 = null;

        Enumerator lv_active_4_0 = null;

        Enumerator lv_actionable_6_0 = null;

        EObject lv_lead_8_0 = null;

        EObject lv_children_18_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:723:28: ( ( ( (lv_name_0_0= ruleAI_DEF_REFERENCE ) ) ( (lv_aiDefOption_1_0= ruleActionableItemOption ) )* (otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'actionable' ( (lv_actionable_6_0= ruleBooleanDef ) ) )? (otherlv_7= 'lead' ( (lv_lead_8_0= ruleUserRef ) ) )* (otherlv_9= 'staticId' ( (lv_staticId_10_0= RULE_STRING ) ) )* (otherlv_11= 'team' ( (lv_teamDef_12_0= RULE_STRING ) ) )? (otherlv_13= 'accessContextId' ( (lv_accessContextId_14_0= RULE_STRING ) ) )* (otherlv_15= 'children' otherlv_16= '{' (otherlv_17= 'actionableItem' ( (lv_children_18_0= ruleActionableItemDef ) ) )+ otherlv_19= '}' )? otherlv_20= '}' )? ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:724:1: ( ( (lv_name_0_0= ruleAI_DEF_REFERENCE ) ) ( (lv_aiDefOption_1_0= ruleActionableItemOption ) )* (otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'actionable' ( (lv_actionable_6_0= ruleBooleanDef ) ) )? (otherlv_7= 'lead' ( (lv_lead_8_0= ruleUserRef ) ) )* (otherlv_9= 'staticId' ( (lv_staticId_10_0= RULE_STRING ) ) )* (otherlv_11= 'team' ( (lv_teamDef_12_0= RULE_STRING ) ) )? (otherlv_13= 'accessContextId' ( (lv_accessContextId_14_0= RULE_STRING ) ) )* (otherlv_15= 'children' otherlv_16= '{' (otherlv_17= 'actionableItem' ( (lv_children_18_0= ruleActionableItemDef ) ) )+ otherlv_19= '}' )? otherlv_20= '}' )? )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:724:1: ( ( (lv_name_0_0= ruleAI_DEF_REFERENCE ) ) ( (lv_aiDefOption_1_0= ruleActionableItemOption ) )* (otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'actionable' ( (lv_actionable_6_0= ruleBooleanDef ) ) )? (otherlv_7= 'lead' ( (lv_lead_8_0= ruleUserRef ) ) )* (otherlv_9= 'staticId' ( (lv_staticId_10_0= RULE_STRING ) ) )* (otherlv_11= 'team' ( (lv_teamDef_12_0= RULE_STRING ) ) )? (otherlv_13= 'accessContextId' ( (lv_accessContextId_14_0= RULE_STRING ) ) )* (otherlv_15= 'children' otherlv_16= '{' (otherlv_17= 'actionableItem' ( (lv_children_18_0= ruleActionableItemDef ) ) )+ otherlv_19= '}' )? otherlv_20= '}' )? )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:724:2: ( (lv_name_0_0= ruleAI_DEF_REFERENCE ) ) ( (lv_aiDefOption_1_0= ruleActionableItemOption ) )* (otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'actionable' ( (lv_actionable_6_0= ruleBooleanDef ) ) )? (otherlv_7= 'lead' ( (lv_lead_8_0= ruleUserRef ) ) )* (otherlv_9= 'staticId' ( (lv_staticId_10_0= RULE_STRING ) ) )* (otherlv_11= 'team' ( (lv_teamDef_12_0= RULE_STRING ) ) )? (otherlv_13= 'accessContextId' ( (lv_accessContextId_14_0= RULE_STRING ) ) )* (otherlv_15= 'children' otherlv_16= '{' (otherlv_17= 'actionableItem' ( (lv_children_18_0= ruleActionableItemDef ) ) )+ otherlv_19= '}' )? otherlv_20= '}' )?
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:724:2: ( (lv_name_0_0= ruleAI_DEF_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:725:1: (lv_name_0_0= ruleAI_DEF_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:725:1: (lv_name_0_0= ruleAI_DEF_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:726:3: lv_name_0_0= ruleAI_DEF_REFERENCE
            {
             
            	        newCompositeNode(grammarAccess.getActionableItemDefAccess().getNameAI_DEF_REFERENCEParserRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_ruleAI_DEF_REFERENCE_in_ruleActionableItemDef1458);
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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:742:2: ( (lv_aiDefOption_1_0= ruleActionableItemOption ) )*
            loop23:
            do {
                int alt23=2;
                int LA23_0 = input.LA(1);

                if ( (LA23_0==RULE_STRING||LA23_0==72) ) {
                    alt23=1;
                }


                switch (alt23) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:743:1: (lv_aiDefOption_1_0= ruleActionableItemOption )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:743:1: (lv_aiDefOption_1_0= ruleActionableItemOption )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:744:3: lv_aiDefOption_1_0= ruleActionableItemOption
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getActionableItemDefAccess().getAiDefOptionActionableItemOptionParserRuleCall_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleActionableItemOption_in_ruleActionableItemDef1479);
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
            	    break loop23;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:760:3: (otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'actionable' ( (lv_actionable_6_0= ruleBooleanDef ) ) )? (otherlv_7= 'lead' ( (lv_lead_8_0= ruleUserRef ) ) )* (otherlv_9= 'staticId' ( (lv_staticId_10_0= RULE_STRING ) ) )* (otherlv_11= 'team' ( (lv_teamDef_12_0= RULE_STRING ) ) )? (otherlv_13= 'accessContextId' ( (lv_accessContextId_14_0= RULE_STRING ) ) )* (otherlv_15= 'children' otherlv_16= '{' (otherlv_17= 'actionableItem' ( (lv_children_18_0= ruleActionableItemDef ) ) )+ otherlv_19= '}' )? otherlv_20= '}' )?
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==15) ) {
                alt32=1;
            }
            switch (alt32) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:760:5: otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'actionable' ( (lv_actionable_6_0= ruleBooleanDef ) ) )? (otherlv_7= 'lead' ( (lv_lead_8_0= ruleUserRef ) ) )* (otherlv_9= 'staticId' ( (lv_staticId_10_0= RULE_STRING ) ) )* (otherlv_11= 'team' ( (lv_teamDef_12_0= RULE_STRING ) ) )? (otherlv_13= 'accessContextId' ( (lv_accessContextId_14_0= RULE_STRING ) ) )* (otherlv_15= 'children' otherlv_16= '{' (otherlv_17= 'actionableItem' ( (lv_children_18_0= ruleActionableItemDef ) ) )+ otherlv_19= '}' )? otherlv_20= '}'
                    {
                    otherlv_2=(Token)match(input,15,FOLLOW_15_in_ruleActionableItemDef1493); 

                        	newLeafNode(otherlv_2, grammarAccess.getActionableItemDefAccess().getLeftCurlyBracketKeyword_2_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:764:1: (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )?
                    int alt24=2;
                    int LA24_0 = input.LA(1);

                    if ( (LA24_0==16) ) {
                        alt24=1;
                    }
                    switch (alt24) {
                        case 1 :
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:764:3: otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) )
                            {
                            otherlv_3=(Token)match(input,16,FOLLOW_16_in_ruleActionableItemDef1506); 

                                	newLeafNode(otherlv_3, grammarAccess.getActionableItemDefAccess().getActiveKeyword_2_1_0());
                                
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:768:1: ( (lv_active_4_0= ruleBooleanDef ) )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:769:1: (lv_active_4_0= ruleBooleanDef )
                            {
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:769:1: (lv_active_4_0= ruleBooleanDef )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:770:3: lv_active_4_0= ruleBooleanDef
                            {
                             
                            	        newCompositeNode(grammarAccess.getActionableItemDefAccess().getActiveBooleanDefEnumRuleCall_2_1_1_0()); 
                            	    
                            pushFollow(FOLLOW_ruleBooleanDef_in_ruleActionableItemDef1527);
                            lv_active_4_0=ruleBooleanDef();

                            state._fsp--;


                            	        if (current==null) {
                            	            current = createModelElementForParent(grammarAccess.getActionableItemDefRule());
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

                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:786:4: (otherlv_5= 'actionable' ( (lv_actionable_6_0= ruleBooleanDef ) ) )?
                    int alt25=2;
                    int LA25_0 = input.LA(1);

                    if ( (LA25_0==29) ) {
                        alt25=1;
                    }
                    switch (alt25) {
                        case 1 :
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:786:6: otherlv_5= 'actionable' ( (lv_actionable_6_0= ruleBooleanDef ) )
                            {
                            otherlv_5=(Token)match(input,29,FOLLOW_29_in_ruleActionableItemDef1542); 

                                	newLeafNode(otherlv_5, grammarAccess.getActionableItemDefAccess().getActionableKeyword_2_2_0());
                                
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:790:1: ( (lv_actionable_6_0= ruleBooleanDef ) )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:791:1: (lv_actionable_6_0= ruleBooleanDef )
                            {
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:791:1: (lv_actionable_6_0= ruleBooleanDef )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:792:3: lv_actionable_6_0= ruleBooleanDef
                            {
                             
                            	        newCompositeNode(grammarAccess.getActionableItemDefAccess().getActionableBooleanDefEnumRuleCall_2_2_1_0()); 
                            	    
                            pushFollow(FOLLOW_ruleBooleanDef_in_ruleActionableItemDef1563);
                            lv_actionable_6_0=ruleBooleanDef();

                            state._fsp--;


                            	        if (current==null) {
                            	            current = createModelElementForParent(grammarAccess.getActionableItemDefRule());
                            	        }
                                   		set(
                                   			current, 
                                   			"actionable",
                                    		lv_actionable_6_0, 
                                    		"BooleanDef");
                            	        afterParserOrEnumRuleCall();
                            	    

                            }


                            }


                            }
                            break;

                    }

                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:808:4: (otherlv_7= 'lead' ( (lv_lead_8_0= ruleUserRef ) ) )*
                    loop26:
                    do {
                        int alt26=2;
                        int LA26_0 = input.LA(1);

                        if ( (LA26_0==23) ) {
                            alt26=1;
                        }


                        switch (alt26) {
                    	case 1 :
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:808:6: otherlv_7= 'lead' ( (lv_lead_8_0= ruleUserRef ) )
                    	    {
                    	    otherlv_7=(Token)match(input,23,FOLLOW_23_in_ruleActionableItemDef1578); 

                    	        	newLeafNode(otherlv_7, grammarAccess.getActionableItemDefAccess().getLeadKeyword_2_3_0());
                    	        
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:812:1: ( (lv_lead_8_0= ruleUserRef ) )
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:813:1: (lv_lead_8_0= ruleUserRef )
                    	    {
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:813:1: (lv_lead_8_0= ruleUserRef )
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:814:3: lv_lead_8_0= ruleUserRef
                    	    {
                    	     
                    	    	        newCompositeNode(grammarAccess.getActionableItemDefAccess().getLeadUserRefParserRuleCall_2_3_1_0()); 
                    	    	    
                    	    pushFollow(FOLLOW_ruleUserRef_in_ruleActionableItemDef1599);
                    	    lv_lead_8_0=ruleUserRef();

                    	    state._fsp--;


                    	    	        if (current==null) {
                    	    	            current = createModelElementForParent(grammarAccess.getActionableItemDefRule());
                    	    	        }
                    	           		add(
                    	           			current, 
                    	           			"lead",
                    	            		lv_lead_8_0, 
                    	            		"UserRef");
                    	    	        afterParserOrEnumRuleCall();
                    	    	    

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop26;
                        }
                    } while (true);

                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:830:4: (otherlv_9= 'staticId' ( (lv_staticId_10_0= RULE_STRING ) ) )*
                    loop27:
                    do {
                        int alt27=2;
                        int LA27_0 = input.LA(1);

                        if ( (LA27_0==22) ) {
                            alt27=1;
                        }


                        switch (alt27) {
                    	case 1 :
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:830:6: otherlv_9= 'staticId' ( (lv_staticId_10_0= RULE_STRING ) )
                    	    {
                    	    otherlv_9=(Token)match(input,22,FOLLOW_22_in_ruleActionableItemDef1614); 

                    	        	newLeafNode(otherlv_9, grammarAccess.getActionableItemDefAccess().getStaticIdKeyword_2_4_0());
                    	        
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:834:1: ( (lv_staticId_10_0= RULE_STRING ) )
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:835:1: (lv_staticId_10_0= RULE_STRING )
                    	    {
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:835:1: (lv_staticId_10_0= RULE_STRING )
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:836:3: lv_staticId_10_0= RULE_STRING
                    	    {
                    	    lv_staticId_10_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleActionableItemDef1631); 

                    	    			newLeafNode(lv_staticId_10_0, grammarAccess.getActionableItemDefAccess().getStaticIdSTRINGTerminalRuleCall_2_4_1_0()); 
                    	    		

                    	    	        if (current==null) {
                    	    	            current = createModelElement(grammarAccess.getActionableItemDefRule());
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

                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:852:4: (otherlv_11= 'team' ( (lv_teamDef_12_0= RULE_STRING ) ) )?
                    int alt28=2;
                    int LA28_0 = input.LA(1);

                    if ( (LA28_0==30) ) {
                        alt28=1;
                    }
                    switch (alt28) {
                        case 1 :
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:852:6: otherlv_11= 'team' ( (lv_teamDef_12_0= RULE_STRING ) )
                            {
                            otherlv_11=(Token)match(input,30,FOLLOW_30_in_ruleActionableItemDef1651); 

                                	newLeafNode(otherlv_11, grammarAccess.getActionableItemDefAccess().getTeamKeyword_2_5_0());
                                
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:856:1: ( (lv_teamDef_12_0= RULE_STRING ) )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:857:1: (lv_teamDef_12_0= RULE_STRING )
                            {
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:857:1: (lv_teamDef_12_0= RULE_STRING )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:858:3: lv_teamDef_12_0= RULE_STRING
                            {
                            lv_teamDef_12_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleActionableItemDef1668); 

                            			newLeafNode(lv_teamDef_12_0, grammarAccess.getActionableItemDefAccess().getTeamDefSTRINGTerminalRuleCall_2_5_1_0()); 
                            		

                            	        if (current==null) {
                            	            current = createModelElement(grammarAccess.getActionableItemDefRule());
                            	        }
                                   		setWithLastConsumed(
                                   			current, 
                                   			"teamDef",
                                    		lv_teamDef_12_0, 
                                    		"STRING");
                            	    

                            }


                            }


                            }
                            break;

                    }

                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:874:4: (otherlv_13= 'accessContextId' ( (lv_accessContextId_14_0= RULE_STRING ) ) )*
                    loop29:
                    do {
                        int alt29=2;
                        int LA29_0 = input.LA(1);

                        if ( (LA29_0==26) ) {
                            alt29=1;
                        }


                        switch (alt29) {
                    	case 1 :
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:874:6: otherlv_13= 'accessContextId' ( (lv_accessContextId_14_0= RULE_STRING ) )
                    	    {
                    	    otherlv_13=(Token)match(input,26,FOLLOW_26_in_ruleActionableItemDef1688); 

                    	        	newLeafNode(otherlv_13, grammarAccess.getActionableItemDefAccess().getAccessContextIdKeyword_2_6_0());
                    	        
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:878:1: ( (lv_accessContextId_14_0= RULE_STRING ) )
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:879:1: (lv_accessContextId_14_0= RULE_STRING )
                    	    {
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:879:1: (lv_accessContextId_14_0= RULE_STRING )
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:880:3: lv_accessContextId_14_0= RULE_STRING
                    	    {
                    	    lv_accessContextId_14_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleActionableItemDef1705); 

                    	    			newLeafNode(lv_accessContextId_14_0, grammarAccess.getActionableItemDefAccess().getAccessContextIdSTRINGTerminalRuleCall_2_6_1_0()); 
                    	    		

                    	    	        if (current==null) {
                    	    	            current = createModelElement(grammarAccess.getActionableItemDefRule());
                    	    	        }
                    	           		addWithLastConsumed(
                    	           			current, 
                    	           			"accessContextId",
                    	            		lv_accessContextId_14_0, 
                    	            		"STRING");
                    	    	    

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop29;
                        }
                    } while (true);

                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:896:4: (otherlv_15= 'children' otherlv_16= '{' (otherlv_17= 'actionableItem' ( (lv_children_18_0= ruleActionableItemDef ) ) )+ otherlv_19= '}' )?
                    int alt31=2;
                    int LA31_0 = input.LA(1);

                    if ( (LA31_0==28) ) {
                        alt31=1;
                    }
                    switch (alt31) {
                        case 1 :
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:896:6: otherlv_15= 'children' otherlv_16= '{' (otherlv_17= 'actionableItem' ( (lv_children_18_0= ruleActionableItemDef ) ) )+ otherlv_19= '}'
                            {
                            otherlv_15=(Token)match(input,28,FOLLOW_28_in_ruleActionableItemDef1725); 

                                	newLeafNode(otherlv_15, grammarAccess.getActionableItemDefAccess().getChildrenKeyword_2_7_0());
                                
                            otherlv_16=(Token)match(input,15,FOLLOW_15_in_ruleActionableItemDef1737); 

                                	newLeafNode(otherlv_16, grammarAccess.getActionableItemDefAccess().getLeftCurlyBracketKeyword_2_7_1());
                                
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:904:1: (otherlv_17= 'actionableItem' ( (lv_children_18_0= ruleActionableItemDef ) ) )+
                            int cnt30=0;
                            loop30:
                            do {
                                int alt30=2;
                                int LA30_0 = input.LA(1);

                                if ( (LA30_0==14) ) {
                                    alt30=1;
                                }


                                switch (alt30) {
                            	case 1 :
                            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:904:3: otherlv_17= 'actionableItem' ( (lv_children_18_0= ruleActionableItemDef ) )
                            	    {
                            	    otherlv_17=(Token)match(input,14,FOLLOW_14_in_ruleActionableItemDef1750); 

                            	        	newLeafNode(otherlv_17, grammarAccess.getActionableItemDefAccess().getActionableItemKeyword_2_7_2_0());
                            	        
                            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:908:1: ( (lv_children_18_0= ruleActionableItemDef ) )
                            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:909:1: (lv_children_18_0= ruleActionableItemDef )
                            	    {
                            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:909:1: (lv_children_18_0= ruleActionableItemDef )
                            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:910:3: lv_children_18_0= ruleActionableItemDef
                            	    {
                            	     
                            	    	        newCompositeNode(grammarAccess.getActionableItemDefAccess().getChildrenActionableItemDefParserRuleCall_2_7_2_1_0()); 
                            	    	    
                            	    pushFollow(FOLLOW_ruleActionableItemDef_in_ruleActionableItemDef1771);
                            	    lv_children_18_0=ruleActionableItemDef();

                            	    state._fsp--;


                            	    	        if (current==null) {
                            	    	            current = createModelElementForParent(grammarAccess.getActionableItemDefRule());
                            	    	        }
                            	           		add(
                            	           			current, 
                            	           			"children",
                            	            		lv_children_18_0, 
                            	            		"ActionableItemDef");
                            	    	        afterParserOrEnumRuleCall();
                            	    	    

                            	    }


                            	    }


                            	    }
                            	    break;

                            	default :
                            	    if ( cnt30 >= 1 ) break loop30;
                                        EarlyExitException eee =
                                            new EarlyExitException(30, input);
                                        throw eee;
                                }
                                cnt30++;
                            } while (true);

                            otherlv_19=(Token)match(input,20,FOLLOW_20_in_ruleActionableItemDef1785); 

                                	newLeafNode(otherlv_19, grammarAccess.getActionableItemDefAccess().getRightCurlyBracketKeyword_2_7_3());
                                

                            }
                            break;

                    }

                    otherlv_20=(Token)match(input,20,FOLLOW_20_in_ruleActionableItemDef1799); 

                        	newLeafNode(otherlv_20, grammarAccess.getActionableItemDefAccess().getRightCurlyBracketKeyword_2_8());
                        

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:942:1: entryRuleVERSION_DEF_REFERENCE returns [String current=null] : iv_ruleVERSION_DEF_REFERENCE= ruleVERSION_DEF_REFERENCE EOF ;
    public final String entryRuleVERSION_DEF_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleVERSION_DEF_REFERENCE = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:943:2: (iv_ruleVERSION_DEF_REFERENCE= ruleVERSION_DEF_REFERENCE EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:944:2: iv_ruleVERSION_DEF_REFERENCE= ruleVERSION_DEF_REFERENCE EOF
            {
             newCompositeNode(grammarAccess.getVERSION_DEF_REFERENCERule()); 
            pushFollow(FOLLOW_ruleVERSION_DEF_REFERENCE_in_entryRuleVERSION_DEF_REFERENCE1838);
            iv_ruleVERSION_DEF_REFERENCE=ruleVERSION_DEF_REFERENCE();

            state._fsp--;

             current =iv_ruleVERSION_DEF_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleVERSION_DEF_REFERENCE1849); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:951:1: ruleVERSION_DEF_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleVERSION_DEF_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:954:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:955:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleVERSION_DEF_REFERENCE1888); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:970:1: entryRuleVersionDef returns [EObject current=null] : iv_ruleVersionDef= ruleVersionDef EOF ;
    public final EObject entryRuleVersionDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleVersionDef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:971:2: (iv_ruleVersionDef= ruleVersionDef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:972:2: iv_ruleVersionDef= ruleVersionDef EOF
            {
             newCompositeNode(grammarAccess.getVersionDefRule()); 
            pushFollow(FOLLOW_ruleVersionDef_in_entryRuleVersionDef1932);
            iv_ruleVersionDef=ruleVersionDef();

            state._fsp--;

             current =iv_ruleVersionDef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleVersionDef1942); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:979:1: ruleVersionDef returns [EObject current=null] : ( ( (lv_name_0_0= ruleVERSION_DEF_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'active' ( (lv_active_3_0= ruleBooleanDef ) ) )? (otherlv_4= 'staticId' ( (lv_staticId_5_0= RULE_STRING ) ) )* (otherlv_6= 'next' ( (lv_next_7_0= ruleBooleanDef ) ) )? (otherlv_8= 'released' ( (lv_released_9_0= ruleBooleanDef ) ) )? (otherlv_10= 'allowCreateBranch' ( (lv_allowCreateBranch_11_0= ruleBooleanDef ) ) )? (otherlv_12= 'allowCommitBranch' ( (lv_allowCommitBranch_13_0= ruleBooleanDef ) ) )? (otherlv_14= 'baslineBranchGuid' ( (lv_baselineBranchGuid_15_0= RULE_STRING ) ) )? (otherlv_16= 'parallelVersion' ( (lv_parallelVersion_17_0= RULE_STRING ) ) )* otherlv_18= '}' ) ;
    public final EObject ruleVersionDef() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_2=null;
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

        Enumerator lv_active_3_0 = null;

        Enumerator lv_next_7_0 = null;

        Enumerator lv_released_9_0 = null;

        Enumerator lv_allowCreateBranch_11_0 = null;

        Enumerator lv_allowCommitBranch_13_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:982:28: ( ( ( (lv_name_0_0= ruleVERSION_DEF_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'active' ( (lv_active_3_0= ruleBooleanDef ) ) )? (otherlv_4= 'staticId' ( (lv_staticId_5_0= RULE_STRING ) ) )* (otherlv_6= 'next' ( (lv_next_7_0= ruleBooleanDef ) ) )? (otherlv_8= 'released' ( (lv_released_9_0= ruleBooleanDef ) ) )? (otherlv_10= 'allowCreateBranch' ( (lv_allowCreateBranch_11_0= ruleBooleanDef ) ) )? (otherlv_12= 'allowCommitBranch' ( (lv_allowCommitBranch_13_0= ruleBooleanDef ) ) )? (otherlv_14= 'baslineBranchGuid' ( (lv_baselineBranchGuid_15_0= RULE_STRING ) ) )? (otherlv_16= 'parallelVersion' ( (lv_parallelVersion_17_0= RULE_STRING ) ) )* otherlv_18= '}' ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:983:1: ( ( (lv_name_0_0= ruleVERSION_DEF_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'active' ( (lv_active_3_0= ruleBooleanDef ) ) )? (otherlv_4= 'staticId' ( (lv_staticId_5_0= RULE_STRING ) ) )* (otherlv_6= 'next' ( (lv_next_7_0= ruleBooleanDef ) ) )? (otherlv_8= 'released' ( (lv_released_9_0= ruleBooleanDef ) ) )? (otherlv_10= 'allowCreateBranch' ( (lv_allowCreateBranch_11_0= ruleBooleanDef ) ) )? (otherlv_12= 'allowCommitBranch' ( (lv_allowCommitBranch_13_0= ruleBooleanDef ) ) )? (otherlv_14= 'baslineBranchGuid' ( (lv_baselineBranchGuid_15_0= RULE_STRING ) ) )? (otherlv_16= 'parallelVersion' ( (lv_parallelVersion_17_0= RULE_STRING ) ) )* otherlv_18= '}' )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:983:1: ( ( (lv_name_0_0= ruleVERSION_DEF_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'active' ( (lv_active_3_0= ruleBooleanDef ) ) )? (otherlv_4= 'staticId' ( (lv_staticId_5_0= RULE_STRING ) ) )* (otherlv_6= 'next' ( (lv_next_7_0= ruleBooleanDef ) ) )? (otherlv_8= 'released' ( (lv_released_9_0= ruleBooleanDef ) ) )? (otherlv_10= 'allowCreateBranch' ( (lv_allowCreateBranch_11_0= ruleBooleanDef ) ) )? (otherlv_12= 'allowCommitBranch' ( (lv_allowCommitBranch_13_0= ruleBooleanDef ) ) )? (otherlv_14= 'baslineBranchGuid' ( (lv_baselineBranchGuid_15_0= RULE_STRING ) ) )? (otherlv_16= 'parallelVersion' ( (lv_parallelVersion_17_0= RULE_STRING ) ) )* otherlv_18= '}' )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:983:2: ( (lv_name_0_0= ruleVERSION_DEF_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'active' ( (lv_active_3_0= ruleBooleanDef ) ) )? (otherlv_4= 'staticId' ( (lv_staticId_5_0= RULE_STRING ) ) )* (otherlv_6= 'next' ( (lv_next_7_0= ruleBooleanDef ) ) )? (otherlv_8= 'released' ( (lv_released_9_0= ruleBooleanDef ) ) )? (otherlv_10= 'allowCreateBranch' ( (lv_allowCreateBranch_11_0= ruleBooleanDef ) ) )? (otherlv_12= 'allowCommitBranch' ( (lv_allowCommitBranch_13_0= ruleBooleanDef ) ) )? (otherlv_14= 'baslineBranchGuid' ( (lv_baselineBranchGuid_15_0= RULE_STRING ) ) )? (otherlv_16= 'parallelVersion' ( (lv_parallelVersion_17_0= RULE_STRING ) ) )* otherlv_18= '}'
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:983:2: ( (lv_name_0_0= ruleVERSION_DEF_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:984:1: (lv_name_0_0= ruleVERSION_DEF_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:984:1: (lv_name_0_0= ruleVERSION_DEF_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:985:3: lv_name_0_0= ruleVERSION_DEF_REFERENCE
            {
             
            	        newCompositeNode(grammarAccess.getVersionDefAccess().getNameVERSION_DEF_REFERENCEParserRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_ruleVERSION_DEF_REFERENCE_in_ruleVersionDef1988);
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

            otherlv_1=(Token)match(input,15,FOLLOW_15_in_ruleVersionDef2000); 

                	newLeafNode(otherlv_1, grammarAccess.getVersionDefAccess().getLeftCurlyBracketKeyword_1());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1005:1: (otherlv_2= 'active' ( (lv_active_3_0= ruleBooleanDef ) ) )?
            int alt33=2;
            int LA33_0 = input.LA(1);

            if ( (LA33_0==16) ) {
                alt33=1;
            }
            switch (alt33) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1005:3: otherlv_2= 'active' ( (lv_active_3_0= ruleBooleanDef ) )
                    {
                    otherlv_2=(Token)match(input,16,FOLLOW_16_in_ruleVersionDef2013); 

                        	newLeafNode(otherlv_2, grammarAccess.getVersionDefAccess().getActiveKeyword_2_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1009:1: ( (lv_active_3_0= ruleBooleanDef ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1010:1: (lv_active_3_0= ruleBooleanDef )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1010:1: (lv_active_3_0= ruleBooleanDef )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1011:3: lv_active_3_0= ruleBooleanDef
                    {
                     
                    	        newCompositeNode(grammarAccess.getVersionDefAccess().getActiveBooleanDefEnumRuleCall_2_1_0()); 
                    	    
                    pushFollow(FOLLOW_ruleBooleanDef_in_ruleVersionDef2034);
                    lv_active_3_0=ruleBooleanDef();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getVersionDefRule());
                    	        }
                           		set(
                           			current, 
                           			"active",
                            		lv_active_3_0, 
                            		"BooleanDef");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1027:4: (otherlv_4= 'staticId' ( (lv_staticId_5_0= RULE_STRING ) ) )*
            loop34:
            do {
                int alt34=2;
                int LA34_0 = input.LA(1);

                if ( (LA34_0==22) ) {
                    alt34=1;
                }


                switch (alt34) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1027:6: otherlv_4= 'staticId' ( (lv_staticId_5_0= RULE_STRING ) )
            	    {
            	    otherlv_4=(Token)match(input,22,FOLLOW_22_in_ruleVersionDef2049); 

            	        	newLeafNode(otherlv_4, grammarAccess.getVersionDefAccess().getStaticIdKeyword_3_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1031:1: ( (lv_staticId_5_0= RULE_STRING ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1032:1: (lv_staticId_5_0= RULE_STRING )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1032:1: (lv_staticId_5_0= RULE_STRING )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1033:3: lv_staticId_5_0= RULE_STRING
            	    {
            	    lv_staticId_5_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleVersionDef2066); 

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
            	    break loop34;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1049:4: (otherlv_6= 'next' ( (lv_next_7_0= ruleBooleanDef ) ) )?
            int alt35=2;
            int LA35_0 = input.LA(1);

            if ( (LA35_0==31) ) {
                alt35=1;
            }
            switch (alt35) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1049:6: otherlv_6= 'next' ( (lv_next_7_0= ruleBooleanDef ) )
                    {
                    otherlv_6=(Token)match(input,31,FOLLOW_31_in_ruleVersionDef2086); 

                        	newLeafNode(otherlv_6, grammarAccess.getVersionDefAccess().getNextKeyword_4_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1053:1: ( (lv_next_7_0= ruleBooleanDef ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1054:1: (lv_next_7_0= ruleBooleanDef )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1054:1: (lv_next_7_0= ruleBooleanDef )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1055:3: lv_next_7_0= ruleBooleanDef
                    {
                     
                    	        newCompositeNode(grammarAccess.getVersionDefAccess().getNextBooleanDefEnumRuleCall_4_1_0()); 
                    	    
                    pushFollow(FOLLOW_ruleBooleanDef_in_ruleVersionDef2107);
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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1071:4: (otherlv_8= 'released' ( (lv_released_9_0= ruleBooleanDef ) ) )?
            int alt36=2;
            int LA36_0 = input.LA(1);

            if ( (LA36_0==32) ) {
                alt36=1;
            }
            switch (alt36) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1071:6: otherlv_8= 'released' ( (lv_released_9_0= ruleBooleanDef ) )
                    {
                    otherlv_8=(Token)match(input,32,FOLLOW_32_in_ruleVersionDef2122); 

                        	newLeafNode(otherlv_8, grammarAccess.getVersionDefAccess().getReleasedKeyword_5_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1075:1: ( (lv_released_9_0= ruleBooleanDef ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1076:1: (lv_released_9_0= ruleBooleanDef )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1076:1: (lv_released_9_0= ruleBooleanDef )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1077:3: lv_released_9_0= ruleBooleanDef
                    {
                     
                    	        newCompositeNode(grammarAccess.getVersionDefAccess().getReleasedBooleanDefEnumRuleCall_5_1_0()); 
                    	    
                    pushFollow(FOLLOW_ruleBooleanDef_in_ruleVersionDef2143);
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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1093:4: (otherlv_10= 'allowCreateBranch' ( (lv_allowCreateBranch_11_0= ruleBooleanDef ) ) )?
            int alt37=2;
            int LA37_0 = input.LA(1);

            if ( (LA37_0==33) ) {
                alt37=1;
            }
            switch (alt37) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1093:6: otherlv_10= 'allowCreateBranch' ( (lv_allowCreateBranch_11_0= ruleBooleanDef ) )
                    {
                    otherlv_10=(Token)match(input,33,FOLLOW_33_in_ruleVersionDef2158); 

                        	newLeafNode(otherlv_10, grammarAccess.getVersionDefAccess().getAllowCreateBranchKeyword_6_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1097:1: ( (lv_allowCreateBranch_11_0= ruleBooleanDef ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1098:1: (lv_allowCreateBranch_11_0= ruleBooleanDef )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1098:1: (lv_allowCreateBranch_11_0= ruleBooleanDef )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1099:3: lv_allowCreateBranch_11_0= ruleBooleanDef
                    {
                     
                    	        newCompositeNode(grammarAccess.getVersionDefAccess().getAllowCreateBranchBooleanDefEnumRuleCall_6_1_0()); 
                    	    
                    pushFollow(FOLLOW_ruleBooleanDef_in_ruleVersionDef2179);
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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1115:4: (otherlv_12= 'allowCommitBranch' ( (lv_allowCommitBranch_13_0= ruleBooleanDef ) ) )?
            int alt38=2;
            int LA38_0 = input.LA(1);

            if ( (LA38_0==34) ) {
                alt38=1;
            }
            switch (alt38) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1115:6: otherlv_12= 'allowCommitBranch' ( (lv_allowCommitBranch_13_0= ruleBooleanDef ) )
                    {
                    otherlv_12=(Token)match(input,34,FOLLOW_34_in_ruleVersionDef2194); 

                        	newLeafNode(otherlv_12, grammarAccess.getVersionDefAccess().getAllowCommitBranchKeyword_7_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1119:1: ( (lv_allowCommitBranch_13_0= ruleBooleanDef ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1120:1: (lv_allowCommitBranch_13_0= ruleBooleanDef )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1120:1: (lv_allowCommitBranch_13_0= ruleBooleanDef )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1121:3: lv_allowCommitBranch_13_0= ruleBooleanDef
                    {
                     
                    	        newCompositeNode(grammarAccess.getVersionDefAccess().getAllowCommitBranchBooleanDefEnumRuleCall_7_1_0()); 
                    	    
                    pushFollow(FOLLOW_ruleBooleanDef_in_ruleVersionDef2215);
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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1137:4: (otherlv_14= 'baslineBranchGuid' ( (lv_baselineBranchGuid_15_0= RULE_STRING ) ) )?
            int alt39=2;
            int LA39_0 = input.LA(1);

            if ( (LA39_0==35) ) {
                alt39=1;
            }
            switch (alt39) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1137:6: otherlv_14= 'baslineBranchGuid' ( (lv_baselineBranchGuid_15_0= RULE_STRING ) )
                    {
                    otherlv_14=(Token)match(input,35,FOLLOW_35_in_ruleVersionDef2230); 

                        	newLeafNode(otherlv_14, grammarAccess.getVersionDefAccess().getBaslineBranchGuidKeyword_8_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1141:1: ( (lv_baselineBranchGuid_15_0= RULE_STRING ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1142:1: (lv_baselineBranchGuid_15_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1142:1: (lv_baselineBranchGuid_15_0= RULE_STRING )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1143:3: lv_baselineBranchGuid_15_0= RULE_STRING
                    {
                    lv_baselineBranchGuid_15_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleVersionDef2247); 

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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1159:4: (otherlv_16= 'parallelVersion' ( (lv_parallelVersion_17_0= RULE_STRING ) ) )*
            loop40:
            do {
                int alt40=2;
                int LA40_0 = input.LA(1);

                if ( (LA40_0==36) ) {
                    alt40=1;
                }


                switch (alt40) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1159:6: otherlv_16= 'parallelVersion' ( (lv_parallelVersion_17_0= RULE_STRING ) )
            	    {
            	    otherlv_16=(Token)match(input,36,FOLLOW_36_in_ruleVersionDef2267); 

            	        	newLeafNode(otherlv_16, grammarAccess.getVersionDefAccess().getParallelVersionKeyword_9_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1163:1: ( (lv_parallelVersion_17_0= RULE_STRING ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1164:1: (lv_parallelVersion_17_0= RULE_STRING )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1164:1: (lv_parallelVersion_17_0= RULE_STRING )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1165:3: lv_parallelVersion_17_0= RULE_STRING
            	    {
            	    lv_parallelVersion_17_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleVersionDef2284); 

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
            	    break loop40;
                }
            } while (true);

            otherlv_18=(Token)match(input,20,FOLLOW_20_in_ruleVersionDef2303); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1193:1: entryRuleWorkDef returns [EObject current=null] : iv_ruleWorkDef= ruleWorkDef EOF ;
    public final EObject entryRuleWorkDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleWorkDef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1194:2: (iv_ruleWorkDef= ruleWorkDef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1195:2: iv_ruleWorkDef= ruleWorkDef EOF
            {
             newCompositeNode(grammarAccess.getWorkDefRule()); 
            pushFollow(FOLLOW_ruleWorkDef_in_entryRuleWorkDef2339);
            iv_ruleWorkDef=ruleWorkDef();

            state._fsp--;

             current =iv_ruleWorkDef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleWorkDef2349); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1202:1: ruleWorkDef returns [EObject current=null] : ( ( (lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'id' ( (lv_id_3_0= RULE_STRING ) ) )+ (otherlv_4= 'startState' ( ( ruleSTATE_NAME_REFERENCE ) ) ) ( (lv_widgetDefs_6_0= ruleWidgetDef ) )* ( (lv_decisionReviewDefs_7_0= ruleDecisionReviewDef ) )* ( (lv_peerReviewDefs_8_0= rulePeerReviewDef ) )* ( (lv_states_9_0= ruleStateDef ) )+ otherlv_10= '}' ) ;
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
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1205:28: ( ( ( (lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'id' ( (lv_id_3_0= RULE_STRING ) ) )+ (otherlv_4= 'startState' ( ( ruleSTATE_NAME_REFERENCE ) ) ) ( (lv_widgetDefs_6_0= ruleWidgetDef ) )* ( (lv_decisionReviewDefs_7_0= ruleDecisionReviewDef ) )* ( (lv_peerReviewDefs_8_0= rulePeerReviewDef ) )* ( (lv_states_9_0= ruleStateDef ) )+ otherlv_10= '}' ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1206:1: ( ( (lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'id' ( (lv_id_3_0= RULE_STRING ) ) )+ (otherlv_4= 'startState' ( ( ruleSTATE_NAME_REFERENCE ) ) ) ( (lv_widgetDefs_6_0= ruleWidgetDef ) )* ( (lv_decisionReviewDefs_7_0= ruleDecisionReviewDef ) )* ( (lv_peerReviewDefs_8_0= rulePeerReviewDef ) )* ( (lv_states_9_0= ruleStateDef ) )+ otherlv_10= '}' )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1206:1: ( ( (lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'id' ( (lv_id_3_0= RULE_STRING ) ) )+ (otherlv_4= 'startState' ( ( ruleSTATE_NAME_REFERENCE ) ) ) ( (lv_widgetDefs_6_0= ruleWidgetDef ) )* ( (lv_decisionReviewDefs_7_0= ruleDecisionReviewDef ) )* ( (lv_peerReviewDefs_8_0= rulePeerReviewDef ) )* ( (lv_states_9_0= ruleStateDef ) )+ otherlv_10= '}' )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1206:2: ( (lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'id' ( (lv_id_3_0= RULE_STRING ) ) )+ (otherlv_4= 'startState' ( ( ruleSTATE_NAME_REFERENCE ) ) ) ( (lv_widgetDefs_6_0= ruleWidgetDef ) )* ( (lv_decisionReviewDefs_7_0= ruleDecisionReviewDef ) )* ( (lv_peerReviewDefs_8_0= rulePeerReviewDef ) )* ( (lv_states_9_0= ruleStateDef ) )+ otherlv_10= '}'
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1206:2: ( (lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1207:1: (lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1207:1: (lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1208:3: lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE
            {
             
            	        newCompositeNode(grammarAccess.getWorkDefAccess().getNameWORK_DEFINITION_NAME_REFERENCEParserRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_ruleWORK_DEFINITION_NAME_REFERENCE_in_ruleWorkDef2395);
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

            otherlv_1=(Token)match(input,15,FOLLOW_15_in_ruleWorkDef2407); 

                	newLeafNode(otherlv_1, grammarAccess.getWorkDefAccess().getLeftCurlyBracketKeyword_1());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1228:1: (otherlv_2= 'id' ( (lv_id_3_0= RULE_STRING ) ) )+
            int cnt41=0;
            loop41:
            do {
                int alt41=2;
                int LA41_0 = input.LA(1);

                if ( (LA41_0==37) ) {
                    alt41=1;
                }


                switch (alt41) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1228:3: otherlv_2= 'id' ( (lv_id_3_0= RULE_STRING ) )
            	    {
            	    otherlv_2=(Token)match(input,37,FOLLOW_37_in_ruleWorkDef2420); 

            	        	newLeafNode(otherlv_2, grammarAccess.getWorkDefAccess().getIdKeyword_2_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1232:1: ( (lv_id_3_0= RULE_STRING ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1233:1: (lv_id_3_0= RULE_STRING )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1233:1: (lv_id_3_0= RULE_STRING )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1234:3: lv_id_3_0= RULE_STRING
            	    {
            	    lv_id_3_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleWorkDef2437); 

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
            	    if ( cnt41 >= 1 ) break loop41;
                        EarlyExitException eee =
                            new EarlyExitException(41, input);
                        throw eee;
                }
                cnt41++;
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1250:4: (otherlv_4= 'startState' ( ( ruleSTATE_NAME_REFERENCE ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1250:6: otherlv_4= 'startState' ( ( ruleSTATE_NAME_REFERENCE ) )
            {
            otherlv_4=(Token)match(input,38,FOLLOW_38_in_ruleWorkDef2457); 

                	newLeafNode(otherlv_4, grammarAccess.getWorkDefAccess().getStartStateKeyword_3_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1254:1: ( ( ruleSTATE_NAME_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1255:1: ( ruleSTATE_NAME_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1255:1: ( ruleSTATE_NAME_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1256:3: ruleSTATE_NAME_REFERENCE
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getWorkDefRule());
            	        }
                    
             
            	        newCompositeNode(grammarAccess.getWorkDefAccess().getStartStateStateDefCrossReference_3_1_0()); 
            	    
            pushFollow(FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleWorkDef2480);
            ruleSTATE_NAME_REFERENCE();

            state._fsp--;

             
            	        afterParserOrEnumRuleCall();
            	    

            }


            }


            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1269:3: ( (lv_widgetDefs_6_0= ruleWidgetDef ) )*
            loop42:
            do {
                int alt42=2;
                int LA42_0 = input.LA(1);

                if ( (LA42_0==39) ) {
                    alt42=1;
                }


                switch (alt42) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1270:1: (lv_widgetDefs_6_0= ruleWidgetDef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1270:1: (lv_widgetDefs_6_0= ruleWidgetDef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1271:3: lv_widgetDefs_6_0= ruleWidgetDef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getWorkDefAccess().getWidgetDefsWidgetDefParserRuleCall_4_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleWidgetDef_in_ruleWorkDef2502);
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
            	    break loop42;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1287:3: ( (lv_decisionReviewDefs_7_0= ruleDecisionReviewDef ) )*
            loop43:
            do {
                int alt43=2;
                int LA43_0 = input.LA(1);

                if ( (LA43_0==55) ) {
                    alt43=1;
                }


                switch (alt43) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1288:1: (lv_decisionReviewDefs_7_0= ruleDecisionReviewDef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1288:1: (lv_decisionReviewDefs_7_0= ruleDecisionReviewDef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1289:3: lv_decisionReviewDefs_7_0= ruleDecisionReviewDef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getWorkDefAccess().getDecisionReviewDefsDecisionReviewDefParserRuleCall_5_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleDecisionReviewDef_in_ruleWorkDef2524);
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
            	    break loop43;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1305:3: ( (lv_peerReviewDefs_8_0= rulePeerReviewDef ) )*
            loop44:
            do {
                int alt44=2;
                int LA44_0 = input.LA(1);

                if ( (LA44_0==63) ) {
                    alt44=1;
                }


                switch (alt44) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1306:1: (lv_peerReviewDefs_8_0= rulePeerReviewDef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1306:1: (lv_peerReviewDefs_8_0= rulePeerReviewDef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1307:3: lv_peerReviewDefs_8_0= rulePeerReviewDef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getWorkDefAccess().getPeerReviewDefsPeerReviewDefParserRuleCall_6_0()); 
            	    	    
            	    pushFollow(FOLLOW_rulePeerReviewDef_in_ruleWorkDef2546);
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
            	    break loop44;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1323:3: ( (lv_states_9_0= ruleStateDef ) )+
            int cnt45=0;
            loop45:
            do {
                int alt45=2;
                int LA45_0 = input.LA(1);

                if ( (LA45_0==49) ) {
                    alt45=1;
                }


                switch (alt45) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1324:1: (lv_states_9_0= ruleStateDef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1324:1: (lv_states_9_0= ruleStateDef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1325:3: lv_states_9_0= ruleStateDef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getWorkDefAccess().getStatesStateDefParserRuleCall_7_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleStateDef_in_ruleWorkDef2568);
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
            	    if ( cnt45 >= 1 ) break loop45;
                        EarlyExitException eee =
                            new EarlyExitException(45, input);
                        throw eee;
                }
                cnt45++;
            } while (true);

            otherlv_10=(Token)match(input,20,FOLLOW_20_in_ruleWorkDef2581); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1353:1: entryRuleWidgetDef returns [EObject current=null] : iv_ruleWidgetDef= ruleWidgetDef EOF ;
    public final EObject entryRuleWidgetDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleWidgetDef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1354:2: (iv_ruleWidgetDef= ruleWidgetDef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1355:2: iv_ruleWidgetDef= ruleWidgetDef EOF
            {
             newCompositeNode(grammarAccess.getWidgetDefRule()); 
            pushFollow(FOLLOW_ruleWidgetDef_in_entryRuleWidgetDef2617);
            iv_ruleWidgetDef=ruleWidgetDef();

            state._fsp--;

             current =iv_ruleWidgetDef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleWidgetDef2627); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1362:1: ruleWidgetDef returns [EObject current=null] : (otherlv_0= 'widgetDefinition' ( (lv_name_1_0= ruleWIDGET_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'attributeName' ( (lv_attributeName_4_0= RULE_STRING ) ) )? (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'xWidgetName' ( (lv_xWidgetName_8_0= RULE_STRING ) ) )? (otherlv_9= 'defaultValue' ( (lv_defaultValue_10_0= RULE_STRING ) ) )? (otherlv_11= 'height' ( (lv_height_12_0= RULE_INT ) ) )? (otherlv_13= 'option' ( (lv_option_14_0= ruleWidgetOption ) ) )* otherlv_15= '}' ) ;
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
        AntlrDatatypeRuleToken lv_name_1_0 = null;

        AntlrDatatypeRuleToken lv_option_14_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1365:28: ( (otherlv_0= 'widgetDefinition' ( (lv_name_1_0= ruleWIDGET_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'attributeName' ( (lv_attributeName_4_0= RULE_STRING ) ) )? (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'xWidgetName' ( (lv_xWidgetName_8_0= RULE_STRING ) ) )? (otherlv_9= 'defaultValue' ( (lv_defaultValue_10_0= RULE_STRING ) ) )? (otherlv_11= 'height' ( (lv_height_12_0= RULE_INT ) ) )? (otherlv_13= 'option' ( (lv_option_14_0= ruleWidgetOption ) ) )* otherlv_15= '}' ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1366:1: (otherlv_0= 'widgetDefinition' ( (lv_name_1_0= ruleWIDGET_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'attributeName' ( (lv_attributeName_4_0= RULE_STRING ) ) )? (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'xWidgetName' ( (lv_xWidgetName_8_0= RULE_STRING ) ) )? (otherlv_9= 'defaultValue' ( (lv_defaultValue_10_0= RULE_STRING ) ) )? (otherlv_11= 'height' ( (lv_height_12_0= RULE_INT ) ) )? (otherlv_13= 'option' ( (lv_option_14_0= ruleWidgetOption ) ) )* otherlv_15= '}' )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1366:1: (otherlv_0= 'widgetDefinition' ( (lv_name_1_0= ruleWIDGET_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'attributeName' ( (lv_attributeName_4_0= RULE_STRING ) ) )? (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'xWidgetName' ( (lv_xWidgetName_8_0= RULE_STRING ) ) )? (otherlv_9= 'defaultValue' ( (lv_defaultValue_10_0= RULE_STRING ) ) )? (otherlv_11= 'height' ( (lv_height_12_0= RULE_INT ) ) )? (otherlv_13= 'option' ( (lv_option_14_0= ruleWidgetOption ) ) )* otherlv_15= '}' )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1366:3: otherlv_0= 'widgetDefinition' ( (lv_name_1_0= ruleWIDGET_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'attributeName' ( (lv_attributeName_4_0= RULE_STRING ) ) )? (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'xWidgetName' ( (lv_xWidgetName_8_0= RULE_STRING ) ) )? (otherlv_9= 'defaultValue' ( (lv_defaultValue_10_0= RULE_STRING ) ) )? (otherlv_11= 'height' ( (lv_height_12_0= RULE_INT ) ) )? (otherlv_13= 'option' ( (lv_option_14_0= ruleWidgetOption ) ) )* otherlv_15= '}'
            {
            otherlv_0=(Token)match(input,39,FOLLOW_39_in_ruleWidgetDef2664); 

                	newLeafNode(otherlv_0, grammarAccess.getWidgetDefAccess().getWidgetDefinitionKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1370:1: ( (lv_name_1_0= ruleWIDGET_NAME_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1371:1: (lv_name_1_0= ruleWIDGET_NAME_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1371:1: (lv_name_1_0= ruleWIDGET_NAME_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1372:3: lv_name_1_0= ruleWIDGET_NAME_REFERENCE
            {
             
            	        newCompositeNode(grammarAccess.getWidgetDefAccess().getNameWIDGET_NAME_REFERENCEParserRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_ruleWIDGET_NAME_REFERENCE_in_ruleWidgetDef2685);
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

            otherlv_2=(Token)match(input,15,FOLLOW_15_in_ruleWidgetDef2697); 

                	newLeafNode(otherlv_2, grammarAccess.getWidgetDefAccess().getLeftCurlyBracketKeyword_2());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1392:1: (otherlv_3= 'attributeName' ( (lv_attributeName_4_0= RULE_STRING ) ) )?
            int alt46=2;
            int LA46_0 = input.LA(1);

            if ( (LA46_0==40) ) {
                alt46=1;
            }
            switch (alt46) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1392:3: otherlv_3= 'attributeName' ( (lv_attributeName_4_0= RULE_STRING ) )
                    {
                    otherlv_3=(Token)match(input,40,FOLLOW_40_in_ruleWidgetDef2710); 

                        	newLeafNode(otherlv_3, grammarAccess.getWidgetDefAccess().getAttributeNameKeyword_3_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1396:1: ( (lv_attributeName_4_0= RULE_STRING ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1397:1: (lv_attributeName_4_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1397:1: (lv_attributeName_4_0= RULE_STRING )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1398:3: lv_attributeName_4_0= RULE_STRING
                    {
                    lv_attributeName_4_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleWidgetDef2727); 

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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1414:4: (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )?
            int alt47=2;
            int LA47_0 = input.LA(1);

            if ( (LA47_0==41) ) {
                alt47=1;
            }
            switch (alt47) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1414:6: otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) )
                    {
                    otherlv_5=(Token)match(input,41,FOLLOW_41_in_ruleWidgetDef2747); 

                        	newLeafNode(otherlv_5, grammarAccess.getWidgetDefAccess().getDescriptionKeyword_4_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1418:1: ( (lv_description_6_0= RULE_STRING ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1419:1: (lv_description_6_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1419:1: (lv_description_6_0= RULE_STRING )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1420:3: lv_description_6_0= RULE_STRING
                    {
                    lv_description_6_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleWidgetDef2764); 

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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1436:4: (otherlv_7= 'xWidgetName' ( (lv_xWidgetName_8_0= RULE_STRING ) ) )?
            int alt48=2;
            int LA48_0 = input.LA(1);

            if ( (LA48_0==42) ) {
                alt48=1;
            }
            switch (alt48) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1436:6: otherlv_7= 'xWidgetName' ( (lv_xWidgetName_8_0= RULE_STRING ) )
                    {
                    otherlv_7=(Token)match(input,42,FOLLOW_42_in_ruleWidgetDef2784); 

                        	newLeafNode(otherlv_7, grammarAccess.getWidgetDefAccess().getXWidgetNameKeyword_5_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1440:1: ( (lv_xWidgetName_8_0= RULE_STRING ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1441:1: (lv_xWidgetName_8_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1441:1: (lv_xWidgetName_8_0= RULE_STRING )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1442:3: lv_xWidgetName_8_0= RULE_STRING
                    {
                    lv_xWidgetName_8_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleWidgetDef2801); 

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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1458:4: (otherlv_9= 'defaultValue' ( (lv_defaultValue_10_0= RULE_STRING ) ) )?
            int alt49=2;
            int LA49_0 = input.LA(1);

            if ( (LA49_0==43) ) {
                alt49=1;
            }
            switch (alt49) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1458:6: otherlv_9= 'defaultValue' ( (lv_defaultValue_10_0= RULE_STRING ) )
                    {
                    otherlv_9=(Token)match(input,43,FOLLOW_43_in_ruleWidgetDef2821); 

                        	newLeafNode(otherlv_9, grammarAccess.getWidgetDefAccess().getDefaultValueKeyword_6_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1462:1: ( (lv_defaultValue_10_0= RULE_STRING ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1463:1: (lv_defaultValue_10_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1463:1: (lv_defaultValue_10_0= RULE_STRING )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1464:3: lv_defaultValue_10_0= RULE_STRING
                    {
                    lv_defaultValue_10_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleWidgetDef2838); 

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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1480:4: (otherlv_11= 'height' ( (lv_height_12_0= RULE_INT ) ) )?
            int alt50=2;
            int LA50_0 = input.LA(1);

            if ( (LA50_0==44) ) {
                alt50=1;
            }
            switch (alt50) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1480:6: otherlv_11= 'height' ( (lv_height_12_0= RULE_INT ) )
                    {
                    otherlv_11=(Token)match(input,44,FOLLOW_44_in_ruleWidgetDef2858); 

                        	newLeafNode(otherlv_11, grammarAccess.getWidgetDefAccess().getHeightKeyword_7_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1484:1: ( (lv_height_12_0= RULE_INT ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1485:1: (lv_height_12_0= RULE_INT )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1485:1: (lv_height_12_0= RULE_INT )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1486:3: lv_height_12_0= RULE_INT
                    {
                    lv_height_12_0=(Token)match(input,RULE_INT,FOLLOW_RULE_INT_in_ruleWidgetDef2875); 

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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1502:4: (otherlv_13= 'option' ( (lv_option_14_0= ruleWidgetOption ) ) )*
            loop51:
            do {
                int alt51=2;
                int LA51_0 = input.LA(1);

                if ( (LA51_0==45) ) {
                    alt51=1;
                }


                switch (alt51) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1502:6: otherlv_13= 'option' ( (lv_option_14_0= ruleWidgetOption ) )
            	    {
            	    otherlv_13=(Token)match(input,45,FOLLOW_45_in_ruleWidgetDef2895); 

            	        	newLeafNode(otherlv_13, grammarAccess.getWidgetDefAccess().getOptionKeyword_8_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1506:1: ( (lv_option_14_0= ruleWidgetOption ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1507:1: (lv_option_14_0= ruleWidgetOption )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1507:1: (lv_option_14_0= ruleWidgetOption )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1508:3: lv_option_14_0= ruleWidgetOption
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getWidgetDefAccess().getOptionWidgetOptionParserRuleCall_8_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleWidgetOption_in_ruleWidgetDef2916);
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
            	    break loop51;
                }
            } while (true);

            otherlv_15=(Token)match(input,20,FOLLOW_20_in_ruleWidgetDef2930); 

                	newLeafNode(otherlv_15, grammarAccess.getWidgetDefAccess().getRightCurlyBracketKeyword_9());
                

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1536:1: entryRuleWidgetRef returns [EObject current=null] : iv_ruleWidgetRef= ruleWidgetRef EOF ;
    public final EObject entryRuleWidgetRef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleWidgetRef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1537:2: (iv_ruleWidgetRef= ruleWidgetRef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1538:2: iv_ruleWidgetRef= ruleWidgetRef EOF
            {
             newCompositeNode(grammarAccess.getWidgetRefRule()); 
            pushFollow(FOLLOW_ruleWidgetRef_in_entryRuleWidgetRef2966);
            iv_ruleWidgetRef=ruleWidgetRef();

            state._fsp--;

             current =iv_ruleWidgetRef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleWidgetRef2976); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1545:1: ruleWidgetRef returns [EObject current=null] : (otherlv_0= 'widget' ( ( ruleWIDGET_NAME_REFERENCE ) ) ) ;
    public final EObject ruleWidgetRef() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1548:28: ( (otherlv_0= 'widget' ( ( ruleWIDGET_NAME_REFERENCE ) ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1549:1: (otherlv_0= 'widget' ( ( ruleWIDGET_NAME_REFERENCE ) ) )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1549:1: (otherlv_0= 'widget' ( ( ruleWIDGET_NAME_REFERENCE ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1549:3: otherlv_0= 'widget' ( ( ruleWIDGET_NAME_REFERENCE ) )
            {
            otherlv_0=(Token)match(input,46,FOLLOW_46_in_ruleWidgetRef3013); 

                	newLeafNode(otherlv_0, grammarAccess.getWidgetRefAccess().getWidgetKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1553:1: ( ( ruleWIDGET_NAME_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1554:1: ( ruleWIDGET_NAME_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1554:1: ( ruleWIDGET_NAME_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1555:3: ruleWIDGET_NAME_REFERENCE
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getWidgetRefRule());
            	        }
                    
             
            	        newCompositeNode(grammarAccess.getWidgetRefAccess().getWidgetWidgetDefCrossReference_1_0()); 
            	    
            pushFollow(FOLLOW_ruleWIDGET_NAME_REFERENCE_in_ruleWidgetRef3036);
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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1576:1: entryRuleAttrWidget returns [EObject current=null] : iv_ruleAttrWidget= ruleAttrWidget EOF ;
    public final EObject entryRuleAttrWidget() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttrWidget = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1577:2: (iv_ruleAttrWidget= ruleAttrWidget EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1578:2: iv_ruleAttrWidget= ruleAttrWidget EOF
            {
             newCompositeNode(grammarAccess.getAttrWidgetRule()); 
            pushFollow(FOLLOW_ruleAttrWidget_in_entryRuleAttrWidget3072);
            iv_ruleAttrWidget=ruleAttrWidget();

            state._fsp--;

             current =iv_ruleAttrWidget; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttrWidget3082); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1585:1: ruleAttrWidget returns [EObject current=null] : (otherlv_0= 'attributeWidget' ( (lv_attributeName_1_0= RULE_STRING ) ) (otherlv_2= 'with' ( (lv_option_3_0= ruleWidgetOption ) ) )* ) ;
    public final EObject ruleAttrWidget() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_attributeName_1_0=null;
        Token otherlv_2=null;
        AntlrDatatypeRuleToken lv_option_3_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1588:28: ( (otherlv_0= 'attributeWidget' ( (lv_attributeName_1_0= RULE_STRING ) ) (otherlv_2= 'with' ( (lv_option_3_0= ruleWidgetOption ) ) )* ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1589:1: (otherlv_0= 'attributeWidget' ( (lv_attributeName_1_0= RULE_STRING ) ) (otherlv_2= 'with' ( (lv_option_3_0= ruleWidgetOption ) ) )* )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1589:1: (otherlv_0= 'attributeWidget' ( (lv_attributeName_1_0= RULE_STRING ) ) (otherlv_2= 'with' ( (lv_option_3_0= ruleWidgetOption ) ) )* )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1589:3: otherlv_0= 'attributeWidget' ( (lv_attributeName_1_0= RULE_STRING ) ) (otherlv_2= 'with' ( (lv_option_3_0= ruleWidgetOption ) ) )*
            {
            otherlv_0=(Token)match(input,47,FOLLOW_47_in_ruleAttrWidget3119); 

                	newLeafNode(otherlv_0, grammarAccess.getAttrWidgetAccess().getAttributeWidgetKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1593:1: ( (lv_attributeName_1_0= RULE_STRING ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1594:1: (lv_attributeName_1_0= RULE_STRING )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1594:1: (lv_attributeName_1_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1595:3: lv_attributeName_1_0= RULE_STRING
            {
            lv_attributeName_1_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAttrWidget3136); 

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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1611:2: (otherlv_2= 'with' ( (lv_option_3_0= ruleWidgetOption ) ) )*
            loop52:
            do {
                int alt52=2;
                int LA52_0 = input.LA(1);

                if ( (LA52_0==48) ) {
                    alt52=1;
                }


                switch (alt52) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1611:4: otherlv_2= 'with' ( (lv_option_3_0= ruleWidgetOption ) )
            	    {
            	    otherlv_2=(Token)match(input,48,FOLLOW_48_in_ruleAttrWidget3154); 

            	        	newLeafNode(otherlv_2, grammarAccess.getAttrWidgetAccess().getWithKeyword_2_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1615:1: ( (lv_option_3_0= ruleWidgetOption ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1616:1: (lv_option_3_0= ruleWidgetOption )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1616:1: (lv_option_3_0= ruleWidgetOption )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1617:3: lv_option_3_0= ruleWidgetOption
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getAttrWidgetAccess().getOptionWidgetOptionParserRuleCall_2_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleWidgetOption_in_ruleAttrWidget3175);
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
            	    break loop52;
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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1641:1: entryRuleStateDef returns [EObject current=null] : iv_ruleStateDef= ruleStateDef EOF ;
    public final EObject entryRuleStateDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleStateDef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1642:2: (iv_ruleStateDef= ruleStateDef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1643:2: iv_ruleStateDef= ruleStateDef EOF
            {
             newCompositeNode(grammarAccess.getStateDefRule()); 
            pushFollow(FOLLOW_ruleStateDef_in_entryRuleStateDef3213);
            iv_ruleStateDef=ruleStateDef();

            state._fsp--;

             current =iv_ruleStateDef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleStateDef3223); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1650:1: ruleStateDef returns [EObject current=null] : (otherlv_0= 'state' ( (lv_name_1_0= ruleSTATE_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'description' ( (lv_description_4_0= RULE_STRING ) ) )? otherlv_5= 'type' ( (lv_pageType_6_0= rulePageType ) ) otherlv_7= 'ordinal' ( (lv_ordinal_8_0= RULE_INT ) ) ( (lv_transitionStates_9_0= ruleToState ) )* (otherlv_10= 'rule' ( (lv_rules_11_0= ruleRule ) ) )* ( (lv_decisionReviews_12_0= ruleDecisionReviewRef ) )* ( (lv_peerReviews_13_0= rulePeerReviewRef ) )* (otherlv_14= 'percentWeight' ( (lv_percentWeight_15_0= RULE_INT ) ) )? ( (lv_layout_16_0= ruleLayoutType ) )? otherlv_17= '}' ) ;
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
        Token otherlv_17=null;
        AntlrDatatypeRuleToken lv_name_1_0 = null;

        AntlrDatatypeRuleToken lv_pageType_6_0 = null;

        EObject lv_transitionStates_9_0 = null;

        AntlrDatatypeRuleToken lv_rules_11_0 = null;

        EObject lv_decisionReviews_12_0 = null;

        EObject lv_peerReviews_13_0 = null;

        EObject lv_layout_16_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1653:28: ( (otherlv_0= 'state' ( (lv_name_1_0= ruleSTATE_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'description' ( (lv_description_4_0= RULE_STRING ) ) )? otherlv_5= 'type' ( (lv_pageType_6_0= rulePageType ) ) otherlv_7= 'ordinal' ( (lv_ordinal_8_0= RULE_INT ) ) ( (lv_transitionStates_9_0= ruleToState ) )* (otherlv_10= 'rule' ( (lv_rules_11_0= ruleRule ) ) )* ( (lv_decisionReviews_12_0= ruleDecisionReviewRef ) )* ( (lv_peerReviews_13_0= rulePeerReviewRef ) )* (otherlv_14= 'percentWeight' ( (lv_percentWeight_15_0= RULE_INT ) ) )? ( (lv_layout_16_0= ruleLayoutType ) )? otherlv_17= '}' ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1654:1: (otherlv_0= 'state' ( (lv_name_1_0= ruleSTATE_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'description' ( (lv_description_4_0= RULE_STRING ) ) )? otherlv_5= 'type' ( (lv_pageType_6_0= rulePageType ) ) otherlv_7= 'ordinal' ( (lv_ordinal_8_0= RULE_INT ) ) ( (lv_transitionStates_9_0= ruleToState ) )* (otherlv_10= 'rule' ( (lv_rules_11_0= ruleRule ) ) )* ( (lv_decisionReviews_12_0= ruleDecisionReviewRef ) )* ( (lv_peerReviews_13_0= rulePeerReviewRef ) )* (otherlv_14= 'percentWeight' ( (lv_percentWeight_15_0= RULE_INT ) ) )? ( (lv_layout_16_0= ruleLayoutType ) )? otherlv_17= '}' )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1654:1: (otherlv_0= 'state' ( (lv_name_1_0= ruleSTATE_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'description' ( (lv_description_4_0= RULE_STRING ) ) )? otherlv_5= 'type' ( (lv_pageType_6_0= rulePageType ) ) otherlv_7= 'ordinal' ( (lv_ordinal_8_0= RULE_INT ) ) ( (lv_transitionStates_9_0= ruleToState ) )* (otherlv_10= 'rule' ( (lv_rules_11_0= ruleRule ) ) )* ( (lv_decisionReviews_12_0= ruleDecisionReviewRef ) )* ( (lv_peerReviews_13_0= rulePeerReviewRef ) )* (otherlv_14= 'percentWeight' ( (lv_percentWeight_15_0= RULE_INT ) ) )? ( (lv_layout_16_0= ruleLayoutType ) )? otherlv_17= '}' )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1654:3: otherlv_0= 'state' ( (lv_name_1_0= ruleSTATE_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'description' ( (lv_description_4_0= RULE_STRING ) ) )? otherlv_5= 'type' ( (lv_pageType_6_0= rulePageType ) ) otherlv_7= 'ordinal' ( (lv_ordinal_8_0= RULE_INT ) ) ( (lv_transitionStates_9_0= ruleToState ) )* (otherlv_10= 'rule' ( (lv_rules_11_0= ruleRule ) ) )* ( (lv_decisionReviews_12_0= ruleDecisionReviewRef ) )* ( (lv_peerReviews_13_0= rulePeerReviewRef ) )* (otherlv_14= 'percentWeight' ( (lv_percentWeight_15_0= RULE_INT ) ) )? ( (lv_layout_16_0= ruleLayoutType ) )? otherlv_17= '}'
            {
            otherlv_0=(Token)match(input,49,FOLLOW_49_in_ruleStateDef3260); 

                	newLeafNode(otherlv_0, grammarAccess.getStateDefAccess().getStateKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1658:1: ( (lv_name_1_0= ruleSTATE_NAME_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1659:1: (lv_name_1_0= ruleSTATE_NAME_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1659:1: (lv_name_1_0= ruleSTATE_NAME_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1660:3: lv_name_1_0= ruleSTATE_NAME_REFERENCE
            {
             
            	        newCompositeNode(grammarAccess.getStateDefAccess().getNameSTATE_NAME_REFERENCEParserRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleStateDef3281);
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

            otherlv_2=(Token)match(input,15,FOLLOW_15_in_ruleStateDef3293); 

                	newLeafNode(otherlv_2, grammarAccess.getStateDefAccess().getLeftCurlyBracketKeyword_2());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1680:1: (otherlv_3= 'description' ( (lv_description_4_0= RULE_STRING ) ) )?
            int alt53=2;
            int LA53_0 = input.LA(1);

            if ( (LA53_0==41) ) {
                alt53=1;
            }
            switch (alt53) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1680:3: otherlv_3= 'description' ( (lv_description_4_0= RULE_STRING ) )
                    {
                    otherlv_3=(Token)match(input,41,FOLLOW_41_in_ruleStateDef3306); 

                        	newLeafNode(otherlv_3, grammarAccess.getStateDefAccess().getDescriptionKeyword_3_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1684:1: ( (lv_description_4_0= RULE_STRING ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1685:1: (lv_description_4_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1685:1: (lv_description_4_0= RULE_STRING )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1686:3: lv_description_4_0= RULE_STRING
                    {
                    lv_description_4_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleStateDef3323); 

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

            otherlv_5=(Token)match(input,50,FOLLOW_50_in_ruleStateDef3342); 

                	newLeafNode(otherlv_5, grammarAccess.getStateDefAccess().getTypeKeyword_4());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1706:1: ( (lv_pageType_6_0= rulePageType ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1707:1: (lv_pageType_6_0= rulePageType )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1707:1: (lv_pageType_6_0= rulePageType )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1708:3: lv_pageType_6_0= rulePageType
            {
             
            	        newCompositeNode(grammarAccess.getStateDefAccess().getPageTypePageTypeParserRuleCall_5_0()); 
            	    
            pushFollow(FOLLOW_rulePageType_in_ruleStateDef3363);
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

            otherlv_7=(Token)match(input,51,FOLLOW_51_in_ruleStateDef3375); 

                	newLeafNode(otherlv_7, grammarAccess.getStateDefAccess().getOrdinalKeyword_6());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1728:1: ( (lv_ordinal_8_0= RULE_INT ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1729:1: (lv_ordinal_8_0= RULE_INT )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1729:1: (lv_ordinal_8_0= RULE_INT )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1730:3: lv_ordinal_8_0= RULE_INT
            {
            lv_ordinal_8_0=(Token)match(input,RULE_INT,FOLLOW_RULE_INT_in_ruleStateDef3392); 

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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1746:2: ( (lv_transitionStates_9_0= ruleToState ) )*
            loop54:
            do {
                int alt54=2;
                int LA54_0 = input.LA(1);

                if ( (LA54_0==67) ) {
                    alt54=1;
                }


                switch (alt54) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1747:1: (lv_transitionStates_9_0= ruleToState )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1747:1: (lv_transitionStates_9_0= ruleToState )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1748:3: lv_transitionStates_9_0= ruleToState
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getStateDefAccess().getTransitionStatesToStateParserRuleCall_8_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleToState_in_ruleStateDef3418);
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
            	    break loop54;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1764:3: (otherlv_10= 'rule' ( (lv_rules_11_0= ruleRule ) ) )*
            loop55:
            do {
                int alt55=2;
                int LA55_0 = input.LA(1);

                if ( (LA55_0==52) ) {
                    alt55=1;
                }


                switch (alt55) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1764:5: otherlv_10= 'rule' ( (lv_rules_11_0= ruleRule ) )
            	    {
            	    otherlv_10=(Token)match(input,52,FOLLOW_52_in_ruleStateDef3432); 

            	        	newLeafNode(otherlv_10, grammarAccess.getStateDefAccess().getRuleKeyword_9_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1768:1: ( (lv_rules_11_0= ruleRule ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1769:1: (lv_rules_11_0= ruleRule )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1769:1: (lv_rules_11_0= ruleRule )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1770:3: lv_rules_11_0= ruleRule
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getStateDefAccess().getRulesRuleParserRuleCall_9_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleRule_in_ruleStateDef3453);
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
            	    break loop55;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1786:4: ( (lv_decisionReviews_12_0= ruleDecisionReviewRef ) )*
            loop56:
            do {
                int alt56=2;
                int LA56_0 = input.LA(1);

                if ( (LA56_0==54) ) {
                    alt56=1;
                }


                switch (alt56) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1787:1: (lv_decisionReviews_12_0= ruleDecisionReviewRef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1787:1: (lv_decisionReviews_12_0= ruleDecisionReviewRef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1788:3: lv_decisionReviews_12_0= ruleDecisionReviewRef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getStateDefAccess().getDecisionReviewsDecisionReviewRefParserRuleCall_10_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleDecisionReviewRef_in_ruleStateDef3476);
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
            	    break loop56;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1804:3: ( (lv_peerReviews_13_0= rulePeerReviewRef ) )*
            loop57:
            do {
                int alt57=2;
                int LA57_0 = input.LA(1);

                if ( (LA57_0==62) ) {
                    alt57=1;
                }


                switch (alt57) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1805:1: (lv_peerReviews_13_0= rulePeerReviewRef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1805:1: (lv_peerReviews_13_0= rulePeerReviewRef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1806:3: lv_peerReviews_13_0= rulePeerReviewRef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getStateDefAccess().getPeerReviewsPeerReviewRefParserRuleCall_11_0()); 
            	    	    
            	    pushFollow(FOLLOW_rulePeerReviewRef_in_ruleStateDef3498);
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
            	    break loop57;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1822:3: (otherlv_14= 'percentWeight' ( (lv_percentWeight_15_0= RULE_INT ) ) )?
            int alt58=2;
            int LA58_0 = input.LA(1);

            if ( (LA58_0==53) ) {
                alt58=1;
            }
            switch (alt58) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1822:5: otherlv_14= 'percentWeight' ( (lv_percentWeight_15_0= RULE_INT ) )
                    {
                    otherlv_14=(Token)match(input,53,FOLLOW_53_in_ruleStateDef3512); 

                        	newLeafNode(otherlv_14, grammarAccess.getStateDefAccess().getPercentWeightKeyword_12_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1826:1: ( (lv_percentWeight_15_0= RULE_INT ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1827:1: (lv_percentWeight_15_0= RULE_INT )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1827:1: (lv_percentWeight_15_0= RULE_INT )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1828:3: lv_percentWeight_15_0= RULE_INT
                    {
                    lv_percentWeight_15_0=(Token)match(input,RULE_INT,FOLLOW_RULE_INT_in_ruleStateDef3529); 

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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1844:4: ( (lv_layout_16_0= ruleLayoutType ) )?
            int alt59=2;
            int LA59_0 = input.LA(1);

            if ( ((LA59_0>=68 && LA59_0<=69)) ) {
                alt59=1;
            }
            switch (alt59) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1845:1: (lv_layout_16_0= ruleLayoutType )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1845:1: (lv_layout_16_0= ruleLayoutType )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1846:3: lv_layout_16_0= ruleLayoutType
                    {
                     
                    	        newCompositeNode(grammarAccess.getStateDefAccess().getLayoutLayoutTypeParserRuleCall_13_0()); 
                    	    
                    pushFollow(FOLLOW_ruleLayoutType_in_ruleStateDef3557);
                    lv_layout_16_0=ruleLayoutType();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getStateDefRule());
                    	        }
                           		set(
                           			current, 
                           			"layout",
                            		lv_layout_16_0, 
                            		"LayoutType");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }
                    break;

            }

            otherlv_17=(Token)match(input,20,FOLLOW_20_in_ruleStateDef3570); 

                	newLeafNode(otherlv_17, grammarAccess.getStateDefAccess().getRightCurlyBracketKeyword_14());
                

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1874:1: entryRuleDecisionReviewRef returns [EObject current=null] : iv_ruleDecisionReviewRef= ruleDecisionReviewRef EOF ;
    public final EObject entryRuleDecisionReviewRef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDecisionReviewRef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1875:2: (iv_ruleDecisionReviewRef= ruleDecisionReviewRef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1876:2: iv_ruleDecisionReviewRef= ruleDecisionReviewRef EOF
            {
             newCompositeNode(grammarAccess.getDecisionReviewRefRule()); 
            pushFollow(FOLLOW_ruleDecisionReviewRef_in_entryRuleDecisionReviewRef3606);
            iv_ruleDecisionReviewRef=ruleDecisionReviewRef();

            state._fsp--;

             current =iv_ruleDecisionReviewRef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleDecisionReviewRef3616); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1883:1: ruleDecisionReviewRef returns [EObject current=null] : (otherlv_0= 'decisionReview' ( ( ruleDECISION_REVIEW_NAME_REFERENCE ) ) ) ;
    public final EObject ruleDecisionReviewRef() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1886:28: ( (otherlv_0= 'decisionReview' ( ( ruleDECISION_REVIEW_NAME_REFERENCE ) ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1887:1: (otherlv_0= 'decisionReview' ( ( ruleDECISION_REVIEW_NAME_REFERENCE ) ) )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1887:1: (otherlv_0= 'decisionReview' ( ( ruleDECISION_REVIEW_NAME_REFERENCE ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1887:3: otherlv_0= 'decisionReview' ( ( ruleDECISION_REVIEW_NAME_REFERENCE ) )
            {
            otherlv_0=(Token)match(input,54,FOLLOW_54_in_ruleDecisionReviewRef3653); 

                	newLeafNode(otherlv_0, grammarAccess.getDecisionReviewRefAccess().getDecisionReviewKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1891:1: ( ( ruleDECISION_REVIEW_NAME_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1892:1: ( ruleDECISION_REVIEW_NAME_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1892:1: ( ruleDECISION_REVIEW_NAME_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1893:3: ruleDECISION_REVIEW_NAME_REFERENCE
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getDecisionReviewRefRule());
            	        }
                    
             
            	        newCompositeNode(grammarAccess.getDecisionReviewRefAccess().getDecisionReviewDecisionReviewDefCrossReference_1_0()); 
            	    
            pushFollow(FOLLOW_ruleDECISION_REVIEW_NAME_REFERENCE_in_ruleDecisionReviewRef3676);
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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1914:1: entryRuleDecisionReviewDef returns [EObject current=null] : iv_ruleDecisionReviewDef= ruleDecisionReviewDef EOF ;
    public final EObject entryRuleDecisionReviewDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDecisionReviewDef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1915:2: (iv_ruleDecisionReviewDef= ruleDecisionReviewDef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1916:2: iv_ruleDecisionReviewDef= ruleDecisionReviewDef EOF
            {
             newCompositeNode(grammarAccess.getDecisionReviewDefRule()); 
            pushFollow(FOLLOW_ruleDecisionReviewDef_in_entryRuleDecisionReviewDef3712);
            iv_ruleDecisionReviewDef=ruleDecisionReviewDef();

            state._fsp--;

             current =iv_ruleDecisionReviewDef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleDecisionReviewDef3722); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1923:1: ruleDecisionReviewDef returns [EObject current=null] : (otherlv_0= 'decisionReviewDefinition' ( (lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_9= 'blockingType' ( (lv_blockingType_10_0= ruleReviewBlockingType ) ) otherlv_11= 'onEvent' ( (lv_stateEvent_12_0= ruleWorkflowEventType ) ) (otherlv_13= 'assignee' ( (lv_assigneeRefs_14_0= ruleUserRef ) ) )* (otherlv_15= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_16_0= ruleBooleanDef ) ) )? ( (lv_options_17_0= ruleDecisionReviewOpt ) )+ otherlv_18= '}' ) ;
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
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1926:28: ( (otherlv_0= 'decisionReviewDefinition' ( (lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_9= 'blockingType' ( (lv_blockingType_10_0= ruleReviewBlockingType ) ) otherlv_11= 'onEvent' ( (lv_stateEvent_12_0= ruleWorkflowEventType ) ) (otherlv_13= 'assignee' ( (lv_assigneeRefs_14_0= ruleUserRef ) ) )* (otherlv_15= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_16_0= ruleBooleanDef ) ) )? ( (lv_options_17_0= ruleDecisionReviewOpt ) )+ otherlv_18= '}' ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1927:1: (otherlv_0= 'decisionReviewDefinition' ( (lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_9= 'blockingType' ( (lv_blockingType_10_0= ruleReviewBlockingType ) ) otherlv_11= 'onEvent' ( (lv_stateEvent_12_0= ruleWorkflowEventType ) ) (otherlv_13= 'assignee' ( (lv_assigneeRefs_14_0= ruleUserRef ) ) )* (otherlv_15= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_16_0= ruleBooleanDef ) ) )? ( (lv_options_17_0= ruleDecisionReviewOpt ) )+ otherlv_18= '}' )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1927:1: (otherlv_0= 'decisionReviewDefinition' ( (lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_9= 'blockingType' ( (lv_blockingType_10_0= ruleReviewBlockingType ) ) otherlv_11= 'onEvent' ( (lv_stateEvent_12_0= ruleWorkflowEventType ) ) (otherlv_13= 'assignee' ( (lv_assigneeRefs_14_0= ruleUserRef ) ) )* (otherlv_15= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_16_0= ruleBooleanDef ) ) )? ( (lv_options_17_0= ruleDecisionReviewOpt ) )+ otherlv_18= '}' )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1927:3: otherlv_0= 'decisionReviewDefinition' ( (lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_9= 'blockingType' ( (lv_blockingType_10_0= ruleReviewBlockingType ) ) otherlv_11= 'onEvent' ( (lv_stateEvent_12_0= ruleWorkflowEventType ) ) (otherlv_13= 'assignee' ( (lv_assigneeRefs_14_0= ruleUserRef ) ) )* (otherlv_15= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_16_0= ruleBooleanDef ) ) )? ( (lv_options_17_0= ruleDecisionReviewOpt ) )+ otherlv_18= '}'
            {
            otherlv_0=(Token)match(input,55,FOLLOW_55_in_ruleDecisionReviewDef3759); 

                	newLeafNode(otherlv_0, grammarAccess.getDecisionReviewDefAccess().getDecisionReviewDefinitionKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1931:1: ( (lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1932:1: (lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1932:1: (lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1933:3: lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE
            {
             
            	        newCompositeNode(grammarAccess.getDecisionReviewDefAccess().getNameDECISION_REVIEW_NAME_REFERENCEParserRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_ruleDECISION_REVIEW_NAME_REFERENCE_in_ruleDecisionReviewDef3780);
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

            otherlv_2=(Token)match(input,15,FOLLOW_15_in_ruleDecisionReviewDef3792); 

                	newLeafNode(otherlv_2, grammarAccess.getDecisionReviewDefAccess().getLeftCurlyBracketKeyword_2());
                
            otherlv_3=(Token)match(input,56,FOLLOW_56_in_ruleDecisionReviewDef3804); 

                	newLeafNode(otherlv_3, grammarAccess.getDecisionReviewDefAccess().getTitleKeyword_3());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1957:1: ( (lv_title_4_0= RULE_STRING ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1958:1: (lv_title_4_0= RULE_STRING )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1958:1: (lv_title_4_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1959:3: lv_title_4_0= RULE_STRING
            {
            lv_title_4_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleDecisionReviewDef3821); 

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

            otherlv_5=(Token)match(input,41,FOLLOW_41_in_ruleDecisionReviewDef3838); 

                	newLeafNode(otherlv_5, grammarAccess.getDecisionReviewDefAccess().getDescriptionKeyword_5());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1979:1: ( (lv_description_6_0= RULE_STRING ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1980:1: (lv_description_6_0= RULE_STRING )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1980:1: (lv_description_6_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1981:3: lv_description_6_0= RULE_STRING
            {
            lv_description_6_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleDecisionReviewDef3855); 

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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1997:2: (otherlv_7= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )?
            int alt60=2;
            int LA60_0 = input.LA(1);

            if ( (LA60_0==57) ) {
                alt60=1;
            }
            switch (alt60) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1997:4: otherlv_7= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) )
                    {
                    otherlv_7=(Token)match(input,57,FOLLOW_57_in_ruleDecisionReviewDef3873); 

                        	newLeafNode(otherlv_7, grammarAccess.getDecisionReviewDefAccess().getRelatedToStateKeyword_7_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2001:1: ( ( ruleSTATE_NAME_REFERENCE ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2002:1: ( ruleSTATE_NAME_REFERENCE )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2002:1: ( ruleSTATE_NAME_REFERENCE )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2003:3: ruleSTATE_NAME_REFERENCE
                    {

                    			if (current==null) {
                    	            current = createModelElement(grammarAccess.getDecisionReviewDefRule());
                    	        }
                            
                     
                    	        newCompositeNode(grammarAccess.getDecisionReviewDefAccess().getRelatedToStateStateDefCrossReference_7_1_0()); 
                    	    
                    pushFollow(FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleDecisionReviewDef3896);
                    ruleSTATE_NAME_REFERENCE();

                    state._fsp--;

                     
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }


                    }
                    break;

            }

            otherlv_9=(Token)match(input,58,FOLLOW_58_in_ruleDecisionReviewDef3910); 

                	newLeafNode(otherlv_9, grammarAccess.getDecisionReviewDefAccess().getBlockingTypeKeyword_8());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2020:1: ( (lv_blockingType_10_0= ruleReviewBlockingType ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2021:1: (lv_blockingType_10_0= ruleReviewBlockingType )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2021:1: (lv_blockingType_10_0= ruleReviewBlockingType )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2022:3: lv_blockingType_10_0= ruleReviewBlockingType
            {
             
            	        newCompositeNode(grammarAccess.getDecisionReviewDefAccess().getBlockingTypeReviewBlockingTypeEnumRuleCall_9_0()); 
            	    
            pushFollow(FOLLOW_ruleReviewBlockingType_in_ruleDecisionReviewDef3931);
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

            otherlv_11=(Token)match(input,59,FOLLOW_59_in_ruleDecisionReviewDef3943); 

                	newLeafNode(otherlv_11, grammarAccess.getDecisionReviewDefAccess().getOnEventKeyword_10());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2042:1: ( (lv_stateEvent_12_0= ruleWorkflowEventType ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2043:1: (lv_stateEvent_12_0= ruleWorkflowEventType )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2043:1: (lv_stateEvent_12_0= ruleWorkflowEventType )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2044:3: lv_stateEvent_12_0= ruleWorkflowEventType
            {
             
            	        newCompositeNode(grammarAccess.getDecisionReviewDefAccess().getStateEventWorkflowEventTypeEnumRuleCall_11_0()); 
            	    
            pushFollow(FOLLOW_ruleWorkflowEventType_in_ruleDecisionReviewDef3964);
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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2060:2: (otherlv_13= 'assignee' ( (lv_assigneeRefs_14_0= ruleUserRef ) ) )*
            loop61:
            do {
                int alt61=2;
                int LA61_0 = input.LA(1);

                if ( (LA61_0==60) ) {
                    alt61=1;
                }


                switch (alt61) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2060:4: otherlv_13= 'assignee' ( (lv_assigneeRefs_14_0= ruleUserRef ) )
            	    {
            	    otherlv_13=(Token)match(input,60,FOLLOW_60_in_ruleDecisionReviewDef3977); 

            	        	newLeafNode(otherlv_13, grammarAccess.getDecisionReviewDefAccess().getAssigneeKeyword_12_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2064:1: ( (lv_assigneeRefs_14_0= ruleUserRef ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2065:1: (lv_assigneeRefs_14_0= ruleUserRef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2065:1: (lv_assigneeRefs_14_0= ruleUserRef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2066:3: lv_assigneeRefs_14_0= ruleUserRef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getDecisionReviewDefAccess().getAssigneeRefsUserRefParserRuleCall_12_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleUserRef_in_ruleDecisionReviewDef3998);
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
            	    break loop61;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2082:4: (otherlv_15= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_16_0= ruleBooleanDef ) ) )?
            int alt62=2;
            int LA62_0 = input.LA(1);

            if ( (LA62_0==61) ) {
                alt62=1;
            }
            switch (alt62) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2082:6: otherlv_15= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_16_0= ruleBooleanDef ) )
                    {
                    otherlv_15=(Token)match(input,61,FOLLOW_61_in_ruleDecisionReviewDef4013); 

                        	newLeafNode(otherlv_15, grammarAccess.getDecisionReviewDefAccess().getAutoTransitionToDecisionKeyword_13_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2086:1: ( (lv_autoTransitionToDecision_16_0= ruleBooleanDef ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2087:1: (lv_autoTransitionToDecision_16_0= ruleBooleanDef )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2087:1: (lv_autoTransitionToDecision_16_0= ruleBooleanDef )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2088:3: lv_autoTransitionToDecision_16_0= ruleBooleanDef
                    {
                     
                    	        newCompositeNode(grammarAccess.getDecisionReviewDefAccess().getAutoTransitionToDecisionBooleanDefEnumRuleCall_13_1_0()); 
                    	    
                    pushFollow(FOLLOW_ruleBooleanDef_in_ruleDecisionReviewDef4034);
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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2104:4: ( (lv_options_17_0= ruleDecisionReviewOpt ) )+
            int cnt63=0;
            loop63:
            do {
                int alt63=2;
                int LA63_0 = input.LA(1);

                if ( (LA63_0==45) ) {
                    alt63=1;
                }


                switch (alt63) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2105:1: (lv_options_17_0= ruleDecisionReviewOpt )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2105:1: (lv_options_17_0= ruleDecisionReviewOpt )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2106:3: lv_options_17_0= ruleDecisionReviewOpt
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getDecisionReviewDefAccess().getOptionsDecisionReviewOptParserRuleCall_14_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleDecisionReviewOpt_in_ruleDecisionReviewDef4057);
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
            	    if ( cnt63 >= 1 ) break loop63;
                        EarlyExitException eee =
                            new EarlyExitException(63, input);
                        throw eee;
                }
                cnt63++;
            } while (true);

            otherlv_18=(Token)match(input,20,FOLLOW_20_in_ruleDecisionReviewDef4070); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2134:1: entryRuleDECISION_REVIEW_OPT_REF returns [String current=null] : iv_ruleDECISION_REVIEW_OPT_REF= ruleDECISION_REVIEW_OPT_REF EOF ;
    public final String entryRuleDECISION_REVIEW_OPT_REF() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleDECISION_REVIEW_OPT_REF = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2135:2: (iv_ruleDECISION_REVIEW_OPT_REF= ruleDECISION_REVIEW_OPT_REF EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2136:2: iv_ruleDECISION_REVIEW_OPT_REF= ruleDECISION_REVIEW_OPT_REF EOF
            {
             newCompositeNode(grammarAccess.getDECISION_REVIEW_OPT_REFRule()); 
            pushFollow(FOLLOW_ruleDECISION_REVIEW_OPT_REF_in_entryRuleDECISION_REVIEW_OPT_REF4107);
            iv_ruleDECISION_REVIEW_OPT_REF=ruleDECISION_REVIEW_OPT_REF();

            state._fsp--;

             current =iv_ruleDECISION_REVIEW_OPT_REF.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleDECISION_REVIEW_OPT_REF4118); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2143:1: ruleDECISION_REVIEW_OPT_REF returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleDECISION_REVIEW_OPT_REF() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2146:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2147:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleDECISION_REVIEW_OPT_REF4157); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2162:1: entryRuleDecisionReviewOpt returns [EObject current=null] : iv_ruleDecisionReviewOpt= ruleDecisionReviewOpt EOF ;
    public final EObject entryRuleDecisionReviewOpt() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDecisionReviewOpt = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2163:2: (iv_ruleDecisionReviewOpt= ruleDecisionReviewOpt EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2164:2: iv_ruleDecisionReviewOpt= ruleDecisionReviewOpt EOF
            {
             newCompositeNode(grammarAccess.getDecisionReviewOptRule()); 
            pushFollow(FOLLOW_ruleDecisionReviewOpt_in_entryRuleDecisionReviewOpt4201);
            iv_ruleDecisionReviewOpt=ruleDecisionReviewOpt();

            state._fsp--;

             current =iv_ruleDecisionReviewOpt; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleDecisionReviewOpt4211); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2171:1: ruleDecisionReviewOpt returns [EObject current=null] : (otherlv_0= 'option' ( (lv_name_1_0= ruleDECISION_REVIEW_OPT_REF ) ) ( (lv_followup_2_0= ruleFollowupRef ) )? ) ;
    public final EObject ruleDecisionReviewOpt() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        AntlrDatatypeRuleToken lv_name_1_0 = null;

        EObject lv_followup_2_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2174:28: ( (otherlv_0= 'option' ( (lv_name_1_0= ruleDECISION_REVIEW_OPT_REF ) ) ( (lv_followup_2_0= ruleFollowupRef ) )? ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2175:1: (otherlv_0= 'option' ( (lv_name_1_0= ruleDECISION_REVIEW_OPT_REF ) ) ( (lv_followup_2_0= ruleFollowupRef ) )? )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2175:1: (otherlv_0= 'option' ( (lv_name_1_0= ruleDECISION_REVIEW_OPT_REF ) ) ( (lv_followup_2_0= ruleFollowupRef ) )? )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2175:3: otherlv_0= 'option' ( (lv_name_1_0= ruleDECISION_REVIEW_OPT_REF ) ) ( (lv_followup_2_0= ruleFollowupRef ) )?
            {
            otherlv_0=(Token)match(input,45,FOLLOW_45_in_ruleDecisionReviewOpt4248); 

                	newLeafNode(otherlv_0, grammarAccess.getDecisionReviewOptAccess().getOptionKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2179:1: ( (lv_name_1_0= ruleDECISION_REVIEW_OPT_REF ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2180:1: (lv_name_1_0= ruleDECISION_REVIEW_OPT_REF )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2180:1: (lv_name_1_0= ruleDECISION_REVIEW_OPT_REF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2181:3: lv_name_1_0= ruleDECISION_REVIEW_OPT_REF
            {
             
            	        newCompositeNode(grammarAccess.getDecisionReviewOptAccess().getNameDECISION_REVIEW_OPT_REFParserRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_ruleDECISION_REVIEW_OPT_REF_in_ruleDecisionReviewOpt4269);
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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2197:2: ( (lv_followup_2_0= ruleFollowupRef ) )?
            int alt64=2;
            int LA64_0 = input.LA(1);

            if ( (LA64_0==65) ) {
                alt64=1;
            }
            switch (alt64) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2198:1: (lv_followup_2_0= ruleFollowupRef )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2198:1: (lv_followup_2_0= ruleFollowupRef )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2199:3: lv_followup_2_0= ruleFollowupRef
                    {
                     
                    	        newCompositeNode(grammarAccess.getDecisionReviewOptAccess().getFollowupFollowupRefParserRuleCall_2_0()); 
                    	    
                    pushFollow(FOLLOW_ruleFollowupRef_in_ruleDecisionReviewOpt4290);
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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2223:1: entryRulePeerReviewRef returns [EObject current=null] : iv_rulePeerReviewRef= rulePeerReviewRef EOF ;
    public final EObject entryRulePeerReviewRef() throws RecognitionException {
        EObject current = null;

        EObject iv_rulePeerReviewRef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2224:2: (iv_rulePeerReviewRef= rulePeerReviewRef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2225:2: iv_rulePeerReviewRef= rulePeerReviewRef EOF
            {
             newCompositeNode(grammarAccess.getPeerReviewRefRule()); 
            pushFollow(FOLLOW_rulePeerReviewRef_in_entryRulePeerReviewRef4327);
            iv_rulePeerReviewRef=rulePeerReviewRef();

            state._fsp--;

             current =iv_rulePeerReviewRef; 
            match(input,EOF,FOLLOW_EOF_in_entryRulePeerReviewRef4337); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2232:1: rulePeerReviewRef returns [EObject current=null] : (otherlv_0= 'peerReview' ( ( rulePEER_REVIEW_NAME_REFERENCE ) ) ) ;
    public final EObject rulePeerReviewRef() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2235:28: ( (otherlv_0= 'peerReview' ( ( rulePEER_REVIEW_NAME_REFERENCE ) ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2236:1: (otherlv_0= 'peerReview' ( ( rulePEER_REVIEW_NAME_REFERENCE ) ) )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2236:1: (otherlv_0= 'peerReview' ( ( rulePEER_REVIEW_NAME_REFERENCE ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2236:3: otherlv_0= 'peerReview' ( ( rulePEER_REVIEW_NAME_REFERENCE ) )
            {
            otherlv_0=(Token)match(input,62,FOLLOW_62_in_rulePeerReviewRef4374); 

                	newLeafNode(otherlv_0, grammarAccess.getPeerReviewRefAccess().getPeerReviewKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2240:1: ( ( rulePEER_REVIEW_NAME_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2241:1: ( rulePEER_REVIEW_NAME_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2241:1: ( rulePEER_REVIEW_NAME_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2242:3: rulePEER_REVIEW_NAME_REFERENCE
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getPeerReviewRefRule());
            	        }
                    
             
            	        newCompositeNode(grammarAccess.getPeerReviewRefAccess().getPeerReviewPeerReviewDefCrossReference_1_0()); 
            	    
            pushFollow(FOLLOW_rulePEER_REVIEW_NAME_REFERENCE_in_rulePeerReviewRef4397);
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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2263:1: entryRulePeerReviewDef returns [EObject current=null] : iv_rulePeerReviewDef= rulePeerReviewDef EOF ;
    public final EObject entryRulePeerReviewDef() throws RecognitionException {
        EObject current = null;

        EObject iv_rulePeerReviewDef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2264:2: (iv_rulePeerReviewDef= rulePeerReviewDef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2265:2: iv_rulePeerReviewDef= rulePeerReviewDef EOF
            {
             newCompositeNode(grammarAccess.getPeerReviewDefRule()); 
            pushFollow(FOLLOW_rulePeerReviewDef_in_entryRulePeerReviewDef4433);
            iv_rulePeerReviewDef=rulePeerReviewDef();

            state._fsp--;

             current =iv_rulePeerReviewDef; 
            match(input,EOF,FOLLOW_EOF_in_entryRulePeerReviewDef4443); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2272:1: rulePeerReviewDef returns [EObject current=null] : (otherlv_0= 'peerReviewDefinition' ( (lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) )? otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'location' ( (lv_location_8_0= RULE_STRING ) ) )? (otherlv_9= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_11= 'blockingType' ( (lv_blockingType_12_0= ruleReviewBlockingType ) ) otherlv_13= 'onEvent' ( (lv_stateEvent_14_0= ruleWorkflowEventType ) ) (otherlv_15= 'assignee' ( (lv_assigneeRefs_16_0= ruleUserRef ) ) )* otherlv_17= '}' ) ;
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
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2275:28: ( (otherlv_0= 'peerReviewDefinition' ( (lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) )? otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'location' ( (lv_location_8_0= RULE_STRING ) ) )? (otherlv_9= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_11= 'blockingType' ( (lv_blockingType_12_0= ruleReviewBlockingType ) ) otherlv_13= 'onEvent' ( (lv_stateEvent_14_0= ruleWorkflowEventType ) ) (otherlv_15= 'assignee' ( (lv_assigneeRefs_16_0= ruleUserRef ) ) )* otherlv_17= '}' ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2276:1: (otherlv_0= 'peerReviewDefinition' ( (lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) )? otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'location' ( (lv_location_8_0= RULE_STRING ) ) )? (otherlv_9= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_11= 'blockingType' ( (lv_blockingType_12_0= ruleReviewBlockingType ) ) otherlv_13= 'onEvent' ( (lv_stateEvent_14_0= ruleWorkflowEventType ) ) (otherlv_15= 'assignee' ( (lv_assigneeRefs_16_0= ruleUserRef ) ) )* otherlv_17= '}' )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2276:1: (otherlv_0= 'peerReviewDefinition' ( (lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) )? otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'location' ( (lv_location_8_0= RULE_STRING ) ) )? (otherlv_9= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_11= 'blockingType' ( (lv_blockingType_12_0= ruleReviewBlockingType ) ) otherlv_13= 'onEvent' ( (lv_stateEvent_14_0= ruleWorkflowEventType ) ) (otherlv_15= 'assignee' ( (lv_assigneeRefs_16_0= ruleUserRef ) ) )* otherlv_17= '}' )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2276:3: otherlv_0= 'peerReviewDefinition' ( (lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) )? otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'location' ( (lv_location_8_0= RULE_STRING ) ) )? (otherlv_9= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_11= 'blockingType' ( (lv_blockingType_12_0= ruleReviewBlockingType ) ) otherlv_13= 'onEvent' ( (lv_stateEvent_14_0= ruleWorkflowEventType ) ) (otherlv_15= 'assignee' ( (lv_assigneeRefs_16_0= ruleUserRef ) ) )* otherlv_17= '}'
            {
            otherlv_0=(Token)match(input,63,FOLLOW_63_in_rulePeerReviewDef4480); 

                	newLeafNode(otherlv_0, grammarAccess.getPeerReviewDefAccess().getPeerReviewDefinitionKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2280:1: ( (lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2281:1: (lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2281:1: (lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2282:3: lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE
            {
             
            	        newCompositeNode(grammarAccess.getPeerReviewDefAccess().getNamePEER_REVIEW_NAME_REFERENCEParserRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_rulePEER_REVIEW_NAME_REFERENCE_in_rulePeerReviewDef4501);
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

            otherlv_2=(Token)match(input,15,FOLLOW_15_in_rulePeerReviewDef4513); 

                	newLeafNode(otherlv_2, grammarAccess.getPeerReviewDefAccess().getLeftCurlyBracketKeyword_2());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2302:1: (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) )?
            int alt65=2;
            int LA65_0 = input.LA(1);

            if ( (LA65_0==56) ) {
                alt65=1;
            }
            switch (alt65) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2302:3: otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) )
                    {
                    otherlv_3=(Token)match(input,56,FOLLOW_56_in_rulePeerReviewDef4526); 

                        	newLeafNode(otherlv_3, grammarAccess.getPeerReviewDefAccess().getTitleKeyword_3_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2306:1: ( (lv_title_4_0= RULE_STRING ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2307:1: (lv_title_4_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2307:1: (lv_title_4_0= RULE_STRING )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2308:3: lv_title_4_0= RULE_STRING
                    {
                    lv_title_4_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rulePeerReviewDef4543); 

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

            otherlv_5=(Token)match(input,41,FOLLOW_41_in_rulePeerReviewDef4562); 

                	newLeafNode(otherlv_5, grammarAccess.getPeerReviewDefAccess().getDescriptionKeyword_4());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2328:1: ( (lv_description_6_0= RULE_STRING ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2329:1: (lv_description_6_0= RULE_STRING )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2329:1: (lv_description_6_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2330:3: lv_description_6_0= RULE_STRING
            {
            lv_description_6_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rulePeerReviewDef4579); 

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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2346:2: (otherlv_7= 'location' ( (lv_location_8_0= RULE_STRING ) ) )?
            int alt66=2;
            int LA66_0 = input.LA(1);

            if ( (LA66_0==64) ) {
                alt66=1;
            }
            switch (alt66) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2346:4: otherlv_7= 'location' ( (lv_location_8_0= RULE_STRING ) )
                    {
                    otherlv_7=(Token)match(input,64,FOLLOW_64_in_rulePeerReviewDef4597); 

                        	newLeafNode(otherlv_7, grammarAccess.getPeerReviewDefAccess().getLocationKeyword_6_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2350:1: ( (lv_location_8_0= RULE_STRING ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2351:1: (lv_location_8_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2351:1: (lv_location_8_0= RULE_STRING )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2352:3: lv_location_8_0= RULE_STRING
                    {
                    lv_location_8_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rulePeerReviewDef4614); 

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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2368:4: (otherlv_9= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )?
            int alt67=2;
            int LA67_0 = input.LA(1);

            if ( (LA67_0==57) ) {
                alt67=1;
            }
            switch (alt67) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2368:6: otherlv_9= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) )
                    {
                    otherlv_9=(Token)match(input,57,FOLLOW_57_in_rulePeerReviewDef4634); 

                        	newLeafNode(otherlv_9, grammarAccess.getPeerReviewDefAccess().getRelatedToStateKeyword_7_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2372:1: ( ( ruleSTATE_NAME_REFERENCE ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2373:1: ( ruleSTATE_NAME_REFERENCE )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2373:1: ( ruleSTATE_NAME_REFERENCE )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2374:3: ruleSTATE_NAME_REFERENCE
                    {

                    			if (current==null) {
                    	            current = createModelElement(grammarAccess.getPeerReviewDefRule());
                    	        }
                            
                     
                    	        newCompositeNode(grammarAccess.getPeerReviewDefAccess().getRelatedToStateStateDefCrossReference_7_1_0()); 
                    	    
                    pushFollow(FOLLOW_ruleSTATE_NAME_REFERENCE_in_rulePeerReviewDef4657);
                    ruleSTATE_NAME_REFERENCE();

                    state._fsp--;

                     
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }


                    }
                    break;

            }

            otherlv_11=(Token)match(input,58,FOLLOW_58_in_rulePeerReviewDef4671); 

                	newLeafNode(otherlv_11, grammarAccess.getPeerReviewDefAccess().getBlockingTypeKeyword_8());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2391:1: ( (lv_blockingType_12_0= ruleReviewBlockingType ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2392:1: (lv_blockingType_12_0= ruleReviewBlockingType )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2392:1: (lv_blockingType_12_0= ruleReviewBlockingType )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2393:3: lv_blockingType_12_0= ruleReviewBlockingType
            {
             
            	        newCompositeNode(grammarAccess.getPeerReviewDefAccess().getBlockingTypeReviewBlockingTypeEnumRuleCall_9_0()); 
            	    
            pushFollow(FOLLOW_ruleReviewBlockingType_in_rulePeerReviewDef4692);
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

            otherlv_13=(Token)match(input,59,FOLLOW_59_in_rulePeerReviewDef4704); 

                	newLeafNode(otherlv_13, grammarAccess.getPeerReviewDefAccess().getOnEventKeyword_10());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2413:1: ( (lv_stateEvent_14_0= ruleWorkflowEventType ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2414:1: (lv_stateEvent_14_0= ruleWorkflowEventType )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2414:1: (lv_stateEvent_14_0= ruleWorkflowEventType )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2415:3: lv_stateEvent_14_0= ruleWorkflowEventType
            {
             
            	        newCompositeNode(grammarAccess.getPeerReviewDefAccess().getStateEventWorkflowEventTypeEnumRuleCall_11_0()); 
            	    
            pushFollow(FOLLOW_ruleWorkflowEventType_in_rulePeerReviewDef4725);
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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2431:2: (otherlv_15= 'assignee' ( (lv_assigneeRefs_16_0= ruleUserRef ) ) )*
            loop68:
            do {
                int alt68=2;
                int LA68_0 = input.LA(1);

                if ( (LA68_0==60) ) {
                    alt68=1;
                }


                switch (alt68) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2431:4: otherlv_15= 'assignee' ( (lv_assigneeRefs_16_0= ruleUserRef ) )
            	    {
            	    otherlv_15=(Token)match(input,60,FOLLOW_60_in_rulePeerReviewDef4738); 

            	        	newLeafNode(otherlv_15, grammarAccess.getPeerReviewDefAccess().getAssigneeKeyword_12_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2435:1: ( (lv_assigneeRefs_16_0= ruleUserRef ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2436:1: (lv_assigneeRefs_16_0= ruleUserRef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2436:1: (lv_assigneeRefs_16_0= ruleUserRef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2437:3: lv_assigneeRefs_16_0= ruleUserRef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getPeerReviewDefAccess().getAssigneeRefsUserRefParserRuleCall_12_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleUserRef_in_rulePeerReviewDef4759);
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
            	    break loop68;
                }
            } while (true);

            otherlv_17=(Token)match(input,20,FOLLOW_20_in_rulePeerReviewDef4773); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2465:1: entryRuleFollowupRef returns [EObject current=null] : iv_ruleFollowupRef= ruleFollowupRef EOF ;
    public final EObject entryRuleFollowupRef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleFollowupRef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2466:2: (iv_ruleFollowupRef= ruleFollowupRef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2467:2: iv_ruleFollowupRef= ruleFollowupRef EOF
            {
             newCompositeNode(grammarAccess.getFollowupRefRule()); 
            pushFollow(FOLLOW_ruleFollowupRef_in_entryRuleFollowupRef4809);
            iv_ruleFollowupRef=ruleFollowupRef();

            state._fsp--;

             current =iv_ruleFollowupRef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleFollowupRef4819); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2474:1: ruleFollowupRef returns [EObject current=null] : (otherlv_0= 'followup by' (otherlv_1= 'assignee' ( (lv_assigneeRefs_2_0= ruleUserRef ) ) )+ ) ;
    public final EObject ruleFollowupRef() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;
        EObject lv_assigneeRefs_2_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2477:28: ( (otherlv_0= 'followup by' (otherlv_1= 'assignee' ( (lv_assigneeRefs_2_0= ruleUserRef ) ) )+ ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2478:1: (otherlv_0= 'followup by' (otherlv_1= 'assignee' ( (lv_assigneeRefs_2_0= ruleUserRef ) ) )+ )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2478:1: (otherlv_0= 'followup by' (otherlv_1= 'assignee' ( (lv_assigneeRefs_2_0= ruleUserRef ) ) )+ )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2478:3: otherlv_0= 'followup by' (otherlv_1= 'assignee' ( (lv_assigneeRefs_2_0= ruleUserRef ) ) )+
            {
            otherlv_0=(Token)match(input,65,FOLLOW_65_in_ruleFollowupRef4856); 

                	newLeafNode(otherlv_0, grammarAccess.getFollowupRefAccess().getFollowupByKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2482:1: (otherlv_1= 'assignee' ( (lv_assigneeRefs_2_0= ruleUserRef ) ) )+
            int cnt69=0;
            loop69:
            do {
                int alt69=2;
                int LA69_0 = input.LA(1);

                if ( (LA69_0==60) ) {
                    alt69=1;
                }


                switch (alt69) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2482:3: otherlv_1= 'assignee' ( (lv_assigneeRefs_2_0= ruleUserRef ) )
            	    {
            	    otherlv_1=(Token)match(input,60,FOLLOW_60_in_ruleFollowupRef4869); 

            	        	newLeafNode(otherlv_1, grammarAccess.getFollowupRefAccess().getAssigneeKeyword_1_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2486:1: ( (lv_assigneeRefs_2_0= ruleUserRef ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2487:1: (lv_assigneeRefs_2_0= ruleUserRef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2487:1: (lv_assigneeRefs_2_0= ruleUserRef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2488:3: lv_assigneeRefs_2_0= ruleUserRef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getFollowupRefAccess().getAssigneeRefsUserRefParserRuleCall_1_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleUserRef_in_ruleFollowupRef4890);
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
            	    if ( cnt69 >= 1 ) break loop69;
                        EarlyExitException eee =
                            new EarlyExitException(69, input);
                        throw eee;
                }
                cnt69++;
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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2512:1: entryRuleUserRef returns [EObject current=null] : iv_ruleUserRef= ruleUserRef EOF ;
    public final EObject entryRuleUserRef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleUserRef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2513:2: (iv_ruleUserRef= ruleUserRef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2514:2: iv_ruleUserRef= ruleUserRef EOF
            {
             newCompositeNode(grammarAccess.getUserRefRule()); 
            pushFollow(FOLLOW_ruleUserRef_in_entryRuleUserRef4928);
            iv_ruleUserRef=ruleUserRef();

            state._fsp--;

             current =iv_ruleUserRef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleUserRef4938); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2521:1: ruleUserRef returns [EObject current=null] : (this_UserByUserId_0= ruleUserByUserId | this_UserByName_1= ruleUserByName ) ;
    public final EObject ruleUserRef() throws RecognitionException {
        EObject current = null;

        EObject this_UserByUserId_0 = null;

        EObject this_UserByName_1 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2524:28: ( (this_UserByUserId_0= ruleUserByUserId | this_UserByName_1= ruleUserByName ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2525:1: (this_UserByUserId_0= ruleUserByUserId | this_UserByName_1= ruleUserByName )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2525:1: (this_UserByUserId_0= ruleUserByUserId | this_UserByName_1= ruleUserByName )
            int alt70=2;
            int LA70_0 = input.LA(1);

            if ( (LA70_0==17) ) {
                alt70=1;
            }
            else if ( (LA70_0==66) ) {
                alt70=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 70, 0, input);

                throw nvae;
            }
            switch (alt70) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2526:5: this_UserByUserId_0= ruleUserByUserId
                    {
                     
                            newCompositeNode(grammarAccess.getUserRefAccess().getUserByUserIdParserRuleCall_0()); 
                        
                    pushFollow(FOLLOW_ruleUserByUserId_in_ruleUserRef4985);
                    this_UserByUserId_0=ruleUserByUserId();

                    state._fsp--;

                     
                            current = this_UserByUserId_0; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2536:5: this_UserByName_1= ruleUserByName
                    {
                     
                            newCompositeNode(grammarAccess.getUserRefAccess().getUserByNameParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_ruleUserByName_in_ruleUserRef5012);
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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2552:1: entryRuleUserByUserId returns [EObject current=null] : iv_ruleUserByUserId= ruleUserByUserId EOF ;
    public final EObject entryRuleUserByUserId() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleUserByUserId = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2553:2: (iv_ruleUserByUserId= ruleUserByUserId EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2554:2: iv_ruleUserByUserId= ruleUserByUserId EOF
            {
             newCompositeNode(grammarAccess.getUserByUserIdRule()); 
            pushFollow(FOLLOW_ruleUserByUserId_in_entryRuleUserByUserId5047);
            iv_ruleUserByUserId=ruleUserByUserId();

            state._fsp--;

             current =iv_ruleUserByUserId; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleUserByUserId5057); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2561:1: ruleUserByUserId returns [EObject current=null] : (otherlv_0= 'userId' ( (lv_userId_1_0= RULE_STRING ) ) ) ;
    public final EObject ruleUserByUserId() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_userId_1_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2564:28: ( (otherlv_0= 'userId' ( (lv_userId_1_0= RULE_STRING ) ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2565:1: (otherlv_0= 'userId' ( (lv_userId_1_0= RULE_STRING ) ) )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2565:1: (otherlv_0= 'userId' ( (lv_userId_1_0= RULE_STRING ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2565:3: otherlv_0= 'userId' ( (lv_userId_1_0= RULE_STRING ) )
            {
            otherlv_0=(Token)match(input,17,FOLLOW_17_in_ruleUserByUserId5094); 

                	newLeafNode(otherlv_0, grammarAccess.getUserByUserIdAccess().getUserIdKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2569:1: ( (lv_userId_1_0= RULE_STRING ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2570:1: (lv_userId_1_0= RULE_STRING )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2570:1: (lv_userId_1_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2571:3: lv_userId_1_0= RULE_STRING
            {
            lv_userId_1_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleUserByUserId5111); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2595:1: entryRuleUserByName returns [EObject current=null] : iv_ruleUserByName= ruleUserByName EOF ;
    public final EObject entryRuleUserByName() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleUserByName = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2596:2: (iv_ruleUserByName= ruleUserByName EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2597:2: iv_ruleUserByName= ruleUserByName EOF
            {
             newCompositeNode(grammarAccess.getUserByNameRule()); 
            pushFollow(FOLLOW_ruleUserByName_in_entryRuleUserByName5152);
            iv_ruleUserByName=ruleUserByName();

            state._fsp--;

             current =iv_ruleUserByName; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleUserByName5162); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2604:1: ruleUserByName returns [EObject current=null] : (otherlv_0= 'named' ( (lv_userName_1_0= RULE_STRING ) ) ) ;
    public final EObject ruleUserByName() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_userName_1_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2607:28: ( (otherlv_0= 'named' ( (lv_userName_1_0= RULE_STRING ) ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2608:1: (otherlv_0= 'named' ( (lv_userName_1_0= RULE_STRING ) ) )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2608:1: (otherlv_0= 'named' ( (lv_userName_1_0= RULE_STRING ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2608:3: otherlv_0= 'named' ( (lv_userName_1_0= RULE_STRING ) )
            {
            otherlv_0=(Token)match(input,66,FOLLOW_66_in_ruleUserByName5199); 

                	newLeafNode(otherlv_0, grammarAccess.getUserByNameAccess().getNamedKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2612:1: ( (lv_userName_1_0= RULE_STRING ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2613:1: (lv_userName_1_0= RULE_STRING )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2613:1: (lv_userName_1_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2614:3: lv_userName_1_0= RULE_STRING
            {
            lv_userName_1_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleUserByName5216); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2638:1: entryRuleDECISION_REVIEW_NAME_REFERENCE returns [String current=null] : iv_ruleDECISION_REVIEW_NAME_REFERENCE= ruleDECISION_REVIEW_NAME_REFERENCE EOF ;
    public final String entryRuleDECISION_REVIEW_NAME_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleDECISION_REVIEW_NAME_REFERENCE = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2639:2: (iv_ruleDECISION_REVIEW_NAME_REFERENCE= ruleDECISION_REVIEW_NAME_REFERENCE EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2640:2: iv_ruleDECISION_REVIEW_NAME_REFERENCE= ruleDECISION_REVIEW_NAME_REFERENCE EOF
            {
             newCompositeNode(grammarAccess.getDECISION_REVIEW_NAME_REFERENCERule()); 
            pushFollow(FOLLOW_ruleDECISION_REVIEW_NAME_REFERENCE_in_entryRuleDECISION_REVIEW_NAME_REFERENCE5258);
            iv_ruleDECISION_REVIEW_NAME_REFERENCE=ruleDECISION_REVIEW_NAME_REFERENCE();

            state._fsp--;

             current =iv_ruleDECISION_REVIEW_NAME_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleDECISION_REVIEW_NAME_REFERENCE5269); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2647:1: ruleDECISION_REVIEW_NAME_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleDECISION_REVIEW_NAME_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2650:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2651:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleDECISION_REVIEW_NAME_REFERENCE5308); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2666:1: entryRulePEER_REVIEW_NAME_REFERENCE returns [String current=null] : iv_rulePEER_REVIEW_NAME_REFERENCE= rulePEER_REVIEW_NAME_REFERENCE EOF ;
    public final String entryRulePEER_REVIEW_NAME_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_rulePEER_REVIEW_NAME_REFERENCE = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2667:2: (iv_rulePEER_REVIEW_NAME_REFERENCE= rulePEER_REVIEW_NAME_REFERENCE EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2668:2: iv_rulePEER_REVIEW_NAME_REFERENCE= rulePEER_REVIEW_NAME_REFERENCE EOF
            {
             newCompositeNode(grammarAccess.getPEER_REVIEW_NAME_REFERENCERule()); 
            pushFollow(FOLLOW_rulePEER_REVIEW_NAME_REFERENCE_in_entryRulePEER_REVIEW_NAME_REFERENCE5353);
            iv_rulePEER_REVIEW_NAME_REFERENCE=rulePEER_REVIEW_NAME_REFERENCE();

            state._fsp--;

             current =iv_rulePEER_REVIEW_NAME_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRulePEER_REVIEW_NAME_REFERENCE5364); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2675:1: rulePEER_REVIEW_NAME_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken rulePEER_REVIEW_NAME_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2678:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2679:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rulePEER_REVIEW_NAME_REFERENCE5403); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2694:1: entryRuleSTATE_NAME_REFERENCE returns [String current=null] : iv_ruleSTATE_NAME_REFERENCE= ruleSTATE_NAME_REFERENCE EOF ;
    public final String entryRuleSTATE_NAME_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleSTATE_NAME_REFERENCE = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2695:2: (iv_ruleSTATE_NAME_REFERENCE= ruleSTATE_NAME_REFERENCE EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2696:2: iv_ruleSTATE_NAME_REFERENCE= ruleSTATE_NAME_REFERENCE EOF
            {
             newCompositeNode(grammarAccess.getSTATE_NAME_REFERENCERule()); 
            pushFollow(FOLLOW_ruleSTATE_NAME_REFERENCE_in_entryRuleSTATE_NAME_REFERENCE5448);
            iv_ruleSTATE_NAME_REFERENCE=ruleSTATE_NAME_REFERENCE();

            state._fsp--;

             current =iv_ruleSTATE_NAME_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleSTATE_NAME_REFERENCE5459); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2703:1: ruleSTATE_NAME_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleSTATE_NAME_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2706:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2707:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleSTATE_NAME_REFERENCE5498); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2722:1: entryRuleWIDGET_NAME_REFERENCE returns [String current=null] : iv_ruleWIDGET_NAME_REFERENCE= ruleWIDGET_NAME_REFERENCE EOF ;
    public final String entryRuleWIDGET_NAME_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleWIDGET_NAME_REFERENCE = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2723:2: (iv_ruleWIDGET_NAME_REFERENCE= ruleWIDGET_NAME_REFERENCE EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2724:2: iv_ruleWIDGET_NAME_REFERENCE= ruleWIDGET_NAME_REFERENCE EOF
            {
             newCompositeNode(grammarAccess.getWIDGET_NAME_REFERENCERule()); 
            pushFollow(FOLLOW_ruleWIDGET_NAME_REFERENCE_in_entryRuleWIDGET_NAME_REFERENCE5543);
            iv_ruleWIDGET_NAME_REFERENCE=ruleWIDGET_NAME_REFERENCE();

            state._fsp--;

             current =iv_ruleWIDGET_NAME_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleWIDGET_NAME_REFERENCE5554); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2731:1: ruleWIDGET_NAME_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleWIDGET_NAME_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2734:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2735:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleWIDGET_NAME_REFERENCE5593); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2750:1: entryRuleWORK_DEFINITION_NAME_REFERENCE returns [String current=null] : iv_ruleWORK_DEFINITION_NAME_REFERENCE= ruleWORK_DEFINITION_NAME_REFERENCE EOF ;
    public final String entryRuleWORK_DEFINITION_NAME_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleWORK_DEFINITION_NAME_REFERENCE = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2751:2: (iv_ruleWORK_DEFINITION_NAME_REFERENCE= ruleWORK_DEFINITION_NAME_REFERENCE EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2752:2: iv_ruleWORK_DEFINITION_NAME_REFERENCE= ruleWORK_DEFINITION_NAME_REFERENCE EOF
            {
             newCompositeNode(grammarAccess.getWORK_DEFINITION_NAME_REFERENCERule()); 
            pushFollow(FOLLOW_ruleWORK_DEFINITION_NAME_REFERENCE_in_entryRuleWORK_DEFINITION_NAME_REFERENCE5638);
            iv_ruleWORK_DEFINITION_NAME_REFERENCE=ruleWORK_DEFINITION_NAME_REFERENCE();

            state._fsp--;

             current =iv_ruleWORK_DEFINITION_NAME_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleWORK_DEFINITION_NAME_REFERENCE5649); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2759:1: ruleWORK_DEFINITION_NAME_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleWORK_DEFINITION_NAME_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2762:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2763:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleWORK_DEFINITION_NAME_REFERENCE5688); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2778:1: entryRuleToState returns [EObject current=null] : iv_ruleToState= ruleToState EOF ;
    public final EObject entryRuleToState() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleToState = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2779:2: (iv_ruleToState= ruleToState EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2780:2: iv_ruleToState= ruleToState EOF
            {
             newCompositeNode(grammarAccess.getToStateRule()); 
            pushFollow(FOLLOW_ruleToState_in_entryRuleToState5732);
            iv_ruleToState=ruleToState();

            state._fsp--;

             current =iv_ruleToState; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleToState5742); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2787:1: ruleToState returns [EObject current=null] : (otherlv_0= 'to' ( ( ruleSTATE_NAME_REFERENCE ) ) ( (lv_options_2_0= ruleTransitionOption ) )* ) ;
    public final EObject ruleToState() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        AntlrDatatypeRuleToken lv_options_2_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2790:28: ( (otherlv_0= 'to' ( ( ruleSTATE_NAME_REFERENCE ) ) ( (lv_options_2_0= ruleTransitionOption ) )* ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2791:1: (otherlv_0= 'to' ( ( ruleSTATE_NAME_REFERENCE ) ) ( (lv_options_2_0= ruleTransitionOption ) )* )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2791:1: (otherlv_0= 'to' ( ( ruleSTATE_NAME_REFERENCE ) ) ( (lv_options_2_0= ruleTransitionOption ) )* )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2791:3: otherlv_0= 'to' ( ( ruleSTATE_NAME_REFERENCE ) ) ( (lv_options_2_0= ruleTransitionOption ) )*
            {
            otherlv_0=(Token)match(input,67,FOLLOW_67_in_ruleToState5779); 

                	newLeafNode(otherlv_0, grammarAccess.getToStateAccess().getToKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2795:1: ( ( ruleSTATE_NAME_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2796:1: ( ruleSTATE_NAME_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2796:1: ( ruleSTATE_NAME_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2797:3: ruleSTATE_NAME_REFERENCE
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getToStateRule());
            	        }
                    
             
            	        newCompositeNode(grammarAccess.getToStateAccess().getStateStateDefCrossReference_1_0()); 
            	    
            pushFollow(FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleToState5802);
            ruleSTATE_NAME_REFERENCE();

            state._fsp--;

             
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2810:2: ( (lv_options_2_0= ruleTransitionOption ) )*
            loop71:
            do {
                int alt71=2;
                int LA71_0 = input.LA(1);

                if ( (LA71_0==RULE_STRING||(LA71_0>=74 && LA71_0<=75)) ) {
                    alt71=1;
                }


                switch (alt71) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2811:1: (lv_options_2_0= ruleTransitionOption )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2811:1: (lv_options_2_0= ruleTransitionOption )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2812:3: lv_options_2_0= ruleTransitionOption
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getToStateAccess().getOptionsTransitionOptionParserRuleCall_2_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleTransitionOption_in_ruleToState5823);
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
            	    break loop71;
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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2836:1: entryRuleLayoutType returns [EObject current=null] : iv_ruleLayoutType= ruleLayoutType EOF ;
    public final EObject entryRuleLayoutType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleLayoutType = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2837:2: (iv_ruleLayoutType= ruleLayoutType EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2838:2: iv_ruleLayoutType= ruleLayoutType EOF
            {
             newCompositeNode(grammarAccess.getLayoutTypeRule()); 
            pushFollow(FOLLOW_ruleLayoutType_in_entryRuleLayoutType5860);
            iv_ruleLayoutType=ruleLayoutType();

            state._fsp--;

             current =iv_ruleLayoutType; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleLayoutType5870); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2845:1: ruleLayoutType returns [EObject current=null] : (this_LayoutDef_0= ruleLayoutDef | this_LayoutCopy_1= ruleLayoutCopy ) ;
    public final EObject ruleLayoutType() throws RecognitionException {
        EObject current = null;

        EObject this_LayoutDef_0 = null;

        EObject this_LayoutCopy_1 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2848:28: ( (this_LayoutDef_0= ruleLayoutDef | this_LayoutCopy_1= ruleLayoutCopy ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2849:1: (this_LayoutDef_0= ruleLayoutDef | this_LayoutCopy_1= ruleLayoutCopy )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2849:1: (this_LayoutDef_0= ruleLayoutDef | this_LayoutCopy_1= ruleLayoutCopy )
            int alt72=2;
            int LA72_0 = input.LA(1);

            if ( (LA72_0==68) ) {
                alt72=1;
            }
            else if ( (LA72_0==69) ) {
                alt72=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 72, 0, input);

                throw nvae;
            }
            switch (alt72) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2850:5: this_LayoutDef_0= ruleLayoutDef
                    {
                     
                            newCompositeNode(grammarAccess.getLayoutTypeAccess().getLayoutDefParserRuleCall_0()); 
                        
                    pushFollow(FOLLOW_ruleLayoutDef_in_ruleLayoutType5917);
                    this_LayoutDef_0=ruleLayoutDef();

                    state._fsp--;

                     
                            current = this_LayoutDef_0; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2860:5: this_LayoutCopy_1= ruleLayoutCopy
                    {
                     
                            newCompositeNode(grammarAccess.getLayoutTypeAccess().getLayoutCopyParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_ruleLayoutCopy_in_ruleLayoutType5944);
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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2876:1: entryRuleLayoutDef returns [EObject current=null] : iv_ruleLayoutDef= ruleLayoutDef EOF ;
    public final EObject entryRuleLayoutDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleLayoutDef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2877:2: (iv_ruleLayoutDef= ruleLayoutDef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2878:2: iv_ruleLayoutDef= ruleLayoutDef EOF
            {
             newCompositeNode(grammarAccess.getLayoutDefRule()); 
            pushFollow(FOLLOW_ruleLayoutDef_in_entryRuleLayoutDef5979);
            iv_ruleLayoutDef=ruleLayoutDef();

            state._fsp--;

             current =iv_ruleLayoutDef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleLayoutDef5989); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2885:1: ruleLayoutDef returns [EObject current=null] : (otherlv_0= 'layout' otherlv_1= '{' ( (lv_layoutItems_2_0= ruleLayoutItem ) )+ otherlv_3= '}' ) ;
    public final EObject ruleLayoutDef() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;
        Token otherlv_3=null;
        EObject lv_layoutItems_2_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2888:28: ( (otherlv_0= 'layout' otherlv_1= '{' ( (lv_layoutItems_2_0= ruleLayoutItem ) )+ otherlv_3= '}' ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2889:1: (otherlv_0= 'layout' otherlv_1= '{' ( (lv_layoutItems_2_0= ruleLayoutItem ) )+ otherlv_3= '}' )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2889:1: (otherlv_0= 'layout' otherlv_1= '{' ( (lv_layoutItems_2_0= ruleLayoutItem ) )+ otherlv_3= '}' )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2889:3: otherlv_0= 'layout' otherlv_1= '{' ( (lv_layoutItems_2_0= ruleLayoutItem ) )+ otherlv_3= '}'
            {
            otherlv_0=(Token)match(input,68,FOLLOW_68_in_ruleLayoutDef6026); 

                	newLeafNode(otherlv_0, grammarAccess.getLayoutDefAccess().getLayoutKeyword_0());
                
            otherlv_1=(Token)match(input,15,FOLLOW_15_in_ruleLayoutDef6038); 

                	newLeafNode(otherlv_1, grammarAccess.getLayoutDefAccess().getLeftCurlyBracketKeyword_1());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2897:1: ( (lv_layoutItems_2_0= ruleLayoutItem ) )+
            int cnt73=0;
            loop73:
            do {
                int alt73=2;
                int LA73_0 = input.LA(1);

                if ( ((LA73_0>=46 && LA73_0<=47)||LA73_0==70) ) {
                    alt73=1;
                }


                switch (alt73) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2898:1: (lv_layoutItems_2_0= ruleLayoutItem )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2898:1: (lv_layoutItems_2_0= ruleLayoutItem )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2899:3: lv_layoutItems_2_0= ruleLayoutItem
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getLayoutDefAccess().getLayoutItemsLayoutItemParserRuleCall_2_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleLayoutItem_in_ruleLayoutDef6059);
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
            	    if ( cnt73 >= 1 ) break loop73;
                        EarlyExitException eee =
                            new EarlyExitException(73, input);
                        throw eee;
                }
                cnt73++;
            } while (true);

            otherlv_3=(Token)match(input,20,FOLLOW_20_in_ruleLayoutDef6072); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2927:1: entryRuleLayoutCopy returns [EObject current=null] : iv_ruleLayoutCopy= ruleLayoutCopy EOF ;
    public final EObject entryRuleLayoutCopy() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleLayoutCopy = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2928:2: (iv_ruleLayoutCopy= ruleLayoutCopy EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2929:2: iv_ruleLayoutCopy= ruleLayoutCopy EOF
            {
             newCompositeNode(grammarAccess.getLayoutCopyRule()); 
            pushFollow(FOLLOW_ruleLayoutCopy_in_entryRuleLayoutCopy6108);
            iv_ruleLayoutCopy=ruleLayoutCopy();

            state._fsp--;

             current =iv_ruleLayoutCopy; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleLayoutCopy6118); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2936:1: ruleLayoutCopy returns [EObject current=null] : (otherlv_0= 'layoutCopyFrom' ( ( ruleSTATE_NAME_REFERENCE ) ) ) ;
    public final EObject ruleLayoutCopy() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2939:28: ( (otherlv_0= 'layoutCopyFrom' ( ( ruleSTATE_NAME_REFERENCE ) ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2940:1: (otherlv_0= 'layoutCopyFrom' ( ( ruleSTATE_NAME_REFERENCE ) ) )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2940:1: (otherlv_0= 'layoutCopyFrom' ( ( ruleSTATE_NAME_REFERENCE ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2940:3: otherlv_0= 'layoutCopyFrom' ( ( ruleSTATE_NAME_REFERENCE ) )
            {
            otherlv_0=(Token)match(input,69,FOLLOW_69_in_ruleLayoutCopy6155); 

                	newLeafNode(otherlv_0, grammarAccess.getLayoutCopyAccess().getLayoutCopyFromKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2944:1: ( ( ruleSTATE_NAME_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2945:1: ( ruleSTATE_NAME_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2945:1: ( ruleSTATE_NAME_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2946:3: ruleSTATE_NAME_REFERENCE
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getLayoutCopyRule());
            	        }
                    
             
            	        newCompositeNode(grammarAccess.getLayoutCopyAccess().getStateStateDefCrossReference_1_0()); 
            	    
            pushFollow(FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleLayoutCopy6178);
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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2967:1: entryRuleLayoutItem returns [EObject current=null] : iv_ruleLayoutItem= ruleLayoutItem EOF ;
    public final EObject entryRuleLayoutItem() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleLayoutItem = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2968:2: (iv_ruleLayoutItem= ruleLayoutItem EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2969:2: iv_ruleLayoutItem= ruleLayoutItem EOF
            {
             newCompositeNode(grammarAccess.getLayoutItemRule()); 
            pushFollow(FOLLOW_ruleLayoutItem_in_entryRuleLayoutItem6214);
            iv_ruleLayoutItem=ruleLayoutItem();

            state._fsp--;

             current =iv_ruleLayoutItem; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleLayoutItem6224); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2976:1: ruleLayoutItem returns [EObject current=null] : (this_WidgetRef_0= ruleWidgetRef | this_AttrWidget_1= ruleAttrWidget | this_Composite_2= ruleComposite ) ;
    public final EObject ruleLayoutItem() throws RecognitionException {
        EObject current = null;

        EObject this_WidgetRef_0 = null;

        EObject this_AttrWidget_1 = null;

        EObject this_Composite_2 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2979:28: ( (this_WidgetRef_0= ruleWidgetRef | this_AttrWidget_1= ruleAttrWidget | this_Composite_2= ruleComposite ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2980:1: (this_WidgetRef_0= ruleWidgetRef | this_AttrWidget_1= ruleAttrWidget | this_Composite_2= ruleComposite )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2980:1: (this_WidgetRef_0= ruleWidgetRef | this_AttrWidget_1= ruleAttrWidget | this_Composite_2= ruleComposite )
            int alt74=3;
            switch ( input.LA(1) ) {
            case 46:
                {
                alt74=1;
                }
                break;
            case 47:
                {
                alt74=2;
                }
                break;
            case 70:
                {
                alt74=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 74, 0, input);

                throw nvae;
            }

            switch (alt74) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2981:5: this_WidgetRef_0= ruleWidgetRef
                    {
                     
                            newCompositeNode(grammarAccess.getLayoutItemAccess().getWidgetRefParserRuleCall_0()); 
                        
                    pushFollow(FOLLOW_ruleWidgetRef_in_ruleLayoutItem6271);
                    this_WidgetRef_0=ruleWidgetRef();

                    state._fsp--;

                     
                            current = this_WidgetRef_0; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2991:5: this_AttrWidget_1= ruleAttrWidget
                    {
                     
                            newCompositeNode(grammarAccess.getLayoutItemAccess().getAttrWidgetParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_ruleAttrWidget_in_ruleLayoutItem6298);
                    this_AttrWidget_1=ruleAttrWidget();

                    state._fsp--;

                     
                            current = this_AttrWidget_1; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3001:5: this_Composite_2= ruleComposite
                    {
                     
                            newCompositeNode(grammarAccess.getLayoutItemAccess().getCompositeParserRuleCall_2()); 
                        
                    pushFollow(FOLLOW_ruleComposite_in_ruleLayoutItem6325);
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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3017:1: entryRuleComposite returns [EObject current=null] : iv_ruleComposite= ruleComposite EOF ;
    public final EObject entryRuleComposite() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleComposite = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3018:2: (iv_ruleComposite= ruleComposite EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3019:2: iv_ruleComposite= ruleComposite EOF
            {
             newCompositeNode(grammarAccess.getCompositeRule()); 
            pushFollow(FOLLOW_ruleComposite_in_entryRuleComposite6360);
            iv_ruleComposite=ruleComposite();

            state._fsp--;

             current =iv_ruleComposite; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleComposite6370); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3026:1: ruleComposite returns [EObject current=null] : (otherlv_0= 'composite' otherlv_1= '{' otherlv_2= 'numColumns' ( (lv_numColumns_3_0= RULE_INT ) ) ( (lv_layoutItems_4_0= ruleLayoutItem ) )+ (otherlv_5= 'option' ( (lv_options_6_0= ruleCompositeOption ) ) )* otherlv_7= '}' ) ;
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
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3029:28: ( (otherlv_0= 'composite' otherlv_1= '{' otherlv_2= 'numColumns' ( (lv_numColumns_3_0= RULE_INT ) ) ( (lv_layoutItems_4_0= ruleLayoutItem ) )+ (otherlv_5= 'option' ( (lv_options_6_0= ruleCompositeOption ) ) )* otherlv_7= '}' ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3030:1: (otherlv_0= 'composite' otherlv_1= '{' otherlv_2= 'numColumns' ( (lv_numColumns_3_0= RULE_INT ) ) ( (lv_layoutItems_4_0= ruleLayoutItem ) )+ (otherlv_5= 'option' ( (lv_options_6_0= ruleCompositeOption ) ) )* otherlv_7= '}' )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3030:1: (otherlv_0= 'composite' otherlv_1= '{' otherlv_2= 'numColumns' ( (lv_numColumns_3_0= RULE_INT ) ) ( (lv_layoutItems_4_0= ruleLayoutItem ) )+ (otherlv_5= 'option' ( (lv_options_6_0= ruleCompositeOption ) ) )* otherlv_7= '}' )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3030:3: otherlv_0= 'composite' otherlv_1= '{' otherlv_2= 'numColumns' ( (lv_numColumns_3_0= RULE_INT ) ) ( (lv_layoutItems_4_0= ruleLayoutItem ) )+ (otherlv_5= 'option' ( (lv_options_6_0= ruleCompositeOption ) ) )* otherlv_7= '}'
            {
            otherlv_0=(Token)match(input,70,FOLLOW_70_in_ruleComposite6407); 

                	newLeafNode(otherlv_0, grammarAccess.getCompositeAccess().getCompositeKeyword_0());
                
            otherlv_1=(Token)match(input,15,FOLLOW_15_in_ruleComposite6419); 

                	newLeafNode(otherlv_1, grammarAccess.getCompositeAccess().getLeftCurlyBracketKeyword_1());
                
            otherlv_2=(Token)match(input,71,FOLLOW_71_in_ruleComposite6431); 

                	newLeafNode(otherlv_2, grammarAccess.getCompositeAccess().getNumColumnsKeyword_2());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3042:1: ( (lv_numColumns_3_0= RULE_INT ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3043:1: (lv_numColumns_3_0= RULE_INT )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3043:1: (lv_numColumns_3_0= RULE_INT )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3044:3: lv_numColumns_3_0= RULE_INT
            {
            lv_numColumns_3_0=(Token)match(input,RULE_INT,FOLLOW_RULE_INT_in_ruleComposite6448); 

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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3060:2: ( (lv_layoutItems_4_0= ruleLayoutItem ) )+
            int cnt75=0;
            loop75:
            do {
                int alt75=2;
                int LA75_0 = input.LA(1);

                if ( ((LA75_0>=46 && LA75_0<=47)||LA75_0==70) ) {
                    alt75=1;
                }


                switch (alt75) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3061:1: (lv_layoutItems_4_0= ruleLayoutItem )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3061:1: (lv_layoutItems_4_0= ruleLayoutItem )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3062:3: lv_layoutItems_4_0= ruleLayoutItem
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getCompositeAccess().getLayoutItemsLayoutItemParserRuleCall_4_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleLayoutItem_in_ruleComposite6474);
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
            	    if ( cnt75 >= 1 ) break loop75;
                        EarlyExitException eee =
                            new EarlyExitException(75, input);
                        throw eee;
                }
                cnt75++;
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3078:3: (otherlv_5= 'option' ( (lv_options_6_0= ruleCompositeOption ) ) )*
            loop76:
            do {
                int alt76=2;
                int LA76_0 = input.LA(1);

                if ( (LA76_0==45) ) {
                    alt76=1;
                }


                switch (alt76) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3078:5: otherlv_5= 'option' ( (lv_options_6_0= ruleCompositeOption ) )
            	    {
            	    otherlv_5=(Token)match(input,45,FOLLOW_45_in_ruleComposite6488); 

            	        	newLeafNode(otherlv_5, grammarAccess.getCompositeAccess().getOptionKeyword_5_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3082:1: ( (lv_options_6_0= ruleCompositeOption ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3083:1: (lv_options_6_0= ruleCompositeOption )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3083:1: (lv_options_6_0= ruleCompositeOption )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3084:3: lv_options_6_0= ruleCompositeOption
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getCompositeAccess().getOptionsCompositeOptionParserRuleCall_5_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleCompositeOption_in_ruleComposite6509);
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
            	    break loop76;
                }
            } while (true);

            otherlv_7=(Token)match(input,20,FOLLOW_20_in_ruleComposite6523); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3112:1: entryRuleUSER_DEF_OPTION_NAME returns [String current=null] : iv_ruleUSER_DEF_OPTION_NAME= ruleUSER_DEF_OPTION_NAME EOF ;
    public final String entryRuleUSER_DEF_OPTION_NAME() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleUSER_DEF_OPTION_NAME = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3113:2: (iv_ruleUSER_DEF_OPTION_NAME= ruleUSER_DEF_OPTION_NAME EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3114:2: iv_ruleUSER_DEF_OPTION_NAME= ruleUSER_DEF_OPTION_NAME EOF
            {
             newCompositeNode(grammarAccess.getUSER_DEF_OPTION_NAMERule()); 
            pushFollow(FOLLOW_ruleUSER_DEF_OPTION_NAME_in_entryRuleUSER_DEF_OPTION_NAME6560);
            iv_ruleUSER_DEF_OPTION_NAME=ruleUSER_DEF_OPTION_NAME();

            state._fsp--;

             current =iv_ruleUSER_DEF_OPTION_NAME.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleUSER_DEF_OPTION_NAME6571); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3121:1: ruleUSER_DEF_OPTION_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleUSER_DEF_OPTION_NAME() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3124:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3125:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleUSER_DEF_OPTION_NAME6610); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3140:1: entryRuleUserDefOption returns [String current=null] : iv_ruleUserDefOption= ruleUserDefOption EOF ;
    public final String entryRuleUserDefOption() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleUserDefOption = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3141:2: (iv_ruleUserDefOption= ruleUserDefOption EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3142:2: iv_ruleUserDefOption= ruleUserDefOption EOF
            {
             newCompositeNode(grammarAccess.getUserDefOptionRule()); 
            pushFollow(FOLLOW_ruleUserDefOption_in_entryRuleUserDefOption6655);
            iv_ruleUserDefOption=ruleUserDefOption();

            state._fsp--;

             current =iv_ruleUserDefOption.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleUserDefOption6666); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3149:1: ruleUserDefOption returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'GetOrCreate' | this_USER_DEF_OPTION_NAME_1= ruleUSER_DEF_OPTION_NAME ) ;
    public final AntlrDatatypeRuleToken ruleUserDefOption() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        AntlrDatatypeRuleToken this_USER_DEF_OPTION_NAME_1 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3152:28: ( (kw= 'GetOrCreate' | this_USER_DEF_OPTION_NAME_1= ruleUSER_DEF_OPTION_NAME ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3153:1: (kw= 'GetOrCreate' | this_USER_DEF_OPTION_NAME_1= ruleUSER_DEF_OPTION_NAME )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3153:1: (kw= 'GetOrCreate' | this_USER_DEF_OPTION_NAME_1= ruleUSER_DEF_OPTION_NAME )
            int alt77=2;
            int LA77_0 = input.LA(1);

            if ( (LA77_0==72) ) {
                alt77=1;
            }
            else if ( (LA77_0==RULE_STRING) ) {
                alt77=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 77, 0, input);

                throw nvae;
            }
            switch (alt77) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3154:2: kw= 'GetOrCreate'
                    {
                    kw=(Token)match(input,72,FOLLOW_72_in_ruleUserDefOption6704); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getUserDefOptionAccess().getGetOrCreateKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3161:5: this_USER_DEF_OPTION_NAME_1= ruleUSER_DEF_OPTION_NAME
                    {
                     
                            newCompositeNode(grammarAccess.getUserDefOptionAccess().getUSER_DEF_OPTION_NAMEParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_ruleUSER_DEF_OPTION_NAME_in_ruleUserDefOption6732);
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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3179:1: entryRuleTEAM_DEF_OPTION_NAME returns [String current=null] : iv_ruleTEAM_DEF_OPTION_NAME= ruleTEAM_DEF_OPTION_NAME EOF ;
    public final String entryRuleTEAM_DEF_OPTION_NAME() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleTEAM_DEF_OPTION_NAME = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3180:2: (iv_ruleTEAM_DEF_OPTION_NAME= ruleTEAM_DEF_OPTION_NAME EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3181:2: iv_ruleTEAM_DEF_OPTION_NAME= ruleTEAM_DEF_OPTION_NAME EOF
            {
             newCompositeNode(grammarAccess.getTEAM_DEF_OPTION_NAMERule()); 
            pushFollow(FOLLOW_ruleTEAM_DEF_OPTION_NAME_in_entryRuleTEAM_DEF_OPTION_NAME6778);
            iv_ruleTEAM_DEF_OPTION_NAME=ruleTEAM_DEF_OPTION_NAME();

            state._fsp--;

             current =iv_ruleTEAM_DEF_OPTION_NAME.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleTEAM_DEF_OPTION_NAME6789); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3188:1: ruleTEAM_DEF_OPTION_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleTEAM_DEF_OPTION_NAME() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3191:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3192:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleTEAM_DEF_OPTION_NAME6828); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3207:1: entryRuleTeamDefOption returns [String current=null] : iv_ruleTeamDefOption= ruleTeamDefOption EOF ;
    public final String entryRuleTeamDefOption() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleTeamDefOption = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3208:2: (iv_ruleTeamDefOption= ruleTeamDefOption EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3209:2: iv_ruleTeamDefOption= ruleTeamDefOption EOF
            {
             newCompositeNode(grammarAccess.getTeamDefOptionRule()); 
            pushFollow(FOLLOW_ruleTeamDefOption_in_entryRuleTeamDefOption6873);
            iv_ruleTeamDefOption=ruleTeamDefOption();

            state._fsp--;

             current =iv_ruleTeamDefOption.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleTeamDefOption6884); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3216:1: ruleTeamDefOption returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'GetOrCreate' | this_TEAM_DEF_OPTION_NAME_1= ruleTEAM_DEF_OPTION_NAME ) ;
    public final AntlrDatatypeRuleToken ruleTeamDefOption() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        AntlrDatatypeRuleToken this_TEAM_DEF_OPTION_NAME_1 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3219:28: ( (kw= 'GetOrCreate' | this_TEAM_DEF_OPTION_NAME_1= ruleTEAM_DEF_OPTION_NAME ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3220:1: (kw= 'GetOrCreate' | this_TEAM_DEF_OPTION_NAME_1= ruleTEAM_DEF_OPTION_NAME )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3220:1: (kw= 'GetOrCreate' | this_TEAM_DEF_OPTION_NAME_1= ruleTEAM_DEF_OPTION_NAME )
            int alt78=2;
            int LA78_0 = input.LA(1);

            if ( (LA78_0==72) ) {
                alt78=1;
            }
            else if ( (LA78_0==RULE_STRING) ) {
                alt78=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 78, 0, input);

                throw nvae;
            }
            switch (alt78) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3221:2: kw= 'GetOrCreate'
                    {
                    kw=(Token)match(input,72,FOLLOW_72_in_ruleTeamDefOption6922); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getTeamDefOptionAccess().getGetOrCreateKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3228:5: this_TEAM_DEF_OPTION_NAME_1= ruleTEAM_DEF_OPTION_NAME
                    {
                     
                            newCompositeNode(grammarAccess.getTeamDefOptionAccess().getTEAM_DEF_OPTION_NAMEParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_ruleTEAM_DEF_OPTION_NAME_in_ruleTeamDefOption6950);
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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3246:1: entryRuleAI_DEF_OPTION_NAME returns [String current=null] : iv_ruleAI_DEF_OPTION_NAME= ruleAI_DEF_OPTION_NAME EOF ;
    public final String entryRuleAI_DEF_OPTION_NAME() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleAI_DEF_OPTION_NAME = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3247:2: (iv_ruleAI_DEF_OPTION_NAME= ruleAI_DEF_OPTION_NAME EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3248:2: iv_ruleAI_DEF_OPTION_NAME= ruleAI_DEF_OPTION_NAME EOF
            {
             newCompositeNode(grammarAccess.getAI_DEF_OPTION_NAMERule()); 
            pushFollow(FOLLOW_ruleAI_DEF_OPTION_NAME_in_entryRuleAI_DEF_OPTION_NAME6996);
            iv_ruleAI_DEF_OPTION_NAME=ruleAI_DEF_OPTION_NAME();

            state._fsp--;

             current =iv_ruleAI_DEF_OPTION_NAME.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAI_DEF_OPTION_NAME7007); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3255:1: ruleAI_DEF_OPTION_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleAI_DEF_OPTION_NAME() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3258:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3259:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAI_DEF_OPTION_NAME7046); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3274:1: entryRuleActionableItemOption returns [String current=null] : iv_ruleActionableItemOption= ruleActionableItemOption EOF ;
    public final String entryRuleActionableItemOption() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleActionableItemOption = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3275:2: (iv_ruleActionableItemOption= ruleActionableItemOption EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3276:2: iv_ruleActionableItemOption= ruleActionableItemOption EOF
            {
             newCompositeNode(grammarAccess.getActionableItemOptionRule()); 
            pushFollow(FOLLOW_ruleActionableItemOption_in_entryRuleActionableItemOption7091);
            iv_ruleActionableItemOption=ruleActionableItemOption();

            state._fsp--;

             current =iv_ruleActionableItemOption.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleActionableItemOption7102); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3283:1: ruleActionableItemOption returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'GetOrCreate' | this_AI_DEF_OPTION_NAME_1= ruleAI_DEF_OPTION_NAME ) ;
    public final AntlrDatatypeRuleToken ruleActionableItemOption() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        AntlrDatatypeRuleToken this_AI_DEF_OPTION_NAME_1 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3286:28: ( (kw= 'GetOrCreate' | this_AI_DEF_OPTION_NAME_1= ruleAI_DEF_OPTION_NAME ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3287:1: (kw= 'GetOrCreate' | this_AI_DEF_OPTION_NAME_1= ruleAI_DEF_OPTION_NAME )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3287:1: (kw= 'GetOrCreate' | this_AI_DEF_OPTION_NAME_1= ruleAI_DEF_OPTION_NAME )
            int alt79=2;
            int LA79_0 = input.LA(1);

            if ( (LA79_0==72) ) {
                alt79=1;
            }
            else if ( (LA79_0==RULE_STRING) ) {
                alt79=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 79, 0, input);

                throw nvae;
            }
            switch (alt79) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3288:2: kw= 'GetOrCreate'
                    {
                    kw=(Token)match(input,72,FOLLOW_72_in_ruleActionableItemOption7140); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getActionableItemOptionAccess().getGetOrCreateKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3295:5: this_AI_DEF_OPTION_NAME_1= ruleAI_DEF_OPTION_NAME
                    {
                     
                            newCompositeNode(grammarAccess.getActionableItemOptionAccess().getAI_DEF_OPTION_NAMEParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_ruleAI_DEF_OPTION_NAME_in_ruleActionableItemOption7168);
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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3313:1: entryRuleCOMPOSITE_OPTION_NAME returns [String current=null] : iv_ruleCOMPOSITE_OPTION_NAME= ruleCOMPOSITE_OPTION_NAME EOF ;
    public final String entryRuleCOMPOSITE_OPTION_NAME() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleCOMPOSITE_OPTION_NAME = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3314:2: (iv_ruleCOMPOSITE_OPTION_NAME= ruleCOMPOSITE_OPTION_NAME EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3315:2: iv_ruleCOMPOSITE_OPTION_NAME= ruleCOMPOSITE_OPTION_NAME EOF
            {
             newCompositeNode(grammarAccess.getCOMPOSITE_OPTION_NAMERule()); 
            pushFollow(FOLLOW_ruleCOMPOSITE_OPTION_NAME_in_entryRuleCOMPOSITE_OPTION_NAME7214);
            iv_ruleCOMPOSITE_OPTION_NAME=ruleCOMPOSITE_OPTION_NAME();

            state._fsp--;

             current =iv_ruleCOMPOSITE_OPTION_NAME.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleCOMPOSITE_OPTION_NAME7225); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3322:1: ruleCOMPOSITE_OPTION_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleCOMPOSITE_OPTION_NAME() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3325:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3326:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleCOMPOSITE_OPTION_NAME7264); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3341:1: entryRuleCompositeOption returns [String current=null] : iv_ruleCompositeOption= ruleCompositeOption EOF ;
    public final String entryRuleCompositeOption() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleCompositeOption = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3342:2: (iv_ruleCompositeOption= ruleCompositeOption EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3343:2: iv_ruleCompositeOption= ruleCompositeOption EOF
            {
             newCompositeNode(grammarAccess.getCompositeOptionRule()); 
            pushFollow(FOLLOW_ruleCompositeOption_in_entryRuleCompositeOption7309);
            iv_ruleCompositeOption=ruleCompositeOption();

            state._fsp--;

             current =iv_ruleCompositeOption.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleCompositeOption7320); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3350:1: ruleCompositeOption returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'None' | this_COMPOSITE_OPTION_NAME_1= ruleCOMPOSITE_OPTION_NAME ) ;
    public final AntlrDatatypeRuleToken ruleCompositeOption() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        AntlrDatatypeRuleToken this_COMPOSITE_OPTION_NAME_1 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3353:28: ( (kw= 'None' | this_COMPOSITE_OPTION_NAME_1= ruleCOMPOSITE_OPTION_NAME ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3354:1: (kw= 'None' | this_COMPOSITE_OPTION_NAME_1= ruleCOMPOSITE_OPTION_NAME )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3354:1: (kw= 'None' | this_COMPOSITE_OPTION_NAME_1= ruleCOMPOSITE_OPTION_NAME )
            int alt80=2;
            int LA80_0 = input.LA(1);

            if ( (LA80_0==73) ) {
                alt80=1;
            }
            else if ( (LA80_0==RULE_STRING) ) {
                alt80=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 80, 0, input);

                throw nvae;
            }
            switch (alt80) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3355:2: kw= 'None'
                    {
                    kw=(Token)match(input,73,FOLLOW_73_in_ruleCompositeOption7358); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getCompositeOptionAccess().getNoneKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3362:5: this_COMPOSITE_OPTION_NAME_1= ruleCOMPOSITE_OPTION_NAME
                    {
                     
                            newCompositeNode(grammarAccess.getCompositeOptionAccess().getCOMPOSITE_OPTION_NAMEParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_ruleCOMPOSITE_OPTION_NAME_in_ruleCompositeOption7386);
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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3380:1: entryRuleTRANSITION_OPTION_NAME returns [String current=null] : iv_ruleTRANSITION_OPTION_NAME= ruleTRANSITION_OPTION_NAME EOF ;
    public final String entryRuleTRANSITION_OPTION_NAME() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleTRANSITION_OPTION_NAME = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3381:2: (iv_ruleTRANSITION_OPTION_NAME= ruleTRANSITION_OPTION_NAME EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3382:2: iv_ruleTRANSITION_OPTION_NAME= ruleTRANSITION_OPTION_NAME EOF
            {
             newCompositeNode(grammarAccess.getTRANSITION_OPTION_NAMERule()); 
            pushFollow(FOLLOW_ruleTRANSITION_OPTION_NAME_in_entryRuleTRANSITION_OPTION_NAME7432);
            iv_ruleTRANSITION_OPTION_NAME=ruleTRANSITION_OPTION_NAME();

            state._fsp--;

             current =iv_ruleTRANSITION_OPTION_NAME.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleTRANSITION_OPTION_NAME7443); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3389:1: ruleTRANSITION_OPTION_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleTRANSITION_OPTION_NAME() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3392:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3393:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleTRANSITION_OPTION_NAME7482); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3408:1: entryRuleTransitionOption returns [String current=null] : iv_ruleTransitionOption= ruleTransitionOption EOF ;
    public final String entryRuleTransitionOption() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleTransitionOption = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3409:2: (iv_ruleTransitionOption= ruleTransitionOption EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3410:2: iv_ruleTransitionOption= ruleTransitionOption EOF
            {
             newCompositeNode(grammarAccess.getTransitionOptionRule()); 
            pushFollow(FOLLOW_ruleTransitionOption_in_entryRuleTransitionOption7527);
            iv_ruleTransitionOption=ruleTransitionOption();

            state._fsp--;

             current =iv_ruleTransitionOption.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleTransitionOption7538); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3417:1: ruleTransitionOption returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'AsDefault' | kw= 'OverrideAttributeValidation' | this_TRANSITION_OPTION_NAME_2= ruleTRANSITION_OPTION_NAME ) ;
    public final AntlrDatatypeRuleToken ruleTransitionOption() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        AntlrDatatypeRuleToken this_TRANSITION_OPTION_NAME_2 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3420:28: ( (kw= 'AsDefault' | kw= 'OverrideAttributeValidation' | this_TRANSITION_OPTION_NAME_2= ruleTRANSITION_OPTION_NAME ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3421:1: (kw= 'AsDefault' | kw= 'OverrideAttributeValidation' | this_TRANSITION_OPTION_NAME_2= ruleTRANSITION_OPTION_NAME )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3421:1: (kw= 'AsDefault' | kw= 'OverrideAttributeValidation' | this_TRANSITION_OPTION_NAME_2= ruleTRANSITION_OPTION_NAME )
            int alt81=3;
            switch ( input.LA(1) ) {
            case 74:
                {
                alt81=1;
                }
                break;
            case 75:
                {
                alt81=2;
                }
                break;
            case RULE_STRING:
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
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3422:2: kw= 'AsDefault'
                    {
                    kw=(Token)match(input,74,FOLLOW_74_in_ruleTransitionOption7576); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getTransitionOptionAccess().getAsDefaultKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3429:2: kw= 'OverrideAttributeValidation'
                    {
                    kw=(Token)match(input,75,FOLLOW_75_in_ruleTransitionOption7595); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getTransitionOptionAccess().getOverrideAttributeValidationKeyword_1()); 
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3436:5: this_TRANSITION_OPTION_NAME_2= ruleTRANSITION_OPTION_NAME
                    {
                     
                            newCompositeNode(grammarAccess.getTransitionOptionAccess().getTRANSITION_OPTION_NAMEParserRuleCall_2()); 
                        
                    pushFollow(FOLLOW_ruleTRANSITION_OPTION_NAME_in_ruleTransitionOption7623);
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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3454:1: entryRuleRULE_NAME returns [String current=null] : iv_ruleRULE_NAME= ruleRULE_NAME EOF ;
    public final String entryRuleRULE_NAME() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleRULE_NAME = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3455:2: (iv_ruleRULE_NAME= ruleRULE_NAME EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3456:2: iv_ruleRULE_NAME= ruleRULE_NAME EOF
            {
             newCompositeNode(grammarAccess.getRULE_NAMERule()); 
            pushFollow(FOLLOW_ruleRULE_NAME_in_entryRuleRULE_NAME7669);
            iv_ruleRULE_NAME=ruleRULE_NAME();

            state._fsp--;

             current =iv_ruleRULE_NAME.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleRULE_NAME7680); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3463:1: ruleRULE_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleRULE_NAME() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3466:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3467:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleRULE_NAME7719); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3482:1: entryRuleRule returns [String current=null] : iv_ruleRule= ruleRule EOF ;
    public final String entryRuleRule() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleRule = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3483:2: (iv_ruleRule= ruleRule EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3484:2: iv_ruleRule= ruleRule EOF
            {
             newCompositeNode(grammarAccess.getRuleRule()); 
            pushFollow(FOLLOW_ruleRule_in_entryRuleRule7764);
            iv_ruleRule=ruleRule();

            state._fsp--;

             current =iv_ruleRule.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleRule7775); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3491:1: ruleRule returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'RequireStateHourSpentPrompt' | kw= 'AddDecisionValidateBlockingReview' | kw= 'AddDecisionValidateNonBlockingReview' | kw= 'AllowTransitionWithWorkingBranch' | kw= 'ForceAssigneesToTeamLeads' | kw= 'RequireTargetedVersion' | kw= 'AllowPriviledgedEditToTeamMember' | kw= 'AllowPriviledgedEditToTeamMemberAndOriginator' | kw= 'AllowPriviledgedEditToAll' | kw= 'AllowEditToAll' | kw= 'AllowAssigneeToAll' | this_RULE_NAME_11= ruleRULE_NAME ) ;
    public final AntlrDatatypeRuleToken ruleRule() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        AntlrDatatypeRuleToken this_RULE_NAME_11 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3494:28: ( (kw= 'RequireStateHourSpentPrompt' | kw= 'AddDecisionValidateBlockingReview' | kw= 'AddDecisionValidateNonBlockingReview' | kw= 'AllowTransitionWithWorkingBranch' | kw= 'ForceAssigneesToTeamLeads' | kw= 'RequireTargetedVersion' | kw= 'AllowPriviledgedEditToTeamMember' | kw= 'AllowPriviledgedEditToTeamMemberAndOriginator' | kw= 'AllowPriviledgedEditToAll' | kw= 'AllowEditToAll' | kw= 'AllowAssigneeToAll' | this_RULE_NAME_11= ruleRULE_NAME ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3495:1: (kw= 'RequireStateHourSpentPrompt' | kw= 'AddDecisionValidateBlockingReview' | kw= 'AddDecisionValidateNonBlockingReview' | kw= 'AllowTransitionWithWorkingBranch' | kw= 'ForceAssigneesToTeamLeads' | kw= 'RequireTargetedVersion' | kw= 'AllowPriviledgedEditToTeamMember' | kw= 'AllowPriviledgedEditToTeamMemberAndOriginator' | kw= 'AllowPriviledgedEditToAll' | kw= 'AllowEditToAll' | kw= 'AllowAssigneeToAll' | this_RULE_NAME_11= ruleRULE_NAME )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3495:1: (kw= 'RequireStateHourSpentPrompt' | kw= 'AddDecisionValidateBlockingReview' | kw= 'AddDecisionValidateNonBlockingReview' | kw= 'AllowTransitionWithWorkingBranch' | kw= 'ForceAssigneesToTeamLeads' | kw= 'RequireTargetedVersion' | kw= 'AllowPriviledgedEditToTeamMember' | kw= 'AllowPriviledgedEditToTeamMemberAndOriginator' | kw= 'AllowPriviledgedEditToAll' | kw= 'AllowEditToAll' | kw= 'AllowAssigneeToAll' | this_RULE_NAME_11= ruleRULE_NAME )
            int alt82=12;
            switch ( input.LA(1) ) {
            case 76:
                {
                alt82=1;
                }
                break;
            case 77:
                {
                alt82=2;
                }
                break;
            case 78:
                {
                alt82=3;
                }
                break;
            case 79:
                {
                alt82=4;
                }
                break;
            case 80:
                {
                alt82=5;
                }
                break;
            case 81:
                {
                alt82=6;
                }
                break;
            case 82:
                {
                alt82=7;
                }
                break;
            case 83:
                {
                alt82=8;
                }
                break;
            case 84:
                {
                alt82=9;
                }
                break;
            case 85:
                {
                alt82=10;
                }
                break;
            case 86:
                {
                alt82=11;
                }
                break;
            case RULE_STRING:
                {
                alt82=12;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 82, 0, input);

                throw nvae;
            }

            switch (alt82) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3496:2: kw= 'RequireStateHourSpentPrompt'
                    {
                    kw=(Token)match(input,76,FOLLOW_76_in_ruleRule7813); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleAccess().getRequireStateHourSpentPromptKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3503:2: kw= 'AddDecisionValidateBlockingReview'
                    {
                    kw=(Token)match(input,77,FOLLOW_77_in_ruleRule7832); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleAccess().getAddDecisionValidateBlockingReviewKeyword_1()); 
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3510:2: kw= 'AddDecisionValidateNonBlockingReview'
                    {
                    kw=(Token)match(input,78,FOLLOW_78_in_ruleRule7851); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleAccess().getAddDecisionValidateNonBlockingReviewKeyword_2()); 
                        

                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3517:2: kw= 'AllowTransitionWithWorkingBranch'
                    {
                    kw=(Token)match(input,79,FOLLOW_79_in_ruleRule7870); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleAccess().getAllowTransitionWithWorkingBranchKeyword_3()); 
                        

                    }
                    break;
                case 5 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3524:2: kw= 'ForceAssigneesToTeamLeads'
                    {
                    kw=(Token)match(input,80,FOLLOW_80_in_ruleRule7889); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleAccess().getForceAssigneesToTeamLeadsKeyword_4()); 
                        

                    }
                    break;
                case 6 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3531:2: kw= 'RequireTargetedVersion'
                    {
                    kw=(Token)match(input,81,FOLLOW_81_in_ruleRule7908); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleAccess().getRequireTargetedVersionKeyword_5()); 
                        

                    }
                    break;
                case 7 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3538:2: kw= 'AllowPriviledgedEditToTeamMember'
                    {
                    kw=(Token)match(input,82,FOLLOW_82_in_ruleRule7927); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleAccess().getAllowPriviledgedEditToTeamMemberKeyword_6()); 
                        

                    }
                    break;
                case 8 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3545:2: kw= 'AllowPriviledgedEditToTeamMemberAndOriginator'
                    {
                    kw=(Token)match(input,83,FOLLOW_83_in_ruleRule7946); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleAccess().getAllowPriviledgedEditToTeamMemberAndOriginatorKeyword_7()); 
                        

                    }
                    break;
                case 9 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3552:2: kw= 'AllowPriviledgedEditToAll'
                    {
                    kw=(Token)match(input,84,FOLLOW_84_in_ruleRule7965); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleAccess().getAllowPriviledgedEditToAllKeyword_8()); 
                        

                    }
                    break;
                case 10 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3559:2: kw= 'AllowEditToAll'
                    {
                    kw=(Token)match(input,85,FOLLOW_85_in_ruleRule7984); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleAccess().getAllowEditToAllKeyword_9()); 
                        

                    }
                    break;
                case 11 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3566:2: kw= 'AllowAssigneeToAll'
                    {
                    kw=(Token)match(input,86,FOLLOW_86_in_ruleRule8003); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleAccess().getAllowAssigneeToAllKeyword_10()); 
                        

                    }
                    break;
                case 12 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3573:5: this_RULE_NAME_11= ruleRULE_NAME
                    {
                     
                            newCompositeNode(grammarAccess.getRuleAccess().getRULE_NAMEParserRuleCall_11()); 
                        
                    pushFollow(FOLLOW_ruleRULE_NAME_in_ruleRule8031);
                    this_RULE_NAME_11=ruleRULE_NAME();

                    state._fsp--;


                    		current.merge(this_RULE_NAME_11);
                        
                     
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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3591:1: entryRuleWIDGET_OPTION_NAME returns [String current=null] : iv_ruleWIDGET_OPTION_NAME= ruleWIDGET_OPTION_NAME EOF ;
    public final String entryRuleWIDGET_OPTION_NAME() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleWIDGET_OPTION_NAME = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3592:2: (iv_ruleWIDGET_OPTION_NAME= ruleWIDGET_OPTION_NAME EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3593:2: iv_ruleWIDGET_OPTION_NAME= ruleWIDGET_OPTION_NAME EOF
            {
             newCompositeNode(grammarAccess.getWIDGET_OPTION_NAMERule()); 
            pushFollow(FOLLOW_ruleWIDGET_OPTION_NAME_in_entryRuleWIDGET_OPTION_NAME8077);
            iv_ruleWIDGET_OPTION_NAME=ruleWIDGET_OPTION_NAME();

            state._fsp--;

             current =iv_ruleWIDGET_OPTION_NAME.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleWIDGET_OPTION_NAME8088); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3600:1: ruleWIDGET_OPTION_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleWIDGET_OPTION_NAME() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3603:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3604:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleWIDGET_OPTION_NAME8127); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3619:1: entryRuleWidgetOption returns [String current=null] : iv_ruleWidgetOption= ruleWidgetOption EOF ;
    public final String entryRuleWidgetOption() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleWidgetOption = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3620:2: (iv_ruleWidgetOption= ruleWidgetOption EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3621:2: iv_ruleWidgetOption= ruleWidgetOption EOF
            {
             newCompositeNode(grammarAccess.getWidgetOptionRule()); 
            pushFollow(FOLLOW_ruleWidgetOption_in_entryRuleWidgetOption8172);
            iv_ruleWidgetOption=ruleWidgetOption();

            state._fsp--;

             current =iv_ruleWidgetOption.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleWidgetOption8183); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3628:1: ruleWidgetOption returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'REQUIRED_FOR_TRANSITION' | kw= 'NOT_REQUIRED_FOR_TRANSITION' | kw= 'REQUIRED_FOR_COMPLETION' | kw= 'NOT_REQUIRED_FOR_COMPLETION' | kw= 'ENABLED' | kw= 'NOT_ENABLED' | kw= 'EDITABLE' | kw= 'NOT_EDITABLE' | kw= 'MULTI_SELECT' | kw= 'HORIZONTAL_LABEL' | kw= 'VERTICAL_LABEL' | kw= 'LABEL_AFTER' | kw= 'LABEL_BEFORE' | kw= 'NO_LABEL' | kw= 'SORTED' | kw= 'ADD_DEFAULT_VALUE' | kw= 'NO_DEFAULT_VALUE' | kw= 'BEGIN_COMPOSITE_4' | kw= 'BEGIN_COMPOSITE_6' | kw= 'BEGIN_COMPOSITE_8' | kw= 'BEGIN_COMPOSITE_10' | kw= 'END_COMPOSITE' | kw= 'FILL_NONE' | kw= 'FILL_HORIZONTALLY' | kw= 'FILL_VERTICALLY' | kw= 'ALIGN_LEFT' | kw= 'ALIGN_RIGHT' | kw= 'ALIGN_CENTER' | this_WIDGET_OPTION_NAME_28= ruleWIDGET_OPTION_NAME ) ;
    public final AntlrDatatypeRuleToken ruleWidgetOption() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        AntlrDatatypeRuleToken this_WIDGET_OPTION_NAME_28 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3631:28: ( (kw= 'REQUIRED_FOR_TRANSITION' | kw= 'NOT_REQUIRED_FOR_TRANSITION' | kw= 'REQUIRED_FOR_COMPLETION' | kw= 'NOT_REQUIRED_FOR_COMPLETION' | kw= 'ENABLED' | kw= 'NOT_ENABLED' | kw= 'EDITABLE' | kw= 'NOT_EDITABLE' | kw= 'MULTI_SELECT' | kw= 'HORIZONTAL_LABEL' | kw= 'VERTICAL_LABEL' | kw= 'LABEL_AFTER' | kw= 'LABEL_BEFORE' | kw= 'NO_LABEL' | kw= 'SORTED' | kw= 'ADD_DEFAULT_VALUE' | kw= 'NO_DEFAULT_VALUE' | kw= 'BEGIN_COMPOSITE_4' | kw= 'BEGIN_COMPOSITE_6' | kw= 'BEGIN_COMPOSITE_8' | kw= 'BEGIN_COMPOSITE_10' | kw= 'END_COMPOSITE' | kw= 'FILL_NONE' | kw= 'FILL_HORIZONTALLY' | kw= 'FILL_VERTICALLY' | kw= 'ALIGN_LEFT' | kw= 'ALIGN_RIGHT' | kw= 'ALIGN_CENTER' | this_WIDGET_OPTION_NAME_28= ruleWIDGET_OPTION_NAME ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3632:1: (kw= 'REQUIRED_FOR_TRANSITION' | kw= 'NOT_REQUIRED_FOR_TRANSITION' | kw= 'REQUIRED_FOR_COMPLETION' | kw= 'NOT_REQUIRED_FOR_COMPLETION' | kw= 'ENABLED' | kw= 'NOT_ENABLED' | kw= 'EDITABLE' | kw= 'NOT_EDITABLE' | kw= 'MULTI_SELECT' | kw= 'HORIZONTAL_LABEL' | kw= 'VERTICAL_LABEL' | kw= 'LABEL_AFTER' | kw= 'LABEL_BEFORE' | kw= 'NO_LABEL' | kw= 'SORTED' | kw= 'ADD_DEFAULT_VALUE' | kw= 'NO_DEFAULT_VALUE' | kw= 'BEGIN_COMPOSITE_4' | kw= 'BEGIN_COMPOSITE_6' | kw= 'BEGIN_COMPOSITE_8' | kw= 'BEGIN_COMPOSITE_10' | kw= 'END_COMPOSITE' | kw= 'FILL_NONE' | kw= 'FILL_HORIZONTALLY' | kw= 'FILL_VERTICALLY' | kw= 'ALIGN_LEFT' | kw= 'ALIGN_RIGHT' | kw= 'ALIGN_CENTER' | this_WIDGET_OPTION_NAME_28= ruleWIDGET_OPTION_NAME )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3632:1: (kw= 'REQUIRED_FOR_TRANSITION' | kw= 'NOT_REQUIRED_FOR_TRANSITION' | kw= 'REQUIRED_FOR_COMPLETION' | kw= 'NOT_REQUIRED_FOR_COMPLETION' | kw= 'ENABLED' | kw= 'NOT_ENABLED' | kw= 'EDITABLE' | kw= 'NOT_EDITABLE' | kw= 'MULTI_SELECT' | kw= 'HORIZONTAL_LABEL' | kw= 'VERTICAL_LABEL' | kw= 'LABEL_AFTER' | kw= 'LABEL_BEFORE' | kw= 'NO_LABEL' | kw= 'SORTED' | kw= 'ADD_DEFAULT_VALUE' | kw= 'NO_DEFAULT_VALUE' | kw= 'BEGIN_COMPOSITE_4' | kw= 'BEGIN_COMPOSITE_6' | kw= 'BEGIN_COMPOSITE_8' | kw= 'BEGIN_COMPOSITE_10' | kw= 'END_COMPOSITE' | kw= 'FILL_NONE' | kw= 'FILL_HORIZONTALLY' | kw= 'FILL_VERTICALLY' | kw= 'ALIGN_LEFT' | kw= 'ALIGN_RIGHT' | kw= 'ALIGN_CENTER' | this_WIDGET_OPTION_NAME_28= ruleWIDGET_OPTION_NAME )
            int alt83=29;
            switch ( input.LA(1) ) {
            case 87:
                {
                alt83=1;
                }
                break;
            case 88:
                {
                alt83=2;
                }
                break;
            case 89:
                {
                alt83=3;
                }
                break;
            case 90:
                {
                alt83=4;
                }
                break;
            case 91:
                {
                alt83=5;
                }
                break;
            case 92:
                {
                alt83=6;
                }
                break;
            case 93:
                {
                alt83=7;
                }
                break;
            case 94:
                {
                alt83=8;
                }
                break;
            case 95:
                {
                alt83=9;
                }
                break;
            case 96:
                {
                alt83=10;
                }
                break;
            case 97:
                {
                alt83=11;
                }
                break;
            case 98:
                {
                alt83=12;
                }
                break;
            case 99:
                {
                alt83=13;
                }
                break;
            case 100:
                {
                alt83=14;
                }
                break;
            case 101:
                {
                alt83=15;
                }
                break;
            case 102:
                {
                alt83=16;
                }
                break;
            case 103:
                {
                alt83=17;
                }
                break;
            case 104:
                {
                alt83=18;
                }
                break;
            case 105:
                {
                alt83=19;
                }
                break;
            case 106:
                {
                alt83=20;
                }
                break;
            case 107:
                {
                alt83=21;
                }
                break;
            case 108:
                {
                alt83=22;
                }
                break;
            case 109:
                {
                alt83=23;
                }
                break;
            case 110:
                {
                alt83=24;
                }
                break;
            case 111:
                {
                alt83=25;
                }
                break;
            case 112:
                {
                alt83=26;
                }
                break;
            case 113:
                {
                alt83=27;
                }
                break;
            case 114:
                {
                alt83=28;
                }
                break;
            case RULE_STRING:
                {
                alt83=29;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 83, 0, input);

                throw nvae;
            }

            switch (alt83) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3633:2: kw= 'REQUIRED_FOR_TRANSITION'
                    {
                    kw=(Token)match(input,87,FOLLOW_87_in_ruleWidgetOption8221); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getREQUIRED_FOR_TRANSITIONKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3640:2: kw= 'NOT_REQUIRED_FOR_TRANSITION'
                    {
                    kw=(Token)match(input,88,FOLLOW_88_in_ruleWidgetOption8240); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getNOT_REQUIRED_FOR_TRANSITIONKeyword_1()); 
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3647:2: kw= 'REQUIRED_FOR_COMPLETION'
                    {
                    kw=(Token)match(input,89,FOLLOW_89_in_ruleWidgetOption8259); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getREQUIRED_FOR_COMPLETIONKeyword_2()); 
                        

                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3654:2: kw= 'NOT_REQUIRED_FOR_COMPLETION'
                    {
                    kw=(Token)match(input,90,FOLLOW_90_in_ruleWidgetOption8278); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getNOT_REQUIRED_FOR_COMPLETIONKeyword_3()); 
                        

                    }
                    break;
                case 5 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3661:2: kw= 'ENABLED'
                    {
                    kw=(Token)match(input,91,FOLLOW_91_in_ruleWidgetOption8297); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getENABLEDKeyword_4()); 
                        

                    }
                    break;
                case 6 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3668:2: kw= 'NOT_ENABLED'
                    {
                    kw=(Token)match(input,92,FOLLOW_92_in_ruleWidgetOption8316); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getNOT_ENABLEDKeyword_5()); 
                        

                    }
                    break;
                case 7 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3675:2: kw= 'EDITABLE'
                    {
                    kw=(Token)match(input,93,FOLLOW_93_in_ruleWidgetOption8335); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getEDITABLEKeyword_6()); 
                        

                    }
                    break;
                case 8 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3682:2: kw= 'NOT_EDITABLE'
                    {
                    kw=(Token)match(input,94,FOLLOW_94_in_ruleWidgetOption8354); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getNOT_EDITABLEKeyword_7()); 
                        

                    }
                    break;
                case 9 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3689:2: kw= 'MULTI_SELECT'
                    {
                    kw=(Token)match(input,95,FOLLOW_95_in_ruleWidgetOption8373); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getMULTI_SELECTKeyword_8()); 
                        

                    }
                    break;
                case 10 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3696:2: kw= 'HORIZONTAL_LABEL'
                    {
                    kw=(Token)match(input,96,FOLLOW_96_in_ruleWidgetOption8392); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getHORIZONTAL_LABELKeyword_9()); 
                        

                    }
                    break;
                case 11 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3703:2: kw= 'VERTICAL_LABEL'
                    {
                    kw=(Token)match(input,97,FOLLOW_97_in_ruleWidgetOption8411); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getVERTICAL_LABELKeyword_10()); 
                        

                    }
                    break;
                case 12 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3710:2: kw= 'LABEL_AFTER'
                    {
                    kw=(Token)match(input,98,FOLLOW_98_in_ruleWidgetOption8430); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getLABEL_AFTERKeyword_11()); 
                        

                    }
                    break;
                case 13 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3717:2: kw= 'LABEL_BEFORE'
                    {
                    kw=(Token)match(input,99,FOLLOW_99_in_ruleWidgetOption8449); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getLABEL_BEFOREKeyword_12()); 
                        

                    }
                    break;
                case 14 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3724:2: kw= 'NO_LABEL'
                    {
                    kw=(Token)match(input,100,FOLLOW_100_in_ruleWidgetOption8468); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getNO_LABELKeyword_13()); 
                        

                    }
                    break;
                case 15 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3731:2: kw= 'SORTED'
                    {
                    kw=(Token)match(input,101,FOLLOW_101_in_ruleWidgetOption8487); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getSORTEDKeyword_14()); 
                        

                    }
                    break;
                case 16 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3738:2: kw= 'ADD_DEFAULT_VALUE'
                    {
                    kw=(Token)match(input,102,FOLLOW_102_in_ruleWidgetOption8506); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getADD_DEFAULT_VALUEKeyword_15()); 
                        

                    }
                    break;
                case 17 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3745:2: kw= 'NO_DEFAULT_VALUE'
                    {
                    kw=(Token)match(input,103,FOLLOW_103_in_ruleWidgetOption8525); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getNO_DEFAULT_VALUEKeyword_16()); 
                        

                    }
                    break;
                case 18 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3752:2: kw= 'BEGIN_COMPOSITE_4'
                    {
                    kw=(Token)match(input,104,FOLLOW_104_in_ruleWidgetOption8544); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getBEGIN_COMPOSITE_4Keyword_17()); 
                        

                    }
                    break;
                case 19 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3759:2: kw= 'BEGIN_COMPOSITE_6'
                    {
                    kw=(Token)match(input,105,FOLLOW_105_in_ruleWidgetOption8563); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getBEGIN_COMPOSITE_6Keyword_18()); 
                        

                    }
                    break;
                case 20 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3766:2: kw= 'BEGIN_COMPOSITE_8'
                    {
                    kw=(Token)match(input,106,FOLLOW_106_in_ruleWidgetOption8582); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getBEGIN_COMPOSITE_8Keyword_19()); 
                        

                    }
                    break;
                case 21 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3773:2: kw= 'BEGIN_COMPOSITE_10'
                    {
                    kw=(Token)match(input,107,FOLLOW_107_in_ruleWidgetOption8601); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getBEGIN_COMPOSITE_10Keyword_20()); 
                        

                    }
                    break;
                case 22 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3780:2: kw= 'END_COMPOSITE'
                    {
                    kw=(Token)match(input,108,FOLLOW_108_in_ruleWidgetOption8620); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getEND_COMPOSITEKeyword_21()); 
                        

                    }
                    break;
                case 23 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3787:2: kw= 'FILL_NONE'
                    {
                    kw=(Token)match(input,109,FOLLOW_109_in_ruleWidgetOption8639); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getFILL_NONEKeyword_22()); 
                        

                    }
                    break;
                case 24 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3794:2: kw= 'FILL_HORIZONTALLY'
                    {
                    kw=(Token)match(input,110,FOLLOW_110_in_ruleWidgetOption8658); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getFILL_HORIZONTALLYKeyword_23()); 
                        

                    }
                    break;
                case 25 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3801:2: kw= 'FILL_VERTICALLY'
                    {
                    kw=(Token)match(input,111,FOLLOW_111_in_ruleWidgetOption8677); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getFILL_VERTICALLYKeyword_24()); 
                        

                    }
                    break;
                case 26 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3808:2: kw= 'ALIGN_LEFT'
                    {
                    kw=(Token)match(input,112,FOLLOW_112_in_ruleWidgetOption8696); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getALIGN_LEFTKeyword_25()); 
                        

                    }
                    break;
                case 27 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3815:2: kw= 'ALIGN_RIGHT'
                    {
                    kw=(Token)match(input,113,FOLLOW_113_in_ruleWidgetOption8715); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getALIGN_RIGHTKeyword_26()); 
                        

                    }
                    break;
                case 28 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3822:2: kw= 'ALIGN_CENTER'
                    {
                    kw=(Token)match(input,114,FOLLOW_114_in_ruleWidgetOption8734); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getALIGN_CENTERKeyword_27()); 
                        

                    }
                    break;
                case 29 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3829:5: this_WIDGET_OPTION_NAME_28= ruleWIDGET_OPTION_NAME
                    {
                     
                            newCompositeNode(grammarAccess.getWidgetOptionAccess().getWIDGET_OPTION_NAMEParserRuleCall_28()); 
                        
                    pushFollow(FOLLOW_ruleWIDGET_OPTION_NAME_in_ruleWidgetOption8762);
                    this_WIDGET_OPTION_NAME_28=ruleWIDGET_OPTION_NAME();

                    state._fsp--;


                    		current.merge(this_WIDGET_OPTION_NAME_28);
                        
                     
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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3847:1: entryRulePAGE_TYPE_NAME returns [String current=null] : iv_rulePAGE_TYPE_NAME= rulePAGE_TYPE_NAME EOF ;
    public final String entryRulePAGE_TYPE_NAME() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_rulePAGE_TYPE_NAME = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3848:2: (iv_rulePAGE_TYPE_NAME= rulePAGE_TYPE_NAME EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3849:2: iv_rulePAGE_TYPE_NAME= rulePAGE_TYPE_NAME EOF
            {
             newCompositeNode(grammarAccess.getPAGE_TYPE_NAMERule()); 
            pushFollow(FOLLOW_rulePAGE_TYPE_NAME_in_entryRulePAGE_TYPE_NAME8808);
            iv_rulePAGE_TYPE_NAME=rulePAGE_TYPE_NAME();

            state._fsp--;

             current =iv_rulePAGE_TYPE_NAME.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRulePAGE_TYPE_NAME8819); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3856:1: rulePAGE_TYPE_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken rulePAGE_TYPE_NAME() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3859:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3860:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rulePAGE_TYPE_NAME8858); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3875:1: entryRulePageType returns [String current=null] : iv_rulePageType= rulePageType EOF ;
    public final String entryRulePageType() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_rulePageType = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3876:2: (iv_rulePageType= rulePageType EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3877:2: iv_rulePageType= rulePageType EOF
            {
             newCompositeNode(grammarAccess.getPageTypeRule()); 
            pushFollow(FOLLOW_rulePageType_in_entryRulePageType8903);
            iv_rulePageType=rulePageType();

            state._fsp--;

             current =iv_rulePageType.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRulePageType8914); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3884:1: rulePageType returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'Working' | kw= 'Completed' | kw= 'Cancelled' | this_PAGE_TYPE_NAME_3= rulePAGE_TYPE_NAME ) ;
    public final AntlrDatatypeRuleToken rulePageType() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        AntlrDatatypeRuleToken this_PAGE_TYPE_NAME_3 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3887:28: ( (kw= 'Working' | kw= 'Completed' | kw= 'Cancelled' | this_PAGE_TYPE_NAME_3= rulePAGE_TYPE_NAME ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3888:1: (kw= 'Working' | kw= 'Completed' | kw= 'Cancelled' | this_PAGE_TYPE_NAME_3= rulePAGE_TYPE_NAME )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3888:1: (kw= 'Working' | kw= 'Completed' | kw= 'Cancelled' | this_PAGE_TYPE_NAME_3= rulePAGE_TYPE_NAME )
            int alt84=4;
            switch ( input.LA(1) ) {
            case 115:
                {
                alt84=1;
                }
                break;
            case 116:
                {
                alt84=2;
                }
                break;
            case 117:
                {
                alt84=3;
                }
                break;
            case RULE_STRING:
                {
                alt84=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 84, 0, input);

                throw nvae;
            }

            switch (alt84) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3889:2: kw= 'Working'
                    {
                    kw=(Token)match(input,115,FOLLOW_115_in_rulePageType8952); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getPageTypeAccess().getWorkingKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3896:2: kw= 'Completed'
                    {
                    kw=(Token)match(input,116,FOLLOW_116_in_rulePageType8971); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getPageTypeAccess().getCompletedKeyword_1()); 
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3903:2: kw= 'Cancelled'
                    {
                    kw=(Token)match(input,117,FOLLOW_117_in_rulePageType8990); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getPageTypeAccess().getCancelledKeyword_2()); 
                        

                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3910:5: this_PAGE_TYPE_NAME_3= rulePAGE_TYPE_NAME
                    {
                     
                            newCompositeNode(grammarAccess.getPageTypeAccess().getPAGE_TYPE_NAMEParserRuleCall_3()); 
                        
                    pushFollow(FOLLOW_rulePAGE_TYPE_NAME_in_rulePageType9018);
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


    // $ANTLR start "ruleBooleanDef"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3928:1: ruleBooleanDef returns [Enumerator current=null] : ( (enumLiteral_0= 'None' ) | (enumLiteral_1= 'True' ) | (enumLiteral_2= 'False' ) ) ;
    public final Enumerator ruleBooleanDef() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;
        Token enumLiteral_2=null;

         enterRule(); 
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3930:28: ( ( (enumLiteral_0= 'None' ) | (enumLiteral_1= 'True' ) | (enumLiteral_2= 'False' ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3931:1: ( (enumLiteral_0= 'None' ) | (enumLiteral_1= 'True' ) | (enumLiteral_2= 'False' ) )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3931:1: ( (enumLiteral_0= 'None' ) | (enumLiteral_1= 'True' ) | (enumLiteral_2= 'False' ) )
            int alt85=3;
            switch ( input.LA(1) ) {
            case 73:
                {
                alt85=1;
                }
                break;
            case 118:
                {
                alt85=2;
                }
                break;
            case 119:
                {
                alt85=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 85, 0, input);

                throw nvae;
            }

            switch (alt85) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3931:2: (enumLiteral_0= 'None' )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3931:2: (enumLiteral_0= 'None' )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3931:4: enumLiteral_0= 'None'
                    {
                    enumLiteral_0=(Token)match(input,73,FOLLOW_73_in_ruleBooleanDef9077); 

                            current = grammarAccess.getBooleanDefAccess().getNoneEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_0, grammarAccess.getBooleanDefAccess().getNoneEnumLiteralDeclaration_0()); 
                        

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3937:6: (enumLiteral_1= 'True' )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3937:6: (enumLiteral_1= 'True' )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3937:8: enumLiteral_1= 'True'
                    {
                    enumLiteral_1=(Token)match(input,118,FOLLOW_118_in_ruleBooleanDef9094); 

                            current = grammarAccess.getBooleanDefAccess().getTrueEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_1, grammarAccess.getBooleanDefAccess().getTrueEnumLiteralDeclaration_1()); 
                        

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3943:6: (enumLiteral_2= 'False' )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3943:6: (enumLiteral_2= 'False' )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3943:8: enumLiteral_2= 'False'
                    {
                    enumLiteral_2=(Token)match(input,119,FOLLOW_119_in_ruleBooleanDef9111); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3953:1: ruleWorkflowEventType returns [Enumerator current=null] : ( (enumLiteral_0= 'TransitionTo' ) | (enumLiteral_1= 'CreateBranch' ) | (enumLiteral_2= 'CommitBranch' ) ) ;
    public final Enumerator ruleWorkflowEventType() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;
        Token enumLiteral_2=null;

         enterRule(); 
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3955:28: ( ( (enumLiteral_0= 'TransitionTo' ) | (enumLiteral_1= 'CreateBranch' ) | (enumLiteral_2= 'CommitBranch' ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3956:1: ( (enumLiteral_0= 'TransitionTo' ) | (enumLiteral_1= 'CreateBranch' ) | (enumLiteral_2= 'CommitBranch' ) )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3956:1: ( (enumLiteral_0= 'TransitionTo' ) | (enumLiteral_1= 'CreateBranch' ) | (enumLiteral_2= 'CommitBranch' ) )
            int alt86=3;
            switch ( input.LA(1) ) {
            case 120:
                {
                alt86=1;
                }
                break;
            case 121:
                {
                alt86=2;
                }
                break;
            case 122:
                {
                alt86=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 86, 0, input);

                throw nvae;
            }

            switch (alt86) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3956:2: (enumLiteral_0= 'TransitionTo' )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3956:2: (enumLiteral_0= 'TransitionTo' )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3956:4: enumLiteral_0= 'TransitionTo'
                    {
                    enumLiteral_0=(Token)match(input,120,FOLLOW_120_in_ruleWorkflowEventType9156); 

                            current = grammarAccess.getWorkflowEventTypeAccess().getTransitionToEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_0, grammarAccess.getWorkflowEventTypeAccess().getTransitionToEnumLiteralDeclaration_0()); 
                        

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3962:6: (enumLiteral_1= 'CreateBranch' )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3962:6: (enumLiteral_1= 'CreateBranch' )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3962:8: enumLiteral_1= 'CreateBranch'
                    {
                    enumLiteral_1=(Token)match(input,121,FOLLOW_121_in_ruleWorkflowEventType9173); 

                            current = grammarAccess.getWorkflowEventTypeAccess().getCreateBranchEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_1, grammarAccess.getWorkflowEventTypeAccess().getCreateBranchEnumLiteralDeclaration_1()); 
                        

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3968:6: (enumLiteral_2= 'CommitBranch' )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3968:6: (enumLiteral_2= 'CommitBranch' )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3968:8: enumLiteral_2= 'CommitBranch'
                    {
                    enumLiteral_2=(Token)match(input,122,FOLLOW_122_in_ruleWorkflowEventType9190); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3978:1: ruleReviewBlockingType returns [Enumerator current=null] : ( (enumLiteral_0= 'Transition' ) | (enumLiteral_1= 'Commit' ) ) ;
    public final Enumerator ruleReviewBlockingType() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;

         enterRule(); 
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3980:28: ( ( (enumLiteral_0= 'Transition' ) | (enumLiteral_1= 'Commit' ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3981:1: ( (enumLiteral_0= 'Transition' ) | (enumLiteral_1= 'Commit' ) )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3981:1: ( (enumLiteral_0= 'Transition' ) | (enumLiteral_1= 'Commit' ) )
            int alt87=2;
            int LA87_0 = input.LA(1);

            if ( (LA87_0==123) ) {
                alt87=1;
            }
            else if ( (LA87_0==124) ) {
                alt87=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 87, 0, input);

                throw nvae;
            }
            switch (alt87) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3981:2: (enumLiteral_0= 'Transition' )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3981:2: (enumLiteral_0= 'Transition' )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3981:4: enumLiteral_0= 'Transition'
                    {
                    enumLiteral_0=(Token)match(input,123,FOLLOW_123_in_ruleReviewBlockingType9235); 

                            current = grammarAccess.getReviewBlockingTypeAccess().getTransitionEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_0, grammarAccess.getReviewBlockingTypeAccess().getTransitionEnumLiteralDeclaration_0()); 
                        

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3987:6: (enumLiteral_1= 'Commit' )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3987:6: (enumLiteral_1= 'Commit' )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3987:8: enumLiteral_1= 'Commit'
                    {
                    enumLiteral_1=(Token)match(input,124,FOLLOW_124_in_ruleReviewBlockingType9252); 

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
    public static final BitSet FOLLOW_ruleUSER_DEF_REFERENCE_in_ruleUserDef441 = new BitSet(new long[]{0x0000000000008012L,0x0000000000000100L});
    public static final BitSet FOLLOW_ruleUserDefOption_in_ruleUserDef462 = new BitSet(new long[]{0x0000000000008012L,0x0000000000000100L});
    public static final BitSet FOLLOW_15_in_ruleUserDef476 = new BitSet(new long[]{0x00000000001F0000L});
    public static final BitSet FOLLOW_16_in_ruleUserDef489 = new BitSet(new long[]{0x0000000000000000L,0x00C0000000000200L});
    public static final BitSet FOLLOW_ruleBooleanDef_in_ruleUserDef510 = new BitSet(new long[]{0x00000000001E0000L});
    public static final BitSet FOLLOW_17_in_ruleUserDef525 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleUserDef542 = new BitSet(new long[]{0x00000000001C0000L});
    public static final BitSet FOLLOW_18_in_ruleUserDef562 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleUserDef579 = new BitSet(new long[]{0x0000000000180000L});
    public static final BitSet FOLLOW_19_in_ruleUserDef599 = new BitSet(new long[]{0x0000000000000000L,0x00C0000000000200L});
    public static final BitSet FOLLOW_ruleBooleanDef_in_ruleUserDef620 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_ruleUserDef634 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTEAM_DEF_REFERENCE_in_entryRuleTEAM_DEF_REFERENCE673 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleTEAM_DEF_REFERENCE684 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleTEAM_DEF_REFERENCE723 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTeamDef_in_entryRuleTeamDef767 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleTeamDef777 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTEAM_DEF_REFERENCE_in_ruleTeamDef823 = new BitSet(new long[]{0x0000000000008010L,0x0000000000000100L});
    public static final BitSet FOLLOW_ruleTeamDefOption_in_ruleTeamDef844 = new BitSet(new long[]{0x0000000000008010L,0x0000000000000100L});
    public static final BitSet FOLLOW_15_in_ruleTeamDef857 = new BitSet(new long[]{0x000000001FF10800L});
    public static final BitSet FOLLOW_16_in_ruleTeamDef870 = new BitSet(new long[]{0x0000000000000000L,0x00C0000000000200L});
    public static final BitSet FOLLOW_ruleBooleanDef_in_ruleTeamDef891 = new BitSet(new long[]{0x000000001FF00800L});
    public static final BitSet FOLLOW_21_in_ruleTeamDef906 = new BitSet(new long[]{0x0000000000000000L,0x00C0000000000200L});
    public static final BitSet FOLLOW_ruleBooleanDef_in_ruleTeamDef927 = new BitSet(new long[]{0x000000001FD00800L});
    public static final BitSet FOLLOW_22_in_ruleTeamDef942 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleTeamDef959 = new BitSet(new long[]{0x000000001FD00800L});
    public static final BitSet FOLLOW_23_in_ruleTeamDef979 = new BitSet(new long[]{0x0000000000020000L,0x0000000000000004L});
    public static final BitSet FOLLOW_ruleUserRef_in_ruleTeamDef1000 = new BitSet(new long[]{0x000000001F900800L});
    public static final BitSet FOLLOW_24_in_ruleTeamDef1015 = new BitSet(new long[]{0x0000000000020000L,0x0000000000000004L});
    public static final BitSet FOLLOW_ruleUserRef_in_ruleTeamDef1036 = new BitSet(new long[]{0x000000001F100800L});
    public static final BitSet FOLLOW_25_in_ruleTeamDef1051 = new BitSet(new long[]{0x0000000000020000L,0x0000000000000004L});
    public static final BitSet FOLLOW_ruleUserRef_in_ruleTeamDef1072 = new BitSet(new long[]{0x000000001E100800L});
    public static final BitSet FOLLOW_11_in_ruleTeamDef1087 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleTeamDef1104 = new BitSet(new long[]{0x000000001C100000L});
    public static final BitSet FOLLOW_26_in_ruleTeamDef1124 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleTeamDef1141 = new BitSet(new long[]{0x000000001C100000L});
    public static final BitSet FOLLOW_27_in_ruleTeamDef1161 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleVersionDef_in_ruleTeamDef1182 = new BitSet(new long[]{0x0000000018100000L});
    public static final BitSet FOLLOW_28_in_ruleTeamDef1197 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_ruleTeamDef1209 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_13_in_ruleTeamDef1222 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleTeamDef_in_ruleTeamDef1243 = new BitSet(new long[]{0x0000000000102000L});
    public static final BitSet FOLLOW_20_in_ruleTeamDef1257 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_ruleTeamDef1271 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAI_DEF_REFERENCE_in_entryRuleAI_DEF_REFERENCE1308 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAI_DEF_REFERENCE1319 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAI_DEF_REFERENCE1358 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleActionableItemDef_in_entryRuleActionableItemDef1402 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleActionableItemDef1412 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAI_DEF_REFERENCE_in_ruleActionableItemDef1458 = new BitSet(new long[]{0x0000000000008012L,0x0000000000000100L});
    public static final BitSet FOLLOW_ruleActionableItemOption_in_ruleActionableItemDef1479 = new BitSet(new long[]{0x0000000000008012L,0x0000000000000100L});
    public static final BitSet FOLLOW_15_in_ruleActionableItemDef1493 = new BitSet(new long[]{0x0000000074D10000L});
    public static final BitSet FOLLOW_16_in_ruleActionableItemDef1506 = new BitSet(new long[]{0x0000000000000000L,0x00C0000000000200L});
    public static final BitSet FOLLOW_ruleBooleanDef_in_ruleActionableItemDef1527 = new BitSet(new long[]{0x0000000074D00000L});
    public static final BitSet FOLLOW_29_in_ruleActionableItemDef1542 = new BitSet(new long[]{0x0000000000000000L,0x00C0000000000200L});
    public static final BitSet FOLLOW_ruleBooleanDef_in_ruleActionableItemDef1563 = new BitSet(new long[]{0x0000000054D00000L});
    public static final BitSet FOLLOW_23_in_ruleActionableItemDef1578 = new BitSet(new long[]{0x0000000000020000L,0x0000000000000004L});
    public static final BitSet FOLLOW_ruleUserRef_in_ruleActionableItemDef1599 = new BitSet(new long[]{0x0000000054D00000L});
    public static final BitSet FOLLOW_22_in_ruleActionableItemDef1614 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleActionableItemDef1631 = new BitSet(new long[]{0x0000000054500000L});
    public static final BitSet FOLLOW_30_in_ruleActionableItemDef1651 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleActionableItemDef1668 = new BitSet(new long[]{0x0000000014100000L});
    public static final BitSet FOLLOW_26_in_ruleActionableItemDef1688 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleActionableItemDef1705 = new BitSet(new long[]{0x0000000014100000L});
    public static final BitSet FOLLOW_28_in_ruleActionableItemDef1725 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_ruleActionableItemDef1737 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_14_in_ruleActionableItemDef1750 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleActionableItemDef_in_ruleActionableItemDef1771 = new BitSet(new long[]{0x0000000000104000L});
    public static final BitSet FOLLOW_20_in_ruleActionableItemDef1785 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_ruleActionableItemDef1799 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleVERSION_DEF_REFERENCE_in_entryRuleVERSION_DEF_REFERENCE1838 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleVERSION_DEF_REFERENCE1849 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleVERSION_DEF_REFERENCE1888 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleVersionDef_in_entryRuleVersionDef1932 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleVersionDef1942 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleVERSION_DEF_REFERENCE_in_ruleVersionDef1988 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_ruleVersionDef2000 = new BitSet(new long[]{0x0000001F80510000L});
    public static final BitSet FOLLOW_16_in_ruleVersionDef2013 = new BitSet(new long[]{0x0000000000000000L,0x00C0000000000200L});
    public static final BitSet FOLLOW_ruleBooleanDef_in_ruleVersionDef2034 = new BitSet(new long[]{0x0000001F80500000L});
    public static final BitSet FOLLOW_22_in_ruleVersionDef2049 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleVersionDef2066 = new BitSet(new long[]{0x0000001F80500000L});
    public static final BitSet FOLLOW_31_in_ruleVersionDef2086 = new BitSet(new long[]{0x0000000000000000L,0x00C0000000000200L});
    public static final BitSet FOLLOW_ruleBooleanDef_in_ruleVersionDef2107 = new BitSet(new long[]{0x0000001F00100000L});
    public static final BitSet FOLLOW_32_in_ruleVersionDef2122 = new BitSet(new long[]{0x0000000000000000L,0x00C0000000000200L});
    public static final BitSet FOLLOW_ruleBooleanDef_in_ruleVersionDef2143 = new BitSet(new long[]{0x0000001E00100000L});
    public static final BitSet FOLLOW_33_in_ruleVersionDef2158 = new BitSet(new long[]{0x0000000000000000L,0x00C0000000000200L});
    public static final BitSet FOLLOW_ruleBooleanDef_in_ruleVersionDef2179 = new BitSet(new long[]{0x0000001C00100000L});
    public static final BitSet FOLLOW_34_in_ruleVersionDef2194 = new BitSet(new long[]{0x0000000000000000L,0x00C0000000000200L});
    public static final BitSet FOLLOW_ruleBooleanDef_in_ruleVersionDef2215 = new BitSet(new long[]{0x0000001800100000L});
    public static final BitSet FOLLOW_35_in_ruleVersionDef2230 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleVersionDef2247 = new BitSet(new long[]{0x0000001000100000L});
    public static final BitSet FOLLOW_36_in_ruleVersionDef2267 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleVersionDef2284 = new BitSet(new long[]{0x0000001000100000L});
    public static final BitSet FOLLOW_20_in_ruleVersionDef2303 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleWorkDef_in_entryRuleWorkDef2339 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleWorkDef2349 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleWORK_DEFINITION_NAME_REFERENCE_in_ruleWorkDef2395 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_ruleWorkDef2407 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_37_in_ruleWorkDef2420 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleWorkDef2437 = new BitSet(new long[]{0x0000006000000000L});
    public static final BitSet FOLLOW_38_in_ruleWorkDef2457 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleWorkDef2480 = new BitSet(new long[]{0x8082008000000000L});
    public static final BitSet FOLLOW_ruleWidgetDef_in_ruleWorkDef2502 = new BitSet(new long[]{0x8082008000000000L});
    public static final BitSet FOLLOW_ruleDecisionReviewDef_in_ruleWorkDef2524 = new BitSet(new long[]{0x8082008000000000L});
    public static final BitSet FOLLOW_rulePeerReviewDef_in_ruleWorkDef2546 = new BitSet(new long[]{0x8082008000000000L});
    public static final BitSet FOLLOW_ruleStateDef_in_ruleWorkDef2568 = new BitSet(new long[]{0x8082008000100000L});
    public static final BitSet FOLLOW_20_in_ruleWorkDef2581 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleWidgetDef_in_entryRuleWidgetDef2617 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleWidgetDef2627 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_39_in_ruleWidgetDef2664 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleWIDGET_NAME_REFERENCE_in_ruleWidgetDef2685 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_ruleWidgetDef2697 = new BitSet(new long[]{0x00003F0000100000L});
    public static final BitSet FOLLOW_40_in_ruleWidgetDef2710 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleWidgetDef2727 = new BitSet(new long[]{0x00003E0000100000L});
    public static final BitSet FOLLOW_41_in_ruleWidgetDef2747 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleWidgetDef2764 = new BitSet(new long[]{0x00003C0000100000L});
    public static final BitSet FOLLOW_42_in_ruleWidgetDef2784 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleWidgetDef2801 = new BitSet(new long[]{0x0000380000100000L});
    public static final BitSet FOLLOW_43_in_ruleWidgetDef2821 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleWidgetDef2838 = new BitSet(new long[]{0x0000300000100000L});
    public static final BitSet FOLLOW_44_in_ruleWidgetDef2858 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_RULE_INT_in_ruleWidgetDef2875 = new BitSet(new long[]{0x0000200000100000L});
    public static final BitSet FOLLOW_45_in_ruleWidgetDef2895 = new BitSet(new long[]{0x0000000000000010L,0x0007FFFFFF800000L});
    public static final BitSet FOLLOW_ruleWidgetOption_in_ruleWidgetDef2916 = new BitSet(new long[]{0x0000200000100000L});
    public static final BitSet FOLLOW_20_in_ruleWidgetDef2930 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleWidgetRef_in_entryRuleWidgetRef2966 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleWidgetRef2976 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_46_in_ruleWidgetRef3013 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleWIDGET_NAME_REFERENCE_in_ruleWidgetRef3036 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttrWidget_in_entryRuleAttrWidget3072 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttrWidget3082 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_ruleAttrWidget3119 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAttrWidget3136 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_48_in_ruleAttrWidget3154 = new BitSet(new long[]{0x0000000000000010L,0x0007FFFFFF800000L});
    public static final BitSet FOLLOW_ruleWidgetOption_in_ruleAttrWidget3175 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_ruleStateDef_in_entryRuleStateDef3213 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleStateDef3223 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_49_in_ruleStateDef3260 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleStateDef3281 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_ruleStateDef3293 = new BitSet(new long[]{0x0004020000000000L});
    public static final BitSet FOLLOW_41_in_ruleStateDef3306 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleStateDef3323 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_50_in_ruleStateDef3342 = new BitSet(new long[]{0x0000000000000010L,0x0038000000000000L});
    public static final BitSet FOLLOW_rulePageType_in_ruleStateDef3363 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_ruleStateDef3375 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_RULE_INT_in_ruleStateDef3392 = new BitSet(new long[]{0x4070000000100000L,0x0000000000000038L});
    public static final BitSet FOLLOW_ruleToState_in_ruleStateDef3418 = new BitSet(new long[]{0x4070000000100000L,0x0000000000000038L});
    public static final BitSet FOLLOW_52_in_ruleStateDef3432 = new BitSet(new long[]{0x0000000000000010L,0x00000000007FF000L});
    public static final BitSet FOLLOW_ruleRule_in_ruleStateDef3453 = new BitSet(new long[]{0x4070000000100000L,0x0000000000000030L});
    public static final BitSet FOLLOW_ruleDecisionReviewRef_in_ruleStateDef3476 = new BitSet(new long[]{0x4060000000100000L,0x0000000000000030L});
    public static final BitSet FOLLOW_rulePeerReviewRef_in_ruleStateDef3498 = new BitSet(new long[]{0x4020000000100000L,0x0000000000000030L});
    public static final BitSet FOLLOW_53_in_ruleStateDef3512 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_RULE_INT_in_ruleStateDef3529 = new BitSet(new long[]{0x0000000000100000L,0x0000000000000030L});
    public static final BitSet FOLLOW_ruleLayoutType_in_ruleStateDef3557 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_ruleStateDef3570 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDecisionReviewRef_in_entryRuleDecisionReviewRef3606 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleDecisionReviewRef3616 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_54_in_ruleDecisionReviewRef3653 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleDECISION_REVIEW_NAME_REFERENCE_in_ruleDecisionReviewRef3676 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDecisionReviewDef_in_entryRuleDecisionReviewDef3712 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleDecisionReviewDef3722 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_55_in_ruleDecisionReviewDef3759 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleDECISION_REVIEW_NAME_REFERENCE_in_ruleDecisionReviewDef3780 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_ruleDecisionReviewDef3792 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_56_in_ruleDecisionReviewDef3804 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleDecisionReviewDef3821 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_41_in_ruleDecisionReviewDef3838 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleDecisionReviewDef3855 = new BitSet(new long[]{0x0600000000000000L});
    public static final BitSet FOLLOW_57_in_ruleDecisionReviewDef3873 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleDecisionReviewDef3896 = new BitSet(new long[]{0x0400000000000000L});
    public static final BitSet FOLLOW_58_in_ruleDecisionReviewDef3910 = new BitSet(new long[]{0x0000000000000000L,0x1800000000000000L});
    public static final BitSet FOLLOW_ruleReviewBlockingType_in_ruleDecisionReviewDef3931 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_59_in_ruleDecisionReviewDef3943 = new BitSet(new long[]{0x0000000000000000L,0x0700000000000000L});
    public static final BitSet FOLLOW_ruleWorkflowEventType_in_ruleDecisionReviewDef3964 = new BitSet(new long[]{0x3000200000000000L});
    public static final BitSet FOLLOW_60_in_ruleDecisionReviewDef3977 = new BitSet(new long[]{0x0000000000020000L,0x0000000000000004L});
    public static final BitSet FOLLOW_ruleUserRef_in_ruleDecisionReviewDef3998 = new BitSet(new long[]{0x3000200000000000L});
    public static final BitSet FOLLOW_61_in_ruleDecisionReviewDef4013 = new BitSet(new long[]{0x0000000000000000L,0x00C0000000000200L});
    public static final BitSet FOLLOW_ruleBooleanDef_in_ruleDecisionReviewDef4034 = new BitSet(new long[]{0x3000200000000000L});
    public static final BitSet FOLLOW_ruleDecisionReviewOpt_in_ruleDecisionReviewDef4057 = new BitSet(new long[]{0x3000200000100000L});
    public static final BitSet FOLLOW_20_in_ruleDecisionReviewDef4070 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDECISION_REVIEW_OPT_REF_in_entryRuleDECISION_REVIEW_OPT_REF4107 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleDECISION_REVIEW_OPT_REF4118 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleDECISION_REVIEW_OPT_REF4157 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDecisionReviewOpt_in_entryRuleDecisionReviewOpt4201 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleDecisionReviewOpt4211 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_45_in_ruleDecisionReviewOpt4248 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleDECISION_REVIEW_OPT_REF_in_ruleDecisionReviewOpt4269 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_ruleFollowupRef_in_ruleDecisionReviewOpt4290 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rulePeerReviewRef_in_entryRulePeerReviewRef4327 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRulePeerReviewRef4337 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_62_in_rulePeerReviewRef4374 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rulePEER_REVIEW_NAME_REFERENCE_in_rulePeerReviewRef4397 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rulePeerReviewDef_in_entryRulePeerReviewDef4433 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRulePeerReviewDef4443 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_63_in_rulePeerReviewDef4480 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rulePEER_REVIEW_NAME_REFERENCE_in_rulePeerReviewDef4501 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_rulePeerReviewDef4513 = new BitSet(new long[]{0x0100020000000000L});
    public static final BitSet FOLLOW_56_in_rulePeerReviewDef4526 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_rulePeerReviewDef4543 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_41_in_rulePeerReviewDef4562 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_rulePeerReviewDef4579 = new BitSet(new long[]{0x0600000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_64_in_rulePeerReviewDef4597 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_rulePeerReviewDef4614 = new BitSet(new long[]{0x0600000000000000L});
    public static final BitSet FOLLOW_57_in_rulePeerReviewDef4634 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleSTATE_NAME_REFERENCE_in_rulePeerReviewDef4657 = new BitSet(new long[]{0x0400000000000000L});
    public static final BitSet FOLLOW_58_in_rulePeerReviewDef4671 = new BitSet(new long[]{0x0000000000000000L,0x1800000000000000L});
    public static final BitSet FOLLOW_ruleReviewBlockingType_in_rulePeerReviewDef4692 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_59_in_rulePeerReviewDef4704 = new BitSet(new long[]{0x0000000000000000L,0x0700000000000000L});
    public static final BitSet FOLLOW_ruleWorkflowEventType_in_rulePeerReviewDef4725 = new BitSet(new long[]{0x1000000000100000L});
    public static final BitSet FOLLOW_60_in_rulePeerReviewDef4738 = new BitSet(new long[]{0x0000000000020000L,0x0000000000000004L});
    public static final BitSet FOLLOW_ruleUserRef_in_rulePeerReviewDef4759 = new BitSet(new long[]{0x1000000000100000L});
    public static final BitSet FOLLOW_20_in_rulePeerReviewDef4773 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleFollowupRef_in_entryRuleFollowupRef4809 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleFollowupRef4819 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_ruleFollowupRef4856 = new BitSet(new long[]{0x1000000000000000L});
    public static final BitSet FOLLOW_60_in_ruleFollowupRef4869 = new BitSet(new long[]{0x0000000000020000L,0x0000000000000004L});
    public static final BitSet FOLLOW_ruleUserRef_in_ruleFollowupRef4890 = new BitSet(new long[]{0x1000000000000002L});
    public static final BitSet FOLLOW_ruleUserRef_in_entryRuleUserRef4928 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleUserRef4938 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleUserByUserId_in_ruleUserRef4985 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleUserByName_in_ruleUserRef5012 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleUserByUserId_in_entryRuleUserByUserId5047 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleUserByUserId5057 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_17_in_ruleUserByUserId5094 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleUserByUserId5111 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleUserByName_in_entryRuleUserByName5152 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleUserByName5162 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_66_in_ruleUserByName5199 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleUserByName5216 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDECISION_REVIEW_NAME_REFERENCE_in_entryRuleDECISION_REVIEW_NAME_REFERENCE5258 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleDECISION_REVIEW_NAME_REFERENCE5269 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleDECISION_REVIEW_NAME_REFERENCE5308 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rulePEER_REVIEW_NAME_REFERENCE_in_entryRulePEER_REVIEW_NAME_REFERENCE5353 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRulePEER_REVIEW_NAME_REFERENCE5364 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rulePEER_REVIEW_NAME_REFERENCE5403 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleSTATE_NAME_REFERENCE_in_entryRuleSTATE_NAME_REFERENCE5448 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleSTATE_NAME_REFERENCE5459 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleSTATE_NAME_REFERENCE5498 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleWIDGET_NAME_REFERENCE_in_entryRuleWIDGET_NAME_REFERENCE5543 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleWIDGET_NAME_REFERENCE5554 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleWIDGET_NAME_REFERENCE5593 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleWORK_DEFINITION_NAME_REFERENCE_in_entryRuleWORK_DEFINITION_NAME_REFERENCE5638 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleWORK_DEFINITION_NAME_REFERENCE5649 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleWORK_DEFINITION_NAME_REFERENCE5688 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleToState_in_entryRuleToState5732 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleToState5742 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_67_in_ruleToState5779 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleToState5802 = new BitSet(new long[]{0x0000000000000012L,0x0000000000000C00L});
    public static final BitSet FOLLOW_ruleTransitionOption_in_ruleToState5823 = new BitSet(new long[]{0x0000000000000012L,0x0000000000000C00L});
    public static final BitSet FOLLOW_ruleLayoutType_in_entryRuleLayoutType5860 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleLayoutType5870 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleLayoutDef_in_ruleLayoutType5917 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleLayoutCopy_in_ruleLayoutType5944 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleLayoutDef_in_entryRuleLayoutDef5979 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleLayoutDef5989 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_68_in_ruleLayoutDef6026 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_ruleLayoutDef6038 = new BitSet(new long[]{0x0000C00000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_ruleLayoutItem_in_ruleLayoutDef6059 = new BitSet(new long[]{0x0000C00000100000L,0x0000000000000040L});
    public static final BitSet FOLLOW_20_in_ruleLayoutDef6072 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleLayoutCopy_in_entryRuleLayoutCopy6108 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleLayoutCopy6118 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_69_in_ruleLayoutCopy6155 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleLayoutCopy6178 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleLayoutItem_in_entryRuleLayoutItem6214 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleLayoutItem6224 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleWidgetRef_in_ruleLayoutItem6271 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttrWidget_in_ruleLayoutItem6298 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleComposite_in_ruleLayoutItem6325 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleComposite_in_entryRuleComposite6360 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleComposite6370 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_70_in_ruleComposite6407 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_ruleComposite6419 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_71_in_ruleComposite6431 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_RULE_INT_in_ruleComposite6448 = new BitSet(new long[]{0x0000C00000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_ruleLayoutItem_in_ruleComposite6474 = new BitSet(new long[]{0x0000E00000100000L,0x0000000000000040L});
    public static final BitSet FOLLOW_45_in_ruleComposite6488 = new BitSet(new long[]{0x0000000000000010L,0x0000000000000200L});
    public static final BitSet FOLLOW_ruleCompositeOption_in_ruleComposite6509 = new BitSet(new long[]{0x0000200000100000L});
    public static final BitSet FOLLOW_20_in_ruleComposite6523 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleUSER_DEF_OPTION_NAME_in_entryRuleUSER_DEF_OPTION_NAME6560 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleUSER_DEF_OPTION_NAME6571 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleUSER_DEF_OPTION_NAME6610 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleUserDefOption_in_entryRuleUserDefOption6655 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleUserDefOption6666 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_72_in_ruleUserDefOption6704 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleUSER_DEF_OPTION_NAME_in_ruleUserDefOption6732 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTEAM_DEF_OPTION_NAME_in_entryRuleTEAM_DEF_OPTION_NAME6778 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleTEAM_DEF_OPTION_NAME6789 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleTEAM_DEF_OPTION_NAME6828 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTeamDefOption_in_entryRuleTeamDefOption6873 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleTeamDefOption6884 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_72_in_ruleTeamDefOption6922 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTEAM_DEF_OPTION_NAME_in_ruleTeamDefOption6950 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAI_DEF_OPTION_NAME_in_entryRuleAI_DEF_OPTION_NAME6996 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAI_DEF_OPTION_NAME7007 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAI_DEF_OPTION_NAME7046 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleActionableItemOption_in_entryRuleActionableItemOption7091 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleActionableItemOption7102 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_72_in_ruleActionableItemOption7140 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAI_DEF_OPTION_NAME_in_ruleActionableItemOption7168 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleCOMPOSITE_OPTION_NAME_in_entryRuleCOMPOSITE_OPTION_NAME7214 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleCOMPOSITE_OPTION_NAME7225 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleCOMPOSITE_OPTION_NAME7264 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleCompositeOption_in_entryRuleCompositeOption7309 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleCompositeOption7320 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_73_in_ruleCompositeOption7358 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleCOMPOSITE_OPTION_NAME_in_ruleCompositeOption7386 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTRANSITION_OPTION_NAME_in_entryRuleTRANSITION_OPTION_NAME7432 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleTRANSITION_OPTION_NAME7443 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleTRANSITION_OPTION_NAME7482 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTransitionOption_in_entryRuleTransitionOption7527 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleTransitionOption7538 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_74_in_ruleTransitionOption7576 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_75_in_ruleTransitionOption7595 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTRANSITION_OPTION_NAME_in_ruleTransitionOption7623 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRULE_NAME_in_entryRuleRULE_NAME7669 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRULE_NAME7680 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleRULE_NAME7719 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRule_in_entryRuleRule7764 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRule7775 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_76_in_ruleRule7813 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_77_in_ruleRule7832 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_78_in_ruleRule7851 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_79_in_ruleRule7870 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_80_in_ruleRule7889 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_81_in_ruleRule7908 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_82_in_ruleRule7927 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_83_in_ruleRule7946 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_84_in_ruleRule7965 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_85_in_ruleRule7984 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_86_in_ruleRule8003 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRULE_NAME_in_ruleRule8031 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleWIDGET_OPTION_NAME_in_entryRuleWIDGET_OPTION_NAME8077 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleWIDGET_OPTION_NAME8088 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleWIDGET_OPTION_NAME8127 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleWidgetOption_in_entryRuleWidgetOption8172 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleWidgetOption8183 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_87_in_ruleWidgetOption8221 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_88_in_ruleWidgetOption8240 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_89_in_ruleWidgetOption8259 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_90_in_ruleWidgetOption8278 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_91_in_ruleWidgetOption8297 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_92_in_ruleWidgetOption8316 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_93_in_ruleWidgetOption8335 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_94_in_ruleWidgetOption8354 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_95_in_ruleWidgetOption8373 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_96_in_ruleWidgetOption8392 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_97_in_ruleWidgetOption8411 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_98_in_ruleWidgetOption8430 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_99_in_ruleWidgetOption8449 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_100_in_ruleWidgetOption8468 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_101_in_ruleWidgetOption8487 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_102_in_ruleWidgetOption8506 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_103_in_ruleWidgetOption8525 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_104_in_ruleWidgetOption8544 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_105_in_ruleWidgetOption8563 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_106_in_ruleWidgetOption8582 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_107_in_ruleWidgetOption8601 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_108_in_ruleWidgetOption8620 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_109_in_ruleWidgetOption8639 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_110_in_ruleWidgetOption8658 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_111_in_ruleWidgetOption8677 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_112_in_ruleWidgetOption8696 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_113_in_ruleWidgetOption8715 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_114_in_ruleWidgetOption8734 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleWIDGET_OPTION_NAME_in_ruleWidgetOption8762 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rulePAGE_TYPE_NAME_in_entryRulePAGE_TYPE_NAME8808 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRulePAGE_TYPE_NAME8819 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rulePAGE_TYPE_NAME8858 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rulePageType_in_entryRulePageType8903 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRulePageType8914 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_115_in_rulePageType8952 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_116_in_rulePageType8971 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_117_in_rulePageType8990 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rulePAGE_TYPE_NAME_in_rulePageType9018 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_73_in_ruleBooleanDef9077 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_118_in_ruleBooleanDef9094 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_119_in_ruleBooleanDef9111 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_120_in_ruleWorkflowEventType9156 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_121_in_ruleWorkflowEventType9173 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_122_in_ruleWorkflowEventType9190 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_123_in_ruleReviewBlockingType9235 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_124_in_ruleReviewBlockingType9252 = new BitSet(new long[]{0x0000000000000002L});

}