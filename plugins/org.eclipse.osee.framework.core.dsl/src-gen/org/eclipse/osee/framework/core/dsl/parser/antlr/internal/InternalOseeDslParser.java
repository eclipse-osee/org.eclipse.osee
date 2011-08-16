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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE_STRING", "RULE_ID", "RULE_HEX_INT", "RULE_WHOLE_NUM_STR", "RULE_INT", "RULE_ML_COMMENT", "RULE_SL_COMMENT", "RULE_WS", "RULE_ANY_OTHER", "'import'", "'.'", "'abstract'", "'artifactType'", "'extends'", "','", "'{'", "'guid'", "'uuid'", "'}'", "'attribute'", "'branchGuid'", "'attributeType'", "'overrides'", "'dataProvider'", "'DefaultAttributeDataProvider'", "'UriAttributeDataProvider'", "'min'", "'max'", "'unlimited'", "'taggerId'", "'DefaultAttributeTaggerProvider'", "'enumType'", "'description'", "'defaultValue'", "'fileExtension'", "'BooleanAttribute'", "'CompressedContentAttribute'", "'DateAttribute'", "'EnumeratedAttribute'", "'FloatingPointAttribute'", "'IntegerAttribute'", "'JavaObjectAttribute'", "'StringAttribute'", "'WordAttribute'", "'oseeEnumType'", "'entry'", "'entryGuid'", "'overrides enum'", "'inheritAll'", "'add'", "'remove'", "'relationType'", "'sideAName'", "'sideAArtifactType'", "'sideBName'", "'sideBArtifactType'", "'defaultOrderType'", "'multiplicity'", "'Lexicographical_Ascending'", "'Lexicographical_Descending'", "'Unordered'", "'('", "')'", "'artifactMatcher'", "'where'", "';'", "'accessContext'", "'childrenOf'", "'edit'", "'artifact'", "'of'", "'ONE_TO_ONE'", "'ONE_TO_MANY'", "'MANY_TO_ONE'", "'MANY_TO_MANY'", "'EQ'", "'LIKE'", "'AND'", "'OR'", "'artifactName'", "'artifactGuid'", "'branchName'", "'ALLOW'", "'DENY'", "'SIDE_A'", "'SIDE_B'", "'BOTH'"
    };
    public static final int T__68=68;
    public static final int T__69=69;
    public static final int RULE_ID=5;
    public static final int T__66=66;
    public static final int T__67=67;
    public static final int T__64=64;
    public static final int T__29=29;
    public static final int T__65=65;
    public static final int T__28=28;
    public static final int T__62=62;
    public static final int T__27=27;
    public static final int T__63=63;
    public static final int T__26=26;
    public static final int T__25=25;
    public static final int T__24=24;
    public static final int T__23=23;
    public static final int T__22=22;
    public static final int RULE_ANY_OTHER=12;
    public static final int T__21=21;
    public static final int T__20=20;
    public static final int T__61=61;
    public static final int T__60=60;
    public static final int EOF=-1;
    public static final int T__55=55;
    public static final int T__56=56;
    public static final int T__19=19;
    public static final int T__57=57;
    public static final int T__58=58;
    public static final int T__16=16;
    public static final int T__51=51;
    public static final int T__90=90;
    public static final int T__15=15;
    public static final int T__52=52;
    public static final int T__53=53;
    public static final int T__18=18;
    public static final int T__54=54;
    public static final int T__17=17;
    public static final int T__14=14;
    public static final int T__13=13;
    public static final int T__59=59;
    public static final int RULE_INT=8;
    public static final int T__50=50;
    public static final int T__42=42;
    public static final int T__43=43;
    public static final int T__40=40;
    public static final int T__41=41;
    public static final int T__80=80;
    public static final int T__46=46;
    public static final int T__81=81;
    public static final int T__47=47;
    public static final int T__82=82;
    public static final int T__44=44;
    public static final int T__83=83;
    public static final int T__45=45;
    public static final int RULE_WHOLE_NUM_STR=7;
    public static final int T__48=48;
    public static final int T__49=49;
    public static final int RULE_HEX_INT=6;
    public static final int T__85=85;
    public static final int RULE_SL_COMMENT=10;
    public static final int T__84=84;
    public static final int T__87=87;
    public static final int T__86=86;
    public static final int T__89=89;
    public static final int T__88=88;
    public static final int RULE_ML_COMMENT=9;
    public static final int T__30=30;
    public static final int T__31=31;
    public static final int RULE_STRING=4;
    public static final int T__32=32;
    public static final int T__71=71;
    public static final int T__33=33;
    public static final int T__72=72;
    public static final int T__34=34;
    public static final int T__35=35;
    public static final int T__70=70;
    public static final int T__36=36;
    public static final int T__37=37;
    public static final int T__38=38;
    public static final int T__39=39;
    public static final int RULE_WS=11;
    public static final int T__76=76;
    public static final int T__75=75;
    public static final int T__74=74;
    public static final int T__73=73;
    public static final int T__79=79;
    public static final int T__78=78;
    public static final int T__77=77;

    // delegates
    // delegators


        public InternalOseeDslParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public InternalOseeDslParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return InternalOseeDslParser.tokenNames; }
    public String getGrammarFileName() { return "../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g"; }



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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:68:1: entryRuleOseeDsl returns [EObject current=null] : iv_ruleOseeDsl= ruleOseeDsl EOF ;
    public final EObject entryRuleOseeDsl() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOseeDsl = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:69:2: (iv_ruleOseeDsl= ruleOseeDsl EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:70:2: iv_ruleOseeDsl= ruleOseeDsl EOF
            {
             newCompositeNode(grammarAccess.getOseeDslRule()); 
            pushFollow(FOLLOW_ruleOseeDsl_in_entryRuleOseeDsl75);
            iv_ruleOseeDsl=ruleOseeDsl();

            state._fsp--;

             current =iv_ruleOseeDsl; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleOseeDsl85); 

            }

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:77:1: ruleOseeDsl returns [EObject current=null] : ( ( (lv_imports_0_0= ruleImport ) )* ( ( (lv_artifactTypes_1_0= ruleXArtifactType ) ) | ( (lv_relationTypes_2_0= ruleXRelationType ) ) | ( (lv_attributeTypes_3_0= ruleXAttributeType ) ) | ( (lv_enumTypes_4_0= ruleXOseeEnumType ) ) | ( (lv_enumOverrides_5_0= ruleXOseeEnumOverride ) ) )* ( (lv_artifactMatchRefs_6_0= ruleXArtifactMatcher ) )* ( (lv_accessDeclarations_7_0= ruleAccessContext ) )* ) ;
    public final EObject ruleOseeDsl() throws RecognitionException {
        EObject current = null;

        EObject lv_imports_0_0 = null;

        EObject lv_artifactTypes_1_0 = null;

        EObject lv_relationTypes_2_0 = null;

        EObject lv_attributeTypes_3_0 = null;

        EObject lv_enumTypes_4_0 = null;

        EObject lv_enumOverrides_5_0 = null;

        EObject lv_artifactMatchRefs_6_0 = null;

        EObject lv_accessDeclarations_7_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:80:28: ( ( ( (lv_imports_0_0= ruleImport ) )* ( ( (lv_artifactTypes_1_0= ruleXArtifactType ) ) | ( (lv_relationTypes_2_0= ruleXRelationType ) ) | ( (lv_attributeTypes_3_0= ruleXAttributeType ) ) | ( (lv_enumTypes_4_0= ruleXOseeEnumType ) ) | ( (lv_enumOverrides_5_0= ruleXOseeEnumOverride ) ) )* ( (lv_artifactMatchRefs_6_0= ruleXArtifactMatcher ) )* ( (lv_accessDeclarations_7_0= ruleAccessContext ) )* ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:81:1: ( ( (lv_imports_0_0= ruleImport ) )* ( ( (lv_artifactTypes_1_0= ruleXArtifactType ) ) | ( (lv_relationTypes_2_0= ruleXRelationType ) ) | ( (lv_attributeTypes_3_0= ruleXAttributeType ) ) | ( (lv_enumTypes_4_0= ruleXOseeEnumType ) ) | ( (lv_enumOverrides_5_0= ruleXOseeEnumOverride ) ) )* ( (lv_artifactMatchRefs_6_0= ruleXArtifactMatcher ) )* ( (lv_accessDeclarations_7_0= ruleAccessContext ) )* )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:81:1: ( ( (lv_imports_0_0= ruleImport ) )* ( ( (lv_artifactTypes_1_0= ruleXArtifactType ) ) | ( (lv_relationTypes_2_0= ruleXRelationType ) ) | ( (lv_attributeTypes_3_0= ruleXAttributeType ) ) | ( (lv_enumTypes_4_0= ruleXOseeEnumType ) ) | ( (lv_enumOverrides_5_0= ruleXOseeEnumOverride ) ) )* ( (lv_artifactMatchRefs_6_0= ruleXArtifactMatcher ) )* ( (lv_accessDeclarations_7_0= ruleAccessContext ) )* )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:81:2: ( (lv_imports_0_0= ruleImport ) )* ( ( (lv_artifactTypes_1_0= ruleXArtifactType ) ) | ( (lv_relationTypes_2_0= ruleXRelationType ) ) | ( (lv_attributeTypes_3_0= ruleXAttributeType ) ) | ( (lv_enumTypes_4_0= ruleXOseeEnumType ) ) | ( (lv_enumOverrides_5_0= ruleXOseeEnumOverride ) ) )* ( (lv_artifactMatchRefs_6_0= ruleXArtifactMatcher ) )* ( (lv_accessDeclarations_7_0= ruleAccessContext ) )*
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:81:2: ( (lv_imports_0_0= ruleImport ) )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==13) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:82:1: (lv_imports_0_0= ruleImport )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:82:1: (lv_imports_0_0= ruleImport )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:83:3: lv_imports_0_0= ruleImport
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getOseeDslAccess().getImportsImportParserRuleCall_0_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleImport_in_ruleOseeDsl131);
            	    lv_imports_0_0=ruleImport();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getOseeDslRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"imports",
            	            		lv_imports_0_0, 
            	            		"Import");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:99:3: ( ( (lv_artifactTypes_1_0= ruleXArtifactType ) ) | ( (lv_relationTypes_2_0= ruleXRelationType ) ) | ( (lv_attributeTypes_3_0= ruleXAttributeType ) ) | ( (lv_enumTypes_4_0= ruleXOseeEnumType ) ) | ( (lv_enumOverrides_5_0= ruleXOseeEnumOverride ) ) )*
            loop2:
            do {
                int alt2=6;
                switch ( input.LA(1) ) {
                case 15:
                case 16:
                    {
                    alt2=1;
                    }
                    break;
                case 55:
                    {
                    alt2=2;
                    }
                    break;
                case 25:
                    {
                    alt2=3;
                    }
                    break;
                case 48:
                    {
                    alt2=4;
                    }
                    break;
                case 51:
                    {
                    alt2=5;
                    }
                    break;

                }

                switch (alt2) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:99:4: ( (lv_artifactTypes_1_0= ruleXArtifactType ) )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:99:4: ( (lv_artifactTypes_1_0= ruleXArtifactType ) )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:100:1: (lv_artifactTypes_1_0= ruleXArtifactType )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:100:1: (lv_artifactTypes_1_0= ruleXArtifactType )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:101:3: lv_artifactTypes_1_0= ruleXArtifactType
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getOseeDslAccess().getArtifactTypesXArtifactTypeParserRuleCall_1_0_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleXArtifactType_in_ruleOseeDsl154);
            	    lv_artifactTypes_1_0=ruleXArtifactType();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getOseeDslRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"artifactTypes",
            	            		lv_artifactTypes_1_0, 
            	            		"XArtifactType");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;
            	case 2 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:118:6: ( (lv_relationTypes_2_0= ruleXRelationType ) )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:118:6: ( (lv_relationTypes_2_0= ruleXRelationType ) )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:119:1: (lv_relationTypes_2_0= ruleXRelationType )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:119:1: (lv_relationTypes_2_0= ruleXRelationType )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:120:3: lv_relationTypes_2_0= ruleXRelationType
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getOseeDslAccess().getRelationTypesXRelationTypeParserRuleCall_1_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleXRelationType_in_ruleOseeDsl181);
            	    lv_relationTypes_2_0=ruleXRelationType();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getOseeDslRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"relationTypes",
            	            		lv_relationTypes_2_0, 
            	            		"XRelationType");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;
            	case 3 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:137:6: ( (lv_attributeTypes_3_0= ruleXAttributeType ) )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:137:6: ( (lv_attributeTypes_3_0= ruleXAttributeType ) )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:138:1: (lv_attributeTypes_3_0= ruleXAttributeType )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:138:1: (lv_attributeTypes_3_0= ruleXAttributeType )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:139:3: lv_attributeTypes_3_0= ruleXAttributeType
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getOseeDslAccess().getAttributeTypesXAttributeTypeParserRuleCall_1_2_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleXAttributeType_in_ruleOseeDsl208);
            	    lv_attributeTypes_3_0=ruleXAttributeType();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getOseeDslRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"attributeTypes",
            	            		lv_attributeTypes_3_0, 
            	            		"XAttributeType");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;
            	case 4 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:156:6: ( (lv_enumTypes_4_0= ruleXOseeEnumType ) )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:156:6: ( (lv_enumTypes_4_0= ruleXOseeEnumType ) )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:157:1: (lv_enumTypes_4_0= ruleXOseeEnumType )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:157:1: (lv_enumTypes_4_0= ruleXOseeEnumType )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:158:3: lv_enumTypes_4_0= ruleXOseeEnumType
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getOseeDslAccess().getEnumTypesXOseeEnumTypeParserRuleCall_1_3_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleXOseeEnumType_in_ruleOseeDsl235);
            	    lv_enumTypes_4_0=ruleXOseeEnumType();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getOseeDslRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"enumTypes",
            	            		lv_enumTypes_4_0, 
            	            		"XOseeEnumType");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;
            	case 5 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:175:6: ( (lv_enumOverrides_5_0= ruleXOseeEnumOverride ) )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:175:6: ( (lv_enumOverrides_5_0= ruleXOseeEnumOverride ) )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:176:1: (lv_enumOverrides_5_0= ruleXOseeEnumOverride )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:176:1: (lv_enumOverrides_5_0= ruleXOseeEnumOverride )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:177:3: lv_enumOverrides_5_0= ruleXOseeEnumOverride
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getOseeDslAccess().getEnumOverridesXOseeEnumOverrideParserRuleCall_1_4_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleXOseeEnumOverride_in_ruleOseeDsl262);
            	    lv_enumOverrides_5_0=ruleXOseeEnumOverride();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getOseeDslRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"enumOverrides",
            	            		lv_enumOverrides_5_0, 
            	            		"XOseeEnumOverride");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:193:4: ( (lv_artifactMatchRefs_6_0= ruleXArtifactMatcher ) )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==67) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:194:1: (lv_artifactMatchRefs_6_0= ruleXArtifactMatcher )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:194:1: (lv_artifactMatchRefs_6_0= ruleXArtifactMatcher )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:195:3: lv_artifactMatchRefs_6_0= ruleXArtifactMatcher
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getOseeDslAccess().getArtifactMatchRefsXArtifactMatcherParserRuleCall_2_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleXArtifactMatcher_in_ruleOseeDsl285);
            	    lv_artifactMatchRefs_6_0=ruleXArtifactMatcher();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getOseeDslRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"artifactMatchRefs",
            	            		lv_artifactMatchRefs_6_0, 
            	            		"XArtifactMatcher");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:211:3: ( (lv_accessDeclarations_7_0= ruleAccessContext ) )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==70) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:212:1: (lv_accessDeclarations_7_0= ruleAccessContext )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:212:1: (lv_accessDeclarations_7_0= ruleAccessContext )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:213:3: lv_accessDeclarations_7_0= ruleAccessContext
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getOseeDslAccess().getAccessDeclarationsAccessContextParserRuleCall_3_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleAccessContext_in_ruleOseeDsl307);
            	    lv_accessDeclarations_7_0=ruleAccessContext();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getOseeDslRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"accessDeclarations",
            	            		lv_accessDeclarations_7_0, 
            	            		"AccessContext");
            	    	        afterParserOrEnumRuleCall();
            	    	    

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
    // $ANTLR end "ruleOseeDsl"


    // $ANTLR start "entryRuleImport"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:237:1: entryRuleImport returns [EObject current=null] : iv_ruleImport= ruleImport EOF ;
    public final EObject entryRuleImport() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleImport = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:238:2: (iv_ruleImport= ruleImport EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:239:2: iv_ruleImport= ruleImport EOF
            {
             newCompositeNode(grammarAccess.getImportRule()); 
            pushFollow(FOLLOW_ruleImport_in_entryRuleImport344);
            iv_ruleImport=ruleImport();

            state._fsp--;

             current =iv_ruleImport; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleImport354); 

            }

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:246:1: ruleImport returns [EObject current=null] : (otherlv_0= 'import' ( (lv_importURI_1_0= RULE_STRING ) ) ) ;
    public final EObject ruleImport() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_importURI_1_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:249:28: ( (otherlv_0= 'import' ( (lv_importURI_1_0= RULE_STRING ) ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:250:1: (otherlv_0= 'import' ( (lv_importURI_1_0= RULE_STRING ) ) )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:250:1: (otherlv_0= 'import' ( (lv_importURI_1_0= RULE_STRING ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:250:3: otherlv_0= 'import' ( (lv_importURI_1_0= RULE_STRING ) )
            {
            otherlv_0=(Token)match(input,13,FOLLOW_13_in_ruleImport391); 

                	newLeafNode(otherlv_0, grammarAccess.getImportAccess().getImportKeyword_0());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:254:1: ( (lv_importURI_1_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:255:1: (lv_importURI_1_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:255:1: (lv_importURI_1_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:256:3: lv_importURI_1_0= RULE_STRING
            {
            lv_importURI_1_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleImport408); 

            			newLeafNode(lv_importURI_1_0, grammarAccess.getImportAccess().getImportURISTRINGTerminalRuleCall_1_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getImportRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"importURI",
                    		lv_importURI_1_0, 
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
    // $ANTLR end "ruleImport"


    // $ANTLR start "entryRuleQUALIFIED_NAME"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:280:1: entryRuleQUALIFIED_NAME returns [String current=null] : iv_ruleQUALIFIED_NAME= ruleQUALIFIED_NAME EOF ;
    public final String entryRuleQUALIFIED_NAME() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleQUALIFIED_NAME = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:281:2: (iv_ruleQUALIFIED_NAME= ruleQUALIFIED_NAME EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:282:2: iv_ruleQUALIFIED_NAME= ruleQUALIFIED_NAME EOF
            {
             newCompositeNode(grammarAccess.getQUALIFIED_NAMERule()); 
            pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_entryRuleQUALIFIED_NAME450);
            iv_ruleQUALIFIED_NAME=ruleQUALIFIED_NAME();

            state._fsp--;

             current =iv_ruleQUALIFIED_NAME.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleQUALIFIED_NAME461); 

            }

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:289:1: ruleQUALIFIED_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* ) ;
    public final AntlrDatatypeRuleToken ruleQUALIFIED_NAME() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_ID_0=null;
        Token kw=null;
        Token this_ID_2=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:292:28: ( (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:293:1: (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:293:1: (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:293:6: this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )*
            {
            this_ID_0=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleQUALIFIED_NAME501); 

            		current.merge(this_ID_0);
                
             
                newLeafNode(this_ID_0, grammarAccess.getQUALIFIED_NAMEAccess().getIDTerminalRuleCall_0()); 
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:300:1: (kw= '.' this_ID_2= RULE_ID )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==14) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:301:2: kw= '.' this_ID_2= RULE_ID
            	    {
            	    kw=(Token)match(input,14,FOLLOW_14_in_ruleQUALIFIED_NAME520); 

            	            current.merge(kw);
            	            newLeafNode(kw, grammarAccess.getQUALIFIED_NAMEAccess().getFullStopKeyword_1_0()); 
            	        
            	    this_ID_2=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleQUALIFIED_NAME535); 

            	    		current.merge(this_ID_2);
            	        
            	     
            	        newLeafNode(this_ID_2, grammarAccess.getQUALIFIED_NAMEAccess().getIDTerminalRuleCall_1_1()); 
            	        

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
    // $ANTLR end "ruleQUALIFIED_NAME"


    // $ANTLR start "entryRuleOseeType"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:323:1: entryRuleOseeType returns [EObject current=null] : iv_ruleOseeType= ruleOseeType EOF ;
    public final EObject entryRuleOseeType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOseeType = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:324:2: (iv_ruleOseeType= ruleOseeType EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:325:2: iv_ruleOseeType= ruleOseeType EOF
            {
             newCompositeNode(grammarAccess.getOseeTypeRule()); 
            pushFollow(FOLLOW_ruleOseeType_in_entryRuleOseeType584);
            iv_ruleOseeType=ruleOseeType();

            state._fsp--;

             current =iv_ruleOseeType; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleOseeType594); 

            }

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:332:1: ruleOseeType returns [EObject current=null] : (this_XArtifactType_0= ruleXArtifactType | this_XRelationType_1= ruleXRelationType | this_XAttributeType_2= ruleXAttributeType | this_XOseeEnumType_3= ruleXOseeEnumType ) ;
    public final EObject ruleOseeType() throws RecognitionException {
        EObject current = null;

        EObject this_XArtifactType_0 = null;

        EObject this_XRelationType_1 = null;

        EObject this_XAttributeType_2 = null;

        EObject this_XOseeEnumType_3 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:335:28: ( (this_XArtifactType_0= ruleXArtifactType | this_XRelationType_1= ruleXRelationType | this_XAttributeType_2= ruleXAttributeType | this_XOseeEnumType_3= ruleXOseeEnumType ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:336:1: (this_XArtifactType_0= ruleXArtifactType | this_XRelationType_1= ruleXRelationType | this_XAttributeType_2= ruleXAttributeType | this_XOseeEnumType_3= ruleXOseeEnumType )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:336:1: (this_XArtifactType_0= ruleXArtifactType | this_XRelationType_1= ruleXRelationType | this_XAttributeType_2= ruleXAttributeType | this_XOseeEnumType_3= ruleXOseeEnumType )
            int alt6=4;
            switch ( input.LA(1) ) {
            case 15:
            case 16:
                {
                alt6=1;
                }
                break;
            case 55:
                {
                alt6=2;
                }
                break;
            case 25:
                {
                alt6=3;
                }
                break;
            case 48:
                {
                alt6=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;
            }

            switch (alt6) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:337:5: this_XArtifactType_0= ruleXArtifactType
                    {
                     
                            newCompositeNode(grammarAccess.getOseeTypeAccess().getXArtifactTypeParserRuleCall_0()); 
                        
                    pushFollow(FOLLOW_ruleXArtifactType_in_ruleOseeType641);
                    this_XArtifactType_0=ruleXArtifactType();

                    state._fsp--;

                     
                            current = this_XArtifactType_0; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:347:5: this_XRelationType_1= ruleXRelationType
                    {
                     
                            newCompositeNode(grammarAccess.getOseeTypeAccess().getXRelationTypeParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_ruleXRelationType_in_ruleOseeType668);
                    this_XRelationType_1=ruleXRelationType();

                    state._fsp--;

                     
                            current = this_XRelationType_1; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:357:5: this_XAttributeType_2= ruleXAttributeType
                    {
                     
                            newCompositeNode(grammarAccess.getOseeTypeAccess().getXAttributeTypeParserRuleCall_2()); 
                        
                    pushFollow(FOLLOW_ruleXAttributeType_in_ruleOseeType695);
                    this_XAttributeType_2=ruleXAttributeType();

                    state._fsp--;

                     
                            current = this_XAttributeType_2; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:367:5: this_XOseeEnumType_3= ruleXOseeEnumType
                    {
                     
                            newCompositeNode(grammarAccess.getOseeTypeAccess().getXOseeEnumTypeParserRuleCall_3()); 
                        
                    pushFollow(FOLLOW_ruleXOseeEnumType_in_ruleOseeType722);
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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:383:1: entryRuleXArtifactType returns [EObject current=null] : iv_ruleXArtifactType= ruleXArtifactType EOF ;
    public final EObject entryRuleXArtifactType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleXArtifactType = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:384:2: (iv_ruleXArtifactType= ruleXArtifactType EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:385:2: iv_ruleXArtifactType= ruleXArtifactType EOF
            {
             newCompositeNode(grammarAccess.getXArtifactTypeRule()); 
            pushFollow(FOLLOW_ruleXArtifactType_in_entryRuleXArtifactType757);
            iv_ruleXArtifactType=ruleXArtifactType();

            state._fsp--;

             current =iv_ruleXArtifactType; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleXArtifactType767); 

            }

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:392:1: ruleXArtifactType returns [EObject current=null] : ( ( (lv_abstract_0_0= 'abstract' ) )? otherlv_1= 'artifactType' ( (lv_name_2_0= RULE_STRING ) ) (otherlv_3= 'extends' ( (otherlv_4= RULE_STRING ) ) (otherlv_5= ',' ( (otherlv_6= RULE_STRING ) ) )* )? otherlv_7= '{' otherlv_8= 'guid' ( (lv_typeGuid_9_0= RULE_STRING ) ) otherlv_10= 'uuid' ( (lv_uuid_11_0= RULE_HEX_INT ) ) ( (lv_validAttributeTypes_12_0= ruleXAttributeTypeRef ) )* otherlv_13= '}' ) ;
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
        Token lv_uuid_11_0=null;
        Token otherlv_13=null;
        EObject lv_validAttributeTypes_12_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:395:28: ( ( ( (lv_abstract_0_0= 'abstract' ) )? otherlv_1= 'artifactType' ( (lv_name_2_0= RULE_STRING ) ) (otherlv_3= 'extends' ( (otherlv_4= RULE_STRING ) ) (otherlv_5= ',' ( (otherlv_6= RULE_STRING ) ) )* )? otherlv_7= '{' otherlv_8= 'guid' ( (lv_typeGuid_9_0= RULE_STRING ) ) otherlv_10= 'uuid' ( (lv_uuid_11_0= RULE_HEX_INT ) ) ( (lv_validAttributeTypes_12_0= ruleXAttributeTypeRef ) )* otherlv_13= '}' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:396:1: ( ( (lv_abstract_0_0= 'abstract' ) )? otherlv_1= 'artifactType' ( (lv_name_2_0= RULE_STRING ) ) (otherlv_3= 'extends' ( (otherlv_4= RULE_STRING ) ) (otherlv_5= ',' ( (otherlv_6= RULE_STRING ) ) )* )? otherlv_7= '{' otherlv_8= 'guid' ( (lv_typeGuid_9_0= RULE_STRING ) ) otherlv_10= 'uuid' ( (lv_uuid_11_0= RULE_HEX_INT ) ) ( (lv_validAttributeTypes_12_0= ruleXAttributeTypeRef ) )* otherlv_13= '}' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:396:1: ( ( (lv_abstract_0_0= 'abstract' ) )? otherlv_1= 'artifactType' ( (lv_name_2_0= RULE_STRING ) ) (otherlv_3= 'extends' ( (otherlv_4= RULE_STRING ) ) (otherlv_5= ',' ( (otherlv_6= RULE_STRING ) ) )* )? otherlv_7= '{' otherlv_8= 'guid' ( (lv_typeGuid_9_0= RULE_STRING ) ) otherlv_10= 'uuid' ( (lv_uuid_11_0= RULE_HEX_INT ) ) ( (lv_validAttributeTypes_12_0= ruleXAttributeTypeRef ) )* otherlv_13= '}' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:396:2: ( (lv_abstract_0_0= 'abstract' ) )? otherlv_1= 'artifactType' ( (lv_name_2_0= RULE_STRING ) ) (otherlv_3= 'extends' ( (otherlv_4= RULE_STRING ) ) (otherlv_5= ',' ( (otherlv_6= RULE_STRING ) ) )* )? otherlv_7= '{' otherlv_8= 'guid' ( (lv_typeGuid_9_0= RULE_STRING ) ) otherlv_10= 'uuid' ( (lv_uuid_11_0= RULE_HEX_INT ) ) ( (lv_validAttributeTypes_12_0= ruleXAttributeTypeRef ) )* otherlv_13= '}'
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:396:2: ( (lv_abstract_0_0= 'abstract' ) )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==15) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:397:1: (lv_abstract_0_0= 'abstract' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:397:1: (lv_abstract_0_0= 'abstract' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:398:3: lv_abstract_0_0= 'abstract'
                    {
                    lv_abstract_0_0=(Token)match(input,15,FOLLOW_15_in_ruleXArtifactType810); 

                            newLeafNode(lv_abstract_0_0, grammarAccess.getXArtifactTypeAccess().getAbstractAbstractKeyword_0_0());
                        

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getXArtifactTypeRule());
                    	        }
                           		setWithLastConsumed(current, "abstract", true, "abstract");
                    	    

                    }


                    }
                    break;

            }

            otherlv_1=(Token)match(input,16,FOLLOW_16_in_ruleXArtifactType836); 

                	newLeafNode(otherlv_1, grammarAccess.getXArtifactTypeAccess().getArtifactTypeKeyword_1());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:415:1: ( (lv_name_2_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:416:1: (lv_name_2_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:416:1: (lv_name_2_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:417:3: lv_name_2_0= RULE_STRING
            {
            lv_name_2_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXArtifactType853); 

            			newLeafNode(lv_name_2_0, grammarAccess.getXArtifactTypeAccess().getNameSTRINGTerminalRuleCall_2_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getXArtifactTypeRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"name",
                    		lv_name_2_0, 
                    		"STRING");
            	    

            }


            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:433:2: (otherlv_3= 'extends' ( (otherlv_4= RULE_STRING ) ) (otherlv_5= ',' ( (otherlv_6= RULE_STRING ) ) )* )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==17) ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:433:4: otherlv_3= 'extends' ( (otherlv_4= RULE_STRING ) ) (otherlv_5= ',' ( (otherlv_6= RULE_STRING ) ) )*
                    {
                    otherlv_3=(Token)match(input,17,FOLLOW_17_in_ruleXArtifactType871); 

                        	newLeafNode(otherlv_3, grammarAccess.getXArtifactTypeAccess().getExtendsKeyword_3_0());
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:437:1: ( (otherlv_4= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:438:1: (otherlv_4= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:438:1: (otherlv_4= RULE_STRING )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:439:3: otherlv_4= RULE_STRING
                    {

                    			if (current==null) {
                    	            current = createModelElement(grammarAccess.getXArtifactTypeRule());
                    	        }
                            
                    otherlv_4=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXArtifactType891); 

                    		newLeafNode(otherlv_4, grammarAccess.getXArtifactTypeAccess().getSuperArtifactTypesXArtifactTypeCrossReference_3_1_0()); 
                    	

                    }


                    }

                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:450:2: (otherlv_5= ',' ( (otherlv_6= RULE_STRING ) ) )*
                    loop8:
                    do {
                        int alt8=2;
                        int LA8_0 = input.LA(1);

                        if ( (LA8_0==18) ) {
                            alt8=1;
                        }


                        switch (alt8) {
                    	case 1 :
                    	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:450:4: otherlv_5= ',' ( (otherlv_6= RULE_STRING ) )
                    	    {
                    	    otherlv_5=(Token)match(input,18,FOLLOW_18_in_ruleXArtifactType904); 

                    	        	newLeafNode(otherlv_5, grammarAccess.getXArtifactTypeAccess().getCommaKeyword_3_2_0());
                    	        
                    	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:454:1: ( (otherlv_6= RULE_STRING ) )
                    	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:455:1: (otherlv_6= RULE_STRING )
                    	    {
                    	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:455:1: (otherlv_6= RULE_STRING )
                    	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:456:3: otherlv_6= RULE_STRING
                    	    {

                    	    			if (current==null) {
                    	    	            current = createModelElement(grammarAccess.getXArtifactTypeRule());
                    	    	        }
                    	            
                    	    otherlv_6=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXArtifactType924); 

                    	    		newLeafNode(otherlv_6, grammarAccess.getXArtifactTypeAccess().getSuperArtifactTypesXArtifactTypeCrossReference_3_2_1_0()); 
                    	    	

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop8;
                        }
                    } while (true);


                    }
                    break;

            }

            otherlv_7=(Token)match(input,19,FOLLOW_19_in_ruleXArtifactType940); 

                	newLeafNode(otherlv_7, grammarAccess.getXArtifactTypeAccess().getLeftCurlyBracketKeyword_4());
                
            otherlv_8=(Token)match(input,20,FOLLOW_20_in_ruleXArtifactType952); 

                	newLeafNode(otherlv_8, grammarAccess.getXArtifactTypeAccess().getGuidKeyword_5());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:475:1: ( (lv_typeGuid_9_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:476:1: (lv_typeGuid_9_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:476:1: (lv_typeGuid_9_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:477:3: lv_typeGuid_9_0= RULE_STRING
            {
            lv_typeGuid_9_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXArtifactType969); 

            			newLeafNode(lv_typeGuid_9_0, grammarAccess.getXArtifactTypeAccess().getTypeGuidSTRINGTerminalRuleCall_6_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getXArtifactTypeRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"typeGuid",
                    		lv_typeGuid_9_0, 
                    		"STRING");
            	    

            }


            }

            otherlv_10=(Token)match(input,21,FOLLOW_21_in_ruleXArtifactType986); 

                	newLeafNode(otherlv_10, grammarAccess.getXArtifactTypeAccess().getUuidKeyword_7());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:497:1: ( (lv_uuid_11_0= RULE_HEX_INT ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:498:1: (lv_uuid_11_0= RULE_HEX_INT )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:498:1: (lv_uuid_11_0= RULE_HEX_INT )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:499:3: lv_uuid_11_0= RULE_HEX_INT
            {
            lv_uuid_11_0=(Token)match(input,RULE_HEX_INT,FOLLOW_RULE_HEX_INT_in_ruleXArtifactType1003); 

            			newLeafNode(lv_uuid_11_0, grammarAccess.getXArtifactTypeAccess().getUuidHEX_INTTerminalRuleCall_8_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getXArtifactTypeRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"uuid",
                    		lv_uuid_11_0, 
                    		"HEX_INT");
            	    

            }


            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:515:2: ( (lv_validAttributeTypes_12_0= ruleXAttributeTypeRef ) )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0==23) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:516:1: (lv_validAttributeTypes_12_0= ruleXAttributeTypeRef )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:516:1: (lv_validAttributeTypes_12_0= ruleXAttributeTypeRef )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:517:3: lv_validAttributeTypes_12_0= ruleXAttributeTypeRef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getXArtifactTypeAccess().getValidAttributeTypesXAttributeTypeRefParserRuleCall_9_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleXAttributeTypeRef_in_ruleXArtifactType1029);
            	    lv_validAttributeTypes_12_0=ruleXAttributeTypeRef();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getXArtifactTypeRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"validAttributeTypes",
            	            		lv_validAttributeTypes_12_0, 
            	            		"XAttributeTypeRef");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    break loop10;
                }
            } while (true);

            otherlv_13=(Token)match(input,22,FOLLOW_22_in_ruleXArtifactType1042); 

                	newLeafNode(otherlv_13, grammarAccess.getXArtifactTypeAccess().getRightCurlyBracketKeyword_10());
                

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:545:1: entryRuleXAttributeTypeRef returns [EObject current=null] : iv_ruleXAttributeTypeRef= ruleXAttributeTypeRef EOF ;
    public final EObject entryRuleXAttributeTypeRef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleXAttributeTypeRef = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:546:2: (iv_ruleXAttributeTypeRef= ruleXAttributeTypeRef EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:547:2: iv_ruleXAttributeTypeRef= ruleXAttributeTypeRef EOF
            {
             newCompositeNode(grammarAccess.getXAttributeTypeRefRule()); 
            pushFollow(FOLLOW_ruleXAttributeTypeRef_in_entryRuleXAttributeTypeRef1078);
            iv_ruleXAttributeTypeRef=ruleXAttributeTypeRef();

            state._fsp--;

             current =iv_ruleXAttributeTypeRef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleXAttributeTypeRef1088); 

            }

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:554:1: ruleXAttributeTypeRef returns [EObject current=null] : (otherlv_0= 'attribute' ( (otherlv_1= RULE_STRING ) ) (otherlv_2= 'branchGuid' ( (lv_branchGuid_3_0= RULE_STRING ) ) )? ) ;
    public final EObject ruleXAttributeTypeRef() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;
        Token otherlv_2=null;
        Token lv_branchGuid_3_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:557:28: ( (otherlv_0= 'attribute' ( (otherlv_1= RULE_STRING ) ) (otherlv_2= 'branchGuid' ( (lv_branchGuid_3_0= RULE_STRING ) ) )? ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:558:1: (otherlv_0= 'attribute' ( (otherlv_1= RULE_STRING ) ) (otherlv_2= 'branchGuid' ( (lv_branchGuid_3_0= RULE_STRING ) ) )? )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:558:1: (otherlv_0= 'attribute' ( (otherlv_1= RULE_STRING ) ) (otherlv_2= 'branchGuid' ( (lv_branchGuid_3_0= RULE_STRING ) ) )? )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:558:3: otherlv_0= 'attribute' ( (otherlv_1= RULE_STRING ) ) (otherlv_2= 'branchGuid' ( (lv_branchGuid_3_0= RULE_STRING ) ) )?
            {
            otherlv_0=(Token)match(input,23,FOLLOW_23_in_ruleXAttributeTypeRef1125); 

                	newLeafNode(otherlv_0, grammarAccess.getXAttributeTypeRefAccess().getAttributeKeyword_0());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:562:1: ( (otherlv_1= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:563:1: (otherlv_1= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:563:1: (otherlv_1= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:564:3: otherlv_1= RULE_STRING
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getXAttributeTypeRefRule());
            	        }
                    
            otherlv_1=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXAttributeTypeRef1145); 

            		newLeafNode(otherlv_1, grammarAccess.getXAttributeTypeRefAccess().getValidAttributeTypeXAttributeTypeCrossReference_1_0()); 
            	

            }


            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:575:2: (otherlv_2= 'branchGuid' ( (lv_branchGuid_3_0= RULE_STRING ) ) )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==24) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:575:4: otherlv_2= 'branchGuid' ( (lv_branchGuid_3_0= RULE_STRING ) )
                    {
                    otherlv_2=(Token)match(input,24,FOLLOW_24_in_ruleXAttributeTypeRef1158); 

                        	newLeafNode(otherlv_2, grammarAccess.getXAttributeTypeRefAccess().getBranchGuidKeyword_2_0());
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:579:1: ( (lv_branchGuid_3_0= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:580:1: (lv_branchGuid_3_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:580:1: (lv_branchGuid_3_0= RULE_STRING )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:581:3: lv_branchGuid_3_0= RULE_STRING
                    {
                    lv_branchGuid_3_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXAttributeTypeRef1175); 

                    			newLeafNode(lv_branchGuid_3_0, grammarAccess.getXAttributeTypeRefAccess().getBranchGuidSTRINGTerminalRuleCall_2_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getXAttributeTypeRefRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"branchGuid",
                            		lv_branchGuid_3_0, 
                            		"STRING");
                    	    

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:605:1: entryRuleXAttributeType returns [EObject current=null] : iv_ruleXAttributeType= ruleXAttributeType EOF ;
    public final EObject entryRuleXAttributeType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleXAttributeType = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:606:2: (iv_ruleXAttributeType= ruleXAttributeType EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:607:2: iv_ruleXAttributeType= ruleXAttributeType EOF
            {
             newCompositeNode(grammarAccess.getXAttributeTypeRule()); 
            pushFollow(FOLLOW_ruleXAttributeType_in_entryRuleXAttributeType1218);
            iv_ruleXAttributeType=ruleXAttributeType();

            state._fsp--;

             current =iv_ruleXAttributeType; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleXAttributeType1228); 

            }

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:614:1: ruleXAttributeType returns [EObject current=null] : (otherlv_0= 'attributeType' ( (lv_name_1_0= RULE_STRING ) ) (otherlv_2= 'extends' ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) ) ) (otherlv_4= 'overrides' ( (otherlv_5= RULE_STRING ) ) )? otherlv_6= '{' otherlv_7= 'guid' ( (lv_typeGuid_8_0= RULE_STRING ) ) otherlv_9= 'uuid' ( (lv_uuid_10_0= RULE_HEX_INT ) ) otherlv_11= 'dataProvider' ( ( (lv_dataProvider_12_1= 'DefaultAttributeDataProvider' | lv_dataProvider_12_2= 'UriAttributeDataProvider' | lv_dataProvider_12_3= ruleQUALIFIED_NAME ) ) ) otherlv_13= 'min' ( (lv_min_14_0= RULE_WHOLE_NUM_STR ) ) otherlv_15= 'max' ( ( (lv_max_16_1= RULE_WHOLE_NUM_STR | lv_max_16_2= 'unlimited' ) ) ) (otherlv_17= 'taggerId' ( ( (lv_taggerId_18_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_18_2= ruleQUALIFIED_NAME ) ) ) )? (otherlv_19= 'enumType' ( (otherlv_20= RULE_STRING ) ) )? (otherlv_21= 'description' ( (lv_description_22_0= RULE_STRING ) ) )? (otherlv_23= 'defaultValue' ( (lv_defaultValue_24_0= RULE_STRING ) ) )? (otherlv_25= 'fileExtension' ( (lv_fileExtension_26_0= RULE_STRING ) ) )? otherlv_27= '}' ) ;
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
        Token lv_uuid_10_0=null;
        Token otherlv_11=null;
        Token lv_dataProvider_12_1=null;
        Token lv_dataProvider_12_2=null;
        Token otherlv_13=null;
        Token lv_min_14_0=null;
        Token otherlv_15=null;
        Token lv_max_16_1=null;
        Token lv_max_16_2=null;
        Token otherlv_17=null;
        Token lv_taggerId_18_1=null;
        Token otherlv_19=null;
        Token otherlv_20=null;
        Token otherlv_21=null;
        Token lv_description_22_0=null;
        Token otherlv_23=null;
        Token lv_defaultValue_24_0=null;
        Token otherlv_25=null;
        Token lv_fileExtension_26_0=null;
        Token otherlv_27=null;
        AntlrDatatypeRuleToken lv_baseAttributeType_3_0 = null;

        AntlrDatatypeRuleToken lv_dataProvider_12_3 = null;

        AntlrDatatypeRuleToken lv_taggerId_18_2 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:617:28: ( (otherlv_0= 'attributeType' ( (lv_name_1_0= RULE_STRING ) ) (otherlv_2= 'extends' ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) ) ) (otherlv_4= 'overrides' ( (otherlv_5= RULE_STRING ) ) )? otherlv_6= '{' otherlv_7= 'guid' ( (lv_typeGuid_8_0= RULE_STRING ) ) otherlv_9= 'uuid' ( (lv_uuid_10_0= RULE_HEX_INT ) ) otherlv_11= 'dataProvider' ( ( (lv_dataProvider_12_1= 'DefaultAttributeDataProvider' | lv_dataProvider_12_2= 'UriAttributeDataProvider' | lv_dataProvider_12_3= ruleQUALIFIED_NAME ) ) ) otherlv_13= 'min' ( (lv_min_14_0= RULE_WHOLE_NUM_STR ) ) otherlv_15= 'max' ( ( (lv_max_16_1= RULE_WHOLE_NUM_STR | lv_max_16_2= 'unlimited' ) ) ) (otherlv_17= 'taggerId' ( ( (lv_taggerId_18_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_18_2= ruleQUALIFIED_NAME ) ) ) )? (otherlv_19= 'enumType' ( (otherlv_20= RULE_STRING ) ) )? (otherlv_21= 'description' ( (lv_description_22_0= RULE_STRING ) ) )? (otherlv_23= 'defaultValue' ( (lv_defaultValue_24_0= RULE_STRING ) ) )? (otherlv_25= 'fileExtension' ( (lv_fileExtension_26_0= RULE_STRING ) ) )? otherlv_27= '}' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:618:1: (otherlv_0= 'attributeType' ( (lv_name_1_0= RULE_STRING ) ) (otherlv_2= 'extends' ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) ) ) (otherlv_4= 'overrides' ( (otherlv_5= RULE_STRING ) ) )? otherlv_6= '{' otherlv_7= 'guid' ( (lv_typeGuid_8_0= RULE_STRING ) ) otherlv_9= 'uuid' ( (lv_uuid_10_0= RULE_HEX_INT ) ) otherlv_11= 'dataProvider' ( ( (lv_dataProvider_12_1= 'DefaultAttributeDataProvider' | lv_dataProvider_12_2= 'UriAttributeDataProvider' | lv_dataProvider_12_3= ruleQUALIFIED_NAME ) ) ) otherlv_13= 'min' ( (lv_min_14_0= RULE_WHOLE_NUM_STR ) ) otherlv_15= 'max' ( ( (lv_max_16_1= RULE_WHOLE_NUM_STR | lv_max_16_2= 'unlimited' ) ) ) (otherlv_17= 'taggerId' ( ( (lv_taggerId_18_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_18_2= ruleQUALIFIED_NAME ) ) ) )? (otherlv_19= 'enumType' ( (otherlv_20= RULE_STRING ) ) )? (otherlv_21= 'description' ( (lv_description_22_0= RULE_STRING ) ) )? (otherlv_23= 'defaultValue' ( (lv_defaultValue_24_0= RULE_STRING ) ) )? (otherlv_25= 'fileExtension' ( (lv_fileExtension_26_0= RULE_STRING ) ) )? otherlv_27= '}' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:618:1: (otherlv_0= 'attributeType' ( (lv_name_1_0= RULE_STRING ) ) (otherlv_2= 'extends' ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) ) ) (otherlv_4= 'overrides' ( (otherlv_5= RULE_STRING ) ) )? otherlv_6= '{' otherlv_7= 'guid' ( (lv_typeGuid_8_0= RULE_STRING ) ) otherlv_9= 'uuid' ( (lv_uuid_10_0= RULE_HEX_INT ) ) otherlv_11= 'dataProvider' ( ( (lv_dataProvider_12_1= 'DefaultAttributeDataProvider' | lv_dataProvider_12_2= 'UriAttributeDataProvider' | lv_dataProvider_12_3= ruleQUALIFIED_NAME ) ) ) otherlv_13= 'min' ( (lv_min_14_0= RULE_WHOLE_NUM_STR ) ) otherlv_15= 'max' ( ( (lv_max_16_1= RULE_WHOLE_NUM_STR | lv_max_16_2= 'unlimited' ) ) ) (otherlv_17= 'taggerId' ( ( (lv_taggerId_18_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_18_2= ruleQUALIFIED_NAME ) ) ) )? (otherlv_19= 'enumType' ( (otherlv_20= RULE_STRING ) ) )? (otherlv_21= 'description' ( (lv_description_22_0= RULE_STRING ) ) )? (otherlv_23= 'defaultValue' ( (lv_defaultValue_24_0= RULE_STRING ) ) )? (otherlv_25= 'fileExtension' ( (lv_fileExtension_26_0= RULE_STRING ) ) )? otherlv_27= '}' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:618:3: otherlv_0= 'attributeType' ( (lv_name_1_0= RULE_STRING ) ) (otherlv_2= 'extends' ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) ) ) (otherlv_4= 'overrides' ( (otherlv_5= RULE_STRING ) ) )? otherlv_6= '{' otherlv_7= 'guid' ( (lv_typeGuid_8_0= RULE_STRING ) ) otherlv_9= 'uuid' ( (lv_uuid_10_0= RULE_HEX_INT ) ) otherlv_11= 'dataProvider' ( ( (lv_dataProvider_12_1= 'DefaultAttributeDataProvider' | lv_dataProvider_12_2= 'UriAttributeDataProvider' | lv_dataProvider_12_3= ruleQUALIFIED_NAME ) ) ) otherlv_13= 'min' ( (lv_min_14_0= RULE_WHOLE_NUM_STR ) ) otherlv_15= 'max' ( ( (lv_max_16_1= RULE_WHOLE_NUM_STR | lv_max_16_2= 'unlimited' ) ) ) (otherlv_17= 'taggerId' ( ( (lv_taggerId_18_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_18_2= ruleQUALIFIED_NAME ) ) ) )? (otherlv_19= 'enumType' ( (otherlv_20= RULE_STRING ) ) )? (otherlv_21= 'description' ( (lv_description_22_0= RULE_STRING ) ) )? (otherlv_23= 'defaultValue' ( (lv_defaultValue_24_0= RULE_STRING ) ) )? (otherlv_25= 'fileExtension' ( (lv_fileExtension_26_0= RULE_STRING ) ) )? otherlv_27= '}'
            {
            otherlv_0=(Token)match(input,25,FOLLOW_25_in_ruleXAttributeType1265); 

                	newLeafNode(otherlv_0, grammarAccess.getXAttributeTypeAccess().getAttributeTypeKeyword_0());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:622:1: ( (lv_name_1_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:623:1: (lv_name_1_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:623:1: (lv_name_1_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:624:3: lv_name_1_0= RULE_STRING
            {
            lv_name_1_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXAttributeType1282); 

            			newLeafNode(lv_name_1_0, grammarAccess.getXAttributeTypeAccess().getNameSTRINGTerminalRuleCall_1_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getXAttributeTypeRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"name",
                    		lv_name_1_0, 
                    		"STRING");
            	    

            }


            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:640:2: (otherlv_2= 'extends' ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:640:4: otherlv_2= 'extends' ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) )
            {
            otherlv_2=(Token)match(input,17,FOLLOW_17_in_ruleXAttributeType1300); 

                	newLeafNode(otherlv_2, grammarAccess.getXAttributeTypeAccess().getExtendsKeyword_2_0());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:644:1: ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:645:1: (lv_baseAttributeType_3_0= ruleAttributeBaseType )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:645:1: (lv_baseAttributeType_3_0= ruleAttributeBaseType )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:646:3: lv_baseAttributeType_3_0= ruleAttributeBaseType
            {
             
            	        newCompositeNode(grammarAccess.getXAttributeTypeAccess().getBaseAttributeTypeAttributeBaseTypeParserRuleCall_2_1_0()); 
            	    
            pushFollow(FOLLOW_ruleAttributeBaseType_in_ruleXAttributeType1321);
            lv_baseAttributeType_3_0=ruleAttributeBaseType();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getXAttributeTypeRule());
            	        }
                   		set(
                   			current, 
                   			"baseAttributeType",
                    		lv_baseAttributeType_3_0, 
                    		"AttributeBaseType");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }


            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:662:3: (otherlv_4= 'overrides' ( (otherlv_5= RULE_STRING ) ) )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==26) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:662:5: otherlv_4= 'overrides' ( (otherlv_5= RULE_STRING ) )
                    {
                    otherlv_4=(Token)match(input,26,FOLLOW_26_in_ruleXAttributeType1335); 

                        	newLeafNode(otherlv_4, grammarAccess.getXAttributeTypeAccess().getOverridesKeyword_3_0());
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:666:1: ( (otherlv_5= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:667:1: (otherlv_5= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:667:1: (otherlv_5= RULE_STRING )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:668:3: otherlv_5= RULE_STRING
                    {

                    			if (current==null) {
                    	            current = createModelElement(grammarAccess.getXAttributeTypeRule());
                    	        }
                            
                    otherlv_5=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXAttributeType1355); 

                    		newLeafNode(otherlv_5, grammarAccess.getXAttributeTypeAccess().getOverrideXAttributeTypeCrossReference_3_1_0()); 
                    	

                    }


                    }


                    }
                    break;

            }

            otherlv_6=(Token)match(input,19,FOLLOW_19_in_ruleXAttributeType1369); 

                	newLeafNode(otherlv_6, grammarAccess.getXAttributeTypeAccess().getLeftCurlyBracketKeyword_4());
                
            otherlv_7=(Token)match(input,20,FOLLOW_20_in_ruleXAttributeType1381); 

                	newLeafNode(otherlv_7, grammarAccess.getXAttributeTypeAccess().getGuidKeyword_5());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:687:1: ( (lv_typeGuid_8_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:688:1: (lv_typeGuid_8_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:688:1: (lv_typeGuid_8_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:689:3: lv_typeGuid_8_0= RULE_STRING
            {
            lv_typeGuid_8_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXAttributeType1398); 

            			newLeafNode(lv_typeGuid_8_0, grammarAccess.getXAttributeTypeAccess().getTypeGuidSTRINGTerminalRuleCall_6_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getXAttributeTypeRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"typeGuid",
                    		lv_typeGuid_8_0, 
                    		"STRING");
            	    

            }


            }

            otherlv_9=(Token)match(input,21,FOLLOW_21_in_ruleXAttributeType1415); 

                	newLeafNode(otherlv_9, grammarAccess.getXAttributeTypeAccess().getUuidKeyword_7());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:709:1: ( (lv_uuid_10_0= RULE_HEX_INT ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:710:1: (lv_uuid_10_0= RULE_HEX_INT )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:710:1: (lv_uuid_10_0= RULE_HEX_INT )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:711:3: lv_uuid_10_0= RULE_HEX_INT
            {
            lv_uuid_10_0=(Token)match(input,RULE_HEX_INT,FOLLOW_RULE_HEX_INT_in_ruleXAttributeType1432); 

            			newLeafNode(lv_uuid_10_0, grammarAccess.getXAttributeTypeAccess().getUuidHEX_INTTerminalRuleCall_8_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getXAttributeTypeRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"uuid",
                    		lv_uuid_10_0, 
                    		"HEX_INT");
            	    

            }


            }

            otherlv_11=(Token)match(input,27,FOLLOW_27_in_ruleXAttributeType1449); 

                	newLeafNode(otherlv_11, grammarAccess.getXAttributeTypeAccess().getDataProviderKeyword_9());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:731:1: ( ( (lv_dataProvider_12_1= 'DefaultAttributeDataProvider' | lv_dataProvider_12_2= 'UriAttributeDataProvider' | lv_dataProvider_12_3= ruleQUALIFIED_NAME ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:732:1: ( (lv_dataProvider_12_1= 'DefaultAttributeDataProvider' | lv_dataProvider_12_2= 'UriAttributeDataProvider' | lv_dataProvider_12_3= ruleQUALIFIED_NAME ) )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:732:1: ( (lv_dataProvider_12_1= 'DefaultAttributeDataProvider' | lv_dataProvider_12_2= 'UriAttributeDataProvider' | lv_dataProvider_12_3= ruleQUALIFIED_NAME ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:733:1: (lv_dataProvider_12_1= 'DefaultAttributeDataProvider' | lv_dataProvider_12_2= 'UriAttributeDataProvider' | lv_dataProvider_12_3= ruleQUALIFIED_NAME )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:733:1: (lv_dataProvider_12_1= 'DefaultAttributeDataProvider' | lv_dataProvider_12_2= 'UriAttributeDataProvider' | lv_dataProvider_12_3= ruleQUALIFIED_NAME )
            int alt13=3;
            switch ( input.LA(1) ) {
            case 28:
                {
                alt13=1;
                }
                break;
            case 29:
                {
                alt13=2;
                }
                break;
            case RULE_ID:
                {
                alt13=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 13, 0, input);

                throw nvae;
            }

            switch (alt13) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:734:3: lv_dataProvider_12_1= 'DefaultAttributeDataProvider'
                    {
                    lv_dataProvider_12_1=(Token)match(input,28,FOLLOW_28_in_ruleXAttributeType1469); 

                            newLeafNode(lv_dataProvider_12_1, grammarAccess.getXAttributeTypeAccess().getDataProviderDefaultAttributeDataProviderKeyword_10_0_0());
                        

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getXAttributeTypeRule());
                    	        }
                           		setWithLastConsumed(current, "dataProvider", lv_dataProvider_12_1, null);
                    	    

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:746:8: lv_dataProvider_12_2= 'UriAttributeDataProvider'
                    {
                    lv_dataProvider_12_2=(Token)match(input,29,FOLLOW_29_in_ruleXAttributeType1498); 

                            newLeafNode(lv_dataProvider_12_2, grammarAccess.getXAttributeTypeAccess().getDataProviderUriAttributeDataProviderKeyword_10_0_1());
                        

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getXAttributeTypeRule());
                    	        }
                           		setWithLastConsumed(current, "dataProvider", lv_dataProvider_12_2, null);
                    	    

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:758:8: lv_dataProvider_12_3= ruleQUALIFIED_NAME
                    {
                     
                    	        newCompositeNode(grammarAccess.getXAttributeTypeAccess().getDataProviderQUALIFIED_NAMEParserRuleCall_10_0_2()); 
                    	    
                    pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_ruleXAttributeType1530);
                    lv_dataProvider_12_3=ruleQUALIFIED_NAME();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getXAttributeTypeRule());
                    	        }
                           		set(
                           			current, 
                           			"dataProvider",
                            		lv_dataProvider_12_3, 
                            		"QUALIFIED_NAME");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }
                    break;

            }


            }


            }

            otherlv_13=(Token)match(input,30,FOLLOW_30_in_ruleXAttributeType1545); 

                	newLeafNode(otherlv_13, grammarAccess.getXAttributeTypeAccess().getMinKeyword_11());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:780:1: ( (lv_min_14_0= RULE_WHOLE_NUM_STR ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:781:1: (lv_min_14_0= RULE_WHOLE_NUM_STR )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:781:1: (lv_min_14_0= RULE_WHOLE_NUM_STR )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:782:3: lv_min_14_0= RULE_WHOLE_NUM_STR
            {
            lv_min_14_0=(Token)match(input,RULE_WHOLE_NUM_STR,FOLLOW_RULE_WHOLE_NUM_STR_in_ruleXAttributeType1562); 

            			newLeafNode(lv_min_14_0, grammarAccess.getXAttributeTypeAccess().getMinWHOLE_NUM_STRTerminalRuleCall_12_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getXAttributeTypeRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"min",
                    		lv_min_14_0, 
                    		"WHOLE_NUM_STR");
            	    

            }


            }

            otherlv_15=(Token)match(input,31,FOLLOW_31_in_ruleXAttributeType1579); 

                	newLeafNode(otherlv_15, grammarAccess.getXAttributeTypeAccess().getMaxKeyword_13());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:802:1: ( ( (lv_max_16_1= RULE_WHOLE_NUM_STR | lv_max_16_2= 'unlimited' ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:803:1: ( (lv_max_16_1= RULE_WHOLE_NUM_STR | lv_max_16_2= 'unlimited' ) )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:803:1: ( (lv_max_16_1= RULE_WHOLE_NUM_STR | lv_max_16_2= 'unlimited' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:804:1: (lv_max_16_1= RULE_WHOLE_NUM_STR | lv_max_16_2= 'unlimited' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:804:1: (lv_max_16_1= RULE_WHOLE_NUM_STR | lv_max_16_2= 'unlimited' )
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==RULE_WHOLE_NUM_STR) ) {
                alt14=1;
            }
            else if ( (LA14_0==32) ) {
                alt14=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 14, 0, input);

                throw nvae;
            }
            switch (alt14) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:805:3: lv_max_16_1= RULE_WHOLE_NUM_STR
                    {
                    lv_max_16_1=(Token)match(input,RULE_WHOLE_NUM_STR,FOLLOW_RULE_WHOLE_NUM_STR_in_ruleXAttributeType1598); 

                    			newLeafNode(lv_max_16_1, grammarAccess.getXAttributeTypeAccess().getMaxWHOLE_NUM_STRTerminalRuleCall_14_0_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getXAttributeTypeRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"max",
                            		lv_max_16_1, 
                            		"WHOLE_NUM_STR");
                    	    

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:820:8: lv_max_16_2= 'unlimited'
                    {
                    lv_max_16_2=(Token)match(input,32,FOLLOW_32_in_ruleXAttributeType1619); 

                            newLeafNode(lv_max_16_2, grammarAccess.getXAttributeTypeAccess().getMaxUnlimitedKeyword_14_0_1());
                        

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getXAttributeTypeRule());
                    	        }
                           		setWithLastConsumed(current, "max", lv_max_16_2, null);
                    	    

                    }
                    break;

            }


            }


            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:835:2: (otherlv_17= 'taggerId' ( ( (lv_taggerId_18_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_18_2= ruleQUALIFIED_NAME ) ) ) )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==33) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:835:4: otherlv_17= 'taggerId' ( ( (lv_taggerId_18_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_18_2= ruleQUALIFIED_NAME ) ) )
                    {
                    otherlv_17=(Token)match(input,33,FOLLOW_33_in_ruleXAttributeType1648); 

                        	newLeafNode(otherlv_17, grammarAccess.getXAttributeTypeAccess().getTaggerIdKeyword_15_0());
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:839:1: ( ( (lv_taggerId_18_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_18_2= ruleQUALIFIED_NAME ) ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:840:1: ( (lv_taggerId_18_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_18_2= ruleQUALIFIED_NAME ) )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:840:1: ( (lv_taggerId_18_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_18_2= ruleQUALIFIED_NAME ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:841:1: (lv_taggerId_18_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_18_2= ruleQUALIFIED_NAME )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:841:1: (lv_taggerId_18_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_18_2= ruleQUALIFIED_NAME )
                    int alt15=2;
                    int LA15_0 = input.LA(1);

                    if ( (LA15_0==34) ) {
                        alt15=1;
                    }
                    else if ( (LA15_0==RULE_ID) ) {
                        alt15=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 15, 0, input);

                        throw nvae;
                    }
                    switch (alt15) {
                        case 1 :
                            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:842:3: lv_taggerId_18_1= 'DefaultAttributeTaggerProvider'
                            {
                            lv_taggerId_18_1=(Token)match(input,34,FOLLOW_34_in_ruleXAttributeType1668); 

                                    newLeafNode(lv_taggerId_18_1, grammarAccess.getXAttributeTypeAccess().getTaggerIdDefaultAttributeTaggerProviderKeyword_15_1_0_0());
                                

                            	        if (current==null) {
                            	            current = createModelElement(grammarAccess.getXAttributeTypeRule());
                            	        }
                                   		setWithLastConsumed(current, "taggerId", lv_taggerId_18_1, null);
                            	    

                            }
                            break;
                        case 2 :
                            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:854:8: lv_taggerId_18_2= ruleQUALIFIED_NAME
                            {
                             
                            	        newCompositeNode(grammarAccess.getXAttributeTypeAccess().getTaggerIdQUALIFIED_NAMEParserRuleCall_15_1_0_1()); 
                            	    
                            pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_ruleXAttributeType1700);
                            lv_taggerId_18_2=ruleQUALIFIED_NAME();

                            state._fsp--;


                            	        if (current==null) {
                            	            current = createModelElementForParent(grammarAccess.getXAttributeTypeRule());
                            	        }
                                   		set(
                                   			current, 
                                   			"taggerId",
                                    		lv_taggerId_18_2, 
                                    		"QUALIFIED_NAME");
                            	        afterParserOrEnumRuleCall();
                            	    

                            }
                            break;

                    }


                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:872:4: (otherlv_19= 'enumType' ( (otherlv_20= RULE_STRING ) ) )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==35) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:872:6: otherlv_19= 'enumType' ( (otherlv_20= RULE_STRING ) )
                    {
                    otherlv_19=(Token)match(input,35,FOLLOW_35_in_ruleXAttributeType1718); 

                        	newLeafNode(otherlv_19, grammarAccess.getXAttributeTypeAccess().getEnumTypeKeyword_16_0());
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:876:1: ( (otherlv_20= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:877:1: (otherlv_20= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:877:1: (otherlv_20= RULE_STRING )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:878:3: otherlv_20= RULE_STRING
                    {

                    			if (current==null) {
                    	            current = createModelElement(grammarAccess.getXAttributeTypeRule());
                    	        }
                            
                    otherlv_20=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXAttributeType1738); 

                    		newLeafNode(otherlv_20, grammarAccess.getXAttributeTypeAccess().getEnumTypeXOseeEnumTypeCrossReference_16_1_0()); 
                    	

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:889:4: (otherlv_21= 'description' ( (lv_description_22_0= RULE_STRING ) ) )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==36) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:889:6: otherlv_21= 'description' ( (lv_description_22_0= RULE_STRING ) )
                    {
                    otherlv_21=(Token)match(input,36,FOLLOW_36_in_ruleXAttributeType1753); 

                        	newLeafNode(otherlv_21, grammarAccess.getXAttributeTypeAccess().getDescriptionKeyword_17_0());
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:893:1: ( (lv_description_22_0= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:894:1: (lv_description_22_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:894:1: (lv_description_22_0= RULE_STRING )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:895:3: lv_description_22_0= RULE_STRING
                    {
                    lv_description_22_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXAttributeType1770); 

                    			newLeafNode(lv_description_22_0, grammarAccess.getXAttributeTypeAccess().getDescriptionSTRINGTerminalRuleCall_17_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getXAttributeTypeRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"description",
                            		lv_description_22_0, 
                            		"STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:911:4: (otherlv_23= 'defaultValue' ( (lv_defaultValue_24_0= RULE_STRING ) ) )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==37) ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:911:6: otherlv_23= 'defaultValue' ( (lv_defaultValue_24_0= RULE_STRING ) )
                    {
                    otherlv_23=(Token)match(input,37,FOLLOW_37_in_ruleXAttributeType1790); 

                        	newLeafNode(otherlv_23, grammarAccess.getXAttributeTypeAccess().getDefaultValueKeyword_18_0());
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:915:1: ( (lv_defaultValue_24_0= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:916:1: (lv_defaultValue_24_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:916:1: (lv_defaultValue_24_0= RULE_STRING )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:917:3: lv_defaultValue_24_0= RULE_STRING
                    {
                    lv_defaultValue_24_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXAttributeType1807); 

                    			newLeafNode(lv_defaultValue_24_0, grammarAccess.getXAttributeTypeAccess().getDefaultValueSTRINGTerminalRuleCall_18_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getXAttributeTypeRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"defaultValue",
                            		lv_defaultValue_24_0, 
                            		"STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:933:4: (otherlv_25= 'fileExtension' ( (lv_fileExtension_26_0= RULE_STRING ) ) )?
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==38) ) {
                alt20=1;
            }
            switch (alt20) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:933:6: otherlv_25= 'fileExtension' ( (lv_fileExtension_26_0= RULE_STRING ) )
                    {
                    otherlv_25=(Token)match(input,38,FOLLOW_38_in_ruleXAttributeType1827); 

                        	newLeafNode(otherlv_25, grammarAccess.getXAttributeTypeAccess().getFileExtensionKeyword_19_0());
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:937:1: ( (lv_fileExtension_26_0= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:938:1: (lv_fileExtension_26_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:938:1: (lv_fileExtension_26_0= RULE_STRING )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:939:3: lv_fileExtension_26_0= RULE_STRING
                    {
                    lv_fileExtension_26_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXAttributeType1844); 

                    			newLeafNode(lv_fileExtension_26_0, grammarAccess.getXAttributeTypeAccess().getFileExtensionSTRINGTerminalRuleCall_19_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getXAttributeTypeRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"fileExtension",
                            		lv_fileExtension_26_0, 
                            		"STRING");
                    	    

                    }


                    }


                    }
                    break;

            }

            otherlv_27=(Token)match(input,22,FOLLOW_22_in_ruleXAttributeType1863); 

                	newLeafNode(otherlv_27, grammarAccess.getXAttributeTypeAccess().getRightCurlyBracketKeyword_20());
                

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:967:1: entryRuleAttributeBaseType returns [String current=null] : iv_ruleAttributeBaseType= ruleAttributeBaseType EOF ;
    public final String entryRuleAttributeBaseType() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleAttributeBaseType = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:968:2: (iv_ruleAttributeBaseType= ruleAttributeBaseType EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:969:2: iv_ruleAttributeBaseType= ruleAttributeBaseType EOF
            {
             newCompositeNode(grammarAccess.getAttributeBaseTypeRule()); 
            pushFollow(FOLLOW_ruleAttributeBaseType_in_entryRuleAttributeBaseType1900);
            iv_ruleAttributeBaseType=ruleAttributeBaseType();

            state._fsp--;

             current =iv_ruleAttributeBaseType.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttributeBaseType1911); 

            }

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:976:1: ruleAttributeBaseType returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'BooleanAttribute' | kw= 'CompressedContentAttribute' | kw= 'DateAttribute' | kw= 'EnumeratedAttribute' | kw= 'FloatingPointAttribute' | kw= 'IntegerAttribute' | kw= 'JavaObjectAttribute' | kw= 'StringAttribute' | kw= 'WordAttribute' | this_QUALIFIED_NAME_9= ruleQUALIFIED_NAME ) ;
    public final AntlrDatatypeRuleToken ruleAttributeBaseType() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        AntlrDatatypeRuleToken this_QUALIFIED_NAME_9 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:979:28: ( (kw= 'BooleanAttribute' | kw= 'CompressedContentAttribute' | kw= 'DateAttribute' | kw= 'EnumeratedAttribute' | kw= 'FloatingPointAttribute' | kw= 'IntegerAttribute' | kw= 'JavaObjectAttribute' | kw= 'StringAttribute' | kw= 'WordAttribute' | this_QUALIFIED_NAME_9= ruleQUALIFIED_NAME ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:980:1: (kw= 'BooleanAttribute' | kw= 'CompressedContentAttribute' | kw= 'DateAttribute' | kw= 'EnumeratedAttribute' | kw= 'FloatingPointAttribute' | kw= 'IntegerAttribute' | kw= 'JavaObjectAttribute' | kw= 'StringAttribute' | kw= 'WordAttribute' | this_QUALIFIED_NAME_9= ruleQUALIFIED_NAME )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:980:1: (kw= 'BooleanAttribute' | kw= 'CompressedContentAttribute' | kw= 'DateAttribute' | kw= 'EnumeratedAttribute' | kw= 'FloatingPointAttribute' | kw= 'IntegerAttribute' | kw= 'JavaObjectAttribute' | kw= 'StringAttribute' | kw= 'WordAttribute' | this_QUALIFIED_NAME_9= ruleQUALIFIED_NAME )
            int alt21=10;
            switch ( input.LA(1) ) {
            case 39:
                {
                alt21=1;
                }
                break;
            case 40:
                {
                alt21=2;
                }
                break;
            case 41:
                {
                alt21=3;
                }
                break;
            case 42:
                {
                alt21=4;
                }
                break;
            case 43:
                {
                alt21=5;
                }
                break;
            case 44:
                {
                alt21=6;
                }
                break;
            case 45:
                {
                alt21=7;
                }
                break;
            case 46:
                {
                alt21=8;
                }
                break;
            case 47:
                {
                alt21=9;
                }
                break;
            case RULE_ID:
                {
                alt21=10;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 21, 0, input);

                throw nvae;
            }

            switch (alt21) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:981:2: kw= 'BooleanAttribute'
                    {
                    kw=(Token)match(input,39,FOLLOW_39_in_ruleAttributeBaseType1949); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getAttributeBaseTypeAccess().getBooleanAttributeKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:988:2: kw= 'CompressedContentAttribute'
                    {
                    kw=(Token)match(input,40,FOLLOW_40_in_ruleAttributeBaseType1968); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getAttributeBaseTypeAccess().getCompressedContentAttributeKeyword_1()); 
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:995:2: kw= 'DateAttribute'
                    {
                    kw=(Token)match(input,41,FOLLOW_41_in_ruleAttributeBaseType1987); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getAttributeBaseTypeAccess().getDateAttributeKeyword_2()); 
                        

                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1002:2: kw= 'EnumeratedAttribute'
                    {
                    kw=(Token)match(input,42,FOLLOW_42_in_ruleAttributeBaseType2006); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getAttributeBaseTypeAccess().getEnumeratedAttributeKeyword_3()); 
                        

                    }
                    break;
                case 5 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1009:2: kw= 'FloatingPointAttribute'
                    {
                    kw=(Token)match(input,43,FOLLOW_43_in_ruleAttributeBaseType2025); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getAttributeBaseTypeAccess().getFloatingPointAttributeKeyword_4()); 
                        

                    }
                    break;
                case 6 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1016:2: kw= 'IntegerAttribute'
                    {
                    kw=(Token)match(input,44,FOLLOW_44_in_ruleAttributeBaseType2044); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getAttributeBaseTypeAccess().getIntegerAttributeKeyword_5()); 
                        

                    }
                    break;
                case 7 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1023:2: kw= 'JavaObjectAttribute'
                    {
                    kw=(Token)match(input,45,FOLLOW_45_in_ruleAttributeBaseType2063); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getAttributeBaseTypeAccess().getJavaObjectAttributeKeyword_6()); 
                        

                    }
                    break;
                case 8 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1030:2: kw= 'StringAttribute'
                    {
                    kw=(Token)match(input,46,FOLLOW_46_in_ruleAttributeBaseType2082); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getAttributeBaseTypeAccess().getStringAttributeKeyword_7()); 
                        

                    }
                    break;
                case 9 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1037:2: kw= 'WordAttribute'
                    {
                    kw=(Token)match(input,47,FOLLOW_47_in_ruleAttributeBaseType2101); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getAttributeBaseTypeAccess().getWordAttributeKeyword_8()); 
                        

                    }
                    break;
                case 10 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1044:5: this_QUALIFIED_NAME_9= ruleQUALIFIED_NAME
                    {
                     
                            newCompositeNode(grammarAccess.getAttributeBaseTypeAccess().getQUALIFIED_NAMEParserRuleCall_9()); 
                        
                    pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_ruleAttributeBaseType2129);
                    this_QUALIFIED_NAME_9=ruleQUALIFIED_NAME();

                    state._fsp--;


                    		current.merge(this_QUALIFIED_NAME_9);
                        
                     
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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1062:1: entryRuleXOseeEnumType returns [EObject current=null] : iv_ruleXOseeEnumType= ruleXOseeEnumType EOF ;
    public final EObject entryRuleXOseeEnumType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleXOseeEnumType = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1063:2: (iv_ruleXOseeEnumType= ruleXOseeEnumType EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1064:2: iv_ruleXOseeEnumType= ruleXOseeEnumType EOF
            {
             newCompositeNode(grammarAccess.getXOseeEnumTypeRule()); 
            pushFollow(FOLLOW_ruleXOseeEnumType_in_entryRuleXOseeEnumType2174);
            iv_ruleXOseeEnumType=ruleXOseeEnumType();

            state._fsp--;

             current =iv_ruleXOseeEnumType; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleXOseeEnumType2184); 

            }

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1071:1: ruleXOseeEnumType returns [EObject current=null] : (otherlv_0= 'oseeEnumType' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= '{' otherlv_3= 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_HEX_INT ) ) ( (lv_enumEntries_7_0= ruleXOseeEnumEntry ) )* otherlv_8= '}' ) ;
    public final EObject ruleXOseeEnumType() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_name_1_0=null;
        Token otherlv_2=null;
        Token otherlv_3=null;
        Token lv_typeGuid_4_0=null;
        Token otherlv_5=null;
        Token lv_uuid_6_0=null;
        Token otherlv_8=null;
        EObject lv_enumEntries_7_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1074:28: ( (otherlv_0= 'oseeEnumType' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= '{' otherlv_3= 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_HEX_INT ) ) ( (lv_enumEntries_7_0= ruleXOseeEnumEntry ) )* otherlv_8= '}' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1075:1: (otherlv_0= 'oseeEnumType' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= '{' otherlv_3= 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_HEX_INT ) ) ( (lv_enumEntries_7_0= ruleXOseeEnumEntry ) )* otherlv_8= '}' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1075:1: (otherlv_0= 'oseeEnumType' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= '{' otherlv_3= 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_HEX_INT ) ) ( (lv_enumEntries_7_0= ruleXOseeEnumEntry ) )* otherlv_8= '}' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1075:3: otherlv_0= 'oseeEnumType' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= '{' otherlv_3= 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_HEX_INT ) ) ( (lv_enumEntries_7_0= ruleXOseeEnumEntry ) )* otherlv_8= '}'
            {
            otherlv_0=(Token)match(input,48,FOLLOW_48_in_ruleXOseeEnumType2221); 

                	newLeafNode(otherlv_0, grammarAccess.getXOseeEnumTypeAccess().getOseeEnumTypeKeyword_0());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1079:1: ( (lv_name_1_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1080:1: (lv_name_1_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1080:1: (lv_name_1_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1081:3: lv_name_1_0= RULE_STRING
            {
            lv_name_1_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXOseeEnumType2238); 

            			newLeafNode(lv_name_1_0, grammarAccess.getXOseeEnumTypeAccess().getNameSTRINGTerminalRuleCall_1_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getXOseeEnumTypeRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"name",
                    		lv_name_1_0, 
                    		"STRING");
            	    

            }


            }

            otherlv_2=(Token)match(input,19,FOLLOW_19_in_ruleXOseeEnumType2255); 

                	newLeafNode(otherlv_2, grammarAccess.getXOseeEnumTypeAccess().getLeftCurlyBracketKeyword_2());
                
            otherlv_3=(Token)match(input,20,FOLLOW_20_in_ruleXOseeEnumType2267); 

                	newLeafNode(otherlv_3, grammarAccess.getXOseeEnumTypeAccess().getGuidKeyword_3());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1105:1: ( (lv_typeGuid_4_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1106:1: (lv_typeGuid_4_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1106:1: (lv_typeGuid_4_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1107:3: lv_typeGuid_4_0= RULE_STRING
            {
            lv_typeGuid_4_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXOseeEnumType2284); 

            			newLeafNode(lv_typeGuid_4_0, grammarAccess.getXOseeEnumTypeAccess().getTypeGuidSTRINGTerminalRuleCall_4_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getXOseeEnumTypeRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"typeGuid",
                    		lv_typeGuid_4_0, 
                    		"STRING");
            	    

            }


            }

            otherlv_5=(Token)match(input,21,FOLLOW_21_in_ruleXOseeEnumType2301); 

                	newLeafNode(otherlv_5, grammarAccess.getXOseeEnumTypeAccess().getUuidKeyword_5());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1127:1: ( (lv_uuid_6_0= RULE_HEX_INT ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1128:1: (lv_uuid_6_0= RULE_HEX_INT )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1128:1: (lv_uuid_6_0= RULE_HEX_INT )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1129:3: lv_uuid_6_0= RULE_HEX_INT
            {
            lv_uuid_6_0=(Token)match(input,RULE_HEX_INT,FOLLOW_RULE_HEX_INT_in_ruleXOseeEnumType2318); 

            			newLeafNode(lv_uuid_6_0, grammarAccess.getXOseeEnumTypeAccess().getUuidHEX_INTTerminalRuleCall_6_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getXOseeEnumTypeRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"uuid",
                    		lv_uuid_6_0, 
                    		"HEX_INT");
            	    

            }


            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1145:2: ( (lv_enumEntries_7_0= ruleXOseeEnumEntry ) )*
            loop22:
            do {
                int alt22=2;
                int LA22_0 = input.LA(1);

                if ( (LA22_0==49) ) {
                    alt22=1;
                }


                switch (alt22) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1146:1: (lv_enumEntries_7_0= ruleXOseeEnumEntry )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1146:1: (lv_enumEntries_7_0= ruleXOseeEnumEntry )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1147:3: lv_enumEntries_7_0= ruleXOseeEnumEntry
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getXOseeEnumTypeAccess().getEnumEntriesXOseeEnumEntryParserRuleCall_7_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleXOseeEnumEntry_in_ruleXOseeEnumType2344);
            	    lv_enumEntries_7_0=ruleXOseeEnumEntry();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getXOseeEnumTypeRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"enumEntries",
            	            		lv_enumEntries_7_0, 
            	            		"XOseeEnumEntry");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    break loop22;
                }
            } while (true);

            otherlv_8=(Token)match(input,22,FOLLOW_22_in_ruleXOseeEnumType2357); 

                	newLeafNode(otherlv_8, grammarAccess.getXOseeEnumTypeAccess().getRightCurlyBracketKeyword_8());
                

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1175:1: entryRuleXOseeEnumEntry returns [EObject current=null] : iv_ruleXOseeEnumEntry= ruleXOseeEnumEntry EOF ;
    public final EObject entryRuleXOseeEnumEntry() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleXOseeEnumEntry = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1176:2: (iv_ruleXOseeEnumEntry= ruleXOseeEnumEntry EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1177:2: iv_ruleXOseeEnumEntry= ruleXOseeEnumEntry EOF
            {
             newCompositeNode(grammarAccess.getXOseeEnumEntryRule()); 
            pushFollow(FOLLOW_ruleXOseeEnumEntry_in_entryRuleXOseeEnumEntry2393);
            iv_ruleXOseeEnumEntry=ruleXOseeEnumEntry();

            state._fsp--;

             current =iv_ruleXOseeEnumEntry; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleXOseeEnumEntry2403); 

            }

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1184:1: ruleXOseeEnumEntry returns [EObject current=null] : (otherlv_0= 'entry' ( (lv_name_1_0= RULE_STRING ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? (otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? ) ;
    public final EObject ruleXOseeEnumEntry() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_name_1_0=null;
        Token lv_ordinal_2_0=null;
        Token otherlv_3=null;
        Token lv_entryGuid_4_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1187:28: ( (otherlv_0= 'entry' ( (lv_name_1_0= RULE_STRING ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? (otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1188:1: (otherlv_0= 'entry' ( (lv_name_1_0= RULE_STRING ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? (otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1188:1: (otherlv_0= 'entry' ( (lv_name_1_0= RULE_STRING ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? (otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1188:3: otherlv_0= 'entry' ( (lv_name_1_0= RULE_STRING ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? (otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )?
            {
            otherlv_0=(Token)match(input,49,FOLLOW_49_in_ruleXOseeEnumEntry2440); 

                	newLeafNode(otherlv_0, grammarAccess.getXOseeEnumEntryAccess().getEntryKeyword_0());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1192:1: ( (lv_name_1_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1193:1: (lv_name_1_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1193:1: (lv_name_1_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1194:3: lv_name_1_0= RULE_STRING
            {
            lv_name_1_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXOseeEnumEntry2457); 

            			newLeafNode(lv_name_1_0, grammarAccess.getXOseeEnumEntryAccess().getNameSTRINGTerminalRuleCall_1_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getXOseeEnumEntryRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"name",
                    		lv_name_1_0, 
                    		"STRING");
            	    

            }


            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1210:2: ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==RULE_WHOLE_NUM_STR) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1211:1: (lv_ordinal_2_0= RULE_WHOLE_NUM_STR )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1211:1: (lv_ordinal_2_0= RULE_WHOLE_NUM_STR )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1212:3: lv_ordinal_2_0= RULE_WHOLE_NUM_STR
                    {
                    lv_ordinal_2_0=(Token)match(input,RULE_WHOLE_NUM_STR,FOLLOW_RULE_WHOLE_NUM_STR_in_ruleXOseeEnumEntry2479); 

                    			newLeafNode(lv_ordinal_2_0, grammarAccess.getXOseeEnumEntryAccess().getOrdinalWHOLE_NUM_STRTerminalRuleCall_2_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getXOseeEnumEntryRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"ordinal",
                            		lv_ordinal_2_0, 
                            		"WHOLE_NUM_STR");
                    	    

                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1228:3: (otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )?
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==50) ) {
                alt24=1;
            }
            switch (alt24) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1228:5: otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) )
                    {
                    otherlv_3=(Token)match(input,50,FOLLOW_50_in_ruleXOseeEnumEntry2498); 

                        	newLeafNode(otherlv_3, grammarAccess.getXOseeEnumEntryAccess().getEntryGuidKeyword_3_0());
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1232:1: ( (lv_entryGuid_4_0= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1233:1: (lv_entryGuid_4_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1233:1: (lv_entryGuid_4_0= RULE_STRING )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1234:3: lv_entryGuid_4_0= RULE_STRING
                    {
                    lv_entryGuid_4_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXOseeEnumEntry2515); 

                    			newLeafNode(lv_entryGuid_4_0, grammarAccess.getXOseeEnumEntryAccess().getEntryGuidSTRINGTerminalRuleCall_3_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getXOseeEnumEntryRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"entryGuid",
                            		lv_entryGuid_4_0, 
                            		"STRING");
                    	    

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1258:1: entryRuleXOseeEnumOverride returns [EObject current=null] : iv_ruleXOseeEnumOverride= ruleXOseeEnumOverride EOF ;
    public final EObject entryRuleXOseeEnumOverride() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleXOseeEnumOverride = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1259:2: (iv_ruleXOseeEnumOverride= ruleXOseeEnumOverride EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1260:2: iv_ruleXOseeEnumOverride= ruleXOseeEnumOverride EOF
            {
             newCompositeNode(grammarAccess.getXOseeEnumOverrideRule()); 
            pushFollow(FOLLOW_ruleXOseeEnumOverride_in_entryRuleXOseeEnumOverride2558);
            iv_ruleXOseeEnumOverride=ruleXOseeEnumOverride();

            state._fsp--;

             current =iv_ruleXOseeEnumOverride; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleXOseeEnumOverride2568); 

            }

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1267:1: ruleXOseeEnumOverride returns [EObject current=null] : (otherlv_0= 'overrides enum' ( (otherlv_1= RULE_STRING ) ) otherlv_2= '{' ( (lv_inheritAll_3_0= 'inheritAll' ) )? ( (lv_overrideOptions_4_0= ruleOverrideOption ) )* otherlv_5= '}' ) ;
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
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1270:28: ( (otherlv_0= 'overrides enum' ( (otherlv_1= RULE_STRING ) ) otherlv_2= '{' ( (lv_inheritAll_3_0= 'inheritAll' ) )? ( (lv_overrideOptions_4_0= ruleOverrideOption ) )* otherlv_5= '}' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1271:1: (otherlv_0= 'overrides enum' ( (otherlv_1= RULE_STRING ) ) otherlv_2= '{' ( (lv_inheritAll_3_0= 'inheritAll' ) )? ( (lv_overrideOptions_4_0= ruleOverrideOption ) )* otherlv_5= '}' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1271:1: (otherlv_0= 'overrides enum' ( (otherlv_1= RULE_STRING ) ) otherlv_2= '{' ( (lv_inheritAll_3_0= 'inheritAll' ) )? ( (lv_overrideOptions_4_0= ruleOverrideOption ) )* otherlv_5= '}' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1271:3: otherlv_0= 'overrides enum' ( (otherlv_1= RULE_STRING ) ) otherlv_2= '{' ( (lv_inheritAll_3_0= 'inheritAll' ) )? ( (lv_overrideOptions_4_0= ruleOverrideOption ) )* otherlv_5= '}'
            {
            otherlv_0=(Token)match(input,51,FOLLOW_51_in_ruleXOseeEnumOverride2605); 

                	newLeafNode(otherlv_0, grammarAccess.getXOseeEnumOverrideAccess().getOverridesEnumKeyword_0());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1275:1: ( (otherlv_1= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1276:1: (otherlv_1= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1276:1: (otherlv_1= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1277:3: otherlv_1= RULE_STRING
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getXOseeEnumOverrideRule());
            	        }
                    
            otherlv_1=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXOseeEnumOverride2625); 

            		newLeafNode(otherlv_1, grammarAccess.getXOseeEnumOverrideAccess().getOverridenEnumTypeXOseeEnumTypeCrossReference_1_0()); 
            	

            }


            }

            otherlv_2=(Token)match(input,19,FOLLOW_19_in_ruleXOseeEnumOverride2637); 

                	newLeafNode(otherlv_2, grammarAccess.getXOseeEnumOverrideAccess().getLeftCurlyBracketKeyword_2());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1292:1: ( (lv_inheritAll_3_0= 'inheritAll' ) )?
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==52) ) {
                alt25=1;
            }
            switch (alt25) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1293:1: (lv_inheritAll_3_0= 'inheritAll' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1293:1: (lv_inheritAll_3_0= 'inheritAll' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1294:3: lv_inheritAll_3_0= 'inheritAll'
                    {
                    lv_inheritAll_3_0=(Token)match(input,52,FOLLOW_52_in_ruleXOseeEnumOverride2655); 

                            newLeafNode(lv_inheritAll_3_0, grammarAccess.getXOseeEnumOverrideAccess().getInheritAllInheritAllKeyword_3_0());
                        

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getXOseeEnumOverrideRule());
                    	        }
                           		setWithLastConsumed(current, "inheritAll", true, "inheritAll");
                    	    

                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1307:3: ( (lv_overrideOptions_4_0= ruleOverrideOption ) )*
            loop26:
            do {
                int alt26=2;
                int LA26_0 = input.LA(1);

                if ( ((LA26_0>=53 && LA26_0<=54)) ) {
                    alt26=1;
                }


                switch (alt26) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1308:1: (lv_overrideOptions_4_0= ruleOverrideOption )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1308:1: (lv_overrideOptions_4_0= ruleOverrideOption )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1309:3: lv_overrideOptions_4_0= ruleOverrideOption
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getXOseeEnumOverrideAccess().getOverrideOptionsOverrideOptionParserRuleCall_4_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleOverrideOption_in_ruleXOseeEnumOverride2690);
            	    lv_overrideOptions_4_0=ruleOverrideOption();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getXOseeEnumOverrideRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"overrideOptions",
            	            		lv_overrideOptions_4_0, 
            	            		"OverrideOption");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    break loop26;
                }
            } while (true);

            otherlv_5=(Token)match(input,22,FOLLOW_22_in_ruleXOseeEnumOverride2703); 

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1337:1: entryRuleOverrideOption returns [EObject current=null] : iv_ruleOverrideOption= ruleOverrideOption EOF ;
    public final EObject entryRuleOverrideOption() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOverrideOption = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1338:2: (iv_ruleOverrideOption= ruleOverrideOption EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1339:2: iv_ruleOverrideOption= ruleOverrideOption EOF
            {
             newCompositeNode(grammarAccess.getOverrideOptionRule()); 
            pushFollow(FOLLOW_ruleOverrideOption_in_entryRuleOverrideOption2739);
            iv_ruleOverrideOption=ruleOverrideOption();

            state._fsp--;

             current =iv_ruleOverrideOption; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleOverrideOption2749); 

            }

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1346:1: ruleOverrideOption returns [EObject current=null] : (this_AddEnum_0= ruleAddEnum | this_RemoveEnum_1= ruleRemoveEnum ) ;
    public final EObject ruleOverrideOption() throws RecognitionException {
        EObject current = null;

        EObject this_AddEnum_0 = null;

        EObject this_RemoveEnum_1 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1349:28: ( (this_AddEnum_0= ruleAddEnum | this_RemoveEnum_1= ruleRemoveEnum ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1350:1: (this_AddEnum_0= ruleAddEnum | this_RemoveEnum_1= ruleRemoveEnum )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1350:1: (this_AddEnum_0= ruleAddEnum | this_RemoveEnum_1= ruleRemoveEnum )
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==53) ) {
                alt27=1;
            }
            else if ( (LA27_0==54) ) {
                alt27=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 27, 0, input);

                throw nvae;
            }
            switch (alt27) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1351:5: this_AddEnum_0= ruleAddEnum
                    {
                     
                            newCompositeNode(grammarAccess.getOverrideOptionAccess().getAddEnumParserRuleCall_0()); 
                        
                    pushFollow(FOLLOW_ruleAddEnum_in_ruleOverrideOption2796);
                    this_AddEnum_0=ruleAddEnum();

                    state._fsp--;

                     
                            current = this_AddEnum_0; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1361:5: this_RemoveEnum_1= ruleRemoveEnum
                    {
                     
                            newCompositeNode(grammarAccess.getOverrideOptionAccess().getRemoveEnumParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_ruleRemoveEnum_in_ruleOverrideOption2823);
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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1377:1: entryRuleAddEnum returns [EObject current=null] : iv_ruleAddEnum= ruleAddEnum EOF ;
    public final EObject entryRuleAddEnum() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAddEnum = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1378:2: (iv_ruleAddEnum= ruleAddEnum EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1379:2: iv_ruleAddEnum= ruleAddEnum EOF
            {
             newCompositeNode(grammarAccess.getAddEnumRule()); 
            pushFollow(FOLLOW_ruleAddEnum_in_entryRuleAddEnum2858);
            iv_ruleAddEnum=ruleAddEnum();

            state._fsp--;

             current =iv_ruleAddEnum; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAddEnum2868); 

            }

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1386:1: ruleAddEnum returns [EObject current=null] : (otherlv_0= 'add' ( (lv_enumEntry_1_0= RULE_STRING ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? (otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? ) ;
    public final EObject ruleAddEnum() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_enumEntry_1_0=null;
        Token lv_ordinal_2_0=null;
        Token otherlv_3=null;
        Token lv_entryGuid_4_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1389:28: ( (otherlv_0= 'add' ( (lv_enumEntry_1_0= RULE_STRING ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? (otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1390:1: (otherlv_0= 'add' ( (lv_enumEntry_1_0= RULE_STRING ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? (otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1390:1: (otherlv_0= 'add' ( (lv_enumEntry_1_0= RULE_STRING ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? (otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1390:3: otherlv_0= 'add' ( (lv_enumEntry_1_0= RULE_STRING ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? (otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )?
            {
            otherlv_0=(Token)match(input,53,FOLLOW_53_in_ruleAddEnum2905); 

                	newLeafNode(otherlv_0, grammarAccess.getAddEnumAccess().getAddKeyword_0());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1394:1: ( (lv_enumEntry_1_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1395:1: (lv_enumEntry_1_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1395:1: (lv_enumEntry_1_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1396:3: lv_enumEntry_1_0= RULE_STRING
            {
            lv_enumEntry_1_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAddEnum2922); 

            			newLeafNode(lv_enumEntry_1_0, grammarAccess.getAddEnumAccess().getEnumEntrySTRINGTerminalRuleCall_1_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getAddEnumRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"enumEntry",
                    		lv_enumEntry_1_0, 
                    		"STRING");
            	    

            }


            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1412:2: ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )?
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==RULE_WHOLE_NUM_STR) ) {
                alt28=1;
            }
            switch (alt28) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1413:1: (lv_ordinal_2_0= RULE_WHOLE_NUM_STR )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1413:1: (lv_ordinal_2_0= RULE_WHOLE_NUM_STR )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1414:3: lv_ordinal_2_0= RULE_WHOLE_NUM_STR
                    {
                    lv_ordinal_2_0=(Token)match(input,RULE_WHOLE_NUM_STR,FOLLOW_RULE_WHOLE_NUM_STR_in_ruleAddEnum2944); 

                    			newLeafNode(lv_ordinal_2_0, grammarAccess.getAddEnumAccess().getOrdinalWHOLE_NUM_STRTerminalRuleCall_2_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getAddEnumRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"ordinal",
                            		lv_ordinal_2_0, 
                            		"WHOLE_NUM_STR");
                    	    

                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1430:3: (otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )?
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==50) ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1430:5: otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) )
                    {
                    otherlv_3=(Token)match(input,50,FOLLOW_50_in_ruleAddEnum2963); 

                        	newLeafNode(otherlv_3, grammarAccess.getAddEnumAccess().getEntryGuidKeyword_3_0());
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1434:1: ( (lv_entryGuid_4_0= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1435:1: (lv_entryGuid_4_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1435:1: (lv_entryGuid_4_0= RULE_STRING )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1436:3: lv_entryGuid_4_0= RULE_STRING
                    {
                    lv_entryGuid_4_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAddEnum2980); 

                    			newLeafNode(lv_entryGuid_4_0, grammarAccess.getAddEnumAccess().getEntryGuidSTRINGTerminalRuleCall_3_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getAddEnumRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"entryGuid",
                            		lv_entryGuid_4_0, 
                            		"STRING");
                    	    

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1460:1: entryRuleRemoveEnum returns [EObject current=null] : iv_ruleRemoveEnum= ruleRemoveEnum EOF ;
    public final EObject entryRuleRemoveEnum() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRemoveEnum = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1461:2: (iv_ruleRemoveEnum= ruleRemoveEnum EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1462:2: iv_ruleRemoveEnum= ruleRemoveEnum EOF
            {
             newCompositeNode(grammarAccess.getRemoveEnumRule()); 
            pushFollow(FOLLOW_ruleRemoveEnum_in_entryRuleRemoveEnum3023);
            iv_ruleRemoveEnum=ruleRemoveEnum();

            state._fsp--;

             current =iv_ruleRemoveEnum; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleRemoveEnum3033); 

            }

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1469:1: ruleRemoveEnum returns [EObject current=null] : (otherlv_0= 'remove' ( (otherlv_1= RULE_STRING ) ) ) ;
    public final EObject ruleRemoveEnum() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1472:28: ( (otherlv_0= 'remove' ( (otherlv_1= RULE_STRING ) ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1473:1: (otherlv_0= 'remove' ( (otherlv_1= RULE_STRING ) ) )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1473:1: (otherlv_0= 'remove' ( (otherlv_1= RULE_STRING ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1473:3: otherlv_0= 'remove' ( (otherlv_1= RULE_STRING ) )
            {
            otherlv_0=(Token)match(input,54,FOLLOW_54_in_ruleRemoveEnum3070); 

                	newLeafNode(otherlv_0, grammarAccess.getRemoveEnumAccess().getRemoveKeyword_0());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1477:1: ( (otherlv_1= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1478:1: (otherlv_1= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1478:1: (otherlv_1= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1479:3: otherlv_1= RULE_STRING
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getRemoveEnumRule());
            	        }
                    
            otherlv_1=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleRemoveEnum3090); 

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


    // $ANTLR start "entryRuleXRelationType"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1498:1: entryRuleXRelationType returns [EObject current=null] : iv_ruleXRelationType= ruleXRelationType EOF ;
    public final EObject entryRuleXRelationType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleXRelationType = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1499:2: (iv_ruleXRelationType= ruleXRelationType EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1500:2: iv_ruleXRelationType= ruleXRelationType EOF
            {
             newCompositeNode(grammarAccess.getXRelationTypeRule()); 
            pushFollow(FOLLOW_ruleXRelationType_in_entryRuleXRelationType3126);
            iv_ruleXRelationType=ruleXRelationType();

            state._fsp--;

             current =iv_ruleXRelationType; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleXRelationType3136); 

            }

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1507:1: ruleXRelationType returns [EObject current=null] : (otherlv_0= 'relationType' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= '{' otherlv_3= 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_HEX_INT ) ) otherlv_7= 'sideAName' ( (lv_sideAName_8_0= RULE_STRING ) ) otherlv_9= 'sideAArtifactType' ( (otherlv_10= RULE_STRING ) ) otherlv_11= 'sideBName' ( (lv_sideBName_12_0= RULE_STRING ) ) otherlv_13= 'sideBArtifactType' ( (otherlv_14= RULE_STRING ) ) otherlv_15= 'defaultOrderType' ( (lv_defaultOrderType_16_0= ruleRelationOrderType ) ) otherlv_17= 'multiplicity' ( (lv_multiplicity_18_0= ruleRelationMultiplicityEnum ) ) otherlv_19= '}' ) ;
    public final EObject ruleXRelationType() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_name_1_0=null;
        Token otherlv_2=null;
        Token otherlv_3=null;
        Token lv_typeGuid_4_0=null;
        Token otherlv_5=null;
        Token lv_uuid_6_0=null;
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
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1510:28: ( (otherlv_0= 'relationType' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= '{' otherlv_3= 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_HEX_INT ) ) otherlv_7= 'sideAName' ( (lv_sideAName_8_0= RULE_STRING ) ) otherlv_9= 'sideAArtifactType' ( (otherlv_10= RULE_STRING ) ) otherlv_11= 'sideBName' ( (lv_sideBName_12_0= RULE_STRING ) ) otherlv_13= 'sideBArtifactType' ( (otherlv_14= RULE_STRING ) ) otherlv_15= 'defaultOrderType' ( (lv_defaultOrderType_16_0= ruleRelationOrderType ) ) otherlv_17= 'multiplicity' ( (lv_multiplicity_18_0= ruleRelationMultiplicityEnum ) ) otherlv_19= '}' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1511:1: (otherlv_0= 'relationType' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= '{' otherlv_3= 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_HEX_INT ) ) otherlv_7= 'sideAName' ( (lv_sideAName_8_0= RULE_STRING ) ) otherlv_9= 'sideAArtifactType' ( (otherlv_10= RULE_STRING ) ) otherlv_11= 'sideBName' ( (lv_sideBName_12_0= RULE_STRING ) ) otherlv_13= 'sideBArtifactType' ( (otherlv_14= RULE_STRING ) ) otherlv_15= 'defaultOrderType' ( (lv_defaultOrderType_16_0= ruleRelationOrderType ) ) otherlv_17= 'multiplicity' ( (lv_multiplicity_18_0= ruleRelationMultiplicityEnum ) ) otherlv_19= '}' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1511:1: (otherlv_0= 'relationType' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= '{' otherlv_3= 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_HEX_INT ) ) otherlv_7= 'sideAName' ( (lv_sideAName_8_0= RULE_STRING ) ) otherlv_9= 'sideAArtifactType' ( (otherlv_10= RULE_STRING ) ) otherlv_11= 'sideBName' ( (lv_sideBName_12_0= RULE_STRING ) ) otherlv_13= 'sideBArtifactType' ( (otherlv_14= RULE_STRING ) ) otherlv_15= 'defaultOrderType' ( (lv_defaultOrderType_16_0= ruleRelationOrderType ) ) otherlv_17= 'multiplicity' ( (lv_multiplicity_18_0= ruleRelationMultiplicityEnum ) ) otherlv_19= '}' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1511:3: otherlv_0= 'relationType' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= '{' otherlv_3= 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_HEX_INT ) ) otherlv_7= 'sideAName' ( (lv_sideAName_8_0= RULE_STRING ) ) otherlv_9= 'sideAArtifactType' ( (otherlv_10= RULE_STRING ) ) otherlv_11= 'sideBName' ( (lv_sideBName_12_0= RULE_STRING ) ) otherlv_13= 'sideBArtifactType' ( (otherlv_14= RULE_STRING ) ) otherlv_15= 'defaultOrderType' ( (lv_defaultOrderType_16_0= ruleRelationOrderType ) ) otherlv_17= 'multiplicity' ( (lv_multiplicity_18_0= ruleRelationMultiplicityEnum ) ) otherlv_19= '}'
            {
            otherlv_0=(Token)match(input,55,FOLLOW_55_in_ruleXRelationType3173); 

                	newLeafNode(otherlv_0, grammarAccess.getXRelationTypeAccess().getRelationTypeKeyword_0());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1515:1: ( (lv_name_1_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1516:1: (lv_name_1_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1516:1: (lv_name_1_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1517:3: lv_name_1_0= RULE_STRING
            {
            lv_name_1_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXRelationType3190); 

            			newLeafNode(lv_name_1_0, grammarAccess.getXRelationTypeAccess().getNameSTRINGTerminalRuleCall_1_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getXRelationTypeRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"name",
                    		lv_name_1_0, 
                    		"STRING");
            	    

            }


            }

            otherlv_2=(Token)match(input,19,FOLLOW_19_in_ruleXRelationType3207); 

                	newLeafNode(otherlv_2, grammarAccess.getXRelationTypeAccess().getLeftCurlyBracketKeyword_2());
                
            otherlv_3=(Token)match(input,20,FOLLOW_20_in_ruleXRelationType3219); 

                	newLeafNode(otherlv_3, grammarAccess.getXRelationTypeAccess().getGuidKeyword_3());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1541:1: ( (lv_typeGuid_4_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1542:1: (lv_typeGuid_4_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1542:1: (lv_typeGuid_4_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1543:3: lv_typeGuid_4_0= RULE_STRING
            {
            lv_typeGuid_4_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXRelationType3236); 

            			newLeafNode(lv_typeGuid_4_0, grammarAccess.getXRelationTypeAccess().getTypeGuidSTRINGTerminalRuleCall_4_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getXRelationTypeRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"typeGuid",
                    		lv_typeGuid_4_0, 
                    		"STRING");
            	    

            }


            }

            otherlv_5=(Token)match(input,21,FOLLOW_21_in_ruleXRelationType3253); 

                	newLeafNode(otherlv_5, grammarAccess.getXRelationTypeAccess().getUuidKeyword_5());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1563:1: ( (lv_uuid_6_0= RULE_HEX_INT ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1564:1: (lv_uuid_6_0= RULE_HEX_INT )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1564:1: (lv_uuid_6_0= RULE_HEX_INT )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1565:3: lv_uuid_6_0= RULE_HEX_INT
            {
            lv_uuid_6_0=(Token)match(input,RULE_HEX_INT,FOLLOW_RULE_HEX_INT_in_ruleXRelationType3270); 

            			newLeafNode(lv_uuid_6_0, grammarAccess.getXRelationTypeAccess().getUuidHEX_INTTerminalRuleCall_6_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getXRelationTypeRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"uuid",
                    		lv_uuid_6_0, 
                    		"HEX_INT");
            	    

            }


            }

            otherlv_7=(Token)match(input,56,FOLLOW_56_in_ruleXRelationType3287); 

                	newLeafNode(otherlv_7, grammarAccess.getXRelationTypeAccess().getSideANameKeyword_7());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1585:1: ( (lv_sideAName_8_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1586:1: (lv_sideAName_8_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1586:1: (lv_sideAName_8_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1587:3: lv_sideAName_8_0= RULE_STRING
            {
            lv_sideAName_8_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXRelationType3304); 

            			newLeafNode(lv_sideAName_8_0, grammarAccess.getXRelationTypeAccess().getSideANameSTRINGTerminalRuleCall_8_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getXRelationTypeRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"sideAName",
                    		lv_sideAName_8_0, 
                    		"STRING");
            	    

            }


            }

            otherlv_9=(Token)match(input,57,FOLLOW_57_in_ruleXRelationType3321); 

                	newLeafNode(otherlv_9, grammarAccess.getXRelationTypeAccess().getSideAArtifactTypeKeyword_9());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1607:1: ( (otherlv_10= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1608:1: (otherlv_10= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1608:1: (otherlv_10= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1609:3: otherlv_10= RULE_STRING
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getXRelationTypeRule());
            	        }
                    
            otherlv_10=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXRelationType3341); 

            		newLeafNode(otherlv_10, grammarAccess.getXRelationTypeAccess().getSideAArtifactTypeXArtifactTypeCrossReference_10_0()); 
            	

            }


            }

            otherlv_11=(Token)match(input,58,FOLLOW_58_in_ruleXRelationType3353); 

                	newLeafNode(otherlv_11, grammarAccess.getXRelationTypeAccess().getSideBNameKeyword_11());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1624:1: ( (lv_sideBName_12_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1625:1: (lv_sideBName_12_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1625:1: (lv_sideBName_12_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1626:3: lv_sideBName_12_0= RULE_STRING
            {
            lv_sideBName_12_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXRelationType3370); 

            			newLeafNode(lv_sideBName_12_0, grammarAccess.getXRelationTypeAccess().getSideBNameSTRINGTerminalRuleCall_12_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getXRelationTypeRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"sideBName",
                    		lv_sideBName_12_0, 
                    		"STRING");
            	    

            }


            }

            otherlv_13=(Token)match(input,59,FOLLOW_59_in_ruleXRelationType3387); 

                	newLeafNode(otherlv_13, grammarAccess.getXRelationTypeAccess().getSideBArtifactTypeKeyword_13());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1646:1: ( (otherlv_14= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1647:1: (otherlv_14= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1647:1: (otherlv_14= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1648:3: otherlv_14= RULE_STRING
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getXRelationTypeRule());
            	        }
                    
            otherlv_14=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXRelationType3407); 

            		newLeafNode(otherlv_14, grammarAccess.getXRelationTypeAccess().getSideBArtifactTypeXArtifactTypeCrossReference_14_0()); 
            	

            }


            }

            otherlv_15=(Token)match(input,60,FOLLOW_60_in_ruleXRelationType3419); 

                	newLeafNode(otherlv_15, grammarAccess.getXRelationTypeAccess().getDefaultOrderTypeKeyword_15());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1663:1: ( (lv_defaultOrderType_16_0= ruleRelationOrderType ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1664:1: (lv_defaultOrderType_16_0= ruleRelationOrderType )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1664:1: (lv_defaultOrderType_16_0= ruleRelationOrderType )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1665:3: lv_defaultOrderType_16_0= ruleRelationOrderType
            {
             
            	        newCompositeNode(grammarAccess.getXRelationTypeAccess().getDefaultOrderTypeRelationOrderTypeParserRuleCall_16_0()); 
            	    
            pushFollow(FOLLOW_ruleRelationOrderType_in_ruleXRelationType3440);
            lv_defaultOrderType_16_0=ruleRelationOrderType();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getXRelationTypeRule());
            	        }
                   		set(
                   			current, 
                   			"defaultOrderType",
                    		lv_defaultOrderType_16_0, 
                    		"RelationOrderType");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            otherlv_17=(Token)match(input,61,FOLLOW_61_in_ruleXRelationType3452); 

                	newLeafNode(otherlv_17, grammarAccess.getXRelationTypeAccess().getMultiplicityKeyword_17());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1685:1: ( (lv_multiplicity_18_0= ruleRelationMultiplicityEnum ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1686:1: (lv_multiplicity_18_0= ruleRelationMultiplicityEnum )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1686:1: (lv_multiplicity_18_0= ruleRelationMultiplicityEnum )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1687:3: lv_multiplicity_18_0= ruleRelationMultiplicityEnum
            {
             
            	        newCompositeNode(grammarAccess.getXRelationTypeAccess().getMultiplicityRelationMultiplicityEnumEnumRuleCall_18_0()); 
            	    
            pushFollow(FOLLOW_ruleRelationMultiplicityEnum_in_ruleXRelationType3473);
            lv_multiplicity_18_0=ruleRelationMultiplicityEnum();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getXRelationTypeRule());
            	        }
                   		set(
                   			current, 
                   			"multiplicity",
                    		lv_multiplicity_18_0, 
                    		"RelationMultiplicityEnum");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            otherlv_19=(Token)match(input,22,FOLLOW_22_in_ruleXRelationType3485); 

                	newLeafNode(otherlv_19, grammarAccess.getXRelationTypeAccess().getRightCurlyBracketKeyword_19());
                

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1715:1: entryRuleRelationOrderType returns [String current=null] : iv_ruleRelationOrderType= ruleRelationOrderType EOF ;
    public final String entryRuleRelationOrderType() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleRelationOrderType = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1716:2: (iv_ruleRelationOrderType= ruleRelationOrderType EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1717:2: iv_ruleRelationOrderType= ruleRelationOrderType EOF
            {
             newCompositeNode(grammarAccess.getRelationOrderTypeRule()); 
            pushFollow(FOLLOW_ruleRelationOrderType_in_entryRuleRelationOrderType3522);
            iv_ruleRelationOrderType=ruleRelationOrderType();

            state._fsp--;

             current =iv_ruleRelationOrderType.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleRelationOrderType3533); 

            }

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1724:1: ruleRelationOrderType returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'Lexicographical_Ascending' | kw= 'Lexicographical_Descending' | kw= 'Unordered' | this_ID_3= RULE_ID ) ;
    public final AntlrDatatypeRuleToken ruleRelationOrderType() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        Token this_ID_3=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1727:28: ( (kw= 'Lexicographical_Ascending' | kw= 'Lexicographical_Descending' | kw= 'Unordered' | this_ID_3= RULE_ID ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1728:1: (kw= 'Lexicographical_Ascending' | kw= 'Lexicographical_Descending' | kw= 'Unordered' | this_ID_3= RULE_ID )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1728:1: (kw= 'Lexicographical_Ascending' | kw= 'Lexicographical_Descending' | kw= 'Unordered' | this_ID_3= RULE_ID )
            int alt30=4;
            switch ( input.LA(1) ) {
            case 62:
                {
                alt30=1;
                }
                break;
            case 63:
                {
                alt30=2;
                }
                break;
            case 64:
                {
                alt30=3;
                }
                break;
            case RULE_ID:
                {
                alt30=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 30, 0, input);

                throw nvae;
            }

            switch (alt30) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1729:2: kw= 'Lexicographical_Ascending'
                    {
                    kw=(Token)match(input,62,FOLLOW_62_in_ruleRelationOrderType3571); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRelationOrderTypeAccess().getLexicographical_AscendingKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1736:2: kw= 'Lexicographical_Descending'
                    {
                    kw=(Token)match(input,63,FOLLOW_63_in_ruleRelationOrderType3590); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRelationOrderTypeAccess().getLexicographical_DescendingKeyword_1()); 
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1743:2: kw= 'Unordered'
                    {
                    kw=(Token)match(input,64,FOLLOW_64_in_ruleRelationOrderType3609); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRelationOrderTypeAccess().getUnorderedKeyword_2()); 
                        

                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1749:10: this_ID_3= RULE_ID
                    {
                    this_ID_3=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleRelationOrderType3630); 

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1766:1: entryRuleCondition returns [EObject current=null] : iv_ruleCondition= ruleCondition EOF ;
    public final EObject entryRuleCondition() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleCondition = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1767:2: (iv_ruleCondition= ruleCondition EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1768:2: iv_ruleCondition= ruleCondition EOF
            {
             newCompositeNode(grammarAccess.getConditionRule()); 
            pushFollow(FOLLOW_ruleCondition_in_entryRuleCondition3677);
            iv_ruleCondition=ruleCondition();

            state._fsp--;

             current =iv_ruleCondition; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleCondition3687); 

            }

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1775:1: ruleCondition returns [EObject current=null] : (this_SimpleCondition_0= ruleSimpleCondition | this_CompoundCondition_1= ruleCompoundCondition ) ;
    public final EObject ruleCondition() throws RecognitionException {
        EObject current = null;

        EObject this_SimpleCondition_0 = null;

        EObject this_CompoundCondition_1 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1778:28: ( (this_SimpleCondition_0= ruleSimpleCondition | this_CompoundCondition_1= ruleCompoundCondition ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1779:1: (this_SimpleCondition_0= ruleSimpleCondition | this_CompoundCondition_1= ruleCompoundCondition )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1779:1: (this_SimpleCondition_0= ruleSimpleCondition | this_CompoundCondition_1= ruleCompoundCondition )
            int alt31=2;
            int LA31_0 = input.LA(1);

            if ( (LA31_0==24||(LA31_0>=83 && LA31_0<=85)) ) {
                alt31=1;
            }
            else if ( (LA31_0==65) ) {
                alt31=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 31, 0, input);

                throw nvae;
            }
            switch (alt31) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1780:5: this_SimpleCondition_0= ruleSimpleCondition
                    {
                     
                            newCompositeNode(grammarAccess.getConditionAccess().getSimpleConditionParserRuleCall_0()); 
                        
                    pushFollow(FOLLOW_ruleSimpleCondition_in_ruleCondition3734);
                    this_SimpleCondition_0=ruleSimpleCondition();

                    state._fsp--;

                     
                            current = this_SimpleCondition_0; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1790:5: this_CompoundCondition_1= ruleCompoundCondition
                    {
                     
                            newCompositeNode(grammarAccess.getConditionAccess().getCompoundConditionParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_ruleCompoundCondition_in_ruleCondition3761);
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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1806:1: entryRuleSimpleCondition returns [EObject current=null] : iv_ruleSimpleCondition= ruleSimpleCondition EOF ;
    public final EObject entryRuleSimpleCondition() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleSimpleCondition = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1807:2: (iv_ruleSimpleCondition= ruleSimpleCondition EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1808:2: iv_ruleSimpleCondition= ruleSimpleCondition EOF
            {
             newCompositeNode(grammarAccess.getSimpleConditionRule()); 
            pushFollow(FOLLOW_ruleSimpleCondition_in_entryRuleSimpleCondition3796);
            iv_ruleSimpleCondition=ruleSimpleCondition();

            state._fsp--;

             current =iv_ruleSimpleCondition; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleSimpleCondition3806); 

            }

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1815:1: ruleSimpleCondition returns [EObject current=null] : ( ( (lv_field_0_0= ruleMatchField ) ) ( (lv_op_1_0= ruleCompareOp ) ) ( (lv_expression_2_0= RULE_STRING ) ) ) ;
    public final EObject ruleSimpleCondition() throws RecognitionException {
        EObject current = null;

        Token lv_expression_2_0=null;
        Enumerator lv_field_0_0 = null;

        Enumerator lv_op_1_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1818:28: ( ( ( (lv_field_0_0= ruleMatchField ) ) ( (lv_op_1_0= ruleCompareOp ) ) ( (lv_expression_2_0= RULE_STRING ) ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1819:1: ( ( (lv_field_0_0= ruleMatchField ) ) ( (lv_op_1_0= ruleCompareOp ) ) ( (lv_expression_2_0= RULE_STRING ) ) )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1819:1: ( ( (lv_field_0_0= ruleMatchField ) ) ( (lv_op_1_0= ruleCompareOp ) ) ( (lv_expression_2_0= RULE_STRING ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1819:2: ( (lv_field_0_0= ruleMatchField ) ) ( (lv_op_1_0= ruleCompareOp ) ) ( (lv_expression_2_0= RULE_STRING ) )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1819:2: ( (lv_field_0_0= ruleMatchField ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1820:1: (lv_field_0_0= ruleMatchField )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1820:1: (lv_field_0_0= ruleMatchField )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1821:3: lv_field_0_0= ruleMatchField
            {
             
            	        newCompositeNode(grammarAccess.getSimpleConditionAccess().getFieldMatchFieldEnumRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_ruleMatchField_in_ruleSimpleCondition3852);
            lv_field_0_0=ruleMatchField();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getSimpleConditionRule());
            	        }
                   		set(
                   			current, 
                   			"field",
                    		lv_field_0_0, 
                    		"MatchField");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1837:2: ( (lv_op_1_0= ruleCompareOp ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1838:1: (lv_op_1_0= ruleCompareOp )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1838:1: (lv_op_1_0= ruleCompareOp )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1839:3: lv_op_1_0= ruleCompareOp
            {
             
            	        newCompositeNode(grammarAccess.getSimpleConditionAccess().getOpCompareOpEnumRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_ruleCompareOp_in_ruleSimpleCondition3873);
            lv_op_1_0=ruleCompareOp();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getSimpleConditionRule());
            	        }
                   		set(
                   			current, 
                   			"op",
                    		lv_op_1_0, 
                    		"CompareOp");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1855:2: ( (lv_expression_2_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1856:1: (lv_expression_2_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1856:1: (lv_expression_2_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1857:3: lv_expression_2_0= RULE_STRING
            {
            lv_expression_2_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleSimpleCondition3890); 

            			newLeafNode(lv_expression_2_0, grammarAccess.getSimpleConditionAccess().getExpressionSTRINGTerminalRuleCall_2_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getSimpleConditionRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"expression",
                    		lv_expression_2_0, 
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
    // $ANTLR end "ruleSimpleCondition"


    // $ANTLR start "entryRuleCompoundCondition"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1881:1: entryRuleCompoundCondition returns [EObject current=null] : iv_ruleCompoundCondition= ruleCompoundCondition EOF ;
    public final EObject entryRuleCompoundCondition() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleCompoundCondition = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1882:2: (iv_ruleCompoundCondition= ruleCompoundCondition EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1883:2: iv_ruleCompoundCondition= ruleCompoundCondition EOF
            {
             newCompositeNode(grammarAccess.getCompoundConditionRule()); 
            pushFollow(FOLLOW_ruleCompoundCondition_in_entryRuleCompoundCondition3931);
            iv_ruleCompoundCondition=ruleCompoundCondition();

            state._fsp--;

             current =iv_ruleCompoundCondition; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleCompoundCondition3941); 

            }

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1890:1: ruleCompoundCondition returns [EObject current=null] : (otherlv_0= '(' ( (lv_conditions_1_0= ruleSimpleCondition ) ) ( ( (lv_operators_2_0= ruleXLogicOperator ) ) ( (lv_conditions_3_0= ruleSimpleCondition ) ) )+ otherlv_4= ')' ) ;
    public final EObject ruleCompoundCondition() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_4=null;
        EObject lv_conditions_1_0 = null;

        Enumerator lv_operators_2_0 = null;

        EObject lv_conditions_3_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1893:28: ( (otherlv_0= '(' ( (lv_conditions_1_0= ruleSimpleCondition ) ) ( ( (lv_operators_2_0= ruleXLogicOperator ) ) ( (lv_conditions_3_0= ruleSimpleCondition ) ) )+ otherlv_4= ')' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1894:1: (otherlv_0= '(' ( (lv_conditions_1_0= ruleSimpleCondition ) ) ( ( (lv_operators_2_0= ruleXLogicOperator ) ) ( (lv_conditions_3_0= ruleSimpleCondition ) ) )+ otherlv_4= ')' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1894:1: (otherlv_0= '(' ( (lv_conditions_1_0= ruleSimpleCondition ) ) ( ( (lv_operators_2_0= ruleXLogicOperator ) ) ( (lv_conditions_3_0= ruleSimpleCondition ) ) )+ otherlv_4= ')' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1894:3: otherlv_0= '(' ( (lv_conditions_1_0= ruleSimpleCondition ) ) ( ( (lv_operators_2_0= ruleXLogicOperator ) ) ( (lv_conditions_3_0= ruleSimpleCondition ) ) )+ otherlv_4= ')'
            {
            otherlv_0=(Token)match(input,65,FOLLOW_65_in_ruleCompoundCondition3978); 

                	newLeafNode(otherlv_0, grammarAccess.getCompoundConditionAccess().getLeftParenthesisKeyword_0());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1898:1: ( (lv_conditions_1_0= ruleSimpleCondition ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1899:1: (lv_conditions_1_0= ruleSimpleCondition )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1899:1: (lv_conditions_1_0= ruleSimpleCondition )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1900:3: lv_conditions_1_0= ruleSimpleCondition
            {
             
            	        newCompositeNode(grammarAccess.getCompoundConditionAccess().getConditionsSimpleConditionParserRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_ruleSimpleCondition_in_ruleCompoundCondition3999);
            lv_conditions_1_0=ruleSimpleCondition();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getCompoundConditionRule());
            	        }
                   		add(
                   			current, 
                   			"conditions",
                    		lv_conditions_1_0, 
                    		"SimpleCondition");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1916:2: ( ( (lv_operators_2_0= ruleXLogicOperator ) ) ( (lv_conditions_3_0= ruleSimpleCondition ) ) )+
            int cnt32=0;
            loop32:
            do {
                int alt32=2;
                int LA32_0 = input.LA(1);

                if ( ((LA32_0>=81 && LA32_0<=82)) ) {
                    alt32=1;
                }


                switch (alt32) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1916:3: ( (lv_operators_2_0= ruleXLogicOperator ) ) ( (lv_conditions_3_0= ruleSimpleCondition ) )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1916:3: ( (lv_operators_2_0= ruleXLogicOperator ) )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1917:1: (lv_operators_2_0= ruleXLogicOperator )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1917:1: (lv_operators_2_0= ruleXLogicOperator )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1918:3: lv_operators_2_0= ruleXLogicOperator
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getCompoundConditionAccess().getOperatorsXLogicOperatorEnumRuleCall_2_0_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleXLogicOperator_in_ruleCompoundCondition4021);
            	    lv_operators_2_0=ruleXLogicOperator();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getCompoundConditionRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"operators",
            	            		lv_operators_2_0, 
            	            		"XLogicOperator");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }

            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1934:2: ( (lv_conditions_3_0= ruleSimpleCondition ) )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1935:1: (lv_conditions_3_0= ruleSimpleCondition )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1935:1: (lv_conditions_3_0= ruleSimpleCondition )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1936:3: lv_conditions_3_0= ruleSimpleCondition
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getCompoundConditionAccess().getConditionsSimpleConditionParserRuleCall_2_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleSimpleCondition_in_ruleCompoundCondition4042);
            	    lv_conditions_3_0=ruleSimpleCondition();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getCompoundConditionRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"conditions",
            	            		lv_conditions_3_0, 
            	            		"SimpleCondition");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt32 >= 1 ) break loop32;
                        EarlyExitException eee =
                            new EarlyExitException(32, input);
                        throw eee;
                }
                cnt32++;
            } while (true);

            otherlv_4=(Token)match(input,66,FOLLOW_66_in_ruleCompoundCondition4056); 

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1964:1: entryRuleXArtifactMatcher returns [EObject current=null] : iv_ruleXArtifactMatcher= ruleXArtifactMatcher EOF ;
    public final EObject entryRuleXArtifactMatcher() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleXArtifactMatcher = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1965:2: (iv_ruleXArtifactMatcher= ruleXArtifactMatcher EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1966:2: iv_ruleXArtifactMatcher= ruleXArtifactMatcher EOF
            {
             newCompositeNode(grammarAccess.getXArtifactMatcherRule()); 
            pushFollow(FOLLOW_ruleXArtifactMatcher_in_entryRuleXArtifactMatcher4092);
            iv_ruleXArtifactMatcher=ruleXArtifactMatcher();

            state._fsp--;

             current =iv_ruleXArtifactMatcher; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleXArtifactMatcher4102); 

            }

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1973:1: ruleXArtifactMatcher returns [EObject current=null] : (otherlv_0= 'artifactMatcher' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= 'where' ( (lv_conditions_3_0= ruleCondition ) ) ( ( (lv_operators_4_0= ruleXLogicOperator ) ) ( (lv_conditions_5_0= ruleCondition ) ) )* otherlv_6= ';' ) ;
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
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1976:28: ( (otherlv_0= 'artifactMatcher' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= 'where' ( (lv_conditions_3_0= ruleCondition ) ) ( ( (lv_operators_4_0= ruleXLogicOperator ) ) ( (lv_conditions_5_0= ruleCondition ) ) )* otherlv_6= ';' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1977:1: (otherlv_0= 'artifactMatcher' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= 'where' ( (lv_conditions_3_0= ruleCondition ) ) ( ( (lv_operators_4_0= ruleXLogicOperator ) ) ( (lv_conditions_5_0= ruleCondition ) ) )* otherlv_6= ';' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1977:1: (otherlv_0= 'artifactMatcher' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= 'where' ( (lv_conditions_3_0= ruleCondition ) ) ( ( (lv_operators_4_0= ruleXLogicOperator ) ) ( (lv_conditions_5_0= ruleCondition ) ) )* otherlv_6= ';' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1977:3: otherlv_0= 'artifactMatcher' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= 'where' ( (lv_conditions_3_0= ruleCondition ) ) ( ( (lv_operators_4_0= ruleXLogicOperator ) ) ( (lv_conditions_5_0= ruleCondition ) ) )* otherlv_6= ';'
            {
            otherlv_0=(Token)match(input,67,FOLLOW_67_in_ruleXArtifactMatcher4139); 

                	newLeafNode(otherlv_0, grammarAccess.getXArtifactMatcherAccess().getArtifactMatcherKeyword_0());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1981:1: ( (lv_name_1_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1982:1: (lv_name_1_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1982:1: (lv_name_1_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1983:3: lv_name_1_0= RULE_STRING
            {
            lv_name_1_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXArtifactMatcher4156); 

            			newLeafNode(lv_name_1_0, grammarAccess.getXArtifactMatcherAccess().getNameSTRINGTerminalRuleCall_1_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getXArtifactMatcherRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"name",
                    		lv_name_1_0, 
                    		"STRING");
            	    

            }


            }

            otherlv_2=(Token)match(input,68,FOLLOW_68_in_ruleXArtifactMatcher4173); 

                	newLeafNode(otherlv_2, grammarAccess.getXArtifactMatcherAccess().getWhereKeyword_2());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2003:1: ( (lv_conditions_3_0= ruleCondition ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2004:1: (lv_conditions_3_0= ruleCondition )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2004:1: (lv_conditions_3_0= ruleCondition )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2005:3: lv_conditions_3_0= ruleCondition
            {
             
            	        newCompositeNode(grammarAccess.getXArtifactMatcherAccess().getConditionsConditionParserRuleCall_3_0()); 
            	    
            pushFollow(FOLLOW_ruleCondition_in_ruleXArtifactMatcher4194);
            lv_conditions_3_0=ruleCondition();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getXArtifactMatcherRule());
            	        }
                   		add(
                   			current, 
                   			"conditions",
                    		lv_conditions_3_0, 
                    		"Condition");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2021:2: ( ( (lv_operators_4_0= ruleXLogicOperator ) ) ( (lv_conditions_5_0= ruleCondition ) ) )*
            loop33:
            do {
                int alt33=2;
                int LA33_0 = input.LA(1);

                if ( ((LA33_0>=81 && LA33_0<=82)) ) {
                    alt33=1;
                }


                switch (alt33) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2021:3: ( (lv_operators_4_0= ruleXLogicOperator ) ) ( (lv_conditions_5_0= ruleCondition ) )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2021:3: ( (lv_operators_4_0= ruleXLogicOperator ) )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2022:1: (lv_operators_4_0= ruleXLogicOperator )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2022:1: (lv_operators_4_0= ruleXLogicOperator )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2023:3: lv_operators_4_0= ruleXLogicOperator
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getXArtifactMatcherAccess().getOperatorsXLogicOperatorEnumRuleCall_4_0_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleXLogicOperator_in_ruleXArtifactMatcher4216);
            	    lv_operators_4_0=ruleXLogicOperator();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getXArtifactMatcherRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"operators",
            	            		lv_operators_4_0, 
            	            		"XLogicOperator");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }

            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2039:2: ( (lv_conditions_5_0= ruleCondition ) )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2040:1: (lv_conditions_5_0= ruleCondition )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2040:1: (lv_conditions_5_0= ruleCondition )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2041:3: lv_conditions_5_0= ruleCondition
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getXArtifactMatcherAccess().getConditionsConditionParserRuleCall_4_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleCondition_in_ruleXArtifactMatcher4237);
            	    lv_conditions_5_0=ruleCondition();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getXArtifactMatcherRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"conditions",
            	            		lv_conditions_5_0, 
            	            		"Condition");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop33;
                }
            } while (true);

            otherlv_6=(Token)match(input,69,FOLLOW_69_in_ruleXArtifactMatcher4251); 

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


    // $ANTLR start "entryRuleAccessContext"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2069:1: entryRuleAccessContext returns [EObject current=null] : iv_ruleAccessContext= ruleAccessContext EOF ;
    public final EObject entryRuleAccessContext() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAccessContext = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2070:2: (iv_ruleAccessContext= ruleAccessContext EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2071:2: iv_ruleAccessContext= ruleAccessContext EOF
            {
             newCompositeNode(grammarAccess.getAccessContextRule()); 
            pushFollow(FOLLOW_ruleAccessContext_in_entryRuleAccessContext4287);
            iv_ruleAccessContext=ruleAccessContext();

            state._fsp--;

             current =iv_ruleAccessContext; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAccessContext4297); 

            }

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2078:1: ruleAccessContext returns [EObject current=null] : (otherlv_0= 'accessContext' ( (lv_name_1_0= RULE_STRING ) ) (otherlv_2= 'extends' ( (otherlv_3= RULE_STRING ) ) )? otherlv_4= '{' otherlv_5= 'guid' ( (lv_guid_6_0= RULE_STRING ) ) otherlv_7= ';' ( ( (lv_accessRules_8_0= ruleObjectRestriction ) ) | ( (lv_hierarchyRestrictions_9_0= ruleHierarchyRestriction ) ) )+ otherlv_10= '}' ) ;
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
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2081:28: ( (otherlv_0= 'accessContext' ( (lv_name_1_0= RULE_STRING ) ) (otherlv_2= 'extends' ( (otherlv_3= RULE_STRING ) ) )? otherlv_4= '{' otherlv_5= 'guid' ( (lv_guid_6_0= RULE_STRING ) ) otherlv_7= ';' ( ( (lv_accessRules_8_0= ruleObjectRestriction ) ) | ( (lv_hierarchyRestrictions_9_0= ruleHierarchyRestriction ) ) )+ otherlv_10= '}' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2082:1: (otherlv_0= 'accessContext' ( (lv_name_1_0= RULE_STRING ) ) (otherlv_2= 'extends' ( (otherlv_3= RULE_STRING ) ) )? otherlv_4= '{' otherlv_5= 'guid' ( (lv_guid_6_0= RULE_STRING ) ) otherlv_7= ';' ( ( (lv_accessRules_8_0= ruleObjectRestriction ) ) | ( (lv_hierarchyRestrictions_9_0= ruleHierarchyRestriction ) ) )+ otherlv_10= '}' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2082:1: (otherlv_0= 'accessContext' ( (lv_name_1_0= RULE_STRING ) ) (otherlv_2= 'extends' ( (otherlv_3= RULE_STRING ) ) )? otherlv_4= '{' otherlv_5= 'guid' ( (lv_guid_6_0= RULE_STRING ) ) otherlv_7= ';' ( ( (lv_accessRules_8_0= ruleObjectRestriction ) ) | ( (lv_hierarchyRestrictions_9_0= ruleHierarchyRestriction ) ) )+ otherlv_10= '}' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2082:3: otherlv_0= 'accessContext' ( (lv_name_1_0= RULE_STRING ) ) (otherlv_2= 'extends' ( (otherlv_3= RULE_STRING ) ) )? otherlv_4= '{' otherlv_5= 'guid' ( (lv_guid_6_0= RULE_STRING ) ) otherlv_7= ';' ( ( (lv_accessRules_8_0= ruleObjectRestriction ) ) | ( (lv_hierarchyRestrictions_9_0= ruleHierarchyRestriction ) ) )+ otherlv_10= '}'
            {
            otherlv_0=(Token)match(input,70,FOLLOW_70_in_ruleAccessContext4334); 

                	newLeafNode(otherlv_0, grammarAccess.getAccessContextAccess().getAccessContextKeyword_0());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2086:1: ( (lv_name_1_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2087:1: (lv_name_1_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2087:1: (lv_name_1_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2088:3: lv_name_1_0= RULE_STRING
            {
            lv_name_1_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAccessContext4351); 

            			newLeafNode(lv_name_1_0, grammarAccess.getAccessContextAccess().getNameSTRINGTerminalRuleCall_1_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getAccessContextRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"name",
                    		lv_name_1_0, 
                    		"STRING");
            	    

            }


            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2104:2: (otherlv_2= 'extends' ( (otherlv_3= RULE_STRING ) ) )?
            int alt34=2;
            int LA34_0 = input.LA(1);

            if ( (LA34_0==17) ) {
                alt34=1;
            }
            switch (alt34) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2104:4: otherlv_2= 'extends' ( (otherlv_3= RULE_STRING ) )
                    {
                    otherlv_2=(Token)match(input,17,FOLLOW_17_in_ruleAccessContext4369); 

                        	newLeafNode(otherlv_2, grammarAccess.getAccessContextAccess().getExtendsKeyword_2_0());
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2108:1: ( (otherlv_3= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2109:1: (otherlv_3= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2109:1: (otherlv_3= RULE_STRING )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2110:3: otherlv_3= RULE_STRING
                    {

                    			if (current==null) {
                    	            current = createModelElement(grammarAccess.getAccessContextRule());
                    	        }
                            
                    otherlv_3=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAccessContext4389); 

                    		newLeafNode(otherlv_3, grammarAccess.getAccessContextAccess().getSuperAccessContextsAccessContextCrossReference_2_1_0()); 
                    	

                    }


                    }


                    }
                    break;

            }

            otherlv_4=(Token)match(input,19,FOLLOW_19_in_ruleAccessContext4403); 

                	newLeafNode(otherlv_4, grammarAccess.getAccessContextAccess().getLeftCurlyBracketKeyword_3());
                
            otherlv_5=(Token)match(input,20,FOLLOW_20_in_ruleAccessContext4415); 

                	newLeafNode(otherlv_5, grammarAccess.getAccessContextAccess().getGuidKeyword_4());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2129:1: ( (lv_guid_6_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2130:1: (lv_guid_6_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2130:1: (lv_guid_6_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2131:3: lv_guid_6_0= RULE_STRING
            {
            lv_guid_6_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAccessContext4432); 

            			newLeafNode(lv_guid_6_0, grammarAccess.getAccessContextAccess().getGuidSTRINGTerminalRuleCall_5_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getAccessContextRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"guid",
                    		lv_guid_6_0, 
                    		"STRING");
            	    

            }


            }

            otherlv_7=(Token)match(input,69,FOLLOW_69_in_ruleAccessContext4449); 

                	newLeafNode(otherlv_7, grammarAccess.getAccessContextAccess().getSemicolonKeyword_6());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2151:1: ( ( (lv_accessRules_8_0= ruleObjectRestriction ) ) | ( (lv_hierarchyRestrictions_9_0= ruleHierarchyRestriction ) ) )+
            int cnt35=0;
            loop35:
            do {
                int alt35=3;
                int LA35_0 = input.LA(1);

                if ( ((LA35_0>=86 && LA35_0<=87)) ) {
                    alt35=1;
                }
                else if ( (LA35_0==71) ) {
                    alt35=2;
                }


                switch (alt35) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2151:2: ( (lv_accessRules_8_0= ruleObjectRestriction ) )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2151:2: ( (lv_accessRules_8_0= ruleObjectRestriction ) )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2152:1: (lv_accessRules_8_0= ruleObjectRestriction )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2152:1: (lv_accessRules_8_0= ruleObjectRestriction )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2153:3: lv_accessRules_8_0= ruleObjectRestriction
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getAccessContextAccess().getAccessRulesObjectRestrictionParserRuleCall_7_0_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleObjectRestriction_in_ruleAccessContext4471);
            	    lv_accessRules_8_0=ruleObjectRestriction();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getAccessContextRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"accessRules",
            	            		lv_accessRules_8_0, 
            	            		"ObjectRestriction");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;
            	case 2 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2170:6: ( (lv_hierarchyRestrictions_9_0= ruleHierarchyRestriction ) )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2170:6: ( (lv_hierarchyRestrictions_9_0= ruleHierarchyRestriction ) )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2171:1: (lv_hierarchyRestrictions_9_0= ruleHierarchyRestriction )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2171:1: (lv_hierarchyRestrictions_9_0= ruleHierarchyRestriction )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2172:3: lv_hierarchyRestrictions_9_0= ruleHierarchyRestriction
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getAccessContextAccess().getHierarchyRestrictionsHierarchyRestrictionParserRuleCall_7_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleHierarchyRestriction_in_ruleAccessContext4498);
            	    lv_hierarchyRestrictions_9_0=ruleHierarchyRestriction();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getAccessContextRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"hierarchyRestrictions",
            	            		lv_hierarchyRestrictions_9_0, 
            	            		"HierarchyRestriction");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt35 >= 1 ) break loop35;
                        EarlyExitException eee =
                            new EarlyExitException(35, input);
                        throw eee;
                }
                cnt35++;
            } while (true);

            otherlv_10=(Token)match(input,22,FOLLOW_22_in_ruleAccessContext4512); 

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2200:1: entryRuleHierarchyRestriction returns [EObject current=null] : iv_ruleHierarchyRestriction= ruleHierarchyRestriction EOF ;
    public final EObject entryRuleHierarchyRestriction() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleHierarchyRestriction = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2201:2: (iv_ruleHierarchyRestriction= ruleHierarchyRestriction EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2202:2: iv_ruleHierarchyRestriction= ruleHierarchyRestriction EOF
            {
             newCompositeNode(grammarAccess.getHierarchyRestrictionRule()); 
            pushFollow(FOLLOW_ruleHierarchyRestriction_in_entryRuleHierarchyRestriction4548);
            iv_ruleHierarchyRestriction=ruleHierarchyRestriction();

            state._fsp--;

             current =iv_ruleHierarchyRestriction; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleHierarchyRestriction4558); 

            }

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2209:1: ruleHierarchyRestriction returns [EObject current=null] : (otherlv_0= 'childrenOf' ( (otherlv_1= RULE_STRING ) ) otherlv_2= '{' ( (lv_accessRules_3_0= ruleObjectRestriction ) )+ otherlv_4= '}' ) ;
    public final EObject ruleHierarchyRestriction() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;
        Token otherlv_2=null;
        Token otherlv_4=null;
        EObject lv_accessRules_3_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2212:28: ( (otherlv_0= 'childrenOf' ( (otherlv_1= RULE_STRING ) ) otherlv_2= '{' ( (lv_accessRules_3_0= ruleObjectRestriction ) )+ otherlv_4= '}' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2213:1: (otherlv_0= 'childrenOf' ( (otherlv_1= RULE_STRING ) ) otherlv_2= '{' ( (lv_accessRules_3_0= ruleObjectRestriction ) )+ otherlv_4= '}' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2213:1: (otherlv_0= 'childrenOf' ( (otherlv_1= RULE_STRING ) ) otherlv_2= '{' ( (lv_accessRules_3_0= ruleObjectRestriction ) )+ otherlv_4= '}' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2213:3: otherlv_0= 'childrenOf' ( (otherlv_1= RULE_STRING ) ) otherlv_2= '{' ( (lv_accessRules_3_0= ruleObjectRestriction ) )+ otherlv_4= '}'
            {
            otherlv_0=(Token)match(input,71,FOLLOW_71_in_ruleHierarchyRestriction4595); 

                	newLeafNode(otherlv_0, grammarAccess.getHierarchyRestrictionAccess().getChildrenOfKeyword_0());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2217:1: ( (otherlv_1= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2218:1: (otherlv_1= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2218:1: (otherlv_1= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2219:3: otherlv_1= RULE_STRING
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getHierarchyRestrictionRule());
            	        }
                    
            otherlv_1=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleHierarchyRestriction4615); 

            		newLeafNode(otherlv_1, grammarAccess.getHierarchyRestrictionAccess().getArtifactMatcherRefXArtifactMatcherCrossReference_1_0()); 
            	

            }


            }

            otherlv_2=(Token)match(input,19,FOLLOW_19_in_ruleHierarchyRestriction4627); 

                	newLeafNode(otherlv_2, grammarAccess.getHierarchyRestrictionAccess().getLeftCurlyBracketKeyword_2());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2234:1: ( (lv_accessRules_3_0= ruleObjectRestriction ) )+
            int cnt36=0;
            loop36:
            do {
                int alt36=2;
                int LA36_0 = input.LA(1);

                if ( ((LA36_0>=86 && LA36_0<=87)) ) {
                    alt36=1;
                }


                switch (alt36) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2235:1: (lv_accessRules_3_0= ruleObjectRestriction )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2235:1: (lv_accessRules_3_0= ruleObjectRestriction )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2236:3: lv_accessRules_3_0= ruleObjectRestriction
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getHierarchyRestrictionAccess().getAccessRulesObjectRestrictionParserRuleCall_3_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleObjectRestriction_in_ruleHierarchyRestriction4648);
            	    lv_accessRules_3_0=ruleObjectRestriction();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getHierarchyRestrictionRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"accessRules",
            	            		lv_accessRules_3_0, 
            	            		"ObjectRestriction");
            	    	        afterParserOrEnumRuleCall();
            	    	    

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

            otherlv_4=(Token)match(input,22,FOLLOW_22_in_ruleHierarchyRestriction4661); 

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


    // $ANTLR start "entryRuleObjectRestriction"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2264:1: entryRuleObjectRestriction returns [EObject current=null] : iv_ruleObjectRestriction= ruleObjectRestriction EOF ;
    public final EObject entryRuleObjectRestriction() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleObjectRestriction = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2265:2: (iv_ruleObjectRestriction= ruleObjectRestriction EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2266:2: iv_ruleObjectRestriction= ruleObjectRestriction EOF
            {
             newCompositeNode(grammarAccess.getObjectRestrictionRule()); 
            pushFollow(FOLLOW_ruleObjectRestriction_in_entryRuleObjectRestriction4697);
            iv_ruleObjectRestriction=ruleObjectRestriction();

            state._fsp--;

             current =iv_ruleObjectRestriction; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleObjectRestriction4707); 

            }

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2273:1: ruleObjectRestriction returns [EObject current=null] : (this_ArtifactMatchRestriction_0= ruleArtifactMatchRestriction | this_ArtifactTypeRestriction_1= ruleArtifactTypeRestriction | this_RelationTypeRestriction_2= ruleRelationTypeRestriction | this_AttributeTypeRestriction_3= ruleAttributeTypeRestriction ) ;
    public final EObject ruleObjectRestriction() throws RecognitionException {
        EObject current = null;

        EObject this_ArtifactMatchRestriction_0 = null;

        EObject this_ArtifactTypeRestriction_1 = null;

        EObject this_RelationTypeRestriction_2 = null;

        EObject this_AttributeTypeRestriction_3 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2276:28: ( (this_ArtifactMatchRestriction_0= ruleArtifactMatchRestriction | this_ArtifactTypeRestriction_1= ruleArtifactTypeRestriction | this_RelationTypeRestriction_2= ruleRelationTypeRestriction | this_AttributeTypeRestriction_3= ruleAttributeTypeRestriction ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2277:1: (this_ArtifactMatchRestriction_0= ruleArtifactMatchRestriction | this_ArtifactTypeRestriction_1= ruleArtifactTypeRestriction | this_RelationTypeRestriction_2= ruleRelationTypeRestriction | this_AttributeTypeRestriction_3= ruleAttributeTypeRestriction )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2277:1: (this_ArtifactMatchRestriction_0= ruleArtifactMatchRestriction | this_ArtifactTypeRestriction_1= ruleArtifactTypeRestriction | this_RelationTypeRestriction_2= ruleRelationTypeRestriction | this_AttributeTypeRestriction_3= ruleAttributeTypeRestriction )
            int alt37=4;
            int LA37_0 = input.LA(1);

            if ( (LA37_0==86) ) {
                int LA37_1 = input.LA(2);

                if ( (LA37_1==72) ) {
                    switch ( input.LA(3) ) {
                    case 55:
                        {
                        alt37=3;
                        }
                        break;
                    case 25:
                        {
                        alt37=4;
                        }
                        break;
                    case 16:
                        {
                        alt37=2;
                        }
                        break;
                    case 73:
                        {
                        alt37=1;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("", 37, 3, input);

                        throw nvae;
                    }

                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 37, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA37_0==87) ) {
                int LA37_2 = input.LA(2);

                if ( (LA37_2==72) ) {
                    switch ( input.LA(3) ) {
                    case 55:
                        {
                        alt37=3;
                        }
                        break;
                    case 25:
                        {
                        alt37=4;
                        }
                        break;
                    case 16:
                        {
                        alt37=2;
                        }
                        break;
                    case 73:
                        {
                        alt37=1;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("", 37, 3, input);

                        throw nvae;
                    }

                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 37, 2, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 37, 0, input);

                throw nvae;
            }
            switch (alt37) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2278:5: this_ArtifactMatchRestriction_0= ruleArtifactMatchRestriction
                    {
                     
                            newCompositeNode(grammarAccess.getObjectRestrictionAccess().getArtifactMatchRestrictionParserRuleCall_0()); 
                        
                    pushFollow(FOLLOW_ruleArtifactMatchRestriction_in_ruleObjectRestriction4754);
                    this_ArtifactMatchRestriction_0=ruleArtifactMatchRestriction();

                    state._fsp--;

                     
                            current = this_ArtifactMatchRestriction_0; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2288:5: this_ArtifactTypeRestriction_1= ruleArtifactTypeRestriction
                    {
                     
                            newCompositeNode(grammarAccess.getObjectRestrictionAccess().getArtifactTypeRestrictionParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_ruleArtifactTypeRestriction_in_ruleObjectRestriction4781);
                    this_ArtifactTypeRestriction_1=ruleArtifactTypeRestriction();

                    state._fsp--;

                     
                            current = this_ArtifactTypeRestriction_1; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2298:5: this_RelationTypeRestriction_2= ruleRelationTypeRestriction
                    {
                     
                            newCompositeNode(grammarAccess.getObjectRestrictionAccess().getRelationTypeRestrictionParserRuleCall_2()); 
                        
                    pushFollow(FOLLOW_ruleRelationTypeRestriction_in_ruleObjectRestriction4808);
                    this_RelationTypeRestriction_2=ruleRelationTypeRestriction();

                    state._fsp--;

                     
                            current = this_RelationTypeRestriction_2; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2308:5: this_AttributeTypeRestriction_3= ruleAttributeTypeRestriction
                    {
                     
                            newCompositeNode(grammarAccess.getObjectRestrictionAccess().getAttributeTypeRestrictionParserRuleCall_3()); 
                        
                    pushFollow(FOLLOW_ruleAttributeTypeRestriction_in_ruleObjectRestriction4835);
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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2324:1: entryRuleArtifactMatchRestriction returns [EObject current=null] : iv_ruleArtifactMatchRestriction= ruleArtifactMatchRestriction EOF ;
    public final EObject entryRuleArtifactMatchRestriction() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleArtifactMatchRestriction = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2325:2: (iv_ruleArtifactMatchRestriction= ruleArtifactMatchRestriction EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2326:2: iv_ruleArtifactMatchRestriction= ruleArtifactMatchRestriction EOF
            {
             newCompositeNode(grammarAccess.getArtifactMatchRestrictionRule()); 
            pushFollow(FOLLOW_ruleArtifactMatchRestriction_in_entryRuleArtifactMatchRestriction4870);
            iv_ruleArtifactMatchRestriction=ruleArtifactMatchRestriction();

            state._fsp--;

             current =iv_ruleArtifactMatchRestriction; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleArtifactMatchRestriction4880); 

            }

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2333:1: ruleArtifactMatchRestriction returns [EObject current=null] : ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'artifact' ( (otherlv_3= RULE_STRING ) ) otherlv_4= ';' ) ;
    public final EObject ruleArtifactMatchRestriction() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_2=null;
        Token otherlv_3=null;
        Token otherlv_4=null;
        Enumerator lv_permission_0_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2336:28: ( ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'artifact' ( (otherlv_3= RULE_STRING ) ) otherlv_4= ';' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2337:1: ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'artifact' ( (otherlv_3= RULE_STRING ) ) otherlv_4= ';' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2337:1: ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'artifact' ( (otherlv_3= RULE_STRING ) ) otherlv_4= ';' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2337:2: ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'artifact' ( (otherlv_3= RULE_STRING ) ) otherlv_4= ';'
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2337:2: ( (lv_permission_0_0= ruleAccessPermissionEnum ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2338:1: (lv_permission_0_0= ruleAccessPermissionEnum )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2338:1: (lv_permission_0_0= ruleAccessPermissionEnum )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2339:3: lv_permission_0_0= ruleAccessPermissionEnum
            {
             
            	        newCompositeNode(grammarAccess.getArtifactMatchRestrictionAccess().getPermissionAccessPermissionEnumEnumRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_ruleAccessPermissionEnum_in_ruleArtifactMatchRestriction4926);
            lv_permission_0_0=ruleAccessPermissionEnum();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getArtifactMatchRestrictionRule());
            	        }
                   		set(
                   			current, 
                   			"permission",
                    		lv_permission_0_0, 
                    		"AccessPermissionEnum");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            otherlv_1=(Token)match(input,72,FOLLOW_72_in_ruleArtifactMatchRestriction4938); 

                	newLeafNode(otherlv_1, grammarAccess.getArtifactMatchRestrictionAccess().getEditKeyword_1());
                
            otherlv_2=(Token)match(input,73,FOLLOW_73_in_ruleArtifactMatchRestriction4950); 

                	newLeafNode(otherlv_2, grammarAccess.getArtifactMatchRestrictionAccess().getArtifactKeyword_2());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2363:1: ( (otherlv_3= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2364:1: (otherlv_3= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2364:1: (otherlv_3= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2365:3: otherlv_3= RULE_STRING
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getArtifactMatchRestrictionRule());
            	        }
                    
            otherlv_3=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleArtifactMatchRestriction4970); 

            		newLeafNode(otherlv_3, grammarAccess.getArtifactMatchRestrictionAccess().getArtifactMatcherRefXArtifactMatcherCrossReference_3_0()); 
            	

            }


            }

            otherlv_4=(Token)match(input,69,FOLLOW_69_in_ruleArtifactMatchRestriction4982); 

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2388:1: entryRuleArtifactTypeRestriction returns [EObject current=null] : iv_ruleArtifactTypeRestriction= ruleArtifactTypeRestriction EOF ;
    public final EObject entryRuleArtifactTypeRestriction() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleArtifactTypeRestriction = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2389:2: (iv_ruleArtifactTypeRestriction= ruleArtifactTypeRestriction EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2390:2: iv_ruleArtifactTypeRestriction= ruleArtifactTypeRestriction EOF
            {
             newCompositeNode(grammarAccess.getArtifactTypeRestrictionRule()); 
            pushFollow(FOLLOW_ruleArtifactTypeRestriction_in_entryRuleArtifactTypeRestriction5018);
            iv_ruleArtifactTypeRestriction=ruleArtifactTypeRestriction();

            state._fsp--;

             current =iv_ruleArtifactTypeRestriction; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleArtifactTypeRestriction5028); 

            }

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2397:1: ruleArtifactTypeRestriction returns [EObject current=null] : ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'artifactType' ( (otherlv_3= RULE_STRING ) ) otherlv_4= ';' ) ;
    public final EObject ruleArtifactTypeRestriction() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_2=null;
        Token otherlv_3=null;
        Token otherlv_4=null;
        Enumerator lv_permission_0_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2400:28: ( ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'artifactType' ( (otherlv_3= RULE_STRING ) ) otherlv_4= ';' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2401:1: ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'artifactType' ( (otherlv_3= RULE_STRING ) ) otherlv_4= ';' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2401:1: ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'artifactType' ( (otherlv_3= RULE_STRING ) ) otherlv_4= ';' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2401:2: ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'artifactType' ( (otherlv_3= RULE_STRING ) ) otherlv_4= ';'
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2401:2: ( (lv_permission_0_0= ruleAccessPermissionEnum ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2402:1: (lv_permission_0_0= ruleAccessPermissionEnum )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2402:1: (lv_permission_0_0= ruleAccessPermissionEnum )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2403:3: lv_permission_0_0= ruleAccessPermissionEnum
            {
             
            	        newCompositeNode(grammarAccess.getArtifactTypeRestrictionAccess().getPermissionAccessPermissionEnumEnumRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_ruleAccessPermissionEnum_in_ruleArtifactTypeRestriction5074);
            lv_permission_0_0=ruleAccessPermissionEnum();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getArtifactTypeRestrictionRule());
            	        }
                   		set(
                   			current, 
                   			"permission",
                    		lv_permission_0_0, 
                    		"AccessPermissionEnum");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            otherlv_1=(Token)match(input,72,FOLLOW_72_in_ruleArtifactTypeRestriction5086); 

                	newLeafNode(otherlv_1, grammarAccess.getArtifactTypeRestrictionAccess().getEditKeyword_1());
                
            otherlv_2=(Token)match(input,16,FOLLOW_16_in_ruleArtifactTypeRestriction5098); 

                	newLeafNode(otherlv_2, grammarAccess.getArtifactTypeRestrictionAccess().getArtifactTypeKeyword_2());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2427:1: ( (otherlv_3= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2428:1: (otherlv_3= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2428:1: (otherlv_3= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2429:3: otherlv_3= RULE_STRING
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getArtifactTypeRestrictionRule());
            	        }
                    
            otherlv_3=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleArtifactTypeRestriction5118); 

            		newLeafNode(otherlv_3, grammarAccess.getArtifactTypeRestrictionAccess().getArtifactTypeRefXArtifactTypeCrossReference_3_0()); 
            	

            }


            }

            otherlv_4=(Token)match(input,69,FOLLOW_69_in_ruleArtifactTypeRestriction5130); 

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2452:1: entryRuleAttributeTypeRestriction returns [EObject current=null] : iv_ruleAttributeTypeRestriction= ruleAttributeTypeRestriction EOF ;
    public final EObject entryRuleAttributeTypeRestriction() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttributeTypeRestriction = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2453:2: (iv_ruleAttributeTypeRestriction= ruleAttributeTypeRestriction EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2454:2: iv_ruleAttributeTypeRestriction= ruleAttributeTypeRestriction EOF
            {
             newCompositeNode(grammarAccess.getAttributeTypeRestrictionRule()); 
            pushFollow(FOLLOW_ruleAttributeTypeRestriction_in_entryRuleAttributeTypeRestriction5166);
            iv_ruleAttributeTypeRestriction=ruleAttributeTypeRestriction();

            state._fsp--;

             current =iv_ruleAttributeTypeRestriction; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttributeTypeRestriction5176); 

            }

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2461:1: ruleAttributeTypeRestriction returns [EObject current=null] : ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'attributeType' ( (otherlv_3= RULE_STRING ) ) (otherlv_4= 'of' otherlv_5= 'artifactType' ( (otherlv_6= RULE_STRING ) ) )? otherlv_7= ';' ) ;
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
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2464:28: ( ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'attributeType' ( (otherlv_3= RULE_STRING ) ) (otherlv_4= 'of' otherlv_5= 'artifactType' ( (otherlv_6= RULE_STRING ) ) )? otherlv_7= ';' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2465:1: ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'attributeType' ( (otherlv_3= RULE_STRING ) ) (otherlv_4= 'of' otherlv_5= 'artifactType' ( (otherlv_6= RULE_STRING ) ) )? otherlv_7= ';' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2465:1: ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'attributeType' ( (otherlv_3= RULE_STRING ) ) (otherlv_4= 'of' otherlv_5= 'artifactType' ( (otherlv_6= RULE_STRING ) ) )? otherlv_7= ';' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2465:2: ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'attributeType' ( (otherlv_3= RULE_STRING ) ) (otherlv_4= 'of' otherlv_5= 'artifactType' ( (otherlv_6= RULE_STRING ) ) )? otherlv_7= ';'
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2465:2: ( (lv_permission_0_0= ruleAccessPermissionEnum ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2466:1: (lv_permission_0_0= ruleAccessPermissionEnum )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2466:1: (lv_permission_0_0= ruleAccessPermissionEnum )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2467:3: lv_permission_0_0= ruleAccessPermissionEnum
            {
             
            	        newCompositeNode(grammarAccess.getAttributeTypeRestrictionAccess().getPermissionAccessPermissionEnumEnumRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_ruleAccessPermissionEnum_in_ruleAttributeTypeRestriction5222);
            lv_permission_0_0=ruleAccessPermissionEnum();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getAttributeTypeRestrictionRule());
            	        }
                   		set(
                   			current, 
                   			"permission",
                    		lv_permission_0_0, 
                    		"AccessPermissionEnum");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            otherlv_1=(Token)match(input,72,FOLLOW_72_in_ruleAttributeTypeRestriction5234); 

                	newLeafNode(otherlv_1, grammarAccess.getAttributeTypeRestrictionAccess().getEditKeyword_1());
                
            otherlv_2=(Token)match(input,25,FOLLOW_25_in_ruleAttributeTypeRestriction5246); 

                	newLeafNode(otherlv_2, grammarAccess.getAttributeTypeRestrictionAccess().getAttributeTypeKeyword_2());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2491:1: ( (otherlv_3= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2492:1: (otherlv_3= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2492:1: (otherlv_3= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2493:3: otherlv_3= RULE_STRING
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getAttributeTypeRestrictionRule());
            	        }
                    
            otherlv_3=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAttributeTypeRestriction5266); 

            		newLeafNode(otherlv_3, grammarAccess.getAttributeTypeRestrictionAccess().getAttributeTypeRefXAttributeTypeCrossReference_3_0()); 
            	

            }


            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2504:2: (otherlv_4= 'of' otherlv_5= 'artifactType' ( (otherlv_6= RULE_STRING ) ) )?
            int alt38=2;
            int LA38_0 = input.LA(1);

            if ( (LA38_0==74) ) {
                alt38=1;
            }
            switch (alt38) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2504:4: otherlv_4= 'of' otherlv_5= 'artifactType' ( (otherlv_6= RULE_STRING ) )
                    {
                    otherlv_4=(Token)match(input,74,FOLLOW_74_in_ruleAttributeTypeRestriction5279); 

                        	newLeafNode(otherlv_4, grammarAccess.getAttributeTypeRestrictionAccess().getOfKeyword_4_0());
                        
                    otherlv_5=(Token)match(input,16,FOLLOW_16_in_ruleAttributeTypeRestriction5291); 

                        	newLeafNode(otherlv_5, grammarAccess.getAttributeTypeRestrictionAccess().getArtifactTypeKeyword_4_1());
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2512:1: ( (otherlv_6= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2513:1: (otherlv_6= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2513:1: (otherlv_6= RULE_STRING )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2514:3: otherlv_6= RULE_STRING
                    {

                    			if (current==null) {
                    	            current = createModelElement(grammarAccess.getAttributeTypeRestrictionRule());
                    	        }
                            
                    otherlv_6=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAttributeTypeRestriction5311); 

                    		newLeafNode(otherlv_6, grammarAccess.getAttributeTypeRestrictionAccess().getArtifactTypeRefXArtifactTypeCrossReference_4_2_0()); 
                    	

                    }


                    }


                    }
                    break;

            }

            otherlv_7=(Token)match(input,69,FOLLOW_69_in_ruleAttributeTypeRestriction5325); 

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2537:1: entryRuleRelationTypeRestriction returns [EObject current=null] : iv_ruleRelationTypeRestriction= ruleRelationTypeRestriction EOF ;
    public final EObject entryRuleRelationTypeRestriction() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRelationTypeRestriction = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2538:2: (iv_ruleRelationTypeRestriction= ruleRelationTypeRestriction EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2539:2: iv_ruleRelationTypeRestriction= ruleRelationTypeRestriction EOF
            {
             newCompositeNode(grammarAccess.getRelationTypeRestrictionRule()); 
            pushFollow(FOLLOW_ruleRelationTypeRestriction_in_entryRuleRelationTypeRestriction5361);
            iv_ruleRelationTypeRestriction=ruleRelationTypeRestriction();

            state._fsp--;

             current =iv_ruleRelationTypeRestriction; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleRelationTypeRestriction5371); 

            }

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2546:1: ruleRelationTypeRestriction returns [EObject current=null] : ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'relationType' ( (otherlv_3= RULE_STRING ) ) ( (lv_restrictedToSide_4_0= ruleXRelationSideEnum ) ) (otherlv_5= 'artifact' ( (otherlv_6= RULE_STRING ) ) )? otherlv_7= ';' ) ;
    public final EObject ruleRelationTypeRestriction() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_2=null;
        Token otherlv_3=null;
        Token otherlv_5=null;
        Token otherlv_6=null;
        Token otherlv_7=null;
        Enumerator lv_permission_0_0 = null;

        Enumerator lv_restrictedToSide_4_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2549:28: ( ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'relationType' ( (otherlv_3= RULE_STRING ) ) ( (lv_restrictedToSide_4_0= ruleXRelationSideEnum ) ) (otherlv_5= 'artifact' ( (otherlv_6= RULE_STRING ) ) )? otherlv_7= ';' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2550:1: ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'relationType' ( (otherlv_3= RULE_STRING ) ) ( (lv_restrictedToSide_4_0= ruleXRelationSideEnum ) ) (otherlv_5= 'artifact' ( (otherlv_6= RULE_STRING ) ) )? otherlv_7= ';' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2550:1: ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'relationType' ( (otherlv_3= RULE_STRING ) ) ( (lv_restrictedToSide_4_0= ruleXRelationSideEnum ) ) (otherlv_5= 'artifact' ( (otherlv_6= RULE_STRING ) ) )? otherlv_7= ';' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2550:2: ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'relationType' ( (otherlv_3= RULE_STRING ) ) ( (lv_restrictedToSide_4_0= ruleXRelationSideEnum ) ) (otherlv_5= 'artifact' ( (otherlv_6= RULE_STRING ) ) )? otherlv_7= ';'
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2550:2: ( (lv_permission_0_0= ruleAccessPermissionEnum ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2551:1: (lv_permission_0_0= ruleAccessPermissionEnum )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2551:1: (lv_permission_0_0= ruleAccessPermissionEnum )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2552:3: lv_permission_0_0= ruleAccessPermissionEnum
            {
             
            	        newCompositeNode(grammarAccess.getRelationTypeRestrictionAccess().getPermissionAccessPermissionEnumEnumRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_ruleAccessPermissionEnum_in_ruleRelationTypeRestriction5417);
            lv_permission_0_0=ruleAccessPermissionEnum();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getRelationTypeRestrictionRule());
            	        }
                   		set(
                   			current, 
                   			"permission",
                    		lv_permission_0_0, 
                    		"AccessPermissionEnum");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            otherlv_1=(Token)match(input,72,FOLLOW_72_in_ruleRelationTypeRestriction5429); 

                	newLeafNode(otherlv_1, grammarAccess.getRelationTypeRestrictionAccess().getEditKeyword_1());
                
            otherlv_2=(Token)match(input,55,FOLLOW_55_in_ruleRelationTypeRestriction5441); 

                	newLeafNode(otherlv_2, grammarAccess.getRelationTypeRestrictionAccess().getRelationTypeKeyword_2());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2576:1: ( (otherlv_3= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2577:1: (otherlv_3= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2577:1: (otherlv_3= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2578:3: otherlv_3= RULE_STRING
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getRelationTypeRestrictionRule());
            	        }
                    
            otherlv_3=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleRelationTypeRestriction5461); 

            		newLeafNode(otherlv_3, grammarAccess.getRelationTypeRestrictionAccess().getRelationTypeRefXRelationTypeCrossReference_3_0()); 
            	

            }


            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2589:2: ( (lv_restrictedToSide_4_0= ruleXRelationSideEnum ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2590:1: (lv_restrictedToSide_4_0= ruleXRelationSideEnum )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2590:1: (lv_restrictedToSide_4_0= ruleXRelationSideEnum )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2591:3: lv_restrictedToSide_4_0= ruleXRelationSideEnum
            {
             
            	        newCompositeNode(grammarAccess.getRelationTypeRestrictionAccess().getRestrictedToSideXRelationSideEnumEnumRuleCall_4_0()); 
            	    
            pushFollow(FOLLOW_ruleXRelationSideEnum_in_ruleRelationTypeRestriction5482);
            lv_restrictedToSide_4_0=ruleXRelationSideEnum();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getRelationTypeRestrictionRule());
            	        }
                   		set(
                   			current, 
                   			"restrictedToSide",
                    		lv_restrictedToSide_4_0, 
                    		"XRelationSideEnum");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2607:2: (otherlv_5= 'artifact' ( (otherlv_6= RULE_STRING ) ) )?
            int alt39=2;
            int LA39_0 = input.LA(1);

            if ( (LA39_0==73) ) {
                alt39=1;
            }
            switch (alt39) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2607:4: otherlv_5= 'artifact' ( (otherlv_6= RULE_STRING ) )
                    {
                    otherlv_5=(Token)match(input,73,FOLLOW_73_in_ruleRelationTypeRestriction5495); 

                        	newLeafNode(otherlv_5, grammarAccess.getRelationTypeRestrictionAccess().getArtifactKeyword_5_0());
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2611:1: ( (otherlv_6= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2612:1: (otherlv_6= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2612:1: (otherlv_6= RULE_STRING )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2613:3: otherlv_6= RULE_STRING
                    {

                    			if (current==null) {
                    	            current = createModelElement(grammarAccess.getRelationTypeRestrictionRule());
                    	        }
                            
                    otherlv_6=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleRelationTypeRestriction5515); 

                    		newLeafNode(otherlv_6, grammarAccess.getRelationTypeRestrictionAccess().getArtifactMatcherRefXArtifactMatcherCrossReference_5_1_0()); 
                    	

                    }


                    }


                    }
                    break;

            }

            otherlv_7=(Token)match(input,69,FOLLOW_69_in_ruleRelationTypeRestriction5529); 

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2636:1: ruleRelationMultiplicityEnum returns [Enumerator current=null] : ( (enumLiteral_0= 'ONE_TO_ONE' ) | (enumLiteral_1= 'ONE_TO_MANY' ) | (enumLiteral_2= 'MANY_TO_ONE' ) | (enumLiteral_3= 'MANY_TO_MANY' ) ) ;
    public final Enumerator ruleRelationMultiplicityEnum() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;
        Token enumLiteral_2=null;
        Token enumLiteral_3=null;

         enterRule(); 
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2638:28: ( ( (enumLiteral_0= 'ONE_TO_ONE' ) | (enumLiteral_1= 'ONE_TO_MANY' ) | (enumLiteral_2= 'MANY_TO_ONE' ) | (enumLiteral_3= 'MANY_TO_MANY' ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2639:1: ( (enumLiteral_0= 'ONE_TO_ONE' ) | (enumLiteral_1= 'ONE_TO_MANY' ) | (enumLiteral_2= 'MANY_TO_ONE' ) | (enumLiteral_3= 'MANY_TO_MANY' ) )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2639:1: ( (enumLiteral_0= 'ONE_TO_ONE' ) | (enumLiteral_1= 'ONE_TO_MANY' ) | (enumLiteral_2= 'MANY_TO_ONE' ) | (enumLiteral_3= 'MANY_TO_MANY' ) )
            int alt40=4;
            switch ( input.LA(1) ) {
            case 75:
                {
                alt40=1;
                }
                break;
            case 76:
                {
                alt40=2;
                }
                break;
            case 77:
                {
                alt40=3;
                }
                break;
            case 78:
                {
                alt40=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 40, 0, input);

                throw nvae;
            }

            switch (alt40) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2639:2: (enumLiteral_0= 'ONE_TO_ONE' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2639:2: (enumLiteral_0= 'ONE_TO_ONE' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2639:4: enumLiteral_0= 'ONE_TO_ONE'
                    {
                    enumLiteral_0=(Token)match(input,75,FOLLOW_75_in_ruleRelationMultiplicityEnum5579); 

                            current = grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_ONEEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_0, grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_ONEEnumLiteralDeclaration_0()); 
                        

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2645:6: (enumLiteral_1= 'ONE_TO_MANY' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2645:6: (enumLiteral_1= 'ONE_TO_MANY' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2645:8: enumLiteral_1= 'ONE_TO_MANY'
                    {
                    enumLiteral_1=(Token)match(input,76,FOLLOW_76_in_ruleRelationMultiplicityEnum5596); 

                            current = grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_MANYEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_1, grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_MANYEnumLiteralDeclaration_1()); 
                        

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2651:6: (enumLiteral_2= 'MANY_TO_ONE' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2651:6: (enumLiteral_2= 'MANY_TO_ONE' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2651:8: enumLiteral_2= 'MANY_TO_ONE'
                    {
                    enumLiteral_2=(Token)match(input,77,FOLLOW_77_in_ruleRelationMultiplicityEnum5613); 

                            current = grammarAccess.getRelationMultiplicityEnumAccess().getMANY_TO_ONEEnumLiteralDeclaration_2().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_2, grammarAccess.getRelationMultiplicityEnumAccess().getMANY_TO_ONEEnumLiteralDeclaration_2()); 
                        

                    }


                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2657:6: (enumLiteral_3= 'MANY_TO_MANY' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2657:6: (enumLiteral_3= 'MANY_TO_MANY' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2657:8: enumLiteral_3= 'MANY_TO_MANY'
                    {
                    enumLiteral_3=(Token)match(input,78,FOLLOW_78_in_ruleRelationMultiplicityEnum5630); 

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2667:1: ruleCompareOp returns [Enumerator current=null] : ( (enumLiteral_0= 'EQ' ) | (enumLiteral_1= 'LIKE' ) ) ;
    public final Enumerator ruleCompareOp() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;

         enterRule(); 
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2669:28: ( ( (enumLiteral_0= 'EQ' ) | (enumLiteral_1= 'LIKE' ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2670:1: ( (enumLiteral_0= 'EQ' ) | (enumLiteral_1= 'LIKE' ) )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2670:1: ( (enumLiteral_0= 'EQ' ) | (enumLiteral_1= 'LIKE' ) )
            int alt41=2;
            int LA41_0 = input.LA(1);

            if ( (LA41_0==79) ) {
                alt41=1;
            }
            else if ( (LA41_0==80) ) {
                alt41=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 41, 0, input);

                throw nvae;
            }
            switch (alt41) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2670:2: (enumLiteral_0= 'EQ' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2670:2: (enumLiteral_0= 'EQ' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2670:4: enumLiteral_0= 'EQ'
                    {
                    enumLiteral_0=(Token)match(input,79,FOLLOW_79_in_ruleCompareOp5675); 

                            current = grammarAccess.getCompareOpAccess().getEQEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_0, grammarAccess.getCompareOpAccess().getEQEnumLiteralDeclaration_0()); 
                        

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2676:6: (enumLiteral_1= 'LIKE' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2676:6: (enumLiteral_1= 'LIKE' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2676:8: enumLiteral_1= 'LIKE'
                    {
                    enumLiteral_1=(Token)match(input,80,FOLLOW_80_in_ruleCompareOp5692); 

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2686:1: ruleXLogicOperator returns [Enumerator current=null] : ( (enumLiteral_0= 'AND' ) | (enumLiteral_1= 'OR' ) ) ;
    public final Enumerator ruleXLogicOperator() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;

         enterRule(); 
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2688:28: ( ( (enumLiteral_0= 'AND' ) | (enumLiteral_1= 'OR' ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2689:1: ( (enumLiteral_0= 'AND' ) | (enumLiteral_1= 'OR' ) )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2689:1: ( (enumLiteral_0= 'AND' ) | (enumLiteral_1= 'OR' ) )
            int alt42=2;
            int LA42_0 = input.LA(1);

            if ( (LA42_0==81) ) {
                alt42=1;
            }
            else if ( (LA42_0==82) ) {
                alt42=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 42, 0, input);

                throw nvae;
            }
            switch (alt42) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2689:2: (enumLiteral_0= 'AND' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2689:2: (enumLiteral_0= 'AND' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2689:4: enumLiteral_0= 'AND'
                    {
                    enumLiteral_0=(Token)match(input,81,FOLLOW_81_in_ruleXLogicOperator5737); 

                            current = grammarAccess.getXLogicOperatorAccess().getANDEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_0, grammarAccess.getXLogicOperatorAccess().getANDEnumLiteralDeclaration_0()); 
                        

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2695:6: (enumLiteral_1= 'OR' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2695:6: (enumLiteral_1= 'OR' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2695:8: enumLiteral_1= 'OR'
                    {
                    enumLiteral_1=(Token)match(input,82,FOLLOW_82_in_ruleXLogicOperator5754); 

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2705:1: ruleMatchField returns [Enumerator current=null] : ( (enumLiteral_0= 'artifactName' ) | (enumLiteral_1= 'artifactGuid' ) | (enumLiteral_2= 'branchName' ) | (enumLiteral_3= 'branchGuid' ) ) ;
    public final Enumerator ruleMatchField() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;
        Token enumLiteral_2=null;
        Token enumLiteral_3=null;

         enterRule(); 
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2707:28: ( ( (enumLiteral_0= 'artifactName' ) | (enumLiteral_1= 'artifactGuid' ) | (enumLiteral_2= 'branchName' ) | (enumLiteral_3= 'branchGuid' ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2708:1: ( (enumLiteral_0= 'artifactName' ) | (enumLiteral_1= 'artifactGuid' ) | (enumLiteral_2= 'branchName' ) | (enumLiteral_3= 'branchGuid' ) )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2708:1: ( (enumLiteral_0= 'artifactName' ) | (enumLiteral_1= 'artifactGuid' ) | (enumLiteral_2= 'branchName' ) | (enumLiteral_3= 'branchGuid' ) )
            int alt43=4;
            switch ( input.LA(1) ) {
            case 83:
                {
                alt43=1;
                }
                break;
            case 84:
                {
                alt43=2;
                }
                break;
            case 85:
                {
                alt43=3;
                }
                break;
            case 24:
                {
                alt43=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 43, 0, input);

                throw nvae;
            }

            switch (alt43) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2708:2: (enumLiteral_0= 'artifactName' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2708:2: (enumLiteral_0= 'artifactName' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2708:4: enumLiteral_0= 'artifactName'
                    {
                    enumLiteral_0=(Token)match(input,83,FOLLOW_83_in_ruleMatchField5799); 

                            current = grammarAccess.getMatchFieldAccess().getArtifactNameEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_0, grammarAccess.getMatchFieldAccess().getArtifactNameEnumLiteralDeclaration_0()); 
                        

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2714:6: (enumLiteral_1= 'artifactGuid' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2714:6: (enumLiteral_1= 'artifactGuid' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2714:8: enumLiteral_1= 'artifactGuid'
                    {
                    enumLiteral_1=(Token)match(input,84,FOLLOW_84_in_ruleMatchField5816); 

                            current = grammarAccess.getMatchFieldAccess().getArtifactGuidEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_1, grammarAccess.getMatchFieldAccess().getArtifactGuidEnumLiteralDeclaration_1()); 
                        

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2720:6: (enumLiteral_2= 'branchName' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2720:6: (enumLiteral_2= 'branchName' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2720:8: enumLiteral_2= 'branchName'
                    {
                    enumLiteral_2=(Token)match(input,85,FOLLOW_85_in_ruleMatchField5833); 

                            current = grammarAccess.getMatchFieldAccess().getBranchNameEnumLiteralDeclaration_2().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_2, grammarAccess.getMatchFieldAccess().getBranchNameEnumLiteralDeclaration_2()); 
                        

                    }


                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2726:6: (enumLiteral_3= 'branchGuid' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2726:6: (enumLiteral_3= 'branchGuid' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2726:8: enumLiteral_3= 'branchGuid'
                    {
                    enumLiteral_3=(Token)match(input,24,FOLLOW_24_in_ruleMatchField5850); 

                            current = grammarAccess.getMatchFieldAccess().getBranchGuidEnumLiteralDeclaration_3().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_3, grammarAccess.getMatchFieldAccess().getBranchGuidEnumLiteralDeclaration_3()); 
                        

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2736:1: ruleAccessPermissionEnum returns [Enumerator current=null] : ( (enumLiteral_0= 'ALLOW' ) | (enumLiteral_1= 'DENY' ) ) ;
    public final Enumerator ruleAccessPermissionEnum() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;

         enterRule(); 
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2738:28: ( ( (enumLiteral_0= 'ALLOW' ) | (enumLiteral_1= 'DENY' ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2739:1: ( (enumLiteral_0= 'ALLOW' ) | (enumLiteral_1= 'DENY' ) )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2739:1: ( (enumLiteral_0= 'ALLOW' ) | (enumLiteral_1= 'DENY' ) )
            int alt44=2;
            int LA44_0 = input.LA(1);

            if ( (LA44_0==86) ) {
                alt44=1;
            }
            else if ( (LA44_0==87) ) {
                alt44=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 44, 0, input);

                throw nvae;
            }
            switch (alt44) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2739:2: (enumLiteral_0= 'ALLOW' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2739:2: (enumLiteral_0= 'ALLOW' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2739:4: enumLiteral_0= 'ALLOW'
                    {
                    enumLiteral_0=(Token)match(input,86,FOLLOW_86_in_ruleAccessPermissionEnum5895); 

                            current = grammarAccess.getAccessPermissionEnumAccess().getALLOWEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_0, grammarAccess.getAccessPermissionEnumAccess().getALLOWEnumLiteralDeclaration_0()); 
                        

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2745:6: (enumLiteral_1= 'DENY' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2745:6: (enumLiteral_1= 'DENY' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2745:8: enumLiteral_1= 'DENY'
                    {
                    enumLiteral_1=(Token)match(input,87,FOLLOW_87_in_ruleAccessPermissionEnum5912); 

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


    // $ANTLR start "ruleXRelationSideEnum"
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2755:1: ruleXRelationSideEnum returns [Enumerator current=null] : ( (enumLiteral_0= 'SIDE_A' ) | (enumLiteral_1= 'SIDE_B' ) | (enumLiteral_2= 'BOTH' ) ) ;
    public final Enumerator ruleXRelationSideEnum() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;
        Token enumLiteral_2=null;

         enterRule(); 
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2757:28: ( ( (enumLiteral_0= 'SIDE_A' ) | (enumLiteral_1= 'SIDE_B' ) | (enumLiteral_2= 'BOTH' ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2758:1: ( (enumLiteral_0= 'SIDE_A' ) | (enumLiteral_1= 'SIDE_B' ) | (enumLiteral_2= 'BOTH' ) )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2758:1: ( (enumLiteral_0= 'SIDE_A' ) | (enumLiteral_1= 'SIDE_B' ) | (enumLiteral_2= 'BOTH' ) )
            int alt45=3;
            switch ( input.LA(1) ) {
            case 88:
                {
                alt45=1;
                }
                break;
            case 89:
                {
                alt45=2;
                }
                break;
            case 90:
                {
                alt45=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 45, 0, input);

                throw nvae;
            }

            switch (alt45) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2758:2: (enumLiteral_0= 'SIDE_A' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2758:2: (enumLiteral_0= 'SIDE_A' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2758:4: enumLiteral_0= 'SIDE_A'
                    {
                    enumLiteral_0=(Token)match(input,88,FOLLOW_88_in_ruleXRelationSideEnum5957); 

                            current = grammarAccess.getXRelationSideEnumAccess().getSIDE_AEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_0, grammarAccess.getXRelationSideEnumAccess().getSIDE_AEnumLiteralDeclaration_0()); 
                        

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2764:6: (enumLiteral_1= 'SIDE_B' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2764:6: (enumLiteral_1= 'SIDE_B' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2764:8: enumLiteral_1= 'SIDE_B'
                    {
                    enumLiteral_1=(Token)match(input,89,FOLLOW_89_in_ruleXRelationSideEnum5974); 

                            current = grammarAccess.getXRelationSideEnumAccess().getSIDE_BEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_1, grammarAccess.getXRelationSideEnumAccess().getSIDE_BEnumLiteralDeclaration_1()); 
                        

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2770:6: (enumLiteral_2= 'BOTH' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2770:6: (enumLiteral_2= 'BOTH' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2770:8: enumLiteral_2= 'BOTH'
                    {
                    enumLiteral_2=(Token)match(input,90,FOLLOW_90_in_ruleXRelationSideEnum5991); 

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


 

    public static final BitSet FOLLOW_ruleOseeDsl_in_entryRuleOseeDsl75 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleOseeDsl85 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleImport_in_ruleOseeDsl131 = new BitSet(new long[]{0x008900000201A002L,0x0000000000000048L});
    public static final BitSet FOLLOW_ruleXArtifactType_in_ruleOseeDsl154 = new BitSet(new long[]{0x0089000002018002L,0x0000000000000048L});
    public static final BitSet FOLLOW_ruleXRelationType_in_ruleOseeDsl181 = new BitSet(new long[]{0x0089000002018002L,0x0000000000000048L});
    public static final BitSet FOLLOW_ruleXAttributeType_in_ruleOseeDsl208 = new BitSet(new long[]{0x0089000002018002L,0x0000000000000048L});
    public static final BitSet FOLLOW_ruleXOseeEnumType_in_ruleOseeDsl235 = new BitSet(new long[]{0x0089000002018002L,0x0000000000000048L});
    public static final BitSet FOLLOW_ruleXOseeEnumOverride_in_ruleOseeDsl262 = new BitSet(new long[]{0x0089000002018002L,0x0000000000000048L});
    public static final BitSet FOLLOW_ruleXArtifactMatcher_in_ruleOseeDsl285 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000048L});
    public static final BitSet FOLLOW_ruleAccessContext_in_ruleOseeDsl307 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000040L});
    public static final BitSet FOLLOW_ruleImport_in_entryRuleImport344 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleImport354 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_13_in_ruleImport391 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleImport408 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_entryRuleQUALIFIED_NAME450 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleQUALIFIED_NAME461 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleQUALIFIED_NAME501 = new BitSet(new long[]{0x0000000000004002L});
    public static final BitSet FOLLOW_14_in_ruleQUALIFIED_NAME520 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleQUALIFIED_NAME535 = new BitSet(new long[]{0x0000000000004002L});
    public static final BitSet FOLLOW_ruleOseeType_in_entryRuleOseeType584 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleOseeType594 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXArtifactType_in_ruleOseeType641 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXRelationType_in_ruleOseeType668 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXAttributeType_in_ruleOseeType695 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXOseeEnumType_in_ruleOseeType722 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXArtifactType_in_entryRuleXArtifactType757 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleXArtifactType767 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_15_in_ruleXArtifactType810 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_16_in_ruleXArtifactType836 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXArtifactType853 = new BitSet(new long[]{0x00000000000A0000L});
    public static final BitSet FOLLOW_17_in_ruleXArtifactType871 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXArtifactType891 = new BitSet(new long[]{0x00000000000C0000L});
    public static final BitSet FOLLOW_18_in_ruleXArtifactType904 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXArtifactType924 = new BitSet(new long[]{0x00000000000C0000L});
    public static final BitSet FOLLOW_19_in_ruleXArtifactType940 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_ruleXArtifactType952 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXArtifactType969 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_21_in_ruleXArtifactType986 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RULE_HEX_INT_in_ruleXArtifactType1003 = new BitSet(new long[]{0x0000000000C00000L});
    public static final BitSet FOLLOW_ruleXAttributeTypeRef_in_ruleXArtifactType1029 = new BitSet(new long[]{0x0000000000C00000L});
    public static final BitSet FOLLOW_22_in_ruleXArtifactType1042 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXAttributeTypeRef_in_entryRuleXAttributeTypeRef1078 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleXAttributeTypeRef1088 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_23_in_ruleXAttributeTypeRef1125 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXAttributeTypeRef1145 = new BitSet(new long[]{0x0000000001000002L});
    public static final BitSet FOLLOW_24_in_ruleXAttributeTypeRef1158 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXAttributeTypeRef1175 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXAttributeType_in_entryRuleXAttributeType1218 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleXAttributeType1228 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_ruleXAttributeType1265 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXAttributeType1282 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_17_in_ruleXAttributeType1300 = new BitSet(new long[]{0x0000FF8000000020L});
    public static final BitSet FOLLOW_ruleAttributeBaseType_in_ruleXAttributeType1321 = new BitSet(new long[]{0x0000000004080000L});
    public static final BitSet FOLLOW_26_in_ruleXAttributeType1335 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXAttributeType1355 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_ruleXAttributeType1369 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_ruleXAttributeType1381 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXAttributeType1398 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_21_in_ruleXAttributeType1415 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RULE_HEX_INT_in_ruleXAttributeType1432 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_27_in_ruleXAttributeType1449 = new BitSet(new long[]{0x0000FF8030000020L});
    public static final BitSet FOLLOW_28_in_ruleXAttributeType1469 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_29_in_ruleXAttributeType1498 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_ruleXAttributeType1530 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_ruleXAttributeType1545 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_RULE_WHOLE_NUM_STR_in_ruleXAttributeType1562 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_31_in_ruleXAttributeType1579 = new BitSet(new long[]{0x0000000100000080L});
    public static final BitSet FOLLOW_RULE_WHOLE_NUM_STR_in_ruleXAttributeType1598 = new BitSet(new long[]{0x0000007A00400000L});
    public static final BitSet FOLLOW_32_in_ruleXAttributeType1619 = new BitSet(new long[]{0x0000007A00400000L});
    public static final BitSet FOLLOW_33_in_ruleXAttributeType1648 = new BitSet(new long[]{0x0000FF8400000020L});
    public static final BitSet FOLLOW_34_in_ruleXAttributeType1668 = new BitSet(new long[]{0x0000007800400000L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_ruleXAttributeType1700 = new BitSet(new long[]{0x0000007800400000L});
    public static final BitSet FOLLOW_35_in_ruleXAttributeType1718 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXAttributeType1738 = new BitSet(new long[]{0x0000007000400000L});
    public static final BitSet FOLLOW_36_in_ruleXAttributeType1753 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXAttributeType1770 = new BitSet(new long[]{0x0000006000400000L});
    public static final BitSet FOLLOW_37_in_ruleXAttributeType1790 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXAttributeType1807 = new BitSet(new long[]{0x0000004000400000L});
    public static final BitSet FOLLOW_38_in_ruleXAttributeType1827 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXAttributeType1844 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_22_in_ruleXAttributeType1863 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeBaseType_in_entryRuleAttributeBaseType1900 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttributeBaseType1911 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_39_in_ruleAttributeBaseType1949 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_ruleAttributeBaseType1968 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_ruleAttributeBaseType1987 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_ruleAttributeBaseType2006 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_43_in_ruleAttributeBaseType2025 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_ruleAttributeBaseType2044 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_45_in_ruleAttributeBaseType2063 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_46_in_ruleAttributeBaseType2082 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_ruleAttributeBaseType2101 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_ruleAttributeBaseType2129 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXOseeEnumType_in_entryRuleXOseeEnumType2174 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleXOseeEnumType2184 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_ruleXOseeEnumType2221 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXOseeEnumType2238 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_ruleXOseeEnumType2255 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_ruleXOseeEnumType2267 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXOseeEnumType2284 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_21_in_ruleXOseeEnumType2301 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RULE_HEX_INT_in_ruleXOseeEnumType2318 = new BitSet(new long[]{0x0002000000400000L});
    public static final BitSet FOLLOW_ruleXOseeEnumEntry_in_ruleXOseeEnumType2344 = new BitSet(new long[]{0x0002000000400000L});
    public static final BitSet FOLLOW_22_in_ruleXOseeEnumType2357 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXOseeEnumEntry_in_entryRuleXOseeEnumEntry2393 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleXOseeEnumEntry2403 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_49_in_ruleXOseeEnumEntry2440 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXOseeEnumEntry2457 = new BitSet(new long[]{0x0004000000000082L});
    public static final BitSet FOLLOW_RULE_WHOLE_NUM_STR_in_ruleXOseeEnumEntry2479 = new BitSet(new long[]{0x0004000000000002L});
    public static final BitSet FOLLOW_50_in_ruleXOseeEnumEntry2498 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXOseeEnumEntry2515 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXOseeEnumOverride_in_entryRuleXOseeEnumOverride2558 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleXOseeEnumOverride2568 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_51_in_ruleXOseeEnumOverride2605 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXOseeEnumOverride2625 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_ruleXOseeEnumOverride2637 = new BitSet(new long[]{0x0070000000400000L});
    public static final BitSet FOLLOW_52_in_ruleXOseeEnumOverride2655 = new BitSet(new long[]{0x0060000000400000L});
    public static final BitSet FOLLOW_ruleOverrideOption_in_ruleXOseeEnumOverride2690 = new BitSet(new long[]{0x0060000000400000L});
    public static final BitSet FOLLOW_22_in_ruleXOseeEnumOverride2703 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOverrideOption_in_entryRuleOverrideOption2739 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleOverrideOption2749 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAddEnum_in_ruleOverrideOption2796 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRemoveEnum_in_ruleOverrideOption2823 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAddEnum_in_entryRuleAddEnum2858 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAddEnum2868 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_53_in_ruleAddEnum2905 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAddEnum2922 = new BitSet(new long[]{0x0004000000000082L});
    public static final BitSet FOLLOW_RULE_WHOLE_NUM_STR_in_ruleAddEnum2944 = new BitSet(new long[]{0x0004000000000002L});
    public static final BitSet FOLLOW_50_in_ruleAddEnum2963 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAddEnum2980 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRemoveEnum_in_entryRuleRemoveEnum3023 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRemoveEnum3033 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_54_in_ruleRemoveEnum3070 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleRemoveEnum3090 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXRelationType_in_entryRuleXRelationType3126 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleXRelationType3136 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_55_in_ruleXRelationType3173 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXRelationType3190 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_ruleXRelationType3207 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_ruleXRelationType3219 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXRelationType3236 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_21_in_ruleXRelationType3253 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RULE_HEX_INT_in_ruleXRelationType3270 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_56_in_ruleXRelationType3287 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXRelationType3304 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_57_in_ruleXRelationType3321 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXRelationType3341 = new BitSet(new long[]{0x0400000000000000L});
    public static final BitSet FOLLOW_58_in_ruleXRelationType3353 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXRelationType3370 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_59_in_ruleXRelationType3387 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXRelationType3407 = new BitSet(new long[]{0x1000000000000000L});
    public static final BitSet FOLLOW_60_in_ruleXRelationType3419 = new BitSet(new long[]{0xC000000000000020L,0x0000000000000001L});
    public static final BitSet FOLLOW_ruleRelationOrderType_in_ruleXRelationType3440 = new BitSet(new long[]{0x2000000000000000L});
    public static final BitSet FOLLOW_61_in_ruleXRelationType3452 = new BitSet(new long[]{0x0000000000000000L,0x0000000000007800L});
    public static final BitSet FOLLOW_ruleRelationMultiplicityEnum_in_ruleXRelationType3473 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_22_in_ruleXRelationType3485 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelationOrderType_in_entryRuleRelationOrderType3522 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRelationOrderType3533 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_62_in_ruleRelationOrderType3571 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_63_in_ruleRelationOrderType3590 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_64_in_ruleRelationOrderType3609 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleRelationOrderType3630 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleCondition_in_entryRuleCondition3677 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleCondition3687 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleSimpleCondition_in_ruleCondition3734 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleCompoundCondition_in_ruleCondition3761 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleSimpleCondition_in_entryRuleSimpleCondition3796 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleSimpleCondition3806 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleMatchField_in_ruleSimpleCondition3852 = new BitSet(new long[]{0x0000000000000000L,0x0000000000018000L});
    public static final BitSet FOLLOW_ruleCompareOp_in_ruleSimpleCondition3873 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleSimpleCondition3890 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleCompoundCondition_in_entryRuleCompoundCondition3931 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleCompoundCondition3941 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_ruleCompoundCondition3978 = new BitSet(new long[]{0x0000000001000000L,0x0000000000380000L});
    public static final BitSet FOLLOW_ruleSimpleCondition_in_ruleCompoundCondition3999 = new BitSet(new long[]{0x0000000000000000L,0x0000000000060000L});
    public static final BitSet FOLLOW_ruleXLogicOperator_in_ruleCompoundCondition4021 = new BitSet(new long[]{0x0000000001000000L,0x0000000000380000L});
    public static final BitSet FOLLOW_ruleSimpleCondition_in_ruleCompoundCondition4042 = new BitSet(new long[]{0x0000000000000000L,0x0000000000060004L});
    public static final BitSet FOLLOW_66_in_ruleCompoundCondition4056 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXArtifactMatcher_in_entryRuleXArtifactMatcher4092 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleXArtifactMatcher4102 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_67_in_ruleXArtifactMatcher4139 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXArtifactMatcher4156 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_68_in_ruleXArtifactMatcher4173 = new BitSet(new long[]{0x0000000001000000L,0x0000000000380002L});
    public static final BitSet FOLLOW_ruleCondition_in_ruleXArtifactMatcher4194 = new BitSet(new long[]{0x0000000000000000L,0x0000000000060020L});
    public static final BitSet FOLLOW_ruleXLogicOperator_in_ruleXArtifactMatcher4216 = new BitSet(new long[]{0x0000000001000000L,0x0000000000380002L});
    public static final BitSet FOLLOW_ruleCondition_in_ruleXArtifactMatcher4237 = new BitSet(new long[]{0x0000000000000000L,0x0000000000060020L});
    public static final BitSet FOLLOW_69_in_ruleXArtifactMatcher4251 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAccessContext_in_entryRuleAccessContext4287 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAccessContext4297 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_70_in_ruleAccessContext4334 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAccessContext4351 = new BitSet(new long[]{0x00000000000A0000L});
    public static final BitSet FOLLOW_17_in_ruleAccessContext4369 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAccessContext4389 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_ruleAccessContext4403 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_ruleAccessContext4415 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAccessContext4432 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_69_in_ruleAccessContext4449 = new BitSet(new long[]{0x0000000000000000L,0x0000000000C00080L});
    public static final BitSet FOLLOW_ruleObjectRestriction_in_ruleAccessContext4471 = new BitSet(new long[]{0x0000000000400000L,0x0000000000C00080L});
    public static final BitSet FOLLOW_ruleHierarchyRestriction_in_ruleAccessContext4498 = new BitSet(new long[]{0x0000000000400000L,0x0000000000C00080L});
    public static final BitSet FOLLOW_22_in_ruleAccessContext4512 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleHierarchyRestriction_in_entryRuleHierarchyRestriction4548 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleHierarchyRestriction4558 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_71_in_ruleHierarchyRestriction4595 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleHierarchyRestriction4615 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_ruleHierarchyRestriction4627 = new BitSet(new long[]{0x0000000000000000L,0x0000000000C00000L});
    public static final BitSet FOLLOW_ruleObjectRestriction_in_ruleHierarchyRestriction4648 = new BitSet(new long[]{0x0000000000400000L,0x0000000000C00000L});
    public static final BitSet FOLLOW_22_in_ruleHierarchyRestriction4661 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleObjectRestriction_in_entryRuleObjectRestriction4697 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleObjectRestriction4707 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleArtifactMatchRestriction_in_ruleObjectRestriction4754 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleArtifactTypeRestriction_in_ruleObjectRestriction4781 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelationTypeRestriction_in_ruleObjectRestriction4808 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeTypeRestriction_in_ruleObjectRestriction4835 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleArtifactMatchRestriction_in_entryRuleArtifactMatchRestriction4870 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleArtifactMatchRestriction4880 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAccessPermissionEnum_in_ruleArtifactMatchRestriction4926 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_72_in_ruleArtifactMatchRestriction4938 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_73_in_ruleArtifactMatchRestriction4950 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleArtifactMatchRestriction4970 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_69_in_ruleArtifactMatchRestriction4982 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleArtifactTypeRestriction_in_entryRuleArtifactTypeRestriction5018 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleArtifactTypeRestriction5028 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAccessPermissionEnum_in_ruleArtifactTypeRestriction5074 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_72_in_ruleArtifactTypeRestriction5086 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_16_in_ruleArtifactTypeRestriction5098 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleArtifactTypeRestriction5118 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_69_in_ruleArtifactTypeRestriction5130 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeTypeRestriction_in_entryRuleAttributeTypeRestriction5166 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttributeTypeRestriction5176 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAccessPermissionEnum_in_ruleAttributeTypeRestriction5222 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_72_in_ruleAttributeTypeRestriction5234 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_ruleAttributeTypeRestriction5246 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAttributeTypeRestriction5266 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000420L});
    public static final BitSet FOLLOW_74_in_ruleAttributeTypeRestriction5279 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_16_in_ruleAttributeTypeRestriction5291 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAttributeTypeRestriction5311 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_69_in_ruleAttributeTypeRestriction5325 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelationTypeRestriction_in_entryRuleRelationTypeRestriction5361 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRelationTypeRestriction5371 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAccessPermissionEnum_in_ruleRelationTypeRestriction5417 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_72_in_ruleRelationTypeRestriction5429 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_55_in_ruleRelationTypeRestriction5441 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleRelationTypeRestriction5461 = new BitSet(new long[]{0x0000000000000000L,0x0000000007000000L});
    public static final BitSet FOLLOW_ruleXRelationSideEnum_in_ruleRelationTypeRestriction5482 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000220L});
    public static final BitSet FOLLOW_73_in_ruleRelationTypeRestriction5495 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleRelationTypeRestriction5515 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_69_in_ruleRelationTypeRestriction5529 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_75_in_ruleRelationMultiplicityEnum5579 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_76_in_ruleRelationMultiplicityEnum5596 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_77_in_ruleRelationMultiplicityEnum5613 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_78_in_ruleRelationMultiplicityEnum5630 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_79_in_ruleCompareOp5675 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_80_in_ruleCompareOp5692 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_81_in_ruleXLogicOperator5737 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_82_in_ruleXLogicOperator5754 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_83_in_ruleMatchField5799 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_84_in_ruleMatchField5816 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_85_in_ruleMatchField5833 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_24_in_ruleMatchField5850 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_86_in_ruleAccessPermissionEnum5895 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_87_in_ruleAccessPermissionEnum5912 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_88_in_ruleXRelationSideEnum5957 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_89_in_ruleXRelationSideEnum5974 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_90_in_ruleXRelationSideEnum5991 = new BitSet(new long[]{0x0000000000000002L});

}