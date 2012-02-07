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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE_STRING", "RULE_ID", "RULE_HEX_INT", "RULE_WHOLE_NUM_STR", "RULE_INT", "RULE_ML_COMMENT", "RULE_SL_COMMENT", "RULE_WS", "RULE_ANY_OTHER", "'import'", "'.'", "'abstract'", "'artifactType'", "'extends'", "','", "'{'", "'guid'", "'uuid'", "'}'", "'attribute'", "'branchGuid'", "'attributeType'", "'overrides'", "'dataProvider'", "'DefaultAttributeDataProvider'", "'UriAttributeDataProvider'", "'min'", "'max'", "'unlimited'", "'taggerId'", "'DefaultAttributeTaggerProvider'", "'enumType'", "'description'", "'defaultValue'", "'fileExtension'", "'BooleanAttribute'", "'CompressedContentAttribute'", "'DateAttribute'", "'EnumeratedAttribute'", "'FloatingPointAttribute'", "'IntegerAttribute'", "'JavaObjectAttribute'", "'StringAttribute'", "'WordAttribute'", "'OutlineNumberAttribute'", "'oseeEnumType'", "'entry'", "'entryGuid'", "'overrides enum'", "'inheritAll'", "'add'", "'remove'", "'relationType'", "'sideAName'", "'sideAArtifactType'", "'sideBName'", "'sideBArtifactType'", "'defaultOrderType'", "'multiplicity'", "'Lexicographical_Ascending'", "'Lexicographical_Descending'", "'Unordered'", "'('", "')'", "'artifactMatcher'", "'where'", "';'", "'accessContext'", "'childrenOf'", "'artifact'", "'edit'", "'of'", "'ONE_TO_ONE'", "'ONE_TO_MANY'", "'MANY_TO_ONE'", "'MANY_TO_MANY'", "'EQ'", "'LIKE'", "'AND'", "'OR'", "'artifactName'", "'artifactGuid'", "'branchName'", "'ALLOW'", "'DENY'", "'ALL'", "'SIDE_A'", "'SIDE_B'", "'BOTH'"
    };
    public static final int RULE_ID=5;
    public static final int T__29=29;
    public static final int T__28=28;
    public static final int T__27=27;
    public static final int T__26=26;
    public static final int T__25=25;
    public static final int T__24=24;
    public static final int T__23=23;
    public static final int T__22=22;
    public static final int RULE_ANY_OTHER=12;
    public static final int T__21=21;
    public static final int T__20=20;
    public static final int EOF=-1;
    public static final int T__19=19;
    public static final int T__91=91;
    public static final int T__92=92;
    public static final int T__16=16;
    public static final int T__15=15;
    public static final int T__90=90;
    public static final int T__18=18;
    public static final int T__17=17;
    public static final int T__14=14;
    public static final int T__13=13;
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
    public static final int RULE_ML_COMMENT=9;
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
    public static final int T__59=59;
    public static final int RULE_INT=8;
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
    public static final int RULE_WHOLE_NUM_STR=7;
    public static final int T__49=49;
    public static final int RULE_HEX_INT=6;
    public static final int RULE_SL_COMMENT=10;
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
    public static final int RULE_WS=11;

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
                case 56:
                    {
                    alt2=2;
                    }
                    break;
                case 25:
                    {
                    alt2=3;
                    }
                    break;
                case 49:
                    {
                    alt2=4;
                    }
                    break;
                case 52:
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

                if ( (LA3_0==68) ) {
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

                if ( (LA4_0==71) ) {
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
            case 56:
                {
                alt6=2;
                }
                break;
            case 25:
                {
                alt6=3;
                }
                break;
            case 49:
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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:392:1: ruleXArtifactType returns [EObject current=null] : ( ( (lv_abstract_0_0= 'abstract' ) )? otherlv_1= 'artifactType' ( (lv_name_2_0= RULE_STRING ) ) (otherlv_3= 'extends' ( (otherlv_4= RULE_STRING ) ) (otherlv_5= ',' ( (otherlv_6= RULE_STRING ) ) )* )? otherlv_7= '{' (otherlv_8= 'guid' ( (lv_typeGuid_9_0= RULE_STRING ) ) )? otherlv_10= 'uuid' ( (lv_uuid_11_0= RULE_HEX_INT ) ) ( (lv_validAttributeTypes_12_0= ruleXAttributeTypeRef ) )* otherlv_13= '}' ) ;
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
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:395:28: ( ( ( (lv_abstract_0_0= 'abstract' ) )? otherlv_1= 'artifactType' ( (lv_name_2_0= RULE_STRING ) ) (otherlv_3= 'extends' ( (otherlv_4= RULE_STRING ) ) (otherlv_5= ',' ( (otherlv_6= RULE_STRING ) ) )* )? otherlv_7= '{' (otherlv_8= 'guid' ( (lv_typeGuid_9_0= RULE_STRING ) ) )? otherlv_10= 'uuid' ( (lv_uuid_11_0= RULE_HEX_INT ) ) ( (lv_validAttributeTypes_12_0= ruleXAttributeTypeRef ) )* otherlv_13= '}' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:396:1: ( ( (lv_abstract_0_0= 'abstract' ) )? otherlv_1= 'artifactType' ( (lv_name_2_0= RULE_STRING ) ) (otherlv_3= 'extends' ( (otherlv_4= RULE_STRING ) ) (otherlv_5= ',' ( (otherlv_6= RULE_STRING ) ) )* )? otherlv_7= '{' (otherlv_8= 'guid' ( (lv_typeGuid_9_0= RULE_STRING ) ) )? otherlv_10= 'uuid' ( (lv_uuid_11_0= RULE_HEX_INT ) ) ( (lv_validAttributeTypes_12_0= ruleXAttributeTypeRef ) )* otherlv_13= '}' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:396:1: ( ( (lv_abstract_0_0= 'abstract' ) )? otherlv_1= 'artifactType' ( (lv_name_2_0= RULE_STRING ) ) (otherlv_3= 'extends' ( (otherlv_4= RULE_STRING ) ) (otherlv_5= ',' ( (otherlv_6= RULE_STRING ) ) )* )? otherlv_7= '{' (otherlv_8= 'guid' ( (lv_typeGuid_9_0= RULE_STRING ) ) )? otherlv_10= 'uuid' ( (lv_uuid_11_0= RULE_HEX_INT ) ) ( (lv_validAttributeTypes_12_0= ruleXAttributeTypeRef ) )* otherlv_13= '}' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:396:2: ( (lv_abstract_0_0= 'abstract' ) )? otherlv_1= 'artifactType' ( (lv_name_2_0= RULE_STRING ) ) (otherlv_3= 'extends' ( (otherlv_4= RULE_STRING ) ) (otherlv_5= ',' ( (otherlv_6= RULE_STRING ) ) )* )? otherlv_7= '{' (otherlv_8= 'guid' ( (lv_typeGuid_9_0= RULE_STRING ) ) )? otherlv_10= 'uuid' ( (lv_uuid_11_0= RULE_HEX_INT ) ) ( (lv_validAttributeTypes_12_0= ruleXAttributeTypeRef ) )* otherlv_13= '}'
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
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:471:1: (otherlv_8= 'guid' ( (lv_typeGuid_9_0= RULE_STRING ) ) )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==20) ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:471:3: otherlv_8= 'guid' ( (lv_typeGuid_9_0= RULE_STRING ) )
                    {
                    otherlv_8=(Token)match(input,20,FOLLOW_20_in_ruleXArtifactType953); 

                        	newLeafNode(otherlv_8, grammarAccess.getXArtifactTypeAccess().getGuidKeyword_5_0());
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:475:1: ( (lv_typeGuid_9_0= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:476:1: (lv_typeGuid_9_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:476:1: (lv_typeGuid_9_0= RULE_STRING )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:477:3: lv_typeGuid_9_0= RULE_STRING
                    {
                    lv_typeGuid_9_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXArtifactType970); 

                    			newLeafNode(lv_typeGuid_9_0, grammarAccess.getXArtifactTypeAccess().getTypeGuidSTRINGTerminalRuleCall_5_1_0()); 
                    		

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


                    }
                    break;

            }

            otherlv_10=(Token)match(input,21,FOLLOW_21_in_ruleXArtifactType989); 

                	newLeafNode(otherlv_10, grammarAccess.getXArtifactTypeAccess().getUuidKeyword_6());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:497:1: ( (lv_uuid_11_0= RULE_HEX_INT ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:498:1: (lv_uuid_11_0= RULE_HEX_INT )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:498:1: (lv_uuid_11_0= RULE_HEX_INT )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:499:3: lv_uuid_11_0= RULE_HEX_INT
            {
            lv_uuid_11_0=(Token)match(input,RULE_HEX_INT,FOLLOW_RULE_HEX_INT_in_ruleXArtifactType1006); 

            			newLeafNode(lv_uuid_11_0, grammarAccess.getXArtifactTypeAccess().getUuidHEX_INTTerminalRuleCall_7_0()); 
            		

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
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);

                if ( (LA11_0==23) ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:516:1: (lv_validAttributeTypes_12_0= ruleXAttributeTypeRef )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:516:1: (lv_validAttributeTypes_12_0= ruleXAttributeTypeRef )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:517:3: lv_validAttributeTypes_12_0= ruleXAttributeTypeRef
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getXArtifactTypeAccess().getValidAttributeTypesXAttributeTypeRefParserRuleCall_8_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleXAttributeTypeRef_in_ruleXArtifactType1032);
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
            	    break loop11;
                }
            } while (true);

            otherlv_13=(Token)match(input,22,FOLLOW_22_in_ruleXArtifactType1045); 

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:545:1: entryRuleXAttributeTypeRef returns [EObject current=null] : iv_ruleXAttributeTypeRef= ruleXAttributeTypeRef EOF ;
    public final EObject entryRuleXAttributeTypeRef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleXAttributeTypeRef = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:546:2: (iv_ruleXAttributeTypeRef= ruleXAttributeTypeRef EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:547:2: iv_ruleXAttributeTypeRef= ruleXAttributeTypeRef EOF
            {
             newCompositeNode(grammarAccess.getXAttributeTypeRefRule()); 
            pushFollow(FOLLOW_ruleXAttributeTypeRef_in_entryRuleXAttributeTypeRef1081);
            iv_ruleXAttributeTypeRef=ruleXAttributeTypeRef();

            state._fsp--;

             current =iv_ruleXAttributeTypeRef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleXAttributeTypeRef1091); 

            }

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
            otherlv_0=(Token)match(input,23,FOLLOW_23_in_ruleXAttributeTypeRef1128); 

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
                    
            otherlv_1=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXAttributeTypeRef1148); 

            		newLeafNode(otherlv_1, grammarAccess.getXAttributeTypeRefAccess().getValidAttributeTypeXAttributeTypeCrossReference_1_0()); 
            	

            }


            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:575:2: (otherlv_2= 'branchGuid' ( (lv_branchGuid_3_0= RULE_STRING ) ) )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==24) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:575:4: otherlv_2= 'branchGuid' ( (lv_branchGuid_3_0= RULE_STRING ) )
                    {
                    otherlv_2=(Token)match(input,24,FOLLOW_24_in_ruleXAttributeTypeRef1161); 

                        	newLeafNode(otherlv_2, grammarAccess.getXAttributeTypeRefAccess().getBranchGuidKeyword_2_0());
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:579:1: ( (lv_branchGuid_3_0= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:580:1: (lv_branchGuid_3_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:580:1: (lv_branchGuid_3_0= RULE_STRING )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:581:3: lv_branchGuid_3_0= RULE_STRING
                    {
                    lv_branchGuid_3_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXAttributeTypeRef1178); 

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
            pushFollow(FOLLOW_ruleXAttributeType_in_entryRuleXAttributeType1221);
            iv_ruleXAttributeType=ruleXAttributeType();

            state._fsp--;

             current =iv_ruleXAttributeType; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleXAttributeType1231); 

            }

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:614:1: ruleXAttributeType returns [EObject current=null] : (otherlv_0= 'attributeType' ( (lv_name_1_0= RULE_STRING ) ) (otherlv_2= 'extends' ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) ) ) (otherlv_4= 'overrides' ( (otherlv_5= RULE_STRING ) ) )? otherlv_6= '{' (otherlv_7= 'guid' ( (lv_typeGuid_8_0= RULE_STRING ) ) )? otherlv_9= 'uuid' ( (lv_uuid_10_0= RULE_HEX_INT ) ) otherlv_11= 'dataProvider' ( ( (lv_dataProvider_12_1= 'DefaultAttributeDataProvider' | lv_dataProvider_12_2= 'UriAttributeDataProvider' | lv_dataProvider_12_3= ruleQUALIFIED_NAME ) ) ) otherlv_13= 'min' ( (lv_min_14_0= RULE_WHOLE_NUM_STR ) ) otherlv_15= 'max' ( ( (lv_max_16_1= RULE_WHOLE_NUM_STR | lv_max_16_2= 'unlimited' ) ) ) (otherlv_17= 'taggerId' ( ( (lv_taggerId_18_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_18_2= ruleQUALIFIED_NAME ) ) ) )? (otherlv_19= 'enumType' ( (otherlv_20= RULE_STRING ) ) )? (otherlv_21= 'description' ( (lv_description_22_0= RULE_STRING ) ) )? (otherlv_23= 'defaultValue' ( (lv_defaultValue_24_0= RULE_STRING ) ) )? (otherlv_25= 'fileExtension' ( (lv_fileExtension_26_0= RULE_STRING ) ) )? otherlv_27= '}' ) ;
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
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:617:28: ( (otherlv_0= 'attributeType' ( (lv_name_1_0= RULE_STRING ) ) (otherlv_2= 'extends' ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) ) ) (otherlv_4= 'overrides' ( (otherlv_5= RULE_STRING ) ) )? otherlv_6= '{' (otherlv_7= 'guid' ( (lv_typeGuid_8_0= RULE_STRING ) ) )? otherlv_9= 'uuid' ( (lv_uuid_10_0= RULE_HEX_INT ) ) otherlv_11= 'dataProvider' ( ( (lv_dataProvider_12_1= 'DefaultAttributeDataProvider' | lv_dataProvider_12_2= 'UriAttributeDataProvider' | lv_dataProvider_12_3= ruleQUALIFIED_NAME ) ) ) otherlv_13= 'min' ( (lv_min_14_0= RULE_WHOLE_NUM_STR ) ) otherlv_15= 'max' ( ( (lv_max_16_1= RULE_WHOLE_NUM_STR | lv_max_16_2= 'unlimited' ) ) ) (otherlv_17= 'taggerId' ( ( (lv_taggerId_18_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_18_2= ruleQUALIFIED_NAME ) ) ) )? (otherlv_19= 'enumType' ( (otherlv_20= RULE_STRING ) ) )? (otherlv_21= 'description' ( (lv_description_22_0= RULE_STRING ) ) )? (otherlv_23= 'defaultValue' ( (lv_defaultValue_24_0= RULE_STRING ) ) )? (otherlv_25= 'fileExtension' ( (lv_fileExtension_26_0= RULE_STRING ) ) )? otherlv_27= '}' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:618:1: (otherlv_0= 'attributeType' ( (lv_name_1_0= RULE_STRING ) ) (otherlv_2= 'extends' ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) ) ) (otherlv_4= 'overrides' ( (otherlv_5= RULE_STRING ) ) )? otherlv_6= '{' (otherlv_7= 'guid' ( (lv_typeGuid_8_0= RULE_STRING ) ) )? otherlv_9= 'uuid' ( (lv_uuid_10_0= RULE_HEX_INT ) ) otherlv_11= 'dataProvider' ( ( (lv_dataProvider_12_1= 'DefaultAttributeDataProvider' | lv_dataProvider_12_2= 'UriAttributeDataProvider' | lv_dataProvider_12_3= ruleQUALIFIED_NAME ) ) ) otherlv_13= 'min' ( (lv_min_14_0= RULE_WHOLE_NUM_STR ) ) otherlv_15= 'max' ( ( (lv_max_16_1= RULE_WHOLE_NUM_STR | lv_max_16_2= 'unlimited' ) ) ) (otherlv_17= 'taggerId' ( ( (lv_taggerId_18_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_18_2= ruleQUALIFIED_NAME ) ) ) )? (otherlv_19= 'enumType' ( (otherlv_20= RULE_STRING ) ) )? (otherlv_21= 'description' ( (lv_description_22_0= RULE_STRING ) ) )? (otherlv_23= 'defaultValue' ( (lv_defaultValue_24_0= RULE_STRING ) ) )? (otherlv_25= 'fileExtension' ( (lv_fileExtension_26_0= RULE_STRING ) ) )? otherlv_27= '}' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:618:1: (otherlv_0= 'attributeType' ( (lv_name_1_0= RULE_STRING ) ) (otherlv_2= 'extends' ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) ) ) (otherlv_4= 'overrides' ( (otherlv_5= RULE_STRING ) ) )? otherlv_6= '{' (otherlv_7= 'guid' ( (lv_typeGuid_8_0= RULE_STRING ) ) )? otherlv_9= 'uuid' ( (lv_uuid_10_0= RULE_HEX_INT ) ) otherlv_11= 'dataProvider' ( ( (lv_dataProvider_12_1= 'DefaultAttributeDataProvider' | lv_dataProvider_12_2= 'UriAttributeDataProvider' | lv_dataProvider_12_3= ruleQUALIFIED_NAME ) ) ) otherlv_13= 'min' ( (lv_min_14_0= RULE_WHOLE_NUM_STR ) ) otherlv_15= 'max' ( ( (lv_max_16_1= RULE_WHOLE_NUM_STR | lv_max_16_2= 'unlimited' ) ) ) (otherlv_17= 'taggerId' ( ( (lv_taggerId_18_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_18_2= ruleQUALIFIED_NAME ) ) ) )? (otherlv_19= 'enumType' ( (otherlv_20= RULE_STRING ) ) )? (otherlv_21= 'description' ( (lv_description_22_0= RULE_STRING ) ) )? (otherlv_23= 'defaultValue' ( (lv_defaultValue_24_0= RULE_STRING ) ) )? (otherlv_25= 'fileExtension' ( (lv_fileExtension_26_0= RULE_STRING ) ) )? otherlv_27= '}' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:618:3: otherlv_0= 'attributeType' ( (lv_name_1_0= RULE_STRING ) ) (otherlv_2= 'extends' ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) ) ) (otherlv_4= 'overrides' ( (otherlv_5= RULE_STRING ) ) )? otherlv_6= '{' (otherlv_7= 'guid' ( (lv_typeGuid_8_0= RULE_STRING ) ) )? otherlv_9= 'uuid' ( (lv_uuid_10_0= RULE_HEX_INT ) ) otherlv_11= 'dataProvider' ( ( (lv_dataProvider_12_1= 'DefaultAttributeDataProvider' | lv_dataProvider_12_2= 'UriAttributeDataProvider' | lv_dataProvider_12_3= ruleQUALIFIED_NAME ) ) ) otherlv_13= 'min' ( (lv_min_14_0= RULE_WHOLE_NUM_STR ) ) otherlv_15= 'max' ( ( (lv_max_16_1= RULE_WHOLE_NUM_STR | lv_max_16_2= 'unlimited' ) ) ) (otherlv_17= 'taggerId' ( ( (lv_taggerId_18_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_18_2= ruleQUALIFIED_NAME ) ) ) )? (otherlv_19= 'enumType' ( (otherlv_20= RULE_STRING ) ) )? (otherlv_21= 'description' ( (lv_description_22_0= RULE_STRING ) ) )? (otherlv_23= 'defaultValue' ( (lv_defaultValue_24_0= RULE_STRING ) ) )? (otherlv_25= 'fileExtension' ( (lv_fileExtension_26_0= RULE_STRING ) ) )? otherlv_27= '}'
            {
            otherlv_0=(Token)match(input,25,FOLLOW_25_in_ruleXAttributeType1268); 

                	newLeafNode(otherlv_0, grammarAccess.getXAttributeTypeAccess().getAttributeTypeKeyword_0());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:622:1: ( (lv_name_1_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:623:1: (lv_name_1_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:623:1: (lv_name_1_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:624:3: lv_name_1_0= RULE_STRING
            {
            lv_name_1_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXAttributeType1285); 

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
            otherlv_2=(Token)match(input,17,FOLLOW_17_in_ruleXAttributeType1303); 

                	newLeafNode(otherlv_2, grammarAccess.getXAttributeTypeAccess().getExtendsKeyword_2_0());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:644:1: ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:645:1: (lv_baseAttributeType_3_0= ruleAttributeBaseType )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:645:1: (lv_baseAttributeType_3_0= ruleAttributeBaseType )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:646:3: lv_baseAttributeType_3_0= ruleAttributeBaseType
            {
             
            	        newCompositeNode(grammarAccess.getXAttributeTypeAccess().getBaseAttributeTypeAttributeBaseTypeParserRuleCall_2_1_0()); 
            	    
            pushFollow(FOLLOW_ruleAttributeBaseType_in_ruleXAttributeType1324);
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
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==26) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:662:5: otherlv_4= 'overrides' ( (otherlv_5= RULE_STRING ) )
                    {
                    otherlv_4=(Token)match(input,26,FOLLOW_26_in_ruleXAttributeType1338); 

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
                            
                    otherlv_5=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXAttributeType1358); 

                    		newLeafNode(otherlv_5, grammarAccess.getXAttributeTypeAccess().getOverrideXAttributeTypeCrossReference_3_1_0()); 
                    	

                    }


                    }


                    }
                    break;

            }

            otherlv_6=(Token)match(input,19,FOLLOW_19_in_ruleXAttributeType1372); 

                	newLeafNode(otherlv_6, grammarAccess.getXAttributeTypeAccess().getLeftCurlyBracketKeyword_4());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:683:1: (otherlv_7= 'guid' ( (lv_typeGuid_8_0= RULE_STRING ) ) )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==20) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:683:3: otherlv_7= 'guid' ( (lv_typeGuid_8_0= RULE_STRING ) )
                    {
                    otherlv_7=(Token)match(input,20,FOLLOW_20_in_ruleXAttributeType1385); 

                        	newLeafNode(otherlv_7, grammarAccess.getXAttributeTypeAccess().getGuidKeyword_5_0());
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:687:1: ( (lv_typeGuid_8_0= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:688:1: (lv_typeGuid_8_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:688:1: (lv_typeGuid_8_0= RULE_STRING )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:689:3: lv_typeGuid_8_0= RULE_STRING
                    {
                    lv_typeGuid_8_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXAttributeType1402); 

                    			newLeafNode(lv_typeGuid_8_0, grammarAccess.getXAttributeTypeAccess().getTypeGuidSTRINGTerminalRuleCall_5_1_0()); 
                    		

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


                    }
                    break;

            }

            otherlv_9=(Token)match(input,21,FOLLOW_21_in_ruleXAttributeType1421); 

                	newLeafNode(otherlv_9, grammarAccess.getXAttributeTypeAccess().getUuidKeyword_6());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:709:1: ( (lv_uuid_10_0= RULE_HEX_INT ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:710:1: (lv_uuid_10_0= RULE_HEX_INT )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:710:1: (lv_uuid_10_0= RULE_HEX_INT )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:711:3: lv_uuid_10_0= RULE_HEX_INT
            {
            lv_uuid_10_0=(Token)match(input,RULE_HEX_INT,FOLLOW_RULE_HEX_INT_in_ruleXAttributeType1438); 

            			newLeafNode(lv_uuid_10_0, grammarAccess.getXAttributeTypeAccess().getUuidHEX_INTTerminalRuleCall_7_0()); 
            		

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

            otherlv_11=(Token)match(input,27,FOLLOW_27_in_ruleXAttributeType1455); 

                	newLeafNode(otherlv_11, grammarAccess.getXAttributeTypeAccess().getDataProviderKeyword_8());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:731:1: ( ( (lv_dataProvider_12_1= 'DefaultAttributeDataProvider' | lv_dataProvider_12_2= 'UriAttributeDataProvider' | lv_dataProvider_12_3= ruleQUALIFIED_NAME ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:732:1: ( (lv_dataProvider_12_1= 'DefaultAttributeDataProvider' | lv_dataProvider_12_2= 'UriAttributeDataProvider' | lv_dataProvider_12_3= ruleQUALIFIED_NAME ) )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:732:1: ( (lv_dataProvider_12_1= 'DefaultAttributeDataProvider' | lv_dataProvider_12_2= 'UriAttributeDataProvider' | lv_dataProvider_12_3= ruleQUALIFIED_NAME ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:733:1: (lv_dataProvider_12_1= 'DefaultAttributeDataProvider' | lv_dataProvider_12_2= 'UriAttributeDataProvider' | lv_dataProvider_12_3= ruleQUALIFIED_NAME )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:733:1: (lv_dataProvider_12_1= 'DefaultAttributeDataProvider' | lv_dataProvider_12_2= 'UriAttributeDataProvider' | lv_dataProvider_12_3= ruleQUALIFIED_NAME )
            int alt15=3;
            switch ( input.LA(1) ) {
            case 28:
                {
                alt15=1;
                }
                break;
            case 29:
                {
                alt15=2;
                }
                break;
            case RULE_ID:
                {
                alt15=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 15, 0, input);

                throw nvae;
            }

            switch (alt15) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:734:3: lv_dataProvider_12_1= 'DefaultAttributeDataProvider'
                    {
                    lv_dataProvider_12_1=(Token)match(input,28,FOLLOW_28_in_ruleXAttributeType1475); 

                            newLeafNode(lv_dataProvider_12_1, grammarAccess.getXAttributeTypeAccess().getDataProviderDefaultAttributeDataProviderKeyword_9_0_0());
                        

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getXAttributeTypeRule());
                    	        }
                           		setWithLastConsumed(current, "dataProvider", lv_dataProvider_12_1, null);
                    	    

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:746:8: lv_dataProvider_12_2= 'UriAttributeDataProvider'
                    {
                    lv_dataProvider_12_2=(Token)match(input,29,FOLLOW_29_in_ruleXAttributeType1504); 

                            newLeafNode(lv_dataProvider_12_2, grammarAccess.getXAttributeTypeAccess().getDataProviderUriAttributeDataProviderKeyword_9_0_1());
                        

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getXAttributeTypeRule());
                    	        }
                           		setWithLastConsumed(current, "dataProvider", lv_dataProvider_12_2, null);
                    	    

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:758:8: lv_dataProvider_12_3= ruleQUALIFIED_NAME
                    {
                     
                    	        newCompositeNode(grammarAccess.getXAttributeTypeAccess().getDataProviderQUALIFIED_NAMEParserRuleCall_9_0_2()); 
                    	    
                    pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_ruleXAttributeType1536);
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

            otherlv_13=(Token)match(input,30,FOLLOW_30_in_ruleXAttributeType1551); 

                	newLeafNode(otherlv_13, grammarAccess.getXAttributeTypeAccess().getMinKeyword_10());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:780:1: ( (lv_min_14_0= RULE_WHOLE_NUM_STR ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:781:1: (lv_min_14_0= RULE_WHOLE_NUM_STR )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:781:1: (lv_min_14_0= RULE_WHOLE_NUM_STR )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:782:3: lv_min_14_0= RULE_WHOLE_NUM_STR
            {
            lv_min_14_0=(Token)match(input,RULE_WHOLE_NUM_STR,FOLLOW_RULE_WHOLE_NUM_STR_in_ruleXAttributeType1568); 

            			newLeafNode(lv_min_14_0, grammarAccess.getXAttributeTypeAccess().getMinWHOLE_NUM_STRTerminalRuleCall_11_0()); 
            		

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

            otherlv_15=(Token)match(input,31,FOLLOW_31_in_ruleXAttributeType1585); 

                	newLeafNode(otherlv_15, grammarAccess.getXAttributeTypeAccess().getMaxKeyword_12());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:802:1: ( ( (lv_max_16_1= RULE_WHOLE_NUM_STR | lv_max_16_2= 'unlimited' ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:803:1: ( (lv_max_16_1= RULE_WHOLE_NUM_STR | lv_max_16_2= 'unlimited' ) )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:803:1: ( (lv_max_16_1= RULE_WHOLE_NUM_STR | lv_max_16_2= 'unlimited' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:804:1: (lv_max_16_1= RULE_WHOLE_NUM_STR | lv_max_16_2= 'unlimited' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:804:1: (lv_max_16_1= RULE_WHOLE_NUM_STR | lv_max_16_2= 'unlimited' )
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==RULE_WHOLE_NUM_STR) ) {
                alt16=1;
            }
            else if ( (LA16_0==32) ) {
                alt16=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 16, 0, input);

                throw nvae;
            }
            switch (alt16) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:805:3: lv_max_16_1= RULE_WHOLE_NUM_STR
                    {
                    lv_max_16_1=(Token)match(input,RULE_WHOLE_NUM_STR,FOLLOW_RULE_WHOLE_NUM_STR_in_ruleXAttributeType1604); 

                    			newLeafNode(lv_max_16_1, grammarAccess.getXAttributeTypeAccess().getMaxWHOLE_NUM_STRTerminalRuleCall_13_0_0()); 
                    		

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
                    lv_max_16_2=(Token)match(input,32,FOLLOW_32_in_ruleXAttributeType1625); 

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

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:835:2: (otherlv_17= 'taggerId' ( ( (lv_taggerId_18_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_18_2= ruleQUALIFIED_NAME ) ) ) )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==33) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:835:4: otherlv_17= 'taggerId' ( ( (lv_taggerId_18_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_18_2= ruleQUALIFIED_NAME ) ) )
                    {
                    otherlv_17=(Token)match(input,33,FOLLOW_33_in_ruleXAttributeType1654); 

                        	newLeafNode(otherlv_17, grammarAccess.getXAttributeTypeAccess().getTaggerIdKeyword_14_0());
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:839:1: ( ( (lv_taggerId_18_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_18_2= ruleQUALIFIED_NAME ) ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:840:1: ( (lv_taggerId_18_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_18_2= ruleQUALIFIED_NAME ) )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:840:1: ( (lv_taggerId_18_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_18_2= ruleQUALIFIED_NAME ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:841:1: (lv_taggerId_18_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_18_2= ruleQUALIFIED_NAME )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:841:1: (lv_taggerId_18_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_18_2= ruleQUALIFIED_NAME )
                    int alt17=2;
                    int LA17_0 = input.LA(1);

                    if ( (LA17_0==34) ) {
                        alt17=1;
                    }
                    else if ( (LA17_0==RULE_ID) ) {
                        alt17=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 17, 0, input);

                        throw nvae;
                    }
                    switch (alt17) {
                        case 1 :
                            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:842:3: lv_taggerId_18_1= 'DefaultAttributeTaggerProvider'
                            {
                            lv_taggerId_18_1=(Token)match(input,34,FOLLOW_34_in_ruleXAttributeType1674); 

                                    newLeafNode(lv_taggerId_18_1, grammarAccess.getXAttributeTypeAccess().getTaggerIdDefaultAttributeTaggerProviderKeyword_14_1_0_0());
                                

                            	        if (current==null) {
                            	            current = createModelElement(grammarAccess.getXAttributeTypeRule());
                            	        }
                                   		setWithLastConsumed(current, "taggerId", lv_taggerId_18_1, null);
                            	    

                            }
                            break;
                        case 2 :
                            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:854:8: lv_taggerId_18_2= ruleQUALIFIED_NAME
                            {
                             
                            	        newCompositeNode(grammarAccess.getXAttributeTypeAccess().getTaggerIdQUALIFIED_NAMEParserRuleCall_14_1_0_1()); 
                            	    
                            pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_ruleXAttributeType1706);
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
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==35) ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:872:6: otherlv_19= 'enumType' ( (otherlv_20= RULE_STRING ) )
                    {
                    otherlv_19=(Token)match(input,35,FOLLOW_35_in_ruleXAttributeType1724); 

                        	newLeafNode(otherlv_19, grammarAccess.getXAttributeTypeAccess().getEnumTypeKeyword_15_0());
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:876:1: ( (otherlv_20= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:877:1: (otherlv_20= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:877:1: (otherlv_20= RULE_STRING )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:878:3: otherlv_20= RULE_STRING
                    {

                    			if (current==null) {
                    	            current = createModelElement(grammarAccess.getXAttributeTypeRule());
                    	        }
                            
                    otherlv_20=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXAttributeType1744); 

                    		newLeafNode(otherlv_20, grammarAccess.getXAttributeTypeAccess().getEnumTypeXOseeEnumTypeCrossReference_15_1_0()); 
                    	

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:889:4: (otherlv_21= 'description' ( (lv_description_22_0= RULE_STRING ) ) )?
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==36) ) {
                alt20=1;
            }
            switch (alt20) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:889:6: otherlv_21= 'description' ( (lv_description_22_0= RULE_STRING ) )
                    {
                    otherlv_21=(Token)match(input,36,FOLLOW_36_in_ruleXAttributeType1759); 

                        	newLeafNode(otherlv_21, grammarAccess.getXAttributeTypeAccess().getDescriptionKeyword_16_0());
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:893:1: ( (lv_description_22_0= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:894:1: (lv_description_22_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:894:1: (lv_description_22_0= RULE_STRING )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:895:3: lv_description_22_0= RULE_STRING
                    {
                    lv_description_22_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXAttributeType1776); 

                    			newLeafNode(lv_description_22_0, grammarAccess.getXAttributeTypeAccess().getDescriptionSTRINGTerminalRuleCall_16_1_0()); 
                    		

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
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==37) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:911:6: otherlv_23= 'defaultValue' ( (lv_defaultValue_24_0= RULE_STRING ) )
                    {
                    otherlv_23=(Token)match(input,37,FOLLOW_37_in_ruleXAttributeType1796); 

                        	newLeafNode(otherlv_23, grammarAccess.getXAttributeTypeAccess().getDefaultValueKeyword_17_0());
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:915:1: ( (lv_defaultValue_24_0= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:916:1: (lv_defaultValue_24_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:916:1: (lv_defaultValue_24_0= RULE_STRING )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:917:3: lv_defaultValue_24_0= RULE_STRING
                    {
                    lv_defaultValue_24_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXAttributeType1813); 

                    			newLeafNode(lv_defaultValue_24_0, grammarAccess.getXAttributeTypeAccess().getDefaultValueSTRINGTerminalRuleCall_17_1_0()); 
                    		

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
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0==38) ) {
                alt22=1;
            }
            switch (alt22) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:933:6: otherlv_25= 'fileExtension' ( (lv_fileExtension_26_0= RULE_STRING ) )
                    {
                    otherlv_25=(Token)match(input,38,FOLLOW_38_in_ruleXAttributeType1833); 

                        	newLeafNode(otherlv_25, grammarAccess.getXAttributeTypeAccess().getFileExtensionKeyword_18_0());
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:937:1: ( (lv_fileExtension_26_0= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:938:1: (lv_fileExtension_26_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:938:1: (lv_fileExtension_26_0= RULE_STRING )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:939:3: lv_fileExtension_26_0= RULE_STRING
                    {
                    lv_fileExtension_26_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXAttributeType1850); 

                    			newLeafNode(lv_fileExtension_26_0, grammarAccess.getXAttributeTypeAccess().getFileExtensionSTRINGTerminalRuleCall_18_1_0()); 
                    		

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

            otherlv_27=(Token)match(input,22,FOLLOW_22_in_ruleXAttributeType1869); 

                	newLeafNode(otherlv_27, grammarAccess.getXAttributeTypeAccess().getRightCurlyBracketKeyword_19());
                

            }


            }

             leaveRule(); 
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
            pushFollow(FOLLOW_ruleAttributeBaseType_in_entryRuleAttributeBaseType1906);
            iv_ruleAttributeBaseType=ruleAttributeBaseType();

            state._fsp--;

             current =iv_ruleAttributeBaseType.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttributeBaseType1917); 

            }

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:976:1: ruleAttributeBaseType returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'BooleanAttribute' | kw= 'CompressedContentAttribute' | kw= 'DateAttribute' | kw= 'EnumeratedAttribute' | kw= 'FloatingPointAttribute' | kw= 'IntegerAttribute' | kw= 'JavaObjectAttribute' | kw= 'StringAttribute' | kw= 'WordAttribute' | kw= 'OutlineNumberAttribute' | this_QUALIFIED_NAME_10= ruleQUALIFIED_NAME ) ;
    public final AntlrDatatypeRuleToken ruleAttributeBaseType() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        AntlrDatatypeRuleToken this_QUALIFIED_NAME_10 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:979:28: ( (kw= 'BooleanAttribute' | kw= 'CompressedContentAttribute' | kw= 'DateAttribute' | kw= 'EnumeratedAttribute' | kw= 'FloatingPointAttribute' | kw= 'IntegerAttribute' | kw= 'JavaObjectAttribute' | kw= 'StringAttribute' | kw= 'WordAttribute' | kw= 'OutlineNumberAttribute' | this_QUALIFIED_NAME_10= ruleQUALIFIED_NAME ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:980:1: (kw= 'BooleanAttribute' | kw= 'CompressedContentAttribute' | kw= 'DateAttribute' | kw= 'EnumeratedAttribute' | kw= 'FloatingPointAttribute' | kw= 'IntegerAttribute' | kw= 'JavaObjectAttribute' | kw= 'StringAttribute' | kw= 'WordAttribute' | kw= 'OutlineNumberAttribute' | this_QUALIFIED_NAME_10= ruleQUALIFIED_NAME )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:980:1: (kw= 'BooleanAttribute' | kw= 'CompressedContentAttribute' | kw= 'DateAttribute' | kw= 'EnumeratedAttribute' | kw= 'FloatingPointAttribute' | kw= 'IntegerAttribute' | kw= 'JavaObjectAttribute' | kw= 'StringAttribute' | kw= 'WordAttribute' | kw= 'OutlineNumberAttribute' | this_QUALIFIED_NAME_10= ruleQUALIFIED_NAME )
            int alt23=11;
            switch ( input.LA(1) ) {
            case 39:
                {
                alt23=1;
                }
                break;
            case 40:
                {
                alt23=2;
                }
                break;
            case 41:
                {
                alt23=3;
                }
                break;
            case 42:
                {
                alt23=4;
                }
                break;
            case 43:
                {
                alt23=5;
                }
                break;
            case 44:
                {
                alt23=6;
                }
                break;
            case 45:
                {
                alt23=7;
                }
                break;
            case 46:
                {
                alt23=8;
                }
                break;
            case 47:
                {
                alt23=9;
                }
                break;
            case 48:
                {
                alt23=10;
                }
                break;
            case RULE_ID:
                {
                alt23=11;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 23, 0, input);

                throw nvae;
            }

            switch (alt23) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:981:2: kw= 'BooleanAttribute'
                    {
                    kw=(Token)match(input,39,FOLLOW_39_in_ruleAttributeBaseType1955); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getAttributeBaseTypeAccess().getBooleanAttributeKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:988:2: kw= 'CompressedContentAttribute'
                    {
                    kw=(Token)match(input,40,FOLLOW_40_in_ruleAttributeBaseType1974); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getAttributeBaseTypeAccess().getCompressedContentAttributeKeyword_1()); 
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:995:2: kw= 'DateAttribute'
                    {
                    kw=(Token)match(input,41,FOLLOW_41_in_ruleAttributeBaseType1993); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getAttributeBaseTypeAccess().getDateAttributeKeyword_2()); 
                        

                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1002:2: kw= 'EnumeratedAttribute'
                    {
                    kw=(Token)match(input,42,FOLLOW_42_in_ruleAttributeBaseType2012); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getAttributeBaseTypeAccess().getEnumeratedAttributeKeyword_3()); 
                        

                    }
                    break;
                case 5 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1009:2: kw= 'FloatingPointAttribute'
                    {
                    kw=(Token)match(input,43,FOLLOW_43_in_ruleAttributeBaseType2031); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getAttributeBaseTypeAccess().getFloatingPointAttributeKeyword_4()); 
                        

                    }
                    break;
                case 6 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1016:2: kw= 'IntegerAttribute'
                    {
                    kw=(Token)match(input,44,FOLLOW_44_in_ruleAttributeBaseType2050); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getAttributeBaseTypeAccess().getIntegerAttributeKeyword_5()); 
                        

                    }
                    break;
                case 7 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1023:2: kw= 'JavaObjectAttribute'
                    {
                    kw=(Token)match(input,45,FOLLOW_45_in_ruleAttributeBaseType2069); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getAttributeBaseTypeAccess().getJavaObjectAttributeKeyword_6()); 
                        

                    }
                    break;
                case 8 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1030:2: kw= 'StringAttribute'
                    {
                    kw=(Token)match(input,46,FOLLOW_46_in_ruleAttributeBaseType2088); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getAttributeBaseTypeAccess().getStringAttributeKeyword_7()); 
                        

                    }
                    break;
                case 9 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1037:2: kw= 'WordAttribute'
                    {
                    kw=(Token)match(input,47,FOLLOW_47_in_ruleAttributeBaseType2107); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getAttributeBaseTypeAccess().getWordAttributeKeyword_8()); 
                        

                    }
                    break;
                case 10 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1044:2: kw= 'OutlineNumberAttribute'
                    {
                    kw=(Token)match(input,48,FOLLOW_48_in_ruleAttributeBaseType2126); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getAttributeBaseTypeAccess().getOutlineNumberAttributeKeyword_9()); 
                        

                    }
                    break;
                case 11 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1051:5: this_QUALIFIED_NAME_10= ruleQUALIFIED_NAME
                    {
                     
                            newCompositeNode(grammarAccess.getAttributeBaseTypeAccess().getQUALIFIED_NAMEParserRuleCall_10()); 
                        
                    pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_ruleAttributeBaseType2154);
                    this_QUALIFIED_NAME_10=ruleQUALIFIED_NAME();

                    state._fsp--;


                    		current.merge(this_QUALIFIED_NAME_10);
                        
                     
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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1069:1: entryRuleXOseeEnumType returns [EObject current=null] : iv_ruleXOseeEnumType= ruleXOseeEnumType EOF ;
    public final EObject entryRuleXOseeEnumType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleXOseeEnumType = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1070:2: (iv_ruleXOseeEnumType= ruleXOseeEnumType EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1071:2: iv_ruleXOseeEnumType= ruleXOseeEnumType EOF
            {
             newCompositeNode(grammarAccess.getXOseeEnumTypeRule()); 
            pushFollow(FOLLOW_ruleXOseeEnumType_in_entryRuleXOseeEnumType2199);
            iv_ruleXOseeEnumType=ruleXOseeEnumType();

            state._fsp--;

             current =iv_ruleXOseeEnumType; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleXOseeEnumType2209); 

            }

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1078:1: ruleXOseeEnumType returns [EObject current=null] : (otherlv_0= 'oseeEnumType' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= '{' (otherlv_3= 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) )? otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_HEX_INT ) ) ( (lv_enumEntries_7_0= ruleXOseeEnumEntry ) )* otherlv_8= '}' ) ;
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
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1081:28: ( (otherlv_0= 'oseeEnumType' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= '{' (otherlv_3= 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) )? otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_HEX_INT ) ) ( (lv_enumEntries_7_0= ruleXOseeEnumEntry ) )* otherlv_8= '}' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1082:1: (otherlv_0= 'oseeEnumType' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= '{' (otherlv_3= 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) )? otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_HEX_INT ) ) ( (lv_enumEntries_7_0= ruleXOseeEnumEntry ) )* otherlv_8= '}' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1082:1: (otherlv_0= 'oseeEnumType' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= '{' (otherlv_3= 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) )? otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_HEX_INT ) ) ( (lv_enumEntries_7_0= ruleXOseeEnumEntry ) )* otherlv_8= '}' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1082:3: otherlv_0= 'oseeEnumType' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= '{' (otherlv_3= 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) )? otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_HEX_INT ) ) ( (lv_enumEntries_7_0= ruleXOseeEnumEntry ) )* otherlv_8= '}'
            {
            otherlv_0=(Token)match(input,49,FOLLOW_49_in_ruleXOseeEnumType2246); 

                	newLeafNode(otherlv_0, grammarAccess.getXOseeEnumTypeAccess().getOseeEnumTypeKeyword_0());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1086:1: ( (lv_name_1_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1087:1: (lv_name_1_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1087:1: (lv_name_1_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1088:3: lv_name_1_0= RULE_STRING
            {
            lv_name_1_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXOseeEnumType2263); 

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

            otherlv_2=(Token)match(input,19,FOLLOW_19_in_ruleXOseeEnumType2280); 

                	newLeafNode(otherlv_2, grammarAccess.getXOseeEnumTypeAccess().getLeftCurlyBracketKeyword_2());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1108:1: (otherlv_3= 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) )?
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==20) ) {
                alt24=1;
            }
            switch (alt24) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1108:3: otherlv_3= 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) )
                    {
                    otherlv_3=(Token)match(input,20,FOLLOW_20_in_ruleXOseeEnumType2293); 

                        	newLeafNode(otherlv_3, grammarAccess.getXOseeEnumTypeAccess().getGuidKeyword_3_0());
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1112:1: ( (lv_typeGuid_4_0= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1113:1: (lv_typeGuid_4_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1113:1: (lv_typeGuid_4_0= RULE_STRING )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1114:3: lv_typeGuid_4_0= RULE_STRING
                    {
                    lv_typeGuid_4_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXOseeEnumType2310); 

                    			newLeafNode(lv_typeGuid_4_0, grammarAccess.getXOseeEnumTypeAccess().getTypeGuidSTRINGTerminalRuleCall_3_1_0()); 
                    		

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


                    }
                    break;

            }

            otherlv_5=(Token)match(input,21,FOLLOW_21_in_ruleXOseeEnumType2329); 

                	newLeafNode(otherlv_5, grammarAccess.getXOseeEnumTypeAccess().getUuidKeyword_4());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1134:1: ( (lv_uuid_6_0= RULE_HEX_INT ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1135:1: (lv_uuid_6_0= RULE_HEX_INT )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1135:1: (lv_uuid_6_0= RULE_HEX_INT )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1136:3: lv_uuid_6_0= RULE_HEX_INT
            {
            lv_uuid_6_0=(Token)match(input,RULE_HEX_INT,FOLLOW_RULE_HEX_INT_in_ruleXOseeEnumType2346); 

            			newLeafNode(lv_uuid_6_0, grammarAccess.getXOseeEnumTypeAccess().getUuidHEX_INTTerminalRuleCall_5_0()); 
            		

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

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1152:2: ( (lv_enumEntries_7_0= ruleXOseeEnumEntry ) )*
            loop25:
            do {
                int alt25=2;
                int LA25_0 = input.LA(1);

                if ( (LA25_0==50) ) {
                    alt25=1;
                }


                switch (alt25) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1153:1: (lv_enumEntries_7_0= ruleXOseeEnumEntry )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1153:1: (lv_enumEntries_7_0= ruleXOseeEnumEntry )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1154:3: lv_enumEntries_7_0= ruleXOseeEnumEntry
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getXOseeEnumTypeAccess().getEnumEntriesXOseeEnumEntryParserRuleCall_6_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleXOseeEnumEntry_in_ruleXOseeEnumType2372);
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
            	    break loop25;
                }
            } while (true);

            otherlv_8=(Token)match(input,22,FOLLOW_22_in_ruleXOseeEnumType2385); 

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1182:1: entryRuleXOseeEnumEntry returns [EObject current=null] : iv_ruleXOseeEnumEntry= ruleXOseeEnumEntry EOF ;
    public final EObject entryRuleXOseeEnumEntry() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleXOseeEnumEntry = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1183:2: (iv_ruleXOseeEnumEntry= ruleXOseeEnumEntry EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1184:2: iv_ruleXOseeEnumEntry= ruleXOseeEnumEntry EOF
            {
             newCompositeNode(grammarAccess.getXOseeEnumEntryRule()); 
            pushFollow(FOLLOW_ruleXOseeEnumEntry_in_entryRuleXOseeEnumEntry2421);
            iv_ruleXOseeEnumEntry=ruleXOseeEnumEntry();

            state._fsp--;

             current =iv_ruleXOseeEnumEntry; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleXOseeEnumEntry2431); 

            }

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1191:1: ruleXOseeEnumEntry returns [EObject current=null] : (otherlv_0= 'entry' ( (lv_name_1_0= RULE_STRING ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? (otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? ) ;
    public final EObject ruleXOseeEnumEntry() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_name_1_0=null;
        Token lv_ordinal_2_0=null;
        Token otherlv_3=null;
        Token lv_entryGuid_4_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1194:28: ( (otherlv_0= 'entry' ( (lv_name_1_0= RULE_STRING ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? (otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1195:1: (otherlv_0= 'entry' ( (lv_name_1_0= RULE_STRING ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? (otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1195:1: (otherlv_0= 'entry' ( (lv_name_1_0= RULE_STRING ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? (otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1195:3: otherlv_0= 'entry' ( (lv_name_1_0= RULE_STRING ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? (otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )?
            {
            otherlv_0=(Token)match(input,50,FOLLOW_50_in_ruleXOseeEnumEntry2468); 

                	newLeafNode(otherlv_0, grammarAccess.getXOseeEnumEntryAccess().getEntryKeyword_0());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1199:1: ( (lv_name_1_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1200:1: (lv_name_1_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1200:1: (lv_name_1_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1201:3: lv_name_1_0= RULE_STRING
            {
            lv_name_1_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXOseeEnumEntry2485); 

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

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1217:2: ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )?
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==RULE_WHOLE_NUM_STR) ) {
                alt26=1;
            }
            switch (alt26) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1218:1: (lv_ordinal_2_0= RULE_WHOLE_NUM_STR )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1218:1: (lv_ordinal_2_0= RULE_WHOLE_NUM_STR )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1219:3: lv_ordinal_2_0= RULE_WHOLE_NUM_STR
                    {
                    lv_ordinal_2_0=(Token)match(input,RULE_WHOLE_NUM_STR,FOLLOW_RULE_WHOLE_NUM_STR_in_ruleXOseeEnumEntry2507); 

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

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1235:3: (otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )?
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==51) ) {
                alt27=1;
            }
            switch (alt27) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1235:5: otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) )
                    {
                    otherlv_3=(Token)match(input,51,FOLLOW_51_in_ruleXOseeEnumEntry2526); 

                        	newLeafNode(otherlv_3, grammarAccess.getXOseeEnumEntryAccess().getEntryGuidKeyword_3_0());
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1239:1: ( (lv_entryGuid_4_0= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1240:1: (lv_entryGuid_4_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1240:1: (lv_entryGuid_4_0= RULE_STRING )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1241:3: lv_entryGuid_4_0= RULE_STRING
                    {
                    lv_entryGuid_4_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXOseeEnumEntry2543); 

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1265:1: entryRuleXOseeEnumOverride returns [EObject current=null] : iv_ruleXOseeEnumOverride= ruleXOseeEnumOverride EOF ;
    public final EObject entryRuleXOseeEnumOverride() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleXOseeEnumOverride = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1266:2: (iv_ruleXOseeEnumOverride= ruleXOseeEnumOverride EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1267:2: iv_ruleXOseeEnumOverride= ruleXOseeEnumOverride EOF
            {
             newCompositeNode(grammarAccess.getXOseeEnumOverrideRule()); 
            pushFollow(FOLLOW_ruleXOseeEnumOverride_in_entryRuleXOseeEnumOverride2586);
            iv_ruleXOseeEnumOverride=ruleXOseeEnumOverride();

            state._fsp--;

             current =iv_ruleXOseeEnumOverride; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleXOseeEnumOverride2596); 

            }

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1274:1: ruleXOseeEnumOverride returns [EObject current=null] : (otherlv_0= 'overrides enum' ( (otherlv_1= RULE_STRING ) ) otherlv_2= '{' ( (lv_inheritAll_3_0= 'inheritAll' ) )? ( (lv_overrideOptions_4_0= ruleOverrideOption ) )* otherlv_5= '}' ) ;
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
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1277:28: ( (otherlv_0= 'overrides enum' ( (otherlv_1= RULE_STRING ) ) otherlv_2= '{' ( (lv_inheritAll_3_0= 'inheritAll' ) )? ( (lv_overrideOptions_4_0= ruleOverrideOption ) )* otherlv_5= '}' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1278:1: (otherlv_0= 'overrides enum' ( (otherlv_1= RULE_STRING ) ) otherlv_2= '{' ( (lv_inheritAll_3_0= 'inheritAll' ) )? ( (lv_overrideOptions_4_0= ruleOverrideOption ) )* otherlv_5= '}' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1278:1: (otherlv_0= 'overrides enum' ( (otherlv_1= RULE_STRING ) ) otherlv_2= '{' ( (lv_inheritAll_3_0= 'inheritAll' ) )? ( (lv_overrideOptions_4_0= ruleOverrideOption ) )* otherlv_5= '}' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1278:3: otherlv_0= 'overrides enum' ( (otherlv_1= RULE_STRING ) ) otherlv_2= '{' ( (lv_inheritAll_3_0= 'inheritAll' ) )? ( (lv_overrideOptions_4_0= ruleOverrideOption ) )* otherlv_5= '}'
            {
            otherlv_0=(Token)match(input,52,FOLLOW_52_in_ruleXOseeEnumOverride2633); 

                	newLeafNode(otherlv_0, grammarAccess.getXOseeEnumOverrideAccess().getOverridesEnumKeyword_0());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1282:1: ( (otherlv_1= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1283:1: (otherlv_1= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1283:1: (otherlv_1= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1284:3: otherlv_1= RULE_STRING
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getXOseeEnumOverrideRule());
            	        }
                    
            otherlv_1=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXOseeEnumOverride2653); 

            		newLeafNode(otherlv_1, grammarAccess.getXOseeEnumOverrideAccess().getOverridenEnumTypeXOseeEnumTypeCrossReference_1_0()); 
            	

            }


            }

            otherlv_2=(Token)match(input,19,FOLLOW_19_in_ruleXOseeEnumOverride2665); 

                	newLeafNode(otherlv_2, grammarAccess.getXOseeEnumOverrideAccess().getLeftCurlyBracketKeyword_2());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1299:1: ( (lv_inheritAll_3_0= 'inheritAll' ) )?
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==53) ) {
                alt28=1;
            }
            switch (alt28) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1300:1: (lv_inheritAll_3_0= 'inheritAll' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1300:1: (lv_inheritAll_3_0= 'inheritAll' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1301:3: lv_inheritAll_3_0= 'inheritAll'
                    {
                    lv_inheritAll_3_0=(Token)match(input,53,FOLLOW_53_in_ruleXOseeEnumOverride2683); 

                            newLeafNode(lv_inheritAll_3_0, grammarAccess.getXOseeEnumOverrideAccess().getInheritAllInheritAllKeyword_3_0());
                        

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getXOseeEnumOverrideRule());
                    	        }
                           		setWithLastConsumed(current, "inheritAll", true, "inheritAll");
                    	    

                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1314:3: ( (lv_overrideOptions_4_0= ruleOverrideOption ) )*
            loop29:
            do {
                int alt29=2;
                int LA29_0 = input.LA(1);

                if ( ((LA29_0>=54 && LA29_0<=55)) ) {
                    alt29=1;
                }


                switch (alt29) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1315:1: (lv_overrideOptions_4_0= ruleOverrideOption )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1315:1: (lv_overrideOptions_4_0= ruleOverrideOption )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1316:3: lv_overrideOptions_4_0= ruleOverrideOption
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getXOseeEnumOverrideAccess().getOverrideOptionsOverrideOptionParserRuleCall_4_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleOverrideOption_in_ruleXOseeEnumOverride2718);
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
            	    break loop29;
                }
            } while (true);

            otherlv_5=(Token)match(input,22,FOLLOW_22_in_ruleXOseeEnumOverride2731); 

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1344:1: entryRuleOverrideOption returns [EObject current=null] : iv_ruleOverrideOption= ruleOverrideOption EOF ;
    public final EObject entryRuleOverrideOption() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOverrideOption = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1345:2: (iv_ruleOverrideOption= ruleOverrideOption EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1346:2: iv_ruleOverrideOption= ruleOverrideOption EOF
            {
             newCompositeNode(grammarAccess.getOverrideOptionRule()); 
            pushFollow(FOLLOW_ruleOverrideOption_in_entryRuleOverrideOption2767);
            iv_ruleOverrideOption=ruleOverrideOption();

            state._fsp--;

             current =iv_ruleOverrideOption; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleOverrideOption2777); 

            }

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1353:1: ruleOverrideOption returns [EObject current=null] : (this_AddEnum_0= ruleAddEnum | this_RemoveEnum_1= ruleRemoveEnum ) ;
    public final EObject ruleOverrideOption() throws RecognitionException {
        EObject current = null;

        EObject this_AddEnum_0 = null;

        EObject this_RemoveEnum_1 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1356:28: ( (this_AddEnum_0= ruleAddEnum | this_RemoveEnum_1= ruleRemoveEnum ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1357:1: (this_AddEnum_0= ruleAddEnum | this_RemoveEnum_1= ruleRemoveEnum )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1357:1: (this_AddEnum_0= ruleAddEnum | this_RemoveEnum_1= ruleRemoveEnum )
            int alt30=2;
            int LA30_0 = input.LA(1);

            if ( (LA30_0==54) ) {
                alt30=1;
            }
            else if ( (LA30_0==55) ) {
                alt30=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 30, 0, input);

                throw nvae;
            }
            switch (alt30) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1358:5: this_AddEnum_0= ruleAddEnum
                    {
                     
                            newCompositeNode(grammarAccess.getOverrideOptionAccess().getAddEnumParserRuleCall_0()); 
                        
                    pushFollow(FOLLOW_ruleAddEnum_in_ruleOverrideOption2824);
                    this_AddEnum_0=ruleAddEnum();

                    state._fsp--;

                     
                            current = this_AddEnum_0; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1368:5: this_RemoveEnum_1= ruleRemoveEnum
                    {
                     
                            newCompositeNode(grammarAccess.getOverrideOptionAccess().getRemoveEnumParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_ruleRemoveEnum_in_ruleOverrideOption2851);
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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1384:1: entryRuleAddEnum returns [EObject current=null] : iv_ruleAddEnum= ruleAddEnum EOF ;
    public final EObject entryRuleAddEnum() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAddEnum = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1385:2: (iv_ruleAddEnum= ruleAddEnum EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1386:2: iv_ruleAddEnum= ruleAddEnum EOF
            {
             newCompositeNode(grammarAccess.getAddEnumRule()); 
            pushFollow(FOLLOW_ruleAddEnum_in_entryRuleAddEnum2886);
            iv_ruleAddEnum=ruleAddEnum();

            state._fsp--;

             current =iv_ruleAddEnum; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAddEnum2896); 

            }

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1393:1: ruleAddEnum returns [EObject current=null] : (otherlv_0= 'add' ( (lv_enumEntry_1_0= RULE_STRING ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? (otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? ) ;
    public final EObject ruleAddEnum() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_enumEntry_1_0=null;
        Token lv_ordinal_2_0=null;
        Token otherlv_3=null;
        Token lv_entryGuid_4_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1396:28: ( (otherlv_0= 'add' ( (lv_enumEntry_1_0= RULE_STRING ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? (otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1397:1: (otherlv_0= 'add' ( (lv_enumEntry_1_0= RULE_STRING ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? (otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1397:1: (otherlv_0= 'add' ( (lv_enumEntry_1_0= RULE_STRING ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? (otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1397:3: otherlv_0= 'add' ( (lv_enumEntry_1_0= RULE_STRING ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? (otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )?
            {
            otherlv_0=(Token)match(input,54,FOLLOW_54_in_ruleAddEnum2933); 

                	newLeafNode(otherlv_0, grammarAccess.getAddEnumAccess().getAddKeyword_0());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1401:1: ( (lv_enumEntry_1_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1402:1: (lv_enumEntry_1_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1402:1: (lv_enumEntry_1_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1403:3: lv_enumEntry_1_0= RULE_STRING
            {
            lv_enumEntry_1_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAddEnum2950); 

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

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1419:2: ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )?
            int alt31=2;
            int LA31_0 = input.LA(1);

            if ( (LA31_0==RULE_WHOLE_NUM_STR) ) {
                alt31=1;
            }
            switch (alt31) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1420:1: (lv_ordinal_2_0= RULE_WHOLE_NUM_STR )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1420:1: (lv_ordinal_2_0= RULE_WHOLE_NUM_STR )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1421:3: lv_ordinal_2_0= RULE_WHOLE_NUM_STR
                    {
                    lv_ordinal_2_0=(Token)match(input,RULE_WHOLE_NUM_STR,FOLLOW_RULE_WHOLE_NUM_STR_in_ruleAddEnum2972); 

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

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1437:3: (otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )?
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==51) ) {
                alt32=1;
            }
            switch (alt32) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1437:5: otherlv_3= 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) )
                    {
                    otherlv_3=(Token)match(input,51,FOLLOW_51_in_ruleAddEnum2991); 

                        	newLeafNode(otherlv_3, grammarAccess.getAddEnumAccess().getEntryGuidKeyword_3_0());
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1441:1: ( (lv_entryGuid_4_0= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1442:1: (lv_entryGuid_4_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1442:1: (lv_entryGuid_4_0= RULE_STRING )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1443:3: lv_entryGuid_4_0= RULE_STRING
                    {
                    lv_entryGuid_4_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAddEnum3008); 

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1467:1: entryRuleRemoveEnum returns [EObject current=null] : iv_ruleRemoveEnum= ruleRemoveEnum EOF ;
    public final EObject entryRuleRemoveEnum() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRemoveEnum = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1468:2: (iv_ruleRemoveEnum= ruleRemoveEnum EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1469:2: iv_ruleRemoveEnum= ruleRemoveEnum EOF
            {
             newCompositeNode(grammarAccess.getRemoveEnumRule()); 
            pushFollow(FOLLOW_ruleRemoveEnum_in_entryRuleRemoveEnum3051);
            iv_ruleRemoveEnum=ruleRemoveEnum();

            state._fsp--;

             current =iv_ruleRemoveEnum; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleRemoveEnum3061); 

            }

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1476:1: ruleRemoveEnum returns [EObject current=null] : (otherlv_0= 'remove' ( (otherlv_1= RULE_STRING ) ) ) ;
    public final EObject ruleRemoveEnum() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1479:28: ( (otherlv_0= 'remove' ( (otherlv_1= RULE_STRING ) ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1480:1: (otherlv_0= 'remove' ( (otherlv_1= RULE_STRING ) ) )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1480:1: (otherlv_0= 'remove' ( (otherlv_1= RULE_STRING ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1480:3: otherlv_0= 'remove' ( (otherlv_1= RULE_STRING ) )
            {
            otherlv_0=(Token)match(input,55,FOLLOW_55_in_ruleRemoveEnum3098); 

                	newLeafNode(otherlv_0, grammarAccess.getRemoveEnumAccess().getRemoveKeyword_0());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1484:1: ( (otherlv_1= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1485:1: (otherlv_1= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1485:1: (otherlv_1= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1486:3: otherlv_1= RULE_STRING
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getRemoveEnumRule());
            	        }
                    
            otherlv_1=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleRemoveEnum3118); 

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1505:1: entryRuleXRelationType returns [EObject current=null] : iv_ruleXRelationType= ruleXRelationType EOF ;
    public final EObject entryRuleXRelationType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleXRelationType = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1506:2: (iv_ruleXRelationType= ruleXRelationType EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1507:2: iv_ruleXRelationType= ruleXRelationType EOF
            {
             newCompositeNode(grammarAccess.getXRelationTypeRule()); 
            pushFollow(FOLLOW_ruleXRelationType_in_entryRuleXRelationType3154);
            iv_ruleXRelationType=ruleXRelationType();

            state._fsp--;

             current =iv_ruleXRelationType; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleXRelationType3164); 

            }

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1514:1: ruleXRelationType returns [EObject current=null] : (otherlv_0= 'relationType' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= '{' (otherlv_3= 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) )? otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_HEX_INT ) ) otherlv_7= 'sideAName' ( (lv_sideAName_8_0= RULE_STRING ) ) otherlv_9= 'sideAArtifactType' ( (otherlv_10= RULE_STRING ) ) otherlv_11= 'sideBName' ( (lv_sideBName_12_0= RULE_STRING ) ) otherlv_13= 'sideBArtifactType' ( (otherlv_14= RULE_STRING ) ) otherlv_15= 'defaultOrderType' ( (lv_defaultOrderType_16_0= ruleRelationOrderType ) ) otherlv_17= 'multiplicity' ( (lv_multiplicity_18_0= ruleRelationMultiplicityEnum ) ) otherlv_19= '}' ) ;
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
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1517:28: ( (otherlv_0= 'relationType' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= '{' (otherlv_3= 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) )? otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_HEX_INT ) ) otherlv_7= 'sideAName' ( (lv_sideAName_8_0= RULE_STRING ) ) otherlv_9= 'sideAArtifactType' ( (otherlv_10= RULE_STRING ) ) otherlv_11= 'sideBName' ( (lv_sideBName_12_0= RULE_STRING ) ) otherlv_13= 'sideBArtifactType' ( (otherlv_14= RULE_STRING ) ) otherlv_15= 'defaultOrderType' ( (lv_defaultOrderType_16_0= ruleRelationOrderType ) ) otherlv_17= 'multiplicity' ( (lv_multiplicity_18_0= ruleRelationMultiplicityEnum ) ) otherlv_19= '}' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1518:1: (otherlv_0= 'relationType' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= '{' (otherlv_3= 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) )? otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_HEX_INT ) ) otherlv_7= 'sideAName' ( (lv_sideAName_8_0= RULE_STRING ) ) otherlv_9= 'sideAArtifactType' ( (otherlv_10= RULE_STRING ) ) otherlv_11= 'sideBName' ( (lv_sideBName_12_0= RULE_STRING ) ) otherlv_13= 'sideBArtifactType' ( (otherlv_14= RULE_STRING ) ) otherlv_15= 'defaultOrderType' ( (lv_defaultOrderType_16_0= ruleRelationOrderType ) ) otherlv_17= 'multiplicity' ( (lv_multiplicity_18_0= ruleRelationMultiplicityEnum ) ) otherlv_19= '}' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1518:1: (otherlv_0= 'relationType' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= '{' (otherlv_3= 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) )? otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_HEX_INT ) ) otherlv_7= 'sideAName' ( (lv_sideAName_8_0= RULE_STRING ) ) otherlv_9= 'sideAArtifactType' ( (otherlv_10= RULE_STRING ) ) otherlv_11= 'sideBName' ( (lv_sideBName_12_0= RULE_STRING ) ) otherlv_13= 'sideBArtifactType' ( (otherlv_14= RULE_STRING ) ) otherlv_15= 'defaultOrderType' ( (lv_defaultOrderType_16_0= ruleRelationOrderType ) ) otherlv_17= 'multiplicity' ( (lv_multiplicity_18_0= ruleRelationMultiplicityEnum ) ) otherlv_19= '}' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1518:3: otherlv_0= 'relationType' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= '{' (otherlv_3= 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) )? otherlv_5= 'uuid' ( (lv_uuid_6_0= RULE_HEX_INT ) ) otherlv_7= 'sideAName' ( (lv_sideAName_8_0= RULE_STRING ) ) otherlv_9= 'sideAArtifactType' ( (otherlv_10= RULE_STRING ) ) otherlv_11= 'sideBName' ( (lv_sideBName_12_0= RULE_STRING ) ) otherlv_13= 'sideBArtifactType' ( (otherlv_14= RULE_STRING ) ) otherlv_15= 'defaultOrderType' ( (lv_defaultOrderType_16_0= ruleRelationOrderType ) ) otherlv_17= 'multiplicity' ( (lv_multiplicity_18_0= ruleRelationMultiplicityEnum ) ) otherlv_19= '}'
            {
            otherlv_0=(Token)match(input,56,FOLLOW_56_in_ruleXRelationType3201); 

                	newLeafNode(otherlv_0, grammarAccess.getXRelationTypeAccess().getRelationTypeKeyword_0());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1522:1: ( (lv_name_1_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1523:1: (lv_name_1_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1523:1: (lv_name_1_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1524:3: lv_name_1_0= RULE_STRING
            {
            lv_name_1_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXRelationType3218); 

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

            otherlv_2=(Token)match(input,19,FOLLOW_19_in_ruleXRelationType3235); 

                	newLeafNode(otherlv_2, grammarAccess.getXRelationTypeAccess().getLeftCurlyBracketKeyword_2());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1544:1: (otherlv_3= 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) )?
            int alt33=2;
            int LA33_0 = input.LA(1);

            if ( (LA33_0==20) ) {
                alt33=1;
            }
            switch (alt33) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1544:3: otherlv_3= 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) )
                    {
                    otherlv_3=(Token)match(input,20,FOLLOW_20_in_ruleXRelationType3248); 

                        	newLeafNode(otherlv_3, grammarAccess.getXRelationTypeAccess().getGuidKeyword_3_0());
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1548:1: ( (lv_typeGuid_4_0= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1549:1: (lv_typeGuid_4_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1549:1: (lv_typeGuid_4_0= RULE_STRING )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1550:3: lv_typeGuid_4_0= RULE_STRING
                    {
                    lv_typeGuid_4_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXRelationType3265); 

                    			newLeafNode(lv_typeGuid_4_0, grammarAccess.getXRelationTypeAccess().getTypeGuidSTRINGTerminalRuleCall_3_1_0()); 
                    		

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


                    }
                    break;

            }

            otherlv_5=(Token)match(input,21,FOLLOW_21_in_ruleXRelationType3284); 

                	newLeafNode(otherlv_5, grammarAccess.getXRelationTypeAccess().getUuidKeyword_4());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1570:1: ( (lv_uuid_6_0= RULE_HEX_INT ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1571:1: (lv_uuid_6_0= RULE_HEX_INT )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1571:1: (lv_uuid_6_0= RULE_HEX_INT )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1572:3: lv_uuid_6_0= RULE_HEX_INT
            {
            lv_uuid_6_0=(Token)match(input,RULE_HEX_INT,FOLLOW_RULE_HEX_INT_in_ruleXRelationType3301); 

            			newLeafNode(lv_uuid_6_0, grammarAccess.getXRelationTypeAccess().getUuidHEX_INTTerminalRuleCall_5_0()); 
            		

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

            otherlv_7=(Token)match(input,57,FOLLOW_57_in_ruleXRelationType3318); 

                	newLeafNode(otherlv_7, grammarAccess.getXRelationTypeAccess().getSideANameKeyword_6());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1592:1: ( (lv_sideAName_8_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1593:1: (lv_sideAName_8_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1593:1: (lv_sideAName_8_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1594:3: lv_sideAName_8_0= RULE_STRING
            {
            lv_sideAName_8_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXRelationType3335); 

            			newLeafNode(lv_sideAName_8_0, grammarAccess.getXRelationTypeAccess().getSideANameSTRINGTerminalRuleCall_7_0()); 
            		

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

            otherlv_9=(Token)match(input,58,FOLLOW_58_in_ruleXRelationType3352); 

                	newLeafNode(otherlv_9, grammarAccess.getXRelationTypeAccess().getSideAArtifactTypeKeyword_8());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1614:1: ( (otherlv_10= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1615:1: (otherlv_10= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1615:1: (otherlv_10= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1616:3: otherlv_10= RULE_STRING
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getXRelationTypeRule());
            	        }
                    
            otherlv_10=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXRelationType3372); 

            		newLeafNode(otherlv_10, grammarAccess.getXRelationTypeAccess().getSideAArtifactTypeXArtifactTypeCrossReference_9_0()); 
            	

            }


            }

            otherlv_11=(Token)match(input,59,FOLLOW_59_in_ruleXRelationType3384); 

                	newLeafNode(otherlv_11, grammarAccess.getXRelationTypeAccess().getSideBNameKeyword_10());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1631:1: ( (lv_sideBName_12_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1632:1: (lv_sideBName_12_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1632:1: (lv_sideBName_12_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1633:3: lv_sideBName_12_0= RULE_STRING
            {
            lv_sideBName_12_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXRelationType3401); 

            			newLeafNode(lv_sideBName_12_0, grammarAccess.getXRelationTypeAccess().getSideBNameSTRINGTerminalRuleCall_11_0()); 
            		

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

            otherlv_13=(Token)match(input,60,FOLLOW_60_in_ruleXRelationType3418); 

                	newLeafNode(otherlv_13, grammarAccess.getXRelationTypeAccess().getSideBArtifactTypeKeyword_12());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1653:1: ( (otherlv_14= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1654:1: (otherlv_14= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1654:1: (otherlv_14= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1655:3: otherlv_14= RULE_STRING
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getXRelationTypeRule());
            	        }
                    
            otherlv_14=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXRelationType3438); 

            		newLeafNode(otherlv_14, grammarAccess.getXRelationTypeAccess().getSideBArtifactTypeXArtifactTypeCrossReference_13_0()); 
            	

            }


            }

            otherlv_15=(Token)match(input,61,FOLLOW_61_in_ruleXRelationType3450); 

                	newLeafNode(otherlv_15, grammarAccess.getXRelationTypeAccess().getDefaultOrderTypeKeyword_14());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1670:1: ( (lv_defaultOrderType_16_0= ruleRelationOrderType ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1671:1: (lv_defaultOrderType_16_0= ruleRelationOrderType )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1671:1: (lv_defaultOrderType_16_0= ruleRelationOrderType )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1672:3: lv_defaultOrderType_16_0= ruleRelationOrderType
            {
             
            	        newCompositeNode(grammarAccess.getXRelationTypeAccess().getDefaultOrderTypeRelationOrderTypeParserRuleCall_15_0()); 
            	    
            pushFollow(FOLLOW_ruleRelationOrderType_in_ruleXRelationType3471);
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

            otherlv_17=(Token)match(input,62,FOLLOW_62_in_ruleXRelationType3483); 

                	newLeafNode(otherlv_17, grammarAccess.getXRelationTypeAccess().getMultiplicityKeyword_16());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1692:1: ( (lv_multiplicity_18_0= ruleRelationMultiplicityEnum ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1693:1: (lv_multiplicity_18_0= ruleRelationMultiplicityEnum )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1693:1: (lv_multiplicity_18_0= ruleRelationMultiplicityEnum )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1694:3: lv_multiplicity_18_0= ruleRelationMultiplicityEnum
            {
             
            	        newCompositeNode(grammarAccess.getXRelationTypeAccess().getMultiplicityRelationMultiplicityEnumEnumRuleCall_17_0()); 
            	    
            pushFollow(FOLLOW_ruleRelationMultiplicityEnum_in_ruleXRelationType3504);
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

            otherlv_19=(Token)match(input,22,FOLLOW_22_in_ruleXRelationType3516); 

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1722:1: entryRuleRelationOrderType returns [String current=null] : iv_ruleRelationOrderType= ruleRelationOrderType EOF ;
    public final String entryRuleRelationOrderType() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleRelationOrderType = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1723:2: (iv_ruleRelationOrderType= ruleRelationOrderType EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1724:2: iv_ruleRelationOrderType= ruleRelationOrderType EOF
            {
             newCompositeNode(grammarAccess.getRelationOrderTypeRule()); 
            pushFollow(FOLLOW_ruleRelationOrderType_in_entryRuleRelationOrderType3553);
            iv_ruleRelationOrderType=ruleRelationOrderType();

            state._fsp--;

             current =iv_ruleRelationOrderType.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleRelationOrderType3564); 

            }

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1731:1: ruleRelationOrderType returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'Lexicographical_Ascending' | kw= 'Lexicographical_Descending' | kw= 'Unordered' | this_ID_3= RULE_ID ) ;
    public final AntlrDatatypeRuleToken ruleRelationOrderType() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        Token this_ID_3=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1734:28: ( (kw= 'Lexicographical_Ascending' | kw= 'Lexicographical_Descending' | kw= 'Unordered' | this_ID_3= RULE_ID ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1735:1: (kw= 'Lexicographical_Ascending' | kw= 'Lexicographical_Descending' | kw= 'Unordered' | this_ID_3= RULE_ID )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1735:1: (kw= 'Lexicographical_Ascending' | kw= 'Lexicographical_Descending' | kw= 'Unordered' | this_ID_3= RULE_ID )
            int alt34=4;
            switch ( input.LA(1) ) {
            case 63:
                {
                alt34=1;
                }
                break;
            case 64:
                {
                alt34=2;
                }
                break;
            case 65:
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
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1736:2: kw= 'Lexicographical_Ascending'
                    {
                    kw=(Token)match(input,63,FOLLOW_63_in_ruleRelationOrderType3602); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRelationOrderTypeAccess().getLexicographical_AscendingKeyword_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1743:2: kw= 'Lexicographical_Descending'
                    {
                    kw=(Token)match(input,64,FOLLOW_64_in_ruleRelationOrderType3621); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRelationOrderTypeAccess().getLexicographical_DescendingKeyword_1()); 
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1750:2: kw= 'Unordered'
                    {
                    kw=(Token)match(input,65,FOLLOW_65_in_ruleRelationOrderType3640); 

                            current.merge(kw);
                            newLeafNode(kw, grammarAccess.getRelationOrderTypeAccess().getUnorderedKeyword_2()); 
                        

                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1756:10: this_ID_3= RULE_ID
                    {
                    this_ID_3=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleRelationOrderType3661); 

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1773:1: entryRuleCondition returns [EObject current=null] : iv_ruleCondition= ruleCondition EOF ;
    public final EObject entryRuleCondition() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleCondition = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1774:2: (iv_ruleCondition= ruleCondition EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1775:2: iv_ruleCondition= ruleCondition EOF
            {
             newCompositeNode(grammarAccess.getConditionRule()); 
            pushFollow(FOLLOW_ruleCondition_in_entryRuleCondition3708);
            iv_ruleCondition=ruleCondition();

            state._fsp--;

             current =iv_ruleCondition; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleCondition3718); 

            }

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1782:1: ruleCondition returns [EObject current=null] : (this_SimpleCondition_0= ruleSimpleCondition | this_CompoundCondition_1= ruleCompoundCondition ) ;
    public final EObject ruleCondition() throws RecognitionException {
        EObject current = null;

        EObject this_SimpleCondition_0 = null;

        EObject this_CompoundCondition_1 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1785:28: ( (this_SimpleCondition_0= ruleSimpleCondition | this_CompoundCondition_1= ruleCompoundCondition ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1786:1: (this_SimpleCondition_0= ruleSimpleCondition | this_CompoundCondition_1= ruleCompoundCondition )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1786:1: (this_SimpleCondition_0= ruleSimpleCondition | this_CompoundCondition_1= ruleCompoundCondition )
            int alt35=2;
            int LA35_0 = input.LA(1);

            if ( (LA35_0==24||(LA35_0>=84 && LA35_0<=86)) ) {
                alt35=1;
            }
            else if ( (LA35_0==66) ) {
                alt35=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 35, 0, input);

                throw nvae;
            }
            switch (alt35) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1787:5: this_SimpleCondition_0= ruleSimpleCondition
                    {
                     
                            newCompositeNode(grammarAccess.getConditionAccess().getSimpleConditionParserRuleCall_0()); 
                        
                    pushFollow(FOLLOW_ruleSimpleCondition_in_ruleCondition3765);
                    this_SimpleCondition_0=ruleSimpleCondition();

                    state._fsp--;

                     
                            current = this_SimpleCondition_0; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1797:5: this_CompoundCondition_1= ruleCompoundCondition
                    {
                     
                            newCompositeNode(grammarAccess.getConditionAccess().getCompoundConditionParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_ruleCompoundCondition_in_ruleCondition3792);
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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1813:1: entryRuleSimpleCondition returns [EObject current=null] : iv_ruleSimpleCondition= ruleSimpleCondition EOF ;
    public final EObject entryRuleSimpleCondition() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleSimpleCondition = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1814:2: (iv_ruleSimpleCondition= ruleSimpleCondition EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1815:2: iv_ruleSimpleCondition= ruleSimpleCondition EOF
            {
             newCompositeNode(grammarAccess.getSimpleConditionRule()); 
            pushFollow(FOLLOW_ruleSimpleCondition_in_entryRuleSimpleCondition3827);
            iv_ruleSimpleCondition=ruleSimpleCondition();

            state._fsp--;

             current =iv_ruleSimpleCondition; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleSimpleCondition3837); 

            }

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1822:1: ruleSimpleCondition returns [EObject current=null] : ( ( (lv_field_0_0= ruleMatchField ) ) ( (lv_op_1_0= ruleCompareOp ) ) ( (lv_expression_2_0= RULE_STRING ) ) ) ;
    public final EObject ruleSimpleCondition() throws RecognitionException {
        EObject current = null;

        Token lv_expression_2_0=null;
        Enumerator lv_field_0_0 = null;

        Enumerator lv_op_1_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1825:28: ( ( ( (lv_field_0_0= ruleMatchField ) ) ( (lv_op_1_0= ruleCompareOp ) ) ( (lv_expression_2_0= RULE_STRING ) ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1826:1: ( ( (lv_field_0_0= ruleMatchField ) ) ( (lv_op_1_0= ruleCompareOp ) ) ( (lv_expression_2_0= RULE_STRING ) ) )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1826:1: ( ( (lv_field_0_0= ruleMatchField ) ) ( (lv_op_1_0= ruleCompareOp ) ) ( (lv_expression_2_0= RULE_STRING ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1826:2: ( (lv_field_0_0= ruleMatchField ) ) ( (lv_op_1_0= ruleCompareOp ) ) ( (lv_expression_2_0= RULE_STRING ) )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1826:2: ( (lv_field_0_0= ruleMatchField ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1827:1: (lv_field_0_0= ruleMatchField )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1827:1: (lv_field_0_0= ruleMatchField )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1828:3: lv_field_0_0= ruleMatchField
            {
             
            	        newCompositeNode(grammarAccess.getSimpleConditionAccess().getFieldMatchFieldEnumRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_ruleMatchField_in_ruleSimpleCondition3883);
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

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1844:2: ( (lv_op_1_0= ruleCompareOp ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1845:1: (lv_op_1_0= ruleCompareOp )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1845:1: (lv_op_1_0= ruleCompareOp )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1846:3: lv_op_1_0= ruleCompareOp
            {
             
            	        newCompositeNode(grammarAccess.getSimpleConditionAccess().getOpCompareOpEnumRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_ruleCompareOp_in_ruleSimpleCondition3904);
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

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1862:2: ( (lv_expression_2_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1863:1: (lv_expression_2_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1863:1: (lv_expression_2_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1864:3: lv_expression_2_0= RULE_STRING
            {
            lv_expression_2_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleSimpleCondition3921); 

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1888:1: entryRuleCompoundCondition returns [EObject current=null] : iv_ruleCompoundCondition= ruleCompoundCondition EOF ;
    public final EObject entryRuleCompoundCondition() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleCompoundCondition = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1889:2: (iv_ruleCompoundCondition= ruleCompoundCondition EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1890:2: iv_ruleCompoundCondition= ruleCompoundCondition EOF
            {
             newCompositeNode(grammarAccess.getCompoundConditionRule()); 
            pushFollow(FOLLOW_ruleCompoundCondition_in_entryRuleCompoundCondition3962);
            iv_ruleCompoundCondition=ruleCompoundCondition();

            state._fsp--;

             current =iv_ruleCompoundCondition; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleCompoundCondition3972); 

            }

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1897:1: ruleCompoundCondition returns [EObject current=null] : (otherlv_0= '(' ( (lv_conditions_1_0= ruleSimpleCondition ) ) ( ( (lv_operators_2_0= ruleXLogicOperator ) ) ( (lv_conditions_3_0= ruleSimpleCondition ) ) )+ otherlv_4= ')' ) ;
    public final EObject ruleCompoundCondition() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_4=null;
        EObject lv_conditions_1_0 = null;

        Enumerator lv_operators_2_0 = null;

        EObject lv_conditions_3_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1900:28: ( (otherlv_0= '(' ( (lv_conditions_1_0= ruleSimpleCondition ) ) ( ( (lv_operators_2_0= ruleXLogicOperator ) ) ( (lv_conditions_3_0= ruleSimpleCondition ) ) )+ otherlv_4= ')' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1901:1: (otherlv_0= '(' ( (lv_conditions_1_0= ruleSimpleCondition ) ) ( ( (lv_operators_2_0= ruleXLogicOperator ) ) ( (lv_conditions_3_0= ruleSimpleCondition ) ) )+ otherlv_4= ')' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1901:1: (otherlv_0= '(' ( (lv_conditions_1_0= ruleSimpleCondition ) ) ( ( (lv_operators_2_0= ruleXLogicOperator ) ) ( (lv_conditions_3_0= ruleSimpleCondition ) ) )+ otherlv_4= ')' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1901:3: otherlv_0= '(' ( (lv_conditions_1_0= ruleSimpleCondition ) ) ( ( (lv_operators_2_0= ruleXLogicOperator ) ) ( (lv_conditions_3_0= ruleSimpleCondition ) ) )+ otherlv_4= ')'
            {
            otherlv_0=(Token)match(input,66,FOLLOW_66_in_ruleCompoundCondition4009); 

                	newLeafNode(otherlv_0, grammarAccess.getCompoundConditionAccess().getLeftParenthesisKeyword_0());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1905:1: ( (lv_conditions_1_0= ruleSimpleCondition ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1906:1: (lv_conditions_1_0= ruleSimpleCondition )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1906:1: (lv_conditions_1_0= ruleSimpleCondition )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1907:3: lv_conditions_1_0= ruleSimpleCondition
            {
             
            	        newCompositeNode(grammarAccess.getCompoundConditionAccess().getConditionsSimpleConditionParserRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_ruleSimpleCondition_in_ruleCompoundCondition4030);
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

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1923:2: ( ( (lv_operators_2_0= ruleXLogicOperator ) ) ( (lv_conditions_3_0= ruleSimpleCondition ) ) )+
            int cnt36=0;
            loop36:
            do {
                int alt36=2;
                int LA36_0 = input.LA(1);

                if ( ((LA36_0>=82 && LA36_0<=83)) ) {
                    alt36=1;
                }


                switch (alt36) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1923:3: ( (lv_operators_2_0= ruleXLogicOperator ) ) ( (lv_conditions_3_0= ruleSimpleCondition ) )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1923:3: ( (lv_operators_2_0= ruleXLogicOperator ) )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1924:1: (lv_operators_2_0= ruleXLogicOperator )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1924:1: (lv_operators_2_0= ruleXLogicOperator )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1925:3: lv_operators_2_0= ruleXLogicOperator
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getCompoundConditionAccess().getOperatorsXLogicOperatorEnumRuleCall_2_0_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleXLogicOperator_in_ruleCompoundCondition4052);
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

            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1941:2: ( (lv_conditions_3_0= ruleSimpleCondition ) )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1942:1: (lv_conditions_3_0= ruleSimpleCondition )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1942:1: (lv_conditions_3_0= ruleSimpleCondition )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1943:3: lv_conditions_3_0= ruleSimpleCondition
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getCompoundConditionAccess().getConditionsSimpleConditionParserRuleCall_2_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleSimpleCondition_in_ruleCompoundCondition4073);
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
            	    if ( cnt36 >= 1 ) break loop36;
                        EarlyExitException eee =
                            new EarlyExitException(36, input);
                        throw eee;
                }
                cnt36++;
            } while (true);

            otherlv_4=(Token)match(input,67,FOLLOW_67_in_ruleCompoundCondition4087); 

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1971:1: entryRuleXArtifactMatcher returns [EObject current=null] : iv_ruleXArtifactMatcher= ruleXArtifactMatcher EOF ;
    public final EObject entryRuleXArtifactMatcher() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleXArtifactMatcher = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1972:2: (iv_ruleXArtifactMatcher= ruleXArtifactMatcher EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1973:2: iv_ruleXArtifactMatcher= ruleXArtifactMatcher EOF
            {
             newCompositeNode(grammarAccess.getXArtifactMatcherRule()); 
            pushFollow(FOLLOW_ruleXArtifactMatcher_in_entryRuleXArtifactMatcher4123);
            iv_ruleXArtifactMatcher=ruleXArtifactMatcher();

            state._fsp--;

             current =iv_ruleXArtifactMatcher; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleXArtifactMatcher4133); 

            }

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1980:1: ruleXArtifactMatcher returns [EObject current=null] : (otherlv_0= 'artifactMatcher' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= 'where' ( (lv_conditions_3_0= ruleCondition ) ) ( ( (lv_operators_4_0= ruleXLogicOperator ) ) ( (lv_conditions_5_0= ruleCondition ) ) )* otherlv_6= ';' ) ;
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
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1983:28: ( (otherlv_0= 'artifactMatcher' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= 'where' ( (lv_conditions_3_0= ruleCondition ) ) ( ( (lv_operators_4_0= ruleXLogicOperator ) ) ( (lv_conditions_5_0= ruleCondition ) ) )* otherlv_6= ';' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1984:1: (otherlv_0= 'artifactMatcher' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= 'where' ( (lv_conditions_3_0= ruleCondition ) ) ( ( (lv_operators_4_0= ruleXLogicOperator ) ) ( (lv_conditions_5_0= ruleCondition ) ) )* otherlv_6= ';' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1984:1: (otherlv_0= 'artifactMatcher' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= 'where' ( (lv_conditions_3_0= ruleCondition ) ) ( ( (lv_operators_4_0= ruleXLogicOperator ) ) ( (lv_conditions_5_0= ruleCondition ) ) )* otherlv_6= ';' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1984:3: otherlv_0= 'artifactMatcher' ( (lv_name_1_0= RULE_STRING ) ) otherlv_2= 'where' ( (lv_conditions_3_0= ruleCondition ) ) ( ( (lv_operators_4_0= ruleXLogicOperator ) ) ( (lv_conditions_5_0= ruleCondition ) ) )* otherlv_6= ';'
            {
            otherlv_0=(Token)match(input,68,FOLLOW_68_in_ruleXArtifactMatcher4170); 

                	newLeafNode(otherlv_0, grammarAccess.getXArtifactMatcherAccess().getArtifactMatcherKeyword_0());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1988:1: ( (lv_name_1_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1989:1: (lv_name_1_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1989:1: (lv_name_1_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1990:3: lv_name_1_0= RULE_STRING
            {
            lv_name_1_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXArtifactMatcher4187); 

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

            otherlv_2=(Token)match(input,69,FOLLOW_69_in_ruleXArtifactMatcher4204); 

                	newLeafNode(otherlv_2, grammarAccess.getXArtifactMatcherAccess().getWhereKeyword_2());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2010:1: ( (lv_conditions_3_0= ruleCondition ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2011:1: (lv_conditions_3_0= ruleCondition )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2011:1: (lv_conditions_3_0= ruleCondition )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2012:3: lv_conditions_3_0= ruleCondition
            {
             
            	        newCompositeNode(grammarAccess.getXArtifactMatcherAccess().getConditionsConditionParserRuleCall_3_0()); 
            	    
            pushFollow(FOLLOW_ruleCondition_in_ruleXArtifactMatcher4225);
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

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2028:2: ( ( (lv_operators_4_0= ruleXLogicOperator ) ) ( (lv_conditions_5_0= ruleCondition ) ) )*
            loop37:
            do {
                int alt37=2;
                int LA37_0 = input.LA(1);

                if ( ((LA37_0>=82 && LA37_0<=83)) ) {
                    alt37=1;
                }


                switch (alt37) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2028:3: ( (lv_operators_4_0= ruleXLogicOperator ) ) ( (lv_conditions_5_0= ruleCondition ) )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2028:3: ( (lv_operators_4_0= ruleXLogicOperator ) )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2029:1: (lv_operators_4_0= ruleXLogicOperator )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2029:1: (lv_operators_4_0= ruleXLogicOperator )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2030:3: lv_operators_4_0= ruleXLogicOperator
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getXArtifactMatcherAccess().getOperatorsXLogicOperatorEnumRuleCall_4_0_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleXLogicOperator_in_ruleXArtifactMatcher4247);
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

            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2046:2: ( (lv_conditions_5_0= ruleCondition ) )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2047:1: (lv_conditions_5_0= ruleCondition )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2047:1: (lv_conditions_5_0= ruleCondition )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2048:3: lv_conditions_5_0= ruleCondition
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getXArtifactMatcherAccess().getConditionsConditionParserRuleCall_4_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleCondition_in_ruleXArtifactMatcher4268);
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
            	    break loop37;
                }
            } while (true);

            otherlv_6=(Token)match(input,70,FOLLOW_70_in_ruleXArtifactMatcher4282); 

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2076:1: entryRuleAccessContext returns [EObject current=null] : iv_ruleAccessContext= ruleAccessContext EOF ;
    public final EObject entryRuleAccessContext() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAccessContext = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2077:2: (iv_ruleAccessContext= ruleAccessContext EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2078:2: iv_ruleAccessContext= ruleAccessContext EOF
            {
             newCompositeNode(grammarAccess.getAccessContextRule()); 
            pushFollow(FOLLOW_ruleAccessContext_in_entryRuleAccessContext4318);
            iv_ruleAccessContext=ruleAccessContext();

            state._fsp--;

             current =iv_ruleAccessContext; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAccessContext4328); 

            }

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2085:1: ruleAccessContext returns [EObject current=null] : (otherlv_0= 'accessContext' ( (lv_name_1_0= RULE_STRING ) ) (otherlv_2= 'extends' ( (otherlv_3= RULE_STRING ) ) )? otherlv_4= '{' otherlv_5= 'guid' ( (lv_guid_6_0= RULE_STRING ) ) otherlv_7= ';' ( ( (lv_accessRules_8_0= ruleObjectRestriction ) ) | ( (lv_hierarchyRestrictions_9_0= ruleHierarchyRestriction ) ) )+ otherlv_10= '}' ) ;
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
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2088:28: ( (otherlv_0= 'accessContext' ( (lv_name_1_0= RULE_STRING ) ) (otherlv_2= 'extends' ( (otherlv_3= RULE_STRING ) ) )? otherlv_4= '{' otherlv_5= 'guid' ( (lv_guid_6_0= RULE_STRING ) ) otherlv_7= ';' ( ( (lv_accessRules_8_0= ruleObjectRestriction ) ) | ( (lv_hierarchyRestrictions_9_0= ruleHierarchyRestriction ) ) )+ otherlv_10= '}' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2089:1: (otherlv_0= 'accessContext' ( (lv_name_1_0= RULE_STRING ) ) (otherlv_2= 'extends' ( (otherlv_3= RULE_STRING ) ) )? otherlv_4= '{' otherlv_5= 'guid' ( (lv_guid_6_0= RULE_STRING ) ) otherlv_7= ';' ( ( (lv_accessRules_8_0= ruleObjectRestriction ) ) | ( (lv_hierarchyRestrictions_9_0= ruleHierarchyRestriction ) ) )+ otherlv_10= '}' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2089:1: (otherlv_0= 'accessContext' ( (lv_name_1_0= RULE_STRING ) ) (otherlv_2= 'extends' ( (otherlv_3= RULE_STRING ) ) )? otherlv_4= '{' otherlv_5= 'guid' ( (lv_guid_6_0= RULE_STRING ) ) otherlv_7= ';' ( ( (lv_accessRules_8_0= ruleObjectRestriction ) ) | ( (lv_hierarchyRestrictions_9_0= ruleHierarchyRestriction ) ) )+ otherlv_10= '}' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2089:3: otherlv_0= 'accessContext' ( (lv_name_1_0= RULE_STRING ) ) (otherlv_2= 'extends' ( (otherlv_3= RULE_STRING ) ) )? otherlv_4= '{' otherlv_5= 'guid' ( (lv_guid_6_0= RULE_STRING ) ) otherlv_7= ';' ( ( (lv_accessRules_8_0= ruleObjectRestriction ) ) | ( (lv_hierarchyRestrictions_9_0= ruleHierarchyRestriction ) ) )+ otherlv_10= '}'
            {
            otherlv_0=(Token)match(input,71,FOLLOW_71_in_ruleAccessContext4365); 

                	newLeafNode(otherlv_0, grammarAccess.getAccessContextAccess().getAccessContextKeyword_0());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2093:1: ( (lv_name_1_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2094:1: (lv_name_1_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2094:1: (lv_name_1_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2095:3: lv_name_1_0= RULE_STRING
            {
            lv_name_1_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAccessContext4382); 

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

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2111:2: (otherlv_2= 'extends' ( (otherlv_3= RULE_STRING ) ) )?
            int alt38=2;
            int LA38_0 = input.LA(1);

            if ( (LA38_0==17) ) {
                alt38=1;
            }
            switch (alt38) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2111:4: otherlv_2= 'extends' ( (otherlv_3= RULE_STRING ) )
                    {
                    otherlv_2=(Token)match(input,17,FOLLOW_17_in_ruleAccessContext4400); 

                        	newLeafNode(otherlv_2, grammarAccess.getAccessContextAccess().getExtendsKeyword_2_0());
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2115:1: ( (otherlv_3= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2116:1: (otherlv_3= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2116:1: (otherlv_3= RULE_STRING )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2117:3: otherlv_3= RULE_STRING
                    {

                    			if (current==null) {
                    	            current = createModelElement(grammarAccess.getAccessContextRule());
                    	        }
                            
                    otherlv_3=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAccessContext4420); 

                    		newLeafNode(otherlv_3, grammarAccess.getAccessContextAccess().getSuperAccessContextsAccessContextCrossReference_2_1_0()); 
                    	

                    }


                    }


                    }
                    break;

            }

            otherlv_4=(Token)match(input,19,FOLLOW_19_in_ruleAccessContext4434); 

                	newLeafNode(otherlv_4, grammarAccess.getAccessContextAccess().getLeftCurlyBracketKeyword_3());
                
            otherlv_5=(Token)match(input,20,FOLLOW_20_in_ruleAccessContext4446); 

                	newLeafNode(otherlv_5, grammarAccess.getAccessContextAccess().getGuidKeyword_4());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2136:1: ( (lv_guid_6_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2137:1: (lv_guid_6_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2137:1: (lv_guid_6_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2138:3: lv_guid_6_0= RULE_STRING
            {
            lv_guid_6_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAccessContext4463); 

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

            otherlv_7=(Token)match(input,70,FOLLOW_70_in_ruleAccessContext4480); 

                	newLeafNode(otherlv_7, grammarAccess.getAccessContextAccess().getSemicolonKeyword_6());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2158:1: ( ( (lv_accessRules_8_0= ruleObjectRestriction ) ) | ( (lv_hierarchyRestrictions_9_0= ruleHierarchyRestriction ) ) )+
            int cnt39=0;
            loop39:
            do {
                int alt39=3;
                int LA39_0 = input.LA(1);

                if ( ((LA39_0>=87 && LA39_0<=88)) ) {
                    alt39=1;
                }
                else if ( (LA39_0==72) ) {
                    alt39=2;
                }


                switch (alt39) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2158:2: ( (lv_accessRules_8_0= ruleObjectRestriction ) )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2158:2: ( (lv_accessRules_8_0= ruleObjectRestriction ) )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2159:1: (lv_accessRules_8_0= ruleObjectRestriction )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2159:1: (lv_accessRules_8_0= ruleObjectRestriction )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2160:3: lv_accessRules_8_0= ruleObjectRestriction
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getAccessContextAccess().getAccessRulesObjectRestrictionParserRuleCall_7_0_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleObjectRestriction_in_ruleAccessContext4502);
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
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2177:6: ( (lv_hierarchyRestrictions_9_0= ruleHierarchyRestriction ) )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2177:6: ( (lv_hierarchyRestrictions_9_0= ruleHierarchyRestriction ) )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2178:1: (lv_hierarchyRestrictions_9_0= ruleHierarchyRestriction )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2178:1: (lv_hierarchyRestrictions_9_0= ruleHierarchyRestriction )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2179:3: lv_hierarchyRestrictions_9_0= ruleHierarchyRestriction
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getAccessContextAccess().getHierarchyRestrictionsHierarchyRestrictionParserRuleCall_7_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleHierarchyRestriction_in_ruleAccessContext4529);
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
            	    if ( cnt39 >= 1 ) break loop39;
                        EarlyExitException eee =
                            new EarlyExitException(39, input);
                        throw eee;
                }
                cnt39++;
            } while (true);

            otherlv_10=(Token)match(input,22,FOLLOW_22_in_ruleAccessContext4543); 

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2207:1: entryRuleHierarchyRestriction returns [EObject current=null] : iv_ruleHierarchyRestriction= ruleHierarchyRestriction EOF ;
    public final EObject entryRuleHierarchyRestriction() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleHierarchyRestriction = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2208:2: (iv_ruleHierarchyRestriction= ruleHierarchyRestriction EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2209:2: iv_ruleHierarchyRestriction= ruleHierarchyRestriction EOF
            {
             newCompositeNode(grammarAccess.getHierarchyRestrictionRule()); 
            pushFollow(FOLLOW_ruleHierarchyRestriction_in_entryRuleHierarchyRestriction4579);
            iv_ruleHierarchyRestriction=ruleHierarchyRestriction();

            state._fsp--;

             current =iv_ruleHierarchyRestriction; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleHierarchyRestriction4589); 

            }

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2216:1: ruleHierarchyRestriction returns [EObject current=null] : (otherlv_0= 'childrenOf' ( (otherlv_1= RULE_STRING ) ) otherlv_2= '{' ( (lv_accessRules_3_0= ruleObjectRestriction ) )+ otherlv_4= '}' ) ;
    public final EObject ruleHierarchyRestriction() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;
        Token otherlv_2=null;
        Token otherlv_4=null;
        EObject lv_accessRules_3_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2219:28: ( (otherlv_0= 'childrenOf' ( (otherlv_1= RULE_STRING ) ) otherlv_2= '{' ( (lv_accessRules_3_0= ruleObjectRestriction ) )+ otherlv_4= '}' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2220:1: (otherlv_0= 'childrenOf' ( (otherlv_1= RULE_STRING ) ) otherlv_2= '{' ( (lv_accessRules_3_0= ruleObjectRestriction ) )+ otherlv_4= '}' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2220:1: (otherlv_0= 'childrenOf' ( (otherlv_1= RULE_STRING ) ) otherlv_2= '{' ( (lv_accessRules_3_0= ruleObjectRestriction ) )+ otherlv_4= '}' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2220:3: otherlv_0= 'childrenOf' ( (otherlv_1= RULE_STRING ) ) otherlv_2= '{' ( (lv_accessRules_3_0= ruleObjectRestriction ) )+ otherlv_4= '}'
            {
            otherlv_0=(Token)match(input,72,FOLLOW_72_in_ruleHierarchyRestriction4626); 

                	newLeafNode(otherlv_0, grammarAccess.getHierarchyRestrictionAccess().getChildrenOfKeyword_0());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2224:1: ( (otherlv_1= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2225:1: (otherlv_1= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2225:1: (otherlv_1= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2226:3: otherlv_1= RULE_STRING
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getHierarchyRestrictionRule());
            	        }
                    
            otherlv_1=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleHierarchyRestriction4646); 

            		newLeafNode(otherlv_1, grammarAccess.getHierarchyRestrictionAccess().getArtifactMatcherRefXArtifactMatcherCrossReference_1_0()); 
            	

            }


            }

            otherlv_2=(Token)match(input,19,FOLLOW_19_in_ruleHierarchyRestriction4658); 

                	newLeafNode(otherlv_2, grammarAccess.getHierarchyRestrictionAccess().getLeftCurlyBracketKeyword_2());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2241:1: ( (lv_accessRules_3_0= ruleObjectRestriction ) )+
            int cnt40=0;
            loop40:
            do {
                int alt40=2;
                int LA40_0 = input.LA(1);

                if ( ((LA40_0>=87 && LA40_0<=88)) ) {
                    alt40=1;
                }


                switch (alt40) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2242:1: (lv_accessRules_3_0= ruleObjectRestriction )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2242:1: (lv_accessRules_3_0= ruleObjectRestriction )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2243:3: lv_accessRules_3_0= ruleObjectRestriction
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getHierarchyRestrictionAccess().getAccessRulesObjectRestrictionParserRuleCall_3_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleObjectRestriction_in_ruleHierarchyRestriction4679);
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
            	    if ( cnt40 >= 1 ) break loop40;
                        EarlyExitException eee =
                            new EarlyExitException(40, input);
                        throw eee;
                }
                cnt40++;
            } while (true);

            otherlv_4=(Token)match(input,22,FOLLOW_22_in_ruleHierarchyRestriction4692); 

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2271:1: entryRuleRelationTypeArtifactTypePredicate returns [EObject current=null] : iv_ruleRelationTypeArtifactTypePredicate= ruleRelationTypeArtifactTypePredicate EOF ;
    public final EObject entryRuleRelationTypeArtifactTypePredicate() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRelationTypeArtifactTypePredicate = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2272:2: (iv_ruleRelationTypeArtifactTypePredicate= ruleRelationTypeArtifactTypePredicate EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2273:2: iv_ruleRelationTypeArtifactTypePredicate= ruleRelationTypeArtifactTypePredicate EOF
            {
             newCompositeNode(grammarAccess.getRelationTypeArtifactTypePredicateRule()); 
            pushFollow(FOLLOW_ruleRelationTypeArtifactTypePredicate_in_entryRuleRelationTypeArtifactTypePredicate4728);
            iv_ruleRelationTypeArtifactTypePredicate=ruleRelationTypeArtifactTypePredicate();

            state._fsp--;

             current =iv_ruleRelationTypeArtifactTypePredicate; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleRelationTypeArtifactTypePredicate4738); 

            }

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2280:1: ruleRelationTypeArtifactTypePredicate returns [EObject current=null] : (otherlv_0= 'artifactType' ( (otherlv_1= RULE_STRING ) ) ) ;
    public final EObject ruleRelationTypeArtifactTypePredicate() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2283:28: ( (otherlv_0= 'artifactType' ( (otherlv_1= RULE_STRING ) ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2284:1: (otherlv_0= 'artifactType' ( (otherlv_1= RULE_STRING ) ) )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2284:1: (otherlv_0= 'artifactType' ( (otherlv_1= RULE_STRING ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2284:3: otherlv_0= 'artifactType' ( (otherlv_1= RULE_STRING ) )
            {
            otherlv_0=(Token)match(input,16,FOLLOW_16_in_ruleRelationTypeArtifactTypePredicate4775); 

                	newLeafNode(otherlv_0, grammarAccess.getRelationTypeArtifactTypePredicateAccess().getArtifactTypeKeyword_0());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2288:1: ( (otherlv_1= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2289:1: (otherlv_1= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2289:1: (otherlv_1= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2290:3: otherlv_1= RULE_STRING
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getRelationTypeArtifactTypePredicateRule());
            	        }
                    
            otherlv_1=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleRelationTypeArtifactTypePredicate4795); 

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2309:1: entryRuleRelationTypeArtifactPredicate returns [EObject current=null] : iv_ruleRelationTypeArtifactPredicate= ruleRelationTypeArtifactPredicate EOF ;
    public final EObject entryRuleRelationTypeArtifactPredicate() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRelationTypeArtifactPredicate = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2310:2: (iv_ruleRelationTypeArtifactPredicate= ruleRelationTypeArtifactPredicate EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2311:2: iv_ruleRelationTypeArtifactPredicate= ruleRelationTypeArtifactPredicate EOF
            {
             newCompositeNode(grammarAccess.getRelationTypeArtifactPredicateRule()); 
            pushFollow(FOLLOW_ruleRelationTypeArtifactPredicate_in_entryRuleRelationTypeArtifactPredicate4831);
            iv_ruleRelationTypeArtifactPredicate=ruleRelationTypeArtifactPredicate();

            state._fsp--;

             current =iv_ruleRelationTypeArtifactPredicate; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleRelationTypeArtifactPredicate4841); 

            }

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2318:1: ruleRelationTypeArtifactPredicate returns [EObject current=null] : (otherlv_0= 'artifact' ( (otherlv_1= RULE_STRING ) ) ) ;
    public final EObject ruleRelationTypeArtifactPredicate() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2321:28: ( (otherlv_0= 'artifact' ( (otherlv_1= RULE_STRING ) ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2322:1: (otherlv_0= 'artifact' ( (otherlv_1= RULE_STRING ) ) )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2322:1: (otherlv_0= 'artifact' ( (otherlv_1= RULE_STRING ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2322:3: otherlv_0= 'artifact' ( (otherlv_1= RULE_STRING ) )
            {
            otherlv_0=(Token)match(input,73,FOLLOW_73_in_ruleRelationTypeArtifactPredicate4878); 

                	newLeafNode(otherlv_0, grammarAccess.getRelationTypeArtifactPredicateAccess().getArtifactKeyword_0());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2326:1: ( (otherlv_1= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2327:1: (otherlv_1= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2327:1: (otherlv_1= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2328:3: otherlv_1= RULE_STRING
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getRelationTypeArtifactPredicateRule());
            	        }
                    
            otherlv_1=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleRelationTypeArtifactPredicate4898); 

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2347:1: entryRuleRelationTypePredicate returns [EObject current=null] : iv_ruleRelationTypePredicate= ruleRelationTypePredicate EOF ;
    public final EObject entryRuleRelationTypePredicate() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRelationTypePredicate = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2348:2: (iv_ruleRelationTypePredicate= ruleRelationTypePredicate EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2349:2: iv_ruleRelationTypePredicate= ruleRelationTypePredicate EOF
            {
             newCompositeNode(grammarAccess.getRelationTypePredicateRule()); 
            pushFollow(FOLLOW_ruleRelationTypePredicate_in_entryRuleRelationTypePredicate4934);
            iv_ruleRelationTypePredicate=ruleRelationTypePredicate();

            state._fsp--;

             current =iv_ruleRelationTypePredicate; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleRelationTypePredicate4944); 

            }

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2356:1: ruleRelationTypePredicate returns [EObject current=null] : (this_RelationTypeArtifactPredicate_0= ruleRelationTypeArtifactPredicate | this_RelationTypeArtifactTypePredicate_1= ruleRelationTypeArtifactTypePredicate ) ;
    public final EObject ruleRelationTypePredicate() throws RecognitionException {
        EObject current = null;

        EObject this_RelationTypeArtifactPredicate_0 = null;

        EObject this_RelationTypeArtifactTypePredicate_1 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2359:28: ( (this_RelationTypeArtifactPredicate_0= ruleRelationTypeArtifactPredicate | this_RelationTypeArtifactTypePredicate_1= ruleRelationTypeArtifactTypePredicate ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2360:1: (this_RelationTypeArtifactPredicate_0= ruleRelationTypeArtifactPredicate | this_RelationTypeArtifactTypePredicate_1= ruleRelationTypeArtifactTypePredicate )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2360:1: (this_RelationTypeArtifactPredicate_0= ruleRelationTypeArtifactPredicate | this_RelationTypeArtifactTypePredicate_1= ruleRelationTypeArtifactTypePredicate )
            int alt41=2;
            int LA41_0 = input.LA(1);

            if ( (LA41_0==73) ) {
                alt41=1;
            }
            else if ( (LA41_0==16) ) {
                alt41=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 41, 0, input);

                throw nvae;
            }
            switch (alt41) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2361:5: this_RelationTypeArtifactPredicate_0= ruleRelationTypeArtifactPredicate
                    {
                     
                            newCompositeNode(grammarAccess.getRelationTypePredicateAccess().getRelationTypeArtifactPredicateParserRuleCall_0()); 
                        
                    pushFollow(FOLLOW_ruleRelationTypeArtifactPredicate_in_ruleRelationTypePredicate4991);
                    this_RelationTypeArtifactPredicate_0=ruleRelationTypeArtifactPredicate();

                    state._fsp--;

                     
                            current = this_RelationTypeArtifactPredicate_0; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2371:5: this_RelationTypeArtifactTypePredicate_1= ruleRelationTypeArtifactTypePredicate
                    {
                     
                            newCompositeNode(grammarAccess.getRelationTypePredicateAccess().getRelationTypeArtifactTypePredicateParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_ruleRelationTypeArtifactTypePredicate_in_ruleRelationTypePredicate5018);
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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2387:1: entryRuleObjectRestriction returns [EObject current=null] : iv_ruleObjectRestriction= ruleObjectRestriction EOF ;
    public final EObject entryRuleObjectRestriction() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleObjectRestriction = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2388:2: (iv_ruleObjectRestriction= ruleObjectRestriction EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2389:2: iv_ruleObjectRestriction= ruleObjectRestriction EOF
            {
             newCompositeNode(grammarAccess.getObjectRestrictionRule()); 
            pushFollow(FOLLOW_ruleObjectRestriction_in_entryRuleObjectRestriction5053);
            iv_ruleObjectRestriction=ruleObjectRestriction();

            state._fsp--;

             current =iv_ruleObjectRestriction; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleObjectRestriction5063); 

            }

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2396:1: ruleObjectRestriction returns [EObject current=null] : (this_ArtifactMatchRestriction_0= ruleArtifactMatchRestriction | this_ArtifactTypeRestriction_1= ruleArtifactTypeRestriction | this_RelationTypeRestriction_2= ruleRelationTypeRestriction | this_AttributeTypeRestriction_3= ruleAttributeTypeRestriction ) ;
    public final EObject ruleObjectRestriction() throws RecognitionException {
        EObject current = null;

        EObject this_ArtifactMatchRestriction_0 = null;

        EObject this_ArtifactTypeRestriction_1 = null;

        EObject this_RelationTypeRestriction_2 = null;

        EObject this_AttributeTypeRestriction_3 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2399:28: ( (this_ArtifactMatchRestriction_0= ruleArtifactMatchRestriction | this_ArtifactTypeRestriction_1= ruleArtifactTypeRestriction | this_RelationTypeRestriction_2= ruleRelationTypeRestriction | this_AttributeTypeRestriction_3= ruleAttributeTypeRestriction ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2400:1: (this_ArtifactMatchRestriction_0= ruleArtifactMatchRestriction | this_ArtifactTypeRestriction_1= ruleArtifactTypeRestriction | this_RelationTypeRestriction_2= ruleRelationTypeRestriction | this_AttributeTypeRestriction_3= ruleAttributeTypeRestriction )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2400:1: (this_ArtifactMatchRestriction_0= ruleArtifactMatchRestriction | this_ArtifactTypeRestriction_1= ruleArtifactTypeRestriction | this_RelationTypeRestriction_2= ruleRelationTypeRestriction | this_AttributeTypeRestriction_3= ruleAttributeTypeRestriction )
            int alt42=4;
            int LA42_0 = input.LA(1);

            if ( (LA42_0==87) ) {
                int LA42_1 = input.LA(2);

                if ( (LA42_1==74) ) {
                    switch ( input.LA(3) ) {
                    case 56:
                        {
                        alt42=3;
                        }
                        break;
                    case 73:
                        {
                        alt42=1;
                        }
                        break;
                    case 25:
                        {
                        alt42=4;
                        }
                        break;
                    case 16:
                        {
                        alt42=2;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("", 42, 3, input);

                        throw nvae;
                    }

                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 42, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA42_0==88) ) {
                int LA42_2 = input.LA(2);

                if ( (LA42_2==74) ) {
                    switch ( input.LA(3) ) {
                    case 56:
                        {
                        alt42=3;
                        }
                        break;
                    case 73:
                        {
                        alt42=1;
                        }
                        break;
                    case 25:
                        {
                        alt42=4;
                        }
                        break;
                    case 16:
                        {
                        alt42=2;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("", 42, 3, input);

                        throw nvae;
                    }

                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 42, 2, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 42, 0, input);

                throw nvae;
            }
            switch (alt42) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2401:5: this_ArtifactMatchRestriction_0= ruleArtifactMatchRestriction
                    {
                     
                            newCompositeNode(grammarAccess.getObjectRestrictionAccess().getArtifactMatchRestrictionParserRuleCall_0()); 
                        
                    pushFollow(FOLLOW_ruleArtifactMatchRestriction_in_ruleObjectRestriction5110);
                    this_ArtifactMatchRestriction_0=ruleArtifactMatchRestriction();

                    state._fsp--;

                     
                            current = this_ArtifactMatchRestriction_0; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2411:5: this_ArtifactTypeRestriction_1= ruleArtifactTypeRestriction
                    {
                     
                            newCompositeNode(grammarAccess.getObjectRestrictionAccess().getArtifactTypeRestrictionParserRuleCall_1()); 
                        
                    pushFollow(FOLLOW_ruleArtifactTypeRestriction_in_ruleObjectRestriction5137);
                    this_ArtifactTypeRestriction_1=ruleArtifactTypeRestriction();

                    state._fsp--;

                     
                            current = this_ArtifactTypeRestriction_1; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2421:5: this_RelationTypeRestriction_2= ruleRelationTypeRestriction
                    {
                     
                            newCompositeNode(grammarAccess.getObjectRestrictionAccess().getRelationTypeRestrictionParserRuleCall_2()); 
                        
                    pushFollow(FOLLOW_ruleRelationTypeRestriction_in_ruleObjectRestriction5164);
                    this_RelationTypeRestriction_2=ruleRelationTypeRestriction();

                    state._fsp--;

                     
                            current = this_RelationTypeRestriction_2; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2431:5: this_AttributeTypeRestriction_3= ruleAttributeTypeRestriction
                    {
                     
                            newCompositeNode(grammarAccess.getObjectRestrictionAccess().getAttributeTypeRestrictionParserRuleCall_3()); 
                        
                    pushFollow(FOLLOW_ruleAttributeTypeRestriction_in_ruleObjectRestriction5191);
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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2447:1: entryRuleArtifactMatchRestriction returns [EObject current=null] : iv_ruleArtifactMatchRestriction= ruleArtifactMatchRestriction EOF ;
    public final EObject entryRuleArtifactMatchRestriction() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleArtifactMatchRestriction = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2448:2: (iv_ruleArtifactMatchRestriction= ruleArtifactMatchRestriction EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2449:2: iv_ruleArtifactMatchRestriction= ruleArtifactMatchRestriction EOF
            {
             newCompositeNode(grammarAccess.getArtifactMatchRestrictionRule()); 
            pushFollow(FOLLOW_ruleArtifactMatchRestriction_in_entryRuleArtifactMatchRestriction5226);
            iv_ruleArtifactMatchRestriction=ruleArtifactMatchRestriction();

            state._fsp--;

             current =iv_ruleArtifactMatchRestriction; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleArtifactMatchRestriction5236); 

            }

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2456:1: ruleArtifactMatchRestriction returns [EObject current=null] : ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'artifact' ( (otherlv_3= RULE_STRING ) ) otherlv_4= ';' ) ;
    public final EObject ruleArtifactMatchRestriction() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_2=null;
        Token otherlv_3=null;
        Token otherlv_4=null;
        Enumerator lv_permission_0_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2459:28: ( ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'artifact' ( (otherlv_3= RULE_STRING ) ) otherlv_4= ';' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2460:1: ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'artifact' ( (otherlv_3= RULE_STRING ) ) otherlv_4= ';' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2460:1: ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'artifact' ( (otherlv_3= RULE_STRING ) ) otherlv_4= ';' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2460:2: ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'artifact' ( (otherlv_3= RULE_STRING ) ) otherlv_4= ';'
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2460:2: ( (lv_permission_0_0= ruleAccessPermissionEnum ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2461:1: (lv_permission_0_0= ruleAccessPermissionEnum )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2461:1: (lv_permission_0_0= ruleAccessPermissionEnum )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2462:3: lv_permission_0_0= ruleAccessPermissionEnum
            {
             
            	        newCompositeNode(grammarAccess.getArtifactMatchRestrictionAccess().getPermissionAccessPermissionEnumEnumRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_ruleAccessPermissionEnum_in_ruleArtifactMatchRestriction5282);
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

            otherlv_1=(Token)match(input,74,FOLLOW_74_in_ruleArtifactMatchRestriction5294); 

                	newLeafNode(otherlv_1, grammarAccess.getArtifactMatchRestrictionAccess().getEditKeyword_1());
                
            otherlv_2=(Token)match(input,73,FOLLOW_73_in_ruleArtifactMatchRestriction5306); 

                	newLeafNode(otherlv_2, grammarAccess.getArtifactMatchRestrictionAccess().getArtifactKeyword_2());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2486:1: ( (otherlv_3= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2487:1: (otherlv_3= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2487:1: (otherlv_3= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2488:3: otherlv_3= RULE_STRING
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getArtifactMatchRestrictionRule());
            	        }
                    
            otherlv_3=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleArtifactMatchRestriction5326); 

            		newLeafNode(otherlv_3, grammarAccess.getArtifactMatchRestrictionAccess().getArtifactMatcherRefXArtifactMatcherCrossReference_3_0()); 
            	

            }


            }

            otherlv_4=(Token)match(input,70,FOLLOW_70_in_ruleArtifactMatchRestriction5338); 

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2511:1: entryRuleArtifactTypeRestriction returns [EObject current=null] : iv_ruleArtifactTypeRestriction= ruleArtifactTypeRestriction EOF ;
    public final EObject entryRuleArtifactTypeRestriction() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleArtifactTypeRestriction = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2512:2: (iv_ruleArtifactTypeRestriction= ruleArtifactTypeRestriction EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2513:2: iv_ruleArtifactTypeRestriction= ruleArtifactTypeRestriction EOF
            {
             newCompositeNode(grammarAccess.getArtifactTypeRestrictionRule()); 
            pushFollow(FOLLOW_ruleArtifactTypeRestriction_in_entryRuleArtifactTypeRestriction5374);
            iv_ruleArtifactTypeRestriction=ruleArtifactTypeRestriction();

            state._fsp--;

             current =iv_ruleArtifactTypeRestriction; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleArtifactTypeRestriction5384); 

            }

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2520:1: ruleArtifactTypeRestriction returns [EObject current=null] : ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'artifactType' ( (otherlv_3= RULE_STRING ) ) otherlv_4= ';' ) ;
    public final EObject ruleArtifactTypeRestriction() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_2=null;
        Token otherlv_3=null;
        Token otherlv_4=null;
        Enumerator lv_permission_0_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2523:28: ( ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'artifactType' ( (otherlv_3= RULE_STRING ) ) otherlv_4= ';' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2524:1: ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'artifactType' ( (otherlv_3= RULE_STRING ) ) otherlv_4= ';' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2524:1: ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'artifactType' ( (otherlv_3= RULE_STRING ) ) otherlv_4= ';' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2524:2: ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'artifactType' ( (otherlv_3= RULE_STRING ) ) otherlv_4= ';'
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2524:2: ( (lv_permission_0_0= ruleAccessPermissionEnum ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2525:1: (lv_permission_0_0= ruleAccessPermissionEnum )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2525:1: (lv_permission_0_0= ruleAccessPermissionEnum )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2526:3: lv_permission_0_0= ruleAccessPermissionEnum
            {
             
            	        newCompositeNode(grammarAccess.getArtifactTypeRestrictionAccess().getPermissionAccessPermissionEnumEnumRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_ruleAccessPermissionEnum_in_ruleArtifactTypeRestriction5430);
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

            otherlv_1=(Token)match(input,74,FOLLOW_74_in_ruleArtifactTypeRestriction5442); 

                	newLeafNode(otherlv_1, grammarAccess.getArtifactTypeRestrictionAccess().getEditKeyword_1());
                
            otherlv_2=(Token)match(input,16,FOLLOW_16_in_ruleArtifactTypeRestriction5454); 

                	newLeafNode(otherlv_2, grammarAccess.getArtifactTypeRestrictionAccess().getArtifactTypeKeyword_2());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2550:1: ( (otherlv_3= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2551:1: (otherlv_3= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2551:1: (otherlv_3= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2552:3: otherlv_3= RULE_STRING
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getArtifactTypeRestrictionRule());
            	        }
                    
            otherlv_3=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleArtifactTypeRestriction5474); 

            		newLeafNode(otherlv_3, grammarAccess.getArtifactTypeRestrictionAccess().getArtifactTypeRefXArtifactTypeCrossReference_3_0()); 
            	

            }


            }

            otherlv_4=(Token)match(input,70,FOLLOW_70_in_ruleArtifactTypeRestriction5486); 

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2575:1: entryRuleAttributeTypeRestriction returns [EObject current=null] : iv_ruleAttributeTypeRestriction= ruleAttributeTypeRestriction EOF ;
    public final EObject entryRuleAttributeTypeRestriction() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttributeTypeRestriction = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2576:2: (iv_ruleAttributeTypeRestriction= ruleAttributeTypeRestriction EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2577:2: iv_ruleAttributeTypeRestriction= ruleAttributeTypeRestriction EOF
            {
             newCompositeNode(grammarAccess.getAttributeTypeRestrictionRule()); 
            pushFollow(FOLLOW_ruleAttributeTypeRestriction_in_entryRuleAttributeTypeRestriction5522);
            iv_ruleAttributeTypeRestriction=ruleAttributeTypeRestriction();

            state._fsp--;

             current =iv_ruleAttributeTypeRestriction; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttributeTypeRestriction5532); 

            }

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2584:1: ruleAttributeTypeRestriction returns [EObject current=null] : ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'attributeType' ( (otherlv_3= RULE_STRING ) ) (otherlv_4= 'of' otherlv_5= 'artifactType' ( (otherlv_6= RULE_STRING ) ) )? otherlv_7= ';' ) ;
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
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2587:28: ( ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'attributeType' ( (otherlv_3= RULE_STRING ) ) (otherlv_4= 'of' otherlv_5= 'artifactType' ( (otherlv_6= RULE_STRING ) ) )? otherlv_7= ';' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2588:1: ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'attributeType' ( (otherlv_3= RULE_STRING ) ) (otherlv_4= 'of' otherlv_5= 'artifactType' ( (otherlv_6= RULE_STRING ) ) )? otherlv_7= ';' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2588:1: ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'attributeType' ( (otherlv_3= RULE_STRING ) ) (otherlv_4= 'of' otherlv_5= 'artifactType' ( (otherlv_6= RULE_STRING ) ) )? otherlv_7= ';' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2588:2: ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'attributeType' ( (otherlv_3= RULE_STRING ) ) (otherlv_4= 'of' otherlv_5= 'artifactType' ( (otherlv_6= RULE_STRING ) ) )? otherlv_7= ';'
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2588:2: ( (lv_permission_0_0= ruleAccessPermissionEnum ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2589:1: (lv_permission_0_0= ruleAccessPermissionEnum )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2589:1: (lv_permission_0_0= ruleAccessPermissionEnum )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2590:3: lv_permission_0_0= ruleAccessPermissionEnum
            {
             
            	        newCompositeNode(grammarAccess.getAttributeTypeRestrictionAccess().getPermissionAccessPermissionEnumEnumRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_ruleAccessPermissionEnum_in_ruleAttributeTypeRestriction5578);
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

            otherlv_1=(Token)match(input,74,FOLLOW_74_in_ruleAttributeTypeRestriction5590); 

                	newLeafNode(otherlv_1, grammarAccess.getAttributeTypeRestrictionAccess().getEditKeyword_1());
                
            otherlv_2=(Token)match(input,25,FOLLOW_25_in_ruleAttributeTypeRestriction5602); 

                	newLeafNode(otherlv_2, grammarAccess.getAttributeTypeRestrictionAccess().getAttributeTypeKeyword_2());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2614:1: ( (otherlv_3= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2615:1: (otherlv_3= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2615:1: (otherlv_3= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2616:3: otherlv_3= RULE_STRING
            {

            			if (current==null) {
            	            current = createModelElement(grammarAccess.getAttributeTypeRestrictionRule());
            	        }
                    
            otherlv_3=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAttributeTypeRestriction5622); 

            		newLeafNode(otherlv_3, grammarAccess.getAttributeTypeRestrictionAccess().getAttributeTypeRefXAttributeTypeCrossReference_3_0()); 
            	

            }


            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2627:2: (otherlv_4= 'of' otherlv_5= 'artifactType' ( (otherlv_6= RULE_STRING ) ) )?
            int alt43=2;
            int LA43_0 = input.LA(1);

            if ( (LA43_0==75) ) {
                alt43=1;
            }
            switch (alt43) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2627:4: otherlv_4= 'of' otherlv_5= 'artifactType' ( (otherlv_6= RULE_STRING ) )
                    {
                    otherlv_4=(Token)match(input,75,FOLLOW_75_in_ruleAttributeTypeRestriction5635); 

                        	newLeafNode(otherlv_4, grammarAccess.getAttributeTypeRestrictionAccess().getOfKeyword_4_0());
                        
                    otherlv_5=(Token)match(input,16,FOLLOW_16_in_ruleAttributeTypeRestriction5647); 

                        	newLeafNode(otherlv_5, grammarAccess.getAttributeTypeRestrictionAccess().getArtifactTypeKeyword_4_1());
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2635:1: ( (otherlv_6= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2636:1: (otherlv_6= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2636:1: (otherlv_6= RULE_STRING )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2637:3: otherlv_6= RULE_STRING
                    {

                    			if (current==null) {
                    	            current = createModelElement(grammarAccess.getAttributeTypeRestrictionRule());
                    	        }
                            
                    otherlv_6=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAttributeTypeRestriction5667); 

                    		newLeafNode(otherlv_6, grammarAccess.getAttributeTypeRestrictionAccess().getArtifactTypeRefXArtifactTypeCrossReference_4_2_0()); 
                    	

                    }


                    }


                    }
                    break;

            }

            otherlv_7=(Token)match(input,70,FOLLOW_70_in_ruleAttributeTypeRestriction5681); 

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2662:1: entryRuleRelationTypeRestriction returns [EObject current=null] : iv_ruleRelationTypeRestriction= ruleRelationTypeRestriction EOF ;
    public final EObject entryRuleRelationTypeRestriction() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRelationTypeRestriction = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2663:2: (iv_ruleRelationTypeRestriction= ruleRelationTypeRestriction EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2664:2: iv_ruleRelationTypeRestriction= ruleRelationTypeRestriction EOF
            {
             newCompositeNode(grammarAccess.getRelationTypeRestrictionRule()); 
            pushFollow(FOLLOW_ruleRelationTypeRestriction_in_entryRuleRelationTypeRestriction5719);
            iv_ruleRelationTypeRestriction=ruleRelationTypeRestriction();

            state._fsp--;

             current =iv_ruleRelationTypeRestriction; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleRelationTypeRestriction5729); 

            }

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2671:1: ruleRelationTypeRestriction returns [EObject current=null] : ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'relationType' ( ( (lv_relationTypeMatch_3_0= ruleRelationTypeMatch ) ) | ( (otherlv_4= RULE_STRING ) ) ) ( (lv_restrictedToSide_5_0= ruleXRelationSideEnum ) ) ( (lv_predicate_6_0= ruleRelationTypePredicate ) )? otherlv_7= ';' ) ;
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
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2674:28: ( ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'relationType' ( ( (lv_relationTypeMatch_3_0= ruleRelationTypeMatch ) ) | ( (otherlv_4= RULE_STRING ) ) ) ( (lv_restrictedToSide_5_0= ruleXRelationSideEnum ) ) ( (lv_predicate_6_0= ruleRelationTypePredicate ) )? otherlv_7= ';' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2675:1: ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'relationType' ( ( (lv_relationTypeMatch_3_0= ruleRelationTypeMatch ) ) | ( (otherlv_4= RULE_STRING ) ) ) ( (lv_restrictedToSide_5_0= ruleXRelationSideEnum ) ) ( (lv_predicate_6_0= ruleRelationTypePredicate ) )? otherlv_7= ';' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2675:1: ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'relationType' ( ( (lv_relationTypeMatch_3_0= ruleRelationTypeMatch ) ) | ( (otherlv_4= RULE_STRING ) ) ) ( (lv_restrictedToSide_5_0= ruleXRelationSideEnum ) ) ( (lv_predicate_6_0= ruleRelationTypePredicate ) )? otherlv_7= ';' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2675:2: ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) otherlv_1= 'edit' otherlv_2= 'relationType' ( ( (lv_relationTypeMatch_3_0= ruleRelationTypeMatch ) ) | ( (otherlv_4= RULE_STRING ) ) ) ( (lv_restrictedToSide_5_0= ruleXRelationSideEnum ) ) ( (lv_predicate_6_0= ruleRelationTypePredicate ) )? otherlv_7= ';'
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2675:2: ( (lv_permission_0_0= ruleAccessPermissionEnum ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2676:1: (lv_permission_0_0= ruleAccessPermissionEnum )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2676:1: (lv_permission_0_0= ruleAccessPermissionEnum )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2677:3: lv_permission_0_0= ruleAccessPermissionEnum
            {
             
            	        newCompositeNode(grammarAccess.getRelationTypeRestrictionAccess().getPermissionAccessPermissionEnumEnumRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_ruleAccessPermissionEnum_in_ruleRelationTypeRestriction5775);
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

            otherlv_1=(Token)match(input,74,FOLLOW_74_in_ruleRelationTypeRestriction5787); 

                	newLeafNode(otherlv_1, grammarAccess.getRelationTypeRestrictionAccess().getEditKeyword_1());
                
            otherlv_2=(Token)match(input,56,FOLLOW_56_in_ruleRelationTypeRestriction5799); 

                	newLeafNode(otherlv_2, grammarAccess.getRelationTypeRestrictionAccess().getRelationTypeKeyword_2());
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2701:1: ( ( (lv_relationTypeMatch_3_0= ruleRelationTypeMatch ) ) | ( (otherlv_4= RULE_STRING ) ) )
            int alt44=2;
            int LA44_0 = input.LA(1);

            if ( (LA44_0==89) ) {
                alt44=1;
            }
            else if ( (LA44_0==RULE_STRING) ) {
                alt44=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 44, 0, input);

                throw nvae;
            }
            switch (alt44) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2701:2: ( (lv_relationTypeMatch_3_0= ruleRelationTypeMatch ) )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2701:2: ( (lv_relationTypeMatch_3_0= ruleRelationTypeMatch ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2702:1: (lv_relationTypeMatch_3_0= ruleRelationTypeMatch )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2702:1: (lv_relationTypeMatch_3_0= ruleRelationTypeMatch )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2703:3: lv_relationTypeMatch_3_0= ruleRelationTypeMatch
                    {
                     
                    	        newCompositeNode(grammarAccess.getRelationTypeRestrictionAccess().getRelationTypeMatchRelationTypeMatchEnumRuleCall_3_0_0()); 
                    	    
                    pushFollow(FOLLOW_ruleRelationTypeMatch_in_ruleRelationTypeRestriction5821);
                    lv_relationTypeMatch_3_0=ruleRelationTypeMatch();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getRelationTypeRestrictionRule());
                    	        }
                           		set(
                           			current, 
                           			"relationTypeMatch",
                            		lv_relationTypeMatch_3_0, 
                            		"RelationTypeMatch");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2720:6: ( (otherlv_4= RULE_STRING ) )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2720:6: ( (otherlv_4= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2721:1: (otherlv_4= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2721:1: (otherlv_4= RULE_STRING )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2722:3: otherlv_4= RULE_STRING
                    {

                    			if (current==null) {
                    	            current = createModelElement(grammarAccess.getRelationTypeRestrictionRule());
                    	        }
                            
                    otherlv_4=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleRelationTypeRestriction5847); 

                    		newLeafNode(otherlv_4, grammarAccess.getRelationTypeRestrictionAccess().getRelationTypeRefXRelationTypeCrossReference_3_1_0()); 
                    	

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2733:3: ( (lv_restrictedToSide_5_0= ruleXRelationSideEnum ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2734:1: (lv_restrictedToSide_5_0= ruleXRelationSideEnum )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2734:1: (lv_restrictedToSide_5_0= ruleXRelationSideEnum )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2735:3: lv_restrictedToSide_5_0= ruleXRelationSideEnum
            {
             
            	        newCompositeNode(grammarAccess.getRelationTypeRestrictionAccess().getRestrictedToSideXRelationSideEnumEnumRuleCall_4_0()); 
            	    
            pushFollow(FOLLOW_ruleXRelationSideEnum_in_ruleRelationTypeRestriction5869);
            lv_restrictedToSide_5_0=ruleXRelationSideEnum();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getRelationTypeRestrictionRule());
            	        }
                   		set(
                   			current, 
                   			"restrictedToSide",
                    		lv_restrictedToSide_5_0, 
                    		"XRelationSideEnum");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2751:2: ( (lv_predicate_6_0= ruleRelationTypePredicate ) )?
            int alt45=2;
            int LA45_0 = input.LA(1);

            if ( (LA45_0==16||LA45_0==73) ) {
                alt45=1;
            }
            switch (alt45) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2752:1: (lv_predicate_6_0= ruleRelationTypePredicate )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2752:1: (lv_predicate_6_0= ruleRelationTypePredicate )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2753:3: lv_predicate_6_0= ruleRelationTypePredicate
                    {
                     
                    	        newCompositeNode(grammarAccess.getRelationTypeRestrictionAccess().getPredicateRelationTypePredicateParserRuleCall_5_0()); 
                    	    
                    pushFollow(FOLLOW_ruleRelationTypePredicate_in_ruleRelationTypeRestriction5890);
                    lv_predicate_6_0=ruleRelationTypePredicate();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getRelationTypeRestrictionRule());
                    	        }
                           		set(
                           			current, 
                           			"predicate",
                            		lv_predicate_6_0, 
                            		"RelationTypePredicate");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }
                    break;

            }

            otherlv_7=(Token)match(input,70,FOLLOW_70_in_ruleRelationTypeRestriction5903); 

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2781:1: ruleRelationMultiplicityEnum returns [Enumerator current=null] : ( (enumLiteral_0= 'ONE_TO_ONE' ) | (enumLiteral_1= 'ONE_TO_MANY' ) | (enumLiteral_2= 'MANY_TO_ONE' ) | (enumLiteral_3= 'MANY_TO_MANY' ) ) ;
    public final Enumerator ruleRelationMultiplicityEnum() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;
        Token enumLiteral_2=null;
        Token enumLiteral_3=null;

         enterRule(); 
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2783:28: ( ( (enumLiteral_0= 'ONE_TO_ONE' ) | (enumLiteral_1= 'ONE_TO_MANY' ) | (enumLiteral_2= 'MANY_TO_ONE' ) | (enumLiteral_3= 'MANY_TO_MANY' ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2784:1: ( (enumLiteral_0= 'ONE_TO_ONE' ) | (enumLiteral_1= 'ONE_TO_MANY' ) | (enumLiteral_2= 'MANY_TO_ONE' ) | (enumLiteral_3= 'MANY_TO_MANY' ) )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2784:1: ( (enumLiteral_0= 'ONE_TO_ONE' ) | (enumLiteral_1= 'ONE_TO_MANY' ) | (enumLiteral_2= 'MANY_TO_ONE' ) | (enumLiteral_3= 'MANY_TO_MANY' ) )
            int alt46=4;
            switch ( input.LA(1) ) {
            case 76:
                {
                alt46=1;
                }
                break;
            case 77:
                {
                alt46=2;
                }
                break;
            case 78:
                {
                alt46=3;
                }
                break;
            case 79:
                {
                alt46=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 46, 0, input);

                throw nvae;
            }

            switch (alt46) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2784:2: (enumLiteral_0= 'ONE_TO_ONE' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2784:2: (enumLiteral_0= 'ONE_TO_ONE' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2784:4: enumLiteral_0= 'ONE_TO_ONE'
                    {
                    enumLiteral_0=(Token)match(input,76,FOLLOW_76_in_ruleRelationMultiplicityEnum5953); 

                            current = grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_ONEEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_0, grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_ONEEnumLiteralDeclaration_0()); 
                        

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2790:6: (enumLiteral_1= 'ONE_TO_MANY' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2790:6: (enumLiteral_1= 'ONE_TO_MANY' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2790:8: enumLiteral_1= 'ONE_TO_MANY'
                    {
                    enumLiteral_1=(Token)match(input,77,FOLLOW_77_in_ruleRelationMultiplicityEnum5970); 

                            current = grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_MANYEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_1, grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_MANYEnumLiteralDeclaration_1()); 
                        

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2796:6: (enumLiteral_2= 'MANY_TO_ONE' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2796:6: (enumLiteral_2= 'MANY_TO_ONE' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2796:8: enumLiteral_2= 'MANY_TO_ONE'
                    {
                    enumLiteral_2=(Token)match(input,78,FOLLOW_78_in_ruleRelationMultiplicityEnum5987); 

                            current = grammarAccess.getRelationMultiplicityEnumAccess().getMANY_TO_ONEEnumLiteralDeclaration_2().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_2, grammarAccess.getRelationMultiplicityEnumAccess().getMANY_TO_ONEEnumLiteralDeclaration_2()); 
                        

                    }


                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2802:6: (enumLiteral_3= 'MANY_TO_MANY' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2802:6: (enumLiteral_3= 'MANY_TO_MANY' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2802:8: enumLiteral_3= 'MANY_TO_MANY'
                    {
                    enumLiteral_3=(Token)match(input,79,FOLLOW_79_in_ruleRelationMultiplicityEnum6004); 

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2812:1: ruleCompareOp returns [Enumerator current=null] : ( (enumLiteral_0= 'EQ' ) | (enumLiteral_1= 'LIKE' ) ) ;
    public final Enumerator ruleCompareOp() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;

         enterRule(); 
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2814:28: ( ( (enumLiteral_0= 'EQ' ) | (enumLiteral_1= 'LIKE' ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2815:1: ( (enumLiteral_0= 'EQ' ) | (enumLiteral_1= 'LIKE' ) )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2815:1: ( (enumLiteral_0= 'EQ' ) | (enumLiteral_1= 'LIKE' ) )
            int alt47=2;
            int LA47_0 = input.LA(1);

            if ( (LA47_0==80) ) {
                alt47=1;
            }
            else if ( (LA47_0==81) ) {
                alt47=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 47, 0, input);

                throw nvae;
            }
            switch (alt47) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2815:2: (enumLiteral_0= 'EQ' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2815:2: (enumLiteral_0= 'EQ' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2815:4: enumLiteral_0= 'EQ'
                    {
                    enumLiteral_0=(Token)match(input,80,FOLLOW_80_in_ruleCompareOp6049); 

                            current = grammarAccess.getCompareOpAccess().getEQEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_0, grammarAccess.getCompareOpAccess().getEQEnumLiteralDeclaration_0()); 
                        

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2821:6: (enumLiteral_1= 'LIKE' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2821:6: (enumLiteral_1= 'LIKE' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2821:8: enumLiteral_1= 'LIKE'
                    {
                    enumLiteral_1=(Token)match(input,81,FOLLOW_81_in_ruleCompareOp6066); 

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2831:1: ruleXLogicOperator returns [Enumerator current=null] : ( (enumLiteral_0= 'AND' ) | (enumLiteral_1= 'OR' ) ) ;
    public final Enumerator ruleXLogicOperator() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;

         enterRule(); 
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2833:28: ( ( (enumLiteral_0= 'AND' ) | (enumLiteral_1= 'OR' ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2834:1: ( (enumLiteral_0= 'AND' ) | (enumLiteral_1= 'OR' ) )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2834:1: ( (enumLiteral_0= 'AND' ) | (enumLiteral_1= 'OR' ) )
            int alt48=2;
            int LA48_0 = input.LA(1);

            if ( (LA48_0==82) ) {
                alt48=1;
            }
            else if ( (LA48_0==83) ) {
                alt48=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 48, 0, input);

                throw nvae;
            }
            switch (alt48) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2834:2: (enumLiteral_0= 'AND' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2834:2: (enumLiteral_0= 'AND' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2834:4: enumLiteral_0= 'AND'
                    {
                    enumLiteral_0=(Token)match(input,82,FOLLOW_82_in_ruleXLogicOperator6111); 

                            current = grammarAccess.getXLogicOperatorAccess().getANDEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_0, grammarAccess.getXLogicOperatorAccess().getANDEnumLiteralDeclaration_0()); 
                        

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2840:6: (enumLiteral_1= 'OR' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2840:6: (enumLiteral_1= 'OR' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2840:8: enumLiteral_1= 'OR'
                    {
                    enumLiteral_1=(Token)match(input,83,FOLLOW_83_in_ruleXLogicOperator6128); 

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2850:1: ruleMatchField returns [Enumerator current=null] : ( (enumLiteral_0= 'artifactName' ) | (enumLiteral_1= 'artifactGuid' ) | (enumLiteral_2= 'branchName' ) | (enumLiteral_3= 'branchGuid' ) ) ;
    public final Enumerator ruleMatchField() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;
        Token enumLiteral_2=null;
        Token enumLiteral_3=null;

         enterRule(); 
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2852:28: ( ( (enumLiteral_0= 'artifactName' ) | (enumLiteral_1= 'artifactGuid' ) | (enumLiteral_2= 'branchName' ) | (enumLiteral_3= 'branchGuid' ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2853:1: ( (enumLiteral_0= 'artifactName' ) | (enumLiteral_1= 'artifactGuid' ) | (enumLiteral_2= 'branchName' ) | (enumLiteral_3= 'branchGuid' ) )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2853:1: ( (enumLiteral_0= 'artifactName' ) | (enumLiteral_1= 'artifactGuid' ) | (enumLiteral_2= 'branchName' ) | (enumLiteral_3= 'branchGuid' ) )
            int alt49=4;
            switch ( input.LA(1) ) {
            case 84:
                {
                alt49=1;
                }
                break;
            case 85:
                {
                alt49=2;
                }
                break;
            case 86:
                {
                alt49=3;
                }
                break;
            case 24:
                {
                alt49=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 49, 0, input);

                throw nvae;
            }

            switch (alt49) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2853:2: (enumLiteral_0= 'artifactName' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2853:2: (enumLiteral_0= 'artifactName' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2853:4: enumLiteral_0= 'artifactName'
                    {
                    enumLiteral_0=(Token)match(input,84,FOLLOW_84_in_ruleMatchField6173); 

                            current = grammarAccess.getMatchFieldAccess().getArtifactNameEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_0, grammarAccess.getMatchFieldAccess().getArtifactNameEnumLiteralDeclaration_0()); 
                        

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2859:6: (enumLiteral_1= 'artifactGuid' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2859:6: (enumLiteral_1= 'artifactGuid' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2859:8: enumLiteral_1= 'artifactGuid'
                    {
                    enumLiteral_1=(Token)match(input,85,FOLLOW_85_in_ruleMatchField6190); 

                            current = grammarAccess.getMatchFieldAccess().getArtifactGuidEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_1, grammarAccess.getMatchFieldAccess().getArtifactGuidEnumLiteralDeclaration_1()); 
                        

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2865:6: (enumLiteral_2= 'branchName' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2865:6: (enumLiteral_2= 'branchName' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2865:8: enumLiteral_2= 'branchName'
                    {
                    enumLiteral_2=(Token)match(input,86,FOLLOW_86_in_ruleMatchField6207); 

                            current = grammarAccess.getMatchFieldAccess().getBranchNameEnumLiteralDeclaration_2().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_2, grammarAccess.getMatchFieldAccess().getBranchNameEnumLiteralDeclaration_2()); 
                        

                    }


                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2871:6: (enumLiteral_3= 'branchGuid' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2871:6: (enumLiteral_3= 'branchGuid' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2871:8: enumLiteral_3= 'branchGuid'
                    {
                    enumLiteral_3=(Token)match(input,24,FOLLOW_24_in_ruleMatchField6224); 

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2881:1: ruleAccessPermissionEnum returns [Enumerator current=null] : ( (enumLiteral_0= 'ALLOW' ) | (enumLiteral_1= 'DENY' ) ) ;
    public final Enumerator ruleAccessPermissionEnum() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;

         enterRule(); 
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2883:28: ( ( (enumLiteral_0= 'ALLOW' ) | (enumLiteral_1= 'DENY' ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2884:1: ( (enumLiteral_0= 'ALLOW' ) | (enumLiteral_1= 'DENY' ) )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2884:1: ( (enumLiteral_0= 'ALLOW' ) | (enumLiteral_1= 'DENY' ) )
            int alt50=2;
            int LA50_0 = input.LA(1);

            if ( (LA50_0==87) ) {
                alt50=1;
            }
            else if ( (LA50_0==88) ) {
                alt50=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 50, 0, input);

                throw nvae;
            }
            switch (alt50) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2884:2: (enumLiteral_0= 'ALLOW' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2884:2: (enumLiteral_0= 'ALLOW' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2884:4: enumLiteral_0= 'ALLOW'
                    {
                    enumLiteral_0=(Token)match(input,87,FOLLOW_87_in_ruleAccessPermissionEnum6269); 

                            current = grammarAccess.getAccessPermissionEnumAccess().getALLOWEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_0, grammarAccess.getAccessPermissionEnumAccess().getALLOWEnumLiteralDeclaration_0()); 
                        

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2890:6: (enumLiteral_1= 'DENY' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2890:6: (enumLiteral_1= 'DENY' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2890:8: enumLiteral_1= 'DENY'
                    {
                    enumLiteral_1=(Token)match(input,88,FOLLOW_88_in_ruleAccessPermissionEnum6286); 

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2900:1: ruleRelationTypeMatch returns [Enumerator current=null] : (enumLiteral_0= 'ALL' ) ;
    public final Enumerator ruleRelationTypeMatch() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;

         enterRule(); 
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2902:28: ( (enumLiteral_0= 'ALL' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2903:1: (enumLiteral_0= 'ALL' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2903:1: (enumLiteral_0= 'ALL' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2903:3: enumLiteral_0= 'ALL'
            {
            enumLiteral_0=(Token)match(input,89,FOLLOW_89_in_ruleRelationTypeMatch6330); 

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
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2913:1: ruleXRelationSideEnum returns [Enumerator current=null] : ( (enumLiteral_0= 'SIDE_A' ) | (enumLiteral_1= 'SIDE_B' ) | (enumLiteral_2= 'BOTH' ) ) ;
    public final Enumerator ruleXRelationSideEnum() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;
        Token enumLiteral_2=null;

         enterRule(); 
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2915:28: ( ( (enumLiteral_0= 'SIDE_A' ) | (enumLiteral_1= 'SIDE_B' ) | (enumLiteral_2= 'BOTH' ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2916:1: ( (enumLiteral_0= 'SIDE_A' ) | (enumLiteral_1= 'SIDE_B' ) | (enumLiteral_2= 'BOTH' ) )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2916:1: ( (enumLiteral_0= 'SIDE_A' ) | (enumLiteral_1= 'SIDE_B' ) | (enumLiteral_2= 'BOTH' ) )
            int alt51=3;
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
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 51, 0, input);

                throw nvae;
            }

            switch (alt51) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2916:2: (enumLiteral_0= 'SIDE_A' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2916:2: (enumLiteral_0= 'SIDE_A' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2916:4: enumLiteral_0= 'SIDE_A'
                    {
                    enumLiteral_0=(Token)match(input,90,FOLLOW_90_in_ruleXRelationSideEnum6374); 

                            current = grammarAccess.getXRelationSideEnumAccess().getSIDE_AEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_0, grammarAccess.getXRelationSideEnumAccess().getSIDE_AEnumLiteralDeclaration_0()); 
                        

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2922:6: (enumLiteral_1= 'SIDE_B' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2922:6: (enumLiteral_1= 'SIDE_B' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2922:8: enumLiteral_1= 'SIDE_B'
                    {
                    enumLiteral_1=(Token)match(input,91,FOLLOW_91_in_ruleXRelationSideEnum6391); 

                            current = grammarAccess.getXRelationSideEnumAccess().getSIDE_BEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_1, grammarAccess.getXRelationSideEnumAccess().getSIDE_BEnumLiteralDeclaration_1()); 
                        

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2928:6: (enumLiteral_2= 'BOTH' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2928:6: (enumLiteral_2= 'BOTH' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2928:8: enumLiteral_2= 'BOTH'
                    {
                    enumLiteral_2=(Token)match(input,92,FOLLOW_92_in_ruleXRelationSideEnum6408); 

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
    public static final BitSet FOLLOW_ruleImport_in_ruleOseeDsl131 = new BitSet(new long[]{0x011200000201A002L,0x0000000000000090L});
    public static final BitSet FOLLOW_ruleXArtifactType_in_ruleOseeDsl154 = new BitSet(new long[]{0x0112000002018002L,0x0000000000000090L});
    public static final BitSet FOLLOW_ruleXRelationType_in_ruleOseeDsl181 = new BitSet(new long[]{0x0112000002018002L,0x0000000000000090L});
    public static final BitSet FOLLOW_ruleXAttributeType_in_ruleOseeDsl208 = new BitSet(new long[]{0x0112000002018002L,0x0000000000000090L});
    public static final BitSet FOLLOW_ruleXOseeEnumType_in_ruleOseeDsl235 = new BitSet(new long[]{0x0112000002018002L,0x0000000000000090L});
    public static final BitSet FOLLOW_ruleXOseeEnumOverride_in_ruleOseeDsl262 = new BitSet(new long[]{0x0112000002018002L,0x0000000000000090L});
    public static final BitSet FOLLOW_ruleXArtifactMatcher_in_ruleOseeDsl285 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000090L});
    public static final BitSet FOLLOW_ruleAccessContext_in_ruleOseeDsl307 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000080L});
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
    public static final BitSet FOLLOW_19_in_ruleXArtifactType940 = new BitSet(new long[]{0x0000000000300000L});
    public static final BitSet FOLLOW_20_in_ruleXArtifactType953 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXArtifactType970 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_21_in_ruleXArtifactType989 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RULE_HEX_INT_in_ruleXArtifactType1006 = new BitSet(new long[]{0x0000000000C00000L});
    public static final BitSet FOLLOW_ruleXAttributeTypeRef_in_ruleXArtifactType1032 = new BitSet(new long[]{0x0000000000C00000L});
    public static final BitSet FOLLOW_22_in_ruleXArtifactType1045 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXAttributeTypeRef_in_entryRuleXAttributeTypeRef1081 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleXAttributeTypeRef1091 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_23_in_ruleXAttributeTypeRef1128 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXAttributeTypeRef1148 = new BitSet(new long[]{0x0000000001000002L});
    public static final BitSet FOLLOW_24_in_ruleXAttributeTypeRef1161 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXAttributeTypeRef1178 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXAttributeType_in_entryRuleXAttributeType1221 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleXAttributeType1231 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_ruleXAttributeType1268 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXAttributeType1285 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_17_in_ruleXAttributeType1303 = new BitSet(new long[]{0x0001FF8000000020L});
    public static final BitSet FOLLOW_ruleAttributeBaseType_in_ruleXAttributeType1324 = new BitSet(new long[]{0x0000000004080000L});
    public static final BitSet FOLLOW_26_in_ruleXAttributeType1338 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXAttributeType1358 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_ruleXAttributeType1372 = new BitSet(new long[]{0x0000000000300000L});
    public static final BitSet FOLLOW_20_in_ruleXAttributeType1385 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXAttributeType1402 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_21_in_ruleXAttributeType1421 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RULE_HEX_INT_in_ruleXAttributeType1438 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_27_in_ruleXAttributeType1455 = new BitSet(new long[]{0x0001FF8030000020L});
    public static final BitSet FOLLOW_28_in_ruleXAttributeType1475 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_29_in_ruleXAttributeType1504 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_ruleXAttributeType1536 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_ruleXAttributeType1551 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_RULE_WHOLE_NUM_STR_in_ruleXAttributeType1568 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_31_in_ruleXAttributeType1585 = new BitSet(new long[]{0x0000000100000080L});
    public static final BitSet FOLLOW_RULE_WHOLE_NUM_STR_in_ruleXAttributeType1604 = new BitSet(new long[]{0x0000007A00400000L});
    public static final BitSet FOLLOW_32_in_ruleXAttributeType1625 = new BitSet(new long[]{0x0000007A00400000L});
    public static final BitSet FOLLOW_33_in_ruleXAttributeType1654 = new BitSet(new long[]{0x0001FF8400000020L});
    public static final BitSet FOLLOW_34_in_ruleXAttributeType1674 = new BitSet(new long[]{0x0000007800400000L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_ruleXAttributeType1706 = new BitSet(new long[]{0x0000007800400000L});
    public static final BitSet FOLLOW_35_in_ruleXAttributeType1724 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXAttributeType1744 = new BitSet(new long[]{0x0000007000400000L});
    public static final BitSet FOLLOW_36_in_ruleXAttributeType1759 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXAttributeType1776 = new BitSet(new long[]{0x0000006000400000L});
    public static final BitSet FOLLOW_37_in_ruleXAttributeType1796 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXAttributeType1813 = new BitSet(new long[]{0x0000004000400000L});
    public static final BitSet FOLLOW_38_in_ruleXAttributeType1833 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXAttributeType1850 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_22_in_ruleXAttributeType1869 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeBaseType_in_entryRuleAttributeBaseType1906 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttributeBaseType1917 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_39_in_ruleAttributeBaseType1955 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_ruleAttributeBaseType1974 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_ruleAttributeBaseType1993 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_ruleAttributeBaseType2012 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_43_in_ruleAttributeBaseType2031 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_ruleAttributeBaseType2050 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_45_in_ruleAttributeBaseType2069 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_46_in_ruleAttributeBaseType2088 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_ruleAttributeBaseType2107 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_ruleAttributeBaseType2126 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_ruleAttributeBaseType2154 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXOseeEnumType_in_entryRuleXOseeEnumType2199 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleXOseeEnumType2209 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_49_in_ruleXOseeEnumType2246 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXOseeEnumType2263 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_ruleXOseeEnumType2280 = new BitSet(new long[]{0x0000000000300000L});
    public static final BitSet FOLLOW_20_in_ruleXOseeEnumType2293 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXOseeEnumType2310 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_21_in_ruleXOseeEnumType2329 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RULE_HEX_INT_in_ruleXOseeEnumType2346 = new BitSet(new long[]{0x0004000000400000L});
    public static final BitSet FOLLOW_ruleXOseeEnumEntry_in_ruleXOseeEnumType2372 = new BitSet(new long[]{0x0004000000400000L});
    public static final BitSet FOLLOW_22_in_ruleXOseeEnumType2385 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXOseeEnumEntry_in_entryRuleXOseeEnumEntry2421 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleXOseeEnumEntry2431 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_50_in_ruleXOseeEnumEntry2468 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXOseeEnumEntry2485 = new BitSet(new long[]{0x0008000000000082L});
    public static final BitSet FOLLOW_RULE_WHOLE_NUM_STR_in_ruleXOseeEnumEntry2507 = new BitSet(new long[]{0x0008000000000002L});
    public static final BitSet FOLLOW_51_in_ruleXOseeEnumEntry2526 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXOseeEnumEntry2543 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXOseeEnumOverride_in_entryRuleXOseeEnumOverride2586 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleXOseeEnumOverride2596 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_52_in_ruleXOseeEnumOverride2633 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXOseeEnumOverride2653 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_ruleXOseeEnumOverride2665 = new BitSet(new long[]{0x00E0000000400000L});
    public static final BitSet FOLLOW_53_in_ruleXOseeEnumOverride2683 = new BitSet(new long[]{0x00C0000000400000L});
    public static final BitSet FOLLOW_ruleOverrideOption_in_ruleXOseeEnumOverride2718 = new BitSet(new long[]{0x00C0000000400000L});
    public static final BitSet FOLLOW_22_in_ruleXOseeEnumOverride2731 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOverrideOption_in_entryRuleOverrideOption2767 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleOverrideOption2777 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAddEnum_in_ruleOverrideOption2824 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRemoveEnum_in_ruleOverrideOption2851 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAddEnum_in_entryRuleAddEnum2886 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAddEnum2896 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_54_in_ruleAddEnum2933 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAddEnum2950 = new BitSet(new long[]{0x0008000000000082L});
    public static final BitSet FOLLOW_RULE_WHOLE_NUM_STR_in_ruleAddEnum2972 = new BitSet(new long[]{0x0008000000000002L});
    public static final BitSet FOLLOW_51_in_ruleAddEnum2991 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAddEnum3008 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRemoveEnum_in_entryRuleRemoveEnum3051 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRemoveEnum3061 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_55_in_ruleRemoveEnum3098 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleRemoveEnum3118 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXRelationType_in_entryRuleXRelationType3154 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleXRelationType3164 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_56_in_ruleXRelationType3201 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXRelationType3218 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_ruleXRelationType3235 = new BitSet(new long[]{0x0000000000300000L});
    public static final BitSet FOLLOW_20_in_ruleXRelationType3248 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXRelationType3265 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_21_in_ruleXRelationType3284 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RULE_HEX_INT_in_ruleXRelationType3301 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_57_in_ruleXRelationType3318 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXRelationType3335 = new BitSet(new long[]{0x0400000000000000L});
    public static final BitSet FOLLOW_58_in_ruleXRelationType3352 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXRelationType3372 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_59_in_ruleXRelationType3384 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXRelationType3401 = new BitSet(new long[]{0x1000000000000000L});
    public static final BitSet FOLLOW_60_in_ruleXRelationType3418 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXRelationType3438 = new BitSet(new long[]{0x2000000000000000L});
    public static final BitSet FOLLOW_61_in_ruleXRelationType3450 = new BitSet(new long[]{0x8000000000000020L,0x0000000000000003L});
    public static final BitSet FOLLOW_ruleRelationOrderType_in_ruleXRelationType3471 = new BitSet(new long[]{0x4000000000000000L});
    public static final BitSet FOLLOW_62_in_ruleXRelationType3483 = new BitSet(new long[]{0x0000000000000000L,0x000000000000F000L});
    public static final BitSet FOLLOW_ruleRelationMultiplicityEnum_in_ruleXRelationType3504 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_22_in_ruleXRelationType3516 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelationOrderType_in_entryRuleRelationOrderType3553 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRelationOrderType3564 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_63_in_ruleRelationOrderType3602 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_64_in_ruleRelationOrderType3621 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_ruleRelationOrderType3640 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleRelationOrderType3661 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleCondition_in_entryRuleCondition3708 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleCondition3718 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleSimpleCondition_in_ruleCondition3765 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleCompoundCondition_in_ruleCondition3792 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleSimpleCondition_in_entryRuleSimpleCondition3827 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleSimpleCondition3837 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleMatchField_in_ruleSimpleCondition3883 = new BitSet(new long[]{0x0000000000000000L,0x0000000000030000L});
    public static final BitSet FOLLOW_ruleCompareOp_in_ruleSimpleCondition3904 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleSimpleCondition3921 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleCompoundCondition_in_entryRuleCompoundCondition3962 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleCompoundCondition3972 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_66_in_ruleCompoundCondition4009 = new BitSet(new long[]{0x0000000001000000L,0x0000000000700000L});
    public static final BitSet FOLLOW_ruleSimpleCondition_in_ruleCompoundCondition4030 = new BitSet(new long[]{0x0000000000000000L,0x00000000000C0000L});
    public static final BitSet FOLLOW_ruleXLogicOperator_in_ruleCompoundCondition4052 = new BitSet(new long[]{0x0000000001000000L,0x0000000000700000L});
    public static final BitSet FOLLOW_ruleSimpleCondition_in_ruleCompoundCondition4073 = new BitSet(new long[]{0x0000000000000000L,0x00000000000C0008L});
    public static final BitSet FOLLOW_67_in_ruleCompoundCondition4087 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXArtifactMatcher_in_entryRuleXArtifactMatcher4123 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleXArtifactMatcher4133 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_68_in_ruleXArtifactMatcher4170 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXArtifactMatcher4187 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_69_in_ruleXArtifactMatcher4204 = new BitSet(new long[]{0x0000000001000000L,0x0000000000700004L});
    public static final BitSet FOLLOW_ruleCondition_in_ruleXArtifactMatcher4225 = new BitSet(new long[]{0x0000000000000000L,0x00000000000C0040L});
    public static final BitSet FOLLOW_ruleXLogicOperator_in_ruleXArtifactMatcher4247 = new BitSet(new long[]{0x0000000001000000L,0x0000000000700004L});
    public static final BitSet FOLLOW_ruleCondition_in_ruleXArtifactMatcher4268 = new BitSet(new long[]{0x0000000000000000L,0x00000000000C0040L});
    public static final BitSet FOLLOW_70_in_ruleXArtifactMatcher4282 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAccessContext_in_entryRuleAccessContext4318 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAccessContext4328 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_71_in_ruleAccessContext4365 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAccessContext4382 = new BitSet(new long[]{0x00000000000A0000L});
    public static final BitSet FOLLOW_17_in_ruleAccessContext4400 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAccessContext4420 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_ruleAccessContext4434 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_ruleAccessContext4446 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAccessContext4463 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_70_in_ruleAccessContext4480 = new BitSet(new long[]{0x0000000000000000L,0x0000000001800100L});
    public static final BitSet FOLLOW_ruleObjectRestriction_in_ruleAccessContext4502 = new BitSet(new long[]{0x0000000000400000L,0x0000000001800100L});
    public static final BitSet FOLLOW_ruleHierarchyRestriction_in_ruleAccessContext4529 = new BitSet(new long[]{0x0000000000400000L,0x0000000001800100L});
    public static final BitSet FOLLOW_22_in_ruleAccessContext4543 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleHierarchyRestriction_in_entryRuleHierarchyRestriction4579 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleHierarchyRestriction4589 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_72_in_ruleHierarchyRestriction4626 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleHierarchyRestriction4646 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_ruleHierarchyRestriction4658 = new BitSet(new long[]{0x0000000000000000L,0x0000000001800000L});
    public static final BitSet FOLLOW_ruleObjectRestriction_in_ruleHierarchyRestriction4679 = new BitSet(new long[]{0x0000000000400000L,0x0000000001800000L});
    public static final BitSet FOLLOW_22_in_ruleHierarchyRestriction4692 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelationTypeArtifactTypePredicate_in_entryRuleRelationTypeArtifactTypePredicate4728 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRelationTypeArtifactTypePredicate4738 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_16_in_ruleRelationTypeArtifactTypePredicate4775 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleRelationTypeArtifactTypePredicate4795 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelationTypeArtifactPredicate_in_entryRuleRelationTypeArtifactPredicate4831 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRelationTypeArtifactPredicate4841 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_73_in_ruleRelationTypeArtifactPredicate4878 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleRelationTypeArtifactPredicate4898 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelationTypePredicate_in_entryRuleRelationTypePredicate4934 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRelationTypePredicate4944 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelationTypeArtifactPredicate_in_ruleRelationTypePredicate4991 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelationTypeArtifactTypePredicate_in_ruleRelationTypePredicate5018 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleObjectRestriction_in_entryRuleObjectRestriction5053 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleObjectRestriction5063 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleArtifactMatchRestriction_in_ruleObjectRestriction5110 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleArtifactTypeRestriction_in_ruleObjectRestriction5137 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelationTypeRestriction_in_ruleObjectRestriction5164 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeTypeRestriction_in_ruleObjectRestriction5191 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleArtifactMatchRestriction_in_entryRuleArtifactMatchRestriction5226 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleArtifactMatchRestriction5236 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAccessPermissionEnum_in_ruleArtifactMatchRestriction5282 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_74_in_ruleArtifactMatchRestriction5294 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_73_in_ruleArtifactMatchRestriction5306 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleArtifactMatchRestriction5326 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_70_in_ruleArtifactMatchRestriction5338 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleArtifactTypeRestriction_in_entryRuleArtifactTypeRestriction5374 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleArtifactTypeRestriction5384 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAccessPermissionEnum_in_ruleArtifactTypeRestriction5430 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_74_in_ruleArtifactTypeRestriction5442 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_16_in_ruleArtifactTypeRestriction5454 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleArtifactTypeRestriction5474 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_70_in_ruleArtifactTypeRestriction5486 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeTypeRestriction_in_entryRuleAttributeTypeRestriction5522 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttributeTypeRestriction5532 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAccessPermissionEnum_in_ruleAttributeTypeRestriction5578 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_74_in_ruleAttributeTypeRestriction5590 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_ruleAttributeTypeRestriction5602 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAttributeTypeRestriction5622 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000840L});
    public static final BitSet FOLLOW_75_in_ruleAttributeTypeRestriction5635 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_16_in_ruleAttributeTypeRestriction5647 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAttributeTypeRestriction5667 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_70_in_ruleAttributeTypeRestriction5681 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelationTypeRestriction_in_entryRuleRelationTypeRestriction5719 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRelationTypeRestriction5729 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAccessPermissionEnum_in_ruleRelationTypeRestriction5775 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_74_in_ruleRelationTypeRestriction5787 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_56_in_ruleRelationTypeRestriction5799 = new BitSet(new long[]{0x0000000000000010L,0x0000000002000000L});
    public static final BitSet FOLLOW_ruleRelationTypeMatch_in_ruleRelationTypeRestriction5821 = new BitSet(new long[]{0x0000000000000000L,0x000000001C000000L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleRelationTypeRestriction5847 = new BitSet(new long[]{0x0000000000000000L,0x000000001C000000L});
    public static final BitSet FOLLOW_ruleXRelationSideEnum_in_ruleRelationTypeRestriction5869 = new BitSet(new long[]{0x0000000000010000L,0x0000000000000240L});
    public static final BitSet FOLLOW_ruleRelationTypePredicate_in_ruleRelationTypeRestriction5890 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_70_in_ruleRelationTypeRestriction5903 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_76_in_ruleRelationMultiplicityEnum5953 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_77_in_ruleRelationMultiplicityEnum5970 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_78_in_ruleRelationMultiplicityEnum5987 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_79_in_ruleRelationMultiplicityEnum6004 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_80_in_ruleCompareOp6049 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_81_in_ruleCompareOp6066 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_82_in_ruleXLogicOperator6111 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_83_in_ruleXLogicOperator6128 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_84_in_ruleMatchField6173 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_85_in_ruleMatchField6190 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_86_in_ruleMatchField6207 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_24_in_ruleMatchField6224 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_87_in_ruleAccessPermissionEnum6269 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_88_in_ruleAccessPermissionEnum6286 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_89_in_ruleRelationTypeMatch6330 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_90_in_ruleXRelationSideEnum6374 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_91_in_ruleXRelationSideEnum6391 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_92_in_ruleXRelationSideEnum6408 = new BitSet(new long[]{0x0000000000000002L});

}