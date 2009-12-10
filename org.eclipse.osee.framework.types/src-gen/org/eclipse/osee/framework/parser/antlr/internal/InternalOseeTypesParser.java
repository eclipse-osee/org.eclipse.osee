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
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:73:55: (iv_ruleOseeTypeModel= ruleOseeTypeModel EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:74:2: iv_ruleOseeTypeModel= ruleOseeTypeModel EOF
            {
             currentNode = createCompositeNode(grammarAccess.getOseeTypeModelRule(), currentNode); 
            pushFollow(FOLLOW_ruleOseeTypeModel_in_entryRuleOseeTypeModel73);
            iv_ruleOseeTypeModel=ruleOseeTypeModel();
            _fsp--;

             current =iv_ruleOseeTypeModel; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleOseeTypeModel83); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:81:1: ruleOseeTypeModel returns [EObject current=null] : ( ( (lv_imports_0_0= ruleImport ) )* ( ( (lv_artifactTypes_1_0= ruleArtifactType ) ) | ( (lv_relationTypes_2_0= ruleRelationType ) ) | ( (lv_attributeTypes_3_0= ruleAttributeType ) ) | ( (lv_enumTypes_4_0= ruleOseeEnumType ) ) | ( (lv_enumOverrides_5_0= ruleOseeEnumOverride ) ) )* ) ;
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
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:86:6: ( ( ( (lv_imports_0_0= ruleImport ) )* ( ( (lv_artifactTypes_1_0= ruleArtifactType ) ) | ( (lv_relationTypes_2_0= ruleRelationType ) ) | ( (lv_attributeTypes_3_0= ruleAttributeType ) ) | ( (lv_enumTypes_4_0= ruleOseeEnumType ) ) | ( (lv_enumOverrides_5_0= ruleOseeEnumOverride ) ) )* ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:87:1: ( ( (lv_imports_0_0= ruleImport ) )* ( ( (lv_artifactTypes_1_0= ruleArtifactType ) ) | ( (lv_relationTypes_2_0= ruleRelationType ) ) | ( (lv_attributeTypes_3_0= ruleAttributeType ) ) | ( (lv_enumTypes_4_0= ruleOseeEnumType ) ) | ( (lv_enumOverrides_5_0= ruleOseeEnumOverride ) ) )* )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:87:1: ( ( (lv_imports_0_0= ruleImport ) )* ( ( (lv_artifactTypes_1_0= ruleArtifactType ) ) | ( (lv_relationTypes_2_0= ruleRelationType ) ) | ( (lv_attributeTypes_3_0= ruleAttributeType ) ) | ( (lv_enumTypes_4_0= ruleOseeEnumType ) ) | ( (lv_enumOverrides_5_0= ruleOseeEnumOverride ) ) )* )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:87:2: ( (lv_imports_0_0= ruleImport ) )* ( ( (lv_artifactTypes_1_0= ruleArtifactType ) ) | ( (lv_relationTypes_2_0= ruleRelationType ) ) | ( (lv_attributeTypes_3_0= ruleAttributeType ) ) | ( (lv_enumTypes_4_0= ruleOseeEnumType ) ) | ( (lv_enumOverrides_5_0= ruleOseeEnumOverride ) ) )*
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:87:2: ( (lv_imports_0_0= ruleImport ) )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==12) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:88:1: (lv_imports_0_0= ruleImport )
            	    {
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:88:1: (lv_imports_0_0= ruleImport )
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:89:3: lv_imports_0_0= ruleImport
            	    {
            	     
            	    	        currentNode=createCompositeNode(grammarAccess.getOseeTypeModelAccess().getImportsImportParserRuleCall_0_0(), currentNode); 
            	    	    
            	    pushFollow(FOLLOW_ruleImport_in_ruleOseeTypeModel129);
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

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:111:3: ( ( (lv_artifactTypes_1_0= ruleArtifactType ) ) | ( (lv_relationTypes_2_0= ruleRelationType ) ) | ( (lv_attributeTypes_3_0= ruleAttributeType ) ) | ( (lv_enumTypes_4_0= ruleOseeEnumType ) ) | ( (lv_enumOverrides_5_0= ruleOseeEnumOverride ) ) )*
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
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:111:4: ( (lv_artifactTypes_1_0= ruleArtifactType ) )
            	    {
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:111:4: ( (lv_artifactTypes_1_0= ruleArtifactType ) )
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:112:1: (lv_artifactTypes_1_0= ruleArtifactType )
            	    {
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:112:1: (lv_artifactTypes_1_0= ruleArtifactType )
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:113:3: lv_artifactTypes_1_0= ruleArtifactType
            	    {
            	     
            	    	        currentNode=createCompositeNode(grammarAccess.getOseeTypeModelAccess().getArtifactTypesArtifactTypeParserRuleCall_1_0_0(), currentNode); 
            	    	    
            	    pushFollow(FOLLOW_ruleArtifactType_in_ruleOseeTypeModel152);
            	    lv_artifactTypes_1_0=ruleArtifactType();
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
            	    	        		"ArtifactType", 
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
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:136:6: ( (lv_relationTypes_2_0= ruleRelationType ) )
            	    {
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:136:6: ( (lv_relationTypes_2_0= ruleRelationType ) )
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:137:1: (lv_relationTypes_2_0= ruleRelationType )
            	    {
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:137:1: (lv_relationTypes_2_0= ruleRelationType )
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:138:3: lv_relationTypes_2_0= ruleRelationType
            	    {
            	     
            	    	        currentNode=createCompositeNode(grammarAccess.getOseeTypeModelAccess().getRelationTypesRelationTypeParserRuleCall_1_1_0(), currentNode); 
            	    	    
            	    pushFollow(FOLLOW_ruleRelationType_in_ruleOseeTypeModel179);
            	    lv_relationTypes_2_0=ruleRelationType();
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
            	    	        		"RelationType", 
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
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:161:6: ( (lv_attributeTypes_3_0= ruleAttributeType ) )
            	    {
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:161:6: ( (lv_attributeTypes_3_0= ruleAttributeType ) )
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:162:1: (lv_attributeTypes_3_0= ruleAttributeType )
            	    {
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:162:1: (lv_attributeTypes_3_0= ruleAttributeType )
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:163:3: lv_attributeTypes_3_0= ruleAttributeType
            	    {
            	     
            	    	        currentNode=createCompositeNode(grammarAccess.getOseeTypeModelAccess().getAttributeTypesAttributeTypeParserRuleCall_1_2_0(), currentNode); 
            	    	    
            	    pushFollow(FOLLOW_ruleAttributeType_in_ruleOseeTypeModel206);
            	    lv_attributeTypes_3_0=ruleAttributeType();
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
            	    	        		"AttributeType", 
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
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:186:6: ( (lv_enumTypes_4_0= ruleOseeEnumType ) )
            	    {
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:186:6: ( (lv_enumTypes_4_0= ruleOseeEnumType ) )
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:187:1: (lv_enumTypes_4_0= ruleOseeEnumType )
            	    {
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:187:1: (lv_enumTypes_4_0= ruleOseeEnumType )
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:188:3: lv_enumTypes_4_0= ruleOseeEnumType
            	    {
            	     
            	    	        currentNode=createCompositeNode(grammarAccess.getOseeTypeModelAccess().getEnumTypesOseeEnumTypeParserRuleCall_1_3_0(), currentNode); 
            	    	    
            	    pushFollow(FOLLOW_ruleOseeEnumType_in_ruleOseeTypeModel233);
            	    lv_enumTypes_4_0=ruleOseeEnumType();
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
            	    	        		"OseeEnumType", 
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
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:211:6: ( (lv_enumOverrides_5_0= ruleOseeEnumOverride ) )
            	    {
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:211:6: ( (lv_enumOverrides_5_0= ruleOseeEnumOverride ) )
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:212:1: (lv_enumOverrides_5_0= ruleOseeEnumOverride )
            	    {
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:212:1: (lv_enumOverrides_5_0= ruleOseeEnumOverride )
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:213:3: lv_enumOverrides_5_0= ruleOseeEnumOverride
            	    {
            	     
            	    	        currentNode=createCompositeNode(grammarAccess.getOseeTypeModelAccess().getEnumOverridesOseeEnumOverrideParserRuleCall_1_4_0(), currentNode); 
            	    	    
            	    pushFollow(FOLLOW_ruleOseeEnumOverride_in_ruleOseeTypeModel260);
            	    lv_enumOverrides_5_0=ruleOseeEnumOverride();
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
            	    	        		"OseeEnumOverride", 
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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:242:1: entryRuleImport returns [EObject current=null] : iv_ruleImport= ruleImport EOF ;
    public final EObject entryRuleImport() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleImport = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:242:48: (iv_ruleImport= ruleImport EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:243:2: iv_ruleImport= ruleImport EOF
            {
             currentNode = createCompositeNode(grammarAccess.getImportRule(), currentNode); 
            pushFollow(FOLLOW_ruleImport_in_entryRuleImport295);
            iv_ruleImport=ruleImport();
            _fsp--;

             current =iv_ruleImport; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleImport305); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:250:1: ruleImport returns [EObject current=null] : ( 'import' ( (lv_importURI_1_0= RULE_STRING ) ) ) ;
    public final EObject ruleImport() throws RecognitionException {
        EObject current = null;

        Token lv_importURI_1_0=null;

         @SuppressWarnings("unused") EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:255:6: ( ( 'import' ( (lv_importURI_1_0= RULE_STRING ) ) ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:256:1: ( 'import' ( (lv_importURI_1_0= RULE_STRING ) ) )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:256:1: ( 'import' ( (lv_importURI_1_0= RULE_STRING ) ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:256:2: 'import' ( (lv_importURI_1_0= RULE_STRING ) )
            {
            match(input,12,FOLLOW_12_in_ruleImport339); 

                    createLeafNode(grammarAccess.getImportAccess().getImportKeyword_0(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:260:1: ( (lv_importURI_1_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:261:1: (lv_importURI_1_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:261:1: (lv_importURI_1_0= RULE_STRING )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:262:3: lv_importURI_1_0= RULE_STRING
            {
            lv_importURI_1_0=(Token)input.LT(1);
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleImport356); 

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:291:1: entryRuleNAME_REFERENCE returns [String current=null] : iv_ruleNAME_REFERENCE= ruleNAME_REFERENCE EOF ;
    public final String entryRuleNAME_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleNAME_REFERENCE = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:291:55: (iv_ruleNAME_REFERENCE= ruleNAME_REFERENCE EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:292:2: iv_ruleNAME_REFERENCE= ruleNAME_REFERENCE EOF
            {
             currentNode = createCompositeNode(grammarAccess.getNAME_REFERENCERule(), currentNode); 
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_entryRuleNAME_REFERENCE395);
            iv_ruleNAME_REFERENCE=ruleNAME_REFERENCE();
            _fsp--;

             current =iv_ruleNAME_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleNAME_REFERENCE406); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:299:1: ruleNAME_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleNAME_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:304:6: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:305:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)input.LT(1);
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleNAME_REFERENCE445); 

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:320:1: entryRuleQUALIFIED_NAME returns [String current=null] : iv_ruleQUALIFIED_NAME= ruleQUALIFIED_NAME EOF ;
    public final String entryRuleQUALIFIED_NAME() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleQUALIFIED_NAME = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:320:55: (iv_ruleQUALIFIED_NAME= ruleQUALIFIED_NAME EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:321:2: iv_ruleQUALIFIED_NAME= ruleQUALIFIED_NAME EOF
            {
             currentNode = createCompositeNode(grammarAccess.getQUALIFIED_NAMERule(), currentNode); 
            pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_entryRuleQUALIFIED_NAME488);
            iv_ruleQUALIFIED_NAME=ruleQUALIFIED_NAME();
            _fsp--;

             current =iv_ruleQUALIFIED_NAME.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleQUALIFIED_NAME499); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:328:1: ruleQUALIFIED_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* ) ;
    public final AntlrDatatypeRuleToken ruleQUALIFIED_NAME() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_ID_0=null;
        Token kw=null;
        Token this_ID_2=null;

         setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:333:6: ( (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:334:1: (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:334:1: (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:334:6: this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )*
            {
            this_ID_0=(Token)input.LT(1);
            match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleQUALIFIED_NAME539); 

            		current.merge(this_ID_0);
                
             
                createLeafNode(grammarAccess.getQUALIFIED_NAMEAccess().getIDTerminalRuleCall_0(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:341:1: (kw= '.' this_ID_2= RULE_ID )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==13) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:342:2: kw= '.' this_ID_2= RULE_ID
            	    {
            	    kw=(Token)input.LT(1);
            	    match(input,13,FOLLOW_13_in_ruleQUALIFIED_NAME558); 

            	            current.merge(kw);
            	            createLeafNode(grammarAccess.getQUALIFIED_NAMEAccess().getFullStopKeyword_1_0(), null); 
            	        
            	    this_ID_2=(Token)input.LT(1);
            	    match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleQUALIFIED_NAME573); 

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:364:1: entryRuleOseeType returns [EObject current=null] : iv_ruleOseeType= ruleOseeType EOF ;
    public final EObject entryRuleOseeType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOseeType = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:364:50: (iv_ruleOseeType= ruleOseeType EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:365:2: iv_ruleOseeType= ruleOseeType EOF
            {
             currentNode = createCompositeNode(grammarAccess.getOseeTypeRule(), currentNode); 
            pushFollow(FOLLOW_ruleOseeType_in_entryRuleOseeType620);
            iv_ruleOseeType=ruleOseeType();
            _fsp--;

             current =iv_ruleOseeType; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleOseeType630); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:372:1: ruleOseeType returns [EObject current=null] : (this_ArtifactType_0= ruleArtifactType | this_RelationType_1= ruleRelationType | this_AttributeType_2= ruleAttributeType | this_OseeEnumType_3= ruleOseeEnumType ) ;
    public final EObject ruleOseeType() throws RecognitionException {
        EObject current = null;

        EObject this_ArtifactType_0 = null;

        EObject this_RelationType_1 = null;

        EObject this_AttributeType_2 = null;

        EObject this_OseeEnumType_3 = null;


         @SuppressWarnings("unused") EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:377:6: ( (this_ArtifactType_0= ruleArtifactType | this_RelationType_1= ruleRelationType | this_AttributeType_2= ruleAttributeType | this_OseeEnumType_3= ruleOseeEnumType ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:378:1: (this_ArtifactType_0= ruleArtifactType | this_RelationType_1= ruleRelationType | this_AttributeType_2= ruleAttributeType | this_OseeEnumType_3= ruleOseeEnumType )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:378:1: (this_ArtifactType_0= ruleArtifactType | this_RelationType_1= ruleRelationType | this_AttributeType_2= ruleAttributeType | this_OseeEnumType_3= ruleOseeEnumType )
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
                    new NoViableAltException("378:1: (this_ArtifactType_0= ruleArtifactType | this_RelationType_1= ruleRelationType | this_AttributeType_2= ruleAttributeType | this_OseeEnumType_3= ruleOseeEnumType )", 4, 0, input);

                throw nvae;
            }

            switch (alt4) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:379:5: this_ArtifactType_0= ruleArtifactType
                    {
                     
                            currentNode=createCompositeNode(grammarAccess.getOseeTypeAccess().getArtifactTypeParserRuleCall_0(), currentNode); 
                        
                    pushFollow(FOLLOW_ruleArtifactType_in_ruleOseeType677);
                    this_ArtifactType_0=ruleArtifactType();
                    _fsp--;

                     
                            current = this_ArtifactType_0; 
                            currentNode = currentNode.getParent();
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:389:5: this_RelationType_1= ruleRelationType
                    {
                     
                            currentNode=createCompositeNode(grammarAccess.getOseeTypeAccess().getRelationTypeParserRuleCall_1(), currentNode); 
                        
                    pushFollow(FOLLOW_ruleRelationType_in_ruleOseeType704);
                    this_RelationType_1=ruleRelationType();
                    _fsp--;

                     
                            current = this_RelationType_1; 
                            currentNode = currentNode.getParent();
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:399:5: this_AttributeType_2= ruleAttributeType
                    {
                     
                            currentNode=createCompositeNode(grammarAccess.getOseeTypeAccess().getAttributeTypeParserRuleCall_2(), currentNode); 
                        
                    pushFollow(FOLLOW_ruleAttributeType_in_ruleOseeType731);
                    this_AttributeType_2=ruleAttributeType();
                    _fsp--;

                     
                            current = this_AttributeType_2; 
                            currentNode = currentNode.getParent();
                        

                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:409:5: this_OseeEnumType_3= ruleOseeEnumType
                    {
                     
                            currentNode=createCompositeNode(grammarAccess.getOseeTypeAccess().getOseeEnumTypeParserRuleCall_3(), currentNode); 
                        
                    pushFollow(FOLLOW_ruleOseeEnumType_in_ruleOseeType758);
                    this_OseeEnumType_3=ruleOseeEnumType();
                    _fsp--;

                     
                            current = this_OseeEnumType_3; 
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


    // $ANTLR start entryRuleArtifactType
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:424:1: entryRuleArtifactType returns [EObject current=null] : iv_ruleArtifactType= ruleArtifactType EOF ;
    public final EObject entryRuleArtifactType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleArtifactType = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:424:54: (iv_ruleArtifactType= ruleArtifactType EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:425:2: iv_ruleArtifactType= ruleArtifactType EOF
            {
             currentNode = createCompositeNode(grammarAccess.getArtifactTypeRule(), currentNode); 
            pushFollow(FOLLOW_ruleArtifactType_in_entryRuleArtifactType790);
            iv_ruleArtifactType=ruleArtifactType();
            _fsp--;

             current =iv_ruleArtifactType; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleArtifactType800); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end entryRuleArtifactType


    // $ANTLR start ruleArtifactType
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:432:1: ruleArtifactType returns [EObject current=null] : ( ( (lv_abstract_0_0= 'abstract' ) )? 'artifactType' ( (lv_name_2_0= ruleNAME_REFERENCE ) ) ( 'extends' ( ( ruleNAME_REFERENCE ) ) ( ',' ( ( ruleNAME_REFERENCE ) ) )* )? '{' 'guid' ( (lv_typeGuid_9_0= RULE_STRING ) ) ( (lv_validAttributeTypes_10_0= ruleAttributeTypeRef ) )* '}' ) ;
    public final EObject ruleArtifactType() throws RecognitionException {
        EObject current = null;

        Token lv_abstract_0_0=null;
        Token lv_typeGuid_9_0=null;
        AntlrDatatypeRuleToken lv_name_2_0 = null;

        EObject lv_validAttributeTypes_10_0 = null;


         @SuppressWarnings("unused") EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:437:6: ( ( ( (lv_abstract_0_0= 'abstract' ) )? 'artifactType' ( (lv_name_2_0= ruleNAME_REFERENCE ) ) ( 'extends' ( ( ruleNAME_REFERENCE ) ) ( ',' ( ( ruleNAME_REFERENCE ) ) )* )? '{' 'guid' ( (lv_typeGuid_9_0= RULE_STRING ) ) ( (lv_validAttributeTypes_10_0= ruleAttributeTypeRef ) )* '}' ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:438:1: ( ( (lv_abstract_0_0= 'abstract' ) )? 'artifactType' ( (lv_name_2_0= ruleNAME_REFERENCE ) ) ( 'extends' ( ( ruleNAME_REFERENCE ) ) ( ',' ( ( ruleNAME_REFERENCE ) ) )* )? '{' 'guid' ( (lv_typeGuid_9_0= RULE_STRING ) ) ( (lv_validAttributeTypes_10_0= ruleAttributeTypeRef ) )* '}' )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:438:1: ( ( (lv_abstract_0_0= 'abstract' ) )? 'artifactType' ( (lv_name_2_0= ruleNAME_REFERENCE ) ) ( 'extends' ( ( ruleNAME_REFERENCE ) ) ( ',' ( ( ruleNAME_REFERENCE ) ) )* )? '{' 'guid' ( (lv_typeGuid_9_0= RULE_STRING ) ) ( (lv_validAttributeTypes_10_0= ruleAttributeTypeRef ) )* '}' )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:438:2: ( (lv_abstract_0_0= 'abstract' ) )? 'artifactType' ( (lv_name_2_0= ruleNAME_REFERENCE ) ) ( 'extends' ( ( ruleNAME_REFERENCE ) ) ( ',' ( ( ruleNAME_REFERENCE ) ) )* )? '{' 'guid' ( (lv_typeGuid_9_0= RULE_STRING ) ) ( (lv_validAttributeTypes_10_0= ruleAttributeTypeRef ) )* '}'
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:438:2: ( (lv_abstract_0_0= 'abstract' ) )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==14) ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:439:1: (lv_abstract_0_0= 'abstract' )
                    {
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:439:1: (lv_abstract_0_0= 'abstract' )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:440:3: lv_abstract_0_0= 'abstract'
                    {
                    lv_abstract_0_0=(Token)input.LT(1);
                    match(input,14,FOLLOW_14_in_ruleArtifactType842); 

                            createLeafNode(grammarAccess.getArtifactTypeAccess().getAbstractAbstractKeyword_0_0(), "abstract"); 
                        

                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getArtifactTypeRule().getType().getClassifier());
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

            match(input,15,FOLLOW_15_in_ruleArtifactType865); 

                    createLeafNode(grammarAccess.getArtifactTypeAccess().getArtifactTypeKeyword_1(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:463:1: ( (lv_name_2_0= ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:464:1: (lv_name_2_0= ruleNAME_REFERENCE )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:464:1: (lv_name_2_0= ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:465:3: lv_name_2_0= ruleNAME_REFERENCE
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getArtifactTypeAccess().getNameNAME_REFERENCEParserRuleCall_2_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleArtifactType886);
            lv_name_2_0=ruleNAME_REFERENCE();
            _fsp--;


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getArtifactTypeRule().getType().getClassifier());
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

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:487:2: ( 'extends' ( ( ruleNAME_REFERENCE ) ) ( ',' ( ( ruleNAME_REFERENCE ) ) )* )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==16) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:487:3: 'extends' ( ( ruleNAME_REFERENCE ) ) ( ',' ( ( ruleNAME_REFERENCE ) ) )*
                    {
                    match(input,16,FOLLOW_16_in_ruleArtifactType896); 

                            createLeafNode(grammarAccess.getArtifactTypeAccess().getExtendsKeyword_3_0(), null); 
                        
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:491:1: ( ( ruleNAME_REFERENCE ) )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:492:1: ( ruleNAME_REFERENCE )
                    {
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:492:1: ( ruleNAME_REFERENCE )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:493:3: ruleNAME_REFERENCE
                    {

                    			if (current==null) {
                    	            current = factory.create(grammarAccess.getArtifactTypeRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                            
                     
                    	        currentNode=createCompositeNode(grammarAccess.getArtifactTypeAccess().getSuperArtifactTypesArtifactTypeCrossReference_3_1_0(), currentNode); 
                    	    
                    pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleArtifactType919);
                    ruleNAME_REFERENCE();
                    _fsp--;

                     
                    	        currentNode = currentNode.getParent();
                    	    

                    }


                    }

                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:507:2: ( ',' ( ( ruleNAME_REFERENCE ) ) )*
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( (LA6_0==17) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:507:3: ',' ( ( ruleNAME_REFERENCE ) )
                    	    {
                    	    match(input,17,FOLLOW_17_in_ruleArtifactType929); 

                    	            createLeafNode(grammarAccess.getArtifactTypeAccess().getCommaKeyword_3_2_0(), null); 
                    	        
                    	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:511:1: ( ( ruleNAME_REFERENCE ) )
                    	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:512:1: ( ruleNAME_REFERENCE )
                    	    {
                    	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:512:1: ( ruleNAME_REFERENCE )
                    	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:513:3: ruleNAME_REFERENCE
                    	    {

                    	    			if (current==null) {
                    	    	            current = factory.create(grammarAccess.getArtifactTypeRule().getType().getClassifier());
                    	    	            associateNodeWithAstElement(currentNode, current);
                    	    	        }
                    	            
                    	     
                    	    	        currentNode=createCompositeNode(grammarAccess.getArtifactTypeAccess().getSuperArtifactTypesArtifactTypeCrossReference_3_2_1_0(), currentNode); 
                    	    	    
                    	    pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleArtifactType952);
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

            match(input,18,FOLLOW_18_in_ruleArtifactType965); 

                    createLeafNode(grammarAccess.getArtifactTypeAccess().getLeftCurlyBracketKeyword_4(), null); 
                
            match(input,19,FOLLOW_19_in_ruleArtifactType974); 

                    createLeafNode(grammarAccess.getArtifactTypeAccess().getGuidKeyword_5(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:535:1: ( (lv_typeGuid_9_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:536:1: (lv_typeGuid_9_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:536:1: (lv_typeGuid_9_0= RULE_STRING )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:537:3: lv_typeGuid_9_0= RULE_STRING
            {
            lv_typeGuid_9_0=(Token)input.LT(1);
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleArtifactType991); 

            			createLeafNode(grammarAccess.getArtifactTypeAccess().getTypeGuidSTRINGTerminalRuleCall_6_0(), "typeGuid"); 
            		

            	        if (current==null) {
            	            current = factory.create(grammarAccess.getArtifactTypeRule().getType().getClassifier());
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

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:559:2: ( (lv_validAttributeTypes_10_0= ruleAttributeTypeRef ) )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==21) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:560:1: (lv_validAttributeTypes_10_0= ruleAttributeTypeRef )
            	    {
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:560:1: (lv_validAttributeTypes_10_0= ruleAttributeTypeRef )
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:561:3: lv_validAttributeTypes_10_0= ruleAttributeTypeRef
            	    {
            	     
            	    	        currentNode=createCompositeNode(grammarAccess.getArtifactTypeAccess().getValidAttributeTypesAttributeTypeRefParserRuleCall_7_0(), currentNode); 
            	    	    
            	    pushFollow(FOLLOW_ruleAttributeTypeRef_in_ruleArtifactType1017);
            	    lv_validAttributeTypes_10_0=ruleAttributeTypeRef();
            	    _fsp--;


            	    	        if (current==null) {
            	    	            current = factory.create(grammarAccess.getArtifactTypeRule().getType().getClassifier());
            	    	            associateNodeWithAstElement(currentNode.getParent(), current);
            	    	        }
            	    	        try {
            	    	       		add(
            	    	       			current, 
            	    	       			"validAttributeTypes",
            	    	        		lv_validAttributeTypes_10_0, 
            	    	        		"AttributeTypeRef", 
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

            match(input,20,FOLLOW_20_in_ruleArtifactType1027); 

                    createLeafNode(grammarAccess.getArtifactTypeAccess().getRightCurlyBracketKeyword_8(), null); 
                

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
    // $ANTLR end ruleArtifactType


    // $ANTLR start entryRuleAttributeTypeRef
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:594:1: entryRuleAttributeTypeRef returns [EObject current=null] : iv_ruleAttributeTypeRef= ruleAttributeTypeRef EOF ;
    public final EObject entryRuleAttributeTypeRef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttributeTypeRef = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:594:58: (iv_ruleAttributeTypeRef= ruleAttributeTypeRef EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:595:2: iv_ruleAttributeTypeRef= ruleAttributeTypeRef EOF
            {
             currentNode = createCompositeNode(grammarAccess.getAttributeTypeRefRule(), currentNode); 
            pushFollow(FOLLOW_ruleAttributeTypeRef_in_entryRuleAttributeTypeRef1060);
            iv_ruleAttributeTypeRef=ruleAttributeTypeRef();
            _fsp--;

             current =iv_ruleAttributeTypeRef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttributeTypeRef1070); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end entryRuleAttributeTypeRef


    // $ANTLR start ruleAttributeTypeRef
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:602:1: ruleAttributeTypeRef returns [EObject current=null] : ( 'attribute' ( ( ruleNAME_REFERENCE ) ) ( 'branchGuid' ( (lv_branchGuid_3_0= RULE_STRING ) ) )? ) ;
    public final EObject ruleAttributeTypeRef() throws RecognitionException {
        EObject current = null;

        Token lv_branchGuid_3_0=null;

         @SuppressWarnings("unused") EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:607:6: ( ( 'attribute' ( ( ruleNAME_REFERENCE ) ) ( 'branchGuid' ( (lv_branchGuid_3_0= RULE_STRING ) ) )? ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:608:1: ( 'attribute' ( ( ruleNAME_REFERENCE ) ) ( 'branchGuid' ( (lv_branchGuid_3_0= RULE_STRING ) ) )? )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:608:1: ( 'attribute' ( ( ruleNAME_REFERENCE ) ) ( 'branchGuid' ( (lv_branchGuid_3_0= RULE_STRING ) ) )? )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:608:2: 'attribute' ( ( ruleNAME_REFERENCE ) ) ( 'branchGuid' ( (lv_branchGuid_3_0= RULE_STRING ) ) )?
            {
            match(input,21,FOLLOW_21_in_ruleAttributeTypeRef1104); 

                    createLeafNode(grammarAccess.getAttributeTypeRefAccess().getAttributeKeyword_0(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:612:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:613:1: ( ruleNAME_REFERENCE )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:613:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:614:3: ruleNAME_REFERENCE
            {

            			if (current==null) {
            	            current = factory.create(grammarAccess.getAttributeTypeRefRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
                    
             
            	        currentNode=createCompositeNode(grammarAccess.getAttributeTypeRefAccess().getValidAttributeTypeAttributeTypeCrossReference_1_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleAttributeTypeRef1127);
            ruleNAME_REFERENCE();
            _fsp--;

             
            	        currentNode = currentNode.getParent();
            	    

            }


            }

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:628:2: ( 'branchGuid' ( (lv_branchGuid_3_0= RULE_STRING ) ) )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==22) ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:628:3: 'branchGuid' ( (lv_branchGuid_3_0= RULE_STRING ) )
                    {
                    match(input,22,FOLLOW_22_in_ruleAttributeTypeRef1137); 

                            createLeafNode(grammarAccess.getAttributeTypeRefAccess().getBranchGuidKeyword_2_0(), null); 
                        
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:632:1: ( (lv_branchGuid_3_0= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:633:1: (lv_branchGuid_3_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:633:1: (lv_branchGuid_3_0= RULE_STRING )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:634:3: lv_branchGuid_3_0= RULE_STRING
                    {
                    lv_branchGuid_3_0=(Token)input.LT(1);
                    match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAttributeTypeRef1154); 

                    			createLeafNode(grammarAccess.getAttributeTypeRefAccess().getBranchGuidSTRINGTerminalRuleCall_2_1_0(), "branchGuid"); 
                    		

                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getAttributeTypeRefRule().getType().getClassifier());
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
    // $ANTLR end ruleAttributeTypeRef


    // $ANTLR start entryRuleAttributeType
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:663:1: entryRuleAttributeType returns [EObject current=null] : iv_ruleAttributeType= ruleAttributeType EOF ;
    public final EObject entryRuleAttributeType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttributeType = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:663:55: (iv_ruleAttributeType= ruleAttributeType EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:664:2: iv_ruleAttributeType= ruleAttributeType EOF
            {
             currentNode = createCompositeNode(grammarAccess.getAttributeTypeRule(), currentNode); 
            pushFollow(FOLLOW_ruleAttributeType_in_entryRuleAttributeType1194);
            iv_ruleAttributeType=ruleAttributeType();
            _fsp--;

             current =iv_ruleAttributeType; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttributeType1204); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end entryRuleAttributeType


    // $ANTLR start ruleAttributeType
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:671:1: ruleAttributeType returns [EObject current=null] : ( 'attributeType' ( (lv_name_1_0= ruleNAME_REFERENCE ) ) ( 'extends' ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) ) ) ( 'overrides' ( ( ruleNAME_REFERENCE ) ) )? '{' 'guid' ( (lv_typeGuid_8_0= RULE_STRING ) ) 'dataProvider' ( ( (lv_dataProvider_10_1= 'DefaultAttributeDataProvider' | lv_dataProvider_10_2= 'UriAttributeDataProvider' | lv_dataProvider_10_3= ruleQUALIFIED_NAME ) ) ) 'min' ( (lv_min_12_0= RULE_WHOLE_NUM_STR ) ) 'max' ( ( (lv_max_14_1= RULE_WHOLE_NUM_STR | lv_max_14_2= 'unlimited' ) ) ) ( 'taggerId' ( ( (lv_taggerId_16_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_16_2= ruleQUALIFIED_NAME ) ) ) )? ( 'enumType' ( ( ruleNAME_REFERENCE ) ) )? ( 'description' ( (lv_description_20_0= RULE_STRING ) ) )? ( 'defaultValue' ( (lv_defaultValue_22_0= RULE_STRING ) ) )? ( 'fileExtension' ( (lv_fileExtension_24_0= RULE_STRING ) ) )? '}' ) ;
    public final EObject ruleAttributeType() throws RecognitionException {
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
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:676:6: ( ( 'attributeType' ( (lv_name_1_0= ruleNAME_REFERENCE ) ) ( 'extends' ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) ) ) ( 'overrides' ( ( ruleNAME_REFERENCE ) ) )? '{' 'guid' ( (lv_typeGuid_8_0= RULE_STRING ) ) 'dataProvider' ( ( (lv_dataProvider_10_1= 'DefaultAttributeDataProvider' | lv_dataProvider_10_2= 'UriAttributeDataProvider' | lv_dataProvider_10_3= ruleQUALIFIED_NAME ) ) ) 'min' ( (lv_min_12_0= RULE_WHOLE_NUM_STR ) ) 'max' ( ( (lv_max_14_1= RULE_WHOLE_NUM_STR | lv_max_14_2= 'unlimited' ) ) ) ( 'taggerId' ( ( (lv_taggerId_16_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_16_2= ruleQUALIFIED_NAME ) ) ) )? ( 'enumType' ( ( ruleNAME_REFERENCE ) ) )? ( 'description' ( (lv_description_20_0= RULE_STRING ) ) )? ( 'defaultValue' ( (lv_defaultValue_22_0= RULE_STRING ) ) )? ( 'fileExtension' ( (lv_fileExtension_24_0= RULE_STRING ) ) )? '}' ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:677:1: ( 'attributeType' ( (lv_name_1_0= ruleNAME_REFERENCE ) ) ( 'extends' ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) ) ) ( 'overrides' ( ( ruleNAME_REFERENCE ) ) )? '{' 'guid' ( (lv_typeGuid_8_0= RULE_STRING ) ) 'dataProvider' ( ( (lv_dataProvider_10_1= 'DefaultAttributeDataProvider' | lv_dataProvider_10_2= 'UriAttributeDataProvider' | lv_dataProvider_10_3= ruleQUALIFIED_NAME ) ) ) 'min' ( (lv_min_12_0= RULE_WHOLE_NUM_STR ) ) 'max' ( ( (lv_max_14_1= RULE_WHOLE_NUM_STR | lv_max_14_2= 'unlimited' ) ) ) ( 'taggerId' ( ( (lv_taggerId_16_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_16_2= ruleQUALIFIED_NAME ) ) ) )? ( 'enumType' ( ( ruleNAME_REFERENCE ) ) )? ( 'description' ( (lv_description_20_0= RULE_STRING ) ) )? ( 'defaultValue' ( (lv_defaultValue_22_0= RULE_STRING ) ) )? ( 'fileExtension' ( (lv_fileExtension_24_0= RULE_STRING ) ) )? '}' )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:677:1: ( 'attributeType' ( (lv_name_1_0= ruleNAME_REFERENCE ) ) ( 'extends' ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) ) ) ( 'overrides' ( ( ruleNAME_REFERENCE ) ) )? '{' 'guid' ( (lv_typeGuid_8_0= RULE_STRING ) ) 'dataProvider' ( ( (lv_dataProvider_10_1= 'DefaultAttributeDataProvider' | lv_dataProvider_10_2= 'UriAttributeDataProvider' | lv_dataProvider_10_3= ruleQUALIFIED_NAME ) ) ) 'min' ( (lv_min_12_0= RULE_WHOLE_NUM_STR ) ) 'max' ( ( (lv_max_14_1= RULE_WHOLE_NUM_STR | lv_max_14_2= 'unlimited' ) ) ) ( 'taggerId' ( ( (lv_taggerId_16_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_16_2= ruleQUALIFIED_NAME ) ) ) )? ( 'enumType' ( ( ruleNAME_REFERENCE ) ) )? ( 'description' ( (lv_description_20_0= RULE_STRING ) ) )? ( 'defaultValue' ( (lv_defaultValue_22_0= RULE_STRING ) ) )? ( 'fileExtension' ( (lv_fileExtension_24_0= RULE_STRING ) ) )? '}' )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:677:2: 'attributeType' ( (lv_name_1_0= ruleNAME_REFERENCE ) ) ( 'extends' ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) ) ) ( 'overrides' ( ( ruleNAME_REFERENCE ) ) )? '{' 'guid' ( (lv_typeGuid_8_0= RULE_STRING ) ) 'dataProvider' ( ( (lv_dataProvider_10_1= 'DefaultAttributeDataProvider' | lv_dataProvider_10_2= 'UriAttributeDataProvider' | lv_dataProvider_10_3= ruleQUALIFIED_NAME ) ) ) 'min' ( (lv_min_12_0= RULE_WHOLE_NUM_STR ) ) 'max' ( ( (lv_max_14_1= RULE_WHOLE_NUM_STR | lv_max_14_2= 'unlimited' ) ) ) ( 'taggerId' ( ( (lv_taggerId_16_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_16_2= ruleQUALIFIED_NAME ) ) ) )? ( 'enumType' ( ( ruleNAME_REFERENCE ) ) )? ( 'description' ( (lv_description_20_0= RULE_STRING ) ) )? ( 'defaultValue' ( (lv_defaultValue_22_0= RULE_STRING ) ) )? ( 'fileExtension' ( (lv_fileExtension_24_0= RULE_STRING ) ) )? '}'
            {
            match(input,23,FOLLOW_23_in_ruleAttributeType1238); 

                    createLeafNode(grammarAccess.getAttributeTypeAccess().getAttributeTypeKeyword_0(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:681:1: ( (lv_name_1_0= ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:682:1: (lv_name_1_0= ruleNAME_REFERENCE )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:682:1: (lv_name_1_0= ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:683:3: lv_name_1_0= ruleNAME_REFERENCE
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getAttributeTypeAccess().getNameNAME_REFERENCEParserRuleCall_1_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleAttributeType1259);
            lv_name_1_0=ruleNAME_REFERENCE();
            _fsp--;


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getAttributeTypeRule().getType().getClassifier());
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

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:705:2: ( 'extends' ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:705:3: 'extends' ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) )
            {
            match(input,16,FOLLOW_16_in_ruleAttributeType1269); 

                    createLeafNode(grammarAccess.getAttributeTypeAccess().getExtendsKeyword_2_0(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:709:1: ( (lv_baseAttributeType_3_0= ruleAttributeBaseType ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:710:1: (lv_baseAttributeType_3_0= ruleAttributeBaseType )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:710:1: (lv_baseAttributeType_3_0= ruleAttributeBaseType )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:711:3: lv_baseAttributeType_3_0= ruleAttributeBaseType
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getAttributeTypeAccess().getBaseAttributeTypeAttributeBaseTypeParserRuleCall_2_1_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleAttributeBaseType_in_ruleAttributeType1290);
            lv_baseAttributeType_3_0=ruleAttributeBaseType();
            _fsp--;


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getAttributeTypeRule().getType().getClassifier());
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

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:733:3: ( 'overrides' ( ( ruleNAME_REFERENCE ) ) )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==24) ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:733:4: 'overrides' ( ( ruleNAME_REFERENCE ) )
                    {
                    match(input,24,FOLLOW_24_in_ruleAttributeType1301); 

                            createLeafNode(grammarAccess.getAttributeTypeAccess().getOverridesKeyword_3_0(), null); 
                        
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:737:1: ( ( ruleNAME_REFERENCE ) )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:738:1: ( ruleNAME_REFERENCE )
                    {
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:738:1: ( ruleNAME_REFERENCE )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:739:3: ruleNAME_REFERENCE
                    {

                    			if (current==null) {
                    	            current = factory.create(grammarAccess.getAttributeTypeRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                            
                     
                    	        currentNode=createCompositeNode(grammarAccess.getAttributeTypeAccess().getOverrideAttributeTypeCrossReference_3_1_0(), currentNode); 
                    	    
                    pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleAttributeType1324);
                    ruleNAME_REFERENCE();
                    _fsp--;

                     
                    	        currentNode = currentNode.getParent();
                    	    

                    }


                    }


                    }
                    break;

            }

            match(input,18,FOLLOW_18_in_ruleAttributeType1335); 

                    createLeafNode(grammarAccess.getAttributeTypeAccess().getLeftCurlyBracketKeyword_4(), null); 
                
            match(input,19,FOLLOW_19_in_ruleAttributeType1344); 

                    createLeafNode(grammarAccess.getAttributeTypeAccess().getGuidKeyword_5(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:761:1: ( (lv_typeGuid_8_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:762:1: (lv_typeGuid_8_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:762:1: (lv_typeGuid_8_0= RULE_STRING )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:763:3: lv_typeGuid_8_0= RULE_STRING
            {
            lv_typeGuid_8_0=(Token)input.LT(1);
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAttributeType1361); 

            			createLeafNode(grammarAccess.getAttributeTypeAccess().getTypeGuidSTRINGTerminalRuleCall_6_0(), "typeGuid"); 
            		

            	        if (current==null) {
            	            current = factory.create(grammarAccess.getAttributeTypeRule().getType().getClassifier());
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

            match(input,25,FOLLOW_25_in_ruleAttributeType1375); 

                    createLeafNode(grammarAccess.getAttributeTypeAccess().getDataProviderKeyword_7(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:789:1: ( ( (lv_dataProvider_10_1= 'DefaultAttributeDataProvider' | lv_dataProvider_10_2= 'UriAttributeDataProvider' | lv_dataProvider_10_3= ruleQUALIFIED_NAME ) ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:790:1: ( (lv_dataProvider_10_1= 'DefaultAttributeDataProvider' | lv_dataProvider_10_2= 'UriAttributeDataProvider' | lv_dataProvider_10_3= ruleQUALIFIED_NAME ) )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:790:1: ( (lv_dataProvider_10_1= 'DefaultAttributeDataProvider' | lv_dataProvider_10_2= 'UriAttributeDataProvider' | lv_dataProvider_10_3= ruleQUALIFIED_NAME ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:791:1: (lv_dataProvider_10_1= 'DefaultAttributeDataProvider' | lv_dataProvider_10_2= 'UriAttributeDataProvider' | lv_dataProvider_10_3= ruleQUALIFIED_NAME )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:791:1: (lv_dataProvider_10_1= 'DefaultAttributeDataProvider' | lv_dataProvider_10_2= 'UriAttributeDataProvider' | lv_dataProvider_10_3= ruleQUALIFIED_NAME )
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
                    new NoViableAltException("791:1: (lv_dataProvider_10_1= 'DefaultAttributeDataProvider' | lv_dataProvider_10_2= 'UriAttributeDataProvider' | lv_dataProvider_10_3= ruleQUALIFIED_NAME )", 11, 0, input);

                throw nvae;
            }

            switch (alt11) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:792:3: lv_dataProvider_10_1= 'DefaultAttributeDataProvider'
                    {
                    lv_dataProvider_10_1=(Token)input.LT(1);
                    match(input,26,FOLLOW_26_in_ruleAttributeType1394); 

                            createLeafNode(grammarAccess.getAttributeTypeAccess().getDataProviderDefaultAttributeDataProviderKeyword_8_0_0(), "dataProvider"); 
                        

                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getAttributeTypeRule().getType().getClassifier());
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
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:810:8: lv_dataProvider_10_2= 'UriAttributeDataProvider'
                    {
                    lv_dataProvider_10_2=(Token)input.LT(1);
                    match(input,27,FOLLOW_27_in_ruleAttributeType1422); 

                            createLeafNode(grammarAccess.getAttributeTypeAccess().getDataProviderUriAttributeDataProviderKeyword_8_0_1(), "dataProvider"); 
                        

                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getAttributeTypeRule().getType().getClassifier());
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
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:828:8: lv_dataProvider_10_3= ruleQUALIFIED_NAME
                    {
                     
                    	        currentNode=createCompositeNode(grammarAccess.getAttributeTypeAccess().getDataProviderQUALIFIED_NAMEParserRuleCall_8_0_2(), currentNode); 
                    	    
                    pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_ruleAttributeType1454);
                    lv_dataProvider_10_3=ruleQUALIFIED_NAME();
                    _fsp--;


                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getAttributeTypeRule().getType().getClassifier());
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

            match(input,28,FOLLOW_28_in_ruleAttributeType1466); 

                    createLeafNode(grammarAccess.getAttributeTypeAccess().getMinKeyword_9(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:856:1: ( (lv_min_12_0= RULE_WHOLE_NUM_STR ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:857:1: (lv_min_12_0= RULE_WHOLE_NUM_STR )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:857:1: (lv_min_12_0= RULE_WHOLE_NUM_STR )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:858:3: lv_min_12_0= RULE_WHOLE_NUM_STR
            {
            lv_min_12_0=(Token)input.LT(1);
            match(input,RULE_WHOLE_NUM_STR,FOLLOW_RULE_WHOLE_NUM_STR_in_ruleAttributeType1483); 

            			createLeafNode(grammarAccess.getAttributeTypeAccess().getMinWHOLE_NUM_STRTerminalRuleCall_10_0(), "min"); 
            		

            	        if (current==null) {
            	            current = factory.create(grammarAccess.getAttributeTypeRule().getType().getClassifier());
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

            match(input,29,FOLLOW_29_in_ruleAttributeType1497); 

                    createLeafNode(grammarAccess.getAttributeTypeAccess().getMaxKeyword_11(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:884:1: ( ( (lv_max_14_1= RULE_WHOLE_NUM_STR | lv_max_14_2= 'unlimited' ) ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:885:1: ( (lv_max_14_1= RULE_WHOLE_NUM_STR | lv_max_14_2= 'unlimited' ) )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:885:1: ( (lv_max_14_1= RULE_WHOLE_NUM_STR | lv_max_14_2= 'unlimited' ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:886:1: (lv_max_14_1= RULE_WHOLE_NUM_STR | lv_max_14_2= 'unlimited' )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:886:1: (lv_max_14_1= RULE_WHOLE_NUM_STR | lv_max_14_2= 'unlimited' )
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
                    new NoViableAltException("886:1: (lv_max_14_1= RULE_WHOLE_NUM_STR | lv_max_14_2= 'unlimited' )", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:887:3: lv_max_14_1= RULE_WHOLE_NUM_STR
                    {
                    lv_max_14_1=(Token)input.LT(1);
                    match(input,RULE_WHOLE_NUM_STR,FOLLOW_RULE_WHOLE_NUM_STR_in_ruleAttributeType1516); 

                    			createLeafNode(grammarAccess.getAttributeTypeAccess().getMaxWHOLE_NUM_STRTerminalRuleCall_12_0_0(), "max"); 
                    		

                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getAttributeTypeRule().getType().getClassifier());
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
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:908:8: lv_max_14_2= 'unlimited'
                    {
                    lv_max_14_2=(Token)input.LT(1);
                    match(input,30,FOLLOW_30_in_ruleAttributeType1536); 

                            createLeafNode(grammarAccess.getAttributeTypeAccess().getMaxUnlimitedKeyword_12_0_1(), "max"); 
                        

                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getAttributeTypeRule().getType().getClassifier());
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

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:929:2: ( 'taggerId' ( ( (lv_taggerId_16_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_16_2= ruleQUALIFIED_NAME ) ) ) )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==31) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:929:3: 'taggerId' ( ( (lv_taggerId_16_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_16_2= ruleQUALIFIED_NAME ) ) )
                    {
                    match(input,31,FOLLOW_31_in_ruleAttributeType1562); 

                            createLeafNode(grammarAccess.getAttributeTypeAccess().getTaggerIdKeyword_13_0(), null); 
                        
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:933:1: ( ( (lv_taggerId_16_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_16_2= ruleQUALIFIED_NAME ) ) )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:934:1: ( (lv_taggerId_16_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_16_2= ruleQUALIFIED_NAME ) )
                    {
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:934:1: ( (lv_taggerId_16_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_16_2= ruleQUALIFIED_NAME ) )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:935:1: (lv_taggerId_16_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_16_2= ruleQUALIFIED_NAME )
                    {
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:935:1: (lv_taggerId_16_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_16_2= ruleQUALIFIED_NAME )
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
                            new NoViableAltException("935:1: (lv_taggerId_16_1= 'DefaultAttributeTaggerProvider' | lv_taggerId_16_2= ruleQUALIFIED_NAME )", 13, 0, input);

                        throw nvae;
                    }
                    switch (alt13) {
                        case 1 :
                            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:936:3: lv_taggerId_16_1= 'DefaultAttributeTaggerProvider'
                            {
                            lv_taggerId_16_1=(Token)input.LT(1);
                            match(input,32,FOLLOW_32_in_ruleAttributeType1581); 

                                    createLeafNode(grammarAccess.getAttributeTypeAccess().getTaggerIdDefaultAttributeTaggerProviderKeyword_13_1_0_0(), "taggerId"); 
                                

                            	        if (current==null) {
                            	            current = factory.create(grammarAccess.getAttributeTypeRule().getType().getClassifier());
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
                            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:954:8: lv_taggerId_16_2= ruleQUALIFIED_NAME
                            {
                             
                            	        currentNode=createCompositeNode(grammarAccess.getAttributeTypeAccess().getTaggerIdQUALIFIED_NAMEParserRuleCall_13_1_0_1(), currentNode); 
                            	    
                            pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_ruleAttributeType1613);
                            lv_taggerId_16_2=ruleQUALIFIED_NAME();
                            _fsp--;


                            	        if (current==null) {
                            	            current = factory.create(grammarAccess.getAttributeTypeRule().getType().getClassifier());
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

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:978:4: ( 'enumType' ( ( ruleNAME_REFERENCE ) ) )?
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==33) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:978:5: 'enumType' ( ( ruleNAME_REFERENCE ) )
                    {
                    match(input,33,FOLLOW_33_in_ruleAttributeType1628); 

                            createLeafNode(grammarAccess.getAttributeTypeAccess().getEnumTypeKeyword_14_0(), null); 
                        
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:982:1: ( ( ruleNAME_REFERENCE ) )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:983:1: ( ruleNAME_REFERENCE )
                    {
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:983:1: ( ruleNAME_REFERENCE )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:984:3: ruleNAME_REFERENCE
                    {

                    			if (current==null) {
                    	            current = factory.create(grammarAccess.getAttributeTypeRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                            
                     
                    	        currentNode=createCompositeNode(grammarAccess.getAttributeTypeAccess().getEnumTypeOseeEnumTypeCrossReference_14_1_0(), currentNode); 
                    	    
                    pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleAttributeType1651);
                    ruleNAME_REFERENCE();
                    _fsp--;

                     
                    	        currentNode = currentNode.getParent();
                    	    

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:998:4: ( 'description' ( (lv_description_20_0= RULE_STRING ) ) )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==34) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:998:5: 'description' ( (lv_description_20_0= RULE_STRING ) )
                    {
                    match(input,34,FOLLOW_34_in_ruleAttributeType1663); 

                            createLeafNode(grammarAccess.getAttributeTypeAccess().getDescriptionKeyword_15_0(), null); 
                        
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1002:1: ( (lv_description_20_0= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1003:1: (lv_description_20_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1003:1: (lv_description_20_0= RULE_STRING )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1004:3: lv_description_20_0= RULE_STRING
                    {
                    lv_description_20_0=(Token)input.LT(1);
                    match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAttributeType1680); 

                    			createLeafNode(grammarAccess.getAttributeTypeAccess().getDescriptionSTRINGTerminalRuleCall_15_1_0(), "description"); 
                    		

                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getAttributeTypeRule().getType().getClassifier());
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

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1026:4: ( 'defaultValue' ( (lv_defaultValue_22_0= RULE_STRING ) ) )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==35) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1026:5: 'defaultValue' ( (lv_defaultValue_22_0= RULE_STRING ) )
                    {
                    match(input,35,FOLLOW_35_in_ruleAttributeType1697); 

                            createLeafNode(grammarAccess.getAttributeTypeAccess().getDefaultValueKeyword_16_0(), null); 
                        
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1030:1: ( (lv_defaultValue_22_0= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1031:1: (lv_defaultValue_22_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1031:1: (lv_defaultValue_22_0= RULE_STRING )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1032:3: lv_defaultValue_22_0= RULE_STRING
                    {
                    lv_defaultValue_22_0=(Token)input.LT(1);
                    match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAttributeType1714); 

                    			createLeafNode(grammarAccess.getAttributeTypeAccess().getDefaultValueSTRINGTerminalRuleCall_16_1_0(), "defaultValue"); 
                    		

                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getAttributeTypeRule().getType().getClassifier());
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

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1054:4: ( 'fileExtension' ( (lv_fileExtension_24_0= RULE_STRING ) ) )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==36) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1054:5: 'fileExtension' ( (lv_fileExtension_24_0= RULE_STRING ) )
                    {
                    match(input,36,FOLLOW_36_in_ruleAttributeType1731); 

                            createLeafNode(grammarAccess.getAttributeTypeAccess().getFileExtensionKeyword_17_0(), null); 
                        
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1058:1: ( (lv_fileExtension_24_0= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1059:1: (lv_fileExtension_24_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1059:1: (lv_fileExtension_24_0= RULE_STRING )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1060:3: lv_fileExtension_24_0= RULE_STRING
                    {
                    lv_fileExtension_24_0=(Token)input.LT(1);
                    match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAttributeType1748); 

                    			createLeafNode(grammarAccess.getAttributeTypeAccess().getFileExtensionSTRINGTerminalRuleCall_17_1_0(), "fileExtension"); 
                    		

                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getAttributeTypeRule().getType().getClassifier());
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

            match(input,20,FOLLOW_20_in_ruleAttributeType1764); 

                    createLeafNode(grammarAccess.getAttributeTypeAccess().getRightCurlyBracketKeyword_18(), null); 
                

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
    // $ANTLR end ruleAttributeType


    // $ANTLR start entryRuleAttributeBaseType
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1093:1: entryRuleAttributeBaseType returns [String current=null] : iv_ruleAttributeBaseType= ruleAttributeBaseType EOF ;
    public final String entryRuleAttributeBaseType() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleAttributeBaseType = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1093:58: (iv_ruleAttributeBaseType= ruleAttributeBaseType EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1094:2: iv_ruleAttributeBaseType= ruleAttributeBaseType EOF
            {
             currentNode = createCompositeNode(grammarAccess.getAttributeBaseTypeRule(), currentNode); 
            pushFollow(FOLLOW_ruleAttributeBaseType_in_entryRuleAttributeBaseType1798);
            iv_ruleAttributeBaseType=ruleAttributeBaseType();
            _fsp--;

             current =iv_ruleAttributeBaseType.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttributeBaseType1809); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1101:1: ruleAttributeBaseType returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'BooleanAttribute' | kw= 'CompressedContentAttribute' | kw= 'DateAttribute' | kw= 'EnumeratedAttribute' | kw= 'FloatingPointAttribute' | kw= 'IntegerAttribute' | kw= 'JavaObjectAttribute' | kw= 'StringAttribute' | kw= 'WordAttribute' | this_QUALIFIED_NAME_9= ruleQUALIFIED_NAME ) ;
    public final AntlrDatatypeRuleToken ruleAttributeBaseType() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        AntlrDatatypeRuleToken this_QUALIFIED_NAME_9 = null;


         setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1106:6: ( (kw= 'BooleanAttribute' | kw= 'CompressedContentAttribute' | kw= 'DateAttribute' | kw= 'EnumeratedAttribute' | kw= 'FloatingPointAttribute' | kw= 'IntegerAttribute' | kw= 'JavaObjectAttribute' | kw= 'StringAttribute' | kw= 'WordAttribute' | this_QUALIFIED_NAME_9= ruleQUALIFIED_NAME ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1107:1: (kw= 'BooleanAttribute' | kw= 'CompressedContentAttribute' | kw= 'DateAttribute' | kw= 'EnumeratedAttribute' | kw= 'FloatingPointAttribute' | kw= 'IntegerAttribute' | kw= 'JavaObjectAttribute' | kw= 'StringAttribute' | kw= 'WordAttribute' | this_QUALIFIED_NAME_9= ruleQUALIFIED_NAME )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1107:1: (kw= 'BooleanAttribute' | kw= 'CompressedContentAttribute' | kw= 'DateAttribute' | kw= 'EnumeratedAttribute' | kw= 'FloatingPointAttribute' | kw= 'IntegerAttribute' | kw= 'JavaObjectAttribute' | kw= 'StringAttribute' | kw= 'WordAttribute' | this_QUALIFIED_NAME_9= ruleQUALIFIED_NAME )
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
                    new NoViableAltException("1107:1: (kw= 'BooleanAttribute' | kw= 'CompressedContentAttribute' | kw= 'DateAttribute' | kw= 'EnumeratedAttribute' | kw= 'FloatingPointAttribute' | kw= 'IntegerAttribute' | kw= 'JavaObjectAttribute' | kw= 'StringAttribute' | kw= 'WordAttribute' | this_QUALIFIED_NAME_9= ruleQUALIFIED_NAME )", 19, 0, input);

                throw nvae;
            }

            switch (alt19) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1108:2: kw= 'BooleanAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,37,FOLLOW_37_in_ruleAttributeBaseType1847); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getBooleanAttributeKeyword_0(), null); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1115:2: kw= 'CompressedContentAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,38,FOLLOW_38_in_ruleAttributeBaseType1866); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getCompressedContentAttributeKeyword_1(), null); 
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1122:2: kw= 'DateAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,39,FOLLOW_39_in_ruleAttributeBaseType1885); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getDateAttributeKeyword_2(), null); 
                        

                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1129:2: kw= 'EnumeratedAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,40,FOLLOW_40_in_ruleAttributeBaseType1904); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getEnumeratedAttributeKeyword_3(), null); 
                        

                    }
                    break;
                case 5 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1136:2: kw= 'FloatingPointAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,41,FOLLOW_41_in_ruleAttributeBaseType1923); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getFloatingPointAttributeKeyword_4(), null); 
                        

                    }
                    break;
                case 6 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1143:2: kw= 'IntegerAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,42,FOLLOW_42_in_ruleAttributeBaseType1942); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getIntegerAttributeKeyword_5(), null); 
                        

                    }
                    break;
                case 7 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1150:2: kw= 'JavaObjectAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,43,FOLLOW_43_in_ruleAttributeBaseType1961); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getJavaObjectAttributeKeyword_6(), null); 
                        

                    }
                    break;
                case 8 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1157:2: kw= 'StringAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,44,FOLLOW_44_in_ruleAttributeBaseType1980); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getStringAttributeKeyword_7(), null); 
                        

                    }
                    break;
                case 9 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1164:2: kw= 'WordAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,45,FOLLOW_45_in_ruleAttributeBaseType1999); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getWordAttributeKeyword_8(), null); 
                        

                    }
                    break;
                case 10 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1171:5: this_QUALIFIED_NAME_9= ruleQUALIFIED_NAME
                    {
                     
                            currentNode=createCompositeNode(grammarAccess.getAttributeBaseTypeAccess().getQUALIFIED_NAMEParserRuleCall_9(), currentNode); 
                        
                    pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_ruleAttributeBaseType2027);
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


    // $ANTLR start entryRuleOseeEnumType
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1189:1: entryRuleOseeEnumType returns [EObject current=null] : iv_ruleOseeEnumType= ruleOseeEnumType EOF ;
    public final EObject entryRuleOseeEnumType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOseeEnumType = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1189:54: (iv_ruleOseeEnumType= ruleOseeEnumType EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1190:2: iv_ruleOseeEnumType= ruleOseeEnumType EOF
            {
             currentNode = createCompositeNode(grammarAccess.getOseeEnumTypeRule(), currentNode); 
            pushFollow(FOLLOW_ruleOseeEnumType_in_entryRuleOseeEnumType2070);
            iv_ruleOseeEnumType=ruleOseeEnumType();
            _fsp--;

             current =iv_ruleOseeEnumType; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleOseeEnumType2080); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end entryRuleOseeEnumType


    // $ANTLR start ruleOseeEnumType
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1197:1: ruleOseeEnumType returns [EObject current=null] : ( 'oseeEnumType' ( (lv_name_1_0= ruleNAME_REFERENCE ) ) '{' 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) ( (lv_enumEntries_5_0= ruleOseeEnumEntry ) )* '}' ) ;
    public final EObject ruleOseeEnumType() throws RecognitionException {
        EObject current = null;

        Token lv_typeGuid_4_0=null;
        AntlrDatatypeRuleToken lv_name_1_0 = null;

        EObject lv_enumEntries_5_0 = null;


         @SuppressWarnings("unused") EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1202:6: ( ( 'oseeEnumType' ( (lv_name_1_0= ruleNAME_REFERENCE ) ) '{' 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) ( (lv_enumEntries_5_0= ruleOseeEnumEntry ) )* '}' ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1203:1: ( 'oseeEnumType' ( (lv_name_1_0= ruleNAME_REFERENCE ) ) '{' 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) ( (lv_enumEntries_5_0= ruleOseeEnumEntry ) )* '}' )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1203:1: ( 'oseeEnumType' ( (lv_name_1_0= ruleNAME_REFERENCE ) ) '{' 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) ( (lv_enumEntries_5_0= ruleOseeEnumEntry ) )* '}' )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1203:2: 'oseeEnumType' ( (lv_name_1_0= ruleNAME_REFERENCE ) ) '{' 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) ( (lv_enumEntries_5_0= ruleOseeEnumEntry ) )* '}'
            {
            match(input,46,FOLLOW_46_in_ruleOseeEnumType2114); 

                    createLeafNode(grammarAccess.getOseeEnumTypeAccess().getOseeEnumTypeKeyword_0(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1207:1: ( (lv_name_1_0= ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1208:1: (lv_name_1_0= ruleNAME_REFERENCE )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1208:1: (lv_name_1_0= ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1209:3: lv_name_1_0= ruleNAME_REFERENCE
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getOseeEnumTypeAccess().getNameNAME_REFERENCEParserRuleCall_1_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleOseeEnumType2135);
            lv_name_1_0=ruleNAME_REFERENCE();
            _fsp--;


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getOseeEnumTypeRule().getType().getClassifier());
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

            match(input,18,FOLLOW_18_in_ruleOseeEnumType2144); 

                    createLeafNode(grammarAccess.getOseeEnumTypeAccess().getLeftCurlyBracketKeyword_2(), null); 
                
            match(input,19,FOLLOW_19_in_ruleOseeEnumType2153); 

                    createLeafNode(grammarAccess.getOseeEnumTypeAccess().getGuidKeyword_3(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1239:1: ( (lv_typeGuid_4_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1240:1: (lv_typeGuid_4_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1240:1: (lv_typeGuid_4_0= RULE_STRING )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1241:3: lv_typeGuid_4_0= RULE_STRING
            {
            lv_typeGuid_4_0=(Token)input.LT(1);
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleOseeEnumType2170); 

            			createLeafNode(grammarAccess.getOseeEnumTypeAccess().getTypeGuidSTRINGTerminalRuleCall_4_0(), "typeGuid"); 
            		

            	        if (current==null) {
            	            current = factory.create(grammarAccess.getOseeEnumTypeRule().getType().getClassifier());
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

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1263:2: ( (lv_enumEntries_5_0= ruleOseeEnumEntry ) )*
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( (LA20_0==47) ) {
                    alt20=1;
                }


                switch (alt20) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1264:1: (lv_enumEntries_5_0= ruleOseeEnumEntry )
            	    {
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1264:1: (lv_enumEntries_5_0= ruleOseeEnumEntry )
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1265:3: lv_enumEntries_5_0= ruleOseeEnumEntry
            	    {
            	     
            	    	        currentNode=createCompositeNode(grammarAccess.getOseeEnumTypeAccess().getEnumEntriesOseeEnumEntryParserRuleCall_5_0(), currentNode); 
            	    	    
            	    pushFollow(FOLLOW_ruleOseeEnumEntry_in_ruleOseeEnumType2196);
            	    lv_enumEntries_5_0=ruleOseeEnumEntry();
            	    _fsp--;


            	    	        if (current==null) {
            	    	            current = factory.create(grammarAccess.getOseeEnumTypeRule().getType().getClassifier());
            	    	            associateNodeWithAstElement(currentNode.getParent(), current);
            	    	        }
            	    	        try {
            	    	       		add(
            	    	       			current, 
            	    	       			"enumEntries",
            	    	        		lv_enumEntries_5_0, 
            	    	        		"OseeEnumEntry", 
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

            match(input,20,FOLLOW_20_in_ruleOseeEnumType2206); 

                    createLeafNode(grammarAccess.getOseeEnumTypeAccess().getRightCurlyBracketKeyword_6(), null); 
                

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
    // $ANTLR end ruleOseeEnumType


    // $ANTLR start entryRuleOseeEnumEntry
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1298:1: entryRuleOseeEnumEntry returns [EObject current=null] : iv_ruleOseeEnumEntry= ruleOseeEnumEntry EOF ;
    public final EObject entryRuleOseeEnumEntry() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOseeEnumEntry = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1298:55: (iv_ruleOseeEnumEntry= ruleOseeEnumEntry EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1299:2: iv_ruleOseeEnumEntry= ruleOseeEnumEntry EOF
            {
             currentNode = createCompositeNode(grammarAccess.getOseeEnumEntryRule(), currentNode); 
            pushFollow(FOLLOW_ruleOseeEnumEntry_in_entryRuleOseeEnumEntry2239);
            iv_ruleOseeEnumEntry=ruleOseeEnumEntry();
            _fsp--;

             current =iv_ruleOseeEnumEntry; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleOseeEnumEntry2249); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end entryRuleOseeEnumEntry


    // $ANTLR start ruleOseeEnumEntry
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1306:1: ruleOseeEnumEntry returns [EObject current=null] : ( 'entry' ( (lv_name_1_0= ruleNAME_REFERENCE ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? ( 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? ) ;
    public final EObject ruleOseeEnumEntry() throws RecognitionException {
        EObject current = null;

        Token lv_ordinal_2_0=null;
        Token lv_entryGuid_4_0=null;
        AntlrDatatypeRuleToken lv_name_1_0 = null;


         @SuppressWarnings("unused") EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1311:6: ( ( 'entry' ( (lv_name_1_0= ruleNAME_REFERENCE ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? ( 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1312:1: ( 'entry' ( (lv_name_1_0= ruleNAME_REFERENCE ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? ( 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1312:1: ( 'entry' ( (lv_name_1_0= ruleNAME_REFERENCE ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? ( 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1312:2: 'entry' ( (lv_name_1_0= ruleNAME_REFERENCE ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? ( 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )?
            {
            match(input,47,FOLLOW_47_in_ruleOseeEnumEntry2283); 

                    createLeafNode(grammarAccess.getOseeEnumEntryAccess().getEntryKeyword_0(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1316:1: ( (lv_name_1_0= ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1317:1: (lv_name_1_0= ruleNAME_REFERENCE )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1317:1: (lv_name_1_0= ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1318:3: lv_name_1_0= ruleNAME_REFERENCE
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getOseeEnumEntryAccess().getNameNAME_REFERENCEParserRuleCall_1_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleOseeEnumEntry2304);
            lv_name_1_0=ruleNAME_REFERENCE();
            _fsp--;


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getOseeEnumEntryRule().getType().getClassifier());
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

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1340:2: ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==RULE_WHOLE_NUM_STR) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1341:1: (lv_ordinal_2_0= RULE_WHOLE_NUM_STR )
                    {
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1341:1: (lv_ordinal_2_0= RULE_WHOLE_NUM_STR )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1342:3: lv_ordinal_2_0= RULE_WHOLE_NUM_STR
                    {
                    lv_ordinal_2_0=(Token)input.LT(1);
                    match(input,RULE_WHOLE_NUM_STR,FOLLOW_RULE_WHOLE_NUM_STR_in_ruleOseeEnumEntry2321); 

                    			createLeafNode(grammarAccess.getOseeEnumEntryAccess().getOrdinalWHOLE_NUM_STRTerminalRuleCall_2_0(), "ordinal"); 
                    		

                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getOseeEnumEntryRule().getType().getClassifier());
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

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1364:3: ( 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )?
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0==48) ) {
                alt22=1;
            }
            switch (alt22) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1364:4: 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) )
                    {
                    match(input,48,FOLLOW_48_in_ruleOseeEnumEntry2337); 

                            createLeafNode(grammarAccess.getOseeEnumEntryAccess().getEntryGuidKeyword_3_0(), null); 
                        
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1368:1: ( (lv_entryGuid_4_0= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1369:1: (lv_entryGuid_4_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1369:1: (lv_entryGuid_4_0= RULE_STRING )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1370:3: lv_entryGuid_4_0= RULE_STRING
                    {
                    lv_entryGuid_4_0=(Token)input.LT(1);
                    match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleOseeEnumEntry2354); 

                    			createLeafNode(grammarAccess.getOseeEnumEntryAccess().getEntryGuidSTRINGTerminalRuleCall_3_1_0(), "entryGuid"); 
                    		

                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getOseeEnumEntryRule().getType().getClassifier());
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
    // $ANTLR end ruleOseeEnumEntry


    // $ANTLR start entryRuleOseeEnumOverride
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1399:1: entryRuleOseeEnumOverride returns [EObject current=null] : iv_ruleOseeEnumOverride= ruleOseeEnumOverride EOF ;
    public final EObject entryRuleOseeEnumOverride() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOseeEnumOverride = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1399:58: (iv_ruleOseeEnumOverride= ruleOseeEnumOverride EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1400:2: iv_ruleOseeEnumOverride= ruleOseeEnumOverride EOF
            {
             currentNode = createCompositeNode(grammarAccess.getOseeEnumOverrideRule(), currentNode); 
            pushFollow(FOLLOW_ruleOseeEnumOverride_in_entryRuleOseeEnumOverride2394);
            iv_ruleOseeEnumOverride=ruleOseeEnumOverride();
            _fsp--;

             current =iv_ruleOseeEnumOverride; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleOseeEnumOverride2404); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end entryRuleOseeEnumOverride


    // $ANTLR start ruleOseeEnumOverride
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1407:1: ruleOseeEnumOverride returns [EObject current=null] : ( 'overrides enum' ( ( ruleNAME_REFERENCE ) ) '{' ( (lv_inheritAll_3_0= 'inheritAll' ) )? ( (lv_overrideOptions_4_0= ruleOverrideOption ) )* '}' ) ;
    public final EObject ruleOseeEnumOverride() throws RecognitionException {
        EObject current = null;

        Token lv_inheritAll_3_0=null;
        EObject lv_overrideOptions_4_0 = null;


         @SuppressWarnings("unused") EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1412:6: ( ( 'overrides enum' ( ( ruleNAME_REFERENCE ) ) '{' ( (lv_inheritAll_3_0= 'inheritAll' ) )? ( (lv_overrideOptions_4_0= ruleOverrideOption ) )* '}' ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1413:1: ( 'overrides enum' ( ( ruleNAME_REFERENCE ) ) '{' ( (lv_inheritAll_3_0= 'inheritAll' ) )? ( (lv_overrideOptions_4_0= ruleOverrideOption ) )* '}' )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1413:1: ( 'overrides enum' ( ( ruleNAME_REFERENCE ) ) '{' ( (lv_inheritAll_3_0= 'inheritAll' ) )? ( (lv_overrideOptions_4_0= ruleOverrideOption ) )* '}' )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1413:2: 'overrides enum' ( ( ruleNAME_REFERENCE ) ) '{' ( (lv_inheritAll_3_0= 'inheritAll' ) )? ( (lv_overrideOptions_4_0= ruleOverrideOption ) )* '}'
            {
            match(input,49,FOLLOW_49_in_ruleOseeEnumOverride2438); 

                    createLeafNode(grammarAccess.getOseeEnumOverrideAccess().getOverridesEnumKeyword_0(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1417:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1418:1: ( ruleNAME_REFERENCE )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1418:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1419:3: ruleNAME_REFERENCE
            {

            			if (current==null) {
            	            current = factory.create(grammarAccess.getOseeEnumOverrideRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
                    
             
            	        currentNode=createCompositeNode(grammarAccess.getOseeEnumOverrideAccess().getOverridenEnumTypeOseeEnumTypeCrossReference_1_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleOseeEnumOverride2461);
            ruleNAME_REFERENCE();
            _fsp--;

             
            	        currentNode = currentNode.getParent();
            	    

            }


            }

            match(input,18,FOLLOW_18_in_ruleOseeEnumOverride2470); 

                    createLeafNode(grammarAccess.getOseeEnumOverrideAccess().getLeftCurlyBracketKeyword_2(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1437:1: ( (lv_inheritAll_3_0= 'inheritAll' ) )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==50) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1438:1: (lv_inheritAll_3_0= 'inheritAll' )
                    {
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1438:1: (lv_inheritAll_3_0= 'inheritAll' )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1439:3: lv_inheritAll_3_0= 'inheritAll'
                    {
                    lv_inheritAll_3_0=(Token)input.LT(1);
                    match(input,50,FOLLOW_50_in_ruleOseeEnumOverride2487); 

                            createLeafNode(grammarAccess.getOseeEnumOverrideAccess().getInheritAllInheritAllKeyword_3_0(), "inheritAll"); 
                        

                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getOseeEnumOverrideRule().getType().getClassifier());
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

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1458:3: ( (lv_overrideOptions_4_0= ruleOverrideOption ) )*
            loop24:
            do {
                int alt24=2;
                int LA24_0 = input.LA(1);

                if ( ((LA24_0>=51 && LA24_0<=52)) ) {
                    alt24=1;
                }


                switch (alt24) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1459:1: (lv_overrideOptions_4_0= ruleOverrideOption )
            	    {
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1459:1: (lv_overrideOptions_4_0= ruleOverrideOption )
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1460:3: lv_overrideOptions_4_0= ruleOverrideOption
            	    {
            	     
            	    	        currentNode=createCompositeNode(grammarAccess.getOseeEnumOverrideAccess().getOverrideOptionsOverrideOptionParserRuleCall_4_0(), currentNode); 
            	    	    
            	    pushFollow(FOLLOW_ruleOverrideOption_in_ruleOseeEnumOverride2522);
            	    lv_overrideOptions_4_0=ruleOverrideOption();
            	    _fsp--;


            	    	        if (current==null) {
            	    	            current = factory.create(grammarAccess.getOseeEnumOverrideRule().getType().getClassifier());
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

            match(input,20,FOLLOW_20_in_ruleOseeEnumOverride2532); 

                    createLeafNode(grammarAccess.getOseeEnumOverrideAccess().getRightCurlyBracketKeyword_5(), null); 
                

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
    // $ANTLR end ruleOseeEnumOverride


    // $ANTLR start entryRuleOverrideOption
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1493:1: entryRuleOverrideOption returns [EObject current=null] : iv_ruleOverrideOption= ruleOverrideOption EOF ;
    public final EObject entryRuleOverrideOption() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOverrideOption = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1493:56: (iv_ruleOverrideOption= ruleOverrideOption EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1494:2: iv_ruleOverrideOption= ruleOverrideOption EOF
            {
             currentNode = createCompositeNode(grammarAccess.getOverrideOptionRule(), currentNode); 
            pushFollow(FOLLOW_ruleOverrideOption_in_entryRuleOverrideOption2565);
            iv_ruleOverrideOption=ruleOverrideOption();
            _fsp--;

             current =iv_ruleOverrideOption; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleOverrideOption2575); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1501:1: ruleOverrideOption returns [EObject current=null] : (this_AddEnum_0= ruleAddEnum | this_RemoveEnum_1= ruleRemoveEnum ) ;
    public final EObject ruleOverrideOption() throws RecognitionException {
        EObject current = null;

        EObject this_AddEnum_0 = null;

        EObject this_RemoveEnum_1 = null;


         @SuppressWarnings("unused") EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1506:6: ( (this_AddEnum_0= ruleAddEnum | this_RemoveEnum_1= ruleRemoveEnum ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1507:1: (this_AddEnum_0= ruleAddEnum | this_RemoveEnum_1= ruleRemoveEnum )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1507:1: (this_AddEnum_0= ruleAddEnum | this_RemoveEnum_1= ruleRemoveEnum )
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
                    new NoViableAltException("1507:1: (this_AddEnum_0= ruleAddEnum | this_RemoveEnum_1= ruleRemoveEnum )", 25, 0, input);

                throw nvae;
            }
            switch (alt25) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1508:5: this_AddEnum_0= ruleAddEnum
                    {
                     
                            currentNode=createCompositeNode(grammarAccess.getOverrideOptionAccess().getAddEnumParserRuleCall_0(), currentNode); 
                        
                    pushFollow(FOLLOW_ruleAddEnum_in_ruleOverrideOption2622);
                    this_AddEnum_0=ruleAddEnum();
                    _fsp--;

                     
                            current = this_AddEnum_0; 
                            currentNode = currentNode.getParent();
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1518:5: this_RemoveEnum_1= ruleRemoveEnum
                    {
                     
                            currentNode=createCompositeNode(grammarAccess.getOverrideOptionAccess().getRemoveEnumParserRuleCall_1(), currentNode); 
                        
                    pushFollow(FOLLOW_ruleRemoveEnum_in_ruleOverrideOption2649);
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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1533:1: entryRuleAddEnum returns [EObject current=null] : iv_ruleAddEnum= ruleAddEnum EOF ;
    public final EObject entryRuleAddEnum() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAddEnum = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1533:49: (iv_ruleAddEnum= ruleAddEnum EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1534:2: iv_ruleAddEnum= ruleAddEnum EOF
            {
             currentNode = createCompositeNode(grammarAccess.getAddEnumRule(), currentNode); 
            pushFollow(FOLLOW_ruleAddEnum_in_entryRuleAddEnum2681);
            iv_ruleAddEnum=ruleAddEnum();
            _fsp--;

             current =iv_ruleAddEnum; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAddEnum2691); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1541:1: ruleAddEnum returns [EObject current=null] : ( 'add' ( (lv_enumEntry_1_0= ruleNAME_REFERENCE ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? ( 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? ) ;
    public final EObject ruleAddEnum() throws RecognitionException {
        EObject current = null;

        Token lv_ordinal_2_0=null;
        Token lv_entryGuid_4_0=null;
        AntlrDatatypeRuleToken lv_enumEntry_1_0 = null;


         @SuppressWarnings("unused") EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1546:6: ( ( 'add' ( (lv_enumEntry_1_0= ruleNAME_REFERENCE ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? ( 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1547:1: ( 'add' ( (lv_enumEntry_1_0= ruleNAME_REFERENCE ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? ( 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1547:1: ( 'add' ( (lv_enumEntry_1_0= ruleNAME_REFERENCE ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? ( 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )? )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1547:2: 'add' ( (lv_enumEntry_1_0= ruleNAME_REFERENCE ) ) ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )? ( 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )?
            {
            match(input,51,FOLLOW_51_in_ruleAddEnum2725); 

                    createLeafNode(grammarAccess.getAddEnumAccess().getAddKeyword_0(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1551:1: ( (lv_enumEntry_1_0= ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1552:1: (lv_enumEntry_1_0= ruleNAME_REFERENCE )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1552:1: (lv_enumEntry_1_0= ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1553:3: lv_enumEntry_1_0= ruleNAME_REFERENCE
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getAddEnumAccess().getEnumEntryNAME_REFERENCEParserRuleCall_1_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleAddEnum2746);
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

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1575:2: ( (lv_ordinal_2_0= RULE_WHOLE_NUM_STR ) )?
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==RULE_WHOLE_NUM_STR) ) {
                alt26=1;
            }
            switch (alt26) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1576:1: (lv_ordinal_2_0= RULE_WHOLE_NUM_STR )
                    {
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1576:1: (lv_ordinal_2_0= RULE_WHOLE_NUM_STR )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1577:3: lv_ordinal_2_0= RULE_WHOLE_NUM_STR
                    {
                    lv_ordinal_2_0=(Token)input.LT(1);
                    match(input,RULE_WHOLE_NUM_STR,FOLLOW_RULE_WHOLE_NUM_STR_in_ruleAddEnum2763); 

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

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1599:3: ( 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) ) )?
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==48) ) {
                alt27=1;
            }
            switch (alt27) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1599:4: 'entryGuid' ( (lv_entryGuid_4_0= RULE_STRING ) )
                    {
                    match(input,48,FOLLOW_48_in_ruleAddEnum2779); 

                            createLeafNode(grammarAccess.getAddEnumAccess().getEntryGuidKeyword_3_0(), null); 
                        
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1603:1: ( (lv_entryGuid_4_0= RULE_STRING ) )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1604:1: (lv_entryGuid_4_0= RULE_STRING )
                    {
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1604:1: (lv_entryGuid_4_0= RULE_STRING )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1605:3: lv_entryGuid_4_0= RULE_STRING
                    {
                    lv_entryGuid_4_0=(Token)input.LT(1);
                    match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAddEnum2796); 

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1634:1: entryRuleRemoveEnum returns [EObject current=null] : iv_ruleRemoveEnum= ruleRemoveEnum EOF ;
    public final EObject entryRuleRemoveEnum() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRemoveEnum = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1634:52: (iv_ruleRemoveEnum= ruleRemoveEnum EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1635:2: iv_ruleRemoveEnum= ruleRemoveEnum EOF
            {
             currentNode = createCompositeNode(grammarAccess.getRemoveEnumRule(), currentNode); 
            pushFollow(FOLLOW_ruleRemoveEnum_in_entryRuleRemoveEnum2836);
            iv_ruleRemoveEnum=ruleRemoveEnum();
            _fsp--;

             current =iv_ruleRemoveEnum; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleRemoveEnum2846); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1642:1: ruleRemoveEnum returns [EObject current=null] : ( 'remove' ( ( ruleNAME_REFERENCE ) ) ) ;
    public final EObject ruleRemoveEnum() throws RecognitionException {
        EObject current = null;

         @SuppressWarnings("unused") EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1647:6: ( ( 'remove' ( ( ruleNAME_REFERENCE ) ) ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1648:1: ( 'remove' ( ( ruleNAME_REFERENCE ) ) )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1648:1: ( 'remove' ( ( ruleNAME_REFERENCE ) ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1648:2: 'remove' ( ( ruleNAME_REFERENCE ) )
            {
            match(input,52,FOLLOW_52_in_ruleRemoveEnum2880); 

                    createLeafNode(grammarAccess.getRemoveEnumAccess().getRemoveKeyword_0(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1652:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1653:1: ( ruleNAME_REFERENCE )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1653:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1654:3: ruleNAME_REFERENCE
            {

            			if (current==null) {
            	            current = factory.create(grammarAccess.getRemoveEnumRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
                    
             
            	        currentNode=createCompositeNode(grammarAccess.getRemoveEnumAccess().getEnumEntryOseeEnumEntryCrossReference_1_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleRemoveEnum2903);
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


    // $ANTLR start entryRuleRelationType
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1675:1: entryRuleRelationType returns [EObject current=null] : iv_ruleRelationType= ruleRelationType EOF ;
    public final EObject entryRuleRelationType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRelationType = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1675:54: (iv_ruleRelationType= ruleRelationType EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1676:2: iv_ruleRelationType= ruleRelationType EOF
            {
             currentNode = createCompositeNode(grammarAccess.getRelationTypeRule(), currentNode); 
            pushFollow(FOLLOW_ruleRelationType_in_entryRuleRelationType2936);
            iv_ruleRelationType=ruleRelationType();
            _fsp--;

             current =iv_ruleRelationType; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleRelationType2946); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end entryRuleRelationType


    // $ANTLR start ruleRelationType
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1683:1: ruleRelationType returns [EObject current=null] : ( 'relationType' ( (lv_name_1_0= ruleNAME_REFERENCE ) ) '{' 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) 'sideAName' ( (lv_sideAName_6_0= RULE_STRING ) ) 'sideAArtifactType' ( ( ruleNAME_REFERENCE ) ) 'sideBName' ( (lv_sideBName_10_0= RULE_STRING ) ) 'sideBArtifactType' ( ( ruleNAME_REFERENCE ) ) 'defaultOrderType' ( (lv_defaultOrderType_14_0= ruleRelationOrderType ) ) 'multiplicity' ( (lv_multiplicity_16_0= ruleRelationMultiplicityEnum ) ) '}' ) ;
    public final EObject ruleRelationType() throws RecognitionException {
        EObject current = null;

        Token lv_typeGuid_4_0=null;
        Token lv_sideAName_6_0=null;
        Token lv_sideBName_10_0=null;
        AntlrDatatypeRuleToken lv_name_1_0 = null;

        AntlrDatatypeRuleToken lv_defaultOrderType_14_0 = null;

        Enumerator lv_multiplicity_16_0 = null;


         @SuppressWarnings("unused") EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1688:6: ( ( 'relationType' ( (lv_name_1_0= ruleNAME_REFERENCE ) ) '{' 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) 'sideAName' ( (lv_sideAName_6_0= RULE_STRING ) ) 'sideAArtifactType' ( ( ruleNAME_REFERENCE ) ) 'sideBName' ( (lv_sideBName_10_0= RULE_STRING ) ) 'sideBArtifactType' ( ( ruleNAME_REFERENCE ) ) 'defaultOrderType' ( (lv_defaultOrderType_14_0= ruleRelationOrderType ) ) 'multiplicity' ( (lv_multiplicity_16_0= ruleRelationMultiplicityEnum ) ) '}' ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1689:1: ( 'relationType' ( (lv_name_1_0= ruleNAME_REFERENCE ) ) '{' 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) 'sideAName' ( (lv_sideAName_6_0= RULE_STRING ) ) 'sideAArtifactType' ( ( ruleNAME_REFERENCE ) ) 'sideBName' ( (lv_sideBName_10_0= RULE_STRING ) ) 'sideBArtifactType' ( ( ruleNAME_REFERENCE ) ) 'defaultOrderType' ( (lv_defaultOrderType_14_0= ruleRelationOrderType ) ) 'multiplicity' ( (lv_multiplicity_16_0= ruleRelationMultiplicityEnum ) ) '}' )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1689:1: ( 'relationType' ( (lv_name_1_0= ruleNAME_REFERENCE ) ) '{' 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) 'sideAName' ( (lv_sideAName_6_0= RULE_STRING ) ) 'sideAArtifactType' ( ( ruleNAME_REFERENCE ) ) 'sideBName' ( (lv_sideBName_10_0= RULE_STRING ) ) 'sideBArtifactType' ( ( ruleNAME_REFERENCE ) ) 'defaultOrderType' ( (lv_defaultOrderType_14_0= ruleRelationOrderType ) ) 'multiplicity' ( (lv_multiplicity_16_0= ruleRelationMultiplicityEnum ) ) '}' )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1689:2: 'relationType' ( (lv_name_1_0= ruleNAME_REFERENCE ) ) '{' 'guid' ( (lv_typeGuid_4_0= RULE_STRING ) ) 'sideAName' ( (lv_sideAName_6_0= RULE_STRING ) ) 'sideAArtifactType' ( ( ruleNAME_REFERENCE ) ) 'sideBName' ( (lv_sideBName_10_0= RULE_STRING ) ) 'sideBArtifactType' ( ( ruleNAME_REFERENCE ) ) 'defaultOrderType' ( (lv_defaultOrderType_14_0= ruleRelationOrderType ) ) 'multiplicity' ( (lv_multiplicity_16_0= ruleRelationMultiplicityEnum ) ) '}'
            {
            match(input,53,FOLLOW_53_in_ruleRelationType2980); 

                    createLeafNode(grammarAccess.getRelationTypeAccess().getRelationTypeKeyword_0(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1693:1: ( (lv_name_1_0= ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1694:1: (lv_name_1_0= ruleNAME_REFERENCE )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1694:1: (lv_name_1_0= ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1695:3: lv_name_1_0= ruleNAME_REFERENCE
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getRelationTypeAccess().getNameNAME_REFERENCEParserRuleCall_1_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleRelationType3001);
            lv_name_1_0=ruleNAME_REFERENCE();
            _fsp--;


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getRelationTypeRule().getType().getClassifier());
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

            match(input,18,FOLLOW_18_in_ruleRelationType3010); 

                    createLeafNode(grammarAccess.getRelationTypeAccess().getLeftCurlyBracketKeyword_2(), null); 
                
            match(input,19,FOLLOW_19_in_ruleRelationType3019); 

                    createLeafNode(grammarAccess.getRelationTypeAccess().getGuidKeyword_3(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1725:1: ( (lv_typeGuid_4_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1726:1: (lv_typeGuid_4_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1726:1: (lv_typeGuid_4_0= RULE_STRING )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1727:3: lv_typeGuid_4_0= RULE_STRING
            {
            lv_typeGuid_4_0=(Token)input.LT(1);
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleRelationType3036); 

            			createLeafNode(grammarAccess.getRelationTypeAccess().getTypeGuidSTRINGTerminalRuleCall_4_0(), "typeGuid"); 
            		

            	        if (current==null) {
            	            current = factory.create(grammarAccess.getRelationTypeRule().getType().getClassifier());
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

            match(input,54,FOLLOW_54_in_ruleRelationType3050); 

                    createLeafNode(grammarAccess.getRelationTypeAccess().getSideANameKeyword_5(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1753:1: ( (lv_sideAName_6_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1754:1: (lv_sideAName_6_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1754:1: (lv_sideAName_6_0= RULE_STRING )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1755:3: lv_sideAName_6_0= RULE_STRING
            {
            lv_sideAName_6_0=(Token)input.LT(1);
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleRelationType3067); 

            			createLeafNode(grammarAccess.getRelationTypeAccess().getSideANameSTRINGTerminalRuleCall_6_0(), "sideAName"); 
            		

            	        if (current==null) {
            	            current = factory.create(grammarAccess.getRelationTypeRule().getType().getClassifier());
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

            match(input,55,FOLLOW_55_in_ruleRelationType3081); 

                    createLeafNode(grammarAccess.getRelationTypeAccess().getSideAArtifactTypeKeyword_7(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1781:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1782:1: ( ruleNAME_REFERENCE )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1782:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1783:3: ruleNAME_REFERENCE
            {

            			if (current==null) {
            	            current = factory.create(grammarAccess.getRelationTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
                    
             
            	        currentNode=createCompositeNode(grammarAccess.getRelationTypeAccess().getSideAArtifactTypeArtifactTypeCrossReference_8_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleRelationType3104);
            ruleNAME_REFERENCE();
            _fsp--;

             
            	        currentNode = currentNode.getParent();
            	    

            }


            }

            match(input,56,FOLLOW_56_in_ruleRelationType3113); 

                    createLeafNode(grammarAccess.getRelationTypeAccess().getSideBNameKeyword_9(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1801:1: ( (lv_sideBName_10_0= RULE_STRING ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1802:1: (lv_sideBName_10_0= RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1802:1: (lv_sideBName_10_0= RULE_STRING )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1803:3: lv_sideBName_10_0= RULE_STRING
            {
            lv_sideBName_10_0=(Token)input.LT(1);
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleRelationType3130); 

            			createLeafNode(grammarAccess.getRelationTypeAccess().getSideBNameSTRINGTerminalRuleCall_10_0(), "sideBName"); 
            		

            	        if (current==null) {
            	            current = factory.create(grammarAccess.getRelationTypeRule().getType().getClassifier());
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

            match(input,57,FOLLOW_57_in_ruleRelationType3144); 

                    createLeafNode(grammarAccess.getRelationTypeAccess().getSideBArtifactTypeKeyword_11(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1829:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1830:1: ( ruleNAME_REFERENCE )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1830:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1831:3: ruleNAME_REFERENCE
            {

            			if (current==null) {
            	            current = factory.create(grammarAccess.getRelationTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
                    
             
            	        currentNode=createCompositeNode(grammarAccess.getRelationTypeAccess().getSideBArtifactTypeArtifactTypeCrossReference_12_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleRelationType3167);
            ruleNAME_REFERENCE();
            _fsp--;

             
            	        currentNode = currentNode.getParent();
            	    

            }


            }

            match(input,58,FOLLOW_58_in_ruleRelationType3176); 

                    createLeafNode(grammarAccess.getRelationTypeAccess().getDefaultOrderTypeKeyword_13(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1849:1: ( (lv_defaultOrderType_14_0= ruleRelationOrderType ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1850:1: (lv_defaultOrderType_14_0= ruleRelationOrderType )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1850:1: (lv_defaultOrderType_14_0= ruleRelationOrderType )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1851:3: lv_defaultOrderType_14_0= ruleRelationOrderType
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getRelationTypeAccess().getDefaultOrderTypeRelationOrderTypeParserRuleCall_14_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleRelationOrderType_in_ruleRelationType3197);
            lv_defaultOrderType_14_0=ruleRelationOrderType();
            _fsp--;


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getRelationTypeRule().getType().getClassifier());
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

            match(input,59,FOLLOW_59_in_ruleRelationType3206); 

                    createLeafNode(grammarAccess.getRelationTypeAccess().getMultiplicityKeyword_15(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1877:1: ( (lv_multiplicity_16_0= ruleRelationMultiplicityEnum ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1878:1: (lv_multiplicity_16_0= ruleRelationMultiplicityEnum )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1878:1: (lv_multiplicity_16_0= ruleRelationMultiplicityEnum )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1879:3: lv_multiplicity_16_0= ruleRelationMultiplicityEnum
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getRelationTypeAccess().getMultiplicityRelationMultiplicityEnumEnumRuleCall_16_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleRelationMultiplicityEnum_in_ruleRelationType3227);
            lv_multiplicity_16_0=ruleRelationMultiplicityEnum();
            _fsp--;


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getRelationTypeRule().getType().getClassifier());
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

            match(input,20,FOLLOW_20_in_ruleRelationType3236); 

                    createLeafNode(grammarAccess.getRelationTypeAccess().getRightCurlyBracketKeyword_17(), null); 
                

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
    // $ANTLR end ruleRelationType


    // $ANTLR start entryRuleRelationOrderType
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1912:1: entryRuleRelationOrderType returns [String current=null] : iv_ruleRelationOrderType= ruleRelationOrderType EOF ;
    public final String entryRuleRelationOrderType() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleRelationOrderType = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1912:58: (iv_ruleRelationOrderType= ruleRelationOrderType EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1913:2: iv_ruleRelationOrderType= ruleRelationOrderType EOF
            {
             currentNode = createCompositeNode(grammarAccess.getRelationOrderTypeRule(), currentNode); 
            pushFollow(FOLLOW_ruleRelationOrderType_in_entryRuleRelationOrderType3270);
            iv_ruleRelationOrderType=ruleRelationOrderType();
            _fsp--;

             current =iv_ruleRelationOrderType.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleRelationOrderType3281); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1920:1: ruleRelationOrderType returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'Lexicographical_Ascending' | kw= 'Lexicographical_Descending' | kw= 'Unordered' | this_ID_3= RULE_ID ) ;
    public final AntlrDatatypeRuleToken ruleRelationOrderType() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        Token this_ID_3=null;

         setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1925:6: ( (kw= 'Lexicographical_Ascending' | kw= 'Lexicographical_Descending' | kw= 'Unordered' | this_ID_3= RULE_ID ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1926:1: (kw= 'Lexicographical_Ascending' | kw= 'Lexicographical_Descending' | kw= 'Unordered' | this_ID_3= RULE_ID )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1926:1: (kw= 'Lexicographical_Ascending' | kw= 'Lexicographical_Descending' | kw= 'Unordered' | this_ID_3= RULE_ID )
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
                    new NoViableAltException("1926:1: (kw= 'Lexicographical_Ascending' | kw= 'Lexicographical_Descending' | kw= 'Unordered' | this_ID_3= RULE_ID )", 28, 0, input);

                throw nvae;
            }

            switch (alt28) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1927:2: kw= 'Lexicographical_Ascending'
                    {
                    kw=(Token)input.LT(1);
                    match(input,60,FOLLOW_60_in_ruleRelationOrderType3319); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getRelationOrderTypeAccess().getLexicographical_AscendingKeyword_0(), null); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1934:2: kw= 'Lexicographical_Descending'
                    {
                    kw=(Token)input.LT(1);
                    match(input,61,FOLLOW_61_in_ruleRelationOrderType3338); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getRelationOrderTypeAccess().getLexicographical_DescendingKeyword_1(), null); 
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1941:2: kw= 'Unordered'
                    {
                    kw=(Token)input.LT(1);
                    match(input,62,FOLLOW_62_in_ruleRelationOrderType3357); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getRelationOrderTypeAccess().getUnorderedKeyword_2(), null); 
                        

                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1947:10: this_ID_3= RULE_ID
                    {
                    this_ID_3=(Token)input.LT(1);
                    match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleRelationOrderType3378); 

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1962:1: ruleRelationMultiplicityEnum returns [Enumerator current=null] : ( ( 'ONE_TO_ONE' ) | ( 'ONE_TO_MANY' ) | ( 'MANY_TO_ONE' ) | ( 'MANY_TO_MANY' ) ) ;
    public final Enumerator ruleRelationMultiplicityEnum() throws RecognitionException {
        Enumerator current = null;

         setCurrentLookahead(); resetLookahead(); 
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1966:6: ( ( ( 'ONE_TO_ONE' ) | ( 'ONE_TO_MANY' ) | ( 'MANY_TO_ONE' ) | ( 'MANY_TO_MANY' ) ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1967:1: ( ( 'ONE_TO_ONE' ) | ( 'ONE_TO_MANY' ) | ( 'MANY_TO_ONE' ) | ( 'MANY_TO_MANY' ) )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1967:1: ( ( 'ONE_TO_ONE' ) | ( 'ONE_TO_MANY' ) | ( 'MANY_TO_ONE' ) | ( 'MANY_TO_MANY' ) )
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
                    new NoViableAltException("1967:1: ( ( 'ONE_TO_ONE' ) | ( 'ONE_TO_MANY' ) | ( 'MANY_TO_ONE' ) | ( 'MANY_TO_MANY' ) )", 29, 0, input);

                throw nvae;
            }

            switch (alt29) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1967:2: ( 'ONE_TO_ONE' )
                    {
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1967:2: ( 'ONE_TO_ONE' )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1967:4: 'ONE_TO_ONE'
                    {
                    match(input,63,FOLLOW_63_in_ruleRelationMultiplicityEnum3435); 

                            current = grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_ONEEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            createLeafNode(grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_ONEEnumLiteralDeclaration_0(), null); 
                        

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1973:6: ( 'ONE_TO_MANY' )
                    {
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1973:6: ( 'ONE_TO_MANY' )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1973:8: 'ONE_TO_MANY'
                    {
                    match(input,64,FOLLOW_64_in_ruleRelationMultiplicityEnum3450); 

                            current = grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_MANYEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                            createLeafNode(grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_MANYEnumLiteralDeclaration_1(), null); 
                        

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1979:6: ( 'MANY_TO_ONE' )
                    {
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1979:6: ( 'MANY_TO_ONE' )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1979:8: 'MANY_TO_ONE'
                    {
                    match(input,65,FOLLOW_65_in_ruleRelationMultiplicityEnum3465); 

                            current = grammarAccess.getRelationMultiplicityEnumAccess().getMANY_TO_ONEEnumLiteralDeclaration_2().getEnumLiteral().getInstance();
                            createLeafNode(grammarAccess.getRelationMultiplicityEnumAccess().getMANY_TO_ONEEnumLiteralDeclaration_2(), null); 
                        

                    }


                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1985:6: ( 'MANY_TO_MANY' )
                    {
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1985:6: ( 'MANY_TO_MANY' )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1985:8: 'MANY_TO_MANY'
                    {
                    match(input,66,FOLLOW_66_in_ruleRelationMultiplicityEnum3480); 

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


 

    public static final BitSet FOLLOW_ruleOseeTypeModel_in_entryRuleOseeTypeModel73 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleOseeTypeModel83 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleImport_in_ruleOseeTypeModel129 = new BitSet(new long[]{0x002240000080D002L});
    public static final BitSet FOLLOW_ruleArtifactType_in_ruleOseeTypeModel152 = new BitSet(new long[]{0x002240000080C002L});
    public static final BitSet FOLLOW_ruleRelationType_in_ruleOseeTypeModel179 = new BitSet(new long[]{0x002240000080C002L});
    public static final BitSet FOLLOW_ruleAttributeType_in_ruleOseeTypeModel206 = new BitSet(new long[]{0x002240000080C002L});
    public static final BitSet FOLLOW_ruleOseeEnumType_in_ruleOseeTypeModel233 = new BitSet(new long[]{0x002240000080C002L});
    public static final BitSet FOLLOW_ruleOseeEnumOverride_in_ruleOseeTypeModel260 = new BitSet(new long[]{0x002240000080C002L});
    public static final BitSet FOLLOW_ruleImport_in_entryRuleImport295 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleImport305 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_12_in_ruleImport339 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleImport356 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_entryRuleNAME_REFERENCE395 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleNAME_REFERENCE406 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleNAME_REFERENCE445 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_entryRuleQUALIFIED_NAME488 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleQUALIFIED_NAME499 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleQUALIFIED_NAME539 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_13_in_ruleQUALIFIED_NAME558 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleQUALIFIED_NAME573 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_ruleOseeType_in_entryRuleOseeType620 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleOseeType630 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleArtifactType_in_ruleOseeType677 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelationType_in_ruleOseeType704 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeType_in_ruleOseeType731 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOseeEnumType_in_ruleOseeType758 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleArtifactType_in_entryRuleArtifactType790 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleArtifactType800 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_14_in_ruleArtifactType842 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_ruleArtifactType865 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleArtifactType886 = new BitSet(new long[]{0x0000000000050000L});
    public static final BitSet FOLLOW_16_in_ruleArtifactType896 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleArtifactType919 = new BitSet(new long[]{0x0000000000060000L});
    public static final BitSet FOLLOW_17_in_ruleArtifactType929 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleArtifactType952 = new BitSet(new long[]{0x0000000000060000L});
    public static final BitSet FOLLOW_18_in_ruleArtifactType965 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_ruleArtifactType974 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleArtifactType991 = new BitSet(new long[]{0x0000000000300000L});
    public static final BitSet FOLLOW_ruleAttributeTypeRef_in_ruleArtifactType1017 = new BitSet(new long[]{0x0000000000300000L});
    public static final BitSet FOLLOW_20_in_ruleArtifactType1027 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeTypeRef_in_entryRuleAttributeTypeRef1060 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttributeTypeRef1070 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_21_in_ruleAttributeTypeRef1104 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleAttributeTypeRef1127 = new BitSet(new long[]{0x0000000000400002L});
    public static final BitSet FOLLOW_22_in_ruleAttributeTypeRef1137 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAttributeTypeRef1154 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeType_in_entryRuleAttributeType1194 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttributeType1204 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_23_in_ruleAttributeType1238 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleAttributeType1259 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_16_in_ruleAttributeType1269 = new BitSet(new long[]{0x00003FE000000020L});
    public static final BitSet FOLLOW_ruleAttributeBaseType_in_ruleAttributeType1290 = new BitSet(new long[]{0x0000000001040000L});
    public static final BitSet FOLLOW_24_in_ruleAttributeType1301 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleAttributeType1324 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_18_in_ruleAttributeType1335 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_ruleAttributeType1344 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAttributeType1361 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_ruleAttributeType1375 = new BitSet(new long[]{0x000000000C000020L});
    public static final BitSet FOLLOW_26_in_ruleAttributeType1394 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_27_in_ruleAttributeType1422 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_ruleAttributeType1454 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_28_in_ruleAttributeType1466 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RULE_WHOLE_NUM_STR_in_ruleAttributeType1483 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_ruleAttributeType1497 = new BitSet(new long[]{0x0000000040000040L});
    public static final BitSet FOLLOW_RULE_WHOLE_NUM_STR_in_ruleAttributeType1516 = new BitSet(new long[]{0x0000001E80100000L});
    public static final BitSet FOLLOW_30_in_ruleAttributeType1536 = new BitSet(new long[]{0x0000001E80100000L});
    public static final BitSet FOLLOW_31_in_ruleAttributeType1562 = new BitSet(new long[]{0x0000000100000020L});
    public static final BitSet FOLLOW_32_in_ruleAttributeType1581 = new BitSet(new long[]{0x0000001E00100000L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_ruleAttributeType1613 = new BitSet(new long[]{0x0000001E00100000L});
    public static final BitSet FOLLOW_33_in_ruleAttributeType1628 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleAttributeType1651 = new BitSet(new long[]{0x0000001C00100000L});
    public static final BitSet FOLLOW_34_in_ruleAttributeType1663 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAttributeType1680 = new BitSet(new long[]{0x0000001800100000L});
    public static final BitSet FOLLOW_35_in_ruleAttributeType1697 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAttributeType1714 = new BitSet(new long[]{0x0000001000100000L});
    public static final BitSet FOLLOW_36_in_ruleAttributeType1731 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAttributeType1748 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_ruleAttributeType1764 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeBaseType_in_entryRuleAttributeBaseType1798 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttributeBaseType1809 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_ruleAttributeBaseType1847 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_38_in_ruleAttributeBaseType1866 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_39_in_ruleAttributeBaseType1885 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_ruleAttributeBaseType1904 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_ruleAttributeBaseType1923 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_ruleAttributeBaseType1942 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_43_in_ruleAttributeBaseType1961 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_ruleAttributeBaseType1980 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_45_in_ruleAttributeBaseType1999 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_ruleAttributeBaseType2027 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOseeEnumType_in_entryRuleOseeEnumType2070 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleOseeEnumType2080 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_46_in_ruleOseeEnumType2114 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleOseeEnumType2135 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_18_in_ruleOseeEnumType2144 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_ruleOseeEnumType2153 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleOseeEnumType2170 = new BitSet(new long[]{0x0000800000100000L});
    public static final BitSet FOLLOW_ruleOseeEnumEntry_in_ruleOseeEnumType2196 = new BitSet(new long[]{0x0000800000100000L});
    public static final BitSet FOLLOW_20_in_ruleOseeEnumType2206 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOseeEnumEntry_in_entryRuleOseeEnumEntry2239 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleOseeEnumEntry2249 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_ruleOseeEnumEntry2283 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleOseeEnumEntry2304 = new BitSet(new long[]{0x0001000000000042L});
    public static final BitSet FOLLOW_RULE_WHOLE_NUM_STR_in_ruleOseeEnumEntry2321 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_48_in_ruleOseeEnumEntry2337 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleOseeEnumEntry2354 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOseeEnumOverride_in_entryRuleOseeEnumOverride2394 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleOseeEnumOverride2404 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_49_in_ruleOseeEnumOverride2438 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleOseeEnumOverride2461 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_18_in_ruleOseeEnumOverride2470 = new BitSet(new long[]{0x001C000000100000L});
    public static final BitSet FOLLOW_50_in_ruleOseeEnumOverride2487 = new BitSet(new long[]{0x0018000000100000L});
    public static final BitSet FOLLOW_ruleOverrideOption_in_ruleOseeEnumOverride2522 = new BitSet(new long[]{0x0018000000100000L});
    public static final BitSet FOLLOW_20_in_ruleOseeEnumOverride2532 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOverrideOption_in_entryRuleOverrideOption2565 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleOverrideOption2575 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAddEnum_in_ruleOverrideOption2622 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRemoveEnum_in_ruleOverrideOption2649 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAddEnum_in_entryRuleAddEnum2681 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAddEnum2691 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_51_in_ruleAddEnum2725 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleAddEnum2746 = new BitSet(new long[]{0x0001000000000042L});
    public static final BitSet FOLLOW_RULE_WHOLE_NUM_STR_in_ruleAddEnum2763 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_48_in_ruleAddEnum2779 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAddEnum2796 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRemoveEnum_in_entryRuleRemoveEnum2836 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRemoveEnum2846 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_52_in_ruleRemoveEnum2880 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleRemoveEnum2903 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelationType_in_entryRuleRelationType2936 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRelationType2946 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_53_in_ruleRelationType2980 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleRelationType3001 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_18_in_ruleRelationType3010 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_ruleRelationType3019 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleRelationType3036 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_54_in_ruleRelationType3050 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleRelationType3067 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_55_in_ruleRelationType3081 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleRelationType3104 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_56_in_ruleRelationType3113 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleRelationType3130 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_57_in_ruleRelationType3144 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleRelationType3167 = new BitSet(new long[]{0x0400000000000000L});
    public static final BitSet FOLLOW_58_in_ruleRelationType3176 = new BitSet(new long[]{0x7000000000000020L});
    public static final BitSet FOLLOW_ruleRelationOrderType_in_ruleRelationType3197 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_59_in_ruleRelationType3206 = new BitSet(new long[]{0x8000000000000000L,0x0000000000000007L});
    public static final BitSet FOLLOW_ruleRelationMultiplicityEnum_in_ruleRelationType3227 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_ruleRelationType3236 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelationOrderType_in_entryRuleRelationOrderType3270 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRelationOrderType3281 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_60_in_ruleRelationOrderType3319 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_61_in_ruleRelationOrderType3338 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_62_in_ruleRelationOrderType3357 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleRelationOrderType3378 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_63_in_ruleRelationMultiplicityEnum3435 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_64_in_ruleRelationMultiplicityEnum3450 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_ruleRelationMultiplicityEnum3465 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_66_in_ruleRelationMultiplicityEnum3480 = new BitSet(new long[]{0x0000000000000002L});

}