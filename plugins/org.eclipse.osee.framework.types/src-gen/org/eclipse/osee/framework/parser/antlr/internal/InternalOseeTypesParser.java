package org.eclipse.osee.framework.parser.antlr.internal; 

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
import org.eclipse.osee.framework.services.OseeTypesGrammarAccess;



import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class InternalOseeTypesParser extends AbstractInternalAntlrParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE_STRING", "RULE_ID", "RULE_WHOLE_NUM_STR", "RULE_INT", "RULE_ML_COMMENT", "RULE_SL_COMMENT", "RULE_WS", "RULE_ANY_OTHER", "'import'", "'.'", "'abstract'", "'artifactType'", "'extends'", "','", "'{'", "'guid'", "'}'", "'attribute'", "'branchGuid'", "'attributeType'", "'overrides'", "'dataProvider'", "'DefaultAttributeDataProvider'", "'UriAttributeDataProvider'", "'min'", "'max'", "'unlimited'", "'taggerId'", "'DefaultAttributeTaggerProvider'", "'enumType'", "'description'", "'defaultValue'", "'fileExtension'", "'BooleanAttribute'", "'CompressedContentAttribute'", "'DateAttribute'", "'EnumeratedAttribute'", "'FloatingPointAttribute'", "'IntegerAttribute'", "'JavaObjectAttribute'", "'StringAttribute'", "'WordAttribute'", "'oseeEnumType'", "'entry'", "'entryGuid'", "'overrides enum'", "'inheritAll'", "'add'", "'remove'", "'relationType'", "'sideAName'", "'sideAArtifactType'", "'sideBName'", "'sideBArtifactType'", "'defaultOrderType'", "'multiplicity'", "'Lexicographical_Ascending'", "'Lexicographical_Descending'", "'Unordered'", "'ONE_TO_ONE'", "'ONE_TO_MANY'", "'MANY_TO_ONE'", "'MANY_TO_MANY'"
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

        public InternalOseeTypesParser(TokenStream input) {
            super(input);
        }
        

    public String[] getTokenNames() { return tokenNames; }
    public String getGrammarFileName() { return "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g"; }



     	private OseeTypesGrammarAccess grammarAccess;
     	
        public InternalOseeTypesParser(TokenStream input, IAstFactory factory, OseeTypesGrammarAccess grammarAccess) {
            this(input);
            this.factory = factory;
            registerRules(grammarAccess.getGrammar());
            this.grammarAccess = grammarAccess;
        }
        
        @Override
        protected InputStream getTokenFile() {
        	ClassLoader classLoader = getClass().getClassLoader();
        	return classLoader.getResourceAsStream("org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.tokens");
        }
        
        @Override
        protected String getFirstRuleName() {
        	return "OseeTypeModel";	
       	} 



    // $ANTLR start entryRuleOseeTypeModel
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:73:1: entryRuleOseeTypeModel returns [EObject current=null] : iv_ruleOseeTypeModel= ruleOseeTypeModel EOF ;
    public final EObject entryRuleOseeTypeModel() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOseeTypeModel = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:74:2: (iv_ruleOseeTypeModel= ruleOseeTypeModel EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:75:2: iv_ruleOseeTypeModel= ruleOseeTypeModel EOF
            {
             currentNode = createCompositeNode(grammarAccess.getOseeTypeModelRule(), currentNode); 
            pushFollow(FOLLOW_ruleOseeTypeModel_in_entryRuleOseeTypeModel75);
            iv_ruleOseeTypeModel=ruleOseeTypeModel();
            _fsp--;

             current =iv_ruleOseeTypeModel; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleOseeTypeModel85); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end entryRuleOseeTypeModel


    // $ANTLR start ruleOseeTypeModel
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:82:1: ruleOseeTypeModel returns [EObject current=null] : ( ( (lv_imports_0_0= ruleImport ) )* ( ( (lv_artifactTypes_1_0= ruleXArtifactType ) ) | ( (lv_relationTypes_2_0= ruleXRelationType ) ) | ( (lv_attributeTypes_3_0= ruleXAttributeType ) ) | ( (lv_enumTypes_4_0= ruleXOseeEnumType ) ) | ( (lv_enumOverrides_5_0= ruleXOseeEnumOverride ) ) )* ) ;
    public final EObject ruleOseeTypeModel() throws RecognitionException {
        EObject current = null;

        EObject lv_imports_0_0 = null;

        EObject lv_artifactTypes_1_0 = null;

        EObject lv_relationTypes_2_0 = null;

        EObject lv_attributeTypes_3_0 = null;

        EObject lv_enumTypes_4_0 = null;

        EObject lv_enumOverrides_5_0 = null;


         @SuppressWarnings("unused") EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:87:6: ( ( ( (lv_imports_0_0= ruleImport ) )* ( ( (lv_artifactTypes_1_0= ruleXArtifactType ) ) | ( (lv_relationTypes_2_0= ruleXRelationType ) ) | ( (lv_attributeTypes_3_0= ruleXAttributeType ) ) | ( (lv_enumTypes_4_0= ruleXOseeEnumType ) ) | ( (lv_enumOverrides_5_0= ruleXOseeEnumOverride ) ) )* ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:88:1: ( ( (lv_imports_0_0= ruleImport ) )* ( ( (lv_artifactTypes_1_0= ruleXArtifactType ) ) | ( (lv_relationTypes_2_0= ruleXRelationType ) ) | ( (lv_attributeTypes_3_0= ruleXAttributeType ) ) | ( (lv_enumTypes_4_0= ruleXOseeEnumType ) ) | ( (lv_enumOverrides_5_0= ruleXOseeEnumOverride ) ) )* )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:88:1: ( ( (lv_imports_0_0= ruleImport ) )* ( ( (lv_artifactTypes_1_0= ruleXArtifactType ) ) | ( (lv_relationTypes_2_0= ruleXRelationType ) ) | ( (lv_attributeTypes_3_0= ruleXAttributeType ) ) | ( (lv_enumTypes_4_0= ruleXOseeEnumType ) ) | ( (lv_enumOverrides_5_0= ruleXOseeEnumOverride ) ) )* )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:88:2: ( (lv_imports_0_0= ruleImport ) )* ( ( (lv_artifactTypes_1_0= ruleXArtifactType ) ) | ( (lv_relationTypes_2_0= ruleXRelationType ) ) | ( (lv_attributeTypes_3_0= ruleXAttributeType ) ) | ( (lv_enumTypes_4_0= ruleXOseeEnumType ) ) | ( (lv_enumOverrides_5_0= ruleXOseeEnumOverride ) ) )*
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:88:2: ( (lv_imports_0_0= ruleImport ) )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==12) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:89:1: (lv_imports_0_0= ruleImport )
            	    {
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:89:1: (lv_imports_0_0= ruleImport )
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:90:3: lv_imports_0_0= ruleImport
            	    {
            	     
            	    	        currentNode=createCompositeNode(grammarAccess.getOseeTypeModelAccess().getImportsImportParserRuleCall_0_0(), currentNode); 
            	    	    
            	    pushFollow(FOLLOW_ruleImport_in_ruleOseeTypeModel131);
            	    lv_imports_0_0=ruleImport();
            	    _fsp--;


            	    	        if (current==null) {
            	    	            current = factory.create(grammarAccess.getOseeTypeModelRule().getType().getClassifier());
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

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:112:3: ( ( (lv_artifactTypes_1_0= ruleXArtifactType ) ) | ( (lv_relationTypes_2_0= ruleXRelationType ) ) | ( (lv_attributeTypes_3_0= ruleXAttributeType ) ) | ( (lv_enumTypes_4_0= ruleXOseeEnumType ) ) | ( (lv_enumOverrides_5_0= ruleXOseeEnumOverride ) ) )*
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
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:112:4: ( (lv_artifactTypes_1_0= ruleXArtifactType ) )
            	    {
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:112:4: ( (lv_artifactTypes_1_0= ruleXArtifactType ) )
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:113:1: (lv_artifactTypes_1_0= ruleXArtifactType )
            	    {
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:113:1: (lv_artifactTypes_1_0= ruleXArtifactType )
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:114:3: lv_artifactTypes_1_0= ruleXArtifactType
            	    {
            	     
            	    	        currentNode=createCompositeNode(grammarAccess.getOseeTypeModelAccess().getArtifactTypesXArtifactTypeParserRuleCall_1_0_0(), currentNode); 
            	    	    
            	    pushFollow(FOLLOW_ruleXArtifactType_in_ruleOseeTypeModel154);
            	    lv_artifactTypes_1_0=ruleXArtifactType();
            	    _fsp--;


            	    	        if (current==null) {
            	    	            current = factory.create(grammarAccess.getOseeTypeModelRule().getType().getClassifier());
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
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:137:6: ( (lv_relationTypes_2_0= ruleXRelationType ) )
            	    {
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:137:6: ( (lv_relationTypes_2_0= ruleXRelationType ) )
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:138:1: (lv_relationTypes_2_0= ruleXRelationType )
            	    {
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:138:1: (lv_relationTypes_2_0= ruleXRelationType )
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:139:3: lv_relationTypes_2_0= ruleXRelationType
            	    {
            	     
            	    	        currentNode=createCompositeNode(grammarAccess.getOseeTypeModelAccess().getRelationTypesXRelationTypeParserRuleCall_1_1_0(), currentNode); 
            	    	    
            	    pushFollow(FOLLOW_ruleXRelationType_in_ruleOseeTypeModel181);
            	    lv_relationTypes_2_0=ruleXRelationType();
            	    _fsp--;


            	    	        if (current==null) {
            	    	            current = factory.create(grammarAccess.getOseeTypeModelRule().getType().getClassifier());
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
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:162:6: ( (lv_attributeTypes_3_0= ruleXAttributeType ) )
            	    {
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:162:6: ( (lv_attributeTypes_3_0= ruleXAttributeType ) )
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:163:1: (lv_attributeTypes_3_0= ruleXAttributeType )
            	    {
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:163:1: (lv_attributeTypes_3_0= ruleXAttributeType )
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:164:3: lv_attributeTypes_3_0= ruleXAttributeType
            	    {
            	     
            	    	        currentNode=createCompositeNode(grammarAccess.getOseeTypeModelAccess().getAttributeTypesXAttributeTypeParserRuleCall_1_2_0(), currentNode); 
            	    	    
            	    pushFollow(FOLLOW_ruleXAttributeType_in_ruleOseeTypeModel208);
            	    lv_attributeTypes_3_0=ruleXAttributeType();
            	    _fsp--;


            	    	        if (current==null) {
            	    	            current = factory.create(grammarAccess.getOseeTypeModelRule().getType().getClassifier());
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
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:187:6: ( (lv_enumTypes_4_0= ruleXOseeEnumType ) )
            	    {
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:187:6: ( (lv_enumTypes_4_0= ruleXOseeEnumType ) )
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:188:1: (lv_enumTypes_4_0= ruleXOseeEnumType )
            	    {
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:188:1: (lv_enumTypes_4_0= ruleXOseeEnumType )
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:189:3: lv_enumTypes_4_0= ruleXOseeEnumType
            	    {
            	     
            	    	        currentNode=createCompositeNode(grammarAccess.getOseeTypeModelAccess().getEnumTypesXOseeEnumTypeParserRuleCall_1_3_0(), currentNode); 
            	    	    
            	    pushFollow(FOLLOW_ruleXOseeEnumType_in_ruleOseeTypeModel235);
            	    lv_enumTypes_4_0=ruleXOseeEnumType();
            	    _fsp--;


            	    	        if (current==null) {
            	    	            current = factory.create(grammarAccess.getOseeTypeModelRule().getType().getClassifier());
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
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:212:6: ( (lv_enumOverrides_5_0= ruleXOseeEnumOverride ) )
            	    {
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:212:6: ( (lv_enumOverrides_5_0= ruleXOseeEnumOverride ) )
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:213:1: (lv_enumOverrides_5_0= ruleXOseeEnumOverride )
            	    {
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:213:1: (lv_enumOverrides_5_0= ruleXOseeEnumOverride )
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:214:3: lv_enumOverrides_5_0= ruleXOseeEnumOverride
            	    {
            	     
            	    	        currentNode=createCompositeNode(grammarAccess.getOseeTypeModelAccess().getEnumOverridesXOseeEnumOverrideParserRuleCall_1_4_0(), currentNode); 
            	    	    
            	    pushFollow(FOLLOW_ruleXOseeEnumOverride_in_ruleOseeTypeModel262);
            	    lv_enumOverrides_5_0=ruleXOseeEnumOverride();
            	    _fsp--;


            	    	        if (current==null) {
            	    	            current = factory.create(grammarAccess.getOseeTypeModelRule().getType().getClassifier());
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
    // $ANTLR end ruleOseeTypeModel


    // $ANTLR start entryRuleImport
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:244:1: entryRuleImport returns [EObject current=null] : iv_ruleImport= ruleImport EOF ;
    public final EObject entryRuleImport() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleImport = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:245:2: (iv_ruleImport= ruleImport EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:246:2: iv_ruleImport= ruleImport EOF
            {
             currentNode = createCompositeNode(grammarAccess.getImportRule(), currentNode); 
            pushFollow(FOLLOW_ruleImport_in_entryRuleImport300);
            iv_ruleImport=ruleImport();
            _fsp--;

             current =iv_ruleImport; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleImport310); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:253:1: ruleImport returns [EObject current=null] : ( 'import' ( (lv_importURI_1_0= RULE_STRING ) ) ) ;
    public final EObject ruleImport() throws RecognitionException {
        EObject current = null;

        Token lv_importURI_1_0=null;

         @SuppressWarnings("unused") EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:258:6: ( ( 'import' ( (lv_importURI_1_0= RULE_STRING ) ) ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:259:1: ( 'import' ( (lv_importURI_1_0= RULE_STRING ) ) )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:259:1: ( 'import' ( (lv_importURI_1_0= RULE_STRING ) ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:259:3: 'import' ( (lv_importURI_1_0= RULE_STRING ) )
            {
            match(input,12,FOLLOW_12_in_ruleImport345); 

                    createLeafNode(grammarAccess.getImportAccess().getImportKeyword_0(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:263:1: ( (lv_importURI_1_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:264:1: (lv_importURI_1_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:264:1: (lv_importURI_1_0= RULE_STRING )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:265:3: lv_importURI_1_0= RULE_STRING
            {
            lv_importURI_1_0=(Token)input.LT(1);
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleImport362); 

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


    // $ANTLR start entryRuleNAME_REFERENCE
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:295:1: entryRuleNAME_REFERENCE returns [String current=null] : iv_ruleNAME_REFERENCE= ruleNAME_REFERENCE EOF ;
    public final String entryRuleNAME_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleNAME_REFERENCE = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:296:2: (iv_ruleNAME_REFERENCE= ruleNAME_REFERENCE EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:297:2: iv_ruleNAME_REFERENCE= ruleNAME_REFERENCE EOF
            {
             currentNode = createCompositeNode(grammarAccess.getNAME_REFERENCERule(), currentNode); 
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_entryRuleNAME_REFERENCE404);
            iv_ruleNAME_REFERENCE=ruleNAME_REFERENCE();
            _fsp--;

             current =iv_ruleNAME_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleNAME_REFERENCE415); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end entryRuleNAME_REFERENCE


    // $ANTLR start ruleNAME_REFERENCE
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:304:1: ruleNAME_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleNAME_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:309:6: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:310:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)input.LT(1);
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleNAME_REFERENCE454); 

            		current.merge(this_STRING_0);
                
             
                createLeafNode(grammarAccess.getNAME_REFERENCEAccess().getSTRINGTerminalRuleCall(), null); 
                

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
    // $ANTLR end ruleNAME_REFERENCE


    // $ANTLR start entryRuleQUALIFIED_NAME
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:325:1: entryRuleQUALIFIED_NAME returns [String current=null] : iv_ruleQUALIFIED_NAME= ruleQUALIFIED_NAME EOF ;
    public final String entryRuleQUALIFIED_NAME() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleQUALIFIED_NAME = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:326:2: (iv_ruleQUALIFIED_NAME= ruleQUALIFIED_NAME EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:327:2: iv_ruleQUALIFIED_NAME= ruleQUALIFIED_NAME EOF
            {
             currentNode = createCompositeNode(grammarAccess.getQUALIFIED_NAMERule(), currentNode); 
            pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_entryRuleQUALIFIED_NAME499);
            iv_ruleQUALIFIED_NAME=ruleQUALIFIED_NAME();
            _fsp--;

             current =iv_ruleQUALIFIED_NAME.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleQUALIFIED_NAME510); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:334:1: ruleQUALIFIED_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* ) ;
    public final AntlrDatatypeRuleToken ruleQUALIFIED_NAME() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_ID_0=null;
        Token kw=null;
        Token this_ID_2=null;

         setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:339:6: ( (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:340:1: (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:340:1: (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:340:6: this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )*
            {
            this_ID_0=(Token)input.LT(1);
            match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleQUALIFIED_NAME550); 

            		current.merge(this_ID_0);
                
             
                createLeafNode(grammarAccess.getQUALIFIED_NAMEAccess().getIDTerminalRuleCall_0(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:347:1: (kw= '.' this_ID_2= RULE_ID )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==13) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:348:2: kw= '.' this_ID_2= RULE_ID
            	    {
            	    kw=(Token)input.LT(1);
            	    match(input,13,FOLLOW_13_in_ruleQUALIFIED_NAME569); 

            	            current.merge(kw);
            	            createLeafNode(grammarAccess.getQUALIFIED_NAMEAccess().getFullStopKeyword_1_0(), null); 
            	        
            	    this_ID_2=(Token)input.LT(1);
            	    match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleQUALIFIED_NAME584); 

            	    		current.merge(this_ID_2);
            	        
            	     
            	        createLeafNode(grammarAccess.getQUALIFIED_NAMEAccess().getIDTerminalRuleCall_1_1(), null); 
            	        

            	    }
            	    break;

            	default :
            	    break loop3;
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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:370:1: entryRuleOseeType returns [EObject current=null] : iv_ruleOseeType= ruleOseeType EOF ;
    public final EObject entryRuleOseeType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOseeType = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:371:2: (iv_ruleOseeType= ruleOseeType EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:372:2: iv_ruleOseeType= ruleOseeType EOF
            {
             currentNode = createCompositeNode(grammarAccess.getOseeTypeRule(), currentNode); 
            pushFollow(FOLLOW_ruleOseeType_in_entryRuleOseeType633);
            iv_ruleOseeType=ruleOseeType();
            _fsp--;

             current =iv_ruleOseeType; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleOseeType643); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:379:1: ruleOseeType returns [EObject current=null] : (this_XArtifactType_0= ruleXArtifactType | this_XRelationType_1= ruleXRelationType | this_XAttributeType_2= ruleXAttributeType | this_XOseeEnumType_3= ruleXOseeEnumType ) ;
    public final EObject ruleOseeType() throws RecognitionException {
        EObject current = null;

        EObject this_XArtifactType_0 = null;

        EObject this_XRelationType_1 = null;

        EObject this_XAttributeType_2 = null;

        EObject this_XOseeEnumType_3 = null;


         @SuppressWarnings("unused") EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:384:6: ( (this_XArtifactType_0= ruleXArtifactType | this_XRelationType_1= ruleXRelationType | this_XAttributeType_2= ruleXAttributeType | this_XOseeEnumType_3= ruleXOseeEnumType ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:385:1: (this_XArtifactType_0= ruleXArtifactType | this_XRelationType_1= ruleXRelationType | this_XAttributeType_2= ruleXAttributeType | this_XOseeEnumType_3= ruleXOseeEnumType )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:385:1: (this_XArtifactType_0= ruleXArtifactType | this_XRelationType_1= ruleXRelationType | this_XAttributeType_2= ruleXAttributeType | this_XOseeEnumType_3= ruleXOseeEnumType )
            int alt4=4;
            switch ( input.LA(1) ) {
            case 14:
            case 15:
                {
                alt4=1;
                }
                break;
            case 53:
                {
                alt4=2;
                }
                break;
            case 23:
                {
                alt4=3;
                }
                break;
            case 46:
                {
                alt4=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("385:1: (this_XArtifactType_0= ruleXArtifactType | this_XRelationType_1= ruleXRelationType | this_XAttributeType_2= ruleXAttributeType | this_XOseeEnumType_3= ruleXOseeEnumType )", 4, 0, input);

                throw nvae;
            }

            switch (alt4) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:386:5: this_XArtifactType_0= ruleXArtifactType
                    {
                     
                            currentNode=createCompositeNode(grammarAccess.getOseeTypeAccess().getXArtifactTypeParserRuleCall_0(), currentNode); 
                        
                    pushFollow(FOLLOW_ruleXArtifactType_in_ruleOseeType690);
                    this_XArtifactType_0=ruleXArtifactType();
                    _fsp--;

                     
                            current = this_XArtifactType_0; 
                            currentNode = currentNode.getParent();
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:396:5: this_XRelationType_1= ruleXRelationType
                    {
                     
                            currentNode=createCompositeNode(grammarAccess.getOseeTypeAccess().getXRelationTypeParserRuleCall_1(), currentNode); 
                        
                    pushFollow(FOLLOW_ruleXRelationType_in_ruleOseeType717);
                    this_XRelationType_1=ruleXRelationType();
                    _fsp--;

                     
                            current = this_XRelationType_1; 
                            currentNode = currentNode.getParent();
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:406:5: this_XAttributeType_2= ruleXAttributeType
                    {
                     
                            currentNode=createCompositeNode(grammarAccess.getOseeTypeAccess().getXAttributeTypeParserRuleCall_2(), currentNode); 
                        
                    pushFollow(FOLLOW_ruleXAttributeType_in_ruleOseeType744);
                    this_XAttributeType_2=ruleXAttributeType();
                    _fsp--;

                     
                            current = this_XAttributeType_2; 
                            currentNode = currentNode.getParent();
                        

                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:416:5: this_XOseeEnumType_3= ruleXOseeEnumType
                    {
                     
                            currentNode=createCompositeNode(grammarAccess.getOseeTypeAccess().getXOseeEnumTypeParserRuleCall_3(), currentNode); 
                        
                    pushFollow(FOLLOW_ruleXOseeEnumType_in_ruleOseeType771);
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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:432:1: entryRuleXArtifactType returns [EObject current=null] : iv_ruleXArtifactType= ruleXArtifactType EOF ;
    public final EObject entryRuleXArtifactType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleXArtifactType = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:433:2: (iv_ruleXArtifactType= ruleXArtifactType EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:434:2: iv_ruleXArtifactType= ruleXArtifactType EOF
            {
             currentNode = createCompositeNode(grammarAccess.getXArtifactTypeRule(), currentNode); 
            pushFollow(FOLLOW_ruleXArtifactType_in_entryRuleXArtifactType806);
            iv_ruleXArtifactType=ruleXArtifactType();
            _fsp--;

             current =iv_ruleXArtifactType; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleXArtifactType816); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:441:1: ruleXArtifactType returns [EObject current=null] : ( ( (lv_abstract_0_0= 'abstract' ) )? 'artifactType' ( (lv_name_2_0= ruleNAME_REFERENCE ) ) ( 'extends' ( ( ruleNAME_REFERENCE ) ) ( ',' ( ( ruleNAME_REFERENCE ) ) )* )? '{' 'guid' ( (lv_typeGuid_9_0= RULE_STRING ) ) ( (lv_validAttributeTypes_10_0= ruleXAttributeTypeRef ) )* '}' ) ;
    public final EObject ruleXArtifactType() throws RecognitionException {
        EObject current = null;

        Token lv_abstract_0_0=null;
        Token lv_typeGuid_9_0=null;
        AntlrDatatypeRuleToken lv_name_2_0 = null;

        EObject lv_validAttributeTypes_10_0 = null;


         @SuppressWarnings("unused") EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:446:6: ( ( ( (lv_abstract_0_0= 'abstract' ) )? 'artifactType' ( (lv_name_2_0= ruleNAME_REFERENCE ) ) ( 'extends' ( ( ruleNAME_REFERENCE ) ) ( ',' ( ( ruleNAME_REFERENCE ) ) )* )? '{' 'guid' ( (lv_typeGuid_9_0= RULE_STRING ) ) ( (lv_validAttributeTypes_10_0= ruleXAttributeTypeRef ) )* '}' ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:447:1: ( ( (lv_abstract_0_0= 'abstract' ) )? 'artifactType' ( (lv_name_2_0= ruleNAME_REFERENCE ) ) ( 'extends' ( ( ruleNAME_REFERENCE ) ) ( ',' ( ( ruleNAME_REFERENCE ) ) )* )? '{' 'guid' ( (lv_typeGuid_9_0= RULE_STRING ) ) ( (lv_validAttributeTypes_10_0= ruleXAttributeTypeRef ) )* '}' )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:447:1: ( ( (lv_abstract_0_0= 'abstract' ) )? 'artifactType' ( (lv_name_2_0= ruleNAME_REFERENCE ) ) ( 'extends' ( ( ruleNAME_REFERENCE ) ) ( ',' ( ( ruleNAME_REFERENCE ) ) )* )? '{' 'guid' ( (lv_typeGuid_9_0= RULE_STRING ) ) ( (lv_validAttributeTypes_10_0= ruleXAttributeTypeRef ) )* '}' )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:447:2: ( (lv_abstract_0_0= 'abstract' ) )? 'artifactType' ( (lv_name_2_0= ruleNAME_REFERENCE ) ) ( 'extends' ( ( ruleNAME_REFERENCE ) ) ( ',' ( ( ruleNAME_REFERENCE ) ) )* )? '{' 'guid' ( (lv_typeGuid_9_0= RULE_STRING ) ) ( (lv_validAttributeTypes_10_0= ruleXAttributeTypeRef ) )* '}'
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:447:2: ( (lv_abstract_0_0= 'abstract' ) )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==14) ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:448:1: (lv_abstract_0_0= 'abstract' )
                    {
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:448:1: (lv_abstract_0_0= 'abstract' )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:449:3: lv_abstract_0_0= 'abstract'
                    {
                    lv_abstract_0_0=(Token)input.LT(1);
                    match(input,14,FOLLOW_14_in_ruleXArtifactType859); 

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

            match(input,15,FOLLOW_15_in_ruleXArtifactType883); 

                    createLeafNode(grammarAccess.getXArtifactTypeAccess().getArtifactTypeKeyword_1(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:472:1: ( (lv_name_2_0= ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:473:1: (lv_name_2_0= ruleNAME_REFERENCE )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:473:1: (lv_name_2_0= ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:474:3: lv_name_2_0= ruleNAME_REFERENCE
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getXArtifactTypeAccess().getNameNAME_REFERENCEParserRuleCall_2_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleXArtifactType904);
            lv_name_2_0=ruleNAME_REFERENCE();
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
            	        		"NAME_REFERENCE", 
            	        		currentNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	        currentNode = currentNode.getParent();
            	    

            }


            }

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:496:2: ( 'extends' ( ( ruleNAME_REFERENCE ) ) ( ',' ( ( ruleNAME_REFERENCE ) ) )* )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==16) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:496:4: 'extends' ( ( ruleNAME_REFERENCE ) ) ( ',' ( ( ruleNAME_REFERENCE ) ) )*
                    {
                    match(input,16,FOLLOW_16_in_ruleXArtifactType915); 

                            createLeafNode(grammarAccess.getXArtifactTypeAccess().getExtendsKeyword_3_0(), null); 
                        
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:500:1: ( ( ruleNAME_REFERENCE ) )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:501:1: ( ruleNAME_REFERENCE )
                    {
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:501:1: ( ruleNAME_REFERENCE )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:502:3: ruleNAME_REFERENCE
                    {

                    			if (current==null) {
                    	            current = factory.create(grammarAccess.getXArtifactTypeRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                            
                     
                    	        currentNode=createCompositeNode(grammarAccess.getXArtifactTypeAccess().getSuperArtifactTypesXArtifactTypeCrossReference_3_1_0(), currentNode); 
                    	    
                    pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleXArtifactType938);
                    ruleNAME_REFERENCE();
                    _fsp--;

                     
                    	        currentNode = currentNode.getParent();
                    	    

                    }


                    }

                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:516:2: ( ',' ( ( ruleNAME_REFERENCE ) ) )*
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( (LA6_0==17) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:516:4: ',' ( ( ruleNAME_REFERENCE ) )
                    	    {
                    	    match(input,17,FOLLOW_17_in_ruleXArtifactType949); 

                    	            createLeafNode(grammarAccess.getXArtifactTypeAccess().getCommaKeyword_3_2_0(), null); 
                    	        
                    	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:520:1: ( ( ruleNAME_REFERENCE ) )
                    	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:521:1: ( ruleNAME_REFERENCE )
                    	    {
                    	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:521:1: ( ruleNAME_REFERENCE )
                    	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:522:3: ruleNAME_REFERENCE
                    	    {

                    	    			if (current==null) {
                    	    	            current = factory.create(grammarAccess.getXArtifactTypeRule().getType().getClassifier());
                    	    	            associateNodeWithAstElement(currentNode, current);
                    	    	        }
                    	            
                    	     
                    	    	        currentNode=createCompositeNode(grammarAccess.getXArtifactTypeAccess().getSuperArtifactTypesXArtifactTypeCrossReference_3_2_1_0(), currentNode); 
                    	    	    
                    	    pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleXArtifactType972);
                    	    ruleNAME_REFERENCE();
                    	    _fsp--;

                    	     
                    	    	        currentNode = currentNode.getParent();
                    	    	    

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

            match(input,18,FOLLOW_18_in_ruleXArtifactType986); 

                    createLeafNode(grammarAccess.getXArtifactTypeAccess().getLeftCurlyBracketKeyword_4(), null); 
                
            match(input,19,FOLLOW_19_in_ruleXArtifactType996); 

                    createLeafNode(grammarAccess.getXArtifactTypeAccess().getGuidKeyword_5(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:544:1: ( (lv_typeGuid_9_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:545:1: (lv_typeGuid_9_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:545:1: (lv_typeGuid_9_0= RULE_STRING )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:546:3: lv_typeGuid_9_0= RULE_STRING
            {
            lv_typeGuid_9_0=(Token)input.LT(1);
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXArtifactType1013); 

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

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:568:2: ( (lv_validAttributeTypes_10_0= ruleXAttributeTypeRef ) )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==21) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:569:1: (lv_validAttributeTypes_10_0= ruleXAttributeTypeRef )
            	    {
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:569:1: (lv_validAttributeTypes_10_0= ruleXAttributeTypeRef )
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:570:3: lv_validAttributeTypes_10_0= ruleXAttributeTypeRef
            	    {
            	     
            	    	        currentNode=createCompositeNode(grammarAccess.getXArtifactTypeAccess().getValidAttributeTypesXAttributeTypeRefParserRuleCall_7_0(), currentNode); 
            	    	    
            	    pushFollow(FOLLOW_ruleXAttributeTypeRef_in_ruleXArtifactType1039);
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
            	    break loop8;
                }
            } while (true);

            match(input,20,FOLLOW_20_in_ruleXArtifactType1050); 

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:604:1: entryRuleXAttributeTypeRef returns [EObject current=null] : iv_ruleXAttributeTypeRef= ruleXAttributeTypeRef EOF ;
    public final EObject entryRuleXAttributeTypeRef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleXAttributeTypeRef = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:605:2: (iv_ruleXAttributeTypeRef= ruleXAttributeTypeRef EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:606:2: iv_ruleXAttributeTypeRef= ruleXAttributeTypeRef EOF
            {
             currentNode = createCompositeNode(grammarAccess.getXAttributeTypeRefRule(), currentNode); 
            pushFollow(FOLLOW_ruleXAttributeTypeRef_in_entryRuleXAttributeTypeRef1086);
            iv_ruleXAttributeTypeRef=ruleXAttributeTypeRef();
            _fsp--;

             current =iv_ruleXAttributeTypeRef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleXAttributeTypeRef1096); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:613:1: ruleXAttributeTypeRef returns [EObject current=null] : ( 'attribute' ( ( ruleNAME_REFERENCE ) ) ( 'branchGuid' ( (lv_branchGuid_3_0= RULE_STRING ) ) )? ) ;
    public final EObject ruleXAttributeTypeRef() throws RecognitionException {
        EObject current = null;

        Token lv_branchGuid_3_0=null;

         @SuppressWarnings("unused") EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:618:6: ( ( 'attribute' ( ( ruleNAME_REFERENCE ) ) ( 'branchGuid' ( (lv_branchGuid_3_0= RULE_STRING ) ) )? ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:619:1: ( 'attribute' ( ( ruleNAME_REFERENCE ) ) ( 'branchGuid' ( (lv_branchGuid_3_0= RULE_STRING ) ) )? )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:619:1: ( 'attribute' ( ( ruleNAME_REFERENCE ) ) ( 'branchGuid' ( (lv_branchGuid_3_0= RULE_STRING ) ) )? )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:619:3: 'attribute' ( ( ruleNAME_REFERENCE ) ) ( 'branchGuid' ( (lv_branchGuid_3_0= RULE_STRING ) ) )?
            {
            match(input,21,FOLLOW_21_in_ruleXAttributeTypeRef1131); 

                    createLeafNode(grammarAccess.getXAttributeTypeRefAccess().getAttributeKeyword_0(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:623:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:624:1: ( ruleNAME_REFERENCE )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:624:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:625:3: ruleNAME_REFERENCE
            {

            			if (current==null) {
            	            current = factory.create(grammarAccess.getXAttributeTypeRefRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
                    
             
            	        currentNode=createCompositeNode(grammarAccess.getXAttributeTypeRefAccess().getValidAttributeTypeXAttributeTypeCrossReference_1_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleXAttributeTypeRef1154);
            ruleNAME_REFERENCE();
            _fsp--;

             
            	        currentNode = currentNode.getParent();
            	    

            }


            }

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:639:2: ( 'branchGuid' ( (lv_branchGuid_3_0= RULE_STRING ) ) )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==22) ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:639:4: 'branchGuid' ( (lv_branchGuid_3_0= RULE_STRING ) )
                    {
                    match(input,22,FOLLOW_22_in_ruleXAttributeTypeRef1165); 

                            createLeafNode(grammarAccess.getXAttributeTypeRefAccess().getBranchGuidKeyword_2_0(), null); 
                        
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:643:1: ( (lv_branchGuid_3_0= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:644:1: (lv_branchGuid_3_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:644:1: (lv_branchGuid_3_0= RULE_STRING )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:645:3: lv_branchGuid_3_0= RULE_STRING
                    {
                    lv_branchGuid_3_0=(Token)input.LT(1);
                    match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXAttributeTypeRef1182); 

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:675:1: entryRuleXAttributeType returns [EObject current=null] : iv_ruleXAttributeType= ruleXAttributeType EOF ;
    public final EObject entryRuleXAttributeType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleXAttributeType = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:676:2: (iv_ruleXAttributeType= ruleXAttributeType EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:677:2: iv_ruleXAttributeType= ruleXAttributeType EOF
            {
             currentNode = createCompositeNode(grammarAccess.getXAttributeTypeRule(), currentNode); 
            pushFollow(FOLLOW_ruleXAttributeType_in_entryRuleXAttributeType1225);
            iv_ruleXAttributeType=ruleXAttributeType();
            _fsp--;

             current =iv_ruleXAttributeType; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleXAttributeType1235); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:684:1: ruleXAttributeType returns [EObject current=null] : ( 'attributeType' ( (lv_name_1_0= ruleNAME_REFERENCE ) ) ( 'extends' ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) ) ) ( 'overrides' ( ( ruleNAME_REFERENCE ) ) )? '{' 'guid' ( (lv_typeGuid_8_0= RULE_STRING ) ) 'dataProvider' ( ( (lv_dataProvider_10_1= 'DefaultAttributeDataProvider' | lv_dataProvider_10_2= 'UriAttributeDataProvider' | lv_dataProvider_10_3= ruleQUALIFIED_NAME ) ) ) 'min' ( (lv_min_12_0= RULE_WHOLE_NUM_STR ) ) 'max' ( ( (lv_max_14_1= RULE_WHOLE_NUM_STR | lv_max_14_2= 'unlimited' ) ) ) ( 'taggerId' ( ( (lv_taggerId_16_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_16_2= ruleQUALIFIED_NAME ) ) ) )? ( 'enumType' ( ( ruleNAME_REFERENCE ) ) )? ( 'description' ( (lv_description_20_0= RULE_STRING ) ) )? ( 'defaultValue' ( (lv_defaultValue_22_0= RULE_STRING ) ) )? ( 'fileExtension' ( (lv_fileExtension_24_0= RULE_STRING ) ) )? '}' ) ;
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


         @SuppressWarnings("unused") EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:689:6: ( ( 'attributeType' ( (lv_name_1_0= ruleNAME_REFERENCE ) ) ( 'extends' ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) ) ) ( 'overrides' ( ( ruleNAME_REFERENCE ) ) )? '{' 'guid' ( (lv_typeGuid_8_0= RULE_STRING ) ) 'dataProvider' ( ( (lv_dataProvider_10_1= 'DefaultAttributeDataProvider' | lv_dataProvider_10_2= 'UriAttributeDataProvider' | lv_dataProvider_10_3= ruleQUALIFIED_NAME ) ) ) 'min' ( (lv_min_12_0= RULE_WHOLE_NUM_STR ) ) 'max' ( ( (lv_max_14_1= RULE_WHOLE_NUM_STR | lv_max_14_2= 'unlimited' ) ) ) ( 'taggerId' ( ( (lv_taggerId_16_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_16_2= ruleQUALIFIED_NAME ) ) ) )? ( 'enumType' ( ( ruleNAME_REFERENCE ) ) )? ( 'description' ( (lv_description_20_0= RULE_STRING ) ) )? ( 'defaultValue' ( (lv_defaultValue_22_0= RULE_STRING ) ) )? ( 'fileExtension' ( (lv_fileExtension_24_0= RULE_STRING ) ) )? '}' ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:690:1: ( 'attributeType' ( (lv_name_1_0= ruleNAME_REFERENCE ) ) ( 'extends' ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) ) ) ( 'overrides' ( ( ruleNAME_REFERENCE ) ) )? '{' 'guid' ( (lv_typeGuid_8_0= RULE_STRING ) ) 'dataProvider' ( ( (lv_dataProvider_10_1= 'DefaultAttributeDataProvider' | lv_dataProvider_10_2= 'UriAttributeDataProvider' | lv_dataProvider_10_3= ruleQUALIFIED_NAME ) ) ) 'min' ( (lv_min_12_0= RULE_WHOLE_NUM_STR ) ) 'max' ( ( (lv_max_14_1= RULE_WHOLE_NUM_STR | lv_max_14_2= 'unlimited' ) ) ) ( 'taggerId' ( ( (lv_taggerId_16_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_16_2= ruleQUALIFIED_NAME ) ) ) )? ( 'enumType' ( ( ruleNAME_REFERENCE ) ) )? ( 'description' ( (lv_description_20_0= RULE_STRING ) ) )? ( 'defaultValue' ( (lv_defaultValue_22_0= RULE_STRING ) ) )? ( 'fileExtension' ( (lv_fileExtension_24_0= RULE_STRING ) ) )? '}' )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:690:1: ( 'attributeType' ( (lv_name_1_0= ruleNAME_REFERENCE ) ) ( 'extends' ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) ) ) ( 'overrides' ( ( ruleNAME_REFERENCE ) ) )? '{' 'guid' ( (lv_typeGuid_8_0= RULE_STRING ) ) 'dataProvider' ( ( (lv_dataProvider_10_1= 'DefaultAttributeDataProvider' | lv_dataProvider_10_2= 'UriAttributeDataProvider' | lv_dataProvider_10_3= ruleQUALIFIED_NAME ) ) ) 'min' ( (lv_min_12_0= RULE_WHOLE_NUM_STR ) ) 'max' ( ( (lv_max_14_1= RULE_WHOLE_NUM_STR | lv_max_14_2= 'unlimited' ) ) ) ( 'taggerId' ( ( (lv_taggerId_16_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_16_2= ruleQUALIFIED_NAME ) ) ) )? ( 'enumType' ( ( ruleNAME_REFERENCE ) ) )? ( 'description' ( (lv_description_20_0= RULE_STRING ) ) )? ( 'defaultValue' ( (lv_defaultValue_22_0= RULE_STRING ) ) )? ( 'fileExtension' ( (lv_fileExtension_24_0= RULE_STRING ) ) )? '}' )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:690:3: 'attributeType' ( (lv_name_1_0= ruleNAME_REFERENCE ) ) ( 'extends' ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) ) ) ( 'overrides' ( ( ruleNAME_REFERENCE ) ) )? '{' 'guid' ( (lv_typeGuid_8_0= RULE_STRING ) ) 'dataProvider' ( ( (lv_dataProvider_10_1= 'DefaultAttributeDataProvider' | lv_dataProvider_10_2= 'UriAttributeDataProvider' | lv_dataProvider_10_3= ruleQUALIFIED_NAME ) ) ) 'min' ( (lv_min_12_0= RULE_WHOLE_NUM_STR ) ) 'max' ( ( (lv_max_14_1= RULE_WHOLE_NUM_STR | lv_max_14_2= 'unlimited' ) ) ) ( 'taggerId' ( ( (lv_taggerId_16_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_16_2= ruleQUALIFIED_NAME ) ) ) )? ( 'enumType' ( ( ruleNAME_REFERENCE ) ) )? ( 'description' ( (lv_description_20_0= RULE_STRING ) ) )? ( 'defaultValue' ( (lv_defaultValue_22_0= RULE_STRING ) ) )? ( 'fileExtension' ( (lv_fileExtension_24_0= RULE_STRING ) ) )? '}'
            {
            match(input,23,FOLLOW_23_in_ruleXAttributeType1270); 

                    createLeafNode(grammarAccess.getXAttributeTypeAccess().getAttributeTypeKeyword_0(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:694:1: ( (lv_name_1_0= ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:695:1: (lv_name_1_0= ruleNAME_REFERENCE )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:695:1: (lv_name_1_0= ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:696:3: lv_name_1_0= ruleNAME_REFERENCE
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getXAttributeTypeAccess().getNameNAME_REFERENCEParserRuleCall_1_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleXAttributeType1291);
            lv_name_1_0=ruleNAME_REFERENCE();
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
            	        		"NAME_REFERENCE", 
            	        		currentNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	        currentNode = currentNode.getParent();
            	    

            }


            }

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:718:2: ( 'extends' ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:718:4: 'extends' ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) )
            {
            match(input,16,FOLLOW_16_in_ruleXAttributeType1302); 

                    createLeafNode(grammarAccess.getXAttributeTypeAccess().getExtendsKeyword_2_0(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:722:1: ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:723:1: (lv_baseAttributeType_3_0= ruleAttributeBaseType )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:723:1: (lv_baseAttributeType_3_0= ruleAttributeBaseType )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:724:3: lv_baseAttributeType_3_0= ruleAttributeBaseType
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getXAttributeTypeAccess().getBaseAttributeTypeAttributeBaseTypeParserRuleCall_2_1_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleAttributeBaseType_in_ruleXAttributeType1323);
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

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:746:3: ( 'overrides' ( ( ruleNAME_REFERENCE ) ) )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==24) ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:746:5: 'overrides' ( ( ruleNAME_REFERENCE ) )
                    {
                    match(input,24,FOLLOW_24_in_ruleXAttributeType1335); 

                            createLeafNode(grammarAccess.getXAttributeTypeAccess().getOverridesKeyword_3_0(), null); 
                        
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:750:1: ( ( ruleNAME_REFERENCE ) )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:751:1: ( ruleNAME_REFERENCE )
                    {
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:751:1: ( ruleNAME_REFERENCE )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:752:3: ruleNAME_REFERENCE
                    {

                    			if (current==null) {
                    	            current = factory.create(grammarAccess.getXAttributeTypeRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                            
                     
                    	        currentNode=createCompositeNode(grammarAccess.getXAttributeTypeAccess().getOverrideXAttributeTypeCrossReference_3_1_0(), currentNode); 
                    	    
                    pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleXAttributeType1358);
                    ruleNAME_REFERENCE();
                    _fsp--;

                     
                    	        currentNode = currentNode.getParent();
                    	    

                    }


                    }


                    }
                    break;

            }

            match(input,18,FOLLOW_18_in_ruleXAttributeType1370); 

                    createLeafNode(grammarAccess.getXAttributeTypeAccess().getLeftCurlyBracketKeyword_4(), null); 
                
            match(input,19,FOLLOW_19_in_ruleXAttributeType1380); 

                    createLeafNode(grammarAccess.getXAttributeTypeAccess().getGuidKeyword_5(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:774:1: ( (lv_typeGuid_8_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:775:1: (lv_typeGuid_8_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:775:1: (lv_typeGuid_8_0= RULE_STRING )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:776:3: lv_typeGuid_8_0= RULE_STRING
            {
            lv_typeGuid_8_0=(Token)input.LT(1);
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXAttributeType1397); 

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

            match(input,25,FOLLOW_25_in_ruleXAttributeType1412); 

                    createLeafNode(grammarAccess.getXAttributeTypeAccess().getDataProviderKeyword_7(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:802:1: ( ( (lv_dataProvider_10_1= 'DefaultAttributeDataProvider' | lv_dataProvider_10_2= 'UriAttributeDataProvider' | lv_dataProvider_10_3= ruleQUALIFIED_NAME ) ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:803:1: ( (lv_dataProvider_10_1= 'DefaultAttributeDataProvider' | lv_dataProvider_10_2= 'UriAttributeDataProvider' | lv_dataProvider_10_3= ruleQUALIFIED_NAME ) )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:803:1: ( (lv_dataProvider_10_1= 'DefaultAttributeDataProvider' | lv_dataProvider_10_2= 'UriAttributeDataProvider' | lv_dataProvider_10_3= ruleQUALIFIED_NAME ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:804:1: (lv_dataProvider_10_1= 'DefaultAttributeDataProvider' | lv_dataProvider_10_2= 'UriAttributeDataProvider' | lv_dataProvider_10_3= ruleQUALIFIED_NAME )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:804:1: (lv_dataProvider_10_1= 'DefaultAttributeDataProvider' | lv_dataProvider_10_2= 'UriAttributeDataProvider' | lv_dataProvider_10_3= ruleQUALIFIED_NAME )
            int alt11=3;
            switch ( input.LA(1) ) {
            case 26:
                {
                alt11=1;
                }
                break;
            case 27:
                {
                alt11=2;
                }
                break;
            case RULE_ID:
                {
                alt11=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("804:1: (lv_dataProvider_10_1= 'DefaultAttributeDataProvider' | lv_dataProvider_10_2= 'UriAttributeDataProvider' | lv_dataProvider_10_3= ruleQUALIFIED_NAME )", 11, 0, input);

                throw nvae;
            }

            switch (alt11) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:805:3: lv_dataProvider_10_1= 'DefaultAttributeDataProvider'
                    {
                    lv_dataProvider_10_1=(Token)input.LT(1);
                    match(input,26,FOLLOW_26_in_ruleXAttributeType1432); 

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
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:823:8: lv_dataProvider_10_2= 'UriAttributeDataProvider'
                    {
                    lv_dataProvider_10_2=(Token)input.LT(1);
                    match(input,27,FOLLOW_27_in_ruleXAttributeType1461); 

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
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:841:8: lv_dataProvider_10_3= ruleQUALIFIED_NAME
                    {
                     
                    	        currentNode=createCompositeNode(grammarAccess.getXAttributeTypeAccess().getDataProviderQUALIFIED_NAMEParserRuleCall_8_0_2(), currentNode); 
                    	    
                    pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_ruleXAttributeType1493);
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

            match(input,28,FOLLOW_28_in_ruleXAttributeType1506); 

                    createLeafNode(grammarAccess.getXAttributeTypeAccess().getMinKeyword_9(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:869:1: ( (lv_min_12_0= RULE_WHOLE_NUM_STR ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:870:1: (lv_min_12_0= RULE_WHOLE_NUM_STR )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:870:1: (lv_min_12_0= RULE_WHOLE_NUM_STR )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:871:3: lv_min_12_0= RULE_WHOLE_NUM_STR
            {
            lv_min_12_0=(Token)input.LT(1);
            match(input,RULE_WHOLE_NUM_STR,FOLLOW_RULE_WHOLE_NUM_STR_in_ruleXAttributeType1523); 

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

            match(input,29,FOLLOW_29_in_ruleXAttributeType1538); 

                    createLeafNode(grammarAccess.getXAttributeTypeAccess().getMaxKeyword_11(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:897:1: ( ( (lv_max_14_1= RULE_WHOLE_NUM_STR | lv_max_14_2= 'unlimited' ) ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:898:1: ( (lv_max_14_1= RULE_WHOLE_NUM_STR | lv_max_14_2= 'unlimited' ) )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:898:1: ( (lv_max_14_1= RULE_WHOLE_NUM_STR | lv_max_14_2= 'unlimited' ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:899:1: (lv_max_14_1= RULE_WHOLE_NUM_STR | lv_max_14_2= 'unlimited' )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:899:1: (lv_max_14_1= RULE_WHOLE_NUM_STR | lv_max_14_2= 'unlimited' )
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==RULE_WHOLE_NUM_STR) ) {
                alt12=1;
            }
            else if ( (LA12_0==30) ) {
                alt12=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("899:1: (lv_max_14_1= RULE_WHOLE_NUM_STR | lv_max_14_2= 'unlimited' )", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:900:3: lv_max_14_1= RULE_WHOLE_NUM_STR
                    {
                    lv_max_14_1=(Token)input.LT(1);
                    match(input,RULE_WHOLE_NUM_STR,FOLLOW_RULE_WHOLE_NUM_STR_in_ruleXAttributeType1557); 

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
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:921:8: lv_max_14_2= 'unlimited'
                    {
                    lv_max_14_2=(Token)input.LT(1);
                    match(input,30,FOLLOW_30_in_ruleXAttributeType1578); 

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

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:942:2: ( 'taggerId' ( ( (lv_taggerId_16_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_16_2= ruleQUALIFIED_NAME ) ) ) )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==31) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:942:4: 'taggerId' ( ( (lv_taggerId_16_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_16_2= ruleQUALIFIED_NAME ) ) )
                    {
                    match(input,31,FOLLOW_31_in_ruleXAttributeType1605); 

                            createLeafNode(grammarAccess.getXAttributeTypeAccess().getTaggerIdKeyword_13_0(), null); 
                        
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:946:1: ( ( (lv_taggerId_16_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_16_2= ruleQUALIFIED_NAME ) ) )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:947:1: ( (lv_taggerId_16_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_16_2= ruleQUALIFIED_NAME ) )
                    {
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:947:1: ( (lv_taggerId_16_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_16_2= ruleQUALIFIED_NAME ) )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:948:1: (lv_taggerId_16_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_16_2= ruleQUALIFIED_NAME )
                    {
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:948:1: (lv_taggerId_16_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_16_2= ruleQUALIFIED_NAME )
                    int alt13=2;
                    int LA13_0 = input.LA(1);

                    if ( (LA13_0==32) ) {
                        alt13=1;
                    }
                    else if ( (LA13_0==RULE_ID) ) {
                        alt13=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("948:1: (lv_taggerId_16_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_16_2= ruleQUALIFIED_NAME )", 13, 0, input);

                        throw nvae;
                    }
                    switch (alt13) {
                        case 1 :
                            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:949:3: lv_taggerId_16_1= 'DefaultAttributeTaggerProvider'
                            {
                            lv_taggerId_16_1=(Token)input.LT(1);
                            match(input,32,FOLLOW_32_in_ruleXAttributeType1625); 

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
                            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:967:8: lv_taggerId_16_2= ruleQUALIFIED_NAME
                            {
                             
                            	        currentNode=createCompositeNode(grammarAccess.getXAttributeTypeAccess().getTaggerIdQUALIFIED_NAMEParserRuleCall_13_1_0_1(), currentNode); 
                            	    
                            pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_ruleXAttributeType1657);
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

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:991:4: ( 'enumType' ( ( ruleNAME_REFERENCE ) ) )?
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==33) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:991:6: 'enumType' ( ( ruleNAME_REFERENCE ) )
                    {
                    match(input,33,FOLLOW_33_in_ruleXAttributeType1673); 

                            createLeafNode(grammarAccess.getXAttributeTypeAccess().getEnumTypeKeyword_14_0(), null); 
                        
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:995:1: ( ( ruleNAME_REFERENCE ) )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:996:1: ( ruleNAME_REFERENCE )
                    {
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:996:1: ( ruleNAME_REFERENCE )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:997:3: ruleNAME_REFERENCE
                    {

                    			if (current==null) {
                    	            current = factory.create(grammarAccess.getXAttributeTypeRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                            
                     
                    	        currentNode=createCompositeNode(grammarAccess.getXAttributeTypeAccess().getEnumTypeXOseeEnumTypeCrossReference_14_1_0(), currentNode); 
                    	    
                    pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleXAttributeType1696);
                    ruleNAME_REFERENCE();
                    _fsp--;

                     
                    	        currentNode = currentNode.getParent();
                    	    

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1011:4: ( 'description' ( (lv_description_20_0= RULE_STRING ) ) )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==34) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1011:6: 'description' ( (lv_description_20_0= RULE_STRING ) )
                    {
                    match(input,34,FOLLOW_34_in_ruleXAttributeType1709); 

                            createLeafNode(grammarAccess.getXAttributeTypeAccess().getDescriptionKeyword_15_0(), null); 
                        
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1015:1: ( (lv_description_20_0= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1016:1: (lv_description_20_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1016:1: (lv_description_20_0= RULE_STRING )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1017:3: lv_description_20_0= RULE_STRING
                    {
                    lv_description_20_0=(Token)input.LT(1);
                    match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXAttributeType1726); 

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

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1039:4: ( 'defaultValue' ( (lv_defaultValue_22_0= RULE_STRING ) ) )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==35) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1039:6: 'defaultValue' ( (lv_defaultValue_22_0= RULE_STRING ) )
                    {
                    match(input,35,FOLLOW_35_in_ruleXAttributeType1744); 

                            createLeafNode(grammarAccess.getXAttributeTypeAccess().getDefaultValueKeyword_16_0(), null); 
                        
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1043:1: ( (lv_defaultValue_22_0= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1044:1: (lv_defaultValue_22_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1044:1: (lv_defaultValue_22_0= RULE_STRING )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1045:3: lv_defaultValue_22_0= RULE_STRING
                    {
                    lv_defaultValue_22_0=(Token)input.LT(1);
                    match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXAttributeType1761); 

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

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1067:4: ( 'fileExtension' ( (lv_fileExtension_24_0= RULE_STRING ) ) )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==36) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1067:6: 'fileExtension' ( (lv_fileExtension_24_0= RULE_STRING ) )
                    {
                    match(input,36,FOLLOW_36_in_ruleXAttributeType1779); 

                            createLeafNode(grammarAccess.getXAttributeTypeAccess().getFileExtensionKeyword_17_0(), null); 
                        
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1071:1: ( (lv_fileExtension_24_0= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1072:1: (lv_fileExtension_24_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1072:1: (lv_fileExtension_24_0= RULE_STRING )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1073:3: lv_fileExtension_24_0= RULE_STRING
                    {
                    lv_fileExtension_24_0=(Token)input.LT(1);
                    match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXAttributeType1796); 

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

            match(input,20,FOLLOW_20_in_ruleXAttributeType1813); 

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1107:1: entryRuleAttributeBaseType returns [String current=null] : iv_ruleAttributeBaseType= ruleAttributeBaseType EOF ;
    public final String entryRuleAttributeBaseType() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleAttributeBaseType = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1108:2: (iv_ruleAttributeBaseType= ruleAttributeBaseType EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1109:2: iv_ruleAttributeBaseType= ruleAttributeBaseType EOF
            {
             currentNode = createCompositeNode(grammarAccess.getAttributeBaseTypeRule(), currentNode); 
            pushFollow(FOLLOW_ruleAttributeBaseType_in_entryRuleAttributeBaseType1850);
            iv_ruleAttributeBaseType=ruleAttributeBaseType();
            _fsp--;

             current =iv_ruleAttributeBaseType.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttributeBaseType1861); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1116:1: ruleAttributeBaseType returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'BooleanAttribute' | kw= 'CompressedContentAttribute' | kw= 'DateAttribute' | kw= 'EnumeratedAttribute' | kw= 'FloatingPointAttribute' | kw= 'IntegerAttribute' | kw= 'JavaObjectAttribute' | kw= 'StringAttribute' | kw= 'WordAttribute' | this_QUALIFIED_NAME_9= ruleQUALIFIED_NAME ) ;
    public final AntlrDatatypeRuleToken ruleAttributeBaseType() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        AntlrDatatypeRuleToken this_QUALIFIED_NAME_9 = null;


         setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1121:6: ( (kw= 'BooleanAttribute' | kw= 'CompressedContentAttribute' | kw= 'DateAttribute' | kw= 'EnumeratedAttribute' | kw= 'FloatingPointAttribute' | kw= 'IntegerAttribute' | kw= 'JavaObjectAttribute' | kw= 'StringAttribute' | kw= 'WordAttribute' | this_QUALIFIED_NAME_9= ruleQUALIFIED_NAME ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1122:1: (kw= 'BooleanAttribute' | kw= 'CompressedContentAttribute' | kw= 'DateAttribute' | kw= 'EnumeratedAttribute' | kw= 'FloatingPointAttribute' | kw= 'IntegerAttribute' | kw= 'JavaObjectAttribute' | kw= 'StringAttribute' | kw= 'WordAttribute' | this_QUALIFIED_NAME_9= ruleQUALIFIED_NAME )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1122:1: (kw= 'BooleanAttribute' | kw= 'CompressedContentAttribute' | kw= 'DateAttribute' | kw= 'EnumeratedAttribute' | kw= 'FloatingPointAttribute' | kw= 'IntegerAttribute' | kw= 'JavaObjectAttribute' | kw= 'StringAttribute' | kw= 'WordAttribute' | this_QUALIFIED_NAME_9= ruleQUALIFIED_NAME )
            int alt19=10;
            switch ( input.LA(1) ) {
            case 37:
                {
                alt19=1;
                }
                break;
            case 38:
                {
                alt19=2;
                }
                break;
            case 39:
                {
                alt19=3;
                }
                break;
            case 40:
                {
                alt19=4;
                }
                break;
            case 41:
                {
                alt19=5;
                }
                break;
            case 42:
                {
                alt19=6;
                }
                break;
            case 43:
                {
                alt19=7;
                }
                break;
            case 44:
                {
                alt19=8;
                }
                break;
            case 45:
                {
                alt19=9;
                }
                break;
            case RULE_ID:
                {
                alt19=10;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("1122:1: (kw= 'BooleanAttribute' | kw= 'CompressedContentAttribute' | kw= 'DateAttribute' | kw= 'EnumeratedAttribute' | kw= 'FloatingPointAttribute' | kw= 'IntegerAttribute' | kw= 'JavaObjectAttribute' | kw= 'StringAttribute' | kw= 'WordAttribute' | this_QUALIFIED_NAME_9= ruleQUALIFIED_NAME )", 19, 0, input);

                throw nvae;
            }

            switch (alt19) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1123:2: kw= 'BooleanAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,37,FOLLOW_37_in_ruleAttributeBaseType1899); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getBooleanAttributeKeyword_0(), null); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1130:2: kw= 'CompressedContentAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,38,FOLLOW_38_in_ruleAttributeBaseType1918); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getCompressedContentAttributeKeyword_1(), null); 
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1137:2: kw= 'DateAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,39,FOLLOW_39_in_ruleAttributeBaseType1937); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getDateAttributeKeyword_2(), null); 
                        

                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1144:2: kw= 'EnumeratedAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,40,FOLLOW_40_in_ruleAttributeBaseType1956); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getEnumeratedAttributeKeyword_3(), null); 
                        

                    }
                    break;
                case 5 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1151:2: kw= 'FloatingPointAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,41,FOLLOW_41_in_ruleAttributeBaseType1975); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getFloatingPointAttributeKeyword_4(), null); 
                        

                    }
                    break;
                case 6 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1158:2: kw= 'IntegerAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,42,FOLLOW_42_in_ruleAttributeBaseType1994); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getIntegerAttributeKeyword_5(), null); 
                        

                    }
                    break;
                case 7 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1165:2: kw= 'JavaObjectAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,43,FOLLOW_43_in_ruleAttributeBaseType2013); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getJavaObjectAttributeKeyword_6(), null); 
                        

                    }
                    break;
                case 8 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1172:2: kw= 'StringAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,44,FOLLOW_44_in_ruleAttributeBaseType2032); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getStringAttributeKeyword_7(), null); 
                        

                    }
                    break;
                case 9 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1179:2: kw= 'WordAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,45,FOLLOW_45_in_ruleAttributeBaseType2051); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getWordAttributeKeyword_8(), null); 
                        

                    }
                    break;
                case 10 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1186:5: this_QUALIFIED_NAME_9= ruleQUALIFIED_NAME
                    {
                     
                            currentNode=createCompositeNode(grammarAccess.getAttributeBaseTypeAccess().getQUALIFIED_NAMEParserRuleCall_9(), currentNode); 
                        
                    pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_ruleAttributeBaseType2079);
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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1204:1: entryRuleXOseeEnumType returns [EObject current=null] : iv_ruleXOseeEnumType= ruleXOseeEnumType EOF ;
    public final EObject entryRuleXOseeEnumType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleXOseeEnumType = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1205:2: (iv_ruleXOseeEnumType= ruleXOseeEnumType EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1206:2: iv_ruleXOseeEnumType= ruleXOseeEnumType EOF
            {
             currentNode = createCompositeNode(grammarAccess.getXOseeEnumTypeRule(), currentNode); 
            pushFollow(FOLLOW_ruleXOseeEnumType_in_entryRuleXOseeEnumType2124);
            iv_ruleXOseeEnumType=ruleXOseeEnumType();
            _fsp--;

             current =iv_ruleXOseeEnumType; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleXOseeEnumType2134); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1213:1: ruleXOseeEnumType returns [EObject current=null] : ( 'oseeEnumType' ( (lv_name_1_0= ruleNAME_REFERENCE ) ) '{' 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) ( (lv_enumEntries_5_0= ruleXOseeEnumEntry ) )* '}' ) ;
    public final EObject ruleXOseeEnumType() throws RecognitionException {
        EObject current = null;

        Token lv_typeGuid_4_0=null;
        AntlrDatatypeRuleToken lv_name_1_0 = null;

        EObject lv_enumEntries_5_0 = null;


         @SuppressWarnings("unused") EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1218:6: ( ( 'oseeEnumType' ( (lv_name_1_0= ruleNAME_REFERENCE ) ) '{' 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) ( (lv_enumEntries_5_0= ruleXOseeEnumEntry ) )* '}' ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1219:1: ( 'oseeEnumType' ( (lv_name_1_0= ruleNAME_REFERENCE ) ) '{' 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) ( (lv_enumEntries_5_0= ruleXOseeEnumEntry ) )* '}' )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1219:1: ( 'oseeEnumType' ( (lv_name_1_0= ruleNAME_REFERENCE ) ) '{' 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) ( (lv_enumEntries_5_0= ruleXOseeEnumEntry ) )* '}' )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1219:3: 'oseeEnumType' ( (lv_name_1_0= ruleNAME_REFERENCE ) ) '{' 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) ( (lv_enumEntries_5_0= ruleXOseeEnumEntry ) )* '}'
            {
            match(input,46,FOLLOW_46_in_ruleXOseeEnumType2169); 

                    createLeafNode(grammarAccess.getXOseeEnumTypeAccess().getOseeEnumTypeKeyword_0(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1223:1: ( (lv_name_1_0= ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1224:1: (lv_name_1_0= ruleNAME_REFERENCE )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1224:1: (lv_name_1_0= ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1225:3: lv_name_1_0= ruleNAME_REFERENCE
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getXOseeEnumTypeAccess().getNameNAME_REFERENCEParserRuleCall_1_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleXOseeEnumType2190);
            lv_name_1_0=ruleNAME_REFERENCE();
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
            	        		"NAME_REFERENCE", 
            	        		currentNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	        currentNode = currentNode.getParent();
            	    

            }


            }

            match(input,18,FOLLOW_18_in_ruleXOseeEnumType2200); 

                    createLeafNode(grammarAccess.getXOseeEnumTypeAccess().getLeftCurlyBracketKeyword_2(), null); 
                
            match(input,19,FOLLOW_19_in_ruleXOseeEnumType2210); 

                    createLeafNode(grammarAccess.getXOseeEnumTypeAccess().getGuidKeyword_3(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1255:1: ( (lv_typeGuid_4_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1256:1: (lv_typeGuid_4_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1256:1: (lv_typeGuid_4_0= RULE_STRING )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1257:3: lv_typeGuid_4_0= RULE_STRING
            {
            lv_typeGuid_4_0=(Token)input.LT(1);
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXOseeEnumType2227); 

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

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1279:2: ( (lv_enumEntries_5_0= ruleXOseeEnumEntry ) )*
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( (LA20_0==47) ) {
                    alt20=1;
                }


                switch (alt20) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1280:1: (lv_enumEntries_5_0= ruleXOseeEnumEntry )
            	    {
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1280:1: (lv_enumEntries_5_0= ruleXOseeEnumEntry )
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1281:3: lv_enumEntries_5_0= ruleXOseeEnumEntry
            	    {
            	     
            	    	        currentNode=createCompositeNode(grammarAccess.getXOseeEnumTypeAccess().getEnumEntriesXOseeEnumEntryParserRuleCall_5_0(), currentNode); 
            	    	    
            	    pushFollow(FOLLOW_ruleXOseeEnumEntry_in_ruleXOseeEnumType2253);
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
            	    break loop20;
                }
            } while (true);

            match(input,20,FOLLOW_20_in_ruleXOseeEnumType2264); 

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1315:1: entryRuleXOseeEnumEntry returns [EObject current=null] : iv_ruleXOseeEnumEntry= ruleXOseeEnumEntry EOF ;
    public final EObject entryRuleXOseeEnumEntry() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleXOseeEnumEntry = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1316:2: (iv_ruleXOseeEnumEntry= ruleXOseeEnumEntry EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1317:2: iv_ruleXOseeEnumEntry= ruleXOseeEnumEntry EOF
            {
             currentNode = createCompositeNode(grammarAccess.getXOseeEnumEntryRule(), currentNode); 
            pushFollow(FOLLOW_ruleXOseeEnumEntry_in_entryRuleXOseeEnumEntry2300);
            iv_ruleXOseeEnumEntry=ruleXOseeEnumEntry();
            _fsp--;

             current =iv_ruleXOseeEnumEntry; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleXOseeEnumEntry2310); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1324:1: ruleXOseeEnumEntry returns [EObject current=null] : ( 'entry' ( (lv_name_1_0= ruleNAME_REFERENCE ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? ( 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? ) ;
    public final EObject ruleXOseeEnumEntry() throws RecognitionException {
        EObject current = null;

        Token lv_ordinal_2_0=null;
        Token lv_entryGuid_4_0=null;
        AntlrDatatypeRuleToken lv_name_1_0 = null;


         @SuppressWarnings("unused") EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1329:6: ( ( 'entry' ( (lv_name_1_0= ruleNAME_REFERENCE ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? ( 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1330:1: ( 'entry' ( (lv_name_1_0= ruleNAME_REFERENCE ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? ( 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1330:1: ( 'entry' ( (lv_name_1_0= ruleNAME_REFERENCE ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? ( 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1330:3: 'entry' ( (lv_name_1_0= ruleNAME_REFERENCE ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? ( 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )?
            {
            match(input,47,FOLLOW_47_in_ruleXOseeEnumEntry2345); 

                    createLeafNode(grammarAccess.getXOseeEnumEntryAccess().getEntryKeyword_0(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1334:1: ( (lv_name_1_0= ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1335:1: (lv_name_1_0= ruleNAME_REFERENCE )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1335:1: (lv_name_1_0= ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1336:3: lv_name_1_0= ruleNAME_REFERENCE
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getXOseeEnumEntryAccess().getNameNAME_REFERENCEParserRuleCall_1_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleXOseeEnumEntry2366);
            lv_name_1_0=ruleNAME_REFERENCE();
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
            	        		"NAME_REFERENCE", 
            	        		currentNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	        currentNode = currentNode.getParent();
            	    

            }


            }

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1358:2: ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==RULE_WHOLE_NUM_STR) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1359:1: (lv_ordinal_2_0= RULE_WHOLE_NUM_STR )
                    {
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1359:1: (lv_ordinal_2_0= RULE_WHOLE_NUM_STR )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1360:3: lv_ordinal_2_0= RULE_WHOLE_NUM_STR
                    {
                    lv_ordinal_2_0=(Token)input.LT(1);
                    match(input,RULE_WHOLE_NUM_STR,FOLLOW_RULE_WHOLE_NUM_STR_in_ruleXOseeEnumEntry2383); 

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

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1382:3: ( 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )?
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0==48) ) {
                alt22=1;
            }
            switch (alt22) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1382:5: 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) )
                    {
                    match(input,48,FOLLOW_48_in_ruleXOseeEnumEntry2400); 

                            createLeafNode(grammarAccess.getXOseeEnumEntryAccess().getEntryGuidKeyword_3_0(), null); 
                        
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1386:1: ( (lv_entryGuid_4_0= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1387:1: (lv_entryGuid_4_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1387:1: (lv_entryGuid_4_0= RULE_STRING )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1388:3: lv_entryGuid_4_0= RULE_STRING
                    {
                    lv_entryGuid_4_0=(Token)input.LT(1);
                    match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXOseeEnumEntry2417); 

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1418:1: entryRuleXOseeEnumOverride returns [EObject current=null] : iv_ruleXOseeEnumOverride= ruleXOseeEnumOverride EOF ;
    public final EObject entryRuleXOseeEnumOverride() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleXOseeEnumOverride = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1419:2: (iv_ruleXOseeEnumOverride= ruleXOseeEnumOverride EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1420:2: iv_ruleXOseeEnumOverride= ruleXOseeEnumOverride EOF
            {
             currentNode = createCompositeNode(grammarAccess.getXOseeEnumOverrideRule(), currentNode); 
            pushFollow(FOLLOW_ruleXOseeEnumOverride_in_entryRuleXOseeEnumOverride2460);
            iv_ruleXOseeEnumOverride=ruleXOseeEnumOverride();
            _fsp--;

             current =iv_ruleXOseeEnumOverride; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleXOseeEnumOverride2470); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1427:1: ruleXOseeEnumOverride returns [EObject current=null] : ( 'overrides enum' ( ( ruleNAME_REFERENCE ) ) '{' ( (lv_inheritAll_3_0= 'inheritAll' ) )? ( (lv_overrideOptions_4_0= ruleOverrideOption ) )* '}' ) ;
    public final EObject ruleXOseeEnumOverride() throws RecognitionException {
        EObject current = null;

        Token lv_inheritAll_3_0=null;
        EObject lv_overrideOptions_4_0 = null;


         @SuppressWarnings("unused") EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1432:6: ( ( 'overrides enum' ( ( ruleNAME_REFERENCE ) ) '{' ( (lv_inheritAll_3_0= 'inheritAll' ) )? ( (lv_overrideOptions_4_0= ruleOverrideOption ) )* '}' ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1433:1: ( 'overrides enum' ( ( ruleNAME_REFERENCE ) ) '{' ( (lv_inheritAll_3_0= 'inheritAll' ) )? ( (lv_overrideOptions_4_0= ruleOverrideOption ) )* '}' )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1433:1: ( 'overrides enum' ( ( ruleNAME_REFERENCE ) ) '{' ( (lv_inheritAll_3_0= 'inheritAll' ) )? ( (lv_overrideOptions_4_0= ruleOverrideOption ) )* '}' )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1433:3: 'overrides enum' ( ( ruleNAME_REFERENCE ) ) '{' ( (lv_inheritAll_3_0= 'inheritAll' ) )? ( (lv_overrideOptions_4_0= ruleOverrideOption ) )* '}'
            {
            match(input,49,FOLLOW_49_in_ruleXOseeEnumOverride2505); 

                    createLeafNode(grammarAccess.getXOseeEnumOverrideAccess().getOverridesEnumKeyword_0(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1437:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1438:1: ( ruleNAME_REFERENCE )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1438:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1439:3: ruleNAME_REFERENCE
            {

            			if (current==null) {
            	            current = factory.create(grammarAccess.getXOseeEnumOverrideRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
                    
             
            	        currentNode=createCompositeNode(grammarAccess.getXOseeEnumOverrideAccess().getOverridenEnumTypeXOseeEnumTypeCrossReference_1_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleXOseeEnumOverride2528);
            ruleNAME_REFERENCE();
            _fsp--;

             
            	        currentNode = currentNode.getParent();
            	    

            }


            }

            match(input,18,FOLLOW_18_in_ruleXOseeEnumOverride2538); 

                    createLeafNode(grammarAccess.getXOseeEnumOverrideAccess().getLeftCurlyBracketKeyword_2(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1457:1: ( (lv_inheritAll_3_0= 'inheritAll' ) )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==50) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1458:1: (lv_inheritAll_3_0= 'inheritAll' )
                    {
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1458:1: (lv_inheritAll_3_0= 'inheritAll' )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1459:3: lv_inheritAll_3_0= 'inheritAll'
                    {
                    lv_inheritAll_3_0=(Token)input.LT(1);
                    match(input,50,FOLLOW_50_in_ruleXOseeEnumOverride2556); 

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

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1478:3: ( (lv_overrideOptions_4_0= ruleOverrideOption ) )*
            loop24:
            do {
                int alt24=2;
                int LA24_0 = input.LA(1);

                if ( ((LA24_0>=51 && LA24_0<=52)) ) {
                    alt24=1;
                }


                switch (alt24) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1479:1: (lv_overrideOptions_4_0= ruleOverrideOption )
            	    {
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1479:1: (lv_overrideOptions_4_0= ruleOverrideOption )
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1480:3: lv_overrideOptions_4_0= ruleOverrideOption
            	    {
            	     
            	    	        currentNode=createCompositeNode(grammarAccess.getXOseeEnumOverrideAccess().getOverrideOptionsOverrideOptionParserRuleCall_4_0(), currentNode); 
            	    	    
            	    pushFollow(FOLLOW_ruleOverrideOption_in_ruleXOseeEnumOverride2591);
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
            	    break loop24;
                }
            } while (true);

            match(input,20,FOLLOW_20_in_ruleXOseeEnumOverride2602); 

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1514:1: entryRuleOverrideOption returns [EObject current=null] : iv_ruleOverrideOption= ruleOverrideOption EOF ;
    public final EObject entryRuleOverrideOption() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOverrideOption = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1515:2: (iv_ruleOverrideOption= ruleOverrideOption EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1516:2: iv_ruleOverrideOption= ruleOverrideOption EOF
            {
             currentNode = createCompositeNode(grammarAccess.getOverrideOptionRule(), currentNode); 
            pushFollow(FOLLOW_ruleOverrideOption_in_entryRuleOverrideOption2638);
            iv_ruleOverrideOption=ruleOverrideOption();
            _fsp--;

             current =iv_ruleOverrideOption; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleOverrideOption2648); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1523:1: ruleOverrideOption returns [EObject current=null] : (this_AddEnum_0= ruleAddEnum | this_RemoveEnum_1= ruleRemoveEnum ) ;
    public final EObject ruleOverrideOption() throws RecognitionException {
        EObject current = null;

        EObject this_AddEnum_0 = null;

        EObject this_RemoveEnum_1 = null;


         @SuppressWarnings("unused") EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1528:6: ( (this_AddEnum_0= ruleAddEnum | this_RemoveEnum_1= ruleRemoveEnum ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1529:1: (this_AddEnum_0= ruleAddEnum | this_RemoveEnum_1= ruleRemoveEnum )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1529:1: (this_AddEnum_0= ruleAddEnum | this_RemoveEnum_1= ruleRemoveEnum )
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==51) ) {
                alt25=1;
            }
            else if ( (LA25_0==52) ) {
                alt25=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("1529:1: (this_AddEnum_0= ruleAddEnum | this_RemoveEnum_1= ruleRemoveEnum )", 25, 0, input);

                throw nvae;
            }
            switch (alt25) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1530:5: this_AddEnum_0= ruleAddEnum
                    {
                     
                            currentNode=createCompositeNode(grammarAccess.getOverrideOptionAccess().getAddEnumParserRuleCall_0(), currentNode); 
                        
                    pushFollow(FOLLOW_ruleAddEnum_in_ruleOverrideOption2695);
                    this_AddEnum_0=ruleAddEnum();
                    _fsp--;

                     
                            current = this_AddEnum_0; 
                            currentNode = currentNode.getParent();
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1540:5: this_RemoveEnum_1= ruleRemoveEnum
                    {
                     
                            currentNode=createCompositeNode(grammarAccess.getOverrideOptionAccess().getRemoveEnumParserRuleCall_1(), currentNode); 
                        
                    pushFollow(FOLLOW_ruleRemoveEnum_in_ruleOverrideOption2722);
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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1556:1: entryRuleAddEnum returns [EObject current=null] : iv_ruleAddEnum= ruleAddEnum EOF ;
    public final EObject entryRuleAddEnum() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAddEnum = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1557:2: (iv_ruleAddEnum= ruleAddEnum EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1558:2: iv_ruleAddEnum= ruleAddEnum EOF
            {
             currentNode = createCompositeNode(grammarAccess.getAddEnumRule(), currentNode); 
            pushFollow(FOLLOW_ruleAddEnum_in_entryRuleAddEnum2757);
            iv_ruleAddEnum=ruleAddEnum();
            _fsp--;

             current =iv_ruleAddEnum; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAddEnum2767); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1565:1: ruleAddEnum returns [EObject current=null] : ( 'add' ( (lv_enumEntry_1_0= ruleNAME_REFERENCE ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? ( 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? ) ;
    public final EObject ruleAddEnum() throws RecognitionException {
        EObject current = null;

        Token lv_ordinal_2_0=null;
        Token lv_entryGuid_4_0=null;
        AntlrDatatypeRuleToken lv_enumEntry_1_0 = null;


         @SuppressWarnings("unused") EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1570:6: ( ( 'add' ( (lv_enumEntry_1_0= ruleNAME_REFERENCE ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? ( 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1571:1: ( 'add' ( (lv_enumEntry_1_0= ruleNAME_REFERENCE ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? ( 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1571:1: ( 'add' ( (lv_enumEntry_1_0= ruleNAME_REFERENCE ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? ( 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1571:3: 'add' ( (lv_enumEntry_1_0= ruleNAME_REFERENCE ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? ( 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )?
            {
            match(input,51,FOLLOW_51_in_ruleAddEnum2802); 

                    createLeafNode(grammarAccess.getAddEnumAccess().getAddKeyword_0(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1575:1: ( (lv_enumEntry_1_0= ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1576:1: (lv_enumEntry_1_0= ruleNAME_REFERENCE )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1576:1: (lv_enumEntry_1_0= ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1577:3: lv_enumEntry_1_0= ruleNAME_REFERENCE
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getAddEnumAccess().getEnumEntryNAME_REFERENCEParserRuleCall_1_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleAddEnum2823);
            lv_enumEntry_1_0=ruleNAME_REFERENCE();
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
            	        		"NAME_REFERENCE", 
            	        		currentNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	        currentNode = currentNode.getParent();
            	    

            }


            }

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1599:2: ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )?
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==RULE_WHOLE_NUM_STR) ) {
                alt26=1;
            }
            switch (alt26) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1600:1: (lv_ordinal_2_0= RULE_WHOLE_NUM_STR )
                    {
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1600:1: (lv_ordinal_2_0= RULE_WHOLE_NUM_STR )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1601:3: lv_ordinal_2_0= RULE_WHOLE_NUM_STR
                    {
                    lv_ordinal_2_0=(Token)input.LT(1);
                    match(input,RULE_WHOLE_NUM_STR,FOLLOW_RULE_WHOLE_NUM_STR_in_ruleAddEnum2840); 

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

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1623:3: ( 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )?
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==48) ) {
                alt27=1;
            }
            switch (alt27) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1623:5: 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) )
                    {
                    match(input,48,FOLLOW_48_in_ruleAddEnum2857); 

                            createLeafNode(grammarAccess.getAddEnumAccess().getEntryGuidKeyword_3_0(), null); 
                        
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1627:1: ( (lv_entryGuid_4_0= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1628:1: (lv_entryGuid_4_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1628:1: (lv_entryGuid_4_0= RULE_STRING )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1629:3: lv_entryGuid_4_0= RULE_STRING
                    {
                    lv_entryGuid_4_0=(Token)input.LT(1);
                    match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAddEnum2874); 

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1659:1: entryRuleRemoveEnum returns [EObject current=null] : iv_ruleRemoveEnum= ruleRemoveEnum EOF ;
    public final EObject entryRuleRemoveEnum() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRemoveEnum = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1660:2: (iv_ruleRemoveEnum= ruleRemoveEnum EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1661:2: iv_ruleRemoveEnum= ruleRemoveEnum EOF
            {
             currentNode = createCompositeNode(grammarAccess.getRemoveEnumRule(), currentNode); 
            pushFollow(FOLLOW_ruleRemoveEnum_in_entryRuleRemoveEnum2917);
            iv_ruleRemoveEnum=ruleRemoveEnum();
            _fsp--;

             current =iv_ruleRemoveEnum; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleRemoveEnum2927); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1668:1: ruleRemoveEnum returns [EObject current=null] : ( 'remove' ( ( ruleNAME_REFERENCE ) ) ) ;
    public final EObject ruleRemoveEnum() throws RecognitionException {
        EObject current = null;

         @SuppressWarnings("unused") EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1673:6: ( ( 'remove' ( ( ruleNAME_REFERENCE ) ) ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1674:1: ( 'remove' ( ( ruleNAME_REFERENCE ) ) )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1674:1: ( 'remove' ( ( ruleNAME_REFERENCE ) ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1674:3: 'remove' ( ( ruleNAME_REFERENCE ) )
            {
            match(input,52,FOLLOW_52_in_ruleRemoveEnum2962); 

                    createLeafNode(grammarAccess.getRemoveEnumAccess().getRemoveKeyword_0(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1678:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1679:1: ( ruleNAME_REFERENCE )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1679:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1680:3: ruleNAME_REFERENCE
            {

            			if (current==null) {
            	            current = factory.create(grammarAccess.getRemoveEnumRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
                    
             
            	        currentNode=createCompositeNode(grammarAccess.getRemoveEnumAccess().getEnumEntryXOseeEnumEntryCrossReference_1_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleRemoveEnum2985);
            ruleNAME_REFERENCE();
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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1702:1: entryRuleXRelationType returns [EObject current=null] : iv_ruleXRelationType= ruleXRelationType EOF ;
    public final EObject entryRuleXRelationType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleXRelationType = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1703:2: (iv_ruleXRelationType= ruleXRelationType EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1704:2: iv_ruleXRelationType= ruleXRelationType EOF
            {
             currentNode = createCompositeNode(grammarAccess.getXRelationTypeRule(), currentNode); 
            pushFollow(FOLLOW_ruleXRelationType_in_entryRuleXRelationType3021);
            iv_ruleXRelationType=ruleXRelationType();
            _fsp--;

             current =iv_ruleXRelationType; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleXRelationType3031); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1711:1: ruleXRelationType returns [EObject current=null] : ( 'relationType' ( (lv_name_1_0= ruleNAME_REFERENCE ) ) '{' 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) 'sideAName' ( (lv_sideAName_6_0= RULE_STRING ) ) 'sideAArtifactType' ( ( ruleNAME_REFERENCE ) ) 'sideBName' ( (lv_sideBName_10_0= RULE_STRING ) ) 'sideBArtifactType' ( ( ruleNAME_REFERENCE ) ) 'defaultOrderType' ( (lv_defaultOrderType_14_0= ruleRelationOrderType ) ) 'multiplicity' ( (lv_multiplicity_16_0= ruleRelationMultiplicityEnum ) ) '}' ) ;
    public final EObject ruleXRelationType() throws RecognitionException {
        EObject current = null;

        Token lv_typeGuid_4_0=null;
        Token lv_sideAName_6_0=null;
        Token lv_sideBName_10_0=null;
        AntlrDatatypeRuleToken lv_name_1_0 = null;

        AntlrDatatypeRuleToken lv_defaultOrderType_14_0 = null;

        Enumerator lv_multiplicity_16_0 = null;


         @SuppressWarnings("unused") EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1716:6: ( ( 'relationType' ( (lv_name_1_0= ruleNAME_REFERENCE ) ) '{' 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) 'sideAName' ( (lv_sideAName_6_0= RULE_STRING ) ) 'sideAArtifactType' ( ( ruleNAME_REFERENCE ) ) 'sideBName' ( (lv_sideBName_10_0= RULE_STRING ) ) 'sideBArtifactType' ( ( ruleNAME_REFERENCE ) ) 'defaultOrderType' ( (lv_defaultOrderType_14_0= ruleRelationOrderType ) ) 'multiplicity' ( (lv_multiplicity_16_0= ruleRelationMultiplicityEnum ) ) '}' ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1717:1: ( 'relationType' ( (lv_name_1_0= ruleNAME_REFERENCE ) ) '{' 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) 'sideAName' ( (lv_sideAName_6_0= RULE_STRING ) ) 'sideAArtifactType' ( ( ruleNAME_REFERENCE ) ) 'sideBName' ( (lv_sideBName_10_0= RULE_STRING ) ) 'sideBArtifactType' ( ( ruleNAME_REFERENCE ) ) 'defaultOrderType' ( (lv_defaultOrderType_14_0= ruleRelationOrderType ) ) 'multiplicity' ( (lv_multiplicity_16_0= ruleRelationMultiplicityEnum ) ) '}' )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1717:1: ( 'relationType' ( (lv_name_1_0= ruleNAME_REFERENCE ) ) '{' 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) 'sideAName' ( (lv_sideAName_6_0= RULE_STRING ) ) 'sideAArtifactType' ( ( ruleNAME_REFERENCE ) ) 'sideBName' ( (lv_sideBName_10_0= RULE_STRING ) ) 'sideBArtifactType' ( ( ruleNAME_REFERENCE ) ) 'defaultOrderType' ( (lv_defaultOrderType_14_0= ruleRelationOrderType ) ) 'multiplicity' ( (lv_multiplicity_16_0= ruleRelationMultiplicityEnum ) ) '}' )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1717:3: 'relationType' ( (lv_name_1_0= ruleNAME_REFERENCE ) ) '{' 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) 'sideAName' ( (lv_sideAName_6_0= RULE_STRING ) ) 'sideAArtifactType' ( ( ruleNAME_REFERENCE ) ) 'sideBName' ( (lv_sideBName_10_0= RULE_STRING ) ) 'sideBArtifactType' ( ( ruleNAME_REFERENCE ) ) 'defaultOrderType' ( (lv_defaultOrderType_14_0= ruleRelationOrderType ) ) 'multiplicity' ( (lv_multiplicity_16_0= ruleRelationMultiplicityEnum ) ) '}'
            {
            match(input,53,FOLLOW_53_in_ruleXRelationType3066); 

                    createLeafNode(grammarAccess.getXRelationTypeAccess().getRelationTypeKeyword_0(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1721:1: ( (lv_name_1_0= ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1722:1: (lv_name_1_0= ruleNAME_REFERENCE )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1722:1: (lv_name_1_0= ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1723:3: lv_name_1_0= ruleNAME_REFERENCE
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getXRelationTypeAccess().getNameNAME_REFERENCEParserRuleCall_1_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleXRelationType3087);
            lv_name_1_0=ruleNAME_REFERENCE();
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
            	        		"NAME_REFERENCE", 
            	        		currentNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	        currentNode = currentNode.getParent();
            	    

            }


            }

            match(input,18,FOLLOW_18_in_ruleXRelationType3097); 

                    createLeafNode(grammarAccess.getXRelationTypeAccess().getLeftCurlyBracketKeyword_2(), null); 
                
            match(input,19,FOLLOW_19_in_ruleXRelationType3107); 

                    createLeafNode(grammarAccess.getXRelationTypeAccess().getGuidKeyword_3(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1753:1: ( (lv_typeGuid_4_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1754:1: (lv_typeGuid_4_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1754:1: (lv_typeGuid_4_0= RULE_STRING )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1755:3: lv_typeGuid_4_0= RULE_STRING
            {
            lv_typeGuid_4_0=(Token)input.LT(1);
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXRelationType3124); 

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

            match(input,54,FOLLOW_54_in_ruleXRelationType3139); 

                    createLeafNode(grammarAccess.getXRelationTypeAccess().getSideANameKeyword_5(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1781:1: ( (lv_sideAName_6_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1782:1: (lv_sideAName_6_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1782:1: (lv_sideAName_6_0= RULE_STRING )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1783:3: lv_sideAName_6_0= RULE_STRING
            {
            lv_sideAName_6_0=(Token)input.LT(1);
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXRelationType3156); 

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

            match(input,55,FOLLOW_55_in_ruleXRelationType3171); 

                    createLeafNode(grammarAccess.getXRelationTypeAccess().getSideAArtifactTypeKeyword_7(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1809:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1810:1: ( ruleNAME_REFERENCE )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1810:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1811:3: ruleNAME_REFERENCE
            {

            			if (current==null) {
            	            current = factory.create(grammarAccess.getXRelationTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
                    
             
            	        currentNode=createCompositeNode(grammarAccess.getXRelationTypeAccess().getSideAArtifactTypeXArtifactTypeCrossReference_8_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleXRelationType3194);
            ruleNAME_REFERENCE();
            _fsp--;

             
            	        currentNode = currentNode.getParent();
            	    

            }


            }

            match(input,56,FOLLOW_56_in_ruleXRelationType3204); 

                    createLeafNode(grammarAccess.getXRelationTypeAccess().getSideBNameKeyword_9(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1829:1: ( (lv_sideBName_10_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1830:1: (lv_sideBName_10_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1830:1: (lv_sideBName_10_0= RULE_STRING )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1831:3: lv_sideBName_10_0= RULE_STRING
            {
            lv_sideBName_10_0=(Token)input.LT(1);
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleXRelationType3221); 

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

            match(input,57,FOLLOW_57_in_ruleXRelationType3236); 

                    createLeafNode(grammarAccess.getXRelationTypeAccess().getSideBArtifactTypeKeyword_11(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1857:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1858:1: ( ruleNAME_REFERENCE )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1858:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1859:3: ruleNAME_REFERENCE
            {

            			if (current==null) {
            	            current = factory.create(grammarAccess.getXRelationTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
                    
             
            	        currentNode=createCompositeNode(grammarAccess.getXRelationTypeAccess().getSideBArtifactTypeXArtifactTypeCrossReference_12_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleXRelationType3259);
            ruleNAME_REFERENCE();
            _fsp--;

             
            	        currentNode = currentNode.getParent();
            	    

            }


            }

            match(input,58,FOLLOW_58_in_ruleXRelationType3269); 

                    createLeafNode(grammarAccess.getXRelationTypeAccess().getDefaultOrderTypeKeyword_13(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1877:1: ( (lv_defaultOrderType_14_0= ruleRelationOrderType ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1878:1: (lv_defaultOrderType_14_0= ruleRelationOrderType )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1878:1: (lv_defaultOrderType_14_0= ruleRelationOrderType )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1879:3: lv_defaultOrderType_14_0= ruleRelationOrderType
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getXRelationTypeAccess().getDefaultOrderTypeRelationOrderTypeParserRuleCall_14_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleRelationOrderType_in_ruleXRelationType3290);
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

            match(input,59,FOLLOW_59_in_ruleXRelationType3300); 

                    createLeafNode(grammarAccess.getXRelationTypeAccess().getMultiplicityKeyword_15(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1905:1: ( (lv_multiplicity_16_0= ruleRelationMultiplicityEnum ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1906:1: (lv_multiplicity_16_0= ruleRelationMultiplicityEnum )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1906:1: (lv_multiplicity_16_0= ruleRelationMultiplicityEnum )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1907:3: lv_multiplicity_16_0= ruleRelationMultiplicityEnum
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getXRelationTypeAccess().getMultiplicityRelationMultiplicityEnumEnumRuleCall_16_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleRelationMultiplicityEnum_in_ruleXRelationType3321);
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

            match(input,20,FOLLOW_20_in_ruleXRelationType3331); 

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1941:1: entryRuleRelationOrderType returns [String current=null] : iv_ruleRelationOrderType= ruleRelationOrderType EOF ;
    public final String entryRuleRelationOrderType() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleRelationOrderType = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1942:2: (iv_ruleRelationOrderType= ruleRelationOrderType EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1943:2: iv_ruleRelationOrderType= ruleRelationOrderType EOF
            {
             currentNode = createCompositeNode(grammarAccess.getRelationOrderTypeRule(), currentNode); 
            pushFollow(FOLLOW_ruleRelationOrderType_in_entryRuleRelationOrderType3368);
            iv_ruleRelationOrderType=ruleRelationOrderType();
            _fsp--;

             current =iv_ruleRelationOrderType.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleRelationOrderType3379); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1950:1: ruleRelationOrderType returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'Lexicographical_Ascending' | kw= 'Lexicographical_Descending' | kw= 'Unordered' | this_ID_3= RULE_ID ) ;
    public final AntlrDatatypeRuleToken ruleRelationOrderType() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        Token this_ID_3=null;

         setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1955:6: ( (kw= 'Lexicographical_Ascending' | kw= 'Lexicographical_Descending' | kw= 'Unordered' | this_ID_3= RULE_ID ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1956:1: (kw= 'Lexicographical_Ascending' | kw= 'Lexicographical_Descending' | kw= 'Unordered' | this_ID_3= RULE_ID )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1956:1: (kw= 'Lexicographical_Ascending' | kw= 'Lexicographical_Descending' | kw= 'Unordered' | this_ID_3= RULE_ID )
            int alt28=4;
            switch ( input.LA(1) ) {
            case 60:
                {
                alt28=1;
                }
                break;
            case 61:
                {
                alt28=2;
                }
                break;
            case 62:
                {
                alt28=3;
                }
                break;
            case RULE_ID:
                {
                alt28=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("1956:1: (kw= 'Lexicographical_Ascending' | kw= 'Lexicographical_Descending' | kw= 'Unordered' | this_ID_3= RULE_ID )", 28, 0, input);

                throw nvae;
            }

            switch (alt28) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1957:2: kw= 'Lexicographical_Ascending'
                    {
                    kw=(Token)input.LT(1);
                    match(input,60,FOLLOW_60_in_ruleRelationOrderType3417); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getRelationOrderTypeAccess().getLexicographical_AscendingKeyword_0(), null); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1964:2: kw= 'Lexicographical_Descending'
                    {
                    kw=(Token)input.LT(1);
                    match(input,61,FOLLOW_61_in_ruleRelationOrderType3436); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getRelationOrderTypeAccess().getLexicographical_DescendingKeyword_1(), null); 
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1971:2: kw= 'Unordered'
                    {
                    kw=(Token)input.LT(1);
                    match(input,62,FOLLOW_62_in_ruleRelationOrderType3455); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getRelationOrderTypeAccess().getUnorderedKeyword_2(), null); 
                        

                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1977:10: this_ID_3= RULE_ID
                    {
                    this_ID_3=(Token)input.LT(1);
                    match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleRelationOrderType3476); 

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


    // $ANTLR start ruleRelationMultiplicityEnum
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1992:1: ruleRelationMultiplicityEnum returns [Enumerator current=null] : ( ( 'ONE_TO_ONE' ) | ( 'ONE_TO_MANY' ) | ( 'MANY_TO_ONE' ) | ( 'MANY_TO_MANY' ) ) ;
    public final Enumerator ruleRelationMultiplicityEnum() throws RecognitionException {
        Enumerator current = null;

         setCurrentLookahead(); resetLookahead(); 
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1996:6: ( ( ( 'ONE_TO_ONE' ) | ( 'ONE_TO_MANY' ) | ( 'MANY_TO_ONE' ) | ( 'MANY_TO_MANY' ) ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1997:1: ( ( 'ONE_TO_ONE' ) | ( 'ONE_TO_MANY' ) | ( 'MANY_TO_ONE' ) | ( 'MANY_TO_MANY' ) )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1997:1: ( ( 'ONE_TO_ONE' ) | ( 'ONE_TO_MANY' ) | ( 'MANY_TO_ONE' ) | ( 'MANY_TO_MANY' ) )
            int alt29=4;
            switch ( input.LA(1) ) {
            case 63:
                {
                alt29=1;
                }
                break;
            case 64:
                {
                alt29=2;
                }
                break;
            case 65:
                {
                alt29=3;
                }
                break;
            case 66:
                {
                alt29=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("1997:1: ( ( 'ONE_TO_ONE' ) | ( 'ONE_TO_MANY' ) | ( 'MANY_TO_ONE' ) | ( 'MANY_TO_MANY' ) )", 29, 0, input);

                throw nvae;
            }

            switch (alt29) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1997:2: ( 'ONE_TO_ONE' )
                    {
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1997:2: ( 'ONE_TO_ONE' )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1997:4: 'ONE_TO_ONE'
                    {
                    match(input,63,FOLLOW_63_in_ruleRelationMultiplicityEnum3533); 

                            current = grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_ONEEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            createLeafNode(grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_ONEEnumLiteralDeclaration_0(), null); 
                        

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:2003:6: ( 'ONE_TO_MANY' )
                    {
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:2003:6: ( 'ONE_TO_MANY' )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:2003:8: 'ONE_TO_MANY'
                    {
                    match(input,64,FOLLOW_64_in_ruleRelationMultiplicityEnum3548); 

                            current = grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_MANYEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                            createLeafNode(grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_MANYEnumLiteralDeclaration_1(), null); 
                        

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:2009:6: ( 'MANY_TO_ONE' )
                    {
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:2009:6: ( 'MANY_TO_ONE' )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:2009:8: 'MANY_TO_ONE'
                    {
                    match(input,65,FOLLOW_65_in_ruleRelationMultiplicityEnum3563); 

                            current = grammarAccess.getRelationMultiplicityEnumAccess().getMANY_TO_ONEEnumLiteralDeclaration_2().getEnumLiteral().getInstance();
                            createLeafNode(grammarAccess.getRelationMultiplicityEnumAccess().getMANY_TO_ONEEnumLiteralDeclaration_2(), null); 
                        

                    }


                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:2015:6: ( 'MANY_TO_MANY' )
                    {
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:2015:6: ( 'MANY_TO_MANY' )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:2015:8: 'MANY_TO_MANY'
                    {
                    match(input,66,FOLLOW_66_in_ruleRelationMultiplicityEnum3578); 

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


 

    public static final BitSet FOLLOW_ruleOseeTypeModel_in_entryRuleOseeTypeModel75 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleOseeTypeModel85 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleImport_in_ruleOseeTypeModel131 = new BitSet(new long[]{0x002240000080D002L});
    public static final BitSet FOLLOW_ruleXArtifactType_in_ruleOseeTypeModel154 = new BitSet(new long[]{0x002240000080C002L});
    public static final BitSet FOLLOW_ruleXRelationType_in_ruleOseeTypeModel181 = new BitSet(new long[]{0x002240000080C002L});
    public static final BitSet FOLLOW_ruleXAttributeType_in_ruleOseeTypeModel208 = new BitSet(new long[]{0x002240000080C002L});
    public static final BitSet FOLLOW_ruleXOseeEnumType_in_ruleOseeTypeModel235 = new BitSet(new long[]{0x002240000080C002L});
    public static final BitSet FOLLOW_ruleXOseeEnumOverride_in_ruleOseeTypeModel262 = new BitSet(new long[]{0x002240000080C002L});
    public static final BitSet FOLLOW_ruleImport_in_entryRuleImport300 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleImport310 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_12_in_ruleImport345 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleImport362 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_entryRuleNAME_REFERENCE404 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleNAME_REFERENCE415 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleNAME_REFERENCE454 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_entryRuleQUALIFIED_NAME499 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleQUALIFIED_NAME510 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleQUALIFIED_NAME550 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_13_in_ruleQUALIFIED_NAME569 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleQUALIFIED_NAME584 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_ruleOseeType_in_entryRuleOseeType633 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleOseeType643 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXArtifactType_in_ruleOseeType690 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXRelationType_in_ruleOseeType717 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXAttributeType_in_ruleOseeType744 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXOseeEnumType_in_ruleOseeType771 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXArtifactType_in_entryRuleXArtifactType806 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleXArtifactType816 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_14_in_ruleXArtifactType859 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_ruleXArtifactType883 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleXArtifactType904 = new BitSet(new long[]{0x0000000000050000L});
    public static final BitSet FOLLOW_16_in_ruleXArtifactType915 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleXArtifactType938 = new BitSet(new long[]{0x0000000000060000L});
    public static final BitSet FOLLOW_17_in_ruleXArtifactType949 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleXArtifactType972 = new BitSet(new long[]{0x0000000000060000L});
    public static final BitSet FOLLOW_18_in_ruleXArtifactType986 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_ruleXArtifactType996 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXArtifactType1013 = new BitSet(new long[]{0x0000000000300000L});
    public static final BitSet FOLLOW_ruleXAttributeTypeRef_in_ruleXArtifactType1039 = new BitSet(new long[]{0x0000000000300000L});
    public static final BitSet FOLLOW_20_in_ruleXArtifactType1050 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXAttributeTypeRef_in_entryRuleXAttributeTypeRef1086 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleXAttributeTypeRef1096 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_21_in_ruleXAttributeTypeRef1131 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleXAttributeTypeRef1154 = new BitSet(new long[]{0x0000000000400002L});
    public static final BitSet FOLLOW_22_in_ruleXAttributeTypeRef1165 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXAttributeTypeRef1182 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXAttributeType_in_entryRuleXAttributeType1225 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleXAttributeType1235 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_23_in_ruleXAttributeType1270 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleXAttributeType1291 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_16_in_ruleXAttributeType1302 = new BitSet(new long[]{0x00003FE000000020L});
    public static final BitSet FOLLOW_ruleAttributeBaseType_in_ruleXAttributeType1323 = new BitSet(new long[]{0x0000000001040000L});
    public static final BitSet FOLLOW_24_in_ruleXAttributeType1335 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleXAttributeType1358 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_18_in_ruleXAttributeType1370 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_ruleXAttributeType1380 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXAttributeType1397 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_ruleXAttributeType1412 = new BitSet(new long[]{0x000000000C000020L});
    public static final BitSet FOLLOW_26_in_ruleXAttributeType1432 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_27_in_ruleXAttributeType1461 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_ruleXAttributeType1493 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_28_in_ruleXAttributeType1506 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RULE_WHOLE_NUM_STR_in_ruleXAttributeType1523 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_ruleXAttributeType1538 = new BitSet(new long[]{0x0000000040000040L});
    public static final BitSet FOLLOW_RULE_WHOLE_NUM_STR_in_ruleXAttributeType1557 = new BitSet(new long[]{0x0000001E80100000L});
    public static final BitSet FOLLOW_30_in_ruleXAttributeType1578 = new BitSet(new long[]{0x0000001E80100000L});
    public static final BitSet FOLLOW_31_in_ruleXAttributeType1605 = new BitSet(new long[]{0x0000000100000020L});
    public static final BitSet FOLLOW_32_in_ruleXAttributeType1625 = new BitSet(new long[]{0x0000001E00100000L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_ruleXAttributeType1657 = new BitSet(new long[]{0x0000001E00100000L});
    public static final BitSet FOLLOW_33_in_ruleXAttributeType1673 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleXAttributeType1696 = new BitSet(new long[]{0x0000001C00100000L});
    public static final BitSet FOLLOW_34_in_ruleXAttributeType1709 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXAttributeType1726 = new BitSet(new long[]{0x0000001800100000L});
    public static final BitSet FOLLOW_35_in_ruleXAttributeType1744 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXAttributeType1761 = new BitSet(new long[]{0x0000001000100000L});
    public static final BitSet FOLLOW_36_in_ruleXAttributeType1779 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXAttributeType1796 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_ruleXAttributeType1813 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeBaseType_in_entryRuleAttributeBaseType1850 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttributeBaseType1861 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_ruleAttributeBaseType1899 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_38_in_ruleAttributeBaseType1918 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_39_in_ruleAttributeBaseType1937 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_ruleAttributeBaseType1956 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_ruleAttributeBaseType1975 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_ruleAttributeBaseType1994 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_43_in_ruleAttributeBaseType2013 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_ruleAttributeBaseType2032 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_45_in_ruleAttributeBaseType2051 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_ruleAttributeBaseType2079 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXOseeEnumType_in_entryRuleXOseeEnumType2124 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleXOseeEnumType2134 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_46_in_ruleXOseeEnumType2169 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleXOseeEnumType2190 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_18_in_ruleXOseeEnumType2200 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_ruleXOseeEnumType2210 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXOseeEnumType2227 = new BitSet(new long[]{0x0000800000100000L});
    public static final BitSet FOLLOW_ruleXOseeEnumEntry_in_ruleXOseeEnumType2253 = new BitSet(new long[]{0x0000800000100000L});
    public static final BitSet FOLLOW_20_in_ruleXOseeEnumType2264 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXOseeEnumEntry_in_entryRuleXOseeEnumEntry2300 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleXOseeEnumEntry2310 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_ruleXOseeEnumEntry2345 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleXOseeEnumEntry2366 = new BitSet(new long[]{0x0001000000000042L});
    public static final BitSet FOLLOW_RULE_WHOLE_NUM_STR_in_ruleXOseeEnumEntry2383 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_48_in_ruleXOseeEnumEntry2400 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXOseeEnumEntry2417 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXOseeEnumOverride_in_entryRuleXOseeEnumOverride2460 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleXOseeEnumOverride2470 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_49_in_ruleXOseeEnumOverride2505 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleXOseeEnumOverride2528 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_18_in_ruleXOseeEnumOverride2538 = new BitSet(new long[]{0x001C000000100000L});
    public static final BitSet FOLLOW_50_in_ruleXOseeEnumOverride2556 = new BitSet(new long[]{0x0018000000100000L});
    public static final BitSet FOLLOW_ruleOverrideOption_in_ruleXOseeEnumOverride2591 = new BitSet(new long[]{0x0018000000100000L});
    public static final BitSet FOLLOW_20_in_ruleXOseeEnumOverride2602 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOverrideOption_in_entryRuleOverrideOption2638 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleOverrideOption2648 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAddEnum_in_ruleOverrideOption2695 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRemoveEnum_in_ruleOverrideOption2722 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAddEnum_in_entryRuleAddEnum2757 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAddEnum2767 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_51_in_ruleAddEnum2802 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleAddEnum2823 = new BitSet(new long[]{0x0001000000000042L});
    public static final BitSet FOLLOW_RULE_WHOLE_NUM_STR_in_ruleAddEnum2840 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_48_in_ruleAddEnum2857 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAddEnum2874 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRemoveEnum_in_entryRuleRemoveEnum2917 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRemoveEnum2927 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_52_in_ruleRemoveEnum2962 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleRemoveEnum2985 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXRelationType_in_entryRuleXRelationType3021 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleXRelationType3031 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_53_in_ruleXRelationType3066 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleXRelationType3087 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_18_in_ruleXRelationType3097 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_ruleXRelationType3107 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXRelationType3124 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_54_in_ruleXRelationType3139 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXRelationType3156 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_55_in_ruleXRelationType3171 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleXRelationType3194 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_56_in_ruleXRelationType3204 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleXRelationType3221 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_57_in_ruleXRelationType3236 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleXRelationType3259 = new BitSet(new long[]{0x0400000000000000L});
    public static final BitSet FOLLOW_58_in_ruleXRelationType3269 = new BitSet(new long[]{0x7000000000000020L});
    public static final BitSet FOLLOW_ruleRelationOrderType_in_ruleXRelationType3290 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_59_in_ruleXRelationType3300 = new BitSet(new long[]{0x8000000000000000L,0x0000000000000007L});
    public static final BitSet FOLLOW_ruleRelationMultiplicityEnum_in_ruleXRelationType3321 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_ruleXRelationType3331 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelationOrderType_in_entryRuleRelationOrderType3368 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRelationOrderType3379 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_60_in_ruleRelationOrderType3417 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_61_in_ruleRelationOrderType3436 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_62_in_ruleRelationOrderType3455 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleRelationOrderType3476 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_63_in_ruleRelationMultiplicityEnum3533 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_64_in_ruleRelationMultiplicityEnum3548 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_ruleRelationMultiplicityEnum3563 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_66_in_ruleRelationMultiplicityEnum3578 = new BitSet(new long[]{0x0000000000000002L});

}