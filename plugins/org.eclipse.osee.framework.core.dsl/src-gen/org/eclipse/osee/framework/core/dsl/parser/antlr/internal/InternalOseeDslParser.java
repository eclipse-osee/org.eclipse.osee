package org.eclipse.osee.framework.core.dsl.parser.antlr.internal; 

import java.io.InputStream;
import org.eclipse.xtext.*;
import org.eclipse.xtext.parser.*;
import org.eclipse.xtext.parser.impl.*;
import org.eclipse.xtext.parsetree.*;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.xtext.parser.antlr.AbstractInternalAntlrParser;
import org.eclipse.xtext.parser.antlr.XtextTokenStream;
import org.eclipse.xtext.parser.antlr.XtextTokenStream.HiddenTokens;
import org.eclipse.xtext.parser.antlr.AntlrDatatypeRuleToken;
import org.eclipse.xtext.conversion.ValueConverterException;
import org.eclipse.osee.framework.core.dsl.services.OseeDslGrammarAccess;



import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class InternalOseeDslParser extends AbstractInternalAntlrParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE_STRING", "RULE_ID", "RULE_WHOLE_NUM_STR", "RULE_INT", "RULE_ML_COMMENT", "RULE_SL_COMMENT", "RULE_WS", "RULE_ANY_OTHER", "'import'", "'.'", "'abstract'", "'artifactType'", "'extends'", "','", "'{'", "'guid'", "'}'", "'attribute'", "'branchGuid'", "'attributeType'", "'overrides'", "'dataProvider'", "'DefaultAttributeDataProvider'", "'UriAttributeDataProvider'", "'min'", "'max'", "'unlimited'", "'taggerId'", "'DefaultAttributeTaggerProvider'", "'enumType'", "'description'", "'defaultValue'", "'fileExtension'", "'BooleanAttribute'", "'CompressedContentAttribute'", "'DateAttribute'", "'EnumeratedAttribute'", "'FloatingPointAttribute'", "'IntegerAttribute'", "'JavaObjectAttribute'", "'StringAttribute'", "'WordAttribute'", "'oseeEnumType'", "'entry'", "'entryGuid'", "'overrides enum'", "'inheritAll'", "'add'", "'remove'", "'relationType'", "'sideAName'", "'sideAArtifactType'", "'sideBName'", "'sideBArtifactType'", "'defaultOrderType'", "'multiplicity'", "'Lexicographical_Ascending'", "'Lexicographical_Descending'", "'Unordered'", "'artifact'", "'artGuid'", "';'", "'branch'", "'accessContext'", "'childrenOf'", "'edit'", "'of'", "'ONE_TO_ONE'", "'ONE_TO_MANY'", "'MANY_TO_ONE'", "'MANY_TO_MANY'", "'ALLOW'", "'DENY'", "'SIDE_A'", "'SIDE_B'", "'BOTH'"
    };
    public static final int RULE_ID=5;
    public static final int RULE_STRING=4;
    public static final int RULE_WHOLE_NUM_STR=6;
    public static final int RULE_ANY_OTHER=11;
    public static final int RULE_INT=7;
    public static final int RULE_WS=10;
    public static final int RULE_SL_COMMENT=9;
    public static final int EOF=-1;
    public static final int RULE_ML_COMMENT=8;

        public InternalOseeDslParser(TokenStream input) {
            super(input);
        }
        

    public String[] getTokenNames() { return tokenNames; }
    public String getGrammarFileName() { return "../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g"; }



     	private OseeDslGrammarAccess grammarAccess;
     	
        public InternalOseeDslParser(TokenStream input, IAstFactory factory, OseeDslGrammarAccess grammarAccess) {
            this(input);
            this.factory = factory;
            registerRules(grammarAccess.getGrammar());
            this.grammarAccess = grammarAccess;
        }
        
        @Override
        protected InputStream getTokenFile() {
        	ClassLoader classLoader = getClass().getClassLoader();
        	return classLoader.getResourceAsStream("org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.tokens");
        }
        
        @Override
        protected String getFirstRuleName() {
        	return "OseeDsl";	
       	}
       	
       	@Override
       	protected OseeDslGrammarAccess getGrammarAccess() {
       		return grammarAccess;
       	}



    // $ANTLR start entryRuleOseeDsl
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:78:1: entryRuleOseeDsl returns [EObject current=null] : iv_ruleOseeDsl= ruleOseeDsl EOF ;
    public final EObject entryRuleOseeDsl() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOseeDsl = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:79:2: (iv_ruleOseeDsl= ruleOseeDsl EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:80:2: iv_ruleOseeDsl= ruleOseeDsl EOF
            {
             currentNode = createCompositeNode(grammarAccess.getOseeDslRule(), currentNode); 
            pushFollow(FOLLOW_ruleOseeDsl_in_entryRuleOseeDsl75);
            iv_ruleOseeDsl=ruleOseeDsl();
            _fsp--;

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
    // $ANTLR end entryRuleOseeDsl


    // $ANTLR start ruleOseeDsl
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:87:1: ruleOseeDsl returns [EObject current=null] : ( ( (lv_imports_0_0= ruleImport ) )* ( ( (lv_artifactTypes_1_0= ruleXArtifactType ) ) | ( (lv_relationTypes_2_0= ruleXRelationType ) ) | ( (lv_attributeTypes_3_0= ruleXAttributeType ) ) | ( (lv_enumTypes_4_0= ruleXOseeEnumType ) ) | ( (lv_enumOverrides_5_0= ruleXOseeEnumOverride ) ) )* ( ( (lv_branchRefs_6_0= ruleXBranchRef ) ) | ( (lv_artifactRefs_7_0= ruleXArtifactRef ) ) )* ( (lv_accessDeclarations_8_0= ruleAccessContext ) )* ) ;
    public final EObject ruleOseeDsl() throws RecognitionException {
        EObject current = null;

        EObject lv_imports_0_0 = null;

        EObject lv_artifactTypes_1_0 = null;

        EObject lv_relationTypes_2_0 = null;

        EObject lv_attributeTypes_3_0 = null;

        EObject lv_enumTypes_4_0 = null;

        EObject lv_enumOverrides_5_0 = null;

        EObject lv_branchRefs_6_0 = null;

        EObject lv_artifactRefs_7_0 = null;

        EObject lv_accessDeclarations_8_0 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:92:6: ( ( ( (lv_imports_0_0= ruleImport ) )* ( ( (lv_artifactTypes_1_0= ruleXArtifactType ) ) | ( (lv_relationTypes_2_0= ruleXRelationType ) ) | ( (lv_attributeTypes_3_0= ruleXAttributeType ) ) | ( (lv_enumTypes_4_0= ruleXOseeEnumType ) ) | ( (lv_enumOverrides_5_0= ruleXOseeEnumOverride ) ) )* ( ( (lv_branchRefs_6_0= ruleXBranchRef ) ) | ( (lv_artifactRefs_7_0= ruleXArtifactRef ) ) )* ( (lv_accessDeclarations_8_0= ruleAccessContext ) )* ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:93:1: ( ( (lv_imports_0_0= ruleImport ) )* ( ( (lv_artifactTypes_1_0= ruleXArtifactType ) ) | ( (lv_relationTypes_2_0= ruleXRelationType ) ) | ( (lv_attributeTypes_3_0= ruleXAttributeType ) ) | ( (lv_enumTypes_4_0= ruleXOseeEnumType ) ) | ( (lv_enumOverrides_5_0= ruleXOseeEnumOverride ) ) )* ( ( (lv_branchRefs_6_0= ruleXBranchRef ) ) | ( (lv_artifactRefs_7_0= ruleXArtifactRef ) ) )* ( (lv_accessDeclarations_8_0= ruleAccessContext ) )* )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:93:1: ( ( (lv_imports_0_0= ruleImport ) )* ( ( (lv_artifactTypes_1_0= ruleXArtifactType ) ) | ( (lv_relationTypes_2_0= ruleXRelationType ) ) | ( (lv_attributeTypes_3_0= ruleXAttributeType ) ) | ( (lv_enumTypes_4_0= ruleXOseeEnumType ) ) | ( (lv_enumOverrides_5_0= ruleXOseeEnumOverride ) ) )* ( ( (lv_branchRefs_6_0= ruleXBranchRef ) ) | ( (lv_artifactRefs_7_0= ruleXArtifactRef ) ) )* ( (lv_accessDeclarations_8_0= ruleAccessContext ) )* )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:93:2: ( (lv_imports_0_0= ruleImport ) )* ( ( (lv_artifactTypes_1_0= ruleXArtifactType ) ) | ( (lv_relationTypes_2_0= ruleXRelationType ) ) | ( (lv_attributeTypes_3_0= ruleXAttributeType ) ) | ( (lv_enumTypes_4_0= ruleXOseeEnumType ) ) | ( (lv_enumOverrides_5_0= ruleXOseeEnumOverride ) ) )* ( ( (lv_branchRefs_6_0= ruleXBranchRef ) ) | ( (lv_artifactRefs_7_0= ruleXArtifactRef ) ) )* ( (lv_accessDeclarations_8_0= ruleAccessContext ) )*
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:93:2: ( (lv_imports_0_0= ruleImport ) )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==12) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:94:1: (lv_imports_0_0= ruleImport )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:94:1: (lv_imports_0_0= ruleImport )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:95:3: lv_imports_0_0= ruleImport
            	    {
            	     
            	    	        currentNode=createCompositeNode(grammarAccess.getOseeDslAccess().getImportsImportParserRuleCall_0_0(), currentNode); 
            	    	    
            	    pushFollow(FOLLOW_ruleImport_in_ruleOseeDsl131);
            	    lv_imports_0_0=ruleImport();
            	    _fsp--;


            	    	        if (current==null) {
            	    	            current = factory.create(grammarAccess.getOseeDslRule().getType().getClassifier());
            	    	            associateNodeWithAstElement(currentNode.getParent(), current);
            	    	        }
            	    	        try {
            	    	       		add(
            	    	       			current, 
            	    	       			"imports",
            	    	        		lv_imports_0_0, 
            	    	        		"Import", 
            	    	        		currentNode);
            	    	        } catch (ValueConverterException vce) {
            	    				handleValueConverterException(vce);
            	    	        }
            	    	        currentNode = currentNode.getParent();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:117:3: ( ( (lv_artifactTypes_1_0= ruleXArtifactType ) ) | ( (lv_relationTypes_2_0= ruleXRelationType ) ) | ( (lv_attributeTypes_3_0= ruleXAttributeType ) ) | ( (lv_enumTypes_4_0= ruleXOseeEnumType ) ) | ( (lv_enumOverrides_5_0= ruleXOseeEnumOverride ) ) )*
            loop2:
            do {
                int alt2=6;
                switch ( input.LA(1) ) {
                case 14:
                case 15:
                    {
                    alt2=1;
                    }
                    break;
                case 53:
                    {
                    alt2=2;
                    }
                    break;
                case 23:
                    {
                    alt2=3;
                    }
                    break;
                case 46:
                    {
                    alt2=4;
                    }
                    break;
                case 49:
                    {
                    alt2=5;
                    }
                    break;

                }

                switch (alt2) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:117:4: ( (lv_artifactTypes_1_0= ruleXArtifactType ) )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:117:4: ( (lv_artifactTypes_1_0= ruleXArtifactType ) )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:118:1: (lv_artifactTypes_1_0= ruleXArtifactType )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:118:1: (lv_artifactTypes_1_0= ruleXArtifactType )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:119:3: lv_artifactTypes_1_0= ruleXArtifactType
            	    {
            	     
            	    	        currentNode=createCompositeNode(grammarAccess.getOseeDslAccess().getArtifactTypesXArtifactTypeParserRuleCall_1_0_0(), currentNode); 
            	    	    
            	    pushFollow(FOLLOW_ruleXArtifactType_in_ruleOseeDsl154);
            	    lv_artifactTypes_1_0=ruleXArtifactType();
            	    _fsp--;


            	    	        if (current==null) {
            	    	            current = factory.create(grammarAccess.getOseeDslRule().getType().getClassifier());
            	    	            associateNodeWithAstElement(currentNode.getParent(), current);
            	    	        }
            	    	        try {
            	    	       		add(
            	    	       			current, 
            	    	       			"artifactTypes",
            	    	        		lv_artifactTypes_1_0, 
            	    	        		"XArtifactType", 
            	    	        		currentNode);
            	    	        } catch (ValueConverterException vce) {
            	    				handleValueConverterException(vce);
            	    	        }
            	    	        currentNode = currentNode.getParent();
            	    	    

            	    }


            	    }


            	    }
            	    break;
            	case 2 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:142:6: ( (lv_relationTypes_2_0= ruleXRelationType ) )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:142:6: ( (lv_relationTypes_2_0= ruleXRelationType ) )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:143:1: (lv_relationTypes_2_0= ruleXRelationType )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:143:1: (lv_relationTypes_2_0= ruleXRelationType )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:144:3: lv_relationTypes_2_0= ruleXRelationType
            	    {
            	     
            	    	        currentNode=createCompositeNode(grammarAccess.getOseeDslAccess().getRelationTypesXRelationTypeParserRuleCall_1_1_0(), currentNode); 
            	    	    
            	    pushFollow(FOLLOW_ruleXRelationType_in_ruleOseeDsl181);
            	    lv_relationTypes_2_0=ruleXRelationType();
            	    _fsp--;


            	    	        if (current==null) {
            	    	            current = factory.create(grammarAccess.getOseeDslRule().getType().getClassifier());
            	    	            associateNodeWithAstElement(currentNode.getParent(), current);
            	    	        }
            	    	        try {
            	    	       		add(
            	    	       			current, 
            	    	       			"relationTypes",
            	    	        		lv_relationTypes_2_0, 
            	    	        		"XRelationType", 
            	    	        		currentNode);
            	    	        } catch (ValueConverterException vce) {
            	    				handleValueConverterException(vce);
            	    	        }
            	    	        currentNode = currentNode.getParent();
            	    	    

            	    }


            	    }


            	    }
            	    break;
            	case 3 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:167:6: ( (lv_attributeTypes_3_0= ruleXAttributeType ) )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:167:6: ( (lv_attributeTypes_3_0= ruleXAttributeType ) )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:168:1: (lv_attributeTypes_3_0= ruleXAttributeType )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:168:1: (lv_attributeTypes_3_0= ruleXAttributeType )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:169:3: lv_attributeTypes_3_0= ruleXAttributeType
            	    {
            	     
            	    	        currentNode=createCompositeNode(grammarAccess.getOseeDslAccess().getAttributeTypesXAttributeTypeParserRuleCall_1_2_0(), currentNode); 
            	    	    
            	    pushFollow(FOLLOW_ruleXAttributeType_in_ruleOseeDsl208);
            	    lv_attributeTypes_3_0=ruleXAttributeType();
            	    _fsp--;


            	    	        if (current==null) {
            	    	            current = factory.create(grammarAccess.getOseeDslRule().getType().getClassifier());
            	    	            associateNodeWithAstElement(currentNode.getParent(), current);
            	    	        }
            	    	        try {
            	    	       		add(
            	    	       			current, 
            	    	       			"attributeTypes",
            	    	        		lv_attributeTypes_3_0, 
            	    	        		"XAttributeType", 
            	    	        		currentNode);
            	    	        } catch (ValueConverterException vce) {
            	    				handleValueConverterException(vce);
            	    	        }
            	    	        currentNode = currentNode.getParent();
            	    	    

            	    }


            	    }


            	    }
            	    break;
            	case 4 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:192:6: ( (lv_enumTypes_4_0= ruleXOseeEnumType ) )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:192:6: ( (lv_enumTypes_4_0= ruleXOseeEnumType ) )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:193:1: (lv_enumTypes_4_0= ruleXOseeEnumType )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:193:1: (lv_enumTypes_4_0= ruleXOseeEnumType )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:194:3: lv_enumTypes_4_0= ruleXOseeEnumType
            	    {
            	     
            	    	        currentNode=createCompositeNode(grammarAccess.getOseeDslAccess().getEnumTypesXOseeEnumTypeParserRuleCall_1_3_0(), currentNode); 
            	    	    
            	    pushFollow(FOLLOW_ruleXOseeEnumType_in_ruleOseeDsl235);
            	    lv_enumTypes_4_0=ruleXOseeEnumType();
            	    _fsp--;


            	    	        if (current==null) {
            	    	            current = factory.create(grammarAccess.getOseeDslRule().getType().getClassifier());
            	    	            associateNodeWithAstElement(currentNode.getParent(), current);
            	    	        }
            	    	        try {
            	    	       		add(
            	    	       			current, 
            	    	       			"enumTypes",
            	    	        		lv_enumTypes_4_0, 
            	    	        		"XOseeEnumType", 
            	    	        		currentNode);
            	    	        } catch (ValueConverterException vce) {
            	    				handleValueConverterException(vce);
            	    	        }
            	    	        currentNode = currentNode.getParent();
            	    	    

            	    }


            	    }


            	    }
            	    break;
            	case 5 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:217:6: ( (lv_enumOverrides_5_0= ruleXOseeEnumOverride ) )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:217:6: ( (lv_enumOverrides_5_0= ruleXOseeEnumOverride ) )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:218:1: (lv_enumOverrides_5_0= ruleXOseeEnumOverride )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:218:1: (lv_enumOverrides_5_0= ruleXOseeEnumOverride )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:219:3: lv_enumOverrides_5_0= ruleXOseeEnumOverride
            	    {
            	     
            	    	        currentNode=createCompositeNode(grammarAccess.getOseeDslAccess().getEnumOverridesXOseeEnumOverrideParserRuleCall_1_4_0(), currentNode); 
            	    	    
            	    pushFollow(FOLLOW_ruleXOseeEnumOverride_in_ruleOseeDsl262);
            	    lv_enumOverrides_5_0=ruleXOseeEnumOverride();
            	    _fsp--;


            	    	        if (current==null) {
            	    	            current = factory.create(grammarAccess.getOseeDslRule().getType().getClassifier());
            	    	            associateNodeWithAstElement(currentNode.getParent(), current);
            	    	        }
            	    	        try {
            	    	       		add(
            	    	       			current, 
            	    	       			"enumOverrides",
            	    	        		lv_enumOverrides_5_0, 
            	    	        		"XOseeEnumOverride", 
            	    	        		currentNode);
            	    	        } catch (ValueConverterException vce) {
            	    				handleValueConverterException(vce);
            	    	        }
            	    	        currentNode = currentNode.getParent();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:241:4: ( ( (lv_branchRefs_6_0= ruleXBranchRef ) ) | ( (lv_artifactRefs_7_0= ruleXArtifactRef ) ) )*
            loop3:
            do {
                int alt3=3;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==66) ) {
                    alt3=1;
                }
                else if ( (LA3_0==63) ) {
                    alt3=2;
                }


                switch (alt3) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:241:5: ( (lv_branchRefs_6_0= ruleXBranchRef ) )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:241:5: ( (lv_branchRefs_6_0= ruleXBranchRef ) )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:242:1: (lv_branchRefs_6_0= ruleXBranchRef )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:242:1: (lv_branchRefs_6_0= ruleXBranchRef )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:243:3: lv_branchRefs_6_0= ruleXBranchRef
            	    {
            	     
            	    	        currentNode=createCompositeNode(grammarAccess.getOseeDslAccess().getBranchRefsXBranchRefParserRuleCall_2_0_0(), currentNode); 
            	    	    
            	    pushFollow(FOLLOW_ruleXBranchRef_in_ruleOseeDsl286);
            	    lv_branchRefs_6_0=ruleXBranchRef();
            	    _fsp--;


            	    	        if (current==null) {
            	    	            current = factory.create(grammarAccess.getOseeDslRule().getType().getClassifier());
            	    	            associateNodeWithAstElement(currentNode.getParent(), current);
            	    	        }
            	    	        try {
            	    	       		add(
            	    	       			current, 
            	    	       			"branchRefs",
            	    	        		lv_branchRefs_6_0, 
            	    	        		"XBranchRef", 
            	    	        		currentNode);
            	    	        } catch (ValueConverterException vce) {
            	    				handleValueConverterException(vce);
            	    	        }
            	    	        currentNode = currentNode.getParent();
            	    	    

            	    }


            	    }


            	    }
            	    break;
            	case 2 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:266:6: ( (lv_artifactRefs_7_0= ruleXArtifactRef ) )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:266:6: ( (lv_artifactRefs_7_0= ruleXArtifactRef ) )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:267:1: (lv_artifactRefs_7_0= ruleXArtifactRef )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:267:1: (lv_artifactRefs_7_0= ruleXArtifactRef )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:268:3: lv_artifactRefs_7_0= ruleXArtifactRef
            	    {
            	     
            	    	        currentNode=createCompositeNode(grammarAccess.getOseeDslAccess().getArtifactRefsXArtifactRefParserRuleCall_2_1_0(), currentNode); 
            	    	    
            	    pushFollow(FOLLOW_ruleXArtifactRef_in_ruleOseeDsl313);
            	    lv_artifactRefs_7_0=ruleXArtifactRef();
            	    _fsp--;


            	    	        if (current==null) {
            	    	            current = factory.create(grammarAccess.getOseeDslRule().getType().getClassifier());
            	    	            associateNodeWithAstElement(currentNode.getParent(), current);
            	    	        }
            	    	        try {
            	    	       		add(
            	    	       			current, 
            	    	       			"artifactRefs",
            	    	        		lv_artifactRefs_7_0, 
            	    	        		"XArtifactRef", 
            	    	        		currentNode);
            	    	        } catch (ValueConverterException vce) {
            	    				handleValueConverterException(vce);
            	    	        }
            	    	        currentNode = currentNode.getParent();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:290:4: ( (lv_accessDeclarations_8_0= ruleAccessContext ) )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==67) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:291:1: (lv_accessDeclarations_8_0= ruleAccessContext )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:291:1: (lv_accessDeclarations_8_0= ruleAccessContext )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:292:3: lv_accessDeclarations_8_0= ruleAccessContext
            	    {
            	     
            	    	        currentNode=createCompositeNode(grammarAccess.getOseeDslAccess().getAccessDeclarationsAccessContextParserRuleCall_3_0(), currentNode); 
            	    	    
            	    pushFollow(FOLLOW_ruleAccessContext_in_ruleOseeDsl336);
            	    lv_accessDeclarations_8_0=ruleAccessContext();
            	    _fsp--;


            	    	        if (current==null) {
            	    	            current = factory.create(grammarAccess.getOseeDslRule().getType().getClassifier());
            	    	            associateNodeWithAstElement(currentNode.getParent(), current);
            	    	        }
            	    	        try {
            	    	       		add(
            	    	       			current, 
            	    	       			"accessDeclarations",
            	    	        		lv_accessDeclarations_8_0, 
            	    	        		"AccessContext", 
            	    	        		currentNode);
            	    	        } catch (ValueConverterException vce) {
            	    				handleValueConverterException(vce);
            	    	        }
            	    	        currentNode = currentNode.getParent();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);


            }


            }

             resetLookahead(); 
                	lastConsumedNode = currentNode;
                
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end ruleOseeDsl


    // $ANTLR start entryRuleImport
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:322:1: entryRuleImport returns [EObject current=null] : iv_ruleImport= ruleImport EOF ;
    public final EObject entryRuleImport() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleImport = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:323:2: (iv_ruleImport= ruleImport EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:324:2: iv_ruleImport= ruleImport EOF
            {
             currentNode = createCompositeNode(grammarAccess.getImportRule(), currentNode); 
            pushFollow(FOLLOW_ruleImport_in_entryRuleImport373);
            iv_ruleImport=ruleImport();
            _fsp--;

             current =iv_ruleImport; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleImport383); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end entryRuleImport


    // $ANTLR start ruleImport
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:331:1: ruleImport returns [EObject current=null] : ( 'import' ( (lv_importURI_1_0= RULE_STRING ) ) ) ;
    public final EObject ruleImport() throws RecognitionException {
        EObject current = null;

        Token lv_importURI_1_0=null;

         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:336:6: ( ( 'import' ( (lv_importURI_1_0= RULE_STRING ) ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:337:1: ( 'import' ( (lv_importURI_1_0= RULE_STRING ) ) )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:337:1: ( 'import' ( (lv_importURI_1_0= RULE_STRING ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:337:3: 'import' ( (lv_importURI_1_0= RULE_STRING ) )
            {
            match(input,12,FOLLOW_12_in_ruleImport418); 

                    createLeafNode(grammarAccess.getImportAccess().getImportKeyword_0(), null); 
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:341:1: ( (lv_importURI_1_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:342:1: (lv_importURI_1_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:342:1: (lv_importURI_1_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:343:3: lv_importURI_1_0= RULE_STRING
            {
            lv_importURI_1_0=(Token)input.LT(1);
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleImport435); 

            			createLeafNode(grammarAccess.getImportAccess().getImportURISTRINGTerminalRuleCall_1_0(), "importURI"); 
            		

            	        if (current==null) {
            	            current = factory.create(grammarAccess.getImportRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
            	        try {
            	       		set(
            	       			current, 
            	       			"importURI",
            	        		lv_importURI_1_0, 
            	        		"STRING", 
            	        		lastConsumedNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	    

            }


            }


            }


            }

             resetLookahead(); 
                	lastConsumedNode = currentNode;
                
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end ruleImport


    // $ANTLR start entryRuleATTRIBUTE_TYPE_REFERENCE
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:375:1: entryRuleATTRIBUTE_TYPE_REFERENCE returns [String current=null] : iv_ruleATTRIBUTE_TYPE_REFERENCE= ruleATTRIBUTE_TYPE_REFERENCE EOF ;
    public final String entryRuleATTRIBUTE_TYPE_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleATTRIBUTE_TYPE_REFERENCE = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:376:2: (iv_ruleATTRIBUTE_TYPE_REFERENCE= ruleATTRIBUTE_TYPE_REFERENCE EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:377:2: iv_ruleATTRIBUTE_TYPE_REFERENCE= ruleATTRIBUTE_TYPE_REFERENCE EOF
            {
             currentNode = createCompositeNode(grammarAccess.getATTRIBUTE_TYPE_REFERENCERule(), currentNode); 
            pushFollow(FOLLOW_ruleATTRIBUTE_TYPE_REFERENCE_in_entryRuleATTRIBUTE_TYPE_REFERENCE479);
            iv_ruleATTRIBUTE_TYPE_REFERENCE=ruleATTRIBUTE_TYPE_REFERENCE();
            _fsp--;

             current =iv_ruleATTRIBUTE_TYPE_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleATTRIBUTE_TYPE_REFERENCE490); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end entryRuleATTRIBUTE_TYPE_REFERENCE


    // $ANTLR start ruleATTRIBUTE_TYPE_REFERENCE
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:384:1: ruleATTRIBUTE_TYPE_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleATTRIBUTE_TYPE_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:389:6: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:390:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)input.LT(1);
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleATTRIBUTE_TYPE_REFERENCE529); 

            		current.merge(this_STRING_0);
                
             
                createLeafNode(grammarAccess.getATTRIBUTE_TYPE_REFERENCEAccess().getSTRINGTerminalRuleCall(), null); 
                

            }

             resetLookahead(); 
            	    lastConsumedNode = currentNode;
                
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end ruleATTRIBUTE_TYPE_REFERENCE


    // $ANTLR start entryRuleARTIFACT_TYPE_REFERENCE
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:405:1: entryRuleARTIFACT_TYPE_REFERENCE returns [String current=null] : iv_ruleARTIFACT_TYPE_REFERENCE= ruleARTIFACT_TYPE_REFERENCE EOF ;
    public final String entryRuleARTIFACT_TYPE_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleARTIFACT_TYPE_REFERENCE = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:406:2: (iv_ruleARTIFACT_TYPE_REFERENCE= ruleARTIFACT_TYPE_REFERENCE EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:407:2: iv_ruleARTIFACT_TYPE_REFERENCE= ruleARTIFACT_TYPE_REFERENCE EOF
            {
             currentNode = createCompositeNode(grammarAccess.getARTIFACT_TYPE_REFERENCERule(), currentNode); 
            pushFollow(FOLLOW_ruleARTIFACT_TYPE_REFERENCE_in_entryRuleARTIFACT_TYPE_REFERENCE574);
            iv_ruleARTIFACT_TYPE_REFERENCE=ruleARTIFACT_TYPE_REFERENCE();
            _fsp--;

             current =iv_ruleARTIFACT_TYPE_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleARTIFACT_TYPE_REFERENCE585); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end entryRuleARTIFACT_TYPE_REFERENCE


    // $ANTLR start ruleARTIFACT_TYPE_REFERENCE
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:414:1: ruleARTIFACT_TYPE_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleARTIFACT_TYPE_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:419:6: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:420:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)input.LT(1);
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleARTIFACT_TYPE_REFERENCE624); 

            		current.merge(this_STRING_0);
                
             
                createLeafNode(grammarAccess.getARTIFACT_TYPE_REFERENCEAccess().getSTRINGTerminalRuleCall(), null); 
                

            }

             resetLookahead(); 
            	    lastConsumedNode = currentNode;
                
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end ruleARTIFACT_TYPE_REFERENCE


    // $ANTLR start entryRuleRELATION_TYPE_REFERENCE
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:435:1: entryRuleRELATION_TYPE_REFERENCE returns [String current=null] : iv_ruleRELATION_TYPE_REFERENCE= ruleRELATION_TYPE_REFERENCE EOF ;
    public final String entryRuleRELATION_TYPE_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleRELATION_TYPE_REFERENCE = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:436:2: (iv_ruleRELATION_TYPE_REFERENCE= ruleRELATION_TYPE_REFERENCE EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:437:2: iv_ruleRELATION_TYPE_REFERENCE= ruleRELATION_TYPE_REFERENCE EOF
            {
             currentNode = createCompositeNode(grammarAccess.getRELATION_TYPE_REFERENCERule(), currentNode); 
            pushFollow(FOLLOW_ruleRELATION_TYPE_REFERENCE_in_entryRuleRELATION_TYPE_REFERENCE669);
            iv_ruleRELATION_TYPE_REFERENCE=ruleRELATION_TYPE_REFERENCE();
            _fsp--;

             current =iv_ruleRELATION_TYPE_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleRELATION_TYPE_REFERENCE680); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end entryRuleRELATION_TYPE_REFERENCE


    // $ANTLR start ruleRELATION_TYPE_REFERENCE
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:444:1: ruleRELATION_TYPE_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleRELATION_TYPE_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:449:6: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:450:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)input.LT(1);
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleRELATION_TYPE_REFERENCE719); 

            		current.merge(this_STRING_0);
                
             
                createLeafNode(grammarAccess.getRELATION_TYPE_REFERENCEAccess().getSTRINGTerminalRuleCall(), null); 
                

            }

             resetLookahead(); 
            	    lastConsumedNode = currentNode;
                
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end ruleRELATION_TYPE_REFERENCE


    // $ANTLR start entryRuleENUM_TYPE_REFERENCE
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:465:1: entryRuleENUM_TYPE_REFERENCE returns [String current=null] : iv_ruleENUM_TYPE_REFERENCE= ruleENUM_TYPE_REFERENCE EOF ;
    public final String entryRuleENUM_TYPE_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleENUM_TYPE_REFERENCE = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:466:2: (iv_ruleENUM_TYPE_REFERENCE= ruleENUM_TYPE_REFERENCE EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:467:2: iv_ruleENUM_TYPE_REFERENCE= ruleENUM_TYPE_REFERENCE EOF
            {
             currentNode = createCompositeNode(grammarAccess.getENUM_TYPE_REFERENCERule(), currentNode); 
            pushFollow(FOLLOW_ruleENUM_TYPE_REFERENCE_in_entryRuleENUM_TYPE_REFERENCE764);
            iv_ruleENUM_TYPE_REFERENCE=ruleENUM_TYPE_REFERENCE();
            _fsp--;

             current =iv_ruleENUM_TYPE_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleENUM_TYPE_REFERENCE775); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end entryRuleENUM_TYPE_REFERENCE


    // $ANTLR start ruleENUM_TYPE_REFERENCE
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:474:1: ruleENUM_TYPE_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleENUM_TYPE_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:479:6: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:480:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)input.LT(1);
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleENUM_TYPE_REFERENCE814); 

            		current.merge(this_STRING_0);
                
             
                createLeafNode(grammarAccess.getENUM_TYPE_REFERENCEAccess().getSTRINGTerminalRuleCall(), null); 
                

            }

             resetLookahead(); 
            	    lastConsumedNode = currentNode;
                
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end ruleENUM_TYPE_REFERENCE


    // $ANTLR start entryRuleENUM_ENTRY_TYPE_REFERENCE
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:495:1: entryRuleENUM_ENTRY_TYPE_REFERENCE returns [String current=null] : iv_ruleENUM_ENTRY_TYPE_REFERENCE= ruleENUM_ENTRY_TYPE_REFERENCE EOF ;
    public final String entryRuleENUM_ENTRY_TYPE_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleENUM_ENTRY_TYPE_REFERENCE = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:496:2: (iv_ruleENUM_ENTRY_TYPE_REFERENCE= ruleENUM_ENTRY_TYPE_REFERENCE EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:497:2: iv_ruleENUM_ENTRY_TYPE_REFERENCE= ruleENUM_ENTRY_TYPE_REFERENCE EOF
            {
             currentNode = createCompositeNode(grammarAccess.getENUM_ENTRY_TYPE_REFERENCERule(), currentNode); 
            pushFollow(FOLLOW_ruleENUM_ENTRY_TYPE_REFERENCE_in_entryRuleENUM_ENTRY_TYPE_REFERENCE859);
            iv_ruleENUM_ENTRY_TYPE_REFERENCE=ruleENUM_ENTRY_TYPE_REFERENCE();
            _fsp--;

             current =iv_ruleENUM_ENTRY_TYPE_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleENUM_ENTRY_TYPE_REFERENCE870); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end entryRuleENUM_ENTRY_TYPE_REFERENCE


    // $ANTLR start ruleENUM_ENTRY_TYPE_REFERENCE
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:504:1: ruleENUM_ENTRY_TYPE_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleENUM_ENTRY_TYPE_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:509:6: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:510:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)input.LT(1);
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleENUM_ENTRY_TYPE_REFERENCE909); 

            		current.merge(this_STRING_0);
                
             
                createLeafNode(grammarAccess.getENUM_ENTRY_TYPE_REFERENCEAccess().getSTRINGTerminalRuleCall(), null); 
                

            }

             resetLookahead(); 
            	    lastConsumedNode = currentNode;
                
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end ruleENUM_ENTRY_TYPE_REFERENCE


    // $ANTLR start entryRuleQUALIFIED_NAME
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:525:1: entryRuleQUALIFIED_NAME returns [String current=null] : iv_ruleQUALIFIED_NAME= ruleQUALIFIED_NAME EOF ;
    public final String entryRuleQUALIFIED_NAME() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleQUALIFIED_NAME = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:526:2: (iv_ruleQUALIFIED_NAME= ruleQUALIFIED_NAME EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:527:2: iv_ruleQUALIFIED_NAME= ruleQUALIFIED_NAME EOF
            {
             currentNode = createCompositeNode(grammarAccess.getQUALIFIED_NAMERule(), currentNode); 
            pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_entryRuleQUALIFIED_NAME954);
            iv_ruleQUALIFIED_NAME=ruleQUALIFIED_NAME();
            _fsp--;

             current =iv_ruleQUALIFIED_NAME.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleQUALIFIED_NAME965); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end entryRuleQUALIFIED_NAME


    // $ANTLR start ruleQUALIFIED_NAME
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:534:1: ruleQUALIFIED_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* ) ;
    public final AntlrDatatypeRuleToken ruleQUALIFIED_NAME() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_ID_0=null;
        Token kw=null;
        Token this_ID_2=null;

         setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:539:6: ( (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:540:1: (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:540:1: (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:540:6: this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )*
            {
            this_ID_0=(Token)input.LT(1);
            match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleQUALIFIED_NAME1005); 

            		current.merge(this_ID_0);
                
             
                createLeafNode(grammarAccess.getQUALIFIED_NAMEAccess().getIDTerminalRuleCall_0(), null); 
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:547:1: (kw= '.' this_ID_2= RULE_ID )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==13) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:548:2: kw= '.' this_ID_2= RULE_ID
            	    {
            	    kw=(Token)input.LT(1);
            	    match(input,13,FOLLOW_13_in_ruleQUALIFIED_NAME1024); 

            	            current.merge(kw);
            	            createLeafNode(grammarAccess.getQUALIFIED_NAMEAccess().getFullStopKeyword_1_0(), null); 
            	        
            	    this_ID_2=(Token)input.LT(1);
            	    match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleQUALIFIED_NAME1039); 

            	    		current.merge(this_ID_2);
            	        
            	     
            	        createLeafNode(grammarAccess.getQUALIFIED_NAMEAccess().getIDTerminalRuleCall_1_1(), null); 
            	        

            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);


            }


            }

             resetLookahead(); 
            	    lastConsumedNode = currentNode;
                
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end ruleQUALIFIED_NAME


    // $ANTLR start entryRuleOseeType
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:570:1: entryRuleOseeType returns [EObject current=null] : iv_ruleOseeType= ruleOseeType EOF ;
    public final EObject entryRuleOseeType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOseeType = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:571:2: (iv_ruleOseeType= ruleOseeType EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:572:2: iv_ruleOseeType= ruleOseeType EOF
            {
             currentNode = createCompositeNode(grammarAccess.getOseeTypeRule(), currentNode); 
            pushFollow(FOLLOW_ruleOseeType_in_entryRuleOseeType1088);
            iv_ruleOseeType=ruleOseeType();
            _fsp--;

             current =iv_ruleOseeType; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleOseeType1098); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end entryRuleOseeType


    // $ANTLR start ruleOseeType
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:579:1: ruleOseeType returns [EObject current=null] : (this_XArtifactType_0= ruleXArtifactType | this_XRelationType_1= ruleXRelationType | this_XAttributeType_2= ruleXAttributeType | this_XOseeEnumType_3= ruleXOseeEnumType ) ;
    public final EObject ruleOseeType() throws RecognitionException {
        EObject current = null;

        EObject this_XArtifactType_0 = null;

        EObject this_XRelationType_1 = null;

        EObject this_XAttributeType_2 = null;

        EObject this_XOseeEnumType_3 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:584:6: ( (this_XArtifactType_0= ruleXArtifactType | this_XRelationType_1= ruleXRelationType | this_XAttributeType_2= ruleXAttributeType | this_XOseeEnumType_3= ruleXOseeEnumType ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:585:1: (this_XArtifactType_0= ruleXArtifactType | this_XRelationType_1= ruleXRelationType | this_XAttributeType_2= ruleXAttributeType | this_XOseeEnumType_3= ruleXOseeEnumType )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:585:1: (this_XArtifactType_0= ruleXArtifactType | this_XRelationType_1= ruleXRelationType | this_XAttributeType_2= ruleXAttributeType | this_XOseeEnumType_3= ruleXOseeEnumType )
            int alt6=4;
            switch ( input.LA(1) ) {
            case 14:
            case 15:
                {
                alt6=1;
                }
                break;
            case 53:
                {
                alt6=2;
                }
                break;
            case 23:
                {
                alt6=3;
                }
                break;
            case 46:
                {
                alt6=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("585:1: (this_XArtifactType_0= ruleXArtifactType | this_XRelationType_1= ruleXRelationType | this_XAttributeType_2= ruleXAttributeType | this_XOseeEnumType_3= ruleXOseeEnumType )", 6, 0, input);

                throw nvae;
            }

            switch (alt6) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:586:5: this_XArtifactType_0= ruleXArtifactType
                    {
                     
                            currentNode=createCompositeNode(grammarAccess.getOseeTypeAccess().getXArtifactTypeParserRuleCall_0(), currentNode); 
                        
                    pushFollow(FOLLOW_ruleXArtifactType_in_ruleOseeType1145);
                    this_XArtifactType_0=ruleXArtifactType();
                    _fsp--;

                     
                            current = this_XArtifactType_0; 
                            currentNode = currentNode.getParent();
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:596:5: this_XRelationType_1= ruleXRelationType
                    {
                     
                            currentNode=createCompositeNode(grammarAccess.getOseeTypeAccess().getXRelationTypeParserRuleCall_1(), currentNode); 
                        
                    pushFollow(FOLLOW_ruleXRelationType_in_ruleOseeType1172);
                    this_XRelationType_1=ruleXRelationType();
                    _fsp--;

                     
                            current = this_XRelationType_1; 
                            currentNode = currentNode.getParent();
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:606:5: this_XAttributeType_2= ruleXAttributeType
                    {
                     
                            currentNode=createCompositeNode(grammarAccess.getOseeTypeAccess().getXAttributeTypeParserRuleCall_2(), currentNode); 
                        
                    pushFollow(FOLLOW_ruleXAttributeType_in_ruleOseeType1199);
                    this_XAttributeType_2=ruleXAttributeType();
                    _fsp--;

                     
                            current = this_XAttributeType_2; 
                            currentNode = currentNode.getParent();
                        

                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:616:5: this_XOseeEnumType_3= ruleXOseeEnumType
                    {
                     
                            currentNode=createCompositeNode(grammarAccess.getOseeTypeAccess().getXOseeEnumTypeParserRuleCall_3(), currentNode); 
                        
                    pushFollow(FOLLOW_ruleXOseeEnumType_in_ruleOseeType1226);
                    this_XOseeEnumType_3=ruleXOseeEnumType();
                    _fsp--;

                     
                            current = this_XOseeEnumType_3; 
                            currentNode = currentNode.getParent();
                        

                    }
                    break;

            }


            }

             resetLookahead(); 
                	lastConsumedNode = currentNode;
                
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end ruleOseeType


    // $ANTLR start entryRuleXArtifactType
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:632:1: entryRuleXArtifactType returns [EObject current=null] : iv_ruleXArtifactType= ruleXArtifactType EOF ;
    public final EObject entryRuleXArtifactType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleXArtifactType = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:633:2: (iv_ruleXArtifactType= ruleXArtifactType EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:634:2: iv_ruleXArtifactType= ruleXArtifactType EOF
            {
             currentNode = createCompositeNode(grammarAccess.getXArtifactTypeRule(), currentNode); 
            pushFollow(FOLLOW_ruleXArtifactType_in_entryRuleXArtifactType1261);
            iv_ruleXArtifactType=ruleXArtifactType();
            _fsp--;

             current =iv_ruleXArtifactType; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleXArtifactType1271); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end entryRuleXArtifactType


    // $ANTLR start ruleXArtifactType
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:641:1: ruleXArtifactType returns [EObject current=null] : ( ( (lv_abstract_0_0= 'abstract' ) )? 'artifactType' ( (lv_name_2_0= ruleARTIFACT_TYPE_REFERENCE ) ) ( 'extends' ( ( ruleARTIFACT_TYPE_REFERENCE ) ) ( ',' ( ( ruleARTIFACT_TYPE_REFERENCE ) ) )* )? '{' 'guid' ( (lv_typeGuid_9_0= RULE_STRING ) ) ( (lv_validAttributeTypes_10_0= ruleXAttributeTypeRef ) )* '}' ) ;
    public final EObject ruleXArtifactType() throws RecognitionException {
        EObject current = null;

        Token lv_abstract_0_0=null;
        Token lv_typeGuid_9_0=null;
        AntlrDatatypeRuleToken lv_name_2_0 = null;

        EObject lv_validAttributeTypes_10_0 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:646:6: ( ( ( (lv_abstract_0_0= 'abstract' ) )? 'artifactType' ( (lv_name_2_0= ruleARTIFACT_TYPE_REFERENCE ) ) ( 'extends' ( ( ruleARTIFACT_TYPE_REFERENCE ) ) ( ',' ( ( ruleARTIFACT_TYPE_REFERENCE ) ) )* )? '{' 'guid' ( (lv_typeGuid_9_0= RULE_STRING ) ) ( (lv_validAttributeTypes_10_0= ruleXAttributeTypeRef ) )* '}' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:647:1: ( ( (lv_abstract_0_0= 'abstract' ) )? 'artifactType' ( (lv_name_2_0= ruleARTIFACT_TYPE_REFERENCE ) ) ( 'extends' ( ( ruleARTIFACT_TYPE_REFERENCE ) ) ( ',' ( ( ruleARTIFACT_TYPE_REFERENCE ) ) )* )? '{' 'guid' ( (lv_typeGuid_9_0= RULE_STRING ) ) ( (lv_validAttributeTypes_10_0= ruleXAttributeTypeRef ) )* '}' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:647:1: ( ( (lv_abstract_0_0= 'abstract' ) )? 'artifactType' ( (lv_name_2_0= ruleARTIFACT_TYPE_REFERENCE ) ) ( 'extends' ( ( ruleARTIFACT_TYPE_REFERENCE ) ) ( ',' ( ( ruleARTIFACT_TYPE_REFERENCE ) ) )* )? '{' 'guid' ( (lv_typeGuid_9_0= RULE_STRING ) ) ( (lv_validAttributeTypes_10_0= ruleXAttributeTypeRef ) )* '}' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:647:2: ( (lv_abstract_0_0= 'abstract' ) )? 'artifactType' ( (lv_name_2_0= ruleARTIFACT_TYPE_REFERENCE ) ) ( 'extends' ( ( ruleARTIFACT_TYPE_REFERENCE ) ) ( ',' ( ( ruleARTIFACT_TYPE_REFERENCE ) ) )* )? '{' 'guid' ( (lv_typeGuid_9_0= RULE_STRING ) ) ( (lv_validAttributeTypes_10_0= ruleXAttributeTypeRef ) )* '}'
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:647:2: ( (lv_abstract_0_0= 'abstract' ) )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==14) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:648:1: (lv_abstract_0_0= 'abstract' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:648:1: (lv_abstract_0_0= 'abstract' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:649:3: lv_abstract_0_0= 'abstract'
                    {
                    lv_abstract_0_0=(Token)input.LT(1);
                    match(input,14,FOLLOW_14_in_ruleXArtifactType1314); 

                            createLeafNode(grammarAccess.getXArtifactTypeAccess().getAbstractAbstractKeyword_0_0(), "abstract"); 
                        

                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getXArtifactTypeRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                    	        
                    	        try {
                    	       		set(current, "abstract", true, "abstract", lastConsumedNode);
                    	        } catch (ValueConverterException vce) {
                    				handleValueConverterException(vce);
                    	        }
                    	    

                    }


                    }
                    break;

            }

            match(input,15,FOLLOW_15_in_ruleXArtifactType1338); 

                    createLeafNode(grammarAccess.getXArtifactTypeAccess().getArtifactTypeKeyword_1(), null); 
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:672:1: ( (lv_name_2_0= ruleARTIFACT_TYPE_REFERENCE ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:673:1: (lv_name_2_0= ruleARTIFACT_TYPE_REFERENCE )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:673:1: (lv_name_2_0= ruleARTIFACT_TYPE_REFERENCE )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:674:3: lv_name_2_0= ruleARTIFACT_TYPE_REFERENCE
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getXArtifactTypeAccess().getNameARTIFACT_TYPE_REFERENCEParserRuleCall_2_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleARTIFACT_TYPE_REFERENCE_in_ruleXArtifactType1359);
            lv_name_2_0=ruleARTIFACT_TYPE_REFERENCE();
            _fsp--;


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getXArtifactTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode.getParent(), current);
            	        }
            	        try {
            	       		set(
            	       			current, 
            	       			"name",
            	        		lv_name_2_0, 
            	        		"ARTIFACT_TYPE_REFERENCE", 
            	        		currentNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	        currentNode = currentNode.getParent();
            	    

            }


            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:696:2: ( 'extends' ( ( ruleARTIFACT_TYPE_REFERENCE ) ) ( ',' ( ( ruleARTIFACT_TYPE_REFERENCE ) ) )* )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==16) ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:696:4: 'extends' ( ( ruleARTIFACT_TYPE_REFERENCE ) ) ( ',' ( ( ruleARTIFACT_TYPE_REFERENCE ) ) )*
                    {
                    match(input,16,FOLLOW_16_in_ruleXArtifactType1370); 

                            createLeafNode(grammarAccess.getXArtifactTypeAccess().getExtendsKeyword_3_0(), null); 
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:700:1: ( ( ruleARTIFACT_TYPE_REFERENCE ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:701:1: ( ruleARTIFACT_TYPE_REFERENCE )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:701:1: ( ruleARTIFACT_TYPE_REFERENCE )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:702:3: ruleARTIFACT_TYPE_REFERENCE
                    {

                    			if (current==null) {
                    	            current = factory.create(grammarAccess.getXArtifactTypeRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                            
                     
                    	        currentNode=createCompositeNode(grammarAccess.getXArtifactTypeAccess().getSuperArtifactTypesXArtifactTypeCrossReference_3_1_0(), currentNode); 
                    	    
                    pushFollow(FOLLOW_ruleARTIFACT_TYPE_REFERENCE_in_ruleXArtifactType1393);
                    ruleARTIFACT_TYPE_REFERENCE();
                    _fsp--;

                     
                    	        currentNode = currentNode.getParent();
                    	    

                    }


                    }

                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:716:2: ( ',' ( ( ruleARTIFACT_TYPE_REFERENCE ) ) )*
                    loop8:
                    do {
                        int alt8=2;
                        int LA8_0 = input.LA(1);

                        if ( (LA8_0==17) ) {
                            alt8=1;
                        }


                        switch (alt8) {
                    	case 1 :
                    	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:716:4: ',' ( ( ruleARTIFACT_TYPE_REFERENCE ) )
                    	    {
                    	    match(input,17,FOLLOW_17_in_ruleXArtifactType1404); 

                    	            createLeafNode(grammarAccess.getXArtifactTypeAccess().getCommaKeyword_3_2_0(), null); 
                    	        
                    	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:720:1: ( ( ruleARTIFACT_TYPE_REFERENCE ) )
                    	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:721:1: ( ruleARTIFACT_TYPE_REFERENCE )
                    	    {
                    	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:721:1: ( ruleARTIFACT_TYPE_REFERENCE )
                    	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:722:3: ruleARTIFACT_TYPE_REFERENCE
                    	    {

                    	    			if (current==null) {
                    	    	            current = factory.create(grammarAccess.getXArtifactTypeRule().getType().getClassifier());
                    	    	            associateNodeWithAstElement(currentNode, current);
                    	    	        }
                    	            
                    	     
                    	    	        currentNode=createCompositeNode(grammarAccess.getXArtifactTypeAccess().getSuperArtifactTypesXArtifactTypeCrossReference_3_2_1_0(), currentNode); 
                    	    	    
                    	    pushFollow(FOLLOW_ruleARTIFACT_TYPE_REFERENCE_in_ruleXArtifactType1427);
                    	    ruleARTIFACT_TYPE_REFERENCE();
                    	    _fsp--;

                    	     
                    	    	        currentNode = currentNode.getParent();
                    	    	    

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

            match(input,18,FOLLOW_18_in_ruleXArtifactType1441); 

                    createLeafNode(grammarAccess.getXArtifactTypeAccess().getLeftCurlyBracketKeyword_4(), null); 
                
            match(input,19,FOLLOW_19_in_ruleXArtifactType1451); 

                    createLeafNode(grammarAccess.getXArtifactTypeAccess().getGuidKeyword_5(), null); 
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:744:1: ( (lv_typeGuid_9_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:745:1: (lv_typeGuid_9_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:745:1: (lv_typeGuid_9_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:746:3: lv_typeGuid_9_0= RULE_STRING
            {
            lv_typeGuid_9_0=(Token)input.LT(1);
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXArtifactType1468); 

            			createLeafNode(grammarAccess.getXArtifactTypeAccess().getTypeGuidSTRINGTerminalRuleCall_6_0(), "typeGuid"); 
            		

            	        if (current==null) {
            	            current = factory.create(grammarAccess.getXArtifactTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
            	        try {
            	       		set(
            	       			current, 
            	       			"typeGuid",
            	        		lv_typeGuid_9_0, 
            	        		"STRING", 
            	        		lastConsumedNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	    

            }


            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:768:2: ( (lv_validAttributeTypes_10_0= ruleXAttributeTypeRef ) )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0==21) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:769:1: (lv_validAttributeTypes_10_0= ruleXAttributeTypeRef )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:769:1: (lv_validAttributeTypes_10_0= ruleXAttributeTypeRef )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:770:3: lv_validAttributeTypes_10_0= ruleXAttributeTypeRef
            	    {
            	     
            	    	        currentNode=createCompositeNode(grammarAccess.getXArtifactTypeAccess().getValidAttributeTypesXAttributeTypeRefParserRuleCall_7_0(), currentNode); 
            	    	    
            	    pushFollow(FOLLOW_ruleXAttributeTypeRef_in_ruleXArtifactType1494);
            	    lv_validAttributeTypes_10_0=ruleXAttributeTypeRef();
            	    _fsp--;


            	    	        if (current==null) {
            	    	            current = factory.create(grammarAccess.getXArtifactTypeRule().getType().getClassifier());
            	    	            associateNodeWithAstElement(currentNode.getParent(), current);
            	    	        }
            	    	        try {
            	    	       		add(
            	    	       			current, 
            	    	       			"validAttributeTypes",
            	    	        		lv_validAttributeTypes_10_0, 
            	    	        		"XAttributeTypeRef", 
            	    	        		currentNode);
            	    	        } catch (ValueConverterException vce) {
            	    				handleValueConverterException(vce);
            	    	        }
            	    	        currentNode = currentNode.getParent();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    break loop10;
                }
            } while (true);

            match(input,20,FOLLOW_20_in_ruleXArtifactType1505); 

                    createLeafNode(grammarAccess.getXArtifactTypeAccess().getRightCurlyBracketKeyword_8(), null); 
                

            }


            }

             resetLookahead(); 
                	lastConsumedNode = currentNode;
                
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end ruleXArtifactType


    // $ANTLR start entryRuleXAttributeTypeRef
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:804:1: entryRuleXAttributeTypeRef returns [EObject current=null] : iv_ruleXAttributeTypeRef= ruleXAttributeTypeRef EOF ;
    public final EObject entryRuleXAttributeTypeRef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleXAttributeTypeRef = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:805:2: (iv_ruleXAttributeTypeRef= ruleXAttributeTypeRef EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:806:2: iv_ruleXAttributeTypeRef= ruleXAttributeTypeRef EOF
            {
             currentNode = createCompositeNode(grammarAccess.getXAttributeTypeRefRule(), currentNode); 
            pushFollow(FOLLOW_ruleXAttributeTypeRef_in_entryRuleXAttributeTypeRef1541);
            iv_ruleXAttributeTypeRef=ruleXAttributeTypeRef();
            _fsp--;

             current =iv_ruleXAttributeTypeRef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleXAttributeTypeRef1551); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end entryRuleXAttributeTypeRef


    // $ANTLR start ruleXAttributeTypeRef
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:813:1: ruleXAttributeTypeRef returns [EObject current=null] : ( 'attribute' ( ( ruleATTRIBUTE_TYPE_REFERENCE ) ) ( 'branchGuid' ( (lv_branchGuid_3_0= RULE_STRING ) ) )? ) ;
    public final EObject ruleXAttributeTypeRef() throws RecognitionException {
        EObject current = null;

        Token lv_branchGuid_3_0=null;

         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:818:6: ( ( 'attribute' ( ( ruleATTRIBUTE_TYPE_REFERENCE ) ) ( 'branchGuid' ( (lv_branchGuid_3_0= RULE_STRING ) ) )? ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:819:1: ( 'attribute' ( ( ruleATTRIBUTE_TYPE_REFERENCE ) ) ( 'branchGuid' ( (lv_branchGuid_3_0= RULE_STRING ) ) )? )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:819:1: ( 'attribute' ( ( ruleATTRIBUTE_TYPE_REFERENCE ) ) ( 'branchGuid' ( (lv_branchGuid_3_0= RULE_STRING ) ) )? )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:819:3: 'attribute' ( ( ruleATTRIBUTE_TYPE_REFERENCE ) ) ( 'branchGuid' ( (lv_branchGuid_3_0= RULE_STRING ) ) )?
            {
            match(input,21,FOLLOW_21_in_ruleXAttributeTypeRef1586); 

                    createLeafNode(grammarAccess.getXAttributeTypeRefAccess().getAttributeKeyword_0(), null); 
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:823:1: ( ( ruleATTRIBUTE_TYPE_REFERENCE ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:824:1: ( ruleATTRIBUTE_TYPE_REFERENCE )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:824:1: ( ruleATTRIBUTE_TYPE_REFERENCE )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:825:3: ruleATTRIBUTE_TYPE_REFERENCE
            {

            			if (current==null) {
            	            current = factory.create(grammarAccess.getXAttributeTypeRefRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
                    
             
            	        currentNode=createCompositeNode(grammarAccess.getXAttributeTypeRefAccess().getValidAttributeTypeXAttributeTypeCrossReference_1_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleATTRIBUTE_TYPE_REFERENCE_in_ruleXAttributeTypeRef1609);
            ruleATTRIBUTE_TYPE_REFERENCE();
            _fsp--;

             
            	        currentNode = currentNode.getParent();
            	    

            }


            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:839:2: ( 'branchGuid' ( (lv_branchGuid_3_0= RULE_STRING ) ) )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==22) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:839:4: 'branchGuid' ( (lv_branchGuid_3_0= RULE_STRING ) )
                    {
                    match(input,22,FOLLOW_22_in_ruleXAttributeTypeRef1620); 

                            createLeafNode(grammarAccess.getXAttributeTypeRefAccess().getBranchGuidKeyword_2_0(), null); 
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:843:1: ( (lv_branchGuid_3_0= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:844:1: (lv_branchGuid_3_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:844:1: (lv_branchGuid_3_0= RULE_STRING )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:845:3: lv_branchGuid_3_0= RULE_STRING
                    {
                    lv_branchGuid_3_0=(Token)input.LT(1);
                    match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXAttributeTypeRef1637); 

                    			createLeafNode(grammarAccess.getXAttributeTypeRefAccess().getBranchGuidSTRINGTerminalRuleCall_2_1_0(), "branchGuid"); 
                    		

                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getXAttributeTypeRefRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                    	        try {
                    	       		set(
                    	       			current, 
                    	       			"branchGuid",
                    	        		lv_branchGuid_3_0, 
                    	        		"STRING", 
                    	        		lastConsumedNode);
                    	        } catch (ValueConverterException vce) {
                    				handleValueConverterException(vce);
                    	        }
                    	    

                    }


                    }


                    }
                    break;

            }


            }


            }

             resetLookahead(); 
                	lastConsumedNode = currentNode;
                
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end ruleXAttributeTypeRef


    // $ANTLR start entryRuleXAttributeType
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:875:1: entryRuleXAttributeType returns [EObject current=null] : iv_ruleXAttributeType= ruleXAttributeType EOF ;
    public final EObject entryRuleXAttributeType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleXAttributeType = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:876:2: (iv_ruleXAttributeType= ruleXAttributeType EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:877:2: iv_ruleXAttributeType= ruleXAttributeType EOF
            {
             currentNode = createCompositeNode(grammarAccess.getXAttributeTypeRule(), currentNode); 
            pushFollow(FOLLOW_ruleXAttributeType_in_entryRuleXAttributeType1680);
            iv_ruleXAttributeType=ruleXAttributeType();
            _fsp--;

             current =iv_ruleXAttributeType; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleXAttributeType1690); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end entryRuleXAttributeType


    // $ANTLR start ruleXAttributeType
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:884:1: ruleXAttributeType returns [EObject current=null] : ( 'attributeType' ( (lv_name_1_0= ruleATTRIBUTE_TYPE_REFERENCE ) ) ( 'extends' ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) ) ) ( 'overrides' ( ( ruleATTRIBUTE_TYPE_REFERENCE ) ) )? '{' 'guid' ( (lv_typeGuid_8_0= RULE_STRING ) ) 'dataProvider' ( ( (lv_dataProvider_10_1= 'DefaultAttributeDataProvider' | lv_dataProvider_10_2= 'UriAttributeDataProvider' | lv_dataProvider_10_3= ruleQUALIFIED_NAME ) ) ) 'min' ( (lv_min_12_0= RULE_WHOLE_NUM_STR ) ) 'max' ( ( (lv_max_14_1= RULE_WHOLE_NUM_STR | lv_max_14_2= 'unlimited' ) ) ) ( 'taggerId' ( ( (lv_taggerId_16_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_16_2= ruleQUALIFIED_NAME ) ) ) )? ( 'enumType' ( ( ruleENUM_TYPE_REFERENCE ) ) )? ( 'description' ( (lv_description_20_0= RULE_STRING ) ) )? ( 'defaultValue' ( (lv_defaultValue_22_0= RULE_STRING ) ) )? ( 'fileExtension' ( (lv_fileExtension_24_0= RULE_STRING ) ) )? '}' ) ;
    public final EObject ruleXAttributeType() throws RecognitionException {
        EObject current = null;

        Token lv_typeGuid_8_0=null;
        Token lv_dataProvider_10_1=null;
        Token lv_dataProvider_10_2=null;
        Token lv_min_12_0=null;
        Token lv_max_14_1=null;
        Token lv_max_14_2=null;
        Token lv_taggerId_16_1=null;
        Token lv_description_20_0=null;
        Token lv_defaultValue_22_0=null;
        Token lv_fileExtension_24_0=null;
        AntlrDatatypeRuleToken lv_name_1_0 = null;

        AntlrDatatypeRuleToken lv_baseAttributeType_3_0 = null;

        AntlrDatatypeRuleToken lv_dataProvider_10_3 = null;

        AntlrDatatypeRuleToken lv_taggerId_16_2 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:889:6: ( ( 'attributeType' ( (lv_name_1_0= ruleATTRIBUTE_TYPE_REFERENCE ) ) ( 'extends' ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) ) ) ( 'overrides' ( ( ruleATTRIBUTE_TYPE_REFERENCE ) ) )? '{' 'guid' ( (lv_typeGuid_8_0= RULE_STRING ) ) 'dataProvider' ( ( (lv_dataProvider_10_1= 'DefaultAttributeDataProvider' | lv_dataProvider_10_2= 'UriAttributeDataProvider' | lv_dataProvider_10_3= ruleQUALIFIED_NAME ) ) ) 'min' ( (lv_min_12_0= RULE_WHOLE_NUM_STR ) ) 'max' ( ( (lv_max_14_1= RULE_WHOLE_NUM_STR | lv_max_14_2= 'unlimited' ) ) ) ( 'taggerId' ( ( (lv_taggerId_16_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_16_2= ruleQUALIFIED_NAME ) ) ) )? ( 'enumType' ( ( ruleENUM_TYPE_REFERENCE ) ) )? ( 'description' ( (lv_description_20_0= RULE_STRING ) ) )? ( 'defaultValue' ( (lv_defaultValue_22_0= RULE_STRING ) ) )? ( 'fileExtension' ( (lv_fileExtension_24_0= RULE_STRING ) ) )? '}' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:890:1: ( 'attributeType' ( (lv_name_1_0= ruleATTRIBUTE_TYPE_REFERENCE ) ) ( 'extends' ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) ) ) ( 'overrides' ( ( ruleATTRIBUTE_TYPE_REFERENCE ) ) )? '{' 'guid' ( (lv_typeGuid_8_0= RULE_STRING ) ) 'dataProvider' ( ( (lv_dataProvider_10_1= 'DefaultAttributeDataProvider' | lv_dataProvider_10_2= 'UriAttributeDataProvider' | lv_dataProvider_10_3= ruleQUALIFIED_NAME ) ) ) 'min' ( (lv_min_12_0= RULE_WHOLE_NUM_STR ) ) 'max' ( ( (lv_max_14_1= RULE_WHOLE_NUM_STR | lv_max_14_2= 'unlimited' ) ) ) ( 'taggerId' ( ( (lv_taggerId_16_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_16_2= ruleQUALIFIED_NAME ) ) ) )? ( 'enumType' ( ( ruleENUM_TYPE_REFERENCE ) ) )? ( 'description' ( (lv_description_20_0= RULE_STRING ) ) )? ( 'defaultValue' ( (lv_defaultValue_22_0= RULE_STRING ) ) )? ( 'fileExtension' ( (lv_fileExtension_24_0= RULE_STRING ) ) )? '}' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:890:1: ( 'attributeType' ( (lv_name_1_0= ruleATTRIBUTE_TYPE_REFERENCE ) ) ( 'extends' ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) ) ) ( 'overrides' ( ( ruleATTRIBUTE_TYPE_REFERENCE ) ) )? '{' 'guid' ( (lv_typeGuid_8_0= RULE_STRING ) ) 'dataProvider' ( ( (lv_dataProvider_10_1= 'DefaultAttributeDataProvider' | lv_dataProvider_10_2= 'UriAttributeDataProvider' | lv_dataProvider_10_3= ruleQUALIFIED_NAME ) ) ) 'min' ( (lv_min_12_0= RULE_WHOLE_NUM_STR ) ) 'max' ( ( (lv_max_14_1= RULE_WHOLE_NUM_STR | lv_max_14_2= 'unlimited' ) ) ) ( 'taggerId' ( ( (lv_taggerId_16_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_16_2= ruleQUALIFIED_NAME ) ) ) )? ( 'enumType' ( ( ruleENUM_TYPE_REFERENCE ) ) )? ( 'description' ( (lv_description_20_0= RULE_STRING ) ) )? ( 'defaultValue' ( (lv_defaultValue_22_0= RULE_STRING ) ) )? ( 'fileExtension' ( (lv_fileExtension_24_0= RULE_STRING ) ) )? '}' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:890:3: 'attributeType' ( (lv_name_1_0= ruleATTRIBUTE_TYPE_REFERENCE ) ) ( 'extends' ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) ) ) ( 'overrides' ( ( ruleATTRIBUTE_TYPE_REFERENCE ) ) )? '{' 'guid' ( (lv_typeGuid_8_0= RULE_STRING ) ) 'dataProvider' ( ( (lv_dataProvider_10_1= 'DefaultAttributeDataProvider' | lv_dataProvider_10_2= 'UriAttributeDataProvider' | lv_dataProvider_10_3= ruleQUALIFIED_NAME ) ) ) 'min' ( (lv_min_12_0= RULE_WHOLE_NUM_STR ) ) 'max' ( ( (lv_max_14_1= RULE_WHOLE_NUM_STR | lv_max_14_2= 'unlimited' ) ) ) ( 'taggerId' ( ( (lv_taggerId_16_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_16_2= ruleQUALIFIED_NAME ) ) ) )? ( 'enumType' ( ( ruleENUM_TYPE_REFERENCE ) ) )? ( 'description' ( (lv_description_20_0= RULE_STRING ) ) )? ( 'defaultValue' ( (lv_defaultValue_22_0= RULE_STRING ) ) )? ( 'fileExtension' ( (lv_fileExtension_24_0= RULE_STRING ) ) )? '}'
            {
            match(input,23,FOLLOW_23_in_ruleXAttributeType1725); 

                    createLeafNode(grammarAccess.getXAttributeTypeAccess().getAttributeTypeKeyword_0(), null); 
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:894:1: ( (lv_name_1_0= ruleATTRIBUTE_TYPE_REFERENCE ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:895:1: (lv_name_1_0= ruleATTRIBUTE_TYPE_REFERENCE )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:895:1: (lv_name_1_0= ruleATTRIBUTE_TYPE_REFERENCE )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:896:3: lv_name_1_0= ruleATTRIBUTE_TYPE_REFERENCE
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getXAttributeTypeAccess().getNameATTRIBUTE_TYPE_REFERENCEParserRuleCall_1_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleATTRIBUTE_TYPE_REFERENCE_in_ruleXAttributeType1746);
            lv_name_1_0=ruleATTRIBUTE_TYPE_REFERENCE();
            _fsp--;


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getXAttributeTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode.getParent(), current);
            	        }
            	        try {
            	       		set(
            	       			current, 
            	       			"name",
            	        		lv_name_1_0, 
            	        		"ATTRIBUTE_TYPE_REFERENCE", 
            	        		currentNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	        currentNode = currentNode.getParent();
            	    

            }


            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:918:2: ( 'extends' ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:918:4: 'extends' ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) )
            {
            match(input,16,FOLLOW_16_in_ruleXAttributeType1757); 

                    createLeafNode(grammarAccess.getXAttributeTypeAccess().getExtendsKeyword_2_0(), null); 
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:922:1: ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:923:1: (lv_baseAttributeType_3_0= ruleAttributeBaseType )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:923:1: (lv_baseAttributeType_3_0= ruleAttributeBaseType )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:924:3: lv_baseAttributeType_3_0= ruleAttributeBaseType
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getXAttributeTypeAccess().getBaseAttributeTypeAttributeBaseTypeParserRuleCall_2_1_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleAttributeBaseType_in_ruleXAttributeType1778);
            lv_baseAttributeType_3_0=ruleAttributeBaseType();
            _fsp--;


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getXAttributeTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode.getParent(), current);
            	        }
            	        try {
            	       		set(
            	       			current, 
            	       			"baseAttributeType",
            	        		lv_baseAttributeType_3_0, 
            	        		"AttributeBaseType", 
            	        		currentNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	        currentNode = currentNode.getParent();
            	    

            }


            }


            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:946:3: ( 'overrides' ( ( ruleATTRIBUTE_TYPE_REFERENCE ) ) )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==24) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:946:5: 'overrides' ( ( ruleATTRIBUTE_TYPE_REFERENCE ) )
                    {
                    match(input,24,FOLLOW_24_in_ruleXAttributeType1790); 

                            createLeafNode(grammarAccess.getXAttributeTypeAccess().getOverridesKeyword_3_0(), null); 
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:950:1: ( ( ruleATTRIBUTE_TYPE_REFERENCE ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:951:1: ( ruleATTRIBUTE_TYPE_REFERENCE )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:951:1: ( ruleATTRIBUTE_TYPE_REFERENCE )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:952:3: ruleATTRIBUTE_TYPE_REFERENCE
                    {

                    			if (current==null) {
                    	            current = factory.create(grammarAccess.getXAttributeTypeRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                            
                     
                    	        currentNode=createCompositeNode(grammarAccess.getXAttributeTypeAccess().getOverrideXAttributeTypeCrossReference_3_1_0(), currentNode); 
                    	    
                    pushFollow(FOLLOW_ruleATTRIBUTE_TYPE_REFERENCE_in_ruleXAttributeType1813);
                    ruleATTRIBUTE_TYPE_REFERENCE();
                    _fsp--;

                     
                    	        currentNode = currentNode.getParent();
                    	    

                    }


                    }


                    }
                    break;

            }

            match(input,18,FOLLOW_18_in_ruleXAttributeType1825); 

                    createLeafNode(grammarAccess.getXAttributeTypeAccess().getLeftCurlyBracketKeyword_4(), null); 
                
            match(input,19,FOLLOW_19_in_ruleXAttributeType1835); 

                    createLeafNode(grammarAccess.getXAttributeTypeAccess().getGuidKeyword_5(), null); 
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:974:1: ( (lv_typeGuid_8_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:975:1: (lv_typeGuid_8_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:975:1: (lv_typeGuid_8_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:976:3: lv_typeGuid_8_0= RULE_STRING
            {
            lv_typeGuid_8_0=(Token)input.LT(1);
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXAttributeType1852); 

            			createLeafNode(grammarAccess.getXAttributeTypeAccess().getTypeGuidSTRINGTerminalRuleCall_6_0(), "typeGuid"); 
            		

            	        if (current==null) {
            	            current = factory.create(grammarAccess.getXAttributeTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
            	        try {
            	       		set(
            	       			current, 
            	       			"typeGuid",
            	        		lv_typeGuid_8_0, 
            	        		"STRING", 
            	        		lastConsumedNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	    

            }


            }

            match(input,25,FOLLOW_25_in_ruleXAttributeType1867); 

                    createLeafNode(grammarAccess.getXAttributeTypeAccess().getDataProviderKeyword_7(), null); 
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1002:1: ( ( (lv_dataProvider_10_1= 'DefaultAttributeDataProvider' | lv_dataProvider_10_2= 'UriAttributeDataProvider' | lv_dataProvider_10_3= ruleQUALIFIED_NAME ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1003:1: ( (lv_dataProvider_10_1= 'DefaultAttributeDataProvider' | lv_dataProvider_10_2= 'UriAttributeDataProvider' | lv_dataProvider_10_3= ruleQUALIFIED_NAME ) )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1003:1: ( (lv_dataProvider_10_1= 'DefaultAttributeDataProvider' | lv_dataProvider_10_2= 'UriAttributeDataProvider' | lv_dataProvider_10_3= ruleQUALIFIED_NAME ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1004:1: (lv_dataProvider_10_1= 'DefaultAttributeDataProvider' | lv_dataProvider_10_2= 'UriAttributeDataProvider' | lv_dataProvider_10_3= ruleQUALIFIED_NAME )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1004:1: (lv_dataProvider_10_1= 'DefaultAttributeDataProvider' | lv_dataProvider_10_2= 'UriAttributeDataProvider' | lv_dataProvider_10_3= ruleQUALIFIED_NAME )
            int alt13=3;
            switch ( input.LA(1) ) {
            case 26:
                {
                alt13=1;
                }
                break;
            case 27:
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
                    new NoViableAltException("1004:1: (lv_dataProvider_10_1= 'DefaultAttributeDataProvider' | lv_dataProvider_10_2= 'UriAttributeDataProvider' | lv_dataProvider_10_3= ruleQUALIFIED_NAME )", 13, 0, input);

                throw nvae;
            }

            switch (alt13) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1005:3: lv_dataProvider_10_1= 'DefaultAttributeDataProvider'
                    {
                    lv_dataProvider_10_1=(Token)input.LT(1);
                    match(input,26,FOLLOW_26_in_ruleXAttributeType1887); 

                            createLeafNode(grammarAccess.getXAttributeTypeAccess().getDataProviderDefaultAttributeDataProviderKeyword_8_0_0(), "dataProvider"); 
                        

                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getXAttributeTypeRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                    	        
                    	        try {
                    	       		set(current, "dataProvider", lv_dataProvider_10_1, null, lastConsumedNode);
                    	        } catch (ValueConverterException vce) {
                    				handleValueConverterException(vce);
                    	        }
                    	    

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1023:8: lv_dataProvider_10_2= 'UriAttributeDataProvider'
                    {
                    lv_dataProvider_10_2=(Token)input.LT(1);
                    match(input,27,FOLLOW_27_in_ruleXAttributeType1916); 

                            createLeafNode(grammarAccess.getXAttributeTypeAccess().getDataProviderUriAttributeDataProviderKeyword_8_0_1(), "dataProvider"); 
                        

                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getXAttributeTypeRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                    	        
                    	        try {
                    	       		set(current, "dataProvider", lv_dataProvider_10_2, null, lastConsumedNode);
                    	        } catch (ValueConverterException vce) {
                    				handleValueConverterException(vce);
                    	        }
                    	    

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1041:8: lv_dataProvider_10_3= ruleQUALIFIED_NAME
                    {
                     
                    	        currentNode=createCompositeNode(grammarAccess.getXAttributeTypeAccess().getDataProviderQUALIFIED_NAMEParserRuleCall_8_0_2(), currentNode); 
                    	    
                    pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_ruleXAttributeType1948);
                    lv_dataProvider_10_3=ruleQUALIFIED_NAME();
                    _fsp--;


                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getXAttributeTypeRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode.getParent(), current);
                    	        }
                    	        try {
                    	       		set(
                    	       			current, 
                    	       			"dataProvider",
                    	        		lv_dataProvider_10_3, 
                    	        		"QUALIFIED_NAME", 
                    	        		currentNode);
                    	        } catch (ValueConverterException vce) {
                    				handleValueConverterException(vce);
                    	        }
                    	        currentNode = currentNode.getParent();
                    	    

                    }
                    break;

            }


            }


            }

            match(input,28,FOLLOW_28_in_ruleXAttributeType1961); 

                    createLeafNode(grammarAccess.getXAttributeTypeAccess().getMinKeyword_9(), null); 
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1069:1: ( (lv_min_12_0= RULE_WHOLE_NUM_STR ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1070:1: (lv_min_12_0= RULE_WHOLE_NUM_STR )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1070:1: (lv_min_12_0= RULE_WHOLE_NUM_STR )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1071:3: lv_min_12_0= RULE_WHOLE_NUM_STR
            {
            lv_min_12_0=(Token)input.LT(1);
            match(input,RULE_WHOLE_NUM_STR,FOLLOW_RULE_WHOLE_NUM_STR_in_ruleXAttributeType1978); 

            			createLeafNode(grammarAccess.getXAttributeTypeAccess().getMinWHOLE_NUM_STRTerminalRuleCall_10_0(), "min"); 
            		

            	        if (current==null) {
            	            current = factory.create(grammarAccess.getXAttributeTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
            	        try {
            	       		set(
            	       			current, 
            	       			"min",
            	        		lv_min_12_0, 
            	        		"WHOLE_NUM_STR", 
            	        		lastConsumedNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	    

            }


            }

            match(input,29,FOLLOW_29_in_ruleXAttributeType1993); 

                    createLeafNode(grammarAccess.getXAttributeTypeAccess().getMaxKeyword_11(), null); 
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1097:1: ( ( (lv_max_14_1= RULE_WHOLE_NUM_STR | lv_max_14_2= 'unlimited' ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1098:1: ( (lv_max_14_1= RULE_WHOLE_NUM_STR | lv_max_14_2= 'unlimited' ) )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1098:1: ( (lv_max_14_1= RULE_WHOLE_NUM_STR | lv_max_14_2= 'unlimited' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1099:1: (lv_max_14_1= RULE_WHOLE_NUM_STR | lv_max_14_2= 'unlimited' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1099:1: (lv_max_14_1= RULE_WHOLE_NUM_STR | lv_max_14_2= 'unlimited' )
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==RULE_WHOLE_NUM_STR) ) {
                alt14=1;
            }
            else if ( (LA14_0==30) ) {
                alt14=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("1099:1: (lv_max_14_1= RULE_WHOLE_NUM_STR | lv_max_14_2= 'unlimited' )", 14, 0, input);

                throw nvae;
            }
            switch (alt14) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1100:3: lv_max_14_1= RULE_WHOLE_NUM_STR
                    {
                    lv_max_14_1=(Token)input.LT(1);
                    match(input,RULE_WHOLE_NUM_STR,FOLLOW_RULE_WHOLE_NUM_STR_in_ruleXAttributeType2012); 

                    			createLeafNode(grammarAccess.getXAttributeTypeAccess().getMaxWHOLE_NUM_STRTerminalRuleCall_12_0_0(), "max"); 
                    		

                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getXAttributeTypeRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                    	        try {
                    	       		set(
                    	       			current, 
                    	       			"max",
                    	        		lv_max_14_1, 
                    	        		"WHOLE_NUM_STR", 
                    	        		lastConsumedNode);
                    	        } catch (ValueConverterException vce) {
                    				handleValueConverterException(vce);
                    	        }
                    	    

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1121:8: lv_max_14_2= 'unlimited'
                    {
                    lv_max_14_2=(Token)input.LT(1);
                    match(input,30,FOLLOW_30_in_ruleXAttributeType2033); 

                            createLeafNode(grammarAccess.getXAttributeTypeAccess().getMaxUnlimitedKeyword_12_0_1(), "max"); 
                        

                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getXAttributeTypeRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                    	        
                    	        try {
                    	       		set(current, "max", lv_max_14_2, null, lastConsumedNode);
                    	        } catch (ValueConverterException vce) {
                    				handleValueConverterException(vce);
                    	        }
                    	    

                    }
                    break;

            }


            }


            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1142:2: ( 'taggerId' ( ( (lv_taggerId_16_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_16_2= ruleQUALIFIED_NAME ) ) ) )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==31) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1142:4: 'taggerId' ( ( (lv_taggerId_16_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_16_2= ruleQUALIFIED_NAME ) ) )
                    {
                    match(input,31,FOLLOW_31_in_ruleXAttributeType2060); 

                            createLeafNode(grammarAccess.getXAttributeTypeAccess().getTaggerIdKeyword_13_0(), null); 
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1146:1: ( ( (lv_taggerId_16_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_16_2= ruleQUALIFIED_NAME ) ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1147:1: ( (lv_taggerId_16_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_16_2= ruleQUALIFIED_NAME ) )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1147:1: ( (lv_taggerId_16_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_16_2= ruleQUALIFIED_NAME ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1148:1: (lv_taggerId_16_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_16_2= ruleQUALIFIED_NAME )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1148:1: (lv_taggerId_16_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_16_2= ruleQUALIFIED_NAME )
                    int alt15=2;
                    int LA15_0 = input.LA(1);

                    if ( (LA15_0==32) ) {
                        alt15=1;
                    }
                    else if ( (LA15_0==RULE_ID) ) {
                        alt15=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("1148:1: (lv_taggerId_16_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_16_2= ruleQUALIFIED_NAME )", 15, 0, input);

                        throw nvae;
                    }
                    switch (alt15) {
                        case 1 :
                            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1149:3: lv_taggerId_16_1= 'DefaultAttributeTaggerProvider'
                            {
                            lv_taggerId_16_1=(Token)input.LT(1);
                            match(input,32,FOLLOW_32_in_ruleXAttributeType2080); 

                                    createLeafNode(grammarAccess.getXAttributeTypeAccess().getTaggerIdDefaultAttributeTaggerProviderKeyword_13_1_0_0(), "taggerId"); 
                                

                            	        if (current==null) {
                            	            current = factory.create(grammarAccess.getXAttributeTypeRule().getType().getClassifier());
                            	            associateNodeWithAstElement(currentNode, current);
                            	        }
                            	        
                            	        try {
                            	       		set(current, "taggerId", lv_taggerId_16_1, null, lastConsumedNode);
                            	        } catch (ValueConverterException vce) {
                            				handleValueConverterException(vce);
                            	        }
                            	    

                            }
                            break;
                        case 2 :
                            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1167:8: lv_taggerId_16_2= ruleQUALIFIED_NAME
                            {
                             
                            	        currentNode=createCompositeNode(grammarAccess.getXAttributeTypeAccess().getTaggerIdQUALIFIED_NAMEParserRuleCall_13_1_0_1(), currentNode); 
                            	    
                            pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_ruleXAttributeType2112);
                            lv_taggerId_16_2=ruleQUALIFIED_NAME();
                            _fsp--;


                            	        if (current==null) {
                            	            current = factory.create(grammarAccess.getXAttributeTypeRule().getType().getClassifier());
                            	            associateNodeWithAstElement(currentNode.getParent(), current);
                            	        }
                            	        try {
                            	       		set(
                            	       			current, 
                            	       			"taggerId",
                            	        		lv_taggerId_16_2, 
                            	        		"QUALIFIED_NAME", 
                            	        		currentNode);
                            	        } catch (ValueConverterException vce) {
                            				handleValueConverterException(vce);
                            	        }
                            	        currentNode = currentNode.getParent();
                            	    

                            }
                            break;

                    }


                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1191:4: ( 'enumType' ( ( ruleENUM_TYPE_REFERENCE ) ) )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==33) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1191:6: 'enumType' ( ( ruleENUM_TYPE_REFERENCE ) )
                    {
                    match(input,33,FOLLOW_33_in_ruleXAttributeType2128); 

                            createLeafNode(grammarAccess.getXAttributeTypeAccess().getEnumTypeKeyword_14_0(), null); 
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1195:1: ( ( ruleENUM_TYPE_REFERENCE ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1196:1: ( ruleENUM_TYPE_REFERENCE )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1196:1: ( ruleENUM_TYPE_REFERENCE )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1197:3: ruleENUM_TYPE_REFERENCE
                    {

                    			if (current==null) {
                    	            current = factory.create(grammarAccess.getXAttributeTypeRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                            
                     
                    	        currentNode=createCompositeNode(grammarAccess.getXAttributeTypeAccess().getEnumTypeXOseeEnumTypeCrossReference_14_1_0(), currentNode); 
                    	    
                    pushFollow(FOLLOW_ruleENUM_TYPE_REFERENCE_in_ruleXAttributeType2151);
                    ruleENUM_TYPE_REFERENCE();
                    _fsp--;

                     
                    	        currentNode = currentNode.getParent();
                    	    

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1211:4: ( 'description' ( (lv_description_20_0= RULE_STRING ) ) )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==34) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1211:6: 'description' ( (lv_description_20_0= RULE_STRING ) )
                    {
                    match(input,34,FOLLOW_34_in_ruleXAttributeType2164); 

                            createLeafNode(grammarAccess.getXAttributeTypeAccess().getDescriptionKeyword_15_0(), null); 
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1215:1: ( (lv_description_20_0= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1216:1: (lv_description_20_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1216:1: (lv_description_20_0= RULE_STRING )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1217:3: lv_description_20_0= RULE_STRING
                    {
                    lv_description_20_0=(Token)input.LT(1);
                    match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXAttributeType2181); 

                    			createLeafNode(grammarAccess.getXAttributeTypeAccess().getDescriptionSTRINGTerminalRuleCall_15_1_0(), "description"); 
                    		

                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getXAttributeTypeRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                    	        try {
                    	       		set(
                    	       			current, 
                    	       			"description",
                    	        		lv_description_20_0, 
                    	        		"STRING", 
                    	        		lastConsumedNode);
                    	        } catch (ValueConverterException vce) {
                    				handleValueConverterException(vce);
                    	        }
                    	    

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1239:4: ( 'defaultValue' ( (lv_defaultValue_22_0= RULE_STRING ) ) )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==35) ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1239:6: 'defaultValue' ( (lv_defaultValue_22_0= RULE_STRING ) )
                    {
                    match(input,35,FOLLOW_35_in_ruleXAttributeType2199); 

                            createLeafNode(grammarAccess.getXAttributeTypeAccess().getDefaultValueKeyword_16_0(), null); 
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1243:1: ( (lv_defaultValue_22_0= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1244:1: (lv_defaultValue_22_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1244:1: (lv_defaultValue_22_0= RULE_STRING )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1245:3: lv_defaultValue_22_0= RULE_STRING
                    {
                    lv_defaultValue_22_0=(Token)input.LT(1);
                    match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXAttributeType2216); 

                    			createLeafNode(grammarAccess.getXAttributeTypeAccess().getDefaultValueSTRINGTerminalRuleCall_16_1_0(), "defaultValue"); 
                    		

                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getXAttributeTypeRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                    	        try {
                    	       		set(
                    	       			current, 
                    	       			"defaultValue",
                    	        		lv_defaultValue_22_0, 
                    	        		"STRING", 
                    	        		lastConsumedNode);
                    	        } catch (ValueConverterException vce) {
                    				handleValueConverterException(vce);
                    	        }
                    	    

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1267:4: ( 'fileExtension' ( (lv_fileExtension_24_0= RULE_STRING ) ) )?
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==36) ) {
                alt20=1;
            }
            switch (alt20) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1267:6: 'fileExtension' ( (lv_fileExtension_24_0= RULE_STRING ) )
                    {
                    match(input,36,FOLLOW_36_in_ruleXAttributeType2234); 

                            createLeafNode(grammarAccess.getXAttributeTypeAccess().getFileExtensionKeyword_17_0(), null); 
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1271:1: ( (lv_fileExtension_24_0= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1272:1: (lv_fileExtension_24_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1272:1: (lv_fileExtension_24_0= RULE_STRING )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1273:3: lv_fileExtension_24_0= RULE_STRING
                    {
                    lv_fileExtension_24_0=(Token)input.LT(1);
                    match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXAttributeType2251); 

                    			createLeafNode(grammarAccess.getXAttributeTypeAccess().getFileExtensionSTRINGTerminalRuleCall_17_1_0(), "fileExtension"); 
                    		

                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getXAttributeTypeRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                    	        try {
                    	       		set(
                    	       			current, 
                    	       			"fileExtension",
                    	        		lv_fileExtension_24_0, 
                    	        		"STRING", 
                    	        		lastConsumedNode);
                    	        } catch (ValueConverterException vce) {
                    				handleValueConverterException(vce);
                    	        }
                    	    

                    }


                    }


                    }
                    break;

            }

            match(input,20,FOLLOW_20_in_ruleXAttributeType2268); 

                    createLeafNode(grammarAccess.getXAttributeTypeAccess().getRightCurlyBracketKeyword_18(), null); 
                

            }


            }

             resetLookahead(); 
                	lastConsumedNode = currentNode;
                
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end ruleXAttributeType


    // $ANTLR start entryRuleAttributeBaseType
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1307:1: entryRuleAttributeBaseType returns [String current=null] : iv_ruleAttributeBaseType= ruleAttributeBaseType EOF ;
    public final String entryRuleAttributeBaseType() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleAttributeBaseType = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1308:2: (iv_ruleAttributeBaseType= ruleAttributeBaseType EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1309:2: iv_ruleAttributeBaseType= ruleAttributeBaseType EOF
            {
             currentNode = createCompositeNode(grammarAccess.getAttributeBaseTypeRule(), currentNode); 
            pushFollow(FOLLOW_ruleAttributeBaseType_in_entryRuleAttributeBaseType2305);
            iv_ruleAttributeBaseType=ruleAttributeBaseType();
            _fsp--;

             current =iv_ruleAttributeBaseType.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttributeBaseType2316); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end entryRuleAttributeBaseType


    // $ANTLR start ruleAttributeBaseType
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1316:1: ruleAttributeBaseType returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'BooleanAttribute' | kw= 'CompressedContentAttribute' | kw= 'DateAttribute' | kw= 'EnumeratedAttribute' | kw= 'FloatingPointAttribute' | kw= 'IntegerAttribute' | kw= 'JavaObjectAttribute' | kw= 'StringAttribute' | kw= 'WordAttribute' | this_QUALIFIED_NAME_9= ruleQUALIFIED_NAME ) ;
    public final AntlrDatatypeRuleToken ruleAttributeBaseType() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        AntlrDatatypeRuleToken this_QUALIFIED_NAME_9 = null;


         setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1321:6: ( (kw= 'BooleanAttribute' | kw= 'CompressedContentAttribute' | kw= 'DateAttribute' | kw= 'EnumeratedAttribute' | kw= 'FloatingPointAttribute' | kw= 'IntegerAttribute' | kw= 'JavaObjectAttribute' | kw= 'StringAttribute' | kw= 'WordAttribute' | this_QUALIFIED_NAME_9= ruleQUALIFIED_NAME ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1322:1: (kw= 'BooleanAttribute' | kw= 'CompressedContentAttribute' | kw= 'DateAttribute' | kw= 'EnumeratedAttribute' | kw= 'FloatingPointAttribute' | kw= 'IntegerAttribute' | kw= 'JavaObjectAttribute' | kw= 'StringAttribute' | kw= 'WordAttribute' | this_QUALIFIED_NAME_9= ruleQUALIFIED_NAME )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1322:1: (kw= 'BooleanAttribute' | kw= 'CompressedContentAttribute' | kw= 'DateAttribute' | kw= 'EnumeratedAttribute' | kw= 'FloatingPointAttribute' | kw= 'IntegerAttribute' | kw= 'JavaObjectAttribute' | kw= 'StringAttribute' | kw= 'WordAttribute' | this_QUALIFIED_NAME_9= ruleQUALIFIED_NAME )
            int alt21=10;
            switch ( input.LA(1) ) {
            case 37:
                {
                alt21=1;
                }
                break;
            case 38:
                {
                alt21=2;
                }
                break;
            case 39:
                {
                alt21=3;
                }
                break;
            case 40:
                {
                alt21=4;
                }
                break;
            case 41:
                {
                alt21=5;
                }
                break;
            case 42:
                {
                alt21=6;
                }
                break;
            case 43:
                {
                alt21=7;
                }
                break;
            case 44:
                {
                alt21=8;
                }
                break;
            case 45:
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
                    new NoViableAltException("1322:1: (kw= 'BooleanAttribute' | kw= 'CompressedContentAttribute' | kw= 'DateAttribute' | kw= 'EnumeratedAttribute' | kw= 'FloatingPointAttribute' | kw= 'IntegerAttribute' | kw= 'JavaObjectAttribute' | kw= 'StringAttribute' | kw= 'WordAttribute' | this_QUALIFIED_NAME_9= ruleQUALIFIED_NAME )", 21, 0, input);

                throw nvae;
            }

            switch (alt21) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1323:2: kw= 'BooleanAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,37,FOLLOW_37_in_ruleAttributeBaseType2354); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getBooleanAttributeKeyword_0(), null); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1330:2: kw= 'CompressedContentAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,38,FOLLOW_38_in_ruleAttributeBaseType2373); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getCompressedContentAttributeKeyword_1(), null); 
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1337:2: kw= 'DateAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,39,FOLLOW_39_in_ruleAttributeBaseType2392); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getDateAttributeKeyword_2(), null); 
                        

                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1344:2: kw= 'EnumeratedAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,40,FOLLOW_40_in_ruleAttributeBaseType2411); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getEnumeratedAttributeKeyword_3(), null); 
                        

                    }
                    break;
                case 5 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1351:2: kw= 'FloatingPointAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,41,FOLLOW_41_in_ruleAttributeBaseType2430); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getFloatingPointAttributeKeyword_4(), null); 
                        

                    }
                    break;
                case 6 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1358:2: kw= 'IntegerAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,42,FOLLOW_42_in_ruleAttributeBaseType2449); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getIntegerAttributeKeyword_5(), null); 
                        

                    }
                    break;
                case 7 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1365:2: kw= 'JavaObjectAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,43,FOLLOW_43_in_ruleAttributeBaseType2468); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getJavaObjectAttributeKeyword_6(), null); 
                        

                    }
                    break;
                case 8 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1372:2: kw= 'StringAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,44,FOLLOW_44_in_ruleAttributeBaseType2487); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getStringAttributeKeyword_7(), null); 
                        

                    }
                    break;
                case 9 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1379:2: kw= 'WordAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,45,FOLLOW_45_in_ruleAttributeBaseType2506); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getWordAttributeKeyword_8(), null); 
                        

                    }
                    break;
                case 10 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1386:5: this_QUALIFIED_NAME_9= ruleQUALIFIED_NAME
                    {
                     
                            currentNode=createCompositeNode(grammarAccess.getAttributeBaseTypeAccess().getQUALIFIED_NAMEParserRuleCall_9(), currentNode); 
                        
                    pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_ruleAttributeBaseType2534);
                    this_QUALIFIED_NAME_9=ruleQUALIFIED_NAME();
                    _fsp--;


                    		current.merge(this_QUALIFIED_NAME_9);
                        
                     
                            currentNode = currentNode.getParent();
                        

                    }
                    break;

            }


            }

             resetLookahead(); 
            	    lastConsumedNode = currentNode;
                
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end ruleAttributeBaseType


    // $ANTLR start entryRuleXOseeEnumType
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1404:1: entryRuleXOseeEnumType returns [EObject current=null] : iv_ruleXOseeEnumType= ruleXOseeEnumType EOF ;
    public final EObject entryRuleXOseeEnumType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleXOseeEnumType = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1405:2: (iv_ruleXOseeEnumType= ruleXOseeEnumType EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1406:2: iv_ruleXOseeEnumType= ruleXOseeEnumType EOF
            {
             currentNode = createCompositeNode(grammarAccess.getXOseeEnumTypeRule(), currentNode); 
            pushFollow(FOLLOW_ruleXOseeEnumType_in_entryRuleXOseeEnumType2579);
            iv_ruleXOseeEnumType=ruleXOseeEnumType();
            _fsp--;

             current =iv_ruleXOseeEnumType; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleXOseeEnumType2589); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end entryRuleXOseeEnumType


    // $ANTLR start ruleXOseeEnumType
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1413:1: ruleXOseeEnumType returns [EObject current=null] : ( 'oseeEnumType' ( (lv_name_1_0= ruleENUM_TYPE_REFERENCE ) ) '{' 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) ( (lv_enumEntries_5_0= ruleXOseeEnumEntry ) )* '}' ) ;
    public final EObject ruleXOseeEnumType() throws RecognitionException {
        EObject current = null;

        Token lv_typeGuid_4_0=null;
        AntlrDatatypeRuleToken lv_name_1_0 = null;

        EObject lv_enumEntries_5_0 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1418:6: ( ( 'oseeEnumType' ( (lv_name_1_0= ruleENUM_TYPE_REFERENCE ) ) '{' 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) ( (lv_enumEntries_5_0= ruleXOseeEnumEntry ) )* '}' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1419:1: ( 'oseeEnumType' ( (lv_name_1_0= ruleENUM_TYPE_REFERENCE ) ) '{' 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) ( (lv_enumEntries_5_0= ruleXOseeEnumEntry ) )* '}' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1419:1: ( 'oseeEnumType' ( (lv_name_1_0= ruleENUM_TYPE_REFERENCE ) ) '{' 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) ( (lv_enumEntries_5_0= ruleXOseeEnumEntry ) )* '}' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1419:3: 'oseeEnumType' ( (lv_name_1_0= ruleENUM_TYPE_REFERENCE ) ) '{' 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) ( (lv_enumEntries_5_0= ruleXOseeEnumEntry ) )* '}'
            {
            match(input,46,FOLLOW_46_in_ruleXOseeEnumType2624); 

                    createLeafNode(grammarAccess.getXOseeEnumTypeAccess().getOseeEnumTypeKeyword_0(), null); 
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1423:1: ( (lv_name_1_0= ruleENUM_TYPE_REFERENCE ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1424:1: (lv_name_1_0= ruleENUM_TYPE_REFERENCE )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1424:1: (lv_name_1_0= ruleENUM_TYPE_REFERENCE )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1425:3: lv_name_1_0= ruleENUM_TYPE_REFERENCE
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getXOseeEnumTypeAccess().getNameENUM_TYPE_REFERENCEParserRuleCall_1_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleENUM_TYPE_REFERENCE_in_ruleXOseeEnumType2645);
            lv_name_1_0=ruleENUM_TYPE_REFERENCE();
            _fsp--;


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getXOseeEnumTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode.getParent(), current);
            	        }
            	        try {
            	       		set(
            	       			current, 
            	       			"name",
            	        		lv_name_1_0, 
            	        		"ENUM_TYPE_REFERENCE", 
            	        		currentNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	        currentNode = currentNode.getParent();
            	    

            }


            }

            match(input,18,FOLLOW_18_in_ruleXOseeEnumType2655); 

                    createLeafNode(grammarAccess.getXOseeEnumTypeAccess().getLeftCurlyBracketKeyword_2(), null); 
                
            match(input,19,FOLLOW_19_in_ruleXOseeEnumType2665); 

                    createLeafNode(grammarAccess.getXOseeEnumTypeAccess().getGuidKeyword_3(), null); 
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1455:1: ( (lv_typeGuid_4_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1456:1: (lv_typeGuid_4_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1456:1: (lv_typeGuid_4_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1457:3: lv_typeGuid_4_0= RULE_STRING
            {
            lv_typeGuid_4_0=(Token)input.LT(1);
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXOseeEnumType2682); 

            			createLeafNode(grammarAccess.getXOseeEnumTypeAccess().getTypeGuidSTRINGTerminalRuleCall_4_0(), "typeGuid"); 
            		

            	        if (current==null) {
            	            current = factory.create(grammarAccess.getXOseeEnumTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
            	        try {
            	       		set(
            	       			current, 
            	       			"typeGuid",
            	        		lv_typeGuid_4_0, 
            	        		"STRING", 
            	        		lastConsumedNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	    

            }


            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1479:2: ( (lv_enumEntries_5_0= ruleXOseeEnumEntry ) )*
            loop22:
            do {
                int alt22=2;
                int LA22_0 = input.LA(1);

                if ( (LA22_0==47) ) {
                    alt22=1;
                }


                switch (alt22) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1480:1: (lv_enumEntries_5_0= ruleXOseeEnumEntry )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1480:1: (lv_enumEntries_5_0= ruleXOseeEnumEntry )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1481:3: lv_enumEntries_5_0= ruleXOseeEnumEntry
            	    {
            	     
            	    	        currentNode=createCompositeNode(grammarAccess.getXOseeEnumTypeAccess().getEnumEntriesXOseeEnumEntryParserRuleCall_5_0(), currentNode); 
            	    	    
            	    pushFollow(FOLLOW_ruleXOseeEnumEntry_in_ruleXOseeEnumType2708);
            	    lv_enumEntries_5_0=ruleXOseeEnumEntry();
            	    _fsp--;


            	    	        if (current==null) {
            	    	            current = factory.create(grammarAccess.getXOseeEnumTypeRule().getType().getClassifier());
            	    	            associateNodeWithAstElement(currentNode.getParent(), current);
            	    	        }
            	    	        try {
            	    	       		add(
            	    	       			current, 
            	    	       			"enumEntries",
            	    	        		lv_enumEntries_5_0, 
            	    	        		"XOseeEnumEntry", 
            	    	        		currentNode);
            	    	        } catch (ValueConverterException vce) {
            	    				handleValueConverterException(vce);
            	    	        }
            	    	        currentNode = currentNode.getParent();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    break loop22;
                }
            } while (true);

            match(input,20,FOLLOW_20_in_ruleXOseeEnumType2719); 

                    createLeafNode(grammarAccess.getXOseeEnumTypeAccess().getRightCurlyBracketKeyword_6(), null); 
                

            }


            }

             resetLookahead(); 
                	lastConsumedNode = currentNode;
                
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end ruleXOseeEnumType


    // $ANTLR start entryRuleXOseeEnumEntry
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1515:1: entryRuleXOseeEnumEntry returns [EObject current=null] : iv_ruleXOseeEnumEntry= ruleXOseeEnumEntry EOF ;
    public final EObject entryRuleXOseeEnumEntry() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleXOseeEnumEntry = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1516:2: (iv_ruleXOseeEnumEntry= ruleXOseeEnumEntry EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1517:2: iv_ruleXOseeEnumEntry= ruleXOseeEnumEntry EOF
            {
             currentNode = createCompositeNode(grammarAccess.getXOseeEnumEntryRule(), currentNode); 
            pushFollow(FOLLOW_ruleXOseeEnumEntry_in_entryRuleXOseeEnumEntry2755);
            iv_ruleXOseeEnumEntry=ruleXOseeEnumEntry();
            _fsp--;

             current =iv_ruleXOseeEnumEntry; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleXOseeEnumEntry2765); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end entryRuleXOseeEnumEntry


    // $ANTLR start ruleXOseeEnumEntry
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1524:1: ruleXOseeEnumEntry returns [EObject current=null] : ( 'entry' ( (lv_name_1_0= ruleENUM_ENTRY_TYPE_REFERENCE ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? ( 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? ) ;
    public final EObject ruleXOseeEnumEntry() throws RecognitionException {
        EObject current = null;

        Token lv_ordinal_2_0=null;
        Token lv_entryGuid_4_0=null;
        AntlrDatatypeRuleToken lv_name_1_0 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1529:6: ( ( 'entry' ( (lv_name_1_0= ruleENUM_ENTRY_TYPE_REFERENCE ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? ( 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1530:1: ( 'entry' ( (lv_name_1_0= ruleENUM_ENTRY_TYPE_REFERENCE ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? ( 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1530:1: ( 'entry' ( (lv_name_1_0= ruleENUM_ENTRY_TYPE_REFERENCE ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? ( 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1530:3: 'entry' ( (lv_name_1_0= ruleENUM_ENTRY_TYPE_REFERENCE ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? ( 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )?
            {
            match(input,47,FOLLOW_47_in_ruleXOseeEnumEntry2800); 

                    createLeafNode(grammarAccess.getXOseeEnumEntryAccess().getEntryKeyword_0(), null); 
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1534:1: ( (lv_name_1_0= ruleENUM_ENTRY_TYPE_REFERENCE ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1535:1: (lv_name_1_0= ruleENUM_ENTRY_TYPE_REFERENCE )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1535:1: (lv_name_1_0= ruleENUM_ENTRY_TYPE_REFERENCE )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1536:3: lv_name_1_0= ruleENUM_ENTRY_TYPE_REFERENCE
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getXOseeEnumEntryAccess().getNameENUM_ENTRY_TYPE_REFERENCEParserRuleCall_1_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleENUM_ENTRY_TYPE_REFERENCE_in_ruleXOseeEnumEntry2821);
            lv_name_1_0=ruleENUM_ENTRY_TYPE_REFERENCE();
            _fsp--;


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getXOseeEnumEntryRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode.getParent(), current);
            	        }
            	        try {
            	       		set(
            	       			current, 
            	       			"name",
            	        		lv_name_1_0, 
            	        		"ENUM_ENTRY_TYPE_REFERENCE", 
            	        		currentNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	        currentNode = currentNode.getParent();
            	    

            }


            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1558:2: ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==RULE_WHOLE_NUM_STR) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1559:1: (lv_ordinal_2_0= RULE_WHOLE_NUM_STR )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1559:1: (lv_ordinal_2_0= RULE_WHOLE_NUM_STR )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1560:3: lv_ordinal_2_0= RULE_WHOLE_NUM_STR
                    {
                    lv_ordinal_2_0=(Token)input.LT(1);
                    match(input,RULE_WHOLE_NUM_STR,FOLLOW_RULE_WHOLE_NUM_STR_in_ruleXOseeEnumEntry2838); 

                    			createLeafNode(grammarAccess.getXOseeEnumEntryAccess().getOrdinalWHOLE_NUM_STRTerminalRuleCall_2_0(), "ordinal"); 
                    		

                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getXOseeEnumEntryRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                    	        try {
                    	       		set(
                    	       			current, 
                    	       			"ordinal",
                    	        		lv_ordinal_2_0, 
                    	        		"WHOLE_NUM_STR", 
                    	        		lastConsumedNode);
                    	        } catch (ValueConverterException vce) {
                    				handleValueConverterException(vce);
                    	        }
                    	    

                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1582:3: ( 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )?
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==48) ) {
                alt24=1;
            }
            switch (alt24) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1582:5: 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) )
                    {
                    match(input,48,FOLLOW_48_in_ruleXOseeEnumEntry2855); 

                            createLeafNode(grammarAccess.getXOseeEnumEntryAccess().getEntryGuidKeyword_3_0(), null); 
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1586:1: ( (lv_entryGuid_4_0= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1587:1: (lv_entryGuid_4_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1587:1: (lv_entryGuid_4_0= RULE_STRING )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1588:3: lv_entryGuid_4_0= RULE_STRING
                    {
                    lv_entryGuid_4_0=(Token)input.LT(1);
                    match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXOseeEnumEntry2872); 

                    			createLeafNode(grammarAccess.getXOseeEnumEntryAccess().getEntryGuidSTRINGTerminalRuleCall_3_1_0(), "entryGuid"); 
                    		

                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getXOseeEnumEntryRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                    	        try {
                    	       		set(
                    	       			current, 
                    	       			"entryGuid",
                    	        		lv_entryGuid_4_0, 
                    	        		"STRING", 
                    	        		lastConsumedNode);
                    	        } catch (ValueConverterException vce) {
                    				handleValueConverterException(vce);
                    	        }
                    	    

                    }


                    }


                    }
                    break;

            }


            }


            }

             resetLookahead(); 
                	lastConsumedNode = currentNode;
                
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end ruleXOseeEnumEntry


    // $ANTLR start entryRuleXOseeEnumOverride
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1618:1: entryRuleXOseeEnumOverride returns [EObject current=null] : iv_ruleXOseeEnumOverride= ruleXOseeEnumOverride EOF ;
    public final EObject entryRuleXOseeEnumOverride() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleXOseeEnumOverride = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1619:2: (iv_ruleXOseeEnumOverride= ruleXOseeEnumOverride EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1620:2: iv_ruleXOseeEnumOverride= ruleXOseeEnumOverride EOF
            {
             currentNode = createCompositeNode(grammarAccess.getXOseeEnumOverrideRule(), currentNode); 
            pushFollow(FOLLOW_ruleXOseeEnumOverride_in_entryRuleXOseeEnumOverride2915);
            iv_ruleXOseeEnumOverride=ruleXOseeEnumOverride();
            _fsp--;

             current =iv_ruleXOseeEnumOverride; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleXOseeEnumOverride2925); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end entryRuleXOseeEnumOverride


    // $ANTLR start ruleXOseeEnumOverride
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1627:1: ruleXOseeEnumOverride returns [EObject current=null] : ( 'overrides enum' ( ( ruleENUM_TYPE_REFERENCE ) ) '{' ( (lv_inheritAll_3_0= 'inheritAll' ) )? ( (lv_overrideOptions_4_0= ruleOverrideOption ) )* '}' ) ;
    public final EObject ruleXOseeEnumOverride() throws RecognitionException {
        EObject current = null;

        Token lv_inheritAll_3_0=null;
        EObject lv_overrideOptions_4_0 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1632:6: ( ( 'overrides enum' ( ( ruleENUM_TYPE_REFERENCE ) ) '{' ( (lv_inheritAll_3_0= 'inheritAll' ) )? ( (lv_overrideOptions_4_0= ruleOverrideOption ) )* '}' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1633:1: ( 'overrides enum' ( ( ruleENUM_TYPE_REFERENCE ) ) '{' ( (lv_inheritAll_3_0= 'inheritAll' ) )? ( (lv_overrideOptions_4_0= ruleOverrideOption ) )* '}' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1633:1: ( 'overrides enum' ( ( ruleENUM_TYPE_REFERENCE ) ) '{' ( (lv_inheritAll_3_0= 'inheritAll' ) )? ( (lv_overrideOptions_4_0= ruleOverrideOption ) )* '}' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1633:3: 'overrides enum' ( ( ruleENUM_TYPE_REFERENCE ) ) '{' ( (lv_inheritAll_3_0= 'inheritAll' ) )? ( (lv_overrideOptions_4_0= ruleOverrideOption ) )* '}'
            {
            match(input,49,FOLLOW_49_in_ruleXOseeEnumOverride2960); 

                    createLeafNode(grammarAccess.getXOseeEnumOverrideAccess().getOverridesEnumKeyword_0(), null); 
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1637:1: ( ( ruleENUM_TYPE_REFERENCE ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1638:1: ( ruleENUM_TYPE_REFERENCE )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1638:1: ( ruleENUM_TYPE_REFERENCE )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1639:3: ruleENUM_TYPE_REFERENCE
            {

            			if (current==null) {
            	            current = factory.create(grammarAccess.getXOseeEnumOverrideRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
                    
             
            	        currentNode=createCompositeNode(grammarAccess.getXOseeEnumOverrideAccess().getOverridenEnumTypeXOseeEnumTypeCrossReference_1_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleENUM_TYPE_REFERENCE_in_ruleXOseeEnumOverride2983);
            ruleENUM_TYPE_REFERENCE();
            _fsp--;

             
            	        currentNode = currentNode.getParent();
            	    

            }


            }

            match(input,18,FOLLOW_18_in_ruleXOseeEnumOverride2993); 

                    createLeafNode(grammarAccess.getXOseeEnumOverrideAccess().getLeftCurlyBracketKeyword_2(), null); 
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1657:1: ( (lv_inheritAll_3_0= 'inheritAll' ) )?
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==50) ) {
                alt25=1;
            }
            switch (alt25) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1658:1: (lv_inheritAll_3_0= 'inheritAll' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1658:1: (lv_inheritAll_3_0= 'inheritAll' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1659:3: lv_inheritAll_3_0= 'inheritAll'
                    {
                    lv_inheritAll_3_0=(Token)input.LT(1);
                    match(input,50,FOLLOW_50_in_ruleXOseeEnumOverride3011); 

                            createLeafNode(grammarAccess.getXOseeEnumOverrideAccess().getInheritAllInheritAllKeyword_3_0(), "inheritAll"); 
                        

                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getXOseeEnumOverrideRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                    	        
                    	        try {
                    	       		set(current, "inheritAll", true, "inheritAll", lastConsumedNode);
                    	        } catch (ValueConverterException vce) {
                    				handleValueConverterException(vce);
                    	        }
                    	    

                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1678:3: ( (lv_overrideOptions_4_0= ruleOverrideOption ) )*
            loop26:
            do {
                int alt26=2;
                int LA26_0 = input.LA(1);

                if ( ((LA26_0>=51 && LA26_0<=52)) ) {
                    alt26=1;
                }


                switch (alt26) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1679:1: (lv_overrideOptions_4_0= ruleOverrideOption )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1679:1: (lv_overrideOptions_4_0= ruleOverrideOption )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1680:3: lv_overrideOptions_4_0= ruleOverrideOption
            	    {
            	     
            	    	        currentNode=createCompositeNode(grammarAccess.getXOseeEnumOverrideAccess().getOverrideOptionsOverrideOptionParserRuleCall_4_0(), currentNode); 
            	    	    
            	    pushFollow(FOLLOW_ruleOverrideOption_in_ruleXOseeEnumOverride3046);
            	    lv_overrideOptions_4_0=ruleOverrideOption();
            	    _fsp--;


            	    	        if (current==null) {
            	    	            current = factory.create(grammarAccess.getXOseeEnumOverrideRule().getType().getClassifier());
            	    	            associateNodeWithAstElement(currentNode.getParent(), current);
            	    	        }
            	    	        try {
            	    	       		add(
            	    	       			current, 
            	    	       			"overrideOptions",
            	    	        		lv_overrideOptions_4_0, 
            	    	        		"OverrideOption", 
            	    	        		currentNode);
            	    	        } catch (ValueConverterException vce) {
            	    				handleValueConverterException(vce);
            	    	        }
            	    	        currentNode = currentNode.getParent();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    break loop26;
                }
            } while (true);

            match(input,20,FOLLOW_20_in_ruleXOseeEnumOverride3057); 

                    createLeafNode(grammarAccess.getXOseeEnumOverrideAccess().getRightCurlyBracketKeyword_5(), null); 
                

            }


            }

             resetLookahead(); 
                	lastConsumedNode = currentNode;
                
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end ruleXOseeEnumOverride


    // $ANTLR start entryRuleOverrideOption
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1714:1: entryRuleOverrideOption returns [EObject current=null] : iv_ruleOverrideOption= ruleOverrideOption EOF ;
    public final EObject entryRuleOverrideOption() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOverrideOption = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1715:2: (iv_ruleOverrideOption= ruleOverrideOption EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1716:2: iv_ruleOverrideOption= ruleOverrideOption EOF
            {
             currentNode = createCompositeNode(grammarAccess.getOverrideOptionRule(), currentNode); 
            pushFollow(FOLLOW_ruleOverrideOption_in_entryRuleOverrideOption3093);
            iv_ruleOverrideOption=ruleOverrideOption();
            _fsp--;

             current =iv_ruleOverrideOption; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleOverrideOption3103); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end entryRuleOverrideOption


    // $ANTLR start ruleOverrideOption
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1723:1: ruleOverrideOption returns [EObject current=null] : (this_AddEnum_0= ruleAddEnum | this_RemoveEnum_1= ruleRemoveEnum ) ;
    public final EObject ruleOverrideOption() throws RecognitionException {
        EObject current = null;

        EObject this_AddEnum_0 = null;

        EObject this_RemoveEnum_1 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1728:6: ( (this_AddEnum_0= ruleAddEnum | this_RemoveEnum_1= ruleRemoveEnum ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1729:1: (this_AddEnum_0= ruleAddEnum | this_RemoveEnum_1= ruleRemoveEnum )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1729:1: (this_AddEnum_0= ruleAddEnum | this_RemoveEnum_1= ruleRemoveEnum )
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==51) ) {
                alt27=1;
            }
            else if ( (LA27_0==52) ) {
                alt27=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("1729:1: (this_AddEnum_0= ruleAddEnum | this_RemoveEnum_1= ruleRemoveEnum )", 27, 0, input);

                throw nvae;
            }
            switch (alt27) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1730:5: this_AddEnum_0= ruleAddEnum
                    {
                     
                            currentNode=createCompositeNode(grammarAccess.getOverrideOptionAccess().getAddEnumParserRuleCall_0(), currentNode); 
                        
                    pushFollow(FOLLOW_ruleAddEnum_in_ruleOverrideOption3150);
                    this_AddEnum_0=ruleAddEnum();
                    _fsp--;

                     
                            current = this_AddEnum_0; 
                            currentNode = currentNode.getParent();
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1740:5: this_RemoveEnum_1= ruleRemoveEnum
                    {
                     
                            currentNode=createCompositeNode(grammarAccess.getOverrideOptionAccess().getRemoveEnumParserRuleCall_1(), currentNode); 
                        
                    pushFollow(FOLLOW_ruleRemoveEnum_in_ruleOverrideOption3177);
                    this_RemoveEnum_1=ruleRemoveEnum();
                    _fsp--;

                     
                            current = this_RemoveEnum_1; 
                            currentNode = currentNode.getParent();
                        

                    }
                    break;

            }


            }

             resetLookahead(); 
                	lastConsumedNode = currentNode;
                
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end ruleOverrideOption


    // $ANTLR start entryRuleAddEnum
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1756:1: entryRuleAddEnum returns [EObject current=null] : iv_ruleAddEnum= ruleAddEnum EOF ;
    public final EObject entryRuleAddEnum() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAddEnum = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1757:2: (iv_ruleAddEnum= ruleAddEnum EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1758:2: iv_ruleAddEnum= ruleAddEnum EOF
            {
             currentNode = createCompositeNode(grammarAccess.getAddEnumRule(), currentNode); 
            pushFollow(FOLLOW_ruleAddEnum_in_entryRuleAddEnum3212);
            iv_ruleAddEnum=ruleAddEnum();
            _fsp--;

             current =iv_ruleAddEnum; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAddEnum3222); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end entryRuleAddEnum


    // $ANTLR start ruleAddEnum
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1765:1: ruleAddEnum returns [EObject current=null] : ( 'add' ( (lv_enumEntry_1_0= ruleENUM_ENTRY_TYPE_REFERENCE ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? ( 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? ) ;
    public final EObject ruleAddEnum() throws RecognitionException {
        EObject current = null;

        Token lv_ordinal_2_0=null;
        Token lv_entryGuid_4_0=null;
        AntlrDatatypeRuleToken lv_enumEntry_1_0 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1770:6: ( ( 'add' ( (lv_enumEntry_1_0= ruleENUM_ENTRY_TYPE_REFERENCE ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? ( 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1771:1: ( 'add' ( (lv_enumEntry_1_0= ruleENUM_ENTRY_TYPE_REFERENCE ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? ( 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1771:1: ( 'add' ( (lv_enumEntry_1_0= ruleENUM_ENTRY_TYPE_REFERENCE ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? ( 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1771:3: 'add' ( (lv_enumEntry_1_0= ruleENUM_ENTRY_TYPE_REFERENCE ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? ( 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )?
            {
            match(input,51,FOLLOW_51_in_ruleAddEnum3257); 

                    createLeafNode(grammarAccess.getAddEnumAccess().getAddKeyword_0(), null); 
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1775:1: ( (lv_enumEntry_1_0= ruleENUM_ENTRY_TYPE_REFERENCE ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1776:1: (lv_enumEntry_1_0= ruleENUM_ENTRY_TYPE_REFERENCE )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1776:1: (lv_enumEntry_1_0= ruleENUM_ENTRY_TYPE_REFERENCE )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1777:3: lv_enumEntry_1_0= ruleENUM_ENTRY_TYPE_REFERENCE
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getAddEnumAccess().getEnumEntryENUM_ENTRY_TYPE_REFERENCEParserRuleCall_1_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleENUM_ENTRY_TYPE_REFERENCE_in_ruleAddEnum3278);
            lv_enumEntry_1_0=ruleENUM_ENTRY_TYPE_REFERENCE();
            _fsp--;


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getAddEnumRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode.getParent(), current);
            	        }
            	        try {
            	       		set(
            	       			current, 
            	       			"enumEntry",
            	        		lv_enumEntry_1_0, 
            	        		"ENUM_ENTRY_TYPE_REFERENCE", 
            	        		currentNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	        currentNode = currentNode.getParent();
            	    

            }


            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1799:2: ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )?
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==RULE_WHOLE_NUM_STR) ) {
                alt28=1;
            }
            switch (alt28) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1800:1: (lv_ordinal_2_0= RULE_WHOLE_NUM_STR )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1800:1: (lv_ordinal_2_0= RULE_WHOLE_NUM_STR )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1801:3: lv_ordinal_2_0= RULE_WHOLE_NUM_STR
                    {
                    lv_ordinal_2_0=(Token)input.LT(1);
                    match(input,RULE_WHOLE_NUM_STR,FOLLOW_RULE_WHOLE_NUM_STR_in_ruleAddEnum3295); 

                    			createLeafNode(grammarAccess.getAddEnumAccess().getOrdinalWHOLE_NUM_STRTerminalRuleCall_2_0(), "ordinal"); 
                    		

                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getAddEnumRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                    	        try {
                    	       		set(
                    	       			current, 
                    	       			"ordinal",
                    	        		lv_ordinal_2_0, 
                    	        		"WHOLE_NUM_STR", 
                    	        		lastConsumedNode);
                    	        } catch (ValueConverterException vce) {
                    				handleValueConverterException(vce);
                    	        }
                    	    

                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1823:3: ( 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )?
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==48) ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1823:5: 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) )
                    {
                    match(input,48,FOLLOW_48_in_ruleAddEnum3312); 

                            createLeafNode(grammarAccess.getAddEnumAccess().getEntryGuidKeyword_3_0(), null); 
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1827:1: ( (lv_entryGuid_4_0= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1828:1: (lv_entryGuid_4_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1828:1: (lv_entryGuid_4_0= RULE_STRING )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1829:3: lv_entryGuid_4_0= RULE_STRING
                    {
                    lv_entryGuid_4_0=(Token)input.LT(1);
                    match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAddEnum3329); 

                    			createLeafNode(grammarAccess.getAddEnumAccess().getEntryGuidSTRINGTerminalRuleCall_3_1_0(), "entryGuid"); 
                    		

                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getAddEnumRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                    	        try {
                    	       		set(
                    	       			current, 
                    	       			"entryGuid",
                    	        		lv_entryGuid_4_0, 
                    	        		"STRING", 
                    	        		lastConsumedNode);
                    	        } catch (ValueConverterException vce) {
                    				handleValueConverterException(vce);
                    	        }
                    	    

                    }


                    }


                    }
                    break;

            }


            }


            }

             resetLookahead(); 
                	lastConsumedNode = currentNode;
                
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end ruleAddEnum


    // $ANTLR start entryRuleRemoveEnum
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1859:1: entryRuleRemoveEnum returns [EObject current=null] : iv_ruleRemoveEnum= ruleRemoveEnum EOF ;
    public final EObject entryRuleRemoveEnum() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRemoveEnum = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1860:2: (iv_ruleRemoveEnum= ruleRemoveEnum EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1861:2: iv_ruleRemoveEnum= ruleRemoveEnum EOF
            {
             currentNode = createCompositeNode(grammarAccess.getRemoveEnumRule(), currentNode); 
            pushFollow(FOLLOW_ruleRemoveEnum_in_entryRuleRemoveEnum3372);
            iv_ruleRemoveEnum=ruleRemoveEnum();
            _fsp--;

             current =iv_ruleRemoveEnum; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleRemoveEnum3382); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end entryRuleRemoveEnum


    // $ANTLR start ruleRemoveEnum
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1868:1: ruleRemoveEnum returns [EObject current=null] : ( 'remove' ( ( ruleENUM_ENTRY_TYPE_REFERENCE ) ) ) ;
    public final EObject ruleRemoveEnum() throws RecognitionException {
        EObject current = null;

         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1873:6: ( ( 'remove' ( ( ruleENUM_ENTRY_TYPE_REFERENCE ) ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1874:1: ( 'remove' ( ( ruleENUM_ENTRY_TYPE_REFERENCE ) ) )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1874:1: ( 'remove' ( ( ruleENUM_ENTRY_TYPE_REFERENCE ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1874:3: 'remove' ( ( ruleENUM_ENTRY_TYPE_REFERENCE ) )
            {
            match(input,52,FOLLOW_52_in_ruleRemoveEnum3417); 

                    createLeafNode(grammarAccess.getRemoveEnumAccess().getRemoveKeyword_0(), null); 
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1878:1: ( ( ruleENUM_ENTRY_TYPE_REFERENCE ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1879:1: ( ruleENUM_ENTRY_TYPE_REFERENCE )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1879:1: ( ruleENUM_ENTRY_TYPE_REFERENCE )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1880:3: ruleENUM_ENTRY_TYPE_REFERENCE
            {

            			if (current==null) {
            	            current = factory.create(grammarAccess.getRemoveEnumRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
                    
             
            	        currentNode=createCompositeNode(grammarAccess.getRemoveEnumAccess().getEnumEntryXOseeEnumEntryCrossReference_1_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleENUM_ENTRY_TYPE_REFERENCE_in_ruleRemoveEnum3440);
            ruleENUM_ENTRY_TYPE_REFERENCE();
            _fsp--;

             
            	        currentNode = currentNode.getParent();
            	    

            }


            }


            }


            }

             resetLookahead(); 
                	lastConsumedNode = currentNode;
                
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end ruleRemoveEnum


    // $ANTLR start entryRuleXRelationType
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1902:1: entryRuleXRelationType returns [EObject current=null] : iv_ruleXRelationType= ruleXRelationType EOF ;
    public final EObject entryRuleXRelationType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleXRelationType = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1903:2: (iv_ruleXRelationType= ruleXRelationType EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1904:2: iv_ruleXRelationType= ruleXRelationType EOF
            {
             currentNode = createCompositeNode(grammarAccess.getXRelationTypeRule(), currentNode); 
            pushFollow(FOLLOW_ruleXRelationType_in_entryRuleXRelationType3476);
            iv_ruleXRelationType=ruleXRelationType();
            _fsp--;

             current =iv_ruleXRelationType; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleXRelationType3486); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end entryRuleXRelationType


    // $ANTLR start ruleXRelationType
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1911:1: ruleXRelationType returns [EObject current=null] : ( 'relationType' ( (lv_name_1_0= ruleRELATION_TYPE_REFERENCE ) ) '{' 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) 'sideAName' ( (lv_sideAName_6_0= RULE_STRING ) ) 'sideAArtifactType' ( ( ruleARTIFACT_TYPE_REFERENCE ) ) 'sideBName' ( (lv_sideBName_10_0= RULE_STRING ) ) 'sideBArtifactType' ( ( ruleARTIFACT_TYPE_REFERENCE ) ) 'defaultOrderType' ( (lv_defaultOrderType_14_0= ruleRelationOrderType ) ) 'multiplicity' ( (lv_multiplicity_16_0= ruleRelationMultiplicityEnum ) ) '}' ) ;
    public final EObject ruleXRelationType() throws RecognitionException {
        EObject current = null;

        Token lv_typeGuid_4_0=null;
        Token lv_sideAName_6_0=null;
        Token lv_sideBName_10_0=null;
        AntlrDatatypeRuleToken lv_name_1_0 = null;

        AntlrDatatypeRuleToken lv_defaultOrderType_14_0 = null;

        Enumerator lv_multiplicity_16_0 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1916:6: ( ( 'relationType' ( (lv_name_1_0= ruleRELATION_TYPE_REFERENCE ) ) '{' 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) 'sideAName' ( (lv_sideAName_6_0= RULE_STRING ) ) 'sideAArtifactType' ( ( ruleARTIFACT_TYPE_REFERENCE ) ) 'sideBName' ( (lv_sideBName_10_0= RULE_STRING ) ) 'sideBArtifactType' ( ( ruleARTIFACT_TYPE_REFERENCE ) ) 'defaultOrderType' ( (lv_defaultOrderType_14_0= ruleRelationOrderType ) ) 'multiplicity' ( (lv_multiplicity_16_0= ruleRelationMultiplicityEnum ) ) '}' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1917:1: ( 'relationType' ( (lv_name_1_0= ruleRELATION_TYPE_REFERENCE ) ) '{' 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) 'sideAName' ( (lv_sideAName_6_0= RULE_STRING ) ) 'sideAArtifactType' ( ( ruleARTIFACT_TYPE_REFERENCE ) ) 'sideBName' ( (lv_sideBName_10_0= RULE_STRING ) ) 'sideBArtifactType' ( ( ruleARTIFACT_TYPE_REFERENCE ) ) 'defaultOrderType' ( (lv_defaultOrderType_14_0= ruleRelationOrderType ) ) 'multiplicity' ( (lv_multiplicity_16_0= ruleRelationMultiplicityEnum ) ) '}' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1917:1: ( 'relationType' ( (lv_name_1_0= ruleRELATION_TYPE_REFERENCE ) ) '{' 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) 'sideAName' ( (lv_sideAName_6_0= RULE_STRING ) ) 'sideAArtifactType' ( ( ruleARTIFACT_TYPE_REFERENCE ) ) 'sideBName' ( (lv_sideBName_10_0= RULE_STRING ) ) 'sideBArtifactType' ( ( ruleARTIFACT_TYPE_REFERENCE ) ) 'defaultOrderType' ( (lv_defaultOrderType_14_0= ruleRelationOrderType ) ) 'multiplicity' ( (lv_multiplicity_16_0= ruleRelationMultiplicityEnum ) ) '}' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1917:3: 'relationType' ( (lv_name_1_0= ruleRELATION_TYPE_REFERENCE ) ) '{' 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) 'sideAName' ( (lv_sideAName_6_0= RULE_STRING ) ) 'sideAArtifactType' ( ( ruleARTIFACT_TYPE_REFERENCE ) ) 'sideBName' ( (lv_sideBName_10_0= RULE_STRING ) ) 'sideBArtifactType' ( ( ruleARTIFACT_TYPE_REFERENCE ) ) 'defaultOrderType' ( (lv_defaultOrderType_14_0= ruleRelationOrderType ) ) 'multiplicity' ( (lv_multiplicity_16_0= ruleRelationMultiplicityEnum ) ) '}'
            {
            match(input,53,FOLLOW_53_in_ruleXRelationType3521); 

                    createLeafNode(grammarAccess.getXRelationTypeAccess().getRelationTypeKeyword_0(), null); 
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1921:1: ( (lv_name_1_0= ruleRELATION_TYPE_REFERENCE ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1922:1: (lv_name_1_0= ruleRELATION_TYPE_REFERENCE )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1922:1: (lv_name_1_0= ruleRELATION_TYPE_REFERENCE )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1923:3: lv_name_1_0= ruleRELATION_TYPE_REFERENCE
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getXRelationTypeAccess().getNameRELATION_TYPE_REFERENCEParserRuleCall_1_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleRELATION_TYPE_REFERENCE_in_ruleXRelationType3542);
            lv_name_1_0=ruleRELATION_TYPE_REFERENCE();
            _fsp--;


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getXRelationTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode.getParent(), current);
            	        }
            	        try {
            	       		set(
            	       			current, 
            	       			"name",
            	        		lv_name_1_0, 
            	        		"RELATION_TYPE_REFERENCE", 
            	        		currentNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	        currentNode = currentNode.getParent();
            	    

            }


            }

            match(input,18,FOLLOW_18_in_ruleXRelationType3552); 

                    createLeafNode(grammarAccess.getXRelationTypeAccess().getLeftCurlyBracketKeyword_2(), null); 
                
            match(input,19,FOLLOW_19_in_ruleXRelationType3562); 

                    createLeafNode(grammarAccess.getXRelationTypeAccess().getGuidKeyword_3(), null); 
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1953:1: ( (lv_typeGuid_4_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1954:1: (lv_typeGuid_4_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1954:1: (lv_typeGuid_4_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1955:3: lv_typeGuid_4_0= RULE_STRING
            {
            lv_typeGuid_4_0=(Token)input.LT(1);
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXRelationType3579); 

            			createLeafNode(grammarAccess.getXRelationTypeAccess().getTypeGuidSTRINGTerminalRuleCall_4_0(), "typeGuid"); 
            		

            	        if (current==null) {
            	            current = factory.create(grammarAccess.getXRelationTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
            	        try {
            	       		set(
            	       			current, 
            	       			"typeGuid",
            	        		lv_typeGuid_4_0, 
            	        		"STRING", 
            	        		lastConsumedNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	    

            }


            }

            match(input,54,FOLLOW_54_in_ruleXRelationType3594); 

                    createLeafNode(grammarAccess.getXRelationTypeAccess().getSideANameKeyword_5(), null); 
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1981:1: ( (lv_sideAName_6_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1982:1: (lv_sideAName_6_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1982:1: (lv_sideAName_6_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:1983:3: lv_sideAName_6_0= RULE_STRING
            {
            lv_sideAName_6_0=(Token)input.LT(1);
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXRelationType3611); 

            			createLeafNode(grammarAccess.getXRelationTypeAccess().getSideANameSTRINGTerminalRuleCall_6_0(), "sideAName"); 
            		

            	        if (current==null) {
            	            current = factory.create(grammarAccess.getXRelationTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
            	        try {
            	       		set(
            	       			current, 
            	       			"sideAName",
            	        		lv_sideAName_6_0, 
            	        		"STRING", 
            	        		lastConsumedNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	    

            }


            }

            match(input,55,FOLLOW_55_in_ruleXRelationType3626); 

                    createLeafNode(grammarAccess.getXRelationTypeAccess().getSideAArtifactTypeKeyword_7(), null); 
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2009:1: ( ( ruleARTIFACT_TYPE_REFERENCE ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2010:1: ( ruleARTIFACT_TYPE_REFERENCE )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2010:1: ( ruleARTIFACT_TYPE_REFERENCE )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2011:3: ruleARTIFACT_TYPE_REFERENCE
            {

            			if (current==null) {
            	            current = factory.create(grammarAccess.getXRelationTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
                    
             
            	        currentNode=createCompositeNode(grammarAccess.getXRelationTypeAccess().getSideAArtifactTypeXArtifactTypeCrossReference_8_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleARTIFACT_TYPE_REFERENCE_in_ruleXRelationType3649);
            ruleARTIFACT_TYPE_REFERENCE();
            _fsp--;

             
            	        currentNode = currentNode.getParent();
            	    

            }


            }

            match(input,56,FOLLOW_56_in_ruleXRelationType3659); 

                    createLeafNode(grammarAccess.getXRelationTypeAccess().getSideBNameKeyword_9(), null); 
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2029:1: ( (lv_sideBName_10_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2030:1: (lv_sideBName_10_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2030:1: (lv_sideBName_10_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2031:3: lv_sideBName_10_0= RULE_STRING
            {
            lv_sideBName_10_0=(Token)input.LT(1);
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXRelationType3676); 

            			createLeafNode(grammarAccess.getXRelationTypeAccess().getSideBNameSTRINGTerminalRuleCall_10_0(), "sideBName"); 
            		

            	        if (current==null) {
            	            current = factory.create(grammarAccess.getXRelationTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
            	        try {
            	       		set(
            	       			current, 
            	       			"sideBName",
            	        		lv_sideBName_10_0, 
            	        		"STRING", 
            	        		lastConsumedNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	    

            }


            }

            match(input,57,FOLLOW_57_in_ruleXRelationType3691); 

                    createLeafNode(grammarAccess.getXRelationTypeAccess().getSideBArtifactTypeKeyword_11(), null); 
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2057:1: ( ( ruleARTIFACT_TYPE_REFERENCE ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2058:1: ( ruleARTIFACT_TYPE_REFERENCE )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2058:1: ( ruleARTIFACT_TYPE_REFERENCE )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2059:3: ruleARTIFACT_TYPE_REFERENCE
            {

            			if (current==null) {
            	            current = factory.create(grammarAccess.getXRelationTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
                    
             
            	        currentNode=createCompositeNode(grammarAccess.getXRelationTypeAccess().getSideBArtifactTypeXArtifactTypeCrossReference_12_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleARTIFACT_TYPE_REFERENCE_in_ruleXRelationType3714);
            ruleARTIFACT_TYPE_REFERENCE();
            _fsp--;

             
            	        currentNode = currentNode.getParent();
            	    

            }


            }

            match(input,58,FOLLOW_58_in_ruleXRelationType3724); 

                    createLeafNode(grammarAccess.getXRelationTypeAccess().getDefaultOrderTypeKeyword_13(), null); 
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2077:1: ( (lv_defaultOrderType_14_0= ruleRelationOrderType ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2078:1: (lv_defaultOrderType_14_0= ruleRelationOrderType )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2078:1: (lv_defaultOrderType_14_0= ruleRelationOrderType )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2079:3: lv_defaultOrderType_14_0= ruleRelationOrderType
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getXRelationTypeAccess().getDefaultOrderTypeRelationOrderTypeParserRuleCall_14_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleRelationOrderType_in_ruleXRelationType3745);
            lv_defaultOrderType_14_0=ruleRelationOrderType();
            _fsp--;


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getXRelationTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode.getParent(), current);
            	        }
            	        try {
            	       		set(
            	       			current, 
            	       			"defaultOrderType",
            	        		lv_defaultOrderType_14_0, 
            	        		"RelationOrderType", 
            	        		currentNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	        currentNode = currentNode.getParent();
            	    

            }


            }

            match(input,59,FOLLOW_59_in_ruleXRelationType3755); 

                    createLeafNode(grammarAccess.getXRelationTypeAccess().getMultiplicityKeyword_15(), null); 
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2105:1: ( (lv_multiplicity_16_0= ruleRelationMultiplicityEnum ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2106:1: (lv_multiplicity_16_0= ruleRelationMultiplicityEnum )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2106:1: (lv_multiplicity_16_0= ruleRelationMultiplicityEnum )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2107:3: lv_multiplicity_16_0= ruleRelationMultiplicityEnum
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getXRelationTypeAccess().getMultiplicityRelationMultiplicityEnumEnumRuleCall_16_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleRelationMultiplicityEnum_in_ruleXRelationType3776);
            lv_multiplicity_16_0=ruleRelationMultiplicityEnum();
            _fsp--;


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getXRelationTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode.getParent(), current);
            	        }
            	        try {
            	       		set(
            	       			current, 
            	       			"multiplicity",
            	        		lv_multiplicity_16_0, 
            	        		"RelationMultiplicityEnum", 
            	        		currentNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	        currentNode = currentNode.getParent();
            	    

            }


            }

            match(input,20,FOLLOW_20_in_ruleXRelationType3786); 

                    createLeafNode(grammarAccess.getXRelationTypeAccess().getRightCurlyBracketKeyword_17(), null); 
                

            }


            }

             resetLookahead(); 
                	lastConsumedNode = currentNode;
                
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end ruleXRelationType


    // $ANTLR start entryRuleRelationOrderType
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2141:1: entryRuleRelationOrderType returns [String current=null] : iv_ruleRelationOrderType= ruleRelationOrderType EOF ;
    public final String entryRuleRelationOrderType() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleRelationOrderType = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2142:2: (iv_ruleRelationOrderType= ruleRelationOrderType EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2143:2: iv_ruleRelationOrderType= ruleRelationOrderType EOF
            {
             currentNode = createCompositeNode(grammarAccess.getRelationOrderTypeRule(), currentNode); 
            pushFollow(FOLLOW_ruleRelationOrderType_in_entryRuleRelationOrderType3823);
            iv_ruleRelationOrderType=ruleRelationOrderType();
            _fsp--;

             current =iv_ruleRelationOrderType.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleRelationOrderType3834); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end entryRuleRelationOrderType


    // $ANTLR start ruleRelationOrderType
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2150:1: ruleRelationOrderType returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'Lexicographical_Ascending' | kw= 'Lexicographical_Descending' | kw= 'Unordered' | this_ID_3= RULE_ID ) ;
    public final AntlrDatatypeRuleToken ruleRelationOrderType() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        Token this_ID_3=null;

         setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2155:6: ( (kw= 'Lexicographical_Ascending' | kw= 'Lexicographical_Descending' | kw= 'Unordered' | this_ID_3= RULE_ID ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2156:1: (kw= 'Lexicographical_Ascending' | kw= 'Lexicographical_Descending' | kw= 'Unordered' | this_ID_3= RULE_ID )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2156:1: (kw= 'Lexicographical_Ascending' | kw= 'Lexicographical_Descending' | kw= 'Unordered' | this_ID_3= RULE_ID )
            int alt30=4;
            switch ( input.LA(1) ) {
            case 60:
                {
                alt30=1;
                }
                break;
            case 61:
                {
                alt30=2;
                }
                break;
            case 62:
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
                    new NoViableAltException("2156:1: (kw= 'Lexicographical_Ascending' | kw= 'Lexicographical_Descending' | kw= 'Unordered' | this_ID_3= RULE_ID )", 30, 0, input);

                throw nvae;
            }

            switch (alt30) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2157:2: kw= 'Lexicographical_Ascending'
                    {
                    kw=(Token)input.LT(1);
                    match(input,60,FOLLOW_60_in_ruleRelationOrderType3872); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getRelationOrderTypeAccess().getLexicographical_AscendingKeyword_0(), null); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2164:2: kw= 'Lexicographical_Descending'
                    {
                    kw=(Token)input.LT(1);
                    match(input,61,FOLLOW_61_in_ruleRelationOrderType3891); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getRelationOrderTypeAccess().getLexicographical_DescendingKeyword_1(), null); 
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2171:2: kw= 'Unordered'
                    {
                    kw=(Token)input.LT(1);
                    match(input,62,FOLLOW_62_in_ruleRelationOrderType3910); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getRelationOrderTypeAccess().getUnorderedKeyword_2(), null); 
                        

                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2177:10: this_ID_3= RULE_ID
                    {
                    this_ID_3=(Token)input.LT(1);
                    match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleRelationOrderType3931); 

                    		current.merge(this_ID_3);
                        
                     
                        createLeafNode(grammarAccess.getRelationOrderTypeAccess().getIDTerminalRuleCall_3(), null); 
                        

                    }
                    break;

            }


            }

             resetLookahead(); 
            	    lastConsumedNode = currentNode;
                
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end ruleRelationOrderType


    // $ANTLR start entryRuleARTIFACT_INSTANCE_REFERENCE
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2192:1: entryRuleARTIFACT_INSTANCE_REFERENCE returns [String current=null] : iv_ruleARTIFACT_INSTANCE_REFERENCE= ruleARTIFACT_INSTANCE_REFERENCE EOF ;
    public final String entryRuleARTIFACT_INSTANCE_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleARTIFACT_INSTANCE_REFERENCE = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2193:2: (iv_ruleARTIFACT_INSTANCE_REFERENCE= ruleARTIFACT_INSTANCE_REFERENCE EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2194:2: iv_ruleARTIFACT_INSTANCE_REFERENCE= ruleARTIFACT_INSTANCE_REFERENCE EOF
            {
             currentNode = createCompositeNode(grammarAccess.getARTIFACT_INSTANCE_REFERENCERule(), currentNode); 
            pushFollow(FOLLOW_ruleARTIFACT_INSTANCE_REFERENCE_in_entryRuleARTIFACT_INSTANCE_REFERENCE3977);
            iv_ruleARTIFACT_INSTANCE_REFERENCE=ruleARTIFACT_INSTANCE_REFERENCE();
            _fsp--;

             current =iv_ruleARTIFACT_INSTANCE_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleARTIFACT_INSTANCE_REFERENCE3988); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end entryRuleARTIFACT_INSTANCE_REFERENCE


    // $ANTLR start ruleARTIFACT_INSTANCE_REFERENCE
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2201:1: ruleARTIFACT_INSTANCE_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleARTIFACT_INSTANCE_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2206:6: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2207:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)input.LT(1);
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleARTIFACT_INSTANCE_REFERENCE4027); 

            		current.merge(this_STRING_0);
                
             
                createLeafNode(grammarAccess.getARTIFACT_INSTANCE_REFERENCEAccess().getSTRINGTerminalRuleCall(), null); 
                

            }

             resetLookahead(); 
            	    lastConsumedNode = currentNode;
                
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end ruleARTIFACT_INSTANCE_REFERENCE


    // $ANTLR start entryRuleXArtifactRef
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2222:1: entryRuleXArtifactRef returns [EObject current=null] : iv_ruleXArtifactRef= ruleXArtifactRef EOF ;
    public final EObject entryRuleXArtifactRef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleXArtifactRef = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2223:2: (iv_ruleXArtifactRef= ruleXArtifactRef EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2224:2: iv_ruleXArtifactRef= ruleXArtifactRef EOF
            {
             currentNode = createCompositeNode(grammarAccess.getXArtifactRefRule(), currentNode); 
            pushFollow(FOLLOW_ruleXArtifactRef_in_entryRuleXArtifactRef4071);
            iv_ruleXArtifactRef=ruleXArtifactRef();
            _fsp--;

             current =iv_ruleXArtifactRef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleXArtifactRef4081); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end entryRuleXArtifactRef


    // $ANTLR start ruleXArtifactRef
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2231:1: ruleXArtifactRef returns [EObject current=null] : ( 'artifact' ( (lv_name_1_0= ruleARTIFACT_INSTANCE_REFERENCE ) ) 'artGuid' ( (lv_guid_3_0= RULE_STRING ) ) ';' ) ;
    public final EObject ruleXArtifactRef() throws RecognitionException {
        EObject current = null;

        Token lv_guid_3_0=null;
        AntlrDatatypeRuleToken lv_name_1_0 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2236:6: ( ( 'artifact' ( (lv_name_1_0= ruleARTIFACT_INSTANCE_REFERENCE ) ) 'artGuid' ( (lv_guid_3_0= RULE_STRING ) ) ';' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2237:1: ( 'artifact' ( (lv_name_1_0= ruleARTIFACT_INSTANCE_REFERENCE ) ) 'artGuid' ( (lv_guid_3_0= RULE_STRING ) ) ';' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2237:1: ( 'artifact' ( (lv_name_1_0= ruleARTIFACT_INSTANCE_REFERENCE ) ) 'artGuid' ( (lv_guid_3_0= RULE_STRING ) ) ';' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2237:3: 'artifact' ( (lv_name_1_0= ruleARTIFACT_INSTANCE_REFERENCE ) ) 'artGuid' ( (lv_guid_3_0= RULE_STRING ) ) ';'
            {
            match(input,63,FOLLOW_63_in_ruleXArtifactRef4116); 

                    createLeafNode(grammarAccess.getXArtifactRefAccess().getArtifactKeyword_0(), null); 
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2241:1: ( (lv_name_1_0= ruleARTIFACT_INSTANCE_REFERENCE ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2242:1: (lv_name_1_0= ruleARTIFACT_INSTANCE_REFERENCE )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2242:1: (lv_name_1_0= ruleARTIFACT_INSTANCE_REFERENCE )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2243:3: lv_name_1_0= ruleARTIFACT_INSTANCE_REFERENCE
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getXArtifactRefAccess().getNameARTIFACT_INSTANCE_REFERENCEParserRuleCall_1_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleARTIFACT_INSTANCE_REFERENCE_in_ruleXArtifactRef4137);
            lv_name_1_0=ruleARTIFACT_INSTANCE_REFERENCE();
            _fsp--;


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getXArtifactRefRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode.getParent(), current);
            	        }
            	        try {
            	       		set(
            	       			current, 
            	       			"name",
            	        		lv_name_1_0, 
            	        		"ARTIFACT_INSTANCE_REFERENCE", 
            	        		currentNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	        currentNode = currentNode.getParent();
            	    

            }


            }

            match(input,64,FOLLOW_64_in_ruleXArtifactRef4147); 

                    createLeafNode(grammarAccess.getXArtifactRefAccess().getArtGuidKeyword_2(), null); 
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2269:1: ( (lv_guid_3_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2270:1: (lv_guid_3_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2270:1: (lv_guid_3_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2271:3: lv_guid_3_0= RULE_STRING
            {
            lv_guid_3_0=(Token)input.LT(1);
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXArtifactRef4164); 

            			createLeafNode(grammarAccess.getXArtifactRefAccess().getGuidSTRINGTerminalRuleCall_3_0(), "guid"); 
            		

            	        if (current==null) {
            	            current = factory.create(grammarAccess.getXArtifactRefRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
            	        try {
            	       		set(
            	       			current, 
            	       			"guid",
            	        		lv_guid_3_0, 
            	        		"STRING", 
            	        		lastConsumedNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	    

            }


            }

            match(input,65,FOLLOW_65_in_ruleXArtifactRef4179); 

                    createLeafNode(grammarAccess.getXArtifactRefAccess().getSemicolonKeyword_4(), null); 
                

            }


            }

             resetLookahead(); 
                	lastConsumedNode = currentNode;
                
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end ruleXArtifactRef


    // $ANTLR start entryRuleBRANCH_INSTANCE_REFERENCE
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2305:1: entryRuleBRANCH_INSTANCE_REFERENCE returns [String current=null] : iv_ruleBRANCH_INSTANCE_REFERENCE= ruleBRANCH_INSTANCE_REFERENCE EOF ;
    public final String entryRuleBRANCH_INSTANCE_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleBRANCH_INSTANCE_REFERENCE = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2306:2: (iv_ruleBRANCH_INSTANCE_REFERENCE= ruleBRANCH_INSTANCE_REFERENCE EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2307:2: iv_ruleBRANCH_INSTANCE_REFERENCE= ruleBRANCH_INSTANCE_REFERENCE EOF
            {
             currentNode = createCompositeNode(grammarAccess.getBRANCH_INSTANCE_REFERENCERule(), currentNode); 
            pushFollow(FOLLOW_ruleBRANCH_INSTANCE_REFERENCE_in_entryRuleBRANCH_INSTANCE_REFERENCE4216);
            iv_ruleBRANCH_INSTANCE_REFERENCE=ruleBRANCH_INSTANCE_REFERENCE();
            _fsp--;

             current =iv_ruleBRANCH_INSTANCE_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleBRANCH_INSTANCE_REFERENCE4227); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end entryRuleBRANCH_INSTANCE_REFERENCE


    // $ANTLR start ruleBRANCH_INSTANCE_REFERENCE
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2314:1: ruleBRANCH_INSTANCE_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleBRANCH_INSTANCE_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2319:6: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2320:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)input.LT(1);
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleBRANCH_INSTANCE_REFERENCE4266); 

            		current.merge(this_STRING_0);
                
             
                createLeafNode(grammarAccess.getBRANCH_INSTANCE_REFERENCEAccess().getSTRINGTerminalRuleCall(), null); 
                

            }

             resetLookahead(); 
            	    lastConsumedNode = currentNode;
                
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end ruleBRANCH_INSTANCE_REFERENCE


    // $ANTLR start entryRuleXBranchRef
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2335:1: entryRuleXBranchRef returns [EObject current=null] : iv_ruleXBranchRef= ruleXBranchRef EOF ;
    public final EObject entryRuleXBranchRef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleXBranchRef = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2336:2: (iv_ruleXBranchRef= ruleXBranchRef EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2337:2: iv_ruleXBranchRef= ruleXBranchRef EOF
            {
             currentNode = createCompositeNode(grammarAccess.getXBranchRefRule(), currentNode); 
            pushFollow(FOLLOW_ruleXBranchRef_in_entryRuleXBranchRef4310);
            iv_ruleXBranchRef=ruleXBranchRef();
            _fsp--;

             current =iv_ruleXBranchRef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleXBranchRef4320); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end entryRuleXBranchRef


    // $ANTLR start ruleXBranchRef
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2344:1: ruleXBranchRef returns [EObject current=null] : ( 'branch' ( (lv_name_1_0= ruleBRANCH_INSTANCE_REFERENCE ) ) 'branchGuid' ( (lv_guid_3_0= RULE_STRING ) ) ';' ) ;
    public final EObject ruleXBranchRef() throws RecognitionException {
        EObject current = null;

        Token lv_guid_3_0=null;
        AntlrDatatypeRuleToken lv_name_1_0 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2349:6: ( ( 'branch' ( (lv_name_1_0= ruleBRANCH_INSTANCE_REFERENCE ) ) 'branchGuid' ( (lv_guid_3_0= RULE_STRING ) ) ';' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2350:1: ( 'branch' ( (lv_name_1_0= ruleBRANCH_INSTANCE_REFERENCE ) ) 'branchGuid' ( (lv_guid_3_0= RULE_STRING ) ) ';' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2350:1: ( 'branch' ( (lv_name_1_0= ruleBRANCH_INSTANCE_REFERENCE ) ) 'branchGuid' ( (lv_guid_3_0= RULE_STRING ) ) ';' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2350:3: 'branch' ( (lv_name_1_0= ruleBRANCH_INSTANCE_REFERENCE ) ) 'branchGuid' ( (lv_guid_3_0= RULE_STRING ) ) ';'
            {
            match(input,66,FOLLOW_66_in_ruleXBranchRef4355); 

                    createLeafNode(grammarAccess.getXBranchRefAccess().getBranchKeyword_0(), null); 
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2354:1: ( (lv_name_1_0= ruleBRANCH_INSTANCE_REFERENCE ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2355:1: (lv_name_1_0= ruleBRANCH_INSTANCE_REFERENCE )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2355:1: (lv_name_1_0= ruleBRANCH_INSTANCE_REFERENCE )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2356:3: lv_name_1_0= ruleBRANCH_INSTANCE_REFERENCE
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getXBranchRefAccess().getNameBRANCH_INSTANCE_REFERENCEParserRuleCall_1_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleBRANCH_INSTANCE_REFERENCE_in_ruleXBranchRef4376);
            lv_name_1_0=ruleBRANCH_INSTANCE_REFERENCE();
            _fsp--;


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getXBranchRefRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode.getParent(), current);
            	        }
            	        try {
            	       		set(
            	       			current, 
            	       			"name",
            	        		lv_name_1_0, 
            	        		"BRANCH_INSTANCE_REFERENCE", 
            	        		currentNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	        currentNode = currentNode.getParent();
            	    

            }


            }

            match(input,22,FOLLOW_22_in_ruleXBranchRef4386); 

                    createLeafNode(grammarAccess.getXBranchRefAccess().getBranchGuidKeyword_2(), null); 
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2382:1: ( (lv_guid_3_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2383:1: (lv_guid_3_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2383:1: (lv_guid_3_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2384:3: lv_guid_3_0= RULE_STRING
            {
            lv_guid_3_0=(Token)input.LT(1);
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXBranchRef4403); 

            			createLeafNode(grammarAccess.getXBranchRefAccess().getGuidSTRINGTerminalRuleCall_3_0(), "guid"); 
            		

            	        if (current==null) {
            	            current = factory.create(grammarAccess.getXBranchRefRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
            	        try {
            	       		set(
            	       			current, 
            	       			"guid",
            	        		lv_guid_3_0, 
            	        		"STRING", 
            	        		lastConsumedNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	    

            }


            }

            match(input,65,FOLLOW_65_in_ruleXBranchRef4418); 

                    createLeafNode(grammarAccess.getXBranchRefAccess().getSemicolonKeyword_4(), null); 
                

            }


            }

             resetLookahead(); 
                	lastConsumedNode = currentNode;
                
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end ruleXBranchRef


    // $ANTLR start entryRuleACCESS_CONTEXT_TYPE_REFRENCE
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2418:1: entryRuleACCESS_CONTEXT_TYPE_REFRENCE returns [String current=null] : iv_ruleACCESS_CONTEXT_TYPE_REFRENCE= ruleACCESS_CONTEXT_TYPE_REFRENCE EOF ;
    public final String entryRuleACCESS_CONTEXT_TYPE_REFRENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleACCESS_CONTEXT_TYPE_REFRENCE = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2419:2: (iv_ruleACCESS_CONTEXT_TYPE_REFRENCE= ruleACCESS_CONTEXT_TYPE_REFRENCE EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2420:2: iv_ruleACCESS_CONTEXT_TYPE_REFRENCE= ruleACCESS_CONTEXT_TYPE_REFRENCE EOF
            {
             currentNode = createCompositeNode(grammarAccess.getACCESS_CONTEXT_TYPE_REFRENCERule(), currentNode); 
            pushFollow(FOLLOW_ruleACCESS_CONTEXT_TYPE_REFRENCE_in_entryRuleACCESS_CONTEXT_TYPE_REFRENCE4455);
            iv_ruleACCESS_CONTEXT_TYPE_REFRENCE=ruleACCESS_CONTEXT_TYPE_REFRENCE();
            _fsp--;

             current =iv_ruleACCESS_CONTEXT_TYPE_REFRENCE.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleACCESS_CONTEXT_TYPE_REFRENCE4466); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end entryRuleACCESS_CONTEXT_TYPE_REFRENCE


    // $ANTLR start ruleACCESS_CONTEXT_TYPE_REFRENCE
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2427:1: ruleACCESS_CONTEXT_TYPE_REFRENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleACCESS_CONTEXT_TYPE_REFRENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2432:6: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2433:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)input.LT(1);
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleACCESS_CONTEXT_TYPE_REFRENCE4505); 

            		current.merge(this_STRING_0);
                
             
                createLeafNode(grammarAccess.getACCESS_CONTEXT_TYPE_REFRENCEAccess().getSTRINGTerminalRuleCall(), null); 
                

            }

             resetLookahead(); 
            	    lastConsumedNode = currentNode;
                
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end ruleACCESS_CONTEXT_TYPE_REFRENCE


    // $ANTLR start entryRuleAccessContext
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2448:1: entryRuleAccessContext returns [EObject current=null] : iv_ruleAccessContext= ruleAccessContext EOF ;
    public final EObject entryRuleAccessContext() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAccessContext = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2449:2: (iv_ruleAccessContext= ruleAccessContext EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2450:2: iv_ruleAccessContext= ruleAccessContext EOF
            {
             currentNode = createCompositeNode(grammarAccess.getAccessContextRule(), currentNode); 
            pushFollow(FOLLOW_ruleAccessContext_in_entryRuleAccessContext4549);
            iv_ruleAccessContext=ruleAccessContext();
            _fsp--;

             current =iv_ruleAccessContext; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAccessContext4559); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end entryRuleAccessContext


    // $ANTLR start ruleAccessContext
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2457:1: ruleAccessContext returns [EObject current=null] : ( 'accessContext' ( (lv_name_1_0= ruleACCESS_CONTEXT_TYPE_REFRENCE ) ) ( 'extends' ( ( ruleACCESS_CONTEXT_TYPE_REFRENCE ) ) ( ',' ( ( ruleACCESS_CONTEXT_TYPE_REFRENCE ) ) )* )? '{' 'guid' ( (lv_guid_8_0= RULE_STRING ) ) ';' ( ( (lv_accessRules_10_0= ruleObjectRestriction ) ) | ( (lv_hierarchyRestrictions_11_0= ruleHierarchyRestriction ) ) )+ '}' ) ;
    public final EObject ruleAccessContext() throws RecognitionException {
        EObject current = null;

        Token lv_guid_8_0=null;
        AntlrDatatypeRuleToken lv_name_1_0 = null;

        EObject lv_accessRules_10_0 = null;

        EObject lv_hierarchyRestrictions_11_0 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2462:6: ( ( 'accessContext' ( (lv_name_1_0= ruleACCESS_CONTEXT_TYPE_REFRENCE ) ) ( 'extends' ( ( ruleACCESS_CONTEXT_TYPE_REFRENCE ) ) ( ',' ( ( ruleACCESS_CONTEXT_TYPE_REFRENCE ) ) )* )? '{' 'guid' ( (lv_guid_8_0= RULE_STRING ) ) ';' ( ( (lv_accessRules_10_0= ruleObjectRestriction ) ) | ( (lv_hierarchyRestrictions_11_0= ruleHierarchyRestriction ) ) )+ '}' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2463:1: ( 'accessContext' ( (lv_name_1_0= ruleACCESS_CONTEXT_TYPE_REFRENCE ) ) ( 'extends' ( ( ruleACCESS_CONTEXT_TYPE_REFRENCE ) ) ( ',' ( ( ruleACCESS_CONTEXT_TYPE_REFRENCE ) ) )* )? '{' 'guid' ( (lv_guid_8_0= RULE_STRING ) ) ';' ( ( (lv_accessRules_10_0= ruleObjectRestriction ) ) | ( (lv_hierarchyRestrictions_11_0= ruleHierarchyRestriction ) ) )+ '}' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2463:1: ( 'accessContext' ( (lv_name_1_0= ruleACCESS_CONTEXT_TYPE_REFRENCE ) ) ( 'extends' ( ( ruleACCESS_CONTEXT_TYPE_REFRENCE ) ) ( ',' ( ( ruleACCESS_CONTEXT_TYPE_REFRENCE ) ) )* )? '{' 'guid' ( (lv_guid_8_0= RULE_STRING ) ) ';' ( ( (lv_accessRules_10_0= ruleObjectRestriction ) ) | ( (lv_hierarchyRestrictions_11_0= ruleHierarchyRestriction ) ) )+ '}' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2463:3: 'accessContext' ( (lv_name_1_0= ruleACCESS_CONTEXT_TYPE_REFRENCE ) ) ( 'extends' ( ( ruleACCESS_CONTEXT_TYPE_REFRENCE ) ) ( ',' ( ( ruleACCESS_CONTEXT_TYPE_REFRENCE ) ) )* )? '{' 'guid' ( (lv_guid_8_0= RULE_STRING ) ) ';' ( ( (lv_accessRules_10_0= ruleObjectRestriction ) ) | ( (lv_hierarchyRestrictions_11_0= ruleHierarchyRestriction ) ) )+ '}'
            {
            match(input,67,FOLLOW_67_in_ruleAccessContext4594); 

                    createLeafNode(grammarAccess.getAccessContextAccess().getAccessContextKeyword_0(), null); 
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2467:1: ( (lv_name_1_0= ruleACCESS_CONTEXT_TYPE_REFRENCE ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2468:1: (lv_name_1_0= ruleACCESS_CONTEXT_TYPE_REFRENCE )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2468:1: (lv_name_1_0= ruleACCESS_CONTEXT_TYPE_REFRENCE )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2469:3: lv_name_1_0= ruleACCESS_CONTEXT_TYPE_REFRENCE
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getAccessContextAccess().getNameACCESS_CONTEXT_TYPE_REFRENCEParserRuleCall_1_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleACCESS_CONTEXT_TYPE_REFRENCE_in_ruleAccessContext4615);
            lv_name_1_0=ruleACCESS_CONTEXT_TYPE_REFRENCE();
            _fsp--;


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getAccessContextRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode.getParent(), current);
            	        }
            	        try {
            	       		set(
            	       			current, 
            	       			"name",
            	        		lv_name_1_0, 
            	        		"ACCESS_CONTEXT_TYPE_REFRENCE", 
            	        		currentNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	        currentNode = currentNode.getParent();
            	    

            }


            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2491:2: ( 'extends' ( ( ruleACCESS_CONTEXT_TYPE_REFRENCE ) ) ( ',' ( ( ruleACCESS_CONTEXT_TYPE_REFRENCE ) ) )* )?
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==16) ) {
                alt32=1;
            }
            switch (alt32) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2491:4: 'extends' ( ( ruleACCESS_CONTEXT_TYPE_REFRENCE ) ) ( ',' ( ( ruleACCESS_CONTEXT_TYPE_REFRENCE ) ) )*
                    {
                    match(input,16,FOLLOW_16_in_ruleAccessContext4626); 

                            createLeafNode(grammarAccess.getAccessContextAccess().getExtendsKeyword_2_0(), null); 
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2495:1: ( ( ruleACCESS_CONTEXT_TYPE_REFRENCE ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2496:1: ( ruleACCESS_CONTEXT_TYPE_REFRENCE )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2496:1: ( ruleACCESS_CONTEXT_TYPE_REFRENCE )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2497:3: ruleACCESS_CONTEXT_TYPE_REFRENCE
                    {

                    			if (current==null) {
                    	            current = factory.create(grammarAccess.getAccessContextRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                            
                     
                    	        currentNode=createCompositeNode(grammarAccess.getAccessContextAccess().getSuperAccessContextsAccessContextCrossReference_2_1_0(), currentNode); 
                    	    
                    pushFollow(FOLLOW_ruleACCESS_CONTEXT_TYPE_REFRENCE_in_ruleAccessContext4649);
                    ruleACCESS_CONTEXT_TYPE_REFRENCE();
                    _fsp--;

                     
                    	        currentNode = currentNode.getParent();
                    	    

                    }


                    }

                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2511:2: ( ',' ( ( ruleACCESS_CONTEXT_TYPE_REFRENCE ) ) )*
                    loop31:
                    do {
                        int alt31=2;
                        int LA31_0 = input.LA(1);

                        if ( (LA31_0==17) ) {
                            alt31=1;
                        }


                        switch (alt31) {
                    	case 1 :
                    	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2511:4: ',' ( ( ruleACCESS_CONTEXT_TYPE_REFRENCE ) )
                    	    {
                    	    match(input,17,FOLLOW_17_in_ruleAccessContext4660); 

                    	            createLeafNode(grammarAccess.getAccessContextAccess().getCommaKeyword_2_2_0(), null); 
                    	        
                    	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2515:1: ( ( ruleACCESS_CONTEXT_TYPE_REFRENCE ) )
                    	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2516:1: ( ruleACCESS_CONTEXT_TYPE_REFRENCE )
                    	    {
                    	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2516:1: ( ruleACCESS_CONTEXT_TYPE_REFRENCE )
                    	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2517:3: ruleACCESS_CONTEXT_TYPE_REFRENCE
                    	    {

                    	    			if (current==null) {
                    	    	            current = factory.create(grammarAccess.getAccessContextRule().getType().getClassifier());
                    	    	            associateNodeWithAstElement(currentNode, current);
                    	    	        }
                    	            
                    	     
                    	    	        currentNode=createCompositeNode(grammarAccess.getAccessContextAccess().getSuperAccessContextsAccessContextCrossReference_2_2_1_0(), currentNode); 
                    	    	    
                    	    pushFollow(FOLLOW_ruleACCESS_CONTEXT_TYPE_REFRENCE_in_ruleAccessContext4683);
                    	    ruleACCESS_CONTEXT_TYPE_REFRENCE();
                    	    _fsp--;

                    	     
                    	    	        currentNode = currentNode.getParent();
                    	    	    

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop31;
                        }
                    } while (true);


                    }
                    break;

            }

            match(input,18,FOLLOW_18_in_ruleAccessContext4697); 

                    createLeafNode(grammarAccess.getAccessContextAccess().getLeftCurlyBracketKeyword_3(), null); 
                
            match(input,19,FOLLOW_19_in_ruleAccessContext4707); 

                    createLeafNode(grammarAccess.getAccessContextAccess().getGuidKeyword_4(), null); 
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2539:1: ( (lv_guid_8_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2540:1: (lv_guid_8_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2540:1: (lv_guid_8_0= RULE_STRING )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2541:3: lv_guid_8_0= RULE_STRING
            {
            lv_guid_8_0=(Token)input.LT(1);
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAccessContext4724); 

            			createLeafNode(grammarAccess.getAccessContextAccess().getGuidSTRINGTerminalRuleCall_5_0(), "guid"); 
            		

            	        if (current==null) {
            	            current = factory.create(grammarAccess.getAccessContextRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
            	        try {
            	       		set(
            	       			current, 
            	       			"guid",
            	        		lv_guid_8_0, 
            	        		"STRING", 
            	        		lastConsumedNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	    

            }


            }

            match(input,65,FOLLOW_65_in_ruleAccessContext4739); 

                    createLeafNode(grammarAccess.getAccessContextAccess().getSemicolonKeyword_6(), null); 
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2567:1: ( ( (lv_accessRules_10_0= ruleObjectRestriction ) ) | ( (lv_hierarchyRestrictions_11_0= ruleHierarchyRestriction ) ) )+
            int cnt33=0;
            loop33:
            do {
                int alt33=3;
                int LA33_0 = input.LA(1);

                if ( ((LA33_0>=75 && LA33_0<=76)) ) {
                    alt33=1;
                }
                else if ( (LA33_0==68) ) {
                    alt33=2;
                }


                switch (alt33) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2567:2: ( (lv_accessRules_10_0= ruleObjectRestriction ) )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2567:2: ( (lv_accessRules_10_0= ruleObjectRestriction ) )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2568:1: (lv_accessRules_10_0= ruleObjectRestriction )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2568:1: (lv_accessRules_10_0= ruleObjectRestriction )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2569:3: lv_accessRules_10_0= ruleObjectRestriction
            	    {
            	     
            	    	        currentNode=createCompositeNode(grammarAccess.getAccessContextAccess().getAccessRulesObjectRestrictionParserRuleCall_7_0_0(), currentNode); 
            	    	    
            	    pushFollow(FOLLOW_ruleObjectRestriction_in_ruleAccessContext4761);
            	    lv_accessRules_10_0=ruleObjectRestriction();
            	    _fsp--;


            	    	        if (current==null) {
            	    	            current = factory.create(grammarAccess.getAccessContextRule().getType().getClassifier());
            	    	            associateNodeWithAstElement(currentNode.getParent(), current);
            	    	        }
            	    	        try {
            	    	       		add(
            	    	       			current, 
            	    	       			"accessRules",
            	    	        		lv_accessRules_10_0, 
            	    	        		"ObjectRestriction", 
            	    	        		currentNode);
            	    	        } catch (ValueConverterException vce) {
            	    				handleValueConverterException(vce);
            	    	        }
            	    	        currentNode = currentNode.getParent();
            	    	    

            	    }


            	    }


            	    }
            	    break;
            	case 2 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2592:6: ( (lv_hierarchyRestrictions_11_0= ruleHierarchyRestriction ) )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2592:6: ( (lv_hierarchyRestrictions_11_0= ruleHierarchyRestriction ) )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2593:1: (lv_hierarchyRestrictions_11_0= ruleHierarchyRestriction )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2593:1: (lv_hierarchyRestrictions_11_0= ruleHierarchyRestriction )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2594:3: lv_hierarchyRestrictions_11_0= ruleHierarchyRestriction
            	    {
            	     
            	    	        currentNode=createCompositeNode(grammarAccess.getAccessContextAccess().getHierarchyRestrictionsHierarchyRestrictionParserRuleCall_7_1_0(), currentNode); 
            	    	    
            	    pushFollow(FOLLOW_ruleHierarchyRestriction_in_ruleAccessContext4788);
            	    lv_hierarchyRestrictions_11_0=ruleHierarchyRestriction();
            	    _fsp--;


            	    	        if (current==null) {
            	    	            current = factory.create(grammarAccess.getAccessContextRule().getType().getClassifier());
            	    	            associateNodeWithAstElement(currentNode.getParent(), current);
            	    	        }
            	    	        try {
            	    	       		add(
            	    	       			current, 
            	    	       			"hierarchyRestrictions",
            	    	        		lv_hierarchyRestrictions_11_0, 
            	    	        		"HierarchyRestriction", 
            	    	        		currentNode);
            	    	        } catch (ValueConverterException vce) {
            	    				handleValueConverterException(vce);
            	    	        }
            	    	        currentNode = currentNode.getParent();
            	    	    

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

            match(input,20,FOLLOW_20_in_ruleAccessContext4800); 

                    createLeafNode(grammarAccess.getAccessContextAccess().getRightCurlyBracketKeyword_8(), null); 
                

            }


            }

             resetLookahead(); 
                	lastConsumedNode = currentNode;
                
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end ruleAccessContext


    // $ANTLR start entryRuleHierarchyRestriction
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2628:1: entryRuleHierarchyRestriction returns [EObject current=null] : iv_ruleHierarchyRestriction= ruleHierarchyRestriction EOF ;
    public final EObject entryRuleHierarchyRestriction() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleHierarchyRestriction = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2629:2: (iv_ruleHierarchyRestriction= ruleHierarchyRestriction EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2630:2: iv_ruleHierarchyRestriction= ruleHierarchyRestriction EOF
            {
             currentNode = createCompositeNode(grammarAccess.getHierarchyRestrictionRule(), currentNode); 
            pushFollow(FOLLOW_ruleHierarchyRestriction_in_entryRuleHierarchyRestriction4836);
            iv_ruleHierarchyRestriction=ruleHierarchyRestriction();
            _fsp--;

             current =iv_ruleHierarchyRestriction; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleHierarchyRestriction4846); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end entryRuleHierarchyRestriction


    // $ANTLR start ruleHierarchyRestriction
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2637:1: ruleHierarchyRestriction returns [EObject current=null] : ( 'childrenOf' ( ( ruleARTIFACT_INSTANCE_REFERENCE ) ) '{' ( (lv_accessRules_3_0= ruleObjectRestriction ) )+ '}' ) ;
    public final EObject ruleHierarchyRestriction() throws RecognitionException {
        EObject current = null;

        EObject lv_accessRules_3_0 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2642:6: ( ( 'childrenOf' ( ( ruleARTIFACT_INSTANCE_REFERENCE ) ) '{' ( (lv_accessRules_3_0= ruleObjectRestriction ) )+ '}' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2643:1: ( 'childrenOf' ( ( ruleARTIFACT_INSTANCE_REFERENCE ) ) '{' ( (lv_accessRules_3_0= ruleObjectRestriction ) )+ '}' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2643:1: ( 'childrenOf' ( ( ruleARTIFACT_INSTANCE_REFERENCE ) ) '{' ( (lv_accessRules_3_0= ruleObjectRestriction ) )+ '}' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2643:3: 'childrenOf' ( ( ruleARTIFACT_INSTANCE_REFERENCE ) ) '{' ( (lv_accessRules_3_0= ruleObjectRestriction ) )+ '}'
            {
            match(input,68,FOLLOW_68_in_ruleHierarchyRestriction4881); 

                    createLeafNode(grammarAccess.getHierarchyRestrictionAccess().getChildrenOfKeyword_0(), null); 
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2647:1: ( ( ruleARTIFACT_INSTANCE_REFERENCE ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2648:1: ( ruleARTIFACT_INSTANCE_REFERENCE )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2648:1: ( ruleARTIFACT_INSTANCE_REFERENCE )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2649:3: ruleARTIFACT_INSTANCE_REFERENCE
            {

            			if (current==null) {
            	            current = factory.create(grammarAccess.getHierarchyRestrictionRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
                    
             
            	        currentNode=createCompositeNode(grammarAccess.getHierarchyRestrictionAccess().getArtifactXArtifactRefCrossReference_1_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleARTIFACT_INSTANCE_REFERENCE_in_ruleHierarchyRestriction4904);
            ruleARTIFACT_INSTANCE_REFERENCE();
            _fsp--;

             
            	        currentNode = currentNode.getParent();
            	    

            }


            }

            match(input,18,FOLLOW_18_in_ruleHierarchyRestriction4914); 

                    createLeafNode(grammarAccess.getHierarchyRestrictionAccess().getLeftCurlyBracketKeyword_2(), null); 
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2667:1: ( (lv_accessRules_3_0= ruleObjectRestriction ) )+
            int cnt34=0;
            loop34:
            do {
                int alt34=2;
                int LA34_0 = input.LA(1);

                if ( ((LA34_0>=75 && LA34_0<=76)) ) {
                    alt34=1;
                }


                switch (alt34) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2668:1: (lv_accessRules_3_0= ruleObjectRestriction )
            	    {
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2668:1: (lv_accessRules_3_0= ruleObjectRestriction )
            	    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2669:3: lv_accessRules_3_0= ruleObjectRestriction
            	    {
            	     
            	    	        currentNode=createCompositeNode(grammarAccess.getHierarchyRestrictionAccess().getAccessRulesObjectRestrictionParserRuleCall_3_0(), currentNode); 
            	    	    
            	    pushFollow(FOLLOW_ruleObjectRestriction_in_ruleHierarchyRestriction4935);
            	    lv_accessRules_3_0=ruleObjectRestriction();
            	    _fsp--;


            	    	        if (current==null) {
            	    	            current = factory.create(grammarAccess.getHierarchyRestrictionRule().getType().getClassifier());
            	    	            associateNodeWithAstElement(currentNode.getParent(), current);
            	    	        }
            	    	        try {
            	    	       		add(
            	    	       			current, 
            	    	       			"accessRules",
            	    	        		lv_accessRules_3_0, 
            	    	        		"ObjectRestriction", 
            	    	        		currentNode);
            	    	        } catch (ValueConverterException vce) {
            	    				handleValueConverterException(vce);
            	    	        }
            	    	        currentNode = currentNode.getParent();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt34 >= 1 ) break loop34;
                        EarlyExitException eee =
                            new EarlyExitException(34, input);
                        throw eee;
                }
                cnt34++;
            } while (true);

            match(input,20,FOLLOW_20_in_ruleHierarchyRestriction4946); 

                    createLeafNode(grammarAccess.getHierarchyRestrictionAccess().getRightCurlyBracketKeyword_4(), null); 
                

            }


            }

             resetLookahead(); 
                	lastConsumedNode = currentNode;
                
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end ruleHierarchyRestriction


    // $ANTLR start entryRuleObjectRestriction
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2703:1: entryRuleObjectRestriction returns [EObject current=null] : iv_ruleObjectRestriction= ruleObjectRestriction EOF ;
    public final EObject entryRuleObjectRestriction() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleObjectRestriction = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2704:2: (iv_ruleObjectRestriction= ruleObjectRestriction EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2705:2: iv_ruleObjectRestriction= ruleObjectRestriction EOF
            {
             currentNode = createCompositeNode(grammarAccess.getObjectRestrictionRule(), currentNode); 
            pushFollow(FOLLOW_ruleObjectRestriction_in_entryRuleObjectRestriction4982);
            iv_ruleObjectRestriction=ruleObjectRestriction();
            _fsp--;

             current =iv_ruleObjectRestriction; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleObjectRestriction4992); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end entryRuleObjectRestriction


    // $ANTLR start ruleObjectRestriction
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2712:1: ruleObjectRestriction returns [EObject current=null] : (this_ArtifactInstanceRestriction_0= ruleArtifactInstanceRestriction | this_ArtifactTypeRestriction_1= ruleArtifactTypeRestriction | this_RelationTypeRestriction_2= ruleRelationTypeRestriction | this_AttributeTypeRestriction_3= ruleAttributeTypeRestriction ) ;
    public final EObject ruleObjectRestriction() throws RecognitionException {
        EObject current = null;

        EObject this_ArtifactInstanceRestriction_0 = null;

        EObject this_ArtifactTypeRestriction_1 = null;

        EObject this_RelationTypeRestriction_2 = null;

        EObject this_AttributeTypeRestriction_3 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2717:6: ( (this_ArtifactInstanceRestriction_0= ruleArtifactInstanceRestriction | this_ArtifactTypeRestriction_1= ruleArtifactTypeRestriction | this_RelationTypeRestriction_2= ruleRelationTypeRestriction | this_AttributeTypeRestriction_3= ruleAttributeTypeRestriction ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2718:1: (this_ArtifactInstanceRestriction_0= ruleArtifactInstanceRestriction | this_ArtifactTypeRestriction_1= ruleArtifactTypeRestriction | this_RelationTypeRestriction_2= ruleRelationTypeRestriction | this_AttributeTypeRestriction_3= ruleAttributeTypeRestriction )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2718:1: (this_ArtifactInstanceRestriction_0= ruleArtifactInstanceRestriction | this_ArtifactTypeRestriction_1= ruleArtifactTypeRestriction | this_RelationTypeRestriction_2= ruleRelationTypeRestriction | this_AttributeTypeRestriction_3= ruleAttributeTypeRestriction )
            int alt35=4;
            int LA35_0 = input.LA(1);

            if ( (LA35_0==75) ) {
                int LA35_1 = input.LA(2);

                if ( (LA35_1==69) ) {
                    switch ( input.LA(3) ) {
                    case 63:
                        {
                        alt35=1;
                        }
                        break;
                    case 23:
                        {
                        alt35=4;
                        }
                        break;
                    case 15:
                        {
                        alt35=2;
                        }
                        break;
                    case 53:
                        {
                        alt35=3;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("2718:1: (this_ArtifactInstanceRestriction_0= ruleArtifactInstanceRestriction | this_ArtifactTypeRestriction_1= ruleArtifactTypeRestriction | this_RelationTypeRestriction_2= ruleRelationTypeRestriction | this_AttributeTypeRestriction_3= ruleAttributeTypeRestriction )", 35, 3, input);

                        throw nvae;
                    }

                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("2718:1: (this_ArtifactInstanceRestriction_0= ruleArtifactInstanceRestriction | this_ArtifactTypeRestriction_1= ruleArtifactTypeRestriction | this_RelationTypeRestriction_2= ruleRelationTypeRestriction | this_AttributeTypeRestriction_3= ruleAttributeTypeRestriction )", 35, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA35_0==76) ) {
                int LA35_2 = input.LA(2);

                if ( (LA35_2==69) ) {
                    switch ( input.LA(3) ) {
                    case 63:
                        {
                        alt35=1;
                        }
                        break;
                    case 23:
                        {
                        alt35=4;
                        }
                        break;
                    case 15:
                        {
                        alt35=2;
                        }
                        break;
                    case 53:
                        {
                        alt35=3;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("2718:1: (this_ArtifactInstanceRestriction_0= ruleArtifactInstanceRestriction | this_ArtifactTypeRestriction_1= ruleArtifactTypeRestriction | this_RelationTypeRestriction_2= ruleRelationTypeRestriction | this_AttributeTypeRestriction_3= ruleAttributeTypeRestriction )", 35, 3, input);

                        throw nvae;
                    }

                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("2718:1: (this_ArtifactInstanceRestriction_0= ruleArtifactInstanceRestriction | this_ArtifactTypeRestriction_1= ruleArtifactTypeRestriction | this_RelationTypeRestriction_2= ruleRelationTypeRestriction | this_AttributeTypeRestriction_3= ruleAttributeTypeRestriction )", 35, 2, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("2718:1: (this_ArtifactInstanceRestriction_0= ruleArtifactInstanceRestriction | this_ArtifactTypeRestriction_1= ruleArtifactTypeRestriction | this_RelationTypeRestriction_2= ruleRelationTypeRestriction | this_AttributeTypeRestriction_3= ruleAttributeTypeRestriction )", 35, 0, input);

                throw nvae;
            }
            switch (alt35) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2719:5: this_ArtifactInstanceRestriction_0= ruleArtifactInstanceRestriction
                    {
                     
                            currentNode=createCompositeNode(grammarAccess.getObjectRestrictionAccess().getArtifactInstanceRestrictionParserRuleCall_0(), currentNode); 
                        
                    pushFollow(FOLLOW_ruleArtifactInstanceRestriction_in_ruleObjectRestriction5039);
                    this_ArtifactInstanceRestriction_0=ruleArtifactInstanceRestriction();
                    _fsp--;

                     
                            current = this_ArtifactInstanceRestriction_0; 
                            currentNode = currentNode.getParent();
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2729:5: this_ArtifactTypeRestriction_1= ruleArtifactTypeRestriction
                    {
                     
                            currentNode=createCompositeNode(grammarAccess.getObjectRestrictionAccess().getArtifactTypeRestrictionParserRuleCall_1(), currentNode); 
                        
                    pushFollow(FOLLOW_ruleArtifactTypeRestriction_in_ruleObjectRestriction5066);
                    this_ArtifactTypeRestriction_1=ruleArtifactTypeRestriction();
                    _fsp--;

                     
                            current = this_ArtifactTypeRestriction_1; 
                            currentNode = currentNode.getParent();
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2739:5: this_RelationTypeRestriction_2= ruleRelationTypeRestriction
                    {
                     
                            currentNode=createCompositeNode(grammarAccess.getObjectRestrictionAccess().getRelationTypeRestrictionParserRuleCall_2(), currentNode); 
                        
                    pushFollow(FOLLOW_ruleRelationTypeRestriction_in_ruleObjectRestriction5093);
                    this_RelationTypeRestriction_2=ruleRelationTypeRestriction();
                    _fsp--;

                     
                            current = this_RelationTypeRestriction_2; 
                            currentNode = currentNode.getParent();
                        

                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2749:5: this_AttributeTypeRestriction_3= ruleAttributeTypeRestriction
                    {
                     
                            currentNode=createCompositeNode(grammarAccess.getObjectRestrictionAccess().getAttributeTypeRestrictionParserRuleCall_3(), currentNode); 
                        
                    pushFollow(FOLLOW_ruleAttributeTypeRestriction_in_ruleObjectRestriction5120);
                    this_AttributeTypeRestriction_3=ruleAttributeTypeRestriction();
                    _fsp--;

                     
                            current = this_AttributeTypeRestriction_3; 
                            currentNode = currentNode.getParent();
                        

                    }
                    break;

            }


            }

             resetLookahead(); 
                	lastConsumedNode = currentNode;
                
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end ruleObjectRestriction


    // $ANTLR start entryRuleArtifactInstanceRestriction
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2765:1: entryRuleArtifactInstanceRestriction returns [EObject current=null] : iv_ruleArtifactInstanceRestriction= ruleArtifactInstanceRestriction EOF ;
    public final EObject entryRuleArtifactInstanceRestriction() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleArtifactInstanceRestriction = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2766:2: (iv_ruleArtifactInstanceRestriction= ruleArtifactInstanceRestriction EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2767:2: iv_ruleArtifactInstanceRestriction= ruleArtifactInstanceRestriction EOF
            {
             currentNode = createCompositeNode(grammarAccess.getArtifactInstanceRestrictionRule(), currentNode); 
            pushFollow(FOLLOW_ruleArtifactInstanceRestriction_in_entryRuleArtifactInstanceRestriction5155);
            iv_ruleArtifactInstanceRestriction=ruleArtifactInstanceRestriction();
            _fsp--;

             current =iv_ruleArtifactInstanceRestriction; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleArtifactInstanceRestriction5165); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end entryRuleArtifactInstanceRestriction


    // $ANTLR start ruleArtifactInstanceRestriction
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2774:1: ruleArtifactInstanceRestriction returns [EObject current=null] : ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) 'edit' 'artifact' ( ( ruleARTIFACT_INSTANCE_REFERENCE ) ) ';' ) ;
    public final EObject ruleArtifactInstanceRestriction() throws RecognitionException {
        EObject current = null;

        Enumerator lv_permission_0_0 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2779:6: ( ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) 'edit' 'artifact' ( ( ruleARTIFACT_INSTANCE_REFERENCE ) ) ';' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2780:1: ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) 'edit' 'artifact' ( ( ruleARTIFACT_INSTANCE_REFERENCE ) ) ';' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2780:1: ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) 'edit' 'artifact' ( ( ruleARTIFACT_INSTANCE_REFERENCE ) ) ';' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2780:2: ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) 'edit' 'artifact' ( ( ruleARTIFACT_INSTANCE_REFERENCE ) ) ';'
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2780:2: ( (lv_permission_0_0= ruleAccessPermissionEnum ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2781:1: (lv_permission_0_0= ruleAccessPermissionEnum )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2781:1: (lv_permission_0_0= ruleAccessPermissionEnum )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2782:3: lv_permission_0_0= ruleAccessPermissionEnum
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getArtifactInstanceRestrictionAccess().getPermissionAccessPermissionEnumEnumRuleCall_0_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleAccessPermissionEnum_in_ruleArtifactInstanceRestriction5211);
            lv_permission_0_0=ruleAccessPermissionEnum();
            _fsp--;


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getArtifactInstanceRestrictionRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode.getParent(), current);
            	        }
            	        try {
            	       		set(
            	       			current, 
            	       			"permission",
            	        		lv_permission_0_0, 
            	        		"AccessPermissionEnum", 
            	        		currentNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	        currentNode = currentNode.getParent();
            	    

            }


            }

            match(input,69,FOLLOW_69_in_ruleArtifactInstanceRestriction5221); 

                    createLeafNode(grammarAccess.getArtifactInstanceRestrictionAccess().getEditKeyword_1(), null); 
                
            match(input,63,FOLLOW_63_in_ruleArtifactInstanceRestriction5231); 

                    createLeafNode(grammarAccess.getArtifactInstanceRestrictionAccess().getArtifactKeyword_2(), null); 
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2812:1: ( ( ruleARTIFACT_INSTANCE_REFERENCE ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2813:1: ( ruleARTIFACT_INSTANCE_REFERENCE )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2813:1: ( ruleARTIFACT_INSTANCE_REFERENCE )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2814:3: ruleARTIFACT_INSTANCE_REFERENCE
            {

            			if (current==null) {
            	            current = factory.create(grammarAccess.getArtifactInstanceRestrictionRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
                    
             
            	        currentNode=createCompositeNode(grammarAccess.getArtifactInstanceRestrictionAccess().getArtifactRefXArtifactRefCrossReference_3_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleARTIFACT_INSTANCE_REFERENCE_in_ruleArtifactInstanceRestriction5254);
            ruleARTIFACT_INSTANCE_REFERENCE();
            _fsp--;

             
            	        currentNode = currentNode.getParent();
            	    

            }


            }

            match(input,65,FOLLOW_65_in_ruleArtifactInstanceRestriction5264); 

                    createLeafNode(grammarAccess.getArtifactInstanceRestrictionAccess().getSemicolonKeyword_4(), null); 
                

            }


            }

             resetLookahead(); 
                	lastConsumedNode = currentNode;
                
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end ruleArtifactInstanceRestriction


    // $ANTLR start entryRuleArtifactTypeRestriction
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2840:1: entryRuleArtifactTypeRestriction returns [EObject current=null] : iv_ruleArtifactTypeRestriction= ruleArtifactTypeRestriction EOF ;
    public final EObject entryRuleArtifactTypeRestriction() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleArtifactTypeRestriction = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2841:2: (iv_ruleArtifactTypeRestriction= ruleArtifactTypeRestriction EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2842:2: iv_ruleArtifactTypeRestriction= ruleArtifactTypeRestriction EOF
            {
             currentNode = createCompositeNode(grammarAccess.getArtifactTypeRestrictionRule(), currentNode); 
            pushFollow(FOLLOW_ruleArtifactTypeRestriction_in_entryRuleArtifactTypeRestriction5300);
            iv_ruleArtifactTypeRestriction=ruleArtifactTypeRestriction();
            _fsp--;

             current =iv_ruleArtifactTypeRestriction; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleArtifactTypeRestriction5310); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end entryRuleArtifactTypeRestriction


    // $ANTLR start ruleArtifactTypeRestriction
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2849:1: ruleArtifactTypeRestriction returns [EObject current=null] : ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) 'edit' 'artifactType' ( ( ruleARTIFACT_TYPE_REFERENCE ) ) ';' ) ;
    public final EObject ruleArtifactTypeRestriction() throws RecognitionException {
        EObject current = null;

        Enumerator lv_permission_0_0 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2854:6: ( ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) 'edit' 'artifactType' ( ( ruleARTIFACT_TYPE_REFERENCE ) ) ';' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2855:1: ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) 'edit' 'artifactType' ( ( ruleARTIFACT_TYPE_REFERENCE ) ) ';' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2855:1: ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) 'edit' 'artifactType' ( ( ruleARTIFACT_TYPE_REFERENCE ) ) ';' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2855:2: ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) 'edit' 'artifactType' ( ( ruleARTIFACT_TYPE_REFERENCE ) ) ';'
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2855:2: ( (lv_permission_0_0= ruleAccessPermissionEnum ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2856:1: (lv_permission_0_0= ruleAccessPermissionEnum )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2856:1: (lv_permission_0_0= ruleAccessPermissionEnum )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2857:3: lv_permission_0_0= ruleAccessPermissionEnum
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getArtifactTypeRestrictionAccess().getPermissionAccessPermissionEnumEnumRuleCall_0_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleAccessPermissionEnum_in_ruleArtifactTypeRestriction5356);
            lv_permission_0_0=ruleAccessPermissionEnum();
            _fsp--;


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getArtifactTypeRestrictionRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode.getParent(), current);
            	        }
            	        try {
            	       		set(
            	       			current, 
            	       			"permission",
            	        		lv_permission_0_0, 
            	        		"AccessPermissionEnum", 
            	        		currentNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	        currentNode = currentNode.getParent();
            	    

            }


            }

            match(input,69,FOLLOW_69_in_ruleArtifactTypeRestriction5366); 

                    createLeafNode(grammarAccess.getArtifactTypeRestrictionAccess().getEditKeyword_1(), null); 
                
            match(input,15,FOLLOW_15_in_ruleArtifactTypeRestriction5376); 

                    createLeafNode(grammarAccess.getArtifactTypeRestrictionAccess().getArtifactTypeKeyword_2(), null); 
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2887:1: ( ( ruleARTIFACT_TYPE_REFERENCE ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2888:1: ( ruleARTIFACT_TYPE_REFERENCE )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2888:1: ( ruleARTIFACT_TYPE_REFERENCE )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2889:3: ruleARTIFACT_TYPE_REFERENCE
            {

            			if (current==null) {
            	            current = factory.create(grammarAccess.getArtifactTypeRestrictionRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
                    
             
            	        currentNode=createCompositeNode(grammarAccess.getArtifactTypeRestrictionAccess().getArtifactTypeRefXArtifactTypeCrossReference_3_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleARTIFACT_TYPE_REFERENCE_in_ruleArtifactTypeRestriction5399);
            ruleARTIFACT_TYPE_REFERENCE();
            _fsp--;

             
            	        currentNode = currentNode.getParent();
            	    

            }


            }

            match(input,65,FOLLOW_65_in_ruleArtifactTypeRestriction5409); 

                    createLeafNode(grammarAccess.getArtifactTypeRestrictionAccess().getSemicolonKeyword_4(), null); 
                

            }


            }

             resetLookahead(); 
                	lastConsumedNode = currentNode;
                
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end ruleArtifactTypeRestriction


    // $ANTLR start entryRuleAttributeTypeRestriction
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2915:1: entryRuleAttributeTypeRestriction returns [EObject current=null] : iv_ruleAttributeTypeRestriction= ruleAttributeTypeRestriction EOF ;
    public final EObject entryRuleAttributeTypeRestriction() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttributeTypeRestriction = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2916:2: (iv_ruleAttributeTypeRestriction= ruleAttributeTypeRestriction EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2917:2: iv_ruleAttributeTypeRestriction= ruleAttributeTypeRestriction EOF
            {
             currentNode = createCompositeNode(grammarAccess.getAttributeTypeRestrictionRule(), currentNode); 
            pushFollow(FOLLOW_ruleAttributeTypeRestriction_in_entryRuleAttributeTypeRestriction5445);
            iv_ruleAttributeTypeRestriction=ruleAttributeTypeRestriction();
            _fsp--;

             current =iv_ruleAttributeTypeRestriction; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttributeTypeRestriction5455); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end entryRuleAttributeTypeRestriction


    // $ANTLR start ruleAttributeTypeRestriction
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2924:1: ruleAttributeTypeRestriction returns [EObject current=null] : ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) 'edit' 'attributeType' ( ( ruleATTRIBUTE_TYPE_REFERENCE ) ) ( 'of' 'artifactType' ( ( ruleARTIFACT_TYPE_REFERENCE ) ) )? ';' ) ;
    public final EObject ruleAttributeTypeRestriction() throws RecognitionException {
        EObject current = null;

        Enumerator lv_permission_0_0 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2929:6: ( ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) 'edit' 'attributeType' ( ( ruleATTRIBUTE_TYPE_REFERENCE ) ) ( 'of' 'artifactType' ( ( ruleARTIFACT_TYPE_REFERENCE ) ) )? ';' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2930:1: ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) 'edit' 'attributeType' ( ( ruleATTRIBUTE_TYPE_REFERENCE ) ) ( 'of' 'artifactType' ( ( ruleARTIFACT_TYPE_REFERENCE ) ) )? ';' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2930:1: ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) 'edit' 'attributeType' ( ( ruleATTRIBUTE_TYPE_REFERENCE ) ) ( 'of' 'artifactType' ( ( ruleARTIFACT_TYPE_REFERENCE ) ) )? ';' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2930:2: ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) 'edit' 'attributeType' ( ( ruleATTRIBUTE_TYPE_REFERENCE ) ) ( 'of' 'artifactType' ( ( ruleARTIFACT_TYPE_REFERENCE ) ) )? ';'
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2930:2: ( (lv_permission_0_0= ruleAccessPermissionEnum ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2931:1: (lv_permission_0_0= ruleAccessPermissionEnum )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2931:1: (lv_permission_0_0= ruleAccessPermissionEnum )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2932:3: lv_permission_0_0= ruleAccessPermissionEnum
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getAttributeTypeRestrictionAccess().getPermissionAccessPermissionEnumEnumRuleCall_0_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleAccessPermissionEnum_in_ruleAttributeTypeRestriction5501);
            lv_permission_0_0=ruleAccessPermissionEnum();
            _fsp--;


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getAttributeTypeRestrictionRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode.getParent(), current);
            	        }
            	        try {
            	       		set(
            	       			current, 
            	       			"permission",
            	        		lv_permission_0_0, 
            	        		"AccessPermissionEnum", 
            	        		currentNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	        currentNode = currentNode.getParent();
            	    

            }


            }

            match(input,69,FOLLOW_69_in_ruleAttributeTypeRestriction5511); 

                    createLeafNode(grammarAccess.getAttributeTypeRestrictionAccess().getEditKeyword_1(), null); 
                
            match(input,23,FOLLOW_23_in_ruleAttributeTypeRestriction5521); 

                    createLeafNode(grammarAccess.getAttributeTypeRestrictionAccess().getAttributeTypeKeyword_2(), null); 
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2962:1: ( ( ruleATTRIBUTE_TYPE_REFERENCE ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2963:1: ( ruleATTRIBUTE_TYPE_REFERENCE )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2963:1: ( ruleATTRIBUTE_TYPE_REFERENCE )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2964:3: ruleATTRIBUTE_TYPE_REFERENCE
            {

            			if (current==null) {
            	            current = factory.create(grammarAccess.getAttributeTypeRestrictionRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
                    
             
            	        currentNode=createCompositeNode(grammarAccess.getAttributeTypeRestrictionAccess().getAttributeTypeRefXAttributeTypeCrossReference_3_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleATTRIBUTE_TYPE_REFERENCE_in_ruleAttributeTypeRestriction5544);
            ruleATTRIBUTE_TYPE_REFERENCE();
            _fsp--;

             
            	        currentNode = currentNode.getParent();
            	    

            }


            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2978:2: ( 'of' 'artifactType' ( ( ruleARTIFACT_TYPE_REFERENCE ) ) )?
            int alt36=2;
            int LA36_0 = input.LA(1);

            if ( (LA36_0==70) ) {
                alt36=1;
            }
            switch (alt36) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2978:4: 'of' 'artifactType' ( ( ruleARTIFACT_TYPE_REFERENCE ) )
                    {
                    match(input,70,FOLLOW_70_in_ruleAttributeTypeRestriction5555); 

                            createLeafNode(grammarAccess.getAttributeTypeRestrictionAccess().getOfKeyword_4_0(), null); 
                        
                    match(input,15,FOLLOW_15_in_ruleAttributeTypeRestriction5565); 

                            createLeafNode(grammarAccess.getAttributeTypeRestrictionAccess().getArtifactTypeKeyword_4_1(), null); 
                        
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2986:1: ( ( ruleARTIFACT_TYPE_REFERENCE ) )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2987:1: ( ruleARTIFACT_TYPE_REFERENCE )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2987:1: ( ruleARTIFACT_TYPE_REFERENCE )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:2988:3: ruleARTIFACT_TYPE_REFERENCE
                    {

                    			if (current==null) {
                    	            current = factory.create(grammarAccess.getAttributeTypeRestrictionRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                            
                     
                    	        currentNode=createCompositeNode(grammarAccess.getAttributeTypeRestrictionAccess().getArtifactTypeRefXArtifactTypeCrossReference_4_2_0(), currentNode); 
                    	    
                    pushFollow(FOLLOW_ruleARTIFACT_TYPE_REFERENCE_in_ruleAttributeTypeRestriction5588);
                    ruleARTIFACT_TYPE_REFERENCE();
                    _fsp--;

                     
                    	        currentNode = currentNode.getParent();
                    	    

                    }


                    }


                    }
                    break;

            }

            match(input,65,FOLLOW_65_in_ruleAttributeTypeRestriction5600); 

                    createLeafNode(grammarAccess.getAttributeTypeRestrictionAccess().getSemicolonKeyword_5(), null); 
                

            }


            }

             resetLookahead(); 
                	lastConsumedNode = currentNode;
                
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end ruleAttributeTypeRestriction


    // $ANTLR start entryRuleRelationTypeRestriction
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3014:1: entryRuleRelationTypeRestriction returns [EObject current=null] : iv_ruleRelationTypeRestriction= ruleRelationTypeRestriction EOF ;
    public final EObject entryRuleRelationTypeRestriction() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRelationTypeRestriction = null;


        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3015:2: (iv_ruleRelationTypeRestriction= ruleRelationTypeRestriction EOF )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3016:2: iv_ruleRelationTypeRestriction= ruleRelationTypeRestriction EOF
            {
             currentNode = createCompositeNode(grammarAccess.getRelationTypeRestrictionRule(), currentNode); 
            pushFollow(FOLLOW_ruleRelationTypeRestriction_in_entryRuleRelationTypeRestriction5636);
            iv_ruleRelationTypeRestriction=ruleRelationTypeRestriction();
            _fsp--;

             current =iv_ruleRelationTypeRestriction; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleRelationTypeRestriction5646); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end entryRuleRelationTypeRestriction


    // $ANTLR start ruleRelationTypeRestriction
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3023:1: ruleRelationTypeRestriction returns [EObject current=null] : ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) 'edit' 'relationType' ( ( ruleRELATION_TYPE_REFERENCE ) ) ( (lv_restrictedToSide_4_0= ruleXRelationSideEnum ) ) ';' ) ;
    public final EObject ruleRelationTypeRestriction() throws RecognitionException {
        EObject current = null;

        Enumerator lv_permission_0_0 = null;

        Enumerator lv_restrictedToSide_4_0 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3028:6: ( ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) 'edit' 'relationType' ( ( ruleRELATION_TYPE_REFERENCE ) ) ( (lv_restrictedToSide_4_0= ruleXRelationSideEnum ) ) ';' ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3029:1: ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) 'edit' 'relationType' ( ( ruleRELATION_TYPE_REFERENCE ) ) ( (lv_restrictedToSide_4_0= ruleXRelationSideEnum ) ) ';' )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3029:1: ( ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) 'edit' 'relationType' ( ( ruleRELATION_TYPE_REFERENCE ) ) ( (lv_restrictedToSide_4_0= ruleXRelationSideEnum ) ) ';' )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3029:2: ( (lv_permission_0_0= ruleAccessPermissionEnum ) ) 'edit' 'relationType' ( ( ruleRELATION_TYPE_REFERENCE ) ) ( (lv_restrictedToSide_4_0= ruleXRelationSideEnum ) ) ';'
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3029:2: ( (lv_permission_0_0= ruleAccessPermissionEnum ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3030:1: (lv_permission_0_0= ruleAccessPermissionEnum )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3030:1: (lv_permission_0_0= ruleAccessPermissionEnum )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3031:3: lv_permission_0_0= ruleAccessPermissionEnum
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getRelationTypeRestrictionAccess().getPermissionAccessPermissionEnumEnumRuleCall_0_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleAccessPermissionEnum_in_ruleRelationTypeRestriction5692);
            lv_permission_0_0=ruleAccessPermissionEnum();
            _fsp--;


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getRelationTypeRestrictionRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode.getParent(), current);
            	        }
            	        try {
            	       		set(
            	       			current, 
            	       			"permission",
            	        		lv_permission_0_0, 
            	        		"AccessPermissionEnum", 
            	        		currentNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	        currentNode = currentNode.getParent();
            	    

            }


            }

            match(input,69,FOLLOW_69_in_ruleRelationTypeRestriction5702); 

                    createLeafNode(grammarAccess.getRelationTypeRestrictionAccess().getEditKeyword_1(), null); 
                
            match(input,53,FOLLOW_53_in_ruleRelationTypeRestriction5712); 

                    createLeafNode(grammarAccess.getRelationTypeRestrictionAccess().getRelationTypeKeyword_2(), null); 
                
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3061:1: ( ( ruleRELATION_TYPE_REFERENCE ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3062:1: ( ruleRELATION_TYPE_REFERENCE )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3062:1: ( ruleRELATION_TYPE_REFERENCE )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3063:3: ruleRELATION_TYPE_REFERENCE
            {

            			if (current==null) {
            	            current = factory.create(grammarAccess.getRelationTypeRestrictionRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
                    
             
            	        currentNode=createCompositeNode(grammarAccess.getRelationTypeRestrictionAccess().getRelationTypeRefXRelationTypeCrossReference_3_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleRELATION_TYPE_REFERENCE_in_ruleRelationTypeRestriction5735);
            ruleRELATION_TYPE_REFERENCE();
            _fsp--;

             
            	        currentNode = currentNode.getParent();
            	    

            }


            }

            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3077:2: ( (lv_restrictedToSide_4_0= ruleXRelationSideEnum ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3078:1: (lv_restrictedToSide_4_0= ruleXRelationSideEnum )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3078:1: (lv_restrictedToSide_4_0= ruleXRelationSideEnum )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3079:3: lv_restrictedToSide_4_0= ruleXRelationSideEnum
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getRelationTypeRestrictionAccess().getRestrictedToSideXRelationSideEnumEnumRuleCall_4_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleXRelationSideEnum_in_ruleRelationTypeRestriction5756);
            lv_restrictedToSide_4_0=ruleXRelationSideEnum();
            _fsp--;


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getRelationTypeRestrictionRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode.getParent(), current);
            	        }
            	        try {
            	       		set(
            	       			current, 
            	       			"restrictedToSide",
            	        		lv_restrictedToSide_4_0, 
            	        		"XRelationSideEnum", 
            	        		currentNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	        currentNode = currentNode.getParent();
            	    

            }


            }

            match(input,65,FOLLOW_65_in_ruleRelationTypeRestriction5766); 

                    createLeafNode(grammarAccess.getRelationTypeRestrictionAccess().getSemicolonKeyword_5(), null); 
                

            }


            }

             resetLookahead(); 
                	lastConsumedNode = currentNode;
                
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end ruleRelationTypeRestriction


    // $ANTLR start ruleRelationMultiplicityEnum
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3113:1: ruleRelationMultiplicityEnum returns [Enumerator current=null] : ( ( 'ONE_TO_ONE' ) | ( 'ONE_TO_MANY' ) | ( 'MANY_TO_ONE' ) | ( 'MANY_TO_MANY' ) ) ;
    public final Enumerator ruleRelationMultiplicityEnum() throws RecognitionException {
        Enumerator current = null;

         setCurrentLookahead(); resetLookahead(); 
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3117:6: ( ( ( 'ONE_TO_ONE' ) | ( 'ONE_TO_MANY' ) | ( 'MANY_TO_ONE' ) | ( 'MANY_TO_MANY' ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3118:1: ( ( 'ONE_TO_ONE' ) | ( 'ONE_TO_MANY' ) | ( 'MANY_TO_ONE' ) | ( 'MANY_TO_MANY' ) )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3118:1: ( ( 'ONE_TO_ONE' ) | ( 'ONE_TO_MANY' ) | ( 'MANY_TO_ONE' ) | ( 'MANY_TO_MANY' ) )
            int alt37=4;
            switch ( input.LA(1) ) {
            case 71:
                {
                alt37=1;
                }
                break;
            case 72:
                {
                alt37=2;
                }
                break;
            case 73:
                {
                alt37=3;
                }
                break;
            case 74:
                {
                alt37=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("3118:1: ( ( 'ONE_TO_ONE' ) | ( 'ONE_TO_MANY' ) | ( 'MANY_TO_ONE' ) | ( 'MANY_TO_MANY' ) )", 37, 0, input);

                throw nvae;
            }

            switch (alt37) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3118:2: ( 'ONE_TO_ONE' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3118:2: ( 'ONE_TO_ONE' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3118:4: 'ONE_TO_ONE'
                    {
                    match(input,71,FOLLOW_71_in_ruleRelationMultiplicityEnum5814); 

                            current = grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_ONEEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            createLeafNode(grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_ONEEnumLiteralDeclaration_0(), null); 
                        

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3124:6: ( 'ONE_TO_MANY' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3124:6: ( 'ONE_TO_MANY' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3124:8: 'ONE_TO_MANY'
                    {
                    match(input,72,FOLLOW_72_in_ruleRelationMultiplicityEnum5829); 

                            current = grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_MANYEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                            createLeafNode(grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_MANYEnumLiteralDeclaration_1(), null); 
                        

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3130:6: ( 'MANY_TO_ONE' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3130:6: ( 'MANY_TO_ONE' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3130:8: 'MANY_TO_ONE'
                    {
                    match(input,73,FOLLOW_73_in_ruleRelationMultiplicityEnum5844); 

                            current = grammarAccess.getRelationMultiplicityEnumAccess().getMANY_TO_ONEEnumLiteralDeclaration_2().getEnumLiteral().getInstance();
                            createLeafNode(grammarAccess.getRelationMultiplicityEnumAccess().getMANY_TO_ONEEnumLiteralDeclaration_2(), null); 
                        

                    }


                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3136:6: ( 'MANY_TO_MANY' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3136:6: ( 'MANY_TO_MANY' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3136:8: 'MANY_TO_MANY'
                    {
                    match(input,74,FOLLOW_74_in_ruleRelationMultiplicityEnum5859); 

                            current = grammarAccess.getRelationMultiplicityEnumAccess().getMANY_TO_MANYEnumLiteralDeclaration_3().getEnumLiteral().getInstance();
                            createLeafNode(grammarAccess.getRelationMultiplicityEnumAccess().getMANY_TO_MANYEnumLiteralDeclaration_3(), null); 
                        

                    }


                    }
                    break;

            }


            }

             resetLookahead(); 
                	lastConsumedNode = currentNode;
                
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end ruleRelationMultiplicityEnum


    // $ANTLR start ruleAccessPermissionEnum
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3146:1: ruleAccessPermissionEnum returns [Enumerator current=null] : ( ( 'ALLOW' ) | ( 'DENY' ) ) ;
    public final Enumerator ruleAccessPermissionEnum() throws RecognitionException {
        Enumerator current = null;

         setCurrentLookahead(); resetLookahead(); 
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3150:6: ( ( ( 'ALLOW' ) | ( 'DENY' ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3151:1: ( ( 'ALLOW' ) | ( 'DENY' ) )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3151:1: ( ( 'ALLOW' ) | ( 'DENY' ) )
            int alt38=2;
            int LA38_0 = input.LA(1);

            if ( (LA38_0==75) ) {
                alt38=1;
            }
            else if ( (LA38_0==76) ) {
                alt38=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("3151:1: ( ( 'ALLOW' ) | ( 'DENY' ) )", 38, 0, input);

                throw nvae;
            }
            switch (alt38) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3151:2: ( 'ALLOW' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3151:2: ( 'ALLOW' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3151:4: 'ALLOW'
                    {
                    match(input,75,FOLLOW_75_in_ruleAccessPermissionEnum5902); 

                            current = grammarAccess.getAccessPermissionEnumAccess().getALLOWEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            createLeafNode(grammarAccess.getAccessPermissionEnumAccess().getALLOWEnumLiteralDeclaration_0(), null); 
                        

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3157:6: ( 'DENY' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3157:6: ( 'DENY' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3157:8: 'DENY'
                    {
                    match(input,76,FOLLOW_76_in_ruleAccessPermissionEnum5917); 

                            current = grammarAccess.getAccessPermissionEnumAccess().getDENYEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                            createLeafNode(grammarAccess.getAccessPermissionEnumAccess().getDENYEnumLiteralDeclaration_1(), null); 
                        

                    }


                    }
                    break;

            }


            }

             resetLookahead(); 
                	lastConsumedNode = currentNode;
                
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end ruleAccessPermissionEnum


    // $ANTLR start ruleXRelationSideEnum
    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3167:1: ruleXRelationSideEnum returns [Enumerator current=null] : ( ( 'SIDE_A' ) | ( 'SIDE_B' ) | ( 'BOTH' ) ) ;
    public final Enumerator ruleXRelationSideEnum() throws RecognitionException {
        Enumerator current = null;

         setCurrentLookahead(); resetLookahead(); 
        try {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3171:6: ( ( ( 'SIDE_A' ) | ( 'SIDE_B' ) | ( 'BOTH' ) ) )
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3172:1: ( ( 'SIDE_A' ) | ( 'SIDE_B' ) | ( 'BOTH' ) )
            {
            // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3172:1: ( ( 'SIDE_A' ) | ( 'SIDE_B' ) | ( 'BOTH' ) )
            int alt39=3;
            switch ( input.LA(1) ) {
            case 77:
                {
                alt39=1;
                }
                break;
            case 78:
                {
                alt39=2;
                }
                break;
            case 79:
                {
                alt39=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("3172:1: ( ( 'SIDE_A' ) | ( 'SIDE_B' ) | ( 'BOTH' ) )", 39, 0, input);

                throw nvae;
            }

            switch (alt39) {
                case 1 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3172:2: ( 'SIDE_A' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3172:2: ( 'SIDE_A' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3172:4: 'SIDE_A'
                    {
                    match(input,77,FOLLOW_77_in_ruleXRelationSideEnum5960); 

                            current = grammarAccess.getXRelationSideEnumAccess().getSIDE_AEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            createLeafNode(grammarAccess.getXRelationSideEnumAccess().getSIDE_AEnumLiteralDeclaration_0(), null); 
                        

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3178:6: ( 'SIDE_B' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3178:6: ( 'SIDE_B' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3178:8: 'SIDE_B'
                    {
                    match(input,78,FOLLOW_78_in_ruleXRelationSideEnum5975); 

                            current = grammarAccess.getXRelationSideEnumAccess().getSIDE_BEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                            createLeafNode(grammarAccess.getXRelationSideEnumAccess().getSIDE_BEnumLiteralDeclaration_1(), null); 
                        

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3184:6: ( 'BOTH' )
                    {
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3184:6: ( 'BOTH' )
                    // ../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g:3184:8: 'BOTH'
                    {
                    match(input,79,FOLLOW_79_in_ruleXRelationSideEnum5990); 

                            current = grammarAccess.getXRelationSideEnumAccess().getBOTHEnumLiteralDeclaration_2().getEnumLiteral().getInstance();
                            createLeafNode(grammarAccess.getXRelationSideEnumAccess().getBOTHEnumLiteralDeclaration_2(), null); 
                        

                    }


                    }
                    break;

            }


            }

             resetLookahead(); 
                	lastConsumedNode = currentNode;
                
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end ruleXRelationSideEnum


 

    public static final BitSet FOLLOW_ruleOseeDsl_in_entryRuleOseeDsl75 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleOseeDsl85 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleImport_in_ruleOseeDsl131 = new BitSet(new long[]{0x802240000080D002L,0x000000000000000CL});
    public static final BitSet FOLLOW_ruleXArtifactType_in_ruleOseeDsl154 = new BitSet(new long[]{0x802240000080C002L,0x000000000000000CL});
    public static final BitSet FOLLOW_ruleXRelationType_in_ruleOseeDsl181 = new BitSet(new long[]{0x802240000080C002L,0x000000000000000CL});
    public static final BitSet FOLLOW_ruleXAttributeType_in_ruleOseeDsl208 = new BitSet(new long[]{0x802240000080C002L,0x000000000000000CL});
    public static final BitSet FOLLOW_ruleXOseeEnumType_in_ruleOseeDsl235 = new BitSet(new long[]{0x802240000080C002L,0x000000000000000CL});
    public static final BitSet FOLLOW_ruleXOseeEnumOverride_in_ruleOseeDsl262 = new BitSet(new long[]{0x802240000080C002L,0x000000000000000CL});
    public static final BitSet FOLLOW_ruleXBranchRef_in_ruleOseeDsl286 = new BitSet(new long[]{0x8000000000000002L,0x000000000000000CL});
    public static final BitSet FOLLOW_ruleXArtifactRef_in_ruleOseeDsl313 = new BitSet(new long[]{0x8000000000000002L,0x000000000000000CL});
    public static final BitSet FOLLOW_ruleAccessContext_in_ruleOseeDsl336 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000008L});
    public static final BitSet FOLLOW_ruleImport_in_entryRuleImport373 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleImport383 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_12_in_ruleImport418 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleImport435 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleATTRIBUTE_TYPE_REFERENCE_in_entryRuleATTRIBUTE_TYPE_REFERENCE479 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleATTRIBUTE_TYPE_REFERENCE490 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleATTRIBUTE_TYPE_REFERENCE529 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleARTIFACT_TYPE_REFERENCE_in_entryRuleARTIFACT_TYPE_REFERENCE574 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleARTIFACT_TYPE_REFERENCE585 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleARTIFACT_TYPE_REFERENCE624 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRELATION_TYPE_REFERENCE_in_entryRuleRELATION_TYPE_REFERENCE669 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRELATION_TYPE_REFERENCE680 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleRELATION_TYPE_REFERENCE719 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleENUM_TYPE_REFERENCE_in_entryRuleENUM_TYPE_REFERENCE764 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleENUM_TYPE_REFERENCE775 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleENUM_TYPE_REFERENCE814 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleENUM_ENTRY_TYPE_REFERENCE_in_entryRuleENUM_ENTRY_TYPE_REFERENCE859 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleENUM_ENTRY_TYPE_REFERENCE870 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleENUM_ENTRY_TYPE_REFERENCE909 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_entryRuleQUALIFIED_NAME954 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleQUALIFIED_NAME965 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleQUALIFIED_NAME1005 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_13_in_ruleQUALIFIED_NAME1024 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleQUALIFIED_NAME1039 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_ruleOseeType_in_entryRuleOseeType1088 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleOseeType1098 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXArtifactType_in_ruleOseeType1145 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXRelationType_in_ruleOseeType1172 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXAttributeType_in_ruleOseeType1199 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXOseeEnumType_in_ruleOseeType1226 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXArtifactType_in_entryRuleXArtifactType1261 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleXArtifactType1271 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_14_in_ruleXArtifactType1314 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_ruleXArtifactType1338 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleARTIFACT_TYPE_REFERENCE_in_ruleXArtifactType1359 = new BitSet(new long[]{0x0000000000050000L});
    public static final BitSet FOLLOW_16_in_ruleXArtifactType1370 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleARTIFACT_TYPE_REFERENCE_in_ruleXArtifactType1393 = new BitSet(new long[]{0x0000000000060000L});
    public static final BitSet FOLLOW_17_in_ruleXArtifactType1404 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleARTIFACT_TYPE_REFERENCE_in_ruleXArtifactType1427 = new BitSet(new long[]{0x0000000000060000L});
    public static final BitSet FOLLOW_18_in_ruleXArtifactType1441 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_ruleXArtifactType1451 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXArtifactType1468 = new BitSet(new long[]{0x0000000000300000L});
    public static final BitSet FOLLOW_ruleXAttributeTypeRef_in_ruleXArtifactType1494 = new BitSet(new long[]{0x0000000000300000L});
    public static final BitSet FOLLOW_20_in_ruleXArtifactType1505 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXAttributeTypeRef_in_entryRuleXAttributeTypeRef1541 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleXAttributeTypeRef1551 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_21_in_ruleXAttributeTypeRef1586 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleATTRIBUTE_TYPE_REFERENCE_in_ruleXAttributeTypeRef1609 = new BitSet(new long[]{0x0000000000400002L});
    public static final BitSet FOLLOW_22_in_ruleXAttributeTypeRef1620 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXAttributeTypeRef1637 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXAttributeType_in_entryRuleXAttributeType1680 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleXAttributeType1690 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_23_in_ruleXAttributeType1725 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleATTRIBUTE_TYPE_REFERENCE_in_ruleXAttributeType1746 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_16_in_ruleXAttributeType1757 = new BitSet(new long[]{0x00003FE000000020L});
    public static final BitSet FOLLOW_ruleAttributeBaseType_in_ruleXAttributeType1778 = new BitSet(new long[]{0x0000000001040000L});
    public static final BitSet FOLLOW_24_in_ruleXAttributeType1790 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleATTRIBUTE_TYPE_REFERENCE_in_ruleXAttributeType1813 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_18_in_ruleXAttributeType1825 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_ruleXAttributeType1835 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXAttributeType1852 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_ruleXAttributeType1867 = new BitSet(new long[]{0x000000000C000020L});
    public static final BitSet FOLLOW_26_in_ruleXAttributeType1887 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_27_in_ruleXAttributeType1916 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_ruleXAttributeType1948 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_28_in_ruleXAttributeType1961 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RULE_WHOLE_NUM_STR_in_ruleXAttributeType1978 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_ruleXAttributeType1993 = new BitSet(new long[]{0x0000000040000040L});
    public static final BitSet FOLLOW_RULE_WHOLE_NUM_STR_in_ruleXAttributeType2012 = new BitSet(new long[]{0x0000001E80100000L});
    public static final BitSet FOLLOW_30_in_ruleXAttributeType2033 = new BitSet(new long[]{0x0000001E80100000L});
    public static final BitSet FOLLOW_31_in_ruleXAttributeType2060 = new BitSet(new long[]{0x0000000100000020L});
    public static final BitSet FOLLOW_32_in_ruleXAttributeType2080 = new BitSet(new long[]{0x0000001E00100000L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_ruleXAttributeType2112 = new BitSet(new long[]{0x0000001E00100000L});
    public static final BitSet FOLLOW_33_in_ruleXAttributeType2128 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleENUM_TYPE_REFERENCE_in_ruleXAttributeType2151 = new BitSet(new long[]{0x0000001C00100000L});
    public static final BitSet FOLLOW_34_in_ruleXAttributeType2164 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXAttributeType2181 = new BitSet(new long[]{0x0000001800100000L});
    public static final BitSet FOLLOW_35_in_ruleXAttributeType2199 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXAttributeType2216 = new BitSet(new long[]{0x0000001000100000L});
    public static final BitSet FOLLOW_36_in_ruleXAttributeType2234 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXAttributeType2251 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_ruleXAttributeType2268 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeBaseType_in_entryRuleAttributeBaseType2305 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttributeBaseType2316 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_ruleAttributeBaseType2354 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_38_in_ruleAttributeBaseType2373 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_39_in_ruleAttributeBaseType2392 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_ruleAttributeBaseType2411 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_ruleAttributeBaseType2430 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_ruleAttributeBaseType2449 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_43_in_ruleAttributeBaseType2468 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_ruleAttributeBaseType2487 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_45_in_ruleAttributeBaseType2506 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_ruleAttributeBaseType2534 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXOseeEnumType_in_entryRuleXOseeEnumType2579 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleXOseeEnumType2589 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_46_in_ruleXOseeEnumType2624 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleENUM_TYPE_REFERENCE_in_ruleXOseeEnumType2645 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_18_in_ruleXOseeEnumType2655 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_ruleXOseeEnumType2665 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXOseeEnumType2682 = new BitSet(new long[]{0x0000800000100000L});
    public static final BitSet FOLLOW_ruleXOseeEnumEntry_in_ruleXOseeEnumType2708 = new BitSet(new long[]{0x0000800000100000L});
    public static final BitSet FOLLOW_20_in_ruleXOseeEnumType2719 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXOseeEnumEntry_in_entryRuleXOseeEnumEntry2755 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleXOseeEnumEntry2765 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_ruleXOseeEnumEntry2800 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleENUM_ENTRY_TYPE_REFERENCE_in_ruleXOseeEnumEntry2821 = new BitSet(new long[]{0x0001000000000042L});
    public static final BitSet FOLLOW_RULE_WHOLE_NUM_STR_in_ruleXOseeEnumEntry2838 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_48_in_ruleXOseeEnumEntry2855 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXOseeEnumEntry2872 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXOseeEnumOverride_in_entryRuleXOseeEnumOverride2915 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleXOseeEnumOverride2925 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_49_in_ruleXOseeEnumOverride2960 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleENUM_TYPE_REFERENCE_in_ruleXOseeEnumOverride2983 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_18_in_ruleXOseeEnumOverride2993 = new BitSet(new long[]{0x001C000000100000L});
    public static final BitSet FOLLOW_50_in_ruleXOseeEnumOverride3011 = new BitSet(new long[]{0x0018000000100000L});
    public static final BitSet FOLLOW_ruleOverrideOption_in_ruleXOseeEnumOverride3046 = new BitSet(new long[]{0x0018000000100000L});
    public static final BitSet FOLLOW_20_in_ruleXOseeEnumOverride3057 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOverrideOption_in_entryRuleOverrideOption3093 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleOverrideOption3103 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAddEnum_in_ruleOverrideOption3150 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRemoveEnum_in_ruleOverrideOption3177 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAddEnum_in_entryRuleAddEnum3212 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAddEnum3222 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_51_in_ruleAddEnum3257 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleENUM_ENTRY_TYPE_REFERENCE_in_ruleAddEnum3278 = new BitSet(new long[]{0x0001000000000042L});
    public static final BitSet FOLLOW_RULE_WHOLE_NUM_STR_in_ruleAddEnum3295 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_48_in_ruleAddEnum3312 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAddEnum3329 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRemoveEnum_in_entryRuleRemoveEnum3372 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRemoveEnum3382 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_52_in_ruleRemoveEnum3417 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleENUM_ENTRY_TYPE_REFERENCE_in_ruleRemoveEnum3440 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXRelationType_in_entryRuleXRelationType3476 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleXRelationType3486 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_53_in_ruleXRelationType3521 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleRELATION_TYPE_REFERENCE_in_ruleXRelationType3542 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_18_in_ruleXRelationType3552 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_ruleXRelationType3562 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXRelationType3579 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_54_in_ruleXRelationType3594 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXRelationType3611 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_55_in_ruleXRelationType3626 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleARTIFACT_TYPE_REFERENCE_in_ruleXRelationType3649 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_56_in_ruleXRelationType3659 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXRelationType3676 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_57_in_ruleXRelationType3691 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleARTIFACT_TYPE_REFERENCE_in_ruleXRelationType3714 = new BitSet(new long[]{0x0400000000000000L});
    public static final BitSet FOLLOW_58_in_ruleXRelationType3724 = new BitSet(new long[]{0x7000000000000020L});
    public static final BitSet FOLLOW_ruleRelationOrderType_in_ruleXRelationType3745 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_59_in_ruleXRelationType3755 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000780L});
    public static final BitSet FOLLOW_ruleRelationMultiplicityEnum_in_ruleXRelationType3776 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_ruleXRelationType3786 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelationOrderType_in_entryRuleRelationOrderType3823 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRelationOrderType3834 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_60_in_ruleRelationOrderType3872 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_61_in_ruleRelationOrderType3891 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_62_in_ruleRelationOrderType3910 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleRelationOrderType3931 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleARTIFACT_INSTANCE_REFERENCE_in_entryRuleARTIFACT_INSTANCE_REFERENCE3977 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleARTIFACT_INSTANCE_REFERENCE3988 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleARTIFACT_INSTANCE_REFERENCE4027 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXArtifactRef_in_entryRuleXArtifactRef4071 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleXArtifactRef4081 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_63_in_ruleXArtifactRef4116 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleARTIFACT_INSTANCE_REFERENCE_in_ruleXArtifactRef4137 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_64_in_ruleXArtifactRef4147 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXArtifactRef4164 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_ruleXArtifactRef4179 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleBRANCH_INSTANCE_REFERENCE_in_entryRuleBRANCH_INSTANCE_REFERENCE4216 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleBRANCH_INSTANCE_REFERENCE4227 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleBRANCH_INSTANCE_REFERENCE4266 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXBranchRef_in_entryRuleXBranchRef4310 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleXBranchRef4320 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_66_in_ruleXBranchRef4355 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleBRANCH_INSTANCE_REFERENCE_in_ruleXBranchRef4376 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_22_in_ruleXBranchRef4386 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXBranchRef4403 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_ruleXBranchRef4418 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleACCESS_CONTEXT_TYPE_REFRENCE_in_entryRuleACCESS_CONTEXT_TYPE_REFRENCE4455 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleACCESS_CONTEXT_TYPE_REFRENCE4466 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleACCESS_CONTEXT_TYPE_REFRENCE4505 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAccessContext_in_entryRuleAccessContext4549 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAccessContext4559 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_67_in_ruleAccessContext4594 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleACCESS_CONTEXT_TYPE_REFRENCE_in_ruleAccessContext4615 = new BitSet(new long[]{0x0000000000050000L});
    public static final BitSet FOLLOW_16_in_ruleAccessContext4626 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleACCESS_CONTEXT_TYPE_REFRENCE_in_ruleAccessContext4649 = new BitSet(new long[]{0x0000000000060000L});
    public static final BitSet FOLLOW_17_in_ruleAccessContext4660 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleACCESS_CONTEXT_TYPE_REFRENCE_in_ruleAccessContext4683 = new BitSet(new long[]{0x0000000000060000L});
    public static final BitSet FOLLOW_18_in_ruleAccessContext4697 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_ruleAccessContext4707 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAccessContext4724 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_ruleAccessContext4739 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001810L});
    public static final BitSet FOLLOW_ruleObjectRestriction_in_ruleAccessContext4761 = new BitSet(new long[]{0x0000000000100000L,0x0000000000001810L});
    public static final BitSet FOLLOW_ruleHierarchyRestriction_in_ruleAccessContext4788 = new BitSet(new long[]{0x0000000000100000L,0x0000000000001810L});
    public static final BitSet FOLLOW_20_in_ruleAccessContext4800 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleHierarchyRestriction_in_entryRuleHierarchyRestriction4836 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleHierarchyRestriction4846 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_68_in_ruleHierarchyRestriction4881 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleARTIFACT_INSTANCE_REFERENCE_in_ruleHierarchyRestriction4904 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_18_in_ruleHierarchyRestriction4914 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001800L});
    public static final BitSet FOLLOW_ruleObjectRestriction_in_ruleHierarchyRestriction4935 = new BitSet(new long[]{0x0000000000100000L,0x0000000000001800L});
    public static final BitSet FOLLOW_20_in_ruleHierarchyRestriction4946 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleObjectRestriction_in_entryRuleObjectRestriction4982 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleObjectRestriction4992 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleArtifactInstanceRestriction_in_ruleObjectRestriction5039 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleArtifactTypeRestriction_in_ruleObjectRestriction5066 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelationTypeRestriction_in_ruleObjectRestriction5093 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeTypeRestriction_in_ruleObjectRestriction5120 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleArtifactInstanceRestriction_in_entryRuleArtifactInstanceRestriction5155 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleArtifactInstanceRestriction5165 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAccessPermissionEnum_in_ruleArtifactInstanceRestriction5211 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_69_in_ruleArtifactInstanceRestriction5221 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_63_in_ruleArtifactInstanceRestriction5231 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleARTIFACT_INSTANCE_REFERENCE_in_ruleArtifactInstanceRestriction5254 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_ruleArtifactInstanceRestriction5264 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleArtifactTypeRestriction_in_entryRuleArtifactTypeRestriction5300 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleArtifactTypeRestriction5310 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAccessPermissionEnum_in_ruleArtifactTypeRestriction5356 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_69_in_ruleArtifactTypeRestriction5366 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_ruleArtifactTypeRestriction5376 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleARTIFACT_TYPE_REFERENCE_in_ruleArtifactTypeRestriction5399 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_ruleArtifactTypeRestriction5409 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeTypeRestriction_in_entryRuleAttributeTypeRestriction5445 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttributeTypeRestriction5455 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAccessPermissionEnum_in_ruleAttributeTypeRestriction5501 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_69_in_ruleAttributeTypeRestriction5511 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_ruleAttributeTypeRestriction5521 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleATTRIBUTE_TYPE_REFERENCE_in_ruleAttributeTypeRestriction5544 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000042L});
    public static final BitSet FOLLOW_70_in_ruleAttributeTypeRestriction5555 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_ruleAttributeTypeRestriction5565 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleARTIFACT_TYPE_REFERENCE_in_ruleAttributeTypeRestriction5588 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_ruleAttributeTypeRestriction5600 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelationTypeRestriction_in_entryRuleRelationTypeRestriction5636 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRelationTypeRestriction5646 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAccessPermissionEnum_in_ruleRelationTypeRestriction5692 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_69_in_ruleRelationTypeRestriction5702 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_53_in_ruleRelationTypeRestriction5712 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleRELATION_TYPE_REFERENCE_in_ruleRelationTypeRestriction5735 = new BitSet(new long[]{0x0000000000000000L,0x000000000000E000L});
    public static final BitSet FOLLOW_ruleXRelationSideEnum_in_ruleRelationTypeRestriction5756 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_ruleRelationTypeRestriction5766 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_71_in_ruleRelationMultiplicityEnum5814 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_72_in_ruleRelationMultiplicityEnum5829 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_73_in_ruleRelationMultiplicityEnum5844 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_74_in_ruleRelationMultiplicityEnum5859 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_75_in_ruleAccessPermissionEnum5902 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_76_in_ruleAccessPermissionEnum5917 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_77_in_ruleXRelationSideEnum5960 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_78_in_ruleXRelationSideEnum5975 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_79_in_ruleXRelationSideEnum5990 = new BitSet(new long[]{0x0000000000000002L});

}