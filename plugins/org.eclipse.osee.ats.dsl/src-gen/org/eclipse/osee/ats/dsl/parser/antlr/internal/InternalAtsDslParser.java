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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE_STRING", "RULE_INT", "RULE_ID", "RULE_ML_COMMENT", "RULE_SL_COMMENT", "RULE_WS", "RULE_ANY_OTHER", "'workDefinition'", "'userDefinition'", "'teamDefinition'", "'actionableItem'", "'{'", "'active'", "'userId'", "'email'", "'isAdmin'", "'}'", "'usesVersions'", "'staticId'", "'lead'", "'member'", "'priviledged'", "'version'", "'children'", "'actionable'", "'team'", "'next'", "'released'", "'allowCreateBranch'", "'allowCommitBranch'", "'baslineBranchGuid'", "'id'", "'startState'", "'widgetDefinition'", "'attributeName'", "'description'", "'xWidgetName'", "'defaultValue'", "'height'", "'option'", "'widget'", "'attributeWidget'", "'with'", "'state'", "'type'", "'ordinal'", "'rule'", "'percentWeight'", "'decisionReview'", "'decisionReviewDefinition'", "'title'", "'relatedToState'", "'blockingType'", "'onEvent'", "'assignee'", "'autoTransitionToDecision'", "'peerReview'", "'peerReviewDefinition'", "'location'", "'followup by'", "'named'", "'to'", "'layout'", "'layoutCopyFrom'", "'composite'", "'numColumns'", "'GetOrCreate'", "'None'", "'AsDefault'", "'OverrideAttributeValidation'", "'RequireStateHourSpentPrompt'", "'AddDecisionValidateBlockingReview'", "'AddDecisionValidateNonBlockingReview'", "'AllowTransitionWithWorkingBranch'", "'ForceAssigneesToTeamLeads'", "'RequireTargetedVersion'", "'AllowPriviledgedEditToTeamMember'", "'AllowPriviledgedEditToTeamMemberAndOriginator'", "'AllowPriviledgedEditToAll'", "'AllowEditToAll'", "'AllowAssigneeToAll'", "'REQUIRED_FOR_TRANSITION'", "'NOT_REQUIRED_FOR_TRANSITION'", "'REQUIRED_FOR_COMPLETION'", "'NOT_REQUIRED_FOR_COMPLETION'", "'ENABLED'", "'NOT_ENABLED'", "'EDITABLE'", "'NOT_EDITABLE'", "'MULTI_SELECT'", "'HORIZONTAL_LABEL'", "'VERTICAL_LABEL'", "'LABEL_AFTER'", "'LABEL_BEFORE'", "'NO_LABEL'", "'SORTED'", "'ADD_DEFAULT_VALUE'", "'NO_DEFAULT_VALUE'", "'BEGIN_COMPOSITE_4'", "'BEGIN_COMPOSITE_6'", "'BEGIN_COMPOSITE_8'", "'BEGIN_COMPOSITE_10'", "'END_COMPOSITE'", "'FILL_NONE'", "'FILL_HORIZONTALLY'", "'FILL_VERTICALLY'", "'ALIGN_LEFT'", "'ALIGN_RIGHT'", "'ALIGN_CENTER'", "'Working'", "'Completed'", "'Cancelled'", "'True'", "'False'", "'TransitionTo'", "'CreateBranch'", "'CommitBranch'", "'Transition'", "'Commit'"
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


    // $ANTLR start "entryRuleUserDef"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:177:1: entryRuleUserDef returns [EObject current=null] : iv_ruleUserDef= ruleUserDef EOF ;
    public final EObject entryRuleUserDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleUserDef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:178:2: (iv_ruleUserDef= ruleUserDef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:179:2: iv_ruleUserDef= ruleUserDef EOF
            {
             newCompositeNode(grammarAccess.getUserDefRule()); 
            pushFollow(FOLLOW_ruleUserDef_in_entryRuleUserDef290);
            iv_ruleUserDef=ruleUserDef();

            state._fsp--;

             current =iv_ruleUserDef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleUserDef300); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:186:1: ruleUserDef returns [EObject current=null] : ( ( (lv_name_0_0= RULE_STRING ) ) ( (lv_userDefOption_1_0= ruleUserDefOption ) )* (otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'userId' ( (lv_userId_6_0= RULE_STRING ) ) )? (otherlv_7= 'email' ( (lv_email_8_0= RULE_STRING ) ) )? (otherlv_9= 'isAdmin' ( (lv_admin_10_0= ruleBooleanDef ) ) )? otherlv_11= '}' )? ) ;
    public final EObject ruleUserDef() throws RecognitionException {
        EObject current = null;

        Token lv_name_0_0=null;
        Token otherlv_2=null;
        Token otherlv_3=null;
        Token otherlv_5=null;
        Token lv_userId_6_0=null;
        Token otherlv_7=null;
        Token lv_email_8_0=null;
        Token otherlv_9=null;
        Token otherlv_11=null;
        AntlrDatatypeRuleToken lv_userDefOption_1_0 = null;

        Enumerator lv_active_4_0 = null;

        Enumerator lv_admin_10_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:189:28: ( ( ( (lv_name_0_0= RULE_STRING ) ) ( (lv_userDefOption_1_0= ruleUserDefOption ) )* (otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'userId' ( (lv_userId_6_0= RULE_STRING ) ) )? (otherlv_7= 'email' ( (lv_email_8_0= RULE_STRING ) ) )? (otherlv_9= 'isAdmin' ( (lv_admin_10_0= ruleBooleanDef ) ) )? otherlv_11= '}' )? ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:190:1: ( ( (lv_name_0_0= RULE_STRING ) ) ( (lv_userDefOption_1_0= ruleUserDefOption ) )* (otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'userId' ( (lv_userId_6_0= RULE_STRING ) ) )? (otherlv_7= 'email' ( (lv_email_8_0= RULE_STRING ) ) )? (otherlv_9= 'isAdmin' ( (lv_admin_10_0= ruleBooleanDef ) ) )? otherlv_11= '}' )? )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:190:1: ( ( (lv_name_0_0= RULE_STRING ) ) ( (lv_userDefOption_1_0= ruleUserDefOption ) )* (otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'userId' ( (lv_userId_6_0= RULE_STRING ) ) )? (otherlv_7= 'email' ( (lv_email_8_0= RULE_STRING ) ) )? (otherlv_9= 'isAdmin' ( (lv_admin_10_0= ruleBooleanDef ) ) )? otherlv_11= '}' )? )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:190:2: ( (lv_name_0_0= RULE_STRING ) ) ( (lv_userDefOption_1_0= ruleUserDefOption ) )* (otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'userId' ( (lv_userId_6_0= RULE_STRING ) ) )? (otherlv_7= 'email' ( (lv_email_8_0= RULE_STRING ) ) )? (otherlv_9= 'isAdmin' ( (lv_admin_10_0= ruleBooleanDef ) ) )? otherlv_11= '}' )?
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:190:2: ( (lv_name_0_0= RULE_STRING ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:191:1: (lv_name_0_0= RULE_STRING )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:191:1: (lv_name_0_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:192:3: lv_name_0_0= RULE_STRING
            {
            lv_name_0_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleUserDef342); 

            			newLeafNode(lv_name_0_0, grammarAccess.getUserDefAccess().getNameSTRINGTerminalRuleCall_0_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getUserDefRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"name",
                    		lv_name_0_0, 
                    		"STRING");
            	    

            }


            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:208:2: ( (lv_userDefOption_1_0= ruleUserDefOption ) )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==RULE_STRING||LA5_0==70) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:209:1: (lv_userDefOption_1_0= ruleUserDefOption )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:209:1: (lv_userDefOption_1_0= ruleUserDefOption )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:210:3: lv_userDefOption_1_0= ruleUserDefOption
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getUserDefAccess().getUserDefOptionUserDefOptionParserRuleCall_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleUserDefOption_in_ruleUserDef368);
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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:226:3: (otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'userId' ( (lv_userId_6_0= RULE_STRING ) ) )? (otherlv_7= 'email' ( (lv_email_8_0= RULE_STRING ) ) )? (otherlv_9= 'isAdmin' ( (lv_admin_10_0= ruleBooleanDef ) ) )? otherlv_11= '}' )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==15) ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:226:5: otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'userId' ( (lv_userId_6_0= RULE_STRING ) ) )? (otherlv_7= 'email' ( (lv_email_8_0= RULE_STRING ) ) )? (otherlv_9= 'isAdmin' ( (lv_admin_10_0= ruleBooleanDef ) ) )? otherlv_11= '}'
                    {
                    otherlv_2=(Token)match(input,15,FOLLOW_15_in_ruleUserDef382); 

                        	newLeafNode(otherlv_2, grammarAccess.getUserDefAccess().getLeftCurlyBracketKeyword_2_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:230:1: (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )?
                    int alt6=2;
                    int LA6_0 = input.LA(1);

                    if ( (LA6_0==16) ) {
                        alt6=1;
                    }
                    switch (alt6) {
                        case 1 :
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:230:3: otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) )
                            {
                            otherlv_3=(Token)match(input,16,FOLLOW_16_in_ruleUserDef395); 

                                	newLeafNode(otherlv_3, grammarAccess.getUserDefAccess().getActiveKeyword_2_1_0());
                                
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:234:1: ( (lv_active_4_0= ruleBooleanDef ) )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:235:1: (lv_active_4_0= ruleBooleanDef )
                            {
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:235:1: (lv_active_4_0= ruleBooleanDef )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:236:3: lv_active_4_0= ruleBooleanDef
                            {
                             
                            	        newCompositeNode(grammarAccess.getUserDefAccess().getActiveBooleanDefEnumRuleCall_2_1_1_0()); 
                            	    
                            pushFollow(FOLLOW_ruleBooleanDef_in_ruleUserDef416);
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

                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:252:4: (otherlv_5= 'userId' ( (lv_userId_6_0= RULE_STRING ) ) )?
                    int alt7=2;
                    int LA7_0 = input.LA(1);

                    if ( (LA7_0==17) ) {
                        alt7=1;
                    }
                    switch (alt7) {
                        case 1 :
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:252:6: otherlv_5= 'userId' ( (lv_userId_6_0= RULE_STRING ) )
                            {
                            otherlv_5=(Token)match(input,17,FOLLOW_17_in_ruleUserDef431); 

                                	newLeafNode(otherlv_5, grammarAccess.getUserDefAccess().getUserIdKeyword_2_2_0());
                                
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:256:1: ( (lv_userId_6_0= RULE_STRING ) )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:257:1: (lv_userId_6_0= RULE_STRING )
                            {
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:257:1: (lv_userId_6_0= RULE_STRING )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:258:3: lv_userId_6_0= RULE_STRING
                            {
                            lv_userId_6_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleUserDef448); 

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

                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:274:4: (otherlv_7= 'email' ( (lv_email_8_0= RULE_STRING ) ) )?
                    int alt8=2;
                    int LA8_0 = input.LA(1);

                    if ( (LA8_0==18) ) {
                        alt8=1;
                    }
                    switch (alt8) {
                        case 1 :
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:274:6: otherlv_7= 'email' ( (lv_email_8_0= RULE_STRING ) )
                            {
                            otherlv_7=(Token)match(input,18,FOLLOW_18_in_ruleUserDef468); 

                                	newLeafNode(otherlv_7, grammarAccess.getUserDefAccess().getEmailKeyword_2_3_0());
                                
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:278:1: ( (lv_email_8_0= RULE_STRING ) )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:279:1: (lv_email_8_0= RULE_STRING )
                            {
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:279:1: (lv_email_8_0= RULE_STRING )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:280:3: lv_email_8_0= RULE_STRING
                            {
                            lv_email_8_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleUserDef485); 

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

                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:296:4: (otherlv_9= 'isAdmin' ( (lv_admin_10_0= ruleBooleanDef ) ) )?
                    int alt9=2;
                    int LA9_0 = input.LA(1);

                    if ( (LA9_0==19) ) {
                        alt9=1;
                    }
                    switch (alt9) {
                        case 1 :
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:296:6: otherlv_9= 'isAdmin' ( (lv_admin_10_0= ruleBooleanDef ) )
                            {
                            otherlv_9=(Token)match(input,19,FOLLOW_19_in_ruleUserDef505); 

                                	newLeafNode(otherlv_9, grammarAccess.getUserDefAccess().getIsAdminKeyword_2_4_0());
                                
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:300:1: ( (lv_admin_10_0= ruleBooleanDef ) )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:301:1: (lv_admin_10_0= ruleBooleanDef )
                            {
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:301:1: (lv_admin_10_0= ruleBooleanDef )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:302:3: lv_admin_10_0= ruleBooleanDef
                            {
                             
                            	        newCompositeNode(grammarAccess.getUserDefAccess().getAdminBooleanDefEnumRuleCall_2_4_1_0()); 
                            	    
                            pushFollow(FOLLOW_ruleBooleanDef_in_ruleUserDef526);
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

                    otherlv_11=(Token)match(input,20,FOLLOW_20_in_ruleUserDef540); 

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


    // $ANTLR start "entryRuleTeamDef"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:330:1: entryRuleTeamDef returns [EObject current=null] : iv_ruleTeamDef= ruleTeamDef EOF ;
    public final EObject entryRuleTeamDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleTeamDef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:331:2: (iv_ruleTeamDef= ruleTeamDef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:332:2: iv_ruleTeamDef= ruleTeamDef EOF
            {
             newCompositeNode(grammarAccess.getTeamDefRule()); 
            pushFollow(FOLLOW_ruleTeamDef_in_entryRuleTeamDef578);
            iv_ruleTeamDef=ruleTeamDef();

            state._fsp--;

             current =iv_ruleTeamDef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleTeamDef588); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:339:1: ruleTeamDef returns [EObject current=null] : ( ( (lv_name_0_0= RULE_STRING ) ) ( (lv_teamDefOption_1_0= ruleTeamDefOption ) )* otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'usesVersions' ( (lv_usesVersions_6_0= ruleBooleanDef ) ) )? (otherlv_7= 'staticId' ( (lv_staticId_8_0= RULE_STRING ) ) )* (otherlv_9= 'lead' ( (lv_lead_10_0= ruleUserRef ) ) )* (otherlv_11= 'member' ( (lv_member_12_0= ruleUserRef ) ) )* (otherlv_13= 'priviledged' ( (lv_priviledged_14_0= ruleUserRef ) ) )* (otherlv_15= 'workDefinition' ( (lv_workDefinition_16_0= RULE_STRING ) ) )? (otherlv_17= 'version' ( (lv_version_18_0= ruleVersionDef ) ) )* (otherlv_19= 'children' otherlv_20= '{' (otherlv_21= 'teamDefinition' ( (lv_children_22_0= ruleTeamDef ) ) )+ otherlv_23= '}' )? otherlv_24= '}' ) ;
    public final EObject ruleTeamDef() throws RecognitionException {
        EObject current = null;

        Token lv_name_0_0=null;
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
        Token otherlv_19=null;
        Token otherlv_20=null;
        Token otherlv_21=null;
        Token otherlv_23=null;
        Token otherlv_24=null;
        AntlrDatatypeRuleToken lv_teamDefOption_1_0 = null;

        Enumerator lv_active_4_0 = null;

        Enumerator lv_usesVersions_6_0 = null;

        EObject lv_lead_10_0 = null;

        EObject lv_member_12_0 = null;

        EObject lv_priviledged_14_0 = null;

        EObject lv_version_18_0 = null;

        EObject lv_children_22_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:342:28: ( ( ( (lv_name_0_0= RULE_STRING ) ) ( (lv_teamDefOption_1_0= ruleTeamDefOption ) )* otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'usesVersions' ( (lv_usesVersions_6_0= ruleBooleanDef ) ) )? (otherlv_7= 'staticId' ( (lv_staticId_8_0= RULE_STRING ) ) )* (otherlv_9= 'lead' ( (lv_lead_10_0= ruleUserRef ) ) )* (otherlv_11= 'member' ( (lv_member_12_0= ruleUserRef ) ) )* (otherlv_13= 'priviledged' ( (lv_priviledged_14_0= ruleUserRef ) ) )* (otherlv_15= 'workDefinition' ( (lv_workDefinition_16_0= RULE_STRING ) ) )? (otherlv_17= 'version' ( (lv_version_18_0= ruleVersionDef ) ) )* (otherlv_19= 'children' otherlv_20= '{' (otherlv_21= 'teamDefinition' ( (lv_children_22_0= ruleTeamDef ) ) )+ otherlv_23= '}' )? otherlv_24= '}' ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:343:1: ( ( (lv_name_0_0= RULE_STRING ) ) ( (lv_teamDefOption_1_0= ruleTeamDefOption ) )* otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'usesVersions' ( (lv_usesVersions_6_0= ruleBooleanDef ) ) )? (otherlv_7= 'staticId' ( (lv_staticId_8_0= RULE_STRING ) ) )* (otherlv_9= 'lead' ( (lv_lead_10_0= ruleUserRef ) ) )* (otherlv_11= 'member' ( (lv_member_12_0= ruleUserRef ) ) )* (otherlv_13= 'priviledged' ( (lv_priviledged_14_0= ruleUserRef ) ) )* (otherlv_15= 'workDefinition' ( (lv_workDefinition_16_0= RULE_STRING ) ) )? (otherlv_17= 'version' ( (lv_version_18_0= ruleVersionDef ) ) )* (otherlv_19= 'children' otherlv_20= '{' (otherlv_21= 'teamDefinition' ( (lv_children_22_0= ruleTeamDef ) ) )+ otherlv_23= '}' )? otherlv_24= '}' )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:343:1: ( ( (lv_name_0_0= RULE_STRING ) ) ( (lv_teamDefOption_1_0= ruleTeamDefOption ) )* otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'usesVersions' ( (lv_usesVersions_6_0= ruleBooleanDef ) ) )? (otherlv_7= 'staticId' ( (lv_staticId_8_0= RULE_STRING ) ) )* (otherlv_9= 'lead' ( (lv_lead_10_0= ruleUserRef ) ) )* (otherlv_11= 'member' ( (lv_member_12_0= ruleUserRef ) ) )* (otherlv_13= 'priviledged' ( (lv_priviledged_14_0= ruleUserRef ) ) )* (otherlv_15= 'workDefinition' ( (lv_workDefinition_16_0= RULE_STRING ) ) )? (otherlv_17= 'version' ( (lv_version_18_0= ruleVersionDef ) ) )* (otherlv_19= 'children' otherlv_20= '{' (otherlv_21= 'teamDefinition' ( (lv_children_22_0= ruleTeamDef ) ) )+ otherlv_23= '}' )? otherlv_24= '}' )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:343:2: ( (lv_name_0_0= RULE_STRING ) ) ( (lv_teamDefOption_1_0= ruleTeamDefOption ) )* otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'usesVersions' ( (lv_usesVersions_6_0= ruleBooleanDef ) ) )? (otherlv_7= 'staticId' ( (lv_staticId_8_0= RULE_STRING ) ) )* (otherlv_9= 'lead' ( (lv_lead_10_0= ruleUserRef ) ) )* (otherlv_11= 'member' ( (lv_member_12_0= ruleUserRef ) ) )* (otherlv_13= 'priviledged' ( (lv_priviledged_14_0= ruleUserRef ) ) )* (otherlv_15= 'workDefinition' ( (lv_workDefinition_16_0= RULE_STRING ) ) )? (otherlv_17= 'version' ( (lv_version_18_0= ruleVersionDef ) ) )* (otherlv_19= 'children' otherlv_20= '{' (otherlv_21= 'teamDefinition' ( (lv_children_22_0= ruleTeamDef ) ) )+ otherlv_23= '}' )? otherlv_24= '}'
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:343:2: ( (lv_name_0_0= RULE_STRING ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:344:1: (lv_name_0_0= RULE_STRING )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:344:1: (lv_name_0_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:345:3: lv_name_0_0= RULE_STRING
            {
            lv_name_0_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleTeamDef630); 

            			newLeafNode(lv_name_0_0, grammarAccess.getTeamDefAccess().getNameSTRINGTerminalRuleCall_0_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getTeamDefRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"name",
                    		lv_name_0_0, 
                    		"STRING");
            	    

            }


            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:361:2: ( (lv_teamDefOption_1_0= ruleTeamDefOption ) )*
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);

                if ( (LA11_0==RULE_STRING||LA11_0==70) ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:362:1: (lv_teamDefOption_1_0= ruleTeamDefOption )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:362:1: (lv_teamDefOption_1_0= ruleTeamDefOption )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:363:3: lv_teamDefOption_1_0= ruleTeamDefOption
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getTeamDefAccess().getTeamDefOptionTeamDefOptionParserRuleCall_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleTeamDefOption_in_ruleTeamDef656);
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

            otherlv_2=(Token)match(input,15,FOLLOW_15_in_ruleTeamDef669); 

                	newLeafNode(otherlv_2, grammarAccess.getTeamDefAccess().getLeftCurlyBracketKeyword_2());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:383:1: (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==16) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:383:3: otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) )
                    {
                    otherlv_3=(Token)match(input,16,FOLLOW_16_in_ruleTeamDef682); 

                        	newLeafNode(otherlv_3, grammarAccess.getTeamDefAccess().getActiveKeyword_3_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:387:1: ( (lv_active_4_0= ruleBooleanDef ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:388:1: (lv_active_4_0= ruleBooleanDef )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:388:1: (lv_active_4_0= ruleBooleanDef )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:389:3: lv_active_4_0= ruleBooleanDef
                    {
                     
                    	        newCompositeNode(grammarAccess.getTeamDefAccess().getActiveBooleanDefEnumRuleCall_3_1_0()); 
                    	    
                    pushFollow(FOLLOW_ruleBooleanDef_in_ruleTeamDef703);
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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:405:4: (otherlv_5= 'usesVersions' ( (lv_usesVersions_6_0= ruleBooleanDef ) ) )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==21) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:405:6: otherlv_5= 'usesVersions' ( (lv_usesVersions_6_0= ruleBooleanDef ) )
                    {
                    otherlv_5=(Token)match(input,21,FOLLOW_21_in_ruleTeamDef718); 

                        	newLeafNode(otherlv_5, grammarAccess.getTeamDefAccess().getUsesVersionsKeyword_4_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:409:1: ( (lv_usesVersions_6_0= ruleBooleanDef ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:410:1: (lv_usesVersions_6_0= ruleBooleanDef )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:410:1: (lv_usesVersions_6_0= ruleBooleanDef )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:411:3: lv_usesVersions_6_0= ruleBooleanDef
                    {
                     
                    	        newCompositeNode(grammarAccess.getTeamDefAccess().getUsesVersionsBooleanDefEnumRuleCall_4_1_0()); 
                    	    
                    pushFollow(FOLLOW_ruleBooleanDef_in_ruleTeamDef739);
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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:427:4: (otherlv_7= 'staticId' ( (lv_staticId_8_0= RULE_STRING ) ) )*
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);

                if ( (LA14_0==22) ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:427:6: otherlv_7= 'staticId' ( (lv_staticId_8_0= RULE_STRING ) )
            	    {
            	    otherlv_7=(Token)match(input,22,FOLLOW_22_in_ruleTeamDef754); 

            	        	newLeafNode(otherlv_7, grammarAccess.getTeamDefAccess().getStaticIdKeyword_5_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:431:1: ( (lv_staticId_8_0= RULE_STRING ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:432:1: (lv_staticId_8_0= RULE_STRING )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:432:1: (lv_staticId_8_0= RULE_STRING )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:433:3: lv_staticId_8_0= RULE_STRING
            	    {
            	    lv_staticId_8_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleTeamDef771); 

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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:449:4: (otherlv_9= 'lead' ( (lv_lead_10_0= ruleUserRef ) ) )*
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( (LA15_0==23) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:449:6: otherlv_9= 'lead' ( (lv_lead_10_0= ruleUserRef ) )
            	    {
            	    otherlv_9=(Token)match(input,23,FOLLOW_23_in_ruleTeamDef791); 

            	        	newLeafNode(otherlv_9, grammarAccess.getTeamDefAccess().getLeadKeyword_6_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:453:1: ( (lv_lead_10_0= ruleUserRef ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:454:1: (lv_lead_10_0= ruleUserRef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:454:1: (lv_lead_10_0= ruleUserRef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:455:3: lv_lead_10_0= ruleUserRef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getTeamDefAccess().getLeadUserRefParserRuleCall_6_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleUserRef_in_ruleTeamDef812);
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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:471:4: (otherlv_11= 'member' ( (lv_member_12_0= ruleUserRef ) ) )*
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( (LA16_0==24) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:471:6: otherlv_11= 'member' ( (lv_member_12_0= ruleUserRef ) )
            	    {
            	    otherlv_11=(Token)match(input,24,FOLLOW_24_in_ruleTeamDef827); 

            	        	newLeafNode(otherlv_11, grammarAccess.getTeamDefAccess().getMemberKeyword_7_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:475:1: ( (lv_member_12_0= ruleUserRef ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:476:1: (lv_member_12_0= ruleUserRef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:476:1: (lv_member_12_0= ruleUserRef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:477:3: lv_member_12_0= ruleUserRef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getTeamDefAccess().getMemberUserRefParserRuleCall_7_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleUserRef_in_ruleTeamDef848);
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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:493:4: (otherlv_13= 'priviledged' ( (lv_priviledged_14_0= ruleUserRef ) ) )*
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);

                if ( (LA17_0==25) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:493:6: otherlv_13= 'priviledged' ( (lv_priviledged_14_0= ruleUserRef ) )
            	    {
            	    otherlv_13=(Token)match(input,25,FOLLOW_25_in_ruleTeamDef863); 

            	        	newLeafNode(otherlv_13, grammarAccess.getTeamDefAccess().getPriviledgedKeyword_8_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:497:1: ( (lv_priviledged_14_0= ruleUserRef ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:498:1: (lv_priviledged_14_0= ruleUserRef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:498:1: (lv_priviledged_14_0= ruleUserRef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:499:3: lv_priviledged_14_0= ruleUserRef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getTeamDefAccess().getPriviledgedUserRefParserRuleCall_8_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleUserRef_in_ruleTeamDef884);
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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:515:4: (otherlv_15= 'workDefinition' ( (lv_workDefinition_16_0= RULE_STRING ) ) )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==11) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:515:6: otherlv_15= 'workDefinition' ( (lv_workDefinition_16_0= RULE_STRING ) )
                    {
                    otherlv_15=(Token)match(input,11,FOLLOW_11_in_ruleTeamDef899); 

                        	newLeafNode(otherlv_15, grammarAccess.getTeamDefAccess().getWorkDefinitionKeyword_9_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:519:1: ( (lv_workDefinition_16_0= RULE_STRING ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:520:1: (lv_workDefinition_16_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:520:1: (lv_workDefinition_16_0= RULE_STRING )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:521:3: lv_workDefinition_16_0= RULE_STRING
                    {
                    lv_workDefinition_16_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleTeamDef916); 

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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:537:4: (otherlv_17= 'version' ( (lv_version_18_0= ruleVersionDef ) ) )*
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);

                if ( (LA19_0==26) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:537:6: otherlv_17= 'version' ( (lv_version_18_0= ruleVersionDef ) )
            	    {
            	    otherlv_17=(Token)match(input,26,FOLLOW_26_in_ruleTeamDef936); 

            	        	newLeafNode(otherlv_17, grammarAccess.getTeamDefAccess().getVersionKeyword_10_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:541:1: ( (lv_version_18_0= ruleVersionDef ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:542:1: (lv_version_18_0= ruleVersionDef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:542:1: (lv_version_18_0= ruleVersionDef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:543:3: lv_version_18_0= ruleVersionDef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getTeamDefAccess().getVersionVersionDefParserRuleCall_10_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleVersionDef_in_ruleTeamDef957);
            	    lv_version_18_0=ruleVersionDef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getTeamDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"version",
            	            		lv_version_18_0, 
            	            		"VersionDef");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop19;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:559:4: (otherlv_19= 'children' otherlv_20= '{' (otherlv_21= 'teamDefinition' ( (lv_children_22_0= ruleTeamDef ) ) )+ otherlv_23= '}' )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==27) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:559:6: otherlv_19= 'children' otherlv_20= '{' (otherlv_21= 'teamDefinition' ( (lv_children_22_0= ruleTeamDef ) ) )+ otherlv_23= '}'
                    {
                    otherlv_19=(Token)match(input,27,FOLLOW_27_in_ruleTeamDef972); 

                        	newLeafNode(otherlv_19, grammarAccess.getTeamDefAccess().getChildrenKeyword_11_0());
                        
                    otherlv_20=(Token)match(input,15,FOLLOW_15_in_ruleTeamDef984); 

                        	newLeafNode(otherlv_20, grammarAccess.getTeamDefAccess().getLeftCurlyBracketKeyword_11_1());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:567:1: (otherlv_21= 'teamDefinition' ( (lv_children_22_0= ruleTeamDef ) ) )+
                    int cnt20=0;
                    loop20:
                    do {
                        int alt20=2;
                        int LA20_0 = input.LA(1);

                        if ( (LA20_0==13) ) {
                            alt20=1;
                        }


                        switch (alt20) {
                    	case 1 :
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:567:3: otherlv_21= 'teamDefinition' ( (lv_children_22_0= ruleTeamDef ) )
                    	    {
                    	    otherlv_21=(Token)match(input,13,FOLLOW_13_in_ruleTeamDef997); 

                    	        	newLeafNode(otherlv_21, grammarAccess.getTeamDefAccess().getTeamDefinitionKeyword_11_2_0());
                    	        
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:571:1: ( (lv_children_22_0= ruleTeamDef ) )
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:572:1: (lv_children_22_0= ruleTeamDef )
                    	    {
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:572:1: (lv_children_22_0= ruleTeamDef )
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:573:3: lv_children_22_0= ruleTeamDef
                    	    {
                    	     
                    	    	        newCompositeNode(grammarAccess.getTeamDefAccess().getChildrenTeamDefParserRuleCall_11_2_1_0()); 
                    	    	    
                    	    pushFollow(FOLLOW_ruleTeamDef_in_ruleTeamDef1018);
                    	    lv_children_22_0=ruleTeamDef();

                    	    state._fsp--;


                    	    	        if (current==null) {
                    	    	            current = createModelElementForParent(grammarAccess.getTeamDefRule());
                    	    	        }
                    	           		add(
                    	           			current, 
                    	           			"children",
                    	            		lv_children_22_0, 
                    	            		"TeamDef");
                    	    	        afterParserOrEnumRuleCall();
                    	    	    

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt20 >= 1 ) break loop20;
                                EarlyExitException eee =
                                    new EarlyExitException(20, input);
                                throw eee;
                        }
                        cnt20++;
                    } while (true);

                    otherlv_23=(Token)match(input,20,FOLLOW_20_in_ruleTeamDef1032); 

                        	newLeafNode(otherlv_23, grammarAccess.getTeamDefAccess().getRightCurlyBracketKeyword_11_3());
                        

                    }
                    break;

            }

            otherlv_24=(Token)match(input,20,FOLLOW_20_in_ruleTeamDef1046); 

                	newLeafNode(otherlv_24, grammarAccess.getTeamDefAccess().getRightCurlyBracketKeyword_12());
                

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


    // $ANTLR start "entryRuleActionableItemDef"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:605:1: entryRuleActionableItemDef returns [EObject current=null] : iv_ruleActionableItemDef= ruleActionableItemDef EOF ;
    public final EObject entryRuleActionableItemDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleActionableItemDef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:606:2: (iv_ruleActionableItemDef= ruleActionableItemDef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:607:2: iv_ruleActionableItemDef= ruleActionableItemDef EOF
            {
             newCompositeNode(grammarAccess.getActionableItemDefRule()); 
            pushFollow(FOLLOW_ruleActionableItemDef_in_entryRuleActionableItemDef1082);
            iv_ruleActionableItemDef=ruleActionableItemDef();

            state._fsp--;

             current =iv_ruleActionableItemDef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleActionableItemDef1092); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:614:1: ruleActionableItemDef returns [EObject current=null] : ( ( (lv_name_0_0= RULE_STRING ) ) ( (lv_aiDefOption_1_0= ruleActionableItemOption ) )* (otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'actionable' ( (lv_actionable_6_0= ruleBooleanDef ) ) )? (otherlv_7= 'lead' ( (lv_lead_8_0= ruleUserRef ) ) )* (otherlv_9= 'staticId' ( (lv_staticId_10_0= RULE_STRING ) ) )* (otherlv_11= 'team' ( (lv_teamDef_12_0= RULE_STRING ) ) )? (otherlv_13= 'children' otherlv_14= '{' (otherlv_15= 'actionableItem' ( (lv_children_16_0= ruleActionableItemDef ) ) )+ otherlv_17= '}' )? otherlv_18= '}' )? ) ;
    public final EObject ruleActionableItemDef() throws RecognitionException {
        EObject current = null;

        Token lv_name_0_0=null;
        Token otherlv_2=null;
        Token otherlv_3=null;
        Token otherlv_5=null;
        Token otherlv_7=null;
        Token otherlv_9=null;
        Token lv_staticId_10_0=null;
        Token otherlv_11=null;
        Token lv_teamDef_12_0=null;
        Token otherlv_13=null;
        Token otherlv_14=null;
        Token otherlv_15=null;
        Token otherlv_17=null;
        Token otherlv_18=null;
        AntlrDatatypeRuleToken lv_aiDefOption_1_0 = null;

        Enumerator lv_active_4_0 = null;

        Enumerator lv_actionable_6_0 = null;

        EObject lv_lead_8_0 = null;

        EObject lv_children_16_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:617:28: ( ( ( (lv_name_0_0= RULE_STRING ) ) ( (lv_aiDefOption_1_0= ruleActionableItemOption ) )* (otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'actionable' ( (lv_actionable_6_0= ruleBooleanDef ) ) )? (otherlv_7= 'lead' ( (lv_lead_8_0= ruleUserRef ) ) )* (otherlv_9= 'staticId' ( (lv_staticId_10_0= RULE_STRING ) ) )* (otherlv_11= 'team' ( (lv_teamDef_12_0= RULE_STRING ) ) )? (otherlv_13= 'children' otherlv_14= '{' (otherlv_15= 'actionableItem' ( (lv_children_16_0= ruleActionableItemDef ) ) )+ otherlv_17= '}' )? otherlv_18= '}' )? ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:618:1: ( ( (lv_name_0_0= RULE_STRING ) ) ( (lv_aiDefOption_1_0= ruleActionableItemOption ) )* (otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'actionable' ( (lv_actionable_6_0= ruleBooleanDef ) ) )? (otherlv_7= 'lead' ( (lv_lead_8_0= ruleUserRef ) ) )* (otherlv_9= 'staticId' ( (lv_staticId_10_0= RULE_STRING ) ) )* (otherlv_11= 'team' ( (lv_teamDef_12_0= RULE_STRING ) ) )? (otherlv_13= 'children' otherlv_14= '{' (otherlv_15= 'actionableItem' ( (lv_children_16_0= ruleActionableItemDef ) ) )+ otherlv_17= '}' )? otherlv_18= '}' )? )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:618:1: ( ( (lv_name_0_0= RULE_STRING ) ) ( (lv_aiDefOption_1_0= ruleActionableItemOption ) )* (otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'actionable' ( (lv_actionable_6_0= ruleBooleanDef ) ) )? (otherlv_7= 'lead' ( (lv_lead_8_0= ruleUserRef ) ) )* (otherlv_9= 'staticId' ( (lv_staticId_10_0= RULE_STRING ) ) )* (otherlv_11= 'team' ( (lv_teamDef_12_0= RULE_STRING ) ) )? (otherlv_13= 'children' otherlv_14= '{' (otherlv_15= 'actionableItem' ( (lv_children_16_0= ruleActionableItemDef ) ) )+ otherlv_17= '}' )? otherlv_18= '}' )? )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:618:2: ( (lv_name_0_0= RULE_STRING ) ) ( (lv_aiDefOption_1_0= ruleActionableItemOption ) )* (otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'actionable' ( (lv_actionable_6_0= ruleBooleanDef ) ) )? (otherlv_7= 'lead' ( (lv_lead_8_0= ruleUserRef ) ) )* (otherlv_9= 'staticId' ( (lv_staticId_10_0= RULE_STRING ) ) )* (otherlv_11= 'team' ( (lv_teamDef_12_0= RULE_STRING ) ) )? (otherlv_13= 'children' otherlv_14= '{' (otherlv_15= 'actionableItem' ( (lv_children_16_0= ruleActionableItemDef ) ) )+ otherlv_17= '}' )? otherlv_18= '}' )?
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:618:2: ( (lv_name_0_0= RULE_STRING ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:619:1: (lv_name_0_0= RULE_STRING )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:619:1: (lv_name_0_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:620:3: lv_name_0_0= RULE_STRING
            {
            lv_name_0_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleActionableItemDef1134); 

            			newLeafNode(lv_name_0_0, grammarAccess.getActionableItemDefAccess().getNameSTRINGTerminalRuleCall_0_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getActionableItemDefRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"name",
                    		lv_name_0_0, 
                    		"STRING");
            	    

            }


            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:636:2: ( (lv_aiDefOption_1_0= ruleActionableItemOption ) )*
            loop22:
            do {
                int alt22=2;
                int LA22_0 = input.LA(1);

                if ( (LA22_0==RULE_STRING||LA22_0==70) ) {
                    alt22=1;
                }


                switch (alt22) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:637:1: (lv_aiDefOption_1_0= ruleActionableItemOption )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:637:1: (lv_aiDefOption_1_0= ruleActionableItemOption )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:638:3: lv_aiDefOption_1_0= ruleActionableItemOption
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getActionableItemDefAccess().getAiDefOptionActionableItemOptionParserRuleCall_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleActionableItemOption_in_ruleActionableItemDef1160);
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
            	    break loop22;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:654:3: (otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'actionable' ( (lv_actionable_6_0= ruleBooleanDef ) ) )? (otherlv_7= 'lead' ( (lv_lead_8_0= ruleUserRef ) ) )* (otherlv_9= 'staticId' ( (lv_staticId_10_0= RULE_STRING ) ) )* (otherlv_11= 'team' ( (lv_teamDef_12_0= RULE_STRING ) ) )? (otherlv_13= 'children' otherlv_14= '{' (otherlv_15= 'actionableItem' ( (lv_children_16_0= ruleActionableItemDef ) ) )+ otherlv_17= '}' )? otherlv_18= '}' )?
            int alt30=2;
            int LA30_0 = input.LA(1);

            if ( (LA30_0==15) ) {
                alt30=1;
            }
            switch (alt30) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:654:5: otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'actionable' ( (lv_actionable_6_0= ruleBooleanDef ) ) )? (otherlv_7= 'lead' ( (lv_lead_8_0= ruleUserRef ) ) )* (otherlv_9= 'staticId' ( (lv_staticId_10_0= RULE_STRING ) ) )* (otherlv_11= 'team' ( (lv_teamDef_12_0= RULE_STRING ) ) )? (otherlv_13= 'children' otherlv_14= '{' (otherlv_15= 'actionableItem' ( (lv_children_16_0= ruleActionableItemDef ) ) )+ otherlv_17= '}' )? otherlv_18= '}'
                    {
                    otherlv_2=(Token)match(input,15,FOLLOW_15_in_ruleActionableItemDef1174); 

                        	newLeafNode(otherlv_2, grammarAccess.getActionableItemDefAccess().getLeftCurlyBracketKeyword_2_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:658:1: (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )?
                    int alt23=2;
                    int LA23_0 = input.LA(1);

                    if ( (LA23_0==16) ) {
                        alt23=1;
                    }
                    switch (alt23) {
                        case 1 :
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:658:3: otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) )
                            {
                            otherlv_3=(Token)match(input,16,FOLLOW_16_in_ruleActionableItemDef1187); 

                                	newLeafNode(otherlv_3, grammarAccess.getActionableItemDefAccess().getActiveKeyword_2_1_0());
                                
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:662:1: ( (lv_active_4_0= ruleBooleanDef ) )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:663:1: (lv_active_4_0= ruleBooleanDef )
                            {
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:663:1: (lv_active_4_0= ruleBooleanDef )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:664:3: lv_active_4_0= ruleBooleanDef
                            {
                             
                            	        newCompositeNode(grammarAccess.getActionableItemDefAccess().getActiveBooleanDefEnumRuleCall_2_1_1_0()); 
                            	    
                            pushFollow(FOLLOW_ruleBooleanDef_in_ruleActionableItemDef1208);
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

                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:680:4: (otherlv_5= 'actionable' ( (lv_actionable_6_0= ruleBooleanDef ) ) )?
                    int alt24=2;
                    int LA24_0 = input.LA(1);

                    if ( (LA24_0==28) ) {
                        alt24=1;
                    }
                    switch (alt24) {
                        case 1 :
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:680:6: otherlv_5= 'actionable' ( (lv_actionable_6_0= ruleBooleanDef ) )
                            {
                            otherlv_5=(Token)match(input,28,FOLLOW_28_in_ruleActionableItemDef1223); 

                                	newLeafNode(otherlv_5, grammarAccess.getActionableItemDefAccess().getActionableKeyword_2_2_0());
                                
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:684:1: ( (lv_actionable_6_0= ruleBooleanDef ) )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:685:1: (lv_actionable_6_0= ruleBooleanDef )
                            {
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:685:1: (lv_actionable_6_0= ruleBooleanDef )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:686:3: lv_actionable_6_0= ruleBooleanDef
                            {
                             
                            	        newCompositeNode(grammarAccess.getActionableItemDefAccess().getActionableBooleanDefEnumRuleCall_2_2_1_0()); 
                            	    
                            pushFollow(FOLLOW_ruleBooleanDef_in_ruleActionableItemDef1244);
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

                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:702:4: (otherlv_7= 'lead' ( (lv_lead_8_0= ruleUserRef ) ) )*
                    loop25:
                    do {
                        int alt25=2;
                        int LA25_0 = input.LA(1);

                        if ( (LA25_0==23) ) {
                            alt25=1;
                        }


                        switch (alt25) {
                    	case 1 :
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:702:6: otherlv_7= 'lead' ( (lv_lead_8_0= ruleUserRef ) )
                    	    {
                    	    otherlv_7=(Token)match(input,23,FOLLOW_23_in_ruleActionableItemDef1259); 

                    	        	newLeafNode(otherlv_7, grammarAccess.getActionableItemDefAccess().getLeadKeyword_2_3_0());
                    	        
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:706:1: ( (lv_lead_8_0= ruleUserRef ) )
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:707:1: (lv_lead_8_0= ruleUserRef )
                    	    {
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:707:1: (lv_lead_8_0= ruleUserRef )
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:708:3: lv_lead_8_0= ruleUserRef
                    	    {
                    	     
                    	    	        newCompositeNode(grammarAccess.getActionableItemDefAccess().getLeadUserRefParserRuleCall_2_3_1_0()); 
                    	    	    
                    	    pushFollow(FOLLOW_ruleUserRef_in_ruleActionableItemDef1280);
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
                    	    break loop25;
                        }
                    } while (true);

                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:724:4: (otherlv_9= 'staticId' ( (lv_staticId_10_0= RULE_STRING ) ) )*
                    loop26:
                    do {
                        int alt26=2;
                        int LA26_0 = input.LA(1);

                        if ( (LA26_0==22) ) {
                            alt26=1;
                        }


                        switch (alt26) {
                    	case 1 :
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:724:6: otherlv_9= 'staticId' ( (lv_staticId_10_0= RULE_STRING ) )
                    	    {
                    	    otherlv_9=(Token)match(input,22,FOLLOW_22_in_ruleActionableItemDef1295); 

                    	        	newLeafNode(otherlv_9, grammarAccess.getActionableItemDefAccess().getStaticIdKeyword_2_4_0());
                    	        
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:728:1: ( (lv_staticId_10_0= RULE_STRING ) )
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:729:1: (lv_staticId_10_0= RULE_STRING )
                    	    {
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:729:1: (lv_staticId_10_0= RULE_STRING )
                    	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:730:3: lv_staticId_10_0= RULE_STRING
                    	    {
                    	    lv_staticId_10_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleActionableItemDef1312); 

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
                    	    break loop26;
                        }
                    } while (true);

                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:746:4: (otherlv_11= 'team' ( (lv_teamDef_12_0= RULE_STRING ) ) )?
                    int alt27=2;
                    int LA27_0 = input.LA(1);

                    if ( (LA27_0==29) ) {
                        alt27=1;
                    }
                    switch (alt27) {
                        case 1 :
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:746:6: otherlv_11= 'team' ( (lv_teamDef_12_0= RULE_STRING ) )
                            {
                            otherlv_11=(Token)match(input,29,FOLLOW_29_in_ruleActionableItemDef1332); 

                                	newLeafNode(otherlv_11, grammarAccess.getActionableItemDefAccess().getTeamKeyword_2_5_0());
                                
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:750:1: ( (lv_teamDef_12_0= RULE_STRING ) )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:751:1: (lv_teamDef_12_0= RULE_STRING )
                            {
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:751:1: (lv_teamDef_12_0= RULE_STRING )
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:752:3: lv_teamDef_12_0= RULE_STRING
                            {
                            lv_teamDef_12_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleActionableItemDef1349); 

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

                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:768:4: (otherlv_13= 'children' otherlv_14= '{' (otherlv_15= 'actionableItem' ( (lv_children_16_0= ruleActionableItemDef ) ) )+ otherlv_17= '}' )?
                    int alt29=2;
                    int LA29_0 = input.LA(1);

                    if ( (LA29_0==27) ) {
                        alt29=1;
                    }
                    switch (alt29) {
                        case 1 :
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:768:6: otherlv_13= 'children' otherlv_14= '{' (otherlv_15= 'actionableItem' ( (lv_children_16_0= ruleActionableItemDef ) ) )+ otherlv_17= '}'
                            {
                            otherlv_13=(Token)match(input,27,FOLLOW_27_in_ruleActionableItemDef1369); 

                                	newLeafNode(otherlv_13, grammarAccess.getActionableItemDefAccess().getChildrenKeyword_2_6_0());
                                
                            otherlv_14=(Token)match(input,15,FOLLOW_15_in_ruleActionableItemDef1381); 

                                	newLeafNode(otherlv_14, grammarAccess.getActionableItemDefAccess().getLeftCurlyBracketKeyword_2_6_1());
                                
                            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:776:1: (otherlv_15= 'actionableItem' ( (lv_children_16_0= ruleActionableItemDef ) ) )+
                            int cnt28=0;
                            loop28:
                            do {
                                int alt28=2;
                                int LA28_0 = input.LA(1);

                                if ( (LA28_0==14) ) {
                                    alt28=1;
                                }


                                switch (alt28) {
                            	case 1 :
                            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:776:3: otherlv_15= 'actionableItem' ( (lv_children_16_0= ruleActionableItemDef ) )
                            	    {
                            	    otherlv_15=(Token)match(input,14,FOLLOW_14_in_ruleActionableItemDef1394); 

                            	        	newLeafNode(otherlv_15, grammarAccess.getActionableItemDefAccess().getActionableItemKeyword_2_6_2_0());
                            	        
                            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:780:1: ( (lv_children_16_0= ruleActionableItemDef ) )
                            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:781:1: (lv_children_16_0= ruleActionableItemDef )
                            	    {
                            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:781:1: (lv_children_16_0= ruleActionableItemDef )
                            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:782:3: lv_children_16_0= ruleActionableItemDef
                            	    {
                            	     
                            	    	        newCompositeNode(grammarAccess.getActionableItemDefAccess().getChildrenActionableItemDefParserRuleCall_2_6_2_1_0()); 
                            	    	    
                            	    pushFollow(FOLLOW_ruleActionableItemDef_in_ruleActionableItemDef1415);
                            	    lv_children_16_0=ruleActionableItemDef();

                            	    state._fsp--;


                            	    	        if (current==null) {
                            	    	            current = createModelElementForParent(grammarAccess.getActionableItemDefRule());
                            	    	        }
                            	           		add(
                            	           			current, 
                            	           			"children",
                            	            		lv_children_16_0, 
                            	            		"ActionableItemDef");
                            	    	        afterParserOrEnumRuleCall();
                            	    	    

                            	    }


                            	    }


                            	    }
                            	    break;

                            	default :
                            	    if ( cnt28 >= 1 ) break loop28;
                                        EarlyExitException eee =
                                            new EarlyExitException(28, input);
                                        throw eee;
                                }
                                cnt28++;
                            } while (true);

                            otherlv_17=(Token)match(input,20,FOLLOW_20_in_ruleActionableItemDef1429); 

                                	newLeafNode(otherlv_17, grammarAccess.getActionableItemDefAccess().getRightCurlyBracketKeyword_2_6_3());
                                

                            }
                            break;

                    }

                    otherlv_18=(Token)match(input,20,FOLLOW_20_in_ruleActionableItemDef1443); 

                        	newLeafNode(otherlv_18, grammarAccess.getActionableItemDefAccess().getRightCurlyBracketKeyword_2_7());
                        

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


    // $ANTLR start "entryRuleVersionDef"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:814:1: entryRuleVersionDef returns [EObject current=null] : iv_ruleVersionDef= ruleVersionDef EOF ;
    public final EObject entryRuleVersionDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleVersionDef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:815:2: (iv_ruleVersionDef= ruleVersionDef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:816:2: iv_ruleVersionDef= ruleVersionDef EOF
            {
             newCompositeNode(grammarAccess.getVersionDefRule()); 
            pushFollow(FOLLOW_ruleVersionDef_in_entryRuleVersionDef1481);
            iv_ruleVersionDef=ruleVersionDef();

            state._fsp--;

             current =iv_ruleVersionDef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleVersionDef1491); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:823:1: ruleVersionDef returns [EObject current=null] : ( ( (lv_name_0_0= RULE_STRING ) ) otherlv_1= '{' (otherlv_2= 'active' ( (lv_active_3_0= ruleBooleanDef ) ) )? (otherlv_4= 'staticId' ( (lv_staticId_5_0= RULE_STRING ) ) )* (otherlv_6= 'next' ( (lv_next_7_0= ruleBooleanDef ) ) )? (otherlv_8= 'released' ( (lv_released_9_0= ruleBooleanDef ) ) )? (otherlv_10= 'allowCreateBranch' ( (lv_allowCreateBranch_11_0= ruleBooleanDef ) ) )? (otherlv_12= 'allowCommitBranch' ( (lv_allowCommitBranch_13_0= ruleBooleanDef ) ) )? (otherlv_14= 'baslineBranchGuid' ( (lv_baselineBranchGuid_15_0= RULE_STRING ) ) )? otherlv_16= '}' ) ;
    public final EObject ruleVersionDef() throws RecognitionException {
        EObject current = null;

        Token lv_name_0_0=null;
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
        Enumerator lv_active_3_0 = null;

        Enumerator lv_next_7_0 = null;

        Enumerator lv_released_9_0 = null;

        Enumerator lv_allowCreateBranch_11_0 = null;

        Enumerator lv_allowCommitBranch_13_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:826:28: ( ( ( (lv_name_0_0= RULE_STRING ) ) otherlv_1= '{' (otherlv_2= 'active' ( (lv_active_3_0= ruleBooleanDef ) ) )? (otherlv_4= 'staticId' ( (lv_staticId_5_0= RULE_STRING ) ) )* (otherlv_6= 'next' ( (lv_next_7_0= ruleBooleanDef ) ) )? (otherlv_8= 'released' ( (lv_released_9_0= ruleBooleanDef ) ) )? (otherlv_10= 'allowCreateBranch' ( (lv_allowCreateBranch_11_0= ruleBooleanDef ) ) )? (otherlv_12= 'allowCommitBranch' ( (lv_allowCommitBranch_13_0= ruleBooleanDef ) ) )? (otherlv_14= 'baslineBranchGuid' ( (lv_baselineBranchGuid_15_0= RULE_STRING ) ) )? otherlv_16= '}' ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:827:1: ( ( (lv_name_0_0= RULE_STRING ) ) otherlv_1= '{' (otherlv_2= 'active' ( (lv_active_3_0= ruleBooleanDef ) ) )? (otherlv_4= 'staticId' ( (lv_staticId_5_0= RULE_STRING ) ) )* (otherlv_6= 'next' ( (lv_next_7_0= ruleBooleanDef ) ) )? (otherlv_8= 'released' ( (lv_released_9_0= ruleBooleanDef ) ) )? (otherlv_10= 'allowCreateBranch' ( (lv_allowCreateBranch_11_0= ruleBooleanDef ) ) )? (otherlv_12= 'allowCommitBranch' ( (lv_allowCommitBranch_13_0= ruleBooleanDef ) ) )? (otherlv_14= 'baslineBranchGuid' ( (lv_baselineBranchGuid_15_0= RULE_STRING ) ) )? otherlv_16= '}' )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:827:1: ( ( (lv_name_0_0= RULE_STRING ) ) otherlv_1= '{' (otherlv_2= 'active' ( (lv_active_3_0= ruleBooleanDef ) ) )? (otherlv_4= 'staticId' ( (lv_staticId_5_0= RULE_STRING ) ) )* (otherlv_6= 'next' ( (lv_next_7_0= ruleBooleanDef ) ) )? (otherlv_8= 'released' ( (lv_released_9_0= ruleBooleanDef ) ) )? (otherlv_10= 'allowCreateBranch' ( (lv_allowCreateBranch_11_0= ruleBooleanDef ) ) )? (otherlv_12= 'allowCommitBranch' ( (lv_allowCommitBranch_13_0= ruleBooleanDef ) ) )? (otherlv_14= 'baslineBranchGuid' ( (lv_baselineBranchGuid_15_0= RULE_STRING ) ) )? otherlv_16= '}' )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:827:2: ( (lv_name_0_0= RULE_STRING ) ) otherlv_1= '{' (otherlv_2= 'active' ( (lv_active_3_0= ruleBooleanDef ) ) )? (otherlv_4= 'staticId' ( (lv_staticId_5_0= RULE_STRING ) ) )* (otherlv_6= 'next' ( (lv_next_7_0= ruleBooleanDef ) ) )? (otherlv_8= 'released' ( (lv_released_9_0= ruleBooleanDef ) ) )? (otherlv_10= 'allowCreateBranch' ( (lv_allowCreateBranch_11_0= ruleBooleanDef ) ) )? (otherlv_12= 'allowCommitBranch' ( (lv_allowCommitBranch_13_0= ruleBooleanDef ) ) )? (otherlv_14= 'baslineBranchGuid' ( (lv_baselineBranchGuid_15_0= RULE_STRING ) ) )? otherlv_16= '}'
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:827:2: ( (lv_name_0_0= RULE_STRING ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:828:1: (lv_name_0_0= RULE_STRING )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:828:1: (lv_name_0_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:829:3: lv_name_0_0= RULE_STRING
            {
            lv_name_0_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleVersionDef1533); 

            			newLeafNode(lv_name_0_0, grammarAccess.getVersionDefAccess().getNameSTRINGTerminalRuleCall_0_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getVersionDefRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"name",
                    		lv_name_0_0, 
                    		"STRING");
            	    

            }


            }

            otherlv_1=(Token)match(input,15,FOLLOW_15_in_ruleVersionDef1550); 

                	newLeafNode(otherlv_1, grammarAccess.getVersionDefAccess().getLeftCurlyBracketKeyword_1());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:849:1: (otherlv_2= 'active' ( (lv_active_3_0= ruleBooleanDef ) ) )?
            int alt31=2;
            int LA31_0 = input.LA(1);

            if ( (LA31_0==16) ) {
                alt31=1;
            }
            switch (alt31) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:849:3: otherlv_2= 'active' ( (lv_active_3_0= ruleBooleanDef ) )
                    {
                    otherlv_2=(Token)match(input,16,FOLLOW_16_in_ruleVersionDef1563); 

                        	newLeafNode(otherlv_2, grammarAccess.getVersionDefAccess().getActiveKeyword_2_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:853:1: ( (lv_active_3_0= ruleBooleanDef ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:854:1: (lv_active_3_0= ruleBooleanDef )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:854:1: (lv_active_3_0= ruleBooleanDef )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:855:3: lv_active_3_0= ruleBooleanDef
                    {
                     
                    	        newCompositeNode(grammarAccess.getVersionDefAccess().getActiveBooleanDefEnumRuleCall_2_1_0()); 
                    	    
                    pushFollow(FOLLOW_ruleBooleanDef_in_ruleVersionDef1584);
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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:871:4: (otherlv_4= 'staticId' ( (lv_staticId_5_0= RULE_STRING ) ) )*
            loop32:
            do {
                int alt32=2;
                int LA32_0 = input.LA(1);

                if ( (LA32_0==22) ) {
                    alt32=1;
                }


                switch (alt32) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:871:6: otherlv_4= 'staticId' ( (lv_staticId_5_0= RULE_STRING ) )
            	    {
            	    otherlv_4=(Token)match(input,22,FOLLOW_22_in_ruleVersionDef1599); 

            	        	newLeafNode(otherlv_4, grammarAccess.getVersionDefAccess().getStaticIdKeyword_3_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:875:1: ( (lv_staticId_5_0= RULE_STRING ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:876:1: (lv_staticId_5_0= RULE_STRING )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:876:1: (lv_staticId_5_0= RULE_STRING )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:877:3: lv_staticId_5_0= RULE_STRING
            	    {
            	    lv_staticId_5_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleVersionDef1616); 

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
            	    break loop32;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:893:4: (otherlv_6= 'next' ( (lv_next_7_0= ruleBooleanDef ) ) )?
            int alt33=2;
            int LA33_0 = input.LA(1);

            if ( (LA33_0==30) ) {
                alt33=1;
            }
            switch (alt33) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:893:6: otherlv_6= 'next' ( (lv_next_7_0= ruleBooleanDef ) )
                    {
                    otherlv_6=(Token)match(input,30,FOLLOW_30_in_ruleVersionDef1636); 

                        	newLeafNode(otherlv_6, grammarAccess.getVersionDefAccess().getNextKeyword_4_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:897:1: ( (lv_next_7_0= ruleBooleanDef ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:898:1: (lv_next_7_0= ruleBooleanDef )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:898:1: (lv_next_7_0= ruleBooleanDef )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:899:3: lv_next_7_0= ruleBooleanDef
                    {
                     
                    	        newCompositeNode(grammarAccess.getVersionDefAccess().getNextBooleanDefEnumRuleCall_4_1_0()); 
                    	    
                    pushFollow(FOLLOW_ruleBooleanDef_in_ruleVersionDef1657);
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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:915:4: (otherlv_8= 'released' ( (lv_released_9_0= ruleBooleanDef ) ) )?
            int alt34=2;
            int LA34_0 = input.LA(1);

            if ( (LA34_0==31) ) {
                alt34=1;
            }
            switch (alt34) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:915:6: otherlv_8= 'released' ( (lv_released_9_0= ruleBooleanDef ) )
                    {
                    otherlv_8=(Token)match(input,31,FOLLOW_31_in_ruleVersionDef1672); 

                        	newLeafNode(otherlv_8, grammarAccess.getVersionDefAccess().getReleasedKeyword_5_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:919:1: ( (lv_released_9_0= ruleBooleanDef ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:920:1: (lv_released_9_0= ruleBooleanDef )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:920:1: (lv_released_9_0= ruleBooleanDef )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:921:3: lv_released_9_0= ruleBooleanDef
                    {
                     
                    	        newCompositeNode(grammarAccess.getVersionDefAccess().getReleasedBooleanDefEnumRuleCall_5_1_0()); 
                    	    
                    pushFollow(FOLLOW_ruleBooleanDef_in_ruleVersionDef1693);
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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:937:4: (otherlv_10= 'allowCreateBranch' ( (lv_allowCreateBranch_11_0= ruleBooleanDef ) ) )?
            int alt35=2;
            int LA35_0 = input.LA(1);

            if ( (LA35_0==32) ) {
                alt35=1;
            }
            switch (alt35) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:937:6: otherlv_10= 'allowCreateBranch' ( (lv_allowCreateBranch_11_0= ruleBooleanDef ) )
                    {
                    otherlv_10=(Token)match(input,32,FOLLOW_32_in_ruleVersionDef1708); 

                        	newLeafNode(otherlv_10, grammarAccess.getVersionDefAccess().getAllowCreateBranchKeyword_6_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:941:1: ( (lv_allowCreateBranch_11_0= ruleBooleanDef ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:942:1: (lv_allowCreateBranch_11_0= ruleBooleanDef )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:942:1: (lv_allowCreateBranch_11_0= ruleBooleanDef )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:943:3: lv_allowCreateBranch_11_0= ruleBooleanDef
                    {
                     
                    	        newCompositeNode(grammarAccess.getVersionDefAccess().getAllowCreateBranchBooleanDefEnumRuleCall_6_1_0()); 
                    	    
                    pushFollow(FOLLOW_ruleBooleanDef_in_ruleVersionDef1729);
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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:959:4: (otherlv_12= 'allowCommitBranch' ( (lv_allowCommitBranch_13_0= ruleBooleanDef ) ) )?
            int alt36=2;
            int LA36_0 = input.LA(1);

            if ( (LA36_0==33) ) {
                alt36=1;
            }
            switch (alt36) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:959:6: otherlv_12= 'allowCommitBranch' ( (lv_allowCommitBranch_13_0= ruleBooleanDef ) )
                    {
                    otherlv_12=(Token)match(input,33,FOLLOW_33_in_ruleVersionDef1744); 

                        	newLeafNode(otherlv_12, grammarAccess.getVersionDefAccess().getAllowCommitBranchKeyword_7_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:963:1: ( (lv_allowCommitBranch_13_0= ruleBooleanDef ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:964:1: (lv_allowCommitBranch_13_0= ruleBooleanDef )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:964:1: (lv_allowCommitBranch_13_0= ruleBooleanDef )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:965:3: lv_allowCommitBranch_13_0= ruleBooleanDef
                    {
                     
                    	        newCompositeNode(grammarAccess.getVersionDefAccess().getAllowCommitBranchBooleanDefEnumRuleCall_7_1_0()); 
                    	    
                    pushFollow(FOLLOW_ruleBooleanDef_in_ruleVersionDef1765);
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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:981:4: (otherlv_14= 'baslineBranchGuid' ( (lv_baselineBranchGuid_15_0= RULE_STRING ) ) )?
            int alt37=2;
            int LA37_0 = input.LA(1);

            if ( (LA37_0==34) ) {
                alt37=1;
            }
            switch (alt37) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:981:6: otherlv_14= 'baslineBranchGuid' ( (lv_baselineBranchGuid_15_0= RULE_STRING ) )
                    {
                    otherlv_14=(Token)match(input,34,FOLLOW_34_in_ruleVersionDef1780); 

                        	newLeafNode(otherlv_14, grammarAccess.getVersionDefAccess().getBaslineBranchGuidKeyword_8_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:985:1: ( (lv_baselineBranchGuid_15_0= RULE_STRING ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:986:1: (lv_baselineBranchGuid_15_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:986:1: (lv_baselineBranchGuid_15_0= RULE_STRING )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:987:3: lv_baselineBranchGuid_15_0= RULE_STRING
                    {
                    lv_baselineBranchGuid_15_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleVersionDef1797); 

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

            otherlv_16=(Token)match(input,20,FOLLOW_20_in_ruleVersionDef1816); 

                	newLeafNode(otherlv_16, grammarAccess.getVersionDefAccess().getRightCurlyBracketKeyword_9());
                

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1015:1: entryRuleWorkDef returns [EObject current=null] : iv_ruleWorkDef= ruleWorkDef EOF ;
    public final EObject entryRuleWorkDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleWorkDef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1016:2: (iv_ruleWorkDef= ruleWorkDef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1017:2: iv_ruleWorkDef= ruleWorkDef EOF
            {
             newCompositeNode(grammarAccess.getWorkDefRule()); 
            pushFollow(FOLLOW_ruleWorkDef_in_entryRuleWorkDef1852);
            iv_ruleWorkDef=ruleWorkDef();

            state._fsp--;

             current =iv_ruleWorkDef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleWorkDef1862); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1024:1: ruleWorkDef returns [EObject current=null] : ( ( (lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'id' ( (lv_id_3_0= RULE_STRING ) ) )+ (otherlv_4= 'startState' ( ( ruleSTATE_NAME_REFERENCE ) ) ) ( (lv_widgetDefs_6_0= ruleWidgetDef ) )* ( (lv_decisionReviewDefs_7_0= ruleDecisionReviewDef ) )* ( (lv_peerReviewDefs_8_0= rulePeerReviewDef ) )* ( (lv_states_9_0= ruleStateDef ) )+ otherlv_10= '}' ) ;
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
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1027:28: ( ( ( (lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'id' ( (lv_id_3_0= RULE_STRING ) ) )+ (otherlv_4= 'startState' ( ( ruleSTATE_NAME_REFERENCE ) ) ) ( (lv_widgetDefs_6_0= ruleWidgetDef ) )* ( (lv_decisionReviewDefs_7_0= ruleDecisionReviewDef ) )* ( (lv_peerReviewDefs_8_0= rulePeerReviewDef ) )* ( (lv_states_9_0= ruleStateDef ) )+ otherlv_10= '}' ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1028:1: ( ( (lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'id' ( (lv_id_3_0= RULE_STRING ) ) )+ (otherlv_4= 'startState' ( ( ruleSTATE_NAME_REFERENCE ) ) ) ( (lv_widgetDefs_6_0= ruleWidgetDef ) )* ( (lv_decisionReviewDefs_7_0= ruleDecisionReviewDef ) )* ( (lv_peerReviewDefs_8_0= rulePeerReviewDef ) )* ( (lv_states_9_0= ruleStateDef ) )+ otherlv_10= '}' )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1028:1: ( ( (lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'id' ( (lv_id_3_0= RULE_STRING ) ) )+ (otherlv_4= 'startState' ( ( ruleSTATE_NAME_REFERENCE ) ) ) ( (lv_widgetDefs_6_0= ruleWidgetDef ) )* ( (lv_decisionReviewDefs_7_0= ruleDecisionReviewDef ) )* ( (lv_peerReviewDefs_8_0= rulePeerReviewDef ) )* ( (lv_states_9_0= ruleStateDef ) )+ otherlv_10= '}' )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1028:2: ( (lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'id' ( (lv_id_3_0= RULE_STRING ) ) )+ (otherlv_4= 'startState' ( ( ruleSTATE_NAME_REFERENCE ) ) ) ( (lv_widgetDefs_6_0= ruleWidgetDef ) )* ( (lv_decisionReviewDefs_7_0= ruleDecisionReviewDef ) )* ( (lv_peerReviewDefs_8_0= rulePeerReviewDef ) )* ( (lv_states_9_0= ruleStateDef ) )+ otherlv_10= '}'
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1028:2: ( (lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1029:1: (lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1029:1: (lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1030:3: lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE
            {
             
            	        newCompositeNode(grammarAccess.getWorkDefAccess().getNameWORK_DEFINITION_NAME_REFERENCEParserRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_ruleWORK_DEFINITION_NAME_REFERENCE_in_ruleWorkDef1908);
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

            otherlv_1=(Token)match(input,15,FOLLOW_15_in_ruleWorkDef1920); 

                	newLeafNode(otherlv_1, grammarAccess.getWorkDefAccess().getLeftCurlyBracketKeyword_1());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1050:1: (otherlv_2= 'id' ( (lv_id_3_0= RULE_STRING ) ) )+
            int cnt38=0;
            loop38:
            do {
                int alt38=2;
                int LA38_0 = input.LA(1);

                if ( (LA38_0==35) ) {
                    alt38=1;
                }


                switch (alt38) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1050:3: otherlv_2= 'id' ( (lv_id_3_0= RULE_STRING ) )
            	    {
            	    otherlv_2=(Token)match(input,35,FOLLOW_35_in_ruleWorkDef1933); 

            	        	newLeafNode(otherlv_2, grammarAccess.getWorkDefAccess().getIdKeyword_2_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1054:1: ( (lv_id_3_0= RULE_STRING ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1055:1: (lv_id_3_0= RULE_STRING )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1055:1: (lv_id_3_0= RULE_STRING )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1056:3: lv_id_3_0= RULE_STRING
            	    {
            	    lv_id_3_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleWorkDef1950); 

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
            	    if ( cnt38 >= 1 ) break loop38;
                        EarlyExitException eee =
                            new EarlyExitException(38, input);
                        throw eee;
                }
                cnt38++;
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1072:4: (otherlv_4= 'startState' ( ( ruleSTATE_NAME_REFERENCE ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1072:6: otherlv_4= 'startState' ( ( ruleSTATE_NAME_REFERENCE ) )
            {
            otherlv_4=(Token)match(input,36,FOLLOW_36_in_ruleWorkDef1970); 

                	newLeafNode(otherlv_4, grammarAccess.getWorkDefAccess().getStartStateKeyword_3_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1076:1: ( ( ruleSTATE_NAME_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1077:1: ( ruleSTATE_NAME_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1077:1: ( ruleSTATE_NAME_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1078:3: ruleSTATE_NAME_REFERENCE
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getWorkDefRule());
            	        }
                    
             
            	        newCompositeNode(grammarAccess.getWorkDefAccess().getStartStateStateDefCrossReference_3_1_0()); 
            	    
            pushFollow(FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleWorkDef1993);
            ruleSTATE_NAME_REFERENCE();

            state._fsp--;

             
            	        afterParserOrEnumRuleCall();
            	    

            }


            }


            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1091:3: ( (lv_widgetDefs_6_0= ruleWidgetDef ) )*
            loop39:
            do {
                int alt39=2;
                int LA39_0 = input.LA(1);

                if ( (LA39_0==37) ) {
                    alt39=1;
                }


                switch (alt39) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1092:1: (lv_widgetDefs_6_0= ruleWidgetDef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1092:1: (lv_widgetDefs_6_0= ruleWidgetDef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1093:3: lv_widgetDefs_6_0= ruleWidgetDef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getWorkDefAccess().getWidgetDefsWidgetDefParserRuleCall_4_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleWidgetDef_in_ruleWorkDef2015);
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
            	    break loop39;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1109:3: ( (lv_decisionReviewDefs_7_0= ruleDecisionReviewDef ) )*
            loop40:
            do {
                int alt40=2;
                int LA40_0 = input.LA(1);

                if ( (LA40_0==53) ) {
                    alt40=1;
                }


                switch (alt40) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1110:1: (lv_decisionReviewDefs_7_0= ruleDecisionReviewDef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1110:1: (lv_decisionReviewDefs_7_0= ruleDecisionReviewDef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1111:3: lv_decisionReviewDefs_7_0= ruleDecisionReviewDef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getWorkDefAccess().getDecisionReviewDefsDecisionReviewDefParserRuleCall_5_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleDecisionReviewDef_in_ruleWorkDef2037);
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
            	    break loop40;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1127:3: ( (lv_peerReviewDefs_8_0= rulePeerReviewDef ) )*
            loop41:
            do {
                int alt41=2;
                int LA41_0 = input.LA(1);

                if ( (LA41_0==61) ) {
                    alt41=1;
                }


                switch (alt41) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1128:1: (lv_peerReviewDefs_8_0= rulePeerReviewDef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1128:1: (lv_peerReviewDefs_8_0= rulePeerReviewDef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1129:3: lv_peerReviewDefs_8_0= rulePeerReviewDef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getWorkDefAccess().getPeerReviewDefsPeerReviewDefParserRuleCall_6_0()); 
            	    	    
            	    pushFollow(FOLLOW_rulePeerReviewDef_in_ruleWorkDef2059);
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
            	    break loop41;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1145:3: ( (lv_states_9_0= ruleStateDef ) )+
            int cnt42=0;
            loop42:
            do {
                int alt42=2;
                int LA42_0 = input.LA(1);

                if ( (LA42_0==47) ) {
                    alt42=1;
                }


                switch (alt42) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1146:1: (lv_states_9_0= ruleStateDef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1146:1: (lv_states_9_0= ruleStateDef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1147:3: lv_states_9_0= ruleStateDef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getWorkDefAccess().getStatesStateDefParserRuleCall_7_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleStateDef_in_ruleWorkDef2081);
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
            	    if ( cnt42 >= 1 ) break loop42;
                        EarlyExitException eee =
                            new EarlyExitException(42, input);
                        throw eee;
                }
                cnt42++;
            } while (true);

            otherlv_10=(Token)match(input,20,FOLLOW_20_in_ruleWorkDef2094); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1175:1: entryRuleWidgetDef returns [EObject current=null] : iv_ruleWidgetDef= ruleWidgetDef EOF ;
    public final EObject entryRuleWidgetDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleWidgetDef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1176:2: (iv_ruleWidgetDef= ruleWidgetDef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1177:2: iv_ruleWidgetDef= ruleWidgetDef EOF
            {
             newCompositeNode(grammarAccess.getWidgetDefRule()); 
            pushFollow(FOLLOW_ruleWidgetDef_in_entryRuleWidgetDef2130);
            iv_ruleWidgetDef=ruleWidgetDef();

            state._fsp--;

             current =iv_ruleWidgetDef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleWidgetDef2140); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1184:1: ruleWidgetDef returns [EObject current=null] : (otherlv_0= 'widgetDefinition' ( (lv_name_1_0= ruleWIDGET_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'attributeName' ( (lv_attributeName_4_0= RULE_STRING ) ) )? (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'xWidgetName' ( (lv_xWidgetName_8_0= RULE_STRING ) ) )? (otherlv_9= 'defaultValue' ( (lv_defaultValue_10_0= RULE_STRING ) ) )? (otherlv_11= 'height' ( (lv_height_12_0= RULE_INT ) ) )? (otherlv_13= 'option' ( (lv_option_14_0= ruleWidgetOption ) ) )* otherlv_15= '}' ) ;
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
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1187:28: ( (otherlv_0= 'widgetDefinition' ( (lv_name_1_0= ruleWIDGET_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'attributeName' ( (lv_attributeName_4_0= RULE_STRING ) ) )? (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'xWidgetName' ( (lv_xWidgetName_8_0= RULE_STRING ) ) )? (otherlv_9= 'defaultValue' ( (lv_defaultValue_10_0= RULE_STRING ) ) )? (otherlv_11= 'height' ( (lv_height_12_0= RULE_INT ) ) )? (otherlv_13= 'option' ( (lv_option_14_0= ruleWidgetOption ) ) )* otherlv_15= '}' ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1188:1: (otherlv_0= 'widgetDefinition' ( (lv_name_1_0= ruleWIDGET_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'attributeName' ( (lv_attributeName_4_0= RULE_STRING ) ) )? (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'xWidgetName' ( (lv_xWidgetName_8_0= RULE_STRING ) ) )? (otherlv_9= 'defaultValue' ( (lv_defaultValue_10_0= RULE_STRING ) ) )? (otherlv_11= 'height' ( (lv_height_12_0= RULE_INT ) ) )? (otherlv_13= 'option' ( (lv_option_14_0= ruleWidgetOption ) ) )* otherlv_15= '}' )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1188:1: (otherlv_0= 'widgetDefinition' ( (lv_name_1_0= ruleWIDGET_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'attributeName' ( (lv_attributeName_4_0= RULE_STRING ) ) )? (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'xWidgetName' ( (lv_xWidgetName_8_0= RULE_STRING ) ) )? (otherlv_9= 'defaultValue' ( (lv_defaultValue_10_0= RULE_STRING ) ) )? (otherlv_11= 'height' ( (lv_height_12_0= RULE_INT ) ) )? (otherlv_13= 'option' ( (lv_option_14_0= ruleWidgetOption ) ) )* otherlv_15= '}' )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1188:3: otherlv_0= 'widgetDefinition' ( (lv_name_1_0= ruleWIDGET_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'attributeName' ( (lv_attributeName_4_0= RULE_STRING ) ) )? (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'xWidgetName' ( (lv_xWidgetName_8_0= RULE_STRING ) ) )? (otherlv_9= 'defaultValue' ( (lv_defaultValue_10_0= RULE_STRING ) ) )? (otherlv_11= 'height' ( (lv_height_12_0= RULE_INT ) ) )? (otherlv_13= 'option' ( (lv_option_14_0= ruleWidgetOption ) ) )* otherlv_15= '}'
            {
            otherlv_0=(Token)match(input,37,FOLLOW_37_in_ruleWidgetDef2177); 

                	newLeafNode(otherlv_0, grammarAccess.getWidgetDefAccess().getWidgetDefinitionKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1192:1: ( (lv_name_1_0= ruleWIDGET_NAME_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1193:1: (lv_name_1_0= ruleWIDGET_NAME_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1193:1: (lv_name_1_0= ruleWIDGET_NAME_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1194:3: lv_name_1_0= ruleWIDGET_NAME_REFERENCE
            {
             
            	        newCompositeNode(grammarAccess.getWidgetDefAccess().getNameWIDGET_NAME_REFERENCEParserRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_ruleWIDGET_NAME_REFERENCE_in_ruleWidgetDef2198);
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

            otherlv_2=(Token)match(input,15,FOLLOW_15_in_ruleWidgetDef2210); 

                	newLeafNode(otherlv_2, grammarAccess.getWidgetDefAccess().getLeftCurlyBracketKeyword_2());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1214:1: (otherlv_3= 'attributeName' ( (lv_attributeName_4_0= RULE_STRING ) ) )?
            int alt43=2;
            int LA43_0 = input.LA(1);

            if ( (LA43_0==38) ) {
                alt43=1;
            }
            switch (alt43) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1214:3: otherlv_3= 'attributeName' ( (lv_attributeName_4_0= RULE_STRING ) )
                    {
                    otherlv_3=(Token)match(input,38,FOLLOW_38_in_ruleWidgetDef2223); 

                        	newLeafNode(otherlv_3, grammarAccess.getWidgetDefAccess().getAttributeNameKeyword_3_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1218:1: ( (lv_attributeName_4_0= RULE_STRING ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1219:1: (lv_attributeName_4_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1219:1: (lv_attributeName_4_0= RULE_STRING )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1220:3: lv_attributeName_4_0= RULE_STRING
                    {
                    lv_attributeName_4_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleWidgetDef2240); 

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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1236:4: (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )?
            int alt44=2;
            int LA44_0 = input.LA(1);

            if ( (LA44_0==39) ) {
                alt44=1;
            }
            switch (alt44) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1236:6: otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) )
                    {
                    otherlv_5=(Token)match(input,39,FOLLOW_39_in_ruleWidgetDef2260); 

                        	newLeafNode(otherlv_5, grammarAccess.getWidgetDefAccess().getDescriptionKeyword_4_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1240:1: ( (lv_description_6_0= RULE_STRING ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1241:1: (lv_description_6_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1241:1: (lv_description_6_0= RULE_STRING )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1242:3: lv_description_6_0= RULE_STRING
                    {
                    lv_description_6_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleWidgetDef2277); 

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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1258:4: (otherlv_7= 'xWidgetName' ( (lv_xWidgetName_8_0= RULE_STRING ) ) )?
            int alt45=2;
            int LA45_0 = input.LA(1);

            if ( (LA45_0==40) ) {
                alt45=1;
            }
            switch (alt45) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1258:6: otherlv_7= 'xWidgetName' ( (lv_xWidgetName_8_0= RULE_STRING ) )
                    {
                    otherlv_7=(Token)match(input,40,FOLLOW_40_in_ruleWidgetDef2297); 

                        	newLeafNode(otherlv_7, grammarAccess.getWidgetDefAccess().getXWidgetNameKeyword_5_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1262:1: ( (lv_xWidgetName_8_0= RULE_STRING ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1263:1: (lv_xWidgetName_8_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1263:1: (lv_xWidgetName_8_0= RULE_STRING )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1264:3: lv_xWidgetName_8_0= RULE_STRING
                    {
                    lv_xWidgetName_8_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleWidgetDef2314); 

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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1280:4: (otherlv_9= 'defaultValue' ( (lv_defaultValue_10_0= RULE_STRING ) ) )?
            int alt46=2;
            int LA46_0 = input.LA(1);

            if ( (LA46_0==41) ) {
                alt46=1;
            }
            switch (alt46) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1280:6: otherlv_9= 'defaultValue' ( (lv_defaultValue_10_0= RULE_STRING ) )
                    {
                    otherlv_9=(Token)match(input,41,FOLLOW_41_in_ruleWidgetDef2334); 

                        	newLeafNode(otherlv_9, grammarAccess.getWidgetDefAccess().getDefaultValueKeyword_6_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1284:1: ( (lv_defaultValue_10_0= RULE_STRING ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1285:1: (lv_defaultValue_10_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1285:1: (lv_defaultValue_10_0= RULE_STRING )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1286:3: lv_defaultValue_10_0= RULE_STRING
                    {
                    lv_defaultValue_10_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleWidgetDef2351); 

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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1302:4: (otherlv_11= 'height' ( (lv_height_12_0= RULE_INT ) ) )?
            int alt47=2;
            int LA47_0 = input.LA(1);

            if ( (LA47_0==42) ) {
                alt47=1;
            }
            switch (alt47) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1302:6: otherlv_11= 'height' ( (lv_height_12_0= RULE_INT ) )
                    {
                    otherlv_11=(Token)match(input,42,FOLLOW_42_in_ruleWidgetDef2371); 

                        	newLeafNode(otherlv_11, grammarAccess.getWidgetDefAccess().getHeightKeyword_7_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1306:1: ( (lv_height_12_0= RULE_INT ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1307:1: (lv_height_12_0= RULE_INT )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1307:1: (lv_height_12_0= RULE_INT )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1308:3: lv_height_12_0= RULE_INT
                    {
                    lv_height_12_0=(Token)match(input,RULE_INT,FOLLOW_RULE_INT_in_ruleWidgetDef2388); 

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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1324:4: (otherlv_13= 'option' ( (lv_option_14_0= ruleWidgetOption ) ) )*
            loop48:
            do {
                int alt48=2;
                int LA48_0 = input.LA(1);

                if ( (LA48_0==43) ) {
                    alt48=1;
                }


                switch (alt48) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1324:6: otherlv_13= 'option' ( (lv_option_14_0= ruleWidgetOption ) )
            	    {
            	    otherlv_13=(Token)match(input,43,FOLLOW_43_in_ruleWidgetDef2408); 

            	        	newLeafNode(otherlv_13, grammarAccess.getWidgetDefAccess().getOptionKeyword_8_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1328:1: ( (lv_option_14_0= ruleWidgetOption ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1329:1: (lv_option_14_0= ruleWidgetOption )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1329:1: (lv_option_14_0= ruleWidgetOption )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1330:3: lv_option_14_0= ruleWidgetOption
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getWidgetDefAccess().getOptionWidgetOptionParserRuleCall_8_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleWidgetOption_in_ruleWidgetDef2429);
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
            	    break loop48;
                }
            } while (true);

            otherlv_15=(Token)match(input,20,FOLLOW_20_in_ruleWidgetDef2443); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1358:1: entryRuleWidgetRef returns [EObject current=null] : iv_ruleWidgetRef= ruleWidgetRef EOF ;
    public final EObject entryRuleWidgetRef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleWidgetRef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1359:2: (iv_ruleWidgetRef= ruleWidgetRef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1360:2: iv_ruleWidgetRef= ruleWidgetRef EOF
            {
             newCompositeNode(grammarAccess.getWidgetRefRule()); 
            pushFollow(FOLLOW_ruleWidgetRef_in_entryRuleWidgetRef2479);
            iv_ruleWidgetRef=ruleWidgetRef();

            state._fsp--;

             current =iv_ruleWidgetRef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleWidgetRef2489); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1367:1: ruleWidgetRef returns [EObject current=null] : (otherlv_0= 'widget' ( ( ruleWIDGET_NAME_REFERENCE ) ) ) ;
    public final EObject ruleWidgetRef() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1370:28: ( (otherlv_0= 'widget' ( ( ruleWIDGET_NAME_REFERENCE ) ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1371:1: (otherlv_0= 'widget' ( ( ruleWIDGET_NAME_REFERENCE ) ) )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1371:1: (otherlv_0= 'widget' ( ( ruleWIDGET_NAME_REFERENCE ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1371:3: otherlv_0= 'widget' ( ( ruleWIDGET_NAME_REFERENCE ) )
            {
            otherlv_0=(Token)match(input,44,FOLLOW_44_in_ruleWidgetRef2526); 

                	newLeafNode(otherlv_0, grammarAccess.getWidgetRefAccess().getWidgetKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1375:1: ( ( ruleWIDGET_NAME_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1376:1: ( ruleWIDGET_NAME_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1376:1: ( ruleWIDGET_NAME_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1377:3: ruleWIDGET_NAME_REFERENCE
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getWidgetRefRule());
            	        }
                    
             
            	        newCompositeNode(grammarAccess.getWidgetRefAccess().getWidgetWidgetDefCrossReference_1_0()); 
            	    
            pushFollow(FOLLOW_ruleWIDGET_NAME_REFERENCE_in_ruleWidgetRef2549);
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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1398:1: entryRuleAttrWidget returns [EObject current=null] : iv_ruleAttrWidget= ruleAttrWidget EOF ;
    public final EObject entryRuleAttrWidget() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttrWidget = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1399:2: (iv_ruleAttrWidget= ruleAttrWidget EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1400:2: iv_ruleAttrWidget= ruleAttrWidget EOF
            {
             newCompositeNode(grammarAccess.getAttrWidgetRule()); 
            pushFollow(FOLLOW_ruleAttrWidget_in_entryRuleAttrWidget2585);
            iv_ruleAttrWidget=ruleAttrWidget();

            state._fsp--;

             current =iv_ruleAttrWidget; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttrWidget2595); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1407:1: ruleAttrWidget returns [EObject current=null] : (otherlv_0= 'attributeWidget' ( (lv_attributeName_1_0= RULE_STRING ) ) (otherlv_2= 'with' ( (lv_option_3_0= ruleWidgetOption ) ) )* ) ;
    public final EObject ruleAttrWidget() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_attributeName_1_0=null;
        Token otherlv_2=null;
        AntlrDatatypeRuleToken lv_option_3_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1410:28: ( (otherlv_0= 'attributeWidget' ( (lv_attributeName_1_0= RULE_STRING ) ) (otherlv_2= 'with' ( (lv_option_3_0= ruleWidgetOption ) ) )* ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1411:1: (otherlv_0= 'attributeWidget' ( (lv_attributeName_1_0= RULE_STRING ) ) (otherlv_2= 'with' ( (lv_option_3_0= ruleWidgetOption ) ) )* )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1411:1: (otherlv_0= 'attributeWidget' ( (lv_attributeName_1_0= RULE_STRING ) ) (otherlv_2= 'with' ( (lv_option_3_0= ruleWidgetOption ) ) )* )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1411:3: otherlv_0= 'attributeWidget' ( (lv_attributeName_1_0= RULE_STRING ) ) (otherlv_2= 'with' ( (lv_option_3_0= ruleWidgetOption ) ) )*
            {
            otherlv_0=(Token)match(input,45,FOLLOW_45_in_ruleAttrWidget2632); 

                	newLeafNode(otherlv_0, grammarAccess.getAttrWidgetAccess().getAttributeWidgetKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1415:1: ( (lv_attributeName_1_0= RULE_STRING ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1416:1: (lv_attributeName_1_0= RULE_STRING )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1416:1: (lv_attributeName_1_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1417:3: lv_attributeName_1_0= RULE_STRING
            {
            lv_attributeName_1_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAttrWidget2649); 

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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1433:2: (otherlv_2= 'with' ( (lv_option_3_0= ruleWidgetOption ) ) )*
            loop49:
            do {
                int alt49=2;
                int LA49_0 = input.LA(1);

                if ( (LA49_0==46) ) {
                    alt49=1;
                }


                switch (alt49) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1433:4: otherlv_2= 'with' ( (lv_option_3_0= ruleWidgetOption ) )
            	    {
            	    otherlv_2=(Token)match(input,46,FOLLOW_46_in_ruleAttrWidget2667); 

            	        	newLeafNode(otherlv_2, grammarAccess.getAttrWidgetAccess().getWithKeyword_2_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1437:1: ( (lv_option_3_0= ruleWidgetOption ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1438:1: (lv_option_3_0= ruleWidgetOption )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1438:1: (lv_option_3_0= ruleWidgetOption )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1439:3: lv_option_3_0= ruleWidgetOption
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getAttrWidgetAccess().getOptionWidgetOptionParserRuleCall_2_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleWidgetOption_in_ruleAttrWidget2688);
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
            	    break loop49;
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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1463:1: entryRuleStateDef returns [EObject current=null] : iv_ruleStateDef= ruleStateDef EOF ;
    public final EObject entryRuleStateDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleStateDef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1464:2: (iv_ruleStateDef= ruleStateDef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1465:2: iv_ruleStateDef= ruleStateDef EOF
            {
             newCompositeNode(grammarAccess.getStateDefRule()); 
            pushFollow(FOLLOW_ruleStateDef_in_entryRuleStateDef2726);
            iv_ruleStateDef=ruleStateDef();

            state._fsp--;

             current =iv_ruleStateDef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleStateDef2736); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1472:1: ruleStateDef returns [EObject current=null] : (otherlv_0= 'state' ( (lv_name_1_0= ruleSTATE_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'description' ( (lv_description_4_0= RULE_STRING ) ) )? otherlv_5= 'type' ( (lv_pageType_6_0= rulePageType ) ) otherlv_7= 'ordinal' ( (lv_ordinal_8_0= RULE_INT ) ) ( (lv_transitionStates_9_0= ruleToState ) )* (otherlv_10= 'rule' ( (lv_rules_11_0= ruleRule ) ) )* ( (lv_decisionReviews_12_0= ruleDecisionReviewRef ) )* ( (lv_peerReviews_13_0= rulePeerReviewRef ) )* (otherlv_14= 'percentWeight' ( (lv_percentWeight_15_0= RULE_INT ) ) )? ( (lv_layout_16_0= ruleLayoutType ) )? otherlv_17= '}' ) ;
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
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1475:28: ( (otherlv_0= 'state' ( (lv_name_1_0= ruleSTATE_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'description' ( (lv_description_4_0= RULE_STRING ) ) )? otherlv_5= 'type' ( (lv_pageType_6_0= rulePageType ) ) otherlv_7= 'ordinal' ( (lv_ordinal_8_0= RULE_INT ) ) ( (lv_transitionStates_9_0= ruleToState ) )* (otherlv_10= 'rule' ( (lv_rules_11_0= ruleRule ) ) )* ( (lv_decisionReviews_12_0= ruleDecisionReviewRef ) )* ( (lv_peerReviews_13_0= rulePeerReviewRef ) )* (otherlv_14= 'percentWeight' ( (lv_percentWeight_15_0= RULE_INT ) ) )? ( (lv_layout_16_0= ruleLayoutType ) )? otherlv_17= '}' ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1476:1: (otherlv_0= 'state' ( (lv_name_1_0= ruleSTATE_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'description' ( (lv_description_4_0= RULE_STRING ) ) )? otherlv_5= 'type' ( (lv_pageType_6_0= rulePageType ) ) otherlv_7= 'ordinal' ( (lv_ordinal_8_0= RULE_INT ) ) ( (lv_transitionStates_9_0= ruleToState ) )* (otherlv_10= 'rule' ( (lv_rules_11_0= ruleRule ) ) )* ( (lv_decisionReviews_12_0= ruleDecisionReviewRef ) )* ( (lv_peerReviews_13_0= rulePeerReviewRef ) )* (otherlv_14= 'percentWeight' ( (lv_percentWeight_15_0= RULE_INT ) ) )? ( (lv_layout_16_0= ruleLayoutType ) )? otherlv_17= '}' )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1476:1: (otherlv_0= 'state' ( (lv_name_1_0= ruleSTATE_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'description' ( (lv_description_4_0= RULE_STRING ) ) )? otherlv_5= 'type' ( (lv_pageType_6_0= rulePageType ) ) otherlv_7= 'ordinal' ( (lv_ordinal_8_0= RULE_INT ) ) ( (lv_transitionStates_9_0= ruleToState ) )* (otherlv_10= 'rule' ( (lv_rules_11_0= ruleRule ) ) )* ( (lv_decisionReviews_12_0= ruleDecisionReviewRef ) )* ( (lv_peerReviews_13_0= rulePeerReviewRef ) )* (otherlv_14= 'percentWeight' ( (lv_percentWeight_15_0= RULE_INT ) ) )? ( (lv_layout_16_0= ruleLayoutType ) )? otherlv_17= '}' )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1476:3: otherlv_0= 'state' ( (lv_name_1_0= ruleSTATE_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'description' ( (lv_description_4_0= RULE_STRING ) ) )? otherlv_5= 'type' ( (lv_pageType_6_0= rulePageType ) ) otherlv_7= 'ordinal' ( (lv_ordinal_8_0= RULE_INT ) ) ( (lv_transitionStates_9_0= ruleToState ) )* (otherlv_10= 'rule' ( (lv_rules_11_0= ruleRule ) ) )* ( (lv_decisionReviews_12_0= ruleDecisionReviewRef ) )* ( (lv_peerReviews_13_0= rulePeerReviewRef ) )* (otherlv_14= 'percentWeight' ( (lv_percentWeight_15_0= RULE_INT ) ) )? ( (lv_layout_16_0= ruleLayoutType ) )? otherlv_17= '}'
            {
            otherlv_0=(Token)match(input,47,FOLLOW_47_in_ruleStateDef2773); 

                	newLeafNode(otherlv_0, grammarAccess.getStateDefAccess().getStateKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1480:1: ( (lv_name_1_0= ruleSTATE_NAME_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1481:1: (lv_name_1_0= ruleSTATE_NAME_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1481:1: (lv_name_1_0= ruleSTATE_NAME_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1482:3: lv_name_1_0= ruleSTATE_NAME_REFERENCE
            {
             
            	        newCompositeNode(grammarAccess.getStateDefAccess().getNameSTATE_NAME_REFERENCEParserRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleStateDef2794);
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

            otherlv_2=(Token)match(input,15,FOLLOW_15_in_ruleStateDef2806); 

                	newLeafNode(otherlv_2, grammarAccess.getStateDefAccess().getLeftCurlyBracketKeyword_2());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1502:1: (otherlv_3= 'description' ( (lv_description_4_0= RULE_STRING ) ) )?
            int alt50=2;
            int LA50_0 = input.LA(1);

            if ( (LA50_0==39) ) {
                alt50=1;
            }
            switch (alt50) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1502:3: otherlv_3= 'description' ( (lv_description_4_0= RULE_STRING ) )
                    {
                    otherlv_3=(Token)match(input,39,FOLLOW_39_in_ruleStateDef2819); 

                        	newLeafNode(otherlv_3, grammarAccess.getStateDefAccess().getDescriptionKeyword_3_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1506:1: ( (lv_description_4_0= RULE_STRING ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1507:1: (lv_description_4_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1507:1: (lv_description_4_0= RULE_STRING )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1508:3: lv_description_4_0= RULE_STRING
                    {
                    lv_description_4_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleStateDef2836); 

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

            otherlv_5=(Token)match(input,48,FOLLOW_48_in_ruleStateDef2855); 

                	newLeafNode(otherlv_5, grammarAccess.getStateDefAccess().getTypeKeyword_4());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1528:1: ( (lv_pageType_6_0= rulePageType ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1529:1: (lv_pageType_6_0= rulePageType )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1529:1: (lv_pageType_6_0= rulePageType )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1530:3: lv_pageType_6_0= rulePageType
            {
             
            	        newCompositeNode(grammarAccess.getStateDefAccess().getPageTypePageTypeParserRuleCall_5_0()); 
            	    
            pushFollow(FOLLOW_rulePageType_in_ruleStateDef2876);
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

            otherlv_7=(Token)match(input,49,FOLLOW_49_in_ruleStateDef2888); 

                	newLeafNode(otherlv_7, grammarAccess.getStateDefAccess().getOrdinalKeyword_6());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1550:1: ( (lv_ordinal_8_0= RULE_INT ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1551:1: (lv_ordinal_8_0= RULE_INT )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1551:1: (lv_ordinal_8_0= RULE_INT )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1552:3: lv_ordinal_8_0= RULE_INT
            {
            lv_ordinal_8_0=(Token)match(input,RULE_INT,FOLLOW_RULE_INT_in_ruleStateDef2905); 

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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1568:2: ( (lv_transitionStates_9_0= ruleToState ) )*
            loop51:
            do {
                int alt51=2;
                int LA51_0 = input.LA(1);

                if ( (LA51_0==65) ) {
                    alt51=1;
                }


                switch (alt51) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1569:1: (lv_transitionStates_9_0= ruleToState )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1569:1: (lv_transitionStates_9_0= ruleToState )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1570:3: lv_transitionStates_9_0= ruleToState
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getStateDefAccess().getTransitionStatesToStateParserRuleCall_8_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleToState_in_ruleStateDef2931);
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
            	    break loop51;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1586:3: (otherlv_10= 'rule' ( (lv_rules_11_0= ruleRule ) ) )*
            loop52:
            do {
                int alt52=2;
                int LA52_0 = input.LA(1);

                if ( (LA52_0==50) ) {
                    alt52=1;
                }


                switch (alt52) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1586:5: otherlv_10= 'rule' ( (lv_rules_11_0= ruleRule ) )
            	    {
            	    otherlv_10=(Token)match(input,50,FOLLOW_50_in_ruleStateDef2945); 

            	        	newLeafNode(otherlv_10, grammarAccess.getStateDefAccess().getRuleKeyword_9_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1590:1: ( (lv_rules_11_0= ruleRule ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1591:1: (lv_rules_11_0= ruleRule )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1591:1: (lv_rules_11_0= ruleRule )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1592:3: lv_rules_11_0= ruleRule
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getStateDefAccess().getRulesRuleParserRuleCall_9_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleRule_in_ruleStateDef2966);
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
            	    break loop52;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1608:4: ( (lv_decisionReviews_12_0= ruleDecisionReviewRef ) )*
            loop53:
            do {
                int alt53=2;
                int LA53_0 = input.LA(1);

                if ( (LA53_0==52) ) {
                    alt53=1;
                }


                switch (alt53) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1609:1: (lv_decisionReviews_12_0= ruleDecisionReviewRef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1609:1: (lv_decisionReviews_12_0= ruleDecisionReviewRef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1610:3: lv_decisionReviews_12_0= ruleDecisionReviewRef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getStateDefAccess().getDecisionReviewsDecisionReviewRefParserRuleCall_10_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleDecisionReviewRef_in_ruleStateDef2989);
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
            	    break loop53;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1626:3: ( (lv_peerReviews_13_0= rulePeerReviewRef ) )*
            loop54:
            do {
                int alt54=2;
                int LA54_0 = input.LA(1);

                if ( (LA54_0==60) ) {
                    alt54=1;
                }


                switch (alt54) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1627:1: (lv_peerReviews_13_0= rulePeerReviewRef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1627:1: (lv_peerReviews_13_0= rulePeerReviewRef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1628:3: lv_peerReviews_13_0= rulePeerReviewRef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getStateDefAccess().getPeerReviewsPeerReviewRefParserRuleCall_11_0()); 
            	    	    
            	    pushFollow(FOLLOW_rulePeerReviewRef_in_ruleStateDef3011);
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
            	    break loop54;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1644:3: (otherlv_14= 'percentWeight' ( (lv_percentWeight_15_0= RULE_INT ) ) )?
            int alt55=2;
            int LA55_0 = input.LA(1);

            if ( (LA55_0==51) ) {
                alt55=1;
            }
            switch (alt55) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1644:5: otherlv_14= 'percentWeight' ( (lv_percentWeight_15_0= RULE_INT ) )
                    {
                    otherlv_14=(Token)match(input,51,FOLLOW_51_in_ruleStateDef3025); 

                        	newLeafNode(otherlv_14, grammarAccess.getStateDefAccess().getPercentWeightKeyword_12_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1648:1: ( (lv_percentWeight_15_0= RULE_INT ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1649:1: (lv_percentWeight_15_0= RULE_INT )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1649:1: (lv_percentWeight_15_0= RULE_INT )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1650:3: lv_percentWeight_15_0= RULE_INT
                    {
                    lv_percentWeight_15_0=(Token)match(input,RULE_INT,FOLLOW_RULE_INT_in_ruleStateDef3042); 

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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1666:4: ( (lv_layout_16_0= ruleLayoutType ) )?
            int alt56=2;
            int LA56_0 = input.LA(1);

            if ( ((LA56_0>=66 && LA56_0<=67)) ) {
                alt56=1;
            }
            switch (alt56) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1667:1: (lv_layout_16_0= ruleLayoutType )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1667:1: (lv_layout_16_0= ruleLayoutType )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1668:3: lv_layout_16_0= ruleLayoutType
                    {
                     
                    	        newCompositeNode(grammarAccess.getStateDefAccess().getLayoutLayoutTypeParserRuleCall_13_0()); 
                    	    
                    pushFollow(FOLLOW_ruleLayoutType_in_ruleStateDef3070);
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

            otherlv_17=(Token)match(input,20,FOLLOW_20_in_ruleStateDef3083); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1696:1: entryRuleDecisionReviewRef returns [EObject current=null] : iv_ruleDecisionReviewRef= ruleDecisionReviewRef EOF ;
    public final EObject entryRuleDecisionReviewRef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDecisionReviewRef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1697:2: (iv_ruleDecisionReviewRef= ruleDecisionReviewRef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1698:2: iv_ruleDecisionReviewRef= ruleDecisionReviewRef EOF
            {
             newCompositeNode(grammarAccess.getDecisionReviewRefRule()); 
            pushFollow(FOLLOW_ruleDecisionReviewRef_in_entryRuleDecisionReviewRef3119);
            iv_ruleDecisionReviewRef=ruleDecisionReviewRef();

            state._fsp--;

             current =iv_ruleDecisionReviewRef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleDecisionReviewRef3129); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1705:1: ruleDecisionReviewRef returns [EObject current=null] : (otherlv_0= 'decisionReview' ( ( ruleDECISION_REVIEW_NAME_REFERENCE ) ) ) ;
    public final EObject ruleDecisionReviewRef() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1708:28: ( (otherlv_0= 'decisionReview' ( ( ruleDECISION_REVIEW_NAME_REFERENCE ) ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1709:1: (otherlv_0= 'decisionReview' ( ( ruleDECISION_REVIEW_NAME_REFERENCE ) ) )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1709:1: (otherlv_0= 'decisionReview' ( ( ruleDECISION_REVIEW_NAME_REFERENCE ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1709:3: otherlv_0= 'decisionReview' ( ( ruleDECISION_REVIEW_NAME_REFERENCE ) )
            {
            otherlv_0=(Token)match(input,52,FOLLOW_52_in_ruleDecisionReviewRef3166); 

                	newLeafNode(otherlv_0, grammarAccess.getDecisionReviewRefAccess().getDecisionReviewKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1713:1: ( ( ruleDECISION_REVIEW_NAME_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1714:1: ( ruleDECISION_REVIEW_NAME_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1714:1: ( ruleDECISION_REVIEW_NAME_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1715:3: ruleDECISION_REVIEW_NAME_REFERENCE
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getDecisionReviewRefRule());
            	        }
                    
             
            	        newCompositeNode(grammarAccess.getDecisionReviewRefAccess().getDecisionReviewDecisionReviewDefCrossReference_1_0()); 
            	    
            pushFollow(FOLLOW_ruleDECISION_REVIEW_NAME_REFERENCE_in_ruleDecisionReviewRef3189);
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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1736:1: entryRuleDecisionReviewDef returns [EObject current=null] : iv_ruleDecisionReviewDef= ruleDecisionReviewDef EOF ;
    public final EObject entryRuleDecisionReviewDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDecisionReviewDef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1737:2: (iv_ruleDecisionReviewDef= ruleDecisionReviewDef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1738:2: iv_ruleDecisionReviewDef= ruleDecisionReviewDef EOF
            {
             newCompositeNode(grammarAccess.getDecisionReviewDefRule()); 
            pushFollow(FOLLOW_ruleDecisionReviewDef_in_entryRuleDecisionReviewDef3225);
            iv_ruleDecisionReviewDef=ruleDecisionReviewDef();

            state._fsp--;

             current =iv_ruleDecisionReviewDef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleDecisionReviewDef3235); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1745:1: ruleDecisionReviewDef returns [EObject current=null] : (otherlv_0= 'decisionReviewDefinition' ( (lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_9= 'blockingType' ( (lv_blockingType_10_0= ruleReviewBlockingType ) ) otherlv_11= 'onEvent' ( (lv_stateEvent_12_0= ruleWorkflowEventType ) ) (otherlv_13= 'assignee' ( (lv_assigneeRefs_14_0= ruleUserRef ) ) )* (otherlv_15= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_16_0= ruleBooleanDef ) ) )? ( (lv_options_17_0= ruleDecisionReviewOpt ) )+ otherlv_18= '}' ) ;
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
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1748:28: ( (otherlv_0= 'decisionReviewDefinition' ( (lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_9= 'blockingType' ( (lv_blockingType_10_0= ruleReviewBlockingType ) ) otherlv_11= 'onEvent' ( (lv_stateEvent_12_0= ruleWorkflowEventType ) ) (otherlv_13= 'assignee' ( (lv_assigneeRefs_14_0= ruleUserRef ) ) )* (otherlv_15= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_16_0= ruleBooleanDef ) ) )? ( (lv_options_17_0= ruleDecisionReviewOpt ) )+ otherlv_18= '}' ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1749:1: (otherlv_0= 'decisionReviewDefinition' ( (lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_9= 'blockingType' ( (lv_blockingType_10_0= ruleReviewBlockingType ) ) otherlv_11= 'onEvent' ( (lv_stateEvent_12_0= ruleWorkflowEventType ) ) (otherlv_13= 'assignee' ( (lv_assigneeRefs_14_0= ruleUserRef ) ) )* (otherlv_15= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_16_0= ruleBooleanDef ) ) )? ( (lv_options_17_0= ruleDecisionReviewOpt ) )+ otherlv_18= '}' )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1749:1: (otherlv_0= 'decisionReviewDefinition' ( (lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_9= 'blockingType' ( (lv_blockingType_10_0= ruleReviewBlockingType ) ) otherlv_11= 'onEvent' ( (lv_stateEvent_12_0= ruleWorkflowEventType ) ) (otherlv_13= 'assignee' ( (lv_assigneeRefs_14_0= ruleUserRef ) ) )* (otherlv_15= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_16_0= ruleBooleanDef ) ) )? ( (lv_options_17_0= ruleDecisionReviewOpt ) )+ otherlv_18= '}' )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1749:3: otherlv_0= 'decisionReviewDefinition' ( (lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_9= 'blockingType' ( (lv_blockingType_10_0= ruleReviewBlockingType ) ) otherlv_11= 'onEvent' ( (lv_stateEvent_12_0= ruleWorkflowEventType ) ) (otherlv_13= 'assignee' ( (lv_assigneeRefs_14_0= ruleUserRef ) ) )* (otherlv_15= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_16_0= ruleBooleanDef ) ) )? ( (lv_options_17_0= ruleDecisionReviewOpt ) )+ otherlv_18= '}'
            {
            otherlv_0=(Token)match(input,53,FOLLOW_53_in_ruleDecisionReviewDef3272); 

                	newLeafNode(otherlv_0, grammarAccess.getDecisionReviewDefAccess().getDecisionReviewDefinitionKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1753:1: ( (lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1754:1: (lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1754:1: (lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1755:3: lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE
            {
             
            	        newCompositeNode(grammarAccess.getDecisionReviewDefAccess().getNameDECISION_REVIEW_NAME_REFERENCEParserRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_ruleDECISION_REVIEW_NAME_REFERENCE_in_ruleDecisionReviewDef3293);
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

            otherlv_2=(Token)match(input,15,FOLLOW_15_in_ruleDecisionReviewDef3305); 

                	newLeafNode(otherlv_2, grammarAccess.getDecisionReviewDefAccess().getLeftCurlyBracketKeyword_2());
                
            otherlv_3=(Token)match(input,54,FOLLOW_54_in_ruleDecisionReviewDef3317); 

                	newLeafNode(otherlv_3, grammarAccess.getDecisionReviewDefAccess().getTitleKeyword_3());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1779:1: ( (lv_title_4_0= RULE_STRING ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1780:1: (lv_title_4_0= RULE_STRING )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1780:1: (lv_title_4_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1781:3: lv_title_4_0= RULE_STRING
            {
            lv_title_4_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleDecisionReviewDef3334); 

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

            otherlv_5=(Token)match(input,39,FOLLOW_39_in_ruleDecisionReviewDef3351); 

                	newLeafNode(otherlv_5, grammarAccess.getDecisionReviewDefAccess().getDescriptionKeyword_5());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1801:1: ( (lv_description_6_0= RULE_STRING ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1802:1: (lv_description_6_0= RULE_STRING )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1802:1: (lv_description_6_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1803:3: lv_description_6_0= RULE_STRING
            {
            lv_description_6_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleDecisionReviewDef3368); 

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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1819:2: (otherlv_7= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )?
            int alt57=2;
            int LA57_0 = input.LA(1);

            if ( (LA57_0==55) ) {
                alt57=1;
            }
            switch (alt57) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1819:4: otherlv_7= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) )
                    {
                    otherlv_7=(Token)match(input,55,FOLLOW_55_in_ruleDecisionReviewDef3386); 

                        	newLeafNode(otherlv_7, grammarAccess.getDecisionReviewDefAccess().getRelatedToStateKeyword_7_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1823:1: ( ( ruleSTATE_NAME_REFERENCE ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1824:1: ( ruleSTATE_NAME_REFERENCE )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1824:1: ( ruleSTATE_NAME_REFERENCE )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1825:3: ruleSTATE_NAME_REFERENCE
                    {

                    			if (current==null) {
                    	            current = createModelElement(grammarAccess.getDecisionReviewDefRule());
                    	        }
                            
                     
                    	        newCompositeNode(grammarAccess.getDecisionReviewDefAccess().getRelatedToStateStateDefCrossReference_7_1_0()); 
                    	    
                    pushFollow(FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleDecisionReviewDef3409);
                    ruleSTATE_NAME_REFERENCE();

                    state._fsp--;

                     
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }


                    }
                    break;

            }

            otherlv_9=(Token)match(input,56,FOLLOW_56_in_ruleDecisionReviewDef3423); 

                	newLeafNode(otherlv_9, grammarAccess.getDecisionReviewDefAccess().getBlockingTypeKeyword_8());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1842:1: ( (lv_blockingType_10_0= ruleReviewBlockingType ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1843:1: (lv_blockingType_10_0= ruleReviewBlockingType )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1843:1: (lv_blockingType_10_0= ruleReviewBlockingType )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1844:3: lv_blockingType_10_0= ruleReviewBlockingType
            {
             
            	        newCompositeNode(grammarAccess.getDecisionReviewDefAccess().getBlockingTypeReviewBlockingTypeEnumRuleCall_9_0()); 
            	    
            pushFollow(FOLLOW_ruleReviewBlockingType_in_ruleDecisionReviewDef3444);
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

            otherlv_11=(Token)match(input,57,FOLLOW_57_in_ruleDecisionReviewDef3456); 

                	newLeafNode(otherlv_11, grammarAccess.getDecisionReviewDefAccess().getOnEventKeyword_10());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1864:1: ( (lv_stateEvent_12_0= ruleWorkflowEventType ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1865:1: (lv_stateEvent_12_0= ruleWorkflowEventType )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1865:1: (lv_stateEvent_12_0= ruleWorkflowEventType )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1866:3: lv_stateEvent_12_0= ruleWorkflowEventType
            {
             
            	        newCompositeNode(grammarAccess.getDecisionReviewDefAccess().getStateEventWorkflowEventTypeEnumRuleCall_11_0()); 
            	    
            pushFollow(FOLLOW_ruleWorkflowEventType_in_ruleDecisionReviewDef3477);
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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1882:2: (otherlv_13= 'assignee' ( (lv_assigneeRefs_14_0= ruleUserRef ) ) )*
            loop58:
            do {
                int alt58=2;
                int LA58_0 = input.LA(1);

                if ( (LA58_0==58) ) {
                    alt58=1;
                }


                switch (alt58) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1882:4: otherlv_13= 'assignee' ( (lv_assigneeRefs_14_0= ruleUserRef ) )
            	    {
            	    otherlv_13=(Token)match(input,58,FOLLOW_58_in_ruleDecisionReviewDef3490); 

            	        	newLeafNode(otherlv_13, grammarAccess.getDecisionReviewDefAccess().getAssigneeKeyword_12_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1886:1: ( (lv_assigneeRefs_14_0= ruleUserRef ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1887:1: (lv_assigneeRefs_14_0= ruleUserRef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1887:1: (lv_assigneeRefs_14_0= ruleUserRef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1888:3: lv_assigneeRefs_14_0= ruleUserRef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getDecisionReviewDefAccess().getAssigneeRefsUserRefParserRuleCall_12_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleUserRef_in_ruleDecisionReviewDef3511);
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
            	    break loop58;
                }
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1904:4: (otherlv_15= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_16_0= ruleBooleanDef ) ) )?
            int alt59=2;
            int LA59_0 = input.LA(1);

            if ( (LA59_0==59) ) {
                alt59=1;
            }
            switch (alt59) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1904:6: otherlv_15= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_16_0= ruleBooleanDef ) )
                    {
                    otherlv_15=(Token)match(input,59,FOLLOW_59_in_ruleDecisionReviewDef3526); 

                        	newLeafNode(otherlv_15, grammarAccess.getDecisionReviewDefAccess().getAutoTransitionToDecisionKeyword_13_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1908:1: ( (lv_autoTransitionToDecision_16_0= ruleBooleanDef ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1909:1: (lv_autoTransitionToDecision_16_0= ruleBooleanDef )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1909:1: (lv_autoTransitionToDecision_16_0= ruleBooleanDef )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1910:3: lv_autoTransitionToDecision_16_0= ruleBooleanDef
                    {
                     
                    	        newCompositeNode(grammarAccess.getDecisionReviewDefAccess().getAutoTransitionToDecisionBooleanDefEnumRuleCall_13_1_0()); 
                    	    
                    pushFollow(FOLLOW_ruleBooleanDef_in_ruleDecisionReviewDef3547);
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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1926:4: ( (lv_options_17_0= ruleDecisionReviewOpt ) )+
            int cnt60=0;
            loop60:
            do {
                int alt60=2;
                int LA60_0 = input.LA(1);

                if ( (LA60_0==43) ) {
                    alt60=1;
                }


                switch (alt60) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1927:1: (lv_options_17_0= ruleDecisionReviewOpt )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1927:1: (lv_options_17_0= ruleDecisionReviewOpt )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1928:3: lv_options_17_0= ruleDecisionReviewOpt
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getDecisionReviewDefAccess().getOptionsDecisionReviewOptParserRuleCall_14_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleDecisionReviewOpt_in_ruleDecisionReviewDef3570);
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
            	    if ( cnt60 >= 1 ) break loop60;
                        EarlyExitException eee =
                            new EarlyExitException(60, input);
                        throw eee;
                }
                cnt60++;
            } while (true);

            otherlv_18=(Token)match(input,20,FOLLOW_20_in_ruleDecisionReviewDef3583); 

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


    // $ANTLR start "entryRuleDecisionReviewOpt"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1956:1: entryRuleDecisionReviewOpt returns [EObject current=null] : iv_ruleDecisionReviewOpt= ruleDecisionReviewOpt EOF ;
    public final EObject entryRuleDecisionReviewOpt() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDecisionReviewOpt = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1957:2: (iv_ruleDecisionReviewOpt= ruleDecisionReviewOpt EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1958:2: iv_ruleDecisionReviewOpt= ruleDecisionReviewOpt EOF
            {
             newCompositeNode(grammarAccess.getDecisionReviewOptRule()); 
            pushFollow(FOLLOW_ruleDecisionReviewOpt_in_entryRuleDecisionReviewOpt3619);
            iv_ruleDecisionReviewOpt=ruleDecisionReviewOpt();

            state._fsp--;

             current =iv_ruleDecisionReviewOpt; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleDecisionReviewOpt3629); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1965:1: ruleDecisionReviewOpt returns [EObject current=null] : (otherlv_0= 'option' ( (lv_name_1_0= RULE_STRING ) ) ( (lv_followup_2_0= ruleFollowupRef ) )? ) ;
    public final EObject ruleDecisionReviewOpt() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_name_1_0=null;
        EObject lv_followup_2_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1968:28: ( (otherlv_0= 'option' ( (lv_name_1_0= RULE_STRING ) ) ( (lv_followup_2_0= ruleFollowupRef ) )? ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1969:1: (otherlv_0= 'option' ( (lv_name_1_0= RULE_STRING ) ) ( (lv_followup_2_0= ruleFollowupRef ) )? )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1969:1: (otherlv_0= 'option' ( (lv_name_1_0= RULE_STRING ) ) ( (lv_followup_2_0= ruleFollowupRef ) )? )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1969:3: otherlv_0= 'option' ( (lv_name_1_0= RULE_STRING ) ) ( (lv_followup_2_0= ruleFollowupRef ) )?
            {
            otherlv_0=(Token)match(input,43,FOLLOW_43_in_ruleDecisionReviewOpt3666); 

                	newLeafNode(otherlv_0, grammarAccess.getDecisionReviewOptAccess().getOptionKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1973:1: ( (lv_name_1_0= RULE_STRING ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1974:1: (lv_name_1_0= RULE_STRING )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1974:1: (lv_name_1_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1975:3: lv_name_1_0= RULE_STRING
            {
            lv_name_1_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleDecisionReviewOpt3683); 

            			newLeafNode(lv_name_1_0, grammarAccess.getDecisionReviewOptAccess().getNameSTRINGTerminalRuleCall_1_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getDecisionReviewOptRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"name",
                    		lv_name_1_0, 
                    		"STRING");
            	    

            }


            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1991:2: ( (lv_followup_2_0= ruleFollowupRef ) )?
            int alt61=2;
            int LA61_0 = input.LA(1);

            if ( (LA61_0==63) ) {
                alt61=1;
            }
            switch (alt61) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1992:1: (lv_followup_2_0= ruleFollowupRef )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1992:1: (lv_followup_2_0= ruleFollowupRef )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:1993:3: lv_followup_2_0= ruleFollowupRef
                    {
                     
                    	        newCompositeNode(grammarAccess.getDecisionReviewOptAccess().getFollowupFollowupRefParserRuleCall_2_0()); 
                    	    
                    pushFollow(FOLLOW_ruleFollowupRef_in_ruleDecisionReviewOpt3709);
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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2017:1: entryRulePeerReviewRef returns [EObject current=null] : iv_rulePeerReviewRef= rulePeerReviewRef EOF ;
    public final EObject entryRulePeerReviewRef() throws RecognitionException {
        EObject current = null;

        EObject iv_rulePeerReviewRef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2018:2: (iv_rulePeerReviewRef= rulePeerReviewRef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2019:2: iv_rulePeerReviewRef= rulePeerReviewRef EOF
            {
             newCompositeNode(grammarAccess.getPeerReviewRefRule()); 
            pushFollow(FOLLOW_rulePeerReviewRef_in_entryRulePeerReviewRef3746);
            iv_rulePeerReviewRef=rulePeerReviewRef();

            state._fsp--;

             current =iv_rulePeerReviewRef; 
            match(input,EOF,FOLLOW_EOF_in_entryRulePeerReviewRef3756); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2026:1: rulePeerReviewRef returns [EObject current=null] : (otherlv_0= 'peerReview' ( ( rulePEER_REVIEW_NAME_REFERENCE ) ) ) ;
    public final EObject rulePeerReviewRef() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2029:28: ( (otherlv_0= 'peerReview' ( ( rulePEER_REVIEW_NAME_REFERENCE ) ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2030:1: (otherlv_0= 'peerReview' ( ( rulePEER_REVIEW_NAME_REFERENCE ) ) )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2030:1: (otherlv_0= 'peerReview' ( ( rulePEER_REVIEW_NAME_REFERENCE ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2030:3: otherlv_0= 'peerReview' ( ( rulePEER_REVIEW_NAME_REFERENCE ) )
            {
            otherlv_0=(Token)match(input,60,FOLLOW_60_in_rulePeerReviewRef3793); 

                	newLeafNode(otherlv_0, grammarAccess.getPeerReviewRefAccess().getPeerReviewKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2034:1: ( ( rulePEER_REVIEW_NAME_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2035:1: ( rulePEER_REVIEW_NAME_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2035:1: ( rulePEER_REVIEW_NAME_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2036:3: rulePEER_REVIEW_NAME_REFERENCE
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getPeerReviewRefRule());
            	        }
                    
             
            	        newCompositeNode(grammarAccess.getPeerReviewRefAccess().getPeerReviewPeerReviewDefCrossReference_1_0()); 
            	    
            pushFollow(FOLLOW_rulePEER_REVIEW_NAME_REFERENCE_in_rulePeerReviewRef3816);
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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2057:1: entryRulePeerReviewDef returns [EObject current=null] : iv_rulePeerReviewDef= rulePeerReviewDef EOF ;
    public final EObject entryRulePeerReviewDef() throws RecognitionException {
        EObject current = null;

        EObject iv_rulePeerReviewDef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2058:2: (iv_rulePeerReviewDef= rulePeerReviewDef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2059:2: iv_rulePeerReviewDef= rulePeerReviewDef EOF
            {
             newCompositeNode(grammarAccess.getPeerReviewDefRule()); 
            pushFollow(FOLLOW_rulePeerReviewDef_in_entryRulePeerReviewDef3852);
            iv_rulePeerReviewDef=rulePeerReviewDef();

            state._fsp--;

             current =iv_rulePeerReviewDef; 
            match(input,EOF,FOLLOW_EOF_in_entryRulePeerReviewDef3862); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2066:1: rulePeerReviewDef returns [EObject current=null] : (otherlv_0= 'peerReviewDefinition' ( (lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) )? otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'location' ( (lv_location_8_0= RULE_STRING ) ) )? (otherlv_9= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_11= 'blockingType' ( (lv_blockingType_12_0= ruleReviewBlockingType ) ) otherlv_13= 'onEvent' ( (lv_stateEvent_14_0= ruleWorkflowEventType ) ) (otherlv_15= 'assignee' ( (lv_assigneeRefs_16_0= ruleUserRef ) ) )* otherlv_17= '}' ) ;
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
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2069:28: ( (otherlv_0= 'peerReviewDefinition' ( (lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) )? otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'location' ( (lv_location_8_0= RULE_STRING ) ) )? (otherlv_9= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_11= 'blockingType' ( (lv_blockingType_12_0= ruleReviewBlockingType ) ) otherlv_13= 'onEvent' ( (lv_stateEvent_14_0= ruleWorkflowEventType ) ) (otherlv_15= 'assignee' ( (lv_assigneeRefs_16_0= ruleUserRef ) ) )* otherlv_17= '}' ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2070:1: (otherlv_0= 'peerReviewDefinition' ( (lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) )? otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'location' ( (lv_location_8_0= RULE_STRING ) ) )? (otherlv_9= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_11= 'blockingType' ( (lv_blockingType_12_0= ruleReviewBlockingType ) ) otherlv_13= 'onEvent' ( (lv_stateEvent_14_0= ruleWorkflowEventType ) ) (otherlv_15= 'assignee' ( (lv_assigneeRefs_16_0= ruleUserRef ) ) )* otherlv_17= '}' )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2070:1: (otherlv_0= 'peerReviewDefinition' ( (lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) )? otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'location' ( (lv_location_8_0= RULE_STRING ) ) )? (otherlv_9= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_11= 'blockingType' ( (lv_blockingType_12_0= ruleReviewBlockingType ) ) otherlv_13= 'onEvent' ( (lv_stateEvent_14_0= ruleWorkflowEventType ) ) (otherlv_15= 'assignee' ( (lv_assigneeRefs_16_0= ruleUserRef ) ) )* otherlv_17= '}' )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2070:3: otherlv_0= 'peerReviewDefinition' ( (lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) )? otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'location' ( (lv_location_8_0= RULE_STRING ) ) )? (otherlv_9= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_11= 'blockingType' ( (lv_blockingType_12_0= ruleReviewBlockingType ) ) otherlv_13= 'onEvent' ( (lv_stateEvent_14_0= ruleWorkflowEventType ) ) (otherlv_15= 'assignee' ( (lv_assigneeRefs_16_0= ruleUserRef ) ) )* otherlv_17= '}'
            {
            otherlv_0=(Token)match(input,61,FOLLOW_61_in_rulePeerReviewDef3899); 

                	newLeafNode(otherlv_0, grammarAccess.getPeerReviewDefAccess().getPeerReviewDefinitionKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2074:1: ( (lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2075:1: (lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2075:1: (lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2076:3: lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE
            {
             
            	        newCompositeNode(grammarAccess.getPeerReviewDefAccess().getNamePEER_REVIEW_NAME_REFERENCEParserRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_rulePEER_REVIEW_NAME_REFERENCE_in_rulePeerReviewDef3920);
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

            otherlv_2=(Token)match(input,15,FOLLOW_15_in_rulePeerReviewDef3932); 

                	newLeafNode(otherlv_2, grammarAccess.getPeerReviewDefAccess().getLeftCurlyBracketKeyword_2());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2096:1: (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) )?
            int alt62=2;
            int LA62_0 = input.LA(1);

            if ( (LA62_0==54) ) {
                alt62=1;
            }
            switch (alt62) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2096:3: otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) )
                    {
                    otherlv_3=(Token)match(input,54,FOLLOW_54_in_rulePeerReviewDef3945); 

                        	newLeafNode(otherlv_3, grammarAccess.getPeerReviewDefAccess().getTitleKeyword_3_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2100:1: ( (lv_title_4_0= RULE_STRING ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2101:1: (lv_title_4_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2101:1: (lv_title_4_0= RULE_STRING )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2102:3: lv_title_4_0= RULE_STRING
                    {
                    lv_title_4_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rulePeerReviewDef3962); 

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

            otherlv_5=(Token)match(input,39,FOLLOW_39_in_rulePeerReviewDef3981); 

                	newLeafNode(otherlv_5, grammarAccess.getPeerReviewDefAccess().getDescriptionKeyword_4());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2122:1: ( (lv_description_6_0= RULE_STRING ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2123:1: (lv_description_6_0= RULE_STRING )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2123:1: (lv_description_6_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2124:3: lv_description_6_0= RULE_STRING
            {
            lv_description_6_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rulePeerReviewDef3998); 

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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2140:2: (otherlv_7= 'location' ( (lv_location_8_0= RULE_STRING ) ) )?
            int alt63=2;
            int LA63_0 = input.LA(1);

            if ( (LA63_0==62) ) {
                alt63=1;
            }
            switch (alt63) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2140:4: otherlv_7= 'location' ( (lv_location_8_0= RULE_STRING ) )
                    {
                    otherlv_7=(Token)match(input,62,FOLLOW_62_in_rulePeerReviewDef4016); 

                        	newLeafNode(otherlv_7, grammarAccess.getPeerReviewDefAccess().getLocationKeyword_6_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2144:1: ( (lv_location_8_0= RULE_STRING ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2145:1: (lv_location_8_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2145:1: (lv_location_8_0= RULE_STRING )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2146:3: lv_location_8_0= RULE_STRING
                    {
                    lv_location_8_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rulePeerReviewDef4033); 

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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2162:4: (otherlv_9= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )?
            int alt64=2;
            int LA64_0 = input.LA(1);

            if ( (LA64_0==55) ) {
                alt64=1;
            }
            switch (alt64) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2162:6: otherlv_9= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) )
                    {
                    otherlv_9=(Token)match(input,55,FOLLOW_55_in_rulePeerReviewDef4053); 

                        	newLeafNode(otherlv_9, grammarAccess.getPeerReviewDefAccess().getRelatedToStateKeyword_7_0());
                        
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2166:1: ( ( ruleSTATE_NAME_REFERENCE ) )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2167:1: ( ruleSTATE_NAME_REFERENCE )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2167:1: ( ruleSTATE_NAME_REFERENCE )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2168:3: ruleSTATE_NAME_REFERENCE
                    {

                    			if (current==null) {
                    	            current = createModelElement(grammarAccess.getPeerReviewDefRule());
                    	        }
                            
                     
                    	        newCompositeNode(grammarAccess.getPeerReviewDefAccess().getRelatedToStateStateDefCrossReference_7_1_0()); 
                    	    
                    pushFollow(FOLLOW_ruleSTATE_NAME_REFERENCE_in_rulePeerReviewDef4076);
                    ruleSTATE_NAME_REFERENCE();

                    state._fsp--;

                     
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }


                    }
                    break;

            }

            otherlv_11=(Token)match(input,56,FOLLOW_56_in_rulePeerReviewDef4090); 

                	newLeafNode(otherlv_11, grammarAccess.getPeerReviewDefAccess().getBlockingTypeKeyword_8());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2185:1: ( (lv_blockingType_12_0= ruleReviewBlockingType ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2186:1: (lv_blockingType_12_0= ruleReviewBlockingType )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2186:1: (lv_blockingType_12_0= ruleReviewBlockingType )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2187:3: lv_blockingType_12_0= ruleReviewBlockingType
            {
             
            	        newCompositeNode(grammarAccess.getPeerReviewDefAccess().getBlockingTypeReviewBlockingTypeEnumRuleCall_9_0()); 
            	    
            pushFollow(FOLLOW_ruleReviewBlockingType_in_rulePeerReviewDef4111);
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

            otherlv_13=(Token)match(input,57,FOLLOW_57_in_rulePeerReviewDef4123); 

                	newLeafNode(otherlv_13, grammarAccess.getPeerReviewDefAccess().getOnEventKeyword_10());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2207:1: ( (lv_stateEvent_14_0= ruleWorkflowEventType ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2208:1: (lv_stateEvent_14_0= ruleWorkflowEventType )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2208:1: (lv_stateEvent_14_0= ruleWorkflowEventType )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2209:3: lv_stateEvent_14_0= ruleWorkflowEventType
            {
             
            	        newCompositeNode(grammarAccess.getPeerReviewDefAccess().getStateEventWorkflowEventTypeEnumRuleCall_11_0()); 
            	    
            pushFollow(FOLLOW_ruleWorkflowEventType_in_rulePeerReviewDef4144);
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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2225:2: (otherlv_15= 'assignee' ( (lv_assigneeRefs_16_0= ruleUserRef ) ) )*
            loop65:
            do {
                int alt65=2;
                int LA65_0 = input.LA(1);

                if ( (LA65_0==58) ) {
                    alt65=1;
                }


                switch (alt65) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2225:4: otherlv_15= 'assignee' ( (lv_assigneeRefs_16_0= ruleUserRef ) )
            	    {
            	    otherlv_15=(Token)match(input,58,FOLLOW_58_in_rulePeerReviewDef4157); 

            	        	newLeafNode(otherlv_15, grammarAccess.getPeerReviewDefAccess().getAssigneeKeyword_12_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2229:1: ( (lv_assigneeRefs_16_0= ruleUserRef ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2230:1: (lv_assigneeRefs_16_0= ruleUserRef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2230:1: (lv_assigneeRefs_16_0= ruleUserRef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2231:3: lv_assigneeRefs_16_0= ruleUserRef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getPeerReviewDefAccess().getAssigneeRefsUserRefParserRuleCall_12_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleUserRef_in_rulePeerReviewDef4178);
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
            	    break loop65;
                }
            } while (true);

            otherlv_17=(Token)match(input,20,FOLLOW_20_in_rulePeerReviewDef4192); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2259:1: entryRuleFollowupRef returns [EObject current=null] : iv_ruleFollowupRef= ruleFollowupRef EOF ;
    public final EObject entryRuleFollowupRef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleFollowupRef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2260:2: (iv_ruleFollowupRef= ruleFollowupRef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2261:2: iv_ruleFollowupRef= ruleFollowupRef EOF
            {
             newCompositeNode(grammarAccess.getFollowupRefRule()); 
            pushFollow(FOLLOW_ruleFollowupRef_in_entryRuleFollowupRef4228);
            iv_ruleFollowupRef=ruleFollowupRef();

            state._fsp--;

             current =iv_ruleFollowupRef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleFollowupRef4238); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2268:1: ruleFollowupRef returns [EObject current=null] : (otherlv_0= 'followup by' (otherlv_1= 'assignee' ( (lv_assigneeRefs_2_0= ruleUserRef ) ) )+ ) ;
    public final EObject ruleFollowupRef() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;
        EObject lv_assigneeRefs_2_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2271:28: ( (otherlv_0= 'followup by' (otherlv_1= 'assignee' ( (lv_assigneeRefs_2_0= ruleUserRef ) ) )+ ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2272:1: (otherlv_0= 'followup by' (otherlv_1= 'assignee' ( (lv_assigneeRefs_2_0= ruleUserRef ) ) )+ )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2272:1: (otherlv_0= 'followup by' (otherlv_1= 'assignee' ( (lv_assigneeRefs_2_0= ruleUserRef ) ) )+ )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2272:3: otherlv_0= 'followup by' (otherlv_1= 'assignee' ( (lv_assigneeRefs_2_0= ruleUserRef ) ) )+
            {
            otherlv_0=(Token)match(input,63,FOLLOW_63_in_ruleFollowupRef4275); 

                	newLeafNode(otherlv_0, grammarAccess.getFollowupRefAccess().getFollowupByKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2276:1: (otherlv_1= 'assignee' ( (lv_assigneeRefs_2_0= ruleUserRef ) ) )+
            int cnt66=0;
            loop66:
            do {
                int alt66=2;
                int LA66_0 = input.LA(1);

                if ( (LA66_0==58) ) {
                    alt66=1;
                }


                switch (alt66) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2276:3: otherlv_1= 'assignee' ( (lv_assigneeRefs_2_0= ruleUserRef ) )
            	    {
            	    otherlv_1=(Token)match(input,58,FOLLOW_58_in_ruleFollowupRef4288); 

            	        	newLeafNode(otherlv_1, grammarAccess.getFollowupRefAccess().getAssigneeKeyword_1_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2280:1: ( (lv_assigneeRefs_2_0= ruleUserRef ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2281:1: (lv_assigneeRefs_2_0= ruleUserRef )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2281:1: (lv_assigneeRefs_2_0= ruleUserRef )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2282:3: lv_assigneeRefs_2_0= ruleUserRef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getFollowupRefAccess().getAssigneeRefsUserRefParserRuleCall_1_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleUserRef_in_ruleFollowupRef4309);
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
            	    if ( cnt66 >= 1 ) break loop66;
                        EarlyExitException eee =
                            new EarlyExitException(66, input);
                        throw eee;
                }
                cnt66++;
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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2306:1: entryRuleUserRef returns [EObject current=null] : iv_ruleUserRef= ruleUserRef EOF ;
    public final EObject entryRuleUserRef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleUserRef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2307:2: (iv_ruleUserRef= ruleUserRef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2308:2: iv_ruleUserRef= ruleUserRef EOF
            {
             newCompositeNode(grammarAccess.getUserRefRule()); 
            pushFollow(FOLLOW_ruleUserRef_in_entryRuleUserRef4347);
            iv_ruleUserRef=ruleUserRef();

            state._fsp--;

             current =iv_ruleUserRef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleUserRef4357); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2315:1: ruleUserRef returns [EObject current=null] : (this_UserByUserId_0= ruleUserByUserId | this_UserByName_1= ruleUserByName ) ;
    public final EObject ruleUserRef() throws RecognitionException {
        EObject current = null;

        EObject this_UserByUserId_0 = null;

        EObject this_UserByName_1 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2318:28: ( (this_UserByUserId_0= ruleUserByUserId | this_UserByName_1= ruleUserByName ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2319:1: (this_UserByUserId_0= ruleUserByUserId | this_UserByName_1= ruleUserByName )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2319:1: (this_UserByUserId_0= ruleUserByUserId | this_UserByName_1= ruleUserByName )
            int alt67=2;
            int LA67_0 = input.LA(1);

            if ( (LA67_0==17) ) {
                alt67=1;
            }
            else if ( (LA67_0==64) ) {
                alt67=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 67, 0, input);

                throw nvae;
            }
            switch (alt67) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2320:5: this_UserByUserId_0= ruleUserByUserId
                    {
                     
                            newCompositeNode(grammarAccess.getUserRefAccess().getUserByUserIdParserRuleCall_0()); 
                        
                    pushFollow(FOLLOW_ruleUserByUserId_in_ruleUserRef4404);
                    this_UserByUserId_0=ruleUserByUserId();

                    state._fsp--;

                     
                            current = this_UserByUserId_0; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2330:5: this_UserByName_1= ruleUserByName
                    {
                     
                            newCompositeNode(grammarAccess.getUserRefAccess().getUserByNameParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_ruleUserByName_in_ruleUserRef4431);
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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2346:1: entryRuleUserByUserId returns [EObject current=null] : iv_ruleUserByUserId= ruleUserByUserId EOF ;
    public final EObject entryRuleUserByUserId() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleUserByUserId = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2347:2: (iv_ruleUserByUserId= ruleUserByUserId EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2348:2: iv_ruleUserByUserId= ruleUserByUserId EOF
            {
             newCompositeNode(grammarAccess.getUserByUserIdRule()); 
            pushFollow(FOLLOW_ruleUserByUserId_in_entryRuleUserByUserId4466);
            iv_ruleUserByUserId=ruleUserByUserId();

            state._fsp--;

             current =iv_ruleUserByUserId; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleUserByUserId4476); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2355:1: ruleUserByUserId returns [EObject current=null] : (otherlv_0= 'userId' ( (lv_userId_1_0= RULE_STRING ) ) ) ;
    public final EObject ruleUserByUserId() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_userId_1_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2358:28: ( (otherlv_0= 'userId' ( (lv_userId_1_0= RULE_STRING ) ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2359:1: (otherlv_0= 'userId' ( (lv_userId_1_0= RULE_STRING ) ) )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2359:1: (otherlv_0= 'userId' ( (lv_userId_1_0= RULE_STRING ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2359:3: otherlv_0= 'userId' ( (lv_userId_1_0= RULE_STRING ) )
            {
            otherlv_0=(Token)match(input,17,FOLLOW_17_in_ruleUserByUserId4513); 

                	newLeafNode(otherlv_0, grammarAccess.getUserByUserIdAccess().getUserIdKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2363:1: ( (lv_userId_1_0= RULE_STRING ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2364:1: (lv_userId_1_0= RULE_STRING )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2364:1: (lv_userId_1_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2365:3: lv_userId_1_0= RULE_STRING
            {
            lv_userId_1_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleUserByUserId4530); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2389:1: entryRuleUserByName returns [EObject current=null] : iv_ruleUserByName= ruleUserByName EOF ;
    public final EObject entryRuleUserByName() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleUserByName = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2390:2: (iv_ruleUserByName= ruleUserByName EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2391:2: iv_ruleUserByName= ruleUserByName EOF
            {
             newCompositeNode(grammarAccess.getUserByNameRule()); 
            pushFollow(FOLLOW_ruleUserByName_in_entryRuleUserByName4571);
            iv_ruleUserByName=ruleUserByName();

            state._fsp--;

             current =iv_ruleUserByName; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleUserByName4581); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2398:1: ruleUserByName returns [EObject current=null] : (otherlv_0= 'named' ( (lv_name_1_0= RULE_STRING ) ) ) ;
    public final EObject ruleUserByName() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_name_1_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2401:28: ( (otherlv_0= 'named' ( (lv_name_1_0= RULE_STRING ) ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2402:1: (otherlv_0= 'named' ( (lv_name_1_0= RULE_STRING ) ) )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2402:1: (otherlv_0= 'named' ( (lv_name_1_0= RULE_STRING ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2402:3: otherlv_0= 'named' ( (lv_name_1_0= RULE_STRING ) )
            {
            otherlv_0=(Token)match(input,64,FOLLOW_64_in_ruleUserByName4618); 

                	newLeafNode(otherlv_0, grammarAccess.getUserByNameAccess().getNamedKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2406:1: ( (lv_name_1_0= RULE_STRING ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2407:1: (lv_name_1_0= RULE_STRING )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2407:1: (lv_name_1_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2408:3: lv_name_1_0= RULE_STRING
            {
            lv_name_1_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleUserByName4635); 

            			newLeafNode(lv_name_1_0, grammarAccess.getUserByNameAccess().getNameSTRINGTerminalRuleCall_1_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getUserByNameRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"name",
                    		lv_name_1_0, 
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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2432:1: entryRuleDECISION_REVIEW_NAME_REFERENCE returns [String current=null] : iv_ruleDECISION_REVIEW_NAME_REFERENCE= ruleDECISION_REVIEW_NAME_REFERENCE EOF ;
    public final String entryRuleDECISION_REVIEW_NAME_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleDECISION_REVIEW_NAME_REFERENCE = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2433:2: (iv_ruleDECISION_REVIEW_NAME_REFERENCE= ruleDECISION_REVIEW_NAME_REFERENCE EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2434:2: iv_ruleDECISION_REVIEW_NAME_REFERENCE= ruleDECISION_REVIEW_NAME_REFERENCE EOF
            {
             newCompositeNode(grammarAccess.getDECISION_REVIEW_NAME_REFERENCERule()); 
            pushFollow(FOLLOW_ruleDECISION_REVIEW_NAME_REFERENCE_in_entryRuleDECISION_REVIEW_NAME_REFERENCE4677);
            iv_ruleDECISION_REVIEW_NAME_REFERENCE=ruleDECISION_REVIEW_NAME_REFERENCE();

            state._fsp--;

             current =iv_ruleDECISION_REVIEW_NAME_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleDECISION_REVIEW_NAME_REFERENCE4688); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2441:1: ruleDECISION_REVIEW_NAME_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleDECISION_REVIEW_NAME_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2444:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2445:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleDECISION_REVIEW_NAME_REFERENCE4727); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2460:1: entryRulePEER_REVIEW_NAME_REFERENCE returns [String current=null] : iv_rulePEER_REVIEW_NAME_REFERENCE= rulePEER_REVIEW_NAME_REFERENCE EOF ;
    public final String entryRulePEER_REVIEW_NAME_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_rulePEER_REVIEW_NAME_REFERENCE = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2461:2: (iv_rulePEER_REVIEW_NAME_REFERENCE= rulePEER_REVIEW_NAME_REFERENCE EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2462:2: iv_rulePEER_REVIEW_NAME_REFERENCE= rulePEER_REVIEW_NAME_REFERENCE EOF
            {
             newCompositeNode(grammarAccess.getPEER_REVIEW_NAME_REFERENCERule()); 
            pushFollow(FOLLOW_rulePEER_REVIEW_NAME_REFERENCE_in_entryRulePEER_REVIEW_NAME_REFERENCE4772);
            iv_rulePEER_REVIEW_NAME_REFERENCE=rulePEER_REVIEW_NAME_REFERENCE();

            state._fsp--;

             current =iv_rulePEER_REVIEW_NAME_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRulePEER_REVIEW_NAME_REFERENCE4783); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2469:1: rulePEER_REVIEW_NAME_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken rulePEER_REVIEW_NAME_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2472:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2473:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rulePEER_REVIEW_NAME_REFERENCE4822); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2488:1: entryRuleSTATE_NAME_REFERENCE returns [String current=null] : iv_ruleSTATE_NAME_REFERENCE= ruleSTATE_NAME_REFERENCE EOF ;
    public final String entryRuleSTATE_NAME_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleSTATE_NAME_REFERENCE = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2489:2: (iv_ruleSTATE_NAME_REFERENCE= ruleSTATE_NAME_REFERENCE EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2490:2: iv_ruleSTATE_NAME_REFERENCE= ruleSTATE_NAME_REFERENCE EOF
            {
             newCompositeNode(grammarAccess.getSTATE_NAME_REFERENCERule()); 
            pushFollow(FOLLOW_ruleSTATE_NAME_REFERENCE_in_entryRuleSTATE_NAME_REFERENCE4867);
            iv_ruleSTATE_NAME_REFERENCE=ruleSTATE_NAME_REFERENCE();

            state._fsp--;

             current =iv_ruleSTATE_NAME_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleSTATE_NAME_REFERENCE4878); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2497:1: ruleSTATE_NAME_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleSTATE_NAME_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2500:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2501:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleSTATE_NAME_REFERENCE4917); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2516:1: entryRuleWIDGET_NAME_REFERENCE returns [String current=null] : iv_ruleWIDGET_NAME_REFERENCE= ruleWIDGET_NAME_REFERENCE EOF ;
    public final String entryRuleWIDGET_NAME_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleWIDGET_NAME_REFERENCE = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2517:2: (iv_ruleWIDGET_NAME_REFERENCE= ruleWIDGET_NAME_REFERENCE EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2518:2: iv_ruleWIDGET_NAME_REFERENCE= ruleWIDGET_NAME_REFERENCE EOF
            {
             newCompositeNode(grammarAccess.getWIDGET_NAME_REFERENCERule()); 
            pushFollow(FOLLOW_ruleWIDGET_NAME_REFERENCE_in_entryRuleWIDGET_NAME_REFERENCE4962);
            iv_ruleWIDGET_NAME_REFERENCE=ruleWIDGET_NAME_REFERENCE();

            state._fsp--;

             current =iv_ruleWIDGET_NAME_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleWIDGET_NAME_REFERENCE4973); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2525:1: ruleWIDGET_NAME_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleWIDGET_NAME_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2528:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2529:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleWIDGET_NAME_REFERENCE5012); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2544:1: entryRuleWORK_DEFINITION_NAME_REFERENCE returns [String current=null] : iv_ruleWORK_DEFINITION_NAME_REFERENCE= ruleWORK_DEFINITION_NAME_REFERENCE EOF ;
    public final String entryRuleWORK_DEFINITION_NAME_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleWORK_DEFINITION_NAME_REFERENCE = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2545:2: (iv_ruleWORK_DEFINITION_NAME_REFERENCE= ruleWORK_DEFINITION_NAME_REFERENCE EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2546:2: iv_ruleWORK_DEFINITION_NAME_REFERENCE= ruleWORK_DEFINITION_NAME_REFERENCE EOF
            {
             newCompositeNode(grammarAccess.getWORK_DEFINITION_NAME_REFERENCERule()); 
            pushFollow(FOLLOW_ruleWORK_DEFINITION_NAME_REFERENCE_in_entryRuleWORK_DEFINITION_NAME_REFERENCE5057);
            iv_ruleWORK_DEFINITION_NAME_REFERENCE=ruleWORK_DEFINITION_NAME_REFERENCE();

            state._fsp--;

             current =iv_ruleWORK_DEFINITION_NAME_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleWORK_DEFINITION_NAME_REFERENCE5068); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2553:1: ruleWORK_DEFINITION_NAME_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleWORK_DEFINITION_NAME_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2556:28: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2557:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleWORK_DEFINITION_NAME_REFERENCE5107); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2572:1: entryRuleToState returns [EObject current=null] : iv_ruleToState= ruleToState EOF ;
    public final EObject entryRuleToState() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleToState = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2573:2: (iv_ruleToState= ruleToState EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2574:2: iv_ruleToState= ruleToState EOF
            {
             newCompositeNode(grammarAccess.getToStateRule()); 
            pushFollow(FOLLOW_ruleToState_in_entryRuleToState5151);
            iv_ruleToState=ruleToState();

            state._fsp--;

             current =iv_ruleToState; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleToState5161); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2581:1: ruleToState returns [EObject current=null] : (otherlv_0= 'to' ( ( ruleSTATE_NAME_REFERENCE ) ) ( (lv_options_2_0= ruleTransitionOption ) )* ) ;
    public final EObject ruleToState() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        AntlrDatatypeRuleToken lv_options_2_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2584:28: ( (otherlv_0= 'to' ( ( ruleSTATE_NAME_REFERENCE ) ) ( (lv_options_2_0= ruleTransitionOption ) )* ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2585:1: (otherlv_0= 'to' ( ( ruleSTATE_NAME_REFERENCE ) ) ( (lv_options_2_0= ruleTransitionOption ) )* )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2585:1: (otherlv_0= 'to' ( ( ruleSTATE_NAME_REFERENCE ) ) ( (lv_options_2_0= ruleTransitionOption ) )* )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2585:3: otherlv_0= 'to' ( ( ruleSTATE_NAME_REFERENCE ) ) ( (lv_options_2_0= ruleTransitionOption ) )*
            {
            otherlv_0=(Token)match(input,65,FOLLOW_65_in_ruleToState5198); 

                	newLeafNode(otherlv_0, grammarAccess.getToStateAccess().getToKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2589:1: ( ( ruleSTATE_NAME_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2590:1: ( ruleSTATE_NAME_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2590:1: ( ruleSTATE_NAME_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2591:3: ruleSTATE_NAME_REFERENCE
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getToStateRule());
            	        }
                    
             
            	        newCompositeNode(grammarAccess.getToStateAccess().getStateStateDefCrossReference_1_0()); 
            	    
            pushFollow(FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleToState5221);
            ruleSTATE_NAME_REFERENCE();

            state._fsp--;

             
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2604:2: ( (lv_options_2_0= ruleTransitionOption ) )*
            loop68:
            do {
                int alt68=2;
                int LA68_0 = input.LA(1);

                if ( (LA68_0==RULE_STRING||(LA68_0>=72 && LA68_0<=73)) ) {
                    alt68=1;
                }


                switch (alt68) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2605:1: (lv_options_2_0= ruleTransitionOption )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2605:1: (lv_options_2_0= ruleTransitionOption )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2606:3: lv_options_2_0= ruleTransitionOption
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getToStateAccess().getOptionsTransitionOptionParserRuleCall_2_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleTransitionOption_in_ruleToState5242);
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
            	    break loop68;
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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2630:1: entryRuleLayoutType returns [EObject current=null] : iv_ruleLayoutType= ruleLayoutType EOF ;
    public final EObject entryRuleLayoutType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleLayoutType = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2631:2: (iv_ruleLayoutType= ruleLayoutType EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2632:2: iv_ruleLayoutType= ruleLayoutType EOF
            {
             newCompositeNode(grammarAccess.getLayoutTypeRule()); 
            pushFollow(FOLLOW_ruleLayoutType_in_entryRuleLayoutType5279);
            iv_ruleLayoutType=ruleLayoutType();

            state._fsp--;

             current =iv_ruleLayoutType; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleLayoutType5289); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2639:1: ruleLayoutType returns [EObject current=null] : (this_LayoutDef_0= ruleLayoutDef | this_LayoutCopy_1= ruleLayoutCopy ) ;
    public final EObject ruleLayoutType() throws RecognitionException {
        EObject current = null;

        EObject this_LayoutDef_0 = null;

        EObject this_LayoutCopy_1 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2642:28: ( (this_LayoutDef_0= ruleLayoutDef | this_LayoutCopy_1= ruleLayoutCopy ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2643:1: (this_LayoutDef_0= ruleLayoutDef | this_LayoutCopy_1= ruleLayoutCopy )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2643:1: (this_LayoutDef_0= ruleLayoutDef | this_LayoutCopy_1= ruleLayoutCopy )
            int alt69=2;
            int LA69_0 = input.LA(1);

            if ( (LA69_0==66) ) {
                alt69=1;
            }
            else if ( (LA69_0==67) ) {
                alt69=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 69, 0, input);

                throw nvae;
            }
            switch (alt69) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2644:5: this_LayoutDef_0= ruleLayoutDef
                    {
                     
                            newCompositeNode(grammarAccess.getLayoutTypeAccess().getLayoutDefParserRuleCall_0()); 
                        
                    pushFollow(FOLLOW_ruleLayoutDef_in_ruleLayoutType5336);
                    this_LayoutDef_0=ruleLayoutDef();

                    state._fsp--;

                     
                            current = this_LayoutDef_0; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2654:5: this_LayoutCopy_1= ruleLayoutCopy
                    {
                     
                            newCompositeNode(grammarAccess.getLayoutTypeAccess().getLayoutCopyParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_ruleLayoutCopy_in_ruleLayoutType5363);
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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2670:1: entryRuleLayoutDef returns [EObject current=null] : iv_ruleLayoutDef= ruleLayoutDef EOF ;
    public final EObject entryRuleLayoutDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleLayoutDef = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2671:2: (iv_ruleLayoutDef= ruleLayoutDef EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2672:2: iv_ruleLayoutDef= ruleLayoutDef EOF
            {
             newCompositeNode(grammarAccess.getLayoutDefRule()); 
            pushFollow(FOLLOW_ruleLayoutDef_in_entryRuleLayoutDef5398);
            iv_ruleLayoutDef=ruleLayoutDef();

            state._fsp--;

             current =iv_ruleLayoutDef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleLayoutDef5408); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2679:1: ruleLayoutDef returns [EObject current=null] : (otherlv_0= 'layout' otherlv_1= '{' ( (lv_layoutItems_2_0= ruleLayoutItem ) )+ otherlv_3= '}' ) ;
    public final EObject ruleLayoutDef() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;
        Token otherlv_3=null;
        EObject lv_layoutItems_2_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2682:28: ( (otherlv_0= 'layout' otherlv_1= '{' ( (lv_layoutItems_2_0= ruleLayoutItem ) )+ otherlv_3= '}' ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2683:1: (otherlv_0= 'layout' otherlv_1= '{' ( (lv_layoutItems_2_0= ruleLayoutItem ) )+ otherlv_3= '}' )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2683:1: (otherlv_0= 'layout' otherlv_1= '{' ( (lv_layoutItems_2_0= ruleLayoutItem ) )+ otherlv_3= '}' )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2683:3: otherlv_0= 'layout' otherlv_1= '{' ( (lv_layoutItems_2_0= ruleLayoutItem ) )+ otherlv_3= '}'
            {
            otherlv_0=(Token)match(input,66,FOLLOW_66_in_ruleLayoutDef5445); 

                	newLeafNode(otherlv_0, grammarAccess.getLayoutDefAccess().getLayoutKeyword_0());
                
            otherlv_1=(Token)match(input,15,FOLLOW_15_in_ruleLayoutDef5457); 

                	newLeafNode(otherlv_1, grammarAccess.getLayoutDefAccess().getLeftCurlyBracketKeyword_1());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2691:1: ( (lv_layoutItems_2_0= ruleLayoutItem ) )+
            int cnt70=0;
            loop70:
            do {
                int alt70=2;
                int LA70_0 = input.LA(1);

                if ( ((LA70_0>=44 && LA70_0<=45)||LA70_0==68) ) {
                    alt70=1;
                }


                switch (alt70) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2692:1: (lv_layoutItems_2_0= ruleLayoutItem )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2692:1: (lv_layoutItems_2_0= ruleLayoutItem )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2693:3: lv_layoutItems_2_0= ruleLayoutItem
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getLayoutDefAccess().getLayoutItemsLayoutItemParserRuleCall_2_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleLayoutItem_in_ruleLayoutDef5478);
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
            	    if ( cnt70 >= 1 ) break loop70;
                        EarlyExitException eee =
                            new EarlyExitException(70, input);
                        throw eee;
                }
                cnt70++;
            } while (true);

            otherlv_3=(Token)match(input,20,FOLLOW_20_in_ruleLayoutDef5491); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2721:1: entryRuleLayoutCopy returns [EObject current=null] : iv_ruleLayoutCopy= ruleLayoutCopy EOF ;
    public final EObject entryRuleLayoutCopy() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleLayoutCopy = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2722:2: (iv_ruleLayoutCopy= ruleLayoutCopy EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2723:2: iv_ruleLayoutCopy= ruleLayoutCopy EOF
            {
             newCompositeNode(grammarAccess.getLayoutCopyRule()); 
            pushFollow(FOLLOW_ruleLayoutCopy_in_entryRuleLayoutCopy5527);
            iv_ruleLayoutCopy=ruleLayoutCopy();

            state._fsp--;

             current =iv_ruleLayoutCopy; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleLayoutCopy5537); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2730:1: ruleLayoutCopy returns [EObject current=null] : (otherlv_0= 'layoutCopyFrom' ( ( ruleSTATE_NAME_REFERENCE ) ) ) ;
    public final EObject ruleLayoutCopy() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2733:28: ( (otherlv_0= 'layoutCopyFrom' ( ( ruleSTATE_NAME_REFERENCE ) ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2734:1: (otherlv_0= 'layoutCopyFrom' ( ( ruleSTATE_NAME_REFERENCE ) ) )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2734:1: (otherlv_0= 'layoutCopyFrom' ( ( ruleSTATE_NAME_REFERENCE ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2734:3: otherlv_0= 'layoutCopyFrom' ( ( ruleSTATE_NAME_REFERENCE ) )
            {
            otherlv_0=(Token)match(input,67,FOLLOW_67_in_ruleLayoutCopy5574); 

                	newLeafNode(otherlv_0, grammarAccess.getLayoutCopyAccess().getLayoutCopyFromKeyword_0());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2738:1: ( ( ruleSTATE_NAME_REFERENCE ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2739:1: ( ruleSTATE_NAME_REFERENCE )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2739:1: ( ruleSTATE_NAME_REFERENCE )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2740:3: ruleSTATE_NAME_REFERENCE
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getLayoutCopyRule());
            	        }
                    
             
            	        newCompositeNode(grammarAccess.getLayoutCopyAccess().getStateStateDefCrossReference_1_0()); 
            	    
            pushFollow(FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleLayoutCopy5597);
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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2761:1: entryRuleLayoutItem returns [EObject current=null] : iv_ruleLayoutItem= ruleLayoutItem EOF ;
    public final EObject entryRuleLayoutItem() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleLayoutItem = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2762:2: (iv_ruleLayoutItem= ruleLayoutItem EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2763:2: iv_ruleLayoutItem= ruleLayoutItem EOF
            {
             newCompositeNode(grammarAccess.getLayoutItemRule()); 
            pushFollow(FOLLOW_ruleLayoutItem_in_entryRuleLayoutItem5633);
            iv_ruleLayoutItem=ruleLayoutItem();

            state._fsp--;

             current =iv_ruleLayoutItem; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleLayoutItem5643); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2770:1: ruleLayoutItem returns [EObject current=null] : (this_WidgetRef_0= ruleWidgetRef | this_AttrWidget_1= ruleAttrWidget | this_Composite_2= ruleComposite ) ;
    public final EObject ruleLayoutItem() throws RecognitionException {
        EObject current = null;

        EObject this_WidgetRef_0 = null;

        EObject this_AttrWidget_1 = null;

        EObject this_Composite_2 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2773:28: ( (this_WidgetRef_0= ruleWidgetRef | this_AttrWidget_1= ruleAttrWidget | this_Composite_2= ruleComposite ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2774:1: (this_WidgetRef_0= ruleWidgetRef | this_AttrWidget_1= ruleAttrWidget | this_Composite_2= ruleComposite )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2774:1: (this_WidgetRef_0= ruleWidgetRef | this_AttrWidget_1= ruleAttrWidget | this_Composite_2= ruleComposite )
            int alt71=3;
            switch ( input.LA(1) ) {
            case 44:
                {
                alt71=1;
                }
                break;
            case 45:
                {
                alt71=2;
                }
                break;
            case 68:
                {
                alt71=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 71, 0, input);

                throw nvae;
            }

            switch (alt71) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2775:5: this_WidgetRef_0= ruleWidgetRef
                    {
                     
                            newCompositeNode(grammarAccess.getLayoutItemAccess().getWidgetRefParserRuleCall_0()); 
                        
                    pushFollow(FOLLOW_ruleWidgetRef_in_ruleLayoutItem5690);
                    this_WidgetRef_0=ruleWidgetRef();

                    state._fsp--;

                     
                            current = this_WidgetRef_0; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2785:5: this_AttrWidget_1= ruleAttrWidget
                    {
                     
                            newCompositeNode(grammarAccess.getLayoutItemAccess().getAttrWidgetParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_ruleAttrWidget_in_ruleLayoutItem5717);
                    this_AttrWidget_1=ruleAttrWidget();

                    state._fsp--;

                     
                            current = this_AttrWidget_1; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2795:5: this_Composite_2= ruleComposite
                    {
                     
                            newCompositeNode(grammarAccess.getLayoutItemAccess().getCompositeParserRuleCall_2()); 
                        
                    pushFollow(FOLLOW_ruleComposite_in_ruleLayoutItem5744);
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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2811:1: entryRuleComposite returns [EObject current=null] : iv_ruleComposite= ruleComposite EOF ;
    public final EObject entryRuleComposite() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleComposite = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2812:2: (iv_ruleComposite= ruleComposite EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2813:2: iv_ruleComposite= ruleComposite EOF
            {
             newCompositeNode(grammarAccess.getCompositeRule()); 
            pushFollow(FOLLOW_ruleComposite_in_entryRuleComposite5779);
            iv_ruleComposite=ruleComposite();

            state._fsp--;

             current =iv_ruleComposite; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleComposite5789); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2820:1: ruleComposite returns [EObject current=null] : (otherlv_0= 'composite' otherlv_1= '{' otherlv_2= 'numColumns' ( (lv_numColumns_3_0= RULE_INT ) ) ( (lv_layoutItems_4_0= ruleLayoutItem ) )+ (otherlv_5= 'option' ( (lv_options_6_0= ruleCompositeOption ) ) )* otherlv_7= '}' ) ;
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
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2823:28: ( (otherlv_0= 'composite' otherlv_1= '{' otherlv_2= 'numColumns' ( (lv_numColumns_3_0= RULE_INT ) ) ( (lv_layoutItems_4_0= ruleLayoutItem ) )+ (otherlv_5= 'option' ( (lv_options_6_0= ruleCompositeOption ) ) )* otherlv_7= '}' ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2824:1: (otherlv_0= 'composite' otherlv_1= '{' otherlv_2= 'numColumns' ( (lv_numColumns_3_0= RULE_INT ) ) ( (lv_layoutItems_4_0= ruleLayoutItem ) )+ (otherlv_5= 'option' ( (lv_options_6_0= ruleCompositeOption ) ) )* otherlv_7= '}' )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2824:1: (otherlv_0= 'composite' otherlv_1= '{' otherlv_2= 'numColumns' ( (lv_numColumns_3_0= RULE_INT ) ) ( (lv_layoutItems_4_0= ruleLayoutItem ) )+ (otherlv_5= 'option' ( (lv_options_6_0= ruleCompositeOption ) ) )* otherlv_7= '}' )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2824:3: otherlv_0= 'composite' otherlv_1= '{' otherlv_2= 'numColumns' ( (lv_numColumns_3_0= RULE_INT ) ) ( (lv_layoutItems_4_0= ruleLayoutItem ) )+ (otherlv_5= 'option' ( (lv_options_6_0= ruleCompositeOption ) ) )* otherlv_7= '}'
            {
            otherlv_0=(Token)match(input,68,FOLLOW_68_in_ruleComposite5826); 

                	newLeafNode(otherlv_0, grammarAccess.getCompositeAccess().getCompositeKeyword_0());
                
            otherlv_1=(Token)match(input,15,FOLLOW_15_in_ruleComposite5838); 

                	newLeafNode(otherlv_1, grammarAccess.getCompositeAccess().getLeftCurlyBracketKeyword_1());
                
            otherlv_2=(Token)match(input,69,FOLLOW_69_in_ruleComposite5850); 

                	newLeafNode(otherlv_2, grammarAccess.getCompositeAccess().getNumColumnsKeyword_2());
                
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2836:1: ( (lv_numColumns_3_0= RULE_INT ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2837:1: (lv_numColumns_3_0= RULE_INT )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2837:1: (lv_numColumns_3_0= RULE_INT )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2838:3: lv_numColumns_3_0= RULE_INT
            {
            lv_numColumns_3_0=(Token)match(input,RULE_INT,FOLLOW_RULE_INT_in_ruleComposite5867); 

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

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2854:2: ( (lv_layoutItems_4_0= ruleLayoutItem ) )+
            int cnt72=0;
            loop72:
            do {
                int alt72=2;
                int LA72_0 = input.LA(1);

                if ( ((LA72_0>=44 && LA72_0<=45)||LA72_0==68) ) {
                    alt72=1;
                }


                switch (alt72) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2855:1: (lv_layoutItems_4_0= ruleLayoutItem )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2855:1: (lv_layoutItems_4_0= ruleLayoutItem )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2856:3: lv_layoutItems_4_0= ruleLayoutItem
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getCompositeAccess().getLayoutItemsLayoutItemParserRuleCall_4_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleLayoutItem_in_ruleComposite5893);
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
            	    if ( cnt72 >= 1 ) break loop72;
                        EarlyExitException eee =
                            new EarlyExitException(72, input);
                        throw eee;
                }
                cnt72++;
            } while (true);

            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2872:3: (otherlv_5= 'option' ( (lv_options_6_0= ruleCompositeOption ) ) )*
            loop73:
            do {
                int alt73=2;
                int LA73_0 = input.LA(1);

                if ( (LA73_0==43) ) {
                    alt73=1;
                }


                switch (alt73) {
            	case 1 :
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2872:5: otherlv_5= 'option' ( (lv_options_6_0= ruleCompositeOption ) )
            	    {
            	    otherlv_5=(Token)match(input,43,FOLLOW_43_in_ruleComposite5907); 

            	        	newLeafNode(otherlv_5, grammarAccess.getCompositeAccess().getOptionKeyword_5_0());
            	        
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2876:1: ( (lv_options_6_0= ruleCompositeOption ) )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2877:1: (lv_options_6_0= ruleCompositeOption )
            	    {
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2877:1: (lv_options_6_0= ruleCompositeOption )
            	    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2878:3: lv_options_6_0= ruleCompositeOption
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getCompositeAccess().getOptionsCompositeOptionParserRuleCall_5_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleCompositeOption_in_ruleComposite5928);
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
            	    break loop73;
                }
            } while (true);

            otherlv_7=(Token)match(input,20,FOLLOW_20_in_ruleComposite5942); 

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


    // $ANTLR start "entryRuleUserDefOption"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2906:1: entryRuleUserDefOption returns [String current=null] : iv_ruleUserDefOption= ruleUserDefOption EOF ;
    public final String entryRuleUserDefOption() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleUserDefOption = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2907:2: (iv_ruleUserDefOption= ruleUserDefOption EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2908:2: iv_ruleUserDefOption= ruleUserDefOption EOF
            {
             newCompositeNode(grammarAccess.getUserDefOptionRule()); 
            pushFollow(FOLLOW_ruleUserDefOption_in_entryRuleUserDefOption5979);
            iv_ruleUserDefOption=ruleUserDefOption();

            state._fsp--;

             current =iv_ruleUserDefOption.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleUserDefOption5990); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2915:1: ruleUserDefOption returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'GetOrCreate' | this_STRING_1= RULE_STRING ) ;
    public final AntlrDatatypeRuleToken ruleUserDefOption() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        Token this_STRING_1=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2918:28: ( (kw= 'GetOrCreate' | this_STRING_1= RULE_STRING ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2919:1: (kw= 'GetOrCreate' | this_STRING_1= RULE_STRING )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2919:1: (kw= 'GetOrCreate' | this_STRING_1= RULE_STRING )
            int alt74=2;
            int LA74_0 = input.LA(1);

            if ( (LA74_0==70) ) {
                alt74=1;
            }
            else if ( (LA74_0==RULE_STRING) ) {
                alt74=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 74, 0, input);

                throw nvae;
            }
            switch (alt74) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2920:2: kw= 'GetOrCreate'
                    {
                    kw=(Token)match(input,70,FOLLOW_70_in_ruleUserDefOption6028); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getUserDefOptionAccess().getGetOrCreateKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2926:10: this_STRING_1= RULE_STRING
                    {
                    this_STRING_1=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleUserDefOption6049); 

                    		current.merge(this_STRING_1);
                        
                     
                        newLeafNode(this_STRING_1, grammarAccess.getUserDefOptionAccess().getSTRINGTerminalRuleCall_1()); 
                        

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


    // $ANTLR start "entryRuleTeamDefOption"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2941:1: entryRuleTeamDefOption returns [String current=null] : iv_ruleTeamDefOption= ruleTeamDefOption EOF ;
    public final String entryRuleTeamDefOption() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleTeamDefOption = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2942:2: (iv_ruleTeamDefOption= ruleTeamDefOption EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2943:2: iv_ruleTeamDefOption= ruleTeamDefOption EOF
            {
             newCompositeNode(grammarAccess.getTeamDefOptionRule()); 
            pushFollow(FOLLOW_ruleTeamDefOption_in_entryRuleTeamDefOption6095);
            iv_ruleTeamDefOption=ruleTeamDefOption();

            state._fsp--;

             current =iv_ruleTeamDefOption.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleTeamDefOption6106); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2950:1: ruleTeamDefOption returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'GetOrCreate' | this_STRING_1= RULE_STRING ) ;
    public final AntlrDatatypeRuleToken ruleTeamDefOption() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        Token this_STRING_1=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2953:28: ( (kw= 'GetOrCreate' | this_STRING_1= RULE_STRING ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2954:1: (kw= 'GetOrCreate' | this_STRING_1= RULE_STRING )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2954:1: (kw= 'GetOrCreate' | this_STRING_1= RULE_STRING )
            int alt75=2;
            int LA75_0 = input.LA(1);

            if ( (LA75_0==70) ) {
                alt75=1;
            }
            else if ( (LA75_0==RULE_STRING) ) {
                alt75=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 75, 0, input);

                throw nvae;
            }
            switch (alt75) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2955:2: kw= 'GetOrCreate'
                    {
                    kw=(Token)match(input,70,FOLLOW_70_in_ruleTeamDefOption6144); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getTeamDefOptionAccess().getGetOrCreateKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2961:10: this_STRING_1= RULE_STRING
                    {
                    this_STRING_1=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleTeamDefOption6165); 

                    		current.merge(this_STRING_1);
                        
                     
                        newLeafNode(this_STRING_1, grammarAccess.getTeamDefOptionAccess().getSTRINGTerminalRuleCall_1()); 
                        

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


    // $ANTLR start "entryRuleActionableItemOption"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2976:1: entryRuleActionableItemOption returns [String current=null] : iv_ruleActionableItemOption= ruleActionableItemOption EOF ;
    public final String entryRuleActionableItemOption() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleActionableItemOption = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2977:2: (iv_ruleActionableItemOption= ruleActionableItemOption EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2978:2: iv_ruleActionableItemOption= ruleActionableItemOption EOF
            {
             newCompositeNode(grammarAccess.getActionableItemOptionRule()); 
            pushFollow(FOLLOW_ruleActionableItemOption_in_entryRuleActionableItemOption6211);
            iv_ruleActionableItemOption=ruleActionableItemOption();

            state._fsp--;

             current =iv_ruleActionableItemOption.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleActionableItemOption6222); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2985:1: ruleActionableItemOption returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'GetOrCreate' | this_STRING_1= RULE_STRING ) ;
    public final AntlrDatatypeRuleToken ruleActionableItemOption() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        Token this_STRING_1=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2988:28: ( (kw= 'GetOrCreate' | this_STRING_1= RULE_STRING ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2989:1: (kw= 'GetOrCreate' | this_STRING_1= RULE_STRING )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2989:1: (kw= 'GetOrCreate' | this_STRING_1= RULE_STRING )
            int alt76=2;
            int LA76_0 = input.LA(1);

            if ( (LA76_0==70) ) {
                alt76=1;
            }
            else if ( (LA76_0==RULE_STRING) ) {
                alt76=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 76, 0, input);

                throw nvae;
            }
            switch (alt76) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2990:2: kw= 'GetOrCreate'
                    {
                    kw=(Token)match(input,70,FOLLOW_70_in_ruleActionableItemOption6260); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getActionableItemOptionAccess().getGetOrCreateKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:2996:10: this_STRING_1= RULE_STRING
                    {
                    this_STRING_1=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleActionableItemOption6281); 

                    		current.merge(this_STRING_1);
                        
                     
                        newLeafNode(this_STRING_1, grammarAccess.getActionableItemOptionAccess().getSTRINGTerminalRuleCall_1()); 
                        

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


    // $ANTLR start "entryRuleCompositeOption"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3011:1: entryRuleCompositeOption returns [String current=null] : iv_ruleCompositeOption= ruleCompositeOption EOF ;
    public final String entryRuleCompositeOption() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleCompositeOption = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3012:2: (iv_ruleCompositeOption= ruleCompositeOption EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3013:2: iv_ruleCompositeOption= ruleCompositeOption EOF
            {
             newCompositeNode(grammarAccess.getCompositeOptionRule()); 
            pushFollow(FOLLOW_ruleCompositeOption_in_entryRuleCompositeOption6327);
            iv_ruleCompositeOption=ruleCompositeOption();

            state._fsp--;

             current =iv_ruleCompositeOption.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleCompositeOption6338); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3020:1: ruleCompositeOption returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'None' | this_STRING_1= RULE_STRING ) ;
    public final AntlrDatatypeRuleToken ruleCompositeOption() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        Token this_STRING_1=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3023:28: ( (kw= 'None' | this_STRING_1= RULE_STRING ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3024:1: (kw= 'None' | this_STRING_1= RULE_STRING )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3024:1: (kw= 'None' | this_STRING_1= RULE_STRING )
            int alt77=2;
            int LA77_0 = input.LA(1);

            if ( (LA77_0==71) ) {
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
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3025:2: kw= 'None'
                    {
                    kw=(Token)match(input,71,FOLLOW_71_in_ruleCompositeOption6376); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getCompositeOptionAccess().getNoneKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3031:10: this_STRING_1= RULE_STRING
                    {
                    this_STRING_1=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleCompositeOption6397); 

                    		current.merge(this_STRING_1);
                        
                     
                        newLeafNode(this_STRING_1, grammarAccess.getCompositeOptionAccess().getSTRINGTerminalRuleCall_1()); 
                        

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


    // $ANTLR start "entryRuleTransitionOption"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3046:1: entryRuleTransitionOption returns [String current=null] : iv_ruleTransitionOption= ruleTransitionOption EOF ;
    public final String entryRuleTransitionOption() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleTransitionOption = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3047:2: (iv_ruleTransitionOption= ruleTransitionOption EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3048:2: iv_ruleTransitionOption= ruleTransitionOption EOF
            {
             newCompositeNode(grammarAccess.getTransitionOptionRule()); 
            pushFollow(FOLLOW_ruleTransitionOption_in_entryRuleTransitionOption6443);
            iv_ruleTransitionOption=ruleTransitionOption();

            state._fsp--;

             current =iv_ruleTransitionOption.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleTransitionOption6454); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3055:1: ruleTransitionOption returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'AsDefault' | kw= 'OverrideAttributeValidation' | this_STRING_2= RULE_STRING ) ;
    public final AntlrDatatypeRuleToken ruleTransitionOption() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        Token this_STRING_2=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3058:28: ( (kw= 'AsDefault' | kw= 'OverrideAttributeValidation' | this_STRING_2= RULE_STRING ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3059:1: (kw= 'AsDefault' | kw= 'OverrideAttributeValidation' | this_STRING_2= RULE_STRING )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3059:1: (kw= 'AsDefault' | kw= 'OverrideAttributeValidation' | this_STRING_2= RULE_STRING )
            int alt78=3;
            switch ( input.LA(1) ) {
            case 72:
                {
                alt78=1;
                }
                break;
            case 73:
                {
                alt78=2;
                }
                break;
            case RULE_STRING:
                {
                alt78=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 78, 0, input);

                throw nvae;
            }

            switch (alt78) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3060:2: kw= 'AsDefault'
                    {
                    kw=(Token)match(input,72,FOLLOW_72_in_ruleTransitionOption6492); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getTransitionOptionAccess().getAsDefaultKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3067:2: kw= 'OverrideAttributeValidation'
                    {
                    kw=(Token)match(input,73,FOLLOW_73_in_ruleTransitionOption6511); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getTransitionOptionAccess().getOverrideAttributeValidationKeyword_1()); 
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3073:10: this_STRING_2= RULE_STRING
                    {
                    this_STRING_2=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleTransitionOption6532); 

                    		current.merge(this_STRING_2);
                        
                     
                        newLeafNode(this_STRING_2, grammarAccess.getTransitionOptionAccess().getSTRINGTerminalRuleCall_2()); 
                        

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


    // $ANTLR start "entryRuleRule"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3088:1: entryRuleRule returns [String current=null] : iv_ruleRule= ruleRule EOF ;
    public final String entryRuleRule() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleRule = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3089:2: (iv_ruleRule= ruleRule EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3090:2: iv_ruleRule= ruleRule EOF
            {
             newCompositeNode(grammarAccess.getRuleRule()); 
            pushFollow(FOLLOW_ruleRule_in_entryRuleRule6578);
            iv_ruleRule=ruleRule();

            state._fsp--;

             current =iv_ruleRule.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleRule6589); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3097:1: ruleRule returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'RequireStateHourSpentPrompt' | kw= 'AddDecisionValidateBlockingReview' | kw= 'AddDecisionValidateNonBlockingReview' | kw= 'AllowTransitionWithWorkingBranch' | kw= 'ForceAssigneesToTeamLeads' | kw= 'RequireTargetedVersion' | kw= 'AllowPriviledgedEditToTeamMember' | kw= 'AllowPriviledgedEditToTeamMemberAndOriginator' | kw= 'AllowPriviledgedEditToAll' | kw= 'AllowEditToAll' | kw= 'AllowAssigneeToAll' | this_STRING_11= RULE_STRING ) ;
    public final AntlrDatatypeRuleToken ruleRule() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        Token this_STRING_11=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3100:28: ( (kw= 'RequireStateHourSpentPrompt' | kw= 'AddDecisionValidateBlockingReview' | kw= 'AddDecisionValidateNonBlockingReview' | kw= 'AllowTransitionWithWorkingBranch' | kw= 'ForceAssigneesToTeamLeads' | kw= 'RequireTargetedVersion' | kw= 'AllowPriviledgedEditToTeamMember' | kw= 'AllowPriviledgedEditToTeamMemberAndOriginator' | kw= 'AllowPriviledgedEditToAll' | kw= 'AllowEditToAll' | kw= 'AllowAssigneeToAll' | this_STRING_11= RULE_STRING ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3101:1: (kw= 'RequireStateHourSpentPrompt' | kw= 'AddDecisionValidateBlockingReview' | kw= 'AddDecisionValidateNonBlockingReview' | kw= 'AllowTransitionWithWorkingBranch' | kw= 'ForceAssigneesToTeamLeads' | kw= 'RequireTargetedVersion' | kw= 'AllowPriviledgedEditToTeamMember' | kw= 'AllowPriviledgedEditToTeamMemberAndOriginator' | kw= 'AllowPriviledgedEditToAll' | kw= 'AllowEditToAll' | kw= 'AllowAssigneeToAll' | this_STRING_11= RULE_STRING )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3101:1: (kw= 'RequireStateHourSpentPrompt' | kw= 'AddDecisionValidateBlockingReview' | kw= 'AddDecisionValidateNonBlockingReview' | kw= 'AllowTransitionWithWorkingBranch' | kw= 'ForceAssigneesToTeamLeads' | kw= 'RequireTargetedVersion' | kw= 'AllowPriviledgedEditToTeamMember' | kw= 'AllowPriviledgedEditToTeamMemberAndOriginator' | kw= 'AllowPriviledgedEditToAll' | kw= 'AllowEditToAll' | kw= 'AllowAssigneeToAll' | this_STRING_11= RULE_STRING )
            int alt79=12;
            switch ( input.LA(1) ) {
            case 74:
                {
                alt79=1;
                }
                break;
            case 75:
                {
                alt79=2;
                }
                break;
            case 76:
                {
                alt79=3;
                }
                break;
            case 77:
                {
                alt79=4;
                }
                break;
            case 78:
                {
                alt79=5;
                }
                break;
            case 79:
                {
                alt79=6;
                }
                break;
            case 80:
                {
                alt79=7;
                }
                break;
            case 81:
                {
                alt79=8;
                }
                break;
            case 82:
                {
                alt79=9;
                }
                break;
            case 83:
                {
                alt79=10;
                }
                break;
            case 84:
                {
                alt79=11;
                }
                break;
            case RULE_STRING:
                {
                alt79=12;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 79, 0, input);

                throw nvae;
            }

            switch (alt79) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3102:2: kw= 'RequireStateHourSpentPrompt'
                    {
                    kw=(Token)match(input,74,FOLLOW_74_in_ruleRule6627); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleAccess().getRequireStateHourSpentPromptKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3109:2: kw= 'AddDecisionValidateBlockingReview'
                    {
                    kw=(Token)match(input,75,FOLLOW_75_in_ruleRule6646); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleAccess().getAddDecisionValidateBlockingReviewKeyword_1()); 
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3116:2: kw= 'AddDecisionValidateNonBlockingReview'
                    {
                    kw=(Token)match(input,76,FOLLOW_76_in_ruleRule6665); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleAccess().getAddDecisionValidateNonBlockingReviewKeyword_2()); 
                        

                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3123:2: kw= 'AllowTransitionWithWorkingBranch'
                    {
                    kw=(Token)match(input,77,FOLLOW_77_in_ruleRule6684); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleAccess().getAllowTransitionWithWorkingBranchKeyword_3()); 
                        

                    }
                    break;
                case 5 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3130:2: kw= 'ForceAssigneesToTeamLeads'
                    {
                    kw=(Token)match(input,78,FOLLOW_78_in_ruleRule6703); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleAccess().getForceAssigneesToTeamLeadsKeyword_4()); 
                        

                    }
                    break;
                case 6 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3137:2: kw= 'RequireTargetedVersion'
                    {
                    kw=(Token)match(input,79,FOLLOW_79_in_ruleRule6722); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleAccess().getRequireTargetedVersionKeyword_5()); 
                        

                    }
                    break;
                case 7 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3144:2: kw= 'AllowPriviledgedEditToTeamMember'
                    {
                    kw=(Token)match(input,80,FOLLOW_80_in_ruleRule6741); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleAccess().getAllowPriviledgedEditToTeamMemberKeyword_6()); 
                        

                    }
                    break;
                case 8 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3151:2: kw= 'AllowPriviledgedEditToTeamMemberAndOriginator'
                    {
                    kw=(Token)match(input,81,FOLLOW_81_in_ruleRule6760); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleAccess().getAllowPriviledgedEditToTeamMemberAndOriginatorKeyword_7()); 
                        

                    }
                    break;
                case 9 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3158:2: kw= 'AllowPriviledgedEditToAll'
                    {
                    kw=(Token)match(input,82,FOLLOW_82_in_ruleRule6779); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleAccess().getAllowPriviledgedEditToAllKeyword_8()); 
                        

                    }
                    break;
                case 10 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3165:2: kw= 'AllowEditToAll'
                    {
                    kw=(Token)match(input,83,FOLLOW_83_in_ruleRule6798); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleAccess().getAllowEditToAllKeyword_9()); 
                        

                    }
                    break;
                case 11 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3172:2: kw= 'AllowAssigneeToAll'
                    {
                    kw=(Token)match(input,84,FOLLOW_84_in_ruleRule6817); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleAccess().getAllowAssigneeToAllKeyword_10()); 
                        

                    }
                    break;
                case 12 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3178:10: this_STRING_11= RULE_STRING
                    {
                    this_STRING_11=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleRule6838); 

                    		current.merge(this_STRING_11);
                        
                     
                        newLeafNode(this_STRING_11, grammarAccess.getRuleAccess().getSTRINGTerminalRuleCall_11()); 
                        

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


    // $ANTLR start "entryRuleWidgetOption"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3193:1: entryRuleWidgetOption returns [String current=null] : iv_ruleWidgetOption= ruleWidgetOption EOF ;
    public final String entryRuleWidgetOption() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleWidgetOption = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3194:2: (iv_ruleWidgetOption= ruleWidgetOption EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3195:2: iv_ruleWidgetOption= ruleWidgetOption EOF
            {
             newCompositeNode(grammarAccess.getWidgetOptionRule()); 
            pushFollow(FOLLOW_ruleWidgetOption_in_entryRuleWidgetOption6884);
            iv_ruleWidgetOption=ruleWidgetOption();

            state._fsp--;

             current =iv_ruleWidgetOption.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleWidgetOption6895); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3202:1: ruleWidgetOption returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'REQUIRED_FOR_TRANSITION' | kw= 'NOT_REQUIRED_FOR_TRANSITION' | kw= 'REQUIRED_FOR_COMPLETION' | kw= 'NOT_REQUIRED_FOR_COMPLETION' | kw= 'ENABLED' | kw= 'NOT_ENABLED' | kw= 'EDITABLE' | kw= 'NOT_EDITABLE' | kw= 'MULTI_SELECT' | kw= 'HORIZONTAL_LABEL' | kw= 'VERTICAL_LABEL' | kw= 'LABEL_AFTER' | kw= 'LABEL_BEFORE' | kw= 'NO_LABEL' | kw= 'SORTED' | kw= 'ADD_DEFAULT_VALUE' | kw= 'NO_DEFAULT_VALUE' | kw= 'BEGIN_COMPOSITE_4' | kw= 'BEGIN_COMPOSITE_6' | kw= 'BEGIN_COMPOSITE_8' | kw= 'BEGIN_COMPOSITE_10' | kw= 'END_COMPOSITE' | kw= 'FILL_NONE' | kw= 'FILL_HORIZONTALLY' | kw= 'FILL_VERTICALLY' | kw= 'ALIGN_LEFT' | kw= 'ALIGN_RIGHT' | kw= 'ALIGN_CENTER' | this_STRING_28= RULE_STRING ) ;
    public final AntlrDatatypeRuleToken ruleWidgetOption() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        Token this_STRING_28=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3205:28: ( (kw= 'REQUIRED_FOR_TRANSITION' | kw= 'NOT_REQUIRED_FOR_TRANSITION' | kw= 'REQUIRED_FOR_COMPLETION' | kw= 'NOT_REQUIRED_FOR_COMPLETION' | kw= 'ENABLED' | kw= 'NOT_ENABLED' | kw= 'EDITABLE' | kw= 'NOT_EDITABLE' | kw= 'MULTI_SELECT' | kw= 'HORIZONTAL_LABEL' | kw= 'VERTICAL_LABEL' | kw= 'LABEL_AFTER' | kw= 'LABEL_BEFORE' | kw= 'NO_LABEL' | kw= 'SORTED' | kw= 'ADD_DEFAULT_VALUE' | kw= 'NO_DEFAULT_VALUE' | kw= 'BEGIN_COMPOSITE_4' | kw= 'BEGIN_COMPOSITE_6' | kw= 'BEGIN_COMPOSITE_8' | kw= 'BEGIN_COMPOSITE_10' | kw= 'END_COMPOSITE' | kw= 'FILL_NONE' | kw= 'FILL_HORIZONTALLY' | kw= 'FILL_VERTICALLY' | kw= 'ALIGN_LEFT' | kw= 'ALIGN_RIGHT' | kw= 'ALIGN_CENTER' | this_STRING_28= RULE_STRING ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3206:1: (kw= 'REQUIRED_FOR_TRANSITION' | kw= 'NOT_REQUIRED_FOR_TRANSITION' | kw= 'REQUIRED_FOR_COMPLETION' | kw= 'NOT_REQUIRED_FOR_COMPLETION' | kw= 'ENABLED' | kw= 'NOT_ENABLED' | kw= 'EDITABLE' | kw= 'NOT_EDITABLE' | kw= 'MULTI_SELECT' | kw= 'HORIZONTAL_LABEL' | kw= 'VERTICAL_LABEL' | kw= 'LABEL_AFTER' | kw= 'LABEL_BEFORE' | kw= 'NO_LABEL' | kw= 'SORTED' | kw= 'ADD_DEFAULT_VALUE' | kw= 'NO_DEFAULT_VALUE' | kw= 'BEGIN_COMPOSITE_4' | kw= 'BEGIN_COMPOSITE_6' | kw= 'BEGIN_COMPOSITE_8' | kw= 'BEGIN_COMPOSITE_10' | kw= 'END_COMPOSITE' | kw= 'FILL_NONE' | kw= 'FILL_HORIZONTALLY' | kw= 'FILL_VERTICALLY' | kw= 'ALIGN_LEFT' | kw= 'ALIGN_RIGHT' | kw= 'ALIGN_CENTER' | this_STRING_28= RULE_STRING )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3206:1: (kw= 'REQUIRED_FOR_TRANSITION' | kw= 'NOT_REQUIRED_FOR_TRANSITION' | kw= 'REQUIRED_FOR_COMPLETION' | kw= 'NOT_REQUIRED_FOR_COMPLETION' | kw= 'ENABLED' | kw= 'NOT_ENABLED' | kw= 'EDITABLE' | kw= 'NOT_EDITABLE' | kw= 'MULTI_SELECT' | kw= 'HORIZONTAL_LABEL' | kw= 'VERTICAL_LABEL' | kw= 'LABEL_AFTER' | kw= 'LABEL_BEFORE' | kw= 'NO_LABEL' | kw= 'SORTED' | kw= 'ADD_DEFAULT_VALUE' | kw= 'NO_DEFAULT_VALUE' | kw= 'BEGIN_COMPOSITE_4' | kw= 'BEGIN_COMPOSITE_6' | kw= 'BEGIN_COMPOSITE_8' | kw= 'BEGIN_COMPOSITE_10' | kw= 'END_COMPOSITE' | kw= 'FILL_NONE' | kw= 'FILL_HORIZONTALLY' | kw= 'FILL_VERTICALLY' | kw= 'ALIGN_LEFT' | kw= 'ALIGN_RIGHT' | kw= 'ALIGN_CENTER' | this_STRING_28= RULE_STRING )
            int alt80=29;
            switch ( input.LA(1) ) {
            case 85:
                {
                alt80=1;
                }
                break;
            case 86:
                {
                alt80=2;
                }
                break;
            case 87:
                {
                alt80=3;
                }
                break;
            case 88:
                {
                alt80=4;
                }
                break;
            case 89:
                {
                alt80=5;
                }
                break;
            case 90:
                {
                alt80=6;
                }
                break;
            case 91:
                {
                alt80=7;
                }
                break;
            case 92:
                {
                alt80=8;
                }
                break;
            case 93:
                {
                alt80=9;
                }
                break;
            case 94:
                {
                alt80=10;
                }
                break;
            case 95:
                {
                alt80=11;
                }
                break;
            case 96:
                {
                alt80=12;
                }
                break;
            case 97:
                {
                alt80=13;
                }
                break;
            case 98:
                {
                alt80=14;
                }
                break;
            case 99:
                {
                alt80=15;
                }
                break;
            case 100:
                {
                alt80=16;
                }
                break;
            case 101:
                {
                alt80=17;
                }
                break;
            case 102:
                {
                alt80=18;
                }
                break;
            case 103:
                {
                alt80=19;
                }
                break;
            case 104:
                {
                alt80=20;
                }
                break;
            case 105:
                {
                alt80=21;
                }
                break;
            case 106:
                {
                alt80=22;
                }
                break;
            case 107:
                {
                alt80=23;
                }
                break;
            case 108:
                {
                alt80=24;
                }
                break;
            case 109:
                {
                alt80=25;
                }
                break;
            case 110:
                {
                alt80=26;
                }
                break;
            case 111:
                {
                alt80=27;
                }
                break;
            case 112:
                {
                alt80=28;
                }
                break;
            case RULE_STRING:
                {
                alt80=29;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 80, 0, input);

                throw nvae;
            }

            switch (alt80) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3207:2: kw= 'REQUIRED_FOR_TRANSITION'
                    {
                    kw=(Token)match(input,85,FOLLOW_85_in_ruleWidgetOption6933); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getREQUIRED_FOR_TRANSITIONKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3214:2: kw= 'NOT_REQUIRED_FOR_TRANSITION'
                    {
                    kw=(Token)match(input,86,FOLLOW_86_in_ruleWidgetOption6952); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getNOT_REQUIRED_FOR_TRANSITIONKeyword_1()); 
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3221:2: kw= 'REQUIRED_FOR_COMPLETION'
                    {
                    kw=(Token)match(input,87,FOLLOW_87_in_ruleWidgetOption6971); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getREQUIRED_FOR_COMPLETIONKeyword_2()); 
                        

                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3228:2: kw= 'NOT_REQUIRED_FOR_COMPLETION'
                    {
                    kw=(Token)match(input,88,FOLLOW_88_in_ruleWidgetOption6990); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getNOT_REQUIRED_FOR_COMPLETIONKeyword_3()); 
                        

                    }
                    break;
                case 5 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3235:2: kw= 'ENABLED'
                    {
                    kw=(Token)match(input,89,FOLLOW_89_in_ruleWidgetOption7009); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getENABLEDKeyword_4()); 
                        

                    }
                    break;
                case 6 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3242:2: kw= 'NOT_ENABLED'
                    {
                    kw=(Token)match(input,90,FOLLOW_90_in_ruleWidgetOption7028); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getNOT_ENABLEDKeyword_5()); 
                        

                    }
                    break;
                case 7 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3249:2: kw= 'EDITABLE'
                    {
                    kw=(Token)match(input,91,FOLLOW_91_in_ruleWidgetOption7047); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getEDITABLEKeyword_6()); 
                        

                    }
                    break;
                case 8 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3256:2: kw= 'NOT_EDITABLE'
                    {
                    kw=(Token)match(input,92,FOLLOW_92_in_ruleWidgetOption7066); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getNOT_EDITABLEKeyword_7()); 
                        

                    }
                    break;
                case 9 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3263:2: kw= 'MULTI_SELECT'
                    {
                    kw=(Token)match(input,93,FOLLOW_93_in_ruleWidgetOption7085); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getMULTI_SELECTKeyword_8()); 
                        

                    }
                    break;
                case 10 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3270:2: kw= 'HORIZONTAL_LABEL'
                    {
                    kw=(Token)match(input,94,FOLLOW_94_in_ruleWidgetOption7104); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getHORIZONTAL_LABELKeyword_9()); 
                        

                    }
                    break;
                case 11 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3277:2: kw= 'VERTICAL_LABEL'
                    {
                    kw=(Token)match(input,95,FOLLOW_95_in_ruleWidgetOption7123); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getVERTICAL_LABELKeyword_10()); 
                        

                    }
                    break;
                case 12 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3284:2: kw= 'LABEL_AFTER'
                    {
                    kw=(Token)match(input,96,FOLLOW_96_in_ruleWidgetOption7142); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getLABEL_AFTERKeyword_11()); 
                        

                    }
                    break;
                case 13 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3291:2: kw= 'LABEL_BEFORE'
                    {
                    kw=(Token)match(input,97,FOLLOW_97_in_ruleWidgetOption7161); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getLABEL_BEFOREKeyword_12()); 
                        

                    }
                    break;
                case 14 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3298:2: kw= 'NO_LABEL'
                    {
                    kw=(Token)match(input,98,FOLLOW_98_in_ruleWidgetOption7180); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getNO_LABELKeyword_13()); 
                        

                    }
                    break;
                case 15 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3305:2: kw= 'SORTED'
                    {
                    kw=(Token)match(input,99,FOLLOW_99_in_ruleWidgetOption7199); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getSORTEDKeyword_14()); 
                        

                    }
                    break;
                case 16 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3312:2: kw= 'ADD_DEFAULT_VALUE'
                    {
                    kw=(Token)match(input,100,FOLLOW_100_in_ruleWidgetOption7218); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getADD_DEFAULT_VALUEKeyword_15()); 
                        

                    }
                    break;
                case 17 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3319:2: kw= 'NO_DEFAULT_VALUE'
                    {
                    kw=(Token)match(input,101,FOLLOW_101_in_ruleWidgetOption7237); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getNO_DEFAULT_VALUEKeyword_16()); 
                        

                    }
                    break;
                case 18 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3326:2: kw= 'BEGIN_COMPOSITE_4'
                    {
                    kw=(Token)match(input,102,FOLLOW_102_in_ruleWidgetOption7256); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getBEGIN_COMPOSITE_4Keyword_17()); 
                        

                    }
                    break;
                case 19 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3333:2: kw= 'BEGIN_COMPOSITE_6'
                    {
                    kw=(Token)match(input,103,FOLLOW_103_in_ruleWidgetOption7275); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getBEGIN_COMPOSITE_6Keyword_18()); 
                        

                    }
                    break;
                case 20 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3340:2: kw= 'BEGIN_COMPOSITE_8'
                    {
                    kw=(Token)match(input,104,FOLLOW_104_in_ruleWidgetOption7294); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getBEGIN_COMPOSITE_8Keyword_19()); 
                        

                    }
                    break;
                case 21 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3347:2: kw= 'BEGIN_COMPOSITE_10'
                    {
                    kw=(Token)match(input,105,FOLLOW_105_in_ruleWidgetOption7313); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getBEGIN_COMPOSITE_10Keyword_20()); 
                        

                    }
                    break;
                case 22 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3354:2: kw= 'END_COMPOSITE'
                    {
                    kw=(Token)match(input,106,FOLLOW_106_in_ruleWidgetOption7332); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getEND_COMPOSITEKeyword_21()); 
                        

                    }
                    break;
                case 23 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3361:2: kw= 'FILL_NONE'
                    {
                    kw=(Token)match(input,107,FOLLOW_107_in_ruleWidgetOption7351); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getFILL_NONEKeyword_22()); 
                        

                    }
                    break;
                case 24 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3368:2: kw= 'FILL_HORIZONTALLY'
                    {
                    kw=(Token)match(input,108,FOLLOW_108_in_ruleWidgetOption7370); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getFILL_HORIZONTALLYKeyword_23()); 
                        

                    }
                    break;
                case 25 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3375:2: kw= 'FILL_VERTICALLY'
                    {
                    kw=(Token)match(input,109,FOLLOW_109_in_ruleWidgetOption7389); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getFILL_VERTICALLYKeyword_24()); 
                        

                    }
                    break;
                case 26 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3382:2: kw= 'ALIGN_LEFT'
                    {
                    kw=(Token)match(input,110,FOLLOW_110_in_ruleWidgetOption7408); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getALIGN_LEFTKeyword_25()); 
                        

                    }
                    break;
                case 27 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3389:2: kw= 'ALIGN_RIGHT'
                    {
                    kw=(Token)match(input,111,FOLLOW_111_in_ruleWidgetOption7427); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getALIGN_RIGHTKeyword_26()); 
                        

                    }
                    break;
                case 28 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3396:2: kw= 'ALIGN_CENTER'
                    {
                    kw=(Token)match(input,112,FOLLOW_112_in_ruleWidgetOption7446); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getALIGN_CENTERKeyword_27()); 
                        

                    }
                    break;
                case 29 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3402:10: this_STRING_28= RULE_STRING
                    {
                    this_STRING_28=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleWidgetOption7467); 

                    		current.merge(this_STRING_28);
                        
                     
                        newLeafNode(this_STRING_28, grammarAccess.getWidgetOptionAccess().getSTRINGTerminalRuleCall_28()); 
                        

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


    // $ANTLR start "entryRulePageType"
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3417:1: entryRulePageType returns [String current=null] : iv_rulePageType= rulePageType EOF ;
    public final String entryRulePageType() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_rulePageType = null;


        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3418:2: (iv_rulePageType= rulePageType EOF )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3419:2: iv_rulePageType= rulePageType EOF
            {
             newCompositeNode(grammarAccess.getPageTypeRule()); 
            pushFollow(FOLLOW_rulePageType_in_entryRulePageType7513);
            iv_rulePageType=rulePageType();

            state._fsp--;

             current =iv_rulePageType.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRulePageType7524); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3426:1: rulePageType returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'Working' | kw= 'Completed' | kw= 'Cancelled' | this_STRING_3= RULE_STRING ) ;
    public final AntlrDatatypeRuleToken rulePageType() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        Token this_STRING_3=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3429:28: ( (kw= 'Working' | kw= 'Completed' | kw= 'Cancelled' | this_STRING_3= RULE_STRING ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3430:1: (kw= 'Working' | kw= 'Completed' | kw= 'Cancelled' | this_STRING_3= RULE_STRING )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3430:1: (kw= 'Working' | kw= 'Completed' | kw= 'Cancelled' | this_STRING_3= RULE_STRING )
            int alt81=4;
            switch ( input.LA(1) ) {
            case 113:
                {
                alt81=1;
                }
                break;
            case 114:
                {
                alt81=2;
                }
                break;
            case 115:
                {
                alt81=3;
                }
                break;
            case RULE_STRING:
                {
                alt81=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 81, 0, input);

                throw nvae;
            }

            switch (alt81) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3431:2: kw= 'Working'
                    {
                    kw=(Token)match(input,113,FOLLOW_113_in_rulePageType7562); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getPageTypeAccess().getWorkingKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3438:2: kw= 'Completed'
                    {
                    kw=(Token)match(input,114,FOLLOW_114_in_rulePageType7581); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getPageTypeAccess().getCompletedKeyword_1()); 
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3445:2: kw= 'Cancelled'
                    {
                    kw=(Token)match(input,115,FOLLOW_115_in_rulePageType7600); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getPageTypeAccess().getCancelledKeyword_2()); 
                        

                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3451:10: this_STRING_3= RULE_STRING
                    {
                    this_STRING_3=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rulePageType7621); 

                    		current.merge(this_STRING_3);
                        
                     
                        newLeafNode(this_STRING_3, grammarAccess.getPageTypeAccess().getSTRINGTerminalRuleCall_3()); 
                        

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3466:1: ruleBooleanDef returns [Enumerator current=null] : ( (enumLiteral_0= 'None' ) | (enumLiteral_1= 'True' ) | (enumLiteral_2= 'False' ) ) ;
    public final Enumerator ruleBooleanDef() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;
        Token enumLiteral_2=null;

         enterRule(); 
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3468:28: ( ( (enumLiteral_0= 'None' ) | (enumLiteral_1= 'True' ) | (enumLiteral_2= 'False' ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3469:1: ( (enumLiteral_0= 'None' ) | (enumLiteral_1= 'True' ) | (enumLiteral_2= 'False' ) )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3469:1: ( (enumLiteral_0= 'None' ) | (enumLiteral_1= 'True' ) | (enumLiteral_2= 'False' ) )
            int alt82=3;
            switch ( input.LA(1) ) {
            case 71:
                {
                alt82=1;
                }
                break;
            case 116:
                {
                alt82=2;
                }
                break;
            case 117:
                {
                alt82=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 82, 0, input);

                throw nvae;
            }

            switch (alt82) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3469:2: (enumLiteral_0= 'None' )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3469:2: (enumLiteral_0= 'None' )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3469:4: enumLiteral_0= 'None'
                    {
                    enumLiteral_0=(Token)match(input,71,FOLLOW_71_in_ruleBooleanDef7680); 

                            current = grammarAccess.getBooleanDefAccess().getNoneEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_0, grammarAccess.getBooleanDefAccess().getNoneEnumLiteralDeclaration_0()); 
                        

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3475:6: (enumLiteral_1= 'True' )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3475:6: (enumLiteral_1= 'True' )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3475:8: enumLiteral_1= 'True'
                    {
                    enumLiteral_1=(Token)match(input,116,FOLLOW_116_in_ruleBooleanDef7697); 

                            current = grammarAccess.getBooleanDefAccess().getTrueEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_1, grammarAccess.getBooleanDefAccess().getTrueEnumLiteralDeclaration_1()); 
                        

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3481:6: (enumLiteral_2= 'False' )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3481:6: (enumLiteral_2= 'False' )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3481:8: enumLiteral_2= 'False'
                    {
                    enumLiteral_2=(Token)match(input,117,FOLLOW_117_in_ruleBooleanDef7714); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3491:1: ruleWorkflowEventType returns [Enumerator current=null] : ( (enumLiteral_0= 'TransitionTo' ) | (enumLiteral_1= 'CreateBranch' ) | (enumLiteral_2= 'CommitBranch' ) ) ;
    public final Enumerator ruleWorkflowEventType() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;
        Token enumLiteral_2=null;

         enterRule(); 
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3493:28: ( ( (enumLiteral_0= 'TransitionTo' ) | (enumLiteral_1= 'CreateBranch' ) | (enumLiteral_2= 'CommitBranch' ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3494:1: ( (enumLiteral_0= 'TransitionTo' ) | (enumLiteral_1= 'CreateBranch' ) | (enumLiteral_2= 'CommitBranch' ) )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3494:1: ( (enumLiteral_0= 'TransitionTo' ) | (enumLiteral_1= 'CreateBranch' ) | (enumLiteral_2= 'CommitBranch' ) )
            int alt83=3;
            switch ( input.LA(1) ) {
            case 118:
                {
                alt83=1;
                }
                break;
            case 119:
                {
                alt83=2;
                }
                break;
            case 120:
                {
                alt83=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 83, 0, input);

                throw nvae;
            }

            switch (alt83) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3494:2: (enumLiteral_0= 'TransitionTo' )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3494:2: (enumLiteral_0= 'TransitionTo' )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3494:4: enumLiteral_0= 'TransitionTo'
                    {
                    enumLiteral_0=(Token)match(input,118,FOLLOW_118_in_ruleWorkflowEventType7759); 

                            current = grammarAccess.getWorkflowEventTypeAccess().getTransitionToEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_0, grammarAccess.getWorkflowEventTypeAccess().getTransitionToEnumLiteralDeclaration_0()); 
                        

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3500:6: (enumLiteral_1= 'CreateBranch' )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3500:6: (enumLiteral_1= 'CreateBranch' )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3500:8: enumLiteral_1= 'CreateBranch'
                    {
                    enumLiteral_1=(Token)match(input,119,FOLLOW_119_in_ruleWorkflowEventType7776); 

                            current = grammarAccess.getWorkflowEventTypeAccess().getCreateBranchEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_1, grammarAccess.getWorkflowEventTypeAccess().getCreateBranchEnumLiteralDeclaration_1()); 
                        

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3506:6: (enumLiteral_2= 'CommitBranch' )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3506:6: (enumLiteral_2= 'CommitBranch' )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3506:8: enumLiteral_2= 'CommitBranch'
                    {
                    enumLiteral_2=(Token)match(input,120,FOLLOW_120_in_ruleWorkflowEventType7793); 

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
    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3516:1: ruleReviewBlockingType returns [Enumerator current=null] : ( (enumLiteral_0= 'Transition' ) | (enumLiteral_1= 'Commit' ) ) ;
    public final Enumerator ruleReviewBlockingType() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;

         enterRule(); 
        try {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3518:28: ( ( (enumLiteral_0= 'Transition' ) | (enumLiteral_1= 'Commit' ) ) )
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3519:1: ( (enumLiteral_0= 'Transition' ) | (enumLiteral_1= 'Commit' ) )
            {
            // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3519:1: ( (enumLiteral_0= 'Transition' ) | (enumLiteral_1= 'Commit' ) )
            int alt84=2;
            int LA84_0 = input.LA(1);

            if ( (LA84_0==121) ) {
                alt84=1;
            }
            else if ( (LA84_0==122) ) {
                alt84=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 84, 0, input);

                throw nvae;
            }
            switch (alt84) {
                case 1 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3519:2: (enumLiteral_0= 'Transition' )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3519:2: (enumLiteral_0= 'Transition' )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3519:4: enumLiteral_0= 'Transition'
                    {
                    enumLiteral_0=(Token)match(input,121,FOLLOW_121_in_ruleReviewBlockingType7838); 

                            current = grammarAccess.getReviewBlockingTypeAccess().getTransitionEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_0, grammarAccess.getReviewBlockingTypeAccess().getTransitionEnumLiteralDeclaration_0()); 
                        

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3525:6: (enumLiteral_1= 'Commit' )
                    {
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3525:6: (enumLiteral_1= 'Commit' )
                    // ../org.eclipse.osee.ats.dsl/src-gen/org/eclipse/osee/ats/dsl/parser/antlr/internal/InternalAtsDsl.g:3525:8: enumLiteral_1= 'Commit'
                    {
                    enumLiteral_1=(Token)match(input,122,FOLLOW_122_in_ruleReviewBlockingType7855); 

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
    public static final BitSet FOLLOW_ruleUserDef_in_entryRuleUserDef290 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleUserDef300 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleUserDef342 = new BitSet(new long[]{0x0000000000008012L,0x0000000000000040L});
    public static final BitSet FOLLOW_ruleUserDefOption_in_ruleUserDef368 = new BitSet(new long[]{0x0000000000008012L,0x0000000000000040L});
    public static final BitSet FOLLOW_15_in_ruleUserDef382 = new BitSet(new long[]{0x00000000001F0000L});
    public static final BitSet FOLLOW_16_in_ruleUserDef395 = new BitSet(new long[]{0x0000000000000000L,0x0030000000000080L});
    public static final BitSet FOLLOW_ruleBooleanDef_in_ruleUserDef416 = new BitSet(new long[]{0x00000000001E0000L});
    public static final BitSet FOLLOW_17_in_ruleUserDef431 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleUserDef448 = new BitSet(new long[]{0x00000000001C0000L});
    public static final BitSet FOLLOW_18_in_ruleUserDef468 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleUserDef485 = new BitSet(new long[]{0x0000000000180000L});
    public static final BitSet FOLLOW_19_in_ruleUserDef505 = new BitSet(new long[]{0x0000000000000000L,0x0030000000000080L});
    public static final BitSet FOLLOW_ruleBooleanDef_in_ruleUserDef526 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_ruleUserDef540 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTeamDef_in_entryRuleTeamDef578 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleTeamDef588 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleTeamDef630 = new BitSet(new long[]{0x0000000000008010L,0x0000000000000040L});
    public static final BitSet FOLLOW_ruleTeamDefOption_in_ruleTeamDef656 = new BitSet(new long[]{0x0000000000008010L,0x0000000000000040L});
    public static final BitSet FOLLOW_15_in_ruleTeamDef669 = new BitSet(new long[]{0x000000000FF10800L});
    public static final BitSet FOLLOW_16_in_ruleTeamDef682 = new BitSet(new long[]{0x0000000000000000L,0x0030000000000080L});
    public static final BitSet FOLLOW_ruleBooleanDef_in_ruleTeamDef703 = new BitSet(new long[]{0x000000000FF00800L});
    public static final BitSet FOLLOW_21_in_ruleTeamDef718 = new BitSet(new long[]{0x0000000000000000L,0x0030000000000080L});
    public static final BitSet FOLLOW_ruleBooleanDef_in_ruleTeamDef739 = new BitSet(new long[]{0x000000000FD00800L});
    public static final BitSet FOLLOW_22_in_ruleTeamDef754 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleTeamDef771 = new BitSet(new long[]{0x000000000FD00800L});
    public static final BitSet FOLLOW_23_in_ruleTeamDef791 = new BitSet(new long[]{0x0000000000020000L,0x0000000000000001L});
    public static final BitSet FOLLOW_ruleUserRef_in_ruleTeamDef812 = new BitSet(new long[]{0x000000000F900800L});
    public static final BitSet FOLLOW_24_in_ruleTeamDef827 = new BitSet(new long[]{0x0000000000020000L,0x0000000000000001L});
    public static final BitSet FOLLOW_ruleUserRef_in_ruleTeamDef848 = new BitSet(new long[]{0x000000000F100800L});
    public static final BitSet FOLLOW_25_in_ruleTeamDef863 = new BitSet(new long[]{0x0000000000020000L,0x0000000000000001L});
    public static final BitSet FOLLOW_ruleUserRef_in_ruleTeamDef884 = new BitSet(new long[]{0x000000000E100800L});
    public static final BitSet FOLLOW_11_in_ruleTeamDef899 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleTeamDef916 = new BitSet(new long[]{0x000000000C100000L});
    public static final BitSet FOLLOW_26_in_ruleTeamDef936 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleVersionDef_in_ruleTeamDef957 = new BitSet(new long[]{0x000000000C100000L});
    public static final BitSet FOLLOW_27_in_ruleTeamDef972 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_ruleTeamDef984 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_13_in_ruleTeamDef997 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleTeamDef_in_ruleTeamDef1018 = new BitSet(new long[]{0x0000000000102000L});
    public static final BitSet FOLLOW_20_in_ruleTeamDef1032 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_ruleTeamDef1046 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleActionableItemDef_in_entryRuleActionableItemDef1082 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleActionableItemDef1092 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleActionableItemDef1134 = new BitSet(new long[]{0x0000000000008012L,0x0000000000000040L});
    public static final BitSet FOLLOW_ruleActionableItemOption_in_ruleActionableItemDef1160 = new BitSet(new long[]{0x0000000000008012L,0x0000000000000040L});
    public static final BitSet FOLLOW_15_in_ruleActionableItemDef1174 = new BitSet(new long[]{0x0000000038D10000L});
    public static final BitSet FOLLOW_16_in_ruleActionableItemDef1187 = new BitSet(new long[]{0x0000000000000000L,0x0030000000000080L});
    public static final BitSet FOLLOW_ruleBooleanDef_in_ruleActionableItemDef1208 = new BitSet(new long[]{0x0000000038D00000L});
    public static final BitSet FOLLOW_28_in_ruleActionableItemDef1223 = new BitSet(new long[]{0x0000000000000000L,0x0030000000000080L});
    public static final BitSet FOLLOW_ruleBooleanDef_in_ruleActionableItemDef1244 = new BitSet(new long[]{0x0000000028D00000L});
    public static final BitSet FOLLOW_23_in_ruleActionableItemDef1259 = new BitSet(new long[]{0x0000000000020000L,0x0000000000000001L});
    public static final BitSet FOLLOW_ruleUserRef_in_ruleActionableItemDef1280 = new BitSet(new long[]{0x0000000028D00000L});
    public static final BitSet FOLLOW_22_in_ruleActionableItemDef1295 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleActionableItemDef1312 = new BitSet(new long[]{0x0000000028500000L});
    public static final BitSet FOLLOW_29_in_ruleActionableItemDef1332 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleActionableItemDef1349 = new BitSet(new long[]{0x0000000008100000L});
    public static final BitSet FOLLOW_27_in_ruleActionableItemDef1369 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_ruleActionableItemDef1381 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_14_in_ruleActionableItemDef1394 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleActionableItemDef_in_ruleActionableItemDef1415 = new BitSet(new long[]{0x0000000000104000L});
    public static final BitSet FOLLOW_20_in_ruleActionableItemDef1429 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_ruleActionableItemDef1443 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleVersionDef_in_entryRuleVersionDef1481 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleVersionDef1491 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleVersionDef1533 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_ruleVersionDef1550 = new BitSet(new long[]{0x00000007C0510000L});
    public static final BitSet FOLLOW_16_in_ruleVersionDef1563 = new BitSet(new long[]{0x0000000000000000L,0x0030000000000080L});
    public static final BitSet FOLLOW_ruleBooleanDef_in_ruleVersionDef1584 = new BitSet(new long[]{0x00000007C0500000L});
    public static final BitSet FOLLOW_22_in_ruleVersionDef1599 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleVersionDef1616 = new BitSet(new long[]{0x00000007C0500000L});
    public static final BitSet FOLLOW_30_in_ruleVersionDef1636 = new BitSet(new long[]{0x0000000000000000L,0x0030000000000080L});
    public static final BitSet FOLLOW_ruleBooleanDef_in_ruleVersionDef1657 = new BitSet(new long[]{0x0000000780100000L});
    public static final BitSet FOLLOW_31_in_ruleVersionDef1672 = new BitSet(new long[]{0x0000000000000000L,0x0030000000000080L});
    public static final BitSet FOLLOW_ruleBooleanDef_in_ruleVersionDef1693 = new BitSet(new long[]{0x0000000700100000L});
    public static final BitSet FOLLOW_32_in_ruleVersionDef1708 = new BitSet(new long[]{0x0000000000000000L,0x0030000000000080L});
    public static final BitSet FOLLOW_ruleBooleanDef_in_ruleVersionDef1729 = new BitSet(new long[]{0x0000000600100000L});
    public static final BitSet FOLLOW_33_in_ruleVersionDef1744 = new BitSet(new long[]{0x0000000000000000L,0x0030000000000080L});
    public static final BitSet FOLLOW_ruleBooleanDef_in_ruleVersionDef1765 = new BitSet(new long[]{0x0000000400100000L});
    public static final BitSet FOLLOW_34_in_ruleVersionDef1780 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleVersionDef1797 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_ruleVersionDef1816 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleWorkDef_in_entryRuleWorkDef1852 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleWorkDef1862 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleWORK_DEFINITION_NAME_REFERENCE_in_ruleWorkDef1908 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_ruleWorkDef1920 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_35_in_ruleWorkDef1933 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleWorkDef1950 = new BitSet(new long[]{0x0000001800000000L});
    public static final BitSet FOLLOW_36_in_ruleWorkDef1970 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleWorkDef1993 = new BitSet(new long[]{0x2020802000000000L});
    public static final BitSet FOLLOW_ruleWidgetDef_in_ruleWorkDef2015 = new BitSet(new long[]{0x2020802000000000L});
    public static final BitSet FOLLOW_ruleDecisionReviewDef_in_ruleWorkDef2037 = new BitSet(new long[]{0x2020802000000000L});
    public static final BitSet FOLLOW_rulePeerReviewDef_in_ruleWorkDef2059 = new BitSet(new long[]{0x2020802000000000L});
    public static final BitSet FOLLOW_ruleStateDef_in_ruleWorkDef2081 = new BitSet(new long[]{0x2020802000100000L});
    public static final BitSet FOLLOW_20_in_ruleWorkDef2094 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleWidgetDef_in_entryRuleWidgetDef2130 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleWidgetDef2140 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_ruleWidgetDef2177 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleWIDGET_NAME_REFERENCE_in_ruleWidgetDef2198 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_ruleWidgetDef2210 = new BitSet(new long[]{0x00000FC000100000L});
    public static final BitSet FOLLOW_38_in_ruleWidgetDef2223 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleWidgetDef2240 = new BitSet(new long[]{0x00000F8000100000L});
    public static final BitSet FOLLOW_39_in_ruleWidgetDef2260 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleWidgetDef2277 = new BitSet(new long[]{0x00000F0000100000L});
    public static final BitSet FOLLOW_40_in_ruleWidgetDef2297 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleWidgetDef2314 = new BitSet(new long[]{0x00000E0000100000L});
    public static final BitSet FOLLOW_41_in_ruleWidgetDef2334 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleWidgetDef2351 = new BitSet(new long[]{0x00000C0000100000L});
    public static final BitSet FOLLOW_42_in_ruleWidgetDef2371 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_RULE_INT_in_ruleWidgetDef2388 = new BitSet(new long[]{0x0000080000100000L});
    public static final BitSet FOLLOW_43_in_ruleWidgetDef2408 = new BitSet(new long[]{0x0000000000000010L,0x0001FFFFFFE00000L});
    public static final BitSet FOLLOW_ruleWidgetOption_in_ruleWidgetDef2429 = new BitSet(new long[]{0x0000080000100000L});
    public static final BitSet FOLLOW_20_in_ruleWidgetDef2443 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleWidgetRef_in_entryRuleWidgetRef2479 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleWidgetRef2489 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_ruleWidgetRef2526 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleWIDGET_NAME_REFERENCE_in_ruleWidgetRef2549 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttrWidget_in_entryRuleAttrWidget2585 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttrWidget2595 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_45_in_ruleAttrWidget2632 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAttrWidget2649 = new BitSet(new long[]{0x0000400000000002L});
    public static final BitSet FOLLOW_46_in_ruleAttrWidget2667 = new BitSet(new long[]{0x0000000000000010L,0x0001FFFFFFE00000L});
    public static final BitSet FOLLOW_ruleWidgetOption_in_ruleAttrWidget2688 = new BitSet(new long[]{0x0000400000000002L});
    public static final BitSet FOLLOW_ruleStateDef_in_entryRuleStateDef2726 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleStateDef2736 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_ruleStateDef2773 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleStateDef2794 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_ruleStateDef2806 = new BitSet(new long[]{0x0001008000000000L});
    public static final BitSet FOLLOW_39_in_ruleStateDef2819 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleStateDef2836 = new BitSet(new long[]{0x0001000000000000L});
    public static final BitSet FOLLOW_48_in_ruleStateDef2855 = new BitSet(new long[]{0x0000000000000010L,0x000E000000000000L});
    public static final BitSet FOLLOW_rulePageType_in_ruleStateDef2876 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_ruleStateDef2888 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_RULE_INT_in_ruleStateDef2905 = new BitSet(new long[]{0x101C000000100000L,0x000000000000000EL});
    public static final BitSet FOLLOW_ruleToState_in_ruleStateDef2931 = new BitSet(new long[]{0x101C000000100000L,0x000000000000000EL});
    public static final BitSet FOLLOW_50_in_ruleStateDef2945 = new BitSet(new long[]{0x0000000000000010L,0x00000000001FFC00L});
    public static final BitSet FOLLOW_ruleRule_in_ruleStateDef2966 = new BitSet(new long[]{0x101C000000100000L,0x000000000000000CL});
    public static final BitSet FOLLOW_ruleDecisionReviewRef_in_ruleStateDef2989 = new BitSet(new long[]{0x1018000000100000L,0x000000000000000CL});
    public static final BitSet FOLLOW_rulePeerReviewRef_in_ruleStateDef3011 = new BitSet(new long[]{0x1008000000100000L,0x000000000000000CL});
    public static final BitSet FOLLOW_51_in_ruleStateDef3025 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_RULE_INT_in_ruleStateDef3042 = new BitSet(new long[]{0x0000000000100000L,0x000000000000000CL});
    public static final BitSet FOLLOW_ruleLayoutType_in_ruleStateDef3070 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_ruleStateDef3083 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDecisionReviewRef_in_entryRuleDecisionReviewRef3119 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleDecisionReviewRef3129 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_52_in_ruleDecisionReviewRef3166 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleDECISION_REVIEW_NAME_REFERENCE_in_ruleDecisionReviewRef3189 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDecisionReviewDef_in_entryRuleDecisionReviewDef3225 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleDecisionReviewDef3235 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_53_in_ruleDecisionReviewDef3272 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleDECISION_REVIEW_NAME_REFERENCE_in_ruleDecisionReviewDef3293 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_ruleDecisionReviewDef3305 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_54_in_ruleDecisionReviewDef3317 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleDecisionReviewDef3334 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_39_in_ruleDecisionReviewDef3351 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleDecisionReviewDef3368 = new BitSet(new long[]{0x0180000000000000L});
    public static final BitSet FOLLOW_55_in_ruleDecisionReviewDef3386 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleDecisionReviewDef3409 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_56_in_ruleDecisionReviewDef3423 = new BitSet(new long[]{0x0000000000000000L,0x0600000000000000L});
    public static final BitSet FOLLOW_ruleReviewBlockingType_in_ruleDecisionReviewDef3444 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_57_in_ruleDecisionReviewDef3456 = new BitSet(new long[]{0x0000000000000000L,0x01C0000000000000L});
    public static final BitSet FOLLOW_ruleWorkflowEventType_in_ruleDecisionReviewDef3477 = new BitSet(new long[]{0x0C00080000000000L});
    public static final BitSet FOLLOW_58_in_ruleDecisionReviewDef3490 = new BitSet(new long[]{0x0000000000020000L,0x0000000000000001L});
    public static final BitSet FOLLOW_ruleUserRef_in_ruleDecisionReviewDef3511 = new BitSet(new long[]{0x0C00080000000000L});
    public static final BitSet FOLLOW_59_in_ruleDecisionReviewDef3526 = new BitSet(new long[]{0x0000000000000000L,0x0030000000000080L});
    public static final BitSet FOLLOW_ruleBooleanDef_in_ruleDecisionReviewDef3547 = new BitSet(new long[]{0x0C00080000000000L});
    public static final BitSet FOLLOW_ruleDecisionReviewOpt_in_ruleDecisionReviewDef3570 = new BitSet(new long[]{0x0C00080000100000L});
    public static final BitSet FOLLOW_20_in_ruleDecisionReviewDef3583 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDecisionReviewOpt_in_entryRuleDecisionReviewOpt3619 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleDecisionReviewOpt3629 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_43_in_ruleDecisionReviewOpt3666 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleDecisionReviewOpt3683 = new BitSet(new long[]{0x8000000000000002L});
    public static final BitSet FOLLOW_ruleFollowupRef_in_ruleDecisionReviewOpt3709 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rulePeerReviewRef_in_entryRulePeerReviewRef3746 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRulePeerReviewRef3756 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_60_in_rulePeerReviewRef3793 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rulePEER_REVIEW_NAME_REFERENCE_in_rulePeerReviewRef3816 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rulePeerReviewDef_in_entryRulePeerReviewDef3852 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRulePeerReviewDef3862 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_61_in_rulePeerReviewDef3899 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rulePEER_REVIEW_NAME_REFERENCE_in_rulePeerReviewDef3920 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_rulePeerReviewDef3932 = new BitSet(new long[]{0x0040008000000000L});
    public static final BitSet FOLLOW_54_in_rulePeerReviewDef3945 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_rulePeerReviewDef3962 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_39_in_rulePeerReviewDef3981 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_rulePeerReviewDef3998 = new BitSet(new long[]{0x4180000000000000L});
    public static final BitSet FOLLOW_62_in_rulePeerReviewDef4016 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_rulePeerReviewDef4033 = new BitSet(new long[]{0x0180000000000000L});
    public static final BitSet FOLLOW_55_in_rulePeerReviewDef4053 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleSTATE_NAME_REFERENCE_in_rulePeerReviewDef4076 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_56_in_rulePeerReviewDef4090 = new BitSet(new long[]{0x0000000000000000L,0x0600000000000000L});
    public static final BitSet FOLLOW_ruleReviewBlockingType_in_rulePeerReviewDef4111 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_57_in_rulePeerReviewDef4123 = new BitSet(new long[]{0x0000000000000000L,0x01C0000000000000L});
    public static final BitSet FOLLOW_ruleWorkflowEventType_in_rulePeerReviewDef4144 = new BitSet(new long[]{0x0400000000100000L});
    public static final BitSet FOLLOW_58_in_rulePeerReviewDef4157 = new BitSet(new long[]{0x0000000000020000L,0x0000000000000001L});
    public static final BitSet FOLLOW_ruleUserRef_in_rulePeerReviewDef4178 = new BitSet(new long[]{0x0400000000100000L});
    public static final BitSet FOLLOW_20_in_rulePeerReviewDef4192 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleFollowupRef_in_entryRuleFollowupRef4228 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleFollowupRef4238 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_63_in_ruleFollowupRef4275 = new BitSet(new long[]{0x0400000000000000L});
    public static final BitSet FOLLOW_58_in_ruleFollowupRef4288 = new BitSet(new long[]{0x0000000000020000L,0x0000000000000001L});
    public static final BitSet FOLLOW_ruleUserRef_in_ruleFollowupRef4309 = new BitSet(new long[]{0x0400000000000002L});
    public static final BitSet FOLLOW_ruleUserRef_in_entryRuleUserRef4347 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleUserRef4357 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleUserByUserId_in_ruleUserRef4404 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleUserByName_in_ruleUserRef4431 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleUserByUserId_in_entryRuleUserByUserId4466 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleUserByUserId4476 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_17_in_ruleUserByUserId4513 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleUserByUserId4530 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleUserByName_in_entryRuleUserByName4571 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleUserByName4581 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_64_in_ruleUserByName4618 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleUserByName4635 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDECISION_REVIEW_NAME_REFERENCE_in_entryRuleDECISION_REVIEW_NAME_REFERENCE4677 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleDECISION_REVIEW_NAME_REFERENCE4688 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleDECISION_REVIEW_NAME_REFERENCE4727 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rulePEER_REVIEW_NAME_REFERENCE_in_entryRulePEER_REVIEW_NAME_REFERENCE4772 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRulePEER_REVIEW_NAME_REFERENCE4783 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rulePEER_REVIEW_NAME_REFERENCE4822 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleSTATE_NAME_REFERENCE_in_entryRuleSTATE_NAME_REFERENCE4867 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleSTATE_NAME_REFERENCE4878 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleSTATE_NAME_REFERENCE4917 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleWIDGET_NAME_REFERENCE_in_entryRuleWIDGET_NAME_REFERENCE4962 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleWIDGET_NAME_REFERENCE4973 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleWIDGET_NAME_REFERENCE5012 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleWORK_DEFINITION_NAME_REFERENCE_in_entryRuleWORK_DEFINITION_NAME_REFERENCE5057 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleWORK_DEFINITION_NAME_REFERENCE5068 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleWORK_DEFINITION_NAME_REFERENCE5107 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleToState_in_entryRuleToState5151 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleToState5161 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_ruleToState5198 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleToState5221 = new BitSet(new long[]{0x0000000000000012L,0x0000000000000300L});
    public static final BitSet FOLLOW_ruleTransitionOption_in_ruleToState5242 = new BitSet(new long[]{0x0000000000000012L,0x0000000000000300L});
    public static final BitSet FOLLOW_ruleLayoutType_in_entryRuleLayoutType5279 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleLayoutType5289 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleLayoutDef_in_ruleLayoutType5336 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleLayoutCopy_in_ruleLayoutType5363 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleLayoutDef_in_entryRuleLayoutDef5398 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleLayoutDef5408 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_66_in_ruleLayoutDef5445 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_ruleLayoutDef5457 = new BitSet(new long[]{0x0000300000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_ruleLayoutItem_in_ruleLayoutDef5478 = new BitSet(new long[]{0x0000300000100000L,0x0000000000000010L});
    public static final BitSet FOLLOW_20_in_ruleLayoutDef5491 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleLayoutCopy_in_entryRuleLayoutCopy5527 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleLayoutCopy5537 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_67_in_ruleLayoutCopy5574 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleSTATE_NAME_REFERENCE_in_ruleLayoutCopy5597 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleLayoutItem_in_entryRuleLayoutItem5633 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleLayoutItem5643 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleWidgetRef_in_ruleLayoutItem5690 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttrWidget_in_ruleLayoutItem5717 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleComposite_in_ruleLayoutItem5744 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleComposite_in_entryRuleComposite5779 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleComposite5789 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_68_in_ruleComposite5826 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_ruleComposite5838 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_69_in_ruleComposite5850 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_RULE_INT_in_ruleComposite5867 = new BitSet(new long[]{0x0000300000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_ruleLayoutItem_in_ruleComposite5893 = new BitSet(new long[]{0x0000380000100000L,0x0000000000000010L});
    public static final BitSet FOLLOW_43_in_ruleComposite5907 = new BitSet(new long[]{0x0000000000000010L,0x0000000000000080L});
    public static final BitSet FOLLOW_ruleCompositeOption_in_ruleComposite5928 = new BitSet(new long[]{0x0000080000100000L});
    public static final BitSet FOLLOW_20_in_ruleComposite5942 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleUserDefOption_in_entryRuleUserDefOption5979 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleUserDefOption5990 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_70_in_ruleUserDefOption6028 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleUserDefOption6049 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTeamDefOption_in_entryRuleTeamDefOption6095 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleTeamDefOption6106 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_70_in_ruleTeamDefOption6144 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleTeamDefOption6165 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleActionableItemOption_in_entryRuleActionableItemOption6211 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleActionableItemOption6222 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_70_in_ruleActionableItemOption6260 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleActionableItemOption6281 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleCompositeOption_in_entryRuleCompositeOption6327 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleCompositeOption6338 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_71_in_ruleCompositeOption6376 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleCompositeOption6397 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTransitionOption_in_entryRuleTransitionOption6443 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleTransitionOption6454 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_72_in_ruleTransitionOption6492 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_73_in_ruleTransitionOption6511 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleTransitionOption6532 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRule_in_entryRuleRule6578 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRule6589 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_74_in_ruleRule6627 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_75_in_ruleRule6646 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_76_in_ruleRule6665 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_77_in_ruleRule6684 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_78_in_ruleRule6703 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_79_in_ruleRule6722 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_80_in_ruleRule6741 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_81_in_ruleRule6760 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_82_in_ruleRule6779 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_83_in_ruleRule6798 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_84_in_ruleRule6817 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleRule6838 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleWidgetOption_in_entryRuleWidgetOption6884 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleWidgetOption6895 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_85_in_ruleWidgetOption6933 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_86_in_ruleWidgetOption6952 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_87_in_ruleWidgetOption6971 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_88_in_ruleWidgetOption6990 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_89_in_ruleWidgetOption7009 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_90_in_ruleWidgetOption7028 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_91_in_ruleWidgetOption7047 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_92_in_ruleWidgetOption7066 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_93_in_ruleWidgetOption7085 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_94_in_ruleWidgetOption7104 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_95_in_ruleWidgetOption7123 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_96_in_ruleWidgetOption7142 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_97_in_ruleWidgetOption7161 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_98_in_ruleWidgetOption7180 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_99_in_ruleWidgetOption7199 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_100_in_ruleWidgetOption7218 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_101_in_ruleWidgetOption7237 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_102_in_ruleWidgetOption7256 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_103_in_ruleWidgetOption7275 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_104_in_ruleWidgetOption7294 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_105_in_ruleWidgetOption7313 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_106_in_ruleWidgetOption7332 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_107_in_ruleWidgetOption7351 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_108_in_ruleWidgetOption7370 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_109_in_ruleWidgetOption7389 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_110_in_ruleWidgetOption7408 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_111_in_ruleWidgetOption7427 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_112_in_ruleWidgetOption7446 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleWidgetOption7467 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rulePageType_in_entryRulePageType7513 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRulePageType7524 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_113_in_rulePageType7562 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_114_in_rulePageType7581 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_115_in_rulePageType7600 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rulePageType7621 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_71_in_ruleBooleanDef7680 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_116_in_ruleBooleanDef7697 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_117_in_ruleBooleanDef7714 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_118_in_ruleWorkflowEventType7759 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_119_in_ruleWorkflowEventType7776 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_120_in_ruleWorkflowEventType7793 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_121_in_ruleReviewBlockingType7838 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_122_in_ruleReviewBlockingType7855 = new BitSet(new long[]{0x0000000000000002L});

}