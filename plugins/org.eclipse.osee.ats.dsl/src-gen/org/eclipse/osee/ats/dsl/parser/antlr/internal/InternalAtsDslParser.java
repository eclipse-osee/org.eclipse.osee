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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE_STRING", "RULE_INT", "RULE_ID", "RULE_ML_COMMENT", "RULE_SL_COMMENT", "RULE_WS", "RULE_ANY_OTHER", "'userDefinition'", "'teamDefinition'", "'actionableItem'", "'workDefinition'", "'program'", "'rule'", "'{'", "'active'", "'userId'", "'email'", "'isAdmin'", "'}'", "'value'", "'id'", "'artifactType'", "'namespace'", "'attribute'", "'staticId'", "'lead'", "'member'", "'privileged'", "'relatedTaskWorkDefinition'", "'teamWorkflowArtifactType'", "'accessContextId'", "'version'", "'children'", "'actionable'", "'owner'", "'team'", "'next'", "'released'", "'allowCreateBranch'", "'allowCommitBranch'", "'baselineBranchUuid'", "'parallelVersion'", "'startState'", "'widgetDefinition'", "'attributeName'", "'description'", "'xWidgetName'", "'defaultValue'", "'height'", "'option'", "'minConstraint'", "'maxConstraint'", "'widget'", "'attributeWidget'", "'with'", "'state'", "'type'", "'ordinal'", "'percentWeight'", "'recommendedPercentComplete'", "'color'", "'decisionReview'", "'decisionReviewDefinition'", "'title'", "'relatedToState'", "'blockingType'", "'onEvent'", "'assignee'", "'autoTransitionToDecision'", "'peerReview'", "'peerReviewDefinition'", "'location'", "'followup by'", "'named'", "'to'", "'layout'", "'layoutCopyFrom'", "'composite'", "'numColumns'", "'GetOrCreate'", "'None'", "'AsDefault'", "'OverrideAttributeValidation'", "'name'", "'ruleLocation'", "'assignees'", "'relatedState'", "'taskWorkDef'", "'RequireStateHourSpentPrompt'", "'AddDecisionValidateBlockingReview'", "'AddDecisionValidateNonBlockingReview'", "'AllowTransitionWithWorkingBranch'", "'ForceAssigneesToTeamLeads'", "'RequireTargetedVersion'", "'AllowPrivilegedEditToTeamMember'", "'AllowPrivilegedEditToTeamMemberAndOriginator'", "'AllowPrivilegedEditToAll'", "'AllowEditToAll'", "'AllowAssigneeToAll'", "'AllowTransitionWithoutTaskCompletion'", "'REQUIRED_FOR_TRANSITION'", "'NOT_REQUIRED_FOR_TRANSITION'", "'REQUIRED_FOR_COMPLETION'", "'NOT_REQUIRED_FOR_COMPLETION'", "'ENABLED'", "'NOT_ENABLED'", "'EDITABLE'", "'NOT_EDITABLE'", "'FUTURE_DATE_REQUIRED'", "'NOT_FUTURE_DATE_REQUIRED'", "'MULTI_SELECT'", "'HORIZONTAL_LABEL'", "'VERTICAL_LABEL'", "'LABEL_AFTER'", "'LABEL_BEFORE'", "'NO_LABEL'", "'SORTED'", "'ADD_DEFAULT_VALUE'", "'NO_DEFAULT_VALUE'", "'BEGIN_COMPOSITE_4'", "'BEGIN_COMPOSITE_6'", "'BEGIN_COMPOSITE_8'", "'BEGIN_COMPOSITE_10'", "'END_COMPOSITE'", "'FILL_NONE'", "'FILL_HORIZONTALLY'", "'FILL_VERTICALLY'", "'ALIGN_LEFT'", "'ALIGN_RIGHT'", "'ALIGN_CENTER'", "'Working'", "'Completed'", "'Cancelled'", "'BLACK'", "'WHITE'", "'RED'", "'DARK_RED'", "'GREEN'", "'DARK_GREEN'", "'YELLOW'", "'DARK_YELLOW'", "'BLUE'", "'DARK_BLUE'", "'MAGENTA'", "'DARK_MAGENTA'", "'CYAN'", "'DARK_CYAN'", "'GRAY'", "'DARK_GRAY'", "'CreateBranch'", "'CommitBranch'", "'CreateWorkflow'", "'TransitionTo'", "'Manual'", "'True'", "'False'", "'Transition'", "'Commit'", "'StateDefinition'", "'TeamDefinition'", "'ActionableItem'"
    };
    public static final int T__144=144;
    public static final int T__143=143;
    public static final int T__146=146;
    public static final int T__50=50;
    public static final int T__145=145;
    public static final int T__140=140;
    public static final int T__142=142;
    public static final int T__141=141;
    public static final int T__59=59;
    public static final int T__55=55;
    public static final int T__56=56;
    public static final int T__57=57;
    public static final int T__58=58;
    public static final int T__51=51;
    public static final int T__137=137;
    public static final int T__52=52;
    public static final int T__136=136;
    public static final int T__53=53;
    public static final int T__139=139;
    public static final int T__54=54;
    public static final int T__138=138;
    public static final int T__133=133;
    public static final int T__132=132;
    public static final int T__60=60;
    public static final int T__135=135;
    public static final int T__61=61;
    public static final int T__134=134;
    public static final int RULE_ID=6;
    public static final int T__131=131;
    public static final int T__130=130;
    public static final int RULE_INT=5;
    public static final int T__66=66;
    public static final int RULE_ML_COMMENT=7;
    public static final int T__67=67;
    public static final int T__129=129;
    public static final int T__68=68;
    public static final int T__69=69;
    public static final int T__62=62;
    public static final int T__126=126;
    public static final int T__63=63;
    public static final int T__125=125;
    public static final int T__64=64;
    public static final int T__128=128;
    public static final int T__65=65;
    public static final int T__127=127;
    public static final int T__162=162;
    public static final int T__161=161;
    public static final int T__164=164;
    public static final int T__163=163;
    public static final int T__160=160;
    public static final int T__37=37;
    public static final int T__38=38;
    public static final int T__39=39;
    public static final int T__33=33;
    public static final int T__34=34;
    public static final int T__35=35;
    public static final int T__36=36;
    public static final int T__159=159;
    public static final int T__30=30;
    public static final int T__158=158;
    public static final int T__31=31;
    public static final int T__32=32;
    public static final int T__155=155;
    public static final int T__154=154;
    public static final int T__157=157;
    public static final int T__156=156;
    public static final int T__151=151;
    public static final int T__150=150;
    public static final int T__153=153;
    public static final int T__152=152;
    public static final int T__48=48;
    public static final int T__49=49;
    public static final int T__44=44;
    public static final int T__45=45;
    public static final int T__46=46;
    public static final int T__47=47;
    public static final int T__40=40;
    public static final int T__148=148;
    public static final int T__41=41;
    public static final int T__147=147;
    public static final int T__42=42;
    public static final int T__43=43;
    public static final int T__149=149;
    public static final int T__91=91;
    public static final int T__100=100;
    public static final int T__92=92;
    public static final int T__93=93;
    public static final int T__102=102;
    public static final int T__94=94;
    public static final int T__101=101;
    public static final int T__90=90;
    public static final int T__19=19;
    public static final int T__15=15;
    public static final int T__16=16;
    public static final int T__17=17;
    public static final int T__18=18;
    public static final int T__11=11;
    public static final int T__99=99;
    public static final int T__12=12;
    public static final int T__13=13;
    public static final int T__14=14;
    public static final int T__95=95;
    public static final int T__96=96;
    public static final int T__97=97;
    public static final int T__98=98;
    public static final int T__26=26;
    public static final int T__27=27;
    public static final int T__28=28;
    public static final int T__29=29;
    public static final int T__22=22;
    public static final int T__23=23;
    public static final int T__24=24;
    public static final int T__25=25;
    public static final int T__20=20;
    public static final int T__21=21;
    public static final int T__122=122;
    public static final int T__70=70;
    public static final int T__121=121;
    public static final int T__71=71;
    public static final int T__124=124;
    public static final int T__72=72;
    public static final int T__123=123;
    public static final int T__120=120;
    public static final int RULE_STRING=4;
    public static final int RULE_SL_COMMENT=8;
    public static final int T__77=77;
    public static final int T__119=119;
    public static final int T__78=78;
    public static final int T__118=118;
    public static final int T__79=79;
    public static final int T__73=73;
    public static final int T__115=115;
    public static final int EOF=-1;
    public static final int T__74=74;
    public static final int T__114=114;
    public static final int T__75=75;
    public static final int T__117=117;
    public static final int T__76=76;
    public static final int T__116=116;
    public static final int T__80=80;
    public static final int T__111=111;
    public static final int T__81=81;
    public static final int T__110=110;
    public static final int T__82=82;
    public static final int T__113=113;
    public static final int T__83=83;
    public static final int T__112=112;
    public static final int RULE_WS=9;
    public static final int RULE_ANY_OTHER=10;
    public static final int T__88=88;
    public static final int T__108=108;
    public static final int T__89=89;
    public static final int T__107=107;
    public static final int T__109=109;
    public static final int T__84=84;
    public static final int T__104=104;
    public static final int T__85=85;
    public static final int T__103=103;
    public static final int T__86=86;
    public static final int T__106=106;
    public static final int T__87=87;
    public static final int T__105=105;

    // delegates
    // delegators


        public InternalAtsDslParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public InternalAtsDslParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return InternalAtsDslParser.tokenNames; }
    public String getGrammarFileName() { return "InternalAtsDsl.g"; }



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
    // InternalAtsDsl.g:68:1: entryRuleAtsDsl returns [EObject current=null] : iv_ruleAtsDsl= ruleAtsDsl EOF ;
    public final EObject entryRuleAtsDsl() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAtsDsl = null;


        try {
            // InternalAtsDsl.g:69:2: (iv_ruleAtsDsl= ruleAtsDsl EOF )
            // InternalAtsDsl.g:70:2: iv_ruleAtsDsl= ruleAtsDsl EOF
            {
             newCompositeNode(grammarAccess.getAtsDslRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleAtsDsl=ruleAtsDsl();

            state._fsp--;

             current =iv_ruleAtsDsl; 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:77:1: ruleAtsDsl returns [EObject current=null] : ( ( (otherlv_0= 'userDefinition' ( (lv_userDef_1_0= ruleUserDef ) ) )* (otherlv_2= 'teamDefinition' ( (lv_teamDef_3_0= ruleTeamDef ) ) )* (otherlv_4= 'actionableItem' ( (lv_actionableItemDef_5_0= ruleActionableItemDef ) ) )* (otherlv_6= 'workDefinition' ( (lv_workDef_7_0= ruleWorkDef ) ) )* (otherlv_8= 'program' ( (lv_program_9_0= ruleProgramDef ) ) )* ) | (otherlv_10= 'rule' ( (lv_rule_11_0= ruleRule ) ) )* ) ;
    public final EObject ruleAtsDsl() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_2=null;
        Token otherlv_4=null;
        Token otherlv_6=null;
        Token otherlv_8=null;
        Token otherlv_10=null;
        EObject lv_userDef_1_0 = null;

        EObject lv_teamDef_3_0 = null;

        EObject lv_actionableItemDef_5_0 = null;

        EObject lv_workDef_7_0 = null;

        EObject lv_program_9_0 = null;

        EObject lv_rule_11_0 = null;


         enterRule(); 
            
        try {
            // InternalAtsDsl.g:80:28: ( ( ( (otherlv_0= 'userDefinition' ( (lv_userDef_1_0= ruleUserDef ) ) )* (otherlv_2= 'teamDefinition' ( (lv_teamDef_3_0= ruleTeamDef ) ) )* (otherlv_4= 'actionableItem' ( (lv_actionableItemDef_5_0= ruleActionableItemDef ) ) )* (otherlv_6= 'workDefinition' ( (lv_workDef_7_0= ruleWorkDef ) ) )* (otherlv_8= 'program' ( (lv_program_9_0= ruleProgramDef ) ) )* ) | (otherlv_10= 'rule' ( (lv_rule_11_0= ruleRule ) ) )* ) )
            // InternalAtsDsl.g:81:1: ( ( (otherlv_0= 'userDefinition' ( (lv_userDef_1_0= ruleUserDef ) ) )* (otherlv_2= 'teamDefinition' ( (lv_teamDef_3_0= ruleTeamDef ) ) )* (otherlv_4= 'actionableItem' ( (lv_actionableItemDef_5_0= ruleActionableItemDef ) ) )* (otherlv_6= 'workDefinition' ( (lv_workDef_7_0= ruleWorkDef ) ) )* (otherlv_8= 'program' ( (lv_program_9_0= ruleProgramDef ) ) )* ) | (otherlv_10= 'rule' ( (lv_rule_11_0= ruleRule ) ) )* )
            {
            // InternalAtsDsl.g:81:1: ( ( (otherlv_0= 'userDefinition' ( (lv_userDef_1_0= ruleUserDef ) ) )* (otherlv_2= 'teamDefinition' ( (lv_teamDef_3_0= ruleTeamDef ) ) )* (otherlv_4= 'actionableItem' ( (lv_actionableItemDef_5_0= ruleActionableItemDef ) ) )* (otherlv_6= 'workDefinition' ( (lv_workDef_7_0= ruleWorkDef ) ) )* (otherlv_8= 'program' ( (lv_program_9_0= ruleProgramDef ) ) )* ) | (otherlv_10= 'rule' ( (lv_rule_11_0= ruleRule ) ) )* )
            int alt7=2;
            switch ( input.LA(1) ) {
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
                {
                alt7=1;
                }
                break;
            case EOF:
                {
                alt7=1;
                }
                break;
            case 16:
                {
                alt7=2;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;
            }

            switch (alt7) {
                case 1 :
                    // InternalAtsDsl.g:81:2: ( (otherlv_0= 'userDefinition' ( (lv_userDef_1_0= ruleUserDef ) ) )* (otherlv_2= 'teamDefinition' ( (lv_teamDef_3_0= ruleTeamDef ) ) )* (otherlv_4= 'actionableItem' ( (lv_actionableItemDef_5_0= ruleActionableItemDef ) ) )* (otherlv_6= 'workDefinition' ( (lv_workDef_7_0= ruleWorkDef ) ) )* (otherlv_8= 'program' ( (lv_program_9_0= ruleProgramDef ) ) )* )
                    {
                    // InternalAtsDsl.g:81:2: ( (otherlv_0= 'userDefinition' ( (lv_userDef_1_0= ruleUserDef ) ) )* (otherlv_2= 'teamDefinition' ( (lv_teamDef_3_0= ruleTeamDef ) ) )* (otherlv_4= 'actionableItem' ( (lv_actionableItemDef_5_0= ruleActionableItemDef ) ) )* (otherlv_6= 'workDefinition' ( (lv_workDef_7_0= ruleWorkDef ) ) )* (otherlv_8= 'program' ( (lv_program_9_0= ruleProgramDef ) ) )* )
                    // InternalAtsDsl.g:81:3: (otherlv_0= 'userDefinition' ( (lv_userDef_1_0= ruleUserDef ) ) )* (otherlv_2= 'teamDefinition' ( (lv_teamDef_3_0= ruleTeamDef ) ) )* (otherlv_4= 'actionableItem' ( (lv_actionableItemDef_5_0= ruleActionableItemDef ) ) )* (otherlv_6= 'workDefinition' ( (lv_workDef_7_0= ruleWorkDef ) ) )* (otherlv_8= 'program' ( (lv_program_9_0= ruleProgramDef ) ) )*
                    {
                    // InternalAtsDsl.g:81:3: (otherlv_0= 'userDefinition' ( (lv_userDef_1_0= ruleUserDef ) ) )*
                    loop1:
                    do {
                        int alt1=2;
                        int LA1_0 = input.LA(1);

                        if ( (LA1_0==11) ) {
                            alt1=1;
                        }


                        switch (alt1) {
                    	case 1 :
                    	    // InternalAtsDsl.g:81:5: otherlv_0= 'userDefinition' ( (lv_userDef_1_0= ruleUserDef ) )
                    	    {
                    	    otherlv_0=(Token)match(input,11,FOLLOW_3); 

                    	        	newLeafNode(otherlv_0, grammarAccess.getAtsDslAccess().getUserDefinitionKeyword_0_0_0());
                    	        
                    	    // InternalAtsDsl.g:85:1: ( (lv_userDef_1_0= ruleUserDef ) )
                    	    // InternalAtsDsl.g:86:1: (lv_userDef_1_0= ruleUserDef )
                    	    {
                    	    // InternalAtsDsl.g:86:1: (lv_userDef_1_0= ruleUserDef )
                    	    // InternalAtsDsl.g:87:3: lv_userDef_1_0= ruleUserDef
                    	    {
                    	     
                    	    	        newCompositeNode(grammarAccess.getAtsDslAccess().getUserDefUserDefParserRuleCall_0_0_1_0()); 
                    	    	    
                    	    pushFollow(FOLLOW_4);
                    	    lv_userDef_1_0=ruleUserDef();

                    	    state._fsp--;


                    	    	        if (current==null) {
                    	    	            current = createModelElementForParent(grammarAccess.getAtsDslRule());
                    	    	        }
                    	           		add(
                    	           			current, 
                    	           			"userDef",
                    	            		lv_userDef_1_0, 
                    	            		"org.eclipse.osee.ats.dsl.AtsDsl.UserDef");
                    	    	        afterParserOrEnumRuleCall();
                    	    	    

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop1;
                        }
                    } while (true);

                    // InternalAtsDsl.g:103:4: (otherlv_2= 'teamDefinition' ( (lv_teamDef_3_0= ruleTeamDef ) ) )*
                    loop2:
                    do {
                        int alt2=2;
                        int LA2_0 = input.LA(1);

                        if ( (LA2_0==12) ) {
                            alt2=1;
                        }


                        switch (alt2) {
                    	case 1 :
                    	    // InternalAtsDsl.g:103:6: otherlv_2= 'teamDefinition' ( (lv_teamDef_3_0= ruleTeamDef ) )
                    	    {
                    	    otherlv_2=(Token)match(input,12,FOLLOW_3); 

                    	        	newLeafNode(otherlv_2, grammarAccess.getAtsDslAccess().getTeamDefinitionKeyword_0_1_0());
                    	        
                    	    // InternalAtsDsl.g:107:1: ( (lv_teamDef_3_0= ruleTeamDef ) )
                    	    // InternalAtsDsl.g:108:1: (lv_teamDef_3_0= ruleTeamDef )
                    	    {
                    	    // InternalAtsDsl.g:108:1: (lv_teamDef_3_0= ruleTeamDef )
                    	    // InternalAtsDsl.g:109:3: lv_teamDef_3_0= ruleTeamDef
                    	    {
                    	     
                    	    	        newCompositeNode(grammarAccess.getAtsDslAccess().getTeamDefTeamDefParserRuleCall_0_1_1_0()); 
                    	    	    
                    	    pushFollow(FOLLOW_5);
                    	    lv_teamDef_3_0=ruleTeamDef();

                    	    state._fsp--;


                    	    	        if (current==null) {
                    	    	            current = createModelElementForParent(grammarAccess.getAtsDslRule());
                    	    	        }
                    	           		add(
                    	           			current, 
                    	           			"teamDef",
                    	            		lv_teamDef_3_0, 
                    	            		"org.eclipse.osee.ats.dsl.AtsDsl.TeamDef");
                    	    	        afterParserOrEnumRuleCall();
                    	    	    

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop2;
                        }
                    } while (true);

                    // InternalAtsDsl.g:125:4: (otherlv_4= 'actionableItem' ( (lv_actionableItemDef_5_0= ruleActionableItemDef ) ) )*
                    loop3:
                    do {
                        int alt3=2;
                        int LA3_0 = input.LA(1);

                        if ( (LA3_0==13) ) {
                            alt3=1;
                        }


                        switch (alt3) {
                    	case 1 :
                    	    // InternalAtsDsl.g:125:6: otherlv_4= 'actionableItem' ( (lv_actionableItemDef_5_0= ruleActionableItemDef ) )
                    	    {
                    	    otherlv_4=(Token)match(input,13,FOLLOW_3); 

                    	        	newLeafNode(otherlv_4, grammarAccess.getAtsDslAccess().getActionableItemKeyword_0_2_0());
                    	        
                    	    // InternalAtsDsl.g:129:1: ( (lv_actionableItemDef_5_0= ruleActionableItemDef ) )
                    	    // InternalAtsDsl.g:130:1: (lv_actionableItemDef_5_0= ruleActionableItemDef )
                    	    {
                    	    // InternalAtsDsl.g:130:1: (lv_actionableItemDef_5_0= ruleActionableItemDef )
                    	    // InternalAtsDsl.g:131:3: lv_actionableItemDef_5_0= ruleActionableItemDef
                    	    {
                    	     
                    	    	        newCompositeNode(grammarAccess.getAtsDslAccess().getActionableItemDefActionableItemDefParserRuleCall_0_2_1_0()); 
                    	    	    
                    	    pushFollow(FOLLOW_6);
                    	    lv_actionableItemDef_5_0=ruleActionableItemDef();

                    	    state._fsp--;


                    	    	        if (current==null) {
                    	    	            current = createModelElementForParent(grammarAccess.getAtsDslRule());
                    	    	        }
                    	           		add(
                    	           			current, 
                    	           			"actionableItemDef",
                    	            		lv_actionableItemDef_5_0, 
                    	            		"org.eclipse.osee.ats.dsl.AtsDsl.ActionableItemDef");
                    	    	        afterParserOrEnumRuleCall();
                    	    	    

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop3;
                        }
                    } while (true);

                    // InternalAtsDsl.g:147:4: (otherlv_6= 'workDefinition' ( (lv_workDef_7_0= ruleWorkDef ) ) )*
                    loop4:
                    do {
                        int alt4=2;
                        int LA4_0 = input.LA(1);

                        if ( (LA4_0==14) ) {
                            alt4=1;
                        }


                        switch (alt4) {
                    	case 1 :
                    	    // InternalAtsDsl.g:147:6: otherlv_6= 'workDefinition' ( (lv_workDef_7_0= ruleWorkDef ) )
                    	    {
                    	    otherlv_6=(Token)match(input,14,FOLLOW_3); 

                    	        	newLeafNode(otherlv_6, grammarAccess.getAtsDslAccess().getWorkDefinitionKeyword_0_3_0());
                    	        
                    	    // InternalAtsDsl.g:151:1: ( (lv_workDef_7_0= ruleWorkDef ) )
                    	    // InternalAtsDsl.g:152:1: (lv_workDef_7_0= ruleWorkDef )
                    	    {
                    	    // InternalAtsDsl.g:152:1: (lv_workDef_7_0= ruleWorkDef )
                    	    // InternalAtsDsl.g:153:3: lv_workDef_7_0= ruleWorkDef
                    	    {
                    	     
                    	    	        newCompositeNode(grammarAccess.getAtsDslAccess().getWorkDefWorkDefParserRuleCall_0_3_1_0()); 
                    	    	    
                    	    pushFollow(FOLLOW_7);
                    	    lv_workDef_7_0=ruleWorkDef();

                    	    state._fsp--;


                    	    	        if (current==null) {
                    	    	            current = createModelElementForParent(grammarAccess.getAtsDslRule());
                    	    	        }
                    	           		add(
                    	           			current, 
                    	           			"workDef",
                    	            		lv_workDef_7_0, 
                    	            		"org.eclipse.osee.ats.dsl.AtsDsl.WorkDef");
                    	    	        afterParserOrEnumRuleCall();
                    	    	    

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop4;
                        }
                    } while (true);

                    // InternalAtsDsl.g:169:4: (otherlv_8= 'program' ( (lv_program_9_0= ruleProgramDef ) ) )*
                    loop5:
                    do {
                        int alt5=2;
                        int LA5_0 = input.LA(1);

                        if ( (LA5_0==15) ) {
                            alt5=1;
                        }


                        switch (alt5) {
                    	case 1 :
                    	    // InternalAtsDsl.g:169:6: otherlv_8= 'program' ( (lv_program_9_0= ruleProgramDef ) )
                    	    {
                    	    otherlv_8=(Token)match(input,15,FOLLOW_3); 

                    	        	newLeafNode(otherlv_8, grammarAccess.getAtsDslAccess().getProgramKeyword_0_4_0());
                    	        
                    	    // InternalAtsDsl.g:173:1: ( (lv_program_9_0= ruleProgramDef ) )
                    	    // InternalAtsDsl.g:174:1: (lv_program_9_0= ruleProgramDef )
                    	    {
                    	    // InternalAtsDsl.g:174:1: (lv_program_9_0= ruleProgramDef )
                    	    // InternalAtsDsl.g:175:3: lv_program_9_0= ruleProgramDef
                    	    {
                    	     
                    	    	        newCompositeNode(grammarAccess.getAtsDslAccess().getProgramProgramDefParserRuleCall_0_4_1_0()); 
                    	    	    
                    	    pushFollow(FOLLOW_8);
                    	    lv_program_9_0=ruleProgramDef();

                    	    state._fsp--;


                    	    	        if (current==null) {
                    	    	            current = createModelElementForParent(grammarAccess.getAtsDslRule());
                    	    	        }
                    	           		add(
                    	           			current, 
                    	           			"program",
                    	            		lv_program_9_0, 
                    	            		"org.eclipse.osee.ats.dsl.AtsDsl.ProgramDef");
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
                    break;
                case 2 :
                    // InternalAtsDsl.g:192:6: (otherlv_10= 'rule' ( (lv_rule_11_0= ruleRule ) ) )*
                    {
                    // InternalAtsDsl.g:192:6: (otherlv_10= 'rule' ( (lv_rule_11_0= ruleRule ) ) )*
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( (LA6_0==16) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // InternalAtsDsl.g:192:8: otherlv_10= 'rule' ( (lv_rule_11_0= ruleRule ) )
                    	    {
                    	    otherlv_10=(Token)match(input,16,FOLLOW_9); 

                    	        	newLeafNode(otherlv_10, grammarAccess.getAtsDslAccess().getRuleKeyword_1_0());
                    	        
                    	    // InternalAtsDsl.g:196:1: ( (lv_rule_11_0= ruleRule ) )
                    	    // InternalAtsDsl.g:197:1: (lv_rule_11_0= ruleRule )
                    	    {
                    	    // InternalAtsDsl.g:197:1: (lv_rule_11_0= ruleRule )
                    	    // InternalAtsDsl.g:198:3: lv_rule_11_0= ruleRule
                    	    {
                    	     
                    	    	        newCompositeNode(grammarAccess.getAtsDslAccess().getRuleRuleParserRuleCall_1_1_0()); 
                    	    	    
                    	    pushFollow(FOLLOW_10);
                    	    lv_rule_11_0=ruleRule();

                    	    state._fsp--;


                    	    	        if (current==null) {
                    	    	            current = createModelElementForParent(grammarAccess.getAtsDslRule());
                    	    	        }
                    	           		add(
                    	           			current, 
                    	           			"rule",
                    	            		lv_rule_11_0, 
                    	            		"org.eclipse.osee.ats.dsl.AtsDsl.Rule");
                    	    	        afterParserOrEnumRuleCall();
                    	    	    

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
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
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleAtsDsl"


    // $ANTLR start "entryRuleUSER_DEF_REFERENCE"
    // InternalAtsDsl.g:222:1: entryRuleUSER_DEF_REFERENCE returns [String current=null] : iv_ruleUSER_DEF_REFERENCE= ruleUSER_DEF_REFERENCE EOF ;
    public final String entryRuleUSER_DEF_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleUSER_DEF_REFERENCE = null;


        try {
            // InternalAtsDsl.g:223:2: (iv_ruleUSER_DEF_REFERENCE= ruleUSER_DEF_REFERENCE EOF )
            // InternalAtsDsl.g:224:2: iv_ruleUSER_DEF_REFERENCE= ruleUSER_DEF_REFERENCE EOF
            {
             newCompositeNode(grammarAccess.getUSER_DEF_REFERENCERule()); 
            pushFollow(FOLLOW_1);
            iv_ruleUSER_DEF_REFERENCE=ruleUSER_DEF_REFERENCE();

            state._fsp--;

             current =iv_ruleUSER_DEF_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:231:1: ruleUSER_DEF_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleUSER_DEF_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // InternalAtsDsl.g:234:28: (this_STRING_0= RULE_STRING )
            // InternalAtsDsl.g:235:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_2); 

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
    // InternalAtsDsl.g:250:1: entryRuleUserDef returns [EObject current=null] : iv_ruleUserDef= ruleUserDef EOF ;
    public final EObject entryRuleUserDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleUserDef = null;


        try {
            // InternalAtsDsl.g:251:2: (iv_ruleUserDef= ruleUserDef EOF )
            // InternalAtsDsl.g:252:2: iv_ruleUserDef= ruleUserDef EOF
            {
             newCompositeNode(grammarAccess.getUserDefRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleUserDef=ruleUserDef();

            state._fsp--;

             current =iv_ruleUserDef; 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:259:1: ruleUserDef returns [EObject current=null] : ( ( (lv_name_0_0= ruleUSER_DEF_REFERENCE ) ) ( (lv_userDefOption_1_0= ruleUserDefOption ) )* (otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'userId' ( (lv_userId_6_0= RULE_STRING ) ) )? (otherlv_7= 'email' ( (lv_email_8_0= RULE_STRING ) ) )? (otherlv_9= 'isAdmin' ( (lv_admin_10_0= ruleBooleanDef ) ) )? otherlv_11= '}' )? ) ;
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
            // InternalAtsDsl.g:262:28: ( ( ( (lv_name_0_0= ruleUSER_DEF_REFERENCE ) ) ( (lv_userDefOption_1_0= ruleUserDefOption ) )* (otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'userId' ( (lv_userId_6_0= RULE_STRING ) ) )? (otherlv_7= 'email' ( (lv_email_8_0= RULE_STRING ) ) )? (otherlv_9= 'isAdmin' ( (lv_admin_10_0= ruleBooleanDef ) ) )? otherlv_11= '}' )? ) )
            // InternalAtsDsl.g:263:1: ( ( (lv_name_0_0= ruleUSER_DEF_REFERENCE ) ) ( (lv_userDefOption_1_0= ruleUserDefOption ) )* (otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'userId' ( (lv_userId_6_0= RULE_STRING ) ) )? (otherlv_7= 'email' ( (lv_email_8_0= RULE_STRING ) ) )? (otherlv_9= 'isAdmin' ( (lv_admin_10_0= ruleBooleanDef ) ) )? otherlv_11= '}' )? )
            {
            // InternalAtsDsl.g:263:1: ( ( (lv_name_0_0= ruleUSER_DEF_REFERENCE ) ) ( (lv_userDefOption_1_0= ruleUserDefOption ) )* (otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'userId' ( (lv_userId_6_0= RULE_STRING ) ) )? (otherlv_7= 'email' ( (lv_email_8_0= RULE_STRING ) ) )? (otherlv_9= 'isAdmin' ( (lv_admin_10_0= ruleBooleanDef ) ) )? otherlv_11= '}' )? )
            // InternalAtsDsl.g:263:2: ( (lv_name_0_0= ruleUSER_DEF_REFERENCE ) ) ( (lv_userDefOption_1_0= ruleUserDefOption ) )* (otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'userId' ( (lv_userId_6_0= RULE_STRING ) ) )? (otherlv_7= 'email' ( (lv_email_8_0= RULE_STRING ) ) )? (otherlv_9= 'isAdmin' ( (lv_admin_10_0= ruleBooleanDef ) ) )? otherlv_11= '}' )?
            {
            // InternalAtsDsl.g:263:2: ( (lv_name_0_0= ruleUSER_DEF_REFERENCE ) )
            // InternalAtsDsl.g:264:1: (lv_name_0_0= ruleUSER_DEF_REFERENCE )
            {
            // InternalAtsDsl.g:264:1: (lv_name_0_0= ruleUSER_DEF_REFERENCE )
            // InternalAtsDsl.g:265:3: lv_name_0_0= ruleUSER_DEF_REFERENCE
            {
             
            	        newCompositeNode(grammarAccess.getUserDefAccess().getNameUSER_DEF_REFERENCEParserRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_11);
            lv_name_0_0=ruleUSER_DEF_REFERENCE();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getUserDefRule());
            	        }
                   		set(
                   			current, 
                   			"name",
                    		lv_name_0_0, 
                    		"org.eclipse.osee.ats.dsl.AtsDsl.USER_DEF_REFERENCE");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // InternalAtsDsl.g:281:2: ( (lv_userDefOption_1_0= ruleUserDefOption ) )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==RULE_STRING||LA8_0==83) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // InternalAtsDsl.g:282:1: (lv_userDefOption_1_0= ruleUserDefOption )
            	    {
            	    // InternalAtsDsl.g:282:1: (lv_userDefOption_1_0= ruleUserDefOption )
            	    // InternalAtsDsl.g:283:3: lv_userDefOption_1_0= ruleUserDefOption
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getUserDefAccess().getUserDefOptionUserDefOptionParserRuleCall_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_11);
            	    lv_userDefOption_1_0=ruleUserDefOption();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getUserDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"userDefOption",
            	            		lv_userDefOption_1_0, 
            	            		"org.eclipse.osee.ats.dsl.AtsDsl.UserDefOption");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);

            // InternalAtsDsl.g:299:3: (otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'userId' ( (lv_userId_6_0= RULE_STRING ) ) )? (otherlv_7= 'email' ( (lv_email_8_0= RULE_STRING ) ) )? (otherlv_9= 'isAdmin' ( (lv_admin_10_0= ruleBooleanDef ) ) )? otherlv_11= '}' )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==17) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // InternalAtsDsl.g:299:5: otherlv_2= '{' (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )? (otherlv_5= 'userId' ( (lv_userId_6_0= RULE_STRING ) ) )? (otherlv_7= 'email' ( (lv_email_8_0= RULE_STRING ) ) )? (otherlv_9= 'isAdmin' ( (lv_admin_10_0= ruleBooleanDef ) ) )? otherlv_11= '}'
                    {
                    otherlv_2=(Token)match(input,17,FOLLOW_12); 

                        	newLeafNode(otherlv_2, grammarAccess.getUserDefAccess().getLeftCurlyBracketKeyword_2_0());
                        
                    // InternalAtsDsl.g:303:1: (otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) ) )?
                    int alt9=2;
                    int LA9_0 = input.LA(1);

                    if ( (LA9_0==18) ) {
                        alt9=1;
                    }
                    switch (alt9) {
                        case 1 :
                            // InternalAtsDsl.g:303:3: otherlv_3= 'active' ( (lv_active_4_0= ruleBooleanDef ) )
                            {
                            otherlv_3=(Token)match(input,18,FOLLOW_13); 

                                	newLeafNode(otherlv_3, grammarAccess.getUserDefAccess().getActiveKeyword_2_1_0());
                                
                            // InternalAtsDsl.g:307:1: ( (lv_active_4_0= ruleBooleanDef ) )
                            // InternalAtsDsl.g:308:1: (lv_active_4_0= ruleBooleanDef )
                            {
                            // InternalAtsDsl.g:308:1: (lv_active_4_0= ruleBooleanDef )
                            // InternalAtsDsl.g:309:3: lv_active_4_0= ruleBooleanDef
                            {
                             
                            	        newCompositeNode(grammarAccess.getUserDefAccess().getActiveBooleanDefEnumRuleCall_2_1_1_0()); 
                            	    
                            pushFollow(FOLLOW_14);
                            lv_active_4_0=ruleBooleanDef();

                            state._fsp--;


                            	        if (current==null) {
                            	            current = createModelElementForParent(grammarAccess.getUserDefRule());
                            	        }
                                   		set(
                                   			current, 
                                   			"active",
                                    		lv_active_4_0, 
                                    		"org.eclipse.osee.ats.dsl.AtsDsl.BooleanDef");
                            	        afterParserOrEnumRuleCall();
                            	    

                            }


                            }


                            }
                            break;

                    }

                    // InternalAtsDsl.g:325:4: (otherlv_5= 'userId' ( (lv_userId_6_0= RULE_STRING ) ) )?
                    int alt10=2;
                    int LA10_0 = input.LA(1);

                    if ( (LA10_0==19) ) {
                        alt10=1;
                    }
                    switch (alt10) {
                        case 1 :
                            // InternalAtsDsl.g:325:6: otherlv_5= 'userId' ( (lv_userId_6_0= RULE_STRING ) )
                            {
                            otherlv_5=(Token)match(input,19,FOLLOW_3); 

                                	newLeafNode(otherlv_5, grammarAccess.getUserDefAccess().getUserIdKeyword_2_2_0());
                                
                            // InternalAtsDsl.g:329:1: ( (lv_userId_6_0= RULE_STRING ) )
                            // InternalAtsDsl.g:330:1: (lv_userId_6_0= RULE_STRING )
                            {
                            // InternalAtsDsl.g:330:1: (lv_userId_6_0= RULE_STRING )
                            // InternalAtsDsl.g:331:3: lv_userId_6_0= RULE_STRING
                            {
                            lv_userId_6_0=(Token)match(input,RULE_STRING,FOLLOW_15); 

                            			newLeafNode(lv_userId_6_0, grammarAccess.getUserDefAccess().getUserIdSTRINGTerminalRuleCall_2_2_1_0()); 
                            		

                            	        if (current==null) {
                            	            current = createModelElement(grammarAccess.getUserDefRule());
                            	        }
                                   		setWithLastConsumed(
                                   			current, 
                                   			"userId",
                                    		lv_userId_6_0, 
                                    		"org.eclipse.xtext.common.Terminals.STRING");
                            	    

                            }


                            }


                            }
                            break;

                    }

                    // InternalAtsDsl.g:347:4: (otherlv_7= 'email' ( (lv_email_8_0= RULE_STRING ) ) )?
                    int alt11=2;
                    int LA11_0 = input.LA(1);

                    if ( (LA11_0==20) ) {
                        alt11=1;
                    }
                    switch (alt11) {
                        case 1 :
                            // InternalAtsDsl.g:347:6: otherlv_7= 'email' ( (lv_email_8_0= RULE_STRING ) )
                            {
                            otherlv_7=(Token)match(input,20,FOLLOW_3); 

                                	newLeafNode(otherlv_7, grammarAccess.getUserDefAccess().getEmailKeyword_2_3_0());
                                
                            // InternalAtsDsl.g:351:1: ( (lv_email_8_0= RULE_STRING ) )
                            // InternalAtsDsl.g:352:1: (lv_email_8_0= RULE_STRING )
                            {
                            // InternalAtsDsl.g:352:1: (lv_email_8_0= RULE_STRING )
                            // InternalAtsDsl.g:353:3: lv_email_8_0= RULE_STRING
                            {
                            lv_email_8_0=(Token)match(input,RULE_STRING,FOLLOW_16); 

                            			newLeafNode(lv_email_8_0, grammarAccess.getUserDefAccess().getEmailSTRINGTerminalRuleCall_2_3_1_0()); 
                            		

                            	        if (current==null) {
                            	            current = createModelElement(grammarAccess.getUserDefRule());
                            	        }
                                   		setWithLastConsumed(
                                   			current, 
                                   			"email",
                                    		lv_email_8_0, 
                                    		"org.eclipse.xtext.common.Terminals.STRING");
                            	    

                            }


                            }


                            }
                            break;

                    }

                    // InternalAtsDsl.g:369:4: (otherlv_9= 'isAdmin' ( (lv_admin_10_0= ruleBooleanDef ) ) )?
                    int alt12=2;
                    int LA12_0 = input.LA(1);

                    if ( (LA12_0==21) ) {
                        alt12=1;
                    }
                    switch (alt12) {
                        case 1 :
                            // InternalAtsDsl.g:369:6: otherlv_9= 'isAdmin' ( (lv_admin_10_0= ruleBooleanDef ) )
                            {
                            otherlv_9=(Token)match(input,21,FOLLOW_13); 

                                	newLeafNode(otherlv_9, grammarAccess.getUserDefAccess().getIsAdminKeyword_2_4_0());
                                
                            // InternalAtsDsl.g:373:1: ( (lv_admin_10_0= ruleBooleanDef ) )
                            // InternalAtsDsl.g:374:1: (lv_admin_10_0= ruleBooleanDef )
                            {
                            // InternalAtsDsl.g:374:1: (lv_admin_10_0= ruleBooleanDef )
                            // InternalAtsDsl.g:375:3: lv_admin_10_0= ruleBooleanDef
                            {
                             
                            	        newCompositeNode(grammarAccess.getUserDefAccess().getAdminBooleanDefEnumRuleCall_2_4_1_0()); 
                            	    
                            pushFollow(FOLLOW_17);
                            lv_admin_10_0=ruleBooleanDef();

                            state._fsp--;


                            	        if (current==null) {
                            	            current = createModelElementForParent(grammarAccess.getUserDefRule());
                            	        }
                                   		set(
                                   			current, 
                                   			"admin",
                                    		lv_admin_10_0, 
                                    		"org.eclipse.osee.ats.dsl.AtsDsl.BooleanDef");
                            	        afterParserOrEnumRuleCall();
                            	    

                            }


                            }


                            }
                            break;

                    }

                    otherlv_11=(Token)match(input,22,FOLLOW_2); 

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
    // InternalAtsDsl.g:403:1: entryRuleATTR_DEF_REFERENCE returns [String current=null] : iv_ruleATTR_DEF_REFERENCE= ruleATTR_DEF_REFERENCE EOF ;
    public final String entryRuleATTR_DEF_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleATTR_DEF_REFERENCE = null;


        try {
            // InternalAtsDsl.g:404:2: (iv_ruleATTR_DEF_REFERENCE= ruleATTR_DEF_REFERENCE EOF )
            // InternalAtsDsl.g:405:2: iv_ruleATTR_DEF_REFERENCE= ruleATTR_DEF_REFERENCE EOF
            {
             newCompositeNode(grammarAccess.getATTR_DEF_REFERENCERule()); 
            pushFollow(FOLLOW_1);
            iv_ruleATTR_DEF_REFERENCE=ruleATTR_DEF_REFERENCE();

            state._fsp--;

             current =iv_ruleATTR_DEF_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:412:1: ruleATTR_DEF_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleATTR_DEF_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // InternalAtsDsl.g:415:28: (this_STRING_0= RULE_STRING )
            // InternalAtsDsl.g:416:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_2); 

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
    // InternalAtsDsl.g:431:1: entryRuleAttrDef returns [EObject current=null] : iv_ruleAttrDef= ruleAttrDef EOF ;
    public final EObject entryRuleAttrDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttrDef = null;


        try {
            // InternalAtsDsl.g:432:2: (iv_ruleAttrDef= ruleAttrDef EOF )
            // InternalAtsDsl.g:433:2: iv_ruleAttrDef= ruleAttrDef EOF
            {
             newCompositeNode(grammarAccess.getAttrDefRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleAttrDef=ruleAttrDef();

            state._fsp--;

             current =iv_ruleAttrDef; 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:440:1: ruleAttrDef returns [EObject current=null] : ( ( (lv_name_0_0= ruleATTR_DEF_REFERENCE ) ) ( (lv_option_1_0= ruleAttrDefOptions ) ) ) ;
    public final EObject ruleAttrDef() throws RecognitionException {
        EObject current = null;

        AntlrDatatypeRuleToken lv_name_0_0 = null;

        EObject lv_option_1_0 = null;


         enterRule(); 
            
        try {
            // InternalAtsDsl.g:443:28: ( ( ( (lv_name_0_0= ruleATTR_DEF_REFERENCE ) ) ( (lv_option_1_0= ruleAttrDefOptions ) ) ) )
            // InternalAtsDsl.g:444:1: ( ( (lv_name_0_0= ruleATTR_DEF_REFERENCE ) ) ( (lv_option_1_0= ruleAttrDefOptions ) ) )
            {
            // InternalAtsDsl.g:444:1: ( ( (lv_name_0_0= ruleATTR_DEF_REFERENCE ) ) ( (lv_option_1_0= ruleAttrDefOptions ) ) )
            // InternalAtsDsl.g:444:2: ( (lv_name_0_0= ruleATTR_DEF_REFERENCE ) ) ( (lv_option_1_0= ruleAttrDefOptions ) )
            {
            // InternalAtsDsl.g:444:2: ( (lv_name_0_0= ruleATTR_DEF_REFERENCE ) )
            // InternalAtsDsl.g:445:1: (lv_name_0_0= ruleATTR_DEF_REFERENCE )
            {
            // InternalAtsDsl.g:445:1: (lv_name_0_0= ruleATTR_DEF_REFERENCE )
            // InternalAtsDsl.g:446:3: lv_name_0_0= ruleATTR_DEF_REFERENCE
            {
             
            	        newCompositeNode(grammarAccess.getAttrDefAccess().getNameATTR_DEF_REFERENCEParserRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_18);
            lv_name_0_0=ruleATTR_DEF_REFERENCE();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getAttrDefRule());
            	        }
                   		set(
                   			current, 
                   			"name",
                    		lv_name_0_0, 
                    		"org.eclipse.osee.ats.dsl.AtsDsl.ATTR_DEF_REFERENCE");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // InternalAtsDsl.g:462:2: ( (lv_option_1_0= ruleAttrDefOptions ) )
            // InternalAtsDsl.g:463:1: (lv_option_1_0= ruleAttrDefOptions )
            {
            // InternalAtsDsl.g:463:1: (lv_option_1_0= ruleAttrDefOptions )
            // InternalAtsDsl.g:464:3: lv_option_1_0= ruleAttrDefOptions
            {
             
            	        newCompositeNode(grammarAccess.getAttrDefAccess().getOptionAttrDefOptionsParserRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_2);
            lv_option_1_0=ruleAttrDefOptions();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getAttrDefRule());
            	        }
                   		set(
                   			current, 
                   			"option",
                    		lv_option_1_0, 
                    		"org.eclipse.osee.ats.dsl.AtsDsl.AttrDefOptions");
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
    // InternalAtsDsl.g:488:1: entryRuleAttrDefOptions returns [EObject current=null] : iv_ruleAttrDefOptions= ruleAttrDefOptions EOF ;
    public final EObject entryRuleAttrDefOptions() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttrDefOptions = null;


        try {
            // InternalAtsDsl.g:489:2: (iv_ruleAttrDefOptions= ruleAttrDefOptions EOF )
            // InternalAtsDsl.g:490:2: iv_ruleAttrDefOptions= ruleAttrDefOptions EOF
            {
             newCompositeNode(grammarAccess.getAttrDefOptionsRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleAttrDefOptions=ruleAttrDefOptions();

            state._fsp--;

             current =iv_ruleAttrDefOptions; 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:497:1: ruleAttrDefOptions returns [EObject current=null] : (this_AttrValueDef_0= ruleAttrValueDef | this_AttrFullDef_1= ruleAttrFullDef ) ;
    public final EObject ruleAttrDefOptions() throws RecognitionException {
        EObject current = null;

        EObject this_AttrValueDef_0 = null;

        EObject this_AttrFullDef_1 = null;


         enterRule(); 
            
        try {
            // InternalAtsDsl.g:500:28: ( (this_AttrValueDef_0= ruleAttrValueDef | this_AttrFullDef_1= ruleAttrFullDef ) )
            // InternalAtsDsl.g:501:1: (this_AttrValueDef_0= ruleAttrValueDef | this_AttrFullDef_1= ruleAttrFullDef )
            {
            // InternalAtsDsl.g:501:1: (this_AttrValueDef_0= ruleAttrValueDef | this_AttrFullDef_1= ruleAttrFullDef )
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==23) ) {
                alt14=1;
            }
            else if ( (LA14_0==17) ) {
                alt14=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 14, 0, input);

                throw nvae;
            }
            switch (alt14) {
                case 1 :
                    // InternalAtsDsl.g:502:5: this_AttrValueDef_0= ruleAttrValueDef
                    {
                     
                            newCompositeNode(grammarAccess.getAttrDefOptionsAccess().getAttrValueDefParserRuleCall_0()); 
                        
                    pushFollow(FOLLOW_2);
                    this_AttrValueDef_0=ruleAttrValueDef();

                    state._fsp--;

                     
                            current = this_AttrValueDef_0; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // InternalAtsDsl.g:512:5: this_AttrFullDef_1= ruleAttrFullDef
                    {
                     
                            newCompositeNode(grammarAccess.getAttrDefOptionsAccess().getAttrFullDefParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_2);
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
    // InternalAtsDsl.g:528:1: entryRuleAttrValueDef returns [EObject current=null] : iv_ruleAttrValueDef= ruleAttrValueDef EOF ;
    public final EObject entryRuleAttrValueDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttrValueDef = null;


        try {
            // InternalAtsDsl.g:529:2: (iv_ruleAttrValueDef= ruleAttrValueDef EOF )
            // InternalAtsDsl.g:530:2: iv_ruleAttrValueDef= ruleAttrValueDef EOF
            {
             newCompositeNode(grammarAccess.getAttrValueDefRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleAttrValueDef=ruleAttrValueDef();

            state._fsp--;

             current =iv_ruleAttrValueDef; 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:537:1: ruleAttrValueDef returns [EObject current=null] : (otherlv_0= 'value' ( (lv_value_1_0= RULE_STRING ) ) ) ;
    public final EObject ruleAttrValueDef() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_value_1_0=null;

         enterRule(); 
            
        try {
            // InternalAtsDsl.g:540:28: ( (otherlv_0= 'value' ( (lv_value_1_0= RULE_STRING ) ) ) )
            // InternalAtsDsl.g:541:1: (otherlv_0= 'value' ( (lv_value_1_0= RULE_STRING ) ) )
            {
            // InternalAtsDsl.g:541:1: (otherlv_0= 'value' ( (lv_value_1_0= RULE_STRING ) ) )
            // InternalAtsDsl.g:541:3: otherlv_0= 'value' ( (lv_value_1_0= RULE_STRING ) )
            {
            otherlv_0=(Token)match(input,23,FOLLOW_3); 

                	newLeafNode(otherlv_0, grammarAccess.getAttrValueDefAccess().getValueKeyword_0());
                
            // InternalAtsDsl.g:545:1: ( (lv_value_1_0= RULE_STRING ) )
            // InternalAtsDsl.g:546:1: (lv_value_1_0= RULE_STRING )
            {
            // InternalAtsDsl.g:546:1: (lv_value_1_0= RULE_STRING )
            // InternalAtsDsl.g:547:3: lv_value_1_0= RULE_STRING
            {
            lv_value_1_0=(Token)match(input,RULE_STRING,FOLLOW_2); 

            			newLeafNode(lv_value_1_0, grammarAccess.getAttrValueDefAccess().getValueSTRINGTerminalRuleCall_1_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getAttrValueDefRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"value",
                    		lv_value_1_0, 
                    		"org.eclipse.xtext.common.Terminals.STRING");
            	    

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
    // InternalAtsDsl.g:571:1: entryRuleAttrFullDef returns [EObject current=null] : iv_ruleAttrFullDef= ruleAttrFullDef EOF ;
    public final EObject entryRuleAttrFullDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttrFullDef = null;


        try {
            // InternalAtsDsl.g:572:2: (iv_ruleAttrFullDef= ruleAttrFullDef EOF )
            // InternalAtsDsl.g:573:2: iv_ruleAttrFullDef= ruleAttrFullDef EOF
            {
             newCompositeNode(grammarAccess.getAttrFullDefRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleAttrFullDef=ruleAttrFullDef();

            state._fsp--;

             current =iv_ruleAttrFullDef; 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:580:1: ruleAttrFullDef returns [EObject current=null] : (otherlv_0= '{' (otherlv_1= 'id' ( (lv_uuid_2_0= RULE_STRING ) ) )? (otherlv_3= 'value' ( (lv_values_4_0= RULE_STRING ) ) )+ otherlv_5= '}' ) ;
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
            // InternalAtsDsl.g:583:28: ( (otherlv_0= '{' (otherlv_1= 'id' ( (lv_uuid_2_0= RULE_STRING ) ) )? (otherlv_3= 'value' ( (lv_values_4_0= RULE_STRING ) ) )+ otherlv_5= '}' ) )
            // InternalAtsDsl.g:584:1: (otherlv_0= '{' (otherlv_1= 'id' ( (lv_uuid_2_0= RULE_STRING ) ) )? (otherlv_3= 'value' ( (lv_values_4_0= RULE_STRING ) ) )+ otherlv_5= '}' )
            {
            // InternalAtsDsl.g:584:1: (otherlv_0= '{' (otherlv_1= 'id' ( (lv_uuid_2_0= RULE_STRING ) ) )? (otherlv_3= 'value' ( (lv_values_4_0= RULE_STRING ) ) )+ otherlv_5= '}' )
            // InternalAtsDsl.g:584:3: otherlv_0= '{' (otherlv_1= 'id' ( (lv_uuid_2_0= RULE_STRING ) ) )? (otherlv_3= 'value' ( (lv_values_4_0= RULE_STRING ) ) )+ otherlv_5= '}'
            {
            otherlv_0=(Token)match(input,17,FOLLOW_19); 

                	newLeafNode(otherlv_0, grammarAccess.getAttrFullDefAccess().getLeftCurlyBracketKeyword_0());
                
            // InternalAtsDsl.g:588:1: (otherlv_1= 'id' ( (lv_uuid_2_0= RULE_STRING ) ) )?
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==24) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // InternalAtsDsl.g:588:3: otherlv_1= 'id' ( (lv_uuid_2_0= RULE_STRING ) )
                    {
                    otherlv_1=(Token)match(input,24,FOLLOW_3); 

                        	newLeafNode(otherlv_1, grammarAccess.getAttrFullDefAccess().getIdKeyword_1_0());
                        
                    // InternalAtsDsl.g:592:1: ( (lv_uuid_2_0= RULE_STRING ) )
                    // InternalAtsDsl.g:593:1: (lv_uuid_2_0= RULE_STRING )
                    {
                    // InternalAtsDsl.g:593:1: (lv_uuid_2_0= RULE_STRING )
                    // InternalAtsDsl.g:594:3: lv_uuid_2_0= RULE_STRING
                    {
                    lv_uuid_2_0=(Token)match(input,RULE_STRING,FOLLOW_20); 

                    			newLeafNode(lv_uuid_2_0, grammarAccess.getAttrFullDefAccess().getUuidSTRINGTerminalRuleCall_1_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getAttrFullDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"uuid",
                            		lv_uuid_2_0, 
                            		"org.eclipse.xtext.common.Terminals.STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            // InternalAtsDsl.g:610:4: (otherlv_3= 'value' ( (lv_values_4_0= RULE_STRING ) ) )+
            int cnt16=0;
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( (LA16_0==23) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // InternalAtsDsl.g:610:6: otherlv_3= 'value' ( (lv_values_4_0= RULE_STRING ) )
            	    {
            	    otherlv_3=(Token)match(input,23,FOLLOW_3); 

            	        	newLeafNode(otherlv_3, grammarAccess.getAttrFullDefAccess().getValueKeyword_2_0());
            	        
            	    // InternalAtsDsl.g:614:1: ( (lv_values_4_0= RULE_STRING ) )
            	    // InternalAtsDsl.g:615:1: (lv_values_4_0= RULE_STRING )
            	    {
            	    // InternalAtsDsl.g:615:1: (lv_values_4_0= RULE_STRING )
            	    // InternalAtsDsl.g:616:3: lv_values_4_0= RULE_STRING
            	    {
            	    lv_values_4_0=(Token)match(input,RULE_STRING,FOLLOW_21); 

            	    			newLeafNode(lv_values_4_0, grammarAccess.getAttrFullDefAccess().getValuesSTRINGTerminalRuleCall_2_1_0()); 
            	    		

            	    	        if (current==null) {
            	    	            current = createModelElement(grammarAccess.getAttrFullDefRule());
            	    	        }
            	           		addWithLastConsumed(
            	           			current, 
            	           			"values",
            	            		lv_values_4_0, 
            	            		"org.eclipse.xtext.common.Terminals.STRING");
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt16 >= 1 ) break loop16;
                        EarlyExitException eee =
                            new EarlyExitException(16, input);
                        throw eee;
                }
                cnt16++;
            } while (true);

            otherlv_5=(Token)match(input,22,FOLLOW_2); 

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
    // InternalAtsDsl.g:644:1: entryRulePROGRAM_REFERENCE returns [String current=null] : iv_rulePROGRAM_REFERENCE= rulePROGRAM_REFERENCE EOF ;
    public final String entryRulePROGRAM_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_rulePROGRAM_REFERENCE = null;


        try {
            // InternalAtsDsl.g:645:2: (iv_rulePROGRAM_REFERENCE= rulePROGRAM_REFERENCE EOF )
            // InternalAtsDsl.g:646:2: iv_rulePROGRAM_REFERENCE= rulePROGRAM_REFERENCE EOF
            {
             newCompositeNode(grammarAccess.getPROGRAM_REFERENCERule()); 
            pushFollow(FOLLOW_1);
            iv_rulePROGRAM_REFERENCE=rulePROGRAM_REFERENCE();

            state._fsp--;

             current =iv_rulePROGRAM_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:653:1: rulePROGRAM_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken rulePROGRAM_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // InternalAtsDsl.g:656:28: (this_STRING_0= RULE_STRING )
            // InternalAtsDsl.g:657:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_2); 

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
    // InternalAtsDsl.g:672:1: entryRuleProgramDef returns [EObject current=null] : iv_ruleProgramDef= ruleProgramDef EOF ;
    public final EObject entryRuleProgramDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleProgramDef = null;


        try {
            // InternalAtsDsl.g:673:2: (iv_ruleProgramDef= ruleProgramDef EOF )
            // InternalAtsDsl.g:674:2: iv_ruleProgramDef= ruleProgramDef EOF
            {
             newCompositeNode(grammarAccess.getProgramDefRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleProgramDef=ruleProgramDef();

            state._fsp--;

             current =iv_ruleProgramDef; 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:681:1: ruleProgramDef returns [EObject current=null] : ( ( (lv_name_0_0= rulePROGRAM_REFERENCE ) ) ( (lv_programDefOption_1_0= ruleProgramDefOption ) )* otherlv_2= '{' (otherlv_3= 'id' ( (lv_uuid_4_0= RULE_INT ) ) )? (otherlv_5= 'artifactType' ( (lv_artifactTypeName_6_0= RULE_STRING ) ) )? (otherlv_7= 'active' ( (lv_active_8_0= ruleBooleanDef ) ) )? (otherlv_9= 'namespace' ( (lv_namespace_10_0= RULE_STRING ) ) )* (otherlv_11= 'teamDefinition' ( (lv_teamDefinition_12_0= ruleTEAM_DEF_REFERENCE ) ) )* (otherlv_13= 'attribute' ( (lv_attributes_14_0= ruleAttrDef ) ) )* otherlv_15= '}' ) ;
    public final EObject ruleProgramDef() throws RecognitionException {
        EObject current = null;

        Token otherlv_2=null;
        Token otherlv_3=null;
        Token lv_uuid_4_0=null;
        Token otherlv_5=null;
        Token lv_artifactTypeName_6_0=null;
        Token otherlv_7=null;
        Token otherlv_9=null;
        Token lv_namespace_10_0=null;
        Token otherlv_11=null;
        Token otherlv_13=null;
        Token otherlv_15=null;
        AntlrDatatypeRuleToken lv_name_0_0 = null;

        AntlrDatatypeRuleToken lv_programDefOption_1_0 = null;

        Enumerator lv_active_8_0 = null;

        AntlrDatatypeRuleToken lv_teamDefinition_12_0 = null;

        EObject lv_attributes_14_0 = null;


         enterRule(); 
            
        try {
            // InternalAtsDsl.g:684:28: ( ( ( (lv_name_0_0= rulePROGRAM_REFERENCE ) ) ( (lv_programDefOption_1_0= ruleProgramDefOption ) )* otherlv_2= '{' (otherlv_3= 'id' ( (lv_uuid_4_0= RULE_INT ) ) )? (otherlv_5= 'artifactType' ( (lv_artifactTypeName_6_0= RULE_STRING ) ) )? (otherlv_7= 'active' ( (lv_active_8_0= ruleBooleanDef ) ) )? (otherlv_9= 'namespace' ( (lv_namespace_10_0= RULE_STRING ) ) )* (otherlv_11= 'teamDefinition' ( (lv_teamDefinition_12_0= ruleTEAM_DEF_REFERENCE ) ) )* (otherlv_13= 'attribute' ( (lv_attributes_14_0= ruleAttrDef ) ) )* otherlv_15= '}' ) )
            // InternalAtsDsl.g:685:1: ( ( (lv_name_0_0= rulePROGRAM_REFERENCE ) ) ( (lv_programDefOption_1_0= ruleProgramDefOption ) )* otherlv_2= '{' (otherlv_3= 'id' ( (lv_uuid_4_0= RULE_INT ) ) )? (otherlv_5= 'artifactType' ( (lv_artifactTypeName_6_0= RULE_STRING ) ) )? (otherlv_7= 'active' ( (lv_active_8_0= ruleBooleanDef ) ) )? (otherlv_9= 'namespace' ( (lv_namespace_10_0= RULE_STRING ) ) )* (otherlv_11= 'teamDefinition' ( (lv_teamDefinition_12_0= ruleTEAM_DEF_REFERENCE ) ) )* (otherlv_13= 'attribute' ( (lv_attributes_14_0= ruleAttrDef ) ) )* otherlv_15= '}' )
            {
            // InternalAtsDsl.g:685:1: ( ( (lv_name_0_0= rulePROGRAM_REFERENCE ) ) ( (lv_programDefOption_1_0= ruleProgramDefOption ) )* otherlv_2= '{' (otherlv_3= 'id' ( (lv_uuid_4_0= RULE_INT ) ) )? (otherlv_5= 'artifactType' ( (lv_artifactTypeName_6_0= RULE_STRING ) ) )? (otherlv_7= 'active' ( (lv_active_8_0= ruleBooleanDef ) ) )? (otherlv_9= 'namespace' ( (lv_namespace_10_0= RULE_STRING ) ) )* (otherlv_11= 'teamDefinition' ( (lv_teamDefinition_12_0= ruleTEAM_DEF_REFERENCE ) ) )* (otherlv_13= 'attribute' ( (lv_attributes_14_0= ruleAttrDef ) ) )* otherlv_15= '}' )
            // InternalAtsDsl.g:685:2: ( (lv_name_0_0= rulePROGRAM_REFERENCE ) ) ( (lv_programDefOption_1_0= ruleProgramDefOption ) )* otherlv_2= '{' (otherlv_3= 'id' ( (lv_uuid_4_0= RULE_INT ) ) )? (otherlv_5= 'artifactType' ( (lv_artifactTypeName_6_0= RULE_STRING ) ) )? (otherlv_7= 'active' ( (lv_active_8_0= ruleBooleanDef ) ) )? (otherlv_9= 'namespace' ( (lv_namespace_10_0= RULE_STRING ) ) )* (otherlv_11= 'teamDefinition' ( (lv_teamDefinition_12_0= ruleTEAM_DEF_REFERENCE ) ) )* (otherlv_13= 'attribute' ( (lv_attributes_14_0= ruleAttrDef ) ) )* otherlv_15= '}'
            {
            // InternalAtsDsl.g:685:2: ( (lv_name_0_0= rulePROGRAM_REFERENCE ) )
            // InternalAtsDsl.g:686:1: (lv_name_0_0= rulePROGRAM_REFERENCE )
            {
            // InternalAtsDsl.g:686:1: (lv_name_0_0= rulePROGRAM_REFERENCE )
            // InternalAtsDsl.g:687:3: lv_name_0_0= rulePROGRAM_REFERENCE
            {
             
            	        newCompositeNode(grammarAccess.getProgramDefAccess().getNamePROGRAM_REFERENCEParserRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_22);
            lv_name_0_0=rulePROGRAM_REFERENCE();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getProgramDefRule());
            	        }
                   		set(
                   			current, 
                   			"name",
                    		lv_name_0_0, 
                    		"org.eclipse.osee.ats.dsl.AtsDsl.PROGRAM_REFERENCE");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // InternalAtsDsl.g:703:2: ( (lv_programDefOption_1_0= ruleProgramDefOption ) )*
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);

                if ( (LA17_0==RULE_STRING||LA17_0==83) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // InternalAtsDsl.g:704:1: (lv_programDefOption_1_0= ruleProgramDefOption )
            	    {
            	    // InternalAtsDsl.g:704:1: (lv_programDefOption_1_0= ruleProgramDefOption )
            	    // InternalAtsDsl.g:705:3: lv_programDefOption_1_0= ruleProgramDefOption
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getProgramDefAccess().getProgramDefOptionProgramDefOptionParserRuleCall_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_22);
            	    lv_programDefOption_1_0=ruleProgramDefOption();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getProgramDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"programDefOption",
            	            		lv_programDefOption_1_0, 
            	            		"org.eclipse.osee.ats.dsl.AtsDsl.ProgramDefOption");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    break loop17;
                }
            } while (true);

            otherlv_2=(Token)match(input,17,FOLLOW_23); 

                	newLeafNode(otherlv_2, grammarAccess.getProgramDefAccess().getLeftCurlyBracketKeyword_2());
                
            // InternalAtsDsl.g:725:1: (otherlv_3= 'id' ( (lv_uuid_4_0= RULE_INT ) ) )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==24) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // InternalAtsDsl.g:725:3: otherlv_3= 'id' ( (lv_uuid_4_0= RULE_INT ) )
                    {
                    otherlv_3=(Token)match(input,24,FOLLOW_24); 

                        	newLeafNode(otherlv_3, grammarAccess.getProgramDefAccess().getIdKeyword_3_0());
                        
                    // InternalAtsDsl.g:729:1: ( (lv_uuid_4_0= RULE_INT ) )
                    // InternalAtsDsl.g:730:1: (lv_uuid_4_0= RULE_INT )
                    {
                    // InternalAtsDsl.g:730:1: (lv_uuid_4_0= RULE_INT )
                    // InternalAtsDsl.g:731:3: lv_uuid_4_0= RULE_INT
                    {
                    lv_uuid_4_0=(Token)match(input,RULE_INT,FOLLOW_25); 

                    			newLeafNode(lv_uuid_4_0, grammarAccess.getProgramDefAccess().getUuidINTTerminalRuleCall_3_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getProgramDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"uuid",
                            		lv_uuid_4_0, 
                            		"org.eclipse.xtext.common.Terminals.INT");
                    	    

                    }


                    }


                    }
                    break;

            }

            // InternalAtsDsl.g:747:4: (otherlv_5= 'artifactType' ( (lv_artifactTypeName_6_0= RULE_STRING ) ) )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==25) ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // InternalAtsDsl.g:747:6: otherlv_5= 'artifactType' ( (lv_artifactTypeName_6_0= RULE_STRING ) )
                    {
                    otherlv_5=(Token)match(input,25,FOLLOW_3); 

                        	newLeafNode(otherlv_5, grammarAccess.getProgramDefAccess().getArtifactTypeKeyword_4_0());
                        
                    // InternalAtsDsl.g:751:1: ( (lv_artifactTypeName_6_0= RULE_STRING ) )
                    // InternalAtsDsl.g:752:1: (lv_artifactTypeName_6_0= RULE_STRING )
                    {
                    // InternalAtsDsl.g:752:1: (lv_artifactTypeName_6_0= RULE_STRING )
                    // InternalAtsDsl.g:753:3: lv_artifactTypeName_6_0= RULE_STRING
                    {
                    lv_artifactTypeName_6_0=(Token)match(input,RULE_STRING,FOLLOW_26); 

                    			newLeafNode(lv_artifactTypeName_6_0, grammarAccess.getProgramDefAccess().getArtifactTypeNameSTRINGTerminalRuleCall_4_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getProgramDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"artifactTypeName",
                            		lv_artifactTypeName_6_0, 
                            		"org.eclipse.xtext.common.Terminals.STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            // InternalAtsDsl.g:769:4: (otherlv_7= 'active' ( (lv_active_8_0= ruleBooleanDef ) ) )?
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==18) ) {
                alt20=1;
            }
            switch (alt20) {
                case 1 :
                    // InternalAtsDsl.g:769:6: otherlv_7= 'active' ( (lv_active_8_0= ruleBooleanDef ) )
                    {
                    otherlv_7=(Token)match(input,18,FOLLOW_13); 

                        	newLeafNode(otherlv_7, grammarAccess.getProgramDefAccess().getActiveKeyword_5_0());
                        
                    // InternalAtsDsl.g:773:1: ( (lv_active_8_0= ruleBooleanDef ) )
                    // InternalAtsDsl.g:774:1: (lv_active_8_0= ruleBooleanDef )
                    {
                    // InternalAtsDsl.g:774:1: (lv_active_8_0= ruleBooleanDef )
                    // InternalAtsDsl.g:775:3: lv_active_8_0= ruleBooleanDef
                    {
                     
                    	        newCompositeNode(grammarAccess.getProgramDefAccess().getActiveBooleanDefEnumRuleCall_5_1_0()); 
                    	    
                    pushFollow(FOLLOW_27);
                    lv_active_8_0=ruleBooleanDef();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getProgramDefRule());
                    	        }
                           		set(
                           			current, 
                           			"active",
                            		lv_active_8_0, 
                            		"org.eclipse.osee.ats.dsl.AtsDsl.BooleanDef");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }


                    }
                    break;

            }

            // InternalAtsDsl.g:791:4: (otherlv_9= 'namespace' ( (lv_namespace_10_0= RULE_STRING ) ) )*
            loop21:
            do {
                int alt21=2;
                int LA21_0 = input.LA(1);

                if ( (LA21_0==26) ) {
                    alt21=1;
                }


                switch (alt21) {
            	case 1 :
            	    // InternalAtsDsl.g:791:6: otherlv_9= 'namespace' ( (lv_namespace_10_0= RULE_STRING ) )
            	    {
            	    otherlv_9=(Token)match(input,26,FOLLOW_3); 

            	        	newLeafNode(otherlv_9, grammarAccess.getProgramDefAccess().getNamespaceKeyword_6_0());
            	        
            	    // InternalAtsDsl.g:795:1: ( (lv_namespace_10_0= RULE_STRING ) )
            	    // InternalAtsDsl.g:796:1: (lv_namespace_10_0= RULE_STRING )
            	    {
            	    // InternalAtsDsl.g:796:1: (lv_namespace_10_0= RULE_STRING )
            	    // InternalAtsDsl.g:797:3: lv_namespace_10_0= RULE_STRING
            	    {
            	    lv_namespace_10_0=(Token)match(input,RULE_STRING,FOLLOW_27); 

            	    			newLeafNode(lv_namespace_10_0, grammarAccess.getProgramDefAccess().getNamespaceSTRINGTerminalRuleCall_6_1_0()); 
            	    		

            	    	        if (current==null) {
            	    	            current = createModelElement(grammarAccess.getProgramDefRule());
            	    	        }
            	           		setWithLastConsumed(
            	           			current, 
            	           			"namespace",
            	            		lv_namespace_10_0, 
            	            		"org.eclipse.xtext.common.Terminals.STRING");
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop21;
                }
            } while (true);

            // InternalAtsDsl.g:813:4: (otherlv_11= 'teamDefinition' ( (lv_teamDefinition_12_0= ruleTEAM_DEF_REFERENCE ) ) )*
            loop22:
            do {
                int alt22=2;
                int LA22_0 = input.LA(1);

                if ( (LA22_0==12) ) {
                    alt22=1;
                }


                switch (alt22) {
            	case 1 :
            	    // InternalAtsDsl.g:813:6: otherlv_11= 'teamDefinition' ( (lv_teamDefinition_12_0= ruleTEAM_DEF_REFERENCE ) )
            	    {
            	    otherlv_11=(Token)match(input,12,FOLLOW_3); 

            	        	newLeafNode(otherlv_11, grammarAccess.getProgramDefAccess().getTeamDefinitionKeyword_7_0());
            	        
            	    // InternalAtsDsl.g:817:1: ( (lv_teamDefinition_12_0= ruleTEAM_DEF_REFERENCE ) )
            	    // InternalAtsDsl.g:818:1: (lv_teamDefinition_12_0= ruleTEAM_DEF_REFERENCE )
            	    {
            	    // InternalAtsDsl.g:818:1: (lv_teamDefinition_12_0= ruleTEAM_DEF_REFERENCE )
            	    // InternalAtsDsl.g:819:3: lv_teamDefinition_12_0= ruleTEAM_DEF_REFERENCE
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getProgramDefAccess().getTeamDefinitionTEAM_DEF_REFERENCEParserRuleCall_7_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_28);
            	    lv_teamDefinition_12_0=ruleTEAM_DEF_REFERENCE();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getProgramDefRule());
            	    	        }
            	           		set(
            	           			current, 
            	           			"teamDefinition",
            	            		lv_teamDefinition_12_0, 
            	            		"org.eclipse.osee.ats.dsl.AtsDsl.TEAM_DEF_REFERENCE");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop22;
                }
            } while (true);

            // InternalAtsDsl.g:835:4: (otherlv_13= 'attribute' ( (lv_attributes_14_0= ruleAttrDef ) ) )*
            loop23:
            do {
                int alt23=2;
                int LA23_0 = input.LA(1);

                if ( (LA23_0==27) ) {
                    alt23=1;
                }


                switch (alt23) {
            	case 1 :
            	    // InternalAtsDsl.g:835:6: otherlv_13= 'attribute' ( (lv_attributes_14_0= ruleAttrDef ) )
            	    {
            	    otherlv_13=(Token)match(input,27,FOLLOW_3); 

            	        	newLeafNode(otherlv_13, grammarAccess.getProgramDefAccess().getAttributeKeyword_8_0());
            	        
            	    // InternalAtsDsl.g:839:1: ( (lv_attributes_14_0= ruleAttrDef ) )
            	    // InternalAtsDsl.g:840:1: (lv_attributes_14_0= ruleAttrDef )
            	    {
            	    // InternalAtsDsl.g:840:1: (lv_attributes_14_0= ruleAttrDef )
            	    // InternalAtsDsl.g:841:3: lv_attributes_14_0= ruleAttrDef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getProgramDefAccess().getAttributesAttrDefParserRuleCall_8_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_29);
            	    lv_attributes_14_0=ruleAttrDef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getProgramDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"attributes",
            	            		lv_attributes_14_0, 
            	            		"org.eclipse.osee.ats.dsl.AtsDsl.AttrDef");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop23;
                }
            } while (true);

            otherlv_15=(Token)match(input,22,FOLLOW_2); 

                	newLeafNode(otherlv_15, grammarAccess.getProgramDefAccess().getRightCurlyBracketKeyword_9());
                

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
    // InternalAtsDsl.g:869:1: entryRuleTEAM_DEF_REFERENCE returns [String current=null] : iv_ruleTEAM_DEF_REFERENCE= ruleTEAM_DEF_REFERENCE EOF ;
    public final String entryRuleTEAM_DEF_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleTEAM_DEF_REFERENCE = null;


        try {
            // InternalAtsDsl.g:870:2: (iv_ruleTEAM_DEF_REFERENCE= ruleTEAM_DEF_REFERENCE EOF )
            // InternalAtsDsl.g:871:2: iv_ruleTEAM_DEF_REFERENCE= ruleTEAM_DEF_REFERENCE EOF
            {
             newCompositeNode(grammarAccess.getTEAM_DEF_REFERENCERule()); 
            pushFollow(FOLLOW_1);
            iv_ruleTEAM_DEF_REFERENCE=ruleTEAM_DEF_REFERENCE();

            state._fsp--;

             current =iv_ruleTEAM_DEF_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:878:1: ruleTEAM_DEF_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleTEAM_DEF_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // InternalAtsDsl.g:881:28: (this_STRING_0= RULE_STRING )
            // InternalAtsDsl.g:882:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_2); 

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
    // InternalAtsDsl.g:897:1: entryRuleTeamDef returns [EObject current=null] : iv_ruleTeamDef= ruleTeamDef EOF ;
    public final EObject entryRuleTeamDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleTeamDef = null;


        try {
            // InternalAtsDsl.g:898:2: (iv_ruleTeamDef= ruleTeamDef EOF )
            // InternalAtsDsl.g:899:2: iv_ruleTeamDef= ruleTeamDef EOF
            {
             newCompositeNode(grammarAccess.getTeamDefRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleTeamDef=ruleTeamDef();

            state._fsp--;

             current =iv_ruleTeamDef; 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:906:1: ruleTeamDef returns [EObject current=null] : ( ( (lv_name_0_0= ruleTEAM_DEF_REFERENCE ) ) ( (lv_teamDefOption_1_0= ruleTeamDefOption ) )* otherlv_2= '{' (otherlv_3= 'id' ( (lv_uuid_4_0= RULE_INT ) ) )? (otherlv_5= 'active' ( (lv_active_6_0= ruleBooleanDef ) ) )? (otherlv_7= 'staticId' ( (lv_staticId_8_0= RULE_STRING ) ) )* (otherlv_9= 'lead' ( (lv_lead_10_0= ruleUserRef ) ) )* (otherlv_11= 'member' ( (lv_member_12_0= ruleUserRef ) ) )* (otherlv_13= 'privileged' ( (lv_privileged_14_0= ruleUserRef ) ) )* (otherlv_15= 'workDefinition' ( (lv_workDefinition_16_0= RULE_STRING ) ) )? (otherlv_17= 'relatedTaskWorkDefinition' ( (lv_relatedTaskWorkDefinition_18_0= RULE_STRING ) ) )? (otherlv_19= 'teamWorkflowArtifactType' ( (lv_teamWorkflowArtifactType_20_0= RULE_STRING ) ) )? (otherlv_21= 'accessContextId' ( (lv_accessContextId_22_0= RULE_STRING ) ) )* (otherlv_23= 'version' ( (lv_version_24_0= ruleVersionDef ) ) )* (otherlv_25= 'rule' ( (lv_rules_26_0= ruleRuleRef ) ) )* (otherlv_27= 'children' otherlv_28= '{' (otherlv_29= 'teamDefinition' ( (lv_children_30_0= ruleTeamDef ) ) )+ otherlv_31= '}' )? otherlv_32= '}' ) ;
    public final EObject ruleTeamDef() throws RecognitionException {
        EObject current = null;

        Token otherlv_2=null;
        Token otherlv_3=null;
        Token lv_uuid_4_0=null;
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
        Token lv_teamWorkflowArtifactType_20_0=null;
        Token otherlv_21=null;
        Token lv_accessContextId_22_0=null;
        Token otherlv_23=null;
        Token otherlv_25=null;
        Token otherlv_27=null;
        Token otherlv_28=null;
        Token otherlv_29=null;
        Token otherlv_31=null;
        Token otherlv_32=null;
        AntlrDatatypeRuleToken lv_name_0_0 = null;

        AntlrDatatypeRuleToken lv_teamDefOption_1_0 = null;

        Enumerator lv_active_6_0 = null;

        EObject lv_lead_10_0 = null;

        EObject lv_member_12_0 = null;

        EObject lv_privileged_14_0 = null;

        EObject lv_version_24_0 = null;

        AntlrDatatypeRuleToken lv_rules_26_0 = null;

        EObject lv_children_30_0 = null;


         enterRule(); 
            
        try {
            // InternalAtsDsl.g:909:28: ( ( ( (lv_name_0_0= ruleTEAM_DEF_REFERENCE ) ) ( (lv_teamDefOption_1_0= ruleTeamDefOption ) )* otherlv_2= '{' (otherlv_3= 'id' ( (lv_uuid_4_0= RULE_INT ) ) )? (otherlv_5= 'active' ( (lv_active_6_0= ruleBooleanDef ) ) )? (otherlv_7= 'staticId' ( (lv_staticId_8_0= RULE_STRING ) ) )* (otherlv_9= 'lead' ( (lv_lead_10_0= ruleUserRef ) ) )* (otherlv_11= 'member' ( (lv_member_12_0= ruleUserRef ) ) )* (otherlv_13= 'privileged' ( (lv_privileged_14_0= ruleUserRef ) ) )* (otherlv_15= 'workDefinition' ( (lv_workDefinition_16_0= RULE_STRING ) ) )? (otherlv_17= 'relatedTaskWorkDefinition' ( (lv_relatedTaskWorkDefinition_18_0= RULE_STRING ) ) )? (otherlv_19= 'teamWorkflowArtifactType' ( (lv_teamWorkflowArtifactType_20_0= RULE_STRING ) ) )? (otherlv_21= 'accessContextId' ( (lv_accessContextId_22_0= RULE_STRING ) ) )* (otherlv_23= 'version' ( (lv_version_24_0= ruleVersionDef ) ) )* (otherlv_25= 'rule' ( (lv_rules_26_0= ruleRuleRef ) ) )* (otherlv_27= 'children' otherlv_28= '{' (otherlv_29= 'teamDefinition' ( (lv_children_30_0= ruleTeamDef ) ) )+ otherlv_31= '}' )? otherlv_32= '}' ) )
            // InternalAtsDsl.g:910:1: ( ( (lv_name_0_0= ruleTEAM_DEF_REFERENCE ) ) ( (lv_teamDefOption_1_0= ruleTeamDefOption ) )* otherlv_2= '{' (otherlv_3= 'id' ( (lv_uuid_4_0= RULE_INT ) ) )? (otherlv_5= 'active' ( (lv_active_6_0= ruleBooleanDef ) ) )? (otherlv_7= 'staticId' ( (lv_staticId_8_0= RULE_STRING ) ) )* (otherlv_9= 'lead' ( (lv_lead_10_0= ruleUserRef ) ) )* (otherlv_11= 'member' ( (lv_member_12_0= ruleUserRef ) ) )* (otherlv_13= 'privileged' ( (lv_privileged_14_0= ruleUserRef ) ) )* (otherlv_15= 'workDefinition' ( (lv_workDefinition_16_0= RULE_STRING ) ) )? (otherlv_17= 'relatedTaskWorkDefinition' ( (lv_relatedTaskWorkDefinition_18_0= RULE_STRING ) ) )? (otherlv_19= 'teamWorkflowArtifactType' ( (lv_teamWorkflowArtifactType_20_0= RULE_STRING ) ) )? (otherlv_21= 'accessContextId' ( (lv_accessContextId_22_0= RULE_STRING ) ) )* (otherlv_23= 'version' ( (lv_version_24_0= ruleVersionDef ) ) )* (otherlv_25= 'rule' ( (lv_rules_26_0= ruleRuleRef ) ) )* (otherlv_27= 'children' otherlv_28= '{' (otherlv_29= 'teamDefinition' ( (lv_children_30_0= ruleTeamDef ) ) )+ otherlv_31= '}' )? otherlv_32= '}' )
            {
            // InternalAtsDsl.g:910:1: ( ( (lv_name_0_0= ruleTEAM_DEF_REFERENCE ) ) ( (lv_teamDefOption_1_0= ruleTeamDefOption ) )* otherlv_2= '{' (otherlv_3= 'id' ( (lv_uuid_4_0= RULE_INT ) ) )? (otherlv_5= 'active' ( (lv_active_6_0= ruleBooleanDef ) ) )? (otherlv_7= 'staticId' ( (lv_staticId_8_0= RULE_STRING ) ) )* (otherlv_9= 'lead' ( (lv_lead_10_0= ruleUserRef ) ) )* (otherlv_11= 'member' ( (lv_member_12_0= ruleUserRef ) ) )* (otherlv_13= 'privileged' ( (lv_privileged_14_0= ruleUserRef ) ) )* (otherlv_15= 'workDefinition' ( (lv_workDefinition_16_0= RULE_STRING ) ) )? (otherlv_17= 'relatedTaskWorkDefinition' ( (lv_relatedTaskWorkDefinition_18_0= RULE_STRING ) ) )? (otherlv_19= 'teamWorkflowArtifactType' ( (lv_teamWorkflowArtifactType_20_0= RULE_STRING ) ) )? (otherlv_21= 'accessContextId' ( (lv_accessContextId_22_0= RULE_STRING ) ) )* (otherlv_23= 'version' ( (lv_version_24_0= ruleVersionDef ) ) )* (otherlv_25= 'rule' ( (lv_rules_26_0= ruleRuleRef ) ) )* (otherlv_27= 'children' otherlv_28= '{' (otherlv_29= 'teamDefinition' ( (lv_children_30_0= ruleTeamDef ) ) )+ otherlv_31= '}' )? otherlv_32= '}' )
            // InternalAtsDsl.g:910:2: ( (lv_name_0_0= ruleTEAM_DEF_REFERENCE ) ) ( (lv_teamDefOption_1_0= ruleTeamDefOption ) )* otherlv_2= '{' (otherlv_3= 'id' ( (lv_uuid_4_0= RULE_INT ) ) )? (otherlv_5= 'active' ( (lv_active_6_0= ruleBooleanDef ) ) )? (otherlv_7= 'staticId' ( (lv_staticId_8_0= RULE_STRING ) ) )* (otherlv_9= 'lead' ( (lv_lead_10_0= ruleUserRef ) ) )* (otherlv_11= 'member' ( (lv_member_12_0= ruleUserRef ) ) )* (otherlv_13= 'privileged' ( (lv_privileged_14_0= ruleUserRef ) ) )* (otherlv_15= 'workDefinition' ( (lv_workDefinition_16_0= RULE_STRING ) ) )? (otherlv_17= 'relatedTaskWorkDefinition' ( (lv_relatedTaskWorkDefinition_18_0= RULE_STRING ) ) )? (otherlv_19= 'teamWorkflowArtifactType' ( (lv_teamWorkflowArtifactType_20_0= RULE_STRING ) ) )? (otherlv_21= 'accessContextId' ( (lv_accessContextId_22_0= RULE_STRING ) ) )* (otherlv_23= 'version' ( (lv_version_24_0= ruleVersionDef ) ) )* (otherlv_25= 'rule' ( (lv_rules_26_0= ruleRuleRef ) ) )* (otherlv_27= 'children' otherlv_28= '{' (otherlv_29= 'teamDefinition' ( (lv_children_30_0= ruleTeamDef ) ) )+ otherlv_31= '}' )? otherlv_32= '}'
            {
            // InternalAtsDsl.g:910:2: ( (lv_name_0_0= ruleTEAM_DEF_REFERENCE ) )
            // InternalAtsDsl.g:911:1: (lv_name_0_0= ruleTEAM_DEF_REFERENCE )
            {
            // InternalAtsDsl.g:911:1: (lv_name_0_0= ruleTEAM_DEF_REFERENCE )
            // InternalAtsDsl.g:912:3: lv_name_0_0= ruleTEAM_DEF_REFERENCE
            {
             
            	        newCompositeNode(grammarAccess.getTeamDefAccess().getNameTEAM_DEF_REFERENCEParserRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_22);
            lv_name_0_0=ruleTEAM_DEF_REFERENCE();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getTeamDefRule());
            	        }
                   		set(
                   			current, 
                   			"name",
                    		lv_name_0_0, 
                    		"org.eclipse.osee.ats.dsl.AtsDsl.TEAM_DEF_REFERENCE");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // InternalAtsDsl.g:928:2: ( (lv_teamDefOption_1_0= ruleTeamDefOption ) )*
            loop24:
            do {
                int alt24=2;
                int LA24_0 = input.LA(1);

                if ( (LA24_0==RULE_STRING||LA24_0==83) ) {
                    alt24=1;
                }


                switch (alt24) {
            	case 1 :
            	    // InternalAtsDsl.g:929:1: (lv_teamDefOption_1_0= ruleTeamDefOption )
            	    {
            	    // InternalAtsDsl.g:929:1: (lv_teamDefOption_1_0= ruleTeamDefOption )
            	    // InternalAtsDsl.g:930:3: lv_teamDefOption_1_0= ruleTeamDefOption
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getTeamDefAccess().getTeamDefOptionTeamDefOptionParserRuleCall_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_22);
            	    lv_teamDefOption_1_0=ruleTeamDefOption();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getTeamDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"teamDefOption",
            	            		lv_teamDefOption_1_0, 
            	            		"org.eclipse.osee.ats.dsl.AtsDsl.TeamDefOption");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    break loop24;
                }
            } while (true);

            otherlv_2=(Token)match(input,17,FOLLOW_30); 

                	newLeafNode(otherlv_2, grammarAccess.getTeamDefAccess().getLeftCurlyBracketKeyword_2());
                
            // InternalAtsDsl.g:950:1: (otherlv_3= 'id' ( (lv_uuid_4_0= RULE_INT ) ) )?
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==24) ) {
                alt25=1;
            }
            switch (alt25) {
                case 1 :
                    // InternalAtsDsl.g:950:3: otherlv_3= 'id' ( (lv_uuid_4_0= RULE_INT ) )
                    {
                    otherlv_3=(Token)match(input,24,FOLLOW_24); 

                        	newLeafNode(otherlv_3, grammarAccess.getTeamDefAccess().getIdKeyword_3_0());
                        
                    // InternalAtsDsl.g:954:1: ( (lv_uuid_4_0= RULE_INT ) )
                    // InternalAtsDsl.g:955:1: (lv_uuid_4_0= RULE_INT )
                    {
                    // InternalAtsDsl.g:955:1: (lv_uuid_4_0= RULE_INT )
                    // InternalAtsDsl.g:956:3: lv_uuid_4_0= RULE_INT
                    {
                    lv_uuid_4_0=(Token)match(input,RULE_INT,FOLLOW_31); 

                    			newLeafNode(lv_uuid_4_0, grammarAccess.getTeamDefAccess().getUuidINTTerminalRuleCall_3_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getTeamDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"uuid",
                            		lv_uuid_4_0, 
                            		"org.eclipse.xtext.common.Terminals.INT");
                    	    

                    }


                    }


                    }
                    break;

            }

            // InternalAtsDsl.g:972:4: (otherlv_5= 'active' ( (lv_active_6_0= ruleBooleanDef ) ) )?
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==18) ) {
                alt26=1;
            }
            switch (alt26) {
                case 1 :
                    // InternalAtsDsl.g:972:6: otherlv_5= 'active' ( (lv_active_6_0= ruleBooleanDef ) )
                    {
                    otherlv_5=(Token)match(input,18,FOLLOW_13); 

                        	newLeafNode(otherlv_5, grammarAccess.getTeamDefAccess().getActiveKeyword_4_0());
                        
                    // InternalAtsDsl.g:976:1: ( (lv_active_6_0= ruleBooleanDef ) )
                    // InternalAtsDsl.g:977:1: (lv_active_6_0= ruleBooleanDef )
                    {
                    // InternalAtsDsl.g:977:1: (lv_active_6_0= ruleBooleanDef )
                    // InternalAtsDsl.g:978:3: lv_active_6_0= ruleBooleanDef
                    {
                     
                    	        newCompositeNode(grammarAccess.getTeamDefAccess().getActiveBooleanDefEnumRuleCall_4_1_0()); 
                    	    
                    pushFollow(FOLLOW_32);
                    lv_active_6_0=ruleBooleanDef();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getTeamDefRule());
                    	        }
                           		set(
                           			current, 
                           			"active",
                            		lv_active_6_0, 
                            		"org.eclipse.osee.ats.dsl.AtsDsl.BooleanDef");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }


                    }
                    break;

            }

            // InternalAtsDsl.g:994:4: (otherlv_7= 'staticId' ( (lv_staticId_8_0= RULE_STRING ) ) )*
            loop27:
            do {
                int alt27=2;
                int LA27_0 = input.LA(1);

                if ( (LA27_0==28) ) {
                    alt27=1;
                }


                switch (alt27) {
            	case 1 :
            	    // InternalAtsDsl.g:994:6: otherlv_7= 'staticId' ( (lv_staticId_8_0= RULE_STRING ) )
            	    {
            	    otherlv_7=(Token)match(input,28,FOLLOW_3); 

            	        	newLeafNode(otherlv_7, grammarAccess.getTeamDefAccess().getStaticIdKeyword_5_0());
            	        
            	    // InternalAtsDsl.g:998:1: ( (lv_staticId_8_0= RULE_STRING ) )
            	    // InternalAtsDsl.g:999:1: (lv_staticId_8_0= RULE_STRING )
            	    {
            	    // InternalAtsDsl.g:999:1: (lv_staticId_8_0= RULE_STRING )
            	    // InternalAtsDsl.g:1000:3: lv_staticId_8_0= RULE_STRING
            	    {
            	    lv_staticId_8_0=(Token)match(input,RULE_STRING,FOLLOW_32); 

            	    			newLeafNode(lv_staticId_8_0, grammarAccess.getTeamDefAccess().getStaticIdSTRINGTerminalRuleCall_5_1_0()); 
            	    		

            	    	        if (current==null) {
            	    	            current = createModelElement(grammarAccess.getTeamDefRule());
            	    	        }
            	           		addWithLastConsumed(
            	           			current, 
            	           			"staticId",
            	            		lv_staticId_8_0, 
            	            		"org.eclipse.xtext.common.Terminals.STRING");
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop27;
                }
            } while (true);

            // InternalAtsDsl.g:1016:4: (otherlv_9= 'lead' ( (lv_lead_10_0= ruleUserRef ) ) )*
            loop28:
            do {
                int alt28=2;
                int LA28_0 = input.LA(1);

                if ( (LA28_0==29) ) {
                    alt28=1;
                }


                switch (alt28) {
            	case 1 :
            	    // InternalAtsDsl.g:1016:6: otherlv_9= 'lead' ( (lv_lead_10_0= ruleUserRef ) )
            	    {
            	    otherlv_9=(Token)match(input,29,FOLLOW_33); 

            	        	newLeafNode(otherlv_9, grammarAccess.getTeamDefAccess().getLeadKeyword_6_0());
            	        
            	    // InternalAtsDsl.g:1020:1: ( (lv_lead_10_0= ruleUserRef ) )
            	    // InternalAtsDsl.g:1021:1: (lv_lead_10_0= ruleUserRef )
            	    {
            	    // InternalAtsDsl.g:1021:1: (lv_lead_10_0= ruleUserRef )
            	    // InternalAtsDsl.g:1022:3: lv_lead_10_0= ruleUserRef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getTeamDefAccess().getLeadUserRefParserRuleCall_6_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_34);
            	    lv_lead_10_0=ruleUserRef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getTeamDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"lead",
            	            		lv_lead_10_0, 
            	            		"org.eclipse.osee.ats.dsl.AtsDsl.UserRef");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop28;
                }
            } while (true);

            // InternalAtsDsl.g:1038:4: (otherlv_11= 'member' ( (lv_member_12_0= ruleUserRef ) ) )*
            loop29:
            do {
                int alt29=2;
                int LA29_0 = input.LA(1);

                if ( (LA29_0==30) ) {
                    alt29=1;
                }


                switch (alt29) {
            	case 1 :
            	    // InternalAtsDsl.g:1038:6: otherlv_11= 'member' ( (lv_member_12_0= ruleUserRef ) )
            	    {
            	    otherlv_11=(Token)match(input,30,FOLLOW_33); 

            	        	newLeafNode(otherlv_11, grammarAccess.getTeamDefAccess().getMemberKeyword_7_0());
            	        
            	    // InternalAtsDsl.g:1042:1: ( (lv_member_12_0= ruleUserRef ) )
            	    // InternalAtsDsl.g:1043:1: (lv_member_12_0= ruleUserRef )
            	    {
            	    // InternalAtsDsl.g:1043:1: (lv_member_12_0= ruleUserRef )
            	    // InternalAtsDsl.g:1044:3: lv_member_12_0= ruleUserRef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getTeamDefAccess().getMemberUserRefParserRuleCall_7_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_35);
            	    lv_member_12_0=ruleUserRef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getTeamDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"member",
            	            		lv_member_12_0, 
            	            		"org.eclipse.osee.ats.dsl.AtsDsl.UserRef");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop29;
                }
            } while (true);

            // InternalAtsDsl.g:1060:4: (otherlv_13= 'privileged' ( (lv_privileged_14_0= ruleUserRef ) ) )*
            loop30:
            do {
                int alt30=2;
                int LA30_0 = input.LA(1);

                if ( (LA30_0==31) ) {
                    alt30=1;
                }


                switch (alt30) {
            	case 1 :
            	    // InternalAtsDsl.g:1060:6: otherlv_13= 'privileged' ( (lv_privileged_14_0= ruleUserRef ) )
            	    {
            	    otherlv_13=(Token)match(input,31,FOLLOW_33); 

            	        	newLeafNode(otherlv_13, grammarAccess.getTeamDefAccess().getPrivilegedKeyword_8_0());
            	        
            	    // InternalAtsDsl.g:1064:1: ( (lv_privileged_14_0= ruleUserRef ) )
            	    // InternalAtsDsl.g:1065:1: (lv_privileged_14_0= ruleUserRef )
            	    {
            	    // InternalAtsDsl.g:1065:1: (lv_privileged_14_0= ruleUserRef )
            	    // InternalAtsDsl.g:1066:3: lv_privileged_14_0= ruleUserRef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getTeamDefAccess().getPrivilegedUserRefParserRuleCall_8_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_36);
            	    lv_privileged_14_0=ruleUserRef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getTeamDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"privileged",
            	            		lv_privileged_14_0, 
            	            		"org.eclipse.osee.ats.dsl.AtsDsl.UserRef");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop30;
                }
            } while (true);

            // InternalAtsDsl.g:1082:4: (otherlv_15= 'workDefinition' ( (lv_workDefinition_16_0= RULE_STRING ) ) )?
            int alt31=2;
            int LA31_0 = input.LA(1);

            if ( (LA31_0==14) ) {
                alt31=1;
            }
            switch (alt31) {
                case 1 :
                    // InternalAtsDsl.g:1082:6: otherlv_15= 'workDefinition' ( (lv_workDefinition_16_0= RULE_STRING ) )
                    {
                    otherlv_15=(Token)match(input,14,FOLLOW_3); 

                        	newLeafNode(otherlv_15, grammarAccess.getTeamDefAccess().getWorkDefinitionKeyword_9_0());
                        
                    // InternalAtsDsl.g:1086:1: ( (lv_workDefinition_16_0= RULE_STRING ) )
                    // InternalAtsDsl.g:1087:1: (lv_workDefinition_16_0= RULE_STRING )
                    {
                    // InternalAtsDsl.g:1087:1: (lv_workDefinition_16_0= RULE_STRING )
                    // InternalAtsDsl.g:1088:3: lv_workDefinition_16_0= RULE_STRING
                    {
                    lv_workDefinition_16_0=(Token)match(input,RULE_STRING,FOLLOW_37); 

                    			newLeafNode(lv_workDefinition_16_0, grammarAccess.getTeamDefAccess().getWorkDefinitionSTRINGTerminalRuleCall_9_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getTeamDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"workDefinition",
                            		lv_workDefinition_16_0, 
                            		"org.eclipse.xtext.common.Terminals.STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            // InternalAtsDsl.g:1104:4: (otherlv_17= 'relatedTaskWorkDefinition' ( (lv_relatedTaskWorkDefinition_18_0= RULE_STRING ) ) )?
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==32) ) {
                alt32=1;
            }
            switch (alt32) {
                case 1 :
                    // InternalAtsDsl.g:1104:6: otherlv_17= 'relatedTaskWorkDefinition' ( (lv_relatedTaskWorkDefinition_18_0= RULE_STRING ) )
                    {
                    otherlv_17=(Token)match(input,32,FOLLOW_3); 

                        	newLeafNode(otherlv_17, grammarAccess.getTeamDefAccess().getRelatedTaskWorkDefinitionKeyword_10_0());
                        
                    // InternalAtsDsl.g:1108:1: ( (lv_relatedTaskWorkDefinition_18_0= RULE_STRING ) )
                    // InternalAtsDsl.g:1109:1: (lv_relatedTaskWorkDefinition_18_0= RULE_STRING )
                    {
                    // InternalAtsDsl.g:1109:1: (lv_relatedTaskWorkDefinition_18_0= RULE_STRING )
                    // InternalAtsDsl.g:1110:3: lv_relatedTaskWorkDefinition_18_0= RULE_STRING
                    {
                    lv_relatedTaskWorkDefinition_18_0=(Token)match(input,RULE_STRING,FOLLOW_38); 

                    			newLeafNode(lv_relatedTaskWorkDefinition_18_0, grammarAccess.getTeamDefAccess().getRelatedTaskWorkDefinitionSTRINGTerminalRuleCall_10_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getTeamDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"relatedTaskWorkDefinition",
                            		lv_relatedTaskWorkDefinition_18_0, 
                            		"org.eclipse.xtext.common.Terminals.STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            // InternalAtsDsl.g:1126:4: (otherlv_19= 'teamWorkflowArtifactType' ( (lv_teamWorkflowArtifactType_20_0= RULE_STRING ) ) )?
            int alt33=2;
            int LA33_0 = input.LA(1);

            if ( (LA33_0==33) ) {
                alt33=1;
            }
            switch (alt33) {
                case 1 :
                    // InternalAtsDsl.g:1126:6: otherlv_19= 'teamWorkflowArtifactType' ( (lv_teamWorkflowArtifactType_20_0= RULE_STRING ) )
                    {
                    otherlv_19=(Token)match(input,33,FOLLOW_3); 

                        	newLeafNode(otherlv_19, grammarAccess.getTeamDefAccess().getTeamWorkflowArtifactTypeKeyword_11_0());
                        
                    // InternalAtsDsl.g:1130:1: ( (lv_teamWorkflowArtifactType_20_0= RULE_STRING ) )
                    // InternalAtsDsl.g:1131:1: (lv_teamWorkflowArtifactType_20_0= RULE_STRING )
                    {
                    // InternalAtsDsl.g:1131:1: (lv_teamWorkflowArtifactType_20_0= RULE_STRING )
                    // InternalAtsDsl.g:1132:3: lv_teamWorkflowArtifactType_20_0= RULE_STRING
                    {
                    lv_teamWorkflowArtifactType_20_0=(Token)match(input,RULE_STRING,FOLLOW_39); 

                    			newLeafNode(lv_teamWorkflowArtifactType_20_0, grammarAccess.getTeamDefAccess().getTeamWorkflowArtifactTypeSTRINGTerminalRuleCall_11_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getTeamDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"teamWorkflowArtifactType",
                            		lv_teamWorkflowArtifactType_20_0, 
                            		"org.eclipse.xtext.common.Terminals.STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            // InternalAtsDsl.g:1148:4: (otherlv_21= 'accessContextId' ( (lv_accessContextId_22_0= RULE_STRING ) ) )*
            loop34:
            do {
                int alt34=2;
                int LA34_0 = input.LA(1);

                if ( (LA34_0==34) ) {
                    alt34=1;
                }


                switch (alt34) {
            	case 1 :
            	    // InternalAtsDsl.g:1148:6: otherlv_21= 'accessContextId' ( (lv_accessContextId_22_0= RULE_STRING ) )
            	    {
            	    otherlv_21=(Token)match(input,34,FOLLOW_3); 

            	        	newLeafNode(otherlv_21, grammarAccess.getTeamDefAccess().getAccessContextIdKeyword_12_0());
            	        
            	    // InternalAtsDsl.g:1152:1: ( (lv_accessContextId_22_0= RULE_STRING ) )
            	    // InternalAtsDsl.g:1153:1: (lv_accessContextId_22_0= RULE_STRING )
            	    {
            	    // InternalAtsDsl.g:1153:1: (lv_accessContextId_22_0= RULE_STRING )
            	    // InternalAtsDsl.g:1154:3: lv_accessContextId_22_0= RULE_STRING
            	    {
            	    lv_accessContextId_22_0=(Token)match(input,RULE_STRING,FOLLOW_39); 

            	    			newLeafNode(lv_accessContextId_22_0, grammarAccess.getTeamDefAccess().getAccessContextIdSTRINGTerminalRuleCall_12_1_0()); 
            	    		

            	    	        if (current==null) {
            	    	            current = createModelElement(grammarAccess.getTeamDefRule());
            	    	        }
            	           		addWithLastConsumed(
            	           			current, 
            	           			"accessContextId",
            	            		lv_accessContextId_22_0, 
            	            		"org.eclipse.xtext.common.Terminals.STRING");
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop34;
                }
            } while (true);

            // InternalAtsDsl.g:1170:4: (otherlv_23= 'version' ( (lv_version_24_0= ruleVersionDef ) ) )*
            loop35:
            do {
                int alt35=2;
                int LA35_0 = input.LA(1);

                if ( (LA35_0==35) ) {
                    alt35=1;
                }


                switch (alt35) {
            	case 1 :
            	    // InternalAtsDsl.g:1170:6: otherlv_23= 'version' ( (lv_version_24_0= ruleVersionDef ) )
            	    {
            	    otherlv_23=(Token)match(input,35,FOLLOW_3); 

            	        	newLeafNode(otherlv_23, grammarAccess.getTeamDefAccess().getVersionKeyword_13_0());
            	        
            	    // InternalAtsDsl.g:1174:1: ( (lv_version_24_0= ruleVersionDef ) )
            	    // InternalAtsDsl.g:1175:1: (lv_version_24_0= ruleVersionDef )
            	    {
            	    // InternalAtsDsl.g:1175:1: (lv_version_24_0= ruleVersionDef )
            	    // InternalAtsDsl.g:1176:3: lv_version_24_0= ruleVersionDef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getTeamDefAccess().getVersionVersionDefParserRuleCall_13_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_40);
            	    lv_version_24_0=ruleVersionDef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getTeamDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"version",
            	            		lv_version_24_0, 
            	            		"org.eclipse.osee.ats.dsl.AtsDsl.VersionDef");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop35;
                }
            } while (true);

            // InternalAtsDsl.g:1192:4: (otherlv_25= 'rule' ( (lv_rules_26_0= ruleRuleRef ) ) )*
            loop36:
            do {
                int alt36=2;
                int LA36_0 = input.LA(1);

                if ( (LA36_0==16) ) {
                    alt36=1;
                }


                switch (alt36) {
            	case 1 :
            	    // InternalAtsDsl.g:1192:6: otherlv_25= 'rule' ( (lv_rules_26_0= ruleRuleRef ) )
            	    {
            	    otherlv_25=(Token)match(input,16,FOLLOW_41); 

            	        	newLeafNode(otherlv_25, grammarAccess.getTeamDefAccess().getRuleKeyword_14_0());
            	        
            	    // InternalAtsDsl.g:1196:1: ( (lv_rules_26_0= ruleRuleRef ) )
            	    // InternalAtsDsl.g:1197:1: (lv_rules_26_0= ruleRuleRef )
            	    {
            	    // InternalAtsDsl.g:1197:1: (lv_rules_26_0= ruleRuleRef )
            	    // InternalAtsDsl.g:1198:3: lv_rules_26_0= ruleRuleRef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getTeamDefAccess().getRulesRuleRefParserRuleCall_14_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_42);
            	    lv_rules_26_0=ruleRuleRef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getTeamDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"rules",
            	            		lv_rules_26_0, 
            	            		"org.eclipse.osee.ats.dsl.AtsDsl.RuleRef");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop36;
                }
            } while (true);

            // InternalAtsDsl.g:1214:4: (otherlv_27= 'children' otherlv_28= '{' (otherlv_29= 'teamDefinition' ( (lv_children_30_0= ruleTeamDef ) ) )+ otherlv_31= '}' )?
            int alt38=2;
            int LA38_0 = input.LA(1);

            if ( (LA38_0==36) ) {
                alt38=1;
            }
            switch (alt38) {
                case 1 :
                    // InternalAtsDsl.g:1214:6: otherlv_27= 'children' otherlv_28= '{' (otherlv_29= 'teamDefinition' ( (lv_children_30_0= ruleTeamDef ) ) )+ otherlv_31= '}'
                    {
                    otherlv_27=(Token)match(input,36,FOLLOW_43); 

                        	newLeafNode(otherlv_27, grammarAccess.getTeamDefAccess().getChildrenKeyword_15_0());
                        
                    otherlv_28=(Token)match(input,17,FOLLOW_44); 

                        	newLeafNode(otherlv_28, grammarAccess.getTeamDefAccess().getLeftCurlyBracketKeyword_15_1());
                        
                    // InternalAtsDsl.g:1222:1: (otherlv_29= 'teamDefinition' ( (lv_children_30_0= ruleTeamDef ) ) )+
                    int cnt37=0;
                    loop37:
                    do {
                        int alt37=2;
                        int LA37_0 = input.LA(1);

                        if ( (LA37_0==12) ) {
                            alt37=1;
                        }


                        switch (alt37) {
                    	case 1 :
                    	    // InternalAtsDsl.g:1222:3: otherlv_29= 'teamDefinition' ( (lv_children_30_0= ruleTeamDef ) )
                    	    {
                    	    otherlv_29=(Token)match(input,12,FOLLOW_3); 

                    	        	newLeafNode(otherlv_29, grammarAccess.getTeamDefAccess().getTeamDefinitionKeyword_15_2_0());
                    	        
                    	    // InternalAtsDsl.g:1226:1: ( (lv_children_30_0= ruleTeamDef ) )
                    	    // InternalAtsDsl.g:1227:1: (lv_children_30_0= ruleTeamDef )
                    	    {
                    	    // InternalAtsDsl.g:1227:1: (lv_children_30_0= ruleTeamDef )
                    	    // InternalAtsDsl.g:1228:3: lv_children_30_0= ruleTeamDef
                    	    {
                    	     
                    	    	        newCompositeNode(grammarAccess.getTeamDefAccess().getChildrenTeamDefParserRuleCall_15_2_1_0()); 
                    	    	    
                    	    pushFollow(FOLLOW_45);
                    	    lv_children_30_0=ruleTeamDef();

                    	    state._fsp--;


                    	    	        if (current==null) {
                    	    	            current = createModelElementForParent(grammarAccess.getTeamDefRule());
                    	    	        }
                    	           		add(
                    	           			current, 
                    	           			"children",
                    	            		lv_children_30_0, 
                    	            		"org.eclipse.osee.ats.dsl.AtsDsl.TeamDef");
                    	    	        afterParserOrEnumRuleCall();
                    	    	    

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt37 >= 1 ) break loop37;
                                EarlyExitException eee =
                                    new EarlyExitException(37, input);
                                throw eee;
                        }
                        cnt37++;
                    } while (true);

                    otherlv_31=(Token)match(input,22,FOLLOW_17); 

                        	newLeafNode(otherlv_31, grammarAccess.getTeamDefAccess().getRightCurlyBracketKeyword_15_3());
                        

                    }
                    break;

            }

            otherlv_32=(Token)match(input,22,FOLLOW_2); 

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
    // InternalAtsDsl.g:1260:1: entryRuleAI_DEF_REFERENCE returns [String current=null] : iv_ruleAI_DEF_REFERENCE= ruleAI_DEF_REFERENCE EOF ;
    public final String entryRuleAI_DEF_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleAI_DEF_REFERENCE = null;


        try {
            // InternalAtsDsl.g:1261:2: (iv_ruleAI_DEF_REFERENCE= ruleAI_DEF_REFERENCE EOF )
            // InternalAtsDsl.g:1262:2: iv_ruleAI_DEF_REFERENCE= ruleAI_DEF_REFERENCE EOF
            {
             newCompositeNode(grammarAccess.getAI_DEF_REFERENCERule()); 
            pushFollow(FOLLOW_1);
            iv_ruleAI_DEF_REFERENCE=ruleAI_DEF_REFERENCE();

            state._fsp--;

             current =iv_ruleAI_DEF_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:1269:1: ruleAI_DEF_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleAI_DEF_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // InternalAtsDsl.g:1272:28: (this_STRING_0= RULE_STRING )
            // InternalAtsDsl.g:1273:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_2); 

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
    // InternalAtsDsl.g:1288:1: entryRuleActionableItemDef returns [EObject current=null] : iv_ruleActionableItemDef= ruleActionableItemDef EOF ;
    public final EObject entryRuleActionableItemDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleActionableItemDef = null;


        try {
            // InternalAtsDsl.g:1289:2: (iv_ruleActionableItemDef= ruleActionableItemDef EOF )
            // InternalAtsDsl.g:1290:2: iv_ruleActionableItemDef= ruleActionableItemDef EOF
            {
             newCompositeNode(grammarAccess.getActionableItemDefRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleActionableItemDef=ruleActionableItemDef();

            state._fsp--;

             current =iv_ruleActionableItemDef; 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:1297:1: ruleActionableItemDef returns [EObject current=null] : ( ( (lv_name_0_0= ruleAI_DEF_REFERENCE ) ) ( (lv_aiDefOption_1_0= ruleActionableItemOption ) )* (otherlv_2= '{' (otherlv_3= 'id' ( (lv_uuid_4_0= RULE_INT ) ) )? (otherlv_5= 'active' ( (lv_active_6_0= ruleBooleanDef ) ) )? (otherlv_7= 'actionable' ( (lv_actionable_8_0= ruleBooleanDef ) ) )? (otherlv_9= 'lead' ( (lv_lead_10_0= ruleUserRef ) ) )* (otherlv_11= 'owner' ( (lv_owner_12_0= ruleUserRef ) ) )* (otherlv_13= 'staticId' ( (lv_staticId_14_0= RULE_STRING ) ) )* (otherlv_15= 'team' ( (lv_teamDef_16_0= RULE_STRING ) ) )? (otherlv_17= 'accessContextId' ( (lv_accessContextId_18_0= RULE_STRING ) ) )* (otherlv_19= 'rule' ( (lv_rules_20_0= ruleRuleRef ) ) )* (otherlv_21= 'children' otherlv_22= '{' (otherlv_23= 'actionableItem' ( (lv_children_24_0= ruleActionableItemDef ) ) )+ otherlv_25= '}' )? otherlv_26= '}' )? ) ;
    public final EObject ruleActionableItemDef() throws RecognitionException {
        EObject current = null;

        Token otherlv_2=null;
        Token otherlv_3=null;
        Token lv_uuid_4_0=null;
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
        Token otherlv_21=null;
        Token otherlv_22=null;
        Token otherlv_23=null;
        Token otherlv_25=null;
        Token otherlv_26=null;
        AntlrDatatypeRuleToken lv_name_0_0 = null;

        AntlrDatatypeRuleToken lv_aiDefOption_1_0 = null;

        Enumerator lv_active_6_0 = null;

        Enumerator lv_actionable_8_0 = null;

        EObject lv_lead_10_0 = null;

        EObject lv_owner_12_0 = null;

        AntlrDatatypeRuleToken lv_rules_20_0 = null;

        EObject lv_children_24_0 = null;


         enterRule(); 
            
        try {
            // InternalAtsDsl.g:1300:28: ( ( ( (lv_name_0_0= ruleAI_DEF_REFERENCE ) ) ( (lv_aiDefOption_1_0= ruleActionableItemOption ) )* (otherlv_2= '{' (otherlv_3= 'id' ( (lv_uuid_4_0= RULE_INT ) ) )? (otherlv_5= 'active' ( (lv_active_6_0= ruleBooleanDef ) ) )? (otherlv_7= 'actionable' ( (lv_actionable_8_0= ruleBooleanDef ) ) )? (otherlv_9= 'lead' ( (lv_lead_10_0= ruleUserRef ) ) )* (otherlv_11= 'owner' ( (lv_owner_12_0= ruleUserRef ) ) )* (otherlv_13= 'staticId' ( (lv_staticId_14_0= RULE_STRING ) ) )* (otherlv_15= 'team' ( (lv_teamDef_16_0= RULE_STRING ) ) )? (otherlv_17= 'accessContextId' ( (lv_accessContextId_18_0= RULE_STRING ) ) )* (otherlv_19= 'rule' ( (lv_rules_20_0= ruleRuleRef ) ) )* (otherlv_21= 'children' otherlv_22= '{' (otherlv_23= 'actionableItem' ( (lv_children_24_0= ruleActionableItemDef ) ) )+ otherlv_25= '}' )? otherlv_26= '}' )? ) )
            // InternalAtsDsl.g:1301:1: ( ( (lv_name_0_0= ruleAI_DEF_REFERENCE ) ) ( (lv_aiDefOption_1_0= ruleActionableItemOption ) )* (otherlv_2= '{' (otherlv_3= 'id' ( (lv_uuid_4_0= RULE_INT ) ) )? (otherlv_5= 'active' ( (lv_active_6_0= ruleBooleanDef ) ) )? (otherlv_7= 'actionable' ( (lv_actionable_8_0= ruleBooleanDef ) ) )? (otherlv_9= 'lead' ( (lv_lead_10_0= ruleUserRef ) ) )* (otherlv_11= 'owner' ( (lv_owner_12_0= ruleUserRef ) ) )* (otherlv_13= 'staticId' ( (lv_staticId_14_0= RULE_STRING ) ) )* (otherlv_15= 'team' ( (lv_teamDef_16_0= RULE_STRING ) ) )? (otherlv_17= 'accessContextId' ( (lv_accessContextId_18_0= RULE_STRING ) ) )* (otherlv_19= 'rule' ( (lv_rules_20_0= ruleRuleRef ) ) )* (otherlv_21= 'children' otherlv_22= '{' (otherlv_23= 'actionableItem' ( (lv_children_24_0= ruleActionableItemDef ) ) )+ otherlv_25= '}' )? otherlv_26= '}' )? )
            {
            // InternalAtsDsl.g:1301:1: ( ( (lv_name_0_0= ruleAI_DEF_REFERENCE ) ) ( (lv_aiDefOption_1_0= ruleActionableItemOption ) )* (otherlv_2= '{' (otherlv_3= 'id' ( (lv_uuid_4_0= RULE_INT ) ) )? (otherlv_5= 'active' ( (lv_active_6_0= ruleBooleanDef ) ) )? (otherlv_7= 'actionable' ( (lv_actionable_8_0= ruleBooleanDef ) ) )? (otherlv_9= 'lead' ( (lv_lead_10_0= ruleUserRef ) ) )* (otherlv_11= 'owner' ( (lv_owner_12_0= ruleUserRef ) ) )* (otherlv_13= 'staticId' ( (lv_staticId_14_0= RULE_STRING ) ) )* (otherlv_15= 'team' ( (lv_teamDef_16_0= RULE_STRING ) ) )? (otherlv_17= 'accessContextId' ( (lv_accessContextId_18_0= RULE_STRING ) ) )* (otherlv_19= 'rule' ( (lv_rules_20_0= ruleRuleRef ) ) )* (otherlv_21= 'children' otherlv_22= '{' (otherlv_23= 'actionableItem' ( (lv_children_24_0= ruleActionableItemDef ) ) )+ otherlv_25= '}' )? otherlv_26= '}' )? )
            // InternalAtsDsl.g:1301:2: ( (lv_name_0_0= ruleAI_DEF_REFERENCE ) ) ( (lv_aiDefOption_1_0= ruleActionableItemOption ) )* (otherlv_2= '{' (otherlv_3= 'id' ( (lv_uuid_4_0= RULE_INT ) ) )? (otherlv_5= 'active' ( (lv_active_6_0= ruleBooleanDef ) ) )? (otherlv_7= 'actionable' ( (lv_actionable_8_0= ruleBooleanDef ) ) )? (otherlv_9= 'lead' ( (lv_lead_10_0= ruleUserRef ) ) )* (otherlv_11= 'owner' ( (lv_owner_12_0= ruleUserRef ) ) )* (otherlv_13= 'staticId' ( (lv_staticId_14_0= RULE_STRING ) ) )* (otherlv_15= 'team' ( (lv_teamDef_16_0= RULE_STRING ) ) )? (otherlv_17= 'accessContextId' ( (lv_accessContextId_18_0= RULE_STRING ) ) )* (otherlv_19= 'rule' ( (lv_rules_20_0= ruleRuleRef ) ) )* (otherlv_21= 'children' otherlv_22= '{' (otherlv_23= 'actionableItem' ( (lv_children_24_0= ruleActionableItemDef ) ) )+ otherlv_25= '}' )? otherlv_26= '}' )?
            {
            // InternalAtsDsl.g:1301:2: ( (lv_name_0_0= ruleAI_DEF_REFERENCE ) )
            // InternalAtsDsl.g:1302:1: (lv_name_0_0= ruleAI_DEF_REFERENCE )
            {
            // InternalAtsDsl.g:1302:1: (lv_name_0_0= ruleAI_DEF_REFERENCE )
            // InternalAtsDsl.g:1303:3: lv_name_0_0= ruleAI_DEF_REFERENCE
            {
             
            	        newCompositeNode(grammarAccess.getActionableItemDefAccess().getNameAI_DEF_REFERENCEParserRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_11);
            lv_name_0_0=ruleAI_DEF_REFERENCE();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getActionableItemDefRule());
            	        }
                   		set(
                   			current, 
                   			"name",
                    		lv_name_0_0, 
                    		"org.eclipse.osee.ats.dsl.AtsDsl.AI_DEF_REFERENCE");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // InternalAtsDsl.g:1319:2: ( (lv_aiDefOption_1_0= ruleActionableItemOption ) )*
            loop39:
            do {
                int alt39=2;
                int LA39_0 = input.LA(1);

                if ( (LA39_0==RULE_STRING||LA39_0==83) ) {
                    alt39=1;
                }


                switch (alt39) {
            	case 1 :
            	    // InternalAtsDsl.g:1320:1: (lv_aiDefOption_1_0= ruleActionableItemOption )
            	    {
            	    // InternalAtsDsl.g:1320:1: (lv_aiDefOption_1_0= ruleActionableItemOption )
            	    // InternalAtsDsl.g:1321:3: lv_aiDefOption_1_0= ruleActionableItemOption
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getActionableItemDefAccess().getAiDefOptionActionableItemOptionParserRuleCall_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_11);
            	    lv_aiDefOption_1_0=ruleActionableItemOption();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getActionableItemDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"aiDefOption",
            	            		lv_aiDefOption_1_0, 
            	            		"org.eclipse.osee.ats.dsl.AtsDsl.ActionableItemOption");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    break loop39;
                }
            } while (true);

            // InternalAtsDsl.g:1337:3: (otherlv_2= '{' (otherlv_3= 'id' ( (lv_uuid_4_0= RULE_INT ) ) )? (otherlv_5= 'active' ( (lv_active_6_0= ruleBooleanDef ) ) )? (otherlv_7= 'actionable' ( (lv_actionable_8_0= ruleBooleanDef ) ) )? (otherlv_9= 'lead' ( (lv_lead_10_0= ruleUserRef ) ) )* (otherlv_11= 'owner' ( (lv_owner_12_0= ruleUserRef ) ) )* (otherlv_13= 'staticId' ( (lv_staticId_14_0= RULE_STRING ) ) )* (otherlv_15= 'team' ( (lv_teamDef_16_0= RULE_STRING ) ) )? (otherlv_17= 'accessContextId' ( (lv_accessContextId_18_0= RULE_STRING ) ) )* (otherlv_19= 'rule' ( (lv_rules_20_0= ruleRuleRef ) ) )* (otherlv_21= 'children' otherlv_22= '{' (otherlv_23= 'actionableItem' ( (lv_children_24_0= ruleActionableItemDef ) ) )+ otherlv_25= '}' )? otherlv_26= '}' )?
            int alt51=2;
            int LA51_0 = input.LA(1);

            if ( (LA51_0==17) ) {
                alt51=1;
            }
            switch (alt51) {
                case 1 :
                    // InternalAtsDsl.g:1337:5: otherlv_2= '{' (otherlv_3= 'id' ( (lv_uuid_4_0= RULE_INT ) ) )? (otherlv_5= 'active' ( (lv_active_6_0= ruleBooleanDef ) ) )? (otherlv_7= 'actionable' ( (lv_actionable_8_0= ruleBooleanDef ) ) )? (otherlv_9= 'lead' ( (lv_lead_10_0= ruleUserRef ) ) )* (otherlv_11= 'owner' ( (lv_owner_12_0= ruleUserRef ) ) )* (otherlv_13= 'staticId' ( (lv_staticId_14_0= RULE_STRING ) ) )* (otherlv_15= 'team' ( (lv_teamDef_16_0= RULE_STRING ) ) )? (otherlv_17= 'accessContextId' ( (lv_accessContextId_18_0= RULE_STRING ) ) )* (otherlv_19= 'rule' ( (lv_rules_20_0= ruleRuleRef ) ) )* (otherlv_21= 'children' otherlv_22= '{' (otherlv_23= 'actionableItem' ( (lv_children_24_0= ruleActionableItemDef ) ) )+ otherlv_25= '}' )? otherlv_26= '}'
                    {
                    otherlv_2=(Token)match(input,17,FOLLOW_46); 

                        	newLeafNode(otherlv_2, grammarAccess.getActionableItemDefAccess().getLeftCurlyBracketKeyword_2_0());
                        
                    // InternalAtsDsl.g:1341:1: (otherlv_3= 'id' ( (lv_uuid_4_0= RULE_INT ) ) )?
                    int alt40=2;
                    int LA40_0 = input.LA(1);

                    if ( (LA40_0==24) ) {
                        alt40=1;
                    }
                    switch (alt40) {
                        case 1 :
                            // InternalAtsDsl.g:1341:3: otherlv_3= 'id' ( (lv_uuid_4_0= RULE_INT ) )
                            {
                            otherlv_3=(Token)match(input,24,FOLLOW_24); 

                                	newLeafNode(otherlv_3, grammarAccess.getActionableItemDefAccess().getIdKeyword_2_1_0());
                                
                            // InternalAtsDsl.g:1345:1: ( (lv_uuid_4_0= RULE_INT ) )
                            // InternalAtsDsl.g:1346:1: (lv_uuid_4_0= RULE_INT )
                            {
                            // InternalAtsDsl.g:1346:1: (lv_uuid_4_0= RULE_INT )
                            // InternalAtsDsl.g:1347:3: lv_uuid_4_0= RULE_INT
                            {
                            lv_uuid_4_0=(Token)match(input,RULE_INT,FOLLOW_47); 

                            			newLeafNode(lv_uuid_4_0, grammarAccess.getActionableItemDefAccess().getUuidINTTerminalRuleCall_2_1_1_0()); 
                            		

                            	        if (current==null) {
                            	            current = createModelElement(grammarAccess.getActionableItemDefRule());
                            	        }
                                   		setWithLastConsumed(
                                   			current, 
                                   			"uuid",
                                    		lv_uuid_4_0, 
                                    		"org.eclipse.xtext.common.Terminals.INT");
                            	    

                            }


                            }


                            }
                            break;

                    }

                    // InternalAtsDsl.g:1363:4: (otherlv_5= 'active' ( (lv_active_6_0= ruleBooleanDef ) ) )?
                    int alt41=2;
                    int LA41_0 = input.LA(1);

                    if ( (LA41_0==18) ) {
                        alt41=1;
                    }
                    switch (alt41) {
                        case 1 :
                            // InternalAtsDsl.g:1363:6: otherlv_5= 'active' ( (lv_active_6_0= ruleBooleanDef ) )
                            {
                            otherlv_5=(Token)match(input,18,FOLLOW_13); 

                                	newLeafNode(otherlv_5, grammarAccess.getActionableItemDefAccess().getActiveKeyword_2_2_0());
                                
                            // InternalAtsDsl.g:1367:1: ( (lv_active_6_0= ruleBooleanDef ) )
                            // InternalAtsDsl.g:1368:1: (lv_active_6_0= ruleBooleanDef )
                            {
                            // InternalAtsDsl.g:1368:1: (lv_active_6_0= ruleBooleanDef )
                            // InternalAtsDsl.g:1369:3: lv_active_6_0= ruleBooleanDef
                            {
                             
                            	        newCompositeNode(grammarAccess.getActionableItemDefAccess().getActiveBooleanDefEnumRuleCall_2_2_1_0()); 
                            	    
                            pushFollow(FOLLOW_48);
                            lv_active_6_0=ruleBooleanDef();

                            state._fsp--;


                            	        if (current==null) {
                            	            current = createModelElementForParent(grammarAccess.getActionableItemDefRule());
                            	        }
                                   		set(
                                   			current, 
                                   			"active",
                                    		lv_active_6_0, 
                                    		"org.eclipse.osee.ats.dsl.AtsDsl.BooleanDef");
                            	        afterParserOrEnumRuleCall();
                            	    

                            }


                            }


                            }
                            break;

                    }

                    // InternalAtsDsl.g:1385:4: (otherlv_7= 'actionable' ( (lv_actionable_8_0= ruleBooleanDef ) ) )?
                    int alt42=2;
                    int LA42_0 = input.LA(1);

                    if ( (LA42_0==37) ) {
                        alt42=1;
                    }
                    switch (alt42) {
                        case 1 :
                            // InternalAtsDsl.g:1385:6: otherlv_7= 'actionable' ( (lv_actionable_8_0= ruleBooleanDef ) )
                            {
                            otherlv_7=(Token)match(input,37,FOLLOW_13); 

                                	newLeafNode(otherlv_7, grammarAccess.getActionableItemDefAccess().getActionableKeyword_2_3_0());
                                
                            // InternalAtsDsl.g:1389:1: ( (lv_actionable_8_0= ruleBooleanDef ) )
                            // InternalAtsDsl.g:1390:1: (lv_actionable_8_0= ruleBooleanDef )
                            {
                            // InternalAtsDsl.g:1390:1: (lv_actionable_8_0= ruleBooleanDef )
                            // InternalAtsDsl.g:1391:3: lv_actionable_8_0= ruleBooleanDef
                            {
                             
                            	        newCompositeNode(grammarAccess.getActionableItemDefAccess().getActionableBooleanDefEnumRuleCall_2_3_1_0()); 
                            	    
                            pushFollow(FOLLOW_49);
                            lv_actionable_8_0=ruleBooleanDef();

                            state._fsp--;


                            	        if (current==null) {
                            	            current = createModelElementForParent(grammarAccess.getActionableItemDefRule());
                            	        }
                                   		set(
                                   			current, 
                                   			"actionable",
                                    		lv_actionable_8_0, 
                                    		"org.eclipse.osee.ats.dsl.AtsDsl.BooleanDef");
                            	        afterParserOrEnumRuleCall();
                            	    

                            }


                            }


                            }
                            break;

                    }

                    // InternalAtsDsl.g:1407:4: (otherlv_9= 'lead' ( (lv_lead_10_0= ruleUserRef ) ) )*
                    loop43:
                    do {
                        int alt43=2;
                        int LA43_0 = input.LA(1);

                        if ( (LA43_0==29) ) {
                            alt43=1;
                        }


                        switch (alt43) {
                    	case 1 :
                    	    // InternalAtsDsl.g:1407:6: otherlv_9= 'lead' ( (lv_lead_10_0= ruleUserRef ) )
                    	    {
                    	    otherlv_9=(Token)match(input,29,FOLLOW_33); 

                    	        	newLeafNode(otherlv_9, grammarAccess.getActionableItemDefAccess().getLeadKeyword_2_4_0());
                    	        
                    	    // InternalAtsDsl.g:1411:1: ( (lv_lead_10_0= ruleUserRef ) )
                    	    // InternalAtsDsl.g:1412:1: (lv_lead_10_0= ruleUserRef )
                    	    {
                    	    // InternalAtsDsl.g:1412:1: (lv_lead_10_0= ruleUserRef )
                    	    // InternalAtsDsl.g:1413:3: lv_lead_10_0= ruleUserRef
                    	    {
                    	     
                    	    	        newCompositeNode(grammarAccess.getActionableItemDefAccess().getLeadUserRefParserRuleCall_2_4_1_0()); 
                    	    	    
                    	    pushFollow(FOLLOW_49);
                    	    lv_lead_10_0=ruleUserRef();

                    	    state._fsp--;


                    	    	        if (current==null) {
                    	    	            current = createModelElementForParent(grammarAccess.getActionableItemDefRule());
                    	    	        }
                    	           		add(
                    	           			current, 
                    	           			"lead",
                    	            		lv_lead_10_0, 
                    	            		"org.eclipse.osee.ats.dsl.AtsDsl.UserRef");
                    	    	        afterParserOrEnumRuleCall();
                    	    	    

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop43;
                        }
                    } while (true);

                    // InternalAtsDsl.g:1429:4: (otherlv_11= 'owner' ( (lv_owner_12_0= ruleUserRef ) ) )*
                    loop44:
                    do {
                        int alt44=2;
                        int LA44_0 = input.LA(1);

                        if ( (LA44_0==38) ) {
                            alt44=1;
                        }


                        switch (alt44) {
                    	case 1 :
                    	    // InternalAtsDsl.g:1429:6: otherlv_11= 'owner' ( (lv_owner_12_0= ruleUserRef ) )
                    	    {
                    	    otherlv_11=(Token)match(input,38,FOLLOW_33); 

                    	        	newLeafNode(otherlv_11, grammarAccess.getActionableItemDefAccess().getOwnerKeyword_2_5_0());
                    	        
                    	    // InternalAtsDsl.g:1433:1: ( (lv_owner_12_0= ruleUserRef ) )
                    	    // InternalAtsDsl.g:1434:1: (lv_owner_12_0= ruleUserRef )
                    	    {
                    	    // InternalAtsDsl.g:1434:1: (lv_owner_12_0= ruleUserRef )
                    	    // InternalAtsDsl.g:1435:3: lv_owner_12_0= ruleUserRef
                    	    {
                    	     
                    	    	        newCompositeNode(grammarAccess.getActionableItemDefAccess().getOwnerUserRefParserRuleCall_2_5_1_0()); 
                    	    	    
                    	    pushFollow(FOLLOW_50);
                    	    lv_owner_12_0=ruleUserRef();

                    	    state._fsp--;


                    	    	        if (current==null) {
                    	    	            current = createModelElementForParent(grammarAccess.getActionableItemDefRule());
                    	    	        }
                    	           		add(
                    	           			current, 
                    	           			"owner",
                    	            		lv_owner_12_0, 
                    	            		"org.eclipse.osee.ats.dsl.AtsDsl.UserRef");
                    	    	        afterParserOrEnumRuleCall();
                    	    	    

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop44;
                        }
                    } while (true);

                    // InternalAtsDsl.g:1451:4: (otherlv_13= 'staticId' ( (lv_staticId_14_0= RULE_STRING ) ) )*
                    loop45:
                    do {
                        int alt45=2;
                        int LA45_0 = input.LA(1);

                        if ( (LA45_0==28) ) {
                            alt45=1;
                        }


                        switch (alt45) {
                    	case 1 :
                    	    // InternalAtsDsl.g:1451:6: otherlv_13= 'staticId' ( (lv_staticId_14_0= RULE_STRING ) )
                    	    {
                    	    otherlv_13=(Token)match(input,28,FOLLOW_3); 

                    	        	newLeafNode(otherlv_13, grammarAccess.getActionableItemDefAccess().getStaticIdKeyword_2_6_0());
                    	        
                    	    // InternalAtsDsl.g:1455:1: ( (lv_staticId_14_0= RULE_STRING ) )
                    	    // InternalAtsDsl.g:1456:1: (lv_staticId_14_0= RULE_STRING )
                    	    {
                    	    // InternalAtsDsl.g:1456:1: (lv_staticId_14_0= RULE_STRING )
                    	    // InternalAtsDsl.g:1457:3: lv_staticId_14_0= RULE_STRING
                    	    {
                    	    lv_staticId_14_0=(Token)match(input,RULE_STRING,FOLLOW_51); 

                    	    			newLeafNode(lv_staticId_14_0, grammarAccess.getActionableItemDefAccess().getStaticIdSTRINGTerminalRuleCall_2_6_1_0()); 
                    	    		

                    	    	        if (current==null) {
                    	    	            current = createModelElement(grammarAccess.getActionableItemDefRule());
                    	    	        }
                    	           		addWithLastConsumed(
                    	           			current, 
                    	           			"staticId",
                    	            		lv_staticId_14_0, 
                    	            		"org.eclipse.xtext.common.Terminals.STRING");
                    	    	    

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop45;
                        }
                    } while (true);

                    // InternalAtsDsl.g:1473:4: (otherlv_15= 'team' ( (lv_teamDef_16_0= RULE_STRING ) ) )?
                    int alt46=2;
                    int LA46_0 = input.LA(1);

                    if ( (LA46_0==39) ) {
                        alt46=1;
                    }
                    switch (alt46) {
                        case 1 :
                            // InternalAtsDsl.g:1473:6: otherlv_15= 'team' ( (lv_teamDef_16_0= RULE_STRING ) )
                            {
                            otherlv_15=(Token)match(input,39,FOLLOW_3); 

                                	newLeafNode(otherlv_15, grammarAccess.getActionableItemDefAccess().getTeamKeyword_2_7_0());
                                
                            // InternalAtsDsl.g:1477:1: ( (lv_teamDef_16_0= RULE_STRING ) )
                            // InternalAtsDsl.g:1478:1: (lv_teamDef_16_0= RULE_STRING )
                            {
                            // InternalAtsDsl.g:1478:1: (lv_teamDef_16_0= RULE_STRING )
                            // InternalAtsDsl.g:1479:3: lv_teamDef_16_0= RULE_STRING
                            {
                            lv_teamDef_16_0=(Token)match(input,RULE_STRING,FOLLOW_52); 

                            			newLeafNode(lv_teamDef_16_0, grammarAccess.getActionableItemDefAccess().getTeamDefSTRINGTerminalRuleCall_2_7_1_0()); 
                            		

                            	        if (current==null) {
                            	            current = createModelElement(grammarAccess.getActionableItemDefRule());
                            	        }
                                   		setWithLastConsumed(
                                   			current, 
                                   			"teamDef",
                                    		lv_teamDef_16_0, 
                                    		"org.eclipse.xtext.common.Terminals.STRING");
                            	    

                            }


                            }


                            }
                            break;

                    }

                    // InternalAtsDsl.g:1495:4: (otherlv_17= 'accessContextId' ( (lv_accessContextId_18_0= RULE_STRING ) ) )*
                    loop47:
                    do {
                        int alt47=2;
                        int LA47_0 = input.LA(1);

                        if ( (LA47_0==34) ) {
                            alt47=1;
                        }


                        switch (alt47) {
                    	case 1 :
                    	    // InternalAtsDsl.g:1495:6: otherlv_17= 'accessContextId' ( (lv_accessContextId_18_0= RULE_STRING ) )
                    	    {
                    	    otherlv_17=(Token)match(input,34,FOLLOW_3); 

                    	        	newLeafNode(otherlv_17, grammarAccess.getActionableItemDefAccess().getAccessContextIdKeyword_2_8_0());
                    	        
                    	    // InternalAtsDsl.g:1499:1: ( (lv_accessContextId_18_0= RULE_STRING ) )
                    	    // InternalAtsDsl.g:1500:1: (lv_accessContextId_18_0= RULE_STRING )
                    	    {
                    	    // InternalAtsDsl.g:1500:1: (lv_accessContextId_18_0= RULE_STRING )
                    	    // InternalAtsDsl.g:1501:3: lv_accessContextId_18_0= RULE_STRING
                    	    {
                    	    lv_accessContextId_18_0=(Token)match(input,RULE_STRING,FOLLOW_52); 

                    	    			newLeafNode(lv_accessContextId_18_0, grammarAccess.getActionableItemDefAccess().getAccessContextIdSTRINGTerminalRuleCall_2_8_1_0()); 
                    	    		

                    	    	        if (current==null) {
                    	    	            current = createModelElement(grammarAccess.getActionableItemDefRule());
                    	    	        }
                    	           		addWithLastConsumed(
                    	           			current, 
                    	           			"accessContextId",
                    	            		lv_accessContextId_18_0, 
                    	            		"org.eclipse.xtext.common.Terminals.STRING");
                    	    	    

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop47;
                        }
                    } while (true);

                    // InternalAtsDsl.g:1517:4: (otherlv_19= 'rule' ( (lv_rules_20_0= ruleRuleRef ) ) )*
                    loop48:
                    do {
                        int alt48=2;
                        int LA48_0 = input.LA(1);

                        if ( (LA48_0==16) ) {
                            alt48=1;
                        }


                        switch (alt48) {
                    	case 1 :
                    	    // InternalAtsDsl.g:1517:6: otherlv_19= 'rule' ( (lv_rules_20_0= ruleRuleRef ) )
                    	    {
                    	    otherlv_19=(Token)match(input,16,FOLLOW_41); 

                    	        	newLeafNode(otherlv_19, grammarAccess.getActionableItemDefAccess().getRuleKeyword_2_9_0());
                    	        
                    	    // InternalAtsDsl.g:1521:1: ( (lv_rules_20_0= ruleRuleRef ) )
                    	    // InternalAtsDsl.g:1522:1: (lv_rules_20_0= ruleRuleRef )
                    	    {
                    	    // InternalAtsDsl.g:1522:1: (lv_rules_20_0= ruleRuleRef )
                    	    // InternalAtsDsl.g:1523:3: lv_rules_20_0= ruleRuleRef
                    	    {
                    	     
                    	    	        newCompositeNode(grammarAccess.getActionableItemDefAccess().getRulesRuleRefParserRuleCall_2_9_1_0()); 
                    	    	    
                    	    pushFollow(FOLLOW_42);
                    	    lv_rules_20_0=ruleRuleRef();

                    	    state._fsp--;


                    	    	        if (current==null) {
                    	    	            current = createModelElementForParent(grammarAccess.getActionableItemDefRule());
                    	    	        }
                    	           		add(
                    	           			current, 
                    	           			"rules",
                    	            		lv_rules_20_0, 
                    	            		"org.eclipse.osee.ats.dsl.AtsDsl.RuleRef");
                    	    	        afterParserOrEnumRuleCall();
                    	    	    

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop48;
                        }
                    } while (true);

                    // InternalAtsDsl.g:1539:4: (otherlv_21= 'children' otherlv_22= '{' (otherlv_23= 'actionableItem' ( (lv_children_24_0= ruleActionableItemDef ) ) )+ otherlv_25= '}' )?
                    int alt50=2;
                    int LA50_0 = input.LA(1);

                    if ( (LA50_0==36) ) {
                        alt50=1;
                    }
                    switch (alt50) {
                        case 1 :
                            // InternalAtsDsl.g:1539:6: otherlv_21= 'children' otherlv_22= '{' (otherlv_23= 'actionableItem' ( (lv_children_24_0= ruleActionableItemDef ) ) )+ otherlv_25= '}'
                            {
                            otherlv_21=(Token)match(input,36,FOLLOW_43); 

                                	newLeafNode(otherlv_21, grammarAccess.getActionableItemDefAccess().getChildrenKeyword_2_10_0());
                                
                            otherlv_22=(Token)match(input,17,FOLLOW_53); 

                                	newLeafNode(otherlv_22, grammarAccess.getActionableItemDefAccess().getLeftCurlyBracketKeyword_2_10_1());
                                
                            // InternalAtsDsl.g:1547:1: (otherlv_23= 'actionableItem' ( (lv_children_24_0= ruleActionableItemDef ) ) )+
                            int cnt49=0;
                            loop49:
                            do {
                                int alt49=2;
                                int LA49_0 = input.LA(1);

                                if ( (LA49_0==13) ) {
                                    alt49=1;
                                }


                                switch (alt49) {
                            	case 1 :
                            	    // InternalAtsDsl.g:1547:3: otherlv_23= 'actionableItem' ( (lv_children_24_0= ruleActionableItemDef ) )
                            	    {
                            	    otherlv_23=(Token)match(input,13,FOLLOW_3); 

                            	        	newLeafNode(otherlv_23, grammarAccess.getActionableItemDefAccess().getActionableItemKeyword_2_10_2_0());
                            	        
                            	    // InternalAtsDsl.g:1551:1: ( (lv_children_24_0= ruleActionableItemDef ) )
                            	    // InternalAtsDsl.g:1552:1: (lv_children_24_0= ruleActionableItemDef )
                            	    {
                            	    // InternalAtsDsl.g:1552:1: (lv_children_24_0= ruleActionableItemDef )
                            	    // InternalAtsDsl.g:1553:3: lv_children_24_0= ruleActionableItemDef
                            	    {
                            	     
                            	    	        newCompositeNode(grammarAccess.getActionableItemDefAccess().getChildrenActionableItemDefParserRuleCall_2_10_2_1_0()); 
                            	    	    
                            	    pushFollow(FOLLOW_54);
                            	    lv_children_24_0=ruleActionableItemDef();

                            	    state._fsp--;


                            	    	        if (current==null) {
                            	    	            current = createModelElementForParent(grammarAccess.getActionableItemDefRule());
                            	    	        }
                            	           		add(
                            	           			current, 
                            	           			"children",
                            	            		lv_children_24_0, 
                            	            		"org.eclipse.osee.ats.dsl.AtsDsl.ActionableItemDef");
                            	    	        afterParserOrEnumRuleCall();
                            	    	    

                            	    }


                            	    }


                            	    }
                            	    break;

                            	default :
                            	    if ( cnt49 >= 1 ) break loop49;
                                        EarlyExitException eee =
                                            new EarlyExitException(49, input);
                                        throw eee;
                                }
                                cnt49++;
                            } while (true);

                            otherlv_25=(Token)match(input,22,FOLLOW_17); 

                                	newLeafNode(otherlv_25, grammarAccess.getActionableItemDefAccess().getRightCurlyBracketKeyword_2_10_3());
                                

                            }
                            break;

                    }

                    otherlv_26=(Token)match(input,22,FOLLOW_2); 

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
    // InternalAtsDsl.g:1585:1: entryRuleVERSION_DEF_REFERENCE returns [String current=null] : iv_ruleVERSION_DEF_REFERENCE= ruleVERSION_DEF_REFERENCE EOF ;
    public final String entryRuleVERSION_DEF_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleVERSION_DEF_REFERENCE = null;


        try {
            // InternalAtsDsl.g:1586:2: (iv_ruleVERSION_DEF_REFERENCE= ruleVERSION_DEF_REFERENCE EOF )
            // InternalAtsDsl.g:1587:2: iv_ruleVERSION_DEF_REFERENCE= ruleVERSION_DEF_REFERENCE EOF
            {
             newCompositeNode(grammarAccess.getVERSION_DEF_REFERENCERule()); 
            pushFollow(FOLLOW_1);
            iv_ruleVERSION_DEF_REFERENCE=ruleVERSION_DEF_REFERENCE();

            state._fsp--;

             current =iv_ruleVERSION_DEF_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:1594:1: ruleVERSION_DEF_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleVERSION_DEF_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // InternalAtsDsl.g:1597:28: (this_STRING_0= RULE_STRING )
            // InternalAtsDsl.g:1598:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_2); 

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
    // InternalAtsDsl.g:1613:1: entryRuleVersionDef returns [EObject current=null] : iv_ruleVersionDef= ruleVersionDef EOF ;
    public final EObject entryRuleVersionDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleVersionDef = null;


        try {
            // InternalAtsDsl.g:1614:2: (iv_ruleVersionDef= ruleVersionDef EOF )
            // InternalAtsDsl.g:1615:2: iv_ruleVersionDef= ruleVersionDef EOF
            {
             newCompositeNode(grammarAccess.getVersionDefRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleVersionDef=ruleVersionDef();

            state._fsp--;

             current =iv_ruleVersionDef; 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:1622:1: ruleVersionDef returns [EObject current=null] : ( ( (lv_name_0_0= ruleVERSION_DEF_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'id' ( (lv_uuid_3_0= RULE_INT ) ) )? (otherlv_4= 'staticId' ( (lv_staticId_5_0= RULE_STRING ) ) )* (otherlv_6= 'next' ( (lv_next_7_0= ruleBooleanDef ) ) )? (otherlv_8= 'released' ( (lv_released_9_0= ruleBooleanDef ) ) )? (otherlv_10= 'allowCreateBranch' ( (lv_allowCreateBranch_11_0= ruleBooleanDef ) ) )? (otherlv_12= 'allowCommitBranch' ( (lv_allowCommitBranch_13_0= ruleBooleanDef ) ) )? (otherlv_14= 'baselineBranchUuid' ( (lv_baselineBranchUuid_15_0= RULE_STRING ) ) )? (otherlv_16= 'parallelVersion' ( (lv_parallelVersion_17_0= RULE_STRING ) ) )* otherlv_18= '}' ) ;
    public final EObject ruleVersionDef() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_2=null;
        Token lv_uuid_3_0=null;
        Token otherlv_4=null;
        Token lv_staticId_5_0=null;
        Token otherlv_6=null;
        Token otherlv_8=null;
        Token otherlv_10=null;
        Token otherlv_12=null;
        Token otherlv_14=null;
        Token lv_baselineBranchUuid_15_0=null;
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
            // InternalAtsDsl.g:1625:28: ( ( ( (lv_name_0_0= ruleVERSION_DEF_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'id' ( (lv_uuid_3_0= RULE_INT ) ) )? (otherlv_4= 'staticId' ( (lv_staticId_5_0= RULE_STRING ) ) )* (otherlv_6= 'next' ( (lv_next_7_0= ruleBooleanDef ) ) )? (otherlv_8= 'released' ( (lv_released_9_0= ruleBooleanDef ) ) )? (otherlv_10= 'allowCreateBranch' ( (lv_allowCreateBranch_11_0= ruleBooleanDef ) ) )? (otherlv_12= 'allowCommitBranch' ( (lv_allowCommitBranch_13_0= ruleBooleanDef ) ) )? (otherlv_14= 'baselineBranchUuid' ( (lv_baselineBranchUuid_15_0= RULE_STRING ) ) )? (otherlv_16= 'parallelVersion' ( (lv_parallelVersion_17_0= RULE_STRING ) ) )* otherlv_18= '}' ) )
            // InternalAtsDsl.g:1626:1: ( ( (lv_name_0_0= ruleVERSION_DEF_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'id' ( (lv_uuid_3_0= RULE_INT ) ) )? (otherlv_4= 'staticId' ( (lv_staticId_5_0= RULE_STRING ) ) )* (otherlv_6= 'next' ( (lv_next_7_0= ruleBooleanDef ) ) )? (otherlv_8= 'released' ( (lv_released_9_0= ruleBooleanDef ) ) )? (otherlv_10= 'allowCreateBranch' ( (lv_allowCreateBranch_11_0= ruleBooleanDef ) ) )? (otherlv_12= 'allowCommitBranch' ( (lv_allowCommitBranch_13_0= ruleBooleanDef ) ) )? (otherlv_14= 'baselineBranchUuid' ( (lv_baselineBranchUuid_15_0= RULE_STRING ) ) )? (otherlv_16= 'parallelVersion' ( (lv_parallelVersion_17_0= RULE_STRING ) ) )* otherlv_18= '}' )
            {
            // InternalAtsDsl.g:1626:1: ( ( (lv_name_0_0= ruleVERSION_DEF_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'id' ( (lv_uuid_3_0= RULE_INT ) ) )? (otherlv_4= 'staticId' ( (lv_staticId_5_0= RULE_STRING ) ) )* (otherlv_6= 'next' ( (lv_next_7_0= ruleBooleanDef ) ) )? (otherlv_8= 'released' ( (lv_released_9_0= ruleBooleanDef ) ) )? (otherlv_10= 'allowCreateBranch' ( (lv_allowCreateBranch_11_0= ruleBooleanDef ) ) )? (otherlv_12= 'allowCommitBranch' ( (lv_allowCommitBranch_13_0= ruleBooleanDef ) ) )? (otherlv_14= 'baselineBranchUuid' ( (lv_baselineBranchUuid_15_0= RULE_STRING ) ) )? (otherlv_16= 'parallelVersion' ( (lv_parallelVersion_17_0= RULE_STRING ) ) )* otherlv_18= '}' )
            // InternalAtsDsl.g:1626:2: ( (lv_name_0_0= ruleVERSION_DEF_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'id' ( (lv_uuid_3_0= RULE_INT ) ) )? (otherlv_4= 'staticId' ( (lv_staticId_5_0= RULE_STRING ) ) )* (otherlv_6= 'next' ( (lv_next_7_0= ruleBooleanDef ) ) )? (otherlv_8= 'released' ( (lv_released_9_0= ruleBooleanDef ) ) )? (otherlv_10= 'allowCreateBranch' ( (lv_allowCreateBranch_11_0= ruleBooleanDef ) ) )? (otherlv_12= 'allowCommitBranch' ( (lv_allowCommitBranch_13_0= ruleBooleanDef ) ) )? (otherlv_14= 'baselineBranchUuid' ( (lv_baselineBranchUuid_15_0= RULE_STRING ) ) )? (otherlv_16= 'parallelVersion' ( (lv_parallelVersion_17_0= RULE_STRING ) ) )* otherlv_18= '}'
            {
            // InternalAtsDsl.g:1626:2: ( (lv_name_0_0= ruleVERSION_DEF_REFERENCE ) )
            // InternalAtsDsl.g:1627:1: (lv_name_0_0= ruleVERSION_DEF_REFERENCE )
            {
            // InternalAtsDsl.g:1627:1: (lv_name_0_0= ruleVERSION_DEF_REFERENCE )
            // InternalAtsDsl.g:1628:3: lv_name_0_0= ruleVERSION_DEF_REFERENCE
            {
             
            	        newCompositeNode(grammarAccess.getVersionDefAccess().getNameVERSION_DEF_REFERENCEParserRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_43);
            lv_name_0_0=ruleVERSION_DEF_REFERENCE();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getVersionDefRule());
            	        }
                   		set(
                   			current, 
                   			"name",
                    		lv_name_0_0, 
                    		"org.eclipse.osee.ats.dsl.AtsDsl.VERSION_DEF_REFERENCE");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            otherlv_1=(Token)match(input,17,FOLLOW_55); 

                	newLeafNode(otherlv_1, grammarAccess.getVersionDefAccess().getLeftCurlyBracketKeyword_1());
                
            // InternalAtsDsl.g:1648:1: (otherlv_2= 'id' ( (lv_uuid_3_0= RULE_INT ) ) )?
            int alt52=2;
            int LA52_0 = input.LA(1);

            if ( (LA52_0==24) ) {
                alt52=1;
            }
            switch (alt52) {
                case 1 :
                    // InternalAtsDsl.g:1648:3: otherlv_2= 'id' ( (lv_uuid_3_0= RULE_INT ) )
                    {
                    otherlv_2=(Token)match(input,24,FOLLOW_24); 

                        	newLeafNode(otherlv_2, grammarAccess.getVersionDefAccess().getIdKeyword_2_0());
                        
                    // InternalAtsDsl.g:1652:1: ( (lv_uuid_3_0= RULE_INT ) )
                    // InternalAtsDsl.g:1653:1: (lv_uuid_3_0= RULE_INT )
                    {
                    // InternalAtsDsl.g:1653:1: (lv_uuid_3_0= RULE_INT )
                    // InternalAtsDsl.g:1654:3: lv_uuid_3_0= RULE_INT
                    {
                    lv_uuid_3_0=(Token)match(input,RULE_INT,FOLLOW_56); 

                    			newLeafNode(lv_uuid_3_0, grammarAccess.getVersionDefAccess().getUuidINTTerminalRuleCall_2_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getVersionDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"uuid",
                            		lv_uuid_3_0, 
                            		"org.eclipse.xtext.common.Terminals.INT");
                    	    

                    }


                    }


                    }
                    break;

            }

            // InternalAtsDsl.g:1670:4: (otherlv_4= 'staticId' ( (lv_staticId_5_0= RULE_STRING ) ) )*
            loop53:
            do {
                int alt53=2;
                int LA53_0 = input.LA(1);

                if ( (LA53_0==28) ) {
                    alt53=1;
                }


                switch (alt53) {
            	case 1 :
            	    // InternalAtsDsl.g:1670:6: otherlv_4= 'staticId' ( (lv_staticId_5_0= RULE_STRING ) )
            	    {
            	    otherlv_4=(Token)match(input,28,FOLLOW_3); 

            	        	newLeafNode(otherlv_4, grammarAccess.getVersionDefAccess().getStaticIdKeyword_3_0());
            	        
            	    // InternalAtsDsl.g:1674:1: ( (lv_staticId_5_0= RULE_STRING ) )
            	    // InternalAtsDsl.g:1675:1: (lv_staticId_5_0= RULE_STRING )
            	    {
            	    // InternalAtsDsl.g:1675:1: (lv_staticId_5_0= RULE_STRING )
            	    // InternalAtsDsl.g:1676:3: lv_staticId_5_0= RULE_STRING
            	    {
            	    lv_staticId_5_0=(Token)match(input,RULE_STRING,FOLLOW_56); 

            	    			newLeafNode(lv_staticId_5_0, grammarAccess.getVersionDefAccess().getStaticIdSTRINGTerminalRuleCall_3_1_0()); 
            	    		

            	    	        if (current==null) {
            	    	            current = createModelElement(grammarAccess.getVersionDefRule());
            	    	        }
            	           		addWithLastConsumed(
            	           			current, 
            	           			"staticId",
            	            		lv_staticId_5_0, 
            	            		"org.eclipse.xtext.common.Terminals.STRING");
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop53;
                }
            } while (true);

            // InternalAtsDsl.g:1692:4: (otherlv_6= 'next' ( (lv_next_7_0= ruleBooleanDef ) ) )?
            int alt54=2;
            int LA54_0 = input.LA(1);

            if ( (LA54_0==40) ) {
                alt54=1;
            }
            switch (alt54) {
                case 1 :
                    // InternalAtsDsl.g:1692:6: otherlv_6= 'next' ( (lv_next_7_0= ruleBooleanDef ) )
                    {
                    otherlv_6=(Token)match(input,40,FOLLOW_13); 

                        	newLeafNode(otherlv_6, grammarAccess.getVersionDefAccess().getNextKeyword_4_0());
                        
                    // InternalAtsDsl.g:1696:1: ( (lv_next_7_0= ruleBooleanDef ) )
                    // InternalAtsDsl.g:1697:1: (lv_next_7_0= ruleBooleanDef )
                    {
                    // InternalAtsDsl.g:1697:1: (lv_next_7_0= ruleBooleanDef )
                    // InternalAtsDsl.g:1698:3: lv_next_7_0= ruleBooleanDef
                    {
                     
                    	        newCompositeNode(grammarAccess.getVersionDefAccess().getNextBooleanDefEnumRuleCall_4_1_0()); 
                    	    
                    pushFollow(FOLLOW_57);
                    lv_next_7_0=ruleBooleanDef();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getVersionDefRule());
                    	        }
                           		set(
                           			current, 
                           			"next",
                            		lv_next_7_0, 
                            		"org.eclipse.osee.ats.dsl.AtsDsl.BooleanDef");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }


                    }
                    break;

            }

            // InternalAtsDsl.g:1714:4: (otherlv_8= 'released' ( (lv_released_9_0= ruleBooleanDef ) ) )?
            int alt55=2;
            int LA55_0 = input.LA(1);

            if ( (LA55_0==41) ) {
                alt55=1;
            }
            switch (alt55) {
                case 1 :
                    // InternalAtsDsl.g:1714:6: otherlv_8= 'released' ( (lv_released_9_0= ruleBooleanDef ) )
                    {
                    otherlv_8=(Token)match(input,41,FOLLOW_13); 

                        	newLeafNode(otherlv_8, grammarAccess.getVersionDefAccess().getReleasedKeyword_5_0());
                        
                    // InternalAtsDsl.g:1718:1: ( (lv_released_9_0= ruleBooleanDef ) )
                    // InternalAtsDsl.g:1719:1: (lv_released_9_0= ruleBooleanDef )
                    {
                    // InternalAtsDsl.g:1719:1: (lv_released_9_0= ruleBooleanDef )
                    // InternalAtsDsl.g:1720:3: lv_released_9_0= ruleBooleanDef
                    {
                     
                    	        newCompositeNode(grammarAccess.getVersionDefAccess().getReleasedBooleanDefEnumRuleCall_5_1_0()); 
                    	    
                    pushFollow(FOLLOW_58);
                    lv_released_9_0=ruleBooleanDef();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getVersionDefRule());
                    	        }
                           		set(
                           			current, 
                           			"released",
                            		lv_released_9_0, 
                            		"org.eclipse.osee.ats.dsl.AtsDsl.BooleanDef");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }


                    }
                    break;

            }

            // InternalAtsDsl.g:1736:4: (otherlv_10= 'allowCreateBranch' ( (lv_allowCreateBranch_11_0= ruleBooleanDef ) ) )?
            int alt56=2;
            int LA56_0 = input.LA(1);

            if ( (LA56_0==42) ) {
                alt56=1;
            }
            switch (alt56) {
                case 1 :
                    // InternalAtsDsl.g:1736:6: otherlv_10= 'allowCreateBranch' ( (lv_allowCreateBranch_11_0= ruleBooleanDef ) )
                    {
                    otherlv_10=(Token)match(input,42,FOLLOW_13); 

                        	newLeafNode(otherlv_10, grammarAccess.getVersionDefAccess().getAllowCreateBranchKeyword_6_0());
                        
                    // InternalAtsDsl.g:1740:1: ( (lv_allowCreateBranch_11_0= ruleBooleanDef ) )
                    // InternalAtsDsl.g:1741:1: (lv_allowCreateBranch_11_0= ruleBooleanDef )
                    {
                    // InternalAtsDsl.g:1741:1: (lv_allowCreateBranch_11_0= ruleBooleanDef )
                    // InternalAtsDsl.g:1742:3: lv_allowCreateBranch_11_0= ruleBooleanDef
                    {
                     
                    	        newCompositeNode(grammarAccess.getVersionDefAccess().getAllowCreateBranchBooleanDefEnumRuleCall_6_1_0()); 
                    	    
                    pushFollow(FOLLOW_59);
                    lv_allowCreateBranch_11_0=ruleBooleanDef();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getVersionDefRule());
                    	        }
                           		set(
                           			current, 
                           			"allowCreateBranch",
                            		lv_allowCreateBranch_11_0, 
                            		"org.eclipse.osee.ats.dsl.AtsDsl.BooleanDef");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }


                    }
                    break;

            }

            // InternalAtsDsl.g:1758:4: (otherlv_12= 'allowCommitBranch' ( (lv_allowCommitBranch_13_0= ruleBooleanDef ) ) )?
            int alt57=2;
            int LA57_0 = input.LA(1);

            if ( (LA57_0==43) ) {
                alt57=1;
            }
            switch (alt57) {
                case 1 :
                    // InternalAtsDsl.g:1758:6: otherlv_12= 'allowCommitBranch' ( (lv_allowCommitBranch_13_0= ruleBooleanDef ) )
                    {
                    otherlv_12=(Token)match(input,43,FOLLOW_13); 

                        	newLeafNode(otherlv_12, grammarAccess.getVersionDefAccess().getAllowCommitBranchKeyword_7_0());
                        
                    // InternalAtsDsl.g:1762:1: ( (lv_allowCommitBranch_13_0= ruleBooleanDef ) )
                    // InternalAtsDsl.g:1763:1: (lv_allowCommitBranch_13_0= ruleBooleanDef )
                    {
                    // InternalAtsDsl.g:1763:1: (lv_allowCommitBranch_13_0= ruleBooleanDef )
                    // InternalAtsDsl.g:1764:3: lv_allowCommitBranch_13_0= ruleBooleanDef
                    {
                     
                    	        newCompositeNode(grammarAccess.getVersionDefAccess().getAllowCommitBranchBooleanDefEnumRuleCall_7_1_0()); 
                    	    
                    pushFollow(FOLLOW_60);
                    lv_allowCommitBranch_13_0=ruleBooleanDef();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getVersionDefRule());
                    	        }
                           		set(
                           			current, 
                           			"allowCommitBranch",
                            		lv_allowCommitBranch_13_0, 
                            		"org.eclipse.osee.ats.dsl.AtsDsl.BooleanDef");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }


                    }
                    break;

            }

            // InternalAtsDsl.g:1780:4: (otherlv_14= 'baselineBranchUuid' ( (lv_baselineBranchUuid_15_0= RULE_STRING ) ) )?
            int alt58=2;
            int LA58_0 = input.LA(1);

            if ( (LA58_0==44) ) {
                alt58=1;
            }
            switch (alt58) {
                case 1 :
                    // InternalAtsDsl.g:1780:6: otherlv_14= 'baselineBranchUuid' ( (lv_baselineBranchUuid_15_0= RULE_STRING ) )
                    {
                    otherlv_14=(Token)match(input,44,FOLLOW_3); 

                        	newLeafNode(otherlv_14, grammarAccess.getVersionDefAccess().getBaselineBranchUuidKeyword_8_0());
                        
                    // InternalAtsDsl.g:1784:1: ( (lv_baselineBranchUuid_15_0= RULE_STRING ) )
                    // InternalAtsDsl.g:1785:1: (lv_baselineBranchUuid_15_0= RULE_STRING )
                    {
                    // InternalAtsDsl.g:1785:1: (lv_baselineBranchUuid_15_0= RULE_STRING )
                    // InternalAtsDsl.g:1786:3: lv_baselineBranchUuid_15_0= RULE_STRING
                    {
                    lv_baselineBranchUuid_15_0=(Token)match(input,RULE_STRING,FOLLOW_61); 

                    			newLeafNode(lv_baselineBranchUuid_15_0, grammarAccess.getVersionDefAccess().getBaselineBranchUuidSTRINGTerminalRuleCall_8_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getVersionDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"baselineBranchUuid",
                            		lv_baselineBranchUuid_15_0, 
                            		"org.eclipse.xtext.common.Terminals.STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            // InternalAtsDsl.g:1802:4: (otherlv_16= 'parallelVersion' ( (lv_parallelVersion_17_0= RULE_STRING ) ) )*
            loop59:
            do {
                int alt59=2;
                int LA59_0 = input.LA(1);

                if ( (LA59_0==45) ) {
                    alt59=1;
                }


                switch (alt59) {
            	case 1 :
            	    // InternalAtsDsl.g:1802:6: otherlv_16= 'parallelVersion' ( (lv_parallelVersion_17_0= RULE_STRING ) )
            	    {
            	    otherlv_16=(Token)match(input,45,FOLLOW_3); 

            	        	newLeafNode(otherlv_16, grammarAccess.getVersionDefAccess().getParallelVersionKeyword_9_0());
            	        
            	    // InternalAtsDsl.g:1806:1: ( (lv_parallelVersion_17_0= RULE_STRING ) )
            	    // InternalAtsDsl.g:1807:1: (lv_parallelVersion_17_0= RULE_STRING )
            	    {
            	    // InternalAtsDsl.g:1807:1: (lv_parallelVersion_17_0= RULE_STRING )
            	    // InternalAtsDsl.g:1808:3: lv_parallelVersion_17_0= RULE_STRING
            	    {
            	    lv_parallelVersion_17_0=(Token)match(input,RULE_STRING,FOLLOW_61); 

            	    			newLeafNode(lv_parallelVersion_17_0, grammarAccess.getVersionDefAccess().getParallelVersionSTRINGTerminalRuleCall_9_1_0()); 
            	    		

            	    	        if (current==null) {
            	    	            current = createModelElement(grammarAccess.getVersionDefRule());
            	    	        }
            	           		addWithLastConsumed(
            	           			current, 
            	           			"parallelVersion",
            	            		lv_parallelVersion_17_0, 
            	            		"org.eclipse.xtext.common.Terminals.STRING");
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop59;
                }
            } while (true);

            otherlv_18=(Token)match(input,22,FOLLOW_2); 

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
    // InternalAtsDsl.g:1836:1: entryRuleWorkDef returns [EObject current=null] : iv_ruleWorkDef= ruleWorkDef EOF ;
    public final EObject entryRuleWorkDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleWorkDef = null;


        try {
            // InternalAtsDsl.g:1837:2: (iv_ruleWorkDef= ruleWorkDef EOF )
            // InternalAtsDsl.g:1838:2: iv_ruleWorkDef= ruleWorkDef EOF
            {
             newCompositeNode(grammarAccess.getWorkDefRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleWorkDef=ruleWorkDef();

            state._fsp--;

             current =iv_ruleWorkDef; 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:1845:1: ruleWorkDef returns [EObject current=null] : ( ( (lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'id' ( (lv_id_3_0= RULE_STRING ) ) )+ (otherlv_4= 'startState' ( ( ruleSTATE_NAME_REFERENCE ) ) ) ( (lv_widgetDefs_6_0= ruleWidgetDef ) )* ( (lv_decisionReviewDefs_7_0= ruleDecisionReviewDef ) )* ( (lv_peerReviewDefs_8_0= rulePeerReviewDef ) )* ( (lv_states_9_0= ruleStateDef ) )+ otherlv_10= '}' ) ;
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
            // InternalAtsDsl.g:1848:28: ( ( ( (lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'id' ( (lv_id_3_0= RULE_STRING ) ) )+ (otherlv_4= 'startState' ( ( ruleSTATE_NAME_REFERENCE ) ) ) ( (lv_widgetDefs_6_0= ruleWidgetDef ) )* ( (lv_decisionReviewDefs_7_0= ruleDecisionReviewDef ) )* ( (lv_peerReviewDefs_8_0= rulePeerReviewDef ) )* ( (lv_states_9_0= ruleStateDef ) )+ otherlv_10= '}' ) )
            // InternalAtsDsl.g:1849:1: ( ( (lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'id' ( (lv_id_3_0= RULE_STRING ) ) )+ (otherlv_4= 'startState' ( ( ruleSTATE_NAME_REFERENCE ) ) ) ( (lv_widgetDefs_6_0= ruleWidgetDef ) )* ( (lv_decisionReviewDefs_7_0= ruleDecisionReviewDef ) )* ( (lv_peerReviewDefs_8_0= rulePeerReviewDef ) )* ( (lv_states_9_0= ruleStateDef ) )+ otherlv_10= '}' )
            {
            // InternalAtsDsl.g:1849:1: ( ( (lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'id' ( (lv_id_3_0= RULE_STRING ) ) )+ (otherlv_4= 'startState' ( ( ruleSTATE_NAME_REFERENCE ) ) ) ( (lv_widgetDefs_6_0= ruleWidgetDef ) )* ( (lv_decisionReviewDefs_7_0= ruleDecisionReviewDef ) )* ( (lv_peerReviewDefs_8_0= rulePeerReviewDef ) )* ( (lv_states_9_0= ruleStateDef ) )+ otherlv_10= '}' )
            // InternalAtsDsl.g:1849:2: ( (lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE ) ) otherlv_1= '{' (otherlv_2= 'id' ( (lv_id_3_0= RULE_STRING ) ) )+ (otherlv_4= 'startState' ( ( ruleSTATE_NAME_REFERENCE ) ) ) ( (lv_widgetDefs_6_0= ruleWidgetDef ) )* ( (lv_decisionReviewDefs_7_0= ruleDecisionReviewDef ) )* ( (lv_peerReviewDefs_8_0= rulePeerReviewDef ) )* ( (lv_states_9_0= ruleStateDef ) )+ otherlv_10= '}'
            {
            // InternalAtsDsl.g:1849:2: ( (lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE ) )
            // InternalAtsDsl.g:1850:1: (lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE )
            {
            // InternalAtsDsl.g:1850:1: (lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE )
            // InternalAtsDsl.g:1851:3: lv_name_0_0= ruleWORK_DEFINITION_NAME_REFERENCE
            {
             
            	        newCompositeNode(grammarAccess.getWorkDefAccess().getNameWORK_DEFINITION_NAME_REFERENCEParserRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_43);
            lv_name_0_0=ruleWORK_DEFINITION_NAME_REFERENCE();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getWorkDefRule());
            	        }
                   		set(
                   			current, 
                   			"name",
                    		lv_name_0_0, 
                    		"org.eclipse.osee.ats.dsl.AtsDsl.WORK_DEFINITION_NAME_REFERENCE");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            otherlv_1=(Token)match(input,17,FOLLOW_62); 

                	newLeafNode(otherlv_1, grammarAccess.getWorkDefAccess().getLeftCurlyBracketKeyword_1());
                
            // InternalAtsDsl.g:1871:1: (otherlv_2= 'id' ( (lv_id_3_0= RULE_STRING ) ) )+
            int cnt60=0;
            loop60:
            do {
                int alt60=2;
                int LA60_0 = input.LA(1);

                if ( (LA60_0==24) ) {
                    alt60=1;
                }


                switch (alt60) {
            	case 1 :
            	    // InternalAtsDsl.g:1871:3: otherlv_2= 'id' ( (lv_id_3_0= RULE_STRING ) )
            	    {
            	    otherlv_2=(Token)match(input,24,FOLLOW_3); 

            	        	newLeafNode(otherlv_2, grammarAccess.getWorkDefAccess().getIdKeyword_2_0());
            	        
            	    // InternalAtsDsl.g:1875:1: ( (lv_id_3_0= RULE_STRING ) )
            	    // InternalAtsDsl.g:1876:1: (lv_id_3_0= RULE_STRING )
            	    {
            	    // InternalAtsDsl.g:1876:1: (lv_id_3_0= RULE_STRING )
            	    // InternalAtsDsl.g:1877:3: lv_id_3_0= RULE_STRING
            	    {
            	    lv_id_3_0=(Token)match(input,RULE_STRING,FOLLOW_63); 

            	    			newLeafNode(lv_id_3_0, grammarAccess.getWorkDefAccess().getIdSTRINGTerminalRuleCall_2_1_0()); 
            	    		

            	    	        if (current==null) {
            	    	            current = createModelElement(grammarAccess.getWorkDefRule());
            	    	        }
            	           		addWithLastConsumed(
            	           			current, 
            	           			"id",
            	            		lv_id_3_0, 
            	            		"org.eclipse.xtext.common.Terminals.STRING");
            	    	    

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

            // InternalAtsDsl.g:1893:4: (otherlv_4= 'startState' ( ( ruleSTATE_NAME_REFERENCE ) ) )
            // InternalAtsDsl.g:1893:6: otherlv_4= 'startState' ( ( ruleSTATE_NAME_REFERENCE ) )
            {
            otherlv_4=(Token)match(input,46,FOLLOW_3); 

                	newLeafNode(otherlv_4, grammarAccess.getWorkDefAccess().getStartStateKeyword_3_0());
                
            // InternalAtsDsl.g:1897:1: ( ( ruleSTATE_NAME_REFERENCE ) )
            // InternalAtsDsl.g:1898:1: ( ruleSTATE_NAME_REFERENCE )
            {
            // InternalAtsDsl.g:1898:1: ( ruleSTATE_NAME_REFERENCE )
            // InternalAtsDsl.g:1899:3: ruleSTATE_NAME_REFERENCE
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getWorkDefRule());
            	        }
                    
             
            	        newCompositeNode(grammarAccess.getWorkDefAccess().getStartStateStateDefCrossReference_3_1_0()); 
            	    
            pushFollow(FOLLOW_64);
            ruleSTATE_NAME_REFERENCE();

            state._fsp--;

             
            	        afterParserOrEnumRuleCall();
            	    

            }


            }


            }

            // InternalAtsDsl.g:1912:3: ( (lv_widgetDefs_6_0= ruleWidgetDef ) )*
            loop61:
            do {
                int alt61=2;
                int LA61_0 = input.LA(1);

                if ( (LA61_0==47) ) {
                    alt61=1;
                }


                switch (alt61) {
            	case 1 :
            	    // InternalAtsDsl.g:1913:1: (lv_widgetDefs_6_0= ruleWidgetDef )
            	    {
            	    // InternalAtsDsl.g:1913:1: (lv_widgetDefs_6_0= ruleWidgetDef )
            	    // InternalAtsDsl.g:1914:3: lv_widgetDefs_6_0= ruleWidgetDef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getWorkDefAccess().getWidgetDefsWidgetDefParserRuleCall_4_0()); 
            	    	    
            	    pushFollow(FOLLOW_64);
            	    lv_widgetDefs_6_0=ruleWidgetDef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getWorkDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"widgetDefs",
            	            		lv_widgetDefs_6_0, 
            	            		"org.eclipse.osee.ats.dsl.AtsDsl.WidgetDef");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    break loop61;
                }
            } while (true);

            // InternalAtsDsl.g:1930:3: ( (lv_decisionReviewDefs_7_0= ruleDecisionReviewDef ) )*
            loop62:
            do {
                int alt62=2;
                int LA62_0 = input.LA(1);

                if ( (LA62_0==66) ) {
                    alt62=1;
                }


                switch (alt62) {
            	case 1 :
            	    // InternalAtsDsl.g:1931:1: (lv_decisionReviewDefs_7_0= ruleDecisionReviewDef )
            	    {
            	    // InternalAtsDsl.g:1931:1: (lv_decisionReviewDefs_7_0= ruleDecisionReviewDef )
            	    // InternalAtsDsl.g:1932:3: lv_decisionReviewDefs_7_0= ruleDecisionReviewDef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getWorkDefAccess().getDecisionReviewDefsDecisionReviewDefParserRuleCall_5_0()); 
            	    	    
            	    pushFollow(FOLLOW_64);
            	    lv_decisionReviewDefs_7_0=ruleDecisionReviewDef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getWorkDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"decisionReviewDefs",
            	            		lv_decisionReviewDefs_7_0, 
            	            		"org.eclipse.osee.ats.dsl.AtsDsl.DecisionReviewDef");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    break loop62;
                }
            } while (true);

            // InternalAtsDsl.g:1948:3: ( (lv_peerReviewDefs_8_0= rulePeerReviewDef ) )*
            loop63:
            do {
                int alt63=2;
                int LA63_0 = input.LA(1);

                if ( (LA63_0==74) ) {
                    alt63=1;
                }


                switch (alt63) {
            	case 1 :
            	    // InternalAtsDsl.g:1949:1: (lv_peerReviewDefs_8_0= rulePeerReviewDef )
            	    {
            	    // InternalAtsDsl.g:1949:1: (lv_peerReviewDefs_8_0= rulePeerReviewDef )
            	    // InternalAtsDsl.g:1950:3: lv_peerReviewDefs_8_0= rulePeerReviewDef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getWorkDefAccess().getPeerReviewDefsPeerReviewDefParserRuleCall_6_0()); 
            	    	    
            	    pushFollow(FOLLOW_64);
            	    lv_peerReviewDefs_8_0=rulePeerReviewDef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getWorkDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"peerReviewDefs",
            	            		lv_peerReviewDefs_8_0, 
            	            		"org.eclipse.osee.ats.dsl.AtsDsl.PeerReviewDef");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    break loop63;
                }
            } while (true);

            // InternalAtsDsl.g:1966:3: ( (lv_states_9_0= ruleStateDef ) )+
            int cnt64=0;
            loop64:
            do {
                int alt64=2;
                int LA64_0 = input.LA(1);

                if ( (LA64_0==59) ) {
                    alt64=1;
                }


                switch (alt64) {
            	case 1 :
            	    // InternalAtsDsl.g:1967:1: (lv_states_9_0= ruleStateDef )
            	    {
            	    // InternalAtsDsl.g:1967:1: (lv_states_9_0= ruleStateDef )
            	    // InternalAtsDsl.g:1968:3: lv_states_9_0= ruleStateDef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getWorkDefAccess().getStatesStateDefParserRuleCall_7_0()); 
            	    	    
            	    pushFollow(FOLLOW_65);
            	    lv_states_9_0=ruleStateDef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getWorkDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"states",
            	            		lv_states_9_0, 
            	            		"org.eclipse.osee.ats.dsl.AtsDsl.StateDef");
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

            otherlv_10=(Token)match(input,22,FOLLOW_2); 

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
    // InternalAtsDsl.g:1996:1: entryRuleWidgetDef returns [EObject current=null] : iv_ruleWidgetDef= ruleWidgetDef EOF ;
    public final EObject entryRuleWidgetDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleWidgetDef = null;


        try {
            // InternalAtsDsl.g:1997:2: (iv_ruleWidgetDef= ruleWidgetDef EOF )
            // InternalAtsDsl.g:1998:2: iv_ruleWidgetDef= ruleWidgetDef EOF
            {
             newCompositeNode(grammarAccess.getWidgetDefRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleWidgetDef=ruleWidgetDef();

            state._fsp--;

             current =iv_ruleWidgetDef; 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:2005:1: ruleWidgetDef returns [EObject current=null] : (otherlv_0= 'widgetDefinition' ( (lv_name_1_0= ruleWIDGET_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'attributeName' ( (lv_attributeName_4_0= RULE_STRING ) ) )? (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'xWidgetName' ( (lv_xWidgetName_8_0= RULE_STRING ) ) )? (otherlv_9= 'defaultValue' ( (lv_defaultValue_10_0= RULE_STRING ) ) )? (otherlv_11= 'height' ( (lv_height_12_0= RULE_INT ) ) )? (otherlv_13= 'option' ( (lv_option_14_0= ruleWidgetOption ) ) )* (otherlv_15= 'minConstraint' ( (lv_minConstraint_16_0= RULE_STRING ) ) )? (otherlv_17= 'maxConstraint' ( (lv_maxConstraint_18_0= RULE_STRING ) ) )? otherlv_19= '}' ) ;
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
            // InternalAtsDsl.g:2008:28: ( (otherlv_0= 'widgetDefinition' ( (lv_name_1_0= ruleWIDGET_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'attributeName' ( (lv_attributeName_4_0= RULE_STRING ) ) )? (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'xWidgetName' ( (lv_xWidgetName_8_0= RULE_STRING ) ) )? (otherlv_9= 'defaultValue' ( (lv_defaultValue_10_0= RULE_STRING ) ) )? (otherlv_11= 'height' ( (lv_height_12_0= RULE_INT ) ) )? (otherlv_13= 'option' ( (lv_option_14_0= ruleWidgetOption ) ) )* (otherlv_15= 'minConstraint' ( (lv_minConstraint_16_0= RULE_STRING ) ) )? (otherlv_17= 'maxConstraint' ( (lv_maxConstraint_18_0= RULE_STRING ) ) )? otherlv_19= '}' ) )
            // InternalAtsDsl.g:2009:1: (otherlv_0= 'widgetDefinition' ( (lv_name_1_0= ruleWIDGET_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'attributeName' ( (lv_attributeName_4_0= RULE_STRING ) ) )? (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'xWidgetName' ( (lv_xWidgetName_8_0= RULE_STRING ) ) )? (otherlv_9= 'defaultValue' ( (lv_defaultValue_10_0= RULE_STRING ) ) )? (otherlv_11= 'height' ( (lv_height_12_0= RULE_INT ) ) )? (otherlv_13= 'option' ( (lv_option_14_0= ruleWidgetOption ) ) )* (otherlv_15= 'minConstraint' ( (lv_minConstraint_16_0= RULE_STRING ) ) )? (otherlv_17= 'maxConstraint' ( (lv_maxConstraint_18_0= RULE_STRING ) ) )? otherlv_19= '}' )
            {
            // InternalAtsDsl.g:2009:1: (otherlv_0= 'widgetDefinition' ( (lv_name_1_0= ruleWIDGET_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'attributeName' ( (lv_attributeName_4_0= RULE_STRING ) ) )? (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'xWidgetName' ( (lv_xWidgetName_8_0= RULE_STRING ) ) )? (otherlv_9= 'defaultValue' ( (lv_defaultValue_10_0= RULE_STRING ) ) )? (otherlv_11= 'height' ( (lv_height_12_0= RULE_INT ) ) )? (otherlv_13= 'option' ( (lv_option_14_0= ruleWidgetOption ) ) )* (otherlv_15= 'minConstraint' ( (lv_minConstraint_16_0= RULE_STRING ) ) )? (otherlv_17= 'maxConstraint' ( (lv_maxConstraint_18_0= RULE_STRING ) ) )? otherlv_19= '}' )
            // InternalAtsDsl.g:2009:3: otherlv_0= 'widgetDefinition' ( (lv_name_1_0= ruleWIDGET_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'attributeName' ( (lv_attributeName_4_0= RULE_STRING ) ) )? (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'xWidgetName' ( (lv_xWidgetName_8_0= RULE_STRING ) ) )? (otherlv_9= 'defaultValue' ( (lv_defaultValue_10_0= RULE_STRING ) ) )? (otherlv_11= 'height' ( (lv_height_12_0= RULE_INT ) ) )? (otherlv_13= 'option' ( (lv_option_14_0= ruleWidgetOption ) ) )* (otherlv_15= 'minConstraint' ( (lv_minConstraint_16_0= RULE_STRING ) ) )? (otherlv_17= 'maxConstraint' ( (lv_maxConstraint_18_0= RULE_STRING ) ) )? otherlv_19= '}'
            {
            otherlv_0=(Token)match(input,47,FOLLOW_3); 

                	newLeafNode(otherlv_0, grammarAccess.getWidgetDefAccess().getWidgetDefinitionKeyword_0());
                
            // InternalAtsDsl.g:2013:1: ( (lv_name_1_0= ruleWIDGET_NAME_REFERENCE ) )
            // InternalAtsDsl.g:2014:1: (lv_name_1_0= ruleWIDGET_NAME_REFERENCE )
            {
            // InternalAtsDsl.g:2014:1: (lv_name_1_0= ruleWIDGET_NAME_REFERENCE )
            // InternalAtsDsl.g:2015:3: lv_name_1_0= ruleWIDGET_NAME_REFERENCE
            {
             
            	        newCompositeNode(grammarAccess.getWidgetDefAccess().getNameWIDGET_NAME_REFERENCEParserRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_43);
            lv_name_1_0=ruleWIDGET_NAME_REFERENCE();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getWidgetDefRule());
            	        }
                   		set(
                   			current, 
                   			"name",
                    		lv_name_1_0, 
                    		"org.eclipse.osee.ats.dsl.AtsDsl.WIDGET_NAME_REFERENCE");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            otherlv_2=(Token)match(input,17,FOLLOW_66); 

                	newLeafNode(otherlv_2, grammarAccess.getWidgetDefAccess().getLeftCurlyBracketKeyword_2());
                
            // InternalAtsDsl.g:2035:1: (otherlv_3= 'attributeName' ( (lv_attributeName_4_0= RULE_STRING ) ) )?
            int alt65=2;
            int LA65_0 = input.LA(1);

            if ( (LA65_0==48) ) {
                alt65=1;
            }
            switch (alt65) {
                case 1 :
                    // InternalAtsDsl.g:2035:3: otherlv_3= 'attributeName' ( (lv_attributeName_4_0= RULE_STRING ) )
                    {
                    otherlv_3=(Token)match(input,48,FOLLOW_3); 

                        	newLeafNode(otherlv_3, grammarAccess.getWidgetDefAccess().getAttributeNameKeyword_3_0());
                        
                    // InternalAtsDsl.g:2039:1: ( (lv_attributeName_4_0= RULE_STRING ) )
                    // InternalAtsDsl.g:2040:1: (lv_attributeName_4_0= RULE_STRING )
                    {
                    // InternalAtsDsl.g:2040:1: (lv_attributeName_4_0= RULE_STRING )
                    // InternalAtsDsl.g:2041:3: lv_attributeName_4_0= RULE_STRING
                    {
                    lv_attributeName_4_0=(Token)match(input,RULE_STRING,FOLLOW_67); 

                    			newLeafNode(lv_attributeName_4_0, grammarAccess.getWidgetDefAccess().getAttributeNameSTRINGTerminalRuleCall_3_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getWidgetDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"attributeName",
                            		lv_attributeName_4_0, 
                            		"org.eclipse.xtext.common.Terminals.STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            // InternalAtsDsl.g:2057:4: (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )?
            int alt66=2;
            int LA66_0 = input.LA(1);

            if ( (LA66_0==49) ) {
                alt66=1;
            }
            switch (alt66) {
                case 1 :
                    // InternalAtsDsl.g:2057:6: otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) )
                    {
                    otherlv_5=(Token)match(input,49,FOLLOW_3); 

                        	newLeafNode(otherlv_5, grammarAccess.getWidgetDefAccess().getDescriptionKeyword_4_0());
                        
                    // InternalAtsDsl.g:2061:1: ( (lv_description_6_0= RULE_STRING ) )
                    // InternalAtsDsl.g:2062:1: (lv_description_6_0= RULE_STRING )
                    {
                    // InternalAtsDsl.g:2062:1: (lv_description_6_0= RULE_STRING )
                    // InternalAtsDsl.g:2063:3: lv_description_6_0= RULE_STRING
                    {
                    lv_description_6_0=(Token)match(input,RULE_STRING,FOLLOW_68); 

                    			newLeafNode(lv_description_6_0, grammarAccess.getWidgetDefAccess().getDescriptionSTRINGTerminalRuleCall_4_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getWidgetDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"description",
                            		lv_description_6_0, 
                            		"org.eclipse.xtext.common.Terminals.STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            // InternalAtsDsl.g:2079:4: (otherlv_7= 'xWidgetName' ( (lv_xWidgetName_8_0= RULE_STRING ) ) )?
            int alt67=2;
            int LA67_0 = input.LA(1);

            if ( (LA67_0==50) ) {
                alt67=1;
            }
            switch (alt67) {
                case 1 :
                    // InternalAtsDsl.g:2079:6: otherlv_7= 'xWidgetName' ( (lv_xWidgetName_8_0= RULE_STRING ) )
                    {
                    otherlv_7=(Token)match(input,50,FOLLOW_3); 

                        	newLeafNode(otherlv_7, grammarAccess.getWidgetDefAccess().getXWidgetNameKeyword_5_0());
                        
                    // InternalAtsDsl.g:2083:1: ( (lv_xWidgetName_8_0= RULE_STRING ) )
                    // InternalAtsDsl.g:2084:1: (lv_xWidgetName_8_0= RULE_STRING )
                    {
                    // InternalAtsDsl.g:2084:1: (lv_xWidgetName_8_0= RULE_STRING )
                    // InternalAtsDsl.g:2085:3: lv_xWidgetName_8_0= RULE_STRING
                    {
                    lv_xWidgetName_8_0=(Token)match(input,RULE_STRING,FOLLOW_69); 

                    			newLeafNode(lv_xWidgetName_8_0, grammarAccess.getWidgetDefAccess().getXWidgetNameSTRINGTerminalRuleCall_5_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getWidgetDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"xWidgetName",
                            		lv_xWidgetName_8_0, 
                            		"org.eclipse.xtext.common.Terminals.STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            // InternalAtsDsl.g:2101:4: (otherlv_9= 'defaultValue' ( (lv_defaultValue_10_0= RULE_STRING ) ) )?
            int alt68=2;
            int LA68_0 = input.LA(1);

            if ( (LA68_0==51) ) {
                alt68=1;
            }
            switch (alt68) {
                case 1 :
                    // InternalAtsDsl.g:2101:6: otherlv_9= 'defaultValue' ( (lv_defaultValue_10_0= RULE_STRING ) )
                    {
                    otherlv_9=(Token)match(input,51,FOLLOW_3); 

                        	newLeafNode(otherlv_9, grammarAccess.getWidgetDefAccess().getDefaultValueKeyword_6_0());
                        
                    // InternalAtsDsl.g:2105:1: ( (lv_defaultValue_10_0= RULE_STRING ) )
                    // InternalAtsDsl.g:2106:1: (lv_defaultValue_10_0= RULE_STRING )
                    {
                    // InternalAtsDsl.g:2106:1: (lv_defaultValue_10_0= RULE_STRING )
                    // InternalAtsDsl.g:2107:3: lv_defaultValue_10_0= RULE_STRING
                    {
                    lv_defaultValue_10_0=(Token)match(input,RULE_STRING,FOLLOW_70); 

                    			newLeafNode(lv_defaultValue_10_0, grammarAccess.getWidgetDefAccess().getDefaultValueSTRINGTerminalRuleCall_6_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getWidgetDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"defaultValue",
                            		lv_defaultValue_10_0, 
                            		"org.eclipse.xtext.common.Terminals.STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            // InternalAtsDsl.g:2123:4: (otherlv_11= 'height' ( (lv_height_12_0= RULE_INT ) ) )?
            int alt69=2;
            int LA69_0 = input.LA(1);

            if ( (LA69_0==52) ) {
                alt69=1;
            }
            switch (alt69) {
                case 1 :
                    // InternalAtsDsl.g:2123:6: otherlv_11= 'height' ( (lv_height_12_0= RULE_INT ) )
                    {
                    otherlv_11=(Token)match(input,52,FOLLOW_24); 

                        	newLeafNode(otherlv_11, grammarAccess.getWidgetDefAccess().getHeightKeyword_7_0());
                        
                    // InternalAtsDsl.g:2127:1: ( (lv_height_12_0= RULE_INT ) )
                    // InternalAtsDsl.g:2128:1: (lv_height_12_0= RULE_INT )
                    {
                    // InternalAtsDsl.g:2128:1: (lv_height_12_0= RULE_INT )
                    // InternalAtsDsl.g:2129:3: lv_height_12_0= RULE_INT
                    {
                    lv_height_12_0=(Token)match(input,RULE_INT,FOLLOW_71); 

                    			newLeafNode(lv_height_12_0, grammarAccess.getWidgetDefAccess().getHeightINTTerminalRuleCall_7_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getWidgetDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"height",
                            		lv_height_12_0, 
                            		"org.eclipse.xtext.common.Terminals.INT");
                    	    

                    }


                    }


                    }
                    break;

            }

            // InternalAtsDsl.g:2145:4: (otherlv_13= 'option' ( (lv_option_14_0= ruleWidgetOption ) ) )*
            loop70:
            do {
                int alt70=2;
                int LA70_0 = input.LA(1);

                if ( (LA70_0==53) ) {
                    alt70=1;
                }


                switch (alt70) {
            	case 1 :
            	    // InternalAtsDsl.g:2145:6: otherlv_13= 'option' ( (lv_option_14_0= ruleWidgetOption ) )
            	    {
            	    otherlv_13=(Token)match(input,53,FOLLOW_72); 

            	        	newLeafNode(otherlv_13, grammarAccess.getWidgetDefAccess().getOptionKeyword_8_0());
            	        
            	    // InternalAtsDsl.g:2149:1: ( (lv_option_14_0= ruleWidgetOption ) )
            	    // InternalAtsDsl.g:2150:1: (lv_option_14_0= ruleWidgetOption )
            	    {
            	    // InternalAtsDsl.g:2150:1: (lv_option_14_0= ruleWidgetOption )
            	    // InternalAtsDsl.g:2151:3: lv_option_14_0= ruleWidgetOption
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getWidgetDefAccess().getOptionWidgetOptionParserRuleCall_8_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_71);
            	    lv_option_14_0=ruleWidgetOption();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getWidgetDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"option",
            	            		lv_option_14_0, 
            	            		"org.eclipse.osee.ats.dsl.AtsDsl.WidgetOption");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop70;
                }
            } while (true);

            // InternalAtsDsl.g:2167:4: (otherlv_15= 'minConstraint' ( (lv_minConstraint_16_0= RULE_STRING ) ) )?
            int alt71=2;
            int LA71_0 = input.LA(1);

            if ( (LA71_0==54) ) {
                alt71=1;
            }
            switch (alt71) {
                case 1 :
                    // InternalAtsDsl.g:2167:6: otherlv_15= 'minConstraint' ( (lv_minConstraint_16_0= RULE_STRING ) )
                    {
                    otherlv_15=(Token)match(input,54,FOLLOW_3); 

                        	newLeafNode(otherlv_15, grammarAccess.getWidgetDefAccess().getMinConstraintKeyword_9_0());
                        
                    // InternalAtsDsl.g:2171:1: ( (lv_minConstraint_16_0= RULE_STRING ) )
                    // InternalAtsDsl.g:2172:1: (lv_minConstraint_16_0= RULE_STRING )
                    {
                    // InternalAtsDsl.g:2172:1: (lv_minConstraint_16_0= RULE_STRING )
                    // InternalAtsDsl.g:2173:3: lv_minConstraint_16_0= RULE_STRING
                    {
                    lv_minConstraint_16_0=(Token)match(input,RULE_STRING,FOLLOW_73); 

                    			newLeafNode(lv_minConstraint_16_0, grammarAccess.getWidgetDefAccess().getMinConstraintSTRINGTerminalRuleCall_9_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getWidgetDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"minConstraint",
                            		lv_minConstraint_16_0, 
                            		"org.eclipse.xtext.common.Terminals.STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            // InternalAtsDsl.g:2189:4: (otherlv_17= 'maxConstraint' ( (lv_maxConstraint_18_0= RULE_STRING ) ) )?
            int alt72=2;
            int LA72_0 = input.LA(1);

            if ( (LA72_0==55) ) {
                alt72=1;
            }
            switch (alt72) {
                case 1 :
                    // InternalAtsDsl.g:2189:6: otherlv_17= 'maxConstraint' ( (lv_maxConstraint_18_0= RULE_STRING ) )
                    {
                    otherlv_17=(Token)match(input,55,FOLLOW_3); 

                        	newLeafNode(otherlv_17, grammarAccess.getWidgetDefAccess().getMaxConstraintKeyword_10_0());
                        
                    // InternalAtsDsl.g:2193:1: ( (lv_maxConstraint_18_0= RULE_STRING ) )
                    // InternalAtsDsl.g:2194:1: (lv_maxConstraint_18_0= RULE_STRING )
                    {
                    // InternalAtsDsl.g:2194:1: (lv_maxConstraint_18_0= RULE_STRING )
                    // InternalAtsDsl.g:2195:3: lv_maxConstraint_18_0= RULE_STRING
                    {
                    lv_maxConstraint_18_0=(Token)match(input,RULE_STRING,FOLLOW_17); 

                    			newLeafNode(lv_maxConstraint_18_0, grammarAccess.getWidgetDefAccess().getMaxConstraintSTRINGTerminalRuleCall_10_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getWidgetDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"maxConstraint",
                            		lv_maxConstraint_18_0, 
                            		"org.eclipse.xtext.common.Terminals.STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            otherlv_19=(Token)match(input,22,FOLLOW_2); 

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
    // InternalAtsDsl.g:2223:1: entryRuleWidgetRef returns [EObject current=null] : iv_ruleWidgetRef= ruleWidgetRef EOF ;
    public final EObject entryRuleWidgetRef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleWidgetRef = null;


        try {
            // InternalAtsDsl.g:2224:2: (iv_ruleWidgetRef= ruleWidgetRef EOF )
            // InternalAtsDsl.g:2225:2: iv_ruleWidgetRef= ruleWidgetRef EOF
            {
             newCompositeNode(grammarAccess.getWidgetRefRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleWidgetRef=ruleWidgetRef();

            state._fsp--;

             current =iv_ruleWidgetRef; 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:2232:1: ruleWidgetRef returns [EObject current=null] : (otherlv_0= 'widget' ( ( ruleWIDGET_NAME_REFERENCE ) ) ) ;
    public final EObject ruleWidgetRef() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;

         enterRule(); 
            
        try {
            // InternalAtsDsl.g:2235:28: ( (otherlv_0= 'widget' ( ( ruleWIDGET_NAME_REFERENCE ) ) ) )
            // InternalAtsDsl.g:2236:1: (otherlv_0= 'widget' ( ( ruleWIDGET_NAME_REFERENCE ) ) )
            {
            // InternalAtsDsl.g:2236:1: (otherlv_0= 'widget' ( ( ruleWIDGET_NAME_REFERENCE ) ) )
            // InternalAtsDsl.g:2236:3: otherlv_0= 'widget' ( ( ruleWIDGET_NAME_REFERENCE ) )
            {
            otherlv_0=(Token)match(input,56,FOLLOW_3); 

                	newLeafNode(otherlv_0, grammarAccess.getWidgetRefAccess().getWidgetKeyword_0());
                
            // InternalAtsDsl.g:2240:1: ( ( ruleWIDGET_NAME_REFERENCE ) )
            // InternalAtsDsl.g:2241:1: ( ruleWIDGET_NAME_REFERENCE )
            {
            // InternalAtsDsl.g:2241:1: ( ruleWIDGET_NAME_REFERENCE )
            // InternalAtsDsl.g:2242:3: ruleWIDGET_NAME_REFERENCE
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getWidgetRefRule());
            	        }
                    
             
            	        newCompositeNode(grammarAccess.getWidgetRefAccess().getWidgetWidgetDefCrossReference_1_0()); 
            	    
            pushFollow(FOLLOW_2);
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
    // InternalAtsDsl.g:2263:1: entryRuleAttrWidget returns [EObject current=null] : iv_ruleAttrWidget= ruleAttrWidget EOF ;
    public final EObject entryRuleAttrWidget() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttrWidget = null;


        try {
            // InternalAtsDsl.g:2264:2: (iv_ruleAttrWidget= ruleAttrWidget EOF )
            // InternalAtsDsl.g:2265:2: iv_ruleAttrWidget= ruleAttrWidget EOF
            {
             newCompositeNode(grammarAccess.getAttrWidgetRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleAttrWidget=ruleAttrWidget();

            state._fsp--;

             current =iv_ruleAttrWidget; 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:2272:1: ruleAttrWidget returns [EObject current=null] : (otherlv_0= 'attributeWidget' ( (lv_attributeName_1_0= RULE_STRING ) ) (otherlv_2= 'with' ( (lv_option_3_0= ruleWidgetOption ) ) )* ) ;
    public final EObject ruleAttrWidget() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_attributeName_1_0=null;
        Token otherlv_2=null;
        AntlrDatatypeRuleToken lv_option_3_0 = null;


         enterRule(); 
            
        try {
            // InternalAtsDsl.g:2275:28: ( (otherlv_0= 'attributeWidget' ( (lv_attributeName_1_0= RULE_STRING ) ) (otherlv_2= 'with' ( (lv_option_3_0= ruleWidgetOption ) ) )* ) )
            // InternalAtsDsl.g:2276:1: (otherlv_0= 'attributeWidget' ( (lv_attributeName_1_0= RULE_STRING ) ) (otherlv_2= 'with' ( (lv_option_3_0= ruleWidgetOption ) ) )* )
            {
            // InternalAtsDsl.g:2276:1: (otherlv_0= 'attributeWidget' ( (lv_attributeName_1_0= RULE_STRING ) ) (otherlv_2= 'with' ( (lv_option_3_0= ruleWidgetOption ) ) )* )
            // InternalAtsDsl.g:2276:3: otherlv_0= 'attributeWidget' ( (lv_attributeName_1_0= RULE_STRING ) ) (otherlv_2= 'with' ( (lv_option_3_0= ruleWidgetOption ) ) )*
            {
            otherlv_0=(Token)match(input,57,FOLLOW_3); 

                	newLeafNode(otherlv_0, grammarAccess.getAttrWidgetAccess().getAttributeWidgetKeyword_0());
                
            // InternalAtsDsl.g:2280:1: ( (lv_attributeName_1_0= RULE_STRING ) )
            // InternalAtsDsl.g:2281:1: (lv_attributeName_1_0= RULE_STRING )
            {
            // InternalAtsDsl.g:2281:1: (lv_attributeName_1_0= RULE_STRING )
            // InternalAtsDsl.g:2282:3: lv_attributeName_1_0= RULE_STRING
            {
            lv_attributeName_1_0=(Token)match(input,RULE_STRING,FOLLOW_74); 

            			newLeafNode(lv_attributeName_1_0, grammarAccess.getAttrWidgetAccess().getAttributeNameSTRINGTerminalRuleCall_1_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getAttrWidgetRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"attributeName",
                    		lv_attributeName_1_0, 
                    		"org.eclipse.xtext.common.Terminals.STRING");
            	    

            }


            }

            // InternalAtsDsl.g:2298:2: (otherlv_2= 'with' ( (lv_option_3_0= ruleWidgetOption ) ) )*
            loop73:
            do {
                int alt73=2;
                int LA73_0 = input.LA(1);

                if ( (LA73_0==58) ) {
                    alt73=1;
                }


                switch (alt73) {
            	case 1 :
            	    // InternalAtsDsl.g:2298:4: otherlv_2= 'with' ( (lv_option_3_0= ruleWidgetOption ) )
            	    {
            	    otherlv_2=(Token)match(input,58,FOLLOW_72); 

            	        	newLeafNode(otherlv_2, grammarAccess.getAttrWidgetAccess().getWithKeyword_2_0());
            	        
            	    // InternalAtsDsl.g:2302:1: ( (lv_option_3_0= ruleWidgetOption ) )
            	    // InternalAtsDsl.g:2303:1: (lv_option_3_0= ruleWidgetOption )
            	    {
            	    // InternalAtsDsl.g:2303:1: (lv_option_3_0= ruleWidgetOption )
            	    // InternalAtsDsl.g:2304:3: lv_option_3_0= ruleWidgetOption
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getAttrWidgetAccess().getOptionWidgetOptionParserRuleCall_2_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_74);
            	    lv_option_3_0=ruleWidgetOption();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getAttrWidgetRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"option",
            	            		lv_option_3_0, 
            	            		"org.eclipse.osee.ats.dsl.AtsDsl.WidgetOption");
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
    // InternalAtsDsl.g:2328:1: entryRuleStateDef returns [EObject current=null] : iv_ruleStateDef= ruleStateDef EOF ;
    public final EObject entryRuleStateDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleStateDef = null;


        try {
            // InternalAtsDsl.g:2329:2: (iv_ruleStateDef= ruleStateDef EOF )
            // InternalAtsDsl.g:2330:2: iv_ruleStateDef= ruleStateDef EOF
            {
             newCompositeNode(grammarAccess.getStateDefRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleStateDef=ruleStateDef();

            state._fsp--;

             current =iv_ruleStateDef; 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:2337:1: ruleStateDef returns [EObject current=null] : (otherlv_0= 'state' ( (lv_name_1_0= ruleSTATE_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'description' ( (lv_description_4_0= RULE_STRING ) ) )? otherlv_5= 'type' ( (lv_pageType_6_0= rulePageType ) ) otherlv_7= 'ordinal' ( (lv_ordinal_8_0= RULE_INT ) ) ( (lv_transitionStates_9_0= ruleToState ) )* (otherlv_10= 'rule' ( (lv_rules_11_0= ruleRuleRef ) ) )* ( (lv_decisionReviews_12_0= ruleDecisionReviewRef ) )* ( (lv_peerReviews_13_0= rulePeerReviewRef ) )* (otherlv_14= 'percentWeight' ( (lv_percentWeight_15_0= RULE_INT ) ) )? (otherlv_16= 'recommendedPercentComplete' ( (lv_recommendedPercentComplete_17_0= RULE_INT ) ) )? (otherlv_18= 'color' ( (lv_color_19_0= ruleStateColor ) ) )? ( (lv_layout_20_0= ruleLayoutType ) )? otherlv_21= '}' ) ;
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
            // InternalAtsDsl.g:2340:28: ( (otherlv_0= 'state' ( (lv_name_1_0= ruleSTATE_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'description' ( (lv_description_4_0= RULE_STRING ) ) )? otherlv_5= 'type' ( (lv_pageType_6_0= rulePageType ) ) otherlv_7= 'ordinal' ( (lv_ordinal_8_0= RULE_INT ) ) ( (lv_transitionStates_9_0= ruleToState ) )* (otherlv_10= 'rule' ( (lv_rules_11_0= ruleRuleRef ) ) )* ( (lv_decisionReviews_12_0= ruleDecisionReviewRef ) )* ( (lv_peerReviews_13_0= rulePeerReviewRef ) )* (otherlv_14= 'percentWeight' ( (lv_percentWeight_15_0= RULE_INT ) ) )? (otherlv_16= 'recommendedPercentComplete' ( (lv_recommendedPercentComplete_17_0= RULE_INT ) ) )? (otherlv_18= 'color' ( (lv_color_19_0= ruleStateColor ) ) )? ( (lv_layout_20_0= ruleLayoutType ) )? otherlv_21= '}' ) )
            // InternalAtsDsl.g:2341:1: (otherlv_0= 'state' ( (lv_name_1_0= ruleSTATE_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'description' ( (lv_description_4_0= RULE_STRING ) ) )? otherlv_5= 'type' ( (lv_pageType_6_0= rulePageType ) ) otherlv_7= 'ordinal' ( (lv_ordinal_8_0= RULE_INT ) ) ( (lv_transitionStates_9_0= ruleToState ) )* (otherlv_10= 'rule' ( (lv_rules_11_0= ruleRuleRef ) ) )* ( (lv_decisionReviews_12_0= ruleDecisionReviewRef ) )* ( (lv_peerReviews_13_0= rulePeerReviewRef ) )* (otherlv_14= 'percentWeight' ( (lv_percentWeight_15_0= RULE_INT ) ) )? (otherlv_16= 'recommendedPercentComplete' ( (lv_recommendedPercentComplete_17_0= RULE_INT ) ) )? (otherlv_18= 'color' ( (lv_color_19_0= ruleStateColor ) ) )? ( (lv_layout_20_0= ruleLayoutType ) )? otherlv_21= '}' )
            {
            // InternalAtsDsl.g:2341:1: (otherlv_0= 'state' ( (lv_name_1_0= ruleSTATE_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'description' ( (lv_description_4_0= RULE_STRING ) ) )? otherlv_5= 'type' ( (lv_pageType_6_0= rulePageType ) ) otherlv_7= 'ordinal' ( (lv_ordinal_8_0= RULE_INT ) ) ( (lv_transitionStates_9_0= ruleToState ) )* (otherlv_10= 'rule' ( (lv_rules_11_0= ruleRuleRef ) ) )* ( (lv_decisionReviews_12_0= ruleDecisionReviewRef ) )* ( (lv_peerReviews_13_0= rulePeerReviewRef ) )* (otherlv_14= 'percentWeight' ( (lv_percentWeight_15_0= RULE_INT ) ) )? (otherlv_16= 'recommendedPercentComplete' ( (lv_recommendedPercentComplete_17_0= RULE_INT ) ) )? (otherlv_18= 'color' ( (lv_color_19_0= ruleStateColor ) ) )? ( (lv_layout_20_0= ruleLayoutType ) )? otherlv_21= '}' )
            // InternalAtsDsl.g:2341:3: otherlv_0= 'state' ( (lv_name_1_0= ruleSTATE_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'description' ( (lv_description_4_0= RULE_STRING ) ) )? otherlv_5= 'type' ( (lv_pageType_6_0= rulePageType ) ) otherlv_7= 'ordinal' ( (lv_ordinal_8_0= RULE_INT ) ) ( (lv_transitionStates_9_0= ruleToState ) )* (otherlv_10= 'rule' ( (lv_rules_11_0= ruleRuleRef ) ) )* ( (lv_decisionReviews_12_0= ruleDecisionReviewRef ) )* ( (lv_peerReviews_13_0= rulePeerReviewRef ) )* (otherlv_14= 'percentWeight' ( (lv_percentWeight_15_0= RULE_INT ) ) )? (otherlv_16= 'recommendedPercentComplete' ( (lv_recommendedPercentComplete_17_0= RULE_INT ) ) )? (otherlv_18= 'color' ( (lv_color_19_0= ruleStateColor ) ) )? ( (lv_layout_20_0= ruleLayoutType ) )? otherlv_21= '}'
            {
            otherlv_0=(Token)match(input,59,FOLLOW_3); 

                	newLeafNode(otherlv_0, grammarAccess.getStateDefAccess().getStateKeyword_0());
                
            // InternalAtsDsl.g:2345:1: ( (lv_name_1_0= ruleSTATE_NAME_REFERENCE ) )
            // InternalAtsDsl.g:2346:1: (lv_name_1_0= ruleSTATE_NAME_REFERENCE )
            {
            // InternalAtsDsl.g:2346:1: (lv_name_1_0= ruleSTATE_NAME_REFERENCE )
            // InternalAtsDsl.g:2347:3: lv_name_1_0= ruleSTATE_NAME_REFERENCE
            {
             
            	        newCompositeNode(grammarAccess.getStateDefAccess().getNameSTATE_NAME_REFERENCEParserRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_43);
            lv_name_1_0=ruleSTATE_NAME_REFERENCE();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getStateDefRule());
            	        }
                   		set(
                   			current, 
                   			"name",
                    		lv_name_1_0, 
                    		"org.eclipse.osee.ats.dsl.AtsDsl.STATE_NAME_REFERENCE");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            otherlv_2=(Token)match(input,17,FOLLOW_75); 

                	newLeafNode(otherlv_2, grammarAccess.getStateDefAccess().getLeftCurlyBracketKeyword_2());
                
            // InternalAtsDsl.g:2367:1: (otherlv_3= 'description' ( (lv_description_4_0= RULE_STRING ) ) )?
            int alt74=2;
            int LA74_0 = input.LA(1);

            if ( (LA74_0==49) ) {
                alt74=1;
            }
            switch (alt74) {
                case 1 :
                    // InternalAtsDsl.g:2367:3: otherlv_3= 'description' ( (lv_description_4_0= RULE_STRING ) )
                    {
                    otherlv_3=(Token)match(input,49,FOLLOW_3); 

                        	newLeafNode(otherlv_3, grammarAccess.getStateDefAccess().getDescriptionKeyword_3_0());
                        
                    // InternalAtsDsl.g:2371:1: ( (lv_description_4_0= RULE_STRING ) )
                    // InternalAtsDsl.g:2372:1: (lv_description_4_0= RULE_STRING )
                    {
                    // InternalAtsDsl.g:2372:1: (lv_description_4_0= RULE_STRING )
                    // InternalAtsDsl.g:2373:3: lv_description_4_0= RULE_STRING
                    {
                    lv_description_4_0=(Token)match(input,RULE_STRING,FOLLOW_76); 

                    			newLeafNode(lv_description_4_0, grammarAccess.getStateDefAccess().getDescriptionSTRINGTerminalRuleCall_3_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getStateDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"description",
                            		lv_description_4_0, 
                            		"org.eclipse.xtext.common.Terminals.STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            otherlv_5=(Token)match(input,60,FOLLOW_77); 

                	newLeafNode(otherlv_5, grammarAccess.getStateDefAccess().getTypeKeyword_4());
                
            // InternalAtsDsl.g:2393:1: ( (lv_pageType_6_0= rulePageType ) )
            // InternalAtsDsl.g:2394:1: (lv_pageType_6_0= rulePageType )
            {
            // InternalAtsDsl.g:2394:1: (lv_pageType_6_0= rulePageType )
            // InternalAtsDsl.g:2395:3: lv_pageType_6_0= rulePageType
            {
             
            	        newCompositeNode(grammarAccess.getStateDefAccess().getPageTypePageTypeParserRuleCall_5_0()); 
            	    
            pushFollow(FOLLOW_78);
            lv_pageType_6_0=rulePageType();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getStateDefRule());
            	        }
                   		set(
                   			current, 
                   			"pageType",
                    		lv_pageType_6_0, 
                    		"org.eclipse.osee.ats.dsl.AtsDsl.PageType");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            otherlv_7=(Token)match(input,61,FOLLOW_24); 

                	newLeafNode(otherlv_7, grammarAccess.getStateDefAccess().getOrdinalKeyword_6());
                
            // InternalAtsDsl.g:2415:1: ( (lv_ordinal_8_0= RULE_INT ) )
            // InternalAtsDsl.g:2416:1: (lv_ordinal_8_0= RULE_INT )
            {
            // InternalAtsDsl.g:2416:1: (lv_ordinal_8_0= RULE_INT )
            // InternalAtsDsl.g:2417:3: lv_ordinal_8_0= RULE_INT
            {
            lv_ordinal_8_0=(Token)match(input,RULE_INT,FOLLOW_79); 

            			newLeafNode(lv_ordinal_8_0, grammarAccess.getStateDefAccess().getOrdinalINTTerminalRuleCall_7_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getStateDefRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"ordinal",
                    		lv_ordinal_8_0, 
                    		"org.eclipse.xtext.common.Terminals.INT");
            	    

            }


            }

            // InternalAtsDsl.g:2433:2: ( (lv_transitionStates_9_0= ruleToState ) )*
            loop75:
            do {
                int alt75=2;
                int LA75_0 = input.LA(1);

                if ( (LA75_0==78) ) {
                    alt75=1;
                }


                switch (alt75) {
            	case 1 :
            	    // InternalAtsDsl.g:2434:1: (lv_transitionStates_9_0= ruleToState )
            	    {
            	    // InternalAtsDsl.g:2434:1: (lv_transitionStates_9_0= ruleToState )
            	    // InternalAtsDsl.g:2435:3: lv_transitionStates_9_0= ruleToState
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getStateDefAccess().getTransitionStatesToStateParserRuleCall_8_0()); 
            	    	    
            	    pushFollow(FOLLOW_79);
            	    lv_transitionStates_9_0=ruleToState();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getStateDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"transitionStates",
            	            		lv_transitionStates_9_0, 
            	            		"org.eclipse.osee.ats.dsl.AtsDsl.ToState");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    break loop75;
                }
            } while (true);

            // InternalAtsDsl.g:2451:3: (otherlv_10= 'rule' ( (lv_rules_11_0= ruleRuleRef ) ) )*
            loop76:
            do {
                int alt76=2;
                int LA76_0 = input.LA(1);

                if ( (LA76_0==16) ) {
                    alt76=1;
                }


                switch (alt76) {
            	case 1 :
            	    // InternalAtsDsl.g:2451:5: otherlv_10= 'rule' ( (lv_rules_11_0= ruleRuleRef ) )
            	    {
            	    otherlv_10=(Token)match(input,16,FOLLOW_41); 

            	        	newLeafNode(otherlv_10, grammarAccess.getStateDefAccess().getRuleKeyword_9_0());
            	        
            	    // InternalAtsDsl.g:2455:1: ( (lv_rules_11_0= ruleRuleRef ) )
            	    // InternalAtsDsl.g:2456:1: (lv_rules_11_0= ruleRuleRef )
            	    {
            	    // InternalAtsDsl.g:2456:1: (lv_rules_11_0= ruleRuleRef )
            	    // InternalAtsDsl.g:2457:3: lv_rules_11_0= ruleRuleRef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getStateDefAccess().getRulesRuleRefParserRuleCall_9_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_80);
            	    lv_rules_11_0=ruleRuleRef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getStateDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"rules",
            	            		lv_rules_11_0, 
            	            		"org.eclipse.osee.ats.dsl.AtsDsl.RuleRef");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop76;
                }
            } while (true);

            // InternalAtsDsl.g:2473:4: ( (lv_decisionReviews_12_0= ruleDecisionReviewRef ) )*
            loop77:
            do {
                int alt77=2;
                int LA77_0 = input.LA(1);

                if ( (LA77_0==65) ) {
                    alt77=1;
                }


                switch (alt77) {
            	case 1 :
            	    // InternalAtsDsl.g:2474:1: (lv_decisionReviews_12_0= ruleDecisionReviewRef )
            	    {
            	    // InternalAtsDsl.g:2474:1: (lv_decisionReviews_12_0= ruleDecisionReviewRef )
            	    // InternalAtsDsl.g:2475:3: lv_decisionReviews_12_0= ruleDecisionReviewRef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getStateDefAccess().getDecisionReviewsDecisionReviewRefParserRuleCall_10_0()); 
            	    	    
            	    pushFollow(FOLLOW_81);
            	    lv_decisionReviews_12_0=ruleDecisionReviewRef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getStateDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"decisionReviews",
            	            		lv_decisionReviews_12_0, 
            	            		"org.eclipse.osee.ats.dsl.AtsDsl.DecisionReviewRef");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    break loop77;
                }
            } while (true);

            // InternalAtsDsl.g:2491:3: ( (lv_peerReviews_13_0= rulePeerReviewRef ) )*
            loop78:
            do {
                int alt78=2;
                int LA78_0 = input.LA(1);

                if ( (LA78_0==73) ) {
                    alt78=1;
                }


                switch (alt78) {
            	case 1 :
            	    // InternalAtsDsl.g:2492:1: (lv_peerReviews_13_0= rulePeerReviewRef )
            	    {
            	    // InternalAtsDsl.g:2492:1: (lv_peerReviews_13_0= rulePeerReviewRef )
            	    // InternalAtsDsl.g:2493:3: lv_peerReviews_13_0= rulePeerReviewRef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getStateDefAccess().getPeerReviewsPeerReviewRefParserRuleCall_11_0()); 
            	    	    
            	    pushFollow(FOLLOW_82);
            	    lv_peerReviews_13_0=rulePeerReviewRef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getStateDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"peerReviews",
            	            		lv_peerReviews_13_0, 
            	            		"org.eclipse.osee.ats.dsl.AtsDsl.PeerReviewRef");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    break loop78;
                }
            } while (true);

            // InternalAtsDsl.g:2509:3: (otherlv_14= 'percentWeight' ( (lv_percentWeight_15_0= RULE_INT ) ) )?
            int alt79=2;
            int LA79_0 = input.LA(1);

            if ( (LA79_0==62) ) {
                alt79=1;
            }
            switch (alt79) {
                case 1 :
                    // InternalAtsDsl.g:2509:5: otherlv_14= 'percentWeight' ( (lv_percentWeight_15_0= RULE_INT ) )
                    {
                    otherlv_14=(Token)match(input,62,FOLLOW_24); 

                        	newLeafNode(otherlv_14, grammarAccess.getStateDefAccess().getPercentWeightKeyword_12_0());
                        
                    // InternalAtsDsl.g:2513:1: ( (lv_percentWeight_15_0= RULE_INT ) )
                    // InternalAtsDsl.g:2514:1: (lv_percentWeight_15_0= RULE_INT )
                    {
                    // InternalAtsDsl.g:2514:1: (lv_percentWeight_15_0= RULE_INT )
                    // InternalAtsDsl.g:2515:3: lv_percentWeight_15_0= RULE_INT
                    {
                    lv_percentWeight_15_0=(Token)match(input,RULE_INT,FOLLOW_83); 

                    			newLeafNode(lv_percentWeight_15_0, grammarAccess.getStateDefAccess().getPercentWeightINTTerminalRuleCall_12_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getStateDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"percentWeight",
                            		lv_percentWeight_15_0, 
                            		"org.eclipse.xtext.common.Terminals.INT");
                    	    

                    }


                    }


                    }
                    break;

            }

            // InternalAtsDsl.g:2531:4: (otherlv_16= 'recommendedPercentComplete' ( (lv_recommendedPercentComplete_17_0= RULE_INT ) ) )?
            int alt80=2;
            int LA80_0 = input.LA(1);

            if ( (LA80_0==63) ) {
                alt80=1;
            }
            switch (alt80) {
                case 1 :
                    // InternalAtsDsl.g:2531:6: otherlv_16= 'recommendedPercentComplete' ( (lv_recommendedPercentComplete_17_0= RULE_INT ) )
                    {
                    otherlv_16=(Token)match(input,63,FOLLOW_24); 

                        	newLeafNode(otherlv_16, grammarAccess.getStateDefAccess().getRecommendedPercentCompleteKeyword_13_0());
                        
                    // InternalAtsDsl.g:2535:1: ( (lv_recommendedPercentComplete_17_0= RULE_INT ) )
                    // InternalAtsDsl.g:2536:1: (lv_recommendedPercentComplete_17_0= RULE_INT )
                    {
                    // InternalAtsDsl.g:2536:1: (lv_recommendedPercentComplete_17_0= RULE_INT )
                    // InternalAtsDsl.g:2537:3: lv_recommendedPercentComplete_17_0= RULE_INT
                    {
                    lv_recommendedPercentComplete_17_0=(Token)match(input,RULE_INT,FOLLOW_84); 

                    			newLeafNode(lv_recommendedPercentComplete_17_0, grammarAccess.getStateDefAccess().getRecommendedPercentCompleteINTTerminalRuleCall_13_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getStateDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"recommendedPercentComplete",
                            		lv_recommendedPercentComplete_17_0, 
                            		"org.eclipse.xtext.common.Terminals.INT");
                    	    

                    }


                    }


                    }
                    break;

            }

            // InternalAtsDsl.g:2553:4: (otherlv_18= 'color' ( (lv_color_19_0= ruleStateColor ) ) )?
            int alt81=2;
            int LA81_0 = input.LA(1);

            if ( (LA81_0==64) ) {
                alt81=1;
            }
            switch (alt81) {
                case 1 :
                    // InternalAtsDsl.g:2553:6: otherlv_18= 'color' ( (lv_color_19_0= ruleStateColor ) )
                    {
                    otherlv_18=(Token)match(input,64,FOLLOW_85); 

                        	newLeafNode(otherlv_18, grammarAccess.getStateDefAccess().getColorKeyword_14_0());
                        
                    // InternalAtsDsl.g:2557:1: ( (lv_color_19_0= ruleStateColor ) )
                    // InternalAtsDsl.g:2558:1: (lv_color_19_0= ruleStateColor )
                    {
                    // InternalAtsDsl.g:2558:1: (lv_color_19_0= ruleStateColor )
                    // InternalAtsDsl.g:2559:3: lv_color_19_0= ruleStateColor
                    {
                     
                    	        newCompositeNode(grammarAccess.getStateDefAccess().getColorStateColorParserRuleCall_14_1_0()); 
                    	    
                    pushFollow(FOLLOW_86);
                    lv_color_19_0=ruleStateColor();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getStateDefRule());
                    	        }
                           		set(
                           			current, 
                           			"color",
                            		lv_color_19_0, 
                            		"org.eclipse.osee.ats.dsl.AtsDsl.StateColor");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }


                    }
                    break;

            }

            // InternalAtsDsl.g:2575:4: ( (lv_layout_20_0= ruleLayoutType ) )?
            int alt82=2;
            int LA82_0 = input.LA(1);

            if ( ((LA82_0>=79 && LA82_0<=80)) ) {
                alt82=1;
            }
            switch (alt82) {
                case 1 :
                    // InternalAtsDsl.g:2576:1: (lv_layout_20_0= ruleLayoutType )
                    {
                    // InternalAtsDsl.g:2576:1: (lv_layout_20_0= ruleLayoutType )
                    // InternalAtsDsl.g:2577:3: lv_layout_20_0= ruleLayoutType
                    {
                     
                    	        newCompositeNode(grammarAccess.getStateDefAccess().getLayoutLayoutTypeParserRuleCall_15_0()); 
                    	    
                    pushFollow(FOLLOW_17);
                    lv_layout_20_0=ruleLayoutType();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getStateDefRule());
                    	        }
                           		set(
                           			current, 
                           			"layout",
                            		lv_layout_20_0, 
                            		"org.eclipse.osee.ats.dsl.AtsDsl.LayoutType");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }
                    break;

            }

            otherlv_21=(Token)match(input,22,FOLLOW_2); 

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
    // InternalAtsDsl.g:2605:1: entryRuleDecisionReviewRef returns [EObject current=null] : iv_ruleDecisionReviewRef= ruleDecisionReviewRef EOF ;
    public final EObject entryRuleDecisionReviewRef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDecisionReviewRef = null;


        try {
            // InternalAtsDsl.g:2606:2: (iv_ruleDecisionReviewRef= ruleDecisionReviewRef EOF )
            // InternalAtsDsl.g:2607:2: iv_ruleDecisionReviewRef= ruleDecisionReviewRef EOF
            {
             newCompositeNode(grammarAccess.getDecisionReviewRefRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleDecisionReviewRef=ruleDecisionReviewRef();

            state._fsp--;

             current =iv_ruleDecisionReviewRef; 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:2614:1: ruleDecisionReviewRef returns [EObject current=null] : (otherlv_0= 'decisionReview' ( ( ruleDECISION_REVIEW_NAME_REFERENCE ) ) ) ;
    public final EObject ruleDecisionReviewRef() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;

         enterRule(); 
            
        try {
            // InternalAtsDsl.g:2617:28: ( (otherlv_0= 'decisionReview' ( ( ruleDECISION_REVIEW_NAME_REFERENCE ) ) ) )
            // InternalAtsDsl.g:2618:1: (otherlv_0= 'decisionReview' ( ( ruleDECISION_REVIEW_NAME_REFERENCE ) ) )
            {
            // InternalAtsDsl.g:2618:1: (otherlv_0= 'decisionReview' ( ( ruleDECISION_REVIEW_NAME_REFERENCE ) ) )
            // InternalAtsDsl.g:2618:3: otherlv_0= 'decisionReview' ( ( ruleDECISION_REVIEW_NAME_REFERENCE ) )
            {
            otherlv_0=(Token)match(input,65,FOLLOW_3); 

                	newLeafNode(otherlv_0, grammarAccess.getDecisionReviewRefAccess().getDecisionReviewKeyword_0());
                
            // InternalAtsDsl.g:2622:1: ( ( ruleDECISION_REVIEW_NAME_REFERENCE ) )
            // InternalAtsDsl.g:2623:1: ( ruleDECISION_REVIEW_NAME_REFERENCE )
            {
            // InternalAtsDsl.g:2623:1: ( ruleDECISION_REVIEW_NAME_REFERENCE )
            // InternalAtsDsl.g:2624:3: ruleDECISION_REVIEW_NAME_REFERENCE
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getDecisionReviewRefRule());
            	        }
                    
             
            	        newCompositeNode(grammarAccess.getDecisionReviewRefAccess().getDecisionReviewDecisionReviewDefCrossReference_1_0()); 
            	    
            pushFollow(FOLLOW_2);
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
    // InternalAtsDsl.g:2645:1: entryRuleDecisionReviewDef returns [EObject current=null] : iv_ruleDecisionReviewDef= ruleDecisionReviewDef EOF ;
    public final EObject entryRuleDecisionReviewDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDecisionReviewDef = null;


        try {
            // InternalAtsDsl.g:2646:2: (iv_ruleDecisionReviewDef= ruleDecisionReviewDef EOF )
            // InternalAtsDsl.g:2647:2: iv_ruleDecisionReviewDef= ruleDecisionReviewDef EOF
            {
             newCompositeNode(grammarAccess.getDecisionReviewDefRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleDecisionReviewDef=ruleDecisionReviewDef();

            state._fsp--;

             current =iv_ruleDecisionReviewDef; 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:2654:1: ruleDecisionReviewDef returns [EObject current=null] : (otherlv_0= 'decisionReviewDefinition' ( (lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_9= 'blockingType' ( (lv_blockingType_10_0= ruleReviewBlockingType ) ) otherlv_11= 'onEvent' ( (lv_stateEvent_12_0= ruleWorkflowEventType ) ) (otherlv_13= 'assignee' ( (lv_assigneeRefs_14_0= ruleUserRef ) ) )* (otherlv_15= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_16_0= ruleBooleanDef ) ) )? ( (lv_options_17_0= ruleDecisionReviewOpt ) )+ otherlv_18= '}' ) ;
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
            // InternalAtsDsl.g:2657:28: ( (otherlv_0= 'decisionReviewDefinition' ( (lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_9= 'blockingType' ( (lv_blockingType_10_0= ruleReviewBlockingType ) ) otherlv_11= 'onEvent' ( (lv_stateEvent_12_0= ruleWorkflowEventType ) ) (otherlv_13= 'assignee' ( (lv_assigneeRefs_14_0= ruleUserRef ) ) )* (otherlv_15= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_16_0= ruleBooleanDef ) ) )? ( (lv_options_17_0= ruleDecisionReviewOpt ) )+ otherlv_18= '}' ) )
            // InternalAtsDsl.g:2658:1: (otherlv_0= 'decisionReviewDefinition' ( (lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_9= 'blockingType' ( (lv_blockingType_10_0= ruleReviewBlockingType ) ) otherlv_11= 'onEvent' ( (lv_stateEvent_12_0= ruleWorkflowEventType ) ) (otherlv_13= 'assignee' ( (lv_assigneeRefs_14_0= ruleUserRef ) ) )* (otherlv_15= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_16_0= ruleBooleanDef ) ) )? ( (lv_options_17_0= ruleDecisionReviewOpt ) )+ otherlv_18= '}' )
            {
            // InternalAtsDsl.g:2658:1: (otherlv_0= 'decisionReviewDefinition' ( (lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_9= 'blockingType' ( (lv_blockingType_10_0= ruleReviewBlockingType ) ) otherlv_11= 'onEvent' ( (lv_stateEvent_12_0= ruleWorkflowEventType ) ) (otherlv_13= 'assignee' ( (lv_assigneeRefs_14_0= ruleUserRef ) ) )* (otherlv_15= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_16_0= ruleBooleanDef ) ) )? ( (lv_options_17_0= ruleDecisionReviewOpt ) )+ otherlv_18= '}' )
            // InternalAtsDsl.g:2658:3: otherlv_0= 'decisionReviewDefinition' ( (lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_9= 'blockingType' ( (lv_blockingType_10_0= ruleReviewBlockingType ) ) otherlv_11= 'onEvent' ( (lv_stateEvent_12_0= ruleWorkflowEventType ) ) (otherlv_13= 'assignee' ( (lv_assigneeRefs_14_0= ruleUserRef ) ) )* (otherlv_15= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_16_0= ruleBooleanDef ) ) )? ( (lv_options_17_0= ruleDecisionReviewOpt ) )+ otherlv_18= '}'
            {
            otherlv_0=(Token)match(input,66,FOLLOW_3); 

                	newLeafNode(otherlv_0, grammarAccess.getDecisionReviewDefAccess().getDecisionReviewDefinitionKeyword_0());
                
            // InternalAtsDsl.g:2662:1: ( (lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE ) )
            // InternalAtsDsl.g:2663:1: (lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE )
            {
            // InternalAtsDsl.g:2663:1: (lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE )
            // InternalAtsDsl.g:2664:3: lv_name_1_0= ruleDECISION_REVIEW_NAME_REFERENCE
            {
             
            	        newCompositeNode(grammarAccess.getDecisionReviewDefAccess().getNameDECISION_REVIEW_NAME_REFERENCEParserRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_43);
            lv_name_1_0=ruleDECISION_REVIEW_NAME_REFERENCE();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getDecisionReviewDefRule());
            	        }
                   		set(
                   			current, 
                   			"name",
                    		lv_name_1_0, 
                    		"org.eclipse.osee.ats.dsl.AtsDsl.DECISION_REVIEW_NAME_REFERENCE");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            otherlv_2=(Token)match(input,17,FOLLOW_87); 

                	newLeafNode(otherlv_2, grammarAccess.getDecisionReviewDefAccess().getLeftCurlyBracketKeyword_2());
                
            otherlv_3=(Token)match(input,67,FOLLOW_3); 

                	newLeafNode(otherlv_3, grammarAccess.getDecisionReviewDefAccess().getTitleKeyword_3());
                
            // InternalAtsDsl.g:2688:1: ( (lv_title_4_0= RULE_STRING ) )
            // InternalAtsDsl.g:2689:1: (lv_title_4_0= RULE_STRING )
            {
            // InternalAtsDsl.g:2689:1: (lv_title_4_0= RULE_STRING )
            // InternalAtsDsl.g:2690:3: lv_title_4_0= RULE_STRING
            {
            lv_title_4_0=(Token)match(input,RULE_STRING,FOLLOW_88); 

            			newLeafNode(lv_title_4_0, grammarAccess.getDecisionReviewDefAccess().getTitleSTRINGTerminalRuleCall_4_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getDecisionReviewDefRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"title",
                    		lv_title_4_0, 
                    		"org.eclipse.xtext.common.Terminals.STRING");
            	    

            }


            }

            otherlv_5=(Token)match(input,49,FOLLOW_3); 

                	newLeafNode(otherlv_5, grammarAccess.getDecisionReviewDefAccess().getDescriptionKeyword_5());
                
            // InternalAtsDsl.g:2710:1: ( (lv_description_6_0= RULE_STRING ) )
            // InternalAtsDsl.g:2711:1: (lv_description_6_0= RULE_STRING )
            {
            // InternalAtsDsl.g:2711:1: (lv_description_6_0= RULE_STRING )
            // InternalAtsDsl.g:2712:3: lv_description_6_0= RULE_STRING
            {
            lv_description_6_0=(Token)match(input,RULE_STRING,FOLLOW_89); 

            			newLeafNode(lv_description_6_0, grammarAccess.getDecisionReviewDefAccess().getDescriptionSTRINGTerminalRuleCall_6_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getDecisionReviewDefRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"description",
                    		lv_description_6_0, 
                    		"org.eclipse.xtext.common.Terminals.STRING");
            	    

            }


            }

            // InternalAtsDsl.g:2728:2: (otherlv_7= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )?
            int alt83=2;
            int LA83_0 = input.LA(1);

            if ( (LA83_0==68) ) {
                alt83=1;
            }
            switch (alt83) {
                case 1 :
                    // InternalAtsDsl.g:2728:4: otherlv_7= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) )
                    {
                    otherlv_7=(Token)match(input,68,FOLLOW_3); 

                        	newLeafNode(otherlv_7, grammarAccess.getDecisionReviewDefAccess().getRelatedToStateKeyword_7_0());
                        
                    // InternalAtsDsl.g:2732:1: ( ( ruleSTATE_NAME_REFERENCE ) )
                    // InternalAtsDsl.g:2733:1: ( ruleSTATE_NAME_REFERENCE )
                    {
                    // InternalAtsDsl.g:2733:1: ( ruleSTATE_NAME_REFERENCE )
                    // InternalAtsDsl.g:2734:3: ruleSTATE_NAME_REFERENCE
                    {

                    			if (current==null) {
                    	            current = createModelElement(grammarAccess.getDecisionReviewDefRule());
                    	        }
                            
                     
                    	        newCompositeNode(grammarAccess.getDecisionReviewDefAccess().getRelatedToStateStateDefCrossReference_7_1_0()); 
                    	    
                    pushFollow(FOLLOW_90);
                    ruleSTATE_NAME_REFERENCE();

                    state._fsp--;

                     
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }


                    }
                    break;

            }

            otherlv_9=(Token)match(input,69,FOLLOW_91); 

                	newLeafNode(otherlv_9, grammarAccess.getDecisionReviewDefAccess().getBlockingTypeKeyword_8());
                
            // InternalAtsDsl.g:2751:1: ( (lv_blockingType_10_0= ruleReviewBlockingType ) )
            // InternalAtsDsl.g:2752:1: (lv_blockingType_10_0= ruleReviewBlockingType )
            {
            // InternalAtsDsl.g:2752:1: (lv_blockingType_10_0= ruleReviewBlockingType )
            // InternalAtsDsl.g:2753:3: lv_blockingType_10_0= ruleReviewBlockingType
            {
             
            	        newCompositeNode(grammarAccess.getDecisionReviewDefAccess().getBlockingTypeReviewBlockingTypeEnumRuleCall_9_0()); 
            	    
            pushFollow(FOLLOW_92);
            lv_blockingType_10_0=ruleReviewBlockingType();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getDecisionReviewDefRule());
            	        }
                   		set(
                   			current, 
                   			"blockingType",
                    		lv_blockingType_10_0, 
                    		"org.eclipse.osee.ats.dsl.AtsDsl.ReviewBlockingType");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            otherlv_11=(Token)match(input,70,FOLLOW_93); 

                	newLeafNode(otherlv_11, grammarAccess.getDecisionReviewDefAccess().getOnEventKeyword_10());
                
            // InternalAtsDsl.g:2773:1: ( (lv_stateEvent_12_0= ruleWorkflowEventType ) )
            // InternalAtsDsl.g:2774:1: (lv_stateEvent_12_0= ruleWorkflowEventType )
            {
            // InternalAtsDsl.g:2774:1: (lv_stateEvent_12_0= ruleWorkflowEventType )
            // InternalAtsDsl.g:2775:3: lv_stateEvent_12_0= ruleWorkflowEventType
            {
             
            	        newCompositeNode(grammarAccess.getDecisionReviewDefAccess().getStateEventWorkflowEventTypeEnumRuleCall_11_0()); 
            	    
            pushFollow(FOLLOW_94);
            lv_stateEvent_12_0=ruleWorkflowEventType();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getDecisionReviewDefRule());
            	        }
                   		set(
                   			current, 
                   			"stateEvent",
                    		lv_stateEvent_12_0, 
                    		"org.eclipse.osee.ats.dsl.AtsDsl.WorkflowEventType");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // InternalAtsDsl.g:2791:2: (otherlv_13= 'assignee' ( (lv_assigneeRefs_14_0= ruleUserRef ) ) )*
            loop84:
            do {
                int alt84=2;
                int LA84_0 = input.LA(1);

                if ( (LA84_0==71) ) {
                    alt84=1;
                }


                switch (alt84) {
            	case 1 :
            	    // InternalAtsDsl.g:2791:4: otherlv_13= 'assignee' ( (lv_assigneeRefs_14_0= ruleUserRef ) )
            	    {
            	    otherlv_13=(Token)match(input,71,FOLLOW_33); 

            	        	newLeafNode(otherlv_13, grammarAccess.getDecisionReviewDefAccess().getAssigneeKeyword_12_0());
            	        
            	    // InternalAtsDsl.g:2795:1: ( (lv_assigneeRefs_14_0= ruleUserRef ) )
            	    // InternalAtsDsl.g:2796:1: (lv_assigneeRefs_14_0= ruleUserRef )
            	    {
            	    // InternalAtsDsl.g:2796:1: (lv_assigneeRefs_14_0= ruleUserRef )
            	    // InternalAtsDsl.g:2797:3: lv_assigneeRefs_14_0= ruleUserRef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getDecisionReviewDefAccess().getAssigneeRefsUserRefParserRuleCall_12_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_94);
            	    lv_assigneeRefs_14_0=ruleUserRef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getDecisionReviewDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"assigneeRefs",
            	            		lv_assigneeRefs_14_0, 
            	            		"org.eclipse.osee.ats.dsl.AtsDsl.UserRef");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop84;
                }
            } while (true);

            // InternalAtsDsl.g:2813:4: (otherlv_15= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_16_0= ruleBooleanDef ) ) )?
            int alt85=2;
            int LA85_0 = input.LA(1);

            if ( (LA85_0==72) ) {
                alt85=1;
            }
            switch (alt85) {
                case 1 :
                    // InternalAtsDsl.g:2813:6: otherlv_15= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_16_0= ruleBooleanDef ) )
                    {
                    otherlv_15=(Token)match(input,72,FOLLOW_13); 

                        	newLeafNode(otherlv_15, grammarAccess.getDecisionReviewDefAccess().getAutoTransitionToDecisionKeyword_13_0());
                        
                    // InternalAtsDsl.g:2817:1: ( (lv_autoTransitionToDecision_16_0= ruleBooleanDef ) )
                    // InternalAtsDsl.g:2818:1: (lv_autoTransitionToDecision_16_0= ruleBooleanDef )
                    {
                    // InternalAtsDsl.g:2818:1: (lv_autoTransitionToDecision_16_0= ruleBooleanDef )
                    // InternalAtsDsl.g:2819:3: lv_autoTransitionToDecision_16_0= ruleBooleanDef
                    {
                     
                    	        newCompositeNode(grammarAccess.getDecisionReviewDefAccess().getAutoTransitionToDecisionBooleanDefEnumRuleCall_13_1_0()); 
                    	    
                    pushFollow(FOLLOW_94);
                    lv_autoTransitionToDecision_16_0=ruleBooleanDef();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getDecisionReviewDefRule());
                    	        }
                           		set(
                           			current, 
                           			"autoTransitionToDecision",
                            		lv_autoTransitionToDecision_16_0, 
                            		"org.eclipse.osee.ats.dsl.AtsDsl.BooleanDef");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }


                    }
                    break;

            }

            // InternalAtsDsl.g:2835:4: ( (lv_options_17_0= ruleDecisionReviewOpt ) )+
            int cnt86=0;
            loop86:
            do {
                int alt86=2;
                int LA86_0 = input.LA(1);

                if ( (LA86_0==53) ) {
                    alt86=1;
                }


                switch (alt86) {
            	case 1 :
            	    // InternalAtsDsl.g:2836:1: (lv_options_17_0= ruleDecisionReviewOpt )
            	    {
            	    // InternalAtsDsl.g:2836:1: (lv_options_17_0= ruleDecisionReviewOpt )
            	    // InternalAtsDsl.g:2837:3: lv_options_17_0= ruleDecisionReviewOpt
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getDecisionReviewDefAccess().getOptionsDecisionReviewOptParserRuleCall_14_0()); 
            	    	    
            	    pushFollow(FOLLOW_95);
            	    lv_options_17_0=ruleDecisionReviewOpt();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getDecisionReviewDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"options",
            	            		lv_options_17_0, 
            	            		"org.eclipse.osee.ats.dsl.AtsDsl.DecisionReviewOpt");
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

            otherlv_18=(Token)match(input,22,FOLLOW_2); 

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
    // InternalAtsDsl.g:2865:1: entryRuleDECISION_REVIEW_OPT_REF returns [String current=null] : iv_ruleDECISION_REVIEW_OPT_REF= ruleDECISION_REVIEW_OPT_REF EOF ;
    public final String entryRuleDECISION_REVIEW_OPT_REF() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleDECISION_REVIEW_OPT_REF = null;


        try {
            // InternalAtsDsl.g:2866:2: (iv_ruleDECISION_REVIEW_OPT_REF= ruleDECISION_REVIEW_OPT_REF EOF )
            // InternalAtsDsl.g:2867:2: iv_ruleDECISION_REVIEW_OPT_REF= ruleDECISION_REVIEW_OPT_REF EOF
            {
             newCompositeNode(grammarAccess.getDECISION_REVIEW_OPT_REFRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleDECISION_REVIEW_OPT_REF=ruleDECISION_REVIEW_OPT_REF();

            state._fsp--;

             current =iv_ruleDECISION_REVIEW_OPT_REF.getText(); 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:2874:1: ruleDECISION_REVIEW_OPT_REF returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleDECISION_REVIEW_OPT_REF() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // InternalAtsDsl.g:2877:28: (this_STRING_0= RULE_STRING )
            // InternalAtsDsl.g:2878:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_2); 

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
    // InternalAtsDsl.g:2893:1: entryRuleDecisionReviewOpt returns [EObject current=null] : iv_ruleDecisionReviewOpt= ruleDecisionReviewOpt EOF ;
    public final EObject entryRuleDecisionReviewOpt() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDecisionReviewOpt = null;


        try {
            // InternalAtsDsl.g:2894:2: (iv_ruleDecisionReviewOpt= ruleDecisionReviewOpt EOF )
            // InternalAtsDsl.g:2895:2: iv_ruleDecisionReviewOpt= ruleDecisionReviewOpt EOF
            {
             newCompositeNode(grammarAccess.getDecisionReviewOptRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleDecisionReviewOpt=ruleDecisionReviewOpt();

            state._fsp--;

             current =iv_ruleDecisionReviewOpt; 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:2902:1: ruleDecisionReviewOpt returns [EObject current=null] : (otherlv_0= 'option' ( (lv_name_1_0= ruleDECISION_REVIEW_OPT_REF ) ) ( (lv_followup_2_0= ruleFollowupRef ) )? ) ;
    public final EObject ruleDecisionReviewOpt() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        AntlrDatatypeRuleToken lv_name_1_0 = null;

        EObject lv_followup_2_0 = null;


         enterRule(); 
            
        try {
            // InternalAtsDsl.g:2905:28: ( (otherlv_0= 'option' ( (lv_name_1_0= ruleDECISION_REVIEW_OPT_REF ) ) ( (lv_followup_2_0= ruleFollowupRef ) )? ) )
            // InternalAtsDsl.g:2906:1: (otherlv_0= 'option' ( (lv_name_1_0= ruleDECISION_REVIEW_OPT_REF ) ) ( (lv_followup_2_0= ruleFollowupRef ) )? )
            {
            // InternalAtsDsl.g:2906:1: (otherlv_0= 'option' ( (lv_name_1_0= ruleDECISION_REVIEW_OPT_REF ) ) ( (lv_followup_2_0= ruleFollowupRef ) )? )
            // InternalAtsDsl.g:2906:3: otherlv_0= 'option' ( (lv_name_1_0= ruleDECISION_REVIEW_OPT_REF ) ) ( (lv_followup_2_0= ruleFollowupRef ) )?
            {
            otherlv_0=(Token)match(input,53,FOLLOW_3); 

                	newLeafNode(otherlv_0, grammarAccess.getDecisionReviewOptAccess().getOptionKeyword_0());
                
            // InternalAtsDsl.g:2910:1: ( (lv_name_1_0= ruleDECISION_REVIEW_OPT_REF ) )
            // InternalAtsDsl.g:2911:1: (lv_name_1_0= ruleDECISION_REVIEW_OPT_REF )
            {
            // InternalAtsDsl.g:2911:1: (lv_name_1_0= ruleDECISION_REVIEW_OPT_REF )
            // InternalAtsDsl.g:2912:3: lv_name_1_0= ruleDECISION_REVIEW_OPT_REF
            {
             
            	        newCompositeNode(grammarAccess.getDecisionReviewOptAccess().getNameDECISION_REVIEW_OPT_REFParserRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_96);
            lv_name_1_0=ruleDECISION_REVIEW_OPT_REF();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getDecisionReviewOptRule());
            	        }
                   		set(
                   			current, 
                   			"name",
                    		lv_name_1_0, 
                    		"org.eclipse.osee.ats.dsl.AtsDsl.DECISION_REVIEW_OPT_REF");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // InternalAtsDsl.g:2928:2: ( (lv_followup_2_0= ruleFollowupRef ) )?
            int alt87=2;
            int LA87_0 = input.LA(1);

            if ( (LA87_0==76) ) {
                alt87=1;
            }
            switch (alt87) {
                case 1 :
                    // InternalAtsDsl.g:2929:1: (lv_followup_2_0= ruleFollowupRef )
                    {
                    // InternalAtsDsl.g:2929:1: (lv_followup_2_0= ruleFollowupRef )
                    // InternalAtsDsl.g:2930:3: lv_followup_2_0= ruleFollowupRef
                    {
                     
                    	        newCompositeNode(grammarAccess.getDecisionReviewOptAccess().getFollowupFollowupRefParserRuleCall_2_0()); 
                    	    
                    pushFollow(FOLLOW_2);
                    lv_followup_2_0=ruleFollowupRef();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getDecisionReviewOptRule());
                    	        }
                           		set(
                           			current, 
                           			"followup",
                            		lv_followup_2_0, 
                            		"org.eclipse.osee.ats.dsl.AtsDsl.FollowupRef");
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
    // InternalAtsDsl.g:2954:1: entryRulePeerReviewRef returns [EObject current=null] : iv_rulePeerReviewRef= rulePeerReviewRef EOF ;
    public final EObject entryRulePeerReviewRef() throws RecognitionException {
        EObject current = null;

        EObject iv_rulePeerReviewRef = null;


        try {
            // InternalAtsDsl.g:2955:2: (iv_rulePeerReviewRef= rulePeerReviewRef EOF )
            // InternalAtsDsl.g:2956:2: iv_rulePeerReviewRef= rulePeerReviewRef EOF
            {
             newCompositeNode(grammarAccess.getPeerReviewRefRule()); 
            pushFollow(FOLLOW_1);
            iv_rulePeerReviewRef=rulePeerReviewRef();

            state._fsp--;

             current =iv_rulePeerReviewRef; 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:2963:1: rulePeerReviewRef returns [EObject current=null] : (otherlv_0= 'peerReview' ( ( rulePEER_REVIEW_NAME_REFERENCE ) ) ) ;
    public final EObject rulePeerReviewRef() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;

         enterRule(); 
            
        try {
            // InternalAtsDsl.g:2966:28: ( (otherlv_0= 'peerReview' ( ( rulePEER_REVIEW_NAME_REFERENCE ) ) ) )
            // InternalAtsDsl.g:2967:1: (otherlv_0= 'peerReview' ( ( rulePEER_REVIEW_NAME_REFERENCE ) ) )
            {
            // InternalAtsDsl.g:2967:1: (otherlv_0= 'peerReview' ( ( rulePEER_REVIEW_NAME_REFERENCE ) ) )
            // InternalAtsDsl.g:2967:3: otherlv_0= 'peerReview' ( ( rulePEER_REVIEW_NAME_REFERENCE ) )
            {
            otherlv_0=(Token)match(input,73,FOLLOW_3); 

                	newLeafNode(otherlv_0, grammarAccess.getPeerReviewRefAccess().getPeerReviewKeyword_0());
                
            // InternalAtsDsl.g:2971:1: ( ( rulePEER_REVIEW_NAME_REFERENCE ) )
            // InternalAtsDsl.g:2972:1: ( rulePEER_REVIEW_NAME_REFERENCE )
            {
            // InternalAtsDsl.g:2972:1: ( rulePEER_REVIEW_NAME_REFERENCE )
            // InternalAtsDsl.g:2973:3: rulePEER_REVIEW_NAME_REFERENCE
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getPeerReviewRefRule());
            	        }
                    
             
            	        newCompositeNode(grammarAccess.getPeerReviewRefAccess().getPeerReviewPeerReviewDefCrossReference_1_0()); 
            	    
            pushFollow(FOLLOW_2);
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
    // InternalAtsDsl.g:2994:1: entryRulePeerReviewDef returns [EObject current=null] : iv_rulePeerReviewDef= rulePeerReviewDef EOF ;
    public final EObject entryRulePeerReviewDef() throws RecognitionException {
        EObject current = null;

        EObject iv_rulePeerReviewDef = null;


        try {
            // InternalAtsDsl.g:2995:2: (iv_rulePeerReviewDef= rulePeerReviewDef EOF )
            // InternalAtsDsl.g:2996:2: iv_rulePeerReviewDef= rulePeerReviewDef EOF
            {
             newCompositeNode(grammarAccess.getPeerReviewDefRule()); 
            pushFollow(FOLLOW_1);
            iv_rulePeerReviewDef=rulePeerReviewDef();

            state._fsp--;

             current =iv_rulePeerReviewDef; 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:3003:1: rulePeerReviewDef returns [EObject current=null] : (otherlv_0= 'peerReviewDefinition' ( (lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) )? otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'location' ( (lv_location_8_0= RULE_STRING ) ) )? (otherlv_9= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_11= 'blockingType' ( (lv_blockingType_12_0= ruleReviewBlockingType ) ) otherlv_13= 'onEvent' ( (lv_stateEvent_14_0= ruleWorkflowEventType ) ) (otherlv_15= 'assignee' ( (lv_assigneeRefs_16_0= ruleUserRef ) ) )* otherlv_17= '}' ) ;
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
            // InternalAtsDsl.g:3006:28: ( (otherlv_0= 'peerReviewDefinition' ( (lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) )? otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'location' ( (lv_location_8_0= RULE_STRING ) ) )? (otherlv_9= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_11= 'blockingType' ( (lv_blockingType_12_0= ruleReviewBlockingType ) ) otherlv_13= 'onEvent' ( (lv_stateEvent_14_0= ruleWorkflowEventType ) ) (otherlv_15= 'assignee' ( (lv_assigneeRefs_16_0= ruleUserRef ) ) )* otherlv_17= '}' ) )
            // InternalAtsDsl.g:3007:1: (otherlv_0= 'peerReviewDefinition' ( (lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) )? otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'location' ( (lv_location_8_0= RULE_STRING ) ) )? (otherlv_9= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_11= 'blockingType' ( (lv_blockingType_12_0= ruleReviewBlockingType ) ) otherlv_13= 'onEvent' ( (lv_stateEvent_14_0= ruleWorkflowEventType ) ) (otherlv_15= 'assignee' ( (lv_assigneeRefs_16_0= ruleUserRef ) ) )* otherlv_17= '}' )
            {
            // InternalAtsDsl.g:3007:1: (otherlv_0= 'peerReviewDefinition' ( (lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) )? otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'location' ( (lv_location_8_0= RULE_STRING ) ) )? (otherlv_9= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_11= 'blockingType' ( (lv_blockingType_12_0= ruleReviewBlockingType ) ) otherlv_13= 'onEvent' ( (lv_stateEvent_14_0= ruleWorkflowEventType ) ) (otherlv_15= 'assignee' ( (lv_assigneeRefs_16_0= ruleUserRef ) ) )* otherlv_17= '}' )
            // InternalAtsDsl.g:3007:3: otherlv_0= 'peerReviewDefinition' ( (lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) )? otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) (otherlv_7= 'location' ( (lv_location_8_0= RULE_STRING ) ) )? (otherlv_9= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )? otherlv_11= 'blockingType' ( (lv_blockingType_12_0= ruleReviewBlockingType ) ) otherlv_13= 'onEvent' ( (lv_stateEvent_14_0= ruleWorkflowEventType ) ) (otherlv_15= 'assignee' ( (lv_assigneeRefs_16_0= ruleUserRef ) ) )* otherlv_17= '}'
            {
            otherlv_0=(Token)match(input,74,FOLLOW_3); 

                	newLeafNode(otherlv_0, grammarAccess.getPeerReviewDefAccess().getPeerReviewDefinitionKeyword_0());
                
            // InternalAtsDsl.g:3011:1: ( (lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE ) )
            // InternalAtsDsl.g:3012:1: (lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE )
            {
            // InternalAtsDsl.g:3012:1: (lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE )
            // InternalAtsDsl.g:3013:3: lv_name_1_0= rulePEER_REVIEW_NAME_REFERENCE
            {
             
            	        newCompositeNode(grammarAccess.getPeerReviewDefAccess().getNamePEER_REVIEW_NAME_REFERENCEParserRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_43);
            lv_name_1_0=rulePEER_REVIEW_NAME_REFERENCE();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getPeerReviewDefRule());
            	        }
                   		set(
                   			current, 
                   			"name",
                    		lv_name_1_0, 
                    		"org.eclipse.osee.ats.dsl.AtsDsl.PEER_REVIEW_NAME_REFERENCE");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            otherlv_2=(Token)match(input,17,FOLLOW_97); 

                	newLeafNode(otherlv_2, grammarAccess.getPeerReviewDefAccess().getLeftCurlyBracketKeyword_2());
                
            // InternalAtsDsl.g:3033:1: (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) )?
            int alt88=2;
            int LA88_0 = input.LA(1);

            if ( (LA88_0==67) ) {
                alt88=1;
            }
            switch (alt88) {
                case 1 :
                    // InternalAtsDsl.g:3033:3: otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) )
                    {
                    otherlv_3=(Token)match(input,67,FOLLOW_3); 

                        	newLeafNode(otherlv_3, grammarAccess.getPeerReviewDefAccess().getTitleKeyword_3_0());
                        
                    // InternalAtsDsl.g:3037:1: ( (lv_title_4_0= RULE_STRING ) )
                    // InternalAtsDsl.g:3038:1: (lv_title_4_0= RULE_STRING )
                    {
                    // InternalAtsDsl.g:3038:1: (lv_title_4_0= RULE_STRING )
                    // InternalAtsDsl.g:3039:3: lv_title_4_0= RULE_STRING
                    {
                    lv_title_4_0=(Token)match(input,RULE_STRING,FOLLOW_88); 

                    			newLeafNode(lv_title_4_0, grammarAccess.getPeerReviewDefAccess().getTitleSTRINGTerminalRuleCall_3_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getPeerReviewDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"title",
                            		lv_title_4_0, 
                            		"org.eclipse.xtext.common.Terminals.STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            otherlv_5=(Token)match(input,49,FOLLOW_3); 

                	newLeafNode(otherlv_5, grammarAccess.getPeerReviewDefAccess().getDescriptionKeyword_4());
                
            // InternalAtsDsl.g:3059:1: ( (lv_description_6_0= RULE_STRING ) )
            // InternalAtsDsl.g:3060:1: (lv_description_6_0= RULE_STRING )
            {
            // InternalAtsDsl.g:3060:1: (lv_description_6_0= RULE_STRING )
            // InternalAtsDsl.g:3061:3: lv_description_6_0= RULE_STRING
            {
            lv_description_6_0=(Token)match(input,RULE_STRING,FOLLOW_98); 

            			newLeafNode(lv_description_6_0, grammarAccess.getPeerReviewDefAccess().getDescriptionSTRINGTerminalRuleCall_5_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getPeerReviewDefRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"description",
                    		lv_description_6_0, 
                    		"org.eclipse.xtext.common.Terminals.STRING");
            	    

            }


            }

            // InternalAtsDsl.g:3077:2: (otherlv_7= 'location' ( (lv_location_8_0= RULE_STRING ) ) )?
            int alt89=2;
            int LA89_0 = input.LA(1);

            if ( (LA89_0==75) ) {
                alt89=1;
            }
            switch (alt89) {
                case 1 :
                    // InternalAtsDsl.g:3077:4: otherlv_7= 'location' ( (lv_location_8_0= RULE_STRING ) )
                    {
                    otherlv_7=(Token)match(input,75,FOLLOW_3); 

                        	newLeafNode(otherlv_7, grammarAccess.getPeerReviewDefAccess().getLocationKeyword_6_0());
                        
                    // InternalAtsDsl.g:3081:1: ( (lv_location_8_0= RULE_STRING ) )
                    // InternalAtsDsl.g:3082:1: (lv_location_8_0= RULE_STRING )
                    {
                    // InternalAtsDsl.g:3082:1: (lv_location_8_0= RULE_STRING )
                    // InternalAtsDsl.g:3083:3: lv_location_8_0= RULE_STRING
                    {
                    lv_location_8_0=(Token)match(input,RULE_STRING,FOLLOW_89); 

                    			newLeafNode(lv_location_8_0, grammarAccess.getPeerReviewDefAccess().getLocationSTRINGTerminalRuleCall_6_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getPeerReviewDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"location",
                            		lv_location_8_0, 
                            		"org.eclipse.xtext.common.Terminals.STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            // InternalAtsDsl.g:3099:4: (otherlv_9= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) ) )?
            int alt90=2;
            int LA90_0 = input.LA(1);

            if ( (LA90_0==68) ) {
                alt90=1;
            }
            switch (alt90) {
                case 1 :
                    // InternalAtsDsl.g:3099:6: otherlv_9= 'relatedToState' ( ( ruleSTATE_NAME_REFERENCE ) )
                    {
                    otherlv_9=(Token)match(input,68,FOLLOW_3); 

                        	newLeafNode(otherlv_9, grammarAccess.getPeerReviewDefAccess().getRelatedToStateKeyword_7_0());
                        
                    // InternalAtsDsl.g:3103:1: ( ( ruleSTATE_NAME_REFERENCE ) )
                    // InternalAtsDsl.g:3104:1: ( ruleSTATE_NAME_REFERENCE )
                    {
                    // InternalAtsDsl.g:3104:1: ( ruleSTATE_NAME_REFERENCE )
                    // InternalAtsDsl.g:3105:3: ruleSTATE_NAME_REFERENCE
                    {

                    			if (current==null) {
                    	            current = createModelElement(grammarAccess.getPeerReviewDefRule());
                    	        }
                            
                     
                    	        newCompositeNode(grammarAccess.getPeerReviewDefAccess().getRelatedToStateStateDefCrossReference_7_1_0()); 
                    	    
                    pushFollow(FOLLOW_90);
                    ruleSTATE_NAME_REFERENCE();

                    state._fsp--;

                     
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }


                    }
                    break;

            }

            otherlv_11=(Token)match(input,69,FOLLOW_91); 

                	newLeafNode(otherlv_11, grammarAccess.getPeerReviewDefAccess().getBlockingTypeKeyword_8());
                
            // InternalAtsDsl.g:3122:1: ( (lv_blockingType_12_0= ruleReviewBlockingType ) )
            // InternalAtsDsl.g:3123:1: (lv_blockingType_12_0= ruleReviewBlockingType )
            {
            // InternalAtsDsl.g:3123:1: (lv_blockingType_12_0= ruleReviewBlockingType )
            // InternalAtsDsl.g:3124:3: lv_blockingType_12_0= ruleReviewBlockingType
            {
             
            	        newCompositeNode(grammarAccess.getPeerReviewDefAccess().getBlockingTypeReviewBlockingTypeEnumRuleCall_9_0()); 
            	    
            pushFollow(FOLLOW_92);
            lv_blockingType_12_0=ruleReviewBlockingType();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getPeerReviewDefRule());
            	        }
                   		set(
                   			current, 
                   			"blockingType",
                    		lv_blockingType_12_0, 
                    		"org.eclipse.osee.ats.dsl.AtsDsl.ReviewBlockingType");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            otherlv_13=(Token)match(input,70,FOLLOW_93); 

                	newLeafNode(otherlv_13, grammarAccess.getPeerReviewDefAccess().getOnEventKeyword_10());
                
            // InternalAtsDsl.g:3144:1: ( (lv_stateEvent_14_0= ruleWorkflowEventType ) )
            // InternalAtsDsl.g:3145:1: (lv_stateEvent_14_0= ruleWorkflowEventType )
            {
            // InternalAtsDsl.g:3145:1: (lv_stateEvent_14_0= ruleWorkflowEventType )
            // InternalAtsDsl.g:3146:3: lv_stateEvent_14_0= ruleWorkflowEventType
            {
             
            	        newCompositeNode(grammarAccess.getPeerReviewDefAccess().getStateEventWorkflowEventTypeEnumRuleCall_11_0()); 
            	    
            pushFollow(FOLLOW_99);
            lv_stateEvent_14_0=ruleWorkflowEventType();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getPeerReviewDefRule());
            	        }
                   		set(
                   			current, 
                   			"stateEvent",
                    		lv_stateEvent_14_0, 
                    		"org.eclipse.osee.ats.dsl.AtsDsl.WorkflowEventType");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // InternalAtsDsl.g:3162:2: (otherlv_15= 'assignee' ( (lv_assigneeRefs_16_0= ruleUserRef ) ) )*
            loop91:
            do {
                int alt91=2;
                int LA91_0 = input.LA(1);

                if ( (LA91_0==71) ) {
                    alt91=1;
                }


                switch (alt91) {
            	case 1 :
            	    // InternalAtsDsl.g:3162:4: otherlv_15= 'assignee' ( (lv_assigneeRefs_16_0= ruleUserRef ) )
            	    {
            	    otherlv_15=(Token)match(input,71,FOLLOW_33); 

            	        	newLeafNode(otherlv_15, grammarAccess.getPeerReviewDefAccess().getAssigneeKeyword_12_0());
            	        
            	    // InternalAtsDsl.g:3166:1: ( (lv_assigneeRefs_16_0= ruleUserRef ) )
            	    // InternalAtsDsl.g:3167:1: (lv_assigneeRefs_16_0= ruleUserRef )
            	    {
            	    // InternalAtsDsl.g:3167:1: (lv_assigneeRefs_16_0= ruleUserRef )
            	    // InternalAtsDsl.g:3168:3: lv_assigneeRefs_16_0= ruleUserRef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getPeerReviewDefAccess().getAssigneeRefsUserRefParserRuleCall_12_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_99);
            	    lv_assigneeRefs_16_0=ruleUserRef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getPeerReviewDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"assigneeRefs",
            	            		lv_assigneeRefs_16_0, 
            	            		"org.eclipse.osee.ats.dsl.AtsDsl.UserRef");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop91;
                }
            } while (true);

            otherlv_17=(Token)match(input,22,FOLLOW_2); 

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
    // InternalAtsDsl.g:3196:1: entryRuleFollowupRef returns [EObject current=null] : iv_ruleFollowupRef= ruleFollowupRef EOF ;
    public final EObject entryRuleFollowupRef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleFollowupRef = null;


        try {
            // InternalAtsDsl.g:3197:2: (iv_ruleFollowupRef= ruleFollowupRef EOF )
            // InternalAtsDsl.g:3198:2: iv_ruleFollowupRef= ruleFollowupRef EOF
            {
             newCompositeNode(grammarAccess.getFollowupRefRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleFollowupRef=ruleFollowupRef();

            state._fsp--;

             current =iv_ruleFollowupRef; 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:3205:1: ruleFollowupRef returns [EObject current=null] : (otherlv_0= 'followup by' (otherlv_1= 'assignee' ( (lv_assigneeRefs_2_0= ruleUserRef ) ) )+ ) ;
    public final EObject ruleFollowupRef() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;
        EObject lv_assigneeRefs_2_0 = null;


         enterRule(); 
            
        try {
            // InternalAtsDsl.g:3208:28: ( (otherlv_0= 'followup by' (otherlv_1= 'assignee' ( (lv_assigneeRefs_2_0= ruleUserRef ) ) )+ ) )
            // InternalAtsDsl.g:3209:1: (otherlv_0= 'followup by' (otherlv_1= 'assignee' ( (lv_assigneeRefs_2_0= ruleUserRef ) ) )+ )
            {
            // InternalAtsDsl.g:3209:1: (otherlv_0= 'followup by' (otherlv_1= 'assignee' ( (lv_assigneeRefs_2_0= ruleUserRef ) ) )+ )
            // InternalAtsDsl.g:3209:3: otherlv_0= 'followup by' (otherlv_1= 'assignee' ( (lv_assigneeRefs_2_0= ruleUserRef ) ) )+
            {
            otherlv_0=(Token)match(input,76,FOLLOW_100); 

                	newLeafNode(otherlv_0, grammarAccess.getFollowupRefAccess().getFollowupByKeyword_0());
                
            // InternalAtsDsl.g:3213:1: (otherlv_1= 'assignee' ( (lv_assigneeRefs_2_0= ruleUserRef ) ) )+
            int cnt92=0;
            loop92:
            do {
                int alt92=2;
                int LA92_0 = input.LA(1);

                if ( (LA92_0==71) ) {
                    alt92=1;
                }


                switch (alt92) {
            	case 1 :
            	    // InternalAtsDsl.g:3213:3: otherlv_1= 'assignee' ( (lv_assigneeRefs_2_0= ruleUserRef ) )
            	    {
            	    otherlv_1=(Token)match(input,71,FOLLOW_33); 

            	        	newLeafNode(otherlv_1, grammarAccess.getFollowupRefAccess().getAssigneeKeyword_1_0());
            	        
            	    // InternalAtsDsl.g:3217:1: ( (lv_assigneeRefs_2_0= ruleUserRef ) )
            	    // InternalAtsDsl.g:3218:1: (lv_assigneeRefs_2_0= ruleUserRef )
            	    {
            	    // InternalAtsDsl.g:3218:1: (lv_assigneeRefs_2_0= ruleUserRef )
            	    // InternalAtsDsl.g:3219:3: lv_assigneeRefs_2_0= ruleUserRef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getFollowupRefAccess().getAssigneeRefsUserRefParserRuleCall_1_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_101);
            	    lv_assigneeRefs_2_0=ruleUserRef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getFollowupRefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"assigneeRefs",
            	            		lv_assigneeRefs_2_0, 
            	            		"org.eclipse.osee.ats.dsl.AtsDsl.UserRef");
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
    // InternalAtsDsl.g:3243:1: entryRuleUserRef returns [EObject current=null] : iv_ruleUserRef= ruleUserRef EOF ;
    public final EObject entryRuleUserRef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleUserRef = null;


        try {
            // InternalAtsDsl.g:3244:2: (iv_ruleUserRef= ruleUserRef EOF )
            // InternalAtsDsl.g:3245:2: iv_ruleUserRef= ruleUserRef EOF
            {
             newCompositeNode(grammarAccess.getUserRefRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleUserRef=ruleUserRef();

            state._fsp--;

             current =iv_ruleUserRef; 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:3252:1: ruleUserRef returns [EObject current=null] : (this_UserByUserId_0= ruleUserByUserId | this_UserByName_1= ruleUserByName ) ;
    public final EObject ruleUserRef() throws RecognitionException {
        EObject current = null;

        EObject this_UserByUserId_0 = null;

        EObject this_UserByName_1 = null;


         enterRule(); 
            
        try {
            // InternalAtsDsl.g:3255:28: ( (this_UserByUserId_0= ruleUserByUserId | this_UserByName_1= ruleUserByName ) )
            // InternalAtsDsl.g:3256:1: (this_UserByUserId_0= ruleUserByUserId | this_UserByName_1= ruleUserByName )
            {
            // InternalAtsDsl.g:3256:1: (this_UserByUserId_0= ruleUserByUserId | this_UserByName_1= ruleUserByName )
            int alt93=2;
            int LA93_0 = input.LA(1);

            if ( (LA93_0==19) ) {
                alt93=1;
            }
            else if ( (LA93_0==77) ) {
                alt93=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 93, 0, input);

                throw nvae;
            }
            switch (alt93) {
                case 1 :
                    // InternalAtsDsl.g:3257:5: this_UserByUserId_0= ruleUserByUserId
                    {
                     
                            newCompositeNode(grammarAccess.getUserRefAccess().getUserByUserIdParserRuleCall_0()); 
                        
                    pushFollow(FOLLOW_2);
                    this_UserByUserId_0=ruleUserByUserId();

                    state._fsp--;

                     
                            current = this_UserByUserId_0; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // InternalAtsDsl.g:3267:5: this_UserByName_1= ruleUserByName
                    {
                     
                            newCompositeNode(grammarAccess.getUserRefAccess().getUserByNameParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_2);
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
    // InternalAtsDsl.g:3283:1: entryRuleUserByUserId returns [EObject current=null] : iv_ruleUserByUserId= ruleUserByUserId EOF ;
    public final EObject entryRuleUserByUserId() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleUserByUserId = null;


        try {
            // InternalAtsDsl.g:3284:2: (iv_ruleUserByUserId= ruleUserByUserId EOF )
            // InternalAtsDsl.g:3285:2: iv_ruleUserByUserId= ruleUserByUserId EOF
            {
             newCompositeNode(grammarAccess.getUserByUserIdRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleUserByUserId=ruleUserByUserId();

            state._fsp--;

             current =iv_ruleUserByUserId; 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:3292:1: ruleUserByUserId returns [EObject current=null] : (otherlv_0= 'userId' ( (lv_userId_1_0= RULE_STRING ) ) ) ;
    public final EObject ruleUserByUserId() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_userId_1_0=null;

         enterRule(); 
            
        try {
            // InternalAtsDsl.g:3295:28: ( (otherlv_0= 'userId' ( (lv_userId_1_0= RULE_STRING ) ) ) )
            // InternalAtsDsl.g:3296:1: (otherlv_0= 'userId' ( (lv_userId_1_0= RULE_STRING ) ) )
            {
            // InternalAtsDsl.g:3296:1: (otherlv_0= 'userId' ( (lv_userId_1_0= RULE_STRING ) ) )
            // InternalAtsDsl.g:3296:3: otherlv_0= 'userId' ( (lv_userId_1_0= RULE_STRING ) )
            {
            otherlv_0=(Token)match(input,19,FOLLOW_3); 

                	newLeafNode(otherlv_0, grammarAccess.getUserByUserIdAccess().getUserIdKeyword_0());
                
            // InternalAtsDsl.g:3300:1: ( (lv_userId_1_0= RULE_STRING ) )
            // InternalAtsDsl.g:3301:1: (lv_userId_1_0= RULE_STRING )
            {
            // InternalAtsDsl.g:3301:1: (lv_userId_1_0= RULE_STRING )
            // InternalAtsDsl.g:3302:3: lv_userId_1_0= RULE_STRING
            {
            lv_userId_1_0=(Token)match(input,RULE_STRING,FOLLOW_2); 

            			newLeafNode(lv_userId_1_0, grammarAccess.getUserByUserIdAccess().getUserIdSTRINGTerminalRuleCall_1_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getUserByUserIdRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"userId",
                    		lv_userId_1_0, 
                    		"org.eclipse.xtext.common.Terminals.STRING");
            	    

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
    // InternalAtsDsl.g:3326:1: entryRuleUserByName returns [EObject current=null] : iv_ruleUserByName= ruleUserByName EOF ;
    public final EObject entryRuleUserByName() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleUserByName = null;


        try {
            // InternalAtsDsl.g:3327:2: (iv_ruleUserByName= ruleUserByName EOF )
            // InternalAtsDsl.g:3328:2: iv_ruleUserByName= ruleUserByName EOF
            {
             newCompositeNode(grammarAccess.getUserByNameRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleUserByName=ruleUserByName();

            state._fsp--;

             current =iv_ruleUserByName; 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:3335:1: ruleUserByName returns [EObject current=null] : (otherlv_0= 'named' ( (lv_userName_1_0= RULE_STRING ) ) ) ;
    public final EObject ruleUserByName() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_userName_1_0=null;

         enterRule(); 
            
        try {
            // InternalAtsDsl.g:3338:28: ( (otherlv_0= 'named' ( (lv_userName_1_0= RULE_STRING ) ) ) )
            // InternalAtsDsl.g:3339:1: (otherlv_0= 'named' ( (lv_userName_1_0= RULE_STRING ) ) )
            {
            // InternalAtsDsl.g:3339:1: (otherlv_0= 'named' ( (lv_userName_1_0= RULE_STRING ) ) )
            // InternalAtsDsl.g:3339:3: otherlv_0= 'named' ( (lv_userName_1_0= RULE_STRING ) )
            {
            otherlv_0=(Token)match(input,77,FOLLOW_3); 

                	newLeafNode(otherlv_0, grammarAccess.getUserByNameAccess().getNamedKeyword_0());
                
            // InternalAtsDsl.g:3343:1: ( (lv_userName_1_0= RULE_STRING ) )
            // InternalAtsDsl.g:3344:1: (lv_userName_1_0= RULE_STRING )
            {
            // InternalAtsDsl.g:3344:1: (lv_userName_1_0= RULE_STRING )
            // InternalAtsDsl.g:3345:3: lv_userName_1_0= RULE_STRING
            {
            lv_userName_1_0=(Token)match(input,RULE_STRING,FOLLOW_2); 

            			newLeafNode(lv_userName_1_0, grammarAccess.getUserByNameAccess().getUserNameSTRINGTerminalRuleCall_1_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getUserByNameRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"userName",
                    		lv_userName_1_0, 
                    		"org.eclipse.xtext.common.Terminals.STRING");
            	    

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
    // InternalAtsDsl.g:3369:1: entryRuleDECISION_REVIEW_NAME_REFERENCE returns [String current=null] : iv_ruleDECISION_REVIEW_NAME_REFERENCE= ruleDECISION_REVIEW_NAME_REFERENCE EOF ;
    public final String entryRuleDECISION_REVIEW_NAME_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleDECISION_REVIEW_NAME_REFERENCE = null;


        try {
            // InternalAtsDsl.g:3370:2: (iv_ruleDECISION_REVIEW_NAME_REFERENCE= ruleDECISION_REVIEW_NAME_REFERENCE EOF )
            // InternalAtsDsl.g:3371:2: iv_ruleDECISION_REVIEW_NAME_REFERENCE= ruleDECISION_REVIEW_NAME_REFERENCE EOF
            {
             newCompositeNode(grammarAccess.getDECISION_REVIEW_NAME_REFERENCERule()); 
            pushFollow(FOLLOW_1);
            iv_ruleDECISION_REVIEW_NAME_REFERENCE=ruleDECISION_REVIEW_NAME_REFERENCE();

            state._fsp--;

             current =iv_ruleDECISION_REVIEW_NAME_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:3378:1: ruleDECISION_REVIEW_NAME_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleDECISION_REVIEW_NAME_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // InternalAtsDsl.g:3381:28: (this_STRING_0= RULE_STRING )
            // InternalAtsDsl.g:3382:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_2); 

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
    // InternalAtsDsl.g:3397:1: entryRulePEER_REVIEW_NAME_REFERENCE returns [String current=null] : iv_rulePEER_REVIEW_NAME_REFERENCE= rulePEER_REVIEW_NAME_REFERENCE EOF ;
    public final String entryRulePEER_REVIEW_NAME_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_rulePEER_REVIEW_NAME_REFERENCE = null;


        try {
            // InternalAtsDsl.g:3398:2: (iv_rulePEER_REVIEW_NAME_REFERENCE= rulePEER_REVIEW_NAME_REFERENCE EOF )
            // InternalAtsDsl.g:3399:2: iv_rulePEER_REVIEW_NAME_REFERENCE= rulePEER_REVIEW_NAME_REFERENCE EOF
            {
             newCompositeNode(grammarAccess.getPEER_REVIEW_NAME_REFERENCERule()); 
            pushFollow(FOLLOW_1);
            iv_rulePEER_REVIEW_NAME_REFERENCE=rulePEER_REVIEW_NAME_REFERENCE();

            state._fsp--;

             current =iv_rulePEER_REVIEW_NAME_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:3406:1: rulePEER_REVIEW_NAME_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken rulePEER_REVIEW_NAME_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // InternalAtsDsl.g:3409:28: (this_STRING_0= RULE_STRING )
            // InternalAtsDsl.g:3410:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_2); 

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
    // InternalAtsDsl.g:3425:1: entryRuleSTATE_NAME_REFERENCE returns [String current=null] : iv_ruleSTATE_NAME_REFERENCE= ruleSTATE_NAME_REFERENCE EOF ;
    public final String entryRuleSTATE_NAME_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleSTATE_NAME_REFERENCE = null;


        try {
            // InternalAtsDsl.g:3426:2: (iv_ruleSTATE_NAME_REFERENCE= ruleSTATE_NAME_REFERENCE EOF )
            // InternalAtsDsl.g:3427:2: iv_ruleSTATE_NAME_REFERENCE= ruleSTATE_NAME_REFERENCE EOF
            {
             newCompositeNode(grammarAccess.getSTATE_NAME_REFERENCERule()); 
            pushFollow(FOLLOW_1);
            iv_ruleSTATE_NAME_REFERENCE=ruleSTATE_NAME_REFERENCE();

            state._fsp--;

             current =iv_ruleSTATE_NAME_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:3434:1: ruleSTATE_NAME_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleSTATE_NAME_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // InternalAtsDsl.g:3437:28: (this_STRING_0= RULE_STRING )
            // InternalAtsDsl.g:3438:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_2); 

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
    // InternalAtsDsl.g:3453:1: entryRuleWIDGET_NAME_REFERENCE returns [String current=null] : iv_ruleWIDGET_NAME_REFERENCE= ruleWIDGET_NAME_REFERENCE EOF ;
    public final String entryRuleWIDGET_NAME_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleWIDGET_NAME_REFERENCE = null;


        try {
            // InternalAtsDsl.g:3454:2: (iv_ruleWIDGET_NAME_REFERENCE= ruleWIDGET_NAME_REFERENCE EOF )
            // InternalAtsDsl.g:3455:2: iv_ruleWIDGET_NAME_REFERENCE= ruleWIDGET_NAME_REFERENCE EOF
            {
             newCompositeNode(grammarAccess.getWIDGET_NAME_REFERENCERule()); 
            pushFollow(FOLLOW_1);
            iv_ruleWIDGET_NAME_REFERENCE=ruleWIDGET_NAME_REFERENCE();

            state._fsp--;

             current =iv_ruleWIDGET_NAME_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:3462:1: ruleWIDGET_NAME_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleWIDGET_NAME_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // InternalAtsDsl.g:3465:28: (this_STRING_0= RULE_STRING )
            // InternalAtsDsl.g:3466:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_2); 

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
    // InternalAtsDsl.g:3481:1: entryRuleWORK_DEFINITION_NAME_REFERENCE returns [String current=null] : iv_ruleWORK_DEFINITION_NAME_REFERENCE= ruleWORK_DEFINITION_NAME_REFERENCE EOF ;
    public final String entryRuleWORK_DEFINITION_NAME_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleWORK_DEFINITION_NAME_REFERENCE = null;


        try {
            // InternalAtsDsl.g:3482:2: (iv_ruleWORK_DEFINITION_NAME_REFERENCE= ruleWORK_DEFINITION_NAME_REFERENCE EOF )
            // InternalAtsDsl.g:3483:2: iv_ruleWORK_DEFINITION_NAME_REFERENCE= ruleWORK_DEFINITION_NAME_REFERENCE EOF
            {
             newCompositeNode(grammarAccess.getWORK_DEFINITION_NAME_REFERENCERule()); 
            pushFollow(FOLLOW_1);
            iv_ruleWORK_DEFINITION_NAME_REFERENCE=ruleWORK_DEFINITION_NAME_REFERENCE();

            state._fsp--;

             current =iv_ruleWORK_DEFINITION_NAME_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:3490:1: ruleWORK_DEFINITION_NAME_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleWORK_DEFINITION_NAME_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // InternalAtsDsl.g:3493:28: (this_STRING_0= RULE_STRING )
            // InternalAtsDsl.g:3494:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_2); 

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
    // InternalAtsDsl.g:3509:1: entryRuleToState returns [EObject current=null] : iv_ruleToState= ruleToState EOF ;
    public final EObject entryRuleToState() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleToState = null;


        try {
            // InternalAtsDsl.g:3510:2: (iv_ruleToState= ruleToState EOF )
            // InternalAtsDsl.g:3511:2: iv_ruleToState= ruleToState EOF
            {
             newCompositeNode(grammarAccess.getToStateRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleToState=ruleToState();

            state._fsp--;

             current =iv_ruleToState; 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:3518:1: ruleToState returns [EObject current=null] : (otherlv_0= 'to' ( ( ruleSTATE_NAME_REFERENCE ) ) ( (lv_options_2_0= ruleTransitionOption ) )* ) ;
    public final EObject ruleToState() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        AntlrDatatypeRuleToken lv_options_2_0 = null;


         enterRule(); 
            
        try {
            // InternalAtsDsl.g:3521:28: ( (otherlv_0= 'to' ( ( ruleSTATE_NAME_REFERENCE ) ) ( (lv_options_2_0= ruleTransitionOption ) )* ) )
            // InternalAtsDsl.g:3522:1: (otherlv_0= 'to' ( ( ruleSTATE_NAME_REFERENCE ) ) ( (lv_options_2_0= ruleTransitionOption ) )* )
            {
            // InternalAtsDsl.g:3522:1: (otherlv_0= 'to' ( ( ruleSTATE_NAME_REFERENCE ) ) ( (lv_options_2_0= ruleTransitionOption ) )* )
            // InternalAtsDsl.g:3522:3: otherlv_0= 'to' ( ( ruleSTATE_NAME_REFERENCE ) ) ( (lv_options_2_0= ruleTransitionOption ) )*
            {
            otherlv_0=(Token)match(input,78,FOLLOW_3); 

                	newLeafNode(otherlv_0, grammarAccess.getToStateAccess().getToKeyword_0());
                
            // InternalAtsDsl.g:3526:1: ( ( ruleSTATE_NAME_REFERENCE ) )
            // InternalAtsDsl.g:3527:1: ( ruleSTATE_NAME_REFERENCE )
            {
            // InternalAtsDsl.g:3527:1: ( ruleSTATE_NAME_REFERENCE )
            // InternalAtsDsl.g:3528:3: ruleSTATE_NAME_REFERENCE
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getToStateRule());
            	        }
                    
             
            	        newCompositeNode(grammarAccess.getToStateAccess().getStateStateDefCrossReference_1_0()); 
            	    
            pushFollow(FOLLOW_102);
            ruleSTATE_NAME_REFERENCE();

            state._fsp--;

             
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // InternalAtsDsl.g:3541:2: ( (lv_options_2_0= ruleTransitionOption ) )*
            loop94:
            do {
                int alt94=2;
                int LA94_0 = input.LA(1);

                if ( (LA94_0==RULE_STRING||(LA94_0>=85 && LA94_0<=86)) ) {
                    alt94=1;
                }


                switch (alt94) {
            	case 1 :
            	    // InternalAtsDsl.g:3542:1: (lv_options_2_0= ruleTransitionOption )
            	    {
            	    // InternalAtsDsl.g:3542:1: (lv_options_2_0= ruleTransitionOption )
            	    // InternalAtsDsl.g:3543:3: lv_options_2_0= ruleTransitionOption
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getToStateAccess().getOptionsTransitionOptionParserRuleCall_2_0()); 
            	    	    
            	    pushFollow(FOLLOW_102);
            	    lv_options_2_0=ruleTransitionOption();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getToStateRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"options",
            	            		lv_options_2_0, 
            	            		"org.eclipse.osee.ats.dsl.AtsDsl.TransitionOption");
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
    // InternalAtsDsl.g:3567:1: entryRuleLayoutType returns [EObject current=null] : iv_ruleLayoutType= ruleLayoutType EOF ;
    public final EObject entryRuleLayoutType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleLayoutType = null;


        try {
            // InternalAtsDsl.g:3568:2: (iv_ruleLayoutType= ruleLayoutType EOF )
            // InternalAtsDsl.g:3569:2: iv_ruleLayoutType= ruleLayoutType EOF
            {
             newCompositeNode(grammarAccess.getLayoutTypeRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleLayoutType=ruleLayoutType();

            state._fsp--;

             current =iv_ruleLayoutType; 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:3576:1: ruleLayoutType returns [EObject current=null] : (this_LayoutDef_0= ruleLayoutDef | this_LayoutCopy_1= ruleLayoutCopy ) ;
    public final EObject ruleLayoutType() throws RecognitionException {
        EObject current = null;

        EObject this_LayoutDef_0 = null;

        EObject this_LayoutCopy_1 = null;


         enterRule(); 
            
        try {
            // InternalAtsDsl.g:3579:28: ( (this_LayoutDef_0= ruleLayoutDef | this_LayoutCopy_1= ruleLayoutCopy ) )
            // InternalAtsDsl.g:3580:1: (this_LayoutDef_0= ruleLayoutDef | this_LayoutCopy_1= ruleLayoutCopy )
            {
            // InternalAtsDsl.g:3580:1: (this_LayoutDef_0= ruleLayoutDef | this_LayoutCopy_1= ruleLayoutCopy )
            int alt95=2;
            int LA95_0 = input.LA(1);

            if ( (LA95_0==79) ) {
                alt95=1;
            }
            else if ( (LA95_0==80) ) {
                alt95=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 95, 0, input);

                throw nvae;
            }
            switch (alt95) {
                case 1 :
                    // InternalAtsDsl.g:3581:5: this_LayoutDef_0= ruleLayoutDef
                    {
                     
                            newCompositeNode(grammarAccess.getLayoutTypeAccess().getLayoutDefParserRuleCall_0()); 
                        
                    pushFollow(FOLLOW_2);
                    this_LayoutDef_0=ruleLayoutDef();

                    state._fsp--;

                     
                            current = this_LayoutDef_0; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // InternalAtsDsl.g:3591:5: this_LayoutCopy_1= ruleLayoutCopy
                    {
                     
                            newCompositeNode(grammarAccess.getLayoutTypeAccess().getLayoutCopyParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_2);
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
    // InternalAtsDsl.g:3607:1: entryRuleLayoutDef returns [EObject current=null] : iv_ruleLayoutDef= ruleLayoutDef EOF ;
    public final EObject entryRuleLayoutDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleLayoutDef = null;


        try {
            // InternalAtsDsl.g:3608:2: (iv_ruleLayoutDef= ruleLayoutDef EOF )
            // InternalAtsDsl.g:3609:2: iv_ruleLayoutDef= ruleLayoutDef EOF
            {
             newCompositeNode(grammarAccess.getLayoutDefRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleLayoutDef=ruleLayoutDef();

            state._fsp--;

             current =iv_ruleLayoutDef; 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:3616:1: ruleLayoutDef returns [EObject current=null] : (otherlv_0= 'layout' otherlv_1= '{' ( (lv_layoutItems_2_0= ruleLayoutItem ) )+ otherlv_3= '}' ) ;
    public final EObject ruleLayoutDef() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;
        Token otherlv_3=null;
        EObject lv_layoutItems_2_0 = null;


         enterRule(); 
            
        try {
            // InternalAtsDsl.g:3619:28: ( (otherlv_0= 'layout' otherlv_1= '{' ( (lv_layoutItems_2_0= ruleLayoutItem ) )+ otherlv_3= '}' ) )
            // InternalAtsDsl.g:3620:1: (otherlv_0= 'layout' otherlv_1= '{' ( (lv_layoutItems_2_0= ruleLayoutItem ) )+ otherlv_3= '}' )
            {
            // InternalAtsDsl.g:3620:1: (otherlv_0= 'layout' otherlv_1= '{' ( (lv_layoutItems_2_0= ruleLayoutItem ) )+ otherlv_3= '}' )
            // InternalAtsDsl.g:3620:3: otherlv_0= 'layout' otherlv_1= '{' ( (lv_layoutItems_2_0= ruleLayoutItem ) )+ otherlv_3= '}'
            {
            otherlv_0=(Token)match(input,79,FOLLOW_43); 

                	newLeafNode(otherlv_0, grammarAccess.getLayoutDefAccess().getLayoutKeyword_0());
                
            otherlv_1=(Token)match(input,17,FOLLOW_103); 

                	newLeafNode(otherlv_1, grammarAccess.getLayoutDefAccess().getLeftCurlyBracketKeyword_1());
                
            // InternalAtsDsl.g:3628:1: ( (lv_layoutItems_2_0= ruleLayoutItem ) )+
            int cnt96=0;
            loop96:
            do {
                int alt96=2;
                int LA96_0 = input.LA(1);

                if ( ((LA96_0>=56 && LA96_0<=57)||LA96_0==81) ) {
                    alt96=1;
                }


                switch (alt96) {
            	case 1 :
            	    // InternalAtsDsl.g:3629:1: (lv_layoutItems_2_0= ruleLayoutItem )
            	    {
            	    // InternalAtsDsl.g:3629:1: (lv_layoutItems_2_0= ruleLayoutItem )
            	    // InternalAtsDsl.g:3630:3: lv_layoutItems_2_0= ruleLayoutItem
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getLayoutDefAccess().getLayoutItemsLayoutItemParserRuleCall_2_0()); 
            	    	    
            	    pushFollow(FOLLOW_104);
            	    lv_layoutItems_2_0=ruleLayoutItem();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getLayoutDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"layoutItems",
            	            		lv_layoutItems_2_0, 
            	            		"org.eclipse.osee.ats.dsl.AtsDsl.LayoutItem");
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

            otherlv_3=(Token)match(input,22,FOLLOW_2); 

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
    // InternalAtsDsl.g:3658:1: entryRuleLayoutCopy returns [EObject current=null] : iv_ruleLayoutCopy= ruleLayoutCopy EOF ;
    public final EObject entryRuleLayoutCopy() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleLayoutCopy = null;


        try {
            // InternalAtsDsl.g:3659:2: (iv_ruleLayoutCopy= ruleLayoutCopy EOF )
            // InternalAtsDsl.g:3660:2: iv_ruleLayoutCopy= ruleLayoutCopy EOF
            {
             newCompositeNode(grammarAccess.getLayoutCopyRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleLayoutCopy=ruleLayoutCopy();

            state._fsp--;

             current =iv_ruleLayoutCopy; 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:3667:1: ruleLayoutCopy returns [EObject current=null] : (otherlv_0= 'layoutCopyFrom' ( ( ruleSTATE_NAME_REFERENCE ) ) ) ;
    public final EObject ruleLayoutCopy() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;

         enterRule(); 
            
        try {
            // InternalAtsDsl.g:3670:28: ( (otherlv_0= 'layoutCopyFrom' ( ( ruleSTATE_NAME_REFERENCE ) ) ) )
            // InternalAtsDsl.g:3671:1: (otherlv_0= 'layoutCopyFrom' ( ( ruleSTATE_NAME_REFERENCE ) ) )
            {
            // InternalAtsDsl.g:3671:1: (otherlv_0= 'layoutCopyFrom' ( ( ruleSTATE_NAME_REFERENCE ) ) )
            // InternalAtsDsl.g:3671:3: otherlv_0= 'layoutCopyFrom' ( ( ruleSTATE_NAME_REFERENCE ) )
            {
            otherlv_0=(Token)match(input,80,FOLLOW_3); 

                	newLeafNode(otherlv_0, grammarAccess.getLayoutCopyAccess().getLayoutCopyFromKeyword_0());
                
            // InternalAtsDsl.g:3675:1: ( ( ruleSTATE_NAME_REFERENCE ) )
            // InternalAtsDsl.g:3676:1: ( ruleSTATE_NAME_REFERENCE )
            {
            // InternalAtsDsl.g:3676:1: ( ruleSTATE_NAME_REFERENCE )
            // InternalAtsDsl.g:3677:3: ruleSTATE_NAME_REFERENCE
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getLayoutCopyRule());
            	        }
                    
             
            	        newCompositeNode(grammarAccess.getLayoutCopyAccess().getStateStateDefCrossReference_1_0()); 
            	    
            pushFollow(FOLLOW_2);
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
    // InternalAtsDsl.g:3698:1: entryRuleLayoutItem returns [EObject current=null] : iv_ruleLayoutItem= ruleLayoutItem EOF ;
    public final EObject entryRuleLayoutItem() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleLayoutItem = null;


        try {
            // InternalAtsDsl.g:3699:2: (iv_ruleLayoutItem= ruleLayoutItem EOF )
            // InternalAtsDsl.g:3700:2: iv_ruleLayoutItem= ruleLayoutItem EOF
            {
             newCompositeNode(grammarAccess.getLayoutItemRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleLayoutItem=ruleLayoutItem();

            state._fsp--;

             current =iv_ruleLayoutItem; 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:3707:1: ruleLayoutItem returns [EObject current=null] : (this_WidgetRef_0= ruleWidgetRef | this_AttrWidget_1= ruleAttrWidget | this_Composite_2= ruleComposite ) ;
    public final EObject ruleLayoutItem() throws RecognitionException {
        EObject current = null;

        EObject this_WidgetRef_0 = null;

        EObject this_AttrWidget_1 = null;

        EObject this_Composite_2 = null;


         enterRule(); 
            
        try {
            // InternalAtsDsl.g:3710:28: ( (this_WidgetRef_0= ruleWidgetRef | this_AttrWidget_1= ruleAttrWidget | this_Composite_2= ruleComposite ) )
            // InternalAtsDsl.g:3711:1: (this_WidgetRef_0= ruleWidgetRef | this_AttrWidget_1= ruleAttrWidget | this_Composite_2= ruleComposite )
            {
            // InternalAtsDsl.g:3711:1: (this_WidgetRef_0= ruleWidgetRef | this_AttrWidget_1= ruleAttrWidget | this_Composite_2= ruleComposite )
            int alt97=3;
            switch ( input.LA(1) ) {
            case 56:
                {
                alt97=1;
                }
                break;
            case 57:
                {
                alt97=2;
                }
                break;
            case 81:
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
                    // InternalAtsDsl.g:3712:5: this_WidgetRef_0= ruleWidgetRef
                    {
                     
                            newCompositeNode(grammarAccess.getLayoutItemAccess().getWidgetRefParserRuleCall_0()); 
                        
                    pushFollow(FOLLOW_2);
                    this_WidgetRef_0=ruleWidgetRef();

                    state._fsp--;

                     
                            current = this_WidgetRef_0; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // InternalAtsDsl.g:3722:5: this_AttrWidget_1= ruleAttrWidget
                    {
                     
                            newCompositeNode(grammarAccess.getLayoutItemAccess().getAttrWidgetParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_2);
                    this_AttrWidget_1=ruleAttrWidget();

                    state._fsp--;

                     
                            current = this_AttrWidget_1; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 3 :
                    // InternalAtsDsl.g:3732:5: this_Composite_2= ruleComposite
                    {
                     
                            newCompositeNode(grammarAccess.getLayoutItemAccess().getCompositeParserRuleCall_2()); 
                        
                    pushFollow(FOLLOW_2);
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
    // InternalAtsDsl.g:3748:1: entryRuleComposite returns [EObject current=null] : iv_ruleComposite= ruleComposite EOF ;
    public final EObject entryRuleComposite() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleComposite = null;


        try {
            // InternalAtsDsl.g:3749:2: (iv_ruleComposite= ruleComposite EOF )
            // InternalAtsDsl.g:3750:2: iv_ruleComposite= ruleComposite EOF
            {
             newCompositeNode(grammarAccess.getCompositeRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleComposite=ruleComposite();

            state._fsp--;

             current =iv_ruleComposite; 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:3757:1: ruleComposite returns [EObject current=null] : (otherlv_0= 'composite' otherlv_1= '{' otherlv_2= 'numColumns' ( (lv_numColumns_3_0= RULE_INT ) ) ( (lv_layoutItems_4_0= ruleLayoutItem ) )+ (otherlv_5= 'option' ( (lv_options_6_0= ruleCompositeOption ) ) )* otherlv_7= '}' ) ;
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
            // InternalAtsDsl.g:3760:28: ( (otherlv_0= 'composite' otherlv_1= '{' otherlv_2= 'numColumns' ( (lv_numColumns_3_0= RULE_INT ) ) ( (lv_layoutItems_4_0= ruleLayoutItem ) )+ (otherlv_5= 'option' ( (lv_options_6_0= ruleCompositeOption ) ) )* otherlv_7= '}' ) )
            // InternalAtsDsl.g:3761:1: (otherlv_0= 'composite' otherlv_1= '{' otherlv_2= 'numColumns' ( (lv_numColumns_3_0= RULE_INT ) ) ( (lv_layoutItems_4_0= ruleLayoutItem ) )+ (otherlv_5= 'option' ( (lv_options_6_0= ruleCompositeOption ) ) )* otherlv_7= '}' )
            {
            // InternalAtsDsl.g:3761:1: (otherlv_0= 'composite' otherlv_1= '{' otherlv_2= 'numColumns' ( (lv_numColumns_3_0= RULE_INT ) ) ( (lv_layoutItems_4_0= ruleLayoutItem ) )+ (otherlv_5= 'option' ( (lv_options_6_0= ruleCompositeOption ) ) )* otherlv_7= '}' )
            // InternalAtsDsl.g:3761:3: otherlv_0= 'composite' otherlv_1= '{' otherlv_2= 'numColumns' ( (lv_numColumns_3_0= RULE_INT ) ) ( (lv_layoutItems_4_0= ruleLayoutItem ) )+ (otherlv_5= 'option' ( (lv_options_6_0= ruleCompositeOption ) ) )* otherlv_7= '}'
            {
            otherlv_0=(Token)match(input,81,FOLLOW_43); 

                	newLeafNode(otherlv_0, grammarAccess.getCompositeAccess().getCompositeKeyword_0());
                
            otherlv_1=(Token)match(input,17,FOLLOW_105); 

                	newLeafNode(otherlv_1, grammarAccess.getCompositeAccess().getLeftCurlyBracketKeyword_1());
                
            otherlv_2=(Token)match(input,82,FOLLOW_24); 

                	newLeafNode(otherlv_2, grammarAccess.getCompositeAccess().getNumColumnsKeyword_2());
                
            // InternalAtsDsl.g:3773:1: ( (lv_numColumns_3_0= RULE_INT ) )
            // InternalAtsDsl.g:3774:1: (lv_numColumns_3_0= RULE_INT )
            {
            // InternalAtsDsl.g:3774:1: (lv_numColumns_3_0= RULE_INT )
            // InternalAtsDsl.g:3775:3: lv_numColumns_3_0= RULE_INT
            {
            lv_numColumns_3_0=(Token)match(input,RULE_INT,FOLLOW_103); 

            			newLeafNode(lv_numColumns_3_0, grammarAccess.getCompositeAccess().getNumColumnsINTTerminalRuleCall_3_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getCompositeRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"numColumns",
                    		lv_numColumns_3_0, 
                    		"org.eclipse.xtext.common.Terminals.INT");
            	    

            }


            }

            // InternalAtsDsl.g:3791:2: ( (lv_layoutItems_4_0= ruleLayoutItem ) )+
            int cnt98=0;
            loop98:
            do {
                int alt98=2;
                int LA98_0 = input.LA(1);

                if ( ((LA98_0>=56 && LA98_0<=57)||LA98_0==81) ) {
                    alt98=1;
                }


                switch (alt98) {
            	case 1 :
            	    // InternalAtsDsl.g:3792:1: (lv_layoutItems_4_0= ruleLayoutItem )
            	    {
            	    // InternalAtsDsl.g:3792:1: (lv_layoutItems_4_0= ruleLayoutItem )
            	    // InternalAtsDsl.g:3793:3: lv_layoutItems_4_0= ruleLayoutItem
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getCompositeAccess().getLayoutItemsLayoutItemParserRuleCall_4_0()); 
            	    	    
            	    pushFollow(FOLLOW_106);
            	    lv_layoutItems_4_0=ruleLayoutItem();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getCompositeRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"layoutItems",
            	            		lv_layoutItems_4_0, 
            	            		"org.eclipse.osee.ats.dsl.AtsDsl.LayoutItem");
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

            // InternalAtsDsl.g:3809:3: (otherlv_5= 'option' ( (lv_options_6_0= ruleCompositeOption ) ) )*
            loop99:
            do {
                int alt99=2;
                int LA99_0 = input.LA(1);

                if ( (LA99_0==53) ) {
                    alt99=1;
                }


                switch (alt99) {
            	case 1 :
            	    // InternalAtsDsl.g:3809:5: otherlv_5= 'option' ( (lv_options_6_0= ruleCompositeOption ) )
            	    {
            	    otherlv_5=(Token)match(input,53,FOLLOW_107); 

            	        	newLeafNode(otherlv_5, grammarAccess.getCompositeAccess().getOptionKeyword_5_0());
            	        
            	    // InternalAtsDsl.g:3813:1: ( (lv_options_6_0= ruleCompositeOption ) )
            	    // InternalAtsDsl.g:3814:1: (lv_options_6_0= ruleCompositeOption )
            	    {
            	    // InternalAtsDsl.g:3814:1: (lv_options_6_0= ruleCompositeOption )
            	    // InternalAtsDsl.g:3815:3: lv_options_6_0= ruleCompositeOption
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getCompositeAccess().getOptionsCompositeOptionParserRuleCall_5_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_108);
            	    lv_options_6_0=ruleCompositeOption();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getCompositeRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"options",
            	            		lv_options_6_0, 
            	            		"org.eclipse.osee.ats.dsl.AtsDsl.CompositeOption");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop99;
                }
            } while (true);

            otherlv_7=(Token)match(input,22,FOLLOW_2); 

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
    // InternalAtsDsl.g:3843:1: entryRuleUSER_DEF_OPTION_NAME returns [String current=null] : iv_ruleUSER_DEF_OPTION_NAME= ruleUSER_DEF_OPTION_NAME EOF ;
    public final String entryRuleUSER_DEF_OPTION_NAME() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleUSER_DEF_OPTION_NAME = null;


        try {
            // InternalAtsDsl.g:3844:2: (iv_ruleUSER_DEF_OPTION_NAME= ruleUSER_DEF_OPTION_NAME EOF )
            // InternalAtsDsl.g:3845:2: iv_ruleUSER_DEF_OPTION_NAME= ruleUSER_DEF_OPTION_NAME EOF
            {
             newCompositeNode(grammarAccess.getUSER_DEF_OPTION_NAMERule()); 
            pushFollow(FOLLOW_1);
            iv_ruleUSER_DEF_OPTION_NAME=ruleUSER_DEF_OPTION_NAME();

            state._fsp--;

             current =iv_ruleUSER_DEF_OPTION_NAME.getText(); 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:3852:1: ruleUSER_DEF_OPTION_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleUSER_DEF_OPTION_NAME() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // InternalAtsDsl.g:3855:28: (this_STRING_0= RULE_STRING )
            // InternalAtsDsl.g:3856:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_2); 

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
    // InternalAtsDsl.g:3871:1: entryRuleUserDefOption returns [String current=null] : iv_ruleUserDefOption= ruleUserDefOption EOF ;
    public final String entryRuleUserDefOption() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleUserDefOption = null;


        try {
            // InternalAtsDsl.g:3872:2: (iv_ruleUserDefOption= ruleUserDefOption EOF )
            // InternalAtsDsl.g:3873:2: iv_ruleUserDefOption= ruleUserDefOption EOF
            {
             newCompositeNode(grammarAccess.getUserDefOptionRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleUserDefOption=ruleUserDefOption();

            state._fsp--;

             current =iv_ruleUserDefOption.getText(); 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:3880:1: ruleUserDefOption returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'GetOrCreate' | this_USER_DEF_OPTION_NAME_1= ruleUSER_DEF_OPTION_NAME ) ;
    public final AntlrDatatypeRuleToken ruleUserDefOption() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        AntlrDatatypeRuleToken this_USER_DEF_OPTION_NAME_1 = null;


         enterRule(); 
            
        try {
            // InternalAtsDsl.g:3883:28: ( (kw= 'GetOrCreate' | this_USER_DEF_OPTION_NAME_1= ruleUSER_DEF_OPTION_NAME ) )
            // InternalAtsDsl.g:3884:1: (kw= 'GetOrCreate' | this_USER_DEF_OPTION_NAME_1= ruleUSER_DEF_OPTION_NAME )
            {
            // InternalAtsDsl.g:3884:1: (kw= 'GetOrCreate' | this_USER_DEF_OPTION_NAME_1= ruleUSER_DEF_OPTION_NAME )
            int alt100=2;
            int LA100_0 = input.LA(1);

            if ( (LA100_0==83) ) {
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
                    // InternalAtsDsl.g:3885:2: kw= 'GetOrCreate'
                    {
                    kw=(Token)match(input,83,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getUserDefOptionAccess().getGetOrCreateKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // InternalAtsDsl.g:3892:5: this_USER_DEF_OPTION_NAME_1= ruleUSER_DEF_OPTION_NAME
                    {
                     
                            newCompositeNode(grammarAccess.getUserDefOptionAccess().getUSER_DEF_OPTION_NAMEParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_2);
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
    // InternalAtsDsl.g:3910:1: entryRulePROGRAM_DEF_OPTION_NAME returns [String current=null] : iv_rulePROGRAM_DEF_OPTION_NAME= rulePROGRAM_DEF_OPTION_NAME EOF ;
    public final String entryRulePROGRAM_DEF_OPTION_NAME() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_rulePROGRAM_DEF_OPTION_NAME = null;


        try {
            // InternalAtsDsl.g:3911:2: (iv_rulePROGRAM_DEF_OPTION_NAME= rulePROGRAM_DEF_OPTION_NAME EOF )
            // InternalAtsDsl.g:3912:2: iv_rulePROGRAM_DEF_OPTION_NAME= rulePROGRAM_DEF_OPTION_NAME EOF
            {
             newCompositeNode(grammarAccess.getPROGRAM_DEF_OPTION_NAMERule()); 
            pushFollow(FOLLOW_1);
            iv_rulePROGRAM_DEF_OPTION_NAME=rulePROGRAM_DEF_OPTION_NAME();

            state._fsp--;

             current =iv_rulePROGRAM_DEF_OPTION_NAME.getText(); 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:3919:1: rulePROGRAM_DEF_OPTION_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken rulePROGRAM_DEF_OPTION_NAME() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // InternalAtsDsl.g:3922:28: (this_STRING_0= RULE_STRING )
            // InternalAtsDsl.g:3923:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_2); 

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
    // InternalAtsDsl.g:3938:1: entryRuleProgramDefOption returns [String current=null] : iv_ruleProgramDefOption= ruleProgramDefOption EOF ;
    public final String entryRuleProgramDefOption() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleProgramDefOption = null;


        try {
            // InternalAtsDsl.g:3939:2: (iv_ruleProgramDefOption= ruleProgramDefOption EOF )
            // InternalAtsDsl.g:3940:2: iv_ruleProgramDefOption= ruleProgramDefOption EOF
            {
             newCompositeNode(grammarAccess.getProgramDefOptionRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleProgramDefOption=ruleProgramDefOption();

            state._fsp--;

             current =iv_ruleProgramDefOption.getText(); 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:3947:1: ruleProgramDefOption returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'GetOrCreate' | this_PROGRAM_DEF_OPTION_NAME_1= rulePROGRAM_DEF_OPTION_NAME ) ;
    public final AntlrDatatypeRuleToken ruleProgramDefOption() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        AntlrDatatypeRuleToken this_PROGRAM_DEF_OPTION_NAME_1 = null;


         enterRule(); 
            
        try {
            // InternalAtsDsl.g:3950:28: ( (kw= 'GetOrCreate' | this_PROGRAM_DEF_OPTION_NAME_1= rulePROGRAM_DEF_OPTION_NAME ) )
            // InternalAtsDsl.g:3951:1: (kw= 'GetOrCreate' | this_PROGRAM_DEF_OPTION_NAME_1= rulePROGRAM_DEF_OPTION_NAME )
            {
            // InternalAtsDsl.g:3951:1: (kw= 'GetOrCreate' | this_PROGRAM_DEF_OPTION_NAME_1= rulePROGRAM_DEF_OPTION_NAME )
            int alt101=2;
            int LA101_0 = input.LA(1);

            if ( (LA101_0==83) ) {
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
                    // InternalAtsDsl.g:3952:2: kw= 'GetOrCreate'
                    {
                    kw=(Token)match(input,83,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getProgramDefOptionAccess().getGetOrCreateKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // InternalAtsDsl.g:3959:5: this_PROGRAM_DEF_OPTION_NAME_1= rulePROGRAM_DEF_OPTION_NAME
                    {
                     
                            newCompositeNode(grammarAccess.getProgramDefOptionAccess().getPROGRAM_DEF_OPTION_NAMEParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_2);
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
    // InternalAtsDsl.g:3977:1: entryRuleTEAM_DEF_OPTION_NAME returns [String current=null] : iv_ruleTEAM_DEF_OPTION_NAME= ruleTEAM_DEF_OPTION_NAME EOF ;
    public final String entryRuleTEAM_DEF_OPTION_NAME() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleTEAM_DEF_OPTION_NAME = null;


        try {
            // InternalAtsDsl.g:3978:2: (iv_ruleTEAM_DEF_OPTION_NAME= ruleTEAM_DEF_OPTION_NAME EOF )
            // InternalAtsDsl.g:3979:2: iv_ruleTEAM_DEF_OPTION_NAME= ruleTEAM_DEF_OPTION_NAME EOF
            {
             newCompositeNode(grammarAccess.getTEAM_DEF_OPTION_NAMERule()); 
            pushFollow(FOLLOW_1);
            iv_ruleTEAM_DEF_OPTION_NAME=ruleTEAM_DEF_OPTION_NAME();

            state._fsp--;

             current =iv_ruleTEAM_DEF_OPTION_NAME.getText(); 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:3986:1: ruleTEAM_DEF_OPTION_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleTEAM_DEF_OPTION_NAME() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // InternalAtsDsl.g:3989:28: (this_STRING_0= RULE_STRING )
            // InternalAtsDsl.g:3990:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_2); 

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
    // InternalAtsDsl.g:4005:1: entryRuleTeamDefOption returns [String current=null] : iv_ruleTeamDefOption= ruleTeamDefOption EOF ;
    public final String entryRuleTeamDefOption() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleTeamDefOption = null;


        try {
            // InternalAtsDsl.g:4006:2: (iv_ruleTeamDefOption= ruleTeamDefOption EOF )
            // InternalAtsDsl.g:4007:2: iv_ruleTeamDefOption= ruleTeamDefOption EOF
            {
             newCompositeNode(grammarAccess.getTeamDefOptionRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleTeamDefOption=ruleTeamDefOption();

            state._fsp--;

             current =iv_ruleTeamDefOption.getText(); 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:4014:1: ruleTeamDefOption returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'GetOrCreate' | this_TEAM_DEF_OPTION_NAME_1= ruleTEAM_DEF_OPTION_NAME ) ;
    public final AntlrDatatypeRuleToken ruleTeamDefOption() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        AntlrDatatypeRuleToken this_TEAM_DEF_OPTION_NAME_1 = null;


         enterRule(); 
            
        try {
            // InternalAtsDsl.g:4017:28: ( (kw= 'GetOrCreate' | this_TEAM_DEF_OPTION_NAME_1= ruleTEAM_DEF_OPTION_NAME ) )
            // InternalAtsDsl.g:4018:1: (kw= 'GetOrCreate' | this_TEAM_DEF_OPTION_NAME_1= ruleTEAM_DEF_OPTION_NAME )
            {
            // InternalAtsDsl.g:4018:1: (kw= 'GetOrCreate' | this_TEAM_DEF_OPTION_NAME_1= ruleTEAM_DEF_OPTION_NAME )
            int alt102=2;
            int LA102_0 = input.LA(1);

            if ( (LA102_0==83) ) {
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
                    // InternalAtsDsl.g:4019:2: kw= 'GetOrCreate'
                    {
                    kw=(Token)match(input,83,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getTeamDefOptionAccess().getGetOrCreateKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // InternalAtsDsl.g:4026:5: this_TEAM_DEF_OPTION_NAME_1= ruleTEAM_DEF_OPTION_NAME
                    {
                     
                            newCompositeNode(grammarAccess.getTeamDefOptionAccess().getTEAM_DEF_OPTION_NAMEParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_2);
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
    // InternalAtsDsl.g:4044:1: entryRuleAI_DEF_OPTION_NAME returns [String current=null] : iv_ruleAI_DEF_OPTION_NAME= ruleAI_DEF_OPTION_NAME EOF ;
    public final String entryRuleAI_DEF_OPTION_NAME() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleAI_DEF_OPTION_NAME = null;


        try {
            // InternalAtsDsl.g:4045:2: (iv_ruleAI_DEF_OPTION_NAME= ruleAI_DEF_OPTION_NAME EOF )
            // InternalAtsDsl.g:4046:2: iv_ruleAI_DEF_OPTION_NAME= ruleAI_DEF_OPTION_NAME EOF
            {
             newCompositeNode(grammarAccess.getAI_DEF_OPTION_NAMERule()); 
            pushFollow(FOLLOW_1);
            iv_ruleAI_DEF_OPTION_NAME=ruleAI_DEF_OPTION_NAME();

            state._fsp--;

             current =iv_ruleAI_DEF_OPTION_NAME.getText(); 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:4053:1: ruleAI_DEF_OPTION_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleAI_DEF_OPTION_NAME() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // InternalAtsDsl.g:4056:28: (this_STRING_0= RULE_STRING )
            // InternalAtsDsl.g:4057:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_2); 

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
    // InternalAtsDsl.g:4072:1: entryRuleActionableItemOption returns [String current=null] : iv_ruleActionableItemOption= ruleActionableItemOption EOF ;
    public final String entryRuleActionableItemOption() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleActionableItemOption = null;


        try {
            // InternalAtsDsl.g:4073:2: (iv_ruleActionableItemOption= ruleActionableItemOption EOF )
            // InternalAtsDsl.g:4074:2: iv_ruleActionableItemOption= ruleActionableItemOption EOF
            {
             newCompositeNode(grammarAccess.getActionableItemOptionRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleActionableItemOption=ruleActionableItemOption();

            state._fsp--;

             current =iv_ruleActionableItemOption.getText(); 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:4081:1: ruleActionableItemOption returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'GetOrCreate' | this_AI_DEF_OPTION_NAME_1= ruleAI_DEF_OPTION_NAME ) ;
    public final AntlrDatatypeRuleToken ruleActionableItemOption() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        AntlrDatatypeRuleToken this_AI_DEF_OPTION_NAME_1 = null;


         enterRule(); 
            
        try {
            // InternalAtsDsl.g:4084:28: ( (kw= 'GetOrCreate' | this_AI_DEF_OPTION_NAME_1= ruleAI_DEF_OPTION_NAME ) )
            // InternalAtsDsl.g:4085:1: (kw= 'GetOrCreate' | this_AI_DEF_OPTION_NAME_1= ruleAI_DEF_OPTION_NAME )
            {
            // InternalAtsDsl.g:4085:1: (kw= 'GetOrCreate' | this_AI_DEF_OPTION_NAME_1= ruleAI_DEF_OPTION_NAME )
            int alt103=2;
            int LA103_0 = input.LA(1);

            if ( (LA103_0==83) ) {
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
                    // InternalAtsDsl.g:4086:2: kw= 'GetOrCreate'
                    {
                    kw=(Token)match(input,83,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getActionableItemOptionAccess().getGetOrCreateKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // InternalAtsDsl.g:4093:5: this_AI_DEF_OPTION_NAME_1= ruleAI_DEF_OPTION_NAME
                    {
                     
                            newCompositeNode(grammarAccess.getActionableItemOptionAccess().getAI_DEF_OPTION_NAMEParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_2);
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
    // InternalAtsDsl.g:4111:1: entryRuleCOMPOSITE_OPTION_NAME returns [String current=null] : iv_ruleCOMPOSITE_OPTION_NAME= ruleCOMPOSITE_OPTION_NAME EOF ;
    public final String entryRuleCOMPOSITE_OPTION_NAME() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleCOMPOSITE_OPTION_NAME = null;


        try {
            // InternalAtsDsl.g:4112:2: (iv_ruleCOMPOSITE_OPTION_NAME= ruleCOMPOSITE_OPTION_NAME EOF )
            // InternalAtsDsl.g:4113:2: iv_ruleCOMPOSITE_OPTION_NAME= ruleCOMPOSITE_OPTION_NAME EOF
            {
             newCompositeNode(grammarAccess.getCOMPOSITE_OPTION_NAMERule()); 
            pushFollow(FOLLOW_1);
            iv_ruleCOMPOSITE_OPTION_NAME=ruleCOMPOSITE_OPTION_NAME();

            state._fsp--;

             current =iv_ruleCOMPOSITE_OPTION_NAME.getText(); 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:4120:1: ruleCOMPOSITE_OPTION_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleCOMPOSITE_OPTION_NAME() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // InternalAtsDsl.g:4123:28: (this_STRING_0= RULE_STRING )
            // InternalAtsDsl.g:4124:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_2); 

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
    // InternalAtsDsl.g:4139:1: entryRuleCompositeOption returns [String current=null] : iv_ruleCompositeOption= ruleCompositeOption EOF ;
    public final String entryRuleCompositeOption() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleCompositeOption = null;


        try {
            // InternalAtsDsl.g:4140:2: (iv_ruleCompositeOption= ruleCompositeOption EOF )
            // InternalAtsDsl.g:4141:2: iv_ruleCompositeOption= ruleCompositeOption EOF
            {
             newCompositeNode(grammarAccess.getCompositeOptionRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleCompositeOption=ruleCompositeOption();

            state._fsp--;

             current =iv_ruleCompositeOption.getText(); 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:4148:1: ruleCompositeOption returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'None' | this_COMPOSITE_OPTION_NAME_1= ruleCOMPOSITE_OPTION_NAME ) ;
    public final AntlrDatatypeRuleToken ruleCompositeOption() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        AntlrDatatypeRuleToken this_COMPOSITE_OPTION_NAME_1 = null;


         enterRule(); 
            
        try {
            // InternalAtsDsl.g:4151:28: ( (kw= 'None' | this_COMPOSITE_OPTION_NAME_1= ruleCOMPOSITE_OPTION_NAME ) )
            // InternalAtsDsl.g:4152:1: (kw= 'None' | this_COMPOSITE_OPTION_NAME_1= ruleCOMPOSITE_OPTION_NAME )
            {
            // InternalAtsDsl.g:4152:1: (kw= 'None' | this_COMPOSITE_OPTION_NAME_1= ruleCOMPOSITE_OPTION_NAME )
            int alt104=2;
            int LA104_0 = input.LA(1);

            if ( (LA104_0==84) ) {
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
                    // InternalAtsDsl.g:4153:2: kw= 'None'
                    {
                    kw=(Token)match(input,84,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getCompositeOptionAccess().getNoneKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // InternalAtsDsl.g:4160:5: this_COMPOSITE_OPTION_NAME_1= ruleCOMPOSITE_OPTION_NAME
                    {
                     
                            newCompositeNode(grammarAccess.getCompositeOptionAccess().getCOMPOSITE_OPTION_NAMEParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_2);
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
    // InternalAtsDsl.g:4178:1: entryRuleTRANSITION_OPTION_NAME returns [String current=null] : iv_ruleTRANSITION_OPTION_NAME= ruleTRANSITION_OPTION_NAME EOF ;
    public final String entryRuleTRANSITION_OPTION_NAME() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleTRANSITION_OPTION_NAME = null;


        try {
            // InternalAtsDsl.g:4179:2: (iv_ruleTRANSITION_OPTION_NAME= ruleTRANSITION_OPTION_NAME EOF )
            // InternalAtsDsl.g:4180:2: iv_ruleTRANSITION_OPTION_NAME= ruleTRANSITION_OPTION_NAME EOF
            {
             newCompositeNode(grammarAccess.getTRANSITION_OPTION_NAMERule()); 
            pushFollow(FOLLOW_1);
            iv_ruleTRANSITION_OPTION_NAME=ruleTRANSITION_OPTION_NAME();

            state._fsp--;

             current =iv_ruleTRANSITION_OPTION_NAME.getText(); 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:4187:1: ruleTRANSITION_OPTION_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleTRANSITION_OPTION_NAME() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // InternalAtsDsl.g:4190:28: (this_STRING_0= RULE_STRING )
            // InternalAtsDsl.g:4191:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_2); 

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
    // InternalAtsDsl.g:4206:1: entryRuleTransitionOption returns [String current=null] : iv_ruleTransitionOption= ruleTransitionOption EOF ;
    public final String entryRuleTransitionOption() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleTransitionOption = null;


        try {
            // InternalAtsDsl.g:4207:2: (iv_ruleTransitionOption= ruleTransitionOption EOF )
            // InternalAtsDsl.g:4208:2: iv_ruleTransitionOption= ruleTransitionOption EOF
            {
             newCompositeNode(grammarAccess.getTransitionOptionRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleTransitionOption=ruleTransitionOption();

            state._fsp--;

             current =iv_ruleTransitionOption.getText(); 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:4215:1: ruleTransitionOption returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'AsDefault' | kw= 'OverrideAttributeValidation' | this_TRANSITION_OPTION_NAME_2= ruleTRANSITION_OPTION_NAME ) ;
    public final AntlrDatatypeRuleToken ruleTransitionOption() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        AntlrDatatypeRuleToken this_TRANSITION_OPTION_NAME_2 = null;


         enterRule(); 
            
        try {
            // InternalAtsDsl.g:4218:28: ( (kw= 'AsDefault' | kw= 'OverrideAttributeValidation' | this_TRANSITION_OPTION_NAME_2= ruleTRANSITION_OPTION_NAME ) )
            // InternalAtsDsl.g:4219:1: (kw= 'AsDefault' | kw= 'OverrideAttributeValidation' | this_TRANSITION_OPTION_NAME_2= ruleTRANSITION_OPTION_NAME )
            {
            // InternalAtsDsl.g:4219:1: (kw= 'AsDefault' | kw= 'OverrideAttributeValidation' | this_TRANSITION_OPTION_NAME_2= ruleTRANSITION_OPTION_NAME )
            int alt105=3;
            switch ( input.LA(1) ) {
            case 85:
                {
                alt105=1;
                }
                break;
            case 86:
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
                    // InternalAtsDsl.g:4220:2: kw= 'AsDefault'
                    {
                    kw=(Token)match(input,85,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getTransitionOptionAccess().getAsDefaultKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // InternalAtsDsl.g:4227:2: kw= 'OverrideAttributeValidation'
                    {
                    kw=(Token)match(input,86,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getTransitionOptionAccess().getOverrideAttributeValidationKeyword_1()); 
                        

                    }
                    break;
                case 3 :
                    // InternalAtsDsl.g:4234:5: this_TRANSITION_OPTION_NAME_2= ruleTRANSITION_OPTION_NAME
                    {
                     
                            newCompositeNode(grammarAccess.getTransitionOptionAccess().getTRANSITION_OPTION_NAMEParserRuleCall_2()); 
                        
                    pushFollow(FOLLOW_2);
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


    // $ANTLR start "entryRuleRULE_NAME_REFERENCE"
    // InternalAtsDsl.g:4252:1: entryRuleRULE_NAME_REFERENCE returns [String current=null] : iv_ruleRULE_NAME_REFERENCE= ruleRULE_NAME_REFERENCE EOF ;
    public final String entryRuleRULE_NAME_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleRULE_NAME_REFERENCE = null;


        try {
            // InternalAtsDsl.g:4253:2: (iv_ruleRULE_NAME_REFERENCE= ruleRULE_NAME_REFERENCE EOF )
            // InternalAtsDsl.g:4254:2: iv_ruleRULE_NAME_REFERENCE= ruleRULE_NAME_REFERENCE EOF
            {
             newCompositeNode(grammarAccess.getRULE_NAME_REFERENCERule()); 
            pushFollow(FOLLOW_1);
            iv_ruleRULE_NAME_REFERENCE=ruleRULE_NAME_REFERENCE();

            state._fsp--;

             current =iv_ruleRULE_NAME_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_2); 

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
    // $ANTLR end "entryRuleRULE_NAME_REFERENCE"


    // $ANTLR start "ruleRULE_NAME_REFERENCE"
    // InternalAtsDsl.g:4261:1: ruleRULE_NAME_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleRULE_NAME_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // InternalAtsDsl.g:4264:28: (this_STRING_0= RULE_STRING )
            // InternalAtsDsl.g:4265:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_2); 

            		current.merge(this_STRING_0);
                
             
                newLeafNode(this_STRING_0, grammarAccess.getRULE_NAME_REFERENCEAccess().getSTRINGTerminalRuleCall()); 
                

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
    // $ANTLR end "ruleRULE_NAME_REFERENCE"


    // $ANTLR start "entryRuleRuleDef"
    // InternalAtsDsl.g:4280:1: entryRuleRuleDef returns [EObject current=null] : iv_ruleRuleDef= ruleRuleDef EOF ;
    public final EObject entryRuleRuleDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRuleDef = null;


        try {
            // InternalAtsDsl.g:4281:2: (iv_ruleRuleDef= ruleRuleDef EOF )
            // InternalAtsDsl.g:4282:2: iv_ruleRuleDef= ruleRuleDef EOF
            {
             newCompositeNode(grammarAccess.getRuleDefRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleRuleDef=ruleRuleDef();

            state._fsp--;

             current =iv_ruleRuleDef; 
            match(input,EOF,FOLLOW_2); 

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
    // $ANTLR end "entryRuleRuleDef"


    // $ANTLR start "ruleRuleDef"
    // InternalAtsDsl.g:4289:1: ruleRuleDef returns [EObject current=null] : ( (otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) ) (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) ) )+ otherlv_9= '}' ) ;
    public final EObject ruleRuleDef() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_2=null;
        Token otherlv_3=null;
        Token lv_title_4_0=null;
        Token otherlv_5=null;
        Token lv_description_6_0=null;
        Token otherlv_7=null;
        Token otherlv_9=null;
        AntlrDatatypeRuleToken lv_name_1_0 = null;

        Enumerator lv_ruleLocation_8_0 = null;


         enterRule(); 
            
        try {
            // InternalAtsDsl.g:4292:28: ( ( (otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) ) (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) ) )+ otherlv_9= '}' ) )
            // InternalAtsDsl.g:4293:1: ( (otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) ) (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) ) )+ otherlv_9= '}' )
            {
            // InternalAtsDsl.g:4293:1: ( (otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) ) (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) ) )+ otherlv_9= '}' )
            // InternalAtsDsl.g:4293:2: (otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) ) (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) ) )+ otherlv_9= '}'
            {
            // InternalAtsDsl.g:4293:2: (otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) ) )
            // InternalAtsDsl.g:4293:4: otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) )
            {
            otherlv_0=(Token)match(input,87,FOLLOW_41); 

                	newLeafNode(otherlv_0, grammarAccess.getRuleDefAccess().getNameKeyword_0_0());
                
            // InternalAtsDsl.g:4297:1: ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) )
            // InternalAtsDsl.g:4298:1: (lv_name_1_0= ruleRULE_NAME_REFERENCE )
            {
            // InternalAtsDsl.g:4298:1: (lv_name_1_0= ruleRULE_NAME_REFERENCE )
            // InternalAtsDsl.g:4299:3: lv_name_1_0= ruleRULE_NAME_REFERENCE
            {
             
            	        newCompositeNode(grammarAccess.getRuleDefAccess().getNameRULE_NAME_REFERENCEParserRuleCall_0_1_0()); 
            	    
            pushFollow(FOLLOW_43);
            lv_name_1_0=ruleRULE_NAME_REFERENCE();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getRuleDefRule());
            	        }
                   		set(
                   			current, 
                   			"name",
                    		lv_name_1_0, 
                    		"org.eclipse.osee.ats.dsl.AtsDsl.RULE_NAME_REFERENCE");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }


            }

            otherlv_2=(Token)match(input,17,FOLLOW_87); 

                	newLeafNode(otherlv_2, grammarAccess.getRuleDefAccess().getLeftCurlyBracketKeyword_1());
                
            // InternalAtsDsl.g:4319:1: (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) )
            // InternalAtsDsl.g:4319:3: otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) )
            {
            otherlv_3=(Token)match(input,67,FOLLOW_3); 

                	newLeafNode(otherlv_3, grammarAccess.getRuleDefAccess().getTitleKeyword_2_0());
                
            // InternalAtsDsl.g:4323:1: ( (lv_title_4_0= RULE_STRING ) )
            // InternalAtsDsl.g:4324:1: (lv_title_4_0= RULE_STRING )
            {
            // InternalAtsDsl.g:4324:1: (lv_title_4_0= RULE_STRING )
            // InternalAtsDsl.g:4325:3: lv_title_4_0= RULE_STRING
            {
            lv_title_4_0=(Token)match(input,RULE_STRING,FOLLOW_109); 

            			newLeafNode(lv_title_4_0, grammarAccess.getRuleDefAccess().getTitleSTRINGTerminalRuleCall_2_1_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getRuleDefRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"title",
                    		lv_title_4_0, 
                    		"org.eclipse.xtext.common.Terminals.STRING");
            	    

            }


            }


            }

            // InternalAtsDsl.g:4341:3: (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )?
            int alt106=2;
            int LA106_0 = input.LA(1);

            if ( (LA106_0==49) ) {
                alt106=1;
            }
            switch (alt106) {
                case 1 :
                    // InternalAtsDsl.g:4341:5: otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) )
                    {
                    otherlv_5=(Token)match(input,49,FOLLOW_3); 

                        	newLeafNode(otherlv_5, grammarAccess.getRuleDefAccess().getDescriptionKeyword_3_0());
                        
                    // InternalAtsDsl.g:4345:1: ( (lv_description_6_0= RULE_STRING ) )
                    // InternalAtsDsl.g:4346:1: (lv_description_6_0= RULE_STRING )
                    {
                    // InternalAtsDsl.g:4346:1: (lv_description_6_0= RULE_STRING )
                    // InternalAtsDsl.g:4347:3: lv_description_6_0= RULE_STRING
                    {
                    lv_description_6_0=(Token)match(input,RULE_STRING,FOLLOW_110); 

                    			newLeafNode(lv_description_6_0, grammarAccess.getRuleDefAccess().getDescriptionSTRINGTerminalRuleCall_3_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getRuleDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"description",
                            		lv_description_6_0, 
                            		"org.eclipse.xtext.common.Terminals.STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            // InternalAtsDsl.g:4363:4: (otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) ) )+
            int cnt107=0;
            loop107:
            do {
                int alt107=2;
                int LA107_0 = input.LA(1);

                if ( (LA107_0==88) ) {
                    alt107=1;
                }


                switch (alt107) {
            	case 1 :
            	    // InternalAtsDsl.g:4363:6: otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) )
            	    {
            	    otherlv_7=(Token)match(input,88,FOLLOW_111); 

            	        	newLeafNode(otherlv_7, grammarAccess.getRuleDefAccess().getRuleLocationKeyword_4_0());
            	        
            	    // InternalAtsDsl.g:4367:1: ( (lv_ruleLocation_8_0= ruleRuleLocation ) )
            	    // InternalAtsDsl.g:4368:1: (lv_ruleLocation_8_0= ruleRuleLocation )
            	    {
            	    // InternalAtsDsl.g:4368:1: (lv_ruleLocation_8_0= ruleRuleLocation )
            	    // InternalAtsDsl.g:4369:3: lv_ruleLocation_8_0= ruleRuleLocation
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getRuleDefAccess().getRuleLocationRuleLocationEnumRuleCall_4_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_112);
            	    lv_ruleLocation_8_0=ruleRuleLocation();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getRuleDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"ruleLocation",
            	            		lv_ruleLocation_8_0, 
            	            		"org.eclipse.osee.ats.dsl.AtsDsl.RuleLocation");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt107 >= 1 ) break loop107;
                        EarlyExitException eee =
                            new EarlyExitException(107, input);
                        throw eee;
                }
                cnt107++;
            } while (true);

            otherlv_9=(Token)match(input,22,FOLLOW_2); 

                	newLeafNode(otherlv_9, grammarAccess.getRuleDefAccess().getRightCurlyBracketKeyword_5());
                

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
    // $ANTLR end "ruleRuleDef"


    // $ANTLR start "entryRuleCreateTaskRuleDef"
    // InternalAtsDsl.g:4397:1: entryRuleCreateTaskRuleDef returns [EObject current=null] : iv_ruleCreateTaskRuleDef= ruleCreateTaskRuleDef EOF ;
    public final EObject entryRuleCreateTaskRuleDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleCreateTaskRuleDef = null;


        try {
            // InternalAtsDsl.g:4398:2: (iv_ruleCreateTaskRuleDef= ruleCreateTaskRuleDef EOF )
            // InternalAtsDsl.g:4399:2: iv_ruleCreateTaskRuleDef= ruleCreateTaskRuleDef EOF
            {
             newCompositeNode(grammarAccess.getCreateTaskRuleDefRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleCreateTaskRuleDef=ruleCreateTaskRuleDef();

            state._fsp--;

             current =iv_ruleCreateTaskRuleDef; 
            match(input,EOF,FOLLOW_2); 

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
    // $ANTLR end "entryRuleCreateTaskRuleDef"


    // $ANTLR start "ruleCreateTaskRuleDef"
    // InternalAtsDsl.g:4406:1: ruleCreateTaskRuleDef returns [EObject current=null] : ( (otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) ) (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) ) )+ (otherlv_9= 'assignees' ( (lv_assignees_10_0= ruleUserDef ) ) )* (otherlv_11= 'relatedState' ( (lv_relatedState_12_0= ruleSTATE_NAME_REFERENCE ) ) )? (otherlv_13= 'taskWorkDef' ( (lv_taskWorkDef_14_0= RULE_STRING ) ) )? (otherlv_15= 'onEvent' ( (lv_onEvent_16_0= ruleOnEventType ) ) )* (otherlv_17= 'attribute' ( (lv_attributes_18_0= ruleAttrDef ) ) )* otherlv_19= '}' ) ;
    public final EObject ruleCreateTaskRuleDef() throws RecognitionException {
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
        Token lv_taskWorkDef_14_0=null;
        Token otherlv_15=null;
        Token otherlv_17=null;
        Token otherlv_19=null;
        AntlrDatatypeRuleToken lv_name_1_0 = null;

        Enumerator lv_ruleLocation_8_0 = null;

        EObject lv_assignees_10_0 = null;

        AntlrDatatypeRuleToken lv_relatedState_12_0 = null;

        Enumerator lv_onEvent_16_0 = null;

        EObject lv_attributes_18_0 = null;


         enterRule(); 
            
        try {
            // InternalAtsDsl.g:4409:28: ( ( (otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) ) (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) ) )+ (otherlv_9= 'assignees' ( (lv_assignees_10_0= ruleUserDef ) ) )* (otherlv_11= 'relatedState' ( (lv_relatedState_12_0= ruleSTATE_NAME_REFERENCE ) ) )? (otherlv_13= 'taskWorkDef' ( (lv_taskWorkDef_14_0= RULE_STRING ) ) )? (otherlv_15= 'onEvent' ( (lv_onEvent_16_0= ruleOnEventType ) ) )* (otherlv_17= 'attribute' ( (lv_attributes_18_0= ruleAttrDef ) ) )* otherlv_19= '}' ) )
            // InternalAtsDsl.g:4410:1: ( (otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) ) (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) ) )+ (otherlv_9= 'assignees' ( (lv_assignees_10_0= ruleUserDef ) ) )* (otherlv_11= 'relatedState' ( (lv_relatedState_12_0= ruleSTATE_NAME_REFERENCE ) ) )? (otherlv_13= 'taskWorkDef' ( (lv_taskWorkDef_14_0= RULE_STRING ) ) )? (otherlv_15= 'onEvent' ( (lv_onEvent_16_0= ruleOnEventType ) ) )* (otherlv_17= 'attribute' ( (lv_attributes_18_0= ruleAttrDef ) ) )* otherlv_19= '}' )
            {
            // InternalAtsDsl.g:4410:1: ( (otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) ) (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) ) )+ (otherlv_9= 'assignees' ( (lv_assignees_10_0= ruleUserDef ) ) )* (otherlv_11= 'relatedState' ( (lv_relatedState_12_0= ruleSTATE_NAME_REFERENCE ) ) )? (otherlv_13= 'taskWorkDef' ( (lv_taskWorkDef_14_0= RULE_STRING ) ) )? (otherlv_15= 'onEvent' ( (lv_onEvent_16_0= ruleOnEventType ) ) )* (otherlv_17= 'attribute' ( (lv_attributes_18_0= ruleAttrDef ) ) )* otherlv_19= '}' )
            // InternalAtsDsl.g:4410:2: (otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) ) (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) ) )+ (otherlv_9= 'assignees' ( (lv_assignees_10_0= ruleUserDef ) ) )* (otherlv_11= 'relatedState' ( (lv_relatedState_12_0= ruleSTATE_NAME_REFERENCE ) ) )? (otherlv_13= 'taskWorkDef' ( (lv_taskWorkDef_14_0= RULE_STRING ) ) )? (otherlv_15= 'onEvent' ( (lv_onEvent_16_0= ruleOnEventType ) ) )* (otherlv_17= 'attribute' ( (lv_attributes_18_0= ruleAttrDef ) ) )* otherlv_19= '}'
            {
            // InternalAtsDsl.g:4410:2: (otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) ) )
            // InternalAtsDsl.g:4410:4: otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) )
            {
            otherlv_0=(Token)match(input,87,FOLLOW_41); 

                	newLeafNode(otherlv_0, grammarAccess.getCreateTaskRuleDefAccess().getNameKeyword_0_0());
                
            // InternalAtsDsl.g:4414:1: ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) )
            // InternalAtsDsl.g:4415:1: (lv_name_1_0= ruleRULE_NAME_REFERENCE )
            {
            // InternalAtsDsl.g:4415:1: (lv_name_1_0= ruleRULE_NAME_REFERENCE )
            // InternalAtsDsl.g:4416:3: lv_name_1_0= ruleRULE_NAME_REFERENCE
            {
             
            	        newCompositeNode(grammarAccess.getCreateTaskRuleDefAccess().getNameRULE_NAME_REFERENCEParserRuleCall_0_1_0()); 
            	    
            pushFollow(FOLLOW_43);
            lv_name_1_0=ruleRULE_NAME_REFERENCE();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getCreateTaskRuleDefRule());
            	        }
                   		set(
                   			current, 
                   			"name",
                    		lv_name_1_0, 
                    		"org.eclipse.osee.ats.dsl.AtsDsl.RULE_NAME_REFERENCE");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }


            }

            otherlv_2=(Token)match(input,17,FOLLOW_87); 

                	newLeafNode(otherlv_2, grammarAccess.getCreateTaskRuleDefAccess().getLeftCurlyBracketKeyword_1());
                
            // InternalAtsDsl.g:4436:1: (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) )
            // InternalAtsDsl.g:4436:3: otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) )
            {
            otherlv_3=(Token)match(input,67,FOLLOW_3); 

                	newLeafNode(otherlv_3, grammarAccess.getCreateTaskRuleDefAccess().getTitleKeyword_2_0());
                
            // InternalAtsDsl.g:4440:1: ( (lv_title_4_0= RULE_STRING ) )
            // InternalAtsDsl.g:4441:1: (lv_title_4_0= RULE_STRING )
            {
            // InternalAtsDsl.g:4441:1: (lv_title_4_0= RULE_STRING )
            // InternalAtsDsl.g:4442:3: lv_title_4_0= RULE_STRING
            {
            lv_title_4_0=(Token)match(input,RULE_STRING,FOLLOW_109); 

            			newLeafNode(lv_title_4_0, grammarAccess.getCreateTaskRuleDefAccess().getTitleSTRINGTerminalRuleCall_2_1_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getCreateTaskRuleDefRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"title",
                    		lv_title_4_0, 
                    		"org.eclipse.xtext.common.Terminals.STRING");
            	    

            }


            }


            }

            // InternalAtsDsl.g:4458:3: (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )?
            int alt108=2;
            int LA108_0 = input.LA(1);

            if ( (LA108_0==49) ) {
                alt108=1;
            }
            switch (alt108) {
                case 1 :
                    // InternalAtsDsl.g:4458:5: otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) )
                    {
                    otherlv_5=(Token)match(input,49,FOLLOW_3); 

                        	newLeafNode(otherlv_5, grammarAccess.getCreateTaskRuleDefAccess().getDescriptionKeyword_3_0());
                        
                    // InternalAtsDsl.g:4462:1: ( (lv_description_6_0= RULE_STRING ) )
                    // InternalAtsDsl.g:4463:1: (lv_description_6_0= RULE_STRING )
                    {
                    // InternalAtsDsl.g:4463:1: (lv_description_6_0= RULE_STRING )
                    // InternalAtsDsl.g:4464:3: lv_description_6_0= RULE_STRING
                    {
                    lv_description_6_0=(Token)match(input,RULE_STRING,FOLLOW_110); 

                    			newLeafNode(lv_description_6_0, grammarAccess.getCreateTaskRuleDefAccess().getDescriptionSTRINGTerminalRuleCall_3_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getCreateTaskRuleDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"description",
                            		lv_description_6_0, 
                            		"org.eclipse.xtext.common.Terminals.STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            // InternalAtsDsl.g:4480:4: (otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) ) )+
            int cnt109=0;
            loop109:
            do {
                int alt109=2;
                int LA109_0 = input.LA(1);

                if ( (LA109_0==88) ) {
                    alt109=1;
                }


                switch (alt109) {
            	case 1 :
            	    // InternalAtsDsl.g:4480:6: otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) )
            	    {
            	    otherlv_7=(Token)match(input,88,FOLLOW_111); 

            	        	newLeafNode(otherlv_7, grammarAccess.getCreateTaskRuleDefAccess().getRuleLocationKeyword_4_0());
            	        
            	    // InternalAtsDsl.g:4484:1: ( (lv_ruleLocation_8_0= ruleRuleLocation ) )
            	    // InternalAtsDsl.g:4485:1: (lv_ruleLocation_8_0= ruleRuleLocation )
            	    {
            	    // InternalAtsDsl.g:4485:1: (lv_ruleLocation_8_0= ruleRuleLocation )
            	    // InternalAtsDsl.g:4486:3: lv_ruleLocation_8_0= ruleRuleLocation
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getCreateTaskRuleDefAccess().getRuleLocationRuleLocationEnumRuleCall_4_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_113);
            	    lv_ruleLocation_8_0=ruleRuleLocation();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getCreateTaskRuleDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"ruleLocation",
            	            		lv_ruleLocation_8_0, 
            	            		"org.eclipse.osee.ats.dsl.AtsDsl.RuleLocation");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt109 >= 1 ) break loop109;
                        EarlyExitException eee =
                            new EarlyExitException(109, input);
                        throw eee;
                }
                cnt109++;
            } while (true);

            // InternalAtsDsl.g:4502:4: (otherlv_9= 'assignees' ( (lv_assignees_10_0= ruleUserDef ) ) )*
            loop110:
            do {
                int alt110=2;
                int LA110_0 = input.LA(1);

                if ( (LA110_0==89) ) {
                    alt110=1;
                }


                switch (alt110) {
            	case 1 :
            	    // InternalAtsDsl.g:4502:6: otherlv_9= 'assignees' ( (lv_assignees_10_0= ruleUserDef ) )
            	    {
            	    otherlv_9=(Token)match(input,89,FOLLOW_3); 

            	        	newLeafNode(otherlv_9, grammarAccess.getCreateTaskRuleDefAccess().getAssigneesKeyword_5_0());
            	        
            	    // InternalAtsDsl.g:4506:1: ( (lv_assignees_10_0= ruleUserDef ) )
            	    // InternalAtsDsl.g:4507:1: (lv_assignees_10_0= ruleUserDef )
            	    {
            	    // InternalAtsDsl.g:4507:1: (lv_assignees_10_0= ruleUserDef )
            	    // InternalAtsDsl.g:4508:3: lv_assignees_10_0= ruleUserDef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getCreateTaskRuleDefAccess().getAssigneesUserDefParserRuleCall_5_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_114);
            	    lv_assignees_10_0=ruleUserDef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getCreateTaskRuleDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"assignees",
            	            		lv_assignees_10_0, 
            	            		"org.eclipse.osee.ats.dsl.AtsDsl.UserDef");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop110;
                }
            } while (true);

            // InternalAtsDsl.g:4524:4: (otherlv_11= 'relatedState' ( (lv_relatedState_12_0= ruleSTATE_NAME_REFERENCE ) ) )?
            int alt111=2;
            int LA111_0 = input.LA(1);

            if ( (LA111_0==90) ) {
                alt111=1;
            }
            switch (alt111) {
                case 1 :
                    // InternalAtsDsl.g:4524:6: otherlv_11= 'relatedState' ( (lv_relatedState_12_0= ruleSTATE_NAME_REFERENCE ) )
                    {
                    otherlv_11=(Token)match(input,90,FOLLOW_3); 

                        	newLeafNode(otherlv_11, grammarAccess.getCreateTaskRuleDefAccess().getRelatedStateKeyword_6_0());
                        
                    // InternalAtsDsl.g:4528:1: ( (lv_relatedState_12_0= ruleSTATE_NAME_REFERENCE ) )
                    // InternalAtsDsl.g:4529:1: (lv_relatedState_12_0= ruleSTATE_NAME_REFERENCE )
                    {
                    // InternalAtsDsl.g:4529:1: (lv_relatedState_12_0= ruleSTATE_NAME_REFERENCE )
                    // InternalAtsDsl.g:4530:3: lv_relatedState_12_0= ruleSTATE_NAME_REFERENCE
                    {
                     
                    	        newCompositeNode(grammarAccess.getCreateTaskRuleDefAccess().getRelatedStateSTATE_NAME_REFERENCEParserRuleCall_6_1_0()); 
                    	    
                    pushFollow(FOLLOW_115);
                    lv_relatedState_12_0=ruleSTATE_NAME_REFERENCE();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getCreateTaskRuleDefRule());
                    	        }
                           		set(
                           			current, 
                           			"relatedState",
                            		lv_relatedState_12_0, 
                            		"org.eclipse.osee.ats.dsl.AtsDsl.STATE_NAME_REFERENCE");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }


                    }
                    break;

            }

            // InternalAtsDsl.g:4546:4: (otherlv_13= 'taskWorkDef' ( (lv_taskWorkDef_14_0= RULE_STRING ) ) )?
            int alt112=2;
            int LA112_0 = input.LA(1);

            if ( (LA112_0==91) ) {
                alt112=1;
            }
            switch (alt112) {
                case 1 :
                    // InternalAtsDsl.g:4546:6: otherlv_13= 'taskWorkDef' ( (lv_taskWorkDef_14_0= RULE_STRING ) )
                    {
                    otherlv_13=(Token)match(input,91,FOLLOW_3); 

                        	newLeafNode(otherlv_13, grammarAccess.getCreateTaskRuleDefAccess().getTaskWorkDefKeyword_7_0());
                        
                    // InternalAtsDsl.g:4550:1: ( (lv_taskWorkDef_14_0= RULE_STRING ) )
                    // InternalAtsDsl.g:4551:1: (lv_taskWorkDef_14_0= RULE_STRING )
                    {
                    // InternalAtsDsl.g:4551:1: (lv_taskWorkDef_14_0= RULE_STRING )
                    // InternalAtsDsl.g:4552:3: lv_taskWorkDef_14_0= RULE_STRING
                    {
                    lv_taskWorkDef_14_0=(Token)match(input,RULE_STRING,FOLLOW_116); 

                    			newLeafNode(lv_taskWorkDef_14_0, grammarAccess.getCreateTaskRuleDefAccess().getTaskWorkDefSTRINGTerminalRuleCall_7_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getCreateTaskRuleDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"taskWorkDef",
                            		lv_taskWorkDef_14_0, 
                            		"org.eclipse.xtext.common.Terminals.STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            // InternalAtsDsl.g:4568:4: (otherlv_15= 'onEvent' ( (lv_onEvent_16_0= ruleOnEventType ) ) )*
            loop113:
            do {
                int alt113=2;
                int LA113_0 = input.LA(1);

                if ( (LA113_0==70) ) {
                    alt113=1;
                }


                switch (alt113) {
            	case 1 :
            	    // InternalAtsDsl.g:4568:6: otherlv_15= 'onEvent' ( (lv_onEvent_16_0= ruleOnEventType ) )
            	    {
            	    otherlv_15=(Token)match(input,70,FOLLOW_117); 

            	        	newLeafNode(otherlv_15, grammarAccess.getCreateTaskRuleDefAccess().getOnEventKeyword_8_0());
            	        
            	    // InternalAtsDsl.g:4572:1: ( (lv_onEvent_16_0= ruleOnEventType ) )
            	    // InternalAtsDsl.g:4573:1: (lv_onEvent_16_0= ruleOnEventType )
            	    {
            	    // InternalAtsDsl.g:4573:1: (lv_onEvent_16_0= ruleOnEventType )
            	    // InternalAtsDsl.g:4574:3: lv_onEvent_16_0= ruleOnEventType
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getCreateTaskRuleDefAccess().getOnEventOnEventTypeEnumRuleCall_8_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_116);
            	    lv_onEvent_16_0=ruleOnEventType();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getCreateTaskRuleDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"onEvent",
            	            		lv_onEvent_16_0, 
            	            		"org.eclipse.osee.ats.dsl.AtsDsl.OnEventType");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop113;
                }
            } while (true);

            // InternalAtsDsl.g:4590:4: (otherlv_17= 'attribute' ( (lv_attributes_18_0= ruleAttrDef ) ) )*
            loop114:
            do {
                int alt114=2;
                int LA114_0 = input.LA(1);

                if ( (LA114_0==27) ) {
                    alt114=1;
                }


                switch (alt114) {
            	case 1 :
            	    // InternalAtsDsl.g:4590:6: otherlv_17= 'attribute' ( (lv_attributes_18_0= ruleAttrDef ) )
            	    {
            	    otherlv_17=(Token)match(input,27,FOLLOW_3); 

            	        	newLeafNode(otherlv_17, grammarAccess.getCreateTaskRuleDefAccess().getAttributeKeyword_9_0());
            	        
            	    // InternalAtsDsl.g:4594:1: ( (lv_attributes_18_0= ruleAttrDef ) )
            	    // InternalAtsDsl.g:4595:1: (lv_attributes_18_0= ruleAttrDef )
            	    {
            	    // InternalAtsDsl.g:4595:1: (lv_attributes_18_0= ruleAttrDef )
            	    // InternalAtsDsl.g:4596:3: lv_attributes_18_0= ruleAttrDef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getCreateTaskRuleDefAccess().getAttributesAttrDefParserRuleCall_9_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_29);
            	    lv_attributes_18_0=ruleAttrDef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getCreateTaskRuleDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"attributes",
            	            		lv_attributes_18_0, 
            	            		"org.eclipse.osee.ats.dsl.AtsDsl.AttrDef");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop114;
                }
            } while (true);

            otherlv_19=(Token)match(input,22,FOLLOW_2); 

                	newLeafNode(otherlv_19, grammarAccess.getCreateTaskRuleDefAccess().getRightCurlyBracketKeyword_10());
                

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
    // $ANTLR end "ruleCreateTaskRuleDef"


    // $ANTLR start "entryRuleCreateDecisionReviewRuleDef"
    // InternalAtsDsl.g:4624:1: entryRuleCreateDecisionReviewRuleDef returns [EObject current=null] : iv_ruleCreateDecisionReviewRuleDef= ruleCreateDecisionReviewRuleDef EOF ;
    public final EObject entryRuleCreateDecisionReviewRuleDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleCreateDecisionReviewRuleDef = null;


        try {
            // InternalAtsDsl.g:4625:2: (iv_ruleCreateDecisionReviewRuleDef= ruleCreateDecisionReviewRuleDef EOF )
            // InternalAtsDsl.g:4626:2: iv_ruleCreateDecisionReviewRuleDef= ruleCreateDecisionReviewRuleDef EOF
            {
             newCompositeNode(grammarAccess.getCreateDecisionReviewRuleDefRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleCreateDecisionReviewRuleDef=ruleCreateDecisionReviewRuleDef();

            state._fsp--;

             current =iv_ruleCreateDecisionReviewRuleDef; 
            match(input,EOF,FOLLOW_2); 

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
    // $ANTLR end "entryRuleCreateDecisionReviewRuleDef"


    // $ANTLR start "ruleCreateDecisionReviewRuleDef"
    // InternalAtsDsl.g:4633:1: ruleCreateDecisionReviewRuleDef returns [EObject current=null] : ( (otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) ) (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) ) )+ (otherlv_9= 'assignees' ( (lv_assignees_10_0= ruleUserDef ) ) )* (otherlv_11= 'relatedToState' ( (lv_relatedToState_12_0= ruleSTATE_NAME_REFERENCE ) ) )? otherlv_13= 'blockingType' ( (lv_blockingType_14_0= ruleReviewBlockingType ) ) otherlv_15= 'onEvent' ( (lv_stateEvent_16_0= ruleWorkflowEventType ) ) (otherlv_17= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_18_0= ruleBooleanDef ) ) ) ( (lv_options_19_0= ruleDecisionReviewOpt ) )+ (otherlv_20= 'attribute' ( (lv_attributes_21_0= ruleAttrDef ) ) )* otherlv_22= '}' ) ;
    public final EObject ruleCreateDecisionReviewRuleDef() throws RecognitionException {
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
        Token otherlv_17=null;
        Token otherlv_20=null;
        Token otherlv_22=null;
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
            // InternalAtsDsl.g:4636:28: ( ( (otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) ) (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) ) )+ (otherlv_9= 'assignees' ( (lv_assignees_10_0= ruleUserDef ) ) )* (otherlv_11= 'relatedToState' ( (lv_relatedToState_12_0= ruleSTATE_NAME_REFERENCE ) ) )? otherlv_13= 'blockingType' ( (lv_blockingType_14_0= ruleReviewBlockingType ) ) otherlv_15= 'onEvent' ( (lv_stateEvent_16_0= ruleWorkflowEventType ) ) (otherlv_17= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_18_0= ruleBooleanDef ) ) ) ( (lv_options_19_0= ruleDecisionReviewOpt ) )+ (otherlv_20= 'attribute' ( (lv_attributes_21_0= ruleAttrDef ) ) )* otherlv_22= '}' ) )
            // InternalAtsDsl.g:4637:1: ( (otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) ) (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) ) )+ (otherlv_9= 'assignees' ( (lv_assignees_10_0= ruleUserDef ) ) )* (otherlv_11= 'relatedToState' ( (lv_relatedToState_12_0= ruleSTATE_NAME_REFERENCE ) ) )? otherlv_13= 'blockingType' ( (lv_blockingType_14_0= ruleReviewBlockingType ) ) otherlv_15= 'onEvent' ( (lv_stateEvent_16_0= ruleWorkflowEventType ) ) (otherlv_17= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_18_0= ruleBooleanDef ) ) ) ( (lv_options_19_0= ruleDecisionReviewOpt ) )+ (otherlv_20= 'attribute' ( (lv_attributes_21_0= ruleAttrDef ) ) )* otherlv_22= '}' )
            {
            // InternalAtsDsl.g:4637:1: ( (otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) ) (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) ) )+ (otherlv_9= 'assignees' ( (lv_assignees_10_0= ruleUserDef ) ) )* (otherlv_11= 'relatedToState' ( (lv_relatedToState_12_0= ruleSTATE_NAME_REFERENCE ) ) )? otherlv_13= 'blockingType' ( (lv_blockingType_14_0= ruleReviewBlockingType ) ) otherlv_15= 'onEvent' ( (lv_stateEvent_16_0= ruleWorkflowEventType ) ) (otherlv_17= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_18_0= ruleBooleanDef ) ) ) ( (lv_options_19_0= ruleDecisionReviewOpt ) )+ (otherlv_20= 'attribute' ( (lv_attributes_21_0= ruleAttrDef ) ) )* otherlv_22= '}' )
            // InternalAtsDsl.g:4637:2: (otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) ) (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) ) )+ (otherlv_9= 'assignees' ( (lv_assignees_10_0= ruleUserDef ) ) )* (otherlv_11= 'relatedToState' ( (lv_relatedToState_12_0= ruleSTATE_NAME_REFERENCE ) ) )? otherlv_13= 'blockingType' ( (lv_blockingType_14_0= ruleReviewBlockingType ) ) otherlv_15= 'onEvent' ( (lv_stateEvent_16_0= ruleWorkflowEventType ) ) (otherlv_17= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_18_0= ruleBooleanDef ) ) ) ( (lv_options_19_0= ruleDecisionReviewOpt ) )+ (otherlv_20= 'attribute' ( (lv_attributes_21_0= ruleAttrDef ) ) )* otherlv_22= '}'
            {
            // InternalAtsDsl.g:4637:2: (otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) ) )
            // InternalAtsDsl.g:4637:4: otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) )
            {
            otherlv_0=(Token)match(input,87,FOLLOW_41); 

                	newLeafNode(otherlv_0, grammarAccess.getCreateDecisionReviewRuleDefAccess().getNameKeyword_0_0());
                
            // InternalAtsDsl.g:4641:1: ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) )
            // InternalAtsDsl.g:4642:1: (lv_name_1_0= ruleRULE_NAME_REFERENCE )
            {
            // InternalAtsDsl.g:4642:1: (lv_name_1_0= ruleRULE_NAME_REFERENCE )
            // InternalAtsDsl.g:4643:3: lv_name_1_0= ruleRULE_NAME_REFERENCE
            {
             
            	        newCompositeNode(grammarAccess.getCreateDecisionReviewRuleDefAccess().getNameRULE_NAME_REFERENCEParserRuleCall_0_1_0()); 
            	    
            pushFollow(FOLLOW_43);
            lv_name_1_0=ruleRULE_NAME_REFERENCE();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getCreateDecisionReviewRuleDefRule());
            	        }
                   		set(
                   			current, 
                   			"name",
                    		lv_name_1_0, 
                    		"org.eclipse.osee.ats.dsl.AtsDsl.RULE_NAME_REFERENCE");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }


            }

            otherlv_2=(Token)match(input,17,FOLLOW_87); 

                	newLeafNode(otherlv_2, grammarAccess.getCreateDecisionReviewRuleDefAccess().getLeftCurlyBracketKeyword_1());
                
            // InternalAtsDsl.g:4663:1: (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) )
            // InternalAtsDsl.g:4663:3: otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) )
            {
            otherlv_3=(Token)match(input,67,FOLLOW_3); 

                	newLeafNode(otherlv_3, grammarAccess.getCreateDecisionReviewRuleDefAccess().getTitleKeyword_2_0());
                
            // InternalAtsDsl.g:4667:1: ( (lv_title_4_0= RULE_STRING ) )
            // InternalAtsDsl.g:4668:1: (lv_title_4_0= RULE_STRING )
            {
            // InternalAtsDsl.g:4668:1: (lv_title_4_0= RULE_STRING )
            // InternalAtsDsl.g:4669:3: lv_title_4_0= RULE_STRING
            {
            lv_title_4_0=(Token)match(input,RULE_STRING,FOLLOW_109); 

            			newLeafNode(lv_title_4_0, grammarAccess.getCreateDecisionReviewRuleDefAccess().getTitleSTRINGTerminalRuleCall_2_1_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getCreateDecisionReviewRuleDefRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"title",
                    		lv_title_4_0, 
                    		"org.eclipse.xtext.common.Terminals.STRING");
            	    

            }


            }


            }

            // InternalAtsDsl.g:4685:3: (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )?
            int alt115=2;
            int LA115_0 = input.LA(1);

            if ( (LA115_0==49) ) {
                alt115=1;
            }
            switch (alt115) {
                case 1 :
                    // InternalAtsDsl.g:4685:5: otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) )
                    {
                    otherlv_5=(Token)match(input,49,FOLLOW_3); 

                        	newLeafNode(otherlv_5, grammarAccess.getCreateDecisionReviewRuleDefAccess().getDescriptionKeyword_3_0());
                        
                    // InternalAtsDsl.g:4689:1: ( (lv_description_6_0= RULE_STRING ) )
                    // InternalAtsDsl.g:4690:1: (lv_description_6_0= RULE_STRING )
                    {
                    // InternalAtsDsl.g:4690:1: (lv_description_6_0= RULE_STRING )
                    // InternalAtsDsl.g:4691:3: lv_description_6_0= RULE_STRING
                    {
                    lv_description_6_0=(Token)match(input,RULE_STRING,FOLLOW_110); 

                    			newLeafNode(lv_description_6_0, grammarAccess.getCreateDecisionReviewRuleDefAccess().getDescriptionSTRINGTerminalRuleCall_3_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getCreateDecisionReviewRuleDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"description",
                            		lv_description_6_0, 
                            		"org.eclipse.xtext.common.Terminals.STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            // InternalAtsDsl.g:4707:4: (otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) ) )+
            int cnt116=0;
            loop116:
            do {
                int alt116=2;
                int LA116_0 = input.LA(1);

                if ( (LA116_0==88) ) {
                    alt116=1;
                }


                switch (alt116) {
            	case 1 :
            	    // InternalAtsDsl.g:4707:6: otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) )
            	    {
            	    otherlv_7=(Token)match(input,88,FOLLOW_111); 

            	        	newLeafNode(otherlv_7, grammarAccess.getCreateDecisionReviewRuleDefAccess().getRuleLocationKeyword_4_0());
            	        
            	    // InternalAtsDsl.g:4711:1: ( (lv_ruleLocation_8_0= ruleRuleLocation ) )
            	    // InternalAtsDsl.g:4712:1: (lv_ruleLocation_8_0= ruleRuleLocation )
            	    {
            	    // InternalAtsDsl.g:4712:1: (lv_ruleLocation_8_0= ruleRuleLocation )
            	    // InternalAtsDsl.g:4713:3: lv_ruleLocation_8_0= ruleRuleLocation
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getCreateDecisionReviewRuleDefAccess().getRuleLocationRuleLocationEnumRuleCall_4_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_118);
            	    lv_ruleLocation_8_0=ruleRuleLocation();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getCreateDecisionReviewRuleDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"ruleLocation",
            	            		lv_ruleLocation_8_0, 
            	            		"org.eclipse.osee.ats.dsl.AtsDsl.RuleLocation");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt116 >= 1 ) break loop116;
                        EarlyExitException eee =
                            new EarlyExitException(116, input);
                        throw eee;
                }
                cnt116++;
            } while (true);

            // InternalAtsDsl.g:4729:4: (otherlv_9= 'assignees' ( (lv_assignees_10_0= ruleUserDef ) ) )*
            loop117:
            do {
                int alt117=2;
                int LA117_0 = input.LA(1);

                if ( (LA117_0==89) ) {
                    alt117=1;
                }


                switch (alt117) {
            	case 1 :
            	    // InternalAtsDsl.g:4729:6: otherlv_9= 'assignees' ( (lv_assignees_10_0= ruleUserDef ) )
            	    {
            	    otherlv_9=(Token)match(input,89,FOLLOW_3); 

            	        	newLeafNode(otherlv_9, grammarAccess.getCreateDecisionReviewRuleDefAccess().getAssigneesKeyword_5_0());
            	        
            	    // InternalAtsDsl.g:4733:1: ( (lv_assignees_10_0= ruleUserDef ) )
            	    // InternalAtsDsl.g:4734:1: (lv_assignees_10_0= ruleUserDef )
            	    {
            	    // InternalAtsDsl.g:4734:1: (lv_assignees_10_0= ruleUserDef )
            	    // InternalAtsDsl.g:4735:3: lv_assignees_10_0= ruleUserDef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getCreateDecisionReviewRuleDefAccess().getAssigneesUserDefParserRuleCall_5_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_119);
            	    lv_assignees_10_0=ruleUserDef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getCreateDecisionReviewRuleDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"assignees",
            	            		lv_assignees_10_0, 
            	            		"org.eclipse.osee.ats.dsl.AtsDsl.UserDef");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop117;
                }
            } while (true);

            // InternalAtsDsl.g:4751:4: (otherlv_11= 'relatedToState' ( (lv_relatedToState_12_0= ruleSTATE_NAME_REFERENCE ) ) )?
            int alt118=2;
            int LA118_0 = input.LA(1);

            if ( (LA118_0==68) ) {
                alt118=1;
            }
            switch (alt118) {
                case 1 :
                    // InternalAtsDsl.g:4751:6: otherlv_11= 'relatedToState' ( (lv_relatedToState_12_0= ruleSTATE_NAME_REFERENCE ) )
                    {
                    otherlv_11=(Token)match(input,68,FOLLOW_3); 

                        	newLeafNode(otherlv_11, grammarAccess.getCreateDecisionReviewRuleDefAccess().getRelatedToStateKeyword_6_0());
                        
                    // InternalAtsDsl.g:4755:1: ( (lv_relatedToState_12_0= ruleSTATE_NAME_REFERENCE ) )
                    // InternalAtsDsl.g:4756:1: (lv_relatedToState_12_0= ruleSTATE_NAME_REFERENCE )
                    {
                    // InternalAtsDsl.g:4756:1: (lv_relatedToState_12_0= ruleSTATE_NAME_REFERENCE )
                    // InternalAtsDsl.g:4757:3: lv_relatedToState_12_0= ruleSTATE_NAME_REFERENCE
                    {
                     
                    	        newCompositeNode(grammarAccess.getCreateDecisionReviewRuleDefAccess().getRelatedToStateSTATE_NAME_REFERENCEParserRuleCall_6_1_0()); 
                    	    
                    pushFollow(FOLLOW_90);
                    lv_relatedToState_12_0=ruleSTATE_NAME_REFERENCE();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getCreateDecisionReviewRuleDefRule());
                    	        }
                           		set(
                           			current, 
                           			"relatedToState",
                            		lv_relatedToState_12_0, 
                            		"org.eclipse.osee.ats.dsl.AtsDsl.STATE_NAME_REFERENCE");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }


                    }
                    break;

            }

            otherlv_13=(Token)match(input,69,FOLLOW_91); 

                	newLeafNode(otherlv_13, grammarAccess.getCreateDecisionReviewRuleDefAccess().getBlockingTypeKeyword_7());
                
            // InternalAtsDsl.g:4777:1: ( (lv_blockingType_14_0= ruleReviewBlockingType ) )
            // InternalAtsDsl.g:4778:1: (lv_blockingType_14_0= ruleReviewBlockingType )
            {
            // InternalAtsDsl.g:4778:1: (lv_blockingType_14_0= ruleReviewBlockingType )
            // InternalAtsDsl.g:4779:3: lv_blockingType_14_0= ruleReviewBlockingType
            {
             
            	        newCompositeNode(grammarAccess.getCreateDecisionReviewRuleDefAccess().getBlockingTypeReviewBlockingTypeEnumRuleCall_8_0()); 
            	    
            pushFollow(FOLLOW_92);
            lv_blockingType_14_0=ruleReviewBlockingType();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getCreateDecisionReviewRuleDefRule());
            	        }
                   		set(
                   			current, 
                   			"blockingType",
                    		lv_blockingType_14_0, 
                    		"org.eclipse.osee.ats.dsl.AtsDsl.ReviewBlockingType");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            otherlv_15=(Token)match(input,70,FOLLOW_93); 

                	newLeafNode(otherlv_15, grammarAccess.getCreateDecisionReviewRuleDefAccess().getOnEventKeyword_9());
                
            // InternalAtsDsl.g:4799:1: ( (lv_stateEvent_16_0= ruleWorkflowEventType ) )
            // InternalAtsDsl.g:4800:1: (lv_stateEvent_16_0= ruleWorkflowEventType )
            {
            // InternalAtsDsl.g:4800:1: (lv_stateEvent_16_0= ruleWorkflowEventType )
            // InternalAtsDsl.g:4801:3: lv_stateEvent_16_0= ruleWorkflowEventType
            {
             
            	        newCompositeNode(grammarAccess.getCreateDecisionReviewRuleDefAccess().getStateEventWorkflowEventTypeEnumRuleCall_10_0()); 
            	    
            pushFollow(FOLLOW_120);
            lv_stateEvent_16_0=ruleWorkflowEventType();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getCreateDecisionReviewRuleDefRule());
            	        }
                   		set(
                   			current, 
                   			"stateEvent",
                    		lv_stateEvent_16_0, 
                    		"org.eclipse.osee.ats.dsl.AtsDsl.WorkflowEventType");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // InternalAtsDsl.g:4817:2: (otherlv_17= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_18_0= ruleBooleanDef ) ) )
            // InternalAtsDsl.g:4817:4: otherlv_17= 'autoTransitionToDecision' ( (lv_autoTransitionToDecision_18_0= ruleBooleanDef ) )
            {
            otherlv_17=(Token)match(input,72,FOLLOW_13); 

                	newLeafNode(otherlv_17, grammarAccess.getCreateDecisionReviewRuleDefAccess().getAutoTransitionToDecisionKeyword_11_0());
                
            // InternalAtsDsl.g:4821:1: ( (lv_autoTransitionToDecision_18_0= ruleBooleanDef ) )
            // InternalAtsDsl.g:4822:1: (lv_autoTransitionToDecision_18_0= ruleBooleanDef )
            {
            // InternalAtsDsl.g:4822:1: (lv_autoTransitionToDecision_18_0= ruleBooleanDef )
            // InternalAtsDsl.g:4823:3: lv_autoTransitionToDecision_18_0= ruleBooleanDef
            {
             
            	        newCompositeNode(grammarAccess.getCreateDecisionReviewRuleDefAccess().getAutoTransitionToDecisionBooleanDefEnumRuleCall_11_1_0()); 
            	    
            pushFollow(FOLLOW_94);
            lv_autoTransitionToDecision_18_0=ruleBooleanDef();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getCreateDecisionReviewRuleDefRule());
            	        }
                   		set(
                   			current, 
                   			"autoTransitionToDecision",
                    		lv_autoTransitionToDecision_18_0, 
                    		"org.eclipse.osee.ats.dsl.AtsDsl.BooleanDef");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }


            }

            // InternalAtsDsl.g:4839:3: ( (lv_options_19_0= ruleDecisionReviewOpt ) )+
            int cnt119=0;
            loop119:
            do {
                int alt119=2;
                int LA119_0 = input.LA(1);

                if ( (LA119_0==53) ) {
                    alt119=1;
                }


                switch (alt119) {
            	case 1 :
            	    // InternalAtsDsl.g:4840:1: (lv_options_19_0= ruleDecisionReviewOpt )
            	    {
            	    // InternalAtsDsl.g:4840:1: (lv_options_19_0= ruleDecisionReviewOpt )
            	    // InternalAtsDsl.g:4841:3: lv_options_19_0= ruleDecisionReviewOpt
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getCreateDecisionReviewRuleDefAccess().getOptionsDecisionReviewOptParserRuleCall_12_0()); 
            	    	    
            	    pushFollow(FOLLOW_121);
            	    lv_options_19_0=ruleDecisionReviewOpt();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getCreateDecisionReviewRuleDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"options",
            	            		lv_options_19_0, 
            	            		"org.eclipse.osee.ats.dsl.AtsDsl.DecisionReviewOpt");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt119 >= 1 ) break loop119;
                        EarlyExitException eee =
                            new EarlyExitException(119, input);
                        throw eee;
                }
                cnt119++;
            } while (true);

            // InternalAtsDsl.g:4857:3: (otherlv_20= 'attribute' ( (lv_attributes_21_0= ruleAttrDef ) ) )*
            loop120:
            do {
                int alt120=2;
                int LA120_0 = input.LA(1);

                if ( (LA120_0==27) ) {
                    alt120=1;
                }


                switch (alt120) {
            	case 1 :
            	    // InternalAtsDsl.g:4857:5: otherlv_20= 'attribute' ( (lv_attributes_21_0= ruleAttrDef ) )
            	    {
            	    otherlv_20=(Token)match(input,27,FOLLOW_3); 

            	        	newLeafNode(otherlv_20, grammarAccess.getCreateDecisionReviewRuleDefAccess().getAttributeKeyword_13_0());
            	        
            	    // InternalAtsDsl.g:4861:1: ( (lv_attributes_21_0= ruleAttrDef ) )
            	    // InternalAtsDsl.g:4862:1: (lv_attributes_21_0= ruleAttrDef )
            	    {
            	    // InternalAtsDsl.g:4862:1: (lv_attributes_21_0= ruleAttrDef )
            	    // InternalAtsDsl.g:4863:3: lv_attributes_21_0= ruleAttrDef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getCreateDecisionReviewRuleDefAccess().getAttributesAttrDefParserRuleCall_13_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_29);
            	    lv_attributes_21_0=ruleAttrDef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getCreateDecisionReviewRuleDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"attributes",
            	            		lv_attributes_21_0, 
            	            		"org.eclipse.osee.ats.dsl.AtsDsl.AttrDef");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop120;
                }
            } while (true);

            otherlv_22=(Token)match(input,22,FOLLOW_2); 

                	newLeafNode(otherlv_22, grammarAccess.getCreateDecisionReviewRuleDefAccess().getRightCurlyBracketKeyword_14());
                

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
    // $ANTLR end "ruleCreateDecisionReviewRuleDef"


    // $ANTLR start "entryRuleCreatePeerReviewRuleDef"
    // InternalAtsDsl.g:4891:1: entryRuleCreatePeerReviewRuleDef returns [EObject current=null] : iv_ruleCreatePeerReviewRuleDef= ruleCreatePeerReviewRuleDef EOF ;
    public final EObject entryRuleCreatePeerReviewRuleDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleCreatePeerReviewRuleDef = null;


        try {
            // InternalAtsDsl.g:4892:2: (iv_ruleCreatePeerReviewRuleDef= ruleCreatePeerReviewRuleDef EOF )
            // InternalAtsDsl.g:4893:2: iv_ruleCreatePeerReviewRuleDef= ruleCreatePeerReviewRuleDef EOF
            {
             newCompositeNode(grammarAccess.getCreatePeerReviewRuleDefRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleCreatePeerReviewRuleDef=ruleCreatePeerReviewRuleDef();

            state._fsp--;

             current =iv_ruleCreatePeerReviewRuleDef; 
            match(input,EOF,FOLLOW_2); 

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
    // $ANTLR end "entryRuleCreatePeerReviewRuleDef"


    // $ANTLR start "ruleCreatePeerReviewRuleDef"
    // InternalAtsDsl.g:4900:1: ruleCreatePeerReviewRuleDef returns [EObject current=null] : ( (otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) ) (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) ) )+ (otherlv_9= 'assignees' ( (lv_assignees_10_0= ruleUserDef ) ) )* (otherlv_11= 'relatedToState' ( (lv_relatedToState_12_0= ruleSTATE_NAME_REFERENCE ) ) )? (otherlv_13= 'location' ( (lv_location_14_0= RULE_STRING ) ) )? otherlv_15= 'blockingType' ( (lv_blockingType_16_0= ruleReviewBlockingType ) ) otherlv_17= 'onEvent' ( (lv_stateEvent_18_0= ruleWorkflowEventType ) ) (otherlv_19= 'attribute' ( (lv_attributes_20_0= ruleAttrDef ) ) )* otherlv_21= '}' ) ;
    public final EObject ruleCreatePeerReviewRuleDef() throws RecognitionException {
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
        Token lv_location_14_0=null;
        Token otherlv_15=null;
        Token otherlv_17=null;
        Token otherlv_19=null;
        Token otherlv_21=null;
        AntlrDatatypeRuleToken lv_name_1_0 = null;

        Enumerator lv_ruleLocation_8_0 = null;

        EObject lv_assignees_10_0 = null;

        AntlrDatatypeRuleToken lv_relatedToState_12_0 = null;

        Enumerator lv_blockingType_16_0 = null;

        Enumerator lv_stateEvent_18_0 = null;

        EObject lv_attributes_20_0 = null;


         enterRule(); 
            
        try {
            // InternalAtsDsl.g:4903:28: ( ( (otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) ) (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) ) )+ (otherlv_9= 'assignees' ( (lv_assignees_10_0= ruleUserDef ) ) )* (otherlv_11= 'relatedToState' ( (lv_relatedToState_12_0= ruleSTATE_NAME_REFERENCE ) ) )? (otherlv_13= 'location' ( (lv_location_14_0= RULE_STRING ) ) )? otherlv_15= 'blockingType' ( (lv_blockingType_16_0= ruleReviewBlockingType ) ) otherlv_17= 'onEvent' ( (lv_stateEvent_18_0= ruleWorkflowEventType ) ) (otherlv_19= 'attribute' ( (lv_attributes_20_0= ruleAttrDef ) ) )* otherlv_21= '}' ) )
            // InternalAtsDsl.g:4904:1: ( (otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) ) (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) ) )+ (otherlv_9= 'assignees' ( (lv_assignees_10_0= ruleUserDef ) ) )* (otherlv_11= 'relatedToState' ( (lv_relatedToState_12_0= ruleSTATE_NAME_REFERENCE ) ) )? (otherlv_13= 'location' ( (lv_location_14_0= RULE_STRING ) ) )? otherlv_15= 'blockingType' ( (lv_blockingType_16_0= ruleReviewBlockingType ) ) otherlv_17= 'onEvent' ( (lv_stateEvent_18_0= ruleWorkflowEventType ) ) (otherlv_19= 'attribute' ( (lv_attributes_20_0= ruleAttrDef ) ) )* otherlv_21= '}' )
            {
            // InternalAtsDsl.g:4904:1: ( (otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) ) (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) ) )+ (otherlv_9= 'assignees' ( (lv_assignees_10_0= ruleUserDef ) ) )* (otherlv_11= 'relatedToState' ( (lv_relatedToState_12_0= ruleSTATE_NAME_REFERENCE ) ) )? (otherlv_13= 'location' ( (lv_location_14_0= RULE_STRING ) ) )? otherlv_15= 'blockingType' ( (lv_blockingType_16_0= ruleReviewBlockingType ) ) otherlv_17= 'onEvent' ( (lv_stateEvent_18_0= ruleWorkflowEventType ) ) (otherlv_19= 'attribute' ( (lv_attributes_20_0= ruleAttrDef ) ) )* otherlv_21= '}' )
            // InternalAtsDsl.g:4904:2: (otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) ) ) otherlv_2= '{' (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) ) (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? (otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) ) )+ (otherlv_9= 'assignees' ( (lv_assignees_10_0= ruleUserDef ) ) )* (otherlv_11= 'relatedToState' ( (lv_relatedToState_12_0= ruleSTATE_NAME_REFERENCE ) ) )? (otherlv_13= 'location' ( (lv_location_14_0= RULE_STRING ) ) )? otherlv_15= 'blockingType' ( (lv_blockingType_16_0= ruleReviewBlockingType ) ) otherlv_17= 'onEvent' ( (lv_stateEvent_18_0= ruleWorkflowEventType ) ) (otherlv_19= 'attribute' ( (lv_attributes_20_0= ruleAttrDef ) ) )* otherlv_21= '}'
            {
            // InternalAtsDsl.g:4904:2: (otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) ) )
            // InternalAtsDsl.g:4904:4: otherlv_0= 'name' ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) )
            {
            otherlv_0=(Token)match(input,87,FOLLOW_41); 

                	newLeafNode(otherlv_0, grammarAccess.getCreatePeerReviewRuleDefAccess().getNameKeyword_0_0());
                
            // InternalAtsDsl.g:4908:1: ( (lv_name_1_0= ruleRULE_NAME_REFERENCE ) )
            // InternalAtsDsl.g:4909:1: (lv_name_1_0= ruleRULE_NAME_REFERENCE )
            {
            // InternalAtsDsl.g:4909:1: (lv_name_1_0= ruleRULE_NAME_REFERENCE )
            // InternalAtsDsl.g:4910:3: lv_name_1_0= ruleRULE_NAME_REFERENCE
            {
             
            	        newCompositeNode(grammarAccess.getCreatePeerReviewRuleDefAccess().getNameRULE_NAME_REFERENCEParserRuleCall_0_1_0()); 
            	    
            pushFollow(FOLLOW_43);
            lv_name_1_0=ruleRULE_NAME_REFERENCE();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getCreatePeerReviewRuleDefRule());
            	        }
                   		set(
                   			current, 
                   			"name",
                    		lv_name_1_0, 
                    		"org.eclipse.osee.ats.dsl.AtsDsl.RULE_NAME_REFERENCE");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }


            }

            otherlv_2=(Token)match(input,17,FOLLOW_87); 

                	newLeafNode(otherlv_2, grammarAccess.getCreatePeerReviewRuleDefAccess().getLeftCurlyBracketKeyword_1());
                
            // InternalAtsDsl.g:4930:1: (otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) ) )
            // InternalAtsDsl.g:4930:3: otherlv_3= 'title' ( (lv_title_4_0= RULE_STRING ) )
            {
            otherlv_3=(Token)match(input,67,FOLLOW_3); 

                	newLeafNode(otherlv_3, grammarAccess.getCreatePeerReviewRuleDefAccess().getTitleKeyword_2_0());
                
            // InternalAtsDsl.g:4934:1: ( (lv_title_4_0= RULE_STRING ) )
            // InternalAtsDsl.g:4935:1: (lv_title_4_0= RULE_STRING )
            {
            // InternalAtsDsl.g:4935:1: (lv_title_4_0= RULE_STRING )
            // InternalAtsDsl.g:4936:3: lv_title_4_0= RULE_STRING
            {
            lv_title_4_0=(Token)match(input,RULE_STRING,FOLLOW_109); 

            			newLeafNode(lv_title_4_0, grammarAccess.getCreatePeerReviewRuleDefAccess().getTitleSTRINGTerminalRuleCall_2_1_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getCreatePeerReviewRuleDefRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"title",
                    		lv_title_4_0, 
                    		"org.eclipse.xtext.common.Terminals.STRING");
            	    

            }


            }


            }

            // InternalAtsDsl.g:4952:3: (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )?
            int alt121=2;
            int LA121_0 = input.LA(1);

            if ( (LA121_0==49) ) {
                alt121=1;
            }
            switch (alt121) {
                case 1 :
                    // InternalAtsDsl.g:4952:5: otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) )
                    {
                    otherlv_5=(Token)match(input,49,FOLLOW_3); 

                        	newLeafNode(otherlv_5, grammarAccess.getCreatePeerReviewRuleDefAccess().getDescriptionKeyword_3_0());
                        
                    // InternalAtsDsl.g:4956:1: ( (lv_description_6_0= RULE_STRING ) )
                    // InternalAtsDsl.g:4957:1: (lv_description_6_0= RULE_STRING )
                    {
                    // InternalAtsDsl.g:4957:1: (lv_description_6_0= RULE_STRING )
                    // InternalAtsDsl.g:4958:3: lv_description_6_0= RULE_STRING
                    {
                    lv_description_6_0=(Token)match(input,RULE_STRING,FOLLOW_110); 

                    			newLeafNode(lv_description_6_0, grammarAccess.getCreatePeerReviewRuleDefAccess().getDescriptionSTRINGTerminalRuleCall_3_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getCreatePeerReviewRuleDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"description",
                            		lv_description_6_0, 
                            		"org.eclipse.xtext.common.Terminals.STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            // InternalAtsDsl.g:4974:4: (otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) ) )+
            int cnt122=0;
            loop122:
            do {
                int alt122=2;
                int LA122_0 = input.LA(1);

                if ( (LA122_0==88) ) {
                    alt122=1;
                }


                switch (alt122) {
            	case 1 :
            	    // InternalAtsDsl.g:4974:6: otherlv_7= 'ruleLocation' ( (lv_ruleLocation_8_0= ruleRuleLocation ) )
            	    {
            	    otherlv_7=(Token)match(input,88,FOLLOW_111); 

            	        	newLeafNode(otherlv_7, grammarAccess.getCreatePeerReviewRuleDefAccess().getRuleLocationKeyword_4_0());
            	        
            	    // InternalAtsDsl.g:4978:1: ( (lv_ruleLocation_8_0= ruleRuleLocation ) )
            	    // InternalAtsDsl.g:4979:1: (lv_ruleLocation_8_0= ruleRuleLocation )
            	    {
            	    // InternalAtsDsl.g:4979:1: (lv_ruleLocation_8_0= ruleRuleLocation )
            	    // InternalAtsDsl.g:4980:3: lv_ruleLocation_8_0= ruleRuleLocation
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getCreatePeerReviewRuleDefAccess().getRuleLocationRuleLocationEnumRuleCall_4_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_122);
            	    lv_ruleLocation_8_0=ruleRuleLocation();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getCreatePeerReviewRuleDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"ruleLocation",
            	            		lv_ruleLocation_8_0, 
            	            		"org.eclipse.osee.ats.dsl.AtsDsl.RuleLocation");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt122 >= 1 ) break loop122;
                        EarlyExitException eee =
                            new EarlyExitException(122, input);
                        throw eee;
                }
                cnt122++;
            } while (true);

            // InternalAtsDsl.g:4996:4: (otherlv_9= 'assignees' ( (lv_assignees_10_0= ruleUserDef ) ) )*
            loop123:
            do {
                int alt123=2;
                int LA123_0 = input.LA(1);

                if ( (LA123_0==89) ) {
                    alt123=1;
                }


                switch (alt123) {
            	case 1 :
            	    // InternalAtsDsl.g:4996:6: otherlv_9= 'assignees' ( (lv_assignees_10_0= ruleUserDef ) )
            	    {
            	    otherlv_9=(Token)match(input,89,FOLLOW_3); 

            	        	newLeafNode(otherlv_9, grammarAccess.getCreatePeerReviewRuleDefAccess().getAssigneesKeyword_5_0());
            	        
            	    // InternalAtsDsl.g:5000:1: ( (lv_assignees_10_0= ruleUserDef ) )
            	    // InternalAtsDsl.g:5001:1: (lv_assignees_10_0= ruleUserDef )
            	    {
            	    // InternalAtsDsl.g:5001:1: (lv_assignees_10_0= ruleUserDef )
            	    // InternalAtsDsl.g:5002:3: lv_assignees_10_0= ruleUserDef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getCreatePeerReviewRuleDefAccess().getAssigneesUserDefParserRuleCall_5_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_123);
            	    lv_assignees_10_0=ruleUserDef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getCreatePeerReviewRuleDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"assignees",
            	            		lv_assignees_10_0, 
            	            		"org.eclipse.osee.ats.dsl.AtsDsl.UserDef");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop123;
                }
            } while (true);

            // InternalAtsDsl.g:5018:4: (otherlv_11= 'relatedToState' ( (lv_relatedToState_12_0= ruleSTATE_NAME_REFERENCE ) ) )?
            int alt124=2;
            int LA124_0 = input.LA(1);

            if ( (LA124_0==68) ) {
                alt124=1;
            }
            switch (alt124) {
                case 1 :
                    // InternalAtsDsl.g:5018:6: otherlv_11= 'relatedToState' ( (lv_relatedToState_12_0= ruleSTATE_NAME_REFERENCE ) )
                    {
                    otherlv_11=(Token)match(input,68,FOLLOW_3); 

                        	newLeafNode(otherlv_11, grammarAccess.getCreatePeerReviewRuleDefAccess().getRelatedToStateKeyword_6_0());
                        
                    // InternalAtsDsl.g:5022:1: ( (lv_relatedToState_12_0= ruleSTATE_NAME_REFERENCE ) )
                    // InternalAtsDsl.g:5023:1: (lv_relatedToState_12_0= ruleSTATE_NAME_REFERENCE )
                    {
                    // InternalAtsDsl.g:5023:1: (lv_relatedToState_12_0= ruleSTATE_NAME_REFERENCE )
                    // InternalAtsDsl.g:5024:3: lv_relatedToState_12_0= ruleSTATE_NAME_REFERENCE
                    {
                     
                    	        newCompositeNode(grammarAccess.getCreatePeerReviewRuleDefAccess().getRelatedToStateSTATE_NAME_REFERENCEParserRuleCall_6_1_0()); 
                    	    
                    pushFollow(FOLLOW_124);
                    lv_relatedToState_12_0=ruleSTATE_NAME_REFERENCE();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getCreatePeerReviewRuleDefRule());
                    	        }
                           		set(
                           			current, 
                           			"relatedToState",
                            		lv_relatedToState_12_0, 
                            		"org.eclipse.osee.ats.dsl.AtsDsl.STATE_NAME_REFERENCE");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }


                    }
                    break;

            }

            // InternalAtsDsl.g:5040:4: (otherlv_13= 'location' ( (lv_location_14_0= RULE_STRING ) ) )?
            int alt125=2;
            int LA125_0 = input.LA(1);

            if ( (LA125_0==75) ) {
                alt125=1;
            }
            switch (alt125) {
                case 1 :
                    // InternalAtsDsl.g:5040:6: otherlv_13= 'location' ( (lv_location_14_0= RULE_STRING ) )
                    {
                    otherlv_13=(Token)match(input,75,FOLLOW_3); 

                        	newLeafNode(otherlv_13, grammarAccess.getCreatePeerReviewRuleDefAccess().getLocationKeyword_7_0());
                        
                    // InternalAtsDsl.g:5044:1: ( (lv_location_14_0= RULE_STRING ) )
                    // InternalAtsDsl.g:5045:1: (lv_location_14_0= RULE_STRING )
                    {
                    // InternalAtsDsl.g:5045:1: (lv_location_14_0= RULE_STRING )
                    // InternalAtsDsl.g:5046:3: lv_location_14_0= RULE_STRING
                    {
                    lv_location_14_0=(Token)match(input,RULE_STRING,FOLLOW_90); 

                    			newLeafNode(lv_location_14_0, grammarAccess.getCreatePeerReviewRuleDefAccess().getLocationSTRINGTerminalRuleCall_7_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getCreatePeerReviewRuleDefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"location",
                            		lv_location_14_0, 
                            		"org.eclipse.xtext.common.Terminals.STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            otherlv_15=(Token)match(input,69,FOLLOW_91); 

                	newLeafNode(otherlv_15, grammarAccess.getCreatePeerReviewRuleDefAccess().getBlockingTypeKeyword_8());
                
            // InternalAtsDsl.g:5066:1: ( (lv_blockingType_16_0= ruleReviewBlockingType ) )
            // InternalAtsDsl.g:5067:1: (lv_blockingType_16_0= ruleReviewBlockingType )
            {
            // InternalAtsDsl.g:5067:1: (lv_blockingType_16_0= ruleReviewBlockingType )
            // InternalAtsDsl.g:5068:3: lv_blockingType_16_0= ruleReviewBlockingType
            {
             
            	        newCompositeNode(grammarAccess.getCreatePeerReviewRuleDefAccess().getBlockingTypeReviewBlockingTypeEnumRuleCall_9_0()); 
            	    
            pushFollow(FOLLOW_92);
            lv_blockingType_16_0=ruleReviewBlockingType();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getCreatePeerReviewRuleDefRule());
            	        }
                   		set(
                   			current, 
                   			"blockingType",
                    		lv_blockingType_16_0, 
                    		"org.eclipse.osee.ats.dsl.AtsDsl.ReviewBlockingType");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            otherlv_17=(Token)match(input,70,FOLLOW_93); 

                	newLeafNode(otherlv_17, grammarAccess.getCreatePeerReviewRuleDefAccess().getOnEventKeyword_10());
                
            // InternalAtsDsl.g:5088:1: ( (lv_stateEvent_18_0= ruleWorkflowEventType ) )
            // InternalAtsDsl.g:5089:1: (lv_stateEvent_18_0= ruleWorkflowEventType )
            {
            // InternalAtsDsl.g:5089:1: (lv_stateEvent_18_0= ruleWorkflowEventType )
            // InternalAtsDsl.g:5090:3: lv_stateEvent_18_0= ruleWorkflowEventType
            {
             
            	        newCompositeNode(grammarAccess.getCreatePeerReviewRuleDefAccess().getStateEventWorkflowEventTypeEnumRuleCall_11_0()); 
            	    
            pushFollow(FOLLOW_29);
            lv_stateEvent_18_0=ruleWorkflowEventType();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getCreatePeerReviewRuleDefRule());
            	        }
                   		set(
                   			current, 
                   			"stateEvent",
                    		lv_stateEvent_18_0, 
                    		"org.eclipse.osee.ats.dsl.AtsDsl.WorkflowEventType");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // InternalAtsDsl.g:5106:2: (otherlv_19= 'attribute' ( (lv_attributes_20_0= ruleAttrDef ) ) )*
            loop126:
            do {
                int alt126=2;
                int LA126_0 = input.LA(1);

                if ( (LA126_0==27) ) {
                    alt126=1;
                }


                switch (alt126) {
            	case 1 :
            	    // InternalAtsDsl.g:5106:4: otherlv_19= 'attribute' ( (lv_attributes_20_0= ruleAttrDef ) )
            	    {
            	    otherlv_19=(Token)match(input,27,FOLLOW_3); 

            	        	newLeafNode(otherlv_19, grammarAccess.getCreatePeerReviewRuleDefAccess().getAttributeKeyword_12_0());
            	        
            	    // InternalAtsDsl.g:5110:1: ( (lv_attributes_20_0= ruleAttrDef ) )
            	    // InternalAtsDsl.g:5111:1: (lv_attributes_20_0= ruleAttrDef )
            	    {
            	    // InternalAtsDsl.g:5111:1: (lv_attributes_20_0= ruleAttrDef )
            	    // InternalAtsDsl.g:5112:3: lv_attributes_20_0= ruleAttrDef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getCreatePeerReviewRuleDefAccess().getAttributesAttrDefParserRuleCall_12_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_29);
            	    lv_attributes_20_0=ruleAttrDef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getCreatePeerReviewRuleDefRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"attributes",
            	            		lv_attributes_20_0, 
            	            		"org.eclipse.osee.ats.dsl.AtsDsl.AttrDef");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop126;
                }
            } while (true);

            otherlv_21=(Token)match(input,22,FOLLOW_2); 

                	newLeafNode(otherlv_21, grammarAccess.getCreatePeerReviewRuleDefAccess().getRightCurlyBracketKeyword_13());
                

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
    // $ANTLR end "ruleCreatePeerReviewRuleDef"


    // $ANTLR start "entryRuleRuleRef"
    // InternalAtsDsl.g:5140:1: entryRuleRuleRef returns [String current=null] : iv_ruleRuleRef= ruleRuleRef EOF ;
    public final String entryRuleRuleRef() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleRuleRef = null;


        try {
            // InternalAtsDsl.g:5141:2: (iv_ruleRuleRef= ruleRuleRef EOF )
            // InternalAtsDsl.g:5142:2: iv_ruleRuleRef= ruleRuleRef EOF
            {
             newCompositeNode(grammarAccess.getRuleRefRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleRuleRef=ruleRuleRef();

            state._fsp--;

             current =iv_ruleRuleRef.getText(); 
            match(input,EOF,FOLLOW_2); 

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
    // $ANTLR end "entryRuleRuleRef"


    // $ANTLR start "ruleRuleRef"
    // InternalAtsDsl.g:5149:1: ruleRuleRef returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'RequireStateHourSpentPrompt' | kw= 'AddDecisionValidateBlockingReview' | kw= 'AddDecisionValidateNonBlockingReview' | kw= 'AllowTransitionWithWorkingBranch' | kw= 'ForceAssigneesToTeamLeads' | kw= 'RequireTargetedVersion' | kw= 'AllowPrivilegedEditToTeamMember' | kw= 'AllowPrivilegedEditToTeamMemberAndOriginator' | kw= 'AllowPrivilegedEditToAll' | kw= 'AllowEditToAll' | kw= 'AllowAssigneeToAll' | kw= 'AllowTransitionWithoutTaskCompletion' | this_RULE_NAME_REFERENCE_12= ruleRULE_NAME_REFERENCE ) ;
    public final AntlrDatatypeRuleToken ruleRuleRef() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        AntlrDatatypeRuleToken this_RULE_NAME_REFERENCE_12 = null;


         enterRule(); 
            
        try {
            // InternalAtsDsl.g:5152:28: ( (kw= 'RequireStateHourSpentPrompt' | kw= 'AddDecisionValidateBlockingReview' | kw= 'AddDecisionValidateNonBlockingReview' | kw= 'AllowTransitionWithWorkingBranch' | kw= 'ForceAssigneesToTeamLeads' | kw= 'RequireTargetedVersion' | kw= 'AllowPrivilegedEditToTeamMember' | kw= 'AllowPrivilegedEditToTeamMemberAndOriginator' | kw= 'AllowPrivilegedEditToAll' | kw= 'AllowEditToAll' | kw= 'AllowAssigneeToAll' | kw= 'AllowTransitionWithoutTaskCompletion' | this_RULE_NAME_REFERENCE_12= ruleRULE_NAME_REFERENCE ) )
            // InternalAtsDsl.g:5153:1: (kw= 'RequireStateHourSpentPrompt' | kw= 'AddDecisionValidateBlockingReview' | kw= 'AddDecisionValidateNonBlockingReview' | kw= 'AllowTransitionWithWorkingBranch' | kw= 'ForceAssigneesToTeamLeads' | kw= 'RequireTargetedVersion' | kw= 'AllowPrivilegedEditToTeamMember' | kw= 'AllowPrivilegedEditToTeamMemberAndOriginator' | kw= 'AllowPrivilegedEditToAll' | kw= 'AllowEditToAll' | kw= 'AllowAssigneeToAll' | kw= 'AllowTransitionWithoutTaskCompletion' | this_RULE_NAME_REFERENCE_12= ruleRULE_NAME_REFERENCE )
            {
            // InternalAtsDsl.g:5153:1: (kw= 'RequireStateHourSpentPrompt' | kw= 'AddDecisionValidateBlockingReview' | kw= 'AddDecisionValidateNonBlockingReview' | kw= 'AllowTransitionWithWorkingBranch' | kw= 'ForceAssigneesToTeamLeads' | kw= 'RequireTargetedVersion' | kw= 'AllowPrivilegedEditToTeamMember' | kw= 'AllowPrivilegedEditToTeamMemberAndOriginator' | kw= 'AllowPrivilegedEditToAll' | kw= 'AllowEditToAll' | kw= 'AllowAssigneeToAll' | kw= 'AllowTransitionWithoutTaskCompletion' | this_RULE_NAME_REFERENCE_12= ruleRULE_NAME_REFERENCE )
            int alt127=13;
            switch ( input.LA(1) ) {
            case 92:
                {
                alt127=1;
                }
                break;
            case 93:
                {
                alt127=2;
                }
                break;
            case 94:
                {
                alt127=3;
                }
                break;
            case 95:
                {
                alt127=4;
                }
                break;
            case 96:
                {
                alt127=5;
                }
                break;
            case 97:
                {
                alt127=6;
                }
                break;
            case 98:
                {
                alt127=7;
                }
                break;
            case 99:
                {
                alt127=8;
                }
                break;
            case 100:
                {
                alt127=9;
                }
                break;
            case 101:
                {
                alt127=10;
                }
                break;
            case 102:
                {
                alt127=11;
                }
                break;
            case 103:
                {
                alt127=12;
                }
                break;
            case RULE_STRING:
                {
                alt127=13;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 127, 0, input);

                throw nvae;
            }

            switch (alt127) {
                case 1 :
                    // InternalAtsDsl.g:5154:2: kw= 'RequireStateHourSpentPrompt'
                    {
                    kw=(Token)match(input,92,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleRefAccess().getRequireStateHourSpentPromptKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // InternalAtsDsl.g:5161:2: kw= 'AddDecisionValidateBlockingReview'
                    {
                    kw=(Token)match(input,93,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleRefAccess().getAddDecisionValidateBlockingReviewKeyword_1()); 
                        

                    }
                    break;
                case 3 :
                    // InternalAtsDsl.g:5168:2: kw= 'AddDecisionValidateNonBlockingReview'
                    {
                    kw=(Token)match(input,94,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleRefAccess().getAddDecisionValidateNonBlockingReviewKeyword_2()); 
                        

                    }
                    break;
                case 4 :
                    // InternalAtsDsl.g:5175:2: kw= 'AllowTransitionWithWorkingBranch'
                    {
                    kw=(Token)match(input,95,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleRefAccess().getAllowTransitionWithWorkingBranchKeyword_3()); 
                        

                    }
                    break;
                case 5 :
                    // InternalAtsDsl.g:5182:2: kw= 'ForceAssigneesToTeamLeads'
                    {
                    kw=(Token)match(input,96,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleRefAccess().getForceAssigneesToTeamLeadsKeyword_4()); 
                        

                    }
                    break;
                case 6 :
                    // InternalAtsDsl.g:5189:2: kw= 'RequireTargetedVersion'
                    {
                    kw=(Token)match(input,97,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleRefAccess().getRequireTargetedVersionKeyword_5()); 
                        

                    }
                    break;
                case 7 :
                    // InternalAtsDsl.g:5196:2: kw= 'AllowPrivilegedEditToTeamMember'
                    {
                    kw=(Token)match(input,98,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleRefAccess().getAllowPrivilegedEditToTeamMemberKeyword_6()); 
                        

                    }
                    break;
                case 8 :
                    // InternalAtsDsl.g:5203:2: kw= 'AllowPrivilegedEditToTeamMemberAndOriginator'
                    {
                    kw=(Token)match(input,99,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleRefAccess().getAllowPrivilegedEditToTeamMemberAndOriginatorKeyword_7()); 
                        

                    }
                    break;
                case 9 :
                    // InternalAtsDsl.g:5210:2: kw= 'AllowPrivilegedEditToAll'
                    {
                    kw=(Token)match(input,100,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleRefAccess().getAllowPrivilegedEditToAllKeyword_8()); 
                        

                    }
                    break;
                case 10 :
                    // InternalAtsDsl.g:5217:2: kw= 'AllowEditToAll'
                    {
                    kw=(Token)match(input,101,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleRefAccess().getAllowEditToAllKeyword_9()); 
                        

                    }
                    break;
                case 11 :
                    // InternalAtsDsl.g:5224:2: kw= 'AllowAssigneeToAll'
                    {
                    kw=(Token)match(input,102,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleRefAccess().getAllowAssigneeToAllKeyword_10()); 
                        

                    }
                    break;
                case 12 :
                    // InternalAtsDsl.g:5231:2: kw= 'AllowTransitionWithoutTaskCompletion'
                    {
                    kw=(Token)match(input,103,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRuleRefAccess().getAllowTransitionWithoutTaskCompletionKeyword_11()); 
                        

                    }
                    break;
                case 13 :
                    // InternalAtsDsl.g:5238:5: this_RULE_NAME_REFERENCE_12= ruleRULE_NAME_REFERENCE
                    {
                     
                            newCompositeNode(grammarAccess.getRuleRefAccess().getRULE_NAME_REFERENCEParserRuleCall_12()); 
                        
                    pushFollow(FOLLOW_2);
                    this_RULE_NAME_REFERENCE_12=ruleRULE_NAME_REFERENCE();

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
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleRuleRef"


    // $ANTLR start "entryRuleReviewRule"
    // InternalAtsDsl.g:5256:1: entryRuleReviewRule returns [EObject current=null] : iv_ruleReviewRule= ruleReviewRule EOF ;
    public final EObject entryRuleReviewRule() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleReviewRule = null;


        try {
            // InternalAtsDsl.g:5257:2: (iv_ruleReviewRule= ruleReviewRule EOF )
            // InternalAtsDsl.g:5258:2: iv_ruleReviewRule= ruleReviewRule EOF
            {
             newCompositeNode(grammarAccess.getReviewRuleRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleReviewRule=ruleReviewRule();

            state._fsp--;

             current =iv_ruleReviewRule; 
            match(input,EOF,FOLLOW_2); 

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
    // $ANTLR end "entryRuleReviewRule"


    // $ANTLR start "ruleReviewRule"
    // InternalAtsDsl.g:5265:1: ruleReviewRule returns [EObject current=null] : (this_CreateDecisionReviewRuleDef_0= ruleCreateDecisionReviewRuleDef | this_CreatePeerReviewRuleDef_1= ruleCreatePeerReviewRuleDef ) ;
    public final EObject ruleReviewRule() throws RecognitionException {
        EObject current = null;

        EObject this_CreateDecisionReviewRuleDef_0 = null;

        EObject this_CreatePeerReviewRuleDef_1 = null;


         enterRule(); 
            
        try {
            // InternalAtsDsl.g:5268:28: ( (this_CreateDecisionReviewRuleDef_0= ruleCreateDecisionReviewRuleDef | this_CreatePeerReviewRuleDef_1= ruleCreatePeerReviewRuleDef ) )
            // InternalAtsDsl.g:5269:1: (this_CreateDecisionReviewRuleDef_0= ruleCreateDecisionReviewRuleDef | this_CreatePeerReviewRuleDef_1= ruleCreatePeerReviewRuleDef )
            {
            // InternalAtsDsl.g:5269:1: (this_CreateDecisionReviewRuleDef_0= ruleCreateDecisionReviewRuleDef | this_CreatePeerReviewRuleDef_1= ruleCreatePeerReviewRuleDef )
            int alt128=2;
            alt128 = dfa128.predict(input);
            switch (alt128) {
                case 1 :
                    // InternalAtsDsl.g:5270:5: this_CreateDecisionReviewRuleDef_0= ruleCreateDecisionReviewRuleDef
                    {
                     
                            newCompositeNode(grammarAccess.getReviewRuleAccess().getCreateDecisionReviewRuleDefParserRuleCall_0()); 
                        
                    pushFollow(FOLLOW_2);
                    this_CreateDecisionReviewRuleDef_0=ruleCreateDecisionReviewRuleDef();

                    state._fsp--;

                     
                            current = this_CreateDecisionReviewRuleDef_0; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // InternalAtsDsl.g:5280:5: this_CreatePeerReviewRuleDef_1= ruleCreatePeerReviewRuleDef
                    {
                     
                            newCompositeNode(grammarAccess.getReviewRuleAccess().getCreatePeerReviewRuleDefParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_2);
                    this_CreatePeerReviewRuleDef_1=ruleCreatePeerReviewRuleDef();

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
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleReviewRule"


    // $ANTLR start "entryRuleRule"
    // InternalAtsDsl.g:5296:1: entryRuleRule returns [EObject current=null] : iv_ruleRule= ruleRule EOF ;
    public final EObject entryRuleRule() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRule = null;


        try {
            // InternalAtsDsl.g:5297:2: (iv_ruleRule= ruleRule EOF )
            // InternalAtsDsl.g:5298:2: iv_ruleRule= ruleRule EOF
            {
             newCompositeNode(grammarAccess.getRuleRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleRule=ruleRule();

            state._fsp--;

             current =iv_ruleRule; 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:5305:1: ruleRule returns [EObject current=null] : (this_RuleDef_0= ruleRuleDef | this_CreateTaskRuleDef_1= ruleCreateTaskRuleDef | this_ReviewRule_2= ruleReviewRule ) ;
    public final EObject ruleRule() throws RecognitionException {
        EObject current = null;

        EObject this_RuleDef_0 = null;

        EObject this_CreateTaskRuleDef_1 = null;

        EObject this_ReviewRule_2 = null;


         enterRule(); 
            
        try {
            // InternalAtsDsl.g:5308:28: ( (this_RuleDef_0= ruleRuleDef | this_CreateTaskRuleDef_1= ruleCreateTaskRuleDef | this_ReviewRule_2= ruleReviewRule ) )
            // InternalAtsDsl.g:5309:1: (this_RuleDef_0= ruleRuleDef | this_CreateTaskRuleDef_1= ruleCreateTaskRuleDef | this_ReviewRule_2= ruleReviewRule )
            {
            // InternalAtsDsl.g:5309:1: (this_RuleDef_0= ruleRuleDef | this_CreateTaskRuleDef_1= ruleCreateTaskRuleDef | this_ReviewRule_2= ruleReviewRule )
            int alt129=3;
            alt129 = dfa129.predict(input);
            switch (alt129) {
                case 1 :
                    // InternalAtsDsl.g:5310:5: this_RuleDef_0= ruleRuleDef
                    {
                     
                            newCompositeNode(grammarAccess.getRuleAccess().getRuleDefParserRuleCall_0()); 
                        
                    pushFollow(FOLLOW_2);
                    this_RuleDef_0=ruleRuleDef();

                    state._fsp--;

                     
                            current = this_RuleDef_0; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // InternalAtsDsl.g:5320:5: this_CreateTaskRuleDef_1= ruleCreateTaskRuleDef
                    {
                     
                            newCompositeNode(grammarAccess.getRuleAccess().getCreateTaskRuleDefParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_2);
                    this_CreateTaskRuleDef_1=ruleCreateTaskRuleDef();

                    state._fsp--;

                     
                            current = this_CreateTaskRuleDef_1; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 3 :
                    // InternalAtsDsl.g:5330:5: this_ReviewRule_2= ruleReviewRule
                    {
                     
                            newCompositeNode(grammarAccess.getRuleAccess().getReviewRuleParserRuleCall_2()); 
                        
                    pushFollow(FOLLOW_2);
                    this_ReviewRule_2=ruleReviewRule();

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
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleRule"


    // $ANTLR start "entryRuleWIDGET_OPTION_NAME"
    // InternalAtsDsl.g:5346:1: entryRuleWIDGET_OPTION_NAME returns [String current=null] : iv_ruleWIDGET_OPTION_NAME= ruleWIDGET_OPTION_NAME EOF ;
    public final String entryRuleWIDGET_OPTION_NAME() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleWIDGET_OPTION_NAME = null;


        try {
            // InternalAtsDsl.g:5347:2: (iv_ruleWIDGET_OPTION_NAME= ruleWIDGET_OPTION_NAME EOF )
            // InternalAtsDsl.g:5348:2: iv_ruleWIDGET_OPTION_NAME= ruleWIDGET_OPTION_NAME EOF
            {
             newCompositeNode(grammarAccess.getWIDGET_OPTION_NAMERule()); 
            pushFollow(FOLLOW_1);
            iv_ruleWIDGET_OPTION_NAME=ruleWIDGET_OPTION_NAME();

            state._fsp--;

             current =iv_ruleWIDGET_OPTION_NAME.getText(); 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:5355:1: ruleWIDGET_OPTION_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleWIDGET_OPTION_NAME() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // InternalAtsDsl.g:5358:28: (this_STRING_0= RULE_STRING )
            // InternalAtsDsl.g:5359:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_2); 

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
    // InternalAtsDsl.g:5374:1: entryRuleWidgetOption returns [String current=null] : iv_ruleWidgetOption= ruleWidgetOption EOF ;
    public final String entryRuleWidgetOption() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleWidgetOption = null;


        try {
            // InternalAtsDsl.g:5375:2: (iv_ruleWidgetOption= ruleWidgetOption EOF )
            // InternalAtsDsl.g:5376:2: iv_ruleWidgetOption= ruleWidgetOption EOF
            {
             newCompositeNode(grammarAccess.getWidgetOptionRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleWidgetOption=ruleWidgetOption();

            state._fsp--;

             current =iv_ruleWidgetOption.getText(); 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:5383:1: ruleWidgetOption returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'REQUIRED_FOR_TRANSITION' | kw= 'NOT_REQUIRED_FOR_TRANSITION' | kw= 'REQUIRED_FOR_COMPLETION' | kw= 'NOT_REQUIRED_FOR_COMPLETION' | kw= 'ENABLED' | kw= 'NOT_ENABLED' | kw= 'EDITABLE' | kw= 'NOT_EDITABLE' | kw= 'FUTURE_DATE_REQUIRED' | kw= 'NOT_FUTURE_DATE_REQUIRED' | kw= 'MULTI_SELECT' | kw= 'HORIZONTAL_LABEL' | kw= 'VERTICAL_LABEL' | kw= 'LABEL_AFTER' | kw= 'LABEL_BEFORE' | kw= 'NO_LABEL' | kw= 'SORTED' | kw= 'ADD_DEFAULT_VALUE' | kw= 'NO_DEFAULT_VALUE' | kw= 'BEGIN_COMPOSITE_4' | kw= 'BEGIN_COMPOSITE_6' | kw= 'BEGIN_COMPOSITE_8' | kw= 'BEGIN_COMPOSITE_10' | kw= 'END_COMPOSITE' | kw= 'FILL_NONE' | kw= 'FILL_HORIZONTALLY' | kw= 'FILL_VERTICALLY' | kw= 'ALIGN_LEFT' | kw= 'ALIGN_RIGHT' | kw= 'ALIGN_CENTER' | this_WIDGET_OPTION_NAME_30= ruleWIDGET_OPTION_NAME ) ;
    public final AntlrDatatypeRuleToken ruleWidgetOption() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        AntlrDatatypeRuleToken this_WIDGET_OPTION_NAME_30 = null;


         enterRule(); 
            
        try {
            // InternalAtsDsl.g:5386:28: ( (kw= 'REQUIRED_FOR_TRANSITION' | kw= 'NOT_REQUIRED_FOR_TRANSITION' | kw= 'REQUIRED_FOR_COMPLETION' | kw= 'NOT_REQUIRED_FOR_COMPLETION' | kw= 'ENABLED' | kw= 'NOT_ENABLED' | kw= 'EDITABLE' | kw= 'NOT_EDITABLE' | kw= 'FUTURE_DATE_REQUIRED' | kw= 'NOT_FUTURE_DATE_REQUIRED' | kw= 'MULTI_SELECT' | kw= 'HORIZONTAL_LABEL' | kw= 'VERTICAL_LABEL' | kw= 'LABEL_AFTER' | kw= 'LABEL_BEFORE' | kw= 'NO_LABEL' | kw= 'SORTED' | kw= 'ADD_DEFAULT_VALUE' | kw= 'NO_DEFAULT_VALUE' | kw= 'BEGIN_COMPOSITE_4' | kw= 'BEGIN_COMPOSITE_6' | kw= 'BEGIN_COMPOSITE_8' | kw= 'BEGIN_COMPOSITE_10' | kw= 'END_COMPOSITE' | kw= 'FILL_NONE' | kw= 'FILL_HORIZONTALLY' | kw= 'FILL_VERTICALLY' | kw= 'ALIGN_LEFT' | kw= 'ALIGN_RIGHT' | kw= 'ALIGN_CENTER' | this_WIDGET_OPTION_NAME_30= ruleWIDGET_OPTION_NAME ) )
            // InternalAtsDsl.g:5387:1: (kw= 'REQUIRED_FOR_TRANSITION' | kw= 'NOT_REQUIRED_FOR_TRANSITION' | kw= 'REQUIRED_FOR_COMPLETION' | kw= 'NOT_REQUIRED_FOR_COMPLETION' | kw= 'ENABLED' | kw= 'NOT_ENABLED' | kw= 'EDITABLE' | kw= 'NOT_EDITABLE' | kw= 'FUTURE_DATE_REQUIRED' | kw= 'NOT_FUTURE_DATE_REQUIRED' | kw= 'MULTI_SELECT' | kw= 'HORIZONTAL_LABEL' | kw= 'VERTICAL_LABEL' | kw= 'LABEL_AFTER' | kw= 'LABEL_BEFORE' | kw= 'NO_LABEL' | kw= 'SORTED' | kw= 'ADD_DEFAULT_VALUE' | kw= 'NO_DEFAULT_VALUE' | kw= 'BEGIN_COMPOSITE_4' | kw= 'BEGIN_COMPOSITE_6' | kw= 'BEGIN_COMPOSITE_8' | kw= 'BEGIN_COMPOSITE_10' | kw= 'END_COMPOSITE' | kw= 'FILL_NONE' | kw= 'FILL_HORIZONTALLY' | kw= 'FILL_VERTICALLY' | kw= 'ALIGN_LEFT' | kw= 'ALIGN_RIGHT' | kw= 'ALIGN_CENTER' | this_WIDGET_OPTION_NAME_30= ruleWIDGET_OPTION_NAME )
            {
            // InternalAtsDsl.g:5387:1: (kw= 'REQUIRED_FOR_TRANSITION' | kw= 'NOT_REQUIRED_FOR_TRANSITION' | kw= 'REQUIRED_FOR_COMPLETION' | kw= 'NOT_REQUIRED_FOR_COMPLETION' | kw= 'ENABLED' | kw= 'NOT_ENABLED' | kw= 'EDITABLE' | kw= 'NOT_EDITABLE' | kw= 'FUTURE_DATE_REQUIRED' | kw= 'NOT_FUTURE_DATE_REQUIRED' | kw= 'MULTI_SELECT' | kw= 'HORIZONTAL_LABEL' | kw= 'VERTICAL_LABEL' | kw= 'LABEL_AFTER' | kw= 'LABEL_BEFORE' | kw= 'NO_LABEL' | kw= 'SORTED' | kw= 'ADD_DEFAULT_VALUE' | kw= 'NO_DEFAULT_VALUE' | kw= 'BEGIN_COMPOSITE_4' | kw= 'BEGIN_COMPOSITE_6' | kw= 'BEGIN_COMPOSITE_8' | kw= 'BEGIN_COMPOSITE_10' | kw= 'END_COMPOSITE' | kw= 'FILL_NONE' | kw= 'FILL_HORIZONTALLY' | kw= 'FILL_VERTICALLY' | kw= 'ALIGN_LEFT' | kw= 'ALIGN_RIGHT' | kw= 'ALIGN_CENTER' | this_WIDGET_OPTION_NAME_30= ruleWIDGET_OPTION_NAME )
            int alt130=31;
            switch ( input.LA(1) ) {
            case 104:
                {
                alt130=1;
                }
                break;
            case 105:
                {
                alt130=2;
                }
                break;
            case 106:
                {
                alt130=3;
                }
                break;
            case 107:
                {
                alt130=4;
                }
                break;
            case 108:
                {
                alt130=5;
                }
                break;
            case 109:
                {
                alt130=6;
                }
                break;
            case 110:
                {
                alt130=7;
                }
                break;
            case 111:
                {
                alt130=8;
                }
                break;
            case 112:
                {
                alt130=9;
                }
                break;
            case 113:
                {
                alt130=10;
                }
                break;
            case 114:
                {
                alt130=11;
                }
                break;
            case 115:
                {
                alt130=12;
                }
                break;
            case 116:
                {
                alt130=13;
                }
                break;
            case 117:
                {
                alt130=14;
                }
                break;
            case 118:
                {
                alt130=15;
                }
                break;
            case 119:
                {
                alt130=16;
                }
                break;
            case 120:
                {
                alt130=17;
                }
                break;
            case 121:
                {
                alt130=18;
                }
                break;
            case 122:
                {
                alt130=19;
                }
                break;
            case 123:
                {
                alt130=20;
                }
                break;
            case 124:
                {
                alt130=21;
                }
                break;
            case 125:
                {
                alt130=22;
                }
                break;
            case 126:
                {
                alt130=23;
                }
                break;
            case 127:
                {
                alt130=24;
                }
                break;
            case 128:
                {
                alt130=25;
                }
                break;
            case 129:
                {
                alt130=26;
                }
                break;
            case 130:
                {
                alt130=27;
                }
                break;
            case 131:
                {
                alt130=28;
                }
                break;
            case 132:
                {
                alt130=29;
                }
                break;
            case 133:
                {
                alt130=30;
                }
                break;
            case RULE_STRING:
                {
                alt130=31;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 130, 0, input);

                throw nvae;
            }

            switch (alt130) {
                case 1 :
                    // InternalAtsDsl.g:5388:2: kw= 'REQUIRED_FOR_TRANSITION'
                    {
                    kw=(Token)match(input,104,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getREQUIRED_FOR_TRANSITIONKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // InternalAtsDsl.g:5395:2: kw= 'NOT_REQUIRED_FOR_TRANSITION'
                    {
                    kw=(Token)match(input,105,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getNOT_REQUIRED_FOR_TRANSITIONKeyword_1()); 
                        

                    }
                    break;
                case 3 :
                    // InternalAtsDsl.g:5402:2: kw= 'REQUIRED_FOR_COMPLETION'
                    {
                    kw=(Token)match(input,106,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getREQUIRED_FOR_COMPLETIONKeyword_2()); 
                        

                    }
                    break;
                case 4 :
                    // InternalAtsDsl.g:5409:2: kw= 'NOT_REQUIRED_FOR_COMPLETION'
                    {
                    kw=(Token)match(input,107,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getNOT_REQUIRED_FOR_COMPLETIONKeyword_3()); 
                        

                    }
                    break;
                case 5 :
                    // InternalAtsDsl.g:5416:2: kw= 'ENABLED'
                    {
                    kw=(Token)match(input,108,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getENABLEDKeyword_4()); 
                        

                    }
                    break;
                case 6 :
                    // InternalAtsDsl.g:5423:2: kw= 'NOT_ENABLED'
                    {
                    kw=(Token)match(input,109,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getNOT_ENABLEDKeyword_5()); 
                        

                    }
                    break;
                case 7 :
                    // InternalAtsDsl.g:5430:2: kw= 'EDITABLE'
                    {
                    kw=(Token)match(input,110,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getEDITABLEKeyword_6()); 
                        

                    }
                    break;
                case 8 :
                    // InternalAtsDsl.g:5437:2: kw= 'NOT_EDITABLE'
                    {
                    kw=(Token)match(input,111,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getNOT_EDITABLEKeyword_7()); 
                        

                    }
                    break;
                case 9 :
                    // InternalAtsDsl.g:5444:2: kw= 'FUTURE_DATE_REQUIRED'
                    {
                    kw=(Token)match(input,112,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getFUTURE_DATE_REQUIREDKeyword_8()); 
                        

                    }
                    break;
                case 10 :
                    // InternalAtsDsl.g:5451:2: kw= 'NOT_FUTURE_DATE_REQUIRED'
                    {
                    kw=(Token)match(input,113,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getNOT_FUTURE_DATE_REQUIREDKeyword_9()); 
                        

                    }
                    break;
                case 11 :
                    // InternalAtsDsl.g:5458:2: kw= 'MULTI_SELECT'
                    {
                    kw=(Token)match(input,114,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getMULTI_SELECTKeyword_10()); 
                        

                    }
                    break;
                case 12 :
                    // InternalAtsDsl.g:5465:2: kw= 'HORIZONTAL_LABEL'
                    {
                    kw=(Token)match(input,115,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getHORIZONTAL_LABELKeyword_11()); 
                        

                    }
                    break;
                case 13 :
                    // InternalAtsDsl.g:5472:2: kw= 'VERTICAL_LABEL'
                    {
                    kw=(Token)match(input,116,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getVERTICAL_LABELKeyword_12()); 
                        

                    }
                    break;
                case 14 :
                    // InternalAtsDsl.g:5479:2: kw= 'LABEL_AFTER'
                    {
                    kw=(Token)match(input,117,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getLABEL_AFTERKeyword_13()); 
                        

                    }
                    break;
                case 15 :
                    // InternalAtsDsl.g:5486:2: kw= 'LABEL_BEFORE'
                    {
                    kw=(Token)match(input,118,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getLABEL_BEFOREKeyword_14()); 
                        

                    }
                    break;
                case 16 :
                    // InternalAtsDsl.g:5493:2: kw= 'NO_LABEL'
                    {
                    kw=(Token)match(input,119,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getNO_LABELKeyword_15()); 
                        

                    }
                    break;
                case 17 :
                    // InternalAtsDsl.g:5500:2: kw= 'SORTED'
                    {
                    kw=(Token)match(input,120,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getSORTEDKeyword_16()); 
                        

                    }
                    break;
                case 18 :
                    // InternalAtsDsl.g:5507:2: kw= 'ADD_DEFAULT_VALUE'
                    {
                    kw=(Token)match(input,121,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getADD_DEFAULT_VALUEKeyword_17()); 
                        

                    }
                    break;
                case 19 :
                    // InternalAtsDsl.g:5514:2: kw= 'NO_DEFAULT_VALUE'
                    {
                    kw=(Token)match(input,122,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getNO_DEFAULT_VALUEKeyword_18()); 
                        

                    }
                    break;
                case 20 :
                    // InternalAtsDsl.g:5521:2: kw= 'BEGIN_COMPOSITE_4'
                    {
                    kw=(Token)match(input,123,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getBEGIN_COMPOSITE_4Keyword_19()); 
                        

                    }
                    break;
                case 21 :
                    // InternalAtsDsl.g:5528:2: kw= 'BEGIN_COMPOSITE_6'
                    {
                    kw=(Token)match(input,124,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getBEGIN_COMPOSITE_6Keyword_20()); 
                        

                    }
                    break;
                case 22 :
                    // InternalAtsDsl.g:5535:2: kw= 'BEGIN_COMPOSITE_8'
                    {
                    kw=(Token)match(input,125,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getBEGIN_COMPOSITE_8Keyword_21()); 
                        

                    }
                    break;
                case 23 :
                    // InternalAtsDsl.g:5542:2: kw= 'BEGIN_COMPOSITE_10'
                    {
                    kw=(Token)match(input,126,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getBEGIN_COMPOSITE_10Keyword_22()); 
                        

                    }
                    break;
                case 24 :
                    // InternalAtsDsl.g:5549:2: kw= 'END_COMPOSITE'
                    {
                    kw=(Token)match(input,127,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getEND_COMPOSITEKeyword_23()); 
                        

                    }
                    break;
                case 25 :
                    // InternalAtsDsl.g:5556:2: kw= 'FILL_NONE'
                    {
                    kw=(Token)match(input,128,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getFILL_NONEKeyword_24()); 
                        

                    }
                    break;
                case 26 :
                    // InternalAtsDsl.g:5563:2: kw= 'FILL_HORIZONTALLY'
                    {
                    kw=(Token)match(input,129,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getFILL_HORIZONTALLYKeyword_25()); 
                        

                    }
                    break;
                case 27 :
                    // InternalAtsDsl.g:5570:2: kw= 'FILL_VERTICALLY'
                    {
                    kw=(Token)match(input,130,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getFILL_VERTICALLYKeyword_26()); 
                        

                    }
                    break;
                case 28 :
                    // InternalAtsDsl.g:5577:2: kw= 'ALIGN_LEFT'
                    {
                    kw=(Token)match(input,131,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getALIGN_LEFTKeyword_27()); 
                        

                    }
                    break;
                case 29 :
                    // InternalAtsDsl.g:5584:2: kw= 'ALIGN_RIGHT'
                    {
                    kw=(Token)match(input,132,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getALIGN_RIGHTKeyword_28()); 
                        

                    }
                    break;
                case 30 :
                    // InternalAtsDsl.g:5591:2: kw= 'ALIGN_CENTER'
                    {
                    kw=(Token)match(input,133,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getWidgetOptionAccess().getALIGN_CENTERKeyword_29()); 
                        

                    }
                    break;
                case 31 :
                    // InternalAtsDsl.g:5598:5: this_WIDGET_OPTION_NAME_30= ruleWIDGET_OPTION_NAME
                    {
                     
                            newCompositeNode(grammarAccess.getWidgetOptionAccess().getWIDGET_OPTION_NAMEParserRuleCall_30()); 
                        
                    pushFollow(FOLLOW_2);
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
    // InternalAtsDsl.g:5616:1: entryRulePAGE_TYPE_NAME returns [String current=null] : iv_rulePAGE_TYPE_NAME= rulePAGE_TYPE_NAME EOF ;
    public final String entryRulePAGE_TYPE_NAME() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_rulePAGE_TYPE_NAME = null;


        try {
            // InternalAtsDsl.g:5617:2: (iv_rulePAGE_TYPE_NAME= rulePAGE_TYPE_NAME EOF )
            // InternalAtsDsl.g:5618:2: iv_rulePAGE_TYPE_NAME= rulePAGE_TYPE_NAME EOF
            {
             newCompositeNode(grammarAccess.getPAGE_TYPE_NAMERule()); 
            pushFollow(FOLLOW_1);
            iv_rulePAGE_TYPE_NAME=rulePAGE_TYPE_NAME();

            state._fsp--;

             current =iv_rulePAGE_TYPE_NAME.getText(); 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:5625:1: rulePAGE_TYPE_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken rulePAGE_TYPE_NAME() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // InternalAtsDsl.g:5628:28: (this_STRING_0= RULE_STRING )
            // InternalAtsDsl.g:5629:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_2); 

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
    // InternalAtsDsl.g:5644:1: entryRulePageType returns [String current=null] : iv_rulePageType= rulePageType EOF ;
    public final String entryRulePageType() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_rulePageType = null;


        try {
            // InternalAtsDsl.g:5645:2: (iv_rulePageType= rulePageType EOF )
            // InternalAtsDsl.g:5646:2: iv_rulePageType= rulePageType EOF
            {
             newCompositeNode(grammarAccess.getPageTypeRule()); 
            pushFollow(FOLLOW_1);
            iv_rulePageType=rulePageType();

            state._fsp--;

             current =iv_rulePageType.getText(); 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:5653:1: rulePageType returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'Working' | kw= 'Completed' | kw= 'Cancelled' | this_PAGE_TYPE_NAME_3= rulePAGE_TYPE_NAME ) ;
    public final AntlrDatatypeRuleToken rulePageType() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        AntlrDatatypeRuleToken this_PAGE_TYPE_NAME_3 = null;


         enterRule(); 
            
        try {
            // InternalAtsDsl.g:5656:28: ( (kw= 'Working' | kw= 'Completed' | kw= 'Cancelled' | this_PAGE_TYPE_NAME_3= rulePAGE_TYPE_NAME ) )
            // InternalAtsDsl.g:5657:1: (kw= 'Working' | kw= 'Completed' | kw= 'Cancelled' | this_PAGE_TYPE_NAME_3= rulePAGE_TYPE_NAME )
            {
            // InternalAtsDsl.g:5657:1: (kw= 'Working' | kw= 'Completed' | kw= 'Cancelled' | this_PAGE_TYPE_NAME_3= rulePAGE_TYPE_NAME )
            int alt131=4;
            switch ( input.LA(1) ) {
            case 134:
                {
                alt131=1;
                }
                break;
            case 135:
                {
                alt131=2;
                }
                break;
            case 136:
                {
                alt131=3;
                }
                break;
            case RULE_STRING:
                {
                alt131=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 131, 0, input);

                throw nvae;
            }

            switch (alt131) {
                case 1 :
                    // InternalAtsDsl.g:5658:2: kw= 'Working'
                    {
                    kw=(Token)match(input,134,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getPageTypeAccess().getWorkingKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // InternalAtsDsl.g:5665:2: kw= 'Completed'
                    {
                    kw=(Token)match(input,135,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getPageTypeAccess().getCompletedKeyword_1()); 
                        

                    }
                    break;
                case 3 :
                    // InternalAtsDsl.g:5672:2: kw= 'Cancelled'
                    {
                    kw=(Token)match(input,136,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getPageTypeAccess().getCancelledKeyword_2()); 
                        

                    }
                    break;
                case 4 :
                    // InternalAtsDsl.g:5679:5: this_PAGE_TYPE_NAME_3= rulePAGE_TYPE_NAME
                    {
                     
                            newCompositeNode(grammarAccess.getPageTypeAccess().getPAGE_TYPE_NAMEParserRuleCall_3()); 
                        
                    pushFollow(FOLLOW_2);
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
    // InternalAtsDsl.g:5697:1: entryRuleCOLOR_NAME returns [String current=null] : iv_ruleCOLOR_NAME= ruleCOLOR_NAME EOF ;
    public final String entryRuleCOLOR_NAME() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleCOLOR_NAME = null;


        try {
            // InternalAtsDsl.g:5698:2: (iv_ruleCOLOR_NAME= ruleCOLOR_NAME EOF )
            // InternalAtsDsl.g:5699:2: iv_ruleCOLOR_NAME= ruleCOLOR_NAME EOF
            {
             newCompositeNode(grammarAccess.getCOLOR_NAMERule()); 
            pushFollow(FOLLOW_1);
            iv_ruleCOLOR_NAME=ruleCOLOR_NAME();

            state._fsp--;

             current =iv_ruleCOLOR_NAME.getText(); 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:5706:1: ruleCOLOR_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleCOLOR_NAME() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         enterRule(); 
            
        try {
            // InternalAtsDsl.g:5709:28: (this_STRING_0= RULE_STRING )
            // InternalAtsDsl.g:5710:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_2); 

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
    // InternalAtsDsl.g:5725:1: entryRuleStateColor returns [String current=null] : iv_ruleStateColor= ruleStateColor EOF ;
    public final String entryRuleStateColor() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleStateColor = null;


        try {
            // InternalAtsDsl.g:5726:2: (iv_ruleStateColor= ruleStateColor EOF )
            // InternalAtsDsl.g:5727:2: iv_ruleStateColor= ruleStateColor EOF
            {
             newCompositeNode(grammarAccess.getStateColorRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleStateColor=ruleStateColor();

            state._fsp--;

             current =iv_ruleStateColor.getText(); 
            match(input,EOF,FOLLOW_2); 

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
    // InternalAtsDsl.g:5734:1: ruleStateColor returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'BLACK' | kw= 'WHITE' | kw= 'RED' | kw= 'DARK_RED' | kw= 'GREEN' | kw= 'DARK_GREEN' | kw= 'YELLOW' | kw= 'DARK_YELLOW' | kw= 'BLUE' | kw= 'DARK_BLUE' | kw= 'MAGENTA' | kw= 'DARK_MAGENTA' | kw= 'CYAN' | kw= 'DARK_CYAN' | kw= 'GRAY' | kw= 'DARK_GRAY' | this_COLOR_NAME_16= ruleCOLOR_NAME ) ;
    public final AntlrDatatypeRuleToken ruleStateColor() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        AntlrDatatypeRuleToken this_COLOR_NAME_16 = null;


         enterRule(); 
            
        try {
            // InternalAtsDsl.g:5737:28: ( (kw= 'BLACK' | kw= 'WHITE' | kw= 'RED' | kw= 'DARK_RED' | kw= 'GREEN' | kw= 'DARK_GREEN' | kw= 'YELLOW' | kw= 'DARK_YELLOW' | kw= 'BLUE' | kw= 'DARK_BLUE' | kw= 'MAGENTA' | kw= 'DARK_MAGENTA' | kw= 'CYAN' | kw= 'DARK_CYAN' | kw= 'GRAY' | kw= 'DARK_GRAY' | this_COLOR_NAME_16= ruleCOLOR_NAME ) )
            // InternalAtsDsl.g:5738:1: (kw= 'BLACK' | kw= 'WHITE' | kw= 'RED' | kw= 'DARK_RED' | kw= 'GREEN' | kw= 'DARK_GREEN' | kw= 'YELLOW' | kw= 'DARK_YELLOW' | kw= 'BLUE' | kw= 'DARK_BLUE' | kw= 'MAGENTA' | kw= 'DARK_MAGENTA' | kw= 'CYAN' | kw= 'DARK_CYAN' | kw= 'GRAY' | kw= 'DARK_GRAY' | this_COLOR_NAME_16= ruleCOLOR_NAME )
            {
            // InternalAtsDsl.g:5738:1: (kw= 'BLACK' | kw= 'WHITE' | kw= 'RED' | kw= 'DARK_RED' | kw= 'GREEN' | kw= 'DARK_GREEN' | kw= 'YELLOW' | kw= 'DARK_YELLOW' | kw= 'BLUE' | kw= 'DARK_BLUE' | kw= 'MAGENTA' | kw= 'DARK_MAGENTA' | kw= 'CYAN' | kw= 'DARK_CYAN' | kw= 'GRAY' | kw= 'DARK_GRAY' | this_COLOR_NAME_16= ruleCOLOR_NAME )
            int alt132=17;
            switch ( input.LA(1) ) {
            case 137:
                {
                alt132=1;
                }
                break;
            case 138:
                {
                alt132=2;
                }
                break;
            case 139:
                {
                alt132=3;
                }
                break;
            case 140:
                {
                alt132=4;
                }
                break;
            case 141:
                {
                alt132=5;
                }
                break;
            case 142:
                {
                alt132=6;
                }
                break;
            case 143:
                {
                alt132=7;
                }
                break;
            case 144:
                {
                alt132=8;
                }
                break;
            case 145:
                {
                alt132=9;
                }
                break;
            case 146:
                {
                alt132=10;
                }
                break;
            case 147:
                {
                alt132=11;
                }
                break;
            case 148:
                {
                alt132=12;
                }
                break;
            case 149:
                {
                alt132=13;
                }
                break;
            case 150:
                {
                alt132=14;
                }
                break;
            case 151:
                {
                alt132=15;
                }
                break;
            case 152:
                {
                alt132=16;
                }
                break;
            case RULE_STRING:
                {
                alt132=17;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 132, 0, input);

                throw nvae;
            }

            switch (alt132) {
                case 1 :
                    // InternalAtsDsl.g:5739:2: kw= 'BLACK'
                    {
                    kw=(Token)match(input,137,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getStateColorAccess().getBLACKKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // InternalAtsDsl.g:5746:2: kw= 'WHITE'
                    {
                    kw=(Token)match(input,138,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getStateColorAccess().getWHITEKeyword_1()); 
                        

                    }
                    break;
                case 3 :
                    // InternalAtsDsl.g:5753:2: kw= 'RED'
                    {
                    kw=(Token)match(input,139,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getStateColorAccess().getREDKeyword_2()); 
                        

                    }
                    break;
                case 4 :
                    // InternalAtsDsl.g:5760:2: kw= 'DARK_RED'
                    {
                    kw=(Token)match(input,140,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getStateColorAccess().getDARK_REDKeyword_3()); 
                        

                    }
                    break;
                case 5 :
                    // InternalAtsDsl.g:5767:2: kw= 'GREEN'
                    {
                    kw=(Token)match(input,141,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getStateColorAccess().getGREENKeyword_4()); 
                        

                    }
                    break;
                case 6 :
                    // InternalAtsDsl.g:5774:2: kw= 'DARK_GREEN'
                    {
                    kw=(Token)match(input,142,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getStateColorAccess().getDARK_GREENKeyword_5()); 
                        

                    }
                    break;
                case 7 :
                    // InternalAtsDsl.g:5781:2: kw= 'YELLOW'
                    {
                    kw=(Token)match(input,143,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getStateColorAccess().getYELLOWKeyword_6()); 
                        

                    }
                    break;
                case 8 :
                    // InternalAtsDsl.g:5788:2: kw= 'DARK_YELLOW'
                    {
                    kw=(Token)match(input,144,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getStateColorAccess().getDARK_YELLOWKeyword_7()); 
                        

                    }
                    break;
                case 9 :
                    // InternalAtsDsl.g:5795:2: kw= 'BLUE'
                    {
                    kw=(Token)match(input,145,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getStateColorAccess().getBLUEKeyword_8()); 
                        

                    }
                    break;
                case 10 :
                    // InternalAtsDsl.g:5802:2: kw= 'DARK_BLUE'
                    {
                    kw=(Token)match(input,146,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getStateColorAccess().getDARK_BLUEKeyword_9()); 
                        

                    }
                    break;
                case 11 :
                    // InternalAtsDsl.g:5809:2: kw= 'MAGENTA'
                    {
                    kw=(Token)match(input,147,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getStateColorAccess().getMAGENTAKeyword_10()); 
                        

                    }
                    break;
                case 12 :
                    // InternalAtsDsl.g:5816:2: kw= 'DARK_MAGENTA'
                    {
                    kw=(Token)match(input,148,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getStateColorAccess().getDARK_MAGENTAKeyword_11()); 
                        

                    }
                    break;
                case 13 :
                    // InternalAtsDsl.g:5823:2: kw= 'CYAN'
                    {
                    kw=(Token)match(input,149,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getStateColorAccess().getCYANKeyword_12()); 
                        

                    }
                    break;
                case 14 :
                    // InternalAtsDsl.g:5830:2: kw= 'DARK_CYAN'
                    {
                    kw=(Token)match(input,150,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getStateColorAccess().getDARK_CYANKeyword_13()); 
                        

                    }
                    break;
                case 15 :
                    // InternalAtsDsl.g:5837:2: kw= 'GRAY'
                    {
                    kw=(Token)match(input,151,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getStateColorAccess().getGRAYKeyword_14()); 
                        

                    }
                    break;
                case 16 :
                    // InternalAtsDsl.g:5844:2: kw= 'DARK_GRAY'
                    {
                    kw=(Token)match(input,152,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getStateColorAccess().getDARK_GRAYKeyword_15()); 
                        

                    }
                    break;
                case 17 :
                    // InternalAtsDsl.g:5851:5: this_COLOR_NAME_16= ruleCOLOR_NAME
                    {
                     
                            newCompositeNode(grammarAccess.getStateColorAccess().getCOLOR_NAMEParserRuleCall_16()); 
                        
                    pushFollow(FOLLOW_2);
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


    // $ANTLR start "ruleOnEventType"
    // InternalAtsDsl.g:5869:1: ruleOnEventType returns [Enumerator current=null] : ( (enumLiteral_0= 'CreateBranch' ) | (enumLiteral_1= 'CommitBranch' ) | (enumLiteral_2= 'CreateWorkflow' ) | (enumLiteral_3= 'TransitionTo' ) | (enumLiteral_4= 'Manual' ) ) ;
    public final Enumerator ruleOnEventType() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;
        Token enumLiteral_2=null;
        Token enumLiteral_3=null;
        Token enumLiteral_4=null;

         enterRule(); 
        try {
            // InternalAtsDsl.g:5871:28: ( ( (enumLiteral_0= 'CreateBranch' ) | (enumLiteral_1= 'CommitBranch' ) | (enumLiteral_2= 'CreateWorkflow' ) | (enumLiteral_3= 'TransitionTo' ) | (enumLiteral_4= 'Manual' ) ) )
            // InternalAtsDsl.g:5872:1: ( (enumLiteral_0= 'CreateBranch' ) | (enumLiteral_1= 'CommitBranch' ) | (enumLiteral_2= 'CreateWorkflow' ) | (enumLiteral_3= 'TransitionTo' ) | (enumLiteral_4= 'Manual' ) )
            {
            // InternalAtsDsl.g:5872:1: ( (enumLiteral_0= 'CreateBranch' ) | (enumLiteral_1= 'CommitBranch' ) | (enumLiteral_2= 'CreateWorkflow' ) | (enumLiteral_3= 'TransitionTo' ) | (enumLiteral_4= 'Manual' ) )
            int alt133=5;
            switch ( input.LA(1) ) {
            case 153:
                {
                alt133=1;
                }
                break;
            case 154:
                {
                alt133=2;
                }
                break;
            case 155:
                {
                alt133=3;
                }
                break;
            case 156:
                {
                alt133=4;
                }
                break;
            case 157:
                {
                alt133=5;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 133, 0, input);

                throw nvae;
            }

            switch (alt133) {
                case 1 :
                    // InternalAtsDsl.g:5872:2: (enumLiteral_0= 'CreateBranch' )
                    {
                    // InternalAtsDsl.g:5872:2: (enumLiteral_0= 'CreateBranch' )
                    // InternalAtsDsl.g:5872:4: enumLiteral_0= 'CreateBranch'
                    {
                    enumLiteral_0=(Token)match(input,153,FOLLOW_2); 

                            current = grammarAccess.getOnEventTypeAccess().getCreateBranchEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_0, grammarAccess.getOnEventTypeAccess().getCreateBranchEnumLiteralDeclaration_0()); 
                        

                    }


                    }
                    break;
                case 2 :
                    // InternalAtsDsl.g:5878:6: (enumLiteral_1= 'CommitBranch' )
                    {
                    // InternalAtsDsl.g:5878:6: (enumLiteral_1= 'CommitBranch' )
                    // InternalAtsDsl.g:5878:8: enumLiteral_1= 'CommitBranch'
                    {
                    enumLiteral_1=(Token)match(input,154,FOLLOW_2); 

                            current = grammarAccess.getOnEventTypeAccess().getCommitBranchEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_1, grammarAccess.getOnEventTypeAccess().getCommitBranchEnumLiteralDeclaration_1()); 
                        

                    }


                    }
                    break;
                case 3 :
                    // InternalAtsDsl.g:5884:6: (enumLiteral_2= 'CreateWorkflow' )
                    {
                    // InternalAtsDsl.g:5884:6: (enumLiteral_2= 'CreateWorkflow' )
                    // InternalAtsDsl.g:5884:8: enumLiteral_2= 'CreateWorkflow'
                    {
                    enumLiteral_2=(Token)match(input,155,FOLLOW_2); 

                            current = grammarAccess.getOnEventTypeAccess().getCreateWorkflowEnumLiteralDeclaration_2().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_2, grammarAccess.getOnEventTypeAccess().getCreateWorkflowEnumLiteralDeclaration_2()); 
                        

                    }


                    }
                    break;
                case 4 :
                    // InternalAtsDsl.g:5890:6: (enumLiteral_3= 'TransitionTo' )
                    {
                    // InternalAtsDsl.g:5890:6: (enumLiteral_3= 'TransitionTo' )
                    // InternalAtsDsl.g:5890:8: enumLiteral_3= 'TransitionTo'
                    {
                    enumLiteral_3=(Token)match(input,156,FOLLOW_2); 

                            current = grammarAccess.getOnEventTypeAccess().getTransitionToEnumLiteralDeclaration_3().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_3, grammarAccess.getOnEventTypeAccess().getTransitionToEnumLiteralDeclaration_3()); 
                        

                    }


                    }
                    break;
                case 5 :
                    // InternalAtsDsl.g:5896:6: (enumLiteral_4= 'Manual' )
                    {
                    // InternalAtsDsl.g:5896:6: (enumLiteral_4= 'Manual' )
                    // InternalAtsDsl.g:5896:8: enumLiteral_4= 'Manual'
                    {
                    enumLiteral_4=(Token)match(input,157,FOLLOW_2); 

                            current = grammarAccess.getOnEventTypeAccess().getManualEnumLiteralDeclaration_4().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_4, grammarAccess.getOnEventTypeAccess().getManualEnumLiteralDeclaration_4()); 
                        

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
    // $ANTLR end "ruleOnEventType"


    // $ANTLR start "ruleBooleanDef"
    // InternalAtsDsl.g:5906:1: ruleBooleanDef returns [Enumerator current=null] : ( (enumLiteral_0= 'None' ) | (enumLiteral_1= 'True' ) | (enumLiteral_2= 'False' ) ) ;
    public final Enumerator ruleBooleanDef() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;
        Token enumLiteral_2=null;

         enterRule(); 
        try {
            // InternalAtsDsl.g:5908:28: ( ( (enumLiteral_0= 'None' ) | (enumLiteral_1= 'True' ) | (enumLiteral_2= 'False' ) ) )
            // InternalAtsDsl.g:5909:1: ( (enumLiteral_0= 'None' ) | (enumLiteral_1= 'True' ) | (enumLiteral_2= 'False' ) )
            {
            // InternalAtsDsl.g:5909:1: ( (enumLiteral_0= 'None' ) | (enumLiteral_1= 'True' ) | (enumLiteral_2= 'False' ) )
            int alt134=3;
            switch ( input.LA(1) ) {
            case 84:
                {
                alt134=1;
                }
                break;
            case 158:
                {
                alt134=2;
                }
                break;
            case 159:
                {
                alt134=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 134, 0, input);

                throw nvae;
            }

            switch (alt134) {
                case 1 :
                    // InternalAtsDsl.g:5909:2: (enumLiteral_0= 'None' )
                    {
                    // InternalAtsDsl.g:5909:2: (enumLiteral_0= 'None' )
                    // InternalAtsDsl.g:5909:4: enumLiteral_0= 'None'
                    {
                    enumLiteral_0=(Token)match(input,84,FOLLOW_2); 

                            current = grammarAccess.getBooleanDefAccess().getNoneEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_0, grammarAccess.getBooleanDefAccess().getNoneEnumLiteralDeclaration_0()); 
                        

                    }


                    }
                    break;
                case 2 :
                    // InternalAtsDsl.g:5915:6: (enumLiteral_1= 'True' )
                    {
                    // InternalAtsDsl.g:5915:6: (enumLiteral_1= 'True' )
                    // InternalAtsDsl.g:5915:8: enumLiteral_1= 'True'
                    {
                    enumLiteral_1=(Token)match(input,158,FOLLOW_2); 

                            current = grammarAccess.getBooleanDefAccess().getTrueEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_1, grammarAccess.getBooleanDefAccess().getTrueEnumLiteralDeclaration_1()); 
                        

                    }


                    }
                    break;
                case 3 :
                    // InternalAtsDsl.g:5921:6: (enumLiteral_2= 'False' )
                    {
                    // InternalAtsDsl.g:5921:6: (enumLiteral_2= 'False' )
                    // InternalAtsDsl.g:5921:8: enumLiteral_2= 'False'
                    {
                    enumLiteral_2=(Token)match(input,159,FOLLOW_2); 

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
    // InternalAtsDsl.g:5931:1: ruleWorkflowEventType returns [Enumerator current=null] : ( (enumLiteral_0= 'TransitionTo' ) | (enumLiteral_1= 'CreateBranch' ) | (enumLiteral_2= 'CommitBranch' ) ) ;
    public final Enumerator ruleWorkflowEventType() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;
        Token enumLiteral_2=null;

         enterRule(); 
        try {
            // InternalAtsDsl.g:5933:28: ( ( (enumLiteral_0= 'TransitionTo' ) | (enumLiteral_1= 'CreateBranch' ) | (enumLiteral_2= 'CommitBranch' ) ) )
            // InternalAtsDsl.g:5934:1: ( (enumLiteral_0= 'TransitionTo' ) | (enumLiteral_1= 'CreateBranch' ) | (enumLiteral_2= 'CommitBranch' ) )
            {
            // InternalAtsDsl.g:5934:1: ( (enumLiteral_0= 'TransitionTo' ) | (enumLiteral_1= 'CreateBranch' ) | (enumLiteral_2= 'CommitBranch' ) )
            int alt135=3;
            switch ( input.LA(1) ) {
            case 156:
                {
                alt135=1;
                }
                break;
            case 153:
                {
                alt135=2;
                }
                break;
            case 154:
                {
                alt135=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 135, 0, input);

                throw nvae;
            }

            switch (alt135) {
                case 1 :
                    // InternalAtsDsl.g:5934:2: (enumLiteral_0= 'TransitionTo' )
                    {
                    // InternalAtsDsl.g:5934:2: (enumLiteral_0= 'TransitionTo' )
                    // InternalAtsDsl.g:5934:4: enumLiteral_0= 'TransitionTo'
                    {
                    enumLiteral_0=(Token)match(input,156,FOLLOW_2); 

                            current = grammarAccess.getWorkflowEventTypeAccess().getTransitionToEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_0, grammarAccess.getWorkflowEventTypeAccess().getTransitionToEnumLiteralDeclaration_0()); 
                        

                    }


                    }
                    break;
                case 2 :
                    // InternalAtsDsl.g:5940:6: (enumLiteral_1= 'CreateBranch' )
                    {
                    // InternalAtsDsl.g:5940:6: (enumLiteral_1= 'CreateBranch' )
                    // InternalAtsDsl.g:5940:8: enumLiteral_1= 'CreateBranch'
                    {
                    enumLiteral_1=(Token)match(input,153,FOLLOW_2); 

                            current = grammarAccess.getWorkflowEventTypeAccess().getCreateBranchEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_1, grammarAccess.getWorkflowEventTypeAccess().getCreateBranchEnumLiteralDeclaration_1()); 
                        

                    }


                    }
                    break;
                case 3 :
                    // InternalAtsDsl.g:5946:6: (enumLiteral_2= 'CommitBranch' )
                    {
                    // InternalAtsDsl.g:5946:6: (enumLiteral_2= 'CommitBranch' )
                    // InternalAtsDsl.g:5946:8: enumLiteral_2= 'CommitBranch'
                    {
                    enumLiteral_2=(Token)match(input,154,FOLLOW_2); 

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
    // InternalAtsDsl.g:5956:1: ruleReviewBlockingType returns [Enumerator current=null] : ( (enumLiteral_0= 'Transition' ) | (enumLiteral_1= 'Commit' ) ) ;
    public final Enumerator ruleReviewBlockingType() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;

         enterRule(); 
        try {
            // InternalAtsDsl.g:5958:28: ( ( (enumLiteral_0= 'Transition' ) | (enumLiteral_1= 'Commit' ) ) )
            // InternalAtsDsl.g:5959:1: ( (enumLiteral_0= 'Transition' ) | (enumLiteral_1= 'Commit' ) )
            {
            // InternalAtsDsl.g:5959:1: ( (enumLiteral_0= 'Transition' ) | (enumLiteral_1= 'Commit' ) )
            int alt136=2;
            int LA136_0 = input.LA(1);

            if ( (LA136_0==160) ) {
                alt136=1;
            }
            else if ( (LA136_0==161) ) {
                alt136=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 136, 0, input);

                throw nvae;
            }
            switch (alt136) {
                case 1 :
                    // InternalAtsDsl.g:5959:2: (enumLiteral_0= 'Transition' )
                    {
                    // InternalAtsDsl.g:5959:2: (enumLiteral_0= 'Transition' )
                    // InternalAtsDsl.g:5959:4: enumLiteral_0= 'Transition'
                    {
                    enumLiteral_0=(Token)match(input,160,FOLLOW_2); 

                            current = grammarAccess.getReviewBlockingTypeAccess().getTransitionEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_0, grammarAccess.getReviewBlockingTypeAccess().getTransitionEnumLiteralDeclaration_0()); 
                        

                    }


                    }
                    break;
                case 2 :
                    // InternalAtsDsl.g:5965:6: (enumLiteral_1= 'Commit' )
                    {
                    // InternalAtsDsl.g:5965:6: (enumLiteral_1= 'Commit' )
                    // InternalAtsDsl.g:5965:8: enumLiteral_1= 'Commit'
                    {
                    enumLiteral_1=(Token)match(input,161,FOLLOW_2); 

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


    // $ANTLR start "ruleRuleLocation"
    // InternalAtsDsl.g:5975:1: ruleRuleLocation returns [Enumerator current=null] : ( (enumLiteral_0= 'StateDefinition' ) | (enumLiteral_1= 'TeamDefinition' ) | (enumLiteral_2= 'ActionableItem' ) ) ;
    public final Enumerator ruleRuleLocation() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;
        Token enumLiteral_2=null;

         enterRule(); 
        try {
            // InternalAtsDsl.g:5977:28: ( ( (enumLiteral_0= 'StateDefinition' ) | (enumLiteral_1= 'TeamDefinition' ) | (enumLiteral_2= 'ActionableItem' ) ) )
            // InternalAtsDsl.g:5978:1: ( (enumLiteral_0= 'StateDefinition' ) | (enumLiteral_1= 'TeamDefinition' ) | (enumLiteral_2= 'ActionableItem' ) )
            {
            // InternalAtsDsl.g:5978:1: ( (enumLiteral_0= 'StateDefinition' ) | (enumLiteral_1= 'TeamDefinition' ) | (enumLiteral_2= 'ActionableItem' ) )
            int alt137=3;
            switch ( input.LA(1) ) {
            case 162:
                {
                alt137=1;
                }
                break;
            case 163:
                {
                alt137=2;
                }
                break;
            case 164:
                {
                alt137=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 137, 0, input);

                throw nvae;
            }

            switch (alt137) {
                case 1 :
                    // InternalAtsDsl.g:5978:2: (enumLiteral_0= 'StateDefinition' )
                    {
                    // InternalAtsDsl.g:5978:2: (enumLiteral_0= 'StateDefinition' )
                    // InternalAtsDsl.g:5978:4: enumLiteral_0= 'StateDefinition'
                    {
                    enumLiteral_0=(Token)match(input,162,FOLLOW_2); 

                            current = grammarAccess.getRuleLocationAccess().getStateDefinitionEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_0, grammarAccess.getRuleLocationAccess().getStateDefinitionEnumLiteralDeclaration_0()); 
                        

                    }


                    }
                    break;
                case 2 :
                    // InternalAtsDsl.g:5984:6: (enumLiteral_1= 'TeamDefinition' )
                    {
                    // InternalAtsDsl.g:5984:6: (enumLiteral_1= 'TeamDefinition' )
                    // InternalAtsDsl.g:5984:8: enumLiteral_1= 'TeamDefinition'
                    {
                    enumLiteral_1=(Token)match(input,163,FOLLOW_2); 

                            current = grammarAccess.getRuleLocationAccess().getTeamDefinitionEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_1, grammarAccess.getRuleLocationAccess().getTeamDefinitionEnumLiteralDeclaration_1()); 
                        

                    }


                    }
                    break;
                case 3 :
                    // InternalAtsDsl.g:5990:6: (enumLiteral_2= 'ActionableItem' )
                    {
                    // InternalAtsDsl.g:5990:6: (enumLiteral_2= 'ActionableItem' )
                    // InternalAtsDsl.g:5990:8: enumLiteral_2= 'ActionableItem'
                    {
                    enumLiteral_2=(Token)match(input,164,FOLLOW_2); 

                            current = grammarAccess.getRuleLocationAccess().getActionableItemEnumLiteralDeclaration_2().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_2, grammarAccess.getRuleLocationAccess().getActionableItemEnumLiteralDeclaration_2()); 
                        

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
    // $ANTLR end "ruleRuleLocation"

    // Delegated rules


    protected DFA128 dfa128 = new DFA128(this);
    protected DFA129 dfa129 = new DFA129(this);
    static final String dfa_1s = "\51\uffff";
    static final String dfa_2s = "\1\127\1\4\1\21\1\103\1\4\1\61\1\4\1\u00a2\1\130\3\104\2\4\1\uffff\1\u00a0\1\4\1\105\2\106\2\4\1\22\1\u0099\1\124\2\4\1\124\1\104\3\26\3\23\1\24\1\25\3\26\1\uffff";
    static final String dfa_3s = "\1\127\1\4\1\21\1\103\1\4\1\130\1\4\1\u00a4\1\130\3\131\2\4\1\uffff\1\u00a1\1\131\1\113\2\106\2\131\1\26\1\u009c\1\u009f\2\4\1\u009f\1\131\3\110\10\26\1\uffff";
    static final String dfa_4s = "\16\uffff\1\2\31\uffff\1\1";
    static final String dfa_5s = "\51\uffff}>";
    static final String[] dfa_6s = {
            "\1\1",
            "\1\2",
            "\1\3",
            "\1\4",
            "\1\5",
            "\1\6\46\uffff\1\7",
            "\1\10",
            "\1\11\1\12\1\13",
            "\1\7",
            "\1\15\1\17\5\uffff\1\16\14\uffff\1\7\1\14",
            "\1\15\1\17\5\uffff\1\16\14\uffff\1\7\1\14",
            "\1\15\1\17\5\uffff\1\16\14\uffff\1\7\1\14",
            "\1\20",
            "\1\21",
            "",
            "\1\22\1\23",
            "\1\25\14\uffff\1\26\62\uffff\1\15\1\17\5\uffff\1\16\7\uffff\1\24\5\uffff\1\14",
            "\1\17\5\uffff\1\16",
            "\1\27",
            "\1\27",
            "\1\25\14\uffff\1\26\62\uffff\1\15\1\17\5\uffff\1\16\7\uffff\1\24\5\uffff\1\14",
            "\1\25\14\uffff\1\26\62\uffff\1\15\1\17\5\uffff\1\16\7\uffff\1\24\5\uffff\1\14",
            "\1\30\1\31\1\32\1\33\1\34",
            "\1\36\1\37\1\uffff\1\35",
            "\1\40\111\uffff\1\41\1\42",
            "\1\43",
            "\1\44",
            "\1\45\111\uffff\1\46\1\47",
            "\1\15\1\17\5\uffff\1\16\15\uffff\1\14",
            "\1\16\4\uffff\1\16\54\uffff\1\50",
            "\1\16\4\uffff\1\16\54\uffff\1\50",
            "\1\16\4\uffff\1\16\54\uffff\1\50",
            "\1\31\1\32\1\33\1\34",
            "\1\31\1\32\1\33\1\34",
            "\1\31\1\32\1\33\1\34",
            "\1\32\1\33\1\34",
            "\1\33\1\34",
            "\1\34",
            "\1\34",
            "\1\34",
            ""
    };

    static final short[] dfa_1 = DFA.unpackEncodedString(dfa_1s);
    static final char[] dfa_2 = DFA.unpackEncodedStringToUnsignedChars(dfa_2s);
    static final char[] dfa_3 = DFA.unpackEncodedStringToUnsignedChars(dfa_3s);
    static final short[] dfa_4 = DFA.unpackEncodedString(dfa_4s);
    static final short[] dfa_5 = DFA.unpackEncodedString(dfa_5s);
    static final short[][] dfa_6 = unpackEncodedStringArray(dfa_6s);

    class DFA128 extends DFA {

        public DFA128(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 128;
            this.eot = dfa_1;
            this.eof = dfa_1;
            this.min = dfa_2;
            this.max = dfa_3;
            this.accept = dfa_4;
            this.special = dfa_5;
            this.transition = dfa_6;
        }
        public String getDescription() {
            return "5269:1: (this_CreateDecisionReviewRuleDef_0= ruleCreateDecisionReviewRuleDef | this_CreatePeerReviewRuleDef_1= ruleCreatePeerReviewRuleDef )";
        }
    }
    static final String dfa_7s = "\41\uffff";
    static final String dfa_8s = "\1\127\1\4\1\21\1\103\1\4\1\61\1\4\1\u00a2\1\130\3\26\1\4\3\uffff\3\4\1\22\1\124\2\4\1\124\1\26\3\23\1\24\1\25\3\26";
    static final String dfa_9s = "\1\127\1\4\1\21\1\103\1\4\1\130\1\4\1\u00a4\1\130\3\133\1\4\3\uffff\3\133\1\26\1\u009f\2\4\1\u009f\1\133\10\26";
    static final String dfa_10s = "\15\uffff\1\2\1\1\1\3\21\uffff";
    static final String dfa_11s = "\41\uffff}>";
    static final String[] dfa_12s = {
            "\1\1",
            "\1\2",
            "\1\3",
            "\1\4",
            "\1\5",
            "\1\6\46\uffff\1\7",
            "\1\10",
            "\1\11\1\12\1\13",
            "\1\7",
            "\1\16\4\uffff\1\15\50\uffff\2\17\1\15\4\uffff\1\17\14\uffff\1\7\1\14\2\15",
            "\1\16\4\uffff\1\15\50\uffff\2\17\1\15\4\uffff\1\17\14\uffff\1\7\1\14\2\15",
            "\1\16\4\uffff\1\15\50\uffff\2\17\1\15\4\uffff\1\17\14\uffff\1\7\1\14\2\15",
            "\1\20",
            "",
            "",
            "",
            "\1\22\14\uffff\1\23\4\uffff\1\15\4\uffff\1\15\50\uffff\2\17\1\15\4\uffff\1\17\7\uffff\1\21\5\uffff\1\14\2\15",
            "\1\22\14\uffff\1\23\4\uffff\1\15\4\uffff\1\15\50\uffff\2\17\1\15\4\uffff\1\17\7\uffff\1\21\5\uffff\1\14\2\15",
            "\1\22\14\uffff\1\23\4\uffff\1\15\4\uffff\1\15\50\uffff\2\17\1\15\4\uffff\1\17\7\uffff\1\21\5\uffff\1\14\2\15",
            "\1\24\1\25\1\26\1\27\1\30",
            "\1\31\111\uffff\1\32\1\33",
            "\1\34",
            "\1\35",
            "\1\36\111\uffff\1\37\1\40",
            "\1\15\4\uffff\1\15\50\uffff\2\17\1\15\4\uffff\1\17\15\uffff\1\14\2\15",
            "\1\25\1\26\1\27\1\30",
            "\1\25\1\26\1\27\1\30",
            "\1\25\1\26\1\27\1\30",
            "\1\26\1\27\1\30",
            "\1\27\1\30",
            "\1\30",
            "\1\30",
            "\1\30"
    };

    static final short[] dfa_7 = DFA.unpackEncodedString(dfa_7s);
    static final char[] dfa_8 = DFA.unpackEncodedStringToUnsignedChars(dfa_8s);
    static final char[] dfa_9 = DFA.unpackEncodedStringToUnsignedChars(dfa_9s);
    static final short[] dfa_10 = DFA.unpackEncodedString(dfa_10s);
    static final short[] dfa_11 = DFA.unpackEncodedString(dfa_11s);
    static final short[][] dfa_12 = unpackEncodedStringArray(dfa_12s);

    class DFA129 extends DFA {

        public DFA129(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 129;
            this.eot = dfa_7;
            this.eof = dfa_7;
            this.min = dfa_8;
            this.max = dfa_9;
            this.accept = dfa_10;
            this.special = dfa_11;
            this.transition = dfa_12;
        }
        public String getDescription() {
            return "5309:1: (this_RuleDef_0= ruleRuleDef | this_CreateTaskRuleDef_1= ruleCreateTaskRuleDef | this_ReviewRule_2= ruleReviewRule )";
        }
    }
 

    public static final BitSet FOLLOW_1 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_2 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_3 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_4 = new BitSet(new long[]{0x000000000000F802L});
    public static final BitSet FOLLOW_5 = new BitSet(new long[]{0x000000000000F002L});
    public static final BitSet FOLLOW_6 = new BitSet(new long[]{0x000000000000E002L});
    public static final BitSet FOLLOW_7 = new BitSet(new long[]{0x000000000000C002L});
    public static final BitSet FOLLOW_8 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_9 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_10 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_11 = new BitSet(new long[]{0x0000000000020012L,0x0000000000080000L});
    public static final BitSet FOLLOW_12 = new BitSet(new long[]{0x00000000007C0000L});
    public static final BitSet FOLLOW_13 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L,0x00000000C0000000L});
    public static final BitSet FOLLOW_14 = new BitSet(new long[]{0x0000000000780000L});
    public static final BitSet FOLLOW_15 = new BitSet(new long[]{0x0000000000700000L});
    public static final BitSet FOLLOW_16 = new BitSet(new long[]{0x0000000000600000L});
    public static final BitSet FOLLOW_17 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_18 = new BitSet(new long[]{0x0000000000820000L});
    public static final BitSet FOLLOW_19 = new BitSet(new long[]{0x0000000001800000L});
    public static final BitSet FOLLOW_20 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_21 = new BitSet(new long[]{0x0000000000C00000L});
    public static final BitSet FOLLOW_22 = new BitSet(new long[]{0x0000000000020010L,0x0000000000080000L});
    public static final BitSet FOLLOW_23 = new BitSet(new long[]{0x000000000F441000L});
    public static final BitSet FOLLOW_24 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_25 = new BitSet(new long[]{0x000000000E441000L});
    public static final BitSet FOLLOW_26 = new BitSet(new long[]{0x000000000C441000L});
    public static final BitSet FOLLOW_27 = new BitSet(new long[]{0x000000000C401000L});
    public static final BitSet FOLLOW_28 = new BitSet(new long[]{0x0000000008401000L});
    public static final BitSet FOLLOW_29 = new BitSet(new long[]{0x0000000008400000L});
    public static final BitSet FOLLOW_30 = new BitSet(new long[]{0x0000001FF1454000L});
    public static final BitSet FOLLOW_31 = new BitSet(new long[]{0x0000001FF0454000L});
    public static final BitSet FOLLOW_32 = new BitSet(new long[]{0x0000001FF0414000L});
    public static final BitSet FOLLOW_33 = new BitSet(new long[]{0x0000000000080000L,0x0000000000002000L});
    public static final BitSet FOLLOW_34 = new BitSet(new long[]{0x0000001FE0414000L});
    public static final BitSet FOLLOW_35 = new BitSet(new long[]{0x0000001FC0414000L});
    public static final BitSet FOLLOW_36 = new BitSet(new long[]{0x0000001F80414000L});
    public static final BitSet FOLLOW_37 = new BitSet(new long[]{0x0000001F00410000L});
    public static final BitSet FOLLOW_38 = new BitSet(new long[]{0x0000001E00410000L});
    public static final BitSet FOLLOW_39 = new BitSet(new long[]{0x0000001C00410000L});
    public static final BitSet FOLLOW_40 = new BitSet(new long[]{0x0000001800410000L});
    public static final BitSet FOLLOW_41 = new BitSet(new long[]{0x0000000000000010L,0x000000FFF0000000L});
    public static final BitSet FOLLOW_42 = new BitSet(new long[]{0x0000001000410000L});
    public static final BitSet FOLLOW_43 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_44 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_45 = new BitSet(new long[]{0x0000000000401000L});
    public static final BitSet FOLLOW_46 = new BitSet(new long[]{0x000000F431450000L});
    public static final BitSet FOLLOW_47 = new BitSet(new long[]{0x000000F430450000L});
    public static final BitSet FOLLOW_48 = new BitSet(new long[]{0x000000F430410000L});
    public static final BitSet FOLLOW_49 = new BitSet(new long[]{0x000000D430410000L});
    public static final BitSet FOLLOW_50 = new BitSet(new long[]{0x000000D410410000L});
    public static final BitSet FOLLOW_51 = new BitSet(new long[]{0x0000009410410000L});
    public static final BitSet FOLLOW_52 = new BitSet(new long[]{0x0000001400410000L});
    public static final BitSet FOLLOW_53 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_54 = new BitSet(new long[]{0x0000000000402000L});
    public static final BitSet FOLLOW_55 = new BitSet(new long[]{0x00003F0011400000L});
    public static final BitSet FOLLOW_56 = new BitSet(new long[]{0x00003F0010400000L});
    public static final BitSet FOLLOW_57 = new BitSet(new long[]{0x00003E0000400000L});
    public static final BitSet FOLLOW_58 = new BitSet(new long[]{0x00003C0000400000L});
    public static final BitSet FOLLOW_59 = new BitSet(new long[]{0x0000380000400000L});
    public static final BitSet FOLLOW_60 = new BitSet(new long[]{0x0000300000400000L});
    public static final BitSet FOLLOW_61 = new BitSet(new long[]{0x0000200000400000L});
    public static final BitSet FOLLOW_62 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_63 = new BitSet(new long[]{0x0000400001000000L});
    public static final BitSet FOLLOW_64 = new BitSet(new long[]{0x0800800000000000L,0x0000000000000404L});
    public static final BitSet FOLLOW_65 = new BitSet(new long[]{0x0800800000400000L,0x0000000000000404L});
    public static final BitSet FOLLOW_66 = new BitSet(new long[]{0x00FF000000400000L});
    public static final BitSet FOLLOW_67 = new BitSet(new long[]{0x00FE000000400000L});
    public static final BitSet FOLLOW_68 = new BitSet(new long[]{0x00FC000000400000L});
    public static final BitSet FOLLOW_69 = new BitSet(new long[]{0x00F8000000400000L});
    public static final BitSet FOLLOW_70 = new BitSet(new long[]{0x00F0000000400000L});
    public static final BitSet FOLLOW_71 = new BitSet(new long[]{0x00E0000000400000L});
    public static final BitSet FOLLOW_72 = new BitSet(new long[]{0x0000000000000010L,0xFFFFFF0000000000L,0x000000000000003FL});
    public static final BitSet FOLLOW_73 = new BitSet(new long[]{0x0080000000400000L});
    public static final BitSet FOLLOW_74 = new BitSet(new long[]{0x0400000000000002L});
    public static final BitSet FOLLOW_75 = new BitSet(new long[]{0x1002000000000000L});
    public static final BitSet FOLLOW_76 = new BitSet(new long[]{0x1000000000000000L});
    public static final BitSet FOLLOW_77 = new BitSet(new long[]{0x0000000000000010L,0x0000000000000000L,0x00000000000001C0L});
    public static final BitSet FOLLOW_78 = new BitSet(new long[]{0x2000000000000000L});
    public static final BitSet FOLLOW_79 = new BitSet(new long[]{0xC000000000410000L,0x000000000001C203L});
    public static final BitSet FOLLOW_80 = new BitSet(new long[]{0xC000000000410000L,0x0000000000018203L});
    public static final BitSet FOLLOW_81 = new BitSet(new long[]{0xC000000000400000L,0x0000000000018203L});
    public static final BitSet FOLLOW_82 = new BitSet(new long[]{0xC000000000400000L,0x0000000000018201L});
    public static final BitSet FOLLOW_83 = new BitSet(new long[]{0x8000000000400000L,0x0000000000018001L});
    public static final BitSet FOLLOW_84 = new BitSet(new long[]{0x0000000000400000L,0x0000000000018001L});
    public static final BitSet FOLLOW_85 = new BitSet(new long[]{0x0000000000000010L,0x0000000000000000L,0x0000000001FFFE00L});
    public static final BitSet FOLLOW_86 = new BitSet(new long[]{0x0000000000400000L,0x0000000000018000L});
    public static final BitSet FOLLOW_87 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_88 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_89 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000030L});
    public static final BitSet FOLLOW_90 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_91 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000300000000L});
    public static final BitSet FOLLOW_92 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_93 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000016000000L});
    public static final BitSet FOLLOW_94 = new BitSet(new long[]{0x0020000000000000L,0x0000000000000180L});
    public static final BitSet FOLLOW_95 = new BitSet(new long[]{0x0020000000400000L,0x0000000000000180L});
    public static final BitSet FOLLOW_96 = new BitSet(new long[]{0x0000000000000002L,0x0000000000001000L});
    public static final BitSet FOLLOW_97 = new BitSet(new long[]{0x0002000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_98 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000830L});
    public static final BitSet FOLLOW_99 = new BitSet(new long[]{0x0000000000400000L,0x0000000000000080L});
    public static final BitSet FOLLOW_100 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_101 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_102 = new BitSet(new long[]{0x0000000000000012L,0x0000000000600000L});
    public static final BitSet FOLLOW_103 = new BitSet(new long[]{0x0300000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_104 = new BitSet(new long[]{0x0300000000400000L,0x0000000000020000L});
    public static final BitSet FOLLOW_105 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_106 = new BitSet(new long[]{0x0320000000400000L,0x0000000000020000L});
    public static final BitSet FOLLOW_107 = new BitSet(new long[]{0x0000000000000010L,0x0000000000100000L});
    public static final BitSet FOLLOW_108 = new BitSet(new long[]{0x0020000000400000L});
    public static final BitSet FOLLOW_109 = new BitSet(new long[]{0x0002000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_110 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_111 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000001C00000000L});
    public static final BitSet FOLLOW_112 = new BitSet(new long[]{0x0000000000400000L,0x0000000001000000L});
    public static final BitSet FOLLOW_113 = new BitSet(new long[]{0x0000000008400000L,0x000000000F000040L});
    public static final BitSet FOLLOW_114 = new BitSet(new long[]{0x0000000008400000L,0x000000000E000040L});
    public static final BitSet FOLLOW_115 = new BitSet(new long[]{0x0000000008400000L,0x0000000008000040L});
    public static final BitSet FOLLOW_116 = new BitSet(new long[]{0x0000000008400000L,0x0000000000000040L});
    public static final BitSet FOLLOW_117 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x000000003E000000L});
    public static final BitSet FOLLOW_118 = new BitSet(new long[]{0x0000000000000000L,0x0000000003000030L});
    public static final BitSet FOLLOW_119 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000030L});
    public static final BitSet FOLLOW_120 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_121 = new BitSet(new long[]{0x0020000008400000L,0x0000000000000180L});
    public static final BitSet FOLLOW_122 = new BitSet(new long[]{0x0000000000000000L,0x0000000003000830L});
    public static final BitSet FOLLOW_123 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000830L});
    public static final BitSet FOLLOW_124 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000820L});

}