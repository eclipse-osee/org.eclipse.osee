package org.eclipse.osee.framework.core.dsl.parser.antlr.internal; 

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
import org.eclipse.osee.framework.core.dsl.services.OseeDslGrammarAccess;



import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class InternalOseeDslParser extends AbstractInternalAntlrParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE_STRING", "RULE_ID", "RULE_WHOLE_NUM_STR", "RULE_INT", "RULE_ML_COMMENT", "RULE_SL_COMMENT", "RULE_WS", "RULE_ANY_OTHER", "'import'", "'.'", "'abstract'", "'artifactType'", "'extends'", "','", "'{'", "'guid'", "'id'", "'}'", "'attribute'", "'branchUuid'", "'attributeType'", "'overrides'", "'dataProvider'", "'DefaultAttributeDataProvider'", "'UriAttributeDataProvider'", "'min'", "'max'", "'unlimited'", "'taggerId'", "'DefaultAttributeTaggerProvider'", "'enumType'", "'description'", "'defaultValue'", "'fileExtension'", "'mediaType'", "'BooleanAttribute'", "'CompressedContentAttribute'", "'DateAttribute'", "'EnumeratedAttribute'", "'FloatingPointAttribute'", "'IntegerAttribute'", "'LongAttribute'", "'JavaObjectAttribute'", "'StringAttribute'", "'ArtifactReferenceAttribute'", "'BranchReferenceAttribute'", "'WordAttribute'", "'OutlineNumberAttribute'", "'oseeEnumType'", "'entry'", "'entryGuid'", "'overrides enum'", "'inheritAll'", "'add'", "'remove'", "'overrides artifactType'", "'update'", "'relationType'", "'sideAName'", "'sideAArtifactType'", "'sideBName'", "'sideBArtifactType'", "'defaultOrderType'", "'multiplicity'", "'Lexicographical_Ascending'", "'Lexicographical_Descending'", "'Unordered'", "'('", "')'", "'artifactMatcher'", "'where'", "';'", "'role'", "'accessContext'", "'childrenOf'", "'artifact'", "'edit'", "'of'", "'ONE_TO_ONE'", "'ONE_TO_MANY'", "'MANY_TO_ONE'", "'MANY_TO_MANY'", "'EQ'", "'LIKE'", "'AND'", "'OR'", "'artifactName'", "'artifactGuid'", "'branchName'", "'ALLOW'", "'DENY'", "'ALL'", "'SIDE_A'", "'SIDE_B'", "'BOTH'"
    };
    public static final int T__50=50;
    public static final int T__59=59;
    public static final int T__55=55;
    public static final int T__56=56;
    public static final int T__57=57;
    public static final int T__58=58;
    public static final int T__51=51;
    public static final int T__52=52;
    public static final int T__53=53;
    public static final int T__54=54;
    public static final int T__60=60;
    public static final int T__61=61;
    public static final int RULE_ID=5;
    public static final int RULE_INT=7;
    public static final int T__66=66;
    public static final int RULE_ML_COMMENT=8;
    public static final int T__67=67;
    public static final int T__68=68;
    public static final int T__69=69;
    public static final int T__62=62;
    public static final int T__63=63;
    public static final int T__64=64;
    public static final int T__65=65;
    public static final int T__37=37;
    public static final int T__38=38;
    public static final int T__39=39;
    public static final int RULE_WHOLE_NUM_STR=6;
    public static final int T__33=33;
    public static final int T__34=34;
    public static final int T__35=35;
    public static final int T__36=36;
    public static final int T__30=30;
    public static final int T__31=31;
    public static final int T__32=32;
    public static final int T__48=48;
    public static final int T__49=49;
    public static final int T__44=44;
    public static final int T__45=45;
    public static final int T__46=46;
    public static final int T__47=47;
    public static final int T__40=40;
    public static final int T__41=41;
    public static final int T__42=42;
    public static final int T__43=43;
    public static final int T__91=91;
    public static final int T__92=92;
    public static final int T__93=93;
    public static final int T__94=94;
    public static final int T__90=90;
    public static final int T__19=19;
    public static final int T__15=15;
    public static final int T__16=16;
    public static final int T__17=17;
    public static final int T__18=18;
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
    public static final int T__70=70;
    public static final int T__71=71;
    public static final int T__72=72;
    public static final int RULE_STRING=4;
    public static final int RULE_SL_COMMENT=9;
    public static final int T__77=77;
    public static final int T__78=78;
    public static final int T__79=79;
    public static final int T__73=73;
    public static final int EOF=-1;
    public static final int T__74=74;
    public static final int T__75=75;
    public static final int T__76=76;
    public static final int T__80=80;
    public static final int T__81=81;
    public static final int T__82=82;
    public static final int T__83=83;
    public static final int RULE_WS=10;
    public static final int RULE_ANY_OTHER=11;
    public static final int T__88=88;
    public static final int T__89=89;
    public static final int T__84=84;
    public static final int T__85=85;
    public static final int T__86=86;
    public static final int T__87=87;

    // delegates
    // delegators


        public InternalOseeDslParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public InternalOseeDslParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return InternalOseeDslParser.tokenNames; }
    public String getGrammarFileName() { return "InternalOseeDsl.g"; }



     	private OseeDslGrammarAccess grammarAccess;
     	
        public InternalOseeDslParser(TokenStream input, OseeDslGrammarAccess grammarAccess) {
            this(input);
            this.grammarAccess = grammarAccess;
            registerRules(grammarAccess.getGrammar());
        }
        
        @Override
        protected String getFirstRuleName() {
        	return "OseeDsl";	
       	}
       	
       	@Override
       	protected OseeDslGrammarAccess getGrammarAccess() {
       		return grammarAccess;
       	}



    // $ANTLR start "entryRuleOseeDsl"
    // InternalOseeDsl.g:68:1: entryRuleOseeDsl returns [EObject current=null] : iv_ruleOseeDsl= ruleOseeDsl EOF ;
    public final EObject entryRuleOseeDsl() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOseeDsl = null;


        try {
            // InternalOseeDsl.g:69:2: (iv_ruleOseeDsl= ruleOseeDsl EOF )
            // InternalOseeDsl.g:70:2: iv_ruleOseeDsl= ruleOseeDsl EOF
            {
             newCompositeNode(grammarAccess.getOseeDslRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleOseeDsl=ruleOseeDsl();

            state._fsp--;

             current =iv_ruleOseeDsl; 
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
    // $ANTLR end "entryRuleOseeDsl"


    // $ANTLR start "ruleOseeDsl"
    // InternalOseeDsl.g:77:1: ruleOseeDsl returns [EObject current=null] : ( ( (lv_imports_0_0= ruleImport ) )* ( ( (lv_artifactTypes_1_0= ruleXArtifactType ) ) | ( (lv_relationTypes_2_0= ruleXRelationType ) ) | ( (lv_attributeTypes_3_0= ruleXAttributeType ) ) | ( (lv_enumTypes_4_0= ruleXOseeEnumType ) ) | ( (lv_enumOverrides_5_0= ruleXOseeEnumOverride ) ) | ( (lv_artifactTypeOverrides_6_0= ruleXOseeArtifactTypeOverride ) ) )* ( ( (lv_artifactMatchRefs_7_0= ruleXArtifactMatcher ) ) | ( (lv_accessDeclarations_8_0= ruleAccessContext ) ) | ( (lv_roleDeclarations_9_0= ruleRole ) ) )* ) ;
    public final EObject ruleOseeDsl() throws RecognitionException {
        EObject current = null;

        EObject lv_imports_0_0 = null;

        EObject lv_artifactTypes_1_0 = null;

        EObject lv_relationTypes_2_0 = null;

        EObject lv_attributeTypes_3_0 = null;

        EObject lv_enumTypes_4_0 = null;

        EObject lv_enumOverrides_5_0 = null;

        EObject lv_artifactTypeOverrides_6_0 = null;

        EObject lv_artifactMatchRefs_7_0 = null;

        EObject lv_accessDeclarations_8_0 = null;

        EObject lv_roleDeclarations_9_0 = null;


         enterRule(); 
            
        try {
            // InternalOseeDsl.g:80:28: ( ( ( (lv_imports_0_0= ruleImport ) )* ( ( (lv_artifactTypes_1_0= ruleXArtifactType ) ) | ( (lv_relationTypes_2_0= ruleXRelationType ) ) | ( (lv_attributeTypes_3_0= ruleXAttributeType ) ) | ( (lv_enumTypes_4_0= ruleXOseeEnumType ) ) | ( (lv_enumOverrides_5_0= ruleXOseeEnumOverride ) ) | ( (lv_artifactTypeOverrides_6_0= ruleXOseeArtifactTypeOverride ) ) )* ( ( (lv_artifactMatchRefs_7_0= ruleXArtifactMatcher ) ) | ( (lv_accessDeclarations_8_0= ruleAccessContext ) ) | ( (lv_roleDeclarations_9_0= ruleRole ) ) )* ) )
            // InternalOseeDsl.g:81:1: ( ( (lv_imports_0_0= ruleImport ) )* ( ( (lv_artifactTypes_1_0= ruleXArtifactType ) ) | ( (lv_relationTypes_2_0= ruleXRelationType ) ) | ( (lv_attributeTypes_3_0= ruleXAttributeType ) ) | ( (lv_enumTypes_4_0= ruleXOseeEnumType ) ) | ( (lv_enumOverrides_5_0= ruleXOseeEnumOverride ) ) | ( (lv_artifactTypeOverrides_6_0= ruleXOseeArtifactTypeOverride ) ) )* ( ( (lv_artifactMatchRefs_7_0= ruleXArtifactMatcher ) ) | ( (lv_accessDeclarations_8_0= ruleAccessContext ) ) | ( (lv_roleDeclarations_9_0= ruleRole ) ) )* )
            {
            // InternalOseeDsl.g:81:1: ( ( (lv_imports_0_0= ruleImport ) )* ( ( (lv_artifactTypes_1_0= ruleXArtifactType ) ) | ( (lv_relationTypes_2_0= ruleXRelationType ) ) | ( (lv_attributeTypes_3_0= ruleXAttributeType ) ) | ( (lv_enumTypes_4_0= ruleXOseeEnumType ) ) | ( (lv_enumOverrides_5_0= ruleXOseeEnumOverride ) ) | ( (lv_artifactTypeOverrides_6_0= ruleXOseeArtifactTypeOverride ) ) )* ( ( (lv_artifactMatchRefs_7_0= ruleXArtifactMatcher ) ) | ( (lv_accessDeclarations_8_0= ruleAccessContext ) ) | ( (lv_roleDeclarations_9_0= ruleRole ) ) )* )
            // InternalOseeDsl.g:81:2: ( (lv_imports_0_0= ruleImport ) )* ( ( (lv_artifactTypes_1_0= ruleXArtifactType ) ) | ( (lv_relationTypes_2_0= ruleXRelationType ) ) | ( (lv_attributeTypes_3_0= ruleXAttributeType ) ) | ( (lv_enumTypes_4_0= ruleXOseeEnumType ) ) | ( (lv_enumOverrides_5_0= ruleXOseeEnumOverride ) ) | ( (lv_artifactTypeOverrides_6_0= ruleXOseeArtifactTypeOverride ) ) )* ( ( (lv_artifactMatchRefs_7_0= ruleXArtifactMatcher ) ) | ( (lv_accessDeclarations_8_0= ruleAccessContext ) ) | ( (lv_roleDeclarations_9_0= ruleRole ) ) )*
            {
            // InternalOseeDsl.g:81:2: ( (lv_imports_0_0= ruleImport ) )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==12) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // InternalOseeDsl.g:82:1: (lv_imports_0_0= ruleImport )
            	    {
            	    // InternalOseeDsl.g:82:1: (lv_imports_0_0= ruleImport )
            	    // InternalOseeDsl.g:83:3: lv_imports_0_0= ruleImport
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getOseeDslAccess().getImportsImportParserRuleCall_0_0()); 
            	    	    
            	    pushFollow(FOLLOW_3);
            	    lv_imports_0_0=ruleImport();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getOseeDslRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"imports",
            	            		lv_imports_0_0, 
            	            		"org.eclipse.osee.framework.core.dsl.OseeDsl.Import");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);

            // InternalOseeDsl.g:99:3: ( ( (lv_artifactTypes_1_0= ruleXArtifactType ) ) | ( (lv_relationTypes_2_0= ruleXRelationType ) ) | ( (lv_attributeTypes_3_0= ruleXAttributeType ) ) | ( (lv_enumTypes_4_0= ruleXOseeEnumType ) ) | ( (lv_enumOverrides_5_0= ruleXOseeEnumOverride ) ) | ( (lv_artifactTypeOverrides_6_0= ruleXOseeArtifactTypeOverride ) ) )*
            loop2:
            do {
                int alt2=7;
                switch ( input.LA(1) ) {
                case 14:
                case 15:
                    {
                    alt2=1;
                    }
                    break;
                case 61:
                    {
                    alt2=2;
                    }
                    break;
                case 24:
                    {
                    alt2=3;
                    }
                    break;
                case 52:
                    {
                    alt2=4;
                    }
                    break;
                case 55:
                    {
                    alt2=5;
                    }
                    break;
                case 59:
                    {
                    alt2=6;
                    }
                    break;

                }

                switch (alt2) {
            	case 1 :
            	    // InternalOseeDsl.g:99:4: ( (lv_artifactTypes_1_0= ruleXArtifactType ) )
            	    {
            	    // InternalOseeDsl.g:99:4: ( (lv_artifactTypes_1_0= ruleXArtifactType ) )
            	    // InternalOseeDsl.g:100:1: (lv_artifactTypes_1_0= ruleXArtifactType )
            	    {
            	    // InternalOseeDsl.g:100:1: (lv_artifactTypes_1_0= ruleXArtifactType )
            	    // InternalOseeDsl.g:101:3: lv_artifactTypes_1_0= ruleXArtifactType
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getOseeDslAccess().getArtifactTypesXArtifactTypeParserRuleCall_1_0_0()); 
            	    	    
            	    pushFollow(FOLLOW_4);
            	    lv_artifactTypes_1_0=ruleXArtifactType();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getOseeDslRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"artifactTypes",
            	            		lv_artifactTypes_1_0, 
            	            		"org.eclipse.osee.framework.core.dsl.OseeDsl.XArtifactType");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;
            	case 2 :
            	    // InternalOseeDsl.g:118:6: ( (lv_relationTypes_2_0= ruleXRelationType ) )
            	    {
            	    // InternalOseeDsl.g:118:6: ( (lv_relationTypes_2_0= ruleXRelationType ) )
            	    // InternalOseeDsl.g:119:1: (lv_relationTypes_2_0= ruleXRelationType )
            	    {
            	    // InternalOseeDsl.g:119:1: (lv_relationTypes_2_0= ruleXRelationType )
            	    // InternalOseeDsl.g:120:3: lv_relationTypes_2_0= ruleXRelationType
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getOseeDslAccess().getRelationTypesXRelationTypeParserRuleCall_1_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_4);
            	    lv_relationTypes_2_0=ruleXRelationType();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getOseeDslRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"relationTypes",
            	            		lv_relationTypes_2_0, 
            	            		"org.eclipse.osee.framework.core.dsl.OseeDsl.XRelationType");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;
            	case 3 :
            	    // InternalOseeDsl.g:137:6: ( (lv_attributeTypes_3_0= ruleXAttributeType ) )
            	    {
            	    // InternalOseeDsl.g:137:6: ( (lv_attributeTypes_3_0= ruleXAttributeType ) )
            	    // InternalOseeDsl.g:138:1: (lv_attributeTypes_3_0= ruleXAttributeType )
            	    {
            	    // InternalOseeDsl.g:138:1: (lv_attributeTypes_3_0= ruleXAttributeType )
            	    // InternalOseeDsl.g:139:3: lv_attributeTypes_3_0= ruleXAttributeType
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getOseeDslAccess().getAttributeTypesXAttributeTypeParserRuleCall_1_2_0()); 
            	    	    
            	    pushFollow(FOLLOW_4);
            	    lv_attributeTypes_3_0=ruleXAttributeType();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getOseeDslRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"attributeTypes",
            	            		lv_attributeTypes_3_0, 
            	            		"org.eclipse.osee.framework.core.dsl.OseeDsl.XAttributeType");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;
            	case 4 :
            	    // InternalOseeDsl.g:156:6: ( (lv_enumTypes_4_0= ruleXOseeEnumType ) )
            	    {
            	    // InternalOseeDsl.g:156:6: ( (lv_enumTypes_4_0= ruleXOseeEnumType ) )
            	    // InternalOseeDsl.g:157:1: (lv_enumTypes_4_0= ruleXOseeEnumType )
            	    {
            	    // InternalOseeDsl.g:157:1: (lv_enumTypes_4_0= ruleXOseeEnumType )
            	    // InternalOseeDsl.g:158:3: lv_enumTypes_4_0= ruleXOseeEnumType
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getOseeDslAccess().getEnumTypesXOseeEnumTypeParserRuleCall_1_3_0()); 
            	    	    
            	    pushFollow(FOLLOW_4);
            	    lv_enumTypes_4_0=ruleXOseeEnumType();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getOseeDslRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"enumTypes",
            	            		lv_enumTypes_4_0, 
            	            		"org.eclipse.osee.framework.core.dsl.OseeDsl.XOseeEnumType");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;
            	case 5 :
            	    // InternalOseeDsl.g:175:6: ( (lv_enumOverrides_5_0= ruleXOseeEnumOverride ) )
            	    {
            	    // InternalOseeDsl.g:175:6: ( (lv_enumOverrides_5_0= ruleXOseeEnumOverride ) )
            	    // InternalOseeDsl.g:176:1: (lv_enumOverrides_5_0= ruleXOseeEnumOverride )
            	    {
            	    // InternalOseeDsl.g:176:1: (lv_enumOverrides_5_0= ruleXOseeEnumOverride )
            	    // InternalOseeDsl.g:177:3: lv_enumOverrides_5_0= ruleXOseeEnumOverride
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getOseeDslAccess().getEnumOverridesXOseeEnumOverrideParserRuleCall_1_4_0()); 
            	    	    
            	    pushFollow(FOLLOW_4);
            	    lv_enumOverrides_5_0=ruleXOseeEnumOverride();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getOseeDslRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"enumOverrides",
            	            		lv_enumOverrides_5_0, 
            	            		"org.eclipse.osee.framework.core.dsl.OseeDsl.XOseeEnumOverride");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;
            	case 6 :
            	    // InternalOseeDsl.g:194:6: ( (lv_artifactTypeOverrides_6_0= ruleXOseeArtifactTypeOverride ) )
            	    {
            	    // InternalOseeDsl.g:194:6: ( (lv_artifactTypeOverrides_6_0= ruleXOseeArtifactTypeOverride ) )
            	    // InternalOseeDsl.g:195:1: (lv_artifactTypeOverrides_6_0= ruleXOseeArtifactTypeOverride )
            	    {
            	    // InternalOseeDsl.g:195:1: (lv_artifactTypeOverrides_6_0= ruleXOseeArtifactTypeOverride )
            	    // InternalOseeDsl.g:196:3: lv_artifactTypeOverrides_6_0= ruleXOseeArtifactTypeOverride
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getOseeDslAccess().getArtifactTypeOverridesXOseeArtifactTypeOverrideParserRuleCall_1_5_0()); 
            	    	    
            	    pushFollow(FOLLOW_4);
            	    lv_artifactTypeOverrides_6_0=ruleXOseeArtifactTypeOverride();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getOseeDslRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"artifactTypeOverrides",
            	            		lv_artifactTypeOverrides_6_0, 
            	            		"org.eclipse.osee.framework.core.dsl.OseeDsl.XOseeArtifactTypeOverride");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);

            // InternalOseeDsl.g:212:4: ( ( (lv_artifactMatchRefs_7_0= ruleXArtifactMatcher ) ) | ( (lv_accessDeclarations_8_0= ruleAccessContext ) ) | ( (lv_roleDeclarations_9_0= ruleRole ) ) )*
            loop3:
            do {
                int alt3=4;
                switch ( input.LA(1) ) {
                case 73:
                    {
                    alt3=1;
                    }
                    break;
                case 77:
                    {
                    alt3=2;
                    }
                    break;
                case 76:
                    {
                    alt3=3;
                    }
                    break;

                }

                switch (alt3) {
            	case 1 :
            	    // InternalOseeDsl.g:212:5: ( (lv_artifactMatchRefs_7_0= ruleXArtifactMatcher ) )
            	    {
            	    // InternalOseeDsl.g:212:5: ( (lv_artifactMatchRefs_7_0= ruleXArtifactMatcher ) )
            	    // InternalOseeDsl.g:213:1: (lv_artifactMatchRefs_7_0= ruleXArtifactMatcher )
            	    {
            	    // InternalOseeDsl.g:213:1: (lv_artifactMatchRefs_7_0= ruleXArtifactMatcher )
            	    // InternalOseeDsl.g:214:3: lv_artifactMatchRefs_7_0= ruleXArtifactMatcher
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getOseeDslAccess().getArtifactMatchRefsXArtifactMatcherParserRuleCall_2_0_0()); 
            	    	    
            	    pushFollow(FOLLOW_5);
            	    lv_artifactMatchRefs_7_0=ruleXArtifactMatcher();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getOseeDslRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"artifactMatchRefs",
            	            		lv_artifactMatchRefs_7_0, 
            	            		"org.eclipse.osee.framework.core.dsl.OseeDsl.XArtifactMatcher");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;
            	case 2 :
            	    // InternalOseeDsl.g:231:6: ( (lv_accessDeclarations_8_0= ruleAccessContext ) )
            	    {
            	    // InternalOseeDsl.g:231:6: ( (lv_accessDeclarations_8_0= ruleAccessContext ) )
            	    // InternalOseeDsl.g:232:1: (lv_accessDeclarations_8_0= ruleAccessContext )
            	    {
            	    // InternalOseeDsl.g:232:1: (lv_accessDeclarations_8_0= ruleAccessContext )
            	    // InternalOseeDsl.g:233:3: lv_accessDeclarations_8_0= ruleAccessContext
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getOseeDslAccess().getAccessDeclarationsAccessContextParserRuleCall_2_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_5);
            	    lv_accessDeclarations_8_0=ruleAccessContext();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getOseeDslRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"accessDeclarations",
            	            		lv_accessDeclarations_8_0, 
            	            		"org.eclipse.osee.framework.core.dsl.OseeDsl.AccessContext");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;
            	case 3 :
            	    // InternalOseeDsl.g:250:6: ( (lv_roleDeclarations_9_0= ruleRole ) )
            	    {
            	    // InternalOseeDsl.g:250:6: ( (lv_roleDeclarations_9_0= ruleRole ) )
            	    // InternalOseeDsl.g:251:1: (lv_roleDeclarations_9_0= ruleRole )
            	    {
            	    // InternalOseeDsl.g:251:1: (lv_roleDeclarations_9_0= ruleRole )
            	    // InternalOseeDsl.g:252:3: lv_roleDeclarations_9_0= ruleRole
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getOseeDslAccess().getRoleDeclarationsRoleParserRuleCall_2_2_0()); 
            	    	    
            	    pushFollow(FOLLOW_5);
            	    lv_roleDeclarations_9_0=ruleRole();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getOseeDslRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"roleDeclarations",
            	            		lv_roleDeclarations_9_0, 
            	            		"org.eclipse.osee.framework.core.dsl.OseeDsl.Role");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop3;
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
    // $ANTLR end "ruleOseeDsl"


    // $ANTLR start "entryRuleImport"
    // InternalOseeDsl.g:276:1: entryRuleImport returns [EObject current=null] : iv_ruleImport= ruleImport EOF ;
    public final EObject entryRuleImport() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleImport = null;


        try {
            // InternalOseeDsl.g:277:2: (iv_ruleImport= ruleImport EOF )
            // InternalOseeDsl.g:278:2: iv_ruleImport= ruleImport EOF
            {
             newCompositeNode(grammarAccess.getImportRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleImport=ruleImport();

            state._fsp--;

             current =iv_ruleImport; 
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
    // $ANTLR end "entryRuleImport"


    // $ANTLR start "ruleImport"
    // InternalOseeDsl.g:285:1: ruleImport returns [EObject current=null] : (otherlv_0= 'import' ( (lv_importURI_1_0= RULE_STRING ) ) ) ;
    public final EObject ruleImport() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_importURI_1_0=null;

         enterRule(); 
            
        try {
            // InternalOseeDsl.g:288:28: ( (otherlv_0= 'import' ( (lv_importURI_1_0= RULE_STRING ) ) ) )
            // InternalOseeDsl.g:289:1: (otherlv_0= 'import' ( (lv_importURI_1_0= RULE_STRING ) ) )
            {
            // InternalOseeDsl.g:289:1: (otherlv_0= 'import' ( (lv_importURI_1_0= RULE_STRING ) ) )
            // InternalOseeDsl.g:289:3: otherlv_0= 'import' ( (lv_importURI_1_0= RULE_STRING ) )
            {
            otherlv_0=(Token)match(input,12,FOLLOW_6); 

                	newLeafNode(otherlv_0, grammarAccess.getImportAccess().getImportKeyword_0());
                
            // InternalOseeDsl.g:293:1: ( (lv_importURI_1_0= RULE_STRING ) )
            // InternalOseeDsl.g:294:1: (lv_importURI_1_0= RULE_STRING )
            {
            // InternalOseeDsl.g:294:1: (lv_importURI_1_0= RULE_STRING )
            // InternalOseeDsl.g:295:3: lv_importURI_1_0= RULE_STRING
            {
            lv_importURI_1_0=(Token)match(input,RULE_STRING,FOLLOW_2); 

            			newLeafNode(lv_importURI_1_0, grammarAccess.getImportAccess().getImportURISTRINGTerminalRuleCall_1_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getImportRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"importURI",
                    		lv_importURI_1_0, 
                    		"org.eclipse.osee.framework.core.dsl.OseeDsl.STRING");
            	    

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
    // $ANTLR end "ruleImport"


    // $ANTLR start "entryRuleQUALIFIED_NAME"
    // InternalOseeDsl.g:319:1: entryRuleQUALIFIED_NAME returns [String current=null] : iv_ruleQUALIFIED_NAME= ruleQUALIFIED_NAME EOF ;
    public final String entryRuleQUALIFIED_NAME() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleQUALIFIED_NAME = null;


        try {
            // InternalOseeDsl.g:320:2: (iv_ruleQUALIFIED_NAME= ruleQUALIFIED_NAME EOF )
            // InternalOseeDsl.g:321:2: iv_ruleQUALIFIED_NAME= ruleQUALIFIED_NAME EOF
            {
             newCompositeNode(grammarAccess.getQUALIFIED_NAMERule()); 
            pushFollow(FOLLOW_1);
            iv_ruleQUALIFIED_NAME=ruleQUALIFIED_NAME();

            state._fsp--;

             current =iv_ruleQUALIFIED_NAME.getText(); 
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
    // $ANTLR end "entryRuleQUALIFIED_NAME"


    // $ANTLR start "ruleQUALIFIED_NAME"
    // InternalOseeDsl.g:328:1: ruleQUALIFIED_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* ) ;
    public final AntlrDatatypeRuleToken ruleQUALIFIED_NAME() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_ID_0=null;
        Token kw=null;
        Token this_ID_2=null;

         enterRule(); 
            
        try {
            // InternalOseeDsl.g:331:28: ( (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* ) )
            // InternalOseeDsl.g:332:1: (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* )
            {
            // InternalOseeDsl.g:332:1: (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* )
            // InternalOseeDsl.g:332:6: this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )*
            {
            this_ID_0=(Token)match(input,RULE_ID,FOLLOW_7); 

            		current.merge(this_ID_0);
                
             
                newLeafNode(this_ID_0, grammarAccess.getQUALIFIED_NAMEAccess().getIDTerminalRuleCall_0()); 
                
            // InternalOseeDsl.g:339:1: (kw= '.' this_ID_2= RULE_ID )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==13) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // InternalOseeDsl.g:340:2: kw= '.' this_ID_2= RULE_ID
            	    {
            	    kw=(Token)match(input,13,FOLLOW_8); 

            	            current.merge(kw);
            	            newLeafNode(kw, grammarAccess.getQUALIFIED_NAMEAccess().getFullStopKeyword_1_0()); 
            	        
            	    this_ID_2=(Token)match(input,RULE_ID,FOLLOW_7); 

            	    		current.merge(this_ID_2);
            	        
            	     
            	        newLeafNode(this_ID_2, grammarAccess.getQUALIFIED_NAMEAccess().getIDTerminalRuleCall_1_1()); 
            	        

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
    // $ANTLR end "ruleQUALIFIED_NAME"


    // $ANTLR start "entryRuleOseeType"
    // InternalOseeDsl.g:362:1: entryRuleOseeType returns [EObject current=null] : iv_ruleOseeType= ruleOseeType EOF ;
    public final EObject entryRuleOseeType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOseeType = null;


        try {
            // InternalOseeDsl.g:363:2: (iv_ruleOseeType= ruleOseeType EOF )
            // InternalOseeDsl.g:364:2: iv_ruleOseeType= ruleOseeType EOF
            {
             newCompositeNode(grammarAccess.getOseeTypeRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleOseeType=ruleOseeType();

            state._fsp--;

             current =iv_ruleOseeType; 
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
    // $ANTLR end "entryRuleOseeType"


    // $ANTLR start "ruleOseeType"
    // InternalOseeDsl.g:371:1: ruleOseeType returns [EObject current=null] : (this_XArtifactType_0= ruleXArtifactType | this_XRelationType_1= ruleXRelationType | this_XAttributeType_2= ruleXAttributeType | this_XOseeEnumType_3= ruleXOseeEnumType ) ;
    public final EObject ruleOseeType() throws RecognitionException {
        EObject current = null;

        EObject this_XArtifactType_0 = null;

        EObject this_XRelationType_1 = null;

        EObject this_XAttributeType_2 = null;

        EObject this_XOseeEnumType_3 = null;


         enterRule(); 
            
        try {
            // InternalOseeDsl.g:374:28: ( (this_XArtifactType_0= ruleXArtifactType | this_XRelationType_1= ruleXRelationType | this_XAttributeType_2= ruleXAttributeType | this_XOseeEnumType_3= ruleXOseeEnumType ) )
            // InternalOseeDsl.g:375:1: (this_XArtifactType_0= ruleXArtifactType | this_XRelationType_1= ruleXRelationType | this_XAttributeType_2= ruleXAttributeType | this_XOseeEnumType_3= ruleXOseeEnumType )
            {
            // InternalOseeDsl.g:375:1: (this_XArtifactType_0= ruleXArtifactType | this_XRelationType_1= ruleXRelationType | this_XAttributeType_2= ruleXAttributeType | this_XOseeEnumType_3= ruleXOseeEnumType )
            int alt5=4;
            switch ( input.LA(1) ) {
            case 14:
            case 15:
                {
                alt5=1;
                }
                break;
            case 61:
                {
                alt5=2;
                }
                break;
            case 24:
                {
                alt5=3;
                }
                break;
            case 52:
                {
                alt5=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;
            }

            switch (alt5) {
                case 1 :
                    // InternalOseeDsl.g:376:5: this_XArtifactType_0= ruleXArtifactType
                    {
                     
                            newCompositeNode(grammarAccess.getOseeTypeAccess().getXArtifactTypeParserRuleCall_0()); 
                        
                    pushFollow(FOLLOW_2);
                    this_XArtifactType_0=ruleXArtifactType();

                    state._fsp--;

                     
                            current = this_XArtifactType_0; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // InternalOseeDsl.g:386:5: this_XRelationType_1= ruleXRelationType
                    {
                     
                            newCompositeNode(grammarAccess.getOseeTypeAccess().getXRelationTypeParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_2);
                    this_XRelationType_1=ruleXRelationType();

                    state._fsp--;

                     
                            current = this_XRelationType_1; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 3 :
                    // InternalOseeDsl.g:396:5: this_XAttributeType_2= ruleXAttributeType
                    {
                     
                            newCompositeNode(grammarAccess.getOseeTypeAccess().getXAttributeTypeParserRuleCall_2()); 
                        
                    pushFollow(FOLLOW_2);
                    this_XAttributeType_2=ruleXAttributeType();

                    state._fsp--;

                     
                            current = this_XAttributeType_2; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 4 :
                    // InternalOseeDsl.g:406:5: this_XOseeEnumType_3= ruleXOseeEnumType
                    {
                     
                            newCompositeNode(grammarAccess.getOseeTypeAccess().getXOseeEnumTypeParserRuleCall_3()); 
                        
                    pushFollow(FOLLOW_2);
                    this_XOseeEnumType_3=ruleXOseeEnumType();

                    state._fsp--;

                     
                            current = this_XOseeEnumType_3; 
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
    // $ANTLR end "ruleOseeType"


    // $ANTLR start "entryRuleXArtifactType"
    // InternalOseeDsl.g:422:1: entryRuleXArtifactType returns [EObject current=null] : iv_ruleXArtifactType= ruleXArtifactType EOF ;
    public final EObject entryRuleXArtifactType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleXArtifactType = null;


        try {
            // InternalOseeDsl.g:423:2: (iv_ruleXArtifactType= ruleXArtifactType EOF )
            // InternalOseeDsl.g:424:2: iv_ruleXArtifactType= ruleXArtifactType EOF
            {
             newCompositeNode(grammarAccess.getXArtifactTypeRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleXArtifactType=ruleXArtifactType();

            state._fsp--;

             current =iv_ruleXArtifactType; 
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
    // $ANTLR end "entryRuleXArtifactType"


    // $ANTLR start "ruleXArtifactType"
    // InternalOseeDsl.g:431:1: ruleXArtifactType returns [EObject current=null] : ( ( (lv_abstract_0_0= 'abstract' ) )? otherlv_1= 'artifactType' ( (lv_name_2_0= RULE_STRING ) ) (otherlv_3= 'extends' ( (otherlv_4= RULE_STRING ) ) (otherlv_5= ',' ( (otherlv_6= RULE_STRING ) ) )* )? otherlv_7= '{' (otherlv_8= 'guid' ( (lv_typeGuid_9_0= RULE_STRING ) ) )? otherlv_10= 'id' ( (lv_id_11_0= RULE_WHOLE_NUM_STR ) ) ( (lv_validAttributeTypes_12_0= ruleXAttributeTypeRef ) )* otherlv_13= '}' ) ;
    public final EObject ruleXArtifactType() throws RecognitionException {
        EObject current = null;

        Token lv_abstract_0_0=null;
        Token otherlv_1=null;
        Token lv_name_2_0=null;
        Token otherlv_3=null;
        Token otherlv_4=null;
        Token otherlv_5=null;
        Token otherlv_6=null;
        Token otherlv_7=null;
        Token otherlv_8=null;
        Token lv_typeGuid_9_0=null;
        Token otherlv_10=null;
        Token lv_id_11_0=null;
        Token otherlv_13=null;
        EObject lv_validAttributeTypes_12_0 = null;


         enterRule(); 
            
        try {
            // InternalOseeDsl.g:434:28: ( ( ( (lv_abstract_0_0= 'abstract' ) )? otherlv_1= 'artifactType' ( (lv_name_2_0= RULE_STRING ) ) (otherlv_3= 'extends' ( (otherlv_4= RULE_STRING ) ) (otherlv_5= ',' ( (otherlv_6= RULE_STRING ) ) )* )? otherlv_7= '{' (otherlv_8= 'guid' ( (lv_typeGuid_9_0= RULE_STRING ) ) )? otherlv_10= 'id' ( (lv_id_11_0= RULE_WHOLE_NUM_STR ) ) ( (lv_validAttributeTypes_12_0= ruleXAttributeTypeRef ) )* otherlv_13= '}' ) )
            // InternalOseeDsl.g:435:1: ( ( (lv_abstract_0_0= 'abstract' ) )? otherlv_1= 'artifactType' ( (lv_name_2_0= RULE_STRING ) ) (otherlv_3= 'extends' ( (otherlv_4= RULE_STRING ) ) (otherlv_5= ',' ( (otherlv_6= RULE_STRING ) ) )* )? otherlv_7= '{' (otherlv_8= 'guid' ( (lv_typeGuid_9_0= RULE_STRING ) ) )? otherlv_10= 'id' ( (lv_id_11_0= RULE_WHOLE_NUM_STR ) ) ( (lv_validAttributeTypes_12_0= ruleXAttributeTypeRef ) )* otherlv_13= '}' )
            {
            // InternalOseeDsl.g:435:1: ( ( (lv_abstract_0_0= 'abstract' ) )? otherlv_1= 'artifactType' ( (lv_name_2_0= RULE_STRING ) ) (otherlv_3= 'extends' ( (otherlv_4= RULE_STRING ) ) (otherlv_5= ',' ( (otherlv_6= RULE_STRING ) ) )* )? otherlv_7= '{' (otherlv_8= 'guid' ( (lv_typeGuid_9_0= RULE_STRING ) ) )? otherlv_10= 'id' ( (lv_id_11_0= RULE_WHOLE_NUM_STR ) ) ( (lv_validAttributeTypes_12_0= ruleXAttributeTypeRef ) )* otherlv_13= '}' )
            // InternalOseeDsl.g:435:2: ( (lv_abstract_0_0= 'abstract' ) )? otherlv_1= 'artifactType' ( (lv_name_2_0= RULE_STRING ) ) (otherlv_3= 'extends' ( (otherlv_4= RULE_STRING ) ) (otherlv_5= ',' ( (otherlv_6= RULE_STRING ) ) )* )? otherlv_7= '{' (otherlv_8= 'guid' ( (lv_typeGuid_9_0= RULE_STRING ) ) )? otherlv_10= 'id' ( (lv_id_11_0= RULE_WHOLE_NUM_STR ) ) ( (lv_validAttributeTypes_12_0= ruleXAttributeTypeRef ) )* otherlv_13= '}'
            {
            // InternalOseeDsl.g:435:2: ( (lv_abstract_0_0= 'abstract' ) )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==14) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // InternalOseeDsl.g:436:1: (lv_abstract_0_0= 'abstract' )
                    {
                    // InternalOseeDsl.g:436:1: (lv_abstract_0_0= 'abstract' )
                    // InternalOseeDsl.g:437:3: lv_abstract_0_0= 'abstract'
                    {
                    lv_abstract_0_0=(Token)match(input,14,FOLLOW_9); 

                            newLeafNode(lv_abstract_0_0, grammarAccess.getXArtifactTypeAccess().getAbstractAbstractKeyword_0_0());
                        

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getXArtifactTypeRule());
                    	        }
                           		setWithLastConsumed(current, "abstract", true, "abstract");
                    	    

                    }


                    }
                    break;

            }

            otherlv_1=(Token)match(input,15,FOLLOW_6); 

                	newLeafNode(otherlv_1, grammarAccess.getXArtifactTypeAccess().getArtifactTypeKeyword_1());
                
            // InternalOseeDsl.g:454:1: ( (lv_name_2_0= RULE_STRING ) )
            // InternalOseeDsl.g:455:1: (lv_name_2_0= RULE_STRING )
            {
            // InternalOseeDsl.g:455:1: (lv_name_2_0= RULE_STRING )
            // InternalOseeDsl.g:456:3: lv_name_2_0= RULE_STRING
            {
            lv_name_2_0=(Token)match(input,RULE_STRING,FOLLOW_10); 

            			newLeafNode(lv_name_2_0, grammarAccess.getXArtifactTypeAccess().getNameSTRINGTerminalRuleCall_2_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getXArtifactTypeRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"name",
                    		lv_name_2_0, 
                    		"org.eclipse.osee.framework.core.dsl.OseeDsl.STRING");
            	    

            }


            }

            // InternalOseeDsl.g:472:2: (otherlv_3= 'extends' ( (otherlv_4= RULE_STRING ) ) (otherlv_5= ',' ( (otherlv_6= RULE_STRING ) ) )* )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==16) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // InternalOseeDsl.g:472:4: otherlv_3= 'extends' ( (otherlv_4= RULE_STRING ) ) (otherlv_5= ',' ( (otherlv_6= RULE_STRING ) ) )*
                    {
                    otherlv_3=(Token)match(input,16,FOLLOW_6); 

                        	newLeafNode(otherlv_3, grammarAccess.getXArtifactTypeAccess().getExtendsKeyword_3_0());
                        
                    // InternalOseeDsl.g:476:1: ( (otherlv_4= RULE_STRING ) )
                    // InternalOseeDsl.g:477:1: (otherlv_4= RULE_STRING )
                    {
                    // InternalOseeDsl.g:477:1: (otherlv_4= RULE_STRING )
                    // InternalOseeDsl.g:478:3: otherlv_4= RULE_STRING
                    {

                    			if (current==null) {
                    	            current = createModelElement(grammarAccess.getXArtifactTypeRule());
                    	        }
                            
                    otherlv_4=(Token)match(input,RULE_STRING,FOLLOW_11); 

                    		newLeafNode(otherlv_4, grammarAccess.getXArtifactTypeAccess().getSuperArtifactTypesXArtifactTypeCrossReference_3_1_0()); 
                    	

                    }


                    }

                    // InternalOseeDsl.g:489:2: (otherlv_5= ',' ( (otherlv_6= RULE_STRING ) ) )*
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( (LA7_0==17) ) {
                            alt7=1;
                        }


                        switch (alt7) {
                    	case 1 :
                    	    // InternalOseeDsl.g:489:4: otherlv_5= ',' ( (otherlv_6= RULE_STRING ) )
                    	    {
                    	    otherlv_5=(Token)match(input,17,FOLLOW_6); 

                    	        	newLeafNode(otherlv_5, grammarAccess.getXArtifactTypeAccess().getCommaKeyword_3_2_0());
                    	        
                    	    // InternalOseeDsl.g:493:1: ( (otherlv_6= RULE_STRING ) )
                    	    // InternalOseeDsl.g:494:1: (otherlv_6= RULE_STRING )
                    	    {
                    	    // InternalOseeDsl.g:494:1: (otherlv_6= RULE_STRING )
                    	    // InternalOseeDsl.g:495:3: otherlv_6= RULE_STRING
                    	    {

                    	    			if (current==null) {
                    	    	            current = createModelElement(grammarAccess.getXArtifactTypeRule());
                    	    	        }
                    	            
                    	    otherlv_6=(Token)match(input,RULE_STRING,FOLLOW_11); 

                    	    		newLeafNode(otherlv_6, grammarAccess.getXArtifactTypeAccess().getSuperArtifactTypesXArtifactTypeCrossReference_3_2_1_0()); 
                    	    	

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop7;
                        }
                    } while (true);


                    }
                    break;

            }

            otherlv_7=(Token)match(input,18,FOLLOW_12); 

                	newLeafNode(otherlv_7, grammarAccess.getXArtifactTypeAccess().getLeftCurlyBracketKeyword_4());
                
            // InternalOseeDsl.g:510:1: (otherlv_8= 'guid' ( (lv_typeGuid_9_0= RULE_STRING ) ) )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==19) ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // InternalOseeDsl.g:510:3: otherlv_8= 'guid' ( (lv_typeGuid_9_0= RULE_STRING ) )
                    {
                    otherlv_8=(Token)match(input,19,FOLLOW_6); 

                        	newLeafNode(otherlv_8, grammarAccess.getXArtifactTypeAccess().getGuidKeyword_5_0());
                        
                    // InternalOseeDsl.g:514:1: ( (lv_typeGuid_9_0= RULE_STRING ) )
                    // InternalOseeDsl.g:515:1: (lv_typeGuid_9_0= RULE_STRING )
                    {
                    // InternalOseeDsl.g:515:1: (lv_typeGuid_9_0= RULE_STRING )
                    // InternalOseeDsl.g:516:3: lv_typeGuid_9_0= RULE_STRING
                    {
                    lv_typeGuid_9_0=(Token)match(input,RULE_STRING,FOLLOW_13); 

                    			newLeafNode(lv_typeGuid_9_0, grammarAccess.getXArtifactTypeAccess().getTypeGuidSTRINGTerminalRuleCall_5_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getXArtifactTypeRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"typeGuid",
                            		lv_typeGuid_9_0, 
                            		"org.eclipse.osee.framework.core.dsl.OseeDsl.STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            otherlv_10=(Token)match(input,20,FOLLOW_14); 

                	newLeafNode(otherlv_10, grammarAccess.getXArtifactTypeAccess().getIdKeyword_6());
                
            // InternalOseeDsl.g:536:1: ( (lv_id_11_0= RULE_WHOLE_NUM_STR ) )
            // InternalOseeDsl.g:537:1: (lv_id_11_0= RULE_WHOLE_NUM_STR )
            {
            // InternalOseeDsl.g:537:1: (lv_id_11_0= RULE_WHOLE_NUM_STR )
            // InternalOseeDsl.g:538:3: lv_id_11_0= RULE_WHOLE_NUM_STR
            {
            lv_id_11_0=(Token)match(input,RULE_WHOLE_NUM_STR,FOLLOW_15); 

            			newLeafNode(lv_id_11_0, grammarAccess.getXArtifactTypeAccess().getIdWHOLE_NUM_STRTerminalRuleCall_7_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getXArtifactTypeRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"id",
                    		lv_id_11_0, 
                    		"org.eclipse.osee.framework.core.dsl.OseeDsl.WHOLE_NUM_STR");
            	    

            }


            }

            // InternalOseeDsl.g:554:2: ( (lv_validAttributeTypes_12_0= ruleXAttributeTypeRef ) )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0==22) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // InternalOseeDsl.g:555:1: (lv_validAttributeTypes_12_0= ruleXAttributeTypeRef )
            	    {
            	    // InternalOseeDsl.g:555:1: (lv_validAttributeTypes_12_0= ruleXAttributeTypeRef )
            	    // InternalOseeDsl.g:556:3: lv_validAttributeTypes_12_0= ruleXAttributeTypeRef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getXArtifactTypeAccess().getValidAttributeTypesXAttributeTypeRefParserRuleCall_8_0()); 
            	    	    
            	    pushFollow(FOLLOW_15);
            	    lv_validAttributeTypes_12_0=ruleXAttributeTypeRef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getXArtifactTypeRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"validAttributeTypes",
            	            		lv_validAttributeTypes_12_0, 
            	            		"org.eclipse.osee.framework.core.dsl.OseeDsl.XAttributeTypeRef");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    break loop10;
                }
            } while (true);

            otherlv_13=(Token)match(input,21,FOLLOW_2); 

                	newLeafNode(otherlv_13, grammarAccess.getXArtifactTypeAccess().getRightCurlyBracketKeyword_9());
                

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
    // $ANTLR end "ruleXArtifactType"


    // $ANTLR start "entryRuleXAttributeTypeRef"
    // InternalOseeDsl.g:584:1: entryRuleXAttributeTypeRef returns [EObject current=null] : iv_ruleXAttributeTypeRef= ruleXAttributeTypeRef EOF ;
    public final EObject entryRuleXAttributeTypeRef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleXAttributeTypeRef = null;


        try {
            // InternalOseeDsl.g:585:2: (iv_ruleXAttributeTypeRef= ruleXAttributeTypeRef EOF )
            // InternalOseeDsl.g:586:2: iv_ruleXAttributeTypeRef= ruleXAttributeTypeRef EOF
            {
             newCompositeNode(grammarAccess.getXAttributeTypeRefRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleXAttributeTypeRef=ruleXAttributeTypeRef();

            state._fsp--;

             current =iv_ruleXAttributeTypeRef; 
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
    // $ANTLR end "entryRuleXAttributeTypeRef"


    // $ANTLR start "ruleXAttributeTypeRef"
    // InternalOseeDsl.g:593:1: ruleXAttributeTypeRef returns [EObject current=null] : (otherlv_0= 'attribute' ( (otherlv_1= RULE_STRING ) ) (otherlv_2= 'branchUuid' ( (lv_branchUuid_3_0= RULE_WHOLE_NUM_STR ) ) )? ) ;
    public final EObject ruleXAttributeTypeRef() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;
        Token otherlv_2=null;
        Token lv_branchUuid_3_0=null;

         enterRule(); 
            
        try {
            // InternalOseeDsl.g:596:28: ( (otherlv_0= 'attribute' ( (otherlv_1= RULE_STRING ) ) (otherlv_2= 'branchUuid' ( (lv_branchUuid_3_0= RULE_WHOLE_NUM_STR ) ) )? ) )
            // InternalOseeDsl.g:597:1: (otherlv_0= 'attribute' ( (otherlv_1= RULE_STRING ) ) (otherlv_2= 'branchUuid' ( (lv_branchUuid_3_0= RULE_WHOLE_NUM_STR ) ) )? )
            {
            // InternalOseeDsl.g:597:1: (otherlv_0= 'attribute' ( (otherlv_1= RULE_STRING ) ) (otherlv_2= 'branchUuid' ( (lv_branchUuid_3_0= RULE_WHOLE_NUM_STR ) ) )? )
            // InternalOseeDsl.g:597:3: otherlv_0= 'attribute' ( (otherlv_1= RULE_STRING ) ) (otherlv_2= 'branchUuid' ( (lv_branchUuid_3_0= RULE_WHOLE_NUM_STR ) ) )?
            {
            otherlv_0=(Token)match(input,22,FOLLOW_6); 

                	newLeafNode(otherlv_0, grammarAccess.getXAttributeTypeRefAccess().getAttributeKeyword_0());
                
            // InternalOseeDsl.g:601:1: ( (otherlv_1= RULE_STRING ) )
            // InternalOseeDsl.g:602:1: (otherlv_1= RULE_STRING )
            {
            // InternalOseeDsl.g:602:1: (otherlv_1= RULE_STRING )
            // InternalOseeDsl.g:603:3: otherlv_1= RULE_STRING
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getXAttributeTypeRefRule());
            	        }
                    
            otherlv_1=(Token)match(input,RULE_STRING,FOLLOW_16); 

            		newLeafNode(otherlv_1, grammarAccess.getXAttributeTypeRefAccess().getValidAttributeTypeXAttributeTypeCrossReference_1_0()); 
            	

            }


            }

            // InternalOseeDsl.g:614:2: (otherlv_2= 'branchUuid' ( (lv_branchUuid_3_0= RULE_WHOLE_NUM_STR ) ) )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==23) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // InternalOseeDsl.g:614:4: otherlv_2= 'branchUuid' ( (lv_branchUuid_3_0= RULE_WHOLE_NUM_STR ) )
                    {
                    otherlv_2=(Token)match(input,23,FOLLOW_14); 

                        	newLeafNode(otherlv_2, grammarAccess.getXAttributeTypeRefAccess().getBranchUuidKeyword_2_0());
                        
                    // InternalOseeDsl.g:618:1: ( (lv_branchUuid_3_0= RULE_WHOLE_NUM_STR ) )
                    // InternalOseeDsl.g:619:1: (lv_branchUuid_3_0= RULE_WHOLE_NUM_STR )
                    {
                    // InternalOseeDsl.g:619:1: (lv_branchUuid_3_0= RULE_WHOLE_NUM_STR )
                    // InternalOseeDsl.g:620:3: lv_branchUuid_3_0= RULE_WHOLE_NUM_STR
                    {
                    lv_branchUuid_3_0=(Token)match(input,RULE_WHOLE_NUM_STR,FOLLOW_2); 

                    			newLeafNode(lv_branchUuid_3_0, grammarAccess.getXAttributeTypeRefAccess().getBranchUuidWHOLE_NUM_STRTerminalRuleCall_2_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getXAttributeTypeRefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"branchUuid",
                            		lv_branchUuid_3_0, 
                            		"org.eclipse.osee.framework.core.dsl.OseeDsl.WHOLE_NUM_STR");
                    	    

                    }


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
    // $ANTLR end "ruleXAttributeTypeRef"


    // $ANTLR start "entryRuleXAttributeType"
    // InternalOseeDsl.g:644:1: entryRuleXAttributeType returns [EObject current=null] : iv_ruleXAttributeType= ruleXAttributeType EOF ;
    public final EObject entryRuleXAttributeType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleXAttributeType = null;


        try {
            // InternalOseeDsl.g:645:2: (iv_ruleXAttributeType= ruleXAttributeType EOF )
            // InternalOseeDsl.g:646:2: iv_ruleXAttributeType= ruleXAttributeType EOF
            {
             newCompositeNode(grammarAccess.getXAttributeTypeRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleXAttributeType=ruleXAttributeType();

            state._fsp--;

             current =iv_ruleXAttributeType; 
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
    // $ANTLR end "entryRuleXAttributeType"


    // $ANTLR start "ruleXAttributeType"
    // InternalOseeDsl.g:653:1: ruleXAttributeType returns [EObject current=null] : (otherlv_0= 'attributeType' ( (lv_name_1_0= RULE_STRING ) ) (otherlv_2= 'extends' ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) ) ) (otherlv_4= 'overrides' ( (otherlv_5= RULE_STRING ) ) )? otherlv_6= '{' (otherlv_7= 'guid' ( (lv_typeGuid_8_0= RULE_STRING ) ) )? otherlv_9= 'id' ( (lv_id_10_0= RULE_WHOLE_NUM_STR ) ) otherlv_11= 'dataProvider' ( ( (lv_dataProvider_12_1= 'DefaultAttributeDataProvider' | lv_dataProvider_12_2= 'UriAttributeDataProvider' | lv_dataProvider_12_3= ruleQUALIFIED_NAME ) ) ) otherlv_13= 'min' ( (lv_min_14_0= RULE_WHOLE_NUM_STR ) ) otherlv_15= 'max' ( ( (lv_max_16_1= RULE_WHOLE_NUM_STR | lv_max_16_2= 'unlimited' ) ) ) ( ( ( ( ({...}? => ( ({...}? => (otherlv_18= 'taggerId' ( ( (lv_taggerId_19_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_19_2= ruleQUALIFIED_NAME ) ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_20= 'enumType' ( (otherlv_21= RULE_STRING ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_22= 'description' ( (lv_description_23_0= RULE_STRING ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_24= 'defaultValue' ( (lv_defaultValue_25_0= RULE_STRING ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_26= 'fileExtension' ( (lv_fileExtension_27_0= RULE_STRING ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_28= 'mediaType' ( (lv_mediaType_29_0= RULE_STRING ) ) ) ) ) ) )* ) ) ) otherlv_30= '}' ) ;
    public final EObject ruleXAttributeType() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_name_1_0=null;
        Token otherlv_2=null;
        Token otherlv_4=null;
        Token otherlv_5=null;
        Token otherlv_6=null;
        Token otherlv_7=null;
        Token lv_typeGuid_8_0=null;
        Token otherlv_9=null;
        Token lv_id_10_0=null;
        Token otherlv_11=null;
        Token lv_dataProvider_12_1=null;
        Token lv_dataProvider_12_2=null;
        Token otherlv_13=null;
        Token lv_min_14_0=null;
        Token otherlv_15=null;
        Token lv_max_16_1=null;
        Token lv_max_16_2=null;
        Token otherlv_18=null;
        Token lv_taggerId_19_1=null;
        Token otherlv_20=null;
        Token otherlv_21=null;
        Token otherlv_22=null;
        Token lv_description_23_0=null;
        Token otherlv_24=null;
        Token lv_defaultValue_25_0=null;
        Token otherlv_26=null;
        Token lv_fileExtension_27_0=null;
        Token otherlv_28=null;
        Token lv_mediaType_29_0=null;
        Token otherlv_30=null;
        AntlrDatatypeRuleToken lv_baseAttributeType_3_0 = null;

        AntlrDatatypeRuleToken lv_dataProvider_12_3 = null;

        AntlrDatatypeRuleToken lv_taggerId_19_2 = null;


         enterRule(); 
            
        try {
            // InternalOseeDsl.g:656:28: ( (otherlv_0= 'attributeType' ( (lv_name_1_0= RULE_STRING ) ) (otherlv_2= 'extends' ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) ) ) (otherlv_4= 'overrides' ( (otherlv_5= RULE_STRING ) ) )? otherlv_6= '{' (otherlv_7= 'guid' ( (lv_typeGuid_8_0= RULE_STRING ) ) )? otherlv_9= 'id' ( (lv_id_10_0= RULE_WHOLE_NUM_STR ) ) otherlv_11= 'dataProvider' ( ( (lv_dataProvider_12_1= 'DefaultAttributeDataProvider' | lv_dataProvider_12_2= 'UriAttributeDataProvider' | lv_dataProvider_12_3= ruleQUALIFIED_NAME ) ) ) otherlv_13= 'min' ( (lv_min_14_0= RULE_WHOLE_NUM_STR ) ) otherlv_15= 'max' ( ( (lv_max_16_1= RULE_WHOLE_NUM_STR | lv_max_16_2= 'unlimited' ) ) ) ( ( ( ( ({...}? => ( ({...}? => (otherlv_18= 'taggerId' ( ( (lv_taggerId_19_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_19_2= ruleQUALIFIED_NAME ) ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_20= 'enumType' ( (otherlv_21= RULE_STRING ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_22= 'description' ( (lv_description_23_0= RULE_STRING ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_24= 'defaultValue' ( (lv_defaultValue_25_0= RULE_STRING ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_26= 'fileExtension' ( (lv_fileExtension_27_0= RULE_STRING ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_28= 'mediaType' ( (lv_mediaType_29_0= RULE_STRING ) ) ) ) ) ) )* ) ) ) otherlv_30= '}' ) )
            // InternalOseeDsl.g:657:1: (otherlv_0= 'attributeType' ( (lv_name_1_0= RULE_STRING ) ) (otherlv_2= 'extends' ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) ) ) (otherlv_4= 'overrides' ( (otherlv_5= RULE_STRING ) ) )? otherlv_6= '{' (otherlv_7= 'guid' ( (lv_typeGuid_8_0= RULE_STRING ) ) )? otherlv_9= 'id' ( (lv_id_10_0= RULE_WHOLE_NUM_STR ) ) otherlv_11= 'dataProvider' ( ( (lv_dataProvider_12_1= 'DefaultAttributeDataProvider' | lv_dataProvider_12_2= 'UriAttributeDataProvider' | lv_dataProvider_12_3= ruleQUALIFIED_NAME ) ) ) otherlv_13= 'min' ( (lv_min_14_0= RULE_WHOLE_NUM_STR ) ) otherlv_15= 'max' ( ( (lv_max_16_1= RULE_WHOLE_NUM_STR | lv_max_16_2= 'unlimited' ) ) ) ( ( ( ( ({...}? => ( ({...}? => (otherlv_18= 'taggerId' ( ( (lv_taggerId_19_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_19_2= ruleQUALIFIED_NAME ) ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_20= 'enumType' ( (otherlv_21= RULE_STRING ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_22= 'description' ( (lv_description_23_0= RULE_STRING ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_24= 'defaultValue' ( (lv_defaultValue_25_0= RULE_STRING ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_26= 'fileExtension' ( (lv_fileExtension_27_0= RULE_STRING ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_28= 'mediaType' ( (lv_mediaType_29_0= RULE_STRING ) ) ) ) ) ) )* ) ) ) otherlv_30= '}' )
            {
            // InternalOseeDsl.g:657:1: (otherlv_0= 'attributeType' ( (lv_name_1_0= RULE_STRING ) ) (otherlv_2= 'extends' ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) ) ) (otherlv_4= 'overrides' ( (otherlv_5= RULE_STRING ) ) )? otherlv_6= '{' (otherlv_7= 'guid' ( (lv_typeGuid_8_0= RULE_STRING ) ) )? otherlv_9= 'id' ( (lv_id_10_0= RULE_WHOLE_NUM_STR ) ) otherlv_11= 'dataProvider' ( ( (lv_dataProvider_12_1= 'DefaultAttributeDataProvider' | lv_dataProvider_12_2= 'UriAttributeDataProvider' | lv_dataProvider_12_3= ruleQUALIFIED_NAME ) ) ) otherlv_13= 'min' ( (lv_min_14_0= RULE_WHOLE_NUM_STR ) ) otherlv_15= 'max' ( ( (lv_max_16_1= RULE_WHOLE_NUM_STR | lv_max_16_2= 'unlimited' ) ) ) ( ( ( ( ({...}? => ( ({...}? => (otherlv_18= 'taggerId' ( ( (lv_taggerId_19_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_19_2= ruleQUALIFIED_NAME ) ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_20= 'enumType' ( (otherlv_21= RULE_STRING ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_22= 'description' ( (lv_description_23_0= RULE_STRING ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_24= 'defaultValue' ( (lv_defaultValue_25_0= RULE_STRING ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_26= 'fileExtension' ( (lv_fileExtension_27_0= RULE_STRING ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_28= 'mediaType' ( (lv_mediaType_29_0= RULE_STRING ) ) ) ) ) ) )* ) ) ) otherlv_30= '}' )
            // InternalOseeDsl.g:657:3: otherlv_0= 'attributeType' ( (lv_name_1_0= RULE_STRING ) ) (otherlv_2= 'extends' ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) ) ) (otherlv_4= 'overrides' ( (otherlv_5= RULE_STRING ) ) )? otherlv_6= '{' (otherlv_7= 'guid' ( (lv_typeGuid_8_0= RULE_STRING ) ) )? otherlv_9= 'id' ( (lv_id_10_0= RULE_WHOLE_NUM_STR ) ) otherlv_11= 'dataProvider' ( ( (lv_dataProvider_12_1= 'DefaultAttributeDataProvider' | lv_dataProvider_12_2= 'UriAttributeDataProvider' | lv_dataProvider_12_3= ruleQUALIFIED_NAME ) ) ) otherlv_13= 'min' ( (lv_min_14_0= RULE_WHOLE_NUM_STR ) ) otherlv_15= 'max' ( ( (lv_max_16_1= RULE_WHOLE_NUM_STR | lv_max_16_2= 'unlimited' ) ) ) ( ( ( ( ({...}? => ( ({...}? => (otherlv_18= 'taggerId' ( ( (lv_taggerId_19_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_19_2= ruleQUALIFIED_NAME ) ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_20= 'enumType' ( (otherlv_21= RULE_STRING ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_22= 'description' ( (lv_description_23_0= RULE_STRING ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_24= 'defaultValue' ( (lv_defaultValue_25_0= RULE_STRING ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_26= 'fileExtension' ( (lv_fileExtension_27_0= RULE_STRING ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_28= 'mediaType' ( (lv_mediaType_29_0= RULE_STRING ) ) ) ) ) ) )* ) ) ) otherlv_30= '}'
            {
            otherlv_0=(Token)match(input,24,FOLLOW_6); 

                	newLeafNode(otherlv_0, grammarAccess.getXAttributeTypeAccess().getAttributeTypeKeyword_0());
                
            // InternalOseeDsl.g:661:1: ( (lv_name_1_0= RULE_STRING ) )
            // InternalOseeDsl.g:662:1: (lv_name_1_0= RULE_STRING )
            {
            // InternalOseeDsl.g:662:1: (lv_name_1_0= RULE_STRING )
            // InternalOseeDsl.g:663:3: lv_name_1_0= RULE_STRING
            {
            lv_name_1_0=(Token)match(input,RULE_STRING,FOLLOW_17); 

            			newLeafNode(lv_name_1_0, grammarAccess.getXAttributeTypeAccess().getNameSTRINGTerminalRuleCall_1_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getXAttributeTypeRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"name",
                    		lv_name_1_0, 
                    		"org.eclipse.osee.framework.core.dsl.OseeDsl.STRING");
            	    

            }


            }

            // InternalOseeDsl.g:679:2: (otherlv_2= 'extends' ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) ) )
            // InternalOseeDsl.g:679:4: otherlv_2= 'extends' ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) )
            {
            otherlv_2=(Token)match(input,16,FOLLOW_18); 

                	newLeafNode(otherlv_2, grammarAccess.getXAttributeTypeAccess().getExtendsKeyword_2_0());
                
            // InternalOseeDsl.g:683:1: ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) )
            // InternalOseeDsl.g:684:1: (lv_baseAttributeType_3_0= ruleAttributeBaseType )
            {
            // InternalOseeDsl.g:684:1: (lv_baseAttributeType_3_0= ruleAttributeBaseType )
            // InternalOseeDsl.g:685:3: lv_baseAttributeType_3_0= ruleAttributeBaseType
            {
             
            	        newCompositeNode(grammarAccess.getXAttributeTypeAccess().getBaseAttributeTypeAttributeBaseTypeParserRuleCall_2_1_0()); 
            	    
            pushFollow(FOLLOW_19);
            lv_baseAttributeType_3_0=ruleAttributeBaseType();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getXAttributeTypeRule());
            	        }
                   		set(
                   			current, 
                   			"baseAttributeType",
                    		lv_baseAttributeType_3_0, 
                    		"org.eclipse.osee.framework.core.dsl.OseeDsl.AttributeBaseType");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }


            }

            // InternalOseeDsl.g:701:3: (otherlv_4= 'overrides' ( (otherlv_5= RULE_STRING ) ) )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==25) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // InternalOseeDsl.g:701:5: otherlv_4= 'overrides' ( (otherlv_5= RULE_STRING ) )
                    {
                    otherlv_4=(Token)match(input,25,FOLLOW_6); 

                        	newLeafNode(otherlv_4, grammarAccess.getXAttributeTypeAccess().getOverridesKeyword_3_0());
                        
                    // InternalOseeDsl.g:705:1: ( (otherlv_5= RULE_STRING ) )
                    // InternalOseeDsl.g:706:1: (otherlv_5= RULE_STRING )
                    {
                    // InternalOseeDsl.g:706:1: (otherlv_5= RULE_STRING )
                    // InternalOseeDsl.g:707:3: otherlv_5= RULE_STRING
                    {

                    			if (current==null) {
                    	            current = createModelElement(grammarAccess.getXAttributeTypeRule());
                    	        }
                            
                    otherlv_5=(Token)match(input,RULE_STRING,FOLLOW_20); 

                    		newLeafNode(otherlv_5, grammarAccess.getXAttributeTypeAccess().getOverrideXAttributeTypeCrossReference_3_1_0()); 
                    	

                    }


                    }


                    }
                    break;

            }

            otherlv_6=(Token)match(input,18,FOLLOW_12); 

                	newLeafNode(otherlv_6, grammarAccess.getXAttributeTypeAccess().getLeftCurlyBracketKeyword_4());
                
            // InternalOseeDsl.g:722:1: (otherlv_7= 'guid' ( (lv_typeGuid_8_0= RULE_STRING ) ) )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==19) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // InternalOseeDsl.g:722:3: otherlv_7= 'guid' ( (lv_typeGuid_8_0= RULE_STRING ) )
                    {
                    otherlv_7=(Token)match(input,19,FOLLOW_6); 

                        	newLeafNode(otherlv_7, grammarAccess.getXAttributeTypeAccess().getGuidKeyword_5_0());
                        
                    // InternalOseeDsl.g:726:1: ( (lv_typeGuid_8_0= RULE_STRING ) )
                    // InternalOseeDsl.g:727:1: (lv_typeGuid_8_0= RULE_STRING )
                    {
                    // InternalOseeDsl.g:727:1: (lv_typeGuid_8_0= RULE_STRING )
                    // InternalOseeDsl.g:728:3: lv_typeGuid_8_0= RULE_STRING
                    {
                    lv_typeGuid_8_0=(Token)match(input,RULE_STRING,FOLLOW_13); 

                    			newLeafNode(lv_typeGuid_8_0, grammarAccess.getXAttributeTypeAccess().getTypeGuidSTRINGTerminalRuleCall_5_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getXAttributeTypeRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"typeGuid",
                            		lv_typeGuid_8_0, 
                            		"org.eclipse.osee.framework.core.dsl.OseeDsl.STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            otherlv_9=(Token)match(input,20,FOLLOW_14); 

                	newLeafNode(otherlv_9, grammarAccess.getXAttributeTypeAccess().getIdKeyword_6());
                
            // InternalOseeDsl.g:748:1: ( (lv_id_10_0= RULE_WHOLE_NUM_STR ) )
            // InternalOseeDsl.g:749:1: (lv_id_10_0= RULE_WHOLE_NUM_STR )
            {
            // InternalOseeDsl.g:749:1: (lv_id_10_0= RULE_WHOLE_NUM_STR )
            // InternalOseeDsl.g:750:3: lv_id_10_0= RULE_WHOLE_NUM_STR
            {
            lv_id_10_0=(Token)match(input,RULE_WHOLE_NUM_STR,FOLLOW_21); 

            			newLeafNode(lv_id_10_0, grammarAccess.getXAttributeTypeAccess().getIdWHOLE_NUM_STRTerminalRuleCall_7_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getXAttributeTypeRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"id",
                    		lv_id_10_0, 
                    		"org.eclipse.osee.framework.core.dsl.OseeDsl.WHOLE_NUM_STR");
            	    

            }


            }

            otherlv_11=(Token)match(input,26,FOLLOW_22); 

                	newLeafNode(otherlv_11, grammarAccess.getXAttributeTypeAccess().getDataProviderKeyword_8());
                
            // InternalOseeDsl.g:770:1: ( ( (lv_dataProvider_12_1= 'DefaultAttributeDataProvider' | lv_dataProvider_12_2= 'UriAttributeDataProvider' | lv_dataProvider_12_3= ruleQUALIFIED_NAME ) ) )
            // InternalOseeDsl.g:771:1: ( (lv_dataProvider_12_1= 'DefaultAttributeDataProvider' | lv_dataProvider_12_2= 'UriAttributeDataProvider' | lv_dataProvider_12_3= ruleQUALIFIED_NAME ) )
            {
            // InternalOseeDsl.g:771:1: ( (lv_dataProvider_12_1= 'DefaultAttributeDataProvider' | lv_dataProvider_12_2= 'UriAttributeDataProvider' | lv_dataProvider_12_3= ruleQUALIFIED_NAME ) )
            // InternalOseeDsl.g:772:1: (lv_dataProvider_12_1= 'DefaultAttributeDataProvider' | lv_dataProvider_12_2= 'UriAttributeDataProvider' | lv_dataProvider_12_3= ruleQUALIFIED_NAME )
            {
            // InternalOseeDsl.g:772:1: (lv_dataProvider_12_1= 'DefaultAttributeDataProvider' | lv_dataProvider_12_2= 'UriAttributeDataProvider' | lv_dataProvider_12_3= ruleQUALIFIED_NAME )
            int alt14=3;
            switch ( input.LA(1) ) {
            case 27:
                {
                alt14=1;
                }
                break;
            case 28:
                {
                alt14=2;
                }
                break;
            case RULE_ID:
                {
                alt14=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 14, 0, input);

                throw nvae;
            }

            switch (alt14) {
                case 1 :
                    // InternalOseeDsl.g:773:3: lv_dataProvider_12_1= 'DefaultAttributeDataProvider'
                    {
                    lv_dataProvider_12_1=(Token)match(input,27,FOLLOW_23); 

                            newLeafNode(lv_dataProvider_12_1, grammarAccess.getXAttributeTypeAccess().getDataProviderDefaultAttributeDataProviderKeyword_9_0_0());
                        

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getXAttributeTypeRule());
                    	        }
                           		setWithLastConsumed(current, "dataProvider", lv_dataProvider_12_1, null);
                    	    

                    }
                    break;
                case 2 :
                    // InternalOseeDsl.g:785:8: lv_dataProvider_12_2= 'UriAttributeDataProvider'
                    {
                    lv_dataProvider_12_2=(Token)match(input,28,FOLLOW_23); 

                            newLeafNode(lv_dataProvider_12_2, grammarAccess.getXAttributeTypeAccess().getDataProviderUriAttributeDataProviderKeyword_9_0_1());
                        

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getXAttributeTypeRule());
                    	        }
                           		setWithLastConsumed(current, "dataProvider", lv_dataProvider_12_2, null);
                    	    

                    }
                    break;
                case 3 :
                    // InternalOseeDsl.g:797:8: lv_dataProvider_12_3= ruleQUALIFIED_NAME
                    {
                     
                    	        newCompositeNode(grammarAccess.getXAttributeTypeAccess().getDataProviderQUALIFIED_NAMEParserRuleCall_9_0_2()); 
                    	    
                    pushFollow(FOLLOW_23);
                    lv_dataProvider_12_3=ruleQUALIFIED_NAME();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getXAttributeTypeRule());
                    	        }
                           		set(
                           			current, 
                           			"dataProvider",
                            		lv_dataProvider_12_3, 
                            		"org.eclipse.osee.framework.core.dsl.OseeDsl.QUALIFIED_NAME");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }
                    break;

            }


            }


            }

            otherlv_13=(Token)match(input,29,FOLLOW_14); 

                	newLeafNode(otherlv_13, grammarAccess.getXAttributeTypeAccess().getMinKeyword_10());
                
            // InternalOseeDsl.g:819:1: ( (lv_min_14_0= RULE_WHOLE_NUM_STR ) )
            // InternalOseeDsl.g:820:1: (lv_min_14_0= RULE_WHOLE_NUM_STR )
            {
            // InternalOseeDsl.g:820:1: (lv_min_14_0= RULE_WHOLE_NUM_STR )
            // InternalOseeDsl.g:821:3: lv_min_14_0= RULE_WHOLE_NUM_STR
            {
            lv_min_14_0=(Token)match(input,RULE_WHOLE_NUM_STR,FOLLOW_24); 

            			newLeafNode(lv_min_14_0, grammarAccess.getXAttributeTypeAccess().getMinWHOLE_NUM_STRTerminalRuleCall_11_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getXAttributeTypeRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"min",
                    		lv_min_14_0, 
                    		"org.eclipse.osee.framework.core.dsl.OseeDsl.WHOLE_NUM_STR");
            	    

            }


            }

            otherlv_15=(Token)match(input,30,FOLLOW_25); 

                	newLeafNode(otherlv_15, grammarAccess.getXAttributeTypeAccess().getMaxKeyword_12());
                
            // InternalOseeDsl.g:841:1: ( ( (lv_max_16_1= RULE_WHOLE_NUM_STR | lv_max_16_2= 'unlimited' ) ) )
            // InternalOseeDsl.g:842:1: ( (lv_max_16_1= RULE_WHOLE_NUM_STR | lv_max_16_2= 'unlimited' ) )
            {
            // InternalOseeDsl.g:842:1: ( (lv_max_16_1= RULE_WHOLE_NUM_STR | lv_max_16_2= 'unlimited' ) )
            // InternalOseeDsl.g:843:1: (lv_max_16_1= RULE_WHOLE_NUM_STR | lv_max_16_2= 'unlimited' )
            {
            // InternalOseeDsl.g:843:1: (lv_max_16_1= RULE_WHOLE_NUM_STR | lv_max_16_2= 'unlimited' )
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==RULE_WHOLE_NUM_STR) ) {
                alt15=1;
            }
            else if ( (LA15_0==31) ) {
                alt15=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 15, 0, input);

                throw nvae;
            }
            switch (alt15) {
                case 1 :
                    // InternalOseeDsl.g:844:3: lv_max_16_1= RULE_WHOLE_NUM_STR
                    {
                    lv_max_16_1=(Token)match(input,RULE_WHOLE_NUM_STR,FOLLOW_26); 

                    			newLeafNode(lv_max_16_1, grammarAccess.getXAttributeTypeAccess().getMaxWHOLE_NUM_STRTerminalRuleCall_13_0_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getXAttributeTypeRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"max",
                            		lv_max_16_1, 
                            		"org.eclipse.osee.framework.core.dsl.OseeDsl.WHOLE_NUM_STR");
                    	    

                    }
                    break;
                case 2 :
                    // InternalOseeDsl.g:859:8: lv_max_16_2= 'unlimited'
                    {
                    lv_max_16_2=(Token)match(input,31,FOLLOW_26); 

                            newLeafNode(lv_max_16_2, grammarAccess.getXAttributeTypeAccess().getMaxUnlimitedKeyword_13_0_1());
                        

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getXAttributeTypeRule());
                    	        }
                           		setWithLastConsumed(current, "max", lv_max_16_2, null);
                    	    

                    }
                    break;

            }


            }


            }

            // InternalOseeDsl.g:874:2: ( ( ( ( ({...}? => ( ({...}? => (otherlv_18= 'taggerId' ( ( (lv_taggerId_19_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_19_2= ruleQUALIFIED_NAME ) ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_20= 'enumType' ( (otherlv_21= RULE_STRING ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_22= 'description' ( (lv_description_23_0= RULE_STRING ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_24= 'defaultValue' ( (lv_defaultValue_25_0= RULE_STRING ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_26= 'fileExtension' ( (lv_fileExtension_27_0= RULE_STRING ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_28= 'mediaType' ( (lv_mediaType_29_0= RULE_STRING ) ) ) ) ) ) )* ) ) )
            // InternalOseeDsl.g:876:1: ( ( ( ({...}? => ( ({...}? => (otherlv_18= 'taggerId' ( ( (lv_taggerId_19_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_19_2= ruleQUALIFIED_NAME ) ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_20= 'enumType' ( (otherlv_21= RULE_STRING ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_22= 'description' ( (lv_description_23_0= RULE_STRING ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_24= 'defaultValue' ( (lv_defaultValue_25_0= RULE_STRING ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_26= 'fileExtension' ( (lv_fileExtension_27_0= RULE_STRING ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_28= 'mediaType' ( (lv_mediaType_29_0= RULE_STRING ) ) ) ) ) ) )* ) )
            {
            // InternalOseeDsl.g:876:1: ( ( ( ({...}? => ( ({...}? => (otherlv_18= 'taggerId' ( ( (lv_taggerId_19_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_19_2= ruleQUALIFIED_NAME ) ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_20= 'enumType' ( (otherlv_21= RULE_STRING ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_22= 'description' ( (lv_description_23_0= RULE_STRING ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_24= 'defaultValue' ( (lv_defaultValue_25_0= RULE_STRING ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_26= 'fileExtension' ( (lv_fileExtension_27_0= RULE_STRING ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_28= 'mediaType' ( (lv_mediaType_29_0= RULE_STRING ) ) ) ) ) ) )* ) )
            // InternalOseeDsl.g:877:2: ( ( ({...}? => ( ({...}? => (otherlv_18= 'taggerId' ( ( (lv_taggerId_19_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_19_2= ruleQUALIFIED_NAME ) ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_20= 'enumType' ( (otherlv_21= RULE_STRING ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_22= 'description' ( (lv_description_23_0= RULE_STRING ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_24= 'defaultValue' ( (lv_defaultValue_25_0= RULE_STRING ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_26= 'fileExtension' ( (lv_fileExtension_27_0= RULE_STRING ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_28= 'mediaType' ( (lv_mediaType_29_0= RULE_STRING ) ) ) ) ) ) )* )
            {
             
            	  getUnorderedGroupHelper().enter(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14());
            	
            // InternalOseeDsl.g:880:2: ( ( ({...}? => ( ({...}? => (otherlv_18= 'taggerId' ( ( (lv_taggerId_19_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_19_2= ruleQUALIFIED_NAME ) ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_20= 'enumType' ( (otherlv_21= RULE_STRING ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_22= 'description' ( (lv_description_23_0= RULE_STRING ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_24= 'defaultValue' ( (lv_defaultValue_25_0= RULE_STRING ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_26= 'fileExtension' ( (lv_fileExtension_27_0= RULE_STRING ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_28= 'mediaType' ( (lv_mediaType_29_0= RULE_STRING ) ) ) ) ) ) )* )
            // InternalOseeDsl.g:881:3: ( ({...}? => ( ({...}? => (otherlv_18= 'taggerId' ( ( (lv_taggerId_19_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_19_2= ruleQUALIFIED_NAME ) ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_20= 'enumType' ( (otherlv_21= RULE_STRING ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_22= 'description' ( (lv_description_23_0= RULE_STRING ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_24= 'defaultValue' ( (lv_defaultValue_25_0= RULE_STRING ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_26= 'fileExtension' ( (lv_fileExtension_27_0= RULE_STRING ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_28= 'mediaType' ( (lv_mediaType_29_0= RULE_STRING ) ) ) ) ) ) )*
            {
            // InternalOseeDsl.g:881:3: ( ({...}? => ( ({...}? => (otherlv_18= 'taggerId' ( ( (lv_taggerId_19_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_19_2= ruleQUALIFIED_NAME ) ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_20= 'enumType' ( (otherlv_21= RULE_STRING ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_22= 'description' ( (lv_description_23_0= RULE_STRING ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_24= 'defaultValue' ( (lv_defaultValue_25_0= RULE_STRING ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_26= 'fileExtension' ( (lv_fileExtension_27_0= RULE_STRING ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_28= 'mediaType' ( (lv_mediaType_29_0= RULE_STRING ) ) ) ) ) ) )*
            loop17:
            do {
                int alt17=7;
                int LA17_0 = input.LA(1);

                if ( LA17_0 == 32 && getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 0) ) {
                    alt17=1;
                }
                else if ( LA17_0 == 34 && getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 1) ) {
                    alt17=2;
                }
                else if ( LA17_0 == 35 && getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 2) ) {
                    alt17=3;
                }
                else if ( LA17_0 == 36 && getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 3) ) {
                    alt17=4;
                }
                else if ( LA17_0 == 37 && getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 4) ) {
                    alt17=5;
                }
                else if ( LA17_0 == 38 && getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 5) ) {
                    alt17=6;
                }


                switch (alt17) {
            	case 1 :
            	    // InternalOseeDsl.g:883:4: ({...}? => ( ({...}? => (otherlv_18= 'taggerId' ( ( (lv_taggerId_19_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_19_2= ruleQUALIFIED_NAME ) ) ) ) ) ) )
            	    {
            	    // InternalOseeDsl.g:883:4: ({...}? => ( ({...}? => (otherlv_18= 'taggerId' ( ( (lv_taggerId_19_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_19_2= ruleQUALIFIED_NAME ) ) ) ) ) ) )
            	    // InternalOseeDsl.g:884:5: {...}? => ( ({...}? => (otherlv_18= 'taggerId' ( ( (lv_taggerId_19_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_19_2= ruleQUALIFIED_NAME ) ) ) ) ) )
            	    {
            	    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 0) ) {
            	        throw new FailedPredicateException(input, "ruleXAttributeType", "getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 0)");
            	    }
            	    // InternalOseeDsl.g:884:112: ( ({...}? => (otherlv_18= 'taggerId' ( ( (lv_taggerId_19_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_19_2= ruleQUALIFIED_NAME ) ) ) ) ) )
            	    // InternalOseeDsl.g:885:6: ({...}? => (otherlv_18= 'taggerId' ( ( (lv_taggerId_19_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_19_2= ruleQUALIFIED_NAME ) ) ) ) )
            	    {
            	     
            	    	 				  getUnorderedGroupHelper().select(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 0);
            	    	 				
            	    // InternalOseeDsl.g:888:6: ({...}? => (otherlv_18= 'taggerId' ( ( (lv_taggerId_19_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_19_2= ruleQUALIFIED_NAME ) ) ) ) )
            	    // InternalOseeDsl.g:888:7: {...}? => (otherlv_18= 'taggerId' ( ( (lv_taggerId_19_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_19_2= ruleQUALIFIED_NAME ) ) ) )
            	    {
            	    if ( !((true)) ) {
            	        throw new FailedPredicateException(input, "ruleXAttributeType", "true");
            	    }
            	    // InternalOseeDsl.g:888:16: (otherlv_18= 'taggerId' ( ( (lv_taggerId_19_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_19_2= ruleQUALIFIED_NAME ) ) ) )
            	    // InternalOseeDsl.g:888:18: otherlv_18= 'taggerId' ( ( (lv_taggerId_19_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_19_2= ruleQUALIFIED_NAME ) ) )
            	    {
            	    otherlv_18=(Token)match(input,32,FOLLOW_27); 

            	        	newLeafNode(otherlv_18, grammarAccess.getXAttributeTypeAccess().getTaggerIdKeyword_14_0_0());
            	        
            	    // InternalOseeDsl.g:892:1: ( ( (lv_taggerId_19_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_19_2= ruleQUALIFIED_NAME ) ) )
            	    // InternalOseeDsl.g:893:1: ( (lv_taggerId_19_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_19_2= ruleQUALIFIED_NAME ) )
            	    {
            	    // InternalOseeDsl.g:893:1: ( (lv_taggerId_19_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_19_2= ruleQUALIFIED_NAME ) )
            	    // InternalOseeDsl.g:894:1: (lv_taggerId_19_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_19_2= ruleQUALIFIED_NAME )
            	    {
            	    // InternalOseeDsl.g:894:1: (lv_taggerId_19_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_19_2= ruleQUALIFIED_NAME )
            	    int alt16=2;
            	    int LA16_0 = input.LA(1);

            	    if ( (LA16_0==33) ) {
            	        alt16=1;
            	    }
            	    else if ( (LA16_0==RULE_ID) ) {
            	        alt16=2;
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 16, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt16) {
            	        case 1 :
            	            // InternalOseeDsl.g:895:3: lv_taggerId_19_1= 'DefaultAttributeTaggerProvider'
            	            {
            	            lv_taggerId_19_1=(Token)match(input,33,FOLLOW_26); 

            	                    newLeafNode(lv_taggerId_19_1, grammarAccess.getXAttributeTypeAccess().getTaggerIdDefaultAttributeTaggerProviderKeyword_14_0_1_0_0());
            	                

            	            	        if (current==null) {
            	            	            current = createModelElement(grammarAccess.getXAttributeTypeRule());
            	            	        }
            	                   		setWithLastConsumed(current, "taggerId", lv_taggerId_19_1, null);
            	            	    

            	            }
            	            break;
            	        case 2 :
            	            // InternalOseeDsl.g:907:8: lv_taggerId_19_2= ruleQUALIFIED_NAME
            	            {
            	             
            	            	        newCompositeNode(grammarAccess.getXAttributeTypeAccess().getTaggerIdQUALIFIED_NAMEParserRuleCall_14_0_1_0_1()); 
            	            	    
            	            pushFollow(FOLLOW_26);
            	            lv_taggerId_19_2=ruleQUALIFIED_NAME();

            	            state._fsp--;


            	            	        if (current==null) {
            	            	            current = createModelElementForParent(grammarAccess.getXAttributeTypeRule());
            	            	        }
            	                   		set(
            	                   			current, 
            	                   			"taggerId",
            	                    		lv_taggerId_19_2, 
            	                    		"org.eclipse.osee.framework.core.dsl.OseeDsl.QUALIFIED_NAME");
            	            	        afterParserOrEnumRuleCall();
            	            	    

            	            }
            	            break;

            	    }


            	    }


            	    }


            	    }


            	    }

            	     
            	    	 				  getUnorderedGroupHelper().returnFromSelection(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14());
            	    	 				

            	    }


            	    }


            	    }
            	    break;
            	case 2 :
            	    // InternalOseeDsl.g:932:4: ({...}? => ( ({...}? => (otherlv_20= 'enumType' ( (otherlv_21= RULE_STRING ) ) ) ) ) )
            	    {
            	    // InternalOseeDsl.g:932:4: ({...}? => ( ({...}? => (otherlv_20= 'enumType' ( (otherlv_21= RULE_STRING ) ) ) ) ) )
            	    // InternalOseeDsl.g:933:5: {...}? => ( ({...}? => (otherlv_20= 'enumType' ( (otherlv_21= RULE_STRING ) ) ) ) )
            	    {
            	    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 1) ) {
            	        throw new FailedPredicateException(input, "ruleXAttributeType", "getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 1)");
            	    }
            	    // InternalOseeDsl.g:933:112: ( ({...}? => (otherlv_20= 'enumType' ( (otherlv_21= RULE_STRING ) ) ) ) )
            	    // InternalOseeDsl.g:934:6: ({...}? => (otherlv_20= 'enumType' ( (otherlv_21= RULE_STRING ) ) ) )
            	    {
            	     
            	    	 				  getUnorderedGroupHelper().select(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 1);
            	    	 				
            	    // InternalOseeDsl.g:937:6: ({...}? => (otherlv_20= 'enumType' ( (otherlv_21= RULE_STRING ) ) ) )
            	    // InternalOseeDsl.g:937:7: {...}? => (otherlv_20= 'enumType' ( (otherlv_21= RULE_STRING ) ) )
            	    {
            	    if ( !((true)) ) {
            	        throw new FailedPredicateException(input, "ruleXAttributeType", "true");
            	    }
            	    // InternalOseeDsl.g:937:16: (otherlv_20= 'enumType' ( (otherlv_21= RULE_STRING ) ) )
            	    // InternalOseeDsl.g:937:18: otherlv_20= 'enumType' ( (otherlv_21= RULE_STRING ) )
            	    {
            	    otherlv_20=(Token)match(input,34,FOLLOW_6); 

            	        	newLeafNode(otherlv_20, grammarAccess.getXAttributeTypeAccess().getEnumTypeKeyword_14_1_0());
            	        
            	    // InternalOseeDsl.g:941:1: ( (otherlv_21= RULE_STRING ) )
            	    // InternalOseeDsl.g:942:1: (otherlv_21= RULE_STRING )
            	    {
            	    // InternalOseeDsl.g:942:1: (otherlv_21= RULE_STRING )
            	    // InternalOseeDsl.g:943:3: otherlv_21= RULE_STRING
            	    {

            	    			if (current==null) {
            	    	            current = createModelElement(grammarAccess.getXAttributeTypeRule());
            	    	        }
            	            
            	    otherlv_21=(Token)match(input,RULE_STRING,FOLLOW_26); 

            	    		newLeafNode(otherlv_21, grammarAccess.getXAttributeTypeAccess().getEnumTypeXOseeEnumTypeCrossReference_14_1_1_0()); 
            	    	

            	    }


            	    }


            	    }


            	    }

            	     
            	    	 				  getUnorderedGroupHelper().returnFromSelection(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14());
            	    	 				

            	    }


            	    }


            	    }
            	    break;
            	case 3 :
            	    // InternalOseeDsl.g:961:4: ({...}? => ( ({...}? => (otherlv_22= 'description' ( (lv_description_23_0= RULE_STRING ) ) ) ) ) )
            	    {
            	    // InternalOseeDsl.g:961:4: ({...}? => ( ({...}? => (otherlv_22= 'description' ( (lv_description_23_0= RULE_STRING ) ) ) ) ) )
            	    // InternalOseeDsl.g:962:5: {...}? => ( ({...}? => (otherlv_22= 'description' ( (lv_description_23_0= RULE_STRING ) ) ) ) )
            	    {
            	    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 2) ) {
            	        throw new FailedPredicateException(input, "ruleXAttributeType", "getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 2)");
            	    }
            	    // InternalOseeDsl.g:962:112: ( ({...}? => (otherlv_22= 'description' ( (lv_description_23_0= RULE_STRING ) ) ) ) )
            	    // InternalOseeDsl.g:963:6: ({...}? => (otherlv_22= 'description' ( (lv_description_23_0= RULE_STRING ) ) ) )
            	    {
            	     
            	    	 				  getUnorderedGroupHelper().select(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 2);
            	    	 				
            	    // InternalOseeDsl.g:966:6: ({...}? => (otherlv_22= 'description' ( (lv_description_23_0= RULE_STRING ) ) ) )
            	    // InternalOseeDsl.g:966:7: {...}? => (otherlv_22= 'description' ( (lv_description_23_0= RULE_STRING ) ) )
            	    {
            	    if ( !((true)) ) {
            	        throw new FailedPredicateException(input, "ruleXAttributeType", "true");
            	    }
            	    // InternalOseeDsl.g:966:16: (otherlv_22= 'description' ( (lv_description_23_0= RULE_STRING ) ) )
            	    // InternalOseeDsl.g:966:18: otherlv_22= 'description' ( (lv_description_23_0= RULE_STRING ) )
            	    {
            	    otherlv_22=(Token)match(input,35,FOLLOW_6); 

            	        	newLeafNode(otherlv_22, grammarAccess.getXAttributeTypeAccess().getDescriptionKeyword_14_2_0());
            	        
            	    // InternalOseeDsl.g:970:1: ( (lv_description_23_0= RULE_STRING ) )
            	    // InternalOseeDsl.g:971:1: (lv_description_23_0= RULE_STRING )
            	    {
            	    // InternalOseeDsl.g:971:1: (lv_description_23_0= RULE_STRING )
            	    // InternalOseeDsl.g:972:3: lv_description_23_0= RULE_STRING
            	    {
            	    lv_description_23_0=(Token)match(input,RULE_STRING,FOLLOW_26); 

            	    			newLeafNode(lv_description_23_0, grammarAccess.getXAttributeTypeAccess().getDescriptionSTRINGTerminalRuleCall_14_2_1_0()); 
            	    		

            	    	        if (current==null) {
            	    	            current = createModelElement(grammarAccess.getXAttributeTypeRule());
            	    	        }
            	           		setWithLastConsumed(
            	           			current, 
            	           			"description",
            	            		lv_description_23_0, 
            	            		"org.eclipse.osee.framework.core.dsl.OseeDsl.STRING");
            	    	    

            	    }


            	    }


            	    }


            	    }

            	     
            	    	 				  getUnorderedGroupHelper().returnFromSelection(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14());
            	    	 				

            	    }


            	    }


            	    }
            	    break;
            	case 4 :
            	    // InternalOseeDsl.g:995:4: ({...}? => ( ({...}? => (otherlv_24= 'defaultValue' ( (lv_defaultValue_25_0= RULE_STRING ) ) ) ) ) )
            	    {
            	    // InternalOseeDsl.g:995:4: ({...}? => ( ({...}? => (otherlv_24= 'defaultValue' ( (lv_defaultValue_25_0= RULE_STRING ) ) ) ) ) )
            	    // InternalOseeDsl.g:996:5: {...}? => ( ({...}? => (otherlv_24= 'defaultValue' ( (lv_defaultValue_25_0= RULE_STRING ) ) ) ) )
            	    {
            	    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 3) ) {
            	        throw new FailedPredicateException(input, "ruleXAttributeType", "getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 3)");
            	    }
            	    // InternalOseeDsl.g:996:112: ( ({...}? => (otherlv_24= 'defaultValue' ( (lv_defaultValue_25_0= RULE_STRING ) ) ) ) )
            	    // InternalOseeDsl.g:997:6: ({...}? => (otherlv_24= 'defaultValue' ( (lv_defaultValue_25_0= RULE_STRING ) ) ) )
            	    {
            	     
            	    	 				  getUnorderedGroupHelper().select(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 3);
            	    	 				
            	    // InternalOseeDsl.g:1000:6: ({...}? => (otherlv_24= 'defaultValue' ( (lv_defaultValue_25_0= RULE_STRING ) ) ) )
            	    // InternalOseeDsl.g:1000:7: {...}? => (otherlv_24= 'defaultValue' ( (lv_defaultValue_25_0= RULE_STRING ) ) )
            	    {
            	    if ( !((true)) ) {
            	        throw new FailedPredicateException(input, "ruleXAttributeType", "true");
            	    }
            	    // InternalOseeDsl.g:1000:16: (otherlv_24= 'defaultValue' ( (lv_defaultValue_25_0= RULE_STRING ) ) )
            	    // InternalOseeDsl.g:1000:18: otherlv_24= 'defaultValue' ( (lv_defaultValue_25_0= RULE_STRING ) )
            	    {
            	    otherlv_24=(Token)match(input,36,FOLLOW_6); 

            	        	newLeafNode(otherlv_24, grammarAccess.getXAttributeTypeAccess().getDefaultValueKeyword_14_3_0());
            	        
            	    // InternalOseeDsl.g:1004:1: ( (lv_defaultValue_25_0= RULE_STRING ) )
            	    // InternalOseeDsl.g:1005:1: (lv_defaultValue_25_0= RULE_STRING )
            	    {
            	    // InternalOseeDsl.g:1005:1: (lv_defaultValue_25_0= RULE_STRING )
            	    // InternalOseeDsl.g:1006:3: lv_defaultValue_25_0= RULE_STRING
            	    {
            	    lv_defaultValue_25_0=(Token)match(input,RULE_STRING,FOLLOW_26); 

            	    			newLeafNode(lv_defaultValue_25_0, grammarAccess.getXAttributeTypeAccess().getDefaultValueSTRINGTerminalRuleCall_14_3_1_0()); 
            	    		

            	    	        if (current==null) {
            	    	            current = createModelElement(grammarAccess.getXAttributeTypeRule());
            	    	        }
            	           		setWithLastConsumed(
            	           			current, 
            	           			"defaultValue",
            	            		lv_defaultValue_25_0, 
            	            		"org.eclipse.osee.framework.core.dsl.OseeDsl.STRING");
            	    	    

            	    }


            	    }


            	    }


            	    }

            	     
            	    	 				  getUnorderedGroupHelper().returnFromSelection(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14());
            	    	 				

            	    }


            	    }


            	    }
            	    break;
            	case 5 :
            	    // InternalOseeDsl.g:1029:4: ({...}? => ( ({...}? => (otherlv_26= 'fileExtension' ( (lv_fileExtension_27_0= RULE_STRING ) ) ) ) ) )
            	    {
            	    // InternalOseeDsl.g:1029:4: ({...}? => ( ({...}? => (otherlv_26= 'fileExtension' ( (lv_fileExtension_27_0= RULE_STRING ) ) ) ) ) )
            	    // InternalOseeDsl.g:1030:5: {...}? => ( ({...}? => (otherlv_26= 'fileExtension' ( (lv_fileExtension_27_0= RULE_STRING ) ) ) ) )
            	    {
            	    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 4) ) {
            	        throw new FailedPredicateException(input, "ruleXAttributeType", "getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 4)");
            	    }
            	    // InternalOseeDsl.g:1030:112: ( ({...}? => (otherlv_26= 'fileExtension' ( (lv_fileExtension_27_0= RULE_STRING ) ) ) ) )
            	    // InternalOseeDsl.g:1031:6: ({...}? => (otherlv_26= 'fileExtension' ( (lv_fileExtension_27_0= RULE_STRING ) ) ) )
            	    {
            	     
            	    	 				  getUnorderedGroupHelper().select(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 4);
            	    	 				
            	    // InternalOseeDsl.g:1034:6: ({...}? => (otherlv_26= 'fileExtension' ( (lv_fileExtension_27_0= RULE_STRING ) ) ) )
            	    // InternalOseeDsl.g:1034:7: {...}? => (otherlv_26= 'fileExtension' ( (lv_fileExtension_27_0= RULE_STRING ) ) )
            	    {
            	    if ( !((true)) ) {
            	        throw new FailedPredicateException(input, "ruleXAttributeType", "true");
            	    }
            	    // InternalOseeDsl.g:1034:16: (otherlv_26= 'fileExtension' ( (lv_fileExtension_27_0= RULE_STRING ) ) )
            	    // InternalOseeDsl.g:1034:18: otherlv_26= 'fileExtension' ( (lv_fileExtension_27_0= RULE_STRING ) )
            	    {
            	    otherlv_26=(Token)match(input,37,FOLLOW_6); 

            	        	newLeafNode(otherlv_26, grammarAccess.getXAttributeTypeAccess().getFileExtensionKeyword_14_4_0());
            	        
            	    // InternalOseeDsl.g:1038:1: ( (lv_fileExtension_27_0= RULE_STRING ) )
            	    // InternalOseeDsl.g:1039:1: (lv_fileExtension_27_0= RULE_STRING )
            	    {
            	    // InternalOseeDsl.g:1039:1: (lv_fileExtension_27_0= RULE_STRING )
            	    // InternalOseeDsl.g:1040:3: lv_fileExtension_27_0= RULE_STRING
            	    {
            	    lv_fileExtension_27_0=(Token)match(input,RULE_STRING,FOLLOW_26); 

            	    			newLeafNode(lv_fileExtension_27_0, grammarAccess.getXAttributeTypeAccess().getFileExtensionSTRINGTerminalRuleCall_14_4_1_0()); 
            	    		

            	    	        if (current==null) {
            	    	            current = createModelElement(grammarAccess.getXAttributeTypeRule());
            	    	        }
            	           		setWithLastConsumed(
            	           			current, 
            	           			"fileExtension",
            	            		lv_fileExtension_27_0, 
            	            		"org.eclipse.osee.framework.core.dsl.OseeDsl.STRING");
            	    	    

            	    }


            	    }


            	    }


            	    }

            	     
            	    	 				  getUnorderedGroupHelper().returnFromSelection(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14());
            	    	 				

            	    }


            	    }


            	    }
            	    break;
            	case 6 :
            	    // InternalOseeDsl.g:1063:4: ({...}? => ( ({...}? => (otherlv_28= 'mediaType' ( (lv_mediaType_29_0= RULE_STRING ) ) ) ) ) )
            	    {
            	    // InternalOseeDsl.g:1063:4: ({...}? => ( ({...}? => (otherlv_28= 'mediaType' ( (lv_mediaType_29_0= RULE_STRING ) ) ) ) ) )
            	    // InternalOseeDsl.g:1064:5: {...}? => ( ({...}? => (otherlv_28= 'mediaType' ( (lv_mediaType_29_0= RULE_STRING ) ) ) ) )
            	    {
            	    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 5) ) {
            	        throw new FailedPredicateException(input, "ruleXAttributeType", "getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 5)");
            	    }
            	    // InternalOseeDsl.g:1064:112: ( ({...}? => (otherlv_28= 'mediaType' ( (lv_mediaType_29_0= RULE_STRING ) ) ) ) )
            	    // InternalOseeDsl.g:1065:6: ({...}? => (otherlv_28= 'mediaType' ( (lv_mediaType_29_0= RULE_STRING ) ) ) )
            	    {
            	     
            	    	 				  getUnorderedGroupHelper().select(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 5);
            	    	 				
            	    // InternalOseeDsl.g:1068:6: ({...}? => (otherlv_28= 'mediaType' ( (lv_mediaType_29_0= RULE_STRING ) ) ) )
            	    // InternalOseeDsl.g:1068:7: {...}? => (otherlv_28= 'mediaType' ( (lv_mediaType_29_0= RULE_STRING ) ) )
            	    {
            	    if ( !((true)) ) {
            	        throw new FailedPredicateException(input, "ruleXAttributeType", "true");
            	    }
            	    // InternalOseeDsl.g:1068:16: (otherlv_28= 'mediaType' ( (lv_mediaType_29_0= RULE_STRING ) ) )
            	    // InternalOseeDsl.g:1068:18: otherlv_28= 'mediaType' ( (lv_mediaType_29_0= RULE_STRING ) )
            	    {
            	    otherlv_28=(Token)match(input,38,FOLLOW_6); 

            	        	newLeafNode(otherlv_28, grammarAccess.getXAttributeTypeAccess().getMediaTypeKeyword_14_5_0());
            	        
            	    // InternalOseeDsl.g:1072:1: ( (lv_mediaType_29_0= RULE_STRING ) )
            	    // InternalOseeDsl.g:1073:1: (lv_mediaType_29_0= RULE_STRING )
            	    {
            	    // InternalOseeDsl.g:1073:1: (lv_mediaType_29_0= RULE_STRING )
            	    // InternalOseeDsl.g:1074:3: lv_mediaType_29_0= RULE_STRING
            	    {
            	    lv_mediaType_29_0=(Token)match(input,RULE_STRING,FOLLOW_26); 

            	    			newLeafNode(lv_mediaType_29_0, grammarAccess.getXAttributeTypeAccess().getMediaTypeSTRINGTerminalRuleCall_14_5_1_0()); 
            	    		

            	    	        if (current==null) {
            	    	            current = createModelElement(grammarAccess.getXAttributeTypeRule());
            	    	        }
            	           		setWithLastConsumed(
            	           			current, 
            	           			"mediaType",
            	            		lv_mediaType_29_0, 
            	            		"org.eclipse.osee.framework.core.dsl.OseeDsl.STRING");
            	    	    

            	    }


            	    }


            	    }


            	    }

            	     
            	    	 				  getUnorderedGroupHelper().returnFromSelection(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14());
            	    	 				

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop17;
                }
            } while (true);


            }


            }

             
            	  getUnorderedGroupHelper().leave(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14());
            	

            }

            otherlv_30=(Token)match(input,21,FOLLOW_2); 

                	newLeafNode(otherlv_30, grammarAccess.getXAttributeTypeAccess().getRightCurlyBracketKeyword_15());
                

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
    // $ANTLR end "ruleXAttributeType"


    // $ANTLR start "entryRuleAttributeBaseType"
    // InternalOseeDsl.g:1116:1: entryRuleAttributeBaseType returns [String current=null] : iv_ruleAttributeBaseType= ruleAttributeBaseType EOF ;
    public final String entryRuleAttributeBaseType() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleAttributeBaseType = null;


        try {
            // InternalOseeDsl.g:1117:2: (iv_ruleAttributeBaseType= ruleAttributeBaseType EOF )
            // InternalOseeDsl.g:1118:2: iv_ruleAttributeBaseType= ruleAttributeBaseType EOF
            {
             newCompositeNode(grammarAccess.getAttributeBaseTypeRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleAttributeBaseType=ruleAttributeBaseType();

            state._fsp--;

             current =iv_ruleAttributeBaseType.getText(); 
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
    // $ANTLR end "entryRuleAttributeBaseType"


    // $ANTLR start "ruleAttributeBaseType"
    // InternalOseeDsl.g:1125:1: ruleAttributeBaseType returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'BooleanAttribute' | kw= 'CompressedContentAttribute' | kw= 'DateAttribute' | kw= 'EnumeratedAttribute' | kw= 'FloatingPointAttribute' | kw= 'IntegerAttribute' | kw= 'LongAttribute' | kw= 'JavaObjectAttribute' | kw= 'StringAttribute' | kw= 'ArtifactReferenceAttribute' | kw= 'BranchReferenceAttribute' | kw= 'WordAttribute' | kw= 'OutlineNumberAttribute' | this_QUALIFIED_NAME_13= ruleQUALIFIED_NAME ) ;
    public final AntlrDatatypeRuleToken ruleAttributeBaseType() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        AntlrDatatypeRuleToken this_QUALIFIED_NAME_13 = null;


         enterRule(); 
            
        try {
            // InternalOseeDsl.g:1128:28: ( (kw= 'BooleanAttribute' | kw= 'CompressedContentAttribute' | kw= 'DateAttribute' | kw= 'EnumeratedAttribute' | kw= 'FloatingPointAttribute' | kw= 'IntegerAttribute' | kw= 'LongAttribute' | kw= 'JavaObjectAttribute' | kw= 'StringAttribute' | kw= 'ArtifactReferenceAttribute' | kw= 'BranchReferenceAttribute' | kw= 'WordAttribute' | kw= 'OutlineNumberAttribute' | this_QUALIFIED_NAME_13= ruleQUALIFIED_NAME ) )
            // InternalOseeDsl.g:1129:1: (kw= 'BooleanAttribute' | kw= 'CompressedContentAttribute' | kw= 'DateAttribute' | kw= 'EnumeratedAttribute' | kw= 'FloatingPointAttribute' | kw= 'IntegerAttribute' | kw= 'LongAttribute' | kw= 'JavaObjectAttribute' | kw= 'StringAttribute' | kw= 'ArtifactReferenceAttribute' | kw= 'BranchReferenceAttribute' | kw= 'WordAttribute' | kw= 'OutlineNumberAttribute' | this_QUALIFIED_NAME_13= ruleQUALIFIED_NAME )
            {
            // InternalOseeDsl.g:1129:1: (kw= 'BooleanAttribute' | kw= 'CompressedContentAttribute' | kw= 'DateAttribute' | kw= 'EnumeratedAttribute' | kw= 'FloatingPointAttribute' | kw= 'IntegerAttribute' | kw= 'LongAttribute' | kw= 'JavaObjectAttribute' | kw= 'StringAttribute' | kw= 'ArtifactReferenceAttribute' | kw= 'BranchReferenceAttribute' | kw= 'WordAttribute' | kw= 'OutlineNumberAttribute' | this_QUALIFIED_NAME_13= ruleQUALIFIED_NAME )
            int alt18=14;
            switch ( input.LA(1) ) {
            case 39:
                {
                alt18=1;
                }
                break;
            case 40:
                {
                alt18=2;
                }
                break;
            case 41:
                {
                alt18=3;
                }
                break;
            case 42:
                {
                alt18=4;
                }
                break;
            case 43:
                {
                alt18=5;
                }
                break;
            case 44:
                {
                alt18=6;
                }
                break;
            case 45:
                {
                alt18=7;
                }
                break;
            case 46:
                {
                alt18=8;
                }
                break;
            case 47:
                {
                alt18=9;
                }
                break;
            case 48:
                {
                alt18=10;
                }
                break;
            case 49:
                {
                alt18=11;
                }
                break;
            case 50:
                {
                alt18=12;
                }
                break;
            case 51:
                {
                alt18=13;
                }
                break;
            case RULE_ID:
                {
                alt18=14;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 18, 0, input);

                throw nvae;
            }

            switch (alt18) {
                case 1 :
                    // InternalOseeDsl.g:1130:2: kw= 'BooleanAttribute'
                    {
                    kw=(Token)match(input,39,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getAttributeBaseTypeAccess().getBooleanAttributeKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // InternalOseeDsl.g:1137:2: kw= 'CompressedContentAttribute'
                    {
                    kw=(Token)match(input,40,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getAttributeBaseTypeAccess().getCompressedContentAttributeKeyword_1()); 
                        

                    }
                    break;
                case 3 :
                    // InternalOseeDsl.g:1144:2: kw= 'DateAttribute'
                    {
                    kw=(Token)match(input,41,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getAttributeBaseTypeAccess().getDateAttributeKeyword_2()); 
                        

                    }
                    break;
                case 4 :
                    // InternalOseeDsl.g:1151:2: kw= 'EnumeratedAttribute'
                    {
                    kw=(Token)match(input,42,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getAttributeBaseTypeAccess().getEnumeratedAttributeKeyword_3()); 
                        

                    }
                    break;
                case 5 :
                    // InternalOseeDsl.g:1158:2: kw= 'FloatingPointAttribute'
                    {
                    kw=(Token)match(input,43,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getAttributeBaseTypeAccess().getFloatingPointAttributeKeyword_4()); 
                        

                    }
                    break;
                case 6 :
                    // InternalOseeDsl.g:1165:2: kw= 'IntegerAttribute'
                    {
                    kw=(Token)match(input,44,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getAttributeBaseTypeAccess().getIntegerAttributeKeyword_5()); 
                        

                    }
                    break;
                case 7 :
                    // InternalOseeDsl.g:1172:2: kw= 'LongAttribute'
                    {
                    kw=(Token)match(input,45,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getAttributeBaseTypeAccess().getLongAttributeKeyword_6()); 
                        

                    }
                    break;
                case 8 :
                    // InternalOseeDsl.g:1179:2: kw= 'JavaObjectAttribute'
                    {
                    kw=(Token)match(input,46,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getAttributeBaseTypeAccess().getJavaObjectAttributeKeyword_7()); 
                        

                    }
                    break;
                case 9 :
                    // InternalOseeDsl.g:1186:2: kw= 'StringAttribute'
                    {
                    kw=(Token)match(input,47,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getAttributeBaseTypeAccess().getStringAttributeKeyword_8()); 
                        

                    }
                    break;
                case 10 :
                    // InternalOseeDsl.g:1193:2: kw= 'ArtifactReferenceAttribute'
                    {
                    kw=(Token)match(input,48,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getAttributeBaseTypeAccess().getArtifactReferenceAttributeKeyword_9()); 
                        

                    }
                    break;
                case 11 :
                    // InternalOseeDsl.g:1200:2: kw= 'BranchReferenceAttribute'
                    {
                    kw=(Token)match(input,49,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getAttributeBaseTypeAccess().getBranchReferenceAttributeKeyword_10()); 
                        

                    }
                    break;
                case 12 :
                    // InternalOseeDsl.g:1207:2: kw= 'WordAttribute'
                    {
                    kw=(Token)match(input,50,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getAttributeBaseTypeAccess().getWordAttributeKeyword_11()); 
                        

                    }
                    break;
                case 13 :
                    // InternalOseeDsl.g:1214:2: kw= 'OutlineNumberAttribute'
                    {
                    kw=(Token)match(input,51,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getAttributeBaseTypeAccess().getOutlineNumberAttributeKeyword_12()); 
                        

                    }
                    break;
                case 14 :
                    // InternalOseeDsl.g:1221:5: this_QUALIFIED_NAME_13= ruleQUALIFIED_NAME
                    {
                     
                            newCompositeNode(grammarAccess.getAttributeBaseTypeAccess().getQUALIFIED_NAMEParserRuleCall_13()); 
                        
                    pushFollow(FOLLOW_2);
                    this_QUALIFIED_NAME_13=ruleQUALIFIED_NAME();

                    state._fsp--;


                    		current.merge(this_QUALIFIED_NAME_13);
                        
                     
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
    // $ANTLR end "ruleAttributeBaseType"


    // $ANTLR start "entryRuleXOseeEnumType"
    // InternalOseeDsl.g:1239:1: entryRuleXOseeEnumType returns [EObject current=null] : iv_ruleXOseeEnumType= ruleXOseeEnumType EOF ;
    public final EObject entryRuleXOseeEnumType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleXOseeEnumType = null;


        try {
            // InternalOseeDsl.g:1240:2: (iv_ruleXOseeEnumType= ruleXOseeEnumType EOF )
            // InternalOseeDsl.g:1241:2: iv_ruleXOseeEnumType= ruleXOseeEnumType EOF
            {
             newCompositeNode(grammarAccess.getXOseeEnumTypeRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleXOseeEnumType=ruleXOseeEnumType();

            state._fsp--;

             current =iv_ruleXOseeEnumType; 
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
    // $ANTLR end "entryRuleXOseeEnumType"


    // $ANTLR start "ruleXOseeEnumType"
    // InternalOseeDsl.g:1248:1: ruleXOseeEnumType returns [EObject current=null] : (otherlv_0= 'oseeEnumType' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= '{' (otherlv_3= 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) )? otherlv_5= 'id' ( (lv_id_6_0= RULE_WHOLE_NUM_STR ) ) ( (lv_enumEntries_7_0= ruleXOseeEnumEntry ) )* otherlv_8= '}' ) ;
    public final EObject ruleXOseeEnumType() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_name_1_0=null;
        Token otherlv_2=null;
        Token otherlv_3=null;
        Token lv_typeGuid_4_0=null;
        Token otherlv_5=null;
        Token lv_id_6_0=null;
        Token otherlv_8=null;
        EObject lv_enumEntries_7_0 = null;


         enterRule(); 
            
        try {
            // InternalOseeDsl.g:1251:28: ( (otherlv_0= 'oseeEnumType' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= '{' (otherlv_3= 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) )? otherlv_5= 'id' ( (lv_id_6_0= RULE_WHOLE_NUM_STR ) ) ( (lv_enumEntries_7_0= ruleXOseeEnumEntry ) )* otherlv_8= '}' ) )
            // InternalOseeDsl.g:1252:1: (otherlv_0= 'oseeEnumType' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= '{' (otherlv_3= 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) )? otherlv_5= 'id' ( (lv_id_6_0= RULE_WHOLE_NUM_STR ) ) ( (lv_enumEntries_7_0= ruleXOseeEnumEntry ) )* otherlv_8= '}' )
            {
            // InternalOseeDsl.g:1252:1: (otherlv_0= 'oseeEnumType' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= '{' (otherlv_3= 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) )? otherlv_5= 'id' ( (lv_id_6_0= RULE_WHOLE_NUM_STR ) ) ( (lv_enumEntries_7_0= ruleXOseeEnumEntry ) )* otherlv_8= '}' )
            // InternalOseeDsl.g:1252:3: otherlv_0= 'oseeEnumType' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= '{' (otherlv_3= 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) )? otherlv_5= 'id' ( (lv_id_6_0= RULE_WHOLE_NUM_STR ) ) ( (lv_enumEntries_7_0= ruleXOseeEnumEntry ) )* otherlv_8= '}'
            {
            otherlv_0=(Token)match(input,52,FOLLOW_6); 

                	newLeafNode(otherlv_0, grammarAccess.getXOseeEnumTypeAccess().getOseeEnumTypeKeyword_0());
                
            // InternalOseeDsl.g:1256:1: ( (lv_name_1_0= RULE_STRING ) )
            // InternalOseeDsl.g:1257:1: (lv_name_1_0= RULE_STRING )
            {
            // InternalOseeDsl.g:1257:1: (lv_name_1_0= RULE_STRING )
            // InternalOseeDsl.g:1258:3: lv_name_1_0= RULE_STRING
            {
            lv_name_1_0=(Token)match(input,RULE_STRING,FOLLOW_20); 

            			newLeafNode(lv_name_1_0, grammarAccess.getXOseeEnumTypeAccess().getNameSTRINGTerminalRuleCall_1_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getXOseeEnumTypeRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"name",
                    		lv_name_1_0, 
                    		"org.eclipse.osee.framework.core.dsl.OseeDsl.STRING");
            	    

            }


            }

            otherlv_2=(Token)match(input,18,FOLLOW_12); 

                	newLeafNode(otherlv_2, grammarAccess.getXOseeEnumTypeAccess().getLeftCurlyBracketKeyword_2());
                
            // InternalOseeDsl.g:1278:1: (otherlv_3= 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==19) ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // InternalOseeDsl.g:1278:3: otherlv_3= 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) )
                    {
                    otherlv_3=(Token)match(input,19,FOLLOW_6); 

                        	newLeafNode(otherlv_3, grammarAccess.getXOseeEnumTypeAccess().getGuidKeyword_3_0());
                        
                    // InternalOseeDsl.g:1282:1: ( (lv_typeGuid_4_0= RULE_STRING ) )
                    // InternalOseeDsl.g:1283:1: (lv_typeGuid_4_0= RULE_STRING )
                    {
                    // InternalOseeDsl.g:1283:1: (lv_typeGuid_4_0= RULE_STRING )
                    // InternalOseeDsl.g:1284:3: lv_typeGuid_4_0= RULE_STRING
                    {
                    lv_typeGuid_4_0=(Token)match(input,RULE_STRING,FOLLOW_13); 

                    			newLeafNode(lv_typeGuid_4_0, grammarAccess.getXOseeEnumTypeAccess().getTypeGuidSTRINGTerminalRuleCall_3_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getXOseeEnumTypeRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"typeGuid",
                            		lv_typeGuid_4_0, 
                            		"org.eclipse.osee.framework.core.dsl.OseeDsl.STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            otherlv_5=(Token)match(input,20,FOLLOW_14); 

                	newLeafNode(otherlv_5, grammarAccess.getXOseeEnumTypeAccess().getIdKeyword_4());
                
            // InternalOseeDsl.g:1304:1: ( (lv_id_6_0= RULE_WHOLE_NUM_STR ) )
            // InternalOseeDsl.g:1305:1: (lv_id_6_0= RULE_WHOLE_NUM_STR )
            {
            // InternalOseeDsl.g:1305:1: (lv_id_6_0= RULE_WHOLE_NUM_STR )
            // InternalOseeDsl.g:1306:3: lv_id_6_0= RULE_WHOLE_NUM_STR
            {
            lv_id_6_0=(Token)match(input,RULE_WHOLE_NUM_STR,FOLLOW_28); 

            			newLeafNode(lv_id_6_0, grammarAccess.getXOseeEnumTypeAccess().getIdWHOLE_NUM_STRTerminalRuleCall_5_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getXOseeEnumTypeRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"id",
                    		lv_id_6_0, 
                    		"org.eclipse.osee.framework.core.dsl.OseeDsl.WHOLE_NUM_STR");
            	    

            }


            }

            // InternalOseeDsl.g:1322:2: ( (lv_enumEntries_7_0= ruleXOseeEnumEntry ) )*
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( (LA20_0==53) ) {
                    alt20=1;
                }


                switch (alt20) {
            	case 1 :
            	    // InternalOseeDsl.g:1323:1: (lv_enumEntries_7_0= ruleXOseeEnumEntry )
            	    {
            	    // InternalOseeDsl.g:1323:1: (lv_enumEntries_7_0= ruleXOseeEnumEntry )
            	    // InternalOseeDsl.g:1324:3: lv_enumEntries_7_0= ruleXOseeEnumEntry
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getXOseeEnumTypeAccess().getEnumEntriesXOseeEnumEntryParserRuleCall_6_0()); 
            	    	    
            	    pushFollow(FOLLOW_28);
            	    lv_enumEntries_7_0=ruleXOseeEnumEntry();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getXOseeEnumTypeRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"enumEntries",
            	            		lv_enumEntries_7_0, 
            	            		"org.eclipse.osee.framework.core.dsl.OseeDsl.XOseeEnumEntry");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    break loop20;
                }
            } while (true);

            otherlv_8=(Token)match(input,21,FOLLOW_2); 

                	newLeafNode(otherlv_8, grammarAccess.getXOseeEnumTypeAccess().getRightCurlyBracketKeyword_7());
                

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
    // $ANTLR end "ruleXOseeEnumType"


    // $ANTLR start "entryRuleXOseeEnumEntry"
    // InternalOseeDsl.g:1352:1: entryRuleXOseeEnumEntry returns [EObject current=null] : iv_ruleXOseeEnumEntry= ruleXOseeEnumEntry EOF ;
    public final EObject entryRuleXOseeEnumEntry() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleXOseeEnumEntry = null;


        try {
            // InternalOseeDsl.g:1353:2: (iv_ruleXOseeEnumEntry= ruleXOseeEnumEntry EOF )
            // InternalOseeDsl.g:1354:2: iv_ruleXOseeEnumEntry= ruleXOseeEnumEntry EOF
            {
             newCompositeNode(grammarAccess.getXOseeEnumEntryRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleXOseeEnumEntry=ruleXOseeEnumEntry();

            state._fsp--;

             current =iv_ruleXOseeEnumEntry; 
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
    // $ANTLR end "entryRuleXOseeEnumEntry"


    // $ANTLR start "ruleXOseeEnumEntry"
    // InternalOseeDsl.g:1361:1: ruleXOseeEnumEntry returns [EObject current=null] : (otherlv_0= 'entry' ( (lv_name_1_0= RULE_STRING ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? (otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? ) ;
    public final EObject ruleXOseeEnumEntry() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_name_1_0=null;
        Token lv_ordinal_2_0=null;
        Token otherlv_3=null;
        Token lv_entryGuid_4_0=null;
        Token otherlv_5=null;
        Token lv_description_6_0=null;

         enterRule(); 
            
        try {
            // InternalOseeDsl.g:1364:28: ( (otherlv_0= 'entry' ( (lv_name_1_0= RULE_STRING ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? (otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? ) )
            // InternalOseeDsl.g:1365:1: (otherlv_0= 'entry' ( (lv_name_1_0= RULE_STRING ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? (otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? )
            {
            // InternalOseeDsl.g:1365:1: (otherlv_0= 'entry' ( (lv_name_1_0= RULE_STRING ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? (otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? )
            // InternalOseeDsl.g:1365:3: otherlv_0= 'entry' ( (lv_name_1_0= RULE_STRING ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? (otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )?
            {
            otherlv_0=(Token)match(input,53,FOLLOW_6); 

                	newLeafNode(otherlv_0, grammarAccess.getXOseeEnumEntryAccess().getEntryKeyword_0());
                
            // InternalOseeDsl.g:1369:1: ( (lv_name_1_0= RULE_STRING ) )
            // InternalOseeDsl.g:1370:1: (lv_name_1_0= RULE_STRING )
            {
            // InternalOseeDsl.g:1370:1: (lv_name_1_0= RULE_STRING )
            // InternalOseeDsl.g:1371:3: lv_name_1_0= RULE_STRING
            {
            lv_name_1_0=(Token)match(input,RULE_STRING,FOLLOW_29); 

            			newLeafNode(lv_name_1_0, grammarAccess.getXOseeEnumEntryAccess().getNameSTRINGTerminalRuleCall_1_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getXOseeEnumEntryRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"name",
                    		lv_name_1_0, 
                    		"org.eclipse.osee.framework.core.dsl.OseeDsl.STRING");
            	    

            }


            }

            // InternalOseeDsl.g:1387:2: ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==RULE_WHOLE_NUM_STR) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // InternalOseeDsl.g:1388:1: (lv_ordinal_2_0= RULE_WHOLE_NUM_STR )
                    {
                    // InternalOseeDsl.g:1388:1: (lv_ordinal_2_0= RULE_WHOLE_NUM_STR )
                    // InternalOseeDsl.g:1389:3: lv_ordinal_2_0= RULE_WHOLE_NUM_STR
                    {
                    lv_ordinal_2_0=(Token)match(input,RULE_WHOLE_NUM_STR,FOLLOW_30); 

                    			newLeafNode(lv_ordinal_2_0, grammarAccess.getXOseeEnumEntryAccess().getOrdinalWHOLE_NUM_STRTerminalRuleCall_2_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getXOseeEnumEntryRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"ordinal",
                            		lv_ordinal_2_0, 
                            		"org.eclipse.osee.framework.core.dsl.OseeDsl.WHOLE_NUM_STR");
                    	    

                    }


                    }
                    break;

            }

            // InternalOseeDsl.g:1405:3: (otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )?
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0==54) ) {
                alt22=1;
            }
            switch (alt22) {
                case 1 :
                    // InternalOseeDsl.g:1405:5: otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) )
                    {
                    otherlv_3=(Token)match(input,54,FOLLOW_6); 

                        	newLeafNode(otherlv_3, grammarAccess.getXOseeEnumEntryAccess().getEntryGuidKeyword_3_0());
                        
                    // InternalOseeDsl.g:1409:1: ( (lv_entryGuid_4_0= RULE_STRING ) )
                    // InternalOseeDsl.g:1410:1: (lv_entryGuid_4_0= RULE_STRING )
                    {
                    // InternalOseeDsl.g:1410:1: (lv_entryGuid_4_0= RULE_STRING )
                    // InternalOseeDsl.g:1411:3: lv_entryGuid_4_0= RULE_STRING
                    {
                    lv_entryGuid_4_0=(Token)match(input,RULE_STRING,FOLLOW_31); 

                    			newLeafNode(lv_entryGuid_4_0, grammarAccess.getXOseeEnumEntryAccess().getEntryGuidSTRINGTerminalRuleCall_3_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getXOseeEnumEntryRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"entryGuid",
                            		lv_entryGuid_4_0, 
                            		"org.eclipse.osee.framework.core.dsl.OseeDsl.STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            // InternalOseeDsl.g:1427:4: (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==35) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // InternalOseeDsl.g:1427:6: otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) )
                    {
                    otherlv_5=(Token)match(input,35,FOLLOW_6); 

                        	newLeafNode(otherlv_5, grammarAccess.getXOseeEnumEntryAccess().getDescriptionKeyword_4_0());
                        
                    // InternalOseeDsl.g:1431:1: ( (lv_description_6_0= RULE_STRING ) )
                    // InternalOseeDsl.g:1432:1: (lv_description_6_0= RULE_STRING )
                    {
                    // InternalOseeDsl.g:1432:1: (lv_description_6_0= RULE_STRING )
                    // InternalOseeDsl.g:1433:3: lv_description_6_0= RULE_STRING
                    {
                    lv_description_6_0=(Token)match(input,RULE_STRING,FOLLOW_2); 

                    			newLeafNode(lv_description_6_0, grammarAccess.getXOseeEnumEntryAccess().getDescriptionSTRINGTerminalRuleCall_4_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getXOseeEnumEntryRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"description",
                            		lv_description_6_0, 
                            		"org.eclipse.osee.framework.core.dsl.OseeDsl.STRING");
                    	    

                    }


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
    // $ANTLR end "ruleXOseeEnumEntry"


    // $ANTLR start "entryRuleXOseeEnumOverride"
    // InternalOseeDsl.g:1457:1: entryRuleXOseeEnumOverride returns [EObject current=null] : iv_ruleXOseeEnumOverride= ruleXOseeEnumOverride EOF ;
    public final EObject entryRuleXOseeEnumOverride() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleXOseeEnumOverride = null;


        try {
            // InternalOseeDsl.g:1458:2: (iv_ruleXOseeEnumOverride= ruleXOseeEnumOverride EOF )
            // InternalOseeDsl.g:1459:2: iv_ruleXOseeEnumOverride= ruleXOseeEnumOverride EOF
            {
             newCompositeNode(grammarAccess.getXOseeEnumOverrideRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleXOseeEnumOverride=ruleXOseeEnumOverride();

            state._fsp--;

             current =iv_ruleXOseeEnumOverride; 
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
    // $ANTLR end "entryRuleXOseeEnumOverride"


    // $ANTLR start "ruleXOseeEnumOverride"
    // InternalOseeDsl.g:1466:1: ruleXOseeEnumOverride returns [EObject current=null] : (otherlv_0= 'overrides enum' ( (otherlv_1= RULE_STRING ) ) otherlv_2= '{' ( (lv_inheritAll_3_0= 'inheritAll' ) )? ( (lv_overrideOptions_4_0= ruleOverrideOption ) )* otherlv_5= '}' ) ;
    public final EObject ruleXOseeEnumOverride() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;
        Token otherlv_2=null;
        Token lv_inheritAll_3_0=null;
        Token otherlv_5=null;
        EObject lv_overrideOptions_4_0 = null;


         enterRule(); 
            
        try {
            // InternalOseeDsl.g:1469:28: ( (otherlv_0= 'overrides enum' ( (otherlv_1= RULE_STRING ) ) otherlv_2= '{' ( (lv_inheritAll_3_0= 'inheritAll' ) )? ( (lv_overrideOptions_4_0= ruleOverrideOption ) )* otherlv_5= '}' ) )
            // InternalOseeDsl.g:1470:1: (otherlv_0= 'overrides enum' ( (otherlv_1= RULE_STRING ) ) otherlv_2= '{' ( (lv_inheritAll_3_0= 'inheritAll' ) )? ( (lv_overrideOptions_4_0= ruleOverrideOption ) )* otherlv_5= '}' )
            {
            // InternalOseeDsl.g:1470:1: (otherlv_0= 'overrides enum' ( (otherlv_1= RULE_STRING ) ) otherlv_2= '{' ( (lv_inheritAll_3_0= 'inheritAll' ) )? ( (lv_overrideOptions_4_0= ruleOverrideOption ) )* otherlv_5= '}' )
            // InternalOseeDsl.g:1470:3: otherlv_0= 'overrides enum' ( (otherlv_1= RULE_STRING ) ) otherlv_2= '{' ( (lv_inheritAll_3_0= 'inheritAll' ) )? ( (lv_overrideOptions_4_0= ruleOverrideOption ) )* otherlv_5= '}'
            {
            otherlv_0=(Token)match(input,55,FOLLOW_6); 

                	newLeafNode(otherlv_0, grammarAccess.getXOseeEnumOverrideAccess().getOverridesEnumKeyword_0());
                
            // InternalOseeDsl.g:1474:1: ( (otherlv_1= RULE_STRING ) )
            // InternalOseeDsl.g:1475:1: (otherlv_1= RULE_STRING )
            {
            // InternalOseeDsl.g:1475:1: (otherlv_1= RULE_STRING )
            // InternalOseeDsl.g:1476:3: otherlv_1= RULE_STRING
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getXOseeEnumOverrideRule());
            	        }
                    
            otherlv_1=(Token)match(input,RULE_STRING,FOLLOW_20); 

            		newLeafNode(otherlv_1, grammarAccess.getXOseeEnumOverrideAccess().getOverridenEnumTypeXOseeEnumTypeCrossReference_1_0()); 
            	

            }


            }

            otherlv_2=(Token)match(input,18,FOLLOW_32); 

                	newLeafNode(otherlv_2, grammarAccess.getXOseeEnumOverrideAccess().getLeftCurlyBracketKeyword_2());
                
            // InternalOseeDsl.g:1491:1: ( (lv_inheritAll_3_0= 'inheritAll' ) )?
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==56) ) {
                alt24=1;
            }
            switch (alt24) {
                case 1 :
                    // InternalOseeDsl.g:1492:1: (lv_inheritAll_3_0= 'inheritAll' )
                    {
                    // InternalOseeDsl.g:1492:1: (lv_inheritAll_3_0= 'inheritAll' )
                    // InternalOseeDsl.g:1493:3: lv_inheritAll_3_0= 'inheritAll'
                    {
                    lv_inheritAll_3_0=(Token)match(input,56,FOLLOW_33); 

                            newLeafNode(lv_inheritAll_3_0, grammarAccess.getXOseeEnumOverrideAccess().getInheritAllInheritAllKeyword_3_0());
                        

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getXOseeEnumOverrideRule());
                    	        }
                           		setWithLastConsumed(current, "inheritAll", true, "inheritAll");
                    	    

                    }


                    }
                    break;

            }

            // InternalOseeDsl.g:1506:3: ( (lv_overrideOptions_4_0= ruleOverrideOption ) )*
            loop25:
            do {
                int alt25=2;
                int LA25_0 = input.LA(1);

                if ( ((LA25_0>=57 && LA25_0<=58)) ) {
                    alt25=1;
                }


                switch (alt25) {
            	case 1 :
            	    // InternalOseeDsl.g:1507:1: (lv_overrideOptions_4_0= ruleOverrideOption )
            	    {
            	    // InternalOseeDsl.g:1507:1: (lv_overrideOptions_4_0= ruleOverrideOption )
            	    // InternalOseeDsl.g:1508:3: lv_overrideOptions_4_0= ruleOverrideOption
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getXOseeEnumOverrideAccess().getOverrideOptionsOverrideOptionParserRuleCall_4_0()); 
            	    	    
            	    pushFollow(FOLLOW_33);
            	    lv_overrideOptions_4_0=ruleOverrideOption();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getXOseeEnumOverrideRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"overrideOptions",
            	            		lv_overrideOptions_4_0, 
            	            		"org.eclipse.osee.framework.core.dsl.OseeDsl.OverrideOption");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    break loop25;
                }
            } while (true);

            otherlv_5=(Token)match(input,21,FOLLOW_2); 

                	newLeafNode(otherlv_5, grammarAccess.getXOseeEnumOverrideAccess().getRightCurlyBracketKeyword_5());
                

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
    // $ANTLR end "ruleXOseeEnumOverride"


    // $ANTLR start "entryRuleOverrideOption"
    // InternalOseeDsl.g:1536:1: entryRuleOverrideOption returns [EObject current=null] : iv_ruleOverrideOption= ruleOverrideOption EOF ;
    public final EObject entryRuleOverrideOption() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOverrideOption = null;


        try {
            // InternalOseeDsl.g:1537:2: (iv_ruleOverrideOption= ruleOverrideOption EOF )
            // InternalOseeDsl.g:1538:2: iv_ruleOverrideOption= ruleOverrideOption EOF
            {
             newCompositeNode(grammarAccess.getOverrideOptionRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleOverrideOption=ruleOverrideOption();

            state._fsp--;

             current =iv_ruleOverrideOption; 
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
    // $ANTLR end "entryRuleOverrideOption"


    // $ANTLR start "ruleOverrideOption"
    // InternalOseeDsl.g:1545:1: ruleOverrideOption returns [EObject current=null] : (this_AddEnum_0= ruleAddEnum | this_RemoveEnum_1= ruleRemoveEnum ) ;
    public final EObject ruleOverrideOption() throws RecognitionException {
        EObject current = null;

        EObject this_AddEnum_0 = null;

        EObject this_RemoveEnum_1 = null;


         enterRule(); 
            
        try {
            // InternalOseeDsl.g:1548:28: ( (this_AddEnum_0= ruleAddEnum | this_RemoveEnum_1= ruleRemoveEnum ) )
            // InternalOseeDsl.g:1549:1: (this_AddEnum_0= ruleAddEnum | this_RemoveEnum_1= ruleRemoveEnum )
            {
            // InternalOseeDsl.g:1549:1: (this_AddEnum_0= ruleAddEnum | this_RemoveEnum_1= ruleRemoveEnum )
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==57) ) {
                alt26=1;
            }
            else if ( (LA26_0==58) ) {
                alt26=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 26, 0, input);

                throw nvae;
            }
            switch (alt26) {
                case 1 :
                    // InternalOseeDsl.g:1550:5: this_AddEnum_0= ruleAddEnum
                    {
                     
                            newCompositeNode(grammarAccess.getOverrideOptionAccess().getAddEnumParserRuleCall_0()); 
                        
                    pushFollow(FOLLOW_2);
                    this_AddEnum_0=ruleAddEnum();

                    state._fsp--;

                     
                            current = this_AddEnum_0; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // InternalOseeDsl.g:1560:5: this_RemoveEnum_1= ruleRemoveEnum
                    {
                     
                            newCompositeNode(grammarAccess.getOverrideOptionAccess().getRemoveEnumParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_2);
                    this_RemoveEnum_1=ruleRemoveEnum();

                    state._fsp--;

                     
                            current = this_RemoveEnum_1; 
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
    // $ANTLR end "ruleOverrideOption"


    // $ANTLR start "entryRuleAddEnum"
    // InternalOseeDsl.g:1576:1: entryRuleAddEnum returns [EObject current=null] : iv_ruleAddEnum= ruleAddEnum EOF ;
    public final EObject entryRuleAddEnum() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAddEnum = null;


        try {
            // InternalOseeDsl.g:1577:2: (iv_ruleAddEnum= ruleAddEnum EOF )
            // InternalOseeDsl.g:1578:2: iv_ruleAddEnum= ruleAddEnum EOF
            {
             newCompositeNode(grammarAccess.getAddEnumRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleAddEnum=ruleAddEnum();

            state._fsp--;

             current =iv_ruleAddEnum; 
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
    // $ANTLR end "entryRuleAddEnum"


    // $ANTLR start "ruleAddEnum"
    // InternalOseeDsl.g:1585:1: ruleAddEnum returns [EObject current=null] : (otherlv_0= 'add' ( (lv_enumEntry_1_0= RULE_STRING ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? (otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? ) ;
    public final EObject ruleAddEnum() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_enumEntry_1_0=null;
        Token lv_ordinal_2_0=null;
        Token otherlv_3=null;
        Token lv_entryGuid_4_0=null;
        Token otherlv_5=null;
        Token lv_description_6_0=null;

         enterRule(); 
            
        try {
            // InternalOseeDsl.g:1588:28: ( (otherlv_0= 'add' ( (lv_enumEntry_1_0= RULE_STRING ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? (otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? ) )
            // InternalOseeDsl.g:1589:1: (otherlv_0= 'add' ( (lv_enumEntry_1_0= RULE_STRING ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? (otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? )
            {
            // InternalOseeDsl.g:1589:1: (otherlv_0= 'add' ( (lv_enumEntry_1_0= RULE_STRING ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? (otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )? )
            // InternalOseeDsl.g:1589:3: otherlv_0= 'add' ( (lv_enumEntry_1_0= RULE_STRING ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? (otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )?
            {
            otherlv_0=(Token)match(input,57,FOLLOW_6); 

                	newLeafNode(otherlv_0, grammarAccess.getAddEnumAccess().getAddKeyword_0());
                
            // InternalOseeDsl.g:1593:1: ( (lv_enumEntry_1_0= RULE_STRING ) )
            // InternalOseeDsl.g:1594:1: (lv_enumEntry_1_0= RULE_STRING )
            {
            // InternalOseeDsl.g:1594:1: (lv_enumEntry_1_0= RULE_STRING )
            // InternalOseeDsl.g:1595:3: lv_enumEntry_1_0= RULE_STRING
            {
            lv_enumEntry_1_0=(Token)match(input,RULE_STRING,FOLLOW_29); 

            			newLeafNode(lv_enumEntry_1_0, grammarAccess.getAddEnumAccess().getEnumEntrySTRINGTerminalRuleCall_1_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getAddEnumRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"enumEntry",
                    		lv_enumEntry_1_0, 
                    		"org.eclipse.osee.framework.core.dsl.OseeDsl.STRING");
            	    

            }


            }

            // InternalOseeDsl.g:1611:2: ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )?
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==RULE_WHOLE_NUM_STR) ) {
                alt27=1;
            }
            switch (alt27) {
                case 1 :
                    // InternalOseeDsl.g:1612:1: (lv_ordinal_2_0= RULE_WHOLE_NUM_STR )
                    {
                    // InternalOseeDsl.g:1612:1: (lv_ordinal_2_0= RULE_WHOLE_NUM_STR )
                    // InternalOseeDsl.g:1613:3: lv_ordinal_2_0= RULE_WHOLE_NUM_STR
                    {
                    lv_ordinal_2_0=(Token)match(input,RULE_WHOLE_NUM_STR,FOLLOW_30); 

                    			newLeafNode(lv_ordinal_2_0, grammarAccess.getAddEnumAccess().getOrdinalWHOLE_NUM_STRTerminalRuleCall_2_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getAddEnumRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"ordinal",
                            		lv_ordinal_2_0, 
                            		"org.eclipse.osee.framework.core.dsl.OseeDsl.WHOLE_NUM_STR");
                    	    

                    }


                    }
                    break;

            }

            // InternalOseeDsl.g:1629:3: (otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )?
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==54) ) {
                alt28=1;
            }
            switch (alt28) {
                case 1 :
                    // InternalOseeDsl.g:1629:5: otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) )
                    {
                    otherlv_3=(Token)match(input,54,FOLLOW_6); 

                        	newLeafNode(otherlv_3, grammarAccess.getAddEnumAccess().getEntryGuidKeyword_3_0());
                        
                    // InternalOseeDsl.g:1633:1: ( (lv_entryGuid_4_0= RULE_STRING ) )
                    // InternalOseeDsl.g:1634:1: (lv_entryGuid_4_0= RULE_STRING )
                    {
                    // InternalOseeDsl.g:1634:1: (lv_entryGuid_4_0= RULE_STRING )
                    // InternalOseeDsl.g:1635:3: lv_entryGuid_4_0= RULE_STRING
                    {
                    lv_entryGuid_4_0=(Token)match(input,RULE_STRING,FOLLOW_31); 

                    			newLeafNode(lv_entryGuid_4_0, grammarAccess.getAddEnumAccess().getEntryGuidSTRINGTerminalRuleCall_3_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getAddEnumRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"entryGuid",
                            		lv_entryGuid_4_0, 
                            		"org.eclipse.osee.framework.core.dsl.OseeDsl.STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            // InternalOseeDsl.g:1651:4: (otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) ) )?
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==35) ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // InternalOseeDsl.g:1651:6: otherlv_5= 'description' ( (lv_description_6_0= RULE_STRING ) )
                    {
                    otherlv_5=(Token)match(input,35,FOLLOW_6); 

                        	newLeafNode(otherlv_5, grammarAccess.getAddEnumAccess().getDescriptionKeyword_4_0());
                        
                    // InternalOseeDsl.g:1655:1: ( (lv_description_6_0= RULE_STRING ) )
                    // InternalOseeDsl.g:1656:1: (lv_description_6_0= RULE_STRING )
                    {
                    // InternalOseeDsl.g:1656:1: (lv_description_6_0= RULE_STRING )
                    // InternalOseeDsl.g:1657:3: lv_description_6_0= RULE_STRING
                    {
                    lv_description_6_0=(Token)match(input,RULE_STRING,FOLLOW_2); 

                    			newLeafNode(lv_description_6_0, grammarAccess.getAddEnumAccess().getDescriptionSTRINGTerminalRuleCall_4_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getAddEnumRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"description",
                            		lv_description_6_0, 
                            		"org.eclipse.osee.framework.core.dsl.OseeDsl.STRING");
                    	    

                    }


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
    // $ANTLR end "ruleAddEnum"


    // $ANTLR start "entryRuleRemoveEnum"
    // InternalOseeDsl.g:1681:1: entryRuleRemoveEnum returns [EObject current=null] : iv_ruleRemoveEnum= ruleRemoveEnum EOF ;
    public final EObject entryRuleRemoveEnum() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRemoveEnum = null;


        try {
            // InternalOseeDsl.g:1682:2: (iv_ruleRemoveEnum= ruleRemoveEnum EOF )
            // InternalOseeDsl.g:1683:2: iv_ruleRemoveEnum= ruleRemoveEnum EOF
            {
             newCompositeNode(grammarAccess.getRemoveEnumRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleRemoveEnum=ruleRemoveEnum();

            state._fsp--;

             current =iv_ruleRemoveEnum; 
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
    // $ANTLR end "entryRuleRemoveEnum"


    // $ANTLR start "ruleRemoveEnum"
    // InternalOseeDsl.g:1690:1: ruleRemoveEnum returns [EObject current=null] : (otherlv_0= 'remove' ( (otherlv_1= RULE_STRING ) ) ) ;
    public final EObject ruleRemoveEnum() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;

         enterRule(); 
            
        try {
            // InternalOseeDsl.g:1693:28: ( (otherlv_0= 'remove' ( (otherlv_1= RULE_STRING ) ) ) )
            // InternalOseeDsl.g:1694:1: (otherlv_0= 'remove' ( (otherlv_1= RULE_STRING ) ) )
            {
            // InternalOseeDsl.g:1694:1: (otherlv_0= 'remove' ( (otherlv_1= RULE_STRING ) ) )
            // InternalOseeDsl.g:1694:3: otherlv_0= 'remove' ( (otherlv_1= RULE_STRING ) )
            {
            otherlv_0=(Token)match(input,58,FOLLOW_6); 

                	newLeafNode(otherlv_0, grammarAccess.getRemoveEnumAccess().getRemoveKeyword_0());
                
            // InternalOseeDsl.g:1698:1: ( (otherlv_1= RULE_STRING ) )
            // InternalOseeDsl.g:1699:1: (otherlv_1= RULE_STRING )
            {
            // InternalOseeDsl.g:1699:1: (otherlv_1= RULE_STRING )
            // InternalOseeDsl.g:1700:3: otherlv_1= RULE_STRING
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getRemoveEnumRule());
            	        }
                    
            otherlv_1=(Token)match(input,RULE_STRING,FOLLOW_2); 

            		newLeafNode(otherlv_1, grammarAccess.getRemoveEnumAccess().getEnumEntryXOseeEnumEntryCrossReference_1_0()); 
            	

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
    // $ANTLR end "ruleRemoveEnum"


    // $ANTLR start "entryRuleXOseeArtifactTypeOverride"
    // InternalOseeDsl.g:1719:1: entryRuleXOseeArtifactTypeOverride returns [EObject current=null] : iv_ruleXOseeArtifactTypeOverride= ruleXOseeArtifactTypeOverride EOF ;
    public final EObject entryRuleXOseeArtifactTypeOverride() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleXOseeArtifactTypeOverride = null;


        try {
            // InternalOseeDsl.g:1720:2: (iv_ruleXOseeArtifactTypeOverride= ruleXOseeArtifactTypeOverride EOF )
            // InternalOseeDsl.g:1721:2: iv_ruleXOseeArtifactTypeOverride= ruleXOseeArtifactTypeOverride EOF
            {
             newCompositeNode(grammarAccess.getXOseeArtifactTypeOverrideRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleXOseeArtifactTypeOverride=ruleXOseeArtifactTypeOverride();

            state._fsp--;

             current =iv_ruleXOseeArtifactTypeOverride; 
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
    // $ANTLR end "entryRuleXOseeArtifactTypeOverride"


    // $ANTLR start "ruleXOseeArtifactTypeOverride"
    // InternalOseeDsl.g:1728:1: ruleXOseeArtifactTypeOverride returns [EObject current=null] : (otherlv_0= 'overrides artifactType' ( (otherlv_1= RULE_STRING ) ) otherlv_2= '{' ( (lv_inheritAll_3_0= 'inheritAll' ) )? ( (lv_overrideOptions_4_0= ruleAttributeOverrideOption ) )+ otherlv_5= '}' ) ;
    public final EObject ruleXOseeArtifactTypeOverride() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;
        Token otherlv_2=null;
        Token lv_inheritAll_3_0=null;
        Token otherlv_5=null;
        EObject lv_overrideOptions_4_0 = null;


         enterRule(); 
            
        try {
            // InternalOseeDsl.g:1731:28: ( (otherlv_0= 'overrides artifactType' ( (otherlv_1= RULE_STRING ) ) otherlv_2= '{' ( (lv_inheritAll_3_0= 'inheritAll' ) )? ( (lv_overrideOptions_4_0= ruleAttributeOverrideOption ) )+ otherlv_5= '}' ) )
            // InternalOseeDsl.g:1732:1: (otherlv_0= 'overrides artifactType' ( (otherlv_1= RULE_STRING ) ) otherlv_2= '{' ( (lv_inheritAll_3_0= 'inheritAll' ) )? ( (lv_overrideOptions_4_0= ruleAttributeOverrideOption ) )+ otherlv_5= '}' )
            {
            // InternalOseeDsl.g:1732:1: (otherlv_0= 'overrides artifactType' ( (otherlv_1= RULE_STRING ) ) otherlv_2= '{' ( (lv_inheritAll_3_0= 'inheritAll' ) )? ( (lv_overrideOptions_4_0= ruleAttributeOverrideOption ) )+ otherlv_5= '}' )
            // InternalOseeDsl.g:1732:3: otherlv_0= 'overrides artifactType' ( (otherlv_1= RULE_STRING ) ) otherlv_2= '{' ( (lv_inheritAll_3_0= 'inheritAll' ) )? ( (lv_overrideOptions_4_0= ruleAttributeOverrideOption ) )+ otherlv_5= '}'
            {
            otherlv_0=(Token)match(input,59,FOLLOW_6); 

                	newLeafNode(otherlv_0, grammarAccess.getXOseeArtifactTypeOverrideAccess().getOverridesArtifactTypeKeyword_0());
                
            // InternalOseeDsl.g:1736:1: ( (otherlv_1= RULE_STRING ) )
            // InternalOseeDsl.g:1737:1: (otherlv_1= RULE_STRING )
            {
            // InternalOseeDsl.g:1737:1: (otherlv_1= RULE_STRING )
            // InternalOseeDsl.g:1738:3: otherlv_1= RULE_STRING
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getXOseeArtifactTypeOverrideRule());
            	        }
                    
            otherlv_1=(Token)match(input,RULE_STRING,FOLLOW_20); 

            		newLeafNode(otherlv_1, grammarAccess.getXOseeArtifactTypeOverrideAccess().getOverridenArtifactTypeXArtifactTypeCrossReference_1_0()); 
            	

            }


            }

            otherlv_2=(Token)match(input,18,FOLLOW_34); 

                	newLeafNode(otherlv_2, grammarAccess.getXOseeArtifactTypeOverrideAccess().getLeftCurlyBracketKeyword_2());
                
            // InternalOseeDsl.g:1753:1: ( (lv_inheritAll_3_0= 'inheritAll' ) )?
            int alt30=2;
            int LA30_0 = input.LA(1);

            if ( (LA30_0==56) ) {
                alt30=1;
            }
            switch (alt30) {
                case 1 :
                    // InternalOseeDsl.g:1754:1: (lv_inheritAll_3_0= 'inheritAll' )
                    {
                    // InternalOseeDsl.g:1754:1: (lv_inheritAll_3_0= 'inheritAll' )
                    // InternalOseeDsl.g:1755:3: lv_inheritAll_3_0= 'inheritAll'
                    {
                    lv_inheritAll_3_0=(Token)match(input,56,FOLLOW_34); 

                            newLeafNode(lv_inheritAll_3_0, grammarAccess.getXOseeArtifactTypeOverrideAccess().getInheritAllInheritAllKeyword_3_0());
                        

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getXOseeArtifactTypeOverrideRule());
                    	        }
                           		setWithLastConsumed(current, "inheritAll", true, "inheritAll");
                    	    

                    }


                    }
                    break;

            }

            // InternalOseeDsl.g:1768:3: ( (lv_overrideOptions_4_0= ruleAttributeOverrideOption ) )+
            int cnt31=0;
            loop31:
            do {
                int alt31=2;
                int LA31_0 = input.LA(1);

                if ( ((LA31_0>=57 && LA31_0<=58)||LA31_0==60) ) {
                    alt31=1;
                }


                switch (alt31) {
            	case 1 :
            	    // InternalOseeDsl.g:1769:1: (lv_overrideOptions_4_0= ruleAttributeOverrideOption )
            	    {
            	    // InternalOseeDsl.g:1769:1: (lv_overrideOptions_4_0= ruleAttributeOverrideOption )
            	    // InternalOseeDsl.g:1770:3: lv_overrideOptions_4_0= ruleAttributeOverrideOption
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getXOseeArtifactTypeOverrideAccess().getOverrideOptionsAttributeOverrideOptionParserRuleCall_4_0()); 
            	    	    
            	    pushFollow(FOLLOW_35);
            	    lv_overrideOptions_4_0=ruleAttributeOverrideOption();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getXOseeArtifactTypeOverrideRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"overrideOptions",
            	            		lv_overrideOptions_4_0, 
            	            		"org.eclipse.osee.framework.core.dsl.OseeDsl.AttributeOverrideOption");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt31 >= 1 ) break loop31;
                        EarlyExitException eee =
                            new EarlyExitException(31, input);
                        throw eee;
                }
                cnt31++;
            } while (true);

            otherlv_5=(Token)match(input,21,FOLLOW_2); 

                	newLeafNode(otherlv_5, grammarAccess.getXOseeArtifactTypeOverrideAccess().getRightCurlyBracketKeyword_5());
                

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
    // $ANTLR end "ruleXOseeArtifactTypeOverride"


    // $ANTLR start "entryRuleAttributeOverrideOption"
    // InternalOseeDsl.g:1798:1: entryRuleAttributeOverrideOption returns [EObject current=null] : iv_ruleAttributeOverrideOption= ruleAttributeOverrideOption EOF ;
    public final EObject entryRuleAttributeOverrideOption() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttributeOverrideOption = null;


        try {
            // InternalOseeDsl.g:1799:2: (iv_ruleAttributeOverrideOption= ruleAttributeOverrideOption EOF )
            // InternalOseeDsl.g:1800:2: iv_ruleAttributeOverrideOption= ruleAttributeOverrideOption EOF
            {
             newCompositeNode(grammarAccess.getAttributeOverrideOptionRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleAttributeOverrideOption=ruleAttributeOverrideOption();

            state._fsp--;

             current =iv_ruleAttributeOverrideOption; 
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
    // $ANTLR end "entryRuleAttributeOverrideOption"


    // $ANTLR start "ruleAttributeOverrideOption"
    // InternalOseeDsl.g:1807:1: ruleAttributeOverrideOption returns [EObject current=null] : (this_AddAttribute_0= ruleAddAttribute | this_RemoveAttribute_1= ruleRemoveAttribute | this_UpdateAttribute_2= ruleUpdateAttribute ) ;
    public final EObject ruleAttributeOverrideOption() throws RecognitionException {
        EObject current = null;

        EObject this_AddAttribute_0 = null;

        EObject this_RemoveAttribute_1 = null;

        EObject this_UpdateAttribute_2 = null;


         enterRule(); 
            
        try {
            // InternalOseeDsl.g:1810:28: ( (this_AddAttribute_0= ruleAddAttribute | this_RemoveAttribute_1= ruleRemoveAttribute | this_UpdateAttribute_2= ruleUpdateAttribute ) )
            // InternalOseeDsl.g:1811:1: (this_AddAttribute_0= ruleAddAttribute | this_RemoveAttribute_1= ruleRemoveAttribute | this_UpdateAttribute_2= ruleUpdateAttribute )
            {
            // InternalOseeDsl.g:1811:1: (this_AddAttribute_0= ruleAddAttribute | this_RemoveAttribute_1= ruleRemoveAttribute | this_UpdateAttribute_2= ruleUpdateAttribute )
            int alt32=3;
            switch ( input.LA(1) ) {
            case 57:
                {
                alt32=1;
                }
                break;
            case 58:
                {
                alt32=2;
                }
                break;
            case 60:
                {
                alt32=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 32, 0, input);

                throw nvae;
            }

            switch (alt32) {
                case 1 :
                    // InternalOseeDsl.g:1812:5: this_AddAttribute_0= ruleAddAttribute
                    {
                     
                            newCompositeNode(grammarAccess.getAttributeOverrideOptionAccess().getAddAttributeParserRuleCall_0()); 
                        
                    pushFollow(FOLLOW_2);
                    this_AddAttribute_0=ruleAddAttribute();

                    state._fsp--;

                     
                            current = this_AddAttribute_0; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // InternalOseeDsl.g:1822:5: this_RemoveAttribute_1= ruleRemoveAttribute
                    {
                     
                            newCompositeNode(grammarAccess.getAttributeOverrideOptionAccess().getRemoveAttributeParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_2);
                    this_RemoveAttribute_1=ruleRemoveAttribute();

                    state._fsp--;

                     
                            current = this_RemoveAttribute_1; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 3 :
                    // InternalOseeDsl.g:1832:5: this_UpdateAttribute_2= ruleUpdateAttribute
                    {
                     
                            newCompositeNode(grammarAccess.getAttributeOverrideOptionAccess().getUpdateAttributeParserRuleCall_2()); 
                        
                    pushFollow(FOLLOW_2);
                    this_UpdateAttribute_2=ruleUpdateAttribute();

                    state._fsp--;

                     
                            current = this_UpdateAttribute_2; 
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
    // $ANTLR end "ruleAttributeOverrideOption"


    // $ANTLR start "entryRuleAddAttribute"
    // InternalOseeDsl.g:1848:1: entryRuleAddAttribute returns [EObject current=null] : iv_ruleAddAttribute= ruleAddAttribute EOF ;
    public final EObject entryRuleAddAttribute() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAddAttribute = null;


        try {
            // InternalOseeDsl.g:1849:2: (iv_ruleAddAttribute= ruleAddAttribute EOF )
            // InternalOseeDsl.g:1850:2: iv_ruleAddAttribute= ruleAddAttribute EOF
            {
             newCompositeNode(grammarAccess.getAddAttributeRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleAddAttribute=ruleAddAttribute();

            state._fsp--;

             current =iv_ruleAddAttribute; 
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
    // $ANTLR end "entryRuleAddAttribute"


    // $ANTLR start "ruleAddAttribute"
    // InternalOseeDsl.g:1857:1: ruleAddAttribute returns [EObject current=null] : (otherlv_0= 'add' ( (lv_attribute_1_0= ruleXAttributeTypeRef ) ) ) ;
    public final EObject ruleAddAttribute() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        EObject lv_attribute_1_0 = null;


         enterRule(); 
            
        try {
            // InternalOseeDsl.g:1860:28: ( (otherlv_0= 'add' ( (lv_attribute_1_0= ruleXAttributeTypeRef ) ) ) )
            // InternalOseeDsl.g:1861:1: (otherlv_0= 'add' ( (lv_attribute_1_0= ruleXAttributeTypeRef ) ) )
            {
            // InternalOseeDsl.g:1861:1: (otherlv_0= 'add' ( (lv_attribute_1_0= ruleXAttributeTypeRef ) ) )
            // InternalOseeDsl.g:1861:3: otherlv_0= 'add' ( (lv_attribute_1_0= ruleXAttributeTypeRef ) )
            {
            otherlv_0=(Token)match(input,57,FOLLOW_36); 

                	newLeafNode(otherlv_0, grammarAccess.getAddAttributeAccess().getAddKeyword_0());
                
            // InternalOseeDsl.g:1865:1: ( (lv_attribute_1_0= ruleXAttributeTypeRef ) )
            // InternalOseeDsl.g:1866:1: (lv_attribute_1_0= ruleXAttributeTypeRef )
            {
            // InternalOseeDsl.g:1866:1: (lv_attribute_1_0= ruleXAttributeTypeRef )
            // InternalOseeDsl.g:1867:3: lv_attribute_1_0= ruleXAttributeTypeRef
            {
             
            	        newCompositeNode(grammarAccess.getAddAttributeAccess().getAttributeXAttributeTypeRefParserRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_2);
            lv_attribute_1_0=ruleXAttributeTypeRef();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getAddAttributeRule());
            	        }
                   		set(
                   			current, 
                   			"attribute",
                    		lv_attribute_1_0, 
                    		"org.eclipse.osee.framework.core.dsl.OseeDsl.XAttributeTypeRef");
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
    // $ANTLR end "ruleAddAttribute"


    // $ANTLR start "entryRuleRemoveAttribute"
    // InternalOseeDsl.g:1891:1: entryRuleRemoveAttribute returns [EObject current=null] : iv_ruleRemoveAttribute= ruleRemoveAttribute EOF ;
    public final EObject entryRuleRemoveAttribute() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRemoveAttribute = null;


        try {
            // InternalOseeDsl.g:1892:2: (iv_ruleRemoveAttribute= ruleRemoveAttribute EOF )
            // InternalOseeDsl.g:1893:2: iv_ruleRemoveAttribute= ruleRemoveAttribute EOF
            {
             newCompositeNode(grammarAccess.getRemoveAttributeRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleRemoveAttribute=ruleRemoveAttribute();

            state._fsp--;

             current =iv_ruleRemoveAttribute; 
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
    // $ANTLR end "entryRuleRemoveAttribute"


    // $ANTLR start "ruleRemoveAttribute"
    // InternalOseeDsl.g:1900:1: ruleRemoveAttribute returns [EObject current=null] : (otherlv_0= 'remove' otherlv_1= 'attribute' ( (otherlv_2= RULE_STRING ) ) ) ;
    public final EObject ruleRemoveAttribute() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;
        Token otherlv_2=null;

         enterRule(); 
            
        try {
            // InternalOseeDsl.g:1903:28: ( (otherlv_0= 'remove' otherlv_1= 'attribute' ( (otherlv_2= RULE_STRING ) ) ) )
            // InternalOseeDsl.g:1904:1: (otherlv_0= 'remove' otherlv_1= 'attribute' ( (otherlv_2= RULE_STRING ) ) )
            {
            // InternalOseeDsl.g:1904:1: (otherlv_0= 'remove' otherlv_1= 'attribute' ( (otherlv_2= RULE_STRING ) ) )
            // InternalOseeDsl.g:1904:3: otherlv_0= 'remove' otherlv_1= 'attribute' ( (otherlv_2= RULE_STRING ) )
            {
            otherlv_0=(Token)match(input,58,FOLLOW_36); 

                	newLeafNode(otherlv_0, grammarAccess.getRemoveAttributeAccess().getRemoveKeyword_0());
                
            otherlv_1=(Token)match(input,22,FOLLOW_6); 

                	newLeafNode(otherlv_1, grammarAccess.getRemoveAttributeAccess().getAttributeKeyword_1());
                
            // InternalOseeDsl.g:1912:1: ( (otherlv_2= RULE_STRING ) )
            // InternalOseeDsl.g:1913:1: (otherlv_2= RULE_STRING )
            {
            // InternalOseeDsl.g:1913:1: (otherlv_2= RULE_STRING )
            // InternalOseeDsl.g:1914:3: otherlv_2= RULE_STRING
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getRemoveAttributeRule());
            	        }
                    
            otherlv_2=(Token)match(input,RULE_STRING,FOLLOW_2); 

            		newLeafNode(otherlv_2, grammarAccess.getRemoveAttributeAccess().getAttributeXAttributeTypeCrossReference_2_0()); 
            	

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
    // $ANTLR end "ruleRemoveAttribute"


    // $ANTLR start "entryRuleUpdateAttribute"
    // InternalOseeDsl.g:1933:1: entryRuleUpdateAttribute returns [EObject current=null] : iv_ruleUpdateAttribute= ruleUpdateAttribute EOF ;
    public final EObject entryRuleUpdateAttribute() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleUpdateAttribute = null;


        try {
            // InternalOseeDsl.g:1934:2: (iv_ruleUpdateAttribute= ruleUpdateAttribute EOF )
            // InternalOseeDsl.g:1935:2: iv_ruleUpdateAttribute= ruleUpdateAttribute EOF
            {
             newCompositeNode(grammarAccess.getUpdateAttributeRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleUpdateAttribute=ruleUpdateAttribute();

            state._fsp--;

             current =iv_ruleUpdateAttribute; 
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
    // $ANTLR end "entryRuleUpdateAttribute"


    // $ANTLR start "ruleUpdateAttribute"
    // InternalOseeDsl.g:1942:1: ruleUpdateAttribute returns [EObject current=null] : (otherlv_0= 'update' ( (lv_attribute_1_0= ruleXAttributeTypeRef ) ) ) ;
    public final EObject ruleUpdateAttribute() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        EObject lv_attribute_1_0 = null;


         enterRule(); 
            
        try {
            // InternalOseeDsl.g:1945:28: ( (otherlv_0= 'update' ( (lv_attribute_1_0= ruleXAttributeTypeRef ) ) ) )
            // InternalOseeDsl.g:1946:1: (otherlv_0= 'update' ( (lv_attribute_1_0= ruleXAttributeTypeRef ) ) )
            {
            // InternalOseeDsl.g:1946:1: (otherlv_0= 'update' ( (lv_attribute_1_0= ruleXAttributeTypeRef ) ) )
            // InternalOseeDsl.g:1946:3: otherlv_0= 'update' ( (lv_attribute_1_0= ruleXAttributeTypeRef ) )
            {
            otherlv_0=(Token)match(input,60,FOLLOW_36); 

                	newLeafNode(otherlv_0, grammarAccess.getUpdateAttributeAccess().getUpdateKeyword_0());
                
            // InternalOseeDsl.g:1950:1: ( (lv_attribute_1_0= ruleXAttributeTypeRef ) )
            // InternalOseeDsl.g:1951:1: (lv_attribute_1_0= ruleXAttributeTypeRef )
            {
            // InternalOseeDsl.g:1951:1: (lv_attribute_1_0= ruleXAttributeTypeRef )
            // InternalOseeDsl.g:1952:3: lv_attribute_1_0= ruleXAttributeTypeRef
            {
             
            	        newCompositeNode(grammarAccess.getUpdateAttributeAccess().getAttributeXAttributeTypeRefParserRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_2);
            lv_attribute_1_0=ruleXAttributeTypeRef();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getUpdateAttributeRule());
            	        }
                   		set(
                   			current, 
                   			"attribute",
                    		lv_attribute_1_0, 
                    		"org.eclipse.osee.framework.core.dsl.OseeDsl.XAttributeTypeRef");
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
    // $ANTLR end "ruleUpdateAttribute"


    // $ANTLR start "entryRuleXRelationType"
    // InternalOseeDsl.g:1976:1: entryRuleXRelationType returns [EObject current=null] : iv_ruleXRelationType= ruleXRelationType EOF ;
    public final EObject entryRuleXRelationType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleXRelationType = null;


        try {
            // InternalOseeDsl.g:1977:2: (iv_ruleXRelationType= ruleXRelationType EOF )
            // InternalOseeDsl.g:1978:2: iv_ruleXRelationType= ruleXRelationType EOF
            {
             newCompositeNode(grammarAccess.getXRelationTypeRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleXRelationType=ruleXRelationType();

            state._fsp--;

             current =iv_ruleXRelationType; 
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
    // $ANTLR end "entryRuleXRelationType"


    // $ANTLR start "ruleXRelationType"
    // InternalOseeDsl.g:1985:1: ruleXRelationType returns [EObject current=null] : (otherlv_0= 'relationType' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= '{' (otherlv_3= 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) )? otherlv_5= 'id' ( (lv_id_6_0= RULE_WHOLE_NUM_STR ) ) otherlv_7= 'sideAName' ( (lv_sideAName_8_0= RULE_STRING ) ) otherlv_9= 'sideAArtifactType' ( (otherlv_10= RULE_STRING ) ) otherlv_11= 'sideBName' ( (lv_sideBName_12_0= RULE_STRING ) ) otherlv_13= 'sideBArtifactType' ( (otherlv_14= RULE_STRING ) ) otherlv_15= 'defaultOrderType' ( (lv_defaultOrderType_16_0= ruleRelationOrderType ) ) otherlv_17= 'multiplicity' ( (lv_multiplicity_18_0= ruleRelationMultiplicityEnum ) ) otherlv_19= '}' ) ;
    public final EObject ruleXRelationType() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_name_1_0=null;
        Token otherlv_2=null;
        Token otherlv_3=null;
        Token lv_typeGuid_4_0=null;
        Token otherlv_5=null;
        Token lv_id_6_0=null;
        Token otherlv_7=null;
        Token lv_sideAName_8_0=null;
        Token otherlv_9=null;
        Token otherlv_10=null;
        Token otherlv_11=null;
        Token lv_sideBName_12_0=null;
        Token otherlv_13=null;
        Token otherlv_14=null;
        Token otherlv_15=null;
        Token otherlv_17=null;
        Token otherlv_19=null;
        AntlrDatatypeRuleToken lv_defaultOrderType_16_0 = null;

        Enumerator lv_multiplicity_18_0 = null;


         enterRule(); 
            
        try {
            // InternalOseeDsl.g:1988:28: ( (otherlv_0= 'relationType' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= '{' (otherlv_3= 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) )? otherlv_5= 'id' ( (lv_id_6_0= RULE_WHOLE_NUM_STR ) ) otherlv_7= 'sideAName' ( (lv_sideAName_8_0= RULE_STRING ) ) otherlv_9= 'sideAArtifactType' ( (otherlv_10= RULE_STRING ) ) otherlv_11= 'sideBName' ( (lv_sideBName_12_0= RULE_STRING ) ) otherlv_13= 'sideBArtifactType' ( (otherlv_14= RULE_STRING ) ) otherlv_15= 'defaultOrderType' ( (lv_defaultOrderType_16_0= ruleRelationOrderType ) ) otherlv_17= 'multiplicity' ( (lv_multiplicity_18_0= ruleRelationMultiplicityEnum ) ) otherlv_19= '}' ) )
            // InternalOseeDsl.g:1989:1: (otherlv_0= 'relationType' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= '{' (otherlv_3= 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) )? otherlv_5= 'id' ( (lv_id_6_0= RULE_WHOLE_NUM_STR ) ) otherlv_7= 'sideAName' ( (lv_sideAName_8_0= RULE_STRING ) ) otherlv_9= 'sideAArtifactType' ( (otherlv_10= RULE_STRING ) ) otherlv_11= 'sideBName' ( (lv_sideBName_12_0= RULE_STRING ) ) otherlv_13= 'sideBArtifactType' ( (otherlv_14= RULE_STRING ) ) otherlv_15= 'defaultOrderType' ( (lv_defaultOrderType_16_0= ruleRelationOrderType ) ) otherlv_17= 'multiplicity' ( (lv_multiplicity_18_0= ruleRelationMultiplicityEnum ) ) otherlv_19= '}' )
            {
            // InternalOseeDsl.g:1989:1: (otherlv_0= 'relationType' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= '{' (otherlv_3= 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) )? otherlv_5= 'id' ( (lv_id_6_0= RULE_WHOLE_NUM_STR ) ) otherlv_7= 'sideAName' ( (lv_sideAName_8_0= RULE_STRING ) ) otherlv_9= 'sideAArtifactType' ( (otherlv_10= RULE_STRING ) ) otherlv_11= 'sideBName' ( (lv_sideBName_12_0= RULE_STRING ) ) otherlv_13= 'sideBArtifactType' ( (otherlv_14= RULE_STRING ) ) otherlv_15= 'defaultOrderType' ( (lv_defaultOrderType_16_0= ruleRelationOrderType ) ) otherlv_17= 'multiplicity' ( (lv_multiplicity_18_0= ruleRelationMultiplicityEnum ) ) otherlv_19= '}' )
            // InternalOseeDsl.g:1989:3: otherlv_0= 'relationType' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= '{' (otherlv_3= 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) )? otherlv_5= 'id' ( (lv_id_6_0= RULE_WHOLE_NUM_STR ) ) otherlv_7= 'sideAName' ( (lv_sideAName_8_0= RULE_STRING ) ) otherlv_9= 'sideAArtifactType' ( (otherlv_10= RULE_STRING ) ) otherlv_11= 'sideBName' ( (lv_sideBName_12_0= RULE_STRING ) ) otherlv_13= 'sideBArtifactType' ( (otherlv_14= RULE_STRING ) ) otherlv_15= 'defaultOrderType' ( (lv_defaultOrderType_16_0= ruleRelationOrderType ) ) otherlv_17= 'multiplicity' ( (lv_multiplicity_18_0= ruleRelationMultiplicityEnum ) ) otherlv_19= '}'
            {
            otherlv_0=(Token)match(input,61,FOLLOW_6); 

                	newLeafNode(otherlv_0, grammarAccess.getXRelationTypeAccess().getRelationTypeKeyword_0());
                
            // InternalOseeDsl.g:1993:1: ( (lv_name_1_0= RULE_STRING ) )
            // InternalOseeDsl.g:1994:1: (lv_name_1_0= RULE_STRING )
            {
            // InternalOseeDsl.g:1994:1: (lv_name_1_0= RULE_STRING )
            // InternalOseeDsl.g:1995:3: lv_name_1_0= RULE_STRING
            {
            lv_name_1_0=(Token)match(input,RULE_STRING,FOLLOW_20); 

            			newLeafNode(lv_name_1_0, grammarAccess.getXRelationTypeAccess().getNameSTRINGTerminalRuleCall_1_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getXRelationTypeRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"name",
                    		lv_name_1_0, 
                    		"org.eclipse.osee.framework.core.dsl.OseeDsl.STRING");
            	    

            }


            }

            otherlv_2=(Token)match(input,18,FOLLOW_12); 

                	newLeafNode(otherlv_2, grammarAccess.getXRelationTypeAccess().getLeftCurlyBracketKeyword_2());
                
            // InternalOseeDsl.g:2015:1: (otherlv_3= 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) )?
            int alt33=2;
            int LA33_0 = input.LA(1);

            if ( (LA33_0==19) ) {
                alt33=1;
            }
            switch (alt33) {
                case 1 :
                    // InternalOseeDsl.g:2015:3: otherlv_3= 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) )
                    {
                    otherlv_3=(Token)match(input,19,FOLLOW_6); 

                        	newLeafNode(otherlv_3, grammarAccess.getXRelationTypeAccess().getGuidKeyword_3_0());
                        
                    // InternalOseeDsl.g:2019:1: ( (lv_typeGuid_4_0= RULE_STRING ) )
                    // InternalOseeDsl.g:2020:1: (lv_typeGuid_4_0= RULE_STRING )
                    {
                    // InternalOseeDsl.g:2020:1: (lv_typeGuid_4_0= RULE_STRING )
                    // InternalOseeDsl.g:2021:3: lv_typeGuid_4_0= RULE_STRING
                    {
                    lv_typeGuid_4_0=(Token)match(input,RULE_STRING,FOLLOW_13); 

                    			newLeafNode(lv_typeGuid_4_0, grammarAccess.getXRelationTypeAccess().getTypeGuidSTRINGTerminalRuleCall_3_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getXRelationTypeRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"typeGuid",
                            		lv_typeGuid_4_0, 
                            		"org.eclipse.osee.framework.core.dsl.OseeDsl.STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            otherlv_5=(Token)match(input,20,FOLLOW_14); 

                	newLeafNode(otherlv_5, grammarAccess.getXRelationTypeAccess().getIdKeyword_4());
                
            // InternalOseeDsl.g:2041:1: ( (lv_id_6_0= RULE_WHOLE_NUM_STR ) )
            // InternalOseeDsl.g:2042:1: (lv_id_6_0= RULE_WHOLE_NUM_STR )
            {
            // InternalOseeDsl.g:2042:1: (lv_id_6_0= RULE_WHOLE_NUM_STR )
            // InternalOseeDsl.g:2043:3: lv_id_6_0= RULE_WHOLE_NUM_STR
            {
            lv_id_6_0=(Token)match(input,RULE_WHOLE_NUM_STR,FOLLOW_37); 

            			newLeafNode(lv_id_6_0, grammarAccess.getXRelationTypeAccess().getIdWHOLE_NUM_STRTerminalRuleCall_5_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getXRelationTypeRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"id",
                    		lv_id_6_0, 
                    		"org.eclipse.osee.framework.core.dsl.OseeDsl.WHOLE_NUM_STR");
            	    

            }


            }

            otherlv_7=(Token)match(input,62,FOLLOW_6); 

                	newLeafNode(otherlv_7, grammarAccess.getXRelationTypeAccess().getSideANameKeyword_6());
                
            // InternalOseeDsl.g:2063:1: ( (lv_sideAName_8_0= RULE_STRING ) )
            // InternalOseeDsl.g:2064:1: (lv_sideAName_8_0= RULE_STRING )
            {
            // InternalOseeDsl.g:2064:1: (lv_sideAName_8_0= RULE_STRING )
            // InternalOseeDsl.g:2065:3: lv_sideAName_8_0= RULE_STRING
            {
            lv_sideAName_8_0=(Token)match(input,RULE_STRING,FOLLOW_38); 

            			newLeafNode(lv_sideAName_8_0, grammarAccess.getXRelationTypeAccess().getSideANameSTRINGTerminalRuleCall_7_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getXRelationTypeRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"sideAName",
                    		lv_sideAName_8_0, 
                    		"org.eclipse.osee.framework.core.dsl.OseeDsl.STRING");
            	    

            }


            }

            otherlv_9=(Token)match(input,63,FOLLOW_6); 

                	newLeafNode(otherlv_9, grammarAccess.getXRelationTypeAccess().getSideAArtifactTypeKeyword_8());
                
            // InternalOseeDsl.g:2085:1: ( (otherlv_10= RULE_STRING ) )
            // InternalOseeDsl.g:2086:1: (otherlv_10= RULE_STRING )
            {
            // InternalOseeDsl.g:2086:1: (otherlv_10= RULE_STRING )
            // InternalOseeDsl.g:2087:3: otherlv_10= RULE_STRING
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getXRelationTypeRule());
            	        }
                    
            otherlv_10=(Token)match(input,RULE_STRING,FOLLOW_39); 

            		newLeafNode(otherlv_10, grammarAccess.getXRelationTypeAccess().getSideAArtifactTypeXArtifactTypeCrossReference_9_0()); 
            	

            }


            }

            otherlv_11=(Token)match(input,64,FOLLOW_6); 

                	newLeafNode(otherlv_11, grammarAccess.getXRelationTypeAccess().getSideBNameKeyword_10());
                
            // InternalOseeDsl.g:2102:1: ( (lv_sideBName_12_0= RULE_STRING ) )
            // InternalOseeDsl.g:2103:1: (lv_sideBName_12_0= RULE_STRING )
            {
            // InternalOseeDsl.g:2103:1: (lv_sideBName_12_0= RULE_STRING )
            // InternalOseeDsl.g:2104:3: lv_sideBName_12_0= RULE_STRING
            {
            lv_sideBName_12_0=(Token)match(input,RULE_STRING,FOLLOW_40); 

            			newLeafNode(lv_sideBName_12_0, grammarAccess.getXRelationTypeAccess().getSideBNameSTRINGTerminalRuleCall_11_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getXRelationTypeRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"sideBName",
                    		lv_sideBName_12_0, 
                    		"org.eclipse.osee.framework.core.dsl.OseeDsl.STRING");
            	    

            }


            }

            otherlv_13=(Token)match(input,65,FOLLOW_6); 

                	newLeafNode(otherlv_13, grammarAccess.getXRelationTypeAccess().getSideBArtifactTypeKeyword_12());
                
            // InternalOseeDsl.g:2124:1: ( (otherlv_14= RULE_STRING ) )
            // InternalOseeDsl.g:2125:1: (otherlv_14= RULE_STRING )
            {
            // InternalOseeDsl.g:2125:1: (otherlv_14= RULE_STRING )
            // InternalOseeDsl.g:2126:3: otherlv_14= RULE_STRING
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getXRelationTypeRule());
            	        }
                    
            otherlv_14=(Token)match(input,RULE_STRING,FOLLOW_41); 

            		newLeafNode(otherlv_14, grammarAccess.getXRelationTypeAccess().getSideBArtifactTypeXArtifactTypeCrossReference_13_0()); 
            	

            }


            }

            otherlv_15=(Token)match(input,66,FOLLOW_42); 

                	newLeafNode(otherlv_15, grammarAccess.getXRelationTypeAccess().getDefaultOrderTypeKeyword_14());
                
            // InternalOseeDsl.g:2141:1: ( (lv_defaultOrderType_16_0= ruleRelationOrderType ) )
            // InternalOseeDsl.g:2142:1: (lv_defaultOrderType_16_0= ruleRelationOrderType )
            {
            // InternalOseeDsl.g:2142:1: (lv_defaultOrderType_16_0= ruleRelationOrderType )
            // InternalOseeDsl.g:2143:3: lv_defaultOrderType_16_0= ruleRelationOrderType
            {
             
            	        newCompositeNode(grammarAccess.getXRelationTypeAccess().getDefaultOrderTypeRelationOrderTypeParserRuleCall_15_0()); 
            	    
            pushFollow(FOLLOW_43);
            lv_defaultOrderType_16_0=ruleRelationOrderType();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getXRelationTypeRule());
            	        }
                   		set(
                   			current, 
                   			"defaultOrderType",
                    		lv_defaultOrderType_16_0, 
                    		"org.eclipse.osee.framework.core.dsl.OseeDsl.RelationOrderType");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            otherlv_17=(Token)match(input,67,FOLLOW_44); 

                	newLeafNode(otherlv_17, grammarAccess.getXRelationTypeAccess().getMultiplicityKeyword_16());
                
            // InternalOseeDsl.g:2163:1: ( (lv_multiplicity_18_0= ruleRelationMultiplicityEnum ) )
            // InternalOseeDsl.g:2164:1: (lv_multiplicity_18_0= ruleRelationMultiplicityEnum )
            {
            // InternalOseeDsl.g:2164:1: (lv_multiplicity_18_0= ruleRelationMultiplicityEnum )
            // InternalOseeDsl.g:2165:3: lv_multiplicity_18_0= ruleRelationMultiplicityEnum
            {
             
            	        newCompositeNode(grammarAccess.getXRelationTypeAccess().getMultiplicityRelationMultiplicityEnumEnumRuleCall_17_0()); 
            	    
            pushFollow(FOLLOW_45);
            lv_multiplicity_18_0=ruleRelationMultiplicityEnum();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getXRelationTypeRule());
            	        }
                   		set(
                   			current, 
                   			"multiplicity",
                    		lv_multiplicity_18_0, 
                    		"org.eclipse.osee.framework.core.dsl.OseeDsl.RelationMultiplicityEnum");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            otherlv_19=(Token)match(input,21,FOLLOW_2); 

                	newLeafNode(otherlv_19, grammarAccess.getXRelationTypeAccess().getRightCurlyBracketKeyword_18());
                

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
    // $ANTLR end "ruleXRelationType"


    // $ANTLR start "entryRuleRelationOrderType"
    // InternalOseeDsl.g:2193:1: entryRuleRelationOrderType returns [String current=null] : iv_ruleRelationOrderType= ruleRelationOrderType EOF ;
    public final String entryRuleRelationOrderType() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleRelationOrderType = null;


        try {
            // InternalOseeDsl.g:2194:2: (iv_ruleRelationOrderType= ruleRelationOrderType EOF )
            // InternalOseeDsl.g:2195:2: iv_ruleRelationOrderType= ruleRelationOrderType EOF
            {
             newCompositeNode(grammarAccess.getRelationOrderTypeRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleRelationOrderType=ruleRelationOrderType();

            state._fsp--;

             current =iv_ruleRelationOrderType.getText(); 
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
    // $ANTLR end "entryRuleRelationOrderType"


    // $ANTLR start "ruleRelationOrderType"
    // InternalOseeDsl.g:2202:1: ruleRelationOrderType returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'Lexicographical_Ascending' | kw= 'Lexicographical_Descending' | kw= 'Unordered' | this_ID_3= RULE_ID ) ;
    public final AntlrDatatypeRuleToken ruleRelationOrderType() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        Token this_ID_3=null;

         enterRule(); 
            
        try {
            // InternalOseeDsl.g:2205:28: ( (kw= 'Lexicographical_Ascending' | kw= 'Lexicographical_Descending' | kw= 'Unordered' | this_ID_3= RULE_ID ) )
            // InternalOseeDsl.g:2206:1: (kw= 'Lexicographical_Ascending' | kw= 'Lexicographical_Descending' | kw= 'Unordered' | this_ID_3= RULE_ID )
            {
            // InternalOseeDsl.g:2206:1: (kw= 'Lexicographical_Ascending' | kw= 'Lexicographical_Descending' | kw= 'Unordered' | this_ID_3= RULE_ID )
            int alt34=4;
            switch ( input.LA(1) ) {
            case 68:
                {
                alt34=1;
                }
                break;
            case 69:
                {
                alt34=2;
                }
                break;
            case 70:
                {
                alt34=3;
                }
                break;
            case RULE_ID:
                {
                alt34=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 34, 0, input);

                throw nvae;
            }

            switch (alt34) {
                case 1 :
                    // InternalOseeDsl.g:2207:2: kw= 'Lexicographical_Ascending'
                    {
                    kw=(Token)match(input,68,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRelationOrderTypeAccess().getLexicographical_AscendingKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // InternalOseeDsl.g:2214:2: kw= 'Lexicographical_Descending'
                    {
                    kw=(Token)match(input,69,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRelationOrderTypeAccess().getLexicographical_DescendingKeyword_1()); 
                        

                    }
                    break;
                case 3 :
                    // InternalOseeDsl.g:2221:2: kw= 'Unordered'
                    {
                    kw=(Token)match(input,70,FOLLOW_2); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRelationOrderTypeAccess().getUnorderedKeyword_2()); 
                        

                    }
                    break;
                case 4 :
                    // InternalOseeDsl.g:2227:10: this_ID_3= RULE_ID
                    {
                    this_ID_3=(Token)match(input,RULE_ID,FOLLOW_2); 

                    		current.merge(this_ID_3);
                        
                     
                        newLeafNode(this_ID_3, grammarAccess.getRelationOrderTypeAccess().getIDTerminalRuleCall_3()); 
                        

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
    // $ANTLR end "ruleRelationOrderType"


    // $ANTLR start "entryRuleCondition"
    // InternalOseeDsl.g:2244:1: entryRuleCondition returns [EObject current=null] : iv_ruleCondition= ruleCondition EOF ;
    public final EObject entryRuleCondition() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleCondition = null;


        try {
            // InternalOseeDsl.g:2245:2: (iv_ruleCondition= ruleCondition EOF )
            // InternalOseeDsl.g:2246:2: iv_ruleCondition= ruleCondition EOF
            {
             newCompositeNode(grammarAccess.getConditionRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleCondition=ruleCondition();

            state._fsp--;

             current =iv_ruleCondition; 
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
    // $ANTLR end "entryRuleCondition"


    // $ANTLR start "ruleCondition"
    // InternalOseeDsl.g:2253:1: ruleCondition returns [EObject current=null] : (this_SimpleCondition_0= ruleSimpleCondition | this_CompoundCondition_1= ruleCompoundCondition ) ;
    public final EObject ruleCondition() throws RecognitionException {
        EObject current = null;

        EObject this_SimpleCondition_0 = null;

        EObject this_CompoundCondition_1 = null;


         enterRule(); 
            
        try {
            // InternalOseeDsl.g:2256:28: ( (this_SimpleCondition_0= ruleSimpleCondition | this_CompoundCondition_1= ruleCompoundCondition ) )
            // InternalOseeDsl.g:2257:1: (this_SimpleCondition_0= ruleSimpleCondition | this_CompoundCondition_1= ruleCompoundCondition )
            {
            // InternalOseeDsl.g:2257:1: (this_SimpleCondition_0= ruleSimpleCondition | this_CompoundCondition_1= ruleCompoundCondition )
            int alt35=2;
            int LA35_0 = input.LA(1);

            if ( (LA35_0==23||(LA35_0>=90 && LA35_0<=92)) ) {
                alt35=1;
            }
            else if ( (LA35_0==71) ) {
                alt35=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 35, 0, input);

                throw nvae;
            }
            switch (alt35) {
                case 1 :
                    // InternalOseeDsl.g:2258:5: this_SimpleCondition_0= ruleSimpleCondition
                    {
                     
                            newCompositeNode(grammarAccess.getConditionAccess().getSimpleConditionParserRuleCall_0()); 
                        
                    pushFollow(FOLLOW_2);
                    this_SimpleCondition_0=ruleSimpleCondition();

                    state._fsp--;

                     
                            current = this_SimpleCondition_0; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // InternalOseeDsl.g:2268:5: this_CompoundCondition_1= ruleCompoundCondition
                    {
                     
                            newCompositeNode(grammarAccess.getConditionAccess().getCompoundConditionParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_2);
                    this_CompoundCondition_1=ruleCompoundCondition();

                    state._fsp--;

                     
                            current = this_CompoundCondition_1; 
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
    // $ANTLR end "ruleCondition"


    // $ANTLR start "entryRuleSimpleCondition"
    // InternalOseeDsl.g:2284:1: entryRuleSimpleCondition returns [EObject current=null] : iv_ruleSimpleCondition= ruleSimpleCondition EOF ;
    public final EObject entryRuleSimpleCondition() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleSimpleCondition = null;


        try {
            // InternalOseeDsl.g:2285:2: (iv_ruleSimpleCondition= ruleSimpleCondition EOF )
            // InternalOseeDsl.g:2286:2: iv_ruleSimpleCondition= ruleSimpleCondition EOF
            {
             newCompositeNode(grammarAccess.getSimpleConditionRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleSimpleCondition=ruleSimpleCondition();

            state._fsp--;

             current =iv_ruleSimpleCondition; 
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
    // $ANTLR end "entryRuleSimpleCondition"


    // $ANTLR start "ruleSimpleCondition"
    // InternalOseeDsl.g:2293:1: ruleSimpleCondition returns [EObject current=null] : ( ( (lv_field_0_0= ruleMatchField ) ) ( (lv_op_1_0= ruleCompareOp ) ) ( (lv_expression_2_0= RULE_STRING ) ) ) ;
    public final EObject ruleSimpleCondition() throws RecognitionException {
        EObject current = null;

        Token lv_expression_2_0=null;
        Enumerator lv_field_0_0 = null;

        Enumerator lv_op_1_0 = null;


         enterRule(); 
            
        try {
            // InternalOseeDsl.g:2296:28: ( ( ( (lv_field_0_0= ruleMatchField ) ) ( (lv_op_1_0= ruleCompareOp ) ) ( (lv_expression_2_0= RULE_STRING ) ) ) )
            // InternalOseeDsl.g:2297:1: ( ( (lv_field_0_0= ruleMatchField ) ) ( (lv_op_1_0= ruleCompareOp ) ) ( (lv_expression_2_0= RULE_STRING ) ) )
            {
            // InternalOseeDsl.g:2297:1: ( ( (lv_field_0_0= ruleMatchField ) ) ( (lv_op_1_0= ruleCompareOp ) ) ( (lv_expression_2_0= RULE_STRING ) ) )
            // InternalOseeDsl.g:2297:2: ( (lv_field_0_0= ruleMatchField ) ) ( (lv_op_1_0= ruleCompareOp ) ) ( (lv_expression_2_0= RULE_STRING ) )
            {
            // InternalOseeDsl.g:2297:2: ( (lv_field_0_0= ruleMatchField ) )
            // InternalOseeDsl.g:2298:1: (lv_field_0_0= ruleMatchField )
            {
            // InternalOseeDsl.g:2298:1: (lv_field_0_0= ruleMatchField )
            // InternalOseeDsl.g:2299:3: lv_field_0_0= ruleMatchField
            {
             
            	        newCompositeNode(grammarAccess.getSimpleConditionAccess().getFieldMatchFieldEnumRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_46);
            lv_field_0_0=ruleMatchField();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getSimpleConditionRule());
            	        }
                   		set(
                   			current, 
                   			"field",
                    		lv_field_0_0, 
                    		"org.eclipse.osee.framework.core.dsl.OseeDsl.MatchField");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // InternalOseeDsl.g:2315:2: ( (lv_op_1_0= ruleCompareOp ) )
            // InternalOseeDsl.g:2316:1: (lv_op_1_0= ruleCompareOp )
            {
            // InternalOseeDsl.g:2316:1: (lv_op_1_0= ruleCompareOp )
            // InternalOseeDsl.g:2317:3: lv_op_1_0= ruleCompareOp
            {
             
            	        newCompositeNode(grammarAccess.getSimpleConditionAccess().getOpCompareOpEnumRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_6);
            lv_op_1_0=ruleCompareOp();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getSimpleConditionRule());
            	        }
                   		set(
                   			current, 
                   			"op",
                    		lv_op_1_0, 
                    		"org.eclipse.osee.framework.core.dsl.OseeDsl.CompareOp");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // InternalOseeDsl.g:2333:2: ( (lv_expression_2_0= RULE_STRING ) )
            // InternalOseeDsl.g:2334:1: (lv_expression_2_0= RULE_STRING )
            {
            // InternalOseeDsl.g:2334:1: (lv_expression_2_0= RULE_STRING )
            // InternalOseeDsl.g:2335:3: lv_expression_2_0= RULE_STRING
            {
            lv_expression_2_0=(Token)match(input,RULE_STRING,FOLLOW_2); 

            			newLeafNode(lv_expression_2_0, grammarAccess.getSimpleConditionAccess().getExpressionSTRINGTerminalRuleCall_2_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getSimpleConditionRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"expression",
                    		lv_expression_2_0, 
                    		"org.eclipse.osee.framework.core.dsl.OseeDsl.STRING");
            	    

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
    // $ANTLR end "ruleSimpleCondition"


    // $ANTLR start "entryRuleCompoundCondition"
    // InternalOseeDsl.g:2359:1: entryRuleCompoundCondition returns [EObject current=null] : iv_ruleCompoundCondition= ruleCompoundCondition EOF ;
    public final EObject entryRuleCompoundCondition() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleCompoundCondition = null;


        try {
            // InternalOseeDsl.g:2360:2: (iv_ruleCompoundCondition= ruleCompoundCondition EOF )
            // InternalOseeDsl.g:2361:2: iv_ruleCompoundCondition= ruleCompoundCondition EOF
            {
             newCompositeNode(grammarAccess.getCompoundConditionRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleCompoundCondition=ruleCompoundCondition();

            state._fsp--;

             current =iv_ruleCompoundCondition; 
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
    // $ANTLR end "entryRuleCompoundCondition"


    // $ANTLR start "ruleCompoundCondition"
    // InternalOseeDsl.g:2368:1: ruleCompoundCondition returns [EObject current=null] : (otherlv_0= '(' ( (lv_conditions_1_0= ruleSimpleCondition ) ) ( ( (lv_operators_2_0= ruleXLogicOperator ) ) ( (lv_conditions_3_0= ruleSimpleCondition ) ) )+ otherlv_4= ')' ) ;
    public final EObject ruleCompoundCondition() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_4=null;
        EObject lv_conditions_1_0 = null;

        Enumerator lv_operators_2_0 = null;

        EObject lv_conditions_3_0 = null;


         enterRule(); 
            
        try {
            // InternalOseeDsl.g:2371:28: ( (otherlv_0= '(' ( (lv_conditions_1_0= ruleSimpleCondition ) ) ( ( (lv_operators_2_0= ruleXLogicOperator ) ) ( (lv_conditions_3_0= ruleSimpleCondition ) ) )+ otherlv_4= ')' ) )
            // InternalOseeDsl.g:2372:1: (otherlv_0= '(' ( (lv_conditions_1_0= ruleSimpleCondition ) ) ( ( (lv_operators_2_0= ruleXLogicOperator ) ) ( (lv_conditions_3_0= ruleSimpleCondition ) ) )+ otherlv_4= ')' )
            {
            // InternalOseeDsl.g:2372:1: (otherlv_0= '(' ( (lv_conditions_1_0= ruleSimpleCondition ) ) ( ( (lv_operators_2_0= ruleXLogicOperator ) ) ( (lv_conditions_3_0= ruleSimpleCondition ) ) )+ otherlv_4= ')' )
            // InternalOseeDsl.g:2372:3: otherlv_0= '(' ( (lv_conditions_1_0= ruleSimpleCondition ) ) ( ( (lv_operators_2_0= ruleXLogicOperator ) ) ( (lv_conditions_3_0= ruleSimpleCondition ) ) )+ otherlv_4= ')'
            {
            otherlv_0=(Token)match(input,71,FOLLOW_47); 

                	newLeafNode(otherlv_0, grammarAccess.getCompoundConditionAccess().getLeftParenthesisKeyword_0());
                
            // InternalOseeDsl.g:2376:1: ( (lv_conditions_1_0= ruleSimpleCondition ) )
            // InternalOseeDsl.g:2377:1: (lv_conditions_1_0= ruleSimpleCondition )
            {
            // InternalOseeDsl.g:2377:1: (lv_conditions_1_0= ruleSimpleCondition )
            // InternalOseeDsl.g:2378:3: lv_conditions_1_0= ruleSimpleCondition
            {
             
            	        newCompositeNode(grammarAccess.getCompoundConditionAccess().getConditionsSimpleConditionParserRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_48);
            lv_conditions_1_0=ruleSimpleCondition();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getCompoundConditionRule());
            	        }
                   		add(
                   			current, 
                   			"conditions",
                    		lv_conditions_1_0, 
                    		"org.eclipse.osee.framework.core.dsl.OseeDsl.SimpleCondition");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // InternalOseeDsl.g:2394:2: ( ( (lv_operators_2_0= ruleXLogicOperator ) ) ( (lv_conditions_3_0= ruleSimpleCondition ) ) )+
            int cnt36=0;
            loop36:
            do {
                int alt36=2;
                int LA36_0 = input.LA(1);

                if ( ((LA36_0>=88 && LA36_0<=89)) ) {
                    alt36=1;
                }


                switch (alt36) {
            	case 1 :
            	    // InternalOseeDsl.g:2394:3: ( (lv_operators_2_0= ruleXLogicOperator ) ) ( (lv_conditions_3_0= ruleSimpleCondition ) )
            	    {
            	    // InternalOseeDsl.g:2394:3: ( (lv_operators_2_0= ruleXLogicOperator ) )
            	    // InternalOseeDsl.g:2395:1: (lv_operators_2_0= ruleXLogicOperator )
            	    {
            	    // InternalOseeDsl.g:2395:1: (lv_operators_2_0= ruleXLogicOperator )
            	    // InternalOseeDsl.g:2396:3: lv_operators_2_0= ruleXLogicOperator
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getCompoundConditionAccess().getOperatorsXLogicOperatorEnumRuleCall_2_0_0()); 
            	    	    
            	    pushFollow(FOLLOW_47);
            	    lv_operators_2_0=ruleXLogicOperator();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getCompoundConditionRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"operators",
            	            		lv_operators_2_0, 
            	            		"org.eclipse.osee.framework.core.dsl.OseeDsl.XLogicOperator");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }

            	    // InternalOseeDsl.g:2412:2: ( (lv_conditions_3_0= ruleSimpleCondition ) )
            	    // InternalOseeDsl.g:2413:1: (lv_conditions_3_0= ruleSimpleCondition )
            	    {
            	    // InternalOseeDsl.g:2413:1: (lv_conditions_3_0= ruleSimpleCondition )
            	    // InternalOseeDsl.g:2414:3: lv_conditions_3_0= ruleSimpleCondition
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getCompoundConditionAccess().getConditionsSimpleConditionParserRuleCall_2_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_49);
            	    lv_conditions_3_0=ruleSimpleCondition();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getCompoundConditionRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"conditions",
            	            		lv_conditions_3_0, 
            	            		"org.eclipse.osee.framework.core.dsl.OseeDsl.SimpleCondition");
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

            otherlv_4=(Token)match(input,72,FOLLOW_2); 

                	newLeafNode(otherlv_4, grammarAccess.getCompoundConditionAccess().getRightParenthesisKeyword_3());
                

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
    // $ANTLR end "ruleCompoundCondition"


    // $ANTLR start "entryRuleXArtifactMatcher"
    // InternalOseeDsl.g:2442:1: entryRuleXArtifactMatcher returns [EObject current=null] : iv_ruleXArtifactMatcher= ruleXArtifactMatcher EOF ;
    public final EObject entryRuleXArtifactMatcher() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleXArtifactMatcher = null;


        try {
            // InternalOseeDsl.g:2443:2: (iv_ruleXArtifactMatcher= ruleXArtifactMatcher EOF )
            // InternalOseeDsl.g:2444:2: iv_ruleXArtifactMatcher= ruleXArtifactMatcher EOF
            {
             newCompositeNode(grammarAccess.getXArtifactMatcherRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleXArtifactMatcher=ruleXArtifactMatcher();

            state._fsp--;

             current =iv_ruleXArtifactMatcher; 
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
    // $ANTLR end "entryRuleXArtifactMatcher"


    // $ANTLR start "ruleXArtifactMatcher"
    // InternalOseeDsl.g:2451:1: ruleXArtifactMatcher returns [EObject current=null] : (otherlv_0= 'artifactMatcher' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= 'where' ( (lv_conditions_3_0= ruleCondition ) ) ( ( (lv_operators_4_0= ruleXLogicOperator ) ) ( (lv_conditions_5_0= ruleCondition ) ) )* otherlv_6= ';' ) ;
    public final EObject ruleXArtifactMatcher() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_name_1_0=null;
        Token otherlv_2=null;
        Token otherlv_6=null;
        EObject lv_conditions_3_0 = null;

        Enumerator lv_operators_4_0 = null;

        EObject lv_conditions_5_0 = null;


         enterRule(); 
            
        try {
            // InternalOseeDsl.g:2454:28: ( (otherlv_0= 'artifactMatcher' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= 'where' ( (lv_conditions_3_0= ruleCondition ) ) ( ( (lv_operators_4_0= ruleXLogicOperator ) ) ( (lv_conditions_5_0= ruleCondition ) ) )* otherlv_6= ';' ) )
            // InternalOseeDsl.g:2455:1: (otherlv_0= 'artifactMatcher' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= 'where' ( (lv_conditions_3_0= ruleCondition ) ) ( ( (lv_operators_4_0= ruleXLogicOperator ) ) ( (lv_conditions_5_0= ruleCondition ) ) )* otherlv_6= ';' )
            {
            // InternalOseeDsl.g:2455:1: (otherlv_0= 'artifactMatcher' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= 'where' ( (lv_conditions_3_0= ruleCondition ) ) ( ( (lv_operators_4_0= ruleXLogicOperator ) ) ( (lv_conditions_5_0= ruleCondition ) ) )* otherlv_6= ';' )
            // InternalOseeDsl.g:2455:3: otherlv_0= 'artifactMatcher' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= 'where' ( (lv_conditions_3_0= ruleCondition ) ) ( ( (lv_operators_4_0= ruleXLogicOperator ) ) ( (lv_conditions_5_0= ruleCondition ) ) )* otherlv_6= ';'
            {
            otherlv_0=(Token)match(input,73,FOLLOW_6); 

                	newLeafNode(otherlv_0, grammarAccess.getXArtifactMatcherAccess().getArtifactMatcherKeyword_0());
                
            // InternalOseeDsl.g:2459:1: ( (lv_name_1_0= RULE_STRING ) )
            // InternalOseeDsl.g:2460:1: (lv_name_1_0= RULE_STRING )
            {
            // InternalOseeDsl.g:2460:1: (lv_name_1_0= RULE_STRING )
            // InternalOseeDsl.g:2461:3: lv_name_1_0= RULE_STRING
            {
            lv_name_1_0=(Token)match(input,RULE_STRING,FOLLOW_50); 

            			newLeafNode(lv_name_1_0, grammarAccess.getXArtifactMatcherAccess().getNameSTRINGTerminalRuleCall_1_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getXArtifactMatcherRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"name",
                    		lv_name_1_0, 
                    		"org.eclipse.osee.framework.core.dsl.OseeDsl.STRING");
            	    

            }


            }

            otherlv_2=(Token)match(input,74,FOLLOW_51); 

                	newLeafNode(otherlv_2, grammarAccess.getXArtifactMatcherAccess().getWhereKeyword_2());
                
            // InternalOseeDsl.g:2481:1: ( (lv_conditions_3_0= ruleCondition ) )
            // InternalOseeDsl.g:2482:1: (lv_conditions_3_0= ruleCondition )
            {
            // InternalOseeDsl.g:2482:1: (lv_conditions_3_0= ruleCondition )
            // InternalOseeDsl.g:2483:3: lv_conditions_3_0= ruleCondition
            {
             
            	        newCompositeNode(grammarAccess.getXArtifactMatcherAccess().getConditionsConditionParserRuleCall_3_0()); 
            	    
            pushFollow(FOLLOW_52);
            lv_conditions_3_0=ruleCondition();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getXArtifactMatcherRule());
            	        }
                   		add(
                   			current, 
                   			"conditions",
                    		lv_conditions_3_0, 
                    		"org.eclipse.osee.framework.core.dsl.OseeDsl.Condition");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // InternalOseeDsl.g:2499:2: ( ( (lv_operators_4_0= ruleXLogicOperator ) ) ( (lv_conditions_5_0= ruleCondition ) ) )*
            loop37:
            do {
                int alt37=2;
                int LA37_0 = input.LA(1);

                if ( ((LA37_0>=88 && LA37_0<=89)) ) {
                    alt37=1;
                }


                switch (alt37) {
            	case 1 :
            	    // InternalOseeDsl.g:2499:3: ( (lv_operators_4_0= ruleXLogicOperator ) ) ( (lv_conditions_5_0= ruleCondition ) )
            	    {
            	    // InternalOseeDsl.g:2499:3: ( (lv_operators_4_0= ruleXLogicOperator ) )
            	    // InternalOseeDsl.g:2500:1: (lv_operators_4_0= ruleXLogicOperator )
            	    {
            	    // InternalOseeDsl.g:2500:1: (lv_operators_4_0= ruleXLogicOperator )
            	    // InternalOseeDsl.g:2501:3: lv_operators_4_0= ruleXLogicOperator
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getXArtifactMatcherAccess().getOperatorsXLogicOperatorEnumRuleCall_4_0_0()); 
            	    	    
            	    pushFollow(FOLLOW_51);
            	    lv_operators_4_0=ruleXLogicOperator();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getXArtifactMatcherRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"operators",
            	            		lv_operators_4_0, 
            	            		"org.eclipse.osee.framework.core.dsl.OseeDsl.XLogicOperator");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }

            	    // InternalOseeDsl.g:2517:2: ( (lv_conditions_5_0= ruleCondition ) )
            	    // InternalOseeDsl.g:2518:1: (lv_conditions_5_0= ruleCondition )
            	    {
            	    // InternalOseeDsl.g:2518:1: (lv_conditions_5_0= ruleCondition )
            	    // InternalOseeDsl.g:2519:3: lv_conditions_5_0= ruleCondition
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getXArtifactMatcherAccess().getConditionsConditionParserRuleCall_4_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_52);
            	    lv_conditions_5_0=ruleCondition();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getXArtifactMatcherRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"conditions",
            	            		lv_conditions_5_0, 
            	            		"org.eclipse.osee.framework.core.dsl.OseeDsl.Condition");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop37;
                }
            } while (true);

            otherlv_6=(Token)match(input,75,FOLLOW_2); 

                	newLeafNode(otherlv_6, grammarAccess.getXArtifactMatcherAccess().getSemicolonKeyword_5());
                

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
    // $ANTLR end "ruleXArtifactMatcher"


    // $ANTLR start "entryRuleRole"
    // InternalOseeDsl.g:2547:1: entryRuleRole returns [EObject current=null] : iv_ruleRole= ruleRole EOF ;
    public final EObject entryRuleRole() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRole = null;


        try {
            // InternalOseeDsl.g:2548:2: (iv_ruleRole= ruleRole EOF )
            // InternalOseeDsl.g:2549:2: iv_ruleRole= ruleRole EOF
            {
             newCompositeNode(grammarAccess.getRoleRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleRole=ruleRole();

            state._fsp--;

             current =iv_ruleRole; 
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
    // $ANTLR end "entryRuleRole"


    // $ANTLR start "ruleRole"
    // InternalOseeDsl.g:2556:1: ruleRole returns [EObject current=null] : (otherlv_0= 'role' ( (lv_name_1_0= RULE_STRING ) ) (otherlv_2= 'extends' ( (otherlv_3= RULE_STRING ) ) )? otherlv_4= '{' ( ( (lv_usersAndGroups_5_0= ruleUsersAndGroups ) ) | ( (lv_referencedContexts_6_0= ruleReferencedContext ) ) )+ otherlv_7= '}' ) ;
    public final EObject ruleRole() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_name_1_0=null;
        Token otherlv_2=null;
        Token otherlv_3=null;
        Token otherlv_4=null;
        Token otherlv_7=null;
        EObject lv_usersAndGroups_5_0 = null;

        EObject lv_referencedContexts_6_0 = null;


         enterRule(); 
            
        try {
            // InternalOseeDsl.g:2559:28: ( (otherlv_0= 'role' ( (lv_name_1_0= RULE_STRING ) ) (otherlv_2= 'extends' ( (otherlv_3= RULE_STRING ) ) )? otherlv_4= '{' ( ( (lv_usersAndGroups_5_0= ruleUsersAndGroups ) ) | ( (lv_referencedContexts_6_0= ruleReferencedContext ) ) )+ otherlv_7= '}' ) )
            // InternalOseeDsl.g:2560:1: (otherlv_0= 'role' ( (lv_name_1_0= RULE_STRING ) ) (otherlv_2= 'extends' ( (otherlv_3= RULE_STRING ) ) )? otherlv_4= '{' ( ( (lv_usersAndGroups_5_0= ruleUsersAndGroups ) ) | ( (lv_referencedContexts_6_0= ruleReferencedContext ) ) )+ otherlv_7= '}' )
            {
            // InternalOseeDsl.g:2560:1: (otherlv_0= 'role' ( (lv_name_1_0= RULE_STRING ) ) (otherlv_2= 'extends' ( (otherlv_3= RULE_STRING ) ) )? otherlv_4= '{' ( ( (lv_usersAndGroups_5_0= ruleUsersAndGroups ) ) | ( (lv_referencedContexts_6_0= ruleReferencedContext ) ) )+ otherlv_7= '}' )
            // InternalOseeDsl.g:2560:3: otherlv_0= 'role' ( (lv_name_1_0= RULE_STRING ) ) (otherlv_2= 'extends' ( (otherlv_3= RULE_STRING ) ) )? otherlv_4= '{' ( ( (lv_usersAndGroups_5_0= ruleUsersAndGroups ) ) | ( (lv_referencedContexts_6_0= ruleReferencedContext ) ) )+ otherlv_7= '}'
            {
            otherlv_0=(Token)match(input,76,FOLLOW_6); 

                	newLeafNode(otherlv_0, grammarAccess.getRoleAccess().getRoleKeyword_0());
                
            // InternalOseeDsl.g:2564:1: ( (lv_name_1_0= RULE_STRING ) )
            // InternalOseeDsl.g:2565:1: (lv_name_1_0= RULE_STRING )
            {
            // InternalOseeDsl.g:2565:1: (lv_name_1_0= RULE_STRING )
            // InternalOseeDsl.g:2566:3: lv_name_1_0= RULE_STRING
            {
            lv_name_1_0=(Token)match(input,RULE_STRING,FOLLOW_10); 

            			newLeafNode(lv_name_1_0, grammarAccess.getRoleAccess().getNameSTRINGTerminalRuleCall_1_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getRoleRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"name",
                    		lv_name_1_0, 
                    		"org.eclipse.osee.framework.core.dsl.OseeDsl.STRING");
            	    

            }


            }

            // InternalOseeDsl.g:2582:2: (otherlv_2= 'extends' ( (otherlv_3= RULE_STRING ) ) )?
            int alt38=2;
            int LA38_0 = input.LA(1);

            if ( (LA38_0==16) ) {
                alt38=1;
            }
            switch (alt38) {
                case 1 :
                    // InternalOseeDsl.g:2582:4: otherlv_2= 'extends' ( (otherlv_3= RULE_STRING ) )
                    {
                    otherlv_2=(Token)match(input,16,FOLLOW_6); 

                        	newLeafNode(otherlv_2, grammarAccess.getRoleAccess().getExtendsKeyword_2_0());
                        
                    // InternalOseeDsl.g:2586:1: ( (otherlv_3= RULE_STRING ) )
                    // InternalOseeDsl.g:2587:1: (otherlv_3= RULE_STRING )
                    {
                    // InternalOseeDsl.g:2587:1: (otherlv_3= RULE_STRING )
                    // InternalOseeDsl.g:2588:3: otherlv_3= RULE_STRING
                    {

                    			if (current==null) {
                    	            current = createModelElement(grammarAccess.getRoleRule());
                    	        }
                            
                    otherlv_3=(Token)match(input,RULE_STRING,FOLLOW_20); 

                    		newLeafNode(otherlv_3, grammarAccess.getRoleAccess().getSuperRolesRoleCrossReference_2_1_0()); 
                    	

                    }


                    }


                    }
                    break;

            }

            otherlv_4=(Token)match(input,18,FOLLOW_53); 

                	newLeafNode(otherlv_4, grammarAccess.getRoleAccess().getLeftCurlyBracketKeyword_3());
                
            // InternalOseeDsl.g:2603:1: ( ( (lv_usersAndGroups_5_0= ruleUsersAndGroups ) ) | ( (lv_referencedContexts_6_0= ruleReferencedContext ) ) )+
            int cnt39=0;
            loop39:
            do {
                int alt39=3;
                int LA39_0 = input.LA(1);

                if ( (LA39_0==19) ) {
                    alt39=1;
                }
                else if ( (LA39_0==77) ) {
                    alt39=2;
                }


                switch (alt39) {
            	case 1 :
            	    // InternalOseeDsl.g:2603:2: ( (lv_usersAndGroups_5_0= ruleUsersAndGroups ) )
            	    {
            	    // InternalOseeDsl.g:2603:2: ( (lv_usersAndGroups_5_0= ruleUsersAndGroups ) )
            	    // InternalOseeDsl.g:2604:1: (lv_usersAndGroups_5_0= ruleUsersAndGroups )
            	    {
            	    // InternalOseeDsl.g:2604:1: (lv_usersAndGroups_5_0= ruleUsersAndGroups )
            	    // InternalOseeDsl.g:2605:3: lv_usersAndGroups_5_0= ruleUsersAndGroups
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getRoleAccess().getUsersAndGroupsUsersAndGroupsParserRuleCall_4_0_0()); 
            	    	    
            	    pushFollow(FOLLOW_54);
            	    lv_usersAndGroups_5_0=ruleUsersAndGroups();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getRoleRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"usersAndGroups",
            	            		lv_usersAndGroups_5_0, 
            	            		"org.eclipse.osee.framework.core.dsl.OseeDsl.UsersAndGroups");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;
            	case 2 :
            	    // InternalOseeDsl.g:2622:6: ( (lv_referencedContexts_6_0= ruleReferencedContext ) )
            	    {
            	    // InternalOseeDsl.g:2622:6: ( (lv_referencedContexts_6_0= ruleReferencedContext ) )
            	    // InternalOseeDsl.g:2623:1: (lv_referencedContexts_6_0= ruleReferencedContext )
            	    {
            	    // InternalOseeDsl.g:2623:1: (lv_referencedContexts_6_0= ruleReferencedContext )
            	    // InternalOseeDsl.g:2624:3: lv_referencedContexts_6_0= ruleReferencedContext
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getRoleAccess().getReferencedContextsReferencedContextParserRuleCall_4_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_54);
            	    lv_referencedContexts_6_0=ruleReferencedContext();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getRoleRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"referencedContexts",
            	            		lv_referencedContexts_6_0, 
            	            		"org.eclipse.osee.framework.core.dsl.OseeDsl.ReferencedContext");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt39 >= 1 ) break loop39;
                        EarlyExitException eee =
                            new EarlyExitException(39, input);
                        throw eee;
                }
                cnt39++;
            } while (true);

            otherlv_7=(Token)match(input,21,FOLLOW_2); 

                	newLeafNode(otherlv_7, grammarAccess.getRoleAccess().getRightCurlyBracketKeyword_5());
                

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
    // $ANTLR end "ruleRole"


    // $ANTLR start "entryRuleReferencedContext"
    // InternalOseeDsl.g:2652:1: entryRuleReferencedContext returns [EObject current=null] : iv_ruleReferencedContext= ruleReferencedContext EOF ;
    public final EObject entryRuleReferencedContext() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleReferencedContext = null;


        try {
            // InternalOseeDsl.g:2653:2: (iv_ruleReferencedContext= ruleReferencedContext EOF )
            // InternalOseeDsl.g:2654:2: iv_ruleReferencedContext= ruleReferencedContext EOF
            {
             newCompositeNode(grammarAccess.getReferencedContextRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleReferencedContext=ruleReferencedContext();

            state._fsp--;

             current =iv_ruleReferencedContext; 
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
    // $ANTLR end "entryRuleReferencedContext"


    // $ANTLR start "ruleReferencedContext"
    // InternalOseeDsl.g:2661:1: ruleReferencedContext returns [EObject current=null] : (otherlv_0= 'accessContext' ( (lv_accessContextRef_1_0= RULE_STRING ) ) otherlv_2= ';' ) ;
    public final EObject ruleReferencedContext() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_accessContextRef_1_0=null;
        Token otherlv_2=null;

         enterRule(); 
            
        try {
            // InternalOseeDsl.g:2664:28: ( (otherlv_0= 'accessContext' ( (lv_accessContextRef_1_0= RULE_STRING ) ) otherlv_2= ';' ) )
            // InternalOseeDsl.g:2665:1: (otherlv_0= 'accessContext' ( (lv_accessContextRef_1_0= RULE_STRING ) ) otherlv_2= ';' )
            {
            // InternalOseeDsl.g:2665:1: (otherlv_0= 'accessContext' ( (lv_accessContextRef_1_0= RULE_STRING ) ) otherlv_2= ';' )
            // InternalOseeDsl.g:2665:3: otherlv_0= 'accessContext' ( (lv_accessContextRef_1_0= RULE_STRING ) ) otherlv_2= ';'
            {
            otherlv_0=(Token)match(input,77,FOLLOW_6); 

                	newLeafNode(otherlv_0, grammarAccess.getReferencedContextAccess().getAccessContextKeyword_0());
                
            // InternalOseeDsl.g:2669:1: ( (lv_accessContextRef_1_0= RULE_STRING ) )
            // InternalOseeDsl.g:2670:1: (lv_accessContextRef_1_0= RULE_STRING )
            {
            // InternalOseeDsl.g:2670:1: (lv_accessContextRef_1_0= RULE_STRING )
            // InternalOseeDsl.g:2671:3: lv_accessContextRef_1_0= RULE_STRING
            {
            lv_accessContextRef_1_0=(Token)match(input,RULE_STRING,FOLLOW_55); 

            			newLeafNode(lv_accessContextRef_1_0, grammarAccess.getReferencedContextAccess().getAccessContextRefSTRINGTerminalRuleCall_1_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getReferencedContextRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"accessContextRef",
                    		lv_accessContextRef_1_0, 
                    		"org.eclipse.osee.framework.core.dsl.OseeDsl.STRING");
            	    

            }


            }

            otherlv_2=(Token)match(input,75,FOLLOW_2); 

                	newLeafNode(otherlv_2, grammarAccess.getReferencedContextAccess().getSemicolonKeyword_2());
                

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
    // $ANTLR end "ruleReferencedContext"


    // $ANTLR start "entryRuleUsersAndGroups"
    // InternalOseeDsl.g:2699:1: entryRuleUsersAndGroups returns [EObject current=null] : iv_ruleUsersAndGroups= ruleUsersAndGroups EOF ;
    public final EObject entryRuleUsersAndGroups() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleUsersAndGroups = null;


        try {
            // InternalOseeDsl.g:2700:2: (iv_ruleUsersAndGroups= ruleUsersAndGroups EOF )
            // InternalOseeDsl.g:2701:2: iv_ruleUsersAndGroups= ruleUsersAndGroups EOF
            {
             newCompositeNode(grammarAccess.getUsersAndGroupsRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleUsersAndGroups=ruleUsersAndGroups();

            state._fsp--;

             current =iv_ruleUsersAndGroups; 
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
    // $ANTLR end "entryRuleUsersAndGroups"


    // $ANTLR start "ruleUsersAndGroups"
    // InternalOseeDsl.g:2708:1: ruleUsersAndGroups returns [EObject current=null] : (otherlv_0= 'guid' ( (lv_userOrGroupGuid_1_0= RULE_STRING ) ) otherlv_2= ';' ) ;
    public final EObject ruleUsersAndGroups() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_userOrGroupGuid_1_0=null;
        Token otherlv_2=null;

         enterRule(); 
            
        try {
            // InternalOseeDsl.g:2711:28: ( (otherlv_0= 'guid' ( (lv_userOrGroupGuid_1_0= RULE_STRING ) ) otherlv_2= ';' ) )
            // InternalOseeDsl.g:2712:1: (otherlv_0= 'guid' ( (lv_userOrGroupGuid_1_0= RULE_STRING ) ) otherlv_2= ';' )
            {
            // InternalOseeDsl.g:2712:1: (otherlv_0= 'guid' ( (lv_userOrGroupGuid_1_0= RULE_STRING ) ) otherlv_2= ';' )
            // InternalOseeDsl.g:2712:3: otherlv_0= 'guid' ( (lv_userOrGroupGuid_1_0= RULE_STRING ) ) otherlv_2= ';'
            {
            otherlv_0=(Token)match(input,19,FOLLOW_6); 

                	newLeafNode(otherlv_0, grammarAccess.getUsersAndGroupsAccess().getGuidKeyword_0());
                
            // InternalOseeDsl.g:2716:1: ( (lv_userOrGroupGuid_1_0= RULE_STRING ) )
            // InternalOseeDsl.g:2717:1: (lv_userOrGroupGuid_1_0= RULE_STRING )
            {
            // InternalOseeDsl.g:2717:1: (lv_userOrGroupGuid_1_0= RULE_STRING )
            // InternalOseeDsl.g:2718:3: lv_userOrGroupGuid_1_0= RULE_STRING
            {
            lv_userOrGroupGuid_1_0=(Token)match(input,RULE_STRING,FOLLOW_55); 

            			newLeafNode(lv_userOrGroupGuid_1_0, grammarAccess.getUsersAndGroupsAccess().getUserOrGroupGuidSTRINGTerminalRuleCall_1_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getUsersAndGroupsRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"userOrGroupGuid",
                    		lv_userOrGroupGuid_1_0, 
                    		"org.eclipse.osee.framework.core.dsl.OseeDsl.STRING");
            	    

            }


            }

            otherlv_2=(Token)match(input,75,FOLLOW_2); 

                	newLeafNode(otherlv_2, grammarAccess.getUsersAndGroupsAccess().getSemicolonKeyword_2());
                

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
    // $ANTLR end "ruleUsersAndGroups"


    // $ANTLR start "entryRuleAccessContext"
    // InternalOseeDsl.g:2746:1: entryRuleAccessContext returns [EObject current=null] : iv_ruleAccessContext= ruleAccessContext EOF ;
    public final EObject entryRuleAccessContext() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAccessContext = null;


        try {
            // InternalOseeDsl.g:2747:2: (iv_ruleAccessContext= ruleAccessContext EOF )
            // InternalOseeDsl.g:2748:2: iv_ruleAccessContext= ruleAccessContext EOF
            {
             newCompositeNode(grammarAccess.getAccessContextRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleAccessContext=ruleAccessContext();

            state._fsp--;

             current =iv_ruleAccessContext; 
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
    // $ANTLR end "entryRuleAccessContext"


    // $ANTLR start "ruleAccessContext"
    // InternalOseeDsl.g:2755:1: ruleAccessContext returns [EObject current=null] : (otherlv_0= 'accessContext' ( (lv_name_1_0= RULE_STRING ) ) (otherlv_2= 'extends' ( (otherlv_3= RULE_STRING ) ) )? otherlv_4= '{' otherlv_5= 'guid' ( (lv_guid_6_0= RULE_STRING ) ) otherlv_7= ';' ( ( (lv_accessRules_8_0= ruleObjectRestriction ) ) | ( (lv_hierarchyRestrictions_9_0= ruleHierarchyRestriction ) ) )+ otherlv_10= '}' ) ;
    public final EObject ruleAccessContext() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_name_1_0=null;
        Token otherlv_2=null;
        Token otherlv_3=null;
        Token otherlv_4=null;
        Token otherlv_5=null;
        Token lv_guid_6_0=null;
        Token otherlv_7=null;
        Token otherlv_10=null;
        EObject lv_accessRules_8_0 = null;

        EObject lv_hierarchyRestrictions_9_0 = null;


         enterRule(); 
            
        try {
            // InternalOseeDsl.g:2758:28: ( (otherlv_0= 'accessContext' ( (lv_name_1_0= RULE_STRING ) ) (otherlv_2= 'extends' ( (otherlv_3= RULE_STRING ) ) )? otherlv_4= '{' otherlv_5= 'guid' ( (lv_guid_6_0= RULE_STRING ) ) otherlv_7= ';' ( ( (lv_accessRules_8_0= ruleObjectRestriction ) ) | ( (lv_hierarchyRestrictions_9_0= ruleHierarchyRestriction ) ) )+ otherlv_10= '}' ) )
            // InternalOseeDsl.g:2759:1: (otherlv_0= 'accessContext' ( (lv_name_1_0= RULE_STRING ) ) (otherlv_2= 'extends' ( (otherlv_3= RULE_STRING ) ) )? otherlv_4= '{' otherlv_5= 'guid' ( (lv_guid_6_0= RULE_STRING ) ) otherlv_7= ';' ( ( (lv_accessRules_8_0= ruleObjectRestriction ) ) | ( (lv_hierarchyRestrictions_9_0= ruleHierarchyRestriction ) ) )+ otherlv_10= '}' )
            {
            // InternalOseeDsl.g:2759:1: (otherlv_0= 'accessContext' ( (lv_name_1_0= RULE_STRING ) ) (otherlv_2= 'extends' ( (otherlv_3= RULE_STRING ) ) )? otherlv_4= '{' otherlv_5= 'guid' ( (lv_guid_6_0= RULE_STRING ) ) otherlv_7= ';' ( ( (lv_accessRules_8_0= ruleObjectRestriction ) ) | ( (lv_hierarchyRestrictions_9_0= ruleHierarchyRestriction ) ) )+ otherlv_10= '}' )
            // InternalOseeDsl.g:2759:3: otherlv_0= 'accessContext' ( (lv_name_1_0= RULE_STRING ) ) (otherlv_2= 'extends' ( (otherlv_3= RULE_STRING ) ) )? otherlv_4= '{' otherlv_5= 'guid' ( (lv_guid_6_0= RULE_STRING ) ) otherlv_7= ';' ( ( (lv_accessRules_8_0= ruleObjectRestriction ) ) | ( (lv_hierarchyRestrictions_9_0= ruleHierarchyRestriction ) ) )+ otherlv_10= '}'
            {
            otherlv_0=(Token)match(input,77,FOLLOW_6); 

                	newLeafNode(otherlv_0, grammarAccess.getAccessContextAccess().getAccessContextKeyword_0());
                
            // InternalOseeDsl.g:2763:1: ( (lv_name_1_0= RULE_STRING ) )
            // InternalOseeDsl.g:2764:1: (lv_name_1_0= RULE_STRING )
            {
            // InternalOseeDsl.g:2764:1: (lv_name_1_0= RULE_STRING )
            // InternalOseeDsl.g:2765:3: lv_name_1_0= RULE_STRING
            {
            lv_name_1_0=(Token)match(input,RULE_STRING,FOLLOW_10); 

            			newLeafNode(lv_name_1_0, grammarAccess.getAccessContextAccess().getNameSTRINGTerminalRuleCall_1_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getAccessContextRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"name",
                    		lv_name_1_0, 
                    		"org.eclipse.osee.framework.core.dsl.OseeDsl.STRING");
            	    

            }


            }

            // InternalOseeDsl.g:2781:2: (otherlv_2= 'extends' ( (otherlv_3= RULE_STRING ) ) )?
            int alt40=2;
            int LA40_0 = input.LA(1);

            if ( (LA40_0==16) ) {
                alt40=1;
            }
            switch (alt40) {
                case 1 :
                    // InternalOseeDsl.g:2781:4: otherlv_2= 'extends' ( (otherlv_3= RULE_STRING ) )
                    {
                    otherlv_2=(Token)match(input,16,FOLLOW_6); 

                        	newLeafNode(otherlv_2, grammarAccess.getAccessContextAccess().getExtendsKeyword_2_0());
                        
                    // InternalOseeDsl.g:2785:1: ( (otherlv_3= RULE_STRING ) )
                    // InternalOseeDsl.g:2786:1: (otherlv_3= RULE_STRING )
                    {
                    // InternalOseeDsl.g:2786:1: (otherlv_3= RULE_STRING )
                    // InternalOseeDsl.g:2787:3: otherlv_3= RULE_STRING
                    {

                    			if (current==null) {
                    	            current = createModelElement(grammarAccess.getAccessContextRule());
                    	        }
                            
                    otherlv_3=(Token)match(input,RULE_STRING,FOLLOW_20); 

                    		newLeafNode(otherlv_3, grammarAccess.getAccessContextAccess().getSuperAccessContextsAccessContextCrossReference_2_1_0()); 
                    	

                    }


                    }


                    }
                    break;

            }

            otherlv_4=(Token)match(input,18,FOLLOW_56); 

                	newLeafNode(otherlv_4, grammarAccess.getAccessContextAccess().getLeftCurlyBracketKeyword_3());
                
            otherlv_5=(Token)match(input,19,FOLLOW_6); 

                	newLeafNode(otherlv_5, grammarAccess.getAccessContextAccess().getGuidKeyword_4());
                
            // InternalOseeDsl.g:2806:1: ( (lv_guid_6_0= RULE_STRING ) )
            // InternalOseeDsl.g:2807:1: (lv_guid_6_0= RULE_STRING )
            {
            // InternalOseeDsl.g:2807:1: (lv_guid_6_0= RULE_STRING )
            // InternalOseeDsl.g:2808:3: lv_guid_6_0= RULE_STRING
            {
            lv_guid_6_0=(Token)match(input,RULE_STRING,FOLLOW_55); 

            			newLeafNode(lv_guid_6_0, grammarAccess.getAccessContextAccess().getGuidSTRINGTerminalRuleCall_5_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getAccessContextRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"guid",
                    		lv_guid_6_0, 
                    		"org.eclipse.osee.framework.core.dsl.OseeDsl.STRING");
            	    

            }


            }

            otherlv_7=(Token)match(input,75,FOLLOW_57); 

                	newLeafNode(otherlv_7, grammarAccess.getAccessContextAccess().getSemicolonKeyword_6());
                
            // InternalOseeDsl.g:2828:1: ( ( (lv_accessRules_8_0= ruleObjectRestriction ) ) | ( (lv_hierarchyRestrictions_9_0= ruleHierarchyRestriction ) ) )+
            int cnt41=0;
            loop41:
            do {
                int alt41=3;
                int LA41_0 = input.LA(1);

                if ( ((LA41_0>=93 && LA41_0<=94)) ) {
                    alt41=1;
                }
                else if ( (LA41_0==78) ) {
                    alt41=2;
                }


                switch (alt41) {
            	case 1 :
            	    // InternalOseeDsl.g:2828:2: ( (lv_accessRules_8_0= ruleObjectRestriction ) )
            	    {
            	    // InternalOseeDsl.g:2828:2: ( (lv_accessRules_8_0= ruleObjectRestriction ) )
            	    // InternalOseeDsl.g:2829:1: (lv_accessRules_8_0= ruleObjectRestriction )
            	    {
            	    // InternalOseeDsl.g:2829:1: (lv_accessRules_8_0= ruleObjectRestriction )
            	    // InternalOseeDsl.g:2830:3: lv_accessRules_8_0= ruleObjectRestriction
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getAccessContextAccess().getAccessRulesObjectRestrictionParserRuleCall_7_0_0()); 
            	    	    
            	    pushFollow(FOLLOW_58);
            	    lv_accessRules_8_0=ruleObjectRestriction();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getAccessContextRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"accessRules",
            	            		lv_accessRules_8_0, 
            	            		"org.eclipse.osee.framework.core.dsl.OseeDsl.ObjectRestriction");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;
            	case 2 :
            	    // InternalOseeDsl.g:2847:6: ( (lv_hierarchyRestrictions_9_0= ruleHierarchyRestriction ) )
            	    {
            	    // InternalOseeDsl.g:2847:6: ( (lv_hierarchyRestrictions_9_0= ruleHierarchyRestriction ) )
            	    // InternalOseeDsl.g:2848:1: (lv_hierarchyRestrictions_9_0= ruleHierarchyRestriction )
            	    {
            	    // InternalOseeDsl.g:2848:1: (lv_hierarchyRestrictions_9_0= ruleHierarchyRestriction )
            	    // InternalOseeDsl.g:2849:3: lv_hierarchyRestrictions_9_0= ruleHierarchyRestriction
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getAccessContextAccess().getHierarchyRestrictionsHierarchyRestrictionParserRuleCall_7_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_58);
            	    lv_hierarchyRestrictions_9_0=ruleHierarchyRestriction();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getAccessContextRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"hierarchyRestrictions",
            	            		lv_hierarchyRestrictions_9_0, 
            	            		"org.eclipse.osee.framework.core.dsl.OseeDsl.HierarchyRestriction");
            	    	        afterParserOrEnumRuleCall();
            	    	    

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

            otherlv_10=(Token)match(input,21,FOLLOW_2); 

                	newLeafNode(otherlv_10, grammarAccess.getAccessContextAccess().getRightCurlyBracketKeyword_8());
                

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
    // $ANTLR end "ruleAccessContext"


    // $ANTLR start "entryRuleHierarchyRestriction"
    // InternalOseeDsl.g:2877:1: entryRuleHierarchyRestriction returns [EObject current=null] : iv_ruleHierarchyRestriction= ruleHierarchyRestriction EOF ;
    public final EObject entryRuleHierarchyRestriction() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleHierarchyRestriction = null;


        try {
            // InternalOseeDsl.g:2878:2: (iv_ruleHierarchyRestriction= ruleHierarchyRestriction EOF )
            // InternalOseeDsl.g:2879:2: iv_ruleHierarchyRestriction= ruleHierarchyRestriction EOF
            {
             newCompositeNode(grammarAccess.getHierarchyRestrictionRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleHierarchyRestriction=ruleHierarchyRestriction();

            state._fsp--;

             current =iv_ruleHierarchyRestriction; 
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
    // $ANTLR end "entryRuleHierarchyRestriction"


    // $ANTLR start "ruleHierarchyRestriction"
    // InternalOseeDsl.g:2886:1: ruleHierarchyRestriction returns [EObject current=null] : (otherlv_0= 'childrenOf' ( (otherlv_1= RULE_STRING ) ) otherlv_2= '{' ( (lv_accessRules_3_0= ruleObjectRestriction ) )+ otherlv_4= '}' ) ;
    public final EObject ruleHierarchyRestriction() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;
        Token otherlv_2=null;
        Token otherlv_4=null;
        EObject lv_accessRules_3_0 = null;


         enterRule(); 
            
        try {
            // InternalOseeDsl.g:2889:28: ( (otherlv_0= 'childrenOf' ( (otherlv_1= RULE_STRING ) ) otherlv_2= '{' ( (lv_accessRules_3_0= ruleObjectRestriction ) )+ otherlv_4= '}' ) )
            // InternalOseeDsl.g:2890:1: (otherlv_0= 'childrenOf' ( (otherlv_1= RULE_STRING ) ) otherlv_2= '{' ( (lv_accessRules_3_0= ruleObjectRestriction ) )+ otherlv_4= '}' )
            {
            // InternalOseeDsl.g:2890:1: (otherlv_0= 'childrenOf' ( (otherlv_1= RULE_STRING ) ) otherlv_2= '{' ( (lv_accessRules_3_0= ruleObjectRestriction ) )+ otherlv_4= '}' )
            // InternalOseeDsl.g:2890:3: otherlv_0= 'childrenOf' ( (otherlv_1= RULE_STRING ) ) otherlv_2= '{' ( (lv_accessRules_3_0= ruleObjectRestriction ) )+ otherlv_4= '}'
            {
            otherlv_0=(Token)match(input,78,FOLLOW_6); 

                	newLeafNode(otherlv_0, grammarAccess.getHierarchyRestrictionAccess().getChildrenOfKeyword_0());
                
            // InternalOseeDsl.g:2894:1: ( (otherlv_1= RULE_STRING ) )
            // InternalOseeDsl.g:2895:1: (otherlv_1= RULE_STRING )
            {
            // InternalOseeDsl.g:2895:1: (otherlv_1= RULE_STRING )
            // InternalOseeDsl.g:2896:3: otherlv_1= RULE_STRING
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getHierarchyRestrictionRule());
            	        }
                    
            otherlv_1=(Token)match(input,RULE_STRING,FOLLOW_20); 

            		newLeafNode(otherlv_1, grammarAccess.getHierarchyRestrictionAccess().getArtifactMatcherRefXArtifactMatcherCrossReference_1_0()); 
            	

            }


            }

            otherlv_2=(Token)match(input,18,FOLLOW_59); 

                	newLeafNode(otherlv_2, grammarAccess.getHierarchyRestrictionAccess().getLeftCurlyBracketKeyword_2());
                
            // InternalOseeDsl.g:2911:1: ( (lv_accessRules_3_0= ruleObjectRestriction ) )+
            int cnt42=0;
            loop42:
            do {
                int alt42=2;
                int LA42_0 = input.LA(1);

                if ( ((LA42_0>=93 && LA42_0<=94)) ) {
                    alt42=1;
                }


                switch (alt42) {
            	case 1 :
            	    // InternalOseeDsl.g:2912:1: (lv_accessRules_3_0= ruleObjectRestriction )
            	    {
            	    // InternalOseeDsl.g:2912:1: (lv_accessRules_3_0= ruleObjectRestriction )
            	    // InternalOseeDsl.g:2913:3: lv_accessRules_3_0= ruleObjectRestriction
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getHierarchyRestrictionAccess().getAccessRulesObjectRestrictionParserRuleCall_3_0()); 
            	    	    
            	    pushFollow(FOLLOW_60);
            	    lv_accessRules_3_0=ruleObjectRestriction();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getHierarchyRestrictionRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"accessRules",
            	            		lv_accessRules_3_0, 
            	            		"org.eclipse.osee.framework.core.dsl.OseeDsl.ObjectRestriction");
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

            otherlv_4=(Token)match(input,21,FOLLOW_2); 

                	newLeafNode(otherlv_4, grammarAccess.getHierarchyRestrictionAccess().getRightCurlyBracketKeyword_4());
                

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
    // $ANTLR end "ruleHierarchyRestriction"


    // $ANTLR start "entryRuleRelationTypeArtifactTypePredicate"
    // InternalOseeDsl.g:2941:1: entryRuleRelationTypeArtifactTypePredicate returns [EObject current=null] : iv_ruleRelationTypeArtifactTypePredicate= ruleRelationTypeArtifactTypePredicate EOF ;
    public final EObject entryRuleRelationTypeArtifactTypePredicate() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRelationTypeArtifactTypePredicate = null;


        try {
            // InternalOseeDsl.g:2942:2: (iv_ruleRelationTypeArtifactTypePredicate= ruleRelationTypeArtifactTypePredicate EOF )
            // InternalOseeDsl.g:2943:2: iv_ruleRelationTypeArtifactTypePredicate= ruleRelationTypeArtifactTypePredicate EOF
            {
             newCompositeNode(grammarAccess.getRelationTypeArtifactTypePredicateRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleRelationTypeArtifactTypePredicate=ruleRelationTypeArtifactTypePredicate();

            state._fsp--;

             current =iv_ruleRelationTypeArtifactTypePredicate; 
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
    // $ANTLR end "entryRuleRelationTypeArtifactTypePredicate"


    // $ANTLR start "ruleRelationTypeArtifactTypePredicate"
    // InternalOseeDsl.g:2950:1: ruleRelationTypeArtifactTypePredicate returns [EObject current=null] : (otherlv_0= 'artifactType' ( (otherlv_1= RULE_STRING ) ) ) ;
    public final EObject ruleRelationTypeArtifactTypePredicate() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;

         enterRule(); 
            
        try {
            // InternalOseeDsl.g:2953:28: ( (otherlv_0= 'artifactType' ( (otherlv_1= RULE_STRING ) ) ) )
            // InternalOseeDsl.g:2954:1: (otherlv_0= 'artifactType' ( (otherlv_1= RULE_STRING ) ) )
            {
            // InternalOseeDsl.g:2954:1: (otherlv_0= 'artifactType' ( (otherlv_1= RULE_STRING ) ) )
            // InternalOseeDsl.g:2954:3: otherlv_0= 'artifactType' ( (otherlv_1= RULE_STRING ) )
            {
            otherlv_0=(Token)match(input,15,FOLLOW_6); 

                	newLeafNode(otherlv_0, grammarAccess.getRelationTypeArtifactTypePredicateAccess().getArtifactTypeKeyword_0());
                
            // InternalOseeDsl.g:2958:1: ( (otherlv_1= RULE_STRING ) )
            // InternalOseeDsl.g:2959:1: (otherlv_1= RULE_STRING )
            {
            // InternalOseeDsl.g:2959:1: (otherlv_1= RULE_STRING )
            // InternalOseeDsl.g:2960:3: otherlv_1= RULE_STRING
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getRelationTypeArtifactTypePredicateRule());
            	        }
                    
            otherlv_1=(Token)match(input,RULE_STRING,FOLLOW_2); 

            		newLeafNode(otherlv_1, grammarAccess.getRelationTypeArtifactTypePredicateAccess().getArtifactTypeRefXArtifactTypeCrossReference_1_0()); 
            	

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
    // $ANTLR end "ruleRelationTypeArtifactTypePredicate"


    // $ANTLR start "entryRuleRelationTypeArtifactPredicate"
    // InternalOseeDsl.g:2979:1: entryRuleRelationTypeArtifactPredicate returns [EObject current=null] : iv_ruleRelationTypeArtifactPredicate= ruleRelationTypeArtifactPredicate EOF ;
    public final EObject entryRuleRelationTypeArtifactPredicate() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRelationTypeArtifactPredicate = null;


        try {
            // InternalOseeDsl.g:2980:2: (iv_ruleRelationTypeArtifactPredicate= ruleRelationTypeArtifactPredicate EOF )
            // InternalOseeDsl.g:2981:2: iv_ruleRelationTypeArtifactPredicate= ruleRelationTypeArtifactPredicate EOF
            {
             newCompositeNode(grammarAccess.getRelationTypeArtifactPredicateRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleRelationTypeArtifactPredicate=ruleRelationTypeArtifactPredicate();

            state._fsp--;

             current =iv_ruleRelationTypeArtifactPredicate; 
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
    // $ANTLR end "entryRuleRelationTypeArtifactPredicate"


    // $ANTLR start "ruleRelationTypeArtifactPredicate"
    // InternalOseeDsl.g:2988:1: ruleRelationTypeArtifactPredicate returns [EObject current=null] : (otherlv_0= 'artifact' ( (otherlv_1= RULE_STRING ) ) ) ;
    public final EObject ruleRelationTypeArtifactPredicate() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;

         enterRule(); 
            
        try {
            // InternalOseeDsl.g:2991:28: ( (otherlv_0= 'artifact' ( (otherlv_1= RULE_STRING ) ) ) )
            // InternalOseeDsl.g:2992:1: (otherlv_0= 'artifact' ( (otherlv_1= RULE_STRING ) ) )
            {
            // InternalOseeDsl.g:2992:1: (otherlv_0= 'artifact' ( (otherlv_1= RULE_STRING ) ) )
            // InternalOseeDsl.g:2992:3: otherlv_0= 'artifact' ( (otherlv_1= RULE_STRING ) )
            {
            otherlv_0=(Token)match(input,79,FOLLOW_6); 

                	newLeafNode(otherlv_0, grammarAccess.getRelationTypeArtifactPredicateAccess().getArtifactKeyword_0());
                
            // InternalOseeDsl.g:2996:1: ( (otherlv_1= RULE_STRING ) )
            // InternalOseeDsl.g:2997:1: (otherlv_1= RULE_STRING )
            {
            // InternalOseeDsl.g:2997:1: (otherlv_1= RULE_STRING )
            // InternalOseeDsl.g:2998:3: otherlv_1= RULE_STRING
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getRelationTypeArtifactPredicateRule());
            	        }
                    
            otherlv_1=(Token)match(input,RULE_STRING,FOLLOW_2); 

            		newLeafNode(otherlv_1, grammarAccess.getRelationTypeArtifactPredicateAccess().getArtifactMatcherRefXArtifactMatcherCrossReference_1_0()); 
            	

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
    // $ANTLR end "ruleRelationTypeArtifactPredicate"


    // $ANTLR start "entryRuleRelationTypePredicate"
    // InternalOseeDsl.g:3017:1: entryRuleRelationTypePredicate returns [EObject current=null] : iv_ruleRelationTypePredicate= ruleRelationTypePredicate EOF ;
    public final EObject entryRuleRelationTypePredicate() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRelationTypePredicate = null;


        try {
            // InternalOseeDsl.g:3018:2: (iv_ruleRelationTypePredicate= ruleRelationTypePredicate EOF )
            // InternalOseeDsl.g:3019:2: iv_ruleRelationTypePredicate= ruleRelationTypePredicate EOF
            {
             newCompositeNode(grammarAccess.getRelationTypePredicateRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleRelationTypePredicate=ruleRelationTypePredicate();

            state._fsp--;

             current =iv_ruleRelationTypePredicate; 
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
    // $ANTLR end "entryRuleRelationTypePredicate"


    // $ANTLR start "ruleRelationTypePredicate"
    // InternalOseeDsl.g:3026:1: ruleRelationTypePredicate returns [EObject current=null] : (this_RelationTypeArtifactPredicate_0= ruleRelationTypeArtifactPredicate | this_RelationTypeArtifactTypePredicate_1= ruleRelationTypeArtifactTypePredicate ) ;
    public final EObject ruleRelationTypePredicate() throws RecognitionException {
        EObject current = null;

        EObject this_RelationTypeArtifactPredicate_0 = null;

        EObject this_RelationTypeArtifactTypePredicate_1 = null;


         enterRule(); 
            
        try {
            // InternalOseeDsl.g:3029:28: ( (this_RelationTypeArtifactPredicate_0= ruleRelationTypeArtifactPredicate | this_RelationTypeArtifactTypePredicate_1= ruleRelationTypeArtifactTypePredicate ) )
            // InternalOseeDsl.g:3030:1: (this_RelationTypeArtifactPredicate_0= ruleRelationTypeArtifactPredicate | this_RelationTypeArtifactTypePredicate_1= ruleRelationTypeArtifactTypePredicate )
            {
            // InternalOseeDsl.g:3030:1: (this_RelationTypeArtifactPredicate_0= ruleRelationTypeArtifactPredicate | this_RelationTypeArtifactTypePredicate_1= ruleRelationTypeArtifactTypePredicate )
            int alt43=2;
            int LA43_0 = input.LA(1);

            if ( (LA43_0==79) ) {
                alt43=1;
            }
            else if ( (LA43_0==15) ) {
                alt43=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 43, 0, input);

                throw nvae;
            }
            switch (alt43) {
                case 1 :
                    // InternalOseeDsl.g:3031:5: this_RelationTypeArtifactPredicate_0= ruleRelationTypeArtifactPredicate
                    {
                     
                            newCompositeNode(grammarAccess.getRelationTypePredicateAccess().getRelationTypeArtifactPredicateParserRuleCall_0()); 
                        
                    pushFollow(FOLLOW_2);
                    this_RelationTypeArtifactPredicate_0=ruleRelationTypeArtifactPredicate();

                    state._fsp--;

                     
                            current = this_RelationTypeArtifactPredicate_0; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // InternalOseeDsl.g:3041:5: this_RelationTypeArtifactTypePredicate_1= ruleRelationTypeArtifactTypePredicate
                    {
                     
                            newCompositeNode(grammarAccess.getRelationTypePredicateAccess().getRelationTypeArtifactTypePredicateParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_2);
                    this_RelationTypeArtifactTypePredicate_1=ruleRelationTypeArtifactTypePredicate();

                    state._fsp--;

                     
                            current = this_RelationTypeArtifactTypePredicate_1; 
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
    // $ANTLR end "ruleRelationTypePredicate"


    // $ANTLR start "entryRuleObjectRestriction"
    // InternalOseeDsl.g:3057:1: entryRuleObjectRestriction returns [EObject current=null] : iv_ruleObjectRestriction= ruleObjectRestriction EOF ;
    public final EObject entryRuleObjectRestriction() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleObjectRestriction = null;


        try {
            // InternalOseeDsl.g:3058:2: (iv_ruleObjectRestriction= ruleObjectRestriction EOF )
            // InternalOseeDsl.g:3059:2: iv_ruleObjectRestriction= ruleObjectRestriction EOF
            {
             newCompositeNode(grammarAccess.getObjectRestrictionRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleObjectRestriction=ruleObjectRestriction();

            state._fsp--;

             current =iv_ruleObjectRestriction; 
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
    // $ANTLR end "entryRuleObjectRestriction"


    // $ANTLR start "ruleObjectRestriction"
    // InternalOseeDsl.g:3066:1: ruleObjectRestriction returns [EObject current=null] : (this_ArtifactMatchRestriction_0= ruleArtifactMatchRestriction | this_ArtifactTypeRestriction_1= ruleArtifactTypeRestriction | this_RelationTypeRestriction_2= ruleRelationTypeRestriction | this_AttributeTypeRestriction_3= ruleAttributeTypeRestriction ) ;
    public final EObject ruleObjectRestriction() throws RecognitionException {
        EObject current = null;

        EObject this_ArtifactMatchRestriction_0 = null;

        EObject this_ArtifactTypeRestriction_1 = null;

        EObject this_RelationTypeRestriction_2 = null;

        EObject this_AttributeTypeRestriction_3 = null;


         enterRule(); 
            
        try {
            // InternalOseeDsl.g:3069:28: ( (this_ArtifactMatchRestriction_0= ruleArtifactMatchRestriction | this_ArtifactTypeRestriction_1= ruleArtifactTypeRestriction | this_RelationTypeRestriction_2= ruleRelationTypeRestriction | this_AttributeTypeRestriction_3= ruleAttributeTypeRestriction ) )
            // InternalOseeDsl.g:3070:1: (this_ArtifactMatchRestriction_0= ruleArtifactMatchRestriction | this_ArtifactTypeRestriction_1= ruleArtifactTypeRestriction | this_RelationTypeRestriction_2= ruleRelationTypeRestriction | this_AttributeTypeRestriction_3= ruleAttributeTypeRestriction )
            {
            // InternalOseeDsl.g:3070:1: (this_ArtifactMatchRestriction_0= ruleArtifactMatchRestriction | this_ArtifactTypeRestriction_1= ruleArtifactTypeRestriction | this_RelationTypeRestriction_2= ruleRelationTypeRestriction | this_AttributeTypeRestriction_3= ruleAttributeTypeRestriction )
            int alt44=4;
            int LA44_0 = input.LA(1);

            if ( (LA44_0==93) ) {
                int LA44_1 = input.LA(2);

                if ( (LA44_1==80) ) {
                    switch ( input.LA(3) ) {
                    case 24:
                        {
                        alt44=4;
                        }
                        break;
                    case 61:
                        {
                        alt44=3;
                        }
                        break;
                    case 15:
                        {
                        alt44=2;
                        }
                        break;
                    case 79:
                        {
                        alt44=1;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("", 44, 3, input);

                        throw nvae;
                    }

                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 44, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA44_0==94) ) {
                int LA44_2 = input.LA(2);

                if ( (LA44_2==80) ) {
                    switch ( input.LA(3) ) {
                    case 24:
                        {
                        alt44=4;
                        }
                        break;
                    case 61:
                        {
                        alt44=3;
                        }
                        break;
                    case 15:
                        {
                        alt44=2;
                        }
                        break;
                    case 79:
                        {
                        alt44=1;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("", 44, 3, input);

                        throw nvae;
                    }

                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 44, 2, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 44, 0, input);

                throw nvae;
            }
            switch (alt44) {
                case 1 :
                    // InternalOseeDsl.g:3071:5: this_ArtifactMatchRestriction_0= ruleArtifactMatchRestriction
                    {
                     
                            newCompositeNode(grammarAccess.getObjectRestrictionAccess().getArtifactMatchRestrictionParserRuleCall_0()); 
                        
                    pushFollow(FOLLOW_2);
                    this_ArtifactMatchRestriction_0=ruleArtifactMatchRestriction();

                    state._fsp--;

                     
                            current = this_ArtifactMatchRestriction_0; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // InternalOseeDsl.g:3081:5: this_ArtifactTypeRestriction_1= ruleArtifactTypeRestriction
                    {
                     
                            newCompositeNode(grammarAccess.getObjectRestrictionAccess().getArtifactTypeRestrictionParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_2);
                    this_ArtifactTypeRestriction_1=ruleArtifactTypeRestriction();

                    state._fsp--;

                     
                            current = this_ArtifactTypeRestriction_1; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 3 :
                    // InternalOseeDsl.g:3091:5: this_RelationTypeRestriction_2= ruleRelationTypeRestriction
                    {
                     
                            newCompositeNode(grammarAccess.getObjectRestrictionAccess().getRelationTypeRestrictionParserRuleCall_2()); 
                        
                    pushFollow(FOLLOW_2);
                    this_RelationTypeRestriction_2=ruleRelationTypeRestriction();

                    state._fsp--;

                     
                            current = this_RelationTypeRestriction_2; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 4 :
                    // InternalOseeDsl.g:3101:5: this_AttributeTypeRestriction_3= ruleAttributeTypeRestriction
                    {
                     
                            newCompositeNode(grammarAccess.getObjectRestrictionAccess().getAttributeTypeRestrictionParserRuleCall_3()); 
                        
                    pushFollow(FOLLOW_2);
                    this_AttributeTypeRestriction_3=ruleAttributeTypeRestriction();

                    state._fsp--;

                     
                            current = this_AttributeTypeRestriction_3; 
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
    // $ANTLR end "ruleObjectRestriction"


    // $ANTLR start "entryRuleArtifactMatchRestriction"
    // InternalOseeDsl.g:3117:1: entryRuleArtifactMatchRestriction returns [EObject current=null] : iv_ruleArtifactMatchRestriction= ruleArtifactMatchRestriction EOF ;
    public final EObject entryRuleArtifactMatchRestriction() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleArtifactMatchRestriction = null;


        try {
            // InternalOseeDsl.g:3118:2: (iv_ruleArtifactMatchRestriction= ruleArtifactMatchRestriction EOF )
            // InternalOseeDsl.g:3119:2: iv_ruleArtifactMatchRestriction= ruleArtifactMatchRestriction EOF
            {
             newCompositeNode(grammarAccess.getArtifactMatchRestrictionRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleArtifactMatchRestriction=ruleArtifactMatchRestriction();

            state._fsp--;

             current =iv_ruleArtifactMatchRestriction; 
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
    // $ANTLR end "entryRuleArtifactMatchRestriction"


    // $ANTLR start "ruleArtifactMatchRestriction"
    // InternalOseeDsl.g:3126:1: ruleArtifactMatchRestriction returns [EObject current=null] : ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'artifact' ( (otherlv_3= RULE_STRING ) ) otherlv_4= ';' ) ;
    public final EObject ruleArtifactMatchRestriction() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_2=null;
        Token otherlv_3=null;
        Token otherlv_4=null;
        Enumerator lv_permission_0_0 = null;


         enterRule(); 
            
        try {
            // InternalOseeDsl.g:3129:28: ( ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'artifact' ( (otherlv_3= RULE_STRING ) ) otherlv_4= ';' ) )
            // InternalOseeDsl.g:3130:1: ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'artifact' ( (otherlv_3= RULE_STRING ) ) otherlv_4= ';' )
            {
            // InternalOseeDsl.g:3130:1: ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'artifact' ( (otherlv_3= RULE_STRING ) ) otherlv_4= ';' )
            // InternalOseeDsl.g:3130:2: ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'artifact' ( (otherlv_3= RULE_STRING ) ) otherlv_4= ';'
            {
            // InternalOseeDsl.g:3130:2: ( (lv_permission_0_0= ruleAccessPermissionEnum ) )
            // InternalOseeDsl.g:3131:1: (lv_permission_0_0= ruleAccessPermissionEnum )
            {
            // InternalOseeDsl.g:3131:1: (lv_permission_0_0= ruleAccessPermissionEnum )
            // InternalOseeDsl.g:3132:3: lv_permission_0_0= ruleAccessPermissionEnum
            {
             
            	        newCompositeNode(grammarAccess.getArtifactMatchRestrictionAccess().getPermissionAccessPermissionEnumEnumRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_61);
            lv_permission_0_0=ruleAccessPermissionEnum();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getArtifactMatchRestrictionRule());
            	        }
                   		set(
                   			current, 
                   			"permission",
                    		lv_permission_0_0, 
                    		"org.eclipse.osee.framework.core.dsl.OseeDsl.AccessPermissionEnum");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            otherlv_1=(Token)match(input,80,FOLLOW_62); 

                	newLeafNode(otherlv_1, grammarAccess.getArtifactMatchRestrictionAccess().getEditKeyword_1());
                
            otherlv_2=(Token)match(input,79,FOLLOW_6); 

                	newLeafNode(otherlv_2, grammarAccess.getArtifactMatchRestrictionAccess().getArtifactKeyword_2());
                
            // InternalOseeDsl.g:3156:1: ( (otherlv_3= RULE_STRING ) )
            // InternalOseeDsl.g:3157:1: (otherlv_3= RULE_STRING )
            {
            // InternalOseeDsl.g:3157:1: (otherlv_3= RULE_STRING )
            // InternalOseeDsl.g:3158:3: otherlv_3= RULE_STRING
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getArtifactMatchRestrictionRule());
            	        }
                    
            otherlv_3=(Token)match(input,RULE_STRING,FOLLOW_55); 

            		newLeafNode(otherlv_3, grammarAccess.getArtifactMatchRestrictionAccess().getArtifactMatcherRefXArtifactMatcherCrossReference_3_0()); 
            	

            }


            }

            otherlv_4=(Token)match(input,75,FOLLOW_2); 

                	newLeafNode(otherlv_4, grammarAccess.getArtifactMatchRestrictionAccess().getSemicolonKeyword_4());
                

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
    // $ANTLR end "ruleArtifactMatchRestriction"


    // $ANTLR start "entryRuleArtifactTypeRestriction"
    // InternalOseeDsl.g:3181:1: entryRuleArtifactTypeRestriction returns [EObject current=null] : iv_ruleArtifactTypeRestriction= ruleArtifactTypeRestriction EOF ;
    public final EObject entryRuleArtifactTypeRestriction() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleArtifactTypeRestriction = null;


        try {
            // InternalOseeDsl.g:3182:2: (iv_ruleArtifactTypeRestriction= ruleArtifactTypeRestriction EOF )
            // InternalOseeDsl.g:3183:2: iv_ruleArtifactTypeRestriction= ruleArtifactTypeRestriction EOF
            {
             newCompositeNode(grammarAccess.getArtifactTypeRestrictionRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleArtifactTypeRestriction=ruleArtifactTypeRestriction();

            state._fsp--;

             current =iv_ruleArtifactTypeRestriction; 
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
    // $ANTLR end "entryRuleArtifactTypeRestriction"


    // $ANTLR start "ruleArtifactTypeRestriction"
    // InternalOseeDsl.g:3190:1: ruleArtifactTypeRestriction returns [EObject current=null] : ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'artifactType' ( (otherlv_3= RULE_STRING ) ) otherlv_4= ';' ) ;
    public final EObject ruleArtifactTypeRestriction() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_2=null;
        Token otherlv_3=null;
        Token otherlv_4=null;
        Enumerator lv_permission_0_0 = null;


         enterRule(); 
            
        try {
            // InternalOseeDsl.g:3193:28: ( ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'artifactType' ( (otherlv_3= RULE_STRING ) ) otherlv_4= ';' ) )
            // InternalOseeDsl.g:3194:1: ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'artifactType' ( (otherlv_3= RULE_STRING ) ) otherlv_4= ';' )
            {
            // InternalOseeDsl.g:3194:1: ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'artifactType' ( (otherlv_3= RULE_STRING ) ) otherlv_4= ';' )
            // InternalOseeDsl.g:3194:2: ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'artifactType' ( (otherlv_3= RULE_STRING ) ) otherlv_4= ';'
            {
            // InternalOseeDsl.g:3194:2: ( (lv_permission_0_0= ruleAccessPermissionEnum ) )
            // InternalOseeDsl.g:3195:1: (lv_permission_0_0= ruleAccessPermissionEnum )
            {
            // InternalOseeDsl.g:3195:1: (lv_permission_0_0= ruleAccessPermissionEnum )
            // InternalOseeDsl.g:3196:3: lv_permission_0_0= ruleAccessPermissionEnum
            {
             
            	        newCompositeNode(grammarAccess.getArtifactTypeRestrictionAccess().getPermissionAccessPermissionEnumEnumRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_61);
            lv_permission_0_0=ruleAccessPermissionEnum();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getArtifactTypeRestrictionRule());
            	        }
                   		set(
                   			current, 
                   			"permission",
                    		lv_permission_0_0, 
                    		"org.eclipse.osee.framework.core.dsl.OseeDsl.AccessPermissionEnum");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            otherlv_1=(Token)match(input,80,FOLLOW_9); 

                	newLeafNode(otherlv_1, grammarAccess.getArtifactTypeRestrictionAccess().getEditKeyword_1());
                
            otherlv_2=(Token)match(input,15,FOLLOW_6); 

                	newLeafNode(otherlv_2, grammarAccess.getArtifactTypeRestrictionAccess().getArtifactTypeKeyword_2());
                
            // InternalOseeDsl.g:3220:1: ( (otherlv_3= RULE_STRING ) )
            // InternalOseeDsl.g:3221:1: (otherlv_3= RULE_STRING )
            {
            // InternalOseeDsl.g:3221:1: (otherlv_3= RULE_STRING )
            // InternalOseeDsl.g:3222:3: otherlv_3= RULE_STRING
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getArtifactTypeRestrictionRule());
            	        }
                    
            otherlv_3=(Token)match(input,RULE_STRING,FOLLOW_55); 

            		newLeafNode(otherlv_3, grammarAccess.getArtifactTypeRestrictionAccess().getArtifactTypeRefXArtifactTypeCrossReference_3_0()); 
            	

            }


            }

            otherlv_4=(Token)match(input,75,FOLLOW_2); 

                	newLeafNode(otherlv_4, grammarAccess.getArtifactTypeRestrictionAccess().getSemicolonKeyword_4());
                

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
    // $ANTLR end "ruleArtifactTypeRestriction"


    // $ANTLR start "entryRuleAttributeTypeRestriction"
    // InternalOseeDsl.g:3245:1: entryRuleAttributeTypeRestriction returns [EObject current=null] : iv_ruleAttributeTypeRestriction= ruleAttributeTypeRestriction EOF ;
    public final EObject entryRuleAttributeTypeRestriction() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttributeTypeRestriction = null;


        try {
            // InternalOseeDsl.g:3246:2: (iv_ruleAttributeTypeRestriction= ruleAttributeTypeRestriction EOF )
            // InternalOseeDsl.g:3247:2: iv_ruleAttributeTypeRestriction= ruleAttributeTypeRestriction EOF
            {
             newCompositeNode(grammarAccess.getAttributeTypeRestrictionRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleAttributeTypeRestriction=ruleAttributeTypeRestriction();

            state._fsp--;

             current =iv_ruleAttributeTypeRestriction; 
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
    // $ANTLR end "entryRuleAttributeTypeRestriction"


    // $ANTLR start "ruleAttributeTypeRestriction"
    // InternalOseeDsl.g:3254:1: ruleAttributeTypeRestriction returns [EObject current=null] : ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'attributeType' ( (otherlv_3= RULE_STRING ) ) (otherlv_4= 'of' otherlv_5= 'artifactType' ( (otherlv_6= RULE_STRING ) ) )? otherlv_7= ';' ) ;
    public final EObject ruleAttributeTypeRestriction() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_2=null;
        Token otherlv_3=null;
        Token otherlv_4=null;
        Token otherlv_5=null;
        Token otherlv_6=null;
        Token otherlv_7=null;
        Enumerator lv_permission_0_0 = null;


         enterRule(); 
            
        try {
            // InternalOseeDsl.g:3257:28: ( ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'attributeType' ( (otherlv_3= RULE_STRING ) ) (otherlv_4= 'of' otherlv_5= 'artifactType' ( (otherlv_6= RULE_STRING ) ) )? otherlv_7= ';' ) )
            // InternalOseeDsl.g:3258:1: ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'attributeType' ( (otherlv_3= RULE_STRING ) ) (otherlv_4= 'of' otherlv_5= 'artifactType' ( (otherlv_6= RULE_STRING ) ) )? otherlv_7= ';' )
            {
            // InternalOseeDsl.g:3258:1: ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'attributeType' ( (otherlv_3= RULE_STRING ) ) (otherlv_4= 'of' otherlv_5= 'artifactType' ( (otherlv_6= RULE_STRING ) ) )? otherlv_7= ';' )
            // InternalOseeDsl.g:3258:2: ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'attributeType' ( (otherlv_3= RULE_STRING ) ) (otherlv_4= 'of' otherlv_5= 'artifactType' ( (otherlv_6= RULE_STRING ) ) )? otherlv_7= ';'
            {
            // InternalOseeDsl.g:3258:2: ( (lv_permission_0_0= ruleAccessPermissionEnum ) )
            // InternalOseeDsl.g:3259:1: (lv_permission_0_0= ruleAccessPermissionEnum )
            {
            // InternalOseeDsl.g:3259:1: (lv_permission_0_0= ruleAccessPermissionEnum )
            // InternalOseeDsl.g:3260:3: lv_permission_0_0= ruleAccessPermissionEnum
            {
             
            	        newCompositeNode(grammarAccess.getAttributeTypeRestrictionAccess().getPermissionAccessPermissionEnumEnumRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_61);
            lv_permission_0_0=ruleAccessPermissionEnum();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getAttributeTypeRestrictionRule());
            	        }
                   		set(
                   			current, 
                   			"permission",
                    		lv_permission_0_0, 
                    		"org.eclipse.osee.framework.core.dsl.OseeDsl.AccessPermissionEnum");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            otherlv_1=(Token)match(input,80,FOLLOW_63); 

                	newLeafNode(otherlv_1, grammarAccess.getAttributeTypeRestrictionAccess().getEditKeyword_1());
                
            otherlv_2=(Token)match(input,24,FOLLOW_6); 

                	newLeafNode(otherlv_2, grammarAccess.getAttributeTypeRestrictionAccess().getAttributeTypeKeyword_2());
                
            // InternalOseeDsl.g:3284:1: ( (otherlv_3= RULE_STRING ) )
            // InternalOseeDsl.g:3285:1: (otherlv_3= RULE_STRING )
            {
            // InternalOseeDsl.g:3285:1: (otherlv_3= RULE_STRING )
            // InternalOseeDsl.g:3286:3: otherlv_3= RULE_STRING
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getAttributeTypeRestrictionRule());
            	        }
                    
            otherlv_3=(Token)match(input,RULE_STRING,FOLLOW_64); 

            		newLeafNode(otherlv_3, grammarAccess.getAttributeTypeRestrictionAccess().getAttributeTypeRefXAttributeTypeCrossReference_3_0()); 
            	

            }


            }

            // InternalOseeDsl.g:3297:2: (otherlv_4= 'of' otherlv_5= 'artifactType' ( (otherlv_6= RULE_STRING ) ) )?
            int alt45=2;
            int LA45_0 = input.LA(1);

            if ( (LA45_0==81) ) {
                alt45=1;
            }
            switch (alt45) {
                case 1 :
                    // InternalOseeDsl.g:3297:4: otherlv_4= 'of' otherlv_5= 'artifactType' ( (otherlv_6= RULE_STRING ) )
                    {
                    otherlv_4=(Token)match(input,81,FOLLOW_9); 

                        	newLeafNode(otherlv_4, grammarAccess.getAttributeTypeRestrictionAccess().getOfKeyword_4_0());
                        
                    otherlv_5=(Token)match(input,15,FOLLOW_6); 

                        	newLeafNode(otherlv_5, grammarAccess.getAttributeTypeRestrictionAccess().getArtifactTypeKeyword_4_1());
                        
                    // InternalOseeDsl.g:3305:1: ( (otherlv_6= RULE_STRING ) )
                    // InternalOseeDsl.g:3306:1: (otherlv_6= RULE_STRING )
                    {
                    // InternalOseeDsl.g:3306:1: (otherlv_6= RULE_STRING )
                    // InternalOseeDsl.g:3307:3: otherlv_6= RULE_STRING
                    {

                    			if (current==null) {
                    	            current = createModelElement(grammarAccess.getAttributeTypeRestrictionRule());
                    	        }
                            
                    otherlv_6=(Token)match(input,RULE_STRING,FOLLOW_55); 

                    		newLeafNode(otherlv_6, grammarAccess.getAttributeTypeRestrictionAccess().getArtifactTypeRefXArtifactTypeCrossReference_4_2_0()); 
                    	

                    }


                    }


                    }
                    break;

            }

            otherlv_7=(Token)match(input,75,FOLLOW_2); 

                	newLeafNode(otherlv_7, grammarAccess.getAttributeTypeRestrictionAccess().getSemicolonKeyword_5());
                

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
    // $ANTLR end "ruleAttributeTypeRestriction"


    // $ANTLR start "entryRuleRelationTypeRestriction"
    // InternalOseeDsl.g:3332:1: entryRuleRelationTypeRestriction returns [EObject current=null] : iv_ruleRelationTypeRestriction= ruleRelationTypeRestriction EOF ;
    public final EObject entryRuleRelationTypeRestriction() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRelationTypeRestriction = null;


        try {
            // InternalOseeDsl.g:3333:2: (iv_ruleRelationTypeRestriction= ruleRelationTypeRestriction EOF )
            // InternalOseeDsl.g:3334:2: iv_ruleRelationTypeRestriction= ruleRelationTypeRestriction EOF
            {
             newCompositeNode(grammarAccess.getRelationTypeRestrictionRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleRelationTypeRestriction=ruleRelationTypeRestriction();

            state._fsp--;

             current =iv_ruleRelationTypeRestriction; 
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
    // $ANTLR end "entryRuleRelationTypeRestriction"


    // $ANTLR start "ruleRelationTypeRestriction"
    // InternalOseeDsl.g:3341:1: ruleRelationTypeRestriction returns [EObject current=null] : ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'relationType' ( ( (lv_relationTypeMatch_3_0= ruleRelationTypeMatch ) ) | ( (otherlv_4= RULE_STRING ) ) ) ( (lv_restrictedToSide_5_0= ruleXRelationSideEnum ) ) ( (lv_predicate_6_0= ruleRelationTypePredicate ) )? otherlv_7= ';' ) ;
    public final EObject ruleRelationTypeRestriction() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_2=null;
        Token otherlv_4=null;
        Token otherlv_7=null;
        Enumerator lv_permission_0_0 = null;

        Enumerator lv_relationTypeMatch_3_0 = null;

        Enumerator lv_restrictedToSide_5_0 = null;

        EObject lv_predicate_6_0 = null;


         enterRule(); 
            
        try {
            // InternalOseeDsl.g:3344:28: ( ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'relationType' ( ( (lv_relationTypeMatch_3_0= ruleRelationTypeMatch ) ) | ( (otherlv_4= RULE_STRING ) ) ) ( (lv_restrictedToSide_5_0= ruleXRelationSideEnum ) ) ( (lv_predicate_6_0= ruleRelationTypePredicate ) )? otherlv_7= ';' ) )
            // InternalOseeDsl.g:3345:1: ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'relationType' ( ( (lv_relationTypeMatch_3_0= ruleRelationTypeMatch ) ) | ( (otherlv_4= RULE_STRING ) ) ) ( (lv_restrictedToSide_5_0= ruleXRelationSideEnum ) ) ( (lv_predicate_6_0= ruleRelationTypePredicate ) )? otherlv_7= ';' )
            {
            // InternalOseeDsl.g:3345:1: ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'relationType' ( ( (lv_relationTypeMatch_3_0= ruleRelationTypeMatch ) ) | ( (otherlv_4= RULE_STRING ) ) ) ( (lv_restrictedToSide_5_0= ruleXRelationSideEnum ) ) ( (lv_predicate_6_0= ruleRelationTypePredicate ) )? otherlv_7= ';' )
            // InternalOseeDsl.g:3345:2: ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'relationType' ( ( (lv_relationTypeMatch_3_0= ruleRelationTypeMatch ) ) | ( (otherlv_4= RULE_STRING ) ) ) ( (lv_restrictedToSide_5_0= ruleXRelationSideEnum ) ) ( (lv_predicate_6_0= ruleRelationTypePredicate ) )? otherlv_7= ';'
            {
            // InternalOseeDsl.g:3345:2: ( (lv_permission_0_0= ruleAccessPermissionEnum ) )
            // InternalOseeDsl.g:3346:1: (lv_permission_0_0= ruleAccessPermissionEnum )
            {
            // InternalOseeDsl.g:3346:1: (lv_permission_0_0= ruleAccessPermissionEnum )
            // InternalOseeDsl.g:3347:3: lv_permission_0_0= ruleAccessPermissionEnum
            {
             
            	        newCompositeNode(grammarAccess.getRelationTypeRestrictionAccess().getPermissionAccessPermissionEnumEnumRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_61);
            lv_permission_0_0=ruleAccessPermissionEnum();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getRelationTypeRestrictionRule());
            	        }
                   		set(
                   			current, 
                   			"permission",
                    		lv_permission_0_0, 
                    		"org.eclipse.osee.framework.core.dsl.OseeDsl.AccessPermissionEnum");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            otherlv_1=(Token)match(input,80,FOLLOW_65); 

                	newLeafNode(otherlv_1, grammarAccess.getRelationTypeRestrictionAccess().getEditKeyword_1());
                
            otherlv_2=(Token)match(input,61,FOLLOW_66); 

                	newLeafNode(otherlv_2, grammarAccess.getRelationTypeRestrictionAccess().getRelationTypeKeyword_2());
                
            // InternalOseeDsl.g:3371:1: ( ( (lv_relationTypeMatch_3_0= ruleRelationTypeMatch ) ) | ( (otherlv_4= RULE_STRING ) ) )
            int alt46=2;
            int LA46_0 = input.LA(1);

            if ( (LA46_0==95) ) {
                alt46=1;
            }
            else if ( (LA46_0==RULE_STRING) ) {
                alt46=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 46, 0, input);

                throw nvae;
            }
            switch (alt46) {
                case 1 :
                    // InternalOseeDsl.g:3371:2: ( (lv_relationTypeMatch_3_0= ruleRelationTypeMatch ) )
                    {
                    // InternalOseeDsl.g:3371:2: ( (lv_relationTypeMatch_3_0= ruleRelationTypeMatch ) )
                    // InternalOseeDsl.g:3372:1: (lv_relationTypeMatch_3_0= ruleRelationTypeMatch )
                    {
                    // InternalOseeDsl.g:3372:1: (lv_relationTypeMatch_3_0= ruleRelationTypeMatch )
                    // InternalOseeDsl.g:3373:3: lv_relationTypeMatch_3_0= ruleRelationTypeMatch
                    {
                     
                    	        newCompositeNode(grammarAccess.getRelationTypeRestrictionAccess().getRelationTypeMatchRelationTypeMatchEnumRuleCall_3_0_0()); 
                    	    
                    pushFollow(FOLLOW_67);
                    lv_relationTypeMatch_3_0=ruleRelationTypeMatch();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getRelationTypeRestrictionRule());
                    	        }
                           		set(
                           			current, 
                           			"relationTypeMatch",
                            		true, 
                            		"org.eclipse.osee.framework.core.dsl.OseeDsl.RelationTypeMatch");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }


                    }
                    break;
                case 2 :
                    // InternalOseeDsl.g:3390:6: ( (otherlv_4= RULE_STRING ) )
                    {
                    // InternalOseeDsl.g:3390:6: ( (otherlv_4= RULE_STRING ) )
                    // InternalOseeDsl.g:3391:1: (otherlv_4= RULE_STRING )
                    {
                    // InternalOseeDsl.g:3391:1: (otherlv_4= RULE_STRING )
                    // InternalOseeDsl.g:3392:3: otherlv_4= RULE_STRING
                    {

                    			if (current==null) {
                    	            current = createModelElement(grammarAccess.getRelationTypeRestrictionRule());
                    	        }
                            
                    otherlv_4=(Token)match(input,RULE_STRING,FOLLOW_67); 

                    		newLeafNode(otherlv_4, grammarAccess.getRelationTypeRestrictionAccess().getRelationTypeRefXRelationTypeCrossReference_3_1_0()); 
                    	

                    }


                    }


                    }
                    break;

            }

            // InternalOseeDsl.g:3403:3: ( (lv_restrictedToSide_5_0= ruleXRelationSideEnum ) )
            // InternalOseeDsl.g:3404:1: (lv_restrictedToSide_5_0= ruleXRelationSideEnum )
            {
            // InternalOseeDsl.g:3404:1: (lv_restrictedToSide_5_0= ruleXRelationSideEnum )
            // InternalOseeDsl.g:3405:3: lv_restrictedToSide_5_0= ruleXRelationSideEnum
            {
             
            	        newCompositeNode(grammarAccess.getRelationTypeRestrictionAccess().getRestrictedToSideXRelationSideEnumEnumRuleCall_4_0()); 
            	    
            pushFollow(FOLLOW_68);
            lv_restrictedToSide_5_0=ruleXRelationSideEnum();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getRelationTypeRestrictionRule());
            	        }
                   		set(
                   			current, 
                   			"restrictedToSide",
                    		lv_restrictedToSide_5_0, 
                    		"org.eclipse.osee.framework.core.dsl.OseeDsl.XRelationSideEnum");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // InternalOseeDsl.g:3421:2: ( (lv_predicate_6_0= ruleRelationTypePredicate ) )?
            int alt47=2;
            int LA47_0 = input.LA(1);

            if ( (LA47_0==15||LA47_0==79) ) {
                alt47=1;
            }
            switch (alt47) {
                case 1 :
                    // InternalOseeDsl.g:3422:1: (lv_predicate_6_0= ruleRelationTypePredicate )
                    {
                    // InternalOseeDsl.g:3422:1: (lv_predicate_6_0= ruleRelationTypePredicate )
                    // InternalOseeDsl.g:3423:3: lv_predicate_6_0= ruleRelationTypePredicate
                    {
                     
                    	        newCompositeNode(grammarAccess.getRelationTypeRestrictionAccess().getPredicateRelationTypePredicateParserRuleCall_5_0()); 
                    	    
                    pushFollow(FOLLOW_55);
                    lv_predicate_6_0=ruleRelationTypePredicate();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getRelationTypeRestrictionRule());
                    	        }
                           		set(
                           			current, 
                           			"predicate",
                            		lv_predicate_6_0, 
                            		"org.eclipse.osee.framework.core.dsl.OseeDsl.RelationTypePredicate");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }
                    break;

            }

            otherlv_7=(Token)match(input,75,FOLLOW_2); 

                	newLeafNode(otherlv_7, grammarAccess.getRelationTypeRestrictionAccess().getSemicolonKeyword_6());
                

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
    // $ANTLR end "ruleRelationTypeRestriction"


    // $ANTLR start "ruleRelationMultiplicityEnum"
    // InternalOseeDsl.g:3451:1: ruleRelationMultiplicityEnum returns [Enumerator current=null] : ( (enumLiteral_0= 'ONE_TO_ONE' ) | (enumLiteral_1= 'ONE_TO_MANY' ) | (enumLiteral_2= 'MANY_TO_ONE' ) | (enumLiteral_3= 'MANY_TO_MANY' ) ) ;
    public final Enumerator ruleRelationMultiplicityEnum() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;
        Token enumLiteral_2=null;
        Token enumLiteral_3=null;

         enterRule(); 
        try {
            // InternalOseeDsl.g:3453:28: ( ( (enumLiteral_0= 'ONE_TO_ONE' ) | (enumLiteral_1= 'ONE_TO_MANY' ) | (enumLiteral_2= 'MANY_TO_ONE' ) | (enumLiteral_3= 'MANY_TO_MANY' ) ) )
            // InternalOseeDsl.g:3454:1: ( (enumLiteral_0= 'ONE_TO_ONE' ) | (enumLiteral_1= 'ONE_TO_MANY' ) | (enumLiteral_2= 'MANY_TO_ONE' ) | (enumLiteral_3= 'MANY_TO_MANY' ) )
            {
            // InternalOseeDsl.g:3454:1: ( (enumLiteral_0= 'ONE_TO_ONE' ) | (enumLiteral_1= 'ONE_TO_MANY' ) | (enumLiteral_2= 'MANY_TO_ONE' ) | (enumLiteral_3= 'MANY_TO_MANY' ) )
            int alt48=4;
            switch ( input.LA(1) ) {
            case 82:
                {
                alt48=1;
                }
                break;
            case 83:
                {
                alt48=2;
                }
                break;
            case 84:
                {
                alt48=3;
                }
                break;
            case 85:
                {
                alt48=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 48, 0, input);

                throw nvae;
            }

            switch (alt48) {
                case 1 :
                    // InternalOseeDsl.g:3454:2: (enumLiteral_0= 'ONE_TO_ONE' )
                    {
                    // InternalOseeDsl.g:3454:2: (enumLiteral_0= 'ONE_TO_ONE' )
                    // InternalOseeDsl.g:3454:4: enumLiteral_0= 'ONE_TO_ONE'
                    {
                    enumLiteral_0=(Token)match(input,82,FOLLOW_2); 

                            current = grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_ONEEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_0, grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_ONEEnumLiteralDeclaration_0()); 
                        

                    }


                    }
                    break;
                case 2 :
                    // InternalOseeDsl.g:3460:6: (enumLiteral_1= 'ONE_TO_MANY' )
                    {
                    // InternalOseeDsl.g:3460:6: (enumLiteral_1= 'ONE_TO_MANY' )
                    // InternalOseeDsl.g:3460:8: enumLiteral_1= 'ONE_TO_MANY'
                    {
                    enumLiteral_1=(Token)match(input,83,FOLLOW_2); 

                            current = grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_MANYEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_1, grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_MANYEnumLiteralDeclaration_1()); 
                        

                    }


                    }
                    break;
                case 3 :
                    // InternalOseeDsl.g:3466:6: (enumLiteral_2= 'MANY_TO_ONE' )
                    {
                    // InternalOseeDsl.g:3466:6: (enumLiteral_2= 'MANY_TO_ONE' )
                    // InternalOseeDsl.g:3466:8: enumLiteral_2= 'MANY_TO_ONE'
                    {
                    enumLiteral_2=(Token)match(input,84,FOLLOW_2); 

                            current = grammarAccess.getRelationMultiplicityEnumAccess().getMANY_TO_ONEEnumLiteralDeclaration_2().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_2, grammarAccess.getRelationMultiplicityEnumAccess().getMANY_TO_ONEEnumLiteralDeclaration_2()); 
                        

                    }


                    }
                    break;
                case 4 :
                    // InternalOseeDsl.g:3472:6: (enumLiteral_3= 'MANY_TO_MANY' )
                    {
                    // InternalOseeDsl.g:3472:6: (enumLiteral_3= 'MANY_TO_MANY' )
                    // InternalOseeDsl.g:3472:8: enumLiteral_3= 'MANY_TO_MANY'
                    {
                    enumLiteral_3=(Token)match(input,85,FOLLOW_2); 

                            current = grammarAccess.getRelationMultiplicityEnumAccess().getMANY_TO_MANYEnumLiteralDeclaration_3().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_3, grammarAccess.getRelationMultiplicityEnumAccess().getMANY_TO_MANYEnumLiteralDeclaration_3()); 
                        

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
    // $ANTLR end "ruleRelationMultiplicityEnum"


    // $ANTLR start "ruleCompareOp"
    // InternalOseeDsl.g:3482:1: ruleCompareOp returns [Enumerator current=null] : ( (enumLiteral_0= 'EQ' ) | (enumLiteral_1= 'LIKE' ) ) ;
    public final Enumerator ruleCompareOp() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;

         enterRule(); 
        try {
            // InternalOseeDsl.g:3484:28: ( ( (enumLiteral_0= 'EQ' ) | (enumLiteral_1= 'LIKE' ) ) )
            // InternalOseeDsl.g:3485:1: ( (enumLiteral_0= 'EQ' ) | (enumLiteral_1= 'LIKE' ) )
            {
            // InternalOseeDsl.g:3485:1: ( (enumLiteral_0= 'EQ' ) | (enumLiteral_1= 'LIKE' ) )
            int alt49=2;
            int LA49_0 = input.LA(1);

            if ( (LA49_0==86) ) {
                alt49=1;
            }
            else if ( (LA49_0==87) ) {
                alt49=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 49, 0, input);

                throw nvae;
            }
            switch (alt49) {
                case 1 :
                    // InternalOseeDsl.g:3485:2: (enumLiteral_0= 'EQ' )
                    {
                    // InternalOseeDsl.g:3485:2: (enumLiteral_0= 'EQ' )
                    // InternalOseeDsl.g:3485:4: enumLiteral_0= 'EQ'
                    {
                    enumLiteral_0=(Token)match(input,86,FOLLOW_2); 

                            current = grammarAccess.getCompareOpAccess().getEQEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_0, grammarAccess.getCompareOpAccess().getEQEnumLiteralDeclaration_0()); 
                        

                    }


                    }
                    break;
                case 2 :
                    // InternalOseeDsl.g:3491:6: (enumLiteral_1= 'LIKE' )
                    {
                    // InternalOseeDsl.g:3491:6: (enumLiteral_1= 'LIKE' )
                    // InternalOseeDsl.g:3491:8: enumLiteral_1= 'LIKE'
                    {
                    enumLiteral_1=(Token)match(input,87,FOLLOW_2); 

                            current = grammarAccess.getCompareOpAccess().getLIKEEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_1, grammarAccess.getCompareOpAccess().getLIKEEnumLiteralDeclaration_1()); 
                        

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
    // $ANTLR end "ruleCompareOp"


    // $ANTLR start "ruleXLogicOperator"
    // InternalOseeDsl.g:3501:1: ruleXLogicOperator returns [Enumerator current=null] : ( (enumLiteral_0= 'AND' ) | (enumLiteral_1= 'OR' ) ) ;
    public final Enumerator ruleXLogicOperator() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;

         enterRule(); 
        try {
            // InternalOseeDsl.g:3503:28: ( ( (enumLiteral_0= 'AND' ) | (enumLiteral_1= 'OR' ) ) )
            // InternalOseeDsl.g:3504:1: ( (enumLiteral_0= 'AND' ) | (enumLiteral_1= 'OR' ) )
            {
            // InternalOseeDsl.g:3504:1: ( (enumLiteral_0= 'AND' ) | (enumLiteral_1= 'OR' ) )
            int alt50=2;
            int LA50_0 = input.LA(1);

            if ( (LA50_0==88) ) {
                alt50=1;
            }
            else if ( (LA50_0==89) ) {
                alt50=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 50, 0, input);

                throw nvae;
            }
            switch (alt50) {
                case 1 :
                    // InternalOseeDsl.g:3504:2: (enumLiteral_0= 'AND' )
                    {
                    // InternalOseeDsl.g:3504:2: (enumLiteral_0= 'AND' )
                    // InternalOseeDsl.g:3504:4: enumLiteral_0= 'AND'
                    {
                    enumLiteral_0=(Token)match(input,88,FOLLOW_2); 

                            current = grammarAccess.getXLogicOperatorAccess().getANDEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_0, grammarAccess.getXLogicOperatorAccess().getANDEnumLiteralDeclaration_0()); 
                        

                    }


                    }
                    break;
                case 2 :
                    // InternalOseeDsl.g:3510:6: (enumLiteral_1= 'OR' )
                    {
                    // InternalOseeDsl.g:3510:6: (enumLiteral_1= 'OR' )
                    // InternalOseeDsl.g:3510:8: enumLiteral_1= 'OR'
                    {
                    enumLiteral_1=(Token)match(input,89,FOLLOW_2); 

                            current = grammarAccess.getXLogicOperatorAccess().getOREnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_1, grammarAccess.getXLogicOperatorAccess().getOREnumLiteralDeclaration_1()); 
                        

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
    // $ANTLR end "ruleXLogicOperator"


    // $ANTLR start "ruleMatchField"
    // InternalOseeDsl.g:3520:1: ruleMatchField returns [Enumerator current=null] : ( (enumLiteral_0= 'artifactName' ) | (enumLiteral_1= 'artifactGuid' ) | (enumLiteral_2= 'branchName' ) | (enumLiteral_3= 'branchUuid' ) ) ;
    public final Enumerator ruleMatchField() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;
        Token enumLiteral_2=null;
        Token enumLiteral_3=null;

         enterRule(); 
        try {
            // InternalOseeDsl.g:3522:28: ( ( (enumLiteral_0= 'artifactName' ) | (enumLiteral_1= 'artifactGuid' ) | (enumLiteral_2= 'branchName' ) | (enumLiteral_3= 'branchUuid' ) ) )
            // InternalOseeDsl.g:3523:1: ( (enumLiteral_0= 'artifactName' ) | (enumLiteral_1= 'artifactGuid' ) | (enumLiteral_2= 'branchName' ) | (enumLiteral_3= 'branchUuid' ) )
            {
            // InternalOseeDsl.g:3523:1: ( (enumLiteral_0= 'artifactName' ) | (enumLiteral_1= 'artifactGuid' ) | (enumLiteral_2= 'branchName' ) | (enumLiteral_3= 'branchUuid' ) )
            int alt51=4;
            switch ( input.LA(1) ) {
            case 90:
                {
                alt51=1;
                }
                break;
            case 91:
                {
                alt51=2;
                }
                break;
            case 92:
                {
                alt51=3;
                }
                break;
            case 23:
                {
                alt51=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 51, 0, input);

                throw nvae;
            }

            switch (alt51) {
                case 1 :
                    // InternalOseeDsl.g:3523:2: (enumLiteral_0= 'artifactName' )
                    {
                    // InternalOseeDsl.g:3523:2: (enumLiteral_0= 'artifactName' )
                    // InternalOseeDsl.g:3523:4: enumLiteral_0= 'artifactName'
                    {
                    enumLiteral_0=(Token)match(input,90,FOLLOW_2); 

                            current = grammarAccess.getMatchFieldAccess().getArtifactNameEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_0, grammarAccess.getMatchFieldAccess().getArtifactNameEnumLiteralDeclaration_0()); 
                        

                    }


                    }
                    break;
                case 2 :
                    // InternalOseeDsl.g:3529:6: (enumLiteral_1= 'artifactGuid' )
                    {
                    // InternalOseeDsl.g:3529:6: (enumLiteral_1= 'artifactGuid' )
                    // InternalOseeDsl.g:3529:8: enumLiteral_1= 'artifactGuid'
                    {
                    enumLiteral_1=(Token)match(input,91,FOLLOW_2); 

                            current = grammarAccess.getMatchFieldAccess().getArtifactGuidEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_1, grammarAccess.getMatchFieldAccess().getArtifactGuidEnumLiteralDeclaration_1()); 
                        

                    }


                    }
                    break;
                case 3 :
                    // InternalOseeDsl.g:3535:6: (enumLiteral_2= 'branchName' )
                    {
                    // InternalOseeDsl.g:3535:6: (enumLiteral_2= 'branchName' )
                    // InternalOseeDsl.g:3535:8: enumLiteral_2= 'branchName'
                    {
                    enumLiteral_2=(Token)match(input,92,FOLLOW_2); 

                            current = grammarAccess.getMatchFieldAccess().getBranchNameEnumLiteralDeclaration_2().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_2, grammarAccess.getMatchFieldAccess().getBranchNameEnumLiteralDeclaration_2()); 
                        

                    }


                    }
                    break;
                case 4 :
                    // InternalOseeDsl.g:3541:6: (enumLiteral_3= 'branchUuid' )
                    {
                    // InternalOseeDsl.g:3541:6: (enumLiteral_3= 'branchUuid' )
                    // InternalOseeDsl.g:3541:8: enumLiteral_3= 'branchUuid'
                    {
                    enumLiteral_3=(Token)match(input,23,FOLLOW_2); 

                            current = grammarAccess.getMatchFieldAccess().getBranchUuidEnumLiteralDeclaration_3().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_3, grammarAccess.getMatchFieldAccess().getBranchUuidEnumLiteralDeclaration_3()); 
                        

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
    // $ANTLR end "ruleMatchField"


    // $ANTLR start "ruleAccessPermissionEnum"
    // InternalOseeDsl.g:3551:1: ruleAccessPermissionEnum returns [Enumerator current=null] : ( (enumLiteral_0= 'ALLOW' ) | (enumLiteral_1= 'DENY' ) ) ;
    public final Enumerator ruleAccessPermissionEnum() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;

         enterRule(); 
        try {
            // InternalOseeDsl.g:3553:28: ( ( (enumLiteral_0= 'ALLOW' ) | (enumLiteral_1= 'DENY' ) ) )
            // InternalOseeDsl.g:3554:1: ( (enumLiteral_0= 'ALLOW' ) | (enumLiteral_1= 'DENY' ) )
            {
            // InternalOseeDsl.g:3554:1: ( (enumLiteral_0= 'ALLOW' ) | (enumLiteral_1= 'DENY' ) )
            int alt52=2;
            int LA52_0 = input.LA(1);

            if ( (LA52_0==93) ) {
                alt52=1;
            }
            else if ( (LA52_0==94) ) {
                alt52=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 52, 0, input);

                throw nvae;
            }
            switch (alt52) {
                case 1 :
                    // InternalOseeDsl.g:3554:2: (enumLiteral_0= 'ALLOW' )
                    {
                    // InternalOseeDsl.g:3554:2: (enumLiteral_0= 'ALLOW' )
                    // InternalOseeDsl.g:3554:4: enumLiteral_0= 'ALLOW'
                    {
                    enumLiteral_0=(Token)match(input,93,FOLLOW_2); 

                            current = grammarAccess.getAccessPermissionEnumAccess().getALLOWEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_0, grammarAccess.getAccessPermissionEnumAccess().getALLOWEnumLiteralDeclaration_0()); 
                        

                    }


                    }
                    break;
                case 2 :
                    // InternalOseeDsl.g:3560:6: (enumLiteral_1= 'DENY' )
                    {
                    // InternalOseeDsl.g:3560:6: (enumLiteral_1= 'DENY' )
                    // InternalOseeDsl.g:3560:8: enumLiteral_1= 'DENY'
                    {
                    enumLiteral_1=(Token)match(input,94,FOLLOW_2); 

                            current = grammarAccess.getAccessPermissionEnumAccess().getDENYEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_1, grammarAccess.getAccessPermissionEnumAccess().getDENYEnumLiteralDeclaration_1()); 
                        

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
    // $ANTLR end "ruleAccessPermissionEnum"


    // $ANTLR start "ruleRelationTypeMatch"
    // InternalOseeDsl.g:3570:1: ruleRelationTypeMatch returns [Enumerator current=null] : (enumLiteral_0= 'ALL' ) ;
    public final Enumerator ruleRelationTypeMatch() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;

         enterRule(); 
        try {
            // InternalOseeDsl.g:3572:28: ( (enumLiteral_0= 'ALL' ) )
            // InternalOseeDsl.g:3573:1: (enumLiteral_0= 'ALL' )
            {
            // InternalOseeDsl.g:3573:1: (enumLiteral_0= 'ALL' )
            // InternalOseeDsl.g:3573:3: enumLiteral_0= 'ALL'
            {
            enumLiteral_0=(Token)match(input,95,FOLLOW_2); 

                    current = grammarAccess.getRelationTypeMatchAccess().getALLEnumLiteralDeclaration().getEnumLiteral().getInstance();
                    newLeafNode(enumLiteral_0, grammarAccess.getRelationTypeMatchAccess().getALLEnumLiteralDeclaration()); 
                

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
    // $ANTLR end "ruleRelationTypeMatch"


    // $ANTLR start "ruleXRelationSideEnum"
    // InternalOseeDsl.g:3583:1: ruleXRelationSideEnum returns [Enumerator current=null] : ( (enumLiteral_0= 'SIDE_A' ) | (enumLiteral_1= 'SIDE_B' ) | (enumLiteral_2= 'BOTH' ) ) ;
    public final Enumerator ruleXRelationSideEnum() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;
        Token enumLiteral_2=null;

         enterRule(); 
        try {
            // InternalOseeDsl.g:3585:28: ( ( (enumLiteral_0= 'SIDE_A' ) | (enumLiteral_1= 'SIDE_B' ) | (enumLiteral_2= 'BOTH' ) ) )
            // InternalOseeDsl.g:3586:1: ( (enumLiteral_0= 'SIDE_A' ) | (enumLiteral_1= 'SIDE_B' ) | (enumLiteral_2= 'BOTH' ) )
            {
            // InternalOseeDsl.g:3586:1: ( (enumLiteral_0= 'SIDE_A' ) | (enumLiteral_1= 'SIDE_B' ) | (enumLiteral_2= 'BOTH' ) )
            int alt53=3;
            switch ( input.LA(1) ) {
            case 96:
                {
                alt53=1;
                }
                break;
            case 97:
                {
                alt53=2;
                }
                break;
            case 98:
                {
                alt53=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 53, 0, input);

                throw nvae;
            }

            switch (alt53) {
                case 1 :
                    // InternalOseeDsl.g:3586:2: (enumLiteral_0= 'SIDE_A' )
                    {
                    // InternalOseeDsl.g:3586:2: (enumLiteral_0= 'SIDE_A' )
                    // InternalOseeDsl.g:3586:4: enumLiteral_0= 'SIDE_A'
                    {
                    enumLiteral_0=(Token)match(input,96,FOLLOW_2); 

                            current = grammarAccess.getXRelationSideEnumAccess().getSIDE_AEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_0, grammarAccess.getXRelationSideEnumAccess().getSIDE_AEnumLiteralDeclaration_0()); 
                        

                    }


                    }
                    break;
                case 2 :
                    // InternalOseeDsl.g:3592:6: (enumLiteral_1= 'SIDE_B' )
                    {
                    // InternalOseeDsl.g:3592:6: (enumLiteral_1= 'SIDE_B' )
                    // InternalOseeDsl.g:3592:8: enumLiteral_1= 'SIDE_B'
                    {
                    enumLiteral_1=(Token)match(input,97,FOLLOW_2); 

                            current = grammarAccess.getXRelationSideEnumAccess().getSIDE_BEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_1, grammarAccess.getXRelationSideEnumAccess().getSIDE_BEnumLiteralDeclaration_1()); 
                        

                    }


                    }
                    break;
                case 3 :
                    // InternalOseeDsl.g:3598:6: (enumLiteral_2= 'BOTH' )
                    {
                    // InternalOseeDsl.g:3598:6: (enumLiteral_2= 'BOTH' )
                    // InternalOseeDsl.g:3598:8: enumLiteral_2= 'BOTH'
                    {
                    enumLiteral_2=(Token)match(input,98,FOLLOW_2); 

                            current = grammarAccess.getXRelationSideEnumAccess().getBOTHEnumLiteralDeclaration_2().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_2, grammarAccess.getXRelationSideEnumAccess().getBOTHEnumLiteralDeclaration_2()); 
                        

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
    // $ANTLR end "ruleXRelationSideEnum"

    // Delegated rules


 

    public static final BitSet FOLLOW_1 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_2 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_3 = new BitSet(new long[]{0x289000000100D002L,0x0000000000003200L});
    public static final BitSet FOLLOW_4 = new BitSet(new long[]{0x289000000100C002L,0x0000000000003200L});
    public static final BitSet FOLLOW_5 = new BitSet(new long[]{0x0000000000000002L,0x0000000000003200L});
    public static final BitSet FOLLOW_6 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_7 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_8 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_9 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_10 = new BitSet(new long[]{0x0000000000050000L});
    public static final BitSet FOLLOW_11 = new BitSet(new long[]{0x0000000000060000L});
    public static final BitSet FOLLOW_12 = new BitSet(new long[]{0x0000000000180000L});
    public static final BitSet FOLLOW_13 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_14 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_15 = new BitSet(new long[]{0x0000000000600000L});
    public static final BitSet FOLLOW_16 = new BitSet(new long[]{0x0000000000800002L});
    public static final BitSet FOLLOW_17 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_18 = new BitSet(new long[]{0x000FFF8000000020L});
    public static final BitSet FOLLOW_19 = new BitSet(new long[]{0x0000000002040000L});
    public static final BitSet FOLLOW_20 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_21 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_22 = new BitSet(new long[]{0x000FFF8018000020L});
    public static final BitSet FOLLOW_23 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_24 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_25 = new BitSet(new long[]{0x0000000080000040L});
    public static final BitSet FOLLOW_26 = new BitSet(new long[]{0x0000007D00200000L});
    public static final BitSet FOLLOW_27 = new BitSet(new long[]{0x000FFF8200000020L});
    public static final BitSet FOLLOW_28 = new BitSet(new long[]{0x0020000000200000L});
    public static final BitSet FOLLOW_29 = new BitSet(new long[]{0x0040000800000042L});
    public static final BitSet FOLLOW_30 = new BitSet(new long[]{0x0040000800000002L});
    public static final BitSet FOLLOW_31 = new BitSet(new long[]{0x0000000800000002L});
    public static final BitSet FOLLOW_32 = new BitSet(new long[]{0x0700000000200000L});
    public static final BitSet FOLLOW_33 = new BitSet(new long[]{0x0600000000200000L});
    public static final BitSet FOLLOW_34 = new BitSet(new long[]{0x1700000000000000L});
    public static final BitSet FOLLOW_35 = new BitSet(new long[]{0x1700000000200000L});
    public static final BitSet FOLLOW_36 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_37 = new BitSet(new long[]{0x4000000000000000L});
    public static final BitSet FOLLOW_38 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_39 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_40 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_41 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_42 = new BitSet(new long[]{0x0000000000000020L,0x0000000000000070L});
    public static final BitSet FOLLOW_43 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_44 = new BitSet(new long[]{0x0000000000000000L,0x00000000003C0000L});
    public static final BitSet FOLLOW_45 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_46 = new BitSet(new long[]{0x0000000000000000L,0x0000000000C00000L});
    public static final BitSet FOLLOW_47 = new BitSet(new long[]{0x0000000000800000L,0x000000001C000000L});
    public static final BitSet FOLLOW_48 = new BitSet(new long[]{0x0000000000000000L,0x0000000003000000L});
    public static final BitSet FOLLOW_49 = new BitSet(new long[]{0x0000000000000000L,0x0000000003000100L});
    public static final BitSet FOLLOW_50 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_51 = new BitSet(new long[]{0x0000000000800000L,0x000000001C000080L});
    public static final BitSet FOLLOW_52 = new BitSet(new long[]{0x0000000000000000L,0x0000000003000800L});
    public static final BitSet FOLLOW_53 = new BitSet(new long[]{0x0000000000080000L,0x0000000000002000L});
    public static final BitSet FOLLOW_54 = new BitSet(new long[]{0x0000000000280000L,0x0000000000002000L});
    public static final BitSet FOLLOW_55 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_56 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_57 = new BitSet(new long[]{0x0000000000000000L,0x0000000060004000L});
    public static final BitSet FOLLOW_58 = new BitSet(new long[]{0x0000000000200000L,0x0000000060004000L});
    public static final BitSet FOLLOW_59 = new BitSet(new long[]{0x0000000000000000L,0x0000000060000000L});
    public static final BitSet FOLLOW_60 = new BitSet(new long[]{0x0000000000200000L,0x0000000060000000L});
    public static final BitSet FOLLOW_61 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_62 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_63 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_64 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020800L});
    public static final BitSet FOLLOW_65 = new BitSet(new long[]{0x2000000000000000L});
    public static final BitSet FOLLOW_66 = new BitSet(new long[]{0x0000000000000010L,0x0000000080000000L});
    public static final BitSet FOLLOW_67 = new BitSet(new long[]{0x0000000000000000L,0x0000000700000000L});
    public static final BitSet FOLLOW_68 = new BitSet(new long[]{0x0000000000008000L,0x0000000000008800L});

}
